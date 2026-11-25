package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.math.tan

/* Physik-Ausbau (seit 13.2): Energie, Impuls, Drehung. Alle Texte selbst geschrieben. */

private const val G2 = 9.81
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object PhysNew2 {
    val all: List<TaskDef> get() = energy + momentum + rotation

    private val energy = listOf(
        lab("phy.bungee", Topic.Energy, H, 0.6f) {
            val mass = pick(60.0, 70.0, 80.0); val L = 20.0; val k = 150.0
            // m g (L + x) = 1/2 k x^2
            val a = k / 2; val b = -mass * G2; val c = -mass * G2 * L
            val x = (-b + sqrt(b * b - 4 * a * c)) / (2 * a); val total = L + x
            task(
                question = l("Beim Bungeesprung springt eine Person (${n(mass, 0)} kg) von einer Brücke. Das Seil ist ungedehnt 20 m lang und verhält sich wie eine Feder mit D = 150 N/m. Wie tief unter der Absprungstelle ist der tiefste Punkt?", "A person (${n(mass, 0)} kg) does a bungee jump from a bridge. The rope is 20 m long when unstretched and behaves like a spring with k = 150 N/m. How far below the jump-off point is the lowest point?"),
                steps = listOf(num(l("Tiefster Punkt unter der Brücke", "Lowest point below the bridge"), total, "m", rel = 0.02, done = """§h \approx ${m(sig(total))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Am tiefsten Punkt ist die Person kurz in Ruhe. Die ganze verlorene Lageenergie steckt dann im gedehnten Seil.", "Key idea: at the lowest point the person is momentarily at rest. All the lost potential energy is then stored in the stretched rope."), """§m g (L + x) = \tfrac{1}{2} D x^2§"""))),
                solution = listOf("""§${m(mass * G2, 1)}\,(20 + x) = 75\, x^2 \Rightarrow x \approx ${m(x, 1)}\ \text{m}§""", """§h = 20\ \text{m} + ${m(x, 1)}\ \text{m} \approx ${m(sig(total))}\ \text{m}§"""),
                background = l("Energieerhaltung erspart hier die ganze Bewegung: Oben und unten ist die Person in Ruhe, also muss die Lageenergie, die sie auf dem ganzen Weg verliert, vollständig in die Spannenergie des Seils gegangen sein. Weil diese mit dem Quadrat der Dehnung wächst, entsteht eine quadratische Gleichung.", "Energy conservation saves you working out the whole motion: at the top and at the bottom the person is at rest, so the potential energy lost over the whole drop must have gone entirely into the elastic energy of the rope. Because that grows with the square of the stretch, you get a quadratic equation."),
                fact = l("Bungeeseile werden für jede Person passend nach Gewicht gewählt. Schwerere Springer bekommen mehr Gummistränge, damit der tiefste Punkt gleich bleibt.", "Bungee ropes are chosen to suit each jumper's weight. Heavier jumpers get more rubber strands so the lowest point stays the same."),
            )
        },
        lab("phy.bogen", Topic.Energy, M, 0.5f) {
            val f = pick(250.0, 300.0, 350.0); val d = 0.6; val mArrow = 0.025; val eta = 0.75
            val e = 0.5 * f * d; val v = sqrt(2 * eta * e / mArrow)
            task(
                question = l("Ein Bogen wird 60 cm weit ausgezogen. Die Zugkraft wächst dabei gleichmäßig von 0 auf ${n(f, 0)} N. Der Pfeil wiegt 25 g, und 75 % der gespeicherten Energie gehen in den Pfeil.", "A bow is drawn back 60 cm. The pulling force grows evenly from 0 to ${n(f, 0)} N. The arrow has a mass of 25 g, and 75% of the stored energy goes into the arrow."),
                steps = listOf(
                    num(l("Wie viel Energie steckt im gespannten Bogen?", "How much energy is stored in the drawn bow?"), e, "J", rel = 0.02, done = """§E = ${m(e, 0)}\ \text{J}§""",
                        hints = listOf(l("Kernidee: Die Kraft steigt gleichmäßig an, also zählt im Mittel nur die halbe Endkraft.", "Key idea: the force rises evenly, so on average only half the final force counts."), """§E = \tfrac{1}{2} F s§""")),
                    num(l("Wie schnell fliegt der Pfeil los?", "How fast does the arrow leave the bow?"), v, "m/s", rel = 0.02, done = """§v \approx ${m(v, 0)}\ \text{m/s}§""",
                        hints = listOf(l("75 % der Energie werden Bewegungsenergie des Pfeils.", "75% of the energy becomes the arrow's kinetic energy."), """§\tfrac{1}{2} m v^2 = 0{,}75\, E§""")),
                ),
                solution = listOf("""§E = \tfrac{1}{2} \cdot ${m(f, 0)}\ \text{N} \cdot 0{,}6\ \text{m} = ${m(e, 0)}\ \text{J}§""", """§v = \sqrt{\dfrac{2 \cdot 0{,}75 \cdot ${m(e, 0)}}{0{,}025}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 0)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = l("Ein Bogen ist eine Feder: Beim Ausziehen speichert er Arbeit, die beim Loslassen in kurzer Zeit an den Pfeil geht. Arbeit ist Kraft mal Weg, und wenn die Kraft dabei gleichmäßig wächst, ist es die Fläche unter einer Geraden, also ein Dreieck. Ein Teil der Energie bleibt in den schwingenden Wurfarmen und der Sehne.", "A bow is a spring: when drawn it stores work, which on release goes into the arrow in a very short time. Work is force times distance, and if the force grows evenly, it's the area under a straight line, i.e. a triangle. Part of the energy stays in the vibrating limbs and the string."),
                fact = l("Moderne Compoundbögen haben Rollen an den Enden. Die Kraft ist beim vollen Auszug am kleinsten, so kann man lange zielen, und trotzdem steckt mehr Energie darin.", "Modern compound bows have pulleys at the ends. The force is smallest at full draw, so you can aim for a long time, and yet more energy is stored."),
            )
        },
        lab("phy.schokolade", Topic.Energy, M, 0.5f) {
            val kcal = 540.0; val mass = pick(50.0, 60.0, 70.0); val eta = 0.25
            val ej = kcal * 4184; val h = eta * ej / (mass * G2); val floors = h / 3
            task(
                question = l("Eine Tafel Schokolade (100 g) hat etwa 540 kcal. Deine Muskeln machen etwa 25 % davon zu mechanischer Arbeit. Wie hoch könntest du (${n(mass, 0)} kg) damit Treppen steigen?", "A bar of chocolate (100 g) has about 540 kcal. Your muscles turn about 25% of that into mechanical work. How high could you (${n(mass, 0)} kg) climb stairs with it?"),
                steps = listOf(num(l("Höhe in Metern", "Height in metres"), h, "m", rel = 0.03, done = """§h \approx ${m(sig(h))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Erst kcal in Joule umrechnen (1 kcal = 4184 J), dann das nutzbare Viertel in Lageenergie stecken.", "Key idea: first convert kcal into joules (1 kcal = 4184 J), then put the usable quarter into potential energy."), """§0{,}25 \cdot E = m g h§"""))),
                solution = listOf("""§E = 540 \cdot 4184\ \text{J} \approx ${sci(ej)}\ \text{J}§""", """§h = \dfrac{0{,}25 \cdot ${sci(ej)}}{${m(mass, 0)} \cdot 9{,}81}\ \text{m} \approx ${m(sig(h))}\ \text{m}§"""),
                background = l("Lebensmittel sind erstaunlich energiereich. Die Kilokalorie ist eine alte Wärmeeinheit: die Energie, die 1 kg Wasser um 1 °C erwärmt. Muskeln sind wie Motoren mit mäßigem Wirkungsgrad. Drei Viertel der Energie werden Wärme, deshalb wird dir beim Treppensteigen warm.", "Food is surprisingly rich in energy. The kilocalorie is an old unit of heat: the energy that warms 1 kg of water by 1 °C. Muscles are like engines with a modest efficiency. Three quarters of the energy turn into heat, which is why you get warm climbing stairs."),
                fact = l("Das wären rund ${n(floors, 0)} Stockwerke. Deshalb ist es so schwer, Schokolade allein mit Treppensteigen wieder loszuwerden.", "That would be about ${n(floors, 0)} floors. That's why it's so hard to burn off chocolate by climbing stairs alone."),
            )
        },
        lab("phy.windrad", Topic.Energy, H, 0.6f) {
            val dRotor = pick(100.0, 120.0, 150.0); val v = pick(8.0, 10.0, 12.0); val cp = 0.45
            val area = PI * dRotor * dRotor / 4; val p = 0.5 * 1.2 * area * v * v * v * cp
            task(
                question = l("Eine Windkraftanlage hat einen Rotordurchmesser von ${n(dRotor, 0)} m. Der Wind weht mit ${n(v, 0)} m/s, die Luftdichte ist 1,2 kg/m³. Die Anlage nutzt 45 % der Leistung des Windes.", "A wind turbine has a rotor diameter of ${n(dRotor, 0)} m. The wind blows at ${n(v, 0)} m/s, the air density is 1.2 kg/m³. The turbine uses 45% of the wind's power."),
                steps = listOf(
                    num(l("Welche elektrische Leistung liefert sie (in MW)?", "What electrical power does it deliver (in MW)?"), p / 1e6, "MW", rel = 0.03, done = """§P \approx ${m(sig(p / 1e6))}\ \text{MW}§""",
                        hints = listOf(l("Kernidee: Pro Sekunde strömt ein Luftzylinder mit Fläche A und Länge v durch den Rotor. Seine Bewegungsenergie ist die Leistung des Windes.", "Key idea: every second a cylinder of air with area A and length v flows through the rotor. Its kinetic energy is the wind's power."), """§P = c_p \cdot \tfrac{1}{2} \rho A v^3§""")),
                    num(l("Auf welchen Bruchteil sinkt die Leistung, wenn der Wind nur halb so stark weht?", "To what fraction does the power drop if the wind blows only half as strongly?"), 0.125, "", rel = 0.01, done = """§\left(\tfrac{1}{2}\right)^3 = \tfrac{1}{8}§""",
                        hints = listOf(l("Die Geschwindigkeit steckt in der dritten Potenz.", "The speed appears to the third power."), """§P \propto v^3§""")),
                ),
                solution = listOf("""§A = \pi \cdot \left(\dfrac{${m(dRotor, 0)}}{2}\right)^2\ \text{m}^2 \approx ${m(sig(area))}\ \text{m}^2§""", """§P = 0{,}45 \cdot \tfrac{1}{2} \cdot 1{,}2 \cdot ${m(sig(area))} \cdot ${m(v, 0)}^3\ \text{W} \approx ${m(sig(p / 1e6))}\ \text{MW}§""", """§\left(\tfrac{1}{2}\right)^3 = 0{,}125§"""),
                background = l("Die Windleistung wächst mit der dritten Potenz der Geschwindigkeit: Doppelt so schneller Wind bringt doppelt so viel Luft pro Sekunde, und jedes Kilogramm hat viermal so viel Bewegungsenergie. Deshalb baut man Windräder hoch, wo der Wind stärker weht, und deshalb schwankt ihre Leistung so stark. Mehr als 59 % kann kein Windrad entnehmen (Betz-Grenze), sonst würde die Luft dahinter stehen bleiben.", "Wind power grows with the cube of the speed: wind twice as fast brings twice as much air per second, and each kilogram carries four times the kinetic energy. That's why turbines are built tall, where the wind is stronger, and why their output varies so much. No turbine can extract more than 59% (the Betz limit), otherwise the air behind it would come to a standstill."),
                fact = l("Ein einziges großes Windrad kann bei gutem Wind mehrere Tausend Haushalte versorgen.", "A single large wind turbine can supply several thousand households when the wind is good."),
            )
        },
        lab("phy.solardach", Topic.Energy, M, 0.4f) {
            val use = pick(2500.0, 3500.0, 4500.0); val sun = 1000.0; val eta = 0.20
            val area = use / (sun * eta)
            task(
                question = l("In Deutschland kommen im Jahr etwa 1000 kWh Sonnenenergie auf jeden Quadratmeter. Solarmodule machen 20 % davon zu Strom. Wie viel Dachfläche braucht ein Haushalt, der ${n(use, 0)} kWh im Jahr verbraucht?", "In Germany about 1000 kWh of solar energy falls on each square metre per year. Solar panels turn 20% of it into electricity. How much roof area does a household need that uses ${n(use, 0)} kWh a year?"),
                steps = listOf(num(l("Fläche in Quadratmetern", "Area in square metres"), area, "m²", rel = 0.02, done = """§A \approx ${m(sig(area))}\ \text{m}^2§""",
                    hints = listOf(l("Kernidee: Ein Quadratmeter Modul liefert 20 % von 1000 kWh im Jahr.", "Key idea: one square metre of panel delivers 20% of 1000 kWh a year."), """§A = \dfrac{W}{0{,}2 \cdot 1000\ \text{kWh/m}^2}§"""))),
                solution = listOf("""§1\ \text{m}^2 \to 0{,}2 \cdot 1000\ \text{kWh} = 200\ \text{kWh}§""", """§A = \dfrac{${m(use, 0)}}{200}\ \text{m}^2 \approx ${m(sig(area))}\ \text{m}^2§"""),
                background = l("Im Jahresmittel kommt in Deutschland nur ein Bruchteil der Mittagsleistung der Sonne an: Nachts gibt es nichts, im Winter wenig, und Wolken schlucken viel. Auf einem Quadratmeter sind es übers Jahr etwa 1000 kWh, in der Sahara gut das Doppelte. Mit 20 % Wirkungsgrad reicht trotzdem ein normales Einfamilienhausdach für den Strom eines Haushalts, nur nicht zur richtigen Uhrzeit.", "On a yearly average only a fraction of the Sun's midday power arrives in Germany: nothing at night, little in winter, and clouds swallow a lot. On one square metre it adds up to about 1000 kWh over a year, in the Sahara well over double. With 20% efficiency an ordinary family house roof is still enough for a household's electricity, just not at the right time of day."),
                fact = l("Die Sonne liefert der Erde in einer Stunde etwa so viel Energie, wie die ganze Menschheit in einem Jahr verbraucht.", "The Sun delivers about as much energy to the Earth in one hour as all of humanity uses in a year."),
            )
        },
        lab("phy.klimmzug", Topic.Energy, E, 0.3f) {
            val mass = pick(50.0, 60.0, 70.0); val h = 0.5; val t = pick(1.0, 1.5)
            val p = mass * G2 * h / t
            task(
                question = l("Bei einem Klimmzug ziehst du dich (${n(mass, 0)} kg) in ${n(t)} s um 50 cm nach oben. Welche Leistung bringst du dabei?", "In a pull-up you pull yourself (${n(mass, 0)} kg) up by 50 cm in ${n(t)} s. What power do you produce?"),
                steps = listOf(num(l("Leistung in Watt", "Power in watts"), p, "W", rel = 0.02, done = """§P \approx ${m(sig(p))}\ \text{W}§""",
                    hints = listOf(l("Kernidee: Leistung ist Arbeit pro Zeit. Die Arbeit ist hier die gewonnene Lageenergie.", "Key idea: power is work per time. The work here is the potential energy gained."), """§P = \dfrac{m g h}{t}§"""))),
                solution = listOf("""§P = \dfrac{${m(mass, 0)} \cdot 9{,}81 \cdot 0{,}5}{${m(t)}}\ \text{W} \approx ${m(sig(p))}\ \text{W}§"""),
                background = l("Arbeit ist Kraft mal Weg, Leistung sagt, wie schnell die Arbeit verrichtet wird. Langsame und schnelle Klimmzüge kosten dieselbe Arbeit, aber die schnellen brauchen mehr Leistung. Ein trainierter Mensch schafft kurzzeitig über 1000 W, auf Dauer etwa 100 bis 200 W.", "Work is force times distance; power tells you how quickly the work is done. Slow and fast pull-ups take the same work, but fast ones need more power. A trained person can manage over 1000 W for short bursts, about 100 to 200 W in the long run."),
                fact = l("Die Einheit Pferdestärke (735 W) stammt von James Watt, der damit seine Dampfmaschinen mit Grubenpferden verglich.", "The unit horsepower (735 W) comes from James Watt, who used it to compare his steam engines with pit ponies."),
            )
        },
        lab("phy.wasserfall", Topic.Energy, H, 0.5f) {
            val h = pick(50.0, 100.0, 979.0); val dT = G2 * h / 4190
            task(
                question = l("Wasser stürzt ${n(h, 0)} m tief einen Wasserfall hinab. Angenommen, die ganze Lageenergie wird unten zu Wärme im Wasser: Um wie viel wird es wärmer?", "Water plunges ${n(h, 0)} m down a waterfall. Suppose all the potential energy turns into heat in the water at the bottom: how much warmer does it get?"),
                steps = listOf(num(l("Temperaturanstieg in Kelvin", "Temperature rise in kelvin"), dT, "K", rel = 0.03, done = """§\Delta T \approx ${m(sig(dT, 2), 3)}\ \text{K}§""",
                    hints = listOf(l("Kernidee: Lageenergie pro Kilogramm gleich Wärme pro Kilogramm. Die Masse kürzt sich heraus.", "Key idea: potential energy per kilogram equals heat per kilogram. The mass cancels."), """§m g h = c\, m\, \Delta T \Rightarrow \Delta T = \dfrac{g h}{c}§"""))),
                solution = listOf("""§\Delta T = \dfrac{9{,}81 \cdot ${m(h, 0)}}{4190}\ \text{K} \approx ${m(sig(dT, 2), 3)}\ \text{K}§"""),
                background = l("Mechanische Energie und Wärme sind dasselbe in anderer Form. James Prescott Joule zeigte um 1845, dass eine bestimmte Menge Arbeit immer dieselbe Menge Wärme ergibt. Wasser braucht aber sehr viel Energie für ein Grad. Selbst ein hoher Wasserfall erwärmt es deshalb nur um Bruchteile eines Grades.", "Mechanical energy and heat are the same thing in different forms. Around 1845 James Prescott Joule showed that a given amount of work always produces the same amount of heat. But water needs a lot of energy for one degree. That's why even a tall waterfall warms it by only a fraction of a degree."),
                fact = l("Joule soll auf seiner Hochzeitsreise in der Schweiz ein Thermometer mitgenommen haben, um die Temperatur oben und unten an Wasserfällen zu messen.", "Joule is said to have taken a thermometer on his honeymoon in Switzerland to measure the temperature at the top and bottom of waterfalls."),
            )
        },
        lab("phy.stabhoch", Topic.Energy, M, 0.6f) {
            val v = pick(9.0, 9.5, 10.0); val hcm = 1.0
            val rise = v * v / (2 * G2); val top = rise + hcm
            task(
                question = l("Ein Stabhochspringer läuft mit ${n(v)} m/s an. Sein Schwerpunkt liegt beim Laufen 1,0 m über dem Boden. Wie hoch kann der Schwerpunkt höchstens steigen, wenn der Stab die ganze Bewegungsenergie in Höhe umwandelt?", "A pole vaulter runs up at ${n(v)} m/s. His centre of mass is 1.0 m above the ground while running. How high can his centre of mass rise at most if the pole turns all the kinetic energy into height?"),
                steps = listOf(num(l("Höhe des Schwerpunkts über dem Boden", "Height of the centre of mass above the ground"), top, "m", rel = 0.02, done = """§h \approx ${m(top, 2)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Bewegungsenergie wird Lageenergie. Die Masse kürzt sich heraus.", "Key idea: kinetic energy becomes potential energy. The mass cancels out."), """§\Delta h = \dfrac{v^2}{2g},\quad h = 1{,}0\ \text{m} + \Delta h§"""))),
                solution = listOf("""§\Delta h = \dfrac{${m(v)}^2}{2 \cdot 9{,}81}\ \text{m} \approx ${m(rise, 2)}\ \text{m}§""", """§h = 1{,}0 + ${m(rise, 2)} \approx ${m(top, 2)}\ \text{m}§"""),
                background = l("Der Stab ist ein Energiespeicher: Er biegt sich, speichert die Bewegungsenergie des Anlaufs und gibt sie als Hub wieder ab. Deshalb bestimmt vor allem die Anlaufgeschwindigkeit die Höhe, und die besten Stabhochspringer sind auch sehr schnelle Sprinter. Mit den Armen drücken sie sich oben noch ein Stück höher.", "The pole is an energy store: it bends, stores the kinetic energy of the run-up and gives it back as lift. That's why the run-up speed mostly determines the height, and the best pole vaulters are also very fast sprinters. At the top they push themselves a bit higher with their arms."),
                fact = l("Die besten Springer kommen heute über 6 Meter. Weil der Körper dabei wie ein Bogen über die Latte gleitet, kann der Schwerpunkt sogar knapp darunter bleiben.", "Today the best vaulters clear over 6 metres. Because the body curls over the bar like an arc, the centre of mass can even stay slightly below it."),
            )
        },
        lab("phy.energiedichte", Topic.Energy, E, 0.4f) {
            val fuel = 12.0; val batt = pick(0.25, 0.2)
            val ratio = fuel / batt
            task(
                question = l("1 kg Benzin enthält etwa 12 kWh chemische Energie, 1 kg Lithium-Akku etwa ${n(batt)} kWh. Wie viel mal so viel Energie steckt im Benzin?", "1 kg of petrol contains about 12 kWh of chemical energy, 1 kg of lithium battery about ${n(batt)} kWh. How many times as much energy is in the petrol?"),
                steps = listOf(num(l("Faktor", "Factor"), ratio, "", rel = 0.02, done = """§\approx ${m(ratio, 0)}§""",
                    hints = listOf(l("Kernidee: Einfach die beiden Energien pro Kilogramm durcheinander teilen.", "Key idea: just divide the two energies per kilogram."), """§\dfrac{12}{${m(batt)}}§"""))),
                solution = listOf("""§\dfrac{12\ \text{kWh}}{${m(batt)}\ \text{kWh}} = ${m(ratio, 0)}§"""),
                background = l("Energiedichte sagt, wie viel Energie in einem Kilogramm steckt. Benzin ist darin unschlagbar, aber ein Verbrennungsmotor macht nur etwa 30 % daraus Bewegung, ein Elektromotor über 90 %. Dadurch schrumpft der Vorsprung auf etwa ein Sechzehntel. Trotzdem wiegt der Akku eines Elektroautos oft eine halbe Tonne.", "Energy density tells you how much energy one kilogram holds. Petrol is unbeatable here, but a combustion engine turns only about 30% of it into motion, an electric motor over 90%. That shrinks the lead to roughly a factor of sixteen. Still, an electric car's battery often weighs half a tonne."),
                fact = l("Deshalb sind Flugzeuge so schwer zu elektrifizieren: Ein Langstreckenflug mit Akkus würde mehr Akku als Flugzeug brauchen.", "That's why planes are so hard to electrify: a long-haul flight on batteries would need more battery than aeroplane."),
            )
        },
        lab("phy.koffer", Topic.Energy, E, 0.6f) {
            val f = pick(30.0, 40.0, 50.0); val ang = pick(30.0, 45.0); val s = pick(300.0, 500.0)
            val w = f * s * cos(Math.toRadians(ang))
            task(
                question = l("Du ziehst einen Rollkoffer mit ${n(f, 0)} N am Griff, der ${n(ang, 0)}° gegen den Boden geneigt ist, ${n(s, 0)} m weit durch einen Flughafen. Wie viel Arbeit verrichtest du?", "You pull a wheeled suitcase with ${n(f, 0)} N on its handle, which is tilted ${n(ang, 0)}° to the floor, ${n(s, 0)} m through an airport. How much work do you do?"),
                steps = listOf(num(l("Arbeit in Kilojoule", "Work in kilojoules"), w / 1000, "kJ", rel = 0.02, done = """§W \approx ${m(sig(w / 1000))}\ \text{kJ}§""",
                    hints = listOf(l("Kernidee: Arbeit zählt nur der Teil der Kraft, der in Bewegungsrichtung zeigt.", "Key idea: only the part of the force that points in the direction of motion does work."), """§W = F \cdot s \cdot \cos\alpha§"""))),
                solution = listOf("""§W = ${m(f, 0)} \cdot ${m(s, 0)} \cdot \cos ${m(ang, 0)}^\circ\ \text{J} \approx ${m(sig(w / 1000))}\ \text{kJ}§"""),
                background = l("Wenn du schräg ziehst, hebt ein Teil deiner Kraft den Koffer nur an, der Rest zieht ihn vorwärts. Arbeit im physikalischen Sinn leistet nur der vorwärts gerichtete Teil. Wer einen Koffer nur trägt und geradeaus geht, verrichtet streng genommen gar keine Arbeit an ihm, auch wenn die Arme etwas anderes sagen.", "When you pull at an angle, part of your force only lifts the suitcase, the rest pulls it forwards. Only the forward part does work in the physics sense. Carrying a suitcase and walking straight ahead, strictly speaking, does no work on it at all, even if your arms tell you otherwise."),
                fact = l("Die Arme werden trotzdem müde, weil Muskeln ständig nachspannen müssen, auch ohne Bewegung.", "Your arms still get tired because muscles have to keep re-tensing constantly, even without movement."),
            )
        },
        lab("phy.dynamo", Topic.Energy, E, 0.3f) {
            val wh = pick(15.0, 19.0, 60.0); val p = 100.0; val t = wh / p * 60
            val what = if (wh > 30) l("einen Laptop-Akku (${n(wh, 0)} Wh)", "a laptop battery (${n(wh, 0)} Wh)") else l("einen Handy-Akku (${n(wh, 0)} Wh)", "a phone battery (${n(wh, 0)} Wh)")
            task(
                question = l("Auf einem Ergometer mit Generator strampelst du so, dass 100 W Strom herauskommen. Wie viele Minuten brauchst du, um $what einmal voll zu laden?", "On an exercise bike with a generator you pedal so that 100 W of electricity comes out. How many minutes does it take you to fully charge $what once?"),
                steps = listOf(num(l("Zeit in Minuten", "Time in minutes"), t, "min", rel = 0.02, done = """§t \approx ${m(t, 1)}\ \text{min}§""",
                    hints = listOf(l("Kernidee: Wattstunden durch Watt ergibt Stunden.", "Key idea: watt-hours divided by watts gives hours."), """§t = \dfrac{${m(wh, 0)}\ \text{Wh}}{100\ \text{W}}§"""))),
                solution = listOf("""§t = \dfrac{${m(wh, 0)}}{100}\ \text{h} = ${m(t, 1)}\ \text{min}§"""),
                background = l("Eine Wattstunde ist die Energie, die eine Leistung von einem Watt in einer Stunde liefert. Akkus werden oft in Wattstunden angegeben, Stromrechnungen in Kilowattstunden. Ein Mensch schafft auf dem Rad dauerhaft etwa 100 W, genug für Handys und Lampen, aber nicht für einen Haushalt.", "A watt-hour is the energy that a power of one watt delivers in one hour. Batteries are often rated in watt-hours, electricity bills in kilowatt-hours. A person on a bike can keep up about 100 W, enough for phones and lamps but not for a household."),
                fact = l("Für eine einzige Kilowattstunde müsstest du zehn Stunden strampeln. Aus der Steckdose kostet sie etwa 30 Cent.", "For a single kilowatt-hour you would have to pedal for ten hours. From the socket it costs about 30 cents."),
            )
        },
        lab("phy.wirkungskette", Topic.Energy, M, 0.5f) {
            val plant = 0.40; val grid = 0.95; val led = pick(0.35, 0.40); val bulb = 0.05
            val total = plant * grid * led * 100; val totalBulb = plant * grid * bulb * 100
            task(
                question = l("Ein Kohlekraftwerk macht 40 % der Energie der Kohle zu Strom, im Netz gehen 5 % verloren, und eine LED macht ${n(led * 100, 0)} % des Stroms zu Licht. Welcher Anteil der Kohleenergie wird am Ende Licht?", "A coal power station turns 40% of the coal's energy into electricity, 5% is lost in the grid, and an LED turns ${n(led * 100, 0)}% of the electricity into light. What fraction of the coal's energy ends up as light?"),
                steps = listOf(num(l("Gesamtwirkungsgrad in Prozent", "Overall efficiency in percent"), total, "%", rel = 0.02, done = """§\eta \approx ${m(total, 1)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Hintereinander geschaltete Wirkungsgrade werden multipliziert.", "Key idea: efficiencies in a chain are multiplied."), """§\eta = 0{,}40 \cdot 0{,}95 \cdot ${m(led)}§"""))),
                solution = listOf("""§\eta = 0{,}40 \cdot 0{,}95 \cdot ${m(led)} \approx ${m(total / 100, 3)} = ${m(total, 1)}\ \text{%}§"""),
                background = l("Jede Umwandlung verliert etwas, meist als Wärme. In einer Kette multiplizieren sich die Verluste, und aus vielen ordentlichen Wirkungsgraden wird ein kleiner Gesamtwert. Deshalb spart es so viel, ein Glied in der Kette zu verbessern, etwa die Glühbirne durch eine LED zu ersetzen.", "Every conversion loses something, usually as heat. In a chain the losses multiply, and many decent efficiencies add up to a small overall value. That's why improving one link saves so much, for example replacing an old light bulb with an LED."),
                fact = l("Mit einer alten Glühbirne (5 % Licht) wären es nur ${n(totalBulb, 1)} %. Sie war eher eine Heizung, die nebenbei leuchtet.", "With an old incandescent bulb (5% light) it would be only ${n(totalBulb, 1)}%. It was more of a heater that happened to glow."),
            )
        },
    )

    private val momentum = listOf(
        lab("phy.handyfall", Topic.Momentum, M, 0.5f) {
            val h = pick(1.0, 1.5); val mass = 0.2; val dt = pick(0.001, 0.002)
            val v = sqrt(2 * G2 * h); val f = mass * v / dt
            task(
                question = l("Dein Handy (200 g) fällt aus ${n(h)} m Höhe auf Fliesen und kommt in ${n(dt * 1000, 0)} ms zum Stillstand.", "Your phone (200 g) falls from a height of ${n(h)} m onto tiles and comes to rest in ${n(dt * 1000, 0)} ms."),
                steps = listOf(
                    num(l("Wie schnell trifft es auf?", "How fast does it hit?"), v, "m/s", rel = 0.02, done = """§v \approx ${m(v, 2)}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Freier Fall, Lageenergie wird Bewegungsenergie.", "Key idea: free fall, potential energy becomes kinetic energy."), """§v = \sqrt{2 g h}§""")),
                    num(l("Welche mittlere Kraft wirkt beim Aufprall (in Newton)?", "What average force acts during the impact (in newtons)?"), f, "N", rel = 0.03, done = """§F \approx ${m(sig(f))}\ \text{N}§""",
                        hints = listOf(l("Die Kraft ist die Impulsänderung geteilt durch die Bremszeit.", "The force is the change in momentum divided by the stopping time."), """§F = \dfrac{m v}{\Delta t}§""")),
                ),
                solution = listOf("""§v = \sqrt{2 \cdot 9{,}81 \cdot ${m(h)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 2)}\ \tfrac{\text{m}}{\text{s}}§""", """§F = \dfrac{0{,}2 \cdot ${m(v, 2)}}{${m(dt, 3)}}\ \text{N} \approx ${m(sig(f))}\ \text{N}§"""),
                background = l("Der Impuls des Handys muss beim Aufprall auf null. Wie groß die Kraft dabei wird, hängt nur davon ab, wie schnell das geschieht: Kraft mal Zeit gleich Impulsänderung. Auf einem Teppich dauert das Bremsen zehnmal länger, die Kraft ist zehnmal kleiner. Hüllen, Airbags und Fahrradhelme arbeiten alle nach diesem Prinzip.", "The phone's momentum has to drop to zero on impact. How large the force gets depends only on how quickly that happens: force times time equals change in momentum. On a carpet the stopping takes ten times longer and the force is ten times smaller. Phone cases, airbags and bike helmets all work on this principle."),
                fact = l("Das entspricht kurzzeitig dem Gewicht von rund ${n(f / G2, 0)} kg, verteilt auf eine kleine Ecke des Glases.", "That briefly equals the weight of about ${n(f / G2, 0)} kg, concentrated on a small corner of the glass."),
            )
        },
        lab("phy.tsiolkovsky", Topic.Momentum, X, 0.6f) {
            val ve = pick(3.0, 3.5, 4.4); val ratio = 10.0; val dvNeed = 9.4
            val dv = ve * ln(ratio); val needRatio = exp(dvNeed / ve)
            task(
                question = l("Eine Rakete stößt ihr Gas mit ${n(ve)} km/s aus. Für eine Erdumlaufbahn braucht man mit allen Verlusten etwa 9,4 km/s.", "A rocket ejects its exhaust at ${n(ve)} km/s. Reaching Earth orbit takes about 9.4 km/s including all losses."),
                steps = listOf(
                    num(l("Welche Geschwindigkeit erreicht eine einstufige Rakete, die zu 90 % aus Treibstoff besteht (Startmasse : Leermasse = 10)?", "What speed does a single-stage rocket reach that is 90% propellant (launch mass : empty mass = 10)?"), dv, "km/s", rel = 0.02, done = """§\Delta v \approx ${m(dv, 2)}\ \text{km/s}§""",
                        hints = listOf(l("Kernidee: Jedes ausgestoßene Gasstück gibt der Rakete einen Schubs. Weil sie dabei leichter wird, wirken spätere Schubse stärker. Das ergibt einen Logarithmus.", "Key idea: every bit of ejected gas gives the rocket a push. Because it gets lighter as it goes, later pushes count more. That gives a logarithm."), """§\Delta v = v_e \ln\dfrac{m_0}{m_1}§""")),
                    num(l("Welches Massenverhältnis bräuchte eine einzige Stufe für 9,4 km/s?", "What mass ratio would a single stage need for 9.4 km/s?"), needRatio, "", rel = 0.03, done = """§\dfrac{m_0}{m_1} \approx ${m(sig(needRatio))}§""",
                        hints = listOf(l("Die Raketengleichung nach dem Massenverhältnis umstellen.", "Rearrange the rocket equation for the mass ratio."), """§\dfrac{m_0}{m_1} = e^{\Delta v / v_e}§""")),
                ),
                solution = listOf("""§\Delta v = ${m(ve)} \cdot \ln 10\ \text{km/s} \approx ${m(dv, 2)}\ \text{km/s}§""", """§\dfrac{m_0}{m_1} = e^{9{,}4 / ${m(ve)}} \approx ${m(sig(needRatio))}§"""),
                background = l("Die Raketengleichung von Ziolkowski (1903) ist gnadenlos: Die erreichbare Geschwindigkeit wächst nur mit dem Logarithmus des Massenverhältnisses. Für etwas mehr Geschwindigkeit braucht man exponentiell mehr Treibstoff. Deshalb haben Raketen Stufen: Leere Tanks werden abgeworfen, damit man sie nicht weiter beschleunigen muss.", "Tsiolkovsky's rocket equation (1903) is merciless: the achievable speed grows only with the logarithm of the mass ratio. A little more speed needs exponentially more fuel. That's why rockets have stages: empty tanks are dropped so they don't have to be accelerated any further."),
                fact = l("Wasserstoff-Sauerstoff-Triebwerke erreichen die höchsten Ausströmgeschwindigkeiten chemischer Raketen, etwa 4,4 km/s.", "Hydrogen-oxygen engines reach the highest exhaust speeds of chemical rockets, about 4.4 km/s."),
            )
        },
        lab("phy.feuerwehr", Topic.Momentum, H, 0.5f) {
            val rate = pick(10.0, 15.0, 20.0); val v = pick(25.0, 30.0)
            val f = rate * v
            task(
                question = l("Aus einem Feuerwehrschlauch schießen ${n(rate, 0)} Liter Wasser pro Sekunde mit ${n(v, 0)} m/s. Mit welcher Kraft drückt der Schlauch nach hinten?", "${n(rate, 0)} litres of water per second shoot out of a fire hose at ${n(v, 0)} m/s. With what force does the hose push back?"),
                steps = listOf(num(l("Rückstoßkraft in Newton", "Recoil force in newtons"), f, "N", rel = 0.02, done = """§F = ${m(f, 0)}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Kraft ist Impuls pro Zeit. Jede Sekunde bekommt eine bestimmte Wassermasse ihre Geschwindigkeit.", "Key idea: force is momentum per time. Every second a certain mass of water gets its speed."), """§F = \dfrac{\Delta m}{\Delta t} \cdot v§"""))),
                solution = listOf("""§F = ${m(rate, 0)}\ \tfrac{\text{kg}}{\text{s}} \cdot ${m(v, 0)}\ \tfrac{\text{m}}{\text{s}} = ${m(f, 0)}\ \text{N}§"""),
                background = l("Wer etwas nach vorn wegstößt, wird nach hinten gestoßen. Beim Schlauch geschieht das ununterbrochen: Jede Sekunde bekommt eine neue Wassermenge ihren Impuls, und genau diesen Impuls pro Sekunde spürt man als Kraft. Raketen funktionieren genauso, nur mit heißem Gas statt Wasser.", "Whoever pushes something forwards gets pushed backwards. With a hose this happens continuously: every second a new amount of water gets its momentum, and exactly this momentum per second is felt as a force. Rockets work the same way, only with hot gas instead of water."),
                fact = l("Das entspricht dem Gewicht von etwa ${n(f / G2, 0)} kg. Deshalb halten oft zwei Feuerwehrleute einen großen Schlauch, und ein losgelassener Schlauch schlägt wild um sich.", "That equals the weight of about ${n(f / G2, 0)} kg. That's why two firefighters often hold a large hose, and a hose let go thrashes about wildly."),
            )
        },
        lab("phy.feuerwerk", Topic.Momentum, H, 0.6f) {
            val range = pick(200.0, 300.0, 400.0)
            val land = 1.5 * range
            task(
                question = l("Eine Feuerwerkskugel würde ohne Explosion ${n(range, 0)} m entfernt landen. Genau im höchsten Punkt zerplatzt sie in zwei gleich schwere Teile. Eines fällt senkrecht nach unten. Wo landet das andere (Luftwiderstand vernachlässigt)?", "A firework shell would land ${n(range, 0)} m away without exploding. Exactly at its highest point it bursts into two pieces of equal mass. One falls straight down. Where does the other land (air resistance neglected)?"),
                steps = listOf(num(l("Entfernung vom Abschuss", "Distance from the launch point"), land, "m", rel = 0.02, done = """§x = ${m(land, 0)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Innere Kräfte ändern die Bahn des Schwerpunkts nicht. Er landet dort, wo die ganze Kugel gelandet wäre.", "Key idea: internal forces don't change the path of the centre of mass. It lands where the whole shell would have landed."), l("Der höchste Punkt liegt in der Mitte, bei ${n(range / 2, 0)} m. Dort landet das erste Teil.", "The highest point is in the middle, at ${n(range / 2, 0)} m. That's where the first piece lands."))),
                    ),
                solution = listOf(l("Erstes Teil: bei ${n(range / 2, 0)} m. Schwerpunkt: bei ${n(range, 0)} m.", "First piece: at ${n(range / 2, 0)} m. Centre of mass: at ${n(range, 0)} m."), """§\dfrac{${m(range / 2, 0)} + x}{2} = ${m(range, 0)} \Rightarrow x = ${m(land, 0)}\ \text{m}§"""),
                background = l("Eine Explosion ist eine innere Kraft: Die Teile stoßen sich gegenseitig ab, aber der Gesamtimpuls bleibt gleich. Deshalb fliegt der Schwerpunkt aller Bruchstücke auf derselben Parabel weiter, als wäre nichts passiert. Bleibt ein Teil stehen, muss das andere doppelt so schnell nach vorn.", "An explosion is an internal force: the pieces push each other apart, but the total momentum stays the same. That's why the centre of mass of all the fragments keeps flying along the same parabola as if nothing had happened. If one piece stops dead, the other must fly forwards twice as fast."),
                fact = l("Mit dieser Idee rechnet die Raumfahrt, wenn sich eine Raketenstufe abtrennt: Der gemeinsame Schwerpunkt fliegt unbeirrt weiter.", "Spaceflight uses this idea when a rocket stage separates: the common centre of mass carries on undisturbed."),
            )
        },
        lab("phy.baryzentrum", Topic.Momentum, M, 0.5f) {
            val ratio = 81.3; val d = 384400.0
            val x = d / (1 + ratio)
            task(
                question = l("Erde und Mond kreisen um ihren gemeinsamen Schwerpunkt. Die Erde ist 81,3-mal so schwer wie der Mond, der Abstand der Mittelpunkte beträgt 384.400 km. Wie weit liegt der Schwerpunkt vom Erdmittelpunkt entfernt?", "The Earth and the Moon orbit their common centre of mass. The Earth is 81.3 times as massive as the Moon, and the distance between their centres is 384,400 km. How far from the Earth's centre is the centre of mass?"),
                steps = listOf(num(l("Abstand in km", "Distance in km"), x, "km", rel = 0.02, done = """§x \approx ${m(sig(x))}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Der Schwerpunkt teilt den Abstand im umgekehrten Verhältnis der Massen.", "Key idea: the centre of mass divides the distance in the inverse ratio of the masses."), """§x = \dfrac{m_M}{m_E + m_M} \cdot d = \dfrac{d}{1 + 81{,}3}§"""))),
                solution = listOf("""§x = \dfrac{384\,400}{82{,}3}\ \text{km} \approx ${m(sig(x))}\ \text{km}§"""),
                background = l("Nicht nur der Mond kreist um die Erde, beide kreisen um einen gemeinsamen Punkt, wie zwei Tänzer, die sich an den Händen halten. Weil die Erde viel schwerer ist, liegt dieser Punkt nah bei ihr, sogar noch in ihrem Inneren, denn ihr Radius ist 6371 km. Die Erde eiert also einmal im Monat um diesen Punkt.", "It's not only the Moon that orbits the Earth; both orbit a common point, like two dancers holding hands. Because the Earth is much heavier, this point lies close to it, even inside it, since its radius is 6371 km. So the Earth wobbles around this point once a month."),
                fact = l("Genau dieses Eiern verrät Planeten bei fremden Sternen: Der Stern wackelt ein wenig, und das sieht man im Licht.", "Exactly this wobble gives away planets around other stars: the star wobbles a little, and you can see it in its light."),
            )
        },
        lab("phy.boot", Topic.Momentum, M, 0.5f) {
            val mp = pick(60.0, 70.0, 80.0); val mb = pick(120.0, 140.0, 200.0); val s = 4.0
            val dx = mp * s / (mp + mb)
            task(
                question = l("Du (${n(mp, 0)} kg) stehst hinten in einem ruhenden Ruderboot (${n(mb, 0)} kg) und gehst 4 m nach vorn. Wasserwiderstand vernachlässigen wir. Wie weit bewegt sich das Boot dabei?", "You (${n(mp, 0)} kg) stand at the back of a rowing boat at rest (${n(mb, 0)} kg) and walk 4 m to the front. We neglect water resistance. How far does the boat move?"),
                steps = listOf(num(l("Strecke des Bootes nach hinten", "Distance the boat moves backwards"), dx, "m", rel = 0.02, done = """§\Delta x \approx ${m(dx, 2)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Ohne äußere Kraft bleibt der gemeinsame Schwerpunkt an seinem Platz.", "Key idea: without an external force the common centre of mass stays where it is."), """§m_P (s - \Delta x) = m_B\, \Delta x§"""))),
                solution = listOf("""§\Delta x = \dfrac{${m(mp, 0)} \cdot 4}{${m(mp, 0)} + ${m(mb, 0)}}\ \text{m} \approx ${m(dx, 2)}\ \text{m}§"""),
                background = l("Wenn du vorn gehst, stößt du dich am Boot nach hinten ab, und das Boot rutscht zurück. Der Schwerpunkt von dir und Boot bleibt dabei an Ort und Stelle, denn von außen wirkt keine waagrechte Kraft. Gegenüber dem Ufer gehst du deshalb weniger als 4 m. Wer beim Anlegen ans Ufer springen will, merkt das oft zu spät.", "As you walk forwards you push off the boat backwards, and the boat slides back. The centre of mass of you and the boat stays put, because no horizontal force acts from outside. So relative to the shore you walk less than 4 m. Anyone who wants to jump ashore when landing often notices this too late."),
                fact = l("Das ist dasselbe Prinzip, mit dem sich Astronauten im All drehen und bewegen, ohne sich irgendwo abzustoßen.", "It's the same principle that lets astronauts turn and shift position in space without pushing off anything."),
            )
        },
        lab("phy.newtonwiege", Topic.Momentum, E, 0.5f) {
            task(
                question = l("Bei einem Kugelstoßpendel (Newton-Wiege) hängen fünf gleiche Stahlkugeln nebeneinander. Du hebst zwei Kugeln an und lässt sie los. Was passiert?", "In a Newton's cradle five identical steel balls hang side by side. You lift two balls and let go. What happens?"),
                steps = listOf(pick1(l("Was fliegt auf der anderen Seite weg?", "What flies off on the other side?"),
                    l("Zwei Kugeln, genauso schnell", "Two balls, just as fast"),
                    listOf(l("Eine Kugel, doppelt so schnell", "One ball, twice as fast"), l("Alle drei übrigen Kugeln, etwas langsamer", "All three remaining balls, a bit slower"), l("Nichts, die Kugeln bleiben zusammen", "Nothing, the balls stay together")),
                    hints = listOf(l("Kernidee: Es müssen gleich zwei Dinge erhalten bleiben: der Impuls und die Bewegungsenergie.", "Key idea: two things must be conserved at the same time: momentum and kinetic energy."), l("Eine Kugel mit doppelter Geschwindigkeit hätte denselben Impuls, aber doppelt so viel Energie.", "One ball at twice the speed would have the same momentum, but twice the energy.")))),
                solution = listOf(l("Nur zwei Kugeln mit derselben Geschwindigkeit erfüllen beide Erhaltungssätze. Eine Kugel mit doppeltem Tempo hätte die doppelte Energie, das geht nicht.", "Only two balls at the same speed satisfy both conservation laws. One ball at double speed would have twice the energy, which is impossible.")),
                background = l("Bei elastischen Stößen bleiben Impuls (m·v) und Bewegungsenergie (½·m·v²) erhalten. Weil die Energie mit dem Quadrat der Geschwindigkeit wächst, legen die beiden Gesetze zusammen genau fest, was passieren muss. Die Stöße laufen dabei als Druckwelle durch die ruhenden Kugeln.", "In elastic collisions both momentum (m·v) and kinetic energy (½·m·v²) are conserved. Because energy grows with the square of the speed, the two laws together fix exactly what must happen. The impacts travel as a pressure wave through the balls at rest."),
                fact = l("Ganz perfekt ist es nie: Nach einer Weile schwingen alle Kugeln gemeinsam ein wenig, weil bei jedem Stoß etwas Energie verloren geht.", "It's never quite perfect: after a while all the balls swing a little together, because some energy is lost in every collision."),
            )
        },
        lab("phy.knete", Topic.Momentum, E, 0.5f) {
            task(
                question = l("Du wirfst zwei gleich schwere Bälle gleich schnell gegen eine Wand: einen Flummi, der elastisch zurückprallt, und einen Klumpen Knete, der kleben bleibt. Welcher gibt der Wand den größeren Stoß?", "You throw two balls of equal mass at the same speed against a wall: a bouncy ball that bounces back elastically and a lump of modelling clay that sticks. Which gives the wall the bigger push?"),
                steps = listOf(pick1(l("Welcher stößt stärker?", "Which pushes harder?"),
                    l("Der Flummi, doppelt so stark", "The bouncy ball, twice as hard"),
                    listOf(l("Die Knete, weil sie ihre ganze Energie abgibt", "The clay, because it gives up all its energy"), l("Beide gleich stark", "Both equally hard"), l("Der Flummi, aber nur ein bisschen mehr", "The bouncy ball, but only a little more")),
                    hints = listOf(l("Kernidee: Der Stoß auf die Wand ist die Impulsänderung des Balls.", "Key idea: the push on the wall is the ball's change in momentum."), l("Die Knete geht von v auf null. Der Flummi geht von v auf −v.", "The clay goes from v to zero. The bouncy ball goes from v to −v.")))),
                solution = listOf(l("Knete: Impulsänderung m·v. Flummi: von +m·v auf −m·v, also 2·m·v. Die Wand muss ihn nicht nur stoppen, sondern auch zurückwerfen.", "Clay: change in momentum m·v. Bouncy ball: from +m·v to −m·v, so 2·m·v. The wall has to stop it and also throw it back.")),
                background = l("Viele erwarten, dass der Klumpen stärker wirkt, weil seine Energie in der Wand bleibt. Für den Stoß zählt aber der Impuls, nicht die Energie. Ein abprallender Ball muss erst gebremst und dann wieder beschleunigt werden, beides drückt gegen die Wand. Darum sind abprallende Hagelkörner für ein Dach gefährlicher als Matsch.", "Many expect the lump to have more effect because its energy stays in the wall. But for the push it's the momentum that counts, not the energy. A bouncing ball has to be stopped and then accelerated back, and both push against the wall. That's why bouncing hailstones are more dangerous for a roof than slush."),
                fact = l("Auf diesem Prinzip beruht der Druck eines Gases: Moleküle prallen elastisch von der Wand ab und übertragen dabei jeweils doppelten Impuls.", "The pressure of a gas is based on this principle: molecules bounce elastically off the wall and transfer double momentum each time."),
            )
        },
        lab("phy.regenwaggon", Topic.Momentum, H, 0.6f) {
            val mw = 10.0; val v0 = pick(2.0, 3.0); val water = pick(2.0, 2.5, 5.0)
            val v1 = mw * v0 / (mw + water)
            task(
                question = l("Ein offener Güterwagen (10 t) rollt reibungsfrei mit ${n(v0, 0)} m/s über ein ebenes Gleis. Es regnet senkrecht, und ${n(water)} t Wasser sammeln sich im Wagen. Wie schnell rollt er danach?", "An open freight wagon (10 t) rolls without friction at ${n(v0, 0)} m/s along a level track. It rains vertically, and ${n(water)} t of water collect in the wagon. How fast does it roll afterwards?"),
                steps = listOf(num(l("Neue Geschwindigkeit", "New speed"), v1, "m/s", rel = 0.02, done = """§v \approx ${m(v1, 2)}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Der Regen bringt keinen waagrechten Impuls mit. Der Impuls des Wagens verteilt sich auf mehr Masse.", "Key idea: the rain brings no horizontal momentum. The wagon's momentum gets shared with more mass."), """§m_W v_0 = (m_W + m_R)\, v§"""))),
                solution = listOf("""§v = \dfrac{10 \cdot ${m(v0, 0)}}{10 + ${m(water)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v1, 2)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = l("Jeder Regentropfen, der im Wagen landet, muss auf dessen Geschwindigkeit gebracht werden. Den Impuls dafür nimmt er dem Wagen weg, wie bei einem unelastischen Stoß in Zeitlupe. Waagrecht wirkt von außen keine Kraft, also bleibt der Gesamtimpuls gleich, nur auf mehr Masse verteilt. Die Bewegungsenergie dagegen nimmt ab: Sie wird beim Auftreffen der Tropfen zu Wärme.", "Every raindrop that lands in the wagon has to be brought up to its speed. It takes the momentum for that from the wagon, like an inelastic collision in slow motion. No horizontal force acts from outside, so the total momentum stays the same, just shared among more mass. The kinetic energy, on the other hand, decreases: it turns into heat as the drops land."),
                fact = l("Ein Loch im Boden würde nichts zurückholen: Auslaufendes Wasser nimmt seinen Impuls einfach mit, der Wagen behält seine Geschwindigkeit.", "A hole in the floor wouldn't get anything back: water running out simply takes its momentum with it, and the wagon keeps its speed."),
            )
        },
        lab("phy.ballstapel", Topic.Momentum, X, 0.7f) {
            val h = pick(1.0, 1.5)
            val up = 9 * h
            task(
                question = l("Du hältst einen Tennisball genau auf einen Basketball und lässt beide aus ${n(h)} m Höhe fallen. Der Basketball ist viel schwerer, und alle Stöße sind elastisch. Wie hoch springt der Tennisball im Idealfall?", "You hold a tennis ball exactly on top of a basketball and drop both from a height of ${n(h)} m. The basketball is much heavier, and all collisions are elastic. How high does the tennis ball bounce in the ideal case?"),
                steps = listOf(num(l("Steighöhe des Tennisballs", "Height the tennis ball reaches"), up, "m", rel = 0.02, done = """§h' = ${m(up)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Der Basketball prallt vom Boden ab und kommt mit v nach oben, während der Tennisball noch mit v nach unten fällt. Aus Sicht des Basketballs trifft ihn der Tennisball mit 2v.", "Key idea: the basketball bounces off the floor and comes up at v while the tennis ball is still falling down at v. From the basketball's point of view the tennis ball hits it at 2v."), l("Der leichte Ball prallt von der schweren Wand mit 2v zurück. Dazu kommt die Bewegung des Basketballs: 3v. Höhe wächst mit v².", "The light ball bounces off the heavy 'wall' at 2v. Add the basketball's own motion: 3v. Height grows with v²."))),
                    ),
                solution = listOf("""§v' = 2v + v = 3v§""", """§h' = 3^2 \cdot ${m(h)}\ \text{m} = ${m(up)}\ \text{m}§"""),
                background = l("Ein elastischer Stoß ist am einfachsten im Bezugssystem des schweren Körpers: Dort prallt der leichte einfach mit gleicher Geschwindigkeit zurück. Zurück im Bezugssystem des Bodens kommt die Geschwindigkeit des schweren Balls noch dazu. So wird aus v plötzlich 3v, und aus der Höhe das Neunfache. In echt verliert man Energie, aber das Prinzip ist verblüffend gut zu sehen.", "An elastic collision is simplest in the frame of the heavy body: there the light one just bounces back at the same speed. Back in the frame of the floor, the heavy ball's speed is added on top. So v suddenly becomes 3v, and the height nine times as much. In reality some energy is lost, but the effect is strikingly visible."),
                fact = l("Ähnlich holen Raumsonden beim Vorbeiflug an Planeten Schwung: Der Planet ist der Basketball, die Sonde der Tennisball.", "Space probes gain speed in a similar way when they swing past planets: the planet is the basketball, the probe the tennis ball."),
            )
        },
        lab("phy.protonwinkel", Topic.Momentum, H, 0.7f) {
            task(
                question = l("In einer Blasenkammer trifft ein schnelles Proton ein ruhendes Proton nicht ganz zentral. Beide fliegen danach auseinander. Unter welchem Winkel zueinander laufen ihre Spuren?", "In a bubble chamber a fast proton strikes a proton at rest slightly off-centre. Afterwards both fly apart. At what angle to each other do their tracks run?"),
                steps = listOf(pick1(l("Winkel zwischen den Spuren", "Angle between the tracks"), "90°", listOf("45°", "180°", l("Hängt davon ab, wie schief der Stoß ist", "It depends on how off-centre the collision is")),
                    hints = listOf(l("Kernidee: Gleiche Massen, elastischer Stoß. Impulserhaltung ist eine Pfeil-Summe, Energieerhaltung eine Summe von Quadraten.", "Key idea: equal masses, elastic collision. Conservation of momentum is an arrow sum, conservation of energy a sum of squares."), l("Wann gilt für drei Pfeile a = b + c zugleich a² = b² + c²? Denk an Pythagoras.", "When do three arrows with a = b + c also satisfy a² = b² + c²? Think of Pythagoras.")))),
                solution = listOf(l("Impuls: v = v₁ + v₂ (als Pfeile). Energie: v² = v₁² + v₂². Beides zugleich geht nur, wenn v₁ und v₂ senkrecht aufeinander stehen (Pythagoras).", "Momentum: v = v₁ + v₂ (as arrows). Energy: v² = v₁² + v₂². Both at once only works if v₁ and v₂ are perpendicular (Pythagoras).")),
                background = l("Bei elastischen Stößen gleich schwerer Teilchen, von denen eins ruht, stehen die Bahnen danach immer im rechten Winkel, egal wie schief der Stoß ist. Physiker nutzten das, um Teilchen in Blasenkammerfotos zu erkennen: Ein 90°-Winkel verriet, dass zwei gleich schwere Teilchen zusammengestoßen waren.", "In elastic collisions between particles of equal mass, one of them at rest, the paths afterwards are always at right angles, however off-centre the collision. Physicists used this to identify particles in bubble chamber photos: a 90° angle revealed that two particles of equal mass had collided."),
                fact = l("Beim Billard sieht man denselben Effekt: Trifft die weiße Kugel ohne Effet schräg auf eine Kugel, laufen beide fast im rechten Winkel auseinander.", "You see the same effect in billiards: if the cue ball hits another ball at an angle without spin, the two run apart at almost a right angle."),
            )
        },
    )

    private val rotation = listOf(
        lab("phy.schleuder", Topic.Rotation, M, 0.5f) {
            val rpm = pick(1200.0, 1400.0, 1600.0); val r = 0.25
            val w = 2 * PI * rpm / 60; val a = w * w * r; val g = a / G2
            task(
                question = l("Die Trommel einer Waschmaschine (Radius 25 cm) schleudert mit ${n(rpm, 0)} Umdrehungen pro Minute. Wie groß ist die Zentripetalbeschleunigung an der Trommelwand, in Vielfachen von g?", "A washing machine drum (radius 25 cm) spins at ${n(rpm, 0)} revolutions per minute. How large is the centripetal acceleration at the drum wall, in multiples of g?"),
                steps = listOf(num(l("Beschleunigung in g", "Acceleration in g"), g, "g", rel = 0.03, done = """§a \approx ${m(sig(g))}\, g§""",
                    hints = listOf(l("Kernidee: Erst die Winkelgeschwindigkeit in rad/s, dann a = ω²·r.", "Key idea: first the angular velocity in rad/s, then a = ω²·r."), """§\omega = \dfrac{2\pi \cdot ${m(rpm, 0)}}{60\ \text{s}},\quad a = \omega^2 r§"""))),
                solution = listOf("""§\omega = \dfrac{2\pi \cdot ${m(rpm, 0)}}{60}\ \text{s}^{-1} \approx ${m(w, 1)}\ \text{s}^{-1}§""", """§a = ${m(w, 1)}^2 \cdot 0{,}25\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(a))}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(g))}\, g§"""),
                background = l("Damit sich etwas im Kreis bewegt, muss es ständig zur Mitte hin beschleunigt werden. Die Trommelwand drückt die Wäsche nach innen. Das Wasser aber kann durch die Löcher entkommen, es fliegt geradeaus weiter und verlässt so die Trommel. Die Beschleunigung wächst mit dem Quadrat der Drehzahl.", "For something to move in a circle it has to be accelerated towards the centre all the time. The drum wall pushes the laundry inwards. But the water can escape through the holes: it carries on in a straight line and so leaves the drum. The acceleration grows with the square of the spin speed."),
                fact = l("Laborzentrifugen erreichen über 100.000 g. So trennt man Blutbestandteile oder sogar verschieden schwere Moleküle.", "Laboratory centrifuges reach over 100,000 g. That's how blood components or even molecules of different mass are separated."),
            )
        },
        lab("phy.raumstation", Topic.Rotation, H, 0.5f) {
            val r = pick(50.0, 100.0, 200.0)
            val w = sqrt(G2 / r); val t = 2 * PI / w; val rpm = 60 / t
            task(
                question = l("Eine ringförmige Raumstation mit ${n(r, 0)} m Radius soll sich so drehen, dass man am Außenrand wie auf der Erde steht (1 g). Wie lange dauert eine Umdrehung?", "A ring-shaped space station with a radius of ${n(r, 0)} m is to spin so that people on the outer rim stand as if on Earth (1 g). How long does one rotation take?"),
                steps = listOf(num(l("Umlaufzeit in Sekunden", "Rotation period in seconds"), t, "s", rel = 0.02, done = """§T \approx ${m(t, 1)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Der Boden des Rings muss dich mit genau g zur Mitte beschleunigen.", "Key idea: the floor of the ring must accelerate you towards the centre with exactly g."), """§\omega^2 r = g \Rightarrow T = 2\pi \sqrt{\dfrac{r}{g}}§"""))),
                solution = listOf("""§T = 2\pi \sqrt{\dfrac{${m(r, 0)}}{9{,}81}}\ \text{s} \approx ${m(t, 1)}\ \text{s}§"""),
                background = l("In einer rotierenden Station drückt dich der Boden ständig zur Mitte, und genau das fühlt sich an wie Gewicht. Je kleiner der Ring, desto schneller muss er sich drehen. Das hat Nebenwirkungen: Dreht man schnell den Kopf, verwirrt das den Gleichgewichtssinn, und oben am Kopf ist die Schwerkraft schwächer als an den Füßen.", "In a rotating station the floor constantly pushes you towards the centre, and that is exactly what feels like weight. The smaller the ring, the faster it must spin. That has side effects: turning your head quickly confuses your sense of balance, and gravity is weaker at your head than at your feet."),
                fact = l("Das sind ${n(rpm, 1)} Umdrehungen pro Minute. Studien zeigen, dass Menschen sich an bis zu etwa 2 bis 4 pro Minute gewöhnen können, schnellere Drehung macht übel.", "That's ${n(rpm, 1)} revolutions per minute. Studies show that people can get used to about 2 to 4 per minute; faster spinning makes them sick."),
            )
        },
        lab("phy.erdrotation", Topic.Rotation, M, 0.4f) {
            val r = 6371.0; val lat = pick(50.0, 52.0, 48.0)
            val vEq = 2 * PI * r / 23.93; val vHere = vEq * cos(Math.toRadians(lat))
            task(
                question = l("Die Erde dreht sich in 23,93 Stunden einmal um sich selbst (Radius 6371 km).", "The Earth turns once on its axis in 23.93 hours (radius 6371 km)."),
                steps = listOf(
                    num(l("Wie schnell bewegt sich ein Punkt am Äquator (in km/h)?", "How fast does a point on the equator move (in km/h)?"), vEq, "km/h", rel = 0.01, done = """§v \approx ${m(sig(vEq, 4), 0)}\ \text{km/h}§""",
                        hints = listOf(l("Kernidee: Einmal der Erdumfang pro Drehung.", "Key idea: once around the Earth's circumference per rotation."), """§v = \dfrac{2\pi R}{T}§""")),
                    num(l("Und in Deutschland auf ${n(lat, 0)}° nördlicher Breite?", "And in Germany at ${n(lat, 0)}° north?"), vHere, "km/h", rel = 0.01, done = """§v \approx ${m(sig(vHere, 4), 0)}\ \text{km/h}§""",
                        hints = listOf(l("Auf höherer Breite ist der Kreis, auf dem du dich drehst, kleiner. Sein Radius ist R·cos(Breite).", "At higher latitudes the circle you turn on is smaller. Its radius is R·cos(latitude)."), """§v = v_0 \cdot \cos ${m(lat, 0)}^\circ§""")),
                ),
                solution = listOf("""§v = \dfrac{2\pi \cdot 6371\ \text{km}}{23{,}93\ \text{h}} \approx ${m(sig(vEq, 4), 0)}\ \text{km/h}§""", """§v = ${m(sig(vEq, 4), 0)} \cdot \cos ${m(lat, 0)}^\circ \approx ${m(sig(vHere, 4), 0)}\ \text{km/h}§"""),
                background = l("Wir rasen ständig mit über tausend Kilometern pro Stunde nach Osten und merken nichts, weil die Bewegung gleichmäßig ist und alles um uns, auch die Luft, mitfährt. Man spürt nur Beschleunigungen. Die Drehung zeigt sich aber trotzdem: im Foucault-Pendel, in der Drehrichtung von Tiefdruckgebieten und in einer leicht abgeplatteten Erde.", "We are constantly racing eastwards at over a thousand kilometres per hour and don't notice, because the motion is steady and everything around us, the air included, moves along. You only feel accelerations. But the rotation still shows itself: in the Foucault pendulum, in the spin direction of low-pressure systems and in a slightly flattened Earth."),
                fact = l("Raketen starten deshalb gern nach Osten und möglichst nah am Äquator: Den Schwung der Erde bekommen sie geschenkt.", "That's why rockets like to launch eastwards and as close to the equator as possible: they get the Earth's spin for free."),
            )
        },
        lab("phy.bizeps", Topic.Rotation, H, 0.4f) {
            val mass = pick(5.0, 10.0); val lHand = 0.35; val lMuscle = 0.04
            val f = mass * G2 * lHand / lMuscle
            task(
                question = l("Du hältst eine ${n(mass, 0)}-kg-Hantel mit waagrechtem Unterarm. Die Hand ist 35 cm vom Ellenbogen entfernt, der Bizeps setzt nur 4 cm vom Ellenbogen an und zieht senkrecht nach oben. Wie stark muss er ziehen (Gewicht des Unterarms vernachlässigt)?", "You hold a ${n(mass, 0)} kg dumbbell with your forearm horizontal. The hand is 35 cm from the elbow; the biceps attaches only 4 cm from the elbow and pulls straight up. How hard must it pull (weight of the forearm neglected)?"),
                steps = listOf(num(l("Muskelkraft in Newton", "Muscle force in newtons"), f, "N", rel = 0.02, done = """§F \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Der Ellenbogen ist der Drehpunkt eines Hebels. Die Drehmomente von Muskel und Hantel müssen gleich sein.", "Key idea: the elbow is the pivot of a lever. The torques of muscle and dumbbell must be equal."), """§F \cdot 0{,}04 = m g \cdot 0{,}35§"""))),
                solution = listOf("""§F = \dfrac{${m(mass, 0)} \cdot 9{,}81 \cdot 0{,}35}{0{,}04}\ \text{N} \approx ${m(sig(f))}\ \text{N}§"""),
                background = l("Unsere Arme sind Hebel, die auf Geschwindigkeit statt auf Kraft gebaut sind. Der Muskel greift nah am Gelenk an: Er muss sehr stark ziehen, verkürzt sich dafür aber nur wenig, während die Hand einen großen Weg zurücklegt. So können wir schnell werfen und schlagen, zahlen aber mit hohen Kräften in Muskeln und Sehnen.", "Our arms are levers built for speed rather than force. The muscle attaches close to the joint: it has to pull very hard, but in return it only shortens a little while the hand travels a long way. That lets us throw and strike quickly, but we pay with high forces in muscles and tendons."),
                fact = l("Das entspricht dem Gewicht von etwa ${n(f / G2, 0)} kg, also rund dem Neunfachen der Hantel.", "That equals the weight of about ${n(f / G2, 0)} kg, roughly nine times the dumbbell."),
            )
        },
        lab("phy.leiter", Topic.Rotation, X, 0.6f) {
            val mu = pick(0.3, 0.4, 0.5); val ang = Math.toDegrees(atan(1 / (2 * mu)))
            task(
                question = l("Eine Leiter lehnt an einer glatten Wand (keine Reibung dort). Zwischen Leiter und Boden ist die Haftreibungszahl ${n(mu)}. Wie steil muss die Leiter mindestens stehen, damit sie nicht wegrutscht (Winkel zum Boden)?", "A ladder leans against a smooth wall (no friction there). Between the ladder and the floor the coefficient of static friction is ${n(mu)}. How steep must the ladder be at least so that it doesn't slip (angle to the floor)?"),
                steps = listOf(num(l("Kleinster Winkel zum Boden", "Smallest angle to the floor"), ang, "°", abs = 0.5, done = """§\alpha \approx ${m(ang, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Kräfte und Drehmomente müssen sich aufheben. Die Wand drückt waagrecht, die Reibung am Boden muss genau dagegenhalten.", "Key idea: forces and torques must cancel. The wall pushes horizontally, and friction at the floor must hold exactly against it."), """§\tan\alpha = \dfrac{1}{2\mu}§"""))),
                solution = listOf(l("Drehmoment um den Fußpunkt: Wandkraft · L sin α = Gewicht · (L/2) cos α. Reibung = Wandkraft ≤ μ · Gewicht.", "Torque about the foot: wall force · L sin α = weight · (L/2) cos α. Friction = wall force ≤ μ · weight."), """§\tan\alpha = \dfrac{1}{2 \cdot ${m(mu)}} \Rightarrow \alpha \approx ${m(ang, 1)}^\circ§"""),
                background = l("Je flacher die Leiter, desto stärker drückt sie gegen die Wand, und desto mehr Reibung braucht sie am Fuß. Die Länge und das Gewicht der Leiter kürzen sich heraus, nur μ zählt. Steigt man hinauf, verschiebt sich der Schwerpunkt nach oben, und die Leiter rutscht leichter. Deshalb gilt die Faustregel: lieber zu steil als zu flach.", "The flatter the ladder, the harder it pushes against the wall, and the more friction it needs at the foot. The length and weight of the ladder cancel out; only μ counts. When you climb up, the centre of mass moves up and the ladder slips more easily. Hence the rule of thumb: better too steep than too flat."),
                fact = l("Arbeitsschutzregeln empfehlen etwa 75° als Anstellwinkel, mit Gummifüßen für hohe Reibung.", "Safety rules recommend an angle of about 75°, with rubber feet for high friction."),
            )
        },
        lab("phy.jojo", Topic.Rotation, H, 0.6f) {
            val bigR = pick(3.0, 3.5); val r = 0.5
            val k = 0.5 * (bigR / r) * (bigR / r); val a = G2 / (1 + k)
            task(
                question = l("Ein Jojo ist eine Vollscheibe mit ${n(bigR)} cm Radius. Die Schnur ist auf eine Achse mit 0,5 cm Radius gewickelt. Du lässt es fallen und hältst die Schnur fest. Mit welcher Beschleunigung sinkt es?", "A yo-yo is a solid disc with a radius of ${n(bigR)} cm. The string is wound on an axle with a radius of 0.5 cm. You let it drop while holding the string. With what acceleration does it fall?"),
                steps = listOf(num(l("Beschleunigung", "Acceleration"), a, "m/s²", rel = 0.03, done = """§a \approx ${m(a, 2)}\ \text{m/s}^2§""",
                    hints = listOf(l("Kernidee: Die Lageenergie muss das Jojo nicht nur nach unten bewegen, sondern vor allem in Drehung versetzen.", "Key idea: the potential energy has to not only move the yo-yo down but mostly set it spinning."), """§a = \dfrac{g}{1 + \dfrac{I}{m r^2}},\quad I = \tfrac{1}{2} m R^2§"""))),
                solution = listOf("""§\dfrac{I}{m r^2} = \tfrac{1}{2} \left(\dfrac{${m(bigR)}}{0{,}5}\right)^2 = ${m(k, 1)}§""", """§a = \dfrac{9{,}81}{1 + ${m(k, 1)}}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(a, 2)}\ \tfrac{\text{m}}{\text{s}^2}§"""),
                background = l("Ein Jojo ist ein Fall in Zeitlupe. Weil die Schnur auf einer dünnen Achse sitzt, muss sich die Scheibe sehr schnell drehen, damit sie ein Stück sinkt. Fast die ganze Energie geht in die Drehung, nur ein kleiner Rest in die Fallbewegung. Unten rollt es sich dann an der Schnur wieder hoch.", "A yo-yo is a fall in slow motion. Because the string sits on a thin axle, the disc must spin very fast to drop a little. Almost all the energy goes into rotation, only a small remainder into falling. At the bottom it then rolls itself back up the string."),
                fact = l("Beim Schlafen-Lassen dreht sich ein gutes Jojo unten mit über 100 Umdrehungen pro Sekunde in der lockeren Schlaufe.", "When 'sleeping', a good yo-yo spins at the bottom at over 100 revolutions per second in the loose loop."),
            )
        },
        lab("phy.schwungrad", Topic.Rotation, H, 0.5f) {
            val mass = pick(300.0, 500.0); val r = 0.5; val rpm = pick(10000.0, 15000.0)
            val i = 0.5 * mass * r * r; val w = 2 * PI * rpm / 60; val e = 0.5 * i * w * w
            task(
                question = l("Ein Schwungrad-Speicher ist eine Stahlscheibe mit ${n(mass, 0)} kg und 50 cm Radius, die mit ${n(rpm, 0)} Umdrehungen pro Minute rotiert. Wie viel Energie steckt darin (in kWh)?", "A flywheel energy store is a steel disc of ${n(mass, 0)} kg and 50 cm radius spinning at ${n(rpm, 0)} revolutions per minute. How much energy does it hold (in kWh)?"),
                steps = listOf(num(l("Energie in kWh", "Energy in kWh"), e / 3.6e6, "kWh", rel = 0.03, done = """§E \approx ${m(sig(e / 3.6e6))}\ \text{kWh}§""",
                    hints = listOf(l("Kernidee: Drehenergie ist ½·I·ω². Für eine Vollscheibe ist I = ½·m·R².", "Key idea: rotational energy is ½·I·ω². For a solid disc I = ½·m·R²."), """§E = \tfrac{1}{2} I \omega^2§"""))),
                solution = listOf("""§I = \tfrac{1}{2} \cdot ${m(mass, 0)} \cdot 0{,}5^2 = ${m(i, 1)}\ \text{kg m}^2,\quad \omega \approx ${m(w, 0)}\ \text{s}^{-1}§""", """§E = \tfrac{1}{2} \cdot ${m(i, 1)} \cdot ${m(w, 0)}^2\ \text{J} \approx ${sci(e)}\ \text{J} \approx ${m(sig(e / 3.6e6))}\ \text{kWh}§"""),
                background = l("Ein Schwungrad speichert Energie als Drehung. Weil die Energie mit dem Quadrat der Drehzahl wächst, lohnt es sich, es extrem schnell drehen zu lassen. Dann reißen aber die Fliehkräfte am Material. Moderne Speicher drehen in Vakuum auf Magnetlagern und geben ihre Energie in Sekunden ab, etwa um Stromnetze zu stabilisieren.", "A flywheel stores energy as rotation. Because the energy grows with the square of the speed, it pays to spin it extremely fast. But then the forces pulling outwards tear at the material. Modern stores spin in a vacuum on magnetic bearings and can release their energy within seconds, for example to stabilise power grids."),
                fact = l("Die Töpferscheibe ist eines der ältesten Schwungräder: Ein kräftiger Stoß hält sie lange am Laufen.", "The potter's wheel is one of the oldest flywheels: one strong push keeps it turning for a long time."),
            )
        },
        lab("phy.velodrom", Topic.Rotation, H, 0.5f) {
            val r = pick(25.0, 30.0); val ang = pick(42.0, 45.0)
            val v = sqrt(r * G2 * tan(Math.toRadians(ang)))
            task(
                question = l("In einer Radrennbahn ist die Kurve ${n(ang, 0)}° überhöht und hat ${n(r, 0)} m Radius. Bei welcher Geschwindigkeit braucht ein Radfahrer dort überhaupt keine Reibung, um nicht wegzurutschen?", "In a velodrome the bend is banked at ${n(ang, 0)}° and has a radius of ${n(r, 0)} m. At what speed does a cyclist need no friction at all there to avoid slipping?"),
                steps = listOf(num(l("Ideale Geschwindigkeit in km/h", "Ideal speed in km/h"), v * 3.6, "km/h", rel = 0.02, done = """§v \approx ${m(sig(v * 3.6))}\ \text{km/h}§""",
                    hints = listOf(l("Kernidee: Die Bahn drückt senkrecht zu ihrer Oberfläche, also schräg. Ihr waagrechter Anteil liefert die Zentripetalkraft, ihr senkrechter trägt das Gewicht.", "Key idea: the track pushes perpendicular to its surface, so at an angle. Its horizontal part provides the centripetal force, its vertical part carries the weight."), """§\tan\alpha = \dfrac{v^2}{r g}§"""))),
                solution = listOf("""§v = \sqrt{${m(r, 0)} \cdot 9{,}81 \cdot \tan ${m(ang, 0)}^\circ}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 1)}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v * 3.6))}\ \text{km/h}§"""),
                background = l("Auf flacher Straße muss die Reibung der Reifen die Kurvenkraft liefern. Eine überhöhte Kurve kippt die Stützkraft des Bodens nach innen, sodass sie selbst die Kurve erzwingt. Bei genau einer Geschwindigkeit passt alles ohne Reibung. Fährt man langsamer, rutscht man nach innen, schneller nach außen.", "On a flat road the friction of the tyres has to provide the cornering force. A banked bend tilts the supporting force of the ground inwards so that it forces the turn by itself. At exactly one speed everything works without friction. Slower than that you slide inwards, faster outwards."),
                fact = l("Wer in einer Radrennbahn zu langsam fährt, kann die Steilkurve gar nicht halten. Deshalb gibt es unten eine flache Rollzone.", "In a velodrome, anyone riding too slowly can't hold the steep bend at all. That's why there's a flat apron at the bottom."),
            )
        },
        lab("phy.hammerwurf", Topic.Rotation, M, 0.5f) {
            val mass = 7.26; val r = pick(1.8, 2.0); val v = pick(27.0, 29.0)
            val f = mass * v * v / r
            task(
                question = l("Ein Hammerwerfer schleudert die 7,26-kg-Kugel auf einem Kreis mit ${n(r)} m Radius und lässt sie bei ${n(v, 0)} m/s los. Mit welcher Kraft muss er kurz vor dem Abwurf ziehen?", "A hammer thrower swings the 7.26 kg ball in a circle with a radius of ${n(r)} m and releases it at ${n(v, 0)} m/s. How hard must he pull just before release?"),
                steps = listOf(num(l("Zugkraft in Newton", "Pulling force in newtons"), f, "N", rel = 0.02, done = """§F \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Die Kugel bleibt nur auf dem Kreis, weil der Draht sie ständig zur Mitte zieht.", "Key idea: the ball only stays on the circle because the wire constantly pulls it towards the centre."), """§F = \dfrac{m v^2}{r}§"""))),
                solution = listOf("""§F = \dfrac{7{,}26 \cdot ${m(v, 0)}^2}{${m(r)}}\ \text{N} \approx ${m(sig(f))}\ \text{N}§"""),
                background = l("Für eine Kreisbewegung braucht es eine Kraft zur Mitte, die mit dem Quadrat der Geschwindigkeit wächst. Lässt der Werfer los, fällt diese Kraft weg und die Kugel fliegt tangential weiter, nicht nach außen. Damit der Werfer nicht selbst nach vorn gerissen wird, lehnt er sich weit nach hinten.", "Circular motion needs a force towards the centre that grows with the square of the speed. When the thrower lets go, this force disappears and the ball flies off along the tangent, not outwards. To avoid being pulled forwards himself, the thrower leans far back."),
                fact = l("Das ist ungefähr das Gewicht von ${n(f / G2, 0)} kg, das der Werfer für einen Moment an seinen Händen hält.", "That's about the weight of ${n(f / G2, 0)} kg that the thrower holds in his hands for a moment."),
            )
        },
        lab("phy.riesenrad", Topic.Rotation, M, 0.6f) {
            val r = pick(15.0, 20.0, 30.0)
            val v = sqrt(G2 * r); val t = 2 * PI * r / v
            task(
                question = l("Ein Riesenrad hat ${n(r, 0)} m Radius. Wie schnell müsste es sich drehen, damit du dich ganz oben gerade schwerelos fühlst? Gib die Zeit für eine Umdrehung an.", "A Ferris wheel has a radius of ${n(r, 0)} m. How fast would it have to turn for you to feel just weightless at the very top? Give the time for one revolution."),
                steps = listOf(num(l("Umlaufzeit in Sekunden", "Time per revolution in seconds"), t, "s", rel = 0.02, done = """§T \approx ${m(t, 1)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Oben zeigt die Mitte nach unten. Schwerelos bist du, wenn die Schwerkraft allein genau die nötige Zentripetalkraft liefert.", "Key idea: at the top the centre is below you. You are weightless when gravity alone provides exactly the centripetal force needed."), """§\dfrac{v^2}{r} = g,\quad T = \dfrac{2\pi r}{v}§"""))),
                solution = listOf("""§v = \sqrt{9{,}81 \cdot ${m(r, 0)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 1)}\ \tfrac{\text{m}}{\text{s}}§""", """§T = \dfrac{2\pi \cdot ${m(r, 0)}}{${m(v, 1)}}\ \text{s} \approx ${m(t, 1)}\ \text{s}§"""),
                background = l("Was du als Gewicht spürst, ist die Kraft des Sitzes auf dich. Oben auf dem Rad brauchst du eine Kraft nach unten, um auf dem Kreis zu bleiben. Ist die Drehung genau schnell genug, übernimmt das die Schwerkraft allein und der Sitz drückt gar nicht mehr. Unten wäre es umgekehrt: Dort fühlst du dich doppelt so schwer.", "What you feel as weight is the force of the seat on you. At the top of the wheel you need a downward force to stay on the circle. If the wheel turns exactly fast enough, gravity alone takes over and the seat doesn't push at all. At the bottom it's the other way round: there you feel twice as heavy."),
                fact = l("Echte Riesenräder drehen sich zehn- bis fünfzigmal langsamer. Beim London Eye dauert eine Runde eine halbe Stunde.", "Real Ferris wheels turn ten to fifty times more slowly. On the London Eye one revolution takes half an hour."),
            )
        },
        lab("phy.cd", Topic.Rotation, M, 0.5f) {
            val v = 1.2; val rIn = 0.025; val rOut = 0.058
            val nIn = v / (2 * PI * rIn) * 60; val nOut = v / (2 * PI * rOut) * 60
            task(
                question = l("Ein CD-Spieler liest die Daten immer mit derselben Bahngeschwindigkeit von 1,2 m/s. Die Spur beginnt innen bei 2,5 cm Radius und endet außen bei 5,8 cm.", "A CD player always reads the data at the same track speed of 1.2 m/s. The track starts inside at a radius of 2.5 cm and ends outside at 5.8 cm."),
                steps = listOf(
                    num(l("Wie viele Umdrehungen pro Minute am Anfang?", "How many revolutions per minute at the start?"), nIn, "1/min", rel = 0.02, done = """§n \approx ${m(nIn, 0)}\ \text{min}^{-1}§""",
                        hints = listOf(l("Kernidee: Pro Umdrehung läuft ein Kreisumfang unter dem Laser durch.", "Key idea: one circumference passes under the laser per revolution."), """§n = \dfrac{v}{2\pi r}§""")),
                    num(l("Und am Ende?", "And at the end?"), nOut, "1/min", rel = 0.02, done = """§n \approx ${m(nOut, 0)}\ \text{min}^{-1}§""",
                        hints = listOf(l("Außen ist der Umfang größer, also reichen weniger Umdrehungen.", "Outside the circumference is larger, so fewer revolutions are enough."), """§n = \dfrac{1{,}2}{2\pi \cdot 0{,}058} \cdot 60§""")),
                ),
                solution = listOf("""§n_i = \dfrac{1{,}2}{2\pi \cdot 0{,}025} \cdot 60 \approx ${m(nIn, 0)}\ \text{min}^{-1}§""", """§n_a = \dfrac{1{,}2}{2\pi \cdot 0{,}058} \cdot 60 \approx ${m(nOut, 0)}\ \text{min}^{-1}§"""),
                background = l("Bahngeschwindigkeit und Drehzahl hängen über den Radius zusammen: v = 2π·r·n. Eine Schallplatte dreht sich immer gleich schnell, dadurch laufen die inneren Rillen langsamer unter der Nadel durch. Die CD macht es umgekehrt und passt die Drehzahl laufend an. So passen innen und außen gleich viele Daten auf jeden Millimeter Spur.", "Track speed and rotation rate are linked through the radius: v = 2π·r·n. A vinyl record always spins at the same rate, so the inner grooves pass more slowly under the needle. The CD does the opposite and keeps adjusting its spin. That way the same amount of data fits on every millimetre of track, inside and out."),
                fact = l("Die Spur einer CD ist über 5 km lang, spiralförmig auf einer Scheibe von 12 cm.", "The track of a CD is over 5 km long, wound in a spiral on a 12 cm disc."),
            )
        },
        lab("phy.buecherstapel", Topic.Rotation, X, 0.7f) {
            val nb = pick(4, 5); var over = 0.0
            for (k in 1..nb) over += 1.0 / (2 * k)
            task(
                question = l("Du stapelst ${nb} gleiche Bücher am Tischrand so, dass das oberste möglichst weit übersteht, ohne dass etwas kippt. Wie weit kann das oberste Buch über die Tischkante ragen, in Buchlängen?", "You stack ${nb} identical books at the edge of a table so that the top one sticks out as far as possible without anything tipping. How far can the top book reach beyond the table edge, in book lengths?"),
                steps = listOf(num(l("Überhang in Buchlängen", "Overhang in book lengths"), over, "", rel = 0.01, done = """§\approx ${m(over, 3)}§""",
                    hints = listOf(l("Kernidee: Arbeite von oben nach unten. Das oberste Buch darf mit seiner Mitte genau auf der Kante des zweiten liegen: ½. Die oberen zwei zusammen dürfen ¼ über das dritte ragen, dann ⅙ …", "Key idea: work from the top down. The top book can rest with its middle exactly on the edge of the second: ½. The top two together can stick out ¼ beyond the third, then ⅙ …"), """§s = \dfrac{1}{2}\left(1 + \dfrac{1}{2} + \dfrac{1}{3} + \dots + \dfrac{1}{n}\right)§"""))),
                solution = listOf("""§s = \tfrac{1}{2} + \tfrac{1}{4} + \tfrac{1}{6} + \dots \approx ${m(over, 3)}§"""),
                background = l("Ein Stapel kippt nicht, solange der gemeinsame Schwerpunkt aller Bücher darüber auf dem Buch darunter liegt. Jedes weitere Buch unten verschiebt alles ein kleineres Stück weiter hinaus. Die Summe ist die harmonische Reihe, und die wächst unendlich, nur sehr langsam. Schon mit 4 Büchern hängt das oberste vollständig in der Luft.", "A stack doesn't tip as long as the combined centre of mass of all the books above lies over the book below. Each extra book at the bottom shifts everything out by a smaller amount. The sum is the harmonic series, which grows without limit, only very slowly. With just 4 books the top one hangs completely in the air."),
                fact = l("Für einen Überhang von 10 Buchlängen bräuchte man über 270 Millionen Bücher.", "For an overhang of 10 book lengths you would need over 270 million books."),
            )
        },
        lab("phy.katze", Topic.Rotation, X, 0.6f) {
            task(
                question = l("Eine Katze fällt mit dem Rücken nach unten, ohne sich zu drehen, und landet trotzdem auf den Pfoten. Ihr Drehimpuls ist die ganze Zeit null. Wie schafft sie das?", "A cat falls with its back downwards, without spinning, and still lands on its paws. Its angular momentum is zero the whole time. How does it manage that?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Sie verdreht Vorder- und Hinterkörper nacheinander gegeneinander und macht dabei mal die Vorder-, mal die Hinterbeine lang, um die Trägheitsmomente zu ändern.", "It twists its front and back halves against each other in turn, stretching out first the front and then the back legs to change the moments of inertia."),
                    listOf(l("Sie stößt sich mit dem Schwanz an der Luft ab.", "It pushes off against the air with its tail."), l("Sie bekommt beim Loslassen immer einen kleinen Drehschwung mit.", "It always picks up a little spin when it is let go."), l("Der Drehimpuls bleibt bei Lebewesen nicht erhalten.", "Angular momentum isn't conserved for living things.")),
                    hints = listOf(l("Kernidee: Der Gesamtdrehimpuls bleibt null. Aber zwei Körperhälften können sich gegeneinander drehen, und wie weit sich jede dreht, hängt von ihrem Trägheitsmoment ab.", "Key idea: the total angular momentum stays zero. But two halves of a body can turn against each other, and how far each one turns depends on its moment of inertia."), l("Eine Hälfte mit eingezogenen Beinen dreht sich weit, eine mit ausgestreckten kaum.", "A half with legs tucked in turns a long way; one with legs stretched out hardly turns at all.")))),
                solution = listOf(l("Mit angezogenen Vorderbeinen dreht sich der Vorderkörper weit, der Hinterkörper mit gestreckten Beinen nur wenig zurück. Dann umgekehrt. Am Ende hat sich die ganze Katze gedreht, ohne je Drehimpuls zu haben.", "With the front legs tucked in, the front half turns a long way and the back half with outstretched legs turns back only a little. Then the other way round. In the end the whole cat has turned without ever having any angular momentum.")),
                background = l("Drehimpuls ist Trägheitsmoment mal Drehgeschwindigkeit. Bei einem einzelnen starren Körper hieße Drehimpuls null: keine Drehung. Ein beweglicher Körper kann aber seine Form ändern, und dann kann ein Bewegungsablauf die Ausrichtung ändern, obwohl in jedem Moment der Gesamtdrehimpuls null ist. Astronauten und Turmspringer nutzen denselben Trick.", "Angular momentum is moment of inertia times rotation speed. For a single rigid body, zero angular momentum would mean no rotation. But a flexible body can change its shape, and then a sequence of movements can change its orientation even though the total angular momentum is zero at every moment. Astronauts and high divers use the same trick."),
                fact = l("Physiker untersuchten das Problem schon 1894 mit Fotoserien fallender Katzen in Paris.", "Physicists were already studying the problem in 1894 with photo series of falling cats in Paris."),
            )
        },
        lab("phy.gangschaltung", Topic.Rotation, E, 0.4f) {
            val front = pick(44, 50, 34); val rear = pick(11, 17); val circ = 2.1
            val ratio = front.toDouble() / rear; val s = ratio * circ
            task(
                question = l("Dein Fahrrad hat vorn ein Kettenblatt mit ${front} Zähnen, hinten ist ein Ritzel mit ${rear} Zähnen eingelegt. Der Reifen hat 2,1 m Umfang. Wie weit fährst du mit einer Kurbelumdrehung?", "Your bike has a chainring with ${front} teeth at the front, and a sprocket with ${rear} teeth is selected at the back. The tyre has a circumference of 2.1 m. How far do you travel with one turn of the pedals?"),
                steps = listOf(num(l("Strecke in Metern", "Distance in metres"), s, "m", rel = 0.02, done = """§s \approx ${m(s, 2)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Die Kette schiebt pro Kurbelumdrehung ${front} Zähne weiter. Das Hinterrad dreht sich so oft, wie ${rear} in ${front} hineinpasst.", "Key idea: per pedal turn the chain moves on by ${front} teeth. The rear wheel turns as many times as ${rear} goes into ${front}."), """§s = \dfrac{${front}}{${rear}} \cdot 2{,}1\ \text{m}§"""))),
                solution = listOf("""§\dfrac{${front}}{${rear}} \approx ${m(ratio, 2)}\ \text{Umdrehungen}§""".let { l(it, it.replace("Umdrehungen", "turns")) }, """§s = ${m(ratio, 2)} \cdot 2{,}1\ \text{m} \approx ${m(s, 2)}\ \text{m}§"""),
                background = l("Zahnräder mit Kette sind Hebel im Kreis: Ein großes Kettenblatt und ein kleines Ritzel bringen viel Weg pro Tritt, brauchen aber viel Kraft. Am Berg schaltet man umgekehrt, dann dreht man öfter, aber leichter. Die Leistung, also Kraft mal Geschwindigkeit, bleibt dieselbe, die Gangschaltung verteilt sie nur anders.", "Gears with a chain are levers going round: a big chainring and a small sprocket give a lot of distance per pedal stroke but need a lot of force. On a hill you shift the other way, so you pedal more often but more easily. The power, force times speed, stays the same; the gears just split it up differently."),
                fact = l("Die ersten Fahrräder hatten kein Getriebe. Deshalb war das Vorderrad des Hochrads so riesig: Ein großes Rad war der einzige Weg zu mehr Strecke pro Tritt.", "The first bicycles had no gears. That's why the front wheel of the penny-farthing was so huge: a big wheel was the only way to get more distance per pedal stroke."),
            )
        },
    )
}
