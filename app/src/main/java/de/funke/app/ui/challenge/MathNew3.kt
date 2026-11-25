package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

/*
 * Mathe-Ausbau (seit 13.2): Textaufgaben und Analysis. Analysis im Geist von Thompson, "Calculus Made Easy"
 * (gemeinfrei): Ableiten heißt "wie schnell wächst es gerade?", Integrieren heißt "aufsummieren".
 * Alle Texte selbst geschrieben.
 */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object MathNew3 {
    val all: List<TaskDef> get() = word + calculus

    private val word = listOf(
        lab("ma.lesen", Topic.Word, E, 0.3f) {
            val pages = pick(340, 280, 412); val read = pick(90, 60, 112); val per = pick(25, 20, 30)
            val days = kotlin.math.ceil((pages - read).toDouble() / per)
            task(
                question = l("Dein Buch hat ${pages} Seiten. ${read} hast du schon gelesen, und jeden Tag schaffst du ${per} Seiten. Nach wie vielen Tagen bist du fertig?", "Your book has ${pages} pages. You've already read ${read}, and every day you manage ${per} pages. After how many days will you have finished?"),
                steps = listOf(num(l("Tage", "Days"), days, "", abs = 0.0, done = """§${m(days, 0)}§""",
                    hints = listOf(l("Kernidee: Erst ausrechnen, wie viele Seiten fehlen, dann durch die Seiten pro Tag teilen und aufrunden.", "Key idea: first work out how many pages are left, then divide by the pages per day and round up."), """§\dfrac{${pages} - ${read}}{${per}}§"""))),
                solution = listOf("""§\dfrac{${pages - read}}{${per}} \approx ${m((pages - read).toDouble() / per, 2)} \Rightarrow ${m(days, 0)}\ \text{Tage}§""".let { l(it, it.replace("Tage", "days")) }),
                background = l("Bei Textaufgaben hilft es, erst die Frage zu klären und dann die Zahlen zu ordnen: Was ist gegeben, was gesucht? Hier steckt noch eine Falle im Ergebnis: Bleibt beim Teilen ein Rest, braucht man einen Tag mehr, auch wenn man an ihm nur wenige Seiten liest. Ob man auf- oder abrundet, entscheidet die Situation, nicht die Mathematik.", "With word problems it helps to clarify the question first and then sort out the numbers: what's given, what's wanted? There's also a trap in the result here: if there's a remainder after dividing, you need one more day, even if you only read a few pages on it. Whether to round up or down is decided by the situation, not by the maths."),
            )
        },
        lab("ma.taschengeld", Topic.Word, E, 0.4f) {
            val allowance = pick(20, 16, 24); val frac = 4; val target = pick(75, 60, 90)
            val save = allowance / frac.toDouble(); val weeks = kotlin.math.ceil(target / save)
            task(
                question = l("Du bekommst ${allowance} € Taschengeld pro Woche und sparst davon ein Viertel. Wie viele Wochen brauchst du für ein Spiel, das ${target} € kostet?", "You get €${allowance} pocket money a week and save a quarter of it. How many weeks do you need for a game that costs €${target}?"),
                steps = listOf(num(l("Wochen", "Weeks"), weeks, "", abs = 0.0, done = """§${m(weeks, 0)}§""",
                    hints = listOf(l("Kernidee: Erst ausrechnen, wie viel du pro Woche sparst.", "Key idea: first work out how much you save per week."), """§\tfrac{1}{4} \cdot ${allowance} = ${m(save, 2)}\ \text{€}§"""))),
                solution = listOf("""§\dfrac{${target}}{${m(save, 2)}} = ${m(target / save, 2)} \Rightarrow ${m(weeks, 0)}§"""),
                background = l("Viele Textaufgaben haben zwei Schritte: eine Zwischengröße ausrechnen und mit ihr weiterarbeiten. Wer beides in einem Schritt versucht, verliert leicht den Überblick. Ein Anteil wie „ein Viertel“ bedeutet: durch 4 teilen. Hilfreich ist immer die Probe: ${m(weeks, 0)} Wochen mal ${n(save, 2)} € reichen für das Spiel.", "Many word problems have two steps: work out an intermediate quantity and then carry on with it. If you try both in one go, you easily lose track. A fraction like 'a quarter' means: divide by 4. Checking always helps: ${m(weeks, 0)} weeks times €${n(save, 2)} is enough for the game."),
            )
        },
        lab("ma.pizzarest", Topic.Word, E, 0.4f) {
            val (pizzas, friends) = pick(3 to 5, 3 to 7, 4 to 5, 4 to 6, 5 to 6, 5 to 7); val slices = 8
            val total = pizzas * slices; val each = total / friends; val rest = total % friends
            task(
                question = l("${pizzas} Pizzen werden in je ${slices} Stücke geschnitten und gerecht unter ${friends} Freunden verteilt, jeder bekommt gleich viele ganze Stücke. Wie viele Stücke bleiben übrig?", "${pizzas} pizzas are each cut into ${slices} slices and shared fairly among ${friends} friends; everyone gets the same number of whole slices. How many slices are left over?"),
                steps = listOf(num(l("Übrige Stücke", "Slices left"), rest.toDouble(), "", abs = 0.0, done = """§${rest}§""",
                    hints = listOf(l("Kernidee: Teilen mit Rest. Wie viele Stücke sind es insgesamt?", "Key idea: division with remainder. How many slices are there in total?"), """§${total} : ${friends}§"""))),
                solution = listOf("""§${total} = ${friends} \cdot ${each} + ${rest}§""", l("Jeder bekommt ${each} Stücke, ${rest} bleiben übrig.", "Each gets ${each} slices, ${rest} are left over.")),
                background = l("Division mit Rest ist die ursprünglichste Form des Teilens: Dinge, die man nicht zerschneiden will, auf Gruppen verteilen. Mathematiker schreiben das als a = q · b + r. Der Rest ist auch die Grundlage der Uhrzeit-Rechnung und vieler Verschlüsselungen: Man rechnet nur mit dem, was nach dem Teilen übrig bleibt.", "Division with remainder is the oldest form of sharing: dividing up things you don't want to cut into groups. Mathematicians write it as a = q · b + r. The remainder is also the basis of clock arithmetic and many kinds of encryption: you only work with what's left over after dividing."),
            )
        },
        lab("ma.stundenlohn", Topic.Word, E, 0.3f) {
            val h = pick(6, 4, 8); val d = pick(5, 3, 4); val wage = pick(13.5, 12.82, 15.0)
            val pay = h * d * wage
            task(
                question = l("In den Ferien jobbst du ${d} Tage pro Woche je ${h} Stunden für ${n(wage, 2)} € pro Stunde. Wie viel verdienst du in einer Woche?", "In the holidays you work ${d} days a week, ${h} hours a day, for €${n(wage, 2)} an hour. How much do you earn in a week?"),
                steps = listOf(num(l("Wochenlohn in Euro", "Weekly pay in euros"), pay, "€", abs = 0.01, done = """§${m(pay, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Erst die Stunden pro Woche, dann mal den Stundenlohn.", "Key idea: first the hours per week, then times the hourly wage."), """§${h} \cdot ${d} = ${h * d}\ \text{h}§"""))),
                solution = listOf("""§${h * d}\ \text{h} \cdot ${m(wage, 2)}\ \tfrac{\text{€}}{\text{h}} = ${m(pay, 2)}\ \text{€}§"""),
                background = l("Hier hilft das Rechnen mit Einheiten: Stunden pro Tag mal Tage ergibt Stunden, Stunden mal Euro pro Stunde ergibt Euro. Passen die Einheiten am Ende, stimmt meistens auch die Rechnung. Ingenieure nutzen diese Kontrolle ständig, man nennt sie Dimensionsanalyse.", "Calculating with units helps here: hours per day times days gives hours, hours times euros per hour gives euros. If the units come out right at the end, the calculation is usually right too. Engineers use this check all the time; it's called dimensional analysis."),
            )
        },
        lab("ma.broetchen", Topic.Word, E, 0.4f) {
            val k = pick(3, 4, 2); val total = pick(240, 200, 300).let { it - it % (k + 1) }
            val mon = total / (k + 1)
            task(
                question = l("Eine Bäckerei verkauft am Samstag ${k}-mal so viele Brötchen wie am Montag. An beiden Tagen zusammen sind es ${total}. Wie viele waren es am Montag?", "A bakery sells ${k} times as many rolls on Saturday as on Monday. On the two days together it's ${total}. How many were sold on Monday?"),
                steps = listOf(num(l("Brötchen am Montag", "Rolls on Monday"), mon.toDouble(), "", abs = 0.0, done = """§${mon}§""",
                    hints = listOf(l("Kernidee: Nenne die Montagszahl x. Dann ist Samstag ${k}x, zusammen ${k + 1}x.", "Key idea: call Monday's number x. Then Saturday is ${k}x, together ${k + 1}x."), """§x + ${k}x = ${total}§"""))),
                solution = listOf("""§${k + 1}x = ${total} \Rightarrow x = ${mon}§""", l("Am Samstag waren es ${k * mon}.", "On Saturday it was ${k * mon}.")),
                background = l("Wer bei solchen Aufgaben spontan ${total} durch ${k} teilt, liegt falsch: Die Gesamtzahl besteht aus ${k + 1} gleichen Teilen. Ein Balkenmodell macht es sichtbar: ein Kästchen für Montag, ${k} gleiche Kästchen für Samstag. So lösten schon Kinder in Singapur solche Aufgaben, lange bevor sie Gleichungen lernten.", "Anyone who spontaneously divides ${total} by ${k} in such problems gets it wrong: the total consists of ${k + 1} equal parts. A bar model makes it visible: one box for Monday, ${k} equal boxes for Saturday. Children in Singapore solve such problems this way long before they learn equations."),
            )
        },
        lab("ma.lauftraining", Topic.Word, E, 0.4f) {
            val start = pick(2.0, 3.0, 1.5); val inc = pick(0.5, 0.25); val week = pick(10, 12, 8)
            val v = start + (week - 1) * inc
            task(
                question = l("Du fängst mit Laufen an: In der ersten Woche läufst du ${n(start, 1)} km am Stück, und jede Woche ${n(inc, 2)} km mehr. Wie weit läufst du in der ${week}. Woche?", "You're starting to run: in the first week you run ${n(start, 1)} km without stopping, and every week ${n(inc, 2)} km more. How far do you run in week ${week}?"),
                steps = listOf(num(l("Strecke in km", "Distance in km"), v, "km", abs = 0.01, done = """§${m(v, 2)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Von Woche 1 bis Woche ${week} sind es ${week - 1} Steigerungen, nicht ${week}.", "Key idea: from week 1 to week ${week} there are ${week - 1} increases, not ${week}."), """§${m(start, 1)} + ${week - 1} \cdot ${m(inc, 2)}§"""))),
                solution = listOf("""§${m(start, 1)} + ${week - 1} \cdot ${m(inc, 2)} = ${m(v, 2)}\ \text{km}§"""),
                background = l("Eine gleichmäßige Steigerung ist eine arithmetische Folge. Lauftrainer empfehlen übrigens, den Umfang pro Woche nicht um mehr als etwa 10 % zu steigern, damit Sehnen und Knochen mitkommen. Muskeln passen sich schneller an als das Bindegewebe, und das führt bei zu ehrgeizigen Plänen oft zu Verletzungen.", "A steady increase is an arithmetic sequence. Running coaches recommend, by the way, not increasing the weekly distance by more than about 10%, so that tendons and bones can keep up. Muscles adapt faster than connective tissue, which often leads to injuries with plans that are too ambitious."),
            )
        },
        lab("ma.kinosaal", Topic.Word, E, 0.3f) {
            val rows = pick(18, 15, 20); val seats = pick(24, 20, 16); val (fz, fn) = pick(3 to 4, 2 to 3, 5 to 6)
            val people = rows * seats * fz / fn
            task(
                question = l("Ein Kinosaal hat ${rows} Reihen mit je ${seats} Plätzen. Heute Abend sind ${fz}/${fn} der Plätze besetzt. Wie viele Leute sind im Kino?", "A cinema has ${rows} rows of ${seats} seats each. Tonight ${fz}/${fn} of the seats are taken. How many people are in the cinema?"),
                steps = listOf(num(l("Zuschauer", "Viewers"), people.toDouble(), "", abs = 0.0, done = """§${people}§""",
                    hints = listOf(l("Kernidee: Erst alle Plätze ausrechnen, dann den Bruchteil davon nehmen.", "Key idea: first work out all the seats, then take the fraction of that."), """§${rows} \cdot ${seats} = ${rows * seats}§"""))),
                solution = listOf("""§${rows * seats} \cdot \dfrac{${fz}}{${fn}} = ${people}§"""),
                background = l("Ein Bruchteil von einer Menge heißt: durch den Nenner teilen, mit dem Zähler malnehmen. Die Reihenfolge ist egal, aber erst zu teilen hält die Zahlen klein. Kinos und Fluggesellschaften verkaufen übrigens gezielt mehr Karten, als Plätze da sind, weil immer ein Teil nicht kommt. Das nennt man Überbuchung.", "A fraction of an amount means: divide by the denominator and multiply by the numerator. The order doesn't matter, but dividing first keeps the numbers small. Airlines in particular deliberately sell more tickets than there are seats, because some people always don't turn up. That's called overbooking."),
            )
        },
        lab("ma.klassenfahrt", Topic.Word, E, 0.4f) {
            val bus = pick(850, 720, 960); val kids = pick(28, 25, 30)
            val each = bus.toDouble() / kids
            task(
                question = l("Der Bus für die Klassenfahrt kostet ${bus} €. Die Kosten teilen sich die ${kids} Schülerinnen und Schüler, die Lehrkräfte fahren umsonst. Wie viel zahlt jedes Kind (auf den Cent gerundet)?", "The bus for the class trip costs €${bus}. The cost is shared by the ${kids} students; the teachers travel free. How much does each child pay (rounded to the cent)?"),
                steps = listOf(num(l("Betrag in Euro", "Amount in euros"), each, "€", abs = 0.01, done = """§${m(each, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Gesamtkosten durch die Zahl der Zahlenden.", "Key idea: total cost divided by the number of people paying."), """§\dfrac{${bus}}{${kids}}§"""))),
                solution = listOf("""§\dfrac{${bus}}{${kids}} \approx ${m(each, 2)}\ \text{€}§"""),
                background = l("Beim Runden auf Cent bleibt oft ein kleiner Rest. In der Praxis rundet man auf, damit am Ende genug Geld da ist. Wer genau rechnet, sieht: ${kids} mal ${n(each, 2)} € ergibt nicht ganz exakt ${bus} €, der Unterschied sind ein paar Cent. Solche Rundungsreste sammeln sich in großen Systemen, etwa bei Banken, zu echten Beträgen.", "When rounding to the cent there's often a small remainder. In practice you round up so that there's enough money in the end. If you check carefully: ${kids} times €${n(each, 2)} doesn't quite give exactly €${bus}; the difference is a few cents. In large systems, such as banks, these rounding remainders add up to real amounts."),
            )
        },
        lab("ma.zugverfolgung", Topic.Word, M, 0.5f) {
            val va = pick(80, 60); val vb = pick(120, 100); val dt = pick(1, 2)
            val tCatch = (va * dt).toDouble() / (vb - va); val clock = 9 + dt + tCatch
            task(
                question = l("Zug A verlässt um 9:00 Uhr den Bahnhof mit ${va} km/h. Um ${9 + dt}:00 Uhr folgt Zug B auf einem parallelen Gleis mit ${vb} km/h. Um wie viel Uhr holt B den Zug A ein?", "Train A leaves the station at 9:00 at ${va} km/h. At ${9 + dt}:00 train B follows on a parallel track at ${vb} km/h. At what time does B catch up with A?"),
                steps = listOf(num(l("Uhrzeit (Stunden, z. B. 12,5 für 12:30)", "Time of day (hours, e.g. 12.5 for 12:30)"), clock, "h", abs = 0.02, done = """§${m(clock, 2)}\ \text{Uhr}§""".let { l(it, it.replace("""\ \text{Uhr}""", "")) },
                    hints = listOf(l("Kernidee: Beim Start von B hat A schon einen Vorsprung. B holt mit dem Unterschied der Geschwindigkeiten auf.", "Key idea: when B starts, A already has a head start. B closes the gap at the difference of the speeds."), """§t = \dfrac{${va} \cdot ${dt}}{${vb} - ${va}}§"""))),
                solution = listOf("""§\text{Vorsprung} = ${va * dt}\ \text{km},\quad t = \dfrac{${va * dt}}{${vb - va}}\ \text{h} = ${m(tCatch, 2)}\ \text{h}§""".let { l(it, it.replace("Vorsprung", "head start")) }, l("B holt A um ${m(clock, 2)} Uhr ein.", "B catches A at ${m(clock, 2)} h.")),
                background = l("Verfolgungsaufgaben sind die Klassiker der Textaufgaben. Der Trick ist immer derselbe: nicht beide Bewegungen getrennt verfolgen, sondern nur den Abstand. Der schrumpft mit der Differenz der Geschwindigkeiten. Das gleiche Prinzip gilt für Überholmanöver, Raubtier und Beute oder Satelliten, die sich im All annähern.", "Chase problems are the classics among word problems. The trick is always the same: don't follow both motions separately, just the distance between them. It shrinks at the difference of the speeds. The same principle applies to overtaking, to predator and prey, or to satellites approaching each other in space."),
            )
        },
        lab("ma.altersraetsel", Topic.Word, M, 0.6f) {
            val a = pick(5, 4, 6); val b = pick(7, 8, 9)
            val t = a + 2 * b
            task(
                question = l("In ${a} Jahren wird Tom doppelt so alt sein, wie er vor ${b} Jahren war. Wie alt ist Tom heute?", "In ${a} years Tom will be twice as old as he was ${b} years ago. How old is Tom today?"),
                steps = listOf(num(l("Alter heute", "Age today"), t.toDouble(), "", abs = 0.0, done = """§${t}§""",
                    hints = listOf(l("Kernidee: Nenne das heutige Alter x. In ${a} Jahren ist er x + ${a}, vor ${b} Jahren war er x − ${b}.", "Key idea: call today's age x. In ${a} years he'll be x + ${a}; ${b} years ago he was x − ${b}."), """§x + ${a} = 2(x - ${b})§"""))),
                solution = listOf("""§x + ${a} = 2x - ${2 * b} \Rightarrow x = ${t}§""", l("Probe: In ${a} Jahren ist er ${t + a}, vor ${b} Jahren war er ${t - b}, und ${t + a} = 2 · ${t - b}.", "Check: in ${a} years he'll be ${t + a}; ${b} years ago he was ${t - b}, and ${t + a} = 2 · ${t - b}.")),
                background = l("Altersrätsel wirken verwirrend, weil sie verschiedene Zeitpunkte vermischen. Wenn man alles auf eine einzige Unbekannte zurückführt, das heutige Alter, werden sie zu einfachen Gleichungen. Solche Rätsel stehen schon in der Griechischen Anthologie aus der Antike, etwa die Grabinschrift des Mathematikers Diophant.", "Age puzzles seem confusing because they mix different points in time. If you reduce everything to a single unknown, today's age, they become simple equations. Such puzzles already appear in the Greek Anthology from antiquity, for example the epitaph of the mathematician Diophantus."),
            )
        },
        lab("ma.becken", Topic.Word, M, 0.5f) {
            val vol = pick(50.0, 40.0, 60.0); val inflow = pick(2.0, 2.5, 3.0); val leak = pick(0.5, 0.4)
            val t = vol / (inflow - leak)
            task(
                question = l("Ein Becken fasst ${n(vol, 0)} m³. Der Zulauf liefert ${n(inflow, 1)} m³ pro Stunde, aber durch ein Leck gehen ${n(leak, 1)} m³ pro Stunde verloren. Nach wie vielen Stunden ist das Becken voll?", "A pool holds ${n(vol, 0)} m³. The inlet delivers ${n(inflow, 1)} m³ per hour, but ${n(leak, 1)} m³ per hour is lost through a leak. After how many hours is the pool full?"),
                steps = listOf(num(l("Zeit in Stunden", "Time in hours"), t, "h", rel = 0.01, done = """§t \approx ${m(t, 1)}\ \text{h}§""",
                    hints = listOf(l("Kernidee: Was zählt, ist der Nettozufluss: Zulauf minus Verlust.", "Key idea: what counts is the net inflow: inflow minus loss."), """§t = \dfrac{V}{${m(inflow, 1)} - ${m(leak, 1)}}§"""))),
                solution = listOf("""§t = \dfrac{${m(vol, 0)}}{${m(inflow - leak, 1)}}\ \text{h} \approx ${m(t, 1)}\ \text{h}§"""),
                background = l("Wenn gleichzeitig etwas hinein- und hinausfließt, rechnet man mit der Bilanz. Dasselbe Prinzip steckt hinter dem Kontostand, dem Kohlendioxid in der Atmosphäre oder dem Gewicht des Körpers: Entscheidend ist nicht, wie viel hineinkommt, sondern der Unterschied zwischen Zu- und Abfluss.", "When something flows in and out at the same time, you calculate with the balance. The same principle lies behind a bank balance, the carbon dioxide in the atmosphere or body weight: what matters isn't how much comes in but the difference between inflow and outflow."),
            )
        },
        lab("ma.fahrgemeinschaft", Topic.Word, M, 0.5f) {
            val dist = pick(300.0, 450.0, 240.0); val cons = pick(7.0, 6.0, 5.5); val price = pick(1.80, 1.75, 1.90); val people = pick(4, 3, 5)
            val each = dist / 100 * cons * price / people
            task(
                question = l("${people} Freunde fahren zusammen ${n(dist, 0)} km weit. Das Auto braucht ${n(cons, 1)} l auf 100 km, Benzin kostet ${n(price, 2)} € pro Liter. Wie viel zahlt jeder, wenn sie die Spritkosten gleich teilen?", "${people} friends drive ${n(dist, 0)} km together. The car uses ${n(cons, 1)} l per 100 km, and petrol costs €${n(price, 2)} a litre. How much does each pay if they share the fuel cost equally?"),
                steps = listOf(num(l("Betrag pro Person in Euro", "Amount per person in euros"), each, "€", abs = 0.01, done = """§${m(each, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Drei Schritte: Liter, Kosten, durch die Zahl der Leute.", "Key idea: three steps: litres, cost, divided by the number of people."), """§\dfrac{${m(dist, 0)}}{100} \cdot ${m(cons, 1)}\ \text{l} = ${m(dist / 100 * cons, 1)}\ \text{l}§"""))),
                solution = listOf("""§${m(dist / 100 * cons, 1)}\ \text{l} \cdot ${m(price, 2)}\ \tfrac{\text{€}}{\text{l}} = ${m(dist / 100 * cons * price, 2)}\ \text{€}§""", """§\dfrac{${m(dist / 100 * cons * price, 2)}}{${people}} \approx ${m(each, 2)}\ \text{€}§"""),
                background = l("Längere Rechenketten bleiben übersichtlich, wenn man jeden Zwischenschritt mit Einheit aufschreibt. Rundet man zu früh, schleppen sich Fehler durch die Rechnung. Deshalb rechnet man mit ungerundeten Zwischenergebnissen weiter und rundet erst am Ende.", "Longer chains of calculation stay clear if you write down every intermediate step with its unit. If you round too early, errors get carried along through the calculation. That's why you keep working with unrounded intermediate results and only round at the end."),
            )
        },
        lab("ma.tickets", Topic.Word, M, 0.5f) {
            val total = pick(120, 60, 240); val (az, an) = pick(3 to 5, 2 to 3, 3 to 4); val pa = pick(25, 18, 30); val pk = pick(12, 9, 15)
            val adults = total * az / an; val kids = total - adults; val sum = adults * pa + kids * pk
            task(
                question = l("Für ein Konzert wurden ${total} Karten verkauft. ${az}/${an} davon gingen an Erwachsene zu je ${pa} €, der Rest an Kinder zu je ${pk} €. Wie viel wurde insgesamt eingenommen?", "${total} tickets were sold for a concert. ${az}/${an} of them went to adults at €${pa} each, the rest to children at €${pk} each. How much was taken in total?"),
                steps = listOf(num(l("Einnahmen in Euro", "Takings in euros"), sum.toDouble(), "€", abs = 0.0, done = """§${sum}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Erst ausrechnen, wie viele Erwachsene und wie viele Kinder es sind.", "Key idea: first work out how many adults and how many children there are."), """§\text{Erwachsene} = ${total} \cdot \dfrac{${az}}{${an}} = ${adults}§""".let { l(it, it.replace("Erwachsene", "adults")) }))),
                solution = listOf("""§${adults} \cdot ${pa} + ${kids} \cdot ${pk} = ${adults * pa} + ${kids * pk} = ${sum}\ \text{€}§"""),
                background = l("„Der Rest“ ist ein kleines Wort, das viel Rechnung versteckt: Wenn ${az}/${an} Erwachsene sind, sind ${an - az}/${an} Kinder. Bei solchen Aufgaben lohnt es sich, die Gruppen erst getrennt aufzuschreiben. Am Ende kann man prüfen, ob die Anzahlen zusammen wieder ${total} ergeben.", "'The rest' is a small word that hides a lot of calculation: if ${az}/${an} are adults, ${an - az}/${an} are children. In such problems it pays to write out the groups separately first. At the end you can check that the numbers add up to ${total} again."),
            )
        },
        lab("ma.fliesen", Topic.Word, M, 0.5f) {
            val (a, b) = pick(4.5 to 3.0, 3.6 to 2.4, 6.0 to 4.2)
            fun fits(x: Double, t: Double) = kotlin.math.abs(x / t - kotlin.math.round(x / t)) < 1e-9
            val tile = if (fits(a, 0.6) && fits(b, 0.6)) pick(0.3, 0.6) else 0.3
            val na = kotlin.math.round(a / tile).toInt(); val nb = kotlin.math.round(b / tile).toInt(); val count = na * nb
            task(
                question = l("Ein Raum ist ${n(a, 1)} m lang und ${n(b, 1)} m breit. Er soll mit quadratischen Fliesen von ${n(tile * 100, 0)} cm Kantenlänge ausgelegt werden. Wie viele Fliesen braucht man (ohne Verschnitt)?", "A room is ${n(a, 1)} m long and ${n(b, 1)} m wide. It is to be covered with square tiles with ${n(tile * 100, 0)} cm sides. How many tiles are needed (without waste)?"),
                steps = listOf(num(l("Anzahl Fliesen", "Number of tiles"), count.toDouble(), "", abs = 0.0, done = """§${count}§""",
                    hints = listOf(l("Kernidee: Wie viele Fliesen passen in eine Reihe entlang der Länge, wie viele entlang der Breite? Achte auf die Einheiten.", "Key idea: how many tiles fit in a row along the length, how many along the width? Watch the units."), """§\dfrac{${m(a * 100, 0)}}{${m(tile * 100, 0)}} \cdot \dfrac{${m(b * 100, 0)}}{${m(tile * 100, 0)}}§"""))),
                solution = listOf("""§${na} \cdot ${nb} = ${count}§"""),
                background = l("Man kann über die Fläche rechnen oder über die Reihen. Über die Reihen sieht man sofort, ob die Fliesen aufgehen oder ob am Rand geschnitten werden muss. Im echten Leben kauft man etwa 10 % mehr, für Verschnitt und Bruch, und eine Reserve für spätere Reparaturen.", "You can calculate via the area or via the rows. Via the rows you can see at once whether the tiles fit exactly or whether some must be cut at the edges. In real life you buy about 10% extra for cutting and breakage, plus a reserve for later repairs."),
            )
        },
        lab("ma.fahrplan", Topic.Word, M, 0.6f) {
            val first = pick(5, 10, 7); val step = pick(20, 15, 30); val endH = 9
            val count = (0 until 1000).count { 6 * 60 + first + it * step < endH * 60 }
            task(
                question = l("Ab 6:${first.toString().padStart(2, '0')} Uhr fährt alle ${step} Minuten eine S-Bahn. Wie viele Bahnen fahren vor 9:00 Uhr ab?", "From 6:${first.toString().padStart(2, '0')} a suburban train leaves every ${step} minutes. How many trains leave before 9:00?"),
                steps = listOf(num(l("Anzahl Bahnen", "Number of trains"), count.toDouble(), "", abs = 0.0, done = """§${count}§""",
                    hints = listOf(l("Kernidee: Zähle Abfahrten, nicht Abstände. Die erste Bahn um 6:${first.toString().padStart(2, '0')} zählt mit.", "Key idea: count departures, not intervals. The first train at 6:${first.toString().padStart(2, '0')} counts too."), l("Welche ist die letzte Abfahrt vor 9:00?", "Which is the last departure before 9:00?"))),
                    ),
                solution = listOf(l("Die Abfahrten liegen ${step} Minuten auseinander, von 6:${first.toString().padStart(2, '0')} bis zur letzten vor 9:00. Das sind ${count} Bahnen.", "The departures are ${step} minutes apart, from 6:${first.toString().padStart(2, '0')} up to the last one before 9:00. That's ${count} trains.")),
                background = l("Das ist wieder das Zaunpfahl-Problem: Zwischen n Abfahrten liegen n − 1 Abstände. Wer einfach die Zeitspanne durch den Takt teilt, zählt eine Bahn zu wenig oder zu viel. Ein sicherer Weg ist, die ersten und die letzten Abfahrten aufzuschreiben und dann genau zu zählen.", "This is the fence-post problem again: between n departures there are n − 1 intervals. If you simply divide the time span by the interval, you count one train too few or too many. A safe way is to write down the first and last departures and then count carefully."),
            )
        },
        lab("ma.gewinnteilung", Topic.Word, M, 0.5f) {
            val (a, b, c) = pick(Triple(2000, 3000, 5000), Triple(1000, 1500, 2500), Triple(4000, 4000, 2000)); val profit = pick(1500, 1200, 3000)
            val share = profit.toDouble() * b / (a + b + c)
            task(
                question = l("Drei Freunde gründen ein kleines Geschäft. Sie zahlen ${a} €, ${b} € und ${c} € ein. Nach einem Jahr gibt es ${profit} € Gewinn, verteilt nach den Einlagen. Wie viel bekommt der zweite?", "Three friends start a small business. They put in €${a}, €${b} and €${c}. After a year there is €${profit} profit, shared according to what each put in. How much does the second get?"),
                steps = listOf(num(l("Anteil in Euro", "Share in euros"), share, "€", abs = 0.01, done = """§${m(share, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Der Gewinn wird im selben Verhältnis geteilt wie die Einlagen.", "Key idea: the profit is split in the same ratio as the money put in."), """§\dfrac{${b}}{${a + b + c}} \cdot ${profit}§"""))),
                solution = listOf("""§\dfrac{${b}}{${a + b + c}} \cdot ${profit} = ${m(share, 2)}\ \text{€}§"""),
                background = l("Proportionale Aufteilung ist gerecht, wenn jeder im selben Verhältnis beiträgt. Sie begegnet einem bei Erbschaften, Gewinnen und Kosten in Wohngemeinschaften. Ob sie wirklich fair ist, ist eine andere Frage: Hat einer mehr Arbeit hineingesteckt, wollen die anderen vielleicht anders teilen.", "Proportional sharing is fair when everyone contributes in the same ratio. You meet it in inheritances, profits and costs in shared flats. Whether it's truly fair is another question: if one person has put in more work, the others might want to share differently."),
            )
        },
        lab("ma.wandern", Topic.Word, M, 0.5f) {
            val up = pick(300.0, 400.0); val down = pick(500.0, 600.0); val h = pick(900.0, 1200.0, 600.0)
            val t = h / up + h / down
            task(
                question = l("Beim Wandern schaffst du bergauf ${n(up, 0)} Höhenmeter pro Stunde, bergab ${n(down, 0)}. Wie lange brauchst du für eine Tour auf einen Gipfel ${n(h, 0)} m über dem Start und wieder zurück?", "When hiking you manage ${n(up, 0)} m of ascent per hour and ${n(down, 0)} m of descent. How long does a tour take to a summit ${n(h, 0)} m above the start and back again?"),
                steps = listOf(num(l("Gesamtzeit in Stunden", "Total time in hours"), t, "h", rel = 0.01, done = """§t \approx ${m(t, 1)}\ \text{h}§""",
                    hints = listOf(l("Kernidee: Auf- und Abstieg getrennt rechnen und dann addieren.", "Key idea: calculate the ascent and descent separately and then add."), """§t = \dfrac{${m(h, 0)}}{${m(up, 0)}} + \dfrac{${m(h, 0)}}{${m(down, 0)}}§"""))),
                solution = listOf("""§t = ${m(h / up, 2)}\ \text{h} + ${m(h / down, 2)}\ \text{h} = ${m(t, 1)}\ \text{h}§"""),
                background = l("Wanderführer rechnen ähnlich: Etwa 300 Höhenmeter pro Stunde bergauf und 500 bergab, plus Zeit für die waagrechte Strecke. Bergab ist man zwar schneller, aber für Knie und Muskeln oft anstrengender, weil man jeden Schritt abbremsen muss. Viele Bergunfälle passieren deshalb im Abstieg.", "Hiking guides calculate in a similar way: about 300 m of ascent per hour and 500 of descent, plus time for the horizontal distance. Going down is faster, but often harder on knees and muscles, because you have to brake every step. That's why many mountain accidents happen on the descent."),
            )
        },
        lab("ma.kuchenverkauf", Topic.Word, M, 0.5f) {
            val cakes = pick(12, 10, 15); val pieces = pick(16, 12); val price = pick(1.5, 1.2, 2.0); val pct = pick(85, 75, 90)
            val money = cakes * pieces * price * pct / 100
            task(
                question = l("Für das Schulfest werden ${cakes} Kuchen gebacken und in je ${pieces} Stücke geschnitten. Ein Stück kostet ${n(price, 2)} €. Am Ende sind ${pct} % verkauft. Wie viel wurde eingenommen?", "${cakes} cakes are baked for the school fair and each cut into ${pieces} pieces. A piece costs €${n(price, 2)}. In the end ${pct}% have been sold. How much was taken?"),
                steps = listOf(num(l("Einnahmen in Euro", "Takings in euros"), money, "€", abs = 0.01, done = """§${m(money, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Erst alle Stücke, dann den verkauften Anteil, dann mal den Preis.", "Key idea: first all the pieces, then the share sold, then times the price."), """§${cakes} \cdot ${pieces} = ${cakes * pieces}\ \text{Stück}§""".let { l(it, it.replace("Stück", "pieces")) }))),
                solution = listOf("""§${cakes * pieces} \cdot ${m(pct / 100.0, 2)} \cdot ${m(price, 2)}\ \text{€} = ${m(money, 2)}\ \text{€}§"""),
                background = l("Bei verketteten Multiplikationen darf man die Reihenfolge frei wählen. Geschickt ist, was die Zahlen rund macht. Prozentsätze rechnet man am einfachsten als Dezimalzahl: ${pct} % ist ${n(pct / 100.0, 2)}. Wer den Anteil vergisst, kommt auf die Einnahmen bei vollem Verkauf, eine gute Obergrenze zur Kontrolle.", "In chains of multiplication you can choose the order freely. It's smart to choose whatever makes the numbers round. Percentages are easiest as decimals: ${pct}% is ${n(pct / 100.0, 2)}. If you forget the share, you get the takings for a complete sell-out, which is a good upper limit to check against."),
            )
        },
        lab("ma.kaffeemaschine", Topic.Word, M, 0.6f) {
            val pA = pick(50.0, 80.0); val cA = pick(0.35, 0.40); val pB = pick(400.0, 500.0); val cB = pick(0.12, 0.15)
            val be = (pB - pA) / (cA - cB)
            task(
                question = l("Eine Kapselmaschine kostet ${n(pA, 0)} €, jede Tasse ${n(cA * 100, 0)} Cent. Ein Vollautomat kostet ${n(pB, 0)} €, jede Tasse nur ${n(cB * 100, 0)} Cent. Ab wie vielen Tassen ist der Vollautomat insgesamt billiger?", "A capsule machine costs €${n(pA, 0)}, and each cup ${n(cA * 100, 0)} cents. A bean-to-cup machine costs €${n(pB, 0)}, each cup only ${n(cB * 100, 0)} cents. From how many cups is the bean-to-cup machine cheaper overall?"),
                steps = listOf(num(l("Tassen", "Cups"), be, "", rel = 0.01, done = """§\approx ${m(be, 0)}§""",
                    hints = listOf(l("Kernidee: Setze die Gesamtkosten beider Maschinen gleich.", "Key idea: set the total costs of both machines equal."), """§${m(pA, 0)} + ${m(cA, 2)}\, x = ${m(pB, 0)} + ${m(cB, 2)}\, x§"""))),
                solution = listOf("""§x = \dfrac{${m(pB, 0)} - ${m(pA, 0)}}{${m(cA, 2)} - ${m(cB, 2)}} \approx ${m(be, 0)}§"""),
                background = l("Billig in der Anschaffung, teuer im Betrieb: Das Geschäftsmodell heißt Rasierklingen-Prinzip und steckt in Druckern, Kaffeekapseln und Spielkonsolen. Ob es sich lohnt, hängt vom Verbrauch ab. Bei ${n(be, 0)} Tassen und zwei Tassen am Tag ist der Punkt nach etwa ${n(be / 2 / 30, 0)} Monaten erreicht.", "Cheap to buy, expensive to run: the business model is called the razor-and-blades principle and is behind printers, coffee capsules and games consoles. Whether it pays off depends on how much you use. At ${n(be, 0)} cups and two cups a day, the point is reached after about ${n(be / 2 / 30, 0)} months."),
            )
        },
        lab("ma.flaschenpreis", Topic.Word, M, 0.5f) {
            val pack = pick(3.30, 2.99, 3.60); val single = pick(0.65, 0.79)
            val perL = pack / 9; val save = single - perL
            task(
                question = l("Ein Sechserpack Wasser mit je 1,5 l kostet ${n(pack, 2)} €. Eine einzelne 1-Liter-Flasche kostet ${n(single, 2)} €. Wie viel sparst du pro Liter mit dem Sechserpack?", "A six-pack of 1.5 l water bottles costs €${n(pack, 2)}. A single 1-litre bottle costs €${n(single, 2)}. How much do you save per litre with the six-pack?"),
                steps = listOf(num(l("Ersparnis pro Liter in Euro", "Saving per litre in euros"), save, "€", abs = 0.005, done = """§\approx ${m(save, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Vergleiche nur Literpreise. Wie viel Liter sind im Sechserpack?", "Key idea: compare only prices per litre. How many litres are in the six-pack?"), """§6 \cdot 1{,}5 = 9\ \text{l}§"""))),
                solution = listOf("""§\dfrac{${m(pack, 2)}}{9} \approx ${m(perL, 3)}\ \tfrac{\text{€}}{\text{l}}§""", """§${m(single, 2)} - ${m(perL, 3)} \approx ${m(save, 2)}\ \tfrac{\text{€}}{\text{l}}§"""),
                background = l("Packungen sind schwer zu vergleichen, wenn Mengen und Preise verschieden sind. Deshalb müssen Supermärkte den Grundpreis angeben, etwa pro Liter oder Kilogramm. Größere Packungen sind meistens günstiger, aber nicht immer, und manchmal ist eine Packung etwas kleiner geworden, während der Preis gleich blieb.", "Packs are hard to compare when amounts and prices differ. That's why supermarkets have to show the unit price, for example per litre or kilogram. Bigger packs are usually cheaper, but not always, and sometimes a pack has shrunk a little while the price stayed the same."),
            )
        },
        lab("ma.datenvolumen", Topic.Word, M, 0.5f) {
            val total = pick(10.0, 15.0, 20.0); val used = pick(6.5, 4.0, 11.0).coerceAtMost(total - 1); val rate = pick(1.5, 0.7, 3.0)
            val h = (total - used) / rate
            task(
                question = l("Dein Handyvertrag hat ${n(total, 0)} GB im Monat, ${n(used, 1)} GB hast du schon verbraucht. Ein Video in HD braucht etwa ${n(rate, 1)} GB pro Stunde. Wie viele Stunden kannst du noch schauen?", "Your phone plan has ${n(total, 0)} GB a month, and you've already used ${n(used, 1)} GB. An HD video uses about ${n(rate, 1)} GB per hour. How many more hours can you watch?"),
                steps = listOf(num(l("Stunden", "Hours"), h, "h", abs = 0.05, done = """§\approx ${m(h, 1)}\ \text{h}§""",
                    hints = listOf(l("Kernidee: Restvolumen durch Verbrauch pro Stunde.", "Key idea: remaining data divided by usage per hour."), """§\dfrac{${m(total, 0)} - ${m(used, 1)}}{${m(rate, 1)}}§"""))),
                solution = listOf("""§\dfrac{${m(total - used, 1)}\ \text{GB}}{${m(rate, 1)}\ \text{GB/h}} \approx ${m(h, 1)}\ \text{h}§"""),
                background = l("Datenmengen sind eine Rate mal eine Zeit, genau wie Strecken oder Energie. Streaming-Dienste passen die Bildqualität an die Verbindung an, deshalb schwankt der Verbrauch. Ein Gigabyte sind eine Milliarde Byte, etwa so viel wie 250 Lieder im MP3-Format oder eine halbe Stunde 4K-Video.", "Amounts of data are a rate times a time, just like distances or energy. Streaming services adapt the picture quality to the connection, so the usage varies. A gigabyte is a billion bytes, about as much as 250 songs in MP3 format or half an hour of 4K video."),
            )
        },
        lab("ma.notenschnitt", Topic.Word, M, 0.6f) {
            val k = pick(4, 3); val avg = pick(2.5, 2.75, 3.0); val goal = avg - pick(0.2, 0.25, 0.3)
            val need = (k + 1) * goal - k * avg
            task(
                question = l("In ${k} Tests hast du im Schnitt eine ${n(avg, 2)}. Welche Note brauchst du im nächsten Test, damit dein Schnitt auf ${n(goal, 2)} sinkt?", "In ${k} tests your average is ${n(avg, 2)}. What grade do you need in the next test for your average to drop to ${n(goal, 2)}?"),
                steps = listOf(num(l("Benötigte Note", "Grade needed"), need, "", abs = 0.01, done = """§${m(need, 2)}§""",
                    hints = listOf(l("Kernidee: Rechne mit Summen statt Durchschnitten. Bisher hast du zusammen ${n(k * avg, 2)} Notenpunkte.", "Key idea: work with totals instead of averages. So far you have ${n(k * avg, 2)} grade points in total."), """§\dfrac{${m(k * avg, 2)} + x}{${k + 1}} = ${m(goal, 2)}§"""))),
                solution = listOf("""§x = ${k + 1} \cdot ${m(goal, 2)} - ${m(k * avg, 2)} = ${m(need, 2)}§"""),
                background = l("Ein Durchschnitt ist eine Summe geteilt durch eine Anzahl. Rückwärts wird daraus wieder eine Summe. Je mehr Noten es schon gibt, desto schwerer lässt sich der Schnitt bewegen: Eine einzelne neue Note zählt nur noch ein ${k + 1}tel. Deshalb lohnt es sich, gleich am Anfang gut zu sein.", "An average is a total divided by a count. Going backwards it becomes a total again. The more grades there already are, the harder the average is to move: a single new grade only counts for one ${k + 1}th. That's why it pays to do well right from the start."),
            )
        },
        lab("ma.rennenvorsprung", Topic.Word, H, 0.7f) {
            task(
                question = l("Bei einem 100-m-Lauf ist Anna im Ziel, als Ben erst 90 m geschafft hat. Beim Rückkampf startet Anna 10 m hinter der Startlinie, beide laufen gleich schnell wie beim ersten Mal. Wer gewinnt?", "In a 100 m race Anna finishes when Ben has only covered 90 m. In the rematch Anna starts 10 m behind the start line; both run exactly as fast as the first time. Who wins?"),
                steps = listOf(pick1(l("Ergebnis", "Result"), l("Anna gewinnt wieder", "Anna wins again"), listOf(l("Beide kommen gleichzeitig an", "Both arrive at the same time"), l("Ben gewinnt", "Ben wins"), l("Man kann es nicht sagen", "You can't tell")),
                    hints = listOf(l("Kernidee: Wo sind beide, wenn Anna 100 m gelaufen ist?", "Key idea: where are both runners when Anna has run 100 m?"), l("Dann sind beide genau 10 m vor dem Ziel, und Anna ist schneller.", "Then both are exactly 10 m from the finish, and Anna is faster.")))),
                solution = listOf(l("Nach 100 m von Anna hat Ben 90 m geschafft. Beide stehen dann an der 90-m-Marke. Auf den letzten 10 m ist Anna schneller, sie gewinnt knapp.", "After Anna's 100 m, Ben has covered 90 m. Both are then at the 90 m mark. Over the last 10 m Anna is faster, so she wins narrowly.")),
                background = l("Viele glauben, 10 m Rückstand gleichen 10 m Vorsprung genau aus. Aber Annas Vorsprung war ein Verhältnis der Geschwindigkeiten: Ben schafft 90 %, während Anna 100 % läuft. Auf 110 m bleibt dieses Verhältnis gleich. Statt gefühlsmäßig zu antworten, hilft es, einen besonderen Zeitpunkt zu betrachten.", "Many believe that a 10 m handicap exactly cancels a 10 m lead. But Anna's lead was a ratio of speeds: Ben manages 90% while Anna runs 100%. Over 110 m that ratio stays the same. Instead of answering by gut feeling, it helps to look at one special moment."),
            )
        },
        lab("ma.erntehelfer", Topic.Word, H, 0.6f) {
            val w = pick(5, 6, 4); val days = pick(12, 10, 15); val after = pick(4, 3, 5); val extra = pick(3, 2, 4)
            val remainingWork = w * (days - after); val rest = remainingWork.toDouble() / (w + extra)
            task(
                question = l("${w} Helfer würden eine Obstplantage in ${days} Tagen abernten. Nach ${after} Tagen kommen ${extra} weitere Helfer dazu, alle arbeiten gleich schnell. Wie viele Tage dauert die Ernte dann noch?", "${w} helpers would harvest an orchard in ${days} days. After ${after} days, ${extra} more helpers join; everyone works equally fast. How many more days does the harvest take?"),
                steps = listOf(num(l("Resttage", "Days remaining"), rest, l("Tage", "days"), abs = 0.01, done = """§${m(rest, 2)}\ \text{Tage}§""".let { l(it, it.replace("Tage", "days")) },
                    hints = listOf(l("Kernidee: Rechne in Helfertagen. Die ganze Arbeit sind ${w} · ${days} = ${w * days} Helfertage.", "Key idea: calculate in helper-days. The whole job is ${w} · ${days} = ${w * days} helper-days."), """§\text{Rest} = ${w} \cdot (${days} - ${after}) = ${remainingWork}§""".let { l(it, it.replace("Rest", "rest")) }))),
                solution = listOf("""§\dfrac{${remainingWork}\ \text{Helfertage}}{${w + extra}\ \text{Helfer}} = ${m(rest, 2)}\ \text{Tage}§""".let { l(it, it.replace("Helfertage", "helper-days").replace("Helfer", "helpers").replace("Tage", "days")) }),
                background = l("Arbeitsaufgaben werden übersichtlich, wenn man die Arbeit in einer Einheit misst, die nicht von der Zahl der Arbeitenden abhängt: Personentage oder Personenstunden. Dann ist die Rechnung nur noch Teilen. In echten Projekten stimmt das allerdings selten: Neue Leute müssen erst eingearbeitet werden, und zu viele stehen sich im Weg.", "Work problems become clear if you measure the work in a unit that doesn't depend on the number of workers: person-days or person-hours. Then the calculation is just division. In real projects that's rarely true, though: new people have to be trained first, and too many get in each other's way."),
            )
        },
    )

    private val calculus = listOf(
        lab("ma.extremum", Topic.Calculus, M, 0.5f) {
            val a = pick(1, 2, 3); val c = 3 * a * a
            task(
                question = l("""Die Funktion §f(x) = x^3 - ${c}x§ hat einen Hochpunkt. Welchen Funktionswert hat sie dort?""", """The function §f(x) = x^3 - ${c}x§ has a local maximum. What is the function's value there?"""),
                steps = listOf(num(l("Wert am Hochpunkt", "Value at the maximum"), 2.0 * a * a * a, "", abs = 0.0, done = """§f(-${a}) = ${2 * a * a * a}§""",
                    hints = listOf(l("Kernidee: An einem Hoch- oder Tiefpunkt ist die Steigung null. Setze f′(x) = 0.", "Key idea: at a maximum or minimum the slope is zero. Set f′(x) = 0."), """§f'(x) = 3x^2 - ${c} = 0 \Rightarrow x = \pm ${a}§"""))),
                solution = listOf("""§f'(x) = 3x^2 - ${c} = 0 \Rightarrow x = \pm ${a}§""", """§f''(-${a}) = -${6 * a} < 0 \Rightarrow \text{Hochpunkt bei } x = -${a}§""".let { l(it, it.replace("Hochpunkt bei", "maximum at")) }, """§f(-${a}) = -${a * a * a} + ${c * a} = ${2 * a * a * a}§"""),
                background = l("Auf einem Gipfel geht es weder bergauf noch bergab: Die Tangente liegt waagrecht. Deshalb sucht man Extremstellen, indem man die Ableitung null setzt. Ob es ein Hoch- oder Tiefpunkt ist, verrät die zweite Ableitung, also ob die Kurve dort nach unten oder nach oben gekrümmt ist.", "On a summit you're going neither uphill nor downhill: the tangent is horizontal. That's why you find extreme points by setting the derivative to zero. Whether it's a maximum or a minimum is revealed by the second derivative, i.e. whether the curve bends downwards or upwards there."),
            )
        },
        lab("ma.flaechezwischen", Topic.Calculus, M, 0.6f) {
            val a = pick(1, 2, 3); val area = a.toDouble().pow(3) / 6
            task(
                question = l("""Wie groß ist die Fläche zwischen der Geraden §y = ${if (a == 1) "" else "$a"}x§ und der Parabel §y = x^2§?""", """What is the area between the line §y = ${if (a == 1) "" else "$a"}x§ and the parabola §y = x^2§?"""),
                steps = listOf(num(l("Fläche", "Area"), area, "", abs = 0.002, done = """§A = ${m(area, 3)}§""",
                    hints = listOf(l("Kernidee: Erst die Schnittpunkte finden, dann das Integral der oberen minus der unteren Funktion.", "Key idea: first find the intersection points, then integrate the upper minus the lower function."), """§x^2 = ${a}x \Rightarrow x = 0,\ x = ${a}§"""))),
                solution = listOf("""§A = \int_0^{${a}} (${a}x - x^2)\, dx = \left[\tfrac{${a}}{2}x^2 - \tfrac{1}{3}x^3\right]_0^{${a}} = ${m(area, 3)}§"""),
                background = l("Ein Integral summiert unendlich viele dünne Streifen. Zwischen zwei Kurven ist jeder Streifen so hoch wie der Abstand der Kurven. Deshalb integriert man die Differenz. Archimedes fand diese Fläche schon vor über 2200 Jahren mit einer cleveren Dreieckskonstruktion, ohne Integralrechnung.", "An integral adds up infinitely many thin strips. Between two curves each strip is as tall as the gap between the curves. That's why you integrate the difference. Archimedes found this area over 2200 years ago with a clever triangle construction, without any integral calculus."),
            )
        },
        lab("ma.sinusflaeche", Topic.Calculus, M, 0.5f) {
            task(
                question = l("""Wie groß ist die Fläche unter einem Bogen der Sinuskurve, also §\displaystyle\int_0^{\pi} \sin x \, dx§?""", """What is the area under one arch of the sine curve, i.e. §\displaystyle\int_0^{\pi} \sin x \, dx§?"""),
                steps = listOf(num(l("Fläche", "Area"), 2.0, "", abs = 0.001, done = """§2§""",
                    hints = listOf(l("Kernidee: Eine Stammfunktion von sin x ist −cos x.", "Key idea: an antiderivative of sin x is −cos x."), """§\left[-\cos x\right]_0^{\pi}§"""))),
                solution = listOf("""§\left[-\cos x\right]_0^{\pi} = -\cos\pi + \cos 0 = 1 + 1 = 2§"""),
                background = l("Erstaunlich, dass eine so krumme Kurve eine so glatte Fläche einschließt. Der Hauptsatz der Analysis macht es möglich: Statt unendlich viele Streifen zu addieren, sucht man eine Stammfunktion und setzt nur die Grenzen ein. Über eine ganze Periode von 0 bis 2π wäre das Integral null, weil sich Berg und Tal aufheben.", "It's remarkable that such a curvy curve encloses such a neat area. The fundamental theorem of calculus makes it possible: instead of adding up infinitely many strips, you find an antiderivative and just plug in the limits. Over a whole period from 0 to 2π the integral would be zero, because the hill and the valley cancel."),
            )
        },
        lab("ma.sinxx", Topic.Calculus, M, 0.6f) {
            val x = pick(0.1, 0.01, 0.001); val v = kotlin.math.sin(x) / x
            task(
                question = l("""Rechne mit dem Taschenrechner (Bogenmaß) §\dfrac{\sin(${m(x, 3)})}{${m(x, 3)}}§ aus. Gegen welchen Wert strebt §\dfrac{\sin x}{x}§, wenn x gegen 0 geht?""", """Use the calculator (in radians) to work out §\dfrac{\sin(${m(x, 3)})}{${m(x, 3)}}§. What value does §\dfrac{\sin x}{x}§ tend to as x approaches 0?"""),
                steps = listOf(num(l("Grenzwert", "Limit"), 1.0, "", abs = 0.001, done = """§\lim_{x \to 0} \dfrac{\sin x}{x} = 1§""",
                    hints = listOf(l("Kernidee: Für kleine Winkel ist der Sinus fast genau so groß wie der Winkel selbst.", "Key idea: for small angles the sine is almost exactly as big as the angle itself."), """§\dfrac{\sin(${m(x, 3)})}{${m(x, 3)}} \approx ${m(v, 6)}§"""))),
                solution = listOf("""§\dfrac{\sin(${m(x, 3)})}{${m(x, 3)}} \approx ${m(v, 6)} \to 1§"""),
                background = l("Setzt man x = 0 ein, steht da 0/0, also gar nichts. Trotzdem hat der Ausdruck einen klaren Grenzwert. Geometrisch: Bei einem winzigen Winkel ist der Bogen auf dem Einheitskreis fast eine gerade Strecke, so lang wie die Sinus-Höhe. Dieser Grenzwert ist der Grund, warum die Ableitung von sin x gerade cos x ist, und warum man in der Physik mit Bogenmaß rechnet.", "If you substitute x = 0 you get 0/0, which means nothing at all. Yet the expression has a clear limit. Geometrically: for a tiny angle, the arc on the unit circle is almost a straight line, as long as the sine height. This limit is why the derivative of sin x is exactly cos x, and why physics works in radians."),
            )
        },
        lab("ma.grenzwertunendlich", Topic.Calculus, M, 0.5f) {
            val a = int(2, 7); val b = int(1, 9); val c = int(2, 9)
            task(
                question = l("""Gegen welchen Wert strebt §f(x) = \dfrac{${a}x^2 + ${b}}{x^2 - ${c}}§ für sehr große x?""", """What value does §f(x) = \dfrac{${a}x^2 + ${b}}{x^2 - ${c}}§ approach for very large x?"""),
                steps = listOf(num(l("Grenzwert", "Limit"), a.toDouble(), "", abs = 0.001, done = """§${a}§""",
                    hints = listOf(l("Kernidee: Für riesige x zählen nur die höchsten Potenzen, der Rest wird winzig dagegen.", "Key idea: for huge x only the highest powers count; the rest becomes tiny in comparison."), """§\dfrac{${a}x^2 + ${b}}{x^2 - ${c}} = \dfrac{${a} + ${b}/x^2}{1 - ${c}/x^2}§"""))),
                solution = listOf("""§\lim_{x \to \infty} \dfrac{${a} + ${b}/x^2}{1 - ${c}/x^2} = \dfrac{${a}}{1} = ${a}§"""),
                background = l("Im Unendlichen dominiert immer der stärkste Term. Haben Zähler und Nenner gleich hohe Potenzen, strebt der Bruch gegen das Verhältnis der Vorfaktoren. Die Gerade y = ${a} ist eine waagrechte Asymptote: Der Graph kommt ihr beliebig nah, ohne sie je ganz zu erreichen.", "At infinity the strongest term always dominates. If the numerator and denominator have equally high powers, the fraction tends to the ratio of their leading coefficients. The line y = ${a} is a horizontal asymptote: the graph gets arbitrarily close to it without ever quite reaching it."),
            )
        },
        lab("ma.linearisierung", Topic.Calculus, M, 0.5f) {
            val base = pick(4, 9, 16, 25); val d = pick(0.1, 0.2, 0.3, 0.5); val r = sqrt(base.toDouble())
            val approx = r + d / (2 * r); val exact = sqrt(base + d)
            task(
                question = l("""Schätze §\sqrt{${m(base + d, 1)}}§ mit der Tangente an §\sqrt{x}§ bei x = ${base}.""", """Estimate §\sqrt{${m(base + d, 1)}}§ using the tangent to §\sqrt{x}§ at x = ${base}."""),
                steps = listOf(num(l("Näherung", "Approximation"), approx, "", abs = 0.002, done = """§\approx ${m(approx, 4)}§""",
                    hints = listOf(l("Kernidee: In der Nähe eines Punktes ist eine Kurve fast ihre Tangente. Startwert plus Steigung mal kleine Änderung.", "Key idea: near a point, a curve is almost its tangent. Starting value plus slope times small change."), """§f(x_0 + h) \approx f(x_0) + f'(x_0) \cdot h,\quad f'(x) = \dfrac{1}{2\sqrt{x}}§"""))),
                solution = listOf("""§\sqrt{${m(base + d, 1)}} \approx ${m(r, 0)} + \dfrac{${m(d, 1)}}{2 \cdot ${m(r, 0)}} = ${m(approx, 4)}§""", l("Genau: ${m(exact, 5)}", "Exactly: ${m(exact, 5)}")),
                background = l("Das ist die Grundidee der Differentialrechnung, wie Silvanus Thompson sie in „Calculus Made Easy“ erklärt: Kleine Änderungen hängen fast linear zusammen. Für winzige h ist der Fehler so klein, dass man ihn vergessen kann. Physiker und Ingenieure nutzen solche Näherungen ständig, etwa für Pendel mit kleinen Ausschlägen.", "This is the basic idea of differential calculus, as Silvanus Thompson explains it in 'Calculus Made Easy': small changes are related almost linearly. For tiny h the error is so small that you can forget it. Physicists and engineers use such approximations all the time, for example for pendulums with small swings."),
            )
        },
        lab("ma.mittelwertintegral", Topic.Calculus, M, 0.6f) {
            val b = pick(3, 6, 2); val avg = b.toDouble() * b / 3
            task(
                question = l("""Welchen Durchschnittswert hat §f(x) = x^2§ auf dem Intervall von 0 bis ${b}?""", """What is the average value of §f(x) = x^2§ on the interval from 0 to ${b}?"""),
                steps = listOf(num(l("Durchschnittswert", "Average value"), avg, "", abs = 0.005, done = """§\bar{f} = ${m(avg, 2)}§""",
                    hints = listOf(l("Kernidee: Der Mittelwert einer Funktion ist ihre Fläche geteilt durch die Breite des Intervalls.", "Key idea: the average of a function is its area divided by the width of the interval."), """§\bar{f} = \dfrac{1}{b - a} \int_a^b f(x)\, dx§"""))),
                solution = listOf("""§\int_0^{${b}} x^2\, dx = \dfrac{${b}^3}{3} = ${m(b.toDouble().pow(3) / 3, 2)}§""", """§\bar{f} = \dfrac{${m(b.toDouble().pow(3) / 3, 2)}}{${b}} = ${m(avg, 2)}§"""),
                background = l("Stell dir vor, die Fläche unter der Kurve wäre Sand, den man glatt streicht. Er bildet dann ein Rechteck derselben Breite, und dessen Höhe ist der Mittelwert. Erstaunlich: Für x² auf [0, b] ist der Mittelwert genau ein Drittel des Höchstwerts, nicht die Hälfte, weil die Parabel lange flach bleibt.", "Imagine the area under the curve is sand that you smooth flat. It then forms a rectangle of the same width, and its height is the average. Remarkably, for x² on [0, b] the average is exactly a third of the maximum value, not half, because the parabola stays flat for a long time."),
            )
        },
        lab("ma.wachstumsrate", Topic.Calculus, M, 0.5f) {
            val a0 = pick(100, 200, 50); val k = pick(0.05, 0.1, 0.03); val t = pick(0, 10, 20)
            val rate = a0 * k * exp(k * t)
            task(
                question = l("""Eine Population wächst nach §N(t) = ${a0}\, e^{${m(k, 2)}\, t}§ (t in Jahren). Wie schnell wächst sie zum Zeitpunkt t = ${t}, in Tieren pro Jahr?""", """A population grows according to §N(t) = ${a0}\, e^{${m(k, 2)}\, t}§ (t in years). How fast is it growing at t = ${t}, in animals per year?"""),
                steps = listOf(num(l("Wachstumsrate pro Jahr", "Growth rate per year"), rate, "", rel = 0.01, done = """§N'(${t}) \approx ${m(rate, 2)}§""",
                    hints = listOf(l("Kernidee: Die Ableitung von e^(kt) ist k·e^(kt). Die Wachstumsrate ist also immer k mal der aktuelle Bestand.", "Key idea: the derivative of e^(kt) is k·e^(kt). So the growth rate is always k times the current population."), """§N'(t) = ${a0} \cdot ${m(k, 2)}\, e^{${m(k, 2)}\, t}§"""))),
                solution = listOf("""§N'(${t}) = ${m(a0 * k, 1)}\, e^{${m(k * t, 2)}} \approx ${m(rate, 2)}§"""),
                background = l("Die e-Funktion ist die einzige Funktion, die ihre eigene Ableitung ist. Deshalb beschreibt sie alles, was umso schneller wächst, je mehr schon da ist: Bakterien, Zinseszins, Kettenreaktionen. Die Konstante k sagt, welcher Anteil pro Zeiteinheit dazukommt, hier ${n(k * 100, 0)} % pro Jahr.", "The exponential function is the only function that is its own derivative. That's why it describes everything that grows faster the more there already is: bacteria, compound interest, chain reactions. The constant k says what fraction is added per unit of time, here ${n(k * 100, 0)}% per year."),
            )
        },
        lab("ma.lnableitung", Topic.Calculus, M, 0.6f) {
            val x0 = pick(1, 2, 3); val v = 2.0 * x0 / (x0 * x0 + 1)
            task(
                question = l("""Wie groß ist die Steigung von §f(x) = \ln(x^2 + 1)§ an der Stelle x = ${x0}?""", """What is the slope of §f(x) = \ln(x^2 + 1)§ at x = ${x0}?"""),
                steps = listOf(num(l("Steigung", "Slope"), v, "", abs = 0.002, done = """§f'(${x0}) = ${m(v, 3)}§""",
                    hints = listOf(l("Kernidee: Kettenregel. Die Ableitung von ln(u) ist u′/u.", "Key idea: the chain rule. The derivative of ln(u) is u′/u."), """§f'(x) = \dfrac{2x}{x^2 + 1}§"""))),
                solution = listOf("""§f'(${x0}) = \dfrac{2 \cdot ${x0}}{${x0 * x0} + 1} = ${m(v, 3)}§"""),
                background = l("Die Kettenregel sagt: Ändert sich das Innere, ändert sich das Äußere entsprechend mit. Man multipliziert die Ableitung der äußeren Funktion mit der der inneren. Für den Logarithmus ergibt das eine besonders hübsche Form: Er misst relative Änderungen, also Änderung geteilt durch aktuellen Wert.", "The chain rule says: if the inside changes, the outside changes along with it. You multiply the derivative of the outer function by that of the inner one. For the logarithm this gives a particularly neat form: it measures relative change, i.e. change divided by the current value."),
            )
        },
        lab("ma.wendepunkt", Topic.Calculus, H, 0.6f) {
            val a = pick(2, 1, 3); val b = pick(9, 5, 12)
            task(
                question = l("""Wo hat §f(x) = x^3 - ${3 * a}x^2 + ${b}x§ ihren Wendepunkt?""", """Where does §f(x) = x^3 - ${3 * a}x^2 + ${b}x§ have its point of inflection?"""),
                steps = listOf(num("x", a.toDouble(), "", abs = 0.0, done = """§x = ${a}§""",
                    hints = listOf(l("Kernidee: Am Wendepunkt wechselt die Kurve von einer Links- in eine Rechtskurve. Dort ist die zweite Ableitung null.", "Key idea: at the point of inflection the curve switches from bending one way to the other. The second derivative is zero there."), """§f''(x) = 6x - ${6 * a}§"""))),
                solution = listOf("""§f'(x) = 3x^2 - ${6 * a}x + ${b},\quad f''(x) = 6x - ${6 * a} = 0 \Rightarrow x = ${a}§"""),
                background = l("Stell dir vor, du fährst die Kurve mit dem Fahrrad entlang. Am Wendepunkt geht das Lenkrad von links nach rechts durch die Mitte. Die erste Ableitung sagt, wie steil es ist, die zweite, wie stark sich die Steilheit ändert. Jede Funktion dritten Grades hat genau einen Wendepunkt, und ihr Graph ist dort punktsymmetrisch.", "Imagine riding along the curve on a bike. At the point of inflection the handlebars pass through the centre from left to right. The first derivative says how steep it is, the second how fast the steepness changes. Every cubic function has exactly one point of inflection, and its graph is point-symmetric about it."),
            )
        },
        lab("ma.produktregel", Topic.Calculus, H, 0.6f) {
            val x0 = pick(0, 1, 2); val v = (1 + x0) * exp(x0.toDouble())
            task(
                question = l("""Wie groß ist die Steigung von §f(x) = x \cdot e^x§ an der Stelle x = ${x0}?""", """What is the slope of §f(x) = x \cdot e^x§ at x = ${x0}?"""),
                steps = listOf(num(l("Steigung", "Slope"), v, "", rel = 0.01, done = """§f'(${x0}) \approx ${m(v, 3)}§""",
                    hints = listOf(l("Kernidee: Produktregel: Ableitung des ersten mal zweiter Faktor plus erster Faktor mal Ableitung des zweiten.", "Key idea: product rule: derivative of the first times the second, plus the first times the derivative of the second."), """§(u v)' = u' v + u v'§"""))),
                solution = listOf("""§f'(x) = e^x + x e^x = (1 + x)\, e^x§""", """§f'(${x0}) = ${1 + x0}\, e^{${x0}} \approx ${m(v, 3)}§"""),
                background = l("Warum zwei Summanden? Stell dir ein Rechteck mit Seiten u und v vor, die beide ein wenig wachsen. Die Fläche wächst um einen Streifen u′·v an der einen Seite und einen Streifen u·v′ an der anderen. Das winzige Eckstück ist so klein, dass es nicht zählt. So erklärt es auch Thompson in seinem Buch.", "Why two terms? Imagine a rectangle with sides u and v, both growing a little. The area grows by a strip u′·v along one side and a strip u·v′ along the other. The tiny corner piece is so small it doesn't count. That's how Thompson explains it in his book, too."),
            )
        },
        lab("ma.rotationskoerper", Topic.Calculus, H, 0.6f) {
            val b = pick(4, 2, 9); val v = PI * b * b / 2
            task(
                question = l("""Die Kurve §y = \sqrt{x}§ zwischen x = 0 und x = ${b} dreht sich um die x-Achse und bildet einen Trichter, ein Rotationsparaboloid. Welches Volumen hat er?""", """The curve §y = \sqrt{x}§ between x = 0 and x = ${b} rotates about the x-axis and forms a funnel shape, a paraboloid. What is its volume?"""),
                steps = listOf(num(l("Volumen", "Volume"), v, "", rel = 0.01, done = """§V = ${m(b * b / 2.0, 1)}\pi \approx ${m(v, 2)}§""",
                    hints = listOf(l("Kernidee: Zerschneide den Körper in dünne Scheiben. Jede ist ein Kreis mit Radius y.", "Key idea: slice the solid into thin discs. Each is a circle with radius y."), """§V = \pi \int_0^{${b}} y^2\, dx = \pi \int_0^{${b}} x\, dx§"""))),
                solution = listOf("""§V = \pi \left[\tfrac{x^2}{2}\right]_0^{${b}} = \pi \cdot ${m(b * b / 2.0, 1)} \approx ${m(v, 2)}§"""),
                background = l("Integrale summieren nicht nur Flächen, sondern alles, was sich in dünne Scheiben schneiden lässt. Hier ist jede Scheibe ein Kreis mit Fläche π·y². Interessant: Das Paraboloid hat genau die Hälfte des Volumens des Zylinders, der es umschließt. Satellitenschüsseln und Scheinwerfer haben genau diese Form.", "Integrals don't only add up areas but anything that can be cut into thin slices. Here each slice is a circle with area π·y². Interestingly, the paraboloid has exactly half the volume of the cylinder that encloses it. Satellite dishes and headlight reflectors have exactly this shape."),
            )
        },
        lab("ma.newtonverfahren", Topic.Calculus, H, 0.6f) {
            val a = pick(2, 3, 5, 10); val x0 = if (a > 4) 3.0 else 1.0
            val x1 = x0 - (x0 * x0 - a) / (2 * x0); val x2 = x1 - (x1 * x1 - a) / (2 * x1)
            task(
                question = l("""Das Newton-Verfahren sucht Nullstellen: §x_{neu} = x - \dfrac{f(x)}{f'(x)}§. Wende es zweimal auf §f(x) = x^2 - ${a}§ an, beginnend bei §x_0 = ${m(x0, 0)}§. Welche Näherung für §\sqrt{${a}}§ erhältst du?""", """Newton's method finds zeros: §x_{new} = x - \dfrac{f(x)}{f'(x)}§. Apply it twice to §f(x) = x^2 - ${a}§, starting at §x_0 = ${m(x0, 0)}§. What approximation for §\sqrt{${a}}§ do you get?"""),
                steps = listOf(num(l("Zweite Näherung x₂", "Second approximation x₂"), x2, "", abs = 0.0005, done = """§x_2 \approx ${m(x2, 4)}§""",
                    hints = listOf(l("Kernidee: Folge der Tangente bis zur x-Achse. Mit f′(x) = 2x wird die Formel zu x_neu = (x + ${a}/x)/2.", "Key idea: follow the tangent down to the x-axis. With f′(x) = 2x the formula becomes x_new = (x + ${a}/x)/2."), """§x_1 = ${m(x0, 0)} - \dfrac{${m(x0 * x0 - a, 0)}}{${m(2 * x0, 0)}} = ${m(x1, 4)}§"""))),
                solution = listOf("""§x_1 = ${m(x1, 4)},\quad x_2 = x_1 - \dfrac{x_1^2 - ${a}}{2 x_1} \approx ${m(x2, 4)}§""", l("""Genau: §\sqrt{${a}} \approx ${m(sqrt(a.toDouble()), 5)}§""", """Exactly: §\sqrt{${a}} \approx ${m(sqrt(a.toDouble()), 5)}§""")),
                background = l("Das Newton-Verfahren ersetzt die Kurve durch ihre Tangente und schaut, wo die Tangente null wird. Das wiederholt man. In der Nähe der Lösung verdoppelt sich bei jedem Schritt etwa die Zahl der richtigen Stellen. Taschenrechner und Computer berechnen Wurzeln auf ganz ähnliche Weise.", "Newton's method replaces the curve with its tangent and looks where the tangent hits zero. You repeat this. Near the solution the number of correct digits roughly doubles with every step. Calculators and computers compute square roots in a very similar way."),
            )
        },
        lab("ma.taylor", Topic.Calculus, H, 0.6f) {
            val x = pick(1.0, 0.5, 2.0)
            val approx = 1 + x + x * x / 2 + x * x * x / 6 + x.pow(4) / 24
            task(
                question = l("""Die e-Funktion lässt sich als unendliche Summe schreiben: §e^x = 1 + x + \dfrac{x^2}{2!} + \dfrac{x^3}{3!} + \dots§. Welche Näherung für §e^{${m(x, 1)}}§ ergeben die ersten fünf Glieder (bis §x^4§)?""", """The exponential function can be written as an infinite sum: §e^x = 1 + x + \dfrac{x^2}{2!} + \dfrac{x^3}{3!} + \dots§. What approximation for §e^{${m(x, 1)}}§ do the first five terms (up to §x^4§) give?"""),
                steps = listOf(num(l("Näherung", "Approximation"), approx, "", abs = 0.0005, done = """§\approx ${m(approx, 4)}§""",
                    hints = listOf(l("Kernidee: Einfach einsetzen. 2! = 2, 3! = 6, 4! = 24.", "Key idea: just substitute. 2! = 2, 3! = 6, 4! = 24."), """§1 + ${m(x, 1)} + \dfrac{${m(x * x, 2)}}{2} + \dfrac{${m(x * x * x, 3)}}{6} + \dfrac{${m(x.pow(4), 4)}}{24}§"""))),
                solution = listOf("""§\approx ${m(approx, 4)}§""", l("""Genau: §e^{${m(x, 1)}} \approx ${m(exp(x), 5)}§""", """Exactly: §e^{${m(x, 1)}} \approx ${m(exp(x), 5)}§""")),
                background = l("Eine Taylorreihe baut eine Funktion aus ihren Ableitungen an einer Stelle zusammen: Wert, Steigung, Krümmung und so weiter. Bei e^x sind alle Ableitungen an der Stelle 0 gleich 1, deshalb ist die Reihe so einfach. Weil die Fakultäten so schnell wachsen, werden die Glieder rasch winzig, und schon wenige reichen.", "A Taylor series builds a function from its derivatives at one point: value, slope, curvature and so on. For e^x all the derivatives at 0 are equal to 1, which is why the series is so simple. Because the factorials grow so fast, the terms quickly become tiny, and just a few are enough."),
            )
        },
        lab("ma.uneigentlich", Topic.Calculus, H, 0.6f) {
            val p = pick(2, 3, 4); val v = 1.0 / (p - 1)
            task(
                question = l("""Die Fläche unter §y = \dfrac{1}{x^{${p}}}§ von x = 1 bis ins Unendliche: Ist sie unendlich groß oder endlich? Berechne §\displaystyle\int_1^{\infty} \dfrac{1}{x^{${p}}}\, dx§.""", """The area under §y = \dfrac{1}{x^{${p}}}§ from x = 1 to infinity: is it infinite or finite? Calculate §\displaystyle\int_1^{\infty} \dfrac{1}{x^{${p}}}\, dx§."""),
                steps = listOf(num(l("Wert des Integrals", "Value of the integral"), v, "", abs = 0.002, done = """§${m(v, 3)}§""",
                    hints = listOf(l("Kernidee: Integriere bis zu einer Grenze b und lass b dann gegen unendlich gehen.", "Key idea: integrate up to a limit b and then let b go to infinity."), """§\int_1^{b} x^{-${p}}\, dx = \left[\dfrac{x^{-${p - 1}}}{-${p - 1}}\right]_1^{b}§"""))),
                solution = listOf("""§\lim_{b \to \infty} \dfrac{1}{${p - 1}}\left(1 - \dfrac{1}{b^{${p - 1}}}\right) = \dfrac{1}{${p - 1}}§"""),
                background = l("Eine Fläche, die sich unendlich weit erstreckt, kann trotzdem endlich sein, wenn die Kurve schnell genug abfällt. Bei 1/x reicht das gerade nicht: Diese Fläche ist unendlich. Ein berühmtes Paradox ist Gabriels Horn: Ein Trichter mit endlichem Volumen, aber unendlicher Oberfläche. Man könnte ihn füllen, aber nicht anstreichen.", "An area that stretches out infinitely far can still be finite if the curve falls fast enough. For 1/x it just isn't enough: that area is infinite. A famous paradox is Gabriel's Horn: a funnel with finite volume but infinite surface area. You could fill it with paint but never paint its surface."),
            )
        },
        lab("ma.dosenoptimum", Topic.Calculus, X, 0.7f) {
            val v = pick(1000.0, 330.0, 500.0); val r = (v / (2 * PI)).pow(1.0 / 3.0)
            task(
                question = l("Eine zylindrische Dose soll ${n(v, 0)} cm³ fassen und dabei möglichst wenig Blech brauchen (Deckel und Boden zählen mit). Welchen Radius muss sie haben?", "A cylindrical tin is to hold ${n(v, 0)} cm³ while using as little metal as possible (lid and base count too). What radius must it have?"),
                steps = listOf(num(l("Radius in cm", "Radius in cm"), r, "cm", rel = 0.01, done = """§r \approx ${m(r, 2)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Drücke die Oberfläche nur durch r aus (h aus dem Volumen), leite ab und setze null.", "Key idea: express the surface area in terms of r alone (h from the volume), differentiate and set to zero."), """§O(r) = 2\pi r^2 + \dfrac{2V}{r},\quad O'(r) = 4\pi r - \dfrac{2V}{r^2} = 0§"""))),
                solution = listOf("""§r = \left(\dfrac{V}{2\pi}\right)^{1/3} = \left(\dfrac{${m(v, 0)}}{2\pi}\right)^{1/3} \approx ${m(r, 2)}\ \text{cm}§""", l("Dann ist h = 2r: Die beste Dose ist genau so hoch wie breit.", "Then h = 2r: the best tin is exactly as tall as it is wide.")),
                background = l("Optimierungsaufgaben sind die klassische Anwendung der Ableitung: Am Optimum ist die Steigung null. Das Ergebnis h = 2r ist elegant, aber echte Getränkedosen sind schlanker. Der Grund: Deckel und Boden sind aus dickerem Blech, sie sind also teurer als die Wand, und schlanke Dosen liegen besser in der Hand.", "Optimisation problems are the classic application of the derivative: at the optimum the slope is zero. The result h = 2r is elegant, but real drinks cans are slimmer. The reason: the lid and base are made of thicker metal, so they're more expensive than the wall, and slim cans sit better in the hand."),
            )
        },
        lab("ma.partiell", Topic.Calculus, X, 0.7f) {
            val b = pick(1, 2); val v = (b - 1) * exp(b.toDouble()) + 1
            task(
                question = l("""Berechne §\displaystyle\int_0^{${b}} x\, e^x \, dx§.""", """Calculate §\displaystyle\int_0^{${b}} x\, e^x \, dx§."""),
                steps = listOf(num(l("Wert", "Value"), v, "", abs = 0.002, done = """§${m(v, 3)}§""",
                    hints = listOf(l("Kernidee: Partielle Integration, die Umkehrung der Produktregel. Wähle u = x (wird beim Ableiten einfacher) und v′ = eˣ.", "Key idea: integration by parts, the reverse of the product rule. Choose u = x (gets simpler when differentiated) and v′ = eˣ."), """§\int u v' = u v - \int u' v§"""))),
                solution = listOf("""§\int x e^x\, dx = x e^x - \int e^x\, dx = (x - 1)\, e^x§""", """§\left[(x - 1)\, e^x\right]_0^{${b}} = ${m((b - 1) * exp(b.toDouble()), 3)} - (-1) = ${m(v, 3)}§"""),
                background = l("Partielle Integration ist die Produktregel rückwärts gelesen. Sie hilft immer dann, wenn ein Faktor beim Ableiten einfacher wird und der andere beim Integrieren nicht schwieriger. Probe: Leitet man (x − 1)eˣ ab, kommt wieder x·eˣ heraus. Diese Probe sollte man bei jeder Stammfunktion machen.", "Integration by parts is the product rule read backwards. It helps whenever one factor gets simpler when differentiated and the other doesn't get harder when integrated. Check: differentiate (x − 1)eˣ and you get x·eˣ back. You should do this check for every antiderivative."),
            )
        },
    )
}
