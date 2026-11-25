package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

/*
 * Physik-Ausbau (seit 13.2): Astrophysik & Kosmos, Teilchen. Anregungen zur Kosmologie (nicht wörtlich):
 * Trodden & Carroll, TASI Lectures: Introduction to Cosmology (astro-ph/0401547). Alle Texte selbst geschrieben.
 */

private const val GR7 = 6.674e-11
private const val EV7 = 1.602e-19
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object PhysNew7 {
    val all: List<TaskDef> get() = astro + particles

    private val astro = listOf(
        lab("phy.jahreszeiten", Topic.Astro, E, 0.5f) {
            task(
                question = l("Im Januar ist die Erde der Sonne am nächsten, rund 5 Millionen km näher als im Juli. Trotzdem ist bei uns im Januar Winter. Woher kommen die Jahreszeiten?", "In January the Earth is closest to the Sun, about 5 million km closer than in July. Yet January is winter here. Where do the seasons come from?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Die Erdachse ist um 23,4° geneigt. Im Sommer steht die Sonne bei uns höher und scheint länger.", "The Earth's axis is tilted by 23.4°. In summer the Sun stands higher for us and shines for longer."),
                    listOf(l("Die Entfernung zur Sonne ändert sich, und im Winter sind wir weiter weg.", "The distance to the Sun changes, and in winter we are farther away."), l("Die Sonne strahlt im Winter schwächer.", "The Sun shines more weakly in winter."), l("Im Winter schiebt sich der Mond öfter vor die Sonne.", "In winter the Moon passes in front of the Sun more often.")),
                    hints = listOf(l("Kernidee: Wenn es an der Entfernung läge, hätten Nord- und Südhalbkugel gleichzeitig Sommer. Ist das so?", "Key idea: if it were down to distance, the northern and southern hemispheres would have summer at the same time. Is that so?"), l("Flach einfallendes Licht verteilt sich auf eine größere Fläche.", "Light arriving at a shallow angle spreads over a larger area.")))),
                solution = listOf(l("Wegen der Neigung der Erdachse ist mal die Nord-, mal die Südhalbkugel der Sonne zugewandt. Die Sonne steht dann höher, ihr Licht trifft steiler auf, und die Tage sind länger. Deshalb ist in Australien Sommer, wenn bei uns Winter ist.", "Because of the tilt of the Earth's axis, sometimes the northern and sometimes the southern hemisphere faces the Sun. The Sun then stands higher, its light strikes more steeply, and the days are longer. That's why it's summer in Australia when it's winter here.")),
                background = l("Ein Lichtbündel, das steil auftrifft, heizt eine kleine Fläche stark. Dasselbe Bündel unter flachem Winkel verteilt sich auf eine viel größere Fläche. Dazu kommt die Tageslänge: In Deutschland scheint die Sonne im Juni über 16 Stunden, im Dezember keine 8. Der Abstand zur Sonne schwankt nur um 3 % und spielt kaum eine Rolle.", "A beam of light striking steeply heats a small area strongly. The same beam at a shallow angle spreads over a much larger area. Then there's the length of the day: in Germany the Sun shines for over 16 hours in June and less than 8 in December. The distance to the Sun varies by only 3% and hardly matters."),
                fact = l("Die Achsneigung entstand vermutlich, als vor 4,5 Milliarden Jahren ein marsgroßer Körper die junge Erde traf. Aus den Trümmern bildete sich der Mond.", "The axial tilt probably arose when a Mars-sized body hit the young Earth 4.5 billion years ago. The Moon formed from the debris."),
            )
        },
        lab("phy.sonnenzukunft", Topic.Astro, E, 0.5f) {
            task(
                question = l("In etwa 5 Milliarden Jahren ist der Wasserstoff im Kern der Sonne verbraucht. Was wird dann aus ihr?", "In about 5 billion years the hydrogen in the Sun's core will be used up. What will become of it then?"),
                steps = listOf(pick1(l("Die Sonne wird …", "The Sun will …"), l("zum Roten Riesen und danach zu einem Weißen Zwerg", "become a red giant and then a white dwarf"), listOf(l("in einer Supernova explodieren", "explode in a supernova"), l("zu einem Schwarzen Loch zusammenstürzen", "collapse into a black hole"), l("einfach ausgehen und unsichtbar werden", "simply go out and become invisible")),
                    hints = listOf(l("Kernidee: Das Ende eines Sterns hängt fast nur von seiner Masse ab.", "Key idea: the end of a star depends almost entirely on its mass."), l("Für Supernovae und Schwarze Löcher braucht ein Stern mindestens etwa achtmal die Masse der Sonne.", "For a supernova or a black hole a star needs at least about eight times the mass of the Sun.")))),
                solution = listOf(l("Die Sonne bläht sich zum Roten Riesen auf, der vielleicht bis zur Erdbahn reicht, und stößt ihre äußeren Schichten als leuchtenden Nebel ab. Übrig bleibt ein heißer, erdgroßer Kern, ein Weißer Zwerg, der langsam auskühlt.", "The Sun will swell into a red giant, perhaps reaching as far as the Earth's orbit, and shed its outer layers as a glowing nebula. What remains is a hot, Earth-sized core, a white dwarf, which slowly cools.")),
                background = l("Leichte Sterne wie die Sonne enden friedlich. Ist der Wasserstoff im Kern verbraucht, zieht der Kern sich zusammen und wird heißer, während die Hülle sich ausdehnt. Danach verbrennt sie noch eine Weile Helium zu Kohlenstoff. Für mehr reicht ihre Masse nicht, und ohne neue Energie bleibt ein Weißer Zwerg aus Kohlenstoff und Sauerstoff.", "Light stars like the Sun end peacefully. When the hydrogen in the core is used up, the core contracts and gets hotter while the envelope expands. After that it burns helium into carbon for a while. Its mass isn't enough for anything more, and without new energy a white dwarf of carbon and oxygen remains."),
                fact = l("Viel früher wird es ungemütlich: In etwa einer Milliarde Jahren strahlt die Sonne so viel heller, dass die Ozeane verdampfen.", "It gets uncomfortable much sooner: in about a billion years the Sun will shine so much more brightly that the oceans evaporate."),
            )
        },
        lab("phy.neptunlicht", Topic.Astro, E, 0.4f) {
            val (planet, au) = pick(l("Neptun", "Neptune") to 30.0, l("Jupiter", "Jupiter") to 5.2, l("Saturn", "Saturn") to 9.5)
            val f = au * au
            task(
                question = l("$planet ist ${n(au, 1)}-mal so weit von der Sonne entfernt wie die Erde. Wie viel mal schwächer ist das Sonnenlicht dort?", "$planet is ${n(au, 1)} times as far from the Sun as the Earth. How many times weaker is the sunlight there?"),
                steps = listOf(num(l("Faktor", "Factor"), f, "", rel = 0.02, done = """§${m(f, 0)}§""",
                    hints = listOf(l("Kernidee: Das Licht verteilt sich auf eine Kugeloberfläche, die mit dem Quadrat des Abstands wächst.", "Key idea: the light spreads over the surface of a sphere, which grows with the square of the distance."), """§\dfrac{I_E}{I_P} = \left(\dfrac{r_P}{r_E}\right)^2§"""))),
                solution = listOf("""§${m(au, 1)}^2 \approx ${m(f, 0)}§"""),
                background = l("Das Abstandsgesetz 1/r² gilt für alles, was sich von einem Punkt nach allen Seiten ausbreitet: Licht, Schall, Gravitation. Doppelte Entfernung heißt ein Viertel. Deshalb fliegen Raumsonden zu den äußeren Planeten nicht mit Solarzellen, sondern mit Radionuklidbatterien, die Wärme aus radioaktivem Zerfall in Strom wandeln.", "The inverse-square law 1/r² applies to anything that spreads out in all directions from a point: light, sound, gravity. Twice the distance means a quarter. That's why space probes to the outer planets don't run on solar cells but on radioisotope batteries, which turn heat from radioactive decay into electricity."),
                fact = l("Selbst auf Neptun ist die Sonne noch viel heller als der Vollmond bei uns, sie wäre dort ein grell leuchtender Punkt.", "Even at Neptune the Sun is still much brighter than the full Moon is for us; there it would be a dazzling point of light."),
            )
        },
        lab("phy.sonnenleistung", Topic.Astro, M, 0.5f) {
            val s = 1361.0; val r = 6.371e6; val p = s * PI * r * r; val use = 1.9e13
            task(
                question = l("Auf jeden Quadratmeter, der senkrecht zur Sonne steht, kommen außerhalb der Atmosphäre 1361 W (Solarkonstante). Die Erde hat 6371 km Radius. Die Menschheit verbraucht im Mittel etwa 19 TW (1,9 · 10¹³ W).", "Outside the atmosphere, every square metre facing the Sun receives 1361 W (the solar constant). The Earth has a radius of 6371 km. Humanity uses about 19 TW (1.9 · 10¹³ W) on average."),
                steps = listOf(
                    big(l("Welche Leistung fängt die Erde insgesamt auf?", "What total power does the Earth intercept?"), p, "W", rel = 0.02, done = """§P \approx ${sci(p)}\ \text{W}§""",
                        hints = listOf(l("Kernidee: Die Erde fängt so viel Licht auf wie eine Scheibe mit ihrem Radius, nicht wie ihre ganze Oberfläche.", "Key idea: the Earth intercepts as much light as a disc with its radius, not as much as its whole surface."), """§P = S \cdot \pi R^2§""")),
                    num(l("Wie viel mal so viel ist das wie der Verbrauch der Menschheit?", "How many times humanity's consumption is that?"), p / use, "", rel = 0.03, done = """§\approx ${m(sig(p / use))}§""",
                        hints = listOf(l("Einfach teilen.", "Just divide."), """§\dfrac{P}{1{,}9 \cdot 10^{13}}§""")),
                ),
                solution = listOf("""§P = 1361 \cdot \pi \cdot (6{,}371 \cdot 10^6)^2\ \text{W} \approx ${sci(p)}\ \text{W}§""", """§\dfrac{${sci(p)}}{1{,}9 \cdot 10^{13}} \approx ${m(sig(p / use))}§"""),
                background = l("Die Sonne liefert der Erde ein Vielfaches dessen, was wir brauchen. Ein Teil wird zwar gleich ins All zurückgespiegelt, und der Rest verteilt sich auf Tag und Nacht, Meere und Wüsten. Trotzdem würde schon ein winziger Bruchteil der Landfläche mit Solaranlagen genügen. Das Problem ist nicht die Menge, sondern Speicherung und Verteilung.", "The Sun supplies the Earth with many times what we need. Some of it is reflected straight back into space, and the rest is spread over day and night, oceans and deserts. Even so, a tiny fraction of the land area covered with solar panels would be enough. The problem isn't the amount but storage and distribution."),
                fact = l("Fast alle Energie, die wir nutzen, ist letztlich Sonnenenergie: Kohle, Öl und Gas sind gespeichertes Sonnenlicht urzeitlicher Pflanzen, Wind und Wasserkraft entstehen durch ungleiche Erwärmung.", "Almost all the energy we use is ultimately solar energy: coal, oil and gas are stored sunlight from ancient plants, and wind and hydropower arise from uneven heating."),
            )
        },
        lab("phy.sonnenradius", Topic.Astro, M, 0.4f) {
            val ang = 0.533; val d = 1.496e8; val dia = 2 * d * tan(Math.toRadians(ang / 2))
            task(
                question = l("Die Sonne erscheint am Himmel unter einem Winkel von 0,533°. Sie ist 149,6 Millionen km entfernt. Wie groß ist ihr Durchmesser?", "The Sun appears in the sky at an angle of 0.533°. It is 149.6 million km away. What is its diameter?"),
                steps = listOf(num(l("Durchmesser in km", "Diameter in km"), dia, "km", rel = 0.02, done = """§D \approx ${m(sig(dia))}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Ein Dreieck von deinem Auge zu den beiden Sonnenrändern. Bei kleinen Winkeln gilt D ≈ Entfernung · Winkel (im Bogenmaß).", "Key idea: a triangle from your eye to the two edges of the Sun. For small angles D ≈ distance · angle (in radians)."), """§D = 2 d \tan\dfrac{\alpha}{2} \approx d \cdot \alpha§"""))),
                solution = listOf("""§D = 2 \cdot 1{,}496 \cdot 10^8\ \text{km} \cdot \tan 0{,}2665^\circ \approx ${m(sig(dia))}\ \text{km}§"""),
                background = l("Aus Winkelgröße und Entfernung bekommt man die wahre Größe, mit einfacher Geometrie. Die Entfernung zur Sonne war lange das schwierigste Stück: Man bestimmte sie 1761 und 1769 durch weltweite Beobachtungen, als die Venus vor der Sonne vorbeizog. Dafür reisten Forscher bis nach Tahiti, darunter James Cook.", "From the angular size and the distance you get the true size, with simple geometry. The distance to the Sun was long the hardest part: it was determined in 1761 and 1769 by worldwide observations when Venus passed in front of the Sun. Researchers travelled as far as Tahiti for this, James Cook among them."),
                fact = l("In die Sonne würde die Erde über eine Million Mal hineinpassen.", "The Earth would fit into the Sun over a million times."),
            )
        },
        lab("phy.transit", Topic.Astro, M, 0.5f) {
            val rs = 696000.0; val rj = 71492.0; val re = 6371.0
            val dj = (rj / rs) * (rj / rs) * 100; val de = (re / rs) * (re / rs) * 100
            task(
                question = l("Astronomen finden fremde Planeten, weil sie ihren Stern ein wenig verdunkeln, wenn sie vor ihm vorbeiziehen. Die Sonne hat 696.000 km Radius.", "Astronomers find planets around other stars because they dim their star a little when they pass in front of it. The Sun has a radius of 696,000 km."),
                steps = listOf(
                    num(l("Um wie viel Prozent verdunkelt Jupiter (Radius 71.492 km) die Sonne für einen fernen Beobachter?", "By what percentage does Jupiter (radius 71,492 km) dim the Sun for a distant observer?"), dj, "%", rel = 0.02, done = """§\approx ${m(dj, 2)}\ \text{%}§""",
                        hints = listOf(l("Kernidee: Es zählt der Anteil der Sternscheibe, den die Planetenscheibe abdeckt, also das Verhältnis der Flächen.", "Key idea: what counts is the fraction of the star's disc covered by the planet's disc, i.e. the ratio of the areas."), """§\dfrac{\Delta I}{I} = \left(\dfrac{R_P}{R_S}\right)^2§""")),
                    num(l("Und die Erde (6371 km)?", "And the Earth (6371 km)?"), de, "%", rel = 0.02, done = """§\approx ${m(de, 4)}\ \text{%}§""",
                        hints = listOf(l("Dieselbe Formel, viel kleinerer Radius.", "The same formula, a much smaller radius."), """§\left(\dfrac{6371}{696\,000}\right)^2§""")),
                ),
                solution = listOf("""§\left(\dfrac{71\,492}{696\,000}\right)^2 \approx ${m(dj / 100, 4)} = ${m(dj, 2)}\ \text{%}§""", """§\left(\dfrac{6371}{696\,000}\right)^2 \approx ${sci(de / 100)} = ${m(de, 4)}\ \text{%}§"""),
                background = l("Die Transitmethode misst winzige Helligkeitseinbrüche, die sich regelmäßig wiederholen. Aus der Tiefe folgt die Größe des Planeten, aus dem Abstand der Einbrüche seine Umlaufzeit. Für erdgroße Planeten braucht man Teleskope im All, weil die Luft das Sternlicht ständig flackern lässt. So wurden schon tausende fremde Welten entdeckt.", "The transit method measures tiny dips in brightness that repeat regularly. The depth gives the size of the planet, the spacing of the dips its orbital period. For Earth-sized planets you need telescopes in space, because the air makes starlight flicker constantly. Thousands of alien worlds have been discovered this way."),
                fact = l("Fliegt der Planet vor dem Stern vorbei, scheint ein wenig Sternlicht durch seine Atmosphäre. Daraus liest man sogar ab, welche Gase sie enthält.", "As the planet passes in front of the star, a little starlight shines through its atmosphere. From this you can even read off which gases it contains."),
            )
        },
        lab("phy.olbers", Topic.Astro, M, 0.6f) {
            task(
                question = l("Wäre das Universum unendlich groß, ewig alt und überall mit Sternen gefüllt, würde jede Blickrichtung irgendwann auf einen Stern treffen, und der Nachthimmel wäre so hell wie die Sonne. Warum ist er trotzdem dunkel?", "If the universe were infinitely large, infinitely old and filled with stars everywhere, every line of sight would eventually hit a star, and the night sky would be as bright as the Sun. So why is it dark?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Das Universum hat ein endliches Alter: Licht von sehr fernen Sternen hat uns noch nicht erreicht, und die Expansion schwächt das Licht ab.", "The universe has a finite age: light from very distant stars hasn't reached us yet, and the expansion weakens the light."),
                    listOf(l("Staub zwischen den Sternen schluckt das ganze Licht.", "Dust between the stars swallows all the light."), l("Sterne sind zu weit weg, ihr Licht wird mit der Entfernung null.", "Stars are too far away; their light drops to zero with distance."), l("Die Erde dreht sich zu schnell, um das Licht zu sehen.", "The Earth spins too fast to see the light.")),
                    hints = listOf(l("Kernidee: Weiter entfernte Sterne sind zwar dunkler, aber es gibt auch mehr davon. In einem unendlichen Universum gleicht sich das genau aus.", "Key idea: more distant stars are dimmer, but there are also more of them. In an infinite universe it exactly balances out."), l("Wie lange ist Licht schon unterwegs, seit es Sterne gibt?", "How long has light been travelling since there have been stars?")))),
                solution = listOf(l("Wir sehen nur bis zu der Entfernung, aus der Licht seit dem Urknall kommen konnte. Dahinter gibt es für uns keine Sterne. Zusätzlich verschiebt die Expansion das Licht ferner Galaxien ins Rote und schwächt es ab.", "We can only see as far as light has been able to travel since the Big Bang. Beyond that, there are no stars for us. In addition, the expansion shifts the light of distant galaxies to the red and weakens it.")),
                background = l("Das Olberssche Paradoxon ist ein Beispiel dafür, wie eine scheinbar banale Beobachtung tiefe Folgen hat. Dass es nachts dunkel wird, zeigt, dass das Universum nicht unendlich alt und unveränderlich ist. Edgar Allan Poe ahnte die Lösung schon 1848, lange vor der Urknalltheorie.", "Olbers' paradox is an example of how a seemingly trivial observation has deep consequences. The fact that it gets dark at night shows that the universe isn't infinitely old and unchanging. Edgar Allan Poe guessed the solution as early as 1848, long before the Big Bang theory."),
                fact = l("Ganz dunkel ist der Himmel nicht: Mit Mikrowellen-Augen sähen wir überall das schwache Leuchten der Hintergrundstrahlung aus der Frühzeit des Universums.", "The sky isn't completely dark: with microwave eyes we would see the faint glow of the background radiation from the early universe everywhere."),
            )
        },
        lab("phy.weisserzwerg", Topic.Astro, H, 0.5f) {
            val mass = pick(1.0, 0.6) * 1.989e30; val r = 5800e3
            val rho = mass / (4.0 / 3.0 * PI * r * r * r) / 1e6 / 1000
            task(
                question = l("Der Weiße Zwerg Sirius B hat ${n(mass / 1.989e30, 1)} Sonnenmassen (1 Sonnenmasse = 2,0 · 10³⁰ kg), ist aber nur so groß wie die Erde (Radius 5800 km). Wie dicht ist er im Mittel, in Tonnen pro Kubikzentimeter?", "The white dwarf Sirius B has ${n(mass / 1.989e30, 1)} solar masses (1 solar mass = 2.0 · 10³⁰ kg) but is only as big as the Earth (radius 5800 km). How dense is it on average, in tonnes per cubic centimetre?"),
                steps = listOf(num(l("Dichte in t/cm³", "Density in t/cm³"), rho, "t/cm³", rel = 0.03, done = """§\rho \approx ${m(rho, 2)}\ \text{t/cm}^3§""",
                    hints = listOf(l("Kernidee: Masse durch Kugelvolumen, dann von kg/m³ in t/cm³ umrechnen (1 t/cm³ = 10⁹ kg/m³).", "Key idea: mass over the volume of a sphere, then convert from kg/m³ to t/cm³ (1 t/cm³ = 10⁹ kg/m³)."), """§\rho = \dfrac{M}{\tfrac{4}{3}\pi R^3}§"""))),
                solution = listOf("""§\rho = \dfrac{${sci(mass)}}{\tfrac{4}{3}\pi (5{,}8 \cdot 10^6)^3}\ \tfrac{\text{kg}}{\text{m}^3} \approx ${sci(rho * 1e9)}\ \tfrac{\text{kg}}{\text{m}^3} \approx ${m(rho, 2)}\ \tfrac{\text{t}}{\text{cm}^3}§"""),
                background = l("Ein Weißer Zwerg hat keine Energiequelle mehr und wird nur noch von den Elektronen gestützt, die sich nach dem Pauli-Prinzip nicht beliebig zusammendrücken lassen. Erstaunlich: Je schwerer ein Weißer Zwerg, desto kleiner ist er. Über 1,4 Sonnenmassen (Chandrasekhar-Grenze) halten die Elektronen nicht mehr stand.", "A white dwarf has no energy source any more and is held up only by its electrons, which, by the Pauli principle, can't be squeezed together arbitrarily. Amazingly, the heavier a white dwarf, the smaller it is. Above 1.4 solar masses (the Chandrasekhar limit) the electrons can no longer hold out."),
                fact = l("Ein Zuckerwürfel aus diesem Material wöge so viel wie ein Lastwagen.", "A sugar cube of this material would weigh as much as a lorry."),
            )
        },
        lab("phy.sternleben", Topic.Astro, H, 0.6f) {
            val mStar = pick(10.0, 2.0, 0.5); val life = 10.0 * mStar.pow(-2.5)
            task(
                question = l("Die Sonne lebt etwa 10 Milliarden Jahre. Die Leuchtkraft eines Sterns wächst grob mit M^3,5, sein Brennstoffvorrat nur mit M. Wie lange lebt ein Stern mit ${n(mStar, 1)} Sonnenmassen?", "The Sun lives for about 10 billion years. A star's luminosity grows roughly as M^3.5, its fuel supply only as M. How long does a star with ${n(mStar, 1)} solar masses live?"),
                steps = listOf(num(l("Lebensdauer in Milliarden Jahren", "Lifetime in billions of years"), life, l("Mrd. a", "billion yr"), rel = 0.03, done = """§t \approx ${m(sig(life))}\ \text{Mrd. a}§""".let { l(it, it.replace("Mrd. a", "billion yr")) },
                    hints = listOf(l("Kernidee: Lebensdauer = Vorrat durch Verbrauch, also M / M^3,5.", "Key idea: lifetime = supply over consumption, so M / M^3.5."), """§t = 10\ \text{Mrd. a} \cdot \left(\dfrac{M}{M_S}\right)^{-2{,}5}§""".let { l(it, it.replace("Mrd. a", "billion yr")) }))),
                solution = listOf("""§t = 10 \cdot ${m(mStar, 1)}^{-2{,}5} \approx ${m(sig(life))}§"""),
                background = l("Schwere Sterne haben zwar mehr Brennstoff, verbrennen ihn aber verschwenderisch schnell, weil Druck und Temperatur in ihrem Kern viel höher sind. Ein Stern mit zehn Sonnenmassen lebt deshalb nur einige zehn Millionen Jahre und endet als Supernova. Rote Zwerge dagegen leuchten länger, als das Universum bisher alt ist.", "Heavy stars have more fuel, but they burn it extravagantly fast, because pressure and temperature in their cores are much higher. A star with ten solar masses therefore lives only a few tens of millions of years and ends as a supernova. Red dwarfs, by contrast, shine for longer than the universe has existed so far."),
                fact = l("Noch nie ist ein Roter Zwerg ausgebrannt: Dafür ist das Universum mit 13,8 Milliarden Jahren viel zu jung.", "No red dwarf has ever burnt out: at 13.8 billion years the universe is far too young for that."),
            )
        },
        lab("phy.cepheiden", Topic.Astro, H, 0.6f) {
            val mAbs = -4.0; val mApp = pick(16.0, 11.0, 21.0); val d = 10.0.pow((mApp - mAbs + 5) / 5) / 1000
            task(
                question = l("Ein Cepheiden-Stern pulsiert so, dass man aus seiner Periode seine wahre Helligkeit kennt: absolute Helligkeit −4. Am Himmel sieht man ihn mit der scheinbaren Helligkeit ${n(mApp, 0)}. Wie weit ist er entfernt?", "A Cepheid star pulsates in such a way that its period tells you its true brightness: absolute magnitude −4. In the sky it appears with an apparent magnitude of ${n(mApp, 0)}. How far away is it?"),
                steps = listOf(num(l("Entfernung in Kiloparsec", "Distance in kiloparsecs"), d, "kpc", rel = 0.03, done = """§d \approx ${m(sig(d))}\ \text{kpc}§""",
                    hints = listOf(l("Kernidee: Die Differenz von scheinbarer und absoluter Helligkeit, der Entfernungsmodul, verrät die Entfernung. 5 Größenklassen sind ein Faktor 100 in der Helligkeit, also ein Faktor 10 in der Entfernung.", "Key idea: the difference between apparent and absolute magnitude, the distance modulus, reveals the distance. 5 magnitudes are a factor of 100 in brightness, so a factor of 10 in distance."), """§m - M = 5 \log_{10}\dfrac{d}{10\ \text{pc}}§"""))),
                solution = listOf("""§${m(mApp, 0)} - (-4) = 5 \log_{10}\dfrac{d}{10\ \text{pc}} \Rightarrow d = 10^{${m((mApp - mAbs + 5) / 5, 0)}}\ \text{pc} = ${m(sig(d))}\ \text{kpc}§"""),
                background = l("Henrietta Leavitt entdeckte 1912: Je langsamer ein Cepheid pulsiert, desto heller ist er wirklich. Damit wurden diese Sterne zu Standardkerzen. Edwin Hubble fand 1923 Cepheiden im Andromedanebel und zeigte, dass er weit außerhalb der Milchstraße liegt. Auf einmal war das Universum voller Galaxien.", "In 1912 Henrietta Leavitt discovered that the more slowly a Cepheid pulsates, the brighter it really is. That made these stars into standard candles. In 1923 Edwin Hubble found Cepheids in the Andromeda nebula and showed that it lies far outside the Milky Way. Suddenly the universe was full of galaxies."),
                fact = l("Die Helligkeitsskala ist umgekehrt: Kleinere Zahlen sind heller. Die Sonne hat −27, die schwächsten Sterne, die man mit bloßem Auge sieht, etwa +6.", "The magnitude scale is back to front: smaller numbers are brighter. The Sun is at −27; the faintest stars visible to the naked eye are about +6."),
            )
        },
        lab("phy.rotationskurve", Topic.Astro, X, 0.6f) {
            val v = pick(230.0, 220.0) * 1000; val r = 8.2 * 3.086e19
            val mass = v * v * r / GR7; val sol = mass / 1.989e30
            task(
                question = l("Die Sonne umkreist das Zentrum der Milchstraße in 8,2 kpc (2,5 · 10²⁰ m) Abstand mit ${n(v / 1000, 0)} km/s. Wie viel Masse liegt innerhalb ihrer Bahn, in Sonnenmassen (2,0 · 10³⁰ kg)?", "The Sun orbits the centre of the Milky Way at a distance of 8.2 kpc (2.5 · 10²⁰ m) at ${n(v / 1000, 0)} km/s. How much mass lies inside its orbit, in solar masses (2.0 · 10³⁰ kg)?"),
                steps = listOf(big(l("Masse in Sonnenmassen", "Mass in solar masses"), sol, "", rel = 0.04, done = """§M \approx ${sci(sol)}\ M_\odot§""".replace("""M_\odot""", "M_S"),
                    hints = listOf(l("Kernidee: Für die Kreisbahn zählt nur die Masse innerhalb der Bahn. Die Gravitation liefert die Zentripetalkraft.", "Key idea: for the circular orbit only the mass inside the orbit counts. Gravity provides the centripetal force."), """§\dfrac{v^2}{r} = \dfrac{G M}{r^2} \Rightarrow M = \dfrac{v^2 r}{G}§"""))),
                solution = listOf("""§M = \dfrac{(${m(v / 1000, 0)} \cdot 10^3)^2 \cdot 2{,}53 \cdot 10^{20}}{6{,}674 \cdot 10^{-11}}\ \text{kg} \approx ${sci(mass)}\ \text{kg} \approx ${sci(sol)}\ M_S§"""),
                background = l("Würde fast die ganze Masse einer Galaxie in den hellen Sternen stecken, müssten äußere Sterne langsamer kreisen, wie die äußeren Planeten. Gemessen wird aber, dass die Geschwindigkeit weit draußen fast konstant bleibt. Dort muss also viel unsichtbare Masse liegen. Vera Rubin machte das in den 1970er-Jahren zum stärksten Beleg für Dunkle Materie.", "If almost all of a galaxy's mass were in its bright stars, the outer stars would have to orbit more slowly, like the outer planets. But measurements show that the speed stays almost constant far out. So there must be a lot of invisible mass there. In the 1970s Vera Rubin made this the strongest evidence for dark matter."),
                fact = l("Für eine Runde um die Milchstraße braucht die Sonne etwa 230 Millionen Jahre. Bei der letzten Runde lebten gerade die ersten Dinosaurier.", "The Sun takes about 230 million years for one lap of the Milky Way. On the last lap the first dinosaurs were just appearing."),
            )
        },
        lab("phy.kritdichte", Topic.Astro, X, 0.7f) {
            val h0 = pick(70.0, 67.4) * 1000 / 3.086e22; val rho = 3 * h0 * h0 / (8 * PI * GR7); val protons = rho / 1.673e-27
            task(
                question = l("Ob das Universum flach, geschlossen oder offen ist, hängt davon ab, ob seine Dichte über oder unter der kritischen Dichte liegt. Berechne sie für H₀ = ${n(h0 * 3.086e22 / 1000, 1)} km/s pro Mpc (1 Mpc = 3,086 · 10²² m).", "Whether the universe is flat, closed or open depends on whether its density is above or below the critical density. Calculate it for H₀ = ${n(h0 * 3.086e22 / 1000, 1)} km/s per Mpc (1 Mpc = 3.086 · 10²² m)."),
                steps = listOf(
                    big(l("Kritische Dichte in kg/m³", "Critical density in kg/m³"), rho, "kg/m³", rel = 0.03, done = """§\rho_c \approx ${sci(rho)}\ \text{kg/m}^3§""",
                        hints = listOf(l("Kernidee: Aus der Friedmann-Gleichung für ein flaches Universum. Erst H₀ in 1/s umrechnen.", "Key idea: from the Friedmann equation for a flat universe. First convert H₀ into 1/s."), """§\rho_c = \dfrac{3 H_0^2}{8\pi G}§""")),
                    num(l("Wie vielen Protonen (1,67 · 10⁻²⁷ kg) pro Kubikmeter entspricht das?", "How many protons (1.67 · 10⁻²⁷ kg) per cubic metre does that correspond to?"), protons, "", rel = 0.03, done = """§\approx ${m(protons, 1)}§""",
                        hints = listOf(l("Dichte geteilt durch Protonenmasse.", "Density divided by the proton mass."), """§n = \dfrac{\rho_c}{m_p}§""")),
                ),
                solution = listOf("""§H_0 \approx ${sci(h0)}\ \text{s}^{-1}§""", """§\rho_c = \dfrac{3 \cdot (${sci(h0)})^2}{8\pi \cdot 6{,}674 \cdot 10^{-11}}\ \tfrac{\text{kg}}{\text{m}^3} \approx ${sci(rho)}\ \tfrac{\text{kg}}{\text{m}^3} \approx ${m(protons, 1)}\ m_p/\text{m}^3§"""),
                background = l("Die Friedmann-Gleichung verbindet die Expansionsrate des Universums mit seiner Energiedichte. Bei genau der kritischen Dichte ist der Raum im Großen flach, wie ein Blatt Papier. Messungen der Hintergrundstrahlung zeigen: Unser Universum ist erstaunlich genau flach. Gewöhnliche Materie macht davon aber nur 5 % aus, der Rest ist Dunkle Materie und Dunkle Energie.", "The Friedmann equation links the expansion rate of the universe to its energy density. At exactly the critical density, space on large scales is flat, like a sheet of paper. Measurements of the background radiation show that our universe is remarkably exactly flat. Ordinary matter makes up only 5% of it, though; the rest is dark matter and dark energy."),
                fact = l("Das ist leerer als jedes Vakuum, das man auf der Erde herstellen kann: Eine gute Vakuumkammer enthält noch Milliarden Teilchen pro Kubikzentimeter.", "That's emptier than any vacuum that can be made on Earth: a good vacuum chamber still contains billions of particles per cubic centimetre."),
            )
        },
        lab("phy.gleichheit", Topic.Astro, X, 0.7f) {
            val om = pick(0.31, 0.3); val orad = 9.0e-5; val z1 = om / orad
            task(
                question = l("Heute macht Strahlung (Photonen, Neutrinos) nur 0,009 % der Energiedichte des Universums aus, Materie ${n(om * 100, 0)} %. Wird das Universum um den Faktor (1 + z) kleiner, wächst die Materiedichte mit (1 + z)³, die Strahlungsdichte mit (1 + z)⁴. Bei welchem 1 + z waren beide gleich?", "Today radiation (photons, neutrinos) makes up only 0.009% of the energy density of the universe, matter ${n(om * 100, 0)}%. If the universe shrinks by a factor of (1 + z), the matter density grows as (1 + z)³, the radiation density as (1 + z)⁴. At what 1 + z were the two equal?"),
                steps = listOf(num(l("1 + z bei Gleichheit", "1 + z at equality"), z1, "", rel = 0.03, done = """§1 + z \approx ${m(sig(z1))}§""",
                    hints = listOf(l("Kernidee: Strahlung gewinnt beim Zurückrechnen pro Faktor (1 + z) einen Faktor mehr als Materie, weil jedes Photon zusätzlich röter beziehungsweise blauer wird.", "Key idea: going back in time, radiation gains one extra factor of (1 + z) over matter, because every photon also gets shifted in wavelength."), """§\Omega_r (1 + z)^4 = \Omega_m (1 + z)^3 \Rightarrow 1 + z = \dfrac{\Omega_m}{\Omega_r}§"""))),
                solution = listOf("""§1 + z = \dfrac{${m(om, 2)}}{9 \cdot 10^{-5}} \approx ${m(sig(z1))}§"""),
                background = l("Beim Ausdehnen verdünnt sich Materie einfach mit dem Volumen. Strahlung verliert zusätzlich Energie, weil jede Lichtwelle mitgedehnt wird. Rechnet man zurück, war das junge Universum deshalb erst von Strahlung beherrscht, dann von Materie, und heute von Dunkler Energie. Der Wechsel zur Materie, etwa 50.000 Jahre nach dem Urknall, erlaubte erst das Wachstum von Strukturen wie Galaxien.", "As it expands, matter simply thins out with the volume. Radiation loses energy as well, because every light wave is stretched along with it. Working backwards, the young universe was therefore dominated first by radiation, then by matter, and today by dark energy. The switch to matter, about 50,000 years after the Big Bang, is what allowed structures such as galaxies to grow."),
                fact = l("Die Hintergrundstrahlung entstand später, bei 1 + z ≈ 1100, als das Universum durchsichtig wurde.", "The background radiation was released later, at 1 + z ≈ 1100, when the universe became transparent."),
            )
        },
    )

    private val particles = listOf(
        lab("phy.vierkraefte", Topic.Modern, E, 0.5f) {
            task(
                question = l("Die Physik kennt vier Grundkräfte: Gravitation, Elektromagnetismus, die starke und die schwache Kernkraft. Welche ist zwischen zwei Protonen die mit Abstand schwächste?", "Physics knows four fundamental forces: gravity, electromagnetism, the strong and the weak nuclear force. Which is by far the weakest between two protons?"),
                steps = listOf(pick1(l("Die schwächste Kraft", "The weakest force"), l("Gravitation", "Gravity"), listOf(l("Schwache Kernkraft", "The weak nuclear force"), l("Elektromagnetismus", "Electromagnetism"), l("Starke Kernkraft", "The strong nuclear force")),
                    hints = listOf(l("Kernidee: Ein kleiner Magnet kann eine Büroklammer gegen die Anziehung der ganzen Erde hochheben.", "Key idea: a small magnet can lift a paper clip against the pull of the entire Earth."), l("Die schwache Kraft heißt nur so, weil sie auf sehr kurze Abstände wirkt, ist aber viel stärker als eine der anderen.", "The weak force is only called that because it acts over very short distances; it is much stronger than one of the others.")))),
                solution = listOf(l("Die Gravitation ist zwischen zwei Protonen etwa 10³⁶-mal schwächer als die elektrische Abstoßung. Dass sie im Großen trotzdem herrscht, liegt daran, dass sie sich immer addiert, während sich Ladungen ausgleichen.", "Between two protons gravity is about 10³⁶ times weaker than the electric repulsion. That it still rules on large scales is because it always adds up, whereas charges cancel out.")),
                background = l("Die starke Kraft hält Quarks in Protonen und Protonen im Kern zusammen, wirkt aber nur über etwa einen Protonendurchmesser. Die schwache Kraft ermöglicht den Betazerfall und die Fusion in der Sonne. Elektromagnetismus hält Atome zusammen und macht Chemie und Licht. Warum die Gravitation so unglaublich schwach ist, gehört zu den großen offenen Fragen der Physik.", "The strong force holds quarks together in protons and protons together in the nucleus, but acts only over about the diameter of a proton. The weak force makes beta decay and fusion in the Sun possible. Electromagnetism holds atoms together and makes chemistry and light. Why gravity is so incredibly weak is one of the great open questions in physics."),
                fact = l("Bei sehr hohen Energien verschmelzen elektromagnetische und schwache Kraft zu einer einzigen, der elektroschwachen Kraft. Das zeigte man am CERN in den 1980er-Jahren.", "At very high energies the electromagnetic and weak forces merge into a single one, the electroweak force. This was demonstrated at CERN in the 1980s."),
            )
        },
        lab("phy.neutrinos", Topic.Modern, E, 0.4f) {
            val flux = 6.5e10; val area = pick(7000.0, 5000.0); val nps = flux * area
            task(
                question = l("Von der Sonne fliegen pro Sekunde rund 65 Milliarden Neutrinos durch jeden Quadratzentimeter, Tag und Nacht, denn sie fliegen auch durch die ganze Erde. Wie viele durchqueren pro Sekunde deinen Körper (Querschnitt ${n(area, 0)} cm²)?", "Every second about 65 billion neutrinos from the Sun pass through each square centimetre, day and night, since they fly through the whole Earth too. How many cross your body per second (cross-section ${n(area, 0)} cm²)?"),
                steps = listOf(big(l("Neutrinos pro Sekunde", "Neutrinos per second"), nps, "", rel = 0.02, done = """§\approx ${sci(nps)}§""",
                    hints = listOf(l("Kernidee: Fluss mal Fläche.", "Key idea: flux times area."), """§N = 6{,}5 \cdot 10^{10} \cdot ${m(area, 0)}§"""))),
                solution = listOf("""§N = 6{,}5 \cdot 10^{10}\ \tfrac{1}{\text{cm}^2\,\text{s}} \cdot ${m(area, 0)}\ \text{cm}^2 \approx ${sci(nps)}\ \tfrac{1}{\text{s}}§"""),
                background = l("Neutrinos entstehen bei der Kernfusion in der Sonne und spüren nur die schwache Kraft. Deshalb fliegen sie fast immer ungehindert durch alles, auch durch die ganze Erde. In deinem ganzen Leben bleibt im Mittel nur etwa ein einziges Sonnenneutrino in deinem Körper hängen. Man fängt sie mit riesigen, unterirdischen Wassertanks.", "Neutrinos are produced by nuclear fusion in the Sun and feel only the weak force. That's why they almost always fly unhindered through everything, even through the whole Earth. On average only about one single solar neutrino gets stopped in your body in your whole life. They are caught with huge underground water tanks."),
                fact = l("Neutrinos haben eine winzige Masse, mindestens eine Million Mal weniger als ein Elektron. Das wurde 1998 entdeckt und 2015 mit dem Nobelpreis gewürdigt.", "Neutrinos have a tiny mass, at least a million times less than an electron. This was discovered in 1998 and honoured with the Nobel Prize in 2015."),
            )
        },
        lab("phy.elektronproton", Topic.Modern, E, 0.3f) {
            val ratio = 1.6726e-27 / 9.109e-31
            task(
                question = l("Ein Proton hat die Masse 1,6726 · 10⁻²⁷ kg, ein Elektron 9,109 · 10⁻³¹ kg. Wie viel mal schwerer ist das Proton?", "A proton has a mass of 1.6726 · 10⁻²⁷ kg, an electron 9.109 · 10⁻³¹ kg. How many times heavier is the proton?"),
                steps = listOf(num(l("Verhältnis", "Ratio"), ratio, "", rel = 0.01, done = """§\approx ${m(ratio, 0)}§""",
                    hints = listOf(l("Kernidee: Teilen. Zehnerpotenzen getrennt behandeln: 10⁻²⁷ / 10⁻³¹ = 10⁴.", "Key idea: divide. Treat the powers of ten separately: 10⁻²⁷ / 10⁻³¹ = 10⁴."), """§\dfrac{1{,}6726}{9{,}109} \cdot 10^4§"""))),
                solution = listOf("""§\dfrac{1{,}6726 \cdot 10^{-27}}{9{,}109 \cdot 10^{-31}} \approx ${m(ratio, 0)}§"""),
                background = l("Fast die ganze Masse eines Atoms sitzt im Kern. Die Elektronen machen weniger als ein Zweitausendstel aus, bestimmen aber Größe und Chemie des Atoms. Das Proton selbst besteht aus drei Quarks, die zusammen nur etwa 1 % seiner Masse ausmachen. Der Rest ist Bewegungs- und Bindungsenergie der starken Kraft, nach E = mc².", "Almost all of an atom's mass sits in the nucleus. The electrons account for less than one two-thousandth, yet they determine the size and chemistry of the atom. The proton itself consists of three quarks, which together make up only about 1% of its mass. The rest is the motion and binding energy of the strong force, by E = mc²."),
                fact = l("Warum das Verhältnis gerade etwa 1836 ist, kann bis heute niemand erklären. Es ist eine der Naturkonstanten, die man nur messen kann.", "Why the ratio is about 1836 of all numbers, nobody can yet explain. It's one of the constants of nature that can only be measured."),
            )
        },
        lab("phy.higgs", Topic.Modern, M, 0.5f) {
            val gev = 125.0; val kg = gev * 1e9 * EV7 / (2.998e8 * 2.998e8); val ratio = kg / 1.6726e-27
            task(
                question = l("Das 2012 am CERN entdeckte Higgs-Boson hat eine Masse von 125 GeV/c². Wie viel ist das in Kilogramm?", "The Higgs boson, discovered at CERN in 2012, has a mass of 125 GeV/c². How much is that in kilograms?"),
                steps = listOf(big(l("Masse in kg", "Mass in kg"), kg, "kg", rel = 0.02, done = """§m \approx ${sci(kg)}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Erst die Energie in Joule (1 eV = 1,6 · 10⁻¹⁹ J), dann durch c² teilen.", "Key idea: first the energy in joules (1 eV = 1.6 · 10⁻¹⁹ J), then divide by c²."), """§m = \dfrac{E}{c^2}§"""))),
                solution = listOf("""§E = 125 \cdot 10^9 \cdot 1{,}6 \cdot 10^{-19}\ \text{J} = 2{,}0 \cdot 10^{-8}\ \text{J}§""", """§m = \dfrac{2{,}0 \cdot 10^{-8}}{(3 \cdot 10^8)^2}\ \text{kg} \approx ${sci(kg)}\ \text{kg}§"""),
                background = l("Teilchenphysiker geben Massen in Energieeinheiten an, weil Teilchen in Beschleunigern aus Energie entstehen. Das Higgs-Feld durchzieht den ganzen Raum und gibt Teilchen wie Elektronen und Quarks ihre Masse. Das Higgs-Boson ist eine Welle in diesem Feld. Es lebt nur 10⁻²² Sekunden und verrät sich durch seine Zerfallsprodukte.", "Particle physicists give masses in units of energy, because particles in accelerators are created from energy. The Higgs field fills all of space and gives particles such as electrons and quarks their mass. The Higgs boson is a ripple in this field. It lives for only 10⁻²² seconds and gives itself away through its decay products."),
                fact = l("Das ist etwa ${n(ratio, 0)}-mal so schwer wie ein Proton, so viel wie ein ganzes Atom Cäsium.", "That's about ${n(ratio, 0)} times as heavy as a proton, as much as a whole caesium atom."),
            )
        },
        lab("phy.omgteilchen", Topic.Modern, M, 0.6f) {
            val ev = 3.2e20; val j = ev * EV7; val v = sqrt(2 * j / 0.145) * 3.6
            task(
                question = l("1991 traf ein einzelnes Teilchen der kosmischen Strahlung mit 3,2 · 10²⁰ eV die Atmosphäre, das Oh-My-God-Teilchen.", "In 1991 a single cosmic-ray particle with 3.2 · 10²⁰ eV struck the atmosphere: the Oh-My-God particle."),
                steps = listOf(
                    num(l("Wie viel Energie ist das in Joule?", "How much energy is that in joules?"), j, "J", rel = 0.02, done = """§E \approx ${m(j, 0)}\ \text{J}§""",
                        hints = listOf(l("Kernidee: 1 eV = 1,6 · 10⁻¹⁹ J.", "Key idea: 1 eV = 1.6 · 10⁻¹⁹ J."), """§E = 3{,}2 \cdot 10^{20} \cdot 1{,}6 \cdot 10^{-19}\ \text{J}§""")),
                    num(l("Wie schnell müsste ein Baseball (145 g) fliegen, um dieselbe Energie zu haben (in km/h)?", "How fast would a baseball (145 g) have to fly to have the same energy (in km/h)?"), v, "km/h", rel = 0.02, done = """§v \approx ${m(v, 0)}\ \text{km/h}§""",
                        hints = listOf(l("Bewegungsenergie gleichsetzen und nach v auflösen.", "Set the kinetic energies equal and solve for v."), """§v = \sqrt{\dfrac{2E}{m}}§""")),
                ),
                solution = listOf("""§E = 3{,}2 \cdot 10^{20} \cdot 1{,}6 \cdot 10^{-19}\ \text{J} \approx ${m(j, 0)}\ \text{J}§""", """§v = \sqrt{\dfrac{2 \cdot ${m(j, 0)}}{0{,}145}}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v / 3.6, 1)}\ \tfrac{\text{m}}{\text{s}} \approx ${m(v, 0)}\ \text{km/h}§"""),
                background = l("Ein einziges Proton, vermutlich, trug die Energie eines geworfenen Balls. Das ist rund 50 Millionen Mal mehr, als der LHC einem Proton geben kann. Woher solche Teilchen kommen, ist bis heute rätselhaft. Kandidaten sind aktive Galaxienkerne, in denen gewaltige Schwarze Löcher Materie verschlingen.", "A single particle, probably a proton, carried the energy of a thrown ball. That's about 50 million times more than the LHC can give a proton. Where such particles come from is still a mystery. Candidates are active galactic nuclei, where giant black holes devour matter."),
                fact = l("Beim Aufprall auf die Atmosphäre erzeugt so ein Teilchen einen Schauer aus Milliarden Sekundärteilchen, der viele Quadratkilometer Boden trifft.", "When it hits the atmosphere such a particle creates a shower of billions of secondary particles that strikes many square kilometres of ground."),
            )
        },
        lab("phy.linac", Topic.Modern, M, 0.4f) {
            val gaps = pick(100, 200, 50); val kv = pick(50.0, 100.0, 25.0); val mev = gaps * kv / 1000
            task(
                question = l("In einem Linearbeschleuniger fliegen Protonen durch eine Reihe von Röhren. In jedem der ${gaps} Spalte zwischen den Röhren werden sie mit ${n(kv, 0)} kV beschleunigt. Welche Energie haben sie am Ende?", "In a linear accelerator protons fly through a series of tubes. In each of the ${gaps} gaps between the tubes they are accelerated by ${n(kv, 0)} kV. What energy do they have at the end?"),
                steps = listOf(num(l("Energie in MeV", "Energy in MeV"), mev, "MeV", rel = 0.01, done = """§E = ${m(mev, 1)}\ \text{MeV}§""",
                    hints = listOf(l("Kernidee: Jeder Spalt gibt einem einfach geladenen Teilchen so viele Elektronvolt, wie Volt anliegen.", "Key idea: each gap gives a singly charged particle as many electronvolts as there are volts across it."), """§E = N \cdot e U§"""))),
                solution = listOf("""§E = ${gaps} \cdot ${m(kv, 0)}\ \text{keV} = ${m(mev, 1)}\ \text{MeV}§"""),
                background = l("Statt einer riesigen Spannung nutzt ein Linearbeschleuniger viele kleine Schritte. Eine Wechselspannung polt die Röhren immer genau dann um, wenn die Teilchen im Inneren einer Röhre vom Feld abgeschirmt sind. Weil die Teilchen schneller werden, müssen die Röhren nach hinten immer länger werden, damit der Takt passt. Rolf Widerøe baute 1928 den ersten.", "Instead of one huge voltage, a linear accelerator uses many small steps. An alternating voltage switches the polarity of the tubes exactly while the particles are shielded from the field inside a tube. Because the particles keep getting faster, the tubes must get longer and longer towards the end so that the timing still fits. Rolf Widerøe built the first one in 1928."),
                fact = l("Der längste Linearbeschleuniger der Welt, in Stanford, ist über 3 km lang und brachte Elektronen auf 50 GeV.", "The longest linear accelerator in the world, at Stanford, is over 3 km long and brought electrons up to 50 GeV."),
            )
        },
        lab("phy.blasenkammer", Topic.Modern, M, 0.6f) {
            task(
                question = l("In einer Blasenkammer mit Magnetfeld hinterlassen Elektronen oft Spuren, die sich nach innen zu einer Spirale aufrollen. Warum werden die Kreise immer enger?", "In a bubble chamber with a magnetic field, electrons often leave tracks that curl inwards into a spiral. Why do the circles keep getting tighter?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Das Elektron verliert durch Stöße Energie. Langsamere Teilchen laufen im Magnetfeld auf kleineren Kreisen.", "The electron loses energy through collisions. Slower particles move on smaller circles in a magnetic field."),
                    listOf(l("Das Magnetfeld wird zur Mitte hin schwächer.", "The magnetic field gets weaker towards the centre."), l("Das Elektron wird durch die Flüssigkeit immer schwerer.", "The electron keeps getting heavier through the liquid."), l("Die Ladung des Elektrons nimmt mit der Zeit zu.", "The electron's charge increases over time.")),
                    hints = listOf(l("Kernidee: Der Radius im Magnetfeld ist r = p/(q·B). Was ändert sich unterwegs?", "Key idea: the radius in a magnetic field is r = p/(q·B). What changes along the way?"), l("Jede Blase in der Spur ist eine Stelle, an der das Teilchen Energie abgegeben hat.", "Every bubble in the track is a spot where the particle has given off energy.")))),
                solution = listOf(l("Der Bahnradius ist proportional zum Impuls. Das Elektron ionisiert die Flüssigkeit und strahlt, verliert so Impuls, und der Kreis wird kleiner, bis es zur Ruhe kommt.", "The radius of the path is proportional to the momentum. The electron ionises the liquid and radiates, so it loses momentum, and the circle gets smaller until it comes to rest.")),
                background = l("Eine Blasenkammer ist mit überhitzter Flüssigkeit gefüllt, die nur auf einen Anstoß wartet, um zu sieden. Ein durchfliegendes geladenes Teilchen hinterlässt eine Spur winziger Bläschen. Aus der Krümmung im Magnetfeld liest man Ladung und Impuls ab: Positive und negative Teilchen biegen in entgegengesetzte Richtungen.", "A bubble chamber is filled with superheated liquid that only needs a nudge to start boiling. A charged particle flying through leaves a trail of tiny bubbles. From the curvature in the magnetic field you can read off the charge and momentum: positive and negative particles bend in opposite directions."),
                fact = l("Donald Glaser soll die Idee beim Betrachten der Bläschen in einem Bierglas gehabt haben. Er bekam 1960 den Nobelpreis dafür.", "Donald Glaser is said to have had the idea while looking at the bubbles in a glass of beer. He won the Nobel Prize for it in 1960."),
            )
        },
        lab("phy.antimateriefall", Topic.Modern, M, 0.6f) {
            task(
                question = l("2023 ließ man am CERN zum ersten Mal Antiwasserstoff-Atome in einem senkrechten Rohr fallen, um zu prüfen, ob Antimaterie vielleicht nach oben fällt. Was kam heraus?", "In 2023 antihydrogen atoms were dropped in a vertical tube at CERN for the first time, to test whether antimatter might perhaps fall upwards. What was the result?"),
                steps = listOf(pick1(l("Ergebnis", "Result"), l("Antimaterie fällt nach unten, im Rahmen der Messgenauigkeit genauso wie normale Materie", "Antimatter falls down, within the measurement accuracy just like normal matter"), listOf(l("Antimaterie fällt nach oben", "Antimatter falls upwards"), l("Antimaterie schwebt, die Schwerkraft wirkt nicht auf sie", "Antimatter floats; gravity doesn't act on it"), l("Antimaterie fällt doppelt so schnell", "Antimatter falls twice as fast")),
                    hints = listOf(l("Kernidee: Nach Einstein krümmt jede Energie die Raumzeit gleich, und Antimaterie hat positive Energie.", "Key idea: according to Einstein all energy curves spacetime in the same way, and antimatter has positive energy."), l("Das Experiment hieß ALPHA-g und bestätigte die allgemeine Relativitätstheorie.", "The experiment was called ALPHA-g and confirmed general relativity.")))),
                solution = listOf(l("Die Antiwasserstoff-Atome fielen nach unten, mit einer Beschleunigung, die zu g passt. Antimaterie ist also nicht der Grund, warum das Universum aus Materie besteht, jedenfalls nicht über die Schwerkraft.", "The antihydrogen atoms fell downwards with an acceleration consistent with g. So antimatter isn't the reason the universe is made of matter, at least not through gravity.")),
                background = l("Antimaterie hat die gleiche Masse wie Materie, aber entgegengesetzte Ladung. Treffen beide aufeinander, zerstrahlen sie vollständig. Warum das Universum fast nur aus Materie besteht, obwohl der Urknall vermutlich gleich viel von beiden erzeugt hat, ist eines der großen Rätsel. Ein Unterschied im Fallen hätte ein Hinweis sein können.", "Antimatter has the same mass as matter but the opposite charge. When the two meet, they annihilate completely. Why the universe consists almost entirely of matter, although the Big Bang probably made equal amounts of both, is one of the great puzzles. A difference in how they fall could have been a clue."),
                fact = l("Die Antiatome mussten dafür auf unter ein halbes Kelvin gekühlt und in Magnetfallen gehalten werden, damit sie nicht die Wände berühren.", "To do this the anti-atoms had to be cooled to below half a kelvin and held in magnetic traps so that they wouldn't touch the walls."),
            )
        },
        lab("phy.erhaltungssaetze", Topic.Modern, H, 0.6f) {
            task(
                question = l("Teilchenphysiker prüfen Reaktionen auf Erhaltungsgrößen: Ladung, Energie, Baryonenzahl (Protonen und Neutronen zählen +1) und Leptonenzahl (Elektronen und Neutrinos +1, ihre Antiteilchen −1). Welcher Zerfall ist unmöglich?", "Particle physicists check reactions for conserved quantities: charge, energy, baryon number (protons and neutrons count +1) and lepton number (electrons and neutrinos +1, their antiparticles −1). Which decay is impossible?"),
                steps = listOf(pick1(l("Unmöglich ist …", "Impossible is …"), "p → e⁺ + γ", listOf("n → p + e⁻ + ν̄", "μ⁻ → e⁻ + ν̄ₑ + ν_μ", "π⁰ → γ + γ"),
                    hints = listOf(l("Kernidee: Zähle bei jeder Reaktion die Baryonenzahl vorher und nachher.", "Key idea: for each reaction count the baryon number before and after."), l("Ein Proton hat Baryonenzahl 1. Hat ein Positron oder ein Photon eine?", "A proton has baryon number 1. Does a positron or a photon have one?")))),
                solution = listOf(l("Beim Zerfall p → e⁺ + γ wäre die Baryonenzahl vorher 1, nachher 0, und die Leptonenzahl vorher 0, nachher −1. Ladung und Energie würden passen, die anderen Erhaltungssätze verbieten ihn.", "In the decay p → e⁺ + γ the baryon number would be 1 before and 0 after, and the lepton number 0 before and −1 after. Charge and energy would work out, but the other conservation laws forbid it.")),
                background = l("Viele denkbare Reaktionen werden nie beobachtet, obwohl Energie und Ladung stimmen würden. Daraus schloss man auf weitere Erhaltungsgrößen. Die Baryonenzahl schützt das Proton: Als leichtestes Baryon kann es in nichts zerfallen. Deshalb ist die Materie um uns stabil.", "Many conceivable reactions are never observed, although energy and charge would balance. From this, physicists inferred further conserved quantities. Baryon number protects the proton: as the lightest baryon it has nothing to decay into. That's why the matter around us is stable."),
                fact = l("Manche Theorien sagen voraus, dass auch die Baryonenzahl nicht ganz exakt erhalten ist. Gefunden hat man einen Protonenzerfall aber nie.", "Some theories predict that baryon number isn't exactly conserved either. But no proton decay has ever been found."),
            )
        },
        lab("phy.protonzerfall", Topic.Modern, X, 0.7f) {
            val tons = pick(50000.0, 22500.0); val tau = 1e34
            val nProt = tons * 1000 / 0.018 * 6.022e23 * 10; val perYear = nProt / tau
            task(
                question = l("Der Detektor Super-Kamiokande ist ein Tank mit ${n(tons, 0)} Tonnen reinem Wasser. Angenommen, Protonen zerfallen mit einer mittleren Lebensdauer von 10³⁴ Jahren: Wie viele Zerfälle pro Jahr müsste man sehen? (Ein Wassermolekül hat 10 Protonen, 18 g pro Mol.)", "The Super-Kamiokande detector is a tank holding ${n(tons, 0)} tonnes of pure water. Suppose protons decay with a mean lifetime of 10³⁴ years: how many decays per year should be seen? (A water molecule has 10 protons, 18 g per mole.)"),
                steps = listOf(num(l("Erwartete Zerfälle pro Jahr", "Expected decays per year"), perYear, "", rel = 0.04, done = """§\approx ${m(perYear, 1)}§""",
                    hints = listOf(l("Kernidee: Erst die Zahl der Protonen im Tank. Bei einer Lebensdauer τ zerfällt pro Jahr etwa der Anteil 1/τ.", "Key idea: first the number of protons in the tank. With a lifetime τ, roughly the fraction 1/τ decays each year."), """§N = \dfrac{m}{18\ \text{g/mol}} \cdot N_A \cdot 10,\quad \dfrac{N}{\tau}§"""))),
                solution = listOf("""§N = \dfrac{${sci(tons * 1e6)}\ \text{g}}{18\ \text{g/mol}} \cdot 6{,}02 \cdot 10^{23} \cdot 10 \approx ${sci(nProt)}§""", """§\dfrac{N}{\tau} \approx \dfrac{${sci(nProt)}}{10^{34}} \approx ${m(perYear, 1)}\ \text{pro Jahr}§""".let { l(it, it.replace("pro Jahr", "per year")) }),
                background = l("Eine Lebensdauer von 10³⁴ Jahren ist das Billiardenbillionenfache des Alters des Universums. Trotzdem kann man sie testen, weil man so unvorstellbar viele Protonen auf einmal beobachten kann. Seit Jahrzehnten lauscht der Detektor in einer Mine in Japan, ohne einen einzigen Zerfall gefunden zu haben. Das schließt viele Theorien aus.", "A lifetime of 10³⁴ years is a quadrillion trillion times the age of the universe. Yet it can be tested, because you can watch so unimaginably many protons at once. For decades the detector has been listening in a mine in Japan without finding a single decay. That rules out many theories."),
                fact = l("Super-Kamiokande sah 1998 als erster, dass Neutrinos ihre Art wechseln und deshalb eine Masse haben müssen.", "In 1998 Super-Kamiokande was the first to see that neutrinos change their type and therefore must have mass."),
            )
        },
        lab("phy.yukawa", Topic.Modern, X, 0.6f) {
            val range = pick(1.4, 1.5, 1.3); val mev = 197.3 / range
            task(
                question = l("Hideki Yukawa überlegte 1935: Die Kernkraft wirkt nur über etwa ${n(range, 1)} fm, also muss sie von einem Teilchen mit Masse übertragen werden. Nach der Unschärferelation reicht ein solches Teilchen etwa ħ/(m·c) weit. Welche Ruheenergie muss es haben (ħc = 197,3 MeV·fm)?", "In 1935 Hideki Yukawa reasoned: the nuclear force acts over only about ${n(range, 1)} fm, so it must be carried by a particle with mass. By the uncertainty principle such a particle reaches about ħ/(m·c). What rest energy must it have (ħc = 197.3 MeV·fm)?"),
                steps = listOf(num(l("Ruheenergie in MeV", "Rest energy in MeV"), mev, "MeV", rel = 0.03, done = """§m c^2 \approx ${m(mev, 0)}\ \text{MeV}§""",
                    hints = listOf(l("Kernidee: Ein Austauschteilchen leiht sich seine Energie nur kurz. Je schwerer, desto kürzer die Zeit, desto kürzer die Reichweite.", "Key idea: an exchange particle borrows its energy only briefly. The heavier it is, the shorter the time and the shorter the range."), """§R \approx \dfrac{\hbar}{m c} \Rightarrow m c^2 \approx \dfrac{\hbar c}{R}§"""))),
                solution = listOf("""§m c^2 \approx \dfrac{197{,}3\ \text{MeV fm}}{${m(range, 1)}\ \text{fm}} \approx ${m(mev, 0)}\ \text{MeV}§"""),
                background = l("In der Quantenfeldtheorie wirken Kräfte durch den Austausch von Teilchen. Photonen haben keine Masse, deshalb reicht die elektrische Kraft unendlich weit. Ein schweres Austauschteilchen kann nur kurz existieren und kommt nicht weit. Yukawas Vorhersage traf: 1947 fand man das Pion mit 140 MeV. Er bekam dafür 1949 den Nobelpreis.", "In quantum field theory forces act through the exchange of particles. Photons have no mass, so the electric force reaches infinitely far. A heavy exchange particle can only exist briefly and doesn't get far. Yukawa's prediction was spot on: in 1947 the pion was found, at 140 MeV. He won the Nobel Prize for it in 1949."),
                fact = l("Mit derselben Idee folgt aus der winzigen Reichweite der schwachen Kraft, dass ihre W-Bosonen fast hundertmal so schwer wie ein Proton sein müssen, was 1983 bestätigt wurde.", "The same idea shows from the tiny range of the weak force that its W bosons must be almost a hundred times as heavy as a proton, which was confirmed in 1983."),
            )
        },
    )
}
