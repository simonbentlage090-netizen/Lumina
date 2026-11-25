package de.funke.app.ui.challenge

import kotlin.math.pow
import kotlin.math.sqrt

/* Ausbau (seit 13.2): Geschichte, Allgemeinwissen und Spaß-Fakten. Texte selbst geschrieben. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard

object WorldNew4 {
    val all: List<TaskDef> get() = history + knowledge + funFacts

    private val history = listOf(
        // ------------------------------------------------ leicht
        lab("hist.generationen", Topic.History, E, 0.3f) {
            val gen = pick(25, 30)
            val count = 2000 / gen
            task(
                question = l("Zur Zeit von Kaiser Augustus, vor rund 2000 Jahren, lebte einer deiner direkten Vorfahren. Wenn eine Generation im Mittel ${gen} Jahre dauert: Wie viele Generationen liegen zwischen ihm und dir?", "At the time of the Emperor Augustus, around 2000 years ago, one of your direct ancestors was alive. If a generation lasts ${gen} years on average, how many generations lie between that ancestor and you?"),
                steps = listOf(num(l("Generationen", "Generations"), count.toDouble(), "", abs = 1.0, done = l("etwa $count", "about $count"),
                    hints = listOf(l("Kernidee: Eine Generation ist der Abstand zwischen der Geburt der Eltern und der ihrer Kinder.", "Key idea: a generation is the gap between the birth of the parents and that of their children."), """§\dfrac{2000}{${gen}}§"""))),
                solution = listOf("""§\dfrac{2000}{${gen}} \approx ${count}§""", l("Nur rund $count Menschen, jeweils Elternteil und Kind, verbinden dich mit dem alten Rom.", "Only around $count people, each a parent and child, link you to ancient Rome.")),
                background = l("Geschichte wirkt weit weg, aber die Kette ist überraschend kurz: $count Menschen würden in einen Bus passen. Schon deine Urgroßeltern haben vielleicht noch Menschen gekannt, die vor 1850 geboren wurden. In Generationen zu denken, macht Geschichte greifbarer als Jahreszahlen.", "History feels far away, but the chain is surprisingly short: $count people would fit in a bus. Your great-grandparents may well have known people born before 1850. Thinking in generations makes history more tangible than dates."),
            )
        },
        mc("hist.wikinger", Topic.History, E,
            t("Auf Bildern und in Comics tragen Wikinger oft Helme mit Hörnern. Wie war es wirklich?", "In pictures and comics Vikings often wear helmets with horns. What was it really like?"),
            t("Antwort", "Answer"),
            t("Im Kampf trugen sie keine Hörnerhelme. Das Bild stammt aus dem 19. Jahrhundert.", "They didn't wear horned helmets in battle. The image comes from the 19th century."),
            listOf(t("Alle Wikinger trugen Hörnerhelme, um Feinde zu erschrecken.", "All Vikings wore horned helmets to frighten their enemies."), t("Nur die Anführer trugen Hörner.", "Only the leaders wore horns."), t("Die Hörner waren aus Gold und zeigten den Reichtum.", "The horns were made of gold and showed wealth.")),
            t("Kernidee: Was passiert im Nahkampf mit Hörnern am Kopf, an denen eine gegnerische Axt hängen bleiben kann?", "Key idea: what happens in close combat with horns on your head that an enemy axe can catch on?"),
            t("Archäologen haben bisher nur einen fast vollständigen Wikingerhelm gefunden, einen schlichten Eisenhelm.", "So far archaeologists have found only one almost complete Viking helmet, a plain iron one."),
            t("Wikinger trugen, wenn überhaupt, einfache runde Helme aus Eisen oder Leder. Hörner wären im Kampf gefährlich und unpraktisch gewesen.", "Vikings wore, if anything, simple round helmets of iron or leather. Horns would have been dangerous and impractical in battle."),
            t("Die Hörner erfand im Wesentlichen das 19. Jahrhundert, als Maler und Kostümbildner die nordische Vergangenheit romantisch verklärten, berühmt etwa bei Richard Wagners Opern. Ältere Hörnerhelme aus der Bronzezeit gab es zwar, aber sie waren über 1500 Jahre älter und dienten wohl Zeremonien. Viele Bilder der Vergangenheit stammen aus späteren Zeiten.", "The horns were essentially invented in the 19th century, when painters and costume designers romanticised the Nordic past, famously in productions of Richard Wagner's operas. There were older horned helmets from the Bronze Age, but they were over 1500 years older and probably served ceremonies. Many images of the past come from later times."),
        ),
        mc("hist.marathon", Topic.History, E,
            t("Ein Marathon ist genau 42,195 km lang. Woher kommt diese krumme Zahl?", "A marathon is exactly 42.195 km long. Where does this odd number come from?"),
            t("Herkunft", "Origin"),
            t("Bei den Olympischen Spielen 1908 in London führte die Strecke vom Schloss Windsor bis vor die königliche Loge im Stadion.", "At the 1908 Olympics in London the course ran from Windsor Castle to the royal box in the stadium."),
            listOf(t("Genau so weit lief der Bote im alten Griechenland von Marathon nach Athen.", "That's exactly how far the messenger ran from Marathon to Athens in ancient Greece."), t("Es ist ein Tausendstel des Erdumfangs.", "It's a thousandth of the circumference of the Earth."), t("Die Zahl ergab sich aus einer Umrechnung aus Meilen beim ersten Marathon 1896.", "The number came from converting miles at the first marathon in 1896.")),
            t("Kernidee: Bei den ersten modernen Spielen 1896 war die Strecke rund 40 km lang, später schwankte sie.", "Key idea: at the first modern Games in 1896 the course was about 40 km long; later it varied."),
            t("Die Zahl entspricht genau 26 Meilen und 385 Yards.", "The figure corresponds exactly to 26 miles and 385 yards."),
            t("1908 wurde der Start so gelegt, dass die königlichen Kinder ihn von Windsor aus sehen konnten, und das Ziel vor die Loge der Königin. Das ergab 26 Meilen und 385 Yards, und diese Länge wurde 1921 zur Norm.", "In 1908 the start was placed so that the royal children could watch it from Windsor, and the finish in front of the queen's box. That came to 26 miles and 385 yards, and this length became the standard in 1921."),
            t("Die Legende vom Läufer Pheidippides, der 490 v. Chr. den Sieg bei Marathon nach Athen meldete und dann tot umfiel, ist erst Jahrhunderte später aufgeschrieben worden und wohl ausgeschmückt. Der griechische Geschichtsschreiber Herodot erzählt eine andere Geschichte: einen Lauf nach Sparta, über 200 km weit.", "The legend of the runner Pheidippides, who brought news of the victory at Marathon to Athens in 490 BC and then dropped dead, was only written down centuries later and is probably embellished. The Greek historian Herodotus tells a different story: a run to Sparta, over 200 km."),
        ),
        mc("hist.monatsnamen", Topic.History, E,
            t("„Septem“ heißt auf Latein sieben, „octo“ acht, „decem“ zehn. Warum ist der September dann der neunte Monat und der Dezember der zwölfte?", "'Septem' means seven in Latin, 'octo' eight, 'decem' ten. So why is September the ninth month and December the twelfth?"),
            t("Grund", "Reason"),
            t("Das römische Jahr begann ursprünglich im März.", "The Roman year originally began in March."),
            listOf(t("Ein Schreibfehler im Mittelalter hat die Namen vertauscht.", "A copying mistake in the Middle Ages swapped the names."), t("Zwei Monate wurden zu Ehren von Kaisern nachträglich davorgesetzt.", "Two months were later inserted in front in honour of emperors."), t("Die Römer zählten ab null.", "The Romans counted from zero.")),
            t("Kernidee: Zähl ab März: März 1, April 2, … Welcher Monat ist dann der siebte?", "Key idea: count from March: March 1, April 2, … Which month is then the seventh?"),
            t("Juli und August hießen früher Quintilis und Sextilis, der fünfte und sechste.", "July and August used to be called Quintilis and Sextilis, the fifth and the sixth."),
            t("Im ältesten römischen Kalender war März der erste Monat, September also wirklich der siebte. Später verlegte man den Jahresbeginn auf den Januar, aber die Namen blieben.", "In the oldest Roman calendar March was the first month, so September really was the seventh. Later the start of the year was moved to January, but the names stayed."),
            t("Die Umbenennung von Quintilis und Sextilis in Juli und August ehrte Julius Caesar und Kaiser Augustus, die Zählung verschob sie aber nicht. Der Neujahrstag wanderte schon 153 v. Chr. auf den 1. Januar, weil dann die neuen Konsuln ihr Amt antraten. Dass der Februar so kurz ist und den Schalttag bekommt, stammt auch noch daher, dass er einst das Ende des Jahres war.", "Renaming Quintilis and Sextilis as July and August honoured Julius Caesar and the Emperor Augustus, but it didn't shift the numbering. New Year's Day moved to 1 January as early as 153 BC, because that's when the new consuls took office. That February is so short and gets the leap day also goes back to its once being the end of the year."),
        ),
        mc("hist.napoleon", Topic.History, E,
            t("Napoleon Bonaparte gilt bis heute als besonders klein. Stimmt das?", "Napoleon Bonaparte is still thought of as particularly short. Is that true?"),
            t("Antwort", "Answer"),
            t("Nein, mit etwa 1,69 m war er für seine Zeit durchschnittlich groß.", "No, at about 1.69 m he was of average height for his time."),
            listOf(t("Ja, er war nur etwa 1,50 m groß.", "Yes, he was only about 1.50 m tall."), t("Ja, deshalb trug er immer hohe Hüte.", "Yes, that's why he always wore tall hats."), t("Nein, er war mit über 1,90 m ein Riese.", "No, at over 1.90 m he was a giant.")),
            t("Kernidee: In Frankreich maß man damals in französischen Zoll, die länger waren als die englischen.", "Key idea: in France at the time people measured in French inches, which were longer than English ones."),
            t("Seine Größe wurde als „5 Fuß 2 Zoll“ angegeben, in französischen Maßen.", "His height was given as '5 feet 2 inches', in French units."),
            t("„5 Fuß 2“ in französischen Maßen sind etwa 1,69 m. In englische Fuß gelesen klang es nach 1,57 m. Dazu kamen britische Spottzeichnungen, die ihn als winzigen Wüterich zeigten.", "'5 feet 2' in French units is about 1.69 m. Read as English feet it sounded like 1.57 m. On top of that came British cartoons showing him as a tiny hothead."),
            t("Französische Männer waren damals im Mittel etwa 1,65 m groß. Dass er von seinen großgewachsenen Gardesoldaten umgeben war und liebevoll „der kleine Korporal“ hieß, half dem Mythos. Er zeigt, wie Propaganda und ein Umrechnungsfehler ein Bild formen können, das zwei Jahrhunderte überdauert.", "French men at the time were about 1.65 m tall on average. That he was surrounded by his tall Guards and was affectionately called 'the little corporal' helped the myth. It shows how propaganda and a conversion error can shape an image that lasts two centuries."),
        ),
        // ------------------------------------------------ mittel
        lab("hist.vorfahren", Topic.History, M, 0.55f) {
            val g = pick(30, 40, 35); val anc = 2.0.pow(g)
            task(
                question = l("Du hast 2 Eltern, 4 Großeltern, 8 Urgroßeltern. Wie viele Vorfahren hättest du rechnerisch vor ${g} Generationen, also vor etwa ${g * 25} Jahren?", "You have 2 parents, 4 grandparents, 8 great-grandparents. How many ancestors would you have on paper ${g} generations ago, i.e. about ${g * 25} years ago?"),
                steps = listOf(big(l("Vorfahren", "Ancestors"), anc, "", rel = 0.02, done = """§2^{${g}} \approx ${sci(anc)}§""",
                    hints = listOf(l("Kernidee: Mit jeder Generation verdoppelt sich die Zahl.", "Key idea: the number doubles with every generation."), """§2^{${g}}§"""))),
                solution = listOf("""§2^{${g}} \approx ${sci(anc)}§""", l("Das sind mehr Menschen, als damals auf der ganzen Erde lebten.", "That's more people than were alive on the whole Earth at the time.")),
                background = l("Der Widerspruch löst sich auf, weil viele Plätze im Stammbaum von denselben Menschen besetzt sind: Irgendwann heirateten entfernte Cousins und Cousinen, und ihre Vorfahren zählen doppelt. Man nennt das Ahnenschwund. Daraus folgt Erstaunliches: Jeder heutige Europäer stammt sehr wahrscheinlich von jedem Menschen ab, der vor gut 1000 Jahren in Europa lebte und heute noch Nachkommen hat, also wohl auch von Karl dem Großen.", "The contradiction is resolved because many places in the family tree are filled by the same people: at some point distant cousins married, and their ancestors count twice. This is called pedigree collapse. It leads to something astonishing: every European today very probably descends from every person who lived in Europe a good 1000 years ago and still has descendants, probably including Charlemagne."),
            )
        },
        mc("hist.pest", Topic.History, M,
            t("Zwischen 1347 und 1351 starb in Europa etwa ein Drittel der Bevölkerung am „Schwarzen Tod“. Was übertrug die Pest hauptsächlich?", "Between 1347 and 1351 about a third of Europe's population died of the 'Black Death'. What mainly transmitted the plague?"),
            t("Übertragung", "Transmission"),
            t("Bakterien, die vor allem Flöhe von Ratten auf Menschen brachten", "Bacteria that were mainly carried to people by fleas from rats"),
            listOf(t("Ein Virus in verunreinigtem Trinkwasser", "A virus in contaminated drinking water"), t("Giftige Dämpfe aus Sümpfen", "Poisonous vapours from swamps"), t("Verdorbenes Getreide", "Spoiled grain")),
            t("Kernidee: Der Erreger wurde 1894 entdeckt und ist ein Bakterium. Er braucht ein Tier, das ihn von Mensch zu Mensch trägt.", "Key idea: the pathogen was discovered in 1894 and is a bacterium. It needs an animal to carry it from person to person."),
            t("Die Pest kam mit Handelsschiffen nach Europa, auf denen viele Ratten lebten.", "The plague came to Europe on trading ships, on which many rats lived."),
            t("Das Bakterium Yersinia pestis lebt in Nagetieren. Flöhe übertrugen es beim Stechen auf Menschen. Die Lungenpest konnte sich dann auch direkt durch Husten verbreiten.", "The bacterium Yersinia pestis lives in rodents. Fleas passed it to people when they bit. Pneumonic plague could then also spread directly through coughing."),
            t("Die Menschen damals glaubten an schlechte Luft, sogenannte Miasmen, oder an göttliche Strafe, und in vielen Städten wurden Juden zu Unrecht beschuldigt und ermordet. Eine wirksame Maßnahme entstand dennoch: Hafenstädte wie Ragusa und Venedig ließen Schiffe 30 und später 40 Tage warten. Aus „quaranta giorni“, vierzig Tage, wurde das Wort Quarantäne.", "People at the time believed in bad air, so-called miasmas, or in divine punishment, and in many cities Jews were wrongly blamed and murdered. Yet one effective measure did emerge: port cities like Ragusa and Venice made ships wait 30 and later 40 days. From 'quaranta giorni', forty days, came the word quarantine."),
        ),
        mc("hist.rosetta", Topic.History, M,
            t("Über 1400 Jahre lang konnte niemand die ägyptischen Hieroglyphen lesen. 1822 gelang es Jean-François Champollion. Was war der Schlüssel?", "For over 1400 years nobody could read Egyptian hieroglyphs. In 1822 Jean-François Champollion succeeded. What was the key?"),
            t("Schlüssel", "Key"),
            t("Ein Stein mit demselben Text in Hieroglyphen, einer späteren ägyptischen Schrift und Altgriechisch", "A stone with the same text in hieroglyphs, a later Egyptian script and ancient Greek"),
            listOf(t("Ein Wörterbuch, das ein Pharao hinterlassen hatte", "A dictionary left behind by a pharaoh"), t("Die Erkenntnis, dass jedes Bild genau ein Wort bedeutet", "The realisation that every picture means exactly one word"), t("Mönche, die die Sprache noch mündlich überliefert hatten", "Monks who had still passed the language down by word of mouth")),
            t("Kernidee: Wie würdest du eine unbekannte Schrift knacken? Am besten mit einem Text, dessen Inhalt du schon kennst.", "Key idea: how would you crack an unknown script? Best with a text whose content you already know."),
            t("Altgriechisch konnten Gelehrte im 19. Jahrhundert lesen.", "Scholars in the 19th century could read ancient Greek."),
            t("Der Stein von Rosette, 1799 gefunden, enthält einen Erlass von 196 v. Chr. in drei Schriften. Über Königsnamen wie Ptolemaios in Ringen fand Champollion heraus, dass viele Hieroglyphen für Laute stehen, nicht nur für Wörter.", "The Rosetta Stone, found in 1799, holds a decree from 196 BC in three scripts. Using royal names such as Ptolemy in cartouches, Champollion worked out that many hieroglyphs stand for sounds, not just for words."),
            t("Entscheidend war auch, dass Champollion Koptisch beherrschte, die späte Form der ägyptischen Sprache, die in der Kirche noch gebraucht wurde. Der Stein steht heute im British Museum in London. Bis heute gilt der Name „Rosetta-Stein“ für jeden Schlüssel, der etwas Unverständliches verständlich macht.", "It was also crucial that Champollion knew Coptic, the late form of the Egyptian language, which was still used in the church. The stone is in the British Museum in London today. To this day the name 'Rosetta Stone' stands for any key that makes something incomprehensible understandable."),
        ),
        lab("hist.jahrnull", Topic.History, M, 0.55f) {
            val (a, b) = pick(10 to 10, 5 to 20, 44 to 14, 1 to 1)
            val yrs = a + b - 1
            task(
                question = l("Wie viele Jahre liegen zwischen dem 1. Januar ${a} v. Chr. und dem 1. Januar ${b} n. Chr.?", "How many years lie between 1 January ${a} BC and 1 January AD ${b}?"),
                steps = listOf(num(l("Jahre", "Years"), yrs.toDouble(), "", abs = 0.0, done = "$yrs",
                    hints = listOf(l("Kernidee: In unserer Zeitrechnung gibt es kein Jahr 0. Auf das Jahr 1 v. Chr. folgt direkt das Jahr 1 n. Chr.", "Key idea: there's no year 0 in our calendar. 1 BC is followed directly by AD 1."), l("Vom 1. Januar 1 v. Chr. bis zum 1. Januar 1 n. Chr. ist es also genau ein Jahr.", "So from 1 January 1 BC to 1 January AD 1 is exactly one year.")))),
                solution = listOf(l("${a} + ${b} − 1 = ${yrs} Jahre, weil das Jahr 0 fehlt.", "${a} + ${b} − 1 = ${yrs} years, because there is no year 0.")),
                background = l("Der Mönch Dionysius Exiguus legte um 525 die Zählung nach Christi Geburt fest. Die Null kannte man in Europa damals noch nicht, sie kam erst Jahrhunderte später über arabische Gelehrte aus Indien. Deshalb begann das dritte Jahrtausend genau genommen erst am 1. Januar 2001, auch wenn fast alle schon 2000 feierten. Astronomen rechnen übrigens mit einem Jahr 0, um sich solche Fehler zu sparen.", "Around 525 the monk Dionysius Exiguus established counting from the birth of Christ. At the time zero wasn't known in Europe; it only arrived centuries later from India via Arab scholars. That's why, strictly speaking, the third millennium began on 1 January 2001, even though almost everyone celebrated in 2000. Astronomers, by the way, use a year 0 to avoid such errors."),
            )
        },
        lab("hist.aquaedukt", Topic.History, M, 0.55f) {
            val len = 50.0; val drop = 12.6; val cmkm = drop / len * 100
            task(
                question = l("Die römische Wasserleitung nach Nîmes, zu der die berühmte Brücke Pont du Gard gehört, ist rund ${n(len, 0)} km lang und fällt auf dieser Strecke insgesamt nur ${n(drop, 1)} m ab. Um wie viele Zentimeter sinkt sie im Mittel pro Kilometer?", "The Roman aqueduct to Nîmes, which includes the famous Pont du Gard bridge, is around ${n(len, 0)} km long and drops only ${n(drop, 1)} m in total over this distance. By how many centimetres does it fall on average per kilometre?"),
                steps = listOf(num(l("Gefälle in cm pro km", "Gradient in cm per km"), cmkm, "cm", abs = 0.5, done = """§\approx ${m(cmkm, 0)}\ \text{cm/km}§""",
                    hints = listOf(l("Kernidee: Gesamtgefälle in Zentimetern geteilt durch die Länge in Kilometern.", "Key idea: total drop in centimetres divided by the length in kilometres."), """§\dfrac{${m(drop * 100, 0)}\ \text{cm}}{${m(len, 0)}\ \text{km}}§"""))),
                solution = listOf("""§\dfrac{${m(drop * 100, 0)}}{${m(len, 0)}} \approx ${m(cmkm, 0)}\ \text{cm pro km}§""".let { l(it, it.replace("cm pro km", "cm per km")) }),
                background = l("Das ist ein Gefälle von 0,025 %, auf einen Meter nur ein Viertelmillimeter. Ohne Wasserwaage mit Libelle und ohne Laser schafften die römischen Vermesser das mit dem Chorobat, einem langen Holztisch mit Wasserrinne, und mit Lotschnüren. Die Leitung lieferte Nîmes jeden Tag rund 35.000 m³ Wasser, mehrere Hundert Liter pro Einwohner.", "That's a gradient of 0.025%, only a quarter of a millimetre per metre. Without a spirit level and without lasers, Roman surveyors achieved this with the chorobates, a long wooden table with a water channel, and with plumb lines. The aqueduct supplied Nîmes with around 35,000 m³ of water a day, several hundred litres per inhabitant."),
            )
        },
        mc("hist.einheitszeit", Topic.History, M,
            t("Bis 1893 hatte in Deutschland fast jede Stadt ihre eigene Uhrzeit, nach dem Stand der Sonne. Was gab den Ausschlag, eine einheitliche Zeit für das ganze Land einzuführen?", "Until 1893 almost every town in Germany had its own time, set by the position of the Sun. What tipped the balance towards introducing a single time for the whole country?"),
            t("Grund", "Reason"),
            t("Die Eisenbahn: Fahrpläne mit vielen verschiedenen Ortszeiten waren ein Chaos.", "The railways: timetables with many different local times were chaos."),
            listOf(t("Die Erfindung der Armbanduhr", "The invention of the wristwatch"), t("Ein Beschluss der Kirche für einheitliche Gottesdienste", "A church decision for uniform services"), t("Die Einführung des Radios", "The introduction of radio")),
            t("Kernidee: Ortszeiten stören erst, wenn Menschen schnell von Stadt zu Stadt reisen.", "Key idea: local times only become a nuisance when people travel quickly from town to town."),
            t("Zwischen Aachen und Königsberg lag damals fast eine Stunde Unterschied in der Sonnenzeit.", "Between Aachen and Königsberg at the time there was almost an hour's difference in solar time."),
            t("Mit der Eisenbahn wurden Reisen so schnell, dass jede Ortszeit Verwirrung stiftete. Bahnen rechneten intern mit einer einheitlichen Zeit. 1893 wurde die Mitteleuropäische Zeit für ganz Deutschland gesetzlich.", "With the railways, journeys became so fast that every local time caused confusion. Railways used a single time internally. In 1893 Central European Time became law for the whole of Germany."),
            t("In den USA war es ähnlich: Dort teilten die Eisenbahngesellschaften 1883 das Land selbst in Zeitzonen ein, lange bevor der Staat es regelte. Die Technik bestimmte also, wie spät es ist. Heute geben Atomuhren die Zeit vor, und Funk und Internet verteilen sie auf die Sekunde genau.", "It was similar in the USA: there the railway companies divided the country into time zones themselves in 1883, long before the government regulated it. So technology decided what time it was. Today atomic clocks set the time, and radio and the internet distribute it accurate to the second."),
        ),
        mc("hist.morse", Topic.History, M,
            t("Im Morsealphabet ist das E nur ein einziger kurzer Ton, das Q dagegen lang-lang-kurz-lang. Warum hat das E den kürzesten Code bekommen?", "In Morse code E is just one short signal, while Q is long-long-short-long. Why did E get the shortest code?"),
            t("Grund", "Reason"),
            t("Weil E der häufigste Buchstabe im Englischen ist und Nachrichten so schneller übertragen werden", "Because E is the most common letter in English, so messages are sent faster"),
            listOf(t("Weil E im Alphabet weit vorne steht", "Because E comes early in the alphabet"), t("Weil Samuel Morse ein E im Namen hat", "Because Samuel Morse has an E in his name"), t("Zufall, die Codes wurden ausgelost", "Chance, the codes were drawn by lot")),
            t("Kernidee: Jeder Ton kostet Zeit. Welche Buchstaben sollten deshalb die kürzesten Codes haben?", "Key idea: every signal costs time. So which letters should have the shortest codes?"),
            t("Das A steht im Alphabet vor dem E, hat aber einen längeren Code.", "A comes before E in the alphabet but has a longer code."),
            t("Morse und sein Mitarbeiter Alfred Vail zählten angeblich in einer Druckerei, wie viele Lettern von jedem Buchstaben im Setzkasten lagen. Häufige Buchstaben bekamen kurze Codes, seltene lange. So sinkt die mittlere Länge einer Nachricht.", "Morse and his collaborator Alfred Vail reportedly counted in a print shop how many pieces of type for each letter lay in the type cases. Common letters got short codes, rare ones long codes. That reduces the average length of a message."),
            t("Dieselbe Idee steckt heute in der Datenkompression, zum Beispiel in ZIP-Dateien: Häufige Zeichen werden kürzer gespeichert. 1844 schickte Morse die erste Fernnachricht von Washington nach Baltimore. Der Telegraf veränderte die Welt so stark wie später das Internet: Nachrichten brauchten plötzlich Minuten statt Wochen.", "The same idea is used in data compression today, for example in ZIP files: frequent characters are stored in shorter form. In 1844 Morse sent the first long-distance message from Washington to Baltimore. The telegraph changed the world as much as the internet later would: messages suddenly took minutes instead of weeks."),
        ),
        lab("hist.mayazahl", Topic.History, M, 0.55f) {
            val top = pick(1, 2, 3); val bars = pick(0, 1, 2, 3); val dots = pick(1, 2, 4)
            val low = 5 * bars + dots; val value = 20 * top + low
            task(
                question = l("Die Maya schrieben Zahlen im Zwanzigersystem: Ein Punkt bedeutet 1, ein Strich 5. Die untere Stufe zählt Einer, die Stufe darüber Zwanziger. Eine Zahl zeigt oben ${top} ${if (top == 1) "Punkt" else "Punkte"} und unten ${bars} ${if (bars == 1) "Strich" else "Striche"} und ${dots} ${if (dots == 1) "Punkt" else "Punkte"}. Welche Zahl ist das?", "The Maya wrote numbers in base twenty: a dot means 1, a bar 5. The lower level counts ones, the level above it twenties. A number shows ${top} dot${if (top == 1) "" else "s"} on top and, below, ${bars} bar${if (bars == 1) "" else "s"} and ${dots} dot${if (dots == 1) "" else "s"}. What number is it?"),
                steps = listOf(num(l("Zahl", "Number"), value.toDouble(), "", abs = 0.0, done = "$value",
                    hints = listOf(l("Kernidee: Erst jede Stufe für sich lesen, dann die obere mit 20 malnehmen.", "Key idea: first read each level on its own, then multiply the upper one by 20."), l("Unten: ${bars} · 5 + ${dots} = ${low}.", "Below: ${bars} · 5 + ${dots} = ${low}.")))),
                solution = listOf("""§${top} \cdot 20 + ${low} = ${value}§"""),
                background = l("Die Maya hatten ein echtes Stellenwertsystem mit einem eigenen Zeichen für Null, einer Muschel, und das schon Jahrhunderte, bevor sich die Null in Europa durchsetzte. Mit nur drei Zeichen ließ sich jede Zahl schreiben. Die Römer dagegen kannten keine Null, und mit ihren Zahlen schriftlich zu rechnen war mühsam. Dass wir im Zehnersystem zählen, liegt wohl an unseren Fingern, die Maya zählten vermutlich Finger und Zehen.", "The Maya had a true place-value system with its own symbol for zero, a shell, centuries before zero caught on in Europe. Any number could be written with just three symbols. The Romans, by contrast, had no zero, and doing written sums with their numerals was laborious. That we count in tens is probably due to our fingers; the Maya probably counted fingers and toes."),
            )
        },
        lab("hist.titanic", Topic.History, M, 0.5f) {
            val seats = 1178; val aboard = 2224; val pct = seats * 100.0 / aboard
            task(
                question = l("Die Titanic hatte 20 Rettungsboote mit zusammen ${n(seats)} Plätzen. An Bord waren bei der Jungfernfahrt 1912 etwa ${n(aboard)} Menschen. Für wie viel Prozent hätten die Boote höchstens gereicht?", "The Titanic had 20 lifeboats with a total of ${n(seats)} places. About ${n(aboard)} people were on board on the maiden voyage in 1912. For what percentage, at most, would the boats have been enough?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), pct, "%", abs = 0.5, done = """§\approx ${m(pct, 0)}\ \%§""",
                    hints = listOf(l("Kernidee: Plätze geteilt durch Menschen an Bord.", "Key idea: places divided by people on board."), """§\dfrac{${seats}}{${aboard}}§"""))),
                solution = listOf("""§\dfrac{${seats}}{${aboard}} \approx ${m(pct / 100, 2)} = ${m(pct, 0)}\ \%§"""),
                background = l("Das war damals legal: Die britischen Vorschriften richteten die Zahl der Boote nach der Größe des Schiffs, nicht nach den Menschen, und stammten aus einer Zeit kleinerer Schiffe. Viele Boote wurden zudem halb leer zu Wasser gelassen, nur gut 700 Menschen überlebten. Die Katastrophe führte 1914 zum ersten internationalen Vertrag zur Schiffssicherheit: Seitdem muss es Plätze für alle geben.", "This was legal at the time: British regulations based the number of boats on the size of the ship, not on the number of people, and dated from an era of smaller ships. In addition, many boats were lowered half empty, and only a little over 700 people survived. The disaster led in 1914 to the first international treaty on safety at sea: since then there have had to be places for everyone."),
            )
        },
        // ------------------------------------------------ schwer
        mc("hist.enigma", Topic.History, H,
            t("Im Zweiten Weltkrieg verschlüsselte die deutsche Wehrmacht ihre Funksprüche mit der Enigma-Maschine, die eine astronomisch große Zahl von Einstellungen hatte. Welche Eigenheit der Maschine half den Codeknackern um Alan Turing in Bletchley Park besonders?", "In the Second World War the German armed forces encrypted their radio messages with the Enigma machine, which had an astronomically large number of settings. Which quirk of the machine particularly helped the codebreakers around Alan Turing at Bletchley Park?"),
            t("Schwäche", "Weakness"),
            t("Kein Buchstabe wurde je in sich selbst verschlüsselt.", "No letter was ever encrypted as itself."),
            listOf(t("Die Maschine verschlüsselte jeden Buchstaben immer gleich.", "The machine always encrypted each letter the same way."), t("Die Schlüssel standen in jeder Nachricht im Klartext.", "The keys were in plain text in every message."), t("Die Enigma konnte nur Zahlen verschlüsseln.", "The Enigma could only encrypt numbers.")),
            t("Kernidee: Wenn man ein Wort vermutet, das in der Nachricht vorkommt, etwa „WETTERBERICHT“, wo darf es dann stehen und wo nicht?", "Key idea: if you suspect a word appears in the message, say 'WEATHER REPORT', where can it be and where can't it?"),
            t("Wenn an einer Stelle im Geheimtext ein W steht, kann dort im Klartext kein W gestanden haben.", "If there's a W at one point in the ciphertext, there can't have been a W there in the plain text."),
            t("Weil kein Buchstabe sich selbst ergab, ließen sich vermutete Wörter, sogenannte Cribs, an vielen Stellen sofort ausschließen. Das verkleinerte die Suche so stark, dass Turings elektromechanische Bomben die Tagesschlüssel finden konnten.", "Because no letter turned into itself, suspected words, so-called cribs, could immediately be ruled out at many positions. That shrank the search so much that Turing's electromechanical bombes could find the daily keys."),
            t("Den ersten Durchbruch hatten schon 1932 polnische Mathematiker um Marian Rejewski geschafft. Dazu kamen menschliche Fehler: immer gleiche Grußformeln und Wetterberichte. Die Lektion gilt bis heute: Ein Verfahren ist nur so sicher wie seine kleinste Regelmäßigkeit, und Verschlüsselung muss von der Mathematik her sicher sein, nicht nur kompliziert.", "Polish mathematicians led by Marian Rejewski had already achieved the first breakthrough in 1932. On top of that came human errors: always the same greetings and weather reports. The lesson holds to this day: a method is only as secure as its smallest regularity, and encryption has to be mathematically secure, not just complicated."),
        ),
        mc("hist.lebenserwartung", Topic.History, H,
            t("Im Mittelalter lag die Lebenserwartung bei Geburt bei etwa 30 Jahren. Heißt das, dass die meisten Menschen damals mit etwa 30 starben?", "In the Middle Ages life expectancy at birth was about 30 years. Does that mean most people died at around 30 back then?"),
            t("Antwort", "Answer"),
            t("Nein. Sehr viele Kinder starben früh und drückten den Durchschnitt. Wer die Kindheit überlebte, wurde oft 50 bis 70.", "No. A great many children died young and pulled the average down. Those who survived childhood often reached 50 to 70."),
            listOf(t("Ja, mit 30 galt man als alt und starb bald.", "Yes, at 30 you were considered old and soon died."), t("Ja, aber nur Bauern, Adlige wurden viel älter.", "Yes, but only peasants; nobles lived much longer."), t("Nein, die Zahl ist erfunden.", "No, the figure is made up.")),
            t("Kernidee: Ein Durchschnitt kann einen Wert ergeben, den fast niemand hat. Was passiert mit dem Mittelwert, wenn viele Werte nahe null liegen?", "Key idea: an average can produce a value that almost nobody has. What happens to the mean when many values are close to zero?"),
            t("Rechne: Stirbt die Hälfte der Kinder vor dem 5. Geburtstag und die andere Hälfte mit 60, wie hoch ist dann der Durchschnitt?", "Work it out: if half the children die before their 5th birthday and the other half at 60, what is the average?"),
            t("Stirbt die Hälfte im Kleinkindalter und die andere Hälfte mit 60, ergibt das einen Durchschnitt um 30. Die Zahl beschreibt also vor allem die hohe Kindersterblichkeit, nicht ein typisches Sterbealter.", "If half die as small children and the other half at 60, that gives an average of around 30. So the figure mainly describes the high child mortality, not a typical age of death."),
            t("Durchschnitte sind tückisch, wenn die Werte sehr ungleich verteilt sind. Der größte Fortschritt der Medizin in den letzten 200 Jahren war deshalb die Rettung von Kindern: durch sauberes Wasser, Impfungen und Hygiene. Für solche Fälle ist der Median oft aussagekräftiger, oder man fragt gezielt nach der Lebenserwartung mit 20 Jahren.", "Averages are treacherous when values are very unevenly distributed. So the greatest advance in medicine over the last 200 years has been saving children: through clean water, vaccinations and hygiene. For cases like this the median is often more meaningful, or you specifically ask about life expectancy at age 20."),
        ),
    )

    private val knowledge = listOf(
        lab("wissen.milliardesekunden", Topic.Knowledge, E, 0.35f) {
            val big = chance(0.5)
            val v = if (big) 1e9 / 3.156e7 else 1e6 / 86400
            task(
                question = l("Wie lange dauert es, bis eine ${if (big) "Milliarde" else "Million"} Sekunden vergangen sind? Gib die Antwort in ${if (big) "Jahren" else "Tagen"} an.", "How long does it take for a ${if (big) "billion" else "million"} seconds to pass? Give the answer in ${if (big) "years" else "days"}."),
                steps = listOf(num(if (big) l("Jahre", "Years") else l("Tage", "Days"), v, "", rel = 0.02, done = """§\approx ${m(v, 1)}§""",
                    hints = listOf(l("Kernidee: Ein Tag hat 86.400 Sekunden, ein Jahr gut 31,5 Millionen.", "Key idea: a day has 86,400 seconds, a year just over 31.5 million."), if (big) """§\dfrac{10^9}{3{,}156 \cdot 10^7}§""" else """§\dfrac{10^6}{86\,400}§"""))),
                solution = listOf(if (big) """§\dfrac{10^9\ \text{s}}{3{,}156 \cdot 10^7\ \text{s}} \approx ${m(v, 1)}§""" else """§\dfrac{10^6\ \text{s}}{86\,400\ \text{s}} \approx ${m(v, 1)}§"""),
                background = l("Eine Million Sekunden sind knapp zwölf Tage, eine Milliarde fast 32 Jahre. Das Wort klingt nur ein bisschen größer, die Zahl ist aber tausendmal so groß. Solche Vergleiche helfen bei Nachrichten: Eine Million Euro und eine Milliarde Euro unterscheiden sich wie zwölf Tage und ein halbes Menschenleben.", "A million seconds is just under twelve days, a billion almost 32 years. The word sounds only a little bigger, but the number is a thousand times as large. Comparisons like this help with the news: a million euros and a billion euros differ like twelve days and half a lifetime."),
            )
        },
        mc("wissen.billion", Topic.Knowledge, E,
            t("In einem englischen Text steht „one billion“. Wie viel ist das auf Deutsch?", "A German text says 'eine Billion'. How much is that in English?"),
            t("Bedeutung", "Meaning"),
            t("Eine Milliarde (10⁹)", "One trillion (10¹²)"),
            listOf(t("Eine Billion (10¹²)", "One billion (10⁹)"), t("Eine Million (10⁶)", "One million (10⁶)"), t("Eine Billiarde (10¹⁵)", "One quadrillion (10¹⁵)")),
            t("Kernidee: Die Wörter klingen gleich, aber Deutsch und Englisch zählen große Zahlen verschieden.", "Key idea: the words sound the same, but German and English count large numbers differently."),
            t("Deutsch: Million, Milliarde, Billion, Billiarde. Englisch: million, billion, trillion, quadrillion.", "German: Million, Milliarde, Billion, Billiarde. English: million, billion, trillion, quadrillion."),
            t("Im Deutschen folgt auf die Million die Milliarde (10⁹), erst dann die Billion (10¹²). Im Englischen ist „billion“ schon 10⁹. Das englische „billion“ ist also die deutsche Milliarde.", "In German the Million is followed by the Milliarde (10⁹), and only then the Billion (10¹²). In English 'billion' is already 10⁹. So the German 'Billion' is the English trillion."),
            t("Das ist die lange und die kurze Skala. Großbritannien benutzte früher auch die lange Skala und wechselte 1974 offiziell zur amerikanischen. Übersetzungsfehler dabei sind häufig, selbst in Zeitungen, und machen aus Staatsschulden schnell das Tausendfache. Wissenschaftler umgehen das Problem mit Zehnerpotenzen.", "These are the long and the short scale. Britain also used to use the long scale and officially switched to the American one in 1974. Translation errors are common, even in newspapers, and can quickly make national debts a thousand times larger. Scientists avoid the problem with powers of ten."),
        ),
        lab("wissen.fussballfelder", Topic.Knowledge, E, 0.35f) {
            val area = 105.0 * 68; val nn = 1e6 / area
            task(
                question = l("Ein Fußballfeld ist 105 m lang und 68 m breit. Wie viele Fußballfelder passen auf einen Quadratkilometer?", "A football pitch is 105 m long and 68 m wide. How many football pitches fit on one square kilometre?"),
                steps = listOf(num(l("Anzahl", "Number"), nn, "", rel = 0.03, done = """§\approx ${m(nn, 0)}§""",
                    hints = listOf(l("Kernidee: Ein Quadratkilometer ist 1000 m × 1000 m = 1.000.000 m².", "Key idea: a square kilometre is 1000 m × 1000 m = 1,000,000 m²."), """§\dfrac{1\,000\,000}{105 \cdot 68}§"""))),
                solution = listOf("""§\dfrac{1\,000\,000\ \text{m}^2}{${m(area, 0)}\ \text{m}^2} \approx ${m(nn, 0)}§"""),
                background = l("Fußballfelder sind ein beliebtes Maß in Nachrichten, etwa für abgeholzten Regenwald. Hilfreicher ist oft der Hektar: 100 m × 100 m, das ist ungefähr anderthalb Fußballfelder. Ein Quadratkilometer hat 100 Hektar. Deutschland hat rund 357.000 km², das wären 50 Millionen Fußballfelder.", "Football pitches are a popular unit in the news, for instance for cleared rainforest. Often more helpful is the hectare: 100 m × 100 m, which is about one and a half football pitches. A square kilometre has 100 hectares. Germany has around 357,000 km², which would be 50 million football pitches."),
            )
        },
        mc("wissen.bevoelkerung1800", Topic.Knowledge, E,
            t("Heute leben über 8 Milliarden Menschen auf der Erde. Wie viele waren es um das Jahr 1800?", "Today over 8 billion people live on Earth. How many were there around the year 1800?"),
            t("Weltbevölkerung um 1800", "World population around 1800"),
            t("Etwa 1 Milliarde", "About 1 billion"),
            listOf(t("Etwa 100 Millionen", "About 100 million"), t("Etwa 4 Milliarden", "About 4 billion"), t("Etwa 6 Milliarden", "About 6 billion")),
            t("Kernidee: Die Bevölkerung ist erst mit Medizin, Hygiene und besserer Landwirtschaft explodiert, und das vor allem im 20. Jahrhundert.", "Key idea: the population only exploded with medicine, hygiene and better farming, mainly in the 20th century."),
            t("Die zweite Milliarde wurde um 1927 erreicht.", "The second billion was reached around 1927."),
            t("Um 1804 lebte erstmals eine Milliarde Menschen. Für die zweite brauchte es 123 Jahre, für die achte nur noch 11.", "Around 1804 there were a billion people for the first time. The second took 123 years, the eighth only 11."),
            t("Über fast die ganze Menschheitsgeschichte wuchs die Bevölkerung kaum, weil Hunger und Krankheiten fast so viele Menschen töteten, wie geboren wurden. Seit etwa 1960 sinkt die Wachstumsrate wieder, weil in vielen Ländern weniger Kinder geboren werden. Die Vereinten Nationen erwarten einen Höhepunkt bei gut 10 Milliarden in der zweiten Hälfte dieses Jahrhunderts.", "For almost all of human history the population hardly grew, because hunger and disease killed almost as many people as were born. Since about 1960 the growth rate has been falling again, because fewer children are born in many countries. The United Nations expects a peak of a good 10 billion in the second half of this century."),
        ),
        lab("wissen.menschenquadrat", Topic.Knowledge, M, 0.5f) {
            val people = 8.1e9; val dens = pick(4.0, 2.0); val km2 = people / dens / 1e6; val side = sqrt(km2)
            task(
                question = l("Alle ${n(8.1, 1)} Milliarden Menschen stellen sich dicht an dicht auf einen quadratischen Platz, ${n(dens, 0)} Personen pro Quadratmeter. Wie lang muss eine Seite des Platzes sein?", "All ${n(8.1, 1)} billion people stand close together on a square, ${n(dens, 0)} people per square metre. How long must one side of the square be?"),
                steps = listOf(num(l("Seitenlänge in km", "Side length in km"), side, "km", rel = 0.03, done = """§\approx ${m(side, 0)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Erst die Fläche ausrechnen, dann die Wurzel ziehen. 1 km² = 1.000.000 m².", "Key idea: first work out the area, then take the square root. 1 km² = 1,000,000 m²."), """§A = \dfrac{8{,}1 \cdot 10^9}{${m(dens, 0)}}\ \text{m}^2§"""))),
                solution = listOf("""§A \approx ${m(km2, 0)}\ \text{km}^2,\quad s = \sqrt{${m(km2, 0)}} \approx ${m(side, 0)}\ \text{km}§"""),
                background = l("Die ganze Menschheit passt auf eine Fläche von ${n(km2, 0)} km², ${if (dens >= 4.0) "etwas kleiner als das Saarland" else "etwa anderthalbmal so groß wie das Saarland"}. Das Gefühl, die Erde sei voll, entsteht nicht durch Platzmangel zum Stehen, sondern durch das, was jeder Mensch zum Leben braucht: Ackerland, Wasser, Energie und Wälder, die das CO₂ binden. Dafür braucht es pro Kopf Tausende Quadratmeter.", "All of humanity fits on an area of ${n(km2, 0)} km², ${if (dens >= 4.0) "about 1.3 times the area of Greater London" else "about 2.6 times the area of Greater London"}. The feeling that the Earth is full doesn't come from a lack of room to stand, but from what every person needs to live: farmland, water, energy and forests that absorb CO₂. For that it takes thousands of square metres per person."),
            )
        },
        lab("wissen.tropfhahn", Topic.Knowledge, M, 0.45f) {
            val rate = pick(1.0, 2.0, 0.5); val perL = 20_000.0; val litres = rate * 86400 * 365 / perL
            task(
                question = l("Ein Wasserhahn tropft ${if (rate == 1.0) "einmal" else if (rate == 2.0) "zweimal" else "alle zwei Sekunden einmal"}${if (rate == 0.5) "" else " pro Sekunde"}. Etwa 20 Tropfen ergeben einen Milliliter. Wie viel Liter Wasser gehen so in einem Jahr verloren?", "A tap drips ${if (rate == 1.0) "once a second" else if (rate == 2.0) "twice a second" else "once every two seconds"}. About 20 drops make one millilitre. How many litres of water are lost like this in a year?"),
                steps = listOf(num(l("Liter pro Jahr", "Litres per year"), litres, "l", rel = 0.03, done = """§\approx ${m(litres, 0)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Zähle die Tropfen in einem Jahr und teile durch 20.000 Tropfen pro Liter.", "Key idea: count the drops in a year and divide by 20,000 drops per litre."), """§${m(rate, 1)} \cdot 86\,400 \cdot 365§"""))),
                solution = listOf("""§\dfrac{${m(rate * 86400 * 365, 0)}}{20\,000} \approx ${m(litres, 0)}\ \text{l}§"""),
                background = l("Ein einzelner Tropfen ist nichts, aber eine Sekunde kommt im Jahr über 31 Millionen Mal vor. Solche Rechnungen nennt man Fermi-Abschätzungen: Man zerlegt eine Frage in Schritte, die man grob schätzen kann. In Deutschland verbraucht jeder Mensch im Haushalt etwa 125 Liter am Tag, der tropfende Hahn verschwendet also mehrere Tage davon.", "A single drop is nothing, but a second occurs over 31 million times a year. Calculations like this are called Fermi estimates: you break a question down into steps that you can roughly estimate. In Germany each person uses about 125 litres a day at home, so the dripping tap wastes several days' worth of that."),
            )
        },
        lab("wissen.binaerfinger", Topic.Knowledge, M, 0.5f) {
            val fingers = pick(10, 5, 8); val max = (1 shl fingers) - 1
            task(
                question = l("Normalerweise zählst du mit ${fingers} Fingern bis ${fingers}. Wenn jeder Finger aber eine Stelle einer Binärzahl ist, ausgestreckt 1, eingeklappt 0: Bis zu welcher Zahl kannst du dann zählen?", "Normally you can count to ${fingers} with ${fingers} fingers. But if each finger is one digit of a binary number, raised for 1 and folded for 0, up to which number can you then count?"),
                steps = listOf(num(l("Größte Zahl", "Largest number"), max.toDouble(), "", abs = 0.0, done = "$max",
                    hints = listOf(l("Kernidee: Der erste Finger zählt 1, der zweite 2, der dritte 4, jeder nächste doppelt so viel.", "Key idea: the first finger is worth 1, the second 2, the third 4, each one after that twice as much."), """§1 + 2 + 4 + \dots + 2^{${fingers - 1}} = 2^{${fingers}} - 1§"""))),
                solution = listOf("""§2^{${fingers}} - 1 = ${max}§"""),
                background = l("Mit ${fingers} Fingern gibt es ${n(max + 1.0, 0)} Muster, und jedes steht für eine andere Zahl, die Null mitgezählt. Computer rechnen genau so, mit Bits, die nur an oder aus sein können. Acht Bits sind ein Byte und können 256 verschiedene Werte speichern, deshalb tauchen Zahlen wie 255 und 1024 in der Technik ständig auf.", "With ${fingers} fingers there are ${n(max + 1.0, 0)} patterns, and each stands for a different number, counting zero. Computers calculate exactly like this, with bits that can only be on or off. Eight bits make a byte and can store 256 different values, which is why numbers like 255 and 1024 keep turning up in technology."),
            )
        },
        lab("wissen.buecherspeicher", Topic.Knowledge, M, 0.5f) {
            val pages = pick(400, 300, 250); val chars = 2000; val bytes = pages * chars.toDouble(); val books = 1e9 / bytes
            task(
                question = l("Ein Roman hat ${pages} Seiten mit je etwa ${n(chars)} Zeichen. Jedes Zeichen braucht als einfacher Text ein Byte. Wie viele solcher Romane passen auf einen Speicher von 1 GB (eine Milliarde Byte)?", "A novel has ${pages} pages with about ${n(chars)} characters each. Each character takes one byte as plain text. How many such novels fit on 1 GB of storage (one billion bytes)?"),
                steps = listOf(num(l("Anzahl Romane", "Number of novels"), books, "", rel = 0.03, done = """§\approx ${m(books, 0)}§""",
                    hints = listOf(l("Kernidee: Erst ausrechnen, wie viele Byte ein Roman hat.", "Key idea: first work out how many bytes one novel takes."), """§${pages} \cdot ${chars} = ${m(bytes, 0)}\ \text{Byte}§"""))),
                solution = listOf("""§\dfrac{10^9}{${m(bytes, 0)}} \approx ${m(books, 0)}§"""),
                background = l("Ein Handy mit 128 GB könnte so über hunderttausend Romane speichern, mehr als eine große Stadtbibliothek. Text ist winzig. Speicherplatz fressen Fotos, Musik und vor allem Videos: Eine Minute Film in hoher Auflösung braucht oft mehr Platz als hundert Romane. Die englische Wikipedia passt als reiner Text, komprimiert und ohne Bilder, in rund 25 GB.", "A phone with 128 GB could store over a hundred thousand novels this way, more than a large city library. Text is tiny. What eats up storage is photos, music and above all videos: one minute of high-resolution film often takes more space than a hundred novels. The English Wikipedia as plain text, compressed and without images, fits into around 25 GB."),
            )
        },
        lab("wissen.halbton", Topic.Knowledge, M, 0.6f) {
            val (k, name) = pick(1 to l("das B (ein Halbton höher)", "A♯ (one semitone higher)"), 3 to l("das C darüber (drei Halbtöne höher)", "the C above (three semitones higher)"), 7 to l("das E darüber (sieben Halbtöne höher, eine Quinte)", "the E above (seven semitones higher, a fifth)"))
            val f = 440 * 2.0.pow(k / 12.0)
            task(
                question = l("Eine Oktave verdoppelt die Frequenz und besteht aus 12 gleichen Halbtonschritten. Der Kammerton A hat 440 Hz. Welche Frequenz hat $name?", "An octave doubles the frequency and consists of 12 equal semitone steps. Concert A has 440 Hz. What is the frequency of $name?"),
                steps = listOf(num(l("Frequenz in Hz", "Frequency in Hz"), f, "Hz", rel = 0.01, done = """§\approx ${m(f, 1)}\ \text{Hz}§""",
                    hints = listOf(l("Kernidee: Gleiche Schritte heißt: Jeder Halbton multipliziert die Frequenz mit demselben Faktor. Zwölfmal angewendet ergibt er 2.", "Key idea: equal steps means every semitone multiplies the frequency by the same factor. Applied twelve times it gives 2."), """§q = 2^{1/12} \approx 1{,}0595§"""))),
                solution = listOf("""§f = 440 \cdot 2^{${k}/12} \approx ${m(f, 1)}\ \text{Hz}§"""),
                background = l("Diese gleichstufige Stimmung setzte sich im 18. und 19. Jahrhundert durch. Sie hat einen Haken: Die reinen Intervalle der Natur, etwa die Quinte mit dem Verhältnis 3 : 2, werden leicht verfälscht, 2^(7/12) ist 1,498 statt 1,5. Dafür klingt jede Tonart gleich gut, und man kann frei zwischen ihnen wechseln. Ohne diesen Kompromiss wäre ein Klavier in vielen Tonarten unbrauchbar.", "This equal temperament became established in the 18th and 19th centuries. It has a catch: nature's pure intervals, such as the fifth with the ratio 3 : 2, are slightly distorted; 2^(7/12) is 1.498 instead of 1.5. In return every key sounds equally good, and you can move freely between them. Without this compromise a piano would be unusable in many keys."),
            )
        },
        mc("wissen.marianengraben", Topic.Knowledge, E,
            t("Der Mount Everest ist 8849 m hoch, der Marianengraben im Pazifik ist an seiner tiefsten Stelle etwa 10.900 m tief. Würde der Everest dort ganz im Wasser verschwinden?", "Mount Everest is 8849 m high; the Mariana Trench in the Pacific is about 10,900 m deep at its deepest point. Would Everest disappear completely under water there?"),
            t("Antwort", "Answer"),
            t("Ja, über seinem Gipfel stünden noch rund 2 km Wasser.", "Yes, there would still be about 2 km of water above its summit."),
            listOf(t("Nein, der Gipfel würde 2 km herausragen.", "No, the summit would stick out by 2 km."), t("Gerade so, der Gipfel wäre genau an der Oberfläche.", "Only just; the summit would be exactly at the surface."), t("Das lässt sich nicht sagen, weil Wasser den Berg anhebt.", "It's impossible to say, because the water lifts the mountain.")),
            t("Kernidee: Vergleiche die beiden Zahlen.", "Key idea: compare the two numbers."),
            t("10.900 − 8849 = ?", "10,900 − 8849 = ?"),
            t("10.900 m − 8849 m ≈ 2050 m. Der Gipfel läge also etwa zwei Kilometer unter der Meeresoberfläche.", "10,900 m − 8849 m ≈ 2050 m. So the summit would be about two kilometres below the surface of the sea."),
            t("Am Grund des Grabens drückt das Wasser mit über 1000 bar, wie ein Kleinwagen auf jedem Daumennagel. Lange Zeit waren mehr Menschen auf dem Mond gewesen als an der tiefsten Stelle des Ozeans: 1960 erreichten Jacques Piccard und Don Walsh den Grund zum ersten Mal. Gemessen vom Erdmittelpunkt ist übrigens der Chimborazo in Ecuador der höchste Gipfel, weil die Erde am Äquator dicker ist.", "At the bottom of the trench the water presses with over 1000 bar, like a small car on every thumbnail. For a long time more people had stood on the Moon than had reached the deepest point of the ocean: in 1960 Jacques Piccard and Don Walsh reached the bottom for the first time. Measured from the centre of the Earth, by the way, Chimborazo in Ecuador is the highest summit, because the Earth bulges at the equator."),
        ),
        lab("wissen.oelfilm", Topic.Knowledge, H, 0.65f) {
            val ml = 5.0; val area = pick(2000.0, 1000.0); val nm = ml * 1e-6 / area * 1e9
            task(
                question = l("Benjamin Franklin goss 1773 einen Teelöffel Öl (etwa ${n(ml, 0)} ml) auf einen Teich. Es breitete sich zu einem hauchdünnen Film von ungefähr ${n(area, 0)} m² aus und hörte dann auf. Wie dick war der Film, in Nanometern (1 nm = 10⁻⁹ m)?", "In 1773 Benjamin Franklin poured a teaspoon of oil (about ${n(ml, 0)} ml) on a pond. It spread into a wafer-thin film of roughly ${n(area, 0)} m² and then stopped. How thick was the film, in nanometres (1 nm = 10⁻⁹ m)?"),
                steps = listOf(num(l("Dicke in nm", "Thickness in nm"), nm, "nm", rel = 0.03, done = """§\approx ${m(nm, 1)}\ \text{nm}§""",
                    hints = listOf(l("Kernidee: Volumen ist Fläche mal Dicke. Rechne die Milliliter in Kubikmeter um: 1 ml = 10⁻⁶ m³.", "Key idea: volume is area times thickness. Convert the millilitres into cubic metres: 1 ml = 10⁻⁶ m³."), """§d = \dfrac{V}{A} = \dfrac{${m(ml, 0)} \cdot 10^{-6}\ \text{m}^3}{${m(area, 0)}\ \text{m}^2}§"""))),
                solution = listOf("""§d = ${sci(nm * 1e-9)}\ \text{m} \approx ${m(nm, 1)}\ \text{nm}§"""),
                background = l("Franklin rechnete das nicht aus, aber gut hundert Jahre später taten es Lord Rayleigh und Agnes Pockels, die in ihrer Küche mit Wasserschalen experimentierte. Der Film hört auf, sich auszubreiten, wenn er nur noch eine Molekülschicht dick ist. So bestimmte man erstmals ungefähr, wie groß ein Molekül ist, mit nichts als Öl, Wasser und einem Maßband.", "Franklin didn't work it out, but a good hundred years later Lord Rayleigh did, as did Agnes Pockels, who experimented with bowls of water in her kitchen. The film stops spreading when it's only one layer of molecules thick. This gave the first rough measurement of the size of a molecule, with nothing but oil, water and a tape measure."),
            )
        },
        mc("wissen.sterneodersand", Topic.Knowledge, H,
            t("Gibt es mehr Sterne im beobachtbaren Universum oder mehr Sandkörner an allen Stränden der Erde zusammen?", "Are there more stars in the observable universe, or more grains of sand on all the Earth's beaches put together?"),
            t("Antwort", "Answer"),
            t("Mehr Sterne, wohl mindestens tausendmal so viele", "More stars, probably at least a thousand times as many"),
            listOf(t("Mehr Sandkörner", "More grains of sand"), t("Ungefähr gleich viele", "About the same number"), t("Das lässt sich nicht einmal grob schätzen.", "It can't even be roughly estimated.")),
            t("Kernidee: Schätze Größenordnungen. Wie viele Galaxien gibt es, und wie viele Sterne hat jede?", "Key idea: estimate orders of magnitude. How many galaxies are there, and how many stars does each have?"),
            t("Grob: hunderte Milliarden Galaxien mit je hunderten Milliarden Sternen. Sand: Strände der Welt etwa 10¹⁸ bis 10¹⁹ Körner.", "Roughly: hundreds of billions of galaxies with hundreds of billions of stars each. Sand: the world's beaches have about 10¹⁸ to 10¹⁹ grains."),
            t("Sterne: etwa 10¹¹ Galaxien mal 10¹¹ Sterne ergibt rund 10²² oder mehr. Sandkörner an Stränden: etwa 10¹⁹. Die Sterne gewinnen deutlich.", "Stars: about 10¹¹ galaxies times 10¹¹ stars gives around 10²² or more. Grains of sand on beaches: about 10¹⁹. The stars win clearly."),
            t("Beide Zahlen sind nur grobe Schätzungen, aber der Abstand ist so groß, dass das Ergebnis feststeht. Schon Archimedes schätzte in seiner „Sandrechnung“, wie viele Sandkörner ins ganze Weltall passen würden, und erfand dafür eine Schreibweise für riesige Zahlen. Kurios: In nur zehn Tropfen Wasser stecken etwa so viele Moleküle, wie es Sterne gibt.", "Both figures are only rough estimates, but the gap is so large that the result is clear. Archimedes, in his 'Sand Reckoner', already estimated how many grains of sand would fit into the whole universe, and invented a way of writing huge numbers to do it. Curiously, just ten drops of water contain about as many molecules as there are stars."),
        ),
    )

    private val funFacts = listOf(
        tf("fakt.goldfisch", Topic.FunFacts, E, false,
            t("Goldfische haben ein Gedächtnis von nur drei Sekunden.", "Goldfish have a memory of only three seconds."),
            t("Sie können sich monatelang an Dinge erinnern.", "They can remember things for months."),
            t("In Versuchen lernen Goldfische, auf bestimmte Töne zur Fütterung zu schwimmen oder Hebel zu drücken, und behalten das über Monate. Sie erkennen sogar Menschen, die sie regelmäßig füttern. Woher der Mythos stammt, weiß niemand genau. Vielleicht beruhigt er Menschen, die Fische in kleine Gläser setzen.", "In experiments goldfish learn to swim to particular sounds for feeding or to press levers, and remember it for months. They even recognise people who feed them regularly. Nobody knows exactly where the myth comes from. Perhaps it reassures people who keep fish in small bowls."),
            t("Kann man Fische dressieren?", "Can fish be trained?")),
        tf("fakt.chinamauer", Topic.FunFacts, E, false,
            t("Die Chinesische Mauer ist das einzige Bauwerk, das man mit bloßem Auge aus dem Weltall sieht.", "The Great Wall of China is the only structure you can see from space with the naked eye."),
            t("Aus dem Orbit ist sie mit bloßem Auge praktisch unsichtbar.", "From orbit it's practically invisible to the naked eye."),
            t("Die Mauer ist zwar sehr lang, aber meist nur wenige Meter breit und hat ungefähr die Farbe der Umgebung. Aus 400 km Höhe ist das, als wollte man ein Haar aus über drei Kilometern Entfernung sehen. Chinas erster Raumfahrer Yang Liwei sagte 2003, er habe sie nicht gesehen. Große Städte, Flughäfen und Autobahnen erkennt man dagegen gut, nachts vor allem durch ihre Lichter.", "The wall is very long, but mostly only a few metres wide and roughly the colour of its surroundings. From 400 km up that's like trying to see a hair from over three kilometres away. China's first astronaut, Yang Liwei, said in 2003 that he hadn't seen it. Large cities, airports and motorways, on the other hand, are easy to make out, especially at night by their lights."),
            t("Entscheidend ist die Breite, nicht die Länge.", "What matters is the width, not the length.")),
        tf("fakt.blitzzweimal", Topic.FunFacts, M, false,
            t("Der Blitz schlägt nie zweimal an derselben Stelle ein.", "Lightning never strikes the same place twice."),
            t("Hohe Punkte werden sogar immer wieder getroffen.", "High points are actually hit again and again."),
            t("Ein Blitz sucht sich den kürzesten Weg zum Boden, und das ist oft ein hohes, leitendes Objekt. Das Empire State Building in New York wird jedes Jahr etwa 20- bis 25-mal getroffen, der Berliner Fernsehturm ebenfalls mehrmals. Dafür gibt es Blitzableiter: Sie bieten dem Blitz einen sicheren Weg in die Erde.", "Lightning takes the shortest path to the ground, and that's often a tall, conducting object. The Empire State Building in New York is hit about 20 to 25 times a year, and Berlin's TV tower several times too. That's what lightning conductors are for: they give the lightning a safe path into the earth."),
            t("Was macht eine Stelle für Blitze attraktiv?", "What makes a place attractive to lightning?")),
        tf("fakt.hundejahre", Topic.FunFacts, M, false,
            t("Ein Hundejahr entspricht genau sieben Menschenjahren.", "One dog year equals exactly seven human years."),
            t("Hunde altern am Anfang viel schneller und später langsamer.", "Dogs age much faster at first and more slowly later."),
            t("Ein einjähriger Hund ist schon geschlechtsreif und ausgewachsen, entspricht also eher einem 15-jährigen Menschen. Danach kommen pro Jahr nur noch etwa vier bis fünf Menschenjahre dazu. Außerdem altern große Rassen schneller als kleine: Eine Dogge ist mit acht Jahren alt, ein Chihuahua wird oft fünfzehn.", "A one-year-old dog is already sexually mature and fully grown, so it's more like a 15-year-old human. After that only about four to five human years are added per year. Large breeds also age faster than small ones: a Great Dane is old at eight, while a chihuahua often reaches fifteen."),
            t("Kann ein einjähriger Hund schon Junge bekommen?", "Can a one-year-old dog already have puppies?")),
        tf("fakt.bananebeere", Topic.FunFacts, M, true,
            t("Botanisch gesehen ist die Banane eine Beere, die Erdbeere aber nicht.", "Botanically, a banana is a berry, but a strawberry isn't."),
            t("Die Banane entsteht wie eine Beere aus einem einzigen Fruchtknoten mit Samen im Fleisch.", "The banana develops like a berry from a single ovary with seeds in the flesh."),
            t("Botaniker nennen eine Frucht Beere, wenn sie aus einem Fruchtknoten entsteht und die Samen im weichen Fruchtfleisch liegen. Das trifft auf Bananen, Tomaten, Gurken und Kiwis zu. Bei der Erdbeere sitzen die eigentlichen Früchte, kleine Nüsschen, außen auf einem verdickten Blütenboden. Die Wissenschaft und die Küche benutzen Wörter eben verschieden.", "Botanists call a fruit a berry if it develops from one ovary and the seeds lie in the soft flesh. That applies to bananas, tomatoes, cucumbers and kiwis. In the strawberry the actual fruits, tiny nutlets, sit on the outside of a swollen flower base. Science and the kitchen simply use words differently."),
            t("Wo sitzen bei der Erdbeere die Samen?", "Where are the seeds on a strawberry?")),
        tf("fakt.krake", Topic.FunFacts, M, true,
            t("Kraken haben drei Herzen und blaues Blut.", "Octopuses have three hearts and blue blood."),
            t("Zwei Herzen pumpen Blut durch die Kiemen, eins durch den Körper.", "Two hearts pump blood through the gills, one through the body."),
            t("Das Blut der Kraken enthält statt Eisen das kupferhaltige Hämocyanin, das mit Sauerstoff blau wird. Es transportiert im kalten Meerwasser gut Sauerstoff, aber weniger als unser Blut, deshalb brauchen sie die Extra-Herzen. Kraken haben außerdem rund zwei Drittel ihrer Nervenzellen in den Armen, die dadurch fast selbstständig tasten und greifen.", "Instead of iron, octopus blood contains copper-based haemocyanin, which turns blue with oxygen. It carries oxygen well in cold seawater, but less than our blood, which is why they need the extra hearts. Octopuses also have around two thirds of their nerve cells in their arms, which can therefore feel and grip almost on their own."),
            t("Blaue Farbe kommt von einem anderen Metall als Eisen.", "The blue colour comes from a metal other than iron.")),
        tf("fakt.zungenkarte", Topic.FunFacts, E, false,
            t("Süß schmeckt man an der Zungenspitze, bitter nur ganz hinten auf der Zunge.", "You taste sweetness at the tip of the tongue and bitterness only right at the back."),
            t("Alle Geschmacksrichtungen nimmt man überall auf der Zunge wahr.", "You perceive every taste all over the tongue."),
            t("Die berühmte Zungenkarte beruht auf einem Missverständnis: Ein deutscher Forscher maß um 1900 kleine Unterschiede in der Empfindlichkeit, und später wurde daraus eine Karte mit scharfen Zonen. Tatsächlich hat jede Geschmacksknospe Sinneszellen für süß, sauer, salzig, bitter und umami. Probier es aus: Zucker auf dem hinteren Zungenrand schmeckt genauso süß.", "The famous tongue map is based on a misunderstanding: around 1900 a German researcher measured small differences in sensitivity, and later this was turned into a map with sharp zones. In fact every taste bud has sensory cells for sweet, sour, salty, bitter and umami. Try it: sugar on the back edge of your tongue tastes just as sweet."),
            t("Probier es mit einem Zuckerkorn aus.", "Try it with a grain of sugar.")),
        tf("fakt.zehnprozent", Topic.FunFacts, E, false,
            t("Wir nutzen nur zehn Prozent unseres Gehirns.", "We only use ten percent of our brain."),
            t("Hirnscans zeigen, dass alle Bereiche arbeiten, nur nicht alle gleichzeitig.", "Brain scans show that all areas work, just not all at once."),
            t("Das Gehirn macht nur 2 % der Körpermasse aus, verbraucht aber 20 % der Energie. Kein Körper würde sich ein Organ leisten, das zu 90 % brachliegt. Schon kleine Schäden fast überall im Gehirn haben deutliche Folgen. Was stimmt: Zu jedem Zeitpunkt ist nur ein Teil der Nervenzellen stark aktiv, und das ist auch gut so.", "The brain makes up only 2% of body mass but uses 20% of the energy. No body would afford an organ that lies 90% idle. Even small damage almost anywhere in the brain has clear effects. What is true: at any one time only some of the nerve cells are highly active, and that's a good thing."),
            t("Wie viel Energie verbraucht das Gehirn?", "How much energy does the brain use?")),
        tf("fakt.haibaum", Topic.FunFacts, H, true,
            t("Haie gibt es schon länger als Bäume.", "Sharks have existed for longer than trees."),
            t("Die ältesten Haispuren sind rund 450 Millionen Jahre alt, die ersten Bäume rund 385 Millionen.", "The oldest traces of sharks are around 450 million years old, the first trees around 385 million."),
            t("Frühe Haie schwammen schon durch die Meere, als das Land noch fast kahl war und nur niedrige Pflanzen und Moose kannte. Bäume mit Holz entwickelten sich erst gut 60 Millionen Jahre später. Haie haben seitdem mehrere große Massenaussterben überlebt, auch das der Dinosaurier.", "Early sharks were already swimming through the seas when the land was still almost bare, with only low plants and mosses. Trees with wood only evolved a good 60 million years later. Sharks have since survived several great mass extinctions, including the one that wiped out the dinosaurs."),
            t("Das Leben begann im Meer, das Land wurde erst später besiedelt.", "Life began in the sea; the land was only colonised later.")),
        tf("fakt.venustag", Topic.FunFacts, H, true,
            t("Die Venus braucht für eine Drehung um sich selbst länger als für einen Umlauf um die Sonne.", "Venus takes longer to turn once on its axis than to go once around the Sun."),
            t("Eine Drehung dauert 243 Erdtage, ein Umlauf nur 225.", "One rotation takes 243 Earth days, one orbit only 225."),
            t("Die Venus dreht sich extrem langsam und noch dazu rückwärts, verglichen mit fast allen anderen Planeten. Dort ginge die Sonne im Westen auf. Weil Drehung und Umlauf gegeneinander laufen, dauert ein Tag von Sonnenaufgang zu Sonnenaufgang trotzdem nur etwa 117 Erdtage. Warum sie so seltsam rotiert, ist nicht sicher geklärt, vielleicht nach einem frühen Zusammenstoß.", "Venus spins extremely slowly, and backwards too, compared with almost all the other planets. There the Sun would rise in the west. Because rotation and orbit run against each other, a day from sunrise to sunrise still only lasts about 117 Earth days. Why it rotates so strangely isn't certain; perhaps after an early collision."),
            t("Die Venus ist der langsamste Dreher im Sonnensystem.", "Venus is the slowest spinner in the solar system.")),
        tf("fakt.wolkengewicht", Topic.FunFacts, M, true,
            t("Eine gewöhnliche Schönwetterwolke wiegt mehrere Hundert Tonnen.", "An ordinary fair-weather cloud weighs several hundred tonnes."),
            t("Ein Kubikkilometer Wolke enthält etwa 500 t Wasser.", "A cubic kilometre of cloud contains about 500 t of water."),
            t("In jedem Kubikmeter Wolke schweben etwa 0,5 g Wasser in winzigen Tröpfchen, und eine Haufenwolke kann einen Kubikkilometer groß sein: eine Milliarde Kubikmeter. Sie fällt nicht herunter, weil die Tröpfchen so klein sind, dass schon schwache Aufwinde sie tragen. Die Luft in der Wolke wiegt übrigens noch viel mehr als das Wasser.", "Every cubic metre of cloud holds about 0.5 g of water in tiny droplets, and a heap cloud can be a cubic kilometre in size: a billion cubic metres. It doesn't fall because the droplets are so small that even weak updrafts carry them. The air in the cloud, by the way, weighs much more than the water."),
            t("Rechne: 0,5 g pro Kubikmeter mal eine Milliarde Kubikmeter.", "Work it out: 0.5 g per cubic metre times a billion cubic metres.")),
        tf("fakt.oxford", Topic.FunFacts, H, true,
            t("Die Universität Oxford ist älter als das Reich der Azteken.", "The University of Oxford is older than the Aztec Empire."),
            t("In Oxford wurde schon ab 1096 gelehrt, die Aztekenhauptstadt Tenochtitlan entstand erst um 1325.", "Teaching was going on in Oxford from 1096; the Aztec capital Tenochtitlan was only founded around 1325."),
            t("Unser Bild der Geschichte ordnet „alte Kulturen“ und „Neuzeit“ oft falsch ein. Das Aztekenreich war vergleichsweise jung, als die Spanier es 1521 eroberten: Seine Hauptstadt war knapp 200 Jahre alt, das Reich selbst noch keine 100. Ähnlich überraschend: Kleopatra lebte zeitlich näher an der Mondlandung als am Bau der Cheops-Pyramide.", "Our picture of history often misplaces 'ancient cultures' and 'modern times'. The Aztec Empire was comparatively young when the Spanish conquered it in 1521: its capital was barely 200 years old, the empire itself not yet 100. Just as surprising: Cleopatra lived closer in time to the Moon landing than to the building of the Great Pyramid."),
            t("Wann eroberten die Spanier Mexiko, und wie alt war das Reich da?", "When did the Spanish conquer Mexico, and how old was the empire then?")),
    )
}
