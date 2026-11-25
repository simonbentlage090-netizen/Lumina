package de.funke.app.ui.components

import de.funke.app.ui.theme.LumiColors
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.funke.app.ui.theme.Funke
import kotlin.math.PI
import kotlin.math.sin

/**
 * Kleine Glühbirne in der Akzentfarbe, die ab und zu winkt. Für Stellen, an denen der große Lumi
 * zu viel wäre: Streak, leere Listen, kleine Kacheln.
 *
 * [active] = false: die Birne ist aus (blass) und winkt nicht, z. B. wenn der Streak noch bei 0 steht.
 */
@Composable
fun MiniLumi(
    modifier: Modifier = Modifier,
    active: Boolean = true,
    wave: Boolean = true,
    contentDescription: String? = null,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    // Schleife von 3,4 s: dreimal winken, dann Pause
    val t by rememberLoop(
        initialValue = 0f,
        targetValue = 3.4f,
        animationSpec = infiniteRepeatable(tween(3400, easing = LinearEasing)),
        label = "miniLumiT",
    )
    val glass = if (active) p.accent else lerpTo(p.accent, p.textTertiary, 0.75f)
    val glassAlt = if (active) p.accentAlt else lerpTo(p.accentAlt, p.textTertiary, 0.75f)
    val ink = LumiColors.Ink
    val face = if (glass.luminance() > 0.38f) ink else Color.White
    val outline = when {
        cartoon -> p.ink
        p.isDark -> null
        else -> ink.copy(alpha = 0.18f)
    }

    Canvas(modifier.semantics { if (contentDescription != null) this.contentDescription = contentDescription }) {
        val w = size.minDimension
        val ox = (size.width - w) / 2f
        val oy = (size.height - w) / 2f
        fun P(x: Float, y: Float) = Offset(ox + x * w, oy + y * w)

        // Winken: drei schnelle Schwünge in den ersten 1,2 s, dann ruhig
        val waving = active && wave
        val swing = if (waving && t < 1.2f) sin(t / 0.4f * 2f * PI.toFloat()) * (1f - t / 1.2f * 0.3f) else 0f
        val sway = if (waving) 2.2f * sin(t / 3.4f * 2f * PI.toFloat()) else 0f

        rotate(sway, pivot = P(0.5f, 0.88f)) {
            // Arm (winkt) hinter dem Körper
            val shoulder = P(0.62f, 0.55f)
            // Seit 13.0 länger: Die winkende Hand reicht bis neben den Kopf, die andere hängt bis zum Gewinde
            val angle = -20f + 16f * swing
            rotate(angle, pivot = shoulder) {
                val arm = Path().apply {
                    moveTo(shoulder.x, shoulder.y)
                    quadraticBezierTo(shoulder.x + w * 0.22f, shoulder.y + w * 0.02f, shoulder.x + w * 0.29f, shoulder.y - w * 0.18f)
                }
                if (p.isDark) drawPath(arm, LumiColors.ArmRim, style = Stroke(w * 0.085f, cap = StrokeCap.Round))
                drawPath(arm, ink, style = Stroke(w * 0.05f, cap = StrokeCap.Round))
                val hand = Offset(shoulder.x + w * 0.295f, shoulder.y - w * 0.19f)
                if (p.isDark) drawCircle(LumiColors.ArmRim, w * 0.062f, hand)
                drawCircle(ink, w * 0.045f, hand)
            }
            // zweiter Arm hängt locker
            val shoulderL = P(0.38f, 0.55f)
            val armL = Path().apply {
                moveTo(shoulderL.x, shoulderL.y)
                quadraticBezierTo(shoulderL.x - w * 0.13f, shoulderL.y + w * 0.05f, shoulderL.x - w * 0.11f, shoulderL.y + w * 0.20f)
            }
            if (p.isDark) drawPath(armL, LumiColors.ArmRim, style = Stroke(w * 0.085f, cap = StrokeCap.Round))
            drawPath(armL, ink, style = Stroke(w * 0.05f, cap = StrokeCap.Round))
            val handL = Offset(shoulderL.x - w * 0.11f, shoulderL.y + w * 0.21f)
            if (p.isDark) drawCircle(LumiColors.ArmRim, w * 0.062f, handL)
            drawCircle(ink, w * 0.045f, handL)

            drawBulb(w, ::P, glass, glassAlt, face, outline, flat || cartoon, active)
        }
    }
}

private fun DrawScope.drawBulb(
    w: Float,
    P: (Float, Float) -> Offset,
    glass: Color,
    glassAlt: Color,
    face: Color,
    outline: Color?,
    solid: Boolean,
    active: Boolean,
) {
    // Kolben wie eine echte Glühbirne: runde Kugel, weich auslaufender Hals
    val c = P(0.5f, 0.33f)
    val r = w * 0.25f
    val body = Path().apply {
        // Kugel von rechts unten (30° unter der Waagerechten) über oben nach links unten
        arcTo(Rect(c.x - r, c.y - r, c.x + r, c.y + r), 30f, -240f, true)
        val jl = Offset(c.x - r * 0.866f, c.y + r * 0.5f)
        cubicTo(jl.x + w * 0.05f, jl.y + w * 0.09f, P(0.37f, 0.58f).x, P(0.37f, 0.58f).y, P(0.367f, 0.645f).x, P(0.367f, 0.645f).y)
        lineTo(P(0.633f, 0.645f).x, P(0.633f, 0.645f).y)
        val jr = Offset(c.x + r * 0.866f, c.y + r * 0.5f)
        cubicTo(P(0.63f, 0.58f).x, P(0.63f, 0.58f).y, jr.x - w * 0.05f, jr.y + w * 0.09f, jr.x, jr.y)
        close()
    }
    val fill = if (solid) Brush.linearGradient(listOf(glass, glass))
    else Brush.linearGradient(listOf(glassAlt, glass), start = P(0.3f, 0.08f), end = P(0.7f, 0.65f))
    drawPath(body, fill)
    if (!solid) {
        // Lichtkante oben links
        drawArc(
            Color.White.copy(alpha = if (active) 0.55f else 0.25f),
            startAngle = 200f, sweepAngle = 50f, useCenter = false,
            topLeft = P(0.31f, 0.14f), size = Size(w * 0.38f, w * 0.38f),
            style = Stroke(w * 0.032f, cap = StrokeCap.Round),
        )
    }
    if (outline != null) drawPath(body, outline, style = Stroke(w * 0.028f, join = StrokeJoin.Round))

    // Gewinde: Kragen, drei Gänge, schwarzes Isolierglas, Kontakt
    val metal = LumiColors.Metal
    val metalDark = LumiColors.MetalDark
    val cr = androidx.compose.ui.geometry.CornerRadius(w * 0.022f)
    drawRoundRect(metal, topLeft = P(0.355f, 0.64f), size = Size(w * 0.29f, w * 0.035f), cornerRadius = cr)
    for ((i, y) in listOf(0.68f, 0.725f, 0.77f).withIndex()) {
        val half = 0.135f - i * 0.006f
        drawRoundRect(if (i % 2 == 0) metal else metalDark, topLeft = P(0.5f - half, y), size = Size(w * half * 2f, w * 0.042f), cornerRadius = cr)
    }
    drawRoundRect(LumiColors.Insulator, topLeft = P(0.40f, 0.815f), size = Size(w * 0.20f, w * 0.035f), cornerRadius = cr)
    drawRoundRect(metalDark, topLeft = P(0.455f, 0.848f), size = Size(w * 0.09f, w * 0.035f), cornerRadius = cr)

    // Gesicht
    val eyeY = 0.35f
    if (active) {
        drawOval(face, topLeft = P(0.41f, eyeY - 0.04f), size = Size(w * 0.046f, w * 0.06f))
        drawOval(face, topLeft = P(0.544f, eyeY - 0.04f), size = Size(w * 0.046f, w * 0.06f))
        val shine = if (face == Color.White) LumiColors.Ink else Color.White
        drawCircle(shine, w * 0.01f, P(0.441f, eyeY - 0.022f))
        drawCircle(shine, w * 0.01f, P(0.575f, eyeY - 0.022f))
        val smile = Path().apply {
            moveTo(P(0.458f, 0.415f).x, P(0.458f, 0.415f).y)
            quadraticBezierTo(P(0.5f, 0.46f).x, P(0.5f, 0.46f).y, P(0.542f, 0.415f).x, P(0.542f, 0.415f).y)
        }
        drawPath(smile, face, style = Stroke(w * 0.022f, cap = StrokeCap.Round))
        drawOval(LumiColors.Cheek.copy(alpha = 0.55f), topLeft = P(0.355f, 0.395f), size = Size(w * 0.055f, w * 0.032f))
        drawOval(LumiColors.Cheek.copy(alpha = 0.55f), topLeft = P(0.59f, 0.395f), size = Size(w * 0.055f, w * 0.032f))
    } else {
        // aus: Augen geschlossen
        for (x in listOf(0.433f, 0.567f)) {
            val lid = Path().apply {
                moveTo(P(x - 0.028f, eyeY).x, P(x - 0.028f, eyeY).y)
                quadraticBezierTo(P(x, eyeY + 0.024f).x, P(x, eyeY + 0.024f).y, P(x + 0.028f, eyeY).x, P(x + 0.028f, eyeY).y)
            }
            drawPath(lid, face.copy(alpha = 0.7f), style = Stroke(w * 0.02f, cap = StrokeCap.Round))
        }
    }
}

private fun lerpTo(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f,
)
