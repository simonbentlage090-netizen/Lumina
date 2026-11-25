package de.funke.app.ui.challenge

import de.funke.app.ui.tr

/*
 * Literatur (seit 11.0): Epochen, Werke und Autoren, Stilmittel, Gattungen, Versmaße und Reime, dazu ein paar
 * Aufgaben in mehreren Schritten zu berühmten Texten. Jede Aufgabe erklärt danach kurz, worum es geht, damit man
 * beim Üben etwas lernt. Alles selbst geschrieben; Zitate nur aus gemeinfreien Werken (Goethe, Schiller, Lessing,
 * Gryphius, Kafka, Shakespeare …), Inhalte neuerer Bücher in eigenen Worten.
 */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

// ---------------------------------------------------------------------------------------------
// Epochen
// ---------------------------------------------------------------------------------------------

private class LitEpoch(
    val key: String,
    val level: Difficulty,
    val name: T,
    val span: T,
    /** Beginn (für die Reihenfolge und Überschneidungen) */
    val from: Int,
    val to: Int,
    val features: T,
    val authors: T,
    val why: T,
)

private val EPOCHS = listOf(
    LitEpoch("barock", M, t("Barock", "Baroque"), t("ca. 1600–1720", "c. 1600–1720"), 1600, 1720,
        t("Vergänglichkeit, Todesnähe und der Gegensatz von Diesseits und Jenseits, geprägt vom Dreißigjährigen Krieg", "Transience, closeness to death and the contrast between this world and the next, marked by the Thirty Years' War"),
        t("Andreas Gryphius, Grimmelshausen", "Andreas Gryphius, Grimmelshausen"),
        t("Der Dreißigjährige Krieg (1618–1648) verwüstete weite Teile Deutschlands. Die Dichtung kreist um „memento mori“ (denk an den Tod) und „carpe diem“ (nutze den Tag); beliebt sind das Sonett und der Alexandriner.", "The Thirty Years' War (1618–1648) devastated large parts of Germany. Poetry revolves around \"memento mori\" (remember death) and \"carpe diem\" (seize the day); the sonnet and the alexandrine are popular.")),
    LitEpoch("aufklaerung", M, t("Aufklärung", "Enlightenment"), t("ca. 1720–1800", "c. 1720–1800"), 1720, 1800,
        t("Vernunft, Toleranz und die Erziehung des Menschen; beliebt sind Fabeln und das bürgerliche Trauerspiel", "Reason, tolerance and the education of humankind; fables and bourgeois tragedy are popular"),
        t("Gotthold Ephraim Lessing, Christoph Martin Wieland", "Gotthold Ephraim Lessing, Christoph Martin Wieland"),
        t("Kants Leitspruch „Habe Mut, dich deines eigenen Verstandes zu bedienen!“ fasst die Epoche zusammen. Literatur soll aufklären und bessern; Lessings „Nathan der Weise“ wirbt für religiöse Toleranz.", "Kant's motto \"Have the courage to use your own understanding!\" sums up the period. Literature is meant to enlighten and improve; Lessing's Nathan the Wise argues for religious tolerance.")),
    LitEpoch("sturm", M, t("Sturm und Drang", "Storm and Stress (Sturm und Drang)"), t("ca. 1765–1785", "c. 1765–1785"), 1765, 1785,
        t("Gefühl statt Vernunft, das Genie, das sich über alle Regeln hinwegsetzt, und Auflehnung gegen Fürsten und Standesgrenzen", "Feeling instead of reason, the genius who ignores all rules, and revolt against princes and class barriers"),
        t("der junge Goethe, der junge Schiller, Jakob Michael Reinhold Lenz", "the young Goethe, the young Schiller, Jakob Michael Reinhold Lenz"),
        t("Benannt nach einem Drama von Friedrich Maximilian Klinger (1776). Junge Autoren rebellierten gegen die Vernunftkultur der Aufklärung; Goethes „Werther“ löste eine regelrechte Werther-Mode aus.", "Named after a play by Friedrich Maximilian Klinger (1776). Young writers rebelled against the Enlightenment's culture of reason; Goethe's Werther set off a real Werther fashion.")),
    LitEpoch("klassik", M, t("Weimarer Klassik", "Weimar Classicism"), t("ca. 1786–1805", "c. 1786–1805"), 1786, 1805,
        t("Harmonie, Maß und Menschlichkeit; die Antike als Vorbild und das Ideal des schönen, guten Menschen", "Harmony, moderation and humanity; antiquity as a model and the ideal of the beautiful, good human being"),
        t("Goethe und Schiller in Weimar", "Goethe and Schiller in Weimar"),
        t("Sie beginnt mit Goethes Italienreise (1786) und endet mit Schillers Tod (1805). Statt Rebellion suchen die Autoren das Allgemeingültige; im „Balladenjahr“ 1797 entstanden viele ihrer bekanntesten Balladen.", "It begins with Goethe's journey to Italy (1786) and ends with Schiller's death (1805). Instead of rebellion the authors seek what is universally valid; many of their best-known ballads were written in the \"ballad year\" 1797.")),
    LitEpoch("romantik", E, t("Romantik", "Romanticism"), t("ca. 1795–1840", "c. 1795–1840"), 1795, 1840,
        t("Sehnsucht, Traum und Nacht, das Unheimliche, Märchen und das Mittelalter; ihr Sinnbild ist die blaue Blume", "Longing, dreams and night, the uncanny, fairy tales and the Middle Ages; its symbol is the blue flower"),
        t("Novalis, Joseph von Eichendorff, E. T. A. Hoffmann", "Novalis, Joseph von Eichendorff, E. T. A. Hoffmann"),
        t("Die Romantiker wandten sich gegen eine Welt, die nur nach Nutzen und Vernunft fragt. Novalis prägte mit der blauen Blume das Bild der Sehnsucht nach dem Unendlichen; die Brüder Grimm sammelten Volksmärchen.", "The Romantics turned against a world that asks only about usefulness and reason. With the blue flower Novalis created the image of longing for the infinite; the Brothers Grimm collected folk tales.")),
    LitEpoch("vormaerz", H, t("Vormärz", "Vormärz (pre-March)"), t("ca. 1830–1848", "c. 1830–1848"), 1830, 1848,
        t("Politische Literatur gegen Zensur und Fürstenmacht, der Ruf nach Freiheit und Einheit vor der Revolution", "Political literature against censorship and the power of princes, the call for freedom and unity before the revolution"),
        t("Georg Büchner, Heinrich Heine, Georg Herwegh", "Georg Büchner, Heinrich Heine, Georg Herwegh"),
        t("Der Name meint die Zeit vor der Märzrevolution 1848. Viele Autoren wurden verfolgt oder flohen ins Ausland; Büchner rief 1834 im „Hessischen Landboten“ mit „Friede den Hütten! Krieg den Palästen!“ zum Aufstand auf.", "The name refers to the years before the March Revolution of 1848. Many writers were persecuted or fled abroad; in 1834 Büchner called for revolt in The Hessian Courier with \"Peace to the huts! War on the palaces!\"")),
    LitEpoch("realismus", M, t("Poetischer Realismus", "Poetic realism"), t("ca. 1848–1890", "c. 1848–1890"), 1848, 1890,
        t("Die bürgerliche Welt wird genau, aber leicht verklärt und oft mit Humor erzählt; beliebt ist die Novelle", "The middle-class world is told precisely but slightly idealised, often with humour; the novella is popular"),
        t("Theodor Fontane, Theodor Storm, Gottfried Keller", "Theodor Fontane, Theodor Storm, Gottfried Keller"),
        t("Nach der gescheiterten Revolution von 1848 wandten sich viele Autoren vom Politischen ab und der genauen Beobachtung des Alltags zu. „Poetisch“ heißt: Die Wirklichkeit wird nicht in ihrem ganzen Elend gezeigt, sondern künstlerisch geformt.", "After the failed revolution of 1848 many writers turned away from politics towards careful observation of everyday life. \"Poetic\" means that reality is not shown in all its misery but shaped artistically.")),
    LitEpoch("naturalismus", M, t("Naturalismus", "Naturalism"), t("ca. 1880–1900", "c. 1880–1900"), 1880, 1900,
        t("Die Wirklichkeit ungeschönt abbilden, auch Armut und Elend; Herkunft und Umwelt bestimmen den Menschen, oft spricht er Dialekt", "Depicting reality unvarnished, poverty and misery included; origin and environment determine people, who often speak in dialect"),
        t("Gerhart Hauptmann, Arno Holz", "Gerhart Hauptmann, Arno Holz"),
        t("Arno Holz fasste das Programm in die Formel „Kunst = Natur − x“: Je kleiner das x, desto besser die Kunst. Die Naturalisten zeigten die Not der Industriearbeiter so genau, dass manche Aufführungen verboten wurden.", "Arno Holz summed up the programme in the formula \"art = nature − x\": the smaller the x, the better the art. The naturalists showed the misery of industrial workers so precisely that some performances were banned.")),
    LitEpoch("expressionismus", M, t("Expressionismus", "Expressionism"), t("ca. 1910–1925", "c. 1910–1925"), 1910, 1925,
        t("Großstadt, Weltuntergang, Krieg und der Zerfall des Ichs, in grellen Bildern und schreiender Sprache", "The big city, the end of the world, war and the disintegration of the self, in garish images and screaming language"),
        t("Georg Heym, Georg Trakl, Jakob van Hoddis", "Georg Heym, Georg Trakl, Jakob van Hoddis"),
        t("Junge Autoren spürten vor und im Ersten Weltkrieg den Zusammenbruch der alten Ordnung. Statt die Welt abzubilden, drückten sie ihre innere Erschütterung aus; viele, wie Heym und Trakl, starben jung.", "Before and during the First World War young writers sensed the collapse of the old order. Instead of depicting the world they expressed their inner turmoil; many, like Heym and Trakl, died young.")),
    LitEpoch("sachlichkeit", H, t("Neue Sachlichkeit", "New Objectivity"), t("ca. 1920–1933", "c. 1920–1933"), 1920, 1933,
        t("Nüchterne, genaue Sprache über Alltag, Arbeit und Großstadt; Reportage und Gebrauchslyrik statt Pathos", "Sober, precise language about everyday life, work and the big city; reportage and everyday verse instead of pathos"),
        t("Erich Kästner, Irmgard Keun, Kurt Tucholsky", "Erich Kästner, Irmgard Keun, Kurt Tucholsky"),
        t("Nach dem Pathos des Expressionismus wollten Autoren der Weimarer Republik die Wirklichkeit kühl und genau zeigen. Die Epoche endete 1933, als die Nationalsozialisten Bücher verbrannten, darunter Werke von Kästner.", "After the pathos of Expressionism, writers of the Weimar Republic wanted to show reality coolly and precisely. The period ended in 1933 when the Nazis burned books, including works by Kästner.")),
    LitEpoch("exil", H, t("Exilliteratur", "Exile literature"), t("1933–1945", "1933–1945"), 1933, 1945,
        t("Geschrieben von Autoren, die vor den Nationalsozialisten fliehen mussten: Verlust der Heimat, Widerstand und Warnung", "Written by authors who had to flee the Nazis: loss of home, resistance and warning"),
        t("Anna Seghers, Klaus Mann, Bertolt Brecht", "Anna Seghers, Klaus Mann, Bertolt Brecht"),
        t("Über 2000 Schriftstellerinnen und Schriftsteller verließen Deutschland nach 1933. Sie schrieben in Prag, Paris, Moskau, Mexiko oder Kalifornien, oft ohne Leser und in großer Armut; Thomas Mann sprach aus den USA im Radio zu den Deutschen.", "More than 2000 writers left Germany after 1933. They wrote in Prague, Paris, Moscow, Mexico or California, often without readers and in great poverty; Thomas Mann spoke to the Germans on the radio from the USA.")),
    LitEpoch("nachkrieg", M, t("Nachkriegsliteratur", "Post-war literature"), t("ca. 1945–1955", "c. 1945–1955"), 1945, 1955,
        t("Kurze, nüchterne Sätze über Heimkehrer, Trümmer, Hunger und Schuld; die Kurzgeschichte wird beliebt", "Short, plain sentences about returning soldiers, ruins, hunger and guilt; the short story becomes popular"),
        t("Wolfgang Borchert, Heinrich Böll, Günter Eich", "Wolfgang Borchert, Heinrich Böll, Günter Eich"),
        t("Die Autoren misstrauten der Sprache, die die Nationalsozialisten missbraucht hatten, und wollten einen „Kahlschlag“: einfach und wahr schreiben. Viele trafen sich in der Gruppe 47; Günter Eichs Gedicht „Inventur“ zählt nur auf, was einem Kriegsgefangenen bleibt.", "The writers distrusted the language the Nazis had abused and wanted a \"clear-cut\": to write simply and truthfully. Many met in the Group 47; Günter Eich's poem \"Inventory\" simply lists what a prisoner of war has left.")),
)

/** Epochen, die man leicht verwechselt oder deren Autoren sich überschneiden: nie gegenseitig als Ablenker. */
private val EPOCH_CLASH = listOf(
    setOf("aufklaerung", "sturm"),
    setOf("sturm", "klassik"),
    setOf("romantik", "vormaerz"),
    setOf("sachlichkeit", "exil"),
)

private fun epochClash(a: String, b: String): Boolean = a == b || EPOCH_CLASH.any { a in it && b in it }

private fun epochOf(key: String?): LitEpoch? = key?.let { k -> EPOCHS.firstOrNull { it.key == k } }

/** Zeiträume überschneiden sich stark (mehr als die Hälfte des kürzeren): dann kein fairer Ablenker. */
private fun overlaps(a: LitEpoch, b: LitEpoch): Boolean {
    val o = minOf(a.to, b.to) - maxOf(a.from, b.from)
    return o > 0.5 * minOf(a.to - a.from, b.to - b.from)
}

private fun epochDefs(): List<TaskDef> = EPOCHS.map { e ->
    lab("lit.epoch.${e.key}", Topic.LitEpochs, e.level) {
        val background = tr("${e.name.s}, ${e.span.s}: ${e.features.s}. ", "${e.name.s}, ${e.span.s}: ${e.features.s}. ") + e.why.s
        when (int(0, 2)) {
            0 -> quiz(
                "x", Topic.LitEpochs, e.level,
                question = e.features.s,
                prompt = tr("Welche Epoche ist gemeint?", "Which period is meant?"),
                correct = e.name.s,
                wrong = draw(EPOCHS, 3) { it !== e }.map { it.name.s },
                solution = tr("Das beschreibt die Epoche „${e.name.s}“ (${e.span.s}).", "This describes the period \"${e.name.s}\" (${e.span.s})."),
                background = background,
                hint = tr("Typische Autoren: ${e.authors.s}", "Typical authors: ${e.authors.s}"),
            )
            1 -> quiz(
                "x", Topic.LitEpochs, e.level,
                question = tr("Wann ungefähr war die Epoche „${e.name.s}“?", "Roughly when was the period \"${e.name.s}\"?"),
                prompt = tr("Der passende Zeitraum", "The matching time span"),
                correct = e.span.s,
                wrong = draw(EPOCHS, 3) { it !== e && !overlaps(it, e) }.map { it.span.s },
                solution = tr("„${e.name.s}“: ${e.span.s}", "\"${e.name.s}\": ${e.span.s}"),
                background = background,
                hint = tr("Typische Autoren: ${e.authors.s}", "Typical authors: ${e.authors.s}"),
            )
            else -> quiz(
                "x", Topic.LitEpochs, e.level,
                question = tr("Wer gehört zur Epoche „${e.name.s}“?", "Who belongs to the period \"${e.name.s}\"?"),
                prompt = tr("Typische Autoren", "Typical authors"),
                correct = e.authors.s,
                wrong = draw(EPOCHS, 3) { !epochClash(it.key, e.key) }.map { it.authors.s },
                solution = tr("Zur Epoche „${e.name.s}“ gehören ${e.authors.s}.", "${e.authors.s} belong to the period \"${e.name.s}\"."),
                background = background,
                hint = e.span.s,
            )
        }
    }
}

/** Vier Epochen ordnen: Welche ist die älteste, welche die jüngste? */
private fun epochOrder(): TaskDef = lab("lit.epoch.order", Topic.LitEpochs, M) {
    val four = draw(EPOCHS, 4) { true }.sortedBy { it.from }
    val oldest = four.first()
    val newest = four.last()
    val shown = four.shuffled(rnd).joinToString(", ") { it.name.s }
    Task(
        "x", Topic.LitEpochs, M,
        question = tr("Vier Epochen der deutschen Literatur: $shown.", "Four periods of German literature: $shown."),
        steps = listOf(
            choice(
                tr("Welche ist die älteste?", "Which is the oldest?"),
                listOf(oldest.name.s) + four.filter { it !== oldest }.map { it.name.s },
                done = tr("Am ältesten: ${oldest.name.s}", "Oldest: ${oldest.name.s}"),
                hints = listOf(tr("Denk an große Ereignisse: Dreißigjähriger Krieg, Französische Revolution, Weltkriege.", "Think of big events: the Thirty Years' War, the French Revolution, the world wars.")),
            ),
            choice(
                tr("Und welche ist die jüngste?", "And which is the most recent?"),
                listOf(newest.name.s) + four.filter { it !== newest }.map { it.name.s },
                done = tr("Am jüngsten: ${newest.name.s}", "Most recent: ${newest.name.s}"),
                hints = listOf(tr("Welche Epoche liegt am nächsten an heute?", "Which period is closest to today?")),
            ),
        ),
        solution = four.map { "${it.name.s}: ${it.span.s}" },
        background = tr(
            "Epochen sind Ordnungshilfen im Rückblick: Ihre Grenzen sind fließend, und oft schrieben Autoren verschiedener Epochen zur selben Zeit, etwa Goethe neben den jungen Romantikern. Für den Überblick hilft die grobe Reihenfolge trotzdem sehr.",
            "Periods are labels applied in hindsight: their borders are fluid, and authors of different periods often wrote at the same time, such as Goethe alongside the young Romantics. For an overview, the rough order still helps a lot.",
        ),
    )
}

// ---------------------------------------------------------------------------------------------
// Werke und Autoren
// ---------------------------------------------------------------------------------------------

private class Work(
    val key: String,
    val level: Difficulty,
    val title: T,
    val author: T,
    /** Jahr des Erscheinens oder der Uraufführung; null = keine Jahresfrage */
    val year: Int?,
    /** Epoche (Schlüssel) oder null, wenn sich das Werk nicht eindeutig einordnen lässt */
    val epoch: String?,
    /** mit unbestimmtem Artikel, z. B. „ein Briefroman“ */
    val genre: T,
    /** worum es geht, ohne Titel und Autor */
    val desc: T,
    val note: T,
    /** Anzeige der Zeit, wenn es kein genaues Jahr gibt */
    val shown: T? = null,
    /** „von den Brüdern Grimm“ statt „von Brüder Grimm“ */
    val by: T? = null,
)

private val GOETHE = t("Johann Wolfgang von Goethe", "Johann Wolfgang von Goethe")
private val SCHILLER = t("Friedrich Schiller", "Friedrich Schiller")
private val LESSING = t("Gotthold Ephraim Lessing", "Gotthold Ephraim Lessing")
private val KLEIST = t("Heinrich von Kleist", "Heinrich von Kleist")
private val HAUPTMANN = t("Gerhart Hauptmann", "Gerhart Hauptmann")
private val KAFKA = t("Franz Kafka", "Franz Kafka")
private val TMANN = t("Thomas Mann", "Thomas Mann")
private val HESSE = t("Hermann Hesse", "Hermann Hesse")
private val BRECHT = t("Bertolt Brecht", "Bertolt Brecht")
private val DUERRENMATT = t("Friedrich Dürrenmatt", "Friedrich Dürrenmatt")
private val FRISCH = t("Max Frisch", "Max Frisch")
private val FONTANE = t("Theodor Fontane", "Theodor Fontane")
private val HEINE = t("Heinrich Heine", "Heinrich Heine")
private val KAESTNER = t("Erich Kästner", "Erich Kästner")
private val SHAKESPEARE = t("William Shakespeare", "William Shakespeare")

private val NOVEL = t("ein Roman", "a novel")
private val DRAMA = t("ein Drama", "a play")
private val NOVELLA = t("eine Novelle", "a novella")
private val BALLAD = t("eine Ballade", "a ballad")
private val POEM = t("ein Gedicht", "a poem")
private val STORY = t("eine Erzählung", "a story")
private val TRAGEDY = t("eine Tragödie", "a tragedy")
private val BOURGEOIS = t("ein bürgerliches Trauerspiel", "a bourgeois tragedy")
private val COMEDY = t("eine Komödie", "a comedy")

private val WORKS = listOf(
    Work("werther", E, t("Die Leiden des jungen Werthers", "The Sorrows of Young Werther"), GOETHE, 1774, "sturm", t("ein Briefroman", "an epistolary novel"),
        t("Ein junger Mann verliebt sich unglücklich in eine verlobte Frau und schreibt einem Freund in Briefen von seiner wachsenden Verzweiflung.", "A young man falls hopelessly in love with an engaged woman and writes to a friend about his growing despair."),
        t("Der Roman machte den jungen Goethe über Nacht berühmt; junge Männer kleideten sich wie Werther in blauen Frack und gelbe Weste.", "The novel made the young Goethe famous overnight; young men dressed like Werther in a blue coat and a yellow waistcoat.")),
    Work("faust", E, t("Faust. Der Tragödie erster Teil", "Faust, Part One"), GOETHE, 1808, null, TRAGEDY,
        t("Ein Gelehrter, der an den Grenzen allen Wissens verzweifelt, schließt einen Pakt mit dem Teufel, um zu erfahren, was die Welt im Innersten zusammenhält.", "A scholar who despairs at the limits of all knowledge makes a pact with the devil to learn what holds the world together at its core."),
        t("Goethe arbeitete rund 60 Jahre an „Faust“; der zweite Teil erschien erst nach seinem Tod 1832.", "Goethe worked on Faust for about 60 years; the second part only appeared after his death in 1832.")),
    Work("erlkoenig", E, t("Erlkönig", "The Erl-King"), GOETHE, 1782, null, BALLAD,
        t("Ein Vater reitet mit seinem fiebernden Kind durch Nacht und Wind, und eine unheimliche Gestalt lockt das Kind zu sich.", "A father rides through night and wind with his feverish child, and an eerie figure lures the child towards him."),
        t("Franz Schubert vertonte die Ballade 1815 als Lied; das Klavier ahmt dabei den Galopp des Pferdes nach.", "Franz Schubert set the ballad to music in 1815; the piano imitates the horse's gallop.")),
    Work("zauberlehrling", E, t("Der Zauberlehrling", "The Sorcerer's Apprentice"), GOETHE, 1797, "klassik", BALLAD,
        t("Ein Lehrling verzaubert in Abwesenheit seines Meisters einen Besen zum Wasserholen und kann ihn nicht mehr aufhalten.", "While his master is away, an apprentice enchants a broom to fetch water and then cannot stop it."),
        t("Die Ballade entstand im „Balladenjahr“ 1797, als Goethe und Schiller um die Wette Balladen schrieben; der verzweifelte Ruf nach dem Meister wurde sprichwörtlich.", "The ballad was written in the \"ballad year\" 1797, when Goethe and Schiller competed in writing ballads; the desperate call for the master became proverbial.")),
    Work("iphigenie", H, t("Iphigenie auf Tauris", "Iphigenia in Tauris"), GOETHE, 1787, "klassik", DRAMA,
        t("Eine griechische Priesterin auf einer fernen Insel entscheidet sich für die Wahrheit statt für eine List und gewinnt dadurch die Freiheit für sich und ihren Bruder.", "A Greek priestess on a distant island chooses truth over a trick and thereby wins freedom for herself and her brother."),
        t("Das Stück gilt als Musterbeispiel der Weimarer Klassik: Nicht Gewalt, sondern reine Menschlichkeit löst den Konflikt. Die Versfassung vollendete Goethe auf seiner Italienreise.", "The play is a model of Weimar Classicism: not violence but pure humanity resolves the conflict. Goethe completed the verse version on his journey to Italy.")),
    Work("meister", X, t("Wilhelm Meisters Lehrjahre", "Wilhelm Meister's Apprenticeship"), GOETHE, 1795, "klassik", NOVEL,
        t("Ein junger Kaufmannssohn sucht sein Glück beim Theater und findet über viele Umwege zu einem tätigen Leben in der Gesellschaft.", "A young merchant's son seeks his fortune in the theatre and, after many detours, finds his way to an active life in society."),
        t("Das Buch wurde zum Vorbild des Bildungsromans, der die innere Entwicklung eines jungen Menschen erzählt.", "The book became the model of the Bildungsroman, the novel of a young person's inner development.")),
    Work("raeuber", M, t("Die Räuber", "The Robbers"), SCHILLER, 1781, "sturm", DRAMA,
        t("Zwei ungleiche Brüder: Der eine wird durch die Intrige des anderen vom Vater verstoßen und wird zum Anführer einer Räuberbande.", "Two very different brothers: one is disowned by their father through the other's scheming and becomes the leader of a band of robbers."),
        t("Bei der Uraufführung 1782 in Mannheim soll das Publikum getobt haben; bald darauf floh Schiller vor seinem Landesherrn, der ihm das Schreiben verboten hatte.", "At the premiere in Mannheim in 1782 the audience is said to have raved; soon afterwards Schiller fled from his duke, who had forbidden him to write.")),
    Work("kabale", M, t("Kabale und Liebe", "Intrigue and Love"), SCHILLER, 1784, "sturm", BOURGEOIS,
        t("Ein junger Adliger liebt die Tochter eines bürgerlichen Musikers; eine Intrige am Hof treibt beide in den Tod.", "A young nobleman loves the daughter of a middle-class musician; an intrigue at court drives both to their deaths."),
        t("Schiller griff damit offen die Willkür des Adels an; ursprünglich sollte das Stück nach der weiblichen Hauptfigur „Luise Millerin“ heißen.", "Schiller openly attacked the arbitrariness of the nobility; originally the play was to be named after its heroine, \"Luise Millerin\".")),
    Work("tell", E, t("Wilhelm Tell", "William Tell"), SCHILLER, 1804, "klassik", DRAMA,
        t("Ein Schweizer muss einen Apfel vom Kopf seines Sohnes schießen und wird zum Helden des Aufstands gegen einen grausamen Landvogt.", "A Swiss man is forced to shoot an apple off his son's head and becomes the hero of the uprising against a cruel governor."),
        t("Schiller war selbst nie in der Schweiz; er stützte sich auf alte Chroniken und auf Goethes Erzählungen von dessen Reisen.", "Schiller himself never went to Switzerland; he relied on old chronicles and on Goethe's accounts of his travels.")),
    Work("buergschaft", M, t("Die Bürgschaft", "The Pledge"), SCHILLER, 1798, "klassik", BALLAD,
        t("Ein Mann, der einen Tyrannen töten wollte, soll hingerichtet werden; sein Freund bürgt mit dem eigenen Leben, bis er von der Hochzeit seiner Schwester zurückkehrt.", "A man who tried to kill a tyrant is to be executed; his friend stands surety with his own life until he returns from his sister's wedding."),
        t("Die Treue der Freunde beschämt den Tyrannen am Ende so sehr, dass er bittet, als Dritter in ihren Bund aufgenommen zu werden.", "In the end the friends' loyalty shames the tyrant so much that he asks to be admitted to their bond as a third.")),
    Work("maria", H, t("Maria Stuart", "Mary Stuart"), SCHILLER, 1800, "klassik", DRAMA,
        t("Zwei Königinnen, die eine gefangen, die andere an der Macht: Am Ende unterschreibt die eine das Todesurteil der anderen.", "Two queens, one imprisoned, the other in power: in the end one signs the other's death warrant."),
        t("Schiller erfand die berühmte Begegnung der beiden Königinnen im Park; in Wirklichkeit haben sich Maria Stuart und Elisabeth I. nie getroffen.", "Schiller invented the famous meeting of the two queens in the park; in reality Mary Stuart and Elizabeth I never met.")),
    Work("nathan", M, t("Nathan der Weise", "Nathan the Wise"), LESSING, 1779, "aufklaerung", DRAMA,
        t("Ein jüdischer Kaufmann antwortet einem Sultan auf die Frage nach der wahren Religion mit einem Gleichnis von drei Ringen.", "A Jewish merchant answers a sultan's question about the true religion with a parable of three rings."),
        t("Das Stück ist ein Plädoyer für Toleranz; im Nationalsozialismus durfte es nicht gespielt werden, 1945 eröffnete es als erstes Stück wieder das Deutsche Theater in Berlin.", "The play is a plea for tolerance; under the Nazis it could not be performed, and in 1945 it was the first play when the Deutsches Theater in Berlin reopened.")),
    Work("emilia", H, t("Emilia Galotti", "Emilia Galotti"), LESSING, 1772, "aufklaerung", BOURGEOIS,
        t("Ein Prinz begehrt eine junge Bürgerstochter; um sie vor ihm zu bewahren, tötet sie am Ende ihr eigener Vater.", "A prince desires a young middle-class woman; to save her from him, her own father kills her in the end."),
        t("Lessing verlegte die antike Geschichte der Römerin Virginia in seine Zeit und machte Bürgerliche zu tragischen Helden, was zuvor Königen und Fürsten vorbehalten war.", "Lessing moved the ancient story of the Roman Virginia into his own time and made middle-class people tragic heroes, a role previously reserved for kings and princes.")),
    Work("minna", X, t("Minna von Barnhelm", "Minna von Barnhelm"), LESSING, 1767, "aufklaerung", t("ein Lustspiel", "a comedy"),
        t("Ein ehrenhafter, aber entlassener Offizier will seine reiche Verlobte aus Stolz nicht heiraten, bis sie ihn mit einer List umstimmt.", "An honourable but discharged officer refuses out of pride to marry his rich fiancée until she changes his mind with a trick."),
        t("Das Stück spielt kurz nach dem Siebenjährigen Krieg und gilt als erste bedeutende deutsche Komödie.", "The play is set shortly after the Seven Years' War and is considered the first important German comedy.")),
    Work("simplicissimus", H, t("Der abenteuerliche Simplicissimus", "Simplicius Simplicissimus"), t("Grimmelshausen", "Grimmelshausen"), 1668, "barock", NOVEL,
        t("Ein einfältiger Junge gerät in die Wirren des Dreißigjährigen Krieges und erlebt als Soldat, Narr und Einsiedler, wie vergänglich alles ist.", "A simple-minded boy is caught up in the chaos of the Thirty Years' War and learns as a soldier, fool and hermit how fleeting everything is."),
        t("Der Schelmenroman gilt als erster großer deutscher Roman; Grimmelshausen hatte den Krieg selbst als Junge erlebt.", "The picaresque novel is considered the first great German novel; Grimmelshausen had lived through the war himself as a boy.")),
    Work("eitel", M, t("Es ist alles eitel", "All Is Vanity"), t("Andreas Gryphius", "Andreas Gryphius"), 1637, "barock", t("ein Sonett", "a sonnet"),
        t("Ein Gedicht in 14 Versen: Was heute prächtig gebaut wird, ist morgen eine Wiese, und auch der Mensch vergeht.", "A poem of 14 lines: what is splendidly built today will be a meadow tomorrow, and humans pass away too."),
        t("Gryphius erlebte als Kind die Schrecken des Dreißigjährigen Krieges; das Sonett ist ein berühmtes Beispiel für das Vanitas-Motiv.", "Gryphius experienced the horrors of the Thirty Years' War as a child; the sonnet is a famous example of the vanitas motif.")),
    Work("ofterdingen", H, t("Heinrich von Ofterdingen", "Henry of Ofterdingen"), t("Novalis", "Novalis"), 1802, "romantik", NOVEL,
        t("Ein junger Dichter im Mittelalter träumt von einer blauen Blume und macht sich auf die Suche nach ihr und nach sich selbst.", "A young medieval poet dreams of a blue flower and sets out in search of it and of himself."),
        t("Der Roman blieb unvollendet, weil Novalis 1801 mit 28 Jahren starb; die blaue Blume wurde zum Sinnbild der ganzen Romantik.", "The novel remained unfinished because Novalis died in 1801 at the age of 28; the blue flower became the symbol of Romanticism as a whole.")),
    Work("taugenichts", M, t("Aus dem Leben eines Taugenichts", "Memoirs of a Good-for-Nothing"), t("Joseph von Eichendorff", "Joseph von Eichendorff"), 1826, "romantik", NOVELLA,
        t("Ein fröhlicher Müllerssohn zieht mit seiner Geige in die Welt, wandert bis nach Italien und findet am Ende sein Glück.", "A cheerful miller's son sets out into the world with his violin, wanders as far as Italy and finally finds happiness."),
        t("Die Erzählung ist voller Lieder, Wanderlust und Sehnsucht und gilt als Inbegriff der romantischen Lebenshaltung.", "The story is full of songs, wanderlust and longing and is seen as the epitome of the Romantic attitude to life.")),
    Work("sandmann", M, t("Der Sandmann", "The Sandman"), t("E. T. A. Hoffmann", "E. T. A. Hoffmann"), 1816, "romantik", STORY,
        t("Ein Student verliebt sich in eine schöne Frau, die sich als Puppe mit Uhrwerk entpuppt; eine Angst aus seiner Kindheit treibt ihn in den Wahnsinn.", "A student falls in love with a beautiful woman who turns out to be a clockwork doll; a childhood fear drives him mad."),
        t("Ein Hauptwerk der „schwarzen“ Romantik; Sigmund Freud nutzte die Erzählung 1919 in seinem Aufsatz über das Unheimliche.", "A major work of \"dark\" Romanticism; Sigmund Freud used the story in his 1919 essay on the uncanny.")),
    Work("maerchen", E, t("Kinder- und Hausmärchen", "Children's and Household Tales"), t("Brüder Grimm", "Brothers Grimm"), 1812, "romantik", t("eine Märchensammlung", "a collection of fairy tales"),
        t("Eine Sammlung von Volkserzählungen mit Hexen, Wölfen, Prinzessinnen und sprechenden Tieren, die bis heute Kindern vorgelesen werden.", "A collection of folk tales with witches, wolves, princesses and talking animals that are still read to children today."),
        t("Jacob und Wilhelm Grimm wollten das Erzählgut des Volkes bewahren, überarbeiteten die Texte aber stark; das Buch gehört zum Weltdokumentenerbe der UNESCO.", "Jacob and Wilhelm Grimm wanted to preserve folk storytelling but revised the texts heavily; the book is part of UNESCO's Memory of the World."),
        by = t("von den Brüdern Grimm", "by the Brothers Grimm")),
    Work("kohlhaas", H, t("Michael Kohlhaas", "Michael Kohlhaas"), KLEIST, 1810, null, NOVELLA,
        t("Ein rechtschaffener Pferdehändler wird von einem Junker um zwei Pferde betrogen und wird aus verletztem Rechtsgefühl zum Brandstifter und Rebellen.", "An upright horse dealer is cheated out of two horses by a nobleman and, out of a wounded sense of justice, becomes an arsonist and rebel."),
        t("Kleist stützte sich auf einen echten Fall aus dem 16. Jahrhundert; die Novelle fragt, wie weit man für Gerechtigkeit gehen darf.", "Kleist drew on a real case from the 16th century; the novella asks how far one may go for justice.")),
    Work("krug", H, t("Der zerbrochne Krug", "The Broken Jug"), KLEIST, 1808, null, t("ein Lustspiel", "a comedy"),
        t("Ein Dorfrichter muss in einem Prozess über eine nächtliche Tat urteilen, die er selbst begangen hat, und verstrickt sich immer mehr.", "A village judge has to rule on a night-time offence that he committed himself and gets more and more entangled."),
        t("Die Uraufführung 1808 in Weimar unter Goethes Leitung fiel durch; heute ist das Stück eine der meistgespielten deutschen Komödien.", "The 1808 premiere in Weimar under Goethe's direction was a flop; today it is one of the most frequently performed German comedies.")),
    Work("woyzeck", M, t("Woyzeck", "Woyzeck"), t("Georg Büchner", "Georg Büchner"), null, "vormaerz", t("ein Dramenfragment", "a dramatic fragment"),
        t("Ein einfacher Soldat wird von seinem Hauptmann gedemütigt und von einem Arzt für Experimente benutzt; aus Eifersucht ersticht er seine Geliebte.", "A simple soldier is humiliated by his captain and used for experiments by a doctor; out of jealousy he stabs the woman he loves."),
        t("Büchner starb 1837 mit 23 Jahren, das Stück blieb Fragment; es ist eines der ersten Dramen, in denen ein armer, einfacher Mensch im Mittelpunkt steht.", "Büchner died in 1837 aged 23 and the play remained a fragment; it is one of the first dramas to centre on a poor, ordinary person."),
        shown = t("entstanden 1836/37", "written 1836–37")),
    Work("wintermaerchen", H, t("Deutschland. Ein Wintermärchen", "Germany. A Winter's Tale"), HEINE, 1844, "vormaerz", t("ein Versepos", "a verse epic"),
        t("Ein Dichter reist aus dem Pariser Exil durch die alte Heimat und nimmt in bissigen Versen Zensur, Militär und Kleinstaaterei aufs Korn.", "A poet travels from his Paris exile through his old homeland and mocks censorship, the military and petty states in biting verse."),
        t("Heine lebte seit 1831 in Paris; das Werk wurde in Preußen sofort verboten.", "Heine had lived in Paris since 1831; the work was banned in Prussia at once.")),
    Work("loreley", M, t("Die Loreley", "The Lorelei"), HEINE, 1824, "romantik", POEM,
        t("Eine schöne Frau kämmt auf einem Felsen am Rhein ihr goldenes Haar und singt, und ein Schiffer verunglückt, weil er nur zu ihr hinaufschaut.", "A beautiful woman sits on a rock above the Rhine combing her golden hair and singing, and a boatman crashes because he only looks up at her."),
        t("Den Felsen bei St. Goarshausen gibt es wirklich; vertont von Friedrich Silcher wurde das Gedicht zum Volkslied.", "The rock near St. Goarshausen really exists; set to music by Friedrich Silcher, the poem became a folk song.")),
    Work("schimmelreiter", M, t("Der Schimmelreiter", "The Rider on the White Horse"), t("Theodor Storm", "Theodor Storm"), 1888, "realismus", NOVELLA,
        t("Ein ehrgeiziger Deichgraf an der Nordseeküste baut einen neuen Deich und kämpft gegen die Sturmflut und den Aberglauben der Dorfbewohner.", "An ambitious dike reeve on the North Sea coast builds a new dike and fights the storm surge and the villagers' superstition."),
        t("Storms letzte Novelle erschien kurz vor seinem Tod; ihr verschachtelter Rahmen lässt offen, ob der gespenstische Reiter wirklich existiert.", "Storm's last novella appeared shortly before his death; its nested frame leaves open whether the ghostly rider really exists.")),
    Work("effi", E, t("Effi Briest", "Effi Briest"), FONTANE, 1895, "realismus", NOVEL,
        t("Eine 17-Jährige wird mit einem viel älteren Beamten verheiratet; als Jahre später alte Liebesbriefe auftauchen, zerbricht ihr Leben an den Regeln der Gesellschaft.", "A 17-year-old is married to a much older official; when old love letters turn up years later, her life is broken by society's rules."),
        t("Fontane schrieb seinen ersten Roman erst mit fast 60 Jahren; „Effi Briest“ gilt als Höhepunkt des deutschen Realismus.", "Fontane only wrote his first novel at almost 60; Effi Briest is considered the high point of German realism.")),
    Work("ribbeck", M, t("Herr von Ribbeck auf Ribbeck im Havelland", "Herr von Ribbeck of Ribbeck in Havelland"), FONTANE, 1889, "realismus", BALLAD,
        t("Ein großzügiger alter Gutsherr lässt sich eine Birne mit ins Grab legen, damit die Kinder auch nach seinem Tod noch Birnen bekommen.", "A generous old landowner has a pear placed in his grave so that the children will still get pears after his death."),
        t("Aus dem Grab wächst ein Birnbaum, der die Kinder weiter beschenkt; Fontane zeigt so, wie Güte über den Tod hinaus wirkt.", "A pear tree grows from the grave and keeps giving to the children; Fontane shows how kindness lives on beyond death.")),
    Work("romeodorf", X, t("Romeo und Julia auf dem Dorfe", "A Village Romeo and Juliet"), t("Gottfried Keller", "Gottfried Keller"), 1856, "realismus", NOVELLA,
        t("Zwei Bauernkinder lieben sich, doch ihre Väter haben sich über einen Acker zerstritten und dabei ruiniert.", "Two farm children love each other, but their fathers have quarrelled over a field and ruined themselves in the process."),
        t("Der Schweizer Keller verlegte Shakespeares Liebesgeschichte in die bäuerliche Welt; Anlass war eine echte Zeitungsmeldung.", "The Swiss writer Keller moved Shakespeare's love story into the farming world; it was prompted by a real newspaper report.")),
    Work("judenbuche", H, t("Die Judenbuche", "The Jews' Beech"), t("Annette von Droste-Hülshoff", "Annette von Droste-Hülshoff"), 1842, null, NOVELLA,
        t("In einem abgelegenen westfälischen Dorf bleibt ein Mord jahrzehntelang ungesühnt; in einen Baum wird eine hebräische Inschrift geritzt.", "In a remote Westphalian village a murder goes unpunished for decades; a Hebrew inscription is carved into a tree."),
        t("Die Dichterin verarbeitete einen echten Kriminalfall; die Novelle zeigt, wie Armut und Umfeld einen Menschen prägen.", "The poet drew on a real criminal case; the novella shows how poverty and surroundings shape a person.")),
    Work("weber", M, t("Die Weber", "The Weavers"), HAUPTMANN, 1892, "naturalismus", DRAMA,
        t("Schlesische Arbeiter leben in bitterer Armut und erheben sich gegen einen Fabrikanten, der ihre Löhne immer weiter drückt.", "Silesian workers live in bitter poverty and rise up against a manufacturer who keeps cutting their wages."),
        t("Das Stück beruht auf dem Weberaufstand von 1844; Kaiser Wilhelm II. kündigte aus Protest seine Loge im Deutschen Theater. Hauptmann bekam 1912 den Literaturnobelpreis.", "The play is based on the weavers' uprising of 1844; Emperor Wilhelm II gave up his box at the Deutsches Theater in protest. Hauptmann won the Nobel Prize in Literature in 1912.")),
    Work("thiel", H, t("Bahnwärter Thiel", "Flagman Thiel"), HAUPTMANN, 1888, "naturalismus", t("eine novellistische Studie", "a novella-like study"),
        t("Ein wortkarger Bahnwärter lebt zwischen der Erinnerung an seine verstorbene erste Frau und seiner herrischen zweiten, bis ein Zug seinen kleinen Sohn erfasst.", "A taciturn railway flagman lives between the memory of his late first wife and his domineering second one, until a train hits his little son."),
        t("Hauptmann beschreibt fast wie in Zeitlupe, was in Thiel vorgeht; der Text gilt als wichtiges Frühwerk des Naturalismus.", "Hauptmann describes what goes on inside Thiel almost in slow motion; the text is an important early work of Naturalism.")),
    Work("verwandlung", E, t("Die Verwandlung", "The Metamorphosis"), KAFKA, 1915, null, STORY,
        t("Ein Handelsreisender erwacht eines Morgens als riesiges Ungeziefer, und seine Familie weiß nicht, wie sie mit ihm umgehen soll.", "A travelling salesman wakes up one morning as a giant insect, and his family doesn't know how to deal with him."),
        t("Kafka veröffentlichte zu Lebzeiten nur wenig; er verlangte sogar, dass sein Nachlass verbrannt werde, doch sein Freund Max Brod hielt sich nicht daran.", "Kafka published little during his lifetime; he even asked for his papers to be burned, but his friend Max Brod did not comply.")),
    Work("process", M, t("Der Process", "The Trial"), KAFKA, 1925, null, NOVEL,
        t("Ein Bankangestellter wird an seinem 30. Geburtstag verhaftet, ohne je zu erfahren, was man ihm vorwirft.", "A bank clerk is arrested on his 30th birthday without ever finding out what he is accused of."),
        t("Der Roman erschien erst nach Kafkas Tod, herausgegeben von Max Brod; das Gericht bleibt bis zum Ende undurchschaubar.", "The novel only appeared after Kafka's death, edited by Max Brod; the court remains impenetrable to the end.")),
    Work("buddenbrooks", M, t("Buddenbrooks", "Buddenbrooks"), TMANN, 1901, null, NOVEL,
        t("Über vier Generationen erlebt eine reiche Lübecker Kaufmannsfamilie ihren langsamen Niedergang.", "Over four generations a wealthy merchant family from Lübeck experiences its slow decline."),
        t("Thomas Mann war erst 26, als der Roman erschien; vor allem für dieses Buch bekam er 1929 den Literaturnobelpreis.", "Thomas Mann was only 26 when the novel appeared; he won the 1929 Nobel Prize in Literature mainly for this book.")),
    Work("zauberberg", H, t("Der Zauberberg", "The Magic Mountain"), TMANN, 1924, null, NOVEL,
        t("Ein junger Ingenieur besucht seinen Vetter in einem Lungensanatorium in den Schweizer Alpen und bleibt sieben Jahre.", "A young engineer visits his cousin at a lung sanatorium in the Swiss Alps and stays for seven years."),
        t("In langen Gesprächen über Leben, Tod, Zeit und Politik spiegelt der Roman Europa vor dem Ersten Weltkrieg.", "In long conversations about life, death, time and politics the novel mirrors Europe before the First World War.")),
    Work("alexanderplatz", H, t("Berlin Alexanderplatz", "Berlin Alexanderplatz"), t("Alfred Döblin", "Alfred Döblin"), 1929, null, NOVEL,
        t("Ein entlassener Häftling will in der Großstadt anständig bleiben, gerät aber immer wieder ins Verbrechermilieu; erzählt mit Zeitungsschnipseln, Schlagern und Werbesprüchen.", "A released prisoner wants to go straight in the big city but keeps slipping back into the criminal world; told with newspaper snippets, pop songs and advertising slogans."),
        t("Döblin übertrug die Schnitttechnik des Films auf den Roman; Franz Biberkopf wurde zu einer der bekanntesten Figuren der Weimarer Zeit.", "Döblin transferred film-style montage to the novel; Franz Biberkopf became one of the best-known figures of the Weimar era.")),
    Work("fabian", X, t("Fabian", "Fabian (Going to the Dogs)"), KAESTNER, 1931, "sachlichkeit", NOVEL,
        t("Ein arbeitsloser Werbetexter treibt durch das Nachtleben Berlins am Ende der Weimarer Republik, als Moralist unter lauter Gleichgültigen.", "An unemployed copywriter drifts through Berlin's nightlife at the end of the Weimar Republic, a moralist among people who don't care."),
        t("Kästner gab dem Roman den Untertitel „Die Geschichte eines Moralisten“; 1933 verbrannten die Nationalsozialisten das Buch.", "Kästner subtitled the novel \"The Story of a Moralist\"; in 1933 the Nazis burned the book.")),
    Work("emil", E, t("Emil und die Detektive", "Emil and the Detectives"), KAESTNER, 1929, null, t("ein Kinderroman", "a children's novel"),
        t("Einem Jungen wird im Zug nach Berlin das Geld gestohlen; mit einer Bande von Kindern jagt er den Dieb durch die Großstadt.", "A boy has his money stolen on the train to Berlin; with a gang of children he hunts the thief through the big city."),
        t("Es war einer der ersten Kinderromane, die in einer echten modernen Großstadt spielen, und wurde mehrfach verfilmt.", "It was one of the first children's novels set in a real modern city and has been filmed several times.")),
    Work("westen", E, t("Im Westen nichts Neues", "All Quiet on the Western Front"), t("Erich Maria Remarque", "Erich Maria Remarque"), 1929, null, NOVEL,
        t("Ein junger Soldat erzählt, wie er und seine Schulkameraden an der Front des Ersten Weltkriegs abstumpfen und sterben.", "A young soldier tells how he and his classmates are numbed and killed at the front in the First World War."),
        t("Das Buch wurde ein Weltbestseller und 1933 von den Nationalsozialisten verbrannt; die Verfilmungen von 1930 und 2022 gewannen Oscars.", "The book became a worldwide bestseller and was burned by the Nazis in 1933; the films of 1930 and 2022 both won Oscars.")),
    Work("siddhartha", M, t("Siddhartha", "Siddhartha"), HESSE, 1922, null, STORY,
        t("Ein junger Brahmanensohn sucht als Asket, Kaufmann und Liebender nach Erleuchtung und findet sie schließlich an einem Fluss.", "A young Brahmin's son seeks enlightenment as an ascetic, a merchant and a lover and finally finds it by a river."),
        t("Hesse nannte das Buch „eine indische Dichtung“; in den 1960er-Jahren wurde es in den USA zum Kultbuch.", "Hesse called the book \"an Indian poem\"; in the 1960s it became a cult book in the USA.")),
    Work("steppenwolf", H, t("Der Steppenwolf", "Steppenwolf"), HESSE, 1927, null, NOVEL,
        t("Ein einsamer Intellektueller, zerrissen zwischen bürgerlichem Leben und wilder Einsamkeit, gerät in ein rätselhaftes „magisches Theater“.", "A lonely intellectual, torn between middle-class life and wild solitude, ends up in a mysterious \"magic theatre\"."),
        t("Eine Rockband benannte sich nach dem Roman; Hesse bekam 1946 den Literaturnobelpreis.", "A rock band named itself after the novel; Hesse won the Nobel Prize in Literature in 1946.")),
    Work("courage", M, t("Mutter Courage und ihre Kinder", "Mother Courage and Her Children"), BRECHT, 1941, "exil", DRAMA,
        t("Eine Händlerin zieht mit ihrem Planwagen durch den Dreißigjährigen Krieg; weil sie am Krieg verdienen will, verliert sie alle drei Kinder.", "A trader pulls her wagon through the Thirty Years' War; because she wants to profit from the war, she loses all three of her children."),
        t("Brecht schrieb das Stück 1939 im Exil als Warnung vor dem nächsten Krieg; uraufgeführt wurde es 1941 in Zürich.", "Brecht wrote the play in exile in 1939 as a warning against the coming war; it premiered in Zurich in 1941.")),
    Work("galilei", H, t("Leben des Galilei", "Life of Galileo"), BRECHT, 1943, "exil", DRAMA,
        t("Ein Astronom widerruft vor der Inquisition seine Lehre, dass sich die Erde um die Sonne dreht, und fragt sich später, was er der Wissenschaft schuldig blieb.", "An astronomer recants before the Inquisition his teaching that the Earth moves around the Sun, and later asks himself what he owed to science."),
        t("Nach den Atombomben von 1945 überarbeitete Brecht das Stück und betonte die Verantwortung der Wissenschaftler.", "After the atomic bombs of 1945 Brecht revised the play and stressed the responsibility of scientists.")),
    Work("schach", M, t("Schachnovelle", "Chess Story"), t("Stefan Zweig", "Stefan Zweig"), 1942, "exil", NOVELLA,
        t("Auf einem Passagierschiff spielt ein Unbekannter gegen den Schachweltmeister; er hat das Spiel in monatelanger Einzelhaft der Gestapo gelernt.", "On a passenger ship a stranger plays against the world chess champion; he learned the game during months of solitary confinement by the Gestapo."),
        t("Zweig schrieb die Novelle im brasilianischen Exil; sie zeigt, wie Isolationshaft einen Menschen an den Rand des Wahnsinns treibt.", "Zweig wrote the novella in exile in Brazil; it shows how solitary confinement drives a person to the edge of madness.")),
    Work("kreuz", H, t("Das siebte Kreuz", "The Seventh Cross"), t("Anna Seghers", "Anna Seghers"), 1942, "exil", NOVEL,
        t("Sieben Häftlinge fliehen aus einem Konzentrationslager; nur einer entkommt, weil ihm Menschen unter Lebensgefahr helfen.", "Seven prisoners escape from a concentration camp; only one gets away, because people help him at the risk of their lives."),
        t("Seghers schrieb den Roman im französischen Exil; er erschien 1942 zuerst auf Englisch in den USA und wurde 1944 in Hollywood verfilmt.", "Seghers wrote the novel in exile in France; it first appeared in English in the USA in 1942 and was filmed in Hollywood in 1944.")),
    Work("draussen", M, t("Draußen vor der Tür", "The Man Outside"), t("Wolfgang Borchert", "Wolfgang Borchert"), 1947, "nachkrieg", DRAMA,
        t("Ein Kriegsheimkehrer findet in der zerstörten Stadt keinen Platz mehr: Seine Frau hat einen anderen, und überall wird er abgewiesen.", "A soldier returning from the war finds no place in the ruined city: his wife has someone else, and he is turned away everywhere."),
        t("Borchert schrieb das Stück in wenigen Tagen und starb einen Tag vor der Uraufführung mit 26 Jahren. Der Untertitel lautet: „Ein Stück, das kein Theater spielen und kein Publikum sehen will.“", "Borchert wrote the play in a few days and died, aged 26, the day before its premiere. Its subtitle reads: \"A play that no theatre wants to stage and no audience wants to see.\"")),
    Work("clown", X, t("Ansichten eines Clowns", "The Clown"), t("Heinrich Böll", "Heinrich Böll"), 1963, null, NOVEL,
        t("Ein Clown, von seiner Frau verlassen, sitzt in Bonn am Telefon und rechnet mit der frommen, satten Gesellschaft der jungen Bundesrepublik ab.", "A clown whose wife has left him sits by the phone in Bonn and settles scores with the pious, complacent society of the young Federal Republic."),
        t("Böll war einer der wichtigsten kritischen Stimmen der Nachkriegszeit und bekam 1972 den Literaturnobelpreis.", "Böll was one of the most important critical voices of the post-war era and won the Nobel Prize in Literature in 1972.")),
    Work("blechtrommel", M, t("Die Blechtrommel", "The Tin Drum"), t("Günter Grass", "Günter Grass"), 1959, null, NOVEL,
        t("Ein Junge beschließt an seinem dritten Geburtstag, nicht mehr zu wachsen, und trommelt sich durch die Geschichte Danzigs bis in die Nachkriegszeit.", "On his third birthday a boy decides to stop growing and drums his way through the history of Danzig into the post-war years."),
        t("Der Roman machte Grass weltberühmt; 1999 bekam er den Literaturnobelpreis, die Verfilmung von 1979 einen Oscar.", "The novel made Grass world-famous; he won the Nobel Prize in Literature in 1999, and the 1979 film won an Oscar.")),
    Work("dame", M, t("Der Besuch der alten Dame", "The Visit"), DUERRENMATT, 1956, null, t("eine tragische Komödie", "a tragicomedy"),
        t("Eine Milliardärin kehrt in ihr verarmtes Heimatstädtchen zurück und bietet eine Milliarde für den Tod des Mannes, der sie einst verraten hat.", "A billionairess returns to her impoverished home town and offers a billion for the death of the man who once betrayed her."),
        t("Die Bürger lehnen empört ab und machen doch Schulden im Vertrauen auf das Geld; Dürrenmatt zeigt, wie käuflich Moral sein kann.", "The townspeople indignantly refuse, yet run up debts counting on the money; Dürrenmatt shows how morality can be bought.")),
    Work("physiker", M, t("Die Physiker", "The Physicists"), DUERRENMATT, 1962, null, COMEDY,
        t("Drei Physiker in einer Nervenheilanstalt: Zwei geben sich als Newton und Einstein aus, dem dritten erscheint angeblich König Salomo, und alle haben ein Geheimnis.", "Three physicists in a mental asylum: two pretend to be Newton and Einstein, the third claims King Solomon appears to him, and all of them have a secret."),
        t("Das Stück entstand in der Zeit des atomaren Wettrüstens; Dürrenmatt meinte, was einmal gedacht wurde, lasse sich nicht mehr zurücknehmen.", "The play was written during the nuclear arms race; Dürrenmatt held that what has once been thought can never be taken back.")),
    Work("faber", H, t("Homo faber", "Homo Faber"), FRISCH, 1957, null, NOVEL,
        t("Ein Ingenieur, der nur an Technik und Wahrscheinlichkeit glaubt, verliebt sich auf einer Schiffsreise in eine junge Frau, ohne zu ahnen, wer sie wirklich ist.", "An engineer who believes only in technology and probability falls in love with a young woman on a sea voyage, without suspecting who she really is."),
        t("Frisch stellt einen Menschen, der alles berechnen will, vor Zufall und Schicksal; der lateinische Titel bedeutet „der Mensch als Handwerker“.", "Frisch confronts a man who wants to calculate everything with chance and fate; the Latin title means \"man the maker\".")),
    Work("andorra", H, t("Andorra", "Andorra"), FRISCH, 1961, null, DRAMA,
        t("In einem erfundenen Kleinstaat halten alle einen jungen Mann für einen Juden, bis er das Bild, das sie von ihm haben, selbst annimmt.", "In an invented small country everyone takes a young man for a Jew, until he accepts the image they have of him."),
        t("Frisch zeigt, wie Vorurteile Menschen formen; „Du sollst dir kein Bildnis machen“ ist ein Grundgedanke seines Werks.", "Frisch shows how prejudice shapes people; \"Thou shalt not make unto thee any graven image\" is a core idea of his work.")),
    Work("parfum", E, t("Das Parfum", "Perfume"), t("Patrick Süskind", "Patrick Süskind"), 1985, null, NOVEL,
        t("Im Frankreich des 18. Jahrhunderts wird ein Mann ohne eigenen Körpergeruch zum genialen Parfümeur und zum Mörder.", "In 18th-century France a man without any body odour of his own becomes a brilliant perfumer and a murderer."),
        t("Der Roman wurde in rund 50 Sprachen übersetzt und stand jahrelang auf den Bestsellerlisten; Süskind selbst meidet die Öffentlichkeit fast völlig.", "The novel has been translated into about 50 languages and was on the bestseller lists for years; Süskind himself almost completely avoids the public.")),
    Work("vorleser", M, t("Der Vorleser", "The Reader"), t("Bernhard Schlink", "Bernhard Schlink"), 1995, null, NOVEL,
        t("Ein Jugendlicher liest einer älteren Frau regelmäßig aus Büchern vor; Jahre später sieht er sie als Angeklagte in einem KZ-Prozess wieder und erkennt ihr Geheimnis.", "A teenager regularly reads books aloud to an older woman; years later he sees her again as a defendant in a concentration camp trial and discovers her secret."),
        t("Der Roman fragt, wie die Nachkriegsgeneration mit der Schuld ihrer Eltern umgehen soll; er wurde in viele Sprachen übersetzt und 2008 verfilmt.", "The novel asks how the post-war generation should deal with their parents' guilt; it was translated into many languages and filmed in 2008.")),
    Work("tschick", E, t("Tschick", "Why We Took the Car (Tschick)"), t("Wolfgang Herrndorf", "Wolfgang Herrndorf"), 2010, null, NOVEL,
        t("Zwei Außenseiter einer Berliner Schule fahren in den Sommerferien mit einem geklauten Lada durch die ostdeutsche Provinz.", "Two outsiders from a Berlin school drive through the East German countryside in a stolen Lada during the summer holidays."),
        t("Der Jugendroman wurde ein Bestseller, ist heute oft Schullektüre und wurde 2016 von Fatih Akin verfilmt.", "The young adult novel became a bestseller, is often read in schools today and was filmed by Fatih Akin in 2016.")),
    Work("momo", E, t("Momo", "Momo"), t("Michael Ende", "Michael Ende"), 1973, null, NOVEL,
        t("Ein Mädchen, das wunderbar zuhören kann, kämpft gegen graue Herren, die den Menschen ihre Zeit stehlen.", "A girl who is a wonderful listener fights against grey gentlemen who steal people's time."),
        t("Der Märchenroman über Zeit und Hetze ist bis heute aktuell; zusammen mit „Die unendliche Geschichte“ (1979) gehört er zu den erfolgreichsten deutschen Kinderbüchern.", "The fairy-tale novel about time and hurry is still topical; together with The Neverending Story (1979) it is one of the most successful German children's books.")),
    Work("gottstadt", H, t("Der Gott der Stadt", "The God of the City"), t("Georg Heym", "Georg Heym"), 1910, "expressionismus", POEM,
        t("Ein riesiger Götze hockt breit auf einem Häuserblock; die Großstadt opfert ihm Rauch und Feuer.", "A huge idol squats on a block of houses; the city offers him smoke and fire as sacrifices."),
        t("Heym ertrank 1912 mit 24 Jahren beim Schlittschuhlaufen; seine Großstadtgedichte gehören zu den bekanntesten des Expressionismus.", "Heym drowned while ice skating in 1912, aged 24; his poems about the city are among the best known of Expressionism.")),
    Work("weltende", X, t("Weltende", "End of the World"), t("Jakob van Hoddis", "Jakob van Hoddis"), 1911, "expressionismus", POEM,
        t("In nur acht Zeilen fliegt dem Bürger der Hut vom Kopf, Dachdecker stürzen, Eisenbahnen fallen von Brücken: lauter unverbundene Katastrophenbilder.", "In just eight lines the citizen's hat flies off, roofers fall, trains drop from bridges: a string of unconnected images of disaster."),
        t("Das kurze Gedicht gilt als Startsignal des Expressionismus; der Reihungsstil setzt Bilder ohne Zusammenhang nebeneinander wie Schlagzeilen.", "The short poem is seen as the starting signal of Expressionism; its list-like style sets images side by side without connection, like headlines.")),
    Work("morgue", X, t("Morgue", "Morgue"), t("Gottfried Benn", "Gottfried Benn"), 1912, "expressionismus", t("ein Gedichtband", "a collection of poems"),
        t("Ein junger Arzt beschreibt in kühlen, schockierenden Gedichten Leichen im Sektionssaal.", "A young doctor describes corpses in the dissecting room in cool, shocking poems."),
        t("Benn war selbst Arzt; die nüchterne Sprache über Tod und Verfall war ein Skandal und machte ihn bekannt.", "Benn was a doctor himself; his matter-of-fact language about death and decay caused a scandal and made him known.")),
    Work("panther", M, t("Der Panther", "The Panther"), t("Rainer Maria Rilke", "Rainer Maria Rilke"), 1902, null, POEM,
        t("Ein Raubtier im Pariser Zoo läuft hinter Gitterstäben im Kreis, bis es für ihn keine Welt mehr dahinter gibt.", "A big cat in the Paris zoo paces in circles behind bars until for him there is no world beyond them."),
        t("Ein berühmtes „Dinggedicht“: Rilke beobachtete das Tier im Jardin des Plantes und versuchte, sein Wesen ganz genau zu erfassen.", "A famous \"thing poem\": Rilke observed the animal in the Jardin des Plantes and tried to capture its essence exactly.")),
    Work("todesfuge", H, t("Todesfuge", "Death Fugue"), t("Paul Celan", "Paul Celan"), 1948, null, POEM,
        t("Ein Gedicht über die Ermordung der Juden in den Vernichtungslagern, gebaut wie eine musikalische Fuge mit immer wiederkehrenden Motiven.", "A poem about the murder of the Jews in the death camps, built like a musical fugue with constantly recurring motifs."),
        t("Celan, dessen Eltern in einem Lager umkamen, schrieb es um das Kriegsende; es wurde zum bekanntesten Gedicht über die Shoah.", "Celan, whose parents died in a camp, wrote it around the end of the war; it became the best-known poem about the Shoah.")),
    // Weltliteratur
    Work("romeo", E, t("Romeo und Julia", "Romeo and Juliet"), SHAKESPEARE, 1597, null, TRAGEDY,
        t("Zwei junge Menschen aus verfeindeten Familien in Verona lieben sich heimlich; ein Missverständnis führt zum Tod beider.", "Two young people from feuding families in Verona love each other in secret; a misunderstanding leads to the death of both."),
        t("Shakespeare übernahm den Stoff aus älteren italienischen Erzählungen; die Balkonszene ist eine der berühmtesten Szenen der Theatergeschichte.", "Shakespeare took the story from older Italian tales; the balcony scene is one of the most famous scenes in theatre history.")),
    Work("hamlet", E, t("Hamlet", "Hamlet"), SHAKESPEARE, 1601, null, TRAGEDY,
        t("Ein dänischer Prinz soll den Mord an seinem Vater rächen, zweifelt und zögert, und am Ende sterben fast alle.", "A Danish prince is meant to avenge his father's murder, doubts and hesitates, and in the end almost everyone dies."),
        t("„Sein oder Nichtsein“ aus diesem Stück ist wohl der bekannteste Satz der Weltliteratur.", "\"To be, or not to be\" from this play is probably the best-known line in world literature.")),
    Work("quijote", E, t("Don Quijote", "Don Quixote"), t("Miguel de Cervantes", "Miguel de Cervantes"), 1605, null, NOVEL,
        t("Ein verarmter Landadliger hat zu viele Ritterromane gelesen und zieht mit seinem Knappen los, um gegen Windmühlen zu kämpfen, die er für Riesen hält.", "An impoverished country gentleman has read too many tales of chivalry and sets out with his squire to fight windmills he takes for giants."),
        t("Der Roman gilt als erster moderner Roman Europas; „gegen Windmühlen kämpfen“ wurde zur Redewendung.", "The novel is considered Europe's first modern novel; \"tilting at windmills\" became an idiom.")),
    Work("dante", H, t("Die Göttliche Komödie", "The Divine Comedy"), t("Dante Alighieri", "Dante Alighieri"), null, null, t("ein Versepos", "an epic poem"),
        t("Ein Dichter wandert durch Hölle, Läuterungsberg und Paradies, zuerst geführt vom römischen Dichter Vergil.", "A poet journeys through Hell, Purgatory and Paradise, first guided by the Roman poet Virgil."),
        t("Das Werk prägte die italienische Sprache; „Komödie“ heißt es, weil es schlimm beginnt und gut endet.", "The work shaped the Italian language; it is called a \"comedy\" because it begins badly and ends well."),
        shown = t("um 1320 vollendet", "completed around 1320")),
    Work("odyssee", E, t("Odyssee", "The Odyssey"), t("Homer", "Homer"), null, null, t("ein Epos", "an epic"),
        t("Ein listiger König braucht nach einem großen Krieg zehn Jahre für die Heimreise auf seine Insel, wo seine Frau auf ihn wartet.", "After a great war a cunning king needs ten years to get home to his island, where his wife is waiting for him."),
        t("Ob es Homer wirklich gab, ist umstritten; das Wort „Odyssee“ steht heute für jede lange Irrfahrt.", "Whether Homer really existed is disputed; the word \"odyssey\" today stands for any long wandering journey."),
        shown = t("um 700 v. Chr.", "around 700 BC")),
    Work("oedipus", M, t("König Ödipus", "Oedipus the King"), t("Sophokles", "Sophocles"), null, null, TRAGEDY,
        t("Ein König will die Ursache einer Seuche finden und entdeckt, dass er selbst unwissentlich seinen Vater getötet und seine Mutter geheiratet hat.", "A king tries to find the cause of a plague and discovers that he himself unknowingly killed his father and married his mother."),
        t("Aristoteles hielt das Stück für die vollkommene Tragödie; Sigmund Freud benannte nach der Hauptfigur den Ödipuskomplex.", "Aristotle considered the play the perfect tragedy; Sigmund Freud named the Oedipus complex after its hero."),
        shown = t("um 429 v. Chr.", "around 429 BC")),
    Work("frankenstein", E, t("Frankenstein", "Frankenstein"), t("Mary Shelley", "Mary Shelley"), 1818, null, NOVEL,
        t("Ein junger Forscher erschafft aus Leichenteilen ein lebendiges Wesen und flieht entsetzt vor seinem Geschöpf.", "A young scientist creates a living being from parts of corpses and flees in horror from his creature."),
        t("Mary Shelley war 18, als sie die Geschichte 1816 am Genfersee bei einem Wettstreit um die beste Gespenstergeschichte begann; „Frankenstein“ ist der Name des Forschers, nicht des Wesens.", "Mary Shelley was 18 when she began the story in 1816 at Lake Geneva during a contest for the best ghost story; \"Frankenstein\" is the scientist's name, not the creature's.")),
    Work("austen", E, t("Stolz und Vorurteil", "Pride and Prejudice"), t("Jane Austen", "Jane Austen"), 1813, null, NOVEL,
        t("Eine kluge junge Frau und ein stolzer, reicher Mann müssen erst ihre Vorurteile übereinander überwinden.", "A clever young woman and a proud, rich man must first overcome their prejudices about each other."),
        t("Austen veröffentlichte ihre Romane ohne ihren Namen; Elizabeth Bennet und Mr. Darcy wurden zum Vorbild unzähliger Liebesgeschichten.", "Austen published her novels without her name; Elizabeth Bennet and Mr Darcy became the model for countless love stories.")),
    Work("dostojewski", M, t("Schuld und Sühne", "Crime and Punishment"), t("Fjodor Dostojewski", "Fyodor Dostoevsky"), 1866, null, NOVEL,
        t("Ein armer Student in Sankt Petersburg erschlägt eine Pfandleiherin, um zu beweisen, dass außergewöhnliche Menschen über der Moral stehen, und zerbricht an seiner Schuld.", "A poor student in St Petersburg kills a pawnbroker to prove that extraordinary people stand above morality, and is broken by his guilt."),
        t("Der Roman ist Kriminalgeschichte und Seelenstudie zugleich; Dostojewski war selbst zum Tod verurteilt und erst im letzten Moment begnadigt worden.", "The novel is both a crime story and a psychological study; Dostoevsky himself had been sentenced to death and pardoned only at the last moment.")),
    Work("tolstoi", M, t("Krieg und Frieden", "War and Peace"), t("Leo Tolstoi", "Leo Tolstoy"), 1869, null, NOVEL,
        t("Mehrere russische Adelsfamilien erleben Liebe, Ehe und Tod in der Zeit der Kriege gegen Napoleon.", "Several Russian noble families experience love, marriage and death in the time of the wars against Napoleon."),
        t("Mit rund 500 Figuren gehört es zu den umfangreichsten Romanen der Weltliteratur.", "With around 500 characters it is one of the most extensive novels in world literature.")),
    Work("bovary", H, t("Madame Bovary", "Madame Bovary"), t("Gustave Flaubert", "Gustave Flaubert"), 1857, null, NOVEL,
        t("Die Frau eines Landarztes träumt vom großen Leben aus Romanen, flüchtet in Affären und Schulden und findet keinen Ausweg mehr.", "A country doctor's wife dreams of the grand life she knows from novels, escapes into affairs and debts and can find no way out."),
        t("Flaubert wurde wegen Verstoßes gegen die guten Sitten angeklagt und freigesprochen; er feilte oft tagelang an einem einzigen Satz.", "Flaubert was tried for offending public morals and acquitted; he often polished a single sentence for days.")),
    Work("orwell", E, t("1984", "Nineteen Eighty-Four"), t("George Orwell", "George Orwell"), 1949, null, NOVEL,
        t("In einem totalitären Staat werden alle Bürger über Bildschirme überwacht; selbst die Sprache wird umgebaut, damit kein Widerspruch mehr denkbar ist.", "In a totalitarian state all citizens are watched through screens; even the language is rebuilt so that no dissent can be thought any more."),
        t("Begriffe wie „Großer Bruder“ und „Neusprech“ stammen aus diesem Roman und prägen bis heute Debatten über Überwachung.", "Terms such as \"Big Brother\" and \"Newspeak\" come from this novel and still shape debates about surveillance.")),
    Work("huxley", M, t("Schöne neue Welt", "Brave New World"), t("Aldous Huxley", "Aldous Huxley"), 1932, null, NOVEL,
        t("Menschen werden im Labor gezüchtet, in Kasten eingeteilt und mit Konsum und einer Glücksdroge zufrieden gehalten.", "People are bred in laboratories, sorted into castes and kept content with consumption and a happiness drug."),
        t("Anders als in Orwells „1984“ herrscht hier keine Angst, sondern Vergnügen; viele halten gerade diese Vision für die wahrscheinlichere.", "Unlike Orwell's Nineteen Eighty-Four, this world is ruled not by fear but by pleasure; many consider this vision the more likely one.")),
    Work("camus", H, t("Der Fremde", "The Stranger"), t("Albert Camus", "Albert Camus"), 1942, null, NOVEL,
        t("Ein gleichgültiger Mann erschießt am Strand von Algier einen anderen Mann; verurteilt wird er auch dafür, dass er bei der Beerdigung seiner Mutter nicht geweint hat.", "An indifferent man shoots another man on a beach near Algiers; he is condemned partly because he did not cry at his mother's funeral."),
        t("Der Roman ist ein Hauptwerk über das Absurde; Camus bekam 1957 als einer der jüngsten Preisträger den Literaturnobelpreis.", "The novel is a major work about the absurd; in 1957 Camus became one of the youngest winners of the Nobel Prize in Literature.")),
    Work("prinz", E, t("Der kleine Prinz", "The Little Prince"), t("Antoine de Saint-Exupéry", "Antoine de Saint-Exupéry"), 1943, null, STORY,
        t("Ein Pilot muss in der Wüste notlanden und trifft einen Jungen von einem winzigen Planeten, der dort eine Rose zurückgelassen hat.", "A pilot makes an emergency landing in the desert and meets a boy from a tiny planet who has left a rose behind there."),
        t("Der Autor, selbst Pilot, verschwand 1944 bei einem Aufklärungsflug über dem Mittelmeer; das Buch gehört zu den meistübersetzten der Welt.", "The author, a pilot himself, disappeared on a reconnaissance flight over the Mediterranean in 1944; the book is one of the most translated in the world.")),
    Work("marquez", H, t("Hundert Jahre Einsamkeit", "One Hundred Years of Solitude"), t("Gabriel García Márquez", "Gabriel García Márquez"), 1967, null, NOVEL,
        t("Sieben Generationen einer Familie im erfundenen Dorf Macondo, erzählt so, dass Wunder ganz selbstverständlich wirken.", "Seven generations of a family in the invented village of Macondo, told so that miracles seem perfectly natural."),
        t("Der Roman ist das bekannteste Werk des magischen Realismus; García Márquez bekam 1982 den Literaturnobelpreis.", "The novel is the best-known work of magical realism; García Márquez won the Nobel Prize in Literature in 1982.")),
    Work("nora", H, t("Nora oder Ein Puppenheim", "A Doll's House"), t("Henrik Ibsen", "Henrik Ibsen"), 1879, null, DRAMA,
        t("Eine Ehefrau, von ihrem Mann wie ein Spielzeug behandelt, verlässt am Ende Mann und Kinder, um selbst ein Mensch zu werden.", "A wife whom her husband treats like a toy leaves her husband and children at the end in order to become a person in her own right."),
        t("Das Zuschlagen der Tür am Ende sorgte europaweit für Empörung; für eine Aufführung in Deutschland musste Ibsen sogar einen versöhnlichen Schluss schreiben.", "The slamming of the door at the end caused outrage across Europe; for a production in Germany Ibsen even had to write a conciliatory ending.")),
    Work("godot", M, t("Warten auf Godot", "Waiting for Godot"), t("Samuel Beckett", "Samuel Beckett"), 1952, null, DRAMA,
        t("Zwei Landstreicher warten an einer Landstraße bei einem kahlen Baum auf jemanden, der nie kommt.", "Two tramps wait by a country road next to a bare tree for someone who never comes."),
        t("Ein Hauptwerk des absurden Theaters; Beckett schrieb es zuerst auf Französisch und bekam 1969 den Literaturnobelpreis.", "A major work of the theatre of the absurd; Beckett wrote it first in French and won the Nobel Prize in Literature in 1969.")),
)

/** Werke, deren Beschreibungen man leicht verwechselt: nie gegenseitig als Ablenker. */
private val WORK_CLASH = listOf(
    setOf("romeo", "romeodorf"),
    setOf("effi", "bovary"),
    setOf("orwell", "huxley"),
)

private fun workClash(a: Work, b: Work): Boolean = a === b || WORK_CLASH.any { a.key in it && b.key in it }

private fun Work.date(): String = shown?.s ?: year.toString()

private fun Work.byText(): String = by?.s ?: tr("von ${author.s}", "by ${author.s}")

/** „1774, Sturm und Drang“ */
private fun Work.dated(): String = date() + (epochOf(epoch)?.let { ", " + it.name.s } ?: "")

private fun Work.background(): String {
    val ep = epochOf(epoch)
    val head = tr("„${title.s}“ ist ${genre.s} ${byText()} (${date()}).", "\"${title.s}\" is ${genre.s} ${byText()} (${date()}).")
    val era = ep?.let { tr(" Epoche: ${it.name.s}.", " Period: ${it.name.s}.") } ?: ""
    return "$head$era ${desc.s} ${note.s}"
}

private fun workDefs(): List<TaskDef> = WORKS.map { w ->
    lab("lit.work.${w.key}", Topic.LitWorks, w.level) {
        val ep = epochOf(w.epoch)
        val kinds = buildList {
            add(0); add(1); add(2)
            if (ep != null) add(3)
            if (w.year != null && w.level != Difficulty.Easy) add(4)
        }
        val title = w.title.s
        val sameAuthor = WORKS.firstOrNull { it !== w && it.author.de == w.author.de }
        when (pick(kinds)) {
            0 -> quiz(
                "x", Topic.LitWorks, w.level,
                question = tr("Wer schrieb „$title“?", "Who wrote \"$title\"?"),
                prompt = tr("Autor oder Autorin", "Author"),
                correct = w.author.s,
                wrong = draw(WORKS.map { it.author }.distinctBy { it.de }, 3) { it.de != w.author.de }.map { it.s },
                solution = tr("„$title“ stammt ${w.byText()}.", "\"$title\" is ${w.byText()}."),
                background = w.background(),
                hint = w.dated(),
                hint2 = sameAuthor?.let { tr("Vom selben Autor: „${it.title.s}“", "By the same author: \"${it.title.s}\"") },
            )
            1 -> quiz(
                "x", Topic.LitWorks, w.level,
                question = w.desc.s,
                prompt = tr("Welches Werk ist gemeint?", "Which work is this?"),
                correct = title,
                wrong = draw(WORKS, 3) { !workClash(it, w) }.map { it.title.s },
                solution = tr("Gemeint ist „$title“ ${w.byText()}.", "This is \"$title\" ${w.byText()}."),
                background = w.background(),
                hint = w.dated(),
                hint2 = tr("Geschrieben ${w.byText()}", "Written ${w.byText()}"),
            )
            2 -> quiz(
                "x", Topic.LitWorks, w.level,
                question = tr("Welches Werk stammt ${w.byText()}?", "Which work is ${w.byText()}?"),
                prompt = tr("Der passende Titel", "The matching title"),
                correct = title,
                wrong = draw(WORKS, 3) { it.author.de != w.author.de && !workClash(it, w) }.map { it.title.s },
                solution = tr("${w.author.s}: „$title“ (${w.date()})", "${w.author.s}: \"$title\" (${w.date()})"),
                background = w.background(),
                hint = w.dated(),
                hint2 = w.desc.s,
            )
            3 -> quiz(
                "x", Topic.LitWorks, w.level,
                question = tr("Zu welcher Epoche gehört „$title“ ${w.byText()} (${w.date()})?", "Which period does \"$title\" ${w.byText()} (${w.date()}) belong to?"),
                prompt = tr("Die Epoche", "The period"),
                correct = ep!!.name.s,
                wrong = draw(EPOCHS, 3) { !epochClash(it.key, ep.key) }.map { it.name.s },
                solution = tr("„$title“ gehört zur Epoche „${ep.name.s}“ (${ep.span.s}).", "\"$title\" belongs to the period \"${ep.name.s}\" (${ep.span.s})."),
                background = w.background(),
                hint = tr("Erschienen: ${w.date()}", "Published: ${w.date()}"),
                hint2 = ep.features.s,
            )
            else -> yearTask(
                "x", Topic.LitWorks, w.level,
                question = tr("In welchem Jahr kam „$title“ ${w.byText()} heraus?", "In which year did \"$title\" ${w.byText()} come out?"),
                year = w.year!!,
                tolerance = if (w.year >= 1700) 10 else 15,
                solution = tr("„$title“ kam ${w.year} heraus.", "\"$title\" came out in ${w.year}."),
                background = w.background(),
                hint = ep?.let { tr("Epoche: ${it.name.s}, ${it.span.s}", "Period: ${it.name.s}, ${it.span.s}") },
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Stilmittel
// ---------------------------------------------------------------------------------------------

private class Device(
    val key: String,
    val level: Difficulty,
    val name: T,
    val def: T,
    val example: T,
    /** Tipp beim Beispiel: worauf man achten soll */
    val look: T,
    val why: T,
)

private val DEVICES = listOf(
    Device("metapher", E, t("Metapher", "Metaphor"),
        t("Ein Wort wird in übertragener Bedeutung gebraucht, ohne „wie“: Ein Bild ersetzt die eigentliche Sache.", "A word is used in a transferred sense, without \"like\": an image replaces the actual thing."),
        t("Er ist ein Fels in der Brandung.", "He is a rock in the storm."),
        t("Ist das wörtlich gemeint? Und fehlt ein „wie“?", "Is it meant literally? And is there a \"like\"?"),
        t("Gemeint ist nicht, dass er aus Stein ist, sondern dass er in schweren Zeiten ruhig und stark bleibt. Eine Metapher ist wie ein verkürzter Vergleich ohne „wie“; Aristoteles hielt die Gabe für gute Metaphern für ein Zeichen von Begabung.", "It doesn't mean he's made of stone, but that he stays calm and strong in hard times. A metaphor is like a shortened comparison without \"like\"; Aristotle saw a gift for good metaphors as a sign of talent.")),
    Device("vergleich", E, t("Vergleich", "Simile"),
        t("Zwei Dinge werden ausdrücklich mit „wie“ oder „als ob“ verbunden.", "Two things are explicitly linked with \"like\" or \"as\"."),
        t("Sie flitzt wie ein Wiesel über den Platz.", "She darts across the square like a weasel."),
        t("Achte auf das kleine Wort „wie“.", "Look out for the little word \"like\"."),
        t("Das Wort „wie“ macht den Vergleich sichtbar; ohne „wie“ („Sie ist ein Wiesel“) wäre es eine Metapher. Gute Vergleiche machen Unbekanntes anschaulich, indem sie es mit Bekanntem verbinden.", "The word \"like\" makes the comparison visible; without it (\"She is a weasel\") it would be a metaphor. Good similes make the unfamiliar vivid by linking it to the familiar.")),
    Device("personifikation", E, t("Personifikation", "Personification"),
        t("Dingen, Tieren oder Begriffen werden menschliche Eigenschaften oder Handlungen gegeben.", "Things, animals or ideas are given human traits or actions."),
        t("Der Wind flüstert in den Bäumen.", "The wind whispers in the trees."),
        t("Wer kann das eigentlich nur: ein Mensch oder der Wind?", "Who can actually do that: a person or the wind?"),
        t("Nur Menschen flüstern; der Wind wird hier vermenschlicht. So wirkt die Natur lebendig und nah, und man spürt ihre Stimmung. Die Personifikation ist eine besondere Form der Metapher.", "Only people whisper; the wind is humanised here. This makes nature feel alive and close, and you can sense its mood. Personification is a special kind of metaphor.")),
    Device("allegorie", H, t("Allegorie", "Allegory"),
        t("Ein abstrakter Begriff wird durch ein ausgestaltetes Bild oder eine Figur dargestellt.", "An abstract idea is shown through an elaborate image or figure."),
        t("Eine Frau mit verbundenen Augen, Waage und Schwert steht für die Gerechtigkeit.", "A blindfolded woman with scales and a sword stands for justice."),
        t("Eine ganze Figur steht für einen abstrakten Begriff.", "A whole figure stands for an abstract idea."),
        t("Justitia ist eine Allegorie: Jedes Detail hat eine Bedeutung. Die Augenbinde steht für Unparteilichkeit, die Waage für das Abwägen, das Schwert für die Macht, das Urteil durchzusetzen. Auch der Tod als Sensenmann ist eine Allegorie.", "Justitia is an allegory: every detail has a meaning. The blindfold stands for impartiality, the scales for weighing up, the sword for the power to enforce the verdict. Death as the Grim Reaper is an allegory too.")),
    Device("synaesthesie", H, t("Synästhesie", "Synaesthesia"),
        t("Eindrücke verschiedener Sinne werden miteinander verschmolzen.", "Impressions from different senses are merged."),
        t("Sie sang mit einer samtweichen, dunklen Stimme.", "She sang in a velvety, dark voice."),
        t("Mit welchem Sinn nimmt man eine Stimme wahr, und zu welchen Sinnen gehören „samtweich“ und „dunkel“?", "Which sense perceives a voice, and which senses do \"velvety\" and \"dark\" belong to?"),
        t("Eine Stimme kann man weder anfassen noch sehen, trotzdem versteht jeder „samtweich“ und „dunkel“. Die Romantiker liebten die Synästhesie, weil sie zeigt, wie alle Sinne zusammenhängen.", "You can neither touch nor see a voice, yet everyone understands \"velvety\" and \"dark\". The Romantics loved synaesthesia because it shows how all the senses are connected.")),
    Device("hyperbel", E, t("Hyperbel", "Hyperbole"),
        t("Eine starke, nicht wörtlich gemeinte Übertreibung.", "A strong exaggeration that is not meant literally."),
        t("Ich habe dir das schon tausendmal gesagt!", "I've told you a thousand times!"),
        t("Ist die Zahl wörtlich gemeint?", "Is the number meant literally?"),
        t("Niemand hat wirklich bis tausend gezählt; die Übertreibung macht den Ärger spürbar. Hyperbeln sind im Alltag überall: „todmüde“, „ein Meer von Tränen“, „steinalt“.", "Nobody really counted to a thousand; the exaggeration makes the annoyance felt. Hyperbole is everywhere in everyday speech: \"dead tired\", \"a sea of tears\", \"ancient\".")),
    Device("ironie", E, t("Ironie", "Irony"),
        t("Man sagt das Gegenteil von dem, was man meint, und erwartet, dass der andere es merkt.", "You say the opposite of what you mean and expect the other person to notice."),
        t("„Na toll, schon wieder Regen“, sagt sie am Morgen des Ausflugs.", "\"Oh great, rain again,\" she says on the morning of the trip."),
        t("Meint sie wirklich, was sie sagt?", "Does she really mean what she says?"),
        t("Sie findet den Regen natürlich gar nicht toll. Ironie lebt vom Zusammenhang und vom Tonfall; geschrieben wird sie leicht missverstanden. Sokrates war berühmt dafür, sich ironisch dümmer zu stellen, als er war.", "Of course she doesn't think the rain is great. Irony depends on context and tone of voice; in writing it is easily misunderstood. Socrates was famous for ironically pretending to know less than he did.")),
    Device("rhetfrage", E, t("Rhetorische Frage", "Rhetorical question"),
        t("Eine Frage, auf die keine Antwort erwartet wird, weil die Antwort schon feststeht.", "A question to which no answer is expected because the answer is already clear."),
        t("Wer will schon ewig im Stau stehen?", "Who wants to sit in traffic forever?"),
        t("Erwartet hier jemand wirklich eine Antwort?", "Is anyone really expecting an answer here?"),
        t("Die Antwort „niemand“ denkt jeder sofort mit; so bindet die Frage den Zuhörer ein und macht eine Behauptung eindringlicher. In Reden und in der Werbung ist sie besonders beliebt.", "Everyone silently answers \"nobody\"; that way the question draws the listener in and makes a claim more forceful. It is especially popular in speeches and advertising.")),
    Device("anapher", M, t("Anapher", "Anaphora"),
        t("Mehrere Sätze, Satzteile oder Verse beginnen mit demselben Wort.", "Several sentences, clauses or lines begin with the same word."),
        t("Ich will lachen, ich will tanzen, ich will leben.", "I want to laugh, I want to dance, I want to live."),
        t("Schau auf den Anfang jedes Teils.", "Look at the start of each part."),
        t("Die Wiederholung am Anfang erzeugt Rhythmus und Nachdruck, fast wie Trommelschläge. Ein berühmtes Beispiel ist Martin Luther Kings Rede, in der „I have a dream“ immer wiederkehrt.", "Repetition at the start creates rhythm and emphasis, almost like drumbeats. A famous example is Martin Luther King's speech in which \"I have a dream\" keeps returning.")),
    Device("epipher", M, t("Epipher", "Epistrophe"),
        t("Mehrere Sätze, Satzteile oder Verse enden mit demselben Wort.", "Several sentences, clauses or lines end with the same word."),
        t("Wir wollen Frieden, ihr wollt Frieden, alle wollen Frieden.", "We want peace, you want peace, everyone wants peace."),
        t("Schau auf das Ende jedes Teils.", "Look at the end of each part."),
        t("Das Wort am Ende bleibt hängen, weil es zuletzt kommt und immer wiederkehrt. Die Epipher ist das Gegenstück zur Anapher, bei der die Wiederholung am Anfang steht; beide zusammen heißen Symploke.", "The word at the end sticks because it comes last and keeps returning. Epistrophe is the counterpart of anaphora, where the repetition comes at the start; the two together are called symploce.")),
    Device("alliteration", E, t("Alliteration", "Alliteration"),
        t("Mehrere aufeinanderfolgende Wörter beginnen mit demselben Laut.", "Several words in a row begin with the same sound."),
        t("Milch macht müde Männer munter.", "Peter Piper picked a peck of pickled peppers."),
        t("Sprich es laut: Womit beginnen die Wörter?", "Say it aloud: how do the words begin?"),
        t("Der gleiche Anlaut macht Sätze klangvoll und leicht zu merken; deshalb nutzen ihn Werbung, Sprichwörter und Markennamen. Feste Wendungen wie „Kind und Kegel“ oder „Haus und Hof“ sind Alliterationen.", "The same initial sound makes phrases sound good and easy to remember; that's why advertising, sayings and brand names use it. Fixed phrases such as \"busy as a bee\" or \"time will tell\" are alliterations.")),
    Device("klimax", M, t("Klimax", "Climax"),
        t("Eine Steigerung über mehrere Glieder, vom Schwächeren zum Stärkeren.", "A build-up over several steps, from weaker to stronger."),
        t("Das ist wichtig, ja dringend, ja lebensnotwendig.", "This is important, urgent, a matter of life and death."),
        t("Wird es von Glied zu Glied stärker?", "Does it get stronger from step to step?"),
        t("Jedes Glied ist stärker als das vorige, die Spannung wächst. Das berühmteste Beispiel ist Caesars „Ich kam, ich sah, ich siegte“. Das Gegenstück, die Abschwächung, heißt Antiklimax.", "Each step is stronger than the last, and the tension grows. The most famous example is Caesar's \"I came, I saw, I conquered\". The opposite, a weakening, is called anticlimax.")),
    Device("antithese", M, t("Antithese", "Antithesis"),
        t("Gegensätzliche Begriffe oder Gedanken werden einander gegenübergestellt.", "Opposing words or ideas are set against each other."),
        t("„Was dieser heute baut, reißt jener morgen ein.“ (Gryphius)", "\"Many are called, but few are chosen.\" (Bible)"),
        t("Welche Wörter sind Gegensätze?", "Which words are opposites?"),
        t("Die Gegensätze prallen aufeinander, und gerade dadurch wird der Gedanke besonders deutlich. Im Barock war die Antithese das wichtigste Stilmittel, oft betont durch die Pause in der Mitte des Alexandriners.", "The opposites collide, and that is exactly what makes the idea so clear. In the Baroque, antithesis was the most important device, often stressed by the pause in the middle of the alexandrine.")),
    Device("chiasmus", H, t("Chiasmus", "Chiasmus"),
        t("Satzglieder werden über Kreuz angeordnet, nach dem Muster AB–BA.", "Parts of a phrase are arranged crosswise, in the pattern AB–BA."),
        t("„Die Kunst ist lang, und kurz ist unser Leben.“ (Goethe)", "\"Fair is foul, and foul is fair.\" (Shakespeare)"),
        t("Verbinde die gleichen oder gegensätzlichen Begriffe mit Linien: Kreuzen sie sich?", "Link the matching or opposite words with lines: do they cross?"),
        t("Die Reihenfolge kehrt sich um; verbindet man die Paare, entsteht ein Kreuz. Der Name kommt vom griechischen Buchstaben Chi (χ), der wie ein Kreuz aussieht. Oft trägt der Chiasmus wie hier einen Gegensatz.", "The order is reversed; linking the pairs draws a cross. The name comes from the Greek letter chi (χ), which looks like a cross. As here, chiasmus often carries a contrast.")),
    Device("oxymoron", M, t("Oxymoron", "Oxymoron"),
        t("Zwei sich widersprechende Begriffe werden direkt zu einer Einheit verbunden.", "Two contradictory words are joined directly into one unit."),
        t("Ein beredtes Schweigen erfüllte den Raum.", "A deafening silence filled the room."),
        t("Welche zwei Wörter passen eigentlich nicht zusammen?", "Which two words don't really go together?"),
        t("Das Schweigen kann eigentlich nicht reden; gerade der Widerspruch macht das Bild stark. Weitere Beispiele sind „bittersüß“ und „Hassliebe“. Das Wort ist selbst eines: griechisch oxys heißt scharf, moros dumm.", "Silence can't really make a sound; the contradiction itself makes the image strong. Other examples are \"bittersweet\" and \"living death\". The word is one itself: Greek oxys means sharp, moros foolish.")),
    Device("paradoxon", H, t("Paradoxon", "Paradox"),
        t("Eine Aussage, die widersinnig wirkt, bei genauerem Nachdenken aber einen tieferen Sinn hat.", "A statement that seems absurd but on reflection has a deeper meaning."),
        t("Wer sein Leben gewinnen will, wird es verlieren.", "Whoever wants to save his life will lose it."),
        t("Der ganze Satz scheint sich selbst zu widersprechen.", "The whole sentence seems to contradict itself."),
        t("Gemeint ist, dass ein Leben, das nur um sich selbst kreist, sein Ziel verfehlt. Anders als beim Oxymoron steckt der Widerspruch hier in einer ganzen Aussage, nicht in zwei Wörtern.", "It means that a life revolving only around itself misses its goal. Unlike an oxymoron, the contradiction lies in a whole statement, not in two words.")),
    Device("parallelismus", M, t("Parallelismus", "Parallelism"),
        t("Aufeinanderfolgende Sätze oder Satzteile haben denselben Aufbau.", "Successive sentences or clauses have the same structure."),
        t("Die Kinder spielen im Hof, die Eltern sitzen im Garten.", "The children play in the yard, the parents sit in the garden."),
        t("Vergleiche den Bau der beiden Satzhälften.", "Compare how the two halves are built."),
        t("Beide Hälften sind gleich gebaut: Wer, was tut er, wo. Das schafft Ordnung und Rhythmus und lädt zum Vergleich ein; oft verbindet sich der Parallelismus mit einer Anapher oder einer Antithese.", "Both halves are built the same way: who, does what, where. This creates order and rhythm and invites comparison; parallelism often combines with anaphora or antithesis.")),
    Device("ellipse", M, t("Ellipse", "Ellipsis"),
        t("Ein Satz ist unvollständig; Wörter, die man leicht ergänzen kann, fehlen.", "A sentence is incomplete; words that are easy to fill in are left out."),
        t("Ende gut, alles gut.", "No pain, no gain."),
        t("Fehlt da nicht etwas?", "Isn't something missing?"),
        t("Vollständig hieße es etwa „Wenn das Ende gut ist, ist alles gut“. Die Verkürzung macht den Satz knapp und einprägsam; in Gedichten kann sie auch Aufregung oder Atemlosigkeit ausdrücken.", "In full it would be something like \"If there is no pain, there is no gain\". The shortening makes the phrase punchy and memorable; in poems it can also express excitement or breathlessness.")),
    Device("neologismus", M, t("Neologismus", "Neologism"),
        t("Ein neu gebildetes Wort, das es so vorher nicht gab.", "A newly coined word that didn't exist before."),
        t("Nach dem dritten Montag in Folge fühlte er sich zukunftsmüde.", "After the third Monday in a row he felt future-weary."),
        t("Steht dieses Wort im Wörterbuch?", "Is this word in the dictionary?"),
        t("„Zukunftsmüde“ steht in keinem Wörterbuch, aber jeder versteht es. Dichter erfinden Wörter, um etwas Neues auszudrücken, und manche setzen sich durch: „Weltschmerz“ (Jean Paul) oder „googeln“ waren einmal Neologismen.", "\"Future-weary\" is in no dictionary, yet everyone understands it. Writers invent words to express something new, and some catch on: \"serendipity\" (Horace Walpole) or \"to google\" were once neologisms.")),
    Device("euphemismus", M, t("Euphemismus", "Euphemism"),
        t("Etwas Unangenehmes wird beschönigend oder verhüllend ausgedrückt.", "Something unpleasant is expressed in a softened or veiled way."),
        t("Opa ist friedlich eingeschlafen.", "Grandpa has passed away."),
        t("Was ist eigentlich passiert, und wie wird es gesagt?", "What actually happened, and how is it said?"),
        t("Gemeint ist, dass er gestorben ist; die Wendung schont die Gefühle. Euphemismen können aber auch täuschen, etwa „Freisetzung“ statt Entlassung oder „Kollateralschaden“ für getötete Zivilisten.", "It means that he has died; the phrase spares feelings. But euphemisms can also deceive, such as \"letting go\" for firing or \"collateral damage\" for killed civilians.")),
    Device("litotes", H, t("Litotes", "Litotes"),
        t("Etwas wird betont, indem man das Gegenteil verneint.", "Something is emphasised by denying its opposite."),
        t("Das war gar nicht schlecht.", "That wasn't bad at all."),
        t("Was ist mit „nicht schlecht“ eigentlich gemeint?", "What does \"not bad\" actually mean?"),
        t("Gemeint ist „Das war gut“, oft sogar „sehr gut“. Die Verneinung wirkt bescheiden oder vorsichtig und kann gerade dadurch besonders stark loben. Ihr Gegenstück ist die Hyperbel, die übertreibt.", "It means \"That was good\", often even \"very good\". The negation sounds modest or cautious and can praise all the more strongly for it. Its counterpart is hyperbole, which exaggerates.")),
    Device("parsprototo", H, t("Pars pro toto", "Pars pro toto"),
        t("Ein Teil steht für das Ganze.", "A part stands for the whole."),
        t("Wir brauchen endlich ein Dach über dem Kopf.", "We finally need a roof over our heads."),
        t("Wird nur ein Teil genannt, aber mehr gemeint?", "Is only a part named, but more meant?"),
        t("Gemeint ist eine ganze Wohnung, genannt wird nur das Dach. Weitere Beispiele: „Köpfe zählen“ für Menschen oder „unter vier Augen“ für zwei Personen. Lateinisch heißt es „ein Teil für das Ganze“.", "What's meant is a whole home, but only the roof is named. Other examples: \"all hands on deck\" for the whole crew, or \"heads\" for people. It's Latin for \"a part for the whole\".")),
    Device("lautmalerei", E, t("Lautmalerei", "Onomatopoeia"),
        t("Wörter ahmen einen Klang oder ein Geräusch nach.", "Words imitate a sound or noise."),
        t("Die Bienen summen und brummen im Klee.", "The bees buzz and hum in the clover."),
        t("Wie klingen die Wörter?", "What do the words sound like?"),
        t("„Summen“ und „brummen“ klingen fast wie die Bienen selbst. Comics leben von Lautmalerei (peng, zack, wumm), und in Gedichten macht sie Szenen hörbar. Der Fachbegriff heißt Onomatopoesie.", "\"Buzz\" and \"hum\" sound almost like the bees themselves. Comics thrive on onomatopoeia (bang, zap, boom), and in poems it makes scenes audible.")),
    Device("inversion", H, t("Inversion", "Inversion"),
        t("Die übliche Wortstellung wird umgestellt, um etwas zu betonen.", "The usual word order is changed to emphasise something."),
        t("Groß war die Freude im ganzen Dorf.", "Great was the joy in the whole village."),
        t("Wie wäre die gewöhnliche Wortstellung?", "What would the normal word order be?"),
        t("Normal hieße es „Die Freude war groß“; durch die Umstellung rückt „groß“ nach vorn und wird betont. In Gedichten hilft die Inversion auch, das Versmaß oder einen Reim einzuhalten.", "Normally it would be \"The joy was great\"; moving \"great\" to the front emphasises it. In poems inversion also helps to keep the metre or a rhyme.")),
)

/** Beispiele, die auch zu einem anderen Stilmittel passen: nie gegenseitig als Ablenker. */
private val DEVICE_CLASH = listOf(
    setOf("metapher", "personifikation", "allegorie"),
    setOf("metapher", "synaesthesie"),
    setOf("metapher", "parsprototo"),
    setOf("metapher", "euphemismus"),
    setOf("litotes", "euphemismus"),
    setOf("antithese", "chiasmus", "oxymoron", "paradoxon"),
    setOf("anapher", "epipher", "parallelismus", "klimax", "ellipse"),
)

private fun deviceClash(a: Device, b: Device): Boolean = a === b || DEVICE_CLASH.any { a.key in it && b.key in it }

private fun deviceDefs(): List<TaskDef> = DEVICES.map { d ->
    lab("lit.device.${d.key}", Topic.LitDevices, d.level) {
        val background = tr("Beispiel: „${d.example.s}“ ", "Example: \"${d.example.s}\" ") + d.why.s
        val others = draw(DEVICES, 3) { !deviceClash(it, d) }
        val solution = tr("${d.name.s}: ${d.def.s}", "${d.name.s}: ${d.def.s}")
        when (int(0, 2)) {
            0 -> quiz(
                "x", Topic.LitDevices, d.level,
                question = tr("„${d.example.s}“", "\"${d.example.s}\""),
                prompt = tr("Welches Stilmittel steckt vor allem darin?", "Which device is mainly at work here?"),
                correct = d.name.s, wrong = others.map { it.name.s },
                solution = solution, background = background,
                hint = d.look.s, hint2 = d.def.s,
            )
            1 -> quiz(
                "x", Topic.LitDevices, d.level,
                question = tr("Was versteht man unter „${d.name.s}“?", "What is meant by \"${d.name.s}\"?"),
                prompt = tr("Die richtige Erklärung", "The right explanation"),
                correct = d.def.s, wrong = others.map { it.def.s },
                solution = solution, background = background,
                hint = tr("Ein Beispiel: „${d.example.s}“", "An example: \"${d.example.s}\""),
            )
            else -> quiz(
                "x", Topic.LitDevices, d.level,
                question = d.def.s,
                prompt = tr("Wie heißt dieses Stilmittel?", "What is this device called?"),
                correct = d.name.s, wrong = others.map { it.name.s },
                solution = solution, background = background,
                hint = tr("Ein Beispiel: „${d.example.s}“", "An example: \"${d.example.s}\""),
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Gattungen, Erzählweisen und Fachbegriffe
// ---------------------------------------------------------------------------------------------

private class Form(val key: String, val level: Difficulty, val name: T, val def: T, val hint: T, val why: T)

private val FORMS = listOf(
    Form("sonett", M, t("Sonett", "Sonnet"),
        t("Ein Gedicht aus 14 Versen, in der klassischen Form zwei Quartette und zwei Terzette.", "A poem of 14 lines, in the classic form two quatrains and two tercets."),
        t("Italienisch sonetto: kleiner Klang", "Italian sonetto: little sound"),
        t("Das Sonett entstand im 13. Jahrhundert in Italien und wurde durch Petrarca berühmt. Oft stellen die Quartette eine These und eine Gegenthese auf, die Terzette ziehen die Folgerung. Shakespeare schrieb 154 Sonette in einer englischen Variante.", "The sonnet arose in 13th-century Italy and became famous through Petrarch. Often the quatrains set up a thesis and a counter-thesis, and the tercets draw the conclusion. Shakespeare wrote 154 sonnets in an English variant.")),
    Form("ballade", M, t("Ballade", "Ballad"),
        t("Ein erzählendes Gedicht mit spannender Handlung, oft mit wörtlicher Rede und dramatischem Höhepunkt.", "A narrative poem with an exciting plot, often with direct speech and a dramatic climax."),
        t("Goethe und Schiller, 1797", "Goethe and Schiller, 1797"),
        t("Goethe nannte die Ballade ein „Ur-Ei“ der Dichtung, weil sie Erzählung, Gedicht und Drama in sich vereint. Berühmt sind „Erlkönig“, „Der Zauberlehrling“ und Schillers „Die Bürgschaft“.", "Goethe called the ballad the \"primal egg\" of poetry because it unites story, poem and drama. Famous examples are The Erl-King, The Sorcerer's Apprentice and Schiller's The Pledge.")),
    Form("novelle", M, t("Novelle", "Novella"),
        t("Eine mittellange Erzählung über eine einzelne, außergewöhnliche Begebenheit mit einem deutlichen Wendepunkt.", "A medium-length story about a single, extraordinary event with a clear turning point."),
        t("Italienisch novella: kleine Neuigkeit", "Italian novella: a little piece of news"),
        t("Goethe nannte die Novelle eine „sich ereignete unerhörte Begebenheit“. Typisch sind ein straffer Aufbau, ein Dingsymbol (nach Paul Heyse der „Falke“) und oft eine Rahmenhandlung, etwa in Storms „Schimmelreiter“.", "Goethe called the novella \"an unheard-of event that has taken place\". Typical features are a tight structure, a central symbol (Paul Heyse's \"falcon\") and often a frame story, as in Storm's The Rider on the White Horse.")),
    Form("kurzgeschichte", M, t("Kurzgeschichte", "Short story"),
        t("Ein kurzer Prosatext mit offenem Anfang, offenem Ende und Alltagsfiguren in einer entscheidenden Situation.", "A short prose text with an open beginning, an open ending and everyday characters in a decisive situation."),
        t("In Deutschland nach 1945 sehr beliebt", "Very popular in Germany after 1945"),
        t("Die Kurzgeschichte kam aus den USA (short story) und wurde in Deutschland nach 1945 sehr beliebt, etwa bei Wolfgang Borchert und Heinrich Böll. Man wird ohne Einleitung ins Geschehen geworfen, und das Ende lässt Fragen offen.", "The short story came from the USA and became very popular in Germany after 1945, for example with Wolfgang Borchert and Heinrich Böll. You are thrown into the action without an introduction, and the ending leaves questions open.")),
    Form("parabel", H, t("Parabel", "Parable"),
        t("Eine gleichnishafte Erzählung, deren Sinn man selbst auf eine allgemeine Wahrheit übertragen muss.", "A story told as a comparison, whose meaning you must transfer to a general truth yourself."),
        t("Griechisch parabole: Gleichnis, Nebeneinanderstellen", "Greek parabole: comparison, placing side by side"),
        t("Die Parabel sagt ihre Lehre nicht direkt; der Leser muss sie entschlüsseln. Berühmt sind Lessings Ringparabel, Kafkas „Vor dem Gesetz“ und die Gleichnisse der Bibel.", "A parable does not state its lesson directly; the reader has to decode it. Famous examples are Lessing's parable of the rings, Kafka's Before the Law and the parables of the Bible.")),
    Form("fabel", E, t("Fabel", "Fable"),
        t("Eine kurze Erzählung, in der Tiere wie Menschen handeln, mit einer Lehre am Ende.", "A short story in which animals act like people, with a lesson at the end."),
        t("„Der Fuchs und die Trauben“", "\"The Fox and the Grapes\""),
        t("Die ältesten Fabeln werden dem Griechen Äsop zugeschrieben (6. Jahrhundert v. Chr.). Tiere stehen für menschliche Eigenschaften, der Fuchs für List, der Löwe für Macht. In der Aufklärung schrieb Lessing viele Fabeln.", "The oldest fables are attributed to the Greek Aesop (6th century BC). Animals stand for human traits, the fox for cunning, the lion for power. In the Enlightenment Lessing wrote many fables.")),
    Form("haiku", E, t("Haiku", "Haiku"),
        t("Ein japanisches Kurzgedicht aus drei Zeilen mit 5, 7 und 5 Silben, oft über die Natur.", "A short Japanese poem of three lines with 5, 7 and 5 syllables, often about nature."),
        t("Eine sehr kurze Form, die einen einzigen Augenblick festhält", "A very short form that captures a single moment"),
        t("Der bekannteste Haiku-Dichter ist Matsuo Bashō (17. Jahrhundert). Ein Haiku hält einen einzigen Augenblick fest, oft mit einem Hinweis auf die Jahreszeit, und verzichtet auf Erklärungen.", "The best-known haiku poet is Matsuo Bashō (17th century). A haiku captures a single moment, often with a reference to the season, and does without explanations.")),
    Form("epigramm", X, t("Epigramm", "Epigram"),
        t("Ein kurzes, zugespitztes Gedicht, das mit einer überraschenden Pointe endet.", "A short, pointed poem that ends with a surprising twist."),
        t("Griechisch epigramma: Aufschrift", "Greek epigramma: inscription"),
        t("Ursprünglich war es eine Aufschrift auf Grabmälern und Weihegeschenken im alten Griechenland. Lessing beschrieb den Aufbau: Erst wird eine Erwartung geweckt, dann folgt der überraschende Aufschluss.", "Originally it was an inscription on tombs and offerings in ancient Greece. Lessing described its structure: first an expectation is raised, then comes the surprising resolution.")),
    Form("bildungsroman", H, t("Bildungsroman", "Bildungsroman (coming-of-age novel)"),
        t("Ein Roman, der die innere Entwicklung eines jungen Menschen bis zu seinem Platz in der Welt erzählt.", "A novel that tells of a young person's inner development until they find their place in the world."),
        t("Goethes „Wilhelm Meister“", "Goethe's Wilhelm Meister"),
        t("Vorbild ist Goethes „Wilhelm Meisters Lehrjahre“ (1795/96). Der Held lernt durch Irrtümer, Begegnungen und Krisen; viele heutige Jugendromane stehen in dieser Tradition.", "The model is Goethe's Wilhelm Meister's Apprenticeship (1795–96). The hero learns through mistakes, encounters and crises; many of today's young adult novels stand in this tradition.")),
    Form("briefroman", M, t("Briefroman", "Epistolary novel"),
        t("Ein Roman, der ganz oder überwiegend aus Briefen besteht.", "A novel that consists entirely or mostly of letters."),
        t("„Die Leiden des jungen Werthers“", "The Sorrows of Young Werther"),
        t("Briefe lassen die Leser ganz nah an die Gefühle einer Figur heran, ohne Erzähler dazwischen. Im 18. Jahrhundert war die Form sehr beliebt; das bekannteste deutsche Beispiel ist Goethes „Werther“ (1774).", "Letters bring readers very close to a character's feelings, without a narrator in between. The form was very popular in the 18th century; the best-known German example is Goethe's Werther (1774).")),
    Form("episch", H, t("Episches Theater", "Epic theatre"),
        t("Ein Theater, das das Publikum zum kritischen Nachdenken bringen soll, statt es mitfühlen zu lassen, etwa durch Songs und direkte Ansprache.", "A theatre meant to make the audience think critically instead of empathise, for example through songs and direct address."),
        t("Bertolt Brecht", "Bertolt Brecht"),
        t("Bertolt Brecht entwickelte es in den 1920er-Jahren. Mit dem Verfremdungseffekt zeigt er Vertrautes so, dass es fremd wirkt und man fragt, warum es so ist. Gegenbild ist das „aristotelische“ Theater der Einfühlung.", "Bertolt Brecht developed it in the 1920s. With the alienation effect he shows familiar things so that they seem strange and you ask why they are the way they are. Its opposite is the \"Aristotelian\" theatre of empathy.")),
    Form("geschlossen", X, t("Geschlossenes Drama", "Closed-form drama"),
        t("Ein streng gebautes Drama, meist in fünf Akten, mit Einheit von Ort, Zeit und Handlung und gehobener Sprache.", "A strictly built play, usually in five acts, with unity of place, time and action and elevated language."),
        t("Freytags Pyramide passt dazu.", "Freytag's pyramid fits it."),
        t("Die Regeln leitete man aus der „Poetik“ des Aristoteles ab. Beispiele sind Schillers „Maria Stuart“ und Goethes „Iphigenie“. Das offene Drama, etwa Büchners „Woyzeck“, besteht dagegen aus lose verbundenen Szenen.", "The rules were derived from Aristotle's Poetics. Examples are Schiller's Mary Stuart and Goethe's Iphigenia. The open drama, such as Büchner's Woyzeck, consists instead of loosely connected scenes.")),
    Form("bewusstseinsstrom", X, t("Bewusstseinsstrom", "Stream of consciousness"),
        t("Die ungeordneten Gedanken, Gefühle und Eindrücke einer Figur werden ohne Erzählerkommentar wiedergegeben, so wie sie gerade auftauchen.", "A character's disordered thoughts, feelings and impressions are rendered without narrator comment, just as they come up."),
        t("James Joyce, „Ulysses“", "James Joyce, Ulysses"),
        t("Berühmt wurde die Technik durch James Joyce („Ulysses“, 1922) und Virginia Woolf; im Deutschen nutzte sie etwa Arthur Schnitzler in „Leutnant Gustl“ (1900). Sätze brechen ab, springen und vermischen Erinnerung und Gegenwart.", "The technique became famous through James Joyce (Ulysses, 1922) and Virginia Woolf; in German, Arthur Schnitzler used it in Lieutenant Gustl (1900). Sentences break off, jump and mix memory with the present.")),
    Form("lyrischesich", M, t("Lyrisches Ich", "Lyrical I (poetic speaker)"),
        t("Die Stimme, die in einem Gedicht spricht; sie ist nicht einfach dieselbe Person wie der Autor.", "The voice that speaks in a poem; it is not simply the same person as the author."),
        t("Im Roman entspricht ihm der Erzähler.", "In a novel the narrator is its counterpart."),
        t("Wenn ein Gedicht „ich bin so traurig“ sagt, muss der Dichter nicht traurig gewesen sein. Wie bei einer Romanfigur unterscheidet man zwischen der Person, die schreibt, und der Stimme im Text. Den Begriff prägte Margarete Susman (1910).", "When a poem says \"I am so sad\", the poet need not have been sad. As with a character in a novel, you distinguish between the person who writes and the voice in the text. The German term was coined by Margarete Susman (1910).")),
    Form("enjambement", H, t("Enjambement (Zeilensprung)", "Enjambment"),
        t("Ein Satz geht über das Ende eines Verses hinaus und setzt sich im nächsten Vers fort.", "A sentence runs past the end of a line and continues in the next."),
        t("Das Gegenteil heißt Zeilenstil.", "Its opposite is the end-stopped line."),
        t("Französisch enjamber heißt überschreiten. Das Enjambement erzeugt Spannung oder Fluss, weil Satzende und Versende nicht zusammenfallen; fallen sie zusammen, spricht man vom Zeilenstil.", "French enjamber means to step over. Enjambment creates suspense or flow because the end of the sentence and the end of the line don't coincide; when they do, the line is called end-stopped.")),
    Form("kadenz", X, t("Kadenz", "Line ending (masculine or feminine)"),
        t("Die Art, wie ein Vers endet: männlich auf einer betonten, weiblich auf einer unbetonten Silbe.", "The way a line ends: masculine on a stressed syllable, feminine on an unstressed one."),
        t("Lateinisch cadere: fallen", "Latin cadere: to fall"),
        t("„Der Mond ist aufgegangen“ endet weiblich (auf-ge-GAN-gen), ein Vers auf „Nacht“ endet männlich. Wechselnde Kadenzen gliedern viele Strophen und geben ihnen ihren Klang.", "\"Shall I compare thee to a summer's DAY\" ends masculine; \"To be, or not to be: that is the QUES-tion\" ends feminine, with an extra unstressed syllable. Alternating endings shape the sound of many stanzas.")),
    Form("tragoedie", E, t("Tragödie", "Tragedy"),
        t("Ein Drama, in dem die Hauptfigur an einem unlösbaren Konflikt scheitert, oft mit ihrem Tod.", "A play in which the main character fails because of an unsolvable conflict, often dying."),
        t("„König Ödipus“, „Hamlet“", "Oedipus the King, Hamlet"),
        t("Die Tragödie entstand im antiken Athen bei Festen für den Gott Dionysos. Nach Aristoteles soll sie beim Publikum Jammer und Schaudern erregen und es dadurch reinigen (Katharsis).", "Tragedy arose in ancient Athens at festivals for the god Dionysus. According to Aristotle it should arouse pity and fear in the audience and thereby purify them (catharsis).")),
    Form("komoedie", E, t("Komödie", "Comedy"),
        t("Ein Drama mit heiterem Ausgang, das menschliche Schwächen und Verwicklungen vorführt.", "A play with a happy ending that shows human weaknesses and complications."),
        t("Lessings „Minna von Barnhelm“", "Lessing's Minna von Barnhelm"),
        t("Auch die Komödie stammt aus dem antiken Griechenland. Sie lacht über Eitelkeit, Geiz oder Liebeswirren und endet meist versöhnlich, oft mit einer Hochzeit. Deutsche Beispiele sind „Minna von Barnhelm“ und Kleists „Der zerbrochne Krug“.", "Comedy also comes from ancient Greece. It laughs at vanity, greed or romantic mix-ups and usually ends happily, often with a wedding. German examples are Minna von Barnhelm and Kleist's The Broken Jug.")),
    Form("maerchen", E, t("Märchen", "Fairy tale"),
        t("Eine fantastische Erzählung ohne genaue Zeit und Ort, mit Zauberwesen, in der am Ende das Gute siegt.", "A fantastic story with no exact time or place, with magical beings, in which good triumphs in the end."),
        t("Die Brüder Grimm sammelten sie.", "The Brothers Grimm collected them."),
        t("Typisch sind der Anfang „Es war einmal“, sprechende Tiere, die Zahlen Drei und Sieben und ein glückliches Ende. Man unterscheidet Volksmärchen, wie sie die Brüder Grimm sammelten, und Kunstmärchen, etwa von Hans Christian Andersen.", "Typical features are the opening \"Once upon a time\", talking animals, the numbers three and seven and a happy ending. Folk tales, like those collected by the Brothers Grimm, are distinguished from literary fairy tales, such as those by Hans Christian Andersen.")),
    Form("sage", M, t("Sage", "Legend"),
        t("Eine Erzählung mit wunderbaren Elementen, die an einen wirklichen Ort, eine Person oder ein Ereignis gebunden ist.", "A story with marvellous elements that is tied to a real place, person or event."),
        t("Der Rattenfänger von Hameln", "The Pied Piper of Hamelin"),
        t("Anders als das Märchen nennt die Sage einen bestimmten Ort, etwa die Loreley am Rhein oder Hameln mit seinem Rattenfänger, und will oft geglaubt werden. Die Brüder Grimm sammelten auch „Deutsche Sagen“.", "Unlike a fairy tale, a legend names a particular place, such as the Lorelei on the Rhine or Hamelin with its Pied Piper, and often wants to be believed. The Brothers Grimm also collected German legends.")),
    Form("satire", M, t("Satire", "Satire"),
        t("Ein Text, der Missstände oder Personen durch Übertreibung, Spott und Ironie angreift.", "A text that attacks wrongs or people through exaggeration, mockery and irony."),
        t("Jonathan Swift, „Gullivers Reisen“", "Jonathan Swift, Gulliver's Travels"),
        t("Satire will nicht nur unterhalten, sondern etwas verändern. Kurt Tucholsky beantwortete 1919 die Frage, was Satire darf, mit „Alles.“ Klassiker sind Swifts „Gullivers Reisen“ und Heines „Wintermärchen“.", "Satire wants not only to entertain but to change something. In 1919 Kurt Tucholsky answered the question of what satire may do with \"Everything.\" Classics are Swift's Gulliver's Travels and Heine's Germany. A Winter's Tale.")),
    Form("roman", E, t("Roman", "Novel"),
        t("Ein langer erzählender Text in Prosa mit vielen Figuren, Schauplätzen und Handlungssträngen.", "A long narrative text in prose with many characters, settings and storylines."),
        t("„Effi Briest“, „Krieg und Frieden“", "Effi Briest, War and Peace"),
        t("Der Name kommt von Texten in der Volkssprache (lingua romana) statt in Latein. Als erster moderner Roman gilt „Don Quijote“ (1605); im 19. Jahrhundert wurde der Roman zur wichtigsten Gattung.", "The German word Roman comes from texts in the vernacular (lingua romana) instead of Latin. Don Quixote (1605) is considered the first modern novel; in the 19th century the novel became the most important genre.")),
    Form("auktorial", M, t("Auktorialer Erzähler", "Omniscient narrator"),
        t("Ein allwissender Erzähler, der in alle Figuren hineinsehen, vorausdeuten und das Geschehen kommentieren kann.", "An all-knowing narrator who can see into every character, foreshadow and comment on events."),
        t("Lateinisch auctor: Urheber", "Latin auctor: author, originator"),
        t("Der auktoriale Erzähler steht über der Handlung, etwa in Thomas Manns „Buddenbrooks“. Er sagt Dinge wie „Er ahnte noch nicht, dass …“ und lenkt so den Blick der Leser.", "The omniscient narrator stands above the action, as in Thomas Mann's Buddenbrooks. He says things like \"He did not yet suspect that …\" and so steers the reader's view.")),
    Form("personal", H, t("Personaler Erzähler", "Third-person limited narrator"),
        t("Erzählt in der dritten Person, aber nur aus der Sicht einer Figur, ohne mehr zu wissen als sie.", "Narrates in the third person, but only from one character's point of view, knowing no more than they do."),
        t("Kafka erzählt oft so.", "Kafka often narrates this way."),
        t("Man sieht die Welt wie durch die Augen der Figur, etwa in Kafkas „Der Process“: Josef K. weiß nicht, warum er angeklagt ist, und der Leser auch nicht. So entstehen große Nähe und oft Unsicherheit.", "You see the world as if through the character's eyes, as in Kafka's The Trial: Josef K. doesn't know why he is accused, and neither does the reader. This creates great closeness and often uncertainty.")),
    Form("icherzaehler", E, t("Ich-Erzähler", "First-person narrator"),
        t("Eine Figur der Geschichte erzählt selbst, was sie erlebt, in der ersten Person.", "A character in the story tells what they experience themselves, in the first person."),
        t("„Tschick“", "Tschick"),
        t("Der Ich-Erzähler wirkt besonders unmittelbar, kann sich aber auch irren oder lügen; dann spricht man von einem unzuverlässigen Erzähler. Beispiele sind „Tschick“ und „Im Westen nichts Neues“.", "The first-person narrator feels especially immediate but can also be wrong or lie; then one speaks of an unreliable narrator. Examples are Tschick and All Quiet on the Western Front.")),
)

private val FORM_CLASH = listOf(setOf("roman", "bildungsroman", "briefroman"))

private fun formDefs(): List<TaskDef> = FORMS.map { f ->
    lab("lit.form.${f.key}", Topic.LitForms, f.level) {
        val others = draw(FORMS, 3) { it !== f && FORM_CLASH.none { s -> f.key in s && it.key in s } }
        val solution = tr("${f.name.s}: ${f.def.s}", "${f.name.s}: ${f.def.s}")
        if (chance(0.5)) {
            quiz(
                "x", Topic.LitForms, f.level,
                question = tr("Was versteht man unter „${f.name.s}“?", "What is meant by \"${f.name.s}\"?"),
                prompt = tr("Die richtige Erklärung", "The right explanation"),
                correct = f.def.s, wrong = others.map { it.def.s },
                solution = solution, background = f.why.s, hint = f.hint.s,
            )
        } else {
            quiz(
                "x", Topic.LitForms, f.level,
                question = f.def.s,
                prompt = tr("Wie heißt das?", "What is this called?"),
                correct = f.name.s, wrong = others.map { it.name.s },
                solution = solution, background = f.why.s, hint = f.hint.s,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Versmaße und Reime
// ---------------------------------------------------------------------------------------------

private class Metre(
    val key: String,
    val level: Difficulty,
    val name: T,
    /** Muster eines Versfußes, x = unbetont, X = betont */
    val foot: String,
    /** Beispielvers (deutsch / englisch, gemeinfrei oder selbst geschrieben) */
    val line: T,
    val source: T,
    val why: T,
)

private val METRES = listOf(
    Metre("jambus", M, t("Jambus", "Iamb"), "x X",
        t("Der Mond ist aufgegangen", "Shall I compare thee to a summer's day?"),
        t("Matthias Claudius, „Abendlied“ (1779)", "William Shakespeare, Sonnet 18 (1609)"),
        t("Der Jambus ist das häufigste Versmaß im Deutschen: unbetont, betont, wie in „Ge-DICHT“. Goethe und Schiller schrieben ihre klassischen Dramen im Blankvers, einem fünfhebigen Jambus ohne Reim.", "The iamb is the most common metre in English: unstressed, stressed, as in \"a-BOUT\". Shakespeare wrote his plays mostly in blank verse, unrhymed iambic pentameter.")),
    Metre("trochaeus", M, t("Trochäus", "Trochee"), "X x",
        t("Sah ein Knab ein Röslein stehn", "Tyger Tyger, burning bright"),
        t("Goethe, „Heidenröslein“ (1789)", "William Blake, The Tyger (1794)"),
        t("Der Trochäus beginnt betont: betont, unbetont, wie in „SON-ne“. Er wirkt oft volksliedhaft und eingängig; viele Kinderreime und Lieder sind trochäisch.", "The trochee starts stressed: stressed, unstressed, as in \"GAR-den\". It often sounds chant-like and catchy; many nursery rhymes are trochaic.")),
    Metre("daktylus", H, t("Daktylus", "Dactyl"), "X x x",
        t("Glänzende Wellen und leuchtende Sterne", "This is the forest primeval, the murmuring pines and the hemlocks"),
        t("selbst geschriebenes Beispiel", "Henry W. Longfellow, Evangeline (1847)"),
        t("Der Daktylus (griechisch „Finger“: ein langes und zwei kurze Glieder) bildet den Hexameter, das Versmaß der Epen Homers. Im Deutschen klingt er schwingend, fast wie ein Walzer.", "The dactyl (Greek for \"finger\": one long and two short joints) forms the hexameter, the metre of Homer's epics. In English it sounds rolling, almost like a waltz.")),
    Metre("anapaest", H, t("Anapäst", "Anapaest"), "x x X",
        t("Und es wallet und siedet und brauset und zischt", "'Twas the night before Christmas, when all through the house"),
        t("Schiller, „Der Taucher“ (1797)", "Clement C. Moore, A Visit from St. Nicholas (1823)"),
        t("Der Anapäst ist ein umgedrehter Daktylus: zwei unbetonte Silben, dann eine betonte. Er treibt den Vers nach vorn; Schiller ahmt damit das brodelnde Wasser im Strudel nach.", "The anapaest is a reversed dactyl: two unstressed syllables, then a stressed one. It drives the line forward, which suits galloping, lively poems.")),
)

private fun Metre.pattern(): String = List(3) { foot }.joinToString(" | ") + " …"

private fun metreDefs(): List<TaskDef> = METRES.map { m ->
    lab("lit.metre.${m.key}", Topic.LitForms, m.level) {
        val others = METRES.filter { it !== m }
        val legend = tr("x = unbetont, X = betont", "x = unstressed, X = stressed")
        val background = tr("${m.name.s}: ${m.foot} ($legend). ", "${m.name.s}: ${m.foot} ($legend). ") + m.why.s
        when (int(0, 2)) {
            0 -> quiz(
                "x", Topic.LitForms, m.level,
                question = tr("Welches Versmaß hat dieses Muster? ($legend)  ${m.pattern()}", "Which metre has this pattern? ($legend)  ${m.pattern()}"),
                prompt = tr("Das Versmaß", "The metre"),
                correct = m.name.s, wrong = others.map { it.name.s },
                solution = tr("${m.foot} ist ein ${m.name.s}.", "${m.foot} is a${if (m.name.en.first() in "AEIOU") "n" else ""} ${m.name.s.lowercase()}."),
                background = background,
                hint = tr("Beginnt der Versfuß betont oder unbetont, und wie viele Silben hat er?", "Does the foot start stressed or unstressed, and how many syllables does it have?"),
            )
            1 -> quiz(
                "x", Topic.LitForms, m.level,
                question = tr("„${m.line.s}“ (${m.source.s})", "\"${m.line.s}\" (${m.source.s})"),
                prompt = tr("Welches Versmaß hat dieser Vers?", "Which metre does this line have?"),
                correct = m.name.s, wrong = others.map { it.name.s },
                solution = tr("Der Vers ist ein ${m.name.s}: ${m.pattern()}", "The line is in ${m.name.s.lowercase()}s: ${m.pattern()}"),
                background = background,
                hint = tr("Lies den Vers halblaut und klopf bei jeder betonten Silbe.", "Read the line half aloud and tap on every stressed syllable."),
                hint2 = tr("Das Muster: ${m.pattern()} ($legend)", "The pattern: ${m.pattern()} ($legend)"),
            )
            else -> quiz(
                "x", Topic.LitForms, m.level,
                question = tr("Wie ist ein ${m.name.s} aufgebaut? ($legend)", "How is a${if (m.name.en.first() in "AEIOU") "n" else ""} ${m.name.s.lowercase()} built? ($legend)"),
                prompt = tr("Das Muster eines Versfußes", "The pattern of one foot"),
                correct = m.foot, wrong = others.map { it.foot },
                solution = tr("${m.name.s}: ${m.foot}", "${m.name.s}: ${m.foot}"),
                background = background,
                hint = tr("Ein Beispiel: „${m.line.s}“", "An example: \"${m.line.s}\""),
            )
        }
    }
}

private class RhymeScheme(
    val key: String,
    val level: Difficulty,
    val name: T,
    val letters: String,
    val def: T,
    val stanza: T,
    val why: T,
)

private val RHYMES = listOf(
    RhymeScheme("kreuz", E, t("Kreuzreim", "Cross rhyme"), "abab",
        t("Vers 1 reimt sich auf Vers 3, Vers 2 auf Vers 4.", "Line 1 rhymes with line 3, line 2 with line 4."),
        t("Der Morgen kommt mit leisem Schritt, / die Vögel singen schon im Baum, / der Bach nimmt alle Sorgen mit, / und langsam endet jeder Traum.", "The morning comes on quiet feet, / the birds already sing in trees, / the brook runs on with sounds so sweet, / and dreams drift off upon the breeze."),
        t("Die Reime wechseln sich ab wie die Felder eines Schachbretts. Der Kreuzreim ist eine der häufigsten Reimformen in Volksliedern und Gedichten, weil er Ordnung schafft, ohne eintönig zu klingen.", "The rhymes alternate like the squares of a chessboard. Cross rhyme is one of the most common schemes in folk songs and poems because it creates order without sounding monotonous.")),
    RhymeScheme("paar", E, t("Paarreim", "Rhyming couplets"), "aabb",
        t("Je zwei aufeinanderfolgende Verse reimen sich.", "Each pair of consecutive lines rhymes."),
        t("Die Katze sitzt auf dem Dach, / der Hund liegt unten wach. / Der Mond scheint hell und rund, / es schlafen Katz und Hund.", "The cat is sitting on the roof, / the dog below is standing proof. / The moon is shining round and bright, / and both are sleeping through the night."),
        t("Der Paarreim wirkt schlicht und eingängig; er ist typisch für Kinderreime, Sprüche und Bildergeschichten wie Wilhelm Buschs „Max und Moritz“.", "Rhyming couplets sound simple and catchy; they are typical of nursery rhymes, sayings and comic verse such as Wilhelm Busch's Max and Moritz.")),
    RhymeScheme("umarmend", M, t("Umarmender Reim", "Enclosed rhyme"), "abba",
        t("Vers 1 reimt sich auf Vers 4, Vers 2 auf Vers 3.", "Line 1 rhymes with line 4, line 2 with line 3."),
        t("Im Garten blüht ein roter Mohn, / die Bienen fliegen hin und her, / sie summen laut und tragen schwer / und warten auf den süßen Lohn.", "A poppy blooms beside the wall, / the bees fly busy to and fro, / they hum and carry, slow and low, / and wait until the evening's call."),
        t("Die äußeren Verse umschließen die inneren wie eine Umarmung. Man findet diese Form vor allem in den Quartetten des Sonetts, etwa bei Andreas Gryphius.", "The outer lines enclose the inner ones like an embrace. The form is found above all in the quatrains of the Italian sonnet.")),
    RhymeScheme("schweif", H, t("Schweifreim", "Tail rhyme"), "aabccb",
        t("Zwei Paarreime, nach denen jeweils ein Vers folgt; diese beiden Verse reimen sich miteinander.", "Two couplets, each followed by a line; these two lines rhyme with each other."),
        t("Am Morgen steigt der Nebel sacht, / die Wiese hat noch kaum gelacht, / die Sonne schläft im Tal. / Dann bricht ein goldner Strahl hervor, / die Lerche steigt zum Licht empor, / und hell wird es mit einem Mal.", "The mist is rising, soft and slow, / the meadow hardly wakes below, / the sun still sleeps in bed. / Then golden light comes breaking through, / the lark climbs up into the blue, / and all is bright instead."),
        t("Der „Schweif“ reimt sich über die Strophe hinweg. Die Form ist typisch für Kirchenlieder und Volkslieder; auch Claudius' „Der Mond ist aufgegangen“ hat dieses Reimschema.", "The \"tail\" rhymes across the stanza. The form is typical of hymns and folk songs; Claudius' famous German evening song \"Der Mond ist aufgegangen\" uses this scheme too.")),
)

private fun rhymeDefs(): List<TaskDef> = RHYMES.map { r ->
    lab("lit.rhyme.${r.key}", Topic.LitForms, r.level) {
        val others = RHYMES.filter { it !== r }
        val background = tr("${r.name.s} (${r.letters}): ${r.def.s} ", "${r.name.s} (${r.letters}): ${r.def.s} ") + r.why.s
        if (chance(0.6)) {
            quiz(
                "x", Topic.LitForms, r.level,
                question = r.stanza.s,
                prompt = tr("Welches Reimschema hat die Strophe?", "Which rhyme scheme does the stanza have?"),
                correct = "${r.name.s} (${r.letters})", wrong = others.map { "${it.name.s} (${it.letters})" },
                solution = tr("Das Reimschema ist ${r.letters}: ${r.name.s}.", "The rhyme scheme is ${r.letters}: ${r.name.s.lowercase()}."),
                background = background,
                hint = tr("Schreib hinter jeden Vers einen Buchstaben: gleicher Reim, gleicher Buchstabe.", "Put a letter after each line: same rhyme, same letter."),
                hint2 = tr("Wie viele Verse hat die Strophe?", "How many lines does the stanza have?"),
            )
        } else {
            quiz(
                "x", Topic.LitForms, r.level,
                question = tr("Was versteht man unter „${r.name.s}“?", "What does \"${r.name.s}\" mean?"),
                prompt = tr("Die richtige Erklärung", "The right explanation"),
                correct = r.def.s, wrong = others.map { it.def.s },
                solution = tr("${r.name.s} (${r.letters}): ${r.def.s}", "${r.name.s} (${r.letters}): ${r.def.s}"),
                background = background,
                hint = tr("Das Schema: ${r.letters}", "The scheme: ${r.letters}"),
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Genau lesen: geschriebene Aufgaben in mehreren Schritten
// ---------------------------------------------------------------------------------------------

private fun written(): List<TaskDef> = listOf(
    TaskDef("lit.erlkoenig", Topic.LitWorks, M) {
        Task(
            "lit.erlkoenig", Topic.LitWorks, M,
            question = l("In Goethes Ballade „Erlkönig“ (1782) sprechen ein Erzähler, ein Vater, sein Kind und der Erlkönig. Ein Vers lautet: „Mein Sohn, was birgst du so bang dein Gesicht?“", "In Goethe's ballad \"The Erl-King\" (1782) a narrator, a father, his child and the Erl-King speak. One line reads (translated): \"My son, why hide your face so fearfully?\""),
            steps = listOf(
                choice(
                    l("Wer spricht diesen Vers?", "Who speaks this line?"),
                    listOf(l("Der Vater", "The father"), l("Das Kind", "The child"), l("Der Erlkönig", "The Erl-King"), l("Der Erzähler", "The narrator")),
                    done = l("Der Vater", "The father"),
                    hints = listOf(l("Wer nennt das Kind „mein Sohn“?", "Who calls the child \"my son\"?")),
                ),
                choice(
                    l("Wie erklärt der Vater, was das Kind sieht und hört?", "How does the father explain what the child sees and hears?"),
                    listOf(l("Mit der Natur: Nebel, Wind im Laub, alte Weiden", "With nature: mist, wind in the leaves, old willows"), l("Er sieht den Erlkönig auch", "He sees the Erl-King too"), l("Er sagt, das Kind lüge", "He says the child is lying")),
                    done = l("Natürlich: Nebel, Wind, Weiden", "Naturally: mist, wind, willows"),
                    hints = listOf(l("Der Vater will sein Kind beruhigen.", "The father wants to calm his child.")),
                ),
                choice(
                    l("Wie endet die Ballade?", "How does the ballad end?"),
                    listOf(l("Das Kind ist tot", "The child is dead"), l("Der Vater vertreibt den Erlkönig", "The father drives the Erl-King away"), l("Das Kind wacht gesund auf", "The child wakes up healthy")),
                    done = l("„In seinen Armen das Kind war tot.“", "\"In his arms the child was dead.\""),
                    hints = listOf(l("Es ist keine Gutenachtgeschichte.", "It's not a bedtime story.")),
                ),
            ),
            solution = listOf(
                l("Der Vater fragt, er spricht das Kind als „mein Sohn“ an.", "The father asks; he addresses the child as \"my son\"."),
                l("Er deutet alles natürlich: Nebelstreif, dürre Blätter im Wind, alte Weiden.", "He explains everything naturally: a streak of mist, dry leaves in the wind, old willows."),
                l("Am Hof angekommen, hält er ein totes Kind in den Armen.", "When he reaches the farm, he is holding a dead child in his arms."),
            ),
            background = l(
                "Goethe nannte die Ballade ein „Ur-Ei“ der Dichtung, weil sie Erzählung, Dialog und Gefühl verbindet. Spannend ist die Deutung: Ist der Erlkönig eine Fieberfantasie des kranken Kindes oder eine echte, unheimliche Naturmacht? Der Vater steht für die vernünftige, das Kind für die unheimliche Sicht, und das Ende lässt offen, wer recht hatte.",
                "Goethe called the ballad the \"primal egg\" of poetry because it combines story, dialogue and feeling. The interpretation is what makes it exciting: is the Erl-King a fever dream of the sick child or a real, eerie force of nature? The father stands for the rational view, the child for the uncanny one, and the ending leaves open who was right.",
            ),
        )
    },
    TaskDef("lit.ringparabel", Topic.LitWorks, H) {
        Task(
            "lit.ringparabel", Topic.LitWorks, H,
            question = l("In Lessings „Nathan der Weise“ (1779) fragt Sultan Saladin den jüdischen Kaufmann Nathan, welche Religion die wahre sei: Judentum, Christentum oder Islam. Nathan erzählt von einem Ring, der seinen Träger vor Gott und den Menschen angenehm macht und immer an den liebsten Sohn vererbt wird.", "In Lessing's Nathan the Wise (1779) Sultan Saladin asks the Jewish merchant Nathan which religion is the true one: Judaism, Christianity or Islam. Nathan tells of a ring that makes its wearer pleasing to God and people and is always passed on to the favourite son."),
            steps = listOf(
                choice(
                    l("Ein Vater liebt alle drei Söhne gleich. Was tut er?", "A father loves all three sons equally. What does he do?"),
                    listOf(l("Er lässt zwei gleiche Ringe dazu anfertigen", "He has two identical rings made"), l("Er gibt den Ring dem ältesten Sohn", "He gives the ring to the eldest son"), l("Er zerbricht den Ring in drei Teile", "He breaks the ring into three pieces")),
                    done = l("Drei gleiche Ringe", "Three identical rings"),
                    hints = listOf(l("Keiner der Söhne soll leer ausgehen.", "None of the sons is to go without.")),
                ),
                choice(
                    l("Die Söhne streiten vor einem Richter, wer den echten Ring hat. Was rät er ihnen?", "The sons argue before a judge about who has the real ring. What does he advise?"),
                    listOf(l("Jeder soll die Kraft seines Rings durch gutes Handeln beweisen", "Each should prove his ring's power through good deeds"), l("Sie sollen die Ringe von einem Goldschmied prüfen lassen", "They should have a goldsmith test the rings"), l("Der Älteste soll entscheiden", "The eldest should decide")),
                    done = l("Durch gutes Handeln beweisen", "Prove it through good deeds"),
                    hints = listOf(l("Äußerlich lassen sich die Ringe nicht unterscheiden.", "The rings cannot be told apart from the outside.")),
                ),
                choice(
                    l("Was will Lessing damit zeigen?", "What does Lessing want to show?"),
                    listOf(l("Keine Religion kann beweisen, dass nur sie wahr ist; es zählt, wie man handelt", "No religion can prove that it alone is true; what counts is how you act"), l("Alle Religionen sind falsch", "All religions are false"), l("Man sollte über Religion nicht sprechen", "One should not talk about religion")),
                    done = l("Toleranz statt Wahrheitsstreit", "Tolerance instead of a dispute about truth"),
                    hints = listOf(l("Denk an das Ideal der Aufklärung.", "Think of the ideal of the Enlightenment.")),
                ),
            ),
            solution = listOf(
                l("Der Vater lässt zwei Kopien machen, die vom Original nicht zu unterscheiden sind, und gibt jedem Sohn einen Ring.", "The father has two copies made that cannot be told from the original and gives each son a ring."),
                l("Der Richter rät: Jeder soll so leben, dass die Kraft seines Rings sichtbar wird.", "The judge advises: each should live so that the power of his ring becomes visible."),
                l("Die Wahrheit einer Religion zeigt sich nicht im Beweis, sondern im menschlichen Handeln.", "The truth of a religion shows not in proof but in humane action."),
            ),
            background = l(
                "Die Ringparabel ist das Herz des Stücks und ein Schlüsseltext der Aufklärung. Lessing übernahm die Geschichte aus Boccaccios „Decamerone“ und änderte sie entscheidend: Bei ihm wird der echte Ring nie gefunden, er muss sich im Leben zeigen. Weil ihm nach einem Streit mit einem Hamburger Hauptpastor verboten wurde, weiter über Religion zu schreiben, schrieb er ein Theaterstück.",
                "The parable of the rings is the heart of the play and a key text of the Enlightenment. Lessing took the story from Boccaccio's Decameron and changed it decisively: in his version the real ring is never found and has to prove itself in life. Because he had been forbidden to write any more about religion after a dispute with a leading Hamburg pastor, he wrote a play instead.",
            ),
        )
    },
    TaskDef("lit.gretchen", Topic.LitWorks, M) {
        Task(
            "lit.gretchen", Topic.LitWorks, M,
            question = l("In Goethes „Faust I“ fragt eine junge Frau den Gelehrten: „Nun sag, wie hast du's mit der Religion?“", "In Goethe's Faust, Part One, a young woman asks the scholar (translated): \"Now tell me, how do you stand on religion?\""),
            steps = listOf(
                choice(
                    l("Wer stellt die Frage?", "Who asks the question?"),
                    listOf(l("Gretchen (Margarete)", "Gretchen (Margarete)"), l("Mephisto", "Mephisto"), l("Wagner, Fausts Gehilfe", "Wagner, Faust's assistant")),
                    done = l("Gretchen", "Gretchen"),
                    hints = listOf(l("Die Frage hat ihren Namen von ihr.", "The question is named after her.")),
                ),
                choice(
                    l("Was bedeutet „Gretchenfrage“ heute?", "What does \"Gretchen question\" mean in German today?"),
                    listOf(l("Eine direkte Frage nach der entscheidenden Haltung, der man lieber ausweichen würde", "A direct question about someone's decisive stance that they would rather avoid"), l("Eine besonders leichte Frage", "A particularly easy question"), l("Eine Frage, die nur Kinder stellen", "A question only children ask")),
                    done = l("Die entscheidende, unbequeme Frage", "The decisive, awkward question"),
                    hints = listOf(l("Faust antwortet ausweichend. Warum wohl?", "Faust answers evasively. Why might that be?")),
                ),
            ),
            solution = listOf(
                l("Gretchen fragt Faust in der Szene „Marthens Garten“.", "Gretchen asks Faust in the scene \"Martha's Garden\"."),
                l("Wer heute die Gretchenfrage stellt, will wissen, woran er beim anderen wirklich ist, etwa bei einer politischen Grundsatzfrage.", "Whoever asks the \"Gretchen question\" today wants to know where the other person really stands, for example on a political matter of principle."),
            ),
            background = l(
                "Faust weicht mit schönen, schwärmerischen Worten aus, denn er steht im Bund mit dem Teufel. Gretchen spürt, dass etwas nicht stimmt. Aus der Szene wurde ein fester Ausdruck der deutschen Sprache, ähnlich wie „des Pudels Kern“, ebenfalls aus dem „Faust“.",
                "Faust evades with beautiful, rapturous words, because he is in league with the devil. Gretchen senses that something is wrong. The scene gave German a fixed expression, much like \"the poodle's core\" (the heart of the matter), also from Faust.",
            ),
        )
    },
    TaskDef("lit.sonett", Topic.LitForms, H) {
        Task(
            "lit.sonett", Topic.LitForms, H,
            question = l(
                "Andreas Gryphius, „Es ist alles eitel“ (1637), erste Strophe: Du siehst, wohin du siehst, nur Eitelkeit auf Erden. / Was dieser heute baut, reißt jener morgen ein; / Wo jetzund Städte stehn, wird eine Wiese sein, / Auf der ein Schäferskind wird spielen mit den Herden.",
                "William Shakespeare, Sonnet 18 (1609), first four lines: Shall I compare thee to a summer's day? / Thou art more lovely and more temperate: / Rough winds do shake the darling buds of May, / And summer's lease hath all too short a date.",
            ),
            steps = listOf(
                choice(
                    l("Welches Versmaß ist das?", "Which metre is this?"),
                    listOf(l("Alexandriner", "Iambic pentameter"), l("Hexameter", "Dactylic hexameter"), l("Knittelvers", "Trochaic tetrameter")),
                    done = l("Alexandriner: sechs Hebungen, Pause in der Mitte", "Iambic pentameter: five stresses"),
                    hints = listOf(l("Zähl die betonten Silben: Es sind sechs, mit einer Pause nach der dritten.", "Count the stressed syllables: there are five."), l("Der Vers beginnt unbetont und wechselt regelmäßig: unbetont, betont.", "The line starts unstressed and alternates regularly: unstressed, stressed.")),
                ),
                choice(
                    l("Welches Reimschema hat die Strophe?", "Which rhyme scheme do the four lines have?"),
                    listOf(l("abba (umarmender Reim)", "abab (cross rhyme)"), l("abab (Kreuzreim)", "abba (enclosed rhyme)"), l("aabb (Paarreim)", "aabb (couplets)")),
                    done = l("abba: Erden – ein – sein – Herden", "abab: day – temperate – May – date"),
                    hints = listOf(l("Vergleiche die Versenden: Erden, ein, sein, Herden.", "Compare the line endings: day, temperate, May, date.")),
                ),
                choice(
                    l("Welches Grundthema steckt darin?", "What is the central theme?"),
                    listOf(l("Vanitas: Alles Irdische ist vergänglich", "Beauty fades, but the poem keeps it alive"), l("Carpe diem: Genieße das Leben", "Summer is the best season of the year"), l("Die Liebe besiegt den Tod", "Love never changes")),
                    done = l("Vanitas", "Beauty preserved in verse"),
                    hints = listOf(l("Was geschieht mit Städten und Bauten?", "What does the poem say about summer, and what will last longer?")),
                ),
            ),
            solution = listOf(
                l("„du SIEHST, wo-HIN du SIEHST, nur EI-tel-KEIT auf ER-den“: sechs Hebungen, Pause nach der dritten, also ein Alexandriner.", "\"shall I | com-PARE | thee TO | a SUM | mer's DAY\": five iambs, i.e. iambic pentameter."),
                l("Erden reimt auf Herden (a), ein auf sein (b): abba, ein umarmender Reim wie in den Quartetten des italienischen Sonetts.", "day rhymes with May (a), temperate with date (b): abab, the cross rhyme of the English sonnet."),
                l("Was heute steht, ist morgen vergangen: das Vanitas-Motiv des Barock.", "Summer and beauty fade, but, the sonnet ends, \"this gives life to thee\": the poem preserves the beloved."),
            ),
            background = l(
                "Das Sonett kommt aus Italien (Petrarca) und besteht aus zwei Quartetten und zwei Terzetten. Gryphius erlebte den Dreißigjährigen Krieg; seine Sonette mahnen, dass Ruhm, Schönheit und Städte vergehen. Der Alexandriner mit seiner Pause in der Mitte eignet sich ideal für Gegensätze: heute baut, morgen reißt ein.",
                "The sonnet comes from Italy (Petrarch). Shakespeare's 154 sonnets use the English form: three quatrains rhyming abab cdcd efef and a closing couplet gg, which often brings the turn. Sonnet 18 contrasts the brief summer with lasting poetry.",
            ),
        )
    },
    TaskDef("lit.kafka", Topic.LitWorks, M) {
        Task(
            "lit.kafka", Topic.LitWorks, M,
            question = l("Der erste Satz von Franz Kafkas „Die Verwandlung“ (1915): „Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt.“", "The first sentence of Franz Kafka's The Metamorphosis (1915), in translation: \"One morning, waking from troubled dreams, Gregor Samsa found that he had been changed in his bed into an enormous bug.\""),
            steps = listOf(
                choice(
                    l("Was ist am Erzählton auffällig?", "What is striking about the tone?"),
                    listOf(l("Das Unmögliche wird nüchtern wie eine Tatsache berichtet", "The impossible is reported soberly, like a plain fact"), l("Der Erzähler schreit vor Entsetzen", "The narrator screams in horror"), l("Es wird sofort erklärt, wie es dazu kam", "It is immediately explained how it happened")),
                    done = l("Nüchtern wie eine Tatsache", "Sober, like a fact"),
                    hints = listOf(l("Gibt es Ausrufezeichen, Erklärungen, Staunen?", "Are there exclamation marks, explanations, astonishment?")),
                ),
                choice(
                    l("Worüber sorgt sich Gregor auf den ersten Seiten vor allem?", "What does Gregor worry about most on the first pages?"),
                    listOf(l("Dass er seinen Zug zur Arbeit verpasst", "That he will miss his train to work"), l("Wie er wieder ein Mensch wird", "How he can become human again"), l("Wer ihn verzaubert hat", "Who has put a spell on him")),
                    done = l("Um den Zug zur Arbeit", "About the train to work"),
                    hints = listOf(l("Gregor ernährt mit seiner Arbeit die ganze Familie.", "Gregor's job supports the whole family.")),
                ),
                choice(
                    l("Was bedeutet das Wort „kafkaesk“?", "What does the word \"Kafkaesque\" mean?"),
                    listOf(l("Eine undurchschaubare, bedrohliche Lage, in der man anonymen Mächten ausgeliefert ist", "An impenetrable, threatening situation in which you are at the mercy of anonymous powers"), l("Lustig und verspielt", "Funny and playful"), l("Märchenhaft und tröstlich", "Fairy-tale-like and comforting")),
                    done = l("Undurchschaubar und bedrohlich", "Impenetrable and threatening"),
                    hints = listOf(l("Denk auch an „Der Process“.", "Think of The Trial as well.")),
                ),
            ),
            solution = listOf(
                l("Das Ungeheuerliche steht da wie eine Zeitungsmeldung; gerade das macht es so verstörend.", "The monstrous is stated like a news item, and that is exactly what makes it so disturbing."),
                l("Gregor denkt zuerst an den Zug und an seinen Chef, nicht an seinen Körper: ein Bild für ein Leben, das nur noch aus Pflicht besteht.", "Gregor first thinks of the train and his boss, not of his body: an image of a life that consists only of duty."),
                l("„Kafkaesk“ nennt man Lagen wie in Kafkas Texten: absurd, unentrinnbar, ohne erkennbaren Grund.", "\"Kafkaesque\" describes situations like those in Kafka's texts: absurd, inescapable, with no recognisable reason."),
            ),
            background = l(
                "Kafka erzählt das Unmögliche wie Alltag. Gregor, der die Familie mit seiner Arbeit ernährt hat, wird ihr zur Last und von ihr verstoßen. Viele lesen das als Bild für Entfremdung im Arbeitsleben oder für Kafkas Verhältnis zu seinem strengen Vater. Kafka bat seinen Verleger ausdrücklich, das Insekt auf dem Umschlag nicht abzubilden.",
                "Kafka narrates the impossible like everyday life. Gregor, who supported his family with his job, becomes a burden to them and is cast out. Many read this as an image of alienation at work or of Kafka's relationship with his strict father. Kafka explicitly asked his publisher not to show the insect on the cover.",
            ),
        )
    },
    TaskDef("lit.brecht", Topic.LitForms, H) {
        Task(
            "lit.brecht", Topic.LitForms, H,
            question = l("Bertolt Brecht wollte nicht, dass sich die Zuschauer ganz in die Figuren einfühlen und mitweinen. Sein „episches Theater“ arbeitet deshalb mit dem Verfremdungseffekt.", "Bertolt Brecht did not want the audience to identify completely with the characters and weep along. His \"epic theatre\" therefore works with the alienation effect."),
            steps = listOf(
                choice(
                    l("Was ist das Ziel des Verfremdungseffekts?", "What is the aim of the alienation effect?"),
                    listOf(l("Das Publikum soll kritisch nachdenken und die Verhältnisse als veränderbar erkennen", "The audience should think critically and see that circumstances can be changed"), l("Das Publikum soll sich ganz verlieren und alles um sich vergessen", "The audience should lose itself completely and forget everything around it"), l("Die Schauspieler sollen möglichst natürlich wirken", "The actors should seem as natural as possible")),
                    done = l("Kritisch nachdenken", "Think critically"),
                    hints = listOf(l("Brecht wollte die Welt verändern, nicht nur zeigen.", "Brecht wanted to change the world, not just show it.")),
                ),
                choice(
                    l("Welches Mittel gehört zum epischen Theater?", "Which device belongs to epic theatre?"),
                    listOf(l("Ein Song unterbricht die Handlung und kommentiert sie", "A song interrupts the action and comments on it"), l("Der Saal bleibt völlig dunkel, damit man sich ganz verliert", "The auditorium stays completely dark so you lose yourself"), l("Der Vorhang verdeckt jeden Umbau, damit die Illusion bleibt", "The curtain hides every scene change to keep the illusion")),
                    done = l("Songs, die kommentieren", "Songs that comment"),
                    hints = listOf(l("Was stört die Illusion, man säße vor dem echten Leben?", "What disrupts the illusion that you are watching real life?")),
                ),
            ),
            solution = listOf(
                l("Wer nicht gefesselt ist, sieht besser, warum etwas geschieht und dass man es ändern könnte.", "Whoever is not spellbound sees better why something happens and that it could be changed."),
                l("Songs, Tafeln mit Inhaltsangaben, Schauspieler, die das Publikum ansprechen, sichtbare Bühnentechnik: All das bricht die Illusion.", "Songs, placards summarising the scene, actors addressing the audience, visible stage machinery: all of this breaks the illusion."),
            ),
            background = l(
                "Brecht nannte das alte Theater der Einfühlung „aristotelisch“. Bei ihm treten Schauspieler aus ihrer Rolle, Tafeln verraten vorab, was passiert, und Lieder kommentieren das Geschehen. Beispiele sind „Mutter Courage und ihre Kinder“ und „Der gute Mensch von Sezuan“.",
                "Brecht called the old theatre of empathy \"Aristotelian\". In his plays actors step out of their roles, placards reveal in advance what will happen, and songs comment on the action. Examples are Mother Courage and Her Children and The Good Person of Szechwan.",
            ),
        )
    },
    TaskDef("lit.falke", Topic.LitForms, X) {
        Task(
            "lit.falke", Topic.LitForms, X,
            question = l("In einer Geschichte aus Boccaccios „Decamerone“ (um 1350) besitzt ein verarmter Edelmann nur noch einen kostbaren Falken. Als die Frau, die er liebt, ihn besucht, hat er nichts anderes zu essen und brät den Falken für sie. Dabei war sie gekommen, um ihn um genau diesen Falken für ihren kranken Sohn zu bitten.", "In a story from Boccaccio's Decameron (around 1350) an impoverished nobleman owns nothing but a precious falcon. When the woman he loves visits him, he has nothing else to serve and roasts the falcon for her. Yet she had come to ask him for exactly this falcon for her sick son."),
            steps = listOf(
                choice(
                    l("Für welche Gattung leitete Paul Heyse daraus seine „Falkentheorie“ ab?", "For which genre did Paul Heyse derive his \"falcon theory\" from this?"),
                    listOf(l("Novelle", "The novella"), l("Roman", "The novel"), l("Ballade", "The ballad")),
                    done = l("Für die Novelle", "For the novella"),
                    hints = listOf(l("Boccaccios Geschichten sind kürzer als ein Roman und in Prosa.", "Boccaccio's stories are shorter than a novel and in prose.")),
                ),
                choice(
                    l("Was ist nach der Theorie der „Falke“ einer solchen Geschichte?", "According to the theory, what is the \"falcon\" of such a story?"),
                    listOf(l("Ein zentrales Dingsymbol, an dem sich die Handlung wendet", "A central symbolic object on which the plot turns"), l("Der Bösewicht der Geschichte", "The villain of the story"), l("Die Moral am Schluss", "The moral at the end")),
                    done = l("Das Dingsymbol am Wendepunkt", "The symbol at the turning point"),
                    hints = listOf(l("Am Falken entscheidet sich alles.", "Everything is decided by the falcon.")),
                ),
            ),
            solution = listOf(
                l("Heyse (Literaturnobelpreis 1910) nannte das Prinzip nach dieser Geschichte: Jede Novelle brauche ihren „Falken“.", "Heyse (Nobel Prize in Literature 1910) named the principle after this story: every novella needs its \"falcon\"."),
                l("Der Falke ist ein prägnantes Dingsymbol, das die Novelle unverwechselbar macht und an dem sich das Schicksal wendet.", "The falcon is a striking symbolic object that makes the novella unmistakable and on which fate turns."),
            ),
            background = l(
                "Dazu passt Goethes Bestimmung der Novelle als „sich ereignete unerhörte Begebenheit“: ein einzelnes, außergewöhnliches Ereignis mit klarem Wendepunkt. In Storms „Schimmelreiter“ übernimmt etwa das gespenstische weiße Pferd diese Rolle. In Boccaccios Geschichte führt das Opfer am Ende doch zum Glück: Die Frau heiratet den Edelmann.",
                "This fits Goethe's definition of the novella as \"an unheard-of event that has taken place\": a single, extraordinary event with a clear turning point. In Storm's The Rider on the White Horse the ghostly white horse takes on this role. In Boccaccio's story the sacrifice leads to happiness in the end: the woman marries the nobleman.",
            ),
        )
    },
    TaskDef("lit.freytag", Topic.LitForms, H) {
        Task(
            "lit.freytag", Topic.LitForms, H,
            question = l("Gustav Freytag beschrieb 1863 den Aufbau des klassischen Dramas in fünf Akten als Pyramide: Exposition, steigende Handlung, Höhepunkt, fallende Handlung, Katastrophe.", "In 1863 Gustav Freytag described the structure of the classical five-act drama as a pyramid: exposition, rising action, climax, falling action, catastrophe."),
            steps = listOf(
                num(
                    l("In welchem Akt liegt der Höhepunkt?", "In which act is the climax?"), 3.0, "", abs = 0.0,
                    done = l("im 3. Akt", "in act 3"),
                    hints = listOf(l("Die Spitze der Pyramide liegt in der Mitte.", "The top of the pyramid is in the middle.")),
                ),
                choice(
                    l("Wozu dient die Exposition?", "What is the exposition for?"),
                    listOf(l("Sie stellt Figuren, Ort und Konflikt vor", "It introduces characters, setting and conflict"), l("Sie löst den Konflikt auf", "It resolves the conflict"), l("Sie zeigt den Untergang des Helden", "It shows the hero's downfall")),
                    done = l("Einführung in Figuren und Konflikt", "Introduces characters and conflict"),
                    hints = listOf(l("Lateinisch exponere: darlegen", "Latin exponere: to set out")),
                ),
                choice(
                    l("Was ist das „retardierende Moment“ kurz vor dem Ende?", "What is the \"moment of final suspense\" shortly before the end?"),
                    listOf(l("Eine letzte Verzögerung, die noch einmal Hoffnung auf Rettung weckt", "A last delay that raises hope of rescue once more"), l("Die Vorstellung der Figuren am Anfang", "The introduction of the characters at the beginning"), l("Eine Pause für das Publikum", "An interval for the audience")),
                    done = l("Letzte Hoffnung vor der Katastrophe", "Last hope before the catastrophe"),
                    hints = listOf(l("Lateinisch retardare: verzögern", "Latin retardare: to delay")),
                ),
            ),
            solution = listOf(
                l("Fünf Akte, die Spitze liegt in der Mitte: Der Höhepunkt (oft mit der Wende, der Peripetie) kommt im 3. Akt.", "Five acts with the peak in the middle: the climax (often with the reversal, the peripeteia) comes in act 3."),
                l("Die Exposition im 1. Akt führt in Figuren, Ort, Zeit und Grundkonflikt ein.", "The exposition in act 1 introduces the characters, place, time and basic conflict."),
                l("Im 4. Akt scheint für einen Augenblick alles gut zu gehen, bevor im 5. die Katastrophe doch eintritt.", "In act 4 everything seems to turn out well for a moment before the catastrophe strikes in act 5 after all."),
            ),
            background = l(
                "Freytags Pyramide aus „Die Technik des Dramas“ (1863) passt gut auf Dramen wie Schillers „Maria Stuart“ oder Lessings „Emilia Galotti“. Das retardierende Moment steigert die Spannung kurz vor dem Ende. Moderne Stücke, etwa von Brecht, brechen bewusst mit diesem Bau.",
                "Freytag's pyramid from The Technique of the Drama (1863) fits plays such as Schiller's Mary Stuart or Lessing's Emilia Galotti. The moment of final suspense heightens the tension shortly before the end. Modern plays, for example by Brecht, deliberately break with this structure.",
            ),
        )
    },
    TaskDef("lit.brot", Topic.LitForms, M) {
        Task(
            "lit.brot", Topic.LitForms, M,
            question = l("Wolfgang Borcherts Kurzgeschichte „Das Brot“ (1946): Nachts wacht eine Frau auf und findet ihren Mann in der Küche. Auf dem Tisch liegen Krümel; er hat heimlich eine Scheibe Brot gegessen. Beide tun so, als sei nichts gewesen. Am nächsten Abend schiebt sie ihm eine ihrer Scheiben zu und sagt, sie vertrage das Brot abends nicht.", "Wolfgang Borchert's short story \"The Bread\" (1946): at night a woman wakes up and finds her husband in the kitchen. There are crumbs on the table; he has secretly eaten a slice of bread. Both pretend nothing happened. The next evening she pushes one of her slices over to him and says she can't digest bread in the evening."),
            steps = listOf(
                choice(
                    l("Welches Merkmal einer Kurzgeschichte zeigt der Anfang?", "Which feature of a short story does the opening show?"),
                    listOf(l("Man ist ohne Einleitung sofort mitten im Geschehen", "You are in the middle of the action straight away, without an introduction"), l("Ein Erzähler stellt ausführlich alle Figuren vor", "A narrator introduces all the characters in detail"), l("Am Anfang steht die Moral", "The moral comes at the beginning")),
                    done = l("Offener Anfang", "Open beginning"),
                    hints = listOf(l("Erfährt man zuerst, wer die beiden sind und wo sie leben?", "Do you first learn who the two are and where they live?")),
                ),
                choice(
                    l("Was zeigt die Geschichte?", "What does the story show?"),
                    listOf(l("Liebe zeigt sich in einer stillen Geste, die den anderen schont", "Love shows itself in a quiet gesture that spares the other person"), l("Hunger macht Menschen böse", "Hunger makes people wicked"), l("Eine Lüge zerstört jede Ehe", "A lie destroys every marriage")),
                    done = l("Liebe als stille Geste", "Love as a quiet gesture"),
                    hints = listOf(l("Die Frau entlarvt die Notlüge nicht. Warum?", "The woman doesn't expose the white lie. Why not?")),
                ),
            ),
            solution = listOf(
                l("Die Geschichte beginnt mitten in der Nacht, ohne Vorgeschichte; typisch für die Kurzgeschichte.", "The story begins in the middle of the night, without any backstory, as is typical of the short story."),
                l("Die Frau antwortet auf die Notlüge ihres Mannes nicht mit einem Vorwurf, sondern mit einer eigenen, liebevollen Notlüge.", "The woman answers her husband's white lie not with a reproach but with a loving white lie of her own."),
            ),
            background = l(
                "Kurzgeschichten wurden in Deutschland nach 1945 sehr beliebt, angeregt durch amerikanische Vorbilder wie Ernest Hemingway. Typisch sind offener Anfang und offenes Ende, Alltagsmenschen und eine einfache Sprache. „Das Brot“ spielt in der Hungerzeit nach dem Krieg: Ein Stück Brot war damals kostbar.",
                "Short stories became very popular in Germany after 1945, inspired by American models such as Ernest Hemingway. Typical features are an open beginning and ending, ordinary people and plain language. \"The Bread\" is set in the hunger years after the war, when a slice of bread was precious.",
            ),
        )
    },
)

object LabLiterature {
    val all: List<TaskDef> by lazy {
        epochDefs() + epochOrder() + workDefs() + deviceDefs() + formDefs() + metreDefs() + rhymeDefs() + written()
    }
}
