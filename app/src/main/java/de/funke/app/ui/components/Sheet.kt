package de.funke.app.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Radius
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion

/**
 * Zählt offene Blätter und Vollbild-Auswahlen (seit 14.0). Solange eins offen ist, weicht die Leiste unten
 * und die Reiter lassen sich nicht wegwischen – vorher lag die Leiste über dem Blatt und verdeckte dessen Knöpfe
 * (z. B. "Entfernen" bei einem Buch).
 */
object Overlays {
    var count by mutableIntStateOf(0)
        private set

    internal fun push() { count++ }
    internal fun pop() { count = (count - 1).coerceAtLeast(0) }
}

/** Lässt die Leiste unten weichen, solange [active] ist. */
@Composable
fun HideNavBarWhile(active: Boolean) {
    if (active) {
        DisposableEffect(Unit) {
            Overlays.push()
            onDispose { Overlays.pop() }
        }
    }
}

/**
 * Blatt von unten (transitions.dev "drawer"): Abdunklung blendet ein, das Blatt fährt mit der
 * Drawer-Kurve herein (300 ms) und schneller wieder hinaus (200 ms). Antippen der Abdunklung,
 * der Griff oben und die Zurück-Geste schließen es. Das Blatt ist so hoch wie sein Inhalt, scrollt bei Bedarf.
 * Liegt im selben Fenster: Für ein Blatt über allem den Aufrufer ganz oben platzieren.
 */
@Composable
fun BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val p = Funke.colors
    val still = reducedMotion
    BackHandler(enabled = visible, onBack = onDismiss)
    HideNavBarWhile(visible)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)),
            exit = fadeOut(tween(Motion.Base, easing = Motion.EaseOut)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(p.scrim)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
            )
        }
        AnimatedVisibility(
            visible = visible,
            enter = if (still) fadeIn(tween(Motion.Fast)) else slideInVertically(tween(Motion.Slow, easing = Motion.Drawer)) { it } + fadeIn(tween(Motion.Fast)),
            exit = if (still) fadeOut(tween(Motion.Fast)) else slideOutVertically(tween(Motion.Base, easing = Motion.EaseOut)) { it } + fadeOut(tween(Motion.Base)),
        ) {
            val shape = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet)
            androidx.compose.foundation.layout.BoxWithConstraints(
                Modifier.fillMaxSize().statusBarsPadding().padding(top = Space.s12),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .heightIn(max = maxHeight)
                        .elevation(shape, Elevation.Level3)
                        .clip(shape)
                        .background(p.raised(Elevation.Level2))
                        // Klicks im Blatt nicht an die Abdunklung durchreichen
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                ) {
                    // Griff: Tippen schließt
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(Space.s8)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(Modifier.size(width = 36.dp, height = 4.dp).clip(Shapes.pill).background(p.textTertiary.copy(alpha = 0.4f)))
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(start = Space.page, end = Space.page, bottom = Space.s8),
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
