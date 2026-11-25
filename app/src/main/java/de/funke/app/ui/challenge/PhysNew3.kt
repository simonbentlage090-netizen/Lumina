package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/* Physik-Ausbau (seit 13.2): Gravitation, Schwingungen, Druck & Dichte. Alle Texte selbst geschrieben. */

private const val G3 = 9.81
private const val GRAV = 6.674e-11
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object PhysNew3 {
    val all: List<TaskDef> get() = gravity + oscillations + matter

    private val gravity = listOf(
        lab("phy.erdmasse", Topic.Gravity, M, 0.5f) {
            val r = 6.371e6; val mE = G3 * r * r / GRAV
            task(
                question = l("Am Erdboden fällt alles mit 9,81 m/s², der Erdradius ist 6371 km. Wie schwer ist die Erde?", "At the Earth's surface everything falls at 9.81 m/s², and the Earth's radius is 6371 km. What is the mass of the Earth?"),
                steps = listOf(big(l("Masse der Erde in kg", "Mass of the Earth in kg"), mE, "kg", rel = 0.02, done = """§M \approx ${sci(mE)}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Die Fallbeschleunigung ist die Gravitationskraft pro Kilogramm. Dafür gibt es Newtons Gravitationsgesetz.", "Key idea: the acceleration due to gravity is the gravitational force per kilogram. Newton's law of gravitation gives it."), """§g = \dfrac{G M}{R^2} \Rightarrow M = \dfrac{g R^2}{G}§"""))),
                solution = listOf("""§M = \dfrac{9{,}81 \cdot (6{,}371 \cdot 10^6)^2}{6{,}674 \cdot 10^{-11}}\ \text{kg} \approx ${sci(mE)}\ \text{kg}§"""),
                background = l("g kennt jeder, den Erdradius kannte man schon in der Antike. Was fehlte, war die Gravitationskonstante G. Henry Cavendish maß sie 1798 mit Bleikugeln an einem Torsionsfaden, deren winzige Anziehung er beobachtete. Damit konnte man zum ersten Mal die Erde wiegen.", "Everyone knows g, and the Earth's radius was known even in antiquity. What was missing was the gravitational constant G. Henry Cavendish measured it in 1798 with lead balls on a torsion fibre, observing their tiny attraction. With it the Earth could be weighed for the first time."),
                fact = l("Teilt man durch das Volumen, kommt eine mittlere Dichte von 5,5 g/cm³ heraus, doppelt so viel wie Gestein an der Oberfläche. Im Inneren muss also etwas sehr Schweres stecken: der Eisenkern.", "Dividing by the volume gives an average density of 5.5 g/cm³, twice that of surface rock. So something very heavy must be inside: the iron core."),
            )
        },
        lab("phy.gezeiten", Topic.Gravity, M, 0.6f) {
            task(
                question = l("An den meisten Küsten gibt es zweimal am Tag Flut, obwohl der Mond nur einmal am Tag über den Himmel zieht. Woher kommt die zweite Flut auf der mondabgewandten Seite?", "On most coasts there are two high tides a day, although the Moon crosses the sky only once a day. Where does the second high tide, on the side facing away from the Moon, come from?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Der Mond zieht die nahe Seite stärker an als den Erdmittelpunkt und diesen stärker als die ferne Seite. Die Erde wird in die Länge gezogen.", "The Moon pulls the near side harder than the Earth's centre, and the centre harder than the far side. The Earth is stretched out."),
                    listOf(l("Die Sonne erzeugt auf der anderen Seite eine eigene Flut.", "The Sun creates its own high tide on the other side."), l("Die Fliehkraft der Erddrehung schleudert dort Wasser nach außen.", "The centrifugal force of the Earth's spin flings water outwards there."), l("Das Wasser schwappt von der ersten Flut auf die andere Seite.", "The water sloshes over from the first high tide to the other side.")),
                    hints = listOf(l("Kernidee: Die Anziehung des Mondes wird mit dem Abstand schwächer. Was zählt, ist der Unterschied über den Erddurchmesser.", "Key idea: the Moon's pull gets weaker with distance. What matters is the difference across the Earth's diameter."), l("Aus Sicht des Erdmittelpunkts: Die nahe Seite wird zum Mond gezogen, die ferne bleibt zurück.", "From the Earth's centre: the near side is pulled towards the Moon, the far side is left behind.")))),
                solution = listOf(l("Gezeiten entstehen durch den Unterschied der Anziehung. Relativ zum Mittelpunkt wird die nahe Seite zum Mond hin, die ferne vom Mond weg gezogen, also zwei Flutberge.", "Tides come from the difference in pull. Relative to the centre, the near side is pulled towards the Moon and the far side away from it, so there are two tidal bulges.")),
                background = l("Nicht die Anziehung selbst macht die Gezeiten, sondern wie stark sie sich über die Erde hinweg ändert. Diese Gezeitenkraft fällt mit der dritten Potenz des Abstands ab. Deshalb wirkt der nahe Mond stärker als die viel schwerere, aber ferne Sonne. Stehen beide in einer Linie, gibt es Springflut.", "It isn't the pull itself that makes tides but how much it changes across the Earth. This tidal force falls off with the cube of the distance. That's why the nearby Moon has a stronger effect than the much heavier but distant Sun. When both line up, there are spring tides."),
                fact = l("Die Gezeitenreibung bremst die Erde: Ein Tag wird pro Jahrhundert etwa 2 Millisekunden länger, und der Mond entfernt sich jedes Jahr um 3,8 cm.", "Tidal friction is slowing the Earth: a day gets about 2 milliseconds longer per century, and the Moon moves 3.8 cm further away every year."),
            )
        },
        lab("phy.marsjahr", Topic.Gravity, M, 0.4f) {
            val (planet, a) = pick(l("Mars", "Mars") to 1.524, l("Jupiter", "Jupiter") to 5.203, l("Venus", "Venus") to 0.723)
            val t = a.pow(1.5); val days = t * 365.25
            task(
                question = l("$planet kreist in ${n(a, 3)} Astronomischen Einheiten Abstand um die Sonne, die Erde in 1 AE. Wie lang ist ein Jahr dort, in Erdtagen?", "$planet orbits the Sun at a distance of ${n(a, 3)} astronomical units, the Earth at 1 AU. How long is a year there, in Earth days?"),
                steps = listOf(num(l("Umlaufzeit in Tagen", "Orbital period in days"), days, l("Tage", "days"), rel = 0.02, done = """§T \approx ${m(sig(days))}\ \text{d}§""",
                    hints = listOf(l("Kernidee: Drittes Keplersches Gesetz. In Jahren und AE gemessen gilt einfach T² = a³.", "Key idea: Kepler's third law. Measured in years and AU it's simply T² = a³."), """§T = a^{3/2}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }))),
                solution = listOf("""§T = ${m(a, 3)}^{1{,}5} \approx ${m(t, 3)}\ \text{a} \approx ${m(sig(days))}\ \text{d}§"""),
                background = l("Kepler fand um 1619 durch jahrelanges Rechnen mit Tycho Brahes Messdaten: Das Quadrat der Umlaufzeit ist proportional zur dritten Potenz des Bahnradius. Newton zeigte später, dass das aus seinem Gravitationsgesetz folgt. Äußere Planeten sind doppelt langsam: Ihr Weg ist länger, und sie fliegen langsamer.", "Around 1619, after years of calculation with Tycho Brahe's measurements, Kepler found that the square of the orbital period is proportional to the cube of the orbit's radius. Newton later showed that it follows from his law of gravitation. Outer planets are doubly slow: their path is longer, and they move more slowly."),
                fact = l("Auf dem Mars wärst du nach Marsjahren gerechnet nur etwa halb so alt wie auf der Erde.", "Counted in Mars years, you would be only about half as old as on Earth."),
            )
        },
        lab("phy.halley", Topic.Gravity, H, 0.6f) {
            val t = 76.0; val peri = 0.59
            val a = t.pow(2.0 / 3.0); val aph = 2 * a - peri
            task(
                question = l("Der Halleysche Komet kehrt alle 76 Jahre zur Sonne zurück. Am sonnennächsten Punkt ist er nur 0,59 AE von ihr entfernt.", "Halley's Comet returns to the Sun every 76 years. At its closest point it is only 0.59 AU from it."),
                steps = listOf(
                    num(l("Wie groß ist die große Halbachse seiner Bahn (in AE)?", "What is the semi-major axis of its orbit (in AU)?"), a, l("AE", "AU"), rel = 0.02, done = """§a \approx ${m(a, 1)}\ \text{AE}§""".let { l(it, it.replace("AE", "AU")) },
                        hints = listOf(l("Kernidee: Keplers drittes Gesetz gilt auch für Ellipsen, mit der großen Halbachse statt des Radius.", "Key idea: Kepler's third law also holds for ellipses, with the semi-major axis in place of the radius."), """§a = T^{2/3}§""")),
                    num(l("Wie weit entfernt er sich höchstens von der Sonne (in AE)?", "How far does it get from the Sun at most (in AU)?"), aph, l("AE", "AU"), rel = 0.02, done = """§r_{max} \approx ${m(aph, 1)}\ \text{AE}§""".let { l(it, it.replace("AE", "AU")) },
                        hints = listOf(l("Sonnennächster und sonnenfernster Abstand ergeben zusammen die ganze große Achse, also 2a.", "The closest and farthest distances add up to the whole major axis, i.e. 2a."), """§r_{max} = 2a - r_{min}§""")),
                ),
                solution = listOf("""§a = 76^{2/3} \approx ${m(a, 2)}§""", """§r_{max} = 2 \cdot ${m(a, 2)} - 0{,}59 \approx ${m(aph, 1)}§"""),
                background = l("Kometen laufen auf langgestreckten Ellipsen. In Sonnennähe rasen sie, verdampfen teilweise und bilden ihren Schweif, weit draußen kriechen sie jahrzehntelang durch die Kälte. Keplers Gesetz verbindet die Umlaufzeit nur mit der großen Halbachse, egal wie gestreckt die Ellipse ist.", "Comets travel on elongated ellipses. Near the Sun they race along, partly evaporate and form their tails; far out they creep through the cold for decades. Kepler's law links the period only to the semi-major axis, however stretched the ellipse is."),
                fact = l("Das ist weiter draußen als Neptun (30 AE). Edmond Halley sagte 1705 die Rückkehr für 1758 voraus, erlebte sie aber nicht mehr. Das nächste Mal kommt der Komet 2061.", "That's farther out than Neptune (30 AU). In 1705 Edmond Halley predicted the comet's return for 1758 but didn't live to see it. Its next visit is in 2061."),
            )
        },
        lab("phy.schweben", Topic.Gravity, E, 0.5f) {
            task(
                question = l("In der Raumstation ISS, 400 km über der Erde, ist die Schwerkraft noch etwa 90 % so stark wie am Boden. Warum schweben die Astronauten trotzdem?", "On the ISS, 400 km above the Earth, gravity is still about 90% as strong as on the ground. Why do the astronauts float anyway?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Station und Astronauten fallen ständig gemeinsam um die Erde herum.", "The station and the astronauts are constantly falling around the Earth together."),
                    listOf(l("Oberhalb der Atmosphäre gibt es keine Schwerkraft mehr.", "Above the atmosphere there is no gravity any more."), l("Die Station hat Maschinen, die die Schwerkraft ausgleichen.", "The station has machines that cancel out gravity."), l("Mond und Sonne ziehen sie genauso stark nach oben.", "The Moon and the Sun pull them upwards just as hard.")),
                    hints = listOf(l("Kernidee: Wann fühlt man sich auf der Erde schwerelos? In einem fallenden Aufzug.", "Key idea: when do you feel weightless on Earth? In a falling lift."), l("Die ISS fliegt so schnell seitwärts, dass sie beim Fallen immer an der Erde vorbeifällt.", "The ISS moves sideways so fast that as it falls it keeps missing the Earth.")))),
                solution = listOf(l("Die ISS ist im freien Fall. Weil alle gleich schnell fallen, drückt nichts gegen den Boden: Man fühlt kein Gewicht. Die Seitwärtsgeschwindigkeit von 7,7 km/s sorgt dafür, dass sie die Erde nie trifft.", "The ISS is in free fall. Because everything falls equally fast, nothing presses against the floor: you feel no weight. The sideways speed of 7.7 km/s makes sure it never hits the Earth.")),
                background = l("Newton stellte sich eine Kanone auf einem hohen Berg vor: Je schneller die Kugel, desto weiter fliegt sie, bis sie schließlich um die ganze Erde fällt, ohne je anzukommen. Genau das ist eine Umlaufbahn. Gewicht spürt man nur, wenn etwas einen am Fallen hindert.", "Newton imagined a cannon on a high mountain: the faster the ball, the farther it flies, until in the end it falls all the way round the Earth without ever landing. That is exactly what an orbit is. You only feel weight when something stops you from falling."),
                fact = l("Für Trainingsflüge fliegen Flugzeuge Parabeln: Etwa 20 Sekunden lang fallen sie mit den Insassen frei, die dann schweben.", "For training, planes fly parabolas: for about 20 seconds they fall freely along with the people inside, who then float."),
            )
        },
        lab("phy.neutronenstern", Topic.Gravity, H, 0.5f) {
            val mass = 1.4 * 1.989e30; val r = pick(10.0, 12.0) * 1000
            val g = GRAV * mass / (r * r); val v1 = sqrt(2 * g * 1.0)
            task(
                question = l("Ein Neutronenstern hat 1,4 Sonnenmassen (1 Sonnenmasse = 2,0 · 10³⁰ kg), aber nur ${n(r / 1000, 0)} km Radius. Wie groß ist die Fallbeschleunigung an seiner Oberfläche?", "A neutron star has 1.4 solar masses (1 solar mass = 2.0 · 10³⁰ kg) but a radius of only ${n(r / 1000, 0)} km. How large is the acceleration due to gravity at its surface?"),
                steps = listOf(big(l("Fallbeschleunigung in m/s²", "Gravitational acceleration in m/s²"), g, "m/s²", rel = 0.03, done = """§g \approx ${sci(g)}\ \text{m/s}^2§""",
                    hints = listOf(l("Kernidee: Dasselbe Gesetz wie auf der Erde, nur mit riesiger Masse und winzigem Radius.", "Key idea: the same law as on Earth, only with a huge mass and a tiny radius."), """§g = \dfrac{G M}{R^2}§"""))),
                solution = listOf("""§g = \dfrac{6{,}674 \cdot 10^{-11} \cdot ${sci(mass)}}{(${sci(r)})^2}\ \tfrac{\text{m}}{\text{s}^2} \approx ${sci(g)}\ \tfrac{\text{m}}{\text{s}^2}§"""),
                background = l("Ein Neutronenstern ist der ausgebrannte Kern eines großen Sterns nach einer Supernova. Die Schwerkraft hat Elektronen und Protonen zu Neutronen zusammengedrückt, und eine Masse größer als die Sonne steckt in einer Kugel von der Größe einer Stadt. Weil g mit 1/R² wächst, wird die Schwerkraft an der Oberfläche unvorstellbar groß.", "A neutron star is the burnt-out core of a large star after a supernova. Gravity has squeezed electrons and protons together into neutrons, and more mass than the Sun sits in a ball the size of a city. Because g grows as 1/R², gravity at the surface becomes unimaginably strong."),
                fact = l("Ein Gegenstand, der dort aus 1 m Höhe fällt, schlägt mit etwa ${n(v1 / 1000, 0)} km/s auf. Berge können auf Neutronensternen höchstens Millimeter hoch sein.", "An object falling from 1 m there hits at about ${n(v1 / 1000, 0)} km/s. Mountains on neutron stars can be at most millimetres high."),
            )
        },
        lab("phy.sonnenmasse", Topic.Gravity, H, 0.5f) {
            val r = 1.496e11; val t = 365.25 * 86400
            val ms = 4 * PI * PI * r * r * r / (GRAV * t * t)
            task(
                question = l("Die Erde umkreist die Sonne in 365,25 Tagen auf einer fast kreisförmigen Bahn mit 1,496 · 10¹¹ m Radius. Wie schwer ist die Sonne?", "The Earth orbits the Sun in 365.25 days on an almost circular orbit with a radius of 1.496 · 10¹¹ m. What is the mass of the Sun?"),
                steps = listOf(big(l("Sonnenmasse in kg", "Mass of the Sun in kg"), ms, "kg", rel = 0.02, done = """§M \approx ${sci(ms)}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Die Gravitation der Sonne liefert genau die Zentripetalkraft für die Kreisbahn der Erde. Die Erdmasse kürzt sich dabei heraus.", "Key idea: the Sun's gravity provides exactly the centripetal force for the Earth's circular orbit. The Earth's mass cancels out."), """§\dfrac{G M m}{r^2} = m \dfrac{4\pi^2 r}{T^2} \Rightarrow M = \dfrac{4\pi^2 r^3}{G T^2}§"""))),
                solution = listOf("""§T = 365{,}25 \cdot 86\,400\ \text{s} \approx ${sci(t)}\ \text{s}§""", """§M = \dfrac{4\pi^2 \cdot (1{,}496 \cdot 10^{11})^3}{6{,}674 \cdot 10^{-11} \cdot (${sci(t)})^2}\ \text{kg} \approx ${sci(ms)}\ \text{kg}§"""),
                background = l("Weil sich die Masse des umlaufenden Körpers herauskürzt, verrät jede Umlaufbahn die Masse dessen, was in der Mitte sitzt. So wiegt man Planeten über ihre Monde, Sterne über ihre Planeten und sogar das Schwarze Loch im Zentrum der Milchstraße über die Sterne, die es umkreisen.", "Because the mass of the orbiting body cancels, every orbit reveals the mass of whatever sits at the centre. That's how planets are weighed by their moons, stars by their planets, and even the black hole at the centre of the Milky Way by the stars orbiting it."),
                fact = l("Die Sonne vereint 99,86 % der Masse des ganzen Sonnensystems. Alle Planeten zusammen sind fast nur Krümel.", "The Sun contains 99.86% of the mass of the whole solar system. All the planets together are hardly more than crumbs."),
            )
        },
        lab("phy.hohmann", Topic.Gravity, X, 0.6f) {
            val r2 = 1.524; val a = (1 + r2) / 2; val days = 0.5 * a.pow(1.5) * 365.25
            task(
                question = l("Eine Sonde fliegt auf der sparsamsten Bahn von der Erde (1 AE) zum Mars (1,52 AE): einer halben Ellipse, die die Erdbahn innen und die Marsbahn außen gerade berührt. Wie lange dauert der Flug?", "A probe flies to Mars (1.52 AU) from the Earth (1 AU) on the most economical path: half an ellipse that just touches the Earth's orbit on the inside and the orbit of Mars on the outside. How long does the flight take?"),
                steps = listOf(num(l("Flugdauer in Tagen", "Flight time in days"), days, l("Tage", "days"), rel = 0.02, done = """§t \approx ${m(days, 0)}\ \text{d}§""",
                    hints = listOf(l("Kernidee: Die große Achse der Ellipse reicht von 1 AE bis 1,52 AE. Mit Kepler bekommst du die volle Umlaufzeit, geflogen wird die Hälfte.", "Key idea: the major axis of the ellipse runs from 1 AU to 1.52 AU. Kepler gives you the full period; the flight is half of it."), """§a = \dfrac{1 + 1{,}52}{2},\quad t = \tfrac{1}{2} a^{3/2}\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }))),
                solution = listOf("""§a = \dfrac{1 + 1{,}524}{2} = ${m(a, 3)}\ \text{AE}§""".let { l(it, it.replace("AE", "AU")) }, """§t = \tfrac{1}{2} \cdot ${m(a, 3)}^{1{,}5} \cdot 365{,}25\ \text{d} \approx ${m(days, 0)}\ \text{d}§"""),
                background = l("Im All fliegt man nicht geradeaus, sondern auf Umlaufbahnen um die Sonne. Die Hohmann-Bahn (1925) braucht nur zwei kurze Triebwerksstöße: einen zum Verlassen der Erdbahn, einen zum Einschwenken in die Marsbahn. Dafür muss der Mars zur richtigen Zeit am richtigen Ort sein, und so ein Startfenster öffnet sich nur alle 26 Monate.", "In space you don't fly in straight lines but on orbits around the Sun. The Hohmann orbit (1925) needs only two short engine burns: one to leave the Earth's orbit and one to settle into the orbit of Mars. But Mars has to be in the right place at the right time, and such a launch window only opens every 26 months."),
                fact = l("Deshalb starten Marsmissionen verschiedener Länder oft im selben Sommer, wie 2020 Perseverance, die chinesische Tianwen-1 und die Hope-Sonde der Emirate.", "That's why Mars missions from different countries often launch in the same summer, like Perseverance, China's Tianwen-1 and the Emirates' Hope probe in 2020."),
            )
        },
        lab("phy.gezeitenverh", Topic.Gravity, X, 0.6f) {
            val mm = 7.35e22; val dm = 3.844e8; val ms = 1.989e30; val ds = 1.496e11
            val ratio = (mm / (dm * dm * dm)) / (ms / (ds * ds * ds)); val pull = (ms / (ds * ds)) / (mm / (dm * dm))
            task(
                question = l("Die Sonne zieht die Erde etwa ${n(pull, 0)}-mal stärker an als der Mond. Trotzdem macht der Mond die stärkeren Gezeiten. Die Gezeitenkraft ist proportional zu M/d³. Wie viel mal so stark sind die Gezeiten des Mondes wie die der Sonne?", "The Sun pulls on the Earth about ${n(pull, 0)} times harder than the Moon does. Yet the Moon produces the stronger tides. The tidal force is proportional to M/d³. How many times as strong are the Moon's tides as the Sun's?"),
                given = listOf(l("""Mond: §7{,}35 \cdot 10^{22}\ \text{kg}§, §3{,}844 \cdot 10^8\ \text{m}§. Sonne: §1{,}989 \cdot 10^{30}\ \text{kg}§, §1{,}496 \cdot 10^{11}\ \text{m}§""", """Moon: §7{,}35 \cdot 10^{22}\ \text{kg}§, §3{,}844 \cdot 10^8\ \text{m}§. Sun: §1{,}989 \cdot 10^{30}\ \text{kg}§, §1{,}496 \cdot 10^{11}\ \text{m}§""")),
                steps = listOf(num(l("Verhältnis Mond zu Sonne", "Ratio of Moon to Sun"), ratio, "", rel = 0.03, done = """§\approx ${m(ratio, 2)}§""",
                    hints = listOf(l("Kernidee: Die Gezeitenkraft ist der Unterschied der Anziehung über die Erde. Der wird mit dem Abstand noch schneller kleiner als die Anziehung selbst.", "Key idea: the tidal force is the difference in pull across the Earth. It falls off even faster with distance than the pull itself."), """§\dfrac{M_M / d_M^3}{M_S / d_S^3}§"""))),
                solution = listOf("""§\dfrac{7{,}35 \cdot 10^{22} / (3{,}844 \cdot 10^8)^3}{1{,}989 \cdot 10^{30} / (1{,}496 \cdot 10^{11})^3} \approx ${m(ratio, 2)}§"""),
                background = l("Die Anziehung fällt mit 1/d², ihr Unterschied zwischen Vorder- und Rückseite der Erde mit 1/d³. Der Mond ist fast 400-mal näher als die Sonne, das macht in der dritten Potenz viel mehr aus als ihre 27-millionenfache Masse. Stehen Sonne und Mond in einer Linie (Neu- und Vollmond), addieren sich die Gezeiten zur Springflut.", "The pull falls off as 1/d², its difference between the front and back of the Earth as 1/d³. The Moon is almost 400 times closer than the Sun, which counts for far more when cubed than the Sun's 27-million-fold mass. When Sun and Moon line up (new and full moon), the tides add up to spring tides."),
                fact = l("Ein Zufall des Sonnensystems: Sonne und Mond erscheinen fast gleich groß am Himmel. Daher die perfekten totalen Sonnenfinsternisse.", "A coincidence of the solar system: the Sun and the Moon appear almost the same size in the sky. Hence the perfect total solar eclipses."),
            )
        },
        lab("phy.erdtunnel", Topic.Gravity, X, 0.7f) {
            val r = 6.371e6; val t = PI * sqrt(r / G3) / 60
            task(
                question = l("Man bohrt einen geraden Tunnel durch den Erdmittelpunkt bis auf die andere Seite und lässt eine Kugel hineinfallen (keine Reibung, keine Luft, Erde gleichmäßig dicht). Wie lange dauert es, bis sie auf der anderen Seite ankommt?", "A straight tunnel is drilled through the centre of the Earth to the other side and a ball is dropped in (no friction, no air, Earth of uniform density). How long does it take to arrive on the other side?"),
                steps = listOf(num(l("Zeit in Minuten", "Time in minutes"), t, "min", rel = 0.02, done = """§t \approx ${m(t, 1)}\ \text{min}§""",
                    hints = listOf(l("Kernidee: Im Inneren zieht nur die Masse unter dir. Die Kraft ist proportional zum Abstand von der Mitte, wie bei einer Feder. Die Kugel schwingt also harmonisch.", "Key idea: inside, only the mass below you pulls. The force is proportional to the distance from the centre, like a spring. So the ball oscillates harmonically."), """§F = -\dfrac{m g}{R}\, r \Rightarrow t = \tfrac{1}{2} T = \pi \sqrt{\dfrac{R}{g}}§"""))),
                solution = listOf("""§t = \pi \sqrt{\dfrac{6{,}371 \cdot 10^6}{9{,}81}}\ \text{s} \approx ${m(t * 60, 0)}\ \text{s} \approx ${m(t, 1)}\ \text{min}§"""),
                background = l("Unter der Oberfläche zieht dich die Erdschale über dir in alle Richtungen gleich stark, ihre Wirkung hebt sich auf. Übrig bleibt eine Kraft, die genau mit dem Abstand zur Mitte wächst, wie eine Feder. Die Kugel pendelt also ewig hin und her. Erstaunlich: Jeder gerade Tunnel zwischen zwei beliebigen Orten dauert gleich lang.", "Below the surface, the shell of Earth above you pulls equally in all directions and its effect cancels. What's left is a force that grows exactly with the distance from the centre, like a spring. So the ball swings back and forth forever. Amazingly, any straight tunnel between two places takes the same time."),
                fact = l("Das ist genau die halbe Umlaufzeit eines Satelliten, der knapp über dem Boden kreisen würde.", "That's exactly half the orbital period of a satellite skimming just above the ground."),
            )
        },
        lab("phy.aequator", Topic.Gravity, M, 0.6f) {
            val w = 2 * PI / (23.93 * 3600); val a = w * w * 6.371e6; val pct = a / G3 * 100
            task(
                question = l("Am Äquator dreht dich die Erde mit 1670 km/h im Kreis. Ein Teil der Schwerkraft wird dafür gebraucht, dich auf dieser Kreisbahn zu halten. Um wie viel Prozent wiegst du deshalb am Äquator weniger als am Pol (nur dieser Effekt)?", "At the equator the Earth carries you round in a circle at 1670 km/h. Part of gravity is needed just to keep you on that circle. By what percentage do you therefore weigh less at the equator than at the pole (only this effect)?"),
                steps = listOf(num(l("Gewichtsverlust in Prozent", "Weight loss in percent"), pct, "%", rel = 0.03, done = """§\approx ${m(pct, 2)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Für die Kreisbahn braucht man die Zentripetalbeschleunigung ω²·R. Um die ist die Waage leichter.", "Key idea: the circle needs the centripetal acceleration ω²·R. The scale reads lighter by that much."), """§\omega = \dfrac{2\pi}{23{,}93\ \text{h}},\quad a = \omega^2 R§"""))),
                solution = listOf("""§\omega \approx ${sci(w, 3)}\ \text{s}^{-1},\quad a = \omega^2 \cdot 6{,}371 \cdot 10^6\ \text{m} \approx ${m(a, 4)}\ \tfrac{\text{m}}{\text{s}^2}§""", """§\dfrac{${m(a, 4)}}{9{,}81} \approx ${m(pct, 2)}\ \text{%}§"""),
                background = l("Eine Waage misst nicht die Gravitation selbst, sondern wie stark sie dich hochdrücken muss. Auf einer Kreisbahn muss ein kleiner Teil der Gravitation dich zur Mitte ziehen, und der fehlt dann auf der Waage. Dazu kommt, dass die Erde am Äquator etwas dicker ist: Man ist dort weiter vom Mittelpunkt entfernt. Beides zusammen ergibt etwa 0,5 %.", "A scale doesn't measure gravity itself but how hard it has to push you up. On a circular path a small part of gravity has to pull you towards the centre, and that part is missing on the scale. On top of that, the Earth bulges a little at the equator, so you're farther from the centre there. Together this gives about 0.5%."),
                fact = l("Die Abplattung entstand selbst durch die Drehung: Der Äquatordurchmesser ist 43 km größer als der Poldurchmesser.", "The flattening itself came from the spin: the equatorial diameter is 43 km larger than the polar diameter."),
            )
        },
        lab("phy.neutralpunkt", Topic.Gravity, H, 0.5f) {
            val d = 384400.0; val k = sqrt(81.3); val x = d * k / (1 + k)
            task(
                question = l("Zwischen Erde und Mond gibt es einen Punkt, an dem sich ihre Anziehungskräfte genau aufheben. Die Erde ist 81,3-mal so schwer wie der Mond, der Abstand ist 384.400 km. Wie weit ist dieser Punkt vom Erdmittelpunkt entfernt?", "Between the Earth and the Moon there is a point where their gravitational pulls exactly cancel. The Earth is 81.3 times as massive as the Moon, and the distance is 384,400 km. How far is this point from the Earth's centre?"),
                steps = listOf(num(l("Abstand von der Erde in km", "Distance from the Earth in km"), x, "km", rel = 0.01, done = """§x \approx ${m(sig(x))}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Gleich große Anziehung, und die fällt mit dem Quadrat des Abstands. Also verhalten sich die Abstände wie die Wurzeln der Massen.", "Key idea: equal pulls, and they fall off with the square of the distance. So the distances are in the ratio of the square roots of the masses."), """§\dfrac{M_E}{x^2} = \dfrac{M_M}{(d - x)^2} \Rightarrow \dfrac{x}{d - x} = \sqrt{81{,}3}§"""))),
                solution = listOf("""§\dfrac{x}{d - x} = \sqrt{81{,}3} \approx ${m(k, 3)}§""", """§x = \dfrac{${m(k, 3)}}{1 + ${m(k, 3)}} \cdot 384\,400\ \text{km} \approx ${m(sig(x))}\ \text{km}§"""),
                background = l("Weil die Anziehung mit dem Quadrat des Abstands abnimmt, liegt der Gleichgewichtspunkt nicht dort, wo man die Massen teilen würde, sondern viel näher am Mond: Ein Faktor 81 bei der Masse ist nur ein Faktor 9 beim Abstand. Für die Raumfahrt ist das die Grenze, ab der eine Sonde zum Mond hin zu fallen beginnt.", "Because the pull decreases with the square of the distance, the balance point isn't where you'd split by mass but much closer to the Moon: a factor of 81 in mass is only a factor of 9 in distance. For spaceflight this is the boundary beyond which a probe starts falling towards the Moon."),
                fact = l("Die Apollo-Kapseln wurden bis zu diesem Punkt immer langsamer und danach vom Mond wieder beschleunigt.", "The Apollo capsules kept slowing down until this point and were then accelerated again by the Moon."),
            )
        },
        lab("phy.apfelerde", Topic.Gravity, E, 0.4f) {
            task(
                question = l("Ein Apfel fällt vom Baum, weil die Erde ihn anzieht. Zieht der Apfel auch die Erde an?", "An apple falls from the tree because the Earth pulls it. Does the apple also pull on the Earth?"),
                steps = listOf(pick1(l("Was stimmt?", "What is true?"),
                    l("Ja, genauso stark. Die Erde bewegt sich aber wegen ihrer riesigen Masse nur unmessbar wenig.", "Yes, just as hard. But because of its huge mass the Earth moves only an immeasurably tiny amount."),
                    listOf(l("Nein, nur schwere Körper können anziehen.", "No, only heavy objects can attract."), l("Ja, aber viel schwächer, als die Erde den Apfel anzieht.", "Yes, but much more weakly than the Earth pulls the apple."), l("Nur solange der Apfel noch am Baum hängt.", "Only while the apple is still hanging on the tree.")),
                    hints = listOf(l("Kernidee: Newtons drittes Gesetz: Jede Kraft hat eine gleich große Gegenkraft.", "Key idea: Newton's third law: every force has an equal and opposite force."), l("Gleiche Kraft, aber wie stark beschleunigt sie eine riesige Masse?", "Equal force, but how strongly does it accelerate a huge mass?")))),
                solution = listOf(l("Apfel und Erde ziehen sich mit genau derselben Kraft an, etwa 1 N. Für den Apfel bedeutet das 9,81 m/s², für die Erde etwa 10⁻²⁵ m/s².", "Apple and Earth pull on each other with exactly the same force, about 1 N. For the apple that means 9.81 m/s², for the Earth about 10⁻²⁵ m/s².")),
                background = l("Gravitation ist immer gegenseitig. Die Kraft hängt vom Produkt beider Massen ab, also ist sie für beide gleich. Wie stark sich etwas dadurch bewegt, hängt aber von der eigenen Masse ab: Die Erde ist rund 10²⁵-mal schwerer als ein Apfel und bewegt sich entsprechend weniger.", "Gravity is always mutual. The force depends on the product of the two masses, so it is the same for both. But how much something moves as a result depends on its own mass: the Earth is about 10²⁵ times heavier than an apple and moves correspondingly less."),
                fact = l("Ob die Geschichte mit Newtons Apfel stimmt, ist unklar, aber Newton erzählte sie selbst im Alter.", "Whether the story of Newton's apple is true is unclear, but Newton himself told it in old age."),
            )
        },
    )

    private val oscillations = listOf(
        lab("phy.federvier", Topic.Oscillations, E, 0.5f) {
            task(
                question = l("Eine Masse schwingt an einer Feder auf und ab. Du ersetzt sie durch eine viermal so schwere Masse. Was passiert mit der Dauer einer Schwingung?", "A mass bounces up and down on a spring. You replace it with a mass four times as heavy. What happens to the time of one oscillation?"),
                steps = listOf(pick1(l("Die Schwingungsdauer …", "The period …"), l("verdoppelt sich", "doubles"), listOf(l("vervierfacht sich", "quadruples"), l("bleibt gleich", "stays the same"), l("halbiert sich", "halves")),
                    hints = listOf(l("Kernidee: Schwerer heißt träger, aber nicht im gleichen Verhältnis langsamer.", "Key idea: heavier means more sluggish, but not slower in the same proportion."), """§T = 2\pi \sqrt{\dfrac{m}{D}}§"""))),
                solution = listOf("""§T \propto \sqrt{m} \Rightarrow \sqrt{4} = 2§"""),
                background = l("Eine schwerere Masse wird von der Feder genauso stark zurückgezogen, kommt aber langsamer in Gang. Die Schwingungsdauer wächst deshalb nur mit der Wurzel der Masse. Erstaunlich ist, dass sie gar nicht von der Größe des Ausschlags abhängt: Weit oder knapp schwingend, ein Hin und Her dauert gleich lang.", "A heavier mass is pulled back by the spring just as hard but gets going more slowly. That's why the period grows only with the square root of the mass. The surprising part is that it doesn't depend at all on the size of the swing: wide or narrow, one back-and-forth takes the same time."),
                fact = l("Auf der ISS wiegen sich Astronauten mit einem Federstuhl: Aus der Schwingungsdauer berechnet man ihre Masse, denn eine normale Waage zeigt dort nichts.", "On the ISS astronauts weigh themselves on a spring chair: their mass is worked out from the period, because a normal scale shows nothing up there."),
            )
        },
        lab("phy.quarz", Topic.Oscillations, E, 0.4f) {
            val f = 32768.0; val k = (ln(f) / ln(2.0))
            task(
                question = l("Der Quarz in einer Armbanduhr schwingt mit 32.768 Hz. Ein Zählerbaustein kann eine Frequenz immer nur halbieren. Wie viele solche Halbierungen braucht man, um auf genau 1 Hz zu kommen, einen Takt pro Sekunde?", "The quartz crystal in a wristwatch vibrates at 32,768 Hz. A counter chip can only ever halve a frequency. How many such halvings are needed to get exactly 1 Hz, one tick per second?"),
                steps = listOf(num(l("Anzahl Halbierungen", "Number of halvings"), k, "", abs = 0.0, done = """§2^{15} = 32\,768§""",
                    hints = listOf(l("Kernidee: Welche Zweierpotenz ist 32.768?", "Key idea: which power of two is 32,768?"), """§2^{10} = 1024§"""))),
                solution = listOf("""§32\,768 = 2^{15} \Rightarrow 15\ \text{Halbierungen}§""".let { l(it, it.replace("Halbierungen", "halvings")) }),
                background = l("Ein Quarzkristall verformt sich, wenn man Spannung anlegt, und erzeugt Spannung, wenn er sich verformt. So schwingt er in einer Schaltung sehr genau auf seiner Eigenfrequenz. Die Uhrmacher wählten 2¹⁵ Hz, weil man das mit einfachen Kippstufen auf 1 Hz herunterteilen kann, und weil es knapp über dem Hörbaren liegt.", "A quartz crystal deforms when a voltage is applied and produces a voltage when it is deformed. That lets it vibrate very precisely at its natural frequency in a circuit. Watchmakers chose 2¹⁵ Hz because it can be divided down to 1 Hz with simple flip-flops, and because it lies just above the range of hearing."),
                fact = l("Eine gute Quarzuhr weicht im Monat nur um wenige Sekunden ab, eine mechanische Uhr oft um Minuten.", "A good quartz watch drifts by only a few seconds a month, a mechanical watch often by minutes."),
            )
        },
        lab("phy.daempfer", Topic.Oscillations, M, 0.5f) {
            val f = pick(0.15, 0.2, 0.1); val lp = G3 / (4 * PI * PI * f * f)
            task(
                question = l("Ein Hochhaus schwankt im Wind mit einer Eigenfrequenz von ${n(f, 2)} Hz. Oben soll ein schweres Pendel als Schwingungsdämpfer hängen, das mit genau derselben Frequenz schwingt. Wie lang muss es sein?", "A skyscraper sways in the wind at a natural frequency of ${n(f, 2)} Hz. A heavy pendulum is to hang at the top as a vibration damper, swinging at exactly the same frequency. How long must it be?"),
                steps = listOf(num(l("Pendellänge in Metern", "Pendulum length in metres"), lp, "m", rel = 0.02, done = """§L \approx ${m(lp, 1)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Die Pendelformel nach der Länge umstellen.", "Key idea: rearrange the pendulum formula for the length."), """§f = \dfrac{1}{2\pi}\sqrt{\dfrac{g}{L}} \Rightarrow L = \dfrac{g}{(2\pi f)^2}§"""))),
                solution = listOf("""§L = \dfrac{9{,}81}{(2\pi \cdot ${m(f, 2)})^2}\ \text{m} \approx ${m(lp, 1)}\ \text{m}§"""),
                background = l("Schwingt das Gebäude, schwingt das abgestimmte Pendel mit derselben Frequenz, aber gegenläufig, und zieht immer gegen die Bewegung. So nimmt es dem Turm Energie weg, die dann in Dämpfern zu Wärme wird. Das funktioniert nur, wenn beide Frequenzen genau zusammenpassen.", "When the building sways, the tuned pendulum swings at the same frequency but in the opposite direction, always pulling against the motion. That takes energy away from the tower, which then turns into heat in dampers. It only works if both frequencies match exactly."),
                fact = l("Im Taipei 101 hängt eine goldene Stahlkugel von 660 Tonnen. Besucher können sie bei Taifun sichtbar schwingen sehen.", "In Taipei 101 there hangs a golden steel ball weighing 660 tonnes. Visitors can see it visibly swing during typhoons."),
            )
        },
        lab("phy.trampolin", Topic.Oscillations, M, 0.5f) {
            val mass = pick(50.0, 60.0, 70.0); val dx = pick(0.15, 0.2, 0.25)
            val k = mass * G3 / dx; val t = 2 * PI * sqrt(mass / k)
            task(
                question = l("Du (${n(mass, 0)} kg) stellst dich ruhig auf ein Trampolin, und das Tuch sinkt ${n(dx * 100, 0)} cm ein. Wir tun so, als wäre es eine einfache Feder.", "You (${n(mass, 0)} kg) stand still on a trampoline, and the bed sinks ${n(dx * 100, 0)} cm. We pretend it is a simple spring."),
                steps = listOf(
                    num(l("Wie groß ist seine Federkonstante (in N/m)?", "What is its spring constant (in N/m)?"), k, "N/m", rel = 0.02, done = """§D \approx ${m(sig(k))}\ \text{N/m}§""",
                        hints = listOf(l("Kernidee: Im Ruhezustand trägt die Feder genau dein Gewicht.", "Key idea: at rest the spring carries exactly your weight."), """§D = \dfrac{m g}{x}§""")),
                    num(l("Mit welcher Periode würdest du auf und ab wippen, ohne den Kontakt zu verlieren?", "With what period would you bob up and down without losing contact?"), t, "s", rel = 0.02, done = """§T \approx ${m(t, 2)}\ \text{s}§""",
                        hints = listOf(l("Federschwinger: Masse und Federkonstante bestimmen die Periode.", "Spring oscillator: mass and spring constant set the period."), """§T = 2\pi \sqrt{\dfrac{m}{D}}§""")),
                ),
                solution = listOf("""§D = \dfrac{${m(mass, 0)} \cdot 9{,}81}{${m(dx)}}\ \tfrac{\text{N}}{\text{m}} \approx ${m(sig(k))}\ \tfrac{\text{N}}{\text{m}}§""", """§T = 2\pi \sqrt{\dfrac{${m(mass, 0)}}{${m(sig(k))}}}\ \text{s} \approx ${m(t, 2)}\ \text{s}§"""),
                background = l("Hier steckt eine schöne Abkürzung: Setzt man D = m·g/x in die Periode ein, kürzt sich die Masse heraus, und es bleibt T = 2π·√(x/g). Die Periode hängt nur davon ab, wie weit das Gewicht die Feder zusammendrückt. Das ist genau die Formel eines Pendels mit der Länge x.", "There's a neat shortcut here: substitute k = m·g/x into the period and the mass cancels, leaving T = 2π·√(x/g). The period depends only on how far the weight compresses the spring. That's exactly the formula for a pendulum of length x."),
                fact = l("Beim Springen verlässt man das Tuch, dann ist es keine reine Federschwingung mehr: Die Flugphase dauert viel länger als der Kontakt.", "When jumping you leave the bed, and then it's no longer a pure spring oscillation: the flight phase lasts much longer than the contact."),
            )
        },
        lab("phy.daempfung", Topic.Oscillations, M, 0.6f) {
            val q = pick(0.9, 0.95, 0.8); val nSwing = ceil(ln(0.5) / ln(q))
            task(
                question = l("Eine Schaukel verliert bei jeder Schwingung ${n((1 - q) * 100, 0)} % ihres Ausschlags. Nach wie vielen Schwingungen ist er zum ersten Mal kleiner als die Hälfte?", "A swing loses ${n((1 - q) * 100, 0)}% of its amplitude with every swing. After how many swings is it smaller than half for the first time?"),
                steps = listOf(num(l("Anzahl Schwingungen", "Number of swings"), nSwing, "", abs = 0.0, done = """§n = ${m(nSwing, 0)}§""",
                    hints = listOf(l("Kernidee: Prozentuale Abnahme heißt Malnehmen mit demselben Faktor, also eine Potenz.", "Key idea: a percentage decrease means multiplying by the same factor each time, so a power."), """§${m(q)}^n < 0{,}5 \Rightarrow n > \dfrac{\ln 0{,}5}{\ln ${m(q)}}§"""))),
                solution = listOf("""§n > \dfrac{\ln 0{,}5}{\ln ${m(q)}} \approx ${m(ln(0.5) / ln(q), 2)} \Rightarrow n = ${m(nSwing, 0)}§"""),
                background = l("Reibung nimmt einer Schwingung bei jedem Hin und Her einen festen Bruchteil ihrer Energie. Der Ausschlag schrumpft deshalb nicht gleichmäßig, sondern exponentiell: erst schnell, dann immer langsamer, ohne je ganz null zu werden. Dieselbe Mathematik beschreibt radioaktiven Zerfall und abkühlenden Kaffee.", "Friction takes a fixed fraction of an oscillation's energy with every back-and-forth. So the amplitude doesn't shrink evenly but exponentially: quickly at first, then more and more slowly, never quite reaching zero. The same mathematics describes radioactive decay and cooling coffee."),
                fact = l("Die Güte einer Stimmgabel ist so hoch, dass sie tausende Schwingungen braucht, um die Hälfte zu verlieren. Deshalb klingt sie so lange nach.", "A tuning fork's quality factor is so high that it takes thousands of vibrations to lose half its amplitude. That's why it rings for so long."),
            )
        },
        lab("phy.gebaeudeschwingung", Topic.Oscillations, M, 0.6f) {
            val fq = pick(1.0, 2.0, 0.5); val floors = 10 / fq
            task(
                question = l("Faustregel für Bauingenieure: Ein Gebäude mit N Stockwerken hat eine Eigenschwingungsdauer von etwa 0,1·N Sekunden. Ein Erdbeben schüttelt den Boden vor allem mit ${n(fq, 1)} Hz. Welche Häuser sind am stärksten gefährdet?", "A rule of thumb for engineers: a building with N storeys has a natural period of about 0.1·N seconds. An earthquake shakes the ground mainly at ${n(fq, 1)} Hz. Which buildings are most at risk?"),
                steps = listOf(num(l("Anzahl der Stockwerke", "Number of storeys"), floors, "", abs = 0.5, done = """§N \approx ${m(floors, 0)}§""",
                    hints = listOf(l("Kernidee: Resonanz. Gefährlich wird es, wenn die Eigenschwingung des Hauses zum Takt des Bodens passt.", "Key idea: resonance. It gets dangerous when the building's natural vibration matches the beat of the ground."), """§T = \dfrac{1}{f} = 0{,}1 \cdot N§"""))),
                solution = listOf("""§T = \dfrac{1}{${m(fq, 1)}\ \text{Hz}} = ${m(1 / fq, 1)}\ \text{s} \Rightarrow N = \dfrac{${m(1 / fq, 1)}}{0{,}1} = ${m(floors, 0)}§"""),
                background = l("Jedes Gebäude hat eine Eigenfrequenz, wie eine Stimmgabel. Schüttelt der Boden genau in diesem Takt, schaukelt sich die Bewegung von Schwingung zu Schwingung auf. Kleinere und größere Häuser daneben bleiben oft fast unbeschädigt. Moderne Hochhäuser stehen deshalb auf Gummilagern oder haben Dämpfer.", "Every building has a natural frequency, like a tuning fork. If the ground shakes at exactly that rate, the motion builds up from swing to swing. Smaller and taller buildings next door often stay almost undamaged. That's why modern tower blocks stand on rubber bearings or have dampers."),
                fact = l("Beim Beben von Mexiko-Stadt 1985 stürzten vor allem Häuser mit 6 bis 15 Stockwerken ein. Der weiche Seeboden der Stadt verstärkte genau deren Frequenz.", "In the 1985 Mexico City earthquake it was mainly buildings with 6 to 15 storeys that collapsed. The city's soft lake bed amplified exactly their frequency."),
            )
        },
        lab("phy.schattenrad", Topic.Oscillations, M, 0.5f) {
            task(
                question = l("Ein Stift sitzt am Rand einer Scheibe, die sich gleichmäßig dreht. Eine Lampe wirft seinen Schatten von der Seite an eine Wand. Wie bewegt sich der Schatten?", "A peg sits on the rim of a disc that turns at a steady rate. A lamp casts its shadow from the side onto a wall. How does the shadow move?"),
                steps = listOf(pick1(l("Der Schatten …", "The shadow …"),
                    l("schwingt hin und her wie ein Federpendel: in der Mitte am schnellsten, an den Enden kurz in Ruhe", "swings back and forth like a mass on a spring: fastest in the middle, briefly at rest at the ends"),
                    listOf(l("läuft gleichmäßig hin und her und kehrt an den Enden abrupt um", "moves back and forth at a steady speed and turns round abruptly at the ends"), l("ist an den Enden am schnellsten", "is fastest at the ends"), l("steht still, weil der Stift sich im Kreis bewegt", "stands still, because the peg moves in a circle")),
                    hints = listOf(l("Kernidee: Der Schatten zeigt nur eine Koordinate der Kreisbewegung, etwa x = r·cos(ωt).", "Key idea: the shadow shows only one coordinate of the circular motion, e.g. x = r·cos(ωt)."), l("Am Rand des Schattenwegs bewegt sich der Stift gerade genau auf die Wand zu.", "At the edge of the shadow's path the peg is moving straight towards the wall.")))),
                solution = listOf("""§x(t) = r \cos(\omega t)§""", l("Das ist genau die Gleichung einer harmonischen Schwingung.", "That is exactly the equation of a harmonic oscillation.")),
                background = l("Eine harmonische Schwingung ist der Schatten einer gleichmäßigen Kreisbewegung. Deshalb tauchen Sinus und Kosinus in allen Schwingungen auf, und deshalb heißt ω auch bei einem Pendel Kreisfrequenz. Wer das Bild im Kopf hat, versteht sofort, warum die Geschwindigkeit in der Mitte am größten ist.", "A harmonic oscillation is the shadow of a steady circular motion. That's why sine and cosine appear in every oscillation, and why ω is called the angular frequency even for a pendulum. With that picture in mind you immediately see why the speed is greatest in the middle."),
                fact = l("Die Kolben eines Automotors laufen fast genau so: Die Kurbelwelle dreht sich im Kreis, der Kolben folgt ihrem Schatten.", "The pistons of a car engine move almost exactly like this: the crankshaft turns in a circle and the piston follows its shadow."),
            )
        },
        lab("phy.lcradio", Topic.Oscillations, H, 0.6f) {
            val f = pick(88.0, 100.0, 104.0) * 1e6; val lH = 1e-6
            val c = 1 / ((2 * PI * f) * (2 * PI * f) * lH)
            task(
                question = l("In einem UKW-Radio wählt ein Schwingkreis aus Spule (1 µH) und Kondensator den Sender. Welche Kapazität braucht man, um ${n(f / 1e6, 0)} MHz zu empfangen?", "In an FM radio a tuned circuit of a coil (1 µH) and a capacitor selects the station. What capacitance is needed to receive ${n(f / 1e6, 0)} MHz?"),
                steps = listOf(num(l("Kapazität in Picofarad", "Capacitance in picofarads"), c * 1e12, "pF", rel = 0.02, done = """§C \approx ${m(c * 1e12, 2)}\ \text{pF}§""",
                    hints = listOf(l("Kernidee: Die Energie pendelt zwischen Spule (Magnetfeld) und Kondensator (elektrisches Feld) hin und her, mit der Thomson-Frequenz.", "Key idea: the energy swings back and forth between the coil (magnetic field) and the capacitor (electric field), at the Thomson frequency."), """§f = \dfrac{1}{2\pi\sqrt{LC}} \Rightarrow C = \dfrac{1}{(2\pi f)^2 L}§"""))),
                solution = listOf("""§C = \dfrac{1}{(2\pi \cdot ${sci(f)})^2 \cdot 10^{-6}}\ \text{F} \approx ${m(c * 1e12, 2)}\ \text{pF}§"""),
                background = l("Ein Schwingkreis ist das elektrische Gegenstück zum Federpendel: Der Kondensator speichert Energie wie die gespannte Feder, die Spule wie die bewegte Masse. Er reagiert nur auf eine einzige Frequenz stark, genau die, auf die er abgestimmt ist. Dreht man am Senderknopf, ändert man die Kapazität.", "A tuned circuit is the electrical counterpart of a mass on a spring: the capacitor stores energy like the stretched spring, the coil like the moving mass. It responds strongly to just one frequency, the one it is tuned to. Turning the tuning knob changes the capacitance."),
                fact = l("Weil die Frequenz nur mit der Wurzel von C geht, braucht man für das ganze UKW-Band von 88 bis 108 MHz nur eine Kapazitätsänderung von etwa 50 %.", "Because the frequency goes only with the square root of C, the whole FM band from 88 to 108 MHz needs a change in capacitance of only about 50%."),
            )
        },
        lab("phy.beinpendel", Topic.Oscillations, H, 0.6f) {
            val lLeg = pick(0.8, 0.9, 1.0)
            val t = 2 * PI * sqrt(2 * lLeg / (3 * G3)); val step = t / 2
            task(
                question = l("Beim Gehen pendelt dein Bein von selbst nach vorn. Modell: ein gleichmäßiger Stab von ${n(lLeg)} m Länge, der an der Hüfte aufgehängt ist. Wie lange dauert ein Schritt, also eine halbe Schwingung?", "When you walk, your leg swings forward by itself. Model: a uniform rod ${n(lLeg)} m long hanging from the hip. How long does one step take, i.e. half a swing?"),
                steps = listOf(num(l("Schrittdauer in Sekunden", "Step time in seconds"), step, "s", rel = 0.02, done = """§t \approx ${m(step, 2)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Ein Stab ist ein physikalisches Pendel. Seine Masse sitzt nicht unten, sondern ist verteilt, er schwingt deshalb schneller als ein Fadenpendel gleicher Länge.", "Key idea: a rod is a physical pendulum. Its mass isn't at the bottom but spread out, so it swings faster than a simple pendulum of the same length."), """§T = 2\pi \sqrt{\dfrac{2L}{3g}},\quad t = \dfrac{T}{2}§"""))),
                solution = listOf("""§T = 2\pi \sqrt{\dfrac{2 \cdot ${m(lLeg)}}{3 \cdot 9{,}81}}\ \text{s} \approx ${m(t, 2)}\ \text{s}§""", """§t = \dfrac{T}{2} \approx ${m(step, 2)}\ \text{s}§"""),
                background = l("Beim entspannten Gehen lassen wir die Beine fast von allein pendeln, das spart Energie. Deshalb hat jeder Mensch ein natürliches Schritttempo, das von der Beinlänge abhängt: Große Menschen schreiten langsamer, kleine Kinder trippeln schnell. Will man schneller sein, muss man das Bein mit Muskelkraft gegen das Pendeln beschleunigen.", "When walking relaxed we let our legs swing almost on their own, which saves energy. That's why everyone has a natural step rate that depends on leg length: tall people stride more slowly, small children patter quickly. To go faster you have to accelerate the leg with muscle power against its natural swing."),
                fact = l("Aus Fußspuren kann man so die Geschwindigkeit von Dinosauriern schätzen: Beinlänge aus der Fußgröße, Tempo aus der Schrittlänge.", "This lets you estimate the speed of dinosaurs from footprints: leg length from foot size, speed from stride length."),
            )
        },
        lab("phy.hcl", Topic.Oscillations, X, 0.7f) {
            val k = 516.0; val u = 1.6605e-27; val mu = (1.008 * 34.97) / (1.008 + 34.97) * u
            val f = sqrt(k / mu) / (2 * PI); val lambda = 2.998e8 / f
            task(
                question = l("Im Molekül HCl schwingen H- und Cl-Atom gegeneinander, als wären sie mit einer Feder (D = 516 N/m) verbunden. Die Massen sind 1,008 u und 34,97 u (1 u = 1,66 · 10⁻²⁷ kg).", "In the HCl molecule the H and Cl atoms vibrate against each other as if joined by a spring (k = 516 N/m). The masses are 1.008 u and 34.97 u (1 u = 1.66 · 10⁻²⁷ kg)."),
                steps = listOf(
                    big(l("Mit welcher Frequenz schwingt das Molekül?", "At what frequency does the molecule vibrate?"), f, "Hz", rel = 0.03, done = """§f \approx ${sci(f)}\ \text{Hz}§""",
                        hints = listOf(l("Kernidee: Zwei Massen an einer Feder schwingen wie eine einzige Masse mit der reduzierten Masse μ.", "Key idea: two masses on a spring vibrate like a single mass with the reduced mass μ."), """§\mu = \dfrac{m_1 m_2}{m_1 + m_2},\quad f = \dfrac{1}{2\pi}\sqrt{\dfrac{D}{\mu}}§""")),
                    num(l("Welche Wellenlänge hat Licht dieser Frequenz (in µm)?", "What wavelength does light of this frequency have (in µm)?"), lambda * 1e6, "µm", rel = 0.03, done = """§\lambda \approx ${m(lambda * 1e6, 2)}\ \mu\text{m}§""",
                        hints = listOf(l("Licht mit genau dieser Frequenz kann die Schwingung anregen.", "Light of exactly this frequency can excite the vibration."), """§\lambda = \dfrac{c}{f}§""")),
                ),
                solution = listOf("""§\mu = \dfrac{1{,}008 \cdot 34{,}97}{35{,}98}\ \text{u} \approx ${sci(mu)}\ \text{kg}§""", """§f = \dfrac{1}{2\pi}\sqrt{\dfrac{516}{${sci(mu)}}}\ \text{Hz} \approx ${sci(f)}\ \text{Hz}§""", """§\lambda = \dfrac{3 \cdot 10^8}{${sci(f)}}\ \text{m} \approx ${m(lambda * 1e6, 2)}\ \mu\text{m}§"""),
                background = l("Chemische Bindungen verhalten sich bei kleinen Auslenkungen wie Federn. Weil Atome so leicht sind, schwingen Moleküle billionenfach pro Sekunde, im Takt von Infrarotlicht. Jedes Molekül hat dabei seine eigenen Frequenzen, wie einen Fingerabdruck. Infrarot-Spektroskopie nutzt das, um Stoffe zu erkennen.", "Chemical bonds behave like springs for small displacements. Because atoms are so light, molecules vibrate trillions of times per second, at the rhythm of infrared light. Each molecule has its own set of frequencies, like a fingerprint. Infrared spectroscopy uses this to identify substances."),
                fact = l("Genau deshalb ist CO₂ ein Treibhausgas: Seine Schwingungen schlucken Infrarotlicht, das die Erde abstrahlt.", "That's exactly why CO₂ is a greenhouse gas: its vibrations absorb infrared light radiated by the Earth."),
            )
        },
        lab("phy.uhrtemp", Topic.Oscillations, X, 0.6f) {
            val alpha = 19e-6; val dT = pick(10.0, 15.0, 20.0)
            val rel = 0.5 * alpha * dT; val sec = rel * 86400
            task(
                question = l("Eine Pendeluhr mit Messingpendel (Ausdehnungskoeffizient 19 · 10⁻⁶ pro Kelvin) geht im Winter genau. Im Sommer ist es ${n(dT, 0)} K wärmer. Wie viele Sekunden geht sie pro Tag nach?", "A pendulum clock with a brass pendulum (expansion coefficient 19 · 10⁻⁶ per kelvin) keeps perfect time in winter. In summer it is ${n(dT, 0)} K warmer. How many seconds per day does it lose?"),
                steps = listOf(num(l("Nachgehen in Sekunden pro Tag", "Seconds lost per day"), sec, "s", rel = 0.03, done = """§\Delta t \approx ${m(sec, 1)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Das Pendel wird länger und schwingt langsamer. Die Periode geht mit der Wurzel der Länge, also halbe relative Änderung.", "Key idea: the pendulum gets longer and swings more slowly. The period goes with the square root of the length, so half the relative change."), """§\dfrac{\Delta T}{T} = \tfrac{1}{2} \dfrac{\Delta L}{L} = \tfrac{1}{2} \alpha \Delta T§"""))),
                solution = listOf("""§\dfrac{\Delta T}{T} = \tfrac{1}{2} \cdot 19 \cdot 10^{-6} \cdot ${m(dT, 0)} \approx ${sci(rel)}§""", """§\Delta t = ${sci(rel)} \cdot 86\,400\ \text{s} \approx ${m(sec, 1)}\ \text{s}§"""),
                background = l("Eine winzige Längenänderung von wenigen Zehntelmillimetern summiert sich über einen Tag zu Sekunden. Für Präzisionsuhren war das ein großes Problem. Uhrmacher bauten deshalb Kompensationspendel, etwa mit Quecksilbergefäßen, die sich nach oben ausdehnen, während der Stab nach unten länger wird.", "A tiny change in length of a few tenths of a millimetre adds up to seconds over a day. For precision clocks that was a big problem. So clockmakers built compensating pendulums, for example with vessels of mercury that expand upwards while the rod grows longer downwards."),
                fact = l("Ab 1896 nutzte man Pendel aus Invar, einer Eisen-Nickel-Legierung, die sich kaum ausdehnt. Ihr Erfinder bekam dafür den Physik-Nobelpreis.", "From 1896 pendulums were made of Invar, an iron-nickel alloy that hardly expands. Its inventor won the Nobel Prize in Physics for it."),
            )
        },
    )

    private val matter = listOf(
        lab("phy.strohhalm", Topic.Matter, M, 0.5f) {
            val h = 101300 / (1000 * G3)
            task(
                question = l("Wie lang darf ein senkrechter Strohhalm höchstens sein, damit du daraus noch Wasser trinken kannst? Nimm an, du könntest im Mund ein perfektes Vakuum erzeugen.", "What is the longest a vertical straw can be for you still to drink water through it? Assume you could create a perfect vacuum in your mouth."),
                steps = listOf(num(l("Höchste Länge in Metern", "Maximum length in metres"), h, "m", rel = 0.02, done = """§h \approx ${m(h, 1)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Du saugst nicht das Wasser hoch, der Luftdruck drückt es hinauf. Er kann eine Säule tragen, deren Schweredruck genau so groß ist wie er selbst.", "Key idea: you don't suck the water up, the air pressure pushes it up. It can support a column whose pressure equals the air pressure."), """§\rho g h = p_0 = 1013\ \text{hPa}§"""))),
                solution = listOf("""§h = \dfrac{101\,300}{1000 \cdot 9{,}81}\ \text{m} \approx ${m(h, 1)}\ \text{m}§"""),
                background = l("Beim Trinken senkst du den Druck im Mund, und der Luftdruck auf die Wasseroberfläche im Glas schiebt das Wasser in den Halm. Mehr als den vollen Luftdruck kann er nicht liefern. Schon Brunnenbauer im 17. Jahrhundert wunderten sich, dass ihre Saugpumpen nie mehr als etwa 10 m schafften. Das führte Torricelli zur Erfindung des Barometers.", "When you drink, you lower the pressure in your mouth, and the air pressure on the water surface in the glass pushes the water up the straw. It can't provide more than the full air pressure. Well-builders in the 17th century already wondered why their suction pumps never managed more than about 10 m. That led Torricelli to invent the barometer."),
                fact = l("In der Praxis schafft deine Lunge nur etwa ein Zehntel davon, rund einen Meter.", "In practice your lungs only manage about a tenth of that, around one metre."),
            )
        },
        lab("phy.staumauer", Topic.Matter, H, 0.6f) {
            val h = pick(30.0, 50.0, 80.0); val w = pick(100.0, 200.0)
            val f = 0.5 * 1000 * G3 * h * h * w
            task(
                question = l("Hinter einer ${n(w, 0)} m breiten, senkrechten Staumauer steht das Wasser ${n(h, 0)} m tief. Mit welcher Gesamtkraft drückt es gegen die Mauer?", "Behind a vertical dam ${n(w, 0)} m wide the water is ${n(h, 0)} m deep. What total force does it push against the dam with?"),
                steps = listOf(num(l("Kraft in Meganewton", "Force in meganewtons"), f / 1e6, "MN", rel = 0.03, done = """§F \approx ${m(sig(f / 1e6))}\ \text{MN}§""",
                    hints = listOf(l("Kernidee: Oben ist der Druck null, unten ρ·g·h. Er wächst gleichmäßig, also wirkt im Mittel der halbe Bodendruck auf die ganze Fläche.", "Key idea: at the top the pressure is zero, at the bottom ρ·g·h. It grows evenly, so on average half the bottom pressure acts on the whole area."), """§F = \tfrac{1}{2} \rho g h \cdot (h \cdot b) = \tfrac{1}{2} \rho g h^2 b§"""))),
                solution = listOf("""§F = \tfrac{1}{2} \cdot 1000 \cdot 9{,}81 \cdot ${m(h, 0)}^2 \cdot ${m(w, 0)}\ \text{N} \approx ${m(sig(f / 1e6))}\ \text{MN}§"""),
                background = l("Wasserdruck hängt nur von der Tiefe ab, nicht davon, wie groß der See dahinter ist. Ein schmaler Kanal drückt gleich stark gegen die Mauer wie ein riesiger Stausee. Weil die Kraft mit dem Quadrat der Tiefe wächst, sind Staumauern unten viel dicker als oben, und die tiefsten Talsperren werden oft als Bogen gebaut, der die Last in die Felswände leitet.", "Water pressure depends only on depth, not on how big the lake behind is. A narrow channel pushes on the dam just as hard as a huge reservoir. Because the force grows with the square of the depth, dams are much thicker at the bottom than at the top, and the deepest ones are often built as arches that lead the load into the rock walls."),
                fact = l("Das entspricht dem Gewicht von etwa ${n(f / G3 / 1000, 0)} Tonnen, gestemmt von einer Betonwand.", "That equals the weight of about ${n(f / G3 / 1000, 0)} tonnes, held back by a concrete wall."),
            )
        },
        lab("phy.gartenschlauch", Topic.Matter, E, 0.4f) {
            val v = pick(1.5, 2.0, 3.0); val frac = pick(0.25, 0.2, 0.5)
            val v2 = v / frac
            task(
                question = l("Aus einem Gartenschlauch fließt das Wasser mit ${n(v)} m/s. Du drückst den Daumen auf die Öffnung, sodass nur noch ${n(frac * 100, 0)} % der Fläche frei sind. Gleich viel Wasser pro Sekunde kommt heraus. Wie schnell spritzt es jetzt?", "Water flows out of a garden hose at ${n(v)} m/s. You press your thumb over the opening so that only ${n(frac * 100, 0)}% of the area is left open. The same amount of water per second comes out. How fast does it squirt now?"),
                steps = listOf(num(l("Geschwindigkeit", "Speed"), v2, "m/s", rel = 0.02, done = """§v = ${m(v2)}\ \text{m/s}§""",
                    hints = listOf(l("Kernidee: Was hineinfließt, muss auch hinaus. Durch weniger Fläche muss das Wasser schneller.", "Key idea: what flows in must flow out. Through less area the water has to go faster."), """§A_1 v_1 = A_2 v_2§"""))),
                solution = listOf("""§v_2 = v_1 \cdot \dfrac{A_1}{A_2} = ${m(v)} \cdot \dfrac{1}{${m(frac)}}\ \tfrac{\text{m}}{\text{s}} = ${m(v2)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = l("Wasser lässt sich kaum zusammendrücken. Pro Sekunde muss durch jeden Querschnitt dieselbe Menge strömen, das ist die Kontinuitätsgleichung. Wo es eng wird, wird es schnell. Deshalb fließt ein Fluss an engen Stellen reißend und in breiten Abschnitten träge.", "Water can hardly be compressed. The same amount must flow through every cross-section each second; that's the continuity equation. Where it gets narrow, it gets fast. That's why a river rushes through narrow places and flows lazily in wide stretches."),
                fact = l("Hochdruck-Wasserstrahlschneider pressen Wasser durch eine Düse von 0,1 mm und erreichen fast dreifache Schallgeschwindigkeit. Damit schneidet man Stahl.", "High-pressure water-jet cutters force water through a 0.1 mm nozzle and reach almost three times the speed of sound. They can cut steel."),
            )
        },
        lab("phy.sturmdach", Topic.Matter, H, 0.6f) {
            val vKmh = pick(100.0, 120.0, 150.0); val a = pick(80.0, 100.0, 150.0)
            val v = vKmh / 3.6; val dp = 0.5 * 1.2 * v * v; val f = dp * a
            task(
                question = l("Ein Sturm fegt mit ${n(vKmh, 0)} km/h über ein flaches Dach von ${n(a, 0)} m². Unter dem Dach ist die Luft still. Mit welcher Kraft wird das Dach nach oben gezogen (Luftdichte 1,2 kg/m³)?", "A storm sweeps at ${n(vKmh, 0)} km/h over a flat roof of ${n(a, 0)} m². Under the roof the air is still. With what force is the roof pulled upwards (air density 1.2 kg/m³)?"),
                steps = listOf(num(l("Kraft in Kilonewton", "Force in kilonewtons"), f / 1000, "kN", rel = 0.03, done = """§F \approx ${m(sig(f / 1000))}\ \text{kN}§""",
                    hints = listOf(l("Kernidee: In schnell strömender Luft ist der Druck kleiner (Bernoulli). Der Unterschied ist der Staudruck.", "Key idea: in fast-moving air the pressure is lower (Bernoulli). The difference is the dynamic pressure."), """§\Delta p = \tfrac{1}{2} \rho v^2,\quad F = \Delta p \cdot A§"""))),
                solution = listOf("""§v = ${m(v, 1)}\ \tfrac{\text{m}}{\text{s}},\quad \Delta p = \tfrac{1}{2} \cdot 1{,}2 \cdot ${m(v, 1)}^2\ \text{Pa} \approx ${m(sig(dp))}\ \text{Pa}§""", """§F = ${m(sig(dp))} \cdot ${m(a, 0)}\ \text{N} \approx ${m(sig(f / 1000))}\ \text{kN}§"""),
                background = l("Strömende Luft drückt weniger auf eine Fläche als ruhende, weil ein Teil ihrer Energie in der Bewegung steckt. Bei einem Sturm reicht dieser kleine Druckunterschied über eine große Fläche, um Dächer abzuheben. Dasselbe Prinzip trägt Flugzeuge: Über dem Flügel strömt die Luft schneller als darunter.", "Moving air presses less on a surface than still air, because part of its energy is tied up in the motion. In a storm this small pressure difference over a large area is enough to lift off roofs. The same principle holds up aircraft: the air flows faster over the wing than under it."),
                fact = l("Deshalb rät man bei Orkanen, die Fenster geschlossen zu halten: Würde Luft unter das Dach strömen, käme zum Sog auch noch Druck von innen.", "That's why the advice in hurricanes is to keep windows shut: if air rushed in under the roof, pressure from inside would add to the suction."),
            )
        },
        lab("phy.uroehr", Topic.Matter, M, 0.5f) {
            val hOil = pick(10.0, 12.0, 20.0); val rho = pick(800.0, 900.0)
            val hWater = hOil * rho / 1000; val diff = hOil - hWater
            task(
                question = l("In einem U-Rohr ist Wasser. In den linken Schenkel gießt du Öl (Dichte ${n(rho, 0)} kg/m³), bis die Ölsäule ${n(hOil, 0)} cm hoch ist. Um wie viel steht die Öloberfläche höher als die Wasseroberfläche im rechten Schenkel?", "A U-tube contains water. You pour oil (density ${n(rho, 0)} kg/m³) into the left arm until the oil column is ${n(hOil, 0)} cm tall. How much higher is the oil surface than the water surface in the right arm?"),
                steps = listOf(num(l("Höhenunterschied in cm", "Difference in height in cm"), diff, "cm", rel = 0.03, done = """§\Delta h = ${m(diff, 1)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Auf Höhe der Grenzfläche Öl/Wasser muss in beiden Schenkeln derselbe Druck herrschen.", "Key idea: at the level of the oil/water boundary the pressure must be the same in both arms."), """§\rho_{Öl}\, h_{Öl} = \rho_W\, h_W§""".let { l(it, it.replace("Öl", "oil")) }))),
                solution = listOf("""§h_W = ${m(hOil, 0)} \cdot \dfrac{${m(rho, 0)}}{1000}\ \text{cm} = ${m(hWater, 1)}\ \text{cm}§""", """§\Delta h = ${m(hOil, 0)} - ${m(hWater, 1)} = ${m(diff, 1)}\ \text{cm}§"""),
                background = l("In einer zusammenhängenden, ruhenden Flüssigkeit ist der Druck auf gleicher Höhe überall gleich. Das leichtere Öl braucht deshalb eine höhere Säule, um denselben Druck zu erzeugen wie das Wasser. So kann man mit einem U-Rohr die Dichte einer Flüssigkeit messen, ganz ohne Waage.", "In a connected liquid at rest, the pressure is the same everywhere at the same height. That's why the lighter oil needs a taller column to produce the same pressure as the water. This lets you measure the density of a liquid with a U-tube, without any scales at all."),
                fact = l("Ärzte maßen den Blutdruck früher mit einer Quecksilbersäule. Daher kommt die Einheit mmHg, Millimeter Quecksilbersäule.", "Doctors used to measure blood pressure with a column of mercury. That's where the unit mmHg, millimetres of mercury, comes from."),
            )
        },
        lab("phy.krone", Topic.Matter, H, 0.6f) {
            val air = 10.0; val water = pick(9.4, 9.45, 9.5)
            val rho = air / (air - water)
            task(
                question = l("Archimedes soll prüfen, ob eine Krone aus reinem Gold ist (Dichte 19,3 g/cm³). An Luft zieht sie an einer Federwaage mit ${n(air, 1)} N, ganz unter Wasser getaucht nur noch mit ${n(water, 2)} N. Welche Dichte hat sie?", "Archimedes is asked to check whether a crown is pure gold (density 19.3 g/cm³). In air it pulls on a spring balance with ${n(air, 1)} N, fully submerged in water only with ${n(water, 2)} N. What is its density?"),
                steps = listOf(num(l("Dichte in g/cm³", "Density in g/cm³"), rho, "g/cm³", rel = 0.02, done = """§\rho \approx ${m(rho, 1)}\ \text{g/cm}^3§""",
                    hints = listOf(l("Kernidee: Der Gewichtsverlust unter Wasser ist der Auftrieb, also das Gewicht des verdrängten Wassers. Er verrät das Volumen.", "Key idea: the weight lost under water is the buoyancy, i.e. the weight of the displaced water. It reveals the volume."), """§\dfrac{\rho_K}{\rho_W} = \dfrac{F_{Luft}}{F_{Luft} - F_{Wasser}}§""".let { l(it, it.replace("Luft", "air").replace("Wasser", "water")) }))),
                solution = listOf("""§F_A = ${m(air, 1)} - ${m(water, 2)} = ${m(air - water, 2)}\ \text{N}§""", """§\rho = \dfrac{${m(air, 1)}}{${m(air - water, 2)}} \cdot 1\ \tfrac{\text{g}}{\text{cm}^3} \approx ${m(rho, 1)}\ \tfrac{\text{g}}{\text{cm}^3}§"""),
                background = l("Das Gewicht an Luft verrät die Masse, der Gewichtsverlust im Wasser das Volumen. Zusammen ergibt das die Dichte, ohne die Krone zu beschädigen. Ist sie kleiner als die von Gold, wurde leichteres Metall wie Silber beigemischt. Die Legende sagt, Archimedes sei bei dieser Erkenntnis aus der Badewanne gesprungen und habe Heureka gerufen.", "The weight in air reveals the mass, the weight lost in water the volume. Together they give the density without damaging the crown. If it's lower than gold's, a lighter metal like silver has been mixed in. Legend has it that Archimedes jumped out of his bath at this realisation and shouted Eureka."),
                fact = l("Gold ist so dicht, dass ein Liter davon über 19 kg wiegt, fast so viel wie ein voller Wasserkasten.", "Gold is so dense that a litre of it weighs over 19 kg, almost as much as a full crate of water bottles."),
            )
        },
        lab("phy.ponton", Topic.Matter, M, 0.4f) {
            val lp = 10.0; val wp = 4.0; val dh = pick(3.0, 4.0, 5.0)
            val mass = 1000 * lp * wp * dh / 100
            task(
                question = l("Ein rechteckiger Ponton (10 m × 4 m) schwimmt auf einem See. Ein Auto fährt darauf, und der Ponton sinkt ${n(dh, 0)} cm tiefer ein. Wie schwer ist das Auto?", "A rectangular pontoon (10 m × 4 m) floats on a lake. A car drives onto it, and the pontoon sinks ${n(dh, 0)} cm deeper. What is the mass of the car?"),
                steps = listOf(num(l("Masse des Autos in kg", "Mass of the car in kg"), mass, "kg", rel = 0.02, done = """§m = ${m(mass, 0)}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Das zusätzliche Gewicht muss genau durch zusätzlich verdrängtes Wasser getragen werden.", "Key idea: the extra weight must be carried by exactly the extra water displaced."), """§m = \rho_W \cdot A \cdot \Delta h§"""))),
                solution = listOf("""§m = 1000\ \tfrac{\text{kg}}{\text{m}^3} \cdot 40\ \text{m}^2 \cdot ${m(dh / 100)}\ \text{m} = ${m(mass, 0)}\ \text{kg}§"""),
                background = l("Ein schwimmender Körper verdrängt genau sein eigenes Gewicht an Wasser. Kommt Last dazu, sinkt er gerade so viel tiefer, dass das neu verdrängte Wasser die Last trägt. Schiffe haben deshalb an der Seite Markierungen, die Ladelinien, an denen man ihre Beladung ablesen kann.", "A floating body displaces exactly its own weight of water. When load is added, it sinks just deep enough for the newly displaced water to carry the load. That's why ships have markings on their sides, the load lines, from which their cargo can be read off."),
                fact = l("Ein Stahlschiff schwimmt, obwohl Stahl achtmal so dicht wie Wasser ist: Sein Rumpf ist innen fast nur Luft.", "A steel ship floats although steel is eight times as dense as water: its hull is almost all air inside."),
            )
        },
        lab("phy.magdeburg", Topic.Matter, M, 0.5f) {
            val d = pick(0.4, 0.5); val r = d / 2
            val f = 101300 * PI * r * r
            task(
                question = l("1654 pumpte Otto von Guericke die Luft aus zwei aneinandergelegten Halbkugeln mit ${n(d * 100, 0)} cm Durchmesser. Mit welcher Kraft musste man sie auseinanderziehen?", "In 1654 Otto von Guericke pumped the air out of two hemispheres ${n(d * 100, 0)} cm in diameter placed together. What force was needed to pull them apart?"),
                steps = listOf(num(l("Kraft in Kilonewton", "Force in kilonewtons"), f / 1000, "kN", rel = 0.03, done = """§F \approx ${m(sig(f / 1000))}\ \text{kN}§""",
                    hints = listOf(l("Kernidee: Innen ist kein Druck mehr, außen drückt der volle Luftdruck. Es zählt die Querschnittsfläche der Kugel, ein Kreis.", "Key idea: inside there's no pressure left, outside the full air pressure presses. What counts is the cross-section of the sphere, a circle."), """§F = p_0 \cdot \pi r^2§"""))),
                solution = listOf("""§F = 101\,300\ \text{Pa} \cdot \pi \cdot ${m(r, 2)}^2\ \text{m}^2 \approx ${m(sig(f / 1000))}\ \text{kN}§"""),
                background = l("Wir leben am Grund eines Luftmeeres, das mit rund 10 Tonnen auf jeden Quadratmeter drückt. Wir merken es nicht, weil der Druck von allen Seiten gleich ist. Erst wenn eine Seite fehlt, zeigt er seine Kraft. Guerickes Vorführung machte das Vakuum, das viele Gelehrte für unmöglich hielten, für alle sichtbar.", "We live at the bottom of an ocean of air that presses on every square metre with around 10 tonnes. We don't notice because the pressure is the same from all sides. Only when one side is missing does it show its strength. Guericke's demonstration made the vacuum, which many scholars thought impossible, visible to everyone."),
                fact = l("Bei der berühmten Vorführung zogen 16 Pferde, je acht auf jeder Seite, und schafften es kaum. Lässt man Luft hinein, fallen die Kugeln von selbst auseinander.", "In the famous demonstration 16 horses pulled, eight on each side, and barely managed it. Let air in and the hemispheres fall apart by themselves."),
            )
        },
        lab("phy.giraffe", Topic.Matter, M, 0.5f) {
            val h = pick(2.0, 2.5); val dp = 1050 * G3 * h; val mmhg = dp / 133.3
            task(
                question = l("Der Kopf einer Giraffe liegt ${n(h)} m über ihrem Herzen. Um wie viel muss der Blutdruck am Herzen höher sein, damit das Blut überhaupt bis ins Gehirn kommt (Blutdichte 1050 kg/m³)?", "A giraffe's head is ${n(h)} m above its heart. By how much must the blood pressure at the heart be higher just to get the blood up to the brain (blood density 1050 kg/m³)?"),
                steps = listOf(num(l("Zusätzlicher Druck in mmHg (1 mmHg = 133,3 Pa)", "Extra pressure in mmHg (1 mmHg = 133.3 Pa)"), mmhg, "mmHg", rel = 0.03, done = """§\Delta p \approx ${m(mmhg, 0)}\ \text{mmHg}§""",
                    hints = listOf(l("Kernidee: Eine Blutsäule drückt wie eine Wassersäule, mit ihrem Schweredruck.", "Key idea: a column of blood presses like a column of water, with its hydrostatic pressure."), """§\Delta p = \rho g h§"""))),
                solution = listOf("""§\Delta p = 1050 \cdot 9{,}81 \cdot ${m(h)}\ \text{Pa} \approx ${m(sig(dp))}\ \text{Pa} \approx ${m(mmhg, 0)}\ \text{mmHg}§"""),
                background = l("Unser Blutdruck wird deshalb am Oberarm gemessen, auf Herzhöhe. In den Füßen ist er beim Stehen viel höher, im Kopf niedriger. Giraffen haben dafür einen extrem hohen Blutdruck, doppelt so hoch wie unserer, und straffe Haut an den Beinen wie Kompressionsstrümpfe.", "That's why our blood pressure is measured on the upper arm, at heart level. In the feet it is much higher when standing, in the head lower. Giraffes deal with this through extremely high blood pressure, twice ours, and tight skin on their legs like compression stockings."),
                fact = l("Beim Trinken senkt die Giraffe den Kopf um über 4 m. Spezielle Klappen und ein Gefäßgeflecht verhindern, dass ihr dabei das Blut in den Kopf schießt.", "When drinking, a giraffe lowers its head by over 4 m. Special valves and a network of vessels stop the blood from rushing into its head."),
            )
        },
        lab("phy.wasserturm", Topic.Matter, E, 0.3f) {
            val h = pick(30.0, 40.0, 50.0); val p = 1000 * G3 * h / 1e5
            task(
                question = l("Der Wasserspiegel im Wasserturm liegt ${n(h, 0)} m über deinem Wasserhahn. Mit welchem Überdruck kommt das Wasser bei dir an?", "The water level in the water tower is ${n(h, 0)} m above your tap. With what pressure above atmospheric does the water arrive at your tap?"),
                steps = listOf(num(l("Überdruck in bar", "Pressure in bar"), p, "bar", rel = 0.02, done = """§p \approx ${m(p, 1)}\ \text{bar}§""",
                    hints = listOf(l("Kernidee: Nur der Höhenunterschied zählt, nicht die Länge oder Form der Leitung.", "Key idea: only the difference in height counts, not the length or shape of the pipes."), """§p = \rho g h§"""))),
                solution = listOf("""§p = 1000 \cdot 9{,}81 \cdot ${m(h, 0)}\ \text{Pa} \approx ${m(p, 1)}\ \text{bar}§"""),
                background = l("Ein Wasserturm ist ein Druckspeicher: Pumpen füllen ihn nachts, und tagsüber drückt allein die Höhe des Wassers es in jedes Haus. Etwa alle 10 m Höhe bringen 1 bar. Weil nur die Höhe zählt, brauchen Hochhäuser oben eigene Pumpen.", "A water tower is a pressure store: pumps fill it at night, and during the day the height of the water alone pushes it into every house. Roughly every 10 m of height gives 1 bar. Because only height counts, tower blocks need their own pumps for the upper floors."),
                fact = l("Wasser ist praktisch nicht zusammendrückbar. Deshalb kommt der Druck am Hahn sofort an, obwohl das Wasser selbst nur langsam durch die Rohre fließt.", "Water is practically incompressible. That's why the pressure arrives at the tap instantly, even though the water itself flows only slowly through the pipes."),
            )
        },
        lab("phy.heliumballons", Topic.Matter, M, 0.5f) {
            val kid = pick(20.0, 30.0, 40.0); val vol = 10.0
            val lift = (1.2 - 0.17) * vol / 1000; val nb = kid / lift
            task(
                question = l("Ein Partyballon fasst 10 Liter Helium (Dichte 0,17 kg/m³), Luft hat 1,2 kg/m³. Wie viele Ballons bräuchte man, um ein Kind mit ${n(kid, 0)} kg hochzuheben (Gummi und Schnur vernachlässigt)?", "A party balloon holds 10 litres of helium (density 0.17 kg/m³); air has 1.2 kg/m³. How many balloons would it take to lift a child of ${n(kid, 0)} kg (rubber and string neglected)?"),
                steps = listOf(num(l("Anzahl Ballons", "Number of balloons"), nb, "", rel = 0.03, done = """§N \approx ${m(sig(nb))}§""",
                    hints = listOf(l("Kernidee: Jeder Ballon trägt den Unterschied zwischen verdrängter Luft und eigenem Helium.", "Key idea: each balloon carries the difference between the air it displaces and its own helium."), """§m_{Ballon} = (1{,}2 - 0{,}17) \cdot 0{,}01\ \text{kg} \approx 10\ \text{g}§""".let { l(it, it.replace("Ballon", "balloon")) }))),
                solution = listOf("""§\Delta m = (1{,}2 - 0{,}17)\ \tfrac{\text{kg}}{\text{m}^3} \cdot 0{,}01\ \text{m}^3 \approx ${m(lift * 1000, 1)}\ \text{g}§""", """§N = \dfrac{${m(kid, 0)}\ \text{kg}}{${m(lift * 1000, 1)}\ \text{g}} \approx ${m(sig(nb))}§"""),
                background = l("Auftrieb gibt es auch in Luft: Ein Körper wird um das Gewicht der verdrängten Luft leichter. Luft ist aber so dünn, dass selbst das leichteste Gas nur wenig gewinnt, etwa 1 Gramm pro Liter. Deshalb brauchen Zeppeline riesige Hüllen, und deshalb tragen Partyballons gerade mal ihre Schnur.", "There is buoyancy in air too: an object becomes lighter by the weight of the air it displaces. But air is so thin that even the lightest gas gains only a little, about 1 gram per litre. That's why airships need huge envelopes and why party balloons can barely carry their own string."),
                fact = l("Das ist fast ein ganzer Raum voller Ballons. Im echten Leben wäre das Gummi zu schwer: Ein Ballon wiegt selbst schon einige Gramm.", "That's almost a whole room full of balloons. In real life the rubber would be too heavy: a balloon itself already weighs a few grams."),
            )
        },
        lab("phy.stokes", Topic.Matter, X, 0.7f) {
            val rMm = pick(1.0, 1.5); val eta = pick(10.0, 5.0)
            val r = rMm / 1000; val v = 2 * r * r * (7800 - 1400) * G3 / (9 * eta)
            task(
                question = l("Eine Stahlkugel (Dichte 7800 kg/m³, Radius ${n(rMm)} mm) sinkt langsam durch Honig (Dichte 1400 kg/m³, Viskosität ${n(eta, 0)} Pa·s). Mit welcher Endgeschwindigkeit sinkt sie?", "A steel ball (density 7800 kg/m³, radius ${n(rMm)} mm) sinks slowly through honey (density 1400 kg/m³, viscosity ${n(eta, 0)} Pa·s). At what terminal speed does it sink?"),
                steps = listOf(num(l("Sinkgeschwindigkeit in mm/s", "Sinking speed in mm/s"), v * 1000, "mm/s", rel = 0.03, done = """§v \approx ${m(v * 1000, 2)}\ \text{mm/s}§""",
                    hints = listOf(l("Kernidee: Gewicht minus Auftrieb wird von der Reibung im zähen Honig ausgeglichen. Für langsame Kugeln gilt das Gesetz von Stokes.", "Key idea: weight minus buoyancy is balanced by friction in the thick honey. For slow spheres Stokes' law applies."), """§6\pi \eta r v = \tfrac{4}{3}\pi r^3 (\rho_K - \rho_H) g \Rightarrow v = \dfrac{2 r^2 (\rho_K - \rho_H) g}{9\eta}§"""))),
                solution = listOf("""§v = \dfrac{2 \cdot (${sci(r)})^2 \cdot 6400 \cdot 9{,}81}{9 \cdot ${m(eta, 0)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v * 1000, 2)}\ \tfrac{\text{mm}}{\text{s}}§"""),
                background = l("In zähen Flüssigkeiten wächst die Reibung einfach mit der Geschwindigkeit. Eine Kugel beschleunigt deshalb nur kurz und sinkt dann gleichmäßig. Weil die Endgeschwindigkeit mit r² wächst, sinken große Teilchen viel schneller als kleine. So trennen sich Sand und Ton in einem Glas Wasser in Schichten.", "In thick liquids friction simply grows with speed. So a ball accelerates only briefly and then sinks steadily. Because the terminal speed grows with r², big particles sink much faster than small ones. That's how sand and clay separate into layers in a glass of water."),
                fact = l("Mit diesem Gesetz bestimmte Robert Millikan 1909 die Größe winziger Öltröpfchen und daraus die Ladung eines einzelnen Elektrons.", "With this law Robert Millikan determined the size of tiny oil droplets in 1909, and from that the charge of a single electron."),
            )
        },
        lab("phy.torricelli", Topic.Matter, H, 0.6f) {
            val h = pick(0.8, 1.25, 1.8); val y = pick(0.8, 0.5)
            val v = sqrt(2 * G3 * h); val x = v * sqrt(2 * y / G3)
            task(
                question = l("Eine Regentonne hat ein Loch ${n(h)} m unter dem Wasserspiegel. Das Loch selbst liegt ${n(y * 100, 0)} cm über dem Boden.", "A rain barrel has a hole ${n(h)} m below the water surface. The hole itself is ${n(y * 100, 0)} cm above the ground."),
                steps = listOf(
                    num(l("Mit welcher Geschwindigkeit schießt das Wasser heraus?", "At what speed does the water shoot out?"), v, "m/s", rel = 0.02, done = """§v \approx ${m(v, 2)}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Das ausströmende Wasser hat dieselbe Geschwindigkeit, als wäre es frei von der Oberfläche bis zum Loch gefallen (Torricelli).", "Key idea: the outflowing water has the same speed as if it had fallen freely from the surface to the hole (Torricelli)."), """§v = \sqrt{2 g h}§""")),
                    num(l("Wie weit von der Tonne trifft der Strahl den Boden?", "How far from the barrel does the jet hit the ground?"), x, "m", rel = 0.02, done = """§x \approx ${m(x, 2)}\ \text{m}§""",
                        hints = listOf(l("Ab dem Loch fliegt das Wasser wie ein waagrecht geworfener Stein.", "From the hole on, the water flies like a stone thrown horizontally."), """§t = \sqrt{\dfrac{2y}{g}},\quad x = v t§""")),
                ),
                solution = listOf("""§v = \sqrt{2 \cdot 9{,}81 \cdot ${m(h)}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 2)}\ \tfrac{\text{m}}{\text{s}}§""", """§x = ${m(v, 2)} \cdot \sqrt{\dfrac{2 \cdot ${m(y)}}{9{,}81}}\ \text{m} \approx ${m(x, 2)}\ \text{m}§"""),
                background = l("Torricelli erkannte um 1643: Ein Wasserteilchen, das unten herausfließt, hat die Lageenergie verloren, die einer Höhe h entspricht, genau wie ein fallender Stein. Je voller die Tonne, desto schneller der Strahl. Weil der Wasserspiegel beim Auslaufen sinkt, wird der Strahl immer kürzer, und das Leerlaufen dauert gegen Ende immer länger.", "Around 1643 Torricelli realised that a parcel of water flowing out at the bottom has lost the potential energy of a height h, just like a falling stone. The fuller the barrel, the faster the jet. Because the water level drops as it drains, the jet gets shorter and shorter, and emptying slows down more and more towards the end."),
                fact = l("Wasseruhren der Antike hatten deshalb nach oben breiter werdende Gefäße: So sank der Pegel trotzdem gleichmäßig.", "Ancient water clocks therefore used vessels that widened towards the top: that way the level still fell evenly."),
            )
        },
        lab("phy.boyle", Topic.Matter, M, 0.5f) {
            val depth = pick(10.0, 20.0, 30.0); val v = 6.0
            val p = 1 + depth / 10; val v2 = v * p
            task(
                question = l("Ein Taucher atmet in ${n(depth, 0)} m Tiefe aus seiner Flasche und hat 6 Liter Luft in der Lunge. Er steigt auf, ohne auszuatmen. Wie groß wäre das Luftvolumen an der Oberfläche (Temperatur gleich)?", "A diver breathes from his tank at a depth of ${n(depth, 0)} m and has 6 litres of air in his lungs. He ascends without breathing out. How large would the volume of air be at the surface (same temperature)?"),
                steps = listOf(num(l("Volumen in Litern", "Volume in litres"), v2, "l", rel = 0.02, done = """§V = ${m(v2, 0)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Alle 10 m Wasser kommt ein bar dazu. Bei gleicher Temperatur ist Druck mal Volumen konstant.", "Key idea: every 10 m of water adds one bar. At constant temperature, pressure times volume stays the same."), """§p_1 V_1 = p_2 V_2§"""))),
                solution = listOf("""§p_1 = 1 + \dfrac{${m(depth, 0)}}{10} = ${m(p, 0)}\ \text{bar}§""", """§V_2 = \dfrac{${m(p, 0)} \cdot 6}{1}\ \text{l} = ${m(v2, 0)}\ \text{l}§"""),
                background = l("Gase lassen sich zusammendrücken: Doppelter Druck, halbes Volumen (Boyle-Mariotte). Die Luft aus der Flasche kommt in der Tiefe mit dem dort herrschenden Druck in die Lunge. Steigt der Taucher auf, dehnt sie sich aus, und die Lunge kann das nicht. Deshalb lautet die wichtigste Regel beim Tauchen: Beim Aufstieg niemals die Luft anhalten.", "Gases can be compressed: double the pressure, half the volume (Boyle's law). Air from the tank enters the lungs at the pressure of that depth. As the diver ascends it expands, and the lungs can't cope. That's why the most important rule in diving is: never hold your breath while ascending."),
                fact = l("Beim Freitauchen ohne Flasche passiert das Gegenteil: Die Lunge wird in der Tiefe zusammengedrückt und dehnt sich beim Auftauchen nur auf ihre normale Größe zurück.", "In free diving without a tank the opposite happens: the lungs are squeezed at depth and expand back only to their normal size on the way up."),
            )
        },
    )
}
