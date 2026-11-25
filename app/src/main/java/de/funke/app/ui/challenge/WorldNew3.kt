package de.funke.app.ui.challenge

import kotlin.math.pow

/* Ausbau (seit 13.2): Chemie und Biologie. Texte selbst geschrieben, Zahlen gerundet. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard

object WorldNew3 {
    val all: List<TaskDef> get() = chemistry + biology

    private val chemistry = listOf(
        // ------------------------------------------------ leicht
        lab("chem.molmasse", Topic.Chemistry, E, 0.35f) {
            val k = int(0, 4)
            val name = listOf(l("Kohlenstoffdioxid", "carbon dioxide"), l("Wasser", "water"), l("Methan", "methane"), l("Traubenzucker", "glucose"), l("Ammoniak", "ammonia"))[k]
            val formula = listOf("CO₂", "H₂O", "CH₄", "C₆H₁₂O₆", "NH₃")[k]
            val mm = listOf(44.0, 18.0, 16.0, 180.0, 17.0)[k]
            val calc = listOf("12 + 2 · 16", "2 · 1 + 16", "12 + 4 · 1", "6 · 12 + 12 · 1 + 6 · 16", "14 + 3 · 1")[k]
            task(
                question = l("Wie schwer ist ein Mol ${name} (${formula})? Atommassen: H = 1, C = 12, N = 14, O = 16 g/mol.", "How heavy is one mole of ${name} (${formula})? Atomic masses: H = 1, C = 12, N = 14, O = 16 g/mol."),
                steps = listOf(num(l("Molmasse in g/mol", "Molar mass in g/mol"), mm, "g/mol", abs = 0.0, done = """§${m(mm, 0)}\ \text{g/mol}§""",
                    hints = listOf(l("Kernidee: Die tiefgestellten Zahlen sagen, wie viele Atome jeder Sorte im Molekül stecken. Addiere ihre Massen.", "Key idea: the subscript numbers tell you how many atoms of each kind are in the molecule. Add up their masses."), "${calc} = ?"))),
                solution = listOf("${calc} = ${n(mm, 0)} g/mol"),
                background = l("Ein Mol ist eine feste Anzahl von Teilchen, rund 6 · 10²³. Der Trick daran: Ein Mol eines Stoffes wiegt in Gramm genau so viel, wie seine Teilchen in atomaren Masseneinheiten wiegen. So kann man Atome mit einer Küchenwaage abzählen. Wer weiß, dass 44 g CO₂ ein Mol sind, kann ausrechnen, wie viel CO₂ beim Verbrennen von Kohle entsteht.", "A mole is a fixed number of particles, around 6 · 10²³. The trick is that one mole of a substance weighs exactly as many grams as its particles weigh in atomic mass units. That way you can count atoms with a kitchen scale. If you know that 44 g of CO₂ is one mole, you can work out how much CO₂ is produced when coal is burnt."),
            )
        },
        mc("chem.alkali", Topic.Chemistry, E,
            t("Natrium ist ein weiches Metall, das in Wasser zischend herumflitzt und Wasserstoff freisetzt. Welches dieser Elemente verhält sich am ähnlichsten?", "Sodium is a soft metal that whizzes around fizzing in water and releases hydrogen. Which of these elements behaves most similarly?"),
            t("Element", "Element"),
            t("Kalium", "Potassium"),
            listOf(t("Magnesium", "Magnesium"), t("Chlor", "Chlorine"), t("Eisen", "Iron")),
            t("Kernidee: Elemente in derselben Spalte des Periodensystems haben gleich viele Elektronen auf der äußersten Schale.", "Key idea: elements in the same column of the periodic table have the same number of electrons in their outermost shell."),
            t("Natrium steht in der ersten Hauptgruppe, direkt über einem anderen weichen Metall.", "Sodium is in the first main group, directly above another soft metal."),
            t("Kalium steht direkt unter Natrium in der Gruppe der Alkalimetalle. Beide haben ein einzelnes Außenelektron, das sie leicht abgeben, und reagieren deshalb heftig mit Wasser.", "Potassium is directly below sodium in the group of alkali metals. Both have a single outer electron that they give up easily, which is why they react violently with water."),
            t("Je weiter unten in der Gruppe, desto weiter ist das Außenelektron vom Kern entfernt und desto leichter geht es verloren: Kalium entzündet sich in Wasser mit violetter Flamme, Caesium explodiert. Dmitri Mendelejew ordnete 1869 die Elemente nach solchen Ähnlichkeiten und ließ Lücken für Elemente, die erst später entdeckt wurden. Ihre Eigenschaften hatte er richtig vorhergesagt.", "The further down the group, the further the outer electron is from the nucleus and the more easily it's lost: potassium ignites in water with a violet flame, caesium explodes. In 1869 Dmitri Mendeleev arranged the elements by such similarities and left gaps for elements that were only discovered later. He had correctly predicted their properties."),
        ),
        lab("chem.massenerhaltung", Topic.Chemistry, E, 0.3f) {
            val w = pick(200, 500, 250); val s = pick(20, 30, 15)
            task(
                question = l("Du stellst ein Glas mit ${w} g Wasser auf eine Waage und rührst ${s} g Salz hinein, bis man nichts mehr davon sieht. Was zeigt die Waage jetzt (ohne Glas)?", "You put a glass with ${w} g of water on a scale and stir in ${s} g of salt until you can't see any of it any more. What does the scale show now (without the glass)?"),
                steps = listOf(num(l("Masse in Gramm", "Mass in grams"), (w + s).toDouble(), "g", abs = 0.0, done = "${w + s} g",
                    hints = listOf(l("Kernidee: Das Salz ist unsichtbar geworden. Ist es deshalb weg?", "Key idea: the salt has become invisible. Does that mean it's gone?"), l("Beim Lösen verteilen sich die Salzteilchen zwischen den Wasserteilchen, keines verschwindet.", "When it dissolves, the salt particles spread out between the water particles; none of them disappears.")))),
                solution = listOf(l("${w} g + ${s} g = ${w + s} g. Gelöstes Salz wiegt genauso viel wie festes.", "${w} g + ${s} g = ${w + s} g. Dissolved salt weighs just as much as solid salt.")),
                background = l("Bei allen chemischen und physikalischen Vorgängen bleibt die Masse erhalten. Antoine Lavoisier bewies das im 18. Jahrhundert mit sehr genauen Waagen und verschlossenen Gefäßen. Wenn scheinbar Masse verschwindet, etwa beim Verbrennen von Holz, ist sie als Gas entwichen. Das Volumen der Salzlösung ist übrigens etwas kleiner als das von Wasser und Salz getrennt.", "In all chemical and physical processes mass is conserved. Antoine Lavoisier proved this in the 18th century with very precise balances and sealed vessels. When mass seems to disappear, for example when wood burns, it has escaped as a gas. The volume of the salt solution, by the way, is slightly less than that of the water and salt separately."),
            )
        },
        mc("chem.trennen", Topic.Chemistry, E,
            t("In einer Dose sind Sand und Salz vermischt. Wie bekommst du beide wieder sauber getrennt?", "A tin holds a mixture of sand and salt. How do you get them cleanly separated again?"),
            t("Verfahren", "Method"),
            t("In Wasser rühren, durch einen Filter gießen (der Sand bleibt hängen) und dann das Wasser verdampfen (das Salz bleibt zurück)", "Stir into water, pour through a filter (the sand stays behind), then evaporate the water (the salt is left)"),
            listOf(t("Durch ein feines Sieb schütteln", "Shake through a fine sieve"), t("Mit einem Magneten das Salz herausziehen", "Pull the salt out with a magnet"), t("Das Gemisch einfrieren", "Freeze the mixture")),
            t("Kernidee: Welche Eigenschaft unterscheidet Salz und Sand am deutlichsten?", "Key idea: which property distinguishes salt and sand most clearly?"),
            t("Salz löst sich in Wasser, Sand nicht.", "Salt dissolves in water; sand doesn't."),
            t("Gelöstes Salz läuft mit dem Wasser durch den Filter, Sand nicht. Verdampft man das Wasser, kristallisiert das Salz wieder aus.", "Dissolved salt runs through the filter with the water; sand doesn't. If you evaporate the water, the salt crystallises out again."),
            t("Jedes Trennverfahren nutzt einen Unterschied zwischen den Stoffen: Löslichkeit, Korngröße, Siedepunkt, Magnetismus oder Dichte. Sieben scheitert hier, weil Salz- und Sandkörner ähnlich groß sind. In Salinen gewinnt man Salz genau so, nur mit Sonne und Wind statt Herdplatte, und Kläranlagen kombinieren mehrere dieser Schritte hintereinander.", "Every separation method uses a difference between the substances: solubility, grain size, boiling point, magnetism or density. Sieving fails here because salt and sand grains are a similar size. Salt works produce salt in exactly this way, only with sun and wind instead of a hotplate, and sewage works combine several of these steps one after another."),
        ),
        mc("chem.backpulver", Topic.Chemistry, E,
            t("Du gibst einen Löffel Natron in ein Glas Essig. Es schäumt heftig. Welches Gas entsteht?", "You put a spoonful of baking soda into a glass of vinegar. It foams violently. Which gas is produced?"),
            t("Gas", "Gas"),
            t("Kohlenstoffdioxid", "Carbon dioxide"),
            listOf(t("Sauerstoff", "Oxygen"), t("Wasserstoff", "Hydrogen"), t("Stickstoff", "Nitrogen")),
            t("Kernidee: Natron ist Natriumhydrogencarbonat. Säuren zerlegen Carbonate.", "Key idea: baking soda is sodium hydrogen carbonate. Acids break down carbonates."),
            t("Dasselbe Gas ist in Sprudelwasser und lässt Teig aufgehen.", "The same gas is in fizzy water and makes dough rise."),
            t("Die Essigsäure reagiert mit dem Natron zu Natriumacetat, Wasser und Kohlenstoffdioxid. Das Gas bildet die Bläschen.", "The acetic acid reacts with the baking soda to form sodium acetate, water and carbon dioxide. The gas forms the bubbles."),
            t("Backpulver enthält Natron und eine trockene Säure. Erst wenn Feuchtigkeit und Wärme dazukommen, reagieren sie, und das CO₂ bläht den Teig auf. Man kann das Gas auch nachweisen: Hält man ein brennendes Streichholz in das Glas, erlischt es, weil CO₂ schwerer als Luft ist und den Sauerstoff verdrängt.", "Baking powder contains baking soda and a dry acid. Only when moisture and heat are added do they react, and the CO₂ puffs up the dough. You can also detect the gas: if you hold a burning match in the glass, it goes out, because CO₂ is heavier than air and pushes out the oxygen."),
        ),
        lab("chem.flammenfarbe", Topic.Chemistry, E, 0.4f) {
            val all = listOf(l("Gelb", "Yellow") to l("Natrium", "Sodium"), l("Rot", "Red") to l("Strontium", "Strontium"), l("Blaugrün", "Blue-green") to l("Kupfer", "Copper"), l("Violett", "Violet") to l("Kalium", "Potassium"))
            val (col, el) = pick(all)
            task(
                question = l("Feuerwerk bekommt seine Farben von Metallsalzen in den Leuchtsternen. Welches Element sorgt für die Farbe ${col}?", "Fireworks get their colours from metal salts in the stars. Which element produces the colour ${col.lowercase()}?"),
                steps = listOf(pick1(l("Element", "Element"), el, all.filter { it.second != el }.map { it.second },
                    hints = listOf(l("Kernidee: Jedes Element leuchtet in der Flamme in seinen ganz eigenen Farben, wie ein Fingerabdruck.", "Key idea: in a flame every element glows in its very own colours, like a fingerprint."), l("Natrium kennst du von gelben Straßenlaternen, Kupfer von grünen Dächern.", "You know sodium from yellow street lamps, copper from green roofs.")))),
                solution = listOf(l("${col}: ${el}. Gelb Natrium, Rot Strontium, Blaugrün Kupfer, Violett Kalium.", "${col}: ${el.lowercase()}. Yellow sodium, red strontium, blue-green copper, violet potassium.")),
                background = l("In der Hitze springen Elektronen auf höhere Bahnen und fallen dann zurück. Dabei senden sie Licht ganz bestimmter Wellenlängen aus, die nur von den Abständen der Energieniveaus abhängen. Robert Bunsen und Gustav Kirchhoff entdeckten mit dieser Spektralanalyse 1860 zwei neue Elemente, Caesium und Rubidium. Später fand man so Helium zuerst in der Sonne, bevor man es auf der Erde kannte.", "In the heat electrons jump to higher orbits and then fall back. As they do, they emit light of very particular wavelengths that depend only on the spacing of the energy levels. In 1860 Robert Bunsen and Gustav Kirchhoff discovered two new elements with this spectral analysis, caesium and rubidium. Later, helium was found this way in the Sun before it was known on Earth."),
            )
        },
        mc("chem.rotkohl", Topic.Chemistry, E,
            t("Du kochst Rotkohl und gießt etwas vom violetten Kochwasser in zwei Gläser. In das erste gibst du Essig, in das zweite ein wenig Waschsoda. Was passiert?", "You cook red cabbage and pour some of the purple cooking water into two glasses. In the first you add vinegar, in the second a little washing soda. What happens?"),
            t("Ergebnis", "Result"),
            t("Mit Essig wird es rot, mit Soda grün bis gelb.", "With vinegar it turns red, with soda green to yellow."),
            listOf(t("Beide bleiben violett.", "Both stay purple."), t("Mit Essig wird es grün, mit Soda rot.", "With vinegar it turns green, with soda red."), t("Beide werden farblos.", "Both turn colourless.")),
            t("Kernidee: Der Farbstoff im Rotkohl ändert seine Form je nach pH-Wert, und jede Form hat eine andere Farbe.", "Key idea: the dye in red cabbage changes its shape depending on the pH, and each shape has a different colour."),
            t("Essig ist sauer, Waschsoda ist eine Lauge.", "Vinegar is acidic; washing soda is alkaline."),
            t("Die Anthocyane im Rotkohl sind rot in Säuren, violett im Neutralen und grün bis gelb in Laugen. Rotkohlsaft ist ein natürlicher pH-Indikator.", "The anthocyanins in red cabbage are red in acids, purple in neutral solutions and green to yellow in alkaline ones. Red cabbage juice is a natural pH indicator."),
            t("Daher auch die zwei Namen: Im Norden kocht man Rotkohl oft mit Äpfeln oder Essig, und er wird rot. Im Süden heißt er Blaukraut, weil er ohne Säure eher blau bleibt. Dieselben Farbstoffe färben Heidelbeeren, Rotwein und viele Blüten. Hortensien blühen je nach Boden blau oder rosa, wobei dort zusätzlich Aluminium im Boden mitspielt.", "That explains its two German names: in the north red cabbage is often cooked with apples or vinegar and turns red. In the south it's called 'blue cabbage', because without acid it stays more blue. The same dyes colour blueberries, red wine and many flowers. Hydrangeas flower blue or pink depending on the soil, though aluminium in the soil plays a part there too."),
        ),
        mc("chem.fettbrand", Topic.Chemistry, E,
            t("In einer Pfanne fängt heißes Öl Feuer. Was ist die richtige Reaktion?", "Hot oil catches fire in a frying pan. What is the right thing to do?"),
            t("Reaktion", "Reaction"),
            t("Herd ausschalten und einen Deckel auf die Pfanne legen", "Turn off the hob and put a lid on the pan"),
            listOf(t("Ein Glas Wasser hineinschütten", "Pour a glass of water in"), t("Kräftig pusten", "Blow hard on it"), t("Die Pfanne schnell ins Spülbecken tragen und Wasser aufdrehen", "Carry the pan quickly to the sink and turn on the tap")),
            t("Kernidee: Feuer braucht Brennstoff, Sauerstoff und Hitze. Welches davon kannst du am sichersten wegnehmen?", "Key idea: fire needs fuel, oxygen and heat. Which of them can you take away most safely?"),
            t("Was passiert mit Wasser, das in 300 °C heißes Öl fällt?", "What happens to water that falls into oil at 300 °C?"),
            t("Der Deckel nimmt dem Feuer den Sauerstoff, das Abschalten die Hitze. Wasser dagegen sinkt unter das Öl, verdampft schlagartig zum 1700-fachen Volumen und reißt brennendes Öl als Feuerball mit.", "The lid takes the oxygen away from the fire, switching off removes the heat. Water, on the other hand, sinks beneath the oil, instantly turns to steam with 1700 times the volume and carries burning oil with it as a fireball."),
            t("Das Verbrennungsdreieck erklärt jede Löschmethode: Wasser kühlt, Decken und Schaum ersticken, und beim Waldbrand schlägt man Schneisen, um den Brennstoff zu entziehen. Weil Fettbrände so gefährlich sind, gibt es dafür eigene Feuerlöscher der Klasse F. Die Feuerwehr zeigt oft mit nur einem Becher Wasser, welche Stichflamme entsteht.", "The fire triangle explains every method of putting out a fire: water cools, blankets and foam smother, and in a forest fire firebreaks are cut to remove the fuel. Because fat fires are so dangerous there are special class F fire extinguishers for them. Fire brigades often demonstrate the huge flare produced by just one cup of water."),
        ),
        // ------------------------------------------------ mittel
        lab("chem.benzin", Topic.Chemistry, M, 0.55f) {
            val litres = pick(1.0, 50.0, 7.0); val kgFuel = litres * 0.75; val co2 = kgFuel * 8 * 44 / 114
            task(
                question = l("Benzin besteht vor allem aus Octan (C₈H₁₈, 114 g/mol) und hat eine Dichte von 0,75 kg/l. Beim Verbrennen wird aus jedem Kohlenstoffatom ein CO₂-Molekül (44 g/mol). Wie viel CO₂ entsteht aus ${n(litres, 0)} Liter Benzin?", "Petrol consists mainly of octane (C₈H₁₈, 114 g/mol) and has a density of 0.75 kg/l. On burning, every carbon atom becomes a CO₂ molecule (44 g/mol). How much CO₂ is produced from ${n(litres, 0)} litre${if (litres > 1) "s" else ""} of petrol?"),
                steps = listOf(num(l("CO₂ in kg", "CO₂ in kg"), co2, "kg", rel = 0.03, done = """§\approx ${m(co2, if (co2 < 10) 2 else 0)}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Aus einem Octan-Molekül entstehen 8 CO₂-Moleküle. Vergleiche ihre Massen: 114 g Octan werden zu 8 · 44 g CO₂.", "Key idea: one octane molecule produces 8 CO₂ molecules. Compare their masses: 114 g of octane becomes 8 · 44 g of CO₂."), """§m_{CO_2} = m_{Benzin} \cdot \dfrac{8 \cdot 44}{114}§""".let { l(it, it.replace("Benzin", "petrol")) }))),
                solution = listOf("""§m_B = ${m(kgFuel, 2)}\ \text{kg},\quad m_{CO_2} = ${m(kgFuel, 2)} \cdot \dfrac{352}{114} \approx ${m(co2, if (co2 < 10) 2 else 0)}\ \text{kg}§"""),
                background = l("Aus einem Kilogramm Benzin werden über drei Kilogramm CO₂. Das wirkt paradox, aber der Sauerstoff kommt aus der Luft dazu: Jedes Kohlenstoffatom mit Masse 12 holt sich zwei Sauerstoffatome mit zusammen 32. Ein Auto, das 6 Liter auf 100 km braucht, stößt so etwa 140 g CO₂ pro Kilometer aus, mehr als es an Benzin verbraucht.", "One kilogram of petrol becomes more than three kilograms of CO₂. That seems paradoxical, but the oxygen comes in from the air: each carbon atom with mass 12 picks up two oxygen atoms weighing 32 together. A car using 6 litres per 100 km thus emits about 140 g of CO₂ per kilometre, more than the petrol it burns."),
            )
        },
        lab("chem.verduennen", Topic.Chemistry, M, 0.45f) {
            val vol = pick(50, 20, 100); val c0 = 25; val c1 = pick(5, 10)
            val water = vol * c0 / c1 - vol
            task(
                question = l("Essigessenz enthält ${c0} % Essigsäure. Für einen Salat willst du Essig mit ${c1} %. Wie viel Wasser musst du zu ${vol} ml Essigessenz geben?", "Vinegar essence contains ${c0}% acetic acid. For a salad you want vinegar with ${c1}%. How much water do you have to add to ${vol} ml of vinegar essence?"),
                steps = listOf(num(l("Wasser in ml", "Water in ml"), water.toDouble(), "ml", abs = 0.0, done = "${water} ml",
                    hints = listOf(l("Kernidee: Die Menge Essigsäure bleibt gleich, sie verteilt sich nur auf mehr Flüssigkeit.", "Key idea: the amount of acetic acid stays the same; it's just spread over more liquid."), """§c_1 V_1 = c_2 V_2 \Rightarrow V_2 = \dfrac{${c0}}{${c1}} \cdot ${vol}\ \text{ml}§"""))),
                solution = listOf(l("Endvolumen: ${c0}/${c1} · ${vol} ml = ${vol + water} ml. Davon sind ${vol} ml Essenz, also ${water} ml Wasser.", "Final volume: ${c0}/${c1} · ${vol} ml = ${vol + water} ml. ${vol} ml of that is essence, so ${water} ml of water.")),
                background = l("Viele rechnen hier falsch, weil sie das Wasser ausrechnen statt der Gesamtmenge. Die Faustregel c₁ · V₁ = c₂ · V₂ gilt für jede Verdünnung, ob Saft, Medikament oder Laborlösung. Essigessenz ist übrigens gefährlich: Mit 25 % Säure verätzt sie Haut und Schleimhäute, deshalb steht auf der Flasche, dass man sie nie unverdünnt verwenden soll.", "Many people get this wrong because they calculate the water instead of the total amount. The rule c₁ · V₁ = c₂ · V₂ holds for every dilution, whether juice, medicine or a lab solution. Vinegar essence is dangerous, by the way: at 25% acid it burns skin and mucous membranes, which is why the bottle says never to use it undiluted."),
            )
        },
        lab("chem.ionenformel", Topic.Chemistry, M, 0.5f) {
            val k = int(0, 3)
            val metal = listOf(l("Magnesium (gibt 2 Elektronen ab)", "Magnesium (gives up 2 electrons)"), l("Aluminium (gibt 3 Elektronen ab)", "Aluminium (gives up 3 electrons)"), l("Natrium (gibt 1 Elektron ab)", "Sodium (gives up 1 electron)"), l("Calcium (gibt 2 Elektronen ab)", "Calcium (gives up 2 electrons)"))[k]
            val nonm = listOf(l("Chlor (nimmt 1 Elektron auf)", "chlorine (takes 1 electron)"), l("Sauerstoff (nimmt 2 Elektronen auf)", "oxygen (takes 2 electrons)"), l("Sauerstoff (nimmt 2 Elektronen auf)", "oxygen (takes 2 electrons)"), l("Chlor (nimmt 1 Elektron auf)", "chlorine (takes 1 electron)"))[k]
            val right = listOf("MgCl₂", "Al₂O₃", "Na₂O", "CaCl₂")[k]
            val wr = listOf(listOf("MgCl", "Mg₂Cl", "Mg₂Cl₃"), listOf("AlO", "Al₃O₂", "AlO₃"), listOf("NaO", "NaO₂", "Na₃O"), listOf("CaCl", "Ca₂Cl", "CaCl₃"))[k]
            task(
                question = l("$metal reagiert mit $nonm zu einem Salz. Welche Formel hat es?", "$metal reacts with $nonm to form a salt. What is its formula?"),
                steps = listOf(pick1(l("Formel", "Formula"), right, wr,
                    hints = listOf(l("Kernidee: Ein Salz ist insgesamt ungeladen. Es müssen genau so viele Elektronen abgegeben wie aufgenommen werden.", "Key idea: a salt has no overall charge. Exactly as many electrons must be given up as are taken."), l("Suche das kleinste gemeinsame Vielfache der beiden Elektronenzahlen.", "Look for the lowest common multiple of the two electron numbers.")))),
                solution = listOf(l("In ${right} gleichen sich die Ladungen genau aus: Die Metallionen geben insgesamt so viele Elektronen ab, wie die Nichtmetallionen aufnehmen.", "In ${right} the charges balance exactly: the metal ions give up in total as many electrons as the non-metal ions take.")),
                background = l("Metalle geben Elektronen ab, bis sie die Elektronenhülle des nächsten Edelgases haben, Nichtmetalle nehmen welche auf. Die entgegengesetzt geladenen Ionen ziehen sich an und bilden ein festes Gitter. Deshalb sind Salze hart, spröde und schmelzen erst bei Hunderten Grad. Tonerde, Al₂O₃, ist als Korund nach dem Diamanten eines der härtesten Minerale, rot gefärbt heißt sie Rubin.", "Metals give up electrons until they have the electron shell of the nearest noble gas; non-metals take some on. The oppositely charged ions attract one another and form a solid lattice. That's why salts are hard and brittle and only melt at hundreds of degrees. Alumina, Al₂O₃, is, as corundum, one of the hardest minerals after diamond; coloured red it's called ruby."),
            )
        },
        mc("chem.rost", Topic.Chemistry, M,
            t("Warum rosten Eisengeländer am Meer und Autos im Winter mit Streusalz viel schneller als in trockener Stadtluft?", "Why do iron railings by the sea, and cars in winter with road salt, rust much faster than in dry city air?"),
            t("Grund", "Reason"),
            t("Salzwasser leitet Strom und beschleunigt so den Elektronenfluss vom Eisen zum Sauerstoff.", "Salt water conducts electricity and so speeds up the flow of electrons from the iron to the oxygen."),
            listOf(t("Salz enthält Sauerstoff, der das Eisen angreift.", "Salt contains oxygen, which attacks the iron."), t("Salz macht das Eisen weicher.", "Salt makes the iron softer."), t("Salz zieht die Sonnenstrahlung an.", "Salt attracts the Sun's radiation.")),
            t("Kernidee: Rosten ist eine Redoxreaktion: Eisen gibt Elektronen ab, Sauerstoff nimmt sie auf. Dafür braucht es einen Weg für Ladungen.", "Key idea: rusting is a redox reaction: iron gives up electrons and oxygen takes them. That needs a path for charges."),
            t("Reines Wasser leitet kaum, Salzwasser sehr gut.", "Pure water hardly conducts; salt water conducts very well."),
            t("Die Ionen im Salzwasser machen den Wasserfilm auf dem Metall zu einem Elektrolyten, wie in einer Batterie. So laufen die Teilreaktionen des Rostens viel schneller ab.", "The ions in salt water turn the film of water on the metal into an electrolyte, like in a battery. That makes the partial reactions of rusting run much faster."),
            t("Rost braucht immer Wasser und Sauerstoff, in trockener Wüstenluft hält Eisen erstaunlich lange. Man schützt Stahl mit Farbe oder mit einer Zinkschicht: Zink gibt seine Elektronen noch lieber ab als Eisen und opfert sich, solange es da ist. Schiffe tragen aus demselben Grund Zinkblöcke am Rumpf, die regelmäßig ersetzt werden.", "Rust always needs water and oxygen; in dry desert air iron lasts astonishingly long. Steel is protected with paint or a layer of zinc: zinc gives up its electrons even more readily than iron and sacrifices itself as long as it's there. For the same reason ships carry zinc blocks on their hulls, which are replaced regularly."),
        ),
        lab("chem.trockeneis", Topic.Chemistry, M, 0.55f) {
            val kg = pick(1.0, 0.5, 2.0); val mol = kg * 1000 / 44; val litres = mol * 24
            task(
                question = l("Trockeneis ist gefrorenes CO₂ (44 g/mol). Es geht beim Erwärmen direkt in Gas über. Ein Mol Gas nimmt bei Zimmertemperatur etwa 24 Liter ein. Wie viel Liter Gas entstehen aus ${n(kg, 1)} kg Trockeneis?", "Dry ice is frozen CO₂ (44 g/mol). On warming it turns straight into gas. One mole of gas takes up about 24 litres at room temperature. How many litres of gas are produced from ${n(kg, 1)} kg of dry ice?"),
                steps = listOf(num(l("Gasvolumen in Litern", "Gas volume in litres"), litres, "l", rel = 0.03, done = """§\approx ${m(litres, 0)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Erst die Masse in Mol umrechnen, dann jedes Mol mit 24 Litern multiplizieren. Das gilt für jedes Gas.", "Key idea: first convert the mass into moles, then multiply each mole by 24 litres. That holds for any gas."), """§n = \dfrac{${m(kg * 1000, 0)}\ \text{g}}{44\ \text{g/mol}}§"""))),
                solution = listOf("""§n \approx ${m(mol, 1)}\ \text{mol},\quad V \approx ${m(mol, 1)} \cdot 24\ \text{l} \approx ${m(litres, 0)}\ \text{l}§"""),
                background = l("Ein Gas nimmt etwa 500- bis 800-mal so viel Raum ein wie der Feststoff, aus dem es entsteht. Deshalb darf man Trockeneis nie in eine verschlossene Flasche füllen, sie würde platzen. In geschlossenen Räumen kann das CO₂ zudem die Atemluft verdrängen. Die erstaunliche Regel, dass jedes Gas pro Mol gleich viel Platz braucht, fand Amedeo Avogadro schon 1811.", "A gas takes up roughly 500 to 800 times as much space as the solid it comes from. That's why you must never put dry ice in a sealed bottle; it would burst. In closed rooms the CO₂ can also push out the air you breathe. The astonishing rule that every gas takes up the same space per mole was found by Amedeo Avogadro as early as 1811."),
            )
        },
        lab("chem.rgtregel", Topic.Chemistry, M, 0.55f) {
            val (tLow, f) = pick(15 to 2, 5 to 4)
            task(
                question = l("Faustregel: 10 °C mehr verdoppeln ungefähr die Geschwindigkeit chemischer Reaktionen. Angenommen, Milch verdirbt bei 25 °C in einem Tag. Wie viele Tage hält sie dann bei ${tLow} °C?", "Rule of thumb: 10 °C more roughly doubles the speed of chemical reactions. Suppose milk goes off in one day at 25 °C. How many days will it then last at ${tLow} °C?"),
                steps = listOf(num(l("Tage", "Days"), f.toDouble(), "", abs = 0.0, done = l("etwa $f Tage", "about $f days"),
                    hints = listOf(l("Kernidee: Zähle die 10-Grad-Schritte zwischen den Temperaturen. Jeder halbiert das Tempo.", "Key idea: count the 10-degree steps between the temperatures. Each halves the speed."), l("Von 25 °C auf ${tLow} °C sind es ${(25 - tLow) / 10} Schritte.", "From 25 °C to ${tLow} °C is ${(25 - tLow) / 10} steps.")))),
                solution = listOf("""§2^{${(25 - tLow) / 10}} = ${f}§""", l("Bei ${tLow} °C läuft der Verderb ${f}-mal langsamer, die Milch hält etwa ${f} Tage.", "At ${tLow} °C spoiling runs ${f} times more slowly, so the milk lasts about ${f} days.")),
                background = l("Diese Reaktionsgeschwindigkeit-Temperatur-Regel stammt von Jacobus van ’t Hoff. Sie ist nur grob, je nach Reaktion sind es das Zwei- bis Vierfache. Sie erklärt, warum Kühlschränke und Gefriertruhen Lebensmittel so viel länger frisch halten, und auch, warum Fieber den Stoffwechsel beschleunigt und Eidechsen in der Kälte so träge sind.", "This rule linking reaction rate and temperature comes from Jacobus van 't Hoff. It's only rough; depending on the reaction it's two to four times. It explains why fridges and freezers keep food fresh so much longer, and also why a fever speeds up the metabolism and lizards are so sluggish in the cold."),
            )
        },
        mc("chem.sprudel", Topic.Chemistry, M,
            t("Eine Flasche Sprudel, die in der Sonne stand, sprudelt beim Öffnen viel stärker über als eine aus dem Kühlschrank, und sie wird schneller schal. Warum?", "A bottle of fizzy water that has stood in the sun foams over much more when opened than one from the fridge, and it goes flat faster. Why?"),
            t("Grund", "Reason"),
            t("In warmem Wasser löst sich weniger Gas, das CO₂ will hinaus.", "Less gas dissolves in warm water, so the CO₂ wants to get out."),
            listOf(t("Die Sonne erzeugt neues CO₂ in der Flasche.", "The sun produces new CO₂ in the bottle."), t("Warmes Wasser ist dünnflüssiger und schäumt deshalb.", "Warm water is runnier and therefore foams."), t("Kälte zerstört die Kohlensäure.", "Cold destroys the carbonation.")),
            t("Kernidee: Bei Feststoffen wie Zucker gilt: Je wärmer, desto mehr löst sich. Bei Gasen ist es umgekehrt.", "Key idea: for solids like sugar the rule is: the warmer, the more dissolves. For gases it's the other way round."),
            t("Gasteilchen bewegen sich in warmem Wasser schneller und entkommen leichter.", "Gas particles move faster in warm water and escape more easily."),
            t("Die Löslichkeit von Gasen sinkt mit steigender Temperatur. Warmer Sprudel enthält mehr CO₂, als er eigentlich halten kann, und gibt es beim Öffnen schlagartig ab.", "The solubility of gases falls as temperature rises. Warm fizzy water contains more CO₂ than it can really hold and releases it all at once when opened."),
            t("Dasselbe gilt für Sauerstoff, und das hat Folgen: In warmen Sommern sinkt der Sauerstoffgehalt von Seen und Flüssen, und Fische ersticken. Kraftwerke dürfen ihr Kühlwasser deshalb nur begrenzt aufgewärmt in Flüsse leiten. Auch die Ozeane nehmen weniger CO₂ auf, je wärmer sie werden.", "The same holds for oxygen, and that has consequences: in warm summers the oxygen content of lakes and rivers falls, and fish suffocate. That's why power stations may only discharge their cooling water into rivers to a limited temperature. The oceans also take up less CO₂ the warmer they get."),
        ),
        mc("chem.nudelwasser", Topic.Chemistry, M,
            t("Viele salzen Nudelwasser, „damit es heißer kocht“. Um wie viel steigt der Siedepunkt, wenn du einen gehäuften Teelöffel Salz (10 g) in einen Liter Wasser gibst?", "Many people salt pasta water 'so that it boils hotter'. By how much does the boiling point rise if you put a heaped teaspoon of salt (10 g) into a litre of water?"),
            t("Anstieg", "Rise"),
            t("Um etwa 0,2 °C, also praktisch gar nicht", "By about 0.2 °C, so practically not at all"),
            listOf(t("Um etwa 5 °C", "By about 5 °C"), t("Um etwa 20 °C", "By about 20 °C"), t("Er sinkt um etwa 2 °C.", "It falls by about 2 °C.")),
            t("Kernidee: Der Effekt hängt von der Zahl gelöster Teilchen ab. Wie viele Mol Salz sind 10 g? (NaCl: 58,5 g/mol)", "Key idea: the effect depends on the number of dissolved particles. How many moles of salt is 10 g? (NaCl: 58.5 g/mol)"),
            t("Pro Mol gelöster Teilchen in einem Kilogramm Wasser steigt der Siedepunkt nur um etwa 0,5 °C. Jedes NaCl liefert zwei Ionen.", "For each mole of dissolved particles in one kilogram of water the boiling point rises by only about 0.5 °C. Each NaCl gives two ions."),
            t("10 g Salz sind 0,17 mol, das ergibt 0,34 mol Ionen. Mal 0,5 °C pro Mol: rund 0,2 °C. Salz im Nudelwasser ist Geschmackssache, nicht Physik.", "10 g of salt is 0.17 mol, giving 0.34 mol of ions. Times 0.5 °C per mole: around 0.2 °C. Salt in pasta water is a matter of taste, not physics."),
            t("Gelöste Teilchen erschweren es den Wassermolekülen, die Flüssigkeit zu verlassen, deshalb steigt der Siedepunkt und sinkt der Gefrierpunkt. Bei Streusalz auf der Straße wirkt dasselbe stark, weil sehr viel Salz in wenig Wasser kommt. Dass es beim Salzen kurz aufschäumt, liegt nur daran, dass die Körnchen Keime für Dampfblasen sind.", "Dissolved particles make it harder for water molecules to leave the liquid, so the boiling point rises and the freezing point falls. With road salt the same effect is strong, because a lot of salt meets little water. The brief foaming when you add salt is only because the grains act as seeds for steam bubbles."),
        ),
        lab("chem.naehrwert", Topic.Chemistry, M, 0.4f) {
            val fat = pick(20, 12, 8); val carb = pick(30, 45, 60); val prot = pick(5, 10, 3)
            val kcal = fat * 9 + carb * 4 + prot * 4
            task(
                question = l("Auf einem Müsliriegel steht: ${fat} g Fett, ${carb} g Kohlenhydrate, ${prot} g Eiweiß. Fett liefert etwa 9 kcal pro Gramm, Kohlenhydrate und Eiweiß je etwa 4 kcal. Wie viel Energie steckt im Riegel?", "A cereal bar's label says: ${fat} g fat, ${carb} g carbohydrate, ${prot} g protein. Fat provides about 9 kcal per gram, carbohydrate and protein about 4 kcal each. How much energy is in the bar?"),
                steps = listOf(num(l("Energie in kcal", "Energy in kcal"), kcal.toDouble(), "kcal", abs = 0.0, done = "$kcal kcal",
                    hints = listOf(l("Kernidee: Jeder Nährstoff einzeln mal seinem Energiewert, dann addieren.", "Key idea: each nutrient times its energy value, then add them up."), """§${fat} \cdot 9 + ${carb} \cdot 4 + ${prot} \cdot 4§"""))),
                solution = listOf("""§${fat * 9} + ${carb * 4} + ${prot * 4} = ${kcal}\ \text{kcal}§"""),
                background = l("Fett liefert mehr als doppelt so viel Energie pro Gramm, weil seine Moleküle fast nur aus Kohlenstoff und Wasserstoff bestehen und kaum schon an Sauerstoff gebunden sind. Diese Faktoren bestimmte Wilbur Atwater um 1900, indem er Lebensmittel in einem Kalorimeter verbrannte und abzog, was der Körper unverdaut wieder ausscheidet. 1 kcal erwärmt einen Liter Wasser um 1 °C.", "Fat provides more than twice as much energy per gram because its molecules consist almost entirely of carbon and hydrogen and are hardly bound to oxygen yet. Wilbur Atwater determined these factors around 1900 by burning foods in a calorimeter and subtracting what the body excretes undigested. 1 kcal warms one litre of water by 1 °C."),
            )
        },
        mc("chem.seife", Topic.Chemistry, M,
            t("Fett lässt sich mit Wasser allein nicht abwaschen. Mit Seife klappt es. Was macht die Seife?", "Grease won't wash off with water alone. With soap it works. What does the soap do?"),
            t("Wirkung", "Effect"),
            t("Ihre Teilchen haben ein wasserliebendes und ein fettliebendes Ende und packen das Fett in winzige Kügelchen, die im Wasser schwimmen.", "Its particles have a water-loving end and a fat-loving end and wrap the grease into tiny balls that float in the water."),
            listOf(t("Sie verbrennt das Fett chemisch zu Wasser.", "It burns the grease chemically into water."), t("Sie macht das Wasser heißer, sodass Fett schmilzt.", "It makes the water hotter so the grease melts."), t("Sie macht das Fett schwerer, sodass es absinkt.", "It makes the grease heavier so it sinks.")),
            t("Kernidee: Öl und Wasser mischen sich nicht. Man braucht ein Teilchen, das mit beiden kann.", "Key idea: oil and water don't mix. You need a particle that gets on with both."),
            t("Ein Seifenteilchen ist wie eine Stecknadel: Der Kopf liebt Wasser, der lange Schwanz liebt Fett.", "A soap particle is like a pin: the head loves water, the long tail loves fat."),
            t("Die fettliebenden Schwänze bohren sich ins Fett, die wasserliebenden Köpfe zeigen nach außen. So entstehen Mizellen, kleine Fettkugeln mit wasserfreundlicher Hülle, die das Wasser wegspült.", "The fat-loving tails burrow into the grease, the water-loving heads point outwards. This creates micelles, little balls of fat with a water-friendly coat, which the water rinses away."),
            t("Seife ist eine der ältesten Chemikalien, die Babylonier kochten sie schon vor 4000 Jahren aus Fett und Asche. Dieselbe Eigenschaft macht sie gegen viele Viren wirksam: Deren Hülle besteht aus Fett, und Seife reißt sie auf. Deshalb hilft gründliches Händewaschen mit Seife gegen Grippe und Corona besser als Wasser allein.", "Soap is one of the oldest chemicals; the Babylonians were boiling it from fat and ash 4000 years ago. The same property makes it effective against many viruses: their envelope is made of fat, and soap tears it apart. That's why thorough hand-washing with soap helps against flu and coronavirus better than water alone."),
        ),
        // ------------------------------------------------ schwer
        mc("chem.phgrenze", Topic.Chemistry, H,
            t("Eine Säure hat pH 3. Du verdünnst sie mit reinem Wasser auf das Millionenfache. Welchen pH-Wert hat die Lösung danach?", "An acid has a pH of 3. You dilute it with pure water a millionfold. What is the pH of the solution afterwards?"),
            t("pH-Wert", "pH"),
            t("Knapp unter 7", "Just below 7"),
            listOf(t("9", "9"), t("Weiterhin 3", "Still 3"), t("Deutlich über 7, also basisch", "Well above 7, so alkaline")),
            t("Kernidee: Jede Verzehnfachung hebt den pH-Wert um 1. Rechnerisch käme 3 + 6 = 9 heraus. Aber kann eine verdünnte Säure basisch werden?", "Key idea: each tenfold dilution raises the pH by 1. On paper you'd get 3 + 6 = 9. But can a diluted acid become alkaline?"),
            t("Auch reines Wasser enthält H⁺-Ionen: 10⁻⁷ mol pro Liter. Die Säure liefert nach der Verdünnung nur noch 10⁻⁹.", "Pure water also contains H⁺ ions: 10⁻⁷ mol per litre. After dilution the acid only provides 10⁻⁹."),
            t("Die Säure trägt nur noch ein Hundertstel zu den H⁺-Ionen bei, die das Wasser selbst liefert. Der pH-Wert nähert sich deshalb 7 von unten, er kann durch Verdünnen nie basisch werden.", "The acid now only adds a hundredth to the H⁺ ions the water provides itself. So the pH approaches 7 from below; diluting can never make it alkaline."),
            t("Formeln gelten nur, solange ihre Annahmen stimmen. Die Regel „zehnfach verdünnt, pH plus eins“ übersieht die Eigendissoziation des Wassers, die erst bei extremer Verdünnung zählt. Genau dieses Argument zeigt auch, warum homöopathische Verdünnungen wie D30 chemisch nur noch reines Wasser sind: Dort ist im Mittel kein einziges Wirkstoffmolekül mehr übrig.", "Formulas only hold as long as their assumptions are true. The rule 'ten times more dilute, pH plus one' overlooks the self-ionisation of water, which only matters at extreme dilution. The same kind of reasoning shows why homeopathic dilutions such as 30X are chemically just pure water: on average not a single molecule of the active substance is left."),
        ),
        mc("chem.wasserstoffbruecken", Topic.Chemistry, H,
            t("Wasser (H₂O, 18 g/mol) ist bis 100 °C flüssig. Schwefelwasserstoff (H₂S, 34 g/mol) ist fast doppelt so schwer gebaut und ähnlich aufgebaut, kocht aber schon bei −60 °C. Warum?", "Water (H₂O, 18 g/mol) stays liquid up to 100 °C. Hydrogen sulphide (H₂S, 34 g/mol) is almost twice as heavy and built similarly, yet it boils at −60 °C. Why?"),
            t("Grund", "Reason"),
            t("Wassermoleküle halten über Wasserstoffbrücken besonders fest zusammen.", "Water molecules hold together especially tightly through hydrogen bonds."),
            listOf(t("Schwefel ist ein Metall.", "Sulphur is a metal."), t("Wasser ist schwerer, als die Molmasse sagt.", "Water is heavier than its molar mass says."), t("H₂S-Moleküle stoßen sich elektrisch ab.", "H₂S molecules repel each other electrically.")),
            t("Kernidee: Sauerstoff zieht Elektronen viel stärker an als Schwefel. Was bedeutet das für die Ladung der Wasserstoffatome?", "Key idea: oxygen attracts electrons much more strongly than sulphur. What does that mean for the charge on the hydrogen atoms?"),
            t("Die leicht positiven Wasserstoffatome eines Wassermoleküls werden vom leicht negativen Sauerstoff des Nachbarn angezogen.", "The slightly positive hydrogen atoms of one water molecule are attracted by the slightly negative oxygen of the next."),
            t("Im Wasser sind die Ladungen stark verschoben, und jedes Molekül hängt sich über Wasserstoffbrücken an bis zu vier Nachbarn. Um sie zu trennen, braucht es viel Energie. Beim Schwefelwasserstoff fehlt diese Anziehung fast ganz.", "In water the charges are strongly shifted, and each molecule links to up to four neighbours via hydrogen bonds. Separating them takes a lot of energy. In hydrogen sulphide this attraction is almost entirely missing."),
            t("Ohne Wasserstoffbrücken wäre Wasser bei Zimmertemperatur ein Gas, und es gäbe keine Ozeane und kein Leben, wie wir es kennen. Sie sind auch der Grund, warum Eis leichter ist als Wasser: Im Kristall halten die Brücken die Moleküle auf Abstand. Und sie halten die beiden Stränge der DNA zusammen.", "Without hydrogen bonds water would be a gas at room temperature, and there would be no oceans and no life as we know it. They're also why ice is lighter than water: in the crystal the bonds keep the molecules at a distance. And they hold the two strands of DNA together."),
        ),
        lab("chem.isotopenmittel", Topic.Chemistry, H, 0.6f) {
            val k = int(0, 2)
            val el = listOf(l("Chlor", "Chlorine"), l("Bor", "Boron"), l("Kupfer", "Copper"))[k]
            val a1 = listOf(35, 10, 63)[k]; val p1 = listOf(75.8, 19.9, 69.2)[k]; val a2 = listOf(37, 11, 65)[k]
            val avg = a1 * p1 / 100 + a2 * (100 - p1) / 100
            task(
                question = l("$el kommt in der Natur als Gemisch zweier Isotope vor: ${n(p1, 1)} % mit der Massenzahl ${a1} und ${n(100 - p1, 1)} % mit ${a2}. Welche mittlere Atommasse steht deshalb ungefähr im Periodensystem?", "$el occurs in nature as a mixture of two isotopes: ${n(p1, 1)}% with mass number ${a1} and ${n(100 - p1, 1)}% with ${a2}. So roughly what average atomic mass appears in the periodic table?"),
                steps = listOf(num(l("Mittlere Atommasse", "Average atomic mass"), avg, "u", abs = 0.02, done = """§\approx ${m(avg, 2)}\ \text{u}§""",
                    hints = listOf(l("Kernidee: Ein gewichteter Durchschnitt: Jede Masse zählt so stark, wie ihr Anteil ist.", "Key idea: a weighted average: each mass counts as strongly as its share."), """§\bar{m} = ${m(p1 / 100, 3)} \cdot ${a1} + ${m(1 - p1 / 100, 3)} \cdot ${a2}§"""))),
                solution = listOf("""§\bar{m} = ${m(p1 / 100, 3)} \cdot ${a1} + ${m(1 - p1 / 100, 3)} \cdot ${a2} \approx ${m(avg, 2)}§"""),
                background = l("Deshalb stehen im Periodensystem krumme Zahlen wie 35,45 für Chlor, obwohl jedes einzelne Atom eine ganze Massenzahl hat. Isotope verhalten sich chemisch fast gleich, haben aber verschiedene Massen. Das nutzen Forscher: Das Verhältnis von Sauerstoff-18 zu Sauerstoff-16 im Eis verrät die Temperatur vor Hunderttausenden Jahren.", "That's why the periodic table has awkward numbers like 35.45 for chlorine, even though every single atom has a whole mass number. Isotopes behave almost identically in chemistry but have different masses. Researchers make use of this: the ratio of oxygen-18 to oxygen-16 in ice reveals the temperature hundreds of thousands of years ago."),
            )
        },
        lab("chem.ozeanversauerung", Topic.Chemistry, H, 0.6f) {
            val (ph0, ph1) = pick(8.2 to 8.1, 8.2 to 7.9, 8.1 to 7.8)
            val inc = (10.0.pow(ph0 - ph1) - 1) * 100
            task(
                question = l("Der pH-Wert des Meerwassers ist durch gelöstes CO₂ von etwa ${n(ph0, 1)} auf ${n(ph1, 1)} gesunken. Um wie viel Prozent ist die Konzentration der H⁺-Ionen gestiegen?", "The pH of seawater has fallen from about ${n(ph0, 1)} to ${n(ph1, 1)} because of dissolved CO₂. By what percentage has the concentration of H⁺ ions risen?"),
                steps = listOf(num(l("Anstieg in Prozent", "Increase in percent"), inc, "%", rel = 0.03, done = """§\approx ${m(inc, 0)}\ \%§""",
                    hints = listOf(l("Kernidee: Der pH-Wert ist ein Logarithmus. Eine pH-Stufe weniger heißt zehnmal so viele H⁺-Ionen.", "Key idea: pH is a logarithm. One pH unit lower means ten times as many H⁺ ions."), """§\dfrac{[\text{H}^+]_{neu}}{[\text{H}^+]_{alt}} = 10^{${m(ph0 - ph1, 1)}}§""".let { l(it, it.replace("neu", "new").replace("alt", "old")) }))),
                solution = listOf("""§10^{${m(ph0 - ph1, 1)}} \approx ${m(10.0.pow(ph0 - ph1), 2)} \Rightarrow +${m(inc, 0)}\ \%§"""),
                background = l("Eine Änderung um 0,1 klingt winzig, bedeutet aber gut ein Viertel mehr Säure, weil die pH-Skala logarithmisch ist. Die Ozeane haben rund ein Viertel des vom Menschen ausgestoßenen CO₂ aufgenommen. Das bremst die Erwärmung, macht es aber Korallen, Muscheln und Plankton schwerer, ihre Kalkschalen zu bauen.", "A change of 0.1 sounds tiny but means a good quarter more acid, because the pH scale is logarithmic. The oceans have taken up around a quarter of the CO₂ emitted by humans. That slows warming, but it makes it harder for corals, shellfish and plankton to build their chalky shells."),
            )
        },
        lab("chem.elektrolyse", Topic.Chemistry, H, 0.65f) {
            val amp = pick(10.0, 5.0, 20.0); val grams = pick(1.0, 2.0)
            val q = grams * 96485; val hours = q / amp / 3600
            task(
                question = l("Bei der Elektrolyse von Wasser braucht jedes Wasserstoffatom ein Elektron. Ein Mol Elektronen trägt die Ladung 96.485 C, ein Mol Wasserstoffatome wiegt 1 g. Wie viele Stunden muss ein Strom von ${n(amp, 0)} A fließen, um ${n(grams, 0)} g Wasserstoff zu erzeugen?", "In the electrolysis of water every hydrogen atom needs one electron. One mole of electrons carries a charge of 96,485 C, and one mole of hydrogen atoms weighs 1 g. How many hours must a current of ${n(amp, 0)} A flow to produce ${n(grams, 0)} g of hydrogen?"),
                steps = listOf(num(l("Zeit in Stunden", "Time in hours"), hours, "h", rel = 0.03, done = """§\approx ${m(hours, 1)}\ \text{h}§""",
                    hints = listOf(l("Kernidee: ${n(grams, 0)} g Wasserstoff sind ${n(grams, 0)} mol Atome, dafür braucht es ${n(grams, 0)} mol Elektronen. Ladung = Strom mal Zeit.", "Key idea: ${n(grams, 0)} g of hydrogen is ${n(grams, 0)} mol of atoms, which needs ${n(grams, 0)} mol of electrons. Charge = current times time."), """§t = \dfrac{Q}{I} = \dfrac{${m(q, 0)}\ \text{C}}{${m(amp, 0)}\ \text{A}}§"""))),
                solution = listOf("""§t = \dfrac{${m(q, 0)}}{${m(amp, 0)}}\ \text{s} \approx ${m(q / amp, 0)}\ \text{s} \approx ${m(hours, 1)}\ \text{h}§"""),
                background = l("Michael Faraday entdeckte 1834, dass die abgeschiedene Stoffmenge genau der geflossenen Ladung entspricht. Das zeigte, dass Elektrizität in Portionen kommt, lange bevor man das Elektron kannte. Heute ist Elektrolyse mit Ökostrom ein Weg, Energie als Wasserstoff zu speichern. Für ein Kilogramm braucht man dabei rund 50 kWh.", "In 1834 Michael Faraday discovered that the amount of substance deposited corresponds exactly to the charge that has flowed. That showed electricity comes in portions, long before the electron was known. Today electrolysis with green electricity is one way to store energy as hydrogen. One kilogram needs around 50 kWh."),
            )
        },
    )

    private val biology = listOf(
        // ------------------------------------------------ leicht
        mc("bio.baummasse", Topic.Biology, E,
            t("Eine Buche wiegt nach hundert Jahren viele Tonnen. Woher stammt der größte Teil dieser Masse?", "After a hundred years a beech tree weighs many tonnes. Where does most of this mass come from?"),
            t("Herkunft", "Source"),
            t("Aus dem Kohlenstoffdioxid der Luft", "From the carbon dioxide in the air"),
            listOf(t("Aus der Erde unter dem Baum", "From the soil under the tree"), t("Aus den Mineralien im Dünger", "From the minerals in fertiliser"), t("Aus dem Sonnenlicht selbst", "From the sunlight itself")),
            t("Kernidee: Holz besteht vor allem aus Zellulose, und die besteht aus Kohlenstoff, Wasserstoff und Sauerstoff. Woher holt der Baum den Kohlenstoff?", "Key idea: wood consists mainly of cellulose, which is made of carbon, hydrogen and oxygen. Where does the tree get the carbon from?"),
            t("Unter Bäumen entsteht kein Loch im Boden.", "No hole appears in the ground under trees."),
            t("Bei der Fotosynthese bauen Pflanzen CO₂ aus der Luft und Wasser mit Sonnenenergie zu Zucker um. Fast die ganze Trockenmasse eines Baumes ist eingefangene Luft.", "In photosynthesis plants use solar energy to build CO₂ from the air and water into sugar. Almost the entire dry mass of a tree is captured air."),
            t("Jan Baptist van Helmont pflanzte im 17. Jahrhundert eine Weide in einen Kübel mit gewogener Erde. Nach fünf Jahren war die Weide 74 kg schwerer, die Erde hatte nur etwa 60 g verloren. Er schloss falsch, alles stamme aus dem Gießwasser, denn dass Luft Masse liefern kann, ahnte damals niemand. Die Sonne liefert nur die Energie, keine Masse.", "In the 17th century Jan Baptist van Helmont planted a willow in a tub of weighed soil. After five years the willow was 74 kg heavier, but the soil had lost only about 60 g. He wrongly concluded it all came from the watering, because at the time nobody imagined that air could supply mass. The Sun only provides the energy, not the mass."),
        ),
        lab("bio.atemluft", Topic.Biology, E, 0.35f) {
            val vol = 0.5; val rate = pick(15, 12, 16); val litres = vol * rate * 60 * 24
            task(
                question = l("In Ruhe atmest du etwa ${rate}-mal pro Minute, jedes Mal etwa einen halben Liter Luft. Wie viel Liter Luft atmest du an einem ganzen Tag?", "At rest you breathe about ${rate} times a minute, each time about half a litre of air. How many litres of air do you breathe in a whole day?"),
                steps = listOf(num(l("Liter pro Tag", "Litres per day"), litres, "l", rel = 0.02, done = """§${m(litres, 0)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Erst pro Minute, dann pro Stunde, dann pro Tag hochrechnen.", "Key idea: scale up first per minute, then per hour, then per day."), """§0{,}5\ \text{l} \cdot ${rate} \cdot 60 \cdot 24§"""))),
                solution = listOf("""§0{,}5 \cdot ${rate} \cdot 60 \cdot 24 = ${m(litres, 0)}\ \text{l}§"""),
                background = l("Das sind über zehn Kubikmeter am Tag, genug, um ein kleines Zimmer zu füllen. Die eingeatmete Luft enthält 21 % Sauerstoff, die ausgeatmete noch etwa 16 %. Der Körper holt sich also nur ein Viertel des Sauerstoffs heraus. Deshalb funktioniert Mund-zu-Mund-Beatmung: Deine Ausatemluft enthält noch reichlich Sauerstoff für einen anderen Menschen.", "That's over ten cubic metres a day, enough to fill a small room. The air you breathe in contains 21% oxygen, the air you breathe out still about 16%. So the body only extracts a quarter of the oxygen. That's why mouth-to-mouth resuscitation works: your exhaled air still contains plenty of oxygen for another person."),
            )
        },
        mc("bio.antibiotikavirus", Topic.Biology, E,
            t("Du hast eine echte Grippe, ausgelöst durch Influenzaviren. Hilft ein Antibiotikum dagegen?", "You have real flu, caused by influenza viruses. Will an antibiotic help against it?"),
            t("Antwort", "Answer"),
            t("Nein, Antibiotika wirken nur gegen Bakterien, nicht gegen Viren.", "No, antibiotics only work against bacteria, not viruses."),
            listOf(t("Ja, Antibiotika töten alle Krankheitserreger.", "Yes, antibiotics kill all germs."), t("Ja, aber nur in doppelter Dosis.", "Yes, but only in a double dose."), t("Nur wenn man es sehr früh nimmt.", "Only if you take it very early.")),
            t("Kernidee: Antibiotika greifen Bauteile an, die Bakterien haben, zum Beispiel ihre Zellwand. Haben Viren so etwas?", "Key idea: antibiotics attack components that bacteria have, such as their cell wall. Do viruses have anything like that?"),
            t("Ein Virus ist keine Zelle. Es vermehrt sich in deinen eigenen Zellen.", "A virus is not a cell. It reproduces inside your own cells."),
            t("Viren haben keine Zellwand und keinen eigenen Stoffwechsel, an dem ein Antibiotikum ansetzen könnte. Gegen manche Viren gibt es eigene antivirale Medikamente, und gegen Grippe hilft vorbeugend die Impfung.", "Viruses have no cell wall and no metabolism of their own that an antibiotic could act on. There are separate antiviral drugs against some viruses, and the vaccine helps prevent flu."),
            t("Antibiotika gegen Erkältungen und Grippe zu nehmen, schadet sogar: Sie töten nützliche Darmbakterien, und jede unnötige Einnahme fördert resistente Bakterien. Bakterien sind etwa hundertmal größer als typische Viren und unter dem Lichtmikroskop sichtbar, die meisten Viren sieht man erst im Elektronenmikroskop.", "Taking antibiotics for colds and flu even does harm: they kill useful gut bacteria, and every unnecessary dose encourages resistant bacteria. Bacteria are about a hundred times larger than typical viruses and can be seen under a light microscope; most viruses can only be seen with an electron microscope."),
        ),
        mc("bio.knochenzahl", Topic.Biology, E,
            t("Ein erwachsener Mensch hat 206 Knochen. Wie viele hat ein Neugeborenes?", "An adult human has 206 bones. How many does a newborn baby have?"),
            t("Anzahl", "Number"),
            t("Deutlich mehr, etwa 300", "Considerably more, about 300"),
            listOf(t("Genau gleich viele, 206", "Exactly the same, 206"), t("Etwa halb so viele", "About half as many"), t("Noch gar keine, nur Knorpel", "None at all yet, only cartilage")),
            t("Kernidee: Denk an die weiche Stelle am Kopf eines Babys, die Fontanelle.", "Key idea: think of the soft spot on a baby's head, the fontanelle."),
            t("Viele Knochen bestehen bei der Geburt noch aus mehreren Teilen.", "At birth many bones still consist of several parts."),
            t("Babys haben rund 300 Knochen und Knochenkerne. Im Lauf der Kindheit wachsen viele zusammen, etwa die Platten des Schädels, die Teile des Beckens und die Wirbel am Steißbein.", "Babies have around 300 bones and bone centres. During childhood many of them fuse, such as the plates of the skull, the parts of the pelvis and the vertebrae of the tailbone."),
            t("Die beweglichen Schädelplatten sind kein Zufall: Sie lassen den Kopf bei der Geburt etwas zusammengeschoben durch den engen Geburtskanal passen und geben dem schnell wachsenden Gehirn Platz. An den Enden der langen Knochen sitzen Wachstumsfugen, die sich erst mit etwa 20 Jahren schließen. Danach wächst man nicht mehr in die Höhe.", "The movable skull plates aren't a coincidence: they let the head be squeezed slightly to fit through the narrow birth canal and give the fast-growing brain room. At the ends of the long bones there are growth plates that only close at about 20. After that you don't grow taller any more."),
        ),
        mc("bio.blattgruen", Topic.Biology, E,
            t("Warum sind die meisten Blätter grün?", "Why are most leaves green?"),
            t("Grund", "Reason"),
            t("Das Chlorophyll schluckt rotes und blaues Licht für die Fotosynthese und wirft das grüne zurück.", "Chlorophyll absorbs red and blue light for photosynthesis and reflects the green."),
            listOf(t("Grün ist die Farbe, die Pflanzen am besten nutzen können.", "Green is the colour plants can use best."), t("Blätter enthalten grüne Farbe, um Tiere abzuschrecken.", "Leaves contain green colour to scare off animals."), t("Grünes Licht ist im Sonnenlicht am seltensten.", "Green light is the rarest in sunlight.")),
            t("Kernidee: Ein Gegenstand hat die Farbe des Lichts, das er nicht verschluckt.", "Key idea: an object has the colour of the light it doesn't absorb."),
            t("Unter grünem Licht wachsen Pflanzen schlecht, unter rotem und blauem gut.", "Plants grow poorly under green light and well under red and blue."),
            t("Chlorophyll absorbiert vor allem Rot und Blau und nutzt diese Energie. Das grüne Licht in der Mitte des Spektrums bleibt weitgehend übrig und wird zurückgeworfen, darum sehen wir Blätter grün.", "Chlorophyll mainly absorbs red and blue and uses that energy. The green light in the middle of the spectrum is largely left over and reflected, so we see leaves as green."),
            t("Im Herbst baut der Baum das wertvolle Chlorophyll ab, bevor er die Blätter abwirft. Dann werden gelbe und orange Carotinoide sichtbar, die die ganze Zeit schon da waren. Rote Farbtöne bilden manche Bäume dagegen erst im Herbst neu. Gewächshäuser mit LED-Licht leuchten deshalb oft lila: Grünes Licht wäre verschwendet.", "In autumn the tree breaks down the valuable chlorophyll before shedding its leaves. Then yellow and orange carotenoids, which were there all along, become visible. Red shades, on the other hand, are newly made by some trees only in autumn. That's why greenhouses with LED lighting often glow purple: green light would be wasted."),
        ),
        mc("bio.blinderfleck", Topic.Biology, E,
            t("In jedem deiner Augen gibt es eine Stelle, an der du gar nichts siehst, und trotzdem bemerkst du kein Loch im Bild. Wo liegt dieser blinde Fleck?", "In each of your eyes there is a spot where you see nothing at all, and yet you don't notice a hole in the picture. Where is this blind spot?"),
            t("Ort", "Location"),
            t("Dort, wo der Sehnerv die Netzhaut verlässt", "Where the optic nerve leaves the retina"),
            listOf(t("In der Mitte der Pupille", "In the middle of the pupil"), t("Am äußersten Rand der Linse", "At the very edge of the lens"), t("Hinter dem Augenlid", "Behind the eyelid")),
            t("Kernidee: Die Signale aller Sehzellen müssen irgendwie aus dem Auge hinaus ins Gehirn.", "Key idea: the signals from all the light-sensitive cells have to get out of the eye to the brain somehow."),
            t("Wo die Nervenfasern gebündelt das Auge verlassen, ist kein Platz für Sehzellen.", "Where the nerve fibres leave the eye in a bundle, there's no room for light-sensitive cells."),
            t("An der Austrittsstelle des Sehnervs sitzen keine Sehzellen. Das Gehirn füllt die Lücke mit dem, was drum herum ist, und das andere Auge deckt sie ohnehin ab.", "There are no light-sensitive cells where the optic nerve leaves. The brain fills the gap with what's around it, and the other eye covers it anyway."),
            t("Du kannst ihn finden: Mal ein Kreuz und daneben einen Punkt auf ein Blatt, schließ das linke Auge, schau mit dem rechten auf das Kreuz und bewege das Blatt langsam heran. Bei etwa 30 cm verschwindet der Punkt. Tintenfische haben übrigens keinen blinden Fleck, bei ihnen laufen die Nerven hinter der Netzhaut entlang.", "You can find it: draw a cross with a dot next to it on a sheet of paper, close your left eye, look at the cross with your right and slowly bring the sheet closer. At about 30 cm the dot vanishes. Octopuses, by the way, have no blind spot: their nerves run behind the retina."),
        ),
        lab("bio.mikroskop", Topic.Biology, E, 0.3f) {
            val (what, um) = pick(l("Ein rotes Blutkörperchen", "A red blood cell") to 7.5, l("Eine Hefezelle", "A yeast cell") to 5.0, l("Eine Zwiebelhautzelle", "An onion skin cell") to 250.0)
            val mag = pick(100, 400, 40); val mm = um * mag / 1000
            task(
                question = l("$what ist etwa ${n(um, 1)} µm groß (1 µm = 1/1000 mm). Wie groß erscheint es unter einem Mikroskop mit ${mag}-facher Vergrößerung?", "$what is about ${n(um, 1)} µm across (1 µm = 1/1000 mm). How large does it appear under a microscope with ${mag}× magnification?"),
                steps = listOf(num(l("Scheinbare Größe in mm", "Apparent size in mm"), mm, "mm", rel = 0.02, done = """§${m(mm, 1)}\ \text{mm}§""",
                    hints = listOf(l("Kernidee: Vergrößerung heißt einfach: Größe mal Faktor. Rechne danach µm in mm um.", "Key idea: magnification just means size times the factor. Then convert µm into mm."), """§${m(um, 1)}\ \mu\text{m} \cdot ${mag}§"""))),
                solution = listOf("""§${m(um, 1)}\ \mu\text{m} \cdot ${mag} = ${m(um * mag, 0)}\ \mu\text{m} = ${m(mm, 1)}\ \text{mm}§"""),
                background = l("Antoni van Leeuwenhoek baute im 17. Jahrhundert winzige Mikroskope mit einer einzigen Glasperle als Linse und sah als Erster Bakterien und Blutkörperchen. Mehr als etwa 1000-fache Vergrößerung bringt beim Lichtmikroskop nichts, weil Licht selbst zu grob ist: Dinge, die kleiner als etwa eine halbe Lichtwellenlänge sind, verschwimmen. Für Viren braucht man Elektronen.", "In the 17th century Antonie van Leeuwenhoek built tiny microscopes with a single glass bead as the lens and was the first to see bacteria and blood cells. With a light microscope, more than about 1000× magnification is pointless, because light itself is too coarse: things smaller than about half a wavelength of light blur. For viruses you need electrons."),
            )
        },
        // ------------------------------------------------ mittel
        lab("bio.erbsen", Topic.Biology, M, 0.5f) {
            val askWhite = chance(0.5)
            val p = if (askWhite) 25.0 else 75.0
            task(
                question = l("Bei Mendels Erbsen ist lila Blütenfarbe (A) dominant über weiß (a). Zwei Pflanzen mit lila Blüten, die beide den Erbtyp Aa haben, werden gekreuzt. Wie viel Prozent der Nachkommen blühen im Mittel ${if (askWhite) "weiß" else "lila"}?", "In Mendel's peas purple flower colour (A) is dominant over white (a). Two purple-flowered plants, both with the genotype Aa, are crossed. On average, what percentage of the offspring have ${if (askWhite) "white" else "purple"} flowers?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), p, "%", abs = 0.0, done = "${n(p, 0)} %",
                    hints = listOf(l("Kernidee: Jedes Elternteil gibt zufällig eins seiner beiden Allele weiter. Zeichne ein Kreuzungsquadrat mit A und a auf beiden Seiten.", "Key idea: each parent passes on one of its two alleles at random. Draw a Punnett square with A and a on both sides."), l("Mögliche Nachkommen: AA, Aa, aA, aa. Weiß blüht nur, wer zwei a hat.", "Possible offspring: AA, Aa, aA, aa. Only those with two a's flower white.")))),
                solution = listOf(l("Von den vier gleich wahrscheinlichen Kombinationen ist nur aa weiß: 25 %. Die anderen drei blühen lila: 75 %.", "Of the four equally likely combinations only aa is white: 25%. The other three flower purple: 75%.")),
                background = l("Gregor Mendel kreuzte in einem Klostergarten in Brünn zwischen 1856 und 1863 fast 30.000 Erbsenpflanzen und fand das Verhältnis 3 : 1. Daraus schloss er, dass Merkmale durch Paare unteilbarer Erbfaktoren weitergegeben werden. Seine Arbeit wurde 34 Jahre lang ignoriert und erst um 1900 wiederentdeckt. Heute heißen die Faktoren Gene.", "Between 1856 and 1863 Gregor Mendel crossed almost 30,000 pea plants in a monastery garden in Brno and found the ratio 3 : 1. From this he concluded that traits are passed on by pairs of indivisible hereditary factors. His work was ignored for 34 years and only rediscovered around 1900. Today the factors are called genes."),
            )
        },
        mc("bio.blutgruppen", Topic.Biology, M,
            t("Die Mutter hat Blutgruppe A (Erbanlagen A und 0), der Vater Blutgruppe B (Erbanlagen B und 0). Welche Blutgruppen können ihre Kinder haben?", "The mother has blood group A (genes A and O), the father blood group B (genes B and O). Which blood groups can their children have?"),
            t("Mögliche Blutgruppen", "Possible blood groups"),
            t("Alle vier: A, B, AB und 0", "All four: A, B, AB and O"),
            listOf(t("Nur A oder B", "Only A or B"), t("Nur AB", "Only AB"), t("A, B und AB, aber nie 0", "A, B and AB, but never O")),
            t("Kernidee: Jedes Kind bekommt von jedem Elternteil eine der beiden Erbanlagen.", "Key idea: every child gets one of the two genes from each parent."),
            t("Von der Mutter A oder 0, vom Vater B oder 0. Welche Paare sind möglich? 0 wird von A und B überdeckt.", "From the mother A or O, from the father B or O. Which pairs are possible? O is masked by A and B."),
            t("Möglich sind AB, A0, B0 und 00, jeweils mit 25 %. Das ergibt die Blutgruppen AB, A, B und 0.", "Possible are AB, AO, BO and OO, each with 25%. That gives the blood groups AB, A, B and O."),
            t("A und B sind kodominant: Wer beide hat, bildet beide Merkmale aus und hat AB. Die Anlage 0 bildet gar kein Merkmal und ist rezessiv. Deshalb können zwei Eltern mit A und B ein Kind mit 0 bekommen, was viele überrascht. Bei Bluttransfusionen ist das entscheidend, denn falsches Blut verklumpt.", "A and B are codominant: anyone who has both expresses both and has AB. The O gene produces no feature at all and is recessive. That's why two parents with A and B can have a child with O, which surprises many people. This is crucial in blood transfusions, because the wrong blood clumps."),
        ),
        lab("bio.nahrungspyramide", Topic.Biology, M, 0.5f) {
            val plants = pick(10_000.0, 50_000.0, 1_000_000.0); val steps = pick(3, 2)
            val top = plants * Math.pow(0.1, steps.toDouble())
            task(
                question = l("Auf jeder Stufe einer Nahrungskette kommt nur etwa 10 % der Energie bei der nächsten an. Aus ${n(plants, 0)} kg Pflanzen werden Pflanzenfresser, ${if (steps == 3) "daraus Räuber und daraus Spitzenräuber" else "und daraus Räuber"}. Wie viel kg ${if (steps == 3) "Spitzenräuber" else "Räuber"} können davon leben?", "At each step of a food chain only about 10% of the energy reaches the next one. ${n(plants, 0)} kg of plants feed herbivores, ${if (steps == 3) "which feed predators, which feed top predators" else "which feed predators"}. How many kg of ${if (steps == 3) "top predators" else "predators"} can live on that?"),
                steps = listOf(num(l("Masse in kg", "Mass in kg"), top, "kg", rel = 0.01, done = """§${m(top, 0)}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Jede Stufe teilt durch 10. Zähle die Stufen ab den Pflanzen.", "Key idea: each step divides by 10. Count the steps from the plants."), """§${m(plants, 0)} \cdot 0{,}1^{${steps}}§"""))),
                solution = listOf("""§${m(plants, 0)}\ \text{kg} \cdot 0{,}1^{${steps}} = ${m(top, 0)}\ \text{kg}§"""),
                background = l("Die restlichen 90 % werden für Bewegung, Körperwärme und Atmung verbraucht oder bleiben unverdaut. Deshalb sind große Raubtiere selten und Nahrungsketten selten länger als vier oder fünf Stufen. Es erklärt auch, warum auf derselben Ackerfläche viel mehr Menschen satt werden, wenn sie Getreide essen, statt es an Tiere zu verfüttern.", "The other 90% is used for movement, body heat and breathing, or stays undigested. That's why large predators are rare and food chains are seldom longer than four or five steps. It also explains why the same farmland feeds many more people if they eat the grain instead of feeding it to animals."),
            )
        },
        lab("bio.herzminute", Topic.Biology, M, 0.45f) {
            val sv = pick(70, 80); val hr = pick(70, 60, 75)
            val lmin = sv * hr / 1000.0
            task(
                question = l("Dein Herz pumpt bei jedem Schlag etwa ${sv} ml Blut in den Körper und schlägt in Ruhe ${hr}-mal pro Minute. Wie viel Liter Blut pumpt es pro Minute? Und wie lange braucht dein gesamtes Blut (5 Liter) für eine Runde?", "With each beat your heart pumps about ${sv} ml of blood into the body, and at rest it beats ${hr} times a minute. How many litres of blood does it pump per minute? And how long does all your blood (5 litres) take for one round?"),
                steps = listOf(
                    num(l("Liter pro Minute", "Litres per minute"), lmin, "l", rel = 0.02, done = """§${m(lmin, 1)}\ \text{l/min}§""",
                        hints = listOf(l("Kernidee: Menge pro Schlag mal Schläge pro Minute.", "Key idea: amount per beat times beats per minute."), """§${sv}\ \text{ml} \cdot ${hr}§""")),
                    num(l("Zeit für eine Runde in Minuten", "Time for one round in minutes"), 5.0 / lmin, "min", rel = 0.03, done = """§\approx ${m(5.0 / lmin, 1)}\ \text{min}§""",
                        hints = listOf(l("Kernidee: Gesamtmenge geteilt durch Menge pro Minute.", "Key idea: total amount divided by the amount per minute."), """§\dfrac{5\ \text{l}}{${m(lmin, 1)}\ \text{l/min}}§""")),
                ),
                solution = listOf("""§${sv} \cdot ${hr} = ${sv * hr}\ \text{ml} = ${m(lmin, 1)}\ \text{l/min}§""", """§\dfrac{5}{${m(lmin, 1)}} \approx ${m(5.0 / lmin, 1)}\ \text{min}§"""),
                background = l("Ungefähr einmal pro Minute fließt dein ganzes Blut durch den Körper, in Ruhe. Beim Sport kann ein trainiertes Herz 25 Liter pro Minute schaffen, weil es kräftiger und schneller schlägt. Auf dem Weg legt das Blut bis in die feinsten Haargefäße zurück: Alle Blutgefäße eines Menschen zusammen wären rund 100.000 km lang.", "Roughly once a minute all your blood flows round the body, at rest. During exercise a trained heart can manage 25 litres a minute, because it beats harder and faster. On the way the blood reaches the finest capillaries: all of a person's blood vessels together would be around 100,000 km long."),
            )
        },
        mc("bio.giraffenhals", Topic.Biology, M,
            t("Wie erklärt die Evolutionstheorie nach Darwin, dass Giraffen so lange Hälse haben?", "How does Darwin's theory of evolution explain why giraffes have such long necks?"),
            t("Erklärung", "Explanation"),
            t("Giraffen mit zufällig etwas längerem Hals kamen an mehr Futter oder setzten sich besser durch und hatten mehr Nachkommen.", "Giraffes that happened to have slightly longer necks got more food or competed better and had more offspring."),
            listOf(t("Giraffen streckten sich nach hohen Blättern, und ihre gestreckten Hälse wurden vererbt.", "Giraffes stretched for high leaves, and their stretched necks were inherited."), t("Die Hälse wuchsen, weil die Giraffen es so wollten.", "The necks grew because the giraffes wanted them to."), t("Alle Giraffen bekamen auf einmal durch eine einzige Mutation lange Hälse.", "All giraffes suddenly got long necks through a single mutation.")),
            t("Kernidee: Kann etwas, das ein Tier in seinem Leben trainiert, in seine Erbanlagen gelangen?", "Key idea: can something an animal trains during its life get into its genes?"),
            t("Denk an einen Bodybuilder: Bekommen seine Kinder automatisch Muskeln?", "Think of a bodybuilder: do his children automatically get muscles?"),
            t("In jeder Generation unterscheiden sich die Tiere zufällig ein wenig. Wer mit seinen Merkmalen besser zurechtkommt, hinterlässt mehr Nachkommen, und über viele Generationen verschiebt sich so die ganze Art.", "In every generation the animals differ a little by chance. Those whose features serve them better leave more offspring, and over many generations the whole species shifts this way."),
            t("Die Idee, dass im Leben erworbene Eigenschaften vererbt werden, stammt von Jean-Baptiste de Lamarck und ist widerlegt. Darwin erkannte, dass zufällige Vielfalt und Auslese genügen. Welcher Vorteil den Giraffenhals wirklich hervorbrachte, wird übrigens noch erforscht: Neben dem Futter in der Höhe spielen auch Kämpfe zwischen Bullen eine Rolle, die mit den Hälsen schlagen.", "The idea that characteristics acquired during life are inherited comes from Jean-Baptiste de Lamarck and has been disproved. Darwin realised that random variety and selection are enough. Which advantage actually produced the giraffe's neck is still being researched, by the way: besides food high up, fights between bulls, who swing their necks at each other, also play a part."),
        ),
        mc("bio.resistenz", Topic.Biology, M,
            t("Warum werden Bakterien mit der Zeit unempfindlich gegen ein Antibiotikum, das zu oft oder zu kurz genommen wird?", "Why do bacteria gradually become resistant to an antibiotic that is taken too often or for too short a time?"),
            t("Grund", "Reason"),
            t("Die zufällig widerstandsfähigsten Bakterien überleben und vermehren sich, ihre Nachkommen erben die Resistenz.", "The bacteria that happen to be most resistant survive and multiply, and their offspring inherit the resistance."),
            listOf(t("Die Bakterien gewöhnen sich an das Mittel wie ein Mensch an Kaffee.", "The bacteria get used to the drug, like a person gets used to coffee."), t("Das Antibiotikum wird im Körper schwächer.", "The antibiotic gets weaker in the body."), t("Der Mensch wird resistent, nicht die Bakterien.", "The person becomes resistant, not the bacteria.")),
            t("Kernidee: Das ist Evolution im Zeitraffer. Was passiert mit einer Population, wenn nur bestimmte Mitglieder überleben?", "Key idea: this is evolution in fast motion. What happens to a population if only certain members survive?"),
            t("Bakterien teilen sich alle 20 Minuten bis einige Stunden. In einer Woche gibt es Hunderte Generationen.", "Bacteria divide every 20 minutes to a few hours. In a week there are hundreds of generations."),
            t("Unter Milliarden Bakterien haben einige durch Mutation zufällig einen Schutz. Das Antibiotikum tötet die anderen und schafft den Resistenten freie Bahn. Bricht man die Einnahme zu früh ab, überleben gerade die zähesten.", "Among billions of bacteria some happen to have protection through mutation. The antibiotic kills the others and clears the way for the resistant ones. If you stop taking it too early, it's precisely the toughest that survive."),
            t("Resistente Keime sind eines der größten Gesundheitsprobleme der Welt. Bakterien können Resistenzgene sogar untereinander austauschen, auch zwischen verschiedenen Arten. Deshalb sollen Antibiotika nur bei echten bakteriellen Infektionen und genau nach ärztlicher Vorgabe eingesetzt werden, und in der Tiermast möglichst gar nicht vorbeugend.", "Resistant germs are one of the world's biggest health problems. Bacteria can even swap resistance genes among themselves, including between different species. That's why antibiotics should only be used for genuine bacterial infections and exactly as a doctor prescribes, and, if possible, not preventively at all in livestock farming."),
        ),
        lab("bio.chargaff", Topic.Biology, M, 0.5f) {
            val a = pick(30, 20, 35, 22); val g = 50 - a
            task(
                question = l("In der DNA eines Lebewesens sind ${a} % aller Basen Adenin (A). Wie viel Prozent sind Guanin (G)?", "In an organism's DNA ${a}% of all the bases are adenine (A). What percentage are guanine (G)?"),
                steps = listOf(num(l("Guanin in Prozent", "Guanine in percent"), g.toDouble(), "%", abs = 0.0, done = "$g %",
                    hints = listOf(l("Kernidee: In der Doppelhelix liegt jedem A ein T gegenüber und jedem G ein C.", "Key idea: in the double helix every A is opposite a T and every G opposite a C."), l("A und T sind also gleich häufig, G und C auch. Zusammen ergeben alle 100 %.", "So A and T are equally common, and so are G and C. Together they make 100%.")))),
                solution = listOf(l("T = A = ${a} %. Für G und C bleiben 100 − ${2 * a} = ${100 - 2 * a} %, also je ${g} %.", "T = A = ${a}%. That leaves 100 − ${2 * a} = ${100 - 2 * a}% for G and C, so ${g}% each.")),
                background = l("Erwin Chargaff fand um 1950 diese Regel, ohne zu wissen warum. Für James Watson und Francis Crick war sie 1953 ein entscheidender Hinweis: Wenn A immer mit T und G immer mit C gepaart ist, kann jeder Strang als Vorlage für den anderen dienen. So kopiert sich die DNA bei jeder Zellteilung. Das Röntgenbild von Rosalind Franklin lieferte die Form dazu.", "Erwin Chargaff found this rule around 1950 without knowing why. For James Watson and Francis Crick in 1953 it was a crucial clue: if A always pairs with T and G with C, each strand can serve as a template for the other. That's how DNA copies itself at every cell division. Rosalind Franklin's X-ray image supplied the shape."),
            )
        },
        lab("bio.codon", Topic.Biology, M, 0.5f) {
            task(
                question = l("Die DNA hat vier Buchstaben (A, T, G, C). Sie muss damit 20 verschiedene Aminosäuren verschlüsseln. Wie viele verschiedene Wörter gibt es, wenn jedes Wort aus drei Buchstaben besteht?", "DNA has four letters (A, T, G, C). With them it has to encode 20 different amino acids. How many different words are there if each word consists of three letters?"),
                steps = listOf(num(l("Anzahl Wörter", "Number of words"), 64.0, "", abs = 0.0, done = "64",
                    hints = listOf(l("Kernidee: Für jede der drei Stellen gibt es 4 Möglichkeiten.", "Key idea: there are 4 options for each of the three positions."), """§4 \cdot 4 \cdot 4§"""))),
                solution = listOf("""§4^3 = 64§""", l("Mit zwei Buchstaben gäbe es nur 4² = 16 Wörter, zu wenig für 20 Aminosäuren.", "With two letters there would only be 4² = 16 words, too few for 20 amino acids.")),
                background = l("Drei ist also die kleinste Wortlänge, die reicht, und genau die nutzt das Leben. Die 64 Dreierwörter, Codons genannt, sind mehr als nötig: Die meisten Aminosäuren haben mehrere Codons, und drei Codons bedeuten „Stopp“. Dieser genetische Code ist bei fast allen Lebewesen gleich, von Bakterien bis zum Menschen, ein starker Hinweis auf gemeinsame Abstammung.", "So three is the shortest word length that's enough, and that's exactly what life uses. The 64 three-letter words, called codons, are more than needed: most amino acids have several codons, and three codons mean 'stop'. This genetic code is the same in almost all living things, from bacteria to humans, a strong hint of common descent."),
            )
        },
        lab("bio.nervensignal", Topic.Biology, M, 0.5f) {
            val dist = pick(1.8, 1.6, 1.0); val fast = 100.0; val slow = 1.0
            task(
                question = l("Du stößt dir den Zeh. Die schnellen Nervenfasern für „Berührung“ leiten mit etwa 100 m/s, die dünnen Fasern für den dumpfen Schmerz nur mit etwa 1 m/s. Der Weg zum Gehirn ist ${n(dist, 1)} m lang. Wie viele Sekunden nach der Berührung kommt der dumpfe Schmerz an?", "You stub your toe. The fast nerve fibres for 'touch' conduct at about 100 m/s, the thin fibres for dull pain at only about 1 m/s. The path to the brain is ${n(dist, 1)} m long. How many seconds after the touch does the dull pain arrive?"),
                steps = listOf(num(l("Verzögerung in Sekunden", "Delay in seconds"), dist / slow - dist / fast, "s", rel = 0.02, done = """§\approx ${m(dist / slow - dist / fast, 2)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Rechne für beide Signale die Laufzeit aus und bilde die Differenz.", "Key idea: work out the travel time for both signals and take the difference."), """§\Delta t = \dfrac{${m(dist, 1)}}{1} - \dfrac{${m(dist, 1)}}{100}§"""))),
                solution = listOf("""§t_{schnell} = ${m(dist / fast, 3)}\ \text{s},\quad t_{langsam} = ${m(dist / slow, 1)}\ \text{s},\quad \Delta t \approx ${m(dist / slow - dist / fast, 2)}\ \text{s}§""".let { l(it, it.replace("schnell", "fast").replace("langsam", "slow")) }),
                background = l("Deshalb spürt man nach einem Stoß erst den Aufprall und gut eine Sekunde später den pochenden Schmerz. Schnelle Nervenfasern sind mit einer fettreichen Isolierschicht, dem Myelin, umwickelt, an der das Signal entlangspringt. Bei der Krankheit Multiple Sklerose wird diese Schicht geschädigt, und Signale werden langsamer oder fallen aus.", "That's why after a knock you first feel the impact and a good second later the throbbing pain. Fast nerve fibres are wrapped in a fatty insulating layer, myelin, along which the signal jumps. In the disease multiple sclerosis this layer is damaged and signals slow down or fail."),
            )
        },
        lab("bio.verwandtschaft", Topic.Biology, M, 0.55f) {
            val (rel, p) = pick(l("Geschwister", "siblings") to 50.0, l("Cousins und Cousinen", "first cousins") to 12.5, l("Großeltern und Enkel", "grandparents and grandchildren") to 25.0, l("Tante und Neffe", "an aunt and her nephew") to 25.0)
            task(
                question = l("Jedes Kind erbt von jedem Elternteil zufällig die Hälfte von dessen Erbgut. Welcher Anteil ihrer Gene stammt bei ${rel} im Mittel aus gemeinsamer Abstammung?", "Every child inherits a random half of each parent's genes. On average, what share of their genes do ${rel} have in common through shared descent?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), p, "%", abs = 0.0, done = "${n(p, 1)} %",
                    hints = listOf(l("Kernidee: Jeder Schritt im Stammbaum, von Eltern zu Kind oder zurück, halbiert den gemeinsamen Anteil.", "Key idea: every step in the family tree, from parent to child or back, halves the shared share."), l("Geschwister teilen 50 %. Bei Cousins geht es über die Eltern und die Großeltern: mehr Schritte, mehr Halbierungen.", "Siblings share 50%. For cousins the path runs via the parents and grandparents: more steps, more halvings.")))),
                solution = listOf(l("Bei ${rel}: ${n(p, 1)} %. Großeltern–Enkel sind zwei Schritte (½ · ½), Cousins teilen über ein Großelternpaar je ⅛.", "For ${rel}: ${n(p, 1)}%. Grandparent–grandchild is two steps (½ · ½); cousins share ⅛ through a pair of grandparents.")),
                background = l("Gemeint ist der Anteil, der aus gemeinsamen Vorfahren stammt. Insgesamt sind sich alle Menschen genetisch zu über 99,9 % gleich, und mit Schimpansen teilen wir rund 98,8 % der DNA. Der Verwandtschaftsgrad erklärt, warum Tiere wie Bienen oder Erdmännchen für Verwandte sogar ihr Leben riskieren: Sie helfen damit Kopien ihrer eigenen Gene.", "What's meant is the share that comes from common ancestors. Overall, all humans are over 99.9% genetically identical, and we share around 98.8% of our DNA with chimpanzees. The degree of relatedness explains why animals such as bees or meerkats even risk their lives for relatives: by doing so they help copies of their own genes."),
            )
        },
        mc("bio.salat", Topic.Biology, M,
            t("Wenn man Salat schon eine Stunde vor dem Essen mit Salz und Dressing anmacht, liegt er welk und matschig in der Schüssel. Warum?", "If you dress a salad with salt and dressing an hour before eating, it ends up limp and soggy in the bowl. Why?"),
            t("Grund", "Reason"),
            t("Das Salz zieht durch Osmose Wasser aus den Pflanzenzellen, die dadurch erschlaffen.", "The salt draws water out of the plant cells by osmosis, so they go limp."),
            listOf(t("Das Salz kocht die Blätter langsam.", "The salt slowly cooks the leaves."), t("Das Öl im Dressing löst die Zellwände auf.", "The oil in the dressing dissolves the cell walls."), t("Der Salat verliert Luft durch die Löcher, die das Salz macht.", "The salad loses air through holes made by the salt.")),
            t("Kernidee: Wasser wandert durch Zellmembranen zu der Seite, auf der mehr gelöste Stoffe sind.", "Key idea: water moves through cell membranes towards the side with more dissolved substances."),
            t("Ein knackiges Salatblatt ist prall mit Wasser gefüllt, wie ein aufgepumpter Reifen.", "A crisp lettuce leaf is filled tight with water, like an inflated tyre."),
            t("Außen auf dem Blatt entsteht eine sehr salzige Lösung. Wasser strömt aus den Zellen heraus, ihr Innendruck sinkt, und das Blatt verliert seine Spannung.", "A very salty solution forms on the outside of the leaf. Water flows out of the cells, their internal pressure falls, and the leaf loses its firmness."),
            t("Pflanzen stehen nur durch Wasserdruck in ihren Zellen aufrecht, deshalb lassen Blumen ohne Wasser die Köpfe hängen. Derselbe Effekt macht Salz und Zucker zu alten Konservierungsmitteln: Sie entziehen auch Bakterien das Wasser. Welker Salat erholt sich übrigens oft in kaltem, ungesalzenem Wasser, weil das Wasser dann zurück in die Zellen strömt.", "Plants are held upright only by water pressure in their cells, which is why flowers without water droop their heads. The same effect makes salt and sugar ancient preservatives: they draw water out of bacteria too. Limp lettuce often recovers in cold, unsalted water, because the water then flows back into the cells."),
        ),
        // ------------------------------------------------ schwer
        lab("bio.oberflaeche", Topic.Biology, H, 0.6f) {
            val k = pick(10, 2, 5)
            task(
                question = l("Eine würfelförmige Zelle wächst so, dass jede Kante ${k}-mal so lang wird. Um welchen Faktor sinkt dabei das Verhältnis von Oberfläche zu Volumen?", "A cube-shaped cell grows so that every edge becomes ${k} times as long. By what factor does the ratio of surface area to volume fall?"),
                steps = listOf(num(l("Faktor", "Factor"), k.toDouble(), "", abs = 0.0, done = "$k",
                    hints = listOf(l("Kernidee: Die Oberfläche wächst mit der Kantenlänge zum Quadrat, das Volumen hoch drei.", "Key idea: the surface area grows with the square of the edge length, the volume with the cube."), """§\dfrac{O}{V} \to \dfrac{${k}^2 O}{${k}^3 V}§"""))),
                solution = listOf("""§\dfrac{${k * k}}{${k * k * k}} = \dfrac{1}{${k}}§""", l("Jede Volumeneinheit hat nur noch 1/${k} so viel Hülle.", "Each unit of volume now has only 1/${k} as much membrane.")),
                background = l("Durch die Zellhülle muss alles hinein und hinaus: Nahrung, Sauerstoff, Abfall. Das Volumen bestimmt den Bedarf, die Oberfläche das Angebot. Wird eine Zelle zu groß, verhungert ihr Inneres. Deshalb teilen sich Zellen lieber, statt zu wachsen. Darm und Lunge falten sich aus demselben Grund zu riesigen Oberflächen, und Elefanten kühlen sich über ihre großen Ohren.", "Everything has to pass in and out through the cell membrane: food, oxygen, waste. The volume sets the demand, the surface the supply. If a cell gets too big, its interior starves. That's why cells prefer to divide rather than grow. The gut and lungs fold into huge surfaces for the same reason, and elephants cool themselves through their big ears."),
            )
        },
        lab("bio.kleiber", Topic.Biology, H, 0.65f) {
            val (ratio, txt) = pick(10_000.0 to l("10.000-mal", "10,000 times"), 16.0 to l("16-mal", "16 times"), 1e8 to l("100 Millionen Mal", "100 million times"))
            val f = ratio.pow(0.75)
            task(
                question = l("Nach dem Kleiber-Gesetz wächst der Energiebedarf eines Säugetiers nicht proportional zu seiner Masse, sondern mit Masse hoch 0,75. Ein Tier ist ${txt} so schwer wie ein anderes. Wie viel mehr Energie braucht es pro Tag?", "According to Kleiber's law, a mammal's energy needs don't grow in proportion to its mass but with mass to the power 0.75. One animal is ${txt} as heavy as another. How much more energy does it need per day?"),
                steps = listOf(num(l("Faktor", "Factor"), f, "", rel = 0.02, done = """§${m(f, 0)}§""",
                    hints = listOf(l("Kernidee: Rechne das Massenverhältnis hoch 0,75, also hoch drei Viertel. Bei Zehnerpotenzen geht das im Kopf.", "Key idea: raise the mass ratio to the power 0.75, i.e. three quarters. With powers of ten you can do it in your head."), """§${m(ratio, 0)}^{0{,}75}§"""))),
                solution = listOf("""§${m(ratio, 0)}^{0{,}75} = ${m(f, 0)}§""", l("Pro Kilogramm Körpermasse braucht das große Tier dagegen ${n(ratio / f, 0)}-mal weniger.", "Per kilogram of body mass, on the other hand, the big animal needs ${n(ratio / f, 0)} times less.")),
                background = l("Eine Spitzmaus muss fast ununterbrochen fressen und verhungert in wenigen Stunden, ein Elefant kommt mit vergleichsweise wenig Futter pro Kilo aus. Max Kleiber fand die Regel 1932, sie gilt vom Bakterium bis zum Wal erstaunlich gut. Eine Erklärung: Die Versorgungsnetze aus Adern und Gefäßen werden in großen Körpern im Verhältnis immer effizienter.", "A shrew has to eat almost non-stop and starves within a few hours, while an elephant gets by on comparatively little food per kilo. Max Kleiber found the rule in 1932, and it holds astonishingly well from bacteria to whales. One explanation: the supply networks of arteries and vessels become ever more efficient in proportion in large bodies."),
            )
        },
        lab("bio.proteingen", Topic.Biology, H, 0.6f) {
            val aa = pick(300, 150, 500, 1000); val bases = 3 * aa + 3
            task(
                question = l("Ein Protein besteht aus ${aa} Aminosäuren. Jede wird durch ein Codon aus drei Basen verschlüsselt, und am Ende steht ein Stopp-Codon. Wie viele Basen muss der Abschnitt der mRNA mindestens haben, der das Protein codiert?", "A protein consists of ${aa} amino acids. Each is encoded by a codon of three bases, and at the end there's a stop codon. What is the least number of bases the stretch of mRNA coding for the protein must have?"),
                steps = listOf(num(l("Basen", "Bases"), bases.toDouble(), "", abs = 0.0, done = "$bases",
                    hints = listOf(l("Kernidee: Drei Basen pro Aminosäure, plus das Stopp-Signal.", "Key idea: three bases per amino acid, plus the stop signal."), """§3 \cdot ${aa} + 3§"""))),
                solution = listOf("""§3 \cdot ${aa} + 3 = ${bases}§"""),
                background = l("Echte Gene sind meist viel länger: Bei Pflanzen und Tieren sind sie durch nichtcodierende Abschnitte, Introns, unterbrochen, die vor dem Ablesen herausgeschnitten werden. Nur rund 1 bis 2 % des menschlichen Erbguts codieren tatsächlich Proteine. Der Rest steuert unter anderem, wann und wo welche Gene abgelesen werden.", "Real genes are usually much longer: in plants and animals they're interrupted by non-coding stretches, introns, which are cut out before reading. Only around 1 to 2% of the human genome actually codes for proteins. The rest, among other things, controls when and where which genes are read."),
            )
        },
        lab("bio.fangwiederfang", Topic.Biology, H, 0.6f) {
            val marked = pick(50, 80, 120); val (caught, recap) = pick(40 to 8, 60 to 12, 100 to 25)
            val nn = marked * caught / recap
            task(
                question = l("Forscher fangen in einem Teich ${marked} Fische, markieren sie und setzen sie zurück. Eine Woche später fangen sie ${caught} Fische, von denen ${recap} markiert sind. Wie viele Fische leben ungefähr im Teich?", "Researchers catch ${marked} fish in a pond, mark them and release them. A week later they catch ${caught} fish, ${recap} of which are marked. Roughly how many fish live in the pond?"),
                steps = listOf(num(l("Geschätzte Anzahl", "Estimated number"), nn.toDouble(), "", rel = 0.01, done = "$nn",
                    hints = listOf(l("Kernidee: Der Anteil der markierten Fische im zweiten Fang sollte dem Anteil im ganzen Teich entsprechen.", "Key idea: the share of marked fish in the second catch should match the share in the whole pond."), """§\dfrac{${recap}}{${caught}} = \dfrac{${marked}}{N}§"""))),
                solution = listOf("""§N = \dfrac{${marked} \cdot ${caught}}{${recap}} = ${nn}§"""),
                background = l("Diese Fang-Wiederfang-Methode erlaubt es, Tiere zu zählen, die man nie alle auf einmal sehen kann, von Schmetterlingen bis zu Walen. Sie funktioniert nur, wenn sich die markierten Tiere gut durchmischen und nicht leichter oder schwerer zu fangen sind als die anderen. Mit einer verwandten Idee schätzte man im Zweiten Weltkrieg aus Seriennummern, wie viele Panzer der Gegner baute.", "This capture–recapture method makes it possible to count animals you can never see all at once, from butterflies to whales. It only works if the marked animals mix in well and aren't easier or harder to catch than the others. With related logic, serial numbers were used in the Second World War to estimate how many tanks the enemy was building."),
            )
        },
        lab("bio.herdenschutz", Topic.Biology, H, 0.65f) {
            val (dis, r0) = pick(l("Masern", "measles") to 15.0, l("Mumps", "mumps") to 5.0, l("einer Grippe", "flu") to 2.0, l("Röteln", "rubella") to 6.0)
            val share = (1 - 1 / r0) * 100
            task(
                question = l("Bei ${dis} steckt ein Infizierter in einer ungeschützten Bevölkerung im Mittel etwa ${n(r0, 0)} andere an (Basisreproduktionszahl R₀). Wie viel Prozent der Menschen müssen mindestens immun sein, damit sich die Krankheit nicht mehr ausbreiten kann?", "With ${dis}, one infected person in an unprotected population infects about ${n(r0, 0)} others on average (basic reproduction number R₀). What percentage of people, at least, must be immune for the disease to stop spreading?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), share, "%", abs = 0.5, done = """§\approx ${m(share, 0)}\ \%§""",
                    hints = listOf(l("Kernidee: Die Ausbreitung stoppt, wenn jeder Infizierte im Mittel höchstens einen weiteren ansteckt. Von seinen ${n(r0, 0)} Kontakten darf also nur einer empfänglich sein.", "Key idea: spreading stops when each infected person infects at most one more on average. So of their ${n(r0, 0)} contacts only one may be susceptible."), """§p = 1 - \dfrac{1}{R_0}§"""))),
                solution = listOf("""§p = 1 - \dfrac{1}{${m(r0, 0)}} \approx ${m(share / 100, 3)} = ${m(share, 0)}\ \%§"""),
                background = l("Je ansteckender eine Krankheit, desto höher die nötige Impfquote. Masern gehören zu den ansteckendsten Krankheiten überhaupt, deshalb müssen rund 95 % geschützt sein, und schon kleine Impflücken führen zu Ausbrüchen. Herdenschutz schützt auch die, die selbst nicht geimpft werden können, etwa Säuglinge oder Menschen mit geschwächtem Immunsystem.", "The more contagious a disease, the higher the vaccination rate needed. Measles is one of the most contagious diseases of all, so around 95% must be protected, and even small gaps in vaccination lead to outbreaks. Herd immunity also protects those who can't be vaccinated themselves, such as babies or people with weakened immune systems."),
            )
        },
        mc("bio.fettweg", Topic.Biology, H,
            t("Wenn ein Mensch Körperfett abbaut, verschwinden Kilogramm an Masse. Wohin gehen die Atome des Fetts zum größten Teil?", "When a person breaks down body fat, kilograms of mass disappear. Where do most of the atoms of the fat go?"),
            t("Verbleib", "Destination"),
            t("Sie werden als Kohlenstoffdioxid ausgeatmet.", "They are breathed out as carbon dioxide."),
            listOf(t("Sie werden in Wärme umgewandelt.", "They are converted into heat."), t("Sie werden in Muskeln umgebaut.", "They are converted into muscle."), t("Sie werden mit dem Stuhl ausgeschieden.", "They are excreted in the faeces.")),
            t("Kernidee: Masse kann nicht in Energie verschwinden, jedenfalls nicht messbar. Fett besteht aus Kohlenstoff, Wasserstoff und Sauerstoff. Was entsteht beim Verbrennen daraus?", "Key idea: mass can't vanish into energy, at least not measurably. Fat consists of carbon, hydrogen and oxygen. What does it turn into when it's burnt?"),
            t("Die Zellatmung macht aus Fett und Sauerstoff Kohlenstoffdioxid und Wasser.", "Cellular respiration turns fat and oxygen into carbon dioxide and water."),
            t("Rund 84 % der Fettmasse verlassen den Körper als CO₂ über die Lunge, der Rest als Wasser über Urin, Schweiß und Atem. Die Energie wird frei, die Atome bleiben erhalten.", "Around 84% of the fat's mass leaves the body as CO₂ via the lungs, the rest as water via urine, sweat and breath. The energy is released; the atoms are conserved."),
            t("Diese Rechnung veröffentlichten Ruben Meerman und Andrew Brown 2014, und viele Ärzte lagen bei der Frage falsch: Die meisten tippten auf Wärme. Die Lunge ist also das wichtigste Ausscheidungsorgan für Fett. Mehr atmen allein hilft trotzdem nicht, denn wie viel CO₂ entsteht, bestimmt der Energieverbrauch der Zellen.", "Ruben Meerman and Andrew Brown published this calculation in 2014, and many doctors got the question wrong: most guessed heat. So the lungs are the main excretory organ for fat. Breathing more on its own still doesn't help, though, because how much CO₂ is produced is determined by the energy use of the cells."),
        ),
    )
}
