package de.funke.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.funke.app.ui.RingStyle
import de.funke.app.ui.theme.Funke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

/**
 * Der Ring um den Timer in der gewählten Variante. [progress] = verbleibender Anteil (1 = voll, 0 = leer).
 * Die verbleibende Zeit läuft wie beim klassischen Ring von oben im Uhrzeigersinn.
 * Alle Größen hängen von [stroke] ab, so passt dieselbe Zeichnung auch als kleine Vorschau.
 */
@Composable
fun TimerRing(
    progress: Float,
    style: RingStyle,
    modifier: Modifier = Modifier,
    stroke: Dp = 12.dp,
) {
    if (style == RingStyle.Classic) {
        ProgressRing(progress, modifier, stroke)
        return
    }
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val solid = cartoon || flat
    // Zeit in Sekunden (0..60). Alle Bewegungen wiederholen sich genau einmal pro Minute ohne Sprung.
    val time = rememberLoop(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing)),
        label = "ringT",
    )
    Canvas(modifier) {
        val ring = RingPaint(
            prog = progress.coerceIn(0f, 1f),
            t = time.value,
            sw = stroke.toPx(),
            track = if (cartoon) p.ink.copy(alpha = 0.12f) else p.glassAlpha(0.09f),
            accent = p.accent,
            alt = if (solid) p.accent else p.accentAlt,
            head = p.text,
            ink = if (cartoon) p.ink else null,
            glow = !flat, // Flat: kein weiches Leuchten (der Neon-Ring glüht trotzdem, das ist sein Sinn)
        )
        when (style) {
            RingStyle.Ticks -> ring.ticks(this)
            RingStyle.Comet -> ring.comet(this)
            RingStyle.Beads -> ring.beads(this)
            RingStyle.Neon -> ring.neon(this)
            RingStyle.Rays -> ring.rays(this)
            RingStyle.Wave -> ring.wave(this)
            RingStyle.Classic -> Unit
        }
    }
}

private const val DEG = (PI / 180.0).toFloat()
private const val TWO_PI = (2.0 * PI).toFloat()

private fun smoothstep(x: Float): Float {
    val c = x.coerceIn(0f, 1f)
    return c * c * (3f - 2f * c)
}

private class RingPaint(
    val prog: Float,
    val t: Float,
    val sw: Float,
    val track: Color,
    val accent: Color,
    val alt: Color,
    val head: Color,
    val ink: Color?,
    val glow: Boolean,
) {
    fun col(f: Float) = lerp(accent, alt, f.coerceIn(0f, 1f))

    private fun DrawScope.polar(r: Float, deg: Float) =
        Offset(center.x + r * cos(deg * DEG), center.y + r * sin(deg * DEG))

    private fun DrawScope.softDot(at: Offset, radius: Float, color: Color, alpha: Float) {
        drawCircle(
            Brush.radialGradient(listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)), center = at, radius = radius),
            radius = radius,
            center = at,
        )
    }

    /** Skala: 60 Striche wie bei einer Uhr; jeder fünfte länger. Was noch übrig ist, leuchtet. */
    fun ticks(d: DrawScope) = with(d) {
        val n = 60
        val rOut = size.minDimension / 2f - sw * 0.15f
        for (i in 0 until n) {
            val f = (prog * n - i).coerceIn(0f, 1f)
            val major = i % 5 == 0
            val len = sw * (if (major) 1.5f else 0.95f) * (0.55f + 0.45f * f)
            val w = sw * (if (major) 0.30f else 0.22f)
            val base = track.copy(alpha = (track.alpha * if (major) 1.8f else 1.1f).coerceAtMost(1f))
            val a = -90f + i * 6f
            drawLine(lerp(base, col(i / n.toFloat()), f), polar(rOut, a), polar(rOut - len, a), strokeWidth = w, cap = StrokeCap.Round)
        }
        if (prog > 0.002f && glow) softDot(polar(rOut - sw * 0.7f, -90f + 360f * prog), sw * 1.8f, accent, 0.35f)
    }

    /** Komet: ein Lichtschweif kreist einmal pro Minute; darunter ein feiner Bogen für die übrige Zeit. */
    fun comet(d: DrawScope) = with(d) {
        val r = size.minDimension / 2f - sw / 2f
        val tl = Offset(center.x - r, center.y - r)
        val sz = Size(2f * r, 2f * r)
        drawCircle(track, r, center, style = Stroke(sw * 0.25f))
        if (prog > 0.002f) {
            drawArc(accent.copy(alpha = 0.6f), -90f, 360f * prog, false, tl, sz, style = Stroke(sw * 0.25f, cap = StrokeCap.Round))
        }
        val headDeg = -90f + (t % 60f) / 60f * 360f
        val n = 48
        for (i in 0 until n) {
            val k = i / n.toFloat()
            val a1 = headDeg - 110f * k
            val a0 = headDeg - 110f * (k + 1f / n)
            val w = sw * (0.95f * (1f - k).pow(1.4f) + 0.08f)
            drawArc(col(k).copy(alpha = (1f - k).pow(1.6f)), a0, (a1 - a0) + 0.6f, false, tl, sz, style = Stroke(w, cap = StrokeCap.Butt))
        }
        val hp = polar(r, headDeg)
        if (glow) softDot(hp, sw * 2.0f, accent, 0.55f)
        drawCircle(head, sw * 0.48f, hp)
        ink?.let { drawCircle(it, sw * 0.48f, hp, style = Stroke(sw * 0.16f)) }
    }

    /** Perlen: 40 Punkte; die vorderste leuchtende Perle atmet leise. */
    fun beads(d: DrawScope) = with(d) {
        val n = 40
        val r = size.minDimension / 2f - sw / 2f
        val dim = track.copy(alpha = (track.alpha * 1.6f).coerceAtMost(1f))
        for (i in 0 until n) {
            val f = (prog * n - i).coerceIn(0f, 1f)
            val at = polar(r, -90f + 360f * (i + 0.5f) / n)
            val rad = sw * (0.16f + 0.24f * f)
            drawCircle(lerp(dim, col(i / n.toFloat()), f), rad, at)
            if (ink != null && f > 0.5f) drawCircle(ink, rad, at, style = Stroke(sw * 0.12f))
        }
        if (prog > 0.002f) {
            val i = floor(prog * n - 1e-4f).toInt().coerceIn(0, n - 1)
            val at = polar(r, -90f + 360f * (i + 0.5f) / n)
            val pulse = 0.5f + 0.5f * sin(t * TWO_PI / 1.5f)
            if (glow) softDot(at, sw * (1.0f + 0.5f * pulse), accent, 0.45f)
            drawCircle(head, sw * (0.40f + 0.10f * pulse), at)
            ink?.let { drawCircle(it, sw * (0.40f + 0.10f * pulse), at, style = Stroke(sw * 0.14f)) }
        }
    }

    /** Neon: glühender Ring aus mehreren weichen Schichten, pulsiert langsam. */
    fun neon(d: DrawScope) = with(d) {
        val r = size.minDimension / 2f - sw / 2f
        val tl = Offset(center.x - r, center.y - r)
        val sz = Size(2f * r, 2f * r)
        drawCircle(track.copy(alpha = track.alpha * 0.5f), r, center, style = Stroke(sw * 0.4f))
        if (prog <= 0.002f) return@with
        val sweep = 360f * prog
        val pulse = 0.85f + 0.15f * sin(t * TWO_PI / 4f)
        for ((w, a) in listOf(3.4f to 0.05f, 2.5f to 0.08f, 1.7f to 0.14f, 1.1f to 0.24f)) {
            drawArc(accent.copy(alpha = a * pulse), -90f, sweep, false, tl, sz, style = Stroke(sw * w, cap = StrokeCap.Round))
        }
        drawArc(accent, -90f, sweep, false, tl, sz, style = Stroke(sw * 0.62f, cap = StrokeCap.Round))
        drawArc(lerp(accent, Color.White, 0.65f), -90f, sweep, false, tl, sz, style = Stroke(sw * 0.26f, cap = StrokeCap.Round))
    }

    /** Strahlen: Licht wie von einer Glühbirne; die Länge wogt in einer ruhigen Welle um den Ring. */
    fun rays(d: DrawScope) = with(d) {
        val n = 72
        val rIn = size.minDimension / 2f - sw * 1.8f
        val dim = track.copy(alpha = (track.alpha * 1.4f).coerceAtMost(1f))
        drawCircle(track.copy(alpha = track.alpha * 1.1f), rIn - sw * 0.55f, center, style = Stroke(sw * 0.18f))
        for (i in 0 until n) {
            val f = (prog * n - i).coerceIn(0f, 1f)
            val a = -90f + 360f * i / n
            // Langsames Wogen (eine Welle alle 12 s): Gleichmäßige Bewegung lenkt kaum ab, plötzliche schon.
            val wave = 0.5f + 0.5f * sin(a * DEG * 4f + t * TWO_PI / 12f)
            val len = sw * (0.35f + f * (0.70f + 0.70f * wave))
            drawLine(lerp(dim, col(i / n.toFloat()), f), polar(rIn, a), polar(rIn + len, a), strokeWidth = sw * 0.24f, cap = StrokeCap.Round)
        }
    }

    /** Welle: drei feine Lichtlinien, die sich wie Wasser umeinander bewegen. */
    fun wave(d: DrawScope) = with(d) {
        val r = size.minDimension / 2f - sw / 2f
        drawCircle(track.copy(alpha = track.alpha * 0.8f), r, center, style = Stroke(sw * 0.25f))
        if (prog <= 0.002f) return@with
        val sweep = 360f * prog
        val steps = max(24, (prog * 260f).toInt())
        val phaseT = t * TWO_PI / 4f
        val lines = listOf(Triple(0f, 1f, 0.30f), Triple(2.1f, 0.65f, 0.22f), Triple(4.2f, 0.40f, 0.18f))
        lines.forEachIndexed { k, (ph, alpha, width) ->
            val path = Path()
            for (s in 0..steps) {
                val a = -90f + sweep * s / steps
                val edge = min(a + 90f, sweep - (a + 90f))
                val amp = sw * 0.5f * smoothstep(edge / 20f)
                val rr = r + amp * sin(a * DEG * 5f - phaseT + ph)
                val pt = polar(rr, a)
                if (s == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
            }
            if (k == 0 && glow) {
                drawPath(path, accent.copy(alpha = 0.18f), style = Stroke(sw * 1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            drawPath(path, col(k / 2f).copy(alpha = alpha), style = Stroke(sw * width, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}
