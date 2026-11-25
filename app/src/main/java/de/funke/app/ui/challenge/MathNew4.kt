package de.funke.app.ui.challenge

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/* Mathe-Ausbau (seit 13.2): Geometrie, Wahrscheinlichkeit & Kombinatorik. Alle Texte selbst geschrieben. */

private val E = Difficulty.Easy
private val M = Difficulty.Medium
private val H = Difficulty.Hard
private val X = Difficulty.Expert

private fun fact4(n: Int): Double = (1..n).fold(1.0) { a, b -> a * b }
private fun binom4(n: Int, k: Int): Double = fact4(n) / (fact4(k) * fact4(n - k))

object MathNew4 {
    val all: List<TaskDef> get() = geometry + probability

    private val geometry = listOf(
        lab("ma.bogenmass", Topic.Geometry, E, 0.5f) {
            val rad = pick(1.0, 2.0, 0.5); val deg = rad * 180 / PI
            task(
                question = l("""Ein Winkel misst ${n(rad, 1)} im Bogenmaß (Radiant). Wie viel Grad sind das?""", """An angle measures ${n(rad, 1)} in radians. How many degrees is that?"""),
                steps = listOf(num(l("Grad", "Degrees"), deg, "°", abs = 0.2, done = """§\approx ${m(deg, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: Ein ganzer Kreis sind 360° oder 2π. Also ist π dasselbe wie 180°.", "Key idea: a whole circle is 360° or 2π. So π is the same as 180°."), """§\alpha = ${m(rad, 1)} \cdot \dfrac{180^\circ}{\pi}§"""))),
                solution = listOf("""§${m(rad, 1)} \cdot \dfrac{180^\circ}{\pi} \approx ${m(deg, 1)}^\circ§"""),
                background = l("Das Bogenmaß misst einen Winkel durch die Länge des Bogens auf einem Kreis mit Radius 1. Ein Radiant ist der Winkel, bei dem der Bogen genau so lang ist wie der Radius, etwa 57,3°. Grad sind eine willkürliche Einteilung aus Babylon, das Bogenmaß dagegen ist natürlich, und deshalb rechnen Mathematik und Physik damit.", "Radian measure gives an angle by the length of its arc on a circle of radius 1. One radian is the angle at which the arc is exactly as long as the radius, about 57.3°. Degrees are an arbitrary division from Babylon; radians, by contrast, are natural, which is why mathematics and physics use them."),
            )
        },
        lab("ma.abstandkoord", Topic.Geometry, E, 0.4f) {
            val (dx, dy) = pick(6 to 8, 5 to 12, 3 to 4, 8 to 15, 9 to 12); val x1 = int(-3, 4); val y1 = int(-3, 4)
            val d = sqrt((dx * dx + dy * dy).toDouble())
            task(
                question = l("Wie weit liegen die Punkte A(${x1} | ${y1}) und B(${x1 + dx} | ${y1 + dy}) voneinander entfernt?", "How far apart are the points A(${x1} | ${y1}) and B(${x1 + dx} | ${y1 + dy})?"),
                steps = listOf(num(l("Abstand", "Distance"), d, "", abs = 0.01, done = """§d = ${m(d, 0)}§""",
                    hints = listOf(l("Kernidee: Der waagrechte und der senkrechte Unterschied bilden die beiden kurzen Seiten eines rechtwinkligen Dreiecks.", "Key idea: the horizontal and vertical differences form the two short sides of a right triangle."), """§d = \sqrt{(\Delta x)^2 + (\Delta y)^2}§"""))),
                solution = listOf("""§d = \sqrt{${dx}^2 + ${dy}^2} = \sqrt{${dx * dx + dy * dy}} = ${m(d, 0)}§"""),
                background = l("Die Abstandsformel ist einfach der Satz des Pythagoras im Koordinatensystem. Navigationsgeräte, Spiele und Grafikprogramme berechnen sie millionenfach. Im Raum kommt einfach eine dritte Koordinate dazu: d = √(Δx² + Δy² + Δz²).", "The distance formula is simply Pythagoras' theorem in a coordinate system. Navigation devices, games and graphics software calculate it millions of times. In space you just add a third coordinate: d = √(Δx² + Δy² + Δz²)."),
            )
        },
        lab("ma.trapez", Topic.Geometry, E, 0.4f) {
            val a = pick(8, 10, 6); val c = a + pick(4, 2, 6); val h = pick(5, 4, 3)
            val area = (a + c) / 2.0 * h
            task(
                question = l("Ein Garten hat die Form eines Trapezes: Die beiden parallelen Seiten sind ${a} m und ${c} m lang, ihr Abstand ist ${h} m. Wie groß ist die Fläche?", "A garden is shaped like a trapezium: the two parallel sides are ${a} m and ${c} m long, and the distance between them is ${h} m. What is the area?"),
                steps = listOf(num(l("Fläche in m²", "Area in m²"), area, "m²", abs = 0.01, done = """§A = ${m(area, 1)}\ \text{m}^2§""",
                    hints = listOf(l("Kernidee: Ein Trapez ist so groß wie ein Rechteck mit der mittleren Breite der beiden parallelen Seiten.", "Key idea: a trapezium has the same area as a rectangle with the average width of the two parallel sides."), """§A = \dfrac{a + c}{2} \cdot h§"""))),
                solution = listOf("""§A = \dfrac{${a} + ${c}}{2} \cdot ${h} = ${m(area, 1)}\ \text{m}^2§"""),
                background = l("Legt man zwei gleiche Trapeze verkehrt herum aneinander, entsteht ein Parallelogramm mit der Grundseite a + c. Die Hälfte davon ist das Trapez. Mit genau diesem Trick lassen sich viele Flächenformeln herleiten, ohne sie auswendig zu lernen.", "If you put two identical trapeziums together, one upside down, you get a parallelogram with base a + c. Half of that is the trapezium. With exactly this trick many area formulas can be derived without memorising them."),
            )
        },
        lab("ma.geschenkbox", Topic.Geometry, E, 0.4f) {
            val a = pick(30, 25, 40); val b = pick(20, 15); val c = pick(10, 12, 8)
            val o = 2 * (a * b + a * c + b * c)
            task(
                question = l("Ein Geschenkkarton ist ${a} cm lang, ${b} cm breit und ${c} cm hoch. Wie viel Geschenkpapier braucht man mindestens, um ihn ohne Überlappung zu bekleben?", "A gift box is ${a} cm long, ${b} cm wide and ${c} cm high. What is the least amount of wrapping paper needed to cover it with no overlap?"),
                steps = listOf(num(l("Fläche in cm²", "Area in cm²"), o.toDouble(), "cm²", abs = 0.0, done = """§O = ${o}\ \text{cm}^2§""",
                    hints = listOf(l("Kernidee: Ein Quader hat sechs Flächen, je zwei gleiche.", "Key idea: a cuboid has six faces, in three equal pairs."), """§O = 2(ab + ac + bc)§"""))),
                solution = listOf("""§O = 2(${a * b} + ${a * c} + ${b * c}) = ${o}\ \text{cm}^2§"""),
                background = l("Die Oberfläche eines Körpers ist die Fläche seines aufgeklappten Netzes. Für einen Quader sind das sechs Rechtecke. In der Praxis braucht man mehr Papier, weil man überlappen und falten muss. Verpackungsdesigner optimieren solche Netze, um möglichst wenig Karton zu verschwenden.", "The surface area of a solid is the area of its unfolded net. For a cuboid that's six rectangles. In practice you need more paper because you have to overlap and fold. Packaging designers optimise such nets to waste as little cardboard as possible."),
            )
        },
        lab("ma.umfangform", Topic.Geometry, E, 0.6f) {
            task(
                question = l("Du hast 40 m Zaun. Mit welcher Form umzäunst du die größte Fläche?", "You have 40 m of fence. With which shape do you enclose the largest area?"),
                steps = listOf(pick1(l("Form", "Shape"), l("Kreis", "Circle"), listOf(l("Quadrat", "Square"), l("Gleichseitiges Dreieck", "Equilateral triangle"), l("Langes, schmales Rechteck", "Long, narrow rectangle")),
                    hints = listOf(l("Kernidee: Vergleiche konkret. Ein Quadrat mit 10 m Seiten hat 100 m². Ein Kreis mit 40 m Umfang hat den Radius 40/(2π).", "Key idea: compare concretely. A square with 10 m sides has 100 m². A circle with a 40 m circumference has a radius of 40/(2π)."), """§A_{Kreis} = \pi \left(\dfrac{40}{2\pi}\right)^2 \approx 127\ \text{m}^2§""".let { l(it, it.replace("Kreis", "circle")) }))),
                solution = listOf(l("Quadrat: 100 m², gleichseitiges Dreieck: etwa 77 m², Kreis: etwa 127 m². Unter allen Formen mit gleichem Umfang hat der Kreis die größte Fläche.", "Square: 100 m², equilateral triangle: about 77 m², circle: about 127 m². Of all shapes with the same perimeter, the circle has the largest area.")),
                background = l("Das ist die isoperimetrische Ungleichung, eines der ältesten Probleme der Mathematik. Die Sage erzählt, Königin Dido durfte so viel Land haben, wie sie mit einer Ochsenhaut umspannen konnte. Sie schnitt die Haut in Streifen und legte einen Halbkreis an der Küste. Seifenblasen sind aus demselben Grund rund.", "This is the isoperimetric inequality, one of the oldest problems in mathematics. Legend has it that Queen Dido was allowed as much land as she could enclose with an ox hide. She cut the hide into strips and laid out a semicircle along the coast. Soap bubbles are round for the same reason."),
            )
        },
        lab("ma.seilerde", Topic.Geometry, M, 0.6f) {
            val extra = pick(1.0, 2.0, 10.0); val gap = extra / (2 * PI) * 100
            task(
                question = l("Ein Seil liegt straff einmal um den Äquator (40.075 km). Du verlängerst es um ${n(extra, 0)} m und hebst es überall gleich hoch an, bis es wieder straff ist. Wie hoch über dem Boden schwebt es?", "A rope lies tight once around the equator (40,075 km). You make it ${n(extra, 0)} m longer and lift it evenly all the way round until it is tight again. How high above the ground does it float?"),
                steps = listOf(num(l("Abstand in cm", "Gap in cm"), gap, "cm", rel = 0.02, done = """§h \approx ${m(gap, 1)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Der Umfang wächst um ${n(extra, 0)} m. Wie stark muss dafür der Radius wachsen? Die Größe der Erde spielt keine Rolle.", "Key idea: the circumference grows by ${n(extra, 0)} m. How much does the radius have to grow for that? The size of the Earth doesn't matter."), """§\Delta U = 2\pi\, \Delta r \Rightarrow \Delta r = \dfrac{${m(extra, 0)}\ \text{m}}{2\pi}§"""))),
                solution = listOf("""§\Delta r = \dfrac{${m(extra, 0)}}{2\pi}\ \text{m} \approx ${m(gap, 1)}\ \text{cm}§"""),
                background = l("Fast jeder schätzt hier einen winzigen Bruchteil eines Millimeters, weil ${n(extra, 0)} m gegen 40.000 km so wenig ist. Aber Umfang und Radius hängen linear zusammen, und die Zunahme hängt gar nicht vom ursprünglichen Radius ab. Ein Seil um einen Fußball, um ${n(extra, 0)} m verlängert, hebt sich genauso weit.", "Almost everyone guesses a tiny fraction of a millimetre here, because ${n(extra, 0)} m is so little compared with 40,000 km. But circumference and radius are linked linearly, and the increase doesn't depend on the original radius at all. A rope round a football, made ${n(extra, 0)} m longer, lifts by exactly the same amount."),
                fact = l("Bei 1 m Verlängerung könnte eine Katze unter dem Seil hindurchkriechen, rund um die ganze Erde.", "With 1 m extra, a cat could crawl under the rope, all the way round the Earth."),
            )
        },
        lab("ma.kugelball", Topic.Geometry, M, 0.4f) {
            val (what, r) = pick(l("Ein Basketball", "A basketball") to 12.0, l("Ein Fußball", "A football") to 11.0, l("Ein Tennisball", "A tennis ball") to 3.3)
            val v = 4.0 / 3.0 * PI * r * r * r
            task(
                question = l("$what hat etwa ${n(r, 1)} cm Radius. Wie viel Luft passt hinein?", "$what has a radius of about ${n(r, 1)} cm. How much air fits inside?"),
                steps = listOf(num(l("Volumen in cm³", "Volume in cm³"), v, "cm³", rel = 0.02, done = """§V \approx ${m(sig(v), 0)}\ \text{cm}^3§""",
                    hints = listOf(l("Kernidee: Kugelvolumen. Der Radius geht in der dritten Potenz ein.", "Key idea: the volume of a sphere. The radius appears to the third power."), """§V = \tfrac{4}{3}\pi r^3§"""))),
                solution = listOf("""§V = \tfrac{4}{3}\pi \cdot ${m(r, 1)}^3 \approx ${m(sig(v), 0)}\ \text{cm}^3§"""),
                background = l("Archimedes bewies, dass eine Kugel genau zwei Drittel des Zylinders füllt, in den sie gerade passt. Darauf war er so stolz, dass er sich Kugel und Zylinder auf seinen Grabstein wünschte. Weil das Volumen mit r³ wächst, hat ein doppelt so großer Ball achtmal so viel Luft.", "Archimedes proved that a sphere fills exactly two thirds of the cylinder it just fits into. He was so proud of this that he wanted a sphere and cylinder on his gravestone. Because the volume grows with r³, a ball twice as large holds eight times as much air."),
            )
        },
        lab("ma.getraenkedose", Topic.Geometry, M, 0.5f) {
            val d = pick(6.6, 5.8); val h = pick(11.5, 14.6, 9.2)
            val v = PI * (d / 2) * (d / 2) * h
            task(
                question = l("Eine Getränkedose hat ${n(d, 1)} cm Durchmesser und ist ${n(h, 1)} cm hoch. Wie viel Milliliter passen hinein, wenn man sie als Zylinder betrachtet?", "A drinks can has a diameter of ${n(d, 1)} cm and is ${n(h, 1)} cm tall. How many millilitres fit inside if you treat it as a cylinder?"),
                steps = listOf(num(l("Volumen in ml", "Volume in ml"), v, "ml", rel = 0.02, done = """§V \approx ${m(v, 0)}\ \text{ml}§""",
                    hints = listOf(l("Kernidee: Grundfläche mal Höhe. Der Radius ist der halbe Durchmesser. 1 cm³ = 1 ml.", "Key idea: base area times height. The radius is half the diameter. 1 cm³ = 1 ml."), """§V = \pi r^2 h§"""))),
                solution = listOf("""§V = \pi \cdot ${m(d / 2, 2)}^2 \cdot ${m(h, 1)}\ \text{cm}^3 \approx ${m(v, 0)}\ \text{ml}§"""),
                background = l("Das gerechnete Volumen ist meist etwas größer als der aufgedruckte Inhalt, zum Beispiel 393 ml für eine 330-ml-Dose. Der Rest ist Kopfraum für Kohlensäure, und die Dose ist oben und unten eingezogen. Ein Zylinder hat übrigens bei gleichem Volumen mehr Oberfläche als eine Kugel, ist aber viel leichter zu stapeln.", "The calculated volume is usually a bit more than the printed contents, for example 393 ml for a 330 ml can. The rest is headspace for the fizz, and the can narrows at the top and bottom. A cylinder, by the way, has more surface area than a sphere of the same volume, but it's much easier to stack."),
            )
        },
        lab("ma.eistuete", Topic.Geometry, M, 0.5f) {
            val r = pick(3.0, 2.5, 3.5); val h = pick(12.0, 10.0, 14.0)
            val v = PI * r * r * h / 3
            task(
                question = l("Eine Eiswaffel ist ein Kegel mit ${n(r, 1)} cm Radius oben und ${n(h, 0)} cm Höhe. Wie viel Eis passt hinein, wenn man es oben glatt abstreicht?", "An ice cream cone is a cone with a radius of ${n(r, 1)} cm at the top and a height of ${n(h, 0)} cm. How much ice cream fits in if it's levelled off at the top?"),
                steps = listOf(num(l("Volumen in cm³", "Volume in cm³"), v, "cm³", rel = 0.02, done = """§V \approx ${m(v, 0)}\ \text{cm}^3§""",
                    hints = listOf(l("Kernidee: Ein Kegel hat genau ein Drittel des Volumens eines Zylinders mit gleicher Grundfläche und Höhe.", "Key idea: a cone has exactly a third of the volume of a cylinder with the same base and height."), """§V = \tfrac{1}{3}\pi r^2 h§"""))),
                solution = listOf("""§V = \tfrac{1}{3}\pi \cdot ${m(r, 1)}^2 \cdot ${m(h, 0)}\ \text{cm}^3 \approx ${m(v, 0)}\ \text{cm}^3§"""),
                background = l("Kegel und Pyramiden haben immer ein Drittel des Volumens des passenden Zylinders oder Prismas. Man kann es mit Wasser ausprobieren: Drei volle Kegel füllen genau einen Zylinder. Die Ägypter kannten die Formel schon für ihre Pyramidenstümpfe. Bewiesen hat sie Eudoxos um 370 v. Chr.", "Cones and pyramids always have a third of the volume of the matching cylinder or prism. You can try it with water: three full cones exactly fill one cylinder. The Egyptians already knew the formula for their truncated pyramids. Eudoxus proved it around 370 BC."),
            )
        },
        lab("ma.modellmassstab", Topic.Geometry, M, 0.6f) {
            val k = pick(10, 20, 4)
            task(
                question = l("Ein Modellbauer baut ein Schiff im Maßstab 1 : ${k}. Für den Anstrich des Modells braucht er 50 ml Farbe. Wie viel Farbe braucht das echte Schiff, bei gleicher Schichtdicke?", "A model maker builds a ship at a scale of 1 : ${k}. To paint the model he needs 50 ml of paint. How much paint does the real ship need, with the same thickness of coat?"),
                steps = listOf(num(l("Farbe in Litern", "Paint in litres"), 50.0 * k * k / 1000, "l", rel = 0.01, done = """§${m(50.0 * k * k / 1000, 1)}\ \text{l}§""",
                    hints = listOf(l("Kernidee: Farbe hängt von der Fläche ab. Wenn alle Längen ${k}-mal größer sind, ist jede Fläche ${k}²-mal größer.", "Key idea: paint depends on area. If every length is ${k} times larger, every area is ${k}² times larger."), """§50\ \text{ml} \cdot ${k}^2§"""))),
                solution = listOf("""§50\ \text{ml} \cdot ${k * k} = ${m(50.0 * k * k, 0)}\ \text{ml} = ${m(50.0 * k * k / 1000, 1)}\ \text{l}§"""),
                background = l("Vergrößert man alles um den Faktor k, wachsen Flächen mit k² und Volumen mit k³. Das hat Folgen: Ein auf das Zehnfache vergrößertes Tier wäre 1000-mal schwerer, aber seine Knochen wären nur 100-mal so dick im Querschnitt. Deshalb haben Elefanten so dicke Beine und Riesen aus Märchen könnten nicht laufen.", "If you scale everything up by a factor k, areas grow with k² and volumes with k³. That has consequences: an animal scaled up ten times would be 1000 times heavier, but its bones would only be 100 times thicker in cross-section. That's why elephants have such thick legs and why the giants of fairy tales couldn't walk."),
            )
        },
        lab("ma.drachen", Topic.Geometry, M, 0.5f) {
            val len = pick(50.0, 80.0, 40.0); val ang = pick(35.0, 50.0, 60.0)
            val h = len * sin(Math.toRadians(ang))
            task(
                question = l("Die Schnur deines Drachens ist ${n(len, 0)} m lang und straff gespannt. Sie bildet mit dem Boden einen Winkel von ${n(ang, 0)}°. Wie hoch fliegt der Drache (deine Hand am Boden gerechnet)?", "Your kite string is ${n(len, 0)} m long and pulled tight. It makes an angle of ${n(ang, 0)}° with the ground. How high is the kite flying (counting your hand as at ground level)?"),
                steps = listOf(num(l("Höhe in Metern", "Height in metres"), h, "m", rel = 0.01, done = """§h \approx ${m(h, 1)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Die Schnur ist die längste Seite eines rechtwinkligen Dreiecks, die Höhe liegt dem Winkel gegenüber.", "Key idea: the string is the longest side of a right triangle, and the height is opposite the angle."), """§\sin\alpha = \dfrac{h}{L}§"""))),
                solution = listOf("""§h = ${m(len, 0)} \cdot \sin ${m(ang, 0)}^\circ \approx ${m(h, 1)}\ \text{m}§"""),
                background = l("Sinus, Kosinus und Tangens verbinden Winkel mit Seitenverhältnissen im rechtwinkligen Dreieck: Gegenkathete durch Hypotenuse ist der Sinus. So misst man Höhen, an die man nicht herankommt. Echte Drachenschnüre hängen allerdings etwas durch, der Drache ist also etwas tiefer als gerechnet.", "Sine, cosine and tangent link angles to ratios of sides in a right triangle: the opposite side over the hypotenuse is the sine. That's how you measure heights you can't reach. Real kite strings sag a little, though, so the kite is a bit lower than calculated."),
            )
        },
        lab("ma.flussbreite", Topic.Geometry, M, 0.5f) {
            val base = pick(40.0, 30.0, 50.0); val ang = pick(60.0, 55.0, 70.0)
            val w = base * tan(Math.toRadians(ang))
            task(
                question = l("Du willst die Breite eines Flusses messen, ohne ihn zu überqueren. Gegenüber steht ein Baum genau senkrecht zum Ufer vor dir. Du gehst ${n(base, 0)} m am Ufer entlang und peilst den Baum an: Die Sichtlinie bildet mit dem Ufer einen Winkel von ${n(ang, 0)}°. Wie breit ist der Fluss?", "You want to measure the width of a river without crossing it. A tree on the far bank stands directly opposite you. You walk ${n(base, 0)} m along the bank and sight the tree: the line of sight makes an angle of ${n(ang, 0)}° with the bank. How wide is the river?"),
                steps = listOf(num(l("Breite in Metern", "Width in metres"), w, "m", rel = 0.01, done = """§b \approx ${m(w, 1)}\ \text{m}§""",
                    hints = listOf(l("Kernidee: Dein Weg am Ufer und die Flussbreite stehen senkrecht. Der Tangens verbindet den Winkel mit diesen beiden Seiten.", "Key idea: your path along the bank and the width of the river are perpendicular. The tangent links the angle to these two sides."), """§\tan\alpha = \dfrac{b}{s}§"""))),
                solution = listOf("""§b = ${m(base, 0)} \cdot \tan ${m(ang, 0)}^\circ \approx ${m(w, 1)}\ \text{m}§"""),
                background = l("Mit Winkelmessungen kann man Entfernungen bestimmen, ohne sie abzuschreiten. Landvermesser überzogen so im 19. Jahrhundert ganze Länder mit einem Netz aus Dreiecken. Mit derselben Methode, nur mit der Erdbahn als Grundlinie, misst man die Entfernung naher Sterne.", "With angle measurements you can work out distances without pacing them out. In the 19th century surveyors covered whole countries with a network of triangles this way. With the same method, only with the Earth's orbit as the baseline, the distances to nearby stars are measured."),
            )
        },
        lab("ma.rasensprenger", Topic.Geometry, M, 0.5f) {
            val ang = pick(120.0, 90.0, 180.0); val r = pick(6.0, 5.0, 8.0)
            val a = PI * r * r * ang / 360
            task(
                question = l("Ein Rasensprenger schwenkt über einen Winkel von ${n(ang, 0)}° und spritzt ${n(r, 0)} m weit. Wie groß ist die Fläche, die er bewässert?", "A lawn sprinkler sweeps through an angle of ${n(ang, 0)}° and sprays ${n(r, 0)} m. How large is the area it waters?"),
                steps = listOf(num(l("Fläche in m²", "Area in m²"), a, "m²", rel = 0.01, done = """§A \approx ${m(a, 1)}\ \text{m}^2§""",
                    hints = listOf(l("Kernidee: Ein Kreissektor ist der Anteil Winkel/360° vom ganzen Kreis.", "Key idea: a circular sector is the fraction angle/360° of the whole circle."), """§A = \dfrac{\alpha}{360^\circ} \cdot \pi r^2§"""))),
                solution = listOf("""§A = \dfrac{${m(ang, 0)}}{360} \cdot \pi \cdot ${m(r, 0)}^2 \approx ${m(a, 1)}\ \text{m}^2§"""),
                background = l("Ein Kreissektor ist ein Tortenstück: Sein Anteil am Kreis ist derselbe wie der Anteil seines Winkels an 360°. Dasselbe gilt für die Bogenlänge. Weil die Fläche mit r² wächst, bewässert ein Sprenger mit doppelter Reichweite viermal so viel Rasen.", "A circular sector is a slice of cake: its share of the circle is the same as its angle's share of 360°. The same holds for the arc length. Because the area grows with r², a sprinkler with twice the range waters four times as much lawn."),
            )
        },
        lab("ma.gleichseitig", Topic.Geometry, M, 0.4f) {
            val a = pick(10.0, 6.0, 8.0); val h = a * sqrt(3.0) / 2
            task(
                question = l("Ein gleichseitiges Dreieck hat ${n(a, 0)} cm Seitenlänge. Wie hoch ist es?", "An equilateral triangle has sides of ${n(a, 0)} cm. How tall is it?"),
                steps = listOf(num(l("Höhe in cm", "Height in cm"), h, "cm", rel = 0.01, done = """§h \approx ${m(h, 2)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Die Höhe teilt das Dreieck in zwei rechtwinklige Hälften mit der Grundseite a/2.", "Key idea: the height splits the triangle into two right-angled halves with base a/2."), """§h^2 + \left(\dfrac{a}{2}\right)^2 = a^2 \Rightarrow h = \dfrac{\sqrt{3}}{2} a§"""))),
                solution = listOf("""§h = \dfrac{\sqrt{3}}{2} \cdot ${m(a, 0)} \approx ${m(h, 2)}\ \text{cm}§"""),
                background = l("Im gleichseitigen Dreieck sind alle Winkel 60°. Die Höhe halbiert es in zwei rechtwinklige Dreiecke mit 30°, 60° und 90°, deren Seiten im Verhältnis 1 : √3 : 2 stehen. Daraus folgen die Werte sin 60° = √3/2 und cos 60° = 1/2, die man so nicht auswendig lernen muss.", "In an equilateral triangle all the angles are 60°. The height halves it into two right triangles with 30°, 60° and 90°, whose sides are in the ratio 1 : √3 : 2. From this follow the values sin 60° = √3/2 and cos 60° = 1/2, which you then don't need to memorise."),
            )
        },
        lab("ma.sechseck", Topic.Geometry, M, 0.6f) {
            val s = pick(1.0, 2.0, 3.0); val a = 3 * sqrt(3.0) / 2 * s * s
            task(
                question = l("Eine Bienenwabe ist ein regelmäßiges Sechseck mit ${n(s, 0)} cm Seitenlänge (im Modell vergrößert). Wie groß ist ihre Fläche?", "A honeycomb cell is a regular hexagon with sides of ${n(s, 0)} cm (enlarged in this model). What is its area?"),
                steps = listOf(num(l("Fläche in cm²", "Area in cm²"), a, "cm²", rel = 0.01, done = """§A \approx ${m(a, 2)}\ \text{cm}^2§""",
                    hints = listOf(l("Kernidee: Ein regelmäßiges Sechseck besteht aus sechs gleichseitigen Dreiecken.", "Key idea: a regular hexagon is made of six equilateral triangles."), """§A = 6 \cdot \dfrac{\sqrt{3}}{4} s^2§"""))),
                solution = listOf("""§A = 6 \cdot \dfrac{\sqrt{3}}{4} \cdot ${m(s, 0)}^2 \approx ${m(a, 2)}\ \text{cm}^2§"""),
                background = l("Nur drei regelmäßige Vielecke füllen eine Ebene lückenlos: Dreiecke, Quadrate und Sechsecke. Von ihnen umschließt das Sechseck bei gleichem Umfang die meiste Fläche. Bienen sparen so Wachs. Dass das wirklich die beste Aufteilung ist, bewies Thomas Hales erst 1999.", "Only three regular polygons fill a plane without gaps: triangles, squares and hexagons. Of these, the hexagon encloses the most area for the same perimeter. That's how bees save wax. That this really is the best way to divide a plane was only proved by Thomas Hales in 1999."),
            )
        },
        lab("ma.eulerpolyeder", Topic.Geometry, M, 0.6f) {
            val (name, v, f) = pick(Triple(l("Ein Ikosaeder", "An icosahedron"), 12, 20), Triple(l("Ein Dodekaeder", "A dodecahedron"), 20, 12), Triple(l("Ein Fußball (abgestumpftes Ikosaeder)", "A football (truncated icosahedron)"), 60, 32))
            val e = v + f - 2
            task(
                question = l("$name hat ${v} Ecken und ${f} Flächen. Wie viele Kanten hat der Körper?", "$name has ${v} vertices and ${f} faces. How many edges does it have?"),
                steps = listOf(num(l("Kanten", "Edges"), e.toDouble(), "", abs = 0.0, done = """§K = ${e}§""",
                    hints = listOf(l("Kernidee: Für jeden Körper ohne Löcher gilt der Eulersche Polyedersatz.", "Key idea: Euler's polyhedron formula holds for every solid without holes."), """§E - K + F = 2§"""))),
                solution = listOf("""§K = E + F - 2 = ${v} + ${f} - 2 = ${e}§"""),
                background = l("Leonhard Euler bemerkte 1750: Für jeden Körper ohne Löcher ist Ecken minus Kanten plus Flächen immer 2. Würfel: 8 − 12 + 6 = 2. Die Zahl hängt nicht von Größe oder Form ab, nur davon, wie der Körper zusammenhängt. Daraus entstand die Topologie, die Geometrie des Verformbaren.", "In 1750 Leonhard Euler noticed: for every solid without holes, vertices minus edges plus faces is always 2. Cube: 8 − 12 + 6 = 2. The number doesn't depend on size or shape, only on how the solid hangs together. That gave rise to topology, the geometry of things that can be deformed."),
                fact = l("Ein Körper mit einem Loch, etwa ein Ring, ergibt immer 0 statt 2.", "A solid with one hole, such as a ring, always gives 0 instead of 2."),
            )
        },
        lab("ma.steigungprozent", Topic.Geometry, M, 0.5f) {
            val pct = pick(12.0, 8.0, 20.0, 100.0); val ang = Math.toDegrees(kotlin.math.atan(pct / 100))
            task(
                question = l("Ein Verkehrsschild warnt vor ${n(pct, 0)} % Steigung. Welchem Winkel gegen die Waagrechte entspricht das?", "A road sign warns of a ${n(pct, 0)}% gradient. What angle to the horizontal does that correspond to?"),
                steps = listOf(num(l("Winkel in Grad", "Angle in degrees"), ang, "°", abs = 0.1, done = """§\alpha \approx ${m(ang, 1)}^\circ§""",
                    hints = listOf(l("Kernidee: ${n(pct, 0)} % heißt ${n(pct, 0)} m Höhe auf 100 m waagrechte Strecke. Das Verhältnis ist der Tangens des Winkels.", "Key idea: ${n(pct, 0)}% means ${n(pct, 0)} m of height over 100 m of horizontal distance. The ratio is the tangent of the angle."), """§\tan\alpha = \dfrac{${m(pct, 0)}}{100}§"""))),
                solution = listOf("""§\alpha = \arctan ${m(pct / 100, 2)} \approx ${m(ang, 1)}^\circ§"""),
                background = l("Steigungen in Prozent klingen dramatischer als in Grad, aber 100 % sind nicht senkrecht, sondern 45°: ein Meter hoch auf einem Meter Strecke. Die steilsten Straßen der Welt haben etwa 35 %. Straßenbahnen schaffen höchstens rund 10 %, Zahnradbahnen deutlich mehr.", "Gradients in percent sound more dramatic than in degrees, but 100% isn't vertical, it's 45°: one metre up over one metre across. The steepest streets in the world are about 35%. Trams manage at most around 10%, rack railways a lot more."),
            )
        },
        lab("ma.goldenerschnitt", Topic.Geometry, M, 0.6f) {
            val a = pick(10.0, 20.0, 34.0); val phi = (1 + sqrt(5.0)) / 2; val b = a / phi
            task(
                question = l("""Eine Strecke von ${n(a, 0)} cm soll im Goldenen Schnitt geteilt werden: Das Ganze verhält sich zum längeren Teil wie der längere zum kürzeren. Das Verhältnis ist §\varphi = \tfrac{1 + \sqrt{5}}{2} \approx 1{,}618§. Wie lang ist der längere Teil?""", """A line of ${n(a, 0)} cm is to be divided in the golden ratio: the whole is to the longer part as the longer is to the shorter. The ratio is §\varphi = \tfrac{1 + \sqrt{5}}{2} \approx 1{,}618§. How long is the longer part?"""),
                steps = listOf(num(l("Längerer Teil in cm", "Longer part in cm"), b, "cm", rel = 0.01, done = """§\approx ${m(b, 2)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Das Ganze ist φ-mal so lang wie der längere Teil.", "Key idea: the whole is φ times as long as the longer part."), """§b = \dfrac{a}{\varphi}§"""))),
                solution = listOf("""§b = \dfrac{${m(a, 0)}}{1{,}618} \approx ${m(b, 2)}\ \text{cm}§""", l("Der kürzere Teil ist dann ${n(a - b, 2)} cm.", "The shorter part is then ${n(a - b, 2)} cm.")),
                background = l("Der Goldene Schnitt hat eine einzigartige Eigenschaft: Schneidet man von einem goldenen Rechteck ein Quadrat ab, bleibt wieder ein goldenes Rechteck übrig. Das Verhältnis aufeinanderfolgender Fibonacci-Zahlen nähert sich φ. Viele Behauptungen über den Goldenen Schnitt in Kunst und Natur sind allerdings übertrieben.", "The golden ratio has a unique property: if you cut a square off a golden rectangle, another golden rectangle is left. The ratio of consecutive Fibonacci numbers approaches φ. Many claims about the golden ratio in art and nature are exaggerated, though."),
            )
        },
        lab("ma.heron", Topic.Geometry, H, 0.6f) {
            val (a, b, c) = pick(Triple(13, 14, 15), Triple(5, 5, 6), Triple(7, 8, 9), Triple(9, 10, 17))
            val s = (a + b + c) / 2.0; val area = sqrt(s * (s - a) * (s - b) * (s - c))
            task(
                question = l("Ein dreieckiges Grundstück hat die Seiten ${a} m, ${b} m und ${c} m. Wie groß ist seine Fläche?", "A triangular plot of land has sides of ${a} m, ${b} m and ${c} m. What is its area?"),
                steps = listOf(num(l("Fläche in m²", "Area in m²"), area, "m²", rel = 0.01, done = """§A \approx ${m(area, 2)}\ \text{m}^2§""",
                    hints = listOf(l("Kernidee: Mit drei Seiten und ohne Höhe hilft die Formel des Heron. s ist der halbe Umfang.", "Key idea: with three sides and no height, Heron's formula helps. s is half the perimeter."), """§A = \sqrt{s(s - a)(s - b)(s - c)},\quad s = \dfrac{a + b + c}{2}§"""))),
                solution = listOf("""§s = ${m(s, 1)},\quad A = \sqrt{${m(s, 1)} \cdot ${m(s - a, 1)} \cdot ${m(s - b, 1)} \cdot ${m(s - c, 1)}} \approx ${m(area, 2)}\ \text{m}^2§"""),
                background = l("Heron von Alexandria schrieb diese Formel im 1. Jahrhundert auf. Sie ist praktisch, weil man im Gelände Seiten leicht abmessen kann, Höhen aber nicht. Landvermesser teilen deshalb krumme Grundstücke in Dreiecke und berechnen jedes mit Heron. Das Dreieck 13-14-15 ist berühmt, weil Seiten, Höhe und Fläche alle ganzzahlig sind.", "Heron of Alexandria wrote this formula down in the 1st century. It's practical because in the field you can easily measure sides but not heights. That's why surveyors divide irregular plots into triangles and calculate each with Heron's formula. The 13-14-15 triangle is famous because its sides, height and area are all whole numbers."),
            )
        },
        lab("ma.sinussatz", Topic.Geometry, H, 0.6f) {
            val a = pick(10.0, 8.0, 12.0); val al = pick(30.0, 40.0); val be = pick(45.0, 60.0, 70.0)
            val b = a * sin(Math.toRadians(be)) / sin(Math.toRadians(al))
            task(
                question = l("In einem Dreieck ist die Seite a = ${n(a, 0)} cm lang. Ihr gegenüber liegt der Winkel α = ${n(al, 0)}°, und β = ${n(be, 0)}°. Wie lang ist die Seite b gegenüber von β?", "In a triangle, side a = ${n(a, 0)} cm. Opposite it is the angle α = ${n(al, 0)}°, and β = ${n(be, 0)}°. How long is side b, opposite β?"),
                steps = listOf(num(l("Seite b in cm", "Side b in cm"), b, "cm", rel = 0.01, done = """§b \approx ${m(b, 2)}\ \text{cm}§""",
                    hints = listOf(l("Kernidee: Sinussatz: Das Verhältnis Seite zu Sinus des Gegenwinkels ist für alle Seiten gleich.", "Key idea: the sine rule: the ratio of a side to the sine of the opposite angle is the same for all sides."), """§\dfrac{a}{\sin\alpha} = \dfrac{b}{\sin\beta}§"""))),
                solution = listOf("""§b = ${m(a, 0)} \cdot \dfrac{\sin ${m(be, 0)}^\circ}{\sin ${m(al, 0)}^\circ} \approx ${m(b, 2)}\ \text{cm}§"""),
                background = l("Der Sinussatz gilt in jedem Dreieck, nicht nur in rechtwinkligen. Größere Winkel liegen größeren Seiten gegenüber, und der Sinus macht daraus ein genaues Verhältnis. Das gemeinsame Verhältnis ist übrigens genau der Durchmesser des Umkreises. Navigatoren berechneten damit früher Positionen auf See.", "The sine rule holds in every triangle, not only right-angled ones. Larger angles lie opposite larger sides, and the sine turns this into an exact ratio. The common ratio, by the way, is exactly the diameter of the circumscribed circle. Navigators used it to work out positions at sea."),
            )
        },
        lab("ma.kosinussatz", Topic.Geometry, H, 0.6f) {
            val a = pick(7.0, 5.0, 9.0); val b = pick(8.0, 6.0, 10.0); val g = pick(60.0, 120.0, 45.0)
            val c = sqrt(a * a + b * b - 2 * a * b * cos(Math.toRadians(g)))
            task(
                question = l("Zwei Wanderwege gehen vom selben Punkt aus, unter einem Winkel von ${n(g, 0)}°. Anna geht ${n(a, 0)} km auf dem einen, Ben ${n(b, 0)} km auf dem anderen. Wie weit sind sie jetzt voneinander entfernt (Luftlinie)?", "Two hiking trails start from the same point at an angle of ${n(g, 0)}°. Anna walks ${n(a, 0)} km along one, Ben ${n(b, 0)} km along the other. How far apart are they now (as the crow flies)?"),
                steps = listOf(num(l("Abstand in km", "Distance in km"), c, "km", rel = 0.01, done = """§c \approx ${m(c, 2)}\ \text{km}§""",
                    hints = listOf(l("Kernidee: Kosinussatz, der Pythagoras für beliebige Dreiecke.", "Key idea: the cosine rule, Pythagoras for any triangle."), """§c^2 = a^2 + b^2 - 2ab \cos\gamma§"""))),
                solution = listOf("""§c = \sqrt{${m(a, 0)}^2 + ${m(b, 0)}^2 - 2 \cdot ${m(a, 0)} \cdot ${m(b, 0)} \cdot \cos ${m(g, 0)}^\circ} \approx ${m(c, 2)}\ \text{km}§"""),
                background = l("Bei 90° ist der Kosinus null, und der Kosinussatz wird zum Satz des Pythagoras. Bei spitzen Winkeln wird die dritte Seite kürzer, bei stumpfen länger. Der Korrekturterm −2ab·cos γ misst also, wie sehr der Winkel vom rechten Winkel abweicht.", "At 90° the cosine is zero, and the cosine rule becomes Pythagoras' theorem. At acute angles the third side gets shorter, at obtuse ones longer. So the correction term −2ab·cos γ measures how far the angle deviates from a right angle."),
            )
        },
        lab("ma.kugelwuerfel", Topic.Geometry, H, 0.6f) {
            val frac = PI / 6 * 100
            task(
                question = l("Eine Kugel liegt genau passend in einem würfelförmigen Karton, sie berührt alle sechs Wände. Wie viel Prozent des Kartons füllt sie?", "A ball fits exactly into a cube-shaped box, touching all six walls. What percentage of the box does it fill?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), frac, "%", abs = 0.1, done = """§\dfrac{\pi}{6} \approx ${m(frac, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Die Kantenlänge des Würfels ist der Durchmesser der Kugel. Nenne den Radius r, dann ist die Kante 2r.", "Key idea: the cube's edge is the ball's diameter. Call the radius r; then the edge is 2r."), """§\dfrac{\tfrac{4}{3}\pi r^3}{(2r)^3}§"""))),
                solution = listOf("""§\dfrac{\tfrac{4}{3}\pi r^3}{8 r^3} = \dfrac{\pi}{6} \approx ${m(frac / 100, 4)}§"""),
                background = l("Der Radius kürzt sich heraus, das Ergebnis gilt für jede Größe. Fast die Hälfte des Kartons bleibt Luft. Deshalb verpackt man runde Dinge so ungern. Selbst die dichteste Packung vieler Kugeln, wie Orangen am Obststand, füllt nur etwa 74 % des Raums. Das vermutete Kepler schon 1611, bewiesen wurde es erst 1998.", "The radius cancels, so the result holds for any size. Almost half the box stays empty. That's why people dislike packing round things. Even the densest packing of many spheres, like oranges on a market stall, only fills about 74% of space. Kepler conjectured this in 1611, but it was only proved in 1998."),
            )
        },
        lab("ma.archimedespi", Topic.Geometry, H, 0.7f) {
            val nn = pick(6, 12, 24, 96); val est = nn * sin(PI / nn)
            task(
                question = l("Archimedes näherte π an, indem er einem Kreis mit Radius 1 regelmäßige Vielecke einschrieb. Ein ${nn}-Eck hat den Umfang ${2 * nn} · sin(180°/${nn}). Welche Näherung für π liefert der halbe Umfang?", "Archimedes approximated π by drawing regular polygons inside a circle of radius 1. A ${nn}-gon has the perimeter ${2 * nn} · sin(180°/${nn}). What approximation for π does half the perimeter give?"),
                steps = listOf(num(l("Näherung für π", "Approximation for π"), est, "", abs = 0.0005, done = """§\pi \approx ${m(est, 4)}§""",
                    hints = listOf(l("Kernidee: Der halbe Umfang eines Kreises mit Radius 1 ist genau π. Das Vieleck ist etwas kürzer.", "Key idea: half the circumference of a circle with radius 1 is exactly π. The polygon is slightly shorter."), """§${nn} \cdot \sin\dfrac{180^\circ}{${nn}}§"""))),
                solution = listOf("""§${nn} \cdot \sin ${m(180.0 / nn, 3)}^\circ \approx ${m(est, 4)}§"""),
                background = l("Archimedes kam um 250 v. Chr. mit einem 96-Eck innen und außen auf 3,1408 < π < 3,1429, ohne Taschenrechner, nur mit Wurzeln, die er von Hand zog. Jede Verdopplung der Eckenzahl macht den Fehler etwa viermal kleiner. Heute kennt man Billionen Stellen von π, aber für fast alle Berechnungen genügen weniger als 20.", "Around 250 BC Archimedes, using a 96-gon inside and outside, got 3.1408 < π < 3.1429, without a calculator, only with square roots worked out by hand. Each doubling of the number of sides makes the error about four times smaller. Today trillions of digits of π are known, but for almost all calculations fewer than 20 are enough."),
            )
        },
    )

    private val probability = listOf(
        lab("ma.wuerfelmittel", Topic.Probability, E, 0.4f) {
            task(
                question = l("Du würfelst sehr oft mit einem normalen Würfel. Welche Augenzahl kommt im Durchschnitt heraus?", "You roll an ordinary die many, many times. What is the average number shown?"),
                steps = listOf(num(l("Durchschnitt", "Average"), 3.5, "", abs = 0.0, done = """§3{,}5§""",
                    hints = listOf(l("Kernidee: Jede Zahl von 1 bis 6 kommt gleich oft. Der Durchschnitt ist ihr Mittelwert.", "Key idea: every number from 1 to 6 comes up equally often. The average is their mean."), """§\dfrac{1 + 2 + 3 + 4 + 5 + 6}{6}§"""))),
                solution = listOf("""§\dfrac{21}{6} = 3{,}5§"""),
                background = l("Der Erwartungswert muss gar nicht selbst vorkommen können: Niemand würfelt je eine 3,5. Er beschreibt, wohin sich der Durchschnitt vieler Würfe bewegt. Bei 100 Würfen kommt man meist auf eine Summe nahe 350, und je mehr Würfe, desto genauer trifft der Durchschnitt die 3,5.", "The expected value doesn't have to be possible itself: nobody ever rolls a 3.5. It describes where the average of many rolls heads. With 100 rolls you usually get a total close to 350, and the more rolls, the closer the average comes to 3.5."),
            )
        },
        lab("ma.spielerfehler", Topic.Probability, E, 0.5f) {
            val k = pick(5, 7, 10)
            task(
                question = l("Eine faire Münze ist ${k}-mal hintereinander auf Kopf gefallen. Wie wahrscheinlich ist es, dass beim nächsten Wurf Zahl kommt?", "A fair coin has landed heads ${k} times in a row. How likely is it that the next toss is tails?"),
                steps = listOf(pick1(l("Wahrscheinlichkeit für Zahl", "Probability of tails"), "50 %", listOf(l("Mehr als 50 %, Zahl ist überfällig", "More than 50%, tails is overdue"), l("Weniger als 50 %, die Münze hat eine Serie", "Less than 50%, the coin is on a streak"), l("Fast 100 %", "Almost 100%")),
                    hints = listOf(l("Kernidee: Hat die Münze ein Gedächtnis?", "Key idea: does the coin have a memory?"), l("Jeder Wurf ist unabhängig von allen vorigen.", "Every toss is independent of all the previous ones.")))),
                solution = listOf(l("Genau 50 %. Die Münze weiß nichts von den vorigen Würfen. Eine Serie von ${k} war unwahrscheinlich, aber sie ist schon passiert und ändert die Zukunft nicht.", "Exactly 50%. The coin knows nothing of the previous tosses. A streak of ${k} was unlikely, but it has already happened and doesn't change the future.")),
                background = l("Der Spielerfehlschluss ist einer der hartnäckigsten Denkfehler. 1913 kam im Casino von Monte Carlo 26-mal hintereinander Schwarz, und Spieler verloren Millionen, weil sie immer höher auf Rot setzten. Das Gesetz der großen Zahlen gleicht Serien nicht aus, sie werden nur in der großen Menge der Würfe verdünnt.", "The gambler's fallacy is one of the most stubborn errors of thinking. In 1913 black came up 26 times in a row at the Monte Carlo casino, and players lost millions because they kept betting more and more on red. The law of large numbers doesn't balance out streaks; they just get diluted in the huge number of tosses."),
            )
        },
        lab("ma.pfadregel", Topic.Probability, E, 0.5f) {
            val r = pick(3, 4, 2); val b = pick(2, 3); val tot = r + b
            val p = r.toDouble() / tot * b / tot * 100
            task(
                question = l("In einer Dose liegen ${r} rote und ${b} blaue Kugeln. Du ziehst eine, legst sie zurück und ziehst noch einmal. Wie wahrscheinlich ist erst Rot, dann Blau?", "A tin holds ${r} red and ${b} blue balls. You draw one, put it back and draw again. How likely is red first, then blue?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", abs = 0.1, done = """§${m(p, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Pfadregel: Entlang eines Pfades im Baumdiagramm werden die Wahrscheinlichkeiten multipliziert.", "Key idea: the path rule: along a path in a tree diagram the probabilities are multiplied."), """§P = \dfrac{${r}}{${tot}} \cdot \dfrac{${b}}{${tot}}§"""))),
                solution = listOf("""§P = \dfrac{${r}}{${tot}} \cdot \dfrac{${b}}{${tot}} = \dfrac{${r * b}}{${tot * tot}} = ${m(p, 1)}\ \%§"""),
                background = l("Ein Baumdiagramm zerlegt einen Zufallsversuch in Stufen. Für einen bestimmten Ablauf multipliziert man entlang des Weges, für mehrere mögliche Abläufe addiert man die Wege. Durch das Zurücklegen bleibt jede Stufe gleich, die Züge sind unabhängig. Ohne Zurücklegen würde sich die zweite Stufe ändern.", "A tree diagram breaks a random experiment into stages. For one particular outcome you multiply along the path; for several possible outcomes you add up the paths. Putting the ball back keeps every stage the same, so the draws are independent. Without putting it back, the second stage would change."),
            )
        },
        lab("ma.dreigleich", Topic.Probability, E, 0.6f) {
            val dice = pick(3, 2, 4); val p = 6.0 / 6.0.pow(dice) * 100
            task(
                question = l("Du wirfst ${dice} Würfel gleichzeitig. Wie wahrscheinlich zeigen alle dieselbe Zahl?", "You throw ${dice} dice at once. How likely is it that they all show the same number?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", rel = 0.02, done = """§${m(p, 2)}\ \%§""",
                    hints = listOf(l("Kernidee: Der erste Würfel darf zeigen, was er will. Die anderen müssen ihm folgen, jeder mit Wahrscheinlichkeit 1/6.", "Key idea: the first die can show whatever it likes. The others must match it, each with probability 1/6."), """§P = \left(\dfrac{1}{6}\right)^{${dice - 1}}§"""))),
                solution = listOf("""§P = \left(\dfrac{1}{6}\right)^{${dice - 1}} = \dfrac{1}{${m(6.0.pow(dice - 1), 0)}} \approx ${m(p, 2)}\ \%§"""),
                background = l("Ein guter Trick bei Wahrscheinlichkeiten: Frag nicht, ob alle eine bestimmte Zahl zeigen, sondern ob sie zum ersten passen. Das spart den Faktor 6 für die freie Wahl der Zahl. Alternativ zählt man: Es gibt 6 günstige von ${n(6.0.pow(dice), 0)} möglichen Ergebnissen, das ergibt dasselbe.", "A good trick with probabilities: don't ask whether they all show one particular number but whether they match the first one. That saves the factor of 6 for the free choice of number. Alternatively, count: there are 6 favourable out of ${n(6.0.pow(dice), 0)} possible outcomes, which gives the same."),
            )
        },
        lab("ma.schnickschnack", Topic.Probability, E, 0.4f) {
            task(
                question = l("Zwei spielen Schere, Stein, Papier und wählen ganz zufällig. Wie wahrscheinlich ist ein Unentschieden?", "Two people play rock, paper, scissors and choose completely at random. How likely is a draw?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), 100.0 / 3, "%", abs = 0.5, done = """§\tfrac{1}{3} \approx 33{,}3\ \%§""",
                    hints = listOf(l("Kernidee: Egal, was der erste nimmt: Der zweite muss genau dasselbe wählen.", "Key idea: whatever the first player picks, the second must choose exactly the same."), """§\dfrac{3\ \text{günstige}}{9\ \text{mögliche}}§""".let { l(it, it.replace("günstige", "favourable").replace("mögliche", "possible")) }))),
                solution = listOf("""§P = \dfrac{3}{9} = \dfrac{1}{3}§"""),
                background = l("Bei reinem Zufall gewinnt jeder ein Drittel, ein Drittel endet unentschieden. Menschen spielen aber nicht zufällig: Nach einer Niederlage wechseln viele, nach einem Sieg bleiben sie gern dabei. Programme, die solche Muster erkennen, schlagen Menschen auf Dauer deutlich.", "With pure chance each player wins a third of the time, and a third end in a draw. But people don't play randomly: after a loss many switch, after a win they like to stick. Programs that recognise such patterns clearly beat humans in the long run."),
            )
        },
        lab("ma.podium", Topic.Probability, E, 0.5f) {
            val nn = pick(8, 10, 6); val v = nn * (nn - 1) * (nn - 2)
            task(
                question = l("Bei einem Rennen mit ${nn} Läuferinnen werden Gold, Silber und Bronze vergeben. Wie viele verschiedene Siegerpodeste sind möglich?", "In a race with ${nn} runners, gold, silver and bronze are awarded. How many different podiums are possible?"),
                steps = listOf(num(l("Anzahl", "Number"), v.toDouble(), "", abs = 0.0, done = """§${v}§""",
                    hints = listOf(l("Kernidee: Für Gold gibt es ${nn} Möglichkeiten, für Silber dann noch ${nn - 1}, für Bronze ${nn - 2}.", "Key idea: there are ${nn} options for gold, then ${nn - 1} left for silver, ${nn - 2} for bronze."), """§${nn} \cdot ${nn - 1} \cdot ${nn - 2}§"""))),
                solution = listOf("""§${nn} \cdot ${nn - 1} \cdot ${nn - 2} = ${v}§"""),
                background = l("Wenn die Reihenfolge zählt und niemand doppelt vorkommen darf, multipliziert man die schrumpfenden Möglichkeiten. Das ist die Zahl der Variationen. Wäre die Reihenfolge egal, also nur die Frage, wer aufs Podest kommt, müsste man durch die 6 möglichen Reihenfolgen der drei teilen.", "When order matters and nobody can appear twice, you multiply the shrinking numbers of options. That's the number of arrangements. If order didn't matter, i.e. only the question of who gets onto the podium, you'd have to divide by the 6 possible orders of the three."),
            )
        },
        lab("ma.demere", Topic.Probability, M, 0.6f) {
            val k = pick(4, 3, 6); val p = (1 - (5.0 / 6).pow(k)) * 100
            task(
                question = l("Du würfelst ${k}-mal. Wie wahrscheinlich ist mindestens eine Sechs dabei?", "You roll a die ${k} times. How likely is it that there's at least one six?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", abs = 0.2, done = """§\approx ${m(p, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: „Mindestens eine“ rechnet man am besten über das Gegenteil: keine einzige Sechs.", "Key idea: 'at least one' is best calculated via the opposite: not a single six."), """§P = 1 - \left(\dfrac{5}{6}\right)^{${k}}§"""))),
                solution = listOf("""§P = 1 - \left(\dfrac{5}{6}\right)^{${k}} \approx 1 - ${m((5.0 / 6).pow(k), 3)} = ${m(p, 1)}\ \%§"""),
                background = l("Der Chevalier de Méré wettete im 17. Jahrhundert erfolgreich, dass in 4 Würfen eine Sechs fällt. Als er auch darauf wettete, dass in 24 Würfen mit zwei Würfeln ein Sechserpasch fällt, verlor er. Er bat Blaise Pascal um Hilfe, und aus dessen Briefwechsel mit Pierre de Fermat entstand die Wahrscheinlichkeitsrechnung.", "In the 17th century the Chevalier de Méré successfully bet that a six would come up in 4 rolls. When he also bet that a double six would come up in 24 rolls of two dice, he lost. He asked Blaise Pascal for help, and probability theory grew out of Pascal's correspondence with Pierre de Fermat."),
            )
        },
        lab("ma.lotto", Topic.Probability, M, 0.5f) {
            val (nn, k) = pick(49 to 6, 45 to 6, 50 to 5)
            val v = binom4(nn, k)
            task(
                question = l("Beim Lotto werden ${k} aus ${nn} Zahlen gezogen, die Reihenfolge ist egal. Wie viele verschiedene Tipps gibt es?", "In a lottery ${k} out of ${nn} numbers are drawn, and the order doesn't matter. How many different tickets are possible?"),
                steps = listOf(num(l("Anzahl Möglichkeiten", "Number of possibilities"), v, "", abs = 0.0, done = """§\binom{${nn}}{${k}} = ${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: Erst so tun, als zählte die Reihenfolge (${nn} · ${nn - 1} · …), dann durch die ${n(fact4(k), 0)} möglichen Reihenfolgen der ${k} Zahlen teilen.", "Key idea: first pretend the order matters (${nn} · ${nn - 1} · …), then divide by the ${n(fact4(k), 0)} possible orders of the ${k} numbers."), """§\binom{n}{k} = \dfrac{n!}{k!\,(n - k)!}§"""))),
                solution = listOf("""§\binom{${nn}}{${k}} = ${m(v, 0)}§"""),
                background = l("Der Binomialkoeffizient zählt, wie viele Teilmengen einer bestimmten Größe es gibt. Beim deutschen Lotto 6 aus 49 sind es fast 14 Millionen. Würde man jeden Samstag einen Tipp abgeben, käme der Hauptgewinn im Mittel alle 270.000 Jahre, ohne Superzahl.", "The binomial coefficient counts how many subsets of a given size there are. For the German lottery, 6 out of 49, there are almost 14 million. If you entered one ticket every Saturday, you'd win the jackpot on average once every 270,000 years, not even counting the bonus number."),
            )
        },
        lab("ma.passwort", Topic.Probability, M, 0.5f) {
            val len = pick(8, 6, 10); val chars = 62; val v = chars.toDouble().pow(len)
            val secs = v / 1e10
            val (tDe, tEn) = when {
                secs < 120 -> "${n(secs, 0)} Sekunden" to "${n(secs, 0)} seconds"
                secs < 2 * 86400 -> "${n(secs / 3600, 1)} Stunden" to "${n(secs / 3600, 1)} hours"
                else -> "${n(secs / 3.156e7, 1)} Jahre" to "${n(secs / 3.156e7, 1)} years"
            }
            task(
                question = l("Ein Passwort hat ${len} Zeichen, jedes ist ein Groß- oder Kleinbuchstabe oder eine Ziffer (62 Möglichkeiten). Wie viele verschiedene Passwörter gibt es?", "A password has ${len} characters, each an upper- or lower-case letter or a digit (62 possibilities). How many different passwords are there?"),
                steps = listOf(big(l("Anzahl", "Number"), v, "", rel = 0.02, done = """§62^{${len}} \approx ${sci(v)}§""",
                    hints = listOf(l("Kernidee: Jede Stelle hat 62 Möglichkeiten, unabhängig von den anderen. Also multiplizieren.", "Key idea: every position has 62 options, independently of the others. So multiply."), """§62^{${len}}§"""))),
                solution = listOf("""§62^{${len}} \approx ${sci(v)}§"""),
                background = l("Mit jedem zusätzlichen Zeichen wird ein Passwort 62-mal schwerer zu erraten. Länge schlägt Komplexität: Ein langer Satz aus normalen Wörtern ist oft sicherer als ein kurzes Wirrwarr. Moderne Grafikkarten probieren zehn Milliarden Passwörter pro Sekunde, wenn sie an eine gestohlene Datenbank kommen.", "With every extra character a password becomes 62 times harder to guess. Length beats complexity: a long sentence of ordinary words is often more secure than a short jumble. Modern graphics cards try ten billion passwords per second if they get hold of a stolen database."),
                fact = l("Bei zehn Milliarden Versuchen pro Sekunde bräuchte man für alle Möglichkeiten $tDe.", "At ten billion attempts per second, trying them all would take $tEn."),
            )
        },
        lab("ma.anagramm", Topic.Probability, M, 0.6f) {
            val (word, counts) = pick(l("ANANAS", "BANANA") to listOf(3, 2, 1), "MISSISSIPPI" to listOf(4, 4, 2, 1), l("OTTO", "OTTO") to listOf(2, 2))
            val total = counts.sum(); val v = fact4(total) / counts.fold(1.0) { a, c -> a * fact4(c) }
            task(
                question = l("Auf wie viele verschiedene Arten kann man die Buchstaben des Wortes $word anordnen?", "In how many different ways can the letters of the word $word be arranged?"),
                steps = listOf(num(l("Anordnungen", "Arrangements"), v, "", abs = 0.0, done = """§${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: Wären alle Buchstaben verschieden, wären es ${total}! Anordnungen. Gleiche Buchstaben kann man aber vertauschen, ohne dass sich etwas ändert.", "Key idea: if all the letters were different there would be ${total}! arrangements. But identical letters can be swapped without anything changing."), """§\dfrac{${total}!}{${counts.filter { it > 1 }.joinToString(" \\cdot ") { "$it!" }}}§"""))),
                solution = listOf("""§\dfrac{${total}!}{${counts.filter { it > 1 }.joinToString(" \\cdot ") { "$it!" }}} = ${m(v, 0)}§"""),
                background = l("Bei Anordnungen mit Wiederholung teilt man durch die Vertauschungen der gleichen Elemente, denn die erzeugen keine neuen Wörter. Dieselbe Rechnung zählt zum Beispiel, wie viele Wege es auf einem Gitter gibt oder wie viele verschiedene Reihenfolgen ein Kartenstapel mit doppelten Karten haben kann.", "For arrangements with repetition you divide by the swaps of identical elements, since they don't produce new words. The same calculation counts, for example, how many paths there are on a grid or how many different orders a deck with duplicate cards can have."),
            )
        },
        lab("ma.rundertisch", Topic.Probability, M, 0.6f) {
            val nn = pick(6, 5, 8); val v = fact4(nn - 1)
            task(
                question = l("${nn} Freunde setzen sich an einen runden Tisch. Zwei Sitzordnungen gelten als gleich, wenn jeder dieselben Nachbarn links und rechts hat. Wie viele verschiedene Sitzordnungen gibt es?", "${nn} friends sit down at a round table. Two seating plans count as the same if everyone has the same neighbours on their left and right. How many different seating plans are there?"),
                steps = listOf(num(l("Sitzordnungen", "Seating plans"), v, "", abs = 0.0, done = """§${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: An einem runden Tisch kann man alle um einen Platz weiterrücken lassen, und es ändert sich nichts. Setze deshalb eine Person fest.", "Key idea: at a round table everyone can move along one seat and nothing changes. So fix one person in place."), """§(n - 1)!§"""))),
                solution = listOf("""§(${nn} - 1)! = ${m(v, 0)}§"""),
                background = l("In einer Reihe gäbe es ${n(fact4(nn), 0)} Anordnungen. Am runden Tisch fallen aber je ${nn} davon zusammen, weil sie durch Drehen auseinander hervorgehen. Solche Symmetrien sind in der Kombinatorik entscheidend. Wer Fehler beim Zählen vermeiden will, fragt sich immer: Welche Anordnungen gelten als gleich?", "In a row there would be ${n(fact4(nn), 0)} arrangements. At a round table, though, groups of ${nn} of them coincide, because they can be turned into one another by rotation. Such symmetries are crucial in combinatorics. To avoid counting errors, always ask: which arrangements count as the same?"),
            )
        },
        lab("ma.dartpi", Topic.Probability, M, 0.6f) {
            val p = PI / 4 * 100
            task(
                question = l("Auf eine quadratische Dartscheibe ist ein Kreis gemalt, der alle vier Seiten berührt. Ein Pfeil trifft die Scheibe an einer völlig zufälligen Stelle. Wie wahrscheinlich landet er im Kreis?", "A circle touching all four sides is painted on a square dartboard. A dart hits the board at a completely random spot. How likely is it to land inside the circle?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", abs = 0.2, done = """§\dfrac{\pi}{4} \approx ${m(p, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Bei gleichmäßigem Zufall ist die Wahrscheinlichkeit Fläche des Ziels durch Gesamtfläche.", "Key idea: with uniform randomness the probability is the area of the target over the total area."), """§\dfrac{\pi r^2}{(2r)^2}§"""))),
                solution = listOf("""§\dfrac{\pi r^2}{4 r^2} = \dfrac{\pi}{4} \approx ${m(p / 100, 4)}§"""),
                background = l("Das lässt sich umdrehen: Wirft man sehr viele zufällige Pfeile und zählt, wie viele im Kreis landen, kann man π schätzen. Diese Monte-Carlo-Methode nutzt Zufall, um Fragen zu beantworten, die sich nicht direkt ausrechnen lassen. Sie wird heute in der Physik, bei Wettervorhersagen und in Finanzmodellen eingesetzt.", "This can be turned round: if you throw a great many random darts and count how many land in the circle, you can estimate π. This Monte Carlo method uses chance to answer questions that can't be calculated directly. Today it's used in physics, weather forecasting and financial models."),
            )
        },
        lab("ma.wartezeit", Topic.Probability, M, 0.6f) {
            val (what, p) = pick(l("eine Sechs (1/6)", "a six (1/6)") to 6, l("einen Pasch mit zwei Würfeln (1/6)", "a double with two dice (1/6)") to 6, l("Kopf bei einer Münze (1/2)", "heads with a coin (1/2)") to 2, l("eine 1 auf einem Zwölferwürfel (1/12)", "a 1 on a twelve-sided die (1/12)") to 12)
            task(
                question = l("Wie viele Versuche brauchst du im Durchschnitt, bis du zum ersten Mal $what bekommst?", "On average, how many attempts do you need before you get $what for the first time?"),
                steps = listOf(num(l("Durchschnittliche Anzahl Versuche", "Average number of attempts"), p.toDouble(), "", abs = 0.0, done = """§${p}§""",
                    hints = listOf(l("Kernidee: Bei Wahrscheinlichkeit p pro Versuch wartet man im Mittel 1/p Versuche.", "Key idea: with probability p per attempt, you wait 1/p attempts on average."), """§E = \dfrac{1}{p}§"""))),
                solution = listOf("""§E = \dfrac{1}{1/${p}} = ${p}§"""),
                background = l("Das klingt selbstverständlich, ist aber eine echte Rechnung: Man summiert über alle möglichen Wartezeiten, jede mit ihrer Wahrscheinlichkeit. Der Durchschnitt täuscht aber: Bei einer Sechs braucht man in einem Drittel der Fälle mehr als 6 Würfe, und manchmal sogar über 20.", "That sounds obvious, but it's a genuine calculation: you sum over all possible waiting times, each with its probability. But the average is deceptive: for a six, in a third of cases you need more than 6 rolls, and sometimes over 20."),
            )
        },
        lab("ma.gitterwege", Topic.Probability, M, 0.6f) {
            val a = pick(4, 3, 5); val b = pick(4, 3, 6); val v = binom4(a + b, a)
            task(
                question = l("Im Straßengitter einer Stadt liegt dein Ziel ${a} Blocks nach Osten und ${b} Blocks nach Norden. Du gehst immer nur nach Osten oder Norden, nie zurück. Wie viele verschiedene kürzeste Wege gibt es?", "In a city's street grid your destination is ${a} blocks east and ${b} blocks north. You only ever walk east or north, never back. How many different shortest routes are there?"),
                steps = listOf(num(l("Anzahl Wege", "Number of routes"), v, "", abs = 0.0, done = """§${m(v, 0)}§""",
                    hints = listOf(l("Kernidee: Jeder Weg besteht aus ${a + b} Schritten, davon ${a} nach Osten. Man muss nur wählen, welche der Schritte nach Osten gehen.", "Key idea: every route has ${a + b} steps, ${a} of them east. You only have to choose which steps go east."), """§\binom{${a + b}}{${a}}§"""))),
                solution = listOf("""§\binom{${a + b}}{${a}} = ${m(v, 0)}§"""),
                background = l("Statt alle Wege zu zeichnen, übersetzt man sie in Wörter aus O und N, wie OONONN. Dann wird aus einem Geometrie- ein Zählproblem. Jede Kreuzung im Gitter bekommt so viele Wege, wie zu ihren Nachbarn links und unten führen, und so entsteht wieder das Pascalsche Dreieck.", "Instead of drawing all the routes, you translate them into words made of E and N, like EENENN. Then a geometry problem becomes a counting problem. Each junction in the grid gets as many routes as lead to its neighbours on the left and below, and so Pascal's triangle appears again."),
            )
        },
        lab("ma.stichprobe", Topic.Probability, M, 0.6f) {
            val pDef = pick(0.02, 0.01, 0.05); val nn = pick(50, 100, 20); val p = (1 - pDef).pow(nn) * 100
            task(
                question = l("In einer Fabrik sind ${n(pDef * 100, 0)} % der Bauteile fehlerhaft. Eine Prüferin nimmt ${nn} zufällige Teile. Wie wahrscheinlich ist kein einziges fehlerhaftes dabei?", "In a factory ${n(pDef * 100, 0)}% of the parts are faulty. An inspector takes ${nn} random parts. How likely is it that not a single one is faulty?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", rel = 0.03, done = """§\approx ${m(p, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Jedes Teil ist mit Wahrscheinlichkeit ${n(1 - pDef, 2)} in Ordnung, und alle ${nn} müssen es sein.", "Key idea: each part is fine with probability ${n(1 - pDef, 2)}, and all ${nn} must be."), """§P = ${m(1 - pDef, 2)}^{${nn}}§"""))),
                solution = listOf("""§P = ${m(1 - pDef, 2)}^{${nn}} \approx ${m(p / 100, 3)}§"""),
                background = l("Viele kleine Wahrscheinlichkeiten summieren sich. Obwohl jedes einzelne Teil fast sicher in Ordnung ist, findet man in einer großen Stichprobe ziemlich sicher ein fehlerhaftes. Qualitätskontrollen nutzen genau diese Rechnung, um zu entscheiden, wie viele Teile sie prüfen müssen, um Fehler mit hoher Sicherheit zu entdecken.", "Lots of small probabilities add up. Although each individual part is almost certainly fine, in a large sample you'll quite surely find a faulty one. Quality control uses exactly this calculation to decide how many parts to check in order to catch faults with high confidence."),
            )
        },
        lab("ma.zweikinder", Topic.Probability, H, 0.7f) {
            task(
                question = l("Eine Familie hat zwei Kinder. Du erfährst nur: Mindestens eins davon ist ein Mädchen. Wie wahrscheinlich sind beide Mädchen? (Jungen und Mädchen gleich wahrscheinlich.)", "A family has two children. All you learn is: at least one of them is a girl. How likely is it that both are girls? (Boys and girls equally likely.)"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), 100.0 / 3, "%", abs = 0.5, done = """§\tfrac{1}{3} \approx 33{,}3\ \%§""",
                    hints = listOf(l("Kernidee: Schreib alle gleich wahrscheinlichen Familien auf: JJ, JM, MJ, MM. Welche passen zur Information?", "Key idea: write down all the equally likely families: BB, BG, GB, GG. Which ones fit the information?"), l("Übrig bleiben JM, MJ und MM.", "BG, GB and GG are left.")))),
                solution = listOf(l("Von den drei Familien, die mindestens ein Mädchen haben (JM, MJ, MM), hat nur eine zwei Mädchen: 1/3.", "Of the three families with at least one girl (BG, GB, GG), only one has two girls: 1/3.")),
                background = l("Das Ergebnis widerspricht fast jedem Bauchgefühl, das ½ sagt. Der Grund: „Mindestens eins“ schließt nur JJ aus, verrät aber nicht, welches Kind das Mädchen ist. Wüsste man dagegen, dass das ältere Kind ein Mädchen ist, wäre die Antwort ½. Bedingte Wahrscheinlichkeiten hängen genau davon ab, welche Information man bekommt und wie.", "The result contradicts almost everyone's gut feeling, which says ½. The reason: 'at least one' only rules out BB but doesn't reveal which child is the girl. If instead you knew that the older child is a girl, the answer would be ½. Conditional probabilities depend precisely on what information you get and how."),
            )
        },
        lab("ma.binomial", Topic.Probability, H, 0.6f) {
            val nn = pick(10, 8, 6); val k = nn / 2; val p = binom4(nn, k) / 2.0.pow(nn) * 100
            task(
                question = l("Du wirfst eine faire Münze ${nn}-mal. Wie wahrscheinlich ist genau ${k}-mal Kopf, also genau halbe-halbe?", "You toss a fair coin ${nn} times. How likely is it to get exactly ${k} heads, i.e. exactly half and half?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", abs = 0.2, done = """§\approx ${m(p, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Jede einzelne Folge hat die Wahrscheinlichkeit (1/2)^${nn}. Zähle, wie viele Folgen genau ${k}-mal Kopf haben.", "Key idea: each individual sequence has probability (1/2)^${nn}. Count how many sequences have exactly ${k} heads."), """§P = \binom{${nn}}{${k}} \cdot \left(\dfrac{1}{2}\right)^{${nn}}§"""))),
                solution = listOf("""§\binom{${nn}}{${k}} = ${m(binom4(nn, k), 0)},\quad P = \dfrac{${m(binom4(nn, k), 0)}}{${m(2.0.pow(nn), 0)}} \approx ${m(p, 1)}\ \%§"""),
                background = l("Das wahrscheinlichste Einzelergebnis ist zwar halbe-halbe, aber bei ${nn} Würfen kommt es nur in ${n(p, 0)} % der Fälle vor. Je mehr Würfe, desto seltener wird ein exaktes Unentschieden, und doch kommt der Anteil der Köpfe immer näher an 50 %. Beides gilt gleichzeitig, und genau das ist das Gesetz der großen Zahlen.", "The single most likely result is indeed half and half, but with ${nn} tosses it only happens in ${n(p, 0)}% of cases. The more tosses, the rarer an exact tie becomes, and yet the proportion of heads gets closer and closer to 50%. Both are true at once, and that is exactly the law of large numbers."),
            )
        },
        lab("ma.normalverteilung", Topic.Probability, H, 0.6f) {
            val (k, pct) = pick(1 to 15.9, 2 to 2.3, 3 to 0.13)
            val iq = 100 + 15 * k
            task(
                question = l("IQ-Werte sind normalverteilt mit Mittelwert 100 und Standardabweichung 15. Faustregel: Innerhalb ±1σ liegen 68 %, innerhalb ±2σ 95 %, innerhalb ±3σ 99,7 %. Wie viel Prozent der Menschen haben einen IQ über ${iq}?", "IQ scores are normally distributed with mean 100 and standard deviation 15. Rule of thumb: 68% lie within ±1σ, 95% within ±2σ, 99.7% within ±3σ. What percentage of people have an IQ above ${iq}?"),
                steps = listOf(num(l("Anteil in Prozent", "Share in percent"), pct, "%", rel = 0.12, done = """§\approx ${m(pct, 2)}\ \%§""",
                    hints = listOf(l("Kernidee: ${iq} liegt genau ${k} Standardabweichungen über dem Mittelwert. Was nicht im mittleren Bereich liegt, verteilt sich gleich auf beide Seiten.", "Key idea: ${iq} lies exactly ${k} standard deviations above the mean. What isn't in the middle range is split equally between the two sides."), """§\dfrac{100\ \% - ${if (k == 1) "68" else if (k == 2) "95" else "99{,}7"}\ \%}{2}§"""))),
                solution = listOf("""§\dfrac{100 - ${if (k == 1) "68{,}3" else if (k == 2) "95{,}4" else "99{,}73"}}{2}\ \% \approx ${m(pct, 2)}\ \%§"""),
                background = l("Die Glockenkurve beschreibt erstaunlich viele Dinge: Körpergrößen, Messfehler, Prüfungsnoten. Der Grund ist der zentrale Grenzwertsatz: Wenn viele kleine, unabhängige Zufälle zusammenwirken, entsteht fast immer eine Normalverteilung. Die Standardabweichung misst, wie breit die Glocke ist.", "The bell curve describes an astonishing number of things: body heights, measurement errors, exam grades. The reason is the central limit theorem: when many small, independent random effects add up, a normal distribution almost always results. The standard deviation measures how wide the bell is."),
            )
        },
        lab("ma.varianz", Topic.Probability, H, 0.6f) {
            val v = (1..6).sumOf { (it - 3.5) * (it - 3.5) } / 6
            task(
                question = l("Wie groß ist die Varianz der Augenzahl eines fairen Würfels, also der mittlere quadrierte Abstand vom Erwartungswert 3,5?", "What is the variance of the number shown by a fair die, i.e. the average squared distance from the expected value 3.5?"),
                steps = listOf(num(l("Varianz", "Variance"), v, "", abs = 0.005, done = """§\dfrac{35}{12} \approx ${m(v, 3)}§""",
                    hints = listOf(l("Kernidee: Für jede Augenzahl den Abstand zu 3,5 quadrieren, dann den Durchschnitt nehmen.", "Key idea: for each number, square its distance from 3.5, then take the average."), """§\dfrac{(1 - 3{,}5)^2 + (2 - 3{,}5)^2 + \dots + (6 - 3{,}5)^2}{6}§"""))),
                solution = listOf("""§\dfrac{6{,}25 + 2{,}25 + 0{,}25 + 0{,}25 + 2{,}25 + 6{,}25}{6} = \dfrac{17{,}5}{6} \approx ${m(v, 3)}§""", l("""Standardabweichung: §\sqrt{${m(v, 3)}} \approx ${m(sqrt(v), 2)}§""", """Standard deviation: §\sqrt{${m(v, 3)}} \approx ${m(sqrt(v), 2)}§""")),
                background = l("Der Erwartungswert sagt, wo die Mitte liegt, die Varianz, wie stark die Werte streuen. Man quadriert die Abstände, damit sich positive und negative nicht aufheben und große Abweichungen stärker zählen. Die Wurzel daraus, die Standardabweichung, hat wieder dieselbe Einheit wie die Werte selbst.", "The expected value tells you where the centre lies, the variance how much the values spread out. The distances are squared so that positive and negative ones don't cancel and large deviations count more. Its square root, the standard deviation, has the same unit as the values themselves again."),
            )
        },
        lab("ma.sammelbilder", Topic.Probability, X, 0.7f) {
            val k = pick(6, 10, 4); val v = k * (1..k).sumOf { 1.0 / it }
            task(
                question = l("Zu jedem Müsliriegel gibt es eins von ${k} verschiedenen Sammelbildern, jedes gleich wahrscheinlich. Wie viele Riegel musst du im Durchschnitt kaufen, bis du alle ${k} hast?", "Every cereal bar comes with one of ${k} different collector cards, each equally likely. On average, how many bars do you have to buy before you have all ${k}?"),
                steps = listOf(num(l("Durchschnittliche Anzahl Riegel", "Average number of bars"), v, "", abs = 0.05, done = """§\approx ${m(v, 1)}§""",
                    hints = listOf(l("Kernidee: Zerlege die Wartezeit in Etappen. Hast du schon j Bilder, ist ein neues mit Wahrscheinlichkeit (${k} − j)/${k} dabei, du wartest also im Mittel ${k}/(${k} − j) Riegel.", "Key idea: split the waiting into stages. If you already have j cards, a new one comes with probability (${k} − j)/${k}, so you wait ${k}/(${k} − j) bars on average."), """§E = ${k} \left(1 + \dfrac{1}{2} + \dots + \dfrac{1}{${k}}\right)§"""))),
                solution = listOf("""§E = ${k} \cdot ${m(v / k, 3)} \approx ${m(v, 1)}§"""),
                background = l("Das Sammelbilderproblem zeigt, warum die letzten Bilder so lange dauern: Das erste neue Bild kommt sofort, das letzte nur mit Wahrscheinlichkeit 1/${k}. Allein darauf wartet man im Mittel ${k} Käufe. Bei einem Album mit 600 Bildern wären es im Schnitt über 4000 Tüten, wenn man nicht tauscht.", "The coupon collector's problem shows why the last cards take so long: the first new card comes straight away, the last only with probability 1/${k}. For that one alone you wait ${k} purchases on average. For an album with 600 stickers it would take over 4000 packets on average if you don't swap."),
            )
        },
        lab("ma.buffon", Topic.Probability, X, 0.7f) {
            val p = 2 / PI * 100
            task(
                question = l("Buffons Nadel: Auf einem Dielenboden sind die Fugen genau so weit auseinander, wie eine Nadel lang ist. Du wirfst die Nadel zufällig auf den Boden. Wie wahrscheinlich kreuzt sie eine Fuge?", "Buffon's needle: on a wooden floor the gaps between boards are exactly as far apart as a needle is long. You drop the needle on the floor at random. How likely is it to cross a gap?"),
                steps = listOf(num(l("Wahrscheinlichkeit in Prozent", "Probability in percent"), p, "%", abs = 0.3, done = """§\dfrac{2}{\pi} \approx ${m(p, 1)}\ \%§""",
                    hints = listOf(l("Kernidee: Entscheidend sind zwei Zufallsgrößen: der Abstand der Nadelmitte zur nächsten Fuge und der Winkel der Nadel. Die Nadel kreuzt, wenn ihre halbe Länge mal sin(Winkel) größer ist als der Abstand.", "Key idea: two random quantities matter: the distance from the needle's centre to the nearest gap, and the needle's angle. It crosses if half its length times sin(angle) is greater than the distance."), """§P = \dfrac{2}{\pi}§"""))),
                solution = listOf("""§P = \dfrac{1}{\pi/2} \int_0^{\pi/2} \sin\theta\, d\theta = \dfrac{2}{\pi} \approx ${m(p / 100, 4)}§"""),
                background = l("Georges-Louis de Buffon stellte dieses Problem 1777, und es war eines der ersten, das Geometrie und Zufall verband. Dass ausgerechnet π herauskommt, erklärt sich aus der Drehung der Nadel: Ihre Winkel verteilen sich über einen Halbkreis. Man kann so durch Nadelwerfen π schätzen, allerdings sehr langsam.", "Georges-Louis de Buffon posed this problem in 1777, and it was one of the first to combine geometry and chance. That π of all numbers appears comes from the rotation of the needle: its angles are spread over a semicircle. You can estimate π by throwing needles, though very slowly."),
                fact = l("1901 warf der Mathematiker Mario Lazzarini angeblich 3408 Nadeln und kam auf 355/113 ≈ 3,1415929. Das Ergebnis war wohl zu gut, um ehrlich zu sein.", "In 1901 the mathematician Mario Lazzarini supposedly threw 3408 needles and got 355/113 ≈ 3.1415929. The result was probably too good to be honest."),
            )
        },
    )
}
