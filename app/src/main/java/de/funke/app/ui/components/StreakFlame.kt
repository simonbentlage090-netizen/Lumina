package de.funke.app.ui.components

import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.FlameColors
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.theme.Funke
import kotlin.math.PI
import kotlin.math.sin

private val FlameTop = FlameColors.Top
private val FlameBottom = FlameColors.Bottom
private val FlameCore = FlameColors.Core

/**
 * Kleine Streak-Flamme (wie bei Duolingo), auf Wunsch mit der Zahl darin.
 * Sie flackert ganz dezent: die Spitze wiegt sich, die kleine Seitenflamme züngelt, das Ganze atmet minimal.
 * Bei 0 ist sie grau und steht still.
 */
@Composable
fun StreakFlame(streak: Int, modifier: Modifier = Modifier, showNumber: Boolean = true, still: Boolean = false) {
    val p = Funke.colors
    val active = streak > 0
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    // [still]: auf der Startseite steht die Flamme ruhig (ein Flackern am Rand zieht den Blick)
    val loop by rememberLoop(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1700, easing = LinearEasing)),
        label = "flameT",
    )
    val t = if (still) 0.25f else loop
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val ph = if (active) t * 2f * PI.toFloat() else 0f
            val sway = w * 0.07f * sin(ph)
            val lick = 1f + 0.16f * sin(ph * 2f + 1.1f)
            val breathe = 1f + 0.035f * sin(ph * 2f)
            scale(1f, breathe, pivot = Offset(w / 2f, h)) {
                val outer = Path().apply {
                    moveTo(w * 0.52f + sway, h * 0.02f)
                    cubicTo(w * 0.66f + sway * 0.6f, h * 0.24f, w * 0.94f, h * 0.38f, w * 0.94f, h * 0.64f)
                    cubicTo(w * 0.94f, h * 0.86f, w * 0.74f, h * 0.99f, w * 0.50f, h * 0.99f)
                    cubicTo(w * 0.26f, h * 0.99f, w * 0.06f, h * 0.86f, w * 0.06f, h * 0.64f)
                    cubicTo(w * 0.06f, h * 0.48f, w * 0.16f, h * (0.42f - 0.06f * (lick - 1f)), w * 0.24f, h * (0.30f / lick))
                    cubicTo(w * 0.30f, h * 0.40f, w * 0.40f, h * 0.36f, w * 0.40f, h * 0.30f)
                    cubicTo(w * 0.40f, h * 0.18f, w * 0.48f + sway * 0.6f, h * 0.10f, w * 0.52f + sway, h * 0.02f)
                    close()
                }
                val fill = when {
                    !active -> Brush.verticalGradient(listOf(p.glassAlpha(0.32f), p.glassAlpha(0.22f)), startY = 0f, endY = h)
                    flat || cartoon -> Brush.verticalGradient(listOf(FlameColors.Flat, FlameColors.Flat), startY = 0f, endY = h)
                    else -> Brush.verticalGradient(listOf(FlameTop, FlameBottom), startY = 0f, endY = h)
                }
                drawPath(outer, fill)
                if (active) {
                    // kleiner heller Kern oben, der leicht mitflackert
                    val core = Path().apply {
                        val cx = w * 0.56f + sway * 0.7f
                        moveTo(cx, h * 0.22f)
                        cubicTo(cx + w * 0.10f, h * 0.34f, cx + w * 0.14f, h * 0.44f, cx + w * 0.06f, h * 0.50f)
                        cubicTo(cx - w * 0.02f, h * 0.54f, cx - w * 0.10f, h * 0.46f, cx - w * 0.06f, h * 0.38f)
                        cubicTo(cx - w * 0.04f, h * 0.32f, cx - w * 0.01f, h * 0.28f, cx, h * 0.22f)
                        close()
                    }
                    drawPath(core, FlameCore.copy(alpha = 0.85f + 0.15f * sin(ph * 3f)))
                }
                if (cartoon) drawPath(outer, p.ink, style = Stroke(1.6.dp.toPx()))
            }
        }
        if (showNumber) Text(
            if (streak > 999) "999+" else "$streak",
            modifier = Modifier.padding(bottom = Space.s1),
            style = TextStyle(
                color = if (active) Color.White else p.textSecondary,
                fontSize = if (streak >= 100) 9.sp else 12.sp,
                fontWeight = FontWeight.ExtraBold,
                shadow = if (active && !flat) Shadow(FlameColors.Shadow, Offset(0f, 1f), 2f) else null,
            ),
        )
    }
}

/** Kleiner Eiskristall: Streak-Eis (hält den Streak, wenn man einen Tag verpasst). */
@Composable
fun IceIcon(modifier: Modifier = Modifier, color: androidx.compose.ui.graphics.Color = FlameColors.Ice) {
    androidx.compose.foundation.Canvas(modifier) {
        val c = center
        val r = size.minDimension * 0.46f
        val sw = size.minDimension * 0.11f
        for (i in 0 until 3) {
            val a = Math.toRadians(90.0 + i * 60.0)
            val dx = (kotlin.math.cos(a) * r).toFloat()
            val dy = (kotlin.math.sin(a) * r).toFloat()
            drawLine(color, androidx.compose.ui.geometry.Offset(c.x - dx, c.y - dy), androidx.compose.ui.geometry.Offset(c.x + dx, c.y + dy), sw, androidx.compose.ui.graphics.StrokeCap.Round)
            // kleine Äste an beiden Enden
            for (sgn in floatArrayOf(-1f, 1f)) {
                val ex = c.x + sgn * dx * 0.62f
                val ey = c.y + sgn * dy * 0.62f
                for (b in floatArrayOf(-1f, 1f)) {
                    val ba = a + b * Math.toRadians(40.0) + if (sgn < 0) Math.PI else 0.0
                    drawLine(
                        color,
                        androidx.compose.ui.geometry.Offset(ex, ey),
                        androidx.compose.ui.geometry.Offset(ex + (kotlin.math.cos(ba) * r * 0.32f).toFloat(), ey + (kotlin.math.sin(ba) * r * 0.32f).toFloat()),
                        sw * 0.8f,
                        androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                }
            }
        }
    }
}
