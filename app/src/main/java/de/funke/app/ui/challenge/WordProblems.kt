package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import kotlin.random.Random

/*
 * Textaufgaben aus GSM8K ("Grade School Math 8K", OpenAI, https://github.com/openai/grade-school-math).
 * Copyright (c) 2021 OpenAI, veröffentlicht unter der MIT-Lizenz (Lizenztext: GSM8K_LICENSE in AboutPage
 * und THIRD_PARTY_NOTICES.md). Ausgewählt aus dem Test-Teil, sprachlich leicht geglättet und ins Deutsche
 * übertragen; Dollar wurden im Deutschen zu Euro, Meilen zu Kilometern, Pfund zu Kilogramm (die Zahlen bleiben gleich).
 * Die Lösungswege folgen den Musterlösungen des Datensatzes.
 * Schwierigkeit nach Anzahl der Rechenschritte: 2 = leicht, 3 = mittel, 4 = schwer.
 */

const val GSM8K_SOURCE = "GSM8K · © 2021 OpenAI · MIT"

private class WP(
    val id: Int,            // Zeile im GSM8K-Testteil (zum Nachschlagen)
    val answer: Int,
    val unitDe: String,
    val unitEn: String,
    val de: String,
    val en: String,
    val stepsDe: List<String>,
    val stepsEn: List<String>,
) {
    val difficulty: Difficulty
        get() = when {
            stepsEn.size <= 2 -> Difficulty.Easy
            stepsEn.size == 3 -> Difficulty.Medium
            else -> Difficulty.Hard
        }
}

object WordProblems {
    fun pick(d: Difficulty, r: Random): Challenge {
        val pool = ALL.filter { it.difficulty == d }.ifEmpty { ALL }
        val w = pool.random(r)
        val german = Lang.german
        return Challenge(
            topic = Topic.Word,
            difficulty = w.difficulty,
            question = if (german) w.de else w.en,
            answer = w.answer,
            unit = if (german) w.unitDe else w.unitEn,
            steps = if (german) w.stepsDe else w.stepsEn,
            source = GSM8K_SOURCE,
        )
    }

    val count: Int get() = ALL.size

    private val ALL = listOf(
        // --- 2 Schritte ---
        WP(
            69, 60, "€", "dollars",
            "Bailey hat etwas Geld. Dann bekommt sie 8 Wochen lang jede Woche 5 € Taschengeld. Danach hat sie insgesamt 100 €. Mit wie viel Geld hat sie angefangen?",
            "Bailey starts with some money. Then she gets a weekly allowance of \$5 for 8 weeks. After the 8 weeks she has \$100 in total. How much money did Bailey start with?",
            listOf("Taschengeld in 8 Wochen: 5 € · 8 = 40 €", "Am Anfang: 100 € − 40 € = 60 €"),
            listOf("Allowance in 8 weeks: \$5 × 8 = \$40", "At the start: \$100 − \$40 = \$60"),
        ),
        WP(
            83, 600, "", "",
            "Dan pflanzt 3 Rosenbüsche. An jedem Busch sind 25 Rosen, und jede Rose hat 8 Dornen. Wie viele Dornen sind es insgesamt?",
            "Dan plants 3 rose bushes. Each bush has 25 roses, and each rose has 8 thorns. How many thorns are there in total?",
            listOf("Rosen: 3 · 25 = 75", "Dornen: 75 · 8 = 600"),
            listOf("Roses: 3 × 25 = 75", "Thorns: 75 × 8 = 600"),
        ),
        WP(
            105, 20, "", "",
            "Cody isst dreimal so viele Kekse wie Amir. Amir isst 5 Kekse. Wie viele Kekse essen beide zusammen?",
            "Cody eats three times as many cookies as Amir. If Amir eats 5 cookies, how many cookies do they eat together?",
            listOf("Cody: 5 · 3 = 15", "Zusammen: 15 + 5 = 20"),
            listOf("Cody: 5 × 3 = 15", "Together: 15 + 5 = 20"),
        ),
        WP(
            117, 360, "€", "dollars",
            "John kauft für jedes seiner 3 Kinder 2 Paar Schuhe. Jedes Paar kostet 60 €. Wie viel zahlt er?",
            "John buys 2 pairs of shoes for each of his 3 children. Each pair costs \$60. How much does he pay?",
            listOf("Paare: 3 · 2 = 6", "Preis: 6 · 60 € = 360 €"),
            listOf("Pairs: 3 × 2 = 6", "Cost: 6 × \$60 = \$360"),
        ),
        WP(
            120, 240, "", "",
            "Miguel braucht zum Zeichnen jede Woche 2 Zeichenblöcke mit je 30 Blatt. Wie viele Blatt braucht er in einem Monat (4 Wochen)?",
            "Miguel uses 2 pads of paper a week for drawing. Each pad has 30 sheets. How many sheets does he use in a month (4 weeks)?",
            listOf("Pro Woche: 2 · 30 = 60 Blatt", "In 4 Wochen: 60 · 4 = 240 Blatt"),
            listOf("Per week: 2 × 30 = 60 sheets", "In 4 weeks: 60 × 4 = 240 sheets"),
        ),
        WP(
            288, 3, "", "",
            "Jede Steinplatte wiegt 75 kg. Ein Lieferwagen darf 2000 kg laden. Wie viele Lieferwagen braucht man, um 80 Platten auf einmal zu transportieren?",
            "Each flagstone weighs 75 pounds. A delivery truck can carry 2000 pounds. How many trucks are needed to carry 80 flagstones in one trip?",
            listOf("Gewicht: 80 · 75 kg = 6000 kg", "Lieferwagen: 6000 : 2000 = 3"),
            listOf("Weight: 80 × 75 = 6000 pounds", "Trucks: 6000 ÷ 2000 = 3"),
        ),
        WP(
            289, 16, "h", "hours",
            "Ein schnelles Auto fährt 60 km/h, ein langsames halb so schnell. Wie viele Stunden braucht das langsame Auto für 480 km?",
            "A car in the fast lane drives 60 miles per hour. A car in the slow lane drives half as fast. How many hours does the slow car need for 480 miles?",
            listOf("Halb so schnell: 60 : 2 = 30 km/h", "Zeit: 480 : 30 = 16 h"),
            listOf("Half as fast: 60 ÷ 2 = 30 mph", "Time: 480 ÷ 30 = 16 hours"),
        ),
        WP(
            477, 20, "", "",
            "In Ginas Klasse sind 9 Kinder gegen Milch allergisch, 6 gegen Erdnüsse und 3 davon gegen beides. In der Klasse sind 32 Kinder. Wie viele haben keine dieser Allergien?",
            "In Gina's class, 9 kids are allergic to dairy, 6 to peanuts, and 3 of them to both. There are 32 kids in the class. How many aren't allergic to either?",
            listOf("Erst alle Allergiker abziehen: 32 − 9 − 6 = 17", "Die 3 mit beiden Allergien wurden doppelt abgezogen, also wieder dazu: 17 + 3 = 20"),
            listOf("Subtract all allergic kids: 32 − 9 − 6 = 17", "The 3 with both allergies were subtracted twice, so add them back: 17 + 3 = 20"),
        ),
        WP(
            730, 8, "", "",
            "Parker kaut jeden Tag 4 Kaugummis. In einer Packung sind 15 Stück. Wie viele Packungen braucht er für 30 Tage?",
            "Parker chews 4 pieces of gum a day. A pack has 15 pieces. How many packs does he need for 30 days?",
            listOf("In 30 Tagen: 4 · 30 = 120 Kaugummis", "Packungen: 120 : 15 = 8"),
            listOf("In 30 days: 4 × 30 = 120 pieces", "Packs: 120 ÷ 15 = 8"),
        ),
        WP(
            776, 800, "€", "dollars",
            "Ein Laptop kostet 1000 €. Es gibt 20 % Rabatt. Wie viel zahlst du?",
            "A laptop costs \$1000. With a 20% discount, how much do you pay?",
            listOf("Rabatt: 20 % von 1000 € = 200 €", "Preis: 1000 € − 200 € = 800 €"),
            listOf("Discount: 20% of \$1000 = \$200", "Price: \$1000 − \$200 = \$800"),
        ),
        WP(
            1018, 60, "min", "minutes",
            "James braucht für 3 Seiten seines Buchs 10 Minuten. Vor dem Schlafen liest er 18 Seiten. Wie viele Minuten liest er?",
            "James needs 10 minutes to read 3 pages of his book. He reads 18 pages before he goes to sleep. How many minutes does he read?",
            listOf("18 Seiten sind 18 : 3 = 6-mal so viele wie 3 Seiten.", "Zeit: 6 · 10 min = 60 min"),
            listOf("18 pages are 18 ÷ 3 = 6 times as many as 3 pages.", "Time: 6 × 10 = 60 minutes"),
        ),
        WP(
            1126, 50, "", "days",
            "Albatros Alfie fliegt jeden Tag 400 km. Der Erdumfang beträgt 40.000 km. Wie viele Tage braucht Alfie für die halbe Strecke um die Erde?",
            "Alfie the albatross flies 400 kilometers every day. The Earth's circumference is 40,000 km. How many days does Alfie need to fly half the way around the Earth?",
            listOf("Halbe Strecke: 40.000 : 2 = 20.000 km", "Tage: 20.000 : 400 = 50"),
            listOf("Half the way: 40,000 ÷ 2 = 20,000 km", "Days: 20,000 ÷ 400 = 50"),
        ),
        // --- 3 Schritte ---
        WP(
            67, 595, "", "",
            "Eine Schatzsucherin findet eine Kiste voller Edelsteine: 175 Diamanten, 35 Rubine weniger als Diamanten und doppelt so viele Smaragde wie Rubine. Wie viele Edelsteine sind in der Kiste?",
            "A treasure hunter found a chest filled with gems: 175 diamonds, 35 fewer rubies than diamonds, and twice as many emeralds as rubies. How many gems were in the chest?",
            listOf("Rubine: 175 − 35 = 140", "Smaragde: 140 · 2 = 280", "Zusammen: 175 + 140 + 280 = 595"),
            listOf("Rubies: 175 − 35 = 140", "Emeralds: 140 × 2 = 280", "Total: 175 + 140 + 280 = 595"),
        ),
        WP(
            81, 17, "", "",
            "Charlie hat 10 Sticker. Er kauft 21 dazu und bekommt 23 zum Geburtstag. Dann schenkt er seiner Schwester 9 und klebt 28 auf eine Glückwunschkarte. Wie viele Sticker hat er noch?",
            "Charlie had 10 stickers. He bought 21 more and got 23 for his birthday. Then he gave 9 to his sister and used 28 to decorate a greeting card. How many stickers does Charlie have left?",
            listOf("Bekommen: 10 + 21 + 23 = 54", "Weggegeben: 9 + 28 = 37", "Übrig: 54 − 37 = 17"),
            listOf("Got: 10 + 21 + 23 = 54", "Gave away or used: 9 + 28 = 37", "Left: 54 − 37 = 17"),
        ),
        WP(
            135, 40, "", "",
            "Bobby hat 5 Videospiele weniger als dreimal so viele wie Brian. Brian hatte 20 Spiele, hat aber kurz vorher 5 verloren. Wie viele Spiele hat Bobby?",
            "Bobby has 5 fewer than 3 times as many video games as Brian. Brian had 20 games but lost 5 right before they compared. How many games does Bobby have?",
            listOf("Brian jetzt: 20 − 5 = 15", "Dreimal so viele: 15 · 3 = 45", "5 weniger: 45 − 5 = 40"),
            listOf("Brian now: 20 − 5 = 15", "Three times as many: 15 × 3 = 45", "5 fewer: 45 − 5 = 40"),
        ),
        WP(
            187, 106, "€", "dollars",
            "Mandy schuldet Benedict 100 €. Sie vereinbaren 2 % Zinsen pro Monat (immer auf die 100 €). Wie viel muss Mandy nach 3 Monaten zurückzahlen?",
            "Mandy owes Benedict \$100. They agreed on 2% interest per month (always on the \$100). How much does she have to pay back after 3 months?",
            listOf("Zinsen pro Monat: 2 % von 100 € = 2 €", "In 3 Monaten: 2 € · 3 = 6 €", "Zurück: 100 € + 6 € = 106 €"),
            listOf("Interest per month: 2% of \$100 = \$2", "In 3 months: \$2 × 3 = \$6", "Pay back: \$100 + \$6 = \$106"),
        ),
        WP(
            555, 2, "", "weeks",
            "John verdient 2 € pro Stunde und arbeitet 5 Stunden am Tag, 4 Tage pro Woche. Er möchte 80 € sparen. Wie viele Wochen dauert das?",
            "John earns \$2 per hour and works 5 hours a day, 4 days a week. He wants to save \$80. How many weeks will that take?",
            listOf("Pro Tag: 2 € · 5 = 10 €", "Pro Woche: 10 € · 4 = 40 €", "Wochen: 80 : 40 = 2"),
            listOf("Per day: \$2 × 5 = \$10", "Per week: \$10 × 4 = \$40", "Weeks: 80 ÷ 40 = 2"),
        ),
        WP(
            573, 600, "€", "dollars",
            "Katina hat 3000 € auf dem Sparkonto. Jeden Monat hebt sie 100 € ab. Wie viel ist nach 2 Jahren noch übrig?",
            "Katina has \$3000 in her savings account. She takes out \$100 every month. How much is left after 2 years?",
            listOf("2 Jahre = 2 · 12 = 24 Monate", "Abgehoben: 24 · 100 € = 2400 €", "Übrig: 3000 € − 2400 € = 600 €"),
            listOf("2 years = 2 × 12 = 24 months", "Taken out: 24 × \$100 = \$2400", "Left: \$3000 − \$2400 = \$600"),
        ),
        WP(
            712, 14, "", "",
            "Ginger und Amy haben beide 100 Süßigkeiten. Ginger isst 4 am Tag, Amy 3. Wie viele Süßigkeiten hat Amy nach zwei Wochen mehr als Ginger?",
            "Ginger and Amy both start with 100 pieces of candy. Ginger eats 4 pieces a day and Amy eats 3. How many more pieces does Amy have after two weeks?",
            listOf("Unterschied pro Tag: 4 − 3 = 1", "Zwei Wochen: 2 · 7 = 14 Tage", "Unterschied: 1 · 14 = 14"),
            listOf("Difference per day: 4 − 3 = 1", "Two weeks: 2 × 7 = 14 days", "Difference: 1 × 14 = 14"),
        ),
        WP(
            774, 10, "h", "hours",
            "Jack besucht ein Museum, das 150 km entfernt ist. Er fährt hin und zurück mit 75 km/h und bleibt 6 Stunden im Museum. Wie viele Stunden ist er unterwegs?",
            "Jack visits a museum 150 miles from home. He drives 75 miles per hour there and back and spends 6 hours at the museum. How many hours is he away from home?",
            listOf("Strecke: 150 + 150 = 300 km", "Fahrzeit: 300 : 75 = 4 h", "Insgesamt: 4 h + 6 h = 10 h"),
            listOf("Distance: 150 + 150 = 300 miles", "Driving: 300 ÷ 75 = 4 hours", "Total: 4 + 6 = 10 hours"),
        ),
        WP(
            1059, 2, "", "days",
            "Imma hat 3 Katzen. Sie füttert sie zweimal am Tag mit je 60 g pro Katze. Wie viele Tage reichen 720 g Katzenfutter?",
            "Imma has 3 cats. She feeds them twice a day, 60 grams per cat each time. How many days will 720 grams of cat food last?",
            listOf("Pro Mahlzeit: 3 · 60 g = 180 g", "Pro Tag: 180 g · 2 = 360 g", "Tage: 720 : 360 = 2"),
            listOf("Per meal: 3 × 60 g = 180 g", "Per day: 180 g × 2 = 360 g", "Days: 720 ÷ 360 = 2"),
        ),
        WP(
            1124, 64, "", "",
            "Seth gibt die Hälfte seiner Sticker Luis. Luis benutzt die Hälfte davon und gibt den Rest Kris. Kris behält 9 und gibt die übrigen 7 Rob. Wie viele Sticker hatte Seth am Anfang?",
            "Seth gave half of his stickers to Luis. Luis used half of them and gave the rest to Kris. Kris kept 9 and gave the remaining 7 to Rob. How many stickers did Seth have at the start?",
            listOf("Kris bekam 9 + 7 = 16", "Das war die Hälfte von Luis' Stickern: 16 · 2 = 32", "Das war die Hälfte von Seths Stickern: 32 · 2 = 64"),
            listOf("Kris got 9 + 7 = 16", "That was half of Luis's stickers: 16 × 2 = 32", "That was half of Seth's stickers: 32 × 2 = 64"),
        ),
        WP(
            1234, 12, "", "",
            "Regina hat letztes Jahr 9 Romane geschrieben. Das sind drei Viertel so viele wie in diesem Jahr. Wie viele Romane hat sie dieses Jahr geschrieben?",
            "Regina wrote 9 novels last year. That is three quarters of the number she has written this year. How many novels has she written this year?",
            listOf("9 sind 3/4 der gesuchten Zahl.", "Ein Viertel: 9 : 3 = 3", "Alles: 3 · 4 = 12"),
            listOf("9 is 3/4 of the number we want.", "One quarter: 9 ÷ 3 = 3", "All of it: 3 × 4 = 12"),
        ),
        // --- 4 Schritte ---
        WP(
            132, 60, "", "",
            "Pam und Fred sind auf dem Jahrmarkt. Pam fährt 2-mal Achterbahn, Fred 4-mal. Danach fährt jeder 2-mal Rodelbahn. Jede Fahrt kostet 6 Chips. Wie viele Chips brauchen sie zusammen?",
            "Pam and Fred went to a carnival. Pam rode the roller coaster 2 times and Fred rode it 4 times. Then each of them rode the luge 2 times. Each ride costs 6 tickets. How many tickets did they use?",
            listOf("Achterbahn: 2 + 4 = 6 Fahrten", "Rodelbahn: 2 + 2 = 4 Fahrten", "Zusammen: 6 + 4 = 10 Fahrten", "Chips: 10 · 6 = 60"),
            listOf("Roller coaster: 2 + 4 = 6 rides", "Luge: 2 + 2 = 4 rides", "Total: 6 + 4 = 10 rides", "Tickets: 10 × 6 = 60"),
        ),
        WP(
            398, 168, "", "",
            "Nancy räumt ihren alten Schuppen auf. Sie sieht 90 Spinnen, ein Drittel so viele Tausendfüßer und 12 Wanzen weniger als doppelt so viele wie Tausendfüßer. Wie viele Tiere hat sie insgesamt gezählt?",
            "Nancy is cleaning out her old woodshed. She sees 90 spiders, a third as many millipedes as spiders, and twice as many stink bugs as millipedes minus 12. How many bugs did she count in total?",
            listOf("Tausendfüßer: 90 : 3 = 30", "Doppelt so viele: 30 · 2 = 60", "Wanzen: 60 − 12 = 48", "Zusammen: 90 + 30 + 48 = 168"),
            listOf("Millipedes: 90 ÷ 3 = 30", "Twice as many: 30 × 2 = 60", "Stink bugs: 60 − 12 = 48", "Total: 90 + 30 + 48 = 168"),
        ),
        WP(
            426, 11, "€", "dollars",
            "Aleksandra bestellt Suppe, einen Bagel und ein Stück Kuchen. Der Bagel kostet 4 €, die Suppe 25 % mehr. Der Kuchen kostet halb so viel wie der Bagel. Wie viel zahlt sie?",
            "Aleksandra orders soup, a bagel and a piece of cake. The bagel costs \$4 and the soup 25% more. The cake costs half as much as the bagel. How much does she pay?",
            listOf("25 % von 4 € = 1 €", "Suppe: 4 € + 1 € = 5 €", "Kuchen: 4 € : 2 = 2 €", "Zusammen: 4 € + 5 € + 2 € = 11 €"),
            listOf("25% of \$4 = \$1", "Soup: \$4 + \$1 = \$5", "Cake: \$4 ÷ 2 = \$2", "Total: \$4 + \$5 + \$2 = \$11"),
        ),
        WP(
            446, 2, "", "",
            "Rozanne macht Eierpunsch. Sie hat 4 Packungen mit je 12 Eiern und noch 2 einzelne Eier. Für ein Glas braucht sie 5 Eier, auf ein Tablett passen 5 Gläser. Wie viele Tabletts kann sie füllen?",
            "Rozanne uses 4 dozen eggs and 2 more loose eggs to make eggnog. Each glass needs 5 eggs, and each tray holds 5 glasses. How many trays can she put out?",
            listOf("Eier in Packungen: 4 · 12 = 48", "Insgesamt: 48 + 2 = 50 Eier", "Gläser: 50 : 5 = 10", "Tabletts: 10 : 5 = 2"),
            listOf("Eggs in dozens: 4 × 12 = 48", "In total: 48 + 2 = 50 eggs", "Glasses: 50 ÷ 5 = 10", "Trays: 10 ÷ 5 = 2"),
        ),
        WP(
            557, 70, "", "",
            "In einer Tüte sind 21 Knöpfe. 7 davon haben zwei Löcher, der Rest hat vier Löcher. Wie viele Löcher sind es insgesamt?",
            "A bag has 21 buttons. Seven of them have two holes and the rest have four holes. How many holes are there in all?",
            listOf("Knöpfe mit vier Löchern: 21 − 7 = 14", "Deren Löcher: 14 · 4 = 56", "Zwei-Loch-Knöpfe: 7 · 2 = 14 Löcher", "Zusammen: 56 + 14 = 70"),
            listOf("Four-hole buttons: 21 − 7 = 14", "Their holes: 14 × 4 = 56", "Two-hole buttons: 7 × 2 = 14 holes", "Total: 56 + 14 = 70"),
        ),
        WP(
            604, 12, "€", "dollars",
            "Howard gibt am Montag 8 € in der Spielhalle aus, am Dienstag doppelt so viel und am Mittwoch viermal so viel wie am Dienstag. Er hatte 100 €. Wie viel hat er noch?",
            "Howard spends \$8 at the arcade on Monday. On Tuesday he spends twice as much, and on Wednesday 4 times as much as on Tuesday. He started with \$100. How much does he have left?",
            listOf("Dienstag: 8 € · 2 = 16 €", "Mittwoch: 16 € · 4 = 64 €", "Ausgegeben: 8 + 16 + 64 = 88 €", "Übrig: 100 € − 88 € = 12 €"),
            listOf("Tuesday: \$8 × 2 = \$16", "Wednesday: \$16 × 4 = \$64", "Spent: 8 + 16 + 64 = \$88", "Left: \$100 − \$88 = \$12"),
        ),
        WP(
            838, 15, "", "",
            "Einen großen Apfel kann man in 5 Stücke schneiden, einen kleinen in 3. Adam schneidet 3 große und 5 kleine Äpfel und isst 15 Stücke. Wie viele Stücke sind übrig?",
            "A large apple can be cut into 5 slices and a small one into 3. Adam cuts 3 large and 5 small apples and eats 15 slices. How many slices are left?",
            listOf("Große: 3 · 5 = 15 Stücke", "Kleine: 5 · 3 = 15 Stücke", "Zusammen: 15 + 15 = 30", "Übrig: 30 − 15 = 15"),
            listOf("Large: 3 × 5 = 15 slices", "Small: 5 × 3 = 15 slices", "Together: 15 + 15 = 30", "Left: 30 − 15 = 15"),
        ),
        WP(
            840, 16, "mm", "mm",
            "Patrick hat drei angebrauchte Klebestifte, jeder war ursprünglich 12 mm lang. Vom ersten ist noch 1/6 übrig, vom zweiten 2/3, vom dritten 1/2. Wie viele Millimeter Kleber sind noch übrig?",
            "Patrick has three glue sticks, each originally 12 mm long. One has 1/6 left, the second 2/3 and the third 1/2. How many millimeters of glue are left in total?",
            listOf("12 · 1/6 = 2 mm", "12 · 2/3 = 8 mm", "12 · 1/2 = 6 mm", "Zusammen: 2 + 8 + 6 = 16 mm"),
            listOf("12 × 1/6 = 2 mm", "12 × 2/3 = 8 mm", "12 × 1/2 = 6 mm", "Total: 2 + 8 + 6 = 16 mm"),
        ),
        WP(
            1084, 20, "m", "feet",
            "Sam und Harry teilen sich 100 m Zaun. Harry bekommt 60 m mehr als Sam. Wie viele Meter bekommt Sam?",
            "Sam and Harry share 100 feet of fence. Harry gets 60 feet more than Sam. How many feet does Sam get?",
            listOf("Sam bekommt x, Harry x + 60.", "Zusammen: x + (x + 60) = 100, also 2x + 60 = 100", "2x = 40", "x = 20 m"),
            listOf("Sam gets x, Harry x + 60.", "Together: x + (x + 60) = 100, so 2x + 60 = 100", "2x = 40", "x = 20 feet"),
        ),
        WP(
            1267, 40, "", "",
            "Auf einem Bauernhof leben 10 zweibeinige und 15 vierbeinige Tiere. Wie viele Beinpaare sind das insgesamt?",
            "A farm has 10 two-legged animals and 15 four-legged animals. How many pairs of legs are there in total?",
            listOf("Zweibeiner: 10 · 2 = 20 Beine", "Vierbeiner: 15 · 4 = 60 Beine", "Zusammen: 20 + 60 = 80 Beine", "Paare: 80 : 2 = 40"),
            listOf("Two-legged: 10 × 2 = 20 legs", "Four-legged: 15 × 4 = 60 legs", "Together: 20 + 60 = 80 legs", "Pairs: 80 ÷ 2 = 40"),
        ),
        WP(
            1300, 120, "€", "dollars",
            "Josh hat eine Autowerkstatt und schafft 3 Autos am Tag. Sie ist an jedem Tag außer Sonntag und Mittwoch geöffnet. Pro Auto verdient er 4 €. Wie viel verdient er in 2 Wochen?",
            "Josh runs a car shop and services 3 cars a day. It is open every day except Sunday and Wednesday. He earns \$4 per car. How much does he make in 2 weeks?",
            listOf("Offen: 7 − 2 = 5 Tage pro Woche", "Autos pro Woche: 5 · 3 = 15", "Pro Woche: 15 · 4 € = 60 €", "In 2 Wochen: 60 € · 2 = 120 €"),
            listOf("Open: 7 − 2 = 5 days a week", "Cars per week: 5 × 3 = 15", "Per week: 15 × \$4 = \$60", "In 2 weeks: \$60 × 2 = \$120"),
        ),
    )
}

/** Lizenztext von GSM8K (MIT). Muss bei Weitergabe der Aufgaben mitgeliefert werden. */
const val GSM8K_LICENSE = """MIT License

Copyright (c) 2021 OpenAI

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE."""
