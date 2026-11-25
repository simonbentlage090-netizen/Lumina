package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import kotlin.random.Random

/*
 * Wissensfragen mit vier Antworten zur Auswahl (selbst geschrieben, frei verwendbar).
 * Die erste Antwort in der Liste ist jeweils die richtige; beim Stellen werden die Antworten gemischt.
 */

private class KQ(
    val difficulty: Difficulty,
    val de: String,
    val en: String,
    val optionsDe: List<String>,
    val optionsEn: List<String>,
    val whyDe: List<String>,
    val whyEn: List<String>,
)

object Knowledge {
    fun pick(d: Difficulty, r: Random): Challenge {
        val pool = ALL.filter { it.difficulty == d }.ifEmpty { ALL }
        val q = pool.random(r)
        val german = Lang.german
        val options = if (german) q.optionsDe else q.optionsEn
        val order = options.indices.shuffled(r)
        return Challenge(
            topic = Topic.Knowledge,
            difficulty = q.difficulty,
            question = if (german) q.de else q.en,
            answer = order.indexOf(0),
            options = order.map { options[it] },
            steps = if (german) q.whyDe else q.whyEn,
        )
    }

    val count: Int get() = ALL.size

    private val ALL = listOf(
        // --- leicht ---
        KQ(
            Difficulty.Easy,
            "Welcher Planet ist der Sonne am nächsten?", "Which planet is closest to the Sun?",
            listOf("Merkur", "Venus", "Mars", "Erde"), listOf("Mercury", "Venus", "Mars", "Earth"),
            listOf("Die Reihenfolge von innen nach außen: Merkur, Venus, Erde, Mars, …", "Merkur kreist in nur 88 Tagen um die Sonne."),
            listOf("The order from the inside out: Mercury, Venus, Earth, Mars, …", "Mercury orbits the Sun in only 88 days."),
        ),
        KQ(
            Difficulty.Easy,
            "Welches Gas nehmen Pflanzen für die Fotosynthese auf?", "Which gas do plants take in for photosynthesis?",
            listOf("Kohlenstoffdioxid", "Sauerstoff", "Stickstoff", "Wasserstoff"), listOf("Carbon dioxide", "Oxygen", "Nitrogen", "Hydrogen"),
            listOf("Fotosynthese: Kohlenstoffdioxid + Wasser + Licht → Traubenzucker + Sauerstoff.", "Sauerstoff geben Pflanzen also ab, sie nehmen ihn nicht auf."),
            listOf("Photosynthesis: carbon dioxide + water + light → glucose + oxygen.", "So plants release oxygen rather than take it in."),
        ),
        KQ(
            Difficulty.Easy,
            "Wie viele Seiten hat ein Sechseck?", "How many sides does a hexagon have?",
            listOf("6", "5", "8", "7"), listOf("6", "5", "8", "7"),
            listOf("„Hexa“ kommt aus dem Griechischen und heißt sechs.", "Bienenwaben bestehen zum Beispiel aus Sechsecken."),
            listOf("\"Hexa\" is Greek for six.", "Honeycombs, for example, are made of hexagons."),
        ),
        KQ(
            Difficulty.Easy,
            "Wer schrieb „Romeo und Julia“?", "Who wrote \"Romeo and Juliet\"?",
            listOf("William Shakespeare", "Johann Wolfgang von Goethe", "Charles Dickens", "Friedrich Schiller"),
            listOf("William Shakespeare", "Johann Wolfgang von Goethe", "Charles Dickens", "Friedrich Schiller"),
            listOf("Shakespeare schrieb das Drama um 1595 in England.", "Es erzählt von zwei Liebenden aus verfeindeten Familien in Verona."),
            listOf("Shakespeare wrote the play around 1595 in England.", "It tells of two lovers from feuding families in Verona."),
        ),
        KQ(
            Difficulty.Easy,
            "Was ist der größte Ozean der Erde?", "What is the largest ocean on Earth?",
            listOf("Pazifik", "Atlantik", "Indischer Ozean", "Arktischer Ozean"), listOf("Pacific", "Atlantic", "Indian Ocean", "Arctic Ocean"),
            listOf("Der Pazifik bedeckt etwa ein Drittel der Erdoberfläche.", "Er ist größer als alle Landflächen zusammen."),
            listOf("The Pacific covers about a third of Earth's surface.", "It is larger than all land areas combined."),
        ),
        KQ(
            Difficulty.Easy,
            "Bei wie viel Grad Celsius siedet Wasser auf Meereshöhe?", "At what temperature in °C does water boil at sea level?",
            listOf("100 °C", "90 °C", "80 °C", "120 °C"), listOf("100 °C", "90 °C", "80 °C", "120 °C"),
            listOf("Die Celsius-Skala ist so festgelegt: 0 °C Gefrieren, 100 °C Sieden (bei normalem Luftdruck).", "Auf hohen Bergen ist der Luftdruck kleiner, dort siedet Wasser schon früher."),
            listOf("The Celsius scale is defined that way: 0 °C freezing, 100 °C boiling (at normal air pressure).", "On high mountains the air pressure is lower, so water boils earlier."),
        ),
        KQ(
            Difficulty.Easy,
            "Wer malte die „Mona Lisa“?", "Who painted the \"Mona Lisa\"?",
            listOf("Leonardo da Vinci", "Michelangelo", "Vincent van Gogh", "Pablo Picasso"),
            listOf("Leonardo da Vinci", "Michelangelo", "Vincent van Gogh", "Pablo Picasso"),
            listOf("Leonardo malte sie Anfang des 16. Jahrhunderts.", "Heute hängt sie im Louvre in Paris."),
            listOf("Leonardo painted it in the early 16th century.", "Today it hangs in the Louvre in Paris."),
        ),
        KQ(
            Difficulty.Easy,
            "Wie viele Minuten hat ein Tag?", "How many minutes are there in a day?",
            listOf("1440", "1240", "1600", "960"), listOf("1440", "1240", "1600", "960"),
            listOf("Ein Tag hat 24 Stunden, jede Stunde 60 Minuten.", "24 · 60 = 1440"),
            listOf("A day has 24 hours, each with 60 minutes.", "24 × 60 = 1440"),
        ),
        // --- mittel ---
        KQ(
            Difficulty.Medium,
            "Was ist das chemische Zeichen für Gold?", "What is the chemical symbol for gold?",
            listOf("Au", "Ag", "Go", "Gd"), listOf("Au", "Ag", "Go", "Gd"),
            listOf("Au kommt vom lateinischen Wort „aurum“ für Gold.", "Ag ist Silber (lateinisch „argentum“)."),
            listOf("Au comes from the Latin word \"aurum\" for gold.", "Ag is silver (Latin \"argentum\")."),
        ),
        KQ(
            Difficulty.Medium,
            "Welches Gas macht den größten Teil unserer Luft aus?", "Which gas makes up most of the air we breathe?",
            listOf("Stickstoff", "Sauerstoff", "Kohlenstoffdioxid", "Argon"), listOf("Nitrogen", "Oxygen", "Carbon dioxide", "Argon"),
            listOf("Luft besteht zu etwa 78 % aus Stickstoff und zu etwa 21 % aus Sauerstoff.", "Der Rest ist vor allem Argon, dazu ein wenig Kohlenstoffdioxid."),
            listOf("Air is about 78 % nitrogen and about 21 % oxygen.", "The rest is mostly argon, plus a little carbon dioxide."),
        ),
        KQ(
            Difficulty.Medium,
            "Wie heißt die Hauptstadt von Australien?", "What is the capital of Australia?",
            listOf("Canberra", "Sydney", "Melbourne", "Perth"), listOf("Canberra", "Sydney", "Melbourne", "Perth"),
            listOf("Sydney und Melbourne stritten sich um den Titel.", "Als Kompromiss wurde dazwischen eine neue Hauptstadt gebaut: Canberra."),
            listOf("Sydney and Melbourne both wanted the title.", "As a compromise, a new capital was built between them: Canberra."),
        ),
        KQ(
            Difficulty.Medium,
            "Wer schrieb „Faust“?", "Who wrote \"Faust\"?",
            listOf("Johann Wolfgang von Goethe", "Friedrich Schiller", "Franz Kafka", "Thomas Mann"),
            listOf("Johann Wolfgang von Goethe", "Friedrich Schiller", "Franz Kafka", "Thomas Mann"),
            listOf("Goethe arbeitete fast sein ganzes Leben an „Faust“.", "Der erste Teil erschien 1808, der zweite nach seinem Tod 1832."),
            listOf("Goethe worked on \"Faust\" for most of his life.", "Part one came out in 1808, part two after his death in 1832."),
        ),
        KQ(
            Difficulty.Medium,
            "In welchem Jahr fiel die Berliner Mauer?", "In which year did the Berlin Wall fall?",
            listOf("1989", "1990", "1961", "1991"), listOf("1989", "1990", "1961", "1991"),
            listOf("Am 9. November 1989 wurden die Grenzen geöffnet.", "1961 wurde die Mauer gebaut, 1990 folgte die Wiedervereinigung."),
            listOf("The border opened on 9 November 1989.", "The wall was built in 1961; reunification followed in 1990."),
        ),
        KQ(
            Difficulty.Medium,
            "Welcher Teil der Zelle wird oft „Kraftwerk der Zelle“ genannt?", "Which part of the cell is often called the \"powerhouse of the cell\"?",
            listOf("Mitochondrium", "Zellkern", "Ribosom", "Zellmembran"), listOf("Mitochondrion", "Nucleus", "Ribosome", "Cell membrane"),
            listOf("In den Mitochondrien wird bei der Zellatmung Energie (ATP) gewonnen.", "Der Zellkern enthält dagegen die Erbinformation."),
            listOf("Mitochondria release energy (ATP) through cellular respiration.", "The nucleus, by contrast, holds the genetic information."),
        ),
        KQ(
            Difficulty.Medium,
            "In welchem Jahr landeten zum ersten Mal Menschen auf dem Mond?", "In which year did people first land on the Moon?",
            listOf("1969", "1959", "1972", "1965"), listOf("1969", "1959", "1972", "1965"),
            listOf("Mit Apollo 11 betraten Neil Armstrong und Buzz Aldrin im Juli 1969 den Mond.", "1972 war die bisher letzte bemannte Mondlandung (Apollo 17)."),
            listOf("With Apollo 11, Neil Armstrong and Buzz Aldrin walked on the Moon in July 1969.", "1972 saw the last crewed landing so far (Apollo 17)."),
        ),
        KQ(
            Difficulty.Medium,
            "Was ist der härteste natürliche Stoff?", "What is the hardest natural material?",
            listOf("Diamant", "Quarz", "Granit", "Stahl"), listOf("Diamond", "Quartz", "Granite", "Steel"),
            listOf("Diamant hat auf der Mohs-Härteskala den Höchstwert 10.", "Er besteht nur aus Kohlenstoff, fest verbunden in einem Gitter. (Stahl ist übrigens nicht natürlich.)"),
            listOf("Diamond has the top value of 10 on the Mohs hardness scale.", "It is pure carbon in a tightly bonded lattice. (Steel isn't natural anyway.)"),
        ),
        KQ(
            Difficulty.Medium,
            "In welcher Einheit misst man elektrischen Widerstand?", "What unit is electrical resistance measured in?",
            listOf("Ohm", "Volt", "Ampere", "Watt"), listOf("Ohm", "Volt", "Ampere", "Watt"),
            listOf("Benannt nach Georg Simon Ohm (Zeichen Ω).", "Volt = Spannung, Ampere = Stromstärke, Watt = Leistung."),
            listOf("Named after Georg Simon Ohm (symbol Ω).", "Volt = voltage, ampere = current, watt = power."),
        ),
        KQ(
            Difficulty.Medium,
            "Wer schrieb den Roman „Der Process“ (oft auch „Der Prozess“)?", "Who wrote the novel \"The Trial\"?",
            listOf("Franz Kafka", "Hermann Hesse", "Thomas Mann", "Bertolt Brecht"),
            listOf("Franz Kafka", "Hermann Hesse", "Thomas Mann", "Bertolt Brecht"),
            listOf("Kafka schrieb ihn 1914/15, erschienen ist er erst 1925 nach seinem Tod.", "Josef K. wird verhaftet, ohne je zu erfahren, warum."),
            listOf("Kafka wrote it in 1914/15; it was published only in 1925, after his death.", "Josef K. is arrested without ever learning why."),
        ),
        // --- schwer ---
        KQ(
            Difficulty.Hard,
            "Wie schnell ist Licht im Vakuum ungefähr?", "Roughly how fast is light in a vacuum?",
            listOf("300.000 km/s", "30.000 km/s", "3.000 km/s", "3.000.000 km/s"),
            listOf("300,000 km/s", "30,000 km/s", "3,000 km/s", "3,000,000 km/s"),
            listOf("Genau sind es 299.792,458 km pro Sekunde.", "Das Licht der Sonne braucht damit gut 8 Minuten bis zur Erde."),
            listOf("Exactly, it's 299,792.458 km per second.", "That's why sunlight takes just over 8 minutes to reach Earth."),
        ),
        KQ(
            Difficulty.Hard,
            "Wie viele Knochen hat ein erwachsener Mensch ungefähr?", "About how many bones does an adult human have?",
            listOf("206", "106", "306", "156"), listOf("206", "106", "306", "156"),
            listOf("Erwachsene haben meist 206 Knochen.", "Babys haben mehr (rund 300), weil einige Knochen erst später zusammenwachsen."),
            listOf("Adults usually have 206 bones.", "Babies have more (around 300) because some bones fuse later."),
        ),
        KQ(
            Difficulty.Hard,
            "Welcher Fluss ist der längste Europas?", "Which river is the longest in Europe?",
            listOf("Wolga", "Donau", "Rhein", "Dnepr"), listOf("Volga", "Danube", "Rhine", "Dnieper"),
            listOf("Die Wolga in Russland ist etwa 3500 km lang.", "Die Donau folgt mit rund 2850 km auf Platz zwei."),
            listOf("The Volga in Russia is about 3,500 km long.", "The Danube comes second at around 2,850 km."),
        ),
        KQ(
            Difficulty.Hard,
            "Was ist die kleinste Primzahl?", "What is the smallest prime number?",
            listOf("2", "1", "3", "0"), listOf("2", "1", "3", "0"),
            listOf("Eine Primzahl hat genau zwei Teiler: 1 und sich selbst.", "1 hat nur einen Teiler, ist also keine Primzahl. 2 ist die kleinste (und einzige gerade) Primzahl."),
            listOf("A prime has exactly two divisors: 1 and itself.", "1 has only one divisor, so it isn't prime. 2 is the smallest (and only even) prime."),
        ),
        KQ(
            Difficulty.Hard,
            "Wofür steht die Abkürzung DNA (deutsch DNS)?", "What does DNA stand for?",
            listOf("Desoxyribonukleinsäure", "Doppelnukleinsäure", "Dinatriumaminosäure", "Desoxyaminosäure"),
            listOf("Deoxyribonucleic acid", "Double nucleic acid", "Disodium amino acid", "Deoxyamino acid"),
            listOf("Die DNA trägt die Erbinformation, gebaut als Doppelhelix.", "Ihr Zucker heißt Desoxyribose, daher der Name."),
            listOf("DNA carries genetic information, shaped as a double helix.", "Its sugar is deoxyribose, hence the name."),
        ),
        KQ(
            Difficulty.Hard,
            "Wer entwickelte die Relativitätstheorie?", "Who developed the theory of relativity?",
            listOf("Albert Einstein", "Isaac Newton", "Max Planck", "Niels Bohr"),
            listOf("Albert Einstein", "Isaac Newton", "Max Planck", "Niels Bohr"),
            listOf("Einstein veröffentlichte 1905 die spezielle und 1915 die allgemeine Relativitätstheorie.", "Daraus stammt auch die berühmte Formel E = mc²."),
            listOf("Einstein published special relativity in 1905 and general relativity in 1915.", "The famous formula E = mc² comes from it."),
        ),
        KQ(
            Difficulty.Hard,
            "Welche Sprache hat die meisten Muttersprachler?", "Which language has the most native speakers?",
            listOf("Chinesisch (Mandarin)", "Englisch", "Spanisch", "Hindi"),
            listOf("Chinese (Mandarin)", "English", "Spanish", "Hindi"),
            listOf("Mandarin sprechen weit über 900 Millionen Menschen als Muttersprache.", "Englisch hat insgesamt viele Sprecher, aber weniger Muttersprachler."),
            listOf("Well over 900 million people speak Mandarin natively.", "English has many speakers overall, but fewer native ones."),
        ),
    )
}
