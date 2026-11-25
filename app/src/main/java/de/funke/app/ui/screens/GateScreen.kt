package de.funke.app.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.gate.Gate
import de.funke.app.ui.GateRequest
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.challenge.LocalChallengeSettings
import de.funke.app.ui.challenge.TaskUse
import de.funke.app.ui.challenge.Tasks
import de.funke.app.ui.components.AppIcon
import de.funke.app.ui.components.BottomSheet
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr

/**
 * Die Denkpause vor einer geschützten App (seit 12.0 wie bei JEE PYQ Lock): eine Frage, antworten oder
 * "Weiß ich nicht", die Erklärung lesen, weiter zur App. Danach ist bis zur nächsten Fragezeit Ruhe.
 *
 * Oben rechts geht es jederzeit zurück, ohne die App zu öffnen ("Lieber nicht"); nach der Frage steht
 * "Lieber nicht" gleichwertig neben "Weiter". Fächer und Schwierigkeit sind dieselben wie beim Abbrechen
 * einer Lesezeit (Einstellungen → Aufgaben).
 */
@Composable
fun GateScreen(request: GateRequest, onDone: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val installed = LocalInstalledApps.current
    val settings = LocalChallengeSettings.current
    val app = installed.firstOrNull { it.packageName == request.pkg }
    val appName = app?.label
        ?: request.pkg.substringAfterLast('.').replaceFirstChar { it.uppercase() }.ifBlank { tr("der App", "the app") }
    val preview = request.preview
    val currentOnDone by rememberUpdatedState(onDone)

    var run by remember(request) { mutableStateOf(TaskRun(Tasks.next(settings.topics, settings.difficulty, TaskUse.Cancel))) }
    var showExplain by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var settled by remember(request) { mutableStateOf(false) }

    // Beim Ausprobieren zählt nichts und es wird keine Fragezeit verbraucht
    LaunchedEffect(request) { if (!preview) Gate.countShown() }
    LaunchedEffect(run, run.phase) {
        showHint = false
        if (run.finished && !settled) {
            settled = true
            if (!preview) {
                Gate.consume()
                if (run.phase == TaskPhase.Correct) Gate.countRight()
            }
        }
    }
    TaskEffects(run)

    fun leave() {
        if (!preview) {
            Gate.countResisted()
            goHome(context)
        }
        currentOnDone()
    }

    fun open() {
        if (!preview) openApp(context, request.pkg)
        currentOnDone()
    }

    // Zurück heißt: lieber nicht öffnen
    BackHandler { leave() }
    // Verlässt man die Denkpause anders (Startbildschirm, App-Übersicht), ist sie erledigt, ohne zu zählen.
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, request) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_STOP) currentOnDone() }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(p.bg)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
    ) {
        Column(Modifier.fillMaxSize()) {
            // Kopf: App und Anlass links, Schließen (= lieber nicht) rechts
            Row(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = Space.page, end = Space.s4, top = Space.s4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (app == null && request.pkg.isBlank()) {
                    // Ausprobieren ohne gewählte App: ein neutrales Handy-Symbol statt eines Buchstabens
                    Box(
                        Modifier.size(40.dp).clip(de.funke.app.ui.theme.Shapes.md).background(p.track),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Icon(
                            androidx.compose.ui.res.painterResource(de.funke.app.R.drawable.ic_nav_screen),
                            contentDescription = null,
                            tint = p.textSecondary,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                } else {
                    AppIcon(app, appName, size = 40.dp)
                }
                Spacer(Modifier.width(Space.s3))
                Text(
                    when {
                        preview -> tr("Probe", "Preview")
                        request.checkIn -> tr("Kurze Frage", "Quick question")
                        else -> tr("Erst eine Frage", "One question first")
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = p.text,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                de.funke.app.ui.components.CloseButton(onClick = ::leave, description = tr("Lieber nicht öffnen", "Don't open"))
            }

            Box(Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = run,
                    transitionSpec = { fadeIn(tween(Motion.Medium, 60, Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast)) },
                    label = "gateTask",
                ) { r ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = Space.s6),
                    ) {
                        TaskContent(r, TaskMode.Gate)
                    }
                }
                if (showHint) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showHint = false }
                    )
                }
                HintBubble(run, showHint, onDismiss = { showHint = false }, modifier = Modifier.align(Alignment.BottomStart).padding(bottom = Space.s1))
            }

            // Unten: beim Antworten "Weiß ich nicht" und "Prüfen"; danach die Entscheidung
            AnimatedContent(
                targetState = run.finished,
                transitionSpec = {
                    (fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) + slideInVertically(tween(Motion.Medium, easing = Motion.EaseOut)) { it / 3 }) togetherWith fadeOut(tween(Motion.Fast))
                },
                label = "gateDock",
            ) { done ->
                if (done) {
                    DecidePanel(
                        correct = run.phase == TaskPhase.Correct,
                        appName = appName,
                        next = if (preview) null else Gate.nextSlot(),
                        onLeave = ::leave,
                        onOpen = ::open,
                        onExplain = { showExplain = true },
                    )
                } else {
                    TaskDock(
                        run = run,
                        mode = TaskMode.Gate,
                        onContinue = {},
                        onExplain = { showExplain = true },
                        onHelper = { showHint = !showHint },
                    )
                }
            }
        }
        BottomSheet(visible = showExplain, onDismiss = { showExplain = false }) {
            Explanation(run.task)
            Spacer(Modifier.height(Space.s4))
        }
    }
}

/**
 * Nach der Frage: kurze Rückmeldung, "Erklärung" und zwei gleich große Knöpfe. "Lieber nicht" in der
 * Akzentfarbe (die bequeme Möglichkeit, es bleiben zu lassen, wirkt am stärksten), "Weiter" neutral.
 */
@Composable
private fun DecidePanel(
    correct: Boolean,
    appName: String,
    next: java.time.LocalDateTime?,
    onLeave: () -> Unit,
    onOpen: () -> Unit,
    onExplain: () -> Unit,
) {
    val p = Funke.colors
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val tint = if (correct) p.success else p.textSecondary
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(p.bg)
            .background(tint.copy(alpha = if (p.isDark) 0.12f else 0.08f))
            .navigationBarsPadding()
            .padding(start = Space.page, end = Space.page, top = Space.s5, bottom = Space.s4),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (correct) {
                de.funke.app.ui.components.AnimatedCheckBadge(size = 32.dp)
                Spacer(Modifier.width(Space.s3))
            }
            Text(
                if (correct) tr("Richtig!", "Correct!") else tr("Jetzt weißt du's", "Now you know"),
                style = MaterialTheme.typography.titleLarge,
                color = tint,
                modifier = Modifier.weight(1f),
            )
            TextAction(tr("Erklärung", "Explanation"), onClick = onExplain, color = p.accent)
        }
        Spacer(Modifier.height(Space.s4))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            LipButton(tr("Lieber nicht", "Not now"), onClick = onLeave, tone = LipTone.Accent, modifier = Modifier.weight(1f))
            LipButton(tr("Weiter zu $appName", "Open $appName"), onClick = onOpen, tone = LipTone.Neutral, modifier = Modifier.weight(1f))
        }
        if (next != null) {
            Spacer(Modifier.height(Space.s3))
            Text(
                tr("Nächste Frage ab ${next.hour} Uhr", "Next question from ${clock(next.hour)}"),
                style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"),
                color = p.textTertiary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

/** 14 → "2 pm" (nur fürs Englische). */
internal fun clock(hour: Int): String = when {
    hour == 0 -> "12 am"
    hour < 12 -> "$hour am"
    hour == 12 -> "12 pm"
    else -> "${hour - 12} pm"
}

/** Zum Startbildschirm des Handys (Lumina geht in den Hintergrund). */
internal fun goHome(context: Context) {
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

private fun openApp(context: Context, pkg: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(pkg)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (intent == null || runCatching { context.startActivity(intent) }.isFailure) goHome(context)
}
