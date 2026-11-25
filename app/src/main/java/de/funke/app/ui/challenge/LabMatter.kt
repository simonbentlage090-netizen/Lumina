package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.exp
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =============================================================================================
// Vorlagen: Druck & Dichte, Wärme, Schall & Wellen, Licht & Optik (seit 10.1).
// =============================================================================================

private const val G = 9.81
private const val C_LIGHT = 2.998e8
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private object Bg4 {
    val pressure get() = l(
        "Druck ist Kraft pro Fläche. Dieselbe Kraft drückt auf einer kleinen Fläche viel stärker – deshalb sinkt man mit Stöckelschuhen im Rasen ein, mit Schneeschuhen aber nicht im Schnee. Die Einheit Pascal ist winzig: 1 Pa ist 1 Newton auf einem Quadratmeter. Die Luft drückt mit etwa 100.000 Pa auf uns – wir merken es nur nicht, weil der Druck in uns genauso groß ist.",
        "Pressure is force per area. The same force presses much harder on a small area – that's why stilettos sink into a lawn while snowshoes don't sink into snow. The unit pascal is tiny: 1 Pa is 1 newton on one square metre. The air presses on us with about 100,000 Pa – we just don't notice because the pressure inside us is the same.",
    )
    val hydro get() = l(
        "In Flüssigkeiten wächst der Druck mit der Tiefe, weil das Gewicht der Wassersäule darüber drückt: Alle 10 Meter Wasser kommt ungefähr ein ganzer Luftdruck dazu. Die Form des Gefäßes spielt keine Rolle, nur die Tiefe. Deshalb sind Staumauern unten dicker als oben, und deshalb muss man beim Tauchen den Druck in den Ohren ausgleichen.",
        "In liquids the pressure grows with depth because the weight of the water above presses down: every 10 metres of water adds roughly one atmosphere. The shape of the container doesn't matter, only the depth. That's why dams are thicker at the bottom, and why divers have to equalise the pressure in their ears.",
    )
    val buoyancy get() = l(
        "Auftrieb entsteht, weil der Wasserdruck unten an einem Körper größer ist als oben. Er ist genau so groß wie das Gewicht der verdrängten Flüssigkeit (Archimedes). Ist ein Körper weniger dicht als das Wasser, schwimmt er – und taucht gerade so tief ein, dass er sein eigenes Gewicht an Wasser verdrängt. Bei einem Eisberg sind das fast 90 %.",
        "Buoyancy arises because water pressure is greater at the bottom of an object than at its top. It equals the weight of the displaced liquid (Archimedes). If an object is less dense than water it floats – and sinks just deep enough to displace its own weight of water. For an iceberg that's almost 90%.",
    )
    val hydraulics get() = l(
        "In einer eingeschlossenen Flüssigkeit breitet sich Druck überall gleich aus. Drückt man auf einen kleinen Kolben, wirkt derselbe Druck auf einen großen – mit entsprechend größerer Kraft. So hebt eine Hebebühne ein Auto und so bremst ein Auto mit einem leichten Tritt aufs Pedal. Geschenkt wird nichts: Der kleine Kolben muss einen viel längeren Weg gehen.",
        "In an enclosed liquid, pressure spreads equally everywhere. Push on a small piston and the same pressure acts on a large one – with a correspondingly larger force. That's how a car lift raises a car and how a gentle tap on the pedal brakes it. Nothing comes for free: the small piston has to travel a much longer distance.",
    )
    val atmosphere get() = l(
        "Die Luft wird nach oben hin dünner, weil sie zusammendrückbar ist: Unten drückt die ganze Luftsäule darüber, oben fast nichts mehr. Der Druck nimmt ungefähr exponentiell ab – alle 5,5 km halbiert er sich etwa. Auf dem Mount Everest ist nur noch ein Drittel des Luftdrucks übrig, deshalb fehlt Bergsteigern dort der Sauerstoff.",
        "Air gets thinner with height because it can be compressed: at the bottom the whole column of air above presses down, at the top almost nothing. Pressure drops roughly exponentially – about halving every 5.5 km. On top of Mount Everest only a third of the air pressure is left, which is why climbers run short of oxygen there.",
    )
    val heat get() = l(
        "Wärme ist Energie, die wegen eines Temperaturunterschieds fließt. Wie viel man braucht, um etwas zu erwärmen, hängt vom Stoff ab: Wasser hat eine riesige Wärmekapazität (4,19 kJ pro kg und Grad). Deshalb dauert Nudelwasser so lange, deshalb gleichen Meere das Klima aus, und deshalb ist eine Wärmflasche mit Wasser gefüllt.",
        "Heat is energy that flows because of a temperature difference. How much it takes to warm something depends on the material: water has a huge heat capacity (4.19 kJ per kg and degree). That's why pasta water takes so long, why oceans moderate the climate, and why hot-water bottles are filled with water.",
    )
    val latent get() = l(
        "Beim Schmelzen und Verdampfen steckt man Energie hinein, ohne dass die Temperatur steigt: Sie wird gebraucht, um die Bindungen zwischen den Teilchen zu lösen. Eis schmilzt bei 0 °C und braucht dafür so viel Energie, wie man bräuchte, um das Wasser danach auf 80 °C zu erwärmen. Deshalb kühlen Eiswürfel ein Getränk so gut.",
        "Melting and boiling take in energy without raising the temperature: it is needed to break the bonds between particles. Ice melts at 0 °C and needs as much energy for that as it would take to warm the water afterwards to 80 °C. That's why ice cubes cool a drink so well.",
    )
    val gas get() = l(
        "In einem Gas fliegen Moleküle kreuz und quer und prallen gegen die Wände – das ist der Druck. Je heißer, desto schneller die Moleküle, desto größer der Druck. Rechnen muss man dabei in Kelvin, der Temperatur ab dem absoluten Nullpunkt (−273,15 °C): Bei doppelter Kelvin-Temperatur verdoppelt sich der Druck in einem festen Behälter.",
        "In a gas, molecules fly around and bounce off the walls – that's pressure. The hotter, the faster the molecules, the higher the pressure. You must calculate in kelvin, the temperature from absolute zero (−273.15 °C): doubling the kelvin temperature doubles the pressure in a rigid container.",
    )
    val engines get() = l(
        "Eine Wärmekraftmaschine wandelt einen Teil der Wärme, die von heiß nach kalt fließt, in Arbeit um. Den Rest muss sie an die kalte Seite abgeben – das verlangt der zweite Hauptsatz der Thermodynamik. Selbst eine perfekte Maschine (Carnot) schafft nur 1 − T_kalt/T_heiß. Deshalb arbeiten Kraftwerke mit möglichst heißem Dampf.",
        "A heat engine turns part of the heat flowing from hot to cold into work. The rest has to be dumped on the cold side – the second law of thermodynamics demands it. Even a perfect engine (Carnot) only manages 1 − T_cold/T_hot. That's why power plants use steam as hot as possible.",
    )
    val radiation get() = l(
        "Jeder Körper strahlt Wärme ab, und zwar mit der vierten Potenz seiner Temperatur (Stefan-Boltzmann). Die Erde bekommt Strahlung von der Sonne und strahlt selbst im Infraroten ab; im Gleichgewicht ergibt das ohne Atmosphäre etwa −18 °C. Dass es im Mittel +15 °C sind, liegt am natürlichen Treibhauseffekt.",
        "Every object radiates heat, proportional to the fourth power of its temperature (Stefan–Boltzmann). The Earth receives radiation from the Sun and radiates in the infrared itself; in equilibrium, without an atmosphere, that gives about −18 °C. That the average is +15 °C is due to the natural greenhouse effect.",
    )
    val sound get() = l(
        "Schall ist eine Druckwelle: Luftteilchen schwingen hin und her und stoßen ihre Nachbarn an. Die Frequenz bestimmt die Tonhöhe, die Wellenlänge ist Schallgeschwindigkeit geteilt durch Frequenz. Tiefe Töne haben Wellenlängen von mehreren Metern – deshalb hört man den Bass der Nachbarn durch die Wand, die hohen Töne aber nicht.",
        "Sound is a pressure wave: air particles move back and forth and bump into their neighbours. The frequency sets the pitch, the wavelength is the speed of sound divided by the frequency. Low notes have wavelengths of several metres – that's why you hear your neighbours' bass through the wall but not the high notes.",
    )
    val decibel get() = l(
        "Unser Gehör funktioniert logarithmisch: Zehnmal so viel Schallleistung klingt nur etwa doppelt so laut. Deshalb misst man in Dezibel: +10 dB bedeutet zehnfache Intensität, +20 dB hundertfache. Ab etwa 85 dB über längere Zeit nimmt das Gehör Schaden – ein Konzert hat oft über 100 dB.",
        "Our hearing works logarithmically: ten times the sound power only sounds about twice as loud. That's why we measure in decibels: +10 dB means ten times the intensity, +20 dB a hundred times. Above about 85 dB for longer periods hearing gets damaged – a concert is often over 100 dB.",
    )
    val strings get() = l(
        "Eine Saite schwingt in stehenden Wellen: An den Enden ist sie fest, dazwischen passt bei der Grundschwingung genau eine halbe Wellenlänge. Kürzt man die Saite (Finger aufs Griffbrett), wird der Ton höher; spannt man sie stärker, läuft die Welle schneller und der Ton steigt ebenfalls. Obertöne mit doppelter, dreifacher … Frequenz geben jedem Instrument seinen Klang.",
        "A string vibrates in standing waves: it is fixed at both ends, and the fundamental fits exactly half a wavelength in between. Shorten the string (finger on the fretboard) and the note rises; tighten it and the wave travels faster, raising the note too. Overtones at twice, three times … the frequency give every instrument its sound.",
    )
    val light get() = l(
        "Licht ist mit knapp 300.000 km/s das Schnellste, was es gibt – aber nicht unendlich schnell. Vom Mond braucht es gut eine Sekunde, von der Sonne gut acht Minuten. Wenn wir in den Himmel schauen, sehen wir deshalb immer in die Vergangenheit: die Sonne, wie sie vor acht Minuten war, ferne Galaxien, wie sie vor Millionen Jahren waren.",
        "Light, at almost 300,000 km/s, is the fastest thing there is – but not infinitely fast. It takes just over a second from the Moon and just over eight minutes from the Sun. So whenever we look at the sky we look into the past: the Sun as it was eight minutes ago, distant galaxies as they were millions of years ago.",
    )
    val refraction get() = l(
        "Licht ist in Wasser und Glas langsamer als in Luft. Trifft es schräg auf die Grenze, knickt es deshalb ab – zum Lot hin, wenn es ins langsamere Medium kommt. Darum sieht ein Strohhalm im Glas geknickt aus und darum wirkt ein Pool flacher, als er ist. Der Brechungsindex n sagt, wie viel langsamer das Licht im Stoff ist.",
        "Light is slower in water and glass than in air. When it hits the boundary at an angle it bends – towards the normal when it enters the slower medium. That's why a straw looks bent in a glass and why a pool looks shallower than it is. The refractive index n tells you how much slower light is in the material.",
    )
    val lens get() = l(
        "Eine Sammellinse lenkt parallele Strahlen in ihren Brennpunkt. Steht ein Gegenstand weiter weg als die Brennweite, entsteht auf der anderen Seite ein umgedrehtes, echtes Bild – so arbeiten Kamera und Auge. Steht er näher als die Brennweite, sieht man ein vergrößertes, aufrechtes Bild: die Lupe.",
        "A converging lens bends parallel rays into its focal point. If an object is farther away than the focal length, a real, upside-down image appears on the other side – that's how cameras and eyes work. If it's closer than the focal length, you see an enlarged, upright image: the magnifying glass.",
    )
    val tir get() = l(
        "Trifft Licht aus einem dichteren Medium sehr flach auf die Grenze zu einem dünneren, kann es gar nicht mehr austreten: Es wird vollständig zurückgespiegelt – Totalreflexion. In Glasfasern läuft Licht so zigtausendfach im Zickzack über tausende Kilometer. Auch das Funkeln von Diamanten beruht darauf.",
        "When light inside a denser medium meets the boundary with a thinner one at a very shallow angle, it can't get out at all: it is reflected completely – total internal reflection. In optical fibres light zigzags this way tens of thousands of times over thousands of kilometres. The sparkle of diamonds relies on it too.",
    )
    val interference get() = l(
        "Licht ist eine Welle. Gehen zwei Wellen durch eng benachbarte Spalte, überlagern sie sich: Wo Berg auf Berg trifft, wird es hell, wo Berg auf Tal trifft, dunkel. Aus dem Abstand der hellen Streifen kann man die Wellenlänge des Lichts bestimmen – so hat Thomas Young um 1801 gezeigt, dass Licht eine Welle ist. Die Regenbogenfarben auf einer CD entstehen genauso.",
        "Light is a wave. When two waves pass through closely spaced slits, they overlap: where crest meets crest it gets bright, where crest meets trough it gets dark. From the spacing of the bright fringes you can measure the wavelength of light – that's how Thomas Young showed around 1801 that light is a wave. The rainbow colours on a CD arise the same way.",
    )
    val resolution get() = l(
        "Jede Öffnung beugt das Licht ein wenig. Deshalb kann kein Auge und kein Teleskop beliebig feine Details trennen: Der kleinste Winkel ist etwa 1,22 · Wellenlänge / Öffnung. Große Teleskope sehen schärfer, weil ihre Spiegel größer sind – und Radioteleskope verbinden sich über Kontinente zu einem virtuellen Riesenteleskop.",
        "Every aperture diffracts light a little. That's why no eye and no telescope can separate arbitrarily fine details: the smallest angle is about 1.22 · wavelength / aperture. Big telescopes see more sharply because their mirrors are bigger – and radio telescopes link up across continents into one virtual giant telescope.",
    )
}

private object Fc4 {
    val pressure get() = card(l("Druck", "Pressure"), """§p = \dfrac{F}{A}§""", l("1 Pa = 1 N/m², 1 bar = 100.000 Pa.", "1 Pa = 1 N/m², 1 bar = 100,000 Pa."))
    val hydro get() = card(l("Schweredruck", "Hydrostatic pressure"), """§p = \rho g h§""", l("Druck durch eine Flüssigkeitssäule.", "Pressure due to a column of liquid."))
    val archimedes get() = card(l("Auftrieb", "Buoyancy"), l("""§F_A = \rho_{Fl}\, g\, V_{verdr}§""", """§F_B = \rho_{liq}\, g\, V_{disp}§"""), l("Gewicht der verdrängten Flüssigkeit.", "Weight of the displaced liquid."))
    val heat get() = card(l("Wärmemenge", "Heat"), """§Q = c\, m\, \Delta T§""", l("Wasser: c = 4,19 kJ/(kg·K).", "Water: c = 4.19 kJ/(kg·K)."))
    val latent get() = card(l("Schmelzwärme", "Latent heat of fusion"), """§Q = m \cdot L_s§""", l("Eis: L = 334 kJ/kg.", "Ice: L = 334 kJ/kg."))
    val gas get() = card(l("Gas bei festem Volumen", "Gas at fixed volume"), """§\dfrac{p_1}{T_1} = \dfrac{p_2}{T_2}§""", l("Temperaturen in Kelvin!", "Temperatures in kelvin!"))
    val carnot get() = card(l("Carnot-Wirkungsgrad", "Carnot efficiency"), l("""§\eta = 1 - \dfrac{T_k}{T_h}§""", """§\eta = 1 - \dfrac{T_c}{T_h}§"""), l("Obergrenze für jede Wärmekraftmaschine.", "Upper limit for any heat engine."))
    val wave get() = card(l("Wellengleichung", "Wave equation"), """§c = \lambda \cdot f§""", l("Geschwindigkeit = Wellenlänge mal Frequenz.", "Speed = wavelength times frequency."))
    val db get() = card(l("Dezibel", "Decibels"), """§\dfrac{I_2}{I_1} = 10^{(L_2 - L_1)/10}§""", l("+10 dB = zehnfache Intensität.", "+10 dB = ten times the intensity."))
    val snell get() = card(l("Brechungsgesetz", "Snell's law"), """§n_1 \sin\alpha_1 = n_2 \sin\alpha_2§""", l("Winkel zum Lot gemessen.", "Angles measured from the normal."))
    val lens get() = card(l("Linsengleichung", "Lens equation"), """§\dfrac{1}{f} = \dfrac{1}{g} + \dfrac{1}{b}§""", l("g = Gegenstandsweite, b = Bildweite.", "g = object distance, b = image distance."))
    val grating get() = card(l("Gitter", "Grating"), """§d \sin\alpha_k = k \lambda§""", l("d = Abstand der Spalte.", "d = slit spacing."))
}

object LabMatter {
    val all: List<TaskDef> get() = pressure + heat + waves + optics

    // ===================================== DRUCK & DICHTE =====================================
    private val pressure = listOf(
        lab("stoeckel", Topic.Matter, E, 0.3f) {
            val mass = tiered(listOf(50.0, 60.0), 45.0, 80.0, 1.0)
            val aCm = tiered(listOf(1.0, 0.5), 0.4, 1.5, 0.1)
            val p = mass * G / (aCm * 1e-4)
            val eleP = 5000 * G / (4 * 0.12) // Elefant: 4 Füße à 0,12 m²
            task(
                question = l("Eine Frau (${n(mass, 0)} kg) steht für einen Moment ganz auf einem Stöckelabsatz mit ${n(aCm)} cm² Fläche. Wie groß ist der Druck auf den Boden?", "A woman (${n(mass, 0)} kg) briefly puts her whole weight on one stiletto heel with an area of ${n(aCm)} cm². What pressure does it put on the floor?"),
                steps = listOf(num(l("Druck in bar", "Pressure in bar"), p / 1e5, "bar", rel = 0.03, done = """§p \approx ${m(sig(p / 1e5))}\ \text{bar}§""",
                    hints = listOf(l("Kernidee: Druck = Kraft durch Fläche; cm² in m² umrechnen (· 10⁻⁴).", "Key idea: pressure = force over area; convert cm² into m² (· 10⁻⁴)."), """§p = \dfrac{m g}{A}§"""), formula = Fc4.pressure)),
                solution = listOf("""§p = \dfrac{${m(mass, 0)} \cdot 9{,}81\ \text{N}}{${sci(aCm * 1e-4)}\ \text{m}^2} \approx ${sci(p)}\ \text{Pa} \approx ${m(sig(p / 1e5))}\ \text{bar}§"""),
                background = Bg4.pressure,
                fact = l("Ein Elefant (5 t) drückt mit seinen vier großen Füßen nur mit etwa ${n(sig(eleP / 1e5, 2))} bar – der Stöckelschuh ist viel gefährlicher für Parkett.", "An elephant (5 t) presses with its four big feet at only about ${n(sig(eleP / 1e5, 2))} bar – the stiletto is far more dangerous for wooden floors."),
            )
        },
        lab("tauchen", Topic.Matter, M, 0.3f) {
            val depth = tiered(listOf(10.0, 20.0, 30.0), 5.0, 60.0, 1.0)
            val salt = hard
            val rho = if (salt) 1025.0 else 1000.0
            val p = 1.013e5 + rho * G * depth
            task(
                question = l("Welcher Gesamtdruck herrscht in ${n(depth, 0)} m Tiefe in einem ${if (salt) "Meer (Dichte 1025 kg/m³)" else "See"}? Luftdruck an der Oberfläche: 1,013 bar.", "What is the total pressure at a depth of ${n(depth, 0)} m in ${if (salt) "the sea (density 1025 kg/m³)" else "a lake"}? Air pressure at the surface: 1.013 bar."),
                steps = listOf(num(l("Gesamtdruck in bar", "Total pressure in bar"), p / 1e5, "bar", rel = 0.02, done = """§p \approx ${m(sig(p / 1e5))}\ \text{bar}§""",
                    hints = listOf(l("Kernidee: Luftdruck plus Schweredruck des Wassers.", "Key idea: air pressure plus the hydrostatic pressure of the water."), """§p = p_0 + \rho g h§"""), formula = Fc4.hydro)),
                solution = listOf("""§p = 1{,}013 \cdot 10^5\ \text{Pa} + ${m(rho, 0)} \cdot 9{,}81 \cdot ${m(depth, 0)}\ \text{Pa} \approx ${sci(p)}\ \text{Pa} \approx ${m(sig(p / 1e5))}\ \text{bar}§"""),
                background = Bg4.hydro,
            )
        },
        lab("eisberg", Topic.Matter, M, 0.4f) {
            val (what, rho) = pick(l("Eis (917 kg/m³) in Meerwasser (1025 kg/m³)", "ice (917 kg/m³) in seawater (1025 kg/m³)") to 917.0 / 1025.0, l("Eichenholz (750 kg/m³) in Süßwasser", "oak (750 kg/m³) in fresh water") to 0.75, l("ein Mensch (985 kg/m³) mit voller Lunge in Süßwasser", "a person (985 kg/m³) with full lungs in fresh water") to 0.985)
            val above = (1 - rho) * 100
            task(
                question = l("Welcher Anteil des Volumens schaut beim Schwimmen aus dem Wasser? Es geht um $what.", "What fraction of the volume sticks out of the water when floating? We're talking about $what."),
                steps = listOf(num(l("Anteil über Wasser in Prozent", "Fraction above water in percent"), above, "%", abs = 1.0, done = """§\approx ${m(above, 1)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Schwimmend verdrängt der Körper genau sein eigenes Gewicht an Wasser.", "Key idea: when floating, the object displaces exactly its own weight of water."), l("""§\dfrac{V_{unten}}{V} = \dfrac{\rho_K}{\rho_W}§""", """§\dfrac{V_{below}}{V} = \dfrac{\rho_{obj}}{\rho_{w}}§""")), formula = Fc4.archimedes)),
                solution = listOf("""§\dfrac{V_{unten}}{V} = ${m(rho, 3)} \Rightarrow \text{über Wasser:}\ ${m(above, 1)}\ \text{%}§""".let { l(it, it.replace("über Wasser:", "above water:").replace("unten", "below")) }),
                background = Bg4.buoyancy,
            )
        },
        lab("hebebuehne", Topic.Matter, H, 0.5f) {
            val d1 = tiered(listOf(2.0, 3.0), 1.5, 4.0, 0.1) // cm
            val d2 = tiered(listOf(20.0, 30.0), 15.0, 40.0, 1.0)
            val massCar = tiered(listOf(1000.0, 1500.0), 800.0, 2500.0, 50.0)
            val f1 = massCar * G * (d1 / d2).pow(2)
            val lift = 0.1
            val s1 = lift * (d2 / d1).pow(2)
            task(
                question = l("Eine hydraulische Hebebühne: kleiner Kolben ${n(d1)} cm Durchmesser, großer Kolben ${n(d2, 0)} cm. Auf dem großen steht ein Auto mit ${n(massCar, 0)} kg.", "A hydraulic car lift: small piston ${n(d1)} cm in diameter, large piston ${n(d2, 0)} cm. A car of ${n(massCar, 0)} kg stands on the large one."),
                steps = listOf(
                    num(l("Nötige Kraft am kleinen Kolben", "Force needed on the small piston"), f1, "N", rel = 0.03, done = """§F_1 \approx ${m(sig(f1))}\ \text{N}§""",
                        hints = listOf(l("Kernidee: Derselbe Druck auf beiden Kolben – die Kräfte verhalten sich wie die Flächen (also wie die Durchmesser im Quadrat).", "Key idea: the same pressure on both pistons – the forces are in the ratio of the areas (so of the diameters squared)."), """§F_1 = F_2 \left(\dfrac{d_1}{d_2}\right)^2§"""), formula = Fc4.pressure),
                    num(l("Wie weit muss der kleine Kolben gedrückt werden, damit das Auto 10 cm hochkommt?", "How far must the small piston move to lift the car by 10 cm?"), s1, "m", rel = 0.03, done = """§s_1 \approx ${m(sig(s1))}\ \text{m}§""",
                        hints = listOf(l("Das verdrängte Ölvolumen ist auf beiden Seiten gleich.", "The volume of oil displaced is the same on both sides."), """§s_1 = s_2 \left(\dfrac{d_2}{d_1}\right)^2§""")),
                ),
                solution = listOf("""§F_1 = ${m(massCar, 0)} \cdot 9{,}81\ \text{N} \cdot \left(\dfrac{${m(d1)}}{${m(d2, 0)}}\right)^2 \approx ${m(sig(f1))}\ \text{N}§""", """§s_1 = 0{,}1\ \text{m} \cdot \left(\dfrac{${m(d2, 0)}}{${m(d1)}}\right)^2 \approx ${m(sig(s1))}\ \text{m}§"""),
                background = Bg4.hydraulics,
            )
        },
        lab("heissluftballon", Topic.Matter, H, 0.5f) {
            val vol = tiered(listOf(2000.0, 3000.0), 1500.0, 6000.0, 100.0)
            val tOut = tiered(listOf(10.0, 20.0), 0.0, 25.0, 1.0)
            val tIn = tiered(listOf(100.0), 90.0, 115.0, 1.0)
            val rhoOut = 101300 / (287.0 * (tOut + 273.15))
            val rhoIn = 101300 / (287.0 * (tIn + 273.15))
            val payload = vol * (rhoOut - rhoIn)
            task(
                question = l("Ein Heißluftballon hat ${n(vol, 0)} m³. Außen hat die Luft ${n(tOut, 0)} °C, im Ballon ${n(tIn, 0)} °C. Wie viel Masse (Hülle, Korb, Menschen) kann er höchstens tragen?", "A hot-air balloon holds ${n(vol, 0)} m³. Outside the air is ${n(tOut, 0)} °C, inside ${n(tIn, 0)} °C. What is the most mass (envelope, basket, people) it can carry?"),
                given = listOf(l("""Luftdichte: §\rho = \dfrac{p}{R_s T},\ R_s = 287\ \tfrac{\text{J}}{\text{kg K}},\ p = 1013\ \text{hPa}§""", """Air density: §\rho = \dfrac{p}{R_s T},\ R_s = 287\ \tfrac{\text{J}}{\text{kg K}},\ p = 1013\ \text{hPa}§""")),
                steps = listOf(num(l("Tragfähigkeit", "Lifting capacity"), payload, "kg", rel = 0.04, done = """§m \approx ${m(sig(payload))}\ \text{kg}§""",
                    hints = listOf(l("Kernidee: Der Ballon verdrängt kalte Luft und ist mit leichterer warmer Luft gefüllt – der Unterschied kann getragen werden.", "Key idea: the balloon displaces cold air and is filled with lighter warm air – the difference can be carried."), """§m = V(\rho_{kalt} - \rho_{warm})§""".let { l(it, it.replace("kalt", "cold").replace("warm", "hot")) }), formula = Fc4.archimedes)),
                solution = listOf("""§\rho_{kalt} \approx ${m(rhoOut, 3)},\ \rho_{warm} \approx ${m(rhoIn, 3)}\ \tfrac{\text{kg}}{\text{m}^3}§""".let { l(it, it.replace("kalt", "cold").replace("warm", "hot")) }, """§m = ${m(vol, 0)} \cdot (${m(rhoOut, 3)} - ${m(rhoIn, 3)})\ \text{kg} \approx ${m(sig(payload))}\ \text{kg}§"""),
                background = Bg4.buoyancy,
            )
        },
        lab("barometer", Topic.Matter, H, 0.5f) {
            val (place, hM) = pick(l("auf der Zugspitze", "on the Zugspitze") to 2962.0, l("auf dem Mont Blanc", "on Mont Blanc") to 4806.0, l("auf dem Mount Everest", "on Mount Everest") to 8849.0, l("in einem Verkehrsflugzeug (Reiseflughöhe)", "outside an airliner at cruising altitude") to 11000.0)
            val hs = 8400.0
            val p = 1013 * exp(-hM / hs)
            task(
                question = l("Wie groß ist der Luftdruck $place (${n(hM, 0)} m)? Nimm eine Skalenhöhe von 8,4 km an.", "What is the air pressure $place (${n(hM, 0)} m)? Assume a scale height of 8.4 km."),
                steps = listOf(num(l("Luftdruck", "Air pressure"), p, "hPa", rel = 0.03, done = """§p \approx ${m(sig(p))}\ \text{hPa}§""",
                    hints = listOf(l("Kernidee: Der Druck nimmt exponentiell mit der Höhe ab.", "Key idea: pressure decreases exponentially with height."), """§p = p_0\, e^{-h/H}§"""),
                    formula = card(l("Barometrische Höhenformel", "Barometric formula"), """§p(h) = p_0\, e^{-h/H}§""", l("H ≈ 8,4 km (Skalenhöhe).", "H ≈ 8.4 km (scale height)."))) ),
                solution = listOf("""§p = 1013\ \text{hPa} \cdot e^{-${m(hM / 1000)}/8{,}4} \approx ${m(sig(p))}\ \text{hPa}§"""),
                background = Bg4.atmosphere,
            )
        },
    )

    // ===================================== WÄRME =====================================
    private val heat = listOf(
        lab("tee", Topic.Heat, E, 0.3f) {
            val liters = tiered(listOf(0.25, 0.5, 1.0), 0.2, 2.0, 0.05)
            val t0 = tiered(listOf(10.0, 20.0), 5.0, 25.0, 1.0)
            val q = 4190 * liters * (100 - t0)
            task(
                question = l("Wie viel Wärme braucht man, um ${n(liters)} Liter Wasser von ${n(t0, 0)} °C auf 100 °C zu bringen?", "How much heat does it take to bring ${n(liters)} litres of water from ${n(t0, 0)} °C to 100 °C?"),
                steps = listOf(num(l("Wärme in Kilojoule", "Heat in kilojoules"), q / 1000, "kJ", rel = 0.02, done = """§Q \approx ${m(sig(q / 1000))}\ \text{kJ}§""",
                    hints = listOf(l("Kernidee: Wärme = spezifische Wärmekapazität mal Masse mal Temperaturänderung. 1 Liter Wasser ≈ 1 kg.", "Key idea: heat = specific heat capacity times mass times temperature change. 1 litre of water ≈ 1 kg."), """§Q = c\, m\, \Delta T§"""), formula = Fc4.heat)),
                solution = listOf("""§Q = 4{,}19\ \tfrac{\text{kJ}}{\text{kg K}} \cdot ${m(liters)}\ \text{kg} \cdot ${m(100 - t0, 0)}\ \text{K} \approx ${m(sig(q / 1000))}\ \text{kJ}§"""),
                background = Bg4.heat,
            )
        },
        lab("wasserkocher", Topic.Heat, M, 0.4f) {
            val p = pick(1800.0, 2000.0, 2200.0, 3000.0)
            val liters = tiered(listOf(1.0, 1.5), 0.5, 1.7, 0.1)
            val t0 = tiered(listOf(15.0, 20.0), 8.0, 25.0, 1.0)
            val eta = if (hard) 0.85 else 1.0
            val t = 4190 * liters * (100 - t0) / (p * eta)
            task(
                question = l("Ein Wasserkocher (${n(p, 0)} W) erhitzt ${n(liters)} l Wasser von ${n(t0, 0)} °C zum Kochen.${if (hard) " 15 % der Energie gehen an die Umgebung verloren." else ""} Wie lange dauert das?", "A kettle (${n(p, 0)} W) heats ${n(liters)} l of water from ${n(t0, 0)} °C to boiling.${if (hard) " 15% of the energy is lost to the surroundings." else ""} How long does it take?"),
                steps = listOf(num(l("Zeit in Sekunden", "Time in seconds"), t, "s", rel = 0.03, done = """§t \approx ${m(sig(t))}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Nötige Wärme durch nutzbare Leistung.", "Key idea: heat needed divided by useful power."), """§t = \dfrac{c\, m\, \Delta T}{P${if (hard) " \\cdot 0{,}85" else ""}}§"""), formula = Fc4.heat)),
                solution = listOf("""§Q = 4190 \cdot ${m(liters)} \cdot ${m(100 - t0, 0)}\ \text{J} \approx ${m(sig(4190 * liters * (100 - t0)))}\ \text{J}§""", """§t = \dfrac{Q}{${m(p * eta, 0)}\ \text{W}} \approx ${m(sig(t))}\ \text{s}§"""),
                background = Bg4.heat,
            )
        },
        lab("eiswuerfel", Topic.Heat, E, 0.4f) {
            val g = tiered(listOf(20.0, 50.0, 100.0), 10.0, 200.0, 5.0)
            val q = g / 1000 * 334
            task(
                question = l("Wie viel Wärme braucht man, um ${n(g, 0)} g Eis von 0 °C vollständig zu Wasser von 0 °C zu schmelzen?", "How much heat does it take to melt ${n(g, 0)} g of ice at 0 °C completely into water at 0 °C?"),
                steps = listOf(num(l("Wärme in Kilojoule", "Heat in kilojoules"), q, "kJ", rel = 0.02, done = """§Q \approx ${m(sig(q))}\ \text{kJ}§""",
                    hints = listOf(l("Kernidee: Beim Schmelzen steigt die Temperatur nicht – man braucht die Schmelzwärme.", "Key idea: while melting the temperature doesn't rise – you need the latent heat."), """§Q = m \cdot 334\ \tfrac{\text{kJ}}{\text{kg}}§"""), formula = Fc4.latent)),
                solution = listOf("""§Q = ${m(g / 1000, 3)}\ \text{kg} \cdot 334\ \tfrac{\text{kJ}}{\text{kg}} \approx ${m(sig(q))}\ \text{kJ}§"""),
                background = Bg4.latent,
            )
        },
        lab("mischtemperatur", Topic.Heat, M, 0.5f) {
            val m1 = tiered(listOf(2.0, 3.0), 1.0, 5.0, 0.1)
            val t1 = tiered(listOf(60.0, 80.0), 50.0, 90.0, 1.0)
            val m2 = tiered(listOf(1.0, 2.0), 0.5, 4.0, 0.1)
            val t2 = tiered(listOf(10.0, 20.0), 5.0, 25.0, 1.0)
            val tm = (m1 * t1 + m2 * t2) / (m1 + m2)
            task(
                question = l("Du mischst ${n(m1)} l Wasser mit ${n(t1, 0)} °C und ${n(m2)} l Wasser mit ${n(t2, 0)} °C. Welche Temperatur hat die Mischung (ohne Verluste)?", "You mix ${n(m1)} l of water at ${n(t1, 0)} °C with ${n(m2)} l of water at ${n(t2, 0)} °C. What temperature does the mixture have (no losses)?"),
                steps = listOf(num(l("Mischtemperatur", "Mixing temperature"), tm, "°C", rel = 0.02, done = """§T_m \approx ${m(sig(tm))}\ ^\circ\text{C}§""",
                    hints = listOf(l("Kernidee: Die Wärme, die das heiße Wasser abgibt, nimmt das kalte auf.", "Key idea: the heat given off by the hot water is taken in by the cold water."), """§m_1 (T_1 - T_m) = m_2 (T_m - T_2)§"""),
                    formula = card(l("Mischtemperatur", "Mixing temperature"), """§T_m = \dfrac{m_1 T_1 + m_2 T_2}{m_1 + m_2}§""", l("Gleicher Stoff, keine Verluste.", "Same substance, no losses."))) ),
                solution = listOf("""§T_m = \dfrac{${m(m1)} \cdot ${m(t1, 0)} + ${m(m2)} \cdot ${m(t2, 0)}}{${m(m1 + m2)}}\ ^\circ\text{C} \approx ${m(sig(tm))}\ ^\circ\text{C}§"""),
                background = Bg4.heat,
            )
        },
        lab("reifendruck", Topic.Heat, H, 0.5f) {
            val p1 = tiered(listOf(2.0, 2.5), 1.8, 3.2, 0.1)
            val t1 = tiered(listOf(20.0, 15.0), 5.0, 25.0, 1.0)
            val t2 = tiered(listOf(-10.0, 50.0), -20.0, 60.0, 1.0).let { if (kotlin.math.abs(it - t1) < 10) it + 25 else it }
            // Überdruck gemessen: absolut = Überdruck + 1 bar
            val pAbs2 = (p1 + 1.0) * (t2 + 273.15) / (t1 + 273.15)
            val gauge2 = pAbs2 - 1.0
            task(
                question = l("Ein Reifen wird bei ${n(t1, 0)} °C auf ${n(p1)} bar Überdruck aufgepumpt. Dann wird es ${n(t2, 0)} °C. Was zeigt das Manometer jetzt (Volumen gleich)?", "A tyre is inflated to ${n(p1)} bar gauge pressure at ${n(t1, 0)} °C. Then the temperature becomes ${n(t2, 0)} °C. What does the gauge show now (same volume)?"),
                given = listOf(l("Luftdruck außen: 1,0 bar. Das Manometer zeigt den Überdruck.", "Outside air pressure: 1.0 bar. The gauge shows the pressure above that.")),
                steps = listOf(num(l("Neuer Überdruck", "New gauge pressure"), gauge2, "bar", rel = 0.02, done = """§p \approx ${m(sig(gauge2))}\ \text{bar}§""",
                    hints = listOf(l("Kernidee: Für das Gasgesetz brauchst du den absoluten Druck und die Temperatur in Kelvin.", "Key idea: the gas law needs absolute pressure and the temperature in kelvin."), """§p_2 = p_1 \dfrac{T_2}{T_1},\ p_1 = ${m(p1 + 1)}\ \text{bar},\ T_1 = ${m(t1 + 273.15, 2)}\ \text{K}§"""), formula = Fc4.gas)),
                solution = listOf("""§p_{2} = ${m(p1 + 1)}\ \text{bar} \cdot \dfrac{${m(t2 + 273.15, 2)}\ \text{K}}{${m(t1 + 273.15, 2)}\ \text{K}} \approx ${m(sig(pAbs2))}\ \text{bar}§""", l("""Überdruck: §${m(sig(pAbs2))} - 1 \approx ${m(sig(gauge2))}\ \text{bar}§""", """Gauge pressure: §${m(sig(pAbs2))} - 1 \approx ${m(sig(gauge2))}\ \text{bar}§""")),
                background = Bg4.gas,
            )
        },
        lab("kraftwerk", Topic.Heat, H, 0.5f) {
            val th = tiered(listOf(550.0, 600.0), 450.0, 650.0, 10.0)
            val tc = tiered(listOf(30.0, 25.0), 15.0, 40.0, 1.0)
            val eta = 1 - (tc + 273.15) / (th + 273.15)
            task(
                question = l("Ein Kohlekraftwerk arbeitet mit Dampf von ${n(th, 0)} °C und kühlt mit Flusswasser von ${n(tc, 0)} °C. Welchen Wirkungsgrad hätte eine ideale Maschine dazwischen?", "A coal power plant uses steam at ${n(th, 0)} °C and cools with river water at ${n(tc, 0)} °C. What efficiency would an ideal engine between them have?"),
                steps = listOf(num(l("Carnot-Wirkungsgrad", "Carnot efficiency"), eta * 100, "%", abs = 1.0, done = """§\eta \approx ${m(eta * 100, 1)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Temperaturen in Kelvin einsetzen!", "Key idea: use temperatures in kelvin!"), """§\eta = 1 - \dfrac{${m(tc + 273.15, 2)}}{${m(th + 273.15, 2)}}§"""), formula = Fc4.carnot)),
                solution = listOf("""§\eta = 1 - \dfrac{${m(tc + 273.15, 2)}\ \text{K}}{${m(th + 273.15, 2)}\ \text{K}} \approx ${m(eta * 100, 1)}\ \text{%}§"""),
                background = Bg4.engines,
                fact = l("Echte Kohlekraftwerke schaffen etwa 40–45 % – der Rest der Energie heizt Flüsse und Kühltürme.", "Real coal plants reach about 40–45% – the rest of the energy heats rivers and cooling towers."),
            )
        },
        lab("entropie", Topic.Heat, H, 0.3f) {
            val opts = pick(
                l("Ein Glas mit heißem Tee steht im Zimmer. Warum wird nie von selbst der Tee heißer und das Zimmer kühler, obwohl das die Energieerhaltung nicht verletzen würde?", "A glass of hot tea stands in a room. Why does the tea never get hotter by itself while the room cools down, even though that wouldn't violate energy conservation?") to listOf(
                    l("Dabei würde die Entropie insgesamt abnehmen – das ist praktisch unmöglich.", "Total entropy would decrease – that is practically impossible."),
                    l("Weil die Energie dabei verloren ginge.", "Because energy would be lost in the process."),
                    l("Weil Tee und Luft verschiedene Stoffe sind.", "Because tea and air are different substances."),
                    l("Es passiert ständig, nur sehr langsam.", "It happens all the time, just very slowly."),
                ),
                l("Ein Kühlschrank schafft Wärme von innen (kalt) nach außen (warm). Verletzt er den zweiten Hauptsatz?", "A fridge moves heat from inside (cold) to outside (warm). Does it violate the second law?") to listOf(
                    l("Nein – er braucht dafür Arbeit (Strom), und außen entsteht mehr Entropie, als innen verschwindet.", "No – it needs work (electricity) for it, and more entropy is created outside than disappears inside."),
                    l("Ja, Kühlschränke sind eine Ausnahme.", "Yes, fridges are an exception."),
                    l("Nein, weil Kälte einfach eine andere Form von Wärme ist.", "No, because cold is just another form of heat."),
                    l("Ja, deshalb gehen Kühlschränke irgendwann kaputt.", "Yes, that's why fridges eventually break."),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Wärme fließt von selbst nur von warm nach kalt – in einem abgeschlossenen System wächst die Entropie.", "Key idea: heat flows by itself only from hot to cold – in a closed system entropy grows."), l("Wo bekommt eine Maschine die Erlaubnis, Wärme 'bergauf' zu schieben?", "Where does a machine get the permission to push heat 'uphill'?")), formula = null)),
                solution = listOf(l("Der zweite Hauptsatz: Die Gesamtentropie eines abgeschlossenen Systems nimmt nicht ab. Wärme fließt von selbst nur von warm nach kalt; umgekehrt geht es nur mit zusätzlicher Arbeit.", "The second law: the total entropy of a closed system does not decrease. Heat flows by itself only from hot to cold; the other way round only works with extra work.")),
                background = Bg4.engines,
            )
        },
        lab("erdtemperatur", Topic.Heat, X, 0.8f, master = true) {
            val s = tiered(listOf(1361.0), 1355.0, 1365.0, 1.0)
            val albedo = tiered(listOf(0.3), 0.25, 0.35, 0.01)
            val (planet, dist) = if (hard) pick(l("die Erde", "Earth") to 1.0, l("den Mars", "Mars") to 1.524) else (l("die Erde", "Earth") to 1.0)
            val sp = s / (dist * dist)
            val t = (sp * (1 - albedo) / (4 * 5.67e-8)).pow(0.25)
            task(
                question = l("Welche Gleichgewichtstemperatur hätte $planet ohne Treibhauseffekt? Solarkonstante bei der Erde ${n(s, 0)} W/m², Albedo ${n(albedo)}${if (dist > 1) ", Mars ist 1,524-mal so weit von der Sonne entfernt" else ""}.", "What equilibrium temperature would $planet have without a greenhouse effect? Solar constant at Earth ${n(s, 0)} W/m², albedo ${n(albedo)}${if (dist > 1) ", Mars is 1.524 times as far from the Sun" else ""}."),
                given = listOf("""§\sigma = 5{,}67 \cdot 10^{-8}\ \tfrac{\text{W}}{\text{m}^2 \text{K}^4}§"""),
                steps = listOf(num(l("Temperatur in Kelvin", "Temperature in kelvin"), t, "K", rel = 0.02, done = """§T \approx ${m(sig(t))}\ \text{K} \approx ${m(t - 273.15, 0)}\ ^\circ\text{C}§""",
                    hints = listOf(l("Kernidee: Eingefangen wird Licht auf einer Kreisscheibe (πR²), abgestrahlt wird von der ganzen Kugel (4πR²).", "Key idea: light is captured on a disc (πR²) but radiated from the whole sphere (4πR²)."), """§(1 - a) S \pi R^2 = 4\pi R^2 \sigma T^4§"""),
                    formula = card(l("Stefan-Boltzmann", "Stefan–Boltzmann"), """§P = \sigma A T^4§""", l("Abgestrahlte Leistung eines schwarzen Körpers.", "Power radiated by a black body."))) ),
                solution = listOf("""§T = \left(\dfrac{(1 - ${m(albedo)}) \cdot ${m(sig(sp))}}{4 \cdot 5{,}67 \cdot 10^{-8}}\right)^{1/4}\ \text{K} \approx ${m(sig(t))}\ \text{K}§"""),
                background = Bg4.radiation,
            )
        },
    )

    // ===================================== SCHALL & WELLEN =====================================
    private val waves = listOf(
        lab("kammerton", Topic.Waves, E, 0.2f) {
            val f = pick(440.0, 262.0, 880.0, 110.0, 55.0)
            val lam = 343 / f
            task(
                question = l("Wie lang ist die Schallwelle eines Tons mit ${n(f, 0)} Hz in Luft (343 m/s)?", "How long is the sound wave of a ${n(f, 0)} Hz tone in air (343 m/s)?"),
                steps = listOf(num(l("Wellenlänge", "Wavelength"), lam * 100, "cm", rel = 0.02, done = """§\lambda \approx ${m(sig(lam * 100))}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Geschwindigkeit = Wellenlänge mal Frequenz.", "Key idea: speed = wavelength times frequency."), """§\lambda = c / f§"""), formula = Fc4.wave)),
                solution = listOf("""§\lambda = \dfrac{343\ \text{m/s}}{${m(f, 0)}\ \text{Hz}} \approx ${m(sig(lam))}\ \text{m}§"""),
                background = Bg4.sound,
            )
        },
        lab("weltraumschall", Topic.Waves, E, 0.2f) {
            val opts = l("In Science-Fiction-Filmen hört man Explosionen im Weltall. Was würde man dort wirklich hören?", "In sci-fi films you hear explosions in space. What would you really hear there?") to listOf(
                l("Nichts – ohne Luft gibt es nichts, was den Schall weiterträgt.", "Nothing – without air there is nothing to carry the sound."),
                l("Einen leiseren Knall, weil das All so groß ist.", "A quieter bang, because space is so big."),
                l("Einen höheren Ton, weil es so kalt ist.", "A higher pitch, because it is so cold."),
                l("Dasselbe wie auf der Erde, nur später.", "The same as on Earth, just later."),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Schall ist eine Schwingung von Teilchen.", "Key idea: sound is a vibration of particles."), l("Was schwingt im Vakuum?", "What could vibrate in a vacuum?")), formula = null)),
                solution = listOf(l("Schall braucht ein Medium (Gas, Flüssigkeit oder Festkörper). Im Vakuum ist es vollkommen still – Licht dagegen kommt durch, weil es keine Teilchen braucht.", "Sound needs a medium (gas, liquid or solid). In a vacuum it is completely silent – light gets through, though, because it needs no particles.")),
                background = Bg4.sound,
            )
        },
        lab("echo", Topic.Waves, M, 0.3f) {
            val t = tiered(listOf(1.0, 2.0, 0.5), 0.3, 4.0, 0.1)
            val d = 343 * t / 2
            task(
                question = l("Du rufst gegen eine Felswand und hörst das Echo nach ${n(t)} ${if (t == 1.0) "Sekunde" else "Sekunden"}. Wie weit ist die Wand weg?", "You shout at a cliff and hear the echo after ${n(t)} ${if (t == 1.0) "second" else "seconds"}. How far away is the cliff?"),
                steps = listOf(num(l("Entfernung", "Distance"), d, "m", rel = 0.03, done = """§d \approx ${m(sig(d))}\ \text{m}§""",
                    hints = listOf(
                        l("Kernidee: Der Schall läuft zur Wand und wieder zurück, also die doppelte Strecke.", "Key idea: the sound travels to the cliff and back, so twice the distance."),
                        l("""In ${n(t)} s legt der Schall §343 \cdot ${m(t)}§ Meter zurück. Die Hälfte davon ist der Weg zur Wand.""", """In ${n(t)} s the sound covers §343 \cdot ${m(t)}§ metres. Half of that is the way to the cliff."""),
                    ),
                    formula = card(l("Echo", "Echo"), """§d = \dfrac{c \cdot t}{2}§""", l("Schall in Luft: c ≈ 343 m/s. Geteilt durch 2, weil hin und zurück.", "Sound in air: c ≈ 343 m/s. Divided by 2 because there and back.")))),
                solution = listOf("""§d = \dfrac{343\ \text{m/s} \cdot ${m(t)}\ \text{s}}{2} \approx ${m(sig(d))}\ \text{m}§"""),
                background = Bg4.sound,
                fact = l("Fledermäuse und Delfine messen Entfernungen genau so – mit Ultraschall-Echos bis auf Millimeter.", "Bats and dolphins measure distances exactly this way – with ultrasound echoes, down to millimetres."),
            )
        },
        lab("dezibel", Topic.Waves, M, 0.4f) {
            val l1 = tiered(listOf(60.0, 70.0), 50.0, 80.0, 1.0)
            val dl = tiered(listOf(10.0, 20.0, 30.0), 3.0, 40.0, 1.0)
            val ratio = 10.0.pow(dl / 10)
            task(
                question = l("Ein Gespräch hat ${n(l1, 0)} dB, eine laute Straße ${n(l1 + dl, 0)} dB. Wie viel mal so groß ist die Schallintensität auf der Straße?", "A conversation is ${n(l1, 0)} dB, a busy street ${n(l1 + dl, 0)} dB. How many times greater is the sound intensity on the street?"),
                steps = listOf(num(l("Verhältnis der Intensitäten", "Ratio of intensities"), ratio, "", rel = 0.03, done = """§\dfrac{I_2}{I_1} \approx ${m(sig(ratio))}§""",
                    hints = listOf(l("Kernidee: Jede 10 dB bedeuten den Faktor 10.", "Key idea: every 10 dB means a factor of 10."), """§10^{${m(dl, 0)}/10}§"""), formula = Fc4.db)),
                solution = listOf("""§\dfrac{I_2}{I_1} = 10^{${m(dl, 0)}/10} \approx ${m(sig(ratio))}§"""),
                background = Bg4.decibel,
            )
        },
        lab("gitarre", Topic.Waves, H, 0.5f) {
            val len = tiered(listOf(0.65), 0.6, 0.66, 0.01)
            val f = pick(82.4, 110.0, 146.8, 196.0, 246.9, 329.6)
            val v = 2 * len * f
            val fret = 2.0.pow(12 / 12.0) * f
            task(
                question = l("Eine Gitarrensaite schwingt auf ${n(len * 100, 0)} cm Länge mit ${n(f, 1)} Hz (Grundton).", "A guitar string vibrates over a length of ${n(len * 100, 0)} cm at ${n(f, 1)} Hz (fundamental)."),
                steps = listOf(
                    num(l("Wellengeschwindigkeit auf der Saite", "Wave speed on the string"), v, "m/s", rel = 0.02, done = """§v \approx ${m(sig(v))}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Beim Grundton passt eine halbe Wellenlänge auf die Saite.", "Key idea: in the fundamental, half a wavelength fits on the string."), """§\lambda = 2L,\ v = \lambda f§"""), formula = Fc4.wave),
                    num(l("Du greifst im 12. Bund (halbe Länge). Welche Frequenz klingt jetzt?", "You fret at the 12th fret (half the length). What frequency sounds now?"), fret, "Hz", rel = 0.02, done = """§f' \approx ${m(sig(fret))}\ \text{Hz}§""",
                        hints = listOf(l("Gleiche Saite, gleiche Wellengeschwindigkeit – halbe Länge.", "Same string, same wave speed – half the length."), """§f' = \dfrac{v}{2 \cdot L/2}§""")),
                ),
                solution = listOf("""§v = 2 \cdot ${m(len)}\ \text{m} \cdot ${m(f, 1)}\ \text{Hz} \approx ${m(sig(v))}\ \tfrac{\text{m}}{\text{s}}§""", l("""Halbe Länge, doppelte Frequenz: §f' \approx ${m(sig(fret))}\ \text{Hz}§ – eine Oktave höher.""", """Half the length, double the frequency: §f' \approx ${m(sig(fret))}\ \text{Hz}§ – one octave higher.""")),
                background = Bg4.strings,
            )
        },
        lab("schwebung", Topic.Waves, M, 0.4f) {
            val f1 = tiered(listOf(440.0), 435.0, 445.0, 0.5)
            val df = tiered(listOf(2.0, 3.0, 4.0), 0.5, 6.0, 0.5)
            val f2 = f1 + df
            task(
                question = l("Zwei Stimmgabeln mit ${n(f1, 1)} Hz und ${n(f2, 1)} Hz klingen gleichzeitig. Man hört ein An- und Abschwellen der Lautstärke. Wie oft pro Sekunde?", "Two tuning forks at ${n(f1, 1)} Hz and ${n(f2, 1)} Hz sound at the same time. You hear the loudness rise and fall. How often per second?"),
                steps = listOf(num(l("Schwebungsfrequenz", "Beat frequency"), df, "Hz", rel = 0.02, done = """§f_S = ${m(df)}\ \text{Hz}§""",
                    hints = listOf(l("Kernidee: Die beiden Wellen geraten abwechselnd in und außer Takt.", "Key idea: the two waves drift in and out of step."), """§f_S = |f_1 - f_2|§"""),
                    formula = card(l("Schwebung", "Beats"), """§f_S = |f_1 - f_2|§""", l("So stimmen Musiker nach Gehör.", "That's how musicians tune by ear."))) ),
                solution = listOf("""§f_S = |${m(f1, 1)} - ${m(f2, 1)}|\ \text{Hz} = ${m(df)}\ \text{Hz}§"""),
                background = Bg4.strings,
            )
        },
    )

    // ===================================== LICHT & OPTIK =====================================
    private val optics = listOf(
        lab("sonnenlicht", Topic.Optics, E, 0.2f) {
            val (what, d) = pick(l("von der Sonne", "from the Sun") to 1.496e11, l("vom Mond", "from the Moon") to 3.844e8, l("vom Mars (bei größter Annäherung)", "from Mars (at closest approach)") to 5.46e10, l("vom Jupiter (etwa)", "from Jupiter (roughly)") to 6.3e11)
            val t = d / C_LIGHT
            task(
                question = l("Wie lange braucht Licht $what zur Erde? Entfernung: ${n(d / 1000, 0)} km.", "How long does light take to reach Earth $what? Distance: ${n(d / 1000, 0)} km."),
                steps = listOf(num(l("Zeit in Sekunden", "Time in seconds"), t, "s", rel = 0.02, done = """§t \approx ${m(sig(t))}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Licht legt rund 300.000 km pro Sekunde zurück.", "Key idea: light travels about 300,000 km per second."), """§t = d / c§"""),
                    formula = card(l("Lichtgeschwindigkeit", "Speed of light"), """§c \approx 3{,}0 \cdot 10^8\ \tfrac{\text{m}}{\text{s}}§""", l("Im Vakuum, genau 299.792.458 m/s.", "In vacuum, exactly 299,792,458 m/s."))) ),
                solution = listOf("""§t = \dfrac{${sci(d)}\ \text{m}}{3{,}0 \cdot 10^8\ \text{m/s}} \approx ${m(sig(t))}\ \text{s}§"""),
                background = Bg4.light,
            )
        },
        lab("himmelblau", Topic.Optics, E, 0.3f) {
            val opts = pick(
                l("Warum ist der Himmel tagsüber blau?", "Why is the sky blue during the day?") to listOf(
                    l("Luftmoleküle streuen blaues Licht viel stärker als rotes.", "Air molecules scatter blue light much more strongly than red."),
                    l("Der Himmel spiegelt das blaue Meer.", "The sky reflects the blue ocean."),
                    l("Die Sonne schickt fast nur blaues Licht.", "The Sun sends almost only blue light."),
                    l("Sauerstoff ist ein blaues Gas.", "Oxygen is a blue gas."),
                ),
                l("Warum ist die untergehende Sonne rot?", "Why is the setting Sun red?") to listOf(
                    l("Ihr Licht durchquert viel Luft, das Blau wird unterwegs zur Seite gestreut.", "Its light crosses a lot of air, and the blue is scattered away on the way."),
                    l("Die Sonne kühlt abends ab.", "The Sun cools down in the evening."),
                    l("Rotes Licht ist schneller als blaues.", "Red light is faster than blue light."),
                    l("Der Staub glüht in der Abendsonne.", "Dust glows in the evening sun."),
                ),
            )
            task(
                question = opts.first,
                steps = listOf(choice(l("Was stimmt?", "What is right?"), opts.second, done = opts.second.first(),
                    hints = listOf(l("Kernidee: Kleine Teilchen streuen kurze Wellenlängen (blau) viel stärker – mit der vierten Potenz.", "Key idea: small particles scatter short wavelengths (blue) much more – with the fourth power."), l("Blaues Licht hat etwa die halbe Wellenlänge von rotem.", "Blue light has roughly half the wavelength of red.")),
                    formula = card(l("Rayleigh-Streuung", "Rayleigh scattering"), """§I \propto \dfrac{1}{\lambda^4}§""", l("Halbe Wellenlänge, 16-mal mehr Streuung.", "Half the wavelength, 16 times the scattering.")))),
                solution = listOf(l("Luftmoleküle streuen Licht umso stärker, je kürzer seine Wellenlänge ist (Rayleigh-Streuung). Tagsüber kommt das gestreute Blau aus allen Richtungen; am Abend ist auf dem langen Weg durch die Luft das Blau weggestreut, übrig bleibt Rot.", "Air molecules scatter light more strongly the shorter its wavelength (Rayleigh scattering). During the day the scattered blue comes from all directions; in the evening the blue is scattered away on the long path through the air and red is left.")),
                background = Bg4.light,
            )
        },
        lab("brechung", Topic.Optics, M, 0.4f) {
            val a1 = tiered(listOf(30.0, 45.0, 60.0), 20.0, 70.0, 1.0)
            val (mat, n2) = pick(l("Wasser", "water") to 1.33, l("Glas", "glass") to 1.5, l("Diamant", "diamond") to 2.42)
            val a2 = asin(sin(a1 * PI / 180) / n2) * 180 / PI
            task(
                question = l("Ein Lichtstrahl trifft unter ${n(a1, 0)}° zum Lot aus der Luft auf ${mat} (n = ${n(n2)}). Unter welchem Winkel läuft er weiter?", "A light ray travelling in air hits $mat (n = ${n(n2)}) at ${n(a1, 0)}° to the normal. At what angle does it continue?"),
                steps = listOf(num(l("Brechungswinkel", "Angle of refraction"), a2, "°", abs = 0.6, done = """§\alpha_2 \approx ${m(a2, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Im langsameren Medium knickt der Strahl zum Lot hin.", "Key idea: in the slower medium the ray bends towards the normal."), """§\sin\alpha_2 = \dfrac{\sin ${m(a1, 0)}^\circ}{${m(n2)}}§"""), formula = Fc4.snell)),
                solution = listOf("""§\sin\alpha_2 = \dfrac{1 \cdot \sin ${m(a1, 0)}^\circ}{${m(n2)}} \approx ${m(sin(a1 * PI / 180) / n2, 3)} \Rightarrow \alpha_2 \approx ${m(a2, 1)}^\circ§"""),
                background = Bg4.refraction,
            )
        },
        lab("linse", Topic.Optics, M, 0.5f) {
            val f = tiered(listOf(5.0, 10.0, 20.0), 4.0, 30.0, 1.0)
            val g = f * tiered(listOf(2.0, 3.0, 1.5), 1.2, 5.0, 0.1)
            val b = 1 / (1 / f - 1 / g)
            val mag = b / g
            task(
                question = l("Eine Sammellinse hat ${n(f, 0)} cm Brennweite. Eine Kerze steht ${n(g, 1)} cm davor.", "A converging lens has a focal length of ${n(f, 0)} cm. A candle stands ${n(g, 1)} cm in front of it."),
                steps = listOf(
                    num(l("Wo entsteht das scharfe Bild (Bildweite)?", "Where does the sharp image form (image distance)?"), b, "cm", rel = 0.03, done = """§b \approx ${m(sig(b))}\ \text{cm}§""",
                        hints = listOf(l("Kernidee: Linsengleichung nach b auflösen.", "Key idea: solve the lens equation for b."), """§\dfrac{1}{b} = \dfrac{1}{f} - \dfrac{1}{g}§"""), formula = Fc4.lens),
                    num(l("Abbildungsmaßstab (Bildgröße : Kerzengröße)", "Magnification (image size : candle size)"), mag, "", rel = 0.03, done = """§\beta \approx ${m(sig(mag))}§""",
                        hints = listOf(l("Bild- und Gegenstandsgröße verhalten sich wie die Weiten.", "Image and object size are in the ratio of the distances."), """§\beta = b / g§""")),
                ),
                solution = listOf("""§\dfrac{1}{b} = \dfrac{1}{${m(f, 0)}} - \dfrac{1}{${m(g, 1)}} \Rightarrow b \approx ${m(sig(b))}\ \text{cm}§""", """§\beta = \dfrac{${m(sig(b))}}{${m(g, 1)}} \approx ${m(sig(mag))}§"""),
                background = Bg4.lens,
            )
        },
        lab("glasfaser", Topic.Optics, H, 0.5f) {
            val nCore = tiered(listOf(1.5, 1.48), 1.45, 1.6, 0.01)
            val nClad = (nCore - tiered(listOf(0.02, 0.05), 0.01, 0.08, 0.005))
            val crit = asin(nClad / nCore) * 180 / PI
            task(
                question = l("Eine Glasfaser hat einen Kern mit n = ${n(nCore)} und einen Mantel mit n = ${n(nClad, 3)}. Ab welchem Winkel (zum Lot) wird Licht an der Grenze vollständig reflektiert?", "An optical fibre has a core with n = ${n(nCore)} and a cladding with n = ${n(nClad, 3)}. From what angle (to the normal) is light totally reflected at the boundary?"),
                steps = listOf(num(l("Grenzwinkel", "Critical angle"), crit, "°", abs = 0.6, done = """§\alpha_G \approx ${m(crit, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Beim Grenzwinkel würde der gebrochene Strahl genau an der Grenze entlanglaufen (90°).", "Key idea: at the critical angle the refracted ray would run exactly along the boundary (90°)."), l("""§\sin\alpha_G = \dfrac{n_M}{n_K}§""", """§\sin\alpha_c = \dfrac{n_{cl}}{n_{co}}§""")), formula = Fc4.snell)),
                solution = listOf("""§\sin\alpha_G = \dfrac{${m(nClad, 3)}}{${m(nCore)}} \approx ${m(nClad / nCore, 4)} \Rightarrow \alpha_G \approx ${m(crit, 1)}^\circ§"""),
                background = Bg4.tir,
            )
        },
        lab("lupe", Topic.Optics, M, 0.4f) {
            val f = tiered(listOf(5.0, 10.0), 2.0, 12.0, 0.5)
            val v = 25 / f
            task(
                question = l("Eine Lupe hat ${n(f)} cm Brennweite. Wie stark vergrößert sie, wenn man durch sie mit entspanntem Auge schaut (Bezugssehweite 25 cm)?", "A magnifying glass has a focal length of ${n(f)} cm. How much does it magnify when viewed with a relaxed eye (reference distance 25 cm)?"),
                steps = listOf(num(l("Vergrößerung", "Magnification"), v, "×", rel = 0.02, done = """§V \approx ${m(sig(v))}§""",
                    hints = listOf(l("Kernidee: Die Lupe erlaubt, viel näher heranzugehen als die 25 cm, bei denen man ohne sie scharf sieht.", "Key idea: the lens lets you get much closer than the 25 cm at which you see sharply without it."), """§V = \dfrac{25\ \text{cm}}{f}§"""), formula = Fc4.lens)),
                solution = listOf("""§V = \dfrac{25\ \text{cm}}{${m(f)}\ \text{cm}} \approx ${m(sig(v))}§"""),
                background = Bg4.lens,
            )
        },
        lab("cdgitter", Topic.Optics, H, 0.6f) {
            val d = tiered(listOf(1.6, 0.74), 0.7, 2.0, 0.01) // µm
            val lam = pick(450.0, 532.0, 633.0) // nm
            val a = kotlin.math.asin(lam * 1e-9 / (d * 1e-6)) * 180 / PI
            task(
                question = l("Ein Laserpointer (${n(lam, 0)} nm) leuchtet senkrecht auf eine ${if (d > 1.0) "CD" else "DVD"} (Spurabstand ${n(d, 2)} µm). Unter welchem Winkel erscheint der erste helle Fleck neben dem Hauptstrahl?", "A laser pointer (${n(lam, 0)} nm) shines perpendicularly onto a ${if (d > 1.0) "CD" else "DVD"} (track spacing ${n(d, 2)} µm). At what angle does the first bright spot appear next to the main beam?"),
                steps = listOf(num(l("Winkel der 1. Ordnung", "First-order angle"), a, "°", abs = 0.6, done = """§\alpha_1 \approx ${m(a, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Die Spuren wirken wie ein Reflexionsgitter.", "Key idea: the tracks act like a reflection grating."), """§\sin\alpha_1 = \dfrac{\lambda}{d}§"""), formula = Fc4.grating)),
                solution = listOf("""§\sin\alpha_1 = \dfrac{${sci(lam * 1e-9)}}{${sci(d * 1e-6)}} \approx ${m(lam * 1e-3 / d, 3)} \Rightarrow \alpha_1 \approx ${m(a, 1)}^\circ§"""),
                background = Bg4.interference,
            )
        },
        lab("aufloesung", Topic.Optics, X, 0.6f) {
            val (inst, dM) = pick(l("ein menschliches Auge (Pupille 5 mm)", "a human eye (pupil 5 mm)") to 0.005, l("ein Fernglas (50 mm)", "binoculars (50 mm)") to 0.05, l("ein Teleskop (2,4 m, wie Hubble)", "a telescope (2.4 m, like Hubble)") to 2.4)
            val lam = 550e-9
            val theta = 1.22 * lam / dM
            val distMoon = 3.844e8
            val detail = theta * distMoon / 1000
            task(
                question = l("Wie klein dürfen zwei Krater auf dem Mond (384.400 km entfernt) höchstens auseinanderliegen, damit $inst sie bei grünem Licht (550 nm) gerade noch trennt?", "How close can two craters on the Moon (384,400 km away) be and still be separated by $inst in green light (550 nm)?"),
                steps = listOf(num(l("Kleinster Abstand in km", "Smallest separation in km"), detail, "km", rel = 0.03, done = """§s \approx ${m(sig(detail))}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Beugung begrenzt die Auflösung: kleinster Winkel 1,22 λ/D, dann mal Entfernung.", "Key idea: diffraction limits resolution: smallest angle 1.22 λ/D, then times the distance."), """§\theta = 1{,}22 \dfrac{\lambda}{D},\ s = \theta \cdot d§"""),
                    formula = card(l("Rayleigh-Kriterium", "Rayleigh criterion"), """§\theta \approx 1{,}22\, \dfrac{\lambda}{D}§""", l("Winkel im Bogenmaß.", "Angle in radians."))) ),
                solution = listOf("""§\theta = 1{,}22 \cdot \dfrac{5{,}5 \cdot 10^{-7}}{${m(dM, 3)}} \approx ${sci(theta)}\ \text{rad}§""", """§s = ${sci(theta)} \cdot 3{,}844 \cdot 10^8\ \text{m} \approx ${m(sig(detail))}\ \text{km}§"""),
                background = Bg4.resolution,
            )
        },
    )
}
