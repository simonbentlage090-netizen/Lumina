package de.funke.app.ui.challenge

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

// =============================================================================================
// Vorlagen Analysis (seit 10.1). Angeregt von Silvanus P. Thompson, "Calculus Made Easy" (1910,
// gemeinfrei): Ableiten als "Wie schnell wächst etwas gerade?", Integrieren als "Aufsummieren".
// =============================================================================================

private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private object Bg6 {
    val derivative get() = l(
        "Die Ableitung beantwortet eine einfache Frage: Wie schnell ändert sich etwas gerade jetzt? Geometrisch ist sie die Steigung der Tangente. Für Potenzen gibt es eine Faustregel, die fast alles erledigt: Exponent nach vorn, Exponent eins kleiner. Aus x³ wird 3x². Thompson nannte das in 'Calculus Made Easy' den Trick, mit dem 'ein Narr kann, was ein anderer Narr kann'.",
        "The derivative answers a simple question: how fast is something changing right now? Geometrically it's the slope of the tangent. For powers there's one rule that does almost everything: bring the exponent down in front and reduce it by one. x³ becomes 3x². In 'Calculus Made Easy', Thompson called calculus the art where 'what one fool can do, another can'.",
    )
    val motion get() = l(
        "Bewegung ist das Paradebeispiel für Ableitungen: Die Ableitung des Ortes nach der Zeit ist die Geschwindigkeit, die Ableitung der Geschwindigkeit die Beschleunigung. Newton hat die Differentialrechnung genau dafür erfunden – um Planetenbahnen und fallende Äpfel mit derselben Mathematik zu beschreiben.",
        "Motion is the classic example of derivatives: the derivative of position with respect to time is velocity, the derivative of velocity is acceleration. Newton invented calculus precisely for this – to describe planetary orbits and falling apples with the same mathematics.",
    )
    val integral get() = l(
        "Integrieren ist Aufsummieren unendlich vieler, unendlich dünner Streifen. So bekommt man Flächen unter Kurven, aus Geschwindigkeiten zurückgelegte Wege oder aus Leistungen Energien. Der Hauptsatz der Analysis verbindet beides: Integrieren ist die Umkehrung des Ableitens. Für Potenzen: Exponent eins größer und dadurch teilen.",
        "Integration is adding up infinitely many infinitely thin strips. That's how you get areas under curves, distances from velocities or energies from powers. The fundamental theorem of calculus connects it all: integration undoes differentiation. For powers: raise the exponent by one and divide by it.",
    )
    val optimum get() = l(
        "Wo eine Größe am größten oder kleinsten ist, steht die Tangente waagrecht – die Ableitung ist null. Mit diesem einen Gedanken findet man die beste Form einer Dose, den kürzesten Weg oder den größten Gewinn. Ob es ein Maximum oder Minimum ist, verrät das Vorzeichen der zweiten Ableitung.",
        "Where a quantity is largest or smallest, the tangent is horizontal – the derivative is zero. With this single idea you can find the best shape for a can, the shortest path or the largest profit. Whether it's a maximum or a minimum is revealed by the sign of the second derivative.",
    )
    val growth get() = l(
        "Wächst etwas proportional zu sich selbst – Bakterien, Zinsen, Viren in der Anfangsphase –, entsteht exponentielles Wachstum: dN/dt = k·N, mit der Lösung N = N₀·e^(kt). Die Zahl e ist genau die Basis, bei der die Ableitung einer Funktion gleich der Funktion selbst ist. Typisch ist eine feste Verdopplungszeit ln 2 / k.",
        "When something grows in proportion to itself – bacteria, interest, viruses in the early phase – you get exponential growth: dN/dt = k·N, with the solution N = N₀·e^(kt). The number e is exactly the base for which a function's derivative equals the function itself. A fixed doubling time ln 2 / k is typical.",
    )
    val chain get() = l(
        "Die Kettenregel sagt, wie man verschachtelte Funktionen ableitet: äußere Ableitung mal innere Ableitung. Ändert sich x ein wenig, ändert sich erst der innere Ausdruck – und das wird von der äußeren Funktion noch einmal verstärkt. Wie Zahnräder, die ineinandergreifen: Die Übersetzungen multiplizieren sich.",
        "The chain rule tells you how to differentiate nested functions: outer derivative times inner derivative. When x changes a little, first the inner expression changes – and the outer function amplifies that once more. Like gears meshing together: the gear ratios multiply.",
    )
}

private object Fc6 {
    val power get() = card(l("Potenzregel", "Power rule"), """§\dfrac{d}{dx}\, x^n = n\, x^{n-1}§""", l("Konstante Faktoren bleiben stehen.", "Constant factors stay."))
    val integral get() = card(l("Stammfunktion", "Antiderivative"), """§\int x^n\, dx = \dfrac{x^{n+1}}{n+1} + C§""", l("Für n ≠ −1.", "For n ≠ −1."))
    val chain get() = card(l("Kettenregel", "Chain rule"), """§\dfrac{d}{dx}\, f(g(x)) = f'(g(x)) \cdot g'(x)§""", l("Äußere mal innere Ableitung.", "Outer times inner derivative."))
    val exp get() = card(l("Exponentielles Wachstum", "Exponential growth"), """§N(t) = N_0\, e^{k t},\quad T_2 = \dfrac{\ln 2}{k}§""", l("dN/dt = k · N.", "dN/dt = k · N."))
}

object LabMath {
    val all: List<TaskDef> get() = listOf(
        lab("steigung", Topic.Calculus, M, 0.3f) {
            val a = int(2, if (hard) 9 else 5).toDouble()
            val nn = int(2, if (hard) 5 else 3)
            val x0 = int(1, if (hard) 4 else 3).toDouble()
            val slope = a * nn * x0.pow(nn - 1)
            task(
                question = l("""Wie steil ist die Kurve §f(x) = ${m(a, 0)}\, x^{$nn}§ an der Stelle §x = ${m(x0, 0)}§?""", """How steep is the curve §f(x) = ${m(a, 0)}\, x^{$nn}§ at §x = ${m(x0, 0)}§?"""),
                steps = listOf(num(l("Steigung", "Slope"), slope, "", abs = 0.0, done = """§f'(${m(x0, 0)}) = ${m(slope, 0)}§""",
                    hints = listOf(l("Kernidee: Erst ableiten (Potenzregel), dann x einsetzen.", "Key idea: differentiate first (power rule), then plug in x."), """§f'(x) = ${m(a * nn, 0)}\, x^{${nn - 1}}§"""), formula = Fc6.power)),
                solution = listOf("""§f'(x) = ${m(a, 0)} \cdot $nn\, x^{${nn - 1}} = ${m(a * nn, 0)}\, x^{${nn - 1}}§""", """§f'(${m(x0, 0)}) = ${m(a * nn, 0)} \cdot ${m(x0, 0)}^{${nn - 1}} = ${m(slope, 0)}§"""),
                background = Bg6.derivative,
            )
        },
        lab("ortzeit", Topic.Calculus, M, 0.4f) {
            val a = tiered(listOf(2.0, 3.0, 5.0), 1.5, 6.0, 0.5)
            val b = if (hard) tiered(listOf(4.0), 1.0, 8.0, 1.0) else 0.0
            val t0 = tiered(listOf(2.0, 3.0, 4.0), 1.0, 6.0, 0.5)
            val v = 2 * a * t0 + b
            task(
                question = l("""Ein Wagen bewegt sich nach §s(t) = ${m(a)}\, t^2${if (b > 0) " + ${m(b, 0)}\\, t" else ""}§ (s in Metern, t in Sekunden). Wie schnell ist er bei §t = ${m(t0)}\ \text{s}§?""", """A cart moves according to §s(t) = ${m(a)}\, t^2${if (b > 0) " + ${m(b, 0)}\\, t" else ""}§ (s in metres, t in seconds). How fast is it at §t = ${m(t0)}\ \text{s}§?"""),
                steps = listOf(num(l("Geschwindigkeit", "Velocity"), v, "m/s", rel = 0.01, done = """§v = ${m(v)}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Geschwindigkeit ist die Ableitung des Ortes nach der Zeit.", "Key idea: velocity is the derivative of position with respect to time."), """§v(t) = s'(t) = ${m(2 * a)}\, t${if (b > 0) " + ${m(b, 0)}" else ""}§"""), formula = Fc6.power)),
                solution = listOf("""§v(t) = ${m(2 * a)}\, t${if (b > 0) " + ${m(b, 0)}" else ""}§""", """§v(${m(t0)}) = ${m(v)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = Bg6.motion,
            )
        },
        lab("flaeche", Topic.Calculus, H, 0.4f) {
            val a = int(1, if (hard) 6 else 3).toDouble()
            val nn = int(1, if (hard) 3 else 2)
            val b = int(2, if (hard) 5 else 3).toDouble()
            val area = a * b.pow(nn + 1) / (nn + 1)
            task(
                question = l("""Wie groß ist die Fläche unter §f(x) = ${m(a, 0)}\, x^{$nn}§ zwischen §x = 0§ und §x = ${m(b, 0)}§?""", """What is the area under §f(x) = ${m(a, 0)}\, x^{$nn}§ between §x = 0§ and §x = ${m(b, 0)}§?"""),
                steps = listOf(num(l("Fläche", "Area"), area, "", rel = 0.01, done = """§A = ${m(area, 2)}§""",
                    hints = listOf(l("Kernidee: Stammfunktion bilden, obere Grenze einsetzen (unten ist sie 0).", "Key idea: find the antiderivative and plug in the upper limit (it's 0 at the bottom)."), """§F(x) = ${m(a, 0)} \cdot \dfrac{x^{${nn + 1}}}{${nn + 1}}§"""), formula = Fc6.integral)),
                solution = listOf("""§A = \int_0^{${m(b, 0)}} ${m(a, 0)}\, x^{$nn}\, dx = ${m(a, 0)} \cdot \dfrac{${m(b, 0)}^{${nn + 1}}}{${nn + 1}} = ${m(area, 2)}§"""),
                background = Bg6.integral,
            )
        },
        lab("zaun", Topic.Calculus, H, 0.6f) {
            val len = tiered(listOf(40.0, 60.0, 100.0), 30.0, 200.0, 2.0)
            val wall = hard
            val x = if (wall) len / 4 else len / 4
            val area = if (wall) (len / 4) * (len / 2) else (len / 4) * (len / 4)
            task(
                question = if (wall) l("Du hast ${n(len, 0)} m Zaun für ein rechteckiges Gehege an einer Hauswand (die Wand braucht keinen Zaun). Wie groß kann die Fläche höchstens werden?", "You have ${n(len, 0)} m of fence for a rectangular pen against a house wall (the wall needs no fence). What is the largest possible area?")
                else l("Du hast ${n(len, 0)} m Zaun für ein rechteckiges Gehege. Wie groß kann die Fläche höchstens werden?", "You have ${n(len, 0)} m of fence for a rectangular pen. What is the largest possible area?"),
                steps = listOf(
                    num(l("Beste Breite (Seite senkrecht zur Wand bzw. eine Seite)", "Best width (side perpendicular to the wall, or one side)"), x, "m", rel = 0.01, done = """§x = ${m(x)}\ \text{m}§""",
                        hints = listOf(l("Kernidee: Fläche als Funktion einer Seite schreiben, ableiten, null setzen.", "Key idea: write the area as a function of one side, differentiate, set to zero."), if (wall) """§A(x) = x\,(${m(len, 0)} - 2x)§""" else """§A(x) = x\,(${m(len / 2, 0)} - x)§"""),
                        formula = card(l("Extremum", "Extremum"), """§A'(x) = 0§""", l("Waagrechte Tangente am Maximum.", "Horizontal tangent at the maximum."))),
                    num(l("Größte Fläche", "Largest area"), area, "m²", rel = 0.01, done = """§A_{max} = ${m(area)}\ \text{m}^2§""",
                        hints = listOf(l("Die beste Seite in A(x) einsetzen.", "Plug the best side into A(x)."), """§A(${m(x)})§""")),
                ),
                solution = listOfNotNull(
                    if (wall) """§A(x) = x\,(${m(len, 0)} - 2x),\ A'(x) = ${m(len, 0)} - 4x = 0 \Rightarrow x = ${m(x)}\ \text{m}§""" else """§A(x) = x\,(${m(len / 2, 0)} - x),\ A'(x) = ${m(len / 2, 0)} - 2x = 0 \Rightarrow x = ${m(x)}\ \text{m}§""",
                    """§A_{max} = ${m(area)}\ \text{m}^2§""",
                    if (!wall) l("Das beste Rechteck ist ein Quadrat.", "The best rectangle is a square.") else null,
                ),
                background = Bg6.optimum,
            )
        },
        lab("kettenregel", Topic.Calculus, H, 0.5f) {
            val a = int(2, if (hard) 5 else 3).toDouble()
            val b = int(1, if (hard) 7 else 4).toDouble()
            val nn = int(2, if (hard) 4 else 3)
            val x0 = int(0, 2).toDouble()
            val d = nn * (a * x0 + b).pow(nn - 1) * a
            task(
                question = l("""Leite §f(x) = (${m(a, 0)}x + ${m(b, 0)})^{$nn}§ ab und berechne §f'(${m(x0, 0)})§.""", """Differentiate §f(x) = (${m(a, 0)}x + ${m(b, 0)})^{$nn}§ and compute §f'(${m(x0, 0)})§."""),
                steps = listOf(num("""§f'(${m(x0, 0)})§""", d, "", abs = 0.0, done = """§f'(${m(x0, 0)}) = ${m(d, 0)}§""",
                    hints = listOf(l("Kernidee: Äußere Ableitung (Potenzregel) mal innere Ableitung (die Zahl vor x).", "Key idea: outer derivative (power rule) times inner derivative (the number in front of x)."), """§f'(x) = $nn\,(${m(a, 0)}x + ${m(b, 0)})^{${nn - 1}} \cdot ${m(a, 0)}§"""), formula = Fc6.chain)),
                solution = listOf("""§f'(x) = ${m(nn * a, 0)}\,(${m(a, 0)}x + ${m(b, 0)})^{${nn - 1}}§""", """§f'(${m(x0, 0)}) = ${m(nn * a, 0)} \cdot ${m(a * x0 + b, 0)}^{${nn - 1}} = ${m(d, 0)}§"""),
                background = Bg6.chain,
            )
        },
        lab("bakterien", Topic.Calculus, X, 0.5f) {
            val td = tiered(listOf(20.0, 30.0), 15.0, 45.0, 1.0) // min
            val hours = tiered(listOf(2.0, 3.0, 4.0), 1.5, 6.0, 0.5)
            val k = ln(2.0) / td
            val factor = exp(k * hours * 60)
            task(
                question = l("Eine Bakterienkultur verdoppelt sich alle ${n(td, 0)} Minuten. Um welchen Faktor wächst sie in ${n(hours)} Stunden, und wie groß ist die Wachstumsrate k?", "A bacterial culture doubles every ${n(td, 0)} minutes. By what factor does it grow in ${n(hours)} hours, and what is the growth rate k?"),
                steps = listOf(
                    num(l("Wachstumsrate k pro Minute", "Growth rate k per minute"), k, "1/min", rel = 0.02, done = """§k \approx ${m(k, 4)}\ \text{min}^{-1}§""",
                        hints = listOf(l("Kernidee: Aus der Verdopplungszeit: e^(k·T₂) = 2.", "Key idea: from the doubling time: e^(k·T₂) = 2."), """§k = \dfrac{\ln 2}{T_2}§"""), formula = Fc6.exp),
                    num(l("Wachstumsfaktor", "Growth factor"), factor, "", rel = 0.03, done = """§\dfrac{N}{N_0} \approx ${m(sig(factor))}§""",
                        hints = listOf(l("Zeit in Minuten, dann e hoch k·t – oder: 2 hoch Anzahl der Verdopplungen.", "Time in minutes, then e to the k·t – or: 2 to the number of doublings."), """§\dfrac{N}{N_0} = 2^{t/T_2}§""")),
                ),
                solution = listOf("""§k = \dfrac{0{,}693}{${m(td, 0)}\ \text{min}} \approx ${m(k, 4)}\ \text{min}^{-1}§""", """§\dfrac{N}{N_0} = 2^{${m(hours * 60, 0)}/${m(td, 0)}} \approx ${m(sig(factor))}§"""),
                background = Bg6.growth,
            )
        },
        lab("tangente", Topic.Calculus, X, 0.6f) {
            val a = int(1, if (hard) 4 else 2).toDouble()
            val b = int(-4, 4).toDouble()
            val x0 = int(1, 3).toDouble()
            val y0 = a * x0.pow(3) + b * x0
            val sl = 3 * a * x0 * x0 + b
            val icept = y0 - sl * x0
            task(
                question = l("""Die Tangente an §f(x) = ${m(a, 0)}x^3 ${if (b >= 0) "+" else "-"} ${m(kotlin.math.abs(b), 0)}x§ im Punkt §x = ${m(x0, 0)}§ schneidet die y-Achse wo?""", """Where does the tangent to §f(x) = ${m(a, 0)}x^3 ${if (b >= 0) "+" else "-"} ${m(kotlin.math.abs(b), 0)}x§ at §x = ${m(x0, 0)}§ cross the y-axis?"""),
                steps = listOf(
                    num(l("Steigung der Tangente", "Slope of the tangent"), sl, "", abs = 0.0, done = """§m = ${m(sl, 0)}§""",
                        hints = listOf(l("Kernidee: Die Tangente hat die Steigung f'(x₀).", "Key idea: the tangent has slope f'(x₀)."), """§f'(x) = ${m(3 * a, 0)}x^2 ${if (b >= 0) "+" else "-"} ${m(kotlin.math.abs(b), 0)}§"""), formula = Fc6.power),
                    num(l("y-Achsenabschnitt", "y-intercept"), icept, "", abs = 0.0, done = """§t = ${m(icept, 0)}§""",
                        hints = listOf(l("Die Tangente geht durch (x₀, f(x₀)): t = f(x₀) − m·x₀.", "The tangent passes through (x₀, f(x₀)): t = f(x₀) − m·x₀."), """§f(${m(x0, 0)}) = ${m(y0, 0)}§""")),
                ),
                solution = listOf("""§f'(${m(x0, 0)}) = ${m(sl, 0)},\quad f(${m(x0, 0)}) = ${m(y0, 0)}§""", """§t = ${m(y0, 0)} - ${m(sl, 0)} \cdot ${m(x0, 0)} = ${m(icept, 0)}§"""),
                background = Bg6.derivative,
            )
        },
    )
}
