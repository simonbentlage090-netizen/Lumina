package de.funke.app.ui.challenge

import de.funke.app.ui.tr
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

// =============================================================================================
// Aufgabenbank Physik & Mathe (selbst geschrieben, seit 9.8).
// Ergebnisse werden im Code ausgerechnet, nicht abgetippt, damit keine Lösung falsch hinterlegt ist.
// In Texten steht § für $ (Formel). So bleiben Kotlin-Rohstrings frei von Vorlagen.
// Bei Auswahlfragen ist immer die ERSTE Option richtig; die Anzeige mischt die Reihenfolge.
// =============================================================================================

/** Text in der gewählten Sprache; § wird zu $ (Formelgrenze). */
internal fun l(de: String, en: String): String = tr(de, en).replace('§', '$')

internal fun num(
    prompt: String, value: Double, unit: String = "", rel: Double = 0.02, abs: Double? = null,
    done: String, hints: List<String>, formula: FormulaCard? = null, wrong: String? = null,
) = Step(prompt, Answer.Number(value, unit, rel, abs), done, hints, formula, wrong)

/**
 * Zahl-Schritt für sehr große oder sehr kleine Werte. Das Eingabefeld fasst höchstens 10 Zeichen, deshalb fragt
 * der Schritt ab 10⁸ und unter 10⁻⁴ nach dem Vorfaktor vor einer festen Zehnerpotenz: Die Einheit lautet dann
 * etwa "· 10²⁴ kg". Dazwischen ist er ein gewöhnlicher [num].
 */
internal fun big(
    prompt: String, value: Double, unit: String = "", rel: Double = 0.02,
    done: String, hints: List<String>, formula: FormulaCard? = null,
): Step {
    val e = kotlin.math.floor(kotlin.math.log10(kotlin.math.abs(value))).toInt()
    if (e in -4..7) return num(prompt, value, unit, rel, done = done, hints = hints, formula = formula)
    val p = "10" + superscript(e)
    return num(
        "$prompt " + tr("(Vorfaktor vor $p)", "(coefficient of $p)"), value / 10.0.pow(e),
        if (unit.isEmpty()) "· $p" else "· $p $unit", rel, done = done, hints = hints, formula = formula,
    )
}

private fun superscript(e: Int): String = e.toString().map { if (it == '-') '⁻' else "⁰¹²³⁴⁵⁶⁷⁸⁹"[it - '0'] }.joinToString("")

internal fun choice(
    prompt: String, options: List<String>, done: String, hints: List<String>,
    formula: FormulaCard? = null, wrong: String? = null,
) = Step(prompt, Answer.Choice(options, 0), done, hints, formula, wrong)

internal fun estimate(
    prompt: String, value: Double, min: Double, max: Double, kind: EstimateKind,
    done: String, hints: List<String>, factor: Double = 2.0, formula: FormulaCard? = null,
) = Step(prompt, Answer.Estimate(value, min, max, kind, factor), done, hints, formula)

internal fun card(title: String, formula: String, text: String) = FormulaCard(title, formula, text)

private const val G = 9.81
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

object TaskBank {
    val all: List<TaskDef> = listOf(
        // ======================================== LEICHT ========================================
        TaskDef("luft", Topic.Matter, E) {
            val m = 1.2 * 4.0 * 5.0 * 2.5
            Task(
                "luft", Topic.Matter, E,
                question = l("Dein Zimmer ist 4 m lang, 5 m breit und 2,5 m hoch. Wie schwer ist die Luft darin?", "Your room is 4 m long, 5 m wide and 2.5 m high. How heavy is the air inside?"),
                given = listOf(l("""Dichte von Luft: §\rho_L = 1{,}2\ \text{kg/m}^3§""", """Density of air: §\rho_L = 1.2\ \text{kg/m}^3§""")),
                steps = listOf(
                    num(
                        l("Wie viel Kilogramm Luft sind im Zimmer?", "How many kilograms of air are in the room?"), m, "kg", rel = 0.03,
                        done = """§m = 60\ \text{kg}§""",
                        hints = listOf(l("Kernidee: Masse = Dichte mal Volumen.", "Key idea: mass = density times volume."), l("""§V = 4 \cdot 5 \cdot 2{,}5\ \text{m}^3 = 50\ \text{m}^3§""", """§V = 4 \cdot 5 \cdot 2.5\ \text{m}^3 = 50\ \text{m}^3§""")),
                        formula = card(l("Dichte", "Density"), """§\rho = \dfrac{m}{V}§""", l("Wie viel Masse in einem Kubikmeter steckt.", "How much mass there is in one cubic metre.")),
                    ),
                ),
                solution = listOf(l("""§V = 4\ \text{m} \cdot 5\ \text{m} \cdot 2{,}5\ \text{m} = 50\ \text{m}^3§""", """§V = 4\ \text{m} \cdot 5\ \text{m} \cdot 2.5\ \text{m} = 50\ \text{m}^3§"""), l("""§m = \rho_L V = 1{,}2\ \tfrac{\text{kg}}{\text{m}^3} \cdot 50\ \text{m}^3 = 60\ \text{kg}§""", """§m = \rho_L V = 1.2\ \tfrac{\text{kg}}{\text{m}^3} \cdot 50\ \text{m}^3 = 60\ \text{kg}§""")),
                background = l(
                    "Luft ist ein Gas aus Molekülen, vor allem Stickstoff und Sauerstoff. Sie ist rund 800-mal weniger dicht als Wasser, aber eben nicht nichts: Jeder Kubikmeter wiegt gut ein Kilogramm. Weil wir von Luft umgeben sind und sie von allen Seiten gleich drückt, spüren wir ihr Gewicht nicht.",
                    "Air is a gas made of molecules, mostly nitrogen and oxygen. It is about 800 times less dense than water, but it is not nothing: every cubic metre weighs a bit more than a kilogram. Because air surrounds us and pushes equally from all sides, we don't feel its weight.",
                ),
                fact = l("Über jedem Quadratmeter Boden liegen rund 10 Tonnen Luft. Das ist der Luftdruck.", "Above every square metre of ground there are about 10 tonnes of air. That is air pressure."),
            )
        },
        TaskDef("wippe", Topic.Rotation, E) {
            val d = 300.0 * 1.0 / 60.0
            Task(
                "wippe", Topic.Rotation, E,
                question = l("Auf einer Wippe sitzt ein Elefantenbaby (300 kg) 1,0 m vom Drehpunkt entfernt. Wo muss sich dein Freund (60 kg) auf die andere Seite setzen, damit die Wippe im Gleichgewicht ist?", "A baby elephant (300 kg) sits on a seesaw 1.0 m from the pivot. Where must your friend (60 kg) sit on the other side to balance it?"),
                steps = listOf(
                    num(
                        l("Abstand vom Drehpunkt", "Distance from the pivot"), d, "m", rel = 0.02,
                        done = l("""§5{,}0\ \text{m}§ vom Drehpunkt""", """§5.0\ \text{m}§ from the pivot"""),
                        hints = listOf(l("Kernidee: Gleichgewicht heißt: beide Seiten drehen gleich stark.", "Key idea: balance means both sides turn equally hard."), l("""§300\ \text{kg} \cdot 1{,}0\ \text{m} = 60\ \text{kg} \cdot a§""", """§300\ \text{kg} \cdot 1.0\ \text{m} = 60\ \text{kg} \cdot a§""")),
                        formula = card(l("Hebelgesetz", "Law of the lever"), """§F_1 \cdot a_1 = F_2 \cdot a_2§""", l("Kraft mal Hebelarm ist auf beiden Seiten gleich.", "Force times lever arm is the same on both sides.")),
                    ),
                ),
                solution = listOf(l("""§m_1 g\, a_1 = m_2 g\, a_2 \;\Rightarrow\; a_2 = \dfrac{m_1}{m_2}\, a_1 = \dfrac{300}{60} \cdot 1{,}0\ \text{m} = 5{,}0\ \text{m}§""", """§m_1 g\, a_1 = m_2 g\, a_2 \;\Rightarrow\; a_2 = \dfrac{m_1}{m_2}\, a_1 = \dfrac{300}{60} \cdot 1.0\ \text{m} = 5.0\ \text{m}§""")),
                background = l(
                    "Ein Hebel tauscht Kraft gegen Weg: Wer weit außen sitzt, braucht wenig Kraft, muss sich aber weit bewegen. Deshalb kannst du mit einem langen Schraubenschlüssel eine festsitzende Schraube lösen und mit einer Brechstange schwere Steine heben.",
                    "A lever trades force for distance: whoever sits far out needs little force but has to move a long way. That's why a long wrench loosens a stuck bolt and a crowbar lifts heavy stones.",
                ),
                fact = l("Archimedes soll gesagt haben: Gebt mir einen festen Punkt, und ich hebe die Welt aus den Angeln.", "Archimedes is said to have claimed: give me a place to stand, and I will move the Earth."),
            )
        },
        TaskDef("gewitter", Topic.Waves, E) {
            val s = 343.0 * 6.0 / 1000.0
            Task(
                "gewitter", Topic.Waves, E,
                question = l("Du siehst einen Blitz und zählst 6 Sekunden bis zum Donner. Wie weit ist das Gewitter entfernt?", "You see a flash and count 6 seconds until the thunder. How far away is the storm?"),
                given = listOf(l("Schallgeschwindigkeit in Luft: 343 m/s", "Speed of sound in air: 343 m/s")),
                steps = listOf(
                    num(
                        l("Entfernung", "Distance"), s, "km", rel = 0.05, done = """§\approx 2{,}1\ \text{km}§""",
                        hints = listOf(l("Kernidee: Das Licht ist praktisch sofort da, nur der Schall braucht Zeit.", "Key idea: the light arrives almost instantly, only the sound takes time."), l("""§s = v \cdot t = 343\ \tfrac{\text{m}}{\text{s}} \cdot 6\ \text{s}§""", """§s = v \cdot t = 343\ \tfrac{\text{m}}{\text{s}} \cdot 6\ \text{s}§""")),
                        formula = card(l("Gleichförmige Bewegung", "Uniform motion"), """§s = v \cdot t§""", l("Weg gleich Geschwindigkeit mal Zeit.", "Distance equals speed times time.")),
                    ),
                ),
                solution = listOf(l("""§s = 343\ \tfrac{\text{m}}{\text{s}} \cdot 6\ \text{s} = 2058\ \text{m} \approx 2{,}1\ \text{km}§""", """§s = 343\ \tfrac{\text{m}}{\text{s}} \cdot 6\ \text{s} = 2058\ \text{m} \approx 2.1\ \text{km}§""")),
                background = l(
                    "Blitz und Donner entstehen gleichzeitig: Der Blitz erhitzt die Luft schlagartig auf etwa 30.000 °C, sie dehnt sich explosionsartig aus, und das hören wir als Donner. Licht ist fast eine Million Mal schneller als Schall, deshalb sehen wir den Blitz zuerst.",
                    "Lightning and thunder happen at the same time: the lightning heats the air to about 30,000 °C in an instant, the air expands explosively and we hear that as thunder. Light is almost a million times faster than sound, so we see the flash first.",
                ),
                fact = l("Faustregel: Sekunden durch 3 ergibt ungefähr die Entfernung in Kilometern.", "Rule of thumb: seconds divided by 3 gives roughly the distance in kilometres."),
            )
        },
        TaskDef("pizza", Topic.Equations, E) {
            Task(
                "pizza", Topic.Equations, E,
                question = l("Drei Pizzen zu je 8 € plus eine Liefergebühr kosten zusammen 29 €. Welche Zahl gehört in die Lücke?", "Three pizzas at €8 each plus a delivery fee cost €29 in total. Which number goes in the gap?"),
                steps = listOf(
                    num(
                        """§3 \cdot 8 + \square = 29§""", 5.0, "€", abs = 0.0, done = """§\square = 5§""",
                        hints = listOf(l("Rechne zuerst aus, was die Pizzen allein kosten.", "First work out what the pizzas alone cost."), l("""§24 + \square = 29§""", """§24 + \square = 29§""")),
                    ),
                ),
                solution = listOf("""§3 \cdot 8 = 24 \;\Rightarrow\; 24 + \square = 29 \;\Rightarrow\; \square = 29 - 24 = 5§"""),
                background = l(
                    "Eine Gleichung ist wie eine Waage: Was du auf einer Seite abziehst, musst du auch auf der anderen abziehen. Die Lücke ist einfach eine Unbekannte, meistens schreibt man dafür x.",
                    "An equation is like a scale: whatever you take off one side, you must take off the other. The gap is just an unknown, usually written as x.",
                ),
            )
        },

        // ======================================== MITTEL ========================================
        TaskDef("flummi", Topic.Energy, M) {
            val v = sqrt(2 * G * 2.0)
            val v2 = sqrt(0.8) * v
            val n = kotlin.math.ceil(ln(0.05) / ln(0.8))
            Task(
                "flummi", Topic.Energy, M,
                question = l("""Ein Flummi fällt aus §h = 2{,}0\ \text{m}§ auf den Boden. Bei jedem Aufprall wird ein Fünftel seiner Bewegungsenergie zu Wärme.""", """A bouncy ball falls from §h = 2.0\ \text{m}§ onto the floor. With every bounce, a fifth of its kinetic energy turns into heat."""),
                given = listOf(l("""§g = 9{,}81\ \text{m/s}^2§""", """§g = 9.81\ \text{m/s}^2§""")),
                steps = listOf(
                    num(
                        l("Mit welcher Geschwindigkeit trifft er auf?", "How fast does it hit the floor?"), v, "m/s",
                        done = l("""§v \approx 6{,}26\ \text{m/s}§""", """§v \approx 6.26\ \text{m/s}§"""),
                        hints = listOf(l("Kernidee: Die Lageenergie oben wird zu Bewegungsenergie unten.", "Key idea: potential energy at the top turns into kinetic energy at the bottom."), """§mgh = \tfrac{1}{2} m v^2 \;\Rightarrow\; v = \sqrt{2gh}§"""),
                        formula = card(l("Energieerhaltung", "Energy conservation"), """§E_\text{pot} = mgh,\quad E_\text{kin} = \tfrac{1}{2} m v^2§""", l("Ohne Reibung bleibt die Summe gleich.", "Without friction the sum stays the same.")),
                    ),
                    num(
                        l("Mit welcher Geschwindigkeit springt er wieder ab?", "How fast does it leave the floor again?"), v2, "m/s",
                        done = l("""§v' \approx 5{,}60\ \text{m/s}§""", """§v' \approx 5.60\ \text{m/s}§"""),
                        hints = listOf(l("""80 % der Energie bleiben. Aber die Energie wächst mit §v^2§, nicht mit §v§.""", """80 % of the energy remains. But energy grows with §v^2§, not with §v§."""), l("""§v' = \sqrt{0{,}8}\; v§""", """§v' = \sqrt{0.8}\; v§""")),
                    ),
                    num(
                        l("Nach dem wievielten Aufprall springt er zum ersten Mal weniger als 10 cm hoch?", "After which bounce does it first rise less than 10 cm?"), n, "", abs = 0.0,
                        done = """§n = 14§""",
                        hints = listOf(l("""Die Höhe ist proportional zur Energie: §h_n = 2{,}0\ \text{m} \cdot 0{,}8^{n}§""", """Height is proportional to energy: §h_n = 2.0\ \text{m} \cdot 0.8^{n}§"""), l("""Löse §0{,}8^{n} < 0{,}05§ mit dem Logarithmus.""", """Solve §0.8^{n} < 0.05§ with a logarithm.""")),
                        formula = card(l("Logarithmus", "Logarithm"), """§a^n = b \;\Leftrightarrow\; n = \dfrac{\ln b}{\ln a}§""", l("Damit holt man eine Zahl aus dem Exponenten.", "This gets a number out of the exponent.")),
                    ),
                ),
                solution = listOf(
                    l("""§v = \sqrt{2gh} = \sqrt{2 \cdot 9{,}81 \cdot 2{,}0}\ \text{m/s} \approx 6{,}26\ \text{m/s}§""", """§v = \sqrt{2gh} = \sqrt{2 \cdot 9.81 \cdot 2.0}\ \text{m/s} \approx 6.26\ \text{m/s}§"""),
                    l("""§\tfrac{1}{2} m v'^2 = 0{,}8 \cdot \tfrac{1}{2} m v^2 \;\Rightarrow\; v' = \sqrt{0{,}8}\, v \approx 5{,}60\ \text{m/s}§""", """§\tfrac{1}{2} m v'^2 = 0.8 \cdot \tfrac{1}{2} m v^2 \;\Rightarrow\; v' = \sqrt{0.8}\, v \approx 5.60\ \text{m/s}§"""),
                    l("""§h_n = 2{,}0\ \text{m} \cdot 0{,}8^{n} < 0{,}1\ \text{m} \;\Rightarrow\; n > \dfrac{\ln 0{,}05}{\ln 0{,}8} \approx 13{,}4 \;\Rightarrow\; n = 14§""", """§h_n = 2.0\ \text{m} \cdot 0.8^{n} < 0.1\ \text{m} \;\Rightarrow\; n > \dfrac{\ln 0.05}{\ln 0.8} \approx 13.4 \;\Rightarrow\; n = 14§"""),
                ),
                background = l(
                    "Beim Aufprall wird der Flummi kurz platt gedrückt wie eine Feder. Ein Teil der Energie kommt beim Zurückspringen wieder heraus, der Rest wird im Material zu Wärme. Wie viel bleibt, beschreibt die Stoßzahl: das Verhältnis der Geschwindigkeiten nach und vor dem Stoß. Weil die Energie mit dem Quadrat der Geschwindigkeit wächst, bedeuten 80 % der Energie nur knapp 89 % der Geschwindigkeit.",
                    "On impact the ball is squashed briefly, like a spring. Part of the energy comes back out as it rebounds, the rest becomes heat in the material. How much remains is described by the coefficient of restitution: the ratio of speeds after and before the impact. Because energy grows with the square of speed, 80 % of the energy means only about 89 % of the speed.",
                ),
                fact = l("Gute Flummis haben eine Stoßzahl von etwa 0,9, ein Tennisball nur etwa 0,75. Deshalb springt er so viel schlechter.", "Good bouncy balls have a coefficient of about 0.9, a tennis ball only about 0.75. That's why it bounces so much worse."),
            )
        },
        TaskDef("astronautin", Topic.Momentum, M) {
            val vA = 2.0 / 100.0 * 10.0
            Task(
                "astronautin", Topic.Momentum, M,
                question = l("""Eine Astronautin (mit Anzug §100\ \text{kg}§) treibt §20\ \text{m}§ vor ihrer Raumstation, ohne Sicherungsseil und in Ruhe gegenüber der Station. Bei sich hat sie nur einen Schraubenschlüssel (§2{,}0\ \text{kg}§).""", """An astronaut (with suit §100\ \text{kg}§) floats §20\ \text{m}§ from her space station, without a tether and at rest relative to the station. All she has is a wrench (§2.0\ \text{kg}§)."""),
                steps = listOf(
                    choice(
                        l("Wie kommt sie zurück?", "How does she get back?"),
                        listOf(l("Schlüssel von der Station weg werfen", "Throw the wrench away from the station"), l("Schlüssel zur Station hin werfen", "Throw the wrench towards the station"), l("Mit Armen und Beinen rudern", "Paddle with arms and legs")),
                        done = l("Schlüssel weg werfen", "Throw the wrench away"),
                        hints = listOf(l("Kernidee: Ohne äußere Kraft bleibt der Gesamtimpuls gleich, hier also null.", "Key idea: without an outside force, total momentum stays the same, here zero."), l("Was nach hinten fliegt, schiebt dich nach vorn. Rudern braucht etwas, wogegen man drückt.", "Whatever flies backwards pushes you forwards. Paddling needs something to push against.")),
                        formula = card(l("Impulserhaltung", "Conservation of momentum"), """§p = m v,\quad p_\text{vorher} = p_\text{nachher}§""", l("Ohne äußere Kraft ändert sich der Gesamtimpuls nicht.", "Without an outside force, total momentum doesn't change.")),
                    ),
                    num(
                        l("""Sie wirft ihn mit §10\ \text{m/s}§. Wie schnell treibt sie danach?""", """She throws it at §10\ \text{m/s}§. How fast does she drift afterwards?"""), vA, "m/s", rel = 0.03,
                        done = l("""§0{,}20\ \text{m/s}§""", """§0.20\ \text{m/s}§"""),
                        hints = listOf("""§0 = m_A v_A + m_S v_S§""", """§v_A = \dfrac{m_S}{m_A}\, v_S§"""),
                    ),
                    num(
                        l("Wie lange braucht sie bis zur Station?", "How long until she reaches the station?"), 20.0 / vA, "s", rel = 0.03,
                        done = """§100\ \text{s}§""",
                        hints = listOf(l("Ab jetzt bremst sie nichts: Sie bewegt sich gleichförmig.", "From now on nothing slows her down: she moves uniformly."), """§t = s / v§"""),
                    ),
                    num(
                        l("Wie schnell müsste sie werfen, um in 20 s anzukommen?", "How fast would she have to throw to arrive in 20 s?"), 100.0 / 2.0 * 1.0, "m/s", rel = 0.03,
                        done = """§50\ \text{m/s}§""",
                        hints = listOf(l("""Sie selbst braucht dann §1{,}0\ \text{m/s}§.""", """She herself then needs §1.0\ \text{m/s}§."""), """§v_S = \dfrac{m_A}{m_S}\, v_A§"""),
                    ),
                ),
                solution = listOf(
                    """§p_\text{vorher} = 0 = m_A v_A + m_S v_S§""",
                    l("""§v_A = -\dfrac{m_S}{m_A} v_S = -\dfrac{2{,}0}{100} \cdot 10\ \text{m/s} = -0{,}20\ \text{m/s}§""", """§v_A = -\dfrac{m_S}{m_A} v_S = -\dfrac{2.0}{100} \cdot 10\ \text{m/s} = -0.20\ \text{m/s}§"""),
                    l("""§t = \dfrac{20\ \text{m}}{0{,}20\ \text{m/s}} = 100\ \text{s}§""", """§t = \dfrac{20\ \text{m}}{0.20\ \text{m/s}} = 100\ \text{s}§"""),
                    l("""Für §20\ \text{s}§ braucht sie §1{,}0\ \text{m/s}§, also §v_S = \dfrac{100}{2{,}0} \cdot 1{,}0\ \text{m/s} = 50\ \text{m/s}§""", """For §20\ \text{s}§ she needs §1.0\ \text{m/s}§, so §v_S = \dfrac{100}{2.0} \cdot 1.0\ \text{m/s} = 50\ \text{m/s}§"""),
                ),
                background = l(
                    "Der Impuls ist Masse mal Geschwindigkeit. Solange keine äußere Kraft wirkt, bleibt der Gesamtimpuls eines Systems gleich. Vor dem Wurf ist er null, also müssen sich Schraubenschlüssel und Astronautin danach in entgegengesetzte Richtungen bewegen. Der leichte Schlüssel wird schnell, die schwere Astronautin nur langsam. Genau so funktioniert jede Rakete: Sie wirft Gas mit hoher Geschwindigkeit nach hinten.",
                    "Momentum is mass times velocity. As long as no outside force acts, the total momentum of a system stays the same. Before the throw it is zero, so afterwards wrench and astronaut must move in opposite directions. The light wrench becomes fast, the heavy astronaut only slow. Every rocket works exactly like this: it throws gas backwards at high speed.",
                ),
                fact = l("50 m/s wären 180 km/h, schneller als der beste Baseball-Werfer der Welt. Deshalb haben echte Raumanzüge kleine Notfall-Düsen.", "50 m/s is 180 km/h, faster than the best baseball pitcher in the world. That's why real space suits have small emergency thrusters."),
            )
        },
        TaskDef("blitz", Topic.Energy, M) {
            Task(
                "blitz", Topic.Energy, M,
                question = l("""Ein typischer Blitz setzt rund §10^9\ \text{J}§ frei. Wie lange könnte ein Wasserkocher mit §2000\ \text{W}§ mit dieser Energie laufen?""", """A typical lightning bolt releases about §10^9\ \text{J}§. How long could a §2000\ \text{W}§ kettle run on this energy?"""),
                steps = listOf(
                    estimate(
                        l("Schätze die Dauer", "Estimate the duration"), 1e9 / 2000.0, 3600.0, 3.15e7, EstimateKind.Duration,
                        done = l("""§\approx 5{,}8§ Tage""", """§\approx 5.8§ days"""),
                        hints = listOf(l("""Kernidee: Leistung ist Energie pro Zeit, §P = E/t§.""", """Key idea: power is energy per time, §P = E/t§."""), l("""§t = 10^9\ \text{J} : 2000\ \text{W} = 5 \cdot 10^5\ \text{s}§. Wie viele Tage sind das?""", """§t = 10^9\ \text{J} : 2000\ \text{W} = 5 \cdot 10^5\ \text{s}§. How many days is that?""")),
                        formula = card(l("Leistung", "Power"), """§P = \dfrac{E}{t}§""", l("1 Watt ist 1 Joule pro Sekunde.", "1 watt is 1 joule per second.")),
                    ),
                ),
                solution = listOf(
                    l("""§t = \dfrac{E}{P} = \dfrac{10^9\ \text{J}}{2000\ \text{W}} = 5 \cdot 10^5\ \text{s} \approx 5{,}8\ \text{Tage}§""", """§t = \dfrac{E}{P} = \dfrac{10^9\ \text{J}}{2000\ \text{W}} = 5 \cdot 10^5\ \text{s} \approx 5.8\ \text{days}§"""),
                    l("""§10^9\ \text{J} = \dfrac{10^9}{3{,}6 \cdot 10^6}\ \text{kWh} \approx 280\ \text{kWh}§""", """§10^9\ \text{J} = \dfrac{10^9}{3.6 \cdot 10^6}\ \text{kWh} \approx 280\ \text{kWh}§"""),
                ),
                background = l(
                    "Joule ist die Einheit der Energie, Watt die der Leistung, also wie schnell Energie fließt. Auf der Stromrechnung steht die Energie in Kilowattstunden: 1 kWh ist das, was ein 1000-Watt-Gerät in einer Stunde verbraucht, also 3,6 Millionen Joule. Ein Blitz hat zwar enorm viel Leistung, aber nur für Bruchteile einer Sekunde.",
                    "The joule is the unit of energy, the watt the unit of power, meaning how fast energy flows. Your electricity bill shows energy in kilowatt-hours: 1 kWh is what a 1000-watt device uses in one hour, which is 3.6 million joules. A lightning bolt has enormous power, but only for a fraction of a second.",
                ),
                fact = l("280 kWh kosten nur rund 80 €. Blitze einzufangen lohnt sich nicht: Die Energie kommt viel zu schnell und geht größtenteils als Licht, Schall und Wärme verloren.", "280 kWh cost only about €80. Catching lightning isn't worth it: the energy arrives far too fast and is mostly lost as light, sound and heat."),
            )
        },
        TaskDef("looping", Topic.Energy, M) {
            val r = 10.0
            val vTop = sqrt(G * r)
            val h = 2 * r + vTop * vTop / (2 * G)
            Task(
                "looping", Topic.Energy, M,
                question = l("""Ein Achterbahnwagen startet aus dem Stand auf einem Hügel und rollt reibungsfrei durch einen kreisförmigen Looping mit §r = 10\ \text{m}§. Wie hoch muss der Hügel mindestens sein, damit er oben nicht herunterfällt?""", """A roller-coaster car starts at rest on a hill and rolls without friction through a circular loop with §r = 10\ \text{m}§. How high must the hill be at least so the car doesn't fall at the top?"""),
                given = listOf(l("""§g = 9{,}81\ \text{m/s}^2§""", """§g = 9.81\ \text{m/s}^2§""")),
                steps = listOf(
                    choice(
                        l("Was muss im höchsten Punkt des Loopings mindestens gelten?", "What must at least be true at the top of the loop?"),
                        listOf(l("""Die Gewichtskraft reicht gerade als Zentripetalkraft: §mg = \dfrac{mv^2}{r}§""", """Gravity alone is just enough as centripetal force: §mg = \dfrac{mv^2}{r}§"""), l("Die Geschwindigkeit ist dort null", "The speed there is zero"), l("""Die Schiene drückt mit §mg§ nach oben""", """The track pushes up with §mg§""")),
                        done = """§mg = \dfrac{m v^2}{r}§""",
                        hints = listOf(l("Kernidee: Oben muss die Kreisbewegung allein von der Schwerkraft getragen werden können.", "Key idea: at the top, gravity alone must be able to keep it on the circle."), l("Mit der Geschwindigkeit null würde der Wagen einfach herunterfallen.", "With zero speed the car would simply fall.")),
                        formula = card(l("Zentripetalkraft", "Centripetal force"), """§F_Z = \dfrac{m v^2}{r}§""", l("Diese Kraft braucht jede Kreisbewegung.", "Every circular motion needs this force.")),
                    ),
                    num(
                        l("Wie schnell muss der Wagen oben mindestens sein?", "How fast must the car be at the top, at least?"), vTop, "m/s",
                        done = l("""§v \approx 9{,}9\ \text{m/s}§""", """§v \approx 9.9\ \text{m/s}§"""),
                        hints = listOf(l("""Die Masse kürzt sich heraus.""", """The mass cancels out."""), """§v = \sqrt{g r}§"""),
                    ),
                    num(
                        l("Wie hoch muss der Hügel mindestens sein?", "How high must the hill be, at least?"), h, "m",
                        done = """§h = 25\ \text{m}§""",
                        hints = listOf(l("""Oben ist der Wagen §2r§ hoch und hat noch Bewegungsenergie.""", """At the top the car is §2r§ high and still has kinetic energy."""), """§mgh = mg \cdot 2r + \tfrac{1}{2} m v^2§"""),
                    ),
                ),
                solution = listOf(
                    """§mg = \dfrac{m v^2}{r} \;\Rightarrow\; v = \sqrt{g r} \approx 9{,}9\ \text{m/s}§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("9{,}9", "9.9") },
                    """§mgh = mg \cdot 2r + \tfrac{1}{2} m g r \;\Rightarrow\; h = \tfrac{5}{2}\, r = 25\ \text{m}§""".replace('§', '$'),
                ),
                background = l(
                    "Im Looping drückt der Wagen nach außen gegen die Schiene, weil er eigentlich geradeaus weiter will. Oben zeigt die Schwerkraft genau zur Kreismitte und hilft beim Kurvenfahren. Ist der Wagen zu langsam, reicht schon die Schwerkraft allein für einen engeren Kreis, und er löst sich von der Schiene. Erstaunlich: Die Mindesthöhe ist immer das Zweieinhalbfache des Radius, egal wie schwer der Wagen ist.",
                    "In a loop the car presses outwards against the track because it actually wants to go straight on. At the top, gravity points exactly to the centre and helps it around the curve. If the car is too slow, gravity alone pulls it into a tighter circle and it leaves the track. Surprisingly, the minimum height is always two and a half times the radius, no matter how heavy the car is.",
                ),
                fact = l("Echte Loopings sind nicht rund, sondern tropfenförmig. So sind die Kräfte unten, wo der Wagen am schnellsten ist, nicht zu groß.", "Real loops aren't round but teardrop-shaped. That way the forces at the bottom, where the car is fastest, don't get too big."),
            )
        },
        TaskDef("hochwurf", Topic.Equations, M) {
            Task(
                "hochwurf", Topic.Equations, M,
                question = l("""Ein Ball wird senkrecht hochgeworfen. Seine Höhe ist §h(t) = 20\,t - 5\,t^2§ (h in Metern, t in Sekunden).""", """A ball is thrown straight up. Its height is §h(t) = 20\,t - 5\,t^2§ (h in metres, t in seconds)."""),
                steps = listOf(
                    num(
                        l("Nach wie vielen Sekunden landet er wieder?", "After how many seconds does it land again?"), 4.0, "s", rel = 0.01,
                        done = """§t = 4\ \text{s}§""",
                        hints = listOf(l("""Landen heißt: §h(t) = 0§.""", """Landing means §h(t) = 0§."""), l("""Klammere aus: §t\,(20 - 5t) = 0§""", """Factor out: §t\,(20 - 5t) = 0§""")),
                    ),
                    num(
                        l("Wie hoch fliegt er höchstens?", "How high does it get at most?"), 20.0, "m", rel = 0.01,
                        done = """§h_\text{max} = 20\ \text{m}§""",
                        hints = listOf(l("Der höchste Punkt liegt genau in der Mitte der Flugzeit.", "The highest point is exactly in the middle of the flight time."), """§h(2) = 40 - 20§"""),
                    ),
                    num(
                        l("Wann ist er zum ersten Mal 15 m hoch?", "When is it 15 m high for the first time?"), 1.0, "s", rel = 0.01,
                        done = """§t = 1\ \text{s}§""",
                        hints = listOf(l("""Löse §20t - 5t^2 = 15§, also §t^2 - 4t + 3 = 0§.""", """Solve §20t - 5t^2 = 15§, i.e. §t^2 - 4t + 3 = 0§."""), l("""§pq§-Formel oder Raten: §(t-1)(t-3) = 0§""", """Quadratic formula or guessing: §(t-1)(t-3) = 0§""")),
                        formula = card(l("pq-Formel", "Quadratic formula"), """§t = -\dfrac{p}{2} \pm \sqrt{\left(\dfrac{p}{2}\right)^2 - q}§""", l("""Für §t^2 + p\,t + q = 0§.""", """For §t^2 + p\,t + q = 0§.""")),
                    ),
                ),
                solution = listOf(
                    """§20t - 5t^2 = 0 \;\Rightarrow\; t\,(20 - 5t) = 0 \;\Rightarrow\; t = 4\ \text{s}§""".replace('§', '$'),
                    """§h(2) = 40 - 20 = 20\ \text{m}§""".replace('§', '$'),
                    """§t^2 - 4t + 3 = 0 \;\Rightarrow\; (t-1)(t-3) = 0 \;\Rightarrow\; t = 1\ \text{s}§""".replace('§', '$'),
                ),
                background = l(
                    "Ohne Luftwiderstand ist die Flugbahn eine Parabel: Der Ball wird beim Steigen genauso schnell langsamer, wie er beim Fallen schneller wird. Deshalb ist er bei 15 m zweimal: einmal auf dem Weg nach oben (nach 1 s) und einmal auf dem Weg nach unten (nach 3 s). Die Zahl 5 in der Formel ist die Hälfte der Fallbeschleunigung von rund 10 m/s².",
                    "Without air resistance the path is a parabola: the ball slows down on the way up exactly as fast as it speeds up on the way down. That's why it is at 15 m twice: once on the way up (after 1 s) and once on the way down (after 3 s). The 5 in the formula is half the gravitational acceleration of about 10 m/s².",
                ),
                fact = l("Hoch- und Runterflug dauern exakt gleich lang. Mit Luftwiderstand ist der Rückweg länger.", "The way up and the way down take exactly as long. With air resistance, the way down takes longer."),
            )
        },

        // ======================================== SCHWER ========================================
        TaskDef("iss", Topic.Gravity, H) {
            val r = 6.791e6
            val v = sqrt(3.99e14 / r)
            val tMin = 2 * PI * r / v / 60.0
            Task(
                "iss", Topic.Gravity, H,
                question = l("""Die ISS kreist in §h = 420\ \text{km}§ Höhe um die Erde. Wie oft sehen die Astronauten an einem Tag die Sonne aufgehen?""", """The ISS orbits the Earth at §h = 420\ \text{km}§. How many sunrises do the astronauts see in one day?"""),
                given = listOf("""§GM_E = 3{,}99 \cdot 10^{14}\ \text{m}^3/\text{s}^2,\quad R_E = 6371\ \text{km}§""".let { l(it, it.replace("3{,}99", "3.99")) }),
                steps = listOf(
                    choice(
                        l("Welche Gleichung beschreibt die Kreisbahn?", "Which equation describes the circular orbit?"),
                        listOf("""§\dfrac{GMm}{r^2} = \dfrac{m v^2}{r}§""".replace('§', '$'), """§\dfrac{GMm}{r} = \tfrac{1}{2} m v^2§""".replace('§', '$'), """§\dfrac{GMm}{r^2} = m g§""".replace('§', '$')),
                        done = l("Gravitation = Zentripetalkraft", "Gravity = centripetal force"),
                        hints = listOf(l("Kernidee: Die Gravitation ist die einzige Kraft, und sie hält die ISS genau auf dem Kreis.", "Key idea: gravity is the only force, and it keeps the ISS exactly on the circle."), l("""Zentripetalkraft: §F_Z = m v^2 / r§""", """Centripetal force: §F_Z = m v^2 / r§""")),
                        formula = card(l("Gravitationsgesetz", "Law of gravitation"), """§F = \dfrac{G M m}{r^2}§""", l("""§r§ ist der Abstand zum Erdmittelpunkt.""", """§r§ is the distance to the Earth's centre.""")),
                    ),
                    num(
                        l("Wie schnell fliegt die ISS?", "How fast does the ISS fly?"), v / 1000.0, "km/s", rel = 0.01,
                        done = l("""§v \approx 7{,}67\ \text{km/s}§""", """§v \approx 7.67\ \text{km/s}§"""),
                        hints = listOf(l("""Achtung: §r = R_E + h§, nicht nur §h§.""", """Careful: §r = R_E + h§, not just §h§."""), l("""§v = \sqrt{GM/r}§ mit §r = 6{,}791 \cdot 10^6\ \text{m}§""", """§v = \sqrt{GM/r}§ with §r = 6.791 \cdot 10^6\ \text{m}§""")),
                    ),
                    num(
                        l("Wie lange dauert ein Umlauf?", "How long does one orbit take?"), tMin, "min", rel = 0.01,
                        done = l("""§T \approx 92{,}8\ \text{min}§""", """§T \approx 92.8\ \text{min}§"""),
                        hints = listOf(l("Ein Umlauf ist ein Kreisumfang.", "One orbit is one circumference."), """§T = 2\pi r / v§""".replace('§', '$')),
                    ),
                    num(
                        l("Wie viele Sonnenaufgänge sind das pro Tag?", "How many sunrises is that per day?"), 1440.0 / tMin, "", abs = 0.6,
                        done = l("15 bis 16", "15 to 16"),
                        hints = listOf(l("Jeder Umlauf bringt einen Sonnenaufgang.", "Every orbit brings one sunrise."), """§1440\ \text{min} : T§""".replace('§', '$')),
                    ),
                ),
                solution = listOf(
                    """§\dfrac{GMm}{r^2} = \dfrac{m v^2}{r} \;\Rightarrow\; v = \sqrt{\dfrac{GM}{r}}§""".replace('§', '$'),
                    l("""§r = R_E + h = 6791\ \text{km} \;\Rightarrow\; v = \sqrt{\dfrac{3{,}99 \cdot 10^{14}}{6{,}791 \cdot 10^{6}}}\ \text{m/s} \approx 7{,}67\ \text{km/s}§""", """§r = R_E + h = 6791\ \text{km} \;\Rightarrow\; v = \sqrt{\dfrac{3.99 \cdot 10^{14}}{6.791 \cdot 10^{6}}}\ \text{m/s} \approx 7.67\ \text{km/s}§"""),
                    l("""§T = \dfrac{2\pi r}{v} \approx 5570\ \text{s} \approx 92{,}8\ \text{min}§""", """§T = \dfrac{2\pi r}{v} \approx 5570\ \text{s} \approx 92.8\ \text{min}§"""),
                    l("""§\dfrac{1440\ \text{min}}{92{,}8\ \text{min}} \approx 15{,}5§ Sonnenaufgänge""", """§\dfrac{1440\ \text{min}}{92.8\ \text{min}} \approx 15.5§ sunrises"""),
                ),
                background = l(
                    "Ein Satellit fällt ständig zur Erde, ist aber so schnell seitwärts unterwegs, dass die gekrümmte Erdoberfläche unter ihm genauso schnell wegfällt. Je tiefer die Bahn, desto stärker die Gravitation und desto schneller muss er fliegen. Ganz weit draußen, in 36.000 km Höhe, dauert ein Umlauf genau einen Tag: Dort hängen die Fernsehsatelliten scheinbar still am Himmel.",
                    "A satellite is constantly falling towards Earth, but it moves sideways so fast that the curved surface drops away beneath it just as quickly. The lower the orbit, the stronger gravity and the faster it must fly. Far out, at 36,000 km, one orbit takes exactly one day: that's where TV satellites seem to hang still in the sky.",
                ),
                fact = l("Schwerelos sind die Astronauten nicht, weil die Schwerkraft fehlt. Sie ist in 420 km Höhe noch rund 88 % so stark wie am Boden.", "Astronauts aren't weightless because gravity is missing. At 420 km it is still about 88 % as strong as on the ground."),
            )
        },
        TaskDef("scooter", Topic.Calculus, H) {
            Task(
                "scooter", Topic.Calculus, H,
                question = l("""Ein getunter E-Scooter startet aus dem Stand. Seine Beschleunigung nimmt gleichmäßig ab, bis sie null ist: §a(t) = 3{,}0 - 0{,}50\,t§ (in §\text{m/s}^2§, t in Sekunden).""", """A tuned e-scooter starts from rest. Its acceleration decreases steadily until it reaches zero: §a(t) = 3.0 - 0.50\,t§ (in §\text{m/s}^2§, t in seconds)."""),
                steps = listOf(
                    num(
                        l("Wann ist die Beschleunigung null?", "When is the acceleration zero?"), 6.0, "s", rel = 0.01,
                        done = l("""§t = 6{,}0\ \text{s}§""", """§t = 6.0\ \text{s}§"""),
                        hints = listOf(l("""Setze §a(t) = 0§.""", """Set §a(t) = 0§."""), l("""§3{,}0 - 0{,}50\,t = 0§""", """§3.0 - 0.50\,t = 0§""")),
                    ),
                    num(
                        l("Welche Höchstgeschwindigkeit erreicht er?", "What top speed does it reach?"), 3.0 * 6 - 0.25 * 36, "m/s",
                        done = l("""§v_\text{max} = 9{,}0\ \text{m/s}§""", """§v_\text{max} = 9.0\ \text{m/s}§"""),
                        hints = listOf(l("Kernidee: Die Geschwindigkeit ist das Integral der Beschleunigung.", "Key idea: velocity is the integral of acceleration."), l("""§v(t) = \displaystyle\int_0^{t} a\, dt' = 3{,}0\,t - 0{,}25\,t^2§""", """§v(t) = \displaystyle\int_0^{t} a\, dt' = 3.0\,t - 0.25\,t^2§""")),
                        formula = card(l("Integral als Fläche", "Integral as area"), """§v(t) = v_0 + \displaystyle\int_0^{t} a(t')\, dt'§""", l("Die Fläche unter der a-t-Kurve ist die Geschwindigkeitsänderung.", "The area under the a–t curve is the change in velocity.")),
                    ),
                    num(
                        l("Wie weit ist er bis dahin gefahren?", "How far has it travelled by then?"), 1.5 * 36 - 0.25 / 3 * 216, "m",
                        done = """§s = 36\ \text{m}§""",
                        hints = listOf(l("Der Weg ist das Integral der Geschwindigkeit.", "Distance is the integral of velocity."), l("""§s = \displaystyle\int_0^{6} \left(3{,}0\,t - 0{,}25\,t^2\right) dt§""", """§s = \displaystyle\int_0^{6} \left(3.0\,t - 0.25\,t^2\right) dt§""")),
                    ),
                    choice(
                        l("Wäre das in Deutschland erlaubt? (Grenze 20 km/h)", "Would that be legal in Germany? (limit 20 km/h)"),
                        listOf(l("Nein, er fährt über 30 km/h", "No, it goes over 30 km/h"), l("Ja, knapp darunter", "Yes, just below"), l("Ja, deutlich darunter", "Yes, well below")),
                        done = l("32,4 km/h, also zu schnell", "32.4 km/h, so too fast"),
                        hints = listOf(l("Von m/s nach km/h: mal 3,6.", "From m/s to km/h: times 3.6."), l("""§9{,}0\ \text{m/s} \cdot 3{,}6 = 32{,}4\ \text{km/h}§""", """§9.0\ \text{m/s} \cdot 3.6 = 32.4\ \text{km/h}§""")),
                    ),
                ),
                solution = listOf(
                    l("""§a(t) = 0 \;\Rightarrow\; t = \dfrac{3{,}0}{0{,}50}\ \text{s} = 6{,}0\ \text{s}§""", """§a(t) = 0 \;\Rightarrow\; t = \dfrac{3.0}{0.50}\ \text{s} = 6.0\ \text{s}§"""),
                    l("""§v(t) = \displaystyle\int_0^{t} \left(3{,}0 - 0{,}50\,t'\right) dt' = 3{,}0\,t - 0{,}25\,t^2 \;\Rightarrow\; v(6) = 18 - 9 = 9{,}0\ \text{m/s}§""", """§v(t) = \displaystyle\int_0^{t} \left(3.0 - 0.50\,t'\right) dt' = 3.0\,t - 0.25\,t^2 \;\Rightarrow\; v(6) = 18 - 9 = 9.0\ \text{m/s}§"""),
                    l("""§s = \displaystyle\int_0^{6} \left(3{,}0\,t - 0{,}25\,t^2\right) dt = \Big[\,1{,}5\,t^2 - \tfrac{0{,}25}{3}\,t^3\Big]_0^{6} = 54 - 18 = 36\ \text{m}§""", """§s = \displaystyle\int_0^{6} \left(3.0\,t - 0.25\,t^2\right) dt = \Big[\,1.5\,t^2 - \tfrac{0.25}{3}\,t^3\Big]_0^{6} = 54 - 18 = 36\ \text{m}§"""),
                    l("""§9{,}0\ \text{m/s} = 32{,}4\ \text{km/h} > 20\ \text{km/h}§""", """§9.0\ \text{m/s} = 32.4\ \text{km/h} > 20\ \text{km/h}§"""),
                ),
                background = l(
                    "Ableiten und Integrieren sind Gegenstücke: Leitest du den Weg nach der Zeit ab, bekommst du die Geschwindigkeit, leitest du die ab, die Beschleunigung. Integrieren geht den Weg zurück. Anschaulich ist ein Integral eine Fläche unter einer Kurve. Weil a(t) hier eine fallende Gerade ist, ist die Fläche bis t = 6 s einfach ein Dreieck: ½ · 6 · 3 = 9.",
                    "Differentiation and integration are opposites: differentiate position with respect to time and you get velocity, differentiate that and you get acceleration. Integration goes the other way. Visually, an integral is an area under a curve. Because a(t) is a falling straight line here, the area up to t = 6 s is simply a triangle: ½ · 6 · 3 = 9.",
                ),
                fact = l("Auch Tachos und Navis integrieren: Aus vielen kurzen Geschwindigkeitsmessungen rechnen sie die gefahrene Strecke aus.", "Speedometers and navigation systems integrate too: from many short speed measurements they work out the distance travelled."),
            )
        },
        TaskDef("hagel", Topic.Matter, H) {
            val vFree = sqrt(2 * G * 2000.0) * 3.6
            val vt = sqrt(8 * 1000.0 * 0.002 * G / (3 * 0.47 * 1.2))
            val vHail = vt * sqrt(10 * 0.917) * 3.6
            Task(
                "hagel", Topic.Matter, H,
                question = l("""Warum tun Regentropfen nicht weh, Hagelkörner aber schon? Ein Regentropfen (§d = 4{,}0\ \text{mm}§) fällt aus einer Wolke in §2{,}0\ \text{km}§ Höhe.""", """Why don't raindrops hurt, but hailstones do? A raindrop (§d = 4.0\ \text{mm}§) falls from a cloud at §2.0\ \text{km}§."""),
                given = listOf(
                    l("""§F_W = \tfrac{1}{2}\, c_w\, \rho_L A v^2§ mit §c_w = 0{,}47§""", """§F_W = \tfrac{1}{2}\, c_w\, \rho_L A v^2§ with §c_w = 0.47§"""),
                    l("""§\rho_L = 1{,}2\ \text{kg/m}^3,\; \rho_W = 1000\ \text{kg/m}^3,\; g = 9{,}81\ \text{m/s}^2§""", """§\rho_L = 1.2\ \text{kg/m}^3,\; \rho_W = 1000\ \text{kg/m}^3,\; g = 9.81\ \text{m/s}^2§"""),
                ),
                steps = listOf(
                    num(
                        l("Wie schnell wäre er ohne Luft am Boden?", "How fast would it hit the ground without air?"), vFree, "km/h",
                        done = """§\approx 713\ \text{km/h}§""",
                        hints = listOf(l("Ohne Luft ist es ein freier Fall.", "Without air it's a free fall."), l("""§v = \sqrt{2gh}§, dann mal 3,6""", """§v = \sqrt{2gh}§, then times 3.6""")),
                    ),
                    choice(
                        l("Was gilt bei der Endgeschwindigkeit?", "What holds at terminal velocity?"),
                        listOf("""§F_W = F_G§""".replace('§', '$'), """§a = g§""".replace('§', '$'), """§F_W = 0§""".replace('§', '$')),
                        done = """§F_W = F_G§""",
                        hints = listOf(l("Kernidee: Er wird nicht mehr schneller.", "Key idea: it doesn't get any faster."), l("Keine Beschleunigung heißt: Die Kräfte heben sich auf.", "No acceleration means the forces cancel.")),
                    ),
                    num(
                        l("Welche Endgeschwindigkeit hat der Tropfen?", "What is the drop's terminal velocity?"), vt, "m/s", rel = 0.04,
                        done = l("""§v_t \approx 9{,}6\ \text{m/s}§""", """§v_t \approx 9.6\ \text{m/s}§"""),
                        hints = listOf(l("""§m = \rho_W \cdot \tfrac{4}{3}\pi r^3,\; A = \pi r^2,\; r = 2{,}0\ \text{mm}§""", """§m = \rho_W \cdot \tfrac{4}{3}\pi r^3,\; A = \pi r^2,\; r = 2.0\ \text{mm}§"""), """§v_t = \sqrt{\dfrac{8\, \rho_W\, r\, g}{3\, c_w\, \rho_L}}§"""),
                    ),
                    num(
                        l("""Ein Hagelkorn ist 10-mal so groß und hat §\rho = 917\ \text{kg/m}^3§. Wie schnell fällt es?""", """A hailstone is 10 times larger and has §\rho = 917\ \text{kg/m}^3§. How fast does it fall?"""), vHail, "km/h", rel = 0.04,
                        done = """§\approx 105\ \text{km/h}§""",
                        hints = listOf(l("""Du musst nicht alles neu rechnen: §v_t \propto \sqrt{\rho\, r}§.""", """No need to recalculate everything: §v_t \propto \sqrt{\rho\, r}§."""), l("""Faktor §\sqrt{10 \cdot 0{,}917} \approx 3{,}03§""", """Factor §\sqrt{10 \cdot 0.917} \approx 3.03§""")),
                    ),
                ),
                solution = listOf(
                    l("""Ohne Luft: §v = \sqrt{2gh} = \sqrt{2 \cdot 9{,}81 \cdot 2000}\ \text{m/s} \approx 198\ \text{m/s} \approx 713\ \text{km/h}§""", """Without air: §v = \sqrt{2gh} = \sqrt{2 \cdot 9.81 \cdot 2000}\ \text{m/s} \approx 198\ \text{m/s} \approx 713\ \text{km/h}§"""),
                    """§\tfrac{1}{2}\, c_w \rho_L\, \pi r^2\, v_t^2 = \rho_W\, \tfrac{4}{3}\pi r^3\, g \;\Rightarrow\; v_t = \sqrt{\dfrac{8 \rho_W r g}{3 c_w \rho_L}}§""".replace('§', '$'),
                    l("""§v_t = \sqrt{\dfrac{8 \cdot 1000 \cdot 0{,}002 \cdot 9{,}81}{3 \cdot 0{,}47 \cdot 1{,}2}}\ \text{m/s} \approx 9{,}6\ \text{m/s}§""", """§v_t = \sqrt{\dfrac{8 \cdot 1000 \cdot 0.002 \cdot 9.81}{3 \cdot 0.47 \cdot 1.2}}\ \text{m/s} \approx 9.6\ \text{m/s}§"""),
                    l("""Hagel: §9{,}6\ \text{m/s} \cdot \sqrt{10 \cdot 0{,}917} \approx 29\ \text{m/s} \approx 105\ \text{km/h}§""", """Hail: §9.6\ \text{m/s} \cdot \sqrt{10 \cdot 0.917} \approx 29\ \text{m/s} \approx 105\ \text{km/h}§"""),
                ),
                background = l(
                    "Der Luftwiderstand wächst mit dem Quadrat der Geschwindigkeit. Ein fallender Körper wird deshalb schneller, bis der Widerstand so groß ist wie sein Gewicht. Ab dann fällt er gleichmäßig: mit der Endgeschwindigkeit. Große Körper sind dabei im Vorteil, weil ihr Gewicht mit dem Volumen (r³) wächst, die bremsende Fläche aber nur mit r². So kommt es, dass ein Hagelkorn zehnmal so groß dreimal so schnell fällt.",
                    "Air resistance grows with the square of speed. A falling object therefore speeds up until the drag equals its weight. From then on it falls steadily at terminal velocity. Large objects have the advantage, because their weight grows with volume (r³) while the braking area only grows with r². That's why a hailstone ten times larger falls three times faster.",
                ),
                fact = l("Ohne Luft wäre jeder Regenschauer tödlich. Echte Tropfen fallen sogar etwas langsamer, weil sie sich zu kleinen Brötchen abplatten.", "Without air every rain shower would be deadly. Real drops fall even a little slower because they flatten into tiny buns."),
            )
        },
        TaskDef("mond-golf", Topic.Gravity, H) {
            val vEsc = sqrt(2 * 4.90e12 / 1.737e6)
            Task(
                "mond-golf", Topic.Gravity, H,
                question = l("Wie schnell müsste man einen Golfball auf dem Mond schlagen, damit er nie wieder zurückkommt?", "How fast would you have to hit a golf ball on the Moon so that it never comes back?"),
                given = listOf("""§GM_\text{Mond} = 4{,}90 \cdot 10^{12}\ \text{m}^3/\text{s}^2,\quad R_\text{Mond} = 1737\ \text{km}§""".let { l(it, it.replace("4{,}90", "4.90").replace("Mond", "Moon")) }),
                steps = listOf(
                    choice(
                        l("Welcher Ansatz passt?", "Which approach fits?"),
                        listOf("""§\tfrac{1}{2} m v^2 - \dfrac{GMm}{R} = 0§""".replace('§', '$'), """§\tfrac{1}{2} m v^2 = m g h§""".replace('§', '$'), """§\dfrac{m v^2}{R} = \dfrac{GMm}{R^2}§""".replace('§', '$')),
                        done = l("Energie reicht gerade bis ins Unendliche", "Energy just reaches infinity"),
                        hints = listOf(l("Kernidee: Er muss genug Bewegungsenergie haben, um aus dem Schwerefeld zu kommen.", "Key idea: it needs enough kinetic energy to escape the gravitational field."), l("""Die Lageenergie im Abstand §r§ ist §-GMm/r§, im Unendlichen null.""", """The potential energy at distance §r§ is §-GMm/r§, zero at infinity.""")),
                        formula = card(l("Lageenergie im Schwerefeld", "Gravitational potential energy"), """§E_\text{pot} = -\dfrac{G M m}{r}§""", l("Weit weg ist sie null, nah dran negativ.", "Far away it's zero, close by it's negative.")),
                    ),
                    num(
                        l("Fluchtgeschwindigkeit", "Escape velocity"), vEsc / 1000.0, "km/s", rel = 0.02,
                        done = l("""§v \approx 2{,}38\ \text{km/s}§""", """§v \approx 2.38\ \text{km/s}§"""),
                        hints = listOf(l("Die Masse des Balls kürzt sich.", "The mass of the ball cancels."), """§v = \sqrt{\dfrac{2GM}{R}}§"""),
                    ),
                    num(
                        l("Ein Golfprofi schafft etwa 80 m/s. Wie viel mal schneller müsste er schlagen?", "A golf pro manages about 80 m/s. How many times faster would he have to hit?"), vEsc / 80.0, "", rel = 0.03,
                        done = l("etwa 30-mal", "about 30 times"),
                        hints = listOf(l("Einfach teilen.", "Just divide."), """§2380 : 80§"""),
                    ),
                ),
                solution = listOf(
                    """§\tfrac{1}{2} m v^2 = \dfrac{GMm}{R} \;\Rightarrow\; v = \sqrt{\dfrac{2GM}{R}}§""".replace('§', '$'),
                    l("""§v = \sqrt{\dfrac{2 \cdot 4{,}90 \cdot 10^{12}}{1{,}737 \cdot 10^{6}}}\ \text{m/s} \approx 2{,}38\ \text{km/s} \approx 30 \cdot 80\ \text{m/s}§""", """§v = \sqrt{\dfrac{2 \cdot 4.90 \cdot 10^{12}}{1.737 \cdot 10^{6}}}\ \text{m/s} \approx 2.38\ \text{km/s} \approx 30 \cdot 80\ \text{m/s}§"""),
                ),
                background = l(
                    "Die Fluchtgeschwindigkeit ist die Geschwindigkeit, ab der ein Körper ohne weiteren Antrieb nie mehr zurückfällt. Sie hängt nur von Masse und Radius des Himmelskörpers ab, nicht vom Körper selbst. Auf der Erde sind es 11,2 km/s, auf dem Mond nur 2,4 km/s. Deshalb hat der Mond keine Lufthülle: Seine Gasteilchen sind schnell genug, um zu entkommen.",
                    "Escape velocity is the speed above which an object never falls back without further propulsion. It only depends on the mass and radius of the celestial body, not on the object itself. On Earth it is 11.2 km/s, on the Moon only 2.4 km/s. That's why the Moon has no atmosphere: its gas particles are fast enough to escape.",
                ),
                fact = l("Alan Shepard hat 1971 auf dem Mond tatsächlich zwei Golfbälle geschlagen. Weit gekommen sind sie im steifen Raumanzug aber nicht.", "In 1971 Alan Shepard actually hit two golf balls on the Moon. In a stiff space suit they didn't get very far, though."),
            )
        },
        TaskDef("kaffee", Topic.Calculus, H) {
            val k = ln(70.0 / 55.0) / 5.0
            val t = ln(70.0 / 40.0) / k
            Task(
                "kaffee", Topic.Calculus, H,
                question = l("""Dein Kaffee hat §90\ °\text{C}§, das Zimmer §20\ °\text{C}§. Nach 5 Minuten ist er §75\ °\text{C}§ warm. Er kühlt nach §T(t) = 20 + 70\, e^{-kt}§ ab. Wann hat er trinkbare §60\ °\text{C}§?""", """Your coffee is §90\ °\text{C}§, the room §20\ °\text{C}§. After 5 minutes it is §75\ °\text{C}§. It cools as §T(t) = 20 + 70\, e^{-kt}§. When does it reach a drinkable §60\ °\text{C}§?"""),
                steps = listOf(
                    num(
                        l("""Wie groß ist §k§ (pro Minute)?""", """What is §k§ (per minute)?"""), k, "1/min", rel = 0.03,
                        done = l("""§k \approx 0{,}048\ \text{min}^{-1}§""", """§k \approx 0.048\ \text{min}^{-1}§"""),
                        hints = listOf(l("""Setze §T(5) = 75§ ein: §55 = 70\, e^{-5k}§""", """Insert §T(5) = 75§: §55 = 70\, e^{-5k}§"""), """§k = \dfrac{1}{5} \ln \dfrac{70}{55}§"""),
                        formula = card(l("Exponentialfunktion", "Exponential function"), """§e^{-x} = y \;\Leftrightarrow\; x = -\ln y§""", l("Der natürliche Logarithmus ist die Umkehrung.", "The natural logarithm is the inverse.")),
                    ),
                    num(
                        l("Nach wie vielen Minuten ist er 60 °C warm?", "After how many minutes is it at 60 °C?"), t, "min", rel = 0.03,
                        done = l("""§t \approx 11{,}6\ \text{min}§""", """§t \approx 11.6\ \text{min}§"""),
                        hints = listOf("""§40 = 70\, e^{-kt}§""", """§t = \dfrac{1}{k} \ln \dfrac{70}{40}§"""),
                    ),
                    choice(
                        l("Du willst ihn möglichst schnell trinken. Wann gibst du die kalte Milch dazu?", "You want to drink it as soon as possible. When do you add the cold milk?"),
                        listOf(l("Erst kurz vor dem Trinken", "Only just before drinking"), l("Sofort am Anfang", "Right at the start"), l("Das ist egal", "It doesn't matter")),
                        done = l("Erst später", "Later"),
                        hints = listOf(l("Kernidee: Wie schnell er abkühlt, hängt vom Temperaturunterschied zum Zimmer ab.", "Key idea: how fast it cools depends on the temperature difference to the room."), l("Heißer Kaffee verliert pro Minute mehr Wärme als lauwarmer.", "Hot coffee loses more heat per minute than lukewarm coffee.")),
                    ),
                ),
                solution = listOf(
                    l("""§55 = 70\, e^{-5k} \;\Rightarrow\; k = \tfrac{1}{5} \ln \tfrac{70}{55} \approx 0{,}048\ \text{min}^{-1}§""", """§55 = 70\, e^{-5k} \;\Rightarrow\; k = \tfrac{1}{5} \ln \tfrac{70}{55} \approx 0.048\ \text{min}^{-1}§"""),
                    l("""§40 = 70\, e^{-kt} \;\Rightarrow\; t = \dfrac{\ln(70/40)}{k} \approx 11{,}6\ \text{min}§""", """§40 = 70\, e^{-kt} \;\Rightarrow\; t = \dfrac{\ln(70/40)}{k} \approx 11.6\ \text{min}§"""),
                ),
                background = l(
                    "Newtons Abkühlungsgesetz sagt: Ein Körper verliert Wärme umso schneller, je größer der Unterschied zur Umgebung ist. Daraus folgt eine Exponentialfunktion, die sich der Raumtemperatur immer weiter nähert, sie aber nie ganz erreicht. Dieselbe Mathematik beschreibt radioaktiven Zerfall, das Entladen eines Kondensators und wie Medikamente im Blut abgebaut werden.",
                    "Newton's law of cooling says an object loses heat faster the bigger the difference to its surroundings. This leads to an exponential function that gets ever closer to room temperature but never quite reaches it. The same maths describes radioactive decay, a discharging capacitor and how medicines are broken down in the blood.",
                ),
                fact = l("Milch erst am Ende: Solange der Kaffee heiß ist, kühlt er schnell ab. Kühlst du ihn sofort mit Milch, verliert er danach langsamer Wärme.", "Milk at the end: while the coffee is hot it cools quickly. If you cool it with milk straight away, it loses heat more slowly afterwards."),
            )
        },

        // ======================================== EXPERTE ========================================
        TaskDef("spin", Topic.Quantum, X) {
            val p2 = cos(PI / 6).pow(2) * 100
            val p = 0.5 * cos(PI / 6).pow(2) * kotlin.math.sin(PI / 6).pow(2) * 100
            Task(
                "spin", Topic.Quantum, X,
                question = l("""Ein Strahl unpolarisierter Silberatome (Spin ½) läuft durch drei Stern-Gerlach-Magnete. Hinter jedem kommt nur ein Teilstrahl weiter: ① entlang §z§ nur §|\uparrow_z\rangle§, ② um §\theta = 60^\circ§ gegen §z§ gedreht nur §|\uparrow_\theta\rangle§, ③ wieder entlang §z§ nur §|\downarrow_z\rangle§.""", """A beam of unpolarised silver atoms (spin ½) passes three Stern–Gerlach magnets. Behind each only one sub-beam continues: ① along §z§ only §|\uparrow_z\rangle§, ② rotated by §\theta = 60^\circ§ from §z§ only §|\uparrow_\theta\rangle§, ③ along §z§ again only §|\downarrow_z\rangle§."""),
                steps = listOf(
                    choice(
                        l("""Wie wahrscheinlich misst man §|\uparrow_\theta\rangle§, wenn §|\uparrow_z\rangle§ präpariert ist?""", """How likely is §|\uparrow_\theta\rangle§ if §|\uparrow_z\rangle§ was prepared?"""),
                        listOf("""§\cos^2(\theta/2)§""".replace('§', '$'), """§\cos^2\theta§""".replace('§', '$'), """§\cos\theta§""".replace('§', '$')),
                        done = """§P = \cos^2(\theta/2)§""",
                        hints = listOf(l("""Kernidee: §|\uparrow\rangle§ und §|\downarrow\rangle§ zeigen im Raum in entgegengesetzte Richtungen (180°), als Zustände sind sie aber orthogonal (90°).""", """Key idea: §|\uparrow\rangle§ and §|\downarrow\rangle§ point in opposite directions in space (180°), but as states they are orthogonal (90°)."""), """§|\uparrow_\theta\rangle = \cos\tfrac{\theta}{2}\, |\uparrow_z\rangle + \sin\tfrac{\theta}{2}\, |\downarrow_z\rangle§"""),
                        formula = card(l("Bornsche Regel", "Born rule"), """§P = \left|\langle \psi | \phi \rangle\right|^2§""", l("Die Wahrscheinlichkeit ist das Betragsquadrat der Überlappung.", "The probability is the squared magnitude of the overlap.")),
                    ),
                    num(
                        l("Welcher Anteil der Atome, die Magnet ② erreichen, kommt durch?", "What share of the atoms reaching magnet ② gets through?"), p2, "%", abs = 1.0,
                        done = "75 %",
                        hints = listOf("""§\theta / 2 = 30^\circ§""", """§\cos^2 30^\circ = \tfrac{3}{4}§"""),
                    ),
                    num(
                        l("""Welcher Anteil des ursprünglichen Strahls kommt am Ende als §|\downarrow_z\rangle§ heraus?""", """What share of the original beam comes out at the end as §|\downarrow_z\rangle§?"""), p, "%", abs = 0.3,
                        done = l("""§\tfrac{3}{32} \approx 9{,}4\ \%§""", """§\tfrac{3}{32} \approx 9.4\ \%§"""),
                        hints = listOf(l("Unpolarisiert heißt: Magnet ① lässt die Hälfte durch.", "Unpolarised means magnet ① lets half through."), """§P = \tfrac{1}{2} \cdot \cos^2 30^\circ \cdot \sin^2 30^\circ§"""),
                    ),
                    choice(
                        l("Was passiert, wenn man Magnet ② ganz entfernt?", "What happens if you remove magnet ② entirely?"),
                        listOf(l("Am Ende kommt nichts mehr heraus", "Nothing comes out at the end"), l("Es kommen doppelt so viele heraus", "Twice as many come out"), l("Es ändert sich nichts", "Nothing changes")),
                        done = l("Dann kommt nichts heraus", "Then nothing comes out"),
                        hints = listOf(l("""Ohne ② trifft §|\uparrow_z\rangle§ direkt auf den §\downarrow_z§-Filter.""", """Without ②, §|\uparrow_z\rangle§ hits the §\downarrow_z§ filter directly."""), """§\langle \downarrow_z | \uparrow_z \rangle = 0§"""),
                    ),
                ),
                solution = listOf(
                    """§\left|\langle \uparrow_\theta | \uparrow_z \rangle\right|^2 = \cos^2 \tfrac{\theta}{2}, \qquad \left|\langle \downarrow_z | \uparrow_\theta \rangle\right|^2 = \sin^2 \tfrac{\theta}{2}§""".replace('§', '$'),
                    l("""§P = \tfrac{1}{2} \cdot \cos^2 30^\circ \cdot \sin^2 30^\circ = \tfrac{1}{2} \cdot \tfrac{3}{4} \cdot \tfrac{1}{4} = \tfrac{3}{32} \approx 9{,}4\ \%§""", """§P = \tfrac{1}{2} \cdot \cos^2 30^\circ \cdot \sin^2 30^\circ = \tfrac{1}{2} \cdot \tfrac{3}{4} \cdot \tfrac{1}{4} = \tfrac{3}{32} \approx 9.4\ \%§"""),
                    l("""Ohne ②: §P = \tfrac{1}{2} \cdot \left|\langle \downarrow_z | \uparrow_z \rangle\right|^2 = 0§""", """Without ②: §P = \tfrac{1}{2} \cdot \left|\langle \downarrow_z | \uparrow_z \rangle\right|^2 = 0§"""),
                ),
                background = l(
                    "Der Spin ist ein Eigendrehimpuls von Teilchen, der kein klassisches Gegenstück hat. Misst man ihn entlang einer Achse, kommt bei Spin ½ immer nur einer von zwei Werten heraus: auf oder ab. 1922 zeigten Stern und Gerlach das mit Silberatomen in einem ungleichmäßigen Magnetfeld: Der Strahl spaltete sich in genau zwei Flecken. Eine Messung legt den Spin entlang ihrer Achse fest und löscht dabei die Information über die anderen Achsen. Genau deshalb taucht hinter Magnet ③ wieder ↓z auf, obwohl Magnet ① es aussortiert hatte.",
                    "Spin is an intrinsic angular momentum of particles with no classical counterpart. Measured along an axis, spin ½ only ever gives one of two values: up or down. In 1922 Stern and Gerlach showed this with silver atoms in a non-uniform magnetic field: the beam split into exactly two spots. A measurement fixes the spin along its axis and erases the information about the other axes. That's exactly why ↓z reappears behind magnet ③ although magnet ① had filtered it out.",
                ),
                fact = l("Ein zusätzlicher Filter lässt mehr durch als gar keiner, genau wie bei drei Polarisationsfiltern. Und der Faktor θ/2 verrät: Ein Spin-½ ist erst nach einer Drehung um 720° wieder genau im selben Zustand.", "An extra filter lets more through than none at all, just like three polarising filters. And the factor θ/2 reveals: a spin-½ only returns to exactly the same state after a 720° rotation."),
            )
        },
        TaskDef("quantenpunkt", Topic.Quantum, X) {
            val h = 6.63e-34
            val me = 9.11e-31
            val e1 = h * h / (8 * me * 1e-18) / 1.60e-19
            val lam = 1240.0 / (3 * e1)
            val lGreen = sqrt(530.0 / lam)
            Task(
                "quantenpunkt", Topic.Quantum, X,
                question = l("""QLED-Fernseher leuchten mit Quantenpunkten: Nanokristallen, deren Farbe nur von ihrer Größe abhängt. Einfaches Modell: ein Elektron in einem eindimensionalen Kasten mit unendlich hohen Wänden, §L = 1{,}0\ \text{nm}§.""", """QLED TVs glow using quantum dots: nanocrystals whose colour depends only on their size. Simple model: an electron in a one-dimensional box with infinitely high walls, §L = 1.0\ \text{nm}§."""),
                given = listOf(
                    l("""§h = 6{,}63 \cdot 10^{-34}\ \text{J s},\; m_e = 9{,}11 \cdot 10^{-31}\ \text{kg}§""", """§h = 6.63 \cdot 10^{-34}\ \text{J s},\; m_e = 9.11 \cdot 10^{-31}\ \text{kg}§"""),
                    l("""§hc = 1240\ \text{eV nm},\; 1\ \text{eV} = 1{,}60 \cdot 10^{-19}\ \text{J}§""", """§hc = 1240\ \text{eV nm},\; 1\ \text{eV} = 1.60 \cdot 10^{-19}\ \text{J}§"""),
                ),
                steps = listOf(
                    choice(
                        l("""Wie hängen die Energieniveaus von §n§ ab?""", """How do the energy levels depend on §n§?"""),
                        listOf("""§E_n \propto n^2§""".replace('§', '$'), """§E_n \propto n§""".replace('§', '$'), """§E_n \propto 1/n^2§""".replace('§', '$')),
                        done = """§E_n \propto n^2§""",
                        hints = listOf(l("""Kernidee: In den Kasten passen nur stehende Wellen mit §L = n\, \lambda / 2§.""", """Key idea: only standing waves with §L = n\, \lambda / 2§ fit into the box."""), """§p = h / \lambda,\quad E = p^2 / 2m§"""),
                        formula = card("de Broglie", """§\lambda = \dfrac{h}{p}§""", l("Jedes Teilchen ist auch eine Welle mit dieser Wellenlänge.", "Every particle is also a wave with this wavelength.")),
                    ),
                    num(
                        l("""Wie groß ist die Grundzustandsenergie §E_1§?""", """What is the ground-state energy §E_1§?"""), e1, "eV", rel = 0.03,
                        done = l("""§E_1 \approx 0{,}38\ \text{eV}§""", """§E_1 \approx 0.38\ \text{eV}§"""),
                        hints = listOf("""§E_n = \dfrac{n^2 h^2}{8 m L^2}§""", l("""Das Ergebnis in Joule durch §1{,}60 \cdot 10^{-19}§ teilen.""", """Divide the result in joules by §1.60 \cdot 10^{-19}§.""")),
                    ),
                    num(
                        l("""Welche Wellenlänge hat das Licht beim Übergang §n = 2 \to 1§?""", """What is the wavelength of the light for the transition §n = 2 \to 1§?"""), lam, "nm", rel = 0.03,
                        done = """§\lambda \approx 1100\ \text{nm}§""",
                        hints = listOf("""§\Delta E = E_2 - E_1 = 3 E_1§""", """§\lambda = hc / \Delta E§"""),
                    ),
                    num(
                        l("""Wie breit muss der Kasten für grünes Licht (§530\ \text{nm}§) sein?""", """How wide must the box be for green light (§530\ \text{nm}§)?"""), lGreen, "nm", rel = 0.03,
                        done = l("""§L \approx 0{,}69\ \text{nm}§""", """§L \approx 0.69\ \text{nm}§"""),
                        hints = listOf(l("""Es gilt §\Delta E \propto 1/L^2§, also §\lambda \propto L^2§.""", """We have §\Delta E \propto 1/L^2§, so §\lambda \propto L^2§."""), l("""§L = 1{,}0\ \text{nm} \cdot \sqrt{530/1100}§""", """§L = 1.0\ \text{nm} \cdot \sqrt{530/1100}§""")),
                    ),
                ),
                solution = listOf(
                    """§L = n\, \dfrac{\lambda}{2},\quad p = \dfrac{h}{\lambda} \;\Rightarrow\; E_n = \dfrac{p^2}{2m} = \dfrac{n^2 h^2}{8 m L^2}§""".replace('§', '$'),
                    l("""§E_1 = \dfrac{(6{,}63 \cdot 10^{-34})^2}{8 \cdot 9{,}11 \cdot 10^{-31} \cdot (10^{-9})^2}\ \text{J} \approx 6{,}0 \cdot 10^{-20}\ \text{J} \approx 0{,}38\ \text{eV}§""", """§E_1 = \dfrac{(6.63 \cdot 10^{-34})^2}{8 \cdot 9.11 \cdot 10^{-31} \cdot (10^{-9})^2}\ \text{J} \approx 6.0 \cdot 10^{-20}\ \text{J} \approx 0.38\ \text{eV}§"""),
                    l("""§\Delta E = 3 E_1 \approx 1{,}13\ \text{eV} \;\Rightarrow\; \lambda = \dfrac{1240\ \text{eV nm}}{1{,}13\ \text{eV}} \approx 1100\ \text{nm}§ (Infrarot)""", """§\Delta E = 3 E_1 \approx 1.13\ \text{eV} \;\Rightarrow\; \lambda = \dfrac{1240\ \text{eV nm}}{1.13\ \text{eV}} \approx 1100\ \text{nm}§ (infrared)"""),
                    l("""§\lambda \propto L^2 \;\Rightarrow\; L = 1{,}0\ \text{nm} \cdot \sqrt{\tfrac{530}{1100}} \approx 0{,}69\ \text{nm}§""", """§\lambda \propto L^2 \;\Rightarrow\; L = 1.0\ \text{nm} \cdot \sqrt{\tfrac{530}{1100}} \approx 0.69\ \text{nm}§"""),
                ),
                background = l(
                    "Ein eingesperrtes Elektron verhält sich wie eine Welle auf einer gespannten Saite: Nur bestimmte Schwingungen passen hinein, deshalb sind nur bestimmte Energien erlaubt. Springt das Elektron von einem höheren auf ein tieferes Niveau, sendet es ein Photon mit genau der Energiedifferenz aus. Je kleiner der Kasten, desto größer die Abstände zwischen den Niveaus und desto blauer das Licht. So stellt man in Bildschirmen die Farben ein: gleiches Material, nur verschieden große Kristalle.",
                    "A trapped electron behaves like a wave on a stretched string: only certain vibrations fit, so only certain energies are allowed. When the electron jumps from a higher to a lower level, it emits a photon with exactly the energy difference. The smaller the box, the larger the gaps between the levels and the bluer the light. That's how screens set their colours: same material, just crystals of different sizes.",
                ),
                fact = l("Echte Quantenpunkte sind dreidimensional, und das Elektron wirkt im Kristall leichter, deshalb sind sie einige Nanometer groß. Für ihre Entdeckung gab es 2023 den Chemie-Nobelpreis.", "Real quantum dots are three-dimensional and the electron acts lighter inside the crystal, so they are a few nanometres in size. Their discovery won the 2023 Nobel Prize in Chemistry."),
            )
        },
        TaskDef("gps", Topic.Relativity, X) {
            val c = 3.00e8
            val sr = 3870.0 * 3870.0 / (2 * c * c) * 86400 * 1e6
            val gr = 3.99e14 / (c * c) * (1 / 6.371e6 - 1 / 2.6571e7) * 86400 * 1e6
            val km = (gr - sr) * 1e-6 * c / 1000
            Task(
                "gps", Topic.Relativity, X,
                question = l("""GPS-Satelliten kreisen in §20\,200\ \text{km}§ Höhe mit §v = 3{,}87\ \text{km/s}§. Ihre Atomuhren gehen anders als Uhren am Boden, und zwar aus zwei Gründen, die sich teilweise aufheben.""", """GPS satellites orbit at §20\,200\ \text{km}§ with §v = 3.87\ \text{km/s}§. Their atomic clocks run differently from clocks on the ground, for two reasons that partly cancel out."""),
                given = listOf(
                    l("""§c = 3{,}00 \cdot 10^{8}\ \text{m/s},\; GM_E = 3{,}99 \cdot 10^{14}\ \text{m}^3/\text{s}^2§""", """§c = 3.00 \cdot 10^{8}\ \text{m/s},\; GM_E = 3.99 \cdot 10^{14}\ \text{m}^3/\text{s}^2§"""),
                    l("""§R_E = 6371\ \text{km},\; 1\ \text{Tag} = 86\,400\ \text{s}§""", """§R_E = 6371\ \text{km},\; 1\ \text{day} = 86\,400\ \text{s}§"""),
                ),
                steps = listOf(
                    num(
                        l("Spezielle Relativität: Um wie viel geht die Satellitenuhr pro Tag nach?", "Special relativity: by how much does the satellite clock fall behind per day?"), sr, "µs", rel = 0.03,
                        done = l("""§\approx 7{,}2\ \mu\text{s}§ nach""", """§\approx 7.2\ \mu\text{s}§ behind"""),
                        hints = listOf(l("Kernidee: Bewegte Uhren gehen langsamer.", "Key idea: moving clocks run slow."), l("""Für §v \ll c§ gilt §\dfrac{\Delta t}{t} \approx \dfrac{v^2}{2c^2}§.""", """For §v \ll c§: §\dfrac{\Delta t}{t} \approx \dfrac{v^2}{2c^2}§.""")),
                        formula = card(l("Zeitdilatation", "Time dilation"), """§\Delta t' = \dfrac{\Delta t}{\gamma},\quad \gamma = \dfrac{1}{\sqrt{1 - v^2/c^2}}§""", l("Für kleine Geschwindigkeiten ist γ ≈ 1 + v²/2c².", "For small speeds γ ≈ 1 + v²/2c².")),
                    ),
                    num(
                        l("Allgemeine Relativität: Um wie viel geht sie pro Tag vor?", "General relativity: by how much does it run ahead per day?"), gr, "µs", rel = 0.03,
                        done = l("""§\approx 45{,}7\ \mu\text{s}§ vor""", """§\approx 45.7\ \mu\text{s}§ ahead"""),
                        hints = listOf(l("Kernidee: Weiter oben im Schwerefeld vergeht die Zeit schneller.", "Key idea: higher up in a gravitational field, time passes faster."), l("""§\dfrac{\Delta t}{t} \approx \dfrac{GM}{c^2} \left(\dfrac{1}{R_E} - \dfrac{1}{r}\right)§ mit §r = R_E + h§""", """§\dfrac{\Delta t}{t} \approx \dfrac{GM}{c^2} \left(\dfrac{1}{R_E} - \dfrac{1}{r}\right)§ with §r = R_E + h§""")),
                    ),
                    num(
                        l("Welcher Positionsfehler entstünde ohne Korrektur pro Tag?", "What position error would build up per day without correction?"), km, "km", rel = 0.04,
                        done = l("""§\approx 11{,}6\ \text{km}§""", """§\approx 11.6\ \text{km}§"""),
                        hints = listOf(l("Netto: vor minus nach.", "Net: ahead minus behind."), l("""GPS misst Laufzeiten des Lichts: §\Delta x = c\, \Delta t§""", """GPS measures light travel times: §\Delta x = c\, \Delta t§""")),
                    ),
                ),
                solution = listOf(
                    l("""SRT: §\dfrac{v^2}{2c^2} = \dfrac{3870^2}{2 \cdot (3{,}00 \cdot 10^8)^2} \approx 8{,}3 \cdot 10^{-11} \;\Rightarrow\; 7{,}2\ \mu\text{s}§ pro Tag nach""", """SR: §\dfrac{v^2}{2c^2} = \dfrac{3870^2}{2 \cdot (3.00 \cdot 10^8)^2} \approx 8.3 \cdot 10^{-11} \;\Rightarrow\; 7.2\ \mu\text{s}§ behind per day"""),
                    l("""ART: §\dfrac{GM}{c^2} \left(\dfrac{1}{R_E} - \dfrac{1}{r}\right) \approx 5{,}3 \cdot 10^{-10} \;\Rightarrow\; 45{,}7\ \mu\text{s}§ pro Tag vor""", """GR: §\dfrac{GM}{c^2} \left(\dfrac{1}{R_E} - \dfrac{1}{r}\right) \approx 5.3 \cdot 10^{-10} \;\Rightarrow\; 45.7\ \mu\text{s}§ ahead per day"""),
                    l("""Netto §\approx 38{,}5\ \mu\text{s}§ vor §\;\Rightarrow\; \Delta x = c\, \Delta t \approx 11{,}6\ \text{km}§""", """Net §\approx 38.5\ \mu\text{s}§ ahead §\;\Rightarrow\; \Delta x = c\, \Delta t \approx 11.6\ \text{km}§"""),
                ),
                background = l(
                    "GPS funktioniert, weil jeder Satellit ständig sendet, wie spät es bei ihm ist. Dein Handy vergleicht die Zeiten von mindestens vier Satelliten und rechnet daraus aus, wie weit sie entfernt sind. Licht legt in einer Mikrosekunde 300 m zurück, deshalb müssen die Uhren extrem genau gehen. Einsteins spezielle Relativität sagt: Schnell bewegte Uhren gehen langsamer. Die allgemeine Relativität sagt: Im schwächeren Schwerefeld weiter oben vergeht die Zeit schneller. Beim GPS gewinnt die Gravitation.",
                    "GPS works because every satellite constantly broadcasts what time it is aboard. Your phone compares the times from at least four satellites and works out how far away they are. Light travels 300 m in one microsecond, so the clocks must be extremely precise. Einstein's special relativity says fast-moving clocks run slow. General relativity says that in the weaker gravity higher up, time passes faster. For GPS, gravity wins.",
                ),
                fact = l("Die Satellitenuhren werden schon vor dem Start absichtlich verstimmt: Sie ticken mit 10,22999999543 MHz statt mit 10,23 MHz.", "The satellite clocks are deliberately detuned before launch: they tick at 10.22999999543 MHz instead of 10.23 MHz."),
            )
        },
        TaskDef("neutron", Topic.Modern, X) {
            val q = 939.565 - 938.272 - 0.511
            Task(
                "neutron", Topic.Modern, X,
                question = l("Ein freies Neutron lebt im Mittel nur knapp 15 Minuten, dann zerfällt es. In welche Teilchen, und warum genau so?", "A free neutron lives only just under 15 minutes on average, then it decays. Into which particles, and why exactly like that?"),
                given = listOf(l("""§m_n c^2 = 939{,}565\ \text{MeV},\; m_p c^2 = 938{,}272\ \text{MeV},\; m_e c^2 = 0{,}511\ \text{MeV}§""", """§m_n c^2 = 939.565\ \text{MeV},\; m_p c^2 = 938.272\ \text{MeV},\; m_e c^2 = 0.511\ \text{MeV}§""")),
                steps = listOf(
                    choice(
                        l("Welche dieser Reaktionen kommt in der Natur vor?", "Which of these reactions occurs in nature?"),
                        listOf("""§n \to p + e^- + \bar{\nu}_e§""".replace('§', '$'), """§p \to e^+ + \gamma§""".replace('§', '$'), """§\mu^- \to e^- + \gamma§""".replace('§', '$')),
                        done = """§n \to p + e^- + \bar{\nu}_e§""",
                        hints = listOf(l("Kernidee: Ladung, Baryonenzahl und Leptonenzahl (je Familie) müssen erhalten bleiben.", "Key idea: charge, baryon number and lepton number (per family) must be conserved."), l("Beim Proton-Zerfall verschwindet ein Baryon, beim Myon-Zerfall ändert sich die Leptonfamilie.", "In the proton decay a baryon disappears; in the muon decay the lepton family changes.")),
                    ),
                    num(
                        l("Wie viel Energie wird beim Neutronzerfall höchstens frei?", "How much energy is released at most in neutron decay?"), q, "MeV", abs = 0.005,
                        done = l("""§Q \approx 0{,}782\ \text{MeV}§""", """§Q \approx 0.782\ \text{MeV}§"""),
                        hints = listOf(l("Die Ruheenergie vorher minus nachher.", "Rest energy before minus after."), """§Q = (m_n - m_p - m_e)\, c^2§"""),
                        formula = card(l("Masse ist Energie", "Mass is energy"), """§E = m c^2§""", l("Was an Masse fehlt, wird als Bewegungsenergie frei.", "Missing mass is released as kinetic energy.")),
                    ),
                    choice(
                        l("Warum braucht man das Antineutrino überhaupt?", "Why is the antineutrino needed at all?"),
                        listOf(l("Ohne es hätte das Elektron immer dieselbe Energie, gemessen wird aber ein breites Spektrum", "Without it the electron would always have the same energy, but a broad spectrum is measured"), l("Wegen der Ladungserhaltung", "Because of charge conservation"), l("Weil das Neutron sonst zu leicht wäre", "Because otherwise the neutron would be too light")),
                        done = l("Wegen des Energiespektrums", "Because of the energy spectrum"),
                        hints = listOf(l("Bei einem Zerfall in nur zwei Teilchen sind die Energien durch Energie- und Impulserhaltung festgelegt.", "In a decay into just two particles, energy and momentum conservation fix the energies."), l("Gemessen wurden aber Elektronen mit allen Energien von 0 bis Q.", "But electrons with every energy from 0 to Q were measured.")),
                    ),
                ),
                solution = listOf(
                    l("""Erlaubt: §n \to p + e^- + \bar{\nu}_e§ (Ladung 0 = +1 − 1, Baryonenzahl 1 = 1, Leptonenzahl 0 = 1 − 1)""", """Allowed: §n \to p + e^- + \bar{\nu}_e§ (charge 0 = +1 − 1, baryon number 1 = 1, lepton number 0 = 1 − 1)"""),
                    l("""§Q = (939{,}565 - 938{,}272 - 0{,}511)\ \text{MeV} = 0{,}782\ \text{MeV}§""", """§Q = (939.565 - 938.272 - 0.511)\ \text{MeV} = 0.782\ \text{MeV}§"""),
                ),
                background = l(
                    "Teilchenreaktionen folgen Erhaltungssätzen: Neben Energie, Impuls und Ladung bleiben auch die Zahl der Baryonen (Protonen, Neutronen) und die der Leptonen jeder Familie gleich. Das Neutron ist ein bisschen schwerer als Proton plus Elektron, deshalb darf es zerfallen. 1930 sah es so aus, als ginge dabei Energie verloren. Wolfgang Pauli rettete den Energiesatz mit einem damals unsichtbaren Teilchen, dem Neutrino. Nachgewiesen wurde es erst 1956.",
                    "Particle reactions obey conservation laws: besides energy, momentum and charge, the number of baryons (protons, neutrons) and of leptons of each family stays the same. The neutron is slightly heavier than proton plus electron, so it is allowed to decay. In 1930 it looked as if energy was lost in the process. Wolfgang Pauli saved energy conservation with a then invisible particle, the neutrino. It was only detected in 1956.",
                ),
                fact = l("Pauli nannte das Neutrino in einem Brief einen verzweifelten Ausweg und fürchtete, man werde es nie nachweisen können.", "In a letter Pauli called the neutrino a desperate remedy and feared it could never be detected."),
            )
        },
        TaskDef("schwarzloch", Topic.Astro, X) {
            val rs = 2 * 3.99e14 / (3.00e8 * 3.00e8)
            Task(
                "schwarzloch", Topic.Astro, X,
                question = l("Wie klein müsste man die Erde zusammendrücken, damit sie ein Schwarzes Loch wird?", "How small would you have to squeeze the Earth to turn it into a black hole?"),
                given = listOf(l("""§GM_E = 3{,}99 \cdot 10^{14}\ \text{m}^3/\text{s}^2,\; M_E = 5{,}97 \cdot 10^{24}\ \text{kg},\; c = 3{,}00 \cdot 10^{8}\ \text{m/s}§""", """§GM_E = 3.99 \cdot 10^{14}\ \text{m}^3/\text{s}^2,\; M_E = 5.97 \cdot 10^{24}\ \text{kg},\; c = 3.00 \cdot 10^{8}\ \text{m/s}§""")),
                steps = listOf(
                    choice(
                        l("Welche Bedingung führt (zufällig sogar exakt) zum richtigen Radius?", "Which condition leads (by coincidence even exactly) to the right radius?"),
                        listOf(l("""Fluchtgeschwindigkeit §= c§, also §r = 2GM/c^2§""", """Escape velocity §= c§, so §r = 2GM/c^2§"""), """§r = GM/c^2§""".replace('§', '$'), """§r = 2GM/c§""".replace('§', '$')),
                        done = """§r_s = \dfrac{2GM}{c^2}§""",
                        hints = listOf(l("Kernidee: Nicht einmal Licht kommt mehr heraus.", "Key idea: not even light can get out."), """§v_\text{Flucht} = \sqrt{2GM/r} = c§"""),
                    ),
                    num(
                        l("Schwarzschild-Radius der Erde", "Schwarzschild radius of the Earth"), rs * 1000, "mm", rel = 0.02,
                        done = l("""§r_s \approx 8{,}9\ \text{mm}§""", """§r_s \approx 8.9\ \text{mm}§"""),
                        hints = listOf(l("Einsetzen, Ergebnis in Metern, dann mal 1000.", "Insert, result in metres, then times 1000."), l("""§r_s = \dfrac{2 \cdot 3{,}99 \cdot 10^{14}}{9{,}00 \cdot 10^{16}}\ \text{m}§""", """§r_s = \dfrac{2 \cdot 3.99 \cdot 10^{14}}{9.00 \cdot 10^{16}}\ \text{m}§""")),
                    ),
                    num(
                        l("Welche Zehnerpotenz hat dann die Dichte in kg/m³?", "What power of ten is the density then, in kg/m³?"), 30.0, "", abs = 0.0,
                        done = """§\rho \approx 2 \cdot 10^{30}\ \text{kg/m}^3§""",
                        hints = listOf("""§V = \tfrac{4}{3}\pi r_s^3 \approx 3 \cdot 10^{-6}\ \text{m}^3§""", """§\rho = M / V§"""),
                    ),
                    choice(
                        l("Wie wächst der Schwarzschild-Radius mit der Masse?", "How does the Schwarzschild radius grow with mass?"),
                        listOf("""§r_s \propto M§""".replace('§', '$'), """§r_s \propto M^{1/3}§""".replace('§', '$'), """§r_s \propto \sqrt{M}§""".replace('§', '$')),
                        done = """§r_s \propto M§""",
                        hints = listOf(l("Schau auf die Formel aus Schritt 1.", "Look at the formula from step 1."), l("Daraus folgt: Die mittlere Dichte fällt mit 1/M².", "It follows that the mean density falls as 1/M².")),
                    ),
                ),
                solution = listOf(
                    """§\sqrt{\dfrac{2GM}{r_s}} = c \;\Rightarrow\; r_s = \dfrac{2GM}{c^2}§""".replace('§', '$'),
                    l("""§r_s = \dfrac{2 \cdot 3{,}99 \cdot 10^{14}}{(3{,}00 \cdot 10^8)^2}\ \text{m} \approx 8{,}9\ \text{mm}§""", """§r_s = \dfrac{2 \cdot 3.99 \cdot 10^{14}}{(3.00 \cdot 10^8)^2}\ \text{m} \approx 8.9\ \text{mm}§"""),
                    l("""§\rho = \dfrac{5{,}97 \cdot 10^{24}\ \text{kg}}{\tfrac{4}{3}\pi\,(8{,}9 \cdot 10^{-3}\ \text{m})^3} \approx 2 \cdot 10^{30}\ \text{kg/m}^3§""", """§\rho = \dfrac{5.97 \cdot 10^{24}\ \text{kg}}{\tfrac{4}{3}\pi\,(8.9 \cdot 10^{-3}\ \text{m})^3} \approx 2 \cdot 10^{30}\ \text{kg/m}^3§"""),
                ),
                background = l(
                    "Ein Schwarzes Loch entsteht, wenn Masse so dicht gepackt ist, dass die Fluchtgeschwindigkeit die Lichtgeschwindigkeit erreicht. Die Grenze heißt Ereignishorizont, ihr Radius Schwarzschild-Radius. Die Herleitung mit Newton liefert zufällig genau die richtige Formel; die ehrliche Rechnung stammt aus Einsteins allgemeiner Relativität (Karl Schwarzschild, 1916). Weil der Radius nur proportional zur Masse wächst, sind riesige Schwarze Löcher im Mittel erstaunlich wenig dicht.",
                    "A black hole forms when mass is packed so densely that the escape velocity reaches the speed of light. The boundary is called the event horizon, its radius the Schwarzschild radius. The Newtonian derivation happens to give exactly the right formula; the honest calculation comes from Einstein's general relativity (Karl Schwarzschild, 1916). Because the radius only grows in proportion to the mass, huge black holes have a surprisingly low average density.",
                ),
                fact = l("Das Schwarze Loch in der Galaxie M87 hätte im Mittel eine geringere Dichte als Luft.", "The black hole in the galaxy M87 would on average be less dense than air."),
            )
        },
        TaskDef("basel", Topic.Calculus, X) {
            Task(
                "basel", Topic.Calculus, X,
                question = l("""Was ergibt die unendliche Summe §1 + \dfrac{1}{4} + \dfrac{1}{9} + \dfrac{1}{16} + \dots = \displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^2}§?""", """What is the infinite sum §1 + \dfrac{1}{4} + \dfrac{1}{9} + \dfrac{1}{16} + \dots = \displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^2}§?"""),
                steps = listOf(
                    choice(
                        l("Hat die Summe überhaupt einen endlichen Wert?", "Does the sum have a finite value at all?"),
                        listOf(l("""Ja, die Glieder werden schnell genug klein: §\sum 1/n^2 < 1 + \int_1^\infty dx/x^2 = 2§""", """Yes, the terms shrink fast enough: §\sum 1/n^2 < 1 + \int_1^\infty dx/x^2 = 2§"""), l("Nein, unendlich viele positive Zahlen ergeben immer unendlich", "No, infinitely many positive numbers always add up to infinity"), l("""Nein, genau wie §1 + \tfrac{1}{2} + \tfrac{1}{3} + \dots§""", """No, just like §1 + \tfrac{1}{2} + \tfrac{1}{3} + \dots§""")),
                        done = l("Ja, kleiner als 2", "Yes, less than 2"),
                        hints = listOf(l("Vergleiche die Summe mit einer Fläche unter einer Kurve.", "Compare the sum with an area under a curve."), l("""Die harmonische Reihe §\sum 1/n§ wächst unbegrenzt, §\sum 1/n^2§ nicht.""", """The harmonic series §\sum 1/n§ grows without bound, §\sum 1/n^2§ doesn't.""")),
                    ),
                    num(
                        l("Ihr Wert auf zwei Nachkommastellen", "Its value to two decimal places"), PI * PI / 6, "", abs = 0.01,
                        done = """§\dfrac{\pi^2}{6} \approx 1{,}645§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("1{,}645", "1.645") },
                        hints = listOf(l("Die ersten Glieder: 1 + 0,25 + 0,111 + 0,0625 + 0,04 + … der Rest wird immer kleiner.", "First terms: 1 + 0.25 + 0.111 + 0.0625 + 0.04 + … the rest keeps shrinking."), l("""Euler fand §\pi^2/6§.""", """Euler found §\pi^2/6§.""")),
                    ),
                    num(
                        l("""Und §\displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^4}§?""", """And §\displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^4}§?"""), PI.pow(4) / 90, "", abs = 0.005,
                        done = """§\dfrac{\pi^4}{90} \approx 1{,}082§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("1{,}082", "1.082") },
                        hints = listOf(l("Hier zählen fast nur die ersten Glieder: 1 + 1/16 + 1/81 + …", "Here almost only the first terms count: 1 + 1/16 + 1/81 + …"), l("""Euler fand auch hier π: §\pi^4/90§.""", """Euler found π here too: §\pi^4/90§.""")),
                    ),
                ),
                solution = listOf(
                    l("""§\displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^2} = \dfrac{\pi^2}{6} \approx 1{,}645, \qquad \displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^4} = \dfrac{\pi^4}{90} \approx 1{,}082§""", """§\displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^2} = \dfrac{\pi^2}{6} \approx 1.645, \qquad \displaystyle\sum_{n=1}^{\infty} \dfrac{1}{n^4} = \dfrac{\pi^4}{90} \approx 1.082§"""),
                ),
                background = l(
                    "Das Basler Problem hielt Mathematiker fast hundert Jahre auf Trab, bis Leonhard Euler 1734 den Wert π²/6 fand. Überraschend ist, dass die Kreiszahl π auftaucht, obwohl in der Summe nirgends ein Kreis vorkommt. Eulers Trick war, die Sinusfunktion wie ein Polynom in ihre Nullstellen zu zerlegen. Später zeigte sich, dass hinter solchen Summen die Riemannsche Zeta-Funktion steckt, eines der tiefsten Objekte der Mathematik.",
                    "The Basel problem kept mathematicians busy for almost a hundred years until Leonhard Euler found the value π²/6 in 1734. Surprisingly, π shows up although there is no circle anywhere in the sum. Euler's trick was to factor the sine function like a polynomial into its zeros. Later it turned out that the Riemann zeta function, one of the deepest objects in mathematics, lies behind such sums.",
                ),
                fact = l("Für ungerade Potenzen wie 1/n³ kennt man bis heute keine so schöne Formel.", "For odd powers like 1/n³, no such nice formula is known to this day."),
            )
        },

        // ===================================== MEISTERKLASSE =====================================
        TaskDef("bell", Topic.Quantum, X, master = true) {
            val e = cos(PI / 4)
            val s = 4 * e
            Task(
                "bell", Topic.Quantum, X, master = true,
                question = l("""Zwei verschränkte Photonen werden an zwei weit entfernten Orten mit Polarisatoren gemessen. Für die Korrelation bei den Winkeln §\alpha§ und §\beta§ gilt §E(\alpha, \beta) = \cos 2(\alpha - \beta)§. Gemessen wird bei §a = 0^\circ,\, a' = 45^\circ§ und §b = 22{,}5^\circ,\, b' = 67{,}5^\circ§.""", """Two entangled photons are measured with polarisers at two distant places. The correlation for angles §\alpha§ and §\beta§ is §E(\alpha, \beta) = \cos 2(\alpha - \beta)§. Measurements use §a = 0^\circ,\, a' = 45^\circ§ and §b = 22.5^\circ,\, b' = 67.5^\circ§."""),
                given = listOf("""§S = E(a,b) - E(a,b') + E(a',b) + E(a',b')§""".replace('§', '$')),
                steps = listOf(
                    num("""§E(a, b)§""".replace('§', '$'), e, "", abs = 0.01, done = """§E(a,b) \approx 0{,}707§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("0{,}707", "0.707") },
                        hints = listOf("""§2(0^\circ - 22{,}5^\circ) = -45^\circ§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("22{,}5", "22.5") }, """§\cos 45^\circ = \tfrac{1}{\sqrt{2}}§""".replace('§', '$'))),
                    num("""§E(a, b')§""".replace('§', '$'), -e, "", abs = 0.01, done = """§E(a,b') \approx -0{,}707§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("0{,}707", "0.707") },
                        hints = listOf("""§2(0^\circ - 67{,}5^\circ) = -135^\circ§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("67{,}5", "67.5") }, """§\cos 135^\circ = -\tfrac{1}{\sqrt{2}}§""".replace('§', '$'))),
                    num(l("""Wie groß ist §S§?""", """What is §S§?"""), s, "", abs = 0.02, done = """§S = 2\sqrt{2} \approx 2{,}83§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("2{,}83", "2.83") },
                        hints = listOf(l("""Die beiden übrigen Terme sind ebenfalls §\cos(\pm 45^\circ)§.""", """The two remaining terms are also §\cos(\pm 45^\circ)§."""), """§S = 0{,}707 + 0{,}707 + 0{,}707 + 0{,}707§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("0{,}707", "0.707") })),
                    choice(
                        l("""Was bedeutet §S > 2§?""", """What does §S > 2§ mean?"""),
                        listOf(l("Keine Theorie mit lokalen verborgenen Variablen kann diese Korrelationen erklären", "No theory with local hidden variables can explain these correlations"), l("Die Photonen tauschen Signale mit Überlichtgeschwindigkeit aus", "The photons exchange signals faster than light"), l("Die Messung war fehlerhaft", "The measurement was faulty")),
                        done = l("Lokaler Realismus ist widerlegt", "Local realism is ruled out"),
                        hints = listOf(l("""Kernidee: Für jede lokale Theorie mit vorher festgelegten Messergebnissen gilt §|S| \le 2§ (CHSH-Ungleichung).""", """Key idea: any local theory with predetermined outcomes obeys §|S| \le 2§ (CHSH inequality)."""), l("Mit Verschränkung lassen sich keine Nachrichten senden. Es sind Korrelationen, keine Signale.", "Entanglement can't be used to send messages. These are correlations, not signals.")),
                    ),
                ),
                solution = listOf(
                    l("""§S = \cos(-45^\circ) - \cos(-135^\circ) + \cos(45^\circ) + \cos(-45^\circ) = 4 \cdot \tfrac{1}{\sqrt{2}} = 2\sqrt{2} \approx 2{,}83 > 2§""", """§S = \cos(-45^\circ) - \cos(-135^\circ) + \cos(45^\circ) + \cos(-45^\circ) = 4 \cdot \tfrac{1}{\sqrt{2}} = 2\sqrt{2} \approx 2.83 > 2§"""),
                ),
                background = l(
                    "Einstein hielt die Quantenmechanik für unvollständig: Vielleicht tragen die Teilchen ihre Messergebnisse heimlich schon von Anfang an mit sich, als verborgene Variablen. John Bell zeigte 1964, dass man das testen kann. Jede solche lokale Theorie muss eine Ungleichung erfüllen, in der CHSH-Form |S| ≤ 2. Die Quantenmechanik sagt für geschickt gewählte Winkel 2√2 voraus, und genau das wird gemessen. Die Natur ist also nicht lokal-realistisch, aber man kann mit Verschränkung trotzdem keine Nachrichten schneller als Licht senden.",
                    "Einstein thought quantum mechanics was incomplete: maybe particles secretly carry their measurement results from the start, as hidden variables. In 1964 John Bell showed this can be tested. Every such local theory must satisfy an inequality, in CHSH form |S| ≤ 2. Quantum mechanics predicts 2√2 for cleverly chosen angles, and that is exactly what is measured. Nature is therefore not locally realistic, yet entanglement still can't send messages faster than light.",
                ),
                fact = l("2√2 ist das Maximum, das die Quantenmechanik erlaubt (Tsirelson-Schranke). Für die Experimente gab es 2022 den Physik-Nobelpreis an Aspect, Clauser und Zeilinger.", "2√2 is the maximum quantum mechanics allows (Tsirelson's bound). The experiments won the 2022 Nobel Prize in Physics for Aspect, Clauser and Zeilinger."),
            )
        },
        TaskDef("hawking", Topic.Astro, X, master = true) {
            val hbar = 1.055e-34
            val c = 3.00e8
            val gc = 6.674e-11
            val kb = 1.381e-23
            val msun = 1.989e30
            val t = hbar * c * c * c / (8 * PI * gc * msun * kb)
            val mMoon = msun * t / 2.7 / 7.35e22
            Task(
                "hawking", Topic.Astro, X, master = true,
                question = l("""Schwarze Löcher sind nicht ganz schwarz: Sie strahlen wie ein Körper mit der Temperatur §T_H = \dfrac{\hbar c^3}{8\pi G M k_B}§.""", """Black holes aren't completely black: they radiate like a body at temperature §T_H = \dfrac{\hbar c^3}{8\pi G M k_B}§."""),
                given = listOf(
                    l("""§\hbar = 1{,}055 \cdot 10^{-34}\ \text{J s},\; G = 6{,}674 \cdot 10^{-11}\ \text{m}^3/(\text{kg}\, \text{s}^2)§""", """§\hbar = 1.055 \cdot 10^{-34}\ \text{J s},\; G = 6.674 \cdot 10^{-11}\ \text{m}^3/(\text{kg}\, \text{s}^2)§"""),
                    l("""§k_B = 1{,}381 \cdot 10^{-23}\ \text{J/K},\; M_\odot = 1{,}989 \cdot 10^{30}\ \text{kg},\; c = 3{,}00 \cdot 10^8\ \text{m/s}§""", """§k_B = 1.381 \cdot 10^{-23}\ \text{J/K},\; M_\odot = 1.989 \cdot 10^{30}\ \text{kg},\; c = 3.00 \cdot 10^8\ \text{m/s}§"""),
                ),
                steps = listOf(
                    choice(
                        l("Wie hängt die Temperatur von der Masse ab?", "How does the temperature depend on mass?"),
                        listOf("""§T \propto 1/M§""".replace('§', '$'), """§T \propto M§""".replace('§', '$'), """§T \propto M^2§""".replace('§', '$')),
                        done = l("Kleine Löcher sind heißer", "Small holes are hotter"),
                        hints = listOf(l("Schau, wo M in der Formel steht.", "Look where M sits in the formula."), l("Je mehr Masse, desto kälter.", "The more mass, the colder.")),
                    ),
                    num(
                        l("Wie warm ist ein Schwarzes Loch mit einer Sonnenmasse? (in Nanokelvin)", "How warm is a black hole of one solar mass? (in nanokelvin)"), t * 1e9, "nK", rel = 0.03,
                        done = """§T_H \approx 62\ \text{nK}§""",
                        hints = listOf(l("""Zähler: §\hbar c^3 \approx 2{,}85 \cdot 10^{-9}§""", """Numerator: §\hbar c^3 \approx 2.85 \cdot 10^{-9}§"""), l("""Nenner: §8\pi G M_\odot k_B \approx 4{,}6 \cdot 10^{-2}§""", """Denominator: §8\pi G M_\odot k_B \approx 4.6 \cdot 10^{-2}§""")),
                    ),
                    choice(
                        l("Verdampft so ein Schwarzes Loch heute?", "Is such a black hole evaporating today?"),
                        listOf(l("Nein, das All ist mit 2,7 K wärmer: Es nimmt mehr Strahlung auf, als es abgibt", "No, the universe at 2.7 K is warmer: it absorbs more radiation than it emits"), l("Ja, langsam", "Yes, slowly"), l("Ja, sehr schnell", "Yes, very quickly")),
                        done = l("Nein, es wächst sogar", "No, it even grows"),
                        hints = listOf(l("Vergleiche mit der kosmischen Hintergrundstrahlung.", "Compare with the cosmic microwave background."), l("Wärme fließt vom wärmeren zum kälteren Körper.", "Heat flows from the warmer to the colder body.")),
                    ),
                    num(
                        l("Wie schwer müsste es sein, um genau 2,7 K warm zu sein? (in Mondmassen, 1 Mondmasse = 7,35·10²² kg)", "How heavy would it have to be to be exactly 2.7 K? (in Moon masses, 1 Moon mass = 7.35·10²² kg)"), mMoon, "", rel = 0.04,
                        done = l("""§\approx 0{,}6§ Mondmassen""", """§\approx 0.6§ Moon masses"""),
                        hints = listOf("""§M = M_\odot \cdot \dfrac{T_H(M_\odot)}{2{,}7\ \text{K}}§""".let { if (de.funke.app.ui.Lang.german) it.replace('§', '$') else it.replace('§', '$').replace("2{,}7", "2.7") }, l("""Ergebnis in kg durch §7{,}35 \cdot 10^{22}§ teilen.""", """Divide the result in kg by §7.35 \cdot 10^{22}§.""")),
                    ),
                ),
                solution = listOf(
                    l("""§T_H = \dfrac{1{,}055 \cdot 10^{-34} \cdot (3{,}00 \cdot 10^8)^3}{8\pi \cdot 6{,}674 \cdot 10^{-11} \cdot 1{,}989 \cdot 10^{30} \cdot 1{,}381 \cdot 10^{-23}}\ \text{K} \approx 6{,}2 \cdot 10^{-8}\ \text{K}§""", """§T_H = \dfrac{1.055 \cdot 10^{-34} \cdot (3.00 \cdot 10^8)^3}{8\pi \cdot 6.674 \cdot 10^{-11} \cdot 1.989 \cdot 10^{30} \cdot 1.381 \cdot 10^{-23}}\ \text{K} \approx 6.2 \cdot 10^{-8}\ \text{K}§"""),
                    l("""§M = M_\odot \cdot \dfrac{6{,}2 \cdot 10^{-8}}{2{,}7} \approx 4{,}6 \cdot 10^{22}\ \text{kg} \approx 0{,}6\ M_\text{Mond}§""", """§M = M_\odot \cdot \dfrac{6.2 \cdot 10^{-8}}{2.7} \approx 4.6 \cdot 10^{22}\ \text{kg} \approx 0.6\ M_\text{Moon}§"""),
                ),
                background = l(
                    "1974 verband Stephen Hawking Quantenfeldtheorie und allgemeine Relativität: Am Ereignishorizont entstehen ständig Paare virtueller Teilchen, und gelegentlich entkommt eines. Das Schwarze Loch strahlt dadurch schwach wie ein heißer Körper und verliert Masse. In der Formel stecken alle großen Naturkonstanten zusammen: ħ aus der Quantenmechanik, c aus der Relativität, G aus der Gravitation und k_B aus der Thermodynamik.",
                    "In 1974 Stephen Hawking combined quantum field theory and general relativity: pairs of virtual particles constantly form at the event horizon and occasionally one escapes. The black hole therefore glows faintly like a hot body and loses mass. The formula contains all the great constants of nature together: ħ from quantum mechanics, c from relativity, G from gravitation and k_B from thermodynamics.",
                ),
                fact = l("Hawking-Strahlung wurde noch nie direkt gemessen. Trotzdem gilt sie als eine der wichtigsten Brücken zwischen Quantenphysik und Gravitation.", "Hawking radiation has never been measured directly. Still it is considered one of the most important bridges between quantum physics and gravity."),
            )
        },
        TaskDef("feynman", Topic.Modern, X, master = true) {
            Task(
                "feynman", Topic.Modern, X, master = true,
                question = l("""In der Quantenelektrodynamik (QED) zeichnet man Wechselwirkungen als Feynman-Diagramme. Jeder Knoten (Vertex), an dem ein Photon an ein geladenes Teilchen koppelt, bringt einen Faktor §\sqrt{\alpha}§ in die Amplitude, mit §\alpha \approx 1/137§.""", """In quantum electrodynamics (QED), interactions are drawn as Feynman diagrams. Every node (vertex) where a photon couples to a charged particle contributes a factor §\sqrt{\alpha}§ to the amplitude, with §\alpha \approx 1/137§."""),
                steps = listOf(
                    choice(
                        l("Welcher Vertex ist in der QED erlaubt?", "Which vertex is allowed in QED?"),
                        listOf(l("Eine Elektronlinie hinein, eine hinaus, ein Photon", "One electron line in, one out, one photon"), l("Drei Photonen", "Three photons"), l("Zwei Photonen und nichts sonst", "Two photons and nothing else")),
                        done = l("Elektron – Elektron – Photon", "Electron – electron – photon"),
                        hints = listOf(l("Kernidee: Photonen koppeln an Ladung, nicht an andere Photonen.", "Key idea: photons couple to charge, not to other photons."), l("Ein Photon selbst hat keine Ladung.", "A photon itself has no charge.")),
                    ),
                    num(
                        l("Wie viele Vertices hat das einfachste Diagramm für die Streuung zweier Elektronen?", "How many vertices does the simplest diagram for two electrons scattering have?"), 2.0, "", abs = 0.0,
                        done = l("2 Vertices", "2 vertices"),
                        hints = listOf(l("Die Elektronen tauschen ein einziges Photon aus.", "The electrons exchange a single photon."), l("Jedes Elektron hat einen Knoten, an dem das Photon andockt.", "Each electron has one node where the photon attaches.")),
                    ),
                    num(
                        l("Ein Diagramm mit einer Schleife hat zwei Vertices mehr. Um welchen Faktor ist seine Amplitude ungefähr kleiner?", "A one-loop diagram has two more vertices. By roughly what factor is its amplitude smaller?"), 137.0, "", rel = 0.03,
                        done = l("""etwa §1/\alpha \approx 137§""", """about §1/\alpha \approx 137§"""),
                        hints = listOf(l("""Zwei zusätzliche Faktoren §\sqrt{\alpha}§.""", """Two extra factors of §\sqrt{\alpha}§."""), """§\sqrt{\alpha} \cdot \sqrt{\alpha} = \alpha§"""),
                    ),
                    choice(
                        l("Warum klappt diese Reihenentwicklung bei der starken Kraft bei niedrigen Energien nicht?", "Why does this series expansion fail for the strong force at low energies?"),
                        listOf(l("Dort ist die Kopplung von der Größenordnung 1, höhere Diagramme sind nicht kleiner", "There the coupling is of order 1, so higher diagrams aren't smaller"), l("Gluonen haben eine große Masse", "Gluons have a large mass"), l("Bei der starken Kraft gibt es keine Vertices", "The strong force has no vertices")),
                        done = l("Die Kopplung ist zu groß", "The coupling is too large"),
                        hints = listOf(l("Die Methode lebt davon, dass jeder weitere Vertex die Amplitude stark verkleinert.", "The method relies on each extra vertex making the amplitude much smaller."), l("Bei hohen Energien wird die starke Kopplung klein (asymptotische Freiheit), bei niedrigen nicht.", "At high energies the strong coupling becomes small (asymptotic freedom), at low energies it doesn't.")),
                    ),
                ),
                solution = listOf(
                    l("""Einfachstes Diagramm: 2 Vertices §\Rightarrow\; \mathcal{A} \propto \alpha§. Mit einer Schleife: 4 Vertices §\Rightarrow\; \mathcal{A} \propto \alpha^2§, also etwa §137§-mal kleiner.""", """Simplest diagram: 2 vertices §\Rightarrow\; \mathcal{A} \propto \alpha§. With one loop: 4 vertices §\Rightarrow\; \mathcal{A} \propto \alpha^2§, so about §137§ times smaller."""),
                ),
                background = l(
                    "Feynman-Diagramme sind eine Buchhaltung für Quantenprozesse: Jede Linie steht für ein Teilchen, jeder Knoten für eine Wechselwirkung. Die Wahrscheinlichkeit eines Prozesses ergibt sich aus der Summe aller möglichen Diagramme. Weil α so klein ist, reichen in der QED meist die einfachsten. Bei der starken Kraft ist die Kopplung bei niedrigen Energien groß, dort braucht man andere Methoden wie Gitterrechnungen auf Supercomputern.",
                    "Feynman diagrams are bookkeeping for quantum processes: every line stands for a particle, every node for an interaction. The probability of a process comes from the sum of all possible diagrams. Because α is so small, the simplest ones are usually enough in QED. For the strong force the coupling is large at low energies; there other methods are needed, such as lattice calculations on supercomputers.",
                ),
                fact = l("Mit solchen Diagrammen sagt die QED das magnetische Moment des Elektrons auf mehr als zehn Stellen genau voraus. Es ist eine der genauesten Vorhersagen der Physik.", "With such diagrams, QED predicts the electron's magnetic moment to more than ten digits. It is one of the most precise predictions in physics."),
            )
        },
    )
}

