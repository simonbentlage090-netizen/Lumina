package de.funke.app.ui.challenge

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/* Mathe-Ausbau (seit 13.2): Kopfrechnen, Prozent & Alltag. Alle Texte selbst geschrieben. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private fun gcdM(a: Int, b: Int): Int = if (b == 0) a else gcdM(b, a % b)

object MathNew1 {
    val all: List<TaskDef> get() = mental + percent

    private val mental = listOf(
        lab("ma.mal5", Topic.Mental, E, 0.2f) {
            val a = 2 * int(13, 49)
            task(
                question = l("""Rechne im Kopf: §${a} \cdot 5§""", """Mental maths: §${a} \cdot 5§"""),
                steps = listOf(num(l("Ergebnis", "Result"), a * 5.0, "", abs = 0.0, done = """§${a * 5}§""",
                    hints = listOf(l("Kernidee: Mal 5 ist dasselbe wie mal 10 und dann halbieren.", "Key idea: times 5 is the same as times 10 and then halving."), """§${a} \cdot 5 = \dfrac{${a} \cdot 10}{2}§"""))),
                solution = listOf("""§${a} \cdot 5 = \dfrac{${a * 10}}{2} = ${a * 5}§"""),
                background = l("Viele Kopfrechentricks ersetzen eine schwere Rechnung durch zwei leichte. Mit 10 malnehmen heißt nur, eine Null anzuhängen, und Halbieren geht bei geraden Zahlen sofort. Genauso rechnet man mal 50 (mal 100, halbieren) oder mal 25 (mal 100, vierteln).", "Many mental maths tricks replace one hard calculation with two easy ones. Multiplying by 10 just means adding a zero, and halving even numbers is instant. You do times 50 the same way (times 100, halve) or times 25 (times 100, quarter)."),
            )
        },
        lab("ma.mal9", Topic.Mental, E, 0.3f) {
            val a = int(13, 89)
            task(
                question = l("""Rechne im Kopf: §9 \cdot ${a}§""", """Mental maths: §9 \cdot ${a}§"""),
                steps = listOf(num(l("Ergebnis", "Result"), 9.0 * a, "", abs = 0.0, done = """§${9 * a}§""",
                    hints = listOf(l("Kernidee: 9 ist 10 minus 1. Also erst mal 10, dann die Zahl einmal abziehen.", "Key idea: 9 is 10 minus 1. So first times 10, then subtract the number once."), """§9 \cdot ${a} = ${10 * a} - ${a}§"""))),
                solution = listOf("""§9 \cdot ${a} = 10 \cdot ${a} - ${a} = ${10 * a} - ${a} = ${9 * a}§"""),
                background = l("Das Distributivgesetz ist der wichtigste Kopfrechentrick: a · (b − c) = a·b − a·c. Mal 9, mal 99 oder mal 19 wird so zu einer Multiplikation mit einer runden Zahl und einer Subtraktion. Zur Kontrolle: Bei einem Vielfachen von 9 ist die Quersumme immer durch 9 teilbar.", "The distributive law is the most important mental maths trick: a · (b − c) = a·b − a·c. Times 9, times 99 or times 19 become a multiplication by a round number plus a subtraction. To check: for a multiple of 9 the digit sum is always divisible by 9."),
            )
        },
        lab("ma.prozenttausch", Topic.Mental, E, 0.4f) {
            val (p, b) = pick(8 to 50, 4 to 75, 12 to 25, 16 to 25, 6 to 50, 18 to 50)
            val r = p * b / 100.0
            task(
                question = l("Wie viel sind ${p} % von ${b}?", "What is ${p}% of ${b}?"),
                steps = listOf(num(l("Ergebnis", "Result"), r, "", abs = 0.001, done = """§${m(r, 2)}§""",
                    hints = listOf(l("Kernidee: Man darf die beiden Zahlen tauschen. ${p} % von ${b} ist dasselbe wie ${b} % von ${p}.", "Key idea: you may swap the two numbers. ${p}% of ${b} is the same as ${b}% of ${p}."), l("${b} % kann man leicht ausrechnen: 50 % ist die Hälfte, 25 % ein Viertel, 75 % drei Viertel.", "${b}% is easy: 50% is half, 25% a quarter, 75% three quarters."))),
                    ),
                solution = listOf("""§${p}\ \% \text{ von } ${b} = \dfrac{${p} \cdot ${b}}{100} = ${b}\ \% \text{ von } ${p} = ${m(r, 2)}§""".let { l(it, it.replace("von", "of")) }),
                background = l("Prozent heißt „von hundert“. a % von b ist a · b / 100, und weil man bei der Multiplikation die Reihenfolge tauschen darf, ist das gleich b % von a. Mit diesem Trick wird aus einer krummen Prozentrechnung oft eine ganz leichte.", "Percent means 'per hundred'. a% of b is a · b / 100, and because you can swap the order in multiplication, that equals b% of a. This trick often turns an awkward percentage into a very easy one."),
            )
        },
        lab("ma.quadrat5", Topic.Mental, M, 0.4f) {
            val d = int(1, 9); val a = 10 * d + 5
            task(
                question = l("""Rechne im Kopf: §${a}^2§""", """Mental maths: §${a}^2§"""),
                steps = listOf(num(l("Ergebnis", "Result"), a.toDouble() * a, "", abs = 0.0, done = """§${a * a}§""",
                    hints = listOf(l("Kernidee: Bei Zahlen mit 5 am Ende nimmt man die Zehnerziffer mal ihren Nachfolger und hängt 25 an.", "Key idea: for numbers ending in 5, multiply the tens digit by the next number up and append 25."), """§${d} \cdot ${d + 1} = ${d * (d + 1)} \to ${d * (d + 1)}25§"""))),
                solution = listOf("""§${a}^2 = (10 \cdot ${d} + 5)^2 = 100 \cdot ${d} \cdot ${d + 1} + 25 = ${a * a}§"""),
                background = l("Warum klappt das? (10a + 5)² = 100a² + 100a + 25 = 100·a·(a + 1) + 25. Die ersten Ziffern sind also a·(a + 1), die letzten immer 25. Mit der binomischen Formel lassen sich viele solche Tricks herleiten, etwa auch 51² = 2500 + 100 + 1.", "Why does it work? (10a + 5)² = 100a² + 100a + 25 = 100·a·(a + 1) + 25. So the first digits are a·(a + 1), the last always 25. Many such tricks can be derived from the binomial formula, for example 51² = 2500 + 100 + 1."),
            )
        },
        lab("ma.teilbar3", Topic.Mental, M, 0.4f) {
            val good = pick(7452183L, 1234566L, 9876543L, 3332211L, 5550123L)
            fun ds(x: Long) = x.toString().sumOf { it - '0' }
            val wrongs = listOf(1L, 20L, 400L, 7000L, 2L, 50L).map { good + it }.filter { ds(it) % 3 != 0 }.distinct().take(3)
            task(
                question = l("Welche dieser Zahlen ist durch 3 teilbar?", "Which of these numbers is divisible by 3?"),
                steps = listOf(pick1(l("Durch 3 teilbar ist …", "Divisible by 3 is …"), "$good", wrongs.map { "$it" },
                    hints = listOf(l("Kernidee: Eine Zahl ist genau dann durch 3 teilbar, wenn ihre Quersumme durch 3 teilbar ist.", "Key idea: a number is divisible by 3 exactly when its digit sum is divisible by 3."), l("Quersumme: alle Ziffern addieren.", "Digit sum: add up all the digits.")))),
                solution = listOf(l("Quersumme von $good: ${ds(good)}, durch 3 teilbar. Die anderen haben die Quersummen ${wrongs.joinToString { ds(it).toString() }}.", "Digit sum of $good: ${ds(good)}, divisible by 3. The others have digit sums ${wrongs.joinToString { ds(it).toString() }}.")),
                background = l("Die Quersummenregel funktioniert, weil 10, 100, 1000 … alle um genau 1 größer sind als ein Vielfaches von 9 (also auch von 3). Jede Ziffer zählt deshalb beim Teilen durch 3 nur mit ihrem eigenen Wert. Dieselbe Regel gilt für 9. Für 11 nimmt man die abwechselnde Quersumme.", "The digit-sum rule works because 10, 100, 1000 … are all exactly 1 more than a multiple of 9 (and so of 3). So when dividing by 3, each digit only counts with its own value. The same rule applies to 9. For 11 you use the alternating digit sum."),
            )
        },
        lab("ma.bruchdezimal", Topic.Mental, E, 0.4f) {
            val (z, nn) = pick(3 to 8, 5 to 8, 7 to 20, 3 to 16, 4 to 25, 9 to 40)
            val d = z.toDouble() / nn
            task(
                question = l("""Schreibe als Dezimalzahl: §\dfrac{${z}}{${nn}}§""", """Write as a decimal: §\dfrac{${z}}{${nn}}§"""),
                steps = listOf(num(l("Dezimalzahl", "Decimal"), d, "", abs = 0.0005, done = """§${m(d, 4)}§""",
                    hints = listOf(l("Kernidee: Erweitere den Bruch so, dass unten 10, 100 oder 1000 steht.", "Key idea: expand the fraction so that the bottom is 10, 100 or 1000."), """§\dfrac{${z}}{${nn}} = \dfrac{${z * (1000 / nn)}}{1000}§""".takeIf { 1000 % nn == 0 } ?: """§${z} : ${nn}§"""))),
                solution = listOf("""§\dfrac{${z}}{${nn}} = ${m(d, 4)}§"""),
                background = l("Brüche, deren Nenner nur die Primfaktoren 2 und 5 hat, ergeben abbrechende Dezimalzahlen, denn sie lassen sich auf eine Zehnerpotenz erweitern. Bei allen anderen Nennern, etwa 3 oder 7, entsteht eine periodische Dezimalzahl, die sich endlos wiederholt.", "Fractions whose denominator has only the prime factors 2 and 5 give terminating decimals, because they can be expanded to a power of ten. For all other denominators, such as 3 or 7, you get a recurring decimal that repeats forever."),
            )
        },
        lab("ma.wurzelschaetzen", Topic.Mental, M, 0.6f) {
            val nn = pick(50, 30, 80, 120, 200); val r = sqrt(nn.toDouble())
            val lo = kotlin.math.floor(r).toInt()
            task(
                question = l("""Schätze §\sqrt{${nn}}§ auf eine Nachkommastelle genau, ohne Taschenrechner.""", """Estimate §\sqrt{${nn}}§ to one decimal place without a calculator."""),
                steps = listOf(num(l("Wurzel", "Square root"), r, "", abs = 0.06, done = """§\approx ${m(r, 2)}§""",
                    hints = listOf(l("Kernidee: Suche die nächsten Quadratzahlen: §${lo}^2 = ${lo * lo}§ und §${lo + 1}^2 = ${(lo + 1) * (lo + 1)}§.", "Key idea: find the nearest square numbers: §${lo}^2 = ${lo * lo}§ and §${lo + 1}^2 = ${(lo + 1) * (lo + 1)}§."), """§\sqrt{${nn}} \approx ${lo} + \dfrac{${nn} - ${lo * lo}}{2 \cdot ${lo}}§"""))),
                solution = listOf("""§\sqrt{${nn}} \approx ${lo} + \dfrac{${nn - lo * lo}}{${2 * lo}} \approx ${m(lo + (nn - lo * lo) / (2.0 * lo), 2)}§""", l("""Genau: §${m(r, 3)}§""", """Exactly: §${m(r, 3)}§""")),
                background = l("Liegt eine Zahl knapp über einer Quadratzahl a², ist ihre Wurzel etwa a + Rest/(2a). Das ist die Tangentennäherung aus der Analysis: Die Wurzelfunktion ist fast gerade, wenn man nur ein kleines Stück weitergeht. Schon die Babylonier rechneten Wurzeln so.", "If a number lies just above a square a², its square root is about a + remainder/(2a). That's the tangent approximation from calculus: the square root function is almost straight if you only move a little way along. The Babylonians already calculated square roots like this."),
            )
        },
        lab("ma.doppelthalb", Topic.Mental, E, 0.5f) {
            val (a, b) = pick(35 to 18, 45 to 16, 15 to 24, 125 to 16, 25 to 36, 55 to 14)
            task(
                question = l("""Rechne im Kopf: §${a} \cdot ${b}§""", """Mental maths: §${a} \cdot ${b}§"""),
                steps = listOf(num(l("Ergebnis", "Result"), a.toDouble() * b, "", abs = 0.0, done = """§${a * b}§""",
                    hints = listOf(l("Kernidee: Verdopple die eine Zahl und halbiere die andere. Das Produkt bleibt gleich, wird aber oft rund.", "Key idea: double one number and halve the other. The product stays the same but often becomes round."), """§${a} \cdot ${b} = ${2 * a} \cdot ${b / 2}§"""))),
                solution = listOf("""§${a} \cdot ${b} = ${2 * a} \cdot ${b / 2} = ${a * b}§"""),
                background = l("Wenn man einen Faktor verdoppelt und den anderen halbiert, ändert sich das Produkt nicht: (2a)·(b/2) = a·b. Bei Zahlen, die auf 5 enden, entsteht so eine Zahl mit 0 am Ende, und dann ist es leicht. Man kann den Trick auch mehrmals hintereinander anwenden.", "If you double one factor and halve the other, the product doesn't change: (2a)·(b/2) = a·b. With numbers ending in 5 this produces a number ending in 0, and then it's easy. You can apply the trick several times in a row."),
            )
        },
        lab("ma.negativ", Topic.Mental, E, 0.4f) {
            val a = -int(3, 15); val b = -int(5, 20)
            val r = a - b
            task(
                question = l("""Rechne: §${a} - (${b})§""", """Calculate: §${a} - (${b})§"""),
                steps = listOf(num(l("Ergebnis", "Result"), r.toDouble(), "", abs = 0.0, done = """§${r}§""",
                    hints = listOf(l("Kernidee: Eine negative Zahl abziehen ist dasselbe wie die positive Zahl addieren.", "Key idea: subtracting a negative number is the same as adding the positive number."), """§${a} - (${b}) = ${a} + ${-b}§"""))),
                solution = listOf("""§${a} - (${b}) = ${a} + ${-b} = ${r}§"""),
                background = l("Auf dem Zahlenstrahl heißt Minus: nach links gehen, Minus vor einer negativen Zahl aber: umdrehen und nach rechts gehen. Am Thermometer sieht man es gut: Von −5 °C auf −2 °C sind es 3 Grad nach oben, denn −2 − (−5) = 3. Schulden zu streichen ist wie Geld bekommen.", "On the number line, minus means going left, but minus in front of a negative number means turning round and going right. A thermometer shows it well: from −5 °C to −2 °C is 3 degrees up, because −2 − (−5) = 3. Cancelling a debt is like receiving money."),
            )
        },
        lab("ma.potenzregel", Topic.Mental, M, 0.5f) {
            val a = int(2, 6); val b = int(2, 4)
            val e = a + 2 * b; val r = 2.0.pow(e)
            task(
                question = l("""Schreibe als eine einzige Zweierpotenz und rechne aus: §2^{${a}} \cdot 4^{${b}}§""", """Write as a single power of two and calculate: §2^{${a}} \cdot 4^{${b}}§"""),
                steps = listOf(num(l("Ergebnis", "Result"), r, "", abs = 0.0, done = """§2^{${e}} = ${m(r, 0)}§""",
                    hints = listOf(l("Kernidee: 4 ist 2². Bei gleicher Basis werden Exponenten beim Malnehmen addiert.", "Key idea: 4 is 2². With the same base, exponents add when you multiply."), """§4^{${b}} = (2^2)^{${b}} = 2^{${2 * b}}§"""))),
                solution = listOf("""§2^{${a}} \cdot 4^{${b}} = 2^{${a}} \cdot 2^{${2 * b}} = 2^{${e}} = ${m(r, 0)}§"""),
                background = l("Potenzen sind abgekürztes Malnehmen. Daraus folgen die Regeln: aᵐ·aⁿ = aᵐ⁺ⁿ, weil man die Faktoren einfach zusammenzählt, und (aᵐ)ⁿ = aᵐ·ⁿ. Zweierpotenzen sollte man bis 2¹⁰ = 1024 kennen, weil sie in der Informatik überall vorkommen.", "Powers are shorthand for repeated multiplication. That gives the rules: aᵐ·aⁿ = aᵐ⁺ⁿ, because you simply count the factors together, and (aᵐ)ⁿ = aᵐ·ⁿ. You should know the powers of two up to 2¹⁰ = 1024, because they appear everywhere in computing."),
            )
        },
        lab("ma.ggt", Topic.Mental, M, 0.5f) {
            val g = pick(6, 12, 14, 18, 21); val a = g * pick(4, 5, 7); val b = g * pick(6, 9, 11)
            val gg = gcdM(a, b)
            task(
                question = l("Du hast ${a} rote und ${b} blaue Murmeln und willst sie auf möglichst viele Tüten verteilen, sodass in jeder Tüte gleich viele rote und gleich viele blaue sind. Wie viele Tüten werden es?", "You have ${a} red and ${b} blue marbles and want to share them among as many bags as possible, so that every bag has the same number of red and the same number of blue. How many bags will there be?"),
                steps = listOf(num(l("Anzahl Tüten", "Number of bags"), gg.toDouble(), "", abs = 0.0, done = """§${gg}§""",
                    hints = listOf(l("Kernidee: Die Zahl der Tüten muss beide Mengen teilen. Gesucht ist der größte gemeinsame Teiler.", "Key idea: the number of bags must divide both amounts. You're looking for the greatest common divisor."), l("Zerlege beide Zahlen in Primfaktoren und nimm die gemeinsamen.", "Break both numbers into prime factors and take the common ones."))),
                    ),
                solution = listOf("""§\text{ggT}(${a}, ${b}) = ${gg}§""".let { l(it, it.replace("ggT", "gcd")) }, l("Jede Tüte bekommt ${a / gg} rote und ${b / gg} blaue.", "Each bag gets ${a / gg} red and ${b / gg} blue.")),
                background = l("Der größte gemeinsame Teiler ist die größte Zahl, die zwei Zahlen ohne Rest teilt. Euklid fand vor über 2000 Jahren einen schnellen Weg: Teile die größere Zahl durch die kleinere, nimm den Rest und wiederhole. Der letzte Rest vor der Null ist der ggT. Man braucht ihn auch zum Kürzen von Brüchen.", "The greatest common divisor is the largest number that divides two numbers without a remainder. Over 2000 years ago Euclid found a quick method: divide the larger number by the smaller, take the remainder and repeat. The last remainder before zero is the gcd. You also need it to simplify fractions."),
            )
        },
        lab("ma.kgv", Topic.Mental, M, 0.5f) {
            val (a, b) = pick(12 to 18, 15 to 20, 8 to 12, 10 to 25, 14 to 21)
            val kg = a / gcdM(a, b) * b
            task(
                question = l("Zwei Buslinien fahren um 7:00 Uhr gleichzeitig an der Haltestelle ab. Linie A fährt alle ${a} Minuten, Linie B alle ${b} Minuten. Nach wie vielen Minuten fahren sie zum ersten Mal wieder gleichzeitig ab?", "Two bus routes leave the stop at the same time at 7:00. Route A runs every ${a} minutes, route B every ${b} minutes. After how many minutes do they next leave at the same time?"),
                steps = listOf(num(l("Minuten", "Minutes"), kg.toDouble(), "min", abs = 0.0, done = """§${kg}\ \text{min}§""",
                    hints = listOf(l("Kernidee: Gesucht ist die kleinste Zahl, die in beiden Takten vorkommt: das kleinste gemeinsame Vielfache.", "Key idea: you're looking for the smallest number that occurs in both timetables: the least common multiple."), """§\text{kgV}(a, b) = \dfrac{a \cdot b}{\text{ggT}(a, b)}§""".let { l(it, it.replace("kgV", "lcm").replace("ggT", "gcd")) }))),
                solution = listOf("""§\text{kgV}(${a}, ${b}) = \dfrac{${a} \cdot ${b}}{${gcdM(a, b)}} = ${kg}§""".let { l(it, it.replace("kgV", "lcm")) }),
                background = l("Wenn sich zwei Dinge in festen Takten wiederholen, treffen sie sich beim kleinsten gemeinsamen Vielfachen wieder. Das gilt für Busse, für Zahnräder und für Planeten. Zikaden in Nordamerika schlüpfen alle 13 oder 17 Jahre, beides Primzahlen. So treffen sie selten auf Fressfeinde mit kürzeren Zyklen.", "When two things repeat at fixed intervals, they meet again at the least common multiple. That holds for buses, gears and planets. Cicadas in North America emerge every 13 or 17 years, both prime numbers. That way they rarely coincide with predators that have shorter cycles."),
            )
        },
        lab("ma.dezimalmal", Topic.Mental, E, 0.5f) {
            val (a, b) = pick(0.2 to 0.3, 0.4 to 0.5, 0.03 to 0.2, 0.6 to 0.07, 1.5 to 0.4)
            val r = a * b
            task(
                question = l("""Rechne: §${m(a, 2)} \cdot ${m(b, 2)}§""", """Calculate: §${m(a, 2)} \cdot ${m(b, 2)}§"""),
                steps = listOf(num(l("Ergebnis", "Result"), r, "", abs = 1e-9, done = """§${m(r, 4)}§""",
                    wrong = l("Zähle die Nachkommastellen beider Zahlen zusammen. So viele hat auch das Ergebnis.", "Count the decimal places of both numbers together. The result has that many too."),
                    hints = listOf(l("Kernidee: Rechne ohne Komma und setze es danach. Das Ergebnis hat so viele Nachkommastellen wie beide Faktoren zusammen.", "Key idea: calculate without the decimal point and put it back afterwards. The result has as many decimal places as both factors together."), l("Ein Zehntel mal ein Zehntel ist ein Hundertstel.", "A tenth times a tenth is a hundredth."))),
                    ),
                solution = listOf("""§${m(a, 2)} \cdot ${m(b, 2)} = ${m(r, 4)}§"""),
                background = l("Beim Malnehmen mit Zahlen kleiner als 1 wird das Ergebnis kleiner, nicht größer. Das widerspricht dem Bauchgefühl, das wir mit ganzen Zahlen trainiert haben. 0,2 · 0,3 heißt: zwei Zehntel von drei Zehnteln, also sechs Hundertstel. Ein häufiger Fehler ist 0,6.", "Multiplying by numbers smaller than 1 makes the result smaller, not bigger. That goes against the gut feeling we've trained with whole numbers. 0.2 · 0.3 means two tenths of three tenths, i.e. six hundredths. A common mistake is 0.6."),
            )
        },
        lab("ma.punktvorstrich", Topic.Mental, E, 0.3f) {
            val a = int(2, 9); val b = int(2, 6); val c = int(2, 6); val d = int(1, 5)
            val r = a + b * c - d
            task(
                question = l("""Rechne: §${a} + ${b} \cdot ${c} - ${d}§""", """Calculate: §${a} + ${b} \cdot ${c} - ${d}§"""),
                steps = listOf(num(l("Ergebnis", "Result"), r.toDouble(), "", abs = 0.0, done = """§${r}§""",
                    wrong = l("Punkt vor Strich: Erst malnehmen, dann von links nach rechts addieren und subtrahieren.", "Multiplication before addition: multiply first, then add and subtract from left to right."),
                    hints = listOf(l("Kernidee: Punktrechnung (mal, geteilt) kommt vor Strichrechnung (plus, minus).", "Key idea: multiplication and division come before addition and subtraction."), """§${b} \cdot ${c} = ${b * c}§"""))),
                solution = listOf("""§${a} + ${b * c} - ${d} = ${r}§"""),
                background = l("Die Regel Punkt vor Strich ist eine Vereinbarung, damit jeder einen Ausdruck gleich liest. Sie passt zur Bedeutung: 2 + 3 · 4 sind zwei Äpfel und drei Tüten mit je vier. Wer es anders meint, muss Klammern setzen. Im Internet streiten sich trotzdem regelmäßig Tausende über solche Aufgaben.", "The rule that multiplication comes before addition is a convention so that everyone reads an expression the same way. It fits the meaning: 2 + 3 · 4 is two apples and three bags of four. Anyone who means something else has to use brackets. Even so, thousands of people regularly argue online about such puzzles."),
            )
        },
        lab("ma.bruchmal", Topic.Mental, M, 0.5f) {
            val (a, b, c, d) = pick(listOf(2, 3, 9, 14), listOf(3, 4, 8, 9), listOf(5, 6, 3, 10), listOf(4, 9, 3, 8))
            val zz = a * c; val nn = b * d; val g = gcdM(zz, nn)
            task(
                question = l("""Rechne und kürze: §\dfrac{${a}}{${b}} \cdot \dfrac{${c}}{${d}}§. Gib das Ergebnis als Dezimalzahl an.""", """Calculate and simplify: §\dfrac{${a}}{${b}} \cdot \dfrac{${c}}{${d}}§. Give the result as a decimal."""),
                steps = listOf(num(l("Ergebnis", "Result"), zz.toDouble() / nn, "", abs = 0.005, done = """§\dfrac{${zz / g}}{${nn / g}} \approx ${m(zz.toDouble() / nn, 4)}§""",
                    hints = listOf(l("Kernidee: Zähler mal Zähler, Nenner mal Nenner. Kürze am besten schon vorher über Kreuz.", "Key idea: numerator times numerator, denominator times denominator. Best to cancel crosswise beforehand."), """§\dfrac{${a} \cdot ${c}}{${b} \cdot ${d}}§"""))),
                solution = listOf("""§\dfrac{${a} \cdot ${c}}{${b} \cdot ${d}} = \dfrac{${zz}}{${nn}} = \dfrac{${zz / g}}{${nn / g}} \approx ${m(zz.toDouble() / nn, 4)}§"""),
                background = l("Brüche malzunehmen ist leichter als sie zu addieren: Man braucht keinen gemeinsamen Nenner. Anschaulich: Zwei Drittel von neun Vierzehnteln ist ein Teil von einem Teil. Kürzt man vorher über Kreuz, bleiben die Zahlen klein und man verrechnet sich seltener.", "Multiplying fractions is easier than adding them: you don't need a common denominator. Pictured: two thirds of nine fourteenths is a part of a part. If you cancel crosswise first, the numbers stay small and you make fewer mistakes."),
            )
        },
    )

    private val percent = listOf(
        lab("ma.trinkgeld", Topic.Percent, E, 0.3f) {
            val bill = tiered(listOf(46.0, 38.0, 72.0), 23.0, 95.0, 1.0); val pct = pick(10, 15, 20)
            val tip = bill * pct / 100
            task(
                question = l("Die Rechnung im Restaurant beträgt ${n(bill, 2)} €. Du willst ${pct} % Trinkgeld geben. Wie viel ist das?", "The restaurant bill is €${n(bill, 2)}. You want to leave a ${pct}% tip. How much is that?"),
                steps = listOf(num(l("Trinkgeld in Euro", "Tip in euros"), tip, "€", abs = 0.01, done = """§${m(tip, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: 10 % ist ein Zehntel: Komma eine Stelle nach links. 5 % ist die Hälfte davon, 20 % das Doppelte.", "Key idea: 10% is a tenth: move the decimal point one place left. 5% is half of that, 20% double."), """§10\ \% = ${m(bill / 10, 2)}\ \text{€}§"""))),
                solution = listOf("""§${pct}\ \% \cdot ${m(bill, 2)} = ${m(tip, 2)}\ \text{€}§"""),
                background = l("Im Kopf rechnet man Prozente am besten aus Bausteinen: 10 % ist ein Zehntel, 1 % ein Hundertstel, 5 % die Hälfte von 10 %. Daraus lässt sich fast alles zusammensetzen, etwa 15 % = 10 % + 5 %. In Deutschland sind etwa 5 bis 10 % Trinkgeld üblich, in den USA eher 15 bis 20 %.", "Mentally, percentages are best built from blocks: 10% is a tenth, 1% a hundredth, 5% half of 10%. Almost anything can be put together from these, for example 15% = 10% + 5%. In Germany a tip of about 5 to 10% is usual, in the USA more like 15 to 20%."),
            )
        },
        lab("ma.salzwasser", Topic.Percent, E, 0.4f) {
            val g = pick(35.0, 28.0, 40.0, 9.0)
            val pct = g / 1000 * 100
            task(
                question = l("In einem Liter Meerwasser (etwa 1 kg) sind ${n(g, 0)} g Salz gelöst. Wie viel Prozent der Masse sind Salz?", "One litre of seawater (about 1 kg) contains ${n(g, 0)} g of dissolved salt. What percentage of the mass is salt?"),
                steps = listOf(num(l("Salzgehalt in Prozent", "Salt content in percent"), pct, "%", abs = 0.01, done = """§${m(pct, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Anteil = Teil durch Ganzes, mal 100. Beides in Gramm.", "Key idea: share = part over whole, times 100. Both in grams."), """§\dfrac{${m(g, 0)}\ \text{g}}{1000\ \text{g}} \cdot 100\ \%§"""))),
                solution = listOf("""§\dfrac{${m(g, 0)}}{1000} = ${m(g / 1000, 3)} = ${m(pct, 1)}\ \%§"""),
                background = l("Prozent ist ein Anteil von hundert. Damit ein Anteil sinnvoll ist, müssen Teil und Ganzes in derselben Einheit stehen. Die Ozeane haben im Mittel 3,5 % Salz, die Ostsee nur etwa 1 %, das Tote Meer über 30 %. Dort ist das Wasser so dicht, dass man darin nicht untergehen kann.", "Percent is a share out of a hundred. For a share to make sense, part and whole must be in the same unit. The oceans have 3.5% salt on average, the Baltic only about 1%, the Dead Sea over 30%. There the water is so dense that you can't sink in it."),
            )
        },
        lab("ma.verbrauch", Topic.Percent, E, 0.4f) {
            val per = tiered(listOf(6.0, 5.0, 8.0), 4.5, 9.5, 0.1); val dist = tiered(listOf(450.0, 300.0, 600.0), 120.0, 900.0, 10.0)
            val l100 = per * dist / 100
            task(
                question = l("Ein Auto braucht ${n(per, 1)} Liter auf 100 km. Wie viel Liter braucht es für eine Strecke von ${n(dist, 0)} km?", "A car uses ${n(per, 1)} litres per 100 km. How many litres does it need for a distance of ${n(dist, 0)} km?"),
                steps = listOf(num(l("Verbrauch in Litern", "Fuel in litres"), l100, "l", rel = 0.01, done = """§${m(l100, 1)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Dreisatz. Erst für 1 km, dann für die ganze Strecke.", "Key idea: rule of three. First for 1 km, then for the whole distance."), """§\dfrac{${m(per, 1)}}{100} \cdot ${m(dist, 0)}§"""))),
                solution = listOf("""§\dfrac{${m(per, 1)}\ \text{l}}{100\ \text{km}} \cdot ${m(dist, 0)}\ \text{km} = ${m(l100, 1)}\ \text{l}§"""),
                background = l("Der Dreisatz ist die Grundrechenart des Alltags: Wenn zwei Größen proportional sind, rechnet man erst auf eine Einheit herunter und dann wieder hinauf. Liter pro 100 km ist so eine Verhältnisangabe. In den USA gibt man es umgekehrt an, in Meilen pro Gallone, und dort ist größer besser.", "The rule of three is the everyday arithmetic of proportions: when two quantities are proportional, you first scale down to one unit and then back up. Litres per 100 km is such a ratio. In the USA it's given the other way round, in miles per gallon, and there bigger is better."),
            )
        },
        lab("ma.prozentvonprozent", Topic.Percent, E, 0.6f) {
            val a = pick(40, 60, 50); val b = pick(25, 30, 20)
            val r = a * b / 100.0
            task(
                question = l("In einer Schule sind ${a} % der Kinder Mädchen. Von den Mädchen spielen ${b} % Fußball im Verein. Wie viel Prozent aller Kinder sind Fußballerinnen?", "In a school ${a}% of the children are girls. Of the girls, ${b}% play football in a club. What percentage of all the children are girl footballers?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), r, "%", abs = 0.01, done = """§${m(r, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Ein Anteil von einem Anteil: Die Prozentsätze werden multipliziert, nicht addiert.", "Key idea: a share of a share: the percentages are multiplied, not added."), """§${m(a / 100.0, 2)} \cdot ${m(b / 100.0, 2)}§"""))),
                solution = listOf("""§${m(a / 100.0, 2)} \cdot ${m(b / 100.0, 2)} = ${m(r / 100, 3)} = ${m(r, 1)}\ \%§"""),
                background = l("Prozente beziehen sich immer auf ein bestimmtes Ganzes. Hier ist das erste Ganze die Schule, das zweite nur die Mädchen. Will man auf die ganze Schule zurückrechnen, multipliziert man die Anteile. Dieselbe Rechnung steckt in Wahrscheinlichkeitsbäumen und in jeder Statistik mit Untergruppen.", "Percentages always refer to a particular whole. Here the first whole is the school, the second only the girls. To get back to the whole school you multiply the shares. The same calculation lies behind probability trees and every statistic with subgroups."),
            )
        },
        lab("ma.mwstrueck", Topic.Percent, M, 0.6f) {
            val gross = tiered(listOf(59.5, 119.0, 23.8), 10.0, 500.0, 0.01)
            val net = gross / 1.19; val tax = gross - net
            task(
                question = l("Im Preis von ${n(gross, 2)} € sind 19 % Mehrwertsteuer enthalten. Wie viel davon ist Steuer?", "A price of €${n(gross, 2)} includes 19% VAT. How much of it is tax?"),
                steps = listOf(num(l("Steueranteil in Euro", "Tax in euros"), tax, "€", abs = 0.01, done = """§${m(tax, 2)}\ \text{€}§""",
                    wrong = l("Die 19 % beziehen sich auf den Preis ohne Steuer, nicht auf den Endpreis.", "The 19% refers to the price without tax, not to the final price."),
                    hints = listOf(l("Kernidee: Der Endpreis ist 119 % des Nettopreises. Rechne erst auf 100 % zurück.", "Key idea: the final price is 119% of the net price. First work back to 100%."), """§\text{netto} = \dfrac{${m(gross, 2)}}{1{,}19}§""".let { l(it, it.replace("netto", "net")) }))),
                solution = listOf("""§\text{netto} = \dfrac{${m(gross, 2)}}{1{,}19} \approx ${m(net, 2)}\ \text{€}§""".let { l(it, it.replace("netto", "net")) }, """§\text{Steuer} = ${m(gross, 2)} - ${m(net, 2)} \approx ${m(tax, 2)}\ \text{€}§""".let { l(it, it.replace("Steuer", "tax")) }),
                background = l("Ein häufiger Fehler ist, 19 % vom Endpreis abzuziehen. Die Steuer wird aber auf den Nettopreis aufgeschlagen, also ist der Endpreis 119 %. Rückwärts muss man durch 1,19 teilen. Die Steuer ist deshalb nur etwa 16 % vom Endpreis, nicht 19 %.", "A common mistake is to take 19% off the final price. But the tax is added on top of the net price, so the final price is 119%. Going backwards you have to divide by 1.19. The tax is therefore only about 16% of the final price, not 19%."),
            )
        },
        lab("ma.mischung", Topic.Percent, M, 0.5f) {
            val v1 = pick(2.0, 1.0, 3.0); val p1 = pick(30.0, 50.0, 100.0); val v2 = pick(3.0, 4.0); val p2 = pick(10.0, 0.0, 20.0)
            val mix = (v1 * p1 + v2 * p2) / (v1 + v2)
            task(
                question = l("Du mischst ${n(v1, 0)} l Saft mit ${n(p1, 0)} % Fruchtanteil und ${n(v2, 0)} l Getränk mit ${n(p2, 0)} % Fruchtanteil. Wie viel Prozent Frucht hat die Mischung?", "You mix ${n(v1, 0)} l of juice with ${n(p1, 0)}% fruit content and ${n(v2, 0)} l of a drink with ${n(p2, 0)}% fruit content. What percentage of fruit does the mixture have?"),
                steps = listOf(num(l("Fruchtanteil in Prozent", "Fruit content in percent"), mix, "%", abs = 0.05, done = """§${m(mix, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Rechne aus, wie viel Liter reine Frucht in jeder Flasche steckt, und teile die Summe durch die Gesamtmenge.", "Key idea: work out how many litres of pure fruit are in each bottle, and divide the total by the overall amount."), """§\dfrac{${m(v1, 0)} \cdot ${m(p1 / 100, 2)} + ${m(v2, 0)} \cdot ${m(p2 / 100, 2)}}{${m(v1 + v2, 0)}}§"""))),
                solution = listOf("""§\text{Frucht} = ${m(v1 * p1 / 100, 2)} + ${m(v2 * p2 / 100, 2)} = ${m((v1 * p1 + v2 * p2) / 100, 2)}\ \text{l}§""".let { l(it, it.replace("Frucht", "fruit")) }, """§\dfrac{${m((v1 * p1 + v2 * p2) / 100, 2)}}{${m(v1 + v2, 0)}} \approx ${m(mix, 1)}\ \%§"""),
                background = l("Beim Mischen darf man Prozentsätze nicht einfach mitteln, außer die Mengen sind gleich groß. Man muss gewichten: Die größere Menge zieht den Durchschnitt zu sich. Am sichersten rechnet man in absoluten Mengen, hier in Litern reiner Frucht, und teilt erst am Ende.", "When mixing you can't simply average the percentages unless the amounts are equal. You have to weight them: the larger amount pulls the average towards itself. The safest way is to calculate in absolute amounts, here litres of pure fruit, and only divide at the end."),
            )
        },
        lab("ma.regel72", Topic.Percent, M, 0.6f) {
            val r = pick(3.0, 4.0, 6.0, 8.0, 9.0); val est = 72 / r; val exact = ln(2.0) / ln(1 + r / 100)
            task(
                question = l("Faustregel der Banker: Geld verdoppelt sich mit Zinseszins nach etwa 72 / Zinssatz Jahren. Nach wie vielen Jahren verdoppelt sich ein Guthaben bei ${n(r, 0)} % Zinsen ungefähr?", "A bankers' rule of thumb: with compound interest money doubles after about 72 / interest rate years. After roughly how many years does a balance double at ${n(r, 0)}% interest?"),
                steps = listOf(num(l("Jahre", "Years"), exact, l("Jahre", "years"), rel = 0.06, done = """§\approx ${m(est, 0)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) },
                    hints = listOf(l("Kernidee: Einfach 72 durch den Zinssatz teilen.", "Key idea: just divide 72 by the interest rate."), """§\dfrac{72}{${m(r, 0)}}§"""))),
                solution = listOf("""§\dfrac{72}{${m(r, 0)}} = ${m(est, 1)}§""", l("""Genau: §\dfrac{\ln 2}{\ln ${m(1 + r / 100, 2)}} \approx ${m(exact, 2)}§""", """Exactly: §\dfrac{\ln 2}{\ln ${m(1 + r / 100, 2)}} \approx ${m(exact, 2)}§""")),
                background = l("Exakt gilt für die Verdopplungszeit ln 2 / ln(1 + p). Für kleine p ist ln(1 + p) ≈ p, und ln 2 ≈ 0,69. Mit 72 statt 69 wird die Regel für übliche Zinssätze genauer und lässt sich leicht teilen. Sie funktioniert auch rückwärts: Bei 3 % Inflation halbiert sich die Kaufkraft in etwa 24 Jahren.", "Exactly, the doubling time is ln 2 / ln(1 + p). For small p, ln(1 + p) ≈ p, and ln 2 ≈ 0.69. Using 72 instead of 69 makes the rule more accurate for typical rates and easy to divide. It also works the other way: at 3% inflation, purchasing power halves in about 24 years."),
            )
        },
        lab("ma.gewichtet", Topic.Percent, M, 0.5f) {
            val k1 = pick(2.0, 3.0); val k2 = pick(3.0, 2.0, 4.0); val s = pick(1.0, 2.0)
            val avg = (k1 * 2 + k2 + s) / 4.0
            task(
                question = l("In Mathe zählt die Klassenarbeit doppelt so viel wie jeder der beiden kleinen Tests. Du hast in der Arbeit eine ${n(k1, 0)}, in den Tests eine ${n(k2, 0)} und eine ${n(s, 0)}. Wie ist dein Durchschnitt?", "In maths the class exam counts twice as much as each of the two short tests. You got a ${n(k1, 0)} in the exam and a ${n(k2, 0)} and a ${n(s, 0)} in the tests. What is your average?"),
                steps = listOf(num(l("Durchschnittsnote", "Average grade"), avg, "", abs = 0.01, done = """§${m(avg, 2)}§""",
                    hints = listOf(l("Kernidee: Die Arbeit zählt wie zwei Noten. Insgesamt gibt es also vier Gewichte.", "Key idea: the exam counts like two grades. So there are four weights in total."), """§\dfrac{2 \cdot ${m(k1, 0)} + ${m(k2, 0)} + ${m(s, 0)}}{4}§"""))),
                solution = listOf("""§\dfrac{2 \cdot ${m(k1, 0)} + ${m(k2, 0)} + ${m(s, 0)}}{2 + 1 + 1} = ${m(avg, 2)}§"""),
                background = l("Ein gewichteter Durchschnitt zählt manche Werte öfter als andere. Man multipliziert jeden Wert mit seinem Gewicht, addiert und teilt durch die Summe der Gewichte. So berechnet man Zeugnisnoten, Börsenindizes und den Notendurchschnitt im Abitur.", "A weighted average counts some values more often than others. You multiply each value by its weight, add up and divide by the sum of the weights. That's how report grades, stock market indices and final school grades are calculated."),
            )
        },
        lab("ma.median", Topic.Percent, M, 0.6f) {
            val rich = pick(1000000, 500000, 2000000)
            val inc = listOf(1800, 2200, 2500, 3100, rich)
            val mean = inc.sum() / 5.0
            task(
                question = l("Fünf Leute verdienen im Monat 1800 €, 2200 €, 2500 €, 3100 € und ${n(rich.toDouble(), 0)} €. Der Durchschnitt ist ${n(mean, 0)} €. Wie groß ist der Median, das mittlere Einkommen?", "Five people earn €1800, €2200, €2500, €3100 and €${n(rich.toDouble(), 0)} a month. The average is €${n(mean, 0)}. What is the median, the middle income?"),
                steps = listOf(num(l("Median in Euro", "Median in euros"), 2500.0, "€", abs = 0.0, done = """§2500\ \text{€}§""",
                    hints = listOf(l("Kernidee: Ordne die Werte der Größe nach. Der Median ist der Wert genau in der Mitte.", "Key idea: put the values in order of size. The median is the value exactly in the middle."), l("Bei fünf Werten ist es der dritte.", "With five values it's the third one."))),
                    ),
                solution = listOf(l("Sortiert: 1800, 2200, 2500, 3100, ${n(rich.toDouble(), 0)}. Der mittlere Wert ist 2500 €.", "Sorted: 1800, 2200, 2500, 3100, ${n(rich.toDouble(), 0)}. The middle value is €2500.")),
                background = l("Ein einziger Ausreißer kann den Durchschnitt völlig verzerren, den Median aber kaum. Deshalb gibt man bei Einkommen, Hauspreisen oder Wartezeiten oft den Median an. Wenn ein Milliardär in eine Kneipe kommt, wird jeder darin im Durchschnitt zum Millionär, aber niemand ist reicher geworden.", "A single outlier can completely distort the average but hardly affects the median. That's why the median is often given for incomes, house prices or waiting times. When a billionaire walks into a pub, everyone inside becomes a millionaire on average, but nobody is any richer."),
            )
        },
        lab("ma.handytarif", Topic.Percent, M, 0.6f) {
            val base = pick(9.99, 4.99); val perMin = pick(0.09, 0.05); val flat = pick(19.99, 14.99)
            val be = (flat - base) / perMin
            task(
                question = l("Tarif A kostet ${n(base, 2)} € im Monat plus ${n(perMin * 100, 0)} Cent pro Minute. Tarif B ist eine Flatrate für ${n(flat, 2)} €. Ab wie vielen Minuten im Monat lohnt sich Tarif B?", "Plan A costs €${n(base, 2)} a month plus ${n(perMin * 100, 0)} cents per minute. Plan B is a flat rate for €${n(flat, 2)}. From how many minutes a month is plan B worth it?"),
                steps = listOf(num(l("Minuten", "Minutes"), be, "min", rel = 0.01, done = """§\approx ${m(be, 0)}\ \text{min}§""",
                    hints = listOf(l("Kernidee: Setze beide Kosten gleich und löse nach den Minuten auf.", "Key idea: set both costs equal and solve for the minutes."), """§${m(base, 2)} + ${m(perMin, 2)}\, x = ${m(flat, 2)}§"""))),
                solution = listOf("""§x = \dfrac{${m(flat, 2)} - ${m(base, 2)}}{${m(perMin, 2)}} \approx ${m(be, 1)}\ \text{min}§"""),
                background = l("Viele Alltagsentscheidungen sind ein Vergleich zweier Geraden: feste Grundkosten plus Kosten pro Einheit. Wo sich die Geraden schneiden, sind beide gleich teuer. Davor ist der Tarif mit den niedrigeren Grundkosten besser, danach der andere. Dasselbe gilt für Bahncards, Leasing oder Kauf.", "Many everyday decisions compare two straight lines: a fixed base cost plus a cost per unit. Where the lines cross, both cost the same. Before that point the plan with the lower base cost is better, after it the other one. The same applies to railcards, or leasing versus buying."),
            )
        },
        lab("ma.zinstage", Topic.Percent, M, 0.5f) {
            val k = tiered(listOf(2000.0, 5000.0), 1000.0, 10000.0, 100.0); val p = pick(3.0, 2.5, 4.0); val d = pick(90, 60, 180)
            val z = k * p / 100 * d / 365
            task(
                question = l("Du legst ${n(k, 0)} € für ${d} Tage zu ${n(p, 1)} % Jahreszins an. Wie viel Zinsen bekommst du?", "You deposit €${n(k, 0)} for ${d} days at ${n(p, 1)}% annual interest. How much interest do you get?"),
                steps = listOf(num(l("Zinsen in Euro", "Interest in euros"), z, "€", abs = 0.02, done = """§${m(z, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Der Zinssatz gilt für ein ganzes Jahr. Für ${d} Tage bekommt man nur den Anteil ${d}/365.", "Key idea: the rate applies to a whole year. For ${d} days you get only the fraction ${d}/365."), """§Z = K \cdot \dfrac{p}{100} \cdot \dfrac{t}{365}§"""))),
                solution = listOf("""§Z = ${m(k, 0)} \cdot ${m(p / 100, 3)} \cdot \dfrac{${d}}{365} \approx ${m(z, 2)}\ \text{€}§"""),
                background = l("Zinsen sind der Preis für geliehenes Geld, angegeben pro Jahr. Für kürzere Zeiten rechnet man anteilig. Banken nutzen dabei verschiedene Konventionen, manche zählen jeden Monat als 30 Tage und das Jahr als 360. Bei großen Summen machen solche Details echtes Geld aus.", "Interest is the price of borrowed money, given per year. For shorter periods it's calculated pro rata. Banks use different conventions for this; some count every month as 30 days and the year as 360. With large sums such details make real money."),
            )
        },
        lab("ma.mittelgeschw", Topic.Percent, H, 0.6f) {
            val v1 = pick(30.0, 40.0, 20.0); val v2 = pick(90.0, 60.0, 80.0)
            val avg = 2 * v1 * v2 / (v1 + v2)
            task(
                question = l("Du fährst mit dem Rad einen Berg hinauf, im Schnitt ${n(v1, 0)} km/h, und dieselbe Strecke wieder hinunter, mit ${n(v2, 0)} km/h. Wie schnell warst du im Schnitt auf der ganzen Tour?", "You cycle up a hill at ${n(v1, 0)} km/h on average and back down the same way at ${n(v2, 0)} km/h. How fast were you on average over the whole trip?"),
                steps = listOf(num(l("Durchschnitt in km/h", "Average in km/h"), avg, "km/h", rel = 0.01, done = """§${m(avg, 1)}\ \text{km/h}§""",
                    wrong = l("Nicht einfach den Mittelwert nehmen: Bergauf bist du viel länger unterwegs.", "Don't just take the mean: you spend much longer going uphill."),
                    hints = listOf(l("Kernidee: Durchschnittsgeschwindigkeit ist Gesamtstrecke durch Gesamtzeit. Nimm für die Strecke eine bequeme Zahl, etwa ${n(v1 * v2 / 10, 0)} km.", "Key idea: average speed is total distance over total time. Choose a convenient number for the distance, say ${n(v1 * v2 / 10, 0)} km."), """§\bar{v} = \dfrac{2s}{s/v_1 + s/v_2} = \dfrac{2 v_1 v_2}{v_1 + v_2}§"""))),
                solution = listOf("""§\bar{v} = \dfrac{2 \cdot ${m(v1, 0)} \cdot ${m(v2, 0)}}{${m(v1, 0)} + ${m(v2, 0)}} = ${m(avg, 1)}\ \text{km/h}§"""),
                background = l("Der Durchschnitt zweier Geschwindigkeiten auf gleich langen Strecken ist nicht das arithmetische, sondern das harmonische Mittel. Weil man für die langsame Strecke mehr Zeit braucht, zählt sie stärker. Ein berühmtes Rätsel: Wer bergauf 30 km/h fährt, kann den Schnitt von 60 km/h für Hin und Rück niemals erreichen, auch nicht mit unendlich schneller Abfahrt.", "The average of two speeds over equal distances isn't the arithmetic mean but the harmonic mean. Because the slow stretch takes more time, it counts for more. A famous puzzle: if you ride uphill at 30 km/h, you can never reach an average of 60 km/h for the round trip, not even with an infinitely fast descent."),
            )
        },
        lab("ma.umfrage", Topic.Percent, H, 0.6f) {
            val nn = pick(1000, 400, 2500); val err = 1 / sqrt(nn.toDouble()) * 100
            task(
                question = l("Ein Meinungsforschungsinstitut befragt ${nn} zufällig ausgewählte Menschen. Faustregel: Die Fehlerspanne liegt bei etwa 1/√n. Wie groß ist sie ungefähr, in Prozentpunkten?", "A polling institute surveys ${nn} randomly chosen people. Rule of thumb: the margin of error is about 1/√n. Roughly how large is it, in percentage points?"),
                steps = listOf(num(l("Fehlerspanne in Prozentpunkten", "Margin of error in percentage points"), err, "", rel = 0.03, done = """§\pm ${m(err, 1)}§""",
                    hints = listOf(l("Kernidee: Wurzel aus der Zahl der Befragten ziehen, Kehrwert nehmen, mal 100.", "Key idea: take the square root of the number of people, then the reciprocal, times 100."), """§\dfrac{1}{\sqrt{${nn}}}§"""))),
                solution = listOf("""§\dfrac{1}{\sqrt{${nn}}} \approx ${m(err / 100, 3)} = ${m(err, 1)}\ \text{Prozentpunkte}§""".let { l(it, it.replace("Prozentpunkte", "percentage points")) }),
                background = l("Eine Zufallsstichprobe trifft die Wahrheit nie genau, aber erstaunlich gut. Der Fehler schrumpft mit der Wurzel der Stichprobengröße: Für halb so viel Unsicherheit braucht man viermal so viele Befragte. Wichtiger als die Größe ist, dass die Auswahl wirklich zufällig ist. Eine riesige, aber verzerrte Umfrage ist wertlos.", "A random sample never hits the truth exactly, but it comes surprisingly close. The error shrinks with the square root of the sample size: for half the uncertainty you need four times as many respondents. More important than the size is that the selection is truly random. A huge but biased survey is worthless."),
            )
        },
        lab("ma.huerde", Topic.Percent, H, 0.7f) {
            val (a, b, c, d) = pick(listOf(47.0, 38.0, 11.0, 4.0), listOf(44.0, 41.0, 12.0, 3.0), listOf(40.0, 36.0, 20.0, 4.0))
            val valid = a + b + c; val share = a / valid * 100
            task(
                question = l("Bei einer Wahl gibt es eine 5-%-Hürde. Partei A bekommt ${n(a, 0)} %, B ${n(b, 0)} %, C ${n(c, 0)} % und D ${n(d, 0)} %. Die Sitze werden nur unter den Parteien über der Hürde nach ihrem Anteil verteilt. Wie viel Prozent der Sitze bekommt A?", "In an election there is a 5% threshold. Party A gets ${n(a, 0)}%, B ${n(b, 0)}%, C ${n(c, 0)}% and D ${n(d, 0)}%. Seats are shared out only among the parties above the threshold, according to their share. What percentage of the seats does A get?"),
                steps = listOf(num(l("Sitzanteil von A in Prozent", "A's share of seats in percent"), share, "%", abs = 0.1, done = """§\approx ${m(share, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: D fällt heraus. Das neue Ganze sind nur noch die Stimmen von A, B und C.", "Key idea: D drops out. The new whole is only the votes of A, B and C."), """§\dfrac{${m(a, 0)}}{${m(a, 0)} + ${m(b, 0)} + ${m(c, 0)}}§"""))),
                solution = listOf("""§\dfrac{${m(a, 0)}}{${m(valid, 0)}} \approx ${m(share, 1)}\ \%§"""),
                background = l("Fallen Stimmen weg, bezieht sich der Anteil auf ein kleineres Ganzes, und die übrigen Parteien bekommen anteilig mehr. So kann eine Partei mit weniger als der Hälfte der Stimmen eine absolute Mehrheit der Sitze erreichen. Wer die Rechnung kennt, versteht, warum über Sperrklauseln so heftig gestritten wird.", "When votes drop out, the share refers to a smaller whole, and the remaining parties get proportionally more. That's how a party with less than half the votes can win an absolute majority of the seats. Knowing the calculation helps you understand why thresholds are argued about so fiercely."),
            )
        },
    )
}
