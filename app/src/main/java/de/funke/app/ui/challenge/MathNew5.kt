package de.funke.app.ui.challenge

import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

/*
 * Mathe-Ausbau (seit 13.2), Teil 5: Aufgaben mit neuen Situationen, viele zum kritischen Denken mit Zahlen
 * (Statistik in Nachrichten, Denkfallen, räumliches Vorstellen). Alle Texte selbst geschrieben.
 */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private fun sup5(e: Int): String = e.toString().map { if (it == '-') '⁻' else "⁰¹²³⁴⁵⁶⁷⁸⁹"[it - '0'] }.joinToString("")
private fun clock(minOfDay: Int): String = "${minOfDay / 60}:" + (minOfDay % 60).toString().padStart(2, '0')

object MathNew5 {
    val all: List<TaskDef> get() = tasks

    private val tasks = listOf(
        // ================================================ Kopfrechnen
        lab("ma.endziffer", Topic.Mental, M, 0.5f) {
            val b = pick(7, 3, 2, 8); val nExp = pick(100, 50, 2026, 37)
            val last = java.math.BigInteger.valueOf(b.toLong()).modPow(java.math.BigInteger.valueOf(nExp.toLong()), java.math.BigInteger.TEN).toInt()
            val cycle = (1..4).map { java.math.BigInteger.valueOf(b.toLong()).pow(it).mod(java.math.BigInteger.TEN).toInt() }
            task(
                question = l("Mit welcher Ziffer endet die riesige Zahl ${b}^${nExp}?", "Which digit does the huge number ${b}^${nExp} end with?"),
                steps = listOf(num(l("Letzte Ziffer", "Last digit"), last.toDouble(), "", abs = 0.0, done = "$last",
                    hints = listOf(l("Kernidee: Für die letzte Ziffer zählt nur die letzte Ziffer der Faktoren. Schreib die ersten Potenzen auf und such ein Muster.", "Key idea: for the last digit only the last digits of the factors matter. Write down the first few powers and look for a pattern."), l("Die Endziffern von ${b}¹, ${b}², ${b}³, ${b}⁴ sind ${cycle.joinToString(", ")}. Danach wiederholt sich das alle 4 Schritte.", "The last digits of ${b}¹, ${b}², ${b}³, ${b}⁴ are ${cycle.joinToString(", ")}. After that it repeats every 4 steps.")))),
                solution = listOf(l("${nExp} geteilt durch 4 lässt den Rest ${nExp % 4}. Also endet ${b}^${nExp} wie ${b}^${if (nExp % 4 == 0) 4 else nExp % 4}, nämlich auf ${last}.", "${nExp} divided by 4 leaves remainder ${nExp % 4}. So ${b}^${nExp} ends like ${b}^${if (nExp % 4 == 0) 4 else nExp % 4}, namely in ${last}.")),
                background = l("Die Zahl selbst hat Dutzende bis Hunderte Stellen, und trotzdem kennst du ihr Ende in Sekunden. Das ist Rechnen modulo 10: Man rechnet nur mit Resten. Genau dieser Trick, schnelles Potenzieren mit Resten, steckt in der RSA-Verschlüsselung, die Online-Banking schützt.", "The number itself has dozens to hundreds of digits, and yet you know how it ends in seconds. This is arithmetic modulo 10: you only calculate with remainders. Exactly this trick, fast exponentiation with remainders, lies behind RSA encryption, which protects online banking."),
            )
        },
        mc("ma.neunerprobe", Topic.Mental, M,
            t("Lena rechnet 4321 · 1234 schriftlich und erhält 5.332.124. Ohne nachzurechnen: Kann das stimmen? Tipp: Die Quersumme einer Zahl lässt beim Teilen durch 9 denselben Rest wie die Zahl selbst.", "Lena works out 4321 · 1234 on paper and gets 5,332,124. Without redoing it: can that be right? Hint: a number's digit sum leaves the same remainder when divided by 9 as the number itself."),
            t("Urteil", "Verdict"),
            t("Nein, das Ergebnis muss falsch sein.", "No, the result must be wrong."),
            listOf(t("Ja, das Ergebnis stimmt sicher.", "Yes, the result is definitely right."), t("Das lässt sich ohne Nachrechnen nicht sagen.", "It can't be decided without recalculating."), t("Es stimmt, aber nur ungefähr.", "It's right, but only approximately.")),
            t("Kernidee: Quersummen: 4321 → 10 → Rest 1. 1234 → 10 → Rest 1. Welchen Rest muss dann das Produkt haben?", "Key idea: digit sums: 4321 → 10 → remainder 1. 1234 → 10 → remainder 1. So what remainder must the product have?"),
            t("Die Quersumme von 5.332.124 ist 20.", "The digit sum of 5,332,124 is 20."),
            t("Das Produkt müsste den Neunerrest 1 · 1 = 1 haben. 5.332.124 hat die Quersumme 20, also den Rest 2. Richtig wäre 5.332.114.", "The product would have to leave remainder 1 · 1 = 1 when divided by 9. 5,332,124 has digit sum 20, so remainder 2. The correct answer is 5,332,114."),
            t("Die Neunerprobe war jahrhundertelang die Kontrolle der Kaufleute und Buchhalter. Sie findet viele Fehler, aber nicht alle: Vertauschte Ziffern, etwa 5.332.141, ändern die Quersumme nicht und rutschen durch. Prüfziffern in IBANs und ISBNs arbeiten mit demselben Prinzip, nur mit klügeren Resten.", "For centuries casting out nines was the check used by merchants and bookkeepers. It catches many errors, but not all: swapped digits, such as 5,332,141, don't change the digit sum and slip through. Check digits in IBANs and ISBNs work on the same principle, only with cleverer remainders."),
        ),
        lab("ma.dezimalstunden", Topic.Mental, E, 0.3f) {
            val h = pick(1.3, 2.75, 0.4, 1.15, 3.2); val mins = Math.round(h * 60).toInt()
            task(
                question = l("Auf einer Handwerkerrechnung stehen ${n(h, 2)} Stunden Arbeitszeit. Wie viele Minuten sind das?", "A tradesperson's bill shows ${n(h, 2)} hours of working time. How many minutes is that?"),
                steps = listOf(num(l("Minuten", "Minutes"), mins.toDouble(), "min", abs = 0.0, done = "$mins min",
                    hints = listOf(l("Kernidee: Nach dem Komma stehen Zehntel und Hundertstel einer Stunde, keine Minuten. Eine Stunde hat 60 Minuten.", "Key idea: after the decimal point come tenths and hundredths of an hour, not minutes. An hour has 60 minutes."), """§${m(h, 2)} \cdot 60§"""))),
                solution = listOf("""§${m(h, 2)} \cdot 60 = ${mins}\ \text{min}§""", l("Das sind ${mins / 60} h ${mins % 60} min.", "That's ${mins / 60} h ${mins % 60} min.")),
                background = l("Der häufigste Fehler: 1,3 Stunden für 1 Stunde 30 Minuten zu halten. Unsere Zeit rechnet im Sechzigersystem der Babylonier, unser Zahlensystem im Zehnersystem, und beim Übergang passieren die Fehler. Stundenzettel und Abrechnungen nutzen trotzdem Dezimalstunden, weil man damit leichter multiplizieren kann.", "The most common mistake: taking 1.3 hours to mean 1 hour 30 minutes. Our time works in the Babylonians' base sixty, our numbers in base ten, and the errors happen at the switch. Timesheets and bills still use decimal hours because they're easier to multiply."),
            )
        },
        lab("ma.zehnerpotenz", Topic.Mental, E, 0.35f) {
            val (a, b) = pick(2 to 3, 2 to 4, 3 to 3, 4 to 2); val e1 = int(3, 6); val e2 = int(4, 7)
            val right = "${a * b} · 10${sup5(e1 + e2)}"
            val wrong = listOf("${a * b} · 10${sup5(e1 * e2)}", "${a + b} · 10${sup5(e1 + e2)}", "${a * b} · 10${sup5(e1 + e2 + 1)}")
            task(
                question = l("Rechne ohne Taschenrechner: (${a} · 10${sup5(e1)}) · (${b} · 10${sup5(e2)})", "Work it out without a calculator: (${a} · 10${sup5(e1)}) · (${b} · 10${sup5(e2)})"),
                steps = listOf(pick1(l("Ergebnis", "Result"), right, wrong,
                    hints = listOf(l("Kernidee: Vorfaktoren miteinander malnehmen, Zehnerpotenzen miteinander malnehmen.", "Key idea: multiply the coefficients together and the powers of ten together."), l("Zehnerpotenzen multipliziert man, indem man die Hochzahlen addiert: 10³ · 10⁴ = 10⁷.", "To multiply powers of ten you add the exponents: 10³ · 10⁴ = 10⁷.")))),
                solution = listOf(l("${a} · ${b} = ${a * b} und 10${sup5(e1)} · 10${sup5(e2)} = 10${sup5(e1 + e2)}, also ${right}.", "${a} · ${b} = ${a * b} and 10${sup5(e1)} · 10${sup5(e2)} = 10${sup5(e1 + e2)}, so ${right}.")),
                background = l("Mit Zehnerpotenzen rechnet man in der Wissenschaft mit riesigen und winzigen Zahlen, ohne Nullen zu zählen. Die Regel folgt direkt aus der Bedeutung: 10³ sind drei Zehner als Faktor, 10⁴ vier, zusammen sieben. Wer die Hochzahlen multipliziert statt addiert, liegt schnell um Billionen daneben.", "Science uses powers of ten to calculate with huge and tiny numbers without counting zeros. The rule follows straight from the meaning: 10³ is three tens as factors, 10⁴ four, seven together. Anyone who multiplies the exponents instead of adding them is soon out by trillions."),
            )
        },
        // ================================================ Prozent & Statistik
        mc("ma.simpson", Topic.Percent, H,
            t("Zwei Behandlungen gegen Nierensteine. Kleine Steine: A heilt 81 von 87 (93 %), B 234 von 270 (87 %). Große Steine: A heilt 192 von 263 (73 %), B 55 von 80 (69 %). Insgesamt: A 273 von 350 (78 %), B 289 von 350 (83 %). Welche Behandlung ist besser?", "Two treatments for kidney stones. Small stones: A cures 81 of 87 (93%), B 234 of 270 (87%). Large stones: A cures 192 of 263 (73%), B 55 of 80 (69%). Overall: A 273 of 350 (78%), B 289 of 350 (83%). Which treatment is better?"),
            t("Urteil", "Verdict"),
            t("A: Sie ist in beiden Gruppen besser. B sieht insgesamt nur besser aus, weil B vor allem bei den leichteren kleinen Steinen eingesetzt wurde.", "A: it's better in both groups. B only looks better overall because B was mainly used on the easier small stones."),
            listOf(t("B, denn insgesamt heilt sie mehr Patienten.", "B, because overall it cures more patients."), t("Beide sind gleich gut.", "Both are equally good."), t("Das lässt sich aus diesen Zahlen überhaupt nicht sagen.", "It can't be told from these numbers at all.")),
            t("Kernidee: Vergleiche A und B innerhalb jeder Gruppe. Dann schau, wie die Patienten auf die Gruppen verteilt sind.", "Key idea: compare A and B within each group. Then look at how the patients are distributed between the groups."),
            t("A wurde meist bei großen Steinen eingesetzt, die schwerer zu behandeln sind.", "A was mostly used on large stones, which are harder to treat."),
            t("In jeder Gruppe gewinnt A. Weil A aber vor allem die schweren Fälle bekam, zieht das seine Gesamtquote nach unten. Der Gesamtvergleich vermischt die Wirkung der Behandlung mit der Schwere der Fälle.", "In each group A wins. But because A mainly got the difficult cases, that drags its overall rate down. The overall comparison mixes up the effect of the treatment with the severity of the cases."),
            t("Das ist das Simpson-Paradoxon, und diese Zahlen stammen aus einer echten Studie von 1986. Es taucht überall auf: bei Zulassungsquoten von Universitäten, Medikamentenstudien und Statistiken über Gruppen. Die Lehre: Vor jedem Vergleich fragen, ob die Gruppen wirklich vergleichbar sind. Deshalb teilen gute Studien Patienten zufällig auf.", "This is Simpson's paradox, and these figures come from a real study from 1986. It turns up everywhere: in university admission rates, drug trials and statistics about groups. The lesson: before any comparison, ask whether the groups really are comparable. That's why good studies assign patients at random."),
        ),
        lab("ma.risiko", Topic.Percent, M, 0.55f) {
            val (r0, r1) = pick(2.0 to 1.0, 4.0 to 3.0, 10.0 to 5.0, 2.0 to 1.5)
            val nnt = 100 / (r0 - r1); val rel = (1 - r1 / r0) * 100
            task(
                question = l("Eine Schlagzeile: „Neues Medikament senkt das Herzinfarkt-Risiko um ${n(rel, 0)} %!“ In der Studie sank das Risiko, in fünf Jahren einen Infarkt zu bekommen, von ${n(r0, 1)} % auf ${n(r1, 1)} %. Wie viele Menschen muss man fünf Jahre lang behandeln, damit im Mittel ein Herzinfarkt verhindert wird?", "A headline: 'New drug cuts heart-attack risk by ${n(rel, 0)}%!' In the study the risk of having a heart attack within five years fell from ${n(r0, 1)}% to ${n(r1, 1)}%. How many people have to be treated for five years to prevent one heart attack on average?"),
                steps = listOf(num(l("Anzahl Behandelte", "Number treated"), nnt, "", rel = 0.01, done = "${n(nnt, 0)}",
                    hints = listOf(l("Kernidee: Die absolute Risikosenkung ist ${n(r0, 1)} % − ${n(r1, 1)} % = ${n(r0 - r1, 1)} Prozentpunkte. Von 100 Behandelten profitieren also im Mittel so viele.", "Key idea: the absolute risk reduction is ${n(r0, 1)}% − ${n(r1, 1)}% = ${n(r0 - r1, 1)} percentage points. So that's how many of every 100 treated benefit on average."), """§N = \dfrac{100}{${m(r0 - r1, 1)}}§"""))),
                solution = listOf("""§N = \dfrac{1}{${m((r0 - r1) / 100, 3)}} = ${m(nnt, 0)}§""", l("Die anderen ${n(nnt - 1, 0)} nehmen das Medikament, ohne dass es ihnen einen Infarkt erspart.", "The other ${n(nnt - 1, 0)} take the drug without it sparing them a heart attack.")),
                background = l("„Um ${n(rel, 0)} % gesenkt“ ist die relative Risikosenkung. Sie klingt gewaltig, sagt aber nichts darüber, wie groß das Risiko überhaupt war. Ärzte nutzen deshalb die Zahl der notwendigen Behandlungen (englisch NNT). Wer eine Zahl in den Nachrichten liest, sollte immer fragen: Prozent von was? Und welche Nebenwirkungen haben die vielen, die nicht profitieren?", "'Cut by ${n(rel, 0)}%' is the relative risk reduction. It sounds huge but says nothing about how big the risk was in the first place. That's why doctors use the number needed to treat (NNT). Whenever you read a figure in the news, ask: percent of what? And what side effects do all those who don't benefit get?"),
            )
        },
        mc("ma.korrelation", Topic.Percent, M,
            t("Eine Statistik zeigt: In Monaten, in denen viel Eis verkauft wird, ertrinken auch mehr Menschen. Was folgt daraus?", "A statistic shows: in months when a lot of ice cream is sold, more people also drown. What follows from this?"),
            t("Schluss", "Conclusion"),
            t("Wahrscheinlich nichts über Eis: Beides steigt im Sommer, weil es warm ist und viele baden gehen.", "Probably nothing about ice cream: both rise in summer, because it's warm and lots of people go swimming."),
            listOf(t("Eisessen macht schläfrig und führt zum Ertrinken.", "Eating ice cream makes you sleepy and leads to drowning."), t("Wer ertrinkt, hatte vorher Lust auf Eis.", "People who drown had felt like ice cream beforehand."), t("Man sollte den Eisverkauf verbieten.", "Ice cream sales should be banned.")),
            t("Kernidee: Gibt es eine dritte Größe, die beides beeinflusst?", "Key idea: is there a third quantity that influences both?"),
            t("Denk an das Wetter.", "Think about the weather."),
            t("Eine Korrelation zeigt nur, dass zwei Größen zusammen steigen und fallen. Hier treibt eine gemeinsame Ursache, das warme Wetter, beide an. Über eine Wirkung von Eis auf das Ertrinken sagt das nichts.", "A correlation only shows that two quantities rise and fall together. Here a common cause, warm weather, drives both. It says nothing about any effect of ice cream on drowning."),
            t("„Korrelation ist nicht Kausalität“ ist einer der wichtigsten Sätze der Statistik. Es gibt sogar Sammlungen absurder Zusammenhänge, etwa zwischen Käsekonsum und Unfällen mit Bettlaken. Um eine Ursache zu beweisen, braucht man Experimente, bei denen man nur eine Größe gezielt verändert, oder sehr sorgfältige Analysen, die andere Ursachen ausschließen.", "'Correlation is not causation' is one of the most important sayings in statistics. There are even collections of absurd links, such as between cheese consumption and accidents involving bedsheets. To prove a cause you need experiments in which only one quantity is deliberately changed, or very careful analyses that rule out other causes."),
        ),
        lab("ma.achsentrick", Topic.Percent, E, 0.4f) {
            val (a, b, base) = pick(Triple(98, 100, 97), Triple(49, 50, 48), Triple(196, 200, 194))
            val real = (b.toDouble() / a - 1) * 100; val looks = (b - base).toDouble() / (a - base)
            task(
                question = l("In einem Balkendiagramm wirkt der Balken von Firma B ${n(looks, 0)}-mal so hoch wie der von Firma A. Die y-Achse beginnt aber nicht bei 0, sondern bei ${base}. A hat ${a} Punkte, B ${b}. Um wie viel Prozent liegt B wirklich vor A?", "In a bar chart company B's bar looks ${n(looks, 0)} times as tall as company A's. But the y-axis doesn't start at 0, it starts at ${base}. A has ${a} points, B ${b}. By what percentage is B really ahead of A?"),
                steps = listOf(num(l("Unterschied in Prozent", "Difference in percent"), real, "%", abs = 0.1, done = """§\approx ${m(real, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Vergleiche die echten Werte, nicht die Balkenlängen.", "Key idea: compare the real values, not the lengths of the bars."), """§\dfrac{${b} - ${a}}{${a}} \cdot 100\ \%§"""))),
                solution = listOf("""§\dfrac{${b - a}}{${a}} \cdot 100\ \% \approx ${m(real, 1)}\ \%§""", l("Sichtbar sind nur die Teile über ${base}: ${b - base} gegen ${a - base}. Das erzeugt den falschen Eindruck.", "Only the parts above ${base} are visible: ${b - base} against ${a - base}. That creates the false impression.")),
                background = l("Abgeschnittene Achsen gehören zu den häufigsten Tricks in Werbung und Politik. Bei Balken sollte die Achse fast immer bei null beginnen, weil wir die Länge vergleichen. Bei Liniendiagrammen, die eine Entwicklung zeigen, ist ein Ausschnitt dagegen oft sinnvoll, solange er klar beschriftet ist. Schau bei jedem Diagramm zuerst auf die Achsen.", "Truncated axes are among the most common tricks in advertising and politics. With bars the axis should almost always start at zero, because we compare lengths. With line charts showing a trend, on the other hand, zooming in is often sensible, as long as it's clearly labelled. With any chart, look at the axes first."),
            )
        },
        lab("ma.dreifuerzwei", Topic.Percent, E, 0.35f) {
            val (take, pay) = pick(3 to 2, 4 to 3, 5 to 4, 6 to 4)
            val disc = (1 - pay.toDouble() / take) * 100
            task(
                question = l("Im Supermarkt heißt es: „Nimm ${take}, zahl ${pay}.“ Wie viel Prozent Rabatt sind das auf den einzelnen Artikel, wenn du das Angebot nutzt?", "The supermarket says: 'Take ${take}, pay for ${pay}.' What percentage discount is that per item if you use the offer?"),
                steps = listOf(num(l("Rabatt in Prozent", "Discount in percent"), disc, "%", abs = 0.2, done = """§\approx ${m(disc, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Du bekommst ${take} Artikel und bezahlst ${pay}. Wie viel zahlst du also pro Artikel im Verhältnis zum Normalpreis?", "Key idea: you get ${take} items and pay for ${pay}. So what do you pay per item relative to the normal price?"), """§1 - \dfrac{${pay}}{${take}}§"""))),
                solution = listOf("""§1 - \dfrac{${pay}}{${take}} = ${m(1 - pay.toDouble() / take, 3)} \approx ${m(disc, 1)}\ \%§"""),
                background = l("Mengenangebote klingen oft großzügiger, als sie sind. „Das zweite zum halben Preis“ sind zum Beispiel nur 25 % Rabatt, und auch nur, wenn du wirklich zwei brauchst. Händler wissen: Wer mehr kauft, verbraucht oft auch mehr. Der Grundpreis pro Kilo oder Liter auf dem Preisschild ist der ehrlichste Vergleich.", "Multi-buy offers often sound more generous than they are. 'Second one half price', for example, is only a 25% discount, and only if you really need two. Retailers know that people who buy more often use more too. The unit price per kilo or litre on the shelf label is the most honest comparison."),
            )
        },
        // ================================================ Gleichungen
        lab("ma.geradepunkte", Topic.Equations, M, 0.5f) {
            val sl = pick(2, 3, -1, 4); val b0 = pick(1, -2, 5); val x1 = int(1, 3); val x2 = x1 + pick(2, 3, 4)
            val y1 = sl * x1 + b0; val y2 = sl * x2 + b0; val y10 = sl * 10 + b0
            task(
                question = l("Eine Gerade geht durch die Punkte P(${x1} | ${y1}) und Q(${x2} | ${y2}). Welchen y-Wert hat sie bei x = 10?", "A straight line passes through the points P(${x1} | ${y1}) and Q(${x2} | ${y2}). What is its y-value at x = 10?"),
                steps = listOf(num(l("y-Wert bei x = 10", "y-value at x = 10"), y10.toDouble(), "", abs = 0.0, done = "$y10",
                    hints = listOf(l("Kernidee: Erst die Steigung aus den beiden Punkten berechnen, dann den y-Achsenabschnitt.", "Key idea: first work out the gradient from the two points, then the y-intercept."), """§m = \dfrac{${y2} - (${y1})}{${x2} - ${x1}}§"""))),
                solution = listOf("""§m = \dfrac{${y2 - y1}}{${x2 - x1}} = ${sl},\quad b = ${y1} - ${sl} \cdot ${x1} = ${b0},\quad y(10) = ${sl} \cdot 10 + (${b0}) = ${y10}§"""),
                background = l("Zwei Punkte legen eine Gerade eindeutig fest. Die Steigung sagt, um wie viel y wächst, wenn x um 1 wächst. So rechnen Handytarife mit Grundgebühr, Taxipreise und Wachstumstrends. Vorsicht beim Verlängern weit über die Messpunkte hinaus: Kaum ein echter Zusammenhang bleibt für immer gerade.", "Two points determine a straight line uniquely. The gradient says how much y grows when x grows by 1. That's how phone tariffs with a basic charge, taxi fares and growth trends work. Be careful extending far beyond the measured points: hardly any real relationship stays straight for ever."),
            )
        },
        lab("ma.scheitel", Topic.Equations, M, 0.55f) {
            val h = pick(3, 4, -2, 5); val k = pick(2, -5, 1, 7)
            val c = h * h + k
            val bTerm = if (-2 * h >= 0) "+ ${-2 * h}x" else "- ${2 * h}x"
            task(
                question = l("""Wie groß ist der kleinste Wert der Funktion §f(x) = x^2 ${bTerm} ${if (c >= 0) "+ $c" else "- ${-c}"}§?""", """What is the smallest value of the function §f(x) = x^2 ${bTerm} ${if (c >= 0) "+ $c" else "- ${-c}"}§?"""),
                steps = listOf(num(l("Kleinster Funktionswert", "Smallest value"), k.toDouble(), "", abs = 0.0, done = "$k",
                    hints = listOf(l("Kernidee: Quadratische Ergänzung. Schreibe x² ${bTerm} als (x ${if (h >= 0) "−" else "+"} ${kotlin.math.abs(h)})² minus einer Zahl.", "Key idea: complete the square. Write x² ${bTerm} as (x ${if (h >= 0) "−" else "+"} ${kotlin.math.abs(h)})² minus a number."), """§f(x) = (x ${if (h >= 0) "-" else "+"} ${kotlin.math.abs(h)})^2 + \square§"""))),
                solution = listOf("""§f(x) = (x ${if (h >= 0) "-" else "+"} ${kotlin.math.abs(h)})^2 - ${h * h} + ${if (c >= 0) "$c" else "(${c})"} = (x ${if (h >= 0) "-" else "+"} ${kotlin.math.abs(h)})^2 ${if (k >= 0) "+ $k" else "- ${-k}"}§""", l("Ein Quadrat ist nie negativ, also ist das Minimum ${k}, erreicht bei x = ${h}.", "A square is never negative, so the minimum is ${k}, reached at x = ${h}.")),
                background = l("Mit der quadratischen Ergänzung findet man den Scheitel einer Parabel ohne Ableitung. Der Trick ist uralt: Schon babylonische Tontafeln lösen so quadratische Gleichungen, mit Bildern von Quadraten, die man zu einem größeren Quadrat ergänzt. Daraus folgt auch die Lösungsformel, die du vielleicht als p-q-Formel kennst.", "Completing the square finds the vertex of a parabola without differentiating. The trick is ancient: Babylonian clay tablets already solve quadratic equations this way, with pictures of squares that are completed to make a larger square. The quadratic formula you may know follows from it too."),
            )
        },
        lab("ma.briefmarken", Topic.Equations, H, 0.65f) {
            task(
                question = l("Du hast nur Briefmarken zu 7 Cent und zu 11 Cent und willst genau 1 € (100 Cent) zusammenkleben, mit mindestens einer Marke jeder Sorte. Wie viele Marken klebst du insgesamt?", "You only have 7-cent and 11-cent stamps and want to make exactly €1 (100 cents), with at least one stamp of each kind. How many stamps do you use in total?"),
                steps = listOf(num(l("Anzahl Marken", "Number of stamps"), 12.0, "", abs = 0.0, done = "12",
                    hints = listOf(l("Kernidee: Gesucht sind ganze Zahlen x, y mit 7x + 11y = 100. Probiere y = 1, 2, 3, … und schau, ob der Rest durch 7 teilbar ist.", "Key idea: you need whole numbers x, y with 7x + 11y = 100. Try y = 1, 2, 3, … and see whether the remainder is divisible by 7."), l("100 − 11y muss ein Vielfaches von 7 sein.", "100 − 11y must be a multiple of 7.")))),
                solution = listOf(l("y = 4 ergibt 100 − 44 = 56 = 7 · 8. Also 8 Marken zu 7 Cent und 4 zu 11 Cent, zusammen 12. Es ist die einzige Lösung.", "y = 4 gives 100 − 44 = 56 = 7 · 8. So 8 stamps at 7 cents and 4 at 11 cents, 12 in all. It's the only solution.")),
                background = l("Gleichungen, die nur ganzzahlige Lösungen haben dürfen, heißen diophantische Gleichungen, nach Diophantos von Alexandria. Mit 7 und 11 lässt sich jeder Betrag ab 60 Cent bilden. Der größte unmögliche Betrag ist 7 · 11 − 7 − 11 = 59 Cent. Diese Formel fand James Joseph Sylvester 1884, und für drei Markensorten gibt es keine so einfache mehr.", "Equations that may only have whole-number solutions are called Diophantine equations, after Diophantus of Alexandria. With 7 and 11 every amount from 60 cents upwards can be made. The largest impossible amount is 7 · 11 − 7 − 11 = 59 cents. James Joseph Sylvester found this formula in 1884, and for three kinds of stamps there's no such simple one."),
            )
        },
        // ================================================ Folgen
        lab("ma.rekursion", Topic.Sequences, M, 0.5f) {
            val a1 = pick(3, 2, 5); val nn = pick(6, 8)
            val an = 1 + (a1 - 1) * 2.0.pow(nn - 1)
            task(
                question = l("""Eine Folge beginnt mit §a_1 = ${a1}§, und jedes nächste Glied entsteht so: §a_{n+1} = 2\,a_n - 1§. Wie groß ist §a_{${nn}}§?""", """A sequence starts with §a_1 = ${a1}§, and each next term is formed like this: §a_{n+1} = 2\,a_n - 1§. What is §a_{${nn}}§?"""),
                steps = listOf(num(l("Wert", "Value"), an, "", abs = 0.0, done = "${n(an, 0)}",
                    hints = listOf(l("Kernidee: Rechne ein paar Glieder aus. Oder schau dir an, wie weit jedes Glied über 1 liegt.", "Key idea: work out a few terms. Or look at how far each term is above 1."), l("Der Abstand zu 1 verdoppelt sich bei jedem Schritt.", "The distance from 1 doubles with every step.")))),
                solution = listOf("""§a_n - 1 = (a_1 - 1) \cdot 2^{n-1} \Rightarrow a_{${nn}} = 1 + ${a1 - 1} \cdot 2^{${nn - 1}} = ${m(an, 0)}§"""),
                background = l("Eine rekursive Vorschrift sagt, wie man vom einen Glied zum nächsten kommt. Der Trick hier: Man findet den Fixpunkt, die Zahl, die sich nicht ändert (2 · 1 − 1 = 1), und betrachtet den Abstand dazu. So analysiert man auch Kredite mit Raten, Populationen mit Jagd und das Abkühlen von Tee.", "A recursive rule says how to get from one term to the next. The trick here: find the fixed point, the number that doesn't change (2 · 1 − 1 = 1), and look at the distance from it. That's also how loans with repayments, populations with hunting and cooling tea are analysed."),
            )
        },
        lab("ma.sierpinski", Topic.Sequences, M, 0.55f) {
            val steps = pick(3, 5, 10); val rest = 0.75.pow(steps) * 100
            task(
                question = l("Aus einem gleichseitigen Dreieck schneidest du das mittlere Dreieck heraus, das die Seitenmitten verbindet. Übrig bleiben drei kleinere Dreiecke. Mit jedem davon machst du dasselbe, immer wieder. Wie viel Prozent der ursprünglichen Fläche sind nach ${steps} Schritten noch übrig?", "From an equilateral triangle you cut out the middle triangle joining the midpoints of the sides. Three smaller triangles are left. You do the same with each of them, again and again. What percentage of the original area is left after ${steps} steps?"),
                steps = listOf(num(l("Restfläche in Prozent", "Remaining area in percent"), rest, "%", abs = 0.1, done = """§\approx ${m(rest, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Das herausgeschnittene Mitteldreieck ist ein Viertel der Fläche. Bei jedem Schritt bleiben also drei Viertel.", "Key idea: the middle triangle cut out is a quarter of the area. So at every step three quarters remain."), """§\left(\tfrac{3}{4}\right)^{${steps}}§"""))),
                solution = listOf("""§\left(\tfrac{3}{4}\right)^{${steps}} \approx ${m(rest / 100, 3)}§"""),
                background = l("Das Sierpinski-Dreieck ist ein Fraktal: Jeder Teil sieht aus wie das Ganze. Seine Fläche geht gegen null, die Länge aller Ränder wächst dagegen unbegrenzt. Es ist weder eine Linie noch eine Fläche, sondern hat die gebrochene Dimension von etwa 1,58. Erstaunlicherweise entsteht dasselbe Muster, wenn man im Pascalschen Dreieck alle ungeraden Zahlen einfärbt.", "The Sierpinski triangle is a fractal: every part looks like the whole. Its area tends to zero, while the total length of all its edges grows without limit. It's neither a line nor an area, but has the fractional dimension of about 1.58. Amazingly, the same pattern appears if you colour in all the odd numbers in Pascal's triangle."),
            )
        },
        lab("ma.periode", Topic.Sequences, M, 0.5f) {
            val (rep, num0, den) = pick(Triple("36", 4, 11), Triple("45", 5, 11), Triple("142857", 1, 7), Triple("714285", 5, 7), Triple("81", 9, 11))
            val shown = "0," + rep.repeat(if (rep.length > 2) 2 else 3) + "…"
            task(
                question = l("Schreibe die periodische Zahl ${shown} (die Ziffern ${rep} wiederholen sich endlos) als gekürzten Bruch. Wie heißt der Nenner?", "Write the repeating decimal ${shown.replace(',', '.')} (the digits ${rep} repeat for ever) as a fraction in lowest terms. What is the denominator?"),
                steps = listOf(num(l("Nenner", "Denominator"), den.toDouble(), "", abs = 0.0, done = "$den",
                    hints = listOf(l("Kernidee: Multipliziere die Zahl mit 10${sup5(rep.length)}. Dann rutscht genau eine Periode vor das Komma, und der Rest ist wieder die alte Zahl.", "Key idea: multiply the number by 10${sup5(rep.length)}. Then exactly one period moves in front of the decimal point, and the rest is the old number again."), l("Eine Periode aus k Ziffern ist diese Ziffernfolge geteilt durch k Neunen.", "A period of k digits is that string of digits divided by k nines.")))),
                solution = listOf("""§${shown.replace("…", "\\dots").replace(",", "{,}")} = \dfrac{${rep.trimStart('0')}}{${"9".repeat(rep.length)}} = \dfrac{${num0}}{${den}}§"""),
                background = l("Jeder Bruch ist eine abbrechende oder periodische Dezimalzahl, und umgekehrt. Deshalb sind Zahlen wie π oder √2, deren Ziffern sich nie wiederholen, keine Brüche. Bei 1/7 läuft die Periode 142857 sechs Stellen lang, und alle Vielfachen bis 6/7 bestehen aus denselben Ziffern in verschobener Reihenfolge.", "Every fraction is a terminating or repeating decimal, and vice versa. That's why numbers like π or √2, whose digits never repeat, aren't fractions. For 1/7 the period 142857 runs for six digits, and all the multiples up to 6/7 consist of the same digits in rotated order."),
            )
        },
        lab("ma.kreisregionen", Topic.Sequences, H, 0.65f) {
            task(
                question = l("Auf einem Kreis liegen Punkte, und jeder wird mit jedem durch eine gerade Linie verbunden (nie drei Linien durch einen Punkt). Mit 1, 2, 3, 4, 5 Punkten entstehen 1, 2, 4, 8, 16 Gebiete. Wie viele Gebiete sind es mit 6 Punkten?", "Points lie on a circle, and each is joined to every other by a straight line (never three lines through one point). With 1, 2, 3, 4, 5 points you get 1, 2, 4, 8, 16 regions. How many regions are there with 6 points?"),
                steps = listOf(num(l("Anzahl Gebiete", "Number of regions"), 31.0, "", abs = 0.0, done = "31",
                    hints = listOf(l("Kernidee: Vorsicht, nicht einfach dem Muster trauen. Zeichne es oder zähle: Jede Linie und jeder Kreuzungspunkt im Inneren erzeugt ein neues Gebiet.", "Key idea: careful, don't just trust the pattern. Draw it or count: every line and every crossing point inside creates a new region."), """§G = 1 + \binom{n}{2} + \binom{n}{4}§"""))),
                solution = listOf("""§1 + \binom{6}{2} + \binom{6}{4} = 1 + 15 + 15 = 31§""", l("Nicht 32! Das Verdoppeln war Zufall.", "Not 32! The doubling was a coincidence.")),
                background = l("Fast jeder tippt auf 32, weil die Folge so eindeutig aussieht. Das ist das berühmteste Beispiel dafür, dass ein Muster aus wenigen Fällen kein Beweis ist. Jeder Kreuzungspunkt entsteht aus vier Punkten auf dem Kreis, daher der Binomialkoeffizient. Für 10 Punkte sind es 256 statt 512 Gebiete.", "Almost everyone guesses 32, because the sequence looks so clear. This is the most famous example showing that a pattern from a few cases is no proof. Every crossing point comes from four points on the circle, hence the binomial coefficient. For 10 points there are 256 regions, not 512."),
            )
        },
        // ================================================ Textaufgaben
        lab("ma.wechselkurs", Topic.Word, E, 0.35f) {
            val eur = pick(200, 500, 150); val rate = pick(1.08, 1.12, 1.05); val fee = 2.0
            val usd = eur * rate * (1 - fee / 100)
            task(
                question = l("Du tauschst vor dem Urlaub ${eur} € in US-Dollar. Der Kurs ist ${n(rate, 2)} US-Dollar pro Euro, die Bank behält 2 % Gebühr. Wie viele Dollar bekommst du?", "Before a holiday you change €${eur} into US dollars. The rate is ${n(rate, 2)} US dollars per euro, and the bank keeps a 2% fee. How many dollars do you get?"),
                steps = listOf(num(l("Dollar", "Dollars"), usd, "USD", rel = 0.01, done = """§${m(usd, 2)}\ \text{USD}§""",
                    hints = listOf(l("Kernidee: Erst umrechnen, dann 2 % abziehen, also mit 0,98 malnehmen. Die Reihenfolge ist egal.", "Key idea: first convert, then take off 2%, i.e. multiply by 0.98. The order doesn't matter."), """§${eur} \cdot ${m(rate, 2)} \cdot 0{,}98§"""))),
                solution = listOf("""§${eur} \cdot ${m(rate, 2)} = ${m(eur * rate, 2)},\quad \cdot\, 0{,}98 \approx ${m(usd, 2)}\ \text{USD}§"""),
                background = l("Die Gebühr steckt oft gar nicht sichtbar in der Rechnung, sondern in einem schlechteren Kurs. Vergleiche deshalb den angebotenen Kurs mit dem offiziellen Referenzkurs. Am Flughafen sind Aufschläge von 5 bis 10 % üblich. Kartenzahlung im Ausland ist meist günstiger, wenn man die Abrechnung in der Landeswährung wählt.", "The fee often isn't visible on the bill at all but hidden in a worse rate. So compare the rate offered with the official reference rate. At airports mark-ups of 5 to 10% are common. Paying by card abroad is usually cheaper if you choose to be charged in the local currency."),
            )
        },
        lab("ma.streichen", Topic.Word, M, 0.5f) {
            val walls = pick(2, 3, 4); val w = pick(4.0, 3.5); val hh = 2.5; val coats = 2; val cover = 8.0; val bucket = 2.5
            val area = walls * w * hh - 0.9 * 2.0; val litres = area * coats / cover; val buckets = ceil(litres / bucket)
            task(
                question = l("Du streichst ${walls} Wände, jede ${n(w, 1)} m breit und 2,5 m hoch. In einer davon ist eine Tür (0,9 m × 2 m), die nicht gestrichen wird. Du brauchst zwei Anstriche, und ein Liter Farbe reicht pro Anstrich für 8 m². Farbe gibt es nur in 2,5-Liter-Eimern. Wie viele Eimer musst du kaufen?", "You're painting ${walls} walls, each ${n(w, 1)} m wide and 2.5 m high. One of them has a door (0.9 m × 2 m) that isn't painted. You need two coats, and one litre of paint covers 8 m² per coat. Paint only comes in 2.5-litre tins. How many tins do you have to buy?"),
                steps = listOf(num(l("Eimer", "Tins"), buckets, "", abs = 0.0, done = "${n(buckets, 0)}",
                    hints = listOf(l("Kernidee: Fläche minus Tür, mal zwei Anstriche, durch 8 m² pro Liter. Am Ende aufrunden, halbe Eimer gibt es nicht.", "Key idea: area minus the door, times two coats, divided by 8 m² per litre. Round up at the end; there are no half tins."), """§A = ${walls} \cdot ${m(w, 1)} \cdot 2{,}5 - 1{,}8 = ${m(area, 1)}\ \text{m}^2§"""))),
                solution = listOf("""§\dfrac{${m(area, 1)} \cdot 2}{8} \approx ${m(litres, 2)}\ \text{l} \Rightarrow \dfrac{${m(litres, 2)}}{2{,}5} \approx ${m(litres / bucket, 2)} \Rightarrow ${m(buckets, 0)}\ \text{Eimer}§""".let { l(it, it.replace("Eimer", "tins")) }),
                background = l("Echte Aufgaben haben oft mehrere Schritte und eine Rundung am Schluss, bei der man nachdenken muss: Bei Farbe wird aufgerundet, bei der Zahl voller Busse, die man füllen kann, abgerundet. Profis rechnen außerdem 10 % Reserve dazu, weil raue Wände mehr Farbe schlucken. Vorher zu rechnen, spart den zweiten Weg in den Baumarkt.", "Real tasks often have several steps and a rounding at the end that needs thought: with paint you round up, with the number of buses you can fill you round down. Professionals also add 10% extra, because rough walls soak up more paint. Working it out beforehand saves a second trip to the DIY store."),
            )
        },
        lab("ma.ratenkauf", Topic.Word, M, 0.5f) {
            val (price, rate, months) = pick(Triple(499.0, 45.0, 12), Triple(899.0, 39.0, 24), Triple(299.0, 27.5, 12))
            val total = rate * months; val extra = (total / price - 1) * 100
            task(
                question = l("Ein Laptop kostet ${n(price, 0)} €. Alternativ kannst du ihn „bequem“ in ${months} Monatsraten zu je ${n(rate, 2)} € bezahlen. Um wie viel Prozent ist der Ratenkauf teurer?", "A laptop costs €${n(price, 0)}. Alternatively you can pay for it 'conveniently' in ${months} monthly instalments of €${n(rate, 2)} each. By what percentage is buying in instalments more expensive?"),
                steps = listOf(num(l("Aufpreis in Prozent", "Extra cost in percent"), extra, "%", abs = 0.1, done = """§\approx ${m(extra, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Erst die Summe aller Raten ausrechnen und dann mit dem Barpreis vergleichen.", "Key idea: first add up all the instalments, then compare with the cash price."), """§\dfrac{${months} \cdot ${m(rate, 2)}}{${m(price, 0)}} - 1§"""))),
                solution = listOf("""§${months} \cdot ${m(rate, 2)} = ${m(total, 2)}\ €,\quad \dfrac{${m(total, 2)}}{${m(price, 0)}} - 1 \approx ${m(extra, 1)}\ \%§"""),
                background = l("Weil man das Geld nach und nach zahlt, ist der echte Jahreszins sogar höher als dieser Aufpreis: Im Mittel schuldet man ja nur etwa die Hälfte des Preises. Aus ${n(extra, 1)} % Aufpreis über ${months} Monate wird so ein effektiver Jahreszins von grob ${n(extra * 2 * 12 / months, 0)} %. Bei Angeboten mit „0 % Finanzierung“ lohnt sich ein Blick, ob der Barpreis dafür höher ist.", "Because you pay the money bit by bit, the real annual interest rate is even higher than this extra cost: on average you only owe about half the price. So ${n(extra, 1)}% extra over ${months} months becomes an effective annual rate of roughly ${n(extra * 2 * 12 / months, 0)}%. With '0% finance' offers it's worth checking whether the cash price is higher instead."),
            )
        },
        lab("ma.losgehen", Topic.Word, E, 0.35f) {
            val dist = pick(1.2, 2.0, 0.8); val walkMin = Math.round(dist / 4.0 * 60).toInt(); val wrongMin = Math.round(dist / 4.0 * 100).toInt()
            val start = 7 * 60 + 55
            val right = clock(start - walkMin - 5)
            val wrong = listOf(clock(start - walkMin), clock(start - wrongMin - 5), clock(start - walkMin - 15)).distinct().filter { it != right }
            task(
                question = l("Der Unterricht beginnt um 7:55 Uhr. Dein Schulweg ist ${n(dist, 1)} km lang, du gehst mit 4 km/h, und du willst 5 Minuten Puffer haben. Wann musst du spätestens losgehen?", "School starts at 7:55. Your walk to school is ${n(dist, 1)} km, you walk at 4 km/h, and you want 5 minutes to spare. What's the latest time you can set off?"),
                steps = listOf(pick1(l("Abfahrt", "Departure"), right, wrong,
                    hints = listOf(l("Kernidee: Zeit = Strecke durch Geschwindigkeit. Das Ergebnis ist in Stunden, rechne es in Minuten um.", "Key idea: time = distance divided by speed. The result is in hours; convert it into minutes."), """§t = \dfrac{${m(dist, 1)}\ \text{km}}{4\ \text{km/h}} = ${m(dist / 4, 2)}\ \text{h}§"""))),
                solution = listOf(l("${n(dist / 4, 2)} h sind ${walkMin} Minuten. Mit 5 Minuten Puffer: 7:55 − ${walkMin + 5} min = ${right} Uhr.", "${n(dist / 4, 2)} h is ${walkMin} minutes. With 5 minutes to spare: 7:55 − ${walkMin + 5} min = ${right}.")),
                background = l("Die Falle steckt in der Umrechnung: ${n(dist / 4, 2)} Stunden sind nicht ${wrongMin} Minuten, sondern ${n(dist / 4, 2)} · 60 = ${walkMin}. Rückwärtsrechnen von einem festen Termin ist eine Alltagsform der Planung, die auch bei Zugverbindungen, beim Kochen eines Menüs oder bei Projekten funktioniert: Man beginnt beim Ende.", "The trap is in the conversion: ${n(dist / 4, 2)} hours isn't ${wrongMin} minutes, it's ${n(dist / 4, 2)} · 60 = ${walkMin}. Working backwards from a fixed time is an everyday form of planning that also works for train connections, cooking a meal or projects: you start at the end."),
            )
        },
        // ================================================ Geometrie
        lab("ma.flaechendoppelt", Topic.Geometry, E, 0.4f) {
            task(
                question = l("Ein quadratisches Beet soll doppelt so groß werden, und es soll quadratisch bleiben. Mit welchem Faktor musst du die Seitenlänge malnehmen?", "A square flowerbed is to become twice as large and stay square. By what factor must you multiply the side length?"),
                steps = listOf(num(l("Faktor", "Factor"), sqrt(2.0), "", abs = 0.005, done = """§\sqrt{2} \approx 1{,}414§""",
                    hints = listOf(l("Kernidee: Verdoppelst du die Seite, wird die Fläche viermal so groß. Gesucht ist ein Faktor k mit k² = 2.", "Key idea: if you double the side, the area becomes four times as large. You want a factor k with k² = 2."), """§k^2 = 2§"""))),
                solution = listOf("""§k = \sqrt{2} \approx 1{,}414§"""),
                background = l("In Platons Dialog „Menon“ führt Sokrates einen Jungen genau zu dieser Einsicht: Das doppelte Quadrat baut man über der Diagonale des alten. Dieselbe Zahl steckt im DIN-Papierformat: Faltet man ein A4-Blatt in der Mitte, entsteht A5 mit denselben Proportionen, weil die Seiten im Verhältnis 1 : √2 stehen. Jede Stufe halbiert die Fläche.", "In Plato's dialogue 'Meno' Socrates leads a boy to exactly this insight: the doubled square is built on the diagonal of the old one. The same number is in A-series paper sizes: fold an A4 sheet in half and you get A5 with the same proportions, because the sides are in the ratio 1 : √2. Each size halves the area."),
            )
        },
        lab("ma.thales", Topic.Geometry, M, 0.45f) {
            val alpha = pick(35, 28, 52, 61)
            task(
                question = l("Die Strecke AB ist der Durchmesser eines Kreises. Der Punkt C liegt irgendwo auf dem Kreis. Im Dreieck ABC ist der Winkel bei A ${alpha}° groß. Wie groß ist der Winkel bei B?", "The line AB is the diameter of a circle. The point C lies somewhere on the circle. In triangle ABC the angle at A is ${alpha}°. How large is the angle at B?"),
                steps = listOf(num(l("Winkel bei B in Grad", "Angle at B in degrees"), (90 - alpha).toDouble(), "°", abs = 0.0, done = "${90 - alpha}°",
                    hints = listOf(l("Kernidee: Satz des Thales: Liegt C auf einem Kreis über dem Durchmesser AB, ist der Winkel bei C immer ein rechter Winkel.", "Key idea: Thales' theorem: if C lies on a circle with diameter AB, the angle at C is always a right angle."), l("Die Winkelsumme im Dreieck ist 180°.", "The angles in a triangle add up to 180°.")))),
                solution = listOf("""§\gamma = 90^\circ \Rightarrow \beta = 180^\circ - 90^\circ - ${alpha}^\circ = ${90 - alpha}^\circ§"""),
                background = l("Der Satz wird Thales von Milet zugeschrieben, um 600 v. Chr., einem der ersten, die geometrische Aussagen bewiesen statt nur benutzten. Der Beweis ist schön: Verbindet man C mit dem Mittelpunkt, entstehen zwei gleichschenklige Dreiecke. Handwerker nutzen ihn, um rechte Winkel zu prüfen oder den Mittelpunkt eines runden Werkstücks zu finden.", "The theorem is attributed to Thales of Miletus, around 600 BC, one of the first people to prove geometric statements instead of just using them. The proof is lovely: join C to the centre and you get two isosceles triangles. Craftspeople use it to check right angles or to find the centre of a round workpiece."),
            )
        },
        lab("ma.symmetrie", Topic.Geometry, E, 0.3f) {
            val (shape, axes) = pick(l("ein regelmäßiges Sechseck", "a regular hexagon") to 6, l("ein Quadrat", "a square") to 4, l("ein Rechteck, das kein Quadrat ist", "a rectangle that isn't a square") to 2, l("ein gleichseitiges Dreieck", "an equilateral triangle") to 3, l("ein regelmäßiges Fünfeck", "a regular pentagon") to 5)
            task(
                question = l("Wie viele Symmetrieachsen hat $shape? (Eine Symmetrieachse ist eine Linie, an der man die Figur so falten kann, dass beide Hälften genau aufeinanderliegen.)", "How many lines of symmetry does $shape have? (A line of symmetry is a line along which the shape can be folded so that both halves match exactly.)"),
                steps = listOf(num(l("Symmetrieachsen", "Lines of symmetry"), axes.toDouble(), "", abs = 0.0, done = "$axes",
                    hints = listOf(l("Kernidee: Mögliche Achsen gehen durch Ecken oder durch Seitenmitten. Zeichne die Figur und probiere.", "Key idea: possible lines pass through corners or through the midpoints of sides. Draw the shape and try."), l("Beim Rechteck: Gehen die Diagonalen? Falte in Gedanken.", "For the rectangle: do the diagonals work? Fold it in your head.")))),
                solution = listOf(l("$shape hat ${axes} Symmetrieachsen.", "$shape has ${axes} lines of symmetry.").replaceFirstChar { it.uppercase() }),
                background = l("Ein regelmäßiges n-Eck hat genau n Symmetrieachsen, ein Kreis unendlich viele. Beim Rechteck ist die Diagonale eine beliebte Falle: Faltet man dort, passen die Hälften nicht. Schneeflocken haben sechs Achsen, weil sich Wassermoleküle im Eis in Sechsecken anordnen. Symmetrien zu zählen ist der Einstieg in die Gruppentheorie, mit der Physiker Elementarteilchen ordnen.", "A regular n-gon has exactly n lines of symmetry, a circle infinitely many. With a rectangle the diagonal is a popular trap: fold along it and the halves don't match. Snowflakes have six lines because water molecules in ice arrange themselves in hexagons. Counting symmetries is the way into group theory, which physicists use to classify elementary particles."),
            )
        },
        lab("ma.dreieckkoord", Topic.Geometry, M, 0.55f) {
            val pts = pick(listOf(1, 1, 7, 2, 3, 6), listOf(0, 0, 8, 0, 3, 5), listOf(2, 1, 6, 3, 1, 5))
            val x1 = pts[0]; val y1 = pts[1]; val x2 = pts[2]; val y2 = pts[3]; val x3 = pts[4]; val y3 = pts[5]
            val area = kotlin.math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0
            task(
                question = l("Ein Dreieck hat die Eckpunkte A(${x1} | ${y1}), B(${x2} | ${y2}) und C(${x3} | ${y3}). Wie groß ist seine Fläche?", "A triangle has the vertices A(${x1} | ${y1}), B(${x2} | ${y2}) and C(${x3} | ${y3}). What is its area?"),
                steps = listOf(num(l("Fläche", "Area"), area, "", abs = 0.0, done = "${n(area, 1)}",
                    hints = listOf(l("Kernidee: Entweder ein Rechteck um das Dreieck legen und die drei Eckdreiecke abziehen, oder die Formel nach Gauß benutzen.", "Key idea: either draw a rectangle round the triangle and subtract the three corner triangles, or use Gauss's formula."), """§A = \tfrac{1}{2}\left| x_1(y_2 - y_3) + x_2(y_3 - y_1) + x_3(y_1 - y_2) \right|§"""))),
                solution = listOf("""§A = \tfrac{1}{2}\left| ${x1} \cdot (${y2 - y3}) + ${x2} \cdot (${y3 - y1}) + ${x3} \cdot (${y1 - y2}) \right| = ${m(area, 1)}§"""),
                background = l("Diese Formel heißt Gaußsche Trapezformel oder Schnürsenkelformel, weil man die Koordinaten über Kreuz multipliziert wie beim Schnüren. Sie funktioniert für jedes Vieleck, wenn man die Ecken der Reihe nach durchgeht. Landvermesser und Kartenprogramme berechnen so die Fläche von Grundstücken direkt aus GPS-Koordinaten.", "This formula is called the shoelace formula, because you multiply the coordinates crosswise as in lacing a shoe. It works for any polygon if you go round the corners in order. Surveyors and mapping software calculate the area of plots of land directly from GPS coordinates this way."),
            )
        },
        lab("ma.wuerfelseiten", Topic.Geometry, E, 0.35f) {
            val vis = pick(listOf(1, 2, 3), listOf(4, 5, 6), listOf(2, 3, 6), listOf(1, 4, 5))
            val hidden = 21 - vis.sum()
            task(
                question = l("Auf einem normalen Spielwürfel ergeben gegenüberliegende Seiten immer zusammen 7. Du siehst an einer Ecke drei Seiten: ${vis.joinToString(", ")}. Wie groß ist die Summe der drei Seiten, die du nicht siehst?", "On a standard die, opposite faces always add up to 7. At one corner you can see three faces: ${vis.joinToString(", ")}. What is the sum of the three faces you can't see?"),
                steps = listOf(num(l("Summe der verdeckten Seiten", "Sum of the hidden faces"), hidden.toDouble(), "", abs = 0.0, done = "$hidden",
                    hints = listOf(l("Kernidee: Jede verdeckte Seite liegt genau einer sichtbaren gegenüber.", "Key idea: each hidden face is exactly opposite one visible face."), l("Alle sechs Seiten zusammen ergeben 1 + 2 + … + 6 = 21.", "All six faces together add up to 1 + 2 + … + 6 = 21.")))),
                solution = listOf("""§21 - (${vis.joinToString(" + ")}) = ${hidden}§""", l("Oder: 3 · 7 − ${vis.sum()} = ${hidden}.", "Or: 3 · 7 − ${vis.sum()} = ${hidden}.")),
                background = l("Räumliches Denken lässt sich trainieren, und Würfel sind das klassische Übungsobjekt. Die Regel mit der 7 gilt auf fast allen Würfeln der Welt, seit Jahrtausenden. Zauberkünstler nutzen sie für Tricks, bei denen sie scheinbar durch einen Würfelturm hindurchsehen: Sie addieren einfach Siebener.", "Spatial thinking can be trained, and dice are the classic practice object. The rule of 7 holds on almost every die in the world and has done for thousands of years. Magicians use it for tricks in which they seem to see through a tower of dice: they're simply adding up sevens."),
            )
        },
        lab("ma.bemalterwuerfel", Topic.Geometry, H, 0.6f) {
            val nn = pick(3, 4, 5); val twoFaces = chance(0.5)
            val v = if (twoFaces) 12 * (nn - 2) else (nn - 2) * (nn - 2) * (nn - 2)
            task(
                question = l("Ein großer Holzwürfel wird außen rundherum rot angemalt und dann in ${nn} × ${nn} × ${nn} = ${nn * nn * nn} gleiche kleine Würfel zersägt. Wie viele kleine Würfel haben ${if (twoFaces) "genau zwei rote Seiten" else "gar keine rote Seite"}?", "A large wooden cube is painted red all over the outside and then sawn into ${nn} × ${nn} × ${nn} = ${nn * nn * nn} equal small cubes. How many small cubes have ${if (twoFaces) "exactly two red faces" else "no red face at all"}?"),
                steps = listOf(num(l("Anzahl", "Number"), v.toDouble(), "", abs = 0.0, done = "$v",
                    hints = listOf(if (twoFaces) l("Kernidee: Zwei rote Seiten haben die Würfel an den Kanten, aber nicht an den Ecken (die haben drei).", "Key idea: cubes on the edges have two red faces, but not those at the corners (they have three).") else l("Kernidee: Ganz ohne Farbe sind nur die Würfel im Inneren. Schäl in Gedanken die äußerste Schicht ab.", "Key idea: only the cubes inside have no paint at all. In your mind, peel off the outermost layer."),
                        if (twoFaces) l("Ein Würfel hat 12 Kanten. Auf jeder liegen ${nn} kleine Würfel, davon 2 an den Ecken.", "A cube has 12 edges. Each has ${nn} small cubes on it, 2 of them at the corners.") else l("Übrig bleibt ein Würfel mit ${nn - 2} kleinen Würfeln Kantenlänge.", "What's left is a cube ${nn - 2} small cubes along each edge.")))),
                solution = listOf(if (twoFaces) """§12 \cdot (${nn} - 2) = ${v}§""" else """§(${nn} - 2)^3 = ${v}§"""),
                background = l("Insgesamt gibt es vier Sorten: 8 Eckwürfel mit drei Farbseiten, 12·(n − 2) Kantenwürfel mit zwei, 6·(n − 2)² Flächenwürfel mit einer und (n − 2)³ innere ohne Farbe. Zusammen ergeben sie genau n³. Solche Zählaufgaben trainieren, ein Problem in überschaubare Klassen zu zerlegen, statt alles einzeln abzuzählen.", "Altogether there are four kinds: 8 corner cubes with three painted faces, 12·(n − 2) edge cubes with two, 6·(n − 2)² face cubes with one and (n − 2)³ inner ones with no paint. Together they make exactly n³. Counting problems like this train you to break a problem into manageable classes instead of counting everything one by one."),
            )
        },
        // ================================================ Wahrscheinlichkeit
        mc("ma.petersburg", Topic.Probability, X,
            t("Ein Spiel: Eine Münze wird geworfen, bis zum ersten Mal Kopf kommt. Kommt Kopf im ersten Wurf, bekommst du 2 €, im zweiten 4 €, im dritten 8 €, und so weiter, jedes Mal doppelt so viel. Wie viel ist dieses Spiel nach dem Erwartungswert wert?", "A game: a coin is tossed until heads comes up for the first time. If heads comes on the first toss you get €2, on the second €4, on the third €8, and so on, twice as much each time. According to the expected value, how much is this game worth?"),
            t("Erwartungswert", "Expected value"),
            t("Unendlich viel, obwohl kaum jemand mehr als 20 € dafür zahlen würde", "Infinitely much, even though hardly anyone would pay more than €20 for it"),
            listOf(t("Genau 2 €", "Exactly €2"), t("Etwa 4 €", "About €4"), t("Genau 100 €", "Exactly €100")),
            t("Kernidee: Jeder mögliche Ausgang trägt Gewinn mal Wahrscheinlichkeit zum Erwartungswert bei.", "Key idea: each possible outcome contributes winnings times probability to the expected value."),
            t("Kopf im k-ten Wurf: Wahrscheinlichkeit (1/2)^k, Gewinn 2^k €. Was ergibt das Produkt?", "Heads on toss k: probability (1/2)^k, winnings €2^k. What is the product?"),
            t("Jeder Ausgang trägt (1/2)^k · 2^k = 1 € bei, und es gibt unendlich viele Ausgänge: 1 + 1 + 1 + … wächst ohne Grenze. Trotzdem gewinnt man meist nur 2 oder 4 €.", "Each outcome contributes (1/2)^k · 2^k = €1, and there are infinitely many outcomes: 1 + 1 + 1 + … grows without limit. Yet you usually only win €2 or €4."),
            t("Das St.-Petersburg-Paradoxon beschäftigte die Brüder Bernoulli im 18. Jahrhundert. Daniel Bernoulli schlug vor, dass zusätzliches Geld umso weniger Nutzen bringt, je mehr man schon hat, eine Grundidee der Wirtschaftswissenschaft. Außerdem kann keine Bank unendlich viel auszahlen: Begrenzt man den Höchstgewinn auf eine Milliarde, ist das Spiel nur noch etwa 30 € wert.", "The St Petersburg paradox occupied the Bernoulli family in the 18th century. Daniel Bernoulli suggested that extra money brings less benefit the more you already have, a basic idea of economics. Besides, no bank can pay out infinitely much: if you cap the top prize at a billion, the game is only worth about €30."),
        ),
        mc("ma.krankenhaus", Topic.Probability, M,
            t("In einer Stadt gibt es ein großes Krankenhaus mit etwa 45 Geburten am Tag und ein kleines mit etwa 15. Im Schnitt ist die Hälfte der Babys ein Junge. Welches Krankenhaus zählt im Jahr mehr Tage, an denen über 60 % der Neugeborenen Jungen waren?", "A town has a large hospital with about 45 births a day and a small one with about 15. On average half the babies are boys. Which hospital records more days in a year on which over 60% of the newborns were boys?"),
            t("Krankenhaus", "Hospital"),
            t("Das kleine", "The small one"),
            listOf(t("Das große", "The large one"), t("Beide etwa gleich viele", "Both about the same"), t("Keines, so etwas kommt nie vor", "Neither; that never happens")),
            t("Kernidee: Wo schwankt ein Anteil stärker vom Mittelwert weg, bei wenigen oder bei vielen Fällen?", "Key idea: where does a proportion swing further from the average, with few cases or with many?"),
            t("Denk an Münzwürfe: Bei 4 Würfen ist „3-mal Kopf“ häufig, bei 400 Würfen ist „300-mal Kopf“ fast unmöglich.", "Think of coin tosses: with 4 tosses '3 heads' is common; with 400 tosses '300 heads' is almost impossible."),
            t("Kleine Stichproben schwanken stärker. Bei 15 Geburten sind 9 oder mehr Jungen nicht ungewöhnlich, bei 45 Geburten weichen die Anteile seltener so weit von 50 % ab.", "Small samples fluctuate more. With 15 births, 9 or more boys isn't unusual; with 45 births the proportions stray that far from 50% less often."),
            t("Die Psychologen Daniel Kahneman und Amos Tversky stellten diese Frage Studierenden, und die meisten meinten, es sei egal. Dieselbe Falle führt zu Fehlschlüssen in Nachrichten: Die Kreise mit der höchsten und der niedrigsten Krebsrate sind oft beide kleine Landkreise, einfach weil kleine Zahlen stärker streuen. Das ist das Gesetz der kleinen Zahlen.", "The psychologists Daniel Kahneman and Amos Tversky put this question to students, and most thought it made no difference. The same trap leads to wrong conclusions in the news: the districts with the highest and the lowest cancer rates are often both small rural districts, simply because small numbers scatter more. This is the law of small numbers."),
        ),
        mc("ma.zufallsfolge", Topic.Probability, E,
            t("Welche Folge von sechs Münzwürfen (K = Kopf, Z = Zahl) ist wahrscheinlicher: K K K K K K oder K Z K K Z K?", "Which sequence of six coin tosses (H = heads, T = tails) is more likely: H H H H H H or H T H H T H?"),
            t("Antwort", "Answer"),
            t("Beide sind genau gleich wahrscheinlich.", "Both are exactly equally likely."),
            listOf(t("K Z K K Z K, weil sie zufälliger aussieht", "H T H H T H, because it looks more random"), t("K K K K K K, weil Kopf öfter kommt", "H H H H H H, because heads comes up more often"), t("K Z K K Z K, und zwar sechsmal so wahrscheinlich", "H T H H T H, and six times as likely")),
            t("Kernidee: Jede ganz bestimmte Folge von sechs Würfen hat dieselbe Wahrscheinlichkeit.", "Key idea: every completely specific sequence of six tosses has the same probability."),
            t("""Jeder Wurf hat 1/2. Für sechs bestimmte Ergebnisse: §(1/2)^6§.""", """Each toss has 1/2. For six specific results: §(1/2)^6§."""),
            t("Beide Folgen haben die Wahrscheinlichkeit 1/64. „Fünfmal Kopf und einmal Zahl in irgendeiner Reihenfolge“ ist zwar wahrscheinlicher als „sechsmal Kopf“, aber diese eine Reihenfolge ist es nicht.", "Both sequences have probability 1/64. 'Five heads and one tail in any order' is indeed more likely than 'six heads', but this one particular order isn't."),
            t("Unser Gefühl verwechselt „sieht typisch aus“ mit „ist wahrscheinlich“. Deshalb erkennt man erfundene Zufallsfolgen oft daran, dass ihnen lange Serien fehlen: Menschen wechseln zu oft. Bei 100 echten Würfen kommt fast immer irgendwo eine Serie von fünf oder sechs gleichen vor. Lottozahlen wie 1, 2, 3, 4, 5, 6 sind genauso wahrscheinlich wie jede andere Reihe.", "Our intuition confuses 'looks typical' with 'is likely'. That's why made-up random sequences can often be spotted by their lack of long runs: people switch too often. In 100 real tosses there's almost always a run of five or six the same somewhere. Lottery numbers like 1, 2, 3, 4, 5, 6 are just as likely as any other set."),
        ),
        lab("ma.efron", Topic.Probability, H, 0.65f) {
            task(
                question = l("Drei faire Würfel mit ungewöhnlicher Beschriftung: A: 2, 2, 4, 4, 9, 9. B: 1, 1, 6, 6, 8, 8. C: 3, 3, 5, 5, 7, 7. A zeigt gegen B in 5/9 der Fälle die höhere Zahl, B gegen C ebenfalls in 5/9. Wie oft gewinnt C gegen A, in Prozent?", "Three fair dice with unusual numbering: A: 2, 2, 4, 4, 9, 9. B: 1, 1, 6, 6, 8, 8. C: 3, 3, 5, 5, 7, 7. A shows the higher number against B in 5/9 of cases, and B against C also in 5/9. How often does C beat A, in percent?"),
                steps = listOf(num(l("C gewinnt gegen A in Prozent", "C beats A, in percent"), 500.0 / 9, "%", abs = 0.3, done = """§\tfrac{5}{9} \approx 55{,}6\ \%§""",
                    hints = listOf(l("Kernidee: Jede Zahl kommt auf beiden Würfeln mit Wahrscheinlichkeit 1/3 vor. Geh die drei Werte von C durch und frag, gegen welche Werte von A sie gewinnen.", "Key idea: each number appears on each die with probability 1/3. Go through C's three values and ask which of A's values they beat."), l("C = 3 schlägt nur die 2. C = 5 und C = 7 schlagen 2 und 4.", "C = 3 only beats the 2. C = 5 and C = 7 beat 2 and 4.")))),
                solution = listOf("""§P = \tfrac{1}{3}\left(\tfrac{1}{3} + \tfrac{2}{3} + \tfrac{2}{3}\right) = \tfrac{5}{9} \approx 55{,}6\ \%§""", l("A schlägt B, B schlägt C, und trotzdem schlägt C den Würfel A.", "A beats B, B beats C, and yet C beats A.")),
                background = l("Das sind nicht-transitive Würfel, wie Schere, Stein, Papier: Es gibt keinen besten Würfel. Lässt du dein Gegenüber zuerst wählen, kannst du immer einen nehmen, der seinen schlägt. Der Investor Warren Buffett soll Bill Gates einmal solche Würfel angeboten haben, und Gates bestand darauf, dass Buffett zuerst wählt. Für Wahlen und Rankings bedeutet das: Eine faire Rangfolge muss nicht existieren.", "These are non-transitive dice, like rock, paper, scissors: there's no best die. If you let your opponent choose first, you can always take one that beats theirs. The investor Warren Buffett is said to have once offered Bill Gates such dice, and Gates insisted that Buffett choose first. For elections and rankings this means a fair ordering need not exist."),
            )
        },
        // ================================================ Analysis
        lab("ma.punktsymmetrie", Topic.Calculus, M, 0.5f) {
            val a = pick(2, 3, 5); val c = pick(4, 7, 2)
            task(
                question = l("""Berechne §\displaystyle\int_{-${a}}^{${a}} \left(x^3 - ${c}x\right) dx§. Gibt es einen Weg ganz ohne Rechnen?""", """Work out §\displaystyle\int_{-${a}}^{${a}} \left(x^3 - ${c}x\right) dx§. Is there a way without any calculating?"""),
                steps = listOf(num(l("Wert des Integrals", "Value of the integral"), 0.0, "", abs = 0.001, done = "0",
                    hints = listOf(l("Kernidee: Setze −x statt x ein. Was passiert mit dem Funktionswert?", "Key idea: substitute −x for x. What happens to the function value?"), l("Die Funktion ist punktsymmetrisch zum Ursprung: Links und rechts liegen gleich große Flächen, eine unter und eine über der Achse.", "The function is symmetric about the origin: left and right there are equal areas, one below and one above the axis.")))),
                solution = listOf("""§f(-x) = -f(x) \Rightarrow \int_{-${a}}^{${a}} f(x)\,dx = 0§"""),
                background = l("Ein Integral zählt Flächen unter der Achse negativ. Bei ungeraden Funktionen, etwa x, x³ oder sin x, heben sich die Teile links und rechts exakt auf, wenn die Grenzen symmetrisch sind. Physiker sparen sich mit solchen Symmetrieargumenten seitenlange Rechnungen. Vorsicht: Die echte, geometrische Fläche zwischen Kurve und Achse ist natürlich nicht null.", "An integral counts areas below the axis as negative. For odd functions such as x, x³ or sin x, the parts on the left and right cancel exactly if the limits are symmetric. Physicists save themselves pages of calculation with symmetry arguments like this. Careful: the actual geometric area between the curve and the axis is of course not zero."),
            )
        },
        mc("ma.hochpunktgeschw", Topic.Calculus, M,
            t("Das Weg-Zeit-Diagramm eines Balls, der senkrecht hochgeworfen wird, hat bei t = 2 s einen Hochpunkt. Was weißt du über die Geschwindigkeit des Balls in diesem Moment?", "The height–time graph of a ball thrown straight up has a maximum at t = 2 s. What do you know about the ball's velocity at that moment?"),
            t("Geschwindigkeit bei t = 2 s", "Velocity at t = 2 s"),
            t("Sie ist null.", "It's zero."),
            listOf(t("Sie ist am größten.", "It's at its greatest."), t("Sie ist gleich der Fallbeschleunigung.", "It equals the acceleration due to gravity."), t("Das lässt sich nicht sagen.", "It can't be determined.")),
            t("Kernidee: Die Geschwindigkeit ist die Steigung im Weg-Zeit-Diagramm.", "Key idea: the velocity is the gradient in the height–time graph."),
            t("Wie steil ist eine Kurve genau an ihrem höchsten Punkt?", "How steep is a curve exactly at its highest point?"),
            t("Am Hochpunkt ist die Tangente waagrecht, die Steigung und damit die Geschwindigkeit ist null. Die Beschleunigung ist dort aber nicht null, sondern weiterhin 9,81 m/s² nach unten.", "At the maximum the tangent is horizontal, so the gradient, and hence the velocity, is zero. The acceleration there isn't zero, though: it's still 9.81 m/s² downwards."),
            t("Genau dieses Prinzip, Ableitung gleich null, nutzt man, um Maxima und Minima zu finden: den höchsten Punkt eines Wurfs, den geringsten Materialverbrauch oder den größten Gewinn. Viele verwechseln dabei Geschwindigkeit und Beschleunigung. Der Ball steht im höchsten Punkt nur für einen einzigen Augenblick still.", "Exactly this principle, derivative equals zero, is used to find maxima and minima: the highest point of a throw, the least material used or the greatest profit. Many people confuse velocity and acceleration here. At the highest point the ball is only still for a single instant."),
        ),
        lab("ma.hebbareluecke", Topic.Calculus, M, 0.5f) {
            val a = pick(2, 3, 5, 4)
            task(
                question = l("""Die Funktion §f(x) = \dfrac{x^2 - ${a * a}}{x - ${a}}§ ist bei x = ${a} nicht definiert, weil man dort durch null teilen würde. Gegen welchen Wert strebt sie, wenn x gegen ${a} geht?""", """The function §f(x) = \dfrac{x^2 - ${a * a}}{x - ${a}}§ isn't defined at x = ${a}, because you'd be dividing by zero there. What value does it approach as x tends to ${a}?"""),
                steps = listOf(num(l("Grenzwert", "Limit"), 2.0 * a, "", abs = 0.0, done = "${2 * a}",
                    hints = listOf(l("Kernidee: Zerlege den Zähler mit der dritten binomischen Formel.", "Key idea: factorise the numerator using the difference of two squares."), """§x^2 - ${a * a} = (x - ${a})(x + ${a})§"""))),
                solution = listOf("""§\dfrac{(x - ${a})(x + ${a})}{x - ${a}} = x + ${a} \to ${2 * a}§"""),
                background = l("Der Graph ist eine Gerade mit einem einzigen fehlenden Punkt, einem Loch. Solche hebbaren Lücken zeigen, warum Grenzwerte nötig sind: Der Wert an der Stelle existiert nicht, aber das Verhalten in der Nähe ist eindeutig. Genau so ist die Ableitung definiert, als Grenzwert eines Bruchs, dessen Nenner gegen null geht.", "The graph is a straight line with a single missing point, a hole. Removable gaps like this show why limits are needed: the value at the point doesn't exist, but the behaviour nearby is perfectly clear. That's exactly how the derivative is defined, as the limit of a fraction whose denominator tends to zero."),
            )
        },
        mc("ma.wachstumsvergleich", Topic.Calculus, H,
            t("Welche Funktion wird für sehr große x irgendwann größer und bleibt dann für immer größer: x¹⁰⁰ oder 1,01ˣ?", "Which function eventually becomes larger for very large x and then stays larger for ever: x¹⁰⁰ or 1.01ˣ?"),
            t("Gewinner", "Winner"),
            t("1,01ˣ, auch wenn das erst ab etwa x ≈ 117.000 passiert", "1.01ˣ, even though that only happens from about x ≈ 117,000"),
            listOf(t("x¹⁰⁰, weil die Hochzahl 100 so groß ist", "x¹⁰⁰, because the exponent 100 is so large"), t("Keine, beide wachsen gleich schnell.", "Neither; both grow equally fast."), t("x¹⁰⁰, weil 1,01 fast 1 ist", "x¹⁰⁰, because 1.01 is almost 1")),
            t("Kernidee: Vergleiche, wie stark die Funktionen wachsen, wenn x um 1 größer wird.", "Key idea: compare how much the functions grow when x increases by 1."),
            t("1,01ˣ wächst bei jedem Schritt um 1 %. Um wie viel Prozent wächst x¹⁰⁰ bei großem x noch?", "1.01ˣ grows by 1% at every step. By what percentage does x¹⁰⁰ still grow when x is large?"),
            t("Bei x¹⁰⁰ schrumpft der relative Zuwachs pro Schritt immer weiter (er ist etwa 100/x), bei 1,01ˣ bleibt er immer 0,01, also 1 %. Ab x ≈ 10.000 wächst die Exponentialfunktion schneller und überholt die Potenz schließlich bei rund 117.000.", "For x¹⁰⁰ the relative growth per step keeps shrinking (it's about 100/x); for 1.01ˣ it always stays 0.01, i.e. 1%. From x ≈ 10,000 the exponential grows faster and finally overtakes the power at around 117,000."),
            t("Jede Exponentialfunktion mit Basis über 1 schlägt am Ende jede Potenzfunktion, egal wie groß die Hochzahl ist. In der Informatik ist das der Unterschied zwischen lösbaren und praktisch unlösbaren Problemen: Ein Algorithmus mit Laufzeit n³ bleibt beherrschbar, einer mit 2ⁿ explodiert schon bei kleinen Eingaben.", "Every exponential function with a base above 1 eventually beats every power function, however large the exponent. In computer science this is the difference between solvable and practically unsolvable problems: an algorithm with running time n³ stays manageable, while one with 2ⁿ explodes even for small inputs."),
        ),
        mc("ma.eigeneableitung", Topic.Calculus, E,
            t("Welche Funktion ist gleich ihrer eigenen Ableitung, wächst also an jeder Stelle genau so schnell, wie sie groß ist?", "Which function is equal to its own derivative, i.e. grows at every point exactly as fast as it is large?"),
            t("Funktion", "Function"),
            t("eˣ", "eˣ"),
            listOf(t("x²", "x²"), t("1/x", "1/x"), t("sin x", "sin x")),
            t("Kernidee: Leite die Kandidaten ab: Aus x² wird 2x, aus sin x wird cos x.", "Key idea: differentiate the candidates: x² becomes 2x, sin x becomes cos x."),
            t("Gesucht ist eine Funktion, bei der die Wachstumsrate immer gleich dem aktuellen Wert ist, wie bei ungebremster Vermehrung.", "You want a function whose growth rate always equals its current value, as with unchecked reproduction."),
            t("Die e-Funktion mit e ≈ 2,718 ist die einzige Funktion (bis auf einen Faktor), deren Ableitung wieder sie selbst ist.", "The exponential function with e ≈ 2.718 is the only function (up to a constant factor) whose derivative is itself again."),
            t("Deshalb taucht e überall auf, wo etwas proportional zu sich selbst wächst oder schrumpft: Zinseszins mit ständiger Verzinsung, Bakterien, radioaktiver Zerfall und das Abkühlen von Kaffee. Die Zahl e entdeckte Jacob Bernoulli 1683 bei einer Frage über Zinsen, Leonhard Euler gab ihr ihren Namen.", "That's why e turns up wherever something grows or shrinks in proportion to itself: continuously compounded interest, bacteria, radioactive decay and coffee cooling. Jacob Bernoulli discovered the number e in 1683 while asking a question about interest; Leonhard Euler gave it its name."),
        ),
    )
}
