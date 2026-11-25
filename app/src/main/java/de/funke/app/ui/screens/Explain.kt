package de.funke.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.R
import de.funke.app.ui.Lang
import de.funke.app.ui.challenge.Answer
import de.funke.app.ui.challenge.Step
import de.funke.app.ui.challenge.Task
import de.funke.app.ui.challenge.isFormulaOnly
import de.funke.app.ui.challenge.signsIn
import de.funke.app.ui.challenge.solutionByStep
import de.funke.app.ui.challenge.stripLabel
import de.funke.app.ui.components.MathText
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr

/*
 * Erklärungen, die man ohne Vorwissen versteht (seit 13.3).
 *
 * Vorher zeigte der Lösungsweg nur die eingesetzten Formeln. Wer die Formel nicht kannte, wusste danach
 * immer noch nicht, WAS man tut und WARUM. Jetzt bekommt jeder Schritt eine Karte:
 *   Gefragt     – was der Schritt wissen will
 *   Die Idee    – der Gedanke dahinter, in Worten (die "Kernidee" aus den Tipps)
 *   So rechnest du – die allgemeine Formel, mit dem Satz von der Formelkarte
 *   Eingesetzt  – die Rechnung mit den Zahlen dieser Aufgabe
 *   → Ergebnis
 * Dazu "Zeichen kurz erklärt": Für jedes Rechenzeichen, das in der Aufgabe vorkommt (Bruch, Wurzel, Sinus,
 * Ableitung, ⇒, ≈ …), ein einfacher Satz. Das gilt für alle Aufgaben, weil es nur die Schreibweise erklärt.
 */

/** Alle Texte einer Aufgabe, in denen Formeln stehen können. */
internal fun formulaTexts(task: Task): List<String> = buildList {
    addAll(task.solution)
    task.steps.forEach { s -> addAll(s.hints); add(s.done); s.formula?.let { add(it.formula) } }
}

// ---------------------------------------------------------------------------------------------
// Ansicht
// ---------------------------------------------------------------------------------------------

/** Der Lösungsweg Schritt für Schritt (ersetzt die reine Formel-Zeitleiste). */
@Composable
internal fun StepByStep(task: Task) {
    val parts = remember(task.id) { solutionByStep(task) }
    val multi = task.steps.size > 1
    Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
        task.steps.forEachIndexed { i, step ->
            StepCard(
                index = i,
                multi = multi,
                step = step,
                lines = parts.getOrElse(i) { emptyList() },
                modifier = Modifier.reveal(50 * i),
            )
        }
    }
}

@Composable
private fun StepCard(index: Int, multi: Boolean, step: Step, lines: List<String>, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val body = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
    val ideas = step.hints.filterNot(::isFormulaOnly).map(::stripLabel)
    val formulas = step.hints.filter(::isFormulaOnly)
    val card = step.formula?.takeIf { c -> formulas.none { it.contains(c.formula.trim('$')) } }
    Column(
        modifier
            .fillMaxWidth()
            .clip(Shapes.card)
            .background(p.track)
            .padding(Space.s4),
    ) {
        // Kopf: Nummer und was gefragt ist
        Row(verticalAlignment = Alignment.Top) {
            if (multi) {
                Box(Modifier.size(24.dp).clip(CircleShape).background(p.accent), contentAlignment = Alignment.Center) {
                    Text("${index + 1}", style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"), color = p.onAccent)
                }
                Spacer(Modifier.width(Space.s3))
            }
            val ask = step.prompt.takeUnless { it == tr("Welche Antwort stimmt?", "Which answer is right?") || it == tr("Deine Antwort", "Your answer") }
            MathText(
                ask ?: tr("Die Frage", "The question"),
                style = MaterialTheme.typography.titleSmall,
                color = p.text,
                modifier = Modifier.weight(1f).padding(top = 2.dp),
            )
        }
        if (ideas.isNotEmpty()) {
            Part(tr("Die Idee", "The idea"))
            ideas.forEach { MathText(it, style = body, color = p.text, modifier = Modifier.padding(bottom = Space.s1)) }
        }
        if (formulas.isNotEmpty() || card != null) {
            Part(tr("So rechnest du", "How to calculate"))
            formulas.forEach { MathText(it, style = body, color = p.text, modifier = Modifier.padding(bottom = Space.s1)) }
            if (card != null) {
                MathText(card.formula, style = body, color = p.text, modifier = Modifier.padding(bottom = Space.s1))
                if (card.text.isNotBlank()) MathText(card.text, style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
            } else step.formula?.text?.takeIf { it.isNotBlank() }?.let {
                MathText(it, style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
            }
        }
        if (lines.isNotEmpty()) {
            val calc = lines.any { it.contains('$') }
            Part(if (calc) tr("Eingesetzt", "With the numbers") else tr("Erklärung", "Explanation"))
            lines.forEach { MathText(it, style = body, color = p.text, modifier = Modifier.padding(bottom = Space.s1)) }
        }
        if (step.done.isNotBlank() && step.answer !is Answer.Choice) {
            Spacer(Modifier.height(Space.s2))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.success, modifier = Modifier.size(Sizes.iconSm))
                Spacer(Modifier.width(Space.s2))
                MathText(step.done, style = MaterialTheme.typography.titleSmall, color = p.text)
            }
        } else if (step.answer is Answer.Choice) {
            val a = step.answer as Answer.Choice
            Spacer(Modifier.height(Space.s2))
            Row(verticalAlignment = Alignment.Top) {
                Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.success, modifier = Modifier.padding(top = 2.dp).size(Sizes.iconSm))
                Spacer(Modifier.width(Space.s2))
                MathText(a.options.getOrElse(a.correct) { step.done }, style = MaterialTheme.typography.titleSmall, color = p.text)
            }
        }
    }
}

/** Kleine Überschrift innerhalb einer Schritt-Karte. */
@Composable
private fun Part(text: String) {
    Spacer(Modifier.height(Space.s3))
    Text(text, style = MaterialTheme.typography.labelMedium, color = Funke.colors.accent)
    Spacer(Modifier.height(Space.s1))
}

/** "Zeichen kurz erklärt": jedes Rechenzeichen der Aufgabe mit einem einfachen Satz. */
@Composable
internal fun SignsCard(task: Task) {
    val p = Funke.colors
    val signs = remember(task.id, Lang.german) { signsIn(formulaTexts(task)) }
    if (signs.isEmpty()) return
    Spacer(Modifier.height(Space.s3))
    Column(Modifier.fillMaxWidth().clip(Shapes.card).background(p.track).padding(Space.s4)) {
        Text(tr("Zeichen kurz erklärt", "Signs explained"), style = MaterialTheme.typography.labelLarge, color = p.text)
        Spacer(Modifier.height(Space.s3))
        signs.forEachIndexed { i, (show, text) ->
            if (i > 0) Spacer(Modifier.height(Space.s3))
            SignRow(show, text)
        }
    }
}

/** Ein Zeichen: links die Schreibweise, rechts was sie bedeutet. Auch in der Tipp-Blase. */
@Composable
internal fun SignRow(show: String, text: String) {
    val p = Funke.colors
    Row(verticalAlignment = Alignment.Top) {
        Box(Modifier.widthIn(min = 64.dp).padding(end = Space.s3), contentAlignment = Alignment.CenterStart) {
            MathText(show, style = MaterialTheme.typography.bodyMedium, color = p.text)
        }
        Text(text, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp), color = p.textSecondary, modifier = Modifier.weight(1f))
    }
}
