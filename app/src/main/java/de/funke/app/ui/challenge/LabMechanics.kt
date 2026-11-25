package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

// =============================================================================================
// Vorlagen Mechanik I: Bewegung, Kräfte, Energie, Impuls (seit 10.1).
// Zahlen werden bei jedem Ziehen neu gewürfelt; Ergebnisse rechnet der Code aus.
// In Texten steht § für $ (Formel). Bei Auswahlfragen ist die ERSTE Option richtig.
// =============================================================================================

private const val G = 9.81
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

/** Hintergründe: erklären das Thema, nicht die Rechnung. */
private object Bg {
    val speed get() = l(
        "Geschwindigkeit sagt, wie viel Weg pro Zeit zurückgelegt wird. Die Durchschnittsgeschwindigkeit teilt den ganzen Weg durch die ganze Zeit – egal, ob man zwischendurch an der Ampel stand. Der Tacho zeigt dagegen die Momentangeschwindigkeit: wie schnell man genau jetzt ist. Umrechnen: 1 m/s sind 3,6 km/h, weil eine Stunde 3600 Sekunden und ein Kilometer 1000 Meter hat.",
        "Speed tells you how much distance is covered per unit of time. Average speed divides the whole distance by the whole time – no matter whether you waited at a traffic light. A speedometer shows instantaneous speed instead: how fast you are right now. Converting: 1 m/s is 3.6 km/h, because an hour has 3600 seconds and a kilometre 1000 metres.",
    )
    val braking get() = l(
        "Beim Bremsen wird Bewegungsenergie in Wärme umgewandelt. Weil diese Energie mit dem Quadrat der Geschwindigkeit wächst, wächst auch der Bremsweg quadratisch: doppelt so schnell heißt viermal so langer Bremsweg. Dazu kommt der Reaktionsweg – die Strecke, die man fährt, bevor der Fuß überhaupt auf der Bremse ist. Er wächst nur linear mit dem Tempo.",
        "Braking turns kinetic energy into heat. Because that energy grows with the square of the speed, so does the braking distance: twice as fast means four times the braking distance. On top comes the reaction distance – how far you travel before your foot even reaches the brake. It only grows linearly with speed.",
    )
    val projectile get() = l(
        "Ein Wurf ist zwei Bewegungen auf einmal: Waagrecht fliegt der Körper gleichmäßig weiter (ohne Luft bremst ihn nichts), senkrecht fällt er beschleunigt wie im freien Fall. Beide Bewegungen stören sich nicht. Deshalb landet eine waagrecht abgeschossene Kugel genau gleichzeitig mit einer, die man einfach fallen lässt.",
        "A throw is two motions at once: horizontally the object keeps moving steadily (without air nothing slows it down), vertically it falls with constant acceleration like in free fall. The two motions don't interfere. That's why a bullet fired horizontally lands at exactly the same time as one simply dropped.",
    )
    val drag get() = l(
        "Luftwiderstand wächst mit dem Quadrat der Geschwindigkeit. Ein fallender Körper wird deshalb so lange schneller, bis der Luftwiderstand genauso groß ist wie sein Gewicht – dann fällt er mit konstanter Endgeschwindigkeit. Wichtig sind die Fläche, die Form (cw-Wert) und die Dichte der Luft.",
        "Air drag grows with the square of the speed. A falling object therefore speeds up until the drag equals its weight – from then on it falls at a constant terminal velocity. What matters is the area, the shape (drag coefficient) and the density of the air.",
    )
    val newton get() = l(
        "Newtons Gesetze in einem Satz: Ohne Kraft ändert sich die Bewegung nicht, eine Kraft beschleunigt umso weniger, je schwerer etwas ist (F = m · a), und jede Kraft hat eine gleich große Gegenkraft. Gewicht ist die Kraft, mit der die Erde an einer Masse zieht – die Masse selbst ist überall im Universum gleich.",
        "Newton's laws in one breath: without a force, motion doesn't change; a force accelerates something less the heavier it is (F = m · a); and every force has an equal and opposite counterpart. Weight is the force with which a planet pulls on a mass – the mass itself is the same everywhere in the universe.",
    )
    val friction get() = l(
        "Reibung entsteht, weil sich die winzigen Unebenheiten zweier Oberflächen ineinander verhaken. Die Reibungskraft ist ungefähr proportional zur Kraft, mit der die Flächen aufeinander gedrückt werden (Normalkraft), und kaum von der Größe der Fläche abhängig. Haftreibung ist meist größer als Gleitreibung – deshalb ruckt ein Schrank, wenn er endlich losrutscht.",
        "Friction comes from tiny bumps on two surfaces catching on each other. The friction force is roughly proportional to the force pressing the surfaces together (normal force) and hardly depends on the contact area. Static friction is usually larger than sliding friction – that's why a cupboard jerks when it finally starts to slide.",
    )
    val incline get() = l(
        "Auf einer schiefen Ebene teilt sich das Gewicht in zwei Teile: einen, der den Körper hangabwärts zieht (m g sin α), und einen, der ihn gegen die Fläche drückt (m g cos α). Nur der zweite bestimmt die Reibung. Je steiler, desto mehr Hangabtrieb und desto weniger Reibung – irgendwann rutscht alles.",
        "On an incline the weight splits into two parts: one pulling the object down the slope (m g sin α) and one pressing it into the surface (m g cos α). Only the second sets the friction. The steeper the slope, the more pull down the slope and the less friction – at some point everything slides.",
    )
    val spring get() = l(
        "Eine Feder dehnt sich proportional zur Kraft – das Hookesche Gesetz. Die Federkonstante sagt, wie hart die Feder ist: Newton pro Meter Dehnung. So funktionieren Federwaagen, Stoßdämpfer und Trampoline. Überdehnt man eine Feder, verformt sie sich bleibend und das Gesetz gilt nicht mehr.",
        "A spring stretches in proportion to the force – Hooke's law. The spring constant tells you how stiff it is: newtons per metre of stretch. That's how spring scales, shock absorbers and trampolines work. Stretch a spring too far and it deforms permanently – then the law no longer holds.",
    )
    val apparent get() = l(
        "Eine Waage misst nicht deine Masse, sondern wie stark sie dich zurückdrücken muss. Beschleunigt ein Aufzug nach oben, muss der Boden dich zusätzlich beschleunigen – du wirkst schwerer. Fällt der Aufzug frei, drückt der Boden gar nicht mehr: Du bist schwerelos, genau wie Astronauten in der Raumstation, die ständig um die Erde 'fallen'.",
        "A scale doesn't measure your mass but how hard it has to push back on you. If a lift accelerates upwards, the floor has to accelerate you as well – you seem heavier. If the lift falls freely, the floor stops pushing altogether: you are weightless, just like astronauts on the space station, who are constantly 'falling' around the Earth.",
    )
    val energy get() = l(
        "Energie geht nie verloren, sie wechselt nur die Form: Lageenergie (m g h) wird beim Fallen zu Bewegungsenergie (½ m v²), beim Bremsen zu Wärme. Leistung sagt, wie schnell Energie umgesetzt wird: 1 Watt ist 1 Joule pro Sekunde. Eine Kilowattstunde sind 3,6 Millionen Joule – damit kann man etwa einen Eimer Wasser 300 Meter hoch heben.",
        "Energy is never lost, it only changes form: potential energy (m g h) turns into kinetic energy (½ m v²) when falling, into heat when braking. Power tells you how fast energy is converted: 1 watt is 1 joule per second. A kilowatt-hour is 3.6 million joules – enough to lift a bucket of water about 300 metres.",
    )
    val dragPower get() = l(
        "Beim Radfahren geht fast die ganze Kraft gegen die Luft. Die nötige Leistung wächst mit der dritten Potenz der Geschwindigkeit: Doppelt so schnell kostet achtmal so viel Leistung. Deshalb fahren Radprofis im Windschatten und so tief geduckt wie möglich – jeder Quadratzentimeter Stirnfläche zählt.",
        "When cycling, almost all your effort goes against the air. The power needed grows with the cube of the speed: twice as fast costs eight times the power. That's why pro cyclists ride in each other's slipstream and crouch as low as possible – every square centimetre of frontal area counts.",
    )
    val momentum get() = l(
        "Impuls ist Masse mal Geschwindigkeit, und in einem abgeschlossenen System bleibt die Summe aller Impulse gleich – bei jedem Stoß, jeder Explosion, jedem Rückstoß. Beim unelastischen Stoß kleben die Körper zusammen und ein Teil der Bewegungsenergie wird zu Wärme und Verformung; der Impuls bleibt trotzdem erhalten.",
        "Momentum is mass times velocity, and in a closed system the total momentum stays the same – in every collision, explosion and recoil. In an inelastic collision the objects stick together and part of the kinetic energy turns into heat and deformation; momentum is still conserved.",
    )
    val impulse get() = l(
        "Ein Kraftstoß ist Kraft mal Zeit und ändert den Impuls. Dieselbe Impulsänderung kann man mit großer Kraft in kurzer Zeit oder mit kleiner Kraft in langer Zeit erreichen. Airbags, Turnmatten und Knautschzonen verlängern die Bremszeit – und machen damit die Kraft auf den Körper viel kleiner.",
        "An impulse is force times time and changes momentum. The same change of momentum can come from a large force over a short time or a small force over a long time. Airbags, gym mats and crumple zones stretch the stopping time – and make the force on the body much smaller.",
    )
    val elastic get() = l(
        "Beim elastischen Stoß bleiben Impuls und Bewegungsenergie erhalten – wie bei Billardkugeln, fast jedenfalls. Trifft eine Kugel eine gleich schwere ruhende, bleibt sie stehen und die andere rollt mit voller Geschwindigkeit weiter. Ist die gestoßene Kugel viel schwerer, prallt die leichte fast mit ihrer Geschwindigkeit zurück.",
        "In an elastic collision both momentum and kinetic energy are conserved – like billiard balls, almost. When a ball hits an identical one at rest, it stops and the other rolls on at full speed. If the ball being hit is much heavier, the light one bounces back with almost its full speed.",
    )
}

/** Formelkarten. */
private object Fc {
    val speed get() = card(l("Geschwindigkeit", "Speed"), """§v = \dfrac{s}{t}§""", l("Weg durch Zeit. 1 m/s = 3,6 km/h.", "Distance over time. 1 m/s = 3.6 km/h."))
    val brake get() = card(l("Bremsweg", "Braking distance"), """§s_B = \dfrac{v^2}{2a}§""", l("Bis zum Stillstand bei konstanter Verzögerung a.", "Until stopping at constant deceleration a."))
    val fall get() = card(l("Freier Fall", "Free fall"), """§h = \tfrac{1}{2} g t^2§""", l("Ohne Luftwiderstand, aus der Ruhe.", "Without air resistance, starting at rest."))
    val range get() = card(l("Wurfweite", "Range"), """§R = \dfrac{v_0^2 \sin(2\alpha)}{g}§""", l("Schräger Wurf auf ebenem Boden, ohne Luft.", "Oblique throw on level ground, no air."))
    val newton get() = card(l("Grundgesetz der Mechanik", "Newton's second law"), """§F = m \cdot a§""", l("Kraft gleich Masse mal Beschleunigung.", "Force equals mass times acceleration."))
    val weight get() = card(l("Gewichtskraft", "Weight"), """§F_G = m \cdot g§""", l("Auf der Erde ist g = 9,81 m/s².", "On Earth g = 9.81 m/s²."))
    val friction get() = card(l("Reibung", "Friction"), """§F_R = \mu \cdot F_N§""", l("Reibungszahl mal Normalkraft.", "Friction coefficient times normal force."))
    val incline get() = card(l("Schiefe Ebene", "Incline"), """§a = g(\sin\alpha - \mu\cos\alpha)§""", l("Beschleunigung hangabwärts mit Gleitreibung.", "Acceleration down the slope with sliding friction."))
    val hooke get() = card(l("Hookesches Gesetz", "Hooke's law"), """§F = D \cdot x§""", l("Kraft gleich Federkonstante mal Dehnung.", "Force equals spring constant times extension."))
    val potential get() = card(l("Lageenergie", "Potential energy"), """§E_{pot} = m g h§""", l("Energie durch Höhe im Schwerefeld.", "Energy due to height in a gravitational field."))
    val kinetic get() = card(l("Bewegungsenergie", "Kinetic energy"), """§E_{kin} = \tfrac{1}{2} m v^2§""", l("Wächst mit dem Quadrat der Geschwindigkeit.", "Grows with the square of the speed."))
    val power get() = card(l("Leistung", "Power"), """§P = \dfrac{E}{t}§""", l("Energie pro Zeit. 1 W = 1 J/s.", "Energy per time. 1 W = 1 J/s."))
    val dragP get() = card(l("Leistung gegen Luft", "Power against air"), """§P = \tfrac{1}{2} \rho\, c_w A\, v^3§""", l("Luftwiderstandskraft mal Geschwindigkeit.", "Drag force times speed."))
    val momentum get() = card(l("Impulserhaltung", "Conservation of momentum"), """§m_1 v_1 + m_2 v_2 = m_1 v_1' + m_2 v_2'§""", l("Die Summe der Impulse bleibt gleich.", "The total momentum stays the same."))
    val impulse get() = card(l("Kraftstoß", "Impulse"), """§F \cdot \Delta t = m \cdot \Delta v§""", l("Kraft mal Zeit ändert den Impuls.", "Force times time changes momentum."))
    val elastic get() = card(l("Elastischer Stoß", "Elastic collision"), """§v_1' = \dfrac{m_1 - m_2}{m_1 + m_2}\, v_1,\quad v_2' = \dfrac{2 m_1}{m_1 + m_2}\, v_1§""", l("Körper 2 ruht vorher.", "Object 2 is at rest before."))
}

object LabMechanics {
    val all: List<TaskDef> get() = motion + forces + energy + momentum + LabMechanics2.all

    // ===================================== BEWEGUNG =====================================
    private val motion = listOf(
        lab("schulweg", Topic.Motion, E, 0.2f) {
            val km = tiered(listOf(2.0, 3.0, 4.0, 6.0), 1.6, 8.4, 0.2)
            val min = tiered(listOf(10.0, 12.0, 15.0, 20.0), 7.0, 26.0, 1.0)
            val v = km / (min / 60.0)
            task(
                question = l("Dein Schulweg mit dem Rad ist ${n(km)} km lang, du brauchst ${n(min, 0)} Minuten. Wie schnell bist du im Schnitt?", "Your bike ride to school is ${n(km)} km and takes ${n(min, 0)} minutes. What is your average speed?"),
                steps = listOf(
                    num(l("Durchschnittsgeschwindigkeit", "Average speed"), v, "km/h", rel = 0.03,
                        done = """§\bar v \approx ${m(sig(v))}\ \text{km/h}§""",
                        hints = listOf(l("Kernidee: Weg durch Zeit – rechne die Minuten in Stunden um.", "Key idea: distance over time – convert the minutes into hours."), """§t = ${m(min, 0)}\ \text{min} = ${m(min / 60.0, 3)}\ \text{h}§"""),
                        formula = Fc.speed),
                ),
                solution = listOf("""§\bar v = \dfrac{${m(km)}\ \text{km}}{${m(min / 60.0, 3)}\ \text{h}} \approx ${m(sig(v))}\ \text{km/h}§"""),
                background = Bg.speed,
                fact = l("Ein Radfahrer ist auf kurzen Stadtstrecken oft schneller als ein Auto – wegen Ampeln, Stau und Parkplatzsuche.", "On short city trips a cyclist is often faster than a car – because of lights, traffic and finding a parking spot."),
            )
        },
        lab("tacho", Topic.Motion, E, 0.1f) {
            val (q, a) = pick(
                l("Du fährst 2 Stunden Auto und kommst 160 km weit. Unterwegs zeigt der Tacho einmal 130 km/h. Was beschreibt die 130?", "You drive for 2 hours and cover 160 km. At one point the speedometer shows 130 km/h. What do the 130 describe?") to
                    listOf(l("Wie schnell du in genau diesem Moment warst", "How fast you were at that exact moment"), l("Deine Durchschnittsgeschwindigkeit", "Your average speed"), l("Wie weit du in einer Stunde gekommen bist", "How far you got in one hour"), l("Deine Beschleunigung", "Your acceleration")),
                l("Ein Zug fährt 300 km in 2 Stunden, hält aber zweimal 10 Minuten. Was ist 150 km/h?", "A train covers 300 km in 2 hours but stops twice for 10 minutes. What is 150 km/h?") to
                    listOf(l("Seine Durchschnittsgeschwindigkeit für die ganze Fahrt", "Its average speed for the whole trip"), l("Seine Höchstgeschwindigkeit", "Its top speed"), l("Seine Geschwindigkeit zwischen den Halten", "Its speed between the stops"), l("Seine Geschwindigkeit beim Bremsen", "Its speed while braking")),
            )
            task(
                question = q,
                steps = listOf(choice(l("Welche Aussage stimmt?", "Which statement is right?"), a, done = a.first(),
                    hints = listOf(l("Kernidee: Ein Durchschnitt fasst die ganze Fahrt zusammen, der Tacho zeigt einen Augenblick.", "Key idea: an average sums up the whole trip, the speedometer shows one instant."), l("Durchschnitt = ganzer Weg durch ganze Zeit.", "Average = whole distance over whole time.")), formula = Fc.speed)),
                solution = listOf(l("Durchschnittsgeschwindigkeit = gesamter Weg / gesamte Zeit, Momentangeschwindigkeit = Tempo in einem Augenblick.", "Average speed = total distance / total time, instantaneous speed = speed at one instant.")),
                background = Bg.speed,
            )
        },
        lab("bremsweg", Topic.Motion, M, 0.4f) {
            val kmh = tiered(listOf(30.0, 50.0, 70.0, 100.0), 40.0, 130.0, 5.0)
            val a = tiered(listOf(6.0, 7.0, 8.0), 4.5, 8.5, 0.5)
            val v = kmh / 3.6
            val s = v * v / (2 * a)
            val tReact = if (hard) step(0.8, 1.4, 0.1) else 0.0
            val sr = v * tReact
            val steps = buildList {
                if (hard) add(num(l("Reaktionsweg (Strecke, bevor du bremst)", "Reaction distance (before you brake)"), sr, "m", rel = 0.03,
                    done = """§s_R \approx ${m(sig(sr))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: In der Reaktionszeit fährst du ungebremst weiter.", "Key idea: during your reaction time you keep going at full speed."), """§s_R = v \cdot t_R = ${m(v, 2)}\ \tfrac{\text{m}}{\text{s}} \cdot ${m(tReact)}\ \text{s}§"""), formula = Fc.speed))
                add(num(l("Bremsweg", "Braking distance"), s, "m", rel = 0.03,
                    done = """§s_B \approx ${m(sig(s))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Die Bewegungsenergie muss von der Bremskraft aufgezehrt werden – rechne zuerst in m/s um.", "Key idea: the brakes have to eat up all the kinetic energy – convert to m/s first."), """§v = ${m(kmh, 0)} : 3{,}6 \approx ${m(v, 2)}\ \tfrac{\text{m}}{\text{s}},\quad s_B = \dfrac{v^2}{2a}§"""), formula = Fc.brake))
                if (hard) add(num(l("Anhalteweg insgesamt", "Total stopping distance"), sr + s, "m", rel = 0.03,
                    done = """§s \approx ${m(sig(sr + s))}\ \text{m}§""",
                    hints = listOf(l("Reaktionsweg plus Bremsweg.", "Reaction distance plus braking distance."), """§s = s_R + s_B§""")))
            }
            task(
                question = if (hard) l("Ein Auto fährt ${n(kmh, 0)} km/h. Die Fahrerin reagiert nach ${n(tReact)} s und bremst dann mit ${n(a)} m/s² bis zum Stillstand.", "A car drives at ${n(kmh, 0)} km/h. The driver reacts after ${n(tReact)} s and then brakes at ${n(a)} m/s² until it stops.")
                else l("Ein Auto fährt ${n(kmh, 0)} km/h und bremst mit ${n(a)} m/s² voll bis zum Stillstand. Wie lang ist der Bremsweg?", "A car drives at ${n(kmh, 0)} km/h and brakes at ${n(a)} m/s² until it stops. How long is the braking distance?"),
                steps = steps,
                solution = listOfNotNull(
                    """§v = ${m(kmh, 0)}\ \tfrac{\text{km}}{\text{h}} : 3{,}6 \approx ${m(v, 2)}\ \tfrac{\text{m}}{\text{s}}§""",
                    if (hard) """§s_R = ${m(v, 2)}\ \tfrac{\text{m}}{\text{s}} \cdot ${m(tReact)}\ \text{s} \approx ${m(sig(sr))}\ \text{m}§""" else null,
                    """§s_B = \dfrac{(${m(v, 2)}\ \text{m/s})^2}{2 \cdot ${m(a)}\ \text{m/s}^2} \approx ${m(sig(s))}\ \text{m}§""",
                ),
                background = Bg.braking,
                fact = l("Fahrschul-Faustregel für den Bremsweg: (Tempo in km/h durch 10) zum Quadrat – das gilt für normales, nicht für Notbremsen.", "Driving-school rule of thumb for braking distance: (speed in km/h divided by 10) squared – that's for normal braking, not emergency stops."),
            )
        },
        lab("ueberholen", Topic.Motion, M, 0.6f) {
            val v1 = tiered(listOf(90.0, 100.0), 85.0, 120.0, 5.0)
            val v2 = tiered(listOf(80.0, 70.0), 60.0, 80.0, 5.0)
            val need = tiered(listOf(60.0, 80.0), 50.0, 100.0, 5.0)
            val dv = (v1 - v2) / 3.6
            val t = need / dv
            val s = v1 / 3.6 * t
            task(
                question = l("Ein Auto (${n(v1, 0)} km/h) überholt einen Lkw (${n(v2, 0)} km/h). Insgesamt muss es ${n(need, 0)} m gegenüber dem Lkw gutmachen (Abstand davor, Lkw-Länge, Abstand danach).", "A car (${n(v1, 0)} km/h) overtakes a truck (${n(v2, 0)} km/h). In total it has to gain ${n(need, 0)} m relative to the truck (gap before, truck length, gap after)."),
                steps = listOf(
                    num(l("Wie lange dauert das Überholen?", "How long does the overtaking take?"), t, "s", rel = 0.03,
                        done = """§t \approx ${m(sig(t))}\ \text{s}§""",
                        hints = listOf(l("Kernidee: Aus Sicht des Lkw bewegt sich das Auto nur mit dem Unterschied der beiden Tempos.", "Key idea: seen from the truck, the car only moves at the difference of the two speeds."), """§\Delta v = (${m(v1, 0)} - ${m(v2, 0)})\ \tfrac{\text{km}}{\text{h}} \approx ${m(dv, 2)}\ \tfrac{\text{m}}{\text{s}}§"""), formula = Fc.speed),
                    num(l("Welche Strecke fährt das Auto dabei auf der Gegenspur?", "How far does the car travel in the opposite lane?"), s, "m", rel = 0.03,
                        done = """§s \approx ${m(sig(s))}\ \text{m}§""",
                        hints = listOf(l("Jetzt zählt wieder das volle Tempo des Autos.", "Now the car's full speed counts again."), """§s = v_1 \cdot t§""")),
                ),
                solution = listOf(
                    """§\Delta v = \dfrac{${m(v1 - v2, 0)}}{3{,}6}\ \tfrac{\text{m}}{\text{s}} \approx ${m(dv, 2)}\ \tfrac{\text{m}}{\text{s}}§""",
                    """§t = \dfrac{${m(need, 0)}\ \text{m}}{${m(dv, 2)}\ \text{m/s}} \approx ${m(sig(t))}\ \text{s}§""",
                    """§s = \dfrac{${m(v1, 0)}}{3{,}6}\ \tfrac{\text{m}}{\text{s}} \cdot ${m(sig(t))}\ \text{s} \approx ${m(sig(s))}\ \text{m}§""",
                ),
                background = l(
                    "Relativgeschwindigkeit ist die Geschwindigkeit, mit der sich zwei Dinge einander nähern oder voneinander entfernen. Für das Überholen zählt nur der Unterschied der Tempos – deshalb dauert es so lange, einen Lkw mit fast gleichem Tempo zu überholen, und deshalb ist Überholen auf Landstraßen so gefährlich: Der Gegenverkehr kommt mit der Summe der Tempos auf dich zu.",
                    "Relative velocity is how fast two things approach or move apart. For overtaking only the difference in speed counts – that's why passing a truck going almost as fast takes ages, and why overtaking on country roads is so dangerous: oncoming traffic approaches you at the sum of both speeds.",
                ),
            )
        },
        lab("bruecke", Topic.Motion, H, 0.5f) {
            val h = tiered(listOf(5.0, 10.0, 20.0, 45.0), 4.0, 60.0, 1.0)
            val v0 = tiered(listOf(5.0, 8.0, 10.0), 3.0, 15.0, 0.5)
            val t = sqrt(2 * h / G)
            val x = v0 * t
            task(
                question = l("Du wirfst einen Stein mit ${n(v0)} m/s waagrecht von einer ${n(h, 0)} m hohen Brücke. Luftwiderstand vernachlässigen.", "You throw a stone horizontally at ${n(v0)} m/s from a bridge ${n(h, 0)} m high. Ignore air resistance."),
                steps = listOf(
                    num(l("Wie lange fliegt der Stein?", "How long does the stone fly?"), t, "s", rel = 0.02,
                        done = """§t \approx ${m(t, 2)}\ \text{s}§""",
                        hints = listOf(l("Kernidee: Senkrecht fällt der Stein wie im freien Fall – das waagrechte Tempo spielt dafür keine Rolle.", "Key idea: vertically the stone falls like in free fall – its horizontal speed doesn't matter for that."), """§h = \tfrac{1}{2} g t^2 \Rightarrow t = \sqrt{2h/g}§"""), formula = Fc.fall),
                    num(l("Wie weit vom Brückenrand landet er?", "How far from the bridge does it land?"), x, "m", rel = 0.03,
                        done = """§x \approx ${m(sig(x))}\ \text{m}§""",
                        hints = listOf(l("Waagrecht fliegt er die ganze Zeit gleichmäßig weiter.", "Horizontally it keeps moving steadily the whole time."), """§x = v_0 \cdot t§""")),
                ),
                solution = listOf(
                    """§t = \sqrt{\dfrac{2 \cdot ${m(h, 0)}\ \text{m}}{9{,}81\ \text{m/s}^2}} \approx ${m(t, 2)}\ \text{s}§""",
                    """§x = ${m(v0)}\ \tfrac{\text{m}}{\text{s}} \cdot ${m(t, 2)}\ \text{s} \approx ${m(sig(x))}\ \text{m}§""",
                ),
                background = Bg.projectile,
            )
        },
        lab("weitwurf", Topic.Motion, H, 0.6f) {
            val v0 = tiered(listOf(10.0, 15.0, 20.0, 25.0), 8.0, 32.0, 0.5)
            val ang = tiered(listOf(30.0, 45.0, 60.0), 20.0, 70.0, 5.0)
            val r = v0 * v0 * sin(2 * ang * PI / 180) / G
            val rMax = v0 * v0 / G
            task(
                question = l("Ein Ball wird mit ${n(v0)} m/s unter ${n(ang, 0)}° schräg nach oben geschossen und landet wieder auf gleicher Höhe.", "A ball is launched at ${n(v0)} m/s at ${n(ang, 0)}° above the horizontal and lands at the same height."),
                steps = listOf(
                    num(l("Wurfweite", "Range"), r, "m", rel = 0.03, done = """§R \approx ${m(sig(r))}\ \text{m}§""",
                        hints = listOf(l("Kernidee: Die Flugzeit hängt vom senkrechten, die Weite vom waagrechten Anteil ab – zusammen ergibt sich die Wurfweitenformel.", "Key idea: the flight time depends on the vertical part, the range on the horizontal part – together they give the range formula."), """§R = \dfrac{v_0^2 \sin(2 \cdot ${m(ang, 0)}^\circ)}{g}§"""), formula = Fc.range),
                    num(l("Welche Weite wäre beim besten Winkel möglich?", "What range would the best angle give?"), rMax, "m", rel = 0.03, done = """§R_{max} \approx ${m(sig(rMax))}\ \text{m}\ (45^\circ)§""",
                        hints = listOf(l("Der Sinus ist am größten, wenn 2α = 90° ist.", "The sine is largest when 2α = 90°."), """§R_{max} = v_0^2 / g§""")),
                ),
                solution = listOf(
                    """§R = \dfrac{(${m(v0)}\ \text{m/s})^2 \cdot \sin(${m(2 * ang, 0)}^\circ)}{9{,}81\ \text{m/s}^2} \approx ${m(sig(r))}\ \text{m}§""",
                    """§R_{max} = \dfrac{(${m(v0)}\ \text{m/s})^2}{9{,}81\ \text{m/s}^2} \approx ${m(sig(rMax))}\ \text{m}§""",
                ),
                background = Bg.projectile,
                fact = l("Mit Luftwiderstand liegt der beste Winkel unter 45° – Kugelstoßer werfen etwa unter 37°, auch weil sie höher abwerfen, als der Boden ist.", "With air resistance the best angle is below 45° – shot putters release at about 37°, also because they release above ground level."),
            )
        },
        lab("fallschirm", Topic.Motion, X, 0.6f) {
            val mass = tiered(listOf(70.0, 80.0, 90.0), 55.0, 110.0, 1.0)
            val area = pick(0.5, 0.7)
            val cw = step(1.0, 1.3, 0.1)
            val rho = 1.2
            val vt = sqrt(2 * mass * G / (rho * cw * area))
            val chuteA = tiered(listOf(25.0, 30.0), 20.0, 40.0, 1.0)
            val vt2 = sqrt(2 * mass * G / (rho * 1.4 * chuteA))
            task(
                question = l("Eine Fallschirmspringerin (mit Ausrüstung ${n(mass, 0)} kg) fällt im Bauchflug: Stirnfläche ${n(area)} m², cw-Wert ${n(cw)}. Luftdichte 1,2 kg/m³.", "A skydiver (with gear ${n(mass, 0)} kg) falls belly-down: frontal area ${n(area)} m², drag coefficient ${n(cw)}. Air density 1.2 kg/m³."),
                steps = listOf(
                    num(l("Endgeschwindigkeit im freien Fall", "Terminal velocity in free fall"), vt, "m/s", rel = 0.03, done = """§v_E \approx ${m(sig(vt))}\ \text{m/s} \approx ${m(sig(vt * 3.6))}\ \text{km/h}§""",
                        hints = listOf(l("Kernidee: Am Ende ist der Luftwiderstand genauso groß wie das Gewicht.", "Key idea: in the end, drag equals weight."), """§\tfrac{1}{2} \rho\, c_w A\, v^2 = m g§"""),
                        formula = card(l("Luftwiderstand", "Air drag"), """§F_L = \tfrac{1}{2} \rho\, c_w A\, v^2§""", l("Wächst mit dem Quadrat der Geschwindigkeit.", "Grows with the square of the speed."))),
                    num(l("Und mit offenem Schirm (${n(chuteA, 0)} m², cw = 1,4)?", "And with the canopy open (${n(chuteA, 0)} m², drag coefficient 1.4)?"), vt2, "m/s", rel = 0.03, done = """§v_E \approx ${m(sig(vt2))}\ \text{m/s}§""",
                        hints = listOf(l("Gleiche Bedingung, nur andere Fläche und Form.", "Same condition, just a different area and shape."), """§v_E = \sqrt{\dfrac{2 m g}{\rho\, c_w A}}§""")),
                ),
                solution = listOf(
                    """§v_E = \sqrt{\dfrac{2 \cdot ${m(mass, 0)} \cdot 9{,}81}{1{,}2 \cdot ${m(cw)} \cdot ${m(area)}}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vt))}\ \tfrac{\text{m}}{\text{s}}§""",
                    """§v_E' = \sqrt{\dfrac{2 \cdot ${m(mass, 0)} \cdot 9{,}81}{1{,}2 \cdot 1{,}4 \cdot ${m(chuteA, 0)}}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vt2))}\ \tfrac{\text{m}}{\text{s}}§""",
                ),
                background = Bg.drag,
                fact = l("Im Kopfüber-Flug erreichen Speed-Skydiver über 500 km/h – die Rekorde liegen bei über 600 km/h.", "Head-down speed skydivers exceed 500 km/h – records are above 600 km/h."),
            )
        },
        lab("regen", Topic.Motion, X, 0.4f) {
            val vr = tiered(listOf(6.0, 8.0), 5.0, 9.0, 0.5)
            val kmh = tiered(listOf(36.0, 54.0, 72.0), 30.0, 110.0, 2.0)
            val vc = kmh / 3.6
            val ang = atan(vc / vr) * 180 / PI
            task(
                question = l("Regentropfen fallen bei Windstille senkrecht mit ${n(vr)} m/s. Du fährst im Zug mit ${n(kmh, 0)} km/h. Unter welchem Winkel zur Senkrechten laufen die Regenspuren über das Seitenfenster?", "Raindrops fall straight down at ${n(vr)} m/s with no wind. You ride a train at ${n(kmh, 0)} km/h. At what angle from the vertical do the rain streaks run across the side window?"),
                steps = listOf(num(l("Winkel zur Senkrechten", "Angle from the vertical"), ang, "°", abs = 1.5,
                    done = """§\alpha \approx ${m(ang, 0)}^\circ§""",
                    hints = listOf(l("Kernidee: Im Zug siehst du die Tropfen mit deiner Geschwindigkeit entgegengesetzt zur Fahrtrichtung, zusätzlich zum Fallen.", "Key idea: in the train you see the drops moving backwards at your speed, on top of their falling."), l("""§\tan\alpha = \dfrac{v_{Zug}}{v_{Regen}}§""", """§\tan\alpha = \dfrac{v_{train}}{v_{rain}}§""")),
                    formula = card(l("Relativgeschwindigkeit", "Relative velocity"), l("""§\vec v_{rel} = \vec v_{Regen} - \vec v_{Zug}§""", """§\vec v_{rel} = \vec v_{rain} - \vec v_{train}§"""), l("Geschwindigkeiten addieren sich als Pfeile.", "Velocities add as arrows.")))),
                solution = listOf(l("""§v_{Zug} = ${m(kmh, 0)} : 3{,}6 \approx ${m(vc, 2)}\ \tfrac{\text{m}}{\text{s}}§""", """§v_{train} = ${m(kmh, 0)} : 3{,}6 \approx ${m(vc, 2)}\ \tfrac{\text{m}}{\text{s}}§"""), """§\tan\alpha = \dfrac{${m(vc, 2)}}{${m(vr)}} \Rightarrow \alpha \approx ${m(ang, 0)}^\circ§"""),
                background = l(
                    "Geschwindigkeiten sind Pfeile: Sie haben eine Größe und eine Richtung und addieren sich wie Pfeile hintereinander. Was du siehst, ist immer die Geschwindigkeit relativ zu dir. Deshalb scheint der Regen schräg von vorn zu kommen, wenn du läufst – und deshalb hält man den Schirm beim Gehen leicht nach vorn geneigt.",
                    "Velocities are arrows: they have a size and a direction and add like arrows placed tip to tail. What you see is always the velocity relative to you. That's why rain seems to come at a slant from the front when you run – and why you tilt your umbrella forward when walking.",
                ),
            )
        },
    )

    // ===================================== KRÄFTE =====================================
    private val forces = listOf(
        lab("mondwaage", Topic.Forces, E, 0.2f) {
            val mass = tiered(listOf(40.0, 50.0, 60.0, 75.0), 30.0, 95.0, 1.0)
            val (place, gx) = pick(l("auf dem Mond", "on the Moon") to 1.62, l("auf dem Mars", "on Mars") to 3.71, l("auf dem Jupiter (falls man stehen könnte)", "on Jupiter (if you could stand there)") to 24.8)
            val f = mass * gx
            task(
                question = l("Du hast eine Masse von ${n(mass, 0)} kg. Mit welcher Kraft zieht dich die Schwerkraft $place nach unten?", "Your mass is ${n(mass, 0)} kg. With what force does gravity pull you down $place?"),
                given = listOf("""§g = ${m(gx)}\ \text{m/s}^2§"""),
                steps = listOf(num(l("Gewichtskraft", "Weight"), f, "N", rel = 0.02, done = """§F_G \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Die Masse bleibt gleich, nur g ist anders.", "Key idea: the mass stays the same, only g is different."), """§F_G = m \cdot g = ${m(mass, 0)}\ \text{kg} \cdot ${m(gx)}\ \tfrac{\text{m}}{\text{s}^2}§"""), formula = Fc.weight)),
                solution = listOf("""§F_G = ${m(mass, 0)}\ \text{kg} \cdot ${m(gx)}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(f))}\ \text{N}§"""),
                background = Bg.newton,
                fact = l("Eine Personenwaage rechnet die Kraft in Kilogramm um – auf dem Mond würde sie dir ein Sechstel deiner Masse anzeigen.", "A bathroom scale converts the force into kilograms – on the Moon it would show a sixth of your mass."),
            )
        },
        lab("wandschieben", Topic.Forces, E, 0.3f) {
            val opts = pick(
                l("Du stehst auf Inlinern und drückst mit 100 N gegen eine Wand. Was passiert?", "You stand on inline skates and push against a wall with 100 N. What happens?") to
                    listOf(l("Die Wand drückt mit 100 N zurück, du rollst nach hinten.", "The wall pushes back with 100 N and you roll backwards."), l("Nichts – eine Wand kann keine Kraft ausüben.", "Nothing – a wall can't exert a force."), l("Die Wand drückt mit weniger Kraft zurück, weil sie sich nicht bewegt.", "The wall pushes back with less force because it doesn't move."), l("Du bleibst stehen, weil sich die Kräfte aufheben.", "You stay put because the forces cancel.")),
                l("Ein Lkw und eine Mücke stoßen frontal zusammen. Wer erfährt die größere Kraft?", "A truck and a mosquito collide head-on. Which one feels the larger force?") to
                    listOf(l("Beide gleich viel – nur die Mücke hat viel weniger Masse.", "Both the same – the mosquito just has far less mass."), l("Der Lkw, weil er schwerer ist.", "The truck, because it is heavier."), l("Die Mücke, weil sie kaputtgeht.", "The mosquito, because it gets squashed."), l("Keiner – die Mücke ist zu leicht.", "Neither – the mosquito is too light.")),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Kräfte treten immer paarweise auf – actio gleich reactio.", "Key idea: forces always come in pairs – action equals reaction."), l("Die beiden Kräfte wirken auf verschiedene Körper, deshalb heben sie sich nicht auf.", "The two forces act on different bodies, so they don't cancel.")),
                    formula = card(l("Wechselwirkungsprinzip", "Third law"), """§\vec F_{12} = -\vec F_{21}§""", l("Gleich groß, entgegengesetzt, auf verschiedene Körper.", "Equal, opposite, on different bodies.")))),
                solution = listOf(l("Nach dem dritten Newtonschen Gesetz sind beide Kräfte gleich groß. Was sich unterscheidet, ist die Wirkung: Bei kleiner Masse bewirkt dieselbe Kraft eine große Beschleunigung.", "By Newton's third law both forces are equal. What differs is the effect: with a small mass, the same force causes a large acceleration.")),
                background = Bg.newton,
            )
        },
        lab("schlitten", Topic.Forces, M, 0.5f) {
            val mass = tiered(listOf(20.0, 30.0, 40.0), 15.0, 60.0, 1.0)
            val f = tiered(listOf(50.0, 80.0, 100.0), 40.0, 160.0, 5.0)
            val mu = tiered(listOf(0.1, 0.05), 0.03, 0.15, 0.01)
            val fr = mu * mass * G
            val a = (f - fr) / mass
            task(
                question = l("Du ziehst einen Schlitten (mit Kind ${n(mass, 0)} kg) waagrecht mit ${n(f, 0)} N über Schnee. Gleitreibungszahl μ = ${n(mu)}.", "You pull a sledge (with a child, ${n(mass, 0)} kg) horizontally with ${n(f, 0)} N across snow. Sliding friction coefficient μ = ${n(mu)}."),
                steps = listOf(
                    num(l("Reibungskraft", "Friction force"), fr, "N", rel = 0.02, done = """§F_R \approx ${m(sig(fr))}\ \text{N}§""",
                        hints = listOf(l("Kernidee: Die Reibung hängt von der Normalkraft ab – hier ist das das Gewicht.", "Key idea: friction depends on the normal force – here that's the weight."), """§F_R = \mu\, m g§"""), formula = Fc.friction),
                    num(l("Beschleunigung des Schlittens", "Acceleration of the sledge"), a, "m/s²", rel = 0.03, done = """§a \approx ${m(sig(a))}\ \text{m/s}^2§""",
                        hints = listOf(l("Nur die Kraft, die nach Abzug der Reibung übrig bleibt, beschleunigt.", "Only the force left after friction accelerates the sledge."), """§a = \dfrac{F - F_R}{m}§"""), formula = Fc.newton),
                ),
                solution = listOf(
                    """§F_R = ${m(mu)} \cdot ${m(mass, 0)}\ \text{kg} \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(fr))}\ \text{N}§""",
                    """§a = \dfrac{${m(f, 0)}\ \text{N} - ${m(sig(fr))}\ \text{N}}{${m(mass, 0)}\ \text{kg}} \approx ${m(sig(a))}\ \tfrac{\text{m}}{\text{s}^2}§""",
                ),
                background = Bg.friction,
            )
        },
        lab("feder", Topic.Forces, E, 0.3f) {
            val d = tiered(listOf(200.0, 500.0, 1000.0), 150.0, 2500.0, 50.0)
            val mass = tiered(listOf(2.0, 5.0, 10.0), 1.5, 25.0, 0.5)
            val x = mass * G / d
            task(
                question = l("An eine Feder mit der Federkonstante ${n(d, 0)} N/m hängst du eine Tasche mit ${n(mass)} kg. Um wie viele Zentimeter dehnt sich die Feder?", "You hang a bag of ${n(mass)} kg on a spring with spring constant ${n(d, 0)} N/m. By how many centimetres does the spring stretch?"),
                steps = listOf(num(l("Dehnung", "Extension"), x * 100, "cm", rel = 0.03, done = """§x \approx ${m(sig(x * 100))}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Die Feder dehnt sich, bis ihre Kraft das Gewicht trägt.", "Key idea: the spring stretches until its force holds up the weight."), """§x = \dfrac{m g}{D}§"""), formula = Fc.hooke)),
                solution = listOf("""§x = \dfrac{${m(mass)}\ \text{kg} \cdot 9{,}81\ \text{m/s}^2}{${m(d, 0)}\ \text{N/m}} \approx ${m(x, 4)}\ \text{m} \approx ${m(sig(x * 100))}\ \text{cm}§"""),
                background = Bg.spring,
            )
        },
        lab("rampe", Topic.Forces, H, 0.6f) {
            val ang = tiered(listOf(30.0, 25.0, 35.0), 18.0, 40.0, 1.0)
            val mu = tiered(listOf(0.2, 0.3), 0.1, 0.4, 0.05)
            val len = tiered(listOf(4.0, 5.0, 8.0), 3.0, 12.0, 0.5)
            val a = G * (sin(ang * PI / 180) - mu * cos(ang * PI / 180))
            val t = sqrt(2 * len / a)
            task(
                question = l("Eine Kiste rutscht aus der Ruhe eine ${n(len)} m lange Rampe mit ${n(ang, 0)}° Neigung hinunter. Gleitreibungszahl μ = ${n(mu)}.", "A crate slides from rest down a ${n(len)} m long ramp inclined at ${n(ang, 0)}°. Sliding friction coefficient μ = ${n(mu)}."),
                steps = listOf(
                    num(l("Beschleunigung hangabwärts", "Acceleration down the slope"), a, "m/s²", rel = 0.03, done = """§a \approx ${m(sig(a))}\ \text{m/s}^2§""",
                        hints = listOf(l("Kernidee: Hangabtrieb minus Reibung – und die Reibung hängt nur vom Teil des Gewichts ab, der gegen die Rampe drückt.", "Key idea: pull down the slope minus friction – and friction only depends on the part of the weight pressing into the ramp."), """§a = g(\sin\alpha - \mu \cos\alpha)§"""), formula = Fc.incline),
                    num(l("Wie lange braucht sie bis unten?", "How long does it take to reach the bottom?"), t, "s", rel = 0.03, done = """§t \approx ${m(sig(t))}\ \text{s}§""",
                        hints = listOf(l("Gleichmäßig beschleunigt aus der Ruhe.", "Uniformly accelerated from rest."), """§s = \tfrac{1}{2} a t^2 \Rightarrow t = \sqrt{2s/a}§""")),
                ),
                solution = listOf(
                    """§a = 9{,}81 \cdot (\sin ${m(ang, 0)}^\circ - ${m(mu)} \cdot \cos ${m(ang, 0)}^\circ)\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(a))}\ \tfrac{\text{m}}{\text{s}^2}§""",
                    """§t = \sqrt{\dfrac{2 \cdot ${m(len)}\ \text{m}}{${m(sig(a))}\ \text{m/s}^2}} \approx ${m(sig(t))}\ \text{s}§""",
                ),
                background = Bg.incline,
            )
        },
        lab("aufzug", Topic.Forces, H, 0.4f) {
            val mass = tiered(listOf(50.0, 60.0, 70.0), 45.0, 95.0, 1.0)
            val a = tiered(listOf(1.0, 2.0), 0.5, 3.0, 0.1)
            val up = chance(0.5)
            val nF = mass * (G + if (up) a else -a)
            val shown = nF / G
            task(
                question = if (up) l("Du (${n(mass, 0)} kg) stehst im Aufzug auf einer Waage. Der Aufzug fährt an und beschleunigt mit ${n(a)} m/s² nach oben. Was zeigt die Waage (in kg)?", "You (${n(mass, 0)} kg) stand on a scale in a lift. The lift starts and accelerates upwards at ${n(a)} m/s². What does the scale show (in kg)?")
                else l("Du (${n(mass, 0)} kg) stehst im Aufzug auf einer Waage. Der Aufzug fährt nach unten an und beschleunigt mit ${n(a)} m/s². Was zeigt die Waage (in kg)?", "You (${n(mass, 0)} kg) stand on a scale in a lift. The lift starts downwards with an acceleration of ${n(a)} m/s². What does the scale show (in kg)?"),
                steps = listOf(num(l("Anzeige der Waage", "Scale reading"), shown, "kg", rel = 0.02, done = """§\approx ${m(sig(shown))}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Die Waage muss dich nicht nur tragen, sondern auch mitbeschleunigen.", "Key idea: the scale has to not only hold you up but also accelerate you."), if (up) """§F_N = m(g + a)§""" else """§F_N = m(g - a)§"""), formula = Fc.newton)),
                solution = listOf(
                    if (up) """§F_N = ${m(mass, 0)}\ \text{kg} \cdot (9{,}81 + ${m(a)})\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(nF))}\ \text{N}§""" else """§F_N = ${m(mass, 0)}\ \text{kg} \cdot (9{,}81 - ${m(a)})\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(nF))}\ \text{N}§""",
                    l("""Die Waage rechnet durch §g§: §${m(sig(nF))} : 9{,}81 \approx ${m(sig(shown))}\ \text{kg}§""", """The scale divides by §g§: §${m(sig(nF))} : 9{,}81 \approx ${m(sig(shown))}\ \text{kg}§"""),
                ),
                background = Bg.apparent,
            )
        },
        lab("atwood", Topic.Forces, X, 0.6f) {
            val m1 = tiered(listOf(3.0, 5.0), 2.0, 9.0, 0.5)
            val m2 = tiered(listOf(2.0, 3.0), 1.0, 6.0, 0.5).coerceAtMost(m1 - 0.5)
            val a = (m1 - m2) * G / (m1 + m2)
            val tension = m2 * (G + a)
            task(
                question = l("Über eine reibungsfreie Rolle läuft eine leichte Schnur. Links hängt ${n(m1)} kg, rechts ${n(m2)} kg. Man lässt los.", "A light string runs over a frictionless pulley. ${n(m1)} kg hangs on the left, ${n(m2)} kg on the right. They are released."),
                steps = listOf(
                    num(l("Beschleunigung der Massen", "Acceleration of the masses"), a, "m/s²", rel = 0.02, done = """§a \approx ${m(sig(a))}\ \text{m/s}^2§""",
                        hints = listOf(l("Kernidee: Antreibend wirkt nur der Gewichtsunterschied, beschleunigt werden aber beide Massen.", "Key idea: only the difference in weight drives the motion, but both masses have to be accelerated."), """§a = \dfrac{(m_1 - m_2)\, g}{m_1 + m_2}§"""), formula = Fc.newton),
                    num(l("Kraft in der Schnur", "Tension in the string"), tension, "N", rel = 0.02, done = """§F_S \approx ${m(sig(tension))}\ \text{N}§""",
                        hints = listOf(l("Schau auf die leichtere Masse: Die Schnur muss sie tragen und nach oben beschleunigen.", "Look at the lighter mass: the string has to hold it and accelerate it upwards."), """§F_S = m_2 (g + a)§""")),
                ),
                solution = listOf(
                    """§a = \dfrac{(${m(m1)} - ${m(m2)})\ \text{kg} \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2}}{${m(m1 + m2)}\ \text{kg}} \approx ${m(sig(a))}\ \tfrac{\text{m}}{\text{s}^2}§""",
                    """§F_S = ${m(m2)}\ \text{kg} \cdot (9{,}81 + ${m(sig(a))})\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(tension))}\ \text{N}§""",
                ),
                background = l(
                    "Die Atwoodsche Fallmaschine (1784) macht den freien Fall langsam: Weil fast gleich schwere Massen gegeneinander ziehen, ist die Beschleunigung klein und man kann sie mit einfachen Uhren messen. So ließ sich Newtons F = m · a zum ersten Mal genau prüfen. Die Kraft in der Schnur liegt immer zwischen den beiden Gewichten.",
                    "Atwood's machine (1784) slows free fall down: because two nearly equal masses pull against each other, the acceleration is small enough to time with simple clocks. It was one of the first precise tests of Newton's F = m · a. The tension in the string always lies between the two weights.",
                ),
            )
        },
        lab("klotzaufklotz", Topic.Forces, X, 0.7f) {
            val mTop = tiered(listOf(2.0, 3.0), 1.0, 5.0, 0.5)
            val mBot = tiered(listOf(5.0, 8.0), 4.0, 12.0, 0.5)
            val mus = tiered(listOf(0.4, 0.5), 0.3, 0.7, 0.05)
            val aMax = mus * G
            val fMax = (mTop + mBot) * aMax
            task(
                question = l("Ein Klotz (${n(mTop)} kg) liegt auf einem zweiten Klotz (${n(mBot)} kg), der reibungsfrei auf Eis liegt. Zwischen den Klötzen ist die Haftreibungszahl ${n(mus)}. Du ziehst am unteren Klotz.", "A block (${n(mTop)} kg) rests on a second block (${n(mBot)} kg) lying on frictionless ice. The static friction coefficient between the blocks is ${n(mus)}. You pull the lower block."),
                steps = listOf(
                    num(l("Größte Beschleunigung, bei der der obere Klotz noch mitkommt", "Largest acceleration at which the top block still comes along"), aMax, "m/s²", rel = 0.02, done = """§a_{max} \approx ${m(sig(aMax))}\ \text{m/s}^2§""",
                        hints = listOf(l("Kernidee: Den oberen Klotz beschleunigt nur die Haftreibung – und die ist begrenzt.", "Key idea: only static friction accelerates the top block – and it has a limit."), """§\mu_H\, m_o g = m_o\, a_{max} \Rightarrow a_{max} = \mu_H g§"""), formula = Fc.friction),
                    num(l("Mit welcher Kraft darfst du höchstens ziehen?", "What is the largest force you can pull with?"), fMax, "N", rel = 0.02, done = """§F_{max} \approx ${m(sig(fMax))}\ \text{N}§""",
                        hints = listOf(l("Bis dahin bewegen sich beide Klötze gemeinsam.", "Up to that point both blocks move together."), """§F_{max} = (m_o + m_u)\, a_{max}§"""), formula = Fc.newton),
                ),
                solution = listOf(
                    """§a_{max} = ${m(mus)} \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(aMax))}\ \tfrac{\text{m}}{\text{s}^2}§""",
                    """§F_{max} = ${m(mTop + mBot)}\ \text{kg} \cdot ${m(sig(aMax))}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(sig(fMax))}\ \text{N}§""",
                ),
                background = Bg.friction,
                fact = l("Derselbe Gedanke steckt hinter der Ladungssicherung im Lkw: Bremst er stärker, als die Reibung die Ladung halten kann, rutscht sie nach vorn.", "The same idea is behind securing loads on a truck: if it brakes harder than friction can hold the cargo, the cargo slides forward."),
            )
        },
    )

    // ===================================== ENERGIE =====================================
    private val energy = listOf(
        lab("stromkosten", Topic.Energy, E, 0.2f) {
            val (dev, p) = pick(l("Eine Spielkonsole", "A games console") to 150.0, l("Ein Föhn", "A hair dryer") to 1800.0, l("Ein Fernseher", "A TV") to 100.0, l("Ein Gaming-PC", "A gaming PC") to 450.0)
            val h = tiered(listOf(1.0, 2.0, 3.0), 0.5, 6.0, 0.5)
            val days = 30.0
            val price = tiered(listOf(0.3, 0.4), 0.25, 0.45, 0.01)
            val kwh = p / 1000 * h * days
            val cost = kwh * price
            task(
                question = l("$dev braucht ${n(p, 0)} W und läuft ${n(h)} ${if (h == 1.0) "Stunde" else "Stunden"} am Tag. Was kostet das in 30 Tagen bei ${n(price)} € pro kWh?", "$dev uses ${n(p, 0)} W and runs ${n(h)} ${if (h == 1.0) "hour" else "hours"} a day. What does that cost over 30 days at €${n(price)} per kWh?"),
                steps = listOf(num(l("Kosten in 30 Tagen", "Cost over 30 days"), cost, "€", rel = 0.03, done = """§\approx ${m(cost, 2)}\ \text{€}§""",
                    hints = listOf(l("Kernidee: Erst die Energie in kWh: Leistung in kW mal Stunden.", "Key idea: first the energy in kWh: power in kW times hours."), """§E = ${m(p / 1000, 3)}\ \text{kW} \cdot ${m(h * days, 1)}\ \text{h} = ${m(kwh, 2)}\ \text{kWh}§"""), formula = Fc.power)),
                solution = listOf(l("""§E = ${m(p / 1000, 3)}\ \text{kW} \cdot ${m(h)}\ \tfrac{\text{h}}{\text{Tag}} \cdot 30\ \text{Tage} = ${m(kwh, 2)}\ \text{kWh}§""", """§E = ${m(p / 1000, 3)}\ \text{kW} \cdot ${m(h)}\ \tfrac{\text{h}}{\text{day}} \cdot 30\ \text{days} = ${m(kwh, 2)}\ \text{kWh}§"""), """§K = ${m(kwh, 2)}\ \text{kWh} \cdot ${m(price)}\ \tfrac{\text{€}}{\text{kWh}} \approx ${m(cost, 2)}\ \text{€}§"""),
                background = Bg.energy,
            )
        },
        lab("treppe", Topic.Energy, E, 0.4f) {
            val mass = tiered(listOf(50.0, 60.0, 70.0), 40.0, 90.0, 1.0)
            val floors = tiered(listOf(3.0, 4.0, 5.0), 2.0, 12.0, 1.0)
            val hh = floors * 3.0
            val e = mass * G * hh
            task(
                question = l("Du (${n(mass, 0)} kg) steigst ${n(floors, 0)} Stockwerke hoch, jedes 3 m. Wie viel Lageenergie gewinnst du?", "You (${n(mass, 0)} kg) climb ${n(floors, 0)} floors, each 3 m high. How much potential energy do you gain?"),
                steps = listOf(num(l("Lageenergie", "Potential energy"), e / 1000, "kJ", rel = 0.02, done = """§E \approx ${m(sig(e / 1000))}\ \text{kJ}§""",
                    hints = listOf(l("Kernidee: Hubarbeit = Gewicht mal Höhe.", "Key idea: work against gravity = weight times height."), """§E = m g h,\ h = ${m(hh, 0)}\ \text{m}§"""), formula = Fc.potential)),
                solution = listOf("""§E = ${m(mass, 0)}\ \text{kg} \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \cdot ${m(hh, 0)}\ \text{m} \approx ${m(sig(e))}\ \text{J} \approx ${m(sig(e / 1000))}\ \text{kJ}§"""),
                background = Bg.energy,
                fact = l("Ein Gummibärchen hat etwa 30 kJ – rein rechnerisch genug für über zehn Stockwerke. Weil Muskeln nur etwa ein Viertel der Energie in Bewegung umsetzen, schafft man in Wirklichkeit eher drei oder vier.", "A gummy bear has about 30 kJ – in theory enough for more than ten floors. Because muscles turn only about a quarter of the energy into motion, in reality it's more like three or four."),
            )
        },
        lab("fallhoehe", Topic.Energy, M, 0.4f) {
            val h = tiered(listOf(5.0, 10.0, 20.0), 3.0, 50.0, 1.0)
            val v = sqrt(2 * G * h)
            task(
                question = l("Ein Apfel fällt aus ${n(h, 0)} m Höhe. Wie schnell ist er unten, wenn man die Luft vernachlässigt?", "An apple falls from a height of ${n(h, 0)} m. How fast is it at the bottom, ignoring air resistance?"),
                steps = listOf(num(l("Aufprallgeschwindigkeit", "Impact speed"), v, "m/s", rel = 0.02, done = """§v \approx ${m(sig(v))}\ \text{m/s} \approx ${m(sig(v * 3.6))}\ \text{km/h}§""",
                    hints = listOf(l("Kernidee: Die ganze Lageenergie wird zu Bewegungsenergie – die Masse kürzt sich weg.", "Key idea: all the potential energy becomes kinetic energy – the mass cancels out."), """§m g h = \tfrac{1}{2} m v^2 \Rightarrow v = \sqrt{2 g h}§"""), formula = Fc.kinetic)),
                solution = listOf("""§v = \sqrt{2 \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \cdot ${m(h, 0)}\ \text{m}} \approx ${m(sig(v))}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = Bg.energy,
            )
        },
        lab("bergsprint", Topic.Energy, H, 0.5f) {
            val mass = tiered(listOf(60.0, 70.0), 50.0, 90.0, 1.0)
            val hh = tiered(listOf(100.0, 200.0, 300.0), 80.0, 600.0, 10.0)
            val min = tiered(listOf(10.0, 15.0, 20.0), 6.0, 40.0, 1.0)
            val p = mass * G * hh / (min * 60)
            val eff = 0.25
            val food = mass * G * hh / eff / 1000
            task(
                question = l("Du (${n(mass, 0)} kg) wanderst in ${n(min, 0)} Minuten ${n(hh, 0)} Höhenmeter hinauf.", "You (${n(mass, 0)} kg) hike ${n(hh, 0)} metres uphill in ${n(min, 0)} minutes."),
                steps = listOf(
                    num(l("Mittlere Leistung nur fürs Heben", "Average power just for lifting"), p, "W", rel = 0.03, done = """§P \approx ${m(sig(p))}\ \text{W}§""",
                        hints = listOf(l("Kernidee: Leistung ist Hubarbeit durch Zeit – Sekunden nicht vergessen.", "Key idea: power is the lifting work divided by time – don't forget seconds."), """§P = \dfrac{m g h}{t}§"""), formula = Fc.power),
                    num(l("Muskeln haben etwa 25 % Wirkungsgrad. Wie viel Nahrungsenergie kostet der Aufstieg?", "Muscles are about 25% efficient. How much food energy does the climb cost?"), food, "kJ", rel = 0.03, done = """§E \approx ${m(sig(food))}\ \text{kJ}§""",
                        hints = listOf(l("Nur ein Viertel der Energie wird zu Hubarbeit, der Rest wird Wärme.", "Only a quarter of the energy becomes lifting work, the rest turns into heat."), """§E = \dfrac{m g h}{0{,}25}§"""), formula = Fc.potential),
                ),
                solution = listOf(
                    """§P = \dfrac{${m(mass, 0)} \cdot 9{,}81 \cdot ${m(hh, 0)}\ \text{J}}{${m(min * 60, 0)}\ \text{s}} \approx ${m(sig(p))}\ \text{W}§""",
                    """§E = \dfrac{${m(sig(mass * G * hh / 1000))}\ \text{kJ}}{0{,}25} \approx ${m(sig(food))}\ \text{kJ}§""",
                ),
                background = Bg.energy,
            )
        },
        lab("radleistung", Topic.Energy, X, 0.6f) {
            val kmh = tiered(listOf(25.0, 30.0, 36.0), 20.0, 45.0, 1.0)
            val a = pick(0.4, 0.5, 0.6)
            val cw = pick(0.8, 0.9, 1.0)
            val v = kmh / 3.6
            val pw = 0.5 * 1.2 * cw * a * v * v * v
            val v2 = Math.cbrt(2 * pw / (1.2 * cw * a * 0.6)) // geduckt: Fläche ×0,6
            task(
                question = l("Eine Radfahrerin fährt ${n(kmh, 0)} km/h. Stirnfläche ${n(a)} m², cw-Wert ${n(cw)}, Luftdichte 1,2 kg/m³. Rollreibung vernachlässigen.", "A cyclist rides at ${n(kmh, 0)} km/h. Frontal area ${n(a)} m², drag coefficient ${n(cw)}, air density 1.2 kg/m³. Ignore rolling resistance."),
                steps = listOf(
                    num(l("Leistung gegen den Luftwiderstand", "Power against air drag"), pw, "W", rel = 0.03, done = """§P \approx ${m(sig(pw))}\ \text{W}§""",
                        hints = listOf(l("Kernidee: Leistung = Kraft mal Geschwindigkeit, und die Luftkraft wächst mit v².", "Key idea: power = force times speed, and the drag force grows with v²."), """§P = \tfrac{1}{2} \rho\, c_w A\, v^3,\ v = ${m(v, 2)}\ \text{m/s}§"""), formula = Fc.dragP),
                    num(l("Sie duckt sich (Fläche nur noch 60 %). Wie schnell wird sie bei gleicher Leistung?", "She crouches (area only 60%). How fast does she go with the same power?"), v2 * 3.6, "km/h", rel = 0.03, done = """§v' \approx ${m(sig(v2 * 3.6))}\ \text{km/h}§""",
                        hints = listOf(l("Gleiche Leistung, kleinere Fläche – die Geschwindigkeit steckt in der dritten Potenz.", "Same power, smaller area – the speed sits in the cube."), """§v' = v \cdot (1/0{,}6)^{1/3}§""")),
                ),
                solution = listOf(
                    """§P = \tfrac{1}{2} \cdot 1{,}2 \cdot ${m(cw)} \cdot ${m(a)} \cdot (${m(v, 2)})^3\ \text{W} \approx ${m(sig(pw))}\ \text{W}§""",
                    """§v' = v \cdot (1/0{,}6)^{1/3} \approx ${m(kmh, 0)} \cdot 1{,}186\ \tfrac{\text{km}}{\text{h}} \approx ${m(sig(v2 * 3.6))}\ \tfrac{\text{km}}{\text{h}}§""",
                ),
                background = Bg.dragPower,
            )
        },
        lab("pumpspeicher", Topic.Energy, H, 0.6f) {
            val vol = tiered(listOf(1.0, 2.0, 5.0), 0.8, 9.0, 0.1) // Mio m³
            val hh = tiered(listOf(300.0, 400.0, 500.0), 200.0, 700.0, 10.0)
            val eta = tiered(listOf(0.8), 0.72, 0.85, 0.01)
            val e = vol * 1e6 * 1000 * G * hh * eta / 3.6e9 // MWh
            val homes = e * 1000 / 3500
            task(
                question = l("Ein Pumpspeicherkraftwerk hat oben ${n(vol)} Mio. m³ Wasser, ${n(hh, 0)} m über den Turbinen. Wirkungsgrad beim Zurückfließen ${n(eta * 100, 0)} %.", "A pumped-storage plant holds ${n(vol)} million m³ of water ${n(hh, 0)} m above its turbines. Efficiency when the water flows back: ${n(eta * 100, 0)}%."),
                steps = listOf(
                    num(l("Wie viel elektrische Energie liefert eine volle Füllung?", "How much electrical energy does one full reservoir deliver?"), e, "MWh", rel = 0.03, done = """§E \approx ${m(sig(e))}\ \text{MWh}§""",
                        hints = listOf(l("Kernidee: Lageenergie des Wassers mal Wirkungsgrad; 1 m³ Wasser wiegt 1000 kg, 1 MWh = 3,6 · 10⁹ J.", "Key idea: potential energy of the water times efficiency; 1 m³ of water has a mass of 1000 kg, 1 MWh = 3.6 · 10⁹ J."), """§E = \eta\, m g h§"""), formula = Fc.potential),
                    num(l("Ein Haushalt braucht etwa 3500 kWh im Jahr. Wie viele Haushalte könnte eine Füllung ein Jahr lang versorgen?", "A household uses about 3500 kWh per year. How many households could one filling supply for a year?"), homes, "", rel = 0.04, done = """§\approx ${m(sig(homes, 2))}§""",
                        hints = listOf(l("Rechne MWh in kWh um und teile.", "Convert MWh to kWh and divide."), """§N = \dfrac{E}{3500\ \text{kWh}}§""")),
                ),
                solution = listOf(
                    """§E = ${m(eta)} \cdot ${sci(vol * 1e9)}\ \text{kg} \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \cdot ${m(hh, 0)}\ \text{m} \approx ${sci(e * 3.6e9)}\ \text{J} \approx ${m(sig(e))}\ \text{MWh}§""",
                    """§N = \dfrac{${m(sig(e * 1000))}\ \text{kWh}}{3500\ \text{kWh}} \approx ${m(sig(homes, 2))}§""",
                ),
                background = Bg.energy,
                fact = l("Pumpspeicher sind die größten Batterien der Welt: Sie speichern Strom, wenn viel Wind und Sonne da ist, und geben ihn abends zurück.", "Pumped storage plants are the world's biggest batteries: they store electricity when there is plenty of wind and sun and give it back in the evening."),
            )
        },
    )

    // ===================================== IMPULS =====================================
    private val momentum = listOf(
        lab("rueckstoss", Topic.Momentum, E, 0.2f) {
            val opts = pick(
                l("Warum fliegt ein losgelassener Luftballon durchs Zimmer?", "Why does a released balloon fly around the room?") to listOf(
                    l("Die Luft strömt nach hinten raus, der Ballon bekommt den gleichen Impuls nach vorn.", "Air rushes out the back, the balloon gets the same momentum forwards."),
                    l("Die Luft im Zimmer schiebt ihn von hinten an.", "The air in the room pushes it from behind."),
                    l("Gummi zieht sich zusammen und will nach vorn.", "The rubber contracts and wants to move forwards."),
                    l("Er wird leichter und steigt deshalb.", "It gets lighter and therefore rises."),
                ),
                l("Wie kann eine Rakete im leeren Weltraum beschleunigen, wo es nichts zum Abstoßen gibt?", "How can a rocket accelerate in empty space where there is nothing to push against?") to listOf(
                    l("Sie stößt Gas nach hinten aus und bekommt dafür Impuls nach vorn.", "It throws gas out the back and gets momentum forwards in return."),
                    l("Gar nicht – im All schweben Raketen nur.", "It can't – in space rockets just drift."),
                    l("Das Gas drückt gegen die Luft hinter der Rakete.", "The gas pushes against the air behind the rocket."),
                    l("Die Schwerkraft der Sonne zieht sie.", "The Sun's gravity pulls it."),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Der Gesamtimpuls bleibt gleich – was nach hinten geht, gleicht etwas nach vorn aus.", "Key idea: total momentum stays the same – whatever goes backwards is balanced by something going forwards."), l("Die Rakete braucht nichts zum Abstoßen – sie stößt ihr eigenes Gas ab.", "The rocket doesn't need anything to push against – it pushes against its own gas.")), formula = Fc.momentum)),
                solution = listOf(l("Vorher ist der Gesamtimpuls null. Strömt Gas mit Impuls nach hinten, muss der Rest gleich viel Impuls nach vorn bekommen.", "Before, the total momentum is zero. When gas flows backwards with some momentum, the rest has to get the same momentum forwards.")),
                background = Bg.momentum,
            )
        },
        lab("kupplung", Topic.Momentum, M, 0.4f) {
            val m1 = tiered(listOf(20.0, 30.0), 15.0, 45.0, 1.0) // t
            val v1 = tiered(listOf(2.0, 3.0), 1.0, 4.0, 0.1)
            val m2 = tiered(listOf(10.0, 20.0), 8.0, 40.0, 1.0)
            val v2 = if (hard) -step(0.5, 2.0, 0.1) else 0.0
            val vNew = (m1 * v1 + m2 * v2) / (m1 + m2)
            task(
                question = if (hard) l("Ein Güterwagen (${n(m1, 0)} t, ${n(v1)} m/s) rollt auf einen zweiten (${n(m2, 0)} t), der ihm mit ${n(-v2)} m/s entgegenkommt. Sie kuppeln beim Zusammenstoß ein.", "A freight car (${n(m1, 0)} t, ${n(v1)} m/s) rolls towards a second one (${n(m2, 0)} t) coming the other way at ${n(-v2)} m/s. They couple on impact.")
                else l("Ein Güterwagen (${n(m1, 0)} t) rollt mit ${n(v1)} m/s auf einen stehenden Wagen (${n(m2, 0)} t) und kuppelt ein.", "A freight car (${n(m1, 0)} t) rolls at ${n(v1)} m/s into a stationary car (${n(m2, 0)} t) and couples with it."),
                steps = listOf(num(l("Gemeinsame Geschwindigkeit danach", "Common speed afterwards"), vNew, "m/s", rel = 0.03, abs = null, done = """§v' \approx ${m(sig(vNew))}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Der Gesamtimpuls bleibt gleich, danach bewegen sich beide als ein Körper.", "Key idea: the total momentum stays the same, afterwards both move as one."), """§v' = \dfrac{m_1 v_1 + m_2 v_2}{m_1 + m_2}§""" + if (hard) l(" (Gegenrichtung negativ)", " (opposite direction negative)") else ""), formula = Fc.momentum)),
                solution = listOf("""§v' = \dfrac{${m(m1, 0)} \cdot ${m(v1)} + ${m(m2, 0)} \cdot (${m(v2)})}{${m(m1 + m2, 0)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vNew))}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = Bg.momentum,
            )
        },
        lab("fussball", Topic.Momentum, M, 0.5f) {
            val mass = 0.43
            val v = tiered(listOf(20.0, 25.0, 30.0), 15.0, 35.0, 0.5)
            val ms = tiered(listOf(10.0, 8.0), 6.0, 12.0, 0.5)
            val f = mass * v / (ms / 1000)
            task(
                question = l("Ein Fußball (430 g) liegt still und wird so geschossen, dass er mit ${n(v)} m/s davonfliegt. Der Fuß berührt ihn ${n(ms)} Millisekunden lang.", "A football (430 g) lies still and is kicked so it flies off at ${n(v)} m/s. The foot touches it for ${n(ms)} milliseconds."),
                steps = listOf(num(l("Mittlere Kraft auf den Ball", "Average force on the ball"), f, "N", rel = 0.03, done = """§\bar F \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Kraft mal Zeit ist die Impulsänderung.", "Key idea: force times time equals the change in momentum."), """§\bar F = \dfrac{m\, \Delta v}{\Delta t},\ \Delta t = ${m(ms / 1000, 4)}\ \text{s}§"""), formula = Fc.impulse)),
                solution = listOf("""§\bar F = \dfrac{0{,}43\ \text{kg} \cdot ${m(v)}\ \text{m/s}}{${m(ms / 1000, 4)}\ \text{s}} \approx ${m(sig(f))}\ \text{N}§"""),
                background = Bg.impulse,
                fact = l("Das entspricht kurzzeitig dem Gewicht von rund ${n(sig(f / G, 2), 0)} kg – deshalb verformt sich der Ball beim Schuss so stark.", "That briefly equals the weight of about ${n(sig(f / G, 2), 0)} kg – that's why the ball deforms so much when it's kicked."),
            )
        },
        lab("billard", Topic.Momentum, H, 0.6f) {
            val m1 = tiered(listOf(0.17), 0.15, 0.2, 0.01)
            val m2 = tiered(listOf(0.17, 0.5, 1.0), 0.1, 1.2, 0.05)
            val v = tiered(listOf(2.0, 3.0), 1.0, 5.0, 0.1)
            val v1p = (m1 - m2) / (m1 + m2) * v
            val v2p = 2 * m1 / (m1 + m2) * v
            task(
                question = l("Eine Kugel (${n(m1 * 1000, 0)} g) trifft mit ${n(v)} m/s zentral und elastisch eine ruhende Kugel (${n(m2 * 1000, 0)} g).", "A ball (${n(m1 * 1000, 0)} g) hits a resting ball (${n(m2 * 1000, 0)} g) head-on and elastically at ${n(v)} m/s."),
                steps = listOf(
                    num(l("Geschwindigkeit der ersten Kugel danach (zurück = negativ)", "Speed of the first ball afterwards (backwards = negative)"), v1p, "m/s", abs = maxOf(0.05, 0.03 * kotlin.math.abs(v1p)), done = """§v_1' \approx ${m(sig(v1p))}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Beim elastischen Stoß bleiben Impuls und Bewegungsenergie erhalten – daraus folgen zwei feste Formeln.", "Key idea: in an elastic collision momentum and kinetic energy are both conserved – that gives two fixed formulas."), """§v_1' = \dfrac{m_1 - m_2}{m_1 + m_2}\, v_1§"""), formula = Fc.elastic),
                    num(l("Geschwindigkeit der zweiten Kugel danach", "Speed of the second ball afterwards"), v2p, "m/s", rel = 0.03, done = """§v_2' \approx ${m(sig(v2p))}\ \text{m/s}§""",
                        hints = listOf(l("Die zweite Formel – oder über den Impuls.", "The second formula – or via momentum."), """§v_2' = \dfrac{2 m_1}{m_1 + m_2}\, v_1§""")),
                ),
                solution = listOf(
                    """§v_1' = \dfrac{${m(m1)} - ${m(m2)}}{${m(m1 + m2)}} \cdot ${m(v)}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v1p))}\ \tfrac{\text{m}}{\text{s}}§""",
                    """§v_2' = \dfrac{2 \cdot ${m(m1)}}{${m(m1 + m2)}} \cdot ${m(v)}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(v2p))}\ \tfrac{\text{m}}{\text{s}}§""",
                ),
                background = Bg.elastic,
            )
        },
        lab("skater", Topic.Momentum, H, 0.4f) {
            val ms = tiered(listOf(50.0, 60.0), 40.0, 85.0, 1.0)
            val mb = tiered(listOf(5.0, 3.0), 1.0, 7.0, 0.5)
            val vb = tiered(listOf(5.0, 8.0), 3.0, 12.0, 0.5)
            val vs = mb * vb / ms
            task(
                question = l("Du stehst auf Rollschuhen (mit dir ${n(ms, 0)} kg) und wirfst einen ${n(mb)}-kg-Medizinball mit ${n(vb)} m/s waagrecht nach vorn.", "You stand on roller skates (${n(ms, 0)} kg including you) and throw a ${n(mb)} kg medicine ball horizontally forwards at ${n(vb)} m/s."),
                steps = listOf(num(l("Mit welcher Geschwindigkeit rollst du rückwärts?", "How fast do you roll backwards?"), vs, "m/s", rel = 0.03, done = """§v \approx ${m(sig(vs))}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Vorher ruht alles, der Gesamtimpuls ist null – das bleibt so.", "Key idea: before, everything is at rest and the total momentum is zero – it stays that way."), """§m_s v_s = m_b v_b§"""), formula = Fc.momentum)),
                solution = listOf("""§v_s = \dfrac{${m(mb)}\ \text{kg} \cdot ${m(vb)}\ \text{m/s}}{${m(ms, 0)}\ \text{kg}} \approx ${m(sig(vs))}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = Bg.momentum,
            )
        },
        lab("pendelgeschoss", Topic.Momentum, X, 0.7f) {
            val mb = tiered(listOf(0.01, 0.02), 0.005, 0.03, 0.001)
            val mk = tiered(listOf(2.0, 4.0), 1.0, 6.0, 0.5)
            val hh = tiered(listOf(0.1, 0.2), 0.05, 0.3, 0.01)
            val vAfter = sqrt(2 * G * hh)
            val vb = (mb + mk) / mb * vAfter
            task(
                question = l("Ein Pfeil (${n(mb * 1000, 0)} g) steckt nach dem Treffer in einem hängenden Holzklotz (${n(mk)} kg). Der Klotz schwingt danach ${n(hh * 100, 0)} cm hoch.", "An arrow (${n(mb * 1000, 0)} g) ends up stuck in a hanging wooden block (${n(mk)} kg). The block then swings up ${n(hh * 100, 0)} cm."),
                steps = listOf(
                    num(l("Geschwindigkeit von Klotz und Pfeil direkt nach dem Treffer", "Speed of block and arrow right after the hit"), vAfter, "m/s", rel = 0.03, done = """§v' \approx ${m(sig(vAfter))}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Nach dem Stoß zählt nur noch Energie: Bewegungsenergie wird Lageenergie.", "Key idea: after the hit only energy matters: kinetic becomes potential energy."), """§v' = \sqrt{2 g h}§"""), formula = Fc.kinetic),
                    num(l("Geschwindigkeit des Pfeils vor dem Treffer", "Speed of the arrow before the hit"), vb, "m/s", rel = 0.03, done = """§v \approx ${m(sig(vb))}\ \text{m/s}§""",
                        hints = listOf(l("Beim Treffer selbst bleibt nur der Impuls erhalten (unelastisch!).", "During the hit itself only momentum is conserved (inelastic!)."), """§m_p v = (m_p + m_K)\, v'§"""), formula = Fc.momentum),
                ),
                solution = listOf(
                    """§v' = \sqrt{2 \cdot 9{,}81 \cdot ${m(hh, 2)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vAfter))}\ \tfrac{\text{m}}{\text{s}}§""",
                    """§v = \dfrac{${m(mb + mk, 3)}}{${m(mb, 3)}} \cdot ${m(sig(vAfter))}\ \tfrac{\text{m}}{\text{s}} \approx ${m(sig(vb))}\ \tfrac{\text{m}}{\text{s}}§""",
                ),
                background = l(
                    "Das ballistische Pendel (Benjamin Robins, 1742) war jahrhundertelang die Methode, um Geschossgeschwindigkeiten zu messen. Der Trick: Der Stoß selbst ist unelastisch, dabei bleibt nur der Impuls erhalten – ein großer Teil der Energie wird zu Wärme. Erst danach, beim Hochschwingen, gilt die Energieerhaltung. Wer beides verwechselt, rechnet völlig falsch.",
                    "The ballistic pendulum (Benjamin Robins, 1742) was for centuries the way to measure projectile speeds. The trick: the collision itself is inelastic, so only momentum is conserved – much of the energy turns into heat. Only afterwards, while swinging up, does energy conservation apply. Mixing the two up gives completely wrong answers.",
                ),
            )
        },
    )
}
