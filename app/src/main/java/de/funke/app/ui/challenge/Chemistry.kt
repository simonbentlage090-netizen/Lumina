package de.funke.app.ui.challenge

import de.funke.app.ui.Lang
import de.funke.app.ui.tr
import kotlin.random.Random

/*
 * Chemie: Elemente, Formeln, Molmasse, Reaktionsgleichungen. Selbst erzeugt, Daten sind Fakten
 * (Atommassen gerundet, wie in der Schule üblich).
 */
object Chemistry {
    private class El(val sym: String, val de: String, val en: String, val z: Int)

    private val Elements = listOf(
        El("H", "Wasserstoff", "Hydrogen", 1), El("He", "Helium", "Helium", 2), El("Li", "Lithium", "Lithium", 3),
        El("C", "Kohlenstoff", "Carbon", 6), El("N", "Stickstoff", "Nitrogen", 7), El("O", "Sauerstoff", "Oxygen", 8),
        El("F", "Fluor", "Fluorine", 9), El("Ne", "Neon", "Neon", 10), El("Na", "Natrium", "Sodium", 11),
        El("Mg", "Magnesium", "Magnesium", 12), El("Al", "Aluminium", "Aluminium", 13), El("Si", "Silicium", "Silicon", 14),
        El("P", "Phosphor", "Phosphorus", 15), El("S", "Schwefel", "Sulfur", 16), El("Cl", "Chlor", "Chlorine", 17),
        El("K", "Kalium", "Potassium", 19), El("Ca", "Calcium", "Calcium", 20), El("Fe", "Eisen", "Iron", 26),
        El("Cu", "Kupfer", "Copper", 29), El("Zn", "Zink", "Zinc", 30), El("Ag", "Silber", "Silver", 47),
        El("Sn", "Zinn", "Tin", 50), El("I", "Iod", "Iodine", 53), El("W", "Wolfram", "Tungsten", 74),
        El("Au", "Gold", "Gold", 79), El("Hg", "Quecksilber", "Mercury", 80), El("Pb", "Blei", "Lead", 82),
        El("U", "Uran", "Uranium", 92),
    )
    /** Symbole, die nicht vom deutschen/englischen Namen kommen (lateinisch) – gut für "schwer" */
    private val Tricky = setOf("Na", "K", "Fe", "Cu", "Ag", "Sn", "W", "Au", "Hg", "Pb")
    private val name: (El) -> String = { if (Lang.german) it.de else it.en }

    private val Mass = mapOf("H" to 1, "C" to 12, "N" to 14, "O" to 16, "Na" to 23, "Mg" to 24, "S" to 32, "Ca" to 40, "K" to 39)

    /** Formel als Liste (Element, Anzahl) + Schreibweise + Name */
    private class Molecule(val parts: List<Pair<String, Int>>, val de: String, val en: String) {
        val formula: String get() = parts.joinToString("") { (e, n) -> e + if (n > 1) sub(n) else "" }
    }

    private val Molecules = listOf(
        Molecule(listOf("H" to 2, "O" to 1), "Wasser", "water"),
        Molecule(listOf("C" to 1, "O" to 2), "Kohlenstoffdioxid", "carbon dioxide"),
        Molecule(listOf("C" to 1, "H" to 4), "Methan", "methane"),
        Molecule(listOf("N" to 1, "H" to 3), "Ammoniak", "ammonia"),
        Molecule(listOf("O" to 2), "Sauerstoff", "oxygen"),
        Molecule(listOf("N" to 2), "Stickstoff", "nitrogen"),
        Molecule(listOf("C" to 2, "H" to 6), "Ethan", "ethane"),
        Molecule(listOf("Ca" to 1, "C" to 1, "O" to 3), "Kalk (Calciumcarbonat)", "chalk (calcium carbonate)"),
        Molecule(listOf("H" to 2, "S" to 1, "O" to 4), "Schwefelsäure", "sulfuric acid"),
        Molecule(listOf("C" to 6, "H" to 12, "O" to 6), "Traubenzucker", "glucose"),
        Molecule(listOf("Na" to 1, "O" to 1, "H" to 1), "Natronlauge (Natriumhydroxid)", "sodium hydroxide"),
        Molecule(listOf("Mg" to 1, "O" to 1), "Magnesiumoxid", "magnesium oxide"),
        Molecule(listOf("C" to 2, "H" to 6, "O" to 1), "Ethanol", "ethanol"),
        Molecule(listOf("H" to 2, "O" to 2), "Wasserstoffperoxid", "hydrogen peroxide"),
    )

    /** Reaktionsgleichungen mit einer Lücke: Text mit "_" und der fehlende Faktor */
    private class Reaction(val text: String, val missing: Int, val whyDe: String, val whyEn: String)

    private val Reactions = listOf(
        Reaction("_ H₂ + O₂ → 2 H₂O", 2, "Rechts stehen 4 H-Atome (2 · H₂O), also braucht man links 2 H₂.", "There are 4 H atoms on the right (2 · H₂O), so you need 2 H₂ on the left."),
        Reaction("N₂ + _ H₂ → 2 NH₃", 3, "Rechts stehen 2 · 3 = 6 H-Atome, also 3 H₂.", "There are 2 · 3 = 6 H atoms on the right, so 3 H₂."),
        Reaction("CH₄ + _ O₂ → CO₂ + 2 H₂O", 2, "Rechts: 2 O im CO₂ und 2 O im Wasser, zusammen 4 O-Atome, also 2 O₂.", "Right: 2 O in CO₂ and 2 O in the water, 4 O atoms in total, so 2 O₂."),
        Reaction("2 Na + Cl₂ → _ NaCl", 2, "Links stehen 2 Na und 2 Cl, also entstehen 2 NaCl.", "There are 2 Na and 2 Cl on the left, so 2 NaCl form."),
        Reaction("C₃H₈ + _ O₂ → 3 CO₂ + 4 H₂O", 5, "Rechts: 3 · 2 + 4 · 1 = 10 O-Atome, also 5 O₂.", "Right: 3 · 2 + 4 · 1 = 10 O atoms, so 5 O₂."),
        Reaction("4 Fe + _ O₂ → 2 Fe₂O₃", 3, "Rechts: 2 · 3 = 6 O-Atome, also 3 O₂.", "Right: 2 · 3 = 6 O atoms, so 3 O₂."),
        Reaction("2 Mg + O₂ → _ MgO", 2, "Links stehen 2 Mg und 2 O, also 2 MgO.", "There are 2 Mg and 2 O on the left, so 2 MgO."),
        Reaction("Zn + _ HCl → ZnCl₂ + H₂", 2, "Rechts stehen 2 Cl und 2 H, also braucht man 2 HCl.", "There are 2 Cl and 2 H on the right, so you need 2 HCl."),
        Reaction("2 H₂O₂ → 2 H₂O + _ O₂", 1, "Links: 4 O. Rechts im Wasser schon 2 O, es fehlen 2 O, also 1 O₂.", "Left: 4 O. The water on the right has 2 O, 2 O are missing, so 1 O₂."),
        Reaction("_ Al + 3 O₂ → 2 Al₂O₃", 4, "Rechts: 2 · 2 = 4 Al-Atome, also 4 Al.", "Right: 2 · 2 = 4 Al atoms, so 4 Al."),
    )

    fun make(d: Difficulty, r: Random): Challenge = when (d) {
        Difficulty.Easy -> when (r.nextInt(3)) {
            0 -> nameOfSymbol(d, r, Elements.filter { it.sym !in Tricky })
            1 -> protons(d, r)
            else -> phFact(d, r)
        }
        Difficulty.Medium -> when (r.nextInt(3)) {
            0 -> symbolOfName(d, r)
            1 -> atoms(d, r)
            else -> nameOfSymbol(d, r, Elements.filter { it.sym in Tricky })
        }
        else -> if (r.nextBoolean()) molarMass(d, r) else balance(d, r)
    }

    private fun nameOfSymbol(d: Difficulty, r: Random, pool: List<El>): Challenge {
        val e = pool.random(r)
        val wrong = Elements.filter { it != e }.shuffled(r).take(3).map(name)
        return choice(
            Topic.Chemistry, d, tr("Welches Element hat das Symbol „${e.sym}“?", "Which element has the symbol “${e.sym}”?"),
            name(e), wrong,
            listOf(tr("${e.sym} steht für ${name(e)}, Ordnungszahl ${e.z}.", "${e.sym} stands for ${name(e)}, atomic number ${e.z}.")) +
                if (e.sym in Tricky) listOf(tr("Das Symbol kommt vom lateinischen Namen.", "The symbol comes from the Latin name.")) else emptyList(),
            r,
        )
    }

    private fun symbolOfName(d: Difficulty, r: Random): Challenge {
        val e = Elements.filter { it.sym in Tricky || it.sym.length == 2 }.random(r)
        fun cap2(x: String) = x.take(2).lowercase().replaceFirstChar { it.uppercase() }
        val wrong = (listOf(cap2(e.de), cap2(e.en), e.de.take(1)) + Elements.filter { it != e }.shuffled(r).map { it.sym })
            .filter { it != e.sym }.distinct().take(3)
        return choice(
            Topic.Chemistry, d, tr("Welches Symbol hat ${name(e)}?", "What is the symbol for ${name(e)}?"),
            e.sym, wrong,
            listOf(tr("${name(e)} hat das Symbol ${e.sym}.", "${name(e)} has the symbol ${e.sym}.")) +
                if (e.sym in Tricky) listOf(tr("Das Symbol kommt vom lateinischen Namen.", "The symbol comes from the Latin name.")) else emptyList(),
            r,
        )
    }

    private fun protons(d: Difficulty, r: Random): Challenge {
        val e = Elements.filter { it.z <= 20 }.random(r)
        return task(
            Topic.Chemistry, d,
            tr("Wie viele Protonen hat ein Atom ${name(e)} (${e.sym})?", "How many protons does an atom of ${name(e)} (${e.sym}) have?"),
            e.z,
            steps = listOf(
                tr("Die Ordnungszahl im Periodensystem = Anzahl der Protonen im Kern.", "The atomic number in the periodic table = number of protons in the nucleus."),
                tr("${name(e)} hat die Ordnungszahl ${e.z}.", "${name(e)} has atomic number ${e.z}."),
            ),
        )
    }

    private fun phFact(d: Difficulty, r: Random): Challenge = when (r.nextInt(3)) {
        0 -> choice(
            Topic.Chemistry, d, tr("Eine Lösung hat pH 2. Was ist sie?", "A solution has pH 2. What is it?"),
            tr("sauer", "acidic"), listOf(tr("neutral", "neutral"), tr("basisch (alkalisch)", "basic (alkaline)")),
            listOf(tr("pH unter 7 = sauer, 7 = neutral, über 7 = basisch.", "pH below 7 = acidic, 7 = neutral, above 7 = basic."), tr("pH 2 ist stark sauer, etwa wie Zitronensaft.", "pH 2 is strongly acidic, like lemon juice.")),
            r,
        )
        1 -> choice(
            Topic.Chemistry, d, tr("Welchen pH-Wert hat reines Wasser?", "What pH does pure water have?"),
            "7", listOf("0", "3", "11"),
            listOf(tr("Reines Wasser ist neutral: pH 7.", "Pure water is neutral: pH 7.")),
            r,
        )
        else -> choice(
            Topic.Chemistry, d, tr("Was entsteht, wenn eine Säure und eine Lauge sich genau neutralisieren?", "What forms when an acid and a base exactly neutralize each other?"),
            tr("Salz und Wasser", "Salt and water"), listOf(tr("Sauerstoff", "Oxygen"), tr("Nur Wasserstoff", "Only hydrogen"), tr("Kohlenstoffdioxid", "Carbon dioxide")),
            listOf(tr("Neutralisation: Säure + Lauge → Salz + Wasser.", "Neutralization: acid + base → salt + water."), tr("Beispiel: HCl + NaOH → NaCl + H₂O", "Example: HCl + NaOH → NaCl + H₂O")),
            r,
        )
    }

    private fun atoms(d: Difficulty, r: Random): Challenge {
        val m = Molecules.random(r)
        val total = m.parts.sumOf { it.second }
        val mName = if (Lang.german) m.de else m.en
        return task(
            Topic.Chemistry, d,
            tr("Aus wie vielen Atomen besteht ein Molekül ${m.formula} ($mName)?", "How many atoms make up one molecule of ${m.formula} ($mName)?"),
            total,
            steps = listOf(
                tr("Die kleinen Zahlen zählen die Atome davor, ohne Zahl ist es 1.", "The small numbers count the atom in front, no number means 1."),
                m.parts.joinToString(" + ") { "${it.second} ${it.first}" } + " = $total",
            ),
        )
    }

    private fun molarMass(d: Difficulty, r: Random): Challenge {
        val m = Molecules.random(r)
        val total = m.parts.sumOf { (e, n) -> Mass.getValue(e) * n }
        val mName = if (Lang.german) m.de else m.en
        return task(
            Topic.Chemistry, d,
            tr("Wie groß ist die molare Masse von ${m.formula} ($mName)?", "What is the molar mass of ${m.formula} ($mName)?") + "\n" +
                m.parts.joinToString(", ") { "${it.first} = ${Mass[it.first]}" } + " g/mol",
            total, "g/mol",
            steps = listOf(
                tr("Molare Masse: M = Summe der Atommassen", "Molar mass: M = sum of the atomic masses"),
                m.parts.joinToString(" + ") { (e, n) -> if (n > 1) "$n · ${Mass[e]}" else "${Mass[e]}" },
                "M = $total g/mol",
            ),
        )
    }

    private fun balance(d: Difficulty, r: Random): Challenge {
        val x = Reactions.random(r)
        return task(
            Topic.Chemistry, d,
            tr("Welche Zahl gehört in die Lücke, damit die Gleichung stimmt?", "Which number belongs in the gap to balance the equation?") + "\n" + x.text,
            x.missing,
            steps = listOf(
                tr("Erhaltung der Masse: Links und rechts müssen von jedem Element gleich viele Atome stehen.", "Conservation of mass: each element needs the same number of atoms on both sides."),
                tr(x.whyDe, x.whyEn),
                tr("Die Lücke ist ${x.missing}.", "The gap is ${x.missing}."),
            ),
        )
    }
}

/** Zahl als tiefgestellte Ziffern (H₂O). */
internal fun sub(n: Int): String = n.toString().map { "₀₁₂₃₄₅₆₇₈₉"[it - '0'] }.joinToString("")
