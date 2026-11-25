package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import de.funke.app.ui.tr
import kotlin.random.Random

/*
 * Aufgabenbank: selbst ausgedachte Aufgaben (frei verwendbar), die etwas kniffliger und abwechslungsreicher sind
 * als die reinen Generatoren. Jede Vorlage ist fest vorgeschrieben, nur die Zahlen wechseln, sodass man dieselbe
 * Aufgabe mit anderem Ergebnis noch einmal bekommen kann.
 *
 * Jede Vorlage hat eine Stufe und die Fähigkeiten, die man dafür braucht (skills). Innerhalb einer Stufe sind
 * bewusst verschiedene Fähigkeiten gemischt, damit es nicht immer derselbe Aufgabentyp ist.
 * Stufen: Leicht (ein Schritt) · Mittel (zwei Schritte) · Schwer (mehrere Schritte oder ein Trick) ·
 * Experte (mehrere Schritte und eine Idee, auf die man erst kommen muss).
 */
class Template(
    val id: String,
    val topic: Topic,
    val level: Difficulty,
    private val skillsDe: List<String>,
    private val skillsEn: List<String>,
    private val make: (Random) -> Challenge,
) {
    val skills: List<String> get() = if (Lang.german) skillsDe else skillsEn
    fun build(r: Random): Challenge = make(r).copy(templateId = id, difficulty = level)
}

object Bank {
    private val recent = ArrayDeque<String>()

    fun tasks(topic: Topic, level: Difficulty): List<Template> = All.filter { it.topic == topic && it.level == level }

    fun byId(id: String): Template? = All.firstOrNull { it.id == id }

    /** Eine Vorlage, die zuletzt nicht dran war. */
    fun pick(list: List<Template>, r: Random): Challenge {
        val fresh = list.filter { it.id !in recent }.ifEmpty { list }
        val t = fresh.random(r)
        recent.addLast(t.id)
        while (recent.size > 12) recent.removeFirst()
        return t.build(r)
    }

    val count: Int get() = All.size

    private fun T(id: String, topic: Topic, level: Difficulty, de: List<String>, en: List<String>, make: (Random) -> Challenge) =
        Template(id, topic, level, de, en, make)

    private val E = Difficulty.Easy
    private val M = Difficulty.Medium
    private val H = Difficulty.Hard
    private val X = Difficulty.Expert

    val All: List<Template> = listOf(
        // =========================================================================================
        // Physik: Bewegung & Kräfte
        // =========================================================================================
        T("cheetah", Topic.Motion, M, listOf("Geschwindigkeit", "Vergleichen"), listOf("Speed", "Comparing")) { r ->
            val v = listOf(25, 28, 30, 32).random(r)
            task(
                Topic.Motion, M,
                tr("Ein Sprinter läuft 100 m in 10 s. Ein Gepard rennt gleichzeitig los, mit $v m/s. Wie viele Meter ist der Gepard schon weiter, wenn der Sprinter im Ziel ist?",
                    "A sprinter runs 100 m in 10 s. A cheetah starts at the same time at $v m/s. How many meters further is the cheetah when the sprinter finishes?"),
                10 * v - 100, "m",
                listOf(
                    tr("Strecke des Gepards in 10 s: s = v · t = $v · 10 = ${10 * v} m", "Cheetah's distance in 10 s: s = v · t = $v · 10 = ${10 * v} m"),
                    tr("Vorsprung: ${10 * v} − 100 = ${10 * v - 100} m", "Lead: ${10 * v} − 100 = ${10 * v - 100} m"),
                ),
            )
        },
        T("trains", Topic.Motion, H, listOf("Geschwindigkeiten addieren", "Zeit umrechnen"), listOf("Adding speeds", "Converting time")) { r ->
            val (d, v1, v2) = listOf(Triple(150, 40, 60), Triple(200, 60, 90), Triple(100, 70, 80), Triple(250, 70, 80), Triple(180, 40, 50), Triple(140, 60, 80), Triple(300, 80, 120)).random(r)
            val min = d * 60 / (v1 + v2)
            task(
                Topic.Motion, H,
                tr("Zwei Züge starten gleichzeitig in zwei Städten, die $d km auseinanderliegen, und fahren aufeinander zu: einer mit $v1 km/h, der andere mit $v2 km/h. Nach wie vielen Minuten begegnen sie sich?",
                    "Two trains leave two cities $d km apart at the same time and head toward each other, one at $v1 km/h, the other at $v2 km/h. After how many minutes do they meet?"),
                min, "min",
                listOf(
                    tr("Sie kommen sich mit beiden Geschwindigkeiten zusammen näher: $v1 + $v2 = ${v1 + v2} km/h", "They close the gap at both speeds combined: $v1 + $v2 = ${v1 + v2} km/h"),
                    tr("Zeit: t = s : v = $d : ${v1 + v2} h", "Time: t = s : v = $d : ${v1 + v2} h"),
                    tr("In Minuten: $d · 60 : ${v1 + v2} = $min min", "In minutes: $d · 60 : ${v1 + v2} = $min min"),
                ),
            )
        },
        T("fly", Topic.Motion, X, listOf("Geschwindigkeit", "Um die Ecke denken"), listOf("Speed", "Lateral thinking")) { r ->
            val (d, v1, v2, vf) = listOf(listOf(200, 40, 60, 150), listOf(300, 70, 80, 120), listOf(120, 30, 30, 100), listOf(240, 50, 70, 90)).random(r)
            val t2 = d * 2 / (v1 + v2) // halbe Stunden
            task(
                Topic.Motion, X,
                tr("Zwei Radler sind $d km voneinander entfernt und fahren mit $v1 und $v2 km/h aufeinander zu. Eine Fliege fliegt mit $vf km/h vom einen zum anderen und wieder zurück, immer hin und her, bis sich die beiden treffen. Wie viele km fliegt die Fliege insgesamt?",
                    "Two cyclists are $d km apart and ride toward each other at $v1 and $v2 km/h. A fly zips back and forth between them at $vf km/h until they meet. How many km does the fly travel in total?"),
                vf * t2 / 2, "km",
                listOf(
                    tr("Der Trick: Nicht die einzelnen Flüge rechnen, sondern nur, wie lange die Fliege fliegt.", "The trick: don't add up each leg, just work out how long the fly is flying."),
                    tr("Bis zum Treffen: t = $d : ($v1 + $v2) = $d : ${v1 + v2} h", "Until they meet: t = $d : ($v1 + $v2) = $d : ${v1 + v2} h"),
                    tr("Strecke der Fliege: s = v · t = $vf · $d : ${v1 + v2} = ${vf * t2 / 2} km", "Distance of the fly: s = v · t = $vf · $d : ${v1 + v2} = ${vf * t2 / 2} km"),
                ),
            )
        },
        T("braking", Topic.Motion, X, listOf("Einheiten umrechnen", "Reaktionsweg", "Bremsweg"), listOf("Unit conversion", "Reaction distance", "Braking distance")) { r ->
            val (kmh, a) = listOf(36 to 5, 36 to 10, 72 to 5, 72 to 8, 72 to 10, 108 to 5, 108 to 10).random(r)
            val v = kmh * 10 / 36
            val react = v
            val brake = v * v / (2 * a)
            task(
                Topic.Motion, X,
                tr("Ein Auto fährt $kmh km/h. Plötzlich springt ein Reh auf die Straße. Die Fahrerin reagiert nach 1 s und bremst dann mit $a m/s². Wie viele Meter braucht das Auto vom Erschrecken bis zum Stillstand?",
                    "A car drives at $kmh km/h. Suddenly a deer jumps onto the road. The driver reacts after 1 s and then brakes at $a m/s². How many meters does the car need from the scare to a stop?"),
                react + brake, "m",
                listOf(
                    tr("Umrechnen: $kmh km/h : 3,6 = $v m/s", "Convert: $kmh km/h : 3.6 = $v m/s"),
                    tr("Reaktionsweg (1 s ungebremst): s = v · t = $v m", "Reaction distance (1 s unbraked): s = v · t = $v m"),
                    tr("Bremsweg: s = v² : (2 · a) = ${v * v} : ${2 * a} = $brake m", "Braking distance: s = v² : (2 · a) = ${v * v} : ${2 * a} = $brake m"),
                    tr("Zusammen: $react + $brake = ${react + brake} m", "Total: $react + $brake = ${react + brake} m"),
                ),
            )
        },
        T("escalator", Topic.Motion, H, listOf("Geschwindigkeiten addieren", "Brüche"), listOf("Adding speeds", "Fractions")) { r ->
            val (a, b) = listOf(60 to 40, 30 to 60, 20 to 30, 40 to 120, 90 to 45, 36 to 45).random(r)
            val t = a * b / (a + b)
            task(
                Topic.Motion, H,
                tr("Wenn die Rolltreppe steht, läufst du in $a s nach oben. Stehst du still auf der fahrenden Rolltreppe, bist du in $b s oben. Wie lange brauchst du, wenn du auf der fahrenden Rolltreppe läufst?",
                    "On a stopped escalator you walk up in $a s. Standing still on the moving escalator gets you up in $b s. How long does it take if you walk on the moving escalator?"),
                t, "s",
                listOf(
                    tr("Pro Sekunde schaffst du 1/$a der Strecke, die Treppe 1/$b.", "Each second you cover 1/$a of the way, the escalator 1/$b."),
                    tr("Zusammen: 1/$a + 1/$b = ${a + b}/${a * b} der Strecke pro Sekunde", "Together: 1/$a + 1/$b = ${a + b}/${a * b} of the way per second"),
                    tr("Zeit: ${a * b} : ${a + b} = $t s", "Time: ${a * b} : ${a + b} = $t s"),
                ),
            )
        },
        T("cliff", Topic.Motion, X, listOf("Freier Fall", "Waagrechter Wurf"), listOf("Free fall", "Horizontal throw")) { r ->
            val t = r.nextInt(1, 6)
            val h = 5 * t * t
            val vx = 5 * r.nextInt(1, 5)
            task(
                Topic.Motion, X,
                tr("Du wirfst einen Stein von einer $h m hohen Klippe waagrecht mit $vx m/s ins Meer (g = 10 m/s², ohne Luftwiderstand). Wie weit vom Fuß der Klippe entfernt landet er?",
                    "You throw a stone horizontally at $vx m/s from a $h m high cliff into the sea (g = 10 m/s², no air resistance). How far from the foot of the cliff does it land?"),
                vx * t, "m",
                listOf(
                    tr("Nach unten fällt der Stein ganz normal: s = ½ · g · t², also $h = 5 · t²", "Downward the stone simply falls: s = ½ · g · t², so $h = 5 · t²"),
                    tr("t² = ${t * t}, also t = $t s", "t² = ${t * t}, so t = $t s"),
                    tr("Nach vorne fliegt er gleichmäßig: s = v · t = $vx · $t = ${vx * t} m", "Forward it moves evenly: s = v · t = $vx · $t = ${vx * t} m"),
                ),
            )
        },
        T("throwup", Topic.Motion, H, listOf("Senkrechter Wurf", "Energie"), listOf("Vertical throw", "Energy")) { r ->
            val v = 10 * r.nextInt(1, 5)
            task(
                Topic.Motion, H,
                tr("Du wirfst einen Ball mit $v m/s senkrecht nach oben (g = 10 m/s², ohne Luftwiderstand). Wie hoch steigt er?",
                    "You throw a ball straight up at $v m/s (g = 10 m/s², no air resistance). How high does it rise?"),
                v * v / 20, "m",
                listOf(
                    tr("Oben ist die ganze Bewegungsenergie zu Lageenergie geworden: ½ · m · v² = m · g · h", "At the top all kinetic energy has turned into potential energy: ½ · m · v² = m · g · h"),
                    tr("Die Masse kürzt sich weg: h = v² : (2 · g) = ${v * v} : 20", "The mass cancels: h = v² : (2 · g) = ${v * v} : 20"),
                    "h = ${v * v / 20} m",
                ),
            )
        },
        T("signal", Topic.Waves, M, listOf("Lichtgeschwindigkeit", "Große Zahlen"), listOf("Speed of light", "Big numbers")) { r ->
            val t = listOf(100, 200, 500, 1000, 1200).random(r)
            task(
                Topic.Waves, M,
                tr("Ein Funksignal von einer Raumsonde braucht $t s bis zur Erde. Wie viele Millionen km ist die Sonde entfernt? (Licht: 300 000 km/s)",
                    "A radio signal from a space probe takes $t s to reach Earth. How many million km away is the probe? (light: 300,000 km/s)"),
                3 * t / 10, tr("Mio. km", "million km"),
                listOf(
                    tr("Funkwellen sind so schnell wie Licht: s = v · t", "Radio waves travel as fast as light: s = v · t"),
                    "300 000 km/s · $t s = ${300000L * t} km",
                    tr("= ${3 * t / 10} Millionen km", "= ${3 * t / 10} million km"),
                ),
            )
        },

        // =========================================================================================
        // Physik: Energie & Leistung
        // =========================================================================================
        T("powerbank_h", Topic.Energy, E, listOf("Energie und Leistung"), listOf("Energy and power")) { r ->
            val p = listOf(2, 3, 4, 5).random(r)
            val h = r.nextInt(3, 9)
            task(
                Topic.Energy, E,
                tr("Ein Handy-Akku speichert ${p * h} Wh. Das Handy braucht im Schnitt $p W. Wie viele Stunden hält der Akku?",
                    "A phone battery stores ${p * h} Wh. The phone uses $p W on average. How many hours does the battery last?"),
                h, "h",
                listOf(tr("Energie: E = P · t, also t = E : P", "Energy: E = P · t, so t = E : P"), "t = ${p * h} Wh : $p W = $h h"),
            )
        },
        T("gummy", Topic.Energy, H, listOf("Lageenergie", "Einheiten umrechnen"), listOf("Potential energy", "Unit conversion")) { r ->
            val (m, h) = listOf(50 to 15, 60 to 10, 40 to 15, 50 to 20, 60 to 25, 75 to 8).random(r)
            val once = m * 10 * h
            task(
                Topic.Energy, H,
                tr("Ein Gummibärchen hat etwa 30 kJ Energie. Wie oft müsstest du ($m kg) eine $h m hohe Treppe hochsteigen, damit die Lageenergie so groß ist wie ein Gummibärchen? (g = 10 N/kg)",
                    "A gummy bear has about 30 kJ of energy. How many times would you ($m kg) have to climb a $h m staircase for the potential energy to equal one gummy bear? (g = 10 N/kg)"),
                30000 / once, tr("-mal", "times"),
                listOf(
                    tr("Einmal hoch: E = m · g · h = $m · 10 · $h = $once J", "Once up: E = m · g · h = $m · 10 · $h = $once J"),
                    tr("30 kJ = 30000 J", "30 kJ = 30,000 J"),
                    "30000 : $once = ${30000 / once}",
                ),
            )
        },
        T("kettle_cost", Topic.Energy, H, listOf("Elektrische Energie", "Kosten"), listOf("Electric energy", "Cost")) { r ->
            val (p, min, n) = listOf(Triple(2000, 3, 4), Triple(2000, 6, 3), Triple(1500, 4, 5), Triple(3000, 2, 5), Triple(2400, 5, 3)).random(r)
            val whTotal = p * min * n / 60
            val ct = whTotal * 30 / 1000
            task(
                Topic.Energy, H,
                tr("Ein Wasserkocher mit $p W läuft jeden Tag ${n}-mal je $min Minuten. Strom kostet 30 ct pro kWh. Wie viele Cent kostet das pro Tag?",
                    "A $p W kettle runs $n times a day for $min minutes each. Electricity costs 30 ct per kWh. How many cents does that cost per day?"),
                ct, "ct",
                listOf(
                    tr("Laufzeit pro Tag: $n · $min = ${n * min} min", "Running time per day: $n · $min = ${n * min} min"),
                    tr("Energie: E = P · t = $p W · ${n * min}/60 h = $whTotal Wh", "Energy: E = P · t = $p W · ${n * min}/60 h = $whTotal Wh"),
                    tr("$whTotal Wh = ${whTotal / 1000.0} kWh".replace('.', ','), "$whTotal Wh = ${whTotal / 1000.0} kWh"),
                    tr("Kosten: ${whTotal / 1000.0} · 30 ct = $ct ct".replace('.', ','), "Cost: ${whTotal / 1000.0} · 30 ct = $ct ct"),
                ),
            )
        },
        T("coaster", Topic.Energy, H, listOf("Energieerhaltung", "Wurzel ziehen"), listOf("Conservation of energy", "Square roots")) { r ->
            val (dh, v) = listOf(5 to 10, 20 to 20, 45 to 30, 80 to 40).random(r)
            val h1 = dh + 5 * r.nextInt(1, 7)
            val h2 = h1 - dh
            task(
                Topic.Energy, H,
                tr("Ein Achterbahnwagen rollt aus dem Stand von einem $h1 m hohen Hügel. Wie schnell ist er auf der nächsten Kuppe in $h2 m Höhe? (g = 10 m/s², ohne Reibung)",
                    "A roller coaster car starts from rest on a $h1 m hill. How fast is it on the next crest at $h2 m? (g = 10 m/s², no friction)"),
                v, "m/s",
                listOf(
                    tr("Nur der Höhenunterschied zählt: Δh = $h1 − $h2 = $dh m", "Only the height difference matters: Δh = $h1 − $h2 = $dh m"),
                    tr("Lageenergie wird zu Bewegungsenergie: m·g·Δh = ½·m·v²  →  v = √(2·g·Δh)", "Potential energy becomes kinetic: m·g·Δh = ½·m·v²  →  v = √(2·g·Δh)"),
                    "v = √(20 · $dh) = √${20 * dh} = $v m/s",
                ),
            )
        },
        T("stairs_power", Topic.Energy, X, listOf("Hubarbeit", "Leistung", "Vergleichen"), listOf("Work against gravity", "Power", "Comparing")) { r ->
            val (m, h, t) = listOf(Triple(60, 4, 5), Triple(50, 6, 5), Triple(60, 6, 4), Triple(80, 3, 4), Triple(70, 6, 7), Triple(50, 9, 5)).random(r)
            val p = m * 10 * h / t
            val lamps = p / 60
            task(
                Topic.Energy, X,
                tr("Du ($m kg) sprintest in $t s eine $h m hohe Treppe hoch. Wie viele 60-W-Lampen hättest du mit deiner Leistung in dieser Zeit zum Leuchten bringen können? (g = 10 N/kg, nur ganze Lampen)",
                    "You ($m kg) sprint up a $h m staircase in $t s. How many 60 W lamps could your power have lit during that time? (g = 10 N/kg, whole lamps only)"),
                lamps, tr("Lampen", "lamps"),
                listOf(
                    tr("Hubarbeit: W = m · g · h = $m · 10 · $h = ${m * 10 * h} J", "Work: W = m · g · h = $m · 10 · $h = ${m * 10 * h} J"),
                    tr("Leistung: P = W : t = ${m * 10 * h} : $t = $p W", "Power: P = W : t = ${m * 10 * h} : $t = $p W"),
                    tr("$p W : 60 W = ${p / 60.0}".replace('.', ',') + " → $lamps " + "ganze Lampen", "$p W : 60 W = ${p / 60.0} → $lamps whole lamps"),
                ),
            )
        },

        // =========================================================================================
        // Physik: Elektrizität
        // =========================================================================================
        T("fairy_lights", Topic.Electricity, M, listOf("Reihenschaltung", "Spannung teilen"), listOf("Series circuit", "Splitting voltage")) { r ->
            val (u, n) = listOf(230 to 10, 24 to 8, 12 to 4, 230 to 23, 230 to 46, 24 to 12).random(r)
            task(
                Topic.Electricity, M,
                tr("Eine Lichterkette hat $n gleiche Lämpchen in Reihe und hängt an $u V. Welche Spannung liegt an einem Lämpchen?",
                    "A string of lights has $n identical bulbs in series connected to $u V. What voltage is across one bulb?"),
                u / n, "V",
                listOf(
                    tr("Reihenschaltung: Die Spannung teilt sich auf alle Lämpchen auf.", "Series circuit: the voltage is shared among all the bulbs."),
                    "$u V : $n = ${u / n} V",
                ),
            )
        },
        T("fuse", Topic.Electricity, H, listOf("Elektrische Leistung", "Sicherungen"), listOf("Electric power", "Fuses")) { r ->
            val (p, ok) = listOf(2000 to "10 A", 3000 to "16 A", 1200 to "6 A", 5000 to "32 A").random(r)
            choice(
                Topic.Electricity, H,
                tr("Ein Gerät hat $p W und hängt an 230 V. Welche Sicherung braucht der Stromkreis mindestens?", "A $p W device is connected to 230 V. What is the smallest fuse the circuit needs?"),
                ok, listOf("6 A", "10 A", "16 A", "32 A").filter { it != ok },
                listOf(
                    tr("Elektrische Leistung: P = U · I, also I = P : U", "Electric power: P = U · I, so I = P : U"),
                    tr("I = $p : 230 ≈ ${"%.1f".format(p / 230.0).replace('.', ',')} A", "I = $p : 230 ≈ ${"%.1f".format(java.util.Locale.ROOT, p / 230.0)} A"),
                    tr("Die nächstgrößere übliche Sicherung ist $ok.", "The next larger standard fuse is $ok."),
                ),
                r,
            )
        },
        T("parallel_power", Topic.Electricity, X, listOf("Parallelschaltung", "Leistung", "Ohmsches Gesetz"), listOf("Parallel circuit", "Power", "Ohm's law")) { r ->
            val (r1, r2, u) = listOf(Triple(6, 12, 12), Triple(4, 12, 12), Triple(3, 6, 6), Triple(8, 24, 24), Triple(12, 24, 24), Triple(6, 3, 12)).random(r)
            val p1 = u * u / r1
            val p2 = u * u / r2
            task(
                Topic.Electricity, X,
                tr("Zwei Lampen mit $r1 Ω und $r2 Ω sind parallel an $u V angeschlossen. Wie viel Leistung brauchen beide zusammen?",
                    "Two lamps of $r1 Ω and $r2 Ω are connected in parallel to $u V. How much power do they draw together?"),
                p1 + p2, "W",
                listOf(
                    tr("Parallelschaltung: An beiden Lampen liegen die vollen $u V.", "Parallel circuit: each lamp gets the full $u V."),
                    tr("Elektrische Leistung: P = U · I mit I = U : R, also P = U² : R", "Electric power: P = U · I with I = U : R, so P = U² : R"),
                    "P₁ = ${u * u} : $r1 = $p1 W,  P₂ = ${u * u} : $r2 = $p2 W",
                    "$p1 + $p2 = ${p1 + p2} W",
                ),
            )
        },
        T("powerbank", Topic.Electricity, H, listOf("Ladung", "Prozent"), listOf("Charge", "Percent")) { r ->
            val (cap, phone, eff) = listOf(Triple(10000, 4000, 80), Triple(20000, 4000, 80), Triple(15000, 3000, 60), Triple(20000, 5000, 75), Triple(10000, 2500, 75)).random(r)
            val use = cap * eff / 100
            task(
                Topic.Electricity, H,
                tr("Eine Powerbank hat $cap mAh, dein Handy-Akku $phone mAh. Beim Laden kommen nur $eff % an. Wie oft kannst du das Handy ganz aufladen?",
                    "A power bank holds $cap mAh, your phone battery $phone mAh. Only $eff % arrives while charging. How many full charges do you get?"),
                use / phone, tr("-mal", "times"),
                listOf(
                    tr("Nutzbar: $eff % von $cap mAh = $use mAh", "Usable: $eff % of $cap mAh = $use mAh"),
                    "$use : $phone = ${use / phone}",
                ),
            )
        },

        // =========================================================================================
        // Physik: Dichte, Druck & Wärme
        // =========================================================================================
        T("iceberg", Topic.Matter, M, listOf("Prozent", "Schwimmen"), listOf("Percent", "Floating")) { r ->
            val h = 10 * r.nextInt(5, 31)
            task(
                Topic.Matter, M,
                tr("Ein Eisberg ist insgesamt $h m hoch. Etwa 90 % davon liegen unter Wasser. Wie viele Meter ragen heraus? (Stell ihn dir als gleichmäßigen Block vor.)",
                    "An iceberg is $h m tall in total. About 90 % of it is under water. How many meters stick out? (Think of it as a uniform block.)"),
                h / 10, "m",
                listOf(
                    tr("Eis hat etwa 90 % der Dichte von Wasser, darum taucht es zu 90 % ein.", "Ice has about 90 % of water's density, so 90 % of it sinks below."),
                    tr("Oben bleiben 10 %: $h : 10 = ${h / 10} m", "10 % stays above: $h : 10 = ${h / 10} m"),
                ),
            )
        },
        T("submarine", Topic.Matter, E, listOf("Schweredruck"), listOf("Hydrostatic pressure")) { r ->
            val d = 10 * r.nextInt(2, 41)
            task(
                Topic.Matter, E,
                tr("Ein U-Boot taucht $d m tief. Faustregel: alle 10 m Wasser kommt 1 bar Druck dazu. Wie viel bar Wasserdruck lastet auf ihm?",
                    "A submarine dives $d m deep. Rule of thumb: every 10 m of water adds 1 bar of pressure. How many bar of water pressure act on it?"),
                d / 10, "bar",
                listOf(tr("Schweredruck: p = ρ · g · h, also rund 1 bar pro 10 m", "Hydrostatic pressure: p = ρ · g · h, roughly 1 bar per 10 m"), "$d : 10 = ${d / 10} bar"),
            )
        },
        T("wood_float", Topic.Matter, X, listOf("Dichte", "Auftrieb"), listOf("Density", "Buoyancy")) { r ->
            val rho = listOf(2, 4, 5, 6, 7).random(r)
            val names = mapOf(2 to tr("Kork", "cork"), 4 to tr("Fichtenholz", "spruce"), 5 to tr("Kiefernholz", "pine"), 6 to tr("Ahornholz", "maple"), 7 to tr("Eichenholz", "oak"))
            task(
                Topic.Matter, X,
                tr("Ein Würfel aus ${names[rho]} (Dichte etwa 0,$rho g/cm³) hat 1000 cm³ und schwimmt im Wasser. Wie viele cm³ ragen aus dem Wasser?",
                    "A cube of ${names[rho]} (density about 0.$rho g/cm³) has 1000 cm³ and floats in water. How many cm³ stick out of the water?"),
                1000 - rho * 100, "cm³",
                listOf(
                    tr("Masse des Würfels: m = ρ · V = 0,$rho · 1000 = ${rho * 100} g", "Mass of the cube: m = ρ · V = 0.$rho · 1000 = ${rho * 100} g"),
                    tr("Auftrieb: Er taucht so tief ein, bis er ${rho * 100} g Wasser verdrängt, also ${rho * 100} cm³.", "Buoyancy: it sinks until it displaces ${rho * 100} g of water, which is ${rho * 100} cm³."),
                    tr("Heraus ragen: 1000 − ${rho * 100} = ${1000 - rho * 100} cm³", "Sticking out: 1000 − ${rho * 100} = ${1000 - rho * 100} cm³"),
                ),
            )
        },
        T("melt", Topic.Matter, M, listOf("Schmelzwärme"), listOf("Heat of fusion")) { r ->
            val m = r.nextInt(1, 6)
            task(
                Topic.Matter, M,
                tr("Wie viele kJ braucht man, um $m kg Eis von 0 °C zu Wasser von 0 °C zu schmelzen? (Schmelzwärme: 334 kJ pro kg)",
                    "How many kJ does it take to melt $m kg of ice at 0 °C into water at 0 °C? (heat of fusion: 334 kJ per kg)"),
                334 * m, "kJ",
                listOf(
                    tr("Beim Schmelzen bleibt die Temperatur gleich, die Energie geht nur ins Umbauen von Eis zu Wasser.", "While melting the temperature stays the same; the energy only turns ice into water."),
                    "Q = 334 · $m = ${334 * m} kJ",
                ),
            )
        },
        T("mix", Topic.Matter, X, listOf("Wärme", "Mischtemperatur", "Gleichungen"), listOf("Heat", "Mixing temperature", "Equations")) { r ->
            val (m1, t1, m2, t2) = listOf(listOf(2, 80, 3, 20), listOf(1, 90, 4, 15), listOf(3, 60, 1, 20), listOf(2, 70, 2, 30), listOf(1, 100, 3, 20), listOf(4, 50, 1, 25)).random(r)
            val tm = (m1 * t1 + m2 * t2) / (m1 + m2)
            task(
                Topic.Matter, X,
                tr("Du mischst $m1 kg Wasser mit $t1 °C und $m2 kg Wasser mit $t2 °C. Welche Temperatur hat das Gemisch (ohne Wärmeverluste)?",
                    "You mix $m1 kg of water at $t1 °C with $m2 kg of water at $t2 °C. What temperature does the mixture reach (no heat loss)?"),
                tm, "°C",
                listOf(
                    tr("Wärme: Q = c · m · ΔT. Das warme Wasser gibt so viel ab, wie das kalte aufnimmt.", "Heat: Q = c · m · ΔT. The warm water gives off as much as the cold water takes in."),
                    tr("Bei gleichem Stoff: Mischtemperatur = gewichteter Durchschnitt", "Same substance: mixing temperature = weighted average"),
                    "($m1 · $t1 + $m2 · $t2) : ($m1 + $m2) = ${m1 * t1 + m2 * t2} : ${m1 + m2} = $tm °C",
                ),
            )
        },

        // =========================================================================================
        // Physik: Wellen
        // =========================================================================================
        T("thunder_rule", Topic.Waves, E, listOf("Schallgeschwindigkeit"), listOf("Speed of sound")) { r ->
            val km = r.nextInt(1, 8)
            task(
                Topic.Waves, E,
                tr("Zwischen Blitz und Donner zählst du ${3 * km} Sekunden. Faustregel: Schall braucht etwa 3 s pro km. Wie viele km ist das Gewitter entfernt?",
                    "You count ${3 * km} seconds between lightning and thunder. Rule of thumb: sound takes about 3 s per km. How many km away is the storm?"),
                km, "km",
                listOf(tr("Schall: rund 340 m/s, also etwa 1 km in 3 s.", "Sound: about 340 m/s, so roughly 1 km in 3 s."), "${3 * km} : 3 = $km km"),
            )
        },
        T("octave", Topic.Waves, M, listOf("Frequenz", "Verdoppeln"), listOf("Frequency", "Doubling")) { r ->
            val n = r.nextInt(1, 4)
            var f = 440
            repeat(n) { f *= 2 }
            task(
                Topic.Waves, M,
                tr("Der Kammerton A hat 440 Hz. Eine Oktave höher schwingt ein Ton doppelt so schnell. Welche Frequenz hat das A, das $n Oktave${if (n > 1) "n" else ""} höher liegt?",
                    "Concert A is 440 Hz. One octave up, a tone vibrates twice as fast. What frequency does the A $n octave${if (n > 1) "s" else ""} higher have?"),
                f, "Hz",
                listOf(tr("Pro Oktave verdoppelt sich die Frequenz: f = 440 · 2^$n", "Each octave doubles the frequency: f = 440 · 2^$n"), "f = $f Hz"),
            )
        },
        T("sonar", Topic.Waves, H, listOf("Echo", "Schall in Wasser"), listOf("Echo", "Sound in water")) { r ->
            val t = listOf(1, 2, 3, 4, 6, 8).random(r)
            task(
                Topic.Waves, H,
                tr("Ein Schiff schickt einen Ton zum Meeresboden. Das Echo kommt nach $t s zurück. Wie tief ist das Meer dort? (Schall im Wasser: 1500 m/s)",
                    "A ship sends a ping to the seabed. The echo returns after $t s. How deep is the sea there? (sound in water: 1500 m/s)"),
                750 * t, "m",
                listOf(
                    tr("Echo: Der Schall muss hin und zurück.", "Echo: the sound travels there and back."),
                    tr("Gesamtweg: s = v · t = 1500 · $t = ${1500 * t} m", "Total path: s = v · t = 1500 · $t = ${1500 * t} m"),
                    tr("Tiefe = die Hälfte: ${750 * t} m", "Depth = half of that: ${750 * t} m"),
                ),
            )
        },

        // =========================================================================================
        // Mathe
        // =========================================================================================
        T("week_minutes", Topic.Mental, E, listOf("Einheiten", "Kopfrechnen"), listOf("Units", "Mental math")) { r ->
            when (r.nextInt(3)) {
                0 -> task(Topic.Mental, E, tr("Wie viele Minuten hat ein Tag?", "How many minutes are in a day?"), 1440, "min",
                    listOf("24 · 60", tr("= 1440 min", "= 1440 min")))
                1 -> task(Topic.Mental, E, tr("Wie viele Stunden hat eine Woche?", "How many hours are in a week?"), 168, "h",
                    listOf("7 · 24", "= 168 h"))
                else -> task(Topic.Mental, E, tr("Wie viele Sekunden hat eine Stunde?", "How many seconds are in an hour?"), 3600, "s",
                    listOf("60 · 60", "= 3600 s"))
            }
        },
        T("book_days", Topic.Mental, M, listOf("Teilen mit Rest", "Aufrunden"), listOf("Division with remainder", "Rounding up")) { r ->
            val pages = 10 * r.nextInt(15, 45)
            val per = listOf(12, 15, 18, 22, 25, 35).random(r)
            val days = (pages + per - 1) / per
            task(
                Topic.Mental, M,
                tr("Dein Buch hat $pages Seiten. Du liest jeden Tag $per Seiten. Am wievielten Tag liest du die letzte Seite?",
                    "Your book has $pages pages. You read $per pages every day. On which day do you read the last page?"),
                days,
                steps = listOf(
                    tr("$pages : $per = ${pages / per} Rest ${pages % per}", "$pages : $per = ${pages / per} remainder ${pages % per}"),
                    if (pages % per == 0) tr("Kein Rest: Am Tag ${pages / per} bist du genau fertig.", "No remainder: you finish exactly on day ${pages / per}.")
                    else tr("Der Rest braucht noch einen Tag: ${pages / per} + 1 = $days", "The rest needs one more day: ${pages / per} + 1 = $days"),
                ),
            )
        },
        T("gauss", Topic.Mental, H, listOf("Geschickt rechnen", "Paare bilden"), listOf("Clever arithmetic", "Pairing")) { r ->
            val n = listOf(20, 40, 50, 60, 100, 200).random(r)
            task(
                Topic.Mental, H,
                tr("Was ergibt 1 + 2 + 3 + … + $n?", "What is 1 + 2 + 3 + … + $n?"),
                n * (n + 1) / 2,
                steps = listOf(
                    tr("Trick von Gauß: Bilde Paare von außen nach innen: 1 + $n, 2 + ${n - 1}, … Jedes Paar ergibt ${n + 1}.", "Gauss's trick: pair from the outside in: 1 + $n, 2 + ${n - 1}, … Each pair makes ${n + 1}."),
                    tr("Es gibt ${n / 2} Paare: ${n / 2} · ${n + 1}", "There are ${n / 2} pairs: ${n / 2} · ${n + 1}"),
                    "= ${n * (n + 1) / 2}",
                ),
            )
        },
        T("zeros", Topic.Mental, X, listOf("Primfaktoren", "Zählen"), listOf("Prime factors", "Counting")) { r ->
            val n = listOf(10, 15, 20, 25, 30, 50, 100).random(r)
            val z = n / 5 + n / 25
            task(
                Topic.Mental, X,
                tr("Mit wie vielen Nullen endet $n! (also 1 · 2 · 3 · … · $n)?", "How many zeros does $n! (that is 1 · 2 · 3 · … · $n) end with?"),
                z,
                steps = listOf(
                    tr("Jede Null am Ende kommt von einem Faktor 10 = 2 · 5. Zweien gibt es genug, also zählen wir die Fünfen.", "Each trailing zero comes from a factor 10 = 2 · 5. There are plenty of 2s, so count the 5s."),
                    tr("Vielfache von 5 bis $n: ${n / 5}", "Multiples of 5 up to $n: ${n / 5}"),
                    tr("Vielfache von 25 bringen eine zweite Fünf: ${n / 25}", "Multiples of 25 add a second 5: ${n / 25}"),
                    "${n / 5} + ${n / 25} = $z",
                ),
            )
        },
        T("rect", Topic.Equations, H, listOf("Gleichungen aufstellen", "Umfang und Fläche"), listOf("Setting up equations", "Perimeter and area")) { r ->
            val b = r.nextInt(2, 13)
            val k = listOf(2, 3, 4).random(r)
            val u = 2 * (b + k * b)
            task(
                Topic.Equations, H,
                tr("Ein Rechteck ist ${k}-mal so lang wie breit und hat einen Umfang von $u cm. Wie groß ist seine Fläche?",
                    "A rectangle is $k times as long as it is wide and has a perimeter of $u cm. What is its area?"),
                k * b * b, "cm²",
                listOf(
                    tr("Breite x, Länge ${k}x. Umfang: 2 · (x + ${k}x) = ${2 * (k + 1)}x = $u", "Width x, length ${k}x. Perimeter: 2 · (x + ${k}x) = ${2 * (k + 1)}x = $u"),
                    tr("x = $u : ${2 * (k + 1)} = $b cm, Länge ${k * b} cm", "x = $u : ${2 * (k + 1)} = $b cm, length ${k * b} cm"),
                    tr("Fläche: $b · ${k * b} = ${k * b * b} cm²", "Area: $b · ${k * b} = ${k * b * b} cm²"),
                ),
            )
        },
        T("ladder", Topic.Equations, M, listOf("Pythagoras", "Wurzel ziehen"), listOf("Pythagoras", "Square roots")) { r ->
            val (a, b, c) = listOf(Triple(3, 4, 5), Triple(6, 8, 10), Triple(5, 12, 13), Triple(8, 15, 17), Triple(9, 12, 15)).random(r)
            task(
                Topic.Equations, M,
                tr("Eine $c m lange Leiter lehnt an einer Wand. Ihr Fuß steht $a m von der Wand weg. In welcher Höhe berührt sie die Wand?",
                    "A $c m ladder leans against a wall. Its foot is $a m from the wall. At what height does it touch the wall?"),
                b, "m",
                listOf(
                    tr("Wand, Boden und Leiter bilden ein rechtwinkliges Dreieck: a² + b² = c² (Satz des Pythagoras)", "Wall, floor and ladder form a right triangle: a² + b² = c² (Pythagoras)"),
                    "b² = $c² − $a² = ${c * c} − ${a * a} = ${b * b}",
                    "b = √${b * b} = $b m",
                ),
            )
        },
        T("diagonals", Topic.Mental, X, listOf("Kombinatorik", "Formel herleiten"), listOf("Combinatorics", "Deriving a formula")) { r ->
            val n = r.nextInt(6, 16)
            task(
                Topic.Mental, X,
                tr("Wie viele Diagonalen hat ein $n-Eck?", "How many diagonals does a polygon with $n corners have?"),
                n * (n - 3) / 2,
                steps = listOf(
                    tr("Von jeder Ecke geht eine Diagonale zu jeder Ecke außer sich selbst und ihren 2 Nachbarn: ${n - 3} Stück.", "From each corner a diagonal goes to every corner except itself and its 2 neighbors: ${n - 3} of them."),
                    tr("$n Ecken · ${n - 3} = ${n * (n - 3)}, aber jede Diagonale ist so doppelt gezählt.", "$n corners · ${n - 3} = ${n * (n - 3)}, but each diagonal is counted twice."),
                    "${n * (n - 3)} : 2 = ${n * (n - 3) / 2}",
                ),
            )
        },
        T("squares_grid", Topic.Mental, X, listOf("Zählen mit System", "Quadratzahlen"), listOf("Systematic counting", "Square numbers")) { r ->
            val n = r.nextInt(3, 9)
            val total = (1..n).sumOf { it * it }
            task(
                Topic.Mental, X,
                tr("Wie viele Quadrate (in allen Größen) findest du auf einem $n × $n Karofeld?", "How many squares (of all sizes) are there on an $n × $n grid?"),
                total,
                steps = listOf(
                    tr("1×1-Quadrate: $n · $n = ${n * n}. 2×2: ${(n - 1) * (n - 1)}. Und so weiter bis $n×$n: 1.", "1×1 squares: $n · $n = ${n * n}. 2×2: ${(n - 1) * (n - 1)}. And so on up to $n×$n: 1."),
                    (n downTo 1).joinToString(" + ") { "${it * it}" },
                    "= $total",
                ),
            )
        },
        T("up_down", Topic.Percent, H, listOf("Prozent", "Verketten"), listOf("Percent", "Chaining")) { r ->
            val p = listOf(10, 20, 30, 40, 50).random(r)
            val res = 100 - p * p / 100
            task(
                Topic.Percent, H,
                tr("Ein Preis steigt erst um $p % und sinkt dann um $p %. Wie viel Prozent des Anfangspreises kostet die Sache am Ende?",
                    "A price first rises by $p % and then drops by $p %. What percent of the original price does it cost in the end?"),
                res, "%",
                listOf(
                    tr("Steigen um $p % heißt mal ${(100 + p) / 100.0}, fallen um $p % heißt mal ${(100 - p) / 100.0}.".replace('.', ','), "Rising by $p % means times ${(100 + p) / 100.0}, falling by $p % means times ${(100 - p) / 100.0}."),
                    tr("${(100 + p) / 100.0} · ${(100 - p) / 100.0} = ${res / 100.0}".replace('.', ','), "${(100 + p) / 100.0} · ${(100 - p) / 100.0} = ${res / 100.0}"),
                    tr("Also $res % des Anfangspreises. Die $p % beim Fallen werden vom höheren Preis genommen.", "So $res % of the original. The $p % drop is taken from the higher price."),
                ),
            )
        },
        T("bacteria", Topic.Sequences, X, listOf("Verdoppeln", "Zweierpotenzen"), listOf("Doubling", "Powers of two")) { r ->
            val (target, k) = listOf(100 to 7, 1000 to 10, 500 to 9, 10000 to 14, 1000000 to 20).random(r)
            val per = listOf(15, 20, 30).random(r)
            task(
                Topic.Sequences, X,
                tr("Eine Bakterie teilt sich alle $per Minuten in zwei. Nach wie vielen Minuten sind es aus einer Bakterie zum ersten Mal mehr als $target geworden?",
                    "A bacterium splits in two every $per minutes. After how many minutes are there more than $target for the first time, starting from one?"),
                k * per, "min",
                listOf(
                    tr("Nach n Teilungen sind es 2^n Bakterien.", "After n divisions there are 2^n bacteria."),
                    tr("Kleinste Zweierpotenz über $target: 2^$k = ${1L shl k}", "Smallest power of two above $target: 2^$k = ${1L shl k}"),
                    tr("$k Teilungen · $per min = ${k * per} min", "$k divisions · $per min = ${k * per} min"),
                ),
            )
        },
        T("clock_angle", Topic.Mental, H, listOf("Winkel", "Uhr"), listOf("Angles", "Clock")) { r ->
            val h = r.nextInt(1, 12)
            val m = listOf(0, 10, 20, 30, 40, 50).random(r)
            val raw = kotlin.math.abs(60 * h - 11 * m) / 2 // |30h − 5,5m|
            val a = if (raw > 180) 360 - raw else raw
            task(
                Topic.Mental, H,
                tr("Welchen Winkel schließen Stunden- und Minutenzeiger um $h:${m.toString().padStart(2, '0')} Uhr ein? (der kleinere Winkel)",
                    "What angle do the hour and minute hands form at $h:${m.toString().padStart(2, '0')}? (the smaller angle)"),
                a, "°",
                listOf(
                    tr("Minutenzeiger: 6° pro Minute → ${6 * m}°", "Minute hand: 6° per minute → ${6 * m}°"),
                    tr("Stundenzeiger: 30° pro Stunde + 0,5° pro Minute → ${30 * h + m / 2}°", "Hour hand: 30° per hour + 0.5° per minute → ${30 * h + m / 2}°"),
                    tr("Unterschied: $raw°" + if (raw > 180) ", der kleinere Winkel ist 360 − $raw = $a°" else "", "Difference: $raw°" + if (raw > 180) ", the smaller angle is 360 − $raw = $a°" else ""),
                ),
            )
        },
        T("dice7", Topic.Percent, X, listOf("Wahrscheinlichkeit", "Zählen"), listOf("Probability", "Counting")) { r ->
            val sum = listOf(7, 6, 8, 2, 12, 10).random(r)
            val ways = 6 - kotlin.math.abs(7 - sum)
            fun frac(n: Int): String { var a = n; var b = 36; var g = a; var hh = b; while (hh != 0) { val t = g % hh; g = hh; hh = t }; a /= g; b /= g; return "$a/$b" }
            val ok = frac(ways)
            choice(
                Topic.Percent, X,
                tr("Du wirfst zwei Würfel. Wie wahrscheinlich ist die Augensumme $sum?", "You roll two dice. How likely is a total of $sum?"),
                ok, listOf("1/6", "1/12", "1/36", "5/36", "1/9", "1/18").filter { it != ok }.shuffled(r).take(3),
                listOf(
                    tr("Es gibt 6 · 6 = 36 gleich wahrscheinliche Würfe.", "There are 6 · 6 = 36 equally likely rolls."),
                    tr("Summe $sum geht auf $ways Arten (z. B. " + (1..6).mapNotNull { a -> val b = sum - a; if (b in 1..6) "$a+$b" else null }.joinToString(", ") + ").",
                        "A total of $sum happens in $ways ways (e.g. " + (1..6).mapNotNull { a -> val b = sum - a; if (b in 1..6) "$a+$b" else null }.joinToString(", ") + ")."),
                    "$ways/36 = $ok",
                ),
                r,
            )
        },
        T("avg_speed", Topic.Word, X, listOf("Durchschnitt", "Falle erkennen"), listOf("Average", "Spotting the trap")) { r ->
            val (v1, v2) = listOf(60 to 40, 30 to 60, 20 to 30, 40 to 120, 90 to 45).random(r)
            val avg = 2 * v1 * v2 / (v1 + v2)
            task(
                Topic.Word, X,
                tr("Du fährst eine Strecke hin mit $v1 km/h und auf demselben Weg zurück mit $v2 km/h. Wie groß ist deine Durchschnittsgeschwindigkeit?",
                    "You drive somewhere at $v1 km/h and back the same way at $v2 km/h. What is your average speed?"),
                avg, "km/h",
                listOf(
                    tr("Falle: Es ist nicht einfach (${v1} + ${v2}) : 2, weil du auf der langsamen Fahrt länger unterwegs bist.", "Trap: it isn't simply ($v1 + $v2) : 2, because you spend longer on the slow leg."),
                    tr("Nimm z. B. ${v1 * v2} km pro Richtung: hin ${v2} h, zurück ${v1} h.", "Take e.g. ${v1 * v2} km each way: there ${v2} h, back ${v1} h."),
                    tr("v = s : t = ${2 * v1 * v2} km : ${v1 + v2} h = $avg km/h", "v = s : t = ${2 * v1 * v2} km : ${v1 + v2} h = $avg km/h"),
                ),
            )
        },
        T("books_twice", Topic.Word, M, listOf("Gleichungen aufstellen"), listOf("Setting up equations")) { r ->
            val t = r.nextInt(5, 30)
            val k = listOf(2, 3, 4).random(r)
            task(
                Topic.Word, M,
                tr("Lisa hat ${k}-mal so viele Bücher wie Tom. Zusammen haben sie ${t * (k + 1)} Bücher. Wie viele hat Tom?",
                    "Lisa has $k times as many books as Tom. Together they have ${t * (k + 1)} books. How many does Tom have?"),
                t,
                steps = listOf(
                    tr("Tom: x, Lisa: ${k}x. Zusammen: ${k + 1}x = ${t * (k + 1)}", "Tom: x, Lisa: ${k}x. Together: ${k + 1}x = ${t * (k + 1)}"),
                    "x = ${t * (k + 1)} : ${k + 1} = $t",
                ),
            )
        },
        T("factorial_seq", Topic.Sequences, X, listOf("Muster erkennen"), listOf("Spotting patterns")) { r ->
            when (r.nextInt(3)) {
                0 -> task(Topic.Sequences, X, tr("Welche Zahl kommt als Nächstes?", "Which number comes next?") + "\n1, 2, 6, 24, 120, ?", 720,
                    steps = listOf(tr("Mal 2, mal 3, mal 4, mal 5 … (Fakultäten)", "Times 2, times 3, times 4, times 5 … (factorials)"), "120 · 6 = 720"))
                1 -> task(Topic.Sequences, X, tr("Welche Zahl kommt als Nächstes?", "Which number comes next?") + "\n1, 3, 6, 10, 15, 21, ?", 28,
                    steps = listOf(tr("Die Abstände wachsen um 1: +2, +3, +4, +5, +6 (Dreieckszahlen)", "The gaps grow by 1: +2, +3, +4, +5, +6 (triangular numbers)"), "21 + 7 = 28"))
                else -> {
                    val primes = listOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43)
                    val s = r.nextInt(0, primes.size - 6)
                    val seq = primes.subList(s, s + 5)
                    task(Topic.Sequences, X, tr("Welche Zahl kommt als Nächstes?", "Which number comes next?") + "\n" + seq.joinToString(", ") + ", ?", primes[s + 5],
                        steps = listOf(tr("Das sind Primzahlen: nur durch 1 und sich selbst teilbar.", "These are primes: divisible only by 1 and themselves."), tr("Die nächste Primzahl nach ${seq.last()} ist ${primes[s + 5]}.", "The next prime after ${seq.last()} is ${primes[s + 5]}.")))
                }
            }
        },
        T("max_rect", Topic.Calculus, X, listOf("Extremwerte", "Ableiten"), listOf("Optimization", "Derivatives")) { r ->
            val u = 4 * r.nextInt(5, 21)
            val s = u / 4
            task(
                Topic.Calculus, X,
                tr("Mit $u m Zaun willst du ein rechteckiges Beet einzäunen. Wie groß kann die Fläche höchstens werden?",
                    "You have $u m of fence for a rectangular garden bed. What is the largest possible area?"),
                s * s, "m²",
                listOf(
                    tr("Seiten x und ${u / 2} − x. Fläche: A(x) = x · (${u / 2} − x) = ${u / 2}x − x²", "Sides x and ${u / 2} − x. Area: A(x) = x · (${u / 2} − x) = ${u / 2}x − x²"),
                    tr("Ableiten und null setzen: A′(x) = ${u / 2} − 2x = 0  →  x = $s", "Differentiate and set to zero: A′(x) = ${u / 2} − 2x = 0  →  x = $s"),
                    tr("Ein Quadrat: $s · $s = ${s * s} m²", "A square: $s · $s = ${s * s} m²"),
                ),
            )
        },
        T("vertex", Topic.Calculus, H, listOf("Ableiten", "Tiefpunkt"), listOf("Derivatives", "Minimum")) { r ->
            val x0 = r.nextInt(1, 9)
            val c = r.nextInt(1, 20)
            task(
                Topic.Calculus, H,
                "f(x) = x² − ${2 * x0}x + $c\n" + tr("An welcher Stelle x hat f seinen Tiefpunkt?", "At which x does f have its minimum?"),
                x0,
                steps = listOf(
                    tr("Ableiten: f′(x) = 2x − ${2 * x0}", "Differentiate: f′(x) = 2x − ${2 * x0}"),
                    tr("Null setzen: 2x = ${2 * x0}  →  x = $x0", "Set to zero: 2x = ${2 * x0}  →  x = $x0"),
                ),
            )
        },
        T("area_parabola", Topic.Calculus, X, listOf("Integral", "Stammfunktion"), listOf("Integral", "Antiderivative")) { r ->
            val k = listOf(3, 6, 9).random(r)
            task(
                Topic.Calculus, X,
                tr("Wie groß ist die Fläche zwischen der Parabel y = x² und der x-Achse von x = 0 bis x = $k?", "What is the area between the parabola y = x² and the x-axis from x = 0 to x = $k?"),
                k * k * k / 3,
                steps = listOf(
                    tr("Fläche = ∫₀^$k x² dx. Stammfunktion: F(x) = x³ : 3", "Area = ∫₀^$k x² dx. Antiderivative: F(x) = x³ : 3"),
                    "F($k) − F(0) = ${k * k * k} : 3 − 0",
                    "= ${k * k * k / 3}",
                ),
            )
        },

        // =========================================================================================
        // Logik
        // =========================================================================================
        T("bat_ball", Topic.Logic, H, listOf("Falle erkennen", "Gleichungen"), listOf("Spotting the trap", "Equations")) { r ->
            val (total, diff) = listOf(110 to 100, 120 to 100, 150 to 100, 70 to 50, 230 to 200).random(r)
            val ball = (total - diff) / 2
            task(
                Topic.Logic, H,
                tr("Ein Schläger und ein Ball kosten zusammen ${total / 100},${(total % 100).toString().padStart(2, '0')} €. Der Schläger kostet ${diff / 100},${(diff % 100).toString().padStart(2, '0')} € mehr als der Ball. Wie viele Cent kostet der Ball?",
                    "A bat and a ball cost €${total / 100}.${(total % 100).toString().padStart(2, '0')} together. The bat costs €${diff / 100}.${(diff % 100).toString().padStart(2, '0')} more than the ball. How many cents is the ball?"),
                ball, "ct",
                listOf(
                    tr("Die schnelle Antwort ${total - diff} ct ist falsch: Dann kostet der Schläger ${total - diff + diff} ct und zusammen wären es ${2 * (total - diff) + diff} ct.", "The quick answer ${total - diff} ct is wrong: then the bat would cost ${total - diff + diff} ct, ${2 * (total - diff) + diff} ct in total."),
                    tr("Ball x, Schläger x + $diff: 2x + $diff = $total", "Ball x, bat x + $diff: 2x + $diff = $total"),
                    "x = ${total - diff} : 2 = $ball ct",
                ),
            )
        },
        T("machines", Topic.Logic, M, listOf("Falle erkennen", "Raten"), listOf("Spotting the trap", "Rates")) { r ->
            val n = r.nextInt(3, 10)
            val big = listOf(50, 100, 200).random(r)
            task(
                Topic.Logic, M,
                tr("$n Maschinen bauen $n Teile in $n Minuten. Wie viele Minuten brauchen $big Maschinen für $big Teile?",
                    "$n machines make $n parts in $n minutes. How many minutes do $big machines need for $big parts?"),
                n, "min",
                listOf(
                    tr("Jede Maschine baut ein Teil in $n Minuten.", "Each machine makes one part in $n minutes."),
                    tr("$big Maschinen bauen gleichzeitig $big Teile, also auch in $n Minuten.", "$big machines build $big parts at once, so also in $n minutes."),
                ),
            )
        },
        T("lily", Topic.Logic, M, listOf("Rückwärts denken", "Verdoppeln"), listOf("Thinking backwards", "Doubling")) { r ->
            val n = r.nextInt(20, 60)
            val q = listOf(2, 4).random(r)
            val back = if (q == 2) 1 else 2
            task(
                Topic.Logic, M,
                tr("Seerosen verdoppeln ihre Fläche jeden Tag. Nach $n Tagen ist der Teich ganz bedeckt. Nach wie vielen Tagen war ${if (q == 2) "die Hälfte" else "ein Viertel"} bedeckt?",
                    "Water lilies double their area every day. After $n days the pond is fully covered. After how many days was ${if (q == 2) "half" else "a quarter"} of it covered?"),
                n - back,
                steps = listOf(
                    tr("Rückwärts denken: Einen Tag vorher war es die Hälfte.", "Think backwards: one day earlier it was half."),
                    if (q == 2) "$n − 1 = ${n - 1}" else tr("Ein Viertel ist noch einen Tag früher: $n − 2 = ${n - 2}", "A quarter is one more day earlier: $n − 2 = ${n - 2}"),
                ),
            )
        },
        T("monty", Topic.Logic, X, listOf("Wahrscheinlichkeit", "Gegen die Intuition"), listOf("Probability", "Counterintuitive")) { r ->
            choice(
                Topic.Logic, X,
                tr("In einer Show gibt es 3 Türen, hinter einer ist ein Auto. Du wählst Tür 1. Der Moderator, der alles weiß, öffnet Tür 3: eine Ziege. Er fragt, ob du zu Tür 2 wechseln willst. Was ist richtig?",
                    "A game show has 3 doors, a car behind one. You pick door 1. The host, who knows everything, opens door 3: a goat. He asks if you want to switch to door 2. What's right?"),
                tr("Wechseln: Gewinnchance 2/3", "Switch: 2/3 chance to win"),
                listOf(tr("Egal, beides 1/2", "Doesn't matter, both 1/2"), tr("Bleiben: Gewinnchance 2/3", "Stay: 2/3 chance to win"), tr("Wechseln: Gewinnchance 1/3", "Switch: 1/3 chance to win")),
                listOf(
                    tr("Deine erste Wahl trifft nur mit 1/3 das Auto. Mit 2/3 steht es hinter einer der anderen beiden Türen.", "Your first pick hits the car only 1/3 of the time. 2/3 of the time it's behind one of the other two doors."),
                    tr("Der Moderator öffnet absichtlich die Ziegen-Tür unter diesen beiden. Die ganzen 2/3 wandern so auf Tür 2.", "The host deliberately opens the goat door of those two. So the whole 2/3 moves onto door 2."),
                ),
                r,
            )
        },
        T("handshake_rev", Topic.Logic, X, listOf("Rückwärts rechnen", "Paare zählen"), listOf("Working backwards", "Counting pairs")) { r ->
            val n = r.nextInt(5, 16)
            val hs = n * (n - 1) / 2
            task(
                Topic.Logic, X,
                tr("Auf einer Party gibt jeder jedem genau einmal die Hand. Insgesamt werden $hs Hände geschüttelt. Wie viele Gäste sind da?",
                    "At a party everyone shakes hands with everyone else exactly once. There are $hs handshakes in total. How many guests are there?"),
                n,
                steps = listOf(
                    tr("Bei n Gästen gibt es n · (n − 1) : 2 Handschläge.", "With n guests there are n · (n − 1) : 2 handshakes."),
                    tr("Also n · (n − 1) = ${2 * hs}. Zwei aufeinanderfolgende Zahlen mit diesem Produkt: $n · ${n - 1}", "So n · (n − 1) = ${2 * hs}. Two consecutive numbers with that product: $n · ${n - 1}"),
                    tr("Es sind $n Gäste.", "There are $n guests."),
                ),
            )
        },
    )
}
