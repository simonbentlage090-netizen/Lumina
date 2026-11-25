package de.funke.app.lock

import de.funke.app.ui.tr

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.telecom.TelecomManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import de.funke.app.MainActivity

/**
 * Die eigentliche Sperre. Läuft als Bedienungshilfe und bekommt mit, welche App gerade vorne ist.
 *
 * Während einer Session gilt:
 * - Lumina selbst und die erlaubten Apps des Modus dürfen vorne sein.
 * - Wechselt man in irgendetwas anderes (Startbildschirm, App-Übersicht, andere Apps,
 *   Einstellungen), holt der Dienst sofort Lumina zurück.
 * - In einer erlaubten App schwebt oben eine kleine Pille mit der Restzeit. Antippen führt zu Lumina.
 * - Telefon, Notruf, Tastatur und Systemdialoge bleiben immer erlaubt.
 * - Ist die Zeit um, wird Lumina nach vorne geholt, damit man "Geschafft" sieht.
 */
class LockService : AccessibilityService() {

    companion object {
        /** Die laufende Instanz, solange die Bedienungshilfe eingeschaltet ist. */
        @Volatile var instance: LockService? = null
            private set

        /** Immer erlaubt, damit nichts Wichtiges blockiert wird. */
        private val ALWAYS_ALLOWED = setOf(
            "android",
            "com.android.systemui",
            "com.android.phone",
            "com.android.server.telecom",
            "com.android.incallui",
            "com.samsung.android.incallui",
            "com.android.emergency",
            "com.samsung.android.emergency",
            "com.google.android.apps.safetyhub",
            "com.android.permissioncontroller",
            "com.google.android.permissioncontroller",
            "com.samsung.android.biometrics.app.setting",
            "com.samsung.android.app.aodservice",
        )
    }

    private val handler = Handler(Looper.getMainLooper())
    private var pill: TextView? = null
    private var lastBounce = 0L
    private var endHandled = false
    private var inAllowedApp = false
    /** Die App, die zuletzt vorne war (ohne Systemoberfläche und Tastatur). */
    private var lastForeground: String? = null
    /** Für Apps mit Bildschirm-Regel: War der zuletzt gesehene Bildschirm erlaubt? */
    private val screenOk = HashMap<String, Boolean>()
    /** App, die nach kurzer Wartezeit zurückgeholt wird, falls sie dann noch auf einem gesperrten Bildschirm ist. */
    private var pendingBlock: String? = null
    /** Merkt sich, ob ein Klassenname ein echter Bildschirm (Activity) ist. */
    private val activityCache = HashMap<String, Boolean>()

    private val ticker = object : Runnable {
        override fun run() {
            onTick()
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        SessionLock.load(getSharedPreferences("funke", Context.MODE_PRIVATE))
        de.funke.app.gate.Gate.load(getSharedPreferences("funke", Context.MODE_PRIVATE))
        de.funke.app.ui.Lang.load(getSharedPreferences("funke", Context.MODE_PRIVATE))
        handler.post(ticker)
        // Entsperren mitbekommen (für die Erinnerung beim Entsperren). Geht nur dynamisch, solange der Dienst läuft.
        runCatching {
            val filter = android.content.IntentFilter(Intent.ACTION_USER_PRESENT)
            if (android.os.Build.VERSION.SDK_INT >= 33) registerReceiver(unlockReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            else @Suppress("UnspecifiedRegisterReceiverFlag") registerReceiver(unlockReceiver, filter)
            unlockRegistered = true
        }
    }

    override fun onDestroy() {
        instance = null
        if (unlockRegistered) runCatching { unregisterReceiver(unlockReceiver) }
        unlockRegistered = false
        handler.removeCallbacksAndMessages(null)
        runCatching { usageWorker.shutdownNow() }
        hidePill()
        super.onDestroy()
    }

    override fun onInterrupt() = Unit

    /** Wird von [SessionLock] aufgerufen, wenn eine Session startet oder endet. */
    fun onSessionChanged() {
        handler.post {
            endHandled = false
            screenOk.clear()
            pendingBlock = null
            if (!SessionLock.isRunning()) {
                inAllowedApp = false
                hidePill()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        val cls = event.className?.toString()

        if (!SessionLock.isRunning()) {
            inAllowedApp = false
            hidePill()
            handleGate(pkg)
            return
        }

        when {
            pkg == packageName -> {
                // Unsere eigene Pille meldet sich auch. Nur die echte App zählt.
                if (cls != null && cls != MainActivity::class.java.name) return
                pendingBlock = null
                inAllowedApp = false
                lastForeground = pkg
                hidePill()
            }
            isAlwaysAllowed(pkg) -> {
                // Benachrichtigungen/Schnelleinstellungen: Was danach geöffnet wird, ist keine Weiterleitung.
                if (pkg == "com.android.systemui") lastForeground = pkg
            }
            SessionLock.isAllowed(pkg) -> allow(pkg)
            // Nur bestimmte Bildschirme erlaubt (z. B. in der Google-App nur Gemini, nicht die Suche)
            SessionLock.hasScreenRule(pkg) -> {
                if (isActivity(pkg, cls)) {
                    screenOk[pkg] = SessionLock.screenAllowed(pkg, cls!!) ||
                        (!looksLikeSearch(cls) &&
                            SessionLock.acceptHandoff(pkg, cls, lastForeground, packageName, neverHandoff()))
                }
                when {
                    // Erlaubter Bildschirm, oder Dialog/Menü darin
                    screenOk[pkg] == true -> allow(pkg)
                    // Gerade erst geöffnet: Manche Apps springen kurz über einen Zwischen-Bildschirm.
                    // Noch nicht zurückholen, sondern kurz abwarten (siehe onTick).
                    SessionLock.recentlyLaunched() -> {
                        lastForeground = pkg
                        pendingBlock = pkg
                    }
                    else -> block(pkg)
                }
            }
            // Eine erlaubte App leitet direkt an einen anderen Bildschirm weiter (z. B. Login im Browser)
            SessionLock.recentlyLaunched() && !isActivity(pkg, cls) -> Unit
            isActivity(pkg, cls) &&
                SessionLock.acceptHandoff(pkg, cls!!, lastForeground, packageName, neverHandoff()) -> {
                screenOk[pkg] = true
                allow(pkg)
            }
            else -> block(pkg)
        }
    }

    private fun allow(pkg: String) {
        if (pendingBlock != pkg || screenOk[pkg] == true) pendingBlock = null
        inAllowedApp = true
        lastForeground = pkg
        showPill()
    }

    /** Suchbildschirme werden nie automatisch freigeschaltet (Gemini ja, Google-Suche nein). */
    private fun looksLikeSearch(cls: String): Boolean =
        cls.substringAfterLast('.').lowercase().contains("search")

    private fun block(pkg: String) {
        pendingBlock = null
        lastForeground = pkg
        bringBack()
    }

    /** Ist [cls] ein echter Bildschirm (Activity) von [pkg]? Dialoge und Ansichten zählen nicht. */
    private fun isActivity(pkg: String, cls: String?): Boolean {
        if (cls.isNullOrEmpty()) return false
        return activityCache.getOrPut("$pkg/$cls") {
            runCatching { packageManager.getActivityInfo(ComponentName(pkg, cls), 0) }.isSuccess
        }
    }

    /** Startbildschirm(e) und Einstellungen dürfen nie als Weiterleitung durchrutschen. */
    private fun neverHandoff(): Set<String> {
        val home = runCatching {
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0
            ).map { it.activityInfo.packageName }
        }.getOrDefault(emptyList())
        return home.toSet() + setOf("com.android.settings", "com.samsung.android.app.cocktailbarservice")
    }

    private fun isAlwaysAllowed(pkg: String): Boolean {
        if (pkg in ALWAYS_ALLOWED) return true
        val dialer = runCatching {
            (getSystemService(Context.TELECOM_SERVICE) as TelecomManager).defaultDialerPackage
        }.getOrNull()
        if (pkg == dialer) return true
        val keyboards = runCatching {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .enabledInputMethodList.map { it.packageName }
        }.getOrDefault(emptyList())
        return pkg in keyboards
    }

    /** Holt Lumina nach vorne. Kurz gedrosselt, damit es nicht flackert. */
    private fun bringBack() {
        val now = SystemClock.uptimeMillis()
        if (now - lastBounce < 400L) return
        lastBounce = now
        inAllowedApp = false
        hidePill()
        openLumina()
    }

    private fun openLumina() {
        runCatching {
            startActivity(
                Intent(this, MainActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            )
        }
    }

    private fun onTick() {
        if (SessionLock.active && !SessionLock.isRunning()) {
            // Zeit ist um: einmal Lumina zeigen, damit man das Ende mitbekommt.
            if (!endHandled) {
                endHandled = true
                inAllowedApp = false
                hidePill()
                openLumina()
            }
            return
        }
        if (!SessionLock.isRunning()) {
            hidePill()
            checkInTick()
            usageTick()
            return
        }
        // Wartezeit vorbei und immer noch auf einem gesperrten Bildschirm? Dann zurückholen.
        pendingBlock?.let { pkg ->
            if (!SessionLock.recentlyLaunched()) {
                pendingBlock = null
                if (lastForeground == pkg && screenOk[pkg] != true) block(pkg)
            }
        }
        if (inAllowedApp) updatePill()
    }

    // ----- Denkpause (seit 12.0: Fragezeiten wie bei JEE PYQ Lock) -----

    /** Die App, die außerhalb von Sessions gerade vorne ist (für Fragen mitten in einer App). */
    private var gateForeground: String? = null
    private var lastGatePkg: String? = null
    private var lastGateAt = 0L

    /** Öffnet sich eine geschützte App und ist eine Frage fällig, kommt zuerst die Denkpause. */
    private fun handleGate(pkg: String) {
        if (pkg == packageName) {
            gateForeground = null
            return
        }
        // Statusleiste, Tastatur, Telefon: ändern nichts daran, welche App vorne ist
        if (isAlwaysAllowed(pkg)) return
        gateForeground = pkg
        if (!de.funke.app.gate.Gate.needsGate(pkg)) return
        openGateDebounced(pkg, checkIn = false)
    }

    private fun openGateDebounced(pkg: String, checkIn: Boolean) {
        val now = SystemClock.uptimeMillis()
        // Die Frage ist schon offen: nicht noch einmal öffnen (Apps melden im Hintergrund weiter Fenster)
        if (de.funke.app.ui.DeepLink.gate != null) return
        if (pkg == lastGatePkg && now - lastGateAt < 1500L) return
        lastGatePkg = pkg
        lastGateAt = now
        openGate(pkg, checkIn)
    }

    /** Einmal pro Sekunde: Ist man gerade in einer geschützten App, wenn eine Fragezeit beginnt? */
    private fun checkInTick() {
        val g = de.funke.app.gate.Gate
        if (!g.enabled) return
        val fg = gateForeground ?: return
        if (fg !in g.apps || !screenOnAndUnlocked()) return
        if (!g.isDue()) return
        openGateDebounced(fg, checkIn = true)
    }

    private fun screenOnAndUnlocked(): Boolean {
        val power = getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
        if (power?.isInteractive != true) return false
        val keyguard = getSystemService(Context.KEYGUARD_SERVICE) as? android.app.KeyguardManager
        return keyguard?.isKeyguardLocked != true
    }

    private fun openGate(pkg: String, checkIn: Boolean) {
        runCatching {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra(de.funke.app.ui.DeepLink.EXTRA_GATE_PKG, pkg)
                    .putExtra(de.funke.app.ui.DeepLink.EXTRA_GATE_CHECKIN, checkIn)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
            )
        }
    }

    // ----- Erinnerung beim Entsperren (seit 12.0) -----

    private var unlockRegistered = false

    private val unlockReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_USER_PRESENT) return
            // Kurz warten, bis der Startbildschirm oder die letzte App wirklich vorne ist
            handler.postDelayed({ maybeNudge() }, 600L)
        }
    }

    private fun maybeNudge() {
        if (SessionLock.isRunning() || SessionLock.active) return
        // Seit 13.4: Erinnerungen mit "Bildschirm", die kamen, während das Handy weglag, warten hier
        de.funke.app.notify.Reminders.showPendingOnUnlock(this)
    }

    // ----- Nutzungslimit (seit 13.3): etwa minütlich im Hintergrund nachzählen -----

    private val usageWorker = java.util.concurrent.Executors.newSingleThreadExecutor()
    private var lastUsageCheck = 0L

    private fun usageTick() {
        val now = SystemClock.uptimeMillis()
        if (now - lastUsageCheck < 60_000L) return
        lastUsageCheck = now
        if (!screenOnAndUnlocked()) return
        runCatching { usageWorker.execute { runCatching { de.funke.app.notify.Reminders.checkUsage(this) } } }
    }

    // ----- Pille mit Restzeit -----

    private fun showPill() {
        if (pill != null) {
            updatePill()
            return
        }
        val d = resources.displayMetrics.density
        val view = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 13f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            setPadding((14 * d).toInt(), (8 * d).toInt(), (16 * d).toInt(), (8 * d).toInt())
            background = GradientDrawable().apply {
                cornerRadius = 100f * d
                setColor(de.funke.app.ui.theme.BrandColors.OverlayPillArgb)
            }
            elevation = 8f * d
            setOnClickListener { openLumina() }
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
        val statusId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBar = if (statusId > 0) resources.getDimensionPixelSize(statusId) else (24 * d).toInt()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = statusBar + (6 * d).toInt()
        }
        val ok = runCatching {
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).addView(view, params)
        }.isSuccess
        if (ok) {
            pill = view
            view.alpha = 0f
            view.translationY = -12f * d
            view.animate().alpha(1f).translationY(0f).setDuration(260L).start()
            updatePill()
        }
    }

    private fun updatePill() {
        val view = pill ?: return
        val left = ((SessionLock.endAt - System.currentTimeMillis()).coerceAtLeast(0L) + 999L) / 1000L
        val h = left / 3600
        val m = (left % 3600) / 60
        val s = left % 60
        val time = if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
        val text = SpannableString("●  $time  ·  " + tr("Zurück zu Lumina", "Back to Lumina"))
        text.setSpan(ForegroundColorSpan(0xFF8E8AFF.toInt()), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.text = text
        view.contentDescription = tr("Noch $time. Antippen, um zu Lumina zurückzukehren.", "$time left. Tap to return to Lumina.")
    }

    private fun hidePill() {
        val view = pill ?: return
        pill = null
        runCatching { (getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view) }
    }
}
