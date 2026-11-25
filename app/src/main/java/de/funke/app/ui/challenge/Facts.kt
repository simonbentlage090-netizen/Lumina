package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import de.funke.app.ui.tr
import kotlin.random.Random

/*
 * Kleine Wissenspakete (selbst geschrieben, frei verwendbar): Biologie und Spaß-Fakten.
 * Die erste Antwort ist jeweils die richtige, beim Stellen wird gemischt.
 */
object Facts {
    private class Q(
        val d: Difficulty,
        val de: String, val en: String,
        val okDe: String, val okEn: String,
        val wrongDe: List<String>, val wrongEn: List<String>,
        val whyDe: String, val whyEn: String,
    )

    private fun ask(topic: Topic, list: List<Q>, d: Difficulty, r: Random): Challenge {
        val q = list.filter { it.d == d }.ifEmpty { list }.random(r)
        val g = Lang.german
        return choice(topic, q.d, if (g) q.de else q.en, if (g) q.okDe else q.okEn, if (g) q.wrongDe else q.wrongEn, listOf(if (g) q.whyDe else q.whyEn), r)
    }

    fun biology(d: Difficulty, r: Random) = ask(Topic.Biology, Bio, d, r)

    fun funFacts(d: Difficulty, r: Random): Challenge {
        val f = Fun.filter { it.d == d }.ifEmpty { Fun }.random(r)
        val g = Lang.german
        val yes = tr("Stimmt", "True")
        val no = tr("Stimmt nicht", "False")
        return choice(
            Topic.FunFacts, f.d,
            tr("Stimmt das?", "True or false?") + "\n" + (if (g) f.de else f.en),
            if (f.ok) yes else no, listOf(if (f.ok) no else yes),
            listOf(if (g) f.whyDe else f.whyEn), r,
        )
    }

    val count: Int get() = Bio.size + Fun.size

    private val Bio = listOf(
        Q(Difficulty.Easy, "Welches Organ pumpt das Blut durch den Körper?", "Which organ pumps blood through the body?",
            "Das Herz", "The heart", listOf("Die Lunge", "Die Leber", "Die Niere"), listOf("The lungs", "The liver", "The kidney"),
            "Das Herz ist ein Muskel und schlägt in Ruhe etwa 60- bis 80-mal pro Minute.", "The heart is a muscle that beats about 60 to 80 times a minute at rest."),
        Q(Difficulty.Easy, "Was ist das größte Organ des Menschen?", "What is the largest organ of the human body?",
            "Die Haut", "The skin", listOf("Die Leber", "Das Gehirn", "Der Darm"), listOf("The liver", "The brain", "The gut"),
            "Die Haut eines Erwachsenen hat etwa 2 m² Fläche.", "An adult's skin covers about 2 m²."),
        Q(Difficulty.Easy, "Was brauchen Pflanzen für die Fotosynthese?", "What do plants need for photosynthesis?",
            "Licht, Wasser und Kohlenstoffdioxid", "Light, water and carbon dioxide", listOf("Nur Erde", "Sauerstoff und Zucker", "Dunkelheit und Wärme"), listOf("Only soil", "Oxygen and sugar", "Darkness and warmth"),
            "Daraus bauen Pflanzen Traubenzucker und geben Sauerstoff ab.", "From these, plants build glucose and release oxygen."),
        Q(Difficulty.Easy, "Zu welcher Tiergruppe gehört der Wal?", "Which animal group does the whale belong to?",
            "Säugetiere", "Mammals", listOf("Fische", "Reptilien", "Amphibien"), listOf("Fish", "Reptiles", "Amphibians"),
            "Wale atmen mit Lungen, sind warmblütig und säugen ihre Jungen.", "Whales breathe with lungs, are warm-blooded and nurse their young."),
        Q(Difficulty.Easy, "Wie viele Kammern hat das menschliche Herz?", "How many chambers does the human heart have?",
            "4", "4", listOf("2", "3", "6"), listOf("2", "3", "6"),
            "Zwei Vorhöfe und zwei Herzkammern.", "Two atria and two ventricles."),
        Q(Difficulty.Easy, "Zu welcher Tiergruppe gehört der Frosch?", "Which animal group does the frog belong to?",
            "Amphibien", "Amphibians", listOf("Reptilien", "Säugetiere", "Fische"), listOf("Reptiles", "Mammals", "Fish"),
            "Amphibien leben als Larve (Kaulquappe) im Wasser und später oft an Land.", "Amphibians live in water as larvae (tadpoles) and later often on land."),
        Q(Difficulty.Easy, "Wie warm ist der Körper eines gesunden Menschen ungefähr?", "About how warm is a healthy human body?",
            "37 °C", "37 °C", listOf("33 °C", "40 °C", "42 °C"), listOf("33 °C", "40 °C", "42 °C"),
            "Ab etwa 38 °C spricht man von erhöhter Temperatur.", "From about 38 °C on, the temperature counts as raised."),
        Q(Difficulty.Medium, "Welche Zellbestandteile nennt man oft „Kraftwerke der Zelle“?", "Which cell parts are often called the “powerhouses of the cell”?",
            "Mitochondrien", "Mitochondria", listOf("Ribosomen", "Zellkern", "Vakuole"), listOf("Ribosomes", "Nucleus", "Vacuole"),
            "In den Mitochondrien wird bei der Zellatmung Energie aus Zucker gewonnen.", "Mitochondria release energy from sugar during cellular respiration."),
        Q(Difficulty.Medium, "Wo in der Pflanzenzelle findet die Fotosynthese statt?", "Where in a plant cell does photosynthesis happen?",
            "In den Chloroplasten", "In the chloroplasts", listOf("Im Zellkern", "In den Mitochondrien", "In der Zellwand"), listOf("In the nucleus", "In the mitochondria", "In the cell wall"),
            "Chloroplasten enthalten den grünen Farbstoff Chlorophyll, der Licht einfängt.", "Chloroplasts contain the green pigment chlorophyll, which captures light."),
        Q(Difficulty.Medium, "Welche Form hat ein DNA-Molekül?", "What shape does a DNA molecule have?",
            "Doppelhelix", "Double helix", listOf("Kugel", "Einfacher Ring", "Würfel"), listOf("Sphere", "Single ring", "Cube"),
            "Zwei Stränge sind wie eine verdrehte Strickleiter umeinander gewunden.", "Two strands are wound around each other like a twisted rope ladder."),
        Q(Difficulty.Medium, "Wie viele Chromosomen hat eine normale menschliche Körperzelle?", "How many chromosomes does a normal human body cell have?",
            "46", "46", listOf("23", "44", "48"), listOf("23", "44", "48"),
            "23 Paare, je eines von der Mutter und vom Vater. Ei- und Samenzellen haben nur 23.", "23 pairs, one of each from mother and father. Egg and sperm cells have only 23."),
        Q(Difficulty.Medium, "Welche Blutzellen transportieren den Sauerstoff?", "Which blood cells carry oxygen?",
            "Rote Blutkörperchen", "Red blood cells", listOf("Weiße Blutkörperchen", "Blutplättchen", "Nervenzellen"), listOf("White blood cells", "Platelets", "Nerve cells"),
            "Ihr Farbstoff Hämoglobin bindet den Sauerstoff, deshalb ist Blut rot.", "Their pigment hemoglobin binds oxygen, which is why blood is red."),
        Q(Difficulty.Medium, "Welches Organ bildet das Hormon Insulin?", "Which organ makes the hormone insulin?",
            "Die Bauchspeicheldrüse", "The pancreas", listOf("Die Leber", "Die Schilddrüse", "Der Magen"), listOf("The liver", "The thyroid", "The stomach"),
            "Insulin senkt den Blutzuckerspiegel.", "Insulin lowers the blood sugar level."),
        Q(Difficulty.Medium, "Wo findet in der Lunge der Gasaustausch statt?", "Where in the lungs does gas exchange happen?",
            "In den Lungenbläschen", "In the alveoli", listOf("In der Luftröhre", "Im Kehlkopf", "Im Zwerchfell"), listOf("In the windpipe", "In the larynx", "In the diaphragm"),
            "Die Millionen Lungenbläschen haben zusammen eine riesige Oberfläche.", "The millions of alveoli add up to a huge surface area."),
        Q(Difficulty.Medium, "Gegen welche Erreger helfen Antibiotika?", "Which germs do antibiotics work against?",
            "Bakterien", "Bacteria", listOf("Viren", "Alle Erreger", "Allergien"), listOf("Viruses", "All germs", "Allergies"),
            "Gegen Viren, zum Beispiel bei einer Erkältung, helfen Antibiotika nicht.", "Antibiotics don't help against viruses, such as a cold."),
        Q(Difficulty.Hard, "Wie heißt der kleinste Knochen des Menschen?", "What is the smallest bone in the human body?",
            "Steigbügel", "Stapes (stirrup)", listOf("Amboss", "Hammer", "Kniescheibe"), listOf("Incus (anvil)", "Malleus (hammer)", "Kneecap"),
            "Der Steigbügel im Mittelohr ist nur etwa 3 mm groß.", "The stapes in the middle ear is only about 3 mm long."),
        Q(Difficulty.Hard, "Wer entdeckte mit Erbsen die Grundregeln der Vererbung?", "Who discovered the basic rules of inheritance using peas?",
            "Gregor Mendel", "Gregor Mendel", listOf("Charles Darwin", "Louis Pasteur", "Robert Koch"), listOf("Charles Darwin", "Louis Pasteur", "Robert Koch"),
            "Mendel kreuzte im 19. Jahrhundert Tausende Erbsenpflanzen.", "In the 19th century Mendel crossed thousands of pea plants."),
        Q(Difficulty.Hard, "Welches Enzym im Speichel beginnt schon im Mund, Stärke zu zerlegen?", "Which enzyme in saliva starts breaking down starch in the mouth?",
            "Amylase", "Amylase", listOf("Pepsin", "Lipase", "Insulin"), listOf("Pepsin", "Lipase", "Insulin"),
            "Deshalb schmeckt Brot süßlich, wenn man es lange kaut.", "That's why bread tastes sweet when you chew it for a long time."),
        Q(Difficulty.Hard, "Welches Vitamin bildet die Haut mithilfe von Sonnenlicht?", "Which vitamin does the skin make with the help of sunlight?",
            "Vitamin D", "Vitamin D", listOf("Vitamin C", "Vitamin A", "Vitamin B12"), listOf("Vitamin C", "Vitamin A", "Vitamin B12"),
            "Vitamin D ist unter anderem wichtig für die Knochen.", "Vitamin D is important for the bones, among other things."),
        Q(Difficulty.Hard, "Zu welchem Reich gehören Pilze?", "Which kingdom do fungi belong to?",
            "Zu einem eigenen Reich", "Their own kingdom", listOf("Zu den Pflanzen", "Zu den Tieren", "Zu den Bakterien"), listOf("Plants", "Animals", "Bacteria"),
            "Pilze machen keine Fotosynthese und sind näher mit Tieren verwandt als mit Pflanzen.", "Fungi don't photosynthesize and are more closely related to animals than to plants."),
    )

    private class F(val d: Difficulty, val ok: Boolean, val de: String, val en: String, val whyDe: String, val whyEn: String)

    private val Fun = listOf(
        F(Difficulty.Easy, true, "Ein Oktopus hat drei Herzen.", "An octopus has three hearts.",
            "Zwei pumpen Blut durch die Kiemen, eins durch den restlichen Körper.", "Two pump blood through the gills, one through the rest of the body."),
        F(Difficulty.Easy, false, "Goldfische können sich nur drei Sekunden lang etwas merken.", "Goldfish can only remember things for three seconds.",
            "Versuche zeigen, dass Goldfische sich Dinge über Monate merken können.", "Experiments show goldfish can remember things for months."),
        F(Difficulty.Easy, false, "Fledermäuse sind blind.", "Bats are blind.",
            "Fledermäuse können sehen. Im Dunkeln orientieren sie sich zusätzlich mit Ultraschall-Echos.", "Bats can see. In the dark they also find their way with ultrasound echoes."),
        F(Difficulty.Easy, true, "Elefanten können nicht springen.", "Elephants can't jump.",
            "Für ihr Gewicht sind ihre Beine dafür nicht gebaut, mindestens ein Fuß bleibt immer am Boden.", "Their legs aren't built for that weight; at least one foot always stays on the ground."),
        F(Difficulty.Easy, false, "Blitze schlagen nie zweimal an derselben Stelle ein.", "Lightning never strikes the same place twice.",
            "Hohe Gebäude wie das Empire State Building werden jedes Jahr viele Male getroffen.", "Tall buildings like the Empire State Building are hit many times every year."),
        F(Difficulty.Easy, true, "Im Weltall gibt es keinen Schall.", "There is no sound in space.",
            "Schall braucht einen Stoff wie Luft oder Wasser, um sich auszubreiten. Im Vakuum fehlt der.", "Sound needs a medium like air or water to travel. A vacuum has none."),
        F(Difficulty.Easy, true, "Flamingos sind rosa, weil sie Kleinkrebse und Algen fressen.", "Flamingos are pink because they eat small shrimp and algae.",
            "Farbstoffe aus der Nahrung färben die Federn. Ohne sie werden Flamingos blass.", "Pigments from their food color the feathers. Without them flamingos turn pale."),
        F(Difficulty.Medium, true, "Bananen sind botanisch gesehen Beeren.", "Botanically, bananas are berries.",
            "Botanisch sind Beeren Früchte, die aus einem Fruchtknoten entstehen und deren Samen im Fruchtfleisch liegen. Das trifft auf Bananen zu.", "Botanically, berries develop from one ovary with seeds inside the flesh. That fits bananas."),
        F(Difficulty.Medium, false, "Erdbeeren sind botanisch gesehen Beeren.", "Botanically, strawberries are berries.",
            "Erdbeeren sind Sammelnussfrüchte: Die eigentlichen Früchte sind die kleinen Nüsschen außen.", "Strawberries are aggregate fruits: the actual fruits are the tiny seeds on the outside."),
        F(Difficulty.Medium, false, "Die Chinesische Mauer kann man mit bloßem Auge vom Mond aus sehen.", "You can see the Great Wall of China from the Moon with the naked eye.",
            "Die Mauer ist zwar sehr lang, aber nur wenige Meter breit. Vom Mond aus ist sie viel zu schmal.", "The wall is very long but only a few meters wide. From the Moon it is far too narrow."),
        F(Difficulty.Medium, true, "Ein Tag auf der Venus dauert länger als ein Venusjahr.", "A day on Venus is longer than a year on Venus.",
            "Für eine Drehung um sich selbst braucht die Venus etwa 243 Erdtage, für eine Runde um die Sonne nur etwa 225.", "Venus needs about 243 Earth days to spin once, but only about 225 to orbit the Sun."),
        F(Difficulty.Medium, false, "Menschen nutzen nur 10 Prozent ihres Gehirns.", "Humans only use 10 percent of their brain.",
            "Hirnscans zeigen, dass über den Tag verteilt praktisch alle Bereiche aktiv sind.", "Brain scans show that practically all areas are active over the course of a day."),
        F(Difficulty.Medium, true, "Der Eiffelturm ist im Sommer ein paar Zentimeter höher als im Winter.", "The Eiffel Tower is a few centimeters taller in summer than in winter.",
            "Eisen dehnt sich bei Wärme aus. Beim Eiffelturm macht das bis zu etwa 15 cm aus.", "Iron expands when warm. For the Eiffel Tower that adds up to about 15 cm."),
        F(Difficulty.Medium, true, "Babys haben bei der Geburt mehr Knochen als Erwachsene.", "Babies have more bones at birth than adults.",
            "Neugeborene haben rund 300 Knochen, viele wachsen später zusammen. Erwachsene haben etwa 206.", "Newborns have about 300 bones; many fuse later. Adults have about 206."),
        F(Difficulty.Medium, false, "Napoleon war auffallend klein.", "Napoleon was remarkably short.",
            "Er war etwa 1,69 m groß, für seine Zeit eher durchschnittlich. Der Irrtum kommt unter anderem von alten französischen Maßeinheiten.", "He was about 1.69 m tall, roughly average for his time. The myth partly comes from old French units."),
        F(Difficulty.Hard, true, "Haie gab es schon, bevor es Bäume gab.", "Sharks existed before trees did.",
            "Die ältesten Haie sind über 400 Millionen Jahre alt, die ersten Bäume etwa 385 Millionen Jahre.", "The oldest sharks are over 400 million years old, the first trees about 385 million years."),
        F(Difficulty.Hard, true, "Wombats machen würfelförmigen Kot.", "Wombats make cube-shaped droppings.",
            "Unterschiedlich dehnbare Abschnitte im Darm formen die Würfel. So rollen sie nicht weg und markieren das Revier.", "Sections of the gut with different stretchiness shape the cubes, so they don't roll away and can mark territory."),
        F(Difficulty.Hard, false, "Alte Kirchenfenster sind unten dicker, weil Glas langsam nach unten fließt.", "Old church windows are thicker at the bottom because glass slowly flows down.",
            "Glas fließt bei Raumtemperatur praktisch nicht. Früher war Fensterglas einfach ungleichmäßig dick und wurde oft mit der dicken Seite nach unten eingesetzt.", "Glass practically doesn't flow at room temperature. Old panes were simply uneven and often fitted thick side down."),
        F(Difficulty.Hard, false, "Die Erde ist der Sonne im Juli am nächsten.", "Earth is closest to the Sun in July.",
            "Am nächsten ist sie Anfang Januar. Die Jahreszeiten entstehen durch die schräge Erdachse, nicht durch den Abstand.", "It is closest in early January. Seasons come from Earth's tilted axis, not its distance."),
        F(Difficulty.Hard, true, "Ganz reines Wasser leitet Strom nur sehr schlecht.", "Completely pure water conducts electricity very poorly.",
            "Strom leiten vor allem die gelösten Salze im Wasser. Trotzdem: Wasser und Strom nie zusammenbringen!", "It's mostly the dissolved salts that conduct. Still: never mix water and electricity!"),
        F(Difficulty.Hard, false, "Sterne funkeln, weil sie flackern.", "Stars twinkle because they flicker.",
            "Das Funkeln kommt von unruhiger Luft in unserer Atmosphäre, die das Licht hin und her lenkt.", "The twinkling comes from turbulent air in our atmosphere that bends the light back and forth."),
    )
}
