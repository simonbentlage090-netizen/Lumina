package de.funke.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Schlichte, einfarbige Linien-Symbole für die App-Gruppen (statt bunter Emojis), alle im selben Strich.
 * Koordinaten in 0..1 einer quadratischen Fläche.
 */
@Composable
fun GroupIcon(icon: String, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) { drawGroupIcon(icon, color) }
}

private inline fun DrawScope.rotateAround(deg: Float, crossinline block: DrawScope.() -> Unit) =
    rotate(deg, center) { block() }

fun DrawScope.drawGroupIcon(icon: String, color: Color) {
    val w = size.minDimension
    val sw = w * 0.085f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    fun o(x: Float, y: Float) = Offset(x * w, y * w)
    fun line(x0: Float, y0: Float, x1: Float, y1: Float) = drawLine(color, o(x0, y0), o(x1, y1), sw, StrokeCap.Round)
    fun rrect(x: Float, y: Float, rw: Float, rh: Float, r: Float) =
        drawRoundRect(color, o(x, y), Size(rw * w, rh * w), CornerRadius(r * w, r * w), style = stroke)
    fun circle(x: Float, y: Float, r: Float, fill: Boolean = false) =
        if (fill) drawCircle(color, r * w, o(x, y)) else drawCircle(color, r * w, o(x, y), style = stroke)
    fun path(block: Path.() -> Unit) = drawPath(Path().apply(block), color, style = stroke)
    fun fillPath(block: Path.() -> Unit) = drawPath(Path().apply(block), color)

    when (icon) {
        "video" -> {
            rrect(0.12f, 0.24f, 0.76f, 0.52f, 0.10f)
            fillPath { moveTo(0.44f * w, 0.39f * w); lineTo(0.62f * w, 0.50f * w); lineTo(0.44f * w, 0.61f * w); close() }
        }
        "social" -> {
            circle(0.38f, 0.36f, 0.12f)
            path { moveTo(0.16f * w, 0.78f * w); cubicTo(0.18f * w, 0.56f * w, 0.58f * w, 0.56f * w, 0.60f * w, 0.78f * w) }
            circle(0.66f, 0.32f, 0.09f)
            path { moveTo(0.68f * w, 0.52f * w); cubicTo(0.80f * w, 0.52f * w, 0.86f * w, 0.60f * w, 0.86f * w, 0.70f * w) }
        }
        "chat" -> path {
            moveTo(0.20f * w, 0.30f * w)
            quadraticBezierTo(0.20f * w, 0.18f * w, 0.32f * w, 0.18f * w)
            lineTo(0.70f * w, 0.18f * w)
            quadraticBezierTo(0.82f * w, 0.18f * w, 0.82f * w, 0.30f * w)
            lineTo(0.82f * w, 0.56f * w)
            quadraticBezierTo(0.82f * w, 0.68f * w, 0.70f * w, 0.68f * w)
            lineTo(0.42f * w, 0.68f * w)
            lineTo(0.26f * w, 0.82f * w)
            lineTo(0.28f * w, 0.68f * w)
            quadraticBezierTo(0.20f * w, 0.66f * w, 0.20f * w, 0.56f * w)
            close()
        }
        "games" -> {
            rrect(0.10f, 0.30f, 0.80f, 0.42f, 0.20f)
            line(0.28f, 0.51f, 0.42f, 0.51f)
            line(0.35f, 0.44f, 0.35f, 0.58f)
            circle(0.62f, 0.46f, 0.035f, fill = true)
            circle(0.71f, 0.56f, 0.035f, fill = true)
        }
        "music" -> {
            path { moveTo(0.18f * w, 0.58f * w); lineTo(0.18f * w, 0.48f * w); cubicTo(0.18f * w, 0.10f * w, 0.82f * w, 0.10f * w, 0.82f * w, 0.48f * w); lineTo(0.82f * w, 0.58f * w) }
            rrect(0.12f, 0.54f, 0.16f, 0.26f, 0.06f)
            rrect(0.72f, 0.54f, 0.16f, 0.26f, 0.06f)
        }
        "learn" -> {
            // aufgeschlagenes Buch
            path { moveTo(0.50f * w, 0.30f * w); quadraticBezierTo(0.32f * w, 0.20f * w, 0.12f * w, 0.24f * w); lineTo(0.12f * w, 0.74f * w); quadraticBezierTo(0.32f * w, 0.70f * w, 0.50f * w, 0.80f * w) }
            path { moveTo(0.50f * w, 0.30f * w); quadraticBezierTo(0.68f * w, 0.20f * w, 0.88f * w, 0.24f * w); lineTo(0.88f * w, 0.74f * w); quadraticBezierTo(0.68f * w, 0.70f * w, 0.50f * w, 0.80f * w) }
            line(0.50f, 0.30f, 0.50f, 0.80f)
        }
        "browser" -> {
            circle(0.50f, 0.50f, 0.34f)
            drawOval(color, o(0.36f, 0.16f), Size(0.28f * w, 0.68f * w), style = stroke)
            line(0.17f, 0.50f, 0.83f, 0.50f)
        }
        "news" -> {
            rrect(0.16f, 0.20f, 0.68f, 0.60f, 0.08f)
            drawRect(color, o(0.28f, 0.32f), Size(0.18f * w, 0.14f * w))
            line(0.54f, 0.34f, 0.72f, 0.34f)
            line(0.54f, 0.45f, 0.72f, 0.45f)
            line(0.28f, 0.58f, 0.72f, 0.58f)
            line(0.28f, 0.68f, 0.60f, 0.68f)
        }
        "shopping" -> {
            path { moveTo(0.20f * w, 0.36f * w); lineTo(0.80f * w, 0.36f * w); lineTo(0.75f * w, 0.84f * w); lineTo(0.25f * w, 0.84f * w); close() }
            path { moveTo(0.36f * w, 0.42f * w); lineTo(0.36f * w, 0.30f * w); cubicTo(0.36f * w, 0.12f * w, 0.64f * w, 0.12f * w, 0.64f * w, 0.30f * w); lineTo(0.64f * w, 0.42f * w) }
        }
        "finance" -> {
            rrect(0.10f, 0.26f, 0.80f, 0.50f, 0.08f)
            line(0.10f, 0.40f, 0.90f, 0.40f)
            line(0.22f, 0.62f, 0.42f, 0.62f)
        }
        "photo" -> {
            path {
                moveTo(0.14f * w, 0.36f * w); lineTo(0.30f * w, 0.36f * w); lineTo(0.38f * w, 0.24f * w); lineTo(0.62f * w, 0.24f * w)
                lineTo(0.70f * w, 0.36f * w); lineTo(0.86f * w, 0.36f * w); lineTo(0.86f * w, 0.78f * w); lineTo(0.14f * w, 0.78f * w); close()
            }
            circle(0.50f, 0.56f, 0.13f)
        }
        "travel" -> {
            path {
                moveTo(0.50f * w, 0.88f * w)
                cubicTo(0.30f * w, 0.62f * w, 0.20f * w, 0.50f * w, 0.20f * w, 0.40f * w)
                cubicTo(0.20f * w, 0.20f * w, 0.36f * w, 0.10f * w, 0.50f * w, 0.10f * w)
                cubicTo(0.64f * w, 0.10f * w, 0.80f * w, 0.20f * w, 0.80f * w, 0.40f * w)
                cubicTo(0.80f * w, 0.50f * w, 0.70f * w, 0.62f * w, 0.50f * w, 0.88f * w)
                close()
            }
            circle(0.50f, 0.40f, 0.10f)
        }
        "tools" -> {
            // drei Regler
            line(0.18f, 0.30f, 0.82f, 0.30f); circle(0.36f, 0.30f, 0.07f, fill = true)
            line(0.18f, 0.50f, 0.82f, 0.50f); circle(0.64f, 0.50f, 0.07f, fill = true)
            line(0.18f, 0.70f, 0.82f, 0.70f); circle(0.44f, 0.70f, 0.07f, fill = true)
        }
        "star" -> path {
            for (i in 0 until 10) {
                val a = (-90f + i * 36f) * PI.toFloat() / 180f
                val r = if (i % 2 == 0) 0.38f else 0.16f
                val x = (0.5f + r * cos(a)) * w
                val y = (0.53f + r * sin(a)) * w
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        "heart" -> path {
            moveTo(0.50f * w, 0.82f * w)
            cubicTo(0.20f * w, 0.62f * w, 0.10f * w, 0.44f * w, 0.18f * w, 0.30f * w)
            cubicTo(0.28f * w, 0.14f * w, 0.46f * w, 0.20f * w, 0.50f * w, 0.32f * w)
            cubicTo(0.54f * w, 0.20f * w, 0.72f * w, 0.14f * w, 0.82f * w, 0.30f * w)
            cubicTo(0.90f * w, 0.44f * w, 0.80f * w, 0.62f * w, 0.50f * w, 0.82f * w)
            close()
        }
        "bolt" -> path {
            moveTo(0.56f * w, 0.10f * w); lineTo(0.26f * w, 0.54f * w); lineTo(0.48f * w, 0.54f * w)
            lineTo(0.42f * w, 0.90f * w); lineTo(0.74f * w, 0.44f * w); lineTo(0.52f * w, 0.44f * w); close()
        }
        "moon" -> path {
            moveTo(0.62f * w, 0.14f * w)
            cubicTo(0.30f * w, 0.18f * w, 0.18f * w, 0.50f * w, 0.30f * w, 0.70f * w)
            cubicTo(0.44f * w, 0.90f * w, 0.74f * w, 0.88f * w, 0.86f * w, 0.66f * w)
            cubicTo(0.62f * w, 0.70f * w, 0.44f * w, 0.48f * w, 0.62f * w, 0.14f * w)
            close()
        }
        "leaf" -> {
            path { moveTo(0.20f * w, 0.80f * w); cubicTo(0.16f * w, 0.36f * w, 0.48f * w, 0.18f * w, 0.84f * w, 0.18f * w); cubicTo(0.84f * w, 0.56f * w, 0.62f * w, 0.84f * w, 0.20f * w, 0.80f * w); close() }
            line(0.24f, 0.76f, 0.62f, 0.40f)
        }
        "ball" -> {
            circle(0.50f, 0.50f, 0.34f)
            drawArc(color, -60f, 120f, false, o(0.02f, 0.16f), Size(0.68f * w, 0.68f * w), style = stroke)
            drawArc(color, 120f, 120f, false, o(0.30f, 0.16f), Size(0.68f * w, 0.68f * w), style = stroke)
        }
        "brush" -> {
            line(0.80f, 0.16f, 0.46f, 0.52f)
            path { moveTo(0.44f * w, 0.50f * w); cubicTo(0.30f * w, 0.48f * w, 0.22f * w, 0.60f * w, 0.24f * w, 0.70f * w); cubicTo(0.24f * w, 0.78f * w, 0.18f * w, 0.84f * w, 0.14f * w, 0.84f * w); cubicTo(0.34f * w, 0.90f * w, 0.52f * w, 0.78f * w, 0.50f * w, 0.58f * w) }
        }
        "food" -> {
            line(0.30f, 0.14f, 0.30f, 0.86f)
            path { moveTo(0.20f * w, 0.14f * w); lineTo(0.20f * w, 0.34f * w); quadraticBezierTo(0.20f * w, 0.44f * w, 0.30f * w, 0.44f * w); quadraticBezierTo(0.40f * w, 0.44f * w, 0.40f * w, 0.34f * w); lineTo(0.40f * w, 0.14f * w) }
            path { moveTo(0.68f * w, 0.86f * w); lineTo(0.68f * w, 0.14f * w); cubicTo(0.84f * w, 0.20f * w, 0.84f * w, 0.48f * w, 0.68f * w, 0.54f * w) }
        }
        "math" -> {
            line(0.20f, 0.30f, 0.40f, 0.30f); line(0.30f, 0.20f, 0.30f, 0.40f)          // +
            line(0.60f, 0.30f, 0.80f, 0.30f)                                            // −
            line(0.22f, 0.62f, 0.38f, 0.78f); line(0.38f, 0.62f, 0.22f, 0.78f)          // ×
            line(0.60f, 0.65f, 0.80f, 0.65f); line(0.60f, 0.75f, 0.80f, 0.75f)          // =
        }
        "atom" -> {
            circle(0.50f, 0.50f, 0.07f, fill = true)
            for (deg in floatArrayOf(0f, 60f, 120f)) {
                rotateAround(deg) { drawOval(color, o(0.12f, 0.36f), Size(0.76f * w, 0.28f * w), style = stroke) }
            }
        }
        "bulb" -> {
            path {
                moveTo(0.38f * w, 0.66f * w)
                cubicTo(0.38f * w, 0.56f * w, 0.22f * w, 0.50f * w, 0.22f * w, 0.36f * w)
                cubicTo(0.22f * w, 0.20f * w, 0.36f * w, 0.10f * w, 0.50f * w, 0.10f * w)
                cubicTo(0.64f * w, 0.10f * w, 0.78f * w, 0.20f * w, 0.78f * w, 0.36f * w)
                cubicTo(0.78f * w, 0.50f * w, 0.62f * w, 0.56f * w, 0.62f * w, 0.66f * w)
                close()
            }
            line(0.40f, 0.78f, 0.60f, 0.78f)
            line(0.44f, 0.89f, 0.56f, 0.89f)
        }
        "flask" -> {
            path {
                moveTo(0.40f * w, 0.12f * w); lineTo(0.40f * w, 0.40f * w); lineTo(0.18f * w, 0.80f * w)
                quadraticBezierTo(0.16f * w, 0.88f * w, 0.26f * w, 0.88f * w); lineTo(0.74f * w, 0.88f * w)
                quadraticBezierTo(0.84f * w, 0.88f * w, 0.82f * w, 0.80f * w); lineTo(0.60f * w, 0.40f * w); lineTo(0.60f * w, 0.12f * w)
            }
            line(0.34f, 0.12f, 0.66f, 0.12f)
            line(0.30f, 0.64f, 0.70f, 0.64f)
        }
        "hourglass" -> {
            line(0.28f, 0.16f, 0.72f, 0.16f)
            line(0.28f, 0.84f, 0.72f, 0.84f)
            path {
                moveTo(0.34f * w, 0.16f * w); quadraticBezierTo(0.34f * w, 0.42f * w, 0.50f * w, 0.50f * w)
                quadraticBezierTo(0.66f * w, 0.58f * w, 0.66f * w, 0.84f * w)
                moveTo(0.66f * w, 0.16f * w); quadraticBezierTo(0.66f * w, 0.42f * w, 0.50f * w, 0.50f * w)
                quadraticBezierTo(0.34f * w, 0.58f * w, 0.34f * w, 0.84f * w)
            }
        }
        "mountain" -> {
            path {
                moveTo(0.12f * w, 0.78f * w); lineTo(0.40f * w, 0.28f * w); lineTo(0.56f * w, 0.54f * w)
                lineTo(0.66f * w, 0.40f * w); lineTo(0.88f * w, 0.78f * w); close()
            }
            line(0.30f, 0.62f, 0.48f, 0.62f)
        }
        "think" -> {
            path {
                moveTo(0.34f * w, 0.34f * w); cubicTo(0.34f * w, 0.14f * w, 0.66f * w, 0.14f * w, 0.66f * w, 0.34f * w)
                cubicTo(0.66f * w, 0.48f * w, 0.50f * w, 0.48f * w, 0.50f * w, 0.62f * w)
            }
            circle(0.50f, 0.80f, 0.055f, fill = true)
        }
        else -> {
            circle(0.26f, 0.50f, 0.065f, fill = true)
            circle(0.50f, 0.50f, 0.065f, fill = true)
            circle(0.74f, 0.50f, 0.065f, fill = true)
        }
    }
}
