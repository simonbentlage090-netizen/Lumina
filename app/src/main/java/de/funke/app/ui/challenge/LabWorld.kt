package de.funke.app.ui.challenge

import de.funke.app.ui.tr
import kotlin.math.abs

/*
 * Aufgaben für die kleineren Fächer (seit 12.0): Biologie, Chemie, Erdkunde, Geologie, Geschichte und Logik.
 *
 * Wie bei Literatur ist jeder Fachbegriff eine eigene Aufgabe, die bei jedem Ziehen die Frageform und die falschen
 * Antworten neu wählt (aus den anderen Begriffen desselben Fachs). Geschichte fragt nach Jahreszahlen mit
 * Spielraum, Logik hat Rätsel mit Zufallszahlen und mehreren Schritten.
 *
 * Alle Texte sind selbst geschrieben.
 */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

/** Ein Fachbegriff: Name, Erklärung, ein Tipp, der den Namen nicht verrät, und ein Hintergrund zum Weiterlesen. */
internal class Term(val key: String, val level: Difficulty, val name: T, val def: T, val hint: T, val why: T)

/** Jeder Begriff wird eine Vorlage: mal „Was bedeutet …?“, mal „Wie heißt das?“. */
internal fun termDefs(prefix: String, topic: Topic, terms: List<Term>): List<TaskDef> = terms.map { f ->
    lab("$prefix.${f.key}", topic, f.level, grade = 0.3f + 0.15f * f.level.ordinal.coerceAtMost(3)) {
        val others = draw(terms, 3) { it !== f }
        val solution = "${f.name.s}: ${f.def.s}"
        if (chance(0.5)) {
            quiz(
                "x", topic, f.level,
                question = tr("Was bedeutet „${f.name.s}“?", "What does \"${f.name.s}\" mean?"),
                prompt = tr("Die richtige Erklärung", "The right explanation"),
                correct = f.def.s, wrong = others.map { it.def.s },
                solution = solution, background = f.why.s, hint = f.hint.s,
            )
        } else {
            quiz(
                "x", topic, f.level,
                question = f.def.s,
                prompt = tr("Wie heißt das?", "What is this called?"),
                correct = f.name.s, wrong = others.map { it.name.s },
                solution = solution, background = f.why.s, hint = f.hint.s,
            )
        }
    }
}

object LabWorld {
    val all: List<TaskDef> by lazy {
        termDefs("bio", Topic.Biology, BIO) +
            termDefs("chem", Topic.Chemistry, CHEM) +
            termDefs("erd", Topic.Geography, GEO) +
            termDefs("gestein", Topic.Geology, ROCK) +
            history() + logic() + myths()
    }
}

// ---------------------------------------------------------------------------------------------
// Biologie
// ---------------------------------------------------------------------------------------------

private val BIO = listOf(
    Term("ribosom", M, t("Ribosom", "Ribosome"),
        t("Ein winziger Zellbaustein, der nach der Bauanleitung aus der DNA Proteine zusammensetzt.", "A tiny cell structure that assembles proteins according to the instructions from the DNA."),
        t("Es liest die Boten-RNA (mRNA).", "It reads messenger RNA (mRNA)."),
        t("Ribosomen lesen die Boten-RNA wie einen Lochstreifen: Je drei Basen stehen für eine Aminosäure, die sie an die wachsende Kette hängen. Viele Antibiotika wirken, weil sie gezielt die Ribosomen von Bakterien blockieren, die etwas anders gebaut sind als unsere.",
            "Ribosomes read messenger RNA like a punched tape: every three bases stand for one amino acid, which they attach to the growing chain. Many antibiotics work by blocking bacterial ribosomes, which are built slightly differently from ours.")),
    Term("enzym", M, t("Enzym", "Enzyme"),
        t("Ein Eiweiß, das eine chemische Reaktion im Körper beschleunigt, ohne dabei selbst verbraucht zu werden.", "A protein that speeds up a chemical reaction in the body without being used up itself."),
        t("Ein Biokatalysator", "A biological catalyst"),
        t("Jedes Enzym passt nur zu bestimmten Stoffen, wie ein Schlüssel zu seinem Schloss. Die Amylase im Speichel zerlegt Stärke, die Lactase den Milchzucker. Weil Enzyme Eiweiße sind, verlieren sie bei starker Hitze ihre Form und damit ihre Wirkung.",
            "Each enzyme fits only certain substances, like a key fits its lock. Amylase in saliva breaks down starch, lactase breaks down milk sugar. Because enzymes are proteins, strong heat destroys their shape, and with it their effect.")),
    Term("mitose", M, t("Mitose", "Mitosis"),
        t("Eine Zellteilung, bei der aus einer Zelle zwei Tochterzellen mit genau denselben Erbanlagen entstehen.", "A cell division in which one cell produces two daughter cells with exactly the same genetic material."),
        t("So wächst du, und so heilen Wunden.", "This is how you grow and how wounds heal."),
        t("Vor der Mitose verdoppelt die Zelle ihre Chromosomen, dann zieht ein Spindelapparat die Kopien auseinander, je einen Satz in jede Tochterzelle. Dein Körper macht das jede Sekunde millionenfach, etwa im Knochenmark, wo ständig neue Blutzellen entstehen.",
            "Before mitosis the cell doubles its chromosomes, then a spindle pulls the copies apart, one set into each daughter cell. Your body does this millions of times every second, for example in the bone marrow, where new blood cells are constantly made.")),
    Term("meiose", H, t("Meiose", "Meiosis"),
        t("Eine Zellteilung, bei der Keimzellen mit nur halb so vielen Chromosomen entstehen: Ei- und Samenzellen.", "A cell division that produces germ cells with only half the number of chromosomes: eggs and sperm."),
        t("Auch Reifeteilung genannt", "Also called reduction division"),
        t("Menschliche Körperzellen haben 46 Chromosomen, Ei- und Samenzellen nur 23. Bei der Befruchtung ergibt das wieder 46. Weil die Chromosomen dabei neu gemischt werden, ist jedes Kind eine neue Kombination seiner Eltern, außer bei eineiigen Zwillingen.",
            "Human body cells have 46 chromosomes, eggs and sperm only 23. Fertilisation brings the number back to 46. Because the chromosomes are reshuffled along the way, every child is a new combination of its parents, except for identical twins.")),
    Term("osmose", H, t("Osmose", "Osmosis"),
        t("Wasser wandert durch eine Haut, die nur Wasser durchlässt, auf die Seite mit mehr gelösten Stoffen.", "Water moves through a membrane that only lets water through, towards the side with more dissolved substances."),
        t("Darum platzen Kirschen nach starkem Regen.", "That's why cherries burst after heavy rain."),
        t("Die Wasserteilchen wandern in beide Richtungen, aber auf der Seite mit mehr Zucker oder Salz bleiben mehr davon. Darum welkt Salat, den man zu früh salzt, und darum hilft Meerwasser nicht gegen Durst: Der Körper braucht mehr Wasser, um das Salz wieder loszuwerden.",
            "Water particles move both ways, but more of them end up on the side with more sugar or salt. That's why lettuce wilts if you salt it too early, and why seawater doesn't quench thirst: your body needs more water to get rid of the salt again.")),
    Term("synapse", H, t("Synapse", "Synapse"),
        t("Die Kontaktstelle zwischen zwei Nervenzellen, an der ein Signal mit Botenstoffen weitergegeben wird.", "The contact point between two nerve cells where a signal is passed on by chemical messengers."),
        t("Dazwischen liegt ein winziger Spalt.", "There is a tiny gap in between."),
        t("Kommt ein elektrisches Signal an, schüttet die Nervenzelle Botenstoffe wie Dopamin oder Serotonin in den Spalt, und die nächste Zelle fängt sie auf. Dein Gehirn hat etwa 86 Milliarden Nervenzellen mit zusammen rund 100 Billionen Synapsen; beim Lernen werden manche davon stärker.",
            "When an electrical signal arrives, the nerve cell releases messengers such as dopamine or serotonin into the gap, and the next cell picks them up. Your brain has about 86 billion nerve cells with around 100 trillion synapses in total; learning strengthens some of them.")),
    Term("antikoerper", M, t("Antikörper", "Antibody"),
        t("Ein Y-förmiges Eiweiß des Immunsystems, das gezielt an einen bestimmten Erreger andockt und ihn markiert.", "A Y-shaped protein of the immune system that binds to one particular germ and marks it."),
        t("Weiße Blutzellen (B-Zellen) bilden sie.", "White blood cells (B cells) make them."),
        t("Jeder Antikörper passt nur zu einem bestimmten Merkmal eines Erregers. Markierte Erreger verklumpen und werden von Fresszellen beseitigt. Nach einer Infektion oder Impfung bleiben Gedächtniszellen zurück, die beim nächsten Mal sehr schnell neue Antikörper bilden.",
            "Each antibody fits only one particular feature of a germ. Marked germs clump together and are removed by scavenger cells. After an infection or a vaccination, memory cells remain that make new antibodies very quickly the next time.")),
    Term("hormon", E, t("Hormon", "Hormone"),
        t("Ein Botenstoff, den eine Drüse ins Blut abgibt und der an einer ganz anderen Stelle im Körper wirkt.", "A chemical messenger that a gland releases into the blood and that acts somewhere else in the body."),
        t("Adrenalin und Insulin gehören dazu.", "Adrenaline and insulin are examples."),
        t("Hormone sind langsamer als Nervensignale, wirken aber oft im ganzen Körper. Adrenalin aus den Nebennieren macht dich bei einem Schreck in Sekunden hellwach, Insulin aus der Bauchspeicheldrüse senkt den Blutzucker, und Melatonin macht dich abends müde.",
            "Hormones are slower than nerve signals but often act throughout the body. Adrenaline from the adrenal glands makes you wide awake within seconds when you get a fright, insulin from the pancreas lowers blood sugar, and melatonin makes you sleepy in the evening.")),
    Term("gen", E, t("Gen", "Gene"),
        t("Ein Abschnitt der DNA, der die Bauanleitung für ein bestimmtes Eiweiß enthält.", "A section of DNA that contains the instructions for a particular protein."),
        t("Der Mensch hat rund 20.000 davon.", "Humans have about 20,000 of them."),
        t("Gene bestimmen mit, welche Augenfarbe du hast oder ob du Milch gut verträgst. Erstaunlich: Der Mensch hat kaum mehr Gene als ein winziger Fadenwurm. Der Unterschied liegt vor allem darin, wann und wie stark die Gene abgelesen werden.",
            "Genes help decide your eye colour or whether you can digest milk well. Surprisingly, humans have hardly more genes than a tiny roundworm. The difference lies mainly in when and how strongly the genes are read.")),
    Term("mutation", M, t("Mutation", "Mutation"),
        t("Eine zufällige Veränderung im Erbgut, die an Nachkommen weitergegeben werden kann.", "A random change in the genetic material that can be passed on to offspring."),
        t("UV-Strahlung kann sie auslösen.", "UV radiation can cause it."),
        t("Die meisten Mutationen bewirken nichts, manche schaden, ganz wenige nützen. Ohne sie gäbe es keine Evolution. Ein Beispiel: Bei vielen Menschen in Europa sorgt eine Mutation dafür, dass sie auch als Erwachsene noch Milchzucker verdauen können.",
            "Most mutations have no effect, some are harmful, a very few are useful. Without them there would be no evolution. One example: in many people in Europe a mutation means they can still digest milk sugar as adults.")),
    Term("selektion", M, t("Natürliche Selektion", "Natural selection"),
        t("Lebewesen, die besser an ihre Umwelt angepasst sind, überleben häufiger und haben mehr Nachkommen; so setzen sich ihre Merkmale durch.", "Living things that are better adapted to their environment survive more often and have more offspring, so their traits spread."),
        t("Charles Darwin, 1859", "Charles Darwin, 1859"),
        t("Berühmt sind die Darwinfinken der Galápagos-Inseln: Ihre Schnäbel passen jeweils zur Nahrung auf ihrer Insel. Ein Beispiel von heute: Bakterien, die zufällig gegen ein Antibiotikum widerstandsfähig sind, überleben die Behandlung und vermehren sich.",
            "Darwin's finches of the Galapagos Islands are famous: their beaks each suit the food on their island. A modern example: bacteria that happen to resist an antibiotic survive the treatment and multiply.")),
    Term("symbiose", M, t("Symbiose", "Symbiosis"),
        t("Zwei verschiedene Arten leben eng zusammen, und beide haben einen Vorteil davon.", "Two different species live closely together, and both benefit."),
        t("Clownfisch und Seeanemone", "Clownfish and sea anemone"),
        t("Flechten sind eine Symbiose aus Pilz und Alge: Die Alge liefert Zucker aus der Photosynthese, der Pilz Halt und Wasser. Auch in deinem Darm leben Billionen Bakterien, die dir beim Verdauen helfen und dafür Nahrung bekommen.",
            "Lichens are a symbiosis of fungus and alga: the alga supplies sugar from photosynthesis, the fungus provides support and water. Trillions of bacteria in your gut also help you digest food and get nourishment in return.")),
    Term("impfung", E, t("Impfung", "Vaccination"),
        t("Das Immunsystem wird mit abgeschwächten oder harmlosen Teilen eines Erregers trainiert, bevor die echte Krankheit kommt.", "The immune system is trained with weakened or harmless parts of a germ before the real disease comes along."),
        t("Edward Jenner, Pocken, 1796", "Edward Jenner, smallpox, 1796"),
        t("Jenner bemerkte, dass Melkerinnen, die harmlose Kuhpocken gehabt hatten, nicht an den gefährlichen Pocken erkrankten. Das Wort Vakzin kommt vom lateinischen vacca, Kuh. 1980 erklärte die Weltgesundheitsorganisation die Pocken für weltweit ausgerottet.",
            "Jenner noticed that milkmaids who had had harmless cowpox did not catch the dangerous smallpox. The word vaccine comes from the Latin vacca, cow. In 1980 the World Health Organization declared smallpox eradicated worldwide.")),
    Term("produzent", E, t("Produzent (Nahrungskette)", "Producer (food chain)"),
        t("Ein Lebewesen, das seine Nahrung selbst aus Licht, Wasser und Kohlendioxid herstellt und so am Anfang der Nahrungskette steht.", "A living thing that makes its own food from light, water and carbon dioxide, and so stands at the start of the food chain."),
        t("Fast immer eine Pflanze oder Alge", "Almost always a plant or alga"),
        t("Pflanzen fressen nichts, sie bauen sich selbst auf. Von ihnen leben Pflanzenfresser, die Konsumenten, und von denen wiederum Fleischfresser. Pilze und Bakterien zersetzen am Ende alles Tote; sie heißen Destruenten und schließen den Kreislauf.",
            "Plants don't eat anything; they build themselves up. Herbivores, the consumers, live on them, and carnivores live on those in turn. Fungi and bacteria finally break down everything dead; they are called decomposers and close the cycle.")),
)

// ---------------------------------------------------------------------------------------------
// Chemie
// ---------------------------------------------------------------------------------------------

private val CHEM = listOf(
    Term("atom", E, t("Atom", "Atom"),
        t("Das kleinste Teilchen eines Elements, aus einem Kern und einer Hülle aus Elektronen.", "The smallest particle of an element, made of a nucleus and a shell of electrons."),
        t("Griechisch atomos: unteilbar", "Greek atomos: indivisible"),
        t("Ein Atom ist fast leer: Wäre der Kern so groß wie eine Erbse in der Mitte eines Fußballstadions, flögen die Elektronen irgendwo über den Tribünen. Trotzdem steckt im Kern fast die ganze Masse. Ganz unteilbar ist das Atom übrigens nicht, das zeigt die Kernspaltung.",
            "An atom is almost empty: if the nucleus were the size of a pea in the middle of a football stadium, the electrons would be somewhere out over the stands. Yet the nucleus holds almost all the mass. And the atom isn't truly indivisible, as nuclear fission shows.")),
    Term("ion", E, t("Ion", "Ion"),
        t("Ein Atom oder Molekül, das Elektronen abgegeben oder aufgenommen hat und deshalb elektrisch geladen ist.", "An atom or molecule that has given up or taken on electrons and is therefore electrically charged."),
        t("Kochsalz besteht daraus: Na⁺ und Cl⁻.", "Table salt is made of them: Na⁺ and Cl⁻."),
        t("Gibt ein Atom ein Elektron ab, wird es positiv, nimmt es eins auf, negativ. Gelöste Ionen machen Wasser leitfähig; deshalb sind Leitungswasser und Strom in der Badewanne gefährlich, während ganz reines Wasser kaum leitet.",
            "If an atom gives up an electron it becomes positive; if it takes one on, negative. Dissolved ions make water conductive; that's why tap water and electricity are a dangerous mix in the bath, while very pure water hardly conducts at all.")),
    Term("isotop", M, t("Isotop", "Isotope"),
        t("Atome desselben Elements, die gleich viele Protonen, aber verschieden viele Neutronen haben.", "Atoms of the same element that have the same number of protons but different numbers of neutrons."),
        t("Kohlenstoff-12 und Kohlenstoff-14", "Carbon-12 and carbon-14"),
        t("Chemisch verhalten sich Isotope fast gleich, sie sind nur verschieden schwer. Manche sind instabil und zerfallen, etwa Kohlenstoff-14 mit einer Halbwertszeit von rund 5730 Jahren. Damit bestimmt man das Alter von Knochen und Holz.",
            "Chemically, isotopes behave almost the same; they just differ in mass. Some are unstable and decay, such as carbon-14 with a half-life of about 5,730 years. This is used to date bones and wood.")),
    Term("molekuel", E, t("Molekül", "Molecule"),
        t("Zwei oder mehr Atome, die durch chemische Bindungen fest zusammenhalten.", "Two or more atoms held firmly together by chemical bonds."),
        t("H₂O ist ein Beispiel.", "H₂O is an example."),
        t("Ein Wassermolekül besteht aus einem Sauerstoffatom und zwei Wasserstoffatomen, die in einem Winkel von etwa 105° zueinander stehen. Moleküle können winzig sein wie Sauerstoff (O₂) oder riesig wie die DNA mit Milliarden Atomen.",
            "A water molecule consists of one oxygen atom and two hydrogen atoms, set at an angle of about 105°. Molecules can be tiny, like oxygen (O₂), or huge, like DNA with billions of atoms.")),
    Term("element", E, t("Chemisches Element", "Chemical element"),
        t("Ein Reinstoff, dessen Atome alle dieselbe Zahl von Protonen im Kern haben.", "A pure substance whose atoms all have the same number of protons in the nucleus."),
        t("Das Periodensystem ordnet sie.", "The periodic table arranges them."),
        t("Heute kennt man 118 Elemente, rund 90 davon kommen in der Natur vor. Die Zahl der Protonen heißt Ordnungszahl: Wasserstoff hat 1, Kohlenstoff 6, Gold 79. Mendelejew ordnete die Elemente 1869 so geschickt, dass er noch unbekannte voraussagen konnte.",
            "Today 118 elements are known, about 90 of which occur naturally. The number of protons is called the atomic number: hydrogen has 1, carbon 6, gold 79. In 1869 Mendeleev arranged the elements so cleverly that he could predict ones not yet discovered.")),
    Term("katalysator", M, t("Katalysator", "Catalyst"),
        t("Ein Stoff, der eine chemische Reaktion beschleunigt, ohne dabei selbst verbraucht zu werden.", "A substance that speeds up a chemical reaction without being used up itself."),
        t("Steckt im Auspuff von Benzinautos", "Found in the exhaust of petrol cars"),
        t("Der Katalysator im Auto wandelt mit Platin und Rhodium giftiges Kohlenmonoxid und Stickoxide in Kohlendioxid, Stickstoff und Wasser um. Auch in der Industrie laufen die meisten Reaktionen mit Katalysatoren, etwa bei der Herstellung von Dünger.",
            "A car's catalytic converter uses platinum and rhodium to turn toxic carbon monoxide and nitrogen oxides into carbon dioxide, nitrogen and water. Most industrial reactions also use catalysts, for example in making fertiliser.")),
    Term("saeure", E, t("Säure", "Acid"),
        t("Ein Stoff, der in Wasser Wasserstoff-Ionen (H⁺) abgibt; die Lösung hat einen pH-Wert unter 7.", "A substance that releases hydrogen ions (H⁺) in water; the solution has a pH below 7."),
        t("Zitronensaft und Essig", "Lemon juice and vinegar"),
        t("Säuren schmecken sauer und greifen Metalle und Kalk an. Deine Magensäure ist Salzsäure mit einem pH-Wert um 1 bis 2, stark genug, um Bakterien abzutöten. Darum schützt sich der Magen mit einer Schleimschicht vor sich selbst.",
            "Acids taste sour and attack metals and limestone. Your stomach acid is hydrochloric acid with a pH of about 1 to 2, strong enough to kill bacteria. That's why the stomach protects itself with a layer of mucus.")),
    Term("lauge", M, t("Lauge (Base)", "Alkali (base)"),
        t("Ein Stoff, der in Wasser Hydroxid-Ionen (OH⁻) bildet; die Lösung hat einen pH-Wert über 7.", "A substance that forms hydroxide ions (OH⁻) in water; the solution has a pH above 7."),
        t("Seife und Natron", "Soap and baking soda"),
        t("Laugen fühlen sich seifig an, weil sie die Fette der Haut angreifen. Schon vor Jahrtausenden kochte man Seife aus Fett und Holzasche, die eine Lauge enthält. Starke Laugen wie Abflussreiniger sind ebenso gefährlich wie starke Säuren.",
            "Alkalis feel soapy because they attack the fats in your skin. Thousands of years ago people already boiled soap from fat and wood ash, which contains an alkali. Strong alkalis like drain cleaner are just as dangerous as strong acids.")),
    Term("neutralisation", M, t("Neutralisation", "Neutralisation"),
        t("Eine Säure und eine Lauge reagieren miteinander zu Wasser und einem Salz.", "An acid and an alkali react with each other to form water and a salt."),
        t("Im Idealfall landet der pH-Wert bei 7.", "Ideally the pH ends up at 7."),
        t("Tabletten gegen Sodbrennen enthalten eine schwache Base, die überschüssige Magensäure neutralisiert. Gärtner streuen Kalk auf zu saure Böden. Dabei wird Wärme frei, weil sich H⁺ und OH⁻ zu Wasser verbinden.",
            "Heartburn tablets contain a weak base that neutralises excess stomach acid. Gardeners spread lime on soil that is too acidic. Heat is released because H⁺ and OH⁻ combine to form water.")),
    Term("oxidation", H, t("Oxidation", "Oxidation"),
        t("Ein Stoff gibt Elektronen ab, zum Beispiel wenn er mit Sauerstoff reagiert.", "A substance gives up electrons, for example when it reacts with oxygen."),
        t("Rost ist ein Beispiel.", "Rust is an example."),
        t("Ursprünglich hieß Oxidation nur „Reaktion mit Sauerstoff“: Eisen rostet, Holz verbrennt, ein angeschnittener Apfel wird braun. Heute fasst man es weiter: Wer Elektronen abgibt, wird oxidiert, wer sie aufnimmt, reduziert. Beides passiert immer gleichzeitig.",
            "Originally oxidation simply meant reacting with oxygen: iron rusts, wood burns, a cut apple turns brown. Today the idea is broader: whatever gives up electrons is oxidised, whatever takes them on is reduced. Both always happen at the same time.")),
    Term("edelgas", M, t("Edelgas", "Noble gas"),
        t("Ein Element der letzten Gruppe im Periodensystem, dessen äußere Elektronenschale voll ist, sodass es kaum reagiert.", "An element of the last group in the periodic table whose outer electron shell is full, so it hardly reacts."),
        t("Helium, Neon, Argon", "Helium, neon, argon"),
        t("Weil sie so träge sind, nutzt man Edelgase als Schutz: Argon füllt Glühbirnen und schützt beim Schweißen, Helium lässt Ballons steigen und brennt anders als Wasserstoff nicht. Neon leuchtet in Reklameröhren rot-orange.",
            "Because they are so unreactive, noble gases are used for protection: argon fills light bulbs and shields welds, helium lifts balloons and, unlike hydrogen, doesn't burn. Neon glows red-orange in advertising tubes.")),
    Term("mol", H, t("Mol", "Mole"),
        t("Eine Stoffmenge von rund 6 · 10²³ Teilchen, also Atomen, Molekülen oder Ionen.", "An amount of substance of about 6 · 10²³ particles, that is atoms, molecules or ions."),
        t("Die Avogadro-Zahl", "Avogadro's number"),
        t("Das Mol ist für die Chemie, was das Dutzend für den Bäcker ist, nur viel größer. Ein Mol Wasser wiegt 18 Gramm, ein Schluck. Zählte man jede Sekunde ein Teilchen, bräuchte man für ein Mol rund 19 Billiarden Jahre, über eine Million Mal so lange, wie es das Universum gibt.",
            "The mole is to chemistry what the dozen is to a baker, only much bigger. A mole of water weighs 18 grams, a single sip. Counting one particle per second, a mole would take about 19 quadrillion years, more than a million times the age of the universe.")),
    Term("exotherm", M, t("Exotherme Reaktion", "Exothermic reaction"),
        t("Eine chemische Reaktion, bei der Energie frei wird, meist als Wärme.", "A chemical reaction that releases energy, usually as heat."),
        t("Das Gegenteil heißt endotherm.", "Its opposite is called endothermic."),
        t("Jedes Feuer ist exotherm, ebenso Taschenwärmer, in denen Eisenpulver langsam rostet. Endotherme Vorgänge nehmen dagegen Wärme auf: Sofort-Kältepacks werden kalt, wenn sich darin ein Salz in Wasser löst.",
            "Every fire is exothermic, as are hand warmers in which iron powder slowly rusts. Endothermic processes absorb heat instead: instant cold packs get cold when a salt dissolves in water inside them.")),
    Term("legierung", M, t("Legierung", "Alloy"),
        t("Ein Gemisch aus einem Metall mit anderen Elementen, das oft härter oder haltbarer ist als das reine Metall.", "A mixture of a metal with other elements that is often harder or more durable than the pure metal."),
        t("Bronze: Kupfer und Zinn", "Bronze: copper and tin"),
        t("Bronze gab einem ganzen Zeitalter den Namen. Messing ist Kupfer mit Zink, Stahl ist Eisen mit wenig Kohlenstoff, und rostfreier Stahl enthält zusätzlich Chrom. Selbst Goldschmuck ist meist eine Legierung, weil reines Gold sehr weich ist.",
            "Bronze gave its name to an entire age. Brass is copper with zinc, steel is iron with a little carbon, and stainless steel also contains chromium. Even gold jewellery is usually an alloy, because pure gold is very soft.")),
)

// ---------------------------------------------------------------------------------------------
// Erdkunde
// ---------------------------------------------------------------------------------------------

private val GEO = listOf(
    Term("monsun", M, t("Monsun", "Monsoon"),
        t("Ein großräumiger Wind, der mit den Jahreszeiten die Richtung wechselt und im Sommer heftigen Regen bringt.", "A large-scale wind that changes direction with the seasons and brings heavy rain in summer."),
        t("Typisch für Indien", "Typical of India"),
        t("Im Sommer erwärmt sich das Land stärker als das Meer, die Luft steigt auf und saugt feuchte Meeresluft an. In Indien fällt dann ein Großteil des Jahresregens in wenigen Monaten. Im Winter dreht der Wind und weht trocken vom Land aufs Meer.",
            "In summer the land heats up more than the sea, the air rises and draws in moist sea air. In India most of the year's rain then falls within a few months. In winter the wind turns and blows dry from the land out to sea.")),
    Term("passat", H, t("Passat", "Trade wind"),
        t("Ein beständiger Wind, der das ganze Jahr aus den Subtropen in Richtung Äquator weht.", "A steady wind that blows all year from the subtropics towards the equator."),
        t("Segelschiffe nutzten ihn für die Fahrt über den Atlantik.", "Sailing ships used it to cross the Atlantic."),
        t("Wegen der Erddrehung kommt er auf der Nordhalbkugel aus Nordosten, auf der Südhalbkugel aus Südosten. Kolumbus segelte mit dem Nordostpassat nach Amerika. Am Äquator treffen die Passate aufeinander; dort ist es oft windstill und schwül.",
            "Because of the Earth's rotation it comes from the northeast in the northern hemisphere and from the southeast in the southern hemisphere. Columbus sailed to America with the northeast trade wind. At the equator the trade winds meet; it is often calm and humid there.")),
    Term("golfstrom", M, t("Golfstrom", "Gulf Stream"),
        t("Eine warme Meeresströmung, die aus dem Golf von Mexiko über den Atlantik in Richtung Europa zieht.", "A warm ocean current that flows from the Gulf of Mexico across the Atlantic towards Europe."),
        t("Er wirkt wie eine Heizung für einen ganzen Kontinent.", "It works like a heating system for a whole continent."),
        t("Dank des Golfstroms und seiner Fortsetzung, des Nordatlantikstroms, sind die Winter in Westeuropa viel milder als in Kanada auf gleicher Breite. Norwegens Häfen bleiben eisfrei, obwohl manche nördlich des Polarkreises liegen.",
            "Thanks to the Gulf Stream and its continuation, the North Atlantic Current, winters in western Europe are much milder than in Canada at the same latitude. Norway's harbours stay free of ice, even though some lie north of the Arctic Circle.")),
    Term("delta", E, t("Flussdelta", "River delta"),
        t("Eine Flussmündung, an der sich der Fluss in viele Arme aufteilt und Sand und Schlamm ablagert.", "A river mouth where the river splits into many branches and deposits sand and mud."),
        t("Benannt nach einem griechischen Buchstaben", "Named after a Greek letter"),
        t("Der griechische Geschichtsschreiber Herodot verglich die Nilmündung mit dem dreieckigen Buchstaben Δ. Deltas sind sehr fruchtbar, weil der Fluss ständig neuen Boden heranbringt. Das Gegenstück ist der Mündungstrichter, etwa an der Elbe, wo die Gezeiten das Material forttragen.",
            "The Greek historian Herodotus compared the mouth of the Nile to the triangular letter Δ. Deltas are very fertile because the river constantly brings new soil. The counterpart is the estuary, such as the Thames, where the tides carry the material away.")),
    Term("fjord", M, t("Fjord", "Fjord"),
        t("Eine lange, schmale und sehr tiefe Meeresbucht zwischen steilen Felswänden, ausgeschürft von Gletschern.", "A long, narrow and very deep sea inlet between steep rock walls, carved out by glaciers."),
        t("Norwegen ist berühmt dafür.", "Norway is famous for them."),
        t("In den Eiszeiten schoben sich Gletscher durch Flusstäler und schliffen sie zu tiefen U-Tälern aus. Als das Eis schmolz und der Meeresspiegel stieg, lief das Meer hinein. Der Sognefjord in Norwegen ist über 200 Kilometer lang und an der tiefsten Stelle über 1300 Meter tief.",
            "During the ice ages, glaciers pushed through river valleys and ground them into deep U-shaped valleys. When the ice melted and sea level rose, the sea flowed in. Norway's Sognefjord is over 200 kilometres long and more than 1,300 metres deep at its deepest point.")),
    Term("wendekreis", H, t("Wendekreis", "Tropic (Cancer or Capricorn)"),
        t("Einer der beiden Breitenkreise bei etwa 23,4°, über denen die Sonne einmal im Jahr mittags genau senkrecht steht.", "One of the two circles of latitude at about 23.4° where the sun stands directly overhead at noon once a year."),
        t("Der nördliche heißt nach dem Sternbild Krebs.", "The northern one is named after the constellation Cancer."),
        t("Weiter nördlich oder südlich steht die Sonne nie im Zenit. Um den 21. Juni erreicht sie den nördlichen Wendekreis und „wendet“ dann wieder nach Süden. Das Gebiet zwischen den beiden Kreisen heißt Tropen. Der Grund für all das ist die geneigte Erdachse.",
            "Further north or south, the sun is never directly overhead. Around 21 June it reaches the northern tropic and then 'turns' back south. The region between the two circles is called the tropics. All of this comes from the tilt of the Earth's axis.")),
    Term("nullmeridian", M, t("Nullmeridian", "Prime meridian"),
        t("Der Längengrad 0°, von dem aus man nach Osten und Westen zählt; er verläuft durch Greenwich in London.", "The line of longitude at 0°, from which east and west are counted; it runs through Greenwich in London."),
        t("Festgelegt 1884 auf einer Konferenz in Washington", "Fixed in 1884 at a conference in Washington"),
        t("Breitengrade haben mit dem Äquator einen natürlichen Anfang, Längengrade nicht; dafür brauchte man eine Vereinbarung. Weil sich die Erde in 24 Stunden einmal dreht, entspricht jeder Längengrad vier Minuten Zeitunterschied. Darauf bauen die Zeitzonen auf.",
            "Lines of latitude have a natural starting point in the equator, lines of longitude don't, so an agreement was needed. Because the Earth turns once in 24 hours, each degree of longitude corresponds to four minutes of time difference. Time zones are built on this.")),
    Term("tundra", M, t("Tundra", "Tundra"),
        t("Eine baumlose, kalte Landschaft mit Moosen, Flechten und Zwergsträuchern über dauerhaft gefrorenem Boden.", "A treeless, cold landscape with mosses, lichens and dwarf shrubs over permanently frozen ground."),
        t("Nördlich der großen Nadelwälder", "North of the great conifer forests"),
        t("Der Sommer ist hier zu kurz und zu kühl für Bäume. Nur die oberste Schicht des Dauerfrostbodens taut auf, darunter bleibt er gefroren; das Wasser kann nicht versickern, und es entstehen Sümpfe. Taut der Permafrost durch die Erderwärmung, wird viel gespeichertes Methan frei.",
            "The summer here is too short and too cool for trees. Only the top layer of the permafrost thaws, below it stays frozen; the water can't drain away, and bogs form. When global warming thaws the permafrost, a lot of stored methane is released.")),
    Term("atoll", H, t("Atoll", "Atoll"),
        t("Ein ringförmiges Korallenriff, das eine Lagune umschließt, oft auf einem versunkenen Vulkan.", "A ring-shaped coral reef that encloses a lagoon, often on top of a sunken volcano."),
        t("Typisch für die Malediven", "Typical of the Maldives"),
        t("Darwin erklärte als Erster, wie Atolle entstehen: Korallen wachsen als Saum um eine Vulkaninsel. Die Insel sinkt langsam ab, die Korallen wachsen nach oben weiter, bis nur noch der Ring übrig ist. Viele Atolle liegen kaum zwei Meter über dem Meer und sind durch den steigenden Meeresspiegel bedroht.",
            "Darwin was the first to explain how atolls form: corals grow as a fringe around a volcanic island. The island slowly sinks, the corals keep growing upwards, until only the ring is left. Many atolls lie barely two metres above the sea and are threatened by rising sea levels.")),
    Term("gezeiten", E, t("Gezeiten", "Tides"),
        t("Das regelmäßige Steigen und Fallen des Meeresspiegels, etwa zweimal am Tag, vor allem durch die Anziehung des Mondes.", "The regular rise and fall of sea level, about twice a day, mainly caused by the pull of the Moon."),
        t("Ebbe und Flut", "Ebb and flow"),
        t("Der Mond zieht das Wasser auf der ihm zugewandten Seite etwas stärker an als die Erde als Ganzes, auf der abgewandten Seite schwächer; so entstehen zwei Flutberge. Weil der Mond weiterwandert, kommt die Flut jeden Tag etwa 50 Minuten später. An der Nordsee steigt das Wasser um einige Meter, am Mittelmeer kaum.",
            "The Moon pulls the water on the side facing it a little more strongly than the Earth as a whole, and the far side more weakly; this creates two bulges of water. Because the Moon moves on, high tide comes about 50 minutes later each day. On the North Sea the water rises by several metres, in the Mediterranean hardly at all.")),
)

// ---------------------------------------------------------------------------------------------
// Geologie
// ---------------------------------------------------------------------------------------------

private val ROCK = listOf(
    Term("magma", E, t("Magma", "Magma"),
        t("Geschmolzenes Gestein tief in der Erde; tritt es an die Oberfläche, heißt es Lava.", "Molten rock deep inside the Earth; when it reaches the surface it is called lava."),
        t("Bis zu etwa 1200 °C heiß", "Up to about 1200 °C hot"),
        t("Magma entsteht in einigen Dutzend bis weit über hundert Kilometern Tiefe, wo Gestein teilweise schmilzt. Weil es leichter ist als das feste Gestein ringsum, steigt es auf und sammelt sich in Magmakammern. Erstarrt es unterirdisch, entsteht zum Beispiel Granit, an der Oberfläche Basalt.",
            "Magma forms at depths from a few dozen to well over a hundred kilometres, where rock partly melts. Because it is lighter than the solid rock around it, it rises and collects in magma chambers. If it solidifies underground it forms granite, for example; at the surface, basalt.")),
    Term("erosion", E, t("Erosion", "Erosion"),
        t("Das Abtragen von Gestein und Boden durch Wasser, Wind und Eis.", "The wearing away of rock and soil by water, wind and ice."),
        t("Lateinisch erodere: abnagen", "Latin erodere: to gnaw away"),
        t("Erosion arbeitet langsam, aber unaufhörlich: Der Colorado River hat in einigen Millionen Jahren den bis zu 1,8 Kilometer tiefen Grand Canyon gegraben. Auf Äckern ohne Pflanzendecke tragen Regen und Wind jedes Jahr fruchtbaren Boden davon.",
            "Erosion works slowly but never stops: over a few million years the Colorado River dug the Grand Canyon, up to 1.8 kilometres deep. On fields without plant cover, rain and wind carry off fertile soil every year.")),
    Term("sediment", M, t("Sedimentgestein", "Sedimentary rock"),
        t("Ein Gestein aus abgelagerten Schichten von Sand, Schlamm oder Kalkschalen, die mit der Zeit fest geworden sind.", "A rock made of deposited layers of sand, mud or shells that have hardened over time."),
        t("Sandstein und Kalkstein", "Sandstone and limestone"),
        t("Fast alle Fossilien findet man in Sedimentgesteinen, denn nur hier werden Lebewesen sanft eingebettet. Die Kreidefelsen auf Rügen bestehen aus den Kalkschalen winziger Algen. Die Schichten liegen meist wie Buchseiten übereinander: unten alt, oben jung.",
            "Almost all fossils are found in sedimentary rocks, because only here are living things gently embedded. The white chalk cliffs of Dover are made of the tiny shells of microscopic algae. The layers usually lie on top of each other like the pages of a book: old at the bottom, young at the top.")),
    Term("metamorph", H, t("Metamorphes Gestein", "Metamorphic rock"),
        t("Ein Gestein, das durch große Hitze und hohen Druck umgewandelt wurde, ohne zu schmelzen.", "A rock transformed by great heat and high pressure without melting."),
        t("Aus Kalkstein wird Marmor.", "Limestone becomes marble."),
        t("Wenn Gebirge entstehen oder Gestein tief versenkt wird, ordnen sich seine Minerale neu. Aus Tonstein wird Schiefer, aus Granit Gneis. Michelangelos David ist aus Carrara-Marmor gehauen, der einmal Kalkschlamm am Meeresboden war.",
            "When mountains form or rock is buried deep, its minerals rearrange. Mudstone becomes slate, granite becomes gneiss. Michelangelo's David is carved from Carrara marble, which was once lime mud on the sea floor.")),
    Term("subduktion", H, t("Subduktion", "Subduction"),
        t("Eine Erdplatte taucht unter eine andere ab und sinkt in den Erdmantel.", "One tectonic plate dives beneath another and sinks into the mantle."),
        t("So entstehen Tiefseegräben.", "This is how deep-sea trenches form."),
        t("Meist taucht die schwerere ozeanische Platte unter die leichtere Kontinentalplatte. Dabei entstehen Tiefseegräben wie der fast 11 Kilometer tiefe Marianengraben, starke Erdbeben und Vulkanketten wie in den Anden oder rund um den Pazifik, dem „Feuerring“.",
            "Usually the heavier oceanic plate dives beneath the lighter continental plate. This creates deep-sea trenches such as the Mariana Trench, almost 11 kilometres deep, strong earthquakes and chains of volcanoes like those in the Andes or around the Pacific, the \"Ring of Fire\".")),
    Term("fossil", E, t("Fossil", "Fossil"),
        t("Ein Überrest oder eine Spur eines Lebewesens aus der Vorzeit, die im Gestein erhalten geblieben ist.", "The remains or trace of a living thing from the distant past, preserved in rock."),
        t("Lateinisch fossilis: ausgegraben", "Latin fossilis: dug up"),
        t("Meist bleiben nur harte Teile wie Knochen, Zähne und Schalen erhalten, aber auch Fußspuren, Abdrücke von Blättern oder in Bernstein eingeschlossene Insekten zählen dazu. Der Urvogel Archaeopteryx aus Bayern verbindet Merkmale von Dinosauriern und Vögeln.",
            "Usually only hard parts such as bones, teeth and shells survive, but footprints, leaf imprints and insects trapped in amber count too. The early bird Archaeopteryx from Bavaria combines features of dinosaurs and birds.")),
    Term("mohs", M, t("Mohs-Härteskala", "Mohs hardness scale"),
        t("Eine Skala von 1 bis 10 für Minerale: Jedes kann alle mit einer kleineren Zahl ritzen.", "A scale from 1 to 10 for minerals: each one can scratch all those with a lower number."),
        t("Talk ist 1, Diamant 10.", "Talc is 1, diamond 10."),
        t("Friedrich Mohs stellte die Skala 1812 auf. Dein Fingernagel hat etwa Härte 2,5, eine Stahlklinge etwa 5,5, Quarz 7. Die Abstände sind nicht gleich: Diamant ist viel härter als Korund mit Härte 9, obwohl nur eine Stufe dazwischen liegt.",
            "Friedrich Mohs set up the scale in 1812. Your fingernail has a hardness of about 2.5, a steel blade about 5.5, quartz 7. The steps are not equal: diamond is far harder than corundum at 9, even though they are only one step apart.")),
    Term("moraene", H, t("Moräne", "Moraine"),
        t("Ein Wall aus Geröll, Sand und Steinen, den ein Gletscher vor sich hergeschoben oder abgelagert hat.", "A ridge of rubble, sand and stones that a glacier has pushed ahead of it or left behind."),
        t("Norddeutschland ist voll davon.", "Northern Germany is full of them."),
        t("Die Gletscher der Eiszeit reichten aus Skandinavien bis nach Norddeutschland. Sie hinterließen Moränenhügel, Seen und riesige Findlinge, Felsbrocken, die Hunderte Kilometer weit transportiert wurden. Viele sanfte Hügel zwischen Hamburg und Berlin sind solche Moränen.",
            "The Ice Age glaciers reached from Scandinavia as far as northern Germany. They left behind moraine hills, lakes and huge erratic boulders carried hundreds of kilometres. Many gentle hills between Hamburg and Berlin are such moraines.")),
    Term("geysir", M, t("Geysir", "Geyser"),
        t("Eine heiße Quelle, die in Abständen eine Fontäne aus Wasser und Dampf in die Luft schleudert.", "A hot spring that shoots a fountain of water and steam into the air at intervals."),
        t("Benannt nach einer Quelle auf Island", "Named after a spring in Iceland"),
        t("Unter der Erde erhitzt Magma das Wasser in engen Kanälen auf über 100 °C, ohne dass es sofort kocht, weil das Wasser darüber drückt. Kocht es schließlich doch, schiebt der Dampf die ganze Säule hinaus. Der Old Faithful im Yellowstone-Park bricht etwa alle 90 Minuten aus.",
            "Underground, magma heats the water in narrow channels to over 100 °C without it boiling at once, because the water above presses down. When it finally boils, the steam pushes the whole column out. Old Faithful in Yellowstone Park erupts about every 90 minutes.")),
    Term("tsunami", M, t("Tsunami", "Tsunami"),
        t("Eine Folge riesiger Wellen, ausgelöst durch ein Seebeben, einen Erdrutsch oder einen Vulkanausbruch unter Wasser.", "A series of huge waves set off by an undersea earthquake, landslide or volcanic eruption."),
        t("Japanisch: Hafenwelle", "Japanese: harbour wave"),
        t("Auf offener See ist ein Tsunami oft kaum einen Meter hoch, rast aber so schnell wie ein Flugzeug, mit 700 bis 800 km/h. Erst an der Küste staut sich das Wasser zu Wänden von vielen Metern. Ein Warnzeichen: Das Meer zieht sich plötzlich weit zurück.",
            "Out at sea a tsunami is often barely a metre high, but it races along as fast as a jet plane, at 700 to 800 km/h. Only at the coast does the water pile up into walls many metres high. A warning sign: the sea suddenly pulls back a long way.")),
    Term("hotspot", X, t("Hotspot (Geologie)", "Hotspot (geology)"),
        t("Eine Stelle, an der heißes Gestein aus der Tiefe aufsteigt und mitten auf einer Erdplatte Vulkane entstehen lässt.", "A place where hot rock rises from deep below and creates volcanoes in the middle of a tectonic plate."),
        t("Hawaii ist so entstanden.", "This is how Hawaii formed."),
        t("Der Hotspot bleibt ungefähr an seinem Platz, während die Pazifische Platte darüber hinwegwandert. So entstand eine ganze Kette von Inseln: Die ältesten im Nordwesten sind längst erloschen und abgetragen, auf der jüngsten, Big Island, sind die Vulkane noch aktiv. Auch Island liegt über einem Hotspot.",
            "The hotspot stays roughly in place while the Pacific Plate moves over it. This created a whole chain of islands: the oldest, in the northwest, are long extinct and worn down, while on the youngest, the Big Island, the volcanoes are still active. Iceland also sits on a hotspot.")),
)

// ---------------------------------------------------------------------------------------------
// Geschichte: Jahreszahlen mit Spielraum
// ---------------------------------------------------------------------------------------------

private class Event(val key: String, val level: Difficulty, val year: Int, val tolerance: Int, val q: T, val solution: T, val why: T, val hint: T)

private val EVENTS = listOf(
    Event("bastille", E, 1789, 2,
        t("Der Sturm auf die Bastille, ein Pariser Gefängnis, gilt als Beginn der Französischen Revolution. In welchem Jahr war das?", "The storming of the Bastille, a Paris prison, is seen as the start of the French Revolution. In which year did it happen?"),
        t("Am 14. Juli 1789; der Tag ist heute Frankreichs Nationalfeiertag.", "On 14 July 1789; the day is France's national holiday today."),
        t("Frankreich war hoch verschuldet, das Brot teuer, und der dritte Stand – Bürger und Bauern – trug fast alle Steuern. Die Revolution schaffte die Vorrechte des Adels ab und verkündete die Menschen- und Bürgerrechte. Ihr Motto „Freiheit, Gleichheit, Brüderlichkeit“ prägt Demokratien bis heute.",
            "France was deeply in debt, bread was expensive, and the Third Estate, townspeople and peasants, paid almost all the taxes. The Revolution abolished the privileges of the nobility and proclaimed the rights of man and of the citizen. Its motto 'Liberty, equality, fraternity' still shapes democracies today."),
        t("Wenige Jahre nach der Unabhängigkeit der USA", "A few years after American independence")),
    Event("kolumbus", E, 1492, 2,
        t("Christoph Kolumbus wollte westwärts nach Indien segeln und landete stattdessen auf einer Insel der Bahamas. In welchem Jahr?", "Christopher Columbus wanted to sail west to India and instead landed on an island in the Bahamas. In which year?"),
        t("Am 12. Oktober 1492. Bis zu seinem Tod glaubte er, in Asien gewesen zu sein.", "On 12 October 1492. Until his death he believed he had been to Asia."),
        t("Die spanische Krone bezahlte die Reise. Kolumbus hielt die Erde für kleiner, als sie ist; dass sie rund ist, wussten Gebildete schon seit der Antike. Für die Menschen in Amerika begann mit seiner Ankunft eine Zeit der Eroberung, eingeschleppter Krankheiten und Ausbeutung.",
            "The Spanish crown paid for the voyage. Columbus thought the Earth was smaller than it is; educated people had known since antiquity that it was round. For the people of the Americas his arrival began an era of conquest, imported diseases and exploitation."),
        t("Im selben Jahr endete die maurische Herrschaft in Spanien.", "In the same year Moorish rule in Spain came to an end.")),
    Event("thesen", M, 1517, 3,
        t("Martin Luther veröffentlichte in Wittenberg 95 Thesen gegen den Ablasshandel und löste damit die Reformation aus. In welchem Jahr?", "Martin Luther published 95 theses against the sale of indulgences in Wittenberg, sparking the Reformation. In which year?"),
        t("1517, der Überlieferung nach am 31. Oktober, dem heutigen Reformationstag.", "1517, traditionally on 31 October, today's Reformation Day."),
        t("Gegen Geld versprach die Kirche damals, Strafen für Sünden zu erlassen. Luther hielt das für falsch: Gnade könne man nicht kaufen. Dank des noch jungen Buchdrucks verbreiteten sich seine Schriften rasend schnell, und seine Bibelübersetzung prägte die deutsche Sprache.",
            "At the time the Church promised, for money, to cancel punishments for sins. Luther thought this was wrong: grace could not be bought. Thanks to the still young printing press his writings spread incredibly fast, and his translation of the Bible shaped the German language."),
        t("Gut 60 Jahre nach Gutenbergs Bibel", "A good 60 years after Gutenberg's Bible")),
    Event("kriegsende", E, 1945, 1,
        t("Mit der bedingungslosen Kapitulation der Wehrmacht endete der Zweite Weltkrieg in Europa. In welchem Jahr?", "With the unconditional surrender of the German armed forces, the Second World War ended in Europe. In which year?"),
        t("Am 8. Mai 1945. In Asien endete der Krieg erst im September mit der Kapitulation Japans.", "On 8 May 1945. In Asia the war only ended in September with Japan's surrender."),
        t("Den Krieg hatte das nationalsozialistische Deutschland 1939 mit dem Überfall auf Polen begonnen. Er kostete weltweit mehr als 60 Millionen Menschen das Leben, darunter etwa sechs Millionen ermordete Jüdinnen und Juden. Danach wurde Deutschland in vier Besatzungszonen geteilt.",
            "Nazi Germany had started the war in 1939 by invading Poland. It cost more than 60 million lives worldwide, including around six million murdered Jews. Afterwards Germany was divided into four occupation zones."),
        t("Sechs Jahre nach Kriegsbeginn", "Six years after the war began")),
    Event("reichsgruendung", M, 1871, 3,
        t("Im Spiegelsaal von Versailles wurde der preußische König Wilhelm I. zum Deutschen Kaiser ausgerufen. In welchem Jahr wurde so das Deutsche Kaiserreich gegründet?", "In the Hall of Mirrors at Versailles, the Prussian king Wilhelm I was proclaimed German Emperor. In which year was the German Empire founded in this way?"),
        t("Am 18. Januar 1871, noch während des Krieges gegen Frankreich.", "On 18 January 1871, while the war against France was still going on."),
        t("Zuvor bestand Deutschland aus vielen einzelnen Staaten. Treibende Kraft der Einigung war der preußische Ministerpräsident Otto von Bismarck, der sie mit drei Kriegen durchsetzte. Dass die Feier ausgerechnet im Schloss des besiegten Gegners stattfand, empfanden viele Franzosen als Demütigung.",
            "Before that, Germany consisted of many separate states. The driving force behind unification was the Prussian prime minister Otto von Bismarck, who achieved it through three wars. Many French people saw it as a humiliation that the ceremony took place in the palace of the defeated enemy."),
        t("Am Ende des Deutsch-Französischen Krieges", "At the end of the Franco-Prussian War")),
    Event("grundgesetz", M, 1949, 2,
        t("In welchem Jahr trat das Grundgesetz in Kraft, die Verfassung der Bundesrepublik Deutschland?", "In which year did the Basic Law, the constitution of the Federal Republic of Germany, come into force?"),
        t("Am 24. Mai 1949. Im Oktober desselben Jahres wurde die DDR gegründet.", "On 24 May 1949. In October of the same year the GDR was founded."),
        t("Der Parlamentarische Rat schrieb es bewusst als Lehre aus der NS-Zeit; Artikel 1 beginnt mit „Die Würde des Menschen ist unantastbar.“ Eigentlich war es nur als Übergang bis zur Wiedervereinigung gedacht. Seit 1990 gilt es für ganz Deutschland.",
            "The Parliamentary Council wrote it deliberately as a lesson from the Nazi era; Article 1 begins: \"Human dignity shall be inviolable.\" It was actually meant only as a stopgap until reunification. Since 1990 it has applied to the whole of Germany."),
        t("Vier Jahre nach Kriegsende", "Four years after the end of the war")),
    Event("mondlandung", E, 1969, 1,
        t("Neil Armstrong betrat als erster Mensch den Mond. In welchem Jahr?", "Neil Armstrong became the first person to walk on the Moon. In which year?"),
        t("Im Juli 1969, mit der Mission Apollo 11.", "In July 1969, on the Apollo 11 mission."),
        t("Rund 600 Millionen Menschen verfolgten die Landung live im Fernsehen. Armstrong und Buzz Aldrin blieben gut zwei Stunden draußen, sammelten 21,5 Kilogramm Mondgestein und stellten Messgeräte auf. Bis 1972 betraten insgesamt zwölf Astronauten den Mond.",
            "Around 600 million people watched the landing live on television. Armstrong and Buzz Aldrin stayed outside for a little over two hours, collected 21.5 kilograms of moon rock and set up instruments. By 1972, twelve astronauts in total had walked on the Moon."),
        t("Mitten im Wettlauf ins All mit der Sowjetunion", "In the middle of the space race with the Soviet Union")),
    Event("westfalen", H, 1648, 8,
        t("Der Westfälische Frieden, ausgehandelt in Münster und Osnabrück, beendete den Dreißigjährigen Krieg. In welchem Jahr?", "The Peace of Westphalia, negotiated in two Westphalian cities, ended the Thirty Years' War. In which year?"),
        t("1648; der Krieg hatte 1618 begonnen.", "1648; the war had begun in 1618."),
        t("Der Krieg begann als Streit zwischen Katholiken und Protestanten und wurde zu einem Machtkampf halb Europas auf deutschem Boden. In manchen Gegenden starb mehr als die Hälfte der Menschen. Der Friedensvertrag gilt als ein Anfang des modernen Völkerrechts mit gleichberechtigten Staaten.",
            "The war began as a dispute between Catholics and Protestants and became a power struggle of half of Europe on German soil. In some regions more than half the people died. The peace treaty is seen as a beginning of modern international law between equal states."),
        t("Der Name verrät es, wenn du den Beginn kennst: 1618.", "The name gives it away if you know when it began: 1618.")),
    Event("magnacarta", X, 1215, 20,
        t("Englische Adlige zwangen König Johann Ohneland, die Magna Carta zu besiegeln, die seine Macht beschränkte. In welchem Jahr?", "English barons forced King John to seal Magna Carta, which limited his power. In which year?"),
        t("1215. Einige ihrer Grundsätze, etwa dass niemand ohne Urteil eingesperrt werden darf, wirken bis heute nach.", "1215. Some of its principles, such as that no one may be imprisoned without a judgement, still matter today."),
        t("Die „große Urkunde“ legte fest, dass auch der König an Gesetze gebunden ist. Zunächst ging es vor allem um die Rechte der Barone, und der Papst erklärte sie schon nach wenigen Wochen für ungültig. Später wurde sie zum Vorbild für Verfassungen, auch für die der USA.",
            "The 'Great Charter' laid down that even the king is bound by law. At first it was mainly about the rights of the barons, and the Pope declared it invalid after only a few weeks. Later it became a model for constitutions, including that of the USA."),
        t("Etwa 150 Jahre nach der normannischen Eroberung Englands (1066)", "About 150 years after the Norman conquest of England (1066)")),
    Event("konstantinopel", X, 1453, 15,
        t("Osmanische Truppen eroberten Konstantinopel, das heutige Istanbul, und beendeten damit das Byzantinische Reich. In welchem Jahr?", "Ottoman troops conquered Constantinople, today's Istanbul, bringing the Byzantine Empire to an end. In which year?"),
        t("1453, unter Sultan Mehmed II.", "1453, under Sultan Mehmed II."),
        t("Konstantinopel war über tausend Jahre lang die Hauptstadt des Oströmischen Reiches gewesen. Die Osmanen setzten riesige Kanonen gegen die berühmten Stadtmauern ein. Viele griechische Gelehrte flohen nach Italien und brachten antike Schriften mit, was die Renaissance beflügelte.",
            "Constantinople had been the capital of the Eastern Roman Empire for over a thousand years. The Ottomans used huge cannons against the famous city walls. Many Greek scholars fled to Italy, bringing ancient texts with them, which gave the Renaissance a boost."),
        t("Etwa zur Zeit von Gutenbergs Buchdruck", "Around the time of Gutenberg's printing press")),
    Event("unabhaengigkeit", M, 1776, 3,
        t("13 britische Kolonien in Nordamerika erklärten sich für unabhängig. In welchem Jahr?", "Thirteen British colonies in North America declared themselves independent. In which year?"),
        t("Am 4. Juli 1776; der Tag ist heute der Nationalfeiertag der USA.", "On 4 July 1776; the day is now the national holiday of the USA."),
        t("Die Kolonien wehrten sich gegen Steuern, die das britische Parlament ohne ihre Mitsprache beschloss. Die Erklärung, vor allem von Thomas Jefferson formuliert, nennt Leben, Freiheit und das Streben nach Glück als Rechte jedes Menschen. Erkämpft war die Unabhängigkeit aber erst nach einem Krieg, der bis 1783 dauerte.",
            "The colonies resisted taxes that the British Parliament imposed without giving them a say. The Declaration, written mainly by Thomas Jefferson, names life, liberty and the pursuit of happiness as rights of every person. But independence was only won after a war that lasted until 1783."),
        t("13 Jahre vor der Französischen Revolution", "13 years before the French Revolution")),
    Event("weltkrieg", E, 1914, 1,
        t("Nach dem Attentat von Sarajevo auf den österreichischen Thronfolger begann der Erste Weltkrieg. In welchem Jahr?", "After the assassination of the Austrian heir to the throne in Sarajevo, the First World War began. In which year?"),
        t("Im Sommer 1914; er dauerte bis November 1918.", "In the summer of 1914; it lasted until November 1918."),
        t("Ein Netz von Bündnissen zog innerhalb weniger Wochen fast alle Großmächte Europas in den Krieg. Viele glaubten an einen kurzen Krieg, doch an der Westfront erstarrte er in Schützengräben. Rund 17 Millionen Menschen starben, darunter fast zehn Millionen Soldaten.",
            "A web of alliances drew almost all of Europe's great powers into the war within a few weeks. Many expected a short war, but on the Western Front it froze into trench warfare. Around 17 million people died, nearly ten million of them soldiers."),
        t("Kurz vor der Russischen Revolution", "Shortly before the Russian Revolution")),
    Event("dampfmaschine", H, 1769, 10,
        t("James Watt ließ sich eine stark verbesserte Dampfmaschine patentieren, die die Industrialisierung antrieb. In welchem Jahr?", "James Watt patented a greatly improved steam engine that drove industrialisation. In which year?"),
        t("1769. Die Einheit der Leistung, das Watt, ist nach ihm benannt.", "1769. The unit of power, the watt, is named after him."),
        t("Dampfmaschinen gab es schon vorher, doch sie verschwendeten viel Kohle. Watt kühlte den Dampf in einem eigenen Behälter ab, statt jedes Mal den ganzen Zylinder; das sparte etwa drei Viertel des Brennstoffs. Bald trieben solche Maschinen Pumpen in Bergwerken, Spinnereien und Webereien an.",
            "Steam engines existed before, but they wasted a lot of coal. Watt cooled the steam in a separate container instead of cooling the whole cylinder every time; this saved about three quarters of the fuel. Soon such engines were driving pumps in mines, spinning mills and weaving mills."),
        t("Wenige Jahre vor der Unabhängigkeit der USA", "A few years before American independence")),
    Event("karl", H, 800, 15,
        t("Karl der Große wurde in Rom vom Papst zum Kaiser gekrönt. In welchem Jahr?", "Charlemagne was crowned emperor by the Pope in Rome. In which year?"),
        t("Am ersten Weihnachtstag des Jahres 800.", "On Christmas Day in the year 800."),
        t("Karl herrschte über ein Reich, das große Teile des heutigen Frankreichs, Deutschlands und Italiens umfasste. Er förderte Schulen und ließ alte Schriften abschreiben; die Schrift dieser Zeit ist die Grundlage unserer Kleinbuchstaben. Seine liebste Pfalz war Aachen, wo er auch begraben liegt.",
            "Charlemagne ruled an empire that covered large parts of today's France, Germany and Italy. He promoted schools and had old texts copied; the script of that time is the basis of our lower-case letters. His favourite palace was in Aachen, where he is also buried."),
        t("Eine sehr runde Zahl", "A very round number")),
    Event("einheit", E, 1990, 1,
        t("Die DDR trat der Bundesrepublik bei, und Deutschland war wieder vereint. In welchem Jahr?", "The GDR joined the Federal Republic, and Germany was united again. In which year?"),
        t("Am 3. Oktober 1990, knapp ein Jahr nach dem Fall der Mauer.", "On 3 October 1990, almost a year after the fall of the Wall."),
        t("Nach friedlichen Massenprotesten in der DDR fiel im November 1989 die Mauer. Die Einheit musste mit den vier Siegermächten des Zweiten Weltkriegs ausgehandelt werden, im Zwei-plus-Vier-Vertrag. Der 3. Oktober ist seitdem der Tag der Deutschen Einheit.",
            "After peaceful mass protests in the GDR, the Wall fell in November 1989. Unity had to be negotiated with the four victorious powers of the Second World War, in the Two Plus Four Treaty. Since then, 3 October has been the Day of German Unity."),
        t("Ein Jahr nach dem Mauerfall", "One year after the fall of the Wall")),
)

private fun history(): List<TaskDef> = EVENTS.map { e ->
    lab("hist.${e.key}", Topic.History, e.level, grade = 0.3f + 0.15f * e.level.ordinal.coerceAtMost(3)) {
        yearTask(
            "x", Topic.History, e.level,
            question = e.q.s, year = e.year, tolerance = e.tolerance,
            solution = e.solution.s, background = e.why.s, hint = e.hint.s,
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Logik: Rätsel mit Zufallszahlen
// ---------------------------------------------------------------------------------------------

private val LBg_daysDe = listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
private val LBg_daysEn = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

private fun logic(): List<TaskDef> = listOf(
    lab("logik.zaun", Topic.Logic, E, 0.25f) {
        val d = pick(2, 3, 4, 5)
        val k = int(5, if (hard) 30 else 15)
        val len = d * k
        val round = hard && chance(0.5)
        val posts = if (round) k else k + 1
        task(
            question = if (round) l("Rund um einen Teich soll ein ${len} m langer Zaun entstehen. Alle ${d} m kommt ein Pfosten. Wie viele Pfosten brauchst du?", "A ${len} m fence is to go all the way round a pond. There is a post every ${d} m. How many posts do you need?")
            else l("Ein gerader Zaun ist ${len} m lang. Alle ${d} m steht ein Pfosten, auch ganz am Anfang und ganz am Ende. Wie viele Pfosten sind es?", "A straight fence is ${len} m long. There is a post every ${d} m, including at the very start and the very end. How many posts are there?"),
            steps = listOf(
                num(l("Wie viele Lücken zwischen den Pfosten gibt es?", "How many gaps are there between the posts?"), k.toDouble(), abs = 0.0, done = l("${k} Lücken", "${k} gaps"),
                    hints = listOf(l("Kernidee: Teile die Länge durch den Abstand.", "Key idea: divide the length by the spacing."), "§${len} : ${d}§")),
                num(l("Und wie viele Pfosten?", "And how many posts?"), posts.toDouble(), abs = 0.0, done = l("${posts} Pfosten", "${posts} posts"),
                    hints = listOf(
                        if (round) l("Im Kreis schließt sich der Zaun: Der letzte Pfosten ist der erste.", "In a circle the fence closes up: the last post is the first.")
                        else l("Zeichne es klein: 3 m mit 1 m Abstand brauchen 4 Pfosten.", "Draw a small version: 3 m with 1 m spacing needs 4 posts."),
                        l("Vergleiche die Zahl der Lücken mit der Zahl der Pfosten.", "Compare the number of gaps with the number of posts."),
                    )),
            ),
            solution = listOf(
                "§${len} : ${d} = ${k}§ " + l("Lücken", "gaps"),
                if (round) l("Im Kreis gibt es so viele Pfosten wie Lücken: ${posts}.", "In a circle there are as many posts as gaps: ${posts}.")
                else l("Bei einer geraden Linie ist es ein Pfosten mehr: ${k} + 1 = ${posts}.", "On a straight line there is one post more: ${k} + 1 = ${posts}."),
            ),
            background = l(
                "Dieser Denkfehler hat einen eigenen Namen: Zaunpfahlfehler. Er passiert auch Profis, etwa beim Programmieren, wenn eine Schleife einmal zu oft oder zu wenig läuft. Der Trick: Lücken und Pfosten getrennt zählen.",
                "This mistake has its own name: the fencepost error. Even professionals make it, for example in programming, when a loop runs once too often or once too few. The trick is to count the gaps and the posts separately.",
            ),
        )
    },
    lab("logik.kalender", Topic.Logic, E, 0.3f) {
        val offsets = listOf(-2, -1, 1, 2)
        val given = pick(offsets)
        val asked = pick(offsets.filter { it != given })
        val known = int(0, 6)
        val today = Math.floorMod(known - given, 7)
        val answer = Math.floorMod(today + asked, 7)
        fun word(o: Int, capital: Boolean = false): String = when (o) {
            -2 -> l(if (capital) "Vorgestern" else "vorgestern", if (capital) "The day before yesterday" else "the day before yesterday")
            -1 -> l(if (capital) "Gestern" else "gestern", if (capital) "Yesterday" else "yesterday")
            1 -> l(if (capital) "Morgen" else "morgen", if (capital) "Tomorrow" else "tomorrow")
            else -> l(if (capital) "Übermorgen" else "übermorgen", if (capital) "The day after tomorrow" else "the day after tomorrow")
        }
        fun day(i: Int) = l(LBg_daysDe[i], LBg_daysEn[i])
        fun options(right: Int): List<String> = listOf(day(right)) + listOf(Math.floorMod(right + 1, 7), Math.floorMod(right - 1, 7), Math.floorMod(right + 2, 7)).map { day(it) }
        val verbDe = if (given < 0) "war" else "ist"
        val verbEn = if (given < 0) "was" else "is"
        val askDe = if (asked < 0) "war" else "ist"
        val askEn = if (asked < 0) "was" else "is"
        task(
            question = l("${word(given, true)} ${verbDe} ${day(known)}. Welcher Tag ${askDe} ${word(asked)}?", "${word(given, true)} ${verbEn} ${day(known)}. What day ${askEn} ${word(asked)}?"),
            steps = listOf(
                choice(l("Welcher Tag ist heute?", "What day is it today?"), options(today), done = l("Heute ist ${day(today)}.", "Today is ${day(today)}."),
                    hints = listOf(l("Kernidee: Rechne erst bis heute zurück oder vor.", "Key idea: first work out today."), l("${word(given, true)} liegt ${abs(given)} Tag${if (abs(given) > 1) "e" else ""} ${if (given < 0) "vor" else "nach"} heute.", "${word(given, true)} is ${abs(given)} day${if (abs(given) > 1) "s" else ""} ${if (given < 0) "before" else "after"} today."))),
                choice(l("Und ${word(asked)}?", "And ${word(asked)}?"), options(answer), done = l("${word(asked, true)} ${askDe} ${day(answer)}.", "${word(asked, true)} ${askEn} ${day(answer)}."),
                    hints = listOf(l("Jetzt von heute aus zählen.", "Now count from today."), l("${abs(asked)} Tag${if (abs(asked) > 1) "e" else ""} ${if (asked < 0) "zurück" else "weiter"}", "${abs(asked)} day${if (abs(asked) > 1) "s" else ""} ${if (asked < 0) "back" else "on"}"))),
            ),
            solution = listOf(
                l("Heute ist ${day(today)}, also ${askDe} ${word(asked)} ${day(answer)}.", "Today is ${day(today)}, so ${word(asked)} ${askEn} ${day(answer)}."),
            ),
            background = l(
                "Solche Rätsel wirken leicht, bringen aber viele durcheinander, weil man zwei Verschiebungen im Kopf behalten muss. Der sichere Weg ist ein fester Bezugspunkt: Erst den heutigen Tag bestimmen, dann weiterzählen. Dasselbe Prinzip hilft bei Zeitzonen und Fahrplänen.",
                "Puzzles like this look easy but confuse many people, because you have to keep two shifts in your head. The safe way is a fixed reference point: first work out today, then count on. The same principle helps with time zones and timetables.",
            ),
        )
    },
    lab("logik.rueckwaerts", Topic.Logic, E, 0.35f) {
        val x = int(2, if (hard) 40 else 15)
        val a = int(2, if (hard) 9 else 5)
        val b = int(1, if (hard) 30 else 12)
        val inner = a * x - b
        val r = 2 * inner
        task(
            question = l("Ich denke mir eine Zahl. Ich nehme sie mal ${a}, ziehe ${b} ab und verdopple das Ergebnis. Heraus kommt ${r}. Welche Zahl habe ich mir gedacht?", "I think of a number. I multiply it by ${a}, subtract ${b} and double the result. I get ${r}. What number did I think of?"),
            steps = listOf(
                num(l("Was stand vor dem Verdoppeln da?", "What was there before doubling?"), inner.toDouble(), abs = 0.0, done = "§${r} : 2 = ${inner}§",
                    hints = listOf(l("Kernidee: Geh rückwärts und mach jeden Schritt rückgängig.", "Key idea: go backwards and undo each step."), l("Das Gegenteil von Verdoppeln ist Halbieren.", "The opposite of doubling is halving."))),
                num(l("Und vor dem Abziehen?", "And before subtracting?"), (inner + b).toDouble(), abs = 0.0, done = "§${inner} + ${b} = ${inner + b}§",
                    hints = listOf(l("Abziehen macht man mit Addieren rückgängig.", "You undo subtracting by adding."), "§${inner} + ${b}§")),
                num(l("Die gedachte Zahl", "The number I thought of"), x.toDouble(), abs = 0.0, done = "§${inner + b} : ${a} = ${x}§",
                    hints = listOf(l("Mal nehmen macht man mit Teilen rückgängig.", "You undo multiplying by dividing."), "§${inner + b} : ${a}§")),
            ),
            solution = listOf("§${r} : 2 = ${inner}§", "§${inner} + ${b} = ${inner + b}§", "§${inner + b} : ${a} = ${x}§"),
            background = l(
                "Rückwärtsarbeiten ist eine der ältesten Problemlöse-Strategien. Der Mathematiker George Pólya empfahl sie 1945 in seinem Buch „How to Solve It“ (deutsch „Schule des Denkens“): Wenn das Ziel bekannt ist, beginne dort und frage, was unmittelbar davor gewesen sein muss. Auch Labyrinthe löst man oft schneller vom Ausgang her.",
                "Working backwards is one of the oldest problem-solving strategies. In 1945 the mathematician George Polya recommended it in his book How to Solve It: when you know the goal, start there and ask what must have come just before. Mazes, too, are often quicker to solve from the exit.",
            ),
        )
    },
    lab("logik.folge", Topic.Logic, M, 0.5f) {
        val kind = int(0, 5)
        val seq: List<Int>
        val next: Int
        val ruleDe: String
        val ruleEn: String
        when (kind) {
            0 -> { val a = int(2, 30); val d = int(3, 13); seq = List(5) { a + it * d }; next = a + 5 * d; ruleDe = "Jedes Mal kommt ${d} dazu."; ruleEn = "Add ${d} each time." }
            1 -> { val a = int(1, 5); val q = pick(2, 3); seq = List(5) { var v = a; repeat(it) { v *= q }; v }; next = seq.last() * q; ruleDe = "Jedes Mal mal ${q}."; ruleEn = "Multiply by ${q} each time." }
            2 -> { val c = int(-3, 5); seq = List(5) { (it + 1) * (it + 1) + c }; next = 36 + c; ruleDe = if (c == 0) "Quadratzahlen: 1², 2², 3², …" else "Quadratzahlen ${if (c > 0) "plus $c" else "minus ${-c}"}: 1², 2², 3², …"; ruleEn = if (c == 0) "Square numbers: 1², 2², 3², …" else "Square numbers ${if (c > 0) "plus $c" else "minus ${-c}"}: 1², 2², 3², …" }
            3 -> { val a = int(1, 20); seq = List(5) { a + it * (it + 1) / 2 }; next = a + 15; ruleDe = "Die Abstände wachsen: +1, +2, +3, +4, also +5."; ruleEn = "The gaps grow: +1, +2, +3, +4, so +5." }
            4 -> { val x = int(1, 5); val y = int(x, 8); val s = mutableListOf(x, y); while (s.size < 5) s += s[s.size - 1] + s[s.size - 2]; seq = s; next = s[3] + s[4]; ruleDe = "Jede Zahl ist die Summe der beiden davor."; ruleEn = "Each number is the sum of the two before it." }
            else -> { val a = int(1, 6); val p = int(1, 5).let { if (it == 2 * a) it + 1 else it }; val s = mutableListOf(a); while (s.size < 5) s += if (s.size % 2 == 1) s.last() * 2 else s.last() + p; seq = s; next = seq.last() * 2; ruleDe = "Abwechselnd mal 2 und plus ${p}."; ruleEn = "Alternately times 2 and plus ${p}." }
        }
        val gaps = seq.zipWithNext { u, v -> v - u }
        task(
            question = l("Wie geht die Folge weiter? ${seq.joinToString(", ")}, …", "What comes next? ${seq.joinToString(", ")}, …"),
            steps = listOf(num(l("Nächste Zahl", "Next number"), next.toDouble(), abs = 0.0, done = l("${next}. $ruleDe", "${next}. $ruleEn"),
                hints = listOf(
                    l("Kernidee: Schau dir die Abstände zwischen den Zahlen an: ${gaps.joinToString(", ") { if (it >= 0) "+$it" else "−${-it}" }}", "Key idea: look at the gaps between the numbers: ${gaps.joinToString(", ") { if (it >= 0) "+$it" else "−${-it}" }}"),
                    when (kind) {
                        1 -> l("Die Abstände selbst wachsen um denselben Faktor.", "The gaps themselves grow by the same factor.")
                        2 -> l("Vergleiche mit 1, 4, 9, 16, 25.", "Compare with 1, 4, 9, 16, 25.")
                        4 -> l("Addiere zwei Nachbarn.", "Add two neighbours.")
                        5 -> l("Es wechseln sich zwei Rechenarten ab.", "Two operations take turns.")
                        else -> l("Wie ändern sich die Abstände?", "How do the gaps change?")
                    },
                ))),
            solution = listOf(l(ruleDe, ruleEn), "§${seq.joinToString(",\\ ")},\\ ${next}§"),
            background = l(
                "Streng genommen kann jede Folge beliebig weitergehen; man sucht die einfachste Regel, die alle Zahlen erklärt. Folgen, in denen jede Zahl die Summe der beiden davor ist, beschrieb Leonardo von Pisa (Fibonacci) im Jahr 1202. Solche Zahlen findet man in den Spiralen von Sonnenblumen und Tannenzapfen.",
                "Strictly speaking, any sequence could continue in any way; you look for the simplest rule that explains all the numbers. Sequences in which each number is the sum of the two before were described by Leonardo of Pisa (Fibonacci) in 1202. Such numbers appear in the spirals of sunflowers and pine cones.",
            ),
        )
    },
    lab("logik.uhrwinkel", Topic.Logic, M, 0.6f) {
        val h = int(1, 11)
        val mins = pick(10, 15, 20, 25, 30, 35, 40, 45, 50)
        val minute = 6.0 * mins
        val hour = 30.0 * h + 0.5 * mins
        val raw = abs(hour - minute)
        val angle = if (raw > 180) 360 - raw else raw
        val time = "%d:%02d".format(h, mins)
        task(
            question = l("Es ist ${time} Uhr. Wie groß ist der kleinere Winkel zwischen Stunden- und Minutenzeiger?", "It is ${time}. How big is the smaller angle between the hour hand and the minute hand?"),
            steps = listOf(
                num(l("Stellung des Minutenzeigers in Grad, von der 12 aus", "Position of the minute hand in degrees, from 12"), minute, "°", abs = 0.01, done = "§${mins} \\cdot 6° = ${m(minute, 1)}°§",
                    hints = listOf(l("Kernidee: In 60 Minuten dreht er sich um 360°, also 6° pro Minute.", "Key idea: in 60 minutes it turns 360°, so 6° per minute."), "§${mins} \\cdot 6°§")),
                num(l("Stellung des Stundenzeigers in Grad", "Position of the hour hand in degrees"), hour, "°", abs = 0.01, done = "§${h} \\cdot 30° + ${mins} \\cdot 0{,}5° = ${m(hour, 1)}°§",
                    hints = listOf(l("Pro Stunde 30°, und er wandert in jeder Minute noch ein halbes Grad weiter.", "30° per hour, and it moves on half a degree every minute."), "§${h} \\cdot 30° + ${mins} \\cdot 0{,}5°§")),
                num(l("Winkel zwischen den Zeigern", "Angle between the hands"), angle, "°", abs = 0.01, done = "${n(angle, 1)}°",
                    hints = listOf(l("Unterschied der beiden Stellungen; über 180° nimmst du den Rest bis 360°.", "The difference of the two positions; if it's over 180°, take what's left up to 360°."), "§|${m(hour, 1)}° - ${m(minute, 1)}°|§")),
            ),
            solution = listOf("§|${m(hour, 1)}° - ${m(minute, 1)}°| = ${m(raw, 1)}°§") + if (raw > 180) listOf("§360° - ${m(raw, 1)}° = ${m(angle, 1)}°§") else emptyList(),
            background = l(
                "Der häufigste Fehler: Man vergisst, dass der Stundenzeiger nicht auf der vollen Stunde stehen bleibt. Um 3:15 Uhr zeigt er schon ein Viertel des Wegs zur 4, deshalb liegen die Zeiger nicht übereinander, sondern 7,5° auseinander. Genau übereinander stehen sie nur elfmal in zwölf Stunden.",
                "The most common mistake is forgetting that the hour hand doesn't stay on the full hour. At 3:15 it has already moved a quarter of the way to 4, so the hands are not on top of each other but 7.5° apart. They are exactly on top of each other only eleven times in twelve hours.",
            ),
        )
    },
    lab("logik.ritter", Topic.Logic, H, 0.65f) {
        // Ritter sagen immer die Wahrheit, Lügner lügen immer.
        val both = l("A und B sagen beide die Wahrheit", "A and B both tell the truth")
        val aOnly = l("A sagt die Wahrheit, B lügt", "A tells the truth, B lies")
        val bOnly = l("A lügt, B sagt die Wahrheit", "A lies, B tells the truth")
        val none = l("A und B lügen beide", "A and B both lie")
        val case = int(0, 3)
        val (said, right, why) = when (case) {
            0 -> Triple(
                l("A sagt: „Wir sind beide Lügner.“", "A says: \"We are both liars.\""), bOnly,
                listOf(l("Wäre A ehrlich, wäre A selbst ein Lügner, ein Widerspruch. Also lügt A.", "If A were honest, A would be a liar, a contradiction. So A lies."), l("Dann ist der Satz falsch: Nicht beide lügen, also sagt B die Wahrheit.", "Then the statement is false: they don't both lie, so B tells the truth.")),
            )
            1 -> Triple(
                l("A sagt: „Mindestens einer von uns lügt.“", "A says: \"At least one of us is a liar.\""), aOnly,
                listOf(l("Würde A lügen, wäre der Satz falsch und niemand würde lügen, auch A nicht, ein Widerspruch. Also ist A ehrlich.", "If A lied, the statement would be false and nobody would lie, not even A, a contradiction. So A is honest."), l("Dann stimmt der Satz: Einer lügt, und das muss B sein.", "Then the statement is true: someone lies, and that must be B.")),
            )
            2 -> Triple(
                l("A sagt: „B lügt.“ B sagt: „A und ich sind von derselben Sorte.“", "A says: \"B is a liar.\" B says: \"A and I are the same kind.\""), aOnly,
                listOf(l("Wäre B ehrlich, wären beide gleich, also auch A ehrlich. Dann würde B aber lügen, wie A sagt, ein Widerspruch. Also lügt B.", "If B were honest, both would be the same, so A would be honest too. But then B would be a liar, as A says, a contradiction. So B lies."), l("B lügt, also sind A und B verschieden: A sagt die Wahrheit, und tatsächlich lügt B.", "B lies, so A and B are different: A tells the truth, and indeed B lies.")),
            )
            else -> Triple(
                l("A sagt: „Ich bin ein Lügner, oder B sagt die Wahrheit.“", "A says: \"I am a liar, or B tells the truth.\""), both,
                listOf(l("Wäre A ein Lügner, wäre der erste Teil wahr und damit der ganze Satz, aber Lügner sagen nie etwas Wahres. Also ist A ehrlich.", "If A were a liar, the first part would be true, and so the whole statement, but liars never say anything true. So A is honest."), l("Dann stimmt der Satz. Weil A kein Lügner ist, muss der zweite Teil stimmen: B sagt die Wahrheit.", "Then the statement is true. As A is not a liar, the second part must hold: B tells the truth.")),
            )
        }
        task(
            question = l("Auf einer Insel sagen manche Menschen immer die Wahrheit, die anderen lügen immer. Du triffst A und B. $said Wer sagt die Wahrheit?", "On an island, some people always tell the truth and the others always lie. You meet A and B. $said Who tells the truth?"),
            steps = listOf(choice(l("Wer ist ehrlich?", "Who is honest?"), listOf(right) + listOf(both, aOnly, bOnly, none).filter { it != right }, done = right,
                hints = listOf(l("Kernidee: Nimm an, A sagt die Wahrheit, und prüfe, ob das ohne Widerspruch aufgeht. Dann das Gegenteil.", "Key idea: assume A tells the truth and check whether that works without a contradiction. Then try the opposite."), why.first()))),
            solution = why,
            background = l(
                "Rätsel dieser Art machte der Logiker Raymond Smullyan in den 1970er-Jahren berühmt. Sie trainieren genau das, was auch Mathematik und Programmieren brauchen: eine Annahme treffen, ihre Folgen durchdenken und sie verwerfen, sobald ein Widerspruch auftaucht. Man nennt das einen Widerspruchsbeweis.",
                "The logician Raymond Smullyan made puzzles like this famous in the 1970s. They train exactly what mathematics and programming need: make an assumption, think through its consequences and drop it as soon as a contradiction appears. This is called proof by contradiction.",
            ),
        )
    },
)

// ---------------------------------------------------------------------------------------------
// Allgemeinwissen: Stimmt das?
// ---------------------------------------------------------------------------------------------

private class Myth(val key: String, val level: Difficulty, val ok: Boolean, val claim: T, val answer: T, val why: T, val hint: T)

private val MYTHS = listOf(
    Myth("stier", E, false,
        t("Stiere werden wütend, wenn sie die Farbe Rot sehen.", "Bulls get angry when they see the colour red."),
        t("Es ist die Bewegung des Tuchs, nicht die Farbe.", "It's the movement of the cloth, not the colour."),
        t("Rinder können Rot und Grün kaum unterscheiden; ihnen fehlt die Art von Sehzellen, mit der wir Rot erkennen. In Versuchen griffen Stiere ein bewegtes Tuch an, egal ob es rot, blau oder weiß war, ein ruhig gehaltenes rotes Tuch dagegen kaum. Das Rot ist vor allem für das Publikum.",
            "Cattle can hardly tell red from green; they lack the type of cone cell we use to see red. In experiments, bulls charged at a moving cloth whether it was red, blue or white, but hardly at a red cloth held still. The red is mainly for the audience."),
        t("Rinder sehen die Welt anders als wir.", "Cattle see the world differently from us.")),
    Myth("kamel", E, false,
        t("Kamele speichern in ihren Höckern Wasser.", "Camels store water in their humps."),
        t("In den Höckern ist Fett.", "The humps hold fat."),
        t("Der Höcker ist ein Fettvorrat, von dem das Kamel tagelang zehren kann; schrumpft er, hängt er schlaff zur Seite. Wasser sparen Kamele anders: Sie schwitzen wenig, scheiden sehr trockenen Kot aus und können auf einmal rund 100 Liter trinken.",
            "The hump is a fat reserve the camel can live on for days; when it shrinks, it flops to one side. Camels save water in other ways: they sweat little, produce very dry droppings and can drink around 100 litres in one go."),
        t("Was braucht ein Tier, das tagelang nichts frisst?", "What does an animal need if it goes days without food?")),
    Myth("strauss", E, false,
        t("Strauße stecken bei Gefahr den Kopf in den Sand.", "Ostriches bury their heads in the sand when in danger."),
        t("Sie rennen weg oder legen sich flach auf den Boden.", "They run away or lie flat on the ground."),
        t("Strauße rennen mit bis zu 70 km/h davon oder treten kräftig zu. Spüren sie Gefahr am Nest, legen sie Kopf und Hals flach auf den Boden; von Weitem sieht es aus, als wäre der Kopf verschwunden. Außerdem drehen sie mit dem Schnabel regelmäßig die Eier in ihrer Erdmulde.",
            "Ostriches run away at up to 70 km/h or kick hard. If they sense danger at the nest, they lay their head and neck flat on the ground; from a distance it looks as if the head has vanished. They also regularly turn their eggs in the hollow in the ground with their beaks."),
        t("Ein Tier mit dem Kopf im Sand bekäme keine Luft.", "An animal with its head in the sand couldn't breathe.")),
    Myth("spinnen", E, false,
        t("Im Schlaf verschluckt jeder Mensch im Jahr etwa acht Spinnen.", "Everyone swallows about eight spiders a year in their sleep."),
        t("Dafür gibt es keinen einzigen Beleg.", "There isn't a single piece of evidence for it."),
        t("Die Zahl geistert seit den 1990er-Jahren durchs Internet, eine Quelle dafür gibt es nicht. Spinnen meiden schlafende Menschen eher: Atem, Herzschlag und Wärme wirken auf sie wie Warnsignale. Dass mal eine in einen offenen Mund krabbelt, ist extrem unwahrscheinlich.",
            "The figure has been going round the internet since the 1990s, but there is no source for it. Spiders tend to avoid sleeping people: breath, heartbeat and warmth act on them like warning signals. That one crawls into an open mouth is extremely unlikely."),
        t("Woher sollte jemand diese Zahl kennen?", "How would anyone know this number?")),
    Myth("honig", M, true,
        t("Honig kann Tausende Jahre lang essbar bleiben.", "Honey can stay edible for thousands of years."),
        t("Gut verschlossen verdirbt er praktisch nicht.", "Well sealed, it practically never goes off."),
        t("Honig enthält sehr wenig Wasser und sehr viel Zucker, ist leicht sauer und bildet mithilfe eines Enzyms der Bienen geringe Mengen Wasserstoffperoxid. Bakterien und Pilze haben darin kaum eine Chance. Wird er mit der Zeit fest, ist das nur Zucker, der auskristallisiert.",
            "Honey contains very little water and a lot of sugar, is slightly acidic and, with the help of an enzyme from the bees, forms small amounts of hydrogen peroxide. Bacteria and fungi hardly stand a chance in it. If it goes solid over time, that is just the sugar crystallising."),
        t("Was mögen Bakterien gar nicht?", "What do bacteria really dislike?")),
    Myth("everest", M, false,
        t("Der Mount Everest ist der höchste Berg der Erde, wenn man vom Fuß bis zum Gipfel misst.", "Mount Everest is the tallest mountain on Earth when measured from base to summit."),
        t("Vom Fuß aus gemessen ist der Mauna Kea auf Hawaii höher.", "Measured from its base, Mauna Kea in Hawaii is taller."),
        t("Mit 8849 Metern ist der Everest der höchste Berg über dem Meeresspiegel. Der Mauna Kea ragt zwar nur 4207 Meter aus dem Meer, sein Fuß liegt aber tief am Meeresboden; von dort gemessen ist er über 10.000 Meter hoch. Der Everest dagegen erhebt sich aus einem schon sehr hohen Hochland.",
            "At 8,849 metres, Everest is the highest mountain above sea level. Mauna Kea rises only 4,207 metres out of the sea, but its base lies deep on the ocean floor; measured from there it is over 10,000 metres tall. Everest, by contrast, rises from a plateau that is already very high."),
        t("Nicht jeder Berg beginnt an Land.", "Not every mountain starts on land.")),
    Myth("sahara", M, false,
        t("Die Sahara ist die größte Wüste der Erde.", "The Sahara is the largest desert on Earth."),
        t("Die größte Wüste ist die Antarktis.", "The largest desert is Antarctica."),
        t("Als Wüste gilt ein Gebiet, in dem sehr wenig Niederschlag fällt, nicht unbedingt ein heißes. Im Inneren der Antarktis fallen nur wenige Zentimeter im Jahr, und mit 14 Millionen Quadratkilometern ist sie größer als die Sahara mit gut 9 Millionen. Die Sahara ist aber die größte heiße Wüste.",
            "A desert is an area with very little precipitation, not necessarily a hot one. The interior of Antarctica gets only a few centimetres a year, and at 14 million square kilometres it is larger than the Sahara with just over 9 million. The Sahara is, however, the largest hot desert."),
        t("Eine Wüste muss nicht heiß sein.", "A desert doesn't have to be hot.")),
    Myth("freiheitsstatue", M, true,
        t("Die Freiheitsstatue in New York war anfangs kupferbraun und glänzend.", "The Statue of Liberty in New York was originally shiny copper-brown."),
        t("Grün wurde sie erst mit der Zeit.", "It only turned green over time."),
        t("Ihre Haut besteht aus Kupferplatten, die nur gut zwei Millimeter dick sind. Luft, Regen und Salz ließen auf dem Kupfer eine grüne Schicht wachsen, die Patina; nach etwa 20 bis 30 Jahren war die Statue ganz grün. Die Patina schützt das Kupfer darunter, deshalb hat man sie nie entfernt.",
            "Its skin is made of copper sheets only a little over two millimetres thick. Air, rain and salt made a green layer grow on the copper, the patina; after about 20 to 30 years the statue was completely green. The patina protects the copper underneath, which is why it was never removed."),
        t("Woraus ist ihre Haut gemacht?", "What is its skin made of?")),
    Myth("australien", H, true,
        t("Australien ist breiter als der Mond.", "Australia is wider than the Moon."),
        t("Australien misst von West nach Ost rund 4000 km, der Mond 3474 km.", "Australia is about 4,000 km from west to east; the Moon is 3,474 km across."),
        t("Der Mond wirkt riesig, ist aber nur gut ein Viertel so breit wie die Erde. Bei Vollmond kannst du ihn mit dem kleinen Finger am ausgestreckten Arm leicht verdecken. Dass er am Horizont größer aussieht als hoch am Himmel, ist eine Täuschung unseres Gehirns.",
            "The Moon looks huge, but it is only just over a quarter as wide as the Earth. At full moon you can easily cover it with your little finger at arm's length. That it looks bigger near the horizon than high in the sky is a trick of our brain."),
        t("Der Mond hat gut ein Viertel des Erddurchmessers.", "The Moon is just over a quarter of the Earth's diameter.")),
    Myth("kochen", H, true,
        t("Auf dem Gipfel des Mount Everest kocht Wasser schon bei etwa 70 °C.", "At the summit of Mount Everest, water boils at only about 70 °C."),
        t("Der Luftdruck ist dort nur etwa ein Drittel so hoch wie am Meer.", "The air pressure there is only about a third of that at sea level."),
        t("Wasser kocht, wenn sein Dampf so stark drückt wie die Luft darüber. Je dünner die Luft, desto früher ist das erreicht. Nudeln werden in großer Höhe deshalb kaum gar, und ein Schnellkochtopf macht es umgekehrt: Er erhöht den Druck, und das Wasser wird über 100 °C heiß.",
            "Water boils when its vapour pushes as hard as the air above it. The thinner the air, the sooner that happens. That's why pasta hardly cooks at high altitude, and a pressure cooker does the opposite: it raises the pressure, and the water gets hotter than 100 °C."),
        t("Was ändert sich mit der Höhe?", "What changes with altitude?")),
    Myth("glas", X, true,
        t("Ein Glas Wasser enthält mehr Wassermoleküle, als es Gläser voll Wasser in allen Ozeanen gibt.", "A glass of water contains more water molecules than there are glasses of water in all the oceans."),
        t("Rund 8 · 10²⁴ Moleküle gegen etwa 5 · 10²¹ Gläser.", "About 8 · 10²⁴ molecules against about 5 · 10²¹ glasses."),
        t("Ein Glas mit 250 Millilitern enthält etwa 14 Mol Wasser, also rund 8 Quadrillionen Moleküle. Die Ozeane fassen etwa 1,3 Milliarden Kubikkilometer, das sind ungefähr 5 Trilliarden Gläser. Moleküle sind so winzig, dass unser Gefühl für große Zahlen hier völlig versagt.",
            "A 250-millilitre glass contains about 14 moles of water, so around 8 septillion molecules. The oceans hold about 1.3 billion cubic kilometres, which is roughly 5 sextillion glasses. Molecules are so tiny that our feel for large numbers fails completely here."),
        t("Ein Mol sind rund 6 · 10²³ Teilchen.", "A mole is about 6 · 10²³ particles.")),
    Myth("diamant", X, false,
        t("Diamanten entstehen aus gepresster Kohle.", "Diamonds form from compressed coal."),
        t("Die meisten entstanden tief im Erdmantel aus Kohlenstoff, lange bevor es Kohle gab.", "Most formed deep in the mantle from carbon, long before coal existed."),
        t("Kohle stammt aus Pflanzen und liegt höchstens wenige Kilometer tief. Diamanten bilden sich dagegen in 150 bis 200 Kilometern Tiefe bei über 1000 °C, und viele sind über eine Milliarde Jahre alt, älter als die ersten Landpflanzen. Vulkanische Schlote trugen sie schnell nach oben.",
            "Coal comes from plants and lies at most a few kilometres deep. Diamonds, by contrast, form 150 to 200 kilometres down at over 1000 °C, and many are over a billion years old, older than the first land plants. Volcanic pipes carried them up quickly."),
        t("Wie alt ist die älteste Kohle, und wie tief liegt sie?", "How old is the oldest coal, and how deep does it lie?")),
)

private fun myths(): List<TaskDef> = MYTHS.map { f ->
    lab("fakt.${f.key}", Topic.FunFacts, f.level, grade = 0.3f + 0.15f * f.level.ordinal.coerceAtMost(3)) {
        val yes = tr("Stimmt", "True")
        val no = tr("Stimmt nicht", "False")
        quiz(
            "x", Topic.FunFacts, f.level,
            question = f.claim.s,
            prompt = tr("Stimmt das?", "True or false?"),
            correct = if (f.ok) yes else no, wrong = listOf(if (f.ok) no else yes),
            solution = (if (f.ok) tr("Stimmt. ", "True. ") else tr("Stimmt nicht. ", "False. ")) + f.answer.s,
            background = f.why.s, hint = f.hint.s,
        )
    }
}
