package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

/* Mathe-Ausbau (seit 13.2): Gleichungen, Zahlenfolgen. Alle Texte selbst geschrieben. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object MathNew2 {
    val all: List<TaskDef> get() = equations + sequences

    private val equations = listOf(
        lab("ma.exponent", Topic.Equations, E, 0.3f) {
            val b = pick(2, 3, 5); val x = int(2, if (b == 2) 7 else 4); val r = b.toDouble().pow(x).toInt()
            task(
                question = l("""Löse nach x auf: §${b}^x = ${r}§""", """Solve for x: §${b}^x = ${r}§"""),
                steps = listOf(num("x", x.toDouble(), "", abs = 0.0, done = """§x = ${x}§""",
                    hints = listOf(l("Kernidee: Wie oft muss man ${b} mit sich selbst malnehmen, um ${r} zu erhalten?", "Key idea: how many times do you multiply ${b} by itself to get ${r}?"), """§${b} \cdot ${b} = ${b * b},\ \dots§"""))),
                solution = listOf("""§${b}^{${x}} = ${r} \Rightarrow x = ${x}§"""),
                background = l("Bei einer Exponentialgleichung steckt die Unbekannte im Exponenten. Ist das Ergebnis eine glatte Potenz der Basis, sieht man die Lösung durch Probieren. Sonst braucht man den Logarithmus, der genau diese Frage beantwortet: Mit welcher Hochzahl erreicht man eine Zahl?", "In an exponential equation the unknown sits in the exponent. If the result is an exact power of the base, you can find the solution by trying. Otherwise you need the logarithm, which answers exactly this question: which exponent gets you to a given number?"),
            )
        },
        lab("ma.ungleichung", Topic.Equations, E, 0.5f) {
            val a = int(3, 6); val b = int(1, a - 1); val c = int(2, 9); val d = int(3, 15)
            val bound = (d + c).toDouble() / (a - b); val smallest = kotlin.math.floor(bound).toInt() + 1
            task(
                question = l("""Welches ist die kleinste ganze Zahl x mit §${a}x - ${c} > ${b}x + ${d}§?""", """What is the smallest whole number x with §${a}x - ${c} > ${b}x + ${d}§?"""),
                steps = listOf(num("x", smallest.toDouble(), "", abs = 0.0, done = """§x = ${smallest}§""",
                    hints = listOf(l("Kernidee: Ungleichungen löst man wie Gleichungen. Bringe alle x auf eine Seite.", "Key idea: inequalities are solved like equations. Bring all the x terms to one side."), """§${a - b}x > ${c + d}§"""))),
                solution = listOf("""§${a - b}x > ${c + d} \Rightarrow x > ${m(bound, 2)}§""", l("Die kleinste ganze Zahl darüber ist ${smallest}.", "The smallest whole number above that is ${smallest}.")),
                background = l("Ungleichungen behandelt man fast wie Gleichungen: Auf beiden Seiten dasselbe addieren oder durch dieselbe positive Zahl teilen ist erlaubt. Nur beim Malnehmen oder Teilen mit einer negativen Zahl dreht sich das Zeichen um. Die Lösung ist keine einzelne Zahl, sondern ein ganzer Bereich.", "Inequalities are handled almost like equations: adding the same thing to both sides or dividing by the same positive number is allowed. Only when multiplying or dividing by a negative number does the sign flip. The solution isn't a single number but a whole range."),
            )
        },
        lab("ma.schnittpunkt", Topic.Equations, E, 0.5f) {
            val x0 = int(1, 5); val m1 = int(1, 4); val m2 = -int(1, 3); val b1 = int(-3, 4)
            val y0 = m1 * x0 + b1; val b2 = y0 - m2 * x0
            fun lin(mm: Int, bb: Int) = (if (mm == 1) "x" else if (mm == -1) "-x" else "${mm}x") + (if (bb > 0) " + $bb" else if (bb < 0) " - ${-bb}" else "")
            task(
                question = l("""Wo schneiden sich die Geraden §y = ${lin(m1, b1)}§ und §y = ${lin(m2, b2)}§? Gib die x-Koordinate an.""", """Where do the lines §y = ${lin(m1, b1)}§ and §y = ${lin(m2, b2)}§ intersect? Give the x-coordinate."""),
                steps = listOf(num("x", x0.toDouble(), "", abs = 0.0, done = """§S(${x0} \mid ${y0})§""",
                    hints = listOf(l("Kernidee: Im Schnittpunkt haben beide Geraden denselben y-Wert. Setze die rechten Seiten gleich.", "Key idea: at the intersection both lines have the same y value. Set the right-hand sides equal."), """§${lin(m1, b1)} = ${lin(m2, b2)}§"""))),
                solution = listOf("""§${lin(m1, b1)} = ${lin(m2, b2)} \Rightarrow ${m1 - m2}x = ${b2 - b1} \Rightarrow x = ${x0}§""", """§y = ${y0}§"""),
                background = l("Eine Gerade ist die Menge aller Punkte, die ihre Gleichung erfüllen. Der Schnittpunkt muss beide Gleichungen gleichzeitig erfüllen, deshalb setzt man sie gleich. Grafisch sieht man so, wann zwei Tarife gleich teuer sind oder wann ein Läufer den anderen einholt.", "A line is the set of all points that satisfy its equation. The intersection must satisfy both equations at once, which is why you set them equal. Graphically, this shows when two tariffs cost the same or when one runner catches up with another."),
            )
        },
        lab("ma.aufeinanderfolgend", Topic.Equations, E, 0.4f) {
            val k = int(8, 40); val sum = 3 * k + 3
            task(
                question = l("Drei aufeinanderfolgende ganze Zahlen ergeben zusammen ${sum}. Wie heißt die größte?", "Three consecutive whole numbers add up to ${sum}. What is the largest?"),
                steps = listOf(num(l("Größte Zahl", "Largest number"), (k + 2).toDouble(), "", abs = 0.0, done = """§${k + 2}§""",
                    hints = listOf(l("Kernidee: Nenne die kleinste n. Die anderen sind n + 1 und n + 2.", "Key idea: call the smallest n. The others are n + 1 and n + 2."), """§n + (n + 1) + (n + 2) = 3n + 3 = ${sum}§"""))),
                solution = listOf("""§3n + 3 = ${sum} \Rightarrow n = ${k}§""", l("Die Zahlen sind ${k}, ${k + 1} und ${k + 2}.", "The numbers are ${k}, ${k + 1} and ${k + 2}.")),
                background = l("Textaufgaben werden leicht, sobald man die Unbekannte geschickt benennt. Noch eleganter ist es hier, die mittlere Zahl m zu nennen: Dann sind es m − 1, m und m + 1, und die Summe ist einfach 3m. Die mittlere Zahl ist also ein Drittel der Summe.", "Word problems get easy once you name the unknown cleverly. It's even more elegant here to call the middle number m: then the numbers are m − 1, m and m + 1, and their sum is simply 3m. So the middle number is a third of the sum."),
            )
        },
        lab("ma.sparplan", Topic.Equations, E, 0.4f) {
            val start = pick(200, 150, 80); val rate = pick(50, 40, 25); val target = start + rate * int(8, 20)
            val months = (target - start) / rate
            task(
                question = l("Du hast ${start} € gespart und legst jeden Monat ${rate} € dazu. Nach wie vielen Monaten hast du ${target} € für ein neues Fahrrad?", "You have saved €${start} and put €${rate} aside every month. After how many months will you have €${target} for a new bike?"),
                steps = listOf(num(l("Monate", "Months"), months.toDouble(), "", abs = 0.0, done = """§${months}\ \text{Monate}§""".let { l(it, it.replace("Monate", "months")) },
                    hints = listOf(l("Kernidee: Startbetrag plus Monate mal Sparrate soll das Ziel ergeben.", "Key idea: starting amount plus months times saving rate should equal the target."), """§${start} + ${rate}\, n = ${target}§"""))),
                solution = listOf("""§${rate}\, n = ${target - start} \Rightarrow n = ${months}§"""),
                background = l("Eine Größe, die jeden Monat um denselben Betrag wächst, beschreibt eine Gerade: Der Startwert ist der y-Achsenabschnitt, die Rate die Steigung. Mit einer einzigen Gleichung findet man heraus, wann ein Ziel erreicht ist. Mit Zinsen würde das Guthaben zusätzlich immer schneller wachsen.", "A quantity that grows by the same amount every month is described by a straight line: the starting value is the y-intercept, the rate the slope. A single equation tells you when a goal is reached. With interest, the balance would also grow faster and faster."),
            )
        },
        lab("ma.keineloesung", Topic.Equations, E, 0.6f) {
            val c = pick(1, 4, 9)
            task(
                question = l("""Wie viele reelle Lösungen hat die Gleichung §x^2 + ${c} = 0§?""", """How many real solutions does the equation §x^2 + ${c} = 0§ have?"""),
                steps = listOf(pick1(l("Anzahl der Lösungen", "Number of solutions"), l("Keine", "None"), listOf(l("Eine", "One"), l("Zwei", "Two"), l("Unendlich viele", "Infinitely many")),
                    hints = listOf(l("Kernidee: Kann ein Quadrat einer reellen Zahl negativ sein?", "Key idea: can the square of a real number be negative?"), """§x^2 = -${c}§"""))),
                solution = listOf("""§x^2 = -${c}§""", l("Jedes Quadrat einer reellen Zahl ist mindestens 0. Es gibt also keine reelle Lösung.", "Every square of a real number is at least 0. So there is no real solution.")),
                background = l("Grafisch ist x² + ${c} eine Parabel, die ganz über der x-Achse liegt und sie nie berührt. Lange galten solche Gleichungen als unlösbar. Erst mit der Erfindung der imaginären Einheit i mit i² = −1 bekamen sie Lösungen, hier ±${n(sqrt(c.toDouble()), 0)}i. Komplexe Zahlen sind heute unverzichtbar, etwa in der Elektrotechnik und der Quantenphysik.", "Graphically x² + ${c} is a parabola lying entirely above the x-axis, never touching it. For a long time such equations were considered unsolvable. Only with the invention of the imaginary unit i with i² = −1 did they get solutions, here ±${n(sqrt(c.toDouble()), 0)}i. Complex numbers are indispensable today, for example in electrical engineering and quantum physics."),
            )
        },
        lab("ma.bruchgleichung", Topic.Equations, M, 0.5f) {
            val (a, b) = pick(3 to 2, 5 to 3, 4 to 3, 7 to 5)
            val x = a.toDouble() / (a - b)
            task(
                question = l("""Löse nach x auf: §\dfrac{${a}}{x} = \dfrac{${b}}{x - 1}§""", """Solve for x: §\dfrac{${a}}{x} = \dfrac{${b}}{x - 1}§"""),
                steps = listOf(num("x", x, "", abs = 0.01, done = """§x = ${m(x, 2)}§""",
                    hints = listOf(l("Kernidee: Über Kreuz multiplizieren, dann verschwinden die Brüche.", "Key idea: cross-multiply, and the fractions disappear."), """§${a}(x - 1) = ${b}x§"""))),
                solution = listOf("""§${a}x - ${a} = ${b}x \Rightarrow ${a - b}x = ${a} \Rightarrow x = ${m(x, 2)}§"""),
                background = l("Bei Bruchgleichungen multipliziert man mit allen Nennern, dann bleibt eine gewöhnliche Gleichung übrig. Vorher muss man aber festhalten, welche Werte verboten sind: Hier darf x weder 0 noch 1 sein, sonst würde man durch null teilen. Liefert die Rechnung einen verbotenen Wert, ist er keine Lösung.", "With equations involving fractions you multiply by all the denominators, and an ordinary equation remains. But first you must note which values are forbidden: here x can be neither 0 nor 1, otherwise you'd be dividing by zero. If the calculation produces a forbidden value, it isn't a solution."),
            )
        },
        lab("ma.betrag", Topic.Equations, M, 0.5f) {
            val a = pick(2, 3); val b = int(1, 9); val c = int(5, 15)
            val x1 = (c + b).toDouble() / a; val x2 = (b - c).toDouble() / a
            task(
                question = l("""Löse §|${a}x - ${b}| = ${c}§. Wie heißt die größere Lösung?""", """Solve §|${a}x - ${b}| = ${c}§. What is the larger solution?"""),
                steps = listOf(num(l("Größere Lösung", "Larger solution"), x1, "", abs = 0.01, done = """§x_1 = ${m(x1, 2)}§""",
                    hints = listOf(l("Kernidee: Der Betrag ist der Abstand zur Null. Der Inhalt kann also +${c} oder −${c} sein.", "Key idea: absolute value is the distance from zero. So the inside can be +${c} or −${c}."), """§${a}x - ${b} = ${c}\ \text{oder}\ ${a}x - ${b} = -${c}§""".let { l(it, it.replace("oder", "or")) }))),
                solution = listOf("""§${a}x - ${b} = ${c} \Rightarrow x = ${m(x1, 2)}§""", """§${a}x - ${b} = -${c} \Rightarrow x = ${m(x2, 2)}§"""),
                background = l("Der Betrag macht aus jeder Zahl ihren Abstand zur Null, also immer etwas Positives. Eine Betragsgleichung spaltet sich deshalb in zwei Fälle. Geometrisch: Gesucht sind alle Punkte auf dem Zahlenstrahl, die einen bestimmten Abstand von einem Punkt haben, und davon gibt es immer zwei.", "Absolute value turns every number into its distance from zero, so always something positive. An equation with an absolute value therefore splits into two cases. Geometrically: you're looking for all the points on the number line at a given distance from one point, and there are always two of them."),
            )
        },
        lab("ma.logarithmus", Topic.Equations, M, 0.5f) {
            val r = pick(500.0, 2000.0, 50.0, 30.0); val x = log10(r)
            task(
                question = l("""Löse nach x auf: §10^x = ${m(r, 0)}§""", """Solve for x: §10^x = ${m(r, 0)}§"""),
                steps = listOf(num("x", x, "", abs = 0.005, done = """§x \approx ${m(x, 3)}§""",
                    hints = listOf(l("Kernidee: Der Zehnerlogarithmus beantwortet genau die Frage, mit welcher Hochzahl 10 die Zahl ergibt.", "Key idea: the base-ten logarithm answers exactly the question of which exponent turns 10 into the number."), """§x = \log_{10} ${m(r, 0)}§"""))),
                solution = listOf("""§x = \log_{10} ${m(r, 0)} \approx ${m(x, 3)}§""", l("Probe: Die Zahl liegt zwischen §10^{${kotlin.math.floor(x).toInt()}}§ und §10^{${kotlin.math.floor(x).toInt() + 1}}§.", "Check: the number lies between §10^{${kotlin.math.floor(x).toInt()}}§ and §10^{${kotlin.math.floor(x).toInt() + 1}}§.")),
                background = l("Logarithmen machen aus Malnehmen Addieren: log(a·b) = log a + log b. Bevor es Taschenrechner gab, rechneten Ingenieure deshalb mit Logarithmentafeln und Rechenschiebern. Der Zehnerlogarithmus einer Zahl sagt grob, wie viele Stellen sie hat: Alles zwischen 100 und 1000 hat einen Logarithmus zwischen 2 und 3.", "Logarithms turn multiplication into addition: log(a·b) = log a + log b. Before calculators existed, engineers therefore worked with log tables and slide rules. The base-ten logarithm of a number roughly tells you how many digits it has: everything between 100 and 1000 has a logarithm between 2 and 3."),
            )
        },
        lab("ma.diskriminante", Topic.Equations, M, 0.6f) {
            val p = 2 * int(2, 6); val c = (p / 2) * (p / 2)
            task(
                question = l("""Für welche Zahl c hat die Gleichung §x^2 + ${p}x + c = 0§ genau eine Lösung?""", """For which number c does the equation §x^2 + ${p}x + c = 0§ have exactly one solution?"""),
                steps = listOf(num("c", c.toDouble(), "", abs = 0.0, done = """§c = ${c}§""",
                    hints = listOf(l("Kernidee: In der Lösungsformel steht eine Wurzel. Genau eine Lösung gibt es, wenn darunter null steht.", "Key idea: the solution formula contains a square root. There's exactly one solution if the expression under it is zero."), """§\left(\dfrac{p}{2}\right)^2 - c = 0§"""))),
                solution = listOf("""§\left(\dfrac{${p}}{2}\right)^2 - c = 0 \Rightarrow c = ${c}§""", """§x^2 + ${p}x + ${c} = (x + ${p / 2})^2§"""),
                background = l("Die Diskriminante, der Ausdruck unter der Wurzel in der Lösungsformel, entscheidet über die Zahl der Lösungen: positiv zwei, null eine, negativ keine reelle. Grafisch heißt null: Der Scheitel der Parabel berührt die x-Achse genau. Dann ist der Term ein vollständiges Quadrat.", "The discriminant, the expression under the square root in the solution formula, decides the number of solutions: positive two, zero one, negative no real ones. Graphically, zero means that the vertex of the parabola just touches the x-axis. Then the expression is a perfect square."),
            )
        },
        lab("ma.celsiusfahrenheit", Topic.Equations, M, 0.5f) {
            task(
                question = l("Fahrenheit rechnet man aus Celsius mit F = 1,8 · C + 32. Bei welcher Temperatur zeigen beide Skalen dieselbe Zahl?", "Fahrenheit is calculated from Celsius with F = 1.8 · C + 32. At what temperature do both scales show the same number?"),
                steps = listOf(num(l("Temperatur", "Temperature"), -40.0, "°", abs = 0.0, done = """§-40§""",
                    hints = listOf(l("Kernidee: Setze F = C und löse die Gleichung.", "Key idea: set F = C and solve the equation."), """§C = 1{,}8\, C + 32§"""))),
                solution = listOf("""§C = 1{,}8\, C + 32 \Rightarrow -0{,}8\, C = 32 \Rightarrow C = -40§"""),
                background = l("Zwei lineare Skalen mit verschiedener Steigung treffen sich genau einmal. Bei Celsius und Fahrenheit ist das bei −40. Die Fahrenheit-Skala stammt aus dem frühen 18. Jahrhundert, ihr Nullpunkt war die kälteste Mischung, die Daniel Fahrenheit herstellen konnte. Heute wird sie fast nur noch in den USA benutzt.", "Two linear scales with different slopes meet exactly once. For Celsius and Fahrenheit that's at −40. The Fahrenheit scale dates from the early 18th century; its zero point was the coldest mixture Daniel Fahrenheit could make. Today it's used almost only in the USA."),
            )
        },
        lab("ma.flaecheumfang", Topic.Equations, M, 0.6f) {
            val (a, b) = pick(12 to 5, 9 to 4, 10 to 6, 15 to 8)
            val u = 2 * (a + b); val f = a * b
            task(
                question = l("Ein rechteckiges Beet hat ${u} m Umfang und ${f} m² Fläche. Wie lang ist die längere Seite?", "A rectangular flowerbed has a perimeter of ${u} m and an area of ${f} m². How long is the longer side?"),
                steps = listOf(num(l("Längere Seite in Metern", "Longer side in metres"), a.toDouble(), "m", abs = 0.01, done = """§${a}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Zwei Gleichungen: a + b = ${u / 2} und a · b = ${f}. Setze b = ${u / 2} − a ein.", "Key idea: two equations: a + b = ${u / 2} and a · b = ${f}. Substitute b = ${u / 2} − a."), """§a(${u / 2} - a) = ${f} \Rightarrow a^2 - ${u / 2}a + ${f} = 0§"""))),
                solution = listOf("""§a^2 - ${u / 2}a + ${f} = 0 \Rightarrow a = ${a}\ \text{oder}\ a = ${b}§""".let { l(it, it.replace("oder", "or")) }, l("Die Seiten sind ${a} m und ${b} m.", "The sides are ${a} m and ${b} m.")),
                background = l("Zwei Zahlen, deren Summe und Produkt man kennt, sind die Lösungen einer quadratischen Gleichung (Satz von Vieta). Deshalb kommen beide Seiten als Lösung heraus: Welche man Länge und welche Breite nennt, ist egal. Schon die Babylonier lösten vor 4000 Jahren genau solche Aufgaben auf Tontafeln.", "Two numbers whose sum and product you know are the solutions of a quadratic equation (Vieta's theorem). That's why both sides come out as solutions: which you call length and which width doesn't matter. The Babylonians solved exactly such problems on clay tablets 4000 years ago."),
            )
        },
        lab("ma.formelumstellen", Topic.Equations, M, 0.5f) {
            val v = pick(1000.0, 500.0, 330.0); val h = pick(10.0, 12.0, 8.0)
            val r = sqrt(v / (PI * h))
            task(
                question = l("Eine zylindrische Dose soll ${n(v, 0)} cm³ fassen und ${n(h, 0)} cm hoch sein. Welchen Radius braucht sie? (V = π · r² · h)", "A cylindrical tin is to hold ${n(v, 0)} cm³ and be ${n(h, 0)} cm tall. What radius does it need? (V = π · r² · h)"),
                steps = listOf(num(l("Radius in cm", "Radius in cm"), r, "cm", rel = 0.01, done = """§r \approx ${m(r, 2)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Stelle die Formel nach r um: erst durch π und h teilen, dann die Wurzel ziehen.", "Key idea: rearrange the formula for r: first divide by π and h, then take the square root."), """§r = \sqrt{\dfrac{V}{\pi h}}§"""))),
                solution = listOf("""§r = \sqrt{\dfrac{${m(v, 0)}}{\pi \cdot ${m(h, 0)}}}\ \text{cm} \approx ${m(r, 2)}\ \text{cm}§"""),
                background = l("Eine Formel umzustellen heißt, die Rechenschritte rückwärts zu gehen, wie beim Ausziehen erst die Jacke, dann den Pulli. Wurde r erst quadriert und dann mit π·h multipliziert, teilt man zuerst durch π·h und zieht dann die Wurzel. Einheiten zeigen, ob man richtig liegt: cm³ durch cm ergibt cm², die Wurzel cm.", "Rearranging a formula means going through the steps backwards, like taking off your jacket and then your jumper. If r was first squared and then multiplied by π·h, you first divide by π·h and then take the square root. The units show whether you're right: cm³ divided by cm gives cm², and its square root cm."),
            )
        },
        lab("ma.halbierung", Topic.Equations, M, 0.6f) {
            val t12 = pick(6.0, 4.0, 8.0); val frac = pick(10.0, 5.0, 1.0)
            val t = t12 * ln(100 / frac) / ln(2.0)
            task(
                question = l("Die Menge eines Medikaments im Blut halbiert sich alle ${n(t12, 0)} Stunden. Nach wie vielen Stunden sind nur noch ${n(frac, 0)} % übrig?", "The amount of a medicine in the blood halves every ${n(t12, 0)} hours. After how many hours is only ${n(frac, 0)}% left?"),
                steps = listOf(num(l("Zeit in Stunden", "Time in hours"), t, "h", rel = 0.01, done = """§t \approx ${m(t, 1)}\ \text{h}§""",
                    hints = listOf(l("Kernidee: Nach n Halbwertszeiten ist (1/2)ⁿ übrig. Löse mit dem Logarithmus nach n auf.", "Key idea: after n half-lives (1/2)ⁿ is left. Solve for n with a logarithm."), """§\left(\tfrac{1}{2}\right)^n = ${m(frac / 100, 2)} \Rightarrow n = \dfrac{\ln ${m(100 / frac, 0)}}{\ln 2}§"""))),
                solution = listOf("""§n = \dfrac{\ln ${m(100 / frac, 0)}}{\ln 2} \approx ${m(t / t12, 2)}§""", """§t = ${m(t / t12, 2)} \cdot ${m(t12, 0)}\ \text{h} \approx ${m(t, 1)}\ \text{h}§"""),
                background = l("Viele Stoffe werden vom Körper exponentiell abgebaut: Pro Stunde verschwindet immer derselbe Anteil. Deshalb nimmt man Tabletten in festen Abständen. Nach etwa fünf Halbwertszeiten ist ein Stoff praktisch verschwunden, egal wie viel man genommen hat.", "Many substances are broken down by the body exponentially: the same fraction disappears every hour. That's why tablets are taken at fixed intervals. After about five half-lives a substance has practically gone, however much was taken."),
            )
        },
        lab("ma.keinparameter", Topic.Equations, M, 0.7f) {
            val k = int(2, 5); val b = int(1, 6); val c = b + int(1, 6)
            task(
                question = l("""Für welchen Wert von a hat die Gleichung §a x + ${b} = ${k}x + ${c}§ überhaupt keine Lösung?""", """For which value of a does the equation §a x + ${b} = ${k}x + ${c}§ have no solution at all?"""),
                steps = listOf(num("a", k.toDouble(), "", abs = 0.0, done = """§a = ${k}§""",
                    hints = listOf(l("Kernidee: Bring alle x auf eine Seite. Was passiert, wenn der Faktor vor x null wird?", "Key idea: bring all the x terms to one side. What happens if the factor in front of x becomes zero?"), """§(a - ${k})\, x = ${c - b}§"""))),
                solution = listOf("""§(a - ${k})\, x = ${c - b}§""", l("Für a = ${k} steht da 0 = ${c - b}, ein Widerspruch. Keine Zahl x erfüllt das.", "For a = ${k} it says 0 = ${c - b}, a contradiction. No number x satisfies it.")),
                background = l("Eine lineare Gleichung hat meistens genau eine Lösung. Fällt aber das x heraus, bleiben zwei Möglichkeiten: Entweder steht da etwas Falsches wie 0 = 4, dann gibt es keine Lösung, oder etwas immer Wahres wie 0 = 0, dann ist jede Zahl eine Lösung. Grafisch: Zwei parallele Geraden schneiden sich nie, zwei gleiche überall.", "A linear equation usually has exactly one solution. But if the x drops out, two possibilities remain: either it says something false like 0 = 4, then there's no solution, or something always true like 0 = 0, then every number is a solution. Graphically: two parallel lines never meet, two identical ones meet everywhere."),
            )
        },
        lab("ma.wurzelgleichung", Topic.Equations, H, 0.6f) {
            val (a, b) = pick(7 to 5, 13 to 7, 3 to 3)
            // sqrt(x + a) = x - b  ->  x + a = x^2 - 2bx + b^2
            val disc = (2 * b + 1).toDouble().pow(2) - 4 * (b * b - a)
            val x1 = ((2 * b + 1) + sqrt(disc)) / 2; val x2 = ((2 * b + 1) - sqrt(disc)) / 2
            task(
                question = l("""Löse §\sqrt{x + ${a}} = x - ${b}§.""", """Solve §\sqrt{x + ${a}} = x - ${b}§."""),
                steps = listOf(num("x", x1, "", abs = 0.01, done = """§x = ${m(x1, 2)}§""",
                    wrong = l("Probe machen: Beim Quadrieren kann eine falsche Lösung dazukommen.", "Check your answer: squaring can introduce a false solution."),
                    hints = listOf(l("Kernidee: Quadriere beide Seiten, löse die quadratische Gleichung und mach dann unbedingt die Probe.", "Key idea: square both sides, solve the quadratic, and then be sure to check."), """§x + ${a} = (x - ${b})^2§"""))),
                solution = listOf("""§x + ${a} = x^2 - ${2 * b}x + ${b * b} \Rightarrow x^2 - ${2 * b + 1}x + ${b * b - a} = 0§""", """§x_1 = ${m(x1, 2)},\ x_2 = ${m(x2, 2)}§""", l("Probe: Für x₂ wäre die rechte Seite negativ, eine Wurzel kann das nicht sein. Nur x₁ ist eine Lösung.", "Check: for x₂ the right-hand side would be negative, which a square root can't be. Only x₁ is a solution.")),
                background = l("Quadrieren ist keine umkehrbare Umformung: Aus a = b folgt a² = b², aber aus a² = b² folgt nur a = ±b. Deshalb kann beim Quadrieren eine Scheinlösung entstehen, die zur ursprünglichen Gleichung mit dem anderen Vorzeichen gehört. Die Probe ist bei Wurzelgleichungen Pflicht.", "Squaring isn't a reversible step: from a = b follows a² = b², but from a² = b² only a = ±b. That's why squaring can produce a spurious solution that belongs to the original equation with the other sign. Checking is compulsory for equations with square roots."),
            )
        },
        lab("ma.zinsverdopplung", Topic.Equations, H, 0.6f) {
            val years = pick(10.0, 15.0, 20.0); val p = (2.0.pow(1 / years) - 1) * 100
            task(
                question = l("Welcher jährliche Zinssatz verdoppelt ein Guthaben mit Zinseszins in genau ${n(years, 0)} Jahren?", "What annual interest rate doubles a balance with compound interest in exactly ${n(years, 0)} years?"),
                steps = listOf(num(l("Zinssatz in Prozent", "Interest rate in percent"), p, "%", abs = 0.02, done = """§p \approx ${m(p, 2)}\ \%§""",
                    hints = listOf(l("Kernidee: Der Wachstumsfaktor q muss ${n(years, 0)}-mal mit sich selbst multipliziert 2 ergeben. Also ist q die ${n(years, 0)}-te Wurzel aus 2.", "Key idea: the growth factor q multiplied by itself ${n(years, 0)} times must give 2. So q is the ${n(years, 0)}th root of 2."), """§q^{${m(years, 0)}} = 2 \Rightarrow q = 2^{1/${m(years, 0)}}§"""))),
                solution = listOf("""§q = 2^{1/${m(years, 0)}} \approx ${m(1 + p / 100, 4)} \Rightarrow p \approx ${m(p, 2)}\ \%§"""),
                background = l("Zinseszins ist exponentielles Wachstum. Um von der Verdopplungszeit zurück auf den Zinssatz zu kommen, braucht man die Umkehrung der Potenz, eine Wurzel. Zum Vergleich mit der 72er-Regel: 72 / ${n(years, 0)} = ${n(72 / years, 1)} %, sehr nah dran.", "Compound interest is exponential growth. To get from the doubling time back to the interest rate, you need the inverse of the power, a root. For comparison with the rule of 72: 72 / ${n(years, 0)} = ${n(72 / years, 1)}%, very close."),
            )
        },
        lab("ma.polynom", Topic.Equations, H, 0.7f) {
            val (r1, r2, r3) = pick(Triple(1, 2, 3), Triple(1, 3, 4), Triple(-1, 2, 5), Triple(1, 2, 6))
            val s = r1 + r2 + r3; val p2 = r1 * r2 + r1 * r3 + r2 * r3; val p3 = r1 * r2 * r3
            fun t(c: Int, v: String) = if (c >= 0) " + ${c}$v" else " - ${-c}$v"
            task(
                question = l("""Löse §x^3 - ${s}x^2${t(p2, "x")}${t(-p3, "")} = 0§. Wie heißt die größte Lösung?""", """Solve §x^3 - ${s}x^2${t(p2, "x")}${t(-p3, "")} = 0§. What is the largest solution?"""),
                steps = listOf(num(l("Größte Lösung", "Largest solution"), r3.toDouble(), "", abs = 0.0, done = """§x = ${r3}§""",
                    hints = listOf(l("Kernidee: Ganzzahlige Lösungen müssen Teiler des letzten Terms ${-p3} sein. Probiere kleine Teiler.", "Key idea: whole-number solutions must divide the constant term ${-p3}. Try small divisors."), l("Hast du eine Lösung r gefunden, kannst du durch (x − r) teilen und eine quadratische Gleichung lösen.", "Once you've found a solution r, divide by (x − r) and solve a quadratic."))),
                    ),
                solution = listOf("""§x^3 - ${s}x^2${t(p2, "x")}${t(-p3, "")} = (x ${if (r1 < 0) "+ ${-r1}" else "- $r1"})(x - ${r2})(x - ${r3})§""", l("Die Lösungen sind ${r1}, ${r2} und ${r3}.", "The solutions are ${r1}, ${r2} and ${r3}.")),
                background = l("Für Gleichungen dritten Grades gibt es eine Lösungsformel, aber sie ist so unhandlich, dass man fast immer anders vorgeht: Man rät eine Lösung und dividiert sie heraus. Raten ist dabei kein Glücksspiel. Aus dem Ausmultiplizieren folgt, dass ganzzahlige Lösungen das Absolutglied teilen müssen.", "There is a formula for third-degree equations, but it's so unwieldy that you almost always go another way: guess one solution and divide it out. Guessing isn't a lottery here. From multiplying out it follows that whole-number solutions must divide the constant term."),
            )
        },
        lab("ma.biquadratisch", Topic.Equations, H, 0.6f) {
            val (a, b) = pick(2 to 3, 1 to 4, 2 to 5, 1 to 3)
            val p = a * a + b * b; val q = a * a * b * b
            task(
                question = l("""Löse §x^4 - ${p}x^2 + ${q} = 0§. Wie heißt die größte Lösung?""", """Solve §x^4 - ${p}x^2 + ${q} = 0§. What is the largest solution?"""),
                steps = listOf(num(l("Größte Lösung", "Largest solution"), b.toDouble(), "", abs = 0.0, done = """§x = ${b}§""",
                    hints = listOf(l("Kernidee: Ersetze x² durch eine neue Variable z. Dann ist es eine gewöhnliche quadratische Gleichung.", "Key idea: replace x² with a new variable z. Then it's an ordinary quadratic equation."), """§z^2 - ${p}z + ${q} = 0§"""))),
                solution = listOf("""§z^2 - ${p}z + ${q} = 0 \Rightarrow z = ${a * a}\ \text{oder}\ z = ${b * b}§""".let { l(it, it.replace("oder", "or")) }, """§x = \pm ${a},\ x = \pm ${b}§"""),
                background = l("Substitution ist ein Grundtrick der Mathematik: Man ersetzt einen komplizierten Teil durch eine neue Variable, löst das einfachere Problem und übersetzt zurück. Eine biquadratische Gleichung hat deshalb bis zu vier Lösungen, immer paarweise mit Plus und Minus.", "Substitution is a basic trick in mathematics: you replace a complicated part with a new variable, solve the simpler problem and translate back. A biquadratic equation therefore has up to four solutions, always in pairs with plus and minus."),
            )
        },
        lab("ma.exponentialgleichung", Topic.Equations, X, 0.6f) {
            val x = ln(3.0) / (ln(3.0) - ln(2.0))
            task(
                question = l("""Löse §2^x = 3^{x - 1}§.""", """Solve §2^x = 3^{x - 1}§."""),
                steps = listOf(num("x", x, "", abs = 0.005, done = """§x \approx ${m(x, 3)}§""",
                    hints = listOf(l("Kernidee: Logarithmiere beide Seiten. Dann holt man die Exponenten nach vorn.", "Key idea: take the logarithm of both sides. That brings the exponents down in front."), """§x \ln 2 = (x - 1) \ln 3§"""))),
                solution = listOf("""§x \ln 2 = x \ln 3 - \ln 3 \Rightarrow x(\ln 3 - \ln 2) = \ln 3§""", """§x = \dfrac{\ln 3}{\ln 3 - \ln 2} \approx ${m(x, 3)}§"""),
                background = l("Stehen die Unbekannten in Exponenten verschiedener Basen, hilft der Logarithmus: Er verwandelt Potenzen in Produkte, log(aˣ) = x·log a. Welchen Logarithmus man nimmt, ist egal, das Ergebnis ist dasselbe. Grafisch ist es der Punkt, an dem die schneller wachsende Funktion 3ˣ⁻¹ die Funktion 2ˣ überholt.", "If the unknowns sit in the exponents of different bases, the logarithm helps: it turns powers into products, log(aˣ) = x·log a. Which logarithm you use doesn't matter; the result is the same. Graphically it's the point where the faster-growing function 3ˣ⁻¹ overtakes 2ˣ."),
            )
        },
        lab("ma.gleichungssystem3", Topic.Equations, H, 0.7f) {
            val x = int(1, 4); val y = int(1, 4); val z = int(1, 5)
            val r1 = x + y + z; val r2 = x - y + z; val r3 = 2 * x + y - z
            task(
                question = l("""Löse das System §x + y + z = ${r1}§, §x - y + z = ${r2}§, §2x + y - z = ${r3}§. Wie groß ist z?""", """Solve the system §x + y + z = ${r1}§, §x - y + z = ${r2}§, §2x + y - z = ${r3}§. What is z?"""),
                steps = listOf(num("z", z.toDouble(), "", abs = 0.0, done = """§x = ${x},\ y = ${y},\ z = ${z}§""",
                    hints = listOf(l("Kernidee: Ziehe die zweite Gleichung von der ersten ab, dann fallen x und z weg.", "Key idea: subtract the second equation from the first; then x and z drop out."), """§2y = ${r1 - r2} \Rightarrow y = ${y}§"""))),
                solution = listOf("""§(1) - (2):\ 2y = ${r1 - r2} \Rightarrow y = ${y}§""", """§x + z = ${r1 - y},\ 2x - z = ${r3 - y} \Rightarrow 3x = ${r1 + r3 - 2 * y} \Rightarrow x = ${x}§""", """§z = ${r1 - y} - ${x} = ${z}§"""),
                background = l("Bei mehreren Unbekannten braucht man so viele unabhängige Gleichungen wie Unbekannte. Durch geschicktes Addieren und Subtrahieren eliminiert man eine Variable nach der anderen, das Gaußsche Eliminationsverfahren. Computer lösen so Systeme mit Millionen Gleichungen, etwa für Wettervorhersagen und Crashtests.", "With several unknowns you need as many independent equations as there are unknowns. By clever addition and subtraction you eliminate one variable after another: Gaussian elimination. Computers solve systems with millions of equations this way, for example for weather forecasts and crash tests."),
            )
        },
    )

    private val sequences = listOf(
        lab("ma.arithglied", Topic.Sequences, E, 0.4f) {
            val a = int(1, 9); val d = int(2, 7); val k = pick(20, 50, 100)
            val v = a + (k - 1) * d
            task(
                question = l("Die Folge beginnt ${a}, ${a + d}, ${a + 2 * d}, ${a + 3 * d}, … Welche Zahl steht an der ${k}. Stelle?", "The sequence starts ${a}, ${a + d}, ${a + 2 * d}, ${a + 3 * d}, … What number is in position ${k}?"),
                steps = listOf(num(l("${k}. Glied", "Term ${k}"), v.toDouble(), "", abs = 0.0, done = """§a_{${k}} = ${v}§""",
                    hints = listOf(l("Kernidee: Von Glied zu Glied kommt immer ${d} dazu. Bis zur ${k}. Stelle sind es ${k - 1} Schritte.", "Key idea: from term to term you always add ${d}. Up to position ${k} there are ${k - 1} steps."), """§a_n = a_1 + (n - 1) \cdot d§"""))),
                solution = listOf("""§a_{${k}} = ${a} + ${k - 1} \cdot ${d} = ${v}§"""),
                background = l("Eine arithmetische Folge wächst gleichmäßig, wie die Sprossen einer Leiter. Der häufigste Fehler ist, k statt k − 1 Schritte zu zählen: Das erste Glied ist ja schon da. Das ist derselbe Denkfehler wie bei Zaunpfählen und Zwischenräumen.", "An arithmetic sequence grows evenly, like the rungs of a ladder. The most common mistake is to count k steps instead of k − 1: the first term is already there. It's the same slip as with fence posts and gaps."),
            )
        },
        lab("ma.folgeplus", Topic.Sequences, E, 0.5f) {
            val (s, mult, add) = pick(Triple(3, 2, -1), Triple(2, 2, 1), Triple(1, 3, 1), Triple(5, 2, -3))
            val seq = generateSequence(s) { it * mult + add }.take(6).toList()
            task(
                question = l("Wie geht die Folge weiter? ${seq.take(5).joinToString(", ")}, …", "How does the sequence continue? ${seq.take(5).joinToString(", ")}, …"),
                steps = listOf(num(l("Nächste Zahl", "Next number"), seq[5].toDouble(), "", abs = 0.0, done = """§${seq[5]}§""",
                    hints = listOf(l("Kernidee: Die Abstände wachsen schnell. Vergleiche jede Zahl mit dem Doppelten oder Dreifachen der vorigen.", "Key idea: the gaps grow fast. Compare each number with double or triple the previous one."), l("Wie kommt man von ${seq[1]} auf ${seq[2]}, und von ${seq[2]} auf ${seq[3]}?", "How do you get from ${seq[1]} to ${seq[2]}, and from ${seq[2]} to ${seq[3]}?"))),
                    ),
                solution = listOf(l("Jede Zahl ist ${mult}-mal die vorige ${if (add >= 0) "plus $add" else "minus ${-add}"}: ${seq[4]} · ${mult} ${if (add >= 0) "+ $add" else "− ${-add}"} = ${seq[5]}.", "Each number is ${mult} times the previous one ${if (add >= 0) "plus $add" else "minus ${-add}"}: ${seq[4]} · ${mult} ${if (add >= 0) "+ $add" else "− ${-add}"} = ${seq[5]}.")),
                background = l("Wächst eine Folge immer schneller, steckt oft eine Multiplikation dahinter. Ein guter Test: Bilde die Differenzen. Verdoppeln sich auch die Differenzen, wird jedes Glied mit 2 malgenommen, und die Konstante findet man durch Einsetzen. Solche Regeln heißen rekursiv: Jedes Glied wird aus dem vorigen berechnet.", "If a sequence grows faster and faster, there's often a multiplication behind it. A good test: take the differences. If the differences also double, each term is being multiplied by 2, and you find the constant by substituting. Such rules are called recursive: each term is calculated from the previous one."),
            )
        },
        lab("ma.differenzen", Topic.Sequences, E, 0.5f) {
            val c = pick(1, 2, -1, 3)
            val seq = (1..6).map { it * it + c }
            task(
                question = l("Wie geht die Folge weiter? ${seq.take(5).joinToString(", ")}, …", "How does the sequence continue? ${seq.take(5).joinToString(", ")}, …"),
                steps = listOf(num(l("Nächste Zahl", "Next number"), seq[5].toDouble(), "", abs = 0.0, done = """§${seq[5]}§""",
                    hints = listOf(l("Kernidee: Schreib die Abstände zwischen den Zahlen auf. Sie bilden selbst eine einfache Folge.", "Key idea: write down the gaps between the numbers. They form a simple sequence themselves."), l("Die Abstände sind ${(0..3).joinToString(", ") { (seq[it + 1] - seq[it]).toString() }}, …", "The gaps are ${(0..3).joinToString(", ") { (seq[it + 1] - seq[it]).toString() }}, …"))),
                    ),
                solution = listOf(l("Die Abstände sind die ungeraden Zahlen, der nächste ist ${seq[5] - seq[4]}. Also ${seq[4]} + ${seq[5] - seq[4]} = ${seq[5]}. Die Folge ist n² ${if (c >= 0) "+ $c" else "− ${-c}"}.", "The gaps are the odd numbers; the next one is ${seq[5] - seq[4]}. So ${seq[4]} + ${seq[5] - seq[4]} = ${seq[5]}. The sequence is n² ${if (c >= 0) "+ $c" else "− ${-c}"}.")),
                background = l("Die Differenzenmethode ist das Schweizer Messer für Folgen: Sind die ersten Differenzen konstant, ist die Folge linear, sind erst die zweiten Differenzen konstant, ist sie quadratisch. Die Abstände der Quadratzahlen sind immer die ungeraden Zahlen, denn (n + 1)² − n² = 2n + 1.", "The method of differences is the Swiss army knife for sequences: if the first differences are constant, the sequence is linear; if only the second differences are constant, it's quadratic. The gaps between square numbers are always the odd numbers, because (n + 1)² − n² = 2n + 1."),
            )
        },
        lab("ma.streichhoelzer", Topic.Sequences, E, 0.6f) {
            val k = pick(30, 20, 50, 100); val v = 3 * k + 1
            task(
                question = l("Du legst Quadrate aus Streichhölzern in einer Reihe nebeneinander, benachbarte Quadrate teilen sich ein Hölzchen. Für 1 Quadrat brauchst du 4, für 2 Quadrate 7, für 3 Quadrate 10. Wie viele brauchst du für ${k} Quadrate?", "You lay squares made of matches side by side in a row; neighbouring squares share a match. 1 square needs 4, 2 squares need 7, 3 squares need 10. How many do you need for ${k} squares?"),
                steps = listOf(num(l("Streichhölzer", "Matches"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Das erste Quadrat braucht 4 Hölzer, jedes weitere nur 3, weil es sich eins mit dem Nachbarn teilt.", "Key idea: the first square needs 4 matches, every further one only 3, because it shares one with its neighbour."), """§4 + 3 \cdot (n - 1) = 3n + 1§"""))),
                solution = listOf("""§3 \cdot ${k} + 1 = ${v}§"""),
                background = l("Muster aus Bauteilen führen auf Folgen, und die Formel findet man am besten durch Verstehen statt durch Raten. Eine andere Sichtweise: Jedes Quadrat bringt oben, unten und rechts ein Holz mit, und ganz links steht noch eins. Beide Wege führen zu 3n + 1.", "Patterns of building blocks lead to sequences, and the formula is best found by understanding rather than guessing. Another way to see it: each square brings a match at the top, bottom and right, and there's one more at the far left. Both ways lead to 3n + 1."),
            )
        },
        lab("ma.primzahlen", Topic.Sequences, M, 0.5f) {
            val lim = pick(30, 50, 100)
            val cnt = (2 until lim).count { n -> (2..kotlin.math.sqrt(n.toDouble()).toInt()).none { n % it == 0 } }
            task(
                question = l("Primzahlen haben genau zwei Teiler: 1 und sich selbst. Wie viele Primzahlen gibt es unter ${lim}?", "Prime numbers have exactly two divisors: 1 and themselves. How many primes are there below ${lim}?"),
                steps = listOf(num(l("Anzahl", "Count"), cnt.toDouble(), "", abs = 0.0, done = """§${cnt}§""",
                    hints = listOf(l("Kernidee: Sieb des Eratosthenes. Streiche alle Vielfachen von 2, 3, 5, 7 (außer ihnen selbst). Was übrig bleibt, sind Primzahlen.", "Key idea: the sieve of Eratosthenes. Cross out all multiples of 2, 3, 5, 7 (but not the numbers themselves). What's left are primes."), l("1 ist keine Primzahl, 2 ist die einzige gerade.", "1 isn't a prime, and 2 is the only even one."))),
                    ),
                solution = listOf(l("Die Primzahlen unter ${lim}: ${(2 until lim).filter { n -> (2..kotlin.math.sqrt(n.toDouble()).toInt()).none { n % it == 0 } }.joinToString(", ")}. Das sind ${cnt}.", "The primes below ${lim}: ${(2 until lim).filter { n -> (2..kotlin.math.sqrt(n.toDouble()).toInt()).none { n % it == 0 } }.joinToString(", ")}. That's ${cnt}.")),
                background = l("Primzahlen sind die Atome der Zahlen: Jede Zahl lässt sich auf genau eine Weise in Primfaktoren zerlegen. Schon Euklid bewies, dass es unendlich viele gibt. Sie werden nach oben hin seltener, aber nie ganz selten: Unter einer Zahl N liegen etwa N / ln N Primzahlen. Moderne Verschlüsselung beruht darauf, dass man große Zahlen kaum in Primfaktoren zerlegen kann.", "Primes are the atoms of numbers: every number can be broken into prime factors in exactly one way. Euclid already proved that there are infinitely many. They get rarer further up but never very rare: below a number N there are about N / ln N primes. Modern encryption relies on the fact that large numbers can hardly be factorised."),
            )
        },
        lab("ma.ungeradesumme", Topic.Sequences, M, 0.5f) {
            val k = pick(10, 50, 100, 30); val last = 2 * k - 1
            task(
                question = l("""Wie groß ist die Summe §1 + 3 + 5 + \dots + ${last}§ der ersten ${k} ungeraden Zahlen?""", """What is the sum §1 + 3 + 5 + \dots + ${last}§ of the first ${k} odd numbers?"""),
                steps = listOf(num(l("Summe", "Sum"), (k * k).toDouble(), "", abs = 0.0, done = """§${k * k}§""",
                    hints = listOf(l("Kernidee: Probiere klein: 1 = 1, 1 + 3 = 4, 1 + 3 + 5 = 9. Fällt dir etwas auf?", "Key idea: try small cases: 1 = 1, 1 + 3 = 4, 1 + 3 + 5 = 9. Notice anything?"), """§1 + 3 + \dots + (2n - 1) = n^2§"""))),
                solution = listOf("""§${k}^2 = ${k * k}§"""),
                background = l("Die Summe der ersten n ungeraden Zahlen ist immer n². Man sieht es an einem Bild: Ein Quadrat aus Punkten wächst um eine L-förmige Ecke, und jede neue Ecke hat genau zwei Punkte mehr als die vorige. Mit Gauß' Trick klappt es auch: Erstes plus letztes Glied ist immer 2n, und es gibt n/2 solche Paare.", "The sum of the first n odd numbers is always n². You can see it in a picture: a square of dots grows by an L-shaped corner, and each new corner has exactly two dots more than the last. Gauss's trick works too: first plus last term is always 2n, and there are n/2 such pairs."),
            )
        },
        lab("ma.geomglied", Topic.Sequences, M, 0.5f) {
            val a = pick(2, 3, 5); val q = pick(3, 2); val k = pick(8, 10, 7)
            val v = a * q.toDouble().pow(k - 1)
            task(
                question = l("Die Folge beginnt ${a}, ${a * q}, ${a * q * q}, ${a * q * q * q}, … Welche Zahl steht an der ${k}. Stelle?", "The sequence starts ${a}, ${a * q}, ${a * q * q}, ${a * q * q * q}, … What number is in position ${k}?"),
                steps = listOf(num(l("${k}. Glied", "Term ${k}"), v, "", abs = 0.0, done = """§a_{${k}} = ${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: Jedes Glied ist das ${q}-fache des vorigen. Bis zur ${k}. Stelle wird ${k - 1}-mal malgenommen.", "Key idea: each term is ${q} times the previous one. Up to position ${k} you multiply ${k - 1} times."), """§a_n = a_1 \cdot q^{n - 1}§"""))),
                solution = listOf("""§a_{${k}} = ${a} \cdot ${q}^{${k - 1}} = ${m(v, 0)}§"""),
                background = l("Eine geometrische Folge entsteht, wenn man immer mit demselben Faktor malnimmt. Sie wächst irgendwann schneller als jede arithmetische Folge, egal wie klein der Faktor über 1 ist. Viren, die sich ausbreiten, Zinseszins und Kettenbriefe folgen diesem Muster.", "A geometric sequence arises when you always multiply by the same factor. Sooner or later it grows faster than any arithmetic sequence, however close the factor is to 1. Spreading viruses, compound interest and chain letters all follow this pattern."),
            )
        },
        lab("ma.geomsumme", Topic.Sequences, M, 0.6f) {
            val q = pick(2, 3, 4); val sum = 1.0 / (1 - 1.0 / q)
            task(
                question = l("""Was ergibt die unendliche Summe §1 + \dfrac{1}{${q}} + \dfrac{1}{${q * q}} + \dfrac{1}{${q * q * q}} + \dots§?""", """What is the infinite sum §1 + \dfrac{1}{${q}} + \dfrac{1}{${q * q}} + \dfrac{1}{${q * q * q}} + \dots§?"""),
                steps = listOf(num(l("Summe", "Sum"), sum, "", abs = 0.005, done = """§${m(sum, 3)}§""",
                    hints = listOf(l("Kernidee: Nenne die Summe S. Teilst du S durch ${q}, bekommst du dieselbe Summe ohne die 1 vorn.", "Key idea: call the sum S. If you divide S by ${q}, you get the same sum without the 1 at the front."), """§S - \dfrac{S}{${q}} = 1 \Rightarrow S = \dfrac{1}{1 - \tfrac{1}{${q}}}§"""))),
                solution = listOf("""§S = \dfrac{1}{1 - \tfrac{1}{${q}}} = ${m(sum, 3)}§"""),
                background = l("Unendlich viele Zahlen können eine endliche Summe haben, wenn sie schnell genug kleiner werden. Für die geometrische Reihe mit Faktor q (|q| < 1) ist die Summe 1/(1 − q). Damit löst sich auch das Paradox von Achilles und der Schildkröte: Unendlich viele Teilstrecken dauern zusammen nur endlich lange.", "Infinitely many numbers can have a finite sum if they get smaller fast enough. For the geometric series with ratio q (|q| < 1) the sum is 1/(1 − q). This also resolves the paradox of Achilles and the tortoise: infinitely many stretches together take only a finite time."),
            )
        },
        lab("ma.haendedruck", Topic.Sequences, M, 0.5f) {
            val k = pick(10, 20, 25, 30); val v = k * (k - 1) / 2
            task(
                question = l("Bei einer Feier mit ${k} Leuten gibt jeder jedem genau einmal die Hand. Wie viele Händedrücke gibt es?", "At a party with ${k} people everyone shakes hands with everyone else exactly once. How many handshakes are there?"),
                steps = listOf(num(l("Händedrücke", "Handshakes"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Jeder schüttelt ${k - 1} Hände. Aber so zählst du jeden Händedruck doppelt.", "Key idea: everyone shakes ${k - 1} hands. But that way you count every handshake twice."), """§\dfrac{n(n - 1)}{2}§"""))),
                solution = listOf("""§\dfrac{${k} \cdot ${k - 1}}{2} = ${v}§"""),
                background = l("Das ist eine Dreieckszahl: 1 + 2 + 3 + … + (n − 1). Der Erste schüttelt n − 1 neue Hände, der Zweite nur noch n − 2 neue, und so weiter. Dieselbe Zahl zählt die Spiele in einer Liga, in der jeder einmal gegen jeden antritt, und die Verbindungen in einem Netzwerk, in dem jeder mit jedem verbunden ist.", "This is a triangular number: 1 + 2 + 3 + … + (n − 1). The first person shakes n − 1 new hands, the second only n − 2 new ones, and so on. The same number counts the matches in a league where everyone plays everyone once, and the links in a network where everyone is connected to everyone."),
            )
        },
        lab("ma.collatz", Topic.Sequences, M, 0.6f) {
            val s = pick(6, 7, 9, 12)
            var x = s; var steps = 0; val path = mutableListOf(s)
            while (x != 1) { x = if (x % 2 == 0) x / 2 else 3 * x + 1; steps++; path += x }
            task(
                question = l("Collatz-Spiel: Ist die Zahl gerade, halbiere sie. Ist sie ungerade, nimm sie mal 3 und zähle 1 dazu. Wie viele Schritte braucht die ${s}, bis sie bei 1 ankommt?", "The Collatz game: if the number is even, halve it. If it's odd, multiply it by 3 and add 1. How many steps does ${s} take to reach 1?"),
                steps = listOf(num(l("Schritte", "Steps"), steps.toDouble(), "", abs = 0.0, done = """§${steps}§""",
                    hints = listOf(l("Kernidee: Einfach Schritt für Schritt rechnen und mitzählen.", "Key idea: just calculate step by step and keep count."), l("Der Anfang: ${path.take(4).joinToString(" → ")} …", "The start: ${path.take(4).joinToString(" → ")} …"))),
                    ),
                solution = listOf(path.joinToString(" → ") + l(" (${steps} Schritte)", " (${steps} steps)")),
                background = l("Die Collatz-Vermutung von 1937 sagt: Egal mit welcher Zahl man startet, man landet immer bei 1. Computer haben das für alle Zahlen bis über 10²⁰ geprüft, aber bewiesen hat es niemand. Der Mathematiker Paul Erdős meinte, die Mathematik sei für solche Probleme noch nicht reif.", "The Collatz conjecture of 1937 says: whatever number you start with, you always end up at 1. Computers have checked this for all numbers up to over 10²⁰, but nobody has proved it. The mathematician Paul Erdős said mathematics wasn't yet ready for such problems."),
                fact = l("Die 27 braucht 111 Schritte und klettert unterwegs bis auf 9232.", "27 takes 111 steps and climbs as high as 9232 along the way."),
            )
        },
        lab("ma.hanoi", Topic.Sequences, M, 0.5f) {
            val k = pick(5, 6, 8, 10); val v = 2.0.pow(k) - 1
            task(
                question = l("Türme von Hanoi: ${k} Scheiben liegen der Größe nach auf einem Stab. Sie sollen auf einen anderen Stab, man darf immer nur eine Scheibe bewegen und nie eine größere auf eine kleinere legen. Ein dritter Stab hilft. Wie viele Züge braucht man mindestens?", "Towers of Hanoi: ${k} discs lie in order of size on one peg. They have to go onto another peg; you may move only one disc at a time and never put a larger one on a smaller one. A third peg helps. What is the smallest number of moves needed?"),
                steps = listOf(num(l("Züge", "Moves"), v, "", abs = 0.0, done = """§${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: Um die größte Scheibe zu bewegen, muss der Turm darüber erst komplett auf den Hilfsstab und danach wieder auf sie.", "Key idea: to move the largest disc, the tower above it has to go completely onto the spare peg first and then back on top of it."), """§Z(n) = 2 Z(n - 1) + 1,\quad Z(1) = 1§"""))),
                solution = listOf("""§Z(n) = 2^n - 1 \Rightarrow Z(${k}) = ${m(v, 0)}§"""),
                background = l("Das Rätsel erfand der Mathematiker Édouard Lucas 1883. Die Lösung ist rekursiv: Ein großes Problem wird auf zwei kleinere Probleme derselben Art zurückgeführt. Jede Scheibe verdoppelt die Zugzahl, und das ergibt 2ⁿ − 1. Rekursion ist eine der wichtigsten Ideen der Informatik.", "The mathematician Édouard Lucas invented the puzzle in 1883. The solution is recursive: a big problem is reduced to two smaller problems of the same kind. Each disc doubles the number of moves, giving 2ⁿ − 1. Recursion is one of the most important ideas in computer science."),
                fact = l("Nach der Legende sollen Mönche einen Turm mit 64 goldenen Scheiben umsetzen, und dann geht die Welt unter. Mit einem Zug pro Sekunde bräuchten sie rund 585 Milliarden Jahre.", "According to the legend, monks are moving a tower of 64 golden discs, and then the world will end. At one move per second they would need about 585 billion years."),
            )
        },
        lab("ma.zahlwoerter", Topic.Sequences, M, 0.6f) {
            task(
                question = l("Welcher Buchstabe kommt als Nächstes? E, Z, D, V, F, S, S, A, N, …", "Which letter comes next? O, T, T, F, F, S, S, E, N, …"),
                steps = listOf(pick1(l("Nächster Buchstabe", "Next letter"), l("Z", "T"), listOf(l("E", "E"), l("O", "O"), l("T", "Z")).let { w -> if (de.funke.app.ui.Lang.german) listOf("E", "O", "T") else listOf("E", "O", "Z") },
                    hints = listOf(l("Kernidee: Es ist keine Buchstabenfolge aus dem Alphabet. Denk an Zahlen.", "Key idea: it isn't a sequence from the alphabet. Think of numbers."), l("Eins, Zwei, Drei …", "One, Two, Three …")))),
                solution = listOf(l("Die Anfangsbuchstaben der Zahlwörter: Eins, Zwei, Drei, Vier, Fünf, Sechs, Sieben, Acht, Neun, Zehn.", "The first letters of the number words: One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten.")),
                background = l("Nicht jede Folge folgt einer Rechenregel. Manchmal steckt eine ganz andere Ordnung dahinter, und genau das macht solche Rätsel gemein. Mathematisch ist jede endliche Folge mit unendlich vielen Regeln verträglich. Gesucht ist immer die einfachste Erklärung, und was einfach ist, hängt vom Blickwinkel ab.", "Not every sequence follows an arithmetic rule. Sometimes a completely different order is behind it, which is exactly what makes such puzzles mean. Mathematically, every finite sequence is compatible with infinitely many rules. What you're looking for is always the simplest explanation, and what counts as simple depends on your point of view."),
            )
        },
        lab("ma.pascal", Topic.Sequences, M, 0.5f) {
            val row = pick(8, 10, 12)
            task(
                question = l("Im Pascalschen Dreieck ist jede Zahl die Summe der beiden darüber. Zeile 0 ist 1, Zeile 1 ist 1 1, Zeile 2 ist 1 2 1, Zeile 3 ist 1 3 3 1. Was ergibt die Summe aller Zahlen in Zeile ${row}?", "In Pascal's triangle every number is the sum of the two above it. Row 0 is 1, row 1 is 1 1, row 2 is 1 2 1, row 3 is 1 3 3 1. What is the sum of all the numbers in row ${row}?"),
                steps = listOf(num(l("Summe", "Sum"), 2.0.pow(row), "", abs = 0.0, done = """§2^{${row}} = ${m(2.0.pow(row), 0)}§""",
                    hints = listOf(l("Kernidee: Die Zeilensummen sind 1, 2, 4, 8, … Jede Zahl wird beim Bilden der nächsten Zeile zweimal verwendet.", "Key idea: the row sums are 1, 2, 4, 8, … Every number is used twice when building the next row."), """§\text{Summe}(n) = 2^n§""".let { l(it, it.replace("Summe", "sum")) }))),
                solution = listOf("""§2^{${row}} = ${m(2.0.pow(row), 0)}§"""),
                background = l("Die Zahlen im Pascalschen Dreieck sind Binomialkoeffizienten: Die k-te Zahl in Zeile n zählt, auf wie viele Arten man k Dinge aus n auswählen kann. Die Zeilensumme zählt also alle möglichen Auswahlen überhaupt, und für jedes Ding gibt es zwei Möglichkeiten, dabei oder nicht. Das ergibt 2ⁿ.", "The numbers in Pascal's triangle are binomial coefficients: the k-th number in row n counts the ways to choose k things out of n. So the row sum counts all possible selections at all, and for every item there are two options, in or out. That gives 2ⁿ."),
            )
        },
        lab("ma.alternierend", Topic.Sequences, M, 0.5f) {
            val k = pick(100, 50, 200)
            task(
                question = l("""Rechne geschickt: §1 - 2 + 3 - 4 + \dots + ${k - 1} - ${k}§""", """Calculate cleverly: §1 - 2 + 3 - 4 + \dots + ${k - 1} - ${k}§"""),
                steps = listOf(num(l("Ergebnis", "Result"), -k / 2.0, "", abs = 0.0, done = """§${-k / 2}§""",
                    hints = listOf(l("Kernidee: Fasse immer zwei Nachbarn zusammen: (1 − 2), (3 − 4), …", "Key idea: always group two neighbours together: (1 − 2), (3 − 4), …"), l("Jedes Paar ergibt −1. Wie viele Paare sind es?", "Each pair gives −1. How many pairs are there?"))),
                    ),
                solution = listOf("""§(1 - 2) + (3 - 4) + \dots = ${k / 2} \cdot (-1) = ${-k / 2}§"""),
                background = l("Geschicktes Gruppieren macht aus einer langen Rechnung eine kurze. Bei endlichen Summen darf man beliebig umordnen. Bei unendlichen alternierenden Reihen ist Vorsicht geboten: Die Reihe 1 − 1 + 1 − 1 + … hat je nach Klammerung den Wert 0 oder 1, und manche Reihen kann man durch Umordnen sogar auf jeden beliebigen Wert bringen.", "Clever grouping turns a long calculation into a short one. With finite sums you may reorder however you like. With infinite alternating series you have to be careful: the series 1 − 1 + 1 − 1 + … gives 0 or 1 depending on the brackets, and some series can even be rearranged to give any value you like."),
            )
        },
        lab("ma.quadratsumme", Topic.Sequences, M, 0.6f) {
            val k = pick(10, 12, 20); val v = k * (k + 1) * (2 * k + 1) / 6
            task(
                question = l("""Wie groß ist §1^2 + 2^2 + 3^2 + \dots + ${k}^2§?""", """What is §1^2 + 2^2 + 3^2 + \dots + ${k}^2§?"""),
                steps = listOf(num(l("Summe", "Sum"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Für die Summe der Quadratzahlen gibt es eine feste Formel.", "Key idea: there's a fixed formula for the sum of the square numbers."), """§\sum_{k=1}^{n} k^2 = \dfrac{n(n + 1)(2n + 1)}{6}§"""))),
                solution = listOf("""§\dfrac{${k} \cdot ${k + 1} \cdot ${2 * k + 1}}{6} = ${v}§"""),
                background = l("Die Formel lässt sich mit vollständiger Induktion beweisen oder mit einem Bild aus Würfeln, die man zu einem Quader zusammensetzt. Sie steckt auch in der Integralrechnung: Archimedes berechnete damit die Fläche unter einer Parabel, fast 2000 Jahre bevor es Integrale gab.", "The formula can be proved by induction or with a picture of cubes assembled into a cuboid. It's also hidden in integral calculus: Archimedes used it to calculate the area under a parabola, almost 2000 years before integrals existed."),
            )
        },
        lab("ma.kubiksumme", Topic.Sequences, M, 0.6f) {
            val k = pick(5, 10, 8); val t = k * (k + 1) / 2; val v = t * t
            task(
                question = l("""Berechne §1^3 + 2^3 + 3^3 + \dots + ${k}^3§. Tipp: Vergleiche mit §(1 + 2 + \dots + ${k})^2§.""", """Calculate §1^3 + 2^3 + 3^3 + \dots + ${k}^3§. Tip: compare it with §(1 + 2 + \dots + ${k})^2§."""),
                steps = listOf(num(l("Summe", "Sum"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Die Summe der ersten n Kubikzahlen ist genau das Quadrat der Summe der ersten n Zahlen.", "Key idea: the sum of the first n cubes is exactly the square of the sum of the first n numbers."), """§1 + 2 + \dots + ${k} = ${t}§"""))),
                solution = listOf("""§1^3 + \dots + ${k}^3 = (1 + \dots + ${k})^2 = ${t}^2 = ${v}§"""),
                background = l("Das ist eines der schönsten Zufallsergebnisse der Zahlentheorie, wobei es natürlich kein Zufall ist: 1 = 1², 1 + 8 = 9 = 3², 1 + 8 + 27 = 36 = 6². Man kann es mit Quadraten veranschaulichen, die man wie Bilderrahmen um ein wachsendes Quadrat legt. Der Satz heißt nach Nikomachos von Gerasa.", "This is one of the most beautiful results in number theory, though of course it's no accident: 1 = 1², 1 + 8 = 9 = 3², 1 + 8 + 27 = 36 = 6². You can picture it with squares laid like picture frames around a growing square. The theorem is named after Nicomachus of Gerasa."),
            )
        },
        lab("ma.sitzreihen", Topic.Sequences, M, 0.5f) {
            val first = pick(20, 16, 24); val inc = pick(2, 1, 3); val rows = pick(25, 20, 30)
            val total = rows * (2 * first + (rows - 1) * inc) / 2
            task(
                question = l("In einem Theater hat die erste Reihe ${first} Plätze, jede weitere Reihe ${inc} mehr. Es gibt ${rows} Reihen. Wie viele Plätze hat das Theater?", "In a theatre the first row has ${first} seats and every further row ${inc} more. There are ${rows} rows. How many seats does the theatre have?"),
                steps = listOf(num(l("Plätze", "Seats"), total.toDouble(), "", abs = 0.0, done = """§${total}§""",
                    hints = listOf(l("Kernidee: Erste und letzte Reihe zusammen haben so viele Plätze wie zweite und vorletzte. Gauß' Trick: Durchschnitt mal Anzahl.", "Key idea: the first and last rows together have as many seats as the second and second-to-last. Gauss's trick: average times number."), """§S = \dfrac{n}{2}(a_1 + a_n),\quad a_n = ${first} + ${rows - 1} \cdot ${inc} = ${first + (rows - 1) * inc}§"""))),
                solution = listOf("""§S = \dfrac{${rows}}{2} \cdot (${first} + ${first + (rows - 1) * inc}) = ${total}§"""),
                background = l("Der junge Carl Friedrich Gauß soll in der Schule die Zahlen von 1 bis 100 in Sekunden addiert haben, indem er sie paarweise zusammenfasste: 1 + 100, 2 + 99, … Jedes Paar ergibt dasselbe. Mit diesem Trick summiert man jede arithmetische Folge: Mittelwert aus erstem und letztem Glied mal Anzahl.", "Young Carl Friedrich Gauss is said to have added the numbers from 1 to 100 at school in seconds by pairing them up: 1 + 100, 2 + 99, … Each pair gives the same. With this trick you can sum any arithmetic sequence: the average of the first and last term times the number of terms."),
            )
        },
        lab("ma.dreierpotenz", Topic.Sequences, M, 0.6f) {
            val q = pick(3, 4, 5); val k = pick(6, 5); val v = ((q.toDouble().pow(k) - 1) / (q - 1)).toInt()
            task(
                question = l("""Berechne geschickt: §1 + ${q} + ${q * q} + \dots + ${q}^{${k - 1}}§""", """Calculate cleverly: §1 + ${q} + ${q * q} + \dots + ${q}^{${k - 1}}§"""),
                steps = listOf(num(l("Summe", "Sum"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Nimm die Summe S mal ${q}. Dann verschieben sich alle Glieder um eins. Zieh S ab, fast alles hebt sich weg.", "Key idea: multiply the sum S by ${q}. All the terms shift by one place. Subtract S, and almost everything cancels."), """§${q}S - S = ${q}^{${k}} - 1§"""))),
                solution = listOf("""§S = \dfrac{${q}^{${k}} - 1}{${q} - 1} = \dfrac{${m(q.toDouble().pow(k), 0)} - 1}{${q - 1}} = ${v}§"""),
                background = l("Die Summenformel der geometrischen Reihe entsteht aus einem Trick: Verschiebt man die Summe durch Malnehmen mit dem Faktor, überlappt sie fast vollständig mit sich selbst. Beim Abziehen bleiben nur das erste und das letzte Glied übrig. Derselbe Trick erklärt, warum Kredite mit Zinseszins so teuer werden.", "The formula for a geometric series comes from a trick: if you shift the sum by multiplying by the ratio, it overlaps almost completely with itself. When you subtract, only the first and the last terms remain. The same trick explains why loans with compound interest get so expensive."),
            )
        },
        lab("ma.lookandsay", Topic.Sequences, H, 0.7f) {
            task(
                question = l("Wie geht diese Folge weiter? 1, 11, 21, 1211, 111221, …", "How does this sequence continue? 1, 11, 21, 1211, 111221, …"),
                steps = listOf(pick1(l("Nächstes Glied", "Next term"), "312211", listOf("111231", "122111", "1112211"),
                    hints = listOf(l("Kernidee: Lies jedes Glied laut vor und beschreibe, was du siehst.", "Key idea: read each term aloud and describe what you see."), l("„11“ heißt: zwei Einsen, also 21. „21“ heißt: eine Zwei, eine Eins.", "'11' means: two ones, so 21. '21' means: one two, one one.")))),
                solution = listOf(l("111221 ist: drei Einsen, zwei Zweien, eine Eins. Also 31 22 11 = 312211.", "111221 is: three ones, two twos, one one. So 31 22 11 = 312211.")),
                background = l("Das ist die Ansage-Folge (look and say): Jedes Glied beschreibt das vorige. Sie sieht chaotisch aus, aber John Conway bewies 1986, dass die Länge der Glieder von Schritt zu Schritt um einen festen Faktor wächst, etwa 1,3036. In der Folge kommt nie eine Ziffer größer als 3 vor.", "This is the look-and-say sequence: each term describes the previous one. It looks chaotic, but in 1986 John Conway proved that the length of the terms grows by a fixed factor from step to step, about 1.3036. No digit larger than 3 ever appears in the sequence."),
            )
        },
        lab("ma.harmonisch", Topic.Sequences, H, 0.6f) {
            task(
                question = l("""Die Glieder der Reihe §1 + \dfrac{1}{2} + \dfrac{1}{3} + \dfrac{1}{4} + \dots§ werden immer kleiner und gehen gegen null. Was passiert mit der Summe?""", """The terms of the series §1 + \dfrac{1}{2} + \dfrac{1}{3} + \dfrac{1}{4} + \dots§ keep getting smaller and tend to zero. What happens to the sum?"""),
                steps = listOf(pick1(l("Die Summe …", "The sum …"), l("wächst über jede Grenze, nur sehr langsam", "grows beyond any bound, only very slowly"), listOf(l("nähert sich 2", "approaches 2"), l("nähert sich e ≈ 2,718", "approaches e ≈ 2.718"), l("nähert sich π²/6", "approaches π²/6")),
                    hints = listOf(l("Kernidee: Fasse Gruppen zusammen: 1/3 + 1/4 ist größer als 1/2, 1/5 bis 1/8 auch, 1/9 bis 1/16 auch …", "Key idea: group the terms: 1/3 + 1/4 is more than 1/2, so are 1/5 to 1/8, and 1/9 to 1/16 …"), l("Wie oft kann man ½ addieren?", "How often can you add ½?")))),
                solution = listOf(l("Jede Gruppe von 1/(2ᵏ + 1) bis 1/2ᵏ⁺¹ ergibt mehr als ½. Es gibt unendlich viele solche Gruppen, also wächst die Summe unbegrenzt. Kleine Glieder allein reichen nicht für eine endliche Summe.", "Each group from 1/(2ᵏ + 1) to 1/2ᵏ⁺¹ adds up to more than ½. There are infinitely many such groups, so the sum grows without limit. Small terms alone aren't enough for a finite sum.")),
                background = l("Dass die Glieder gegen null gehen, ist nötig, aber nicht genug für eine endliche Summe. Die harmonische Reihe wächst ungefähr wie ln n: Für eine Summe über 10 braucht man über 12.000 Glieder, für über 100 mehr Glieder, als es Atome im Universum gibt. Den Beweis mit den Gruppen fand Nicole Oresme schon um 1350.", "That the terms go to zero is necessary but not enough for a finite sum. The harmonic series grows roughly like ln n: for a sum over 10 you need over 12,000 terms, for over 100 more terms than there are atoms in the universe. Nicole Oresme found the proof with groups as early as around 1350."),
            )
        },
        lab("ma.quadrategitter", Topic.Sequences, H, 0.6f) {
            val k = pick(4, 5, 8); val v = k * (k + 1) * (2 * k + 1) / 6
            task(
                question = l("Wie viele Quadrate aller Größen findest du in einem Gitter aus ${k} × ${k} Kästchen${if (k == 8) " (also auf einem Schachbrett)" else ""}?", "How many squares of all sizes can you find in a grid of ${k} × ${k} boxes${if (k == 8) " (i.e. on a chessboard)" else ""}?"),
                steps = listOf(num(l("Anzahl Quadrate", "Number of squares"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Zähle getrennt nach Größe. Ein 1×1-Quadrat passt ${k}·${k}-mal, ein 2×2-Quadrat ${k - 1}·${k - 1}-mal, …", "Key idea: count by size separately. A 1×1 square fits ${k}·${k} times, a 2×2 square ${k - 1}·${k - 1} times, …"), """§${k}^2 + ${k - 1}^2 + \dots + 1^2§"""))),
                solution = listOf("""§\sum_{j=1}^{${k}} j^2 = ${v}§"""),
                background = l("Das typische Vorgehen bei Zählaufgaben: Man teilt die Möglichkeiten in übersichtliche Gruppen auf, zählt jede Gruppe für sich und addiert. Ein Quadrat der Seitenlänge s kann seine linke obere Ecke an (n − s + 1)² Stellen haben. Die Summe sind die Quadratzahlen bis n².", "The typical approach to counting problems: split the possibilities into manageable groups, count each group separately and add. A square of side s can have its top-left corner in (n − s + 1)² positions. The total is the sum of the square numbers up to n²."),
            )
        },
        lab("ma.wurzelkette", Topic.Sequences, H, 0.7f) {
            val k = pick(2, 3, 4); val a = k * (k - 1)
            task(
                question = l("""Welchen Wert hat der unendliche Ausdruck §\sqrt{${a} + \sqrt{${a} + \sqrt{${a} + \dots}}}§?""", """What is the value of the infinite expression §\sqrt{${a} + \sqrt{${a} + \sqrt{${a} + \dots}}}§?"""),
                steps = listOf(num(l("Wert", "Value"), k.toDouble(), "", abs = 0.001, done = """§${k}§""",
                    hints = listOf(l("Kernidee: Nenne den ganzen Ausdruck x. Unter der ersten Wurzel steht dann wieder genau x.", "Key idea: call the whole expression x. Then under the first square root there is exactly x again."), """§x = \sqrt{${a} + x} \Rightarrow x^2 = ${a} + x§"""))),
                solution = listOf("""§x^2 - x - ${a} = 0 \Rightarrow x = ${k}\ (\text{die negative Lösung entfällt})§""".let { l(it, it.replace("die negative Lösung entfällt", "the negative solution is ruled out")) }),
                background = l("Unendliche Ausdrücke, die sich selbst enthalten, lassen sich oft mit einem Trick lösen: Man gibt dem Ganzen einen Namen und findet ihn in sich selbst wieder. Man muss aber zeigen, dass der Ausdruck überhaupt gegen etwas strebt. Hier wird jede Näherung größer, bleibt aber unter ${k}, also gibt es einen Grenzwert.", "Infinite expressions that contain themselves can often be solved with a trick: give the whole thing a name and find it again inside itself. But you have to show that the expression tends to something at all. Here each approximation gets bigger but stays below ${k}, so there is a limit."),
            )
        },
        lab("ma.eulerlimes", Topic.Sequences, X, 0.6f) {
            val nn = pick(10, 100, 1000); val v = (1 + 1.0 / nn).pow(nn)
            task(
                question = l("""Eine Bank zahlt 100 % Zinsen im Jahr, aber verteilt auf ${nn} gleiche Zinstermine, jedes Mal mit Zinseszins. Aus 1 € wird §\left(1 + \dfrac{1}{${nn}}\right)^{${nn}}§ €. Wie viel ist das?""", """A bank pays 100% interest a year, but split into ${nn} equal payments, each time with compound interest. €1 becomes €§\left(1 + \dfrac{1}{${nn}}\right)^{${nn}}§. How much is that?"""),
                steps = listOf(num(l("Betrag in Euro", "Amount in euros"), v, "€", abs = 0.0005, done = """§\approx ${m(v, 4)}§""",
                    hints = listOf(l("Kernidee: Taschenrechner benutzen. Achte auf die Klammern: erst 1 + 1/${nn}, dann hoch ${nn}.", "Key idea: use the calculator. Watch the brackets: first 1 + 1/${nn}, then to the power of ${nn}."), """§\left(${m(1 + 1.0 / nn, 4)}\right)^{${nn}}§"""))),
                solution = listOf("""§\left(1 + \dfrac{1}{${nn}}\right)^{${nn}} \approx ${m(v, 4)}§""", l("""Für immer mehr Termine nähert sich der Wert §e \approx 2{,}71828§.""", """With more and more payments the value approaches §e \approx 2{,}71828§.""")),
                background = l("Jakob Bernoulli stellte 1683 genau diese Frage nach den Zinsen und entdeckte dabei die Zahl e. Mehr Zinstermine bringen mehr, aber der Gewinn wächst nicht unbegrenzt: Selbst bei unendlich vielen Terminen, also stetiger Verzinsung, wird aus 1 € nur e ≈ 2,72 €. Die Zahl e ist die Basis des natürlichen Wachstums.", "In 1683 Jacob Bernoulli asked exactly this question about interest and discovered the number e in doing so. More payments bring more, but the gain doesn't grow without limit: even with infinitely many payments, i.e. continuous compounding, €1 only becomes e ≈ €2.72. The number e is the base of natural growth."),
            )
        },
        lab("ma.fakultaetnullen", Topic.Sequences, X, 0.7f) {
            val nn = pick(100, 50, 30, 200)
            var z = 0; var p = 5; val parts = mutableListOf<Int>(); while (p <= nn) { z += nn / p; parts += nn / p; p *= 5 }
            val digits = kotlin.math.floor((1..nn).sumOf { log10(it.toDouble()) }).toInt() + 1
            task(
                question = l("""Mit wie vielen Nullen endet §${nn}! = 1 \cdot 2 \cdot 3 \cdots ${nn}§?""", """How many zeros does §${nn}! = 1 \cdot 2 \cdot 3 \cdots ${nn}§ end with?"""),
                steps = listOf(num(l("Anzahl Nullen am Ende", "Number of trailing zeros"), z.toDouble(), "", abs = 0.0, done = """§${z}§""",
                    hints = listOf(l("Kernidee: Jede Null am Ende kommt von einem Faktor 10 = 2 · 5. Zweien gibt es reichlich, also zählen nur die Fünfen.", "Key idea: every trailing zero comes from a factor 10 = 2 · 5. There are plenty of twos, so only the fives count."), l("Vielfache von 25 bringen zwei Fünfen, Vielfache von 125 sogar drei.", "Multiples of 25 bring two fives, multiples of 125 even three."))),
                    ),
                solution = listOf(l("Vielfache von 5, 25, 125 … bis ${nn}: ${parts.joinToString(" + ")} = ${z}.", "Multiples of 5, 25, 125 … up to ${nn}: ${parts.joinToString(" + ")} = ${z}.")),
                background = l("Fakultäten wachsen gigantisch: ${nn}! hat ${digits} Stellen. Trotzdem kann man ohne Ausrechnen sagen, wie viele Nullen am Ende stehen, indem man Primfaktoren zählt. Diese Idee, ein riesiges Problem auf eine einfache Eigenschaft zu reduzieren, ist typisch für die Zahlentheorie.", "Factorials grow enormously: ${nn}! has ${digits} digits. Even so, you can say how many zeros it ends with without calculating it, just by counting prime factors. Reducing a huge problem to a simple property like this is typical of number theory."),
            )
        },
    )
}
