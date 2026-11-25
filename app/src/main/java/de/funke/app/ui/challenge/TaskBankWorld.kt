package de.funke.app.ui.challenge

import kotlin.math.log2
import kotlin.math.pow

// =============================================================================================
// Aufgabenbank Welt: Geschichte, Geologie, Philosophie, Erdkunde, Biologie, Chemie, Allgemeinwissen.
// Gleiche Regeln wie in TaskBank: § steht für $, die erste Auswahl-Option ist die richtige,
// Ergebnisse werden im Code berechnet. Schätzungen: Dauer in Sekunden, Länge in Metern, Masse in kg.
// =============================================================================================

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard

private const val DAY = 86_400.0
private const val YEAR = 365.25 * DAY

object TaskBankWorld {
    val all: List<TaskDef> = listOf(
        // ======================================= GESCHICHTE =======================================
        TaskDef("pyramide", Topic.History, M) {
            val perDay = 2.3e6 / (20 * 365)
            Task(
                "pyramide", Topic.History, M,
                question = l("Die Cheops-Pyramide besteht aus rund 2,3 Millionen Steinblöcken. Gebaut wurde sie vermutlich in etwa 20 Jahren.", "The Great Pyramid of Giza is made of about 2.3 million stone blocks. It was probably built in about 20 years."),
                steps = listOf(
                    num(
                        l("Wie viele Blöcke mussten im Schnitt pro Tag verbaut werden?", "How many blocks had to be placed per day on average?"), perDay, l("Blöcke", "blocks"), rel = 0.03,
                        done = l("etwa 315 pro Tag", "about 315 per day"),
                        hints = listOf(l("Rechne erst aus, wie viele Tage 20 Jahre haben.", "First work out how many days 20 years have."), """§2\,300\,000 : (20 \cdot 365)§"""),
                    ),
                    num(
                        l("Bei 12 Stunden Arbeit am Tag: wie viele pro Stunde?", "With 12 working hours a day: how many per hour?"), perDay / 12, l("Blöcke", "blocks"), rel = 0.04,
                        done = l("etwa 26 pro Stunde", "about 26 per hour"),
                        hints = listOf(l("Teile das Tagesergebnis durch 12.", "Divide the daily result by 12."), """§315 : 12§"""),
                    ),
                ),
                solution = listOf(
                    l("""§20 \cdot 365 = 7300§ Tage §\;\Rightarrow\; 2\,300\,000 : 7300 \approx 315§ Blöcke pro Tag""", """§20 \cdot 365 = 7300§ days §\;\Rightarrow\; 2\,300\,000 : 7300 \approx 315§ blocks per day"""),
                    l("""§315 : 12 \approx 26§ Blöcke pro Stunde, also alle gut 2 Minuten einer""", """§315 : 12 \approx 26§ blocks per hour, so one every 2 minutes or so"""),
                ),
                background = l(
                    "Die Cheops-Pyramide wurde um 2560 v. Chr. für den Pharao Cheops gebaut und war fast 4000 Jahre lang das höchste Bauwerk der Welt. Gebaut haben sie keine Sklaven, wie man lange dachte, sondern bezahlte Arbeiter und Handwerker; man hat ihre Siedlungen und Gräber gefunden. Wie genau die tonnenschweren Blöcke nach oben kamen, ist bis heute umstritten. Vermutlich über Rampen, auf Schlitten, die über angefeuchteten Sand gezogen wurden.",
                    "The Great Pyramid was built around 2560 BC for the pharaoh Khufu and was the tallest structure in the world for almost 4000 years. It wasn't built by slaves, as people long believed, but by paid workers and craftsmen; their villages and tombs have been found. Exactly how the multi-tonne blocks got to the top is still debated. Probably via ramps, on sledges pulled over wet sand.",
                ),
                fact = l("Ein Wandbild aus dem alten Ägypten zeigt einen Arbeiter, der Wasser vor einen Schlitten gießt. Nasser Sand halbiert ungefähr die Reibung.", "An ancient Egyptian wall painting shows a worker pouring water in front of a sledge. Wet sand roughly halves the friction."),
            )
        },
        TaskDef("kleopatra", Topic.History, E) {
            Task(
                "kleopatra", Topic.History, E,
                question = l("Kleopatra starb im Jahr 30 v. Chr. Die Cheops-Pyramide wurde um 2560 v. Chr. gebaut, die erste Mondlandung war 1969. Wem lebte Kleopatra zeitlich näher?", "Cleopatra died in 30 BC. The Great Pyramid was built around 2560 BC, the first Moon landing was in 1969. Which was Cleopatra closer to in time?"),
                steps = listOf(
                    choice(
                        l("Kleopatra lebte näher an …", "Cleopatra lived closer to …"),
                        listOf(l("der Mondlandung", "the Moon landing"), l("dem Bau der Pyramide", "the building of the pyramid"), l("Beides etwa gleich", "Both about the same")),
                        done = l("der Mondlandung", "the Moon landing"),
                        hints = listOf(l("Rechne beide Abstände in Jahren aus.", "Work out both gaps in years."), l("""Bis zur Pyramide: §2560 - 30§. Bis zur Mondlandung: §30 + 1969§.""", """To the pyramid: §2560 - 30§. To the Moon landing: §30 + 1969§.""")),
                    ),
                ),
                solution = listOf(l("""Pyramide: §2560 - 30 = 2530§ Jahre. Mondlandung: §30 + 1969 = 1999§ Jahre. Die Mondlandung liegt gut 500 Jahre näher.""", """Pyramid: §2560 - 30 = 2530§ years. Moon landing: §30 + 1969 = 1999§ years. The Moon landing is more than 500 years closer.""")),
                background = l(
                    "„Das alte Ägypten“ fühlt sich wie eine Epoche an, dauerte aber fast 3000 Jahre. Für Kleopatra waren die Pyramiden schon uralte Ruinen, älter als für uns das Römische Reich. Solche Vergleiche helfen, ein Gefühl für historische Zeit zu bekommen: Zeitabstände schrumpfen in unserer Vorstellung, je weiter sie zurückliegen.",
                    "\"Ancient Egypt\" feels like a single era, but it lasted almost 3000 years. To Cleopatra the pyramids were already ancient ruins, older than the Roman Empire is to us. Comparisons like this help build a sense of historical time: in our minds, gaps shrink the further back they are.",
                ),
                fact = l("Zu Lebzeiten der Pyramidenbauer gab es auf der Erde noch Wollmammuts: Die letzten lebten bis etwa 2000 v. Chr. auf der Wrangelinsel.", "When the pyramids were built, woolly mammoths still existed: the last ones lived on Wrangel Island until about 2000 BC."),
            )
        },
        TaskDef("gutenberg", Topic.History, M) {
            val years = 180.0 * 1282 / 3 / 365.25
            Task(
                "gutenberg", Topic.History, M,
                question = l("Gutenberg druckte um 1455 etwa 180 Bibeln mit je 1282 Seiten. Ein Mönch schaffte beim Abschreiben etwa 3 Seiten pro Tag. Wie viele Jahre hätte ein einzelner Mönch für alle 180 Bibeln gebraucht?", "Around 1455 Gutenberg printed about 180 Bibles with 1282 pages each. A monk copying by hand managed about 3 pages a day. How many years would a single monk have needed for all 180 Bibles?"),
                steps = listOf(
                    num(
                        l("Jahre (ohne freie Tage)", "Years (without days off)"), years, l("Jahre", "years"), rel = 0.03,
                        done = l("rund 211 Jahre", "about 211 years"),
                        hints = listOf(l("Erst alle Seiten zusammen, dann die Tage, dann die Jahre.", "First all pages together, then the days, then the years."), """§180 \cdot 1282 : 3 : 365§"""),
                    ),
                ),
                solution = listOf(
                    l("""§180 \cdot 1282 = 230\,760§ Seiten §\;\Rightarrow\; 230\,760 : 3 = 76\,920§ Tage §\approx 211§ Jahre""", """§180 \cdot 1282 = 230\,760§ pages §\;\Rightarrow\; 230\,760 : 3 = 76\,920§ days §\approx 211§ years"""),
                ),
                background = l(
                    "Vor Gutenberg wurden Bücher von Hand abgeschrieben und waren entsprechend teuer und selten. Seine Erfindung waren bewegliche Lettern aus Metall, die man immer neu zusammensetzen konnte, zusammen mit einer passenden Druckerpresse und Tinte. Innerhalb von 50 Jahren wurden in Europa Millionen Bücher gedruckt. Ohne den Buchdruck hätte sich etwa die Reformation kaum so schnell verbreitet.",
                    "Before Gutenberg, books were copied by hand and were expensive and rare. His invention was movable metal type that could be rearranged again and again, together with a suitable press and ink. Within 50 years, millions of books had been printed in Europe. Without printing, the Reformation, for example, could hardly have spread so fast.",
                ),
                fact = l("Von den Gutenberg-Bibeln sind heute noch 49 erhalten, zwei davon in Göttingen und Mainz.", "49 Gutenberg Bibles survive today, including copies in Göttingen and Mainz."),
            )
        },
        TaskDef("zeitstrahl-neuzeit", Topic.History, E) {
            Task(
                "zeitstrahl-neuzeit", Topic.History, E,
                question = l("Bring die drei Ereignisse in die richtige Reihenfolge.", "Put the three events in the right order."),
                steps = listOf(
                    choice(
                        l("Was kam zuerst?", "What came first?"),
                        listOf(l("Buchdruck → Kolumbus in Amerika → Luthers Thesen", "Printing press → Columbus in America → Luther's theses"), l("Kolumbus in Amerika → Buchdruck → Luthers Thesen", "Columbus in America → printing press → Luther's theses"), l("Luthers Thesen → Buchdruck → Kolumbus in Amerika", "Luther's theses → printing press → Columbus in America")),
                        done = l("1450 → 1492 → 1517", "1450 → 1492 → 1517"),
                        hints = listOf(l("Luthers Thesen verbreiteten sich so schnell, weil sie gedruckt wurden.", "Luther's theses spread so fast because they were printed."), l("Kolumbus kam 1492 an.", "Columbus arrived in 1492.")),
                    ),
                ),
                solution = listOf(l("Buchdruck um 1450, Kolumbus 1492, Luthers 95 Thesen 1517.", "Printing press around 1450, Columbus 1492, Luther's 95 theses 1517.")),
                background = l(
                    "Diese 70 Jahre gelten als Beginn der Neuzeit: Wissen konnte sich durch den Buchdruck plötzlich schnell verbreiten, Europa entdeckte neue Kontinente, und die Kirche verlor ihre Alleinstellung. Die drei Ereignisse hängen zusammen: Ohne gedruckte Flugblätter wäre Luther vielleicht ein unbekannter Mönch geblieben.",
                    "These 70 years are seen as the start of the modern era: thanks to printing, knowledge could suddenly spread fast, Europe reached new continents, and the Church lost its monopoly. The three events are connected: without printed pamphlets, Luther might have remained an unknown monk.",
                ),
                fact = l("Ob Luther seine Thesen wirklich an die Tür der Schlosskirche geschlagen hat, ist unter Historikern umstritten.", "Whether Luther really nailed his theses to the church door is disputed among historians."),
            )
        },
        TaskDef("zeitstrahl-19jh", Topic.History, E) {
            Task(
                "zeitstrahl-19jh", Topic.History, E,
                question = l("Bring die drei Ereignisse in die richtige Reihenfolge.", "Put the three events in the right order."),
                steps = listOf(
                    choice(
                        l("Was kam zuerst?", "What came first?"),
                        listOf(l("Französische Revolution → erste deutsche Eisenbahn → Gründung des Deutschen Reichs", "French Revolution → first German railway → founding of the German Empire"), l("Erste deutsche Eisenbahn → Französische Revolution → Gründung des Deutschen Reichs", "First German railway → French Revolution → founding of the German Empire"), l("Gründung des Deutschen Reichs → Französische Revolution → erste deutsche Eisenbahn", "Founding of the German Empire → French Revolution → first German railway")),
                        done = l("1789 → 1835 → 1871", "1789 → 1835 → 1871"),
                        hints = listOf(l("Die Französische Revolution war noch im 18. Jahrhundert.", "The French Revolution was still in the 18th century."), l("Die erste deutsche Eisenbahn fuhr von Nürnberg nach Fürth.", "The first German railway ran from Nuremberg to Fürth.")),
                    ),
                ),
                solution = listOf(l("Französische Revolution 1789, Nürnberg–Fürth 1835, Reichsgründung 1871.", "French Revolution 1789, Nuremberg–Fürth 1835, founding of the Empire 1871.")),
                background = l(
                    "Die Französische Revolution brachte Ideen wie Freiheit, Gleichheit und Menschenrechte nach Europa. Im 19. Jahrhundert veränderte die Industrialisierung mit Dampfmaschine und Eisenbahn das Leben radikal: Reisen, die Tage dauerten, gingen plötzlich in Stunden. Deutschland war bis 1871 kein einzelner Staat, sondern ein Flickenteppich aus Königreichen und Fürstentümern.",
                    "The French Revolution brought ideas like liberty, equality and human rights to Europe. In the 19th century, industrialisation with steam engines and railways changed life radically: journeys that took days suddenly took hours. Until 1871 Germany wasn't one state but a patchwork of kingdoms and principalities.",
                ),
                fact = l("Die erste deutsche Eisenbahn, der „Adler“, fuhr 6 km weit. Viele Ärzte warnten, so hohe Geschwindigkeiten würden krank machen.", "The first German railway, the \"Adler\", ran 6 km. Many doctors warned such high speeds would make people ill."),
            )
        },
        TaskDef("mauer", Topic.History, M) {
            Task(
                "mauer", Topic.History, M,
                question = l("Die Berliner Mauer wurde am 13. August 1961 gebaut und fiel am 9. November 1989.", "The Berlin Wall was built on 13 August 1961 and fell on 9 November 1989."),
                steps = listOf(
                    num(
                        l("Wie viele volle Jahre stand sie?", "How many full years did it stand?"), 28.0, l("Jahre", "years"), abs = 0.0,
                        done = l("28 Jahre", "28 years"),
                        hints = listOf("""§1989 - 1961§""", l("August bis November: Das 28. Jahr ist schon voll.", "August to November: the 28th year is already complete.")),
                    ),
                    choice(
                        l("An welchem Tag war die Mauer genauso lange weg, wie sie gestanden hatte?", "On which day had the Wall been gone exactly as long as it had stood?"),
                        listOf(l("5. Februar 2018", "5 February 2018"), l("9. November 2017", "9 November 2017"), l("3. Oktober 2018", "3 October 2018")),
                        done = l("5. Februar 2018", "5 February 2018"),
                        hints = listOf(l("Die Mauer stand 28 Jahre, 2 Monate und 27 Tage.", "The Wall stood for 28 years, 2 months and 27 days."), l("Zähle das ab dem 9. November 1989 weiter.", "Count that on from 9 November 1989.")),
                    ),
                ),
                solution = listOf(l("""13.8.1961 bis 9.11.1989: 28 Jahre, 2 Monate, 27 Tage (10.315 Tage). Dieselbe Spanne ab dem 9.11.1989 endet am 5.2.2018.""", """13 Aug 1961 to 9 Nov 1989: 28 years, 2 months, 27 days (10,315 days). The same span from 9 Nov 1989 ends on 5 Feb 2018.""")),
                background = l(
                    "Nach dem Zweiten Weltkrieg wurde Deutschland geteilt. Aus der DDR flohen bis 1961 rund drei Millionen Menschen in den Westen, viele über Berlin. Die DDR-Führung riegelte deshalb West-Berlin mit einer Mauer ab. Mindestens 140 Menschen starben bei Fluchtversuchen. Friedliche Massenproteste in der DDR führten im Herbst 1989 dazu, dass die Grenze geöffnet wurde, ein Jahr später war Deutschland wiedervereinigt.",
                    "After World War II, Germany was divided. By 1961 about three million people had fled East Germany to the West, many through Berlin. The East German leadership therefore sealed off West Berlin with a wall. At least 140 people died trying to escape. Peaceful mass protests in East Germany led to the border opening in autumn 1989, and a year later Germany was reunified.",
                ),
                fact = l("Der 5. Februar 2018 wird „Zirkeltag“ genannt. Wer danach geboren ist, lebt in einer Welt, in der die Mauer länger weg ist, als sie stand.", "5 February 2018 is called \"Zirkeltag\". Anyone born afterwards lives in a world where the Wall has been gone longer than it stood."),
            )
        },
        TaskDef("kalender", Topic.History, H) {
            val drift = 365.25 - 365.2422
            Task(
                "kalender", Topic.History, H,
                question = l("Im Oktober 1582 folgte auf Donnerstag, den 4., direkt Freitag, der 15. Papst Gregor XIII. ließ Tage streichen, weil der alte julianische Kalender gegenüber der Sonne verrutscht war.", "In October 1582, Thursday the 4th was followed directly by Friday the 15th. Pope Gregory XIII had days removed because the old Julian calendar had drifted against the Sun."),
                given = listOf(l("Julianisches Jahr: 365,25 Tage (alle 4 Jahre ein Schaltjahr). Sonnenjahr: 365,2422 Tage.", "Julian year: 365.25 days (a leap year every 4 years). Solar year: 365.2422 days.")),
                steps = listOf(
                    num(
                        l("Nach wie vielen Jahren ist der julianische Kalender um einen Tag verrutscht?", "After how many years has the Julian calendar drifted by one day?"), 1 / drift, l("Jahre", "years"), rel = 0.03,
                        done = l("etwa 128 Jahre", "about 128 years"),
                        hints = listOf(l("Pro Jahr ist er 365,25 − 365,2422 Tage zu lang.", "Each year it is 365.25 − 365.2422 days too long."), l("""§1 : 0{,}0078§""", """§1 : 0.0078§""")),
                    ),
                    num(
                        l("Um wie viele Tage war er 1582 seit dem Konzil von Nizäa (325) verrutscht?", "By how many days had it drifted in 1582 since the Council of Nicaea (325)?"), 1257 * drift, l("Tage", "days"), abs = 0.5,
                        done = l("rund 10 Tage", "about 10 days"),
                        hints = listOf(l("Wie viele Jahre liegen dazwischen?", "How many years lie in between?"), l("""§1257 \cdot 0{,}0078§""", """§1257 \cdot 0.0078§""")),
                    ),
                    choice(
                        l("Welche Regel hat Gregor eingeführt, damit es nicht wieder passiert?", "Which rule did Gregory introduce so it wouldn't happen again?"),
                        listOf(l("Volle Jahrhunderte sind nur Schaltjahre, wenn sie durch 400 teilbar sind", "Full centuries are only leap years if divisible by 400"), l("Jedes 128. Jahr fällt das Schaltjahr aus", "Every 128th year the leap year is skipped"), l("Nur noch alle 5 Jahre ein Schaltjahr", "Only one leap year every 5 years")),
                        done = l("Die 400er-Regel", "The 400 rule"),
                        hints = listOf(l("1900 war kein Schaltjahr, 2000 schon.", "1900 wasn't a leap year, 2000 was."), l("So fallen in 400 Jahren genau 3 Schalttage weg.", "That way exactly 3 leap days are dropped every 400 years.")),
                    ),
                    num(
                        l("Der gregorianische Kalender hat im Mittel 365,2425 Tage. Nach wie vielen Jahren verrutscht er um einen Tag?", "The Gregorian calendar averages 365.2425 days. After how many years does it drift by one day?"), 1 / (365.2425 - 365.2422), l("Jahre", "years"), rel = 0.05,
                        done = l("etwa 3300 Jahre", "about 3300 years"),
                        hints = listOf(l("Wieder: 1 durch die Differenz.", "Again: 1 divided by the difference."), l("""§1 : 0{,}0003§""", """§1 : 0.0003§""")),
                    ),
                ),
                solution = listOf(
                    l("""§365{,}25 - 365{,}2422 = 0{,}0078§ Tage pro Jahr §\;\Rightarrow\; 1 : 0{,}0078 \approx 128§ Jahre""", """§365.25 - 365.2422 = 0.0078§ days per year §\;\Rightarrow\; 1 : 0.0078 \approx 128§ years"""),
                    l("""§(1582 - 325) \cdot 0{,}0078 \approx 9{,}8 \approx 10§ Tage""", """§(1582 - 325) \cdot 0.0078 \approx 9.8 \approx 10§ days"""),
                    l("""§365 + \tfrac{97}{400} = 365{,}2425 \;\Rightarrow\; 1 : 0{,}0003 \approx 3300§ Jahre""", """§365 + \tfrac{97}{400} = 365.2425 \;\Rightarrow\; 1 : 0.0003 \approx 3300§ years"""),
                ),
                background = l(
                    "Ein Jahr dauert nicht genau 365 Tage, sondern knapp 365 ¼. Julius Caesar führte deshalb 45 v. Chr. alle vier Jahre einen Schalttag ein. Das war aber ein wenig zu viel, und über die Jahrhunderte rutschte der Frühlingsanfang im Kalender immer weiter nach vorne. Weil Ostern am Frühlingsanfang hängt, wollte die Kirche das korrigieren. 1582 strich Gregor XIII. zehn Tage und führte die 400er-Regel ein, die wir bis heute nutzen.",
                    "A year doesn't last exactly 365 days but just under 365 ¼. That's why Julius Caesar introduced a leap day every four years in 45 BC. But that was slightly too much, and over the centuries the start of spring kept creeping earlier in the calendar. Because Easter depends on the start of spring, the Church wanted to fix it. In 1582 Gregory XIII removed ten days and introduced the 400 rule we still use.",
                ),
                fact = l("Protestantische Länder machten erst später mit, Russland erst 1918. Deshalb fand die „Oktoberrevolution“ nach unserem Kalender im November statt.", "Protestant countries only joined later, Russia not until 1918. That's why the \"October Revolution\" happened in November by our calendar."),
            )
        },
        TaskDef("roemisch", Topic.History, E) {
            Task(
                "roemisch", Topic.History, E,
                question = l("Rechne mit römischen Zahlen: MCMLXIX + XXXI. Wie lautet das Ergebnis in unseren Ziffern?", "Calculate with Roman numerals: MCMLXIX + XXXI. What is the result in our digits?"),
                steps = listOf(
                    num(
                        """§\text{MCMLXIX} + \text{XXXI} = \square§""", 2000.0, abs = 0.0,
                        done = """§2000 = \text{MM}§""",
                        hints = listOf(l("M = 1000, C = 100, L = 50, X = 10, V = 5, I = 1. Steht ein kleineres Zeichen vor einem größeren, wird es abgezogen.", "M = 1000, C = 100, L = 50, X = 10, V = 5, I = 1. A smaller symbol before a larger one is subtracted."), l("MCMLXIX = 1000 + 900 + 60 + 9", "MCMLXIX = 1000 + 900 + 60 + 9")),
                    ),
                ),
                solution = listOf(l("MCMLXIX = 1000 + (1000 − 100) + 50 + 10 + (10 − 1) = 1969, XXXI = 31, zusammen 2000 = MM.", "MCMLXIX = 1000 + (1000 − 100) + 50 + 10 + (10 − 1) = 1969, XXXI = 31, together 2000 = MM.")),
                background = l(
                    "Die Römer schrieben Zahlen als Summe von Zeichen. Schriftliches Rechnen wie bei uns ging damit kaum, deshalb rechneten sie mit dem Abakus, einem Rechenbrett. Unsere Ziffern stammen aus Indien und kamen über arabische Gelehrte nach Europa. Ihr Geniestreich ist die Null und das Stellenwertsystem: Dieselbe 1 bedeutet je nach Platz eins, zehn oder hundert.",
                    "The Romans wrote numbers as sums of symbols. Written arithmetic like ours was hardly possible with them, so they calculated on an abacus, a counting board. Our digits come from India and reached Europe via Arab scholars. Their stroke of genius is zero and place value: the same 1 means one, ten or a hundred depending on its position.",
                ),
                fact = l("1969 ist das Jahr der Mondlandung. Und eine römische Null gab es nicht.", "1969 is the year of the Moon landing. And there was no Roman zero."),
            )
        },

        // ======================================== GEOLOGIE ========================================
        TaskDef("atlantik", Topic.Geology, M) {
            Task(
                "atlantik", Topic.Geology, M,
                question = l("Amerika und Europa entfernen sich jedes Jahr um etwa 2,5 cm voneinander, ungefähr so schnell, wie deine Fingernägel wachsen.", "America and Europe move apart by about 2.5 cm every year, roughly as fast as your fingernails grow."),
                steps = listOf(
                    num(
                        l("Um wie viele Meter ist der Atlantik seit Kolumbus (rund 530 Jahre) breiter geworden?", "By how many metres has the Atlantic widened since Columbus (about 530 years)?"), 530 * 0.025, "m", rel = 0.05,
                        done = l("etwa 13 m", "about 13 m"),
                        hints = listOf(l("Achte auf die Einheiten: 2,5 cm = 0,025 m.", "Watch the units: 2.5 cm = 0.025 m."), l("""§530 \cdot 0{,}025\ \text{m}§""", """§530 \cdot 0.025\ \text{m}§""")),
                    ),
                    num(
                        l("Der Atlantik ist heute rund 5000 km breit. Vor wie vielen Millionen Jahren hat er sich ungefähr geöffnet?", "The Atlantic is about 5000 km wide today. Roughly how many million years ago did it start opening?"), 5e8 / 2.5 / 1e6, l("Mio. Jahre", "million years"), rel = 0.05,
                        done = l("vor etwa 200 Mio. Jahren", "about 200 million years ago"),
                        hints = listOf(l("5000 km sind 500 Millionen Zentimeter.", "5000 km is 500 million centimetres."), """§500\,000\,000 : 2{,}5§""".let { l(it, it.replace("2{,}5", "2.5")) }),
                    ),
                ),
                solution = listOf(
                    l("""§530 \cdot 2{,}5\ \text{cm} \approx 1325\ \text{cm} \approx 13\ \text{m}§""", """§530 \cdot 2.5\ \text{cm} \approx 1325\ \text{cm} \approx 13\ \text{m}§"""),
                    l("""§5000\ \text{km} = 5 \cdot 10^8\ \text{cm} \;\Rightarrow\; 5 \cdot 10^8 : 2{,}5 = 2 \cdot 10^8§ Jahre""", """§5000\ \text{km} = 5 \cdot 10^8\ \text{cm} \;\Rightarrow\; 5 \cdot 10^8 : 2.5 = 2 \cdot 10^8§ years"""),
                ),
                background = l(
                    "Die Erdkruste besteht aus großen Platten, die auf dem zähen, heißen Erdmantel treiben. In der Mitte des Atlantiks verläuft ein riesiges unterseeisches Gebirge, der Mittelatlantische Rücken. Dort steigt Magma auf, erstarrt und schiebt die Platten auseinander. Alfred Wegener vermutete schon 1912, dass die Kontinente einmal zusammenhingen, weil Südamerika und Afrika wie Puzzleteile zusammenpassen. Ausgelacht wurde er trotzdem, bewiesen wurde es erst in den 1960ern.",
                    "The Earth's crust is made of large plates floating on the hot, viscous mantle. A huge underwater mountain range, the Mid-Atlantic Ridge, runs down the middle of the Atlantic. There magma rises, solidifies and pushes the plates apart. Alfred Wegener suspected as early as 1912 that the continents were once joined, because South America and Africa fit together like puzzle pieces. He was laughed at anyway; proof only came in the 1960s.",
                ),
                fact = l("Island liegt direkt auf dem Mittelatlantischen Rücken. In der Silfra-Spalte kann man zwischen der amerikanischen und der eurasischen Platte tauchen.", "Iceland sits right on the Mid-Atlantic Ridge. In the Silfra fissure you can dive between the American and the Eurasian plate."),
            )
        },
        TaskDef("erduhr", Topic.Geology, M) {
            val perSecond = 4.6e9 / DAY
            val humanS = 300_000 / perSecond
            Task(
                "erduhr", Topic.Geology, M,
                question = l("Stell dir die 4,6 Milliarden Jahre der Erdgeschichte als einen einzigen Tag vor: Um 0 Uhr entsteht die Erde, um Mitternacht ist heute.", "Imagine the 4.6 billion years of Earth's history as a single day: the Earth forms at midnight, and the next midnight is today."),
                steps = listOf(
                    num(
                        l("Wie viele Jahre entspricht eine Sekunde auf dieser Uhr?", "How many years does one second on this clock correspond to?"), perSecond, l("Jahre", "years"), rel = 0.03,
                        done = l("etwa 53.000 Jahre", "about 53,000 years"),
                        hints = listOf(l("Ein Tag hat 86.400 Sekunden.", "A day has 86,400 seconds."), l("""§4{,}6 \cdot 10^9 : 86\,400§""", """§4.6 \cdot 10^9 : 86\,400§""")),
                    ),
                    num(
                        l("Den Homo sapiens gibt es seit rund 300.000 Jahren. Wie viele Sekunden vor Mitternacht taucht er auf?", "Homo sapiens has existed for about 300,000 years. How many seconds before midnight does it appear?"), humanS, "s", rel = 0.04,
                        done = l("knapp 6 Sekunden vor Mitternacht", "just under 6 seconds before midnight"),
                        hints = listOf(l("Teile durch die Jahre pro Sekunde.", "Divide by the years per second."), """§300\,000 : 53\,000§"""),
                    ),
                    choice(
                        l("Die Dinosaurier starben vor 66 Millionen Jahren aus. Um wie viel Uhr ist das?", "The dinosaurs died out 66 million years ago. What time is that?"),
                        listOf(l("Etwa 23:39 Uhr", "About 11:39 pm"), l("Etwa 22:00 Uhr", "About 10:00 pm"), l("Etwa 18:00 Uhr", "About 6:00 pm")),
                        done = l("Etwa 23:39 Uhr", "About 11:39 pm"),
                        hints = listOf(l("""§66\,000\,000 : 53\,000 \approx 1240§ Sekunden vor Mitternacht""", """§66\,000\,000 : 53\,000 \approx 1240§ seconds before midnight"""), l("1240 Sekunden sind gut 20 Minuten.", "1240 seconds is just over 20 minutes.")),
                    ),
                ),
                solution = listOf(
                    l("""§4{,}6 \cdot 10^9\ \text{Jahre} : 86\,400\ \text{s} \approx 53\,000§ Jahre pro Sekunde""", """§4.6 \cdot 10^9\ \text{years} : 86\,400\ \text{s} \approx 53\,000§ years per second"""),
                    l("""Mensch: §300\,000 : 53\,000 \approx 5{,}6\ \text{s}§ vor Mitternacht""", """Humans: §300\,000 : 53\,000 \approx 5.6\ \text{s}§ before midnight"""),
                    l("""Dinosaurier: §66 \cdot 10^6 : 53\,000 \approx 1240\ \text{s} \approx 21\ \text{min}§ vor Mitternacht, also etwa 23:39 Uhr""", """Dinosaurs: §66 \cdot 10^6 : 53\,000 \approx 1240\ \text{s} \approx 21\ \text{min}§ before midnight, so about 11:39 pm"""),
                ),
                background = l(
                    "Geologen teilen die Erdgeschichte in Zeitalter ein, die sie an Gesteinsschichten und Fossilien ablesen. Den größten Teil der Zeit gab es nur Einzeller: Komplexe Tiere tauchen erst um etwa 21 Uhr auf unserer Uhr auf. Das Alter selbst misst man mit radioaktiven Atomen, die mit fester Halbwertszeit zerfallen, zum Beispiel Uran zu Blei in winzigen Zirkon-Kristallen.",
                    "Geologists divide Earth's history into eras that they read from rock layers and fossils. For most of that time there were only single-celled organisms: complex animals only appear around 9 pm on our clock. The age itself is measured with radioactive atoms that decay with a fixed half-life, for example uranium to lead in tiny zircon crystals.",
                ),
                fact = l("Die ältesten bekannten Minerale der Erde sind Zirkone aus Australien. Sie sind rund 4,4 Milliarden Jahre alt.", "The oldest known minerals on Earth are zircons from Australia. They are about 4.4 billion years old."),
            )
        },
        TaskDef("gestein", Topic.Geology, E) {
            Task(
                "gestein", Topic.Geology, E,
                question = l("Granit und Basalt entstehen beide aus erstarrter Gesteinsschmelze. Granit hat große Kristalle, die man mit bloßem Auge sieht, Basalt nur winzige. Warum?", "Granite and basalt both form from solidified molten rock. Granite has large crystals you can see with the naked eye, basalt only tiny ones. Why?"),
                steps = listOf(
                    choice(
                        l("Der Grund:", "The reason:"),
                        listOf(l("Granit ist tief in der Erde sehr langsam abgekühlt, Basalt schnell an der Oberfläche", "Granite cooled very slowly deep underground, basalt quickly at the surface"), l("Granit ist viel älter als Basalt", "Granite is much older than basalt"), l("Granit wurde später von Wasser ausgewaschen", "Granite was later washed out by water")),
                        done = l("Langsam abgekühlt", "Cooled slowly"),
                        hints = listOf(l("Denk an Kandiszucker: Große Kristalle brauchen Zeit zum Wachsen.", "Think of rock candy: large crystals need time to grow."), l("Basalt ist erstarrte Lava.", "Basalt is solidified lava.")),
                    ),
                ),
                solution = listOf(l("Je langsamer eine Schmelze abkühlt, desto mehr Zeit haben die Kristalle zum Wachsen. Granit erstarrt über Jahrtausende in der Tiefe, Basalt in Tagen bis Wochen an der Oberfläche.", "The slower a melt cools, the more time crystals have to grow. Granite solidifies over millennia deep below, basalt within days to weeks at the surface.")),
                background = l(
                    "Gesteine werden nach ihrer Entstehung in drei Gruppen eingeteilt: Magmatite erstarren aus Schmelze (Granit, Basalt). Sedimentgesteine entstehen aus abgelagerten Körnchen oder Resten von Lebewesen (Sandstein, Kalkstein). Metamorphe Gesteine bilden sich, wenn andere Gesteine unter hohem Druck und hoher Temperatur umgewandelt werden (Marmor aus Kalk, Schiefer aus Ton). Über Jahrmillionen wandelt sich jedes Gestein immer wieder in ein anderes um: der Kreislauf der Gesteine.",
                    "Rocks are grouped into three types by how they form: igneous rocks solidify from melt (granite, basalt). Sedimentary rocks form from deposited grains or the remains of living things (sandstone, limestone). Metamorphic rocks form when other rocks are transformed under high pressure and temperature (marble from limestone, slate from clay). Over millions of years every rock keeps turning into another: the rock cycle.",
                ),
                fact = l("Bei sehr schneller Abkühlung entstehen gar keine Kristalle: Dann bildet sich vulkanisches Glas, der Obsidian.", "With very fast cooling no crystals form at all: volcanic glass forms instead, obsidian."),
            )
        },
        TaskDef("erdbeben", Topic.Geology, M) {
            Task(
                "erdbeben", Topic.Geology, M,
                question = l("Bei der Magnitudenskala für Erdbeben bedeutet jede Stufe einen zehnmal größeren Ausschlag des Seismografen, aber etwa 32-mal mehr freigesetzte Energie.", "On the earthquake magnitude scale, each step means a ten times larger swing of the seismograph, but about 32 times more energy released."),
                steps = listOf(
                    num(
                        l("Wie viel mal größer ist der Ausschlag bei Stärke 7 als bei Stärke 5?", "How many times larger is the swing at magnitude 7 than at 5?"), 100.0, "", abs = 0.0,
                        done = l("100-mal", "100 times"),
                        hints = listOf(l("Zwei Stufen, jede mal 10.", "Two steps, each times 10."), """§10 \cdot 10§"""),
                    ),
                    num(
                        l("Und wie viel mal mehr Energie wird frei?", "And how many times more energy is released?"), 10.0.pow(3.0), "", rel = 0.05,
                        done = l("etwa 1000-mal", "about 1000 times"),
                        hints = listOf(l("""32 ist ungefähr §\sqrt{1000}§.""", """32 is roughly §\sqrt{1000}§."""), """§32 \cdot 32 \approx 1000§"""),
                    ),
                ),
                solution = listOf(
                    """§\text{Ausschlag: } 10^{7-5} = 100§""".let { l(it, it.replace("Ausschlag", "Swing")) },
                    l("""§\text{Energie: } \left(10^{1{,}5}\right)^{2} = 10^{3} = 1000§""", """§\text{Energy: } \left(10^{1.5}\right)^{2} = 10^{3} = 1000§"""),
                ),
                background = l(
                    "Erdbeben entstehen, wenn sich zwei Gesteinsblöcke an einer Bruchzone lange verhaken und dann ruckartig gegeneinander verschieben. Weil die Stärken über viele Größenordnungen reichen, nutzt man eine logarithmische Skala: Jede ganze Stufe bedeutet etwa 32-mal mehr Energie. Ein Beben der Stärke 9 setzt deshalb rund eine Milliarde Mal mehr Energie frei als eins der Stärke 3, das man gerade noch spürt.",
                    "Earthquakes happen when two blocks of rock at a fault stay locked for a long time and then suddenly slip past each other. Because strengths span many orders of magnitude, a logarithmic scale is used: each whole step means about 32 times more energy. A magnitude 9 quake therefore releases about a billion times more energy than a magnitude 3 one that you can just about feel.",
                ),
                fact = l("Das Tōhoku-Beben 2011 in Japan (Stärke 9,1) hat Masse in der Erde so verschoben, dass der Tag um etwa 1,8 Mikrosekunden kürzer wurde.", "The 2011 Tōhoku earthquake in Japan (magnitude 9.1) shifted mass inside the Earth so much that the day got about 1.8 microseconds shorter."),
            )
        },
        TaskDef("erdkern", Topic.Geology, E) {
            Task(
                "erdkern", Topic.Geology, E,
                question = l("Im Mittelpunkt der Erde, 6371 km unter dir, liegt der innere Erdkern aus Eisen und Nickel.", "At the centre of the Earth, 6371 km below you, lies the inner core of iron and nickel."),
                steps = listOf(
                    choice(
                        l("Wie heiß ist er ungefähr?", "Roughly how hot is it?"),
                        listOf(l("Etwa so heiß wie die Sonnenoberfläche (rund 5000 bis 6000 °C)", "About as hot as the Sun's surface (around 5000 to 6000 °C)"), l("Etwa 1000 °C, wie Lava", "About 1000 °C, like lava"), l("Etwa 100.000 °C", "About 100,000 °C")),
                        done = l("So heiß wie die Sonnenoberfläche", "As hot as the Sun's surface"),
                        hints = listOf(l("Es ist heißer als Lava, die aus viel geringerer Tiefe kommt.", "It's hotter than lava, which comes from much shallower depths."), l("Die Sonnenoberfläche hat etwa 5500 °C.", "The Sun's surface is about 5500 °C.")),
                    ),
                    choice(
                        l("Eisen schmilzt bei 1538 °C. Warum ist der innere Kern trotzdem fest?", "Iron melts at 1538 °C. Why is the inner core solid anyway?"),
                        listOf(l("Der enorme Druck hält die Atome zusammen", "The enormous pressure holds the atoms together"), l("Er kühlt gerade ab", "It is just cooling down"), l("Er besteht aus anderem Eisen", "It is made of a different kind of iron")),
                        done = l("Wegen des Drucks", "Because of the pressure"),
                        hints = listOf(l("Dort unten herrscht über drei Millionen Mal der Luftdruck an der Oberfläche.", "Down there the pressure is more than three million times the air pressure at the surface."), l("Hoher Druck erhöht bei den meisten Stoffen die Schmelztemperatur.", "High pressure raises the melting temperature of most substances.")),
                    ),
                ),
                solution = listOf(l("Etwa 5000 bis 6000 °C. Der Druck von rund 3,6 Millionen bar drückt die Schmelztemperatur von Eisen so weit hoch, dass der innere Kern fest bleibt. Der äußere Kern darum ist flüssig.", "About 5000 to 6000 °C. The pressure of about 3.6 million bar pushes the melting point of iron up so far that the inner core stays solid. The outer core around it is liquid.")),
                background = l(
                    "Niemand war je im Erdinneren: Das tiefste Bohrloch reicht nur gut 12 km tief. Was wir wissen, stammt von Erdbebenwellen. Sie werden an Schichtgrenzen gebrochen und reflektiert, und bestimmte Wellen können flüssiges Material nicht durchqueren. So entdeckte Inge Lehmann 1936, dass der Kern innen fest ist. Der flüssige äußere Kern ist in ständiger Bewegung und erzeugt das Magnetfeld der Erde.",
                    "No one has ever been inside the Earth: the deepest borehole only reaches just over 12 km. What we know comes from earthquake waves. They are refracted and reflected at layer boundaries, and certain waves can't pass through liquid. That's how Inge Lehmann discovered in 1936 that the core is solid inside. The liquid outer core is constantly moving and generates the Earth's magnetic field.",
                ),
                fact = l("Ohne das Magnetfeld aus dem flüssigen Kern würde der Sonnenwind unsere Atmosphäre nach und nach abtragen, so wie es beim Mars passiert ist.", "Without the magnetic field from the liquid core, the solar wind would gradually strip away our atmosphere, as happened to Mars."),
            )
        },

        // ======================================= PHILOSOPHIE =======================================
        TaskDef("theseus", Topic.Philosophy, E) {
            Task(
                "theseus", Topic.Philosophy, E,
                question = l("Das Schiff des Theseus wird im Hafen aufbewahrt. Jedes Jahr wird eine morsche Planke ersetzt, bis irgendwann keine einzige ursprüngliche mehr übrig ist. Ist es noch dasselbe Schiff?", "The Ship of Theseus is kept in the harbour. Every year a rotten plank is replaced, until eventually not a single original one is left. Is it still the same ship?"),
                steps = listOf(
                    choice(
                        l("Um welche Frage geht es in diesem Gedankenexperiment eigentlich?", "What question is this thought experiment really about?"),
                        listOf(l("Was macht etwas über die Zeit hinweg zu demselben Ding?", "What makes something the same thing over time?"), l("Wie lange Holz im Wasser hält", "How long wood lasts in water"), l("Wem das Schiff rechtlich gehört", "Who legally owns the ship")),
                        done = l("Identität über die Zeit", "Identity over time"),
                        hints = listOf(l("Es geht nicht um Schiffe.", "It isn't about ships."), l("Du bestehst heute auch aus anderen Atomen als vor zehn Jahren.", "You too are made of different atoms than ten years ago.")),
                    ),
                ),
                solution = listOf(l("Es geht um Identität: Ist ein Ding sein Material, seine Form, seine Geschichte oder das, was wir ihm zuschreiben?", "It's about identity: is a thing its material, its form, its history, or what we attribute to it?")),
                background = l(
                    "Der Grieche Plutarch erzählte die Geschichte vor fast 2000 Jahren. Thomas Hobbes machte sie noch kniffliger: Was, wenn jemand die alten Planken aufhebt und daraus ein zweites Schiff baut? Welches ist dann das echte? Es gibt mehrere Antworten: Für manche zählt das Material, für andere die ununterbrochene Geschichte, für wieder andere ist „dasselbe“ nur eine praktische Abmachung unserer Sprache. Dieselbe Frage stellt sich bei Personen: Bist du derselbe Mensch wie mit fünf Jahren?",
                    "The Greek Plutarch told the story almost 2000 years ago. Thomas Hobbes made it even trickier: what if someone keeps the old planks and builds a second ship from them? Which one is the real one? There are several answers: for some the material counts, for others the unbroken history, for others again \"the same\" is just a practical convention of our language. The same question applies to people: are you the same person as when you were five?",
                ),
                fact = l("Ein großer Teil der Atome in deinem Körper wird innerhalb weniger Jahre ausgetauscht. Die meisten Nervenzellen in deinem Gehirn begleiten dich dagegen ein Leben lang.", "A large share of the atoms in your body is replaced within a few years. Most of the nerve cells in your brain, on the other hand, stay with you for life."),
            )
        },
        TaskDef("zenon", Topic.Philosophy, M) {
            Task(
                "zenon", Topic.Philosophy, M,
                question = l("Zenon von Elea behauptete: Der schnelle Achilles kann eine Schildkröte mit Vorsprung nie einholen. Denn wenn er dort ankommt, wo sie war, ist sie schon ein Stück weiter. Und so weiter, unendlich oft. Achilles läuft 10 m/s, die Schildkröte 1 m/s, ihr Vorsprung beträgt 100 m.", "Zeno of Elea claimed: swift Achilles can never catch a tortoise with a head start. Because when he gets to where it was, it has already moved on a bit. And so on, infinitely often. Achilles runs 10 m/s, the tortoise 1 m/s, its head start is 100 m."),
                steps = listOf(
                    num(
                        l("Nach wie vielen Sekunden holt er sie trotzdem ein?", "After how many seconds does he catch it anyway?"), 100.0 / 9, "s", rel = 0.02,
                        done = l("""nach §11{,}1\ \text{s}§""", """after §11.1\ \text{s}§"""),
                        hints = listOf(l("Pro Sekunde holt er 9 m auf.", "Every second he gains 9 m."), """§t = 100\ \text{m} : 9\ \text{m/s}§"""),
                    ),
                    num(
                        l("Wie weit ist Achilles bis dahin gelaufen?", "How far has Achilles run by then?"), 1000.0 / 9, "m", rel = 0.02,
                        done = l("""§111{,}1\ \text{m}§""", """§111.1\ \text{m}§"""),
                        hints = listOf("""§s = v \cdot t§""", l("""Oder als Reihe: §100 + 10 + 1 + 0{,}1 + \dots§""", """Or as a series: §100 + 10 + 1 + 0.1 + \dots§""")),
                    ),
                    choice(
                        l("Wo steckt der Denkfehler?", "Where is the flaw in the reasoning?"),
                        listOf(l("Unendlich viele immer kürzere Abschnitte können zusammen eine endliche Zeit ergeben", "Infinitely many ever shorter segments can add up to a finite time"), l("Die Schildkröte ist in Wahrheit schneller", "The tortoise is actually faster"), l("Bewegung ist nur eine Illusion", "Motion is only an illusion")),
                        done = l("Eine unendliche Summe kann endlich sein", "An infinite sum can be finite"),
                        hints = listOf(l("Zenon zählt Schritte, aber nicht, wie lange jeder dauert.", "Zeno counts steps, but not how long each takes."), l("""§100 + 10 + 1 + 0{,}1 + \dots = 111{,}1\ldots§""", """§100 + 10 + 1 + 0.1 + \dots = 111.1\ldots§""")),
                    ),
                ),
                solution = listOf(
                    l("""§t = \dfrac{100\ \text{m}}{10\ \text{m/s} - 1\ \text{m/s}} = 11{,}1\ \text{s}, \quad s = 10\ \text{m/s} \cdot 11{,}1\ \text{s} = 111{,}1\ \text{m}§""", """§t = \dfrac{100\ \text{m}}{10\ \text{m/s} - 1\ \text{m/s}} = 11.1\ \text{s}, \quad s = 10\ \text{m/s} \cdot 11.1\ \text{s} = 111.1\ \text{m}§"""),
                    l("""Zenons Abschnitte: §100 + 10 + 1 + \dots = \dfrac{100}{1 - 0{,}1} = 111{,}1\ \text{m}§""", """Zeno's segments: §100 + 10 + 1 + \dots = \dfrac{100}{1 - 0.1} = 111.1\ \text{m}§"""),
                ),
                background = l(
                    "Zenon wollte mit seinen Paradoxien zeigen, dass Bewegung widersprüchlich ist. Über 2000 Jahre lang fanden Philosophen keine wirklich befriedigende Antwort. Erst mit dem Grenzwert-Begriff der Mathematik im 19. Jahrhundert wurde klar: Eine Summe aus unendlich vielen Gliedern kann einen endlichen Wert haben, wenn die Glieder schnell genug kleiner werden. Das nennt man eine konvergente Reihe.",
                    "With his paradoxes Zeno wanted to show that motion is contradictory. For over 2000 years philosophers found no truly satisfying answer. Only with the mathematical concept of a limit in the 19th century did it become clear: a sum of infinitely many terms can have a finite value if the terms shrink fast enough. This is called a convergent series.",
                ),
                fact = l("Manche Philosophen finden die Mathematik keine vollständige Antwort: Wie kann man unendlich viele Dinge tatsächlich nacheinander erledigen? Darüber wird bis heute gestritten.", "Some philosophers don't think the maths is a complete answer: how can you actually complete infinitely many things one after another? That is still argued about today."),
            )
        },
        TaskDef("gefangene", Topic.Philosophy, M) {
            Task(
                "gefangene", Topic.Philosophy, M,
                question = l("Zwei Komplizen werden getrennt verhört. Schweigen beide, bekommen beide 1 Jahr. Gestehen beide, bekommen beide 5 Jahre. Gesteht nur einer, kommt er frei, und der andere bekommt 10 Jahre.", "Two accomplices are questioned separately. If both stay silent, each gets 1 year. If both confess, each gets 5 years. If only one confesses, he goes free and the other gets 10 years."),
                steps = listOf(
                    choice(
                        l("Angenommen, B schweigt. Was ist für A besser?", "Suppose B stays silent. What is better for A?"),
                        listOf(l("Gestehen (0 statt 1 Jahr)", "Confess (0 instead of 1 year)"), l("Schweigen (1 statt 0 Jahre)", "Stay silent (1 instead of 0 years)")),
                        done = l("Gestehen", "Confess"),
                        hints = listOf(l("Vergleiche nur die Jahre für A.", "Compare only A's years."), l("Schweigen: 1 Jahr. Gestehen: frei.", "Silent: 1 year. Confess: free.")),
                    ),
                    choice(
                        l("Angenommen, B gesteht. Was ist für A besser?", "Suppose B confesses. What is better for A?"),
                        listOf(l("Gestehen (5 statt 10 Jahre)", "Confess (5 instead of 10 years)"), l("Schweigen (10 statt 5 Jahre)", "Stay silent (10 instead of 5 years)")),
                        done = l("Auch gestehen", "Confess as well"),
                        hints = listOf(l("Schweigen: 10 Jahre. Gestehen: 5 Jahre.", "Silent: 10 years. Confess: 5 years."), l("Egal was B tut, Gestehen ist für A besser.", "Whatever B does, confessing is better for A.")),
                    ),
                    num(
                        l("Beide denken so. Wie viele Jahre sitzen sie zusammen?", "Both think this way. How many years do they serve together?"), 10.0, l("Jahre", "years"), abs = 0.0,
                        done = l("10 Jahre, statt 2 beim Schweigen", "10 years, instead of 2 if silent"),
                        hints = listOf(l("Beide gestehen.", "Both confess."), """§5 + 5§"""),
                    ),
                ),
                solution = listOf(l("Für jeden Einzelnen ist Gestehen immer besser, egal was der andere tut. Gestehen beide, bekommen sie zusammen 10 Jahre. Hätten beide geschwiegen, wären es nur 2 gewesen.", "For each individual, confessing is always better, whatever the other does. If both confess, they get 10 years together. If both had stayed silent, it would only have been 2.")),
                background = l(
                    "Das Gefangenendilemma ist das berühmteste Modell der Spieltheorie. Es zeigt, dass Entscheidungen, die für jeden Einzelnen vernünftig sind, für alle zusammen schlecht ausgehen können. Dasselbe Muster steckt in Klimaschutz, Wettrüsten oder Überfischung: Wer allein verzichtet, verliert, aber wenn keiner verzichtet, verlieren alle. Auswege sind Vertrauen, Verträge und die Aussicht, sich wiederzubegegnen.",
                    "The prisoner's dilemma is the most famous model in game theory. It shows that decisions that are sensible for each individual can turn out badly for everyone together. The same pattern lies behind climate protection, arms races or overfishing: whoever holds back alone loses, but if nobody holds back, everyone loses. Ways out are trust, contracts and the prospect of meeting again.",
                ),
                fact = l("Robert Axelrod ließ 1980 Computerprogramme das Spiel immer wieder gegeneinander spielen. Am besten schnitt das einfachste ab, „Wie du mir, so ich dir“: erst kooperieren, dann den letzten Zug des Gegners kopieren.", "In 1980 Robert Axelrod had computer programs play the game against each other over and over. The simplest one did best, \"tit for tat\": cooperate first, then copy the opponent's last move."),
            )
        },
        TaskDef("trolley", Topic.PhilEthics, M) {
            Task(
                "trolley", Topic.PhilEthics, M,
                question = l("Eine Straßenbahn rast auf fünf Menschen zu. Du stehst an einer Weiche: Legst du sie um, stirbt stattdessen ein Mensch auf dem Nebengleis.", "A tram is racing towards five people. You are standing at a switch: if you pull it, one person on the side track dies instead."),
                steps = listOf(
                    choice(
                        l("Welche Denkrichtung würde klar sagen: umlegen, weil fünf Leben mehr wiegen als eins?", "Which school of thought would clearly say: pull it, because five lives outweigh one?"),
                        listOf(l("Utilitarismus", "Utilitarianism"), l("Kants Pflichtethik", "Kant's duty ethics"), l("Stoizismus", "Stoicism")),
                        done = l("Utilitarismus", "Utilitarianism"),
                        hints = listOf(l("Diese Ethik bewertet Handlungen nur nach ihren Folgen.", "This ethics judges actions only by their consequences."), l("„Das größte Glück der größten Zahl“", "\"The greatest happiness of the greatest number\"")),
                    ),
                    choice(
                        l("Neue Lage: Du stehst auf einer Brücke und könntest einen schweren Mann hinunterstoßen, um die Bahn zu stoppen. Die Rechnung ist gleich, fünf gegen eins. Warum lehnen das die meisten Menschen ab?", "New situation: you're on a bridge and could push a heavy man off to stop the tram. The numbers are the same, five against one. Why do most people reject this?"),
                        listOf(l("Man benutzt den Mann als bloßes Mittel, wie ein Werkzeug", "You use the man merely as a means, like a tool"), l("Es sterben dabei mehr Menschen", "More people die that way"), l("Es ist verboten, auf Brücken zu stehen", "Standing on bridges is forbidden")),
                        done = l("Ein Mensch wäre nur Mittel zum Zweck", "A person would be merely a means to an end"),
                        hints = listOf(l("An der Weiche stirbt der eine als Nebenfolge. Auf der Brücke braucht man seinen Tod, damit es klappt.", "At the switch the one dies as a side effect. On the bridge you need his death for it to work."), l("Kant: Behandle Menschen nie nur als Mittel, sondern immer auch als Zweck.", "Kant: never treat people merely as a means, but always also as an end.")),
                    ),
                ),
                solution = listOf(l("Der Utilitarismus zählt nur die Folgen und würde in beiden Fällen eingreifen. Kants Ethik verbietet, einen Menschen bloß als Mittel zu benutzen, und unterscheidet deshalb die beiden Fälle.", "Utilitarianism only counts consequences and would intervene in both cases. Kant's ethics forbids using a person merely as a means and therefore distinguishes the two cases.")),
                background = l(
                    "Das Trolley-Problem stammt von der Philosophin Philippa Foot (1967). Es trennt zwei große Denkweisen der Ethik: Konsequentialisten wie die Utilitaristen Bentham und Mill bewerten nur das Ergebnis. Deontologen wie Kant sagen, manche Handlungen sind an sich verboten, egal wie gut die Folgen wären. Heute ist das Problem überraschend praktisch: Wie soll ein selbstfahrendes Auto im Notfall entscheiden?",
                    "The trolley problem comes from the philosopher Philippa Foot (1967). It separates two major ways of thinking in ethics: consequentialists like the utilitarians Bentham and Mill judge only the outcome. Deontologists like Kant say some actions are forbidden in themselves, no matter how good the consequences. Today the problem is surprisingly practical: how should a self-driving car decide in an emergency?",
                ),
                fact = l("Im Online-Experiment „Moral Machine“ des MIT haben Millionen Menschen aus über 200 Ländern solche Dilemmata entschieden, mit deutlichen kulturellen Unterschieden.", "In MIT's online experiment \"Moral Machine\", millions of people from over 200 countries decided such dilemmas, with clear cultural differences."),
            )
        },
        TaskDef("fehlschluss", Topic.PhilArgument, M) {
            Task(
                "fehlschluss", Topic.PhilArgument, M,
                question = l("Drei Argumente, drei Fehler. Welcher steckt jeweils dahinter?", "Three arguments, three flaws. Which one is behind each?"),
                steps = listOf(
                    choice(
                        l("„Wenn es regnet, ist die Straße nass. Die Straße ist nass. Also hat es geregnet.“", "\"If it rains, the street is wet. The street is wet. So it rained.\""),
                        listOf(l("Fehlschluss: Die Straße kann auch anders nass geworden sein", "Fallacy: the street could have got wet some other way"), l("Das ist ein gültiger Schluss", "That is a valid argument"), l("Zirkelschluss", "Circular reasoning")),
                        done = l("Bejahung des Konsequens", "Affirming the consequent"),
                        hints = listOf(l("Aus „Wenn A, dann B“ folgt nicht „Wenn B, dann A“.", "\"If A then B\" does not imply \"if B then A\"."), l("Denk an einen Rasensprenger.", "Think of a lawn sprinkler.")),
                    ),
                    choice(
                        l("„Niemand konnte beweisen, dass es keine Geister gibt. Also gibt es Geister.“", "\"Nobody has been able to prove there are no ghosts. So ghosts exist.\""),
                        listOf(l("Argument aus Unwissenheit", "Argument from ignorance"), l("Falsches Dilemma", "False dilemma"), l("Strohmann", "Straw man")),
                        done = l("Argument aus Unwissenheit", "Argument from ignorance"),
                        hints = listOf(l("Dass etwas nicht widerlegt ist, macht es noch nicht wahr.", "Something not being disproved doesn't make it true."), l("Wer etwas behauptet, muss es belegen.", "Whoever makes a claim has to back it up.")),
                    ),
                    choice(
                        l("„Ein berühmter Fußballer sagt, dieses Vitamin-Pulver heilt Erkältungen. Also stimmt das.“", "\"A famous footballer says this vitamin powder cures colds. So it's true.\""),
                        listOf(l("Falsches Autoritätsargument", "False appeal to authority"), l("Argument aus Unwissenheit", "Argument from ignorance"), l("Kein Fehler, er ist ja berühmt", "No flaw, he's famous after all")),
                        done = l("Falsches Autoritätsargument", "False appeal to authority"),
                        hints = listOf(l("Ist er Experte für Medizin?", "Is he an expert in medicine?"), l("Bekanntheit ersetzt keine Fachkenntnis.", "Fame doesn't replace expertise.")),
                    ),
                ),
                solution = listOf(l("1. Bejahung des Konsequens (Hinterglied). 2. Argument aus Unwissenheit. 3. Autoritätsargument ohne echte Fachautorität.", "1. Affirming the consequent. 2. Argument from ignorance. 3. Appeal to an authority without real expertise.")),
                background = l(
                    "Die Logik untersucht, wann ein Schluss gültig ist: wann aus wahren Voraussetzungen zwingend eine wahre Folgerung kommt. Fehlschlüsse klingen oft überzeugend, weil sie einem gültigen Muster ähneln. Schon Aristoteles hat sie gesammelt. Wer sie erkennt, fällt seltener auf Werbung, Verschwörungsmythen und schlechte Debatten herein.",
                    "Logic studies when an inference is valid: when true premises necessarily lead to a true conclusion. Fallacies often sound convincing because they resemble a valid pattern. Aristotle already collected them. Whoever recognises them is fooled less often by advertising, conspiracy myths and bad debates.",
                ),
                fact = l("Die gültige Variante des ersten Arguments heißt Modus ponens: „Wenn A, dann B. A. Also B.“", "The valid version of the first argument is called modus ponens: \"If A then B. A. So B.\""),
            )
        },
        TaskDef("laplace", Topic.PhilMind, H) {
            val days = 2 * log2(100.0)
            Task(
                "laplace", Topic.PhilMind, H,
                question = l("Pierre-Simon Laplace stellte sich 1814 einen Dämon vor, der Ort und Geschwindigkeit aller Teilchen kennt. Für ihn wäre die gesamte Zukunft vorhersagbar. Ist die Welt so berechenbar?", "In 1814 Pierre-Simon Laplace imagined a demon that knows the position and velocity of every particle. To it the entire future would be predictable. Is the world that calculable?"),
                steps = listOf(
                    num(
                        l("In einem Wettermodell verdoppelt sich ein Messfehler alle 2 Tage. Am Anfang ist die Temperatur auf 0,1 °C genau bekannt. Nach wie vielen Tagen ist der Fehler auf 10 °C angewachsen?", "In a weather model a measurement error doubles every 2 days. At the start the temperature is known to within 0.1 °C. After how many days has the error grown to 10 °C?"), days, l("Tage", "days"), rel = 0.04,
                        done = l("nach gut 13 Tagen", "after just over 13 days"),
                        hints = listOf(l("""Der Fehler wächst um den Faktor 100: §2^n = 100§ Verdopplungen.""", """The error grows by a factor of 100: §2^n = 100§ doublings."""), l("""§n = \log_2 100 \approx 6{,}6§, jede dauert 2 Tage""", """§n = \log_2 100 \approx 6.6§, each takes 2 days""")),
                        formula = card(l("Exponentielles Wachstum", "Exponential growth"), """§F(t) = F_0 \cdot 2^{t/T}§""", l("""§T§ ist die Verdopplungszeit.""", """§T§ is the doubling time.""")),
                    ),
                    choice(
                        l("Was folgt daraus für den Dämon?", "What does this mean for the demon?"),
                        listOf(l("Er müsste alle Anfangswerte unendlich genau kennen", "It would have to know all initial values infinitely precisely"), l("Er bräuchte nur einen schnelleren Computer", "It would only need a faster computer"), l("Nichts, Chaos ist reiner Zufall", "Nothing, chaos is pure chance")),
                        done = l("Unendliche Genauigkeit nötig", "Infinite precision needed"),
                        hints = listOf(l("Chaotische Systeme gehorchen festen Gesetzen, verstärken aber kleinste Unterschiede.", "Chaotic systems obey fixed laws but amplify the tiniest differences."), l("Jede Messung hat einen Fehler.", "Every measurement has an error.")),
                    ),
                    choice(
                        l("Welche Theorie stellt den Dämon nach ihrer üblichen Deutung grundsätzlich in Frage, auch mit perfekten Messungen?", "Which theory, in its usual interpretation, fundamentally challenges the demon, even with perfect measurements?"),
                        listOf(l("Die Quantenmechanik: Manche Ereignisse lassen sich nur mit Wahrscheinlichkeiten vorhersagen", "Quantum mechanics: some events can only be predicted as probabilities"), l("Newtons Mechanik", "Newton's mechanics"), l("Die Evolutionstheorie", "The theory of evolution")),
                        done = l("Die Quantenmechanik", "Quantum mechanics"),
                        hints = listOf(l("Denk an Unschärfe und radioaktiven Zerfall.", "Think of uncertainty and radioactive decay."), l("Wann ein einzelnes Atom zerfällt, kann niemand vorhersagen.", "Nobody can predict when a single atom will decay.")),
                    ),
                ),
                solution = listOf(
                    l("""§0{,}1\ °\text{C} \cdot 2^{n} = 10\ °\text{C} \;\Rightarrow\; n = \log_2 100 \approx 6{,}6 \;\Rightarrow\; t \approx 13{,}3§ Tage""", """§0.1\ °\text{C} \cdot 2^{n} = 10\ °\text{C} \;\Rightarrow\; n = \log_2 100 \approx 6.6 \;\Rightarrow\; t \approx 13.3§ days"""),
                ),
                background = l(
                    "Laplaces Dämon steht für den Determinismus: die Idee, dass die Gegenwart die Zukunft vollständig festlegt. Zwei Entdeckungen haben das Bild verändert. Die Chaostheorie zeigt, dass selbst streng deterministische Systeme praktisch unvorhersagbar sind, weil winzige Unterschiede exponentiell wachsen (der „Schmetterlingseffekt“). Und in der Quantenmechanik sind nach der üblichen Deutung manche Ereignisse grundsätzlich zufällig. Ob das Raum für einen freien Willen lässt, ist eine ganz andere, offene Frage.",
                    "Laplace's demon stands for determinism: the idea that the present completely fixes the future. Two discoveries changed the picture. Chaos theory shows that even strictly deterministic systems are practically unpredictable, because tiny differences grow exponentially (the \"butterfly effect\"). And in quantum mechanics, in its usual interpretation, some events are fundamentally random. Whether that leaves room for free will is a completely different, open question.",
                ),
                fact = l("Der Meteorologe Edward Lorenz entdeckte das Chaos 1961, weil er eine Zahl gerundet neu eingab: 0,506 statt 0,506127. Nach kurzer Zeit sagte sein Modell völlig anderes Wetter voraus.", "Meteorologist Edward Lorenz discovered chaos in 1961 because he re-entered a rounded number: 0.506 instead of 0.506127. After a short while his model predicted completely different weather."),
            )
        },

        // ========================================= ERDKUNDE =========================================
        TaskDef("aequator", Topic.Geography, M) {
            val days = 40_075.0 / 40
            Task(
                "aequator", Topic.Geography, M,
                question = l("Du willst einmal zu Fuß um die Erde laufen, immer am Äquator entlang (40.075 km, das Meer ignorieren wir). Du schaffst 5 km pro Stunde und läufst 8 Stunden am Tag.", "You want to walk around the Earth along the equator (40,075 km, we'll ignore the sea). You manage 5 km per hour and walk 8 hours a day."),
                steps = listOf(
                    estimate(
                        l("Schätze: Wie lange bist du unterwegs?", "Estimate: how long will you be on the road?"), days * DAY, 7 * DAY, 100 * YEAR, EstimateKind.Duration,
                        done = l("rund 1000 Tage, also fast 3 Jahre", "about 1000 days, so almost 3 years"),
                        hints = listOf(l("Pro Tag schaffst du 40 km.", "You manage 40 km per day."), """§40\,075 : 40§"""),
                    ),
                    num(
                        l("Ein Flugzeug fliegt 900 km/h ohne Pause. Wie viele Stunden braucht es?", "A plane flies 900 km/h non-stop. How many hours does it need?"), 40_075.0 / 900, "h", rel = 0.03,
                        done = l("etwa 45 Stunden", "about 45 hours"),
                        hints = listOf("""§t = s : v§""", """§40\,075 : 900§"""),
                    ),
                ),
                solution = listOf(
                    l("""§5\ \text{km/h} \cdot 8\ \text{h} = 40\ \text{km}§ pro Tag §\;\Rightarrow\; 40\,075 : 40 \approx 1000§ Tage""", """§5\ \text{km/h} \cdot 8\ \text{h} = 40\ \text{km}§ per day §\;\Rightarrow\; 40\,075 : 40 \approx 1000§ days"""),
                    """§40\,075\ \text{km} : 900\ \text{km/h} \approx 45\ \text{h}§""",
                ),
                background = l(
                    "Der Meter war ursprünglich über die Erde definiert: Ein Zehnmillionstel der Strecke vom Nordpol zum Äquator. Deshalb ist der Umfang so nah an 40.000 km. Weil sich die Erde dreht, ist sie am Äquator etwas dicker als von Pol zu Pol, etwa 43 km im Durchmesser. Der Gipfel des Chimborazo in Ecuador ist deshalb der Punkt, der am weitesten vom Erdmittelpunkt entfernt ist, nicht der Mount Everest.",
                    "The metre was originally defined using the Earth: one ten-millionth of the distance from the North Pole to the equator. That's why the circumference is so close to 40,000 km. Because the Earth rotates, it is slightly fatter at the equator than from pole to pole, by about 43 km in diameter. That's why the summit of Chimborazo in Ecuador, not Mount Everest, is the point furthest from the Earth's centre.",
                ),
                fact = l("Die ISS schafft dieselbe Strecke, sogar etwas mehr, in 92 Minuten.", "The ISS covers the same distance, even a bit more, in 92 minutes."),
            )
        },
        TaskDef("sonnenzeit", Topic.Geography, M) {
            Task(
                "sonnenzeit", Topic.Geography, M,
                question = l("Berlin und Moskau haben verschiedene Zeitzonen. Wir rechnen aber mit der echten Sonnenzeit: Mittag ist, wenn die Sonne am höchsten steht.", "Berlin and Moscow are in different time zones. But here we use real solar time: noon is when the Sun is highest."),
                given = listOf(l("Berlin: 13,4° Ost. Moskau: 37,6° Ost.", "Berlin: 13.4° east. Moscow: 37.6° east.")),
                steps = listOf(
                    num(
                        l("Um wie viel Grad dreht sich die Erde pro Stunde?", "By how many degrees does the Earth turn per hour?"), 15.0, "°", abs = 0.0,
                        done = "15°",
                        hints = listOf(l("Eine volle Drehung (360°) dauert 24 Stunden.", "One full turn (360°) takes 24 hours."), """§360 : 24§"""),
                    ),
                    num(
                        l("Um wie viele Minuten früher steht die Sonne in Moskau am höchsten?", "How many minutes earlier is the Sun highest in Moscow?"), (37.6 - 13.4) * 4, "min", rel = 0.03,
                        done = l("etwa 97 Minuten früher", "about 97 minutes earlier"),
                        hints = listOf(l("15° pro 60 Minuten heißt: 1° pro 4 Minuten.", "15° per 60 minutes means 1° per 4 minutes."), l("""§(37{,}6 - 13{,}4) \cdot 4§""", """§(37.6 - 13.4) \cdot 4§""")),
                    ),
                ),
                solution = listOf(l("""§360° : 24\ \text{h} = 15°/\text{h} = 1°\ \text{pro}\ 4\ \text{min}§, also §24{,}2 \cdot 4 \approx 97\ \text{min}§""", """§360° : 24\ \text{h} = 15°/\text{h} = 1°\ \text{per}\ 4\ \text{min}§, so §24.2 \cdot 4 \approx 97\ \text{min}§""")),
                background = l(
                    "Bis ins 19. Jahrhundert hatte jede Stadt ihre eigene Zeit, gestellt nach der Sonne. Mit der Eisenbahn wurde das zum Chaos, weil jeder Bahnhof eine andere Uhrzeit hatte. 1884 einigte man sich auf 24 Zeitzonen von je etwa 15°, gezählt ab dem Nullmeridian in Greenwich. Innerhalb einer Zeitzone weicht die Sonnenzeit deshalb um bis zu einer halben Stunde oder mehr ab: In Görlitz geht die Sonne über eine halbe Stunde früher auf als in Aachen.",
                    "Until the 19th century every town had its own time, set by the Sun. With the railways this became chaos, because every station had a different time. In 1884 the world agreed on 24 time zones of about 15° each, counted from the prime meridian in Greenwich. Within one time zone, solar time can therefore differ by half an hour or more: in Görlitz the Sun rises over half an hour earlier than in Aachen.",
                ),
                fact = l("China ist rund 60 Längengrade breit, hat aber nur eine einzige Zeitzone. Im Westen geht die Sonne deshalb im Winter erst gegen 10 Uhr auf.", "China spans about 60 degrees of longitude but has only one time zone. In the west, the Sun therefore doesn't rise until around 10 am in winter."),
            )
        },

        // ========================================= BIOLOGIE =========================================
        TaskDef("herzschlag", Topic.Biology, E) {
            val beats = 70.0 * 60 * 24 * 365.25 * 80
            Task(
                "herzschlag", Topic.Biology, E,
                question = l("Dein Herz schlägt in Ruhe etwa 70-mal pro Minute. Wie oft schlägt es in einem 80 Jahre langen Leben?", "At rest your heart beats about 70 times a minute. How often does it beat in an 80-year life?"),
                steps = listOf(
                    estimate(
                        l("Schätze die Zahl der Herzschläge", "Estimate the number of heartbeats"), beats, 1e6, 1e12, EstimateKind.Count,
                        done = l("knapp 3 Milliarden", "almost 3 billion"),
                        hints = listOf(l("Pro Tag sind es etwa 100.000.", "That's about 100,000 per day."), """§100\,000 \cdot 365 \cdot 80§"""),
                    ),
                ),
                solution = listOf("""§70 \cdot 60 \cdot 24 \cdot 365 \cdot 80 \approx 2{,}9 \cdot 10^{9}§""".let { l(it, it.replace("2{,}9", "2.9")) }),
                background = l(
                    "Das Herz ist ein Muskel, der nie Pause macht. Er hat einen eigenen Taktgeber, den Sinusknoten, der elektrische Impulse erzeugt. Darum schlägt ein Herz sogar außerhalb des Körpers noch eine Weile weiter. Bei jedem Schlag pumpt es etwa 70 ml Blut, am Tag also rund 7000 Liter, genug, um eine kleine Badewanne über 40-mal zu füllen.",
                    "The heart is a muscle that never takes a break. It has its own pacemaker, the sinoatrial node, which produces electrical impulses. That's why a heart keeps beating for a while even outside the body. With every beat it pumps about 70 ml of blood, so about 7000 litres a day, enough to fill a small bathtub more than 40 times.",
                ),
                fact = l("Viele Säugetiere kommen auf ungefähr eine Milliarde Herzschläge im Leben: Mäuse schlagen schnell und leben kurz, Elefanten langsam und lange. Der Mensch liegt deutlich darüber.", "Many mammals get roughly one billion heartbeats in a lifetime: mice beat fast and live briefly, elephants slowly and long. Humans are well above that."),
            )
        },
        TaskDef("dna", Topic.Biology, M) {
            val ratio = 2.0 * 3.7e13 / 1.5e11
            Task(
                "dna", Topic.Biology, M,
                question = l("In jeder deiner Zellen steckt DNA mit einer Länge von etwa 2 Metern. Dein Körper hat rund 37 Billionen Zellen.", "Every one of your cells contains DNA about 2 metres long. Your body has about 37 trillion cells."),
                given = listOf(l("""Erde–Sonne: §1{,}5 \cdot 10^{11}\ \text{m}§""", """Earth–Sun: §1.5 \cdot 10^{11}\ \text{m}§""")),
                steps = listOf(
                    num(
                        l("Wie oft würde all deine DNA aneinandergelegt von der Erde bis zur Sonne reichen?", "How many times would all your DNA laid end to end reach from the Earth to the Sun?"), ratio, "", rel = 0.04,
                        done = l("etwa 500-mal", "about 500 times"),
                        hints = listOf(l("""Gesamtlänge: §2\ \text{m} \cdot 3{,}7 \cdot 10^{13}§""", """Total length: §2\ \text{m} \cdot 3.7 \cdot 10^{13}§"""), l("""§7{,}4 \cdot 10^{13} : 1{,}5 \cdot 10^{11}§""", """§7.4 \cdot 10^{13} : 1.5 \cdot 10^{11}§""")),
                    ),
                    choice(
                        l("Wie passen 2 m DNA in einen Zellkern mit 6 µm Durchmesser?", "How do 2 m of DNA fit into a cell nucleus 6 µm across?"),
                        listOf(l("Sie ist um Proteine (Histone) gewickelt und mehrfach gefaltet", "It is wound around proteins (histones) and folded several times"), l("Sie ist in viele kurze Stücke zerschnitten, die durcheinander liegen", "It is cut into many short pieces lying in a jumble"), l("Nur eine von tausend Zellen hat DNA", "Only one cell in a thousand has DNA")),
                        done = l("Aufgewickelt und gefaltet", "Wound and folded"),
                        hints = listOf(l("Denk an ein Garnknäuel, das selbst wieder aufgewickelt ist.", "Think of a ball of yarn that is itself wound up again."), l("Diese Verpackung heißt Chromatin.", "This packaging is called chromatin.")),
                    ),
                ),
                solution = listOf(l("""§2\ \text{m} \cdot 3{,}7 \cdot 10^{13} = 7{,}4 \cdot 10^{13}\ \text{m} \;\Rightarrow\; 7{,}4 \cdot 10^{13} : 1{,}5 \cdot 10^{11} \approx 490§""", """§2\ \text{m} \cdot 3.7 \cdot 10^{13} = 7.4 \cdot 10^{13}\ \text{m} \;\Rightarrow\; 7.4 \cdot 10^{13} : 1.5 \cdot 10^{11} \approx 490§""")),
                background = l(
                    "Die DNA ist eine Doppelhelix, nur 2 Nanometer dick, aber unglaublich lang. In ihr ist mit vier Buchstaben (A, T, G, C) der Bauplan für deinen Körper gespeichert, gut 3 Milliarden Buchstaben pro Satz. Damit sie in den Zellkern passt, wickelt die Zelle sie um Proteinkugeln wie um Spulen und faltet diese Kette immer wieder. Trotzdem bleibt sie lesbar: Die gerade gebrauchten Abschnitte werden gezielt ausgepackt. Kleine Einschränkung zur Rechnung: Rote Blutkörperchen haben keinen Kern und keine DNA.",
                    "DNA is a double helix only 2 nanometres thick but incredibly long. Using four letters (A, T, G, C) it stores the blueprint for your body, over 3 billion letters per set. To fit into the nucleus, the cell winds it around protein balls like spools and folds this chain again and again. It still stays readable: the sections needed right now are unpacked on purpose. One caveat about the calculation: red blood cells have no nucleus and no DNA.",
                ),
                fact = l("Das Verhältnis entspricht etwa 20 Kilometern Faden in einem Tennisball, ordentlich verpackt und jederzeit lesbar.", "The ratio is like about 20 kilometres of thread in a tennis ball, neatly packed and readable at any time."),
            )
        },

        // ========================================== CHEMIE ==========================================
        TaskDef("schluck", Topic.Chemistry, M) {
            Task(
                "schluck", Topic.Chemistry, M,
                question = l("""Wie viele Wassermoleküle sind in einem Schluck Wasser (18 ml)?""", """How many water molecules are in a sip of water (18 ml)?"""),
                given = listOf(l("""Molare Masse von Wasser: §18\ \text{g/mol}§. Ein Mol sind §6{,}02 \cdot 10^{23}§ Teilchen.""", """Molar mass of water: §18\ \text{g/mol}§. One mole is §6.02 \cdot 10^{23}§ particles.""")),
                steps = listOf(
                    estimate(
                        l("Schätze die Zahl der Moleküle", "Estimate the number of molecules"), 6.02e23, 1e10, 1e30, EstimateKind.Count, factor = 3.0,
                        done = l("""§6 \cdot 10^{23}§, genau ein Mol""", """§6 \cdot 10^{23}§, exactly one mole"""),
                        hints = listOf(l("18 ml Wasser wiegen 18 g.", "18 ml of water weighs 18 g."), l("18 g Wasser sind genau ein Mol.", "18 g of water is exactly one mole.")),
                        formula = card(l("Stoffmenge", "Amount of substance"), """§n = \dfrac{m}{M},\quad N = n \cdot N_A§""", l("""§N_A = 6{,}02 \cdot 10^{23}§ pro Mol (Avogadro-Konstante)""", """§N_A = 6.02 \cdot 10^{23}§ per mole (Avogadro constant)""")),
                    ),
                ),
                solution = listOf(l("""§n = \dfrac{18\ \text{g}}{18\ \text{g/mol}} = 1\ \text{mol} \;\Rightarrow\; N = 6{,}02 \cdot 10^{23}§""", """§n = \dfrac{18\ \text{g}}{18\ \text{g/mol}} = 1\ \text{mol} \;\Rightarrow\; N = 6.02 \cdot 10^{23}§""")),
                background = l(
                    "Atome und Moleküle sind so winzig, dass Chemiker sie in Paketen zählen, so wie man Eier im Dutzend kauft. Ein solches Paket heißt Mol und enthält 602 Trilliarden Teilchen. Der Trick: Die Masse eines Mols in Gramm entspricht genau der Molekülmasse in atomaren Einheiten. Wasser (H₂O) hat 2 · 1 + 16 = 18, also wiegt ein Mol Wasser 18 Gramm.",
                    "Atoms and molecules are so tiny that chemists count them in packs, like buying eggs by the dozen. Such a pack is called a mole and contains 602 sextillion particles. The trick: the mass of one mole in grams equals the molecular mass in atomic units. Water (H₂O) has 2 · 1 + 16 = 18, so one mole of water weighs 18 grams.",
                ),
                fact = l("In einem Glas Wasser sind mehr Moleküle, als alle Ozeane der Erde Gläser voll Wasser enthalten.", "There are more molecules in a glass of water than there are glasses of water in all the Earth's oceans."),
            )
        },
        TaskDef("knallgas", Topic.Chemistry, M) {
            val mol = 1000.0 / 18
            Task(
                "knallgas", Topic.Chemistry, M,
                question = l("Wasserstoff verbrennt mit Sauerstoff zu Wasser, so wie in einer Brennstoffzelle. Wie viel Wasserstoffgas braucht man für einen Liter Wasser?", "Hydrogen burns with oxygen to form water, just like in a fuel cell. How much hydrogen gas do you need for one litre of water?"),
                given = listOf(l("""Molare Masse Wasser: §18\ \text{g/mol}§. Ein Mol Gas hat bei 0 °C ein Volumen von §22{,}4\ \text{L}§.""", """Molar mass of water: §18\ \text{g/mol}§. One mole of gas at 0 °C has a volume of §22.4\ \text{L}§.""")),
                steps = listOf(
                    choice(
                        l("Welche Reaktionsgleichung stimmt?", "Which reaction equation is right?"),
                        listOf("""§2\,\text{H}_2 + \text{O}_2 \to 2\,\text{H}_2\text{O}§""", """§\text{H}_2 + \text{O}_2 \to \text{H}_2\text{O}§""", """§\text{H} + \text{O} \to \text{HO}§"""),
                        done = """§2\,\text{H}_2 + \text{O}_2 \to 2\,\text{H}_2\text{O}§""",
                        hints = listOf(l("Links und rechts müssen gleich viele Atome jeder Sorte stehen.", "There must be the same number of each kind of atom on both sides."), l("Wasserstoff und Sauerstoff kommen als Moleküle aus zwei Atomen vor.", "Hydrogen and oxygen occur as molecules of two atoms.")),
                    ),
                    num(
                        l("Wie viel Mol Wasser sind ein Liter (1000 g)?", "How many moles of water are one litre (1000 g)?"), mol, "mol", rel = 0.02,
                        done = l("""§55{,}6\ \text{mol}§""", """§55.6\ \text{mol}§"""),
                        hints = listOf("""§n = m : M§""", """§1000 : 18§"""),
                    ),
                    num(
                        l("Wie viele Liter Wasserstoffgas (bei 0 °C) braucht man dafür?", "How many litres of hydrogen gas (at 0 °C) does that need?"), mol * 22.4, "L", rel = 0.03,
                        done = l("etwa 1240 L", "about 1240 L"),
                        hints = listOf(l("Laut Gleichung gibt ein H₂-Molekül ein H₂O-Molekül.", "According to the equation, one H₂ molecule gives one H₂O molecule."), l("""§55{,}6 \cdot 22{,}4\ \text{L}§""", """§55.6 \cdot 22.4\ \text{L}§""")),
                    ),
                ),
                solution = listOf(
                    """§2\,\text{H}_2 + \text{O}_2 \to 2\,\text{H}_2\text{O}§""",
                    l("""§n = \dfrac{1000\ \text{g}}{18\ \text{g/mol}} \approx 55{,}6\ \text{mol} \;\Rightarrow\; V = 55{,}6 \cdot 22{,}4\ \text{L} \approx 1240\ \text{L}§""", """§n = \dfrac{1000\ \text{g}}{18\ \text{g/mol}} \approx 55.6\ \text{mol} \;\Rightarrow\; V = 55.6 \cdot 22.4\ \text{L} \approx 1240\ \text{L}§"""),
                ),
                background = l(
                    "Ein Gemisch aus Wasserstoff und Sauerstoff heißt Knallgas, weil es schon durch einen Funken explosionsartig reagiert. Dabei wird viel Energie frei. Eine Brennstoffzelle nutzt dieselbe Reaktion, lässt sie aber kontrolliert ablaufen und gewinnt daraus direkt Strom. Gase brauchen viel Platz: Aus über 1200 Litern Gas wird nur ein einziger Liter Wasser. Deshalb wird Wasserstoff für Autos stark zusammengepresst.",
                    "A mixture of hydrogen and oxygen is called oxyhydrogen because a single spark makes it react explosively. A lot of energy is released. A fuel cell uses the same reaction but runs it in a controlled way and turns it directly into electricity. Gases take up a lot of space: more than 1200 litres of gas become just one litre of water. That's why hydrogen for cars is heavily compressed.",
                ),
                fact = l("Die Haupttriebwerke des Space Shuttles verbrannten flüssigen Wasserstoff mit flüssigem Sauerstoff. Ihr Abgas war fast nur heißer Wasserdampf.", "The Space Shuttle's main engines burned liquid hydrogen with liquid oxygen. Their exhaust was almost entirely hot water vapour."),
            )
        },

        // ====================================== ALLGEMEINWISSEN ======================================
        TaskDef("papier", Topic.Knowledge, M) {
            val h = 1e-4 * 2.0.pow(42)
            Task(
                "papier", Topic.Knowledge, M,
                question = l("Ein Blatt Papier ist 0,1 mm dick. Angenommen, du könntest es beliebig oft in der Mitte falten: Wie dick wäre der Stapel nach 42-mal Falten?", "A sheet of paper is 0.1 mm thick. Suppose you could fold it in half as often as you like: how thick would the stack be after 42 folds?"),
                steps = listOf(
                    estimate(
                        l("Schätze die Dicke", "Estimate the thickness"), h, 1.0, 1e12, EstimateKind.Length, factor = 3.0,
                        done = l("rund 440.000 km", "about 440,000 km"),
                        hints = listOf(l("Jedes Falten verdoppelt die Dicke.", "Every fold doubles the thickness."), l("""§2^{10} \approx 1000§, also §2^{42} \approx 4 \cdot 10^{12}§""", """§2^{10} \approx 1000§, so §2^{42} \approx 4 \cdot 10^{12}§""")),
                    ),
                    choice(
                        l("Der Mond ist 384.000 km entfernt. Reicht der Stapel bis dorthin?", "The Moon is 384,000 km away. Does the stack reach it?"),
                        listOf(l("Ja, sogar darüber hinaus", "Yes, even beyond it"), l("Nein, nur bis zur ISS", "No, only up to the ISS"), l("Nein, nur ein paar Kilometer", "No, just a few kilometres")),
                        done = l("Ja, darüber hinaus", "Yes, beyond it"),
                        hints = listOf(l("Vergleiche mit deiner Schätzung.", "Compare with your estimate."), l("""§10^{-4}\ \text{m} \cdot 4{,}4 \cdot 10^{12} = 4{,}4 \cdot 10^{8}\ \text{m}§""", """§10^{-4}\ \text{m} \cdot 4.4 \cdot 10^{12} = 4.4 \cdot 10^{8}\ \text{m}§""")),
                    ),
                ),
                solution = listOf(l("""§0{,}1\ \text{mm} \cdot 2^{42} = 10^{-4}\ \text{m} \cdot 4{,}4 \cdot 10^{12} \approx 4{,}4 \cdot 10^{8}\ \text{m} = 440\,000\ \text{km}§""", """§0.1\ \text{mm} \cdot 2^{42} = 10^{-4}\ \text{m} \cdot 4.4 \cdot 10^{12} \approx 4.4 \cdot 10^{8}\ \text{m} = 440\,000\ \text{km}§""")),
                background = l(
                    "Wiederholtes Verdoppeln ist exponentielles Wachstum, und unser Gefühl dafür ist miserabel. Wir denken linear: 42 Schritte klingen nach wenig. Aber jede Verdopplung legt so viel drauf wie alle vorherigen zusammen. Genau das macht Epidemien, Zinseszinsen und Kettenreaktionen so tückisch: Lange passiert scheinbar nichts, dann plötzlich alles auf einmal.",
                    "Repeated doubling is exponential growth, and our intuition for it is terrible. We think linearly: 42 steps sounds like very little. But each doubling adds as much as all the previous ones together. That's exactly what makes epidemics, compound interest and chain reactions so treacherous: for a long time nothing seems to happen, then suddenly everything at once.",
                ),
                fact = l("Den Rekord hält die Schülerin Britney Gallivan: 2002 faltete sie einen 1,2 km langen Papierstreifen 12-mal.", "The record is held by student Britney Gallivan: in 2002 she folded a 1.2 km long strip of paper 12 times."),
            )
        },
        TaskDef("schachbrett", Topic.Knowledge, H) {
            val years = 2.0.pow(64) * 25e-6 / 1000 / 5e8
            Task(
                "schachbrett", Topic.Knowledge, H,
                question = l("Der Legende nach wünschte sich der Erfinder des Schachspiels als Lohn: ein Reiskorn auf das erste Feld, zwei auf das zweite, vier auf das dritte und immer so weiter, auf alle 64 Felder.", "Legend has it that the inventor of chess asked for this reward: one grain of rice on the first square, two on the second, four on the third and so on, across all 64 squares."),
                given = listOf(l("Ein Reiskorn wiegt etwa 25 mg. Die Welt erntet rund 500 Millionen Tonnen Reis pro Jahr.", "A grain of rice weighs about 25 mg. The world harvests about 500 million tonnes of rice per year.")),
                steps = listOf(
                    choice(
                        l("Wie viele Körner liegen auf dem 64. Feld?", "How many grains are on the 64th square?"),
                        listOf("""§2^{63}§""", """§2^{64}§""", """§64^2§"""),
                        done = """§2^{63} \approx 9{,}2 \cdot 10^{18}§""".let { l(it, it.replace("9{,}2", "9.2")) },
                        hints = listOf(l("Feld 1: 2⁰, Feld 2: 2¹, Feld 3: 2² …", "Square 1: 2⁰, square 2: 2¹, square 3: 2² …"), l("Der Exponent ist immer eins kleiner als die Feldnummer.", "The exponent is always one less than the square number.")),
                    ),
                    num(
                        l("Insgesamt sind es fast genau 2⁶⁴ Körner. Wie viele Jahre Welt-Reisernte wären das?", "In total it's almost exactly 2⁶⁴ grains. How many years of the world rice harvest is that?"), years, l("Jahre", "years"), rel = 0.05,
                        done = l("über 900 Jahre", "over 900 years"),
                        hints = listOf(l("""§2^{64} \approx 1{,}8 \cdot 10^{19}§ Körner mal 25 mg""", """§2^{64} \approx 1.8 \cdot 10^{19}§ grains times 25 mg"""), l("""§4{,}6 \cdot 10^{14}\ \text{kg} = 4{,}6 \cdot 10^{11}\ \text{t}§, geteilt durch §5 \cdot 10^{8}\ \text{t}§""", """§4.6 \cdot 10^{14}\ \text{kg} = 4.6 \cdot 10^{11}\ \text{t}§, divided by §5 \cdot 10^{8}\ \text{t}§""")),
                        formula = card(l("Geometrische Summe", "Geometric sum"), """§1 + 2 + 4 + \dots + 2^{63} = 2^{64} - 1§""", l("Jede Zweierpotenz ist um eins größer als alle vorherigen zusammen.", "Every power of two is one more than all the previous ones together.")),
                    ),
                ),
                solution = listOf(
                    l("""§1 + 2 + \dots + 2^{63} = 2^{64} - 1 \approx 1{,}8 \cdot 10^{19}§ Körner""", """§1 + 2 + \dots + 2^{63} = 2^{64} - 1 \approx 1.8 \cdot 10^{19}§ grains"""),
                    l("""§1{,}8 \cdot 10^{19} \cdot 25\ \text{mg} \approx 4{,}6 \cdot 10^{11}\ \text{t} \;\Rightarrow\; 4{,}6 \cdot 10^{11} : 5 \cdot 10^{8} \approx 920§ Jahre""", """§1.8 \cdot 10^{19} \cdot 25\ \text{mg} \approx 4.6 \cdot 10^{11}\ \text{t} \;\Rightarrow\; 4.6 \cdot 10^{11} : 5 \cdot 10^{8} \approx 920§ years"""),
                ),
                background = l(
                    "Die Legende ist rund tausend Jahre alt und stammt aus Indien oder Persien. Sie zeigt, wie schnell Verdopplung aus winzigen Zahlen gigantische macht. Die Summe hat eine elegante Form: 1 + 2 + 4 + … + 2⁶³ ist genau 2⁶⁴ − 1. Computer rechnen übrigens mit genau diesen Zahlen: Eine 64-Bit-Zahl kann höchstens 2⁶⁴ − 1 betragen.",
                    "The legend is about a thousand years old and comes from India or Persia. It shows how fast doubling turns tiny numbers into gigantic ones. The sum has an elegant form: 1 + 2 + 4 + … + 2⁶³ is exactly 2⁶⁴ − 1. Computers, by the way, work with exactly these numbers: a 64-bit number can be at most 2⁶⁴ − 1.",
                ),
                fact = l("In manchen Fassungen der Legende ließ der König den Erfinder hinrichten, als er merkte, dass er ihn nicht bezahlen konnte.", "In some versions of the legend, the king had the inventor executed when he realised he couldn't pay him."),
            )
        },
    )
}

