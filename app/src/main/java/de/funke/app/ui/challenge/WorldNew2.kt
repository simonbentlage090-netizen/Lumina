package de.funke.app.ui.challenge

/* Ausbau (seit 13.2): Erdkunde und Geologie. Texte selbst geschrieben, Zahlen gerundet aus gängigen Nachschlagewerken. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard

object WorldNew2 {
    val all: List<TaskDef> get() = geography + geology

    private val geography = listOf(
        // ------------------------------------------------ leicht
        lab("erd.breitengrad", Topic.Geography, E, 0.35f) {
            val a = pick(54.8, 53.6, 52.5); val b = pick(47.4, 48.1, 50.1)
            val d = (a - b) * 111.2
            task(
                question = l("Zwei Orte liegen auf demselben Längengrad, der eine bei ${n(a, 1)}° Nord, der andere bei ${n(b, 1)}° Nord. Wie weit sind sie voneinander entfernt? (Erdumfang über die Pole: 40.000 km)", "Two places lie on the same line of longitude, one at ${n(a, 1)}° north, the other at ${n(b, 1)}° north. How far apart are they? (Circumference of the Earth over the poles: 40,000 km)"),
                steps = listOf(num(l("Entfernung in km", "Distance in km"), d, "km", rel = 0.02, done = """§\approx ${m(d, 0)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Ein voller Kreis über die Pole hat 360°. Wie viele Kilometer sind dann ein Grad?", "Key idea: a full circle over the poles has 360°. So how many kilometres is one degree?"), """§\dfrac{40\,000\ \text{km}}{360} \approx 111\ \text{km pro Grad}§""".let { l(it, it.replace("pro Grad", "per degree")) }))),
                solution = listOf("""§(${m(a, 1)} - ${m(b, 1)}) \cdot 111\ \text{km} \approx ${m(d, 0)}\ \text{km}§"""),
                background = l("Ein Breitengrad ist überall auf der Erde etwa 111 km lang, deshalb sind Breitenangaben ein eingebautes Maßband. Bei Längengraden ist das anders: Sie laufen an den Polen zusammen, in Deutschland ist ein Längengrad nur noch rund 70 km breit. Der Meter war ursprünglich als zehnmillionster Teil der Strecke vom Pol zum Äquator festgelegt, daher die runden 40.000 km.", "A degree of latitude is about 111 km long everywhere on Earth, so latitudes are a built-in tape measure. Longitude is different: the lines meet at the poles, and in Germany a degree of longitude is only about 70 km wide. The metre was originally defined as one ten-millionth of the distance from the pole to the equator, hence the round 40,000 km."),
            )
        },
        lab("erd.dichte", Topic.Geography, E, 0.35f) {
            val (land, pop, area) = pick(Triple(l("Deutschland", "Germany"), 84.0, 357_600.0), Triple(l("Kanada", "Canada"), 41.0, 9_985_000.0), Triple(l("Bangladesch", "Bangladesh"), 173.0, 148_500.0), Triple(l("Australien", "Australia"), 27.0, 7_690_000.0))
            val dd = pop * 1e6 / area
            task(
                question = l("$land hat rund ${n(pop, 0)} Millionen Einwohner auf ${n(area, 0)} km². Wie viele Menschen leben dort im Durchschnitt auf einem Quadratkilometer?", "$land has around ${n(pop, 0)} million inhabitants on ${n(area, 0)} km². On average, how many people live on one square kilometre there?"),
                steps = listOf(num(l("Einwohner pro km²", "People per km²"), dd, "", rel = 0.03, done = """§\approx ${m(sig(dd, 3), 0)}\ \text{pro km}^2§""".let { l(it, it.replace("pro km", "per km")) },
                    hints = listOf(l("Kernidee: Einwohner geteilt durch Fläche. Achte darauf, die Millionen auszuschreiben.", "Key idea: inhabitants divided by area. Remember to write out the millions."), """§\dfrac{${m(pop, 0)} \cdot 10^6}{${m(area, 0)}\ \text{km}^2}§"""))),
                solution = listOf("""§\dfrac{${m(pop, 0)} \cdot 10^6}{${m(area, 0)}} \approx ${m(sig(dd, 3), 0)}§"""),
                background = l("Die Bevölkerungsdichte ist ein Durchschnitt, der viel versteckt. In Kanada und Australien leben die meisten Menschen in einem schmalen Streifen an der Küste oder nahe der Grenze, das Innere ist fast leer. In Bangladesch dagegen ist fast das ganze Land dicht besiedeltes, fruchtbares Schwemmland im Delta großer Flüsse.", "Population density is an average that hides a lot. In Canada and Australia most people live in a narrow strip along the coast or near the border, and the interior is almost empty. In Bangladesh, by contrast, almost the whole country is densely settled, fertile land in the delta of great rivers."),
            )
        },
        mc("erd.suesswasser", Topic.Geography, E,
            t("Die Erde ist zu zwei Dritteln mit Wasser bedeckt. Wie viel Prozent des gesamten Wassers auf der Erde ist Süßwasser?", "Two thirds of the Earth is covered in water. What percentage of all the water on Earth is fresh water?"),
            t("Anteil Süßwasser", "Share of fresh water"),
            t("Etwa 2,5 %", "About 2.5%"),
            listOf(t("Etwa 25 %", "About 25%"), t("Etwa 50 %", "About 50%"), t("Etwa 10 %", "About 10%")),
            t("Kernidee: Denk an die Ozeane. Wie viel Wasser steckt in ihnen im Vergleich zu Flüssen und Seen?", "Key idea: think of the oceans. How much water do they hold compared with rivers and lakes?"),
            t("Die Ozeane sind im Mittel fast 4 km tief.", "The oceans are almost 4 km deep on average."),
            t("Rund 97,5 % sind Salzwasser. Vom Süßwasser sind etwa zwei Drittel als Eis gebunden, fast der ganze Rest steckt im Grundwasser.", "Around 97.5% is salt water. Of the fresh water, about two thirds is locked up as ice, and almost all the rest is groundwater."),
            t("Flüsse und Seen, aus denen wir trinken, machen weniger als ein Hundertstel des Süßwassers aus. Wäre alles Wasser der Erde ein 10-Liter-Eimer, wären die Flüsse und Seen nicht einmal ein Teelöffel. Trotzdem reicht es, weil der Wasserkreislauf das Süßwasser ständig erneuert: Die Sonne destilliert jedes Jahr Hunderttausende Kubikkilometer aus dem Meer.", "The rivers and lakes we drink from make up less than a hundredth of the fresh water. If all the water on Earth were a 10-litre bucket, the rivers and lakes wouldn't even be a teaspoon. Yet it's enough, because the water cycle constantly renews the fresh water: every year the Sun distils hundreds of thousands of cubic kilometres from the sea."),
        ),
        lab("erd.niederschlag", Topic.Geography, E, 0.3f) {
            val mm = pick(12, 25, 8, 40); val area = pick(50, 120, 80)
            val litres = mm * area
            task(
                question = l("Der Wetterbericht meldet ${mm} mm Regen. Wie viele Liter Wasser fallen auf einen Garten mit ${area} m²?", "The weather report gives ${mm} mm of rain. How many litres of water fall on a garden of ${area} m²?"),
                steps = listOf(num(l("Liter", "Litres"), litres.toDouble(), "l", abs = 0.0, done = """§${m(litres)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: 1 mm Wasser auf 1 m² ist ein Würfel von 1 m × 1 m × 1 mm. Wie viel Liter sind das?", "Key idea: 1 mm of water on 1 m² is a block of 1 m × 1 m × 1 mm. How many litres is that?"), l("1 mm Niederschlag = 1 Liter pro Quadratmeter.", "1 mm of rain = 1 litre per square metre.")))),
                solution = listOf("""§${mm}\ \tfrac{\text{l}}{\text{m}^2} \cdot ${area}\ \text{m}^2 = ${m(litres)}\ \text{l}§"""),
                background = l("Regenmengen werden in Millimetern angegeben, weil das unabhängig von der Fläche ist: So hoch würde das Wasser stehen, wenn nichts versickert oder abfließt. In Deutschland fallen im Jahr etwa 800 mm, also 800 Liter auf jeden Quadratmeter. In der Sahara sind es oft weniger als 20 mm, an manchen Orten in Indien über 11.000 mm.", "Rainfall is given in millimetres because that doesn't depend on the area: it's how high the water would stand if none soaked away or ran off. Germany gets about 800 mm a year, i.e. 800 litres on every square metre. The Sahara often gets less than 20 mm, some places in India over 11,000 mm."),
            )
        },
        lab("erd.salzgehalt", Topic.Geography, E, 0.35f) {
            val (sea, gkg) = pick(l("der Nordsee", "the North Sea") to 35.0, l("der Ostsee vor Rügen", "the western Baltic") to 8.0, l("dem Toten Meer", "the Dead Sea") to 340.0)
            val salt = gkg * 10
            task(
                question = l("Du lässt einen Eimer mit 10 kg Wasser aus $sea in der Sonne verdunsten. Ein Kilogramm Meerwasser enthält dort etwa ${n(gkg, 0)} g Salz. Wie viel Salz bleibt im Eimer zurück?", "You let a bucket with 10 kg of water from $sea evaporate in the sun. One kilogram of seawater there contains about ${n(gkg, 0)} g of salt. How much salt is left in the bucket?"),
                steps = listOf(num(l("Salz in Gramm", "Salt in grams"), salt, "g", rel = 0.01, done = """§${m(salt, 0)}\ \text{g}§""",
                    hints = listOf(l("Kernidee: Beim Verdunsten geht nur das Wasser, das Salz bleibt komplett da.", "Key idea: when water evaporates only the water leaves; all the salt stays behind."), """§10 \cdot ${m(gkg, 0)}\ \text{g}§"""))),
                solution = listOf("""§10\ \text{kg} \cdot ${m(gkg, 0)}\ \tfrac{\text{g}}{\text{kg}} = ${m(salt, 0)}\ \text{g}§"""),
                background = l("Flüsse bringen ständig gelöste Minerale ins Meer, und die Sonne verdunstet nur das Wasser. Deshalb ist das Meer salzig. Die Ostsee ist fast ein Binnensee mit vielen Flüssen und nur einer engen Verbindung zur Nordsee, ihr Wasser ist brackig. Das Tote Meer hat gar keinen Abfluss und liegt in einer heißen Senke, es verdunstet ständig und ist zehnmal salziger als der Ozean.", "Rivers constantly carry dissolved minerals into the sea, and the Sun only evaporates the water. That's why the sea is salty. The Baltic is almost an inland sea with many rivers and only a narrow link to the North Sea, so its water is brackish. The Dead Sea has no outflow at all and lies in a hot basin; it evaporates constantly and is ten times saltier than the ocean."),
            )
        },
        lab("erd.koordinaten", Topic.Geography, E, 0.4f) {
            val cities = listOf(
                Triple(l("Sydney", "Sydney"), l("34° S, 151° O", "34° S, 151° E"), 0),
                Triple(l("Buenos Aires", "Buenos Aires"), l("35° S, 58° W", "35° S, 58° W"), 1),
                Triple(l("Kapstadt", "Cape Town"), l("34° S, 18° O", "34° S, 18° E"), 2),
                Triple(l("Los Angeles", "Los Angeles"), l("34° N, 118° W", "34° N, 118° W"), 3),
                Triple(l("Tokio", "Tokyo"), l("36° N, 140° O", "36° N, 140° E"), 4),
            )
            val c = pick(cities); val wrong = cities.filter { it !== c }.shuffled(rnd).take(3).map { it.first }
            task(
                question = l("Eine Großstadt liegt bei ${c.second}. Welche ist es?", "A big city lies at ${c.second}. Which one is it?"),
                steps = listOf(pick1(l("Stadt", "City"), c.first, wrong,
                    hints = listOf(l("Kernidee: N oder S sagt dir, ob nördlich oder südlich des Äquators. O oder W sagt, ob östlich oder westlich von Greenwich bei London.", "Key idea: N or S tells you whether it's north or south of the equator. E or W tells you whether it's east or west of Greenwich in London."), l("Alle Städte zur Auswahl liegen ungefähr auf 34° bis 36° Breite, nur auf verschiedenen Halbkugeln und Längen.", "All the cities on offer lie at roughly 34° to 36° latitude, just in different hemispheres and longitudes.")))),
                solution = listOf(l("${c.second} ist ${c.first}. Die anderen liegen auf fast derselben Breite, aber auf einer anderen Halbkugel oder Länge.", "${c.second} is ${c.first}. The others lie at almost the same latitude, but in a different hemisphere or longitude.")),
                background = l("Mit zwei Zahlen lässt sich jeder Punkt der Erde festlegen. Die Breite zählt vom Äquator aus bis 90° zu den Polen, die Länge vom Nullmeridian in Greenwich bis 180° nach Osten und Westen. Erstaunlich: Sydney, Buenos Aires, Kapstadt und Los Angeles liegen fast genau gleich weit vom Äquator entfernt und haben deshalb ein ähnlich mildes Klima.", "Two numbers are enough to pin down any point on Earth. Latitude counts from the equator up to 90° at the poles, longitude from the prime meridian at Greenwich up to 180° east and west. Remarkably, Sydney, Buenos Aires, Cape Town and Los Angeles are almost exactly the same distance from the equator, and so they have a similarly mild climate."),
            )
        },
        mc("erd.stadtklima", Topic.Geography, E,
            t("In einer Sommernacht ist es mitten in einer Großstadt oft mehrere Grad wärmer als im Umland. Was ist der wichtigste Grund?", "On a summer night it's often several degrees warmer in the middle of a big city than in the surrounding countryside. What is the main reason?"),
            t("Grund", "Reason"),
            t("Stein, Beton und Asphalt speichern tagsüber Wärme und geben sie nachts ab, und es verdunstet wenig Wasser.", "Stone, concrete and asphalt store heat during the day and release it at night, and little water evaporates."),
            listOf(t("Die Körperwärme der vielen Menschen heizt die Luft auf.", "The body heat of all the people warms the air."), t("Hochhäuser bündeln das Sonnenlicht wie eine Lupe.", "Tall buildings focus sunlight like a magnifying glass."), t("Über Städten gibt es mehr Wolken, die wie eine Decke wirken.", "There are more clouds over cities, acting like a blanket.")),
            t("Kernidee: Was passiert mit einer Steinmauer, die den ganzen Tag in der Sonne stand, nach Sonnenuntergang?", "Key idea: what happens to a stone wall that has been in the sun all day after sunset?"),
            t("Und was kühlt eine Wiese oder einen Wald ab, das in der Stadt fehlt?", "And what cools a meadow or a forest that's missing in the city?"),
            t("Bauwerke sind riesige Wärmespeicher, und versiegelter Boden kann kaum Wasser verdunsten, das Kühlung bringen würde. Dazu kommen Abwärme von Autos und Klimaanlagen und enge Straßen, in denen der Wind fehlt.", "Buildings are huge heat stores, and sealed ground can barely evaporate water, which would bring cooling. On top of that come waste heat from cars and air conditioning, and narrow streets where there's no wind."),
            t("Diese städtische Wärmeinsel kann nachts bis zu 10 °C ausmachen und belastet in Hitzewellen besonders ältere Menschen. Städte planen deshalb Frischluftschneisen, damit kühle Luft aus dem Umland hereinströmen kann, pflanzen Bäume und begrünen Dächer. Ein großer Stadtbaum verdunstet an einem heißen Tag Hunderte Liter Wasser.", "This urban heat island can amount to up to 10 °C at night and is especially hard on older people during heatwaves. That's why cities plan fresh-air corridors so cool air from the countryside can flow in, plant trees and green their roofs. A large city tree evaporates hundreds of litres of water on a hot day."),
        ),
        // ------------------------------------------------ mittel
        lab("erd.verweilzeit", Topic.Geography, M, 0.5f) {
            val atm = 13_000.0; val rain = 500_000.0; val days = atm / rain * 365
            task(
                question = l("In der Atmosphäre schweben ständig etwa 13.000 km³ Wasser als Dampf und Wolken. Pro Jahr fallen weltweit rund 500.000 km³ als Regen und Schnee. Wie viele Tage bleibt ein Wassermolekül im Durchschnitt in der Luft?", "The atmosphere constantly holds about 13,000 km³ of water as vapour and clouds. Each year around 500,000 km³ fall worldwide as rain and snow. On average, how many days does a water molecule stay in the air?"),
                steps = listOf(num(l("Tage", "Days"), days, "", rel = 0.03, done = """§\approx ${m(days, 1)}\ \text{Tage}§""".let { l(it, it.replace("Tage", "days")) },
                    hints = listOf(l("Kernidee: Die Verweilzeit ist Vorrat geteilt durch Durchfluss, wie bei einer Badewanne mit offenem Hahn und offenem Abfluss.", "Key idea: the residence time is the store divided by the flow, like a bathtub with the tap and the plug both open."), """§t = \dfrac{13\,000}{500\,000}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }))),
                solution = listOf("""§\dfrac{13\,000}{500\,000} \cdot 365 \approx ${m(days, 1)}§"""),
                background = l("Gut eine Woche: So schnell wird das Wasser der Atmosphäre komplett ausgetauscht. Im Ozean bleibt ein Wassermolekül dagegen im Mittel über 3000 Jahre, in tiefem Grundwasser oft Zehntausende Jahre. Dieselbe Rechnung, Vorrat durch Durchfluss, funktioniert für Seen, CO₂ in der Luft und sogar für die Wartezeit in einer Schlange.", "A good week: that's how quickly the water in the atmosphere is completely replaced. In the ocean, by contrast, a water molecule stays over 3000 years on average, in deep groundwater often tens of thousands of years. The same calculation, store divided by flow, works for lakes, CO₂ in the air and even for the waiting time in a queue."),
            )
        },
        lab("erd.hoehenlinien", Topic.Geography, M, 0.5f) {
            val cm = pick(2.0, 1.5, 3.0); val scale = 25_000; val dh = pick(100, 60, 150)
            val horiz = cm * scale / 100; val grade = dh / horiz * 100
            task(
                question = l("Auf einer Wanderkarte im Maßstab 1 : 25.000 liegt dein Start auf der 600-m-Höhenlinie und das Ziel auf der ${600 + dh}-m-Linie. Auf der Karte sind beide ${n(cm, 1)} cm voneinander entfernt. Wie steil ist der Weg im Durchschnitt?", "On a hiking map at a scale of 1 : 25,000 your start is on the 600 m contour line and your destination on the ${600 + dh} m line. On the map they are ${n(cm, 1)} cm apart. How steep is the path on average?"),
                steps = listOf(num(l("Steigung in Prozent", "Gradient in percent"), grade, "%", rel = 0.02, done = """§\approx ${m(grade, 0)}\ \%§""",
                    hints = listOf(l("Kernidee: Erst die Kartenstrecke in echte Meter umrechnen. Steigung ist dann Höhenunterschied durch waagrechte Strecke.", "Key idea: first convert the map distance into real metres. The gradient is then the height difference over the horizontal distance."), """§${m(cm, 1)}\ \text{cm} \cdot 25\,000 = ${m(horiz, 0)}\ \text{m}§"""))),
                solution = listOf("""§\dfrac{${dh}\ \text{m}}{${m(horiz, 0)}\ \text{m}} \approx ${m(grade / 100, 2)} = ${m(grade, 0)}\ \%§"""),
                background = l("Höhenlinien verbinden Punkte gleicher Höhe. Liegen sie dicht beieinander, ist das Gelände steil, liegen sie weit auseinander, ist es flach. Mit etwas Übung sieht man auf einer Karte Täler, Grate und Gipfel wie in 3D. Bergsteiger planen so, wie lange ein Aufstieg dauert: Für 100 Höhenmeter rechnet man etwa eine Viertelstunde zusätzlich.", "Contour lines join points of equal height. If they're close together the ground is steep; if they're far apart it's flat. With a little practice you can see valleys, ridges and peaks on a map as if in 3D. That's how mountaineers plan how long a climb will take: for every 100 metres of ascent you add about a quarter of an hour."),
            )
        },
        mc("erd.mercator", Topic.Geography, M,
            t("Auf vielen Weltkarten sieht Grönland ungefähr so groß aus wie Afrika. Wie ist es wirklich?", "On many world maps Greenland looks roughly as big as Africa. What is the truth?"),
            t("Größenverhältnis", "Size ratio"),
            t("Afrika ist etwa 14-mal so groß wie Grönland.", "Africa is about 14 times as big as Greenland."),
            listOf(t("Beide sind etwa gleich groß.", "They are about the same size."), t("Afrika ist etwa doppelt so groß.", "Africa is about twice as big."), t("Grönland ist größer als Afrika.", "Greenland is bigger than Africa.")),
            t("Kernidee: Eine Kugel lässt sich nicht verzerrungsfrei flach machen. Die übliche Mercator-Karte streckt alles, je näher es an den Polen liegt.", "Key idea: a sphere can't be flattened without distortion. The usual Mercator map stretches everything the closer it is to the poles."),
            t("Grönland: gut 2 Millionen km². Afrika: gut 30 Millionen km².", "Greenland: just over 2 million km². Africa: just over 30 million km²."),
            t("Grönland hat etwa 2,2 Millionen km², Afrika etwa 30 Millionen km². Auf der Mercator-Karte wird Grönland, das nahe am Pol liegt, enorm aufgebläht.", "Greenland has about 2.2 million km², Africa about 30 million km². On the Mercator map Greenland, being close to the pole, is hugely inflated."),
            t("Gerhard Mercator entwarf seine Karte 1569 für Seefahrer: Sie zeigt Winkel richtig, sodass ein fester Kompasskurs eine gerade Linie ist. Dafür muss sie Flächen in Polnähe stark vergrößern. Flächentreue Karten verzerren dafür die Formen. Jede Weltkarte ist ein Kompromiss, nur ein Globus zeigt alles richtig.", "Gerardus Mercator designed his map in 1569 for sailors: it shows angles correctly, so a fixed compass course is a straight line. To do that it has to enlarge areas near the poles a great deal. Equal-area maps distort shapes instead. Every world map is a compromise; only a globe shows everything correctly."),
        ),
        lab("erd.mittagssonne", Topic.Geography, M, 0.55f) {
            val (city, lat) = pick(l("Berlin", "Berlin") to 52.5, l("München", "Munich") to 48.1, l("Oslo", "Oslo") to 59.9, l("Rom", "Rome") to 41.9)
            val (day, dec) = pick(l("am 21. Juni", "on 21 June") to 23.4, l("am 21. Dezember", "on 21 December") to -23.4, l("am 20. März", "on 20 March") to 0.0)
            val h = 90 - lat + dec
            task(
                question = l("Wie hoch steht die Sonne mittags $day in $city (${n(lat, 1)}° Nord) über dem Horizont? Die Erdachse ist um 23,4° geneigt.", "How high is the Sun above the horizon at noon $day in $city (${n(lat, 1)}° north)? The Earth's axis is tilted by 23.4°."),
                steps = listOf(num(l("Sonnenhöhe in Grad", "Sun's height in degrees"), h, "°", abs = 0.5, done = """§${m(h, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Zur Tagundnachtgleiche steht die Sonne am Äquator mittags senkrecht. Pro Grad weiter nördlich steht sie ein Grad tiefer.", "Key idea: at the equinox the Sun is directly overhead at noon on the equator. For every degree further north it's one degree lower."), l("""Im Sommer kommen 23,4° dazu, im Winter gehen sie ab: §h = 90^\circ - \varphi + \delta§""", """In summer add 23.4°, in winter subtract it: §h = 90^\circ - \varphi + \delta§""")))),
                solution = listOf("""§h = 90^\circ - ${m(lat, 1)}^\circ ${if (dec >= 0) "+" else "-"} ${m(kotlin.math.abs(dec), 1)}^\circ = ${m(h, 1)}^\circ§"""),
                background = l("Der Höhenunterschied der Mittagssonne zwischen Sommer und Winter beträgt überall 47°, das Doppelte der Achsneigung. Steht die Sonne flach, verteilt sich dieselbe Strahlung auf eine viel größere Fläche, und sie muss einen langen Weg durch die Luft nehmen. Das ist der eigentliche Grund für die Jahreszeiten, nicht die Entfernung zur Sonne.", "The difference in the height of the noon Sun between summer and winter is 47° everywhere, twice the tilt of the axis. When the Sun is low, the same radiation is spread over a much larger area and has to take a long path through the air. That's the real reason for the seasons, not the distance from the Sun."),
            )
        },
        lab("erd.polarkreis", Topic.Geography, M, 0.55f) {
            task(
                question = l("Die Erdachse ist um 23,4° geneigt. Wie weit nördlich (in Grad Breite) muss ein Ort mindestens liegen, damit dort im Winter an mindestens einem Tag die Sonne gar nicht aufgeht? (Lichtbrechung in der Luft vernachlässigen)", "The Earth's axis is tilted by 23.4°. How far north (in degrees of latitude) must a place be, at least, for the Sun not to rise at all on at least one winter day? (Ignore the bending of light in the air.)"),
                steps = listOf(num(l("Breitengrad", "Latitude"), 66.6, "°", abs = 0.3, done = """§66{,}6^\circ\ \text{N}§""",
                    hints = listOf(l("Kernidee: Am kürzesten Tag steht die Sonne mittags 23,4° tiefer als zur Tagundnachtgleiche. Sie geht nicht auf, wenn sie mittags höchstens am Horizont steht.", "Key idea: on the shortest day the noon Sun is 23.4° lower than at the equinox. It doesn't rise if at noon it is at most on the horizon."), """§90^\circ - \varphi - 23{,}4^\circ = 0§"""))),
                solution = listOf("""§\varphi = 90^\circ - 23{,}4^\circ = 66{,}6^\circ§""", l("Das ist der nördliche Polarkreis.", "That is the Arctic Circle.")),
                background = l("Nördlich des Polarkreises gibt es Polarnacht im Winter und Mitternachtssonne im Sommer. In Wirklichkeit hebt die Lichtbrechung die Sonne etwas an, deshalb sieht man sie knapp am Polarkreis auch am kürzesten Tag kurz. Wäre die Erdachse nicht geneigt, gäbe es weder Jahreszeiten noch Polarkreise, und an jedem Ort wäre der Tag immer 12 Stunden lang.", "North of the Arctic Circle there's polar night in winter and the midnight sun in summer. In reality, refraction lifts the Sun slightly, so just at the Arctic Circle you still see it briefly even on the shortest day. If the Earth's axis weren't tilted, there would be no seasons and no polar circles, and every day everywhere would be 12 hours long."),
            )
        },
        lab("erd.temperaturhoehe", Topic.Geography, M, 0.45f) {
            val t0 = pick(22.0, 18.0, 26.0); val h0 = 700.0; val h1 = 2960.0
            val t1 = t0 - 0.65 * (h1 - h0) / 100
            task(
                question = l("In Garmisch-Partenkirchen (700 m) sind es ${n(t0, 0)} °C. Wie warm ist es ungefähr auf dem Gipfel der Zugspitze (2960 m)? Die Luft wird im Mittel um 0,65 °C pro 100 Höhenmeter kälter.", "In Garmisch-Partenkirchen (700 m) it's ${n(t0, 0)} °C. Roughly how warm is it on the summit of the Zugspitze (2960 m)? On average the air gets 0.65 °C colder for every 100 metres of height."),
                steps = listOf(num(l("Temperatur in °C", "Temperature in °C"), t1, "°C", abs = 0.5, done = """§\approx ${m(t1, 1)}\ ^\circ\text{C}§""",
                    hints = listOf(l("Kernidee: Erst den Höhenunterschied ausrechnen, dann in Hunderter-Schritten abkühlen.", "Key idea: first work out the height difference, then cool down in steps of a hundred metres."), """§\Delta T = 0{,}65 \cdot \dfrac{2260}{100}§"""))),
                solution = listOf("""§T = ${m(t0, 0)} - 0{,}65 \cdot 22{,}6 \approx ${m(t1, 1)}\ ^\circ\text{C}§"""),
                background = l("Aufsteigende Luft dehnt sich aus, weil der Druck nach oben abnimmt, und beim Ausdehnen kühlt sie ab. Deshalb haben selbst Berge am Äquator, wie der Kilimandscharo, Schnee auf dem Gipfel. In den Alpen verschieben sich mit der Höhe die Vegetationsstufen: Laubwald, Nadelwald, Latschen, Matten und schließlich Fels und Eis.", "Rising air expands because the pressure falls with height, and as it expands it cools. That's why even mountains on the equator, such as Kilimanjaro, have snow on their summits. In the Alps the vegetation zones change with height: deciduous forest, conifers, dwarf pines, alpine meadows and finally rock and ice."),
            )
        },
        mc("erd.maeander", Topic.Geography, M,
            t("Ein Fluss schlängelt sich in weiten Bögen durch eine Ebene. An welcher Seite einer Flussbiegung ist das Ufer steil und wird abgetragen?", "A river winds in wide loops through a plain. On which side of a bend is the bank steep and being worn away?"),
            t("Steiles Ufer", "Steep bank"),
            t("Außen in der Kurve", "On the outside of the bend"),
            listOf(t("Innen in der Kurve", "On the inside of the bend"), t("Auf beiden Seiten gleich", "Equally on both sides"), t("Immer am rechten Ufer, wegen der Erddrehung", "Always on the right bank, because of the Earth's rotation")),
            t("Kernidee: Wo fließt das Wasser in einer Kurve schneller, außen oder innen?", "Key idea: where does the water flow faster in a bend, on the outside or the inside?"),
            t("Schnelles Wasser trägt Material ab, langsames lagert es ab.", "Fast water carries material away; slow water deposits it."),
            t("Außen strömt das Wasser schnell und gräbt einen steilen Prallhang. Innen ist es langsam und lagert Sand und Kies ab, es entsteht ein flacher Gleithang.", "On the outside the water flows fast and cuts a steep undercut bank. On the inside it's slow and deposits sand and gravel, forming a gentle slip-off slope."),
            t("So wandern die Flussschlingen mit der Zeit immer weiter nach außen, bis sich zwei Bögen berühren. Dann nimmt der Fluss die Abkürzung, und die alte Schlinge bleibt als Altarm oder Altwasser zurück. Der Name kommt vom Fluss Mäander in der heutigen Türkei, der für seine Windungen berühmt war.", "In this way the loops of a river gradually move further and further outwards until two bends touch. Then the river takes the short cut, and the old loop is left behind as an oxbow lake. The name comes from the river Maeander in present-day Turkey, which was famous for its windings."),
        ),
        mc("erd.demografie", Topic.Geography, M,
            t("In einem Land sterben dank besserer Medizin und sauberem Wasser plötzlich viel weniger Kinder. Die Geburtenrate bleibt aber noch eine Weile hoch. Was passiert mit der Bevölkerung?", "In a country, thanks to better medicine and clean water, far fewer children suddenly die. But the birth rate stays high for a while. What happens to the population?"),
            t("Folge", "Consequence"),
            t("Sie wächst stark, bis auch die Geburtenrate sinkt.", "It grows strongly until the birth rate falls too."),
            listOf(t("Sie bleibt gleich, weil sich alles ausgleicht.", "It stays the same, because everything balances out."), t("Sie schrumpft.", "It shrinks."), t("Sie altert sofort stark.", "It immediately ages a lot.")),
            t("Kernidee: Wachstum ist Geburten minus Todesfälle.", "Key idea: growth is births minus deaths."),
            t("Familien passen sich erst nach einer Generation an: Wenn fast alle Kinder überleben, bekommt man weniger.", "Families only adjust after a generation: when almost all children survive, people have fewer."),
            t("Die Sterberate fällt sofort, die Geburtenrate erst später. In der Lücke dazwischen wächst die Bevölkerung schnell, danach pendelt sie sich wieder ein.", "The death rate falls at once, the birth rate only later. In the gap between, the population grows fast; afterwards it levels off again."),
            t("Dieses Muster heißt demografischer Übergang. Europa durchlief ihn im 19. Jahrhundert, viele Länder Asiens im 20., und in Teilen Afrikas läuft er gerade. Bildung, besonders für Mädchen, und sinkende Kindersterblichkeit senken die Geburtenzahlen am zuverlässigsten. Am Ende steht oft eine alternde Gesellschaft, wie heute in Deutschland oder Japan.", "This pattern is called the demographic transition. Europe went through it in the 19th century, many Asian countries in the 20th, and in parts of Africa it's under way now. Education, especially for girls, and falling child mortality reduce birth rates most reliably. At the end there's often an ageing society, as in Germany or Japan today."),
        ),
        mc("erd.wuestenguertel", Topic.Geography, M,
            t("Die Sahara, die Arabische Wüste, die Kalahari und die Wüsten Australiens liegen alle bei etwa 20° bis 30° nördlicher oder südlicher Breite. Warum gerade dort?", "The Sahara, the Arabian Desert, the Kalahari and the deserts of Australia all lie at about 20° to 30° north or south. Why there of all places?"),
            t("Grund", "Reason"),
            t("Dort sinkt Luft ab, die am Äquator aufgestiegen ist und ihren Regen schon verloren hat.", "Air sinks there that rose at the equator and has already lost its rain."),
            listOf(t("Dort scheint die Sonne am steilsten.", "That's where the Sun shines most directly."), t("Dort sind die Kontinente am weitesten vom Meer entfernt.", "That's where the continents are furthest from the sea."), t("Dort weht überhaupt kein Wind.", "There's no wind there at all.")),
            t("Kernidee: Am Äquator steigt heiße, feuchte Luft auf und regnet sich ab. Irgendwo muss sie wieder herunterkommen.", "Key idea: at the equator hot, moist air rises and rains out. It has to come down again somewhere."),
            t("Absinkende Luft wird zusammengedrückt und erwärmt sich. Können sich dabei Wolken bilden?", "Sinking air is compressed and warms up. Can clouds form like that?"),
            t("Die am Äquator aufgestiegene Luft strömt in der Höhe polwärts und sinkt um 30° wieder ab. Dabei erwärmt sie sich und wird trockener, Wolken lösen sich auf. So entsteht ein Gürtel aus Hochdruckgebieten und Wüsten.", "The air that rose at the equator flows towards the poles at high altitude and sinks again at around 30°. As it does, it warms and dries, and clouds dissolve. That creates a belt of high pressure and deserts."),
            t("Diese große Umwälzung heißt Hadley-Zelle. Am Boden strömt die Luft als Passatwind zurück zum Äquator. Am steilsten steht die Sonne übrigens am Äquator, und dort ist es wegen des aufsteigenden Regens grün. Wo kalte Meeresströmungen entlangfließen, wie in Namibia und Chile, gibt es sogar Wüsten direkt an der Küste.", "This great overturning is called the Hadley cell. At ground level the air flows back to the equator as the trade winds. The Sun is in fact most directly overhead at the equator, and it's green there because of the rising air and its rain. Where cold ocean currents flow along the coast, as in Namibia and Chile, there are even deserts right by the sea."),
        ),
        mc("erd.virtuelleswasser", Topic.Geography, M,
            t("Wie viel Wasser wird insgesamt gebraucht, um 1 kg Rindfleisch herzustellen, wenn man das Futter, das Tränken und die Verarbeitung mitzählt?", "How much water is needed altogether to produce 1 kg of beef, counting the feed, the drinking water and the processing?"),
            t("Wassermenge", "Amount of water"),
            t("Etwa 15.000 Liter", "About 15,000 litres"),
            listOf(t("Etwa 150 Liter", "About 150 litres"), t("Etwa 1.500 Liter", "About 1,500 litres"), t("Etwa 150.000 Liter", "About 150,000 litres")),
            t("Kernidee: Ein Rind frisst in seinem Leben mehrere Tonnen Getreide und Gras. Wie viel Wasser brauchen die Pflanzen dafür?", "Key idea: in its life a cow eats several tonnes of grain and grass. How much water do the plants need for that?"),
            t("Schon 1 kg Getreide braucht etwa 1.500 Liter Wasser zum Wachsen.", "Just 1 kg of grain needs about 1,500 litres of water to grow."),
            t("Rund 15.000 Liter pro Kilogramm. Fast alles davon ist Regenwasser, das auf die Futterflächen fällt, nur ein kleiner Teil ist Wasser aus Brunnen oder Flüssen.", "Around 15,000 litres per kilogram. Almost all of it is rainwater falling on the land that grows the feed; only a small part is water from wells or rivers."),
            t("Man nennt das virtuelles Wasser oder Wasser-Fußabdruck: Wasser, das in einem Produkt steckt, ohne dass man es sieht. Eine Tasse Kaffee braucht etwa 130 Liter, ein Kilo Tomaten gut 200. Wer Produkte aus trockenen Ländern importiert, importiert damit auch deren knappes Wasser. Für Wasserknappheit zählt besonders das künstlich bewässerte Wasser.", "This is called virtual water or the water footprint: water contained in a product without you seeing it. A cup of coffee takes about 130 litres, a kilo of tomatoes a good 200. Importing products from dry countries also means importing their scarce water. For water shortages, irrigation water matters most."),
        ),
        lab("erd.groenlandeis", Topic.Geography, M, 0.6f) {
            val ice = pick(2.9, 2.6); val water = ice * 0.917; val ocean = 361.0; val rise = water / ocean * 1000
            task(
                question = l("Auf Grönland liegen etwa ${n(ice, 1)} Millionen km³ Eis. Eis hat 91,7 % der Dichte von Wasser. Die Ozeane bedecken 361 Millionen km². Um wie viele Meter würde der Meeresspiegel steigen, wenn das ganze Grönlandeis schmilzt?", "Greenland holds about ${n(ice, 1)} million km³ of ice. Ice has 91.7% of the density of water. The oceans cover 361 million km². By how many metres would sea level rise if all of Greenland's ice melted?"),
                steps = listOf(num(l("Anstieg in Metern", "Rise in metres"), rise, "m", rel = 0.03, done = """§\approx ${m(rise, 1)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Erst das Eis in Wasser umrechnen, dann dieses Wasser gleichmäßig über die Meeresfläche verteilen.", "Key idea: first convert the ice into water, then spread that water evenly over the ocean surface."), """§h = \dfrac{V_{Wasser}}{A_{Meer}}§""".let { l(it, it.replace("Wasser", "water").replace("Meer", "ocean")) }))),
                solution = listOf("""§V_W = ${m(ice, 1)} \cdot 0{,}917 \approx ${m(water, 2)}\ \text{Mio. km}^3,\quad h = \dfrac{${m(water, 2)}}{361}\ \text{km} \approx ${m(rise, 1)}\ \text{m}§""".let { l(it, it.replace("Mio.", "million")) }),
                background = l("Das grönländische Eis ist bis zu 3 km dick. Ganz schmelzen würde es über Jahrhunderte bis Jahrtausende, aber schon heute verliert Grönland jedes Jahr mehrere Hundert Milliarden Tonnen. Das Eis der Antarktis enthält etwa achtmal so viel. Schmelzendes Meereis am Nordpol hebt den Meeresspiegel dagegen kaum, weil es schon schwimmt.", "Greenland's ice is up to 3 km thick. Melting completely would take centuries to millennia, but Greenland is already losing several hundred billion tonnes a year. The ice of Antarctica holds about eight times as much. Melting sea ice at the North Pole, by contrast, hardly raises sea level, because it's already floating."),
            )
        },
        lab("erd.bodensee", Topic.Geography, M, 0.55f) {
            val vol = 48.0; val flow = 370.0; val years = vol * 1e9 / flow / 3.156e7
            task(
                question = l("Der Bodensee enthält etwa 48 km³ Wasser. Im Mittel fließen ihm 370 m³ pro Sekunde zu, und genauso viel fließt über den Rhein ab. Wie viele Jahre dauert es im Durchschnitt, bis das Wasser des Sees einmal komplett ausgetauscht ist?", "Lake Constance holds about 48 km³ of water. On average 370 m³ per second flow in, and the same amount flows out via the Rhine. How many years does it take on average for the lake's water to be completely replaced once?"),
                steps = listOf(num(l("Jahre", "Years"), years, "", rel = 0.03, done = """§\approx ${m(years, 1)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) },
                    hints = listOf(l("Kernidee: Seevolumen geteilt durch Durchfluss ergibt die Zeit. Rechne die km³ in m³ um: 1 km³ = 10⁹ m³.", "Key idea: lake volume divided by flow gives the time. Convert km³ into m³: 1 km³ = 10⁹ m³."), """§t = \dfrac{48 \cdot 10^9\ \text{m}^3}{370\ \text{m}^3/\text{s}}§"""))),
                solution = listOf("""§t = \dfrac{4{,}8 \cdot 10^{10}}{370}\ \text{s} \approx ${sci(vol * 1e9 / flow)}\ \text{s} \approx ${m(years, 1)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }),
                background = l("Die Aufenthaltszeit sagt, wie empfindlich ein See ist. Gerät Gift in den Bodensee, bleibt es jahrelang drin, und rund vier Millionen Menschen beziehen ihr Trinkwasser daraus. In den 1970er-Jahren war der See durch Phosphat aus Waschmitteln und Abwasser stark überdüngt, Kläranlagen haben ihn wieder sauber gemacht.", "The residence time tells you how vulnerable a lake is. If poison gets into Lake Constance it stays for years, and around four million people get their drinking water from it. In the 1970s the lake was heavily over-fertilised by phosphate from detergents and sewage; treatment plants have made it clean again."),
            )
        },
        mc("erd.antipode", Topic.Geography, M,
            t("Du gräbst von Deutschland (etwa 51° N, 10° O) einen Tunnel genau durch den Erdmittelpunkt. Wo kommst du heraus?", "From Germany (about 51° N, 10° E) you dig a tunnel straight through the centre of the Earth. Where do you come out?"),
            t("Ausgang", "Exit"),
            t("Im Pazifik südöstlich von Neuseeland", "In the Pacific south-east of New Zealand"),
            listOf(t("In Australien", "In Australia"), t("In China", "In China"), t("In Argentinien", "In Argentina")),
            t("Kernidee: Der Gegenpunkt hat dieselbe Breite auf der anderen Halbkugel und eine um 180° verschobene Länge.", "Key idea: the antipode has the same latitude in the other hemisphere and a longitude shifted by 180°."),
            t("51° N wird zu 51° S. 10° O wird zu 170° W.", "51° N becomes 51° S. 10° E becomes 170° W."),
            t("Der Gegenpunkt liegt bei 51° S und 170° W, mitten im Südpazifik, gut 1000 km südöstlich von Neuseeland. China ist übrigens der Gegenpunkt von Argentinien.", "The antipode is at 51° S and 170° W, in the middle of the South Pacific, a good 1000 km south-east of New Zealand. China, by the way, is the antipode of Argentina."),
            t("Weil fast drei Viertel der Erde Meer sind, landen die meisten Tunnel im Wasser. Nur für etwa ein Siebtel des Festlands liegt auch auf der anderen Seite Land, das bekannteste Paar ist Spanien und Neuseeland. Ein echter Tunnel wäre unmöglich: Im Erdkern ist es über 5000 °C heiß. Das deutsche Sprichwort vom Loch bis nach China stimmt also nicht.", "Because almost three quarters of the Earth is sea, most tunnels end up in water. Only for about a seventh of the land is there land on the other side too; the best-known pair is Spain and New Zealand. A real tunnel would be impossible: the Earth's core is over 5000 °C hot. And the old idea of digging a hole through to China doesn't work from Europe either."),
        ),
        mc("erd.klimadiagramm", Topic.Geography, M,
            t("Eine Wetterstation misst: Jeden Monat liegt die Durchschnittstemperatur zwischen 25 und 28 °C, und in jedem Monat fallen mehr als 150 mm Regen. In welcher Landschaft steht sie?", "A weather station measures: every month the average temperature is between 25 and 28 °C, and more than 150 mm of rain falls in every month. What kind of landscape is it in?"),
            t("Landschaft", "Landscape"),
            t("Tropischer Regenwald", "Tropical rainforest"),
            listOf(t("Savanne", "Savanna"), t("Mittelmeerklima mit Olivenhainen", "Mediterranean climate with olive groves"), t("Heiße Wüste", "Hot desert")),
            t("Kernidee: Es gibt keinen kalten Monat und keinen trockenen Monat. Wo auf der Erde ist das so?", "Key idea: there's no cold month and no dry month. Where on Earth is that the case?"),
            t("Eine Savanne hätte eine Regenzeit und eine lange Trockenzeit.", "A savanna would have a rainy season and a long dry season."),
            t("Gleichmäßig heiß und das ganze Jahr nass: das Klima nahe am Äquator, wo der tropische Regenwald wächst.", "Evenly hot and wet all year round: the climate near the equator, where tropical rainforest grows."),
            t("Ein Klimadiagramm zeigt Temperatur und Niederschlag für jeden Monat. Daraus lässt sich fast immer die Vegetation ablesen: Wie lang ist die Wachstumszeit, und wann fehlt Wasser? Im Regenwald gibt es keine Jahreszeiten, deshalb haben Bäume dort oft keine Jahresringe. In der Savanne werfen viele Bäume in der Trockenzeit ihr Laub ab.", "A climate chart shows temperature and rainfall for each month. From it you can almost always read off the vegetation: how long is the growing season, and when is water lacking? In the rainforest there are no seasons, so trees there often have no annual rings. In the savanna many trees shed their leaves in the dry season."),
        ),
        // ------------------------------------------------ schwer
        lab("erd.foehn", Topic.Geography, H, 0.65f) {
            val t0 = pick(10.0, 12.0, 8.0); val top = pick(3000.0, 2500.0); val base = 500.0
            val dh = (top - base) / 100; val tTop = t0 - 0.5 * dh; val tEnd = tTop + 1.0 * dh
            task(
                question = l("Feuchte Luft strömt von Süden mit ${n(t0, 0)} °C aus 500 m Höhe die Alpen hinauf. Weil sich sofort Wolken bilden, kühlt sie nur um 0,5 °C pro 100 m ab. Am Pass (${n(top, 0)} m) hat sie sich abgeregnet und sinkt auf der Nordseite trocken wieder auf 500 m. Trockene Luft erwärmt sich beim Absinken um 1 °C pro 100 m. Wie warm kommt sie unten an?", "Moist air flows from the south at ${n(t0, 0)} °C from a height of 500 m up the Alps. Because clouds form at once, it only cools by 0.5 °C per 100 m. At the pass (${n(top, 0)} m) it has rained out, and it sinks dry on the northern side back down to 500 m. Dry air warms by 1 °C per 100 m as it sinks. How warm is it when it arrives at the bottom?"),
                steps = listOf(num(l("Temperatur in °C", "Temperature in °C"), tEnd, "°C", abs = 0.3, done = """§${m(tEnd, 1)}\ ^\circ\text{C}§""",
                    hints = listOf(l("Kernidee: Rechne in zwei Etappen: erst hinauf (feucht), dann hinunter (trocken). Beide Strecken sind ${n(top - base, 0)} m lang.", "Key idea: calculate in two stages: first up (moist), then down (dry). Both legs are ${n(top - base, 0)} m long."), """§T_{Pass} = ${m(t0, 0)} - 0{,}5 \cdot ${m(dh, 0)}§"""))),
                solution = listOf("""§T_{Pass} = ${m(t0, 0)} - ${m(0.5 * dh, 1)} = ${m(tTop, 1)}\ ^\circ\text{C},\quad T_{Nord} = ${m(tTop, 1)} + ${m(dh, 0)} = ${m(tEnd, 1)}\ ^\circ\text{C}§""".let { l(it, it.replace("Nord", "north")) }),
                background = l("Das ist der Föhn: ein warmer, trockener Fallwind, der in München manchmal im Winter für 20 °C sorgt. Der Trick liegt in der Kondensation: Beim Aufstieg gibt der Wasserdampf, der zu Wolken wird, Wärme ab und bremst die Abkühlung. Beim Abstieg fehlt diese Bremse. Weil die Luft so klar ist, sieht man bei Föhn die Alpen von München aus zum Greifen nah.", "This is the foehn: a warm, dry downslope wind that sometimes brings 20 °C to Munich in winter. The trick lies in condensation: on the way up, water vapour turning into cloud releases heat and slows the cooling. On the way down that brake is missing. Because the air is so clear, during a foehn the Alps look close enough to touch from Munich."),
            )
        },
        mc("erd.coriolis", Topic.Geography, H,
            t("Auf der Nordhalbkugel strömt Luft von allen Seiten in ein Tiefdruckgebiet hinein. In welche Richtung dreht sich der Wirbel, den man auf Satellitenbildern sieht?", "In the northern hemisphere air flows into a low-pressure area from all sides. In which direction does the swirl you see on satellite images turn?"),
            t("Drehrichtung", "Direction of rotation"),
            t("Gegen den Uhrzeigersinn", "Anticlockwise"),
            listOf(t("Im Uhrzeigersinn", "Clockwise"), t("Gar nicht, die Luft strömt gerade hinein", "Not at all, the air flows straight in"), t("Mal so, mal so, je nach Jahreszeit", "Sometimes one way, sometimes the other, depending on the season")),
            t("Kernidee: Auf der Nordhalbkugel wird jede Bewegung durch die Erddrehung nach rechts abgelenkt.", "Key idea: in the northern hemisphere the Earth's rotation deflects every movement to the right."),
            t("Zeichne ein Tief und Pfeile, die von außen hineinzeigen. Biege jeden Pfeil etwas nach rechts.", "Draw a low with arrows pointing in from outside. Bend each arrow a little to the right."),
            t("Jeder hineinströmende Luftstrom wird nach rechts abgelenkt. Zusammen ergibt das eine Drehung gegen den Uhrzeigersinn. Auf der Südhalbkugel ist es genau umgekehrt.", "Every stream of air flowing in is deflected to the right. Together that gives an anticlockwise rotation. In the southern hemisphere it's exactly the other way round."),
            t("Diese Ablenkung heißt Corioliskraft. Sie entsteht, weil sich der Boden unter der bewegten Luft wegdreht. Deshalb drehen sich Hurrikane im Atlantik immer links herum und Zyklone vor Australien rechts herum. Am Äquator verschwindet die Kraft, dort entstehen keine Wirbelstürme. Für eine Badewanne ist sie viel zu schwach, da entscheidet die Form des Beckens.", "This deflection is called the Coriolis force. It arises because the ground rotates away beneath the moving air. That's why hurricanes in the Atlantic always turn anticlockwise and cyclones off Australia clockwise. At the equator the force vanishes, and no tropical storms form there. In a bathtub it's far too weak; there the shape of the basin decides."),
        ),
        mc("erd.grosskreis", Topic.Geography, H,
            t("Ein Flug von Frankfurt nach Los Angeles führt über Grönland und Kanada, obwohl Los Angeles auf der Weltkarte fast genau westlich und sogar etwas südlich liegt. Warum?", "A flight from Frankfurt to Los Angeles goes over Greenland and Canada, even though on the world map Los Angeles lies almost due west and even slightly south. Why?"),
            t("Grund", "Reason"),
            t("Auf der Kugel ist der Bogen über den Norden kürzer als die gerade Linie auf der flachen Karte.", "On the globe the arc over the north is shorter than the straight line on the flat map."),
            listOf(t("Über dem Atlantik ist der Luftraum gesperrt.", "The airspace over the Atlantic is closed."), t("Im Norden gibt es mehr Rückenwind.", "There's more tailwind in the north."), t("Flugzeuge müssen in der Nähe von Land bleiben.", "Planes have to stay near land.")),
            t("Kernidee: Spann auf einem Globus einen Faden straff zwischen den beiden Städten.", "Key idea: on a globe, stretch a thread tightly between the two cities."),
            t("Die übliche Karte streckt den Norden in die Breite. Gerade Linien auf ihr sind auf der Kugel Umwege.", "The usual map stretches the north sideways. Straight lines on it are detours on the globe."),
            t("Der kürzeste Weg auf einer Kugel ist ein Großkreis, ein Kreis um die ganze Erde durch beide Orte. Zwischen Europa und Kalifornien biegt er weit nach Norden. Auf der Karte sieht er krumm aus, ist aber kürzer.", "The shortest route on a sphere is a great circle, a circle around the whole Earth through both places. Between Europe and California it bends far to the north. On the map it looks curved, but it's shorter."),
            t("Piloten und Seefahrer kennen das seit Jahrhunderten. Die Großkreisroute von Frankfurt nach Los Angeles ist über 1000 km kürzer als der Weg entlang des Breitengrades. Aus demselben Grund fliegen Maschinen von Europa nach Japan über Sibirien oder früher über den Nordpol. Wind und Sicherheitsregeln verschieben die echte Route dann noch etwas.", "Pilots and sailors have known this for centuries. The great-circle route from Frankfurt to Los Angeles is over 1000 km shorter than the route along the line of latitude. For the same reason planes from Europe to Japan fly over Siberia or, in the past, over the North Pole. Wind and safety rules then shift the actual route a little."),
        ),
    )

    private val geology = listOf(
        // ------------------------------------------------ leicht
        mc("gestein.epizentrum", Topic.Geology, E,
            t("Eine Erdbebenstation kann aus den Wellen nur ablesen, wie weit das Beben entfernt war, nicht aus welcher Richtung. Wie viele Stationen braucht man mindestens, um den Ort des Bebens auf der Karte eindeutig zu bestimmen?", "An earthquake station can only read from the waves how far away the quake was, not from which direction. What is the least number of stations needed to pin down the location of the quake on a map?"),
            t("Stationen", "Stations"),
            t("Drei", "Three"),
            listOf(t("Eine", "One"), t("Zwei", "Two"), t("Fünf", "Five")),
            t("Kernidee: Eine Entfernung ohne Richtung heißt: Das Beben liegt irgendwo auf einem Kreis um die Station.", "Key idea: a distance without a direction means the quake lies somewhere on a circle around the station."),
            t("Zwei Kreise schneiden sich meistens in zwei Punkten.", "Two circles usually intersect in two points."),
            t("Jede Station liefert einen Kreis. Zwei Kreise schneiden sich in zwei Punkten, erst der dritte Kreis entscheidet, welcher es ist.", "Each station gives a circle. Two circles meet in two points; only the third circle decides which one it is."),
            t("Die Entfernung liest man am Zeitabstand zwischen den schnellen P-Wellen und den langsameren S-Wellen ab, wie beim Zählen zwischen Blitz und Donner. Dasselbe Prinzip, Trilateration genannt, nutzt dein Handy beim GPS. Weil es dabei auch noch die genaue Uhrzeit und die Höhe bestimmen muss, braucht es mindestens vier Satelliten.", "The distance is read off from the time gap between the fast P waves and the slower S waves, like counting between lightning and thunder. The same principle, called trilateration, is used by your phone for GPS. Because it also has to work out the exact time and the height, it needs at least four satellites."),
        ),
        mc("gestein.marmor", Topic.Geology, E,
            t("Marmor ist ein Gestein, das durch Hitze und Druck tief in der Erde aus einem anderen Gestein entstanden ist. Aus welchem?", "Marble is a rock that formed from another rock through heat and pressure deep in the Earth. From which one?"),
            t("Ausgangsgestein", "Original rock"),
            t("Kalkstein", "Limestone"),
            listOf(t("Granit", "Granite"), t("Sandstein", "Sandstone"), t("Basalt", "Basalt")),
            t("Kernidee: Marmor braust auf, wenn man Säure darauf tropft, genau wie ein bestimmtes Sedimentgestein aus Muschelschalen.", "Key idea: marble fizzes when you drip acid on it, just like a certain sedimentary rock made of seashells."),
            t("Beide bestehen aus Calciumcarbonat.", "Both are made of calcium carbonate."),
            t("Kalkstein, entstanden aus Schalen von Meerestieren, kristallisiert unter Hitze und Druck um und wird zu Marmor. Die Zusammensetzung bleibt gleich, aber die Körner werden zu ineinander verzahnten Kristallen.", "Limestone, formed from the shells of sea creatures, recrystallises under heat and pressure and becomes marble. The composition stays the same, but the grains become interlocking crystals."),
            t("Gesteine wandern in einem großen Kreislauf: Sie verwittern, lagern sich ab, werden verfestigt, umgewandelt, geschmolzen und wieder gehoben. Aus Sandstein wird so Quarzit, aus Tonstein Schiefer. Der berühmte Marmor aus Carrara, aus dem Michelangelo seinen David schlug, war vor 200 Millionen Jahren Meeresboden.", "Rocks travel round a great cycle: they weather, are deposited, harden, are transformed, melt and are lifted up again. In this way sandstone becomes quartzite and mudstone becomes slate. The famous Carrara marble from which Michelangelo carved his David was seabed 200 million years ago."),
        ),
        mc("gestein.kratzprobe", Topic.Geology, E,
            t("Fensterglas hat auf der Mohs-Skala etwa die Härte 5,5, Quarz die Härte 7. Feiner Straßenstaub besteht zu einem großen Teil aus Quarzkörnchen. Was passiert, wenn du eine Brille aus Glas mit einem staubigen T-Shirt trocken abwischst?", "Window glass has a hardness of about 5.5 on the Mohs scale, quartz a hardness of 7. Fine street dust consists largely of tiny quartz grains. What happens if you wipe a pair of glass spectacles dry with a dusty T-shirt?"),
            t("Folge", "Consequence"),
            t("Die Quarzkörnchen können feine Kratzer ins Glas machen.", "The quartz grains can make fine scratches in the glass."),
            listOf(t("Nichts, Staub ist weicher als Glas.", "Nothing, dust is softer than glass."), t("Das Glas zerkratzt den Staub, nicht umgekehrt.", "The glass scratches the dust, not the other way round."), t("Kratzer entstehen nur bei Kunststoffgläsern.", "Scratches only happen with plastic lenses.")),
            t("Kernidee: Auf der Mohs-Skala ritzt jedes Mineral alle mit kleinerer Zahl.", "Key idea: on the Mohs scale every mineral scratches all those with a lower number."),
            t("7 ist größer als 5,5.", "7 is greater than 5.5."),
            t("Quarz ist härter als Glas und kann es deshalb ritzen. Darum spült man Brillen vor dem Putzen ab.", "Quartz is harder than glass and can therefore scratch it. That's why you rinse spectacles before cleaning them."),
            t("Quarz ist das häufigste Mineral der Erdoberfläche, der meiste Sand besteht daraus. Seine Härte ist im Alltag der Feind von Glas und Lack. Uhrengläser aus Saphir (Härte 9) widerstehen dagegen fast allem außer Diamant. Die Mohs-Skala ist übrigens nicht gleichmäßig: Diamant (10) ist viel mehr als nur etwas härter als Korund (9).", "Quartz is the most common mineral at the Earth's surface; most sand consists of it. Its hardness makes it the everyday enemy of glass and paint. Watch glasses made of sapphire (hardness 9), on the other hand, resist almost anything except diamond. The Mohs scale isn't even, by the way: diamond (10) is much more than just slightly harder than corundum (9)."),
        ),
        lab("gestein.grundwasser", Topic.Geology, E, 0.35f) {
            val thick = pick(20, 10, 30); val por = pick(25, 30, 20)
            val litres = thick * por / 100.0 * 1000
            task(
                question = l("Unter einer Wiese liegt eine ${thick} m dicke Kiesschicht, deren Poren komplett mit Grundwasser gefüllt sind. ${por} % des Volumens sind Poren. Wie viel Liter Wasser stecken unter einem Quadratmeter Wiese?", "Under a meadow lies a layer of gravel ${thick} m thick whose pores are completely filled with groundwater. ${por}% of the volume is pores. How many litres of water are under one square metre of meadow?"),
                steps = listOf(num(l("Liter", "Litres"), litres, "l", rel = 0.01, done = """§${m(litres, 0)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Unter 1 m² liegen ${thick} m³ Kies. Davon ist nur der Porenanteil Wasser. 1 m³ = 1000 l.", "Key idea: under 1 m² there are ${thick} m³ of gravel. Only the pore fraction of that is water. 1 m³ = 1000 l."), """§${thick}\ \text{m}^3 \cdot ${m(por / 100.0, 2)}§"""))),
                solution = listOf("""§${thick}\ \text{m}^3 \cdot ${m(por / 100.0, 2)} = ${m(litres / 1000, 1)}\ \text{m}^3 = ${m(litres, 0)}\ \text{l}§"""),
                background = l("Grundwasser ist kein unterirdischer See, sondern Wasser in den winzigen Zwischenräumen von Sand, Kies oder klüftigem Gestein. In Deutschland stammen etwa 70 % des Trinkwassers daraus. Weil es langsam durch den Boden sickert, wird es gefiltert und ist meist sehr sauber. Ist es aber einmal verschmutzt, etwa durch Nitrat aus Dünger, dauert die Erholung Jahrzehnte.", "Groundwater isn't an underground lake but water in the tiny spaces between grains of sand, gravel or fissured rock. In Germany about 70% of drinking water comes from it. Because it seeps slowly through the soil it's filtered and usually very clean. But once it's polluted, say by nitrate from fertiliser, recovery takes decades."),
            )
        },
        lab("gestein.kohlefloez", Topic.Geology, E, 0.4f) {
            val seam = pick(2.0, 1.5, 3.0); val peat = seam * 10; val years = peat * 1000
            task(
                question = l("Aus etwa 10 m Torf wird im Lauf von Jahrmillionen 1 m Steinkohle gepresst. Torf wächst in einem Moor etwa 1 mm pro Jahr. Wie viele Jahre Moorwachstum stecken in einem ${n(seam, 1)} m dicken Kohleflöz?", "Over millions of years about 10 m of peat is compressed into 1 m of coal. In a bog, peat grows about 1 mm a year. How many years of bog growth are in a coal seam ${n(seam, 1)} m thick?"),
                steps = listOf(num(l("Jahre", "Years"), years, "", rel = 0.01, done = """§${m(years, 0)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) },
                    hints = listOf(l("Kernidee: Erst ausrechnen, wie viel Torf nötig war, dann durch die Wachstumsrate teilen.", "Key idea: first work out how much peat was needed, then divide by the growth rate."), """§${m(seam, 1)}\ \text{m} \cdot 10 = ${m(peat, 0)}\ \text{m Torf}§""".let { l(it, it.replace("Torf", "peat")) }))),
                solution = listOf("""§\dfrac{${m(peat, 0)}\ \text{m}}{1\ \text{mm/Jahr}} = ${m(years, 0)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years").replace("Jahr", "year")) }),
                background = l("Die meiste Steinkohle Europas entstand vor etwa 300 Millionen Jahren in riesigen Sumpfwäldern aus baumhohen Bärlapp- und Schachtelhalmgewächsen, die bis zu 30 m hoch wurden. In jedem Flöz stecken also Zehntausende Jahre gespeichertes Sonnenlicht. In einem Kohlekraftwerk wird so ein Flöz in Minuten verbrannt, eine der Ursachen dafür, dass der CO₂-Gehalt der Luft so schnell steigt.", "Most of Europe's coal formed about 300 million years ago in vast swamp forests of tree-sized club mosses and horsetails that grew up to 30 m tall. So every seam holds tens of thousands of years of stored sunlight. In a coal power station such a seam is burnt in minutes, one of the reasons the CO₂ content of the air is rising so fast."),
            )
        },
        mc("gestein.wegener", Topic.Geology, E,
            t("1912 behauptete Alfred Wegener, die Kontinente hätten einst zusammengehangen und seien auseinandergedriftet. Welcher Hinweis stützte seine Idee damals besonders?", "In 1912 Alfred Wegener claimed the continents had once been joined together and had drifted apart. Which clue supported his idea particularly at the time?"),
            t("Hinweis", "Clue"),
            t("Fossilien desselben kleinen Süßwasserreptils in Südamerika und in Afrika", "Fossils of the same small freshwater reptile in South America and Africa"),
            listOf(t("Satellitenmessungen, die zeigen, wie sich die Kontinente bewegen", "Satellite measurements showing how the continents move"), t("Dass auf beiden Seiten des Atlantiks ähnliche Sprachen gesprochen werden", "That similar languages are spoken on both sides of the Atlantic"), t("Dass der Meeresspiegel überall gleich hoch ist", "That sea level is the same everywhere")),
            t("Kernidee: Was konnte man 1912 schon untersuchen? Satelliten gab es noch nicht.", "Key idea: what could already be studied in 1912? There were no satellites yet."),
            t("Ein Reptil aus Süßwasserseen kann keinen Ozean durchschwimmen.", "A reptile from freshwater lakes can't swim across an ocean."),
            t("Mesosaurus lebte in Seen und kann den Atlantik nicht durchquert haben. Seine Fossilien auf beiden Kontinenten passen dazu, dass diese einmal verbunden waren, genau wie die Küstenlinien, Gebirge und Gesteinsschichten.", "Mesosaurus lived in lakes and can't have crossed the Atlantic. Its fossils on both continents fit with them having once been joined, just like the coastlines, mountain ranges and rock layers."),
            t("Die meisten Geologen lehnten Wegeners Idee ab, weil er keine Kraft nennen konnte, die Kontinente bewegt. Erst in den 1960er-Jahren, lange nach seinem Tod auf einer Grönland-Expedition 1930, fand man die Erklärung: Am Meeresboden entsteht ständig neue Kruste, und die Platten wandern mit. Heute misst GPS die Bewegung direkt, wenige Zentimeter pro Jahr.", "Most geologists rejected Wegener's idea because he couldn't name a force that moved the continents. Only in the 1960s, long after his death on a Greenland expedition in 1930, was the explanation found: new crust constantly forms on the sea floor, and the plates move with it. Today GPS measures the movement directly, a few centimetres a year."),
        ),
        lab("gestein.geothermie", Topic.Geology, E, 0.4f) {
            val target = pick(150.0, 120.0, 180.0); val surf = 10.0
            val depth = (target - surf) / 30
            task(
                question = l("Mit jedem Kilometer Tiefe wird das Gestein im Mittel um 30 °C wärmer. An der Oberfläche hat es 10 °C. Wie tief muss man für ein Geothermie-Kraftwerk bohren, um ${n(target, 0)} °C heißes Gestein zu erreichen?", "With every kilometre of depth the rock gets on average 30 °C warmer. At the surface it's 10 °C. How deep must you drill for a geothermal power station to reach rock at ${n(target, 0)} °C?"),
                steps = listOf(num(l("Tiefe in km", "Depth in km"), depth, "km", rel = 0.02, done = """§\approx ${m(depth, 1)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Wie viele Grad fehlen noch? Und wie viele Kilometer sind das bei 30 °C pro Kilometer?", "Key idea: how many degrees are still missing? And how many kilometres is that at 30 °C per kilometre?"), """§d = \dfrac{${m(target, 0)} - 10}{30}\ \text{km}§"""))),
                solution = listOf("""§d = \dfrac{${m(target - surf, 0)}}{30}\ \text{km} \approx ${m(depth, 1)}\ \text{km}§"""),
                background = l("Die Wärme stammt zum Teil noch aus der Entstehung der Erde, vor allem aber aus dem Zerfall radioaktiver Elemente im Gestein. In Island oder Italien liegt heißes Gestein dicht unter der Oberfläche, dort ist Geothermie billig. In Deutschland muss man meist tief bohren, etwa im Oberrheingraben und im Münchner Raum, wo heiße Wasserschichten in 3 bis 5 km Tiefe ganze Stadtteile heizen.", "The heat partly still comes from the formation of the Earth, but mainly from the decay of radioactive elements in the rock. In Iceland or Italy hot rock lies close to the surface, so geothermal energy is cheap there. In Germany you usually have to drill deep, for instance in the Upper Rhine Graben and around Munich, where hot water layers 3 to 5 km down heat whole districts."),
            )
        },
        // ------------------------------------------------ mittel
        mc("gestein.schichtfolge", Topic.Geology, M,
            t("In einem Steinbruch liegen drei Schichten übereinander: A unten, B in der Mitte, C oben. Ein senkrechter Basaltgang schneidet durch alle drei hindurch. Darüber liegt ungestört eine vierte Schicht D. In welcher Reihenfolge ist alles entstanden?", "In a quarry three layers lie on top of each other: A at the bottom, B in the middle, C on top. A vertical basalt dyke cuts through all three. Above it lies a fourth layer D, undisturbed. In what order did everything form?"),
            t("Reihenfolge, älteste zuerst", "Order, oldest first"),
            t("A, B, C, Basaltgang, D", "A, B, C, basalt dyke, D"),
            listOf(t("Basaltgang, A, B, C, D", "Basalt dyke, A, B, C, D"), t("D, C, B, A, Basaltgang", "D, C, B, A, basalt dyke"), t("A, Basaltgang, B, C, D", "A, basalt dyke, B, C, D")),
            t("Kernidee: Was unten liegt, wurde zuerst abgelagert. Und etwas, das eine Schicht durchschneidet, muss jünger sein als diese Schicht.", "Key idea: what lies at the bottom was deposited first. And something that cuts through a layer must be younger than that layer."),
            t("Der Gang durchschneidet C, aber nicht D.", "The dyke cuts through C but not D."),
            t("Die Schichten entstanden von unten nach oben. Der Gang schneidet alle drei, also kam er danach. D liegt unversehrt darüber, also kam D zuletzt.", "The layers formed from the bottom up. The dyke cuts all three, so it came afterwards. D lies intact on top, so D came last."),
            t("Diese einfachen Regeln stellte Nicolaus Steno schon 1669 auf. Mit ihnen lesen Geologen eine Felswand wie ein Geschichtsbuch, ganz ohne Jahreszahlen. Erst im 20. Jahrhundert konnte man mit radioaktiven Uhren auch absolute Alter bestimmen, doch die relative Reihenfolge ist oft schon die halbe Geschichte.", "Nicolas Steno set out these simple rules as early as 1669. With them geologists read a rock face like a history book, without any dates at all. Only in the 20th century did radioactive clocks make it possible to determine absolute ages too, but the relative order is often half the story already."),
        ),
        lab("gestein.hawaii", Topic.Geology, M, 0.5f) {
            val (isl, km, ma) = pick(Triple(l("Kauaʻi", "Kauaʻi"), 500.0, 5.0), Triple(l("das Midway-Atoll", "Midway Atoll"), 2400.0, 28.0), Triple(l("Oʻahu", "Oʻahu"), 330.0, 3.0))
            val v = km * 1e5 / (ma * 1e6)
            task(
                question = l("Hawaiis aktive Vulkane sitzen über einem ortsfesten Hotspot im Erdmantel. Die Pazifische Platte wandert darüber hinweg. Die Gesteine von $isl, ${n(km, 0)} km weiter nordwestlich, sind etwa ${n(ma, 0)} Millionen Jahre alt. Wie schnell bewegt sich die Platte, in Zentimetern pro Jahr?", "Hawaii's active volcanoes sit above a fixed hotspot in the Earth's mantle. The Pacific Plate moves over it. The rocks of $isl, ${n(km, 0)} km further north-west, are about ${n(ma, 0)} million years old. How fast does the plate move, in centimetres per year?"),
                steps = listOf(num(l("Geschwindigkeit in cm/Jahr", "Speed in cm/year"), v, "cm", rel = 0.03, done = """§\approx ${m(v, 1)}\ \text{cm/Jahr}§""".let { l(it, it.replace("Jahr", "year")) },
                    hints = listOf(l("Kernidee: Die Insel ist einmal über dem Hotspot entstanden und dann ${n(km, 0)} km weggetragen worden. Strecke durch Zeit.", "Key idea: the island formed above the hotspot and was then carried ${n(km, 0)} km away. Distance over time."), """§v = \dfrac{${m(km, 0)} \cdot 10^5\ \text{cm}}{${m(ma, 0)} \cdot 10^6\ \text{Jahre}}§""".let { l(it, it.replace("Jahre", "years")) }))),
                solution = listOf("""§v \approx ${m(v, 1)}\ \text{cm/Jahr}§""".let { l(it, it.replace("Jahr", "year")) }),
                background = l("Die Inselkette ist wie eine Spur auf einem Fließband: Je weiter eine Insel vom Hotspot entfernt ist, desto älter, abgetragener und niedriger ist sie. Am Ende versinken die Inseln, und nur Korallenringe bleiben, wie das Midway-Atoll. Südöstlich der großen Insel wächst unter Wasser bereits der nächste Vulkan, er heißt Kamaʻehuakanaloa.", "The chain of islands is like a trail on a conveyor belt: the further an island is from the hotspot, the older, more eroded and lower it is. In the end the islands sink, leaving only rings of coral like Midway Atoll. South-east of the Big Island the next volcano is already growing under water; it's called Kamaʻehuakanaloa."),
            )
        },
        lab("gestein.abtragung", Topic.Geology, M, 0.5f) {
            val hgt = pick(3000.0, 4000.0, 2000.0); val rate = pick(0.5, 0.2, 1.0)
            val ma = hgt * 1000 / rate / 1e6
            task(
                question = l("Angenommen, ein ${n(hgt, 0)} m hohes Gebirge hört auf, sich zu heben. Wind, Wasser und Eis tragen ${n(rate, 1)} mm pro Jahr ab. Nach wie vielen Millionen Jahren wäre es eingeebnet?", "Suppose a mountain range ${n(hgt, 0)} m high stops rising. Wind, water and ice wear away ${n(rate, 1)} mm a year. After how many million years would it be levelled?"),
                steps = listOf(num(l("Millionen Jahre", "Million years"), ma, "", rel = 0.02, done = """§${m(ma, 0)}\ \text{Mio. Jahre}§""".let { l(it, it.replace("Mio. Jahre", "million years")) },
                    hints = listOf(l("Kernidee: Höhe in Millimeter umrechnen und durch die Abtragung pro Jahr teilen.", "Key idea: convert the height into millimetres and divide by the erosion per year."), """§\dfrac{${m(hgt, 0)} \cdot 1000\ \text{mm}}{${m(rate, 1)}\ \text{mm/Jahr}}§""".let { l(it, it.replace("Jahr", "year")) }))),
                solution = listOf("""§\dfrac{${m(hgt * 1000, 0)}}{${m(rate, 1)}} = ${m(ma * 1e6, 0)}\ \text{Jahre} = ${m(ma, 0)}\ \text{Mio. Jahre}§""".let { l(it, it.replace("Mio. Jahre", "million years").replace("Jahre", "years")) }),
                background = l("Ein Millimeter pro Jahr klingt nach nichts, aber in geologischen Zeiträumen verschwinden ganze Gebirge. Der Harz, der Schwarzwald und die schottischen Highlands sind Reste uralter Gebirge, die einmal so hoch waren wie die Alpen. Die Alpen selbst werden noch gehoben, etwa so schnell, wie sie abgetragen werden.", "A millimetre a year sounds like nothing, but over geological time whole mountain ranges disappear. The Harz, the Black Forest and the Scottish Highlands are the remains of ancient mountains that were once as high as the Alps. The Alps themselves are still being lifted, at about the same rate as they're being worn away."),
            )
        },
        mc("gestein.explosiv", Topic.Geology, M,
            t("Auf Hawaii fließt Lava oft ruhig wie ein glühender Fluss. Der Mount St. Helens explodierte 1980 dagegen und riss seine halbe Flanke weg. Was ist der wichtigste Unterschied?", "On Hawaii lava often flows calmly like a glowing river. Mount St. Helens, by contrast, exploded in 1980 and blew away half of its flank. What is the most important difference?"),
            t("Grund", "Reason"),
            t("Zähes, kieselsäurereiches Magma, aus dem das Gas nicht entweichen kann", "Thick, silica-rich magma from which the gas can't escape"),
            listOf(t("Heißeres Magma, das stärker kocht", "Hotter magma that boils harder"), t("Meerwasser, das in den Krater läuft", "Seawater running into the crater"), t("Größere Vulkane explodieren immer.", "Bigger volcanoes always explode.")),
            t("Kernidee: Denk an eine geschüttelte Sprudelflasche. Was passiert, wenn das Gas schnell oder langsam heraus kann?", "Key idea: think of a shaken bottle of fizzy water. What happens if the gas can get out quickly or slowly?"),
            t("Basalt-Lava auf Hawaii ist dünnflüssig, Lava an Subduktionszonen ist zäh wie kalter Honig.", "Basalt lava on Hawaii is runny; lava at subduction zones is thick like cold honey."),
            t("In dünnflüssigem Magma steigen Gasblasen einfach auf und entweichen. In zähem Magma bleiben sie gefangen, der Druck wächst, bis das Magma in Fetzen zerreißt.", "In runny magma gas bubbles simply rise and escape. In thick magma they stay trapped and the pressure builds until the magma is torn into shreds."),
            t("Den Unterschied macht vor allem der Anteil an Kieselsäure (Siliciumdioxid). Er macht Magma zäh, und er ist hoch dort, wo eine Platte abtaucht und Wasser und Krustenmaterial mitschmilzt. Deshalb sind die Vulkane rund um den Pazifik, wie Fuji, Pinatubo und St. Helens, so gefährlich, während man auf Hawaii neben fließender Lava stehen kann.", "The difference is mainly the amount of silica (silicon dioxide). It makes magma thick, and it's high where a plate sinks and melts water and crustal material along with it. That's why the volcanoes around the Pacific, such as Fuji, Pinatubo and St. Helens, are so dangerous, while on Hawaii you can stand next to flowing lava."),
        ),
        lab("gestein.landhebung", Topic.Geology, M, 0.5f) {
            val yrs = pick(400, 300, 500); val rate = pick(8.5, 9.0, 8.0); val hgt = yrs * rate / 1000
            task(
                question = l("Ein steinerner Bootsanleger an der Küste Nordschwedens wurde vor ${yrs} Jahren direkt am Wasser gebaut. Heute liegt er ${n(hgt, 1)} m über dem Meeresspiegel. Um wie viele Millimeter hebt sich das Land dort pro Jahr?", "A stone landing stage on the coast of northern Sweden was built right by the water ${yrs} years ago. Today it is ${n(hgt, 1)} m above sea level. By how many millimetres a year is the land rising there?"),
                steps = listOf(num(l("mm pro Jahr", "mm per year"), rate, "mm", rel = 0.03, done = """§\approx ${m(rate, 1)}\ \text{mm/Jahr}§""".let { l(it, it.replace("Jahr", "year")) },
                    hints = listOf(l("Kernidee: Höhenunterschied in Millimeter durch die Zahl der Jahre.", "Key idea: height difference in millimetres divided by the number of years."), """§\dfrac{${m(hgt * 1000, 0)}\ \text{mm}}{${yrs}}§"""))),
                solution = listOf("""§\dfrac{${m(hgt * 1000, 0)}\ \text{mm}}{${yrs}\ \text{Jahre}} \approx ${m(rate, 1)}\ \text{mm/Jahr}§""".let { l(it, it.replace("Jahre", "years").replace("Jahr", "year")) }),
                background = l("In der letzten Eiszeit lag auf Skandinavien ein bis zu 3 km dicker Eispanzer, der die Erdkruste in den zähen Erdmantel drückte, wie ein Gewicht eine Luftmatratze. Seit das Eis vor gut 10.000 Jahren geschmolzen ist, federt das Land langsam zurück. Am Bottnischen Meerbusen gewinnt Finnland so jedes Jahr mehrere Quadratkilometer neues Land.", "In the last ice age Scandinavia was covered by an ice sheet up to 3 km thick, which pressed the Earth's crust down into the viscous mantle, like a weight on an air mattress. Since the ice melted a good 10,000 years ago, the land has been slowly springing back. On the Gulf of Bothnia, Finland gains several square kilometres of new land every year this way."),
            )
        },
        lab("gestein.gletscher", Topic.Geology, M, 0.5f) {
            val speed = pick(150.0, 100.0, 200.0); val len = pick(15.0, 10.0, 20.0)
            val yrs = len * 1000 / speed
            task(
                question = l("Ein Gletscher fließt im Mittel ${n(speed, 0)} m pro Jahr talwärts. Ein Bergsteiger verliert am oberen Ende einen Eispickel in einer Spalte. Nach wie vielen Jahren taucht er an der Gletscherzunge ${n(len, 0)} km weiter unten wieder auf?", "A glacier flows downhill at an average of ${n(speed, 0)} m a year. A mountaineer loses an ice axe in a crevasse at the top end. After how many years does it reappear at the snout of the glacier, ${n(len, 0)} km further down?"),
                steps = listOf(num(l("Jahre", "Years"), yrs, "", rel = 0.02, done = """§${m(yrs, 0)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) },
                    hints = listOf(l("Kernidee: Das Eis trägt den Pickel mit wie ein sehr langsames Förderband.", "Key idea: the ice carries the axe along like a very slow conveyor belt."), """§t = \dfrac{${m(len * 1000, 0)}\ \text{m}}{${m(speed, 0)}\ \text{m/Jahr}}§""".let { l(it, it.replace("Jahr", "year")) }))),
                solution = listOf("""§t = \dfrac{${m(len * 1000, 0)}}{${m(speed, 0)}} = ${m(yrs, 0)}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }),
                background = l("Gletschereis ist fest und fließt trotzdem: Unter dem eigenen Gewicht verformen sich die Eiskristalle wie extrem zäher Teig. Was hineinfällt, kommt Jahrzehnte später wieder zum Vorschein. Teile eines Flugzeugs, das 1946 auf dem Gauligletscher in der Schweiz notlandete, tauchten ab 2012 an seinem unteren Ende auf. Weil die Gletscher schmelzen, gibt das Eis derzeit besonders viel frei.", "Glacier ice is solid and yet it flows: under its own weight the ice crystals deform like extremely stiff dough. Whatever falls in reappears decades later. Parts of an aircraft that crash-landed on the Gauli Glacier in Switzerland in 1946 appeared at its lower end from 2012 on. Because the glaciers are melting, the ice is giving up a particularly large amount at the moment."),
            )
        },
        mc("gestein.vulkanwinter", Topic.Geology, M,
            t("Im April 1815 brach in Indonesien der Vulkan Tambora aus. 1816 ging in Europa und Nordamerika als „Jahr ohne Sommer“ in die Geschichte ein, mit Schnee im Juni und Hungersnöten. Wie konnte ein Vulkan am anderen Ende der Welt das Wetter so verändern?", "In April 1815 the Tambora volcano erupted in Indonesia. 1816 went down in history in Europe and North America as the 'year without a summer', with snow in June and famines. How could a volcano on the other side of the world change the weather like that?"),
            t("Grund", "Reason"),
            t("Schwefelgase bildeten hoch in der Stratosphäre feine Tröpfchen, die rund um die Erde Sonnenlicht zurückwarfen.", "Sulphur gases formed tiny droplets high in the stratosphere that reflected sunlight all around the Earth."),
            listOf(t("Eine dicke Ascheschicht verdunkelte ein ganzes Jahr lang den Himmel über Europa.", "A thick layer of ash darkened the sky over Europe for a whole year."), t("Die Lava kühlte die Ozeane ab.", "The lava cooled the oceans."), t("Der Ausbruch hat die Erdbahn leicht verschoben.", "The eruption shifted the Earth's orbit slightly.")),
            t("Kernidee: Asche ist schwer und fällt innerhalb von Tagen bis Wochen herunter. Was bleibt viel länger oben?", "Key idea: ash is heavy and falls out within days to weeks. What stays up much longer?"),
            t("Über den Wolken, in der Stratosphäre, regnet es nicht. Dort halten sich winzige Tröpfchen über ein Jahr.", "Above the clouds, in the stratosphere, it doesn't rain. Tiny droplets can stay there for over a year."),
            t("Der Ausbruch schleuderte viele Millionen Tonnen Schwefeldioxid in die Stratosphäre. Daraus wurden Schwefelsäuretröpfchen, die sich um den Globus verteilten und einen Teil des Sonnenlichts ins All zurückwarfen. Die Erde kühlte im Mittel um rund ein halbes Grad ab.", "The eruption flung many millions of tonnes of sulphur dioxide into the stratosphere. This turned into droplets of sulphuric acid, which spread around the globe and reflected part of the sunlight back into space. The Earth cooled on average by around half a degree."),
            t("Ein halbes Grad im Mittel klang harmlos, führte aber regional zu Frost im Sommer und Missernten. In jenem kalten Sommer schrieb Mary Shelley am Genfer See „Frankenstein“. Auch der Ausbruch des Pinatubo 1991 kühlte die Erde messbar für etwa zwei Jahre. Forscher nutzen solche Ereignisse, um Klimamodelle zu prüfen.", "Half a degree on average sounded harmless, but regionally it led to summer frost and failed harvests. In that cold summer Mary Shelley wrote 'Frankenstein' on Lake Geneva. The eruption of Pinatubo in 1991 also measurably cooled the Earth for about two years. Researchers use such events to test climate models."),
        ),
        mc("gestein.karst", Topic.Geology, M,
            t("Große Tropfsteinhöhlen, versickernde Flüsse und Dolinen gibt es fast nur in Gegenden aus Kalkstein, etwa auf der Schwäbischen Alb. Warum gerade dort?", "Large caves with stalactites, disappearing rivers and sinkholes are found almost only in limestone areas, such as the Swabian Jura. Why there in particular?"),
            t("Grund", "Reason"),
            t("Regenwasser enthält etwas Kohlensäure, die Kalk langsam auflöst.", "Rainwater contains a little carbonic acid, which slowly dissolves limestone."),
            listOf(t("Kalkstein ist besonders weich und bricht leicht ein.", "Limestone is especially soft and collapses easily."), t("Unter Kalkgebieten gibt es mehr Erdbeben.", "There are more earthquakes under limestone areas."), t("Höhlen entstehen dort, wo früher Vulkane waren.", "Caves form where there used to be volcanoes.")),
            t("Kernidee: Warum bilden sich in Wasserkochern weiße Krusten, und womit entfernt man sie?", "Key idea: why do white crusts form in kettles, and what do you use to remove them?"),
            t("Regen nimmt CO₂ aus der Luft und dem Boden auf.", "Rain takes up CO₂ from the air and the soil."),
            t("Wasser mit gelöstem CO₂ ist eine schwache Säure. Sie löst Calciumcarbonat auf und weitet Klüfte im Kalk über Jahrtausende zu Höhlen. Tropft das Wasser in der Höhle wieder heraus, entweicht CO₂, und der Kalk lagert sich als Tropfstein ab.", "Water with dissolved CO₂ is a weak acid. It dissolves calcium carbonate and over thousands of years widens cracks in the limestone into caves. When the water drips out again in the cave, CO₂ escapes and the lime is deposited as a stalactite."),
            t("Dieselbe Reaktion läuft in beide Richtungen: Mit viel CO₂ löst sich Kalk, mit wenig fällt er aus. So entstehen in derselben Höhle Hohlräume und Tropfsteine. Karstwasser fließt schnell durch Spalten und wird kaum gefiltert, deshalb gelangen Verschmutzungen dort besonders leicht ins Trinkwasser. Der Blautopf bei Blaubeuren ist der Ausgang eines solchen Höhlensystems.", "The same reaction runs both ways: with lots of CO₂, lime dissolves; with little, it precipitates. That's how hollows and stalactites form in the same cave. Karst water flows quickly through fissures and is hardly filtered, so pollution gets into drinking water there especially easily. The Blautopf near Blaubeuren is the outlet of such a cave system."),
        ),
        lab("gestein.tiefseekern", Topic.Geology, M, 0.5f) {
            val rate = pick(2.0, 1.0, 3.0); val len = pick(300.0, 150.0, 600.0)
            val ma = len * 100 / rate * 1000 / 1e6
            task(
                question = l("Am Boden der Tiefsee lagern sich etwa ${n(rate, 0)} cm Schlamm pro 1000 Jahre ab, aus Staub und den winzigen Schalen von Plankton. Ein Forschungsschiff zieht einen ${n(len, 0)} m langen Bohrkern heraus. Wie viele Millionen Jahre Erdgeschichte stecken darin (ohne Zusammenpressen)?", "On the floor of the deep sea about ${n(rate, 0)} cm of mud is deposited per 1000 years, made of dust and the tiny shells of plankton. A research ship pulls out a core ${n(len, 0)} m long. How many million years of Earth history does it contain (ignoring compaction)?"),
                steps = listOf(num(l("Millionen Jahre", "Million years"), ma, "", rel = 0.02, done = """§${m(ma, 0)}\ \text{Mio. Jahre}§""".let { l(it, it.replace("Mio. Jahre", "million years")) },
                    hints = listOf(l("Kernidee: Erst die Länge in Zentimeter umrechnen, dann durch die Rate pro 1000 Jahre teilen.", "Key idea: first convert the length into centimetres, then divide by the rate per 1000 years."), """§\dfrac{${m(len * 100, 0)}\ \text{cm}}{${m(rate, 0)}\ \text{cm}} \cdot 1000\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }))),
                solution = listOf("""§\dfrac{${m(len * 100, 0)}}{${m(rate, 0)}} \cdot 1000 = ${m(ma * 1e6, 0)}\ \text{Jahre} \approx ${m(ma, 0)}\ \text{Mio. Jahre}§""".let { l(it, it.replace("Mio. Jahre", "million years").replace("Jahre", "years")) }),
                background = l("Tiefseekerne sind ein Klimaarchiv: In den Kalkschalen des Planktons ist gespeichert, wie warm das Meer war und wie viel Eis es an Land gab. Aus solchen Kernen weiß man, dass Eiszeiten in einem Takt von etwa 100.000 Jahren kamen und gingen, angetrieben von kleinen Schwankungen der Erdbahn. Dieselben Kerne zeigen auch den Einschlag, der die Dinosaurier auslöschte.", "Deep-sea cores are a climate archive: the chalky shells of plankton record how warm the sea was and how much ice there was on land. From such cores we know that ice ages came and went with a rhythm of about 100,000 years, driven by small wobbles in the Earth's orbit. The same cores also show the impact that wiped out the dinosaurs."),
            )
        },
        // ------------------------------------------------ schwer
        lab("gestein.isostasie", Topic.Geology, H, 0.65f) {
            val hgt = pick(5.0, 4.0, 8.8); val rc = 2.8; val rm = 3.3
            val root = rc * hgt / (rm - rc)
            task(
                question = l("Gebirge schwimmen auf dem zähen Erdmantel wie Eisberge im Wasser und haben eine tiefe Wurzel. Die Kruste hat die Dichte 2,8 g/cm³, der Mantel 3,3 g/cm³. Wie tief reicht die Wurzel unter einem Gebirge, das ${n(hgt, 1)} km über die umgebende Ebene ragt?", "Mountain ranges float on the viscous mantle like icebergs in water and have deep roots. The crust has a density of 2.8 g/cm³, the mantle 3.3 g/cm³. How deep does the root reach below a range that rises ${n(hgt, 1)} km above the surrounding plain?"),
                steps = listOf(num(l("Tiefe der Wurzel in km", "Depth of the root in km"), root, "km", rel = 0.02, done = """§\approx ${m(root, 0)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Gleichgewicht heißt: Das Gewicht des Berges oben wird ausgeglichen, weil in der Wurzel leichtes Krustengestein schwereren Mantel verdrängt.", "Key idea: balance means the weight of the mountain above is compensated because in the root light crustal rock displaces heavier mantle."), """§\rho_K \cdot h = (\rho_M - \rho_K) \cdot r§"""))),
                solution = listOf("""§r = \dfrac{2{,}8 \cdot ${m(hgt, 1)}}{3{,}3 - 2{,}8}\ \text{km} \approx ${m(root, 0)}\ \text{km}§"""),
                background = l("Jeder Kilometer Berg braucht über fünf Kilometer Wurzel. Das entdeckte man im 19. Jahrhundert, weil Lote in der Nähe des Himalaya weniger stark zum Gebirge hin abgelenkt wurden als erwartet: Die leichte Wurzel gleicht die Masse aus. Wird ein Gebirge abgetragen, hebt sich die Wurzel nach, und deshalb dauert das Einebnen so lange.", "Every kilometre of mountain needs over five kilometres of root. This was discovered in the 19th century because plumb lines near the Himalayas were pulled towards the mountains less than expected: the light root balances the mass. When a range is worn down, the root rises to compensate, which is why levelling it takes so long."),
            )
        },
        lab("gestein.gutenbergrichter", Topic.Geology, H, 0.6f) {
            val mag = pick(8, 7, 6); val nn = 1500.0 / Math.pow(10.0, mag - 5.0)
            task(
                question = l("Weltweit gibt es pro Jahr etwa 1500 Erdbeben der Magnitude 5. Jede Stufe höher kommt ungefähr zehnmal seltener vor. Wie viele Beben der Magnitude ${mag} erwartet man pro Jahr?", "Worldwide there are about 1500 earthquakes of magnitude 5 a year. Each step higher occurs roughly ten times less often. How many quakes of magnitude ${mag} would you expect per year?"),
                steps = listOf(num(l("Beben pro Jahr", "Quakes per year"), nn, "", rel = 0.03, done = """§\approx ${m(nn, if (nn < 10) 1 else 0)}§""",
                    hints = listOf(l("Kernidee: Von 5 bis ${mag} sind es ${mag - 5} Stufen, jede teilt durch 10.", "Key idea: from 5 to ${mag} is ${mag - 5} steps, and each divides by 10."), """§N = \dfrac{1500}{10^{${mag - 5}}}§"""))),
                solution = listOf("""§N = \dfrac{1500}{10^{${mag - 5}}} = ${m(nn, if (nn < 10) 1 else 0)}§"""),
                background = l("Dieses Gesetz von Gutenberg und Richter gilt erstaunlich genau, von winzigen bis zu riesigen Beben. Weil die Energie aber pro Stufe etwa 32-mal größer wird, setzen die seltenen großen Beben den Großteil der Energie frei. Vorhersagen kann man ein einzelnes Beben nicht, aber man weiß, wie oft es in einer Region im Mittel bebt, und baut danach.", "This law of Gutenberg and Richter holds astonishingly well, from tiny to huge quakes. But because the energy grows about 32 times per step, the rare big quakes release most of the energy. You can't predict a single quake, but you know how often a region shakes on average, and you build accordingly."),
            )
        },
        mc("gestein.ozeanboden", Topic.Geology, H,
            t("Die ältesten Gesteine der Kontinente sind rund 4 Milliarden Jahre alt. Wie alt ist der älteste Meeresboden?", "The oldest rocks on the continents are about 4 billion years old. How old is the oldest sea floor?"),
            t("Alter", "Age"),
            t("Nicht viel älter als 200 Millionen Jahre", "Not much older than 200 million years"),
            listOf(t("Ebenfalls etwa 4 Milliarden Jahre", "Also about 4 billion years"), t("Er ist älter als die Kontinente.", "It's older than the continents."), t("Nur wenige Tausend Jahre", "Only a few thousand years")),
            t("Kernidee: Meeresboden entsteht an den Rücken in der Ozeanmitte ständig neu. Wo verschwindet er wieder?", "Key idea: sea floor is constantly being created at the ridges in the middle of the oceans. Where does it disappear again?"),
            t("Ozeanische Kruste ist schwerer als kontinentale und taucht an Tiefseegräben in den Mantel ab.", "Oceanic crust is heavier than continental crust and sinks into the mantle at deep-sea trenches."),
            t("Meeresboden wird am Rücken gebildet, wandert nach außen und wird nach höchstens rund 200 Millionen Jahren an Tiefseegräben wieder verschluckt. Kontinente sind zu leicht zum Abtauchen und bleiben erhalten.", "Sea floor is formed at the ridges, moves outwards and is swallowed again at deep-sea trenches after at most around 200 million years. Continents are too light to sink and are preserved."),
            t("In den 1960er-Jahren entdeckte man symmetrische Streifen im Meeresboden: Gesteine, die das Erdmagnetfeld beim Erkalten in wechselnder Richtung gespeichert hatten, auf beiden Seiten der Rücken spiegelbildlich. Das war der Beweis, dass neuer Boden in der Mitte entsteht und nach außen wandert. Der Ozeanboden ist ein Förderband, die Kontinente sind die Fracht.", "In the 1960s symmetrical stripes were discovered in the sea floor: rocks that had recorded the Earth's magnetic field in alternating directions as they cooled, mirror images on either side of the ridges. That was the proof that new floor forms in the middle and moves outwards. The ocean floor is a conveyor belt; the continents are the cargo."),
        ),
        mc("gestein.erdalter", Topic.Geology, H,
            t("Auf der Erde gibt es kaum Gestein, das älter als 4 Milliarden Jahre ist. Trotzdem weiß man, dass die Erde etwa 4,54 Milliarden Jahre alt ist. Woher?", "There is hardly any rock on Earth older than 4 billion years. Yet we know that the Earth is about 4.54 billion years old. How?"),
            t("Methode", "Method"),
            t("Aus der radioaktiven Datierung von Meteoriten, die zusammen mit der Erde entstanden sind", "From the radioactive dating of meteorites that formed together with the Earth"),
            listOf(t("Aus der Dicke aller Sedimentschichten zusammen", "From the thickness of all the sediment layers together"), t("Aus dem Salzgehalt der Ozeane", "From the saltiness of the oceans"), t("Aus der Abkühlung des Erdinneren", "From the cooling of the Earth's interior")),
            t("Kernidee: Die Erdoberfläche wird ständig umgewälzt. Man braucht Material, das seit der Entstehung des Sonnensystems unverändert geblieben ist.", "Key idea: the Earth's surface is constantly being recycled. You need material that has stayed unchanged since the solar system formed."),
            t("Radioaktive Uhren wie Uran, das zu Blei zerfällt, ticken unbeirrt, egal wie heiß oder kalt es ist.", "Radioactive clocks such as uranium decaying to lead keep ticking regardless of how hot or cold it is."),
            t("Meteoriten sind Bruchstücke aus der Zeit, als sich die Planeten bildeten. Clair Patterson bestimmte 1956 mit dem Zerfall von Uran zu Blei ihr Alter: 4,55 Milliarden Jahre.", "Meteorites are fragments from the time the planets formed. In 1956 Clair Patterson used the decay of uranium to lead to determine their age: 4.55 billion years."),
            t("Frühere Schätzungen lagen weit daneben: Lord Kelvin errechnete aus der Abkühlung höchstens einige Hundert Millionen Jahre, weil niemand von der Wärme aus radioaktivem Zerfall wusste. Rechnungen mit dem Salz der Meere ergaben 90 Millionen Jahre. Patterson bemerkte bei seiner Arbeit übrigens, dass überall Blei aus Autoabgasen war, und kämpfte erfolgreich gegen verbleites Benzin.", "Earlier estimates were way off: Lord Kelvin calculated at most a few hundred million years from cooling, because nobody knew about heat from radioactive decay. Calculations using the salt of the sea gave 90 million years. During his work, Patterson noticed that there was lead from car exhaust everywhere, and he campaigned successfully against leaded petrol."),
        ),
    )
}
