package de.funke.app.notify

import android.content.SharedPreferences
import de.funke.app.ui.tr
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate

/*
 * Alle Erinnerungen an einem Ort (seit 13.3).
 *
 * Eingebaut: Lesen, Streak in Gefahr, Schlafenszeit, Nutzungslimit. Sie behalten ihre alten Schlüssel
 * (reminder_on/hour/minute, streak_warn_*, nudge_bed_*, screen_warn_*), damit nach dem Update alles so
 * eingestellt bleibt wie vorher. Neu dazu: Wochentage und wie die Erinnerung kommt ([Delivery]).
 * Eigene Erinnerungen stehen als JSON unter "custom_reminders".
 */

/** Wie eine Erinnerung kommt (seit 13.4 nur noch zwei Wege). */
enum class Delivery(val id: String, private val de: String, private val en: String) {
    /** Pop-up oben am Bildschirm (heads-up), mit Ton. Seit 14.0 heißt es so, wie man es sieht. */
    Notify("notify", "Pop-up", "Pop-up"),
    /**
     * Lumi im Vollbild: Hast du das Handy gerade in der Hand, kommt er sofort, egal in welcher App.
     * Liegt es weg, kommt er beim nächsten Entsperren. Braucht die Bedienungshilfe "Lumina Sperre".
     */
    Screen("screen", "Vollbild", "Full screen");

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?, fallback: Delivery = Notify): Delivery = when (id) {
            "notify" -> Notify
            // "unlock" und "full" gab es in 13.3; beide werden zum Bildschirm
            "screen", "unlock", "full" -> Screen
            else -> fallback
        }
    }
}

/** Welche Art von Erinnerung. Die eingebauten haben eigene Texte und Bedingungen. */
enum class ReminderKind(val id: String) {
    Read("read"), Streak("streak"), Bed("bed"), Usage("usage"), Custom("custom");

    companion object {
        fun byId(id: String?): ReminderKind = entries.firstOrNull { it.id == id } ?: Custom
    }
}

/** Wochentage als Bitmaske: Montag = Bit 0 … Sonntag = Bit 6. */
object Days {
    const val ALL = 0b1111111
    const val WEEKDAYS = 0b0011111
    const val WEEKEND = 0b1100000

    fun has(mask: Int, day: DayOfWeek): Boolean = (mask and (1 shl (day.value - 1))) != 0
    fun toggle(mask: Int, day: DayOfWeek): Int = mask xor (1 shl (day.value - 1))
    fun on(mask: Int, date: LocalDate): Boolean = has(mask, date.dayOfWeek)

    /** "Täglich", "Mo–Fr", "Sa, So" oder "Mo, Mi, Fr". */
    fun label(mask: Int): String = when (mask and ALL) {
        ALL -> tr("täglich", "daily")
        WEEKDAYS -> tr("Mo–Fr", "Mon–Fri")
        WEEKEND -> tr("Sa, So", "Sat, Sun")
        0 -> tr("nie", "never")
        else -> DayOfWeek.entries.filter { has(mask, it) }.joinToString(", ") { short(it) }
    }

    fun short(d: DayOfWeek): String = when (d) {
        DayOfWeek.MONDAY -> tr("Mo", "Mon")
        DayOfWeek.TUESDAY -> tr("Di", "Tue")
        DayOfWeek.WEDNESDAY -> tr("Mi", "Wed")
        DayOfWeek.THURSDAY -> tr("Do", "Thu")
        DayOfWeek.FRIDAY -> tr("Fr", "Fri")
        DayOfWeek.SATURDAY -> tr("Sa", "Sat")
        DayOfWeek.SUNDAY -> tr("So", "Sun")
    }
}

/** Eine Erinnerung mit Uhrzeit (eingebaut oder eigene). */
data class Reminder(
    /** "read", "streak", "bed" oder "c_…" für eigene. */
    val id: String,
    val kind: ReminderKind,
    val on: Boolean,
    /** Minuten ab Mitternacht. */
    val minuteOfDay: Int,
    val days: Int,
    /** Nur bei eigenen Erinnerungen; die eingebauten bringen ihre eigenen Sätze mit. */
    val title: String,
    val text: String,
    val delivery: Delivery,
) {
    val hour: Int get() = minuteOfDay / 60
    val minute: Int get() = minuteOfDay % 60
    val isCustom: Boolean get() = kind == ReminderKind.Custom

    fun toJson(): JSONObject = JSONObject()
        .put("id", id).put("on", on).put("m", minuteOfDay).put("days", days)
        .put("title", title).put("text", text).put("delivery", delivery.id)

    companion object {
        fun fromJson(o: JSONObject): Reminder? = runCatching {
            Reminder(
                id = o.getString("id"),
                kind = ReminderKind.Custom,
                on = o.optBoolean("on", true),
                minuteOfDay = o.optInt("m", 9 * 60).coerceIn(0, 1439),
                days = o.optInt("days", Days.ALL) and Days.ALL,
                title = o.optString("title", ""),
                text = o.optString("text", ""),
                delivery = Delivery.byId(o.optString("delivery", null)),
            )
        }.getOrNull()
    }
}

/**
 * Nutzungslimit (früher "Viel Videozeit"): zählt die heutige Zeit in gewählten Gruppen und/oder Apps.
 * Ist das Limit erreicht, kommt eine Erinnerung – einmal am Tag, oder auf Wunsch alle 30 Minuten,
 * solange man weiter in diesen Apps ist.
 */
data class UsageLimit(
    val on: Boolean,
    val groups: Set<String>,
    val apps: Set<String>,
    /** Minuten, in 15er-Schritten. */
    val limitMin: Int,
    val repeat: Boolean,
    val delivery: Delivery,
) {
    val hasTargets: Boolean get() = groups.isNotEmpty() || apps.isNotEmpty()
}

object ReminderStore {
    // Eingebaut – alte Schlüssel (nicht umbenennen!)
    const val K_READ_ON = "reminder_on"
    const val K_READ_HOUR = "reminder_hour"
    const val K_READ_MINUTE = "reminder_minute"
    const val K_STREAK_ON = "streak_warn_on"
    const val K_STREAK_HOUR = "streak_warn_hour"
    const val K_STREAK_MINUTE = "streak_warn_minute"
    const val K_BED_ON = "nudge_bed_on"
    const val K_BED_AT = "nudge_bed_at"
    const val K_USAGE_ON = "screen_warn_on"
    const val K_USAGE_LIMIT = "screen_warn_limit"

    // Neu in 13.3
    private const val K_DAYS = "_days"
    private const val K_DELIVERY = "_delivery"
    const val K_USAGE_GROUPS = "usage_groups"
    const val K_USAGE_APPS = "usage_apps"
    const val K_USAGE_REPEAT = "usage_repeat"
    private const val K_CUSTOM = "custom_reminders"

    /** Standard-Gruppen fürs Nutzungslimit (wie früher "Videos & Social Media"). */
    val DefaultUsageGroups = setOf("video", "social")

    fun builtIn(p: SharedPreferences, kind: ReminderKind): Reminder = when (kind) {
        ReminderKind.Read -> Reminder(
            "read", kind, p.safeBool(K_READ_ON, true),
            p.safeInt(K_READ_HOUR, 19).coerceIn(0, 23) * 60 + p.safeInt(K_READ_MINUTE, 0).coerceIn(0, 59),
            p.safeInt("read$K_DAYS", Days.ALL) and Days.ALL, "", "", Delivery.byId(p.safeString("read$K_DELIVERY"), Delivery.Notify),
        )
        ReminderKind.Streak -> Reminder(
            "streak", kind, p.safeBool(K_STREAK_ON, true),
            p.safeInt(K_STREAK_HOUR, 21).coerceIn(0, 23) * 60 + p.safeInt(K_STREAK_MINUTE, 30).coerceIn(0, 59),
            p.safeInt("streak$K_DAYS", Days.ALL) and Days.ALL, "", "", Delivery.byId(p.safeString("streak$K_DELIVERY"), Delivery.Notify),
        )
        ReminderKind.Bed -> Reminder(
            "bed", kind, p.safeBool(K_BED_ON, false),
            p.safeInt(K_BED_AT, 22 * 60).coerceIn(0, 1439),
            p.safeInt("bed$K_DAYS", Days.ALL) and Days.ALL, "", "", Delivery.byId(p.safeString("bed$K_DELIVERY"), Delivery.Screen),
        )
        else -> error("kein eingebauter Termin: $kind")
    }

    /** Speichert eine eingebaute Erinnerung in ihren alten Schlüsseln (+ Tage, Lieferart). */
    fun saveBuiltIn(p: SharedPreferences, r: Reminder) {
        val e = p.edit()
        when (r.kind) {
            ReminderKind.Read -> e.putBoolean(K_READ_ON, r.on).putInt(K_READ_HOUR, r.hour).putInt(K_READ_MINUTE, r.minute)
            ReminderKind.Streak -> e.putBoolean(K_STREAK_ON, r.on).putInt(K_STREAK_HOUR, r.hour).putInt(K_STREAK_MINUTE, r.minute)
            ReminderKind.Bed -> e.putBoolean(K_BED_ON, r.on).putInt(K_BED_AT, r.minuteOfDay)
            else -> return
        }
        e.putInt(r.id + K_DAYS, r.days).putString(r.id + K_DELIVERY, r.delivery.id).apply()
    }

    fun builtIns(p: SharedPreferences): List<Reminder> =
        listOf(ReminderKind.Read, ReminderKind.Streak, ReminderKind.Bed).map { builtIn(p, it) }

    fun customs(p: SharedPreferences): List<Reminder> {
        val raw = p.safeString(K_CUSTOM) ?: return emptyList()
        return runCatching {
            val a = JSONArray(raw)
            List(a.length()) { i -> Reminder.fromJson(a.getJSONObject(i)) }.filterNotNull()
        }.getOrDefault(emptyList())
    }

    fun saveCustoms(p: SharedPreferences, list: List<Reminder>) {
        val a = JSONArray()
        list.forEach { a.put(it.toJson()) }
        p.edit().putString(K_CUSTOM, a.toString()).apply()
    }

    fun upsertCustom(p: SharedPreferences, r: Reminder) {
        val list = customs(p).toMutableList()
        val i = list.indexOfFirst { it.id == r.id }
        if (i >= 0) list[i] = r else list.add(r)
        saveCustoms(p, list)
    }

    fun deleteCustom(p: SharedPreferences, id: String) = saveCustoms(p, customs(p).filterNot { it.id == id })

    fun newCustomId(): String = "c_" + java.lang.Long.toString(System.currentTimeMillis(), 36)

    /** Alle Erinnerungen mit Uhrzeit (eingebaute zuerst). */
    fun all(p: SharedPreferences): List<Reminder> = builtIns(p) + customs(p)

    fun byId(p: SharedPreferences, id: String): Reminder? = when (id) {
        "read" -> builtIn(p, ReminderKind.Read)
        "streak" -> builtIn(p, ReminderKind.Streak)
        "bed" -> builtIn(p, ReminderKind.Bed)
        else -> customs(p).firstOrNull { it.id == id }
    }

    fun usage(p: SharedPreferences): UsageLimit = UsageLimit(
        on = p.safeBool(K_USAGE_ON, true),
        groups = p.safeString(K_USAGE_GROUPS)?.split(',')?.filter { it.isNotBlank() }?.toSet() ?: DefaultUsageGroups,
        apps = p.safeString(K_USAGE_APPS).orEmpty().split(',').filter { it.isNotBlank() }.toSet(),
        limitMin = (p.safeInt(K_USAGE_LIMIT, 120) / 15 * 15).coerceIn(15, 12 * 60),
        repeat = p.safeBool(K_USAGE_REPEAT, false),
        delivery = Delivery.byId(p.safeString("usage$K_DELIVERY"), Delivery.Notify),
    )

    fun saveUsage(p: SharedPreferences, u: UsageLimit) {
        p.edit()
            .putBoolean(K_USAGE_ON, u.on)
            .putString(K_USAGE_GROUPS, u.groups.joinToString(","))
            .putString(K_USAGE_APPS, u.apps.joinToString(","))
            .putInt(K_USAGE_LIMIT, u.limitMin)
            .putBoolean(K_USAGE_REPEAT, u.repeat)
            .putString("usage$K_DELIVERY", u.delivery.id)
            .apply()
    }
}

/*
 * Robuste Lesehilfen (seit 14.0). Wurde ein Schlüssel in einer alten Version mit anderem Typ gespeichert
 * (z. B. Zahl als Text), wirft SharedPreferences eine ClassCastException – und die Seite stürzt ab.
 * Diese Helfer lesen notfalls den rohen Wert und wandeln ihn um, sonst gilt der Standardwert.
 */
internal fun SharedPreferences.safeInt(key: String, def: Int): Int = try {
    getInt(key, def)
} catch (_: ClassCastException) {
    when (val v = all[key]) {
        is Number -> v.toInt()
        is String -> v.toIntOrNull() ?: def
        else -> def
    }
}

internal fun SharedPreferences.safeBool(key: String, def: Boolean): Boolean = try {
    getBoolean(key, def)
} catch (_: ClassCastException) {
    when (val v = all[key]) {
        is String -> v.toBooleanStrictOrNull() ?: def
        is Number -> v.toInt() != 0
        else -> def
    }
}

internal fun SharedPreferences.safeString(key: String): String? = try {
    getString(key, null)
} catch (_: ClassCastException) {
    when (val v = all[key]) {
        is Set<*> -> v.filterIsInstance<String>().joinToString(",")
        null -> null
        else -> v.toString()
    }
}

