package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.math.tan

/*
 * Physik-Ausbau (seit 13.2): Bewegung und Kräfte. Jede Aufgabe hat ein eigenes Setup; Ergebnisse werden im
 * Code ausgerechnet. Anregungen für Themen (nicht wörtlich): OpenStax University Physics (CC BY 4.0),
 * Halliday/Resnick. Alle Texte selbst geschrieben.
 */

private const val G1 = 9.81
private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

/** Auswahl-Schritt, bei dem [right] die richtige Antwort ist (die Ansicht mischt). */
internal fun pick1(prompt: String, right: String, wrong: List<String>, hints: List<String>, done: String = right) =
    choice(prompt, listOf(right) + wrong, done, hints)

object PhysNew1 {
    val all: List<TaskDef> get() = motion + forces

    private val motion = listOf(
        lab("phy.gepard", Topic.Motion, M, 0.4f) {
            val vc = 29.0; val vg = 22.0; val d0 = pick(50.0, 60.0, 70.0)
            val t = d0 / (vc - vg); val s = vc * t
            task(
                question = l("Ein Gepard sprintet mit 29 m/s los, eine Gazelle flieht mit 22 m/s. Beim Start hat sie ${n(d0, 0)} m Vorsprung. Länger als etwa 20 Sekunden hält der Gepard das Tempo nicht durch.", "A cheetah sprints off at 29 m/s, a gazelle flees at 22 m/s. At the start she has a ${n(d0, 0)} m head start. The cheetah can't keep up that pace for longer than about 20 seconds."),
                steps = listOf(
                    num(l("Nach wie vielen Sekunden holt er sie ein?", "After how many seconds does it catch her?"), t, "s", rel = 0.03, done = """§t \approx ${m(sig(t))}\ \text{s}§""",
                        hints = listOf(l("Kernidee: Rechne aus Sicht der Gazelle. Der Gepard kommt ihr nur mit dem Unterschied der Geschwindigkeiten näher.", "Key idea: think from the gazelle's point of view. The cheetah only closes in at the difference of the speeds."), """§t = \dfrac{d_0}{v_G - v_{Gaz}}§""")),
                    num(l("Wie weit ist der Gepard bis dahin gelaufen?", "How far has the cheetah run by then?"), s, "m", rel = 0.03, done = """§s \approx ${m(sig(s))}\ \text{m}§""",
                        hints = listOf(l("Strecke = Geschwindigkeit mal Zeit, mit seinen 29 m/s.", "Distance = speed times time, with its 29 m/s."), """§s = 29\ \tfrac{\text{m}}{\text{s}} \cdot t§""")),
                ),
                solution = listOf("""§t = \dfrac{${m(d0, 0)}\ \text{m}}{(29 - 22)\ \text{m/s}} \approx ${m(sig(t))}\ \text{s}§""", """§s = 29 \cdot ${m(sig(t))}\ \text{m} \approx ${m(sig(s))}\ \text{m}§"""),
                background = l("Verfolgungen rechnet man am einfachsten mit der Relativgeschwindigkeit: Aus Sicht der Gazelle steht sie still, und der Gepard kommt mit nur 7 m/s auf sie zu. Der riesige Unterschied in der Höchstgeschwindigkeit schrumpft so auf einen kleinen Rest, und der Vorsprung entscheidet alles.", "Chases are easiest with relative velocity: from the gazelle's point of view she stands still and the cheetah approaches at only 7 m/s. The huge difference in top speed shrinks to a small remainder, and the head start decides everything."),
                fact = l("Nur etwa jede zweite Jagd eines Gepards gelingt. Sein Körper heizt sich beim Sprint so stark auf, dass er danach minutenlang verschnaufen muss.", "Only about every second hunt of a cheetah succeeds. Its body heats up so much during the sprint that it has to rest for minutes afterwards."),
            )
        },
        lab("phy.lineal", Topic.Motion, M, 0.3f) {
            val hCm = pick(15.0, 18.0, 20.0, 25.0)
            val t = sqrt(2 * hCm / 100 / G1)
            task(
                question = l("Reaktionstest: Jemand hält ein Lineal senkrecht zwischen deine Finger und lässt es ohne Vorwarnung los. Du fängst es, nachdem es ${n(hCm, 0)} cm gefallen ist. Wie lang war deine Reaktionszeit?", "Reaction test: someone holds a ruler vertically between your fingers and lets go without warning. You catch it after it has fallen ${n(hCm, 0)} cm. How long was your reaction time?"),
                steps = listOf(num(l("Reaktionszeit in Sekunden", "Reaction time in seconds"), t, "s", rel = 0.03, done = """§t \approx ${m(t, 3)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Das Lineal fällt frei. Aus der Fallstrecke folgt die Fallzeit.", "Key idea: the ruler falls freely. The distance it fell tells you the time."), """§h = \tfrac{1}{2} g t^2 \Rightarrow t = \sqrt{\dfrac{2h}{g}}§"""))),
                solution = listOf("""§t = \sqrt{\dfrac{2 \cdot ${m(hCm / 100)}\ \text{m}}{9{,}81\ \text{m/s}^2}} \approx ${m(t, 3)}\ \text{s}§"""),
                background = l("Im freien Fall wächst die Strecke mit dem Quadrat der Zeit. Deshalb ist das Lineal ein erstaunlich genauer Reaktionsmesser: Die ersten Zentimeter fällt es langsam, danach immer schneller. 5 cm bedeuten 0,1 s, 20 cm schon 0,2 s.", "In free fall the distance grows with the square of the time. That's why a ruler is a surprisingly precise reaction timer: it falls slowly for the first centimetres and then faster and faster. 5 cm means 0.1 s, 20 cm already 0.2 s."),
                fact = l("Bei Sprintrennen gilt ein Start als Fehlstart, wenn jemand schneller als 0,1 s nach dem Schuss reagiert. So schnell kann das Gehirn gar nicht verarbeiten, es war also geraten.", "In sprint races a start counts as false if someone reacts faster than 0.1 s after the gun. The brain can't process that fast, so it must have been a guess."),
            )
        },
        lab("phy.fluss", Topic.Motion, M, 0.4f) {
            val w = pick(30.0, 40.0, 60.0); val vs = 1.0; val vr = pick(0.5, 0.8, 1.2)
            val drift = w / vs * vr
            task(
                question = l("Du schwimmst durch einen ${n(w, 0)} m breiten Fluss, immer quer zum Ufer ausgerichtet, mit 1 m/s gegenüber dem Wasser. Die Strömung fließt mit ${n(vr)} m/s. Wie weit flussabwärts kommst du drüben an?", "You swim across a river ${n(w, 0)} m wide, always facing straight across, at 1 m/s relative to the water. The current flows at ${n(vr)} m/s. How far downstream do you land on the other side?"),
                steps = listOf(num(l("Abdrift flussabwärts", "Drift downstream"), drift, "m", rel = 0.03, done = """§x = ${m(drift)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Quer- und Längsbewegung stören sich nicht. Wie lange du unterwegs bist, hängt nur vom Queren ab.", "Key idea: the crossing and the drift don't interfere. How long you are in the water depends only on the crossing."), """§t = \dfrac{b}{v_S},\quad x = v_F \cdot t§"""))),
                solution = listOf("""§t = \dfrac{${m(w, 0)}\ \text{m}}{1\ \text{m/s}} = ${m(w, 0)}\ \text{s}§""", """§x = ${m(vr)}\ \tfrac{\text{m}}{\text{s}} \cdot ${m(w, 0)}\ \text{s} = ${m(drift)}\ \text{m}§"""),
                background = l("Bewegungen in zwei Richtungen, die senkrecht aufeinander stehen, laufen unabhängig voneinander ab. Die Strömung nimmt dich mit, macht dich aber nicht langsamer beim Queren. Deshalb ist es keine gute Idee, gegen eine starke Strömung anzukämpfen: Schräg mitschwimmen bringt dich genauso schnell ans Ufer.", "Motions in two perpendicular directions happen independently. The current carries you along but doesn't slow down your crossing. That's why fighting a strong current is a bad idea: swimming across at an angle with it gets you to the bank just as fast."),
                fact = l("Bei einer Rippströmung am Meer rät die DLRG genau das: nicht gegen den Sog zum Strand schwimmen, sondern parallel zum Ufer heraus.", "For a rip current at the sea, lifeguards advise exactly this: don't swim against the pull towards the beach, swim parallel to the shore to get out of it."),
            )
        },
        lab("phy.flugwind", Topic.Motion, H, 0.5f) {
            val va = 180.0; val vw = pick(30.0, 40.0, 50.0)
            val ang = Math.toDegrees(asin(vw / va)); val vg = sqrt(va * va - vw * vw)
            task(
                question = l("Ein kleines Flugzeug fliegt mit 180 km/h gegenüber der Luft. Von Westen weht ein Wind mit ${n(vw, 0)} km/h. Die Pilotin will genau nach Norden fliegen.", "A small plane flies at 180 km/h relative to the air. A wind of ${n(vw, 0)} km/h blows from the west. The pilot wants to fly due north."),
                steps = listOf(
                    num(l("Um wie viel Grad muss sie die Nase nach Westen drehen?", "By how many degrees must she turn the nose towards the west?"), ang, "°", abs = 0.5, done = """§\alpha \approx ${m(ang, 1)}^\circ§""",
                        hints = listOf(l("Kernidee: Der westliche Anteil ihrer Eigengeschwindigkeit muss den Wind genau aufheben.", "Key idea: the westward part of her own velocity must exactly cancel the wind."), """§\sin\alpha = \dfrac{v_W}{v_L}§""")),
                    num(l("Wie schnell kommt sie über dem Boden nach Norden voran?", "How fast does she make progress northwards over the ground?"), vg, "km/h", rel = 0.01, done = """§v \approx ${m(vg, 1)}\ \text{km/h}§""",
                        hints = listOf(l("Eigengeschwindigkeit, Wind und Grundgeschwindigkeit bilden ein rechtwinkliges Dreieck.", "Airspeed, wind and ground speed form a right triangle."), """§v = \sqrt{v_L^2 - v_W^2}§""")),
                ),
                solution = listOf("""§\sin\alpha = \dfrac{${m(vw, 0)}}{180} \Rightarrow \alpha \approx ${m(ang, 1)}^\circ§""", """§v = \sqrt{180^2 - ${m(vw, 0)}^2}\ \text{km/h} \approx ${m(vg, 1)}\ \text{km/h}§"""),
                background = l("Geschwindigkeiten sind Pfeile und addieren sich wie Pfeile. Das Flugzeug fliegt gegenüber der Luft, die Luft selbst bewegt sich gegenüber dem Boden. Damit der Gesamtpfeil nach Norden zeigt, hält die Pilotin schräg in den Wind, was Piloten Vorhaltewinkel nennen. Ein Teil der Geschwindigkeit geht dafür verloren.", "Velocities are arrows and add like arrows. The plane flies relative to the air, and the air itself moves relative to the ground. For the total arrow to point north, the pilot holds the plane at an angle into the wind, which pilots call the wind correction angle. Some speed is lost doing so."),
                fact = l("Auf Langstrecken nach Osten nutzen Flugzeuge den Jetstream in 10 km Höhe, der mit über 300 km/h wehen kann. Der Flug von New York nach Europa ist deshalb oft eine Stunde kürzer als der Rückflug.", "On long flights eastwards, planes use the jet stream at 10 km altitude, which can blow at over 300 km/h. That's why the flight from New York to Europe is often an hour shorter than the way back."),
            )
        },
        lab("phy.brunnen", Topic.Motion, X, 0.6f) {
            val tt = pick(2.5, 3.0, 3.5); val c = 343.0
            val naive = 0.5 * G1 * tt * tt
            // h/c + sqrt(2h/g) = tt  ->  mit u = sqrt(h): u^2/c + sqrt(2/g) u - tt = 0
            val a = 1 / c; val b = sqrt(2 / G1)
            val u = (-b + sqrt(b * b + 4 * a * tt)) / (2 * a); val h = u * u
            task(
                question = l("Du lässt einen Stein in einen tiefen Brunnen fallen und hörst ihn nach genau ${n(tt)} s unten aufschlagen. Schall läuft mit 343 m/s, die Luft bremst den Stein kaum.", "You drop a stone into a deep well and hear it hit the water after exactly ${n(tt)} s. Sound travels at 343 m/s, the air hardly slows the stone."),
                steps = listOf(
                    num(l("Erste Schätzung: Wie tief wäre der Brunnen, wenn der Schall keine Zeit bräuchte?", "First estimate: how deep would the well be if sound took no time?"), naive, "m", rel = 0.02, done = """§h_0 \approx ${m(sig(naive))}\ \text{m}§""",
                        hints = listOf(l("Kernidee: Freier Fall über die ganze Zeit.", "Key idea: free fall for the whole time."), """§h_0 = \tfrac{1}{2} g t^2§""")),
                    num(l("Wie tief ist er wirklich?", "How deep is it really?"), h, "m", rel = 0.02, done = """§h \approx ${m(sig(h))}\ \text{m}§""",
                        hints = listOf(l("Die Zeit besteht aus Fallzeit plus Laufzeit des Schalls nach oben. Setze beide gleich der gemessenen Zeit und löse nach h auf (mit u = √h wird es eine quadratische Gleichung).", "The time is fall time plus the time the sound needs to come back up. Set their sum equal to the measured time and solve for h (with u = √h it becomes a quadratic)."), """§\sqrt{\dfrac{2h}{g}} + \dfrac{h}{c} = t§""")),
                ),
                solution = listOf("""§h_0 = \tfrac{1}{2} \cdot 9{,}81 \cdot ${m(tt)}^2\ \text{m} \approx ${m(sig(naive))}\ \text{m}§""", """§u = \sqrt{h}:\ \dfrac{u^2}{343} + \sqrt{\dfrac{2}{9{,}81}}\, u - ${m(tt)} = 0 \Rightarrow u \approx ${m(u, 3)}§""", """§h = u^2 \approx ${m(sig(h))}\ \text{m}§"""),
                background = l("Hier stecken zwei Bewegungen hintereinander: Der Stein fällt beschleunigt, der Schall kommt mit fester Geschwindigkeit zurück. Die Schallzeit ist klein, aber nicht null, und weil die Fallstrecke mit dem Quadrat der Zeit wächst, macht schon eine Zehntelsekunde einige Meter aus. Gute Physik heißt oft: erst grob rechnen, dann die Korrektur.", "Two motions follow one another here: the stone falls with acceleration, the sound comes back at constant speed. The sound time is small but not zero, and because the fall distance grows with the square of the time, even a tenth of a second makes several metres of difference. Good physics often means: first a rough estimate, then the correction."),
                fact = l("Mit genau diesem Trick schätzen Höhlenforscher die Tiefe von Schächten, in die sie nicht hineinsehen können.", "Cavers use exactly this trick to estimate the depth of shafts they can't see into."),
            )
        },
        lab("phy.jongleur", Topic.Motion, M, 0.5f) {
            val t = pick(0.8, 1.0, 1.2)
            val h = G1 * t * t / 8
            task(
                question = l("Ein Jongleur will, dass jeder Ball ${n(t)} s in der Luft ist, bevor er ihn wieder fängt. Wie hoch über die Hand muss er die Bälle werfen?", "A juggler wants each ball to stay in the air for ${n(t)} s before he catches it again. How high above his hand must he throw the balls?"),
                steps = listOf(num(l("Wurfhöhe über der Hand", "Height above the hand"), h, "m", rel = 0.03, done = """§h \approx ${m(sig(h))}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Hoch und runter dauern gleich lang. Der Ball fällt also die halbe Zeit lang frei vom höchsten Punkt.", "Key idea: going up and coming down take equally long. So the ball falls freely from the top for half the time."), """§h = \tfrac{1}{2} g \left(\dfrac{t}{2}\right)^2 = \dfrac{g t^2}{8}§"""))),
                solution = listOf("""§h = \dfrac{9{,}81 \cdot ${m(t)}^2}{8}\ \text{m} \approx ${m(sig(h))}\ \text{m}§"""),
                background = l("Ein senkrechter Wurf ist spiegelbildlich: Der Ball wird beim Steigen genauso schnell langsamer, wie er beim Fallen wieder schneller wird. Die Flugzeit wächst nur mit der Wurzel der Höhe. Für doppelt so viel Zeit muss man also viermal so hoch werfen.", "A vertical throw is symmetric: the ball slows down on the way up just as quickly as it speeds up on the way down. The flight time grows only with the square root of the height. So for twice the time you have to throw four times as high."),
                fact = l("Deshalb ist jeder weitere Ball so schwer: Für mehr Bälle braucht man mehr Zeit pro Ball, also deutlich höhere Würfe. Der Rekord liegt bei 11 Bällen, die eine Weile im Kreis laufen.", "That's why each extra ball is so hard: more balls need more time per ball, so much higher throws. The record is 11 balls kept going for a while."),
            )
        },
        lab("phy.rolltreppe", Topic.Motion, M, 0.5f) {
            val t1 = pick(60.0, 45.0); val t2 = pick(90.0, 120.0)
            val t = 1 / (1 / t1 + 1 / t2)
            task(
                question = l("Stehst du still auf einer Rolltreppe, bist du nach ${n(t1, 0)} s oben. Ist sie abgeschaltet und du gehst hoch, brauchst du ${n(t2, 0)} s. Wie lange dauert es, wenn du auf der laufenden Rolltreppe gehst?", "Standing still on an escalator, you reach the top after ${n(t1, 0)} s. When it's switched off and you walk up, it takes you ${n(t2, 0)} s. How long does it take if you walk up the moving escalator?"),
                steps = listOf(num(l("Zeit in Sekunden", "Time in seconds"), t, "s", rel = 0.03, done = """§t = ${m(t, 1)}\ \text{s}§""",
                    hints = listOf(l("Kernidee: Nicht die Zeiten addieren sich, sondern die Geschwindigkeiten. Rechne, welchen Bruchteil der Treppe jeder pro Sekunde schafft.", "Key idea: it's not the times that add, it's the speeds. Work out what fraction of the stairs each covers per second."), """§\dfrac{1}{t} = \dfrac{1}{t_1} + \dfrac{1}{t_2}§"""))),
                solution = listOf("""§\dfrac{1}{t} = \dfrac{1}{${m(t1, 0)}} + \dfrac{1}{${m(t2, 0)}} \Rightarrow t = ${m(t, 1)}\ \text{s}§"""),
                background = l("Die Länge der Treppe kennst du nicht, aber du brauchst sie auch nicht: Rechne in Treppen pro Sekunde. Die Rolltreppe schafft 1/${n(t1, 0)} Treppe pro Sekunde, du 1/${n(t2, 0)}. Zusammen ist es die Summe. Dasselbe Prinzip gilt für zwei Wasserhähne, die eine Wanne füllen.", "You don't know the length of the escalator, and you don't need it: calculate in escalators per second. The escalator covers 1/${n(t1, 0)} of the way per second, you 1/${n(t2, 0)}. Together it's the sum. The same principle works for two taps filling a bathtub."),
                fact = l("Messungen in der Londoner U-Bahn zeigten: Wenn alle auf beiden Seiten stehen, passen mehr Leute pro Minute hinauf, als wenn eine Seite für Gehende frei bleibt.", "Trials on the London Underground showed that more people per minute get up an escalator when everyone stands on both sides than when one side is kept free for walking."),
            )
        },
        lab("phy.drohne", Topic.Motion, H, 0.5f) {
            val t = pick(2.0, 3.0, 4.0)
            val v = 4 * t - 0.6 * t * t; val a = 4 - 1.2 * t
            task(
                question = l("""Eine Drohne steigt beim Start nach dem Gesetz §h(t) = 2\,t^2 - 0{,}2\,t^3§ (h in Metern, t in Sekunden, gilt bis t = 5 s).""", """A drone climbs after take-off according to §h(t) = 2\,t^2 - 0{,}2\,t^3§ (h in metres, t in seconds, valid up to t = 5 s)."""),
                steps = listOf(
                    num(l("Wie schnell steigt sie bei t = ${n(t, 0)} s?", "How fast is it climbing at t = ${n(t, 0)} s?"), v, "m/s", rel = 0.02, done = """§v = ${m(v)}\ \text{m/s}§""",
                        hints = listOf(l("Kernidee: Die Geschwindigkeit ist die Steigung des Höhenverlaufs, also die Ableitung.", "Key idea: velocity is the slope of the height curve, i.e. the derivative."), """§v(t) = h'(t) = 4t - 0{,}6\,t^2§""")),
                    num(l("Wie groß ist in diesem Moment die Beschleunigung?", "What is the acceleration at that moment?"), a, "m/s²", abs = 0.05, done = """§a = ${m(a)}\ \text{m/s}^2§""",
                        hints = listOf(l("Die Beschleunigung ist die Ableitung der Geschwindigkeit.", "Acceleration is the derivative of velocity."), """§a(t) = v'(t) = 4 - 1{,}2\,t§""")),
                ),
                solution = listOf("""§v(${m(t, 0)}) = 4 \cdot ${m(t, 0)} - 0{,}6 \cdot ${m(t, 0)}^2 = ${m(v)}\ \text{m/s}§""", """§a(${m(t, 0)}) = 4 - 1{,}2 \cdot ${m(t, 0)} = ${m(a)}\ \text{m/s}^2§"""),
                background = l("Ort, Geschwindigkeit und Beschleunigung hängen über Ableitungen zusammen: Die Geschwindigkeit ist, wie schnell sich der Ort gerade ändert, die Beschleunigung, wie schnell sich die Geschwindigkeit ändert. Eine negative Beschleunigung heißt hier nicht, dass die Drohne sinkt, sondern nur, dass ihr Steigen langsamer wird.", "Position, velocity and acceleration are linked by derivatives: velocity is how fast the position is changing right now, acceleration how fast the velocity is changing. A negative acceleration here doesn't mean the drone is sinking, only that its climb is slowing down."),
                fact = l("Der Flugcontroller einer Drohne misst die Beschleunigung hunderte Male pro Sekunde und rechnet daraus durch Aufsummieren Geschwindigkeit und Lage, also genau rückwärts.", "A drone's flight controller measures acceleration hundreds of times per second and adds it up to get velocity and attitude, exactly the other way round."),
            )
        },
        lab("phy.fallturm", Topic.Motion, H, 0.6f) {
            val tf = 3.0; val sb = pick(25.0, 30.0, 35.0)
            val v = G1 * tf; val a = v * v / (2 * sb); val ag = a / G1
            task(
                question = l("In einem Freifallturm fällst du 3,0 s lang frei. Danach bremsen Magnete die Gondel auf ${n(sb, 0)} m gleichmäßig bis zum Stillstand.", "In a drop tower you fall freely for 3.0 s. Then magnets brake the gondola evenly to a stop over ${n(sb, 0)} m."),
                steps = listOf(
                    num(l("Wie schnell bist du am Ende des freien Falls (in km/h)?", "How fast are you at the end of the free fall (in km/h)?"), v * 3.6, "km/h", rel = 0.02, done = """§v \approx ${m(sig(v * 3.6))}\ \text{km/h}§""",
                        hints = listOf(l("Kernidee: Im freien Fall gewinnst du jede Sekunde 9,81 m/s.", "Key idea: in free fall you gain 9.81 m/s every second."), """§v = g t§""")),
                    num(l("Wie stark bremsen die Magnete, in Vielfachen von g?", "How hard do the magnets brake, in multiples of g?"), ag, "g", rel = 0.03, done = """§a \approx ${m(ag, 1)}\, g§""",
                        hints = listOf(l("Ohne Zeitangabe hilft die Formel, die Geschwindigkeit und Weg verbindet.", "Without a time given, use the formula that links speed and distance."), """§a = \dfrac{v^2}{2s}§""")),
                ),
                solution = listOf("""§v = 9{,}81 \cdot 3\ \text{m/s} \approx ${m(v, 1)}\ \text{m/s} \approx ${m(sig(v * 3.6))}\ \text{km/h}§""", """§a = \dfrac{${m(v, 1)}^2}{2 \cdot ${m(sb, 0)}}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(a, 1)}\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(ag, 1)}\, g§"""),
                background = l("Im freien Fall fühlst du dich schwerelos, weil du genauso schnell fällst wie dein Sitz. Beim Bremsen ist es umgekehrt: Der Sitz muss dich nicht nur halten, sondern zusätzlich abbremsen. Du spürst dann die Bremsverzögerung plus die normale Schwerkraft, hier also rund ${n(ag + 1, 1)}-mal dein Gewicht.", "In free fall you feel weightless because you fall exactly as fast as your seat. When braking it's the other way round: the seat has to hold you and also slow you down. You then feel the braking plus normal gravity, here about ${n(ag + 1, 1)} times your weight."),
                fact = l("Wirbelstrombremsen brauchen keinen Strom: Kupferschwerter an der Gondel gleiten an Magneten vorbei, die Induktion bremst umso stärker, je schneller man ist. Selbst bei Stromausfall hält die Gondel sicher.", "Eddy-current brakes need no power: copper fins on the gondola slide past magnets, and induction brakes harder the faster you go. Even in a power cut the gondola stops safely."),
            )
        },
        lab("phy.mondfeder", Topic.Motion, E, 0.2f) {
            task(
                question = l("1971 ließ ein Astronaut auf dem Mond gleichzeitig einen Hammer und eine Feder aus derselben Höhe fallen. Was geschah?", "In 1971 an astronaut on the Moon dropped a hammer and a feather at the same time from the same height. What happened?"),
                steps = listOf(pick1(l("Was kam zuerst unten an?", "Which reached the ground first?"),
                    l("Beide gleichzeitig", "Both at the same time"),
                    listOf(l("Der Hammer, weil er schwerer ist", "The hammer, because it's heavier"), l("Die Feder, weil sie kaum Gewicht hat", "The feather, because it hardly weighs anything"), l("Keins von beiden, auf dem Mond schwebt alles", "Neither, everything floats on the Moon")),
                    hints = listOf(l("Kernidee: Auf der Erde bremst die Luft die Feder. Gibt es auf dem Mond Luft?", "Key idea: on Earth the air slows the feather down. Is there air on the Moon?"), l("Ein schwerer Körper wird stärker angezogen, ist aber auch genauso viel schwerer in Gang zu bringen.", "A heavy object is pulled harder, but it's also exactly that much harder to get moving.")))),
                solution = listOf(l("Ohne Luftwiderstand fallen alle Körper gleich schnell. Die doppelte Masse wird doppelt so stark angezogen, braucht aber auch die doppelte Kraft für dieselbe Beschleunigung.", "Without air resistance all objects fall equally fast. Twice the mass is pulled twice as hard, but also needs twice the force for the same acceleration.")),
                background = l("Galilei behauptete schon um 1600, dass alle Körper ohne Luft gleich schnell fallen. Der Grund: Schwere Masse (wie stark die Gravitation zieht) und träge Masse (wie schwer etwas zu beschleunigen ist) sind dasselbe. Einstein machte genau diese Gleichheit zum Ausgangspunkt seiner allgemeinen Relativitätstheorie.", "Galileo already claimed around 1600 that all objects fall equally fast without air. The reason: gravitational mass (how hard gravity pulls) and inertial mass (how hard something is to accelerate) are the same. Einstein made exactly this equality the starting point of his general theory of relativity."),
                fact = l("Der Astronaut David Scott von Apollo 15 sagte dabei in die Kamera, Galilei habe recht gehabt.", "Astronaut David Scott of Apollo 15 said to the camera that Galileo had been right."),
            )
        },
        lab("phy.zugtunnel", Topic.Motion, E, 0.5f) {
            val lz = pick(150.0, 200.0, 250.0); val lt = pick(800.0, 1000.0, 1200.0); val v = 20.0
            val t = (lz + lt) / v
            task(
                question = l("Ein ${n(lz, 0)} m langer Zug fährt mit 72 km/h (20 m/s) durch einen ${n(lt, 0)} m langen Tunnel. Wie lange dauert es von dem Moment, in dem die Lok hineinfährt, bis der letzte Wagen ganz draußen ist?", "A train ${n(lz, 0)} m long travels at 72 km/h (20 m/s) through a tunnel ${n(lt, 0)} m long. How long does it take from the moment the engine enters until the last carriage is completely out?"),
                steps = listOf(num(l("Zeit in Sekunden", "Time in seconds"), t, "s", rel = 0.02, done = """§t = ${m(t, 0)}\ \text{s}§""",
                    wrong = l("Achte auf das Ende des Zuges: Wenn die Lok den Tunnel verlässt, steckt der Zug noch drin.", "Watch the end of the train: when the engine leaves the tunnel, the train is still inside."),
                    hints = listOf(l("Kernidee: Verfolge die Spitze des Zuges. Sie muss durch den Tunnel und dann noch eine Zuglänge weiter.", "Key idea: follow the front of the train. It must go through the tunnel and then one more train length."), """§t = \dfrac{L_T + L_Z}{v}§"""))),
                solution = listOf("""§t = \dfrac{${m(lt, 0)}\ \text{m} + ${m(lz, 0)}\ \text{m}}{20\ \text{m/s}} = ${m(t, 0)}\ \text{s}§"""),
                background = l("Bei ausgedehnten Körpern hilft es, einen einzigen Punkt zu verfolgen. Nimm die Spitze der Lok: Wenn das Zugende den Tunnel verlässt, ist sie Tunnellänge plus Zuglänge gefahren. Wer nur die Tunnellänge nimmt, vergisst, dass der Zug selbst Platz braucht.", "For extended objects it helps to follow a single point. Take the front of the engine: when the end of the train leaves the tunnel, it has travelled the tunnel length plus the train length. If you take only the tunnel length, you forget that the train itself takes up space."),
                fact = l("Der Gotthard-Basistunnel ist mit 57 km der längste Eisenbahntunnel der Welt. Ein Güterzug braucht etwa eine halbe Stunde hindurch.", "The Gotthard Base Tunnel, at 57 km, is the longest railway tunnel in the world. A freight train takes about half an hour to get through."),
            )
        },
        lab("phy.marathon", Topic.Motion, E, 0.3f) {
            val sec = 2 * 3600 + 35.0
            val v = 42.195 / (sec / 3600); val t100 = 100 / (42195 / sec)
            task(
                question = l("Kelvin Kiptum lief 2023 in Chicago die Marathonstrecke von 42,195 km in 2:00:35 Stunden. Wie schnell war er im Schnitt?", "In Chicago in 2023 Kelvin Kiptum ran the marathon distance of 42.195 km in 2:00:35 hours. How fast was he on average?"),
                steps = listOf(num(l("Durchschnitt in km/h", "Average in km/h"), v, "km/h", rel = 0.01, done = """§v \approx ${m(v, 1)}\ \text{km/h}§""",
                    hints = listOf(l("Kernidee: Geschwindigkeit = Strecke durch Zeit. Die Zeit zuerst in Stunden umrechnen: 35 s sind 35/3600 h.", "Key idea: speed = distance over time. First convert the time into hours: 35 s is 35/3600 h."), """§v = \dfrac{42{,}195\ \text{km}}{2{,}0097\ \text{h}}§"""))),
                solution = listOf("""§t = 2\ \text{h} + \tfrac{35}{3600}\ \text{h} \approx 2{,}0097\ \text{h}§""", """§v = \dfrac{42{,}195}{2{,}0097}\ \text{km/h} \approx ${m(v, 1)}\ \text{km/h}§"""),
                background = l("Die Durchschnittsgeschwindigkeit sagt nichts über einzelne Abschnitte, nur über das Ganze: Strecke geteilt durch Gesamtzeit. Bei Marathonläufen ist sie aber fast konstant, weil Spitzenläufer ihr Tempo sehr gleichmäßig halten. Jedes Abbremsen und Wiederbeschleunigen kostet Kraft.", "The average speed says nothing about individual sections, only about the whole: distance divided by total time. In a marathon, though, it is almost constant because elite runners keep their pace very even. Every slowing down and speeding up again costs energy."),
                fact = l("Das sind 100 m in etwa ${n(t100, 1)} Sekunden, zwei Stunden lang ohne Pause. Die meisten Menschen schaffen dieses Tempo keine 200 m weit.", "That's 100 m in about ${n(t100, 1)} seconds, for two hours without a break. Most people can't keep that pace up for even 200 m."),
            )
        },
    )

    private val forces = listOf(
        lab("phy.tauziehen", Topic.Forces, E, 0.3f) {
            val f = pick(500.0, 600.0, 800.0)
            task(
                question = l("Beim Tauziehen ziehen beide Mannschaften mit je ${n(f, 0)} N am Seil, und nichts bewegt sich. Welche Kraft wirkt im Seil (die Seilspannung)?", "In a tug of war both teams pull on the rope with ${n(f, 0)} N each, and nothing moves. What force acts in the rope (the tension)?"),
                steps = listOf(pick1(l("Seilspannung", "Rope tension"), "${n(f, 0)} N", listOf("${n(2 * f, 0)} N", "0 N", "${n(f / 2, 0)} N"),
                    hints = listOf(l("Kernidee: Stell dir vor, eine Mannschaft bindet das Seil an einen Baum. Würde sich für die andere etwas ändern?", "Key idea: imagine one team ties the rope to a tree. Would anything change for the other team?"), l("Eine Federwaage im Seil würde zeigen, mit welcher Kraft an jedem Ende gezogen wird.", "A spring balance in the rope would show the force pulling at each end.")))),
                solution = listOf(l("Die Spannung im Seil ist die Kraft, mit der an jedem Ende gezogen wird: ${n(f, 0)} N. Die beiden Kräfte addieren sich nicht, sie halten sich das Gleichgewicht.", "The tension in the rope is the force pulling at each end: ${n(f, 0)} N. The two forces don't add up; they balance each other.")),
                background = l("Ein Seil kann nur ziehen, und überall in einem ruhenden, leichten Seil wirkt dieselbe Spannung. Die andere Mannschaft ersetzt einfach den Baum: Ohne Gegenzug am anderen Ende gäbe es gar keine Spannung. Gewonnen wird übrigens nicht mit Armkraft, sondern mit den Füßen: Wer den Boden stärker nach vorn drückt, zieht den anderen.", "A rope can only pull, and the same tension acts everywhere in a light rope at rest. The other team just takes the place of the tree: without a pull at the other end there would be no tension at all. Tug of war isn't won with arm strength, by the way, but with the feet: whoever pushes the ground forward harder pulls the other team."),
                fact = l("Tauziehen war von 1900 bis 1920 olympische Disziplin.", "Tug of war was an Olympic sport from 1900 to 1920."),
            )
        },
        lab("phy.zweiwaagen", Topic.Forces, M, 0.5f) {
            val x = pick(0.4, 0.6, 0.5); val mk = 40.0; val mb = 8.0; val L = 1.8
            val right = (mk * x + mb * L / 2) / L; val left = mk + mb - right
            task(
                question = l("Ein 1,80 m langes Brett (8 kg) liegt mit seinen Enden auf zwei Personenwaagen. Ein Kind (40 kg) setzt sich ${n(x * 100, 0)} cm vom linken Ende entfernt darauf.", "A plank 1.80 m long (8 kg) rests with its ends on two bathroom scales. A child (40 kg) sits on it ${n(x * 100, 0)} cm from the left end."),
                steps = listOf(
                    num(l("Was zeigt die rechte Waage (in kg)?", "What does the right scale show (in kg)?"), right, "kg", rel = 0.02, done = """§m_R \approx ${m(right, 1)}\ \text{kg}§""",
                        hints = listOf(l("Kernidee: Das Brett dreht sich nicht. Nimm das linke Ende als Drehpunkt, dann fällt die linke Waage aus der Rechnung.", "Key idea: the plank doesn't rotate. Take the left end as the pivot, then the left scale drops out of the calculation."), """§m_R \cdot 1{,}8 = 40 \cdot ${m(x)} + 8 \cdot 0{,}9§""")),
                    num(l("Und die linke?", "And the left one?"), left, "kg", rel = 0.02, done = """§m_L \approx ${m(left, 1)}\ \text{kg}§""",
                        hints = listOf(l("Zusammen tragen beide Waagen alles: Kind plus Brett.", "Together the two scales carry everything: child plus plank."), """§m_L = 48 - m_R§""")),
                ),
                solution = listOf("""§m_R = \dfrac{40 \cdot ${m(x)} + 8 \cdot 0{,}9}{1{,}8}\ \text{kg} \approx ${m(right, 1)}\ \text{kg}§""", """§m_L = 48 - ${m(right, 1)} \approx ${m(left, 1)}\ \text{kg}§"""),
                background = l("Ein Körper ist im Gleichgewicht, wenn sich die Kräfte aufheben und auch die Drehmomente. Der Trick ist, den Drehpunkt geschickt zu wählen: Liegt er dort, wo eine unbekannte Kraft angreift, verschwindet sie aus der Gleichung. Das Gewicht des Brettes wirkt dabei so, als säße es ganz in der Mitte.", "A body is in equilibrium when the forces cancel and so do the torques. The trick is to choose the pivot cleverly: if it's where an unknown force acts, that force disappears from the equation. The plank's weight acts as if it were all at its middle."),
                fact = l("Auf genau diese Weise bestimmt man den Schwerpunkt eines Autos oder Flugzeugs: Man stellt jedes Rad auf eine eigene Waage.", "This is exactly how the centre of gravity of a car or plane is measured: each wheel is put on its own scale."),
            )
        },
        lab("phy.seiltaenzer", Topic.Forces, H, 0.6f) {
            val sag = pick(0.3, 0.5, 1.0); val half = 10.0; val mass = 60.0
            val th = atan(sag / half); val tension = mass * G1 / (2 * kotlin.math.sin(th))
            task(
                question = l("Ein Seiltänzer (60 kg) steht genau in der Mitte eines 20 m langen Seils. Unter ihm hängt das Seil ${n(sag * 100, 0)} cm durch. Wie stark ist das Seil gespannt?", "A tightrope walker (60 kg) stands exactly in the middle of a 20 m rope. Under him the rope sags by ${n(sag * 100, 0)} cm. How tight is the rope?"),
                steps = listOf(num(l("Seilkraft in Kilonewton", "Rope tension in kilonewtons"), tension / 1000, "kN", rel = 0.03, done = """§F \approx ${m(sig(tension / 1000))}\ \text{kN}§""",
                    hints = listOf(l("Kernidee: Beide Seilhälften ziehen schräg nach oben. Nur ihr senkrechter Anteil trägt das Gewicht, und der ist bei flachem Seil winzig.", "Key idea: both halves of the rope pull at an angle upwards. Only their vertical part carries the weight, and with a flat rope that part is tiny."), """§2F \sin\alpha = m g,\quad \tan\alpha = \dfrac{${m(sag)}}{10}§"""))),
                solution = listOf("""§\alpha = \arctan\dfrac{${m(sag)}}{10} \approx ${m(Math.toDegrees(th), 2)}^\circ§""", """§F = \dfrac{60 \cdot 9{,}81}{2 \sin ${m(Math.toDegrees(th), 2)}^\circ}\ \text{N} \approx ${m(sig(tension / 1000))}\ \text{kN}§"""),
                background = l("Ein Seil kann nur entlang seiner Richtung ziehen. Ist es fast waagrecht, muss die Spannung riesig werden, damit der kleine senkrechte Anteil das Gewicht trägt. Deshalb kann man ein Seil nie völlig gerade spannen, sobald etwas daran hängt, und deshalb reißen straffe Wäscheleinen eher als lockere.", "A rope can only pull along its own direction. If it is almost horizontal, the tension must become huge for the small vertical part to carry the weight. That's why you can never pull a rope completely straight once something hangs on it, and why tight washing lines snap sooner than slack ones."),
                fact = l("Das Seil zieht mit dem Gewicht von etwa ${n(tension / G1 / 1000, 1)} Tonnen an den Masten, obwohl nur 60 kg darauf stehen.", "The rope pulls on the masts with the weight of about ${n(tension / G1 / 1000, 1)} tonnes, although only 60 kg stands on it."),
            )
        },
        lab("phy.tablett", Topic.Forces, M, 0.4f) {
            val mu = pick(0.2, 0.3, 0.4); val a = mu * G1
            task(
                question = l("Ein Kellner trägt ein Glas auf einem Tablett. Die Haftreibungszahl zwischen Glas und Tablett ist ${n(mu)}. Wie stark darf er höchstens beschleunigen, bevor das Glas rutscht?", "A waiter carries a glass on a tray. The coefficient of static friction between glass and tray is ${n(mu)}. What is the largest acceleration before the glass slides?"),
                steps = listOf(num(l("Größte Beschleunigung", "Largest acceleration"), a, "m/s²", rel = 0.02, done = """§a_{max} \approx ${m(a, 2)}\ \text{m/s}^2§""",
                    hints = listOf(l("Kernidee: Nur die Haftreibung kann das Glas mitnehmen. Ihr Höchstwert ist μ mal Gewichtskraft.", "Key idea: only static friction can carry the glass along. Its maximum is μ times the weight."), """§m a = \mu m g \Rightarrow a = \mu g§"""))),
                solution = listOf("""§a_{max} = ${m(mu)} \cdot 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(a, 2)}\ \tfrac{\text{m}}{\text{s}^2}§"""),
                background = l("Das Glas wird nur durch Reibung mitgenommen. Haftreibung passt sich an, aber nur bis zu einer Grenze: μ mal Gewichtskraft. Erstaunlich ist, dass die Masse herausfällt. Ein volles Glas rutscht bei derselben Beschleunigung wie ein leeres, weil es zwar stärker angedrückt wird, aber auch mehr Kraft zum Beschleunigen braucht.", "The glass is carried along only by friction. Static friction adjusts itself, but only up to a limit: μ times the weight. The surprising part is that the mass cancels out. A full glass slides at the same acceleration as an empty one, because although it is pressed down harder it also needs more force to accelerate."),
                fact = l("Bei Kellnerwettbewerben in Paris laufen die Teilnehmer seit über 100 Jahren mit vollem Tablett durch die Straßen.", "In waiters' races in Paris, competitors have been running through the streets with full trays for over 100 years."),
            )
        },
        lab("phy.radiergummi", Topic.Forces, M, 0.5f) {
            val ang = pick(25.0, 30.0, 35.0, 40.0); val mu = tan(Math.toRadians(ang))
            task(
                question = l("Ein Radiergummi liegt auf einem Buch. Du hebst das Buch an einer Seite langsam an. Bei ${n(ang, 0)}° Neigung beginnt der Radiergummi zu rutschen. Wie groß ist die Haftreibungszahl?", "An eraser lies on a book. You slowly lift one side of the book. At an angle of ${n(ang, 0)}° the eraser starts to slide. What is the coefficient of static friction?"),
                steps = listOf(num(l("Haftreibungszahl μ", "Coefficient of static friction μ"), mu, "", rel = 0.02, done = """§\mu \approx ${m(mu, 2)}§""",
                    hints = listOf(l("Kernidee: Kurz vor dem Rutschen ist die Hangabtriebskraft genau so groß wie die größte Haftreibung.", "Key idea: just before sliding, the downhill force equals the largest static friction."), """§m g \sin\alpha = \mu\, m g \cos\alpha \Rightarrow \mu = \tan\alpha§"""))),
                solution = listOf("""§\mu = \tan ${m(ang, 0)}^\circ \approx ${m(mu, 2)}§"""),
                background = l("Auf einer schiefen Ebene zerlegt man die Gewichtskraft in zwei Teile: einen, der den Körper hangabwärts zieht, und einen, der ihn auf die Fläche drückt. Mit steigendem Winkel wächst der erste, der zweite schrumpft. Der Grenzwinkel hängt nur von μ ab, nicht von der Masse: ein einfaches Messgerät für Reibung.", "On an incline you split the weight into two parts: one pulling the object down the slope and one pressing it onto the surface. As the angle grows, the first increases and the second shrinks. The critical angle depends only on μ, not on the mass: a simple instrument for measuring friction."),
                fact = l("Schüttguthaufen wie Sand oder Kies haben aus demselben Grund immer denselben Böschungswinkel, egal wie hoch sie werden.", "Piles of loose material like sand or gravel always have the same slope angle for the same reason, however high they get."),
            )
        },
        lab("phy.flaschenzug", Topic.Forces, E, 0.4f) {
            val n4 = pick(2, 4, 6); val mass = pick(60.0, 80.0, 120.0)
            val f = mass * G1 / n4
            task(
                question = l("Mit einem Flaschenzug hebst du eine Last von ${n(mass, 0)} kg. Sie hängt an ${n4} tragenden Seilstücken. Mit welcher Kraft musst du am losen Seilende ziehen (Reibung und Rollen vernachlässigt)?", "You lift a load of ${n(mass, 0)} kg with a block and tackle. It hangs from ${n4} supporting rope segments. What force must you pull with on the free end (friction and pulleys neglected)?"),
                steps = listOf(num(l("Zugkraft in Newton", "Pulling force in newtons"), f, "N", rel = 0.02, done = """§F \approx ${m(sig(f))}\ \text{N}§""",
                    hints = listOf(l("Kernidee: Im ganzen Seil herrscht dieselbe Kraft. Jedes tragende Seilstück hält einen gleichen Teil der Last.", "Key idea: the same force acts throughout the rope. Each supporting segment holds an equal share of the load."), """§F = \dfrac{m g}{n}§"""))),
                solution = listOf("""§F = \dfrac{${m(mass, 0)} \cdot 9{,}81}{${n4}}\ \text{N} \approx ${m(sig(f))}\ \text{N}§"""),
                background = l("Ein Flaschenzug teilt die Last auf mehrere Seilstücke auf. Dafür musst du aber entsprechend mehr Seil ziehen: Für 1 m Hub ${n4} m Seil. Die Arbeit, also Kraft mal Weg, bleibt gleich. Das ist die goldene Regel der Mechanik: Was man an Kraft spart, zahlt man mit Weg.", "A block and tackle shares the load among several rope segments. In return you must pull correspondingly more rope: ${n4} m of rope for 1 m of lift. The work, force times distance, stays the same. That's the golden rule of mechanics: what you save in force you pay for in distance."),
                fact = l("Archimedes soll mit einem Flaschenzug allein ein voll beladenes Schiff an Land gezogen haben.", "Archimedes is said to have pulled a fully loaded ship onto land single-handedly with a block and tackle."),
            )
        },
        lab("phy.federkombi", Topic.Forces, H, 0.5f) {
            val k = pick(200.0, 300.0, 400.0); val mass = pick(2.0, 3.0)
            val f = mass * G1; val xs = f / (k / 2); val xp = f / (2 * k)
            task(
                question = l("Du hast zwei gleiche Federn mit je D = ${n(k, 0)} N/m und hängst eine ${n(mass, 0)}-kg-Hantelscheibe daran.", "You have two identical springs, each with k = ${n(k, 0)} N/m, and hang a ${n(mass, 0)} kg weight plate from them."),
                steps = listOf(
                    num(l("Wie weit dehnen sie sich zusammen, wenn sie hintereinander hängen (in cm)?", "How far do they stretch together when hung one below the other (in cm)?"), xs * 100, "cm", rel = 0.02, done = """§x \approx ${m(sig(xs * 100))}\ \text{cm}§""",
                        hints = listOf(l("Kernidee: Hintereinander trägt jede Feder das volle Gewicht und dehnt sich voll. Die Wege addieren sich.", "Key idea: in series each spring carries the full weight and stretches fully. The stretches add up."), """§D_{ges} = \dfrac{D}{2}§""")),
                    num(l("Und nebeneinander, beide am Gewicht befestigt (in cm)?", "And side by side, both attached to the weight (in cm)?"), xp * 100, "cm", rel = 0.02, done = """§x \approx ${m(sig(xp * 100))}\ \text{cm}§""",
                        hints = listOf(l("Nebeneinander teilen sich die Federn die Last.", "Side by side the springs share the load."), """§D_{ges} = 2D§""")),
                ),
                solution = listOf("""§x_{Reihe} = \dfrac{${m(mass, 0)} \cdot 9{,}81}{${m(k / 2, 0)}}\ \text{m} \approx ${m(sig(xs * 100))}\ \text{cm}§""", """§x_{parallel} = \dfrac{${m(mass, 0)} \cdot 9{,}81}{${m(2 * k, 0)}}\ \text{m} \approx ${m(sig(xp * 100))}\ \text{cm}§"""),
                background = l("Federn hintereinander werden weicher, nebeneinander härter. Hintereinander ist wie eine doppelt so lange Feder: Jedes Stück dehnt sich gleich, insgesamt also doppelt. Nebeneinander ist wie eine doppelt so dicke Feder. Bei elektrischen Widerständen ist es genau umgekehrt.", "Springs in series get softer, springs in parallel stiffer. In series is like a spring twice as long: each piece stretches the same, so twice as much overall. In parallel is like a spring twice as thick. For electrical resistors it's exactly the other way round."),
                fact = l("Ein Trampolin hat ringsum über hundert Federn parallel. Erst zusammen sind sie hart genug, um einen Menschen hochzuwerfen.", "A trampoline has over a hundred springs in parallel all round. Only together are they stiff enough to throw a person up."),
            )
        },
        lab("phy.rakete", Topic.Forces, M, 0.5f) {
            val mass = 549000.0; val thrust = 7.6e6
            val a = thrust / mass - G1
            task(
                question = l("Eine Falcon-9-Rakete wiegt beim Start 549 t. Ihre neun Triebwerke liefern zusammen 7,6 MN Schub. Mit welcher Beschleunigung hebt sie ab?", "A Falcon 9 rocket has a mass of 549 t at lift-off. Its nine engines deliver 7.6 MN of thrust together. With what acceleration does it lift off?"),
                steps = listOf(num(l("Startbeschleunigung", "Lift-off acceleration"), a, "m/s²", rel = 0.03, done = """§a \approx ${m(a, 1)}\ \text{m/s}^2§""",
                    hints = listOf(l("Kernidee: Ein Teil des Schubs wird schon gebraucht, um das Gewicht zu tragen. Nur der Rest beschleunigt.", "Key idea: part of the thrust is already needed just to carry the weight. Only the rest accelerates."), """§m a = F_S - m g§"""))),
                solution = listOf("""§a = \dfrac{7{,}6 \cdot 10^6\ \text{N}}{5{,}49 \cdot 10^5\ \text{kg}} - 9{,}81\ \tfrac{\text{m}}{\text{s}^2} \approx ${m(a, 1)}\ \tfrac{\text{m}}{\text{s}^2}§"""),
                background = l("Newtons zweites Gesetz gilt für die Summe aller Kräfte. Die Gewichtskraft zieht die Rakete nach unten, der Schub drückt sie nach oben. Läge der Schub unter dem Gewicht, bliebe sie trotz donnernder Triebwerke auf der Rampe stehen. Weil sie während des Flugs Treibstoff verbrennt, wird sie immer leichter und beschleunigt immer stärker.", "Newton's second law applies to the sum of all forces. Weight pulls the rocket down, thrust pushes it up. If the thrust were less than the weight, it would stay on the pad despite its roaring engines. Because it burns fuel during flight it gets lighter and lighter and accelerates more and more."),
                fact = l("Mehr als 90 % der Startmasse einer Rakete ist Treibstoff. Die Nutzlast macht oft nur 3 bis 4 % aus.", "More than 90% of a rocket's lift-off mass is propellant. The payload is often only 3 to 4%."),
            )
        },
        lab("phy.pferdwagen", Topic.Forces, M, 0.6f) {
            task(
                question = l("Ein Pferd zieht einen Wagen. Nach Newtons drittem Gesetz zieht der Wagen genauso stark am Pferd zurück. Warum kommen die beiden trotzdem vorwärts?", "A horse pulls a cart. According to Newton's third law the cart pulls back on the horse just as hard. Why do the two still move forward?"),
                steps = listOf(pick1(l("Die richtige Erklärung", "The right explanation"),
                    l("Die beiden Kräfte wirken auf verschiedene Körper. Das Pferd kommt voran, weil der Boden es stärker nach vorn schiebt, als der Wagen es zurückzieht.", "The two forces act on different bodies. The horse moves forward because the ground pushes it forward harder than the cart pulls it back."),
                    listOf(l("Das Pferd zieht in Wahrheit ein bisschen stärker als der Wagen zurück.", "The horse actually pulls a little harder than the cart pulls back."), l("Newtons drittes Gesetz gilt nur für ruhende Körper.", "Newton's third law only applies to bodies at rest."), l("Der Wagen zieht erst zurück, wenn er sich schon bewegt.", "The cart only pulls back once it is already moving.")),
                    hints = listOf(l("Kernidee: Für die Bewegung des Pferdes zählen nur Kräfte, die auf das Pferd wirken.", "Key idea: for the horse's motion only forces that act on the horse count."), l("Woran stößt sich das Pferd mit den Hufen ab?", "What does the horse push off against with its hooves?")))),
                solution = listOf(l("Kraft und Gegenkraft greifen an zwei verschiedenen Körpern an und können sich deshalb nie gegenseitig aufheben. Auf das Pferd wirken der Zug des Wagens nach hinten und die Reibung des Bodens nach vorn. Ist die zweite größer, beschleunigt es.", "Action and reaction act on two different bodies, so they can never cancel each other. On the horse act the cart's pull backwards and the ground's friction forwards. If the second is larger, it accelerates.")),
                background = l("Das dritte Gesetz sagt nur: Wenn A auf B drückt, drückt B gleich stark auf A zurück. Ob sich etwas bewegt, entscheidet aber die Summe der Kräfte auf einen einzelnen Körper. Das Pferd drückt mit den Hufen den Boden nach hinten, der Boden drückt es nach vorn. Auf glattem Eis hätte das Pferd keine Chance.", "The third law only says: if A pushes on B, B pushes back on A just as hard. But whether something moves is decided by the sum of the forces on a single body. The horse pushes the ground backwards with its hooves, and the ground pushes it forwards. On smooth ice the horse wouldn't stand a chance."),
                fact = l("Genauso läufst du: Bei jedem Schritt schiebst du die Erde ein winziges Stück nach hinten.", "That's how you walk too: with every step you push the Earth a tiny bit backwards."),
            )
        },
        lab("phy.zugkupplung", Topic.Forces, H, 0.5f) {
            val nw = 3; val mw = 40000.0; val a = pick(0.3, 0.5, 0.6)
            val f1 = nw * mw * a; val f3 = mw * a
            task(
                question = l("Eine Lok zieht drei gleiche Wagen mit je 40 t und beschleunigt mit ${n(a)} m/s². Reibung vernachlässigen wir.", "A locomotive pulls three identical wagons of 40 t each and accelerates at ${n(a)} m/s². We neglect friction."),
                steps = listOf(
                    num(l("Welche Kraft wirkt in der Kupplung zwischen Lok und erstem Wagen (in kN)?", "What force acts in the coupling between the locomotive and the first wagon (in kN)?"), f1 / 1000, "kN", rel = 0.02, done = """§F_1 = ${m(f1 / 1000, 0)}\ \text{kN}§""",
                        hints = listOf(l("Kernidee: Diese Kupplung muss alle drei Wagen beschleunigen.", "Key idea: this coupling has to accelerate all three wagons."), """§F_1 = 3 m a§""")),
                    num(l("Und zwischen dem zweiten und dem dritten Wagen (in kN)?", "And between the second and third wagon (in kN)?"), f3 / 1000, "kN", rel = 0.02, done = """§F_3 = ${m(f3 / 1000, 0)}\ \text{kN}§""",
                        hints = listOf(l("Was hängt hinter dieser Kupplung noch dran?", "What is still hanging behind this coupling?"), """§F_3 = m a§""")),
                ),
                solution = listOf("""§F_1 = 3 \cdot 40\,000\ \text{kg} \cdot ${m(a)}\ \tfrac{\text{m}}{\text{s}^2} = ${m(f1 / 1000, 0)}\ \text{kN}§""", """§F_3 = 40\,000 \cdot ${m(a)}\ \text{N} = ${m(f3 / 1000, 0)}\ \text{kN}§"""),
                background = l("Der Trick ist, das System richtig abzugrenzen. Schneidet man gedanklich an einer Kupplung, muss die Kraft dort alles beschleunigen, was dahinter hängt, und sonst nichts. Deshalb ist die vorderste Kupplung eines Zuges am stärksten belastet und reißt als erste, wenn ein Zug zu schwer ist.", "The trick is to draw the boundary of the system correctly. If you imagine cutting at a coupling, the force there has to accelerate everything behind it and nothing else. That's why the front coupling of a train carries the most load and is the first to break if a train is too heavy."),
                fact = l("Lange Güterzüge in den USA haben deshalb oft zusätzliche Loks in der Mitte oder am Ende, damit die vorderen Kupplungen nicht reißen.", "That's why long freight trains in the USA often have extra locomotives in the middle or at the end, so the front couplings don't break."),
            )
        },
        lab("phy.kette", Topic.Forces, X, 0.6f) {
            val mu = pick(0.25, 0.3, 0.4); val frac = mu / (1 + mu)
            task(
                question = l("Eine Kette liegt auf einem Tisch, ein Stück hängt über die Kante. Die Haftreibungszahl zwischen Kette und Tisch ist ${n(mu)}. Welcher Anteil der Kette darf höchstens herunterhängen, ohne dass sie abrutscht?", "A chain lies on a table with part of it hanging over the edge. The coefficient of static friction between chain and table is ${n(mu)}. What is the largest fraction of the chain that can hang down without it slipping off?"),
                steps = listOf(num(l("Anteil in Prozent", "Fraction in percent"), frac * 100, "%", abs = 0.6, done = """§f \approx ${m(frac * 100, 1)}\ \text{%}§""",
                    hints = listOf(l("Kernidee: Das hängende Stück zieht mit seinem Gewicht. Halten kann nur die Reibung des liegenden Stücks.", "Key idea: the hanging piece pulls with its weight. Only the friction of the lying piece can hold it."), """§f\, m g = \mu (1 - f)\, m g \Rightarrow f = \dfrac{\mu}{1 + \mu}§"""))),
                solution = listOf("""§f = \dfrac{${m(mu)}}{1 + ${m(mu)}} \approx ${m(frac, 3)} = ${m(frac * 100, 1)}\ \text{%}§"""),
                background = l("Mit jedem Glied, das über die Kante rutscht, wird der Zug größer und die haltende Reibung kleiner. Darum gibt es eine scharfe Grenze und danach kein Halten mehr: Die Kette rauscht immer schneller vom Tisch. Solche Kipp-Punkte, an denen ein kleiner Schubs alles ins Rutschen bringt, findet man auch bei Lawinen.", "With every link that slides over the edge, the pull gets bigger and the holding friction smaller. That's why there is a sharp limit and no stopping after it: the chain rushes off the table faster and faster. Such tipping points, where a small nudge sets everything sliding, are also found in avalanches."),
                fact = l("Fällt eine Kette aus einem hohen Becher, kann sie sich sogar wie ein Springbrunnen über den Rand heben, der sogenannte Mould-Effekt.", "If a chain falls out of a tall beaker it can even rise over the rim like a fountain, the so-called Mould effect."),
            )
        },
    )
}
