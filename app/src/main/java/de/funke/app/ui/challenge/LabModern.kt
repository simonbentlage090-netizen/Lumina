package de.funke.app.ui.challenge

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

// =============================================================================================
// Vorlagen moderne Physik: Relativität, Quanten, Atome & Kerne, Astrophysik, Teilchen (seit 10.1).
// Ideen u. a. nach Susskind "The Theoretical Minimum" und Trodden & Carroll "TASI Lectures on
// Cosmology" – eigene Aufgaben, keine Inhalte daraus.
// =============================================================================================

private const val C = 2.998e8
private const val H_PLANCK = 6.626e-34
private const val HBAR = 1.055e-34
private const val EV = 1.602e-19
private const val ME = 9.109e-31
private const val LY = 9.461e15
private const val GRAV = 6.674e-11
private const val M_SUN = 1.989e30
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert
private val E = Difficulty.Easy

private object Bg5 {
    val relativity get() = l(
        "Einsteins Relativitätstheorie beruht auf einer einfachen, aber verblüffenden Tatsache: Licht ist für jeden gleich schnell, egal wie schnell man selbst unterwegs ist. Damit das stimmt, müssen Zeit und Raum nachgeben: Bewegte Uhren gehen langsamer und bewegte Maßstäbe werden kürzer – um den Lorentz-Faktor γ. Im Alltag ist γ fast genau 1, erst nahe der Lichtgeschwindigkeit wird es dramatisch.",
        "Einstein's relativity rests on a simple but astonishing fact: light has the same speed for everyone, no matter how fast you are moving yourself. For that to hold, time and space have to give way: moving clocks run slow and moving rulers get shorter – by the Lorentz factor γ. In daily life γ is almost exactly 1; only close to the speed of light does it become dramatic.",
    )
    val emc2 get() = l(
        "E = mc² sagt: Masse ist eine extrem konzentrierte Form von Energie. Weil c² so riesig ist, steckt in einem Gramm Materie so viel Energie wie in 20.000 Tonnen TNT. In Kernkraftwerken und in der Sonne wird nur ein winziger Bruchteil der Masse umgewandelt – bei der Sonne trotzdem 4 Millionen Tonnen pro Sekunde.",
        "E = mc² says: mass is an extremely concentrated form of energy. Because c² is so huge, one gram of matter holds as much energy as 20,000 tonnes of TNT. Nuclear reactors and the Sun convert only a tiny fraction of the mass – for the Sun that is still 4 million tonnes every second.",
    )
    val photon get() = l(
        "Licht kommt in Paketen, den Photonen. Die Energie eines Photons hängt nur von seiner Frequenz ab: E = h·f. Blaues Licht hat energiereichere Photonen als rotes, UV-Photonen genug, um Hautzellen zu schädigen. Mehr Helligkeit bedeutet mehr Photonen, nicht stärkere – genau das erklärte Einstein 1905 mit dem Fotoeffekt und bekam dafür den Nobelpreis.",
        "Light comes in packets, called photons. The energy of a photon depends only on its frequency: E = h·f. Blue light has more energetic photons than red, UV photons enough to damage skin cells. More brightness means more photons, not stronger ones – that's exactly what Einstein explained in 1905 with the photoelectric effect, earning him the Nobel Prize.",
    )
    val matterWave get() = l(
        "Auch Teilchen sind Wellen: Louis de Broglie schlug 1924 vor, dass jedes Teilchen eine Wellenlänge λ = h/p hat. Bei einem Fußball ist sie unvorstellbar winzig, bei Elektronen aber so groß wie Atomabstände – deshalb zeigen Elektronen an Kristallen Interferenzmuster, und Elektronenmikroskope sehen viel feiner als Lichtmikroskope.",
        "Particles are waves too: Louis de Broglie proposed in 1924 that every particle has a wavelength λ = h/p. For a football it is unimaginably tiny, but for electrons it is as large as the spacing of atoms – that's why electrons form interference patterns on crystals, and why electron microscopes see far finer details than light microscopes.",
    )
    val uncertainty get() = l(
        "Heisenbergs Unschärferelation sagt: Ort und Impuls eines Teilchens kann man nicht gleichzeitig beliebig genau festlegen – nicht wegen schlechter Messgeräte, sondern weil das Teilchen selbst keine scharfen Werte hat. Je enger man ein Elektron einsperrt, desto wilder zappelt es. Genau das verhindert, dass Elektronen in den Atomkern stürzen.",
        "Heisenberg's uncertainty principle says: position and momentum of a particle can't both be pinned down arbitrarily precisely – not because of poor instruments, but because the particle itself doesn't have sharp values. The more tightly you confine an electron, the more wildly it jiggles. That is precisely what stops electrons from falling into the nucleus.",
    )
    val box get() = l(
        "Ein Teilchen, das in einem Kasten eingesperrt ist, kann nur ganz bestimmte Energien haben – stehende Wellen, die genau in den Kasten passen. Die Energien wachsen mit n² und sind umso größer, je kleiner der Kasten ist. Das ist der Grund, warum Quantenpunkte je nach Größe in anderen Farben leuchten.",
        "A particle trapped in a box can only have certain energies – standing waves that fit exactly into the box. The energies grow with n² and are larger the smaller the box. That's why quantum dots glow in different colours depending on their size.",
    )
    val tunnel get() = l(
        "Klassisch prallt ein Teilchen an einer Wand ab, wenn seine Energie nicht reicht. Quantenmechanisch klingt seine Welle in der Wand nur ab – ist die Wand dünn genug, kommt ein Teil auf der anderen Seite heraus. Dieser Tunneleffekt lässt die Sonne brennen, macht Flash-Speicher möglich und erlaubt Rastertunnelmikroskopen, einzelne Atome abzubilden.",
        "Classically a particle bounces off a wall if it lacks the energy. Quantum mechanically its wave only fades away inside the wall – if the wall is thin enough, part of it emerges on the other side. This tunnelling keeps the Sun burning, makes flash memory possible and lets scanning tunnelling microscopes image single atoms.",
    )
    val atom get() = l(
        "Ein Atom ist fast leer: Fast die ganze Masse steckt im winzigen Kern, die Elektronen bilden eine Hülle, die etwa 100.000-mal größer ist. Wäre der Kern so groß wie eine Erbse in der Mitte eines Fußballstadions, reichte die Elektronenhülle bis zu den Tribünen. Dass sich Dinge trotzdem fest anfühlen, liegt an den elektrischen Kräften zwischen den Hüllen.",
        "An atom is almost empty: nearly all of its mass sits in the tiny nucleus, while the electrons form a cloud about 100,000 times larger. If the nucleus were a pea in the centre of a football stadium, the electron cloud would reach the stands. That things still feel solid is due to the electric forces between the electron clouds.",
    )
    val decay get() = l(
        "Radioaktive Kerne zerfallen zufällig – man kann nie sagen, wann ein bestimmter Kern zerfällt. Für viele Kerne zusammen gilt aber ein strenges Gesetz: Nach einer Halbwertszeit ist die Hälfte übrig, nach zwei ein Viertel, und so weiter. Halbwertszeiten reichen von Bruchteilen einer Sekunde bis zu Milliarden Jahren und machen Altersbestimmungen möglich.",
        "Radioactive nuclei decay at random – you can never say when a particular nucleus will decay. For many nuclei together, though, a strict law holds: after one half-life half is left, after two a quarter, and so on. Half-lives range from fractions of a second to billions of years and make it possible to date things.",
    )
    val binding get() = l(
        "Ein Atomkern wiegt weniger als seine einzelnen Protonen und Neutronen zusammen. Die fehlende Masse ist als Bindungsenergie freigeworden (E = mc²). Am festesten gebunden sind Kerne um Eisen – deshalb setzt Verschmelzen leichter Kerne (Sonne) und Spalten schwerer Kerne (Kernkraftwerk) Energie frei.",
        "An atomic nucleus weighs less than its separate protons and neutrons combined. The missing mass was released as binding energy (E = mc²). Nuclei around iron are the most tightly bound – that's why fusing light nuclei (the Sun) and splitting heavy ones (nuclear plants) both release energy.",
    )
    val bohr get() = l(
        "Im Bohrschen Atommodell dürfen Elektronen nur auf bestimmten Bahnen mit festen Energien kreisen. Springt ein Elektron von einer höheren auf eine niedrigere Bahn, sendet es ein Photon aus, dessen Energie genau dem Unterschied entspricht. Deshalb leuchtet jedes Element in ganz bestimmten Farben – wie ein Fingerabdruck, an dem man Elemente sogar in fernen Sternen erkennt.",
        "In Bohr's model of the atom, electrons may only circle on certain orbits with fixed energies. When an electron jumps from a higher to a lower orbit, it emits a photon whose energy equals exactly the difference. That's why every element glows in very specific colours – like a fingerprint that lets us recognise elements even in distant stars.",
    )
    val distances get() = l(
        "Astronomische Entfernungen sind so groß, dass man sie in Lichtjahren misst: der Weg, den Licht in einem Jahr zurücklegt, knapp 10 Billionen Kilometer. Der nächste Stern ist 4,2 Lichtjahre entfernt, die Milchstraße 100.000 Lichtjahre breit. Nahe Sterne vermisst man über die Parallaxe: Sie wackeln im Laufe eines Jahres ein winziges bisschen vor dem fernen Hintergrund.",
        "Astronomical distances are so large that we measure them in light years: the distance light travels in a year, almost 10 trillion kilometres. The nearest star is 4.2 light years away, the Milky Way 100,000 light years across. Nearby stars are measured by parallax: over a year they wobble a tiny bit against the distant background.",
    )
    val expansion get() = l(
        "Das Universum dehnt sich aus: Je weiter eine Galaxie entfernt ist, desto schneller entfernt sie sich (Hubble-Gesetz). Dabei fliegen die Galaxien nicht durch den Raum, sondern der Raum selbst wächst und dehnt auch das Licht – es wird röter (Rotverschiebung). Dreht man die Expansion zurück, landet man vor etwa 13,8 Milliarden Jahren beim Urknall.",
        "The universe is expanding: the farther away a galaxy is, the faster it recedes (Hubble's law). The galaxies aren't flying through space – space itself grows and stretches the light too, making it redder (redshift). Run the expansion backwards and you arrive at the Big Bang about 13.8 billion years ago.",
    )
    val stars get() = l(
        "Sterne leuchten, weil in ihrem Inneren Wasserstoff zu Helium verschmilzt. Ihre Farbe verrät ihre Oberflächentemperatur: Rote Sterne sind mit gut 3000 K eher kühl, die Sonne hat 5800 K, blaue Riesen über 20.000 K. Das folgt aus dem Wienschen Verschiebungsgesetz – je heißer ein Körper, desto kürzer die Wellenlänge, bei der er am hellsten strahlt.",
        "Stars shine because hydrogen fuses into helium in their cores. Their colour gives away their surface temperature: red stars are rather cool at just over 3000 K, the Sun has 5800 K, blue giants over 20,000 K. This follows from Wien's displacement law – the hotter an object, the shorter the wavelength at which it shines brightest.",
    )
    val blackhole get() = l(
        "Ein Schwarzes Loch entsteht, wenn Masse so stark zusammengedrückt wird, dass nicht einmal Licht entkommen kann. Die Grenze dazu ist der Ereignishorizont mit dem Schwarzschild-Radius 2GM/c². Die Sonne müsste dafür auf 3 km Radius schrumpfen, die Erde auf die Größe einer Murmel.",
        "A black hole forms when mass is squeezed so tightly that not even light can escape. The boundary is the event horizon with the Schwarzschild radius 2GM/c². The Sun would have to shrink to a radius of 3 km, the Earth to the size of a marble.",
    )
    val cmb get() = l(
        "Rund 380.000 Jahre nach dem Urknall war das Universum auf etwa 3000 K abgekühlt: Elektronen und Protonen wurden zu Atomen, und das Licht konnte frei fliegen. Dieses Licht erreicht uns heute als kosmische Hintergrundstrahlung – durch die Expansion um den Faktor 1100 gedehnt und auf 2,7 K abgekühlt. Weil Temperatur und Rotverschiebung genau zusammenhängen, kann man aus der Strahlung das junge Universum vermessen.",
        "Some 380,000 years after the Big Bang the universe had cooled to about 3000 K: electrons and protons combined into atoms and light could fly freely. That light reaches us today as the cosmic microwave background – stretched by a factor of 1100 by the expansion and cooled to 2.7 K. Because temperature and redshift are tied together exactly, the radiation lets us survey the young universe.",
    )
    val particles get() = l(
        "Zu jedem Teilchen gibt es ein Antiteilchen mit gleicher Masse und entgegengesetzter Ladung. Treffen beide aufeinander, zerstrahlen sie vollständig in Energie – meist zwei Photonen. Umgekehrt kann aus Energie ein Teilchen-Antiteilchen-Paar entstehen, wenn mindestens die doppelte Ruheenergie da ist. In PET-Scannern im Krankenhaus misst man genau diese Zerstrahlung.",
        "Every particle has an antiparticle with the same mass and opposite charge. When the two meet, they annihilate completely into energy – usually two photons. Conversely, energy can create a particle–antiparticle pair if at least twice the rest energy is available. Hospital PET scanners measure exactly this annihilation.",
    )
}

private object Fc5 {
    val gamma get() = card(l("Lorentz-Faktor", "Lorentz factor"), """§\gamma = \dfrac{1}{\sqrt{1 - v^2/c^2}}§""", l("Bewegte Uhren gehen um γ langsamer.", "Moving clocks run slow by γ."))
    val length get() = card(l("Längenkontraktion", "Length contraction"), """§L = \dfrac{L_0}{\gamma}§""", l("In Bewegungsrichtung.", "Along the direction of motion."))
    val emc2 get() = card(l("Masse und Energie", "Mass and energy"), """§E = m c^2§""", l("c = 3,0 · 10⁸ m/s.", "c = 3.0 · 10⁸ m/s."))
    val photon get() = card(l("Photonenenergie", "Photon energy"), """§E = h f = \dfrac{h c}{\lambda}§""", l("h = 6,63 · 10⁻³⁴ J s, 1 eV = 1,6 · 10⁻¹⁹ J.", "h = 6.63 · 10⁻³⁴ J s, 1 eV = 1.6 · 10⁻¹⁹ J."))
    val deBroglie get() = card(l("De-Broglie-Wellenlänge", "De Broglie wavelength"), """§\lambda = \dfrac{h}{m v}§""", l("Jedes Teilchen hat eine Wellenlänge.", "Every particle has a wavelength."))
    val heisenberg get() = card(l("Unschärferelation", "Uncertainty principle"), """§\Delta x \cdot \Delta p \geq \dfrac{\hbar}{2}§""", l("ħ = 1,055 · 10⁻³⁴ J s.", "ħ = 1.055 · 10⁻³⁴ J s."))
    val halfLife get() = card(l("Zerfallsgesetz", "Decay law"), """§N = N_0 \left(\tfrac{1}{2}\right)^{t/T_{1/2}}§""", l("Nach jeder Halbwertszeit die Hälfte.", "Half left after each half-life."))
    val rydberg get() = card(l("Wasserstoff-Linien", "Hydrogen lines"), """§\dfrac{1}{\lambda} = R_H \left(\dfrac{1}{n_1^2} - \dfrac{1}{n_2^2}\right)§""", l("R_H = 1,097 · 10⁷ 1/m.", "R_H = 1.097 · 10⁷ 1/m."))
    val hubble get() = card(l("Hubble-Gesetz", "Hubble's law"), """§v = H_0 \cdot d§""", l("H₀ ≈ 70 km/s pro Megaparsec.", "H₀ ≈ 70 km/s per megaparsec."))
    val wien get() = card(l("Wiensches Gesetz", "Wien's law"), """§\lambda_{max} \cdot T = 2{,}898 \cdot 10^{-3}\ \text{m K}§""", l("Wellenlänge der größten Helligkeit.", "Wavelength of peak brightness."))
    val schwarzschild get() = card(l("Schwarzschild-Radius", "Schwarzschild radius"), """§r_S = \dfrac{2 G M}{c^2}§""", l("Radius des Ereignishorizonts.", "Radius of the event horizon."))
}

/** Atomkern für Vorlagen. */
private class Nucleus(val name: String, val z: Int, val n: Int, val mass: Double)

object LabModern {
    val all: List<TaskDef> get() = relativity + quantum + nuclear + astro + particles

    // ===================================== RELATIVITÄT =====================================
    private val relativity = listOf(
        lab("lichtjahr", Topic.Relativity, E, 0.2f) {
            val years = tiered(listOf(1.0, 4.2, 8.6), 2.0, 25.0, 0.1)
            val km = years * LY / 1000
            task(
                question = l("Ein Stern ist ${n(years, 1)} ${if (years == 1.0) "Lichtjahr" else "Lichtjahre"} entfernt. Wie viele Kilometer sind das?", "A star is ${n(years, 1)} ${if (years == 1.0) "light year" else "light years"} away. How many kilometres is that?"),
                steps = listOf(num(l("Entfernung in Billionen km (10¹² km)", "Distance in trillions of km (10¹² km)"), km / 1e12, "", rel = 0.03, done = """§d \approx ${sci(km)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Ein Lichtjahr ist Lichtgeschwindigkeit mal ein Jahr in Sekunden.", "Key idea: a light year is the speed of light times one year in seconds."), """§1\ \text{Lj} = 3 \cdot 10^5\ \tfrac{\text{km}}{\text{s}} \cdot 3{,}156 \cdot 10^7\ \text{s} \approx 9{,}46 \cdot 10^{12}\ \text{km}§""".let { l(it, it.replace("Lj", "ly")) }),
                    formula = card(l("Lichtjahr", "Light year"), """§1\ \text{Lj} \approx 9{,}46 \cdot 10^{12}\ \text{km}§""".let { l(it, it.replace("Lj", "ly")) }, l("Strecke, die Licht in einem Jahr zurücklegt.", "Distance light travels in a year.")))),
                solution = listOf("""§d = ${m(years, 1)} \cdot 9{,}46 \cdot 10^{12}\ \text{km} \approx ${sci(km)}\ \text{km}§"""),
                background = Bg5.relativity,
            )
        },
        lab("lichtkonstant", Topic.Relativity, M, 0.3f) {
            val opts = l("Ein Raumschiff fliegt mit halber Lichtgeschwindigkeit und schaltet nach vorn einen Scheinwerfer ein. Wie schnell misst die Crew das Licht – und wie schnell ein Beobachter auf der Erde?", "A spaceship flying at half the speed of light switches on a headlight pointing forwards. How fast does the crew measure the light – and how fast an observer on Earth?") to listOf(
                l("Beide messen genau c.", "Both measure exactly c."),
                l("Crew c, Erde 1,5 c.", "Crew c, Earth 1.5 c."),
                l("Crew 0,5 c, Erde c.", "Crew 0.5 c, Earth c."),
                l("Crew 1,5 c, Erde c.", "Crew 1.5 c, Earth c."),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Das ist das zweite Postulat der speziellen Relativitätstheorie.", "Key idea: this is the second postulate of special relativity."), l("Geschwindigkeiten addieren sich nahe c nicht einfach.", "Near c, speeds don't simply add up.")), formula = Fc5.gamma)),
                solution = listOf(l("Licht ist in jedem Inertialsystem gleich schnell. Damit das gilt, müssen Zeit und Länge für verschiedene Beobachter verschieden sein.", "Light has the same speed in every inertial frame. For this to hold, time and length have to differ between observers.")),
                background = Bg5.relativity,
                fact = l("Michelson und Morley versuchten 1887, die Bewegung der Erde durch einen 'Äther' am Licht zu messen – und fanden nichts. Erst Einstein deutete das richtig.", "In 1887 Michelson and Morley tried to detect the Earth's motion through an 'aether' using light – and found nothing. Only Einstein interpreted that correctly."),
            )
        },
        lab("zeitdehnung", Topic.Relativity, H, 0.5f) {
            val beta = tiered(listOf(0.6, 0.8, 0.9), 0.5, 0.99, 0.01)
            val years = tiered(listOf(10.0, 20.0), 5.0, 40.0, 1.0)
            val gamma = 1 / sqrt(1 - beta * beta)
            val ship = years / gamma
            task(
                question = l("Ein Raumschiff fliegt mit ${n(beta)} c zu einem Stern. Von der Erde aus gesehen dauert die Reise ${n(years, 0)} Jahre.", "A spaceship flies to a star at ${n(beta)} c. Seen from Earth the trip takes ${n(years, 0)} years."),
                steps = listOf(
                    num(l("Lorentz-Faktor", "Lorentz factor"), gamma, "", rel = 0.02, done = """§\gamma \approx ${m(sig(gamma))}§""",
                        hints = listOf(l("Kernidee: γ sagt, um welchen Faktor bewegte Uhren langsamer gehen.", "Key idea: γ tells you by what factor moving clocks run slow."), """§\gamma = 1/\sqrt{1 - ${m(beta)}^2}§"""), formula = Fc5.gamma),
                    num(l("Wie viele Jahre altert die Crew?", "How many years does the crew age?"), ship, "a", rel = 0.03, done = """§\tau \approx ${m(sig(ship))}\ \text{a}§""",
                        hints = listOf(l("Die Bordzeit ist um γ kürzer.", "Ship time is shorter by γ."), """§\tau = t / \gamma§""")),
                ),
                solution = listOf("""§\gamma = \dfrac{1}{\sqrt{1 - ${m(beta * beta, 4)}}} \approx ${m(sig(gamma))}§""", """§\tau = \dfrac{${m(years, 0)}\ \text{a}}{${m(sig(gamma))}} \approx ${m(sig(ship))}\ \text{a}§"""),
                background = Bg5.relativity,
            )
        },
        lab("laengenkontraktion", Topic.Relativity, M, 0.4f) {
            val beta = tiered(listOf(0.6, 0.8), 0.3, 0.98, 0.01)
            val l0 = tiered(listOf(100.0, 200.0), 50.0, 500.0, 10.0)
            val len = l0 * sqrt(1 - beta * beta)
            task(
                question = l("Ein ${n(l0, 0)} m langes Raumschiff fliegt mit ${n(beta)} c an der Erde vorbei. Wie lang ist es für Beobachter auf der Erde?", "A spaceship ${n(l0, 0)} m long flies past the Earth at ${n(beta)} c. How long is it for observers on Earth?"),
                steps = listOf(num(l("Gemessene Länge", "Measured length"), len, "m", rel = 0.02, done = """§L \approx ${m(sig(len))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: In Bewegungsrichtung schrumpft die Länge um den Faktor γ.", "Key idea: along the direction of motion the length shrinks by the factor γ."), """§L = L_0 \sqrt{1 - v^2/c^2}§"""), formula = Fc5.length)),
                solution = listOf("""§L = ${m(l0, 0)}\ \text{m} \cdot \sqrt{1 - ${m(beta)}^2} \approx ${m(sig(len))}\ \text{m}§"""),
                background = Bg5.relativity,
            )
        },
        lab("emc2", Topic.Relativity, H, 0.4f) {
            val grams = tiered(listOf(1.0, 0.5), 0.1, 5.0, 0.1)
            val e = grams / 1000 * C * C
            val homes = e / 3.6e6 / 3500
            task(
                question = l("Angenommen, man könnte ${n(grams)} g Materie vollständig in Energie umwandeln.", "Suppose you could convert ${n(grams)} g of matter completely into energy."),
                steps = listOf(
                    num(l("Energie in Joule (als Vielfaches von 10¹³ J)", "Energy in joules (as a multiple of 10¹³ J)"), e / 1e13, "", rel = 0.02, done = """§E \approx ${sci(e)}\ \text{J}§""",
                        hints = listOf(l("Kernidee: E = mc² mit der Masse in Kilogramm.", "Key idea: E = mc² with the mass in kilograms."), """§E = ${sci(grams / 1000)}\ \text{kg} \cdot (3 \cdot 10^8\ \text{m/s})^2§"""), formula = Fc5.emc2),
                    num(l("Wie viele Haushalte (3500 kWh pro Jahr) könnte das ein Jahr lang versorgen?", "How many households (3500 kWh per year) could that supply for a year?"), homes, "", rel = 0.03, done = """§\approx ${m(sig(homes, 2))}§""",
                        hints = listOf(l("1 kWh = 3,6 · 10⁶ J.", "1 kWh = 3.6 · 10⁶ J."), """§N = \dfrac{E}{3500 \cdot 3{,}6 \cdot 10^6\ \text{J}}§""")),
                ),
                solution = listOf("""§E = ${sci(grams / 1000)} \cdot (2{,}998 \cdot 10^8)^2\ \text{J} \approx ${sci(e)}\ \text{J}§""", """§N = \dfrac{${sci(e)}}{1{,}26 \cdot 10^{10}} \approx ${m(sig(homes, 2))}§"""),
                background = Bg5.emc2,
            )
        },
        lab("geschwaddition", Topic.Relativity, X, 0.6f) {
            val v = tiered(listOf(0.5, 0.6), 0.3, 0.9, 0.05)
            val u = tiered(listOf(0.5, 0.8), 0.3, 0.95, 0.05)
            val w = (u + v) / (1 + u * v)
            task(
                question = l("Ein Raumschiff fliegt mit ${n(v)} c an der Erde vorbei und schießt eine Sonde mit ${n(u)} c (relativ zum Schiff) nach vorn. Wie schnell ist die Sonde von der Erde aus?", "A spaceship passes the Earth at ${n(v)} c and fires a probe forwards at ${n(u)} c (relative to the ship). How fast is the probe as seen from Earth?"),
                steps = listOf(num(l("Geschwindigkeit in Einheiten von c", "Speed in units of c"), w, "c", rel = 0.01, done = """§w \approx ${m(w, 3)}\, c§""",
                    hints = listOf(l("Kernidee: Nahe c addieren sich Geschwindigkeiten nicht einfach – das Ergebnis bleibt unter c.", "Key idea: near c velocities don't simply add – the result stays below c."), """§w = \dfrac{u + v}{1 + u v / c^2}§"""),
                    formula = card(l("Relativistische Addition", "Relativistic addition"), """§w = \dfrac{u + v}{1 + \dfrac{u v}{c^2}}§""", l("Klassisch wäre es einfach u + v.", "Classically it would just be u + v."))) ),
                solution = listOf("""§w = \dfrac{${m(u)} + ${m(v)}}{1 + ${m(u)} \cdot ${m(v)}}\, c \approx ${m(w, 3)}\, c§"""),
                background = Bg5.relativity,
            )
        },
        lab("photonenrakete", Topic.Relativity, X, 0.8f, master = true) {
            val d = tiered(listOf(4.2, 10.0), 4.0, 30.0, 0.1)
            val beta = tiered(listOf(0.9, 0.99), 0.8, 0.999, 0.001)
            val tEarth = d / beta
            val gamma = 1 / sqrt(1 - beta * beta)
            val tShip = tEarth / gamma
            task(
                question = l("Eine Rakete fliegt (Beschleunigung vernachlässigt) mit ${n(beta, 3)} c zu einem ${n(d, 1)} Lichtjahre entfernten Stern.", "A rocket flies (ignoring acceleration) at ${n(beta, 3)} c to a star ${n(d, 1)} light years away."),
                steps = listOf(
                    num(l("Reisedauer von der Erde aus (Jahre)", "Trip duration seen from Earth (years)"), tEarth, "a", rel = 0.02, done = """§t \approx ${m(sig(tEarth))}\ \text{a}§""",
                        hints = listOf(l("Kernidee: Von der Erde aus ist es einfach Weg durch Geschwindigkeit.", "Key idea: from Earth it's simply distance over speed."), """§t = d / v§""")),
                    num(l("Reisedauer an Bord (Jahre)", "Trip duration on board (years)"), tShip, "a", rel = 0.03, done = """§\tau \approx ${m(sig(tShip))}\ \text{a}§""",
                        hints = listOf(l("Für die Crew ist die Strecke um γ verkürzt – oder: ihre Uhren gehen um γ langsamer.", "For the crew the distance is shorter by γ – or: their clocks run slow by γ."), """§\tau = t/\gamma§"""), formula = Fc5.gamma),
                ),
                solution = listOf("""§t = \dfrac{${m(d, 1)}\ \text{Lj}}{${m(beta, 3)}\, c} \approx ${m(sig(tEarth))}\ \text{a}§""".let { l(it, it.replace("Lj", "ly")) }, """§\gamma \approx ${m(sig(gamma))},\ \tau = t/\gamma \approx ${m(sig(tShip))}\ \text{a}§"""),
                background = Bg5.relativity,
                fact = l("Mit konstant 1 g Beschleunigung könnte man rein theoretisch in einem Menschenleben (Bordzeit) bis ans Ende der Milchstraße reisen – auf der Erde wären dann aber zehntausende Jahre vergangen.", "With a constant 1 g acceleration you could, in theory, travel across the Milky Way within a human lifetime (ship time) – but tens of thousands of years would have passed on Earth."),
            )
        },
    )

    // ===================================== QUANTEN =====================================
    private val quantum = listOf(
        lab("photonenergie", Topic.Quantum, M, 0.4f) {
            val (col, lam) = pick(l("rotem", "red") to 650.0, l("grünem", "green") to 530.0, l("blauem", "blue") to 450.0, l("violettem", "violet") to 400.0)
            val e = H_PLANCK * C / (lam * 1e-9) / EV
            task(
                question = l("Welche Energie hat ein Photon von $col Licht mit ${n(lam, 0)} nm Wellenlänge?", "What energy does a photon of $col light with a wavelength of ${n(lam, 0)} nm have?"),
                steps = listOf(num(l("Energie in Elektronenvolt", "Energy in electron volts"), e, "eV", rel = 0.02, done = """§E \approx ${m(sig(e))}\ \text{eV}§""",
                    hints = listOf(l("Kernidee: E = h·c/λ, dann durch 1,6 · 10⁻¹⁹ J teilen.", "Key idea: E = h·c/λ, then divide by 1.6 · 10⁻¹⁹ J."), """§E = \dfrac{h c}{\lambda}§"""), formula = Fc5.photon)),
                solution = listOf("""§E = \dfrac{6{,}63 \cdot 10^{-34} \cdot 3 \cdot 10^8}{${sci(lam * 1e-9)}}\ \text{J} \approx ${sci(e * EV)}\ \text{J} \approx ${m(sig(e))}\ \text{eV}§"""),
                background = Bg5.photon,
                fact = l("Praktische Abkürzung: E in eV ≈ 1240 / λ in nm.", "Handy shortcut: E in eV ≈ 1240 / λ in nm."),
            )
        },
        lab("fotoeffekt", Topic.Quantum, M, 0.3f) {
            val opts = l("Rotes Licht kann aus einer Zinkplatte keine Elektronen herausschlagen – egal, wie hell es ist. Schwaches UV-Licht schafft es sofort. Warum?", "Red light can't knock electrons out of a zinc plate – no matter how bright it is. Weak UV light does it immediately. Why?") to listOf(
                l("Ein einzelnes rotes Photon hat zu wenig Energie; mehr Helligkeit heißt nur mehr Photonen.", "A single red photon has too little energy; more brightness only means more photons."),
                l("Rotes Licht wird vom Zink komplett reflektiert.", "Red light is completely reflected by zinc."),
                l("UV-Licht ist heller als rotes Licht.", "UV light is brighter than red light."),
                l("Rotes Licht ist zu langsam.", "Red light is too slow."),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Ein Elektron wird immer von genau einem Photon herausgeschlagen.", "Key idea: an electron is always knocked out by exactly one photon."), l("Die Energie eines Photons hängt nur von der Frequenz ab.", "A photon's energy depends only on its frequency.")), formula = Fc5.photon)),
                solution = listOf(l("Ob ein Elektron herauskommt, entscheidet die Energie des einzelnen Photons (E = h·f). Rote Photonen liegen unter der Austrittsarbeit von Zink (ca. 4,3 eV), UV-Photonen darüber.", "Whether an electron gets out depends on the energy of the single photon (E = h·f). Red photons are below zinc's work function (about 4.3 eV), UV photons above it.")),
                background = Bg5.photon,
            )
        },
        lab("debroglie", Topic.Quantum, H, 0.5f) {
            val u = tiered(listOf(100.0, 1000.0), 50.0, 10000.0, 50.0)
            val v = sqrt(2 * EV * u / ME)
            val lam = H_PLANCK / (ME * v)
            task(
                question = l("Ein Elektron wird mit ${n(u, 0)} V beschleunigt (aus der Ruhe). Welche De-Broglie-Wellenlänge hat es danach?", "An electron is accelerated from rest through ${n(u, 0)} V. What is its de Broglie wavelength afterwards?"),
                steps = listOf(num(l("Wellenlänge in Pikometern", "Wavelength in picometres"), lam * 1e12, "pm", rel = 0.03, done = """§\lambda \approx ${m(sig(lam * 1e12))}\ \text{pm}§""",
                    hints = listOf(l("Kernidee: Erst die Geschwindigkeit aus eU = ½mv², dann λ = h/(mv).", "Key idea: first the speed from eU = ½mv², then λ = h/(mv)."), """§\lambda = \dfrac{h}{\sqrt{2 m e U}}§"""), formula = Fc5.deBroglie)),
                solution = listOf("""§v = \sqrt{2 e U / m} \approx ${sci(v)}\ \tfrac{\text{m}}{\text{s}}§""", """§\lambda = \dfrac{6{,}63 \cdot 10^{-34}}{9{,}11 \cdot 10^{-31} \cdot ${sci(v)}}\ \text{m} \approx ${m(sig(lam * 1e12))}\ \text{pm}§"""),
                background = Bg5.matterWave,
            )
        },
        lab("laserphotonen", Topic.Quantum, H, 0.4f) {
            val pmW = tiered(listOf(1.0, 5.0), 0.5, 5.0, 0.1)
            val lam = pick(532.0, 650.0, 405.0)
            val nps = pmW / 1000 * lam * 1e-9 / (H_PLANCK * C)
            task(
                question = l("Ein Laserpointer strahlt ${n(pmW)} mW bei ${n(lam, 0)} nm ab. Wie viele Photonen verlassen ihn pro Sekunde?", "A laser pointer emits ${n(pmW)} mW at ${n(lam, 0)} nm. How many photons leave it per second?"),
                steps = listOf(num(l("Photonen pro Sekunde (als Vielfaches von 10¹⁵)", "Photons per second (as a multiple of 10¹⁵)"), nps / 1e15, "", rel = 0.03, done = """§N \approx ${sci(nps)}\ \text{s}^{-1}§""",
                    hints = listOf(l("Kernidee: Leistung durch Energie eines Photons.", "Key idea: power divided by the energy of one photon."), """§N = \dfrac{P \lambda}{h c}§"""), formula = Fc5.photon)),
                solution = listOf("""§E_{Ph} = \dfrac{h c}{\lambda} \approx ${sci(H_PLANCK * C / (lam * 1e-9))}\ \text{J}§""", """§N = \dfrac{${sci(pmW / 1000)}\ \text{W}}{${sci(H_PLANCK * C / (lam * 1e-9))}\ \text{J}} \approx ${sci(nps)}\ \text{s}^{-1}§"""),
                background = Bg5.photon,
            )
        },
        lab("unschaerfe", Topic.Quantum, X, 0.5f) {
            val dx = tiered(listOf(0.1, 0.05), 0.02, 1.0, 0.01) // nm
            val dp = HBAR / (2 * dx * 1e-9)
            val dv = dp / ME
            task(
                question = l("Ein Elektron ist auf einen Bereich von ${n(dx, 2)} nm eingesperrt (etwa so groß wie ein Atom). Wie groß ist mindestens die Unschärfe seiner Geschwindigkeit?", "An electron is confined to a region of ${n(dx, 2)} nm (about the size of an atom). What is the minimum uncertainty in its velocity?"),
                steps = listOf(num(l("Geschwindigkeitsunschärfe in km/s", "Velocity uncertainty in km/s"), dv / 1000, "km/s", rel = 0.03, done = """§\Delta v \approx ${m(sig(dv / 1000))}\ \text{km/s}§""",
                    hints = listOf(l("Kernidee: Δp ≥ ħ/(2Δx), dann durch die Masse teilen.", "Key idea: Δp ≥ ħ/(2Δx), then divide by the mass."), """§\Delta v = \dfrac{\hbar}{2 m\, \Delta x}§"""), formula = Fc5.heisenberg)),
                solution = listOf("""§\Delta v = \dfrac{1{,}055 \cdot 10^{-34}}{2 \cdot 9{,}11 \cdot 10^{-31} \cdot ${sci(dx * 1e-9)}}\ \tfrac{\text{m}}{\text{s}} \approx ${sci(dv)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = Bg5.uncertainty,
            )
        },
        lab("quantenkasten", Topic.Quantum, X, 0.6f) {
            val lnm = tiered(listOf(1.0, 0.5), 0.3, 2.0, 0.1)
            val n1 = 1
            val n2 = if (hard) 3 else 2
            val e1 = H_PLANCK * H_PLANCK / (8 * ME * (lnm * 1e-9).pow(2)) / EV
            val dE = e1 * (n2 * n2 - n1 * n1)
            val lam = 1239.84 / dE
            task(
                question = l("Ein Elektron sitzt in einem Kasten von ${n(lnm, 1)} nm Breite. Es springt von n = $n2 nach n = 1.", "An electron sits in a box ${n(lnm, 1)} nm wide. It jumps from n = $n2 to n = 1."),
                steps = listOf(
                    num(l("Grundzustandsenergie E₁ in eV", "Ground-state energy E₁ in eV"), e1, "eV", rel = 0.03, done = """§E_1 \approx ${m(sig(e1))}\ \text{eV}§""",
                        hints = listOf(l("Kernidee: Es passen nur stehende Wellen mit L = n·λ/2 hinein.", "Key idea: only standing waves with L = n·λ/2 fit in."), """§E_n = \dfrac{n^2 h^2}{8 m L^2}§"""),
                        formula = card(l("Teilchen im Kasten", "Particle in a box"), """§E_n = \dfrac{n^2 h^2}{8 m L^2}§""", l("n = 1, 2, 3, …", "n = 1, 2, 3, …"))),
                    num(l("Wellenlänge des ausgesandten Photons in nm", "Wavelength of the emitted photon in nm"), lam, "nm", rel = 0.03, done = """§\lambda \approx ${m(sig(lam))}\ \text{nm}§""",
                        hints = listOf(l("Energieunterschied (${n2 * n2 - 1}·E₁), dann λ = hc/ΔE.", "Energy difference (${n2 * n2 - 1}·E₁), then λ = hc/ΔE."), """§\lambda\ [\text{nm}] \approx \dfrac{1240}{\Delta E\ [\text{eV}]}§"""), formula = Fc5.photon),
                ),
                solution = listOf("""§E_1 = \dfrac{(6{,}63 \cdot 10^{-34})^2}{8 \cdot 9{,}11 \cdot 10^{-31} \cdot (${sci(lnm * 1e-9)})^2}\ \text{J} \approx ${m(sig(e1))}\ \text{eV}§""", """§\Delta E = ${n2 * n2 - 1} E_1 \approx ${m(sig(dE))}\ \text{eV},\ \lambda \approx ${m(sig(lam))}\ \text{nm}§"""),
                background = Bg5.box,
            )
        },
        lab("tunnel", Topic.Quantum, X, 0.8f, master = true) {
            val barrier = tiered(listOf(1.0, 0.5), 0.5, 2.0, 0.1) // eV über der Energie
            val width = tiered(listOf(0.3, 0.5), 0.2, 0.6, 0.05) // nm
            val kappa = sqrt(2 * ME * barrier * EV) / HBAR
            val t = exp(-2 * kappa * width * 1e-9)
            task(
                question = l("Ein Elektron trifft auf eine ${n(width, 2)} nm breite Barriere, die ${n(barrier, 1)} eV höher ist als seine Energie. Mit welcher Wahrscheinlichkeit tunnelt es (Näherung T ≈ e^(−2κL))?", "An electron meets a barrier ${n(width, 2)} nm wide that is ${n(barrier, 1)} eV above its energy. With what probability does it tunnel (approximation T ≈ e^(−2κL))?"),
                steps = listOf(
                    num(l("Abklingkonstante κ in 1/nm", "Decay constant κ in 1/nm"), kappa * 1e-9, "1/nm", rel = 0.02, done = """§\kappa \approx ${m(sig(kappa * 1e-9))}\ \text{nm}^{-1}§""",
                        hints = listOf(l("Kernidee: In der Barriere fällt die Welle exponentiell ab.", "Key idea: inside the barrier the wave decays exponentially."), """§\kappa = \dfrac{\sqrt{2 m (V - E)}}{\hbar}§"""),
                        formula = card(l("Tunneleffekt", "Tunnelling"), """§T \approx e^{-2 \kappa L}§""", l("Für breite, hohe Barrieren.", "For wide, high barriers."))),
                    num(l("Tunnelwahrscheinlichkeit in Prozent", "Tunnelling probability in percent"), t * 100, "%", rel = 0.05, done = """§T \approx ${m(sig(t * 100, 2), 6)}\ \text{%}§""",
                        hints = listOf(l("Exponent ausrechnen, dann e hoch.", "Work out the exponent, then e to that power."), """§T = e^{-2 \cdot ${m(sig(kappa * 1e-9))} \cdot ${m(width, 2)}}§""")),
                ),
                solution = listOf("""§\kappa = \dfrac{\sqrt{2 \cdot 9{,}11 \cdot 10^{-31} \cdot ${sci(barrier * EV)}}}{1{,}055 \cdot 10^{-34}}\ \text{m}^{-1} \approx ${m(sig(kappa * 1e-9))}\ \text{nm}^{-1}§""", """§T \approx e^{-${m(2 * kappa * width * 1e-9, 2)}} \approx ${sci(t)}§"""),
                background = Bg5.tunnel,
            )
        },
    )

    // ===================================== ATOME & KERNE =====================================
    private val nuclear = listOf(
        lab("atommodell", Topic.Nuclear, E, 0.3f) {
            val kern = tiered(listOf(1.0, 2.0, 5.0), 0.5, 10.0, 0.5) // cm
            val atom = kern * 1e5 / 100 // m
            task(
                question = l("Ein Atom ist etwa 100.000-mal so groß wie sein Kern. Wäre der Kern ${n(kern)} cm groß, wie groß wäre dann das ganze Atom?", "An atom is about 100,000 times larger than its nucleus. If the nucleus were ${n(kern)} cm across, how big would the whole atom be?"),
                steps = listOf(num(l("Größe des Atoms in Metern", "Size of the atom in metres"), atom, "m", rel = 0.02, done = """§d \approx ${m(sig(atom))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Einfach mal 100.000 – und dann in Meter umrechnen.", "Key idea: just times 100,000 – then convert to metres."), """§${m(kern)}\ \text{cm} \cdot 10^5§"""),
                    formula = card(l("Größenverhältnis", "Size ratio"), """§\dfrac{d_{Atom}}{d_{Kern}} \approx 10^5§""".let { l(it, it.replace("Atom}", "atom}").replace("Kern}", "nucleus}")) }, l("Atom etwa 10⁻¹⁰ m, Kern etwa 10⁻¹⁵ m.", "Atom about 10⁻¹⁰ m, nucleus about 10⁻¹⁵ m.")))),
                solution = listOf("""§${m(kern)}\ \text{cm} \cdot 100\,000 = ${m(kern * 1e5, 0)}\ \text{cm} = ${m(sig(atom))}\ \text{m}§"""),
                background = Bg5.atom,
            )
        },
        lab("halbwertszeit", Topic.Nuclear, M, 0.4f) {
            val t12 = pick(8.0, 30.0, 5730.0)
            val k = tiered(listOf(2.0, 3.0, 4.0), 1.0, 6.0, 0.5)
            val unit = when (t12) { 8.0 -> l("Tagen", "days"); 30.0 -> l("Jahren", "years"); else -> l("Jahren", "years") }
            val iso = when (t12) { 8.0 -> "Iod-131"; 30.0 -> "Cäsium-137"; else -> "C-14" }
            val left = 0.5.pow(k) * 100
            task(
                question = l("$iso hat eine Halbwertszeit von ${n(t12, 0)} $unit. Wie viel Prozent einer Probe sind nach ${n(k * t12, 0)} $unit noch übrig?", "${iso.replace("Cäsium", "Caesium").replace("Iod", "Iodine")} has a half-life of ${n(t12, 0)} $unit. What percentage of a sample is left after ${n(k * t12, 0)} $unit?"),
                steps = listOf(num(l("Übrig in Prozent", "Left in percent"), left, "%", rel = 0.02, done = """§\approx ${m(left, 2)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Zähle, wie viele Halbwertszeiten vergangen sind.", "Key idea: count how many half-lives have passed."), """§\left(\tfrac{1}{2}\right)^{${m(k)}}§"""), formula = Fc5.halfLife)),
                solution = listOf("""§\dfrac{t}{T_{1/2}} = ${m(k)},\quad \dfrac{N}{N_0} = \left(\tfrac{1}{2}\right)^{${m(k)}} \approx ${m(left / 100, 4)} = ${m(left, 2)}\ \text{%}§"""),
                background = Bg5.decay,
            )
        },
        lab("strahlenarten", Topic.Nuclear, E, 0.3f) {
            val opts = pick(
                l("Welche Strahlung wird schon von einem Blatt Papier fast vollständig gestoppt?", "Which radiation is almost completely stopped by a sheet of paper?") to listOf(l("Alphastrahlung", "Alpha radiation"), l("Betastrahlung", "Beta radiation"), l("Gammastrahlung", "Gamma radiation"), l("Röntgenstrahlung", "X-rays")),
                l("Welche Strahlung braucht dicke Blei- oder Betonwände zur Abschirmung?", "Which radiation needs thick lead or concrete walls for shielding?") to listOf(l("Gammastrahlung", "Gamma radiation"), l("Alphastrahlung", "Alpha radiation"), l("Betastrahlung", "Beta radiation"), l("Sichtbares Licht", "Visible light")),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Alpha sind schwere Heliumkerne, Beta Elektronen, Gamma energiereiches Licht.", "Key idea: alpha are heavy helium nuclei, beta are electrons, gamma is high-energy light."), l("Je schwerer und stärker geladen, desto schneller wird es abgebremst.", "The heavier and more charged, the faster it is stopped.")), formula = Fc5.halfLife)),
                solution = listOf(l("Alphateilchen (Heliumkerne) geben ihre Energie sehr schnell ab: Papier oder Haut reichen. Beta braucht einige Millimeter Aluminium, Gamma als ungeladene Strahlung dicke Blei- oder Betonschichten.", "Alpha particles (helium nuclei) lose their energy very quickly: paper or skin is enough. Beta needs a few millimetres of aluminium, gamma – being uncharged – thick layers of lead or concrete.")),
                background = Bg5.decay,
            )
        },
        lab("c14", Topic.Nuclear, H, 0.5f) {
            val pct = tiered(listOf(50.0, 25.0, 12.5), 5.0, 80.0, 1.0)
            val age = 5730 * ln(100 / pct) / ln(2.0)
            task(
                question = l("In einem Holzstück aus einer Ausgrabung ist nur noch ${n(pct, 1)} % des ursprünglichen C-14 vorhanden. Wie alt ist es? (Halbwertszeit 5730 Jahre)", "A piece of wood from a dig has only ${n(pct, 1)}% of its original C-14 left. How old is it? (Half-life 5730 years)"),
                steps = listOf(num(l("Alter in Jahren", "Age in years"), age, "a", rel = 0.02, done = """§t \approx ${m(sig(age))}\ \text{a}§""",
                    hints = listOf(l("Kernidee: Zerfallsgesetz nach t umstellen – mit dem Logarithmus.", "Key idea: rearrange the decay law for t – with a logarithm."), """§t = T_{1/2} \cdot \dfrac{\ln(N_0/N)}{\ln 2}§"""), formula = Fc5.halfLife)),
                solution = listOf("""§t = 5730\ \text{a} \cdot \dfrac{\ln(100/${m(pct, 1)})}{\ln 2} \approx ${m(sig(age))}\ \text{a}§"""),
                background = Bg5.decay,
                fact = l("Die Radiokarbonmethode funktioniert bis etwa 50.000 Jahre – danach ist zu wenig C-14 übrig, um es zuverlässig zu messen.", "Radiocarbon dating works up to about 50,000 years – after that too little C-14 is left to measure reliably."),
            )
        },
        lab("massendefekt", Topic.Nuclear, H, 0.6f) {
            val mp = 1.007276
            val mn = 1.008665
            val nuc = pick(Nucleus(l("Helium-4", "helium-4"), 2, 2, 4.001506), Nucleus(l("Lithium-7", "lithium-7"), 3, 4, 7.014358), Nucleus(l("Kohlenstoff-12", "carbon-12"), 6, 6, 11.996709))
            val z = nuc.z
            val nN = nuc.n
            val mNuc = nuc.mass
            val dm = z * mp + nN * mn - mNuc
            val e = dm * 931.5
            task(
                question = l("Ein ${nuc.name}-Kern hat die Masse ${n(mNuc, 6)} u. Wie groß ist seine Bindungsenergie?", "A ${nuc.name} nucleus has a mass of ${n(mNuc, 6)} u. What is its binding energy?"),
                given = listOf(l("""Proton §1{,}007276\ \text{u}§, Neutron §1{,}008665\ \text{u}§, §1\ \text{u} \cdot c^2 = 931{,}5\ \text{MeV}§""", """Proton §1{,}007276\ \text{u}§, neutron §1{,}008665\ \text{u}§, §1\ \text{u} \cdot c^2 = 931{,}5\ \text{MeV}§""")),
                steps = listOf(
                    num(l("Massendefekt in u", "Mass defect in u"), dm, "u", rel = 0.02, done = """§\Delta m \approx ${m(dm, 5)}\ \text{u}§""",
                        hints = listOf(l("Kernidee: Einzelteile zusammenzählen und den echten Kern abziehen.", "Key idea: add up the separate parts and subtract the real nucleus."), """§\Delta m = $z m_p + $nN m_n - m_K§"""), formula = Fc5.emc2),
                    num(l("Bindungsenergie in MeV", "Binding energy in MeV"), e, "MeV", rel = 0.02, done = """§E_B \approx ${m(sig(e))}\ \text{MeV}§""",
                        hints = listOf(l("Mit 931,5 MeV pro u umrechnen.", "Convert with 931.5 MeV per u."), """§E_B = \Delta m \cdot 931{,}5\ \tfrac{\text{MeV}}{\text{u}}§""")),
                ),
                solution = listOf("""§\Delta m = ${z} \cdot 1{,}007276 + ${nN} \cdot 1{,}008665 - ${m(mNuc, 6)} \approx ${m(dm, 5)}\ \text{u}§""", """§E_B \approx ${m(dm, 5)} \cdot 931{,}5\ \text{MeV} \approx ${m(sig(e))}\ \text{MeV}§"""),
                background = Bg5.binding,
            )
        },
        lab("uranspaltung", Topic.Nuclear, H, 0.5f) {
            val kg = tiered(listOf(1.0), 0.5, 5.0, 0.5)
            val atoms = kg * 1000 / 235 * 6.022e23
            val e = atoms * 200e6 * EV
            val coal = e / 30e6 / 1000 // t Kohle bei 30 MJ/kg
            task(
                question = l("Bei jeder Spaltung eines U-235-Kerns werden etwa 200 MeV frei. Wie viel Energie steckt in ${n(kg)} kg U-235?", "Each fission of a U-235 nucleus releases about 200 MeV. How much energy is in ${n(kg)} kg of U-235?"),
                given = listOf(l("""Molmasse §235\ \text{g/mol}§, §N_A = 6{,}022 \cdot 10^{23}\ \text{mol}^{-1}§, Steinkohle §30\ \text{MJ/kg}§""", """Molar mass §235\ \text{g/mol}§, §N_A = 6{,}022 \cdot 10^{23}\ \text{mol}^{-1}§, coal §30\ \text{MJ/kg}§""")),
                steps = listOf(
                    num(l("Energie in Terajoule (10¹² J)", "Energy in terajoules (10¹² J)"), e / 1e12, "TJ", rel = 0.03, done = """§E \approx ${m(sig(e / 1e12))}\ \text{TJ}§""",
                        hints = listOf(l("Kernidee: Anzahl der Kerne mal Energie pro Spaltung.", "Key idea: number of nuclei times energy per fission."), """§N = \dfrac{m}{235\ \text{g/mol}} \cdot N_A§"""), formula = Fc5.emc2),
                    num(l("Wie viele Tonnen Steinkohle liefern gleich viel Energie?", "How many tonnes of coal give the same energy?"), coal, "t", rel = 0.03, done = """§\approx ${m(sig(coal))}\ \text{t}§""",
                        hints = listOf(l("Energie durch 30 MJ pro kg.", "Energy divided by 30 MJ per kg."), """§m = E / (3 \cdot 10^7\ \text{J/kg})§""")),
                ),
                solution = listOf("""§N = \dfrac{${m(kg * 1000, 0)}}{235} \cdot 6{,}022 \cdot 10^{23} \approx ${sci(atoms)}§""", """§E = ${sci(atoms)} \cdot 200 \cdot 10^6 \cdot 1{,}602 \cdot 10^{-19}\ \text{J} \approx ${sci(e)}\ \text{J}§""", """§m_{Kohle} \approx ${m(sig(coal))}\ \text{t}§""".let { l(it, it.replace("Kohle", "coal")) }),
                background = Bg5.binding,
            )
        },
        lab("balmer", Topic.Nuclear, H, 0.6f) {
            val n2 = pick(3, 4, 5, 6)
            val inv = 1.097e7 * (1.0 / 4 - 1.0 / (n2 * n2))
            val lam = 1 / inv * 1e9
            val col = when (n2) { 3 -> l("rot", "red"); 4 -> l("türkis", "cyan"); 5 -> l("blau", "blue"); else -> l("violett", "violet") }
            task(
                question = l("Im Wasserstoffatom springt ein Elektron von der Bahn n = $n2 auf n = 2. Welche Wellenlänge hat das ausgesandte Licht?", "In a hydrogen atom an electron jumps from orbit n = $n2 to n = 2. What is the wavelength of the emitted light?"),
                steps = listOf(num(l("Wellenlänge in nm", "Wavelength in nm"), lam, "nm", rel = 0.01, done = """§\lambda \approx ${m(lam, 0)}\ \text{nm}\ (${col})§""",
                    hints = listOf(l("Kernidee: Die Rydberg-Formel liefert 1/λ für jeden Sprung.", "Key idea: the Rydberg formula gives 1/λ for every jump."), """§\dfrac{1}{\lambda} = R_H \left(\dfrac{1}{4} - \dfrac{1}{$n2^2}\right)§"""), formula = Fc5.rydberg)),
                solution = listOf("""§\dfrac{1}{\lambda} = 1{,}097 \cdot 10^7 \cdot \left(\dfrac{1}{4} - \dfrac{1}{${n2 * n2}}\right)\ \text{m}^{-1} \approx ${sci(inv)}\ \text{m}^{-1}§""", """§\lambda \approx ${m(lam, 0)}\ \text{nm}§"""),
                background = Bg5.bohr,
            )
        },
    )

    // ===================================== ASTRO =====================================
    private val astro = listOf(
        lab("mondfunk", Topic.Astro, E, 0.2f) {
            val (what, d) = pick(l("zum Mond", "to the Moon") to 3.844e8, l("zum Mars (mittlere Entfernung)", "to Mars (average distance)") to 2.25e11, l("zur Raumsonde Voyager 1 (etwa)", "to the Voyager 1 probe (roughly)") to 2.5e13)
            val t = 2 * d / C
            task(
                question = l("Ein Funkspruch geht von der Erde $what und die Antwort kommt sofort zurück. Wie lange wartet man auf die Antwort?", "A radio message goes from Earth $what and the reply comes straight back. How long do you wait for the answer?"),
                steps = listOf(num(l("Wartezeit in Sekunden", "Waiting time in seconds"), t, "s", rel = 0.03, done = """§t \approx ${m(sig(t))}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Funk ist Licht – hin und zurück.", "Key idea: radio is light – there and back."), """§t = \dfrac{2d}{c}§"""),
                    formula = card(l("Laufzeit", "Travel time"), """§t = \dfrac{d}{c}§""", l("c ≈ 3,0 · 10⁸ m/s.", "c ≈ 3.0 · 10⁸ m/s.")))),
                solution = listOf("""§t = \dfrac{2 \cdot ${sci(d)}\ \text{m}}{3 \cdot 10^8\ \text{m/s}} \approx ${m(sig(t))}\ \text{s}§"""),
                background = Bg5.distances,
            )
        },
        lab("parallaxe", Topic.Astro, M, 0.4f) {
            val p = tiered(listOf(0.1, 0.5, 0.77), 0.02, 0.8, 0.01)
            val pc = 1 / p
            val ly = pc * 3.26
            task(
                question = l("Ein Stern zeigt eine Parallaxe von ${n(p, 2)} Bogensekunden. Wie weit ist er entfernt?", "A star shows a parallax of ${n(p, 2)} arcseconds. How far away is it?"),
                steps = listOf(
                    num(l("Entfernung in Parsec", "Distance in parsecs"), pc, "pc", rel = 0.02, done = """§d \approx ${m(sig(pc))}\ \text{pc}§""",
                        hints = listOf(l("Kernidee: Parsec ist genau so definiert: 1 pc bei 1 Bogensekunde.", "Key idea: that's exactly how the parsec is defined: 1 pc at 1 arcsecond."), """§d = 1/p§"""),
                        formula = card(l("Parallaxe", "Parallax"), """§d\ [\text{pc}] = \dfrac{1}{p\ ['']}§""", l("1 pc ≈ 3,26 Lichtjahre.", "1 pc ≈ 3.26 light years."))),
                    num(l("In Lichtjahren", "In light years"), ly, l("Lj", "ly"), rel = 0.02, done = """§d \approx ${m(sig(ly))}\ \text{Lj}§""".let { l(it, it.replace("Lj", "ly")) },
                        hints = listOf(l("Mal 3,26.", "Times 3.26."), """§${m(sig(pc))} \cdot 3{,}26§""")),
                ),
                solution = listOf("""§d = \dfrac{1}{${m(p, 2)}}\ \text{pc} \approx ${m(sig(pc))}\ \text{pc} \approx ${m(sig(ly))}\ \text{Lj}§""".let { l(it, it.replace("Lj", "ly")) }),
                background = Bg5.distances,
            )
        },
        lab("sternenergie", Topic.Astro, M, 0.3f) {
            val opts = pick(
                l("Woher bekommt die Sonne ihre Energie?", "Where does the Sun get its energy from?") to listOf(
                    l("Kernfusion: Wasserstoff verschmilzt im Kern zu Helium.", "Nuclear fusion: hydrogen fuses into helium in the core."),
                    l("Sie verbrennt Gas wie ein riesiges Feuer.", "It burns gas like a giant fire."),
                    l("Kernspaltung von Uran.", "Nuclear fission of uranium."),
                    l("Sie schrumpft und wird dabei heiß.", "It shrinks and heats up in the process."),
                ),
                l("Warum funkeln Sterne, Planeten aber kaum?", "Why do stars twinkle but planets hardly do?") to listOf(
                    l("Sterne sind fast punktförmig, unruhige Luft lenkt ihr Licht hin und her; Planeten sind kleine Scheibchen.", "Stars are almost points, turbulent air wobbles their light; planets are small discs."),
                    l("Sterne pulsieren wirklich so schnell.", "Stars really pulse that fast."),
                    l("Planeten sind näher und deshalb heller.", "Planets are closer and therefore brighter."),
                    l("Planeten reflektieren nur Licht, Sterne erzeugen es.", "Planets only reflect light, stars produce it."),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Denk an das, was Sterne von allem anderen am Himmel unterscheidet.", "Key idea: think about what sets stars apart from everything else in the sky."), l("Chemisches Feuer hätte die Sonne nur für einige tausend Jahre am Leben gehalten.", "A chemical fire would have kept the Sun going for only a few thousand years.")), formula = Fc5.emc2)),
                solution = listOf(l("Im Kern der Sonne (15 Millionen Grad) verschmelzen Wasserstoffkerne zu Helium; der Massenunterschied wird nach E = mc² zu Energie. Und Sterne funkeln, weil ihr fast punktförmiges Licht von Luftschlieren abgelenkt wird.", "In the Sun's core (15 million degrees) hydrogen nuclei fuse into helium; the mass difference becomes energy via E = mc². And stars twinkle because their almost point-like light is deflected by pockets of moving air.")),
                background = Bg5.stars,
            )
        },
        lab("hubble", Topic.Astro, H, 0.5f) {
            val z = tiered(listOf(0.01, 0.02, 0.05), 0.005, 0.08, 0.001)
            val v = z * 299792.0
            val d = v / 70
            task(
                question = l("Das Licht einer Galaxie ist um z = ${n(z, 3)} rotverschoben. Wie weit ist sie ungefähr entfernt (H₀ = 70 km/s pro Mpc, z klein)?", "The light of a galaxy is redshifted by z = ${n(z, 3)}. Roughly how far away is it (H₀ = 70 km/s per Mpc, small z)?"),
                steps = listOf(
                    num(l("Fluchtgeschwindigkeit in km/s", "Recession speed in km/s"), v, "km/s", rel = 0.02, done = """§v \approx ${m(sig(v))}\ \text{km/s}§""",
                        hints = listOf(l("Kernidee: Für kleine z ist v ≈ c · z.", "Key idea: for small z, v ≈ c · z."), """§v = c \cdot z§""")),
                    num(l("Entfernung in Megaparsec", "Distance in megaparsecs"), d, "Mpc", rel = 0.03, done = """§d \approx ${m(sig(d))}\ \text{Mpc}§""",
                        hints = listOf(l("Hubble-Gesetz nach d umstellen.", "Rearrange Hubble's law for d."), """§d = v / H_0§"""), formula = Fc5.hubble),
                ),
                solution = listOf("""§v = 3 \cdot 10^5\ \tfrac{\text{km}}{\text{s}} \cdot ${m(z, 3)} \approx ${m(sig(v))}\ \tfrac{\text{km}}{\text{s}}§""", """§d = \dfrac{${m(sig(v))}}{70}\ \text{Mpc} \approx ${m(sig(d))}\ \text{Mpc}§"""),
                background = Bg5.expansion,
            )
        },
        lab("schwarzschild", Topic.Astro, H, 0.4f) {
            val (what, mass) = pick(l("die Sonne", "the Sun") to M_SUN, l("die Erde", "the Earth") to 5.972e24, l("ein Stern mit 10 Sonnenmassen", "a star of 10 solar masses") to 10 * M_SUN, l("der Mond", "the Moon") to 7.35e22)
            val rs = 2 * GRAV * mass / (C * C)
            task(
                question = l("Auf welchen Radius müsste $what zusammengedrückt werden, um ein Schwarzes Loch zu werden?", "To what radius would $what have to be squeezed to become a black hole?"),
                given = listOf("""§M = ${sci(mass)}\ \text{kg}§"""),
                steps = listOf(num(l("Schwarzschild-Radius in Metern", "Schwarzschild radius in metres"), rs, "m", rel = 0.02, done = """§r_S \approx ${if (rs >= 1000) m(sig(rs / 1000)) + "\\ \\text{km}" else m(sig(rs * 1000)) + "\\ \\text{mm}"}§""",
                    hints = listOf(l("Kernidee: Am Schwarzschild-Radius wäre die Fluchtgeschwindigkeit gleich c.", "Key idea: at the Schwarzschild radius the escape velocity would equal c."), """§r_S = 2 G M / c^2§"""), formula = Fc5.schwarzschild)),
                solution = listOf("""§r_S = \dfrac{2 \cdot 6{,}674 \cdot 10^{-11} \cdot ${sci(mass)}}{(2{,}998 \cdot 10^8)^2}\ \text{m} \approx ${sci(rs)}\ \text{m}§"""),
                background = Bg5.blackhole,
            )
        },
        lab("sternfarbe", Topic.Astro, H, 0.4f) {
            val (name, t) = pick(l("Beteigeuze (rötlich)", "Betelgeuse (reddish)") to 3500.0, l("die Sonne", "the Sun") to 5800.0, l("Sirius (weiß-bläulich)", "Sirius (bluish white)") to 9900.0, l("Rigel (blau)", "Rigel (blue)") to 12100.0)
            val lam = 2.898e-3 / t * 1e9
            task(
                question = l("Die Oberfläche von $name hat etwa ${n(t, 0)} K. Bei welcher Wellenlänge strahlt sie am hellsten?", "The surface of $name has about ${n(t, 0)} K. At what wavelength does it shine brightest?"),
                steps = listOf(num(l("Wellenlänge in nm", "Wavelength in nm"), lam, "nm", rel = 0.02, done = """§\lambda_{max} \approx ${m(sig(lam))}\ \text{nm}§""",
                    hints = listOf(l("Kernidee: Heißer heißt kürzere Wellenlänge – Wiensches Gesetz.", "Key idea: hotter means shorter wavelength – Wien's law."), """§\lambda_{max} = \dfrac{2{,}898 \cdot 10^{-3}\ \text{m K}}{T}§"""), formula = Fc5.wien)),
                solution = listOf("""§\lambda_{max} = \dfrac{2{,}898 \cdot 10^{-3}}{${m(t, 0)}}\ \text{m} \approx ${m(sig(lam))}\ \text{nm}§"""),
                background = Bg5.stars,
            )
        },
        lab("hubblezeit", Topic.Astro, H, 0.5f) {
            val h0 = tiered(listOf(70.0), 67.0, 74.0, 0.5)
            val mpc = 3.086e19 // km
            val tSec = mpc / h0
            val gyr = tSec / 3.156e16
            task(
                question = l("Wie alt wäre das Universum, wenn es sich immer gleich schnell ausgedehnt hätte? Nimm H₀ = ${n(h0, 1)} km/s pro Megaparsec.", "How old would the universe be if it had always expanded at the same rate? Take H₀ = ${n(h0, 1)} km/s per megaparsec."),
                given = listOf("""§1\ \text{Mpc} = 3{,}086 \cdot 10^{19}\ \text{km},\ 1\ \text{Mrd. Jahre} = 3{,}156 \cdot 10^{16}\ \text{s}§""".let { l(it, it.replace("Mrd. Jahre", "Gyr")) }),
                steps = listOf(num(l("Hubble-Zeit in Milliarden Jahren", "Hubble time in billions of years"), gyr, l("Mrd. a", "Gyr"), rel = 0.02, done = """§t_H \approx ${m(sig(gyr))}\ \text{Mrd. Jahre}§""".let { l(it, it.replace("Mrd. Jahre", "Gyr")) },
                    hints = listOf(l("Kernidee: Zurückgerechnet: Abstand durch Geschwindigkeit ist für alle Galaxien gleich 1/H₀.", "Key idea: running time backwards, distance over speed is the same 1/H₀ for all galaxies."), """§t_H = \dfrac{1}{H_0}§"""), formula = Fc5.hubble)),
                solution = listOf("""§t_H = \dfrac{3{,}086 \cdot 10^{19}\ \text{km}}{${m(h0, 1)}\ \text{km/s}} \approx ${sci(tSec)}\ \text{s} \approx ${m(sig(gyr))}\ \text{Mrd. Jahre}§""".let { l(it, it.replace("Mrd. Jahre", "Gyr")) }),
                background = Bg5.expansion,
                fact = l("Das tatsächliche Alter (13,8 Mrd. Jahre) liegt erstaunlich nah daran – weil sich Abbremsen durch Materie und Beschleunigen durch Dunkle Energie fast ausgleichen.", "The actual age (13.8 billion years) is remarkably close – because braking by matter and acceleration by dark energy almost cancel out."),
            )
        },
        lab("hintergrund", Topic.Astro, X, 0.7f, master = true) {
            val z = tiered(listOf(1100.0), 1000.0, 1200.0, 10.0)
            val t0 = 2.725
            val t = t0 * (1 + z)
            val lamNow = 2.898e-3 / t0 * 1000 // mm
            task(
                question = l("Die kosmische Hintergrundstrahlung hat heute ${n(t0, 3)} K. Sie wurde ausgesandt, als das Universum um den Faktor 1 + z = ${n(1 + z, 0)} kleiner war.", "The cosmic microwave background is ${n(t0, 3)} K today. It was emitted when the universe was smaller by a factor of 1 + z = ${n(1 + z, 0)}."),
                steps = listOf(
                    num(l("Temperatur damals in Kelvin", "Temperature back then in kelvin"), t, "K", rel = 0.02, done = """§T \approx ${m(sig(t))}\ \text{K}§""",
                        hints = listOf(l("Kernidee: Die Expansion dehnt jede Wellenlänge – die Temperatur sinkt im selben Maß.", "Key idea: expansion stretches every wavelength – the temperature drops by the same factor."), """§T = T_0 (1 + z)§"""),
                        formula = card(l("Kosmische Abkühlung", "Cosmic cooling"), """§T \propto \dfrac{1}{a},\quad 1 + z = \dfrac{a_0}{a}§""", l("a = Skalenfaktor des Universums.", "a = scale factor of the universe."))),
                    num(l("Bei welcher Wellenlänge strahlt sie heute am hellsten (mm)?", "At what wavelength does it shine brightest today (mm)?"), lamNow, "mm", rel = 0.02, done = """§\lambda_{max} \approx ${m(sig(lamNow))}\ \text{mm}§""",
                        hints = listOf(l("Wiensches Gesetz mit der heutigen Temperatur.", "Wien's law with today's temperature."), """§\lambda_{max} = 2{,}898 \cdot 10^{-3}\ \text{m K} / T_0§"""), formula = Fc5.wien),
                ),
                solution = listOf("""§T = ${m(t0, 3)}\ \text{K} \cdot ${m(1 + z, 0)} \approx ${m(sig(t))}\ \text{K}§""", """§\lambda_{max} = \dfrac{2{,}898 \cdot 10^{-3}}{${m(t0, 3)}}\ \text{m} \approx ${m(sig(lamNow))}\ \text{mm}§"""),
                background = Bg5.cmb,
            )
        },
    )

    // ===================================== TEILCHEN =====================================
    private val particles = listOf(
        lab("zerstrahlung", Topic.Modern, H, 0.4f) {
            val e = ME * C * C / EV / 1000
            task(
                question = l("Ein Elektron und ein Positron (sein Antiteilchen) treffen sich in Ruhe und zerstrahlen in zwei Photonen. Welche Energie hat jedes Photon?", "An electron and a positron (its antiparticle) meet at rest and annihilate into two photons. What energy does each photon have?"),
                given = listOf("""§m_e = 9{,}109 \cdot 10^{-31}\ \text{kg}§"""),
                steps = listOf(num(l("Energie pro Photon in keV", "Energy per photon in keV"), e, "keV", rel = 0.02, done = """§E_\gamma \approx ${m(sig(e))}\ \text{keV}§""",
                    hints = listOf(l("Kernidee: Die ganze Ruheenergie beider Teilchen wird zu Licht – geteilt auf zwei gleiche Photonen.", "Key idea: the entire rest energy of both particles becomes light – shared between two equal photons."), """§E_\gamma = m_e c^2§"""), formula = Fc5.emc2)),
                solution = listOf("""§E_\gamma = 9{,}109 \cdot 10^{-31} \cdot (2{,}998 \cdot 10^8)^2\ \text{J} \approx ${sci(ME * C * C)}\ \text{J} \approx ${m(sig(e))}\ \text{keV}§"""),
                background = Bg5.particles,
            )
        },
        lab("paarbildung", Topic.Modern, H, 0.5f) {
            val (part, mass) = pick(l("ein Elektron-Positron-Paar", "an electron–positron pair") to ME, l("ein Myon-Antimyon-Paar", "a muon–antimuon pair") to 1.884e-28, l("ein Proton-Antiproton-Paar", "a proton–antiproton pair") to 1.673e-27)
            val e = 2 * mass * C * C / EV / 1e6
            task(
                question = l("Wie viel Energie braucht ein Photon mindestens, um $part zu erzeugen (Rückstoß an einem schweren Kern vernachlässigt)?", "What minimum energy does a photon need to create $part (neglecting recoil on a heavy nucleus)?"),
                given = listOf("""§m = ${sci(mass)}\ \text{kg}§"""),
                steps = listOf(num(l("Mindestenergie in MeV", "Minimum energy in MeV"), e, "MeV", rel = 0.02, done = """§E_{min} \approx ${m(sig(e))}\ \text{MeV}§""",
                    hints = listOf(l("Kernidee: Es müssen zwei Teilchen mit Ruheenergie entstehen.", "Key idea: two particles with rest energy have to be created."), """§E_{min} = 2 m c^2§"""), formula = Fc5.emc2)),
                solution = listOf("""§E_{min} = 2 \cdot ${sci(mass)} \cdot (2{,}998 \cdot 10^8)^2\ \text{J} \approx ${m(sig(e))}\ \text{MeV}§"""),
                background = Bg5.particles,
            )
        },
        lab("quarks", Topic.Modern, M, 0.3f) {
            val opts = pick(
                l("Aus welchen Quarks besteht ein Proton (Ladung +1)?", "Which quarks make up a proton (charge +1)?") to listOf(l("up, up, down", "up, up, down"), l("up, down, down", "up, down, down"), l("up, up, up", "up, up, up"), l("down, down, down", "down, down, down")),
                l("Aus welchen Quarks besteht ein Neutron (Ladung 0)?", "Which quarks make up a neutron (charge 0)?") to listOf(l("up, down, down", "up, down, down"), l("up, up, down", "up, up, down"), l("down, down, down", "down, down, down"), l("up, up, up", "up, up, up")),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Welche Kombination?", "Which combination?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Ein up-Quark hat die Ladung +2/3, ein down-Quark −1/3.", "Key idea: an up quark has charge +2/3, a down quark −1/3."), l("Addiere die Ladungen.", "Add up the charges.")),
                    formula = card(l("Quarkladungen", "Quark charges"), """§q_u = +\tfrac{2}{3}\,e,\quad q_d = -\tfrac{1}{3}\,e§""", l("Drei Quarks bilden ein Proton oder Neutron.", "Three quarks form a proton or neutron.")))),
                solution = listOf(l("Proton: 2/3 + 2/3 − 1/3 = +1. Neutron: 2/3 − 1/3 − 1/3 = 0.", "Proton: 2/3 + 2/3 − 1/3 = +1. Neutron: 2/3 − 1/3 − 1/3 = 0.")),
                background = l(
                    "Protonen und Neutronen sind nicht elementar: Sie bestehen aus je drei Quarks, zusammengehalten von Gluonen. Quarks hat man nie einzeln beobachtet – versucht man sie zu trennen, entsteht aus der Energie ein neues Quark-Antiquark-Paar. Die Masse des Protons stammt übrigens fast ganz aus dieser Bindungsenergie, nicht aus den Quarks selbst.",
                    "Protons and neutrons are not elementary: each consists of three quarks, held together by gluons. Quarks have never been seen alone – if you try to pull them apart, the energy creates a new quark–antiquark pair. Most of the proton's mass comes from this binding energy, not from the quarks themselves.",
                ),
            )
        },
    )
}
