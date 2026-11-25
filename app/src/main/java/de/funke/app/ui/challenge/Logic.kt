package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import de.funke.app.ui.tr
import kotlin.random.Random

/*
 * Logikrätsel, selbst erzeugt bzw. selbst geschrieben (frei verwendbar).
 */
object Logic {
    private val NamesDe = listOf("Mia", "Paul", "Lea", "Ben", "Emma", "Noah", "Lina", "Finn", "Ida", "Tom", "Ella", "Jonas")
    private val NamesEn = listOf("Mia", "Paul", "Lea", "Ben", "Emma", "Noah", "Lina", "Finn", "Ida", "Tom", "Ella", "Jonas")

    fun make(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(4)) {
            0 -> order(d, r)
            1 -> letters(d, r)
            2 -> socks(d, r)
            else -> clock(d, r)
        }
        Difficulty.Medium -> when (r.nextInt(4)) {
            0 -> handshakes(d, r)
            1 -> ages(d, r)
            2 -> snail(d, r)
            else -> liars(d, r)
        }
        else -> when (r.nextInt(5)) {
            0 -> weighing(d, r)
            1 -> arrangements(d, r)
            2 -> codes(d, r)
            3 -> syllogism(d, r)
            else -> liars(d, r)
        }
    }

    private fun names(r: Random, n: Int) = (if (Lang.german) NamesDe else NamesEn).shuffled(r).take(n)

    /** Wer ist am größten/kleinsten? */
    private fun order(d: Difficulty, r: Random): Challenge {
        val (a, b, c) = names(r, 3)
        val askSmallest = r.nextBoolean()
        val q = tr("$a ist größer als $b. $b ist größer als $c. ", "$a is taller than $b. $b is taller than $c. ") +
            if (askSmallest) tr("Wer ist am kleinsten?", "Who is the shortest?") else tr("Wer ist am größten?", "Who is the tallest?")
        val ans = if (askSmallest) c else a
        return choice(
            Topic.Logic, d, q, ans, listOf(a, b, c, tr("Kann man nicht sagen", "Can't tell")).filter { it != ans },
            listOf(
                tr("Ordne der Größe nach: $a > $b > $c.", "Put them in order: $a > $b > $c."),
                if (askSmallest) tr("Ganz rechts steht $c.", "$c is at the end.") else tr("Ganz vorne steht $a.", "$a is at the front."),
            ),
            r,
        )
    }

    /** Buchstabenfolge */
    private fun letters(d: Difficulty, r: Random): Challenge {
        val step = r.nextInt(2, 5)
        val start = r.nextInt(0, 26 - 5 * step)
        val seq = List(4) { ('A' + start + it * step) }
        val next = 'A' + start + 4 * step
        val wrong = listOf(next - 1, next + 1, next + step).filter { it in 'A'..'Z' }.map { it.toString() }
        return choice(
            Topic.Logic, d,
            tr("Welcher Buchstabe kommt als Nächstes?", "Which letter comes next?") + "\n" + seq.joinToString(", ") + ", ?",
            next.toString(), wrong,
            listOf(
                tr("Zähle im Alphabet: Es geht jedes Mal $step Buchstaben weiter.", "Count in the alphabet: each time it moves $step letters on."),
                tr("Nach ${seq.last()} kommt also $next.", "So after ${seq.last()} comes $next."),
            ),
            r,
        )
    }

    /** Schubfachprinzip mit Socken */
    private fun socks(d: Difficulty, r: Random): Challenge {
        val k = r.nextInt(2, 7)
        return task(
            Topic.Logic, d,
            tr(
                "In einer dunklen Schublade liegen Socken in $k Farben, von jeder Farbe viele. Wie viele musst du mindestens ziehen, um sicher zwei gleiche zu haben?",
                "A dark drawer holds socks in $k colors, lots of each. How many must you take at least to be sure of a matching pair?",
            ),
            k + 1,
            steps = listOf(
                tr("Schubfachprinzip: Im schlimmsten Fall ziehst du erst von jeder Farbe eine, das sind $k.", "Pigeonhole principle: in the worst case you first get one of each color, that is $k."),
                tr("Die nächste Socke passt dann sicher zu einer davon: $k + 1 = ${k + 1}", "The next sock must match one of them: $k + 1 = ${k + 1}"),
            ),
        )
    }

    /** Uhrzeit: wie viele Minuten bis …? */
    private fun clock(d: Difficulty, r: Random): Challenge {
        val h = r.nextInt(7, 20)
        val m = 5 * r.nextInt(0, 12)
        val add = 5 * r.nextInt(5, 25)
        val endTotal = h * 60 + m + add
        val eh = endTotal / 60
        val em = endTotal % 60
        fun t(hh: Int, mm: Int) = "$hh:" + mm.toString().padStart(2, '0')
        val toFull = (60 - m) % 60
        return task(
            Topic.Logic, d,
            tr("Es ist ${t(h, m)} Uhr. Wie viele Minuten sind es bis ${t(eh, em)} Uhr?", "It is ${t(h, m)}. How many minutes until ${t(eh, em)}?"),
            add, "min",
            steps = if (toFull in 1 until add && em < add) listOf(
                tr("Bis zur vollen Stunde ${t(h + 1, 0)}: $toFull min", "Up to the full hour ${t(h + 1, 0)}: $toFull min"),
                tr("Dann noch ${add - toFull} min bis ${t(eh, em)}.", "Then another ${add - toFull} min until ${t(eh, em)}."),
                "$toFull + ${add - toFull} = $add min",
            ) else listOf(
                tr("Rechne die Minuten von ${t(h, m)} bis ${t(eh, em)} zusammen.", "Add up the minutes from ${t(h, m)} to ${t(eh, em)}."),
                "${eh * 60 + em} − ${h * 60 + m} = $add min",
            ),
        )
    }

    /** Händeschütteln: jeder mit jedem */
    private fun handshakes(d: Difficulty, r: Random): Challenge {
        val n = r.nextInt(4, 13)
        return task(
            Topic.Logic, d,
            tr("Bei einem Treffen gibt jede der $n Personen jeder anderen genau einmal die Hand. Wie oft werden Hände geschüttelt?", "At a meeting each of the $n people shakes hands with every other person exactly once. How many handshakes are there?"),
            n * (n - 1) / 2,
            steps = listOf(
                tr("Jede Person gibt ${n - 1} anderen die Hand: $n · ${n - 1} = ${n * (n - 1)}", "Each person shakes ${n - 1} hands: $n · ${n - 1} = ${n * (n - 1)}"),
                tr("Dabei ist jeder Handschlag doppelt gezählt (A mit B = B mit A), also durch 2. Paare: n · (n − 1) : 2", "Each handshake was counted twice (A with B = B with A), so divide by 2. Pairs: n · (n − 1) : 2"),
                "${n * (n - 1)} : 2 = ${n * (n - 1) / 2}",
            ),
        )
    }

    /** Altersrätsel */
    private fun ages(d: Difficulty, r: Random): Challenge {
        val (big, small) = names(r, 2)
        return when (r.nextInt(3)) {
            0 -> {
                val t = r.nextInt(2, 13)
                task(
                    Topic.Logic, d,
                    tr("$big ist heute 3-mal so alt wie $small. In $t Jahren ist $big nur noch doppelt so alt. Wie alt ist $small heute?", "$big is 3 times as old as $small today. In $t years $big will only be twice as old. How old is $small today?"),
                    t, tr("Jahre", "years"),
                    steps = listOf(
                        tr("$small ist x Jahre alt, $big also 3x.", "$small is x years old, so $big is 3x."),
                        tr("In $t Jahren: 3x + $t = 2 · (x + $t)", "In $t years: 3x + $t = 2 · (x + $t)"),
                        tr("3x + $t = 2x + ${2 * t}  →  x = $t", "3x + $t = 2x + ${2 * t}  →  x = $t"),
                    ),
                )
            }
            1 -> {
                val y = r.nextInt(1, 7)
                val t = 2 * y
                task(
                    Topic.Logic, d,
                    tr("$big ist heute 4-mal so alt wie $small. In $y Jahren ist $big nur noch 3-mal so alt. Wie alt ist $small heute?", "$big is 4 times as old as $small today. In $y years $big will only be 3 times as old. How old is $small today?"),
                    t, tr("Jahre", "years"),
                    steps = listOf(
                        tr("$small ist x Jahre alt, $big also 4x.", "$small is x years old, so $big is 4x."),
                        tr("In $y Jahren: 4x + $y = 3 · (x + $y)", "In $y years: 4x + $y = 3 · (x + $y)"),
                        tr("4x + $y = 3x + ${3 * y}  →  x = $t", "4x + $y = 3x + ${3 * y}  →  x = $t"),
                    ),
                )
            }
            else -> {
                val t = r.nextInt(1, 6)
                val y = 3 * t
                task(
                    Topic.Logic, d,
                    tr("$big ist heute 5-mal so alt wie $small. In $y Jahren ist $big nur noch doppelt so alt. Wie alt ist $small heute?", "$big is 5 times as old as $small today. In $y years $big will only be twice as old. How old is $small today?"),
                    t, tr("Jahre", "years"),
                    steps = listOf(
                        tr("$small ist x Jahre alt, $big also 5x.", "$small is x years old, so $big is 5x."),
                        tr("In $y Jahren: 5x + $y = 2 · (x + $y)", "In $y years: 5x + $y = 2 · (x + $y)"),
                        tr("5x + $y = 2x + ${2 * y}  →  3x = $y  →  x = $t", "5x + $y = 2x + ${2 * y}  →  3x = $y  →  x = $t"),
                    ),
                )
            }
        }
    }

    /** Schnecke im Brunnen */
    private fun snail(d: Difficulty, r: Random): Challenge {
        val up = r.nextInt(3, 7)
        val down = r.nextInt(1, up)
        val days = r.nextInt(4, 12)
        val h = up + (days - 1) * (up - down)
        return task(
            Topic.Logic, d,
            tr(
                "Eine Schnecke sitzt unten in einem $h m tiefen Brunnen. Tagsüber kriecht sie $up m hoch, nachts rutscht sie $down m zurück. Am wievielten Tag ist sie oben?",
                "A snail sits at the bottom of a $h m deep well. By day it climbs $up m, at night it slides back $down m. On which day does it reach the top?",
            ),
            days,
            steps = listOf(
                tr("Pro Tag und Nacht kommt sie ${up - down} m weiter.", "Each day and night it gains ${up - down} m."),
                tr("Vorsicht: Am letzten Tag kriecht sie $up m und ist draußen, bevor sie zurückrutscht. Sie muss also erst ${h - up} m schaffen.", "Careful: on the last day it climbs $up m and is out before sliding back. So it first needs to reach ${h - up} m."),
                tr("${h - up} : ${up - down} = ${days - 1} Tage, dann noch der letzte Tag: ${days - 1} + 1 = $days", "${h - up} : ${up - down} = ${days - 1} days, then the last day: ${days - 1} + 1 = $days"),
            ),
        )
    }

    /** Wie oft wiegen? */
    private fun weighing(d: Difficulty, r: Random): Challenge {
        val n = listOf(3, 4, 8, 9, 10, 12, 20, 26, 27, 28, 50, 80, 81).random(r)
        var k = 0
        var p = 1
        while (p < n) { p *= 3; k++ }
        return task(
            Topic.Logic, d,
            tr("Unter $n gleich aussehenden Kugeln ist genau eine schwerer. Wie oft musst du mit einer Balkenwaage mindestens wiegen, um sie sicher zu finden?", "Among $n identical-looking balls exactly one is heavier. How many weighings on a balance scale do you need at least to find it for sure?"),
            k,
            steps = listOf(
                tr("Jedes Wiegen hat 3 Ausgänge: links schwerer, rechts schwerer oder gleich. Teile die Kugeln also in 3 Gruppen.", "Each weighing has 3 outcomes: left heavier, right heavier or equal. So split the balls into 3 groups."),
                tr("Mit k Wiegungen schafft man bis zu 3^k Kugeln: " + (1..k).joinToString(", ") { "3^$it = ${pow3(it)}" }, "With k weighings you can handle up to 3^k balls: " + (1..k).joinToString(", ") { "3^$it = ${pow3(it)}" }),
                tr("$n Kugeln brauchen also $k Wiegungen.", "So $n balls need $k weighings."),
            ),
        )
    }

    private fun pow3(k: Int): Int { var p = 1; repeat(k) { p *= 3 }; return p }

    /** Anordnungen n! */
    private fun arrangements(d: Difficulty, r: Random): Challenge {
        val n = r.nextInt(3, 7)
        var f = 1
        for (i in 2..n) f *= i
        return task(
            Topic.Logic, d,
            tr("Auf wie viele verschiedene Arten kann man $n verschiedene Bücher nebeneinander ins Regal stellen?", "In how many different ways can you line up $n different books on a shelf?"),
            f,
            steps = listOf(
                tr("Für den ersten Platz gibt es $n Möglichkeiten, für den zweiten noch ${n - 1}, und so weiter.", "There are $n choices for the first spot, ${n - 1} for the second, and so on."),
                tr("Fakultät: n! = " + (n downTo 1).joinToString(" · "), "Factorial: n! = " + (n downTo 1).joinToString(" · ")),
                "$n! = $f",
            ),
        )
    }

    /** Zahlencodes */
    private fun codes(d: Difficulty, r: Random): Challenge = if (r.nextBoolean()) {
        val n = r.nextInt(4, 8)
        val k = 3
        val v = n * (n - 1) * (n - 2)
        task(
            Topic.Logic, d,
            tr("Wie viele dreistellige Codes kann man aus den Ziffern 1 bis $n bilden, wenn keine Ziffer doppelt vorkommen darf?", "How many three-digit codes can you make from the digits 1 to $n if no digit may repeat?"),
            v,
            steps = listOf(
                tr("Für die erste Stelle gibt es $n Ziffern, für die zweite noch ${n - 1}, für die dritte ${n - 2}.", "There are $n digits for the first place, ${n - 1} for the second and ${n - 2} for the third."),
                "$n · ${n - 1} · ${n - 2} = $v",
            ),
        )
    } else {
        val k = r.nextInt(2, 5)
        val base = listOf(2, 3, 4, 5).random(r)
        var v = 1
        repeat(k) { v *= base }
        task(
            Topic.Logic, d,
            tr("Ein Schloss hat $k Rädchen, jedes mit $base Symbolen. Wie viele verschiedene Einstellungen gibt es?", "A lock has $k dials with $base symbols each. How many different settings are there?"),
            v,
            steps = listOf(
                tr("Jedes Rädchen kann unabhängig $base Symbole zeigen, also $base · $base · … ($k-mal).", "Each dial can independently show $base symbols, so $base · $base · … ($k times)."),
                "$base^$k = $v",
            ),
        )
    }

    /** Folgt das wirklich? (mit erfundenen Wörtern, damit nur die Logik zählt) */
    private fun syllogism(d: Difficulty, r: Random): Challenge {
        val words = listOf("Blorks", "Zumpel", "Fipse", "Gnurze", "Quabbel", "Trilfe").shuffled(r)
        val (a, b) = words
        val col = listOf(tr("grün", "green"), tr("laut", "loud"), tr("klein", "small"), tr("flauschig", "fluffy")).random(r)
        val yes = tr("Ja, das folgt sicher", "Yes, that follows for sure")
        val no = tr("Nein, das folgt nicht", "No, that doesn't follow")
        val never = tr("Nein, das Gegenteil folgt", "No, the opposite follows")
        return when (r.nextInt(3)) {
            0 -> choice(
                Topic.Logic, d,
                tr("Alle $a sind $b. Alle $b sind $col. Sind also alle $a $col?", "All $a are $b. All $b are $col. So are all $a $col?"),
                yes, listOf(no, never),
                listOf(
                    tr("Stell es dir als Kreise vor: Der $a-Kreis liegt im $b-Kreis, und der liegt ganz im Kreis „$col“.", "Picture circles: the $a circle lies inside the $b circle, which lies fully inside the “$col” circle."),
                    tr("Also liegen auch alle $a darin.", "So all $a lie inside it too."),
                ),
                r,
            )
            1 -> choice(
                Topic.Logic, d,
                tr("Alle $a sind $b. Manche $b sind $col. Sind also manche $a $col?", "All $a are $b. Some $b are $col. So are some $a $col?"),
                no, listOf(yes, never),
                listOf(
                    tr("Die „$col“-en $b könnten genau die sein, die keine $a sind.", "The $col $b might be exactly the ones that aren't $a."),
                    tr("Es kann sein, muss aber nicht. Also folgt es nicht sicher.", "It might be true but doesn't have to be. So it doesn't follow for sure."),
                ),
                r,
            )
            else -> choice(
                Topic.Logic, d,
                tr("Kein $a ist $col. Alle $b sind $col. Kann ein $b ein $a sein?", "No $a is $col. All $b are $col. Can a $b be a $a?"),
                never, listOf(yes, no),
                listOf(
                    tr("Jedes $b ist $col, aber kein $a ist $col.", "Every $b is $col, but no $a is $col."),
                    tr("Also kann kein $b ein $a sein: Die beiden Gruppen überschneiden sich nicht.", "So no $b can be a $a: the two groups don't overlap."),
                ),
                r,
            )
        }
    }

    /** Wahrheitssager und Lügner (selbst geschrieben) */
    private fun liars(d: Difficulty, r: Random): Challenge {
        val (a, b, c) = names(r, 3)
        return when (r.nextInt(3)) {
            0 -> choice(
                Topic.Logic, d,
                tr(
                    "Auf einer Insel lügen manche immer, die anderen sagen immer die Wahrheit. $a sagt: „$b lügt.“ $b sagt: „$a und ich sagen beide die Wahrheit.“ Wer lügt?",
                    "On an island some people always lie, the others always tell the truth. $a says: “$b is lying.” $b says: “$a and I both tell the truth.” Who is lying?",
                ),
                b, listOf(a, tr("Beide", "Both"), tr("Keiner", "Neither")),
                listOf(
                    tr("Wäre $b ehrlich, dann wäre auch $a ehrlich. Aber $a sagt, dass $b lügt. Das passt nicht zusammen.", "If $b were honest, $a would be honest too. But $a says $b lies. That's a contradiction."),
                    tr("Also lügt $b. Dann stimmt die Aussage von $a, $a ist ehrlich.", "So $b is lying. Then $a's statement is true, so $a is honest."),
                ),
                r,
            )
            1 -> choice(
                Topic.Logic, d,
                tr(
                    "Genau einer von dreien hat den Kuchen gegessen, und genau einer sagt die Wahrheit. $a: „Ich war's nicht.“ $b: „$a war's.“ $c: „Ich war's nicht.“ Wer hat den Kuchen gegessen?",
                    "Exactly one of three ate the cake, and exactly one tells the truth. $a: “It wasn't me.” $b: “It was $a.” $c: “It wasn't me.” Who ate the cake?",
                ),
                c, listOf(a, b),
                listOf(
                    tr("Die Aussagen von $a und $b widersprechen sich, also sagt genau einer von beiden die Wahrheit.", "$a's and $b's statements contradict each other, so exactly one of them is true."),
                    tr("Dann lügt $c: „Ich war's nicht“ ist falsch, also war es $c.", "So $c is lying: “It wasn't me” is false, so it was $c."),
                    tr("Probe: $a sagt die Wahrheit, $b lügt. Genau einer ehrlich. Passt.", "Check: $a tells the truth, $b lies. Exactly one honest. Fits."),
                ),
                r,
            )
            else -> choice(
                Topic.Logic, d,
                tr(
                    "$a, $b und $c sitzen nebeneinander. $a sitzt nicht ganz links. $b sitzt rechts neben $c. $c sitzt nicht in der Mitte. Wer sitzt in der Mitte?",
                    "$a, $b and $c sit in a row. $a doesn't sit on the far left. $b sits directly right of $c. $c doesn't sit in the middle. Who sits in the middle?",
                ),
                b, listOf(a, c),
                listOf(
                    tr("$c ist nicht in der Mitte und hat rechts neben sich noch $b, also sitzt $c ganz links.", "$c isn't in the middle and has $b to the right, so $c sits on the far left."),
                    tr("Direkt rechts daneben, in der Mitte, sitzt $b. $a sitzt ganz rechts.", "Right next to $c, in the middle, sits $b. $a sits on the far right."),
                ),
                r,
            )
        }
    }
}
