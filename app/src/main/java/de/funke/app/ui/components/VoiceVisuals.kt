package de.funke.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import de.funke.app.ui.tr
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Sprach-Animationen fürs Diktieren: Balken, Kreis, Raster, Linie, Punkte und die Linien-Kugel.
 *
 * Nach Compose übertragen aus "chatcn" (Audio Visualizer und Voice Orb) von Aaditya,
 * https://chatcn.dev, MIT-Lizenz (siehe THIRD_PARTY_NOTICES.md). Formen, Zustände und Bewegung
 * entsprechen dem Original; gezeichnet wird mit dem Compose-Canvas statt mit HTML-Canvas.
 */

enum class VoiceVisual(val id: String, private val de: String, private val en: String) {
    Bars("bars", "Balken", "Bars"),
    Radial("radial", "Kreis", "Radial"),
    Grid("grid", "Raster", "Grid"),
    Dots("dots", "Punkte", "Dots"),
    Orb("orb", "Kugel", "Orb");

    val label: String get() = tr(de, en)

    companion object {
        /** Unbekannt (auch die alte "Linie") → Balken. */
        fun byId(id: String?): VoiceVisual = entries.firstOrNull { it.id == id } ?: Bars
    }
}

/** Kurve fürs Aufteilen: zügig, mit einem kleinen Nachfedern am Ende. */
internal val SplitEase = androidx.compose.animation.core.CubicBezierEasing(0.3f, 1.28f, 0.6f, 1f)

/** Abschnitt [a]..[b] von [t] als 0..1. */
internal fun span(t: Float, a: Float, b: Float) = ((t - a) / (b - a)).coerceIn(0f, 1f)

/** Was gerade passiert: Lumi wartet, verbindet sich, hört zu, denkt (Aufräumen), spricht … */
enum class VoiceState { Idle, Connecting, Listening, Thinking, Speaking, Interrupted, Muted, Error }

/** Bildwechsel-Takt für Animationen, die jedes Bild neu rechnen. */
@Composable
private fun rememberFrameNanos(): State<Long> {
    val t = remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { while (true) withFrameNanos { t.longValue = it } }
    return t
}

/**
 * Eine der Sprach-Animationen. [level] wird jedes Bild gelesen (0..1).
 * [birth] 0…1: Entstehung aus einer runden Kugel mit Radius [seed] in der Mitte (dort landet das fliegende
 * Mikrofon). Jede Animation entsteht anders: Balken und Punkte teilen sich auf, der Kreis entfaltet sich,
 * das Raster spritzt auseinander, die Kugel löst sich in Linien auf. 1 = fertig.
 */
@Composable
fun VoiceVisualizer(
    visual: VoiceVisual,
    state: VoiceState,
    level: () -> Float,
    color: Color,
    modifier: Modifier = Modifier,
    birth: Float = 1f,
    seed: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
) {
    if (visual == VoiceVisual.Orb) VoiceOrbCanvas(state, level, color, modifier, birth, seed)
    else BandsCanvas(visual, state, level, color, modifier, birth, seed)
}

// ---------------------------------------------------------------------------------------------
// Balken, Kreis, Raster, Linie, Punkte
// ---------------------------------------------------------------------------------------------

private fun clamp01(v: Float) = if (v.isFinite()) v.coerceIn(0f, 1f) else 0f
private fun dist(i: Int, count: Int) = if (count <= 1) 0f else abs(i - (count - 1) / 2f) / ((count - 1) / 2f)
private fun approach(current: Float, target: Float, rate: Float, dt: Float) = current + (target - current) * (1f - exp(-rate * dt))

/** Billiges, gleichbleibendes Rauschen aus drei Sinuswellen. */
private fun wobble(t: Float, seed: Float) =
    sin(t * 1.31f + seed * 7.13f) * 0.5f + sin(t * 2.17f + seed * 3.71f) * 0.3f + sin(t * 3.73f + seed * 1.37f) * 0.2f

/** Aus einer Lautstärke ein sprachähnliches Spektrum (Energie eher tief und in der Mitte). */
private fun synthesizeBands(level: Float, count: Int, t: Float) = FloatArray(count) { i ->
    val x = if (count == 1) 0.5f else i / (count - 1f)
    val hump = 0.45f + 0.55f * exp(-((x - 0.38f) / 0.32f).pow(2))
    val ripple = 0.72f + 0.28f * wobble(t * 2.4f, i * 0.77f + 1f)
    clamp01(level * hump * ripple * 1.25f)
}

/** Mit den Nachbarn mischen und zur Mitte hin betonen. */
private fun smooth(values: FloatArray, count: Int) = FloatArray(values.size) { i ->
    val v = values[i]
    val a = values.getOrElse(i - 1) { v }
    val b = values.getOrElse(i + 1) { v }
    (a * 0.25f + v * 0.5f + b * 0.25f) * (1f - 0.3f * dist(i, count).pow(2)) * 1.1f
}

/** Tiefe Bänder in die Mitte, hohe nach außen. */
private fun centreOut(bands: FloatArray, count: Int) = FloatArray(count) { i ->
    val half = (count - 1) / 2f
    val d = abs(i - half) / max(1f, half)
    bands[min(bands.size - 1, (d * (bands.size - 1)).roundToInt())]
}

/** Bewegung ohne Ton: Lichtpunkt beim Verbinden, Atmen beim Denken. */
private fun stateValues(state: VoiceState, count: Int, t: Float, circular: Boolean): FloatArray {
    val out = FloatArray(count)
    if (state == VoiceState.Connecting) {
        val period = 1.6f
        val u = (t % period) / period
        val pos = if (circular) u * count else (0.5f - 0.5f * cos(u * PI.toFloat() * 2f)) * (count - 1)
        val width = max(1f, count * (if (circular) 0.08f else 0.16f))
        for (i in 0 until count) {
            var d = abs(i - pos)
            if (circular) d = min(d, count - d)
            out[i] = 0.55f * exp(-(d / width).pow(2))
        }
    } else if (state == VoiceState.Thinking) {
        for (i in 0 until count) {
            val d = if (circular) 0.5f else dist(i, count)
            val wave = 0.5f + 0.5f * sin((t * PI.toFloat() * 2f) / 1.8f - d * 2.2f)
            out[i] = (0.12f + 0.22f * wave) * (1f - d * 0.35f)
        }
    }
    return out
}

private fun inkAlpha(v: Float) = 0.28f + 0.72f * (v * 2.4f).coerceIn(0f, 1f)

private class BandSim {
    var values = FloatArray(0)
    var last = 0L
    var t = 0f
}

@Composable
private fun BandsCanvas(
    visual: VoiceVisual,
    state: VoiceState,
    level: () -> Float,
    color: Color,
    modifier: Modifier,
    birth: Float,
    seed: androidx.compose.ui.unit.Dp,
) {
    val frame = rememberFrameNanos()
    val sim = remember { BandSim() }
    Canvas(modifier) {
        val now = frame.value
        val dt = if (sim.last == 0L || now == 0L) 1f / 60f else ((now - sim.last) / 1e9f).coerceIn(0f, 0.05f)
        sim.last = now
        sim.t += dt
        val n = when (visual) {
            VoiceVisual.Bars -> 7
            VoiceVisual.Radial -> 48
            VoiceVisual.Grid -> 16
            else -> 9
        }
        val circular = visual == VoiceVisual.Radial
        if (sim.values.size != n) sim.values = FloatArray(n)
        val audio = state == VoiceState.Listening || state == VoiceState.Speaking
        val bandCount = if (circular) ceil(n / 2f).toInt() else ceil(n / 2f).toInt() + 1
        val spectrum = if (audio) synthesizeBands(clamp01(level()), bandCount, sim.t) else null
        val laid = when {
            spectrum == null -> FloatArray(n)
            circular -> FloatArray(n) { i ->
                // zweimal gefaltet: tiefe Töne oben und unten, hohe an den Seiten
                val d = 1f - abs(1f - (2f * min(i, n - i)) / (n / 2f))
                spectrum[min(bandCount - 1, (d * (bandCount - 1)).roundToInt())]
            }
            else -> smooth(centreOut(spectrum, n), n)
        }
        val motion = stateValues(state, n, sim.t, circular)
        for (i in 0 until n) {
            val goal = max((laid[i] * 1.4f).coerceIn(0f, 1f).pow(0.8f), motion[i])
            val prev = sim.values[i]
            sim.values[i] = approach(prev, goal, if (goal > prev) 24f else 8f, dt)
        }
        val b = birth.coerceIn(0f, 1f)
        val seedR = if (seed == androidx.compose.ui.unit.Dp.Unspecified) min(size.width, size.height) * 0.3f else seed.toPx()
        when (visual) {
            VoiceVisual.Bars -> paintBars(sim.values, color, b, seedR)
            VoiceVisual.Radial -> paintRadial(sim.values, color, b, seedR)
            VoiceVisual.Grid -> paintGrid(sim.values, color, b, seedR)
            else -> paintDots(sim.values, color, b, seedR)
        }
    }
}

/** Kugel in der Mitte (Start der Entstehung): verschwindet, während sich die Teile lösen. */
private fun DrawScope.seedBlob(fg: Color, birth: Float, seedR: Float, until: Float = 0.45f) {
    if (birth >= until) return
    val k = birth / until
    drawCircle(fg, radius = seedR * (1f - 0.35f * k), center = center, alpha = (1f - k).pow(1.4f))
}

/**
 * Sieben Balken. Entstehung: Die Kugel teilt sich – alle Balken starten als dieselbe runde Form in der
 * Mitte und gleiten nach außen, werden dabei schmal und nehmen ihre Höhe an. Die Mitte zuerst.
 */
private fun DrawScope.paintBars(values: FloatArray, fg: Color, birth: Float, seedR: Float) {
    val n = values.size
    val gapRatio = 0.6f
    val w = min(size.height * 0.3f, size.width / (n + (n - 1) * gapRatio))
    val gap = w * gapRatio
    val x0 = (size.width - (n * w + (n - 1) * gap)) / 2f
    val cx = size.width / 2f
    val cy = size.height / 2f
    seedBlob(fg, birth, seedR)
    for (i in 0 until n) {
        val v = values[i]
        val d = dist(i, n)
        val k = if (birth >= 1f) 1f else SplitEase.transform(span(birth, 0.04f * d, 0.78f + 0.12f * d))
        val h = max(w, v * size.height)
        val bw = lerpF(seedR * 2f * 0.62f, w, k)
        val bh = lerpF(seedR * 2f * 0.62f, h, k)
        val x = lerpF(cx, x0 + i * (w + gap) + w / 2f, k)
        val a = lerpF(1f, inkAlpha(v), k)
        drawRoundRect(fg.copy(alpha = a), Offset(x - bw / 2f, cy - bh / 2f), Size(bw, bh), CornerRadius(min(bw, bh) / 2f))
    }
}

private fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t

/**
 * Strahlen im Kreis. Entstehung: Die Kugel öffnet sich zum Ring, die Strahlen wachsen nach außen und
 * drehen sich dabei ein Stück in ihre Lage (wie eine Blüte, die aufgeht).
 */
private fun DrawScope.paintRadial(values: FloatArray, fg: Color, birth: Float, seedR: Float) {
    val n = values.size
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outer = min(size.width, size.height) / 2f - dp(2f)
    val inner = outer * 0.48f
    val lw = ((PI.toFloat() * 2f * inner) / n * 0.42f).coerceIn(dp(1.25f), dp(3f))
    val k = if (birth >= 1f) 1f else SplitEase.transform(span(birth, 0.05f, 0.95f))
    seedBlob(fg, birth, seedR, until = 0.55f)
    val twist = (1f - k) * -PI.toFloat() / 3f
    val ringIn = lerpF(seedR * 0.2f, inner, k)
    for (i in 0 until n) {
        val v = values[i]
        val a = (i / n.toFloat()) * PI.toFloat() * 2f - PI.toFloat() / 2f + twist
        val full = lw * 0.5f + v * (outer - inner - lw)
        val len = lerpF(seedR * 0.75f, full, k)
        drawLine(
            fg.copy(alpha = lerpF(1f, inkAlpha(v), k)),
            Offset(cx + cos(a) * ringIn, cy + sin(a) * ringIn),
            Offset(cx + cos(a) * (ringIn + len), cy + sin(a) * (ringIn + len)),
            strokeWidth = lerpF(lw * 2.2f, lw, k),
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Raster aus Kästchen. Entstehung: Die Kugel zerfällt, die Kästchen fliegen von der Mitte an ihren Platz –
 * die nahen zuerst, die äußeren etwas später (wie eine Welle).
 */
private fun DrawScope.paintGrid(values: FloatArray, fg: Color, birth: Float, seedR: Float) {
    val cols = values.size
    val gap = dp(2f)
    val cell = max(dp(2f), (size.width - (cols - 1) * gap) / cols)
    var rows = max(3, floor((size.height + gap) / (cell + gap)).toInt())
    if (rows % 2 == 0) rows -= 1
    val x0 = (size.width - (cols * cell + (cols - 1) * gap)) / 2f
    val y0 = (size.height - (rows * cell + (rows - 1) * gap)) / 2f
    val half = (rows - 1) / 2f
    val r = min(dp(2f), cell / 3f)
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxD = kotlin.math.hypot(size.width / 2f, size.height / 2f)
    seedBlob(fg, birth, seedR, until = 0.5f)
    for (c in 0 until cols) {
        val lit = values[c] * (half + 1f)
        for (row in 0 until rows) {
            val d = abs(row - half)
            val amount = (lit - d).coerceIn(0f, 1f)
            val home = Offset(x0 + c * (cell + gap), y0 + row * (cell + gap))
            val k = if (birth >= 1f) 1f else {
                val far = kotlin.math.hypot(home.x + cell / 2f - cx, home.y + cell / 2f - cy) / maxD
                SplitEase.transform(span(birth, 0.05f + 0.35f * far, 0.6f + 0.35f * far))
            }
            if (k <= 0f) continue
            val tl = Offset(lerpF(cx - cell / 2f, home.x, k), lerpF(cy - cell / 2f, home.y, k))
            drawRoundRect(fg.copy(alpha = 0.10f * k), tl, Size(cell, cell), CornerRadius(r))
            if (amount > 0f) drawRoundRect(fg.copy(alpha = amount * k), tl, Size(cell, cell), CornerRadius(r))
        }
    }
}

/** Punkte in einer Reihe. Entstehung: Die Kugel teilt sich in neun Tropfen, die nach außen rollen. */
private fun DrawScope.paintDots(values: FloatArray, fg: Color, birth: Float, seedR: Float) {
    val n = values.size
    val pitch = size.width / n
    val rMax = min(pitch * 0.42f, size.height / 2f)
    val rMin = max(dp(1.5f), rMax * 0.28f)
    val cx = size.width / 2f
    seedBlob(fg, birth, seedR)
    for (i in 0 until n) {
        val v = values[i]
        val d = dist(i, n)
        val k = if (birth >= 1f) 1f else SplitEase.transform(span(birth, 0.03f * d, 0.8f + 0.1f * d))
        val r = rMin + (v * 1.3f).coerceIn(0f, 1f) * (rMax - rMin)
        drawCircle(
            fg.copy(alpha = lerpF(1f, inkAlpha(v), k)),
            lerpF(seedR * 0.62f, r, k),
            Offset(lerpF(cx, pitch * (i + 0.5f), k), size.height / 2f),
        )
    }
}

/** dp in Pixel, innerhalb des Zeichnens. */
private fun DrawScope.dp(v: Float) = v * density
