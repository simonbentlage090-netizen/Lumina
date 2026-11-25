package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.sqrt

// =============================================================================================
// Vorlagen Elektrizität: Stromkreise, Ladung & Felder, Magnetismus & Induktion (seit 10.1).
// =============================================================================================

private const val K_C = 8.988e9
private const val EPS0 = 8.854e-12
private const val E_CHARGE = 1.602e-19
private const val M_E = 9.109e-31
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private object Bg3 {
    val ohm get() = l(
        "Strom ist fließende Ladung, Spannung der 'Druck', der sie antreibt, und der Widerstand bremst. Das Ohmsche Gesetz verbindet die drei: U = R · I. Ein Draht mit doppeltem Widerstand lässt bei gleicher Spannung nur halb so viel Strom durch. Die elektrische Leistung ist Spannung mal Strom – sie sagt, wie viel Energie pro Sekunde umgesetzt wird.",
        "Current is flowing charge, voltage the 'pressure' that drives it, and resistance holds it back. Ohm's law links the three: U = R · I. A wire with twice the resistance lets through only half the current at the same voltage. Electrical power is voltage times current – it tells you how much energy is converted per second.",
    )
    val circuits get() = l(
        "In Reihe fließt durch alle Bauteile derselbe Strom, die Widerstände addieren sich und die Spannung teilt sich auf. Parallel liegt an allen dieselbe Spannung, die Ströme addieren sich – und der Gesamtwiderstand wird kleiner als jeder einzelne. Deshalb sind alle Steckdosen im Haus parallel geschaltet: Jedes Gerät bekommt die vollen 230 V.",
        "In series the same current flows through every component, resistances add up and the voltage is shared. In parallel every branch gets the same voltage, the currents add up – and the total resistance becomes smaller than any single one. That's why all sockets in a house are wired in parallel: every device gets the full 230 V.",
    )
    val grid get() = l(
        "Strom wird über weite Strecken mit Hochspannung übertragen, weil die Verluste in den Leitungen mit dem Quadrat der Stromstärke wachsen: P = I² R. Bei gleicher Leistung bedeutet zehnmal höhere Spannung ein Zehntel der Stromstärke und nur ein Hundertstel der Verluste. Transformatoren machen den Wechsel zwischen den Spannungen möglich.",
        "Electricity is transmitted over long distances at high voltage because line losses grow with the square of the current: P = I² R. For the same power, ten times the voltage means a tenth of the current and only a hundredth of the losses. Transformers make it possible to switch between voltages.",
    )
    val coulomb get() = l(
        "Ladungen gibt es in zwei Sorten: Gleiche stoßen sich ab, verschiedene ziehen sich an. Die Kraft folgt demselben 1/r²-Gesetz wie die Gravitation, ist aber unvorstellbar viel stärker: Zwischen zwei Protonen ist die elektrische Abstoßung 10³⁶-mal größer als die Anziehung durch die Schwerkraft. Dass wir davon im Alltag wenig merken, liegt daran, dass sich positive und negative Ladungen fast perfekt ausgleichen.",
        "Charge comes in two kinds: like charges repel, opposite charges attract. The force follows the same 1/r² law as gravity but is unimaginably stronger: between two protons the electric repulsion is 10³⁶ times larger than their gravitational attraction. We notice little of it in daily life because positive and negative charges cancel almost perfectly.",
    )
    val field get() = l(
        "Ein elektrisches Feld beschreibt, welche Kraft eine Ladung an jedem Ort spüren würde – Kraft pro Ladung. Man stellt es mit Feldlinien dar, die von plus nach minus laufen. Zwischen zwei geladenen Platten ist das Feld überall gleich stark: E = U/d. Ab etwa 3 Millionen V/m schlägt Luft durch – dann gibt es Funken oder Blitze.",
        "An electric field describes the force a charge would feel at each point – force per charge. It is drawn with field lines running from plus to minus. Between two charged plates the field is equally strong everywhere: E = U/d. Above about 3 million V/m air breaks down – then you get sparks or lightning.",
    )
    val capacitor get() = l(
        "Ein Kondensator speichert Ladung auf zwei dicht beieinanderliegenden Platten. Seine Kapazität wächst mit der Fläche und sinkt mit dem Abstand. Anders als ein Akku kann er seine Energie in Sekundenbruchteilen abgeben – deshalb steckt einer in jedem Kamerablitz und in Defibrillatoren.",
        "A capacitor stores charge on two plates very close together. Its capacitance grows with the area and drops with the distance. Unlike a battery it can release its energy in a fraction of a second – that's why there is one in every camera flash and defibrillator.",
    )
    val lorentz get() = l(
        "Auf bewegte Ladungen wirkt im Magnetfeld eine Kraft senkrecht zur Bewegung und zum Feld – die Lorentzkraft. Sie ändert die Richtung, aber nicht das Tempo: Elektronen kreisen deshalb im Magnetfeld. Auf einen stromdurchflossenen Draht wirkt dieselbe Kraft; auf diesem Prinzip beruht jeder Elektromotor.",
        "Moving charges in a magnetic field feel a force perpendicular to their motion and to the field – the Lorentz force. It changes direction but not speed: that's why electrons go round in circles in a magnetic field. A current-carrying wire feels the same force; every electric motor is based on it.",
    )
    val induction get() = l(
        "Ändert sich das Magnetfeld durch eine Spule, entsteht in ihr eine Spannung – Induktion. Je schneller die Änderung und je mehr Windungen, desto größer die Spannung. Darauf beruhen Generatoren in Kraftwerken, Transformatoren, kabellose Ladegeräte und Induktionsherde. Ohne Änderung keine Spannung: Ein ruhender Magnet in einer Spule tut gar nichts.",
        "When the magnetic field through a coil changes, a voltage appears in it – induction. The faster the change and the more turns, the larger the voltage. Generators in power plants, transformers, wireless chargers and induction hobs all rely on it. No change, no voltage: a magnet sitting still inside a coil does nothing.",
    )
}

private object Fc3 {
    val ohm get() = card(l("Ohmsches Gesetz", "Ohm's law"), """§U = R \cdot I§""", l("Spannung = Widerstand mal Strom.", "Voltage = resistance times current."))
    val power get() = card(l("Elektrische Leistung", "Electrical power"), """§P = U \cdot I§""", l("1 W = 1 V · 1 A.", "1 W = 1 V · 1 A."))
    val series get() = card(l("Reihe und parallel", "Series and parallel"), """§R_{ges} = R_1 + R_2,\quad \dfrac{1}{R_{ges}} = \dfrac{1}{R_1} + \dfrac{1}{R_2}§""", l("Links Reihe, rechts parallel.", "Left series, right parallel."))
    val coulomb get() = card(l("Coulombsches Gesetz", "Coulomb's law"), """§F = k \dfrac{q_1 q_2}{r^2},\ k = 8{,}99 \cdot 10^{9}\ \tfrac{\text{N m}^2}{\text{C}^2}§""", l("Kraft zwischen zwei Punktladungen.", "Force between two point charges."))
    val capacitor get() = card(l("Plattenkondensator", "Parallel-plate capacitor"), """§C = \varepsilon_0 \dfrac{A}{d},\quad Q = C U,\quad W = \tfrac{1}{2} C U^2§""", l("ε₀ = 8,85 · 10⁻¹² F/m.", "ε₀ = 8.85 · 10⁻¹² F/m."))
    val lorentz get() = card(l("Lorentzkraft", "Lorentz force"), """§F = q v B,\quad F = B I L§""", l("Feld senkrecht zur Bewegung bzw. zum Draht.", "Field perpendicular to the motion or the wire."))
    val transformer get() = card(l("Transformator", "Transformer"), """§\dfrac{U_2}{U_1} = \dfrac{N_2}{N_1}§""", l("Ideal, ohne Verluste.", "Ideal, without losses."))
    val induction get() = card(l("Generator", "Generator"), """§U_0 = N B A \omega§""", l("Scheitelspannung einer rotierenden Spule.", "Peak voltage of a rotating coil."))
}

object LabFields {
    val all: List<TaskDef> get() = circuits + fields + magnetism

    // ===================================== STROMKREISE =====================================
    private val circuits = listOf(
        lab("lampe", Topic.Electricity, E, 0.2f) {
            val u = pick(3.0, 4.5, 9.0, 12.0)
            val r = tiered(listOf(6.0, 10.0, 20.0), 4.0, 60.0, 1.0)
            val i = u / r
            task(
                question = l("An einem Lämpchen mit ${n(r, 0)} Ω liegen ${n(u)} V. Wie viel Strom fließt?", "A small bulb with ${n(r, 0)} Ω is connected to ${n(u)} V. How much current flows?"),
                steps = listOf(num(l("Stromstärke", "Current"), i * 1000, "mA", rel = 0.02, done = """§I \approx ${m(sig(i * 1000))}\ \text{mA}§""",
                    hints = listOf(l("Kernidee: Ohmsches Gesetz nach I umstellen.", "Key idea: rearrange Ohm's law for I."), """§I = U / R§"""), formula = Fc3.ohm)),
                solution = listOf("""§I = \dfrac{${m(u)}\ \text{V}}{${m(r, 0)}\ \Omega} \approx ${m(i, 3)}\ \text{A} \approx ${m(sig(i * 1000))}\ \text{mA}§"""),
                background = Bg3.ohm,
            )
        },
        lab("ladegeraet", Topic.Electricity, E, 0.3f) {
            val (dev, u, i) = pick(Triple(l("Ein Handy-Schnellladegerät", "A fast phone charger"), 9.0, 2.0), Triple(l("Ein USB-Ladegerät", "A USB charger"), 5.0, 2.0), Triple(l("Ein Laptop-Netzteil", "A laptop power supply"), 20.0, 3.25))
            val p = u * i
            task(
                question = l("$dev liefert ${n(u)} V bei ${n(i)} A. Welche Leistung ist das?", "${dev} delivers ${n(u)} V at ${n(i)} A. What power is that?"),
                steps = listOf(num(l("Leistung", "Power"), p, "W", rel = 0.02, done = """§P = ${m(p)}\ \text{W}§""",
                    hints = listOf(l("Kernidee: Leistung ist Spannung mal Strom.", "Key idea: power is voltage times current."), """§P = U \cdot I§"""), formula = Fc3.power)),
                solution = listOf("""§P = ${m(u)}\ \text{V} \cdot ${m(i)}\ \text{A} = ${m(p)}\ \text{W}§"""),
                background = Bg3.ohm,
            )
        },
        lab("reihe", Topic.Electricity, M, 0.4f) {
            val r1 = tiered(listOf(10.0, 20.0), 5.0, 80.0, 1.0)
            val r2 = tiered(listOf(30.0, 40.0), 10.0, 120.0, 1.0)
            val u = pick(6.0, 9.0, 12.0)
            val i = u / (r1 + r2)
            val u2 = i * r2
            task(
                question = l("Zwei Widerstände (${n(r1, 0)} Ω und ${n(r2, 0)} Ω) sind in Reihe an ${n(u)} V angeschlossen.", "Two resistors (${n(r1, 0)} Ω and ${n(r2, 0)} Ω) are connected in series to ${n(u)} V."),
                steps = listOf(
                    num(l("Strom im Kreis", "Current in the circuit"), i * 1000, "mA", rel = 0.02, done = """§I \approx ${m(sig(i * 1000))}\ \text{mA}§""",
                        hints = listOf(l("Kernidee: In Reihe addieren sich die Widerstände.", "Key idea: in series the resistances add up."), """§I = \dfrac{U}{R_1 + R_2}§"""), formula = Fc3.series),
                    num(l("Spannung am zweiten Widerstand", "Voltage across the second resistor"), u2, "V", rel = 0.02, done = """§U_2 \approx ${m(sig(u2))}\ \text{V}§""",
                        hints = listOf(l("Durch beide fließt derselbe Strom.", "The same current flows through both."), """§U_2 = R_2 \cdot I§"""), formula = Fc3.ohm),
                ),
                solution = listOf("""§I = \dfrac{${m(u)}\ \text{V}}{${m(r1 + r2, 0)}\ \Omega} \approx ${m(sig(i * 1000))}\ \text{mA}§""", """§U_2 = ${m(r2, 0)}\ \Omega \cdot ${m(i, 4)}\ \text{A} \approx ${m(sig(u2))}\ \text{V}§"""),
                background = Bg3.circuits,
            )
        },
        lab("parallel", Topic.Electricity, M, 0.5f) {
            val r1 = tiered(listOf(20.0, 30.0, 60.0), 10.0, 100.0, 1.0)
            val r2 = tiered(listOf(20.0, 60.0), 10.0, 150.0, 1.0)
            val rp = 1 / (1 / r1 + 1 / r2)
            task(
                question = l("Zwei Widerstände (${n(r1, 0)} Ω und ${n(r2, 0)} Ω) werden parallel geschaltet. Wie groß ist der Gesamtwiderstand?", "Two resistors (${n(r1, 0)} Ω and ${n(r2, 0)} Ω) are connected in parallel. What is the total resistance?"),
                steps = listOf(num(l("Gesamtwiderstand", "Total resistance"), rp, "Ω", rel = 0.02, done = """§R_{ges} \approx ${m(sig(rp))}\ \Omega§""",
                    hints = listOf(l("Kernidee: Parallel addieren sich die Kehrwerte – das Ergebnis ist kleiner als der kleinste Widerstand.", "Key idea: in parallel the reciprocals add up – the result is smaller than the smallest resistor."), """§\dfrac{1}{R} = \dfrac{1}{${m(r1, 0)}} + \dfrac{1}{${m(r2, 0)}}§"""), formula = Fc3.series)),
                solution = listOf("""§R_{ges} = \dfrac{R_1 R_2}{R_1 + R_2} = \dfrac{${m(r1, 0)} \cdot ${m(r2, 0)}}{${m(r1 + r2, 0)}}\ \Omega \approx ${m(sig(rp))}\ \Omega§"""),
                background = Bg3.circuits,
            )
        },
        lab("sicherung", Topic.Electricity, H, 0.5f) {
            val devs = listOf(l("Wasserkocher", "kettle") to 2000.0, l("Toaster", "toaster") to 900.0, l("Föhn", "hair dryer") to 1800.0, l("Mikrowelle", "microwave") to 1200.0, l("Heizlüfter", "fan heater") to 2000.0)
            val chosen = devs.shuffled(rnd).take(if (hard) 4 else 3)
            val pTot = chosen.sumOf { it.second }
            val i = pTot / 230
            task(
                question = l("An einer Steckdosenleiste (230 V, Sicherung 16 A) laufen gleichzeitig: ${chosen.joinToString { it.first + " (" + n(it.second, 0) + " W)" }}.", "On one power strip (230 V, 16 A fuse) these run at the same time: ${chosen.joinToString { it.first + " (" + n(it.second, 0) + " W)" }}."),
                steps = listOf(
                    num(l("Gesamtstrom", "Total current"), i, "A", rel = 0.02, done = """§I \approx ${m(sig(i))}\ \text{A}§""",
                        hints = listOf(l("Kernidee: Alle Geräte hängen parallel an 230 V – die Leistungen (und Ströme) addieren sich.", "Key idea: all devices are in parallel on 230 V – powers (and currents) add up."), """§I = \dfrac{P_{ges}}{U}§"""), formula = Fc3.power),
                    choice(l("Fliegt die Sicherung raus?", "Does the fuse blow?"), if (i > 16) listOf(l("Ja", "Yes"), l("Nein", "No")) else listOf(l("Nein", "No"), l("Ja", "Yes")),
                        done = if (i > 16) l("Ja, mehr als 16 A", "Yes, more than 16 A") else l("Nein, unter 16 A", "No, below 16 A"),
                        hints = listOf(l("Vergleiche den Gesamtstrom mit 16 A.", "Compare the total current with 16 A."))),
                ),
                solution = listOf("""§P_{ges} = ${m(pTot, 0)}\ \text{W},\quad I = \dfrac{${m(pTot, 0)}\ \text{W}}{230\ \text{V}} \approx ${m(sig(i))}\ \text{A}§""",
                    if (i > 16) l("Das ist mehr als 16 A – die Sicherung löst aus.", "That's more than 16 A – the fuse trips.") else l("Das ist weniger als 16 A – die Sicherung hält.", "That's less than 16 A – the fuse holds.")),
                background = Bg3.circuits,
            )
        },
        lab("hochspannung", Topic.Electricity, X, 0.6f) {
            val pMw = tiered(listOf(100.0, 500.0), 50.0, 1000.0, 10.0)
            val r = tiered(listOf(5.0, 10.0), 2.0, 20.0, 0.5)
            val u1 = 110e3
            val u2 = 380e3
            val i1 = pMw * 1e6 / u1
            val i2 = pMw * 1e6 / u2
            val loss1 = i1 * i1 * r / (pMw * 1e6) * 100
            val loss2 = i2 * i2 * r / (pMw * 1e6) * 100
            task(
                question = l("Ein Kraftwerk schickt ${n(pMw, 0)} MW über eine Leitung mit ${n(r)} Ω Widerstand.", "A power plant sends ${n(pMw, 0)} MW through a line with a resistance of ${n(r)} Ω."),
                steps = listOf(
                    num(l("Verlust in Prozent bei 110 kV", "Loss in percent at 110 kV"), loss1, "%", rel = 0.03, done = """§\approx ${m(sig(loss1))}\ \text{%}§""",
                        hints = listOf(l("Kernidee: Erst die Stromstärke I = P/U, dann die Verlustleistung I²R.", "Key idea: first the current I = P/U, then the power lost I²R."), """§I = \dfrac{P}{U},\ P_V = I^2 R§"""),
                        formula = card(l("Leitungsverlust", "Line loss"), """§P_V = I^2 R = \left(\dfrac{P}{U}\right)^2 R§""", l("Wächst mit dem Quadrat des Stroms.", "Grows with the square of the current."))),
                    num(l("Und bei 380 kV?", "And at 380 kV?"), loss2, "%", rel = 0.03, done = """§\approx ${m(sig(loss2))}\ \text{%}§""",
                        hints = listOf(l("Gleiche Leistung, höhere Spannung: kleinerer Strom.", "Same power, higher voltage: smaller current."), """§P_V \propto 1/U^2§""")),
                ),
                solution = listOf(
                    """§I_{110} = \dfrac{${sci(pMw * 1e6)}\ \text{W}}{1{,}1 \cdot 10^5\ \text{V}} \approx ${m(sig(i1))}\ \text{A},\ P_V \approx ${m(sig(loss1))}\ \text{%}§""",
                    """§I_{380} = \dfrac{${sci(pMw * 1e6)}\ \text{W}}{3{,}8 \cdot 10^5\ \text{V}} \approx ${m(sig(i2))}\ \text{A},\ P_V \approx ${m(sig(loss2))}\ \text{%}§""",
                ),
                background = Bg3.grid,
            )
        },
    )

    // ===================================== LADUNG & FELDER =====================================
    private val fields = listOf(
        lab("ballon", Topic.Fields, E, 0.2f) {
            val opts = pick(
                l("Du reibst einen Luftballon an deinen Haaren. Danach stehen die Haare zum Ballon hin ab. Warum?", "You rub a balloon on your hair. Afterwards your hair stands up towards the balloon. Why?") to listOf(
                    l("Ballon und Haare sind entgegengesetzt geladen und ziehen sich an.", "Balloon and hair have opposite charges and attract each other."),
                    l("Der Ballon ist magnetisch geworden.", "The balloon has become magnetic."),
                    l("Die Haare sind gleich geladen wie der Ballon und werden angezogen.", "The hair has the same charge as the balloon and is attracted."),
                    l("Durch die Reibung sind die Haare warm und steigen auf.", "Friction has warmed the hair, so it rises."),
                ),
                l("Warum stehen nach dem Reiben die einzelnen Haare auch voneinander ab?", "Why do the individual hairs also spread apart after rubbing?") to listOf(
                    l("Alle Haare tragen die gleiche Ladung und stoßen sich ab.", "All the hairs carry the same charge and repel each other."),
                    l("Sie ziehen sich gegenseitig an.", "They attract each other."),
                    l("Die Luft zwischen den Haaren dehnt sich aus.", "The air between the hairs expands."),
                    l("Die Haare sind nach dem Reiben leichter.", "The hairs are lighter after rubbing."),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Beim Reiben wandern Elektronen von einem Material aufs andere.", "Key idea: rubbing moves electrons from one material to the other."), l("Gleiche Ladungen stoßen sich ab, verschiedene ziehen sich an.", "Like charges repel, opposite charges attract.")), formula = Fc3.coulomb)),
                solution = listOf(l("Beim Reiben gehen Elektronen von den Haaren auf den Ballon über: Er wird negativ, die Haare positiv. Verschiedene Ladungen ziehen sich an, gleiche stoßen sich ab.", "Rubbing transfers electrons from the hair to the balloon: it becomes negative, the hair positive. Opposite charges attract, like charges repel.")),
                background = Bg3.coulomb,
            )
        },
        lab("coulomb", Topic.Fields, M, 0.4f) {
            val q1 = tiered(listOf(1.0, 2.0), 0.5, 5.0, 0.1)
            val q2 = tiered(listOf(1.0, 3.0), 0.5, 5.0, 0.1)
            val r = tiered(listOf(0.1, 0.2), 0.05, 0.5, 0.01)
            val f = K_C * q1 * 1e-6 * q2 * 1e-6 / (r * r)
            task(
                question = l("Zwei kleine Kugeln tragen ${n(q1)} µC und ${n(q2)} µC und sind ${n(r * 100, 0)} cm voneinander entfernt. Wie groß ist die Kraft zwischen ihnen?", "Two small spheres carry ${n(q1)} µC and ${n(q2)} µC and are ${n(r * 100, 0)} cm apart. How large is the force between them?"),
                steps = listOf(num(l("Kraft", "Force"), f, "N", rel = 0.03, done = """§F \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Coulombsches Gesetz – Mikrocoulomb in Coulomb umrechnen (10⁻⁶).", "Key idea: Coulomb's law – convert microcoulombs into coulombs (10⁻⁶)."), """§F = k \dfrac{q_1 q_2}{r^2}§"""), formula = Fc3.coulomb)),
                solution = listOf("""§F = 8{,}99 \cdot 10^9 \cdot \dfrac{${sci(q1 * 1e-6)} \cdot ${sci(q2 * 1e-6)}}{(${m(r)})^2}\ \text{N} \approx ${m(sig(f))}\ \text{N}§"""),
                background = Bg3.coulomb,
            )
        },
        lab("plattenfeld", Topic.Fields, E, 0.4f) {
            val u = tiered(listOf(100.0, 500.0, 1000.0), 50.0, 5000.0, 50.0)
            val d = tiered(listOf(1.0, 2.0, 5.0), 0.5, 10.0, 0.5)
            val e = u / (d / 1000)
            task(
                question = l("Zwei Metallplatten mit ${n(d)} mm Abstand liegen an ${n(u, 0)} V. Wie stark ist das elektrische Feld dazwischen?", "Two metal plates ${n(d)} mm apart are connected to ${n(u, 0)} V. How strong is the electric field between them?"),
                steps = listOf(num(l("Feldstärke in kV/m", "Field strength in kV/m"), e / 1000, "kV/m", rel = 0.02, done = """§E \approx ${m(sig(e / 1000))}\ \text{kV/m}§""",
                    hints = listOf(l("Kernidee: Im Plattenkondensator ist das Feld überall gleich: Spannung durch Abstand.", "Key idea: in a parallel-plate capacitor the field is the same everywhere: voltage over distance."), """§E = U / d§"""),
                    formula = card(l("Homogenes Feld", "Uniform field"), """§E = \dfrac{U}{d}§""", l("Einheit V/m = N/C.", "Unit V/m = N/C."))),
                    ),
                solution = listOf("""§E = \dfrac{${m(u, 0)}\ \text{V}}{${sci(d / 1000)}\ \text{m}} \approx ${sci(e)}\ \tfrac{\text{V}}{\text{m}}§"""),
                background = Bg3.field,
            )
        },
        lab("kondensator", Topic.Fields, H, 0.5f) {
            val a = tiered(listOf(0.01, 0.04), 0.005, 0.1, 0.005)
            val d = tiered(listOf(0.1, 0.5), 0.05, 1.0, 0.05) // mm
            val u = tiered(listOf(10.0, 50.0), 5.0, 200.0, 5.0)
            val c = EPS0 * a / (d / 1000)
            val q = c * u
            task(
                question = l("Ein Plattenkondensator hat ${n(a * 1e4, 0)} cm² Plattenfläche und ${n(d)} mm Abstand (Luft). Er liegt an ${n(u, 0)} V.", "A parallel-plate capacitor has a plate area of ${n(a * 1e4, 0)} cm² and a gap of ${n(d)} mm (air). It is connected to ${n(u, 0)} V."),
                steps = listOf(
                    num(l("Kapazität in Pikofarad", "Capacitance in picofarads"), c * 1e12, "pF", rel = 0.03, done = """§C \approx ${m(sig(c * 1e12))}\ \text{pF}§""",
                        hints = listOf(l("Kernidee: Große Fläche, kleiner Abstand – große Kapazität.", "Key idea: large area, small gap – large capacitance."), """§C = \varepsilon_0 A / d§"""), formula = Fc3.capacitor),
                    num(l("Gespeicherte Ladung in Nanocoulomb", "Stored charge in nanocoulombs"), q * 1e9, "nC", rel = 0.03, done = """§Q \approx ${m(sig(q * 1e9))}\ \text{nC}§""",
                        hints = listOf(l("Ladung ist Kapazität mal Spannung.", "Charge is capacitance times voltage."), """§Q = C U§""")),
                ),
                solution = listOf("""§C = 8{,}85 \cdot 10^{-12} \cdot \dfrac{${sci(a)}}{${sci(d / 1000)}}\ \text{F} \approx ${sci(c)}\ \text{F}§""", """§Q = ${sci(c)}\ \text{F} \cdot ${m(u, 0)}\ \text{V} \approx ${sci(q)}\ \text{C}§"""),
                background = Bg3.capacitor,
            )
        },
        lab("kamerablitz", Topic.Fields, H, 0.5f) {
            val cMicro = tiered(listOf(100.0, 200.0), 80.0, 500.0, 10.0)
            val u = tiered(listOf(300.0, 330.0), 250.0, 400.0, 10.0)
            val ms = tiered(listOf(1.0, 2.0), 0.5, 3.0, 0.1)
            val w = 0.5 * cMicro * 1e-6 * u * u
            val p = w / (ms / 1000)
            task(
                question = l("Im Blitzgerät einer Kamera wird ein Kondensator (${n(cMicro, 0)} µF) auf ${n(u, 0)} V geladen und in ${n(ms)} ms entladen.", "In a camera flash a capacitor (${n(cMicro, 0)} µF) is charged to ${n(u, 0)} V and discharged in ${n(ms)} ms."),
                steps = listOf(
                    num(l("Gespeicherte Energie", "Stored energy"), w, "J", rel = 0.02, done = """§W \approx ${m(sig(w))}\ \text{J}§""",
                        hints = listOf(l("Kernidee: Die Energie im Kondensator wächst mit dem Quadrat der Spannung.", "Key idea: the energy in a capacitor grows with the square of the voltage."), """§W = \tfrac{1}{2} C U^2§"""), formula = Fc3.capacitor),
                    num(l("Mittlere Leistung beim Blitz in Kilowatt", "Average power of the flash in kilowatts"), p / 1000, "kW", rel = 0.03, done = """§P \approx ${m(sig(p / 1000))}\ \text{kW}§""",
                        hints = listOf(l("Energie durch Zeit.", "Energy over time."), """§P = W / t§""")),
                ),
                solution = listOf("""§W = \tfrac{1}{2} \cdot ${sci(cMicro * 1e-6)}\ \text{F} \cdot (${m(u, 0)}\ \text{V})^2 \approx ${m(sig(w))}\ \text{J}§""", """§P = \dfrac{${m(sig(w))}\ \text{J}}{${sci(ms / 1000)}\ \text{s}} \approx ${m(sig(p / 1000))}\ \text{kW}§"""),
                background = Bg3.capacitor,
            )
        },
        lab("elektronenkanone", Topic.Fields, H, 0.6f) {
            val u = tiered(listOf(100.0, 1000.0), 50.0, 5000.0, 50.0)
            val v = sqrt(2 * E_CHARGE * u / M_E)
            task(
                question = l("Ein Elektron startet aus der Ruhe und wird mit ${n(u, 0)} V beschleunigt (wie früher in Röhrenfernsehern).", "An electron starts at rest and is accelerated through ${n(u, 0)} V (as in old tube TVs)."),
                given = listOf("""§e = 1{,}602 \cdot 10^{-19}\ \text{C},\ m_e = 9{,}11 \cdot 10^{-31}\ \text{kg}§"""),
                steps = listOf(num(l("Endgeschwindigkeit in km/s", "Final speed in km/s"), v / 1000, "km/s", rel = 0.03, done = """§v \approx ${m(sig(v / 1000))}\ \text{km/s}§""",
                    hints = listOf(l("Kernidee: Die elektrische Energie e·U wird ganz zu Bewegungsenergie.", "Key idea: the electrical energy e·U turns completely into kinetic energy."), """§e U = \tfrac{1}{2} m v^2 \Rightarrow v = \sqrt{2 e U / m}§"""),
                    formula = card(l("Beschleunigung im Feld", "Acceleration in a field"), """§E_{kin} = q U§""", l("1 eV = Energie eines Elektrons nach 1 V.", "1 eV = energy of an electron after 1 V.")))),
                solution = listOf("""§v = \sqrt{\dfrac{2 \cdot 1{,}602 \cdot 10^{-19} \cdot ${m(u, 0)}}{9{,}11 \cdot 10^{-31}}}\ \tfrac{\text{m}}{\text{s}} \approx ${sci(v)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = Bg3.field,
                fact = l("Schon bei einigen Kilovolt erreichen Elektronen mehrere Prozent der Lichtgeschwindigkeit – ab etwa 100 kV muss man relativistisch rechnen.", "At just a few kilovolts electrons reach several percent of the speed of light – above about 100 kV you have to use relativity."),
            )
        },
    )

    // ===================================== MAGNETISMUS =====================================
    private val magnetism = listOf(
        lab("kompass", Topic.Magnetism, E, 0.2f) {
            val opts = pick(
                l("Die Nordspitze einer Kompassnadel zeigt nach Norden. Was befindet sich also in der Nähe des geografischen Nordpols?", "The north tip of a compass needle points north. So what is near the geographic North Pole?") to listOf(
                    l("Ein magnetischer Südpol", "A magnetic south pole"), l("Ein magnetischer Nordpol", "A magnetic north pole"), l("Gar kein Magnetpol", "No magnetic pole at all"), l("Ein riesiges Stück Eisen", "A huge lump of iron"),
                ),
                l("Du zerbrichst einen Stabmagneten in der Mitte. Was hast du dann?", "You break a bar magnet in the middle. What do you get?") to listOf(
                    l("Zwei kleinere Magnete mit je Nord- und Südpol", "Two smaller magnets, each with a north and a south pole"), l("Einen Nordpol und einen Südpol einzeln", "A separate north pole and south pole"), l("Zwei unmagnetische Stücke", "Two non-magnetic pieces"), l("Zwei Nordpole", "Two north poles"),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Ungleiche Pole ziehen sich an.", "Key idea: opposite poles attract."), l("Magnetpole kommen immer paarweise vor.", "Magnetic poles always come in pairs.")), formula = Fc3.lorentz)),
                solution = listOf(l("Ein Nordpol wird von einem Südpol angezogen – also liegt im Norden ein magnetischer Südpol. Und jedes Stück eines Magneten hat wieder zwei Pole: Einzelne Magnetpole hat noch niemand gefunden.", "A north pole is attracted by a south pole – so there is a magnetic south pole in the north. And every piece of a magnet again has two poles: nobody has ever found a single magnetic pole.")),
                background = l(
                    "Magnete haben immer einen Nord- und einen Südpol. Die Erde ist selbst ein riesiger Magnet, angetrieben von Strömen aus flüssigem Eisen in ihrem äußeren Kern. Das Erdmagnetfeld lenkt den Sonnenwind ab und erzeugt Polarlichter; etwa alle paar hunderttausend Jahre polt es sich um.",
                    "Magnets always have a north and a south pole. The Earth itself is a giant magnet, driven by currents of liquid iron in its outer core. Earth's magnetic field deflects the solar wind and creates the auroras; every few hundred thousand years it flips.",
                ),
            )
        },
        lab("leiterschaukel", Topic.Magnetism, M, 0.4f) {
            val b = tiered(listOf(0.1, 0.2), 0.05, 0.5, 0.01)
            val i = tiered(listOf(2.0, 5.0), 1.0, 10.0, 0.5)
            val len = tiered(listOf(0.1, 0.2), 0.05, 0.3, 0.01)
            val f = b * i * len
            task(
                question = l("Ein gerader Draht (${n(len * 100, 0)} cm im Feld) führt ${n(i)} A und liegt senkrecht in einem Magnetfeld von ${n(b)} T. Wie groß ist die Kraft auf ihn?", "A straight wire (${n(len * 100, 0)} cm inside the field) carries ${n(i)} A and lies perpendicular to a magnetic field of ${n(b)} T. How large is the force on it?"),
                steps = listOf(num(l("Kraft in Millinewton", "Force in millinewtons"), f * 1000, "mN", rel = 0.02, done = """§F \approx ${m(sig(f * 1000))}\ \text{mN}§""",
                    hints = listOf(l("Kernidee: Lorentzkraft auf einen Leiter.", "Key idea: Lorentz force on a conductor."), """§F = B I L§"""), formula = Fc3.lorentz)),
                solution = listOf("""§F = ${m(b)}\ \text{T} \cdot ${m(i)}\ \text{A} \cdot ${m(len)}\ \text{m} \approx ${m(sig(f * 1000))}\ \text{mN}§"""),
                background = Bg3.lorentz,
            )
        },
        lab("trafo", Topic.Magnetism, M, 0.4f) {
            val n1 = tiered(listOf(1000.0, 2000.0), 500.0, 4000.0, 50.0)
            val u2 = pick(6.0, 9.0, 12.0, 24.0)
            val n2 = n1 * u2 / 230
            task(
                question = l("Ein Netzteil-Transformator hat ${n(n1, 0)} Windungen auf der 230-V-Seite. Wie viele Windungen braucht die andere Spule für ${n(u2)} V?", "A power-supply transformer has ${n(n1, 0)} turns on the 230 V side. How many turns does the other coil need for ${n(u2)} V?"),
                steps = listOf(num(l("Windungszahl", "Number of turns"), n2, "", rel = 0.03, done = """§N_2 \approx ${m(sig(n2))}§""",
                    hints = listOf(l("Kernidee: Die Spannungen verhalten sich wie die Windungszahlen.", "Key idea: the voltages are in the same ratio as the numbers of turns."), """§N_2 = N_1 \dfrac{U_2}{U_1}§"""), formula = Fc3.transformer)),
                solution = listOf("""§N_2 = ${m(n1, 0)} \cdot \dfrac{${m(u2)}\ \text{V}}{230\ \text{V}} \approx ${m(sig(n2))}§"""),
                background = Bg3.induction,
            )
        },
        lab("elektronkreis", Topic.Magnetism, H, 0.5f) {
            val vKm = tiered(listOf(1000.0, 5000.0), 500.0, 20000.0, 100.0)
            val bmT = tiered(listOf(1.0, 2.0), 0.5, 5.0, 0.1)
            val r = M_E * vKm * 1000 / (E_CHARGE * bmT / 1000)
            task(
                question = l("Ein Elektron fliegt mit ${n(vKm, 0)} km/s senkrecht in ein Magnetfeld von ${n(bmT)} mT. Auf welchem Kreis bewegt es sich?", "An electron flies at ${n(vKm, 0)} km/s perpendicular into a magnetic field of ${n(bmT)} mT. What circle does it move on?"),
                given = listOf("""§e = 1{,}602 \cdot 10^{-19}\ \text{C},\ m_e = 9{,}11 \cdot 10^{-31}\ \text{kg}§"""),
                steps = listOf(num(l("Bahnradius in Millimetern", "Radius in millimetres"), r * 1000, "mm", rel = 0.03, done = """§r \approx ${m(sig(r * 1000))}\ \text{mm}§""",
                    hints = listOf(l("Kernidee: Die Lorentzkraft ist die Zentripetalkraft.", "Key idea: the Lorentz force is the centripetal force."), """§e v B = \dfrac{m v^2}{r} \Rightarrow r = \dfrac{m v}{e B}§"""), formula = Fc3.lorentz)),
                solution = listOf("""§r = \dfrac{9{,}11 \cdot 10^{-31} \cdot ${sci(vKm * 1000)}}{1{,}602 \cdot 10^{-19} \cdot ${sci(bmT / 1000)}}\ \text{m} \approx ${m(sig(r * 1000))}\ \text{mm}§"""),
                background = Bg3.lorentz,
            )
        },
        lab("generator", Topic.Magnetism, H, 0.5f) {
            val nTurns = tiered(listOf(100.0, 200.0), 50.0, 500.0, 10.0)
            val b = tiered(listOf(0.1, 0.2), 0.05, 0.5, 0.01)
            val a = tiered(listOf(0.01, 0.02), 0.005, 0.05, 0.001)
            val f = pick(50.0, 25.0, 60.0)
            val u = nTurns * b * a * 2 * PI * f
            task(
                question = l("Eine Spule mit ${n(nTurns, 0)} Windungen (Fläche ${n(a * 1e4, 0)} cm²) dreht sich ${n(f, 0)}-mal pro Sekunde in einem Magnetfeld von ${n(b)} T.", "A coil with ${n(nTurns, 0)} turns (area ${n(a * 1e4, 0)} cm²) rotates ${n(f, 0)} times per second in a magnetic field of ${n(b)} T."),
                steps = listOf(num(l("Scheitelwert der induzierten Spannung", "Peak induced voltage"), u, "V", rel = 0.03, done = """§U_0 \approx ${m(sig(u))}\ \text{V}§""",
                    hints = listOf(l("Kernidee: Der Fluss durch die Spule ändert sich sinusförmig; die Spannung ist am größten, wenn er sich am schnellsten ändert.", "Key idea: the flux through the coil changes sinusoidally; the voltage peaks when it changes fastest."), """§U_0 = N B A \omega,\ \omega = 2\pi f§"""), formula = Fc3.induction)),
                solution = listOf("""§U_0 = ${m(nTurns, 0)} \cdot ${m(b)} \cdot ${m(a, 3)} \cdot 2\pi \cdot ${m(f, 0)}\ \text{V} \approx ${m(sig(u))}\ \text{V}§"""),
                background = Bg3.induction,
            )
        },
        lab("zyklotron", Topic.Magnetism, X, 0.6f) {
            val b = tiered(listOf(1.0, 1.5), 0.5, 2.0, 0.1)
            val rMax = tiered(listOf(0.3, 0.5), 0.2, 1.0, 0.05)
            val mp = 1.673e-27
            val f = E_CHARGE * b / (2 * PI * mp)
            val ekin = (E_CHARGE * b * rMax).let { it * it / (2 * mp) } / E_CHARGE / 1e6
            task(
                question = l("In einem Zyklotron kreisen Protonen in einem Magnetfeld von ${n(b)} T. Der größte Bahnradius ist ${n(rMax * 100, 0)} cm.", "In a cyclotron protons circle in a magnetic field of ${n(b)} T. The largest orbit radius is ${n(rMax * 100, 0)} cm."),
                given = listOf("""§m_p = 1{,}673 \cdot 10^{-27}\ \text{kg},\ e = 1{,}602 \cdot 10^{-19}\ \text{C}§"""),
                steps = listOf(
                    num(l("Umlauffrequenz in MHz", "Orbital frequency in MHz"), f / 1e6, "MHz", rel = 0.03, done = """§f \approx ${m(sig(f / 1e6))}\ \text{MHz}§""",
                        hints = listOf(l("Kernidee: Aus r = mv/(eB) folgt, dass die Umlaufzeit nicht vom Tempo abhängt.", "Key idea: from r = mv/(eB) it follows that the orbital period doesn't depend on speed."), """§f = \dfrac{e B}{2\pi m}§"""), formula = Fc3.lorentz),
                    num(l("Endenergie der Protonen in MeV", "Final proton energy in MeV"), ekin, "MeV", rel = 0.03, done = """§E \approx ${m(sig(ekin))}\ \text{MeV}§""",
                        hints = listOf(l("Am größten Radius ist v = eBr/m, dann ½mv² in eV umrechnen.", "At the largest radius v = eBr/m, then convert ½mv² into eV."), """§E = \dfrac{(e B r)^2}{2 m}§""")),
                ),
                solution = listOf("""§f = \dfrac{1{,}602 \cdot 10^{-19} \cdot ${m(b)}}{2\pi \cdot 1{,}673 \cdot 10^{-27}}\ \text{Hz} \approx ${m(sig(f / 1e6))}\ \text{MHz}§""", """§E = \dfrac{(e \cdot ${m(b)} \cdot ${m(rMax)})^2}{2 m_p} \approx ${m(sig(ekin))}\ \text{MeV}§"""),
                background = Bg3.lorentz,
                fact = l("Zyklotrone erzeugen heute radioaktive Stoffe für die Medizin, zum Beispiel für PET-Scans in Krankenhäusern.", "Today cyclotrons produce radioactive substances for medicine, for example for PET scans in hospitals."),
            )
        },
    )
}
