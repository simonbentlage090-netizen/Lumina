package de.funke.app.progress

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.time.LocalDate

/*
 * Streak und Tagesminuten an einer Stelle, damit App, Widgets und Erinnerungen dasselbe rechnen.
 *
 * Regeln seit 9.0:
 *  - Session ganz durchgezogen: Minuten zählen und der Tag zählt für den Streak.
 *  - Mit gelöster Aufgabe früher beendet: Minuten zählen, der Streak aber nicht.
 *  - Über das Kreuz ohne Aufgabe beendet: zählt gar nicht.
 *  - Streak-Eis: Alle 4 Tage am Stück gibt es ein Eis (höchstens 2 auf Vorrat). Verpasst man einen Tag,
 *    wird er automatisch mit Eis überbrückt, sodass der Streak hält (der Tag selbst zählt nicht mit).
 *  - Rettungs-Eis (seit 9.3): Lief der Streak länger als 5 Tage, ist kein Eis mehr da und wurde genau
 *    ein Tag verpasst, schenkt Lumi einmal ein Eis – höchstens einmal in 30 Tagen. So reißt ein langer
 *    Streak nicht wegen eines einzigen Ausrutschers, aber man kann sich nicht darauf ausruhen.
 */
object Streaks {
    const val PREFS = "funke"
    const val K_DAYS = "day_minutes"
    const val K_DONE = "streak_days"
    const val K_FROZEN = "frozen_days"
    const val K_FREEZES = "streak_freezes"
    const val K_FREEZE_AT = "freeze_awarded_at"
    const val K_GOAL = "daily_goal"
    const val K_BEST = "best_streak"
    const val K_STREAK_GOAL = "streak_goal"
    /** Tage, die ein geschenktes Rettungs-Eis überbrückt hat. */
    const val K_GIFTS = "gift_freeze_days"
    /** Rettungs-Eis gibt es nur, wenn der Streak vorher mindestens so lang war ("länger als 5 Tage"). */
    const val GIFT_MIN_STREAK = 6
    const val GIFT_EVERY_DAYS = 30
    const val MAX_FREEZES = 2
    const val FREEZE_EVERY = 4

    fun parseSet(s: String?): Set<String> = s?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
    fun joinSet(set: Set<String>): String = set.sorted().joinToString(",")

    /** Tage in Folge (heute zählt mit, wenn schon geschafft; sonst ab gestern). Eis-Tage überbrücken nur. */
    fun count(done: Set<String>, frozen: Set<String>, today: LocalDate = LocalDate.now()): Int {
        var day = today
        if (day.toString() !in done && day.toString() !in frozen) day = day.minusDays(1)
        var n = 0
        var guard = 0
        while (guard++ < 5000) {
            val k = day.toString()
            if (k in done) n++ else if (k !in frozen) break
            day = day.minusDays(1)
        }
        return n
    }

    /**
     * Verpasste Tage vor heute mit Eis überbrücken, wenn genug Eis da ist und davor ein Streak lief.
     * @return die neuen Eis-Tage und wie viel Eis verbraucht wurde.
     */
    fun bridge(done: Set<String>, frozen: Set<String>, freezes: Int, today: LocalDate = LocalDate.now()): Pair<Set<String>, Int> {
        if (freezes <= 0) return frozen to 0
        var day = today.minusDays(1)
        val missing = mutableListOf<String>()
        while (true) {
            val k = day.toString()
            if (k in done || k in frozen) break
            missing += k
            if (missing.size > freezes) return frozen to 0
            day = day.minusDays(1)
        }
        if (missing.isEmpty()) return frozen to 0
        if (count(done, frozen, day) == 0) return frozen to 0
        return (frozen + missing) to missing.size
    }

    /**
     * Rettungs-Eis: Gibt den Tag (gestern) zurück, den Lumi verschenkt überbrückt, oder null.
     * Nur wenn genau gestern fehlt, der Streak davor mindestens [GIFT_MIN_STREAK] Tage lang war und
     * in den 30 Tagen davor kein Rettungs-Eis verschenkt wurde.
     */
    fun rescue(done: Set<String>, frozen: Set<String>, gifts: Set<String>, today: LocalDate = LocalDate.now()): String? {
        val y = today.minusDays(1)
        val yk = y.toString()
        if (yk in done || yk in frozen) return null
        val before = y.minusDays(1)
        if (before.toString() !in done && before.toString() !in frozen) return null
        if (count(done, frozen, before) < GIFT_MIN_STREAK) return null
        val since = y.minusDays(GIFT_EVERY_DAYS.toLong())
        val recent = gifts.any { g ->
            runCatching { LocalDate.parse(g) }.getOrNull()?.let { !it.isBefore(since) && it.isBefore(y) } ?: false
        }
        return if (recent) null else yk
    }

    /** Ergebnis von [bridgeAll]: neue Eis-Tage, verbrauchtes Eis und ggf. der Tag mit Rettungs-Eis. */
    class Bridged(val frozen: Set<String>, val used: Int, val gift: String?)

    /** Erst normales Eis, und wenn das nicht reicht, ggf. Rettungs-Eis. */
    fun bridgeAll(done: Set<String>, frozen: Set<String>, freezes: Int, gifts: Set<String>, today: LocalDate = LocalDate.now()): Bridged {
        val (f, used) = bridge(done, frozen, freezes, today)
        if (used > 0) return Bridged(f, used, null)
        val g = rescue(done, frozen, gifts, today) ?: return Bridged(frozen, 0, null)
        return Bridged(frozen + g, 0, g)
    }

    /** Ist der Streak heute in Gefahr? (gestern lief er noch, heute noch nichts geschafft) */
    fun atRisk(done: Set<String>, frozen: Set<String>, today: LocalDate = LocalDate.now()): Boolean =
        today.toString() !in done && count(done, frozen, today) > 0

    /** Alles, was Widgets und Erinnerungen brauchen, direkt aus dem Speicher gelesen. */
    class Snapshot(
        val dayMinutes: Map<String, Int>,
        val done: Set<String>,
        val frozen: Set<String>,
        val freezes: Int,
        val goal: Int,
        val best: Int,
        /** Streak-Ziel in Tagen (0 = keins). */
        val streakGoal: Int = 0,
        /** Tage, die ein geschenktes Rettungs-Eis überbrückt hat. */
        val gifts: Set<String> = emptySet(),
    ) {
        val today: LocalDate get() = LocalDate.now()
        fun minutesOn(d: LocalDate) = dayMinutes[d.toString()] ?: 0
        val todayMinutes: Int get() = minutesOn(today)
        val streak: Int get() = count(done, frozen)
        val doneToday: Boolean get() = today.toString() in done
        val goalMetToday: Boolean get() = todayMinutes >= goal
        val atRisk: Boolean get() = atRisk(done, frozen)
        /** Gestern hat Eis den Streak gerettet. */
        val frozeYesterday: Boolean get() = today.minusDays(1).toString() in frozen
        /** Gestern hat Lumi ein Rettungs-Eis verschenkt. */
        val giftYesterday: Boolean get() = today.minusDays(1).toString() in gifts

        /** Tage seit dem letzten Lesetag (0 = heute gelesen, -1 = noch nie). */
        val daysSinceRead: Int
            get() {
                for (i in 0..365) if (minutesOn(today.minusDays(i.toLong())) > 0 || today.minusDays(i.toLong()).toString() in done) return i
                return -1
            }
    }

    fun snapshot(context: Context): Snapshot = snapshot(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE))

    /**
     * Repariert "streak_days" (seit 15.8): Bis 15.7 schrieb die Streak-Erinnerung ihre Wochentage als Zahl unter
     * denselben Schlüssel. Dann stand dort keine Liste von Tagen mehr, und jedes Lesen stürzte ab. Die Zahl wandert
     * zur Erinnerung zurück; ohne gespeicherte Tage nimmt Lumina wieder alle Tage mit Minuten (wie vor 8.4).
     */
    fun repair(prefs: SharedPreferences) {
        val wrong = try {
            prefs.getString(K_DONE, null)
            return
        } catch (_: ClassCastException) {
            prefs.all[K_DONE]
        }
        val key = de.funke.app.notify.ReminderStore.K_DAYS_PREFIX + "streak"
        val e = prefs.edit().remove(K_DONE)
        if (wrong is Int && !prefs.contains(key)) e.putInt(key, wrong)
        e.commit()
    }

    fun snapshot(prefs: SharedPreferences): Snapshot {
        repair(prefs)
        val days = HashMap<String, Int>()
        prefs.getString(K_DAYS, null)?.let { json ->
            runCatching { val o = JSONObject(json); for (k in o.keys()) days[k] = o.getInt(k) }
        }
        val done = if (prefs.contains(K_DONE)) parseSet(prefs.getString(K_DONE, null)) else days.filterValues { it > 0 }.keys
        // Eis so einrechnen, wie die App es beim nächsten Öffnen tun würde (hier nur lesen, nicht speichern),
        // damit Widget und Erinnerung keinen Streak von 0 zeigen, den das Eis eigentlich rettet.
        val gifts = parseSet(prefs.getString(K_GIFTS, null))
        val b = bridgeAll(done, parseSet(prefs.getString(K_FROZEN, null)), prefs.getInt(K_FREEZES, 0), gifts)
        return Snapshot(
            dayMinutes = days,
            done = done,
            frozen = b.frozen,
            freezes = prefs.getInt(K_FREEZES, 0) - b.used,
            goal = prefs.getInt(K_GOAL, 30),
            best = prefs.getInt(K_BEST, 0),
            streakGoal = prefs.getInt(K_STREAK_GOAL, 0),
            gifts = if (b.gift != null) gifts + b.gift else gifts,
        )
    }
}
