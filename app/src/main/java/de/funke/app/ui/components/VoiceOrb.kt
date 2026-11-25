package de.funke.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Die Linien-Kugel ("Voice Orb"): eine Kugel aus feinen Höhenlinien, leicht zum Betrachter gekippt.
 * Vordere Linien sind dunkler und kräftiger, die Rückseite schimmert durch. Jeder Zustand ist eine
 * Form; die Kugel gleitet von Form zu Form, statt zu springen.
 *  - Bereit: atmet ruhig. Verbinden: Linien nur teilweise, die Spuren jagen herum.
 *  - Zuhören: Wellen laufen nach innen und wachsen mit deiner Stimme.
 *  - Denken: kleiner, dreht sich, ein Lichtband läuft von oben nach unten.
 *  - Sprechen: Wellen laufen nach außen. Unterbrochen: plattgedrückt. Stumm: gepunktet. Fehler: geteilt.
 *
 * Übertragen aus chatcn "Voice Orb" von Aaditya (https://chatcn.dev), MIT-Lizenz.
 */

private const val SCALE = 0
private const val FLOW = 1
private const val STIR = 2
private const val WAVE = 3
private const val TRAVEL = 4
private const val TURN = 5
private const val BREATHE = 6
private const val INK = 7
private const val BACK = 8
private const val TRACE = 9
private const val DOTS = 10
private const val SPLIT = 11
private const val SQUASH = 12
private const val SHIMMER = 13
private const val NF = 14

private fun form(
    scale: Float = 0.9f, flow: Float = 0f, stir: Float = 0f, wave: Float = 0f, travel: Float = 0f,
    turn: Float = 0f, breathe: Float = 0f, ink: Float = 1f, back: Float = 0.11f, trace: Float = 1f,
    dots: Float = 0f, split: Float = 0f, squash: Float = 0f, shimmer: Float = 0f,
) = floatArrayOf(scale, flow, stir, wave, travel, turn, breathe, ink, back, trace, dots, split, squash, shimmer)

private val FORMS: Map<VoiceState, FloatArray> = mapOf(
    VoiceState.Idle to form(flow = 0.022f, stir = 0.32f, turn = 0.1f, breathe = 0.014f),
    VoiceState.Connecting to form(scale = 0.86f, flow = 0.012f, stir = 0.4f, turn = 0.5f, trace = 0.3f, back = 0.08f),
    VoiceState.Listening to form(flow = 0.03f, stir = 0.6f, wave = 0.014f, travel = -0.9f, turn = 0.16f, breathe = 0.01f),
    VoiceState.Thinking to form(scale = 0.84f, flow = 0.08f, stir = 1.3f, turn = 1f, breathe = 0.024f, shimmer = 1f),
    VoiceState.Speaking to form(flow = 0.026f, stir = 0.7f, wave = 0.02f, travel = 1.3f, turn = 0.22f),
    VoiceState.Interrupted to form(scale = 0.88f, flow = 0.016f, squash = 0.16f, back = 0.1f),
    VoiceState.Muted to form(scale = 0.86f, ink = 0.62f, back = 0.05f, dots = 1f),
    VoiceState.Error to form(scale = 0.86f, ink = 0.55f, back = 0.06f, split = 1f),
)

/** Zuhören zieht Wellen nach innen, Sprechen schickt sie nach außen und lässt die Kugel wachsen. */
private fun withLevel(f: FloatArray, state: VoiceState, l: Float): FloatArray {
    val o = f.copyOf()
    if (state == VoiceState.Listening) {
        o[SCALE] += 0.06f * l; o[FLOW] += 0.05f * l; o[STIR] += 1.1f * l; o[WAVE] += 0.05f * l; o[TRAVEL] -= 2.2f * l
    } else if (state == VoiceState.Speaking) {
        o[SCALE] += 0.08f * l; o[FLOW] += 0.03f * l; o[STIR] += 1.4f * l; o[WAVE] += 0.065f * l; o[TRAVEL] += 2.6f * l
    }
    return o
}

private const val BANDS = 5
private const val GLOWS = 4
private const val TILT = 0.36f
private val COS_T = cos(TILT)
private val SIN_T = sin(TILT)

private class OrbSim {
    val form = FloatArray(NF)
    // flow, wave, turn, breath, trace, sweep
    val phase = floatArrayOf(1.3f, 2.2f, 0.6f, 0f, 0.15f, 0.4f)
    var level = 0f
    var first = true
    var last = 0L
    val front = Array(BANDS * GLOWS) { Path() }
    val back = Path()
}

private fun approachF(current: Float, target: Float, rate: Float, dt: Float) = current + (target - current) * (1f - exp(-rate * dt))

@Composable
internal fun VoiceOrbCanvas(
    state: VoiceState,
    level: () -> Float,
    color: Color,
    modifier: Modifier,
    birth: Float = 1f,
    seed: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
) {
    val frame = remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { while (true) withFrameNanos { frame.longValue = it } }
    val sim = remember { OrbSim() }
    Canvas(modifier) {
        val now = frame.longValue
        val dt = if (sim.last == 0L || now == 0L) 1f / 60f else ((now - sim.last) / 1e9f).coerceIn(0f, 0.05f)
        sim.last = now

        val raw = if (state == VoiceState.Listening || state == VoiceState.Speaking) level().coerceIn(0f, 1f) else 0f
        sim.level = approachF(sim.level, raw, if (raw > sim.level) 16f else 5f, dt)
        val target = withLevel(FORMS.getValue(state), state, sim.level)
        for (k in 0 until NF) {
            val rate = if (k == SPLIT || k == SQUASH) 11f else if (k == TRACE) 4f else 6f
            sim.form[k] = if (sim.first) target[k] else approachF(sim.form[k], target[k], rate, dt)
        }
        sim.first = false
        val f = sim.form
        val ph = sim.phase
        ph[0] += dt * f[STIR]
        ph[1] += dt * f[TRAVEL] * 6f
        ph[2] += dt * f[TURN]
        ph[3] += dt * PI.toFloat() * 2f / when (state) {
            VoiceState.Idle -> 4.6f
            VoiceState.Listening -> 3.4f
            VoiceState.Thinking -> 1.8f
            else -> 3.6f
        }
        ph[4] += dt * 0.42f
        ph[5] = (ph[5] + dt / 1.8f) % 1f

        val pxDp = min(size.width, size.height) / density
        val cx = size.width / 2f
        val cy = size.height / 2f
        val fullR = min(size.width, size.height) / 2f * 0.9f
        // Entstehung: aus der vollen Kugel (Mikrofon) wird die Linien-Kugel – sie wächst und löst sich in Linien auf
        val bk = if (birth >= 1f) 1f else SplitEase.transform(span(birth, 0f, 1f))
        val seedR = if (seed == androidx.compose.ui.unit.Dp.Unspecified) fullR * 0.5f else seed.toPx()
        val r = seedR + (fullR - seedR) * bk
        if (birth < 1f) {
            val fade = span(birth, 0f, 0.7f)
            drawCircle(color, radius = r * (1f - 0.25f * fade), center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f), alpha = (1f - fade).pow(1.3f))
        }
        val inkK = span(birth, 0.15f, 0.85f)
        val lines = (pxDp / 10f).coerceIn(5f, 22f).roundToInt()
        val samples = if (pxDp > 120) 120 else if (pxDp > 60) 84 else 56
        buildSphere(f, ph, cx, cy, r, lines, samples, sim)
        val weight = (pxDp / 130f).coerceIn(0.9f, 1.6f) * density

        val paint: DrawScope.() -> Unit = {
            val dash = when {
                f[DOTS] > 0.5f -> PathEffect.dashPathEffect(floatArrayOf(0.001f, weight * 3.2f))
                f[DOTS] > 0.01f -> PathEffect.dashPathEffect(floatArrayOf(weight * 6f * (1f - f[DOTS]), weight * 3.2f))
                else -> null
            }
            if (f[BACK] > 0.005f) {
                drawPath(sim.back, color.copy(alpha = (f[BACK] * f[INK] * inkK).coerceIn(0f, 1f)),
                    style = Stroke(weight * 0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = dash))
            }
            for (band in 0 until BANDS * GLOWS) {
                val z = (floor(band / GLOWS.toFloat()) + 0.5f) / BANDS
                val lit = (band % GLOWS) / (GLOWS - 1f)
                val alpha = (0.28f + 0.72f * z.pow(0.8f)) * (0.45f + 0.55f * lit)
                val w = 0.7f + 0.55f * z
                drawPath(sim.front[band], color.copy(alpha = (alpha * f[INK] * inkK).coerceIn(0f, 1f)),
                    style = Stroke(weight * w * (if (f[DOTS] > 0.5f) 1.5f else 1f), cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = dash))
            }
        }

        if (f[SPLIT] > 0.01f) {
            val gap = f[SPLIT] * r * 0.12f
            for (half in intArrayOf(-1, 1)) {
                translate(top = gap / 2f * half) {
                    clipRect(0f, if (half < 0) 0f else cy, size.width, if (half < 0) cy else size.height) { paint() }
                }
            }
        } else {
            paint()
        }
    }
}

/** Höhenlinien der Kugel, nach Tiefe sortiert in Pfade geschrieben. */
private fun buildSphere(f: FloatArray, ph: FloatArray, cx: Float, cy: Float, r: Float, lines: Int, samples: Int, out: OrbSim) {
    out.front.forEach { it.reset() }
    out.back.reset()
    val breath = 1f + f[BREATHE] * sin(ph[3])
    val radius = r * f[SCALE] * breath
    val sx = 1f + f[SQUASH] * 0.5f
    val sy = 1f - f[SQUASH]
    val ct = cos(ph[2])
    val st = sin(ph[2])
    for (i in 0 until lines) {
        val lat = -PI.toFloat() / 2f + PI.toFloat() * (i + 1) / (lines + 1)
        val cl = cos(lat)
        val sl = sin(lat)
        val start = ph[4] + i * 0.06f
        // Lichtband (beim Denken) läuft von oben nach unten
        val off = (1f - (i + 0.5f) / lines - (ph[5] * 1.3f - 0.15f)) / 0.18f
        val near = exp(-off * off)
        val lit = 1f - f[SHIMMER] * (1f - near)
        val step = (lit * (GLOWS - 1)).roundToInt()

        var band = -2
        var hasLine = false
        var hasPrev = false
        var prevX = 0f
        var prevY = 0f
        var prevZ = 0f
        var prevOn = false
        for (j in 0..samples) {
            val a = j / samples.toFloat() * PI.toFloat() * 2f
            val ux = cl * cos(a)
            val uy = sl
            val uz = cl * sin(a)
            val fx = ux * ct - uz * st
            val fz = ux * st + uz * ct
            val vy = uy * COS_T - uz * SIN_T
            val vz = uy * SIN_T + uz * COS_T
            val flow = 0.5f * sin(2.3f * fx + 1.4f * uy + ph[0]) +
                0.3f * sin(3.1f * uy - 1.9f * fz - ph[0] * 1.27f + 1.1f) +
                0.2f * sin(4.3f * fz + 2.6f * fx + ph[0] * 0.71f + 2.4f)
            val front01 = max(vz, 0f)
            val dist = acos(vz.coerceIn(-1f, 1f))
            val wave = sin(dist * 8f - ph[1]) * front01
            val give = 0.3f + 0.7f * cl
            val rr = radius * (1f + give * (f[FLOW] * flow + f[WAVE] * wave))
            val lift = f[WAVE] * wave * radius * 0.7f * give
            val x = cx + ux * rr * sx
            val y = cy - (vy * rr + lift) * sy

            var on = true
            if (f[TRACE] < 0.985f) {
                val u = (((j / samples.toFloat() - start) % 1f) + 1f) % 1f
                on = u < f[TRACE]
            }
            if (hasPrev && on && prevOn) {
                val z = (prevZ + vz) / 2f
                val next = if (z < 0f) -1 else min(BANDS - 1, floor(z * BANDS).toInt()) * GLOWS + step
                val path = if (next < 0) out.back else out.front[next]
                if (next != band || !hasLine) {
                    path.moveTo(prevX, prevY)
                    band = next
                    hasLine = true
                }
                path.lineTo(x, y)
            } else {
                hasLine = false
                band = -2
            }
            prevX = x; prevY = y; prevZ = vz; prevOn = on; hasPrev = true
        }
    }
}

/**
 * Sprach-ähnliche Lautstärke ohne Mikrofon (für Vorschauen): Silben, kurze Pausen zwischen Wörtern
 * und ein langsamer Satzbogen. Ebenfalls aus chatcn übernommen.
 */
class SpeechSim(private val seed: Float = 1f, private val loudness: Float = 0.8f) {
    private var envelope = 0.02f
    private var last = 0L
    private var phase = 0f
    var speaking = true

    private fun fract(x: Float) = x - floor(x)
    private fun hash(n: Float) = fract(sin(n * 12.9898f + seed * 78.233f) * 43758.5453f)

    fun level(): Float {
        val now = System.nanoTime()
        if (now - last < 8_000_000L) return envelope
        val dt = if (last == 0L) 1f / 60f else min(0.1f, (now - last) / 1e9f)
        last = now
        val t = now / 1e9f
        val k = floor(phase)
        phase += dt * 4.2f * (0.8f + 0.4f * hash(k * 3.1f))
        val goal = if (!speaking) 0.02f + 0.01f * (0.5f + 0.5f * sin(t * 7.3f + seed)) else {
            val kk = floor(phase)
            val u = phase - kk
            var amp = 0.45f + 0.55f * hash(kk)
            if (hash(kk * 1.7f + 3f) < 0.17f) amp *= 0.08f
            val shape = if (u < 0.22f) (u / 0.22f).pow(0.7f) else (1f - (u - 0.22f) / 0.78f).pow(1.5f)
            val contour = 0.78f + 0.22f * sin(t * 0.55f + seed * 2f)
            loudness * contour * (0.12f + 0.88f * amp * shape)
        }
        val rate = if (goal > envelope) 28f else 9f
        envelope += (goal - envelope) * (1f - exp(-rate * dt))
        return envelope
    }
}
