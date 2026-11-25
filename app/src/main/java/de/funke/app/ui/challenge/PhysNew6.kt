package de.funke.app.ui.challenge

import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/* Physik-Ausbau (seit 13.2): Relativität, Quantenphysik, Atome & Kerne. Alle Texte selbst geschrieben. */

private const val C6 = 2.998e8
private const val H6 = 6.626e-34
private const val EV6 = 1.602e-19
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object PhysNew6 {
    val all: List<TaskDef> get() = relativity + quantum + nuclear

    private val relativity = listOf(
        lab("phy.lichtrunden", Topic.Relativity, E, 0.3f) {
            val n1 = 300000.0 / 40075
            task(
                question = l("Licht legt in einer Sekunde etwa 300.000 km zurück. Der Erdumfang am Äquator ist 40.075 km. Wie oft könnte Licht in einer Sekunde um die Erde laufen?", "Light travels about 300,000 km in one second. The Earth's circumference at the equator is 40,075 km. How many times could light go round the Earth in one second?"),
                steps = listOf(num(l("Anzahl Runden", "Number of laps"), n1, "", rel = 0.02, done = """§\approx ${m(n1, 1)}§""",
                    hints = listOf(l("Kernidee: Strecke pro Sekunde geteilt durch die Länge einer Runde.", "Key idea: distance per second divided by the length of one lap."), """§\dfrac{300\,000}{40\,075}§"""))),
                solution = listOf("""§\dfrac{300\,000\ \text{km}}{40\,075\ \text{km}} \approx ${m(n1, 1)}§"""),
                background = l("Die Lichtgeschwindigkeit ist die höchste Geschwindigkeit, mit der sich irgendetwas, auch eine Information, bewegen kann. Im Alltag wirkt sie unendlich, aber sie ist es nicht. Ein Signal von einem Ende Europas zum anderen braucht schon Millisekunden, und für Gespräche mit Mars-Rovern muss man viele Minuten warten.", "The speed of light is the fastest anything, even a piece of information, can move. In everyday life it seems infinite, but it isn't. A signal from one end of Europe to the other already takes milliseconds, and for conversations with Mars rovers you have to wait many minutes."),
                fact = l("Im Glasfaserkabel ist Licht etwa ein Drittel langsamer, dort schafft es nur rund fünf Runden pro Sekunde.", "In optical fibre light is about a third slower; there it manages only about five laps per second."),
            )
        },
        lab("phy.zwilling", Topic.Relativity, M, 0.6f) {
            task(
                question = l("Zwillingsparadoxon: Anna fliegt mit fast Lichtgeschwindigkeit zu einem Stern und zurück, Ben bleibt auf der Erde. Jeder sieht die Uhr des anderen langsamer gehen. Wer ist nach dem Wiedersehen jünger?", "Twin paradox: Anna flies to a star and back at almost the speed of light while Ben stays on Earth. Each sees the other's clock running slow. Who is younger when they meet again?"),
                steps = listOf(pick1(l("Wer ist jünger?", "Who is younger?"), l("Anna, die gereist ist", "Anna, who travelled"), listOf(l("Ben, der zu Hause blieb", "Ben, who stayed at home"), l("Beide gleich alt, die Effekte heben sich auf", "Both the same age; the effects cancel out"), l("Das hängt davon ab, wer zuschaut", "It depends on who is watching")),
                    hints = listOf(l("Kernidee: Die Lage ist nicht symmetrisch. Nur einer von beiden wechselt mitten in der Reise sein Bezugssystem.", "Key idea: the situation isn't symmetric. Only one of them changes reference frame halfway through the journey."), l("Wer muss bremsen, umkehren und wieder beschleunigen?", "Who has to brake, turn round and accelerate again?")))),
                solution = listOf(l("Anna kehrt um und wechselt dabei das Bezugssystem, Ben nicht. Dadurch ist die Situation nicht symmetrisch, und auf Annas Weg vergeht weniger Zeit. Sie ist jünger.", "Anna turns round and changes reference frame in doing so; Ben doesn't. So the situation isn't symmetric, and less time passes along Anna's path. She is younger.")),
                background = l("Das Paradox scheint unlösbar, weil Bewegung doch relativ ist. Aber nur gleichförmige Bewegung ist relativ. Anna muss umkehren und damit beschleunigen, was sie im Raumschiff spürt. In der Raumzeit ist ihr Weg ein anderer als Bens, und entlang ihres Weges vergeht weniger Eigenzeit. Das ist mit Atomuhren in Flugzeugen tatsächlich gemessen worden.", "The paradox seems impossible because motion is relative. But only uniform motion is relative. Anna has to turn round and therefore accelerate, which she can feel in the spaceship. In spacetime her path is different from Ben's, and less proper time passes along her path. This has actually been measured with atomic clocks on aeroplanes."),
                fact = l("Astronauten auf der ISS altern pro halbem Jahr etwa 5 Millisekunden langsamer als Menschen auf der Erde.", "Astronauts on the ISS age about 5 milliseconds less per half year than people on Earth."),
            )
        },
        lab("phy.batteriemasse", Topic.Relativity, M, 0.5f) {
            val kwh = pick(75.0, 60.0, 100.0); val dm = kwh * 3.6e6 / (C6 * C6) * 1e9
            task(
                question = l("Nach E = mc² hat Energie Masse. Um wie viel wird der Akku eines Elektroautos schwerer, wenn man ${n(kwh, 0)} kWh hineinlädt?", "According to E = mc², energy has mass. How much heavier does the battery of an electric car get when you charge ${n(kwh, 0)} kWh into it?"),
                steps = listOf(num(l("Massenzunahme in Mikrogramm", "Increase in mass in micrograms"), dm, "µg", rel = 0.03, done = """§\Delta m \approx ${m(dm, 1)}\ \mu\text{g}§""",
                    hints = listOf(l("Kernidee: Kilowattstunden in Joule umrechnen (1 kWh = 3,6 · 10⁶ J), dann durch c² teilen.", "Key idea: convert kilowatt-hours into joules (1 kWh = 3.6 · 10⁶ J), then divide by c²."), """§\Delta m = \dfrac{E}{c^2}§"""))),
                solution = listOf("""§E = ${m(kwh, 0)} \cdot 3{,}6 \cdot 10^6\ \text{J} = ${sci(kwh * 3.6e6)}\ \text{J}§""", """§\Delta m = \dfrac{${sci(kwh * 3.6e6)}}{(3 \cdot 10^8)^2}\ \text{kg} \approx ${m(dm, 1)}\ \mu\text{g}§"""),
                background = l("E = mc² gilt nicht nur für Atombomben, sondern für jede Energie. Eine gespannte Feder, eine warme Tasse Tee, ein geladener Akku: Alles ist um E/c² schwerer. Weil c² so riesig ist, merkt man das im Alltag nie. Erst bei Kernreaktionen, bei denen ein messbarer Teil der Masse umgewandelt wird, wird der Effekt groß.", "E = mc² doesn't only apply to atomic bombs but to every kind of energy. A compressed spring, a warm cup of tea, a charged battery: everything is heavier by E/c². Because c² is so enormous, you never notice it in everyday life. Only in nuclear reactions, where a measurable part of the mass is converted, does the effect become large."),
                fact = l("Ein Mikrogramm ist etwa die Masse eines winzigen Staubkorns, bei einem Akku, der eine halbe Tonne wiegt.", "A microgram is about the mass of a tiny speck of dust, for a battery that weighs half a tonne."),
            )
        },
        lab("phy.sonnenverlust", Topic.Relativity, M, 0.5f) {
            val l0 = 3.83e26; val dm = l0 / (C6 * C6)
            task(
                question = l("Die Sonne strahlt jede Sekunde 3,83 · 10²⁶ J ab. Wie viel Masse verliert sie dadurch pro Sekunde?", "The Sun radiates 3.83 · 10²⁶ J every second. How much mass does it lose per second as a result?"),
                steps = listOf(num(l("Masse in Millionen Tonnen", "Mass in millions of tonnes"), dm / 1e9, l("Mio. t", "million t"), rel = 0.03, done = """§\Delta m \approx ${m(dm / 1e9, 2)} \cdot 10^6\ \text{t}§""",
                    hints = listOf(l("Kernidee: Die abgestrahlte Energie hat nach E = mc² eine Masse.", "Key idea: by E = mc² the radiated energy has a mass."), """§\Delta m = \dfrac{E}{c^2}§"""))),
                solution = listOf("""§\Delta m = \dfrac{3{,}83 \cdot 10^{26}}{(3 \cdot 10^8)^2}\ \text{kg} \approx ${sci(dm)}\ \text{kg} \approx ${m(dm / 1e9, 2)} \cdot 10^6\ \text{t}§"""),
                background = l("Im Kern der Sonne verschmelzen Wasserstoffkerne zu Helium. Ein Heliumkern ist etwas leichter als die vier Protonen, aus denen er entsteht, und genau dieser Massenunterschied wird als Energie frei. Jede Sekunde werden so Millionen Tonnen Masse in Licht verwandelt.", "In the Sun's core hydrogen nuclei fuse into helium. A helium nucleus is slightly lighter than the four protons it forms from, and exactly this difference in mass is released as energy. Every second millions of tonnes of mass are turned into light."),
                fact = l("Trotzdem hat die Sonne in ihren 4,6 Milliarden Jahren erst etwa 0,03 % ihrer Masse verloren.", "Even so, the Sun has lost only about 0.03% of its mass in its 4.6 billion years."),
            )
        },
        lab("phy.lichtuhr", Topic.Relativity, M, 0.6f) {
            task(
                question = l("Eine Lichtuhr: Ein Lichtpuls läuft zwischen zwei Spiegeln auf und ab, jeder Durchlauf ist ein Tick. Die Uhr fliegt in einem Raumschiff schnell an dir vorbei. Warum siehst du sie langsamer ticken?", "A light clock: a pulse of light bounces up and down between two mirrors, and each trip is one tick. The clock flies past you fast in a spaceship. Why do you see it ticking more slowly?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Für dich läuft der Puls schräg, also einen längeren Weg, aber nicht schneller als c. Deshalb dauert jeder Tick länger.", "For you the pulse travels diagonally, so a longer path, but no faster than c. That's why every tick takes longer."),
                    listOf(l("Das Licht wird durch die Bewegung abgebremst.", "The light is slowed down by the motion."), l("Die Spiegel rücken durch die Bewegung auseinander.", "The mirrors move apart because of the motion."), l("Das ist eine Täuschung, weil das Licht der Uhr länger zu dir braucht.", "It's an illusion, because the clock's light takes longer to reach you.")),
                    hints = listOf(l("Kernidee: Die Lichtgeschwindigkeit ist für alle gleich, auch für dich.", "Key idea: the speed of light is the same for everyone, you included."), l("Während der Puls nach oben läuft, bewegt sich der obere Spiegel ein Stück weiter.", "While the pulse travels upwards, the upper mirror moves on a little.")))),
                solution = listOf(l("Aus deiner Sicht ist der Weg des Pulses die Schräge eines Dreiecks, länger als der senkrechte Abstand. Bei gleicher Geschwindigkeit c braucht er länger. Mit Pythagoras ergibt sich genau der Faktor γ = 1/√(1 − v²/c²).", "From your point of view the pulse's path is the slanted side of a triangle, longer than the vertical distance. At the same speed c it takes longer. Pythagoras gives exactly the factor γ = 1/√(1 − v²/c²).")),
                background = l("Einstein ging von zwei Annahmen aus: Die Naturgesetze sind für alle gleichförmig bewegten Beobachter gleich, und Licht ist für alle gleich schnell. Die Lichtuhr zeigt, dass daraus zwingend folgt: Bewegte Uhren gehen langsamer. Und weil jede Uhr, auch ein Herzschlag, mit der Lichtuhr Schritt halten muss, gilt das für die Zeit selbst.", "Einstein started from two assumptions: the laws of nature are the same for all observers in uniform motion, and light travels at the same speed for everyone. The light clock shows that it follows inevitably: moving clocks run slow. And because every clock, a heartbeat included, has to keep pace with the light clock, this applies to time itself."),
                fact = l("Für das Raumschiff selbst tickt die Uhr ganz normal. Dort sieht man umgekehrt deine Uhr langsamer gehen.", "For the spaceship itself, the clock ticks completely normally. There, conversely, they see your clock running slow."),
            )
        },
        lab("phy.starshot", Topic.Relativity, H, 0.6f) {
            val beta = pick(0.2, 0.15, 0.25); val mass = 1e-3
            val gamma = 1 / sqrt(1 - beta * beta); val ek = (gamma - 1) * mass * C6 * C6; val years = 4.37 / beta
            task(
                question = l("Eine Mini-Raumsonde mit 1 g Masse soll von Lasern auf ${n(beta * 100, 0)} % der Lichtgeschwindigkeit gebracht werden und so Alpha Centauri (4,37 Lichtjahre) erreichen. Wie viel Bewegungsenergie braucht sie (relativistisch gerechnet)?", "A mini space probe with a mass of 1 g is to be pushed by lasers to ${n(beta * 100, 0)}% of the speed of light and so reach Alpha Centauri (4.37 light years). How much kinetic energy does it need (calculated relativistically)?"),
                steps = listOf(num(l("Energie in Terajoule", "Energy in terajoules"), ek / 1e12, "TJ", rel = 0.03, done = """§E \approx ${m(ek / 1e12, 2)}\ \text{TJ}§""",
                    hints = listOf(l("Kernidee: Relativistische Bewegungsenergie ist (γ − 1)·m·c².", "Key idea: relativistic kinetic energy is (γ − 1)·m·c²."), """§\gamma = \dfrac{1}{\sqrt{1 - ${m(beta, 2)}^2}}§"""))),
                solution = listOf("""§\gamma = \dfrac{1}{\sqrt{1 - ${m(beta * beta, 4)}}} \approx ${m(gamma, 4)}§""", """§E = (${m(gamma, 4)} - 1) \cdot 10^{-3} \cdot (3 \cdot 10^8)^2\ \text{J} \approx ${m(ek / 1e12, 2)}\ \text{TJ}§"""),
                background = l("Bei 20 % der Lichtgeschwindigkeit weicht die relativistische Rechnung nur um wenige Prozent von der klassischen ab. Trotzdem ist die Energie gewaltig: etwa so viel, wie ein Kernkraftwerk in einer halben Stunde liefert, für ein einziges Gramm. Deshalb sind reale Raumsonden so langsam: Voyager 1 bräuchte über 70.000 Jahre bis zum nächsten Stern.", "At 20% of the speed of light the relativistic calculation differs from the classical one by only a few percent. Still, the energy is enormous: about as much as a nuclear power station delivers in half an hour, for a single gram. That's why real space probes are so slow: Voyager 1 would need over 70,000 years to reach the nearest star."),
                fact = l("Die Reise würde etwa ${n(years, 0)} Jahre dauern, plus 4,37 Jahre, bis die Funksignale zurück sind.", "The journey would take about ${n(years, 0)} years, plus 4.37 years for the radio signals to come back."),
            )
        },
        lab("phy.ekinverh", Topic.Relativity, H, 0.6f) {
            val beta = pick(0.9, 0.8, 0.99); val gamma = 1 / sqrt(1 - beta * beta)
            val ratio = (gamma - 1) / (0.5 * beta * beta)
            task(
                question = l("Ein Teilchen fliegt mit ${n(beta, 2)} c. Wie viel mal größer ist seine wirkliche Bewegungsenergie als die klassische Formel ½·m·v² behauptet?", "A particle travels at ${n(beta, 2)} c. How many times larger is its true kinetic energy than the classical formula ½·m·v² claims?"),
                steps = listOf(num(l("Verhältnis", "Ratio"), ratio, "", rel = 0.02, done = """§\approx ${m(ratio, 2)}§""",
                    hints = listOf(l("Kernidee: Relativistisch gilt E = (γ − 1)·m·c². Teile durch ½·m·v², dann kürzt sich m·c² heraus.", "Key idea: relativistically E = (γ − 1)·m·c². Divide by ½·m·v², and m·c² cancels."), """§\dfrac{E_{rel}}{E_{klass}} = \dfrac{\gamma - 1}{\tfrac{1}{2}\beta^2}§""".let { l(it, it.replace("klass", "class")) }))),
                solution = listOf("""§\gamma = \dfrac{1}{\sqrt{1 - ${m(beta, 2)}^2}} \approx ${m(gamma, 3)}§""", """§\dfrac{${m(gamma, 3)} - 1}{0{,}5 \cdot ${m(beta, 2)}^2} \approx ${m(ratio, 2)}§"""),
                background = l("Je näher man der Lichtgeschwindigkeit kommt, desto mehr Energie kostet jedes weitere bisschen Geschwindigkeit. Bei c würde die Energie unendlich. Deshalb kann nichts mit Masse die Lichtgeschwindigkeit erreichen, egal wie lange man beschleunigt. Teilchenbeschleuniger stecken fast ihre ganze Energie in γ, nicht mehr in die Geschwindigkeit.", "The closer you get to the speed of light, the more energy each extra bit of speed costs. At c the energy would be infinite. That's why nothing with mass can reach the speed of light, however long it is accelerated. Particle accelerators put almost all their energy into γ, not into speed any more."),
                fact = l("Bei kleinen Geschwindigkeiten ist das Verhältnis fast genau 1: Deshalb funktioniert die klassische Physik im Alltag so gut.", "At low speeds the ratio is almost exactly 1: that's why classical physics works so well in everyday life."),
            )
        },
        lab("phy.zehnjahre", Topic.Relativity, H, 0.6f) {
            val ratio = pick(10.0, 5.0, 2.0); val beta = sqrt(1 - 1 / (ratio * ratio))
            task(
                question = l("Ein Raumschiff fliegt so schnell, dass an Bord nur 1 Jahr vergeht, während auf der Erde ${n(ratio, 0)} Jahre vergehen. Wie schnell fliegt es, in Bruchteilen der Lichtgeschwindigkeit?", "A spaceship flies so fast that only 1 year passes on board while ${n(ratio, 0)} years pass on Earth. How fast is it flying, as a fraction of the speed of light?"),
                steps = listOf(num(l("Geschwindigkeit in c", "Speed in units of c"), beta, "c", abs = 0.002, done = """§v \approx ${m(beta, 4)}\, c§""",
                    hints = listOf(l("Kernidee: Der Faktor der Zeitdehnung ist γ = ${n(ratio, 0)}. Löse die Formel für γ nach v auf.", "Key idea: the time dilation factor is γ = ${n(ratio, 0)}. Solve the formula for γ for v."), """§\gamma = \dfrac{1}{\sqrt{1 - \beta^2}} \Rightarrow \beta = \sqrt{1 - \dfrac{1}{\gamma^2}}§"""))),
                solution = listOf("""§\beta = \sqrt{1 - \dfrac{1}{${m(ratio, 0)}^2}} \approx ${m(beta, 4)}§"""),
                background = l("Die Zeitdehnung wächst nicht gleichmäßig mit der Geschwindigkeit. Bis zur halben Lichtgeschwindigkeit ist sie klein (15 %), danach steigt sie steil an. Für einen Faktor 10 muss man schon bis auf ein halbes Prozent an c heran. Auf einer solchen Reise könnte man in einem Leben Sterne erreichen, die viele Lichtjahre entfernt sind, aber zu Hause wäre die Welt eine andere.", "Time dilation doesn't grow evenly with speed. Up to half the speed of light it is small (15%), after that it rises steeply. For a factor of 10 you already have to get within half a percent of c. On such a journey you could reach stars many light years away within one lifetime, but at home the world would be a different one."),
                fact = l("Für die Crew sieht es aus, als wäre die Strecke geschrumpft: Aus ihrer Sicht ist der Stern um denselben Faktor näher.", "For the crew it looks as if the distance has shrunk: from their point of view the star is closer by the same factor."),
            )
        },
        lab("phy.gleichzeitigkeit", Topic.Relativity, H, 0.7f) {
            task(
                question = l("Ein Zug fährt sehr schnell an einem Bahnsteig vorbei. Für Bea auf dem Bahnsteig schlagen zwei Blitze genau gleichzeitig vorn und hinten am Zug ein, während der Zugmitte gerade an ihr vorbeikommt. Was sieht Tom, der in der Zugmitte sitzt?", "A train passes a platform very fast. For Bea on the platform, two lightning bolts strike the front and the back of the train at exactly the same moment, just as the middle of the train passes her. What does Tom, sitting in the middle of the train, see?"),
                steps = listOf(pick1(l("Für Tom …", "For Tom …"), l("schlägt der vordere Blitz zuerst ein", "the front bolt strikes first"), listOf(l("schlagen beide gleichzeitig ein", "both strike at the same time"), l("schlägt der hintere Blitz zuerst ein", "the rear bolt strikes first"), l("hängt es davon ab, wie laut der Donner ist", "it depends on how loud the thunder is")),
                    hints = listOf(l("Kernidee: Tom fährt dem Licht des vorderen Blitzes entgegen und vor dem hinteren davon.", "Key idea: Tom is moving towards the light of the front bolt and away from that of the rear one."), l("Für Tom sind beide Blitze gleich weit weg, und Licht ist für ihn in beide Richtungen gleich schnell.", "For Tom both bolts are equally far away, and for him light is equally fast in both directions.")))),
                solution = listOf(l("Tom bekommt das Licht von vorn zuerst. Weil er in der Mitte sitzt und Licht für ihn in beide Richtungen gleich schnell ist, schließt er: Der vordere Blitz hat früher eingeschlagen. Gleichzeitigkeit hängt vom Bezugssystem ab.", "Tom receives the light from the front first. Since he sits in the middle and light is equally fast for him in both directions, he concludes: the front bolt struck earlier. Simultaneity depends on the frame of reference.")),
                background = l("Mit genau diesem Gedankenexperiment zeigte Einstein 1905, dass es keine absolute Gleichzeitigkeit gibt. Was für den einen gleichzeitig passiert, geschieht für einen anderen nacheinander. Beide haben recht. Das ist der eigentliche Kern der Relativitätstheorie, aus dem Zeitdehnung und Längenkontraktion folgen.", "With exactly this thought experiment Einstein showed in 1905 that there is no absolute simultaneity. What happens simultaneously for one person happens one after the other for another. Both are right. This is the real core of the theory of relativity, from which time dilation and length contraction follow."),
                fact = l("Für Ereignisse, die durch ein Lichtsignal verbunden sein könnten, bleibt die Reihenfolge immer gleich. Ursache und Wirkung werden nie vertauscht.", "For events that could be connected by a light signal, the order always stays the same. Cause and effect are never swapped."),
            )
        },
        lab("phy.lhcgamma", Topic.Relativity, X, 0.7f) {
            val tev = pick(6.8, 7.0, 6.5); val gamma = tev * 1e6 / 938.3; val dv = C6 / (2 * gamma * gamma)
            task(
                question = l("Im LHC am CERN haben Protonen eine Energie von ${n(tev, 1)} TeV. Ihre Ruheenergie ist 938 MeV.", "In the LHC at CERN, protons have an energy of ${n(tev, 1)} TeV. Their rest energy is 938 MeV."),
                steps = listOf(
                    num(l("Wie groß ist ihr Lorentzfaktor γ?", "What is their Lorentz factor γ?"), gamma, "", rel = 0.02, done = """§\gamma \approx ${m(sig(gamma))}§""",
                        hints = listOf(l("Kernidee: Die Gesamtenergie ist γ mal die Ruheenergie.", "Key idea: the total energy is γ times the rest energy."), """§\gamma = \dfrac{E}{m c^2}§""")),
                    num(l("Um wie viele Meter pro Sekunde sind sie langsamer als das Licht?", "By how many metres per second are they slower than light?"), dv, "m/s", rel = 0.03, done = """§c - v \approx ${m(dv, 1)}\ \text{m/s}§""",
                        hints = listOf(l("Für große γ gilt die Näherung 1 − v/c ≈ 1/(2γ²).", "For large γ the approximation 1 − v/c ≈ 1/(2γ²) applies."), """§c - v \approx \dfrac{c}{2\gamma^2}§""")),
                ),
                solution = listOf("""§\gamma = \dfrac{${m(tev * 1e6, 0)}\ \text{MeV}}{938{,}3\ \text{MeV}} \approx ${m(sig(gamma))}§""", """§c - v \approx \dfrac{3 \cdot 10^8}{2 \cdot ${m(sig(gamma))}^2}\ \tfrac{\text{m}}{\text{s}} \approx ${m(dv, 1)}\ \tfrac{\text{m}}{\text{s}}§"""),
                background = l("Die Protonen im LHC haben über siebentausendmal mehr Energie als in Ruhe, und doch sind sie nur um einen Fußgängerschritt pro Sekunde langsamer als Licht. Die ganze zusätzliche Energie steckt im Faktor γ. Für sie selbst ist der 27-km-Ring durch die Längenkontraktion nur wenige Meter lang.", "The protons in the LHC have over seven thousand times more energy than at rest, and yet they are only about walking pace slower than light. All the extra energy is in the factor γ. For the protons themselves the 27 km ring is only a few metres long because of length contraction."),
                fact = l("Ein Lichtstrahl, der mit ihnen startet, wäre nach einer Runde von 27 km nur wenige Zehntelmillimeter voraus.", "A beam of light starting alongside them would be only a few tenths of a millimetre ahead after one 27 km lap."),
            )
        },
        lab("phy.reldoppler", Topic.Relativity, X, 0.6f) {
            val beta = pick(0.6, 0.8, 0.28); val f = sqrt((1 + beta) / (1 - beta)); val lam = 656.0 * f
            task(
                question = l("Eine Galaxie entfernt sich mit ${n(beta, 2)} c von uns. Wasserstoff leuchtet dort bei 656 nm (rot). Bei welcher Wellenlänge kommt das Licht bei uns an?", "A galaxy is moving away from us at ${n(beta, 2)} c. Hydrogen there glows at 656 nm (red). At what wavelength does the light arrive here?"),
                steps = listOf(num(l("Wellenlänge in nm", "Wavelength in nm"), lam, "nm", rel = 0.02, done = """§\lambda \approx ${m(lam, 0)}\ \text{nm}§""",
                    hints = listOf(l("Kernidee: Relativistischer Dopplereffekt. Zur normalen Verschiebung kommt die Zeitdehnung der bewegten Quelle dazu.", "Key idea: the relativistic Doppler effect. On top of the ordinary shift comes the time dilation of the moving source."), """§\dfrac{\lambda}{\lambda_0} = \sqrt{\dfrac{1 + \beta}{1 - \beta}}§"""))),
                solution = listOf("""§\lambda = 656\ \text{nm} \cdot \sqrt{\dfrac{1 + ${m(beta, 2)}}{1 - ${m(beta, 2)}}} \approx ${m(lam, 0)}\ \text{nm}§"""),
                background = l("Entfernt sich eine Lichtquelle, werden die Wellen gestreckt, das Licht wird röter. Bei hohen Geschwindigkeiten kommt hinzu, dass die Uhren der Quelle langsamer gehen, sie schwingt also auch langsamer. Rotverschiebung ist eines der wichtigsten Werkzeuge der Astronomie: Sie verrät Geschwindigkeit und Entfernung von Galaxien.", "When a light source moves away, the waves are stretched and the light becomes redder. At high speeds you also have to add that the source's clocks run slow, so it vibrates more slowly too. Redshift is one of the most important tools in astronomy: it reveals the speed and distance of galaxies."),
                fact = l("Für sehr ferne Galaxien ist die Rotverschiebung eigentlich keine Bewegung im Raum, sondern die Dehnung des Raums selbst während der langen Reise des Lichts.", "For very distant galaxies the redshift isn't really motion through space but the stretching of space itself during the light's long journey."),
            )
        },
    )

    private val quantum = listOf(
        lab("phy.welleteilchen", Topic.Quantum, E, 0.5f) {
            task(
                question = l("Licht erzeugt hinter einem Doppelspalt Streifen wie Wasserwellen. Beim Fotoeffekt schlägt es aber einzelne Elektronen heraus, wie Kugeln. Was ist Licht denn nun?", "Behind a double slit light makes stripes like water waves. But in the photoelectric effect it knocks out individual electrons, like little balls. So what is light?"),
                steps = listOf(pick1(l("Licht ist …", "Light is …"), l("beides: Es breitet sich wie eine Welle aus und gibt seine Energie in Portionen ab (Photonen)", "both: it travels like a wave and delivers its energy in packets (photons)"), listOf(l("eine Welle, der Fotoeffekt ist eine Ausnahme", "a wave; the photoelectric effect is an exception"), l("Teilchen, die Streifen sind eine Täuschung", "particles; the stripes are an illusion"), l("mal das eine, mal das andere, je nach Farbe", "sometimes one, sometimes the other, depending on its colour")),
                    hints = listOf(l("Kernidee: Beide Experimente sind sauber und reproduzierbar. Keins davon kann man wegerklären.", "Key idea: both experiments are clean and reproducible. Neither can be explained away."), l("Auch Elektronen erzeugen hinter einem Doppelspalt Streifen.", "Electrons also make stripes behind a double slit.")))),
                solution = listOf(l("Licht ist ein Quantenobjekt. Wohin es kommt, beschreibt eine Welle, und die Energie kommt immer in ganzen Portionen an. Selbst einzelne Photonen, die nacheinander durch den Doppelspalt fliegen, bauen Punkt für Punkt das Streifenmuster auf.", "Light is a quantum object. Where it goes is described by a wave, and its energy always arrives in whole packets. Even single photons flying through the double slit one after another build up the stripe pattern dot by dot.")),
                background = l("Die Quantenphysik sagt: Unsere Alltagsbilder von Welle und Teilchen passen beide nicht ganz. Ein Quantenobjekt breitet sich aus wie eine Welle, aber wenn es irgendwo nachgewiesen wird, dann immer als Ganzes an einem Punkt. Das gilt für Licht ebenso wie für Elektronen, Atome und sogar große Moleküle.", "Quantum physics says: neither of our everyday pictures of wave and particle quite fits. A quantum object spreads out like a wave, but when it is detected somewhere, it's always as a whole at one point. This applies to light as much as to electrons, atoms and even large molecules."),
                fact = l("Einstein bekam seinen Nobelpreis nicht für die Relativitätstheorie, sondern 1921 für die Erklärung des Fotoeffekts mit Lichtquanten.", "Einstein didn't get his Nobel Prize for relativity but, in 1921, for explaining the photoelectric effect with quanta of light."),
            )
        },
        lab("phy.baseballwelle", Topic.Quantum, M, 0.5f) {
            val mass = 0.145; val v = pick(40.0, 30.0, 45.0); val lam = H6 / (mass * v)
            task(
                question = l("Auch ein Baseball (145 g) ist nach de Broglie eine Welle. Welche Wellenlänge hat er bei ${n(v, 0)} m/s?", "According to de Broglie, a baseball (145 g) is a wave too. What is its wavelength at ${n(v, 0)} m/s?"),
                steps = listOf(big(l("Wellenlänge in Metern", "Wavelength in metres"), lam, "m", rel = 0.03, done = """§\lambda \approx ${sci(lam)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Die Wellenlänge ist das Plancksche Wirkungsquantum geteilt durch den Impuls.", "Key idea: the wavelength is Planck's constant divided by the momentum."), """§\lambda = \dfrac{h}{m v}§"""))),
                solution = listOf("""§\lambda = \dfrac{6{,}63 \cdot 10^{-34}}{0{,}145 \cdot ${m(v, 0)}}\ \text{m} \approx ${sci(lam)}\ \text{m}§"""),
                background = l("Welleneffekte wie Beugung zeigen sich nur, wenn Spalte oder Hindernisse etwa so groß sind wie die Wellenlänge. Beim Baseball ist sie unvorstellbar winzig, viel kleiner als ein Atomkern. Deshalb fliegen Bälle ganz klassisch. Für Elektronen dagegen ist die Wellenlänge so groß wie Atome, und dort beherrscht die Quantenphysik alles.", "Wave effects such as diffraction only show up when gaps or obstacles are about as big as the wavelength. For a baseball it is unimaginably tiny, far smaller than an atomic nucleus. That's why balls fly in an entirely classical way. For electrons, on the other hand, the wavelength is as big as atoms, and there quantum physics rules everything."),
                fact = l("Die schwersten Teilchen, bei denen man Interferenz gemessen hat, sind Moleküle aus über 2000 Atomen.", "The heaviest particles in which interference has been measured are molecules of over 2000 atoms."),
            )
        },
        lab("phy.qubitmessung", Topic.Quantum, M, 0.5f) {
            val (a2, b2) = pick(0.25 to 0.75, 0.5 to 0.5, 0.1 to 0.9)
            val aTxt = if (a2 == 0.25) """\tfrac{1}{2}""" else if (a2 == 0.5) """\tfrac{1}{\sqrt{2}}""" else """\sqrt{0{,}1}"""
            val bTxt = if (b2 == 0.75) """\tfrac{\sqrt{3}}{2}""" else if (b2 == 0.5) """\tfrac{1}{\sqrt{2}}""" else """\sqrt{0{,}9}"""
            task(
                question = l("""Ein Qubit ist im Zustand §|\psi\rangle = ${aTxt}\,|0\rangle + ${bTxt}\,|1\rangle§. Mit welcher Wahrscheinlichkeit misst man 1?""", """A qubit is in the state §|\psi\rangle = ${aTxt}\,|0\rangle + ${bTxt}\,|1\rangle§. With what probability do you measure 1?"""),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), b2 * 100, "%", abs = 0.5, done = """§P(1) = ${m(b2 * 100, 0)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Die Wahrscheinlichkeit ist das Quadrat des Betrags der Amplitude (Bornsche Regel).", "Key idea: the probability is the square of the magnitude of the amplitude (the Born rule)."), """§P(1) = |b|^2§"""))),
                solution = listOf("""§P(1) = \left|${bTxt}\right|^2 = ${m(b2, 2)} = ${m(b2 * 100, 0)}\ \text{%}§"""),
                background = l("Ein Qubit kann nicht nur 0 oder 1 sein, sondern eine Überlagerung von beidem. Erst die Messung zwingt es zu einer Antwort, und welche kommt, ist echter Zufall. Vorhersagen kann man nur die Wahrscheinlichkeiten, als Quadrate der Amplituden. Die Summe aller Wahrscheinlichkeiten muss 1 ergeben.", "A qubit can be not just 0 or 1 but a superposition of both. Only the measurement forces it to give an answer, and which one comes is genuine chance. Only the probabilities can be predicted, as squares of the amplitudes. The sum of all probabilities must equal 1."),
                fact = l("Nach der Messung ist die Überlagerung verschwunden: Misst man gleich noch einmal, kommt garantiert dasselbe Ergebnis.", "After the measurement the superposition is gone: measure again right away and you are guaranteed to get the same result."),
            )
        },
        lab("phy.qubits", Topic.Quantum, M, 0.5f) {
            val nq = pick(50, 40, 60); val amps = 2.0.pow(nq)
            task(
                question = l("Um einen Quantencomputer mit ${nq} Qubits auf einem normalen Computer zu simulieren, muss man für jede mögliche Kombination aus Nullen und Einsen eine Amplitude speichern. Wie viele Amplituden sind das?", "To simulate a quantum computer with ${nq} qubits on an ordinary computer, you have to store one amplitude for every possible combination of zeros and ones. How many amplitudes is that?"),
                steps = listOf(big(l("Anzahl Amplituden", "Number of amplitudes"), amps, "", rel = 0.02, done = """§2^{${nq}} \approx ${sci(amps)}§""",
                    hints = listOf(l("Kernidee: Jedes zusätzliche Qubit verdoppelt die Zahl der Kombinationen.", "Key idea: every extra qubit doubles the number of combinations."), """§N = 2^n§"""))),
                solution = listOf("""§2^{${nq}} \approx ${sci(amps)}§"""),
                background = l("Ein klassisches Bit speichert eine Zahl, n Qubits beschreiben eine Überlagerung aller 2ⁿ Zustände gleichzeitig. Mit jedem Qubit verdoppelt sich der Aufwand der Simulation. Schon bei etwa 50 Qubits braucht man Petabytes an Speicher. Das ist der Grund, warum Quantencomputer manche Aufgaben grundsätzlich schneller lösen könnten.", "A classical bit stores one number; n qubits describe a superposition of all 2ⁿ states at once. With every qubit the cost of simulation doubles. Already at about 50 qubits you need petabytes of memory. That's why quantum computers could in principle solve some problems faster."),
                fact = l("Mit 300 Qubits gäbe es mehr Amplituden als Atome im sichtbaren Universum.", "With 300 qubits there would be more amplitudes than atoms in the observable universe."),
            )
        },
        lab("phy.gluehfarbe", Topic.Quantum, M, 0.6f) {
            task(
                question = l("Ein Schmied erhitzt ein Stück Eisen. Es glüht erst dunkelrot, dann orange, gelb und schließlich weiß. Warum ändert sich die Farbe?", "A blacksmith heats a piece of iron. It glows dark red first, then orange, yellow and finally white. Why does the colour change?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Je heißer, desto mehr Energie haben die Lichtportionen, und das Maximum der Strahlung rückt zu kürzeren Wellenlängen.", "The hotter it is, the more energy the packets of light have, and the peak of the radiation moves to shorter wavelengths."),
                    listOf(l("Das Eisen verbrennt, und die Flammen haben verschiedene Farben.", "The iron burns, and the flames have different colours."), l("Rote Farbe verdampft zuerst aus dem Metall.", "The red colour evaporates out of the metal first."), l("Das Auge sieht heiße Dinge heller und deshalb weißer.", "The eye sees hot things more brightly and therefore whiter.")),
                    hints = listOf(l("Kernidee: Jeder heiße Körper strahlt ein ganzes Spektrum ab. Wo dessen Gipfel liegt, hängt nur von der Temperatur ab.", "Key idea: every hot body radiates a whole spectrum. Where its peak lies depends only on the temperature."), l("Max Planck erklärte das Spektrum 1900 nur mit der Annahme, dass Licht in Portionen E = h·f abgegeben wird.", "In 1900 Max Planck could only explain the spectrum by assuming that light is emitted in packets E = h·f.")))),
                solution = listOf(l("Der Gipfel der Wärmestrahlung verschiebt sich mit steigender Temperatur zu kürzeren Wellenlängen (Wiensches Gesetz). Zuerst reicht nur der rote Rand ins Sichtbare, bei höherer Temperatur kommen alle Farben dazu: Weiß.", "As the temperature rises, the peak of the thermal radiation shifts to shorter wavelengths (Wien's law). At first only the red edge reaches into the visible; at higher temperatures all colours are added: white.")),
                background = l("Die klassische Physik sagte voraus, dass ein heißer Körper unendlich viel ultraviolettes Licht abstrahlen müsste, die Ultraviolett-Katastrophe. Planck rettete die Rechnung mit einem Trick, an den er selbst nicht glaubte: Energie gibt es nur in Portionen h·f. Hohe Frequenzen sind dann teuer und werden selten. Das war 1900 die Geburt der Quantenphysik.", "Classical physics predicted that a hot body should radiate infinitely much ultraviolet light: the ultraviolet catastrophe. Planck rescued the calculation with a trick he didn't believe in himself: energy only comes in packets of h·f. High frequencies are then expensive and become rare. In 1900 that was the birth of quantum physics."),
                fact = l("Schmiede schätzen die Temperatur seit Jahrhunderten an der Glühfarbe ab: Kirschrot sind etwa 800 °C, Weißglut über 1300 °C.", "Blacksmiths have estimated temperature from the glow colour for centuries: cherry red is about 800 °C, white heat over 1300 °C."),
            )
        },
        lab("phy.pauli", Topic.Quantum, M, 0.5f) {
            val nShell = pick(2, 3, 4); val max = 2 * nShell * nShell
            task(
                question = l("Elektronen im Atom besetzen Schalen. Nach dem Pauli-Prinzip dürfen keine zwei Elektronen in allen Quantenzahlen übereinstimmen. In der Schale n = ${nShell} gibt es ${nShell * nShell} verschiedene Bahnzustände. Wie viele Elektronen passen höchstens hinein?", "Electrons in an atom occupy shells. By the Pauli principle no two electrons may agree in all their quantum numbers. In the shell n = ${nShell} there are ${nShell * nShell} different orbital states. What is the maximum number of electrons that fit?"),
                steps = listOf(num(l("Höchstzahl Elektronen", "Maximum number of electrons"), max.toDouble(), "", abs = 0.0, done = """§${max}§""",
                    hints = listOf(l("Kernidee: Jeder Bahnzustand kann zwei Elektronen aufnehmen, eines mit Spin nach oben und eines mit Spin nach unten.", "Key idea: each orbital state can hold two electrons, one with spin up and one with spin down."), """§2 n^2§"""))),
                solution = listOf("""§2 \cdot ${nShell}^2 = ${max}§"""),
                background = l("Ohne das Pauli-Prinzip würden alle Elektronen in die tiefste Schale fallen, und alle Atome wären sich chemisch ähnlich. Weil jede Schale nur begrenzt Platz hat, bauen sich Atome Schale für Schale auf. Daraus folgt der Aufbau des Periodensystems: Elemente mit gleich vielen Außenelektronen verhalten sich ähnlich.", "Without the Pauli principle all electrons would drop into the lowest shell, and all atoms would be chemically alike. Because each shell has limited room, atoms build up shell by shell. That gives the structure of the periodic table: elements with the same number of outer electrons behave alike."),
                fact = l("Das Pauli-Prinzip ist auch der Grund, warum feste Materie sich nicht zusammendrücken lässt und warum Weiße Zwerge nicht weiter kollabieren.", "The Pauli principle is also the reason why solid matter can't be squashed together and why white dwarfs don't collapse any further."),
            )
        },
        lab("phy.bandluecke", Topic.Quantum, M, 0.5f) {
            val (mat, eg) = pick(l("Silizium", "silicon") to 1.12, l("Galliumarsenid", "gallium arsenide") to 1.42, l("Germanium", "germanium") to 0.67)
            val lam = H6 * C6 / (eg * EV6) * 1e9
            task(
                question = l("In einer Solarzelle aus $mat muss ein Photon mindestens die Bandlücke von ${n(eg, 2)} eV liefern, um ein Elektron zu befreien. Bis zu welcher Wellenlänge kann die Zelle Licht nutzen?", "In a solar cell made of $mat a photon must supply at least the band gap of ${n(eg, 2)} eV to free an electron. Up to what wavelength can the cell use light?"),
                steps = listOf(num(l("Grenzwellenlänge in nm", "Cut-off wavelength in nm"), lam, "nm", rel = 0.02, done = """§\lambda \approx ${m(lam, 0)}\ \text{nm}§""",
                    hints = listOf(l("Kernidee: Längere Wellen haben weniger Energie pro Photon. An der Grenze ist die Photonenenergie genau die Bandlücke.", "Key idea: longer waves have less energy per photon. At the limit the photon energy equals the band gap exactly."), """§\lambda = \dfrac{h c}{E_g}§"""))),
                solution = listOf("""§\lambda = \dfrac{6{,}63 \cdot 10^{-34} \cdot 3 \cdot 10^8}{${m(eg, 2)} \cdot 1{,}6 \cdot 10^{-19}}\ \text{m} \approx ${m(lam, 0)}\ \text{nm}§"""),
                background = l("In einem Halbleiter sind die Elektronen gebunden, bis sie eine bestimmte Mindestenergie bekommen. Photonen mit weniger Energie gehen nutzlos durch, solche mit mehr Energie befreien zwar ein Elektron, der Überschuss wird aber zu Wärme. Deshalb kann eine einfache Solarzelle grundsätzlich nur etwa ein Drittel des Sonnenlichts in Strom verwandeln.", "In a semiconductor the electrons are bound until they receive a certain minimum energy. Photons with less energy pass through uselessly; those with more energy do free an electron, but the excess turns into heat. That's why a simple solar cell can in principle turn only about a third of sunlight into electricity."),
                fact = l("Mehrfachsolarzellen für Satelliten stapeln mehrere Halbleiter mit verschiedenen Bandlücken übereinander und erreichen über 40 %.", "Multi-junction solar cells for satellites stack several semiconductors with different band gaps on top of each other and reach over 40%."),
            )
        },
        lab("phy.austrittsarbeit", Topic.Quantum, H, 0.5f) {
            val lam = pick(400.0, 350.0, 450.0); val w = 2.1
            val ep = H6 * C6 / (lam * 1e-9) / EV6; val ek = ep - w
            task(
                question = l("Violettes Licht mit ${n(lam, 0)} nm fällt auf eine Cäsiumschicht. Um ein Elektron herauszulösen, braucht man 2,1 eV (Austrittsarbeit). Mit welcher Bewegungsenergie fliegen die schnellsten Elektronen davon?", "Violet light at ${n(lam, 0)} nm falls on a layer of caesium. Freeing an electron takes 2.1 eV (the work function). With what kinetic energy do the fastest electrons fly off?"),
                steps = listOf(num(l("Bewegungsenergie in eV", "Kinetic energy in eV"), ek, "eV", rel = 0.03, done = """§E_{kin} \approx ${m(ek, 2)}\ \text{eV}§""",
                    hints = listOf(l("Kernidee: Ein Photon gibt seine ganze Energie an ein Elektron. Ein Teil wird zum Herauslösen gebraucht, der Rest ist Bewegungsenergie.", "Key idea: one photon gives all its energy to one electron. Part of it is needed to get the electron out; the rest is kinetic energy."), """§E_{kin} = \dfrac{hc}{\lambda} - W_A§"""))),
                solution = listOf("""§E_{Ph} = \dfrac{1240\ \text{eV nm}}{${m(lam, 0)}\ \text{nm}} \approx ${m(ep, 2)}\ \text{eV}§""", """§E_{kin} = ${m(ep, 2)} - 2{,}1 \approx ${m(ek, 2)}\ \text{eV}§"""),
                background = l("Einsteins Gleichung für den Fotoeffekt sagt: Die Energie der Elektronen hängt nur von der Farbe des Lichts ab, nicht von seiner Helligkeit. Helleres Licht bringt mehr Photonen und damit mehr Elektronen, aber keine schnelleren. Unterhalb einer Grenzfrequenz kommt überhaupt kein Elektron heraus, egal wie hell es ist.", "Einstein's equation for the photoelectric effect says: the energy of the electrons depends only on the colour of the light, not on its brightness. Brighter light brings more photons and therefore more electrons, but not faster ones. Below a threshold frequency no electron comes out at all, however bright the light."),
                fact = l("Die Zahl hc = 1240 eV·nm ist ein praktischer Trick: Photonenenergie in eV ist 1240 geteilt durch die Wellenlänge in nm.", "The number hc = 1240 eV·nm is a handy trick: photon energy in eV is 1240 divided by the wavelength in nm."),
            )
        },
        lab("phy.rtm", Topic.Quantum, H, 0.6f) {
            val dz = pick(0.1, 0.2, 0.3); val factor = 10.0.pow(dz / 0.1)
            task(
                question = l("Beim Rastertunnelmikroskop fließt ein winziger Tunnelstrom zwischen einer Metallspitze und der Probe, ohne dass sie sich berühren. Er wird pro 0,1 nm mehr Abstand etwa zehnmal kleiner. Um welchen Faktor sinkt der Strom, wenn die Spitze ${n(dz, 1)} nm weiter weg ist?", "In a scanning tunnelling microscope a tiny tunnelling current flows between a metal tip and the sample without them touching. It gets about ten times smaller for every 0.1 nm of extra distance. By what factor does the current drop if the tip is ${n(dz, 1)} nm farther away?"),
                steps = listOf(num(l("Faktor", "Factor"), factor, "", rel = 0.02, done = """§${m(factor, 0)}§""",
                    hints = listOf(l("Kernidee: Die Wahrscheinlichkeit zu tunneln fällt exponentiell mit der Breite der Barriere. Jeder Schritt von 0,1 nm bringt wieder einen Faktor 10.", "Key idea: the probability of tunnelling falls exponentially with the width of the barrier. Every 0.1 nm step brings another factor of 10."), """§10^{\Delta z / 0{,}1\ \text{nm}}§"""))),
                solution = listOf("""§10^{${m(dz, 1)} / 0{,}1} = ${m(factor, 0)}§"""),
                background = l("Klassisch könnte kein Elektron die Lücke überspringen. Quantenmechanisch reicht seine Welle aber ein Stück in die verbotene Zone hinein, und mit einer kleinen Wahrscheinlichkeit taucht es auf der anderen Seite auf. Weil diese Wahrscheinlichkeit so extrem stark vom Abstand abhängt, kann das Mikroskop einzelne Atome abtasten.", "Classically no electron could jump the gap. Quantum mechanically, however, its wave reaches a little way into the forbidden zone, and with a small probability it turns up on the other side. Because this probability depends so extremely strongly on distance, the microscope can feel out individual atoms."),
                fact = l("1989 schrieben Forscher von IBM mit einem solchen Mikroskop das Firmenlogo aus 35 einzelnen Xenonatomen.", "In 1989 IBM researchers used such a microscope to spell out the company logo with 35 individual xenon atoms."),
            )
        },
        lab("phy.compton", Topic.Quantum, X, 0.6f) {
            val ang = pick(90.0, 60.0, 180.0); val dl = 2.426 * (1 - kotlin.math.cos(Math.toRadians(ang)))
            task(
                question = l("Röntgenlicht prallt an einem freien Elektron ab und wird um ${n(ang, 0)}° abgelenkt. Um wie viel wird seine Wellenlänge länger?", "X-rays bounce off a free electron and are deflected by ${n(ang, 0)}°. By how much does their wavelength increase?"),
                steps = listOf(num(l("Wellenlängenzunahme in Pikometer", "Increase in wavelength in picometres"), dl, "pm", rel = 0.02, done = """§\Delta\lambda \approx ${m(dl, 2)}\ \text{pm}§""",
                    hints = listOf(l("Kernidee: Photon und Elektron stoßen wie zwei Billardkugeln. Das Photon gibt Energie ab und wird dadurch langwelliger.", "Key idea: the photon and the electron collide like two billiard balls. The photon gives up energy and its wavelength gets longer."), """§\Delta\lambda = \dfrac{h}{m_e c}(1 - \cos\theta),\quad \dfrac{h}{m_e c} = 2{,}43\ \text{pm}§"""))),
                solution = listOf("""§\Delta\lambda = 2{,}43\ \text{pm} \cdot (1 - \cos ${m(ang, 0)}^\circ) \approx ${m(dl, 2)}\ \text{pm}§"""),
                background = l("Arthur Compton zeigte 1923, dass Licht nicht nur Energie, sondern auch Impuls in Portionen trägt. Die Verschiebung lässt sich genau berechnen, wenn man Photon und Elektron wie Teilchen behandelt, die mit Energie- und Impulserhaltung stoßen. Das überzeugte die letzten Zweifler, dass Photonen echte Teilchen sind.", "In 1923 Arthur Compton showed that light carries not only energy but also momentum in packets. The shift can be calculated exactly by treating the photon and electron as particles colliding with conservation of energy and momentum. That convinced the last doubters that photons are real particles."),
                fact = l("Bei sichtbarem Licht (500 000 pm) ist diese Verschiebung verschwindend klein, nur bei Röntgen- und Gammastrahlung merkt man sie.", "For visible light (500,000 pm) this shift is vanishingly small; it's only noticeable for X-rays and gamma rays."),
            )
        },
        lab("phy.nullpunkt", Topic.Quantum, X, 0.6f) {
            task(
                question = l("Alle Stoffe werden fest, wenn man sie nur kalt genug macht. Nur Helium bleibt bei normalem Druck bis zum absoluten Nullpunkt flüssig. Warum?", "Every substance becomes solid if you make it cold enough. Only helium stays liquid at normal pressure all the way down to absolute zero. Why?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Die Unschärferelation zwingt die leichten Heliumatome zu einer Nullpunktsbewegung, die stärker ist als ihre schwache Anziehung.", "The uncertainty principle forces the light helium atoms into a zero-point motion that is stronger than their weak attraction."),
                    listOf(l("Helium ist radioaktiv und erwärmt sich ständig selbst.", "Helium is radioactive and keeps heating itself."), l("Helium ist ein Gas und kann gar nicht flüssig werden.", "Helium is a gas and can't become liquid at all."), l("Am absoluten Nullpunkt hört jede Bewegung auf, dann gibt es keine Kristalle.", "At absolute zero all motion stops, so there are no crystals.")),
                    hints = listOf(l("Kernidee: Auch bei 0 K steht kein Teilchen völlig still, sonst wären Ort und Impuls zugleich genau bekannt.", "Key idea: even at 0 K no particle stands completely still, otherwise its position and momentum would both be known exactly."), l("Je leichter ein Teilchen, desto stärker zappelt es in einem engen Käfig.", "The lighter a particle, the more it jiggles in a tight cage.")))),
                solution = listOf(l("Ein Atom, das in einem Kristallgitter eingesperrt ist, muss wegen Δx·Δp ≥ ħ/2 eine Mindestbewegung haben. Heliumatome sind sehr leicht und ziehen sich als Edelgas kaum an. Ihre Nullpunktsbewegung reißt jeden Kristall auseinander. Erst bei etwa 25 bar Druck wird Helium fest.", "An atom locked in a crystal lattice must have a minimum motion because Δx·Δp ≥ ħ/2. Helium atoms are very light and, as a noble gas, hardly attract each other. Their zero-point motion tears every crystal apart. Only at about 25 bar of pressure does helium become solid.")),
                background = l("Die Unschärferelation ist kein Messproblem, sondern eine Eigenschaft der Natur. Ein Teilchen in einem kleinen Raum hat zwangsläufig eine Mindestenergie, die Nullpunktsenergie. Bei schweren Atomen ist sie winzig, bei Helium entscheidend. Flüssiges Helium wird unter 2,17 K sogar supraflüssig: Es fließt ohne jede Reibung und kriecht Gefäßwände hoch.", "The uncertainty principle isn't a measuring problem but a property of nature. A particle in a small space necessarily has a minimum energy, the zero-point energy. For heavy atoms it is tiny; for helium it is decisive. Below 2.17 K liquid helium even becomes superfluid: it flows without any friction and creeps up the walls of its container."),
                fact = l("Supraflüssiges Helium kühlt die Magnete des LHC am CERN auf 1,9 K, kälter als der Weltraum.", "Superfluid helium cools the magnets of the LHC at CERN to 1.9 K, colder than outer space."),
            )
        },
    )

    private val nuclear = listOf(
        lab("phy.rutherford", Topic.Nuclear, E, 0.4f) {
            task(
                question = l("1909 schoss man Alphateilchen auf eine hauchdünne Goldfolie. Fast alle flogen glatt hindurch, aber etwa eins von 8000 prallte zurück. Was folgt daraus?", "In 1909 alpha particles were fired at a wafer-thin gold foil. Almost all of them passed straight through, but about one in 8000 bounced back. What follows from this?"),
                steps = listOf(pick1(l("Das Atom …", "The atom …"), l("ist fast leer und hat einen winzigen, schweren Kern", "is almost empty and has a tiny, heavy nucleus"), listOf(l("ist eine gleichmäßige Kugel aus Ladung", "is a uniform ball of charge"), l("ist hohl wie ein Ballon", "is hollow like a balloon"), l("besteht nur aus Elektronen", "consists only of electrons")),
                    hints = listOf(l("Kernidee: Dass die meisten durchfliegen, zeigt, dass fast nichts im Weg ist.", "Key idea: that most of them fly through shows that almost nothing is in the way."), l("Dass einige zurückprallen, zeigt, dass es irgendwo etwas sehr Kleines und Schweres gibt.", "That some bounce back shows that somewhere there's something very small and heavy.")))),
                solution = listOf(l("Fast die ganze Masse und die positive Ladung stecken in einem Kern, der 100.000-mal kleiner ist als das Atom. Nur die wenigen Alphateilchen, die ihm sehr nahe kommen, werden stark abgelenkt.", "Almost all the mass and the positive charge sit in a nucleus 100,000 times smaller than the atom. Only the few alpha particles that come very close to it are strongly deflected.")),
                background = l("Vorher stellte man sich Atome wie einen Rosinenkuchen vor: positive Masse mit eingestreuten Elektronen. Ernest Rutherford sagte über das Ergebnis, es sei so unglaublich, als würde man eine Granate auf ein Blatt Papier schießen und sie käme zurück. Sein Atommodell mit Kern und Hülle gilt im Prinzip bis heute.", "Before that, atoms were imagined like a plum pudding: positive mass with electrons scattered through it. Ernest Rutherford said of the result that it was as incredible as firing a shell at a sheet of paper and having it come back. His model of the atom with nucleus and shell still holds in principle today."),
                fact = l("Wäre ein Atom so groß wie ein Fußballstadion, wäre der Kern eine Erbse am Mittelpunkt.", "If an atom were the size of a football stadium, the nucleus would be a pea at the centre spot."),
            )
        },
        lab("phy.flugdosis", Topic.Nuclear, E, 0.3f) {
            val h = pick(10.0, 12.0, 8.0); val rate = 5.0; val dose = h * rate
            task(
                question = l("In 10 km Höhe ist die kosmische Strahlung viel stärker als am Boden: etwa 5 Mikrosievert pro Stunde. Welche Dosis bekommst du auf einem ${n(h, 0)}-Stunden-Flug?", "At 10 km altitude cosmic radiation is much stronger than on the ground: about 5 microsieverts per hour. What dose do you receive on a ${n(h, 0)}-hour flight?"),
                steps = listOf(num(l("Dosis in Mikrosievert", "Dose in microsieverts"), dose, "µSv", rel = 0.02, done = """§D = ${m(dose, 0)}\ \mu\text{Sv}§""",
                    hints = listOf(l("Kernidee: Dosisleistung mal Zeit.", "Key idea: dose rate times time."), """§D = P \cdot t§""")),
                    ),
                solution = listOf("""§D = 5\ \tfrac{\mu\text{Sv}}{\text{h}} \cdot ${m(h, 0)}\ \text{h} = ${m(dose, 0)}\ \mu\text{Sv}§"""),
                background = l("Wir sind ständig natürlicher Strahlung ausgesetzt: aus dem Boden, aus der Nahrung, aus dem All. In Deutschland sind das im Mittel etwa 2,1 Millisievert im Jahr. Die Atmosphäre schirmt die kosmische Strahlung ab, je höher man ist, desto weniger Schutz. Ein Langstreckenflug bringt etwa so viel wie zwei Wochen am Boden.", "We are constantly exposed to natural radiation: from the ground, from food, from space. In Germany that's about 2.1 millisieverts a year on average. The atmosphere shields us from cosmic radiation; the higher you are, the less protection. A long-haul flight brings about as much as two weeks on the ground."),
                fact = l("Pilotinnen und Piloten gehören deshalb zu den beruflich am stärksten strahlenbelasteten Menschen, mehr als Beschäftigte in Kernkraftwerken.", "That's why pilots are among the people with the highest occupational radiation exposure, more than workers in nuclear power stations."),
            )
        },
        lab("phy.banane", Topic.Nuclear, M, 0.4f) {
            val kBan = pick(0.45, 0.4, 0.5); val a = 31.0
            val aBan = kBan * a; val aBody = 140 * a
            task(
                question = l("Natürliches Kalium enthält ein wenig radioaktives Kalium-40: 1 g Kalium hat eine Aktivität von etwa 31 Becquerel (Zerfälle pro Sekunde).", "Natural potassium contains a little radioactive potassium-40: 1 g of potassium has an activity of about 31 becquerels (decays per second)."),
                steps = listOf(
                    num(l("Welche Aktivität hat eine Banane mit ${n(kBan, 2)} g Kalium?", "What is the activity of a banana with ${n(kBan, 2)} g of potassium?"), aBan, "Bq", rel = 0.02, done = """§A \approx ${m(aBan, 0)}\ \text{Bq}§""",
                        hints = listOf(l("Kernidee: Die Aktivität ist proportional zur Menge des radioaktiven Stoffs.", "Key idea: the activity is proportional to the amount of radioactive material."), """§A = ${m(kBan, 2)} \cdot 31\ \text{Bq}§""")),
                    num(l("Und dein Körper mit etwa 140 g Kalium?", "And your body with about 140 g of potassium?"), aBody, "Bq", rel = 0.02, done = """§A \approx ${m(aBody, 0)}\ \text{Bq}§""",
                        hints = listOf(l("Dieselbe Rechnung mit der Kaliummenge im Körper.", "The same calculation with the amount of potassium in the body."), """§140 \cdot 31§""")),
                ),
                solution = listOf("""§A_{Banane} = ${m(kBan, 2)} \cdot 31 \approx ${m(aBan, 0)}\ \text{Bq}§""".let { l(it, it.replace("Banane", "banana")) }, """§A = 140 \cdot 31 = ${m(aBody, 0)}\ \text{Bq}§"""),
                background = l("Kalium ist lebenswichtig, und ein kleiner Teil davon ist seit der Entstehung der Erde radioaktiv. Deshalb strahlt jeder Mensch selbst: In deinem Körper zerfallen jede Sekunde tausende Kaliumkerne. Das ist völlig normal. Der Körper hält seinen Kaliumgehalt konstant, eine Banane mehr ändert also kaum etwas.", "Potassium is vital, and a small part of it has been radioactive since the Earth formed. That's why every person is radioactive: thousands of potassium nuclei decay in your body every second. That's completely normal. The body keeps its potassium level constant, so one more banana changes hardly anything."),
                fact = l("Die Bananen-Äquivalentdosis ist ein scherzhaftes Maß, mit dem Physiker kleine Strahlendosen anschaulich machen.", "The banana equivalent dose is a jokey measure physicists use to make small radiation doses easier to picture."),
            )
        },
        lab("phy.alphazerfall", Topic.Nuclear, M, 0.5f) {
            val (parent, a, z, kid) = pick(
                listOf(l("Uran-238", "Uranium-238"), "238", "92", l("Thorium-234", "Thorium-234")),
                listOf(l("Radium-226", "Radium-226"), "226", "88", l("Radon-222", "Radon-222")),
                listOf(l("Polonium-210", "Polonium-210"), "210", "84", l("Blei-206", "Lead-206")),
            )
            val wrong = when (a) {
                "238" -> listOf(l("Thorium-238", "Thorium-238"), l("Protactinium-234", "Protactinium-234"), l("Uran-234", "Uranium-234"))
                "226" -> listOf(l("Radon-226", "Radon-226"), l("Actinium-226", "Actinium-226"), l("Radium-222", "Radium-222"))
                else -> listOf(l("Blei-210", "Lead-210"), l("Bismut-206", "Bismuth-206"), l("Polonium-206", "Polonium-206"))
            }
            task(
                question = l("$parent (Ordnungszahl $z) sendet ein Alphateilchen aus, einen Heliumkern mit 2 Protonen und 2 Neutronen. Welcher Kern bleibt übrig?", "$parent (atomic number $z) emits an alpha particle, a helium nucleus with 2 protons and 2 neutrons. Which nucleus is left?"),
                steps = listOf(pick1(l("Tochterkern", "Daughter nucleus"), kid, wrong,
                    hints = listOf(l("Kernidee: Die Massenzahl sinkt um 4, die Ordnungszahl um 2.", "Key idea: the mass number drops by 4, the atomic number by 2."), l("Die Ordnungszahl bestimmt das Element: 90 ist Thorium, 86 Radon, 82 Blei.", "The atomic number determines the element: 90 is thorium, 86 radon, 82 lead.")))),
                solution = listOf(l("Massenzahl ${a} − 4, Ordnungszahl ${z} − 2. Das ergibt $kid.", "Mass number ${a} − 4, atomic number ${z} − 2. That gives $kid.")),
                background = l("Beim Alphazerfall verlässt ein Heliumkern den schweren Kern. Damit verliert er zwei Protonen und wird ein anderes Element, zwei Stellen weiter vorn im Periodensystem. Alphastrahlung ist sehr energiereich, kommt aber in Luft nur wenige Zentimeter weit. Gefährlich wird sie vor allem, wenn ein Alphastrahler eingeatmet wird.", "In alpha decay a helium nucleus leaves the heavy nucleus. It loses two protons and becomes a different element, two places earlier in the periodic table. Alpha radiation is very energetic but travels only a few centimetres in air. It becomes dangerous mainly when an alpha emitter is breathed in."),
                fact = l("Das Helium in Ballons stammt aus genau solchen Zerfällen: Es sammelt sich über Jahrmillionen in Erdgaslagerstätten.", "The helium in balloons comes from exactly such decays: it collects over millions of years in natural gas deposits."),
            )
        },
        lab("phy.betazerfall", Topic.Nuclear, M, 0.6f) {
            task(
                question = l("Kohlenstoff-14 (6 Protonen, 8 Neutronen) zerfällt durch Betazerfall zu Stickstoff-14 (7 Protonen, 7 Neutronen) und sendet dabei ein Elektron aus. Woher kommt das Elektron, Elektronen sitzen doch nicht im Kern?", "Carbon-14 (6 protons, 8 neutrons) decays by beta decay into nitrogen-14 (7 protons, 7 neutrons), emitting an electron. Where does the electron come from, since there are no electrons in the nucleus?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Ein Neutron wandelt sich in ein Proton um. Dabei entstehen ein Elektron und ein Antineutrino neu.", "A neutron turns into a proton. In the process an electron and an antineutrino are newly created."),
                    listOf(l("Ein Elektron aus der Hülle fällt in den Kern und wird zurückgeworfen.", "An electron from the shell falls into the nucleus and is thrown back out."), l("Im Neutron war schon ein Elektron versteckt.", "An electron was already hidden inside the neutron."), l("Das Elektron entsteht aus einem Photon.", "The electron is created from a photon.")),
                    hints = listOf(l("Kernidee: Die Zahl der Protonen steigt um eins, die der Neutronen sinkt um eins.", "Key idea: the number of protons rises by one, the number of neutrons falls by one."), l("Teilchen können bei Zerfällen entstehen, solange Ladung und Energie erhalten bleiben.", "Particles can be created in decays as long as charge and energy are conserved.")))),
                solution = listOf(l("Durch die schwache Kernkraft wird ein Neutron zum Proton: n → p + e⁻ + ν̄. Das Elektron gab es vorher nicht, es entsteht in diesem Moment, genau wie ein Photon beim Leuchten.", "Through the weak nuclear force a neutron turns into a proton: n → p + e⁻ + ν̄. The electron didn't exist before; it is created at that moment, just like a photon when something glows.")),
                background = l("Im Inneren des Neutrons wandelt sich ein Down-Quark in ein Up-Quark um und sendet ein W-Boson aus, das sofort in Elektron und Antineutrino zerfällt. Das Neutrino nimmt einen Teil der Energie mit, deshalb haben Betaelektronen keine feste Energie. Genau diese fehlende Energie brachte Wolfgang Pauli 1930 auf die Idee, dass es Neutrinos geben muss.", "Inside the neutron a down quark turns into an up quark and emits a W boson, which immediately decays into an electron and an antineutrino. The neutrino carries off part of the energy, which is why beta electrons don't have a fixed energy. Exactly this missing energy gave Wolfgang Pauli the idea in 1930 that neutrinos must exist."),
                fact = l("Der Zerfall von C-14 ist die Grundlage der Radiokarbonmethode, mit der man Funde bis etwa 50.000 Jahre zurück datiert.", "The decay of C-14 is the basis of radiocarbon dating, which dates finds back to about 50,000 years."),
            )
        },
        lab("phy.halbwertsdicke", Topic.Nuclear, M, 0.5f) {
            val half = pick(1.0, 1.2, 0.8); val target = pick(1000.0, 100.0)
            val nHalf = ceil(ln(target) / ln(2.0)); val d = nHalf * half
            task(
                question = l("Eine Bleischicht von ${n(half, 1)} cm halbiert die Gammastrahlung einer Quelle. Wie dick muss die Wand mindestens sein, damit höchstens ein ${n(target, 0)}stel durchkommt?", "A layer of lead ${n(half, 1)} cm thick halves the gamma radiation from a source. How thick must the wall be at least so that no more than one ${n(target, 0)}th gets through?"),
                steps = listOf(num(l("Dicke in cm", "Thickness in cm"), d, "cm", rel = 0.02, done = """§d = ${m(d, 1)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Jede weitere Halbwertsdicke halbiert noch einmal. Wie oft muss man halbieren, bis man unter 1/${n(target, 0)} ist?", "Key idea: every additional half-value layer halves it again. How often must you halve to get below 1/${n(target, 0)}?"), """§2^n \ge ${m(target, 0)} \Rightarrow n = ${m(nHalf, 0)}§"""))),
                solution = listOf("""§2^{${m(nHalf, 0)}} = ${m(2.0.pow(nHalf), 0)} \ge ${m(target, 0)}§""", """§d = ${m(nHalf, 0)} \cdot ${m(half, 1)}\ \text{cm} = ${m(d, 1)}\ \text{cm}§"""),
                background = l("Gammastrahlung lässt sich nie ganz abschirmen, nur abschwächen. Jede Schicht schluckt denselben Anteil der ankommenden Strahlung, die Intensität sinkt also exponentiell. Wie beim radioaktiven Zerfall rechnet man mit Halbierungen. Zehn Halbwertsdicken bringen etwa ein Tausendstel, zwanzig ein Millionstel.", "Gamma radiation can never be shielded completely, only weakened. Each layer absorbs the same fraction of the radiation arriving at it, so the intensity falls exponentially. As with radioactive decay, you calculate with halvings. Ten half-value layers give about a thousandth, twenty a millionth."),
                fact = l("Wasser schirmt auch ab, braucht aber etwa zehnmal mehr Dicke als Blei. Deshalb lagern abgebrannte Brennelemente in tiefen Wasserbecken.", "Water shields too, but needs about ten times the thickness of lead. That's why spent fuel rods are stored in deep pools of water."),
            )
        },
        lab("phy.erdwaerme", Topic.Nuclear, M, 0.6f) {
            task(
                question = l("Die Erde ist 4,5 Milliarden Jahre alt. Eine heiße Gesteinskugel dieser Größe hätte längst viel kälter sein müssen, rechnete Lord Kelvin um 1860 vor. Warum ist das Erdinnere trotzdem noch über 5000 °C heiß?", "The Earth is 4.5 billion years old. A hot ball of rock this size should have cooled down much more long ago, Lord Kelvin calculated around 1860. Why is the Earth's interior still over 5000 °C hot?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Radioaktive Stoffe im Gestein (Uran, Thorium, Kalium) zerfallen und heizen die Erde ständig nach.", "Radioactive substances in the rock (uranium, thorium, potassium) decay and keep reheating the Earth."),
                    listOf(l("Die Sonne heizt die Erde bis ins Innere auf.", "The Sun heats the Earth right into its interior."), l("Im Erdkern läuft Kernfusion wie in der Sonne.", "Nuclear fusion takes place in the Earth's core, as in the Sun."), l("Der Druck allein erzeugt immer neue Wärme.", "Pressure alone keeps producing new heat.")),
                    hints = listOf(l("Kernidee: Kelvin kannte eine Wärmequelle noch nicht, die erst um 1896 entdeckt wurde.", "Key idea: Kelvin didn't yet know of a heat source that was only discovered around 1896."), l("Lange Halbwertszeiten von Milliarden Jahren passen gut zum Alter der Erde.", "Long half-lives of billions of years fit well with the age of the Earth.")))),
                solution = listOf(l("Etwa die Hälfte des Wärmestroms aus dem Erdinneren stammt aus radioaktivem Zerfall, der Rest ist Restwärme aus der Entstehung. Mit dieser Quelle passte Kelvins Rechnung plötzlich zu einem viel höheren Alter.", "About half of the heat flowing out of the Earth's interior comes from radioactive decay; the rest is leftover heat from its formation. With this source, Kelvin's calculation suddenly fitted a much greater age.")),
                background = l("Kelvin schätzte das Alter der Erde aus ihrer Abkühlung auf 20 bis 400 Millionen Jahre, zu wenig für Darwins Evolution und die Geologie. Erst die Entdeckung der Radioaktivität löste den Streit. Heute misst man sogar Geoneutrinos aus dem Erdinneren und bestimmt damit direkt, wie viel Wärme der Zerfall liefert.", "Kelvin estimated the age of the Earth from its cooling at 20 to 400 million years, too little for Darwin's evolution and for geology. Only the discovery of radioactivity settled the dispute. Today we even detect geoneutrinos from inside the Earth and use them to measure directly how much heat decay supplies."),
                fact = l("Diese Wärme treibt die Plattentektonik an, also Vulkane, Erdbeben und das Wandern der Kontinente.", "This heat drives plate tectonics, that is, volcanoes, earthquakes and the drifting of the continents."),
            )
        },
        lab("phy.radiumaktivitaet", Topic.Nuclear, H, 0.6f) {
            val t = 1600.0 * 365.25 * 86400; val nAt = 6.022e23 / 226; val a = ln(2.0) / t * nAt
            task(
                question = l("Radium-226 hat eine Halbwertszeit von 1600 Jahren. Wie viele Kerne zerfallen pro Sekunde in 1 g Radium? Ein Mol Radium wiegt 226 g.", "Radium-226 has a half-life of 1600 years. How many nuclei decay per second in 1 g of radium? One mole of radium has a mass of 226 g."),
                steps = listOf(big(l("Aktivität in Becquerel", "Activity in becquerels"), a, "Bq", rel = 0.03, done = """§A \approx ${sci(a)}\ \text{Bq}§""",
                    hints = listOf(l("Kernidee: Erst die Zahl der Atome, dann die Zerfallskonstante λ = ln 2 / T½ (in Sekunden). Aktivität = λ · N.", "Key idea: first the number of atoms, then the decay constant λ = ln 2 / T½ (in seconds). Activity = λ · N."), """§N = \dfrac{6{,}02 \cdot 10^{23}}{226},\quad A = \dfrac{\ln 2}{T_{1/2}} N§"""))),
                solution = listOf("""§N \approx ${sci(nAt)},\quad T_{1/2} \approx ${sci(t)}\ \text{s}§""", """§A = \dfrac{0{,}693}{${sci(t)}} \cdot ${sci(nAt)}\ \text{Bq} \approx ${sci(a)}\ \text{Bq}§"""),
                background = l("Je kürzer die Halbwertszeit, desto stärker strahlt dieselbe Zahl von Atomen. Die Aktivität sagt, wie viele Kerne pro Sekunde zerfallen. Für Radium kommt fast genau eine runde Zahl heraus, und das ist kein Zufall: Die alte Einheit Curie wurde genau als Aktivität von 1 g Radium festgelegt, zu Ehren von Marie und Pierre Curie.", "The shorter the half-life, the more strongly the same number of atoms radiates. The activity says how many nuclei decay per second. For radium the result is almost exactly a round number, and that's no coincidence: the old unit, the curie, was defined as precisely the activity of 1 g of radium, in honour of Marie and Pierre Curie."),
                fact = l("Marie Curies Notizbücher strahlen noch heute und werden in Bleikisten aufbewahrt.", "Marie Curie's notebooks are still radioactive today and are kept in lead boxes."),
            )
        },
        lab("phy.kerndichte", Topic.Nuclear, H, 0.6f) {
            val aNuc = pick(56.0, 12.0, 208.0); val r = 1.2e-15 * aNuc.pow(1.0 / 3.0)
            val rho = aNuc * 1.66e-27 / (4.0 / 3.0 * Math.PI * r * r * r); val spoon = rho * 5e-6 / 1e3 / 1e6
            task(
                question = l("Ein Atomkern mit ${n(aNuc, 0)} Nukleonen hat den Radius 1,2 fm · A^(1/3), jedes Nukleon wiegt etwa 1,66 · 10⁻²⁷ kg.", "An atomic nucleus with ${n(aNuc, 0)} nucleons has a radius of 1.2 fm · A^(1/3), and each nucleon has a mass of about 1.66 · 10⁻²⁷ kg."),
                steps = listOf(
                    big(l("Wie groß ist die Dichte von Kernmaterie?", "What is the density of nuclear matter?"), rho, "kg/m³", rel = 0.03, done = """§\rho \approx ${sci(rho)}\ \text{kg/m}^3§""",
                        hints = listOf(l("Kernidee: Masse durch Volumen einer Kugel. Weil das Volumen mit A wächst, hängt das Ergebnis kaum vom Kern ab.", "Key idea: mass over the volume of a sphere. Because the volume grows with A, the result hardly depends on the nucleus."), """§\rho = \dfrac{A \cdot m_N}{\tfrac{4}{3}\pi r^3}§""")),
                    num(l("Wie schwer wäre ein Teelöffel (5 ml) davon, in Millionen Tonnen?", "How heavy would a teaspoon (5 ml) of it be, in millions of tonnes?"), spoon, l("Mio. t", "million t"), rel = 0.03, done = """§m \approx ${m(sig(spoon))} \cdot 10^6\ \text{t}§""",
                        hints = listOf(l("Dichte mal Volumen, 5 ml sind 5 · 10⁻⁶ m³.", "Density times volume; 5 ml is 5 · 10⁻⁶ m³."), """§m = \rho \cdot 5 \cdot 10^{-6}\ \text{m}^3§""")),
                ),
                solution = listOf("""§r = 1{,}2 \cdot ${m(aNuc, 0)}^{1/3}\ \text{fm} \approx ${m(r * 1e15, 2)}\ \text{fm}§""", """§\rho \approx ${sci(rho)}\ \tfrac{\text{kg}}{\text{m}^3},\quad m \approx ${sci(rho * 5e-6)}\ \text{kg}§"""),
                background = l("Alle Atomkerne sind ungefähr gleich dicht: Nukleonen liegen dicht an dicht wie Kugeln in einem Sack, und mehr Nukleonen heißt einfach ein größerer Sack. Gewöhnliche Materie ist so leicht, weil Atome fast leer sind. Neutronensterne bestehen aus fast reiner Kernmaterie.", "All atomic nuclei are roughly equally dense: nucleons are packed tightly together like marbles in a bag, and more nucleons simply means a bigger bag. Ordinary matter is so light because atoms are almost empty. Neutron stars are made of almost pure nuclear matter."),
                fact = l("Würde man alle Menschen zu Kernmaterie zusammendrücken, passten sie in einen Würfel von der Größe eines Zuckerstücks.", "If all the people on Earth were squashed into nuclear matter, they would fit into a cube the size of a sugar lump."),
            )
        },
        lab("phy.bindungskurve", Topic.Nuclear, H, 0.6f) {
            task(
                question = l("Die Sonne gewinnt Energie, indem sie leichte Kerne verschmilzt. Ein Kernkraftwerk gewinnt Energie, indem es schwere Kerne spaltet. Wie kann beides Energie liefern?", "The Sun gains energy by fusing light nuclei. A nuclear power station gains energy by splitting heavy nuclei. How can both release energy?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Am festesten gebunden sind Kerne mittlerer Größe wie Eisen. Wer zu ihnen hin umbaut, ob durch Verschmelzen oder Spalten, setzt Energie frei.", "Medium-sized nuclei such as iron are the most tightly bound. Rebuilding towards them, whether by fusing or splitting, releases energy."),
                    listOf(l("Fusion und Spaltung sind eigentlich derselbe Vorgang.", "Fusion and fission are really the same process."), l("Die Spaltung nimmt in Wahrheit Energie auf, die Kraftwerke speichern sie nur.", "Fission actually takes in energy; power stations just store it."), l("Leichte und schwere Kerne enthalten verschiedene Arten von Energie.", "Light and heavy nuclei contain different kinds of energy.")),
                    hints = listOf(l("Kernidee: Entscheidend ist die Bindungsenergie pro Nukleon. Sie steigt von Wasserstoff bis Eisen und fällt danach wieder.", "Key idea: what matters is the binding energy per nucleon. It rises from hydrogen up to iron and falls again after that."), l("Energie wird frei, wenn die Nukleonen danach fester gebunden sind als vorher.", "Energy is released when the nucleons are more tightly bound afterwards than before.")))),
                solution = listOf(l("Die Bindungsenergie pro Nukleon hat bei Eisen-56 ein Maximum. Leichte Kerne gewinnen durch Verschmelzen, schwere durch Spalten an Bindung, in beiden Fällen wird die Differenz frei.", "The binding energy per nucleon peaks at iron-56. Light nuclei gain binding by fusing, heavy ones by splitting; in both cases the difference is released.")),
                background = l("In kleinen Kernen sitzen viele Nukleonen am Rand und haben wenige Nachbarn, die starke Kernkraft kann sich nicht voll entfalten. In großen Kernen stoßen sich die vielen Protonen elektrisch ab. Dazwischen liegt bei Eisen das Optimum. Sterne fusionieren deshalb höchstens bis Eisen. Alle schwereren Elemente entstehen bei Supernovae und Neutronensternkollisionen.", "In small nuclei many nucleons sit at the edge and have few neighbours, so the strong nuclear force can't act fully. In large nuclei the many protons repel each other electrically. In between, at iron, lies the optimum. That's why stars fuse at most up to iron. All heavier elements are made in supernovae and neutron star collisions."),
                fact = l("Das Gold in deinem Schmuck stammt sehr wahrscheinlich aus der Kollision zweier Neutronensterne, lange bevor es die Sonne gab.", "The gold in your jewellery very probably comes from the collision of two neutron stars, long before the Sun existed."),
            )
        },
        lab("phy.sonnenvorrat", Topic.Nuclear, X, 0.7f) {
            val ms = 1.989e30; val frac = pick(0.1, 0.12); val eff = 0.007; val l0 = 3.83e26
            val t = frac * ms * eff * C6 * C6 / l0 / (3.156e7 * 1e9)
            task(
                question = l("Im Kern der Sonne werden etwa ${n(frac * 100, 0)} % ihrer Masse (Sonne: 2,0 · 10³⁰ kg) zu Helium verschmolzen. Dabei werden 0,7 % der Masse zu Energie. Die Sonne strahlt 3,83 · 10²⁶ W. Wie lange reicht der Vorrat?", "In the Sun's core about ${n(frac * 100, 0)}% of its mass (Sun: 2.0 · 10³⁰ kg) is fused into helium, with 0.7% of the mass turned into energy. The Sun radiates 3.83 · 10²⁶ W. How long does the supply last?"),
                steps = listOf(num(l("Zeit in Milliarden Jahren", "Time in billions of years"), t, l("Mrd. a", "billion yr"), rel = 0.04, done = """§t \approx ${m(t, 1)} \cdot 10^9\ \text{a}§""",
                    hints = listOf(l("Kernidee: Erst die gesamte verfügbare Energie mit E = mc², dann durch die Leistung teilen.", "Key idea: first the total available energy with E = mc², then divide by the power."), """§t = \dfrac{f \cdot M \cdot 0{,}007 \cdot c^2}{L}§"""))),
                solution = listOf("""§E = ${m(frac, 2)} \cdot 2 \cdot 10^{30} \cdot 0{,}007 \cdot (3 \cdot 10^8)^2\ \text{J} \approx ${sci(frac * ms * eff * C6 * C6)}\ \text{J}§""", """§t = \dfrac{E}{3{,}83 \cdot 10^{26}\ \text{W}} \approx ${m(t, 1)} \cdot 10^9\ \text{Jahre}§""".let { l(it, it.replace("Jahre", "years")) }),
                background = l("Mit chemischer Verbrennung würde die Sonne nur ein paar tausend Jahre leuchten. Erst die Kernfusion, entdeckt um 1938, erklärt ihr hohes Alter. Die Sonne ist heute etwa in der Mitte ihres Lebens. Wenn der Wasserstoff im Kern verbraucht ist, bläht sie sich zum Roten Riesen auf.", "Burning chemically, the Sun would shine for only a few thousand years. Only nuclear fusion, discovered around 1938, explains its great age. The Sun is now about halfway through its life. When the hydrogen in its core is used up, it will swell into a red giant."),
                fact = l("Ein Lichtteilchen, das heute im Sonnenkern entsteht, braucht durch Zehntausende Jahre Zickzack, bis es die Oberfläche erreicht, und dann nur noch 8 Minuten zu uns.", "A particle of light created in the Sun's core today zigzags for tens of thousands of years before it reaches the surface, and then needs just 8 minutes to reach us."),
            )
        },
        lab("phy.zerfallsreihe", Topic.Nuclear, X, 0.6f) {
            val k = int(0, 2)
            val start = listOf(l("Uran-238", "Uranium-238"), l("Thorium-232", "Thorium-232"), l("Uran-235", "Uranium-235"))[k]
            val end = listOf(l("Blei-206", "lead-206"), l("Blei-208", "lead-208"), l("Blei-207", "lead-207"))[k]
            val a0 = listOf(238, 232, 235)[k]; val z0 = listOf(92, 90, 92)[k]; val a1 = listOf(206, 208, 207)[k]; val z1 = 82
            val nA = (a0 - a1) / 4; val nB = 2 * nA - (z0 - z1)
            task(
                question = l("$start (Ordnungszahl $z0) zerfällt über viele Stufen bis zu stabilem $end (Ordnungszahl $z1). Dabei gibt es nur Alpha- und Betazerfälle.", "$start (atomic number $z0) decays through many stages down to stable $end (atomic number $z1). Only alpha and beta decays occur along the way."),
                steps = listOf(
                    num(l("Wie viele Alphazerfälle?", "How many alpha decays?"), nA.toDouble(), "", abs = 0.0, done = """§${nA}§""",
                        hints = listOf(l("Kernidee: Nur Alphazerfälle ändern die Massenzahl, jeder um 4.", "Key idea: only alpha decays change the mass number, each by 4."), """§\dfrac{${a0} - ${a1}}{4}§""")),
                    num(l("Wie viele Betazerfälle?", "How many beta decays?"), nB.toDouble(), "", abs = 0.0, done = """§${nB}§""",
                        hints = listOf(l("Jeder Alphazerfall senkt die Ordnungszahl um 2, jeder Betazerfall hebt sie um 1.", "Each alpha decay lowers the atomic number by 2, each beta decay raises it by 1."), """§${z0} - 2 \cdot ${nA} + n_\beta = ${z1}§""")),
                ),
                solution = listOf("""§n_\alpha = \dfrac{${a0} - ${a1}}{4} = ${nA}§""", """§${z0} - ${2 * nA} + n_\beta = ${z1} \Rightarrow n_\beta = ${nB}§"""),
                background = l("Schwere Kerne werden nicht in einem Schritt stabil, sondern hangeln sich über eine Kette von Zerfällen nach unten. Alphazerfälle bauen Masse ab, machen den Kern aber neutronenreich, Betazerfälle wandeln dann Neutronen in Protonen um. Unterwegs entstehen Elemente wie Radium und Radon, die es nur gibt, weil diese Ketten ständig nachliefern.", "Heavy nuclei don't become stable in one step but work their way down through a chain of decays. Alpha decays shed mass but leave the nucleus rich in neutrons, and beta decays then turn neutrons into protons. Along the way elements such as radium and radon are formed, which only exist because these chains keep producing them."),
                fact = l("Aus dem Verhältnis von Uran zu Blei in Gesteinen bestimmt man das Alter der Erde: 4,54 Milliarden Jahre.", "The age of the Earth is determined from the ratio of uranium to lead in rocks: 4.54 billion years."),
            )
        },
        lab("phy.oklo", Topic.Nuclear, X, 0.7f) {
            val tGy = pick(2.0, 1.7, 3.0); val r0 = 0.0072 / 0.9928
            val r = r0 * 2.0.pow(tGy / 0.704) / 2.0.pow(tGy / 4.468); val frac = r / (1 + r) * 100
            task(
                question = l("Heute besteht natürliches Uran zu 0,72 % aus U-235 (Halbwertszeit 0,704 Mrd. Jahre), der Rest ist U-238 (4,47 Mrd. Jahre). Wie groß war der Anteil von U-235 vor ${n(tGy, 1)} Milliarden Jahren?", "Today natural uranium is 0.72% U-235 (half-life 0.704 billion years); the rest is U-238 (4.47 billion years). What was the share of U-235 ${n(tGy, 1)} billion years ago?"),
                steps = listOf(num(l("Anteil U-235 in Prozent", "Share of U-235 in percent"), frac, "%", rel = 0.03, done = """§\approx ${m(frac, 1)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Rechne rückwärts. Damals gab es von jeder Sorte mehr, und zwar um den Faktor 2^(t/T½). Bei U-235 viel mehr als bei U-238.", "Key idea: work backwards. Back then there was more of each kind, by a factor of 2^(t/T½). For U-235 much more than for U-238."), """§r_{damals} = \dfrac{0{,}72}{99{,}28} \cdot \dfrac{2^{t/0{,}704}}{2^{t/4{,}47}}§""".let { l(it, it.replace("damals", "then")) }))),
                solution = listOf("""§\dfrac{N_{235}}{N_{238}} = ${m(r0, 5)} \cdot \dfrac{2^{${m(tGy / 0.704, 2)}}}{2^{${m(tGy / 4.468, 2)}}} \approx ${m(r, 4)}§""", """§\text{Anteil} = \dfrac{${m(r, 4)}}{1 + ${m(r, 4)}} \approx ${m(frac, 1)}\ \text{%}§""".let { l(it, it.replace("Anteil", "share")) }),
                background = l("U-235 zerfällt viel schneller als U-238, deshalb war es früher viel häufiger. Vor zwei Milliarden Jahren lag sein Anteil etwa so hoch wie im angereicherten Brennstoff heutiger Reaktoren. Genau das machte 1972 eine erstaunliche Entdeckung möglich.", "U-235 decays much faster than U-238, so it used to be much more common. Two billion years ago its share was about as high as in the enriched fuel of today's reactors. Exactly that made an astonishing discovery possible in 1972."),
                fact = l("In Oklo in Gabun fand man Uranerz, das vor rund 2 Milliarden Jahren von selbst als Kernreaktor lief, mit Grundwasser als Moderator, über Hunderttausende Jahre.", "At Oklo in Gabon uranium ore was found that ran by itself as a nuclear reactor about 2 billion years ago, with groundwater as the moderator, for hundreds of thousands of years."),
            )
        },
    )
}
