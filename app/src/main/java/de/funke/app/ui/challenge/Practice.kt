package de.funke.app.ui.challenge

import de.funke.app.R
import de.funke.app.ui.tr

/*
 * Aufgaben-Reiter (seit 12.0 wieder mit Lektionen): Man wählt ein Fach (oder "Gemischt") und eine Stufe,
 * eine Lektion hat 3 bis 5 Aufgaben. Es gibt keinen Kurs zum Durcharbeiten und keinen Fortschritt "x von y"
 * über die Lektionen hinweg; jede Lektion zieht frische Aufgaben (siehe [Tasks.next]).
 */

/** Eine Stufe im Aufgaben-Reiter: die vier Schwierigkeiten plus Meisterklasse. */
enum class PracticeLevel(val id: String, val difficulty: Difficulty, val master: Boolean) {
    Easy("easy", Difficulty.Easy, false),
    Medium("medium", Difficulty.Medium, false),
    Hard("hard", Difficulty.Hard, false),
    Expert("expert", Difficulty.Expert, false),
    Master("master", Difficulty.Expert, true);

    val label: String get() = if (master) tr("Meister", "Master") else difficulty.label

    /** Aufgaben pro Lektion: leichte Stufen etwas mehr, schwere weniger (dafür länger). */
    val lessonSize: Int get() = when (this) {
        Easy -> 5
        Medium, Hard -> 4
        Expert, Master -> 3
    }

    /** Ein Satz, was einen auf der Stufe erwartet. */
    val blurb: String get() = when (this) {
        Easy -> tr("Zum Warmwerden: kurze Fragen, wenig Rechnen.", "To warm up: short questions, little arithmetic.")
        Medium -> tr("Ein bisschen Nachdenken, ein, zwei Schritte.", "A bit of thinking, one or two steps.")
        Hard -> tr("Mehrere Schritte, manchmal eine Formel.", "Several steps, sometimes a formula.")
        Expert -> tr("Knifflig. Für alle, die es wissen wollen.", "Tricky. For those who really want to know.")
        Master -> tr("Uni-Niveau. Nimm dir Zeit.", "University level. Take your time.")
    }

    companion object {
        fun byId(id: String?): PracticeLevel? = entries.firstOrNull { it.id == id }
    }
}

/** Fach-Bild (res/drawable/subj_*.xml). */
val Subject.art: Int
    get() = when (this) {
        Subject.Math -> R.drawable.subj_math
        Subject.Physics -> R.drawable.subj_physics
        Subject.Logic -> R.drawable.subj_logic
        Subject.Geography -> R.drawable.subj_geography
        Subject.Chemistry -> R.drawable.subj_chemistry
        Subject.Biology -> R.drawable.subj_biology
        Subject.History -> R.drawable.subj_history
        Subject.Geology -> R.drawable.subj_geology
        Subject.Philosophy -> R.drawable.subj_philosophy
        Subject.Literature -> R.drawable.subj_literature
        Subject.Extras -> R.drawable.subj_knowledge
    }

/** Alle Themen eines Fachs. */
val Subject.topics: Set<Topic> get() = Topic.entries.filter { it.subject == this }.toSet()

/** Reihenfolge im Reiter: die Schwerpunkte zuerst (seit 12.0 Physik, Mathe, Philosophie, Literatur). */
val PracticeSubjects: List<Subject> = listOf(
    Subject.Physics, Subject.Math, Subject.Philosophy, Subject.Literature, Subject.Chemistry, Subject.Biology,
    Subject.History, Subject.Geography, Subject.Geology, Subject.Logic, Subject.Extras,
)

object Practice {
    private val levelCache = HashMap<Subject, List<PracticeLevel>>()

    /**
     * Stufen, die es in einem Fach wirklich gibt: geschriebene Aufgaben auf der Stufe, oder ein
     * Aufgaben-Generator (der kann alles außer Experte). Meisterklasse nur mit Meister-Aufgaben.
     */
    fun levels(subject: Subject): List<PracticeLevel> = levelCache.getOrPut(subject) {
        val defs = Tasks.allDefs().filter { it.topic.subject == subject }
        val generator = subject.topics.any { it in Challenges.generatedTopics }
        PracticeLevel.entries.filter { lv ->
            when {
                lv.master -> defs.any { it.master }
                else -> defs.any { !it.master && it.level == lv.difficulty } || (generator && lv.difficulty != Difficulty.Expert)
            }
        }
    }

    /** Die passende Stufe in diesem Fach (gibt es [wanted] nicht, die nächstgelegene darunter). */
    fun fit(subject: Subject, wanted: PracticeLevel): PracticeLevel {
        val all = levels(subject)
        return all.lastOrNull { it.ordinal <= wanted.ordinal } ?: all.first()
    }
}
