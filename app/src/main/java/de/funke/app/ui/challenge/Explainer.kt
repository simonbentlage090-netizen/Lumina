package de.funke.app.ui.challenge

import de.funke.app.ui.Lang

/*
 * Logik hinter den Erklärungen (seit 13.3, ohne Oberfläche, damit sie sich testen lässt):
 * Lösungsweg auf die Schritte verteilen, "Kernidee:" abschneiden, Formel-Tipps erkennen und
 * die Rechenzeichen einer Aufgabe in einfachen Sätzen erklären. Die Ansicht steht in ui/screens/Explain.kt.
 */

/** Lösungsweg-Zeilen auf die Schritte verteilen: am Ergebnis jedes Schritts erkennt man, wo er endet. */
internal fun solutionByStep(task: Task): List<List<String>> {
    val n = task.steps.size
    val sol = task.solution
    if (n <= 1) return listOf(sol)
    if (sol.size == n) return sol.map { listOf(it) }
    val keys = task.steps.map { resultKey(it.done) }
    val out = List(n) { mutableListOf<String>() }
    // Jede Zeile gehört zu dem (nächsten) Schritt, dessen Ergebnis sie enthält. Zeilen ohne Ergebnis sind
    // Zwischenrechnungen und gehören zum Schritt, der als Nächstes dran ist.
    var cur = 0
    for (line in sol) {
        val hit = (cur until n).firstOrNull { k -> keys[k]?.let { line.contains(it) } == true }
        if (hit != null) {
            out[hit] += line
            cur = hit + 1
        } else out[minOf(cur, n - 1)] += line
    }
    return out
}

/** Die Zahl, mit der ein Schritt endet ("t ≈ 8{,}57 s" → "8{,}57"), oder null. */
private fun resultKey(done: String): String? {
    val tail = done.substringAfterLast("\\approx").substringAfterLast("=")
    val m = Regex("""-?\d+(?:\{,\}\d+|[.,]\d+)?""").find(tail) ?: return null
    return m.value.takeIf { it.count(Char::isDigit) >= 2 }
}

private val Prefixes = listOf("Kernidee:", "Key idea:", "Ansatz:", "Approach:", "Tipp:", "Hint:")

/** "Kernidee: …" → "…" (die Karte schreibt "Die Idee" schon darüber). */
internal fun stripLabel(s: String): String {
    val t = s.trim()
    val p = Prefixes.firstOrNull { t.startsWith(it) } ?: return t
    return t.removePrefix(p).trim().replaceFirstChar { it.uppercase() }
}

/** Besteht ein Tipp nur aus einer Formel ($…$)? */
internal fun isFormulaOnly(s: String): Boolean {
    val t = s.trim()
    return t.length > 2 && t.startsWith("$") && t.endsWith("$") && t.count { it == '$' } == 2
}

// ---------------------------------------------------------------------------------------------
// Zeichen kurz erklärt
// ---------------------------------------------------------------------------------------------

/** Ein Rechenzeichen: woran man es erkennt, wie es aussieht, was es bedeutet. */
private class Sign(val find: Regex, val show: String, val de: String, val en: String)

/*
 * § steht hier für den Formel-Rand ($). In Kotlin-Texten wäre $ sonst ein Platzhalter,
 * und im Aufgabentext darf es nie roh stehen (siehe Fehlerliste).
 */
private val Signs = listOf(
    Sign(Regex("""\\[dt]?frac"""), """§\dfrac{a}{b}§""", "Bruch: a geteilt durch b.", "Fraction: a divided by b."),
    Sign(Regex("""\\cdot"""), """§a \cdot b§""", "Mal: a malgenommen mit b.", "Times: a multiplied by b."),
    Sign(Regex("""\^\{?2(?![0-9])"""), """§x^2§""", "Zum Quadrat: x mal x.", "Squared: x times x."),
    Sign(Regex("""\^\{?3(?![0-9])"""), """§x^3§""", "Hoch drei: x mal x mal x.", "Cubed: x times x times x."),
    Sign(Regex("""\\sqrt|√"""), """§\sqrt{x}§""", "Wurzel: die Zahl, die mit sich selbst malgenommen x ergibt. Die Wurzel aus 9 ist 3.", "Square root: the number that gives x when multiplied by itself. The root of 9 is 3."),
    Sign(Regex("""10\^\{?-?\d"""), """§10^{n}§""", "Zehnerpotenz: eine 1 mit n Nullen. Mit Minus davor so oft durch 10 geteilt: 10⁻³ = 0,001.", "Power of ten: a 1 followed by n zeros. With a minus, divided by 10 that many times: 10⁻³ = 0.001."),
    Sign(Regex("""\\approx"""), """§\approx§""", "Ungefähr gleich: das Ergebnis ist gerundet.", "Approximately equal: the result is rounded."),
    Sign(Regex("""\\Rightarrow"""), """§\Rightarrow§""", "Daraus folgt: Die Gleichung links wurde so umgestellt, dass die gesuchte Größe allein steht.", "It follows: the equation on the left was rearranged so the unknown stands alone."),
    Sign(Regex("""\\Delta"""), """§\Delta x§""", "Delta: der Unterschied, also Endwert minus Anfangswert.", "Delta: the difference, i.e. final value minus starting value."),
    Sign(Regex("""\\sin(?!h)"""), """§\sin\alpha§""", "Sinus: Im rechtwinkligen Dreieck ist das die Seite gegenüber dem Winkel geteilt durch die längste Seite.", "Sine: in a right triangle, the side opposite the angle divided by the longest side."),
    Sign(Regex("""\\cos(?!h)"""), """§\cos\alpha§""", "Kosinus: die Seite am Winkel geteilt durch die längste Seite des rechtwinkligen Dreiecks.", "Cosine: the side next to the angle divided by the longest side of the right triangle."),
    Sign(Regex("""\\tan(?!h)"""), """§\tan\alpha§""", "Tangens: gegenüberliegende Seite geteilt durch anliegende Seite. Sagt, wie steil etwas ist.", "Tangent: opposite side divided by adjacent side. Tells you how steep something is."),
    Sign(Regex("""\\arc(sin|cos|tan)|\^\{-1\}"""), """§\arcsin§""", "Umkehrung: Man kennt den Sinus-Wert (o. Ä.) und sucht den Winkel dazu. Am Taschenrechner die Taste sin⁻¹.", "Inverse: you know the sine value (etc.) and want the angle. On a calculator it's the sin⁻¹ key."),
    Sign(Regex("""[a-zA-Z]'\s*\("""), """§f'(x)§""", "Ableitung: wie schnell sich f an dieser Stelle gerade ändert, also die Steigung.", "Derivative: how fast f is changing at that point, i.e. the slope."),
    Sign(Regex("""\\int"""), """§\int§""", "Integral: viele kleine Stücke zusammengezählt, die Fläche unter einer Kurve.", "Integral: many small pieces added up, the area under a curve."),
    Sign(Regex("""\\sum"""), """§\sum§""", "Summe: alle Glieder nacheinander zusammengezählt.", "Sum: all terms added up one after another."),
    Sign(Regex("""\\ln"""), """§\ln x§""", "Natürlicher Logarithmus: die Hochzahl, mit der e ≈ 2,72 genau x ergibt. Macht e hoch … rückgängig.", "Natural logarithm: the power that turns e ≈ 2.72 into x. It undoes e to the power of …"),
    Sign(Regex("""\\log"""), """§\log x§""", "Logarithmus: fragt nach der Hochzahl. log₁₀ 1000 = 3, weil 10³ = 1000.", "Logarithm: asks for the power. log₁₀ 1000 = 3 because 10³ = 1000."),
    Sign(Regex("""(?<![a-zA-Z\\])e\^"""), """§e^{x}§""", "e ≈ 2,718 hoch x: die Zahl, mit der Wachstum und Zerfall in der Natur laufen.", "e ≈ 2.718 to the power of x: the number behind natural growth and decay."),
    Sign(Regex("""\\pi"""), """§\pi§""", "Pi ≈ 3,14: wie oft der Durchmesser in den Umfang eines Kreises passt.", "Pi ≈ 3.14: how many times the diameter fits around a circle."),
    Sign(Regex("""9(\{,\}|[.,])81"""), """§g \approx 9{,}81\ \text{m/s}^2§""", "Erdbeschleunigung: Ein fallender Gegenstand wird jede Sekunde um gut 9,8 m/s schneller.", "Gravitational acceleration: a falling object gets almost 9.8 m/s faster every second."),
    Sign(Regex("""\\binom"""), """§\binom{n}{k}§""", "n über k: wie viele Möglichkeiten es gibt, k Dinge aus n auszuwählen.", "n choose k: how many ways there are to pick k things out of n."),
    Sign(Regex("""\\pm"""), """§\pm§""", "Plus oder minus: Es gibt zwei Lösungen.", "Plus or minus: there are two solutions."),
    Sign(Regex("""\\propto"""), """§\propto§""", "Proportional: wächst im selben Verhältnis mit.", "Proportional: grows in the same ratio."),
    Sign(Regex("""\\infty"""), """§\infty§""", "Unendlich: größer als jede Zahl.", "Infinity: bigger than any number."),
    Sign(Regex("""\\lim"""), """§\lim§""", "Grenzwert: der Wert, dem sich etwas immer weiter annähert.", "Limit: the value something gets closer and closer to."),
    Sign(Regex("""\\vec"""), """§\vec{v}§""", "Pfeil darüber: eine Größe mit Richtung (Vektor), etwa eine Geschwindigkeit nach Norden.", "Arrow on top: a quantity with a direction (vector), such as a velocity heading north."),
    Sign(Regex("""\\hbar"""), """§\hbar§""", "h-quer: das Plancksche Wirkungsquantum geteilt durch 2π, die Grundeinheit der Quantenwelt.", "h-bar: Planck's constant divided by 2π, the basic unit of the quantum world."),
    Sign(Regex("""\\le(?!ft)|\\ge(?!q)"""), """§\le\ \ \ge§""", "Kleiner gleich, größer gleich: höchstens bzw. mindestens.", "Less or equal, greater or equal: at most and at least."),
    Sign(Regex("""[a-zA-Z]_\{?[0-9a-zA-Z]"""), """§v_0§""", "Kleines Zeichen unten: sagt nur, welche Größe gemeint ist. Eine 0 heißt meist „am Anfang“.", "Small sign below: only says which quantity is meant. A 0 usually means “at the start”."),
)

/** Welche Zeichen in diesen Texten vorkommen, in der Reihenfolge der Liste (einfache zuerst). */
internal fun signsIn(texts: List<String>, max: Int = 10): List<Pair<String, String>> {
    val all = texts.joinToString(" ")
    if (!all.contains('$') && !all.contains('√')) return emptyList()
    val english = !Lang.german
    fun n(s: String) = s.replace('§', '$').let { if (english) it.replace("{,}", ".") else it }
    return Signs.filter { it.find.containsMatchIn(all) }.take(max).map { n(it.show) to (if (english) it.en else it.de) }
}

