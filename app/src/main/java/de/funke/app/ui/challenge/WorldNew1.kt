package de.funke.app.ui.challenge

import de.funke.app.ui.tr
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.sqrt

/* Ausbau (seit 13.2): Logikrätsel. Klassiker neu erzählt, Texte selbst geschrieben. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private fun g(level: Difficulty) = 0.3f + 0.15f * level.ordinal.coerceAtMost(3)
private fun T.f(): String = s.replace('§', '$')

/** Auswahlfrage: [right] ist richtig, die Ansicht mischt. Texte als [T], damit die Sprache erst beim Erzeugen gewählt wird. */
internal fun mc(
    id: String, topic: Topic, level: Difficulty, q: T, prompt: T, right: T, wrong: List<T>,
    hint: T, hint2: T, sol: T, bg: T, fact: T? = null,
) = lab(id, topic, level, g(level)) {
    task(
        question = q.f(),
        steps = listOf(pick1(prompt.f(), right.f(), wrong.map { it.f() }, listOf(hint.f(), hint2.f()))),
        solution = listOf(sol.f()), background = bg.f(), fact = fact?.f(),
    )
}

/** Stimmt-das-Frage zu einer Behauptung. */
internal fun tf(id: String, topic: Topic, level: Difficulty, ok: Boolean, claim: T, answer: T, why: T, hint: T) =
    lab(id, topic, level, g(level)) {
        val yes = tr("Stimmt", "True"); val no = tr("Stimmt nicht", "False")
        task(
            question = claim.f(),
            steps = listOf(pick1(tr("Stimmt das?", "True or false?"), if (ok) yes else no, listOf(if (ok) no else yes), listOf(hint.f(), tr("Denk an den Mechanismus dahinter, nicht an das, was man oft hört.", "Think about the mechanism behind it, not what people often say.")))),
            solution = listOf((if (ok) tr("Stimmt. ", "True. ") else tr("Stimmt nicht. ", "False. ")) + answer.f()),
            background = why.f(),
        )
    }

private val DAYS_DE = listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
private val DAYS_EN = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

object WorldNew1 {
    val all: List<TaskDef> get() = logic

    private val logic = listOf(
        // ------------------------------------------------ leicht
        lab("logik.socken", Topic.Logic, E, 0.3f) {
            val k = pick(2, 3, 4)
            val colours = when (k) { 2 -> l("schwarze und weiße", "black and white"); 3 -> l("schwarze, weiße und graue", "black, white and grey"); else -> l("schwarze, weiße, graue und blaue", "black, white, grey and blue") }
            task(
                question = l("In deiner Schublade liegen jede Menge $colours Socken durcheinander. Es ist stockdunkel. Wie viele Socken musst du mindestens herausnehmen, um ganz sicher zwei gleichfarbige zu haben?", "Your drawer holds lots of $colours socks all mixed up. It's pitch dark. What is the least number of socks you must take out to be absolutely sure of having two of the same colour?"),
                steps = listOf(num(l("Anzahl Socken", "Number of socks"), (k + 1).toDouble(), "", abs = 0.0, done = "${k + 1}",
                    hints = listOf(l("Kernidee: Stell dir den schlimmsten Fall vor. Wie viele Socken kannst du ziehen, ohne dass zwei gleich sind?", "Key idea: imagine the worst case. How many socks can you draw without two matching?"), l("Höchstens eine von jeder Farbe, also ${k}. Die nächste muss passen.", "At most one of each colour, so ${k}. The next one must match.")))),
                solution = listOf(l("Im schlimmsten Fall hast du nach ${k} Socken von jeder Farbe eine. Die ${k + 1}. Socke muss eine Farbe wiederholen.", "In the worst case, after ${k} socks you have one of each colour. Sock number ${k + 1} has to repeat a colour.")),
                background = l("Das ist das Schubfachprinzip: Verteilt man mehr Dinge als es Fächer gibt, landen in einem Fach mindestens zwei. Es klingt banal, beweist aber Erstaunliches, etwa dass in Berlin mindestens zwei Menschen exakt gleich viele Haare auf dem Kopf haben. Kein Mensch hat mehr als etwa 150.000, Berlin hat aber über drei Millionen Einwohner.", "This is the pigeonhole principle: if you share out more things than there are boxes, at least one box gets two. It sounds trivial but proves astonishing things, for instance that at least two people in London have exactly the same number of hairs on their head. Nobody has more than about 150,000, but London has millions of inhabitants."),
            )
        },
        lab("logik.schnecke", Topic.Logic, E, 0.35f) {
            val hgt = pick(10, 12, 20); val (up, down) = pick(3 to 2, 5 to 3, 4 to 2)
            val days = if (hgt <= up) 1 else ceil((hgt - up).toDouble() / (up - down)).toInt() + 1
            task(
                question = l("Eine Schnecke sitzt am Grund eines ${hgt} m tiefen Brunnens. Tagsüber kriecht sie ${up} m hoch, nachts rutscht sie ${down} m zurück. An welchem Tag erreicht sie den Rand?", "A snail sits at the bottom of a well ${hgt} m deep. By day it crawls up ${up} m, at night it slides back ${down} m. On which day does it reach the top?"),
                steps = listOf(num(l("Tag", "Day"), days.toDouble(), "", abs = 0.0, done = l("am ${days}. Tag", "on day ${days}"),
                    hints = listOf(l("Kernidee: Am letzten Tag rutscht sie nicht mehr zurück. Sobald sie tagsüber oben ist, ist sie draußen.", "Key idea: on the last day it doesn't slide back. As soon as it reaches the top during the day, it's out."), l("Sie muss also morgens nur noch ${hgt - up} m unter dem Rand starten. Pro ganzem Tag gewinnt sie ${up - down} m.", "So in the morning it only needs to start ${hgt - up} m below the top. Each full day it gains ${up - down} m.")))),
                solution = listOf(l("Nach ${days - 1} vollen Tagen ist sie ${(days - 1) * (up - down)} m hoch. Am ${days}. Tag kriecht sie die letzten Meter und ist draußen.", "After ${days - 1} full days it's ${(days - 1) * (up - down)} m up. On day ${days} it crawls the last metres and is out.")),
                background = l("Die schnelle Antwort ${hgt / (up - down)} ist falsch, weil sie den letzten Tag wie alle anderen behandelt. Viele Denkfehler entstehen genau so: Man rechnet mit einem Durchschnitt, obwohl am Rand etwas anderes gilt. Programmierer kennen das als Off-by-one-Fehler, den häufigsten Fehler beim Zählen.", "The quick answer ${hgt / (up - down)} is wrong, because it treats the last day like all the others. Many thinking errors happen exactly like this: you work with an average even though something different happens at the edge. Programmers know this as the off-by-one error, the most common counting mistake."),
            )
        },
        mc("logik.angler", Topic.Logic, E,
            t("Zwei Väter und zwei Söhne gehen angeln. Jeder von ihnen fängt genau einen Fisch, und trotzdem bringen sie nur drei Fische mit nach Hause. Keiner wurde verloren oder gegessen. Wie geht das?", "Two fathers and two sons go fishing. Each of them catches exactly one fish, and yet they only bring three fish home. None were lost or eaten. How can that be?"),
            t("Die Lösung", "The solution"),
            t("Es sind nur drei Personen: Großvater, Vater und Sohn.", "There are only three people: grandfather, father and son."),
            listOf(t("Einer der Fische war so klein, dass er nicht zählt.", "One of the fish was so small it doesn't count."), t("Zwei haben zusammen einen Fisch gefangen.", "Two of them caught one fish together."), t("Einer der Söhne ist kein Mensch.", "One of the sons isn't human.")),
            t("Kernidee: Muss jede Rolle eine eigene Person sein?", "Key idea: does every role have to be a different person?"),
            t("Kann jemand gleichzeitig Vater und Sohn sein?", "Can someone be a father and a son at the same time?"),
            t("Der mittlere Mann ist Vater (seines Sohnes) und Sohn (des Großvaters). Zwei Väter und zwei Söhne sind also nur drei Menschen.", "The man in the middle is a father (of his son) and a son (of the grandfather). So two fathers and two sons are only three people."),
            t("Solche Rätsel spielen mit einer stillen Annahme: dass verschiedene Bezeichnungen verschiedene Dinge meinen. In der Logik und Mathematik ist das ein ständiger Stolperstein. „Zwei Väter und zwei Söhne“ ist eine Aussage über Rollen, nicht über die Zahl der Personen. Beim Zählen lohnt es sich immer zu fragen, ob etwas doppelt gezählt wird.", "Puzzles like this play on a silent assumption: that different labels mean different things. In logic and mathematics this is a constant stumbling block. 'Two fathers and two sons' is a statement about roles, not about the number of people. When counting, it always pays to ask whether something is being counted twice."),
        ),
        lab("logik.obstwaage", Topic.Logic, E, 0.35f) {
            val a = pick(10, 12, 8); val b = pick(5, 4, 3); val c = b - pick(1, 2)
            task(
                question = l("Ein Bilderrätsel mit Obst: Apfel + Apfel + Apfel = ${3 * a}. Apfel + Birne + Birne = ${a + 2 * b}. Birne − Kirsche = ${b - c}. Wie viel ist Apfel + Birne + Kirsche?", "A picture puzzle with fruit: apple + apple + apple = ${3 * a}. Apple + pear + pear = ${a + 2 * b}. Pear − cherry = ${b - c}. What is apple + pear + cherry?"),
                steps = listOf(num(l("Ergebnis", "Result"), (a + b + c).toDouble(), "", abs = 0.0, done = "${a + b + c}",
                    hints = listOf(l("Kernidee: Fang mit der Zeile an, in der nur eine Obstsorte vorkommt.", "Key idea: start with the line that has only one kind of fruit."), l("Apfel = ${a}. Setz das in die zweite Zeile ein.", "Apple = ${a}. Put that into the second line.")))),
                solution = listOf(l("Apfel = ${a}, Birne = (${a + 2 * b} − ${a}) / 2 = ${b}, Kirsche = ${b} − ${b - c} = ${c}. Zusammen ${a + b + c}.", "Apple = ${a}, pear = (${a + 2 * b} − ${a}) / 2 = ${b}, cherry = ${b} − ${b - c} = ${c}. Together ${a + b + c}.")),
                background = l("Hinter diesen Bilderrätseln steckt ein Gleichungssystem mit drei Unbekannten. Der Trick ist immer derselbe: eine Gleichung finden, in der nur eine Unbekannte vorkommt, sie lösen und das Ergebnis einsetzen. So arbeitet sich auch ein Computer durch Systeme mit Tausenden Gleichungen, nur viel schneller.", "Behind these picture puzzles is a system of equations with three unknowns. The trick is always the same: find an equation with only one unknown, solve it and substitute the result. That's how a computer works through systems with thousands of equations too, only much faster."),
            )
        },
        lab("logik.wochentag", Topic.Logic, E, 0.4f) {
            val start = int(0, 6); val days = pick(100, 1000, 365, 250, 500)
            val end = (start + days) % 7
            val wrong = listOf(1, 2, 3, 4, 5, 6).map { (end + it) % 7 }.shuffled(rnd).take(3)
            val dn = { i: Int -> tr(DAYS_DE[i], DAYS_EN[i]) }
            task(
                question = l("Heute ist ${DAYS_DE[start]}. Welcher Wochentag ist in genau ${days} Tagen?", "Today is ${DAYS_EN[start]}. What day of the week is it in exactly ${days} days?"),
                steps = listOf(pick1(l("Wochentag", "Day of the week"), dn(end), wrong.map(dn),
                    hints = listOf(l("Kernidee: Alle 7 Tage ist wieder derselbe Wochentag. Ganze Wochen kannst du also weglassen.", "Key idea: every 7 days it's the same weekday again. So you can drop whole weeks."), l("${days} = ${days / 7} · 7 + ${days % 7}. Nur der Rest ${days % 7} zählt.", "${days} = ${days / 7} · 7 + ${days % 7}. Only the remainder ${days % 7} counts.")))),
                solution = listOf(l("${days} geteilt durch 7 lässt den Rest ${days % 7}. ${days % 7} Tage nach ${DAYS_DE[start]} ist ${DAYS_DE[end]}.", "${days} divided by 7 leaves remainder ${days % 7}. ${days % 7} days after ${DAYS_EN[start]} is ${DAYS_EN[end]}.")),
                background = l("Rechnen mit Resten heißt modulare Arithmetik oder Uhrenrechnen: Nach 7 fängt man wieder bei 0 an. Weil 365 = 52 · 7 + 1 ist, rutscht dein Geburtstag jedes Jahr einen Wochentag weiter, nach einem Schaltjahr sogar zwei. Dieselbe Rechnung schützt heute fast jede verschlüsselte Internetverbindung.", "Calculating with remainders is called modular arithmetic or clock arithmetic: after 7 you start again at 0. Because 365 = 52 · 7 + 1, your birthday moves on one weekday every year, and by two after a leap year. The same arithmetic protects almost every encrypted internet connection today."),
            )
        },
        // ------------------------------------------------ mittel
        lab("logik.waage", Topic.Logic, M, 0.5f) {
            val nC = pick(8, 9, 27, 10, 12)
            var w = 0; var cap = 1; while (cap < nC) { cap *= 3; w++ }
            task(
                question = l("Von ${nC} gleich aussehenden Münzen ist genau eine etwas schwerer. Du hast eine Balkenwaage ohne Gewichte. Wie oft musst du mindestens wiegen, um die schwere Münze sicher zu finden?", "Of ${nC} identical-looking coins exactly one is slightly heavier. You have a balance scale without weights. What is the least number of weighings that is sure to find the heavy coin?"),
                steps = listOf(num(l("Wägungen", "Weighings"), w.toDouble(), "", abs = 0.0, done = "$w",
                    hints = listOf(l("Kernidee: Eine Wägung hat drei mögliche Ausgänge: links schwerer, rechts schwerer oder Gleichgewicht. Teil die Münzen deshalb in drei Gruppen, nicht in zwei.", "Key idea: a weighing has three possible outcomes: left heavier, right heavier or balance. So split the coins into three groups, not two."), l("Mit einer Wägung findest du die schwere unter 3 Münzen, mit zwei unter 9, mit drei unter 27.", "With one weighing you can find the heavy one among 3 coins, with two among 9, with three among 27.")))),
                solution = listOf(l("Jede Wägung drittelt die Zahl der Verdächtigen. ${nC} Münzen brauchen deshalb ${w} Wägungen, denn 3^${w} = ${cap} ≥ ${nC}${if (w > 1) ", aber 3^${w - 1} = ${cap / 3} reicht nicht" else ""}.", "Each weighing cuts the number of suspects to a third. So ${nC} coins need ${w} weighings, because 3^${w} = ${cap} ≥ ${nC}${if (w > 1) ", but 3^${w - 1} = ${cap / 3} is not enough" else ""}.")),
                background = l("Die meisten denken zuerst an Halbieren, aber dabei wird der dritte Ausgang verschenkt: Bleibt die Waage im Gleichgewicht, liegt die schwere Münze in der Gruppe, die gar nicht auf der Waage war. Jede Messung liefert Information, und gute Strategien nutzen alle möglichen Antworten. Genau so denkt die Informationstheorie.", "Most people first think of halving, but that wastes the third outcome: if the scale balances, the heavy coin is in the group that wasn't on the scale at all. Every measurement delivers information, and good strategies make use of every possible answer. That's exactly how information theory thinks."),
            )
        },
        lab("logik.wasserkruege", Topic.Logic, M, 0.5f) {
            task(
                question = l("Du hast einen 3-Liter-Krug, einen 5-Liter-Krug und einen Wasserhahn, aber keine Messstriche. Du brauchst genau 4 Liter. Wie oft musst du mindestens einen Krug am Hahn füllen? (Ausgießen und Umschütten zählt nicht.)", "You have a 3-litre jug, a 5-litre jug and a tap, but no markings. You need exactly 4 litres. What is the least number of times you have to fill a jug at the tap? (Emptying and pouring between jugs doesn't count.)"),
                steps = listOf(num(l("Füllungen", "Fillings"), 2.0, "", abs = 0.0, done = "2",
                    hints = listOf(l("Kernidee: Der kleine Krug ist ein Werkzeug zum Abziehen. 5 − 3 = 2, und von 2 kommt man weiter.", "Key idea: the small jug is a tool for taking away. 5 − 3 = 2, and from 2 you can get further."), l("Was passiert, wenn 2 Liter im kleinen Krug sind und du ihn aus einem vollen großen Krug auffüllst?", "What happens if there are 2 litres in the small jug and you top it up from a full big jug?")))),
                solution = listOf(l("Großen füllen (1), in den kleinen gießen: 2 Liter bleiben. Kleinen leeren, die 2 Liter hineingießen. Großen wieder füllen (2) und den kleinen auffüllen: Dafür braucht es 1 Liter, im großen bleiben genau 4.", "Fill the big one (1), pour into the small one: 2 litres remain. Empty the small one, pour the 2 litres in. Fill the big one again (2) and top up the small one: that takes 1 litre, leaving exactly 4 in the big one.")),
                background = l("Mit Krügen von 3 und 5 Litern lässt sich jede ganze Literzahl bis 8 abmessen, weil 3 und 5 keinen gemeinsamen Teiler haben. Mit 4 und 6 Litern dagegen bekommt man nur gerade Mengen. Dahinter steckt der größte gemeinsame Teiler, und Bézouts Lemma sagt genau, welche Mengen erreichbar sind.", "With jugs of 3 and 5 litres you can measure every whole number of litres up to 8, because 3 and 5 have no common factor. With 4 and 6 litres, by contrast, you only get even amounts. Behind this is the greatest common divisor, and Bézout's lemma says exactly which amounts can be reached."),
            )
        },
        lab("logik.flussueberquerung", Topic.Logic, M, 0.5f) {
            task(
                question = l("Ein Bauer will einen Wolf, eine Ziege und einen Kohlkopf über einen Fluss bringen. Ins Boot passt außer ihm nur eins davon. Allein gelassen frisst der Wolf die Ziege und die Ziege den Kohl. Wie viele Fahrten über den Fluss braucht er mindestens (hin und zurück zählen einzeln)?", "A farmer wants to take a wolf, a goat and a cabbage across a river. Besides him, the boat only holds one of them. Left alone, the wolf eats the goat and the goat eats the cabbage. What is the least number of crossings he needs (there and back count separately)?"),
                steps = listOf(num(l("Fahrten", "Crossings"), 7.0, "", abs = 0.0, done = "7",
                    hints = listOf(l("Kernidee: Die Ziege ist das Problem, denn sie verträgt sich mit keinem. Sie muss zuerst rüber.", "Key idea: the goat is the problem, as it can't be left with either. It has to go first."), l("Der Trick: Man darf etwas auch wieder zurückbringen.", "The trick: you're allowed to bring something back again.")))),
                solution = listOf(l("Ziege rüber, leer zurück, Wolf rüber, Ziege mit zurück, Kohl rüber, leer zurück, Ziege rüber: 7 Fahrten.", "Goat across, back empty, wolf across, goat back, cabbage across, back empty, goat across: 7 crossings.")),
                background = l("Dieses Rätsel steht schon in einer Sammlung des Gelehrten Alkuin aus dem 8. Jahrhundert. Es zeigt eine wichtige Idee beim Problemlösen: Manchmal muss man einen Schritt rückwärts gehen, um ans Ziel zu kommen. Informatiker lösen solche Aufgaben, indem sie alle erlaubten Zustände als Netz zeichnen und den kürzesten Weg suchen.", "This puzzle already appears in a collection by the scholar Alcuin from the 8th century. It shows an important idea in problem solving: sometimes you have to take a step backwards to reach the goal. Computer scientists solve such tasks by drawing all the allowed states as a network and searching for the shortest path."),
            )
        },
        lab("logik.haendedruck", Topic.Logic, M, 0.55f) {
            val nn = pick(7, 9, 5, 11); val deg = pick(3, 5)
            task(
                question = l("Auf einer Party sind ${nn} Leute. Kann es sein, dass jeder genau ${deg} anderen die Hand gegeben hat?", "There are ${nn} people at a party. Is it possible that everyone has shaken hands with exactly ${deg} others?"),
                steps = listOf(pick1(l("Antwort", "Answer"), l("Nein, das ist unmöglich.", "No, that's impossible."), listOf(l("Ja, das geht.", "Yes, that's possible."), l("Nur wenn sich zwei zweimal die Hand geben.", "Only if two people shake hands twice."), l("Das hängt davon ab, wer wen kennt.", "That depends on who knows whom.")),
                    hints = listOf(l("Kernidee: Zähle die Hände. Jeder Händedruck beteiligt genau zwei Hände.", "Key idea: count the hands. Every handshake involves exactly two hands."), l("${nn} · ${deg} = ${nn * deg}. Ist das durch 2 teilbar?", "${nn} · ${deg} = ${nn * deg}. Is that divisible by 2?")))),
                solution = listOf(l("Zählt jeder seine Händedrücke, kommt ${nn} · ${deg} = ${nn * deg} heraus. Dabei wird jeder Händedruck doppelt gezählt, die Summe müsste also gerade sein. ${nn * deg} ist ungerade, also geht es nicht.", "If everyone counts their handshakes, you get ${nn} · ${deg} = ${nn * deg}. Every handshake is counted twice, so the total ought to be even. ${nn * deg} is odd, so it's impossible.")),
                background = l("Das ist das Handschlaglemma, der erste Satz der Graphentheorie. Leonhard Euler bewies ihn 1736 bei seinem berühmten Rätsel über die sieben Brücken von Königsberg. Daraus folgt: In jedem Netzwerk ist die Zahl der Knoten mit ungerade vielen Verbindungen gerade, ob bei Freundschaften, Straßen oder Molekülen.", "This is the handshake lemma, the first theorem of graph theory. Leonhard Euler proved it in 1736 with his famous puzzle about the seven bridges of Königsberg. It follows that in every network the number of nodes with an odd number of links is even, whether for friendships, roads or molecules."),
            )
        },
        mc("logik.nassestrasse", Topic.Logic, M,
            t("Es gilt: Wenn es regnet, wird die Straße nass. Du schaust aus dem Fenster und siehst: Die Straße ist nass. Was folgt daraus sicher?", "This holds: if it rains, the street gets wet. You look out of the window and see: the street is wet. What follows for certain?"),
            t("Schluss", "Conclusion"),
            t("Nichts über den Regen. Die Straße kann auch aus einem anderen Grund nass sein.", "Nothing about rain. The street could be wet for another reason."),
            listOf(t("Es hat geregnet.", "It has rained."), t("Es hat nicht geregnet.", "It has not rained."), t("Es wird gleich regnen.", "It's about to rain.")),
            t("Kernidee: „Wenn A, dann B“ sagt nichts darüber, was sonst noch B verursachen kann.", "Key idea: 'if A, then B' says nothing about what else might cause B."),
            t("Denk an einen Rasensprenger oder ein Reinigungsfahrzeug.", "Think of a sprinkler or a street-cleaning truck."),
            t("Aus „Regen → nass“ folgt nicht „nass → Regen“. Sicher folgt nur die Umkehrung mit Verneinung: Ist die Straße trocken, hat es nicht geregnet.", "From 'rain → wet' it doesn't follow that 'wet → rain'. The only thing that does follow is the contrapositive: if the street is dry, it hasn't rained."),
            t("Vom Ergebnis auf die Ursache zu schließen, ist ein klassischer Fehlschluss, die Bejahung des Hinterglieds. Er steckt in vielen Alltagsargumenten: „Wer krank ist, hat Fieber. Ich habe Fieber, also habe ich Grippe.“ Gültig ist nur der Schluss über das Gegenteil, die Kontraposition. Ärzte nennen das Differenzialdiagnose: mehrere Ursachen prüfen.", "Reasoning from the result back to the cause is a classic fallacy, affirming the consequent. It lurks in many everyday arguments: 'People with flu have a temperature. I have a temperature, so I have flu.' Only the reasoning via the opposite, the contrapositive, is valid. Doctors call checking several possible causes differential diagnosis."),
        ),
        mc("logik.syllogismus", Topic.Logic, M,
            t("Alle Wuppels sind Zorks. Einige Zorks sind blau. Folgt daraus, dass einige Wuppels blau sind?", "All wuppels are zorks. Some zorks are blue. Does it follow that some wuppels are blue?"),
            t("Antwort", "Answer"),
            t("Nein, das folgt nicht.", "No, that doesn't follow."),
            listOf(t("Ja, das folgt.", "Yes, it follows."), t("Ja, sogar alle Wuppels sind blau.", "Yes, in fact all wuppels are blue."), t("Nein, kein Wuppel kann blau sein.", "No, no wuppel can be blue.")),
            t("Kernidee: Zeichne die Zorks als großen Kreis und die Wuppels als kleinen Kreis darin. Wo dürfen die blauen Zorks liegen?", "Key idea: draw the zorks as a big circle with the wuppels as a small circle inside it. Where can the blue zorks be?"),
            t("Die blauen Zorks könnten alle außerhalb des Wuppel-Kreises liegen.", "The blue zorks could all lie outside the wuppel circle."),
            t("Die blauen Zorks können genau die Zorks sein, die keine Wuppels sind. Möglich ist es also, aber es folgt nicht.", "The blue zorks could be exactly the zorks that aren't wuppels. So it's possible, but it doesn't follow."),
            t("Mit Fantasiewörtern merkt man, ob ein Schluss wirklich logisch gültig ist, denn das Vorwissen hilft nicht mehr. Mit echten Wörtern fällt man leichter herein: „Alle Rosen sind Blumen, einige Blumen welken schnell, also welken einige Rosen schnell“ klingt richtig, ist aber genauso ungültig. Venn-Diagramme machen solche Fehler sichtbar.", "With made-up words you notice whether an argument is really logically valid, because prior knowledge no longer helps. With real words it's easier to be fooled: 'All roses are flowers, some flowers fade quickly, so some roses fade quickly' sounds right but is just as invalid. Venn diagrams make such errors visible."),
        ),
        mc("logik.haustiere", Topic.Logic, M,
            t("Anna, Ben und Clara wohnen in einem roten, einem grünen und einem blauen Haus. Jeder hat ein Haustier: Hund, Katze oder Fisch. Der Hund lebt im blauen Haus. Anna wohnt nicht im blauen Haus und hat keine Katze. Clara wohnt im roten Haus. Welches Tier lebt im roten Haus?", "Anna, Ben and Clara live in a red, a green and a blue house. Each has a pet: a dog, a cat or a fish. The dog lives in the blue house. Anna doesn't live in the blue house and doesn't have a cat. Clara lives in the red house. Which animal lives in the red house?"),
            t("Tier im roten Haus", "Animal in the red house"),
            t("Die Katze", "The cat"),
            listOf(t("Der Hund", "The dog"), t("Der Fisch", "The fish"), t("Das lässt sich nicht sagen.", "It can't be determined.")),
            t("Kernidee: Ordne zuerst die Häuser zu. Clara ist sicher, dann bleibt für Anna nur eins.", "Key idea: sort out the houses first. Clara is certain, then there's only one left for Anna."),
            t("Anna: grün, Ben: blau. Der Hund ist im blauen Haus, also bei Ben.", "Anna: green, Ben: blue. The dog is in the blue house, so with Ben."),
            t("Clara rot, Anna grün, Ben blau mit dem Hund. Anna hat keine Katze, also den Fisch. Für Clara im roten Haus bleibt die Katze.", "Clara red, Anna green, Ben blue with the dog. Anna has no cat, so she has the fish. That leaves the cat for Clara in the red house."),
            t("Solche Zuordnungsrätsel löst man mit einer Tabelle, in der man Unmögliches durchstreicht, bis nur eine Möglichkeit übrig bleibt. Das berühmteste ist das Zebrarätsel mit fünf Häusern, das oft fälschlich Einstein zugeschrieben wird. Computer lösen solche Aufgaben mit derselben Idee: Einschränkungen weitergeben, bis alles feststeht.", "Puzzles like this are solved with a grid in which you cross out what's impossible until only one possibility is left. The most famous is the zebra puzzle with five houses, which is often wrongly attributed to Einstein. Computers solve such problems with the same idea: pass on constraints until everything is fixed."),
        ),
        mc("logik.logikerbar", Topic.Logic, M,
            t("Drei Logikerinnen kommen in eine Bar. Der Wirt fragt: „Möchtet ihr alle drei ein Bier?“ Die erste sagt: „Weiß ich nicht.“ Die zweite sagt: „Weiß ich nicht.“ Die dritte möchte ein Bier. Was sagt sie?", "Three logicians walk into a bar. The barman asks: 'Do all three of you want a beer?' The first says: 'I don't know.' The second says: 'I don't know.' The third wants a beer. What does she say?"),
            t("Antwort der dritten", "The third one's answer"),
            t("„Ja!“", "'Yes!'"),
            listOf(t("„Weiß ich nicht.“", "'I don't know.'"), t("„Nein.“", "'No.'"), t("„Das kann niemand wissen.“", "'Nobody can know that.'")),
            t("Kernidee: Was hätte die erste gesagt, wenn sie selbst kein Bier wollte?", "Key idea: what would the first one have said if she didn't want a beer herself?"),
            t("Dann wäre sicher, dass nicht alle eins wollen, und sie hätte „Nein“ gesagt.", "Then it would be certain that not all of them want one, and she'd have said 'No'."),
            t("„Weiß ich nicht“ verrät: Die erste will ein Bier, kennt aber die anderen nicht. Dasselbe gilt für die zweite. Die dritte weiß also, dass beide eins wollen, und sie selbst auch: Ja.", "'I don't know' gives away that the first wants a beer but doesn't know about the others. The same goes for the second. So the third knows that both want one, and so does she: yes."),
            t("Der Witz zeigt, dass auch „Ich weiß es nicht“ Information enthält. Logiker nennen das gemeinsames Wissen: Was jemand weiß, worüber er schweigt oder was er nicht weiß, kann für andere ein Hinweis sein. Dasselbe Prinzip steckt in Pokerstrategien und in der Spieltheorie.", "The joke shows that 'I don't know' contains information too. Logicians call this common knowledge: what someone knows, what they keep quiet about, or what they don't know can be a clue for others. The same principle lies behind poker strategies and game theory."),
        ),
        mc("logik.zuendschnur", Topic.Logic, M,
            t("Du hast zwei Zündschnüre. Jede brennt genau 60 Minuten, aber ungleichmäßig: Die halbe Länge kann nach 10 oder nach 50 Minuten erreicht sein. Wie misst du genau 45 Minuten ab?", "You have two fuses. Each burns for exactly 60 minutes, but unevenly: half the length might be reached after 10 or after 50 minutes. How do you measure exactly 45 minutes?"),
            t("Methode", "Method"),
            t("Schnur A an beiden Enden und Schnur B an einem Ende anzünden. Ist A abgebrannt, das zweite Ende von B anzünden. Wenn B fertig ist, sind 45 Minuten um.", "Light fuse A at both ends and fuse B at one end. When A has burnt out, light the other end of B. When B is finished, 45 minutes are up."),
            listOf(t("Schnur A in der Mitte durchschneiden und eine Hälfte abbrennen lassen, dann noch eine Viertelschnur.", "Cut fuse A in the middle and let one half burn, then another quarter of a fuse."), t("Beide Schnüre nacheinander abbrennen lassen und durch drei teilen.", "Burn both fuses one after the other and divide by three."), t("Das geht nicht, weil die Schnüre ungleichmäßig brennen.", "It can't be done, because the fuses burn unevenly.")),
            t("Kernidee: Brennt eine Schnur von beiden Enden, ist sie nach genau 30 Minuten weg, egal wie ungleichmäßig sie ist.", "Key idea: if a fuse burns from both ends, it's gone after exactly 30 minutes, however unevenly it burns."),
            t("Bei Schnur B sind nach 30 Minuten noch genau 30 Minuten Brenndauer übrig. Was passiert, wenn du die jetzt halbierst?", "After 30 minutes fuse B has exactly 30 minutes of burning left. What happens if you halve that now?"),
            t("A brennt von beiden Seiten und ist nach 30 Minuten weg. In diesem Moment hat B noch 30 Minuten Brenndauer, von beiden Enden brennt das in 15 Minuten ab: 30 + 15 = 45.", "A burns from both ends and is gone after 30 minutes. At that moment B has 30 minutes of burning left, and from both ends that takes 15 minutes: 30 + 15 = 45."),
            t("Abschneiden hilft nicht, weil die halbe Länge nicht die halbe Zeit bedeutet. Der Trick misst deshalb Zeit statt Länge. Gute Lösungen nutzen oft eine Größe, die sicher bekannt ist, hier die Gesamtbrenndauer, und umgehen alles Unsichere.", "Cutting doesn't help, because half the length doesn't mean half the time. That's why the trick measures time instead of length. Good solutions often use a quantity that is known for sure, here the total burning time, and get round everything uncertain."),
        ),
        lab("logik.zahlenraten", Topic.Logic, M, 0.5f) {
            val top = pick(100, 1000, 1_000_000, 50)
            val k = ceil(ln(top + 1.0) / ln(2.0) - 1e-9).toInt()
            task(
                question = l("Jemand denkt sich eine ganze Zahl von 1 bis ${n(top)}. Du darfst raten und hörst jedes Mal „zu groß“, „zu klein“ oder „richtig“. Wie viele Versuche brauchst du mit der besten Strategie höchstens?", "Someone thinks of a whole number from 1 to ${n(top)}. You may guess, and each time you hear 'too high', 'too low' or 'correct'. With the best strategy, what is the maximum number of guesses you need?"),
                steps = listOf(num(l("Höchstens so viele Versuche", "At most this many guesses"), k.toDouble(), "", abs = 0.0, done = "$k",
                    hints = listOf(l("Kernidee: Rate immer genau in der Mitte. Dann bleibt nach jedem Versuch höchstens die Hälfte übrig.", "Key idea: always guess right in the middle. Then at most half is left after each guess."), l("Mit k Versuchen kannst du unter höchstens 2^k − 1 Zahlen die richtige finden.", "With k guesses you can find the right number among at most 2^k − 1 numbers.")))),
                solution = listOf(l("2^${k} − 1 = ${n(Math.pow(2.0, k.toDouble()) - 1, 0)} ≥ ${n(top)}, aber 2^${k - 1} − 1 = ${n(Math.pow(2.0, k - 1.0) - 1, 0)} ist zu wenig. Also ${k} Versuche.", "2^${k} − 1 = ${n(Math.pow(2.0, k.toDouble()) - 1, 0)} ≥ ${n(top)}, but 2^${k - 1} − 1 = ${n(Math.pow(2.0, k - 1.0) - 1, 0)} is too few. So ${k} guesses.")),
                background = l("Diese binäre Suche ist einer der wichtigsten Algorithmen überhaupt. Sie findet einen Namen in einem Telefonbuch mit einer Million Einträgen in höchstens 20 Schritten, in einer Milliarde in 30. Jeder zusätzliche Schritt verdoppelt die Menge, die man durchsuchen kann. So arbeiten Datenbanken und die Suche in sortierten Listen.", "This binary search is one of the most important algorithms of all. It finds a name in a phone book with a million entries in at most 20 steps, and in a billion in 30. Each extra step doubles the amount you can search. That's how databases and searches in sorted lists work."),
            )
        },
        lab("logik.magischesquadrat", Topic.Logic, M, 0.5f) {
            task(
                question = l("In ein 3×3-Feld sollen die Zahlen 1 bis 9 so eingetragen werden, dass jede Zeile, jede Spalte und beide Diagonalen dieselbe Summe haben. Welche Zahl muss in der Mitte stehen?", "The numbers 1 to 9 are to be written into a 3×3 grid so that every row, every column and both diagonals have the same sum. Which number must go in the centre?"),
                steps = listOf(num(l("Zahl in der Mitte", "Number in the centre"), 5.0, "", abs = 0.0, done = "5",
                    hints = listOf(l("Kernidee: Alle Zahlen zusammen ergeben 45, verteilt auf drei Zeilen. Wie groß ist also jede Summe?", "Key idea: all the numbers add up to 45, spread over three rows. So how big is each sum?"), l("Die Mitte liegt auf vier Linien: mittlere Zeile, mittlere Spalte, zwei Diagonalen. Zähl diese vier Linien zusammen.", "The centre lies on four lines: middle row, middle column and two diagonals. Add these four lines together.")))),
                solution = listOf(l("Jede Linie hat die Summe 45 / 3 = 15. Die vier Linien durch die Mitte ergeben 4 · 15 = 60. Darin kommt jede Zahl einmal vor und die Mitte viermal, also 45 + 3 · Mitte = 60. Die Mitte ist 5.", "Each line sums to 45 / 3 = 15. The four lines through the centre give 4 · 15 = 60. In those, every number appears once and the centre four times, so 45 + 3 · centre = 60. The centre is 5.")),
                background = l("Dieses magische Quadrat ist in China als Lo-Shu seit über 2000 Jahren bekannt. Es gibt im Wesentlichen nur ein einziges, alle anderen sind Drehungen oder Spiegelungen davon. Der Trick, etwas auf zwei Arten zu zählen und die Ergebnisse gleichzusetzen, ist eines der stärksten Werkzeuge der Kombinatorik.", "This magic square has been known in China as the Lo Shu for over 2000 years. There is essentially only one; all the others are rotations or reflections of it. The trick of counting something in two ways and setting the results equal is one of the most powerful tools in combinatorics."),
            )
        },
        lab("logik.ziffernzaehlen", Topic.Logic, M, 0.5f) {
            val d = pick(1, 7, 5, 9)
            val cnt = (1..100).sumOf { v -> v.toString().count { it == ('0' + d) } }
            task(
                question = l("Du schreibst alle Zahlen von 1 bis 100 auf. Wie oft schreibst du dabei die Ziffer ${d}?", "You write down all the numbers from 1 to 100. How many times do you write the digit ${d}?"),
                steps = listOf(num(l("Anzahl", "Count"), cnt.toDouble(), "", abs = 0.0, done = "$cnt",
                    hints = listOf(l("Kernidee: Zähl getrennt: Wie oft steht die ${d} an der Einerstelle, wie oft an der Zehnerstelle?", "Key idea: count separately: how often is ${d} in the units place, how often in the tens place?"), l("Einer: ${d}, 1${d}, 2${d}, … das sind 10. Zehner: ${d}0 bis ${d}9, wieder 10.${if (d == 1) " Und dann ist da noch die 100." else ""}", "Units: ${d}, 1${d}, 2${d}, … that's 10. Tens: ${d}0 to ${d}9, another 10.${if (d == 1) " And then there's 100." else ""}")))),
                solution = listOf(l("10 Mal an der Einerstelle, 10 Mal an der Zehnerstelle${if (d == 1) ", dazu die 1 in der 100" else ""}: ${cnt}. Die ${d}${d} zählt dabei zweimal, und das ist richtig, denn man schreibt die Ziffer zweimal.", "10 times in the units place, 10 times in the tens place${if (d == 1) ", plus the 1 in 100" else ""}: ${cnt}. ${d}${d} counts twice, and that's right, because you write the digit twice.")),
                background = l("Fast jeder schätzt hier zu wenig, weil man an die Zahlen denkt, nicht an die Stellen. Zerlegen in Stellen ist der Schlüssel: Jede Stelle durchläuft alle zehn Ziffern gleich oft. So weiß eine Druckerei, wie viele Stempel sie braucht, und Hausnummernhändler wissen, welche Ziffer am häufigsten verkauft wird.", "Almost everyone underestimates here, because you think of the numbers rather than the places. Splitting into places is the key: each place runs through all ten digits equally often. That's how a print shop knows how many stamps it needs, and house-number sellers know which digit sells most."),
            )
        },
        lab("logik.seitenzahlen", Topic.Logic, M, 0.55f) {
            val pages = pick(48, 111, 133, 200, 75)
            val digits = if (pages <= 9) pages else if (pages <= 99) 9 + 2 * (pages - 9) else 189 + 3 * (pages - 99)
            task(
                question = l("Eine Druckerei hat für die Seitenzahlen eines Buches genau ${n(digits)} Ziffern gedruckt. Die Nummerierung beginnt bei 1. Wie viele Seiten hat das Buch?", "For the page numbers of a book, a printer has printed exactly ${n(digits)} digits. Numbering starts at 1. How many pages does the book have?"),
                steps = listOf(num(l("Seiten", "Pages"), pages.toDouble(), "", abs = 0.0, done = "$pages",
                    hints = listOf(l("Kernidee: Seiten 1 bis 9 brauchen je eine Ziffer, 10 bis 99 je zwei, ab 100 je drei.", "Key idea: pages 1 to 9 take one digit each, 10 to 99 two each, from 100 on three each."), l("Bis Seite 99 sind es 9 + 90 · 2 = 189 Ziffern.", "Up to page 99 that's 9 + 90 · 2 = 189 digits.")))),
                solution = listOf(if (pages > 99) l("Bis Seite 99: 189 Ziffern. Übrig: ${digits - 189}, das sind ${(digits - 189) / 3} dreistellige Seiten. 99 + ${(digits - 189) / 3} = ${pages}.", "Up to page 99: 189 digits. Remaining: ${digits - 189}, which is ${(digits - 189) / 3} three-digit pages. 99 + ${(digits - 189) / 3} = ${pages}.")
                    else l("Bis Seite 9: 9 Ziffern. Übrig: ${digits - 9}, das sind ${(digits - 9) / 2} zweistellige Seiten. 9 + ${(digits - 9) / 2} = ${pages}.", "Up to page 9: 9 digits. Remaining: ${digits - 9}, which is ${(digits - 9) / 2} two-digit pages. 9 + ${(digits - 9) / 2} = ${pages}.")),
                background = l("Rückwärts rechnen heißt hier: Den Weg in Abschnitte zerlegen, in denen alles gleich ist, und dann abziehen, bis der Rest passt. Das Prinzip steckt auch in Steuertarifen mit Stufen oder in Handytarifen. Wer die Übergänge kennt, rechnet schnell und sicher.", "Working backwards here means splitting the path into sections in which everything is the same, then subtracting until the remainder fits. The same principle is behind tax brackets or phone tariffs with tiers. If you know where the transitions are, you can calculate quickly and reliably."),
            )
        },
        mc("logik.fehlendereuro", Topic.Logic, M,
            t("Drei Freunde zahlen für ein Hotelzimmer je 10 €, zusammen 30 €. Dann merkt der Wirt, dass das Zimmer nur 25 € kostet, und gibt dem Pagen 5 € zurück. Der Page steckt 2 € ein und gibt jedem 1 € wieder. Jeder hat also 9 € gezahlt, das sind 27 €, plus 2 € beim Pagen: 29 €. Wo ist der 30. Euro?", "Three friends pay €10 each for a hotel room, €30 in total. Then the manager realises the room only costs €25 and gives the porter €5 to hand back. The porter pockets €2 and gives each friend €1 back. So each has paid €9, that's €27, plus €2 with the porter: €29. Where is the 30th euro?"),
            t("Auflösung", "Explanation"),
            t("Es fehlt keiner: In den 27 € stecken die 2 € des Pagen schon drin. Richtig ist 27 € = 25 € Zimmer + 2 € Page.", "None is missing: the €27 already includes the porter's €2. The correct sum is €27 = €25 room + €2 porter."),
            listOf(t("Der Wirt hat einen Euro zu viel behalten.", "The manager kept a euro too many."), t("Beim Zurückgeben ist ein Euro verloren gegangen.", "A euro was lost when handing back the money."), t("Die Freunde haben eigentlich 31 € gezahlt.", "The friends actually paid €31.")),
            t("Kernidee: Verfolge, wo das Geld wirklich ist: beim Wirt, beim Pagen und bei den Freunden.", "Key idea: follow where the money actually is: with the manager, the porter and the friends."),
            t("Darf man die 2 € des Pagen zu den 27 € addieren, oder sind sie schon darin enthalten?", "Is it right to add the porter's €2 to the €27, or is it already included?"),
            t("Wirt 25 €, Page 2 €, Freunde 3 €: Das sind 30 €. Die Rechnung im Rätsel addiert eine Zahl, die man hätte abziehen müssen.", "Manager €25, porter €2, friends €3: that's €30. The sum in the puzzle adds a number that should have been subtracted."),
            t("Das Rätsel funktioniert, weil die Erzählung ganz selbstverständlich eine falsche Rechnung vorgibt, die nach Buchhaltung klingt. Man prüft dann nur noch, ob 27 + 2 = 29 stimmt, und nicht, ob die Addition überhaupt Sinn ergibt. Bei Zahlen in Nachrichten lohnt sich dieselbe Frage: Was wird hier eigentlich mit was verglichen?", "The puzzle works because the story casually sets up a wrong calculation that sounds like bookkeeping. You then only check whether 27 + 2 = 29 is right, not whether the addition makes sense at all. With numbers in the news the same question is worth asking: what is actually being compared with what?"),
        ),
        lab("logik.nim", Topic.Logic, M, 0.55f) {
            val heap = pick(21, 22, 23, 25, 30, 31, 17)
            val take = heap % 4
            task(
                question = l("Auf dem Tisch liegen ${heap} Streichhölzer. Ihr nehmt abwechselnd 1, 2 oder 3 weg. Wer das letzte nimmt, gewinnt. Du fängst an. Wie viele musst du jetzt nehmen, um mit der richtigen Strategie sicher zu gewinnen?", "There are ${heap} matches on the table. You take turns removing 1, 2 or 3. Whoever takes the last one wins. You go first. How many must you take now to be sure of winning with the right strategy?"),
                steps = listOf(num(l("Streichhölzer", "Matches"), take.toDouble(), "", abs = 0.0, done = "$take",
                    hints = listOf(l("Kernidee: Denk vom Ende her. Bleiben dem Gegner genau 4, verliert er: Was er auch nimmt, du nimmst den Rest.", "Key idea: think from the end. If your opponent is left with exactly 4, they lose: whatever they take, you take the rest."), l("Dasselbe gilt für 8, 12, 16 … Hinterlasse immer ein Vielfaches von 4.", "The same holds for 8, 12, 16 … Always leave a multiple of 4.")))),
                solution = listOf(l("${heap} = ${heap / 4} · 4 + ${take}. Nimm ${take}, dann liegen ${heap - take} da. Ab jetzt ergänzt du den Zug des Gegners immer auf 4.", "${heap} = ${heap / 4} · 4 + ${take}. Take ${take}, leaving ${heap - take}. From then on you always make your opponent's move up to 4.")),
                background = l("Solche Spiele löst man durch Rückwärtsdenken: Man markiert alle Stellungen, aus denen man sicher gewinnt, beginnend beim Spielende. Bei 1, 2 oder 3 Hölzern sind das alle außer den Vielfachen von 4. Die allgemeine Theorie solcher Spiele, Nim genannt, wurde 1901 vollständig gelöst und ist Grundlage der kombinatorischen Spieltheorie.", "Games like this are solved by thinking backwards: you mark all the positions from which you're sure to win, starting from the end of the game. With 1, 2 or 3 matches these are all except multiples of 4. The general theory of such games, called Nim, was completely solved in 1901 and is the foundation of combinatorial game theory."),
            )
        },
        // ------------------------------------------------ schwer
        lab("logik.bruecke", Topic.Logic, H, 0.65f) {
            val (a, b, c, d) = pick(listOf(1, 2, 5, 10), listOf(1, 3, 4, 5), listOf(2, 3, 5, 8), listOf(1, 2, 7, 10))
            val best = minOf(a + 3 * b + d, 2 * a + b + c + d)
            task(
                question = l("Vier Leute müssen nachts über eine wackelige Brücke. Sie haben eine einzige Taschenlampe, und höchstens zwei passen gleichzeitig auf die Brücke. Allein bräuchten sie ${a}, ${b}, ${c} und ${d} Minuten. Zu zweit gehen sie im Tempo des Langsameren. Die Lampe muss jedes Mal zurückgebracht werden. Wie viele Minuten brauchen alle zusammen mindestens?", "Four people must cross a rickety bridge at night. They have a single torch, and at most two fit on the bridge at once. Alone they would take ${a}, ${b}, ${c} and ${d} minutes. In pairs they walk at the slower one's pace. The torch must be brought back each time. What is the least number of minutes they need altogether?"),
                steps = listOf(num(l("Minuten", "Minutes"), best.toDouble(), "", abs = 0.0, done = l("$best Minuten", "$best minutes"),
                    hints = listOf(l("Kernidee: Vergleiche zwei Strategien. A: Der Schnellste begleitet jeden einzeln. B: Die beiden Langsamsten gehen zusammen, damit ihre Zeiten nur einmal zählen.", "Key idea: compare two strategies. A: the fastest escorts everyone individually. B: the two slowest go together so their times only count once."),
                        """§A = 2 \cdot ${a} + ${b} + ${c} + ${d},\quad B = ${a} + 3 \cdot ${b} + ${d}§"""))),
                solution = listOf(l("Strategie A (der Schnellste pendelt): ${2 * a + b + c + d} Minuten. Strategie B (${a} und ${b} rüber, ${a} zurück, ${c} und ${d} rüber, ${b} zurück, ${a} und ${b} rüber): ${a + 3 * b + d} Minuten. Am besten: ${best}.", "Strategy A (the fastest shuttles): ${2 * a + b + c + d} minutes. Strategy B (${a} and ${b} across, ${a} back, ${c} and ${d} across, ${b} back, ${a} and ${b} across): ${a + 3 * b + d} minutes. Best: ${best}.")),
                background = l("Die naheliegende Idee, den Schnellsten immer pendeln zu lassen, ist nicht immer die beste. Wenn die beiden Langsamsten viel langsamer sind als der Rest, lohnt es sich, sie zusammen gehen zu lassen. Welche Strategie gewinnt, hängt von den Zahlen ab: B ist besser, wenn 2·${b} kleiner ist als ${a} + ${c}. Gute Optimierung vergleicht Strategien statt einer Faustregel zu folgen.", "The obvious idea of always letting the fastest shuttle back and forth isn't always the best. If the two slowest are much slower than the rest, it pays to let them go together. Which strategy wins depends on the numbers: B is better if 2·${b} is less than ${a} + ${c}. Good optimisation compares strategies instead of following a rule of thumb."),
            )
        },
        lab("logik.hunderttueren", Topic.Logic, H, 0.65f) {
            val nn = pick(100, 50, 200, 30)
            val open = sqrt(nn.toDouble()).toInt()
            task(
                question = l("In einem Flur sind ${nn} geschlossene Türen, nummeriert von 1 bis ${nn}. Person 1 öffnet jede Tür. Person 2 schaltet jede zweite Tür um (offen ↔ zu), Person 3 jede dritte, und so weiter bis Person ${nn}. Wie viele Türen sind am Ende offen?", "In a corridor there are ${nn} closed doors, numbered 1 to ${nn}. Person 1 opens every door. Person 2 toggles every second door (open ↔ closed), person 3 every third, and so on up to person ${nn}. How many doors are open at the end?"),
                steps = listOf(num(l("Offene Türen", "Open doors"), open.toDouble(), "", abs = 0.0, done = "$open",
                    hints = listOf(l("Kernidee: Tür Nummer t wird genau so oft umgeschaltet, wie t Teiler hat. Offen bleibt sie bei ungerade vielen Teilern.", "Key idea: door number t is toggled exactly as many times as t has divisors. It ends up open if it has an odd number of divisors."), l("Teiler kommen paarweise: 12 = 1·12 = 2·6 = 3·4. Wann bleibt einer ohne Partner?", "Divisors come in pairs: 12 = 1·12 = 2·6 = 3·4. When is one left without a partner?")))),
                solution = listOf(l("Nur Quadratzahlen haben ungerade viele Teiler, weil bei ihnen ein Teiler mit sich selbst gepaart ist (36 = 6·6). Bis ${nn} gibt es ${open} Quadratzahlen: 1, 4, 9, … , ${open * open}.", "Only square numbers have an odd number of divisors, because one divisor is paired with itself (36 = 6·6). Up to ${nn} there are ${open} square numbers: 1, 4, 9, … , ${open * open}.")),
                background = l("Man könnte die Aufgabe stur durchspielen, aber die Einsicht über Teilerpaare löst sie für jede Zahl von Türen auf einen Schlag. Das ist typisch für gute Mathematik: Statt zu rechnen, findet man die Struktur, die hinter dem Muster steckt. Danach sieht die Lösung fast selbstverständlich aus.", "You could stubbornly play the task through, but the insight about pairs of divisors solves it for any number of doors in one go. That's typical of good mathematics: instead of calculating, you find the structure behind the pattern. Afterwards the solution looks almost obvious."),
            )
        },
        mc("logik.wason", Topic.Logic, H,
            t("Vier Karten liegen auf dem Tisch. Jede hat auf einer Seite einen Buchstaben und auf der anderen eine Zahl. Du siehst: E, K, 4 und 7. Regel: „Wenn auf einer Seite ein Vokal steht, steht auf der anderen eine gerade Zahl.“ Welche Karten musst du mindestens umdrehen, um zu prüfen, ob die Regel stimmt?", "Four cards lie on the table. Each has a letter on one side and a number on the other. You see: E, K, 4 and 7. Rule: 'If there is a vowel on one side, there is an even number on the other.' Which cards must you turn over, at the least, to check whether the rule holds?"),
            t("Umdrehen", "Turn over"),
            t("E und 7", "E and 7"),
            listOf(t("E und 4", "E and 4"), t("Nur E", "Only E"), t("E, 4 und 7", "E, 4 and 7")),
            t("Kernidee: Welche Karte könnte die Regel brechen? Gesucht ist ein Vokal mit einer ungeraden Zahl.", "Key idea: which card could break the rule? You're looking for a vowel with an odd number."),
            t("Hinter der 4 darf alles stehen, die Regel sagt nichts über Konsonanten. Aber was, wenn hinter der 7 ein Vokal ist?", "Anything can be behind the 4; the rule says nothing about consonants. But what if there's a vowel behind the 7?"),
            t("E muss eine gerade Zahl haben, also umdrehen. Hinter der 7 darf kein Vokal stehen, also auch umdrehen. K und 4 können die Regel nicht verletzen.", "E must have an even number, so turn it over. There must be no vowel behind the 7, so turn that over too. K and 4 can't break the rule."),
            t("Das ist die Wason-Aufgabe von 1966. Weniger als ein Zehntel der Menschen lösen sie richtig, die meisten wählen E und 4. Wir suchen intuitiv nach Bestätigung statt nach dem, was eine Regel widerlegen könnte. Verpackt man dieselbe Logik als Alltagsregel, etwa „Wer Bier trinkt, muss über 18 sein“, lösen sie fast alle.", "This is the Wason selection task from 1966. Fewer than one in ten people solve it correctly; most choose E and 4. We intuitively look for confirmation rather than for what could disprove a rule. Wrap the same logic in an everyday rule, such as 'Anyone drinking beer must be over 18', and almost everyone solves it."),
        ),
        lab("logik.fliege", Topic.Logic, H, 0.6f) {
            val dist = pick(100.0, 120.0, 60.0); val v = pick(50.0, 40.0, 30.0); val vf = pick(75.0, 100.0, 90.0)
            val tt = dist / (2 * v); val s = vf * tt
            task(
                question = l("Zwei Züge stehen ${n(dist, 0)} km voneinander entfernt auf demselben Gleis und fahren mit je ${n(v, 0)} km/h aufeinander zu. Eine Fliege startet am ersten Zug und fliegt mit ${n(vf, 0)} km/h zum zweiten, kehrt sofort um, fliegt zurück, und so weiter, bis die Züge sich treffen. Wie viele Kilometer fliegt sie insgesamt?", "Two trains are ${n(dist, 0)} km apart on the same track and travel towards each other at ${n(v, 0)} km/h each. A fly starts at the first train and flies at ${n(vf, 0)} km/h to the second, turns round at once, flies back, and so on until the trains meet. How many kilometres does it fly altogether?"),
                steps = listOf(num(l("Flugstrecke in km", "Flight distance in km"), s, "km", rel = 0.01, done = """§${m(s, 0)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Rechne nicht die unendlich vielen Teilstrecken aus. Frag stattdessen: Wie lange fliegt die Fliege?", "Key idea: don't work out the infinitely many legs. Instead ask: how long does the fly fly for?"), """§t = \dfrac{${m(dist, 0)}\ \text{km}}{2 \cdot ${m(v, 0)}\ \text{km/h}}§"""))),
                solution = listOf("""§t = ${m(tt, 2)}\ \text{h},\quad s = ${m(vf, 0)}\ \tfrac{\text{km}}{\text{h}} \cdot ${m(tt, 2)}\ \text{h} = ${m(s, 0)}\ \text{km}§"""),
                background = l("Man erzählt, John von Neumann habe diese Frage sofort richtig beantwortet. Als jemand sagte, die meisten würden die unendliche Reihe aufsummieren, antwortete er verwirrt: „Aber genau so habe ich es gemacht.“ Ob wahr oder nicht: Der elegante Weg wechselt die Perspektive vom Weg zur Zeit, und die Reihe verschwindet.", "It's said that John von Neumann answered this question correctly at once. When someone said most people would sum the infinite series, he replied, puzzled: 'But that's exactly what I did.' True or not: the elegant route switches perspective from distance to time, and the series disappears."),
            )
        },
        mc("logik.domino", Topic.Logic, H,
            t("Von einem Schachbrett (8 × 8) werden zwei gegenüberliegende Eckfelder abgesägt. Es bleiben 62 Felder. Kann man diese mit 31 Dominosteinen, die jeweils genau zwei benachbarte Felder bedecken, lückenlos auslegen?", "Two opposite corner squares are sawn off a chessboard (8 × 8). That leaves 62 squares. Can they be covered completely with 31 dominoes, each covering exactly two neighbouring squares?"),
            t("Antwort", "Answer"),
            t("Nein, nie.", "No, never."),
            listOf(t("Ja, mit etwas Geschick.", "Yes, with a little skill."), t("Ja, aber nur auf eine einzige Art.", "Yes, but only in a single way."), t("Nur, wenn man einige Steine diagonal legt.", "Only if you lay some dominoes diagonally.")),
            t("Kernidee: Schau auf die Farben. Welche Farben deckt ein Dominostein immer ab?", "Key idea: look at the colours. Which colours does a domino always cover?"),
            t("Gegenüberliegende Ecken haben dieselbe Farbe.", "Opposite corners have the same colour."),
            t("Jeder Stein deckt ein weißes und ein schwarzes Feld. 31 Steine bräuchten also 31 weiße und 31 schwarze. Weil beide Ecken dieselbe Farbe haben, bleiben aber 32 von der einen und 30 von der anderen.", "Every domino covers one white and one black square. So 31 dominoes would need 31 white and 31 black. But because both corners have the same colour, 32 of one colour and 30 of the other remain."),
            t("Man könnte stundenlang probieren, ohne sicher zu sein, dass es nicht geht. Das Färbungsargument beweist es in einem Satz. Solche Invarianten, Größen, die sich bei keinem erlaubten Schritt ändern, sind ein Standardtrick, um zu zeigen, dass etwas unmöglich ist. Beim Zauberwürfel beweist man damit, dass manche Stellungen nie erreichbar sind.", "You could try for hours without being sure it can't be done. The colouring argument proves it in one sentence. Such invariants, quantities that don't change with any allowed step, are a standard trick for showing that something is impossible. With the Rubik's cube they're used to prove that some positions can never be reached."),
        ),
        mc("logik.gewichte", Topic.Logic, H,
            t("Ein Händler hat eine Balkenwaage und will mit nur vier Gewichtsstücken jede ganze Kilozahl von 1 bis 40 abwiegen können. Er darf Gewichte in beide Schalen legen. Welche vier Gewichte braucht er?", "A trader has a balance scale and wants to be able to weigh every whole number of kilograms from 1 to 40 using only four weights. He may put weights in both pans. Which four weights does he need?"),
            t("Gewichte", "Weights"),
            t("1, 3, 9 und 27 kg", "1, 3, 9 and 27 kg"),
            listOf(t("1, 2, 4 und 8 kg", "1, 2, 4 and 8 kg"), t("1, 5, 10 und 24 kg", "1, 5, 10 and 24 kg"), t("2, 4, 10 und 24 kg", "2, 4, 10 and 24 kg")),
            t("Kernidee: Jedes Gewicht hat drei Möglichkeiten: in der linken Schale, in der rechten oder gar nicht auf der Waage.", "Key idea: each weight has three options: in the left pan, in the right pan, or not on the scale at all."),
            t("Mit 1 und 3 bekommt man 1, 2 (= 3 − 1), 3 und 4. Welches Gewicht kommt als Nächstes, damit es bei 5 weitergeht?", "With 1 and 3 you get 1, 2 (= 3 − 1), 3 and 4. Which weight comes next so you can continue at 5?"),
            t("Mit Potenzen von 3 lässt sich jede Zahl bis 1 + 3 + 9 + 27 = 40 als Summe und Differenz darstellen, zum Beispiel 5 = 9 − 3 − 1 und 20 = 27 − 9 + 3 − 1.", "With powers of 3, every number up to 1 + 3 + 9 + 27 = 40 can be written as a sum and difference, for example 5 = 9 − 3 − 1 and 20 = 27 − 9 + 3 − 1."),
            t("Dieses Problem stellte Claude-Gaspard Bachet 1612. Darf man nur in eine Schale legen, sind Zweierpotenzen am besten, mit 1, 2, 4 und 8 schafft man aber nur bis 15. Mit beiden Schalen hat jedes Gewicht drei Zustände, und es entsteht das ausgeglichene Dreiersystem. In den 1950er-Jahren baute man in der Sowjetunion sogar einen Computer, der damit rechnete.", "Claude-Gaspard Bachet posed this problem in 1612. If you can only use one pan, powers of 2 are best, but with 1, 2, 4 and 8 you only get up to 15. With both pans each weight has three states, giving rise to the balanced ternary system. In the 1950s a computer was even built in the Soviet Union that calculated with it."),
        ),
        mc("logik.waechter", Topic.Logic, H,
            t("Zwei Türen: Eine führt in die Freiheit, die andere zu einem Drachen. Vor ihnen stehen zwei Wächter. Einer lügt immer, der andere sagt immer die Wahrheit, aber du weißt nicht, wer wer ist. Du darfst einem von ihnen genau eine Frage stellen. Was fragst du?", "Two doors: one leads to freedom, the other to a dragon. In front of them stand two guards. One always lies, the other always tells the truth, but you don't know which is which. You may ask one of them exactly one question. What do you ask?"),
            t("Frage", "Question"),
            t("„Welche Tür würde mir der andere Wächter als Weg in die Freiheit zeigen?“ und dann die andere Tür nehmen.", "'Which door would the other guard show me as the way to freedom?' and then take the other door."),
            listOf(t("„Bist du der ehrliche Wächter?“", "'Are you the honest guard?'"), t("„Welche Tür führt in die Freiheit?“ und dann diese Tür nehmen.", "'Which door leads to freedom?' and then take that door."), t("„Welche Tür würde der andere zeigen?“ und dann diese Tür nehmen.", "'Which door would the other one show?' and then take that door.")),
            t("Kernidee: Baue eine Frage, bei der die Antwort durch genau eine Lüge geht, egal wen du fragst.", "Key idea: build a question in which the answer passes through exactly one lie, whoever you ask."),
            t("Fragst du den Ehrlichen nach dem Lügner, gibt er die Lüge ehrlich weiter. Fragst du den Lügner nach dem Ehrlichen, verdreht er die Wahrheit.", "If you ask the honest one about the liar, he honestly passes on the lie. If you ask the liar about the honest one, he twists the truth."),
            t("In beiden Fällen enthält die Antwort genau eine Lüge und zeigt auf die falsche Tür. Also nimmst du die andere.", "In both cases the answer contains exactly one lie and points to the wrong door. So you take the other one."),
            t("Der Trick ist, die Unsicherheit über die Wächter herauszukürzen: Eine Lüge plus eine Wahrheit ergibt immer eine Lüge, zwei Lügen würden sich aufheben. Dasselbe Prinzip nutzt man in der Elektronik, wenn man ein Signal zweimal umkehrt, und in der Logik bei doppelter Verneinung.", "The trick is to cancel out the uncertainty about the guards: one lie plus one truth always gives a lie, while two lies would cancel out. The same principle is used in electronics when a signal is inverted twice, and in logic with double negation."),
        ),
        // ------------------------------------------------ Experte
        mc("logik.dreikinder", Topic.Logic, X,
            t("Ein Bote fragt eine Mutter nach dem Alter ihrer drei Kinder (ganze Jahre). Sie sagt: „Das Produkt ist 36.“ Er: „Das reicht nicht.“ Sie: „Die Summe ist die Hausnummer gegenüber.“ Er schaut hin und sagt: „Das reicht immer noch nicht.“ Sie: „Mein ältestes Kind spielt Klavier.“ Jetzt weiß er es. Wie alt sind die Kinder?", "A messenger asks a mother the ages of her three children (whole years). She says: 'The product is 36.' He: 'That's not enough.' She: 'The sum is the house number opposite.' He looks and says: 'Still not enough.' She: 'My eldest child plays the piano.' Now he knows. How old are the children?"),
            t("Alter", "Ages"),
            t("9, 2 und 2", "9, 2 and 2"),
            listOf(t("6, 6 und 1", "6, 6 and 1"), t("4, 3 und 3", "4, 3 and 3"), t("9, 4 und 1", "9, 4 and 1")),
            t("Kernidee: Schreib alle Dreiergruppen mit Produkt 36 und ihre Summen auf.", "Key idea: write down all the triples with product 36 and their sums."),
            t("Warum reicht die Hausnummer nicht? Nur wenn zwei Gruppen dieselbe Summe haben.", "Why isn't the house number enough? Only if two triples have the same sum."),
            t("Die einzige doppelte Summe ist 13: 1 + 6 + 6 und 2 + 2 + 9. Bei 6, 6, 1 gäbe es kein einzelnes ältestes Kind, also 9, 2 und 2.", "The only repeated sum is 13: 1 + 6 + 6 and 2 + 2 + 9. With 6, 6, 1 there would be no single eldest child, so 9, 2 and 2."),
            t("Das Raffinierte: Die Hausnummer erfährst du nie, und trotzdem hilft sie. Dass der Bote mit ihr nichts anfangen kann, verrät, dass es die einzige mehrdeutige Summe sein muss. Solche Rätsel trainieren, aus dem Wissen und Nichtwissen anderer Schlüsse zu ziehen, eine Grundidee der epistemischen Logik.", "The clever part: you never learn the house number, and yet it helps. That the messenger can't use it reveals that it must be the only ambiguous sum. Puzzles like this train you to draw conclusions from what others know and don't know, a basic idea of epistemic logic."),
        ),
    )
}
