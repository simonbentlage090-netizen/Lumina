package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

/* Physik-Ausbau (seit 13.2): Stromkreise, Ladung & Felder, Magnetismus. Alle Texte selbst geschrieben. */

private const val EL = 1.602e-19
private const val K_C = 8.99e9
private const val MU0 = 4 * PI * 1e-7
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object PhysNew5 {
    val all: List<TaskDef> get() = circuits + fields + magnetism

    private val circuits = listOf(
        lab("phy.vogel", Topic.Electricity, E, 0.4f) {
            task(
                question = l("Ein Vogel sitzt mit beiden Füßen auf einer Hochspannungsleitung mit 110.000 Volt. Warum passiert ihm nichts?", "A bird sits with both feet on a high-voltage power line at 110,000 volts. Why doesn't anything happen to it?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Zwischen seinen Füßen gibt es kaum Spannung, also fließt kein Strom durch ihn.", "There is hardly any voltage between its feet, so no current flows through it."),
                    listOf(l("Seine Federn isolieren ihn.", "Its feathers insulate it."), l("Vögel haben einen sehr hohen Körperwiderstand.", "Birds have a very high body resistance."), l("Der Strom fließt zu schnell, um ihn zu treffen.", "The current flows too fast to hit it.")),
                    hints = listOf(l("Kernidee: Strom fließt nur, wenn es einen Spannungsunterschied gibt. Spannung ist immer ein Unterschied zwischen zwei Punkten.", "Key idea: current only flows when there is a voltage difference. Voltage is always a difference between two points."), l("Beide Füße berühren denselben Draht.", "Both feet touch the same wire.")))),
                solution = listOf(l("Beide Füße liegen fast auf demselben Potenzial. Für einen Strom durch den Vogel bräuchte es einen Weg zu einem Punkt mit anderem Potenzial, etwa zur Erde oder zu einer zweiten Leitung.", "Both feet are at almost the same potential. A current through the bird would need a path to a point at a different potential, such as the ground or a second line.")),
                background = l("110.000 Volt heißt: Die Leitung liegt so viel höher als die Erde. Gefährlich wird es erst, wenn ein Körper zwei Punkte mit verschiedenem Potenzial verbindet. Große Vögel, die mit den Flügeln einen Mast und die Leitung zugleich berühren, sterben deshalb tatsächlich. Netzbetreiber bauen inzwischen Abstandshalter, um sie zu schützen.", "110,000 volts means the line is that much higher than the ground. It only becomes dangerous when a body connects two points at different potentials. Large birds whose wings touch a pylon and the line at the same time really do die. Grid operators now fit spacers to protect them."),
                fact = l("Deshalb dürfen auch Arbeiter an Hochspannungsleitungen unter Spannung arbeiten: Sie werden aus dem Hubschrauber auf das Potenzial der Leitung gebracht.", "That's also why workers can work on live high-voltage lines: they are brought up to the potential of the line from a helicopter."),
            )
        },
        lab("phy.lichterkette", Topic.Electricity, E, 0.4f) {
            val per = pick(11.5, 23.0, 14.375); val nb = 230 / per
            task(
                question = l("Eine alte Lichterkette hängt an 230 V. Alle Lämpchen sind in Reihe geschaltet, und jedes ist für ${n(per, 1)} V gebaut. Wie viele Lämpchen hat die Kette?", "An old string of fairy lights is plugged into 230 V. All the bulbs are connected in series, and each one is made for ${n(per, 1)} V. How many bulbs does the string have?"),
                steps = listOf(num(l("Anzahl Lämpchen", "Number of bulbs"), nb, "", abs = 0.0, done = """§N = ${m(nb, 0)}§""",
                    hints = listOf(l("Kernidee: In einer Reihenschaltung teilen sich alle Lämpchen die Netzspannung.", "Key idea: in a series circuit all the bulbs share the mains voltage."), """§N = \dfrac{230\ \text{V}}{${m(per, 1)}\ \text{V}}§"""))),
                solution = listOf("""§N = \dfrac{230}{${m(per, 1)}} = ${m(nb, 0)}§"""),
                background = l("In einer Reihe fließt durch jedes Lämpchen derselbe Strom, und die Spannungen addieren sich. Brennt eines durch, ist der Kreis unterbrochen, und alle gehen aus. Deshalb musste man früher jedes einzelne Lämpchen durchprobieren. Moderne Ketten haben Lämpchen, die beim Durchbrennen kurzschließen, oder sind parallel geschaltet.", "In series, the same current flows through every bulb and the voltages add up. If one burns out, the circuit is broken and they all go out. That's why people used to have to test every single bulb. Modern strings have bulbs that short-circuit when they fail, or are wired in parallel."),
                fact = l("Die ersten elektrischen Weihnachtsbaumlichter zeigte 1882 ein Mitarbeiter von Thomas Edison in New York.", "The first electric Christmas tree lights were shown in New York in 1882 by an associate of Thomas Edison."),
            )
        },
        lab("phy.zitrone", Topic.Electricity, E, 0.3f) {
            val need = pick(3.0, 2.0, 5.0); val per = 0.9; val nl = ceil(need / per)
            task(
                question = l("Eine Zitrone mit einem Kupfer- und einem Zinkblech liefert etwa 0,9 V. Wie viele Zitronen musst du mindestens in Reihe schalten, um ${n(need, 0)} V zu bekommen?", "A lemon with a copper strip and a zinc strip delivers about 0.9 V. What is the smallest number of lemons you have to connect in series to get ${n(need, 0)} V?"),
                steps = listOf(num(l("Anzahl Zitronen", "Number of lemons"), nl, "", abs = 0.0, done = """§N = ${m(nl, 0)}§""",
                    hints = listOf(l("Kernidee: In Reihe addieren sich die Spannungen. Teile und runde auf, denn eine halbe Zitrone gibt es nicht.", "Key idea: in series the voltages add up. Divide and round up, since there's no such thing as half a lemon."), """§\dfrac{${m(need, 0)}}{0{,}9} \approx ${m(need / per, 2)}§"""))),
                solution = listOf("""§\dfrac{${m(need, 0)}}{0{,}9} \approx ${m(need / per, 2)} \Rightarrow N = ${m(nl, 0)}\ (${m(nl * per, 1)}\ \text{V})§"""),
                background = l("Eine Batterie braucht zwei verschiedene Metalle und eine leitende Flüssigkeit dazwischen. Zink gibt leichter Elektronen ab als Kupfer, und dieser Unterschied ist die Spannung. Die Zitrone liefert nur den sauren Saft. Die Spannung hängt von den Metallen ab, nicht von der Größe der Frucht. Eine Kartoffel geht auch.", "A battery needs two different metals and a conducting liquid between them. Zinc gives up electrons more easily than copper, and that difference is the voltage. The lemon only supplies the acidic juice. The voltage depends on the metals, not on the size of the fruit. A potato works too."),
                fact = l("Der Strom ist dabei winzig. Für eine LED reicht es, für ein Handy bräuchte man hunderte Zitronen und viel Geduld.", "The current is tiny, though. It's enough for an LED; for a phone you'd need hundreds of lemons and a lot of patience."),
            )
        },
        lab("phy.stromzaehler", Topic.Electricity, E, 0.5f) {
            task(
                question = l("Der Stromzähler im Keller zählt immer weiter, und am Jahresende bezahlst du danach. Was misst er eigentlich?", "The electricity meter in the basement keeps counting, and at the end of the year you pay according to it. What does it actually measure?"),
                steps = listOf(pick1(l("Er misst …", "It measures …"), l("die verbrauchte Energie in Kilowattstunden", "the energy used, in kilowatt-hours"), listOf(l("die Leistung in Kilowatt", "the power, in kilowatts"), l("die Stromstärke in Ampere", "the current, in amperes"), l("die Spannung in Volt", "the voltage, in volts")),
                    hints = listOf(l("Kernidee: Ein Föhn, der eine Minute läuft, kostet weniger als einer, der eine Stunde läuft. Was unterscheidet die beiden?", "Key idea: a hair dryer running for one minute costs less than one running for an hour. What's the difference between them?"), l("Leistung mal Zeit", "Power times time")))),
                solution = listOf(l("Bezahlt wird Energie: Leistung mal Zeit. 1 kWh ist ein Gerät mit 1000 W, das eine Stunde läuft.", "You pay for energy: power times time. 1 kWh is a device using 1000 W running for one hour.")),
                background = l("Leistung sagt, wie schnell ein Gerät Energie umsetzt, Energie, wie viel insgesamt umgesetzt wurde. Ein Wasserkocher hat viel Leistung, läuft aber kurz. Ein Kühlschrank hat wenig Leistung, läuft aber das ganze Jahr. Erst die Kilowattstunden zeigen, wer am Ende mehr kostet. Die Kilowattstunde ist übrigens eine Energieeinheit: 3,6 Millionen Joule.", "Power says how quickly a device uses energy; energy says how much was used in total. A kettle has a lot of power but runs briefly. A fridge has little power but runs all year. Only the kilowatt-hours show which costs more in the end. The kilowatt-hour is, by the way, a unit of energy: 3.6 million joules."),
                fact = l("Ein durchschnittlicher Zweipersonenhaushalt in Deutschland verbraucht etwa 2500 kWh im Jahr.", "An average two-person household in Germany uses about 2500 kWh a year."),
            )
        },
        lab("phy.drahtwiderstand", Topic.Electricity, M, 0.5f) {
            val len = pick(25.0, 50.0); val a = pick(1.5, 2.5); val rho = 0.0175
            val r = rho * 2 * len / a
            task(
                question = l("Ein Verlängerungskabel ist ${n(len, 0)} m lang und hat zwei Kupferadern mit je ${n(a, 1)} mm² Querschnitt. Der Strom fließt hin und zurück. Wie groß ist der Widerstand (Kupfer: 0,0175 Ω·mm²/m)?", "An extension cable is ${n(len, 0)} m long and has two copper cores, each with a cross-section of ${n(a, 1)} mm². The current flows there and back. What is the resistance (copper: 0.0175 Ω·mm²/m)?"),
                steps = listOf(num(l("Widerstand in Ohm", "Resistance in ohms"), r, "Ω", rel = 0.02, done = """§R \approx ${m(r, 2)}\ \Omega§""",
                    hints = listOf(l("Kernidee: Der Widerstand wächst mit der Länge und sinkt mit dem Querschnitt. Der Strom läuft durch beide Adern, also die doppelte Länge.", "Key idea: resistance grows with length and falls with cross-section. The current runs through both cores, so double the length."), """§R = \rho \dfrac{2L}{A}§"""))),
                solution = listOf("""§R = 0{,}0175 \cdot \dfrac{2 \cdot ${m(len, 0)}}{${m(a, 1)}}\ \Omega \approx ${m(r, 2)}\ \Omega§"""),
                background = l("Ein Draht ist wie ein Rohr für Elektronen: Je länger, desto mehr Reibung, je dicker, desto leichter kommen sie durch. Bei großen Strömen erwärmt sich ein zu dünnes Kabel spürbar. Deshalb soll man Kabeltrommeln bei hoher Last ganz abrollen, sonst staut sich die Wärme in den Windungen.", "A wire is like a pipe for electrons: the longer it is, the more friction; the thicker, the more easily they get through. With large currents a cable that is too thin warms up noticeably. That's why you should fully unwind a cable reel under high load, otherwise the heat builds up in the coils."),
                fact = l("Bei 10 A gehen in diesem Kabel etwa ${n(r * 100, 0)} W als Wärme verloren.", "At 10 A, about ${n(r * 100, 0)} W are lost as heat in this cable."),
            )
        },
        lab("phy.ledvorwiderstand", Topic.Electricity, M, 0.4f) {
            val u = pick(5.0, 9.0, 12.0); val uLed = 2.0; val i = 0.02
            val r = (u - uLed) / i
            task(
                question = l("Eine rote LED braucht 2,0 V und 20 mA. Du willst sie an einer ${n(u, 0)}-V-Quelle betreiben. Welchen Vorwiderstand brauchst du?", "A red LED needs 2.0 V and 20 mA. You want to run it from a ${n(u, 0)} V source. What series resistor do you need?"),
                steps = listOf(num(l("Widerstand in Ohm", "Resistance in ohms"), r, "Ω", rel = 0.02, done = """§R = ${m(r, 0)}\ \Omega§""",
                    hints = listOf(l("Kernidee: Der Widerstand muss die überschüssige Spannung aufnehmen, und durch ihn fließt derselbe Strom wie durch die LED.", "Key idea: the resistor has to take up the extra voltage, and the same current flows through it as through the LED."), """§R = \dfrac{U - U_{LED}}{I}§"""))),
                solution = listOf("""§R = \dfrac{${m(u, 0)} - 2}{0{,}02}\ \Omega = ${m(r, 0)}\ \Omega§"""),
                background = l("Eine LED ist kein Widerstand, der sich selbst begrenzt: Schon etwas mehr Spannung lässt den Strom steil ansteigen, und sie brennt durch. Der Vorwiderstand teilt sich mit ihr die Spannung und hält den Strom fest. In echten Schaltungen nimmt man den nächstgrößeren Normwert, damit die LED lieber etwas schwächer leuchtet.", "An LED isn't a resistor that limits itself: just a bit more voltage makes the current rise steeply, and it burns out. The series resistor shares the voltage with it and holds the current steady. In real circuits you take the next larger standard value so that the LED glows a little dimmer rather than too bright."),
                fact = l("Die Farbe einer LED hängt vom Halbleitermaterial ab. Blaue LEDs waren lange unmöglich, ihre Erfinder bekamen 2014 den Physik-Nobelpreis.", "The colour of an LED depends on the semiconductor material. Blue LEDs were impossible for a long time; their inventors won the 2014 Nobel Prize in Physics."),
            )
        },
        lab("phy.koerperstrom", Topic.Electricity, M, 0.5f) {
            val r = pick(1000.0, 2000.0, 5000.0); val i = 230 / r * 1000
            task(
                question = l("Mit nassen Händen hat dein Körper zwischen Hand und Fuß etwa ${n(r, 0)} Ω Widerstand. Welcher Strom fließt, wenn du 230 V berührst und barfuß auf der Erde stehst?", "With wet hands your body has a resistance of about ${n(r, 0)} Ω between hand and foot. What current flows if you touch 230 V while standing barefoot on the ground?"),
                steps = listOf(num(l("Strom in Milliampere", "Current in milliamperes"), i, "mA", rel = 0.02, done = """§I \approx ${m(i, 0)}\ \text{mA}§""",
                    hints = listOf(l("Kernidee: Ohmsches Gesetz, dann von Ampere in Milliampere umrechnen.", "Key idea: Ohm's law, then convert from amperes to milliamperes."), """§I = \dfrac{U}{R}§"""))),
                solution = listOf("""§I = \dfrac{230\ \text{V}}{${m(r, 0)}\ \Omega} \approx ${m(i / 1000, 3)}\ \text{A} = ${m(i, 0)}\ \text{mA}§"""),
                background = l("Gefährlich ist nicht die Spannung allein, sondern der Strom durch den Körper. Ab etwa 10 mA verkrampfen sich die Muskeln, man kann nicht mehr loslassen, ab etwa 50 mA durch die Brust droht Herzkammerflimmern. Trockene Haut schützt etwas, nasse kaum. Fehlerstromschalter trennen den Kreis schon bei 30 mA in Sekundenbruchteilen.", "What's dangerous isn't the voltage alone but the current through the body. From about 10 mA the muscles cramp and you can't let go; from about 50 mA through the chest, ventricular fibrillation threatens. Dry skin protects a little, wet skin hardly at all. Residual-current circuit breakers cut the circuit at just 30 mA within a fraction of a second."),
                fact = l("Deshalb gehören Föhn und Steckdosen nie in Wannennähe, und in Bädern sind Fehlerstromschalter Pflicht.", "That's why hair dryers and sockets never belong near the bath, and residual-current breakers are compulsory in bathrooms."),
            )
        },
        lab("phy.eauto", Topic.Electricity, M, 0.5f) {
            val kwh = pick(60.0, 75.0, 50.0)
            val t1 = kwh / 11; val t2 = kwh / 2.3
            task(
                question = l("Ein Elektroauto hat einen Akku mit ${n(kwh, 0)} kWh. Er ist leer und soll voll geladen werden (Verluste vernachlässigt).", "An electric car has a battery of ${n(kwh, 0)} kWh. It is empty and is to be fully charged (losses neglected)."),
                steps = listOf(
                    num(l("Wie viele Stunden dauert es an einer Wallbox mit 11 kW?", "How many hours does it take on an 11 kW wallbox?"), t1, "h", rel = 0.02, done = """§t \approx ${m(t1, 1)}\ \text{h}§""",
                        hints = listOf(l("Kernidee: Energie durch Leistung ist die Zeit.", "Key idea: energy divided by power is the time."), """§t = \dfrac{E}{P}§""")),
                    num(l("Und an einer normalen Steckdose mit 230 V und 10 A?", "And on a normal socket with 230 V and 10 A?"), t2, "h", rel = 0.02, done = """§t \approx ${m(t2, 0)}\ \text{h}§""",
                        hints = listOf(l("Erst die Leistung der Steckdose ausrechnen.", "First work out the power of the socket."), """§P = U \cdot I = 2{,}3\ \text{kW}§""")),
                ),
                solution = listOf("""§t_1 = \dfrac{${m(kwh, 0)}\ \text{kWh}}{11\ \text{kW}} \approx ${m(t1, 1)}\ \text{h}§""", """§t_2 = \dfrac{${m(kwh, 0)}\ \text{kWh}}{2{,}3\ \text{kW}} \approx ${m(t2, 0)}\ \text{h}§"""),
                background = l("Eine Kilowattstunde ist das, was eine Leistung von einem Kilowatt in einer Stunde liefert. Daher ergibt sich die Ladezeit direkt aus Akkugröße durch Ladeleistung. Schnellladesäulen schaffen über 150 kW, aber nicht die ganze Zeit: Ist der Akku fast voll, wird zum Schutz der Zellen gedrosselt.", "A kilowatt-hour is what a power of one kilowatt delivers in one hour. So the charging time follows directly from battery size over charging power. Fast chargers manage over 150 kW, but not the whole time: when the battery is nearly full, the power is throttled to protect the cells."),
                fact = l("Mit ${n(kwh, 0)} kWh könnte man einen Haushalt etwa eine Woche lang mit Strom versorgen.", "With ${n(kwh, 0)} kWh you could supply a household with electricity for about a week."),
            )
        },
        lab("phy.spannungsteiler", Topic.Electricity, M, 0.6f) {
            val u = 12.0; val uOut = pick(5.0, 3.0, 4.0); val r2 = 10.0
            val r1 = r2 * (u - uOut) / uOut
            task(
                question = l("Ein Sensor braucht ${n(uOut, 0)} V, du hast aber 12 V. Du baust einen Spannungsteiler aus zwei Widerständen in Reihe und greifst die Spannung am unteren ab, der 10 kΩ hat. Wie groß muss der obere sein?", "A sensor needs ${n(uOut, 0)} V, but you have 12 V. You build a voltage divider from two resistors in series and take the voltage across the lower one, which is 10 kΩ. How big must the upper one be?"),
                steps = listOf(num(l("Oberer Widerstand in kΩ", "Upper resistor in kΩ"), r1, "kΩ", rel = 0.02, done = """§R_1 = ${m(r1, 1)}\ \text{k}\Omega§""",
                    hints = listOf(l("Kernidee: Durch beide fließt derselbe Strom, die Spannungen verhalten sich wie die Widerstände.", "Key idea: the same current flows through both, so the voltages are in the ratio of the resistances."), """§\dfrac{U_2}{U} = \dfrac{R_2}{R_1 + R_2}§"""))),
                solution = listOf("""§\dfrac{${m(uOut, 0)}}{12} = \dfrac{10}{R_1 + 10} \Rightarrow R_1 = ${m(r1, 1)}\ \text{k}\Omega§"""),
                background = l("In einer Reihenschaltung teilt sich die Spannung im Verhältnis der Widerstände auf. So bekommt man jede gewünschte Teilspannung. Ein Drehregler, das Potentiometer, ist ein Spannungsteiler mit verschiebbarem Abgriff: So funktionierten früher Lautstärkeregler. Für große Ströme taugt der Teiler aber nicht, weil die Last die Aufteilung verändert.", "In a series circuit the voltage divides in the ratio of the resistances. That way you can get any partial voltage you want. A rotary knob, the potentiometer, is a voltage divider with a sliding tap: that's how volume controls used to work. But a divider is no good for large currents, because the load changes the split."),
                fact = l("Viele Sensoren sind selbst ein Teil eines Spannungsteilers: Ein lichtabhängiger Widerstand ändert so die Spannung, wenn es dunkel wird.", "Many sensors are themselves part of a voltage divider: a light-dependent resistor changes the voltage this way when it gets dark."),
            )
        },
        lab("phy.drift", Topic.Electricity, H, 0.6f) {
            val i = pick(1.0, 2.0, 10.0); val a = pick(1.0, 1.5) * 1e-6; val nE = 8.5e28
            val v = i / (nE * EL * a) * 1000
            task(
                question = l("Durch einen Kupferdraht mit ${n(a * 1e6, 1)} mm² Querschnitt fließen ${n(i, 0)} A. In jedem Kubikmeter Kupfer sind 8,5 · 10²⁸ freie Elektronen. Wie schnell wandern sie im Mittel durch den Draht?", "${n(i, 0)} A flow through a copper wire with a cross-section of ${n(a * 1e6, 1)} mm². Every cubic metre of copper holds 8.5 · 10²⁸ free electrons. How fast do they drift through the wire on average?"),
                steps = listOf(num(l("Driftgeschwindigkeit in mm/s", "Drift speed in mm/s"), v, "mm/s", rel = 0.03, done = """§v \approx ${m(sig(v, 2), 3)}\ \text{mm/s}§""",
                    hints = listOf(l("Kernidee: Der Strom ist die Ladung, die pro Sekunde durch den Querschnitt kommt: Elektronendichte mal Ladung mal Fläche mal Geschwindigkeit.", "Key idea: the current is the charge passing through the cross-section per second: electron density times charge times area times speed."), """§I = n e A v \Rightarrow v = \dfrac{I}{n e A}§"""))),
                solution = listOf("""§v = \dfrac{${m(i, 0)}}{8{,}5 \cdot 10^{28} \cdot 1{,}6 \cdot 10^{-19} \cdot ${sci(a)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v, 2), 3)}\ \tfrac{\text{mm}}{\text{s}}§"""),
                background = l("Elektronen kriechen langsamer als eine Schnecke durch den Draht. Trotzdem geht das Licht sofort an, wenn du den Schalter drückst. Der Grund: Der Draht ist schon voller Elektronen, und das elektrische Feld, das sie anschiebt, breitet sich fast mit Lichtgeschwindigkeit aus. Wie bei einem vollen Wasserschlauch kommt sofort vorn Wasser heraus.", "Electrons crawl through the wire more slowly than a snail. Yet the light comes on instantly when you press the switch. The reason: the wire is already full of electrons, and the electric field that pushes them spreads out at nearly the speed of light. As with a hose full of water, water comes out at the front straight away."),
                fact = l("Bei Wechselstrom kommen die Elektronen gar nicht voran: Sie zittern 50-mal pro Sekunde einen Bruchteil eines Mikrometers hin und her.", "With alternating current the electrons don't get anywhere at all: they jiggle back and forth by a fraction of a micrometre 50 times a second."),
            )
        },
        lab("phy.innenwiderstand", Topic.Electricity, H, 0.5f) {
            val u0 = 9.0; val ri = pick(1.0, 2.0, 3.0); val rl = pick(10.0, 5.0)
            val i = u0 / (ri + rl); val uk = i * rl
            task(
                question = l("Eine 9-V-Batterie hat einen Innenwiderstand von ${n(ri, 0)} Ω. Du schließt einen Verbraucher mit ${n(rl, 0)} Ω an. Welche Spannung misst du jetzt an den Polen?", "A 9 V battery has an internal resistance of ${n(ri, 0)} Ω. You connect a load of ${n(rl, 0)} Ω. What voltage do you now measure across the terminals?"),
                steps = listOf(num(l("Klemmenspannung in Volt", "Terminal voltage in volts"), uk, "V", rel = 0.02, done = """§U_K \approx ${m(uk, 2)}\ \text{V}§""",
                    hints = listOf(l("Kernidee: Innen- und Lastwiderstand liegen in Reihe. Ein Teil der Spannung bleibt schon in der Batterie hängen.", "Key idea: the internal and load resistances are in series. Part of the voltage is already lost inside the battery."), """§I = \dfrac{U_0}{R_i + R_L},\quad U_K = I \cdot R_L§"""))),
                solution = listOf("""§I = \dfrac{9}{${m(ri, 0)} + ${m(rl, 0)}}\ \text{A} \approx ${m(i, 3)}\ \text{A}§""", """§U_K = ${m(i, 3)} \cdot ${m(rl, 0)}\ \text{V} \approx ${m(uk, 2)}\ \text{V}§"""),
                background = l("Jede echte Spannungsquelle hat einen Innenwiderstand: Die Chemie in der Batterie kann nicht beliebig schnell liefern. Je mehr Strom man zieht, desto mehr Spannung geht innen verloren. Deshalb zeigt eine fast leere Batterie im Leerlauf noch 9 V, bricht aber unter Last ein, und deshalb wird eine Batterie beim Kurzschluss heiß.", "Every real voltage source has an internal resistance: the chemistry in the battery can't deliver arbitrarily fast. The more current you draw, the more voltage is lost inside. That's why an almost flat battery still shows 9 V with nothing connected but collapses under load, and why a battery gets hot when short-circuited."),
                fact = l("Autobatterien haben einen winzigen Innenwiderstand von wenigen Milliohm. Nur so können sie dem Anlasser kurz über 200 A liefern.", "Car batteries have a tiny internal resistance of a few milliohms. That's the only way they can briefly supply the starter motor with over 200 A."),
            )
        },
        lab("phy.gluehbirne", Topic.Electricity, H, 0.6f) {
            val p = pick(60.0, 100.0, 40.0); val rHot = 230 * 230 / p; val rCold = rHot / 12
            val iHot = 230 / rHot; val iCold = 230 / rCold
            task(
                question = l("Eine Glühbirne hat 230 V und ${n(p, 0)} W. Kalt ist der Widerstand ihres Wolframdrahts zwölfmal kleiner als im glühenden Zustand.", "A light bulb is rated 230 V and ${n(p, 0)} W. When cold, the resistance of its tungsten filament is twelve times smaller than when glowing."),
                steps = listOf(
                    num(l("Wie groß ist der Widerstand im Betrieb (in Ω)?", "What is the resistance in operation (in Ω)?"), rHot, "Ω", rel = 0.02, done = """§R \approx ${m(rHot, 0)}\ \Omega§""",
                        hints = listOf(l("Kernidee: Aus Leistung und Spannung folgt der Widerstand.", "Key idea: the resistance follows from power and voltage."), """§P = \dfrac{U^2}{R} \Rightarrow R = \dfrac{U^2}{P}§""")),
                    num(l("Welcher Strom fließt im ersten Moment nach dem Einschalten (in A)?", "What current flows in the first moment after switching on (in A)?"), iCold, "A", rel = 0.02, done = """§I \approx ${m(iCold, 2)}\ \text{A}§""",
                        hints = listOf(l("Im ersten Moment ist der Draht noch kalt.", "In the first moment the filament is still cold."), """§I = \dfrac{U}{R / 12}§""")),
                ),
                solution = listOf("""§R = \dfrac{230^2}{${m(p, 0)}}\ \Omega \approx ${m(rHot, 0)}\ \Omega§""", """§I_0 = \dfrac{230 \cdot 12}{${m(rHot, 0)}}\ \text{A} \approx ${m(iCold, 2)}\ \text{A}\quad (\text{statt } ${m(iHot, 2)}\ \text{A})§""".let { l(it, it.replace("statt", "instead of")) }),
                background = l("Der Widerstand von Metallen steigt mit der Temperatur, weil die stärker schwingenden Atome die Elektronen mehr behindern. Ein Glühdraht ist bei 2500 °C viel schlechter leitend als bei Zimmertemperatur. Beim Einschalten fließt deshalb kurz ein hoher Stromstoß. Genau dann brennen Glühbirnen meistens durch.", "The resistance of metals rises with temperature, because the more strongly vibrating atoms obstruct the electrons more. A filament at 2500 °C conducts much worse than at room temperature. That's why a large current surge flows briefly when it is switched on. That's exactly when light bulbs usually burn out."),
                fact = l("Die Centennial Light, eine Glühbirne in einer Feuerwache in Kalifornien, brennt seit 1901 fast ununterbrochen, vermutlich weil sie kaum ausgeschaltet wird.", "The Centennial Light, a bulb in a fire station in California, has been burning almost continuously since 1901, probably because it is hardly ever switched off."),
            )
        },
        lab("phy.wheatstone", Topic.Electricity, H, 0.6f) {
            val r1 = 100.0; val r2 = pick(150.0, 220.0); val r3 = pick(200.0, 330.0)
            val rx = r2 * r3 / r1
            task(
                question = l("In einer Brückenschaltung liegen zwei Spannungsteiler parallel: links R₁ = 100 Ω über R₂ = ${n(r2, 0)} Ω, rechts R₃ = ${n(r3, 0)} Ω über einem unbekannten Rₓ. Das Messgerät zwischen den beiden Mittelpunkten zeigt genau null. Wie groß ist Rₓ?", "In a bridge circuit two voltage dividers are in parallel: on the left R₁ = 100 Ω above R₂ = ${n(r2, 0)} Ω, on the right R₃ = ${n(r3, 0)} Ω above an unknown Rₓ. The meter between the two midpoints reads exactly zero. What is Rₓ?"),
                steps = listOf(num(l("Rₓ in Ohm", "Rₓ in ohms"), rx, "Ω", rel = 0.02, done = """§R_x = ${m(rx, 0)}\ \Omega§""",
                    hints = listOf(l("Kernidee: Null heißt, beide Mittelpunkte haben dieselbe Spannung. Dann teilen beide Seiten die Spannung im selben Verhältnis.", "Key idea: zero means both midpoints are at the same voltage. Then both sides divide the voltage in the same ratio."), """§\dfrac{R_2}{R_1} = \dfrac{R_x}{R_3}§"""))),
                solution = listOf("""§R_x = \dfrac{R_2 \cdot R_3}{R_1} = \dfrac{${m(r2, 0)} \cdot ${m(r3, 0)}}{100}\ \Omega = ${m(rx, 0)}\ \Omega§"""),
                background = l("Die Wheatstone-Brücke vergleicht statt zu messen: Man stellt so lange ein, bis kein Strom mehr durch das Messgerät fließt. Dann hängt das Ergebnis nicht mehr von der Batteriespannung oder der Genauigkeit des Messgeräts ab, nur von den bekannten Widerständen. So erreicht man sehr hohe Präzision.", "The Wheatstone bridge compares instead of measuring: you adjust it until no current flows through the meter any more. Then the result no longer depends on the battery voltage or the accuracy of the meter, only on the known resistors. That's how very high precision is achieved."),
                fact = l("Waagen in Supermärkten und Kraftsensoren nutzen solche Brücken: Dehnungsmessstreifen ändern ihren Widerstand winzig, wenn sie gebogen werden.", "Supermarket scales and force sensors use such bridges: strain gauges change their resistance minutely when they are bent."),
            )
        },
        lab("phy.rcentladung", Topic.Electricity, X, 0.6f) {
            val c = pick(100.0, 470.0) * 1e-6; val r = pick(10.0, 4.7) * 1000; val frac = pick(0.01, 0.1, 0.5)
            val tau = r * c; val t = tau * ln(1 / frac)
            task(
                question = l("Ein Kondensator (${n(c * 1e6, 0)} µF) entlädt sich über einen Widerstand von ${n(r / 1000, 1)} kΩ. Wie lange dauert es, bis seine Spannung auf ${n(frac * 100, 0)} % gefallen ist?", "A capacitor (${n(c * 1e6, 0)} µF) discharges through a resistor of ${n(r / 1000, 1)} kΩ. How long does it take for its voltage to fall to ${n(frac * 100, 0)}%?"),
                steps = listOf(num(l("Zeit in Sekunden", "Time in seconds"), t, "s", rel = 0.02, done = """§t \approx ${m(t, 2)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Je weniger Ladung noch drauf ist, desto schwächer fließt der Strom. Die Spannung fällt exponentiell mit der Zeitkonstante τ = R·C.", "Key idea: the less charge is left, the weaker the current flows. The voltage falls exponentially with the time constant τ = R·C."), """§U(t) = U_0\, e^{-t/(RC)} \Rightarrow t = RC \ln\dfrac{U_0}{U}§"""))),
                solution = listOf("""§\tau = ${m(r, 0)} \cdot ${sci(c)}\ \text{s} = ${m(tau, 2)}\ \text{s}§""", """§t = ${m(tau, 2)} \cdot \ln ${m(1 / frac, 0)}\ \text{s} \approx ${m(t, 2)}\ \text{s}§"""),
                background = l("Das Produkt R·C hat die Einheit Sekunde und heißt Zeitkonstante: Nach einer Zeitkonstante sind noch 37 % übrig, nach fünf praktisch nichts mehr. Mit RC-Gliedern baut man Zeitschalter, Blinker und Filter. Der Intervall-Scheibenwischer im Auto war eine der ersten Anwendungen im Alltag.", "The product R·C has the unit of seconds and is called the time constant: after one time constant 37% is left, after five practically nothing. RC circuits are used to build timers, flashers and filters. The intermittent windscreen wiper was one of the first everyday applications."),
                fact = l("Auch Nervenzellen haben eine Membran, die wie ein RC-Glied wirkt. Sie bestimmt, wie schnell Signale abklingen.", "Nerve cells also have a membrane that acts like an RC circuit. It determines how quickly signals fade."),
            )
        },
    )

    private val fields = listOf(
        lab("phy.faraday", Topic.Fields, E, 0.4f) {
            task(
                question = l("Ein Blitz schlägt in ein Auto ein, in dem Menschen sitzen. Warum sind sie im Inneren sicher?", "Lightning strikes a car with people inside. Why are they safe inside?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Die Metallkarosserie leitet den Strom außen herum. Im Inneren eines geschlossenen Metallkäfigs entsteht kein Feld.", "The metal body conducts the current around the outside. No field forms inside a closed metal cage."),
                    listOf(l("Die Gummireifen isolieren das Auto vom Boden.", "The rubber tyres insulate the car from the ground."), l("Blitze schlagen nicht in Fahrzeuge ein, die sich bewegen.", "Lightning doesn't strike vehicles that are moving."), l("Die Autobatterie fängt die Ladung auf.", "The car battery absorbs the charge.")),
                    hints = listOf(l("Kernidee: Ein paar Zentimeter Gummi halten keinen Blitz auf, der kilometerweit durch die Luft gesprungen ist.", "Key idea: a few centimetres of rubber won't stop a bolt that has jumped kilometres through the air."), l("In einem Leiter verteilt sich Ladung so, dass das Feld innen verschwindet.", "In a conductor, charge spreads out so that the field inside vanishes.")))),
                solution = listOf(l("Das Auto wirkt als faradayscher Käfig. Die Ladung bleibt auf der Außenhaut und fließt über sie zum Boden. Mit den Reifen hat das nichts zu tun, Cabrios bieten keinen Schutz.", "The car acts as a Faraday cage. The charge stays on the outer skin and flows across it to the ground. It has nothing to do with the tyres; convertibles offer no protection.")),
                background = l("In einem Leiter sind Ladungen frei beweglich. Kommt ein äußeres Feld, verschieben sie sich so lange, bis sie es im Inneren genau aufheben. Ein geschlossener Metallkasten oder ein engmaschiges Gitter schirmt deshalb das Innere ab. Flugzeuge werden im Schnitt einmal im Jahr vom Blitz getroffen, ohne dass Passagiere etwas merken.", "In a conductor, charges can move freely. When an external field arrives, they shift until they exactly cancel it inside. That's why a closed metal box or a fine mesh shields its interior. Aircraft are struck by lightning about once a year on average without the passengers noticing anything."),
                fact = l("Auch dein Handy ist im Aufzug oft ohne Netz: Die Metallkabine wirkt als Käfig für Funkwellen.", "Your phone often has no signal in a lift for the same reason: the metal car acts as a cage for radio waves."),
            )
        },
        lab("phy.touchscreen", Topic.Fields, E, 0.5f) {
            task(
                question = l("Warum reagiert der Touchscreen deines Handys nicht, wenn du dicke Winterhandschuhe trägst, wohl aber auf einen nackten Finger?", "Why does your phone's touchscreen not react when you wear thick winter gloves, but does react to a bare finger?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Der Bildschirm misst, wie ein leitfähiger Finger das elektrische Feld an der Oberfläche verändert. Der Stoff isoliert.", "The screen measures how a conducting finger changes the electric field at the surface. The fabric insulates."),
                    listOf(l("Er misst die Wärme des Fingers.", "It measures the warmth of the finger."), l("Er misst den Druck, und Handschuhe verteilen ihn zu sehr.", "It measures pressure, and gloves spread it out too much."), l("Er erkennt die Fingerabdrücke.", "It recognises fingerprints.")),
                    hints = listOf(l("Kernidee: Unter dem Glas liegt ein Gitter feiner Elektroden, zwischen denen ein Feld besteht.", "Key idea: under the glass lies a grid of fine electrodes with a field between them."), l("Dein Körper ist ein Leiter, Wolle nicht.", "Your body is a conductor; wool isn't.")))),
                solution = listOf(l("Ein kapazitiver Touchscreen bildet mit dem Finger einen kleinen Kondensator. Der leitfähige Finger zieht Feldlinien an und ändert die Kapazität an dieser Stelle. Ein isolierender Handschuh tut das nicht.", "A capacitive touchscreen forms a small capacitor with your finger. The conducting finger draws in field lines and changes the capacitance at that spot. An insulating glove doesn't.")),
                background = l("Zwischen den Elektroden unter dem Glas wird ständig die Kapazität gemessen, hunderte Male pro Sekunde an tausenden Kreuzungspunkten. Ein Finger in der Nähe nimmt einen Teil des Feldes auf. Aus den veränderten Punkten berechnet das Handy die Position, auch für mehrere Finger zugleich.", "The capacitance between the electrodes under the glass is measured constantly, hundreds of times per second at thousands of crossing points. A finger nearby takes up part of the field. From the changed points the phone calculates the position, even for several fingers at once."),
                fact = l("Spezielle Touchscreen-Handschuhe haben leitfähige Fäden mit Silber in den Fingerspitzen. Eine Wurst funktioniert übrigens auch.", "Special touchscreen gloves have conductive threads containing silver in the fingertips. A sausage works too, by the way."),
            )
        },
        lab("phy.elektronvolt", Topic.Fields, E, 0.4f) {
            val u = pick(1.5, 9.0, 12.0); val e = u * EL
            task(
                question = l("Ein Elektron (Ladung 1,6 · 10⁻¹⁹ C) wird von einer ${n(u, 1)}-V-Batterie einmal durch den Stromkreis geschoben. Wie viel Energie bekommt es dabei, in Joule?", "An electron (charge 1.6 · 10⁻¹⁹ C) is pushed once around the circuit by a ${n(u, 1)} V battery. How much energy does it gain, in joules?"),
                steps = listOf(big(l("Energie in Joule", "Energy in joules"), e, "J", rel = 0.02, done = """§E \approx ${sci(e)}\ \text{J}§""",
                    hints = listOf(l("Kernidee: Spannung ist Energie pro Ladung. Also Energie = Ladung mal Spannung.", "Key idea: voltage is energy per charge. So energy = charge times voltage."), """§E = e \cdot U§"""))),
                solution = listOf("""§E = 1{,}6 \cdot 10^{-19}\ \text{C} \cdot ${m(u, 1)}\ \text{V} \approx ${sci(e)}\ \text{J} = ${m(u, 1)}\ \text{eV}§"""),
                background = l("Für einzelne Teilchen ist das Joule unpraktisch groß. Physiker nehmen deshalb das Elektronvolt: die Energie, die ein Elektron durch 1 V gewinnt. Chemische Bindungen haben einige eV, sichtbare Photonen 2 bis 3 eV, Teilchen im Beschleuniger am CERN Billionen eV.", "For single particles the joule is impractically large. So physicists use the electronvolt: the energy an electron gains through 1 V. Chemical bonds are a few eV, visible photons 2 to 3 eV, particles in the accelerator at CERN trillions of eV."),
                fact = l("Deshalb haben chemische Batterien Spannungen von ein paar Volt: Sie gewinnen ihre Energie aus Bindungen von ein paar eV.", "That's why chemical batteries have voltages of a few volts: they get their energy from bonds of a few eV."),
            )
        },
        lab("phy.tuerklinke", Topic.Fields, M, 0.5f) {
            val dmm = pick(3.0, 5.0, 10.0); val u = 3e6 * dmm / 1000 / 1000
            task(
                question = l("Du schlurfst über einen Teppich, greifst nach der Türklinke, und ein Funke springt über ${n(dmm, 0)} mm. Trockene Luft schlägt bei etwa 3 Millionen V/m durch. Welche Spannung hattest du mindestens?", "You shuffle across a carpet, reach for the door handle, and a spark jumps ${n(dmm, 0)} mm. Dry air breaks down at about 3 million V/m. What voltage did you have at least?"),
                steps = listOf(num(l("Spannung in Kilovolt", "Voltage in kilovolts"), u, "kV", rel = 0.02, done = """§U \approx ${m(u, 0)}\ \text{kV}§""",
                    hints = listOf(l("Kernidee: Im fast homogenen Feld ist Spannung Feldstärke mal Abstand.", "Key idea: in a nearly uniform field, voltage is field strength times distance."), """§U = E \cdot d§"""))),
                solution = listOf("""§U = 3 \cdot 10^6\ \tfrac{\text{V}}{\text{m}} \cdot ${m(dmm / 1000, 3)}\ \text{m} = ${m(u, 0)}\ \text{kV}§"""),
                background = l("Beim Gehen über Kunststoffteppich werden Elektronen abgestreift, und dein Körper lädt sich auf. Weil er nur eine kleine Kapazität hat, entsteht dabei eine hohe Spannung, aber nur sehr wenig Ladung. Der Funke tut kurz weh, ist aber harmlos: Die Energie reicht nicht einmal, um einen Tropfen Wasser messbar zu erwärmen.", "Walking across a synthetic carpet strips off electrons and your body becomes charged. Because it has only a small capacitance, a high voltage builds up, but very little charge. The spark hurts briefly but is harmless: its energy isn't even enough to warm a drop of water measurably."),
                fact = l("Bei trockener Winterluft sind es oft über 10 kV. Bei feuchter Luft leitet die Oberfläche der Haut die Ladung ab, und es knistert kaum.", "In dry winter air it's often over 10 kV. In humid air the surface of the skin drains the charge away and there's hardly any crackle."),
            )
        },
        lab("phy.blitzableiter", Topic.Fields, M, 0.5f) {
            task(
                question = l("Blitzableiter sind oben spitz. Warum ausgerechnet spitz?", "Lightning rods are pointed at the top. Why pointed of all things?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("An Spitzen drängt sich die Ladung zusammen, und das Feld wird dort so stark, dass die Luft zuerst leitend wird.", "Charge crowds together at points, and the field there becomes so strong that the air becomes conductive there first."),
                    listOf(l("Spitzen sind leichter und werden vom Wind nicht umgeworfen.", "Points are lighter and aren't blown over by the wind."), l("Der Blitz trifft das, was am nächsten an der Wolke ist, die Form ist egal.", "Lightning hits whatever is closest to the cloud; the shape doesn't matter."), l("Spitzen stoßen die Ladung der Wolke ab.", "Points repel the cloud's charge.")),
                    hints = listOf(l("Kernidee: Auf einem Leiter ist die Spannung überall gleich. Wo die Oberfläche stark gekrümmt ist, muss das Feld deshalb stärker sein.", "Key idea: on a conductor the voltage is the same everywhere. So where the surface is sharply curved, the field must be stronger."), l("Eine kleine Kugel mit derselben Spannung wie eine große hat an ihrer Oberfläche das stärkere Feld.", "A small sphere at the same voltage as a large one has the stronger field at its surface.")))),
                solution = listOf(l("Das Feld an der Oberfläche eines Leiters wächst mit seiner Krümmung. An einer Spitze wird die Luft zuerst ionisiert, und der Blitz findet dort einen vorbereiteten, leitenden Weg zum Erdboden.", "The field at a conductor's surface grows with its curvature. At a point the air is ionised first, and the lightning finds a prepared, conductive path to the ground there.")),
                background = l("Benjamin Franklin erfand den Blitzableiter um 1750. Er leitet den Blitz über ein dickes Kabel am Haus vorbei in die Erde, statt dass er sich durch Mauern und Leitungen einen Weg sucht. Dass Ladung sich an Spitzen sammelt, sieht man bei Gewitter manchmal als Elmsfeuer: ein bläuliches Leuchten an Mastspitzen.", "Benjamin Franklin invented the lightning rod around 1750. It leads the lightning past the house through a thick cable into the ground, instead of letting it find its own way through walls and wiring. That charge collects at points can sometimes be seen in thunderstorms as St Elmo's fire: a bluish glow at the tips of masts."),
                fact = l("Aus demselben Grund baut man Hochspannungsanlagen mit runden, glatten Formen: Kanten würden ständig Funken sprühen.", "For the same reason high-voltage equipment is built with round, smooth shapes: edges would constantly throw out sparks."),
            )
        },
        lab("phy.erdfeld", Topic.Fields, M, 0.6f) {
            val e = pick(100.0, 130.0, 150.0); val h = 1.8; val u = e * h
            task(
                question = l("Bei schönem Wetter herrscht über dem Erdboden ein elektrisches Feld von etwa ${n(e, 0)} V/m. Welche Spannung liegt zwischen deinen Füßen und deinem Kopf (1,8 m), wenn du im Freien stehst?", "In fair weather there is an electric field of about ${n(e, 0)} V/m above the ground. What voltage lies between your feet and your head (1.8 m) when you stand outdoors?"),
                steps = listOf(num(l("Spannung in Volt", "Voltage in volts"), u, "V", rel = 0.02, done = """§U \approx ${m(u, 0)}\ \text{V}§""",
                    hints = listOf(l("Kernidee: Feldstärke mal Höhe.", "Key idea: field strength times height."), """§U = E \cdot h§"""))),
                solution = listOf("""§U = ${m(e, 0)}\ \tfrac{\text{V}}{\text{m}} \cdot 1{,}8\ \text{m} = ${m(u, 0)}\ \text{V}§""", l("Spürbar ist das nicht: Dein Körper ist ein Leiter und gleicht sein Potenzial sofort an, ohne dass ein nennenswerter Strom fließt.", "You can't feel it: your body is a conductor and equalises its potential instantly without any noticeable current flowing.")),
                background = l("Die Erdoberfläche ist negativ geladen, die obere Atmosphäre positiv. Gewitter auf der ganzen Welt laden diesen riesigen Kondensator ständig nach, und bei schönem Wetter fließt die Ladung langsam zurück. Weil die Luft nur ein winziger Strom durchfließt, merken wir nichts von den Hunderttausenden Volt bis in große Höhen.", "The Earth's surface is negatively charged, the upper atmosphere positively. Thunderstorms all over the world keep recharging this huge capacitor, and in fair weather the charge slowly flows back. Because only a tiny current flows through the air, we notice nothing of the hundreds of thousands of volts up to great heights."),
                fact = l("Zwischen Boden und Ionosphäre liegen insgesamt rund 250.000 Volt.", "In total there are about 250,000 volts between the ground and the ionosphere."),
            )
        },
        lab("phy.ladungaa", Topic.Fields, M, 0.5f) {
            val mah = pick(2500.0, 2000.0, 3000.0); val q = mah / 1000 * 3600; val ne = q / EL
            task(
                question = l("Auf einem Akku steht ${n(mah, 0)} mAh. Das ist eine Ladungsmenge.", "A rechargeable battery is labelled ${n(mah, 0)} mAh. That is an amount of charge."),
                steps = listOf(
                    num(l("Wie viele Coulomb sind das?", "How many coulombs is that?"), q, "C", rel = 0.02, done = """§Q = ${m(q, 0)}\ \text{C}§""",
                        hints = listOf(l("Kernidee: Ein Ampere ist ein Coulomb pro Sekunde. Eine Amperestunde sind also 3600 C.", "Key idea: one ampere is one coulomb per second. So an ampere-hour is 3600 C."), """§Q = I \cdot t§""")),
                    big(l("Wie vielen Elektronen entspricht das?", "How many electrons does that correspond to?"), ne, "", rel = 0.02, done = """§N \approx ${sci(ne)}§""",
                        hints = listOf(l("Jedes Elektron trägt 1,6 · 10⁻¹⁹ C.", "Each electron carries 1.6 · 10⁻¹⁹ C."), """§N = \dfrac{Q}{e}§""")),
                ),
                solution = listOf("""§Q = ${m(mah / 1000, 1)}\ \text{A} \cdot 3600\ \text{s} = ${m(q, 0)}\ \text{C}§""", """§N = \dfrac{${m(q, 0)}}{1{,}6 \cdot 10^{-19}} \approx ${sci(ne)}§"""),
                background = l("Milliamperestunden sagen, wie lange ein Akku einen bestimmten Strom liefern kann. Über die Energie sagen sie erst etwas zusammen mit der Spannung. Ein Coulomb ist eine riesige Zahl von Elektronen. Dass so viele durch einen kleinen Akku wandern, ohne dass er schwerer oder leichter wird, liegt daran, dass sie nur im Kreis laufen.", "Milliampere-hours tell you how long a battery can deliver a certain current. They say something about energy only together with the voltage. A coulomb is a huge number of electrons. That so many pass through a small battery without it getting heavier or lighter is because they only go round in a circle."),
                fact = l("Würde man diese Ladung getrennt sammeln, wären die Kräfte gigantisch: Zwei solche Ladungen in 1 km Abstand stießen sich so stark ab wie das Gewicht von rund ${n(K_C * q * q / 1e6 / 9.81 / 1e9, 0)} Millionen Tonnen.", "If this charge were collected separately, the forces would be gigantic: two such charges 1 km apart would repel each other as hard as the weight of about ${n(K_C * q * q / 1e6 / 9.81 / 1e9, 0)} million tonnes."),
            )
        },
        lab("phy.hverhaeltnis", Topic.Fields, H, 0.6f) {
            val ratio = K_C * EL * EL / (6.674e-11 * 9.109e-31 * 1.673e-27)
            task(
                question = l("Im Wasserstoffatom ziehen sich Proton und Elektron elektrisch an und auch durch die Gravitation. Wie viel mal stärker ist die elektrische Kraft? Der Abstand spielt keine Rolle, beide Kräfte fallen mit 1/r².", "In the hydrogen atom the proton and the electron attract each other electrically and also through gravity. How many times stronger is the electric force? The distance doesn't matter, since both forces fall off as 1/r²."),
                given = listOf("""§k = 8{,}99 \cdot 10^9,\ e = 1{,}6 \cdot 10^{-19}\ \text{C},\ G = 6{,}67 \cdot 10^{-11}§""", """§m_e = 9{,}11 \cdot 10^{-31}\ \text{kg},\ m_p = 1{,}67 \cdot 10^{-27}\ \text{kg}§"""),
                steps = listOf(big(l("Verhältnis", "Ratio"), ratio, "", rel = 0.05, done = """§\approx ${sci(ratio)}§""",
                    hints = listOf(l("Kernidee: Beide Gesetze haben dieselbe Form. Im Verhältnis kürzt sich r² heraus.", "Key idea: both laws have the same form. In the ratio, r² cancels out."), """§\dfrac{F_C}{F_G} = \dfrac{k e^2}{G m_e m_p}§"""))),
                solution = listOf("""§\dfrac{8{,}99 \cdot 10^9 \cdot (1{,}6 \cdot 10^{-19})^2}{6{,}67 \cdot 10^{-11} \cdot 9{,}11 \cdot 10^{-31} \cdot 1{,}67 \cdot 10^{-27}} \approx ${sci(ratio)}§"""),
                background = l("Die Gravitation ist mit Abstand die schwächste aller Kräfte. Dass sie trotzdem Planeten und Galaxien beherrscht, liegt daran, dass es keine negative Masse gibt: Sie addiert sich immer. Elektrische Ladungen dagegen kommen in plus und minus vor und gleichen sich in großen Körpern fast perfekt aus.", "Gravity is by far the weakest of all forces. That it still rules planets and galaxies is because there is no negative mass: it always adds up. Electric charges, on the other hand, come in plus and minus and cancel almost perfectly in large bodies."),
                fact = l("Ein kleiner Magnet hebt eine Büroklammer hoch, gegen die Gravitation der ganzen Erde.", "A small magnet lifts a paper clip against the gravity of the entire Earth."),
            )
        },
        lab("phy.vandegraaff", Topic.Fields, H, 0.5f) {
            val r = pick(0.2, 0.15, 0.3); val e = 3e6; val u = e * r / 1000
            task(
                question = l("Ein Bandgenerator (Van-de-Graaff) hat eine Metallkugel mit ${n(r * 100, 0)} cm Radius. Luft schlägt bei etwa 3 Millionen V/m durch. Auf welche Spannung lässt sich die Kugel höchstens aufladen?", "A Van de Graaff generator has a metal sphere with a radius of ${n(r * 100, 0)} cm. Air breaks down at about 3 million V/m. What is the highest voltage the sphere can be charged to?"),
                steps = listOf(num(l("Höchste Spannung in kV", "Highest voltage in kV"), u, "kV", rel = 0.02, done = """§U \approx ${m(u, 0)}\ \text{kV}§""",
                    hints = listOf(l("Kernidee: Für eine geladene Kugel sind Feld an der Oberfläche und Spannung über den Radius verknüpft.", "Key idea: for a charged sphere, the field at the surface and the voltage are linked through the radius."), """§E = \dfrac{kQ}{r^2},\ U = \dfrac{kQ}{r} \Rightarrow U = E \cdot r§"""))),
                solution = listOf("""§U = 3 \cdot 10^6\ \tfrac{\text{V}}{\text{m}} \cdot ${m(r, 2)}\ \text{m} = ${m(u, 0)}\ \text{kV}§"""),
                background = l("Ein Gummiband transportiert ständig Ladung in die Kugel, und weil sich Ladung auf einem Leiter außen verteilt, kann man immer mehr hineinschaufeln. Begrenzt wird das nur durch die Luft: Wird das Feld an der Oberfläche zu stark, springen Funken. Große Kugeln erreichen deshalb höhere Spannungen, kleine Spitzen fast gar keine.", "A rubber belt constantly carries charge into the sphere, and because charge spreads to the outside of a conductor, you can keep shovelling more in. The only limit is the air: if the field at the surface gets too strong, sparks jump. That's why large spheres reach higher voltages and small points almost none."),
                fact = l("Wer eine geladene Kugel berührt und auf einem isolierenden Hocker steht, bekommt fliegende Haare: Jedes Haar ist gleich geladen und stößt die anderen ab.", "If you touch a charged sphere while standing on an insulating stool, your hair flies up: every hair has the same charge and repels the others."),
            )
        },
        lab("phy.superkondensator", Topic.Fields, H, 0.5f) {
            val c = pick(3000.0, 1000.0, 500.0); val u = 2.7; val e = 0.5 * c * u * u / 1000
            task(
                question = l("Ein Superkondensator hat ${n(c, 0)} F und wird bis 2,7 V geladen. Wie viel Energie speichert er (in kJ)? Zum Vergleich: Eine Mignon-Batterie (AA) hat etwa 10 kJ.", "A supercapacitor has ${n(c, 0)} F and is charged to 2.7 V. How much energy does it store (in kJ)? For comparison: an AA battery holds about 10 kJ."),
                steps = listOf(num(l("Energie in kJ", "Energy in kJ"), e, "kJ", rel = 0.02, done = """§E \approx ${m(e, 1)}\ \text{kJ}§""",
                    hints = listOf(l("Kernidee: Beim Laden wächst die Spannung mit der Ladung, also zählt im Mittel nur die halbe Endspannung.", "Key idea: during charging the voltage grows with the charge, so on average only half the final voltage counts."), """§E = \tfrac{1}{2} C U^2§"""))),
                solution = listOf("""§E = \tfrac{1}{2} \cdot ${m(c, 0)} \cdot 2{,}7^2\ \text{J} \approx ${m(e * 1000, 0)}\ \text{J}§"""),
                background = l("Ein Kondensator speichert Energie im elektrischen Feld, ein Akku in chemischen Bindungen. Superkondensatoren haben riesige innere Oberflächen aus porösem Kohlenstoff und erreichen so Tausende Farad. Sie speichern weniger als Akkus, laden aber in Sekunden und halten Millionen Zyklen durch. Busse nutzen sie, um Bremsenergie aufzufangen.", "A capacitor stores energy in an electric field, a battery in chemical bonds. Supercapacitors have huge internal surfaces of porous carbon and so reach thousands of farads. They store less than batteries but charge in seconds and last for millions of cycles. Buses use them to capture braking energy."),
                fact = l("Ein gewöhnlicher Kondensator in einem Radio hat oft nur Pikofarad, also ein Billiardstel davon.", "An ordinary capacitor in a radio often has only picofarads, a quadrillionth of that."),
            )
        },
        lab("phy.erdkapazitaet", Topic.Fields, H, 0.6f) {
            val r = 6.371e6; val c = r / K_C
            task(
                question = l("Man kann die ganze Erde als leitende Kugel mit 6371 km Radius betrachten. Welche Kapazität hat sie?", "You can treat the whole Earth as a conducting sphere with a radius of 6371 km. What is its capacitance?"),
                steps = listOf(num(l("Kapazität in Mikrofarad", "Capacitance in microfarads"), c * 1e6, "µF", rel = 0.02, done = """§C \approx ${m(c * 1e6, 0)}\ \mu\text{F}§""",
                    hints = listOf(l("Kernidee: Eine Kugel mit Ladung Q hat die Spannung kQ/R. Kapazität ist Ladung pro Spannung.", "Key idea: a sphere with charge Q has the voltage kQ/R. Capacitance is charge per voltage."), """§C = \dfrac{Q}{U} = \dfrac{R}{k} = 4\pi\varepsilon_0 R§"""))),
                solution = listOf("""§C = \dfrac{6{,}371 \cdot 10^6}{8{,}99 \cdot 10^9}\ \text{F} \approx ${m(c * 1e6, 0)}\ \mu\text{F}§"""),
                background = l("Kapazität sagt, wie viel Ladung ein Körper pro Volt aufnehmen kann. Für eine einzelne Kugel wächst sie nur mit dem Radius. Selbst die ganze Erde hat deshalb weniger Kapazität als ein paar Kondensatoren aus dem Elektronikladen. Ein Farad ist eine riesige Einheit.", "Capacitance tells you how much charge a body can take per volt. For a single sphere it grows only with the radius. So even the whole Earth has less capacitance than a few capacitors from an electronics shop. A farad is a huge unit."),
                fact = l("Ein Superkondensator so groß wie eine Getränkedose hat über eine Million Mal mehr Kapazität als der ganze Planet.", "A supercapacitor the size of a drinks can has over a million times more capacitance than the whole planet."),
            )
        },
        lab("phy.millikan", Topic.Fields, X, 0.7f) {
            val q = pick(3, 4, 5); val mass = 2.0e-15; val d = 0.01
            val u = mass * 9.81 * d / (q * EL)
            task(
                question = l("Ein Öltröpfchen (Masse 2,0 · 10⁻¹⁵ kg) schwebt regungslos zwischen zwei waagrechten Metallplatten im Abstand von 1,0 cm, an denen ${n(u, 0)} V liegen. Wie viele Elementarladungen trägt es?", "An oil droplet (mass 2.0 · 10⁻¹⁵ kg) floats motionless between two horizontal metal plates 1.0 cm apart, with ${n(u, 0)} V across them. How many elementary charges does it carry?"),
                steps = listOf(num(l("Anzahl der Elementarladungen", "Number of elementary charges"), q.toDouble(), "", abs = 0.3, done = """§n = ${q}§""",
                    hints = listOf(l("Kernidee: Schweben heißt, die elektrische Kraft nach oben hebt die Gewichtskraft genau auf.", "Key idea: floating means the upward electric force exactly cancels the weight."), """§q \dfrac{U}{d} = m g \Rightarrow q = \dfrac{m g d}{U},\quad n = \dfrac{q}{e}§"""))),
                solution = listOf("""§q = \dfrac{2{,}0 \cdot 10^{-15} \cdot 9{,}81 \cdot 0{,}01}{${m(u, 0)}}\ \text{C} \approx ${sci(q * EL)}\ \text{C}§""", """§n = \dfrac{q}{1{,}6 \cdot 10^{-19}\ \text{C}} \approx ${q}§"""),
                background = l("Robert Millikan maß um 1910 die Ladung von tausenden solcher Tröpfchen. Es kamen nie beliebige Werte heraus, sondern immer ganzzahlige Vielfache einer kleinsten Ladung. Damit war gezeigt, dass Ladung gequantelt ist und dass es eine Elementarladung gibt, die des Elektrons.", "Around 1910 Robert Millikan measured the charge of thousands of such droplets. He never got arbitrary values, always whole-number multiples of a smallest charge. That showed that charge is quantised and that there is an elementary charge, that of the electron."),
                fact = l("Quarks tragen Drittel dieser Ladung, kommen aber nie einzeln vor. Deshalb findet man in der Natur trotzdem nur ganze Vielfache.", "Quarks carry thirds of this charge but never occur on their own. That's why only whole multiples are found in nature even so."),
            )
        },
        lab("phy.spaltungcoulomb", Topic.Fields, X, 0.7f) {
            val z = 46; val a = 118.0; val rr = 1.2e-15 * a.pow(1.0 / 3.0); val d = 2 * rr
            val e = K_C * (z * EL) * (z * EL) / d / EL / 1e6
            task(
                question = l("Ein Urankern (92 Protonen) spaltet sich in zwei gleiche Hälften mit je 46 Protonen und 118 Nukleonen. Kurz nach der Spaltung berühren sie sich gerade noch. Ein Kern hat den Radius 1,2 fm · A^(1/3). Welche elektrische Abstoßungsenergie steckt in den beiden Hälften?", "A uranium nucleus (92 protons) splits into two equal halves with 46 protons and 118 nucleons each. Just after the split they are barely touching. A nucleus has a radius of 1.2 fm · A^(1/3). How much electric repulsion energy is stored in the two halves?"),
                steps = listOf(num(l("Energie in MeV", "Energy in MeV"), e, "MeV", rel = 0.05, done = """§E \approx ${m(sig(e))}\ \text{MeV}§""",
                    hints = listOf(l("Kernidee: Zwei geladene Kugeln, die sich berühren: Abstand der Mittelpunkte ist zweimal der Kernradius. Energie = k·Q₁·Q₂/r.", "Key idea: two charged spheres touching: the distance between centres is twice the nuclear radius. Energy = k·Q₁·Q₂/r."), """§r = 1{,}2\ \text{fm} \cdot 118^{1/3} \approx ${m(rr * 1e15, 2)}\ \text{fm},\quad E = \dfrac{k (46 e)^2}{2r}§"""))),
                solution = listOf("""§E = \dfrac{8{,}99 \cdot 10^9 \cdot (46 \cdot 1{,}6 \cdot 10^{-19})^2}{${sci(d)}}\ \text{J} \approx ${sci(e * 1e6 * EL)}\ \text{J} \approx ${m(sig(e))}\ \text{MeV}§"""),
                background = l("Kernspaltung ist im Grunde elektrische Energie. Im Urankern hält die starke Kernkraft 92 Protonen zusammen, die sich gegenseitig heftig abstoßen. Teilt sich der Kern, wirkt die kurzreichweitige Kernkraft nicht mehr zwischen den Hälften, und die Abstoßung jagt sie auseinander. Ihre Bewegungsenergie wird im Reaktor zu Wärme.", "Nuclear fission is basically electrical energy. In the uranium nucleus the strong nuclear force holds together 92 protons that repel each other violently. When the nucleus splits, the short-range nuclear force no longer acts between the halves, and the repulsion drives them apart. Their kinetic energy becomes heat in the reactor."),
                fact = l("Gemessen werden bei einer Spaltung rund 200 MeV, fast genau dieser Wert. Chemische Reaktionen setzen pro Molekül nur einige eV frei, hundert Millionen Mal weniger.", "About 200 MeV is measured per fission, almost exactly this value. Chemical reactions release only a few eV per molecule, a hundred million times less."),
            )
        },
    )

    private val magnetism = listOf(
        lab("phy.polarlicht", Topic.Magnetism, E, 0.5f) {
            task(
                question = l("Polarlichter sieht man fast nur in der Nähe der Pole, etwa in Norwegen oder Kanada. Warum nicht am Äquator?", "Auroras are seen almost only near the poles, for example in Norway or Canada. Why not at the equator?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Das Magnetfeld der Erde lenkt geladene Teilchen von der Sonne an seinen Feldlinien entlang zu den Polen.", "The Earth's magnetic field steers charged particles from the Sun along its field lines towards the poles."),
                    listOf(l("Nur an den Polen ist es dunkel genug.", "Only at the poles is it dark enough."), l("Die Kälte am Pol bringt die Luft zum Leuchten.", "The cold at the pole makes the air glow."), l("Dort spiegelt sich das Sonnenlicht im Eis.", "There the sunlight reflects off the ice.")),
                    hints = listOf(l("Kernidee: Die Sonne schleudert ständig geladene Teilchen ins All, den Sonnenwind.", "Key idea: the Sun constantly flings charged particles into space: the solar wind."), l("Geladene Teilchen laufen in einem Magnetfeld auf Spiralen um die Feldlinien.", "Charged particles in a magnetic field travel in spirals around the field lines.")))),
                solution = listOf(l("Das Erdmagnetfeld hält die meisten Teilchen ab. An den Polen laufen die Feldlinien aber steil in die Erde, und die Teilchen spiralen an ihnen in die obere Atmosphäre. Dort bringen sie Sauerstoff (grün, rot) und Stickstoff (blau, violett) zum Leuchten.", "The Earth's magnetic field keeps most particles away. At the poles, however, the field lines run steeply into the Earth, and the particles spiral along them into the upper atmosphere. There they make oxygen (green, red) and nitrogen (blue, violet) glow.")),
                background = l("Das Magnetfeld der Erde ist ein Schutzschild. Geladene Teilchen können nicht einfach quer zu den Feldlinien fliegen, die Lorentzkraft zwingt sie auf Kreise. Nur dort, wo die Feldlinien in die Atmosphäre führen, kommen sie hinein: in einem Ring um die Magnetpole, dem Polarlichtoval.", "The Earth's magnetic field is a protective shield. Charged particles can't simply fly across the field lines; the Lorentz force forces them into circles. Only where the field lines lead into the atmosphere can they get in: in a ring around the magnetic poles, the auroral oval."),
                fact = l("Bei starken Sonnenstürmen reicht das Oval weit nach Süden. Im Mai 2024 sah man Polarlichter sogar in ganz Deutschland.", "During strong solar storms the oval reaches far south. In May 2024 auroras were seen all over Germany."),
            )
        },
        lab("phy.mrt", Topic.Magnetism, E, 0.3f) {
            val b = pick(1.5, 3.0, 7.0); val ratio = b / 50e-6
            task(
                question = l("Ein MRT-Gerät im Krankenhaus hat ein Magnetfeld von ${n(b, 1)} Tesla. Das Erdmagnetfeld ist etwa 50 Mikrotesla stark. Wie viel mal stärker ist das MRT?", "An MRI scanner in a hospital has a magnetic field of ${n(b, 1)} tesla. The Earth's magnetic field is about 50 microtesla. How many times stronger is the MRI?"),
                steps = listOf(num(l("Faktor", "Factor"), ratio, "", rel = 0.02, done = """§\approx ${m(ratio, 0)}§""",
                    hints = listOf(l("Kernidee: Beide in dieselbe Einheit bringen. Mikro heißt Millionstel.", "Key idea: bring both into the same unit. Micro means millionth."), """§\dfrac{${m(b, 1)}}{50 \cdot 10^{-6}}§"""))),
                solution = listOf("""§\dfrac{${m(b, 1)}\ \text{T}}{5 \cdot 10^{-5}\ \text{T}} = ${m(ratio, 0)}§"""),
                background = l("Ein MRT nutzt, dass sich die Kerne der Wasserstoffatome in deinem Körper wie winzige Kompassnadeln im starken Feld ausrichten. Radiowellen bringen sie kurz aus dem Takt, und beim Zurückkippen senden sie selbst Signale. Daraus berechnet der Computer Schnittbilder, ohne Röntgenstrahlung.", "An MRI scanner uses the fact that the nuclei of the hydrogen atoms in your body line up like tiny compass needles in the strong field. Radio waves briefly knock them out of step, and as they tip back they send out signals themselves. From these the computer calculates cross-sectional images, without X-rays."),
                fact = l("Das Feld ist so stark, dass eine Schere quer durch den Raum fliegen kann. Deshalb muss alles Metall draußen bleiben.", "The field is so strong that a pair of scissors can fly across the room. That's why all metal has to stay outside."),
            )
        },
        lab("phy.lautsprechermagnet", Topic.Magnetism, E, 0.5f) {
            task(
                question = l("In einem Lautsprecher sitzt eine Drahtspule an der Membran, mitten im Feld eines Dauermagneten. Was bringt die Membran zum Schwingen?", "In a loudspeaker a coil of wire is attached to the cone, right in the field of a permanent magnet. What makes the cone vibrate?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Der wechselnde Strom in der Spule erfährt im Magnetfeld eine Kraft, die ständig die Richtung wechselt.", "The alternating current in the coil experiences a force in the magnetic field that keeps changing direction."),
                    listOf(l("Der Strom erwärmt die Spule, und die Luft dehnt sich im Takt aus.", "The current heats the coil, and the air expands in rhythm."), l("Der Magnet zieht die Membran mal an und lässt sie mal los.", "The magnet sometimes attracts the cone and sometimes lets it go."), l("Die Spule erzeugt den Schall selbst durch elektrische Funken.", "The coil makes the sound itself through electric sparks.")),
                    hints = listOf(l("Kernidee: Auf einen stromdurchflossenen Leiter im Magnetfeld wirkt die Lorentzkraft.", "Key idea: a current-carrying conductor in a magnetic field feels the Lorentz force."), l("Kehrt sich der Strom um, kehrt sich auch die Kraft um.", "If the current reverses, the force reverses too.")))),
                solution = listOf(l("Das Musiksignal ist ein Wechselstrom. Im Feld des Magneten drückt die Lorentzkraft die Spule im selben Takt vor und zurück, die Membran bewegt die Luft: Schall.", "The music signal is an alternating current. In the magnet's field the Lorentz force pushes the coil back and forth in the same rhythm, and the cone moves the air: sound.")),
                background = l("Ein Lautsprecher ist ein Elektromotor, der nicht dreht, sondern zittert. Dreht man das Prinzip um, wird er zum Mikrofon: Bewegt Schall die Membran, wird in der Spule eine Spannung induziert. Tatsächlich kann man einen Kopfhörer zur Not als Mikrofon benutzen.", "A loudspeaker is an electric motor that doesn't turn but shakes. Reverse the principle and it becomes a microphone: if sound moves the cone, a voltage is induced in the coil. In fact, at a pinch you can use a pair of headphones as a microphone."),
                fact = l("Die Bewegung ist winzig: Bei Zimmerlautstärke schwingt eine Hochtönermembran nur um Bruchteile eines Millimeters.", "The movement is tiny: at normal listening volume a tweeter cone moves by only fractions of a millimetre."),
            )
        },
        lab("phy.spulenfeld", Topic.Magnetism, M, 0.5f) {
            val nTurns = pick(500.0, 1000.0); val len = 0.1; val i = pick(1.0, 2.0, 3.0)
            val b = MU0 * nTurns / len * i * 1000
            task(
                question = l("Eine lange Spule hat ${n(nTurns, 0)} Windungen auf 10 cm Länge und führt ${n(i, 0)} A. Wie stark ist das Magnetfeld in ihrem Inneren?", "A long coil has ${n(nTurns, 0)} turns over a length of 10 cm and carries ${n(i, 0)} A. How strong is the magnetic field inside it?"),
                steps = listOf(num(l("Feldstärke in Millitesla", "Field strength in millitesla"), b, "mT", rel = 0.02, done = """§B \approx ${m(b, 1)}\ \text{mT}§""",
                    hints = listOf(l("Kernidee: Das Feld hängt von der Windungsdichte (Windungen pro Meter) und dem Strom ab, nicht vom Durchmesser.", "Key idea: the field depends on the turn density (turns per metre) and the current, not on the diameter."), """§B = \mu_0 \dfrac{N}{L} I§"""))),
                solution = listOf("""§B = 4\pi \cdot 10^{-7} \cdot \dfrac{${m(nTurns, 0)}}{0{,}1} \cdot ${m(i, 0)}\ \text{T} \approx ${m(b, 1)}\ \text{mT}§"""),
                background = l("Jeder Strom erzeugt ein Magnetfeld, das ihn ringförmig umgibt. In einer Spule addieren sich die Felder aller Windungen im Inneren zu einem gleichmäßigen, starken Feld. Ein Eisenkern verstärkt es noch einmal um das Hundert- bis Tausendfache. So entstehen Elektromagnete, die man ein- und ausschalten kann.", "Every current produces a magnetic field that circles around it. In a coil the fields of all the turns add up inside into a uniform, strong field. An iron core strengthens it by another factor of a hundred to a thousand. That's how electromagnets are made that can be switched on and off."),
                fact = l("Auf Schrottplätzen heben Elektromagnete ganze Autos. Schaltet man den Strom ab, fällt das Auto sofort herunter.", "In scrapyards electromagnets lift whole cars. Switch off the current and the car drops immediately."),
            )
        },
        lab("phy.induktionsherd", Topic.Magnetism, M, 0.5f) {
            task(
                question = l("Auf einem Induktionsherd wird ein Eisentopf heiß, die Glasplatte darunter bleibt fast kalt, und ein Glastopf erwärmt sich überhaupt nicht. Wie funktioniert das?", "On an induction hob an iron pot gets hot, the glass plate beneath it stays almost cold, and a glass pot doesn't heat up at all. How does it work?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Ein schnell wechselndes Magnetfeld erzeugt im Metallboden des Topfes Wirbelströme, und die heizen ihn direkt.", "A rapidly changing magnetic field creates eddy currents in the metal base of the pot, and these heat it directly."),
                    listOf(l("Die Platte strahlt unsichtbares Infrarotlicht aus, das nur Metall absorbiert.", "The plate emits invisible infrared light that only metal absorbs."), l("Unter dem Glas glüht eine Heizspirale, die nur nach oben strahlt.", "Under the glass a heating coil glows that radiates only upwards."), l("Der Herd erzeugt Mikrowellen, die das Wasser im Topf erhitzen.", "The hob produces microwaves that heat the water in the pot.")),
                    hints = listOf(l("Kernidee: Ein wechselndes Magnetfeld induziert in einem Leiter eine Spannung.", "Key idea: a changing magnetic field induces a voltage in a conductor."), l("Glas leitet keinen Strom.", "Glass doesn't conduct electricity.")))),
                solution = listOf(l("Unter dem Glas sitzt eine Spule mit Wechselstrom von etwa 20 bis 100 kHz. Ihr Magnetfeld induziert im Topfboden kreisende Ströme, die ihn wegen seines Widerstands erwärmen. Die Glasplatte wird nur indirekt vom heißen Topf warm.", "Under the glass sits a coil carrying alternating current at about 20 to 100 kHz. Its magnetic field induces circular currents in the base of the pot, which heat it because of its resistance. The glass plate only gets warm indirectly from the hot pot.")),
                background = l("Induktion heißt: Ändert sich das Magnetfeld durch einen Leiter, entsteht darin eine Spannung. In einem massiven Metallstück fließen dann Ströme im Kreis, die Wirbelströme. Eisenhaltige Töpfe funktionieren besonders gut, weil sie das Feld bündeln. Weil die Wärme direkt im Topf entsteht, geht kaum Energie verloren.", "Induction means: if the magnetic field through a conductor changes, a voltage appears in it. In a solid piece of metal, currents then flow in circles: eddy currents. Pots containing iron work especially well because they concentrate the field. Because the heat arises directly in the pot, hardly any energy is lost."),
                fact = l("Legt man ein Blatt Papier zwischen Platte und Topf, kocht das Wasser trotzdem, und das Papier verbrennt nicht.", "If you put a sheet of paper between the hob and the pot, the water still boils, and the paper doesn't burn."),
            )
        },
        lab("phy.magnetrohr", Topic.Magnetism, M, 0.6f) {
            task(
                question = l("Du lässt einen starken Magneten durch ein senkrechtes Kupferrohr fallen. Kupfer ist nicht magnetisch. Trotzdem schwebt der Magnet fast in Zeitlupe nach unten. Warum?", "You drop a strong magnet through a vertical copper pipe. Copper isn't magnetic. Yet the magnet drifts down almost in slow motion. Why?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Der fallende Magnet induziert im Rohr Ströme, deren Magnetfeld seine Bewegung bremst.", "The falling magnet induces currents in the pipe whose magnetic field brakes its motion."),
                    listOf(l("Das Kupfer wird doch leicht magnetisch und hält ihn fest.", "The copper does become slightly magnetic and holds it."), l("Die Luft im Rohr wird zusammengedrückt und bremst.", "The air in the pipe is compressed and brakes it."), l("Der Magnet reibt an der Innenwand.", "The magnet rubs against the inner wall.")),
                    hints = listOf(l("Kernidee: Der Magnet bewegt sich, also ändert sich das Magnetfeld im Kupfer ständig.", "Key idea: the magnet is moving, so the magnetic field in the copper changes all the time."), l("Lenzsche Regel: Induzierte Ströme wirken immer ihrer Ursache entgegen.", "Lenz's law: induced currents always oppose their cause.")))),
                solution = listOf(l("Im Rohr entstehen kreisende Wirbelströme. Ihr Magnetfeld stößt den Magneten von oben ab und zieht ihn von unten an, beides bremst. Die Bewegungsenergie wird zu Wärme im Kupfer.", "Circular eddy currents arise in the pipe. Their magnetic field repels the magnet from above and attracts it from below; both brake it. The kinetic energy becomes heat in the copper.")),
                background = l("Die Natur wehrt sich gegen Änderungen des Magnetfelds: Induzierte Ströme fließen immer so, dass sie die Änderung bremsen. Je schneller der Magnet fällt, desto stärker die Ströme, und bald fällt er mit gleichbleibender, geringer Geschwindigkeit. Bei einem Plastikrohr passiert nichts, weil keine Ströme fließen können.", "Nature resists changes in the magnetic field: induced currents always flow in such a way that they slow down the change. The faster the magnet falls, the stronger the currents, and soon it falls at a steady, slow speed. In a plastic pipe nothing happens, because no currents can flow."),
                fact = l("Auf diesem Prinzip beruhen Wirbelstrombremsen in ICE-Zügen und Achterbahnen, die verschleißfrei bremsen.", "Eddy-current brakes in high-speed trains and roller coasters, which brake without wear, are based on this principle."),
            )
        },
        lab("phy.oersted", Topic.Magnetism, H, 0.6f) {
            val i = pick(10.0, 5.0, 20.0); val r = pick(0.04, 0.05); val be = 20e-6
            val b = MU0 * i / (2 * PI * r); val ang = Math.toDegrees(atan(b / be))
            task(
                question = l("Ein Kompass liegt ${n(r * 100, 0)} cm unter einem Draht, der genau in Nord-Süd-Richtung verläuft. Ohne Strom zeigt er nach Norden (waagrechtes Erdfeld 20 µT). Dann fließen ${n(i, 0)} A durch den Draht. Um wie viel Grad dreht sich die Nadel?", "A compass lies ${n(r * 100, 0)} cm below a wire that runs exactly north-south. Without current it points north (horizontal Earth field 20 µT). Then ${n(i, 0)} A flows through the wire. By how many degrees does the needle turn?"),
                steps = listOf(num(l("Ablenkung in Grad", "Deflection in degrees"), ang, "°", abs = 1.0, done = """§\alpha \approx ${m(ang, 0)}^\circ§""",
                    hints = listOf(l("Kernidee: Das Feld des Drahtes zeigt unter ihm nach Osten oder Westen, also quer zum Erdfeld. Die Nadel folgt der Summe beider Felder.", "Key idea: below the wire its field points east or west, i.e. across the Earth's field. The needle follows the sum of both fields."), """§B_D = \dfrac{\mu_0 I}{2\pi r},\quad \tan\alpha = \dfrac{B_D}{B_E}§"""))),
                solution = listOf("""§B_D = \dfrac{4\pi \cdot 10^{-7} \cdot ${m(i, 0)}}{2\pi \cdot ${m(r, 2)}}\ \text{T} \approx ${m(b * 1e6, 0)}\ \mu\text{T}§""", """§\alpha = \arctan\dfrac{${m(b * 1e6, 0)}}{20} \approx ${m(ang, 0)}^\circ§"""),
                background = l("1820 bemerkte Hans Christian Ørsted während einer Vorlesung, dass eine Kompassnadel zuckte, als er Strom durch einen Draht schickte. Das war der erste Hinweis darauf, dass Elektrizität und Magnetismus zusammengehören. Ein stromdurchflossener Draht ist von kreisförmigen Feldlinien umgeben, die mit dem Abstand schwächer werden.", "In 1820 Hans Christian Ørsted noticed during a lecture that a compass needle twitched when he sent current through a wire. That was the first sign that electricity and magnetism belong together. A current-carrying wire is surrounded by circular field lines that get weaker with distance."),
                fact = l("Deshalb soll man einen Kompass nicht neben Stromkabeln im Auto oder Boot benutzen: Er zeigt dann falsch.", "That's why you shouldn't use a compass next to power cables in a car or boat: it will point the wrong way."),
            )
        },
        lab("phy.zweidraehte", Topic.Magnetism, H, 0.5f) {
            val i = pick(10.0, 20.0, 50.0); val d = pick(0.01, 0.005); val len = 1.0
            val f = MU0 * i * i * len / (2 * PI * d)
            task(
                question = l("Zwei parallele Drähte, 1 m lang, liegen ${n(d * 1000, 0)} mm auseinander. Durch beide fließen ${n(i, 0)} A in dieselbe Richtung. Welche Kraft wirkt zwischen ihnen, und ziehen sie sich an oder stoßen sie sich ab?", "Two parallel wires, 1 m long, lie ${n(d * 1000, 0)} mm apart. ${n(i, 0)} A flows through each in the same direction. What force acts between them, and do they attract or repel?"),
                steps = listOf(num(l("Kraft in Millinewton (anziehend)", "Force in millinewtons (attractive)"), f * 1000, "mN", rel = 0.02, done = """§F \approx ${m(sig(f * 1000))}\ \text{mN}§""",
                    hints = listOf(l("Kernidee: Der eine Draht sitzt im Magnetfeld des anderen, und darauf wirkt die Lorentzkraft. Gleichgerichtete Ströme ziehen sich an.", "Key idea: each wire sits in the magnetic field of the other and feels the Lorentz force. Parallel currents attract."), """§F = \dfrac{\mu_0 I_1 I_2 L}{2\pi d}§"""))),
                solution = listOf("""§F = \dfrac{4\pi \cdot 10^{-7} \cdot ${m(i, 0)}^2 \cdot 1}{2\pi \cdot ${m(d, 3)}}\ \text{N} \approx ${m(sig(f * 1000))}\ \text{mN}§"""),
                background = l("Ströme in gleicher Richtung ziehen sich an, in entgegengesetzter Richtung stoßen sie sich ab. Bis 2019 war genau diese Kraft die Definition des Ampere. Bei Kurzschlüssen in Umspannwerken fließen zehntausende Ampere, dann reißen sich die Leitungen gegenseitig regelrecht aus den Halterungen.", "Currents in the same direction attract each other, in opposite directions they repel. Until 2019 exactly this force was the definition of the ampere. In short circuits in substations tens of thousands of amperes flow, and then the conductors literally tear each other out of their mountings."),
                fact = l("Eine Spule zieht sich beim Einschalten deshalb leicht zusammen: Ihre Windungen sind lauter parallele Ströme. Das Brummen von Trafos kommt teilweise daher.", "That's why a coil contracts slightly when switched on: its turns are lots of parallel currents. The hum of transformers partly comes from this."),
            )
        },
        lab("phy.qiladen", Topic.Magnetism, H, 0.6f) {
            val nT = pick(10.0, 20.0); val a = 10e-4; val b0 = pick(1.0, 0.5) * 1e-3; val f = pick(150e3, 100e3)
            val u = nT * a * 2 * PI * f * b0
            task(
                question = l("Beim kabellosen Laden erzeugt die Ladeschale ein Wechselfeld mit ${n(f / 1000, 0)} kHz und ${n(b0 * 1000, 1)} mT Spitzenwert. Die Empfangsspule im Handy hat ${n(nT, 0)} Windungen und 10 cm² Fläche. Welche Spitzenspannung wird induziert?", "For wireless charging the charging pad produces an alternating field at ${n(f / 1000, 0)} kHz with a peak value of ${n(b0 * 1000, 1)} mT. The receiving coil in the phone has ${n(nT, 0)} turns and an area of 10 cm². What peak voltage is induced?"),
                steps = listOf(num(l("Spitzenspannung in Volt", "Peak voltage in volts"), u, "V", rel = 0.03, done = """§U_0 \approx ${m(u, 1)}\ \text{V}§""",
                    hints = listOf(l("Kernidee: Die induzierte Spannung ist proportional dazu, wie schnell sich der magnetische Fluss ändert. Bei einer Sinusschwingung ist die größte Änderungsrate ω mal Amplitude.", "Key idea: the induced voltage is proportional to how fast the magnetic flux changes. For a sine wave the greatest rate of change is ω times the amplitude."), """§U_0 = N A \omega B_0,\quad \omega = 2\pi f§"""))),
                solution = listOf("""§U_0 = ${m(nT, 0)} \cdot 10^{-3}\ \text{m}^2 \cdot 2\pi \cdot ${sci(f)}\ \text{Hz} \cdot ${sci(b0)}\ \text{T} \approx ${m(u, 1)}\ \text{V}§"""),
                background = l("Kabelloses Laden ist ein Transformator, dessen beide Spulen in zwei verschiedenen Geräten sitzen. Weil der Abstand größer ist als im Trafo, geht mehr Energie verloren, und man arbeitet mit hohen Frequenzen: Je schneller sich das Feld ändert, desto größer die induzierte Spannung bei kleinem Feld.", "Wireless charging is a transformer whose two coils sit in two different devices. Because the gap is bigger than in a transformer, more energy is lost, and high frequencies are used: the faster the field changes, the larger the induced voltage for a small field."),
                fact = l("Liegt das Handy schief auf der Schale, geht ein Teil des Flusses an der Spule vorbei, und es lädt langsamer und wird wärmer.", "If the phone lies crooked on the pad, part of the flux misses the coil, and it charges more slowly and gets warmer."),
            )
        },
        lab("phy.motordrehmoment", Topic.Magnetism, H, 0.5f) {
            val nT = 100.0; val side = pick(0.04, 0.05); val i = pick(1.0, 2.0); val b = pick(0.5, 0.3)
            val tq = nT * i * side * side * b
            task(
                question = l("Im Modell eines Elektromotors dreht sich eine quadratische Spule (${n(nT, 0)} Windungen, Seitenlänge ${n(side * 100, 0)} cm, Strom ${n(i, 0)} A) im Feld eines Magneten (${n(b, 1)} T). Wie groß ist das größte Drehmoment?", "In a model electric motor a square coil (${n(nT, 0)} turns, side length ${n(side * 100, 0)} cm, current ${n(i, 0)} A) turns in the field of a magnet (${n(b, 1)} T). What is the largest torque?"),
                steps = listOf(num(l("Drehmoment in Nm", "Torque in Nm"), tq, "Nm", rel = 0.02, done = """§M = ${m(tq, 3)}\ \text{Nm}§""",
                    hints = listOf(l("Kernidee: Auf die beiden Seiten quer zum Feld wirken entgegengesetzte Lorentzkräfte. Sie bilden ein Kräftepaar.", "Key idea: opposite Lorentz forces act on the two sides that cross the field. They form a couple."), """§M_{max} = N I A B§"""))),
                solution = listOf("""§M = ${m(nT, 0)} \cdot ${m(i, 0)} \cdot ${m(side * side, 4)}\ \text{m}^2 \cdot ${m(b, 1)}\ \text{T} = ${m(tq, 3)}\ \text{Nm}§"""),
                background = l("Ein Elektromotor nutzt die Kraft auf Ströme im Magnetfeld. Die eine Seite der Spule wird nach oben, die andere nach unten gedrückt, und sie dreht sich. Damit sie nicht in einer Stellung stehen bleibt, polt ein Kommutator den Strom bei jeder halben Umdrehung um. Mehr Windungen, mehr Strom und ein stärkerer Magnet machen den Motor kräftiger.", "An electric motor uses the force on currents in a magnetic field. One side of the coil is pushed up, the other down, and it turns. To stop it getting stuck in one position, a commutator reverses the current every half turn. More turns, more current and a stronger magnet make the motor more powerful."),
                fact = l("Die Motoren moderner Elektroautos haben keinen Kommutator mehr: Elektronik schaltet die Spulen hunderte Male pro Sekunde um.", "The motors of modern electric cars no longer have a commutator: electronics switch the coils hundreds of times per second."),
            )
        },
        lab("phy.abschaltspannung", Topic.Magnetism, X, 0.6f) {
            val lH = pick(0.05, 0.1, 0.2); val i = pick(1.0, 2.0); val dt = pick(1e-4, 1e-3)
            val u = lH * i / dt
            task(
                question = l("Durch eine Spule mit ${n(lH * 1000, 0)} mH Induktivität fließen ${n(i, 0)} A. Ein Schalter unterbricht den Strom in ${n(dt * 1000, 1)} ms. Welche Spannung entsteht dabei an der Spule?", "${n(i, 0)} A flows through a coil with an inductance of ${n(lH * 1000, 0)} mH. A switch interrupts the current in ${n(dt * 1000, 1)} ms. What voltage appears across the coil?"),
                steps = listOf(num(l("Spannung in Volt", "Voltage in volts"), u, "V", rel = 0.02, done = """§U \approx ${m(sig(u))}\ \text{V}§""",
                    hints = listOf(l("Kernidee: Eine Spule wehrt sich gegen jede Stromänderung mit einer Induktionsspannung. Je schneller die Änderung, desto größer die Spannung.", "Key idea: a coil resists every change in current with an induced voltage. The faster the change, the larger the voltage."), """§U = L \dfrac{\Delta I}{\Delta t}§"""))),
                solution = listOf("""§U = ${m(lH, 2)}\ \text{H} \cdot \dfrac{${m(i, 0)}\ \text{A}}{${sci(dt)}\ \text{s}} = ${m(sig(u))}\ \text{V}§"""),
                background = l("Das Magnetfeld einer Spule speichert Energie, und die kann nicht einfach verschwinden. Unterbricht man den Strom, will die Spule ihn weiter treiben und baut dafür eine hohe Spannung auf, bis ein Funke überspringt. Deshalb haben Relais und Motoren Schutzdioden. Die Zündspule im Benzinmotor nutzt den Effekt absichtlich für den Zündfunken.", "A coil's magnetic field stores energy, and it can't just vanish. If you interrupt the current, the coil tries to keep it going and builds up a high voltage until a spark jumps across. That's why relays and motors have protective diodes. The ignition coil in a petrol engine uses the effect deliberately to make the spark."),
                fact = l("Elektrische Weidezäune arbeiten genauso: Ein kurzer Impuls aus einer Spule liefert tausende Volt, aber nur für Millisekunden.", "Electric fences work the same way: a short pulse from a coil delivers thousands of volts, but only for milliseconds."),
            )
        },
        lab("phy.feldenergie", Topic.Magnetism, X, 0.6f) {
            val b = pick(3.0, 1.5, 7.0); val vol = pick(1.0, 0.5)
            val e = b * b / (2 * MU0) * vol / 1e6
            task(
                question = l("Im Inneren eines MRT-Magneten herrscht auf etwa ${n(vol, 1)} m³ ein Feld von ${n(b, 1)} T. Wie viel Energie steckt in diesem Magnetfeld?", "Inside an MRI magnet there is a field of ${n(b, 1)} T over a volume of about ${n(vol, 1)} m³. How much energy is stored in this magnetic field?"),
                steps = listOf(num(l("Energie in Megajoule", "Energy in megajoules"), e, "MJ", rel = 0.02, done = """§E \approx ${m(e, 2)}\ \text{MJ}§""",
                    hints = listOf(l("Kernidee: Ein Magnetfeld hat eine Energiedichte, die mit B² wächst.", "Key idea: a magnetic field has an energy density that grows with B²."), """§w = \dfrac{B^2}{2\mu_0},\quad E = w \cdot V§"""))),
                solution = listOf("""§w = \dfrac{${m(b, 1)}^2}{2 \cdot 4\pi \cdot 10^{-7}}\ \tfrac{\text{J}}{\text{m}^3} \approx ${sci(b * b / (2 * MU0))}\ \tfrac{\text{J}}{\text{m}^3}§""", """§E = ${sci(b * b / (2 * MU0))} \cdot ${m(vol, 1)}\ \text{J} \approx ${m(e, 2)}\ \text{MJ}§"""),
                background = l("Felder sind nicht nur Rechenhilfen, sie tragen echte Energie. Im MRT steckt sie in einem supraleitenden Magneten, der ohne Widerstand tagelang läuft. Fällt die Kühlung aus und der Supraleiter wird normal leitend, wird diese Energie schlagartig zu Wärme und verdampft das flüssige Helium: ein Quench.", "Fields aren't just calculation aids; they carry real energy. In an MRI scanner it is held in a superconducting magnet that runs for days without resistance. If the cooling fails and the superconductor becomes normally conducting, this energy suddenly turns into heat and boils off the liquid helium: a quench."),
                fact = l("Bei 3 T und einem Kubikmeter sind es 3,6 MJ, so viel Bewegungsenergie wie ein Auto mit 250 km/h hat.", "At 3 T and one cubic metre it is 3.6 MJ, as much kinetic energy as a car doing 250 km/h."),
            )
        },
    )
}
