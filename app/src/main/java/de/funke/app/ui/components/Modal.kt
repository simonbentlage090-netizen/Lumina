package de.funke.app.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion

/** Im Inhalt eines [Modal]: [close] spielt die Schließ-Animation und ruft danach [then] auf. */
@Stable
interface ModalScope {
    fun close(then: () -> Unit = {})
}

/**
 * Dialog mit Übergängen nach transitions.dev:
 * Öffnen: Abdunklung blendet ein (200 ms), die Karte kommt aus 96 % Größe und 1/40 von unten (250 ms, Ease-out).
 * Schließen: schneller (150 ms), die Karte schrumpft auf 97 % und blendet aus. Erst danach wird
 * [onDismiss] bzw. die Aktion aus [ModalScope.close] ausgeführt, so sieht man das Schließen wirklich.
 * Bei "Bewegung reduzieren" nur Überblenden.
 *
 * Antippen der Abdunklung und die Zurück-Geste schließen ebenfalls (mit [dismissible] abschaltbar).
 */
@Composable
fun Modal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissible: Boolean = true,
    padding: Dp = Space.s6,
    maxWidth: Dp = 420.dp,
    content: @Composable ModalScope.() -> Unit,
) {
    val p = Funke.colors
    val reduced = reducedMotion
    val visible = remember { MutableTransitionState(false) }
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    val scope = remember {
        object : ModalScope {
            override fun close(then: () -> Unit) {
                if (pending != null) return
                pending = then
                visible.targetState = false
            }
        }
    }
    LaunchedEffect(Unit) { visible.targetState = true }
    // Schließ-Animation fertig → Aktion ausführen
    LaunchedEffect(visible.currentState, visible.isIdle, pending) {
        val action = pending
        if (action != null && visible.isIdle && !visible.currentState) action()
    }
    BackHandler(enabled = visible.targetState) { if (dismissible) scope.close(onDismiss) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visibleState = visible,
            enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)),
            exit = fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(p.scrim)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        if (dismissible) scope.close(onDismiss)
                    }
            )
        }
        AnimatedVisibility(
            visibleState = visible,
            enter = Motion.modalEnter(reduced),
            exit = Motion.modalExit(reduced),
        ) {
            Column(
                modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = Space.s6)
                    .widthIn(max = maxWidth)
                    .fillMaxWidth()
                    .surface(Shapes.large, Elevation.Level3, opaque = true)
                    // Klicks auf die Karte nicht an die Abdunklung durchreichen
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                    .padding(padding),
            ) {
                scope.content()
            }
        }
    }
}

/**
 * [Modal] in einem eigenen Fenster: liegt über allem (auch über der Navigationsleiste),
 * egal wo es im Code steht. Das Fenster selbst hat keine Abdunklung und keine eigene Animation,
 * beides macht [Modal].
 */
@Composable
fun ModalWindow(
    onDismiss: () -> Unit,
    dismissible: Boolean = true,
    padding: Dp = Space.s6,
    content: @Composable ModalScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            window?.let {
                it.setDimAmount(0f)
                it.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                it.setWindowAnimations(android.R.style.Animation)
            }
        }
        Modal(onDismiss = onDismiss, dismissible = dismissible, padding = padding, content = content)
    }
}
