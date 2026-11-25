package de.funke.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

// Kleine, selbst gezeichnete Symbole. So brauchen wir keine zusätzliche Icon-Bibliothek.

@Composable
fun CloseIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val stroke = Stroke(width = w * 0.10f, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.24f, w * 0.24f), Offset(w * 0.76f, w * 0.76f), strokeWidth = stroke.width, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.76f, w * 0.24f), Offset(w * 0.24f, w * 0.76f), strokeWidth = stroke.width, cap = StrokeCap.Round)
    }
}

@Composable
fun PlusIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val sw = w * 0.10f
        drawLine(color, Offset(w * 0.5f, w * 0.22f), Offset(w * 0.5f, w * 0.78f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.22f, w * 0.5f), Offset(w * 0.78f, w * 0.5f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun ChevronDownIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val path = Path().apply {
            moveTo(w * 0.24f, w * 0.38f)
            lineTo(w * 0.50f, w * 0.64f)
            lineTo(w * 0.76f, w * 0.38f)
        }
        drawPath(path, color, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Farbpalette: ein Kreis mit vier Farbtupfern. */
@Composable
fun PaletteIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        drawCircle(color, radius = w * 0.40f, center = center, style = Stroke(width = w * 0.08f))
        val r = w * 0.065f
        drawCircle(color, r, Offset(w * 0.36f, w * 0.40f))
        drawCircle(color, r, Offset(w * 0.55f, w * 0.30f))
        drawCircle(color, r, Offset(w * 0.68f, w * 0.48f))
        drawCircle(color, r, Offset(w * 0.46f, w * 0.64f))
    }
}

/** Vier Kacheln: "Modi". */
@Composable
fun ModesNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val s = w * 0.30f
        val gap = w * 0.12f
        val start = (w - (2 * s + gap)) / 2f
        for (row in 0..1) {
            for (col in 0..1) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(start + col * (s + gap), start + row * (s + gap)),
                    size = androidx.compose.ui.geometry.Size(s, s),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(s * 0.3f, s * 0.3f),
                    style = Stroke(width = w * 0.07f),
                )
            }
        }
    }
}

/** Zahnrad für die Einstellungen: Ring mit acht Zähnen. */
@Composable
fun GearIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val c = center
        val sw = w * 0.09f
        drawCircle(color, radius = w * 0.20f, center = c, style = Stroke(width = sw))
        drawCircle(color, radius = w * 0.30f, center = c, style = Stroke(width = sw))
        for (i in 0 until 8) {
            val angle = Math.toRadians(i * 45.0)
            val dx = Math.cos(angle).toFloat()
            val dy = Math.sin(angle).toFloat()
            drawLine(
                color,
                Offset(c.x + dx * w * 0.32f, c.y + dy * w * 0.32f),
                Offset(c.x + dx * w * 0.44f, c.y + dy * w * 0.44f),
                strokeWidth = sw * 1.4f,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Abspiel-Dreieck mit weichen Ecken. */
@Composable
fun PlayIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val path = Path().apply {
            moveTo(w * 0.34f, w * 0.22f)
            lineTo(w * 0.80f, w * 0.50f)
            lineTo(w * 0.34f, w * 0.78f)
            close()
        }
        drawPath(path, color)
        drawPath(path, color, style = Stroke(width = w * 0.10f, join = StrokeJoin.Round))
    }
}

/** Kleiner Pfeil nach rechts. */
@Composable
fun ChevronRightIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val path = Path().apply {
            moveTo(w * 0.40f, w * 0.26f)
            lineTo(w * 0.64f, w * 0.50f)
            lineTo(w * 0.40f, w * 0.74f)
        }
        drawPath(path, color, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Stift zum Bearbeiten. */
@Composable
fun EditIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val sw = w * 0.09f
        drawLine(color, Offset(w * 0.26f, w * 0.74f), Offset(w * 0.70f, w * 0.30f), strokeWidth = sw * 1.6f, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.22f, w * 0.80f), Offset(w * 0.30f, w * 0.80f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}
