package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

// =============================================================================================
// Vorlagen Mechanik II: Drehung, Gravitation, Schwingungen (seit 10.1).
// =============================================================================================

private const val G = 9.81
private const val GRAV = 6.674e-11
private const val M_EARTH = 5.972e24
private const val R_EARTH = 6.371e6
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private object Bg2 {
    val torque get() = l(
        "Ein Drehmoment ist das Drehende an einer Kraft: Kraft mal Hebelarm. Dieselbe Kraft dreht stärker, je weiter weg vom Drehpunkt sie angreift. Deshalb sitzt die Türklinke außen und nicht neben den Scharnieren, und deshalb löst man festsitzende Schrauben mit einem langen Schlüssel.",
        "Torque is the turning effect of a force: force times lever arm. The same force turns more strongly the farther from the pivot it acts. That's why the door handle sits at the outer edge and not next to the hinges, and why you loosen stuck bolts with a long wrench.",
    )
    val circle get() = l(
        "Auf einer Kreisbahn ändert sich ständig die Richtung – das ist eine Beschleunigung, auch bei gleichem Tempo. Sie zeigt zur Kreismitte (Zentripetalbeschleunigung v²/r). Damit ein Auto die Kurve schafft, muss die Reibung der Reifen diese Kraft aufbringen. Was wir als 'Fliehkraft' spüren, ist nur unsere Trägheit, die geradeaus will.",
        "On a circular path the direction changes all the time – that is an acceleration, even at constant speed. It points to the centre (centripetal acceleration v²/r). For a car to make a bend, the tyres' friction has to supply this force. What we feel as 'centrifugal force' is just our inertia wanting to go straight on.",
    )
    val angular get() = l(
        "Drehimpuls ist das Gegenstück zum Impuls für Drehungen: Trägheitsmoment mal Winkelgeschwindigkeit. Ohne äußeres Drehmoment bleibt er erhalten. Zieht eine Eiskunstläuferin die Arme an, wird ihr Trägheitsmoment kleiner – also dreht sie sich schneller. Dasselbe passiert, wenn ein Stern zum Neutronenstern kollabiert: Er rotiert dann hunderte Male pro Sekunde.",
        "Angular momentum is the rotational counterpart of momentum: moment of inertia times angular velocity. Without an external torque it is conserved. When a figure skater pulls in her arms, her moment of inertia shrinks – so she spins faster. The same happens when a star collapses into a neutron star: it then spins hundreds of times per second.",
    )
    val rolling get() = l(
        "Ein rollender Körper teilt seine Energie auf: Ein Teil steckt in der Vorwärtsbewegung, ein Teil in der Drehung. Wie viel in die Drehung geht, hängt davon ab, wie weit außen die Masse sitzt. Ein Ring (Masse ganz außen) rollt deshalb langsamer eine Rampe hinunter als eine volle Kugel – egal, wie schwer oder groß sie sind.",
        "A rolling object splits its energy: part goes into moving forward, part into spinning. How much goes into spinning depends on how far out the mass sits. A ring (mass all on the rim) therefore rolls down a ramp more slowly than a solid ball – no matter how heavy or large they are.",
    )
    val gravity get() = l(
        "Jede Masse zieht jede andere an – mit einer Kraft, die mit dem Quadrat des Abstands abnimmt. Weil die Gravitationskonstante winzig ist, merkt man das erst bei Planeten. An der Oberfläche eines Himmelskörpers bestimmen Masse und Radius die Fallbeschleunigung g: Der Mond hat ein Sechstel, Jupiter zweieinhalbmal so viel wie die Erde.",
        "Every mass attracts every other mass – with a force that falls off with the square of the distance. Because the gravitational constant is tiny, you only notice it with planets. At the surface of a body, its mass and radius set the gravitational acceleration g: the Moon has a sixth of Earth's, Jupiter two and a half times as much.",
    )
    val orbit get() = l(
        "Ein Satellit fällt ständig zur Erde – er ist nur so schnell seitwärts unterwegs, dass die Erde unter ihm wegkrümmt. Je weiter draußen die Bahn, desto langsamer und länger der Umlauf (drittes Keplersches Gesetz: T² wächst mit r³). In etwa 36.000 km Höhe dauert ein Umlauf genau einen Tag – dort stehen Wetter- und Fernsehsatelliten scheinbar still am Himmel.",
        "A satellite is constantly falling towards Earth – it's just moving sideways so fast that the Earth curves away beneath it. The farther out the orbit, the slower and longer the trip around (Kepler's third law: T² grows with r³). At about 36,000 km altitude one orbit takes exactly one day – that's where weather and TV satellites seem to stand still in the sky.",
    )
    val escape get() = l(
        "Fluchtgeschwindigkeit ist das Tempo, mit dem man von einem Himmelskörper für immer wegkommt, ohne weiter anzutreiben: Die Bewegungsenergie muss die ganze Lageenergie im Schwerefeld aufwiegen. Von der Erde sind das 11,2 km/s, vom Mond 2,4 km/s – und von einem kleinen Asteroiden so wenig, dass man ihn mit einem Sprung verlassen könnte.",
        "Escape velocity is the speed needed to leave a body forever without further thrust: the kinetic energy must outweigh the entire potential energy in its gravitational field. From Earth that's 11.2 km/s, from the Moon 2.4 km/s – and from a small asteroid so little that you could leave it with a jump.",
    )
    val tides get() = l(
        "Gezeitenkräfte entstehen, weil die Schwerkraft mit dem Abstand abnimmt: Die nahe Seite eines Körpers wird stärker angezogen als die ferne. Der Unterschied wächst mit 1/r³ – deshalb hebt der nahe Mond die Meere stärker als die viel schwerere, aber ferne Sonne. Nahe an einem Schwarzen Loch wird dieser Unterschied so groß, dass er alles in die Länge zieht ('Spaghettifizierung').",
        "Tidal forces arise because gravity weakens with distance: the near side of a body is pulled harder than the far side. The difference grows like 1/r³ – which is why the nearby Moon raises the oceans more than the much heavier but distant Sun. Close to a black hole the difference becomes so large that it stretches everything out ('spaghettification').",
    )
    val pendulum get() = l(
        "Ein Pendel schwingt unabhängig davon, wie schwer es ist und (bei kleinen Ausschlägen) wie weit es ausschlägt – die Schwingungsdauer hängt nur von der Länge und von g ab. Galilei soll das an einem schwingenden Kronleuchter im Dom von Pisa bemerkt haben. Darauf beruhten jahrhundertelang die genauesten Uhren der Welt.",
        "A pendulum swings independently of how heavy it is and (for small swings) how far it swings – the period only depends on its length and on g. Galileo is said to have noticed this watching a swinging chandelier in Pisa cathedral. For centuries the world's most accurate clocks were based on it.",
    )
    val oscillator get() = l(
        "Eine Masse an einer Feder schwingt harmonisch: Je weiter sie ausgelenkt ist, desto stärker zieht die Feder zurück. Schwerere Massen schwingen langsamer, härtere Federn schneller. Energie pendelt dabei zwischen Federenergie (an den Umkehrpunkten) und Bewegungsenergie (in der Mitte, dort ist die Masse am schnellsten).",
        "A mass on a spring oscillates harmonically: the farther it is displaced, the harder the spring pulls back. Heavier masses oscillate more slowly, stiffer springs faster. Energy swaps back and forth between spring energy (at the turning points) and kinetic energy (in the middle, where the mass is fastest).",
    )
    val resonance get() = l(
        "Jedes schwingfähige Ding hat eine Eigenfrequenz. Stößt man es genau in diesem Takt an, schaukelt sich die Schwingung auf – Resonanz. So schaukelt man ein Kind auf der Schaukel hoch, so zerspringt ein Weinglas beim richtigen Ton, und deshalb dürfen Soldaten nicht im Gleichschritt über Brücken marschieren.",
        "Everything that can oscillate has a natural frequency. Push it exactly in that rhythm and the oscillation builds up – resonance. That's how you swing a child higher, how a wine glass shatters at the right note, and why soldiers break step when marching over bridges.",
    )
}

private object Fc2 {
    val torque get() = card(l("Drehmoment", "Torque"), """§M = F \cdot r§""", l("Kraft mal Hebelarm (senkrecht zur Kraft).", "Force times lever arm (perpendicular to the force)."))
    val centripetal get() = card(l("Zentripetalkraft", "Centripetal force"), """§F_Z = \dfrac{m v^2}{r}§""", l("Kraft, die zur Kreismitte zeigt.", "Force pointing to the centre."))
    val omega get() = card(l("Winkelgeschwindigkeit", "Angular velocity"), """§v = \omega r,\quad \omega = 2\pi f§""", l("f = Umdrehungen pro Sekunde.", "f = revolutions per second."))
    val angular get() = card(l("Drehimpulserhaltung", "Conservation of angular momentum"), """§I_1 \omega_1 = I_2 \omega_2§""", l("Ohne äußeres Drehmoment.", "Without external torque."))
    val newtonG get() = card(l("Gravitationsgesetz", "Law of gravitation"), """§F = G \dfrac{m_1 m_2}{r^2}§""", l("G = 6,674 · 10⁻¹¹ N m²/kg².", "G = 6.674 · 10⁻¹¹ N m²/kg²."))
    val kepler get() = card(l("Kreisbahn", "Circular orbit"), """§T = 2\pi \sqrt{\dfrac{r^3}{G M}}§""", l("r vom Mittelpunkt der Erde aus gemessen.", "r measured from the centre of the Earth."))
    val escape get() = card(l("Fluchtgeschwindigkeit", "Escape velocity"), """§v_F = \sqrt{\dfrac{2 G M}{R}}§""", l("Bewegungsenergie = Bindungsenergie.", "Kinetic energy = binding energy."))
    val pendulum get() = card(l("Fadenpendel", "Simple pendulum"), """§T = 2\pi \sqrt{\dfrac{L}{g}}§""", l("Für kleine Ausschläge.", "For small swings."))
    val spring get() = card(l("Federschwinger", "Mass on a spring"), """§T = 2\pi \sqrt{\dfrac{m}{D}}§""", l("D = Federkonstante.", "D = spring constant."))
    val freq get() = card(l("Frequenz", "Frequency"), """§f = \dfrac{1}{T}§""", l("Schwingungen pro Sekunde, 1 Hz = 1/s.", "Oscillations per second, 1 Hz = 1/s."))
}

/** Himmelskörper für Vorlagen. */
private class Planet(val name: String, val day: Double, val mass: Double, val radius: Double)

object LabMechanics2 {
    val all: List<TaskDef> get() = rotation + gravity + oscillations

    // ===================================== DREHUNG =====================================
    private val rotation = listOf(
        lab("schluessel", Topic.Rotation, E, 0.2f) {
            val need = tiered(listOf(60.0, 80.0, 100.0), 40.0, 160.0, 5.0)
            val r = tiered(listOf(0.2, 0.25, 0.4), 0.15, 0.5, 0.01)
            val f = need / r
            task(
                question = l("Eine Radmutter braucht ${n(need, 0)} Nm Drehmoment. Dein Schlüssel ist ${n(r * 100, 0)} cm lang (bis zur Hand). Mit welcher Kraft musst du ziehen?", "A wheel nut needs ${n(need, 0)} Nm of torque. Your wrench is ${n(r * 100, 0)} cm long (to your hand). What force do you have to pull with?"),
                steps = listOf(num(l("Nötige Kraft", "Force needed"), f, "N", rel = 0.02, done = """§F \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Drehmoment = Kraft mal Hebelarm.", "Key idea: torque = force times lever arm."), """§F = \dfrac{M}{r} = \dfrac{${m(need, 0)}\ \text{Nm}}{${m(r)}\ \text{m}}§"""), formula = Fc2.torque)),
                solution = listOf("""§F = \dfrac{${m(need, 0)}\ \text{Nm}}{${m(r)}\ \text{m}} \approx ${m(sig(f))}\ \text{N}§"""),
                background = Bg2.torque,
                fact = l("Das entspricht dem Gewicht von etwa ${n(sig(f / G, 2), 0)} kg – mit doppelt so langem Rohr auf dem Schlüssel halbiert sich die Kraft.", "That's like the weight of about ${n(sig(f / G, 2), 0)} kg – put a pipe twice as long on the wrench and the force halves."),
            )
        },
        lab("kurve", Topic.Rotation, M, 0.5f) {
            val r = tiered(listOf(50.0, 80.0, 100.0), 30.0, 250.0, 10.0)
            val mu = tiered(listOf(0.8, 0.5), 0.2, 0.9, 0.05)
            val v = sqrt(mu * G * r)
            task(
                question = l("Wie schnell darf ein Auto höchstens durch eine flache Kurve mit ${n(r, 0)} m Radius fahren, wenn die Haftreibungszahl der Reifen ${n(mu)} ist?", "What is the top speed a car can take a flat bend of radius ${n(r, 0)} m if the tyres' static friction coefficient is ${n(mu)}?"),
                steps = listOf(num(l("Höchstgeschwindigkeit", "Top speed"), v * 3.6, "km/h", rel = 0.03, done = """§v_{max} \approx ${m(sig(v * 3.6))}\ \text{km/h}§""",
                    hints = listOf(l("Kernidee: Die Reibung muss die ganze Zentripetalkraft liefern – die Masse kürzt sich weg.", "Key idea: friction has to supply the whole centripetal force – the mass cancels."), """§\mu m g = \dfrac{m v^2}{r} \Rightarrow v = \sqrt{\mu g r}§"""), formula = Fc2.centripetal)),
                solution = listOf("""§v = \sqrt{${m(mu)} \cdot 9{,}81 \cdot ${m(r, 0)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v))}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v * 3.6))}\ \tfrac{\text{km}}{\text{h}}§"""),
                background = Bg2.circle,
                fact = l("Auf nasser Straße sinkt μ auf etwa 0,5, auf Eis auf 0,1 – die sichere Kurvengeschwindigkeit fällt mit der Wurzel daraus.", "On a wet road μ drops to about 0.5, on ice to 0.1 – the safe cornering speed drops with the square root of that."),
            )
        },
        lab("fahrradrad", Topic.Rotation, M, 0.3f) {
            val d = pick(0.6, 0.66, 0.7)
            val rpm = tiered(listOf(120.0, 150.0, 200.0), 90.0, 260.0, 5.0)
            val v = PI * d * rpm / 60 * 3.6
            task(
                question = l("Ein Fahrradreifen hat ${n(d * 100, 0)} cm Durchmesser und dreht sich ${n(rpm, 0)}-mal pro Minute. Wie schnell fährt das Rad?", "A bike wheel has a diameter of ${n(d * 100, 0)} cm and turns ${n(rpm, 0)} times per minute. How fast is the bike going?"),
                steps = listOf(num(l("Geschwindigkeit", "Speed"), v, "km/h", rel = 0.03, done = """§v \approx ${m(sig(v))}\ \text{km/h}§""",
                    hints = listOf(l("Kernidee: Bei jeder Umdrehung rollt das Rad einmal seinen Umfang ab.", "Key idea: with every turn the wheel rolls one circumference forward."), """§v = \pi d \cdot f,\ f = ${m(rpm, 0)}/60\ \text{s}^{-1}§"""), formula = Fc2.omega)),
                solution = listOf("""§v = \pi \cdot ${m(d)}\ \text{m} \cdot \dfrac{${m(rpm, 0)}}{60\ \text{s}} \approx ${m(sig(v / 3.6))}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v))}\ \tfrac{\text{km}}{\text{h}}§"""),
                background = Bg2.circle,
            )
        },
        lab("karussell", Topic.Rotation, H, 0.5f) {
            val r = tiered(listOf(5.0, 8.0, 10.0), 4.0, 15.0, 0.5)
            val t = tiered(listOf(4.0, 5.0, 6.0), 3.0, 8.0, 0.1)
            val v = 2 * PI * r / t
            val a = v * v / r
            task(
                question = l("Ein Kettenkarussell: Die Sitze kreisen mit ${n(r)} m Radius, eine Runde dauert ${n(t)} s.", "A swing carousel: the seats circle with a radius of ${n(r)} m, one turn takes ${n(t)} s."),
                steps = listOf(
                    num(l("Geschwindigkeit der Sitze", "Speed of the seats"), v, "m/s", rel = 0.02, done = """§v \approx ${m(sig(v))}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: In einer Runde legt ein Sitz einen Kreisumfang zurück.", "Key idea: in one turn a seat travels one circumference."), """§v = \dfrac{2\pi r}{T}§"""), formula = Fc2.omega),
                    num(l("Zentripetalbeschleunigung in Vielfachen von g", "Centripetal acceleration in multiples of g"), a / G, "g", rel = 0.03, done = """§a \approx ${m(sig(a / G, 2))}\, g§""",
                        hints = listOf(l("Erst a ausrechnen, dann durch 9,81 teilen.", "First work out a, then divide by 9.81."), """§a = \dfrac{v^2}{r}§"""), formula = Fc2.centripetal),
                ),
                solution = listOf(
                    """§v = \dfrac{2\pi \cdot ${m(r)}\ \text{m}}{${m(t)}\ \text{s}} \approx ${m(sig(v))}\ \tfrac{\text{m}}{\text{s}}§""",
                    """§a = \dfrac{(${m(sig(v))})^2}{${m(r)}}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(a))}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(a / G, 2))}\, g§""",
                ),
                background = Bg2.circle,
            )
        },
        lab("pirouette", Topic.Rotation, M, 0.5f) {
            val f1 = tiered(listOf(1.0, 1.5), 0.8, 2.0, 0.1)
            val i1 = tiered(listOf(3.0, 4.0), 2.5, 5.0, 0.1)
            val i2 = tiered(listOf(1.0, 1.2), 0.8, 1.5, 0.1)
            val f2 = f1 * i1 / i2
            task(
                question = l("Eine Eiskunstläuferin dreht sich mit ausgestreckten Armen ${n(f1)}-mal pro Sekunde (Trägheitsmoment ${n(i1)} kg m²). Dann zieht sie die Arme an (${n(i2)} kg m²).", "A figure skater spins with arms outstretched at ${n(f1)} turns per second (moment of inertia ${n(i1)} kg m²). Then she pulls her arms in (${n(i2)} kg m²)."),
                steps = listOf(num(l("Umdrehungen pro Sekunde danach", "Turns per second afterwards"), f2, "1/s", rel = 0.03, done = """§f_2 \approx ${m(sig(f2))}\ \text{s}^{-1}§""",
                    hints = listOf(l("Kernidee: Der Drehimpuls bleibt erhalten – kleineres Trägheitsmoment, schnellere Drehung.", "Key idea: angular momentum is conserved – smaller moment of inertia, faster spin."), """§I_1 f_1 = I_2 f_2§"""), formula = Fc2.angular)),
                solution = listOf("""§f_2 = f_1 \dfrac{I_1}{I_2} = ${m(f1)} \cdot \dfrac{${m(i1)}}{${m(i2)}}\ \text{s}^{-1} \approx ${m(sig(f2))}\ \text{s}^{-1}§"""),
                background = Bg2.angular,
                fact = l("Die Bewegungsenergie steigt dabei – die Arbeit kommt aus ihren Muskeln, die die Arme gegen die 'Fliehkraft' heranziehen.", "Her kinetic energy goes up in the process – the work comes from her muscles pulling her arms in against the 'centrifugal force'."),
            )
        },
        lab("rollrennen", Topic.Rotation, X, 0.7f) {
            val h = tiered(listOf(1.0, 2.0), 0.5, 3.0, 0.1)
            val (name, k) = pick(l("eine Vollkugel", "a solid ball") to 0.4, l("ein Vollzylinder", "a solid cylinder") to 0.5, l("ein dünner Ring", "a thin ring") to 1.0)
            val v = sqrt(2 * G * h / (1 + k))
            val vSlide = sqrt(2 * G * h)
            task(
                question = l("${name.replaceFirstChar { it.uppercase() }} rollt ohne zu rutschen eine Rampe hinunter, die ${n(h)} m Höhenunterschied hat (Start aus der Ruhe).", "${name.replaceFirstChar { it.uppercase() }} rolls without slipping down a ramp with a height difference of ${n(h)} m (starting from rest)."),
                given = listOf(l("""Trägheitsmoment: §I = ${m(k)}\, m r^2§""", """Moment of inertia: §I = ${m(k)}\, m r^2§""")),
                steps = listOf(
                    num(l("Geschwindigkeit unten", "Speed at the bottom"), v, "m/s", rel = 0.03, done = """§v \approx ${m(sig(v))}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Die Lageenergie verteilt sich auf Fortbewegung und Drehung; beim Rollen ist ω = v/r.", "Key idea: the potential energy is shared between moving and spinning; when rolling, ω = v/r."), """§m g h = \tfrac{1}{2} m v^2 + \tfrac{1}{2} I \omega^2 = \tfrac{1}{2} m v^2 (1 + ${m(k)})§"""),
                        formula = card(l("Rollen", "Rolling"), """§v = \sqrt{\dfrac{2 g h}{1 + I/(m r^2)}}§""", l("Masse und Radius kürzen sich weg.", "Mass and radius cancel out."))),
                    num(l("Zum Vergleich: ein reibungsfrei gleitender Klotz", "For comparison: a block sliding without friction"), vSlide, "m/s", rel = 0.03, done = """§v \approx ${m(sig(vSlide))}\ \text{m/s}§""",
                        hints = listOf(l("Ohne Drehung geht alle Energie in die Fortbewegung.", "Without spinning, all the energy goes into moving forward."), """§v = \sqrt{2 g h}§""")),
                ),
                solution = listOf(
                    """§v = \sqrt{\dfrac{2 \cdot 9{,}81 \cdot ${m(h)}}{1 + ${m(k)}}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v))}\ \tfrac{\text{m}}{\text{s}}§""",
                    l("""§v_{gleit} = \sqrt{2 \cdot 9{,}81 \cdot ${m(h)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vSlide))}\ \tfrac{\text{m}}{\text{s}}§""", """§v_{slide} = \sqrt{2 \cdot 9{,}81 \cdot ${m(h)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vSlide))}\ \tfrac{\text{m}}{\text{s}}§"""),
                ),
                background = Bg2.rolling,
            )
        },
    )

    // ===================================== GRAVITATION =====================================
    private val gravity = listOf(
        lab("planetsprung", Topic.Gravity, E, 0.3f) {
            val jump = tiered(listOf(0.4, 0.5), 0.3, 0.7, 0.05)
            val (place, gx) = pick(l("auf dem Mond", "on the Moon") to 1.62, l("auf dem Mars", "on Mars") to 3.71, l("auf Titan", "on Titan") to 1.35)
            val hNew = jump * G / gx
            task(
                question = l("Auf der Erde springst du ${n(jump * 100, 0)} cm hoch. Wie hoch kämst du mit demselben Absprung $place?", "On Earth you can jump ${n(jump * 100, 0)} cm high. How high would the same take-off get you $place?"),
                given = listOf("""§g = ${m(gx)}\ \text{m/s}^2§"""),
                steps = listOf(num(l("Sprunghöhe", "Jump height"), hNew * 100, "cm", rel = 0.03, done = """§h \approx ${m(sig(hNew * 100))}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Gleiche Absprunggeschwindigkeit, also gleiche Bewegungsenergie – die Höhe wächst, wenn g kleiner ist.", "Key idea: same take-off speed, so the same kinetic energy – the height grows when g is smaller."), l("""§h' = h \cdot \dfrac{g_{Erde}}{g'}§""", """§h' = h \cdot \dfrac{g_{Earth}}{g'}§""")), formula = card(l("Lageenergie", "Potential energy"), """§E_{pot} = m g h§""", l("Gleiche Energie, kleineres g: größere Höhe.", "Same energy, smaller g: greater height.")))),
                solution = listOf("""§h' = ${m(jump * 100, 0)}\ \text{cm} \cdot \dfrac{9{,}81}{${m(gx)}} \approx ${m(sig(hNew * 100))}\ \text{cm}§"""),
                background = Bg2.gravity,
            )
        },
        lab("anziehung", Topic.Gravity, M, 0.3f) {
            val m1 = tiered(listOf(60.0, 70.0), 45.0, 95.0, 1.0)
            val m2 = tiered(listOf(60.0, 80.0), 45.0, 95.0, 1.0)
            val r = tiered(listOf(1.0, 0.5), 0.3, 2.0, 0.1)
            val f = GRAV * m1 * m2 / (r * r)
            task(
                question = l("Zwei Menschen (${n(m1, 0)} kg und ${n(m2, 0)} kg) stehen ${n(r)} m auseinander. Wie stark ziehen sie sich durch die Gravitation an?", "Two people (${n(m1, 0)} kg and ${n(m2, 0)} kg) stand ${n(r)} m apart. How strongly do they attract each other by gravity?"),
                steps = listOf(num(l("Kraft in Mikronewton (µN)", "Force in micronewtons (µN)"), f * 1e6, "µN", rel = 0.03, done = """§F \approx ${m(sig(f * 1e6))}\ \mu\text{N}§""",
                    hints = listOf(l("Kernidee: Newtons Gravitationsgesetz – die Konstante G ist winzig.", "Key idea: Newton's law of gravitation – the constant G is tiny."), """§F = G \dfrac{m_1 m_2}{r^2}§"""), formula = Fc2.newtonG)),
                solution = listOf("""§F = 6{,}674 \cdot 10^{-11} \cdot \dfrac{${m(m1, 0)} \cdot ${m(m2, 0)}}{(${m(r)})^2}\ \text{N} \approx ${sci(f)}\ \text{N}§"""),
                background = Bg2.gravity,
                fact = l("Das ist etwa das Gewicht eines Sandkorns – deshalb merkt man Gravitation erst zwischen Planeten und Menschen.", "That's about the weight of a grain of sand – which is why gravity only becomes noticeable between planets and people."),
            )
        },
        lab("gimhoehe", Topic.Gravity, M, 0.5f) {
            val hKm = tiered(listOf(400.0, 1000.0, 6371.0), 300.0, 20000.0, 100.0)
            val g = G * (R_EARTH / (R_EARTH + hKm * 1000)).pow(2)
            task(
                question = l("Wie groß ist die Fallbeschleunigung in ${n(hKm, 0)} km Höhe über der Erde? (Erdradius 6371 km, am Boden 9,81 m/s²)", "What is the gravitational acceleration at ${n(hKm, 0)} km above the Earth? (Earth radius 6371 km, 9.81 m/s² at the ground)"),
                steps = listOf(num(l("Fallbeschleunigung", "Gravitational acceleration"), g, "m/s²", rel = 0.03, done = """§g \approx ${m(sig(g))}\ \text{m/s}^2§""",
                    hints = listOf(l("Kernidee: g nimmt mit dem Quadrat des Abstands vom Erdmittelpunkt ab.", "Key idea: g falls with the square of the distance from the centre of the Earth."), """§g(h) = g_0 \left(\dfrac{R}{R + h}\right)^2§"""), formula = Fc2.newtonG)),
                solution = listOf("""§g = 9{,}81 \cdot \left(\dfrac{6371}{${m(6371 + hKm, 0)}}\right)^2\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(g))}\ \tfrac{\text{m}}{\text{s}^2}§"""),
                background = Bg2.gravity,
                fact = l("In der Höhe der Raumstation (400 km) gibt es noch fast 90 % der Erdschwerkraft – schwerelos ist man dort nur, weil man mitfällt.", "At the space station's altitude (400 km) there is still almost 90% of Earth's gravity – you're only weightless there because you fall along with it."),
            )
        },
        lab("umlaufzeit", Topic.Gravity, H, 0.5f) {
            val hKm = tiered(listOf(400.0, 800.0, 20200.0), 300.0, 30000.0, 100.0)
            val r = R_EARTH + hKm * 1000
            val t = 2 * PI * sqrt(r * r * r / (GRAV * M_EARTH))
            task(
                question = l("Ein Satellit kreist in ${n(hKm, 0)} km Höhe um die Erde. Wie lange dauert ein Umlauf?", "A satellite orbits the Earth at an altitude of ${n(hKm, 0)} km. How long does one orbit take?"),
                given = listOf(l("""§M_E = 5{,}97 \cdot 10^{24}\ \text{kg},\ R_E = 6371\ \text{km}§""", """§M_E = 5{,}97 \cdot 10^{24}\ \text{kg},\ R_E = 6371\ \text{km}§""")),
                steps = listOf(num(l("Umlaufzeit in Minuten", "Orbital period in minutes"), t / 60, "min", rel = 0.03, done = """§T \approx ${m(sig(t / 60))}\ \text{min}§""",
                    hints = listOf(l("Kernidee: Die Gravitation liefert die Zentripetalkraft. Abstand vom Erdmittelpunkt nehmen!", "Key idea: gravity supplies the centripetal force. Use the distance from the centre of the Earth!"), """§r = R_E + h = ${m((r / 1000), 0)}\ \text{km}§"""), formula = Fc2.kepler)),
                solution = listOf("""§T = 2\pi \sqrt{\dfrac{(${sci(r)}\ \text{m})^3}{6{,}674 \cdot 10^{-11} \cdot 5{,}97 \cdot 10^{24}}} \approx ${m(sig(t))}\ \text{s} \approx ${m(sig(t / 60))}\ \text{min}§"""),
                background = Bg2.orbit,
            )
        },
        lab("geostationaer", Topic.Gravity, X, 0.6f) {
            val b = pick(
                Planet(l("die Erde", "Earth"), 86164.0, M_EARTH, R_EARTH),
                Planet(l("den Mars", "Mars"), 88643.0, 6.42e23, 3.39e6),
            )
            val rr = (GRAV * b.mass * b.day.pow(2) / (4 * PI * PI)).pow(1.0 / 3)
            val alt = (rr - b.radius) / 1000
            task(
                question = l("In welcher Höhe muss ein Satellit um ${b.name} kreisen, damit er immer über demselben Punkt steht? (Ein Sterntag dauert ${n(b.day / 3600, 2)} h.)", "At what altitude must a satellite orbit ${b.name} so that it always stays above the same spot? (One sidereal day lasts ${n(b.day / 3600, 2)} h.)"),
                given = listOf("""§M = ${sci(b.mass)}\ \text{kg},\ R = ${m(b.radius / 1000, 0)}\ \text{km}§"""),
                steps = listOf(num(l("Höhe über der Oberfläche", "Altitude above the surface"), alt, "km", rel = 0.02, done = """§h \approx ${m(sig(alt))}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Die Umlaufzeit muss genau ein Sterntag sein – Keplers Gesetz nach r umstellen.", "Key idea: the orbital period has to be exactly one sidereal day – rearrange Kepler's law for r."), """§r = \left(\dfrac{G M T^2}{4\pi^2}\right)^{1/3},\ h = r - R§"""), formula = Fc2.kepler)),
                solution = listOf("""§r = \left(\dfrac{6{,}674 \cdot 10^{-11} \cdot ${sci(b.mass)} \cdot (${m(b.day, 0)})^2}{4\pi^2}\right)^{1/3}\ \text{m} \approx ${m(sig(rr / 1000))}\ \text{km}§""", """§h = r - R \approx ${m(sig(alt))}\ \text{km}§"""),
                background = Bg2.orbit,
                fact = l("Arthur C. Clarke schlug 1945 vor, mit drei solchen Satelliten die ganze Erde per Funk zu verbinden – 20 Jahre bevor es den ersten gab.", "Arthur C. Clarke proposed in 1945 to link the whole Earth by radio with three such satellites – 20 years before the first one existed."),
            )
        },
        lab("asteroidsprung", Topic.Gravity, X, 0.5f) {
            val rKm = tiered(listOf(1.0, 2.0, 5.0), 0.5, 20.0, 0.5)
            val rho = tiered(listOf(2000.0, 2500.0), 1500.0, 3500.0, 100.0)
            val mass = rho * 4.0 / 3 * PI * (rKm * 1000).pow(3)
            val v = sqrt(2 * GRAV * mass / (rKm * 1000))
            task(
                question = l("Ein kugelförmiger Asteroid hat ${n(rKm)} km Radius und eine Dichte von ${n(rho, 0)} kg/m³. Kannst du dich mit einem Sprung (etwa 3 m/s) für immer von ihm entfernen?", "A spherical asteroid has a radius of ${n(rKm)} km and a density of ${n(rho, 0)} kg/m³. Could you leave it forever with a jump (about 3 m/s)?"),
                steps = listOf(num(l("Fluchtgeschwindigkeit", "Escape velocity"), v, "m/s", rel = 0.03, done = """§v_F \approx ${m(sig(v))}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Erst die Masse aus Dichte und Kugelvolumen, dann die Fluchtgeschwindigkeit.", "Key idea: first the mass from density and sphere volume, then the escape velocity."), """§M = \rho \cdot \tfrac{4}{3}\pi R^3§"""), formula = Fc2.escape)),
                solution = listOf("""§M = ${m(rho, 0)} \cdot \tfrac{4}{3}\pi (${sci(rKm * 1000)})^3\ \text{kg} \approx ${sci(mass)}\ \text{kg}§""", """§v_F = \sqrt{\dfrac{2 \cdot 6{,}674 \cdot 10^{-11} \cdot ${sci(mass)}}{${sci(rKm * 1000)}}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v))}\ \tfrac{\text{m}}{\text{s}}§""",
                    if (v < 3.0) l("Also ja – ein kräftiger Sprung reicht.", "So yes – a good jump is enough.") else l("Also nein – dafür ist der Asteroid zu groß.", "So no – the asteroid is too big for that.")),
                background = Bg2.escape,
            )
        },
        lab("spaghetti", Topic.Gravity, X, 0.8f, master = true) {
            val msun = 1.989e30
            val mb = tiered(listOf(10.0, 20.0), 5.0, 40.0, 1.0)
            val rKm = tiered(listOf(1000.0, 3000.0), 500.0, 5000.0, 100.0)
            val lBody = 2.0
            val da = 2 * GRAV * mb * msun * lBody / (rKm * 1000).pow(3)
            val rs = 2 * GRAV * mb * msun / (2.998e8 * 2.998e8) / 1000
            task(
                question = l("Eine Astronautin (2 m groß) treibt mit den Füßen voran ${n(rKm, 0)} km vom Zentrum eines Schwarzen Lochs mit ${n(mb, 0)} Sonnenmassen entfernt.", "An astronaut (2 m tall) drifts feet-first ${n(rKm, 0)} km from the centre of a black hole of ${n(mb, 0)} solar masses."),
                given = listOf(l("""Sonnenmasse: §M_S = 1{,}99 \cdot 10^{30}\ \text{kg}§""", """Solar mass: §M_S = 1{,}99 \cdot 10^{30}\ \text{kg}§""")),
                steps = listOf(
                    num(l("Unterschied der Beschleunigung zwischen Füßen und Kopf (in g)", "Difference in acceleration between feet and head (in g)"), da / G, "g", rel = 0.04, done = """§\Delta a \approx ${m(sig(da / G, 2))}\, g§""",
                        hints = listOf(l("Kernidee: Gezeitenbeschleunigung – Ableitung von GM/r² nach r: Δa ≈ 2GM·L/r³.", "Key idea: tidal acceleration – the derivative of GM/r² with respect to r: Δa ≈ 2GM·L/r³."), """§\Delta a \approx \dfrac{2 G M L}{r^3}§"""),
                        formula = card(l("Gezeitenbeschleunigung", "Tidal acceleration"), """§\Delta a \approx \dfrac{2 G M\, L}{r^3}§""", l("L = Länge des Körpers in Richtung zum Zentrum.", "L = length of the body along the direction to the centre."))),
                    num(l("Schwarzschild-Radius des Lochs", "Schwarzschild radius of the hole"), rs, "km", rel = 0.03, done = """§r_S \approx ${m(sig(rs))}\ \text{km}§""",
                        hints = listOf(l("Ab hier kommt nicht einmal Licht heraus.", "From here not even light gets out."), """§r_S = \dfrac{2 G M}{c^2}§""")),
                ),
                solution = listOf(
                    """§\Delta a = \dfrac{2 \cdot 6{,}674 \cdot 10^{-11} \cdot ${sci(mb * msun)} \cdot 2}{(${sci(rKm * 1000)})^3}\ \tfrac{\text{m}}{\text{s}^2} \approx ${sci(da)}\ \tfrac{\text{m}}{\text{s}^2}§""",
                    """§r_S = \dfrac{2 \cdot 6{,}674 \cdot 10^{-11} \cdot ${sci(mb * msun)}}{(2{,}998 \cdot 10^8)^2}\ \text{m} \approx ${m(sig(rs))}\ \text{km}§""",
                ),
                background = Bg2.tides,
                fact = l("Bei einem supermassereichen Schwarzen Loch wie im Zentrum der Milchstraße wären die Gezeitenkräfte am Ereignishorizont harmlos – man würde ihn überqueren, ohne es zu merken.", "At a supermassive black hole like the one at the centre of the Milky Way, tidal forces at the event horizon would be harmless – you would cross it without noticing."),
            )
        },
    )

    // ===================================== SCHWINGUNGEN =====================================
    private val oscillations = listOf(
        lab("herzschlag", Topic.Oscillations, E, 0.2f) {
            val bpm = tiered(listOf(60.0, 72.0, 90.0), 50.0, 150.0, 1.0)
            val f = bpm / 60
            val t = 1 / f
            task(
                question = l("Dein Herz schlägt ${n(bpm, 0)}-mal pro Minute. Welche Frequenz ist das, und wie lange dauert ein Schlag?", "Your heart beats ${n(bpm, 0)} times a minute. What frequency is that, and how long does one beat last?"),
                steps = listOf(
                    num(l("Frequenz", "Frequency"), f, "Hz", rel = 0.02, done = """§f \approx ${m(sig(f))}\ \text{Hz}§""",
                        hints = listOf(l("Kernidee: Hertz heißt 'pro Sekunde'.", "Key idea: hertz means 'per second'."), """§f = \dfrac{${m(bpm, 0)}}{60\ \text{s}}§"""), formula = Fc2.freq),
                    num(l("Dauer eines Schlags", "Duration of one beat"), t, "s", rel = 0.02, done = """§T \approx ${m(sig(t))}\ \text{s}§""",
                        hints = listOf(l("Periodendauer und Frequenz sind Kehrwerte.", "Period and frequency are reciprocals."), """§T = 1/f§""")),
                ),
                solution = listOf("""§f = \dfrac{${m(bpm, 0)}}{60\ \text{s}} \approx ${m(sig(f))}\ \text{Hz}§""", """§T = \dfrac{1}{f} \approx ${m(sig(t))}\ \text{s}§"""),
                background = l(
                    "Alles, was sich regelmäßig wiederholt, hat eine Periodendauer T (wie lange eine Wiederholung dauert) und eine Frequenz f (wie viele Wiederholungen pro Sekunde). Beide sind Kehrwerte. Ein Kolibri schlägt etwa 50-mal pro Sekunde mit den Flügeln, eine Gitarrensaite schwingt hunderte Male, das WLAN-Signal Milliarden Male pro Sekunde.",
                    "Anything that repeats regularly has a period T (how long one repetition takes) and a frequency f (how many repetitions per second). The two are reciprocals. A hummingbird beats its wings about 50 times a second, a guitar string vibrates hundreds of times, a Wi-Fi signal billions of times per second.",
                ),
            )
        },
        lab("pendeluhr", Topic.Oscillations, M, 0.4f) {
            val l0 = tiered(listOf(1.0, 0.25, 0.5), 0.2, 2.5, 0.05)
            val t = 2 * PI * sqrt(l0 / G)
            task(
                question = l("Ein Pendel hat ${n(l0 * 100, 0)} cm Fadenlänge. Wie lange dauert eine volle Schwingung (hin und zurück)?", "A pendulum has a string length of ${n(l0 * 100, 0)} cm. How long does one full swing (there and back) take?"),
                steps = listOf(num(l("Schwingungsdauer", "Period"), t, "s", rel = 0.02, done = """§T \approx ${m(sig(t))}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Nur Länge und g zählen, nicht die Masse.", "Key idea: only the length and g matter, not the mass."), """§T = 2\pi\sqrt{L/g}§"""), formula = Fc2.pendulum)),
                solution = listOf("""§T = 2\pi \sqrt{\dfrac{${m(l0)}\ \text{m}}{9{,}81\ \text{m/s}^2}} \approx ${m(sig(t))}\ \text{s}§"""),
                background = Bg2.pendulum,
                fact = l("Ein Pendel mit genau 99,4 cm Länge braucht für jede halbe Schwingung eine Sekunde – das 'Sekundenpendel' großer Standuhren.", "A pendulum exactly 99.4 cm long takes one second for each half swing – the 'seconds pendulum' of grandfather clocks."),
            )
        },
        lab("federschwinger", Topic.Oscillations, M, 0.4f) {
            val mass = tiered(listOf(0.5, 1.0, 2.0), 0.2, 3.0, 0.1)
            val d = tiered(listOf(50.0, 100.0, 200.0), 20.0, 400.0, 10.0)
            val t = 2 * PI * sqrt(mass / d)
            val f = 1 / t
            task(
                question = l("An einer Feder (D = ${n(d, 0)} N/m) hängt eine Masse von ${n(mass)} kg und schwingt auf und ab.", "A mass of ${n(mass)} kg hangs on a spring (D = ${n(d, 0)} N/m) and bobs up and down."),
                steps = listOf(
                    num(l("Schwingungsdauer", "Period"), t, "s", rel = 0.02, done = """§T \approx ${m(sig(t))}\ \text{s}§""",
                        hints = listOf(l("Kernidee: Mehr Masse macht träger, eine härtere Feder schneller.", "Key idea: more mass makes it more sluggish, a stiffer spring faster."), """§T = 2\pi\sqrt{m/D}§"""), formula = Fc2.spring),
                    num(l("Frequenz", "Frequency"), f, "Hz", rel = 0.02, done = """§f \approx ${m(sig(f))}\ \text{Hz}§""",
                        hints = listOf(l("Kehrwert der Schwingungsdauer.", "The reciprocal of the period."), """§f = 1/T§"""), formula = Fc2.freq),
                ),
                solution = listOf("""§T = 2\pi \sqrt{\dfrac{${m(mass)}\ \text{kg}}{${m(d, 0)}\ \text{N/m}}} \approx ${m(sig(t))}\ \text{s}§""", """§f = 1/T \approx ${m(sig(f))}\ \text{Hz}§"""),
                background = Bg2.oscillator,
            )
        },
        lab("mondpendel", Topic.Oscillations, H, 0.5f) {
            val t0 = tiered(listOf(1.0, 2.0), 0.8, 3.0, 0.1)
            val (place, gx) = pick(l("auf dem Mond", "on the Moon") to 1.62, l("auf dem Mars", "on Mars") to 3.71)
            val t1 = t0 * sqrt(G / gx)
            val lNeed = gx * (t0 / (2 * PI)).pow(2)
            task(
                question = l("Eine Pendeluhr schwingt auf der Erde mit ${n(t0)} s Periodendauer. Man bringt sie $place (g = ${n(gx)} m/s²).", "A pendulum clock swings with a period of ${n(t0)} s on Earth. It is taken $place (g = ${n(gx)} m/s²)."),
                steps = listOf(
                    num(l("Periodendauer dort", "Period there"), t1, "s", rel = 0.02, done = """§T' \approx ${m(sig(t1))}\ \text{s}§""",
                        hints = listOf(l("Kernidee: T wächst mit 1/√g.", "Key idea: T grows like 1/√g."), l("""§T' = T \sqrt{g_{Erde}/g'}§""", """§T' = T \sqrt{g_{Earth}/g'}§""")), formula = Fc2.pendulum),
                    num(l("Wie lang müsste das Pendel dort sein, damit die Uhr wieder richtig geht?", "How long would the pendulum have to be there for the clock to keep time again?"), lNeed * 100, "cm", rel = 0.03, done = """§L' \approx ${m(sig(lNeed * 100))}\ \text{cm}§""",
                        hints = listOf(l("Gleiche Periodendauer wie auf der Erde – Formel nach L umstellen.", "Same period as on Earth – rearrange the formula for L."), """§L' = g' \left(\dfrac{T}{2\pi}\right)^2§""")),
                ),
                solution = listOf("""§T' = ${m(t0)}\ \text{s} \cdot \sqrt{9{,}81 / ${m(gx)}} \approx ${m(sig(t1))}\ \text{s}§""", """§L' = ${m(gx)} \cdot \left(\dfrac{${m(t0)}}{2\pi}\right)^2\ \text{m} \approx ${m(sig(lNeed * 100))}\ \text{cm}§"""),
                background = Bg2.pendulum,
            )
        },
        lab("resonanz", Topic.Oscillations, H, 0.3f) {
            val opts = pick(
                l("Ein Opernsänger lässt mit seiner Stimme ein Weinglas zerspringen. Was ist der Trick?", "An opera singer shatters a wine glass with his voice. What's the trick?") to listOf(
                    l("Er trifft genau die Eigenfrequenz des Glases und hält den Ton lange.", "He hits exactly the glass's natural frequency and holds the note for a long time."),
                    l("Er singt so hoch wie möglich.", "He sings as high as he can."),
                    l("Er singt so tief wie möglich, damit die Wellen länger sind.", "He sings as low as he can so the waves are longer."),
                    l("Der Luftstrom aus seinem Mund stößt das Glas um.", "The air from his mouth knocks the glass over."),
                ),
                l("Warum sollen Soldaten nicht im Gleichschritt über eine Brücke marschieren?", "Why shouldn't soldiers march in step across a bridge?") to listOf(
                    l("Trifft ihr Schritt die Eigenfrequenz der Brücke, schaukelt sich die Schwingung auf.", "If their step matches the bridge's natural frequency, the oscillation builds up."),
                    l("Ihr gemeinsames Gewicht ist im Gleichschritt größer.", "Their combined weight is larger when in step."),
                    l("Gleichschritt ist lauter und erschreckt Autofahrer.", "Marching in step is louder and startles drivers."),
                    l("Die Brücke wird durch die Reibung der Stiefel warm.", "The bridge heats up from the friction of the boots."),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Kleine Stöße im richtigen Takt addieren sich.", "Key idea: small pushes in the right rhythm add up."), l("Denk an eine Schaukel: Wann musst du anschubsen?", "Think of a swing: when do you have to push?")), formula = Fc2.freq)),
                solution = listOf(l("Jedes System hat Eigenfrequenzen. Regt man es genau mit einer davon an, wird die Energie jedes Stoßes gespeichert und die Schwingung wächst – Resonanz.", "Every system has natural frequencies. Drive it at exactly one of them and the energy of every push is stored, so the oscillation grows – resonance.")),
                background = Bg2.resonance,
            )
        },
        lab("schwingenergie", Topic.Oscillations, X, 0.6f) {
            val mass = tiered(listOf(0.2, 0.5), 0.1, 1.0, 0.05)
            val d = tiered(listOf(50.0, 80.0), 20.0, 200.0, 5.0)
            val amp = tiered(listOf(0.05, 0.1), 0.02, 0.15, 0.01)
            val w = sqrt(d / mass)
            val vmax = amp * w
            val amax = amp * w * w
            task(
                question = l("Eine Masse von ${n(mass * 1000, 0)} g schwingt an einer Feder (D = ${n(d, 0)} N/m) mit ${n(amp * 100, 0)} cm Amplitude.", "A mass of ${n(mass * 1000, 0)} g oscillates on a spring (D = ${n(d, 0)} N/m) with an amplitude of ${n(amp * 100, 0)} cm."),
                steps = listOf(
                    num(l("Höchste Geschwindigkeit", "Maximum speed"), vmax, "m/s", rel = 0.03, done = """§v_{max} \approx ${m(sig(vmax))}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: In der Mitte steckt die ganze Federenergie in Bewegungsenergie: ½DA² = ½mv².", "Key idea: in the middle all the spring energy has become kinetic energy: ½DA² = ½mv²."), """§v_{max} = A\,\omega,\ \omega = \sqrt{D/m}§"""), formula = Fc2.spring),
                    num(l("Größte Beschleunigung (am Umkehrpunkt)", "Maximum acceleration (at a turning point)"), amax, "m/s²", rel = 0.03, done = """§a_{max} \approx ${m(sig(amax))}\ \text{m/s}^2§""",
                        hints = listOf(l("Dort ist die Federkraft am größten: F = D·A.", "That's where the spring force is largest: F = D·A."), """§a_{max} = \dfrac{D A}{m} = A\,\omega^2§""")),
                ),
                solution = listOf("""§\omega = \sqrt{${m(d, 0)} / ${m(mass)}}\ \text{s}^{-1} \approx ${m(sig(w))}\ \text{s}^{-1}§""", """§v_{max} = ${m(amp)} \cdot ${m(sig(w))}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vmax))}\ \tfrac{\text{m}}{\text{s}}§""", """§a_{max} = ${m(amp)} \cdot ${m(sig(w))}^2\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(amax))}\ \tfrac{\text{m}}{\text{s}^2}§"""),
                background = Bg2.oscillator,
            )
        },
    )
}
