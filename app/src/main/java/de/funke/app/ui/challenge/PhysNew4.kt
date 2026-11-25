package de.funke.app.ui.challenge

import kotlin.math.asin
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

/* Physik-Ausbau (seit 13.2): Wärme, Schall & Wellen, Licht & Optik. Alle Texte selbst geschrieben. */

private const val G4 = 9.81
private const val C4 = 2.998e8
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object PhysNew4 {
    val all: List<TaskDef> get() = heat + waves + optics

    private val heat = listOf(
        lab("phy.bruecke", Topic.Heat, E, 0.4f) {
            val len = pick(300.0, 500.0, 800.0); val dT = pick(40.0, 50.0, 60.0)
            val dl = 12e-6 * len * dT * 100
            task(
                question = l("Eine Stahlbrücke ist ${n(len, 0)} m lang. Zwischen kältester Winternacht und heißestem Sommertag schwankt ihre Temperatur um ${n(dT, 0)} K. Stahl dehnt sich um 12 Millionstel pro Kelvin aus. Um wie viele Zentimeter ändert sich ihre Länge?", "A steel bridge is ${n(len, 0)} m long. Between the coldest winter night and the hottest summer day its temperature varies by ${n(dT, 0)} K. Steel expands by 12 millionths per kelvin. By how many centimetres does its length change?"),
                steps = listOf(num(l("Längenänderung in cm", "Change in length in cm"), dl, "cm", rel = 0.02, done = """§\Delta L = ${m(dl, 0)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Jeder Meter wird pro Kelvin um denselben winzigen Bruchteil länger.", "Key idea: every metre gets longer by the same tiny fraction per kelvin."), """§\Delta L = \alpha \cdot L \cdot \Delta T§"""))),
                solution = listOf("""§\Delta L = 12 \cdot 10^{-6} \cdot ${m(len, 0)}\ \text{m} \cdot ${m(dT, 0)} = ${m(dl / 100, 2)}\ \text{m} = ${m(dl, 0)}\ \text{cm}§"""),
                background = l("Wärmere Atome schwingen heftiger und brauchen etwas mehr Platz. Bei einem Meter merkt man das kaum, bei hunderten Metern sind es Dezimeter. Brücken haben deshalb an den Enden Dehnungsfugen, die wie verzahnte Kämme aussehen, und liegen auf Rollen, damit sie wandern können, ohne zu reißen.", "Warmer atoms vibrate more strongly and need a little more room. Over one metre you hardly notice, over hundreds of metres it's tens of centimetres. That's why bridges have expansion joints at their ends, which look like interlocking combs, and rest on rollers so they can move without cracking."),
                fact = l("Der Eiffelturm ist im Sommer bis zu 15 cm höher als im Winter.", "The Eiffel Tower is up to 15 cm taller in summer than in winter."),
            )
        },
        lab("phy.kuehlschrank", Topic.Heat, E, 0.5f) {
            task(
                question = l("Es ist ein heißer Tag. Du lässt die Kühlschranktür in der geschlossenen Küche offen stehen, damit es kühler wird. Was passiert mit der Temperatur in der Küche?", "It's a hot day. You leave the fridge door open in the closed kitchen to cool it down. What happens to the temperature in the kitchen?"),
                steps = listOf(pick1(l("Die Küche wird …", "The kitchen gets …"), l("langsam wärmer", "slowly warmer"), listOf(l("kühler, wie bei einer Klimaanlage", "cooler, like with an air conditioner"), l("erst kühler, dann gleich warm wie vorher", "cooler at first, then as warm as before"), l("genau gleich warm", "exactly as warm as before")),
                    hints = listOf(l("Kernidee: Ein Kühlschrank vernichtet keine Wärme, er pumpt sie nur von innen nach außen. Wo ist außen?", "Key idea: a fridge doesn't destroy heat, it just pumps it from inside to outside. Where is outside?"), l("Der Motor braucht dafür Strom, und der wird am Ende auch zu Wärme.", "The motor needs electricity for that, and in the end that also turns into heat.")))),
                solution = listOf(l("Der Kühlschrank nimmt der Küche vorn Wärme weg und gibt sie hinten über das Gitter wieder an die Küche ab, plus die Energie des Motors. Unterm Strich wird die Küche wärmer.", "The fridge takes heat out of the kitchen at the front and releases it back into the kitchen through the grille at the back, plus the motor's energy. Overall the kitchen gets warmer.")),
                background = l("Wärme fließt von selbst nur von warm nach kalt. Ein Kühlschrank ist eine Wärmepumpe, die sie gegen diese Richtung befördert, und das kostet Arbeit. Alles zusammen, die gepumpte Wärme plus die Arbeit, landet auf der warmen Seite. Eine Klimaanlage funktioniert nur, weil ihre warme Seite draußen vor dem Fenster hängt.", "Heat flows by itself only from warm to cold. A fridge is a heat pump that moves it against this direction, and that takes work. Everything together, the pumped heat plus the work, ends up on the warm side. An air conditioner only works because its warm side hangs outside the window."),
                fact = l("Fühl mal hinten am Kühlschrank: Das schwarze Gitter dort ist warm. Dort gibt er die Wärme ab, die er innen herausholt.", "Feel the back of your fridge: the black grille there is warm. That's where it gives off the heat it pulls out of the inside."),
            )
        },
        lab("phy.schnellkochtopf", Topic.Heat, M, 0.4f) {
            task(
                question = l("Warum werden Kartoffeln im Schnellkochtopf so viel schneller gar?", "Why do potatoes cook so much faster in a pressure cooker?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Der höhere Druck im Topf lässt das Wasser erst bei etwa 120 °C kochen, das Wasser wird also heißer.", "The higher pressure in the pot means the water only boils at about 120 °C, so the water gets hotter."),
                    listOf(l("Der Dampf drückt die Wärme schneller in die Kartoffeln, die Temperatur bleibt bei 100 °C.", "The steam pushes heat into the potatoes faster; the temperature stays at 100 °C."), l("Der Deckel verhindert Wärmeverluste, sonst ändert sich nichts.", "The lid prevents heat losses; nothing else changes."), l("Unter Druck gart Gemüse chemisch schneller, auch bei gleicher Temperatur.", "Under pressure vegetables cook chemically faster, even at the same temperature.")),
                    hints = listOf(l("Kernidee: Kochendes Wasser wird nicht heißer als sein Siedepunkt. Hängt der Siedepunkt vom Druck ab?", "Key idea: boiling water doesn't get hotter than its boiling point. Does the boiling point depend on pressure?"), l("Wasser kocht, wenn sein Dampf so stark drückt wie die Umgebung.", "Water boils when its vapour pushes as hard as its surroundings.")))),
                solution = listOf(l("Bei etwa 2 bar Druck siedet Wasser erst bei rund 120 °C. Garen ist eine chemische Veränderung, und die läuft bei 20 Grad mehr etwa dreimal so schnell.", "At about 2 bar pressure water only boils at around 120 °C. Cooking is a chemical change, and at 20 degrees more it runs about three times as fast.")),
                background = l("Ein Topf mit kochendem Wasser bleibt bei 100 °C, egal wie stark die Herdplatte heizt: Die zusätzliche Energie geht ins Verdampfen. Der Siedepunkt hängt aber vom Druck ab. Im verschlossenen Topf staut sich der Dampf, der Druck steigt, und das Wasser kann heißer werden, bevor es siedet.", "A pot of boiling water stays at 100 °C however hard the hob heats: the extra energy goes into evaporation. But the boiling point depends on the pressure. In a sealed pot the steam builds up, the pressure rises, and the water can get hotter before it boils."),
                fact = l("Im Hochgebirge ist es umgekehrt: Auf 3000 m siedet Wasser schon bei etwa 90 °C, und Nudeln brauchen deutlich länger.", "In high mountains it's the other way round: at 3000 m water boils at about 90 °C, and pasta takes much longer."),
            )
        },
        lab("phy.schwitzen", Topic.Heat, M, 0.5f) {
            val p = pick(400.0, 600.0, 800.0); val lv = 2.4e6
            val liters = p * 3600 / lv
            task(
                question = l("Beim Joggen an einem heißen Tag muss dein Körper ${n(p, 0)} W Wärme loswerden, und zwar nur durch Schwitzen. Das Verdunsten von 1 kg Schweiß nimmt etwa 2400 kJ mit. Wie viel Liter Schweiß verdunstest du pro Stunde?", "Jogging on a hot day, your body has to get rid of ${n(p, 0)} W of heat, only through sweating. Evaporating 1 kg of sweat takes away about 2400 kJ. How many litres of sweat do you evaporate per hour?"),
                steps = listOf(num(l("Schweiß in Litern pro Stunde", "Sweat in litres per hour"), liters, "l", rel = 0.02, done = """§m \approx ${m(liters, 2)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Erst die Wärme in einer Stunde ausrechnen (Leistung mal 3600 s), dann durch die Verdampfungswärme teilen.", "Key idea: first work out the heat in one hour (power times 3600 s), then divide by the heat of vaporisation."), """§m = \dfrac{P \cdot t}{L_V}§"""))),
                solution = listOf("""§Q = ${m(p, 0)}\ \text{W} \cdot 3600\ \text{s} = ${sci(p * 3600)}\ \text{J}§""", """§m = \dfrac{${sci(p * 3600)}}{2{,}4 \cdot 10^6}\ \text{kg} \approx ${m(liters, 2)}\ \text{kg}§"""),
                background = l("Verdunsten kostet viel Energie: Die Moleküle, die aus dem Schweißfilm entkommen, sind die schnellsten und nehmen ihre Energie mit. Die Haut kühlt dabei ab. Das klappt nur, wenn die Luft noch Wasser aufnehmen kann. Bei schwüler Hitze tropft der Schweiß ab, statt zu verdunsten, und kühlt kaum.", "Evaporation takes a lot of energy: the molecules that escape from the film of sweat are the fastest ones and take their energy with them. The skin cools down as a result. This only works if the air can still take up water. In muggy heat the sweat drips off instead of evaporating and hardly cools at all."),
                fact = l("Menschen gehören zu den besten Schwitzern im Tierreich. Das machte unsere Vorfahren zu ausdauernden Läufern, die Beute in der Mittagshitze müde jagen konnten.", "Humans are among the best sweaters in the animal kingdom. That made our ancestors into endurance runners who could chase prey to exhaustion in the midday heat."),
            )
        },
        lab("phy.thermoskanne", Topic.Heat, M, 0.5f) {
            task(
                question = l("Eine Thermoskanne hält Tee heiß und Eistee kalt. Sie hat doppelte Glaswände mit Vakuum dazwischen, und die Wände sind innen verspiegelt. Wofür ist die Verspiegelung da?", "A thermos flask keeps tea hot and iced tea cold. It has double glass walls with a vacuum between them, and the walls are mirrored on the inside. What is the mirror coating for?"),
                steps = listOf(pick1(l("Die Verspiegelung verhindert …", "The mirror coating prevents …"), l("Wärmestrahlung", "heat radiation"), listOf(l("Wärmeleitung", "heat conduction"), l("Wärmeströmung (Konvektion)", "heat convection"), l("Verdunstung", "evaporation")),
                    hints = listOf(l("Kernidee: Wärme wandert auf drei Wegen: Leitung, Strömung und Strahlung. Zwei davon brauchen Materie.", "Key idea: heat travels in three ways: conduction, convection and radiation. Two of them need matter."), l("Was kommt auch durch ein Vakuum, etwa von der Sonne zur Erde?", "What can get through a vacuum, for example from the Sun to the Earth?")))),
                solution = listOf(l("Das Vakuum stoppt Leitung und Strömung. Nur Strahlung kommt durch ein Vakuum, und die wirft der Spiegel zurück.", "The vacuum stops conduction and convection. Only radiation can cross a vacuum, and the mirror reflects it back.")),
                background = l("Leitung braucht sich berührende Teilchen, Strömung braucht bewegliches Gas oder Wasser. Im Vakuum gibt es beides nicht. Strahlung dagegen ist Licht, meist unsichtbares Infrarot, und reist auch durchs leere All. Ein Spiegel wirft es zurück. James Dewar erfand die Kanne 1892, um flüssige Gase im Labor aufzubewahren.", "Conduction needs particles that touch, convection needs a gas or liquid that can move. In a vacuum there is neither. Radiation, however, is light, mostly invisible infrared, and even travels through empty space. A mirror reflects it back. James Dewar invented the flask in 1892 to store liquefied gases in the lab."),
                fact = l("Rettungsdecken aus Goldfolie funktionieren genauso: Sie werfen die Wärmestrahlung des Körpers zurück.", "Gold-foil emergency blankets work the same way: they reflect the body's heat radiation back."),
            )
        },
        lab("phy.waermepumpe", Topic.Heat, H, 0.6f) {
            val tOut = pick(0.0, -5.0, 5.0); val tIn = 35.0; val heat = 10000.0; val cop = 3.5
            val carnot = (tIn + 273.15) / (tIn - tOut); val el = heat / cop
            task(
                question = l("Eine Wärmepumpe holt Wärme aus der Außenluft (${n(tOut, 0)} °C) und heizt mit 35 °C warmem Wasser die Fußbodenheizung.", "A heat pump takes heat from the outside air (${n(tOut, 0)} °C) and heats the underfloor heating with water at 35 °C."),
                steps = listOf(
                    num(l("Wie viel Wärme könnte eine ideale Wärmepumpe pro Kilowattstunde Strom liefern (Leistungszahl)?", "How much heat could an ideal heat pump deliver per kilowatt-hour of electricity (coefficient of performance)?"), carnot, "", rel = 0.02, done = """§\varepsilon \approx ${m(carnot, 1)}§""",
                        hints = listOf(l("Kernidee: Für den idealen Fall zählen nur die beiden Temperaturen, in Kelvin.", "Key idea: in the ideal case only the two temperatures count, in kelvin."), """§\varepsilon = \dfrac{T_{warm}}{T_{warm} - T_{kalt}}§""".let { l(it, it.replace("warm", "hot").replace("kalt", "cold")) })),
                    num(l("Eine echte Pumpe schafft 3,5. Wie viel Strom braucht sie für 10.000 kWh Heizwärme im Jahr?", "A real pump manages 3.5. How much electricity does it need for 10,000 kWh of heat a year?"), el, "kWh", rel = 0.02, done = """§W \approx ${m(sig(el))}\ \text{kWh}§""",
                        hints = listOf(l("Jede Kilowattstunde Strom bringt 3,5 kWh Wärme ins Haus.", "Every kilowatt-hour of electricity brings 3.5 kWh of heat into the house."), """§W = \dfrac{Q}{\varepsilon}§""")),
                ),
                solution = listOf("""§\varepsilon_{ideal} = \dfrac{308{,}15}{35 - (${m(tOut, 0)})} \approx ${m(carnot, 1)}§""", """§W = \dfrac{10\,000}{3{,}5}\ \text{kWh} \approx ${m(sig(el))}\ \text{kWh}§"""),
                background = l("Eine Wärmepumpe erzeugt Wärme nicht, sie schaufelt sie von kalt nach warm, wie ein umgekehrter Kühlschrank. Deshalb kann sie mehr Wärme liefern, als sie Strom verbraucht. Je kleiner der Temperaturunterschied, desto leichter fällt das. Fußbodenheizungen mit niedriger Vorlauftemperatur passen daher besonders gut zu Wärmepumpen.", "A heat pump doesn't create heat, it shovels it from cold to warm, like a fridge in reverse. That's why it can deliver more heat than it uses in electricity. The smaller the temperature difference, the easier it is. Underfloor heating with a low flow temperature is therefore a particularly good match for heat pumps."),
                fact = l("Selbst bei −10 °C steckt in der Luft noch reichlich Wärme. Gemessen vom absoluten Nullpunkt ist sie immer noch 263 Grad warm.", "Even at −10 °C there's still plenty of heat in the air. Measured from absolute zero it is still 263 degrees warm."),
            )
        },
        lab("phy.fenster", Topic.Heat, M, 0.5f) {
            val a = pick(1.5, 2.0, 3.0); val dT = pick(20.0, 25.0)
            val p1 = 5.8 * a * dT; val p3 = 0.6 * a * dT
            task(
                question = l("Ein Fenster hat ${n(a)} m². Drinnen ist es ${n(dT, 0)} K wärmer als draußen. Der Wärmedurchgang (U-Wert) beträgt bei Einfachglas 5,8 W/(m²·K), bei moderner Dreifachverglasung 0,6 W/(m²·K).", "A window has an area of ${n(a)} m². Inside it is ${n(dT, 0)} K warmer than outside. The heat transfer coefficient (U-value) is 5.8 W/(m²·K) for single glazing and 0.6 W/(m²·K) for modern triple glazing."),
                steps = listOf(
                    num(l("Wie viel Wärmeleistung geht durch das Einfachglas verloren?", "How much heating power is lost through the single glazing?"), p1, "W", rel = 0.02, done = """§P \approx ${m(sig(p1))}\ \text{W}§""",
                        hints = listOf(l("Kernidee: Der U-Wert sagt, wie viele Watt pro Quadratmeter und Grad Unterschied durchgehen.", "Key idea: the U-value tells you how many watts pass through per square metre and degree of difference."), """§P = U \cdot A \cdot \Delta T§""")),
                    num(l("Und durch die Dreifachverglasung?", "And through the triple glazing?"), p3, "W", rel = 0.02, done = """§P \approx ${m(sig(p3))}\ \text{W}§""",
                        hints = listOf(l("Dieselbe Formel mit dem kleineren U-Wert.", "The same formula with the smaller U-value."), """§P = 0{,}6 \cdot ${m(a)} \cdot ${m(dT, 0)}§""")),
                ),
                solution = listOf("""§P_1 = 5{,}8 \cdot ${m(a)} \cdot ${m(dT, 0)}\ \text{W} \approx ${m(sig(p1))}\ \text{W}§""", """§P_3 = 0{,}6 \cdot ${m(a)} \cdot ${m(dT, 0)}\ \text{W} \approx ${m(sig(p3))}\ \text{W}§"""),
                background = l("Glas selbst leitet Wärme recht gut. Moderne Fenster dämmen vor allem durch die dünnen Schichten Edelgas zwischen den Scheiben und durch hauchdünne Metallschichten, die Wärmestrahlung zurückwerfen. Der Wärmestrom ist proportional zur Fläche und zum Temperaturunterschied, deshalb geht im Winter so viel mehr verloren.", "Glass itself conducts heat quite well. Modern windows insulate mainly through the thin layers of noble gas between the panes and through wafer-thin metal coatings that reflect heat radiation. The heat flow is proportional to the area and the temperature difference, which is why so much more is lost in winter."),
                fact = l("Ein einziges altes Fenster verliert damit so viel Wärme wie eine kleine Heizung leistet, rund um die Uhr.", "A single old window thus loses as much heat as a small heater produces, around the clock."),
            )
        },
        lab("phy.molekuelgeschw", Topic.Heat, H, 0.6f) {
            val tc = pick(20.0, 0.0, 30.0); val t = tc + 273.15; val m28 = 28 * 1.6605e-27
            val v = sqrt(3 * 1.381e-23 * t / m28)
            task(
                question = l("Die Luft besteht vor allem aus Stickstoffmolekülen (N₂, 28 u, 1 u = 1,66 · 10⁻²⁷ kg). Wie schnell fliegen sie bei ${n(tc, 0)} °C im Mittel (quadratisches Mittel)?", "Air consists mainly of nitrogen molecules (N₂, 28 u, 1 u = 1.66 · 10⁻²⁷ kg). How fast do they fly on average at ${n(tc, 0)} °C (root mean square)?"),
                given = listOf("""§k_B = 1{,}38 \cdot 10^{-23}\ \text{J/K}§"""),
                steps = listOf(num(l("Geschwindigkeit in m/s", "Speed in m/s"), v, "m/s", rel = 0.02, done = """§v \approx ${m(v, 0)}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Temperatur ist mittlere Bewegungsenergie der Teilchen. Pro Molekül sind das 3/2·k·T.", "Key idea: temperature is the average kinetic energy of the particles. Per molecule that's 3/2·k·T."), """§\tfrac{1}{2} m v^2 = \tfrac{3}{2} k_B T \Rightarrow v = \sqrt{\dfrac{3 k_B T}{m}}§"""))),
                solution = listOf("""§v = \sqrt{\dfrac{3 \cdot 1{,}38 \cdot 10^{-23} \cdot ${m(t, 2)}}{28 \cdot 1{,}66 \cdot 10^{-27}}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 0)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = l("Die Moleküle der Luft um dich herum fliegen schneller als ein Verkehrsflugzeug, aber nach wenigen Zehntausendstel Millimetern stoßen sie schon an das nächste. Deshalb braucht ein Geruch lange, um sich im Zimmer zu verteilen. Leichte Moleküle sind bei gleicher Temperatur schneller, deshalb entweicht Wasserstoff aus der Erdatmosphäre.", "The air molecules around you fly faster than an airliner, but after a few ten-thousandths of a millimetre they already hit the next one. That's why a smell takes a long time to spread through a room. Light molecules are faster at the same temperature, which is why hydrogen escapes from the Earth's atmosphere."),
                fact = l("Die Schallgeschwindigkeit liegt nicht zufällig in derselben Größenordnung: Schall wird von genau diesen Molekülen weitergestoßen.", "It's no coincidence that the speed of sound is of the same order: sound is passed along by exactly these molecules."),
            )
        },
        lab("phy.menschstrahlt", Topic.Heat, H, 0.6f) {
            val skin = 33.0 + 273.15; val wall = pick(20.0, 15.0, 22.0) + 273.15; val a = 1.8
            val p = 5.67e-8 * a * (skin.pow(4) - wall.pow(4))
            task(
                question = l("Deine nackte Haut (1,8 m², 33 °C) strahlt Wärme ab und empfängt Strahlung von den Wänden (${n(wall - 273.15, 0)} °C). Wie groß ist die Strahlungsleistung, die du netto verlierst (Haut als schwarzer Strahler)?", "Your bare skin (1.8 m², 33 °C) radiates heat and receives radiation from the walls (${n(wall - 273.15, 0)} °C). What is the net radiant power you lose (treat skin as a black body)?"),
                given = listOf("""§\sigma = 5{,}67 \cdot 10^{-8}\ \tfrac{\text{W}}{\text{m}^2 \text{K}^4}§"""),
                steps = listOf(num(l("Netto-Strahlungsleistung in Watt", "Net radiant power in watts"), p, "W", rel = 0.03, done = """§P \approx ${m(sig(p))}\ \text{W}§""",
                    hints = listOf(l("Kernidee: Du strahlst mit T⁴ deiner Haut ab und bekommst T⁴ der Wände zurück, beides in Kelvin.", "Key idea: you radiate with T⁴ of your skin and receive T⁴ of the walls back, both in kelvin."), """§P = \sigma A (T_H^4 - T_W^4)§"""))),
                solution = listOf("""§P = 5{,}67 \cdot 10^{-8} \cdot 1{,}8 \cdot (${m(skin, 2)}^4 - ${m(wall, 2)}^4)\ \text{W} \approx ${m(sig(p))}\ \text{W}§"""),
                background = l("Jeder Körper leuchtet, wir sehen es nur nicht: Bei Körpertemperatur liegt das Licht im Infrarot. Eine Wärmebildkamera macht es sichtbar. Weil die Abstrahlung mit der vierten Potenz der Temperatur geht, machen schon wenige Grad Unterschied viel aus. Deshalb friert man in einem Raum mit kalten Wänden, auch wenn die Luft warm ist.", "Every body glows; we just can't see it: at body temperature the light is in the infrared. A thermal camera makes it visible. Because radiation goes with the fourth power of temperature, even a few degrees make a big difference. That's why you feel cold in a room with cold walls even if the air is warm."),
                fact = l("Dein Körper erzeugt in Ruhe nur etwa 100 W. Ohne Kleidung würdest du also allein durch Strahlung mehr verlieren, als du nachproduzierst.", "At rest your body produces only about 100 W. Without clothes you would lose more through radiation alone than you produce."),
            )
        },
        lab("phy.meeresspiegel", Topic.Heat, H, 0.5f) {
            val depth = pick(500.0, 700.0, 1000.0); val dT = pick(0.5, 1.0); val beta = 2e-4
            val rise = depth * beta * dT * 100
            task(
                question = l("Die obersten ${n(depth, 0)} m des Ozeans erwärmen sich um ${n(dT)} K. Meerwasser dehnt sich dabei um etwa 0,02 % pro Kelvin aus. Um wie viele Zentimeter steigt der Meeresspiegel allein dadurch?", "The top ${n(depth, 0)} m of the ocean warm by ${n(dT)} K. Seawater expands by about 0.02% per kelvin. By how many centimetres does sea level rise from this alone?"),
                steps = listOf(num(l("Anstieg in cm", "Rise in cm"), rise, "cm", rel = 0.02, done = """§\Delta h \approx ${m(rise, 1)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Das Wasser kann nur nach oben ausweichen. Jede Schicht wird etwas dicker.", "Key idea: the water can only expand upwards. Every layer gets a little thicker."), """§\Delta h = \beta \cdot h \cdot \Delta T§"""))),
                solution = listOf("""§\Delta h = 2 \cdot 10^{-4} \cdot ${m(depth, 0)}\ \text{m} \cdot ${m(dT)} = ${m(rise / 100, 3)}\ \text{m}§"""),
                background = l("Der Meeresspiegel steigt aus zwei Gründen: Gletscher und Eisschilde an Land schmelzen und bringen neues Wasser, und das vorhandene Wasser dehnt sich aus, weil es wärmer wird. Die Ausdehnung ist pro Grad winzig, aber der Ozean ist kilometertief. In den letzten Jahrzehnten war sie etwa für ein Drittel des Anstiegs verantwortlich.", "Sea level rises for two reasons: glaciers and ice sheets on land melt and add new water, and the existing water expands because it gets warmer. The expansion per degree is tiny, but the ocean is kilometres deep. In recent decades it has been responsible for about a third of the rise."),
                fact = l("Schmelzendes Meereis hebt den Meeresspiegel dagegen kaum: Es schwimmt schon und verdrängt genau sein Gewicht.", "Melting sea ice, by contrast, hardly raises the sea level: it is already floating and displaces exactly its own weight."),
            )
        },
        lab("phy.caesar", Topic.Heat, X, 0.7f) {
            val breath = 0.5; val nBreath = breath / 22.4 * 6.022e23; val nAtm = 5.1e18 / 0.029 * 6.022e23
            val expected = nBreath * nBreath / nAtm
            task(
                question = l("Caesars letzter Atemzug (0,5 l) hat sich in 2000 Jahren gleichmäßig in der ganzen Atmosphäre verteilt (5,1 · 10¹⁸ kg Luft, 29 g pro Mol). Wie viele Moleküle davon atmest du mit deinem nächsten Atemzug (0,5 l) im Mittel ein?", "Caesar's last breath (0.5 l) has spread evenly through the whole atmosphere over 2000 years (5.1 · 10¹⁸ kg of air, 29 g per mole). How many of its molecules do you breathe in, on average, with your next breath (0.5 l)?"),
                given = listOf(l("Ein Mol Gas hat etwa 22,4 l und enthält 6,02 · 10²³ Moleküle.", "One mole of gas takes up about 22.4 l and contains 6.02 · 10²³ molecules.")),
                steps = listOf(num(l("Erwartete Anzahl Moleküle", "Expected number of molecules"), expected, "", rel = 0.1, done = """§N \approx ${m(expected, 1)}§""",
                    hints = listOf(l("Kernidee: Erst die Moleküle in einem Atemzug und in der ganzen Atmosphäre zählen. Der Anteil der Caesar-Moleküle an der Luft ist N_Atemzug / N_Atmosphäre.", "Key idea: first count the molecules in one breath and in the whole atmosphere. The share of Caesar molecules in the air is N_breath / N_atmosphere."), """§N = N_{Atem} \cdot \dfrac{N_{Atem}}{N_{Atm}}§""".let { l(it, it.replace("Atem}", "br}").replace("Atm}", "atm}")) }))),
                solution = listOf("""§N_{Atem} = \dfrac{0{,}5}{22{,}4} \cdot 6{,}02 \cdot 10^{23} \approx ${sci(nBreath)}§""".let { l(it, it.replace("Atem", "br")) }, """§N_{Atm} = \dfrac{5{,}1 \cdot 10^{18}}{0{,}029} \cdot 6{,}02 \cdot 10^{23} \approx ${sci(nAtm)}§""".let { l(it, it.replace("Atm", "atm")) }, """§N = \dfrac{(${sci(nBreath)})^2}{${sci(nAtm)}} \approx ${m(expected, 1)}§"""),
                background = l("Ein Atemzug enthält etwa so viele Moleküle, wie die Atmosphäre Atemzüge enthält. Deshalb kommt bei dieser berühmten Schätzfrage etwa eins heraus. Solche Fermi-Rechnungen brauchen keine genauen Zahlen, nur die richtigen Größenordnungen. Wir unterschätzen fast immer, wie winzig Moleküle sind und wie viele es davon gibt.", "One breath contains about as many molecules as the atmosphere contains breaths. That's why this famous estimate comes out at about one. Such Fermi calculations don't need exact numbers, only the right orders of magnitude. We almost always underestimate how tiny molecules are and how many of them there are."),
                fact = l("Ein Teil der Moleküle ist natürlich in Meere, Pflanzen und Gestein gewandert. Die Rechnung ist trotzdem verblüffend robust: Es bleibt etwa ein Molekül.", "Some of the molecules have of course moved into oceans, plants and rocks. Even so, the calculation is surprisingly robust: about one molecule remains."),
            )
        },
        lab("phy.diesel", Topic.Heat, X, 0.6f) {
            val ratio = pick(16.0, 18.0, 20.0); val t1 = 300.0
            val t2 = t1 * ratio.pow(0.4); val tc = t2 - 273.15
            task(
                question = l("Ein Dieselmotor verdichtet Luft (300 K) so schnell auf 1/${n(ratio, 0)} ihres Volumens, dass keine Wärme entweichen kann (adiabatisch, κ = 1,4). Wie heiß wird die Luft?", "A diesel engine quickly compresses air (300 K) to 1/${n(ratio, 0)} of its volume without any heat escaping (adiabatically, γ = 1.4). How hot does the air get?"),
                steps = listOf(num(l("Temperatur in °C", "Temperature in °C"), tc, "°C", rel = 0.02, done = """§T \approx ${m(tc, 0)}\ ^\circ\text{C}§""",
                    hints = listOf(l("Kernidee: Bei schneller Verdichtung bleibt die Arbeit des Kolbens als Wärme im Gas.", "Key idea: in rapid compression the piston's work stays in the gas as heat."), """§T_2 = T_1 \left(\dfrac{V_1}{V_2}\right)^{\kappa - 1}§"""))),
                solution = listOf("""§T_2 = 300 \cdot ${m(ratio, 0)}^{0{,}4}\ \text{K} \approx ${m(t2, 0)}\ \text{K} \approx ${m(tc, 0)}\ ^\circ\text{C}§"""),
                background = l("Drückt man ein Gas zusammen, steckt man Arbeit hinein. Geht das so schnell, dass keine Wärme entweichen kann, wird das Gas heiß. Rudolf Diesel nutzte das: Sein Motor braucht keine Zündkerze. Der Treibstoff wird in die heiße Luft gespritzt und entzündet sich von selbst, weil sie heißer ist als seine Zündtemperatur von etwa 250 °C.", "When you compress a gas you put work into it. If it happens so fast that no heat can escape, the gas gets hot. Rudolf Diesel used this: his engine needs no spark plug. The fuel is sprayed into the hot air and ignites by itself, because the air is hotter than its ignition temperature of about 250 °C."),
                fact = l("Eine Fahrradpumpe wird aus demselben Grund warm. Und ein Feuerkolben, ein Glaszylinder mit Stempel, entzündet ein Stück Watte nur durch einen kräftigen Stoß.", "A bike pump gets warm for the same reason. And a fire piston, a glass cylinder with a plunger, lights a piece of cotton wool with nothing but one hard push."),
            )
        },
        lab("phy.eisentropie", Topic.Heat, X, 0.6f) {
            val mass = pick(1.0, 0.5, 2.0); val ds = mass * 334000 / 273.15
            task(
                question = l("${n(mass)} kg Eis schmelzen bei 0 °C zu Wasser von 0 °C. Um wie viel nimmt die Entropie des Wassers dabei zu?", "${n(mass)} kg of ice melt at 0 °C into water at 0 °C. By how much does the water's entropy increase?"),
                steps = listOf(num(l("Entropiezunahme in J/K", "Increase in entropy in J/K"), ds, "J/K", rel = 0.02, done = """§\Delta S \approx ${m(sig(ds))}\ \text{J/K}§""",
                    hints = listOf(l("Kernidee: Bei konstanter Temperatur ist die Entropieänderung einfach die zugeführte Wärme geteilt durch die absolute Temperatur.", "Key idea: at constant temperature the change in entropy is simply the heat added divided by the absolute temperature."), """§\Delta S = \dfrac{Q}{T} = \dfrac{m L_s}{T}§"""))),
                solution = listOf("""§\Delta S = \dfrac{${m(mass)} \cdot 334\,000}{273{,}15}\ \tfrac{\text{J}}{\text{K}} \approx ${m(sig(ds))}\ \tfrac{\text{J}}{\text{K}}§"""),
                background = l("Entropie misst, auf wie viele Arten die Teilchen eines Systems angeordnet sein können. Im Eiskristall sitzen die Moleküle auf festen Plätzen, im Wasser können sie sich fast beliebig umordnen: Die Entropie steigt. Wärme, die bei niedriger Temperatur zugeführt wird, bringt dabei mehr Entropie als dieselbe Wärme bei hoher Temperatur.", "Entropy measures how many ways the particles of a system can be arranged. In an ice crystal the molecules sit in fixed places; in water they can rearrange almost freely: the entropy rises. Heat added at a low temperature brings more entropy than the same heat added at a high temperature."),
                fact = l("Die Entropie des Universums kann nur wachsen. Viele Physiker sehen darin den Grund, warum die Zeit eine Richtung hat.", "The entropy of the universe can only grow. Many physicists see this as the reason why time has a direction."),
            )
        },
    )

    private val waves = listOf(
        lab("phy.orgelpfeife", Topic.Waves, M, 0.5f) {
            val f = pick(16.35, 32.7, 65.4); val len = 343 / (2 * f)
            task(
                question = l("Eine offene Orgelpfeife (an beiden Enden offen) soll als tiefsten Ton ${n(f, 2)} Hz spielen. Wie lang muss sie sein (Schall 343 m/s)?", "An open organ pipe (open at both ends) is to play ${n(f, 2)} Hz as its lowest note. How long must it be (sound at 343 m/s)?"),
                steps = listOf(num(l("Länge in Metern", "Length in metres"), len, "m", rel = 0.02, done = """§L \approx ${m(len, 2)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: In einer beidseitig offenen Pfeife passt beim Grundton genau eine halbe Wellenlänge hinein.", "Key idea: in a pipe open at both ends, exactly half a wavelength fits in at the fundamental."), """§L = \dfrac{\lambda}{2} = \dfrac{c}{2f}§"""))),
                solution = listOf("""§L = \dfrac{343}{2 \cdot ${m(f, 2)}}\ \text{m} \approx ${m(len, 2)}\ \text{m}§"""),
                background = l("In einer Pfeife schwingt die Luftsäule in stehenden Wellen. An offenen Enden kann die Luft frei hin und her schwingen, dort liegen Bäuche. Tiefe Töne brauchen lange Wellen und damit lange Pfeifen. Eine an einem Ende geschlossene Pfeife klingt bei gleicher Länge eine Oktave tiefer, weil dort nur eine Viertelwelle hineinpasst.", "In a pipe the column of air vibrates in standing waves. At open ends the air can swing freely back and forth, so there are antinodes there. Low notes need long waves and therefore long pipes. A pipe closed at one end sounds an octave lower for the same length, because only a quarter wave fits in."),
                fact = l("Die längsten Orgelpfeifen messen etwa 10 Meter, bei 32 Fuß. Ihren tiefsten Ton hört man kaum noch, man spürt ihn eher im Bauch.", "The longest organ pipes are about 10 metres, at 32 feet. You can hardly hear their lowest note any more; you feel it in your stomach instead."),
            )
        },
        lab("phy.flasche", Topic.Waves, E, 0.4f) {
            task(
                question = l("Du bläst über den Hals einer leeren Flasche und hörst einen tiefen Ton. Dann füllst du etwas Wasser hinein und bläst wieder. Wie ändert sich der Ton?", "You blow across the neck of an empty bottle and hear a low note. Then you pour some water in and blow again. How does the note change?"),
                steps = listOf(pick1(l("Der Ton wird …", "The note gets …"), l("höher", "higher"), listOf(l("tiefer", "lower"), l("leiser, bleibt aber gleich hoch", "quieter but stays at the same pitch"), l("gar nicht anders", "no different at all")),
                    hints = listOf(l("Kernidee: Es klingt die Luft in der Flasche, nicht das Glas.", "Key idea: it's the air in the bottle that sounds, not the glass."), l("Mit Wasser bleibt weniger Luft übrig. Kürzere Luftsäulen schwingen schneller.", "With water there's less air left. Shorter air columns vibrate faster.")))),
                solution = listOf(l("Das Wasser verkleinert die schwingende Luftsäule. Eine kürzere Säule passt zu einer kürzeren Welle, also zu einer höheren Frequenz.", "The water shrinks the vibrating column of air. A shorter column matches a shorter wave, i.e. a higher frequency.")),
                background = l("Beim Blasen regt der Luftstrom die eingeschlossene Luft zum Schwingen an, wie bei einer Orgelpfeife. Je weniger Luft, desto höher der Ton. Klopft man dagegen mit einem Löffel an ein Glas, ist es umgekehrt: Dann schwingt das Glas selbst, und Wasser macht es schwerer und den Ton tiefer.", "When you blow, the airflow sets the trapped air vibrating, like in an organ pipe. The less air, the higher the note. If you tap a glass with a spoon, it's the other way round: then the glass itself vibrates, and water makes it heavier and the note lower."),
                fact = l("Mit acht Flaschen und passend viel Wasser kann man eine ganze Tonleiter spielen.", "With eight bottles and the right amounts of water you can play a whole scale."),
            )
        },
        lab("phy.fledermaus", Topic.Waves, M, 0.4f) {
            val f = pick(40.0, 50.0, 80.0); val lambda = 343 / (f * 1000) * 1000
            task(
                question = l("Eine Fledermaus ortet Insekten mit Ultraschall von ${n(f, 0)} kHz. Wie lang sind diese Schallwellen? Etwa so klein darf ein Insekt sein, damit sie es gerade noch bemerkt.", "A bat locates insects with ultrasound at ${n(f, 0)} kHz. How long are these sound waves? An insect can be about that small for the bat still to notice it."),
                steps = listOf(num(l("Wellenlänge in mm", "Wavelength in mm"), lambda, "mm", rel = 0.02, done = """§\lambda \approx ${m(lambda, 1)}\ \text{mm}§""",
                    hints = listOf(l("Kernidee: Wellenlänge = Schallgeschwindigkeit durch Frequenz. kHz in Hz umrechnen.", "Key idea: wavelength = speed of sound over frequency. Convert kHz to Hz."), """§\lambda = \dfrac{c}{f}§"""))),
                solution = listOf("""§\lambda = \dfrac{343\ \text{m/s}}{${m(f * 1000, 0)}\ \text{Hz}} \approx ${m(lambda, 1)}\ \text{mm}§"""),
                background = l("Wellen werden nur von Dingen gut zurückgeworfen, die mindestens etwa so groß sind wie ihre Wellenlänge. Kleinere Dinge umfließt die Welle einfach. Deshalb nutzen Fledermäuse sehr hohe Frequenzen, die wir nicht hören. Mit tiefen Tönen würden Mücken für sie unsichtbar sein.", "Waves are only reflected well by objects at least about as big as their wavelength. The wave simply flows around smaller ones. That's why bats use very high frequencies that we can't hear. With low notes, mosquitoes would be invisible to them."),
                fact = l("Manche Fledermäuse erkennen sogar, ob ein Insekt die Flügel bewegt: am winzigen Dopplereffekt des Echos.", "Some bats can even tell whether an insect is beating its wings: from the tiny Doppler effect in the echo."),
            )
        },
        lab("phy.tsunami", Topic.Waves, M, 0.5f) {
            val d = pick(4000.0, 3000.0, 5000.0); val v = sqrt(G4 * d) * 3.6
            task(
                question = l("Tsunamiwellen sind so lang, dass sie den ganzen Ozean bis zum Boden bewegen. Ihre Geschwindigkeit hängt dann nur von der Wassertiefe d ab: v = √(g·d). Wie schnell rast ein Tsunami über ${n(d, 0)} m tiefes Meer?", "Tsunami waves are so long that they move the whole ocean down to the floor. Their speed then depends only on the water depth d: v = √(g·d). How fast does a tsunami race across sea ${n(d, 0)} m deep?"),
                steps = listOf(num(l("Geschwindigkeit in km/h", "Speed in km/h"), v, "km/h", rel = 0.02, done = """§v \approx ${m(sig(v))}\ \text{km/h}§""",
                    hints = listOf(l("Kernidee: Einfach einsetzen und von m/s in km/h umrechnen (mal 3,6).", "Key idea: just plug in and convert from m/s to km/h (times 3.6)."), """§v = \sqrt{9{,}81 \cdot ${m(d, 0)}}\ \text{m/s}§"""))),
                solution = listOf("""§v = \sqrt{9{,}81 \cdot ${m(d, 0)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v / 3.6, 0)}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v))}\ \text{km/h}§"""),
                background = l("Auf hoher See ist ein Tsunami oft nur einen Meter hoch und für Schiffe kaum zu bemerken, aber er rast so schnell wie ein Flugzeug. Nähert er sich der Küste, wird das Wasser flacher und die Welle langsamer. Die hinteren Teile holen die vorderen ein, das Wasser türmt sich auf viele Meter Höhe.", "On the open sea a tsunami is often only a metre high and hardly noticeable for ships, but it races along as fast as a plane. As it nears the coast the water gets shallower and the wave slows down. The rear parts catch up with the front, and the water piles up many metres high."),
                fact = l("Nach dem Seebeben vor Sumatra 2004 erreichte der Tsunami nach etwa sieben Stunden sogar die Küste Afrikas.", "After the 2004 earthquake off Sumatra the tsunami even reached the coast of Africa after about seven hours."),
            )
        },
        lab("phy.pswellen", Topic.Waves, H, 0.6f) {
            val dt = pick(20.0, 30.0, 45.0); val vp = 6.0; val vs = 3.5
            val d = dt / (1 / vs - 1 / vp)
            task(
                question = l("Bei einem Erdbeben laufen zwei Wellenarten durch das Gestein: P-Wellen mit 6,0 km/s und langsamere S-Wellen mit 3,5 km/s. Eine Messstation registriert die S-Welle ${n(dt, 0)} s nach der P-Welle. Wie weit ist das Beben entfernt?", "In an earthquake two kinds of wave travel through the rock: P waves at 6.0 km/s and slower S waves at 3.5 km/s. A station records the S wave ${n(dt, 0)} s after the P wave. How far away is the earthquake?"),
                steps = listOf(num(l("Entfernung in km", "Distance in km"), d, "km", rel = 0.02, done = """§d \approx ${m(sig(d))}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Beide Wellen laufen dieselbe Strecke. Der Zeitunterschied wächst mit der Entfernung.", "Key idea: both waves travel the same distance. The time difference grows with the distance."), """§\dfrac{d}{v_S} - \dfrac{d}{v_P} = \Delta t§"""))),
                solution = listOf("""§d = \dfrac{${m(dt, 0)}\ \text{s}}{\tfrac{1}{3{,}5} - \tfrac{1}{6{,}0}\ \tfrac{\text{s}}{\text{km}}} \approx ${m(sig(d))}\ \text{km}§"""),
                background = l("P-Wellen sind Druckwellen wie Schall, S-Wellen schütteln das Gestein seitwärts und sind langsamer. Aus dem Abstand der beiden kennt jede Station ihre Entfernung, aber nicht die Richtung. Mit drei Stationen schneiden sich drei Kreise in einem Punkt: dem Epizentrum.", "P waves are pressure waves like sound; S waves shake the rock sideways and are slower. From the gap between them each station knows its distance but not the direction. With three stations, three circles intersect at one point: the epicentre."),
                fact = l("S-Wellen kommen nicht durch flüssiges Gestein. Dass sie auf der anderen Seite der Erde fehlen, verriet den Forschern den flüssigen äußeren Erdkern.", "S waves can't pass through liquid rock. Their absence on the far side of the Earth revealed the liquid outer core to scientists."),
            )
        },
        lab("phy.anc", Topic.Waves, E, 0.5f) {
            task(
                question = l("Kopfhörer mit aktiver Geräuschunterdrückung haben außen Mikrofone. Wie machen sie den Lärm im Flugzeug leiser?", "Headphones with active noise cancelling have microphones on the outside. How do they make the noise on a plane quieter?"),
                steps = listOf(pick1(l("Die Kopfhörer …", "The headphones …"),
                    l("spielen den Lärm spiegelverkehrt ab, sodass sich die Wellen gegenseitig auslöschen", "play the noise back inverted so that the waves cancel each other out"),
                    listOf(l("dämpfen mit besonders dickem Schaumstoff", "damp it with extra-thick foam"), l("spielen ein Rauschen, das den Lärm übertönt", "play a hiss that drowns out the noise"), l("filtern den Lärm aus der Luft heraus", "filter the noise out of the air")),
                    hints = listOf(l("Kernidee: Schall ist eine Druckwelle. Was passiert, wenn ein Wellenberg auf ein Wellental trifft?", "Key idea: sound is a pressure wave. What happens when a crest meets a trough?"), l("Die Mikrofone hören den Lärm, bevor er dein Ohr erreicht.", "The microphones hear the noise before it reaches your ear.")))),
                solution = listOf(l("Ein Chip erzeugt ein Gegensignal, bei dem jeder Überdruck ein Unterdruck ist. Überlagert heben sich beide fast auf: destruktive Interferenz.", "A chip generates an opposite signal in which every high pressure is a low pressure. Combined, the two almost cancel: destructive interference.")),
                background = l("Wellen addieren sich einfach. Treffen zwei genau entgegengesetzte Wellen zusammen, bleibt nichts übrig. Bei gleichmäßigem, tiefem Lärm wie Triebwerksbrummen funktioniert das sehr gut, weil sich der Lärm gut vorhersagen lässt. Plötzliche, hohe Geräusche wie Stimmen kommen eher durch.", "Waves simply add up. When two exactly opposite waves meet, nothing is left. With steady, low noise like engine hum this works very well because the noise is easy to predict. Sudden, high sounds like voices tend to get through."),
                fact = l("Die Idee wurde schon 1934 patentiert, aber erst schnelle Mikrochips machten sie alltagstauglich.", "The idea was patented as early as 1934, but only fast microchips made it practical."),
            )
        },
        lab("phy.mikrowelle", Topic.Waves, M, 0.6f) {
            val d = pick(6.0, 6.1, 6.2); val c = 2 * d / 100 * 2.45e9
            task(
                question = l("Du nimmst den Drehteller aus der Mikrowelle (2,45 GHz) und erwärmst eine Tafel Schokolade kurz. Sie schmilzt an Stellen, die ${n(d, 1)} cm voneinander entfernt sind. Welche Lichtgeschwindigkeit misst du damit?", "You take the turntable out of the microwave (2.45 GHz) and briefly heat a bar of chocolate. It melts at spots ${n(d, 1)} cm apart. What speed of light do you measure with this?"),
                steps = listOf(num(l("Lichtgeschwindigkeit in m/s", "Speed of light in m/s"), c, "m/s", rel = 0.02, done = """§c \approx ${sci(c)}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: In der Mikrowelle bilden sich stehende Wellen. Heiße Stellen liegen eine halbe Wellenlänge auseinander.", "Key idea: standing waves form in the microwave. Hot spots are half a wavelength apart."), """§\lambda = 2d,\quad c = \lambda f§"""))),
                solution = listOf("""§\lambda = 2 \cdot ${m(d, 1)}\ \text{cm} = ${m(2 * d, 1)}\ \text{cm}§""", """§c = ${m(2 * d / 100, 3)}\ \text{m} \cdot 2{,}45 \cdot 10^9\ \text{Hz} \approx ${sci(c)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = l("Mikrowellen sind Licht mit etwa 12 cm Wellenlänge. Zwischen den Metallwänden laufen sie hin und her und überlagern sich zu stehenden Wellen: An manchen Stellen schwingt das Feld stark, an anderen fast gar nicht. Deshalb gibt es den Drehteller, der das Essen durch die heißen Stellen bewegt.", "Microwaves are light with a wavelength of about 12 cm. Between the metal walls they bounce back and forth and overlap into standing waves: at some points the field oscillates strongly, at others hardly at all. That's what the turntable is for: it moves the food through the hot spots."),
                fact = l("Mit einem Stück Schokolade und einem Lineal misst man so die schnellste Geschwindigkeit des Universums auf wenige Prozent genau.", "With a bar of chocolate and a ruler you can measure the fastest speed in the universe to within a few percent."),
            )
        },
        lab("phy.heliumstimme", Topic.Waves, M, 0.6f) {
            task(
                question = l("Nach einem Atemzug Helium klingt die Stimme wie eine Comicfigur. Was ist der Grund?", "After a breath of helium your voice sounds like a cartoon character. What is the reason?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Schall ist in Helium fast dreimal so schnell. Dadurch verschieben sich die Resonanzen von Mund und Rachen nach oben.", "Sound travels almost three times as fast in helium. This shifts the resonances of the mouth and throat upwards."),
                    listOf(l("Die Stimmbänder schwingen in Helium dreimal so schnell.", "The vocal cords vibrate three times as fast in helium."), l("Helium ist leichter und drückt die Stimmbänder nach oben.", "Helium is lighter and pushes the vocal cords upwards."), l("Das Ohr hört durch Helium anders.", "The ear hears differently through helium.")),
                    hints = listOf(l("Kernidee: Ein Ton hat einen Grundton und Klangfarben. Was davon kommt von den Stimmbändern, was vom Hohlraum dahinter?", "Key idea: a voice has a fundamental pitch and a timbre. Which comes from the vocal cords, which from the cavity behind?"), l("Die Resonanzfrequenz eines Hohlraums hängt von der Schallgeschwindigkeit darin ab.", "The resonance frequency of a cavity depends on the speed of sound inside it.")))),
                solution = listOf(l("Die Stimmbänder schwingen fast genauso schnell wie sonst. Aber die Klangfarbe entsteht in Mund und Rachen, und deren Resonanzen sind proportional zur Schallgeschwindigkeit. In Helium liegen sie viel höher, die Stimme klingt quäkig.", "The vocal cords vibrate almost as fast as usual. But the timbre is formed in the mouth and throat, and their resonances are proportional to the speed of sound. In helium they are much higher, and the voice sounds squeaky.")),
                background = l("Leichte Gase leiten Schall schneller, weil ihre Moleküle schneller unterwegs sind. Unser Gehör erkennt eine Stimme vor allem an den Formanten, den verstärkten Frequenzbereichen des Rachens. Rutschen die nach oben, klingt die Stimme höher, obwohl der eigentliche Grundton kaum steigt. Mit Schwefelhexafluorid, einem schweren Gas, geht es umgekehrt.", "Light gases carry sound faster because their molecules move faster. Our hearing recognises a voice mainly by its formants, the frequency ranges amplified by the throat. If they slide upwards the voice sounds higher, even though the actual pitch hardly rises. With sulphur hexafluoride, a heavy gas, it works the other way round."),
                fact = l("Helium aus Ballons einzuatmen ist nicht ungefährlich: Es verdrängt Sauerstoff aus der Lunge. Ein Atemzug Luft danach ist Pflicht.", "Inhaling helium from balloons isn't harmless: it pushes oxygen out of your lungs. A breath of air afterwards is a must."),
            )
        },
        lab("phy.machkegel", Topic.Waves, H, 0.6f) {
            val mach = pick(2.0, 1.5, 3.0); val h = 10.0
            val ang = Math.toDegrees(asin(1 / mach)); val x = h / tan(Math.toRadians(ang))
            task(
                question = l("Ein Überschalljet fliegt mit Mach ${n(mach, 1)} in 10 km Höhe genau über dich hinweg. Der Knall breitet sich als Kegel hinter ihm aus.", "A supersonic jet flies at Mach ${n(mach, 1)} at an altitude of 10 km directly over you. The boom spreads out as a cone behind it."),
                steps = listOf(
                    num(l("Welchen halben Öffnungswinkel hat der Kegel?", "What is the half-angle of the cone?"), ang, "°", abs = 0.5, done = """§\alpha \approx ${m(ang, 1)}^\circ§""",
                        hints = listOf(l("Kernidee: Während der Jet eine Strecke v·t fliegt, breitet sich der Schall nur um c·t aus.", "Key idea: while the jet flies a distance v·t, the sound spreads out only by c·t."), """§\sin\alpha = \dfrac{c}{v} = \dfrac{1}{M}§""")),
                    num(l("Wie weit ist der Jet schon an dir vorbei (waagrecht), wenn du den Knall hörst?", "How far past you (horizontally) is the jet when you hear the boom?"), x, "km", rel = 0.02, done = """§x \approx ${m(x, 1)}\ \text{km}§""",
                        hints = listOf(l("Der Kegelmantel muss dich erreichen. Zeichne das Dreieck aus Höhe, waagrechtem Abstand und Kegelrand.", "The surface of the cone has to reach you. Draw the triangle of height, horizontal distance and cone edge."), """§\tan\alpha = \dfrac{h}{x}§""")),
                ),
                solution = listOf("""§\alpha = \arcsin\dfrac{1}{${m(mach, 1)}} \approx ${m(ang, 1)}^\circ§""", """§x = \dfrac{10\ \text{km}}{\tan ${m(ang, 1)}^\circ} \approx ${m(x, 1)}\ \text{km}§"""),
                background = l("Ein Flugzeug unter Schallgeschwindigkeit schickt seinen Lärm voraus. Darüber überholt es die eigenen Schallwellen, und die stauen sich zu einem Kegel, dem Machkegel. Wo sein Rand den Boden erreicht, hört man einen Knall. Er entsteht nicht einmal beim Durchbrechen der Schallmauer, sondern zieht die ganze Zeit hinter dem Jet her.", "A plane below the speed of sound sends its noise ahead of it. Above it, the plane overtakes its own sound waves, which pile up into a cone, the Mach cone. Where its edge reaches the ground you hear a bang. It isn't made once when breaking the sound barrier; it trails behind the jet the whole time."),
                fact = l("Eine Peitsche knallt aus demselben Grund: Ihre Spitze wird schneller als der Schall.", "A whip cracks for the same reason: its tip moves faster than sound."),
            )
        },
        lab("phy.konzert", Topic.Waves, H, 0.5f) {
            val l1 = pick(110.0, 115.0, 105.0); val r1 = 10.0; val r2 = pick(100.0, 40.0, 30.0)
            val l2 = l1 - 20 * log10(r2 / r1)
            task(
                question = l("Direkt vor der Bühne, 10 m von den Boxen entfernt, misst du ${n(l1, 0)} dB. Wie laut ist es ${n(r2, 0)} m entfernt? Der Schall breitet sich frei nach allen Seiten aus.", "Right in front of the stage, 10 m from the speakers, you measure ${n(l1, 0)} dB. How loud is it ${n(r2, 0)} m away? The sound spreads freely in all directions."),
                steps = listOf(num(l("Schallpegel in dB", "Sound level in dB"), l2, "dB", abs = 0.5, done = """§L \approx ${m(l2, 1)}\ \text{dB}§""",
                    hints = listOf(l("Kernidee: Die Intensität verteilt sich auf eine Kugeloberfläche, sie fällt mit 1/r². Doppelte Entfernung, ein Viertel der Intensität, also 6 dB weniger.", "Key idea: the intensity spreads over the surface of a sphere and falls off as 1/r². Twice the distance, a quarter of the intensity, so 6 dB less."), """§\Delta L = 20 \log_{10} \dfrac{r_2}{r_1}§"""))),
                solution = listOf("""§\Delta L = 20 \log_{10} \dfrac{${m(r2, 0)}}{10} \approx ${m(l1 - l2, 1)}\ \text{dB}§""", """§L = ${m(l1, 0)} - ${m(l1 - l2, 1)} \approx ${m(l2, 1)}\ \text{dB}§"""),
                background = l("Dezibel sind eine logarithmische Skala: 10 dB weniger heißt ein Zehntel der Intensität. Weil die Intensität mit dem Quadrat des Abstands sinkt, bringt jede Verzehnfachung des Abstands 20 dB weniger. Schon ein paar Schritte zurück schützen das Gehör deutlich besser als man denkt.", "Decibels are a logarithmic scale: 10 dB less means a tenth of the intensity. Because intensity drops with the square of the distance, every tenfold increase in distance gives 20 dB less. Even a few steps back protect your hearing much better than you might think."),
                fact = l("Ab etwa 85 dB über Stunden nimmt das Gehör Schaden. Gehörschutz bei Konzerten dämpft um 15 bis 25 dB, ohne die Musik dumpf zu machen.", "Above about 85 dB for hours, hearing gets damaged. Concert earplugs reduce sound by 15 to 25 dB without making the music sound muffled."),
            )
        },
        lab("phy.radioantenne", Topic.Waves, E, 0.4f) {
            val f = pick(100.0, 90.0, 150.0); val lambda = 3e8 / (f * 1e6); val ant = lambda / 4 * 100
            task(
                question = l("Ein UKW-Sender funkt mit ${n(f, 0)} MHz, Radiowellen laufen mit Lichtgeschwindigkeit (3 · 10⁸ m/s). Eine einfache Stabantenne ist am besten ein Viertel einer Wellenlänge lang. Wie lang ist das?", "An FM station transmits at ${n(f, 0)} MHz, and radio waves travel at the speed of light (3 · 10⁸ m/s). A simple rod antenna works best if it is a quarter of a wavelength long. How long is that?"),
                steps = listOf(num(l("Antennenlänge in cm", "Antenna length in cm"), ant, "cm", rel = 0.02, done = """§L \approx ${m(ant, 0)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Erst die Wellenlänge aus c = λ·f, dann ein Viertel davon.", "Key idea: first the wavelength from c = λ·f, then a quarter of it."), """§\lambda = \dfrac{c}{f},\quad L = \dfrac{\lambda}{4}§"""))),
                solution = listOf("""§\lambda = \dfrac{3 \cdot 10^8}{${m(f, 0)} \cdot 10^6}\ \text{m} = ${m(lambda, 2)}\ \text{m}§""", """§L = \dfrac{${m(lambda, 2)}}{4}\ \text{m} \approx ${m(ant, 0)}\ \text{cm}§"""),
                background = l("Radiowellen sind Licht mit sehr großer Wellenlänge. Eine Antenne funktioniert am besten, wenn die Elektronen darin im Takt der Welle hin und her schwingen können, wie eine Saite, die zur Tonhöhe passt. Deshalb haben Handys winzige Antennen (hohe Frequenzen) und Mittelwellensender riesige Masten.", "Radio waves are light with a very long wavelength. An antenna works best when the electrons in it can swing back and forth in step with the wave, like a string tuned to the right pitch. That's why phones have tiny antennas (high frequencies) and medium-wave transmitters have huge masts."),
                fact = l("Der Langwellensender für die Funkuhren in Deutschland (DCF77, 77,5 kHz) hat eine Wellenlänge von fast 4 km.", "The long-wave transmitter for radio clocks in Germany (DCF77, 77.5 kHz) has a wavelength of almost 4 km."),
            )
        },
        lab("phy.lautsprecher2", Topic.Waves, H, 0.6f) {
            val dPath = pick(0.5, 0.25, 0.4); val f = 343 / (2 * dPath)
            task(
                question = l("Zwei Lautsprecher spielen denselben Ton im Gleichtakt. Du stehst so, dass du vom einen ${n(dPath * 100, 0)} cm weiter entfernt bist als vom anderen. Bei welcher tiefsten Frequenz hörst du an dieser Stelle fast nichts?", "Two loudspeakers play the same note in step. You stand so that you are ${n(dPath * 100, 0)} cm farther from one than from the other. At what lowest frequency do you hear almost nothing at this spot?"),
                steps = listOf(num(l("Frequenz in Hz", "Frequency in Hz"), f, "Hz", rel = 0.02, done = """§f \approx ${m(f, 0)}\ \text{Hz}§""",
                    hints = listOf(l("Kernidee: Still wird es, wenn ein Wellenberg des einen auf ein Wellental des anderen trifft. Der Wegunterschied muss dafür eine halbe Wellenlänge sein.", "Key idea: it goes quiet when a crest from one meets a trough from the other. For that the path difference must be half a wavelength."), """§\Delta s = \dfrac{\lambda}{2} \Rightarrow f = \dfrac{c}{2\Delta s}§"""))),
                solution = listOf("""§\lambda = 2 \cdot ${m(dPath)}\ \text{m} = ${m(2 * dPath)}\ \text{m}§""", """§f = \dfrac{343}{${m(2 * dPath)}}\ \text{Hz} \approx ${m(f, 0)}\ \text{Hz}§"""),
                background = l("Zwei gleiche Schallquellen erzeugen im Raum ein Muster aus lauten und leisen Zonen. Wo die Wege sich um eine ganze Wellenlänge unterscheiden, verstärken sich die Wellen, bei einer halben löschen sie sich aus. Bei Musik merkt man es kaum, weil viele Frequenzen gemischt sind, bei einem reinen Ton aber deutlich, wenn man durch den Raum geht.", "Two identical sound sources create a pattern of loud and quiet zones in a room. Where the paths differ by a whole wavelength the waves reinforce each other; by half a wavelength they cancel. With music you hardly notice because many frequencies are mixed, but with a pure tone it's obvious as you walk through the room."),
                fact = l("Polt man bei einer Stereoanlage einen Lautsprecher falsch herum an, fehlt plötzlich der Bass: Die tiefen Töne löschen sich zwischen den Boxen aus.", "If one speaker of a stereo is wired the wrong way round, the bass suddenly disappears: the low notes cancel out between the speakers."),
            )
        },
        lab("phy.radarpistole", Topic.Waves, X, 0.6f) {
            val vKmh = pick(150.0, 140.0, 160.0); val f = 24.15e9; val v = vKmh / 3.6
            val df = 2 * v * f / 3e8
            task(
                question = l("Eine Radarpistole sendet Mikrowellen mit 24,15 GHz auf einen Baseball, der mit ${n(vKmh, 0)} km/h auf sie zufliegt. Um wie viel Hertz ist die Frequenz des Echos höher?", "A radar gun sends microwaves at 24.15 GHz at a baseball flying towards it at ${n(vKmh, 0)} km/h. By how many hertz is the frequency of the echo higher?"),
                steps = listOf(num(l("Frequenzverschiebung in Hz", "Frequency shift in Hz"), df, "Hz", rel = 0.02, done = """§\Delta f \approx ${m(sig(df))}\ \text{Hz}§""",
                    hints = listOf(l("Kernidee: Der Dopplereffekt wirkt zweimal: Der Ball empfängt die Welle schon erhöht und sendet sie als bewegter Spiegel noch einmal erhöht zurück.", "Key idea: the Doppler effect acts twice: the ball receives the wave already shifted up and, as a moving mirror, sends it back shifted up again."), """§\Delta f = \dfrac{2 v}{c} f§"""))),
                solution = listOf("""§v = ${m(v, 1)}\ \text{m/s}§""", """§\Delta f = \dfrac{2 \cdot ${m(v, 1)}}{3 \cdot 10^8} \cdot 2{,}415 \cdot 10^{10}\ \text{Hz} \approx ${m(sig(df))}\ \text{Hz}§"""),
                background = l("Die Verschiebung ist winzig, nur Millionstel der Frequenz. Man misst sie, indem man das Echo mit dem ausgesandten Signal mischt: Es entsteht eine Schwebung genau mit der Differenzfrequenz, und die lässt sich leicht zählen. So arbeiten auch Wetterradar und Abstandssensoren in Autos.", "The shift is tiny, only millionths of the frequency. It is measured by mixing the echo with the transmitted signal: this produces a beat at exactly the difference frequency, which is easy to count. Weather radar and distance sensors in cars work the same way."),
                fact = l("Mit derselben Idee messen Astronomen, wie schnell sich Sterne auf uns zu oder von uns weg bewegen, nur mit Licht statt Radar.", "Astronomers use the same idea to measure how fast stars move towards or away from us, only with light instead of radar."),
            )
        },
    )

    private val optics = listOf(
        lab("phy.regenbogen", Topic.Optics, M, 0.5f) {
            task(
                question = l("Es ist später Nachmittag, die Sonne steht im Westen, und es regnet. In welcher Richtung musst du nach einem Regenbogen suchen?", "It's late afternoon, the Sun is in the west, and it's raining. In which direction should you look for a rainbow?"),
                steps = listOf(pick1(l("Richtung", "Direction"), l("Nach Osten, mit der Sonne im Rücken", "To the east, with the Sun behind you"), listOf(l("Nach Westen, in Richtung Sonne", "To the west, towards the Sun"), l("Senkrecht nach oben", "Straight up"), l("Egal, er ist überall gleich gut zu sehen", "It doesn't matter, it's equally visible everywhere")),
                    hints = listOf(l("Kernidee: Das Licht wird in den Tropfen gebrochen und an der Rückseite gespiegelt, es kommt also ungefähr zurück, woher es kam.", "Key idea: the light is refracted in the drops and reflected at their back, so it comes back roughly the way it came."), l("Der Bogen ist ein Kreis um den Gegenpunkt der Sonne, mit etwa 42° Radius.", "The bow is a circle around the point opposite the Sun, with a radius of about 42°.")))),
                solution = listOf(l("Der Regenbogen ist ein Kreis mit etwa 42° Radius um den Punkt, auf den dein Schatten zeigt. Mit der Sonne im Westen liegt er im Osten.", "The rainbow is a circle about 42° in radius around the point your shadow points to. With the Sun in the west, it is in the east.")),
                background = l("In jedem Tropfen wird Sonnenlicht beim Eintreten gebrochen, an der Rückwand gespiegelt und beim Austreten noch einmal gebrochen. Die verschiedenen Farben werden dabei unterschiedlich stark abgelenkt, Rot unter 42°, Violett unter 40°. Jeder sieht seinen eigenen Regenbogen aus anderen Tropfen. Steht die Sonne höher als 42°, liegt der Bogen ganz unter dem Horizont.", "In every drop sunlight is refracted on entering, reflected off the back wall and refracted again on leaving. The different colours are bent by different amounts, red at 42°, violet at 40°. Everyone sees their own rainbow, made by different drops. If the Sun is higher than 42°, the bow lies entirely below the horizon."),
                fact = l("Aus einem Flugzeug kann man manchmal den ganzen Regenbogen als vollständigen Kreis sehen.", "From a plane you can sometimes see the whole rainbow as a complete circle."),
            )
        },
        lab("phy.pooltiefe", Topic.Optics, E, 0.4f) {
            val d = pick(1.5, 2.0, 3.0); val app = d / 1.33
            task(
                question = l("Ein Schwimmbecken ist ${n(d)} m tief. Von oben, senkrecht hineingeschaut, wirkt es flacher. Wie tief scheint es zu sein (Wasser: n = 1,33)?", "A swimming pool is ${n(d)} m deep. Looking straight down from above, it seems shallower. How deep does it appear to be (water: n = 1.33)?"),
                steps = listOf(num(l("Scheinbare Tiefe in Metern", "Apparent depth in metres"), app, "m", rel = 0.02, done = """§h' \approx ${m(app, 2)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Beim senkrechten Draufschauen wird die Tiefe einfach durch die Brechzahl geteilt.", "Key idea: looking straight down, the depth is simply divided by the refractive index."), """§h' = \dfrac{h}{n}§"""))),
                solution = listOf("""§h' = \dfrac{${m(d)}}{1{,}33}\ \text{m} \approx ${m(app, 2)}\ \text{m}§"""),
                background = l("Licht vom Beckenboden wird beim Austritt aus dem Wasser vom Lot weg gebrochen. Dein Gehirn verfolgt die Strahlen aber geradlinig zurück und verortet den Boden zu hoch. Deshalb unterschätzt man die Tiefe von klarem Wasser leicht, was beim Hineinspringen gefährlich werden kann.", "Light from the bottom of the pool is bent away from the normal as it leaves the water. But your brain traces the rays back in straight lines and places the bottom too high. That's why people easily underestimate the depth of clear water, which can be dangerous when jumping in."),
                fact = l("Speerfischer zielen deshalb unter den Fisch, den sie sehen. Schützenfische, die Insekten mit einem Wasserstrahl abschießen, gleichen die Brechung sogar automatisch aus.", "Spear fishers therefore aim below the fish they see. Archerfish, which shoot down insects with a jet of water, even compensate for the refraction automatically."),
            )
        },
        lab("phy.brille", Topic.Optics, M, 0.5f) {
            val far = pick(50.0, 25.0, 100.0); val dpt = -100 / far
            task(
                question = l("Eine kurzsichtige Person sieht ohne Brille nur bis ${n(far, 0)} cm scharf. Welche Brechkraft muss ihre Brille haben, damit sie in der Ferne scharf sieht?", "A short-sighted person sees clearly without glasses only up to ${n(far, 0)} cm. What power must her glasses have for her to see clearly in the distance?"),
                steps = listOf(num(l("Brechkraft in Dioptrien", "Power in dioptres"), dpt, "dpt", rel = 0.02, done = """§D = ${m(dpt, 1)}\ \text{dpt}§""",
                    hints = listOf(l("Kernidee: Die Brille muss parallele Strahlen aus der Ferne so auffächern, als kämen sie vom Fernpunkt. Dafür braucht es eine Zerstreuungslinse mit dieser Brennweite.", "Key idea: the glasses must spread out parallel rays from far away as if they came from the far point. That takes a diverging lens with this focal length."), """§D = \dfrac{1}{f},\quad f = -${m(far / 100)}\ \text{m}§"""))),
                solution = listOf("""§D = \dfrac{1}{-${m(far / 100)}\ \text{m}} = ${m(dpt, 1)}\ \text{dpt}§"""),
                background = l("Bei Kurzsichtigkeit ist der Augapfel etwas zu lang, das Bild entsteht vor der Netzhaut. Eine Zerstreuungslinse weitet die Strahlen vorher auf, sodass das Auge sie wieder genau auf die Netzhaut bündelt. Die Brechkraft in Dioptrien ist einfach der Kehrwert der Brennweite in Metern, negativ für Zerstreuungslinsen.", "In short-sightedness the eyeball is a little too long and the image forms in front of the retina. A diverging lens spreads the rays out beforehand so that the eye focuses them exactly on the retina again. The power in dioptres is simply the reciprocal of the focal length in metres, negative for diverging lenses."),
                fact = l("Kurzsichtigkeit nimmt weltweit stark zu. Studien deuten darauf hin, dass Kinder, die viel Zeit draußen im Tageslicht verbringen, seltener kurzsichtig werden.", "Short-sightedness is increasing rapidly worldwide. Studies suggest that children who spend a lot of time outdoors in daylight become short-sighted less often."),
            )
        },
        lab("phy.polfilter", Topic.Optics, M, 0.5f) {
            val ang = pick(60.0, 45.0, 30.0); val c = kotlin.math.cos(Math.toRadians(ang))
            val frac = 0.5 * c * c * 100
            task(
                question = l("Unpolarisiertes Licht fällt durch zwei Polarisationsfilter, deren Durchlassrichtungen ${n(ang, 0)}° gegeneinander verdreht sind. Wie viel Prozent der Helligkeit kommen hinten an?", "Unpolarised light passes through two polarising filters whose transmission directions are rotated ${n(ang, 0)}° relative to each other. What percentage of the brightness gets through?"),
                steps = listOf(num(l("Durchgelassener Anteil in Prozent", "Fraction transmitted in percent"), frac, "%", rel = 0.02, done = """§\approx ${m(frac, 1)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Der erste Filter halbiert unpolarisiertes Licht. Danach gilt für den zweiten das Gesetz von Malus.", "Key idea: the first filter halves unpolarised light. After that, Malus's law applies to the second."), """§I = \tfrac{1}{2} I_0 \cos^2\theta§"""))),
                solution = listOf("""§I = \tfrac{1}{2} \cdot \cos^2 ${m(ang, 0)}^\circ \cdot I_0 \approx ${m(frac / 100, 3)}\, I_0§"""),
                background = l("Licht ist eine Welle, die quer zur Laufrichtung schwingt. Ein Polfilter lässt nur eine Schwingungsrichtung durch. Steht der zweite Filter schräg, kommt nur der Anteil durch, der in seine Richtung zeigt, und die Intensität geht mit dem Quadrat davon. Bei 90° wird es fast ganz dunkel.", "Light is a wave that vibrates across its direction of travel. A polarising filter only lets one direction of vibration through. If the second filter is tilted, only the part pointing in its direction gets through, and the intensity goes with the square of that. At 90° it goes almost completely dark."),
                fact = l("Erstaunlich: Schiebt man zwischen zwei gekreuzte Filter einen dritten unter 45°, wird es wieder hell. Der mittlere Filter dreht das Licht gewissermaßen in zwei Schritten.", "Amazingly, if you slide a third filter at 45° between two crossed filters, light gets through again. The middle filter, in a sense, turns the light in two steps."),
            )
        },
        lab("phy.seifenblase", Topic.Optics, X, 0.6f) {
            val lambda = pick(530.0, 600.0, 450.0); val t = lambda / (4 * 1.33)
            task(
                question = l("Eine Seifenhaut (n = 1,33) schillert im reflektierten Licht ${if (lambda > 580) l("orange", "orange") else if (lambda < 500) l("blau", "blue") else l("grün", "green")} (${n(lambda, 0)} nm). Wie dick ist sie mindestens?", "A soap film (n = 1.33) shimmers ${if (lambda > 580) "orange" else if (lambda < 500) "blue" else "green"} in reflected light (${n(lambda, 0)} nm). What is its smallest possible thickness?"),
                steps = listOf(num(l("Dicke in Nanometern", "Thickness in nanometres"), t, "nm", rel = 0.02, done = """§d \approx ${m(t, 0)}\ \text{nm}§""",
                    hints = listOf(l("Kernidee: Licht wird an Vorder- und Rückseite gespiegelt. An der Vorderseite (Luft → Seife) kippt die Welle um, an der Rückseite nicht. Hell wird es, wenn der Umweg das wieder ausgleicht.", "Key idea: light is reflected at the front and back. At the front (air → soap) the wave flips over, at the back it doesn't. It gets bright when the detour makes up for that again."), """§2 n d = \dfrac{\lambda}{2} \Rightarrow d = \dfrac{\lambda}{4n}§"""))),
                solution = listOf("""§d = \dfrac{${m(lambda, 0)}\ \text{nm}}{4 \cdot 1{,}33} \approx ${m(t, 0)}\ \text{nm}§"""),
                background = l("Die Farben auf Seifenblasen und Ölfilmen sind keine Farbstoffe, sondern Interferenz. Die Haut ist so dünn wie eine Lichtwelle. Je nach Dicke verstärkt sich eine Farbe und löscht eine andere aus. Weil die Haut nach unten abfließt und überall anders dick ist, wandern die Farbbänder ständig.", "The colours on soap bubbles and oil films aren't dyes but interference. The film is about as thin as a wave of light. Depending on its thickness, one colour is reinforced and another cancelled. Because the film drains downwards and is a different thickness everywhere, the colour bands keep moving."),
                fact = l("Kurz bevor eine Blase platzt, wird sie oben schwarz: Die Haut ist dort dünner als ein Viertel jeder sichtbaren Wellenlänge, und alles Licht löscht sich aus.", "Just before a bubble bursts it turns black at the top: there the film is thinner than a quarter of any visible wavelength, and all light cancels out."),
            )
        },
        lab("phy.diamantlicht", Topic.Optics, E, 0.3f) {
            val (what, n) = pick(l("Diamant", "diamond") to 2.42, l("Glas", "glass") to 1.5, l("Wasser", "water") to 1.33)
            val v = 300000 / n
            task(
                question = l("Licht läuft im Vakuum mit 300.000 km/s. In $what ist es langsamer, die Brechzahl ist ${n(n)}. Wie schnell ist es dort?", "Light travels at 300,000 km/s in a vacuum. In $what it is slower; the refractive index is ${n(n)}. How fast is it there?"),
                steps = listOf(num(l("Geschwindigkeit in km/s", "Speed in km/s"), v, "km/s", rel = 0.02, done = """§v \approx ${m(sig(v))}\ \text{km/s}§""",
                    hints = listOf(l("Kernidee: Die Brechzahl sagt genau, wie viel mal langsamer das Licht im Stoff ist.", "Key idea: the refractive index tells you exactly how many times slower light is in the material."), """§v = \dfrac{c}{n}§"""))),
                solution = listOf("""§v = \dfrac{300\,000}{${m(n)}}\ \tfrac{\text{km}}{\text{s}} \approx ${m(sig(v))}\ \tfrac{\text{km}}{\text{s}}§"""),
                background = l("In einem Stoff wird Licht ständig von den Elektronen der Atome aufgenommen und wieder ausgesandt, mit einer winzigen Verzögerung. Insgesamt kommt es dadurch langsamer voran. Je stärker die Verzögerung, desto größer die Brechzahl, und desto stärker knickt Licht an der Oberfläche ab. Diamant bricht Licht besonders stark und funkelt deshalb so.", "In a material, light is constantly absorbed and re-emitted by the electrons of the atoms, with a tiny delay. Overall it therefore makes slower progress. The bigger the delay, the higher the refractive index, and the more strongly light bends at the surface. Diamond bends light especially strongly, which is why it sparkles so much."),
                fact = l("2001 gelang es Forschern, Licht in einem extrem kalten Gas ganz anzuhalten und kurz zu speichern.", "In 2001 researchers managed to bring light to a complete stop in an extremely cold gas and store it briefly."),
            )
        },
        lab("phy.blende", Topic.Optics, M, 0.5f) {
            val k1 = pick(4.0, 2.8, 5.6); val k2 = k1 * 2; val t1 = pick(500.0, 250.0, 1000.0)
            val tms = 1000 / t1 * 4
            task(
                question = l("Ein Foto ist bei Blende ${n(k1, 1)} und 1/${n(t1, 0)} s richtig belichtet. Für mehr Schärfentiefe stellst du auf Blende ${n(k2, 1)}. Wie lange musst du jetzt belichten?", "A photo is correctly exposed at f/${n(k1, 1)} and 1/${n(t1, 0)} s. For more depth of field you switch to f/${n(k2, 1)}. How long do you have to expose now?"),
                steps = listOf(num(l("Belichtungszeit in Millisekunden", "Exposure time in milliseconds"), tms, "ms", rel = 0.02, done = """§t = ${m(tms, 0)}\ \text{ms}§""",
                    hints = listOf(l("Kernidee: Die Blendenzahl ist Brennweite durch Öffnungsdurchmesser. Doppelte Blendenzahl heißt halber Durchmesser, also ein Viertel der Fläche.", "Key idea: the f-number is focal length over aperture diameter. Double the f-number means half the diameter, so a quarter of the area."), """§t_2 = t_1 \left(\dfrac{k_2}{k_1}\right)^2§"""))),
                solution = listOf("""§t_2 = \dfrac{1}{${m(t1, 0)}}\ \text{s} \cdot 2^2 = ${m(tms, 0)}\ \text{ms}§"""),
                background = l("Die Lichtmenge auf dem Sensor ist Fläche der Öffnung mal Zeit. Die Fläche geht mit dem Quadrat des Durchmessers. Deshalb ist die Blendenreihe so seltsam: 1,4 – 2 – 2,8 – 4 – 5,6 … Jede Stufe ist ein Faktor √2, also halbe Lichtmenge. Kleine Öffnungen machen den Schärfebereich tiefer, brauchen aber mehr Zeit.", "The amount of light on the sensor is the area of the opening times the time. The area goes with the square of the diameter. That's why the f-number series is so odd: 1.4 – 2 – 2.8 – 4 – 5.6 … Each step is a factor of √2, so half the light. Small apertures give a deeper zone of sharpness but need more time."),
                fact = l("Auch dein Auge hat eine Blende: Die Pupille wechselt zwischen etwa 2 und 8 mm Durchmesser, ein Faktor 16 in der Lichtmenge.", "Your eye has an aperture too: the pupil changes between about 2 and 8 mm in diameter, a factor of 16 in the amount of light."),
            )
        },
        lab("phy.teleskop", Topic.Optics, E, 0.4f) {
            val fo = pick(900.0, 1000.0, 1200.0); val fe = pick(10.0, 20.0, 25.0); val v = fo / fe
            task(
                question = l("Ein Teleskop hat ein Objektiv mit ${n(fo, 0)} mm Brennweite. Du steckst ein Okular mit ${n(fe, 0)} mm Brennweite hinein. Wie stark vergrößert es?", "A telescope has an objective with a focal length of ${n(fo, 0)} mm. You insert an eyepiece with a focal length of ${n(fe, 0)} mm. What is the magnification?"),
                steps = listOf(num(l("Vergrößerung", "Magnification"), v, "", rel = 0.01, done = """§V = ${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: Das Objektiv macht ein Zwischenbild, das Okular betrachtet es wie eine Lupe. Das Verhältnis der Brennweiten ergibt die Vergrößerung.", "Key idea: the objective forms an intermediate image, and the eyepiece views it like a magnifying glass. The ratio of the focal lengths gives the magnification."), """§V = \dfrac{f_{Obj}}{f_{Ok}}§"""))),
                solution = listOf("""§V = \dfrac{${m(fo, 0)}}{${m(fe, 0)}} = ${m(v, 0)}§"""),
                background = l("Ein Teleskop vergrößert Winkel: Zwei Sterne, die nah beieinander scheinen, rücken im Okular weiter auseinander. Wichtiger als die Vergrößerung ist aber die Öffnung des Objektivs. Sie bestimmt, wie viel Licht gesammelt wird und wie feine Details überhaupt zu trennen sind. Zu viel Vergrößerung macht das Bild nur größer und unscharf.", "A telescope magnifies angles: two stars that appear close together move farther apart in the eyepiece. But more important than the magnification is the aperture of the objective. It decides how much light is collected and how fine the details are that can be separated at all. Too much magnification just makes the image bigger and blurrier."),
                fact = l("Galilei baute 1609 ein Fernrohr mit etwa 20-facher Vergrößerung und entdeckte damit die vier großen Jupitermonde.", "In 1609 Galileo built a telescope with about 20× magnification and discovered the four large moons of Jupiter with it."),
            )
        },
        lab("phy.lochkamera", Topic.Optics, M, 0.5f) {
            val dist = pick(1.0, 2.0, 3.0); val img = dist * 1.39e6 / 1.496e8 * 100
            task(
                question = l("Durch ein kleines Loch im Fensterladen fällt Sonnenlicht auf eine Wand, ${n(dist, 0)} m dahinter. Dort erscheint ein rundes Bild der Sonne (Durchmesser 1,39 Mio. km, Entfernung 149,6 Mio. km). Wie groß ist es?", "Sunlight falls through a small hole in a shutter onto a wall ${n(dist, 0)} m behind it. A round image of the Sun appears there (diameter 1.39 million km, distance 149.6 million km). How big is it?"),
                steps = listOf(num(l("Bilddurchmesser in cm", "Image diameter in cm"), img, "cm", rel = 0.02, done = """§d \approx ${m(img, 2)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Strahlensatz. Die Lichtstrahlen vom oberen und unteren Sonnenrand kreuzen sich im Loch.", "Key idea: similar triangles. The rays from the top and bottom edges of the Sun cross in the hole."), """§\dfrac{d}{s} = \dfrac{D}{E}§"""))),
                solution = listOf("""§d = ${m(dist, 0)}\ \text{m} \cdot \dfrac{1{,}39 \cdot 10^6}{1{,}496 \cdot 10^8} \approx ${m(img, 2)}\ \text{cm}§"""),
                background = l("Eine Lochkamera braucht keine Linse: Durch ein kleines Loch kommt von jedem Punkt draußen nur ein dünnes Strahlenbündel, und auf der Wand entsteht ein umgedrehtes Bild. Mit der Sonne kann man so gefahrlos Sonnenflecken oder eine Finsternis beobachten. Die Sonne erscheint unter einem Winkel von etwa einem halben Grad, wie der Mond.", "A pinhole camera needs no lens: only a thin bundle of rays from each point outside passes through a small hole, and an upside-down image forms on the wall. With the Sun you can safely watch sunspots or an eclipse this way. The Sun appears at an angle of about half a degree, just like the Moon."),
                fact = l("Die runden Lichtflecken im Schatten von Bäumen sind lauter kleine Sonnenbilder: Die Lücken zwischen den Blättern wirken als Lochkameras. Bei einer partiellen Finsternis werden sie zu Sicheln.", "The round spots of light in the shade of trees are lots of little images of the Sun: the gaps between the leaves act as pinhole cameras. During a partial eclipse they turn into crescents."),
            )
        },
        lab("phy.retroreflektor", Topic.Optics, M, 0.6f) {
            task(
                question = l("Auf dem Mond stehen seit den Apollo-Missionen Reflektoren, die aus vielen Würfelecken bestehen: drei Spiegel, die wie die Innenecke eines Würfels zusammenstehen. Warum gerade diese Form?", "Since the Apollo missions there have been reflectors on the Moon made of many corner cubes: three mirrors meeting like the inside corner of a cube. Why this particular shape?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Eine Würfelecke wirft Licht immer genau in die Richtung zurück, aus der es kommt, egal unter welchem Winkel es eintrifft.", "A corner cube always sends light back exactly in the direction it came from, whatever angle it arrives at."),
                    listOf(l("Sie bündelt das Licht wie eine Linse zu einem starken Strahl.", "It focuses the light like a lens into a strong beam."), l("Sie verstärkt das Licht durch dreifache Spiegelung.", "It amplifies the light by reflecting it three times."), l("Sie ist leichter als ein großer flacher Spiegel.", "It is lighter than one large flat mirror.")),
                    hints = listOf(l("Kernidee: Jede Spiegelung kehrt eine Richtungskomponente des Strahls um.", "Key idea: each reflection reverses one component of the ray's direction."), l("Nach drei Spiegeln an drei senkrechten Wänden sind alle drei Komponenten umgekehrt.", "After three mirrors on three perpendicular walls, all three components are reversed.")))),
                solution = listOf(l("Jeder der drei Spiegel kehrt eine Komponente (x, y oder z) um. Danach läuft der Strahl genau entgegengesetzt zurück, parallel zum einfallenden. Ein flacher Spiegel müsste dafür exakt ausgerichtet sein.", "Each of the three mirrors reverses one component (x, y or z). Afterwards the ray travels back exactly opposite, parallel to the incoming one. A flat mirror would have to be aimed exactly to do that.")),
                background = l("Mit Laserpulsen, die zu diesen Reflektoren und zurück laufen, misst man die Entfernung zum Mond auf Millimeter genau. Von einer Billiarde ausgesandter Photonen kommt nur etwa eins zurück. Dass der Spiegel nicht ausgerichtet sein muss, war entscheidend, denn auf dem Mond kann ihn niemand nachjustieren.", "Laser pulses sent to these reflectors and back measure the distance to the Moon to within millimetres. Of a quadrillion photons sent out, only about one comes back. That the mirror doesn't need to be aimed was crucial, since nobody on the Moon can adjust it."),
                fact = l("Fahrradreflektoren und Katzenaugen an Straßenpfosten nutzen dasselbe Prinzip, deshalb leuchten sie im Scheinwerferlicht so hell.", "Bicycle reflectors and cat's eyes on road posts use the same principle, which is why they shine so brightly in headlights."),
            )
        },
        lab("phy.brennglas", Topic.Optics, X, 0.7f) {
            val dLens = pick(10.0, 5.0, 8.0); val f = pick(20.0, 30.0); val sunAng = 0.0093
            val spot = f * sunAng * 10; val gain = (dLens * 10 / spot) * (dLens * 10 / spot)
            task(
                question = l("Eine Lupe mit ${n(dLens, 0)} cm Durchmesser und ${n(f, 0)} cm Brennweite bündelt Sonnenlicht. Die Sonne erscheint unter einem Winkel von 0,0093 rad, deshalb wird der Brennfleck kein Punkt, sondern eine kleine Scheibe. Um welchen Faktor ist das Licht dort heller als normales Sonnenlicht?", "A magnifying glass ${n(dLens, 0)} cm in diameter with a focal length of ${n(f, 0)} cm focuses sunlight. The Sun appears at an angle of 0.0093 rad, so the focal spot isn't a point but a small disc. By what factor is the light there brighter than normal sunlight?"),
                steps = listOf(num(l("Verstärkungsfaktor", "Concentration factor"), gain, "", rel = 0.03, done = """§k \approx ${m(sig(gain))}§""",
                    hints = listOf(l("Kernidee: Der Fleck ist ein Bild der Sonne, sein Durchmesser ist Brennweite mal Sonnenwinkel. Das Licht der ganzen Linsenfläche landet auf dieser Scheibe.", "Key idea: the spot is an image of the Sun; its diameter is focal length times the Sun's angle. The light of the whole lens area lands on this disc."), """§d = f \cdot \theta,\quad k = \left(\dfrac{D_{Linse}}{d}\right)^2§""".let { l(it, it.replace("Linse", "lens")) }))),
                solution = listOf("""§d = ${m(f * 10, 0)}\ \text{mm} \cdot 0{,}0093 \approx ${m(spot, 2)}\ \text{mm}§""", """§k = \left(\dfrac{${m(dLens * 10, 0)}}{${m(spot, 2)}}\right)^2 \approx ${m(sig(gain))}§"""),
                background = l("Keine Linse kann die Sonne auf einen Punkt bündeln, weil die Sonne selbst kein Punkt ist. Das ist ein tiefer Grund der Thermodynamik: Ein Brennglas kann einen Gegenstand niemals heißer machen als die Oberfläche der Sonne selbst. Trotzdem reicht die Konzentration locker, um Papier und Holz zu entzünden.", "No lens can focus the Sun to a point, because the Sun itself isn't a point. This has a deep thermodynamic reason: a burning glass can never make an object hotter than the surface of the Sun itself. Even so, the concentration is easily enough to set paper and wood alight."),
                fact = l("Solarkraftwerke bündeln Sonnenlicht mit tausenden Spiegeln auf einen Turm und erreichen dort über 1000 °C.", "Solar power stations concentrate sunlight with thousands of mirrors onto a tower and reach over 1000 °C there."),
            )
        },
        lab("phy.fatamorgana", Topic.Optics, M, 0.6f) {
            task(
                question = l("An einem heißen Sommertag scheint eine Straße in der Ferne nass zu sein, als lägen Pfützen darauf. Kommt man näher, ist sie trocken. Was sieht man da wirklich?", "On a hot summer day a road in the distance seems to be wet, as if there were puddles on it. When you get closer it's dry. What are you really seeing?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Ein Spiegelbild des Himmels: Licht wird in der heißen, dünnen Luft dicht über dem Asphalt nach oben gebogen.", "A reflection of the sky: light is bent upwards in the hot, thin air just above the asphalt."),
                    listOf(l("Wasserdampf, der aus dem Asphalt aufsteigt.", "Water vapour rising from the asphalt."), l("Eine optische Täuschung im Gehirn, physikalisch ist da nichts.", "An optical illusion in the brain; physically there's nothing there."), l("Die Sonne spiegelt sich im glatten Asphalt.", "The Sun reflecting off the smooth asphalt.")),
                    hints = listOf(l("Kernidee: Heiße Luft ist dünner und hat eine etwas kleinere Brechzahl als kühle Luft.", "Key idea: hot air is thinner and has a slightly smaller refractive index than cool air."), l("Licht krümmt sich immer zur Seite mit der größeren Brechzahl hin.", "Light always curves towards the side with the larger refractive index.")))),
                solution = listOf(l("Licht vom Himmel läuft flach nach unten, wird in der heißen Luftschicht über der Straße allmählich nach oben gekrümmt und erreicht dein Auge von unten. Du siehst blauen Himmel auf der Straße, und das deutet das Gehirn als Wasser.", "Light from the sky travels down at a shallow angle, is gradually curved upwards in the hot layer of air over the road and reaches your eye from below. You see blue sky on the road, and your brain interprets it as water.")),
                background = l("Luftspiegelungen entstehen, wenn die Temperatur der Luft sich mit der Höhe stark ändert. Licht läuft dann nicht gerade, sondern auf gekrümmten Bahnen. In Wüsten kann man so Oasen sehen, die eigentlich hinter dem Horizont liegen, und über kaltem Meer scheinen Schiffe manchmal in der Luft zu schweben.", "Mirages form when the air temperature changes strongly with height. Light then doesn't travel straight but along curved paths. In deserts you can see oases that are actually beyond the horizon, and over cold seas ships sometimes seem to float in the air."),
                fact = l("Die Spiegelung ist echt, man kann sie fotografieren. Nur die Deutung als Wasser macht unser Gehirn.", "The reflection is real; you can photograph it. Only the interpretation as water is made by our brain."),
            )
        },
        lab("phy.bragg", Topic.Optics, X, 0.6f) {
            val lambda = pick(0.154, 0.071); val d = 0.282
            val ang = Math.toDegrees(asin(lambda / (2 * d)))
            task(
                question = l("Röntgenlicht mit ${n(lambda, 3)} nm Wellenlänge trifft auf einen Kochsalzkristall. Die Atomebenen liegen 0,282 nm auseinander. Unter welchem Winkel zu den Ebenen gibt es die erste starke Reflexion?", "X-rays with a wavelength of ${n(lambda, 3)} nm hit a crystal of table salt. The atomic planes are 0.282 nm apart. At what angle to the planes is there the first strong reflection?"),
                steps = listOf(num(l("Glanzwinkel in Grad", "Glancing angle in degrees"), ang, "°", abs = 0.3, done = """§\theta \approx ${m(ang, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Die Wellen, die an benachbarten Ebenen reflektiert werden, müssen sich verstärken. Ihr Umweg muss eine ganze Wellenlänge sein.", "Key idea: the waves reflected from neighbouring planes must reinforce each other. Their detour must be a whole wavelength."), """§2 d \sin\theta = \lambda§"""))),
                solution = listOf("""§\sin\theta = \dfrac{${m(lambda, 3)}}{2 \cdot 0{,}282} \approx ${m(lambda / 0.564, 3)} \Rightarrow \theta \approx ${m(ang, 1)}^\circ§"""),
                background = l("Atome in einem Kristall bilden ein regelmäßiges Gitter, und ihr Abstand ist so groß wie die Wellenlänge von Röntgenstrahlen. Deshalb wirkt ein Kristall für Röntgenlicht wie ein Beugungsgitter. Vater und Sohn Bragg nutzten das 1913, um aus den Winkeln der Reflexe den Aufbau von Kristallen zu entschlüsseln.", "Atoms in a crystal form a regular lattice, and their spacing is about the same as the wavelength of X-rays. That's why a crystal acts like a diffraction grating for X-rays. In 1913 William and Lawrence Bragg, father and son, used this to work out the structure of crystals from the angles of the reflections."),
                fact = l("Mit genau dieser Methode entstand 1952 das berühmte Foto 51 von Rosalind Franklin, das die Doppelhelix der DNA verriet.", "Exactly this method produced Rosalind Franklin's famous Photo 51 in 1952, which revealed the double helix of DNA."),
            )
        },
        lab("phy.atlantikkabel", Topic.Optics, H, 0.5f) {
            val len = pick(6000.0, 5500.0, 6600.0); val nGlass = 1.47
            val tFiber = len * 1000 * nGlass / C4 * 1000; val tSat = 2 * 35786e3 / C4 * 1000
            task(
                question = l("Daten zwischen Europa und Amerika laufen entweder durch ein Glasfaserkabel am Meeresgrund (${n(len, 0)} km, Brechzahl 1,47) oder über einen geostationären Satelliten in 35.786 km Höhe.", "Data between Europe and America travels either through an optical fibre on the sea floor (${n(len, 0)} km, refractive index 1.47) or via a geostationary satellite at an altitude of 35,786 km."),
                steps = listOf(
                    num(l("Wie lange braucht ein Signal durch das Kabel (in ms)?", "How long does a signal take through the cable (in ms)?"), tFiber, "ms", rel = 0.02, done = """§t \approx ${m(tFiber, 1)}\ \text{ms}§""",
                        hints = listOf(l("Kernidee: In Glas läuft Licht um den Faktor n langsamer.", "Key idea: in glass light travels slower by the factor n."), """§t = \dfrac{L \cdot n}{c}§""")),
                    num(l("Und über den Satelliten, mindestens einmal hoch und wieder runter (in ms)?", "And via the satellite, at least once up and back down (in ms)?"), tSat, "ms", rel = 0.03, done = """§t \approx ${m(tSat, 0)}\ \text{ms}§""",
                        hints = listOf(l("Im leeren Raum läuft das Signal mit voller Lichtgeschwindigkeit, aber der Weg ist mindestens zweimal 35.786 km.", "In empty space the signal travels at full light speed, but the path is at least twice 35,786 km."), """§t = \dfrac{2 \cdot 35\,786\ \text{km}}{c}§""")),
                ),
                solution = listOf("""§t_{Kabel} = \dfrac{${m(len * 1000, 0)} \cdot 1{,}47}{3 \cdot 10^8}\ \text{s} \approx ${m(tFiber, 1)}\ \text{ms}§""".let { l(it, it.replace("Kabel", "cable")) }, """§t_{Sat} = \dfrac{7{,}16 \cdot 10^7}{3 \cdot 10^8}\ \text{s} \approx ${m(tSat, 0)}\ \text{ms}§"""),
                background = l("Licht ist schnell, aber auf großen Entfernungen merkt man seine endliche Geschwindigkeit. Geostationäre Satelliten hängen so hoch, dass jedes Gespräch eine spürbare Pause bekommt. Glasfasern sind trotz des langsameren Lichts im Glas viel schneller, weil der Weg kürzer ist. Für Börsenhändler zählt jede Millisekunde: Für sie wurden eigene, möglichst gerade Kabel verlegt.", "Light is fast, but over long distances you notice that its speed is finite. Geostationary satellites hang so high that every conversation gets a noticeable pause. Optical fibres are much faster despite the slower light in glass, because the path is shorter. For stock traders every millisecond counts: dedicated cables were laid for them along routes as straight as possible."),
                fact = l("Satelliten in niedriger Umlaufbahn (etwa 550 km) haben dieses Problem kaum, deshalb setzen neue Internet-Satelliten auf tausende Satelliten in geringer Höhe.", "Satellites in low orbit (about 550 km) hardly have this problem, which is why new internet satellite systems rely on thousands of satellites at low altitude."),
            )
        },
    )
}
