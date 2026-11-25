package de.funke.app.ui.challenge

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

/*
 * Aufgaben-Vorlagen (seit 10.1). Eine Vorlage ist eine Aufgabe mit Zufallszahlen: Jedes Ziehen bringt
 * neue Werte, die Lösung wird im Code ausgerechnet. [Gen.tier] (0–3) steigt, je mehr man auf einer Stufe
 * sauber löst: größere und krummere Zahlen, mehr Größen auf einmal, manchmal ein Schritt mehr.
 *
 * Alle Vorlagen sind selbst geschrieben. Anregungen für Themen und Aufbau (nicht wörtlich):
 * OpenStax "University Physics" (CC BY 4.0), Halliday/Resnick "Fundamentals of Physics",
 * Susskind "The Theoretical Minimum", Trodden & Carroll "TASI Lectures on Cosmology" (astro-ph/0401547),
 * Thompson "Calculus Made Easy" (gemeinfrei).
 */

/** Vorlage: [gen] baut bei jedem Ziehen eine neue Aufgabe. [grade] = Schwierigkeit innerhalb der Stufe (0–1). */
class LabDef(
    id: String,
    topic: Topic,
    level: Difficulty,
    master: Boolean = false,
    private val gradeValue: Float = 0.5f,
    private val gen: Gen.() -> Task,
) : TaskDef(id, topic, level, master, { error("Vorlage") }) {
    override val parametric: Boolean get() = true
    override val grade: Float get() = gradeValue
    override fun make(seed: Int, tier: Int): Task {
        val t = Gen(this, Random(seed), tier.coerceIn(0, 3)).gen()
        return t.copy(id = "$id#$tier.$seed").normalized()
    }
}

/** Kurzform zum Anlegen einer Vorlage. */
internal fun lab(id: String, topic: Topic, level: Difficulty, grade: Float = 0.5f, master: Boolean = false, gen: Gen.() -> Task) =
    LabDef("lab.$id", topic, level, master, grade, gen)

/** Zufall, Steigerung und Zahlen-Schreibweise für eine Vorlage. */
class Gen internal constructor(private val def: TaskDef, val rnd: Random, val tier: Int) {
    /** Ab Stufe 2: größere Zahlen, mehr Größen. */
    val hard: Boolean get() = tier >= 2

    fun int(a: Int, b: Int): Int = rnd.nextInt(a, b + 1)
    fun <T> pick(vararg xs: T): T = xs[rnd.nextInt(xs.size)]
    fun <T> pick(xs: List<T>): T = xs[rnd.nextInt(xs.size)]
    fun chance(p: Double): Boolean = rnd.nextDouble() < p

    /** Zufallswert von [a] bis [b] in Schritten von [step]. */
    fun step(a: Double, b: Double, step: Double): Double {
        val n = ((b - a) / step).roundToInt().coerceAtLeast(0)
        return round(a + step * rnd.nextInt(n + 1), 6)
    }

    /** Wert passend zur Steigerung: [easy] bei tier 0–1, sonst krummer aus [a]..[b] (Schritt [s]). */
    fun tiered(easy: List<Double>, a: Double, b: Double, s: Double): Double = if (tier <= 1) pick(easy) else step(a, b, s)

    /** Auf [s] gültige Stellen runden. */
    fun sig(x: Double, s: Int = 3): Double {
        if (x == 0.0 || x.isNaN() || x.isInfinite()) return x
        val e = floor(log10(abs(x))).toInt()
        val f = 10.0.pow(s - 1 - e)
        return (x * f).roundToInt() / f
    }

    private fun round(x: Double, d: Int): Double {
        val f = 10.0.pow(d)
        return Math.round(x * f) / f
    }

    private val german: Boolean get() = de.funke.app.ui.Lang.german

    /** Zahl mit höchstens [d] Nachkommastellen, ohne Nullen am Ende. */
    private fun plain(x: Double, d: Int): String {
        val r = round(x, d)
        val s = if (abs(r - Math.round(r)) < 1e-9) Math.round(r).toString()
        else "%.${d}f".format(java.util.Locale.ROOT, r).trimEnd('0').trimEnd('.')
        return s
    }

    private fun groupInt(digits: String, sep: String): String {
        val neg = digits.startsWith("-")
        val d = digits.removePrefix("-")
        if (d.length <= 4) return digits
        val out = StringBuilder()
        d.reversed().forEachIndexed { i, c ->
            if (i > 0 && i % 3 == 0) out.append(sep.reversed())
            out.append(c)
        }
        return (if (neg) "-" else "") + out.reverse().toString()
    }

    private fun mantissa(x: Double, d: Int): Pair<Double, Int> {
        val e = floor(log10(abs(x))).toInt()
        var m = x / 10.0.pow(e)
        var ee = e
        if (abs(round(m, d)) >= 10.0) { m /= 10.0; ee++ }
        return m to ee
    }

    private fun sciNeeded(x: Double): Boolean = x != 0.0 && (abs(x) >= 1e6 || abs(x) < 1e-3)

    /** Zahl für Fließtext in der aktuellen Sprache ("12.500,5" bzw. "12,500.5"); sehr groß/klein als 3,0 · 10⁸. */
    fun n(x: Double, d: Int = 2): String {
        if (sciNeeded(x)) {
            val (mm, e) = mantissa(x, d)
            val ms = plain(mm, d).let { if (german) it.replace('.', ',') else it }
            return minus(ms) + " · 10" + sup(e)
        }
        val s = plain(x, d)
        val intPart = s.substringBefore('.')
        val frac = s.substringAfter('.', "")
        val g = groupInt(intPart, if (german) "." else ",")
        return minus(if (frac.isEmpty()) g else g + (if (german) "," else ".") + frac)
    }

    /** Im Fließtext ein echtes Minuszeichen statt des kurzen Bindestrichs. */
    private fun minus(s: String): String = if (s.startsWith("-")) "−" + s.drop(1) else s

    /** Ganze Zahl für Fließtext. */
    fun n(x: Int): String = n(x.toDouble(), 0)

    /** Zahl in einer Formel: Dezimalkomma als {,} (wird im Englischen zum Punkt), Tausender mit schmalem Abstand. */
    fun m(x: Double, d: Int = 2): String {
        if (sciNeeded(x)) return sci(x, d)
        val s = plain(x, d)
        val intPart = s.substringBefore('.')
        val frac = s.substringAfter('.', "")
        val g = groupInt(intPart, "\\,")
        return if (frac.isEmpty()) g else "$g{,}$frac"
    }

    fun m(x: Int): String = m(x.toDouble(), 0)

    /** Zahl in Zehnerpotenz-Schreibweise für Formeln: 3{,}0 \cdot 10^{8}. */
    fun sci(x: Double, d: Int = 2): String {
        if (x == 0.0) return "0"
        val (mm, e) = mantissa(x, d)
        val ms = plain(mm, d).replace(".", "{,}")
        return if (e == 0) ms else "$ms \\cdot 10^{$e}"
    }

    private fun sup(e: Int): String = e.toString().map {
        when (it) {
            '-' -> '⁻'; '0' -> '⁰'; '1' -> '¹'; '2' -> '²'; '3' -> '³'; '4' -> '⁴'
            '5' -> '⁵'; '6' -> '⁶'; '7' -> '⁷'; '8' -> '⁸'; else -> '⁹'
        }
    }.joinToString("")

    /** Fertige Aufgabe mit ID, Thema und Stufe der Vorlage. */
    fun task(
        question: String,
        steps: List<Step>,
        solution: List<String>,
        background: String,
        given: List<String> = emptyList(),
        fact: String? = null,
    ) = Task(
        id = def.id, topic = def.topic, level = def.level, question = question, steps = steps,
        solution = solution, background = background, given = given, fact = fact, master = def.master,
    )
}

/** Ausbau seit 13.2: neue Aufgaben je Fach. */
object NewTasks {
    val all: List<TaskDef> get() = MathNew1.all + MathNew2.all + MathNew3.all + MathNew4.all + MathNew5.all + PhysNew1.all + PhysNew2.all + PhysNew3.all + PhysNew4.all + PhysNew5.all + PhysNew6.all + PhysNew7.all + PhysNew8.all + WorldNew1.all + WorldNew2.all + WorldNew3.all + WorldNew4.all + WorldNew5.all
}

/** Alle Vorlagen. */
object Lab {
    val all: List<TaskDef> by lazy {
        LabMechanics.all + LabFields.all + LabMatter.all + LabModern.all + LabMath.all + LabMath2.all + LabPhilosophy.all + LabLiterature.all + LabWorld.all +
            NewTasks.all
    }
}

/** Hinweis für "Quellen & Lizenzen": OpenStax (CC BY 4.0). */
const val OPENSTAX_NOTE = """OpenStax University Physics, Volumes 1–3
by Samuel J. Ling, Jeff Sanny, William Moebs et al., OpenStax, Rice University (2016).
Licensed under the Creative Commons Attribution 4.0 International License (CC BY 4.0):
https://creativecommons.org/licenses/by/4.0/
Book: https://openstax.org/details/books/university-physics-volume-1

Lumina uses the books only as inspiration for topics and the structure of explanations.
All tasks, numbers and texts in Lumina are written independently; no text or figures were copied.
Changes: none of the original material is reproduced."""

/** Hinweis für "Quellen & Lizenzen": Calculus Made Easy (gemeinfrei). */
const val THOMPSON_NOTE = """Calculus Made Easy
by Silvanus P. Thompson (1910, 2nd edition 1914).
Public domain (Project Gutenberg eBook #33283).

The calculus tasks in Lumina follow the spirit of the book – derivatives as "how fast does it grow
right now?", integrals as "adding up" – but are written independently."""
