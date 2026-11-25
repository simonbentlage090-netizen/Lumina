package de.funke.app.gate

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.funke.app.ui.tr
import java.time.LocalDate
import java.time.LocalDateTime

/*
 * Denkpause (seit 12.0 nach dem Vorbild von JEE PYQ Lock): Man wählt Apps und wie oft am Tag gefragt wird.
 * Zu jeder Fragezeit (z. B. 8, 11, 14, 17, 20 Uhr) wird eine Frage "fällig". Öffnet man danach eine der
 * gewählten Apps – oder ist gerade in einer –, kommt die Frage. Antworten oder "Weiß ich nicht", Erklärung
 * lesen, weiter. Bis zur nächsten Fragezeit ist dann Ruhe.
 *
 * Läuft über dieselbe Bedienungshilfe wie die Lese-Sperre ([de.funke.app.lock.LockService]); während einer
 * Lese-Session hat die Sperre Vorrang. Alles bleibt auf dem Handy: gespeichert werden nur die gewählten Apps,
 * die Fragezeiten und kleine Zähler.
 *
 * Grundlage: In einer Feldstudie mit der App "one sec" (Grüning, Riedel & Lorenz-Spreen 2023, PNAS) wurden
 * Ziel-Apps nach sechs Wochen 57 % seltener geöffnet; eine kurze Unterbrechung mit der bequemen Wahl,
 * es bleiben zu lassen, wirkte am stärksten. Deshalb gibt es nach jeder Frage auch "Lieber nicht".
 */

/** Wie oft am Tag gefragt wird. */
enum class GateIntensity(
    val id: String,
    private val de: String,
    private val en: String,
    val hours: List<Int>,
) {
    Light("light", "Locker", "Easy", listOf(9, 14, 20)),
    Balanced("balanced", "Ausgewogen", "Balanced", listOf(8, 11, 14, 17, 20)),
    Intense("intense", "Intensiv", "Intense", listOf(8, 10, 12, 14, 16, 18, 20, 22)),
    Custom("custom", "Eigene", "Custom", emptyList());

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?): GateIntensity = entries.firstOrNull { it.id == id } ?: Balanced
    }
}

object Gate {
    private const val K_ON = "gate_on"
    private const val K_APPS = "gate_apps"
    private const val K_INTENSITY = "gate_intensity"
    private const val K_CUSTOM = "gate_custom_hours"
    private const val K_DONE = "gate_done_slot"
    private const val K_STATS = "gate_stats"
    private const val K_STATS_DAY = "gate_stats_day"
    private const val K_RESISTED_TOTAL = "gate_resisted_total"

    /** Wählbare Stunden für eigene Fragezeiten. */
    val CustomRange = 6..23

    private var prefs: SharedPreferences? = null

    // Alles Compose-Zustand (seit 13.4): Ändert sich etwas, zeichnen auch offene Blätter sofort neu.
    var enabled: Boolean by mutableStateOf(false)
        private set
    var apps: Set<String> by mutableStateOf(emptySet())
        private set
    var intensity: GateIntensity by mutableStateOf(GateIntensity.Balanced)
        private set
    var customHours: Set<Int> by mutableStateOf(setOf(9, 13, 18, 21))
        private set
    /** Zuletzt erledigte Fragezeit ("2026-11-13T14"). */
    private var doneSlot: String by mutableStateOf("")

    // ----- Zähler (heute) -----
    var shownToday: Int by mutableIntStateOf(0)
        private set
    var rightToday: Int by mutableIntStateOf(0)
        private set
    var resistedToday: Int by mutableIntStateOf(0)
        private set
    var resistedTotal: Int by mutableIntStateOf(0)
        private set
    private var statsDay: String = ""

    /** Wird erhöht, wenn sich etwas ändert (damit die Oberfläche neu zeichnet). */
    var version: Int by mutableIntStateOf(0)
        private set

    fun load(p: SharedPreferences) {
        prefs = p
        enabled = p.getBoolean(K_ON, false)
        apps = p.getString(K_APPS, "")!!.split(',').filter { it.isNotBlank() }.toSet()
        intensity = GateIntensity.byId(p.getString(K_INTENSITY, null))
        customHours = p.getString(K_CUSTOM, null)?.split(',')?.mapNotNull { it.toIntOrNull()?.takeIf { h -> h in 0..23 } }?.toSet()
            ?.takeIf { it.isNotEmpty() } ?: setOf(9, 13, 18, 21)
        doneSlot = p.getString(K_DONE, "") ?: ""
        resistedTotal = p.getInt(K_RESISTED_TOTAL, 0)
        loadStats()
        version++
    }

    private fun edit(block: SharedPreferences.Editor.() -> Unit) {
        prefs?.edit()?.apply(block)?.apply()
        version++
    }

    fun applyEnabled(on: Boolean) { enabled = on; edit { putBoolean(K_ON, on) } }
    fun applyApps(set: Set<String>) { apps = set; edit { putString(K_APPS, set.joinToString(",")) } }
    fun applyIntensity(i: GateIntensity) { intensity = i; edit { putString(K_INTENSITY, i.id) } }
    fun toggleCustomHour(h: Int) {
        val next = if (h in customHours) customHours - h else customHours + h
        if (next.isEmpty()) return
        customHours = next
        edit { putString(K_CUSTOM, next.sorted().joinToString(",")) }
    }

    /**
     * Eine Stunde an- oder ausschalten, egal welche Vorlage gerade gilt (seit 13.4): Bei einer Vorlage werden
     * deren Zeiten zu eigenen Zeiten, dann ändert sich genau diese eine Stunde. Mindestens eine bleibt.
     */
    fun editHour(h: Int) {
        if (h !in CustomRange) return
        val base = if (intensity == GateIntensity.Custom) customHours else hours.toSet()
        val next = if (h in base) base - h else base + h
        if (next.isEmpty()) return
        customHours = next
        intensity = GateIntensity.Custom
        edit { putString(K_CUSTOM, next.sorted().joinToString(",")); putString(K_INTENSITY, GateIntensity.Custom.id) }
    }

    /** Die Fragezeiten (volle Stunden), sortiert. */
    val hours: List<Int> get() = if (intensity == GateIntensity.Custom) customHours.sorted() else intensity.hours

    /** Die letzte Fragezeit heute, die schon begonnen hat (oder null vor der ersten). */
    fun slotAt(t: LocalDateTime): LocalDateTime? =
        hours.lastOrNull { it <= t.hour }?.let { t.toLocalDate().atTime(it, 0) }

    private fun key(slot: LocalDateTime) = slot.toLocalDate().toString() + "T" + slot.hour

    /** Ist gerade eine Frage fällig (seit der letzten Fragezeit noch keine beantwortet)? */
    fun isDue(t: LocalDateTime = LocalDateTime.now()): Boolean {
        if (!enabled || apps.isEmpty()) return false
        val slot = slotAt(t) ?: return false
        return key(slot) != doneSlot
    }

    /** Kommt vor [pkg] gerade eine Frage? */
    fun needsGate(pkg: String, t: LocalDateTime = LocalDateTime.now()): Boolean = pkg in apps && isDue(t)

    /** Frage beantwortet (oder Lösung angesehen): bis zur nächsten Fragezeit ist Ruhe. */
    fun consume(t: LocalDateTime = LocalDateTime.now()) {
        val slot = slotAt(t) ?: return
        doneSlot = key(slot)
        edit { putString(K_DONE, doneSlot) }
    }

    /** Die nächste Fragezeit nach jetzt (heute oder morgen). */
    fun nextSlot(t: LocalDateTime = LocalDateTime.now()): LocalDateTime? {
        val h = hours
        if (h.isEmpty()) return null
        val today = h.firstOrNull { it > t.hour }
        return if (today != null) t.toLocalDate().atTime(today, 0) else t.toLocalDate().plusDays(1).atTime(h.first(), 0)
    }

    // ----- Zähler -----

    private fun loadStats() {
        val p = prefs ?: return
        statsDay = p.getString(K_STATS_DAY, "") ?: ""
        if (statsDay != LocalDate.now().toString()) {
            shownToday = 0; rightToday = 0; resistedToday = 0
            statsDay = LocalDate.now().toString()
            return
        }
        val a = p.getString(K_STATS, "")!!.split(',').map { it.toIntOrNull() ?: 0 }
        shownToday = a.getOrElse(0) { 0 }
        rightToday = a.getOrElse(1) { 0 }
        resistedToday = a.getOrElse(2) { 0 }
    }

    private fun rollDay() {
        val today = LocalDate.now().toString()
        if (statsDay != today) {
            statsDay = today
            shownToday = 0; rightToday = 0; resistedToday = 0
        }
    }

    private fun saveStats() {
        edit {
            putString(K_STATS_DAY, statsDay)
            putString(K_STATS, "$shownToday,$rightToday,$resistedToday")
            putInt(K_RESISTED_TOTAL, resistedTotal)
        }
    }

    fun countShown() { rollDay(); shownToday++; saveStats() }
    fun countRight() { rollDay(); rightToday++; saveStats() }
    fun countResisted() { rollDay(); resistedToday++; resistedTotal++; saveStats() }
}
