package de.funke.app.lock

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings

/**
 * Gemeinsamer Zustand zwischen der App und dem Sperr-Dienst ([LockService]).
 *
 * Beide laufen im selben Prozess, deshalb reicht ein einfaches Objekt. Zusätzlich wird alles
 * gespeichert, damit der Dienst nach einem Neustart des Prozesses weiß, ob gerade gelesen wird.
 */
object SessionLock {
    private const val K_ACTIVE = "lock_active"
    private const val K_END = "lock_end"
    private const val K_ALLOWED = "lock_allowed"
    private const val K_MODE = "lock_mode"
    private const val K_HANDOFF = "lock_handoff"

    /**
     * Apps, die in Wahrheit in einer anderen App laufen. Ist die erste erlaubt, sind in der zweiten
     * NUR deren Bildschirme erlaubt, nicht die ganze App. Erkannt wird das am Namen des Bildschirms.
     *
     * Gemini läuft in der Google-App; intern heißt es dort "Robin". Die Google-Suche ist ein anderer
     * Bildschirm (SearchActivity) und bleibt gesperrt.
     */
    private val PARTNER_SCREENS: Map<String, Map<String, List<String>>> = mapOf(
        "com.google.android.apps.bard" to mapOf(
            "com.google.android.googlequicksearchbox" to listOf(".robin.", "bard", "gemini"),
        ),
    )

    /** So lange nach dem Öffnen einer erlaubten App gilt ein Wechsel in eine andere App als Weiterleitung. */
    private const val HANDOFF_WINDOW_MS = 4000L

    @Volatile var active: Boolean = false
        private set

    /** Ende der Session als Uhrzeit (System.currentTimeMillis). */
    @Volatile var endAt: Long = 0L
        private set

    @Volatile var allowed: Set<String> = emptySet()
        private set

    @Volatile var modeName: String = ""
        private set

    /**
     * Einzelne Bildschirme ("paket/Klasse"), an die eine erlaubte App in dieser Session direkt
     * weitergeleitet hat. Erlaubt ist nur genau dieser Bildschirm, nicht die ganze andere App.
     */
    @Volatile var handoff: Set<String> = emptySet()
        private set

    /** Zuletzt aus Lumina geöffnete erlaubte App und wann (für Weiterleitungen). */
    @Volatile private var launchedPkg: String? = null
    @Volatile private var launchedAt: Long = 0L
    private var prefs: SharedPreferences? = null

    /** Ist die ganze App erlaubt? */
    fun isAllowed(pkg: String): Boolean = pkg in allowed

    private fun partnerKeywords(pkg: String): List<String>? =
        allowed.firstNotNullOfOrNull { PARTNER_SCREENS[it]?.get(pkg) }

    /** Gibt es für diese App eine Regel auf Bildschirm-Ebene (nur bestimmte Bildschirme erlaubt)? */
    fun hasScreenRule(pkg: String): Boolean =
        partnerKeywords(pkg) != null || handoff.any { it.startsWith("$pkg/") }

    /** Ist genau dieser Bildschirm erlaubt? */
    fun screenAllowed(pkg: String, cls: String): Boolean {
        if ("$pkg/$cls" in handoff) return true
        val c = cls.lowercase()
        return partnerKeywords(pkg)?.any { c.contains(it) } == true
    }

    /** Lumina öffnet gleich eine erlaubte App. */
    fun noteLaunch(pkg: String) {
        launchedPkg = pkg
        launchedAt = SystemClock.uptimeMillis()
    }

    /** Wurde gerade eben eine erlaubte App aus Lumina geöffnet? */
    fun recentlyLaunched(): Boolean =
        launchedPkg != null && SystemClock.uptimeMillis() - launchedAt <= HANDOFF_WINDOW_MS

    /**
     * Ein nicht erlaubter Bildschirm ist vorne. Kam er direkt aus der gerade geöffneten erlaubten App
     * (kurz danach, ohne Umweg über Startbildschirm oder Benachrichtigungen), gilt GENAU DIESER
     * Bildschirm für den Rest der Session als erlaubt.
     * [previous] = die App, die davor vorne war. [blocked] = darf nie freigeschaltet werden.
     */
    fun acceptHandoff(pkg: String, cls: String, previous: String?, ownPackage: String, blocked: Set<String>): Boolean {
        val launched = launchedPkg ?: return false
        if (!recentlyLaunched()) return false
        if (pkg == launched || pkg in blocked) return false
        if (previous != launched && previous != ownPackage) return false
        handoff = handoff + "$pkg/$cls"
        prefs?.edit()?.putString(K_HANDOFF, handoff.joinToString(","))?.apply()
        return true
    }

    /** Läuft gerade eine Session, deren Zeit noch nicht um ist? */
    fun isRunning(now: Long = System.currentTimeMillis()): Boolean = active && now < endAt

    fun load(prefs: SharedPreferences) {
        active = prefs.getBoolean(K_ACTIVE, false)
        endAt = prefs.getLong(K_END, 0L)
        allowed = prefs.getString(K_ALLOWED, "")!!.split(',').filter { it.isNotBlank() }.toSet()
        modeName = prefs.getString(K_MODE, "") ?: ""
        handoff = prefs.getString(K_HANDOFF, "")!!.split(',').filter { it.isNotBlank() }.toSet()
        this.prefs = prefs
    }

    fun start(prefs: SharedPreferences?, endAt: Long, allowed: Collection<String>, modeName: String) {
        this.endAt = endAt
        this.allowed = allowed.toSet()
        this.modeName = modeName
        this.handoff = emptySet()
        this.launchedPkg = null
        this.active = true
        if (prefs != null) this.prefs = prefs
        prefs?.edit()
            ?.remove(K_HANDOFF)
            ?.putBoolean(K_ACTIVE, true)
            ?.putLong(K_END, endAt)
            ?.putString(K_ALLOWED, allowed.joinToString(","))
            ?.putString(K_MODE, modeName)
            ?.apply()
        LockService.instance?.onSessionChanged()
    }

    fun stop(prefs: SharedPreferences?) {
        active = false
        handoff = emptySet()
        launchedPkg = null
        prefs?.edit()?.putBoolean(K_ACTIVE, false)?.remove(K_HANDOFF)?.apply()
        LockService.instance?.onSessionChanged()
    }

    /** Ist die Bedienungshilfe "Lumina Sperre" eingeschaltet? */
    fun isServiceEnabled(context: Context): Boolean {
        if (LockService.instance != null) return true
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false
        val me = ComponentName(context, LockService::class.java)
        return enabled.split(':').any { ComponentName.unflattenFromString(it) == me }
    }

    /**
     * Öffnet die Bedienungshilfen-Einstellungen so nah wie möglich an "Lumina Sperre".
     *
     * Direkt auf die Detailseite einer Bedienungshilfe springen darf unter Android nur das System selbst
     * (ACCESSIBILITY_DETAILS_SETTINGS verlangt die Systemberechtigung OPEN_ACCESSIBILITY_DETAILS_SETTINGS).
     * Deshalb, der Reihe nach:
     *  1. Detailseite versuchen – auf den wenigen Geräten, die sie freigeben, landet man direkt beim Schalter.
     *  2. Sonst die Bedienungshilfen-Liste mit "Hervorheben" von Lumina (AOSP/Pixel scrollt hin und markiert).
     *  3. Dazu ein kurzer Hinweis über den Einstellungen, wo es weitergeht (Samsung: "Installierte Apps").
     * @return true, wenn die Detailseite selbst aufging (dann braucht es keinen Weg-Hinweis).
     */
    fun openAccessibilitySettings(context: Context): Boolean {
        val component = ComponentName(context, LockService::class.java)
        val flat = component.flattenToString()
        val direct = Intent("android.settings.ACCESSIBILITY_DETAILS_SETTINGS")
            .putExtra(Intent.EXTRA_COMPONENT_NAME, flat)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (runCatching { context.startActivity(direct) }.isSuccess) return true
        val args = android.os.Bundle().apply { putString(":settings:fragment_args_key", flat) }
        val list = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .putExtra(":settings:fragment_args_key", flat)
            .putExtra(":settings:show_fragment_args", args)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val opened = runCatching { context.startActivity(list) }
            .recoverCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            .isSuccess
        if (opened) showPathHint(context)
        return false
    }

    /** Der Weg zum Schalter, in den Worten der Einstellungen (Samsung: "Installierte Apps"). */
    fun accessibilityPath(context: Context): String {
        val name = context.getString(de.funke.app.R.string.lock_service_label)
        val samsung = Build.MANUFACTURER.equals("samsung", ignoreCase = true)
        return when {
            samsung -> de.funke.app.ui.tr("„Installierte Apps“ → „$name“ → einschalten", "“Installed apps” → “$name” → turn on")
            else -> de.funke.app.ui.tr("„$name“ antippen → einschalten", "Tap “$name” → turn on")
        }
    }

    private fun showPathHint(context: Context) {
        val text = de.funke.app.ui.tr("Jetzt: ", "Next: ") + accessibilityPath(context)
        // Kurz verzögert, damit der Hinweis über den Einstellungen erscheint und nicht über Lumina
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            runCatching { android.widget.Toast.makeText(context.applicationContext, text, android.widget.Toast.LENGTH_LONG).show() }
        }, 450L)
    }

    /** App-Info von Lumina (dort steckt bei Samsung "Eingeschränkte Einstellungen zulassen"). */
    fun openAppInfo(context: Context) {
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
