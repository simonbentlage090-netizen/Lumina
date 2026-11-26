package de.funke.app.notify

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import de.funke.app.R
import de.funke.app.lock.LockService
import de.funke.app.progress.Streaks
import de.funke.app.ui.Lang
import de.funke.app.ui.daysText
import de.funke.app.ui.tr
import de.funke.app.widget.Widgets
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/*
 * Erinnerungen von Lumi (seit 13.3 neu gebaut).
 *
 * Warum vorher oft nichts kam:
 *  - setAndAllowWhileIdle ist ungenau; Samsung verschiebt solche Wecker gern um Stunden oder lässt sie fallen.
 *    Jetzt: exakte Wecker (SCHEDULE_EXACT_ALARM, selbst erlaubt), sonst Ausweichweg.
 *  - Der Kanal hatte nur "Standard"-Wichtigkeit: kein Pop-up, oft kein Ton. Jetzt eigener Kanal mit HIGH.
 *  - Lesen/Streak schweigen, wenn heute schon alles erledigt ist. Das ist gewollt und steht jetzt in der Oberfläche.
 *
 * Zwei Wege, wie eine Erinnerung kommt ([Delivery], seit 13.4):
 *  - Mitteilung: normale Benachrichtigung mit Pop-up.
 *  - Bildschirm: Lumi-Bildschirm in der App. Benutzt man das Handy gerade, holt die Bedienungshilfe Lumina
 *    sofort nach vorn (egal welche App offen ist); sonst wartet der Hinweis bis zum nächsten Entsperren.
 *    "Später" und "Nicht jetzt" schieben Lumina wieder nach hinten, man ist dort, wo man war.
 *    Ohne Bedienungshilfe kommt stattdessen eine Mitteilung.
 *    (Bis 13.3 lief das über eine eigene zweite Activity mit eigenem App-Zustand – das hat bei einem Test
 *    die App unbrauchbar gemacht. Jetzt gibt es nur noch die eine Activity und den einen Zustand.)
 *
 * Alles bleibt auf dem Handy. Nach Neustart, Update oder Zeitumstellung plant [BootReceiver] neu.
 */
object Reminders {
    /**
     * Kanal für alle Erinnerungen (seit 14.0 "v3"). Wichtigkeit HIGH = Pop-up oben auf dem Bildschirm (heads-up).
     * Die Wichtigkeit eines Kanals lässt sich nach dem Anlegen nicht mehr erhöhen. Falls ein älterer Kanal
     * irgendwann leiser angelegt oder im System leiser gestellt wurde, kam nur noch ein Eintrag in der Leiste.
     * Deshalb ein frischer Kanal; die alten werden gelöscht.
     */
    const val CHANNEL = "lumi_reminders_v3"
    private val OLD_CHANNELS = listOf("lumi_reminders", "lumi_reminders_v2")

    internal const val ACTION = "de.funke.app.action.REMIND"
    internal const val ACTION_SNOOZE = "de.funke.app.action.REMIND_SNOOZE"
    internal const val EXTRA_ID = "id"
    internal const val EXTRA_SNOOZED = "snoozed"
    /** Probe "in einer Minute": kommt auf dem echten Weg, aber ohne die Bedingungen (Tagesziel offen usw.). */
    internal const val EXTRA_TEST = "test"
    internal const val EXTRA_SHOWN = "shown"
    internal const val EXTRA_DELIVERY = "delivery"

    /** Interner Termin: alle 30 Minuten das Nutzungslimit prüfen (falls die Bedienungshilfe aus ist). */
    const val USAGE_ID = "usage"

    private const val K_SESSION_END = "session_end"
    private const val K_CODES = "alarm_codes"
    private const val K_PENDING = "reminder_pending"
    private const val K_USAGE_DAY = "usage_fired_day"
    private const val K_USAGE_AT = "usage_fired_at"
    private const val K_USAGE_MIN = "usage_fired_min"
    private const val K_USAGE_CHECK = "usage_last_check"

    /** "In 15 Minuten" */
    const val SNOOZE_MIN = 15
    private const val REPEAT_MS = 30 * 60_000L
    /** So lange wartet ein Hinweis auf das nächste Entsperren. */
    private const val PENDING_MAX_MS = 6 * 60 * 60_000L

    private fun prefs(ctx: Context): SharedPreferences = ctx.getSharedPreferences(Streaks.PREFS, Context.MODE_PRIVATE)

    // ------------------------------------------------------------------------------------------
    // Planen
    // ------------------------------------------------------------------------------------------

    /** Termine (neu) setzen. Beim Öffnen der App, nach jeder Erinnerung, nach Änderungen und nach dem Neustart. */
    fun schedule(context: Context) {
        val ctx = context.applicationContext
        runCatching {
            val p = prefs(ctx)
            val am = ctx.getSystemService(AlarmManager::class.java) ?: return
            // Alles Alte weg (auch gelöschte eigene Erinnerungen und Codes von vor 13.3)
            val old = p.safeString(K_CODES)?.split(',')?.mapNotNull { it.toIntOrNull() }.orEmpty() + listOf(1, 2, 4)
            for (code in old.toSet()) am.cancel(alarmPending(ctx, code, null, false))
            val codes = ArrayList<Int>()
            val now = LocalDateTime.now()
            for (r in ReminderStore.all(p)) {
                if (!r.on || r.days and Days.ALL == 0) continue
                val at = nextOccurrence(r, now) ?: continue
                val code = codeFor(r.id)
                setExact(am, at.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), alarmPending(ctx, code, r.id, false))
                codes += code
            }
            // Nutzungslimit: zur nächsten halben Stunde nachsehen (ungenau reicht, die Bedienungshilfe prüft sowieso minütlich)
            val u = ReminderStore.usage(p)
            if (u.on && u.hasTargets) {
                val code = codeFor(USAGE_ID)
                val next = now.withSecond(0).withNano(0).let { t -> if (t.minute < 30) t.withMinute(30) else t.plusHours(1).withMinute(0) }
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), alarmPending(ctx, code, USAGE_ID, false))
                codes += code
            }
            p.edit().putString(K_CODES, codes.joinToString(",")).apply()
        }
    }

    /** Der nächste Zeitpunkt nach [now], an einem erlaubten Wochentag. */
    fun nextOccurrence(r: Reminder, now: LocalDateTime): LocalDateTime? {
        for (d in 0..7) {
            val date = now.toLocalDate().plusDays(d.toLong())
            if (!Days.on(r.days, date)) continue
            val at = date.atTime(r.hour, r.minute)
            if (at.isAfter(now)) return at
        }
        return null
    }

    private fun codeFor(id: String): Int = when (id) {
        "read" -> 1
        "streak" -> 2
        USAGE_ID -> 4
        "bed" -> 5
        else -> 1000 + (id.hashCode() and 0x7FFF)
    }

    private fun setExact(am: AlarmManager, ms: Long, pi: PendingIntent) {
        val exact = Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()
        val ok = exact && runCatching { am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ms, pi) }.isSuccess
        if (!ok) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ms, pi)
    }

    private fun alarmPending(ctx: Context, code: Int, id: String?, snoozed: Boolean): PendingIntent = PendingIntent.getBroadcast(
        ctx, code + if (snoozed) 50_000 else 0,
        Intent(ctx, ReminderReceiver::class.java).setAction(ACTION).apply {
            if (id != null) putExtra(EXTRA_ID, id)
            putExtra(EXTRA_SNOOZED, snoozed)
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    /**
     * Probe auf dem echten Weg (seit 14.0): Die Erinnerung kommt in einer Minute über den Wecker – so wie später
     * im Alltag (Pop-up, sofort im Vollbild oder beim nächsten Entsperren). Man kann das Handy dafür weglegen.
     * Die fertige Erinnerung reist im Intent mit, deshalb geht das auch für eine noch nicht gespeicherte.
     * @return Zeitpunkt, zu dem sie kommt (0 = ging nicht).
     */
    fun testLater(context: Context, shown: Shown, delivery: Delivery): Long {
        val ctx = context.applicationContext
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return 0L
        val at = System.currentTimeMillis() + 60_000L
        val intent = Intent(ctx, ReminderReceiver::class.java).setAction(ACTION)
            .putExtra(EXTRA_ID, shown.id)
            .putExtra(EXTRA_TEST, true)
            .putExtra(EXTRA_SHOWN, shown.copy(at = at, test = true).toJson().toString())
            .putExtra(EXTRA_DELIVERY, delivery.id)
        val pi = PendingIntent.getBroadcast(ctx, 90_000 + (shown.id.hashCode() and 0x0FFF), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return if (runCatching { setExact(am, at, pi) }.isSuccess) at else 0L
    }

    /** Probe aus [testLater] ist fällig: genau so zeigen, wie sie im Alltag käme. */
    internal fun fireTest(context: Context, shownJson: String?, deliveryId: String?) {
        val shown = shownJson?.let { runCatching { Shown.fromJson(JSONObject(it)) }.getOrNull() } ?: return
        Lang.load(prefs(context))
        deliver(context, shown, Delivery.byId(deliveryId))
    }

    /** "In 15 Minuten": dieselbe Erinnerung kommt genau einmal wieder (auf demselben Weg). */
    fun snooze(context: Context, id: String) {
        val ctx = context.applicationContext
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        removePending(ctx, id)
        NotificationManagerCompat.from(ctx).cancel(notificationId(id))
        setExact(am, System.currentTimeMillis() + SNOOZE_MIN * 60_000L, alarmPending(ctx, codeFor(id), id, snoozed = true))
    }

    // ------------------------------------------------------------------------------------------
    // Auslösen
    // ------------------------------------------------------------------------------------------

    // ----- Protokoll (seit 14.0): was zuletzt mit einer Erinnerung passiert ist -----

    /** Ausgang einer Erinnerung, damit man im Editor sieht, ob und wie sie kam. */
    enum class Outcome(val id: String) {
        Popup("popup"), Screen("screen"), Waiting("waiting"), Done("done"), Busy("busy"), Blocked("blocked");

        companion object {
            fun byId(id: String?): Outcome? = entries.firstOrNull { it.id == id }
        }
    }

    private const val K_LOG = "reminder_log"

    private fun log(ctx: Context, id: String, outcome: Outcome) {
        runCatching {
            val p = prefs(ctx)
            val o = runCatching { JSONObject(p.safeString(K_LOG) ?: "{}") }.getOrDefault(JSONObject())
            o.put(id, JSONObject().put("at", System.currentTimeMillis()).put("o", outcome.id))
            p.edit().putString(K_LOG, o.toString()).apply()
        }
    }

    /** Letzter Ausgang dieser Erinnerung (Zeitpunkt, Ausgang) oder null. */
    fun lastOutcome(context: Context, id: String): Pair<Long, Outcome>? {
        val raw = prefs(context).safeString(K_LOG) ?: return null
        return runCatching {
            val o = JSONObject(raw).optJSONObject(id)
            val out = Outcome.byId(o?.optString("o", ""))
            if (o == null || out == null) null else o.getLong("at") to out
        }.getOrNull()
    }

    /** Was gezeigt wird: fertig ausformuliert, für Mitteilung und Lumi-Bildschirm gleich. */
    data class Shown(
        val id: String,
        val kind: ReminderKind,
        val title: String,
        val text: String,
        val image: Int,
        val at: Long = System.currentTimeMillis(),
        /** Probe: kommt auch, wenn die Bedingung inzwischen erfüllt ist. */
        val test: Boolean = false,
    ) {
        fun toJson(): JSONObject = JSONObject().put("id", id).put("kind", kind.id).put("title", title).put("text", text)
            .put("image", image).put("at", at).put("test", test)

        companion object {
            fun fromJson(o: JSONObject): Shown? = runCatching {
                Shown(
                    o.getString("id"), ReminderKind.byId(o.getString("kind")), o.getString("title"), o.getString("text"),
                    o.getInt("image"), o.getLong("at"), o.optBoolean("test", false),
                )
            }.getOrNull()
        }
    }

    /** Gerade in einer Fokuszeit oder im Buch (der Reader frischt [K_BOOK_UNTIL] alle 30 Sekunden auf)? */
    private fun reading(p: SharedPreferences): Boolean {
        val now = System.currentTimeMillis()
        return runCatching { p.getLong(K_SESSION_END, 0L) > now || p.getLong(K_BOOK_UNTIL, 0L) > now }.getOrDefault(false)
    }

    /** Bis wann gerade im Buch gelesen wird (seit 13.4). */
    const val K_BOOK_UNTIL = "book_reading_until"

    /** Vom Wecker: nachsehen, ob die Erinnerung gerade passt, und sie dann auf ihrem Weg zeigen. */
    internal fun fire(context: Context, id: String, snoozed: Boolean) {
        val p = prefs(context)
        Lang.load(p)
        if (id == USAGE_ID) {
            checkUsage(context, force = snoozed)
            return
        }
        val r = ReminderStore.byId(p, id) ?: return
        if (!r.on) return
        if (!snoozed && !Days.on(r.days, LocalDate.now())) return
        val busy = reading(p)
        // Während einer Fokuszeit sind Tagesziel, Streak und Schlafenszeit überflüssig. Eigene kommen leise als Mitteilung.
        if (busy && !r.isCustom) { log(context, r.id, Outcome.Busy); return }
        val shown = content(context, r, test = false)
        if (shown == null) { log(context, r.id, Outcome.Done); return }
        deliver(context, shown, if (busy) Delivery.Notify else r.delivery)
    }

    /**
     * Text und Bild für eine Erinnerung. Null = gerade nicht nötig (z. B. heute schon gelesen).
     * [test] = immer etwas zeigen, damit man sieht, wie es aussieht.
     */
    fun content(context: Context, r: Reminder, test: Boolean): Shown? {
        val p = prefs(context)
        val s = Streaks.snapshot(p)
        val n = s.streak
        val nd = daysText(n)
        val ndD = de.funke.app.ui.daysTextDative(n)
        val m = s.todayMinutes
        val g = s.goal
        val left = (g - m).coerceAtLeast(1)
        val day = LocalDate.now().toEpochDay().toInt()
        fun pick(vararg o: Triple<Int, String, String>): Shown {
            val t = o[Math.floorMod(day, o.size)]
            return Shown(r.id, r.kind, t.second, t.third, t.first)
        }
        return when (r.kind) {
            ReminderKind.Read -> when {
                s.atRisk && n > 0 -> pick(
                    Triple(R.drawable.lumi_w_scared, tr("Streak in Gefahr", "Streak at risk"),
                        tr("Dein Streak von $ndD endet heute. Eine Fokuszeit reicht.", "Your streak of $nd ends today. One focus session is enough.")),
                    Triple(R.drawable.lumi_w_plead, tr("Lumi macht sich Sorgen", "Lumi is worried"),
                        tr("Heute fehlt noch deine Fokuszeit – sonst ist der Streak von $ndD weg.", "Today's focus time is still missing – or your streak of $nd is gone.")),
                    Triple(R.drawable.lumi_w_scared, tr("Nicht vergessen", "Don't forget"),
                        tr("Tag ${n + 1} wartet auf dich. Komm, wir fangen an.", "Day ${n + 1} is waiting. Come on, let's begin.")),
                )
                s.goalMetToday && !test -> null
                m > 0 && !s.goalMetToday -> pick(
                    Triple(R.drawable.lumi_w_wink, tr("Fast geschafft", "Almost there"),
                        tr("Noch $left Minuten bis zu deinem Tagesziel.", "$left more minutes to your daily goal.")),
                    Triple(R.drawable.lumi_w_go, tr("Nur noch ein bisschen", "Just a little more"),
                        tr("$m von $g Minuten – den Rest schaffst du locker.", "$m of $g minutes – you've got the rest.")),
                )
                else -> pick(
                    Triple(R.drawable.lumi_w_plead, tr("Lumi wartet auf dich", "Lumi is waiting for you"),
                        tr("Heute noch keine Fokuszeit. Wie wär's mit $g Minuten?", "No focus time yet today. How about $g minutes?")),
                    Triple(R.drawable.lumi_w_read, tr("Zeit für Fokus", "Time to focus"),
                        tr("Dein Tagesziel: $g Minuten. Ich leuchte schon mal vor.", "Your daily goal: $g minutes. I'll light the way.")),
                    Triple(R.drawable.lumi_w_sad, tr("Ein ruhiger Moment?", "A quiet moment?"),
                        tr("Ein paar Minuten Fokus und Lumi strahlt wieder.", "A few minutes of focus and Lumi shines again.")),
                )
            }
            ReminderKind.Streak -> when {
                !s.atRisk && !test -> null
                s.freezes > 0 -> pick(
                    Triple(R.drawable.lumi_w_scared, tr("Noch keine Fokuszeit heute", "No focus time yet today"),
                        tr("Sonst springt heute dein Streak-Eis ein. Schaffst du es selbst?", "Otherwise your streak freeze kicks in today. Can you do it yourself?")),
                )
                else -> pick(
                    Triple(R.drawable.lumi_w_cry, tr("Letzte Chance", "Last chance"),
                        tr("Bis Mitternacht – sonst ist dein Streak von $ndD weg.", "Until midnight – or your streak of $nd is gone.")),
                    Triple(R.drawable.lumi_w_cry, tr("Bitte nicht aufgeben …", "Please don't give up …"),
                        tr("Lumi weint gleich. Eine Fokuszeit rettet deinen Streak von $ndD.", "Lumi is about to cry. One focus session saves your streak of $nd.")),
                )
            }
            ReminderKind.Bed -> pick(
                Triple(R.drawable.lumi_w_sleep, tr("Bald ist Schlafenszeit", "Bedtime soon"),
                    tr("Ein paar Seiten oder ein Hörbuch sind jetzt besser als Scrollen.", "A few pages or an audiobook beat scrolling right now.")),
                Triple(R.drawable.lumi_w_read, tr("Zeit, runterzufahren", "Time to wind down"),
                    tr("Leg das Handy weg. Dein Kopf dankt es dir morgen.", "Put the phone away. Your mind will thank you tomorrow.")),
            )
            ReminderKind.Custom -> Shown(
                r.id, r.kind,
                r.title.ifBlank { tr("Erinnerung", "Reminder") },
                r.text,
                R.drawable.lumi_w_hello,
            )
            ReminderKind.Usage -> null
        }
    }

    /** Eine fertige Erinnerung auf dem gewählten Weg zeigen. */
    fun deliver(context: Context, shown: Shown, delivery: Delivery) {
        val ctx = context.applicationContext
        when (delivery) {
            Delivery.Notify -> postLogged(ctx, shown)
            Delivery.Screen -> when {
                // Ohne Bedienungshilfe darf Lumina sich nicht nach vorn holen: dann als Mitteilung
                LockService.instance == null -> postLogged(ctx, shown)
                inUse(ctx) -> if (showNow(ctx, shown)) log(ctx, shown.id, Outcome.Screen) else postLogged(ctx, shown)
                else -> { addPending(ctx, shown); log(ctx, shown.id, Outcome.Waiting) }
            }
        }
    }

    private fun postLogged(ctx: Context, shown: Shown) {
        if (!allowed(ctx)) { log(ctx, shown.id, Outcome.Blocked); return }
        post(ctx, shown)
        log(ctx, shown.id, Outcome.Popup)
    }

    /** Handy an und entsperrt (jemand schaut gerade drauf)? */
    fun inUse(ctx: Context): Boolean {
        val power = ctx.getSystemService(PowerManager::class.java)
        if (power?.isInteractive != true) return false
        val keyguard = ctx.getSystemService(android.app.KeyguardManager::class.java)
        return keyguard?.isKeyguardLocked != true
    }

    /** Intent, der Lumina mit dieser Erinnerung öffnet (sie liegt dann über allem in der App). */
    fun screenIntent(ctx: Context, shown: Shown): Intent = de.funke.app.ui.DeepLink.sign(
        ctx,
        Intent(ctx, de.funke.app.MainActivity::class.java)
            .putExtra(de.funke.app.ui.DeepLink.EXTRA_REMINDER, shown.toJson().toString())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP),
    )

    /** Lumi-Bildschirm sofort öffnen (die Bedienungshilfe darf Lumina auch aus dem Hintergrund nach vorn holen). */
    internal fun showNow(ctx: Context, shown: Shown): Boolean {
        val starter: Context = LockService.instance ?: return false
        return runCatching { starter.startActivity(screenIntent(starter, shown)) }.isSuccess
    }

    // ----- Warteschlange "beim nächsten Entsperren" -----

    private fun pendingList(p: SharedPreferences): MutableList<Shown> = runCatching {
        val a = JSONArray(p.safeString(K_PENDING) ?: "[]")
        MutableList(a.length()) { i -> Shown.fromJson(a.getJSONObject(i)) }.filterNotNull().toMutableList()
    }.getOrDefault(mutableListOf())

    private fun savePending(p: SharedPreferences, list: List<Shown>) {
        val a = JSONArray()
        list.forEach { a.put(it.toJson()) }
        p.edit().putString(K_PENDING, a.toString()).apply()
    }

    private fun addPending(ctx: Context, shown: Shown) {
        val p = prefs(ctx)
        val list = pendingList(p).filterNot { it.id == shown.id }.toMutableList()
        list += shown
        savePending(p, list)
    }

    fun removePending(ctx: Context, id: String) {
        val p = prefs(ctx)
        val list = pendingList(p)
        if (list.removeAll { it.id == id }) savePending(p, list)
    }

    /**
     * Von der Bedienungshilfe beim Entsperren: den ältesten wartenden Hinweis zeigen, falls er noch passt.
     * @return true, wenn ein Bildschirm geöffnet wurde.
     */
    fun showPendingOnUnlock(ctx: Context): Boolean {
        val p = prefs(ctx)
        if (reading(p)) return false
        val now = System.currentTimeMillis()
        val list = pendingList(p)
        val fresh = list.filter { now - it.at < PENDING_MAX_MS && (it.test || stillRelevant(ctx, it)) }.toMutableList()
        if (fresh.size != list.size) savePending(p, fresh)
        val next = fresh.firstOrNull() ?: return false
        Lang.load(p)
        val ok = showNow(ctx, next)
        if (ok) log(ctx, next.id, Outcome.Screen)
        return ok
    }

    /** Hat sich die Lage geändert (z. B. inzwischen gelesen)? Dann den Hinweis nicht mehr zeigen. */
    private fun stillRelevant(ctx: Context, s: Shown): Boolean {
        val snap = Streaks.snapshot(prefs(ctx))
        return when (s.kind) {
            ReminderKind.Read -> !snap.goalMetToday
            ReminderKind.Streak -> snap.atRisk
            else -> true
        }
    }

    // ----- Nutzungslimit -----

    /** Heutige Minuten in den Gruppen/Apps des Limits (null = kein Zugriff auf Nutzungsdaten). */
    fun usageMinutesToday(context: Context, u: UsageLimit): Int? {
        if (!de.funke.app.usage.ScreenTime.hasAccess(context)) return null
        val p = prefs(context)
        val config = de.funke.app.usage.GroupConfig.fromJson(p.safeString("app_groups"))
        val day = de.funke.app.usage.ScreenTime.days(context, 1).lastOrNull() ?: return 0
        val pm = context.packageManager
        var ms = 0L
        for ((pkg, t) in day.apps) {
            if (pkg == context.packageName) continue
            if (pkg in u.apps) { ms += t; continue }
            if (u.groups.isEmpty()) continue
            val group = config.assign[pkg] ?: run {
                val label = runCatching { pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString() }.getOrDefault(pkg)
                de.funke.app.usage.AppGroups.classify(pkg, label, de.funke.app.usage.ScreenTime.category(context, pkg))
            }
            if (group in u.groups) ms += t
        }
        return (ms / 60_000L).toInt()
    }

    /** Kurzer Name für die gewählten Ziele ("Videos & Streaming und 2 Apps"). */
    fun usageTargetLabel(context: Context, u: UsageLimit): String {
        val config = de.funke.app.usage.GroupConfig.fromJson(prefs(context).safeString("app_groups"))
        val names = u.groups.map { config.group(it).name }
        val apps = u.apps.size
        val appText = when (apps) {
            0 -> null
            1 -> runCatching {
                val pm = context.packageManager
                pm.getApplicationLabel(pm.getApplicationInfo(u.apps.first(), 0)).toString()
            }.getOrDefault(tr("1 App", "1 app"))
            else -> tr("$apps Apps", "$apps apps")
        }
        val parts = names + listOfNotNull(appText)
        return when {
            parts.isEmpty() -> tr("Nichts gewählt", "Nothing selected")
            parts.size <= 2 -> parts.joinToString(tr(" und ", " and "))
            else -> parts.take(2).joinToString(", ") + tr(" +${parts.size - 2}", " +${parts.size - 2}")
        }
    }

    /**
     * Ist das Nutzungslimit erreicht? Dann einmal am Tag erinnern (oder alle 30 Minuten, wenn gewünscht).
     * Läuft im Hintergrund (Bedienungshilfe minütlich, Wecker halbstündlich). Nicht auf dem Haupt-Thread aufrufen.
     */
    fun checkUsage(context: Context, force: Boolean = false) {
        val p = prefs(context)
        val u = ReminderStore.usage(p)
        if (!u.on || !u.hasTargets || reading(p)) return
        val now = System.currentTimeMillis()
        // Nicht öfter als minütlich zählen (Bedienungshilfe und Wecker können zusammenfallen)
        if (!force && now - p.getLong(K_USAGE_CHECK, 0L) < 55_000L) return
        p.edit().putLong(K_USAGE_CHECK, now).apply()
        val minutes = usageMinutesToday(context, u) ?: return
        if (minutes < u.limitMin) return
        val today = LocalDate.now().toString()
        if (!force && p.getString(K_USAGE_DAY, null) == today) {
            if (!u.repeat) return
            if (now - p.getLong(K_USAGE_AT, 0L) < REPEAT_MS) return
            if (minutes <= p.getInt(K_USAGE_MIN, 0)) return   // seitdem nicht weiter in diesen Apps
        }
        p.edit().putString(K_USAGE_DAY, today).putLong(K_USAGE_AT, now).putInt(K_USAGE_MIN, minutes).apply()
        Lang.load(p)
        deliver(context, usageShown(context, u, minutes), u.delivery)
    }

    fun usageShown(context: Context, u: UsageLimit, minutes: Int): Shown {
        val s = Streaks.snapshot(prefs(context))
        val h = minutes / 60
        val m = minutes % 60
        val dur = if (h > 0) "$h h ${"%02d".format(m)} min" else "$m min"
        val what = usageTargetLabel(context, u)
        val options = listOf(
            Triple(R.drawable.lumi_w_sad, tr("Ganz schön viel Bildschirm", "That's a lot of screen"),
                tr("Heute schon $dur in $what. Wie wär's jetzt mit ${s.goal} Minuten Fokus?", "Already $dur in $what today. How about ${s.goal} minutes of focus now?")),
            Triple(R.drawable.lumi_w_plead, tr("Lumi hat mitgezählt", "Lumi kept count"),
                tr("$dur in $what heute. Ein bisschen Ruhe würde deinem Kopf jetzt guttun.", "$dur in $what today. A little quiet would do your mind good now.")),
            Triple(R.drawable.lumi_w_scared, tr("Uff, $dur", "Phew, $dur"),
                tr("So lange warst du heute in $what. Legst du das Handy kurz weg?", "That's how long you spent in $what today. Put the phone down for a bit?")),
        )
        val pick = options[Math.floorMod(LocalDate.now().toEpochDay().toInt(), options.size)]
        return Shown(USAGE_ID, ReminderKind.Usage, pick.second, pick.third, pick.first)
    }

    // ------------------------------------------------------------------------------------------
    // Mitteilung
    // ------------------------------------------------------------------------------------------

    fun notificationId(id: String): Int = 7000 + (id.hashCode() and 0x0FFF)

    private fun post(ctx: Context, shown: Shown) {
        if (!allowed(ctx)) return
        ensureChannel(ctx)
        val big = runCatching { BitmapFactory.decodeResource(ctx.resources, shown.image) }.getOrNull()
        val readNow = shown.kind != ReminderKind.Custom
        val b = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_notif)
            .setColor(de.funke.app.ui.theme.BrandColors.NotificationArgb)
            .setContentTitle(shown.title)
            .setContentText(shown.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(shown.text))
            .setLargeIcon(big)
            .setContentIntent(Widgets.openApp(ctx, 0))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            // Pop-up oben: Kanal HIGH (ab Android 8), PRIORITY_HIGH + Ton/Vibration (davor)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOnlyAlertOnce(false)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(0, tr("In $SNOOZE_MIN Min.", "In $SNOOZE_MIN min"), snoozePending(ctx, shown.id))
        if (readNow) b.addAction(0, tr("Loslegen", "Start"), Widgets.openApp(ctx, 0))
        try {
            val nm = NotificationManagerCompat.from(ctx)
            // Gleiche Erinnerung schon in der Leiste? Erst weg, damit das Pop-up sicher neu erscheint
            nm.cancel(notificationId(shown.id))
            nm.notify(notificationId(shown.id), b.build())
        } catch (_: SecurityException) {
        }
    }

    private fun snoozePending(ctx: Context, id: String): PendingIntent = PendingIntent.getBroadcast(
        ctx, 60_000 + (id.hashCode() and 0xFFFF),
        Intent(ctx, ReminderReceiver::class.java).setAction(ACTION_SNOOZE).putExtra(EXTRA_ID, id),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    fun ensureChannel(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        OLD_CHANNELS.forEach { old -> runCatching { nm.deleteNotificationChannel(old) } }
        if (nm.getNotificationChannel(CHANNEL) != null) return
        val channel = NotificationChannel(CHANNEL, tr("Erinnerungen", "Reminders"), NotificationManager.IMPORTANCE_HIGH).apply {
            description = tr(
                "Lumis Erinnerungen: Tagesziel, Streak, Schlafenszeit, Nutzungslimit und deine eigenen.",
                "Lumi's reminders: daily goal, streak, bedtime, usage limit and your own.",
            )
            enableVibration(true)
            setShowBadge(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        nm.createNotificationChannel(channel)
    }

    /** Lässt das System das Pop-up für Lumina zu? (Kanal auf "Wichtig"/Pop-up, nicht leise gestellt) */
    fun popupAllowed(context: Context): Boolean {
        val ch = context.getSystemService(NotificationManager::class.java)?.getNotificationChannel(CHANNEL) ?: return true
        return ch.importance >= NotificationManager.IMPORTANCE_HIGH
    }

    /** Öffnet die Einstellungen genau dieses Kanals (dort lässt sich "Pop-up" wieder einschalten). */
    fun openChannelSettings(context: Context) {
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    .putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure { openSystemSettings(context) }
    }

    // ------------------------------------------------------------------------------------------
    // Zustand & Einstellungen (für die "Beheben"-Zeilen)
    // ------------------------------------------------------------------------------------------

    /** Darf Lumina gerade Benachrichtigungen zeigen? (Berechtigung ab Android 13, nicht abgeschaltet, Kanal an) */
    fun allowed(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return false
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
        val ch = context.getSystemService(NotificationManager::class.java)?.getNotificationChannel(CHANNEL)
        return ch == null || ch.importance != NotificationManager.IMPORTANCE_NONE
    }

    /** Exakte Wecker erlaubt? (ab Android 14 nur, wenn man es selbst erlaubt hat; sonst kommen Erinnerungen ungenau) */
    fun exactOk(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 31) return true
        return context.getSystemService(AlarmManager::class.java)?.canScheduleExactAlarms() != false
    }

    /** Vom Energiesparen ausgenommen? (Samsung legt Apps sonst "schlafen", dann kommen Wecker zu spät) */
    fun batteryOk(context: Context): Boolean =
        context.getSystemService(PowerManager::class.java)?.isIgnoringBatteryOptimizations(context.packageName) == true

    /** Öffnet die Benachrichtigungs-Einstellungen von Lumina im System. */
    fun openSystemSettings(context: Context) {
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun openExactSettings(context: Context) {
        if (Build.VERSION.SDK_INT < 31) return
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + context.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure { openSystemSettings(context) }
    }

    /**
     * Öffnet die Energiespar-Liste des Systems, dort stellt man Lumina auf "Nicht optimieren" (seit 15.6 ohne
     * Direkt-Dialog: dessen Berechtigung erlaubt Google Play kaum einer App). Klappt das nicht, die App-Info.
     */
    fun requestBatteryExemption(context: Context) {
        runCatching {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }.recoverCatching {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    // ------------------------------------------------------------------------------------------
    // Ausprobieren
    // ------------------------------------------------------------------------------------------

    /** Probe-Nachricht (Einstellungen → Benachrichtigungen). */
    fun test(context: Context): Boolean {
        if (!allowed(context)) return false
        Lang.load(prefs(context))
        post(
            context.applicationContext,
            Shown("hello", ReminderKind.Custom, tr("Hallo von Lumi", "Hi from Lumi"),
                tr("So sehen meine Erinnerungen aus.", "This is what my reminders look like."),
                R.drawable.lumi_w_hello),
        )
        return true
    }

    /** Eine Erinnerung ausprobieren, genau auf ihrem Weg. */
    fun testReminder(context: Context, r: Reminder) {
        val shown = content(context.applicationContext, r, test = true) ?: return
        testShown(context, shown.copy(test = true), r.delivery)
    }

    /** Dieselbe Probe in einer Minute über den Wecker. */
    fun testReminderLater(context: Context, r: Reminder): Long {
        val shown = content(context.applicationContext, r, test = true) ?: return 0L
        return testLater(context, shown, r.delivery)
    }

    private fun usageTestShown(context: Context, u: UsageLimit): Shown {
        val ctx = context.applicationContext
        val minutes = (usageMinutesToday(ctx, u) ?: u.limitMin).coerceAtLeast(u.limitMin)
        return usageShown(ctx, u, minutes)
    }

    /** Probe für das Nutzungslimit: mit der heutigen Zeit (oder dem Limit, falls noch darunter). */
    fun testUsage(context: Context, u: UsageLimit) = testShown(context, usageTestShown(context, u).copy(test = true), u.delivery)

    fun testUsageLater(context: Context, u: UsageLimit): Long = testLater(context, usageTestShown(context, u), u.delivery)

    /**
     * Probe aus der App heraus: Mitteilung gleich in die Leiste, Bildschirm gleich hier in der App.
     * (Man benutzt das Handy ja gerade – genau so käme er auch im echten Fall.)
     */
    fun testShown(context: Context, shown: Shown, delivery: Delivery) {
        when (delivery) {
            Delivery.Notify -> post(context.applicationContext, shown)
            Delivery.Screen -> de.funke.app.ui.DeepLink.reminder = de.funke.app.ui.ReminderRequest(shown, fromOutside = false)
        }
    }
}

/** Wird vom AlarmManager geweckt: erinnern, Widgets auffrischen, nächsten Termin setzen. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Reminders.ACTION_SNOOZE -> {
                intent.getStringExtra(Reminders.EXTRA_ID)?.let { Reminders.snooze(context, it) }
                return
            }
            Reminders.ACTION -> {
                // Nutzungsdaten lesen kann etwas dauern: nicht im 10-Sekunden-Fenster des Empfängers hängen bleiben
                val done = goAsync()
                Thread {
                    try {
                        runCatching {
                            if (intent.getBooleanExtra(Reminders.EXTRA_TEST, false)) {
                                Reminders.fireTest(context, intent.getStringExtra(Reminders.EXTRA_SHOWN), intent.getStringExtra(Reminders.EXTRA_DELIVERY))
                            } else intent.getStringExtra(Reminders.EXTRA_ID)?.let {
                                Reminders.fire(context, it, snoozed = intent.getBooleanExtra(Reminders.EXTRA_SNOOZED, false))
                            }
                        }
                        Reminders.schedule(context)
                        Widgets.refresh(context)
                    } finally {
                        done.finish()
                    }
                }.start()
            }
        }
    }
}

/** Nach Neustart, App-Update oder Zeitumstellung sind Wecker weg bzw. falsch: neu planen. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Reminders.schedule(context)
        Widgets.refresh(context)
    }
}
