package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

// =============================================================================================
// Weitere Aufgaben (seit 9.9), damit Themen-Runden nicht nach wenigen Aufgaben leer laufen.
// Gleiche Regeln wie in TaskBank: § steht für $, die erste Auswahl-Option ist die richtige,
// Ergebnisse werden im Code berechnet.
// =============================================================================================

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object TaskBankMore {
    val all: List<TaskDef> = listOf(
        // ========================================== MATHE ==========================================
        TaskDef("sekunden-tag", Topic.Mental, E) {
            Task(
                "sekunden-tag", Topic.Mental, E,
                question = l("Wie viele Sekunden hat ein Tag?", "How many seconds are there in a day?"),
                steps = listOf(
                    num(
                        """§24 \cdot 60 \cdot 60 = \square§""", 86_400.0, "s", abs = 0.0,
                        done = l("86.400 Sekunden", "86,400 seconds"),
                        hints = listOf(l("Eine Stunde hat 3600 Sekunden.", "An hour has 3600 seconds."), """§24 \cdot 3600§"""),
                    ),
                ),
                solution = listOf("""§24 \cdot 60 \cdot 60 = 24 \cdot 3600 = 86\,400§"""),
                background = l(
                    "Dass ein Tag 24 Stunden und eine Stunde 60 Minuten hat, stammt aus der Antike: Die Ägypter teilten Tag und Nacht in je 12 Stunden, die Babylonier rechneten mit der 60. Die 60 ist praktisch, weil man sie durch 2, 3, 4, 5, 6, 10, 12, 15, 20 und 30 teilen kann.",
                    "That a day has 24 hours and an hour 60 minutes goes back to antiquity: the Egyptians split day and night into 12 hours each, and the Babylonians counted in sixties. 60 is handy because it can be divided by 2, 3, 4, 5, 6, 10, 12, 15, 20 and 30.",
                ),
                fact = l("Ganz genau 86.400 Sekunden sind es nicht: Die Erde wird durch die Gezeiten langsam gebremst, der Tag wird pro Jahrhundert etwa 2 Millisekunden länger.", "It isn't exactly 86,400 seconds: tides slowly brake the Earth, and the day gets about 2 milliseconds longer per century."),
            )
        },
        TaskDef("zinseszins", Topic.Percent, M) {
            val k = 1000 * 1.05.pow(10)
            val t = ln(2.0) / ln(1.05)
            Task(
                "zinseszins", Topic.Percent, M,
                question = l("Du legst 1000 € zu 5 % Zinsen pro Jahr an und lässt die Zinsen mitwachsen.", "You invest €1000 at 5 % interest per year and let the interest grow along."),
                steps = listOf(
                    num(
                        l("Wie viel hast du nach 10 Jahren?", "How much do you have after 10 years?"), k, "€", rel = 0.005,
                        done = l("""§\approx 1628{,}89\ €§""", """§\approx €1628.89§"""),
                        hints = listOf(l("Kernidee: Jedes Jahr wird der ganze Betrag mal 1,05 genommen.", "Key idea: every year the whole amount is multiplied by 1.05."), l("""§1000 \cdot 1{,}05^{10}§""", """§1000 \cdot 1.05^{10}§""")),
                        formula = card(l("Zinseszins", "Compound interest"), """§K_n = K_0 \cdot (1 + p)^n§""", l("""§p§ ist der Zinssatz als Dezimalzahl.""", """§p§ is the interest rate as a decimal.""")),
                    ),
                    num(
                        l("Nach wie vielen Jahren hat sich das Geld verdoppelt?", "After how many years has the money doubled?"), t, l("Jahre", "years"), rel = 0.03,
                        done = l("nach gut 14 Jahren", "after just over 14 years"),
                        hints = listOf(l("""Löse §1{,}05^n = 2§.""", """Solve §1.05^n = 2§."""), l("""§n = \dfrac{\ln 2}{\ln 1{,}05}§""", """§n = \dfrac{\ln 2}{\ln 1.05}§""")),
                    ),
                ),
                solution = listOf(
                    l("""§K_{10} = 1000 \cdot 1{,}05^{10} \approx 1628{,}89\ €§""", """§K_{10} = 1000 \cdot 1.05^{10} \approx €1628.89§"""),
                    l("""§1{,}05^n = 2 \;\Rightarrow\; n = \dfrac{\ln 2}{\ln 1{,}05} \approx 14{,}2§ Jahre""", """§1.05^n = 2 \;\Rightarrow\; n = \dfrac{\ln 2}{\ln 1.05} \approx 14.2§ years"""),
                ),
                background = l(
                    "Beim Zinseszins bekommst du nicht nur Zinsen auf dein Geld, sondern auch auf die Zinsen vom Vorjahr. Dadurch wächst der Betrag nicht gleichmäßig, sondern exponentiell: Am Anfang passiert wenig, später sehr viel. Darum lohnt es sich, früh anzufangen, und darum sind Schulden mit hohen Zinsen so gefährlich.",
                    "With compound interest you don't just earn interest on your money, but also on last year's interest. The amount therefore doesn't grow evenly but exponentially: little happens at first, a lot later on. That's why starting early pays off, and why debts with high interest are so dangerous.",
                ),
                fact = l("Faustregel 72: 72 geteilt durch den Zinssatz ergibt ungefähr die Verdopplungszeit. Bei 5 % also etwa 14 Jahre.", "Rule of 72: 72 divided by the interest rate gives roughly the doubling time. At 5 %, about 14 years."),
            )
        },
        TaskDef("fernseher", Topic.Equations, M) {
            val d = 55 * 2.54
            val w = d * 16 / sqrt(16.0 * 16 + 9 * 9)
            Task(
                "fernseher", Topic.Equations, M,
                question = l("Ein Fernseher mit 55 Zoll Bilddiagonale hat das Format 16 : 9. Passt er in ein Regalfach, das 125 cm breit ist?", "A TV with a 55-inch diagonal has a 16 : 9 format. Does it fit into a shelf that is 125 cm wide?"),
                given = listOf(l("1 Zoll = 2,54 cm", "1 inch = 2.54 cm")),
                steps = listOf(
                    num(
                        l("Wie lang ist die Diagonale in cm?", "How long is the diagonal in cm?"), d, "cm", rel = 0.01,
                        done = l("""§139{,}7\ \text{cm}§""", """§139.7\ \text{cm}§"""),
                        hints = listOf(l("Mal 2,54.", "Times 2.54."), l("""§55 \cdot 2{,}54§""", """§55 \cdot 2.54§""")),
                    ),
                    num(
                        l("Wie breit ist das Bild?", "How wide is the picture?"), w, "cm", rel = 0.02,
                        done = l("""§\approx 122\ \text{cm}§, passt also knapp""", """§\approx 122\ \text{cm}§, so it just fits"""),
                        hints = listOf(l("""Kernidee: Breite, Höhe und Diagonale bilden ein rechtwinkliges Dreieck. Mit §b = 16k§ und §h = 9k§ gilt §d = k\sqrt{16^2 + 9^2}§.""", """Key idea: width, height and diagonal form a right triangle. With §b = 16k§ and §h = 9k§ we get §d = k\sqrt{16^2 + 9^2}§."""), l("""§b = 139{,}7 \cdot \dfrac{16}{\sqrt{337}}§""", """§b = 139.7 \cdot \dfrac{16}{\sqrt{337}}§""")),
                        formula = card("Pythagoras", """§a^2 + b^2 = c^2§""", l("Im rechtwinkligen Dreieck: die beiden kurzen Seiten zum Quadrat ergeben zusammen die lange zum Quadrat.", "In a right triangle, the squares of the two short sides add up to the square of the long one.")),
                    ),
                ),
                solution = listOf(
                    l("""§d = 55 \cdot 2{,}54\ \text{cm} = 139{,}7\ \text{cm}§""", """§d = 55 \cdot 2.54\ \text{cm} = 139.7\ \text{cm}§"""),
                    l("""§b = d \cdot \dfrac{16}{\sqrt{16^2 + 9^2}} = 139{,}7 \cdot \dfrac{16}{18{,}36} \approx 121{,}8\ \text{cm} < 125\ \text{cm}§""", """§b = d \cdot \dfrac{16}{\sqrt{16^2 + 9^2}} = 139.7 \cdot \dfrac{16}{18.36} \approx 121.8\ \text{cm} < 125\ \text{cm}§"""),
                ),
                background = l(
                    "Bildschirme werden nach ihrer Diagonale verkauft, weil diese Zahl am größten klingt. Wie breit ein Gerät wirklich ist, hängt vom Seitenverhältnis ab: Ein 16:9-Bild ist breiter und flacher als ein altes 4:3-Bild mit derselben Diagonale. Das Rechnen dahinter ist reiner Pythagoras.",
                    "Screens are sold by their diagonal because that number sounds biggest. How wide a device really is depends on the aspect ratio: a 16:9 picture is wider and flatter than an old 4:3 one with the same diagonal. The maths behind it is pure Pythagoras.",
                ),
                fact = l("Das Gehäuse kommt noch dazu: Mit Rahmen ist so ein Fernseher oft 1 bis 2 cm breiter als das Bild.", "The housing comes on top: with its frame such a TV is often 1 to 2 cm wider than the picture."),
            )
        },
        TaskDef("monty-hall", Topic.Percent, M) {
            Task(
                "monty-hall", Topic.Percent, M,
                question = l("In einer Show stehen drei Türen. Hinter einer ist ein Auto, hinter den anderen je eine Ziege. Du wählst Tür 1. Der Moderator, der weiß, wo das Auto ist, öffnet Tür 3: eine Ziege. Er bietet dir an, zu Tür 2 zu wechseln.", "A game show has three doors. Behind one is a car, behind the others a goat each. You pick door 1. The host, who knows where the car is, opens door 3: a goat. He offers to let you switch to door 2."),
                steps = listOf(
                    choice(
                        l("Was solltest du tun?", "What should you do?"),
                        listOf(l("Wechseln", "Switch"), l("Bei Tür 1 bleiben", "Stay with door 1"), l("Egal, es ist 50 : 50", "Doesn't matter, it's 50 : 50")),
                        done = l("Wechseln", "Switch"),
                        hints = listOf(l("Kernidee: Der Moderator öffnet nie die Tür mit dem Auto. Seine Wahl verrät etwas.", "Key idea: the host never opens the door with the car. His choice reveals something."), l("Wie wahrscheinlich war deine erste Wahl richtig?", "How likely was your first choice right?")),
                    ),
                    num(
                        l("Wie hoch ist die Gewinnchance beim Wechseln (in %)?", "What is the chance of winning if you switch (in %)?"), 200.0 / 3, "%", abs = 1.0,
                        done = l("""§\tfrac{2}{3} \approx 67\ \%§""", """§\tfrac{2}{3} \approx 67\ \%§"""),
                        hints = listOf(l("Deine erste Tür hat 1/3. Der Rest steckt jetzt komplett in der anderen geschlossenen Tür.", "Your first door has 1/3. All of the rest is now in the other closed door."), """§1 - \tfrac{1}{3}§"""),
                    ),
                ),
                solution = listOf(l("""Erste Wahl richtig: §\tfrac{1}{3}§. Falsch: §\tfrac{2}{3}§. Dann muss der Moderator die andere Ziege zeigen, und Wechseln gewinnt immer. Also gewinnt Wechseln mit §\tfrac{2}{3}§.""", """First pick right: §\tfrac{1}{3}§. Wrong: §\tfrac{2}{3}§. Then the host must show the other goat, and switching always wins. So switching wins with §\tfrac{2}{3}§.""")),
                background = l(
                    "Das Ziegenproblem ist berühmt, weil fast alle zuerst 50 : 50 sagen, auch viele Mathematiker. Der Denkfehler: Man tut so, als hätte der Moderator zufällig eine Tür geöffnet. Er hat aber Wissen benutzt. Wer sich unsicher ist, kann es mit 100 Türen durchdenken: Du wählst eine, der Moderator öffnet 98 Ziegentüren. Würdest du dann wechseln?",
                    "The Monty Hall problem is famous because almost everyone says 50 : 50 at first, including many mathematicians. The mistake: you treat the host as if he opened a door at random. But he used knowledge. If you're unsure, think it through with 100 doors: you pick one, the host opens 98 goat doors. Would you switch then?",
                ),
                fact = l("Als Marilyn vos Savant 1990 in einer Zeitschrift „Wechseln!“ schrieb, bekam sie Tausende empörte Briefe, viele von Leuten mit Doktortitel. Sie hatte recht.", "When Marilyn vos Savant wrote \"Switch!\" in a magazine in 1990, she got thousands of outraged letters, many from people with PhDs. She was right."),
            )
        },
        TaskDef("fibonacci", Topic.Sequences, M) {
            val phi = (1 + sqrt(5.0)) / 2
            Task(
                "fibonacci", Topic.Sequences, M,
                question = l("""Bei der Fibonacci-Folge ist jede Zahl die Summe der beiden davor: §1, 1, 2, 3, 5, 8, 13, 21, 34, \dots§""", """In the Fibonacci sequence every number is the sum of the two before it: §1, 1, 2, 3, 5, 8, 13, 21, 34, \dots§"""),
                steps = listOf(
                    num(
                        l("Welche Zahl kommt nach 34?", "Which number comes after 34?"), 55.0, abs = 0.0,
                        done = """§21 + 34 = 55§""",
                        hints = listOf(l("Addiere die letzten beiden.", "Add the last two."), """§21 + 34§"""),
                    ),
                    num(
                        l("Teile 55 durch 34. Welcher Wert kommt heraus (drei Nachkommastellen)?", "Divide 55 by 34. What value do you get (three decimals)?"), 55.0 / 34, abs = 0.005,
                        done = l("""§\approx 1{,}618§""", """§\approx 1.618§"""),
                        hints = listOf(l("Einfach teilen.", "Just divide."), """§55 : 34§"""),
                    ),
                    choice(
                        l("Dieses Verhältnis heißt Goldener Schnitt. Wo findet man ihn in der Natur?", "This ratio is called the golden ratio. Where is it found in nature?"),
                        listOf(l("In den Spiralen von Sonnenblumen und Tannenzapfen", "In the spirals of sunflowers and pine cones"), l("In der Form von Schneeflocken", "In the shape of snowflakes"), l("In den Umlaufbahnen der Planeten", "In the orbits of the planets")),
                        done = l("Sonnenblumen und Tannenzapfen", "Sunflowers and pine cones"),
                        hints = listOf(l("Zähl mal die Spiralen in einer Sonnenblume.", "Try counting the spirals in a sunflower."), l("Man findet dort 34 und 55 Spiralen.", "You'll find 34 and 55 spirals there.")),
                    ),
                ),
                solution = listOf(
                    """§21 + 34 = 55§""",
                    l("""§\dfrac{55}{34} \approx 1{,}6176 \;\to\; \varphi = \dfrac{1 + \sqrt{5}}{2} \approx 1{,}618§""", """§\dfrac{55}{34} \approx 1.6176 \;\to\; \varphi = \dfrac{1 + \sqrt{5}}{2} \approx 1.618§"""),
                ),
                background = l(
                    "Teilt man zwei aufeinanderfolgende Fibonacci-Zahlen, nähert sich das Ergebnis immer weiter dem Goldenen Schnitt φ ≈ 1,618. Pflanzen setzen neue Samen jeweils um den „goldenen Winkel“ von etwa 137,5° versetzt. So überdecken sich die Samen nie und passen am dichtesten zusammen. Dabei entstehen ganz von selbst Spiralen in Fibonacci-Anzahl.",
                    "If you divide two consecutive Fibonacci numbers, the result gets ever closer to the golden ratio φ ≈ 1.618. Plants place each new seed rotated by the \"golden angle\" of about 137.5°. That way seeds never overlap and pack as densely as possible. Spirals in Fibonacci numbers appear all by themselves.",
                ),
                fact = l("Fibonacci beschrieb die Folge 1202 mit einer Kaninchen-Aufgabe. In Indien war sie schon Jahrhunderte vorher aus der Verslehre bekannt.", "Fibonacci described the sequence in 1202 with a rabbit puzzle. In India it was known centuries earlier from the study of poetic metre."),
            )
        },
        TaskDef("bayes", Topic.Percent, H) {
            val sick = 100.0
            val truePos = 99.0
            val falsePos = 9900 * 0.05
            val p = truePos / (truePos + falsePos) * 100
            Task(
                "bayes", Topic.Percent, H,
                question = l("Eine Krankheit hat 1 % der Menschen. Ein Test erkennt 99 % der Kranken, schlägt aber auch bei 5 % der Gesunden fälschlich an. Dein Test ist positiv. Wie wahrscheinlich bist du krank?", "A disease affects 1 % of people. A test detects 99 % of the sick, but also wrongly comes out positive for 5 % of the healthy. Your test is positive. How likely are you to be sick?"),
                steps = listOf(
                    num(
                        l("Stell dir 10.000 Menschen vor. Wie viele sind krank?", "Imagine 10,000 people. How many are sick?"), sick, "", abs = 0.0,
                        done = "100",
                        hints = listOf(l("1 % von 10.000.", "1 % of 10,000."), """§10\,000 \cdot 0{,}01§""".let { l(it, it.replace("0{,}01", "0.01")) }),
                    ),
                    num(
                        l("Wie viele davon testet der Test positiv?", "How many of them does the test flag as positive?"), truePos, "", abs = 0.0,
                        done = "99",
                        hints = listOf(l("99 % der Kranken.", "99 % of the sick."), """§100 \cdot 0{,}99§""".let { l(it, it.replace("0{,}99", "0.99")) }),
                    ),
                    num(
                        l("Wie viele Gesunde testen fälschlich positiv?", "How many healthy people falsely test positive?"), falsePos, "", abs = 0.0,
                        done = "495",
                        hints = listOf(l("Gesund sind 9900.", "9900 are healthy."), """§9900 \cdot 0{,}05§""".let { l(it, it.replace("0{,}05", "0.05")) }),
                    ),
                    num(
                        l("Wie wahrscheinlich bist du bei positivem Test wirklich krank (in %)?", "How likely are you really sick with a positive test (in %)?"), p, "%", abs = 1.0,
                        done = l("nur etwa 17 %", "only about 17 %"),
                        hints = listOf(l("Kernidee: Von allen Positiven, wie viele sind wirklich krank?", "Key idea: of all positives, how many are really sick?"), """§\dfrac{99}{99 + 495}§"""),
                        formula = card(l("Satz von Bayes", "Bayes' theorem"), """§P(K \mid +) = \dfrac{P(+ \mid K)\, P(K)}{P(+)}§""", l("Wie wahrscheinlich die Ursache ist, wenn man die Wirkung sieht.", "How likely the cause is, given that you see the effect.")),
                    ),
                ),
                solution = listOf(
                    l("Von 10.000: 100 krank, davon 99 positiv. 9900 gesund, davon 495 fälschlich positiv.", "Of 10,000: 100 sick, 99 of them positive. 9900 healthy, 495 of them falsely positive."),
                    l("""§P(\text{krank} \mid +) = \dfrac{99}{99 + 495} \approx 16{,}7\ \%§""", """§P(\text{sick} \mid +) = \dfrac{99}{99 + 495} \approx 16.7\ \%§"""),
                ),
                background = l(
                    "Das Ergebnis überrascht fast alle, auch viele Ärzte. Der Grund ist die Grundrate: Die Krankheit ist so selten, dass die wenigen Fehlalarme unter den vielen Gesunden zahlenmäßig die echten Fälle übertreffen. Der Satz von Bayes beschreibt, wie man eine Wahrscheinlichkeit mit neuen Hinweisen richtig anpasst. Deshalb wird bei seltenen Krankheiten ein positiver Test immer mit einem zweiten, anderen Test überprüft.",
                    "The result surprises almost everyone, including many doctors. The reason is the base rate: the disease is so rare that the few false alarms among the many healthy people outnumber the real cases. Bayes' theorem describes how to update a probability correctly with new evidence. That's why with rare diseases a positive test is always checked with a second, different test.",
                ),
                fact = l("Einen zweiten, unabhängigen positiven Test vorausgesetzt, steigt die Wahrscheinlichkeit hier auf etwa 80 %.", "Given a second, independent positive test, the probability here rises to about 80 %."),
            )
        },
        TaskDef("geburtstag", Topic.Percent, H) {
            fun none(n: Int): Double { var q = 1.0; for (k in 0 until n) q *= (365.0 - k) / 365.0; return q }
            val q23 = none(23) * 100
            var n99 = 1
            while (1 - none(n99) < 0.99) n99++
            Task(
                "geburtstag", Topic.Percent, H,
                question = l("In deiner Klasse sind 23 Leute. Wie wahrscheinlich haben mindestens zwei am selben Tag Geburtstag? (Schaltjahre und Zwillinge ignorieren wir.)", "There are 23 people in your class. How likely is it that at least two share a birthday? (We ignore leap years and twins.)"),
                steps = listOf(
                    choice(
                        l("Wie rechnet man das am geschicktesten?", "What's the smartest way to calculate it?"),
                        listOf(l("Über das Gegenteil: Wahrscheinlichkeit, dass alle verschieden sind, von 1 abziehen", "Via the opposite: subtract the probability that all differ from 1"), l("23 durch 365 teilen", "Divide 23 by 365"), l("Alle möglichen Paare einzeln abzählen", "Count all possible pairs one by one")),
                        done = l("Über das Gegenteil", "Via the opposite"),
                        hints = listOf(l("„Mindestens zwei“ hat sehr viele Fälle, „alle verschieden“ nur einen.", "\"At least two\" has very many cases, \"all different\" only one."), """§P = 1 - P(\text{alle verschieden})§""".let { l(it, it.replace("alle verschieden", "all different")) }),
                    ),
                    num(
                        l("Wie wahrscheinlich haben alle 23 verschiedene Geburtstage (in %)?", "How likely do all 23 have different birthdays (in %)?"), q23, "%", abs = 1.0,
                        done = l("""§\approx 49{,}3\ \%§""", """§\approx 49.3\ \%§"""),
                        hints = listOf(l("Der zweite hat 364 von 365 freien Tagen, der dritte 363 …", "The second has 364 of 365 free days, the third 363 …"), """§\dfrac{365}{365} \cdot \dfrac{364}{365} \cdot \dots \cdot \dfrac{343}{365}§"""),
                    ),
                    num(
                        l("Ab wie vielen Personen liegt die Wahrscheinlichkeit für einen gemeinsamen Geburtstag über 99 %?", "From how many people is the chance of a shared birthday above 99 %?"), n99.toDouble(), l("Personen", "people"), abs = 1.0,
                        done = l("ab $n99 Personen", "from $n99 people"),
                        hints = listOf(l("Probier mit dem Rechner verschiedene Gruppengrößen.", "Try different group sizes with the calculator."), l("Es sind deutlich weniger als 100.", "It's far fewer than 100.")),
                    ),
                ),
                solution = listOf(
                    l("""§P(\text{alle verschieden}) = \displaystyle\prod_{k=0}^{22} \dfrac{365 - k}{365} \approx 0{,}493§""", """§P(\text{all different}) = \displaystyle\prod_{k=0}^{22} \dfrac{365 - k}{365} \approx 0.493§"""),
                    l("""§P(\text{mindestens zwei}) = 1 - 0{,}493 \approx 50{,}7\ \%§""", """§P(\text{at least two}) = 1 - 0.493 \approx 50.7\ \%§"""),
                ),
                background = l(
                    "Das Geburtstagsparadoxon ist kein echtes Paradox, es widerspricht nur unserem Gefühl. Wir denken an unseren eigenen Geburtstag. Gefragt ist aber nach irgendeinem Paar, und bei 23 Leuten gibt es schon 253 Paare. Dieselbe Rechnung braucht man in der Informatik: Sie zeigt, wie schnell zufällige Kennungen oder Prüfsummen doppelt vorkommen.",
                    "The birthday paradox isn't a real paradox, it just contradicts our intuition. We think of our own birthday. But the question is about any pair, and 23 people already form 253 pairs. The same calculation is needed in computer science: it shows how quickly random IDs or checksums collide.",
                ),
                fact = l("In einer Fußballpartie stehen mit Schiedsrichter 23 Leute auf dem Platz. In ungefähr jedem zweiten Spiel teilen sich zwei davon den Geburtstag.", "A football match has 23 people on the pitch including the referee. In about every second match, two of them share a birthday."),
            )
        },
        TaskDef("zaun", Topic.Calculus, H) {
            Task(
                "zaun", Topic.Calculus, H,
                question = l("Du hast 40 m Zaun und willst an einer langen Hauswand ein rechteckiges Beet einzäunen. Die Wand ersetzt eine Seite. Wie groß kann das Beet höchstens werden?", "You have 40 m of fence and want to enclose a rectangular bed against a long house wall. The wall replaces one side. How big can the bed be at most?"),
                steps = listOf(
                    choice(
                        l("""Die beiden Seiten senkrecht zur Wand sind §x§ lang. Wie lautet die Fläche?""", """The two sides perpendicular to the wall are §x§ long. What is the area?"""),
                        listOf("""§A(x) = x\,(40 - 2x)§""", """§A(x) = x\,(40 - x)§""", """§A(x) = x\,(20 - x)§"""),
                        done = """§A(x) = x\,(40 - 2x)§""",
                        hints = listOf(l("Der Zaun braucht zwei Seiten mit x und eine Seite parallel zur Wand.", "The fence needs two sides of x and one side parallel to the wall."), l("""Für die parallele Seite bleiben §40 - 2x§ übrig.""", """§40 - 2x§ is left for the parallel side.""")),
                    ),
                    num(
                        l("""Für welches §x§ wird die Fläche am größten?""", """For which §x§ is the area largest?"""), 10.0, "m", rel = 0.01,
                        done = """§x = 10\ \text{m}§""",
                        hints = listOf(l("Kernidee: Am Maximum ist die Ableitung null.", "Key idea: at the maximum the derivative is zero."), """§A'(x) = 40 - 4x = 0§"""),
                        formula = card(l("Extremstelle", "Extremum"), """§f'(x_0) = 0§""", l("An einem höchsten oder tiefsten Punkt ist die Kurve waagrecht.", "At a highest or lowest point the curve is horizontal.")),
                    ),
                    num(
                        l("Wie groß ist die Fläche dann?", "How big is the area then?"), 200.0, "m²", rel = 0.01,
                        done = """§A = 200\ \text{m}^2§""",
                        hints = listOf(l("Einsetzen.", "Insert."), """§10 \cdot (40 - 20)§"""),
                    ),
                ),
                solution = listOf(
                    """§A(x) = x\,(40 - 2x) = 40x - 2x^2§""",
                    """§A'(x) = 40 - 4x = 0 \;\Rightarrow\; x = 10\ \text{m}§""",
                    """§A(10) = 10 \cdot 20 = 200\ \text{m}^2§""",
                ),
                background = l(
                    "Optimieren heißt: Unter einer Bedingung (hier 40 m Zaun) das Beste herausholen. Man schreibt die Größe, die groß werden soll, als Funktion einer Variablen und sucht die Stelle, an der die Ableitung null ist. Mit derselben Methode findet man die günstigste Dosenform, die schnellste Route oder den besten Preis.",
                    "Optimising means getting the most out of something under a constraint (here 40 m of fence). You write the quantity you want to maximise as a function of one variable and look for the point where the derivative is zero. The same method finds the cheapest can shape, the fastest route or the best price.",
                ),
                fact = l("Ohne Hauswand wäre ein Quadrat am besten. Mit Wand ist das beste Beet doppelt so lang wie breit.", "Without the house wall a square would be best. With the wall, the best bed is twice as long as it is wide."),
            )
        },
        TaskDef("euler", Topic.Calculus, X) {
            val ii = exp(-PI / 2)
            Task(
                "euler", Topic.Calculus, X,
                question = l("""Die Eulersche Formel §e^{i\varphi} = \cos\varphi + i \sin\varphi§ verbindet Exponentialfunktion und Kreis. Dabei ist §i^2 = -1§.""", """Euler's formula §e^{i\varphi} = \cos\varphi + i \sin\varphi§ links the exponential function and the circle. Here §i^2 = -1§."""),
                steps = listOf(
                    num(
                        """§e^{i\pi} = \square§""", -1.0, abs = 0.0,
                        done = """§e^{i\pi} = -1§""",
                        hints = listOf("""§\cos\pi = -1,\; \sin\pi = 0§""", l("Einsetzen in die Formel.", "Insert into the formula.")),
                    ),
                    num(
                        l("""Wie groß ist der Betrag §|e^{i\varphi}|§ für jedes §\varphi§?""", """What is the magnitude §|e^{i\varphi}|§ for every §\varphi§?"""), 1.0, abs = 0.0,
                        done = """§|e^{i\varphi}| = 1§""",
                        hints = listOf("""§|a + ib| = \sqrt{a^2 + b^2}§""", """§\cos^2\varphi + \sin^2\varphi = 1§"""),
                        formula = card(l("Komplexe Zahlen als Punkte", "Complex numbers as points"), """§z = a + ib \;\leftrightarrow\; (a, b)§""", l("Der Betrag ist der Abstand vom Nullpunkt.", "The magnitude is the distance from the origin.")),
                    ),
                    num(
                        l("""Und jetzt das Verrückte: Welchen Wert hat §i^i§? (drei Nachkommastellen)""", """And now the crazy part: what is the value of §i^i§? (three decimals)"""), ii, abs = 0.005,
                        done = l("""§i^i = e^{-\pi/2} \approx 0{,}208§, eine reelle Zahl""", """§i^i = e^{-\pi/2} \approx 0.208§, a real number"""),
                        hints = listOf(l("""Schreib §i§ als Exponential: §i = e^{i\pi/2}§.""", """Write §i§ as an exponential: §i = e^{i\pi/2}§."""), """§i^i = \left(e^{i\pi/2}\right)^i = e^{i \cdot i\pi/2}§"""),
                    ),
                ),
                solution = listOf(
                    """§e^{i\pi} = \cos\pi + i\sin\pi = -1§""",
                    """§|e^{i\varphi}| = \sqrt{\cos^2\varphi + \sin^2\varphi} = 1§""",
                    l("""§i^i = e^{i\pi/2 \cdot i} = e^{-\pi/2} \approx 0{,}208§""", """§i^i = e^{i\pi/2 \cdot i} = e^{-\pi/2} \approx 0.208§"""),
                ),
                background = l(
                    "Komplexe Zahlen kann man als Punkte in einer Ebene sehen: der reelle Teil nach rechts, der imaginäre nach oben. Mit i zu multiplizieren heißt dann, um 90° zu drehen. Die Eulersche Formel sagt: e hoch iφ ist einfach der Punkt auf dem Einheitskreis beim Winkel φ. Deshalb beschreiben Physiker Schwingungen, Wellen und Wechselstrom fast immer mit e hoch iωt, und in der Quantenmechanik ist jede Wellenfunktion so gebaut.",
                    "Complex numbers can be seen as points in a plane: the real part to the right, the imaginary part upwards. Multiplying by i then means rotating by 90°. Euler's formula says: e to the iφ is simply the point on the unit circle at angle φ. That's why physicists almost always describe oscillations, waves and alternating current with e to the iωt, and every wave function in quantum mechanics is built like that.",
                ),
                fact = l("""Umgestellt zu §e^{i\pi} + 1 = 0§ enthält die Gleichung die fünf wichtigsten Zahlen der Mathematik. Sie wird oft die schönste Formel genannt.""", """Rearranged as §e^{i\pi} + 1 = 0§ the equation contains the five most important numbers in mathematics. It is often called the most beautiful formula."""),
            )
        },
        TaskDef("gauss", Topic.Calculus, X) {
            Task(
                "gauss", Topic.Calculus, X,
                question = l("""Das Integral §I = \displaystyle\int_{-\infty}^{\infty} e^{-x^2}\, dx§ steckt hinter der Glockenkurve. Eine Stammfunktion gibt es nicht in geschlossener Form. Trotzdem lässt sich der Wert exakt berechnen.""", """The integral §I = \displaystyle\int_{-\infty}^{\infty} e^{-x^2}\, dx§ is behind the bell curve. It has no antiderivative in closed form. Even so, its value can be calculated exactly."""),
                steps = listOf(
                    choice(
                        l("Welcher Trick funktioniert?", "Which trick works?"),
                        listOf(l("""§I^2§ als Doppelintegral über die Ebene schreiben und in Polarkoordinaten rechnen""", """Write §I^2§ as a double integral over the plane and switch to polar coordinates"""), l("Partielle Integration", "Integration by parts"), l("Substitution mit u = x²", "Substitution with u = x²")),
                        done = l("Quadrieren und Polarkoordinaten", "Square it and use polar coordinates"),
                        hints = listOf("""§I^2 = \displaystyle\int\!\!\int e^{-(x^2 + y^2)}\, dx\, dy§""", l("""In Polarkoordinaten ist §dx\,dy = r\,dr\,d\varphi§, und das §r§ rettet alles.""", """In polar coordinates §dx\,dy = r\,dr\,d\varphi§, and the §r§ saves the day.""")),
                    ),
                    num(
                        l("""Welchen Wert hat §I§? (drei Nachkommastellen)""", """What is the value of §I§? (three decimals)"""), sqrt(PI), abs = 0.005,
                        done = l("""§I = \sqrt{\pi} \approx 1{,}772§""", """§I = \sqrt{\pi} \approx 1.772§"""),
                        hints = listOf("""§I^2 = \displaystyle\int_0^{2\pi}\!\! \int_0^\infty e^{-r^2}\, r\, dr\, d\varphi§""", l("""Das innere Integral ist §\tfrac{1}{2}§, also §I^2 = \pi§.""", """The inner integral is §\tfrac{1}{2}§, so §I^2 = \pi§.""")),
                    ),
                    num(
                        l("Bei der Normalverteilung: Wie viel Prozent der Werte liegen höchstens zwei Standardabweichungen vom Mittelwert entfernt?", "For the normal distribution: what percentage of values lie within two standard deviations of the mean?"), 95.45, "%", abs = 0.6,
                        done = l("""§\approx 95{,}4\ \%§""", """§\approx 95.4\ \%§"""),
                        hints = listOf(l("Die bekannte 68-95-99,7-Regel.", "The well-known 68-95-99.7 rule."), l("Innerhalb einer Standardabweichung sind es gut 68 %.", "Within one standard deviation it's just over 68 %.")),
                    ),
                ),
                solution = listOf(
                    """§I^2 = \displaystyle\int_0^{2\pi}\!\! \int_0^\infty e^{-r^2}\, r\, dr\, d\varphi = 2\pi \cdot \tfrac{1}{2} = \pi \;\Rightarrow\; I = \sqrt{\pi}§""",
                ),
                background = l(
                    "Die Glockenkurve beschreibt, wie sich Messwerte um einen Mittelwert verteilen, wenn viele kleine Zufälle zusammenkommen: Körpergrößen, Messfehler, Noten großer Gruppen. Dass das so oft passiert, sagt der zentrale Grenzwertsatz. Dass in der Formel ausgerechnet √π auftaucht, verdankt sie dem Trick mit dem Quadrieren: Aus einer eindimensionalen Kurve wird ein rotationssymmetrischer Hügel, und Kreise bringen π mit.",
                    "The bell curve describes how measurements spread around a mean when many small random effects add up: body heights, measurement errors, grades in large groups. That this happens so often is the central limit theorem. That √π of all things shows up is thanks to the squaring trick: a one-dimensional curve becomes a rotationally symmetric hill, and circles bring π along.",
                ),
                fact = l("Bis 2001 war Carl Friedrich Gauß mitsamt seiner Glockenkurve auf dem deutschen 10-Mark-Schein abgebildet.", "Until 2001 Carl Friedrich Gauss and his bell curve were printed on the German 10 Mark note."),
            )
        },

        // ========================================== PHYSIK ==========================================
        TaskDef("spiegel", Topic.Optics, M) {
            Task(
                "spiegel", Topic.Optics, M,
                question = l("Du bist 1,80 m groß und willst dich in einem Wandspiegel von Kopf bis Fuß sehen. Wie hoch muss der Spiegel mindestens sein?", "You are 1.80 m tall and want to see yourself from head to toe in a wall mirror. How tall does the mirror have to be at least?"),
                steps = listOf(
                    num(
                        l("Mindesthöhe des Spiegels", "Minimum height of the mirror"), 0.90, "m", rel = 0.03,
                        done = l("""§0{,}90\ \text{m}§, die halbe Körpergröße""", """§0.90\ \text{m}§, half your height"""),
                        hints = listOf(l("Kernidee: Einfallswinkel gleich Ausfallswinkel. Der Spiegel muss nur den Punkt auf halber Strecke zwischen deinen Augen und deinen Füßen zeigen.", "Key idea: angle of incidence equals angle of reflection. The mirror only needs to cover the point halfway between your eyes and your feet."), l("Das Gleiche gilt nach oben. Zusammen: die Hälfte deiner Größe.", "The same holds upwards. Together: half your height.")),
                        formula = card(l("Reflexionsgesetz", "Law of reflection"), """§\alpha = \alpha'§""", l("Ein Lichtstrahl verlässt den Spiegel im selben Winkel, in dem er ankam.", "A light ray leaves the mirror at the same angle at which it arrived.")),
                    ),
                ),
                solution = listOf(l("""Licht von deinen Füßen trifft den Spiegel auf halber Höhe zwischen Augen und Füßen, Licht vom Scheitel auf halber Höhe zwischen Augen und Scheitel. Dazwischen liegt genau §\tfrac{1}{2} \cdot 1{,}80\ \text{m} = 0{,}90\ \text{m}§.""", """Light from your feet hits the mirror halfway between eyes and feet, light from the top of your head halfway between eyes and head. Exactly §\tfrac{1}{2} \cdot 1.80\ \text{m} = 0.90\ \text{m}§ lies between them.""")),
                background = l(
                    "Ein flacher Spiegel erzeugt ein Bild, das genauso weit hinter dem Spiegel zu liegen scheint, wie du davor stehst. Weil Einfallswinkel und Ausfallswinkel gleich sind, braucht man immer genau die halbe Körpergröße, und zwar unabhängig vom Abstand. Geht man weiter weg, wird das Spiegelbild zwar kleiner, der Ausschnitt im Spiegel aber auch.",
                    "A flat mirror creates an image that seems to lie as far behind the mirror as you stand in front of it. Because the angles of incidence and reflection are equal, you always need exactly half your height, regardless of distance. If you step back, your reflection gets smaller, but so does the part of the mirror you use.",
                ),
                fact = l("Die meisten tippen, dass man weiter weg weniger Spiegel braucht. Probier es aus: Mit einem Stück Klebeband auf dem Spiegel siehst du, dass sich nichts ändert.", "Most people guess you need less mirror further away. Try it: with a strip of tape on the mirror you'll see that nothing changes."),
            )
        },
        TaskDef("akku", Topic.Electricity, M) {
            val wh = 5.0 * 3.85
            val kwh = wh * 365 / 1000
            Task(
                "akku", Topic.Electricity, M,
                question = l("Dein Handy-Akku hat 5000 mAh bei 3,85 V. Du lädst ihn jeden Tag einmal komplett. Was kostet dich das im Jahr?", "Your phone battery has 5000 mAh at 3.85 V. You charge it fully once every day. What does that cost you per year?"),
                given = listOf(l("Strompreis: 30 Cent pro kWh", "Electricity price: 30 cents per kWh")),
                steps = listOf(
                    num(
                        l("Wie viele Wattstunden speichert der Akku?", "How many watt-hours does the battery store?"), wh, "Wh", rel = 0.02,
                        done = l("""§19{,}25\ \text{Wh}§""", """§19.25\ \text{Wh}§"""),
                        hints = listOf(l("Kernidee: Energie = Ladung mal Spannung. 5000 mAh sind 5 Ah.", "Key idea: energy = charge times voltage. 5000 mAh is 5 Ah."), l("""§5\ \text{Ah} \cdot 3{,}85\ \text{V}§""", """§5\ \text{Ah} \cdot 3.85\ \text{V}§""")),
                        formula = card(l("Elektrische Energie", "Electrical energy"), """§E = Q \cdot U§""", l("Ladung in Amperestunden mal Spannung ergibt Wattstunden.", "Charge in amp-hours times voltage gives watt-hours.")),
                    ),
                    num(
                        l("Wie viele kWh sind das im Jahr?", "How many kWh is that per year?"), kwh, "kWh", rel = 0.03,
                        done = l("""§\approx 7\ \text{kWh}§""", """§\approx 7\ \text{kWh}§"""),
                        hints = listOf(l("Mal 365, dann durch 1000.", "Times 365, then divide by 1000."), l("""§19{,}25 \cdot 365 : 1000§""", """§19.25 \cdot 365 : 1000§""")),
                    ),
                    num(
                        l("Wie viel kostet das?", "How much does that cost?"), kwh * 0.30, "€", rel = 0.05,
                        done = l("etwa 2 € im Jahr", "about €2 per year"),
                        hints = listOf(l("Mal 0,30 €.", "Times €0.30."), l("""§7 \cdot 0{,}30\ €§""", """§7 \cdot €0.30§""")),
                    ),
                ),
                solution = listOf(
                    l("""§E = 5\ \text{Ah} \cdot 3{,}85\ \text{V} = 19{,}25\ \text{Wh}§""", """§E = 5\ \text{Ah} \cdot 3.85\ \text{V} = 19.25\ \text{Wh}§"""),
                    l("""§19{,}25\ \text{Wh} \cdot 365 \approx 7{,}0\ \text{kWh} \;\Rightarrow\; 7{,}0 \cdot 0{,}30\ € \approx 2{,}10\ €§""", """§19.25\ \text{Wh} \cdot 365 \approx 7.0\ \text{kWh} \;\Rightarrow\; 7.0 \cdot €0.30 \approx €2.10§"""),
                ),
                background = l(
                    "Die Angabe in mAh sagt nur, wie viel Ladung im Akku steckt. Wie viel Energie das ist, hängt zusätzlich von der Spannung ab. Deshalb rechnet man für Vergleiche besser in Wattstunden. Beim Laden geht noch etwas Wärme verloren, real sind es eher 10 bis 20 % mehr. Trotzdem: Das Handy ist eines der sparsamsten Geräte im Haushalt.",
                    "The mAh figure only says how much charge is in the battery. How much energy that is also depends on the voltage. That's why it's better to compare in watt-hours. Some heat is lost while charging, so in reality it's 10 to 20 % more. Still, the phone is one of the most economical devices in a household.",
                ),
                fact = l("Ein Wasserkocher braucht für dieselbe Energie nur etwa 35 Sekunden.", "A kettle uses the same energy in only about 35 seconds."),
            )
        },
        TaskDef("blitzlicht", Topic.Electricity, H) {
            val e = 0.5 * 100e-6 * 300.0 * 300.0
            Task(
                "blitzlicht", Topic.Electricity, H,
                question = l("""Ein Kamerablitz lädt einen Kondensator mit §C = 100\ \mu\text{F}§ auf §U = 300\ \text{V}§ und entlädt ihn in etwa einer Millisekunde durch die Blitzröhre.""", """A camera flash charges a capacitor with §C = 100\ \mu\text{F}§ to §U = 300\ \text{V}§ and discharges it through the flash tube in about one millisecond."""),
                steps = listOf(
                    num(
                        l("Wie viel Energie steckt im Kondensator?", "How much energy is stored in the capacitor?"), e, "J", rel = 0.02,
                        done = l("""§4{,}5\ \text{J}§""", """§4.5\ \text{J}§"""),
                        hints = listOf(l("Achtung: Mikro heißt mal 10⁻⁶.", "Careful: micro means times 10⁻⁶."), l("""§E = \tfrac{1}{2} \cdot 10^{-4}\ \text{F} \cdot (300\ \text{V})^2§""", """§E = \tfrac{1}{2} \cdot 10^{-4}\ \text{F} \cdot (300\ \text{V})^2§""")),
                        formula = card(l("Energie im Kondensator", "Energy in a capacitor"), """§E = \tfrac{1}{2}\, C\, U^2§""", l("Doppelte Spannung, vierfache Energie.", "Double the voltage, four times the energy.")),
                    ),
                    num(
                        l("Welche mittlere Leistung hat der Blitz?", "What is the flash's average power?"), e / 1e-3, "W", rel = 0.03,
                        done = l("""§4500\ \text{W}§, mehr als ein Wasserkocher""", """§4500\ \text{W}§, more than a kettle"""),
                        hints = listOf(l("Leistung ist Energie pro Zeit.", "Power is energy per time."), """§P = 4{,}5\ \text{J} : 0{,}001\ \text{s}§""".let { l(it, it.replace("4{,}5", "4.5").replace("0{,}001", "0.001")) }),
                    ),
                ),
                solution = listOf(
                    l("""§E = \tfrac{1}{2} \cdot 100 \cdot 10^{-6}\ \text{F} \cdot (300\ \text{V})^2 = 4{,}5\ \text{J}§""", """§E = \tfrac{1}{2} \cdot 100 \cdot 10^{-6}\ \text{F} \cdot (300\ \text{V})^2 = 4.5\ \text{J}§"""),
                    l("""§P = \dfrac{4{,}5\ \text{J}}{10^{-3}\ \text{s}} = 4500\ \text{W}§""", """§P = \dfrac{4.5\ \text{J}}{10^{-3}\ \text{s}} = 4500\ \text{W}§"""),
                ),
                background = l(
                    "Ein Kondensator ist ein Energiespeicher, der sich schnell leeren lässt. Eine Batterie liefert ihre Energie langsam, weil darin chemische Reaktionen ablaufen. Der Kondensator dagegen speichert sie direkt im elektrischen Feld zwischen zwei Platten. Deshalb lädt der Blitz einige Sekunden lang aus der kleinen Batterie und gibt alles in einer Millisekunde wieder ab.",
                    "A capacitor is an energy store that can be emptied quickly. A battery delivers its energy slowly because chemical reactions take place inside. A capacitor instead stores it directly in the electric field between two plates. That's why a flash charges for a few seconds from the small battery and releases everything in a millisecond.",
                ),
                fact = l("Auch abgeschaltete Blitzgeräte können noch geladen sein. Deshalb steht in der Anleitung: nicht öffnen.", "Even switched-off flash units can still be charged. That's why the manual says: do not open."),
            )
        },
        TaskDef("doppler", Topic.Waves, H) {
            val c = 343.0
            val up = 700 * c / (c - 25)
            val down = 700 * c / (c + 25)
            Task(
                "doppler", Topic.Waves, H,
                question = l("Ein Krankenwagen fährt mit 25 m/s (90 km/h) an dir vorbei. Seine Sirene hat 700 Hz.", "An ambulance drives past you at 25 m/s (90 km/h). Its siren has 700 Hz."),
                given = listOf(l("Schallgeschwindigkeit: 343 m/s", "Speed of sound: 343 m/s")),
                steps = listOf(
                    num(
                        l("Welche Frequenz hörst du, während er auf dich zukommt?", "Which frequency do you hear while it approaches?"), up, "Hz", rel = 0.01,
                        done = l("""§\approx 755\ \text{Hz}§, höher""", """§\approx 755\ \text{Hz}§, higher"""),
                        hints = listOf(l("Kernidee: Die Quelle läuft ihren eigenen Wellen hinterher, die Wellenberge rücken zusammen.", "Key idea: the source chases its own waves, so the crests bunch up."), """§f' = f \cdot \dfrac{c}{c - v}§"""),
                        formula = card(l("Doppler-Effekt (bewegte Quelle)", "Doppler effect (moving source)"), """§f' = f \cdot \dfrac{c}{c \mp v}§""", l("Minus beim Näherkommen, plus beim Entfernen.", "Minus when approaching, plus when moving away.")),
                    ),
                    num(
                        l("Und wenn er sich entfernt?", "And when it moves away?"), down, "Hz", rel = 0.01,
                        done = l("""§\approx 652\ \text{Hz}§, tiefer""", """§\approx 652\ \text{Hz}§, lower"""),
                        hints = listOf(l("Jetzt werden die Abstände größer.", "Now the gaps get bigger."), """§f' = f \cdot \dfrac{c}{c + v}§"""),
                    ),
                ),
                solution = listOf(
                    """§f_\text{hin} = 700 \cdot \dfrac{343}{343 - 25} \approx 755\ \text{Hz}§""".let { l(it, it.replace("hin", "in")) },
                    """§f_\text{weg} = 700 \cdot \dfrac{343}{343 + 25} \approx 652\ \text{Hz}§""".let { l(it, it.replace("weg", "out")) },
                ),
                background = l(
                    "Der Doppler-Effekt tritt bei allen Wellen auf. Bewegt sich die Quelle auf dich zu, kommen die Wellenberge dichter hintereinander an, der Ton klingt höher. Entfernt sie sich, klingt er tiefer. Bei Licht verschiebt sich die Farbe: Astronomen erkennen so, dass sich ferne Galaxien von uns entfernen (Rotverschiebung). Radarfallen messen mit demselben Effekt deine Geschwindigkeit.",
                    "The Doppler effect happens with all waves. If the source moves towards you, the crests arrive closer together and the tone sounds higher. If it moves away, it sounds lower. With light the colour shifts: that's how astronomers see that distant galaxies are moving away from us (redshift). Speed cameras measure your speed with the same effect.",
                ),
                fact = l("Christian Doppler sagte den Effekt 1842 voraus. Getestet wurde er 1845 mit Trompetern auf einem fahrenden Zug.", "Christian Doppler predicted the effect in 1842. It was tested in 1845 with trumpeters on a moving train."),
            )
        },

        // ======================================== GESCHICHTE ========================================
        TaskDef("eratosthenes", Topic.History, M) {
            Task(
                "eratosthenes", Topic.History, M,
                question = l("Um 240 v. Chr. bestimmte Eratosthenes den Erdumfang ohne Satellit: Am Mittag stand die Sonne in Syene genau senkrecht, im 800 km nördlicheren Alexandria warfen Stäbe einen Schatten von 7,2°.", "Around 240 BC Eratosthenes measured the Earth's circumference without a satellite: at noon the Sun stood exactly overhead in Syene, while in Alexandria, 800 km further north, sticks cast a shadow at 7.2°."),
                steps = listOf(
                    num(
                        l("Wie oft passen 7,2° in einen Vollkreis?", "How many times does 7.2° fit into a full circle?"), 360 / 7.2, "", abs = 0.0,
                        done = l("ein Fünfzigstel", "one fiftieth"),
                        hints = listOf(l("Ein Vollkreis hat 360°.", "A full circle has 360°."), l("""§360 : 7{,}2§""", """§360 : 7.2§""")),
                    ),
                    num(
                        l("Wie groß ist demnach der Erdumfang?", "So how big is the Earth's circumference?"), 50.0 * 800, "km", rel = 0.01,
                        done = l("40.000 km", "40,000 km"),
                        hints = listOf(l("Die 800 km sind ein Fünfzigstel des Umfangs.", "The 800 km are one fiftieth of the circumference."), """§50 \cdot 800\ \text{km}§"""),
                    ),
                ),
                solution = listOf(l("""§\dfrac{360°}{7{,}2°} = 50 \;\Rightarrow\; U = 50 \cdot 800\ \text{km} = 40\,000\ \text{km}§""", """§\dfrac{360°}{7.2°} = 50 \;\Rightarrow\; U = 50 \cdot 800\ \text{km} = 40\,000\ \text{km}§""")),
                background = l(
                    "Gebildete Griechen wussten längst, dass die Erde eine Kugel ist: Bei Mondfinsternissen ist ihr Schatten immer rund, und Schiffe verschwinden am Horizont zuerst mit dem Rumpf. Eratosthenes nutzte, dass die Sonne so weit weg ist, dass ihre Strahlen parallel ankommen. Der Winkelunterschied zweier Orte verrät dann direkt, welcher Teil des Erdumfangs zwischen ihnen liegt. Dass Menschen im Mittelalter an eine flache Erde geglaubt hätten, ist übrigens ein Mythos des 19. Jahrhunderts.",
                    "Educated Greeks had long known that the Earth is a sphere: during lunar eclipses its shadow is always round, and ships disappear hull-first over the horizon. Eratosthenes used the fact that the Sun is so far away that its rays arrive parallel. The difference in angle between two places then directly tells you what part of the Earth's circumference lies between them. That people in the Middle Ages believed in a flat Earth, by the way, is a 19th-century myth.",
                ),
                fact = l("Die Entfernung bestimmten bezahlte Schrittzähler, die die Strecke abliefen. Wie genau er wirklich lag, hängt davon ab, wie lang sein „Stadion“ war, und das ist bis heute umstritten.", "The distance was measured by paid pacers who walked the route. How accurate he really was depends on how long his \"stadion\" was, which is still disputed."),
            )
        },
        TaskDef("roemerpost", Topic.History, M) {
            val days = 2000.0 / 75
            Task(
                "roemerpost", Topic.History, M,
                question = l("Die römische Staatspost schaffte mit Pferdewechseln etwa 75 km am Tag. Wie lange war ein Brief von Rom nach London (rund 2000 km) unterwegs?", "With changes of horses, the Roman state post managed about 75 km a day. How long did a letter take from Rome to London (about 2000 km)?"),
                steps = listOf(
                    num(
                        l("Tage unterwegs", "Days on the road"), days, l("Tage", "days"), rel = 0.04,
                        done = l("knapp 4 Wochen", "almost 4 weeks"),
                        hints = listOf(l("Strecke durch Tagesleistung.", "Distance divided by daily distance."), """§2000 : 75§"""),
                    ),
                ),
                solution = listOf(l("""§2000\ \text{km} : 75\ \tfrac{\text{km}}{\text{Tag}} \approx 27§ Tage""", """§2000\ \text{km} : 75\ \tfrac{\text{km}}{\text{day}} \approx 27§ days""")),
                background = l(
                    "Das Römische Reich hielt ein riesiges Gebiet von Schottland bis Syrien zusammen. Möglich machten das rund 80.000 km befestigte Straßen und ein Netz aus Raststationen, an denen Kuriere frische Pferde bekamen. Nachrichten waren trotzdem wochenlang unterwegs: Wenn in einer Provinz ein Aufstand ausbrach, wusste Rom oft erst einen Monat später davon.",
                    "The Roman Empire held together a huge area from Scotland to Syria. This was made possible by about 80,000 km of paved roads and a network of relay stations where couriers got fresh horses. News still took weeks: when a revolt broke out in a province, Rome often only found out a month later.",
                ),
                fact = l("In echten Notfällen schafften römische Reiter auch über 200 km am Tag. Eine E-Mail braucht heute für dieselbe Strecke weniger als eine Zehntelsekunde.", "In real emergencies Roman riders could manage over 200 km a day. An email covers the same distance today in less than a tenth of a second."),
            )
        },
        TaskDef("zeitstrahl-antike", Topic.History, E) {
            Task(
                "zeitstrahl-antike", Topic.History, E,
                question = l("Bring die drei Ereignisse in die richtige Reihenfolge.", "Put the three events in the right order."),
                steps = listOf(
                    choice(
                        l("Was kam zuerst?", "What came first?"),
                        listOf(l("Gründung Roms → Julius Caesar → Kaiserkrönung Karls des Großen", "Founding of Rome → Julius Caesar → coronation of Charlemagne"), l("Julius Caesar → Gründung Roms → Kaiserkrönung Karls des Großen", "Julius Caesar → founding of Rome → coronation of Charlemagne"), l("Kaiserkrönung Karls des Großen → Gründung Roms → Julius Caesar", "Coronation of Charlemagne → founding of Rome → Julius Caesar")),
                        done = l("753 v. Chr. → 44 v. Chr. → 800", "753 BC → 44 BC → 800"),
                        hints = listOf(l("Caesar war Herrscher über eine längst bestehende Stadt.", "Caesar ruled a city that had long existed."), l("Karl der Große wurde im Jahr 800 Kaiser.", "Charlemagne became emperor in the year 800.")),
                    ),
                ),
                solution = listOf(l("Rom wurde der Sage nach 753 v. Chr. gegründet, Caesar starb 44 v. Chr., Karl der Große wurde im Jahr 800 zum Kaiser gekrönt.", "According to legend Rome was founded in 753 BC, Caesar died in 44 BC, Charlemagne was crowned emperor in the year 800.")),
                background = l(
                    "Zwischen der Gründung Roms und Karl dem Großen liegen über 1500 Jahre. Rom war erst ein Königreich, dann eine Republik und schließlich ein Kaiserreich. Nach dem Ende des Weströmischen Reichs 476 wollte Karl der Große an diese Größe anknüpfen und ließ sich vom Papst in Rom zum Kaiser krönen.",
                    "Over 1500 years lie between the founding of Rome and Charlemagne. Rome was first a kingdom, then a republic and finally an empire. After the fall of the Western Roman Empire in 476, Charlemagne wanted to revive that greatness and had himself crowned emperor by the Pope in Rome.",
                ),
                fact = l("Die Jahreszählung „vor Christus“ gab es damals noch nicht. Sie wurde erst im 6. Jahrhundert erfunden und erst viel später rückwärts angewendet.", "The \"BC\" year count didn't exist back then. It was only invented in the 6th century and applied backwards much later."),
            )
        },

        // ========================================= GEOLOGIE =========================================
        TaskDef("tropfstein", Topic.Geology, M) {
            Task(
                "tropfstein", Topic.Geology, M,
                question = l("Ein Tropfstein wächst etwa 0,1 mm pro Jahr. Wie alt ist ein Tropfstein, der 2 m lang ist?", "A stalactite grows about 0.1 mm per year. How old is a stalactite that is 2 m long?"),
                steps = listOf(
                    num(
                        l("Alter in Jahren", "Age in years"), 2000.0 / 0.1, l("Jahre", "years"), rel = 0.02,
                        done = l("etwa 20.000 Jahre", "about 20,000 years"),
                        hints = listOf(l("Rechne die 2 m in Millimeter um.", "Convert the 2 m into millimetres."), l("""§2000\ \text{mm} : 0{,}1\ \tfrac{\text{mm}}{\text{Jahr}}§""", """§2000\ \text{mm} : 0.1\ \tfrac{\text{mm}}{\text{year}}§""")),
                    ),
                ),
                solution = listOf(l("""§2\ \text{m} = 2000\ \text{mm} \;\Rightarrow\; 2000 : 0{,}1 = 20\,000§ Jahre""", """§2\ \text{m} = 2000\ \text{mm} \;\Rightarrow\; 2000 : 0.1 = 20\,000§ years""")),
                background = l(
                    "Regenwasser nimmt im Boden Kohlendioxid auf und wird leicht sauer. Auf dem Weg durch Kalkstein löst es Kalk. Tropft es in einer Höhle von der Decke, entweicht ein Teil des Kohlendioxids, und der Kalk lagert sich wieder ab, Tropfen für Tropfen. Von oben wachsen Stalaktiten, von unten Stalagmiten. Wie schnell, hängt von Wassermenge und Temperatur ab.",
                    "Rainwater takes up carbon dioxide in the soil and becomes slightly acidic. On its way through limestone it dissolves lime. When it drips from a cave ceiling, some carbon dioxide escapes and the lime is deposited again, drop by drop. Stalactites grow from above, stalagmites from below. How fast depends on the amount of water and the temperature.",
                ),
                fact = l("Merkhilfe: Stalaktiten hängen von der Decke, Stalagmiten machen von unten mit.", "Memory aid: stalactites hold tight to the ceiling, stalagmites might reach them from below."),
            )
        },
        TaskDef("oetzi", Topic.Geology, H) {
            val t = 5730 * log2(1 / 0.53)
            Task(
                "oetzi", Topic.Geology, H,
                question = l("In den Knochen von Ötzi, der Gletschermumie aus den Alpen, ist nur noch 53 % so viel Kohlenstoff-14 wie in lebenden Menschen. Wie lange ist er tot?", "The bones of Ötzi, the glacier mummy from the Alps, contain only 53 % as much carbon-14 as living people. How long has he been dead?"),
                given = listOf(l("Halbwertszeit von C-14: 5730 Jahre", "Half-life of C-14: 5730 years")),
                steps = listOf(
                    choice(
                        l("Welche Formel beschreibt den Zerfall?", "Which formula describes the decay?"),
                        listOf("""§N(t) = N_0 \cdot \left(\tfrac{1}{2}\right)^{t / T}§""", """§N(t) = N_0 - \tfrac{t}{T}§""", """§N(t) = N_0 \cdot \tfrac{T}{t}§"""),
                        done = l("Halbierung alle 5730 Jahre", "Halving every 5730 years"),
                        hints = listOf(l("Kernidee: In jeder Halbwertszeit zerfällt die Hälfte des Rests.", "Key idea: in each half-life, half of what's left decays."), l("Nach 2 Halbwertszeiten ist noch ein Viertel da.", "After 2 half-lives a quarter is left.")),
                    ),
                    num(
                        l("Wie lange ist Ötzi tot?", "How long has Ötzi been dead?"), t, l("Jahre", "years"), rel = 0.03,
                        done = l("etwa 5250 Jahre", "about 5250 years"),
                        hints = listOf(l("""Löse §0{,}53 = 0{,}5^{t/5730}§.""", """Solve §0.53 = 0.5^{t/5730}§."""), l("""§t = 5730 \cdot \dfrac{\ln 0{,}53}{\ln 0{,}5}§""", """§t = 5730 \cdot \dfrac{\ln 0.53}{\ln 0.5}§""")),
                        formula = card(l("Halbwertszeit", "Half-life"), """§N = N_0 \cdot 2^{-t/T} \;\Leftrightarrow\; t = T \cdot \log_2 \dfrac{N_0}{N}§""", l("Wie viele Halbierungen stecken in dem Rest?", "How many halvings are in what's left?")),
                    ),
                ),
                solution = listOf(l("""§0{,}53 = 2^{-t/5730} \;\Rightarrow\; t = 5730 \cdot \log_2 \dfrac{1}{0{,}53} \approx 5250§ Jahre""", """§0.53 = 2^{-t/5730} \;\Rightarrow\; t = 5730 \cdot \log_2 \dfrac{1}{0.53} \approx 5250§ years""")),
                background = l(
                    "Kohlenstoff-14 entsteht ständig in der oberen Atmosphäre, wenn kosmische Strahlung Stickstoff trifft. Pflanzen nehmen ihn auf, Tiere und Menschen essen die Pflanzen, deshalb haben alle Lebewesen denselben Anteil. Nach dem Tod kommt nichts mehr nach, und das C-14 zerfällt mit fester Halbwertszeit. Am Rest kann man das Alter ablesen, bis etwa 50.000 Jahre zurück.",
                    "Carbon-14 is constantly created in the upper atmosphere when cosmic rays hit nitrogen. Plants take it up, animals and people eat the plants, so all living things have the same share. After death nothing new comes in, and the C-14 decays with a fixed half-life. From what's left you can read the age, back to about 50,000 years.",
                ),
                fact = l("Ötzi wurde 1991 von Wanderern entdeckt. In seiner Schulter steckt eine Pfeilspitze: Er wurde vermutlich ermordet.", "Ötzi was discovered by hikers in 1991. An arrowhead is lodged in his shoulder: he was probably murdered."),
            )
        },
        TaskDef("pangaea", Topic.Geology, E) {
            Task(
                "pangaea", Topic.Geology, E,
                question = l("Vor etwa 300 Millionen Jahren hingen fast alle Kontinente zu einem einzigen Superkontinent zusammen. Wie heißt er?", "About 300 million years ago almost all continents were joined into a single supercontinent. What is it called?"),
                steps = listOf(
                    choice(
                        l("Der Superkontinent heißt …", "The supercontinent is called …"),
                        listOf(l("Pangäa", "Pangaea"), "Atlantis", l("Laurasia (entstand erst später)", "Laurasia (formed later)")),
                        done = l("Pangäa", "Pangaea"),
                        hints = listOf(l("Der Name kommt aus dem Griechischen: „ganze Erde“.", "The name comes from Greek: \"whole Earth\"."), l("Atlantis ist eine Sage von Platon.", "Atlantis is a legend from Plato.")),
                    ),
                ),
                solution = listOf(l("Pangäa, griechisch für „ganze Erde“. Vor rund 200 Millionen Jahren begann es auseinanderzubrechen.", "Pangaea, Greek for \"whole Earth\". About 200 million years ago it began to break apart.")),
                background = l(
                    "Die Kontinente sitzen auf Platten, die sich jedes Jahr ein paar Zentimeter bewegen. Über Hunderte Millionen Jahre fügen sie sich immer wieder zu Superkontinenten zusammen und brechen wieder auseinander. Hinweise darauf: Die Küsten von Südamerika und Afrika passen zusammen, und auf beiden findet man dieselben Fossilien und Gesteine.",
                    "The continents sit on plates that move a few centimetres every year. Over hundreds of millions of years they keep coming together into supercontinents and breaking apart again. Clues: the coasts of South America and Africa fit together, and the same fossils and rocks are found on both.",
                ),
                fact = l("In etwa 250 Millionen Jahren könnte es wieder einen Superkontinent geben. Geologen nennen ihn schon Pangäa Proxima.", "In about 250 million years there could be a supercontinent again. Geologists already call it Pangaea Proxima."),
            )
        },

        // ======================================= PHILOSOPHIE =======================================
        TaskDef("sandhaufen", Topic.Philosophy, E) {
            Task(
                "sandhaufen", Topic.Philosophy, E,
                question = l("Ein Sandkorn ist kein Haufen. Legt man zu etwas, das kein Haufen ist, ein Korn dazu, wird daraus kein Haufen. Also gibt es gar keine Sandhaufen. Wo steckt das Problem?", "One grain of sand is not a heap. Adding one grain to something that isn't a heap doesn't make it a heap. So there are no heaps of sand at all. Where's the problem?"),
                steps = listOf(
                    choice(
                        l("Das Problem ist …", "The problem is …"),
                        listOf(l("dass „Haufen“ ein unscharfer Begriff ohne feste Grenze ist", "that \"heap\" is a vague word without a sharp boundary"), l("dass Sandkörner verschieden groß sind", "that grains of sand differ in size"), l("ein Rechenfehler beim Zählen", "a counting mistake")),
                        done = l("Unscharfe Begriffe", "Vague words"),
                        hints = listOf(l("Ab wie vielen Körnern genau beginnt ein Haufen?", "From exactly how many grains does a heap begin?"), l("Ähnlich: Ab welchem Haar ist man kahl?", "Similar: at which hair are you bald?")),
                    ),
                ),
                solution = listOf(l("Jeder einzelne Schritt klingt richtig, aber „Haufen“ hat keine scharfe Grenze. Die Logik setzt voraus, dass jeder Satz klar wahr oder falsch ist, und das stimmt für unscharfe Begriffe nicht.", "Each single step sounds right, but \"heap\" has no sharp boundary. Logic assumes every statement is clearly true or false, and that doesn't hold for vague words.")),
                background = l(
                    "Das Sorites-Paradox (griechisch soros = Haufen) stammt aus der Antike. Es zeigt, dass viele Wörter unseres Alltags keine scharfen Grenzen haben: groß, alt, reich, Haufen. Philosophen schlagen verschiedene Lösungen vor, etwa eine Logik mit Abstufungen statt nur wahr und falsch. Das ist ganz praktisch: Auch Computer, Gerichte und Gesetze brauchen Regeln für Grauzonen.",
                    "The sorites paradox (Greek soros = heap) dates back to antiquity. It shows that many everyday words have no sharp boundaries: tall, old, rich, heap. Philosophers propose different solutions, such as a logic with degrees instead of just true and false. That's quite practical: computers, courts and laws need rules for grey areas too.",
                ),
                fact = l("Die „Fuzzy-Logik“ mit Wahrheitswerten zwischen 0 und 1 steckt heute in Waschmaschinen und Kameras.", "\"Fuzzy logic\" with truth values between 0 and 1 is used today in washing machines and cameras."),
            )
        },
        TaskDef("kant-luege", Topic.PhilEthics, M) {
            Task(
                "kant-luege", Topic.PhilEthics, M,
                question = l("Kants kategorischer Imperativ: „Handle nur nach der Regel, von der du zugleich wollen kannst, dass sie ein allgemeines Gesetz wird.“ Ein Freund versteckt sich bei dir, ein Verfolger fragt, wo er ist.", "Kant's categorical imperative: \"Act only according to that rule by which you can at the same time will that it should become a universal law.\" A friend hides at your place, and a pursuer asks where he is."),
                steps = listOf(
                    choice(
                        l("Was sagt Kant selbst zu einer Lüge in diesem Fall?", "What does Kant himself say about lying in this case?"),
                        listOf(l("Auch hier darf man nicht lügen", "Even here you must not lie"), l("Lügen ist erlaubt, weil es Leben rettet", "Lying is allowed because it saves a life"), l("Man muss den Verfolger angreifen", "You must attack the pursuer")),
                        done = l("Auch hier nicht lügen", "No lying, even here"),
                        hints = listOf(l("Kann „Lüge, wenn es dir passt“ ein Gesetz für alle sein?", "Can \"lie when it suits you\" be a law for everyone?"), l("Wenn alle lügen dürften, würde niemand mehr irgendeiner Aussage glauben.", "If everyone were allowed to lie, nobody would believe any statement any more.")),
                    ),
                ),
                solution = listOf(l("Kant schrieb 1797 tatsächlich, man dürfe auch einem Mörder gegenüber nicht lügen: Eine allgemeine Erlaubnis zur Lüge würde das Vertrauen zerstören, auf dem jede Aussage beruht.", "In 1797 Kant really wrote that you mustn't lie even to a murderer: a general permission to lie would destroy the trust every statement relies on.")),
                background = l(
                    "Kants Ethik fragt nicht nach den Folgen einer Handlung, sondern nach ihrer Regel: Könnte jeder so handeln, ohne dass die Regel sich selbst aufhebt? Lügen scheitert an diesem Test. Viele finden Kants Antwort im Beispiel mit dem Verfolger aber unmenschlich. Spätere Philosophen schlagen vor, Pflichten gegeneinander abzuwägen, etwa die Pflicht zur Wahrheit gegen die Pflicht, Leben zu schützen.",
                    "Kant's ethics doesn't ask about the consequences of an action but about its rule: could everyone act this way without the rule cancelling itself out? Lying fails this test. Many find Kant's answer in the pursuer example inhuman, though. Later philosophers suggest weighing duties against each other, for example the duty to tell the truth against the duty to protect life.",
                ),
                fact = l("Kant stand angeblich so pünktlich zu seinem Spaziergang auf, dass die Leute in Königsberg ihre Uhren danach stellten.", "Kant supposedly went for his walk so punctually that people in Königsberg set their clocks by him."),
            )
        },
        TaskDef("gehirn-tank", Topic.PhilKnowledge, H) {
            Task(
                "gehirn-tank", Topic.PhilKnowledge, H,
                question = l("Stell dir vor, dein Gehirn liegt in einem Tank und ein Computer füttert es mit perfekt echten Sinneseindrücken. Alles, was du erlebst, wäre genau gleich.", "Imagine your brain lies in a vat and a computer feeds it perfectly real sensations. Everything you experience would be exactly the same."),
                steps = listOf(
                    choice(
                        l("Welche Frage wirft das auf?", "What question does this raise?"),
                        listOf(l("Ob wir überhaupt sicher wissen können, dass die Außenwelt existiert", "Whether we can know for sure that the outside world exists at all"), l("Wie viel Strom ein Gehirn braucht", "How much electricity a brain needs"), l("Ob Computer Gefühle haben", "Whether computers have feelings")),
                        done = l("Skeptizismus", "Scepticism"),
                        hints = listOf(l("Kannst du von innen einen Unterschied bemerken?", "Could you notice a difference from the inside?"), l("Die Frage ist alt: Descartes dachte an einen Täuschergeist.", "The question is old: Descartes thought of a deceiving demon.")),
                    ),
                    choice(
                        l("Was blieb für Descartes trotzdem sicher?", "What remained certain for Descartes anyway?"),
                        listOf(l("Dass er denkt und deshalb existiert", "That he thinks and therefore exists"), l("Dass die Welt so ist, wie er sie sieht", "That the world is as he sees it"), l("Gar nichts", "Nothing at all")),
                        done = l("„Ich denke, also bin ich.“", "\"I think, therefore I am.\""),
                        hints = listOf(l("Selbst wer getäuscht wird, muss dafür existieren.", "Even someone being deceived has to exist for it."), l("Sein berühmtester Satz.", "His most famous sentence.")),
                    ),
                ),
                solution = listOf(l("Von innen lässt sich eine perfekte Täuschung nicht erkennen. Sicher bleibt nur, dass es ein denkendes Ich gibt, das getäuscht werden könnte: „cogito, ergo sum“.", "From the inside a perfect deception can't be detected. All that remains certain is that there is a thinking self that could be deceived: \"cogito, ergo sum\".")),
                background = l(
                    "Das Gehirn im Tank ist eine moderne Fassung einer sehr alten Frage der Erkenntnistheorie: Was können wir wirklich wissen? René Descartes zweifelte 1641 an allem, was sich bezweifeln lässt, und fand einen festen Punkt: das eigene Denken. Der Philosoph Hilary Putnam meinte später, der Satz „Ich bin ein Gehirn im Tank“ widerlege sich selbst, weil unsere Wörter sich dann gar nicht auf echte Tanks beziehen könnten. Filme wie „Matrix“ spielen genau mit dieser Idee.",
                    "The brain in a vat is a modern version of a very old question in epistemology: what can we really know? In 1641 René Descartes doubted everything that could be doubted and found one fixed point: his own thinking. The philosopher Hilary Putnam later argued that the sentence \"I am a brain in a vat\" refutes itself, because our words couldn't then refer to real vats at all. Films like \"The Matrix\" play with exactly this idea.",
                ),
                fact = l("Descartes schrieb den Satz zuerst auf Französisch: „Je pense, donc je suis.“", "Descartes first wrote the sentence in French: \"Je pense, donc je suis.\""),
            )
        },

        // ========================================= ERDKUNDE =========================================
        TaskDef("horizont", Topic.Geography, H) {
            val r = 6.371e6
            val d1 = sqrt(2 * r * 1.8) / 1000
            val d2 = sqrt(2 * r * 2962.0) / 1000
            Task(
                "horizont", Topic.Geography, H,
                question = l("Wie weit kannst du am Meer bis zum Horizont sehen, wenn deine Augen 1,80 m hoch sind? Und wie weit von der Zugspitze (2962 m)?", "How far can you see to the horizon at the sea if your eyes are 1.80 m high? And how far from the Zugspitze (2962 m)?"),
                given = listOf(l("Erdradius: 6371 km", "Earth's radius: 6371 km")),
                steps = listOf(
                    num(
                        l("Sichtweite am Strand", "Visibility on the beach"), d1, "km", rel = 0.03,
                        done = l("""§\approx 4{,}8\ \text{km}§""", """§\approx 4.8\ \text{km}§"""),
                        hints = listOf(l("Kernidee: Deine Sichtlinie berührt die Erdkugel. Erdradius, Sichtlinie und Radius plus Augenhöhe bilden ein rechtwinkliges Dreieck.", "Key idea: your line of sight touches the globe. Radius, line of sight and radius plus eye height form a right triangle."), """§d \approx \sqrt{2 R h}§"""),
                        formula = card("Pythagoras", """§d^2 + R^2 = (R + h)^2 \;\Rightarrow\; d \approx \sqrt{2Rh}§""", l("""Weil §h§ winzig gegen §R§ ist, darf man §h^2§ weglassen.""", """Because §h§ is tiny compared with §R§, you may drop §h^2§.""")),
                    ),
                    num(
                        l("Sichtweite von der Zugspitze", "Visibility from the Zugspitze"), d2, "km", rel = 0.03,
                        done = l("""§\approx 194\ \text{km}§""", """§\approx 194\ \text{km}§"""),
                        hints = listOf(l("Dieselbe Formel mit 2962 m.", "The same formula with 2962 m."), l("""§\sqrt{2 \cdot 6{,}371 \cdot 10^6 \cdot 2962}\ \text{m}§""", """§\sqrt{2 \cdot 6.371 \cdot 10^6 \cdot 2962}\ \text{m}§""")),
                    ),
                ),
                solution = listOf(
                    l("""§d = \sqrt{2 \cdot 6{,}371 \cdot 10^6\ \text{m} \cdot 1{,}8\ \text{m}} \approx 4{,}8\ \text{km}§""", """§d = \sqrt{2 \cdot 6.371 \cdot 10^6\ \text{m} \cdot 1.8\ \text{m}} \approx 4.8\ \text{km}§"""),
                    l("""§d = \sqrt{2 \cdot 6{,}371 \cdot 10^6\ \text{m} \cdot 2962\ \text{m}} \approx 194\ \text{km}§""", """§d = \sqrt{2 \cdot 6.371 \cdot 10^6\ \text{m} \cdot 2962\ \text{m}} \approx 194\ \text{km}§"""),
                ),
                background = l(
                    "Weil die Erde gekrümmt ist, verschwindet alles ab einer gewissen Entfernung hinter dem Horizont. Wie weit das ist, wächst nur mit der Wurzel der Höhe: Viermal so hoch bedeutet doppelt so weit. Die Luft biegt das Licht zusätzlich ein wenig zur Erde hin, deshalb sieht man in Wirklichkeit etwa 8 % weiter.",
                    "Because the Earth is curved, everything beyond a certain distance disappears behind the horizon. How far that is only grows with the square root of the height: four times as high means twice as far. The air also bends light slightly towards the Earth, so in reality you can see about 8 % further.",
                ),
                fact = l("Bei sehr klarer Luft sieht man von der Zugspitze tatsächlich Berge in Italien und der Schweiz.", "In very clear air you can actually see mountains in Italy and Switzerland from the Zugspitze."),
            )
        },
        TaskDef("flug-new-york", Topic.Geography, M) {
            Task(
                "flug-new-york", Topic.Geography, M,
                question = l("Dein Flug startet um 10:00 Uhr in Frankfurt und dauert 8 Stunden 30 Minuten. New York ist 6 Stunden hinter Deutschland. Wann landest du nach New Yorker Zeit?", "Your flight leaves Frankfurt at 10:00 and takes 8 hours 30 minutes. New York is 6 hours behind Germany. When do you land in New York time?"),
                steps = listOf(
                    choice(
                        l("Landung in New York", "Landing in New York"),
                        listOf(l("12:30 Uhr", "12:30"), l("18:30 Uhr", "18:30"), l("04:30 Uhr", "04:30")),
                        done = l("12:30 Uhr Ortszeit", "12:30 local time"),
                        hints = listOf(l("In Deutschland wäre es bei der Landung 18:30 Uhr.", "In Germany it would be 18:30 when you land."), l("Davon 6 Stunden abziehen.", "Subtract 6 hours.")),
                    ),
                ),
                solution = listOf(l("10:00 + 8:30 = 18:30 deutscher Zeit, minus 6 Stunden = 12:30 Uhr in New York.", "10:00 + 8:30 = 18:30 German time, minus 6 hours = 12:30 in New York.")),
                background = l(
                    "Weil sich die Erde von West nach Ost dreht, geht die Sonne im Osten früher auf. Fliegt man nach Westen, fliegt man der Sonne hinterher und „gewinnt“ Stunden, nach Osten verliert man welche. Der Körper merkt das: Die innere Uhr braucht etwa einen Tag pro Zeitzone, um sich umzustellen. Das ist der Jetlag, und nach Osten ist er meist schlimmer.",
                    "Because the Earth rotates from west to east, the Sun rises earlier in the east. Flying west you chase the Sun and \"gain\" hours, flying east you lose some. Your body notices: the internal clock needs about a day per time zone to adjust. That's jet lag, and it's usually worse going east.",
                ),
                fact = l("Auf dem Rückflug wird es oft absurd: Du startest abends in New York und landest am nächsten Morgen, obwohl du nur 7 Stunden geflogen bist.", "The return flight often gets absurd: you take off in the evening in New York and land the next morning, although you only flew 7 hours."),
            )
        },

        // ========================================= BIOLOGIE =========================================
        TaskDef("gehirn-energie", Topic.Biology, M) {
            val kcal = 20.0 * 86_400 / 4184
            Task(
                "gehirn-energie", Topic.Biology, M,
                question = l("Dein Gehirn arbeitet mit etwa 20 Watt, ungefähr so viel wie eine schwache Lampe. Wie viele Kilokalorien verbraucht es am Tag?", "Your brain runs on about 20 watts, roughly as much as a dim lamp. How many kilocalories does it use per day?"),
                given = listOf("""§1\ \text{kcal} = 4184\ \text{J}§"""),
                steps = listOf(
                    num(
                        l("Kilokalorien pro Tag", "Kilocalories per day"), kcal, "kcal", rel = 0.04,
                        done = l("gut 400 kcal", "just over 400 kcal"),
                        hints = listOf(l("Energie = Leistung mal Zeit, in Sekunden.", "Energy = power times time, in seconds."), """§20\ \text{W} \cdot 86\,400\ \text{s} : 4184§"""),
                    ),
                ),
                solution = listOf(l("""§E = 20\ \text{W} \cdot 86\,400\ \text{s} = 1{,}73\ \text{MJ} \approx 413\ \text{kcal}§""", """§E = 20\ \text{W} \cdot 86\,400\ \text{s} = 1.73\ \text{MJ} \approx 413\ \text{kcal}§""")),
                background = l(
                    "Das Gehirn macht nur etwa 2 % deiner Körpermasse aus, verbraucht aber rund 20 % deiner Energie, auch im Schlaf. Das meiste geht dafür drauf, dass Nervenzellen ständig Ionen durch ihre Wände pumpen, um für das nächste Signal bereit zu sein. Nachdenken über eine schwere Aufgabe kostet übrigens kaum mehr als Tagträumen.",
                    "The brain is only about 2 % of your body mass but uses around 20 % of your energy, even while you sleep. Most of it goes into nerve cells constantly pumping ions through their walls to be ready for the next signal. Thinking hard about a problem, by the way, costs hardly more than daydreaming.",
                ),
                fact = l("Ein Supercomputer, der ein Gehirn grob simulieren will, braucht Millionen Watt.", "A supercomputer trying to roughly simulate a brain needs millions of watts."),
            )
        },
        TaskDef("bakterien", Topic.Biology, H) {
            val n = 2.0.pow(72)
            Task(
                "bakterien", Topic.Biology, H,
                question = l("Ein Darmbakterium teilt sich unter idealen Bedingungen alle 20 Minuten. Du startest mit einem einzigen.", "A gut bacterium divides every 20 minutes under ideal conditions. You start with a single one."),
                steps = listOf(
                    estimate(
                        l("Schätze: Wie viele Bakterien wären es nach 24 Stunden?", "Estimate: how many bacteria would there be after 24 hours?"), n, 1e3, 1e30, EstimateKind.Count, factor = 10.0,
                        done = l("""§2^{72} \approx 5 \cdot 10^{21}§""", """§2^{72} \approx 5 \cdot 10^{21}§"""),
                        hints = listOf(l("24 Stunden sind 72 Teilungen.", "24 hours is 72 divisions."), l("""§2^{10} \approx 1000§, also §2^{72} \approx 4 \cdot 10^{21}§""", """§2^{10} \approx 1000§, so §2^{72} \approx 4 \cdot 10^{21}§""")),
                    ),
                    choice(
                        l("Ein Bakterium wiegt etwa ein billionstel Gramm. Warum ist die Erde trotzdem nicht unter Bakterien begraben?", "A bacterium weighs about a trillionth of a gram. Why isn't the Earth buried under bacteria?"),
                        listOf(l("Nahrung und Platz gehen aus, Abfallstoffe bremsen das Wachstum", "Food and space run out, waste products slow growth"), l("Bakterien teilen sich nur einmal im Leben", "Bacteria only divide once in their life"), l("Die Rechnung stimmt nicht, es sind nur ein paar Tausend", "The calculation is wrong, it's only a few thousand")),
                        done = l("Die Ressourcen werden knapp", "Resources run out"),
                        hints = listOf(l("""§5 \cdot 10^{21} \cdot 10^{-15}\ \text{kg}§ sind Millionen Kilogramm.""", """§5 \cdot 10^{21} \cdot 10^{-15}\ \text{kg}§ is millions of kilograms."""), l("Exponentielles Wachstum hält nie lange.", "Exponential growth never lasts long.")),
                    ),
                ),
                solution = listOf("""§24 \cdot 3 = 72 \;\Rightarrow\; 2^{72} \approx 4{,}7 \cdot 10^{21}§""".let { l(it, it.replace("4{,}7", "4.7")) }),
                background = l(
                    "Bakterien vermehren sich durch Zweiteilung. Solange genug Nahrung da ist, verdoppelt sich ihre Zahl in festen Abständen: exponentielles Wachstum. In der Natur hält das nie lange, weil Nährstoffe ausgehen und Abfallstoffe sich anreichern. Dann flacht die Kurve ab und wird zu einer S-Kurve. Genau deshalb sind Kühlschränke so wirksam: Kälte verlangsamt die Teilung stark.",
                    "Bacteria multiply by splitting in two. As long as there's enough food, their number doubles at fixed intervals: exponential growth. In nature this never lasts long, because nutrients run out and waste builds up. Then the curve flattens into an S-curve. That's exactly why fridges work so well: cold slows division down a lot.",
                ),
                fact = l("In deinem Darm leben ungefähr so viele Bakterien, wie dein Körper eigene Zellen hat, rund 40 Billionen.", "Your gut contains roughly as many bacteria as your body has cells of its own, about 40 trillion."),
            )
        },

        // ========================================== CHEMIE ==========================================
        TaskDef("ph-wert", Topic.Chemistry, M) {
            Task(
                "ph-wert", Topic.Chemistry, M,
                question = l("""Der pH-Wert misst, wie sauer eine Lösung ist: §\text{pH} = -\log_{10} [\text{H}^+]§. Cola hat etwa pH 3, Leitungswasser pH 7.""", """The pH value measures how acidic a solution is: §\text{pH} = -\log_{10} [\text{H}^+]§. Cola has about pH 3, tap water pH 7."""),
                steps = listOf(
                    num(
                        l("Wie viel mal mehr H⁺-Ionen enthält Cola als Wasser?", "How many times more H⁺ ions does cola contain than water?"), 10_000.0, "", rel = 0.01,
                        done = l("10.000-mal", "10,000 times"),
                        hints = listOf(l("Kernidee: Eine pH-Stufe ist ein Faktor 10.", "Key idea: one pH step is a factor of 10."), """§10^{7 - 3}§"""),
                        formula = card(l("Logarithmische Skala", "Logarithmic scale"), """§\Delta \text{pH} = 1 \;\Leftrightarrow\; \text{Faktor } 10§""".let { l(it, it.replace("Faktor", "factor")) }, l("Wie bei der Erdbeben-Skala und den Dezibel.", "Like the earthquake scale and decibels.")),
                    ),
                    num(
                        l("Du verdünnst Säure mit pH 2 auf das Zehnfache. Welchen pH hat sie dann?", "You dilute acid with pH 2 tenfold. What is its pH then?"), 3.0, "", abs = 0.05,
                        done = "pH 3",
                        hints = listOf(l("Zehnmal weniger H⁺ pro Liter.", "Ten times less H⁺ per litre."), l("Eine Stufe nach oben.", "One step up.")),
                    ),
                ),
                solution = listOf("""§10^{-3} : 10^{-7} = 10^4§""", l("Zehnfach verdünnt: zehnmal weniger H⁺, der pH steigt um 1 auf 3.", "Diluted tenfold: ten times less H⁺, the pH rises by 1 to 3.")),
                background = l(
                    "In Wasser gibt es immer ein paar freie Wasserstoff-Ionen. Säuren geben zusätzliche ab, Laugen binden welche. Weil die Mengen über viele Zehnerpotenzen reichen, nutzt man eine logarithmische Skala. pH 7 ist neutral, darunter sauer, darüber basisch. Dein Magen hat etwa pH 1,5, deine Haut leicht sauer um pH 5.",
                    "Water always contains a few free hydrogen ions. Acids release extra ones, bases bind them. Because the amounts span many powers of ten, a logarithmic scale is used. pH 7 is neutral, below is acidic, above is basic. Your stomach is about pH 1.5, your skin slightly acidic around pH 5.",
                ),
                fact = l("Das „p“ im pH steht vermutlich für „Potenz“. Erfunden hat den Begriff 1909 der dänische Chemiker Søren Sørensen in einer Brauerei.", "The \"p\" in pH probably stands for \"power\". The term was invented in 1909 by the Danish chemist Søren Sørensen at a brewery."),
            )
        },
        TaskDef("luftanteil", Topic.Chemistry, E) {
            Task(
                "luftanteil", Topic.Chemistry, E,
                question = l("Wie viel Prozent der Luft, die du atmest, ist Sauerstoff?", "What percentage of the air you breathe is oxygen?"),
                steps = listOf(
                    choice(
                        l("Sauerstoffanteil der Luft", "Oxygen content of air"),
                        listOf(l("Etwa 21 %", "About 21 %"), l("Etwa 50 %", "About 50 %"), l("Etwa 78 %", "About 78 %")),
                        done = l("etwa 21 %", "about 21 %"),
                        hints = listOf(l("Der größte Teil der Luft ist ein anderes Gas.", "Most of the air is a different gas."), l("Stickstoff macht etwa 78 % aus.", "Nitrogen makes up about 78 %.")),
                    ),
                ),
                solution = listOf(l("Luft besteht zu rund 78 % aus Stickstoff, 21 % Sauerstoff und knapp 1 % Argon. Kohlendioxid ist nur etwa 0,04 %.", "Air is about 78 % nitrogen, 21 % oxygen and just under 1 % argon. Carbon dioxide is only about 0.04 %.")),
                background = l(
                    "Den Sauerstoff in der Luft verdanken wir Lebewesen: Vor rund 2,4 Milliarden Jahren begannen Cyanobakterien mit der Fotosynthese und setzten ihn frei. Davor gab es fast keinen. Kohlendioxid ist nur eine Spur, aber eine wichtige: Schon kleine Änderungen seines Anteils verändern das Klima spürbar.",
                    "We owe the oxygen in the air to living things: about 2.4 billion years ago cyanobacteria began photosynthesis and released it. Before that there was hardly any. Carbon dioxide is only a trace, but an important one: even small changes in its share noticeably change the climate.",
                ),
                fact = l("Ausgeatmete Luft enthält immer noch etwa 16 % Sauerstoff. Deshalb funktioniert Mund-zu-Mund-Beatmung.", "Exhaled air still contains about 16 % oxygen. That's why mouth-to-mouth resuscitation works."),
            )
        },

        // ====================================== ALLGEMEINWISSEN ======================================
        TaskDef("licht-sonne", Topic.Knowledge, E) {
            Task(
                "licht-sonne", Topic.Knowledge, E,
                question = l("Wie lange braucht das Licht von der Sonne bis zu dir?", "How long does light take from the Sun to you?"),
                steps = listOf(
                    choice(
                        l("Das Sonnenlicht ist unterwegs …", "Sunlight travels for …"),
                        listOf(l("etwa 8 Minuten", "about 8 minutes"), l("etwa 1 Sekunde", "about 1 second"), l("etwa 8 Stunden", "about 8 hours")),
                        done = l("etwa 8 Minuten", "about 8 minutes"),
                        hints = listOf(l("Die Sonne ist 150 Millionen km entfernt, Licht schafft 300.000 km pro Sekunde.", "The Sun is 150 million km away, light covers 300,000 km per second."), l("150.000.000 : 300.000 = 500 Sekunden.", "150,000,000 : 300,000 = 500 seconds.")),
                    ),
                ),
                solution = listOf("""§\dfrac{1{,}5 \cdot 10^{8}\ \text{km}}{3 \cdot 10^{5}\ \text{km/s}} = 500\ \text{s} \approx 8{,}3\ \text{min}§"""),
                background = l(
                    "Licht ist das Schnellste, was es gibt, aber nicht unendlich schnell. Deshalb ist jeder Blick in den Himmel ein Blick in die Vergangenheit: Die Sonne siehst du so, wie sie vor 8 Minuten war, den nächsten Stern so, wie er vor gut 4 Jahren war. Die Andromeda-Galaxie, die man mit bloßem Auge erkennen kann, zeigt sich so, wie sie vor 2,5 Millionen Jahren aussah.",
                    "Light is the fastest thing there is, but not infinitely fast. So every look at the sky is a look into the past: you see the Sun as it was 8 minutes ago, the nearest star as it was over 4 years ago. The Andromeda galaxy, which you can see with the naked eye, appears as it looked 2.5 million years ago.",
                ),
                fact = l("Würde die Sonne plötzlich verschwinden, würde die Erde noch 8 Minuten weiter um sie kreisen: Auch die Schwerkraft breitet sich mit Lichtgeschwindigkeit aus.", "If the Sun suddenly vanished, the Earth would keep orbiting for 8 more minutes: gravity also travels at the speed of light."),
            )
        },
        TaskDef("klavierstimmer", Topic.Knowledge, M) {
            val n = 3.7e6 / 2 / 20 / (4 * 250)
            Task(
                "klavierstimmer", Topic.Knowledge, M,
                question = l("Eine berühmte Schätzfrage des Physikers Enrico Fermi: Wie viele Klavierstimmer gibt es in Berlin?", "A famous estimation question by physicist Enrico Fermi: how many piano tuners are there in Berlin?"),
                given = listOf(l("3,7 Mio. Einwohner, 2 Personen pro Haushalt, jeder 20. Haushalt hat ein Klavier, das einmal im Jahr gestimmt wird. Ein Stimmer schafft 4 Klaviere am Tag an 250 Tagen.", "3.7 million inhabitants, 2 people per household, every 20th household has a piano, tuned once a year. A tuner manages 4 pianos a day on 250 days.")),
                steps = listOf(
                    estimate(
                        l("Schätze die Zahl der Klavierstimmer", "Estimate the number of piano tuners"), n, 1.0, 1e5, EstimateKind.Count, factor = 2.5,
                        done = l("rund 90", "about 90"),
                        hints = listOf(l("Klaviere: 3,7 Mio. : 2 : 20 ≈ 92.000", "Pianos: 3.7 million : 2 : 20 ≈ 92,000"), l("Ein Stimmer schafft 1000 Klaviere im Jahr.", "One tuner handles 1000 pianos per year.")),
                    ),
                ),
                solution = listOf(l("""§\dfrac{3{,}7 \cdot 10^6}{2 \cdot 20} \approx 92\,500§ Klaviere, §\dfrac{92\,500}{4 \cdot 250} \approx 90§ Stimmer""", """§\dfrac{3.7 \cdot 10^6}{2 \cdot 20} \approx 92\,500§ pianos, §\dfrac{92\,500}{4 \cdot 250} \approx 90§ tuners""")),
                background = l(
                    "Fermi-Fragen trainieren eine wichtige Fähigkeit: ein unlösbar scheinendes Problem in kleine Schritte zu zerlegen, die man grob schätzen kann. Die einzelnen Fehler gleichen sich dabei oft aus, und das Ergebnis liegt erstaunlich häufig in der richtigen Größenordnung. Fermi schätzte so 1945 auch die Sprengkraft der ersten Atombombe, indem er Papierschnipsel fallen ließ und maß, wie weit die Druckwelle sie trug.",
                    "Fermi questions train an important skill: breaking a seemingly impossible problem into small steps you can roughly estimate. The individual errors often cancel out, and the result is surprisingly often in the right order of magnitude. In 1945 Fermi also estimated the yield of the first atomic bomb this way, by dropping scraps of paper and measuring how far the blast wave carried them.",
                ),
                fact = l("In Vorstellungsgesprächen bei großen Firmen werden solche Fragen bis heute gestellt. Es geht nicht um die Zahl, sondern um den Weg.", "Such questions are still asked in job interviews at big companies. It's not about the number but about the approach."),
            )
        },
        // ===================================== MODERNE PHYSIK =====================================
        TaskDef("myon", Topic.Relativity, X) {
            val c = 3.00e8
            val v = 0.998 * c
            val tau = 2.2e-6
            val gamma = 1 / sqrt(1 - 0.998 * 0.998)
            Task(
                "myon", Topic.Relativity, X,
                question = l("""Myonen entstehen in etwa 15 km Höhe, wenn kosmische Strahlung auf Luft trifft. Sie fliegen mit §v = 0{,}998\,c§ und leben im Mittel nur §\tau = 2{,}2\ \mu\text{s}§. Trotzdem kommen viele am Boden an.""", """Muons are created at about 15 km altitude when cosmic rays hit the air. They fly at §v = 0.998\,c§ and live only §\tau = 2.2\ \mu\text{s}§ on average. Even so, many reach the ground."""),
                given = listOf(l("""§c = 3{,}00 \cdot 10^8\ \text{m/s}§""", """§c = 3.00 \cdot 10^8\ \text{m/s}§""")),
                steps = listOf(
                    num(
                        l("Wie weit käme ein Myon ohne Relativität in seiner Lebensdauer?", "How far would a muon get in its lifetime without relativity?"), v * tau, "m", rel = 0.03,
                        done = l("""§\approx 660\ \text{m}§, viel zu wenig""", """§\approx 660\ \text{m}§, far too little"""),
                        hints = listOf("""§s = v \cdot \tau§""", l("""§0{,}998 \cdot 3 \cdot 10^8 \cdot 2{,}2 \cdot 10^{-6}\ \text{m}§""", """§0.998 \cdot 3 \cdot 10^8 \cdot 2.2 \cdot 10^{-6}\ \text{m}§""")),
                    ),
                    num(
                        l("""Wie groß ist der Lorentz-Faktor §\gamma§?""", """What is the Lorentz factor §\gamma§?"""), gamma, "", rel = 0.03,
                        done = l("""§\gamma \approx 15{,}8§""", """§\gamma \approx 15.8§"""),
                        hints = listOf("""§\gamma = \dfrac{1}{\sqrt{1 - v^2/c^2}}§""", l("""§1 - 0{,}998^2 \approx 0{,}004§""", """§1 - 0.998^2 \approx 0.004§""")),
                        formula = card(l("Zeitdilatation", "Time dilation"), """§\Delta t = \gamma\, \tau§""", l("Für uns lebt das schnelle Myon um den Faktor γ länger.", "To us, the fast muon lives longer by a factor of γ.")),
                    ),
                    num(
                        l("Wie weit kommt es mit Zeitdilatation (in km)?", "How far does it get with time dilation (in km)?"), v * tau * gamma / 1000, "km", rel = 0.04,
                        done = l("""§\approx 10{,}4\ \text{km}§""", """§\approx 10.4\ \text{km}§"""),
                        hints = listOf(l("Die Strecke wird um γ länger.", "The distance gets longer by γ."), l("""§660\ \text{m} \cdot 15{,}8§""", """§660\ \text{m} \cdot 15.8§""")),
                    ),
                    choice(
                        l("Wie erklärt das Myon selbst (in seinem eigenen System) die Ankunft?", "How does the muon itself (in its own frame) explain arriving?"),
                        listOf(l("Für es ist die Atmosphäre um γ verkürzt (Längenkontraktion)", "To it, the atmosphere is shortened by γ (length contraction)"), l("Es fliegt schneller als das Licht", "It flies faster than light"), l("Es lebt in seinem System länger", "It lives longer in its own frame")),
                        done = l("Längenkontraktion", "Length contraction"),
                        hints = listOf(l("In seinem eigenen System ist das Myon in Ruhe und lebt normal lange.", "In its own frame the muon is at rest and lives a normal lifetime."), l("Dafür kommt ihm die Erde entgegen, und Strecken in Bewegungsrichtung sind kürzer.", "Instead the Earth comes towards it, and distances along the motion are shorter.")),
                    ),
                ),
                solution = listOf(
                    l("""§s_\text{klassisch} = 0{,}998 \cdot 3 \cdot 10^8 \cdot 2{,}2 \cdot 10^{-6}\ \text{m} \approx 660\ \text{m}§""", """§s_\text{classical} = 0.998 \cdot 3 \cdot 10^8 \cdot 2.2 \cdot 10^{-6}\ \text{m} \approx 660\ \text{m}§"""),
                    l("""§\gamma = \dfrac{1}{\sqrt{1 - 0{,}998^2}} \approx 15{,}8 \;\Rightarrow\; s = \gamma \cdot 660\ \text{m} \approx 10{,}4\ \text{km}§""", """§\gamma = \dfrac{1}{\sqrt{1 - 0.998^2}} \approx 15.8 \;\Rightarrow\; s = \gamma \cdot 660\ \text{m} \approx 10.4\ \text{km}§"""),
                ),
                background = l(
                    "Myonen sind schwere Verwandte des Elektrons. Dass sie in großer Zahl am Boden ankommen, war einer der ersten direkten Belege für Einsteins spezielle Relativitätstheorie. Beide Beschreibungen sind richtig: Von der Erde aus gesehen geht die innere Uhr des Myons langsamer, aus Sicht des Myons ist die Atmosphäre zusammengestaucht. Am Ergebnis, ob es ankommt, ändert die Sichtweise nichts.",
                    "Muons are heavy relatives of the electron. That they reach the ground in large numbers was one of the first direct proofs of Einstein's special relativity. Both descriptions are correct: seen from the Earth, the muon's internal clock runs slow; from the muon's view, the atmosphere is squashed. The point of view doesn't change whether it arrives.",
                ),
                fact = l("Durch deine Hand fliegt etwa ein Myon pro Sekunde. Mit einer Nebelkammer aus Trockeneis und Alkohol kann man ihre Spuren sogar sehen.", "About one muon per second passes through your hand. With a cloud chamber made of dry ice and alcohol you can even see their tracks."),
            )
        },
        TaskDef("wasserstoff", Topic.Nuclear, X) {
            val de = 13.6 * (1.0 / 4 - 1.0 / 9)
            Task(
                "wasserstoff", Topic.Nuclear, X,
                question = l("""Im Wasserstoffatom kann das Elektron nur bestimmte Energien haben: §E_n = -\dfrac{13{,}6\ \text{eV}}{n^2}§. Springt es von einer höheren Bahn auf eine tiefere, sendet es ein Photon aus.""", """In a hydrogen atom the electron can only have certain energies: §E_n = -\dfrac{13.6\ \text{eV}}{n^2}§. When it jumps from a higher level to a lower one, it emits a photon."""),
                given = listOf("""§hc = 1240\ \text{eV nm}§"""),
                steps = listOf(
                    num(
                        l("""Wie viel Energie wird beim Sprung von §n = 3§ nach §n = 2§ frei?""", """How much energy is released in the jump from §n = 3§ to §n = 2§?"""), de, "eV", rel = 0.02,
                        done = l("""§\approx 1{,}89\ \text{eV}§""", """§\approx 1.89\ \text{eV}§"""),
                        hints = listOf("""§\Delta E = E_3 - E_2§""", l("""§13{,}6 \cdot \left(\tfrac{1}{4} - \tfrac{1}{9}\right)§""", """§13.6 \cdot \left(\tfrac{1}{4} - \tfrac{1}{9}\right)§""")),
                    ),
                    num(
                        l("Welche Wellenlänge hat das Licht?", "What is the wavelength of the light?"), 1240 / de, "nm", rel = 0.02,
                        done = l("""§\lambda \approx 656\ \text{nm}§""", """§\lambda \approx 656\ \text{nm}§"""),
                        hints = listOf("""§\lambda = \dfrac{hc}{\Delta E}§""", l("""§1240 : 1{,}89§""", """§1240 : 1.89§""")),
                        formula = card(l("Photonenenergie", "Photon energy"), """§E = \dfrac{hc}{\lambda}§""", l("Mehr Energie heißt kürzere Wellenlänge, also blaueres Licht.", "More energy means a shorter wavelength, so bluer light.")),
                    ),
                    choice(
                        l("Welche Farbe hat dieses Licht?", "What colour is this light?"),
                        listOf(l("Rot", "Red"), l("Blau", "Blue"), l("Unsichtbar (ultraviolett)", "Invisible (ultraviolet)")),
                        done = l("Rot", "Red"),
                        hints = listOf(l("Sichtbares Licht reicht von etwa 400 nm (violett) bis 700 nm (rot).", "Visible light ranges from about 400 nm (violet) to 700 nm (red)."), l("656 nm liegt nah am roten Ende.", "656 nm is close to the red end.")),
                    ),
                ),
                solution = listOf(
                    l("""§\Delta E = 13{,}6\ \text{eV} \cdot \left(\tfrac{1}{4} - \tfrac{1}{9}\right) \approx 1{,}89\ \text{eV}§""", """§\Delta E = 13.6\ \text{eV} \cdot \left(\tfrac{1}{4} - \tfrac{1}{9}\right) \approx 1.89\ \text{eV}§"""),
                    l("""§\lambda = \dfrac{1240\ \text{eV nm}}{1{,}89\ \text{eV}} \approx 656\ \text{nm}§ (rot)""", """§\lambda = \dfrac{1240\ \text{eV nm}}{1.89\ \text{eV}} \approx 656\ \text{nm}§ (red)"""),
                ),
                background = l(
                    "Jedes Element leuchtet in ganz bestimmten Farben, seinem Spektrum, wie ein Fingerabdruck. Niels Bohr erklärte das 1913 beim Wasserstoff: Die Elektronen können nur auf bestimmten Energiestufen sitzen, und beim Wechsel wird genau die Differenz als Licht frei. Die rote Linie bei 656 nm heißt H-alpha. Mit solchen Linien finden Astronomen heraus, woraus ferne Sterne bestehen.",
                    "Every element glows in very specific colours, its spectrum, like a fingerprint. In 1913 Niels Bohr explained this for hydrogen: electrons can only sit on certain energy levels, and when they switch, exactly the difference is released as light. The red line at 656 nm is called H-alpha. Astronomers use such lines to find out what distant stars are made of.",
                ),
                fact = l("Das Element Helium wurde 1868 zuerst im Licht der Sonne entdeckt, an einer unbekannten Spektrallinie, Jahrzehnte bevor man es auf der Erde fand.", "The element helium was first discovered in 1868 in sunlight, through an unknown spectral line, decades before it was found on Earth."),
            )
        },
    )
}
