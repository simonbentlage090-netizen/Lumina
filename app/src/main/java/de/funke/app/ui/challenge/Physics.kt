package de.funke.app.ui.challenge

import de.funke.app.ui.tr
import kotlin.random.Random

/*
 * Physik-Aufgaben, selbst erzeugt (frei verwendbar). Alle Ergebnisse sind ganze Zahlen.
 * Wir rechnen mit g = 10 m/s² (bzw. 10 N/kg) und Schall mit 340 m/s, damit man im Kopf rechnen kann.
 * Die Formeln in den Lösungswegen sind so geschrieben, dass Concepts.kt die passende Formelkarte findet.
 */
object Physics {

    // ---------------------------------------------------------------------------------------------
    // Bewegung & Kräfte
    // ---------------------------------------------------------------------------------------------

    fun motion(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(3)) {
            0 -> {
                val v = 10 * r.nextInt(3, 13)
                val t = r.nextInt(2, 5)
                task(
                    Topic.Motion, d,
                    tr("Ein Zug fährt ${v * t} km in $t Stunden. Wie schnell ist er im Schnitt?", "A train travels ${v * t} km in $t hours. What is its average speed?"),
                    v, "km/h",
                    listOf(tr("Geschwindigkeit: v = s : t", "Speed: v = s : t"), "v = ${v * t} km : $t h = $v km/h"),
                )
            }
            1 -> {
                val v = listOf(12, 15, 18, 20, 24).random(r)
                val t = r.nextInt(2, 5)
                task(
                    Topic.Motion, d,
                    tr("Eine Radfahrerin fährt $t Stunden lang mit $v km/h. Wie weit kommt sie?", "A cyclist rides for $t hours at $v km/h. How far does she get?"),
                    v * t, "km",
                    listOf(tr("Strecke: s = v · t", "Distance: s = v · t"), "s = $v km/h · $t h = ${v * t} km"),
                )
            }
            else -> {
                val m = r.nextInt(2, 40)
                task(
                    Topic.Motion, d,
                    tr("Welche Gewichtskraft wirkt auf einen Rucksack mit $m kg? (g = 10 N/kg)", "What weight force acts on a backpack of $m kg? (g = 10 N/kg)"),
                    10 * m, "N",
                    listOf(tr("Gewichtskraft: F = m · g", "Weight: F = m · g"), "F = $m kg · 10 N/kg = ${10 * m} N"),
                )
            }
        }
        Difficulty.Medium -> when (r.nextInt(3)) {
            0 -> {
                val a = r.nextInt(2, 6)
                val t = r.nextInt(2, 9)
                task(
                    Topic.Motion, d,
                    tr("Ein Roller beschleunigt gleichmäßig in $t s von 0 auf ${a * t} m/s. Wie groß ist die Beschleunigung?", "A scooter speeds up evenly from 0 to ${a * t} m/s in $t s. What is its acceleration?"),
                    a, "m/s²",
                    listOf(tr("Beschleunigung: a = Δv : Δt", "Acceleration: a = Δv : Δt"), "a = ${a * t} m/s : $t s = $a m/s²"),
                )
            }
            1 -> {
                val m = 100 * r.nextInt(5, 16)
                val a = r.nextInt(2, 5)
                task(
                    Topic.Motion, d,
                    tr("Ein Auto mit $m kg beschleunigt mit $a m/s². Welche Kraft braucht es dafür?", "A $m kg car accelerates at $a m/s². What force does it need?"),
                    m * a, "N",
                    listOf(tr("Grundgleichung der Mechanik: F = m · a", "Newton's second law: F = m · a"), "F = $m kg · $a m/s² = ${m * a} N"),
                )
            }
            else -> {
                val k = r.nextInt(1, 9)
                val kmh = 18 * k
                task(
                    Topic.Motion, d,
                    tr("Wie viele m/s sind $kmh km/h?", "How many m/s are $kmh km/h?"),
                    5 * k, "m/s",
                    listOf(
                        tr("1 m/s = 3,6 km/h, also durch 3,6 teilen.", "1 m/s = 3.6 km/h, so divide by 3.6."),
                        tr("Trick: $kmh : 18 = $k, und 18 km/h sind genau 5 m/s.", "Trick: $kmh : 18 = $k, and 18 km/h is exactly 5 m/s."),
                        "$k · 5 m/s = ${5 * k} m/s",
                    ),
                )
            }
        }
        else -> when (r.nextInt(4)) {
            0 -> {
                val t = r.nextInt(2, 7)
                task(
                    Topic.Motion, d,
                    tr("Ein Ball fällt $t s lang frei (g = 10 m/s², ohne Luftwiderstand). Wie tief fällt er?", "A ball falls freely for $t s (g = 10 m/s², no air resistance). How far does it fall?"),
                    5 * t * t, "m",
                    listOf(tr("Fallweg: s = ½ · g · t²", "Falling distance: s = ½ · g · t²"), "t² = ${t * t}", "s = ½ · 10 · ${t * t} = ${5 * t * t} m"),
                )
            }
            1 -> {
                val m = r.nextInt(2, 9)
                val dt = listOf(2, 3, 6).random(r)
                val a = r.nextInt(2, 7)
                val dv = a * dt
                task(
                    Topic.Motion, d,
                    tr("Ein Wagen mit $m kg wird in $dt s gleichmäßig von 0 auf $dv m/s beschleunigt. Wie groß ist die Kraft?", "A $m kg cart speeds up evenly from 0 to $dv m/s in $dt s. What force does it take?"),
                    m * a, "N",
                    listOf(
                        tr("Beschleunigung: a = Δv : Δt = $dv : $dt = $a m/s²", "Acceleration: a = Δv : Δt = $dv : $dt = $a m/s²"),
                        tr("Kraft: F = m · a", "Force: F = m · a"),
                        "F = $m kg · $a m/s² = ${m * a} N",
                    ),
                )
            }
            2 -> {
                val f1 = 10 * r.nextInt(2, 9)
                val a1 = 10 * r.nextInt(2, 7)
                val a2 = listOf(10, 20, 30, 40, 50, 60, 80).filter { it != a1 && (f1 * a1) % it == 0 }.random(r)
                task(
                    Topic.Motion, d,
                    tr(
                        "An einer Wippe hängt links ein Gewicht mit $f1 N, $a1 cm vom Drehpunkt. Welche Kraft brauchst du rechts in $a2 cm Abstand, damit sie im Gleichgewicht ist?",
                        "A seesaw has a $f1 N weight on the left, $a1 cm from the pivot. What force do you need on the right at $a2 cm to balance it?",
                    ),
                    f1 * a1 / a2, "N",
                    listOf(
                        tr("Hebelgesetz: F₁ · a₁ = F₂ · a₂", "Law of the lever: F₁ · a₁ = F₂ · a₂"),
                        "$f1 N · $a1 cm = ${f1 * a1} Ncm",
                        "F₂ = ${f1 * a1} : $a2 = ${f1 * a1 / a2} N",
                    ),
                )
            }
            else -> {
                var m1 = 2
                var m2 = 2
                var v1 = 6
                var v = 3
                for (i in 0 until 60) {
                    val a = r.nextInt(1, 7)
                    val b = r.nextInt(1, 7)
                    val c = r.nextInt(1, 7)
                    if ((c * (a + b)) % a == 0) { m1 = a; m2 = b; v = c; v1 = c * (a + b) / a; break }
                }
                task(
                    Topic.Motion, d,
                    tr(
                        "Ein Wagen ($m1 kg, $v1 m/s) prallt auf einen stehenden Wagen ($m2 kg). Beide haken ein und rollen zusammen weiter. Wie schnell?",
                        "A cart ($m1 kg, $v1 m/s) hits a cart at rest ($m2 kg). They couple and roll on together. How fast?",
                    ),
                    v, "m/s",
                    listOf(
                        tr("Impulserhaltung: p = m · v bleibt gleich", "Conservation of momentum: p = m · v stays the same"),
                        tr("Vorher: p = $m1 · $v1 = ${m1 * v1} kg·m/s", "Before: p = $m1 · $v1 = ${m1 * v1} kg·m/s"),
                        tr("Nachher bewegen sich ${m1 + m2} kg: v = ${m1 * v1} : ${m1 + m2} = $v m/s", "Afterwards ${m1 + m2} kg move: v = ${m1 * v1} : ${m1 + m2} = $v m/s"),
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Energie & Leistung
    // ---------------------------------------------------------------------------------------------

    fun energy(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(3)) {
            0 -> {
                val f = 5 * r.nextInt(2, 21)
                val s = r.nextInt(2, 16)
                task(
                    Topic.Energy, d,
                    tr("Du schiebst eine Kiste mit $f N Kraft $s m weit. Wie viel Arbeit verrichtest du?", "You push a box with a force of $f N for $s m. How much work do you do?"),
                    f * s, "J",
                    listOf(tr("Arbeit: W = F · s", "Work: W = F · s"), "W = $f N · $s m = ${f * s} J"),
                )
            }
            1 -> {
                val m = r.nextInt(1, 21)
                val h = r.nextInt(2, 31)
                task(
                    Topic.Energy, d,
                    tr("Du trägst $m kg $h m die Treppe hoch. Wie viel Lageenergie gewinnt die Last? (g = 10 N/kg)", "You carry $m kg up $h m of stairs. How much potential energy does the load gain? (g = 10 N/kg)"),
                    10 * m * h, "J",
                    listOf(tr("Lageenergie: E = m · g · h", "Potential energy: E = m · g · h"), "E = $m · 10 · $h = ${10 * m * h} J"),
                )
            }
            else -> {
                val p = 50 * r.nextInt(1, 11)
                val t = r.nextInt(2, 11)
                task(
                    Topic.Energy, d,
                    tr("Ein Motor verrichtet in $t s eine Arbeit von ${p * t} J. Welche Leistung hat er?", "A motor does ${p * t} J of work in $t s. What is its power?"),
                    p, "W",
                    listOf(tr("Leistung: P = W : t", "Power: P = W : t"), "P = ${p * t} J : $t s = $p W"),
                )
            }
        }
        Difficulty.Medium -> when (r.nextInt(3)) {
            0 -> {
                val m = listOf(2, 4, 6, 8).random(r)
                val v = r.nextInt(3, 11)
                task(
                    Topic.Energy, d,
                    tr("Ein Körper mit m = $m kg bewegt sich mit v = $v m/s. Wie groß ist seine Bewegungsenergie?", "An object with m = $m kg moves at v = $v m/s. What is its kinetic energy?"),
                    m / 2 * v * v, "J",
                    listOf(tr("Bewegungsenergie: E = ½ · m · v²", "Kinetic energy: E = ½ · m · v²"), "v² = ${v * v}", "E = ½ · $m · ${v * v} = ${m / 2 * v * v} J"),
                )
            }
            1 -> {
                val (p, h) = listOf(500 to 2, 500 to 4, 500 to 6, 1000 to 2, 1000 to 3, 2000 to 2, 2000 to 3, 200 to 5, 100 to 10, 1000 to 5).random(r)
                val kwh = p * h / 1000
                task(
                    Topic.Energy, d,
                    tr("Ein Gerät mit $p W läuft $h Stunden. Strom kostet 30 Cent pro kWh. Wie viel Cent kostet das?", "A $p W device runs for $h hours. Electricity costs 30 cents per kWh. How many cents does that cost?"),
                    kwh * 30, "ct",
                    listOf(
                        tr("Energie: E = P · t = $p W · $h h = ${p * h} Wh", "Energy: E = P · t = $p W · $h h = ${p * h} Wh"),
                        tr("1000 Wh = 1 kWh, also $kwh kWh", "1000 Wh = 1 kWh, so $kwh kWh"),
                        "$kwh · 30 ct = ${kwh * 30} ct",
                    ),
                )
            }
            else -> {
                val eta = listOf(20, 25, 40, 50, 60, 75, 80).random(r)
                val ein = 20 * r.nextInt(5, 51)
                val nutz = ein * eta / 100
                task(
                    Topic.Energy, d,
                    tr("Ein Motor bekommt $ein J und hat einen Wirkungsgrad von $eta %. Wie viel Energie nutzt er?", "A motor receives $ein J and has an efficiency of $eta %. How much energy does it use?"),
                    nutz, "J",
                    listOf(tr("Wirkungsgrad: η = Nutzen : Aufwand", "Efficiency: η = useful : input"), tr("Nutzen = $eta % von $ein J", "Useful = $eta % of $ein J"), "$ein · $eta : 100 = $nutz J"),
                )
            }
        }
        else -> when (r.nextInt(3)) {
            0 -> {
                val (h, v) = listOf(5 to 10, 20 to 20, 45 to 30, 80 to 40, 125 to 50).random(r)
                task(
                    Topic.Energy, d,
                    tr("Ein Stein fällt aus $h m Höhe (g = 10 m/s², ohne Luftwiderstand). Wie schnell ist er beim Aufprall?", "A stone falls from $h m (g = 10 m/s², no air resistance). How fast is it when it lands?"),
                    v, "m/s",
                    listOf(
                        tr("Lageenergie wird zu Bewegungsenergie: m·g·h = ½·m·v²  →  v = √(2·g·h)", "Potential energy becomes kinetic: m·g·h = ½·m·v²  →  v = √(2·g·h)"),
                        "2 · 10 · $h = ${20 * h}",
                        "√${20 * h} = $v m/s",
                    ),
                )
            }
            1 -> {
                val m = r.nextInt(2, 9)
                val h = r.nextInt(2, 16)
                val t = listOf(2, 4, 5, 10).filter { (m * 10 * h) % it == 0 }.random(r)
                task(
                    Topic.Energy, d,
                    tr("Ein Motor hebt $m kg in $t s um $h m hoch (g = 10 m/s²). Welche Leistung braucht er mindestens?", "A motor lifts $m kg by $h m in $t s (g = 10 m/s²). What power does it need at least?"),
                    m * 10 * h / t, "W",
                    listOf(
                        tr("Hubarbeit: W = m · g · h = $m · 10 · $h = ${m * 10 * h} J", "Work: W = m · g · h = $m · 10 · $h = ${m * 10 * h} J"),
                        tr("Leistung: P = W : t", "Power: P = W : t"),
                        "P = ${m * 10 * h} J : $t s = ${m * 10 * h / t} W",
                    ),
                )
            }
            else -> {
                val m = listOf(800, 1000, 1200, 1500, 2000).random(r)
                val v = listOf(10, 20, 30).random(r)
                val e = m * v * v / 2
                task(
                    Topic.Energy, d,
                    tr("Ein Auto mit $m kg bremst von $v m/s bis zum Stillstand. Wie viele kJ werden in den Bremsen zu Wärme?", "A $m kg car brakes from $v m/s to a stop. How many kJ turn into heat in the brakes?"),
                    e / 1000, "kJ",
                    listOf(
                        tr("Die ganze Bewegungsenergie wird zu Wärme: E = ½ · m · v²", "All the kinetic energy turns into heat: E = ½ · m · v²"),
                        "E = ½ · $m · ${v * v} = $e J",
                        tr("$e J = ${e / 1000} kJ", "$e J = ${e / 1000} kJ"),
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Elektrizität
    // ---------------------------------------------------------------------------------------------

    fun electricity(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(3)) {
            0 -> {
                val rr = listOf(5, 10, 20, 50, 100).random(r)
                val i = r.nextInt(2, 7)
                task(
                    Topic.Electricity, d,
                    tr("Durch einen Widerstand von $rr Ω fließen $i A. Welche Spannung liegt an?", "A current of $i A flows through a $rr Ω resistor. What is the voltage?"),
                    rr * i, "V",
                    listOf(tr("Ohmsches Gesetz: U = R · I", "Ohm's law: U = R · I"), "U = $rr Ω · $i A = ${rr * i} V"),
                )
            }
            1 -> {
                val rr = listOf(2, 3, 4, 6, 12).random(r)
                val i = r.nextInt(1, 11)
                task(
                    Topic.Electricity, d,
                    tr("An einem Widerstand von $rr Ω liegen ${rr * i} V. Wie stark ist der Strom?", "A $rr Ω resistor has ${rr * i} V across it. What is the current?"),
                    i, "A",
                    listOf(tr("Ohmsches Gesetz umgestellt: I = U : R", "Ohm's law rearranged: I = U : R"), "I = ${rr * i} V : $rr Ω = $i A"),
                )
            }
            else -> {
                val u = listOf(12, 24, 230).random(r)
                val i = if (u == 230) r.nextInt(2, 9) else r.nextInt(2, 6)
                task(
                    Topic.Electricity, d,
                    tr("Ein Gerät an $u V nimmt $i A auf. Welche Leistung hat es?", "A device at $u V draws $i A. What is its power?"),
                    u * i, "W",
                    listOf(tr("Elektrische Leistung: P = U · I", "Electric power: P = U · I"), "P = $u V · $i A = ${u * i} W"),
                )
            }
        }
        Difficulty.Medium -> when (r.nextInt(3)) {
            0 -> {
                val r1 = 5 * r.nextInt(1, 9)
                val r2 = 5 * r.nextInt(1, 9)
                val i = r.nextInt(1, 6)
                val u = (r1 + r2) * i
                task(
                    Topic.Electricity, d,
                    tr("Zwei Widerstände ($r1 Ω und $r2 Ω) sind in Reihe an $u V angeschlossen. Wie stark ist der Strom?", "Two resistors ($r1 Ω and $r2 Ω) are connected in series to $u V. What is the current?"),
                    i, "A",
                    listOf(
                        tr("Reihenschaltung: R = R₁ + R₂ = $r1 + $r2 = ${r1 + r2} Ω", "Series circuit: R = R₁ + R₂ = $r1 + $r2 = ${r1 + r2} Ω"),
                        tr("Ohmsches Gesetz: I = U : R", "Ohm's law: I = U : R"),
                        "I = $u V : ${r1 + r2} Ω = $i A",
                    ),
                )
            }
            1 -> {
                val p = listOf(5, 8, 10, 40, 60, 100).random(r)
                val h = r.nextInt(2, 13)
                task(
                    Topic.Electricity, d,
                    tr("Eine Lampe mit $p W leuchtet $h Stunden. Wie viele Wattstunden (Wh) braucht sie?", "A $p W lamp is on for $h hours. How many watt-hours (Wh) does it use?"),
                    p * h, "Wh",
                    listOf(tr("Elektrische Energie: E = P · t", "Electric energy: E = P · t"), "E = $p W · $h h = ${p * h} Wh"),
                )
            }
            else -> {
                val i = r.nextInt(2, 11)
                val t = 10 * r.nextInt(1, 13)
                task(
                    Topic.Electricity, d,
                    tr("Ein Strom von $i A fließt $t s lang. Wie viel Ladung fließt durch den Draht?", "A current of $i A flows for $t s. How much charge passes through the wire?"),
                    i * t, "C",
                    listOf(tr("Ladung: Q = I · t", "Charge: Q = I · t"), "Q = $i A · $t s = ${i * t} C"),
                )
            }
        }
        else -> when (r.nextInt(3)) {
            0 -> {
                val (r1, r2, rp) = listOf(Triple(6, 3, 2), Triple(12, 6, 4), Triple(20, 5, 4), Triple(10, 10, 5), Triple(30, 15, 10), Triple(12, 4, 3), Triple(60, 30, 20), Triple(40, 10, 8)).random(r)
                val i = r.nextInt(1, 6)
                val u = rp * i
                task(
                    Topic.Electricity, d,
                    tr("$r1 Ω und $r2 Ω sind parallel an $u V angeschlossen. Wie groß ist der Gesamtstrom?", "$r1 Ω and $r2 Ω are connected in parallel to $u V. What is the total current?"),
                    i, "A",
                    listOf(
                        tr("Parallelschaltung: 1/R = 1/R₁ + 1/R₂, also R = R₁·R₂ : (R₁ + R₂)", "Parallel circuit: 1/R = 1/R₁ + 1/R₂, so R = R₁·R₂ : (R₁ + R₂)"),
                        "R = ${r1 * r2} : ${r1 + r2} = $rp Ω",
                        tr("Ohmsches Gesetz: I = U : R = $u : $rp = $i A", "Ohm's law: I = U : R = $u : $rp = $i A"),
                    ),
                )
            }
            1 -> {
                val i = r.nextInt(2, 11)
                val rr = r.nextInt(1, 11)
                task(
                    Topic.Electricity, d,
                    tr("Durch ein Kabel mit $rr Ω Widerstand fließen $i A. Wie viel Leistung geht als Wärme verloren?", "A current of $i A flows through a cable with $rr Ω resistance. How much power is lost as heat?"),
                    i * i * rr, "W",
                    listOf(
                        tr("Elektrische Leistung: P = U · I und U = R · I, also P = I² · R", "Electric power: P = U · I and U = R · I, so P = I² · R"),
                        "I² = ${i * i}",
                        "P = ${i * i} · $rr = ${i * i * rr} W",
                    ),
                )
            }
            else -> {
                val u = listOf(12, 24).random(r)
                val p = if (u == 12) listOf(6, 12, 24, 36, 48, 72).random(r) else listOf(12, 24, 48, 72, 96, 144).random(r)
                task(
                    Topic.Electricity, d,
                    tr("Eine Lampe hat $p W bei $u V. Welchen Widerstand hat sie im Betrieb?", "A lamp is rated $p W at $u V. What is its resistance when running?"),
                    u * u / p, "Ω",
                    listOf(
                        tr("Elektrische Leistung: P = U · I und I = U : R, also P = U² : R", "Electric power: P = U · I and I = U : R, so P = U² : R"),
                        tr("Umgestellt: R = U² : P", "Rearranged: R = U² : P"),
                        "R = ${u * u} : $p = ${u * u / p} Ω",
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Dichte, Druck & Wärme
    // ---------------------------------------------------------------------------------------------

    fun matter(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(3)) {
            0 -> {
                val rho = listOf(1, 2, 3, 7, 8, 11, 19).random(r)
                val v = r.nextInt(2, 21)
                task(
                    Topic.Matter, d,
                    tr("Ein Würfel hat ein Volumen von $v cm³ und wiegt ${rho * v} g. Welche Dichte hat er (in g/cm³)?", "A cube has a volume of $v cm³ and weighs ${rho * v} g. What is its density (in g/cm³)?"),
                    rho, "g/cm³",
                    listOf(tr("Dichte: ρ = m : V", "Density: ρ = m : V"), "ρ = ${rho * v} g : $v cm³ = $rho g/cm³"),
                )
            }
            1 -> {
                val p = 100 * r.nextInt(1, 51)
                val a = r.nextInt(2, 11)
                task(
                    Topic.Matter, d,
                    tr("Eine Kiste drückt mit ${p * a} N auf eine Fläche von $a m². Wie groß ist der Druck?", "A crate pushes with ${p * a} N on an area of $a m². What is the pressure?"),
                    p, "Pa",
                    listOf(tr("Druck: p = F : A", "Pressure: p = F : A"), "p = ${p * a} N : $a m² = $p Pa"),
                )
            }
            else -> {
                val c = r.nextInt(-40, 101)
                val cText = if (c < 0) "−${-c}" else "$c"
                task(
                    Topic.Matter, d,
                    tr("Wie viel sind $cText °C in Kelvin?", "What is $cText °C in kelvin?"),
                    c + 273, "K",
                    listOf(tr("Kelvin: T = °C + 273", "Kelvin: T = °C + 273"), "$cText + 273 = ${c + 273} K"),
                )
            }
        }
        Difficulty.Medium -> when (r.nextInt(3)) {
            0 -> {
                val (rho, name) = listOf(
                    2 to tr("Glas", "glass"), 7 to tr("Zinn", "tin"), 8 to tr("Messing", "brass"),
                    11 to tr("Blei", "lead"), 19 to tr("Gold", "gold"), 1 to tr("Wasser", "water"),
                ).random(r)
                val v = r.nextInt(3, 41)
                task(
                    Topic.Matter, d,
                    tr("$name hat eine Dichte von etwa $rho g/cm³. Wie schwer sind $v cm³ davon?", "$name has a density of about $rho g/cm³. How much do $v cm³ of it weigh?"),
                    rho * v, "g",
                    listOf(tr("Dichte: ρ = m : V, also m = ρ · V", "Density: ρ = m : V, so m = ρ · V"), "m = $rho g/cm³ · $v cm³ = ${rho * v} g"),
                )
            }
            1 -> {
                val h = r.nextInt(1, 21)
                task(
                    Topic.Matter, d,
                    tr("Wie groß ist der Wasserdruck in $h m Tiefe, ohne den Luftdruck? (in hPa, g = 10 N/kg)", "What is the water pressure at $h m depth, without air pressure? (in hPa, g = 10 N/kg)"),
                    100 * h, "hPa",
                    listOf(
                        tr("Schweredruck: p = ρ · g · h", "Hydrostatic pressure: p = ρ · g · h"),
                        "p = 1000 kg/m³ · 10 N/kg · $h m = ${10000 * h} Pa",
                        "${10000 * h} Pa = ${100 * h} hPa",
                    ),
                )
            }
            else -> {
                val v = r.nextInt(1, 51)
                task(
                    Topic.Matter, d,
                    tr("Ein Ball mit $v Litern Volumen wird ganz unter Wasser gedrückt. Wie groß ist die Auftriebskraft? (g = 10 N/kg)", "A ball with a volume of $v liters is pushed fully under water. What is the buoyant force? (g = 10 N/kg)"),
                    10 * v, "N",
                    listOf(
                        tr("Auftrieb = Gewichtskraft des verdrängten Wassers: F = ρ · g · V", "Buoyancy = weight of the displaced water: F = ρ · g · V"),
                        tr("$v L Wasser wiegen $v kg", "$v L of water weigh $v kg"),
                        "F = $v kg · 10 N/kg = ${10 * v} N",
                    ),
                )
            }
        }
        else -> when (r.nextInt(3)) {
            0 -> {
                val m = listOf(1, 2, 3, 5).random(r)
                val dT = 10 * r.nextInt(1, 9)
                val q = 42 * m * dT / 10
                task(
                    Topic.Matter, d,
                    tr("Wie viele kJ braucht man, um $m kg Wasser um $dT K zu erwärmen? (c = 4,2 kJ/(kg·K))", "How many kJ does it take to heat $m kg of water by $dT K? (c = 4.2 kJ/(kg·K))"),
                    q, "kJ",
                    listOf(tr("Wärme: Q = c · m · ΔT", "Heat: Q = c · m · ΔT"), tr("Q = 4,2 · $m · $dT", "Q = 4.2 · $m · $dT"), "Q = $q kJ"),
                )
            }
            1 -> {
                val m = listOf(1, 2).random(r)
                val dT = listOf(50, 60, 70, 80).random(r)
                val p = 2000
                val q = 4200 * m * dT
                task(
                    Topic.Matter, d,
                    tr("Ein Wasserkocher mit $p W erhitzt $m kg Wasser um $dT K. Wie viele Sekunden dauert das ohne Verluste? (c = 4200 J/(kg·K))", "A $p W kettle heats $m kg of water by $dT K. How many seconds does it take without losses? (c = 4200 J/(kg·K))"),
                    q / p, "s",
                    listOf(
                        tr("Wärme: Q = c · m · ΔT = 4200 · $m · $dT = $q J", "Heat: Q = c · m · ΔT = 4200 · $m · $dT = $q J"),
                        tr("Leistung: P = W : t, also t = Q : P", "Power: P = W : t, so t = Q : P"),
                        "t = $q J : $p W = ${q / p} s",
                    ),
                )
            }
            else -> {
                val a1 = listOf(2, 4, 5, 10).random(r)
                val k = r.nextInt(5, 41)
                val a2 = a1 * k
                val f1 = 10 * r.nextInt(2, 21)
                task(
                    Topic.Matter, d,
                    tr("Bei einer hydraulischen Hebebühne drückst du mit $f1 N auf einen Kolben mit $a1 cm². Wie viel Kraft entsteht am großen Kolben mit $a2 cm²?", "On a hydraulic lift you push with $f1 N on a $a1 cm² piston. How much force acts on the large $a2 cm² piston?"),
                    f1 * k, "N",
                    listOf(
                        tr("Der Druck ist überall gleich: p = F : A", "The pressure is the same everywhere: p = F : A"),
                        tr("Die Fläche ist $a2 : $a1 = $k-mal so groß, also auch die Kraft.", "The area is $a2 : $a1 = $k times larger, and so is the force."),
                        "F₂ = $f1 N · $k = ${f1 * k} N",
                    ),
                )
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Wellen & Optik
    // ---------------------------------------------------------------------------------------------

    fun waves(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(3)) {
            0 -> {
                val t = r.nextInt(1, 11)
                task(
                    Topic.Waves, d,
                    tr("Du siehst einen Blitz und hörst den Donner $t s später. Wie weit ist das Gewitter weg? (Schall: 340 m/s)", "You see lightning and hear the thunder $t s later. How far away is the storm? (sound: 340 m/s)"),
                    340 * t, "m",
                    listOf(tr("Strecke: s = v · t (das Licht kommt fast sofort an)", "Distance: s = v · t (the light arrives almost instantly)"), "s = 340 m/s · $t s = ${340 * t} m"),
                )
            }
            1 -> {
                val f = r.nextInt(2, 51)
                val t = r.nextInt(2, 11)
                task(
                    Topic.Waves, d,
                    tr("Eine Saite schwingt in $t s genau ${f * t}-mal. Welche Frequenz hat sie?", "A string vibrates exactly ${f * t} times in $t s. What is its frequency?"),
                    f, "Hz",
                    listOf(tr("Frequenz: f = Schwingungen : Zeit", "Frequency: f = vibrations : time"), "f = ${f * t} : $t s = $f Hz"),
                )
            }
            else -> {
                val f = listOf(2, 4, 5, 10, 20, 25, 50, 100, 200, 250, 500).random(r)
                task(
                    Topic.Waves, d,
                    tr("Wie viele Millisekunden dauert eine Schwingung bei $f Hz?", "How many milliseconds does one vibration take at $f Hz?"),
                    1000 / f, "ms",
                    listOf(tr("Periodendauer: T = 1 : f", "Period: T = 1 : f"), tr("T = 1 : $f s = 1000 : $f ms", "T = 1 : $f s = 1000 : $f ms"), "T = ${1000 / f} ms"),
                )
            }
        }
        Difficulty.Medium -> when (r.nextInt(3)) {
            0 -> {
                val f = listOf(17, 20, 34, 68, 85, 170).random(r)
                task(
                    Topic.Waves, d,
                    tr("Ein Ton hat $f Hz. Wie lang ist seine Wellenlänge in Luft? (Schall: 340 m/s)", "A tone has $f Hz. How long is its wavelength in air? (sound: 340 m/s)"),
                    340 / f, "m",
                    listOf(tr("Wellengleichung: c = λ · f, also λ = c : f", "Wave equation: c = λ · f, so λ = c : f"), "λ = 340 : $f = ${340 / f} m"),
                )
            }
            1 -> {
                val t = r.nextInt(1, 7)
                task(
                    Topic.Waves, d,
                    tr("Du rufst gegen eine Felswand und hörst das Echo nach $t s. Wie weit ist die Wand weg? (Schall: 340 m/s)", "You shout at a rock wall and hear the echo after $t s. How far away is the wall? (sound: 340 m/s)"),
                    170 * t, "m",
                    listOf(
                        tr("Echo: Der Schall läuft hin und zurück, also ist der Weg doppelt so lang.", "Echo: the sound travels there and back, so the path is twice as long."),
                        tr("Strecke: s = v · t = 340 · $t = ${340 * t} m (hin und zurück)", "Distance: s = v · t = 340 · $t = ${340 * t} m (there and back)"),
                        "${340 * t} : 2 = ${170 * t} m",
                    ),
                )
            }
            else -> {
                val t = r.nextInt(1, 11)
                task(
                    Topic.Waves, d,
                    tr("Licht ist rund 300 000 km/s schnell. Wie viele Sekunden braucht es für ${300000 * t} km?", "Light travels at about 300,000 km/s. How many seconds does it need for ${300000 * t} km?"),
                    t, "s",
                    listOf(tr("Zeit aus v = s : t: t = s : v", "Time from v = s : t: t = s : v"), "t = ${300000 * t} km : 300000 km/s = $t s"),
                )
            }
        }
        else -> when (r.nextInt(3)) {
            0 -> {
                val (f, g, b) = listOf(Triple(10, 15, 30), Triple(12, 20, 30), Triple(20, 30, 60), Triple(6, 10, 15), Triple(8, 12, 24), Triple(15, 20, 60), Triple(10, 20, 20), Triple(4, 6, 12)).random(r)
                task(
                    Topic.Waves, d,
                    tr("Eine Linse hat die Brennweite $f cm. Ein Gegenstand steht $g cm davor. In welchem Abstand entsteht das scharfe Bild?", "A lens has a focal length of $f cm. An object is $g cm in front of it. At what distance is the sharp image?"),
                    b, "cm",
                    listOf(
                        tr("Linsengleichung: 1/f = 1/g + 1/b, also 1/b = 1/f − 1/g", "Lens equation: 1/f = 1/g + 1/b, so 1/b = 1/f − 1/g"),
                        run {
                            val l = lcm(f, g)
                            val n = l / f - l / g
                            "1/$f − 1/$g = ${l / f}/$l − ${l / g}/$l = $n/$l" + if (n != 1 || l != b) " = 1/$b" else ""
                        },
                        "b = $b cm",
                    ),
                )
            }
            1 -> {
                val (g, b) = listOf(20 to 60, 10 to 40, 15 to 30, 25 to 100, 30 to 90, 12 to 36).random(r)
                val gg = r.nextInt(1, 6)
                task(
                    Topic.Waves, d,
                    tr("Ein $gg cm großer Gegenstand steht $g cm vor einer Linse, das Bild entsteht $b cm dahinter. Wie groß ist das Bild?", "A $gg cm tall object stands $g cm in front of a lens; the image forms $b cm behind it. How tall is the image?"),
                    gg * b / g, "cm",
                    listOf(
                        tr("Abbildungsmaßstab: B : G = b : g", "Magnification: B : G = b : g"),
                        tr("Das Bild ist $b : $g = ${b / g}-mal so groß.", "The image is $b : $g = ${b / g} times as large."),
                        "B = $gg cm · ${b / g} = ${gg * b / g} cm",
                    ),
                )
            }
            else -> {
                val (f, l) = listOf(400 to 750, 500 to 600, 600 to 500, 750 to 400).random(r)
                task(
                    Topic.Waves, d,
                    tr("Welche Wellenlänge (in nm) hat Licht mit $f THz? (c = 300 000 km/s)", "What wavelength (in nm) does light with $f THz have? (c = 300,000 km/s)"),
                    l, "nm",
                    listOf(
                        tr("Wellengleichung: c = λ · f, also λ = c : f", "Wave equation: c = λ · f, so λ = c : f"),
                        tr("c = 300 000 000 m/s, f = $f · 10¹² Hz", "c = 300,000,000 m/s, f = $f · 10¹² Hz"),
                        tr("λ = 3 · 10⁸ : ($f · 10¹²) m = $l · 10⁻⁹ m = $l nm", "λ = 3 · 10⁸ : ($f · 10¹²) m = $l · 10⁻⁹ m = $l nm"),
                    ),
                )
            }
        }
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
    private fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b
}
