package de.funke.app.ui.components

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Funke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow

/**
 * "KI-Knopf" im Glas-Look – frei nachgebaut nach der Glass-Button-Idee von 21st.dev (dort React/CSS).
 *
 *  - Glas: halbdurchsichtige Fläche mit schrägem Lichtverlauf und Lichtkante oben.
 *  - Rand: zwei Lichtpunkte (Akzentfarben) wandern langsam um den Knopf herum (beim Arbeiten schneller).
 *  - Ab und zu läuft ein Lichtstreifen über den Knopf.
 *  - Drücken: der Knopf kippt leicht nach hinten (wie im Original), der farbige Schein wird kleiner.
 *
 * Cartoon: Kontur und harter Schatten statt Glas. Flat: flache getönte Fläche.
 */
@Composable
fun GlassAiButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    working: Boolean = false,
    icon: @Composable (Color) -> Unit = {},
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val shape = Shapes.pill
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(if (pressed) 1f else 0f, tween(400, easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)), label = "press")

    val spin by rememberLoop(0f, 1f, infiniteRepeatable(tween(5200, easing = LinearEasing)), label = "spin")
    val fastSpin by rememberLoop(0f, 1f, infiniteRepeatable(tween(1300, easing = LinearEasing)), label = "fastSpin")
    val shimmer by rememberLoop(
        -0.6f, 1.6f,
        infiniteRepeatable(tween(2600, delayMillis = 2400, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)), RepeatMode.Restart),
        label = "shimmer",
    )
    val angle = (if (working) fastSpin else spin) * 2f * PI.toFloat()

    val textColor = if (enabled) p.text else p.textTertiary
    val glowAlpha = if (enabled) (if (p.isDark) 0.30f else 0.34f) * (1f - 0.5f * press) else 0f

    val body = when {
        cartoon -> Modifier
            .hardShadow(shape, p.ink, (3f - 2f * press).dp)
            .clip(shape)
            .background(p.surface)
            .background(Brush.linearGradient(listOf(p.accent.copy(alpha = 0.20f), p.accentAlt.copy(alpha = 0.14f))))
            .border(2.dp, p.ink, shape)
        flat -> Modifier
            .clip(shape)
            .background(lerp(p.surface, p.accent, if (p.isDark) 0.16f else 0.12f))
        else -> Modifier
            .softShadow(shape, p.accent, alpha = glowAlpha, blur = 18.dp, offsetY = (8f - 4f * press).dp)
            .softShadow(shape, Color.Black, alpha = if (p.isDark) 0.30f else 0.08f, blur = 4.dp, offsetY = 2.dp)
            .clip(shape)
            .background(p.surface.copy(alpha = if (p.isDark) 0.55f else 0.70f))
            .background(
                Brush.linearGradient(
                    0f to p.glass.copy(alpha = 0.04f),
                    0.5f to p.glass.copy(alpha = if (p.isDark) 0.14f else 0.05f),
                    1f to p.glass.copy(alpha = 0.04f),
                    start = Offset(0f, 0f),
                    end = Offset(260f, 120f),
                ),
            )
            .background(Brush.linearGradient(listOf(p.accent.copy(alpha = 0.16f), p.accentAlt.copy(alpha = 0.10f))))
    }

    Box(
        modifier
            .graphicsLayer {
                rotationX = 22f * press
                cameraDistance = 14f * density
                val s = 1f - 0.03f * press
                scaleX = s
                scaleY = s
                alpha = if (enabled) 1f else 0.6f
            }
            .then(body)
            .drawWithContent {
                drawContent()
                if (flat) return@drawWithContent
                val w = size.width
                val h = size.height
                // Lichtstreifen, der ab und zu über den Knopf läuft
                if (enabled) {
                    val x = w * shimmer
                    drawRect(
                        Brush.linearGradient(
                            listOf(Color.Transparent, Color.White.copy(alpha = if (p.isDark) 0.16f else 0.45f), Color.Transparent),
                            start = Offset(x - h * 1.2f, 0f),
                            end = Offset(x + h * 0.2f, h),
                        ),
                    )
                }
                if (cartoon) return@drawWithContent
                // Lichtkante oben (innen)
                drawRoundRect(
                    Brush.verticalGradient(0f to Color.White.copy(alpha = if (p.isDark) 0.16f else 0.70f), 0.45f to Color.Transparent),
                    cornerRadius = CornerRadius(h / 2f),
                    style = Stroke(1.dp.toPx()),
                )
                // Wandernde Lichtpunkte am Rand: zwei gegenüberliegende Spitzen (wie der Kegel-Verlauf im Original)
                val n = 48
                val colors = List(n + 1) { i ->
                    val a = i / n.toFloat() * 2f * PI.toFloat()
                    val d1 = cos(a - angle)
                    val d2 = cos(a - angle - PI.toFloat())
                    val k1 = exp(-((1f - d1) * 7f)).toFloat()
                    val k2 = exp(-((1f - d2) * 7f)).toFloat()
                    val base = Color.White.copy(alpha = if (p.isDark) 0.10f else 0.35f)
                    when {
                        k1 >= k2 -> lerp(base, p.accent, k1.pow(0.8f))
                        else -> lerp(base, p.accentAlt, k2.pow(0.8f))
                    }
                }
                val sw = 1.6.dp.toPx()
                drawRoundRect(
                    Brush.sweepGradient(colors, center = Offset(w / 2f, h / 2f)),
                    topLeft = Offset(sw / 2f, sw / 2f),
                    size = Size(w - sw, h - sw),
                    cornerRadius = CornerRadius((h - sw) / 2f),
                    style = Stroke(sw),
                )
            }
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Space.s5, vertical = Space.s4),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon(if (enabled) p.accent else p.textTertiary)
            Spacer(Modifier.width(Space.s2))
            Text(label, style = MaterialTheme.typography.titleSmall, color = textColor)
        }
    }
}
