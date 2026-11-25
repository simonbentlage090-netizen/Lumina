package de.funke.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.BrandColors
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.reducedMotion

/**
 * Wie weit die Aufgeh-Animation des System-Startbildschirms schon war, als die App übernommen hat
 * (wird in MainActivity gesetzt). So läuft sie hier ohne Sprung zu Ende.
 */
object SplashHandOff {
    @Volatile var elapsedMs: Float = 0f
}

/**
 * Start (einmal beim Öffnen): Das App-Symbol vom System-Startbildschirm steht an derselben Stelle,
 * die Sonne geht fertig auf, dann tritt das Symbol leicht zurück und gibt die App frei.
 */
@Composable
fun StartIntro(dark: Boolean, onDone: () -> Unit) {
    val done = rememberUpdatedState(onDone)
    val reduced = reducedMotion
    val start = remember { SplashHandOff.elapsedMs.coerceIn(0f, LOGO_ANIM_MS) }
    val rest = LOGO_ANIM_MS - start
    // Zeitleiste in ms: Rest der Aufgeh-Animation, kurz stehen, dann 260 ms raus
    val hold = 90f
    val exit = if (reduced) 180f else 260f
    val total = rest + hold + exit
    val t = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        t.animateTo(total, tween(total.toInt(), easing = LinearEasing))
        done.value()
    }
    Canvas(Modifier.fillMaxSize()) {
        val now = t.value
        val out = ((now - rest - hold) / exit).coerceIn(0f, 1f)
        val e = Motion.EaseOut.transform(out)
        drawRect(if (dark) BrandColors.WindowDark else BrandColors.WindowLight, alpha = 1f - e)
        val d = 192.dp.toPx() * (if (reduced) 1f else 1f - 0.06f * e)
        drawLuminaIcon(
            center,
            d,
            LogoShape.Circle,
            ms = start + now.coerceAtMost(rest),
            alpha = 1f - e,
        )
    }
}
