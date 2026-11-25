package de.funke.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.funke.app.ui.challenge.LocalChallengeSettings
import de.funke.app.ui.challenge.TaskUse
import de.funke.app.ui.challenge.Tasks
import de.funke.app.ui.components.BottomSheet
import de.funke.app.ui.components.LipPill
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.elevation
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Radius
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import kotlinx.coroutines.delay

/**
 * Die Hürde beim Abbrechen: eine Aufgabe lösen oder weiterlesen. Gelöst darf man gehen, die Session zählt
 * dann aber nicht. Gleiche Aufgaben-Ansicht wie in den Lektionen (unten "Prüfen", Rückmeldung, Lumi hilft),
 * nur einschrittige Aufgaben. Ganz ohne Aufgabe geht es über den kleinen Knopf oben rechts im Fokus-Bildschirm.
 */
@Composable
fun CancelChallenge(
    onKeepReading: () -> Unit,
    onSolved: () -> Unit,
) {
    val p = Funke.colors
    val settings = LocalChallengeSettings.current
    var run by remember { mutableStateOf(TaskRun(Tasks.next(settings.topics, settings.difficulty, TaskUse.Cancel))) }
    var appeared by remember { mutableStateOf(false) }
    var showExplain by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    val still = reducedMotion

    LaunchedEffect(Unit) { appeared = true }
    LaunchedEffect(run, run.phase) {
        showHint = false
        if (run.phase == TaskPhase.Correct) {
            delay(900)
            onSolved()
        }
    }
    TaskEffects(run)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            // Schluckt Berührungen, damit nichts dahinter angetippt wird.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = appeared,
            enter = if (still) fadeIn(tween(Motion.Fast)) else slideInVertically(tween(Motion.Slow, easing = Motion.Drawer)) { it } + fadeIn(tween(Motion.Fast)),
        ) {
            val sheetShape = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet)
            Column(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = Space.s6)
                    .elevation(sheetShape, Elevation.Level3)
                    .clip(sheetShape)
                    .background(p.bg),
            ) {
                // Kopf: Lumi schaut erschrocken (gelöst: freut sich), daneben "Weiterlesen"
                Row(
                    Modifier.fillMaxWidth().padding(start = Space.s5, end = Space.s5, top = Space.s5),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (de.funke.app.ui.LocalSidekick.current) {
                        val solved = run.phase == TaskPhase.Correct
                        Lumi2D(if (solved) LumiPose.Happy else LumiPose.Reading, scare = if (solved) 0f else 1f, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.width(Space.s3))
                    }
                    Text(tr("Wirklich aufhören?", "Really stop?"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(Space.s3))
                    LipPill(tr("Weitermachen", "Keep going"), onClick = onKeepReading, tone = LipTone.Accent)
                }
                Box(Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = run,
                        transitionSpec = { fadeIn(tween(Motion.Medium, 60, Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast)) },
                        label = "cancelTask",
                    ) { r ->
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = Space.s6),
                        ) {
                            TaskContent(r, TaskMode.Cancel)
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
                TaskDock(
                    run = run,
                    mode = TaskMode.Cancel,
                    onContinue = {
                        showExplain = false
                        run = TaskRun(Tasks.next(settings.topics, settings.difficulty, TaskUse.Cancel, avoid = run.task.id))
                    },
                    onExplain = { showExplain = true },
                    onHelper = { showHint = !showHint },
                )
            }
        }
        BottomSheet(visible = showExplain, onDismiss = { showExplain = false }) {
            Explanation(run.task)
            Spacer(Modifier.height(Space.s4))
        }
    }
}
