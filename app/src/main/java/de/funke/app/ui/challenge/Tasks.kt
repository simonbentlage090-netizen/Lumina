package de.funke.app.ui.challenge

import androidx.compose.runtime.Immutable
import de.funke.app.ui.tr
import kotlin.math.abs
import kotlin.random.Random

/*
 * Aufgaben 2.0 (seit 9.8): Eine Aufgabe besteht aus einem oder mehreren Schritten. Jeder Schritt hat
 * seine eigene Antwortart (Zahl mit Toleranz und Einheit, Auswahl, Schätzung), eine Tipp-Leiter
 * (Kernidee → Ansatz → Formelkarte) und ein kurzes Ergebnis für die eingeklappte Zeile.
 * Nach dem Lösen gibt es den Lösungsweg, eine Hintergrund-Erklärung und einen "Wusstest du?"-Fakt.
 *
 * Texte dürfen Formeln zwischen $…$ enthalten (kleine LaTeX-Teilmenge, siehe MathText).
 * Alle Aufgaben sind selbst geschrieben. Vorbild für Aufbau und Art der Fragen: Lehrbuch-Aufgaben in
 * mehreren Teilen mit "Kernidee" (wie in Halliday/Resnick), aber kein Inhalt daraus.
 */

/** Wie ein Schritt beantwortet wird. */
sealed interface Answer {
    /** Zahl. Richtig, wenn höchstens [rel] (relativ) bzw. [abs] (absolut) daneben. */
    data class Number(val value: Double, val unit: String = "", val rel: Double = 0.02, val abs: Double? = null) : Answer {
        fun accepts(x: Double): Boolean = if (abs != null) kotlin.math.abs(x - value) <= abs + 1e-9 else kotlin.math.abs(x - value) <= kotlin.math.abs(value) * rel + 1e-12
    }

    /** Eine richtige Antwort aus mehreren. */
    data class Choice(val options: List<String>, val correct: Int) : Answer

    /** Schätzung auf einer logarithmischen Skala von [min] bis [max]. Richtig innerhalb von Faktor [factor]. */
    data class Estimate(val value: Double, val min: Double, val max: Double, val kind: EstimateKind, val factor: Double = 2.0) : Answer {
        fun accepts(x: Double): Boolean = x / value in (1.0 / factor)..factor
    }
}

/** Wie eine Schätzung angezeigt wird. */
enum class EstimateKind { Duration, Length, Count, Mass }

/** Formelkarte für den letzten Tipp (für alle, die die Formel gerade nicht kennen). */
@Immutable
data class FormulaCard(val title: String, val formula: String, val text: String)

@Immutable
data class Step(
    val prompt: String,
    val answer: Answer,
    /** kurzes Ergebnis für die erledigte Zeile, z. B. "$v\approx 6{,}26\,\text{m/s}$" */
    val done: String,
    /** Tipp-Leiter: zuerst die Kernidee, dann der Ansatz */
    val hints: List<String>,
    val formula: FormulaCard? = null,
    /** Rückmeldung bei falscher Antwort (optional, sonst eine allgemeine) */
    val wrong: String? = null,
)

@Immutable
data class Task(
    val id: String,
    val topic: Topic,
    val level: Difficulty,
    val question: String,
    val steps: List<Step>,
    val solution: List<String>,
    /** Hintergrund: erklärt das Thema dahinter, nicht nur die Rechnung */
    val background: String,
    val given: List<String> = emptyList(),
    val fact: String? = null,
    /** Meisterklasse: Uni-Niveau ab Master, nur wenn eingeschaltet */
    val master: Boolean = false,
    val source: String? = null,
) {
    val subject: Subject get() = topic.subject
    val singleStep: Boolean get() = steps.size == 1
}

/**
 * Eine Aufgabe aus der Bank. [build] läuft erst beim Ziehen, damit die Sprache stimmt.
 * Vorlagen ([LabDef]) erzeugen bei jedem Ziehen neue Zahlen; ihre ID trägt dann "#stufe.seed",
 * damit sich dieselbe Aufgabe wieder genau so bauen lässt.
 */
open class TaskDef(val id: String, val topic: Topic, val level: Difficulty, val master: Boolean = false, private val build: () -> Task) {
    /** Erzeugt die Aufgabe mit Zufallszahlen (Vorlage) oder fest (geschriebene Aufgabe). */
    open val parametric: Boolean get() = false

    /** Schwierigkeit innerhalb der Stufe (0 = eher leicht, 1 = eher schwer), null = aus der Aufgabe geschätzt. */
    open val grade: Float? get() = null

    /** Baut die Aufgabe in der aktuellen Sprache. § wird zu $, im Englischen wird das Dezimalkomma {,} zum Punkt. */
    fun make(): Task = make(Random.nextInt(), 0)

    /** Wie [make], bei Vorlagen mit festem Zufall [seed] und Steigerung [tier] (0–3). */
    open fun make(seed: Int, tier: Int): Task = build().normalized()
}

/** Erste Hälfte einer Aufgaben-ID (ohne "#stufe.seed" einer Vorlage). */
fun baseId(id: String): String = id.substringBefore('#')

/**
 * Naturkonstanten, die eine Rechnung braucht, die man aber nicht einfach im Kopf hat (seit 13.1):
 * Steht eine davon in der Lösung oder den Tipps, aber nicht in der Frage oder den Angaben, kommt sie
 * automatisch zu den Angaben. So ist keine Aufgabe unlösbar, nur weil man eine Zahl nicht auswendig weiß.
 */
private class Const(val pattern: Regex, val de: String, val en: String = de)

private val U = """(?:\\[ ,;]|\s|\\text\{|\{|\})*"""
private val CONSTANTS = listOf(
    Const(Regex("34[03]$U" + "m/s"), "Schall in Luft: §c \\approx 343\\ \\text{m/s}§", "Sound in air: §c \\approx 343\\ \\text{m/s}§"),
    Const(Regex("1[45]00$U" + "m/s"), "Schall in Wasser: §c \\approx 1500\\ \\text{m/s}§", "Sound in water: §c \\approx 1500\\ \\text{m/s}§"),
    Const(Regex("""3\s*\\cdot\s*10\^\{?8|300[. ]?000\s*km"""), "Licht: §c = 3 \\cdot 10^8\\ \\text{m/s}§", "Light: §c = 3 \\cdot 10^8\\ \\text{m/s}§"),
    Const(Regex("""9(?:\{,\}|[,.])81"""), "§g = 9{,}81\\ \\text{m/s}^2§"),
    Const(Regex("""6(?:\{,\}|[,.])67\d*\s*\\cdot\s*10\^\{-11"""), "§G = 6{,}67 \\cdot 10^{-11}\\ \\tfrac{\\text{N m}^2}{\\text{kg}^2}§"),
    Const(Regex("""6(?:\{,\}|[,.])6[23]\d*\s*\\cdot\s*10\^\{-34"""), "§h = 6{,}63 \\cdot 10^{-34}\\ \\text{J s}§"),
    Const(Regex("""1(?:\{,\}|[,.])6\d*\s*\\cdot\s*10\^\{-19"""), "§e = 1{,}6 \\cdot 10^{-19}\\ \\text{C}§"),
    Const(Regex("""8(?:\{,\}|[,.])99\d*\s*\\cdot\s*10\^\{?9"""), "§k = 8{,}99 \\cdot 10^9\\ \\tfrac{\\text{N m}^2}{\\text{C}^2}§"),
    Const(Regex("""9(?:\{,\}|[,.])1[01]?\d*\s*\\cdot\s*10\^\{-31"""), "Elektron: §m_e = 9{,}11 \\cdot 10^{-31}\\ \\text{kg}§", "Electron: §m_e = 9{,}11 \\cdot 10^{-31}\\ \\text{kg}§"),
    Const(Regex("""4(?:\{,\}|[,.])1[89]$U""" + "kJ|4186|4190"), "Wasser: §c = 4{,}19\\ \\tfrac{\\text{kJ}}{\\text{kg K}}§", "Water: §c = 4{,}19\\ \\tfrac{\\text{kJ}}{\\text{kg K}}§"),
    Const(Regex("334$U" + "kJ"), "Schmelzwärme Eis: §334\\ \\text{kJ/kg}§", "Heat of fusion of ice: §334\\ \\text{kJ/kg}§"),
    Const(Regex("22[56]0$U" + "kJ"), "Verdampfungswärme Wasser: §2260\\ \\text{kJ/kg}§", "Heat of vaporisation of water: §2260\\ \\text{kJ/kg}§"),
)

internal fun Task.withConstants(): Task {
    if (subject != Subject.Physics && subject != Subject.Chemistry) return this
    val known = buildString {
        append(question); given.forEach { append(' ').append(it) }
        steps.forEach { st -> append(' ').append(st.prompt); st.formula?.let { append(' ').append(it.formula).append(' ').append(it.text) } }
    }
    val used = buildString {
        solution.forEach { append(' ').append(it) }
        steps.forEach { st -> append(' ').append(st.done); st.hints.forEach { append(' ').append(it) } }
    }
    val add = CONSTANTS.filter { it.pattern.containsMatchIn(used) && !it.pattern.containsMatchIn(known) }
    if (add.isEmpty()) return this
    return copy(given = given + add.map { if (de.funke.app.ui.Lang.german) it.de else it.en })
}

internal fun Task.normalized(): Task = withConstants().normalizedText()

private fun Task.normalizedText(): Task {
    val english = !de.funke.app.ui.Lang.german
    fun n(s: String): String = s.replace('§', '$').let { if (english) it.replace("{,}", ".") else it }
    fun n(a: Answer): Answer = if (a is Answer.Choice) a.copy(options = a.options.map(::n)) else a
    fun n(f: FormulaCard?) = f?.let { FormulaCard(n(it.title), n(it.formula), n(it.text)) }
    return copy(
        question = n(question),
        steps = steps.map { st -> st.copy(prompt = n(st.prompt), answer = n(st.answer), done = n(st.done), hints = st.hints.map(::n), formula = n(st.formula), wrong = st.wrong?.let(::n)) },
        solution = solution.map(::n),
        background = n(background),
        given = given.map(::n),
        fact = fact?.let(::n),
    )
}

/** Wofür eine Aufgabe gezogen wird. */
enum class TaskUse {
    /** Aufgaben-Reiter: alles erlaubt */
    Practice,

    /** Hürde beim Abbrechen: nur einschrittige Aufgaben */
    Cancel,
}

object Tasks {
    private val all: List<TaskDef> by lazy { TaskBank.all + TaskBankWorld.all + TaskBankMore.all + Lab.all }
    private val allById: Map<String, TaskDef> by lazy { all.associateBy { it.id } }
    private val byBase: Map<String, TaskDef> by lazy { all.associateBy { it.id } }

    private var lastTopic: Topic? = null

    /** Gelöste Aufgaben (aus den Einstellungen geladen). */
    private val solved = HashSet<String>()
    private val single = HashMap<String, Boolean>()
    private val kinds = HashMap<String, TaskKind>()
    private val grades = HashMap<String, Float>()

    /**
     * Wiederkehrende Motive (Wasserkocher, Blitz …): Verschiedene Aufgaben mit demselben Gegenstand fühlen
     * sich wie dieselbe Aufgabe an. Ein Motiv, das unter den letzten [MOTIF_MEMORY] Aufgaben schon dran war,
     * wird deutlich seltener gezogen.
     */
    private val MOTIFS = listOf(
        "kettle" to Regex("Wasserkocher|kettle", RegexOption.IGNORE_CASE),
        "storm" to Regex("Blitz|Gewitter|Donner|lightning|thunder", RegexOption.IGNORE_CASE),
        "echo" to Regex("Echo|Felswand", RegexOption.IGNORE_CASE),
        "rocket" to Regex("Rakete|rocket", RegexOption.IGNORE_CASE),
        "coaster" to Regex("Achterbahn|roller coaster", RegexOption.IGNORE_CASE),
        "elevator" to Regex("Aufzug|elevator", RegexOption.IGNORE_CASE),
        "pendulum" to Regex("Pendel|pendulum", RegexOption.IGNORE_CASE),
        "pizza" to Regex("Pizza", RegexOption.IGNORE_CASE),
        "phone" to Regex("Handy|Akku|smartphone|battery", RegexOption.IGNORE_CASE),
        "bike" to Regex("Fahrrad|bicycle|bike", RegexOption.IGNORE_CASE),
    )
    private const val MOTIF_MEMORY = 12
    private val motifCache = HashMap<String, String>()

    private fun motifOf(d: TaskDef): String = motifCache.getOrPut(d.id) {
        val q = runCatching { d.sample().question }.getOrDefault("")
        MOTIFS.firstOrNull { it.second.containsMatchIn(q) }?.first ?: ""
    }

    /** Motive der zuletzt gezogenen Aufgaben (aus dem gespeicherten Verlauf, also auch nach einem Neustart). */
    private fun recentMotifs(): Set<String> {
        return TaskHistory.recent(MOTIF_MEMORY).mapNotNull { allById[it] }.map(::motifOf).filter { it.isNotEmpty() }.toSet()
    }

    val count: Int get() = all.size

    fun byId(id: String): Task? {
        val def = byBase[baseId(id)] ?: return null
        val tail = id.substringAfter('#', "")
        if (tail.isEmpty() || !def.parametric) return def.make()
        val tier = tail.substringBefore('.').toIntOrNull() ?: 0
        val seed = tail.substringAfter('.').toIntOrNull() ?: 0
        return def.make(seed, tier)
    }

    /** Alle Aufgaben und Vorlagen (für Tests und die Übersicht). */
    fun allDefs(): List<TaskDef> = all

    fun restoreSolved(ids: Collection<String>) {
        solved.clear()
        solved += ids.map(::baseId)
    }

    fun markSolved(id: String) {
        if (!id.startsWith("gen:")) solved += baseId(id)
    }

    fun isSolved(id: String): Boolean = baseId(id) in solved

    private fun TaskDef.sample(): Task = make(7, 0)
    private fun TaskDef.isSingle(): Boolean = single.getOrPut(id) { sample().singleStep }
    fun kindOf(d: TaskDef): TaskKind = kinds.getOrPut(d.id) { d.sample().kind }

    /** Schwierigkeit innerhalb der Stufe: aus der Vorlage oder geschätzt (mehr Schritte = schwerer, Auswahl = leichter). */
    fun gradeOf(d: TaskDef): Float = d.grade ?: grades.getOrPut(d.id) {
        val t = d.sample()
        val base = 0.35f + 0.2f * (t.steps.size - 1)
        val adj = when (t.kind) {
            TaskKind.Concept -> -0.12f
            TaskKind.Estimate -> -0.05f
            else -> 0f
        }
        (base + adj).coerceIn(0.05f, 0.95f)
    }

    private fun fitsLevel(d: TaskDef, level: Difficulty, master: Boolean): Boolean =
        if (master && level == Difficulty.Expert) d.master || d.level == Difficulty.Expert
        else !d.master && d.level == level

    /** Aufgaben und Vorlagen zu diesen Themen auf dieser Stufe: (alle, davon gelöst). */
    fun progress(topics: Set<Topic>, level: Difficulty, master: Boolean = false): Pair<Int, Int> {
        val defs = all.filter { it.topic in topics && fitsLevel(it, level, master) }
        return defs.size to defs.count { it.id in solved }
    }

    /**
     * Nächste Aufgabe – der Kern des Aufgaben-Reiters. Jede Kandidatin bekommt ein Gewicht, dann wird
     * gewürfelt (keine feste Reihenfolge, jede Lektion ist anders):
     *
     * - Themen nach den Vorlieben ([TaskPrefs]): "Nur" schließt den Rest aus, "Mehr" zählt vierfach,
     *   "Aus" fällt weg. Themen mit vielen Vorlagen werden gedämpft, damit kleine Themen auch drankommen.
     * - Aufgabenarten nach den Vorlieben (mag ich: ×2,5, mag ich nicht: ×0,25).
     * - Neues zuerst: nie gesehene Aufgaben ×3, kürzlich gesehene fast gar nicht, danach erholt sich das
     *   Gewicht langsam; Meister-Aufgaben zählen auf der Meister-Stufe zweieinhalbfach ([TaskHistory] bleibt über Neustarts erhalten). Geschriebene, schon gelöste Aufgaben
     *   etwas seltener; Vorlagen bringen ohnehin neue Zahlen.
     * - Abwechslung: nicht zweimal hintereinander dasselbe Thema.
     * - Leichte Steigerung in der Lektion: Aufgabe [slot] von [of] bevorzugt anfangs leichtere, am Ende
     *   schwerere Aufgaben der Stufe – nur ein sanfter Zug, kein festes Raster.
     * - [tier] (0–3) macht Vorlagen mit der Zeit anspruchsvoller (größere Zahlen, mehr Größen).
     * - Aufgaben-Generatoren (alte Zahlen-Aufgaben) nur noch, wo es wenig Geschriebenes gibt.
     * - Gibt es auf der Stufe nichts, kommt die nächstgelegene Stufe derselben Themen.
     * - [stretch] (Wissensfächer in einer Runde): Gehen auf der Stufe die frischen Aufgaben aus, kommen frische
     *   von den beiden Nachbarstufen dazu, statt dass sich dieselben Fragen nach kurzer Zeit wiederholen.
     */
    fun next(
        topics: Set<Topic>,
        level: Difficulty,
        use: TaskUse,
        master: Boolean = false,
        random: Random = Random.Default,
        avoid: String? = null,
        exclude: Set<String> = emptySet(),
        slot: Int = -1,
        of: Int = 0,
        tier: Int? = null,
        stretch: Boolean = false,
    ): Task {
        val chosen = topics.ifEmpty { Topic.Default }
        val topicWeights: Map<Topic, Double> = if (use == TaskUse.Practice) TaskPrefs.weightsBySubject(chosen) else chosen.associateWith { 1.0 }
        val avoidBase = avoid?.let(::baseId)
        val excluded = exclude.map(::baseId).toSet()
        fun usable(d: TaskDef) = use == TaskUse.Practice || d.isSingle()
        fun fresh(d: TaskDef) = d.id != avoidBase && d.id !in excluded
        var pool = all.filter { it.topic in topicWeights && fresh(it) && usable(it) && fitsLevel(it, level, master) }
        val generatorTopics = topicWeights.keys.filter { it in Challenges.generatedTopics }.toSet()
        var share = when {
            level == Difficulty.Expert -> 0.0
            pool.size >= 10 -> 0.0
            use == TaskUse.Cancel -> if (level == Difficulty.Easy) 0.5 else 0.35
            level == Difficulty.Easy -> 0.45
            level == Difficulty.Medium -> 0.25
            else -> 0.15
        }
        if (share > 0 && pool.isNotEmpty() && pool.all { TaskHistory.lastSeen(it.id) != null }) share = (share + 0.2).coerceAtMost(0.6)
        val useGenerator = generatorTopics.isNotEmpty() && level != Difficulty.Expert && (pool.isEmpty() || random.nextDouble() < share)
        if (useGenerator) return generated(level, generatorTopics, random)

        if (stretch && !master && pool.count { isFresh(it) } < STRETCH_BELOW) {
            // Auf dieser Stufe ist fast alles vor Kurzem dran gewesen: frische Aufgaben der Nachbarstufen dazunehmen
            pool = pool + all.filter {
                it.topic in topicWeights && !it.master && fresh(it) && usable(it) && isFresh(it) &&
                    kotlin.math.abs(it.level.ordinal - level.ordinal) == 1
            }
        }
        if (pool.isEmpty()) {
            // Für diese Themen gibt es auf der Stufe nichts: nächstgelegene Stufe derselben Themen
            pool = all.filter { it.topic in topicWeights && !it.master && fresh(it) && usable(it) }
                .groupBy { kotlin.math.abs(it.level.ordinal - level.ordinal) * 2 + (if (it.level < level) 0 else 1) }
                .toSortedMap().values.firstOrNull().orEmpty()
        }
        if (pool.isEmpty() && excluded.isNotEmpty()) {
            // Alles schon in dieser Lektion dran gewesen: dann lieber eine Wiederholung aus denselben Themen
            pool = all.filter { it.topic in topicWeights && it.id != avoidBase && usable(it) && fitsLevel(it, level, master) }
                .ifEmpty { all.filter { it.topic in topicWeights && !it.master && it.id != avoidBase && usable(it) } }
        }
        if (pool.isEmpty()) {
            return if (generatorTopics.isNotEmpty()) generated(level, generatorTopics, random)
            else fromChallenge(Challenges.next(ChallengeSettings(level, Topic.Default), random))
        }
        val pick = choose(pool, topicWeights, random, slot, of, master)
        TaskHistory.markSeen(pick.id)
        lastTopic = pick.topic
        val t = tier ?: TaskHistory.tier(pick.topic.subject, level, master)
        return pick.make(random.nextInt(), t)
    }

    /** Noch nie gezogen oder so lange her, dass sich die Aufgabe wieder neu anfühlt. */
    private fun isFresh(d: TaskDef): Boolean = TaskHistory.lastSeen(d.id).let { it == null || TaskHistory.now - it > STRETCH_AFTER }

    /** Weniger frische Aufgaben als das auf der Stufe: Nachbarstufen dazunehmen. */
    private const val STRETCH_BELOW = 6

    /** Nach so vielen anderen Aufgaben gilt eine Aufgabe wieder als frisch. */
    private const val STRETCH_AFTER = 60L

    /** Gewichtet würfeln (siehe [next]). */
    private fun choose(pool: List<TaskDef>, topicWeights: Map<Topic, Double>, random: Random, slot: Int, of: Int, master: Boolean): TaskDef {
        val perTopic = pool.groupingBy { it.topic }.eachCount()
        val now = TaskHistory.now
        val motifs = recentMotifs()
        val target = if (of > 1 && slot >= 0) 0.2f + 0.6f * slot / (of - 1f) else -1f
        val weights = pool.map { d ->
            var w = (topicWeights[d.topic] ?: 1.0) / kotlin.math.sqrt(perTopic[d.topic]!!.toDouble())
            w *= TaskPrefs.kindWeight(kindOf(d))
            // Neues bevorzugen; Gesehenes erholt sich langsam (nach 8 Aufgaben ~8 %, nach 25 ~40 %, nach 50 ~75 %)
            val last = TaskHistory.lastSeen(d.id)
            w *= if (last == null) 3.0 else (1.0 - kotlin.math.exp(-(now - last) / 25.0)).let { 0.01 + it * it }
            if (master && d.master) w *= 2.5
            if (!d.parametric && d.id in solved) w *= 0.6
            if (d.topic == lastTopic && perTopic.size > 1) w *= 0.35
            if (motifs.isNotEmpty()) motifOf(d).let { if (it.isNotEmpty() && it in motifs) w *= 0.1 }
            if (target >= 0f) {
                val g = gradeOf(d)
                val z = (g - target) / 0.32f
                w *= 0.35 + kotlin.math.exp(-(z * z).toDouble())
            }
            w.coerceAtLeast(1e-6)
        }
        var r = random.nextDouble() * weights.sum()
        for (i in pool.indices) {
            r -= weights[i]
            if (r <= 0) return pool[i]
        }
        return pool.last()
    }

    private fun generated(level: Difficulty, topics: Set<Topic>, random: Random): Task {
        // Auch bei erzeugten Aufgaben das Thema wechseln
        val pickFrom = if (topics.size > 1) topics.filter { it != lastTopic }.toSet() else topics
        val c = Challenges.next(ChallengeSettings(level, pickFrom.ifEmpty { topics }), random)
        lastTopic = c.topic
        return fromChallenge(c)
    }

    /** Wandelt eine erzeugte Aufgabe (eine Zahl oder Auswahl) in eine einschrittige Aufgabe um. */
    fun fromChallenge(c: Challenge): Task {
        val concept = Concepts.forChallenge(c).firstOrNull()
        val answer: Answer = if (c.isChoice) Answer.Choice(c.options.orEmpty(), c.answer) else Answer.Number(c.answer.toDouble(), c.unit, abs = 0.0)
        val hints = buildList {
            c.steps.firstOrNull()?.let { add(it) }
            if (c.steps.size > 2) add(c.steps[c.steps.size / 2])
        }
        return Task(
            id = "gen:" + (c.templateId ?: c.question.hashCode().toString()),
            topic = c.topic,
            level = c.difficulty,
            question = c.question,
            steps = listOf(
                Step(
                    prompt = if (c.isChoice) tr("Welche Antwort stimmt?", "Which answer is right?") else tr("Deine Antwort", "Your answer"),
                    answer = answer,
                    done = c.answerText,
                    hints = hints,
                    formula = concept?.let { FormulaCard(it.title, it.formula, it.text) },
                )
            ),
            solution = c.steps,
            background = concept?.text.orEmpty(),
            source = c.source,
        )
    }

    /** Eingabe "1,5" oder "-0.2" als Zahl. */
    fun parse(input: String): Double? = input.trim().replace(',', '.').replace('−', '-').takeIf { it.isNotEmpty() && it != "-" }?.toDoubleOrNull()

    /** Kleine Hilfe für Aufgaben: Zahl in deutscher bzw. englischer Schreibweise. */
    fun fmt(x: Double, digits: Int = 2): String {
        val s = if (abs(x - Math.round(x)) < 1e-9) Math.round(x).toString()
        else "%.${digits}f".format(java.util.Locale.ROOT, x).trimEnd('0').trimEnd('.')
        return if (de.funke.app.ui.Lang.german) s.replace('.', ',') else s
    }
}
