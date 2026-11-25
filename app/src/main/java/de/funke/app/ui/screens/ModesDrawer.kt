package de.funke.app.ui.screens

import de.funke.app.ui.theme.Space
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.funke.app.ui.AppState
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * Zustand der Modus-Leiste, die von links hereinkommt.
 * [progress]: 0 = zu, 1 = ganz offen. Folgt beim Ziehen direkt dem Finger.
 */
@Stable
class ModesDrawerState {
    val progress = Animatable(0f)
    var widthPx by mutableFloatStateOf(1f)

    val isOpen: Boolean get() = progress.targetValue > 0.5f

    suspend fun open() {
        progress.animateTo(1f, spring(dampingRatio = 0.86f, stiffness = 380f))
    }

    suspend fun close() {
        progress.animateTo(0f, spring(dampingRatio = 1f, stiffness = 520f))
    }

    suspend fun dragBy(deltaPx: Float) {
        progress.snapTo((progress.value + deltaPx / widthPx).coerceIn(0f, 1f))
    }

    /** Nach dem Loslassen: Schwung und Position entscheiden, ob sie auf- oder zugeht. */
    suspend fun settle(velocityPx: Float): Boolean {
        val v = velocityPx / widthPx
        val openIt = v > 1.2f || (v > -1.2f && progress.value > 0.45f)
        if (openIt) open() else close()
        return openIt
    }
}

/**
 * Die Modus-Leiste. Beim Öffnen gleitet das Panel herein und die Modi folgen
 * einzeln versetzt, wie eine kleine Welle. Antippen wählt den Modus, man sieht kurz
 * den Haken aufspringen, dann schließt sich die Leiste von selbst.
 */
@Composable
fun ModesDrawer(state: AppState, drawer: ModesDrawerState) {
    val p = Funke.colors
    val scope = rememberCoroutineScope()
    val haptics = LocalHaptics.current
    val installed = LocalInstalledApps.current
    val density = LocalDensity.current
    val progress = drawer.progress.value

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val panelWidth = min(maxWidth.value * 0.86f, 380f).dp
        drawer.widthPx = with(density) { panelWidth.toPx() }
        if (progress <= 0.001f) return@BoxWithConstraints

        val dragState = rememberDraggableState { dx -> scope.launch { drawer.dragBy(dx) } }
        val dragModifier = Modifier.draggable(
            state = dragState,
            orientation = Orientation.Horizontal,
            onDragStopped = { velocity ->
                drawer.settle(velocity)
                haptics?.tick()
            },
        )

        // Abdunkeln dahinter; Tippen schließt
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = progress }
                .background(Color.Black.copy(alpha = 0.55f))
                .then(dragModifier)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    haptics?.tick()
                    scope.launch { drawer.close() }
                }
        )

        val flat = Funke.flat
        val shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
        Column(
            Modifier
                .width(panelWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    translationX = -(1f - progress) * size.width
                }
                .then(if (flat) Modifier else Modifier.shadow(32.dp, shape, ambientColor = Color.Black, spotColor = Color.Black))
                .clip(shape)
                .background(p.surface)
                .then(
                    if (flat) Modifier
                    else Modifier.background(Brush.verticalGradient(listOf(p.accent.copy(alpha = 0.07f), Color.Transparent)))
                )
                .then(dragModifier)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = Space.s5, end = Space.s5, top = Space.s6, bottom = Space.s6),
        ) {
            val headerT = stagger(progress, 0)
            Column(
                Modifier
                    .padding(start = Space.s2, bottom = Space.s5)
                    .graphicsLayer {
                        alpha = headerT
                        translationX = -(1f - headerT) * 40.dp.toPx()
                    }
            ) {
                Text(tr("Modi", "Modes"), style = MaterialTheme.typography.headlineLarge, color = p.text)
            }

            state.modes.forEachIndexed { index, mode ->
                val t = stagger(progress, index + 1)
                ModeTile(
                    mode = mode,
                    installed = installed,
                    selected = mode.id == state.selectedModeId,
                    expanded = false,
                    onClick = {
                        state.selectMode(mode.id)
                        scope.launch {
                            delay(260)
                            drawer.close()
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = Space.s1)
                        .graphicsLayer {
                            alpha = t
                            translationX = -(1f - t) * 64.dp.toPx()
                            val s = 0.94f + 0.06f * t
                            scaleX = s
                            scaleY = s
                        },
                )
            }

            val tNew = stagger(progress, state.modes.size + 1)
            NewModeCard(
                onClick = {
                    scope.launch { drawer.close() }
                    state.openEditor(null)
                },
                modifier = Modifier
                    .padding(top = Space.s2)
                    .graphicsLayer {
                        alpha = tNew
                        translationX = -(1f - tNew) * 64.dp.toPx()
                    },
            )
        }
    }
}

/** Versatz für die Welle: Element [index] startet etwas später als das davor. */
private fun stagger(progress: Float, index: Int): Float {
    val start = 0.12f + index * 0.07f
    val t = ((progress - start) / 0.5f).coerceIn(0f, 1f)
    return 1f - (1f - t) * (1f - t) * (1f - t)
}

/** Schmaler Streifen am linken Rand: von hier aus lässt sich die Leiste mit dem Finger aufziehen. */
@Composable
fun DrawerEdgeHandle(drawer: ModesDrawerState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHaptics.current
    Box(
        modifier
            .width(28.dp)
            .fillMaxHeight()
            .draggable(
                state = rememberDraggableState { dx -> scope.launch { drawer.dragBy(dx) } },
                orientation = Orientation.Horizontal,
                onDragStarted = { haptics?.tick() },
                onDragStopped = { velocity ->
                    if (drawer.settle(velocity)) haptics?.tick()
                },
            )
    )
}

/** Wie stark der Inhalt hinter der Leiste zurücktritt (für Unschärfe und Verschieben). */
fun ModesDrawerState.backdrop(): Float = progress.value

/** Unschärfe ist erst ab Android 12 möglich. */
val canBlur: Boolean get() = Build.VERSION.SDK_INT >= 31
