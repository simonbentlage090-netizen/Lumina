package de.funke.app.ui.challenge

import de.funke.app.ui.Lang

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import de.funke.app.ui.tr
import kotlin.math.abs
import kotlin.random.Random

/*
 * Die Aufgaben, die man lösen muss, um eine Session vorzeitig zu beenden.
 *
 * Quellen (alle frei veröffentlichbar):
 *  - eigene Generatoren (Mathe, Physik.kt, Logic.kt, Chemistry.kt, Geography.kt): jede Aufgabe wird neu
 *    ausgewürfelt und bringt ihren Lösungsweg gleich mit. Fakten (Hauptstädte, Elemente, …) sind nicht geschützt.
 *  - Textaufgaben aus GSM8K (OpenAI, MIT-Lizenz), ausgewählt und ins Deutsche übertragen (WordProblems.kt).
 *  - Wissensfragen mit Auswahl (selbst geschrieben, Knowledge.kt und Facts.kt).
 * Zu jedem Lösungsweg passen Formelkarten (Concepts.kt), die man in der Erklärung antippen kann.
 */

/** Wie schwer die Aufgaben sind. */
enum class Difficulty(val id: String, private val de: String, private val en: String) {
    Easy("easy", "Leicht", "Easy"),
    Medium("medium", "Mittel", "Medium"),
    Hard("hard", "Schwer", "Hard"),
    Expert("expert", "Experte", "Expert");

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?): Difficulty = entries.firstOrNull { it.id == id } ?: Medium
    }
}

/** Fächer. Mathe und Physik sind der Schwerpunkt, der Rest sind kleine Zusatzpakete zum Einschalten. */
enum class Subject(val id: String, private val de: String, private val en: String, val color: Long, val symbol: String) {
    Math("math", "Mathe", "Math", 0xFF5B8CFF, "∑"),
    Physics("physics", "Physik", "Physics", 0xFF3AAEE0, "⚡"),
    Logic("logic", "Logik", "Logic", 0xFFA66BFF, "◆"),
    Geography("geography", "Erdkunde", "Geography", 0xFF2ECC9A, "◍"),
    Chemistry("chemistry", "Chemie", "Chemistry", 0xFFFF6B9A, "⚗"),
    Biology("biology", "Biologie", "Biology", 0xFF7BD35A, "❦"),
    History("history", "Geschichte", "History", 0xFFC9A66B, "⧗"),
    Geology("geology", "Geologie", "Geology", 0xFFB08968, "△"),
    Philosophy("philosophy", "Philosophie", "Philosophy", 0xFF9AA5C8, "?"),
    Literature("literature", "Literatur", "Literature", 0xFFF08DB0, "❡"),
    Extras("extras", "Allgemeinwissen", "General knowledge", 0xFFFFC93C, "★");

    val label: String get() = tr(de, en)

    /**
     * Wissensfach: Eine Aufgabe fragt etwas ab, das man beim zweiten Mal schon kennt. Rechenfächer (neue Zahlen
     * bei jedem Ziehen) bleiben auch bei der hundertsten Aufgabe frisch, Wissensfächer brauchen dafür Nachschub
     * von den Nachbarstufen (siehe [Tasks.next], stretch).
     */
    val recall: Boolean get() = this != Math && this != Physics && this != Logic
}

/** Themen, aus denen gewürfelt wird. */
enum class Topic(
    val id: String,
    val subject: Subject,
    private val de: String,
    private val en: String,
    private val deHint: String,
    private val enHint: String,
) {
    Mental("mental", Subject.Math, "Kopfrechnen", "Mental math", "Mal, plus, minus, geschickt zerlegt", "Times tables, sums, clever splitting"),
    Percent("percent", Subject.Math, "Prozent & Alltag", "Percentages & real life", "Rabatte, Anteile, Dreisatz", "Discounts, shares, proportions"),
    Equations("equations", Subject.Math, "Gleichungen", "Equations", "Nach x auflösen", "Solve for x"),
    Sequences("sequences", Subject.Math, "Zahlenfolgen", "Number sequences", "Wie geht es weiter?", "What comes next?"),
    Word("word", Subject.Math, "Textaufgaben", "Word problems", "Aus GSM8K (OpenAI, MIT)", "From GSM8K (OpenAI, MIT)"),
    Calculus("calculus", Subject.Math, "Analysis", "Calculus", "Ableitungen und Integrale", "Derivatives and integrals"),
    Geometry("geometry", Subject.Math, "Geometrie", "Geometry", "Flächen, Winkel, Pythagoras", "Areas, angles, Pythagoras"),
    Probability("probability", Subject.Math, "Wahrscheinlichkeit", "Probability", "Würfel, Karten, Zufall, Kombinieren", "Dice, cards, chance, counting"),
    Motion("physics", Subject.Physics, "Bewegung", "Motion", "Tempo, Beschleunigung, Bremsweg, Würfe", "Speed, acceleration, braking, projectiles"),
    Forces("physics_forces", Subject.Physics, "Kräfte & Newton", "Forces & Newton", "Newtons Gesetze, Reibung, schiefe Ebene, Federn", "Newton's laws, friction, inclines, springs"),
    Energy("physics_energy", Subject.Physics, "Energie & Leistung", "Energy & power", "Arbeit, Lage- und Bewegungsenergie, kWh", "Work, potential and kinetic energy, kWh"),
    Momentum("physics_momentum", Subject.Physics, "Impuls & Stöße", "Momentum & collisions", "Rückstoß, Stöße, Kraftstoß", "Recoil, collisions, impulse"),
    Rotation("physics_rotation", Subject.Physics, "Kreise & Drehung", "Circles & rotation", "Kurven, Hebel, Drehmoment, Drehimpuls", "Curves, levers, torque, angular momentum"),
    Gravity("physics_gravity", Subject.Physics, "Gravitation & Planeten", "Gravity & planets", "Schwerkraft, Umlaufbahnen, Fluchtgeschwindigkeit", "Gravity, orbits, escape velocity"),
    Oscillations("physics_oscillations", Subject.Physics, "Schwingungen", "Oscillations", "Pendel, Federn, Resonanz", "Pendulums, springs, resonance"),
    Matter("physics_matter", Subject.Physics, "Druck & Dichte", "Pressure & density", "Dichte, Wasserdruck, Auftrieb, Luftdruck", "Density, water pressure, buoyancy, air pressure"),
    Heat("physics_heat", Subject.Physics, "Wärme & Thermodynamik", "Heat & thermodynamics", "Erwärmen, Schmelzen, Gase, Wirkungsgrad", "Heating, melting, gases, efficiency"),
    Waves("physics_waves", Subject.Physics, "Schall & Wellen", "Sound & waves", "Schall, Frequenz, Wellenlänge, Doppler", "Sound, frequency, wavelength, Doppler"),
    Optics("physics_optics", Subject.Physics, "Licht & Optik", "Light & optics", "Brechung, Linsen, Spiegel, Farben", "Refraction, lenses, mirrors, colours"),
    Electricity("physics_electricity", Subject.Physics, "Stromkreise", "Circuits", "Ohm, Leistung, Reihe und parallel", "Ohm, power, series and parallel"),
    Fields("physics_fields", Subject.Physics, "Ladung & Felder", "Charge & fields", "Coulomb, Feldstärke, Kondensatoren", "Coulomb, field strength, capacitors"),
    Magnetism("physics_magnetism", Subject.Physics, "Magnetismus & Induktion", "Magnetism & induction", "Lorentzkraft, Spulen, Generatoren, Trafos", "Lorentz force, coils, generators, transformers"),
    Relativity("physics_relativity", Subject.Physics, "Relativität", "Relativity", "Zeitdehnung, Längenkontraktion, E = mc²", "Time dilation, length contraction, E = mc²"),
    Quantum("physics_quantum", Subject.Physics, "Quantenphysik", "Quantum physics", "Photonen, Welle und Teilchen, Unschärfe", "Photons, wave–particle duality, uncertainty"),
    Nuclear("physics_nuclear", Subject.Physics, "Atome & Kerne", "Atoms & nuclei", "Atommodell, Radioaktivität, Halbwertszeit", "Atomic models, radioactivity, half-life"),
    Astro("physics_astro", Subject.Physics, "Astrophysik & Kosmos", "Astrophysics & cosmos", "Sterne, Lichtjahre, Urknall, Expansion", "Stars, light years, Big Bang, expansion"),
    Modern("physics_modern", Subject.Physics, "Teilchen", "Particles", "Standardmodell, Zerfälle, Antimaterie", "Standard model, decays, antimatter"),
    Logic("logic", Subject.Logic, "Logikrätsel", "Logic puzzles", "Reihenfolgen, Alter, Wiegen, Kombinieren", "Ordering, ages, weighing, counting"),
    Geography("geography", Subject.Geography, "Erdkunde", "Geography", "Hauptstädte, Bundesländer, Flüsse, Berge", "Capitals, states, rivers, mountains"),
    Chemistry("chemistry", Subject.Chemistry, "Chemie", "Chemistry", "Elemente, Formeln, Molmasse", "Elements, formulas, molar mass"),
    Biology("biology", Subject.Biology, "Biologie", "Biology", "Zellen, Körper, Pflanzen, Tiere", "Cells, body, plants, animals"),
    History("history", Subject.History, "Geschichte", "History", "Antike bis heute, Erfindungen, Zeitgefühl", "Antiquity to today, inventions, sense of time"),
    Geology("geology", Subject.Geology, "Geologie", "Geology", "Gesteine, Platten, Vulkane, Erdzeitalter", "Rocks, plates, volcanoes, geological time"),
    Philosophy("philosophy", Subject.Philosophy, "Gedankenexperimente", "Thought experiments", "Paradoxien, Rätsel, große Fragen", "Paradoxes, puzzles, big questions"),
    PhilEthics("phil_ethics", Subject.Philosophy, "Ethik", "Ethics", "Was ist gut, was ist gerecht?", "What is good, what is just?"),
    PhilKnowledge("phil_knowledge", Subject.Philosophy, "Erkenntnis & Wissenschaft", "Knowledge & science", "Was können wir wissen?", "What can we know?"),
    PhilMind("phil_mind", Subject.Philosophy, "Geist & Bewusstsein", "Mind & consciousness", "Ich, Gehirn, freier Wille", "Self, brain, free will"),
    PhilPolitics("phil_politics", Subject.Philosophy, "Staat & Gesellschaft", "State & society", "Macht, Freiheit, Gerechtigkeit", "Power, freedom, justice"),
    PhilThinkers("phil_thinkers", Subject.Philosophy, "Denker & Ideen", "Thinkers & ideas", "Wer dachte was?", "Who thought what?"),
    PhilArgument("phil_argument", Subject.Philosophy, "Argumente prüfen", "Testing arguments", "Gültig, schlüssig, Fehlschluss", "Valid, sound, fallacy"),
    LitWorks("lit_works", Subject.Literature, "Werke & Autoren", "Works & authors", "Wer schrieb was, und worum geht es?", "Who wrote what, and what's it about?"),
    LitEpochs("lit_epochs", Subject.Literature, "Epochen", "Periods", "Von der Aufklärung bis heute", "From the Enlightenment to today"),
    LitDevices("lit_devices", Subject.Literature, "Stilmittel", "Literary devices", "Metapher, Anapher, Ironie …", "Metaphor, anaphora, irony …"),
    LitForms("lit_forms", Subject.Literature, "Gattungen & Verse", "Genres & verse", "Novelle, Sonett, Jambus …", "Novella, sonnet, iamb …"),
    Knowledge("knowledge", Subject.Extras, "Allgemeinwissen", "General knowledge", "Gemischte Fragen", "Mixed questions"),
    FunFacts("funfacts", Subject.Extras, "Spaß-Fakten", "Fun facts", "Stimmt das wirklich?", "Is that really true?");

    val label: String get() = tr(de, en)
    val hint: String get() = tr(deHint, enHint)

    companion object {
        fun byId(id: String): Topic? = entries.firstOrNull { it.id == id }

        /** Standard: Mathe und Physik. */
        val Default: Set<Topic> = entries.filter { it.subject == Subject.Math || it.subject == Subject.Physics }.toSet()
    }
}

/** Welche Aufgaben kommen (Einstellung "Aufgaben"). */
@Immutable
data class ChallengeSettings(
    val difficulty: Difficulty = Difficulty.Medium,
    val topics: Set<Topic> = Topic.Default,
)

val LocalChallengeSettings = staticCompositionLocalOf { ChallengeSettings() }

/**
 * Eine Aufgabe.
 * @param answer die Lösung (ganze Zahl). Bei Auswahlfragen der Index der richtigen Antwort in [options].
 * @param steps der Lösungsweg, Schritt für Schritt (wird gezeigt, wenn man nicht weiterkommt).
 * @param source Herkunft, falls die Aufgabe nicht selbst erzeugt ist (z. B. GSM8K).
 */
@Immutable
data class Challenge(
    val topic: Topic,
    val difficulty: Difficulty,
    val question: String,
    val answer: Int,
    val unit: String = "",
    val steps: List<String>,
    val options: List<String>? = null,
    val source: String? = null,
    /** Vorlage aus der Aufgabenbank (für "ähnliche Aufgabe": gleiche Vorlage, neue Zahlen). */
    val templateId: String? = null,
) {
    val isChoice: Boolean get() = options != null

    /** Die Lösung als Text (bei Auswahlfragen die richtige Antwort). */
    val answerText: String get() = options?.getOrNull(answer) ?: (answer.toString() + if (unit.isNotEmpty()) " $unit" else "")
}

object Challenges {
    /**
     * Würfelt eine neue Aufgabe aus den gewählten Themen. [avoid] (z. B. die gerade erklärte Aufgabe)
     * wird möglichst nicht noch einmal gestellt.
     */
    fun next(settings: ChallengeSettings, random: Random = Random.Default, topic: Topic? = null, avoid: Challenge? = null): Challenge {
        val topics = settings.topics.ifEmpty { Topic.Default }.toList()
        // Bis zu 16 Versuche, eine Aufgabe zu finden, deren Art man zuletzt nicht hatte (auch über App-Starts hinweg).
        var fallback: Challenge? = null
        repeat(16) {
            val t = topic ?: topics.random(random)
            val c = make(t, settings.difficulty, random)
            if (avoid != null && c.question == avoid.question) return@repeat
            if (fallback == null) fallback = c
            if (!History.seen(c.historyKey)) {
                History.add(c.historyKey)
                return c
            }
        }
        val c = fallback ?: make(topic ?: topics.random(random), settings.difficulty, random)
        History.add(c.historyKey)
        return c
    }

    /** Woran man "die gleiche Aufgabe" erkennt: Vorlage aus der Bank, sonst die Frage ohne Zahlen. */
    private val Challenge.historyKey: String get() = templateId ?: ("${topic.name}:" + kind.take(90))

    /**
     * Eine ähnliche Aufgabe zum Üben, nachdem der Lösungsweg gezeigt wurde: gleiches Thema, gleiche Art
     * (z. B. wieder "Prozent mit Rabatt"), aber neue Zahlen. Bei Textaufgaben und Wissen eine andere Frage.
     */
    fun similar(c: Challenge, random: Random = Random.Default): Challenge {
        c.templateId?.let { id ->
            Bank.byId(id)?.let { t ->
                repeat(12) { val n = t.build(random); if (n.question != c.question) return n }
            }
        }
        val kind = c.kind
        var fallback: Challenge? = null
        repeat(30) {
            val n = make(c.topic, c.difficulty, random)
            if (n.question != c.question) {
                if (n.kind == kind) return n
                if (fallback == null) fallback = n
            }
        }
        return fallback ?: make(c.topic, c.difficulty, random)
    }

    /** Art einer Aufgabe: Frage und erster Schritt ohne Zahlen. */
    private val Challenge.kind: String get() = (question + "|" + steps.firstOrNull().orEmpty()).filter { !it.isDigit() }

    /**
     * Neue Aufgabe zu Thema und Stufe. Ein Teil kommt aus der Aufgabenbank (Bank.kt): ausgedachte, kniffligere
     * Aufgaben mit wechselnden Zahlen. Je schwerer die Stufe, desto öfter; bei "Experte" immer, wenn es zum
     * Thema welche gibt. Der Rest wird frisch erzeugt.
     */
    fun make(topic: Topic, d: Difficulty, r: Random): Challenge {
        val bank = Bank.tasks(topic, d)
        val chance = when (d) {
            Difficulty.Easy -> 0.30
            Difficulty.Medium -> 0.50
            Difficulty.Hard -> 0.65
            Difficulty.Expert -> 1.0
        }
        if (bank.isNotEmpty() && r.nextDouble() < chance) return Bank.pick(bank, r)
        val base = generate(topic, if (d == Difficulty.Expert) Difficulty.Hard else d, r)
        return if (d == Difficulty.Expert) base.copy(difficulty = Difficulty.Hard) else base
    }

    private fun generate(topic: Topic, d: Difficulty, r: Random): Challenge = when (topic) {
        Topic.Mental -> mental(d, r)
        Topic.Percent -> percent(d, r)
        Topic.Equations -> equation(d, r)
        Topic.Sequences -> sequence(d, r)
        Topic.Calculus -> calculus(d, r)
        Topic.Word -> WordProblems.pick(d, r)
        Topic.Motion -> Physics.motion(d, r)
        Topic.Energy -> Physics.energy(d, r)
        Topic.Electricity -> Physics.electricity(d, r)
        Topic.Matter -> Physics.matter(d, r)
        Topic.Waves -> Physics.waves(d, r)
        Topic.Logic -> Logic.make(d, r)
        Topic.Geography -> Geography.make(d, r)
        Topic.Chemistry -> Chemistry.make(d, r)
        Topic.Biology -> Facts.biology(d, r)
        Topic.FunFacts -> Facts.funFacts(d, r)
        Topic.Knowledge -> Knowledge.pick(d, r)
        // Für diese Themen gibt es nur geschriebene Aufgaben (Tasks); als Notnagel Allgemeinwissen
        Topic.History, Topic.Geology, Topic.Philosophy -> Knowledge.pick(d, r)
        Topic.Geometry, Topic.Probability -> mental(d, r)
        Topic.PhilEthics, Topic.PhilKnowledge, Topic.PhilMind, Topic.PhilPolitics, Topic.PhilThinkers, Topic.PhilArgument,
        Topic.LitWorks, Topic.LitEpochs, Topic.LitDevices, Topic.LitForms -> Knowledge.pick(d, r)
        Topic.Modern -> Physics.waves(d, r)
        // Neue Physik-Themen (10.1) haben Vorlagen statt Generatoren; als Notnagel das Nachbarthema
        Topic.Forces, Topic.Momentum, Topic.Rotation, Topic.Gravity, Topic.Oscillations -> Physics.motion(d, r)
        Topic.Heat -> Physics.matter(d, r)
        Topic.Optics, Topic.Relativity, Topic.Quantum, Topic.Nuclear, Topic.Astro -> Physics.waves(d, r)
        Topic.Fields, Topic.Magnetism -> Physics.electricity(d, r)
    }

    /** Themen mit eingebautem Zahlen-Generator (ältere, einfache Aufgaben). Die neuen Physik-Themen kommen nur aus Vorlagen. */
    val generatedTopics: Set<Topic> = Topic.entries.toSet() - setOf(
        Topic.History, Topic.Geology, Topic.Philosophy, Topic.Modern, Topic.Geometry, Topic.Probability,
        Topic.PhilEthics, Topic.PhilKnowledge, Topic.PhilMind, Topic.PhilPolitics, Topic.PhilThinkers, Topic.PhilArgument,
        Topic.LitWorks, Topic.LitEpochs, Topic.LitDevices, Topic.LitForms,
        Topic.Forces, Topic.Momentum, Topic.Rotation, Topic.Gravity, Topic.Oscillations, Topic.Heat, Topic.Optics,
        Topic.Fields, Topic.Magnetism, Topic.Relativity, Topic.Quantum, Topic.Nuclear, Topic.Astro,
    )

    // ---------------------------------------------------------------------------------------------
    // Kopfrechnen
    // ---------------------------------------------------------------------------------------------

    private fun mental(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> if (r.nextBoolean()) {
            val a = r.nextInt(3, 10)
            val b = r.nextInt(6, 13)
            Challenge(
                Topic.Mental, d, "$a · $b = ?", a * b,
                steps = if (b > 10) listOf(
                    tr("Zerlege $b in 10 + ${b - 10}.", "Split $b into 10 + ${b - 10}."),
                    "$a · 10 = ${a * 10}",
                    "$a · ${b - 10} = ${a * (b - 10)}",
                    "${a * 10} + ${a * (b - 10)} = ${a * b}",
                ) else listOf(
                    tr("Einmaleins. Tipp: $a · $b ist dasselbe wie $b · $a, nimm die Reihe, die du besser kannst.", "Times table. Tip: $a · $b is the same as $b · $a, use the row you know better."),
                    "$a · $b = ${a * b}",
                ),
            )
        } else {
            val a = r.nextInt(24, 90)
            val b = r.nextInt(12, 60)
            val c = r.nextInt(5, 30)
            Challenge(
                Topic.Mental, d, "$a + $b − $c = ?", a + b - c,
                steps = listOf(
                    tr("Von links nach rechts rechnen.", "Work from left to right."),
                    "$a + $b = ${a + b}",
                    "${a + b} − $c = ${a + b - c}",
                ),
            )
        }
        Difficulty.Medium -> if (r.nextBoolean()) {
            val a = r.nextInt(13, 99)
            val b = r.nextInt(3, 10)
            val t = a / 10 * 10
            val o = a % 10
            Challenge(
                Topic.Mental, d, "$a · $b = ?", a * b,
                steps = listOf(
                    tr("Zerlege $a in $t + $o.", "Split $a into $t + $o."),
                    "$t · $b = ${t * b}",
                    "$o · $b = ${o * b}",
                    "${t * b} + ${o * b} = ${a * b}",
                ),
            )
        } else {
            val a = r.nextInt(5, 40)
            val b = r.nextInt(3, 10)
            val c = r.nextInt(3, 10)
            Challenge(
                Topic.Mental, d, "$a + $b · $c = ?", a + b * c,
                steps = listOf(
                    tr("Punkt vor Strich: zuerst malnehmen.", "Multiplication comes before addition."),
                    "$b · $c = ${b * c}",
                    "$a + ${b * c} = ${a + b * c}",
                ),
            )
        }
        else -> if (r.nextBoolean()) {
            val a = r.nextInt(12, 50)
            val b = r.nextInt(13, 50)
            val t = b / 10 * 10
            val o = b % 10
            Challenge(
                Topic.Mental, d, "$a · $b = ?", a * b,
                steps = listOf(
                    tr("Zerlege $b in $t + $o.", "Split $b into $t + $o."),
                    "$a · $t = ${a * t}",
                    "$a · $o = ${a * o}",
                    "${a * t} + ${a * o} = ${a * b}",
                ),
            )
        } else {
            // geschickt: (n − k)(n + k) = n² − k²
            val n = listOf(20, 30, 40, 50, 60).random(r)
            val k = r.nextInt(1, 6)
            Challenge(
                Topic.Mental, d, "${n - k} · ${n + k} = ?", n * n - k * k,
                steps = listOf(
                    tr("Beide Zahlen liegen gleich weit um $n herum: ($n − $k)·($n + $k).", "Both numbers are equally far from $n: ($n − $k)·($n + $k)."),
                    tr("3. binomische Formel: $n² − $k²", "Difference of squares: $n² − $k²"),
                    "${n * n} − ${k * k} = ${n * n - k * k}",
                ),
            )
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Prozent & Alltag
    // ---------------------------------------------------------------------------------------------

    private fun percent(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> {
            val (pct, frac, div) = listOf(Triple(10, "1/10", 10), Triple(20, "1/5", 5), Triple(25, "1/4", 4), Triple(50, "1/2", 2)).random(r)
            val base = div * r.nextInt(3, 25)
            Challenge(
                Topic.Percent, d, tr("Wie viel sind $pct % von $base?", "What is $pct % of $base?"), base / div,
                steps = listOf(
                    tr("$pct % ist dasselbe wie $frac.", "$pct % is the same as $frac."),
                    "$base : $div = ${base / div}",
                ),
            )
        }
        Difficulty.Medium -> {
            val pct = listOf(10, 15, 20, 25, 30, 40).random(r)
            val price = 20 * r.nextInt(2, 15)
            val off = price * pct / 100
            Challenge(
                Topic.Percent, d,
                tr("Ein Pulli kostet $price €. Im Sale gibt es $pct % Rabatt. Wie viel zahlst du?", "A sweater costs €$price. The sale gives $pct % off. How much do you pay?"),
                price - off, "€",
                steps = percentSteps(price, pct) + "$price € − $off € = ${price - off} €",
            )
        }
        else -> if (r.nextBoolean()) {
            // Grundwert gesucht
            // +20 % = 6/5, +25 % = 5/4, +50 % = 3/2
            val (pct, num, den) = listOf(Triple(20, 6, 5), Triple(25, 5, 4), Triple(50, 3, 2)).random(r)
            val base = den * r.nextInt(4, 20)
            val now = base / den * num
            Challenge(
                Topic.Percent, d,
                tr("Nach einer Preiserhöhung um $pct % kostet ein Spiel $now €. Was hat es vorher gekostet?", "After a $pct % price increase, a game costs €$now. What did it cost before?"),
                base, "€",
                steps = listOf(
                    tr("Der neue Preis ist ${100 + pct} % des alten, also $num/$den davon.", "The new price is ${100 + pct} % of the old one, i.e. $num/$den of it."),
                    tr("Rückwärts rechnen, erst durch $num: $now € : $num = ${now / num} €", "Work backwards, first divide by $num: €$now : $num = €${now / num}"),
                    tr("… dann mal $den: ${now / num} € · $den = $base €", "… then times $den: €${now / num} · $den = €$base"),
                ),
            )
        } else {
            // antiproportionaler Dreisatz
            val workers = r.nextInt(2, 7)
            val days = listOf(6, 8, 10, 12, 15, 18, 20, 24).random(r)
            val total = workers * days
            val divisors = (2..12).filter { it != workers && total % it == 0 }
            val w2 = divisors.random(r)
            Challenge(
                Topic.Percent, d,
                tr("$workers Leute streichen einen Zaun in $days Stunden. Wie lange brauchen $w2 Leute (gleich schnell)?", "$workers people paint a fence in $days hours. How long do $w2 people take (same speed)?"),
                total / w2, "h",
                steps = listOf(
                    tr("Mehr Leute, weniger Zeit: Die Gesamtarbeit bleibt gleich.", "More people, less time: the total work stays the same."),
                    tr("Gesamtarbeit: $workers · $days = $total Personenstunden.", "Total work: $workers · $days = $total person-hours."),
                    "$total : $w2 = ${total / w2} h",
                ),
            )
        }
    }

    /** Lösungsweg für "pct % von price" mit glatten Zwischenschritten (price ist ein Vielfaches von 20). */
    private fun percentSteps(price: Int, pct: Int): List<String> {
        val ten = price / 10
        return when {
            pct == 25 -> listOf(tr("25 % ist ein Viertel: $price € : 4 = ${price / 4} €", "25 % is a quarter: €$price : 4 = €${price / 4}"))
            pct == 15 -> listOf(
                tr("10 % von $price € sind $ten €, 5 % die Hälfte davon: ${ten / 2} €.", "10 % of €$price is €$ten, 5 % is half of that: €${ten / 2}."),
                tr("15 % sind also $ten € + ${ten / 2} € = ${ten + ten / 2} €.", "So 15 % is €$ten + €${ten / 2} = €${ten + ten / 2}."),
            )
            else -> listOf(
                tr("10 % von $price € sind $ten €.", "10 % of €$price is €$ten."),
                tr("$pct % sind also ${pct / 10} · $ten € = ${pct / 10 * ten} €.", "So $pct % is ${pct / 10} · €$ten = €${pct / 10 * ten}."),
            )
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Gleichungen
    // ---------------------------------------------------------------------------------------------

    private fun equation(d: Difficulty, r: Random): Challenge {
        val x = r.nextInt(2, 13)
        return when (d) {
            Difficulty.Easy -> if (r.nextBoolean()) {
                val a = r.nextInt(5, 40)
                Challenge(
                    Topic.Equations, d, "x + $a = ${x + a}\n" + tr("Wie groß ist x?", "What is x?"), x,
                    steps = listOf(tr("Auf beiden Seiten $a abziehen.", "Subtract $a on both sides."), "x = ${x + a} − $a = $x"),
                )
            } else {
                val a = r.nextInt(2, 10)
                Challenge(
                    Topic.Equations, d, "$a · x = ${a * x}\n" + tr("Wie groß ist x?", "What is x?"), x,
                    steps = listOf(tr("Beide Seiten durch $a teilen.", "Divide both sides by $a."), "x = ${a * x} : $a = $x"),
                )
            }
            Difficulty.Medium -> {
                val a = r.nextInt(2, 10)
                val b = r.nextInt(1, 30)
                val c = a * x + b
                Challenge(
                    Topic.Equations, d, "${a}x + $b = $c\n" + tr("Wie groß ist x?", "What is x?"), x,
                    steps = listOf(
                        tr("Zuerst $b auf beiden Seiten abziehen: ${a}x = ${c - b}", "First subtract $b on both sides: ${a}x = ${c - b}"),
                        tr("Dann durch $a teilen: x = ${c - b} : $a", "Then divide by $a: x = ${c - b} : $a"),
                        "x = $x",
                    ),
                )
            }
            else -> {
                val c = r.nextInt(2, 7)
                val a = c + r.nextInt(1, 6)
                val b = r.nextInt(-20, 21)
                val dd = (a - c) * x + b
                fun signed(n: Int) = if (n < 0) "− ${abs(n)}" else "+ $n"
                Challenge(
                    Topic.Equations, d, "${a}x ${signed(b)} = ${c}x ${signed(dd)}\n" + tr("Wie groß ist x?", "What is x?"), x,
                    steps = listOf(
                        tr("Alle x auf eine Seite: ${c}x auf beiden Seiten abziehen → ${a - c}x ${signed(b)} = $dd", "Bring all x to one side: subtract ${c}x → ${a - c}x ${signed(b)} = $dd"),
                        tr("Zahl auf die andere Seite: ${a - c}x = ${dd - b}", "Move the number: ${a - c}x = ${dd - b}"),
                        tr("Durch ${a - c} teilen: x = $x", "Divide by ${a - c}: x = $x"),
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Zahlenfolgen
    // ---------------------------------------------------------------------------------------------

    private fun sequence(d: Difficulty, r: Random): Challenge {
        fun ask(terms: List<Int>) = tr("Welche Zahl kommt als Nächstes?", "Which number comes next?") + "\n" + terms.joinToString(", ") + ", ?"
        return when (d) {
            Difficulty.Easy -> {
                val s = r.nextInt(1, 20)
                val k = r.nextInt(2, 10)
                val t = List(5) { s + it * k }
                Challenge(
                    Topic.Sequences, d, ask(t), s + 5 * k,
                    steps = listOf(
                        tr("Schau auf die Abstände: Es kommt jedes Mal $k dazu.", "Look at the gaps: each time $k is added."),
                        "${t.last()} + $k = ${s + 5 * k}",
                    ),
                )
            }
            Difficulty.Medium -> when (r.nextInt(3)) {
                0 -> {
                    val q = listOf(2, 3).random(r)
                    val s = r.nextInt(1, 6)
                    val t = List(5) { var v = s; repeat(it) { v *= q }; v }
                    Challenge(
                        Topic.Sequences, d, ask(t), t.last() * q,
                        steps = listOf(tr("Jede Zahl ist das ${q}-Fache der vorigen.", "Each number is $q times the previous one."), "${t.last()} · $q = ${t.last() * q}"),
                    )
                }
                1 -> {
                    // wächst insgesamt (a > b), damit keine negativen Zahlen vorkommen
                    val a = r.nextInt(3, 10)
                    val b = r.nextInt(1, a)
                    val s = r.nextInt(b + 1, 20)
                    val t = mutableListOf(s)
                    for (i in 1 until 6) t += t.last() + if (i % 2 == 1) a else -b
                    val next = t.last() + a
                    Challenge(
                        Topic.Sequences, d, ask(t), next,
                        steps = listOf(
                            tr("Abwechselnd +$a und −$b.", "Alternating +$a and −$b."),
                            tr("Als Nächstes kommt wieder +$a: ${t.last()} + $a = $next", "Next is +$a again: ${t.last()} + $a = $next"),
                        ),
                    )
                }
                else -> {
                    val s = r.nextInt(1, 6)
                    val t = List(5) { (s + it) * (s + it) }
                    val n = s + 5
                    Challenge(
                        Topic.Sequences, d, ask(t), n * n,
                        steps = listOf(
                            tr("Das sind Quadratzahlen: $s², ${s + 1}², ${s + 2}², …", "These are square numbers: $s², ${s + 1}², ${s + 2}², …"),
                            "$n² = ${n * n}",
                        ),
                    )
                }
            }
            else -> if (r.nextBoolean()) {
                // Abstände wachsen gleichmäßig
                val s = r.nextInt(1, 10)
                val d0 = r.nextInt(1, 5)
                val dd = r.nextInt(2, 4)
                val t = mutableListOf(s)
                var gap = d0
                repeat(5) { t += t.last() + gap; gap += dd }
                Challenge(
                    Topic.Sequences, d, ask(t), t.last() + gap,
                    steps = listOf(
                        tr("Abstände: " + t.zipWithNext { a, b -> "+${b - a}" }.joinToString(" "), "Gaps: " + t.zipWithNext { a, b -> "+${b - a}" }.joinToString(" ")),
                        tr("Die Abstände wachsen jedes Mal um $dd. Der nächste ist +$gap.", "The gaps grow by $dd each time. The next one is +$gap."),
                        "${t.last()} + $gap = ${t.last() + gap}",
                    ),
                )
            } else {
                // Jede Zahl ist die Summe der beiden vorigen
                val a = r.nextInt(1, 6)
                val b = r.nextInt(a, 9)
                val t = mutableListOf(a, b)
                repeat(4) { t += t[t.size - 1] + t[t.size - 2] }
                val next = t[t.size - 1] + t[t.size - 2]
                Challenge(
                    Topic.Sequences, d, ask(t), next,
                    steps = listOf(
                        tr("Jede Zahl ist die Summe der beiden davor (wie bei Fibonacci).", "Each number is the sum of the two before it (like Fibonacci)."),
                        "${t[t.size - 2]} + ${t.last()} = $next",
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Analysis
    // ---------------------------------------------------------------------------------------------

    private fun calculus(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> {
            val a = r.nextInt(2, 8)
            val k = r.nextInt(2, 7)
            Challenge(
                Topic.Calculus, d, "f(x) = ${a}x²\n" + tr("Wie groß ist f′($k)?", "What is f′($k)?"), 2 * a * k,
                steps = listOf(tr("Potenzregel: (x²)′ = 2x, also f′(x) = ${2 * a}x", "Power rule: (x²)′ = 2x, so f′(x) = ${2 * a}x"), "f′($k) = ${2 * a} · $k = ${2 * a * k}"),
            )
        }
        Difficulty.Medium -> {
            val a = r.nextInt(2, 6)
            val b = r.nextInt(2, 9)
            Challenge(
                Topic.Calculus, d, "f(x) = x³ + ${b}x\n" + tr("Wie groß ist f′($a)?", "What is f′($a)?"), 3 * a * a + b,
                steps = listOf(
                    tr("Ableiten: f′(x) = 3x² + $b", "Differentiate: f′(x) = 3x² + $b"),
                    tr("Einsetzen: 3 · $a² + $b = 3 · ${a * a} + $b", "Plug in: 3 · $a² + $b = 3 · ${a * a} + $b"),
                    "= ${3 * a * a + b}",
                ),
            )
        }
        else -> if (r.nextBoolean()) {
            val k = r.nextInt(2, 6)
            val b = r.nextInt(1, 6)
            Challenge(
                Topic.Calculus, d, "∫₀^$k (3x² + ${2 * b}x) dx = ?", k * k * k + b * k * k,
                steps = listOf(
                    tr("Stammfunktion: F(x) = x³ + ${b}x²", "Antiderivative: F(x) = x³ + ${b}x²"),
                    "F($k) − F(0) = ${k * k * k} + $b · ${k * k} − 0",
                    "= ${k * k * k + b * k * k}",
                ),
            )
        } else {
            // Produktregel
            val a = r.nextInt(1, 5)
            val k = r.nextInt(1, 4)
            // f(x) = x² · (x + a)  →  f′(x) = 2x(x + a) + x² = 3x² + 2ax
            Challenge(
                Topic.Calculus, d, "f(x) = x² · (x + $a)\n" + tr("Wie groß ist f′($k)?", "What is f′($k)?"), 3 * k * k + 2 * a * k,
                steps = listOf(
                    tr("Produktregel: (u·v)′ = u′·v + u·v′ mit u = x², v = x + $a", "Product rule: (u·v)′ = u′·v + u·v′ with u = x², v = x + $a"),
                    "f′(x) = 2x · (x + $a) + x² · 1 = 3x² + ${2 * a}x",
                    "f′($k) = 3 · ${k * k} + ${2 * a} · $k = ${3 * k * k + 2 * a * k}",
                ),
            )
        }
    }
}

/** Aufgabe mit Zahl als Antwort. */
internal fun task(topic: Topic, d: Difficulty, question: String, answer: Int, unit: String = "", steps: List<String>) =
    Challenge(topic, d, question, answer, unit, steps)

/** Auswahlfrage: [correct] wird mit bis zu drei [wrong] Antworten gemischt. */
internal fun choice(topic: Topic, d: Difficulty, question: String, correct: String, wrong: List<String>, steps: List<String>, r: Random): Challenge {
    val opts = listOf(correct) + wrong.distinct().filter { it != correct }.take(3)
    val order = opts.indices.shuffled(r)
    return Challenge(topic, d, question, order.indexOf(0), steps = steps, options = order.map { opts[it] })
}

/**
 * Welche Aufgaben-Arten zuletzt dran waren (die letzten 40), im Speicher und in den Einstellungen,
 * damit sich auch nach einem Neustart nicht gleich alles wiederholt.
 */
object History {
    private const val KEY = "task_history"
    private const val MAX = 40
    private val recent = ArrayDeque<String>()
    private var prefs: android.content.SharedPreferences? = null

    fun attach(p: android.content.SharedPreferences?) {
        prefs = p
        recent.clear()
        p?.getString(KEY, null)?.split('\u001F')?.filter { it.isNotEmpty() }?.forEach { recent.addLast(it) }
    }

    fun seen(key: String) = key in recent

    fun add(key: String) {
        recent.remove(key)
        recent.addLast(key)
        while (recent.size > MAX) recent.removeFirst()
        prefs?.edit()?.putString(KEY, recent.joinToString("\u001F"))?.apply()
    }
}

/**
 * Rechenzeichen für die Anzeige: Im Englischen steht für "geteilt" ÷ statt des deutschen Doppelpunkts
 * ("t = s ÷ v" statt "t = s : v"), und Cent wird zu ¢. Im Deutschen bleibt alles, wie es ist.
 */
fun mathText(s: String): String = if (Lang.german) s else s
    .replace(Regex("(?<=\\S) : (?=\\S)"), " ÷ ")
    .replace(Regex("(\\d) ct\\b"), "$1¢")
