package de.funke.app.ui.challenge

import de.funke.app.ui.tr

/*
 * Vorlieben und Verlauf für den Aufgaben-Reiter (seit 10.1).
 *
 * - [TaskKind]: welche Art Aufgabe (rechnen, mehrere Schritte, verstehen, schätzen).
 * - [TaskPrefs]: Lieblings-Unterthemen ("mehr" oder "nur") und Aufgabenarten, die man mag oder nicht mag.
 *   Standard: alles normal – dann wählt der Algorithmus allein.
 * - [TaskHistory]: wann welche Aufgabe zuletzt dran war (bleibt über App-Neustarts erhalten) und wie gut es
 *   pro Fach und Stufe läuft. Daraus wird die [TaskHistory.tier] (0–3): Wer auf einer Stufe viel richtig
 *   löst, bekommt mit der Zeit größere, krummere Zahlen und mehr Größen auf einmal.
 *
 * Gespeichert wird über einen [Store] (in der App die SharedPreferences, in Tests ein Speicher im Arbeitsspeicher).
 */

/** Art einer Aufgabe – für "mag ich" / "mag ich nicht". */
enum class TaskKind(val id: String, private val de: String, private val en: String, private val deHint: String, private val enHint: String) {
    Calc("calc", "Rechnen", "Calculate", "Eine Größe ausrechnen", "Work out one quantity"),
    Steps("steps", "Mehrere Schritte", "Multi-step", "Aufgaben in Teilen, eins baut aufs andere", "Problems in parts that build on each other"),
    Concept("concept", "Verstehen", "Concepts", "Auswahlfragen: Was passiert und warum?", "Choice questions: what happens and why?"),
    Estimate("estimate", "Schätzen", "Estimate", "Größenordnungen abschätzen", "Estimate orders of magnitude");

    val label: String get() = tr(de, en)
    val hint: String get() = tr(deHint, enHint)

    companion object {
        fun byId(id: String?): TaskKind? = entries.firstOrNull { it.id == id }
    }
}

/** Art einer fertigen Aufgabe. */
val Task.kind: TaskKind
    get() = when {
        steps.size > 1 -> TaskKind.Steps
        steps.first().answer is Answer.Choice -> TaskKind.Concept
        steps.first().answer is Answer.Estimate -> TaskKind.Estimate
        else -> TaskKind.Calc
    }

/** Wie gern man ein Unterthema hat. */
enum class TopicPref(val id: String) {
    Off("off"), Normal("normal"), More("more"), Only("only");

    val label: String
        get() = when (this) {
            Off -> tr("Aus", "Off")
            Normal -> tr("Normal", "Normal")
            More -> tr("Mehr", "More")
            Only -> tr("Nur", "Only")
        }

    companion object {
        fun byId(id: String?): TopicPref = entries.firstOrNull { it.id == id } ?: Normal
    }
}

/** Wie gern man eine Aufgabenart hat. */
enum class KindPref(val id: String) {
    Less("less"), Normal("normal"), More("more");

    val label: String
        get() = when (this) {
            Less -> tr("Weniger", "Less")
            Normal -> tr("Normal", "Normal")
            More -> tr("Mehr", "More")
        }

    companion object {
        fun byId(id: String?): KindPref = entries.firstOrNull { it.id == id } ?: Normal
    }
}

/** Einfacher Schlüssel-Wert-Speicher (SharedPreferences in der App). */
interface Store {
    fun get(key: String): String?
    fun put(key: String, value: String?)
}

/** Speicher im Arbeitsspeicher (Tests, Vorschau). */
class MemoryStore : Store {
    private val map = HashMap<String, String>()
    override fun get(key: String) = map[key]
    override fun put(key: String, value: String?) {
        if (value == null) map.remove(key) else map[key] = value
    }
}

private fun encode(m: Map<String, String>): String = m.entries.joinToString(";") { it.key + "=" + it.value }
private fun decode(s: String?): Map<String, String> =
    s.orEmpty().split(';').mapNotNull { e -> e.split('=', limit = 2).takeIf { it.size == 2 && it[0].isNotEmpty() }?.let { it[0] to it[1] } }.toMap()

object TaskPrefs {
    private const val K_TOPICS = "task_pref_topics"
    private const val K_KINDS = "task_pref_kinds"
    private const val K_OFF = "task_pref_off_subjects"
    private const val K_MIX_OFF = "task_pref_mix_off"
    private var store: Store = MemoryStore()

    /** Wird bei jeder Änderung hochgezählt (damit die Oberfläche neu zeichnet). */
    var version: Int = 0
        private set

    private var topics: Map<String, String> = emptyMap()
    private var kinds: Map<String, String> = emptyMap()
    /** Ausgeschaltete Fächer (seit 11.0): erscheinen nicht im Aufgaben-Reiter und nicht in "Gemischt". */
    private var offSubjects: Set<String> = emptySet()
    /** Fächer, die nicht in "Gemischt" sollen (seit 15.2). Neue Fächer sind damit automatisch im Mix. */
    private var mixOff: Set<String> = emptySet()

    fun attach(s: Store) {
        store = s
        topics = decode(s.get(K_TOPICS))
        kinds = decode(s.get(K_KINDS))
        offSubjects = s.get(K_OFF).orEmpty().split(',').filter { it.isNotBlank() }.toSet()
        mixOff = s.get(K_MIX_OFF).orEmpty().split(',').filter { it.isNotBlank() }.toSet()
        version++
    }

    fun inMix(subject: Subject): Boolean = subject.id !in mixOff

    fun setInMix(subject: Subject, on: Boolean) {
        mixOff = if (on) mixOff - subject.id else mixOff + subject.id
        store.put(K_MIX_OFF, mixOff.joinToString(",").ifEmpty { null })
        version++
    }

    /** Fächer in "Gemischt": eingeschaltet und für den Mix gewählt. Ist keins gewählt, alle eingeschalteten. */
    fun mixSubjects(): List<Subject> {
        val on = PracticeSubjects.filter { subjectOn(it) }
        return on.filter { inMix(it) }.ifEmpty { on }.ifEmpty { PracticeSubjects }
    }

    fun subjectOn(subject: Subject): Boolean = subject.id !in offSubjects

    fun setSubjectOn(subject: Subject, on: Boolean) {
        offSubjects = if (on) offSubjects - subject.id else offSubjects + subject.id
        store.put(K_OFF, offSubjects.joinToString(",").ifEmpty { null })
        version++
    }

    /**
     * Gewichte für eine gemischte Auswahl aus mehreren Fächern: jedes Fach für sich nach [weights]
     * und so skaliert, dass jedes Fach gleich viel zählt (sonst würde Physik mit vielen Themen alles schlucken).
     */
    fun weightsBySubject(all: Set<Topic>): Map<Topic, Double> {
        val groups = all.groupBy { it.subject }
        if (groups.size <= 1) return weights(all)
        return groups.flatMap { (_, list) ->
            val w = weights(list.toSet())
            val sum = w.values.sum().coerceAtLeast(1e-9)
            w.entries.map { it.key to it.value / sum }
        }.toMap()
    }

    fun topic(t: Topic): TopicPref = TopicPref.byId(topics[t.id])
    fun kind(k: TaskKind): KindPref = KindPref.byId(kinds[k.id])

    fun setTopic(t: Topic, p: TopicPref) {
        topics = if (p == TopicPref.Normal) topics - t.id else topics + (t.id to p.id)
        store.put(K_TOPICS, encode(topics).ifEmpty { null })
        version++
    }

    fun setKind(k: TaskKind, p: KindPref) {
        kinds = if (p == KindPref.Normal) kinds - k.id else kinds + (k.id to p.id)
        store.put(K_KINDS, encode(kinds).ifEmpty { null })
        version++
    }

    /** Alles zurück auf "Lumi wählt". */
    fun reset(subject: Subject? = null) {
        topics = if (subject == null) emptyMap() else topics.filterKeys { id -> Topic.byId(id)?.subject != subject }
        if (subject == null) kinds = emptyMap()
        store.put(K_TOPICS, encode(topics).ifEmpty { null })
        store.put(K_KINDS, encode(kinds).ifEmpty { null })
        version++
    }

    /** Hat der Nutzer in diesem Fach etwas eingestellt? */
    fun customized(subject: Subject): Boolean = topics.keys.any { Topic.byId(it)?.subject == subject } || kinds.isNotEmpty()

    /**
     * Themen eines Fachs mit Gewicht: "Nur" schließt alle anderen aus, "Aus" fällt weg, "Mehr" zählt vierfach.
     * Bleibt nichts übrig (alles aus), zählen wieder alle.
     */
    fun weights(all: Set<Topic>): Map<Topic, Double> {
        val only = all.filter { topic(it) == TopicPref.Only }
        val base = if (only.isNotEmpty()) only else all.filter { topic(it) != TopicPref.Off }
        val list = base.ifEmpty { all.toList() }
        return list.associateWith { if (topic(it) == TopicPref.More) 4.0 else 1.0 }
    }

    fun kindWeight(k: TaskKind): Double = when (kind(k)) {
        KindPref.Less -> 0.25
        KindPref.Normal -> 1.0
        KindPref.More -> 2.5
    }
}

object TaskHistory {
    private const val K_SEEN = "task_seen"
    private const val K_TICK = "task_tick"
    private const val K_STATS = "task_stats"
    private const val MAX_SEEN = 600
    private var store: Store = MemoryStore()

    /** Zähler, der bei jeder gezogenen Aufgabe weiterläuft (bleibt gespeichert). */
    private var tick = 0L
    private val seen = LinkedHashMap<String, Long>()

    /** Pro "fach:stufe": sauber gelöst, gelöst, versucht, letzte Ergebnisse (1 = richtig, 0 = nicht). */
    private class Stat(var clean: Int = 0, var solved: Int = 0, var tried: Int = 0, var recent: String = "")
    private val stats = HashMap<String, Stat>()

    fun attach(s: Store) {
        store = s
        tick = s.get(K_TICK)?.toLongOrNull() ?: 0L
        seen.clear()
        decode(s.get(K_SEEN)).forEach { (k, v) -> v.toLongOrNull()?.let { seen[k] = it } }
        stats.clear()
        decode(s.get(K_STATS)).forEach { (k, v) ->
            val p = v.split(',')
            if (p.size >= 4) stats[k] = Stat(p[0].toIntOrNull() ?: 0, p[1].toIntOrNull() ?: 0, p[2].toIntOrNull() ?: 0, p[3])
        }
    }

    fun lastSeen(baseId: String): Long? = seen[baseId]

    /** Die zuletzt gezogenen Aufgaben, neueste zuletzt. */
    fun recent(n: Int): List<String> = seen.keys.toList().takeLast(n)
    val now: Long get() = tick

    /** Aufgabe wurde gezogen. */
    fun markSeen(baseId: String) {
        tick++
        seen.remove(baseId)
        seen[baseId] = tick
        while (seen.size > MAX_SEEN) seen.remove(seen.keys.first())
        store.put(K_TICK, tick.toString())
        store.put(K_SEEN, encode(seen.mapValues { it.value.toString() }))
    }

    private fun key(subject: Subject, level: Difficulty, master: Boolean) = subject.id + ":" + level.id + if (master) "+m" else ""

    /** Ergebnis einer Aufgabe verbuchen. */
    fun record(subject: Subject, level: Difficulty, master: Boolean, solved: Boolean, clean: Boolean) {
        val s = stats.getOrPut(key(subject, level, master)) { Stat() }
        s.tried++
        if (solved) s.solved++
        if (clean) s.clean++
        s.recent = (s.recent + if (solved) "1" else "0").takeLast(10)
        store.put(K_STATS, encode(stats.mapValues { (_, v) -> "${v.clean},${v.solved},${v.tried},${v.recent}" }))
    }

    /**
     * Wie weit man auf dieser Stufe ist (0–3). Steigt mit sauber gelösten Aufgaben (ab 6, 16, 32),
     * sinkt eine Stufe, wenn von den letzten Aufgaben weniger als die Hälfte geklappt hat.
     */
    fun tier(subject: Subject, level: Difficulty, master: Boolean): Int {
        val s = stats[key(subject, level, master)] ?: return 0
        var t = when {
            s.clean >= 32 -> 3
            s.clean >= 16 -> 2
            s.clean >= 6 -> 1
            else -> 0
        }
        if (s.recent.length >= 4 && s.recent.count { it == '1' } * 2 < s.recent.length) t--
        return t.coerceIn(0, 3)
    }

    /** Nur für Tests. */
    fun reset() {
        tick = 0
        seen.clear()
        stats.clear()
    }
}
