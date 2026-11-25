package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

// =============================================================================================
// Mathe-Vorlagen (seit 12.0): Kopfrechnen mit Tricks, Prozent im Alltag, Gleichungen, Geometrie und
// Wahrscheinlichkeit. Jede Vorlage würfelt bei jedem Ziehen neue Zahlen; auf höheren Stufen (tier) werden
// die Zahlen größer oder krummer. Erklärungen kurz und mit dem "Warum".
// =============================================================================================

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private object MBg {
    val split get() = l(
        "Kopfrechnen ist vor allem geschicktes Zerlegen: Man formt eine Aufgabe so um, dass nur noch runde Zahlen übrig bleiben. 25 · 36 wird zu 100 · 9, 48² zu (50 − 2)². Wer zwei, drei solcher Tricks kennt, rechnet vieles schneller im Kopf als mit dem Taschenrechner.",
        "Mental maths is mostly clever splitting: you rewrite a problem until only round numbers are left. 25 · 36 becomes 100 · 9, 48² becomes (50 − 2)². Knowing two or three such tricks lets you do a lot faster in your head than on a calculator.",
    )
    val binomial get() = l(
        "Die binomischen Formeln sind Kopfrechen-Werkzeuge: (a − b)² = a² − 2ab + b² und (a + b)(a − b) = a² − b². Mit runden Zahlen für a werden schwierige Produkte plötzlich leicht, etwa 31 · 29 = 30² − 1² = 899.",
        "The binomial formulas are mental-maths tools: (a − b)² = a² − 2ab + b² and (a + b)(a − b) = a² − b². With a round number for a, hard products suddenly become easy, such as 31 · 29 = 30² − 1² = 899.",
    )
    val gauss get() = l(
        "Der Legende nach sollte der junge Carl Friedrich Gauß die Zahlen von 1 bis 100 addieren und war nach Sekunden fertig: Er paarte die erste mit der letzten Zahl (1 + 100), die zweite mit der vorletzten (2 + 99) und so weiter. Jedes Paar ergibt 101, und es gibt 50 Paare: 5050.",
        "Legend has it that young Carl Friedrich Gauss was asked to add the numbers from 1 to 100 and was done in seconds: he paired the first with the last (1 + 100), the second with the second-to-last (2 + 99) and so on. Each pair makes 101, and there are 50 pairs: 5050.",
    )
    val fraction get() = l(
        "Brüche addiert man, indem man sie erst auf denselben Nenner bringt: Man sucht ein gemeinsames Vielfaches der Nenner und erweitert beide Brüche. Dann werden nur noch die Zähler addiert. Zum Vergleichen hilft dasselbe – oder man rechnet beide in Dezimalzahlen um.",
        "To add fractions, first bring them to the same denominator: find a common multiple of the denominators and scale both fractions up. Then you only add the numerators. The same helps for comparing – or you convert both to decimals.",
    )
    val percent get() = l(
        "Prozent heißt „von Hundert“. 20 % von 80 € sind 20/100 · 80 € = 16 €. Praktisch ist der Faktor: Ein Rabatt von 20 % heißt mal 0,8, ein Aufschlag von 19 % heißt mal 1,19. Mehrere Änderungen hintereinander multipliziert man einfach.",
        "Percent means \"per hundred\". 20 % of €80 is 20/100 · €80 = €16. The factor is handy: a 20 % discount means times 0.8, a 19 % surcharge means times 1.19. Several changes in a row simply multiply.",
    )
    val compound get() = l(
        "Beim Zinseszins werden die Zinsen mitverzinst. Nach n Jahren hat man K · (1 + p)ⁿ. Das wächst anfangs langsam und dann immer schneller – Albert Einstein soll den Zinseszins das achte Weltwunder genannt haben (das Zitat ist allerdings nicht belegt). Faustregel: Bei p % verdoppelt sich das Geld nach etwa 72/p Jahren.",
        "With compound interest, the interest earns interest too. After n years you have K · (1 + p)ⁿ. It grows slowly at first and then faster and faster – Albert Einstein supposedly called it the eighth wonder of the world (the quote is not documented, though). Rule of thumb: at p %, money doubles in about 72/p years.",
    )
    val proportion get() = l(
        "Dreisatz: Erst auf eine Einheit zurückrechnen, dann auf die gesuchte Menge. Bei proportionalen Größen (doppelt so viele Gäste, doppelt so viel Mehl) wird multipliziert und geteilt wie erwartet. Bei antiproportionalen (doppelt so viele Helfer, halb so lange) ist das Produkt konstant.",
        "Rule of three: first scale back to one unit, then to the amount you want. For proportional quantities (twice as many guests, twice as much flour) you multiply and divide as expected. For inversely proportional ones (twice as many helpers, half the time) the product stays constant.",
    )
    val equation get() = l(
        "Eine Gleichung ist wie eine Waage im Gleichgewicht: Was man auf einer Seite tut, muss man auch auf der anderen tun. So räumt man Schritt für Schritt alles weg, bis x allein steht. Die Probe – Lösung einsetzen – zeigt sofort, ob man sich verrechnet hat.",
        "An equation is like a balanced scale: whatever you do on one side, you must do on the other. Step by step you clear everything away until x stands alone. Checking – plugging the solution back in – shows immediately whether you made a mistake.",
    )
    val system get() = l(
        "Zwei Unbekannte brauchen zwei Gleichungen. Ein schneller Weg: die Gleichungen addieren oder subtrahieren, sodass eine Unbekannte wegfällt (Additionsverfahren). Hat man die eine, setzt man sie in eine der Gleichungen ein und bekommt die andere.",
        "Two unknowns need two equations. A quick way: add or subtract the equations so that one unknown drops out (elimination). Once you have one, plug it into either equation to get the other.",
    )
    val quadratic get() = l(
        "Für x² + px + q = 0 gibt es den Satz von Vieta: Die beiden Lösungen ergeben addiert −p und multipliziert q. Oft findet man sie damit schneller als mit der Lösungsformel: Welche zwei Zahlen haben Produkt q und Summe −p?",
        "For x² + px + q = 0 there's Vieta's theorem: the two solutions add up to −p and multiply to q. This often finds them faster than the quadratic formula: which two numbers have product q and sum −p?",
    )
    val pythagoras get() = l(
        "Im rechtwinkligen Dreieck gilt a² + b² = c²: Die Quadrate über den beiden kurzen Seiten haben zusammen genau die Fläche des Quadrats über der langen. Babylonier nutzten das schon 1000 Jahre vor Pythagoras. Berühmte ganzzahlige Beispiele sind 3-4-5 und 5-12-13.",
        "In a right triangle a² + b² = c²: the squares on the two short sides together have exactly the area of the square on the long side. The Babylonians used this 1000 years before Pythagoras. Famous whole-number examples are 3-4-5 and 5-12-13.",
    )
    val circle get() = l(
        "Kreise hängen alle über eine Zahl zusammen: π ≈ 3,14159. Der Umfang ist π · d (oder 2πr), die Fläche π · r². Weil der Radius in der Fläche quadriert wird, hat eine Pizza mit doppeltem Durchmesser viermal so viel Fläche.",
        "All circles are linked by one number: π ≈ 3.14159. The circumference is π · d (or 2πr), the area π · r². Because the radius is squared in the area, a pizza with twice the diameter has four times the area.",
    )
    val angles get() = l(
        "Die Winkel in einem Dreieck ergeben immer zusammen 180°. Ein Vieleck lässt sich von einer Ecke aus in (n − 2) Dreiecke zerlegen, deshalb ist seine Winkelsumme (n − 2) · 180°. Beim Sechseck sind das 720°, also 120° pro Ecke – deshalb passen Bienenwaben so lückenlos.",
        "The angles of a triangle always add up to 180°. A polygon can be split from one corner into (n − 2) triangles, so its angle sum is (n − 2) · 180°. For a hexagon that's 720°, i.e. 120° per corner – which is why honeycombs fit together without gaps.",
    )
    val similar get() = l(
        "Ähnliche Dreiecke haben dieselben Winkel, und ihre Seiten stehen im selben Verhältnis. So maß schon Thales von Milet die Höhe der Pyramiden: Er verglich ihren Schatten mit dem Schatten eines Stabes bekannter Länge, zur selben Tageszeit.",
        "Similar triangles have the same angles, and their sides are in the same ratio. This is how Thales of Miletus is said to have measured the height of the pyramids: he compared their shadow with the shadow of a stick of known length at the same time of day.",
    )
    val volume get() = l(
        "Volumen ist Grundfläche mal Höhe – beim Quader, beim Zylinder, bei jedem Prisma. Ein Liter ist genau ein Würfel mit 10 cm Kantenlänge (1 dm³), ein Kubikmeter sind also 1000 Liter.",
        "Volume is base area times height – for a box, a cylinder, any prism. One litre is exactly a cube with 10 cm sides (1 dm³), so a cubic metre is 1000 litres.",
    )
    val probability get() = l(
        "Wahrscheinlichkeit ist „günstige durch mögliche Fälle“, wenn alle Fälle gleich wahrscheinlich sind. Bei zwei Würfeln gibt es 36 gleich wahrscheinliche Paare; die Summe 7 kommt am häufigsten vor (6 von 36). Für „mindestens einmal“ rechnet man meist besser über das Gegenteil: 1 minus „keinmal“.",
        "Probability is \"favourable over possible outcomes\" when all outcomes are equally likely. With two dice there are 36 equally likely pairs; a sum of 7 is the most common (6 of 36). For \"at least once\" it's usually easier to go via the opposite: 1 minus \"never\".",
    )
    val counting get() = l(
        "Kombinatorik ist geschicktes Zählen. Für eine Reihenfolge von n Dingen gibt es n! = n · (n − 1) · … · 1 Möglichkeiten. Wählt man k aus n ohne Reihenfolge, sind es „n über k“ = n! / (k! · (n − k)!). Diese Zahlen werden schnell riesig: 52 Karten lassen sich auf mehr Arten mischen, als es Atome auf der Erde gibt.",
        "Combinatorics is clever counting. There are n! = n · (n − 1) · … · 1 ways to order n things. Choosing k of n without order gives \"n choose k\" = n! / (k! · (n − k)!). These numbers get huge fast: 52 cards can be shuffled in more ways than there are atoms on Earth.",
    )
    val expected get() = l(
        "Der Erwartungswert ist der Durchschnitt, den man auf lange Sicht pro Versuch bekommt: jedes Ergebnis mal seine Wahrscheinlichkeit, alles addiert. Bei einem fairen Würfel ist das 3,5 – eine Zahl, die man nie würfelt. Glücksspiele haben für Spieler fast immer einen negativen Erwartungswert.",
        "The expected value is the average you get per try in the long run: each outcome times its probability, all added up. For a fair die it's 3.5 – a number you can never roll. Games of chance almost always have a negative expected value for players.",
    )
}

private object MFc {
    val percent get() = card(l("Prozent", "Percent"), """§W = G \cdot \dfrac{p}{100}§""", l("Änderung um p %: mal (1 ± p/100).", "Change by p %: times (1 ± p/100)."))
    val compound get() = card(l("Zinseszins", "Compound interest"), """§K_n = K_0 \cdot (1 + p)^n§""", l("p als Dezimalzahl, z. B. 3 % = 0,03.", "p as a decimal, e.g. 3 % = 0.03."))
    val pythagoras get() = card(l("Satz des Pythagoras", "Pythagorean theorem"), """§a^2 + b^2 = c^2§""", l("c ist die längste Seite, gegenüber dem rechten Winkel.", "c is the longest side, opposite the right angle."))
    val circle get() = card(l("Kreis", "Circle"), """§U = 2\pi r,\quad A = \pi r^2§""", l("π ≈ 3,1416", "π ≈ 3.1416"))
    val vieta get() = card(l("Satz von Vieta", "Vieta's theorem"), """§x_1 + x_2 = -p,\quad x_1 \cdot x_2 = q§""", l("Für x² + px + q = 0.", "For x² + px + q = 0."))
    val binom get() = card(l("Binomische Formeln", "Binomial formulas"), """§(a \pm b)^2 = a^2 \pm 2ab + b^2§""", l("Und (a + b)(a − b) = a² − b².", "And (a + b)(a − b) = a² − b²."))
    val choose get() = card(l("Binomialkoeffizient", "Binomial coefficient"), """§\dbinom{n}{k} = \dfrac{n!}{k!\,(n-k)!}§""", l("k aus n ohne Reihenfolge.", "k of n, order doesn't matter."))
}

private fun factorial(n: Int): Double = (1..n).fold(1.0) { a, b -> a * b }
private fun choose(n: Int, k: Int): Double = factorial(n) / (factorial(k) * factorial(n - k))
private fun gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)

object LabMath2 {
    val all: List<TaskDef> by lazy { mental() + percent() + equations() + geometry() + probability() }

    // ----- Kopfrechnen -----

    private fun mental(): List<TaskDef> = listOf(
        lab("m.mal25", Topic.Mental, E, 0.2f) {
            val k = int(3, if (hard) 39 else 19) * 4
            val r = 25 * k
            task(
                question = l("Rechne im Kopf: §25 \\cdot ${m(k)}§", "Work it out in your head: §25 \\cdot ${m(k)}§"),
                steps = listOf(num(l("Ergebnis", "Result"), r.toDouble(), abs = 0.0, done = "§25 \\cdot ${m(k)} = ${m(r)}§",
                    hints = listOf(l("Kernidee: 25 ist ein Viertel von 100.", "Key idea: 25 is a quarter of 100."), l("Rechne ${n(k)} : 4 und hänge zwei Nullen an.", "Work out ${n(k)} ÷ 4 and add two zeros.")))),
                solution = listOf("§25 \\cdot ${m(k)} = 100 \\cdot ${m(k / 4)} = ${m(r)}§"),
                background = MBg.split,
            )
        },
        lab("m.mal11", Topic.Mental, E, 0.25f) {
            val a = int(12, 98)
            val r = 11 * a
            task(
                question = l("Rechne im Kopf: §11 \\cdot ${m(a)}§", "Work it out in your head: §11 \\cdot ${m(a)}§"),
                steps = listOf(num(l("Ergebnis", "Result"), r.toDouble(), abs = 0.0, done = "§11 \\cdot ${m(a)} = ${m(r)}§",
                    hints = listOf(l("Kernidee: 11 · a = 10 · a + a.", "Key idea: 11 · a = 10 · a + a."), "§${m(10 * a)} + ${m(a)}§"))),
                solution = listOf("§11 \\cdot ${m(a)} = ${m(10 * a)} + ${m(a)} = ${m(r)}§"),
                background = MBg.split,
                fact = l("Bei zweistelligen Zahlen geht es noch schneller: Die Quersumme kommt in die Mitte. 11 · 43 = 4|7|3 = 473.", "For two-digit numbers it's even faster: the digit sum goes in the middle. 11 · 43 = 4|7|3 = 473."),
            )
        },
        lab("m.quadrat", Topic.Mental, M, 0.4f) {
            val base = if (hard) pick(50, 100, 70, 30) else pick(50, 100)
            val d = int(1, if (hard) 4 else 3) * pick(-1, 1)
            val x = base + d
            val r = x * x
            task(
                question = l("Rechne im Kopf: §${m(x)}^2§", "Work it out in your head: §${m(x)}^2§"),
                steps = listOf(num(l("Ergebnis", "Result"), r.toDouble(), abs = 0.0, done = "§${m(x)}^2 = ${m(r)}§",
                    hints = listOf(l("Kernidee: ${n(x)} = ${n(base)} ${if (d > 0) "+" else "−"} ${abs(d)}. Dann binomische Formel.", "Key idea: ${n(x)} = ${n(base)} ${if (d > 0) "+" else "−"} ${abs(d)}. Then the binomial formula."),
                        "§${m(base * base)} ${if (d > 0) "+" else "-"} ${m(2 * base * abs(d))} + ${m(d * d)}§"), formula = MFc.binom)),
                solution = listOf("§(${m(base)} ${if (d > 0) "+" else "-"} ${abs(d)})^2 = ${m(base * base)} ${if (d > 0) "+" else "-"} ${m(2 * base * abs(d))} + ${m(d * d)} = ${m(r)}§"),
                background = MBg.binomial,
            )
        },
        lab("m.dritte", Topic.Mental, M, 0.45f) {
            val a = pick(20, 30, 40, 50, 60, 70, 80, 90, 100)
            val b = int(1, if (hard) 6 else 3)
            val r = (a + b) * (a - b)
            task(
                question = l("Rechne im Kopf: §${m(a + b)} \\cdot ${m(a - b)}§", "Work it out in your head: §${m(a + b)} \\cdot ${m(a - b)}§"),
                steps = listOf(num(l("Ergebnis", "Result"), r.toDouble(), abs = 0.0, done = "§${m(a + b)} \\cdot ${m(a - b)} = ${m(r)}§",
                    hints = listOf(l("Kernidee: Beide Zahlen liegen gleich weit um ${n(a)} herum.", "Key idea: both numbers are equally far from ${n(a)}."), "§(${m(a)} + $b)(${m(a)} - $b) = ${m(a)}^2 - $b^2§"), formula = MFc.binom)),
                solution = listOf("§(${m(a)} + $b)(${m(a)} - $b) = ${m(a * a)} - ${m(b * b)} = ${m(r)}§"),
                background = MBg.binomial,
            )
        },
        lab("m.gauss", Topic.Mental, M, 0.5f) {
            val nn = if (hard) pick(80, 100, 150, 200, 1000) else pick(10, 20, 30, 40, 50)
            val r = nn * (nn + 1) / 2
            task(
                question = l("Wie groß ist die Summe §1 + 2 + 3 + \\dots + ${m(nn)}§?", "What is the sum §1 + 2 + 3 + \\dots + ${m(nn)}§?"),
                steps = listOf(num(l("Summe", "Sum"), r.toDouble(), abs = 0.0, done = "§${m(r)}§",
                    hints = listOf(l("Kernidee: Erste und letzte Zahl zusammen, zweite und vorletzte zusammen …", "Key idea: first plus last, second plus second-to-last …"), l("Jedes Paar ergibt ${n(nn + 1)}, und es gibt ${n(nn / 2.0, 1)} Paare.", "Each pair makes ${n(nn + 1)}, and there are ${n(nn / 2.0, 1)} pairs.")),
                    formula = card(l("Gaußsche Summe", "Gauss sum"), "§1 + 2 + \\dots + n = \\dfrac{n\\,(n+1)}{2}§", l("Paare bilden.", "Make pairs.")))),
                solution = listOf("§\\dfrac{${m(nn)} \\cdot ${m(nn + 1)}}{2} = ${m(r)}§"),
                background = MBg.gauss,
            )
        },
        lab("m.rueckgeld", Topic.Mental, E, 0.2f) {
            val prices = List(3) { int(if (hard) 105 else 50, if (hard) 1490 else 600) / 5 * 5 }
            val sum = prices.sum()
            val paid = listOf(1000, 2000, 5000).first { it > sum }
            val back = paid - sum
            fun eur(c: Int) = n(c / 100.0, 2) + " €"
            task(
                question = l(
                    "Du kaufst ein Buch für ${eur(prices[0])}, ein Heft für ${eur(prices[1])} und Stifte für ${eur(prices[2])}. Du zahlst mit einem ${paid / 100}-Euro-Schein. Wie viel Rückgeld bekommst du?",
                    "You buy a book for ${eur(prices[0])}, a notebook for ${eur(prices[1])} and pens for ${eur(prices[2])}. You pay with a ${paid / 100}-euro note. How much change do you get?",
                ),
                steps = listOf(num(l("Rückgeld", "Change"), back / 100.0, "€", abs = 0.005, done = eur(back),
                    hints = listOf(l("Erst alles zusammenzählen, dann vom Schein abziehen.", "Add everything up first, then subtract from the note."), l("Zusammen: ${eur(sum)}", "Together: ${eur(sum)}")))),
                solution = listOf(l("Zusammen ${eur(sum)}, also ${eur(paid)} − ${eur(sum)} = ${eur(back)}.", "Together ${eur(sum)}, so ${eur(paid)} − ${eur(sum)} = ${eur(back)}.")),
                background = MBg.split,
                fact = l("Kassierer zählen Rückgeld oft „aufwärts“: vom Preis bis zum nächsten runden Betrag und dann bis zum Schein. Das ist dasselbe wie Abziehen, nur leichter.", "Cashiers often count change \"upwards\": from the price to the next round amount, then up to the note. It's the same as subtracting, just easier."),
            )
        },
        lab("m.uhrzeit", Topic.Mental, E, 0.3f) {
            val dep = int(6 * 60, 20 * 60) / 5 * 5
            val dur = int(if (hard) 95 else 40, if (hard) 400 else 200) / 5 * 5
            val arr = (dep + dur) % (24 * 60)
            fun hm(t: Int) = "%d:%02d".format(t / 60, t % 60)
            fun dur(t: Int) = if (de.funke.app.ui.Lang.german) "${t / 60} Std. ${t % 60} Min." else "${t / 60} h ${t % 60} min"
            val wrong = listOf(arr + 60, arr - 10, arr + 40).map { hm((it + 1440) % 1440) }.distinct().filter { it != hm(arr) }.take(3)
            task(
                question = l("Ein Zug fährt um ${hm(dep)} Uhr ab und ist ${dur(dur)} unterwegs. Wann kommt er an?", "A train leaves at ${hm(dep)} and travels for ${dur(dur)}. When does it arrive?"),
                steps = listOf(choice(l("Ankunft", "Arrival"), listOf(hm(arr)) + wrong, done = hm(arr),
                    hints = listOf(l("Erst die vollen Stunden dazu, dann die Minuten.", "Add the full hours first, then the minutes."), l("Über 60 Minuten? Dann eine Stunde mehr.", "Over 60 minutes? Then one more hour.")))),
                solution = listOf(l("${hm(dep)} + ${dur(dur)} = ${hm(arr)} Uhr", "${hm(dep)} + ${dur(dur)} = ${hm(arr)}")),
                background = MBg.split,
            )
        },
        lab("m.bruchplus", Topic.Mental, M, 0.5f) {
            val pairs = if (hard) listOf(3 to 4, 2 to 5, 4 to 6, 3 to 8, 5 to 6, 6 to 8) else listOf(2 to 3, 2 to 4, 3 to 4, 2 to 5, 3 to 6)
            val (a, b) = pick(pairs)
            val num = b + a
            val den = a * b
            val g = gcd(num, den)
            fun fr(p: Int, q: Int) = "§\\tfrac{$p}{$q}§"
            val right = fr(num / g, den / g)
            val wrong = listOf(fr(2, a + b), fr(1, den), fr(num / g + 1, den / g + 1)).distinct().filter { it != right }
            task(
                question = l("Rechne: §\\dfrac{1}{$a} + \\dfrac{1}{$b}§", "Work out: §\\dfrac{1}{$a} + \\dfrac{1}{$b}§"),
                steps = listOf(choice(l("Ergebnis (gekürzt)", "Result (simplified)"), listOf(right) + wrong.take(3), done = right,
                    hints = listOf(l("Kernidee: Erst gleicher Nenner, dann Zähler addieren.", "Key idea: common denominator first, then add the numerators."), "§\\dfrac{$b}{$den} + \\dfrac{$a}{$den}§"))),
                solution = listOf("§\\dfrac{1}{$a} + \\dfrac{1}{$b} = \\dfrac{$b}{$den} + \\dfrac{$a}{$den} = \\dfrac{$num}{$den}${if (g > 1) " = \\dfrac{${num / g}}{${den / g}}" else ""}§"),
                background = MBg.fraction,
                fact = l("Ein häufiger Fehler ist, Zähler und Nenner einfach getrennt zu addieren: 1/2 + 1/3 wäre dann 2/5 – weniger als 1/2 allein!", "A common mistake is adding numerators and denominators separately: 1/2 + 1/3 would then be 2/5 – less than 1/2 on its own!"),
            )
        },
        lab("m.bruchgross", Topic.Mental, E, 0.35f) {
            val cands = listOf(3 to 5, 5 to 8, 2 to 3, 7 to 10, 4 to 7, 5 to 9, 3 to 4, 7 to 12, 5 to 6, 4 to 9)
            val (p1, q1) = pick(cands)
            var p2q2 = pick(cands)
            while (p2q2 == (p1 to q1) || p2q2.first * q1 == p1 * p2q2.second) p2q2 = pick(cands)
            val (p2, q2) = p2q2
            val bigger = if (p1.toDouble() / q1 > p2.toDouble() / q2) (p1 to q1) else (p2 to q2)
            val smaller = if (bigger == (p1 to q1)) (p2 to q2) else (p1 to q1)
            fun fr(f: Pair<Int, Int>) = "§\\tfrac{${f.first}}{${f.second}}§"
            task(
                question = l("Welcher Bruch ist größer?", "Which fraction is bigger?"),
                steps = listOf(choice(l("Der größere Bruch", "The bigger fraction"), listOf(fr(bigger), fr(smaller), l("Beide sind gleich groß", "They are equal")), done = fr(bigger),
                    hints = listOf(l("Kernidee: Über Kreuz multiplizieren oder beide in Dezimalzahlen umrechnen.", "Key idea: cross-multiply or convert both to decimals."), l("${fr(p1 to q1)} ≈ ${n(p1.toDouble() / q1, 3)}", "${fr(p1 to q1)} ≈ ${n(p1.toDouble() / q1, 3)}")))),
                solution = listOf(l("${fr(p1 to q1)} ≈ ${n(p1.toDouble() / q1, 3)} und ${fr(p2 to q2)} ≈ ${n(p2.toDouble() / q2, 3)}, also ist ${fr(bigger)} größer.", "${fr(p1 to q1)} ≈ ${n(p1.toDouble() / q1, 3)} and ${fr(p2 to q2)} ≈ ${n(p2.toDouble() / q2, 3)}, so ${fr(bigger)} is bigger.")),
                background = MBg.fraction,
            )
        },
        lab("m.zweierpotenz", Topic.Mental, M, 0.4f) {
            val e = int(if (hard) 11 else 6, if (hard) 20 else 12)
            val r = 2.0.pow(e)
            task(
                question = l("Wie viel ist §2^{$e}§?", "What is §2^{$e}§?"),
                steps = listOf(num(l("Ergebnis", "Result"), r, abs = 0.0, done = "§2^{$e} = ${m(r, 0)}§",
                    hints = listOf(l("Kernidee: §2^{10} = 1024§ – davon aus weiter verdoppeln oder halbieren.", "Key idea: §2^{10} = 1024§ – double or halve from there."), l("Jeder Schritt im Exponenten verdoppelt das Ergebnis.", "Each step in the exponent doubles the result.")))),
                solution = listOf("§2^{10} = 1024,\\ 2^{$e} = ${m(r, 0)}§"),
                background = l(
                    "Computer rechnen im Zweiersystem, deshalb tauchen Zweierpotenzen überall auf: 1024 Byte sind ein Kibibyte, ein Byte hat 2⁸ = 256 Zustände. Die Zahl 2¹⁰ ≈ 1000 ist eine praktische Brücke zwischen beiden Welten.",
                    "Computers count in binary, so powers of two are everywhere: 1024 bytes are a kibibyte, a byte has 2⁸ = 256 states. The number 2¹⁰ ≈ 1000 is a handy bridge between the two worlds.",
                ),
                fact = l("Faltet man ein Blatt Papier 42-mal, wäre es dicker als der Abstand zum Mond – weil sich die Dicke jedes Mal verdoppelt.", "Fold a sheet of paper 42 times and it would be thicker than the distance to the Moon – because the thickness doubles each time."),
            )
        },
        lab("m.kmh", Topic.Mental, M, 0.4f) {
            val toMs = chance(0.5)
            val kmh = pick(18.0, 36.0, 54.0, 72.0, 90.0, 108.0, 126.0, 144.0)
            val ms = kmh / 3.6
            task(
                question = if (toMs) l("Ein Auto fährt ${n(kmh, 0)} km/h. Wie viele Meter legt es pro Sekunde zurück?", "A car drives at ${n(kmh, 0)} km/h. How many metres does it cover per second?")
                else l("Ein Zug fährt ${n(ms, 0)} m/s. Wie viele km/h sind das?", "A train travels at ${n(ms, 0)} m/s. How many km/h is that?"),
                steps = listOf(
                    if (toMs) num(l("Geschwindigkeit", "Speed"), ms, "m/s", abs = 0.01, done = "§${m(ms, 0)}\\ \\text{m/s}§",
                        hints = listOf(l("Kernidee: 1 km/h = 1000 m pro 3600 s.", "Key idea: 1 km/h = 1000 m per 3600 s."), l("Durch 3,6 teilen.", "Divide by 3.6.")))
                    else num(l("Geschwindigkeit", "Speed"), kmh, "km/h", abs = 0.01, done = "§${m(kmh, 0)}\\ \\text{km/h}§",
                        hints = listOf(l("Kernidee: Eine Stunde hat 3600 Sekunden, ein Kilometer 1000 Meter.", "Key idea: an hour has 3600 seconds, a kilometre 1000 metres."), l("Mal 3,6 nehmen.", "Multiply by 3.6."))),
                ),
                solution = listOf(if (toMs) "§${m(kmh, 0)} : 3{,}6 = ${m(ms, 0)}\\ \\text{m/s}§" else "§${m(ms, 0)} \\cdot 3{,}6 = ${m(kmh, 0)}\\ \\text{km/h}§"),
                background = l(
                    "Zwischen km/h und m/s steht immer der Faktor 3,6, weil 3600 Sekunden eine Stunde und 1000 Meter ein Kilometer sind. Ein Blick genügt: 36 km/h sind 10 m/s, 100 km/h knapp 28 m/s – bei einer Sekunde Schrecksekunde also fast 30 Meter.",
                    "Between km/h and m/s there's always a factor of 3.6, because 3600 seconds are an hour and 1000 metres a kilometre. At a glance: 36 km/h is 10 m/s, 100 km/h almost 28 m/s – so a one-second reaction time means nearly 30 metres.",
                ),
            )
        },
    )

    // ----- Prozent & Alltag -----

    private fun percent(): List<TaskDef> = listOf(
        lab("p.rabatt", Topic.Percent, E, 0.2f) {
            val price = if (hard) step(24.0, 180.0, 0.5) else pick(20.0, 40.0, 60.0, 80.0, 120.0, 150.0)
            val d = pick(10, 20, 25, 30, 40, 50)
            val res = price * (1 - d / 100.0)
            task(
                question = l("Eine Jacke kostet ${n(price)} €. Im Schlussverkauf gibt es ${d} % Rabatt. Was kostet sie dann?", "A jacket costs €${n(price)}. In the sale there's ${d} % off. What does it cost then?"),
                steps = listOf(num(l("Neuer Preis", "New price"), res, "€", abs = 0.01, done = "${n(res)} €",
                    hints = listOf(l("Kernidee: ${d} % weniger heißt, du zahlst ${100 - d} %.", "Key idea: ${d} % off means you pay ${100 - d} %."), "§${m(price)} \\cdot ${m((100 - d) / 100.0)}§"), formula = MFc.percent)),
                solution = listOf("§${m(price)} \\cdot ${m((100 - d) / 100.0)} = ${m(res)}\\ €§"),
                background = MBg.percent,
            )
        },
        lab("p.doppelt", Topic.Percent, M, 0.5f) {
            val a = pick(10, 20, 25, 30)
            val b = pick(10, 20, 25, 50)
            val total = 100 - (100 - a) * (100 - b) / 100.0
            task(
                question = l("Erst ${a} % Rabatt, an der Kasse noch einmal ${b} % auf den reduzierten Preis. Wie viel Prozent spart man insgesamt?", "First ${a} % off, then another ${b} % off the reduced price at the till. What percentage do you save in total?"),
                steps = listOf(num(l("Ersparnis insgesamt", "Total saving"), total, "%", abs = 0.01, done = "${n(total, 1)} %",
                    hints = listOf(l("Kernidee: Nicht einfach addieren! Die Faktoren multiplizieren sich.", "Key idea: don't just add! The factors multiply."), "§${m((100 - a) / 100.0)} \\cdot ${m((100 - b) / 100.0)} = ${m((100 - a) * (100 - b) / 10000.0, 4)}§"), formula = MFc.percent)),
                solution = listOf("§${m((100 - a) / 100.0)} \\cdot ${m((100 - b) / 100.0)} = ${m((100 - a) * (100 - b) / 10000.0, 4)}§", l("Man zahlt ${n(100 - total, 1)} %, spart also ${n(total, 1)} % – weniger als ${a + b} %.", "You pay ${n(100 - total, 1)} %, so you save ${n(total, 1)} % – less than ${a + b} %.")),
                background = MBg.percent,
            )
        },
        lab("p.hinundher", Topic.Percent, M, 0.5f) {
            val p = pick(10, 20, 25, 50)
            val net = (1 + p / 100.0) * (1 - p / 100.0) * 100 - 100
            task(
                question = l("Eine Aktie steigt um ${p} % und fällt danach wieder um ${p} %. Wo steht sie jetzt im Vergleich zum Anfang?", "A share rises by ${p} % and then falls by ${p} %. Where does it stand now compared with the start?"),
                steps = listOf(choice(l("Veränderung", "Change"), listOf("−${n(-net, 2)} %", "±0 %", "+${n(-net, 2)} %", "−${p} %"), done = l("−${n(-net, 2)} %: etwas weniger als vorher", "−${n(-net, 2)} %: a little less than before"),
                    hints = listOf(l("Probier es mit 100 €.", "Try it with €100."), l("Die ${p} % weniger werden vom höheren Betrag abgezogen.", "The ${p} % drop is taken from the higher amount.")))),
                solution = listOf("§100 \\cdot ${m(1 + p / 100.0)} \\cdot ${m(1 - p / 100.0)} = ${m(100 + net)}§", l("Also −${n(-net, 2)} % gegenüber dem Anfang.", "So −${n(-net, 2)} % compared with the start.")),
                background = MBg.percent,
                fact = l("Allgemein gilt: (1 + p)(1 − p) = 1 − p². Nach +50 % und −50 % bleiben nur noch 75 %.", "In general: (1 + p)(1 − p) = 1 − p². After +50 % and −50 % only 75 % is left."),
            )
        },
        lab("p.punkte", Topic.Percent, H, 0.6f) {
            val from = pick(2, 4, 5, 10)
            val to = from + pick(1, 2, 5).coerceAtMost(from)
            val rel = (to - from) * 100.0 / from
            task(
                question = l("Der Zinssatz steigt von ${from} % auf ${to} %. Um wie viel Prozent ist er gestiegen?", "The interest rate rises from ${from} % to ${to} %. By what percentage has it risen?"),
                steps = listOf(num(l("Anstieg in Prozent", "Rise in percent"), rel, "%", abs = 0.01, done = "${n(rel, 0)} % (${to - from} Prozentpunkte)",
                    hints = listOf(l("Kernidee: Prozentpunkte sind nicht dasselbe wie Prozent.", "Key idea: percentage points aren't the same as percent."), l("Vergleiche den Anstieg (${to - from}) mit dem Ausgangswert (${from}).", "Compare the rise (${to - from}) with the starting value (${from})."))),
                ),
                solution = listOf("§\\dfrac{${to} - ${from}}{${from}} = ${m(rel / 100.0)} = ${m(rel, 0)}\\,\\%§", l("Der Zins steigt um ${to - from} Prozentpunkte, das sind ${n(rel, 0)} % mehr.", "The rate rises by ${to - from} percentage points, which is ${n(rel, 0)} % more.")),
                background = l(
                    "Prozentpunkte sind der einfache Abstand zweier Prozentwerte, Prozent die relative Änderung. Steigt eine Partei von 10 % auf 15 %, sind das 5 Prozentpunkte – aber 50 % mehr Stimmen. Zeitungen verwechseln das erstaunlich oft.",
                    "Percentage points are the simple difference between two percentages, percent is the relative change. If a party rises from 10 % to 15 %, that's 5 percentage points – but 50 % more votes. Newspapers mix this up surprisingly often.",
                ),
            )
        },
        lab("p.mwst", Topic.Percent, M, 0.4f) {
            val back = hard && chance(0.6)
            val net = if (hard) step(12.0, 400.0, 1.0) else pick(10.0, 20.0, 50.0, 100.0, 200.0)
            val gross = net * 1.19
            task(
                question = if (back) l("Ein Rechnungsbetrag von ${n(gross)} € enthält 19 % Mehrwertsteuer. Wie hoch ist der Betrag ohne Steuer?", "An invoice of €${n(gross)} includes 19 % VAT. What is the amount without tax?")
                else l("Eine Ware kostet ${n(net)} € ohne Steuer. Was kostet sie mit 19 % Mehrwertsteuer?", "An item costs €${n(net)} before tax. What does it cost with 19 % VAT?"),
                steps = listOf(
                    if (back) num(l("Netto", "Net"), net, "€", abs = 0.01, done = "${n(net)} €",
                        hints = listOf(l("Kernidee: Der Bruttobetrag ist 119 % des Nettobetrags.", "Key idea: the gross amount is 119 % of the net amount."), l("Also durch 1,19 teilen – nicht 19 % abziehen!", "So divide by 1.19 – don't subtract 19 %!")), formula = MFc.percent)
                    else num(l("Brutto", "Gross"), gross, "€", abs = 0.01, done = "${n(gross)} €",
                        hints = listOf(l("Kernidee: Mit Steuer sind es 119 %.", "Key idea: with tax it's 119 %."), "§${m(net)} \\cdot 1{,}19§"), formula = MFc.percent),
                ),
                solution = listOf(if (back) "§${m(gross)} : 1{,}19 = ${m(net)}\\ €§" else "§${m(net)} \\cdot 1{,}19 = ${m(gross)}\\ €§"),
                background = MBg.percent,
                fact = if (back) l("Wer einfach 19 % vom Bruttobetrag abzieht, bekommt zu wenig: 119 € minus 19 % sind 96,39 €, nicht 100 €.", "If you just subtract 19 % from the gross amount you get too little: €119 minus 19 % is €96.39, not €100.") else null,
            )
        },
        lab("p.zinseszins", Topic.Percent, H, 0.6f) {
            val k = pick(1000.0, 2000.0, 5000.0, 10000.0)
            val p = pick(2.0, 3.0, 4.0, 5.0, 6.0)
            val years = pick(5, 10, 15, 20)
            val res = k * (1 + p / 100).pow(years)
            task(
                question = l("Du legst ${n(k, 0)} € zu ${n(p, 0)} % Zinsen im Jahr an, die Zinsen bleiben auf dem Konto. Wie viel ist nach ${years} Jahren darauf?", "You invest €${n(k, 0)} at ${n(p, 0)} % interest a year, and the interest stays in the account. How much is in it after ${years} years?"),
                steps = listOf(num(l("Kontostand", "Balance"), res, "€", abs = maxOf(1.0, res * 0.0015), done = "${n(res)} €",
                    hints = listOf(l("Kernidee: Jedes Jahr mal ${n(1 + p / 100, 2)}.", "Key idea: times ${n(1 + p / 100, 2)} every year."), "§${m(k, 0)} \\cdot ${m(1 + p / 100, 2)}^{$years}§"), formula = MFc.compound)),
                solution = listOf("§${m(k, 0)} \\cdot ${m(1 + p / 100, 2)}^{$years} \\approx ${m(res)}\\ €§", l("Ohne Zinseszins wären es nur ${n(k * (1 + p / 100 * years))} €.", "Without compounding it would only be €${n(k * (1 + p / 100 * years))}.")),
                background = MBg.compound,
            )
        },
        lab("p.anteil", Topic.Percent, E, 0.2f) {
            val total = pick(20, 24, 25, 30, 40, 50, 80)
            val part = (int(1, 19) * total / 20).coerceIn(1, total - 1)
            val pct = part * 100.0 / total
            task(
                question = l("In deiner Klasse sind ${total} Leute, ${part} davon lesen gern. Wie viel Prozent sind das?", "There are ${total} people in your class, ${part} of them like reading. What percentage is that?"),
                steps = listOf(num(l("Anteil", "Share"), pct, "%", abs = 0.05, done = "${n(pct, 1)} %",
                    hints = listOf(l("Kernidee: Teil durch Ganzes, dann mal 100.", "Key idea: part divided by whole, then times 100."), "§\\dfrac{$part}{$total} \\cdot 100§"), formula = MFc.percent)),
                solution = listOf("§\\dfrac{$part}{$total} \\cdot 100\\,\\% = ${m(pct, 1)}\\,\\%§"),
                background = MBg.percent,
            )
        },
        lab("p.rezept", Topic.Percent, E, 0.25f) {
            val forN = pick(2, 4, 6)
            val toN = pick(3, 5, 8, 10, 12).let { if (it == forN) it + 1 else it }
            val flour = pick(150, 200, 250, 300, 400)
            val res = flour.toDouble() / forN * toN
            task(
                question = l("Ein Rezept für ${forN} Personen braucht ${flour} g Mehl. Wie viel Mehl brauchst du für ${toN} Personen?", "A recipe for ${forN} people needs ${flour} g of flour. How much flour do you need for ${toN} people?"),
                steps = listOf(num(l("Mehl", "Flour"), res, "g", abs = 0.5, done = "${n(res, 0)} g",
                    hints = listOf(l("Kernidee: Erst für eine Person, dann für alle.", "Key idea: first for one person, then for everyone."), l("Für eine Person: ${n(flour.toDouble() / forN, 1)} g", "For one person: ${n(flour.toDouble() / forN, 1)} g")))),
                solution = listOf("§\\dfrac{${flour}\\ \\text{g}}{$forN} \\cdot $toN = ${m(res, 0)}\\ \\text{g}§"),
                background = MBg.proportion,
            )
        },
        lab("p.helfer", Topic.Percent, M, 0.45f) {
            val a = pick(2, 3, 4, 6)
            val days = pick(6, 8, 10, 12, 15, 18, 24)
            val b = pick(2, 3, 4, 5, 6, 8, 12).let { if (it == a) it + 1 else it }
            val res = a * days.toDouble() / b
            task(
                question = l("${a} Leute streichen einen Zaun in ${days} Stunden. Wie lange brauchen ${b} Leute, wenn alle gleich schnell arbeiten?", "${a} people paint a fence in ${days} hours. How long do ${b} people take if everyone works equally fast?"),
                steps = listOf(num(l("Zeit", "Time"), res, l("Std.", "h"), rel = 0.01, done = "${n(res, 1)} " + l("Stunden", "hours"),
                    hints = listOf(l("Kernidee: Mehr Leute, weniger Zeit. Die Arbeit insgesamt bleibt gleich.", "Key idea: more people, less time. The total work stays the same."), l("Insgesamt: ${a} · ${days} = ${a * days} Personenstunden", "In total: ${a} · ${days} = ${a * days} person-hours")))),
                solution = listOf("§\\dfrac{$a \\cdot $days}{$b} = ${m(res, 1)}\\ \\text{h}§"),
                background = MBg.proportion,
            )
        },
    )

    // ----- Gleichungen -----

    private fun equations(): List<TaskDef> = listOf(
        lab("e.linear", Topic.Equations, E, 0.25f) {
            val x = int(-9, 12).let { if (it == 0) 3 else it }
            val a = int(2, if (hard) 12 else 6)
            val b = int(-15, 20)
            val c = a * x + b
            task(
                question = l("Löse nach x auf: §${m(a)}x ${if (b >= 0) "+" else "-"} ${abs(b)} = ${m(c)}§", "Solve for x: §${m(a)}x ${if (b >= 0) "+" else "-"} ${abs(b)} = ${m(c)}§"),
                steps = listOf(num("x", x.toDouble(), abs = 0.0, done = "§x = ${m(x)}§",
                    hints = listOf(l("Kernidee: Erst die Zahl ohne x auf die andere Seite bringen, dann durch ${a} teilen.", "Key idea: move the number without x to the other side first, then divide by ${a}."), "§${m(a)}x = ${m(c - b)}§"))),
                solution = listOf("§${m(a)}x = ${m(c)} ${if (b >= 0) "-" else "+"} ${abs(b)} = ${m(c - b)}§", "§x = ${m(c - b)} : ${m(a)} = ${m(x)}§"),
                background = MBg.equation,
            )
        },
        lab("e.beidseitig", Topic.Equations, M, 0.45f) {
            val x = int(-8, 10).let { if (it == 0) 2 else it }
            val a = int(3, 9)
            val c = int(1, a - 1)
            val b = int(-12, 12)
            val d = a * x + b - c * x
            task(
                question = l("Löse nach x auf: §${m(a)}x ${if (b >= 0) "+" else "-"} ${abs(b)} = ${m(c)}x ${if (d >= 0) "+" else "-"} ${abs(d)}§", "Solve for x: §${m(a)}x ${if (b >= 0) "+" else "-"} ${abs(b)} = ${m(c)}x ${if (d >= 0) "+" else "-"} ${abs(d)}§"),
                steps = listOf(num("x", x.toDouble(), abs = 0.0, done = "§x = ${m(x)}§",
                    hints = listOf(l("Kernidee: Alle x auf eine Seite, alle Zahlen auf die andere.", "Key idea: all x to one side, all numbers to the other."), "§${m(a - c)}x = ${m(d - b)}§"))),
                solution = listOf("§${m(a)}x - ${m(c)}x = ${m(d)} ${if (b >= 0) "-" else "+"} ${abs(b)}§", "§${m(a - c)}x = ${m(d - b)} \\Rightarrow x = ${m(x)}§"),
                background = MBg.equation,
            )
        },
        lab("e.system", Topic.Equations, M, 0.5f) {
            val x = int(2, if (hard) 40 else 15)
            val y = int(1, x - 1)
            task(
                question = l("Zwei Zahlen ergeben zusammen ${x + y}. Ihr Unterschied ist ${x - y}. Wie heißt die größere Zahl?", "Two numbers add up to ${x + y}. Their difference is ${x - y}. What is the larger number?"),
                steps = listOf(num(l("Größere Zahl", "Larger number"), x.toDouble(), abs = 0.0, done = "§x = ${m(x)}§",
                    hints = listOf(l("Kernidee: Addiere die beiden Gleichungen x + y = ${x + y} und x − y = ${x - y}.", "Key idea: add the two equations x + y = ${x + y} and x − y = ${x - y}."), "§2x = ${m(2 * x)}§"))),
                solution = listOf("§(x + y) + (x - y) = ${x + y} + ${x - y} \\Rightarrow 2x = ${2 * x}§", l("x = ${x}, die kleinere Zahl ist ${y}.", "x = ${x}, the smaller number is ${y}.")),
                background = MBg.system,
            )
        },
        lab("e.quadratisch", Topic.Equations, H, 0.6f) {
            val roots = (-7..9).filter { it != 0 }
            val r1 = pick(roots)
            val r2 = pick(roots.filter { it != r1 && it != -r1 })
            val p = -(r1 + r2)
            val q = r1 * r2
            val big = maxOf(r1, r2)
            val small = minOf(r1, r2)
            fun lin(c: Int) = (if (c >= 0) "+ " else "- ") + (if (abs(c) == 1) "x" else "${abs(c)}x")
            fun con(c: Int) = (if (c >= 0) "+ " else "- ") + abs(c)
            fun factor(r: Int) = if (r > 0) "(x - ${r})" else "(x + ${-r})"
            val eq = "x^2 ${lin(p)} ${con(q)} = 0"
            task(
                question = l("Löse §${eq}§. Wie heißt die größere Lösung?", "Solve §${eq}§. What is the larger solution?"),
                steps = listOf(num(l("Größere Lösung", "Larger solution"), big.toDouble(), abs = 0.0, done = "§x_1 = ${m(big)}§",
                    hints = listOf(l("Kernidee: Welche zwei Zahlen ergeben multipliziert ${n(q)} und addiert ${n(-p)}?", "Key idea: which two numbers multiply to ${n(q)} and add up to ${n(-p)}?"), l("Oder die p-q-Formel: §x = -\\tfrac{p}{2} \\pm \\sqrt{(\\tfrac{p}{2})^2 - q}§", "Or the quadratic formula: §x = -\\tfrac{p}{2} \\pm \\sqrt{(\\tfrac{p}{2})^2 - q}§")), formula = MFc.vieta)),
                solution = listOf("§${factor(r1)}${factor(r2)} = 0§", l("Die Lösungen sind ${n(big)} und ${n(small)}.", "The solutions are ${n(big)} and ${n(small)}.")),
                background = MBg.quadratic,
            )
        },
        lab("e.alter", Topic.Equations, H, 0.55f) {
            val ben = int(4, 14)
            val k = pick(2, 3, 4)
            val anna = k * ben
            // in j Jahren ist Anna doppelt so alt: k*b + j = 2 (b + j) → j = (k-2) b
            val j = (k - 2) * ben
            val later = if (k == 2) int(3, 10) else j
            task(
                question = if (k == 2) l("Anna ist doppelt so alt wie Ben. Zusammen sind sie ${anna + ben} Jahre alt. Wie alt ist Ben?", "Anna is twice as old as Ben. Together they are ${anna + ben} years old. How old is Ben?")
                else l("Anna ist ${k}-mal so alt wie Ben. In ${later} Jahren ist sie nur noch doppelt so alt wie er. Wie alt ist Ben heute?", "Anna is ${k} times as old as Ben. In ${later} years she will only be twice as old as him. How old is Ben today?"),
                steps = listOf(num(l("Bens Alter", "Ben's age"), ben.toDouble(), l("Jahre", "years"), abs = 0.0, done = l("Ben ist ${ben}, Anna ${anna}.", "Ben is ${ben}, Anna ${anna}."),
                    hints = listOf(l("Kernidee: Nenne Bens Alter b und schreibe Annas Alter mit b.", "Key idea: call Ben's age b and write Anna's age in terms of b."),
                        if (k == 2) "§b + 2b = ${anna + ben}§" else "§${k}b + ${later} = 2\\,(b + ${later})§"))),
                solution = listOf(
                    when (k) {
                        2 -> "§3b = ${anna + ben} \\Rightarrow b = ${ben}§"
                        3 -> "§3b + ${later} = 2b + ${2 * later} \\Rightarrow b = ${ben}§"
                        else -> "§${k}b + ${later} = 2b + ${2 * later} \\Rightarrow ${k - 2}b = ${later} \\Rightarrow b = ${ben}§"
                    }
                ),
                background = MBg.equation,
            )
        },
        lab("e.karten", Topic.Equations, H, 0.6f) {
            val adult = pick(8, 9, 10, 12, 14)
            val child = pick(4, 5, 6).coerceAtMost(adult - 2)
            val a = int(3, 20)
            val c = int(3, 25)
            val total = a * adult + c * child
            task(
                question = l("Für ein Kino wurden ${a + c} Karten verkauft und ${total} € eingenommen. Erwachsene zahlen ${adult} €, Kinder ${child} €. Wie viele Kinderkarten waren es?", "A cinema sold ${a + c} tickets and took €${total}. Adults pay €${adult}, children €${child}. How many children's tickets were sold?"),
                steps = listOf(num(l("Kinderkarten", "Children's tickets"), c.toDouble(), abs = 0.0, done = l("${c} Kinder, ${a} Erwachsene", "${c} children, ${a} adults"),
                    hints = listOf(l("Kernidee: Wären alle Karten Kinderkarten, hätte man ${(a + c) * child} € – jede Erwachsenenkarte bringt ${adult - child} € mehr.", "Key idea: if all tickets were children's, it'd be €${(a + c) * child} – each adult ticket brings €${adult - child} more."),
                        "§${adult}e + ${child}k = ${total},\\ e + k = ${a + c}§"))),
                solution = listOf(l("Mehrbetrag ${total} − ${(a + c) * child} = ${total - (a + c) * child} €, geteilt durch ${adult - child} € ergibt ${a} Erwachsene.", "Extra €${total} − €${(a + c) * child} = €${total - (a + c) * child}, divided by €${adult - child} gives ${a} adults."), l("Also ${c} Kinderkarten.", "So ${c} children's tickets.")),
                background = MBg.system,
            )
        },
    )

    // ----- Geometrie -----

    private fun geometry(): List<TaskDef> = listOf(
        lab("g.rechteck", Topic.Geometry, E, 0.2f) {
            val a = int(3, if (hard) 25 else 12)
            val b = int(2, if (hard) 18 else 9)
            task(
                question = l("Ein Beet ist ${a} m lang und ${b} m breit.", "A flower bed is ${a} m long and ${b} m wide."),
                steps = listOf(
                    num(l("Wie viel Zaun braucht man rundherum?", "How much fence goes all the way round?"), 2.0 * (a + b), "m", abs = 0.0, done = l("Umfang ${2 * (a + b)} m", "Perimeter ${2 * (a + b)} m"),
                        hints = listOf(l("Kernidee: Alle vier Seiten zusammenzählen.", "Key idea: add up all four sides."), "§2 \\cdot (${a} + ${b})§")),
                    num(l("Wie groß ist die Fläche?", "What is the area?"), (a * b).toDouble(), "m²", abs = 0.0, done = l("Fläche ${a * b} m²", "Area ${a * b} m²"),
                        hints = listOf(l("Länge mal Breite.", "Length times width."), "§${a} \\cdot ${b}§")),
                ),
                solution = listOf("§U = 2 \\cdot (${a} + ${b}) = ${2 * (a + b)}\\ \\text{m}§", "§A = ${a} \\cdot ${b} = ${a * b}\\ \\text{m}^2§"),
                background = l(
                    "Umfang und Fläche verwechselt man leicht. Der Umfang ist eine Länge (Meter), die Fläche ein Quadratmaß (Quadratmeter). Zwei Rechtecke mit gleichem Umfang können sehr verschiedene Flächen haben: Bei festem Umfang ist das Quadrat das größte Rechteck.",
                    "Perimeter and area are easy to mix up. The perimeter is a length (metres), the area a square measure (square metres). Two rectangles with the same perimeter can have very different areas: for a fixed perimeter, the square is the largest rectangle.",
                ),
            )
        },
        lab("g.pythagoras", Topic.Geometry, M, 0.4f) {
            val (a0, b0, c0) = pick(Triple(3, 4, 5), Triple(5, 12, 13), Triple(8, 15, 17), Triple(7, 24, 25), Triple(6, 8, 10), Triple(9, 12, 15))
            val k = if (hard) int(2, 5) else 1
            val a = a0 * k; val b = b0 * k; val c = c0 * k
            task(
                question = l("Ein rechtwinkliges Dreieck hat die kurzen Seiten ${a} cm und ${b} cm. Wie lang ist die längste Seite?", "A right triangle has short sides of ${a} cm and ${b} cm. How long is the longest side?"),
                steps = listOf(num(l("Längste Seite", "Longest side"), c.toDouble(), "cm", abs = 0.01, done = "§c = ${m(c)}\\ \\text{cm}§",
                    hints = listOf(l("Kernidee: Pythagoras: a² + b² = c².", "Key idea: Pythagoras: a² + b² = c²."), "§c = \\sqrt{${a * a} + ${b * b}}§"), formula = MFc.pythagoras)),
                solution = listOf("§c = \\sqrt{${a}^2 + ${b}^2} = \\sqrt{${a * a + b * b}} = ${m(c)}\\ \\text{cm}§"),
                background = MBg.pythagoras,
            )
        },
        lab("g.leiter", Topic.Geometry, M, 0.45f) {
            val len = pick(3.0, 4.0, 5.0, 6.0, 8.0)
            val foot = (len * pick(0.25, 0.3, 0.35, 0.4) * 10).let { round(it) / 10 }
            val h = sqrt(len * len - foot * foot)
            task(
                question = l("Eine ${n(len, 0)} m lange Leiter lehnt an einer Wand. Unten steht sie ${n(foot, 1)} m von der Wand entfernt. Wie hoch reicht sie an der Wand?", "A ${n(len, 0)} m ladder leans against a wall. Its foot is ${n(foot, 1)} m from the wall. How high up the wall does it reach?"),
                steps = listOf(num(l("Höhe", "Height"), h, "m", abs = 0.02, done = "§h \\approx ${m(h)}\\ \\text{m}§",
                    hints = listOf(l("Kernidee: Die Leiter ist die längste Seite (c) eines rechtwinkligen Dreiecks.", "Key idea: the ladder is the longest side (c) of a right triangle."), "§h = \\sqrt{${m(len, 0)}^2 - ${m(foot, 1)}^2}§"), formula = MFc.pythagoras)),
                solution = listOf("§h = \\sqrt{${m(len * len, 0)} - ${m(foot * foot, 2)}} \\approx ${m(h)}\\ \\text{m}§"),
                background = MBg.pythagoras,
            )
        },
        lab("g.kreis", Topic.Geometry, M, 0.4f) {
            val d = pick(20.0, 24.0, 26.0, 28.0, 30.0, 32.0, 36.0)
            val r = d / 2
            val area = PI * r * r
            val circ = PI * d
            task(
                question = l("Eine Pizza hat ${n(d, 0)} cm Durchmesser.", "A pizza is ${n(d, 0)} cm across."),
                steps = listOf(
                    num(l("Wie lang ist der Rand (Umfang)?", "How long is the crust (circumference)?"), circ, "cm", rel = 0.01, done = "§U \\approx ${m(circ, 1)}\\ \\text{cm}§",
                        hints = listOf(l("Kernidee: Umfang ist π mal Durchmesser.", "Key idea: circumference is π times diameter."), "§U = \\pi \\cdot ${m(d, 0)}§"), formula = MFc.circle),
                    num(l("Wie groß ist die Fläche?", "What is the area?"), area, "cm²", rel = 0.01, done = "§A \\approx ${m(area, 0)}\\ \\text{cm}^2§",
                        hints = listOf(l("Fläche ist π mal Radius zum Quadrat – erst den Radius bestimmen.", "Area is π times the radius squared – find the radius first."), "§A = \\pi \\cdot ${m(r, 0)}^2§"), formula = MFc.circle),
                ),
                solution = listOf("§U = \\pi \\cdot ${m(d, 0)} \\approx ${m(circ, 1)}\\ \\text{cm}§", "§A = \\pi \\cdot ${m(r, 0)}^2 \\approx ${m(area, 0)}\\ \\text{cm}^2§"),
                background = MBg.circle,
            )
        },
        lab("g.winkel", Topic.Geometry, E, 0.2f) {
            val a = int(25, 95)
            val b = int(20, 150 - a)
            val c = 180 - a - b
            task(
                question = l("Zwei Winkel eines Dreiecks sind ${a}° und ${b}° groß. Wie groß ist der dritte?", "Two angles of a triangle are ${a}° and ${b}°. How big is the third?"),
                steps = listOf(num(l("Dritter Winkel", "Third angle"), c.toDouble(), "°", abs = 0.0, done = "${c}°",
                    hints = listOf(l("Kernidee: Im Dreieck sind alle Winkel zusammen 180°.", "Key idea: in a triangle all angles add up to 180°."), "§180 - ${a} - ${b}§"))),
                solution = listOf("§180° - ${a}° - ${b}° = ${c}°§"),
                background = MBg.angles,
            )
        },
        lab("g.vieleck", Topic.Geometry, M, 0.45f) {
            val nn = pick(5, 6, 8, 10, 12)
            val sum = (nn - 2) * 180
            val each = sum.toDouble() / nn
            val name = when (nn) {
                5 -> l("Fünfeck", "pentagon"); 6 -> l("Sechseck", "hexagon"); 8 -> l("Achteck", "octagon"); 10 -> l("Zehneck", "decagon"); else -> l("Zwölfeck", "dodecagon")
            }
            task(
                question = l("Wie groß ist jeder Innenwinkel in einem regelmäßigen ${name}?", "How big is each interior angle of a regular ${name}?"),
                steps = listOf(
                    num(l("Winkelsumme", "Angle sum"), sum.toDouble(), "°", abs = 0.0, done = "${sum}°",
                        hints = listOf(l("Kernidee: Zerlege das Vieleck von einer Ecke aus in Dreiecke.", "Key idea: split the polygon into triangles from one corner."), l("Es sind ${nn - 2} Dreiecke.", "There are ${nn - 2} triangles."))),
                    num(l("Ein Innenwinkel", "One interior angle"), each, "°", abs = 0.01, done = "${n(each, 1)}°",
                        hints = listOf(l("Die Summe auf alle ${nn} Ecken verteilen.", "Share the sum among all ${nn} corners."), "§${sum} : ${nn}§")),
                ),
                solution = listOf("§(${nn} - 2) \\cdot 180° = ${sum}°§", "§${sum}° : ${nn} = ${m(each, 1)}°§"),
                background = MBg.angles,
            )
        },
        lab("g.massstab", Topic.Geometry, E, 0.3f) {
            val scale = pick(10000, 25000, 50000, 100000)
            val cm = pick(2.0, 3.0, 4.0, 6.0, 8.0, 12.0)
            val km = cm * scale / 100000.0
            task(
                question = l("Auf einer Karte im Maßstab 1 : ${n(scale)} sind zwei Orte ${n(cm, 0)} cm voneinander entfernt. Wie weit ist das in Wirklichkeit?", "On a map at a scale of 1 : ${n(scale)}, two places are ${n(cm, 0)} cm apart. How far apart are they in reality?"),
                steps = listOf(num(l("Entfernung", "Distance"), km, "km", abs = 0.01, done = "${n(km, 2)} km",
                    hints = listOf(l("Kernidee: 1 cm auf der Karte sind ${n(scale)} cm in echt.", "Key idea: 1 cm on the map is ${n(scale)} cm in reality."), l("100.000 cm sind 1 km.", "100,000 cm are 1 km.")))),
                solution = listOf("§${m(cm, 0)}\\ \\text{cm} \\cdot ${m(scale)} = ${m(cm * scale, 0)}\\ \\text{cm} = ${m(km, 2)}\\ \\text{km}§"),
                background = l(
                    "Der Maßstab sagt, wie oft die Wirklichkeit größer ist als die Karte. Bei 1 : 25.000 ist 1 cm auf der Karte 250 m in der Landschaft – Wanderkarten haben oft diesen Maßstab, weil man darauf noch einzelne Wege und Häuser erkennt.",
                    "The scale tells you how many times larger reality is than the map. At 1 : 25,000, 1 cm on the map is 250 m on the ground – hiking maps often use this scale because you can still see individual paths and houses.",
                ),
            )
        },
        lab("g.schatten", Topic.Geometry, M, 0.45f) {
            val stick = pick(1.0, 1.5, 2.0)
            val stickShadow = pick(0.8, 1.2, 1.6, 2.4, 3.0)
            val treeShadow = pick(6.0, 9.0, 12.0, 15.0, 18.0)
            val tree = stick * treeShadow / stickShadow
            task(
                question = l("Ein ${n(stick, 1)} m hoher Stab wirft einen ${n(stickShadow, 1)} m langen Schatten. Ein Baum daneben wirft zur selben Zeit einen ${n(treeShadow, 0)} m langen Schatten. Wie hoch ist der Baum?", "A ${n(stick, 1)} m stick casts a ${n(stickShadow, 1)} m shadow. A tree next to it casts a ${n(treeShadow, 0)} m shadow at the same time. How tall is the tree?"),
                steps = listOf(num(l("Höhe des Baums", "Height of the tree"), tree, "m", rel = 0.01, done = "${n(tree, 1)} m",
                    hints = listOf(l("Kernidee: Die Sonnenstrahlen sind parallel, also sind beide Dreiecke ähnlich.", "Key idea: the sun's rays are parallel, so both triangles are similar."), "§\\dfrac{h}{${m(treeShadow, 0)}} = \\dfrac{${m(stick, 1)}}{${m(stickShadow, 1)}}§"))),
                solution = listOf("§h = ${m(treeShadow, 0)} \\cdot \\dfrac{${m(stick, 1)}}{${m(stickShadow, 1)}} = ${m(tree, 1)}\\ \\text{m}§"),
                background = MBg.similar,
            )
        },
        lab("g.aquarium", Topic.Geometry, M, 0.4f) {
            val l1 = pick(40, 50, 60, 80, 100, 120)
            val w = pick(25, 30, 35, 40, 50)
            val h = pick(30, 40, 45, 50, 60)
            val liters = l1 * w * h / 1000.0
            task(
                question = l("Ein Aquarium ist ${l1} cm lang, ${w} cm tief und ${h} cm hoch. Wie viele Liter passen hinein?", "A fish tank is ${l1} cm long, ${w} cm deep and ${h} cm high. How many litres does it hold?"),
                steps = listOf(num(l("Volumen", "Volume"), liters, "l", abs = 0.01, done = "${n(liters, 1)} l",
                    hints = listOf(l("Kernidee: Länge mal Breite mal Höhe, dann in Liter umrechnen.", "Key idea: length times width times height, then convert to litres."), l("1 Liter = 1000 cm³", "1 litre = 1000 cm³")))),
                solution = listOf("§${l1} \\cdot ${w} \\cdot ${h} = ${m(l1 * w * h)}\\ \\text{cm}^3 = ${m(liters, 1)}\\ \\text{l}§"),
                background = MBg.volume,
                fact = l("Wasser wiegt etwa 1 kg pro Liter – das Aquarium voll wöge also gut ${n(liters, 0)} kg, ohne Glas und Steine.", "Water weighs about 1 kg per litre – so the full tank would weigh over ${n(liters, 0)} kg, without glass and stones."),
            )
        },
        lab("g.raumdiagonale", Topic.Geometry, H, 0.6f) {
            val a = pick(2.0, 3.0, 4.0, 5.0)
            val b = pick(3.0, 4.0, 6.0)
            val c = pick(2.5, 3.0)
            val d = sqrt(a * a + b * b + c * c)
            task(
                question = l("Ein Zimmer ist ${n(a, 1)} m lang, ${n(b, 1)} m breit und ${n(c, 1)} m hoch. Wie lang ist die längste gerade Stange, die von einer unteren Ecke in die gegenüberliegende obere Ecke passt?", "A room is ${n(a, 1)} m long, ${n(b, 1)} m wide and ${n(c, 1)} m high. How long is the longest straight pole that fits from one bottom corner to the opposite top corner?"),
                steps = listOf(
                    num(l("Diagonale des Bodens", "Diagonal of the floor"), sqrt(a * a + b * b), "m", abs = 0.02, done = "§${m(sqrt(a * a + b * b))}\\ \\text{m}§",
                        hints = listOf(l("Kernidee: Erst Pythagoras auf dem Boden.", "Key idea: Pythagoras on the floor first."), "§\\sqrt{${m(a, 1)}^2 + ${m(b, 1)}^2}§"), formula = MFc.pythagoras),
                    num(l("Raumdiagonale", "Space diagonal"), d, "m", abs = 0.02, done = "§${m(d)}\\ \\text{m}§",
                        hints = listOf(l("Noch einmal Pythagoras: Bodendiagonale und Höhe.", "Pythagoras again: floor diagonal and height."), "§\\sqrt{${m(a * a + b * b, 1)} + ${m(c, 1)}^2}§")),
                ),
                solution = listOf("§d = \\sqrt{${m(a, 1)}^2 + ${m(b, 1)}^2 + ${m(c, 1)}^2} \\approx ${m(d)}\\ \\text{m}§"),
                background = MBg.pythagoras,
            )
        },
    )

    // ----- Wahrscheinlichkeit -----

    private fun probability(): List<TaskDef> = listOf(
        lab("w.karte", Topic.Probability, E, 0.25f) {
            val kind = pick(0, 1, 2)
            val (what, count) = when (kind) {
                0 -> l("ein Herz", "a heart") to 13
                1 -> l("ein Ass", "an ace") to 4
                else -> l("eine Bildkarte (Bube, Dame, König)", "a face card (jack, queen, king)") to 12
            }
            val g = gcd(count, 52)
            val right = "§\\tfrac{${count / g}}{${52 / g}}§"
            val opts = listOf(right) + listOf("§\\tfrac{1}{52}§", "§\\tfrac{1}{2}§", "§\\tfrac{1}{4}§", "§\\tfrac{1}{13}§", "§\\tfrac{3}{13}§").filter { it != right }.take(3)
            task(
                question = l("Du ziehst eine Karte aus einem gemischten Kartenspiel mit 52 Karten. Wie wahrscheinlich ist es ${what}?", "You draw a card from a shuffled deck of 52. How likely is it to be ${what}?"),
                steps = listOf(choice(l("Wahrscheinlichkeit", "Probability"), opts, done = right,
                    hints = listOf(l("Kernidee: günstige Fälle durch mögliche Fälle.", "Key idea: favourable cases over possible cases."), l("Es gibt ${count} passende Karten.", "There are ${count} matching cards.")))),
                solution = listOf("§\\dfrac{$count}{52} = \\dfrac{${count / g}}{${52 / g}} \\approx ${m(count * 100.0 / 52, 1)}\\,\\%§"),
                background = MBg.probability,
            )
        },
        lab("w.wuerfelsumme", Topic.Probability, M, 0.45f) {
            val sum = int(3, 11)
            val ways = 6 - abs(7 - sum)
            val pct = ways * 100.0 / 36
            task(
                question = l("Du wirfst zwei Würfel. Wie wahrscheinlich ist die Augensumme ${sum}?", "You roll two dice. How likely is a total of ${sum}?"),
                steps = listOf(
                    num(l("Wie viele der 36 Würfe ergeben ${sum}?", "How many of the 36 rolls give ${sum}?"), ways.toDouble(), abs = 0.0, done = l("${ways} von 36", "${ways} of 36"),
                        hints = listOf(l("Kernidee: Zähle die Paare (erster Würfel, zweiter Würfel).", "Key idea: count the pairs (first die, second die)."), l("Für 7 sind es die meisten: 1+6, 2+5, … 6+1.", "7 has the most: 1+6, 2+5, … 6+1."))),
                    num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), pct, "%", abs = 0.1, done = "${n(pct, 1)} %",
                        hints = listOf(l("Günstige durch alle Fälle, mal 100.", "Favourable over all cases, times 100."), "§\\dfrac{$ways}{36} \\cdot 100§")),
                ),
                solution = listOf(l("${ways} von 36 Paaren ergeben ${sum}.", "${ways} of 36 pairs give ${sum}."), "§\\dfrac{$ways}{36} \\approx ${m(pct, 1)}\\,\\%§"),
                background = MBg.probability,
            )
        },
        lab("w.muenzen", Topic.Probability, H, 0.55f) {
            val k = int(2, if (hard) 8 else 5)
            val pct = (1 - 0.5.pow(k)) * 100
            task(
                question = l("Du wirfst eine Münze ${k}-mal. Wie wahrscheinlich ist es, mindestens einmal Kopf zu bekommen?", "You toss a coin ${k} times. How likely is it to get heads at least once?"),
                steps = listOf(
                    num(l("Wahrscheinlichkeit für keinmal Kopf (in %)", "Probability of no heads at all (in %)"), 100 * 0.5.pow(k), "%", abs = 0.05, done = "§\\left(\\tfrac{1}{2}\\right)^{$k} ${if (k <= 4) "=" else "\\approx"} ${m(100 * 0.5.pow(k), 2)}\\,\\%§",
                        hints = listOf(l("Kernidee: „Keinmal Kopf“ heißt jedes Mal Zahl.", "Key idea: \"no heads\" means tails every time."), "§\\left(\\tfrac{1}{2}\\right)^{$k}§")),
                    num(l("Mindestens einmal Kopf (in %)", "At least one head (in %)"), pct, "%", abs = 0.05, done = "${n(pct, 2)} %",
                        hints = listOf(l("Das Gegenteil von „keinmal“.", "The opposite of \"never\"."), "§100\\,\\% - ${m(100 * 0.5.pow(k), 2)}\\,\\%§")),
                ),
                solution = listOf("§1 - \\left(\\tfrac{1}{2}\\right)^{$k} = ${m(pct / 100, 4)} \\approx ${m(pct, 2)}\\,\\%§"),
                background = MBg.probability,
            )
        },
        lab("w.reihenfolge", Topic.Probability, M, 0.45f) {
            val nn = int(4, if (hard) 9 else 6)
            val r = factorial(nn)
            task(
                question = l("Auf wie viele Arten kannst du ${nn} verschiedene Bücher nebeneinander ins Regal stellen?", "In how many ways can you arrange ${nn} different books side by side on a shelf?"),
                steps = listOf(num(l("Möglichkeiten", "Arrangements"), r, abs = 0.0, done = "§${nn}! = ${m(r, 0)}§",
                    hints = listOf(l("Kernidee: Für den ersten Platz gibt es ${nn} Bücher zur Wahl, für den zweiten noch ${nn - 1} …", "Key idea: there are ${nn} books to choose for the first spot, ${nn - 1} for the second …"), "§${(nn downTo 1).joinToString(" \\cdot ")}§"))),
                solution = listOf("§${nn}! = ${(nn downTo 1).joinToString(" \\cdot ")} = ${m(r, 0)}§"),
                background = MBg.counting,
            )
        },
        lab("w.team", Topic.Probability, H, 0.55f) {
            val nn = int(6, if (hard) 15 else 10)
            val k = int(2, if (hard) 5 else 3).coerceAtMost(nn - 2)
            val r = choose(nn, k)
            task(
                question = l("Aus ${nn} Leuten soll ein Team aus ${k} gebildet werden. Wie viele verschiedene Teams sind möglich?", "A team of ${k} is to be chosen from ${nn} people. How many different teams are possible?"),
                steps = listOf(num(l("Teams", "Teams"), r, abs = 0.0, done = "§\\binom{$nn}{$k} = ${m(r, 0)}§",
                    hints = listOf(l("Kernidee: Die Reihenfolge im Team spielt keine Rolle.", "Key idea: the order within the team doesn't matter."), "§\\dfrac{${(nn downTo nn - k + 1).joinToString(" \\cdot ")}}{${k}!}§"), formula = MFc.choose)),
                solution = listOf("§\\dbinom{$nn}{$k} = \\dfrac{${(nn downTo nn - k + 1).joinToString(" \\cdot ")}}{${(k downTo 1).joinToString(" \\cdot ")}} = ${m(r, 0)}§"),
                background = MBg.counting,
            )
        },
        lab("w.erwartung", Topic.Probability, H, 0.6f) {
            val price = pick(1, 2, 3)
            val win6 = pick(4, 5, 6, 8, 10)
            val ev = win6 / 6.0 - price
            task(
                question = l("Auf dem Jahrmarkt kostet ein Wurf ${price} €. Würfelst du eine 6, bekommst du ${win6} €, sonst nichts. Wie viel gewinnst oder verlierst du im Durchschnitt pro Wurf?", "At the fair, a roll costs €${price}. Roll a 6 and you get €${win6}, otherwise nothing. How much do you win or lose on average per roll?"),
                steps = listOf(num(l("Durchschnitt pro Wurf (Verlust negativ)", "Average per roll (loss negative)"), ev, "€", abs = 0.01, done = "${n(ev, 2)} €",
                    hints = listOf(l("Kernidee: Gewinn mal Wahrscheinlichkeit, minus Einsatz.", "Key idea: prize times probability, minus the stake."), "§\\tfrac{1}{6} \\cdot ${win6} - ${price}§"))),
                solution = listOf("§\\tfrac{1}{6} \\cdot ${win6}\\ € - ${price}\\ € \\approx ${m(ev, 2)}\\ €§", if (ev < 0) l("Auf Dauer verlierst du.", "In the long run you lose.") else l("Auf Dauer gewinnst du – selten auf einem Jahrmarkt!", "In the long run you win – rare at a fair!")),
                background = MBg.expected,
            )
        },
        lab("w.urne", Topic.Probability, X, 0.65f) {
            val red = int(3, 7)
            val blue = int(2, 6)
            val total = red + blue
            val p = red.toDouble() / total * (red - 1) / (total - 1) * 100
            task(
                question = l("In einer Tüte liegen ${red} rote und ${blue} blaue Bonbons. Du nimmst blind zwei nacheinander (ohne Zurücklegen). Wie wahrscheinlich sind beide rot?", "A bag holds ${red} red and ${blue} blue sweets. You take two at random, one after the other (without putting any back). How likely are both red?"),
                steps = listOf(num(l("Wahrscheinlichkeit (in %)", "Probability (in %)"), p, "%", abs = 0.1, done = "${n(p, 1)} %",
                    hints = listOf(l("Kernidee: Erst ${red} von ${total}, dann ist ein rotes weniger da.", "Key idea: first ${red} of ${total}, then there's one red fewer."), "§\\dfrac{$red}{$total} \\cdot \\dfrac{${red - 1}}{${total - 1}}§"))),
                solution = listOf("§\\dfrac{$red}{$total} \\cdot \\dfrac{${red - 1}}{${total - 1}} = \\dfrac{${red * (red - 1)}}{${total * (total - 1)}} \\approx ${m(p, 1)}\\,\\%§"),
                background = MBg.probability,
            )
        },
        lab("w.geburtstag", Topic.Probability, X, 0.7f) {
            val people = pick(10, 23, 30, 40, 50)
            var none = 1.0
            for (i in 0 until people) none *= (365 - i) / 365.0
            val pct = (1 - none) * 100
            val bands = listOf(l("unter 20 %", "under 20 %"), l("20 bis 40 %", "20 to 40 %"), l("40 bis 60 %", "40 to 60 %"), l("60 bis 80 %", "60 to 80 %"), l("über 80 %", "over 80 %"))
            val idx = when { pct < 20 -> 0; pct < 40 -> 1; pct < 60 -> 2; pct < 80 -> 3; else -> 4 }
            task(
                question = l("In einem Raum sind ${people} Leute. Wie wahrscheinlich haben mindestens zwei am selben Tag Geburtstag?", "There are ${people} people in a room. How likely is it that at least two share a birthday?"),
                steps = listOf(choice(l("Schätze", "Estimate"), listOf(bands[idx]) + bands.filterIndexed { i, _ -> i != idx }.shuffled(rnd).take(3), done = l("etwa ${n(pct, 0)} %", "about ${n(pct, 0)} %"),
                    hints = listOf(l("Kernidee: Es zählt jedes mögliche Paar, nicht nur dein Geburtstag.", "Key idea: every possible pair counts, not just your birthday."), l("Bei ${people} Leuten gibt es ${people * (people - 1) / 2} Paare.", "With ${people} people there are ${people * (people - 1) / 2} pairs.")))),
                solution = listOf("§1 - \\dfrac{365}{365} \\cdot \\dfrac{364}{365} \\cdot \\dots \\cdot \\dfrac{${366 - people}}{365} \\approx ${m(pct, 1)}\\,\\%§"),
                background = l(
                    "Das Geburtstagsparadox: Schon ab 23 Leuten ist es wahrscheinlicher als nicht, dass zwei am selben Tag Geburtstag haben. Das wirkt falsch, weil wir an unseren eigenen Geburtstag denken. Es zählen aber alle Paare, und davon gibt es bei 23 Leuten schon 253.",
                    "The birthday paradox: with just 23 people, it's more likely than not that two share a birthday. It feels wrong because we think of our own birthday. But every pair counts, and with 23 people there are already 253 of them.",
                ),
            )
        },
    )
}
