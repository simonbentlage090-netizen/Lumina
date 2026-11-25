package de.funke.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.InterDisplay
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.tr

/*
 * Weitere Timer-Ziffern (seit 11.0). Beide sind bewusst ruhig: Bewegung, die immer wieder neu einsetzt,
 * zieht den Blick vom Buch weg (Abrams & Christ 2003, "Motion onset captures attention").
 *
 * - Klappzahlen: wie eine alte Bahnhofsuhr. Groß stehen nur die Minuten, sie klappen einmal pro Minute um.
 *   Sekunden gibt es erst in der letzten Minute.
 * - Terminal: grüne Schrift auf einem alten Computer, mit feinen Bildzeilen und einem stillen Cursor.
 */

// ---------------------------------------------------------------------------------------------
// Klappzahlen
// ---------------------------------------------------------------------------------------------

/**
 * Klappzahlen für die verbleibende Zeit [seconds]. Unter einer Stunde: zwei Karten mit den Minuten,
 * darunter "Min". Ab einer Stunde: Stunden und Minuten. In der letzten Minute: die Sekunden.
 */
@Composable
fun FlipDigits(seconds: Int, modifier: Modifier = Modifier, cardHeight: Dp = 84.dp) {
    val s = seconds.coerceAtLeast(0)
    val lastMinute = s < 60
    val minutesLeft = (s + 59) / 60
    val text: String
    val unit: String
    when {
        lastMinute -> { text = "%02d".format(s); unit = tr("Sek", "sec") }
        minutesLeft >= 60 -> { text = "%d:%02d".format(minutesLeft / 60, minutesLeft % 60); unit = tr("Std : Min", "h : min") }
        else -> { text = "%02d".format(minutesLeft); unit = tr("Min", "min") }
    }
    val p = Funke.colors
    Column(
        modifier.semantics { contentDescription = tr("Noch $text $unit", "$text $unit left") },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            text.forEach { c ->
                if (c == ':') {
                    Text(":", style = flipStyle(cardHeight, de.funke.app.ui.theme.LocalFontSet.current.let { f -> if (f.numbers == de.funke.app.ui.theme.Inter) InterDisplay else f.numbers }), color = p.textTertiary)
                } else {
                    FlipCard(c, cardHeight)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(unit, style = androidx.compose.material3.MaterialTheme.typography.labelMedium, color = p.textTertiary)
    }
}

private fun flipStyle(h: Dp, family: androidx.compose.ui.text.font.FontFamily = InterDisplay) = TextStyle(
    fontFamily = family,
    fontWeight = FontWeight.Medium,
    fontSize = (h.value * 0.66f).sp,
    lineHeight = (h.value * 0.66f).sp,
    textAlign = TextAlign.Center,
    fontFeatureSettings = "tnum, ss03",
)

/** Eine Karte. Ändert sich [digit], klappt die obere Hälfte nach unten um (420 ms). */
@Composable
private fun FlipCard(digit: Char, height: Dp) {
    val p = Funke.colors
    val width = height * 0.72f
    val still = de.funke.app.ui.theme.reducedMotion
    var old by remember { mutableStateOf(digit) }
    var new by remember { mutableStateOf(digit) }
    val flip = remember { Animatable(1f) }
    LaunchedEffect(digit) {
        if (digit == new) return@LaunchedEffect
        old = new
        new = digit
        if (still) {
            flip.snapTo(1f)
        } else {
            flip.snapTo(0f)
            flip.animateTo(1f, tween(420, easing = Motion.EaseInOut))
        }
        old = digit
    }
    val t = flip.value
    val card = if (p.isDark) lerp(p.surface, Color.White, 0.05f) else lerp(p.surface, Color.Black, 0.04f)
    val cardLow = if (p.isDark) lerp(card, Color.Black, 0.18f) else lerp(card, Color.Black, 0.05f)
    val ink = p.text
    val shape = RoundedCornerShape(10.dp)
    val half = height / 2

    @Composable
    fun Half(ch: Char, top: Boolean, shade: Float = 0f) {
        Box(
            Modifier
                .size(width, half)
                .clipToBounds()
                .background(if (top) card else cardLow),
        ) {
            Box(
                Modifier
                    .requiredHeight(height)
                    .width(width)
                    .offset(y = if (top) half / 2 else -half / 2),
                contentAlignment = Alignment.Center,
            ) {
                Text(ch.toString(), style = flipStyle(height, de.funke.app.ui.theme.LocalFontSet.current.let { f -> if (f.numbers == de.funke.app.ui.theme.Inter) InterDisplay else f.numbers }), color = ink)
            }
            if (shade > 0.001f) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f * shade)))
        }
    }

    Box(Modifier.size(width, height).clip(shape)) {
        Column {
            // oben: schon die neue Zahl (wird von der Klappe verdeckt), unten: noch die alte, bis die Klappe landet
            Half(new, top = true)
            Half(old, top = false)
        }
        // Die Klappe
        if (t < 1f) {
            if (t < 0.5f) {
                Box(
                    Modifier.graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        rotationX = -180f * t
                        cameraDistance = 12f * density
                    }
                ) { Half(old, top = true, shade = t * 2f) }
            } else {
                Box(
                    Modifier
                        .offset(y = half)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0f)
                            rotationX = 180f * (1f - t)
                            cameraDistance = 12f * density
                        }
                ) { Half(new, top = false, shade = (1f - t) * 2f) }
            }
        }
        // Spalt in der Mitte
        Box(
            Modifier
                .offset(y = half - 0.75.dp)
                .size(width, 1.5.dp)
                .background(p.bg.copy(alpha = 0.85f))
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Terminal
// ---------------------------------------------------------------------------------------------

/** Phosphor-Grün, gut lesbar auf Dunkel; im Hellen ein tiefes Grün. */
@Composable
private fun phosphor(): Color = if (Funke.colors.isDark) Color(0xFF6CFFA0) else Color(0xFF0B7A3B)

/**
 * Restzeit wie auf einem alten Computer: eine leise Befehlszeile, darunter groß die Zeit in Monospace
 * mit einem stehenden Block-Cursor (blinkt nicht, damit er nicht ablenkt). Feine Bildzeilen darüber.
 */
@Composable
fun TerminalDigits(seconds: Int, modifier: Modifier = Modifier, big: Dp = 52.dp) {
    val s = seconds.coerceAtLeast(0)
    val h = s / 3600
    val time = if (h > 0) "%d:%02d:%02d".format(h, (s % 3600) / 60, s % 60) else "%02d:%02d".format(s / 60, s % 60)
    val green = phosphor()
    val dark = Funke.colors.isDark
    val glow = Shadow(green.copy(alpha = if (dark) 0.55f else 0.0f), Offset.Zero, if (dark) 18f else 0f)
    val mono = FontFamily.Monospace
    Box(
        modifier.semantics { contentDescription = tr("Noch $time", "$time left") },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                "> " + tr("fokus", "focus") + " --still",
                style = TextStyle(fontFamily = mono, fontSize = 13.sp, color = green.copy(alpha = 0.55f), shadow = glow),
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    time,
                    maxLines = 1,
                    softWrap = false,
                    style = TextStyle(
                        fontFamily = mono,
                        fontWeight = FontWeight.Medium,
                        fontSize = (big.value * if (h > 0) 0.68f else 0.86f).sp,
                        letterSpacing = (-1).sp,
                        color = green,
                        shadow = glow,
                    ),
                )
                Spacer(Modifier.width(6.dp))
                val k = if (h > 0) 0.8f else 1f
                Box(Modifier.size(width = (big.value * 0.32f * k).dp, height = (big.value * 0.62f * k).dp).background(green.copy(alpha = 0.85f)))
            }
        }
        // Bildzeilen: ganz fein, nur im Dunkeln
        if (dark) {
            Canvas(Modifier.matchParentSize()) {
                val step = 3.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawRect(Color.Black.copy(alpha = 0.16f), topLeft = Offset(0f, y), size = androidx.compose.ui.geometry.Size(size.width, 1f))
                    y += step
                }
            }
        }
    }
}
