package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import de.funke.app.ui.tr
import kotlin.random.Random

/*
 * Erdkunde: Fakten (Hauptstädte, Bundesländer, Rekorde) sind nicht urheberrechtlich geschützt.
 * Tabellen und Fragen sind selbst zusammengestellt.
 */
object Geography {
    private class Place(val de: String, val en: String, val capDe: String, val capEn: String) {
        val name get() = if (Lang.german) de else en
        val capital get() = if (Lang.german) capDe else capEn
    }

    private val States = listOf(
        Place("Baden-Württemberg", "Baden-Württemberg", "Stuttgart", "Stuttgart"),
        Place("Bayern", "Bavaria", "München", "Munich"),
        Place("Brandenburg", "Brandenburg", "Potsdam", "Potsdam"),
        Place("Hessen", "Hesse", "Wiesbaden", "Wiesbaden"),
        Place("Mecklenburg-Vorpommern", "Mecklenburg-Western Pomerania", "Schwerin", "Schwerin"),
        Place("Niedersachsen", "Lower Saxony", "Hannover", "Hanover"),
        Place("Nordrhein-Westfalen", "North Rhine-Westphalia", "Düsseldorf", "Düsseldorf"),
        Place("Rheinland-Pfalz", "Rhineland-Palatinate", "Mainz", "Mainz"),
        Place("Saarland", "Saarland", "Saarbrücken", "Saarbrücken"),
        Place("Sachsen", "Saxony", "Dresden", "Dresden"),
        Place("Sachsen-Anhalt", "Saxony-Anhalt", "Magdeburg", "Magdeburg"),
        Place("Schleswig-Holstein", "Schleswig-Holstein", "Kiel", "Kiel"),
        Place("Thüringen", "Thuringia", "Erfurt", "Erfurt"),
    )
    /** Für falsche Antworten bei Bundesländern: große Städte, die keine Landeshauptstadt sind */
    private val NotCapitalsDe = listOf("Frankfurt am Main", "Köln", "Nürnberg", "Leipzig", "Kassel", "Freiburg", "Rostock", "Augsburg", "Bonn", "Lübeck", "Halle (Saale)", "Jena", "Trier", "Cottbus", "Osnabrück")
    private val NotCapitalsEn = listOf("Frankfurt am Main", "Cologne", "Nuremberg", "Leipzig", "Kassel", "Freiburg", "Rostock", "Augsburg", "Bonn", "Lübeck", "Halle (Saale)", "Jena", "Trier", "Cottbus", "Osnabrück")

    private val Europe = listOf(
        Place("Frankreich", "France", "Paris", "Paris"),
        Place("Italien", "Italy", "Rom", "Rome"),
        Place("Spanien", "Spain", "Madrid", "Madrid"),
        Place("Portugal", "Portugal", "Lissabon", "Lisbon"),
        Place("Österreich", "Austria", "Wien", "Vienna"),
        Place("Schweiz", "Switzerland", "Bern", "Bern"),
        Place("Niederlande", "Netherlands", "Amsterdam", "Amsterdam"),
        Place("Belgien", "Belgium", "Brüssel", "Brussels"),
        Place("Polen", "Poland", "Warschau", "Warsaw"),
        Place("Tschechien", "Czechia", "Prag", "Prague"),
        Place("Dänemark", "Denmark", "Kopenhagen", "Copenhagen"),
        Place("Schweden", "Sweden", "Stockholm", "Stockholm"),
        Place("Norwegen", "Norway", "Oslo", "Oslo"),
        Place("Finnland", "Finland", "Helsinki", "Helsinki"),
        Place("Irland", "Ireland", "Dublin", "Dublin"),
        Place("Griechenland", "Greece", "Athen", "Athens"),
        Place("Ungarn", "Hungary", "Budapest", "Budapest"),
        Place("Rumänien", "Romania", "Bukarest", "Bucharest"),
        Place("Kroatien", "Croatia", "Zagreb", "Zagreb"),
        Place("Slowakei", "Slovakia", "Bratislava", "Bratislava"),
        Place("Bulgarien", "Bulgaria", "Sofia", "Sofia"),
        Place("Ukraine", "Ukraine", "Kyjiw", "Kyiv"),
        Place("Island", "Iceland", "Reykjavík", "Reykjavík"),
        Place("Litauen", "Lithuania", "Vilnius", "Vilnius"),
        Place("Lettland", "Latvia", "Riga", "Riga"),
        Place("Estland", "Estonia", "Tallinn", "Tallinn"),
        Place("Slowenien", "Slovenia", "Ljubljana", "Ljubljana"),
    )

    private val World = listOf(
        Place("Kanada", "Canada", "Ottawa", "Ottawa"),
        Place("Australien", "Australia", "Canberra", "Canberra"),
        Place("Brasilien", "Brazil", "Brasília", "Brasília"),
        Place("Japan", "Japan", "Tokio", "Tokyo"),
        Place("China", "China", "Peking", "Beijing"),
        Place("Indien", "India", "Neu-Delhi", "New Delhi"),
        Place("Ägypten", "Egypt", "Kairo", "Cairo"),
        Place("Türkei", "Turkey", "Ankara", "Ankara"),
        Place("Argentinien", "Argentina", "Buenos Aires", "Buenos Aires"),
        Place("Mexiko", "Mexico", "Mexiko-Stadt", "Mexico City"),
        Place("Südkorea", "South Korea", "Seoul", "Seoul"),
        Place("Kenia", "Kenya", "Nairobi", "Nairobi"),
        Place("Neuseeland", "New Zealand", "Wellington", "Wellington"),
        Place("Südafrika", "South Africa", "Pretoria", "Pretoria"),
        Place("Nigeria", "Nigeria", "Abuja", "Abuja"),
        Place("Marokko", "Morocco", "Rabat", "Rabat"),
        Place("Peru", "Peru", "Lima", "Lima"),
        Place("Chile", "Chile", "Santiago de Chile", "Santiago"),
        Place("Thailand", "Thailand", "Bangkok", "Bangkok"),
        Place("Vietnam", "Vietnam", "Hanoi", "Hanoi"),
        Place("Indonesien", "Indonesia", "Jakarta", "Jakarta"),
        Place("Kolumbien", "Colombia", "Bogotá", "Bogotá"),
        Place("Saudi-Arabien", "Saudi Arabia", "Riad", "Riyadh"),
        Place("Iran", "Iran", "Teheran", "Tehran"),
    )
    /** Bekannte Städte, die viele für die Hauptstadt halten (gute falsche Antworten). */
    private val Traps = mapOf(
        "Kanada" to listOf("Toronto", "Montreal", "Vancouver"), "Canada" to listOf("Toronto", "Montreal", "Vancouver"),
        "Australien" to listOf("Sydney", "Melbourne", "Brisbane"), "Australia" to listOf("Sydney", "Melbourne", "Brisbane"),
        "Brasilien" to listOf("Rio de Janeiro", "São Paulo", "Salvador"), "Brazil" to listOf("Rio de Janeiro", "São Paulo", "Salvador"),
        "Türkei" to listOf("Istanbul", "Izmir", "Antalya"), "Turkey" to listOf("Istanbul", "Izmir", "Antalya"),
        "Schweiz" to listOf("Zürich", "Genf", "Basel"), "Switzerland" to listOf("Zurich", "Geneva", "Basel"),
        "Südafrika" to listOf("Kapstadt", "Johannesburg", "Durban"), "South Africa" to listOf("Cape Town", "Johannesburg", "Durban"),
        "Nigeria" to listOf("Lagos", "Kano", "Ibadan"),
        "Marokko" to listOf("Casablanca", "Marrakesch", "Fès"), "Morocco" to listOf("Casablanca", "Marrakesh", "Fez"),
        "Neuseeland" to listOf("Auckland", "Christchurch", "Queenstown"), "New Zealand" to listOf("Auckland", "Christchurch", "Queenstown"),
        "Vietnam" to listOf("Ho-Chi-Minh-Stadt", "Da Nang", "Hue"),
        "Indien" to listOf("Mumbai", "Kalkutta", "Bangalore"), "India" to listOf("Mumbai", "Kolkata", "Bangalore"),
        "China" to listOf("Shanghai", "Hongkong", "Guangzhou"),
        "Saudi-Arabien" to listOf("Dschidda", "Mekka", "Dammam"), "Saudi Arabia" to listOf("Jeddah", "Mecca", "Dammam"),
    )

    private class Fact(val d: Difficulty, val de: String, val en: String, val okDe: String, val okEn: String, val wrongDe: List<String>, val wrongEn: List<String>, val whyDe: String, val whyEn: String)

    private val Facts = listOf(
        Fact(Difficulty.Easy, "Welcher Fluss, der durch Deutschland fließt, ist insgesamt am längsten?", "Which river flowing through Germany is the longest overall?",
            "Donau", "Danube", listOf("Rhein", "Elbe", "Main"), listOf("Rhine", "Elbe", "Main"),
            "Die Donau ist insgesamt rund 2850 km lang, der Rhein rund 1230 km. Innerhalb Deutschlands hat allerdings der Rhein die längste Strecke.",
            "The Danube is about 2,850 km long in total, the Rhine about 1,230 km. Within Germany, however, the Rhine has the longest stretch."),
        Fact(Difficulty.Easy, "Wie heißt der höchste Berg Deutschlands?", "What is the highest mountain in Germany?",
            "Zugspitze", "Zugspitze", listOf("Watzmann", "Feldberg", "Brocken"), listOf("Watzmann", "Feldberg", "Brocken"),
            "Die Zugspitze in den Bayerischen Alpen ist 2962 m hoch.", "The Zugspitze in the Bavarian Alps is 2,962 m high."),
        Fact(Difficulty.Easy, "Welcher ist der größte Ozean der Erde?", "Which is the largest ocean on Earth?",
            "Pazifik", "Pacific", listOf("Atlantik", "Indischer Ozean", "Arktischer Ozean"), listOf("Atlantic", "Indian Ocean", "Arctic Ocean"),
            "Der Pazifik bedeckt etwa ein Drittel der Erdoberfläche, mehr als alle Landflächen zusammen.", "The Pacific covers about a third of Earth's surface, more than all land combined."),
        Fact(Difficulty.Easy, "Auf welchem Kontinent liegt Ägypten?", "On which continent is Egypt?",
            "Afrika", "Africa", listOf("Asien", "Europa", "Südamerika"), listOf("Asia", "Europe", "South America"),
            "Ägypten liegt im Nordosten Afrikas. Nur die Sinai-Halbinsel gehört geografisch zu Asien.", "Egypt lies in northeastern Africa. Only the Sinai Peninsula belongs geographically to Asia."),
        Fact(Difficulty.Medium, "Welches ist flächenmäßig das größte Land der Erde?", "Which is the largest country on Earth by area?",
            "Russland", "Russia", listOf("Kanada", "China", "USA"), listOf("Canada", "China", "USA"),
            "Russland ist mit rund 17 Millionen km² fast doppelt so groß wie Kanada, das auf Platz zwei liegt.", "At about 17 million km², Russia is almost twice the size of Canada in second place."),
        Fact(Difficulty.Medium, "Welches ist das kleinste Land der Welt?", "Which is the smallest country in the world?",
            "Vatikanstadt", "Vatican City", listOf("Monaco", "Liechtenstein", "San Marino"), listOf("Monaco", "Liechtenstein", "San Marino"),
            "Die Vatikanstadt in Rom ist nur etwa 0,44 km² groß.", "Vatican City in Rome covers only about 0.44 km²."),
        Fact(Difficulty.Medium, "Wie heißt der höchste Berg der Erde?", "What is the highest mountain on Earth?",
            "Mount Everest", "Mount Everest", listOf("K2", "Kilimandscharo", "Mont Blanc"), listOf("K2", "Kilimanjaro", "Mont Blanc"),
            "Der Mount Everest im Himalaya ist 8849 m hoch. Der K2 folgt mit 8611 m.", "Mount Everest in the Himalayas is 8,849 m high. K2 follows with 8,611 m."),
        Fact(Difficulty.Medium, "In welches Meer mündet die Elbe?", "Into which sea does the Elbe flow?",
            "Nordsee", "North Sea", listOf("Ostsee", "Schwarzes Meer", "Mittelmeer"), listOf("Baltic Sea", "Black Sea", "Mediterranean Sea"),
            "Die Elbe mündet bei Cuxhaven in die Nordsee.", "The Elbe flows into the North Sea near Cuxhaven."),
        Fact(Difficulty.Medium, "In welches Meer mündet die Donau?", "Into which sea does the Danube flow?",
            "Schwarzes Meer", "Black Sea", listOf("Nordsee", "Ostsee", "Mittelmeer"), listOf("North Sea", "Baltic Sea", "Mediterranean Sea"),
            "Die Donau fließt von West nach Ost durch zehn Länder und mündet in Rumänien ins Schwarze Meer.", "The Danube flows west to east through ten countries and enters the Black Sea in Romania."),
        Fact(Difficulty.Hard, "Welche ist die größte Wüste der Erde?", "Which is the largest desert on Earth?",
            "Antarktis", "Antarctica", listOf("Sahara", "Gobi", "Kalahari"), listOf("Sahara", "Gobi", "Kalahari"),
            "Wüste heißt: sehr wenig Niederschlag, nicht unbedingt heiß. Die Antarktis ist die größte, die Sahara die größte heiße Wüste.",
            "A desert means very little precipitation, not necessarily heat. Antarctica is the largest; the Sahara is the largest hot desert."),
        Fact(Difficulty.Hard, "Welcher Fluss führt das meiste Wasser?", "Which river carries the most water?",
            "Amazonas", "Amazon", listOf("Nil", "Jangtse", "Mississippi"), listOf("Nile", "Yangtze", "Mississippi"),
            "Der Amazonas führt mehr Wasser als die nächsten sieben Flüsse zusammen. Der Nil gilt meist als längster Fluss.",
            "The Amazon carries more water than the next seven rivers combined. The Nile is usually considered the longest."),
        Fact(Difficulty.Hard, "Durch wie viele Länder fließt die Donau?", "How many countries does the Danube flow through?",
            "10", "10", listOf("4", "7", "13"), listOf("4", "7", "13"),
            "Deutschland, Österreich, Slowakei, Ungarn, Kroatien, Serbien, Rumänien, Bulgarien, Moldau und Ukraine: so viele wie bei keinem anderen Fluss.",
            "Germany, Austria, Slovakia, Hungary, Croatia, Serbia, Romania, Bulgaria, Moldova and Ukraine: more than any other river."),
        Fact(Difficulty.Hard, "Welche Stadt liegt auf zwei Kontinenten?", "Which city lies on two continents?",
            "Istanbul", "Istanbul", listOf("Kairo", "Moskau", "Athen"), listOf("Cairo", "Moscow", "Athens"),
            "Der Bosporus teilt Istanbul in einen europäischen und einen asiatischen Teil.", "The Bosporus divides Istanbul into a European and an Asian part."),
    )

    fun make(d: Difficulty, r: Random): Challenge {
        val roll = r.nextInt(10)
        return when {
            roll < 3 -> fact(d, r)
            d == Difficulty.Easy -> stateCapital(d, r)
            d == Difficulty.Medium -> if (r.nextBoolean()) capital(Europe, d, r) else stateCapital(d, r)
            else -> if (r.nextInt(3) == 0) countryOf(World + Europe, d, r) else capital(World, d, r)
        }
    }

    private fun fact(d: Difficulty, r: Random): Challenge {
        val f = Facts.filter { it.d == d }.ifEmpty { Facts }.random(r)
        val g = Lang.german
        return choice(
            Topic.Geography, d, if (g) f.de else f.en, if (g) f.okDe else f.okEn, if (g) f.wrongDe else f.wrongEn,
            listOf(if (g) f.whyDe else f.whyEn), r,
        )
    }

    private fun stateCapital(d: Difficulty, r: Random): Challenge {
        val s = States.random(r)
        val pool = (if (Lang.german) NotCapitalsDe else NotCapitalsEn).shuffled(r).take(2) +
            States.filter { it != s }.random(r).capital
        return choice(
            Topic.Geography, d,
            tr("Wie heißt die Landeshauptstadt von ${s.name}?", "What is the state capital of ${s.name}?"),
            s.capital, pool,
            listOf(tr("Die Landeshauptstadt von ${s.name} ist ${s.capital}.", "The state capital of ${s.name} is ${s.capital}.")) +
                if (s.de == "Hessen" || s.de == "Nordrhein-Westfalen" || s.de == "Niedersachsen" || s.de == "Rheinland-Pfalz")
                    listOf(tr("Achtung: Die größte Stadt ist nicht immer die Hauptstadt.", "Careful: the biggest city isn't always the capital."))
                else emptyList(),
            r,
        )
    }

    private fun capital(list: List<Place>, d: Difficulty, r: Random): Challenge {
        val c = list.random(r)
        val traps = Traps[c.name].orEmpty().shuffled(r).take(2)
        val wrong = traps + list.filter { it != c }.shuffled(r).map { it.capital }.take(3 - traps.size)
        return choice(
            Topic.Geography, d,
            tr("Wie heißt die Hauptstadt von ${c.name}?", "What is the capital of ${c.name}?"),
            c.capital, wrong,
            listOf(tr("Die Hauptstadt von ${c.name} ist ${c.capital}.", "The capital of ${c.name} is ${c.capital}.")) +
                if (traps.isNotEmpty()) listOf(tr("${traps.first()} ist bekannter, aber nicht die Hauptstadt.", "${traps.first()} is better known, but it isn't the capital.")) else emptyList(),
            r,
        )
    }

    private fun countryOf(list: List<Place>, d: Difficulty, r: Random): Challenge {
        val c = list.random(r)
        return choice(
            Topic.Geography, d,
            tr("${c.capital} ist die Hauptstadt von welchem Land?", "${c.capital} is the capital of which country?"),
            c.name, list.filter { it != c }.shuffled(r).map { it.name }.take(3),
            listOf(tr("${c.capital} ist die Hauptstadt von ${c.name}.", "${c.capital} is the capital of ${c.name}.")),
            r,
        )
    }
}
