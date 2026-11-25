package de.funke.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.ui.Lang
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.LocalSounds
import de.funke.app.ui.Sfx
import de.funke.app.ui.challenge.isFormulaOnly
import de.funke.app.ui.challenge.signsIn
import de.funke.app.ui.challenge.Answer
import de.funke.app.ui.challenge.Difficulty
import de.funke.app.ui.challenge.EstimateKind
import de.funke.app.ui.challenge.FormulaCard
import de.funke.app.ui.challenge.Step
import de.funke.app.ui.challenge.Subject
import de.funke.app.ui.challenge.Task
import de.funke.app.ui.challenge.Tasks
import de.funke.app.ui.components.AnimatedCheckBadge
import de.funke.app.ui.components.LipBox
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipColors
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiCue
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.MathText
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.lipColors
import de.funke.app.ui.components.lipOf
import de.funke.app.ui.components.reveal
import de.funke.app.ui.components.shakeOnError
import de.funke.app.ui.components.smoothResize
import de.funke.app.ui.components.surface
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.NumberStyle
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

/*
 * Eine Aufgabe im Stil von Brilliant (seit 10.0), gleich für Lektionen und für die Hürde beim Abbrechen:
 * oben die Frage, darunter die Schritte, unten fest der Knopf "Prüfen". Nach dem Prüfen fährt unten eine
 * Rückmeldung herein: grün mit Haken (richtig) oder weich rot (nicht ganz) mit "Nochmal" und "Lösung".
 * Dazu kurze Töne und Haptik. Tipps gibt Lumi links neben dem Knopf.
 */

/** Ab so vielen Sekunden darf man sich die Lösung zeigen lassen (oder schon nach einem Fehlversuch). */
const val REVEAL_AFTER_S = 20

/** Wofür eine Aufgabe gezeigt wird. */
enum class TaskMode {
    /** Lektion im Reiter "Aufgaben": Funken, Erklärung, weiter zur nächsten */
    Practice,

    /** Hürde beim Abbrechen: gelöst darf man gehen */
    Cancel,

    /** Denkpause vor einer App: antworten oder "Weiß ich nicht", dann weiter */
    Gate,
}

/** Wo eine Aufgabe gerade steht. */
enum class TaskPhase {
    /** Eingabe läuft */
    Answer,

    /** Ein Zwischenschritt ist richtig, gleich geht es mit dem nächsten weiter */
    StepRight,

    /** Ganz gelöst */
    Correct,

    /** Nicht ganz: Rückmeldung, dann "Nochmal" oder "Lösung" */
    Wrong,

    /** Lösung angeschaut */
    Revealed,
}

/** Funken für eine gelöste Aufgabe: schwerer und mehr Schritte geben mehr, jeder Tipp kostet einen. */
fun sparksFor(task: Task, hintsUsed: Int, revealed: Boolean): Int {
    if (revealed) return 0
    val base = if (task.master) 8 else when (task.level) {
        Difficulty.Easy -> 1
        Difficulty.Medium -> 2
        Difficulty.Hard -> 3
        Difficulty.Expert -> 5
    }
    return (base * task.steps.size - hintsUsed).coerceAtLeast(1)
}

/** Zustand einer Aufgabe: Schritt, Eingaben, Fehler, Tipps. Wird von Ansicht und Leiste geteilt. */
@Stable
class TaskRun(val task: Task) {
    var step by mutableIntStateOf(0)
        private set
    var phase by mutableStateOf(TaskPhase.Answer)
        private set
    var wrongTotal by mutableIntStateOf(0)
        private set
    var hintsUsed by mutableIntStateOf(0)
        private set
    /** Tipps im aktuellen Schritt (0 = noch keiner) */
    var hintLevel by mutableIntStateOf(0)
        private set
    var elapsed by mutableIntStateOf(0)
    var message by mutableStateOf<String?>(null)
        private set
    var sparks by mutableIntStateOf(0)
        private set
    /** zählt Fehlversuche, lässt das Eingabefeld wackeln */
    var shake by mutableIntStateOf(0)
        private set

    // Eingaben des aktuellen Schritts
    var number by mutableStateOf("")
        private set
    var choice by mutableIntStateOf(-1)
        private set
    var wrongChoices by mutableStateOf(setOf<Int>())
        private set
    var estimate by mutableFloatStateOf(0.5f)
        private set
    var estimateTouched by mutableStateOf(false)
        private set

    val current: Step? get() = task.steps.getOrNull(step)
    val multi: Boolean get() = task.steps.size > 1
    val clean: Boolean get() = wrongTotal == 0 && hintsUsed == 0
    val canReveal: Boolean get() = phase != TaskPhase.Correct && phase != TaskPhase.Revealed && (wrongTotal > 0 || elapsed >= REVEAL_AFTER_S)
    val maxHints: Int get() = current?.let { it.hints.size + if (it.formula != null) 1 else 0 } ?: 0
    val canHint: Boolean get() = (phase == TaskPhase.Answer || phase == TaskPhase.Wrong) && hintLevel < maxHints
    val finished: Boolean get() = phase == TaskPhase.Correct || phase == TaskPhase.Revealed

    val canCheck: Boolean
        get() = phase == TaskPhase.Answer && when (current?.answer) {
            is Answer.Number -> Tasks.parse(number) != null
            is Answer.Choice -> choice >= 0 && choice !in wrongChoices
            is Answer.Estimate -> estimateTouched
            null -> false
        }

    fun estimateValue(a: Answer.Estimate): Double = roundSig(exp(ln(a.min) + (ln(a.max) - ln(a.min)) * estimate), 2)

    /** Tippen nach einem Fehlversuch heißt: noch mal probieren. */
    private fun editing() {
        if (phase == TaskPhase.Wrong) {
            phase = TaskPhase.Answer
            message = null
        }
    }

    fun type(k: String) {
        if (phase != TaskPhase.Answer && phase != TaskPhase.Wrong) return
        editing()
        number = when (k) {
            "±" -> if (number.startsWith("-")) number.drop(1) else "-$number"
            "," -> if (number.contains(',')) number else (number.ifEmpty { "0" } + ",")
            else -> if (number.trimStart('-').length >= 10) number else if (number == "0") k else number + k
        }
    }

    fun enter(text: String) {
        if (phase != TaskPhase.Answer && phase != TaskPhase.Wrong) return
        editing()
        number = text
    }

    fun deleteDigit() = enter(number.dropLast(1))

    fun clearNumber() = enter("")

    fun pick(i: Int) {
        if (phase != TaskPhase.Answer && phase != TaskPhase.Wrong) return
        if (i in wrongChoices) return
        editing()
        choice = i
    }

    fun slide(f: Float) {
        if (phase != TaskPhase.Answer && phase != TaskPhase.Wrong) return
        editing()
        estimate = f
        estimateTouched = true
    }

    /** Prüft die Eingabe des aktuellen Schritts. */
    fun check() {
        val st = current ?: return
        if (!canCheck) return
        val a = st.answer
        val ok = when (a) {
            is Answer.Number -> a.accepts(Tasks.parse(number) ?: return)
            is Answer.Choice -> choice == a.correct
            is Answer.Estimate -> a.accepts(estimateValue(a))
        }
        if (ok) {
            message = null
            if (step >= task.steps.lastIndex) {
                sparks = sparksFor(task, hintsUsed, revealed = false)
                phase = TaskPhase.Correct
            } else {
                phase = TaskPhase.StepRight
            }
            return
        }
        wrongTotal++
        shake++
        message = st.wrong ?: when (a) {
            is Answer.Number -> numberFeedback(a, Tasks.parse(number) ?: 0.0, wrongTotal)
            is Answer.Choice -> null
            is Answer.Estimate -> if (estimateValue(a) > a.value) tr("Zu viel. Probier es mit weniger.", "Too much. Try less.")
            else tr("Zu wenig. Probier es mit mehr.", "Too little. Try more.")
        }
        if (a is Answer.Choice) wrongChoices = wrongChoices + choice
        phase = TaskPhase.Wrong
    }

    /** Nach "Nicht ganz": zurück zur Eingabe. Bei Auswahl wird die falsche Wahl abgewählt. */
    fun retry() {
        if (phase != TaskPhase.Wrong) return
        phase = TaskPhase.Answer
        message = null
        if (current?.answer is Answer.Choice) choice = -1
    }

    /** Zwischenschritt richtig → nächster Schritt. */
    fun nextStep() {
        if (phase != TaskPhase.StepRight) return
        step++
        number = ""
        choice = -1
        wrongChoices = emptySet()
        estimate = 0.5f
        estimateTouched = false
        hintLevel = 0
        message = null
        phase = TaskPhase.Answer
    }

    fun hint() {
        if (!canHint) return
        hintLevel++
        hintsUsed++
    }

    fun reveal() {
        if (finished) return
        message = null
        phase = TaskPhase.Revealed
    }
}

// ---------------------------------------------------------------------------------------------
// Rückmeldung: Töne und Haptik bei jedem Wechsel
// ---------------------------------------------------------------------------------------------

/**
 * Spielt bei jedem Wechsel der Phase den passenden Ton und die Haptik und schaltet nach einem
 * richtigen Zwischenschritt nach kurzer Pause weiter. Zählt die Sekunden für "Lösung".
 */
@Composable
fun TaskEffects(run: TaskRun) {
    val sounds = LocalSounds.current
    val haptics = LocalHaptics.current
    val still = reducedMotion
    LaunchedEffect(run) {
        while (run.elapsed < REVEAL_AFTER_S) {
            delay(1000)
            run.elapsed++
        }
    }
    LaunchedEffect(run, run.phase, run.step) {
        when (run.phase) {
            TaskPhase.StepRight -> {
                sounds?.play(Sfx.Tap)
                haptics?.click()
                delay(if (still) 300 else 650)
                run.nextStep()
            }
            TaskPhase.Correct -> {
                sounds?.play(Sfx.Correct)
                haptics?.success()
            }
            TaskPhase.Wrong -> {
                sounds?.play(Sfx.Wrong)
                haptics?.error()
            }
            else -> Unit
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Inhalt: Frage, Gegebenes, Schritte, Eingabe
// ---------------------------------------------------------------------------------------------

private enum class RowState { Done, Active, Locked }

/** Allgemeine Fragen erzeugter Aufgaben: die stehen nicht extra über der Eingabe. */
private fun isGenericPrompt(s: String) = s == tr("Welche Antwort stimmt?", "Which answer is right?") || s == tr("Deine Antwort", "Your answer")

/**
 * Teilt den Aufgabentext in Rahmen und eigentliche Frage: Endet der Text mit einer Frage, die ein eigener
 * Satz ist, steht sie danach groß für sich ("Wie lange wartet man auf die Antwort?"). So sieht man auf einen
 * Blick, was zu tun ist (Signaling; Mayer, Multimedia Learning). Sonst bleibt der Text, wie er ist.
 */
fun splitAsk(text: String): Pair<String, String?> {
    val t = text.trim()
    if (!t.endsWith("?")) return t to null
    val ends = Regex("""[.!…:](\s+)(?=[A-ZÄÖÜ„"«(])""").findAll(t).toList()
    val last = ends.lastOrNull() ?: return t to null
    val cut = last.range.first + 1
    val context = t.substring(0, cut).trim()
    val ask = t.substring(cut).trim()
    // Formeln (§…§) nicht zerschneiden, und die Frage braucht etwas Rahmen davor
    if (context.length < 12 || ask.length < 6 || context.count { it == '§' } % 2 != 0) return t to null
    return context to ask
}

@Composable
fun TaskContent(run: TaskRun, mode: TaskMode, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val task = run.task
    val single = task.steps.size == 1
    val firstPrompt = task.steps.first().prompt
    val parts = remember(task.id, task.question) { splitAsk(task.question) }
    val context = parts.first
    val ask = parts.second
    // Einschrittig und die Frage steht nur in der Eingabe-Zeile ("Von wem stammt dieser Gedanke?"): dann ist sie die Überschrift
    val promptAsk = single && ask == null && !isGenericPrompt(firstPrompt) && firstPrompt.trimEnd().endsWith("?")
    val headline = ask ?: if (promptAsk) firstPrompt else null
    // Ohne abgetrennte Frage: Ist der ganze Text selbst eine kurze Frage, steht er groß und fett da
    val contextIsAsk = headline == null && context.endsWith("?") && context.length <= 220
    Column(modifier.fillMaxWidth()) {
        if (task.master) {
            MasterTag()
            Spacer(Modifier.height(Space.s3))
        }
        // Die Aufgabe als Karte: oben ruhig der Rahmen mit den Angaben, unten groß und fett die Frage,
        // direkt über der Antwort. So sieht man sofort, was zu tun ist.
        Column(
            Modifier
                .fillMaxWidth()
                .surface(Shapes.large, Elevation.Level1)
                .padding(Space.s5),
        ) {
            if (context.isNotBlank()) {
                MathText(
                    context,
                    style = if (contextIsAsk) AskStyle else ContextStyle,
                    color = if (headline != null) p.textSecondary else p.text,
                    emphasis = p.accent,
                )
            }
            if (task.given.isNotEmpty()) {
                Spacer(Modifier.height(Space.s4))
                GivenBlock(task.given)
            }
            if (headline != null) {
                if (context.isNotBlank() || task.given.isNotEmpty()) Spacer(Modifier.height(Space.s4))
                MathText(headline, style = AskStyle, color = p.text, emphasis = p.accent)
            }
        }
        Spacer(Modifier.height(Space.s5))
        val revealed = run.phase == TaskPhase.Revealed
        task.steps.forEachIndexed { i, step ->
            val state = when {
                revealed || i < run.step -> RowState.Done
                i == run.step -> RowState.Active
                else -> RowState.Locked
            }
            key(i) {
                val still = reducedMotion
                AnimatedContent(
                    targetState = state,
                    transitionSpec = {
                        if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
                        else (fadeIn(tween(Motion.Base, Motion.Fast / 2, Motion.EaseOut)) + slideInVertically(tween(Motion.Medium, easing = Motion.EaseOut)) { it / 10 }) togetherWith
                            fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
                    },
                    label = "stepRow$i",
                ) { st ->
                    when (st) {
                        RowState.Done -> DoneRow(i, step, run.multi, quiet = revealed && i >= run.step)
                        RowState.Locked -> LockedRow(i, step)
                        RowState.Active -> ActiveStep(run, i, step, mode, promptShown = promptAsk)
                    }
                }
            }
            if (i < task.steps.lastIndex) Spacer(Modifier.height(Space.s3))
        }
    }
}

@Composable
private fun MasterTag() {
    val p = Funke.colors
    Row(
        Modifier
            .clip(Shapes.pill)
            .background(p.text)
            .padding(horizontal = Space.s3, vertical = Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(R.drawable.ic_trophy), contentDescription = null, tint = p.bg, modifier = Modifier.size(Sizes.iconSm))
        Spacer(Modifier.width(Space.s1))
        Text(tr("Meisterklasse", "Masterclass"), style = MaterialTheme.typography.labelMedium, color = p.bg)
    }
}

/** Gegeben: kleine Marken, die man auf einen Blick überfliegt. */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun GivenBlock(lines: List<String>) {
    val p = Funke.colors
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Space.s2),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        lines.forEach {
            Box(
                Modifier
                    .clip(Shapes.pill)
                    .background(p.track)
                    .padding(horizontal = Space.s3, vertical = 6.dp),
            ) {
                MathText(it, style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
            }
        }
    }
}

/** Rahmen der Aufgabe: gut lesbarer Fließtext (17 sp, 1,5-facher Zeilenabstand). */
private val ContextStyle: androidx.compose.ui.text.TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 26.sp)

/**
 * Die eigentliche Frage: größer und kräftiger, damit man sofort weiß, worum es geht. Seit 15.0 halbfett im
 * Display-Schnitt von Inter (für große Größen gezeichnet) statt fett – klar, aber ruhig. Im Cartoon-Stil bleibt sie fett.
 */
private val AskStyle: androidx.compose.ui.text.TextStyle
    @Composable get() = MaterialTheme.typography.titleLarge.copy(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        // Schrift für Aufgaben (seit 15.2 aus dem Look): hat alle Mathe-Zeichen, auch im Cartoon-Stil
        fontFamily = Funke.fonts.question,
        fontWeight = if (Funke.cartoon) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
    )

/** Was in das Feld gehört ("Wartezeit in Sekunden"): kleine Zeile direkt über der Eingabe. */
@Composable
private fun FieldLabel(prompt: String) {
    val p = Funke.colors
    MathText(prompt, style = MaterialTheme.typography.labelLarge, color = p.textSecondary, modifier = Modifier.padding(start = Space.s1))
}

/** Kreis mit Nummer oder Haken. */
@Composable
private fun StepBadge(index: Int, state: RowState, quiet: Boolean = false) {
    val p = Funke.colors
    val bg = when (state) {
        RowState.Done -> if (quiet) p.track else p.success.copy(alpha = 0.16f)
        RowState.Active -> p.text
        RowState.Locked -> p.track
    }
    Box(Modifier.size(24.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
        when (state) {
            RowState.Done -> Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = if (quiet) p.textTertiary else p.success, modifier = Modifier.size(14.dp))
            RowState.Active -> Text("${index + 1}", style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.bg)
            RowState.Locked -> Text("${index + 1}", style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.textTertiary)
        }
    }
}

@Composable
private fun DoneRow(index: Int, step: Step, multi: Boolean, quiet: Boolean) {
    val p = Funke.colors
    Row(Modifier.fillMaxWidth().padding(vertical = Space.s1), verticalAlignment = Alignment.CenterVertically) {
        StepBadge(index, RowState.Done, quiet)
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            if (multi) MathText(step.prompt, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
            MathText(step.done, style = MaterialTheme.typography.titleSmall, color = p.text)
        }
    }
}

@Composable
private fun LockedRow(index: Int, step: Step) {
    val p = Funke.colors
    Row(Modifier.fillMaxWidth().padding(vertical = Space.s1), verticalAlignment = Alignment.CenterVertically) {
        StepBadge(index, RowState.Locked)
        Spacer(Modifier.width(Space.s3))
        MathText(step.prompt, style = MaterialTheme.typography.bodyMedium, color = p.textTertiary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ActiveStep(run: TaskRun, index: Int, step: Step, mode: TaskMode, promptShown: Boolean = false) {
    val p = Funke.colors
    Column(Modifier.fillMaxWidth()) {
        if (run.multi) {
            Row(verticalAlignment = Alignment.Top) {
                StepBadge(index, RowState.Active)
                Spacer(Modifier.width(Space.s3))
                MathText(step.prompt, style = MaterialTheme.typography.titleMedium, color = p.text, modifier = Modifier.weight(1f).padding(top = 1.dp))
            }
            Spacer(Modifier.height(Space.s4))
        } else if (!isGenericPrompt(step.prompt) && !promptShown && step.answer !is Answer.Number) {
            FieldLabel(step.prompt)
            Spacer(Modifier.height(Space.s2))
        }
        // Bei Zahlen steht, was hinein gehört ("Masse in kg"), im Feld selbst
        val label = step.prompt.takeIf { !run.multi && !isGenericPrompt(it) && !promptShown }
        when (val a = step.answer) {
            is Answer.Number -> NumberEntry(run, a, label, scientific = mode == TaskMode.Practice && run.task.level != Difficulty.Easy)
            is Answer.Choice -> ChoiceEntry(run, a, seed = run.task.id.hashCode() * 31 + index)
            is Answer.Estimate -> EstimateEntry(run, a)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Antwortarten
// ---------------------------------------------------------------------------------------------

/** Rahmenfarbe eines Antwortfelds je nach Phase. */
@Composable
private fun phaseOutline(run: TaskRun): Color? {
    val p = Funke.colors
    return when (run.phase) {
        TaskPhase.Correct, TaskPhase.StepRight -> p.success
        TaskPhase.Wrong -> p.danger
        else -> null
    }
}

/** Zahl: Anzeige mit Einheit, darunter Ziffern als 3D-Tasten. Der Rechner liegt hinter dem Symbol rechts. */
@Composable
private fun NumberEntry(run: TaskRun, a: Answer.Number, label: String?, scientific: Boolean) {
    var calc by remember { mutableStateOf(false) }
    NumberField(run, a.unit, label, calc, onCalc = { calc = !calc })
    Spacer(Modifier.height(Space.s3))
    NumberPad(onKey = run::type)
    // Rechner (seit 13.4 der große wissenschaftliche, als Fenster über allem); "Übernehmen" füllt das Antwortfeld
    if (calc) de.funke.app.ui.calc.CalculatorWindow(
        onDismiss = { calc = false },
        onUse = { v -> run.enter(SciCalc.toInput(v)) },
    )
}

/**
 * Antwortfeld für Zahlen. Oben klein, was hinein gehört ("Masse in kg"), darunter die Zahl mit blinkender
 * Schreibmarke und der Einheit. Ohne Beschriftung und Einheit steht "Deine Antwort" als Platzhalter da.
 * Die Zeile der Zahl hat eine feste Höhe, damit nichts springt, wenn die erste Ziffer kommt.
 * Rechts: Löschen und der Rechner.
 */
@Composable
private fun NumberField(run: TaskRun, unit: String, label: String?, calc: Boolean, onCalc: () -> Unit) {
    val p = Funke.colors
    val shown = run.number.replace("-", "−").let { if (Lang.german) it else it.replace(',', '.') }
    val hasValue = shown.isNotEmpty()
    val outline = phaseOutline(run)
    val ring by animateColorAsState(outline ?: Color.Transparent, Motion.fade(Motion.Base), label = "numRing")
    val tint by animateColorAsState(
        when (run.phase) {
            TaskPhase.Correct, TaskPhase.StepRight -> p.success
            TaskPhase.Wrong -> p.danger
            else -> p.text
        },
        Motion.fade(Motion.Base),
        label = "numTint",
    )
    // Schreibmarke: blinkt im Takt einer Textmarke und bleibt beim Tippen kurz stehen
    val typing = run.phase == TaskPhase.Answer
    var caretOn by remember { mutableStateOf(true) }
    LaunchedEffect(run.number, typing) {
        caretOn = true
        if (!typing) return@LaunchedEffect
        while (true) {
            delay(530)
            caretOn = !caretOn
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .shakeOnError(run.shake)
            .surface(Shapes.lg, Elevation.Level1)
            .drawBehind {
                if (ring.alpha > 0.01f) {
                    val w = 2.dp.toPx()
                    drawRoundRect(ring, topLeft = Offset(w / 2, w / 2), size = Size(size.width - w, size.height - w), cornerRadius = CornerRadius(20.dp.toPx() - w / 2), style = androidx.compose.ui.graphics.drawscope.Stroke(w))
                }
            }
            .padding(start = Space.s5, end = Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(vertical = Space.s3)) {
            if (label != null) {
                MathText(label, style = MaterialTheme.typography.labelLarge, color = p.textSecondary)
                Spacer(Modifier.height(2.dp))
            }
            Row(Modifier.height(40.dp), verticalAlignment = Alignment.CenterVertically) {
                if (hasValue) Text(shown, style = NumberStyle, color = tint, maxLines = 1)
                if (typing) {
                    Box(
                        Modifier
                            .padding(start = if (hasValue) 2.dp else 0.dp)
                            .size(width = 2.dp, height = 28.dp)
                            .graphicsLayer { alpha = if (caretOn) 1f else 0f }
                            .background(p.accent, Shapes.pill),
                    )
                }
                if (!hasValue && label == null && unit.isEmpty()) {
                    Text(
                        tr("Deine Antwort", "Your answer"),
                        style = MaterialTheme.typography.titleMedium,
                        color = p.textTertiary,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
                if (unit.isNotEmpty()) {
                    Spacer(Modifier.width(if (hasValue) Space.s2 else 6.dp))
                    MathText(unit, style = MaterialTheme.typography.titleMedium, color = if (hasValue) p.textSecondary else p.textTertiary)
                }
            }
        }
        if (run.number.isNotEmpty() && !run.finished) {
            Box(
                Modifier
                    .size(Sizes.touch)
                    .pointerInput(Unit) { detectTapGestures(onTap = { run.deleteDigit() }, onLongPress = { run.clearNumber() }) }
                    .semantics { contentDescription = tr("Löschen (lang drücken: alles)", "Delete (long press: all)") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.ic_backspace), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.iconLg))
            }
        }
        if (!run.finished) {
            Box(
                Modifier
                    .size(Sizes.touch)
                    .bouncyClickable(shape = CircleShape, pressedScale = 0.9f, onClick = onCalc)
                    .semantics { contentDescription = tr("Rechner", "Calculator") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.ic_calculator), contentDescription = null, tint = if (calc) p.accent else p.textSecondary, modifier = Modifier.size(Sizes.icon))
            }
        }
    }
}

@Composable
private fun NumberPad(onKey: (String) -> Unit) {
    val sep = if (Lang.german) "," else "."
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("±", "0", sep))
    Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                row.forEach { k -> PadKey(k, Modifier.weight(1f), big = k.first().isDigit()) { onKey(if (k == ".") "," else k) } }
            }
        }
    }
}

/** Taste als 3D-Fläche: sinkt beim Drücken auf ihre Kante. */
@Composable
private fun PadKey(label: String, modifier: Modifier = Modifier, big: Boolean = true, accent: Boolean = false, height: androidx.compose.ui.unit.Dp = 50.dp, onClick: () -> Unit) {
    val p = Funke.colors
    val base = p.lipColors(LipTone.Neutral)
    val colors = if (accent) base.copy(face = lerp(base.face, p.accent, if (p.isDark) 0.16f else 0.10f)) else base
    LipBox(
        colors = colors,
        modifier = modifier
            .height(height)
            .semantics {
                contentDescription = when (label) {
                    "±" -> tr("Vorzeichen", "Sign"); "⌫" -> tr("Löschen", "Delete"); "√" -> tr("Wurzel", "Square root"); "^" -> tr("hoch", "power"); else -> label
                }
            },
        shape = Shapes.sm,
        onClick = onClick,
        lip = 3.dp,
        soft = true,
    ) {
        Text(
            label,
            style = if (big) MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum, ss03") else MaterialTheme.typography.titleMedium,
            color = if (big) p.text else p.textSecondary,
        )
    }
}

/** Auswahl: Antippen wählt (Akzent-Rand), "Prüfen" entscheidet. Falsche Wahl wird rot und bleibt gesperrt. */
@Composable
private fun ChoiceEntry(run: TaskRun, a: Answer.Choice, seed: Int) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val order = remember(seed) { a.options.indices.shuffled(Random(seed)) }
    Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
        order.forEach { i ->
            val selected = run.choice == i
            val struck = i in run.wrongChoices
            val state = when {
                run.phase == TaskPhase.Revealed && i == a.correct -> OptionState.Right
                (run.phase == TaskPhase.Correct || run.phase == TaskPhase.StepRight) && selected -> OptionState.Right
                run.phase == TaskPhase.Wrong && selected -> OptionState.Wrong
                struck -> OptionState.Struck
                selected -> OptionState.Selected
                else -> OptionState.Idle
            }
            val c = optionColors(state)
            LipBox(
                colors = c,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
                    .then(if (state == OptionState.Wrong) Modifier.shakeOnError(run.shake) else Modifier),
                shape = Shapes.md,
                onClick = {
                    haptics?.tick()
                    run.pick(i)
                },
                enabled = !struck && !run.finished && run.phase != TaskPhase.StepRight,
                role = Role.RadioButton,
                borderWidth = 2.dp,
                haptic = false,
                lip = if (state == OptionState.Struck) 0.dp else 4.dp,
                contentAlignment = Alignment.CenterStart,
                // Cartoon: unberührte Antworten mit leichter Kontur, die gewählte kräftig umrandet
                soft = state == OptionState.Idle || state == OptionState.Struck,
            ) {
                Row(Modifier.padding(horizontal = Space.s4, vertical = Space.s3), verticalAlignment = Alignment.CenterVertically) {
                    MathText(a.options[i], style = MaterialTheme.typography.bodyLarge, color = c.content, modifier = Modifier.weight(1f))
                    AnimatedVisibility(
                        visible = state == OptionState.Right || state == OptionState.Wrong,
                        enter = scaleIn(tween(Motion.Base, easing = Motion.Bob), initialScale = 0.4f) + fadeIn(tween(Motion.Fast)),
                        exit = fadeOut(tween(Motion.Fast)),
                    ) {
                        Icon(
                            painterResource(if (state == OptionState.Wrong) R.drawable.ic_x else R.drawable.ic_check),
                            contentDescription = null,
                            tint = c.content,
                            modifier = Modifier.padding(start = Space.s3).size(Sizes.icon),
                        )
                    }
                }
            }
        }
    }
}

private enum class OptionState { Idle, Selected, Right, Wrong, Struck }

@Composable
private fun optionColors(state: OptionState): LipColors {
    val p = Funke.colors
    val n = p.lipColors(LipTone.Neutral)
    val strength = if (Funke.cartoon) (if (p.isDark) 0.30f else 0.22f) else (if (p.isDark) 0.14f else 0.08f)
    fun tinted(c: Color) = LipColors(
        face = lerp(n.face, c, strength),
        lip = lipOf(c, if (p.isDark) 0.25f else 0.05f).copy(alpha = 0.9f),
        border = c,
        content = if (c == p.accent) p.text else c,
    )
    return when (state) {
        OptionState.Idle -> n.copy(border = n.border ?: p.glassAlpha(0.06f))
        OptionState.Selected -> tinted(p.accent)
        OptionState.Right -> tinted(p.success)
        OptionState.Wrong -> tinted(p.danger)
        OptionState.Struck -> LipColors(p.raised(Elevation.Level0), Color.Transparent, null, p.textTertiary)
    }
}

/** Schätzung: logarithmischer Regler, Wert groß darüber, Grenzen darunter. */
@Composable
private fun EstimateEntry(run: TaskRun, a: Answer.Estimate) {
    val p = Funke.colors
    val value = run.estimateValue(a)
    val tint by animateColorAsState(
        when (run.phase) {
            TaskPhase.Correct, TaskPhase.StepRight -> p.success
            TaskPhase.Wrong -> p.danger
            else -> if (run.estimateTouched) p.text else p.textTertiary
        },
        Motion.fade(Motion.Base),
        label = "estTint",
    )
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().heightIn(min = 56.dp).shakeOnError(run.shake), contentAlignment = Alignment.Center) {
            MathText(formatEstimate(value, a.kind), style = MaterialTheme.typography.headlineMedium.copy(fontFeatureSettings = "tnum, ss03"), color = tint)
        }
        Spacer(Modifier.height(Space.s4))
        LogSlider(run.estimate, onChange = run::slide, enabled = !run.finished && run.phase != TaskPhase.StepRight)
        Spacer(Modifier.height(Space.s2))
        Row(Modifier.fillMaxWidth()) {
            MathText(formatEstimate(a.min, a.kind), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
            Spacer(Modifier.weight(1f))
            MathText(formatEstimate(a.max, a.kind), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
        }
    }
}

/** Regler 0…1: Spur, Füllung, Daumen mit leichtem Schatten. Ziehen und Tippen. */
@Composable
private fun LogSlider(fraction: Float, onChange: (Float) -> Unit, enabled: Boolean) {
    val p = Funke.colors
    val density = LocalDensity.current
    var dragging by remember { mutableStateOf(false) }
    val thumbScale by animateFloatAsState(if (dragging) 1.12f else 1f, Motion.spec(Motion.Fast, Motion.EaseOut), label = "thumb")
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(Sizes.touch)
            .semantics { contentDescription = tr("Schätzregler", "Estimate slider") }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures { o -> onChange((o.x / size.width).coerceIn(0f, 1f)) }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectHorizontalDragGestures(
                    onDragStart = { o -> dragging = true; onChange((o.x / size.width).coerceIn(0f, 1f)) },
                    onDragEnd = { dragging = false },
                    onDragCancel = { dragging = false },
                ) { change, _ -> onChange((change.position.x / size.width).coerceIn(0f, 1f)) }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        val thumb = 28.dp
        val trackW = maxWidth
        Box(Modifier.fillMaxWidth().height(8.dp).clip(Shapes.pill).background(p.track))
        Box(Modifier.width(trackW * fraction).height(8.dp).clip(Shapes.pill).background(p.accent))
        val x = with(density) { ((trackW - thumb) * fraction).roundToPx() }
        Box(
            Modifier
                .offset { IntOffset(x, 0) }
                .size(thumb)
                .graphicsLayer { scaleX = thumbScale; scaleY = thumbScale }
                .surface(CircleShape, Elevation.Level2)
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Leiste unten: Prüfen, Rückmeldung, Lumi als Helfer
// ---------------------------------------------------------------------------------------------

/**
 * Feste Leiste unten. Beim Eingeben: Lumi (Tipps) und "Prüfen". Nach dem Prüfen fährt die Rückmeldung
 * von unten herein (200 ms, Ease-out): grün mit Haken, weich rot mit "Nochmal", oder die Lösung.
 */
@Composable
fun TaskDock(
    run: TaskRun,
    mode: TaskMode,
    onContinue: () -> Unit,
    onExplain: () -> Unit,
    onHelper: () -> Unit,
    modifier: Modifier = Modifier,
    continueLabel: String = tr("Weiter", "Continue"),
) {
    val p = Funke.colors
    val still = reducedMotion
    val kind = when (run.phase) {
        TaskPhase.Answer, TaskPhase.StepRight -> 0
        TaskPhase.Correct -> 1
        TaskPhase.Wrong -> 2
        TaskPhase.Revealed -> 3
    }
    AnimatedContent(
        targetState = kind,
        transitionSpec = {
            if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
            else if (targetState == 0) fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) togetherWith (fadeOut(tween(Motion.Fast)) + slideOutVertically(tween(Motion.Base, easing = Motion.EaseOut)) { it / 2 })
            else (slideInVertically(tween(Motion.Base, easing = Motion.EaseOut)) { it } + fadeIn(tween(Motion.Fast))) togetherWith fadeOut(tween(Motion.Fast))
        },
        label = "dock",
        modifier = modifier.fillMaxWidth(),
    ) { k ->
        when (k) {
            0 -> AnswerBar(run, mode, onHelper)
            1 -> FeedbackPanel(
                tint = p.success,
                icon = { AnimatedCheckBadge(size = 32.dp) },
                title = if (mode == TaskMode.Cancel) tr("Richtig! Du darfst gehen.", "Correct! You're free to go.")
                else remember { listOf(tr("Richtig!", "Correct!"), tr("Stark!", "Nice!"), tr("Genau so!", "Exactly!")).random() },
                trailing = if (mode == TaskMode.Practice && run.sparks > 0) ({ SparkChip(run.sparks) }) else null,
                message = null,
            ) {
                if (mode == TaskMode.Practice) {
                    LipButton(tr("Erklärung", "Explain"), onClick = onExplain, tone = LipTone.Neutral, modifier = Modifier.widthIn(min = 120.dp))
                    Spacer(Modifier.width(Space.s3))
                    LipButton(continueLabel, onClick = onContinue, tone = LipTone.Success, modifier = Modifier.weight(1f))
                }
            }
            2 -> FeedbackPanel(
                tint = p.danger,
                icon = { WrongBadge() },
                title = tr("Nicht ganz", "Not quite"),
                trailing = null,
                message = run.message,
            ) {
                LipButton(tr("Lösung", "Solution"), onClick = run::reveal, tone = LipTone.Neutral, modifier = Modifier.widthIn(min = 120.dp))
                Spacer(Modifier.width(Space.s3))
                LipButton(tr("Nochmal", "Try again"), onClick = run::retry, tone = LipTone.Danger, modifier = Modifier.weight(1f))
            }
            else -> FeedbackPanel(
                tint = p.textSecondary,
                icon = {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(p.track), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_bulb), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.iconSm))
                    }
                },
                title = tr("Lösung angeschaut", "Solution shown"),
                trailing = null,
                message = null,
            ) {
                LipButton(tr("Erklärung", "Explain"), onClick = onExplain, tone = LipTone.Neutral, modifier = Modifier.widthIn(min = 120.dp))
                Spacer(Modifier.width(Space.s3))
                LipButton(
                    if (mode == TaskMode.Cancel) tr("Neue Aufgabe", "New task") else continueLabel,
                    onClick = onContinue,
                    tone = LipTone.Accent,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Beim Eingeben: Lumi links (Tipps), daneben "Prüfen". Nach einem richtigen Zwischenschritt wird der Knopf kurz grün. */
@Composable
private fun AnswerBar(run: TaskRun, mode: TaskMode, onHelper: () -> Unit) {
    val p = Funke.colors
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(p.bg)
                .navigationBarsPadding()
                .padding(start = Space.page, end = Space.page, top = Space.s3, bottom = Space.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (mode == TaskMode.Gate) {
                // Denkpause: ohne Tipps, dafür jederzeit "Weiß ich nicht" (zeigt die Lösung, dann geht es weiter)
                LipButton(tr("Weiß ich nicht", "I don't know"), onClick = run::reveal, tone = LipTone.Neutral, modifier = Modifier.weight(1f))
            } else {
                HelperButton(run, onHelper)
            }
            Spacer(Modifier.width(Space.s4))
            val stepRight = run.phase == TaskPhase.StepRight
            LipButton(
                if (stepRight) tr("Richtig", "Correct") else tr("Prüfen", "Check"),
                onClick = run::check,
                tone = if (stepRight) LipTone.Success else LipTone.Accent,
                enabled = run.canCheck || stepRight,
                icon = if (stepRight) R.drawable.ic_check else null,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Tipp-Knopf unten links: Lumi mit Doktorhut steht frei neben "Prüfen" (ohne Kasten), oben rechts eine kleine
 * Blase mit "?": Hier gibt es Hilfe. Sobald es auch die Lösung gibt, hüpft die Blase einmal kurz.
 * Ohne Tipps und ohne Lösung ist Lumi blass und nicht antippbar.
 */
@Composable
private fun HelperButton(run: TaskRun, onClick: () -> Unit) {
    val p = Funke.colors
    val active = run.canHint || run.canReveal
    Box(
        Modifier
            .size(56.dp)
            .bouncyClickable(enabled = active, pressedScale = 0.92f, onClick = onClick)
            .semantics { contentDescription = tr("Tipp von Lumi", "Hint from Lumi") },
        contentAlignment = Alignment.Center,
    ) {
        Lumi2D(
            pose = LumiPose.Idle,
            modifier = Modifier.requiredSize(68.dp).offset(y = 2.dp),
            tappable = false,
            hat = true,
            hatTassel = p.accent,
        )
        AnimatedVisibility(
            visible = active,
            enter = scaleIn(tween(Motion.Base, easing = Motion.Bob), initialScale = 0.4f) + fadeIn(tween(Motion.Fast)),
            exit = scaleOut(tween(Motion.Fast, easing = Motion.EaseOut), targetScale = 0.6f) + fadeOut(tween(Motion.Fast)),
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp),
        ) {
            AskBadge(nudge = run.canReveal && run.phase == TaskPhase.Answer)
        }
    }
}

/** Kleine Blase mit "?" an Lumi. [nudge]: hüpft einmal (Ease-out hoch, weich zurück), wenn es jetzt auch die Lösung gibt. */
@Composable
private fun AskBadge(nudge: Boolean) {
    val p = Funke.colors
    val still = reducedMotion
    val hop = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(nudge) {
        if (nudge && !still) {
            hop.animateTo(1f, tween(160, easing = Motion.EaseOut))
            hop.animateTo(0f, tween(280, easing = Motion.EaseOut))
        }
    }
    Box(
        Modifier
            .graphicsLayer {
                translationY = -6.dp.toPx() * hop.value
                val k = 1f + 0.15f * hop.value
                scaleX = k
                scaleY = k
            }
            .size(20.dp)
            .clip(CircleShape)
            .background(p.accent)
            .then(if (Funke.cartoon) Modifier.border(1.5.dp, p.ink, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "?",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            color = p.onAccent,
        )
    }
}

/** Rückmeldung: getönte Fläche, Symbol, Titel, optional Text, darunter die Knöpfe. */
@Composable
private fun FeedbackPanel(
    tint: Color,
    icon: @Composable () -> Unit,
    title: String,
    trailing: (@Composable () -> Unit)?,
    message: String?,
    buttons: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    val p = Funke.colors
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(p.bg)
            .background(tint.copy(alpha = if (p.isDark) 0.14f else 0.10f))
            .navigationBarsPadding()
            .padding(start = Space.page, end = Space.page, top = Space.s5, bottom = Space.s4),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(Modifier.width(Space.s3))
            Text(title, style = MaterialTheme.typography.titleLarge, color = tint, modifier = Modifier.weight(1f))
            trailing?.invoke()
        }
        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(Space.s2))
            MathText(message, style = MaterialTheme.typography.bodyMedium, color = p.textSecondary, modifier = Modifier.padding(start = 44.dp))
        }
        Spacer(Modifier.height(Space.s4))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, content = buttons)
    }
}

/** Weiches Kreuz im Kreis, zeichnet sich in einem Zug (wie der Haken). */
@Composable
private fun WrongBadge() {
    val p = Funke.colors
    val still = reducedMotion
    val t = remember { androidx.compose.animation.core.Animatable(if (still) 1f else 0f) }
    LaunchedEffect(Unit) { t.animateTo(1f, tween(Motion.Slow, easing = Motion.EaseOut)) }
    Box(
        Modifier
            .size(32.dp)
            .graphicsLayer {
                val k = t.value
                scaleX = 0.6f + 0.4f * k
                scaleY = 0.6f + 0.4f * k
                alpha = k
            }
            .clip(CircleShape)
            .background(p.danger),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(12.dp)) {
            val w = size.width
            val k = ((t.value - 0.2f) / 0.8f).coerceIn(0f, 1f)
            val on = if (p.isDark) p.bg else Color.White
            val a = (k * 2f).coerceAtMost(1f)
            val b = ((k - 0.5f) * 2f).coerceIn(0f, 1f)
            drawLine(on, Offset(0f, 0f), Offset(w * a, w * a), strokeWidth = w * 0.2f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            if (b > 0f) drawLine(on, Offset(w, 0f), Offset(w - w * b, w * b), strokeWidth = w * 0.2f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
    }
}

/** Höhe des Zipfels an der Tipp-Blase. */
private val BubbleTail = 10.dp

/**
 * Sprechblase mit Zipfel unten links, der genau auf Lumi zeigt ([tailCenter] vom linken Rand).
 * Ein einziger, geschlossener Umriss: So sitzen Kontur (Cartoon) und Schatten auch am Zipfel richtig.
 */
private class SpeechBubbleShape(private val radius: androidx.compose.ui.unit.Dp, private val tailCenter: androidx.compose.ui.unit.Dp) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(size: Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): androidx.compose.ui.graphics.Outline =
        with(density) {
            val th = BubbleTail.toPx()
            val tw = 22.dp.toPx()
            val w = size.width
            val h = size.height - th
            val r = radius.toPx().coerceAtMost(h / 2f)
            val rb = minOf(r, 12.dp.toPx()) // kleine Ecke unten links, damit der Zipfel Platz hat
            val cx = tailCenter.toPx().coerceIn(rb + tw * 0.5f, w - r - tw * 0.6f)
            val path = Path().apply {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                arcTo(androidx.compose.ui.geometry.Rect(w - 2 * r, 0f, w, 2 * r), -90f, 90f, false)
                lineTo(w, h - r)
                arcTo(androidx.compose.ui.geometry.Rect(w - 2 * r, h - 2 * r, w, h), 0f, 90f, false)
                lineTo(cx + tw * 0.55f, h)
                quadraticBezierTo(cx + tw * 0.12f, h + th * 0.35f, cx, h + th)
                quadraticBezierTo(cx - tw * 0.14f, h + th * 0.45f, cx - tw * 0.45f, h)
                lineTo(rb, h)
                arcTo(androidx.compose.ui.geometry.Rect(0f, h - 2 * rb, 2 * rb, h), 90f, 90f, false)
                lineTo(0f, r)
                arcTo(androidx.compose.ui.geometry.Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
                close()
            }
            androidx.compose.ui.graphics.Outline.Generic(path)
        }
}

/**
 * Lumis Tipp-Blase über der Leiste, mit dem Zipfel auf Lumi. Oben steht, was gerade zu sehen ist, darunter die
 * Tipps als Leiter (Kernidee, Ansatz, zuletzt die Formel). Eine Hauptaktion (nächster Tipp) und leise darunter
 * die Lösung, mit Countdown, bis sie geht. Wächst aus Lumis Ecke (200 ms, Ease-out), schließt schneller.
 * Der Grund ist deckend, damit die Aufgabe dahinter (auch im Glass-Stil) nicht durchscheint.
 */
@Composable
fun HintBubble(run: TaskRun, visible: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val still = reducedMotion
    val origin = androidx.compose.ui.graphics.TransformOrigin(0.1f, 1f)
    AnimatedVisibility(
        visible = visible,
        enter = if (still) fadeIn(tween(Motion.Fast)) else fadeIn(tween(Motion.Fast, easing = Motion.EaseOut)) +
            scaleIn(tween(Motion.Base, easing = Motion.EaseOut), initialScale = 0.92f, transformOrigin = origin),
        exit = if (still) fadeOut(tween(Motion.Fast)) else fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)) +
            scaleOut(tween(Motion.Fast, easing = Motion.EaseOut), targetScale = 0.96f, transformOrigin = origin),
        modifier = modifier,
    ) {
        val step = run.current
        val shape = remember { SpeechBubbleShape(de.funke.app.ui.theme.Radius.xl, tailCenter = 28.dp) }
        Column(
            Modifier
                .padding(horizontal = Space.page)
                .fillMaxWidth()
                .background(p.bg, shape)
                .surface(shape, Elevation.Level3)
                // erst nach der Fläche: animateContentSize schneidet ab, davor träfe es Schatten und Kontur
                .smoothResize()
                .padding(start = Space.s5, end = Space.s2, top = Space.s2, bottom = Space.s3 + BubbleTail),
        ) {
            if (step == null) return@Column
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    when {
                        run.hintLevel == 0 -> tr("Brauchst du einen Tipp?", "Need a hint?")
                        run.hintLevel > step.hints.size -> tr("Die Formel dazu", "The formula")
                        else -> tr("Tipp ${run.hintLevel} von ${step.hints.size}", "Hint ${run.hintLevel} of ${step.hints.size}")
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"),
                    color = p.text,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    Modifier.size(Sizes.touch).clip(CircleShape).clickable(onClick = onDismiss).semantics { contentDescription = tr("Schließen", "Close") },
                    contentAlignment = Alignment.Center,
                ) { Icon(painterResource(R.drawable.ic_x), contentDescription = null, tint = p.textTertiary, modifier = Modifier.size(Sizes.iconSm)) }
            }
            Column(Modifier.padding(end = Space.s3)) {
                if (run.hintLevel > 0) {
                    step.hints.take(run.hintLevel).forEachIndexed { i, h ->
                        key(i) {
                            val newest = i == run.hintLevel - 1
                            // Eine reine Formel bekommt einen Satz davor und, beim neuesten Tipp, ihre Zeichen erklärt
                            if (isFormulaOnly(h)) {
                                Text(
                                    tr("So sieht das als Formel aus:", "Here it is as a formula:"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (newest) p.textSecondary else p.textTertiary,
                                    modifier = Modifier.reveal(0).padding(bottom = Space.s1),
                                )
                            }
                            MathText(
                                h,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (newest) p.text else p.textTertiary,
                                modifier = Modifier.reveal(0).padding(bottom = Space.s3),
                            )
                            if (newest && isFormulaOnly(h)) {
                                val signs = remember(h) { signsIn(listOf(h), max = 3) }
                                signs.forEach { (show, text) ->
                                    Box(Modifier.reveal(40).padding(bottom = Space.s2)) { SignRow(show, text) }
                                }
                                if (signs.isNotEmpty()) Spacer(Modifier.height(Space.s1))
                            }
                        }
                    }
                    if (run.hintLevel > step.hints.size && step.formula != null) {
                        FormulaCardView(step.formula, Modifier.reveal(0).padding(bottom = Space.s3))
                    }
                }
                if (run.canHint) {
                    Spacer(Modifier.height(Space.s1))
                    LipButton(
                        when {
                            run.hintLevel == 0 -> tr("Tipp zeigen", "Show hint")
                            run.hintLevel < step.hints.size -> tr("Noch ein Tipp", "Another hint")
                            else -> tr("Formel zeigen", "Show formula")
                        },
                        onClick = run::hint,
                        tone = LipTone.Accent,
                        icon = R.drawable.ic_bulb,
                        height = Sizes.touch,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                // Die Lösung: leise darunter; bis sie geht, zählt sie herunter
                val left = (REVEAL_AFTER_S - run.elapsed).coerceAtLeast(0)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = Sizes.touch)
                        .then(if (run.canReveal) Modifier.bouncyClickable(shape = Shapes.pill) { onDismiss(); run.reveal() } else Modifier),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (run.canReveal) tr("Lösung zeigen", "Show solution") else tr("Lösung in $left s", "Solution in $left s"),
                        style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"),
                        color = if (run.canReveal) p.textSecondary else p.textTertiary,
                    )
                }
            }
        }
    }
}

/** Was bei einer falschen Zahl hilft: Einheit, Vorzeichen, Rundung – sonst allgemein. */
private fun numberFeedback(a: Answer.Number, x: Double, errors: Int): String {
    val v = a.value
    fun near(y: Double) = v != 0.0 && abs(y - v) <= abs(v) * 0.03
    return when {
        near(-x) -> tr("Fast: Prüf das Vorzeichen.", "Almost: check the sign.")
        near(x / 1000) || near(x * 1000) || near(x / 100) || near(x * 100) ->
            tr("Die Größenordnung stimmt nicht. Achte auf die Einheit" + (if (a.unit.isNotEmpty()) " (${a.unit})." else "."), "The order of magnitude is off. Watch the unit" + (if (a.unit.isNotEmpty()) " (${a.unit})." else "."))
        near(x / 3.6) || near(x * 3.6) -> tr("Verwechselt? km/h und m/s unterscheiden sich um den Faktor 3,6.", "Mixed up? km/h and m/s differ by a factor of 3.6.")
        v != 0.0 && abs(x - v) <= abs(v) * 0.12 -> tr("Ganz nah dran. Rechne genauer oder runde später.", "Very close. Calculate more precisely or round later.")
        errors >= 2 -> tr("Ein Tipp von Lumi hilft vielleicht weiter.", "A hint from Lumi might help.")
        else -> tr("Versuch es noch mal.", "Try again.")
    }
}

// ---------------------------------------------------------------------------------------------
// Erklärung (im Blatt von unten)
// ---------------------------------------------------------------------------------------------

/**
 * Erklärung im Blatt (seit 13.3): oben das Ergebnis, dann jeder Schritt als Karte (Gefragt, Die Idee,
 * So rechnest du, Eingesetzt, Ergebnis), darunter "Worum es geht", "Zeichen kurz erklärt" und "Wusstest du?".
 */
@Composable
fun Explanation(task: Task) {
    val p = Funke.colors
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Lösungsweg", "Solution"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.weight(1f))
        }
        // Ergebnis(se) auf einen Blick
        val results = task.steps.map { it.done }.filter { it.isNotBlank() }
        if (results.isNotEmpty()) {
            Spacer(Modifier.height(Space.s3))
            Column(
                Modifier.fillMaxWidth().clip(Shapes.md).background(p.success.copy(alpha = if (p.isDark) 0.14f else 0.10f)).padding(horizontal = Space.s4, vertical = Space.s3),
            ) {
                Text(tr("Ergebnis", "Result"), style = MaterialTheme.typography.labelMedium, color = p.success)
                Spacer(Modifier.height(Space.s1))
                results.forEach { r -> MathText(r, style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.padding(vertical = 2.dp)) }
            }
        }
        Spacer(Modifier.height(Space.s5))
        // Schritt für Schritt (seit 13.3): Gefragt, Idee, Formel, Eingesetzt, Ergebnis
        Text(
            if (task.steps.size > 1) tr("So kommst du hin – Schritt für Schritt", "How to get there – step by step") else tr("So kommst du hin", "How to get there"),
            style = MaterialTheme.typography.labelLarge,
            color = p.textSecondary,
        )
        Spacer(Modifier.height(Space.s2))
        StepByStep(task)
        Spacer(Modifier.height(Space.s1))
        if (task.background.isNotBlank()) {
            Spacer(Modifier.height(Space.s3))
            Column(
                Modifier.fillMaxWidth().clip(Shapes.card).background(p.accentSoft).padding(Space.s4),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_idea), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconSm + 2.dp))
                    Spacer(Modifier.width(Space.s2))
                    Text(tr("Worum es geht", "What it's about"), style = MaterialTheme.typography.labelLarge, color = p.accent)
                }
                Spacer(Modifier.height(Space.s2))
                MathText(task.background, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp), color = p.text)
            }
        }
        // Rechenzeichen in einfachen Worten (die Formeln selbst stehen schon im jeweiligen Schritt)
        SignsCard(task)
        task.fact?.let { fact ->
            Spacer(Modifier.height(Space.s3))
            Row(
                Modifier.fillMaxWidth().clip(Shapes.md).background(p.track).padding(Space.s4),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(painterResource(R.drawable.ic_sparkles), contentDescription = null, tint = p.tame(SparkGold), modifier = Modifier.padding(top = 2.dp).size(Sizes.iconSm))
                Spacer(Modifier.width(Space.s3))
                Column(Modifier.weight(1f)) {
                    Text(tr("Wusstest du?", "Did you know?"), style = MaterialTheme.typography.labelLarge, color = p.text)
                    Spacer(Modifier.height(Space.s1))
                    MathText(fact, style = MaterialTheme.typography.bodyMedium, color = p.textSecondary)
                }
            }
        }
        task.source?.let { src ->
            Spacer(Modifier.height(Space.s3))
            Text(tr("Aufgabe aus ", "Task from ") + src, style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
        }
    }
}

/** Formelkarte: Titel, Formel groß, ein Satz dazu. */
@Composable
fun FormulaCardView(card: FormulaCard, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Column(
        modifier
            .fillMaxWidth()
            .clip(Shapes.md)
            .background(p.track)
            .padding(Space.s4),
    ) {
        Text(card.title, style = MaterialTheme.typography.labelLarge, color = p.textSecondary)
        Spacer(Modifier.height(Space.s2))
        MathText(card.formula, style = MaterialTheme.typography.titleLarge, color = p.text)
        Spacer(Modifier.height(Space.s2))
        MathText(card.text, style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
    }
}

// ---------------------------------------------------------------------------------------------
// Funken
// ---------------------------------------------------------------------------------------------

/** Goldton der Funken (wird mit tame() ans Farbschema angepasst). */
val SparkGold: Color get() = de.funke.app.ui.theme.RewardColors.Spark

/** "+6" mit Funken-Symbol. */
@Composable
fun SparkChip(n: Int, modifier: Modifier = Modifier, prefix: String = "+") {
    val p = Funke.colors
    Row(
        modifier
            .clip(Shapes.pill)
            .background(p.tame(SparkGold).copy(alpha = if (p.isDark) 0.16f else 0.14f))
            .padding(horizontal = Space.s3, vertical = Space.s1)
            .semantics(mergeDescendants = true) { contentDescription = tr("$prefix$n Funken", "$prefix$n sparks") },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(R.drawable.ic_sparkles), contentDescription = null, tint = p.tame(SparkGold), modifier = Modifier.size(Sizes.iconSm))
        Spacer(Modifier.width(Space.s1))
        Text("$prefix$n", style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.text)
    }
}

// ---------------------------------------------------------------------------------------------
// Zahlen
// ---------------------------------------------------------------------------------------------

/** Auf [digits] gültige Stellen runden. */
private fun roundSig(x: Double, digits: Int): Double {
    if (x == 0.0) return 0.0
    val mag = Math.floor(log10(abs(x))).toInt()
    val scale = 10.0.pow(digits - 1 - mag)
    return Math.round(x * scale) / scale
}

/** Zahl kurz und lesbar, mit deutschem Komma. Große Zahlen in Worten, sehr große als Zehnerpotenz. */
private fun shortNumber(x: Double): String {
    val v = roundSig(x, 2)
    return when {
        v >= 1e12 -> {
            val e = Math.floor(log10(v)).toInt()
            val m = roundSig(v / 10.0.pow(e), 2)
            val mant = Tasks.fmt(m, 1)
            if (mant == "1") "$10^{$e}$" else "$$mant \\cdot 10^{$e}$"
        }
        v >= 1e9 -> Tasks.fmt(v / 1e9, 1) + tr(" Mrd.", " bn")
        v >= 1e6 -> Tasks.fmt(v / 1e6, 1) + tr(" Mio.", " m")
        v >= 1e4 -> groupThousands(Math.round(v))
        v >= 10 -> Math.round(v).toString()
        else -> Tasks.fmt(v, 2)
    }
}

private fun groupThousands(n: Long): String = n.toString().reversed().chunked(3).joinToString(if (Lang.german) "." else ",").reversed()

/** Schätzwert mit passender Einheit: Dauer in Sekunden, Länge in Metern, Masse in kg. */
fun formatEstimate(x: Double, kind: EstimateKind): String {
    val year = 365.25 * 86_400
    return when (kind) {
        EstimateKind.Count -> shortNumber(x)
        EstimateKind.Duration -> when {
            x < 90 -> shortNumber(x) + " s"
            x < 90 * 60 -> shortNumber(x / 60) + " min"
            x < 48 * 3600 -> shortNumber(x / 3600) + tr(" Std.", " h")
            x < 2 * year -> shortNumber(x / 86_400) + tr(" Tage", " days")
            else -> shortNumber(x / year) + tr(" Jahre", " years")
        }
        EstimateKind.Length -> when {
            x < 0.01 -> shortNumber(x * 1000) + " mm"
            x < 1 -> shortNumber(x * 100) + " cm"
            x < 1000 -> shortNumber(x) + " m"
            else -> shortNumber(x / 1000) + " km"
        }
        EstimateKind.Mass -> when {
            x < 1 -> shortNumber(x * 1000) + " g"
            x < 1000 -> shortNumber(x) + " kg"
            else -> shortNumber(x / 1000) + " t"
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Rechner
// ---------------------------------------------------------------------------------------------

/**
 * Rechnet einen Ausdruck aus: + − × ÷, ^ (Potenz), √(…), ln(…), π, Klammern, Malpunkt darf fehlen ("2π").
 * Offene Klammern werden am Ende geschlossen. Ungültig → null.
 */
object SciCalc {
    fun eval(expr: String): Double? {
        if (expr.isBlank()) return null
        var e = expr.replace('×', '*').replace('÷', '/').replace('−', '-').replace(',', '.')
        val open = e.count { it == '(' } - e.count { it == ')' }
        if (open > 0) e += ")".repeat(open)
        val r = Reader(e)
        val v = r.expression() ?: return null
        return if (r.atEnd && v.isFinite()) v else null
    }

    private class Reader(val e: String) {
        var i = 0
        val atEnd get() = i == e.length
        fun peek() = if (i < e.length) e[i] else '\u0000'

        fun expression(): Double? {
            var v = term() ?: return null
            while (peek() == '+' || peek() == '-') {
                val op = peek(); i++
                val r = term() ?: return null
                v = if (op == '+') v + r else v - r
            }
            return v
        }

        fun term(): Double? {
            var v = unary() ?: return null
            while (true) {
                val c = peek()
                when {
                    c == '*' || c == '/' -> {
                        i++
                        val r = unary() ?: return null
                        v = if (c == '*') v * r else { if (r == 0.0) return null; v / r }
                    }
                    // Malpunkt darf fehlen: 2π, 3(4+1), 2√(2)
                    c == '(' || c == 'π' || c == '√' || c == 'l' || c.isDigit() -> v *= power() ?: return null
                    else -> return v
                }
            }
        }

        fun unary(): Double? = if (peek() == '-') { i++; unary()?.let { -it } } else power()

        fun power(): Double? {
            val base = atom() ?: return null
            if (peek() != '^') return base
            i++
            val r = base.pow(unary() ?: return null)
            return if (r.isFinite()) r else null
        }

        fun atom(): Double? = when {
            peek() == '(' -> {
                i++
                val v = expression()
                if (v == null || peek() != ')') null else { i++; v }
            }
            peek() == 'π' -> { i++; Math.PI }
            peek() == '√' -> { i++; atom()?.takeIf { it >= 0 }?.let { kotlin.math.sqrt(it) } }
            e.startsWith("ln", i) -> { i += 2; atom()?.takeIf { it > 0 }?.let { ln(it) } }
            peek().isDigit() || peek() == '.' -> {
                val start = i
                while (peek().isDigit() || peek() == '.') i++
                e.substring(start, i).toDoubleOrNull()
            }
            else -> null
        }
    }

    /** Für die Anzeige: höchstens 6 gültige Stellen, sehr große/kleine mit E. */
    fun format(v: Double): String {
        if (v == 0.0) return "0"
        val a = abs(v)
        if (a >= 1e10 || a < 1e-5) return "%.4e".format(java.util.Locale.ROOT, v).replace(Regex("0+e"), "e").replace(".e", "e")
        val digits = (6 - 1 - Math.floor(log10(a)).toInt()).coerceIn(0, 10)
        return "%.${digits}f".format(java.util.Locale.ROOT, v).let { if ('.' in it) it.trimEnd('0').trimEnd('.') else it }
    }

    /** Als Eingabe für das Zahlenfeld (mit Komma). */
    fun toInput(v: Double): String {
        val s = format(v)
        if ('e' in s) return Tasks.fmt(v, 4).replace('.', ',')
        return s.replace('.', ',')
    }
}

/** Farbe eines Fachs. */
val Subject.tint: Color get() = Color(color)

/** Symbol eines Fachs (siehe GroupIcon), für kleine Stellen. */
val Subject.icon: String
    get() = when (this) {
        Subject.Math -> "math"
        Subject.Physics -> "atom"
        Subject.Logic -> "bulb"
        Subject.Geography -> "browser"
        Subject.Chemistry -> "flask"
        Subject.Biology -> "leaf"
        Subject.History -> "hourglass"
        Subject.Geology -> "mountain"
        Subject.Philosophy -> "think"
        Subject.Literature -> "learn"
        Subject.Extras -> "star"
    }
