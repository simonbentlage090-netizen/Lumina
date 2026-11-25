package de.funke.app.ui.challenge

import de.funke.app.ui.tr

/*
 * Wissens-Vorlagen (seit 11.0) für Philosophie und Literatur.
 *
 * Jeder Eintrag eines Wissens-Pools (ein Gedanke, ein Begriff, ein Werk, ein Stilmittel …) wird eine eigene
 * Aufgabe. Bei jedem Ziehen wählt die Vorlage die Frageform und die falschen Antworten neu (aus den anderen
 * Einträgen desselben Pools), so fühlt sich auch die hundertste Aufgabe noch frisch an. Einträge, die sich
 * inhaltlich überschneiden, tragen dieselbe [group] und werden nie gegenseitig als falsche Antwort gezogen.
 *
 * Alle Texte sind selbst geschrieben; Zitate nur aus gemeinfreien Werken.
 */

/** Zweisprachiger Text. */
internal class T(val de: String, val en: String) {
    val s: String get() = tr(de, en)
}

internal fun t(de: String, en: String = de) = T(de, en)

/** [n] verschiedene zufällige Einträge aus [pool], die [ok] erfüllen. */
internal fun <X> Gen.draw(pool: List<X>, n: Int, ok: (X) -> Boolean): List<X> {
    val candidates = pool.filter(ok).toMutableList()
    val out = ArrayList<X>(n)
    while (out.size < n && candidates.isNotEmpty()) {
        out += candidates.removeAt(rnd.nextInt(candidates.size))
    }
    return out
}

/**
 * Eine Auswahl-Aufgabe mit einem Schritt. [correct] ist die richtige Antwort, [wrong] die falschen
 * (die Ansicht mischt die Reihenfolge). [solution] erklärt kurz, [background] erzählt, worum es geht.
 */
internal fun quiz(
    id: String,
    topic: Topic,
    level: Difficulty,
    question: String,
    prompt: String,
    correct: String,
    wrong: List<String>,
    solution: String,
    background: String = "",
    hint: String? = null,
    hint2: String? = null,
    fact: String? = null,
    master: Boolean = false,
): Task = Task(
    id = id,
    topic = topic,
    level = level,
    question = question,
    steps = listOf(
        Step(
            prompt = prompt,
            answer = Answer.Choice(listOf(correct) + wrong, 0),
            done = correct,
            hints = listOfNotNull(hint, hint2),
        )
    ),
    solution = listOf(solution),
    background = background,
    fact = fact,
    master = master,
)

/** Jahreszahl als Zahl-Aufgabe: richtig, wenn höchstens [tolerance] Jahre daneben. */
internal fun yearTask(
    id: String,
    topic: Topic,
    level: Difficulty,
    question: String,
    year: Int,
    tolerance: Int,
    solution: String,
    background: String = "",
    hint: String? = null,
): Task = Task(
    id = id,
    topic = topic,
    level = level,
    question = question,
    steps = listOf(
        Step(
            prompt = if (tolerance == 1) tr("Jahr (± 1 Jahr zählt)", "Year (± 1 year counts)") else tr("Jahr (± $tolerance Jahre zählt)", "Year (± $tolerance years counts)"),
            answer = Answer.Number(year.toDouble(), "", abs = tolerance.toDouble()),
            done = "$year",
            hints = listOfNotNull(hint, tr("Ungefähr ${century(year)}.", "Roughly ${century(year)}.")),
        )
    ),
    solution = listOf(solution),
    background = background,
)

/** "im frühen 19. Jahrhundert" usw. */
internal fun century(year: Int): String {
    val c = (year - 1) / 100 + 1
    val part = (year - 1) % 100
    val de = when {
        part < 33 -> "im frühen $c. Jahrhundert"
        part < 66 -> "in der Mitte des $c. Jahrhunderts"
        else -> "im späten $c. Jahrhundert"
    }
    val ord = when (c % 10) {
        1 -> if (c % 100 == 11) "th" else "st"
        2 -> if (c % 100 == 12) "th" else "nd"
        3 -> if (c % 100 == 13) "th" else "rd"
        else -> "th"
    }
    val en = when {
        part < 33 -> "in the early $c$ord century"
        part < 66 -> "in the mid $c$ord century"
        else -> "in the late $c$ord century"
    }
    return tr(de, en)
}
