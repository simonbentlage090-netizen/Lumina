package de.funke.app.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// ---------------------------------------------------------------------------------------------
// Dampf-Timer
//
// Jede Ziffer besteht aus Körnchen. Wechselt eine Ziffer, löst sich die alte als Dampf auf, der
// auf einem Windfeld nach oben driftet, und die neue kondensiert aus derselben Wolke: Die Körnchen
// werden von einer weichen Feder zu ihren Plätzen in der neuen Ziffer gezogen.
// Nach der Animation "schläft" die Zeichnung, bis die nächste Sekunde kommt.
// ---------------------------------------------------------------------------------------------

private const val FLOATS = 6 // je Körnchen: x, y, vx, vy, Zielx, Ziely / beim Dampf: x, y, vx, vy, Alter, Lebensdauer
private const val MAX_GRAINS = 1100 // pro Ziffer
private const val SPRING_K = 90f
private const val ZETA = 0.55f // < 1: die Körnchen schwingen leicht über und setzen sich dann
private const val DRAG = 0.92f
private const val DT_MAX = 0.032f
private const val FIELD_SCALE = 0.008f
private const val CURL_EPS = 0.75

// --- Rauschen für das Windfeld -----------------------------------------------------------------

private fun hash2(x: Double, y: Double): Double {
    val n = sin(x * 127.1 + y * 311.7) * 43758.5453123
    return n - floor(n)
}

private fun vnoise(x: Double, y: Double): Double {
    val xi = floor(x)
    val yi = floor(y)
    val xf = x - xi
    val yf = y - yi
    val u = xf * xf * (3 - 2 * xf)
    val v = yf * yf * (3 - 2 * yf)
    val a = hash2(xi, yi)
    val b = hash2(xi + 1, yi)
    val c = hash2(xi, yi + 1)
    val d = hash2(xi + 1, yi + 1)
    return a + (b - a) * u + (c - a) * v + (a - b - c + d) * u * v
}

private fun noise2(x: Double, y: Double): Double =
    0.65 * vnoise(x, y) + 0.35 * vnoise(x * 2.1 + 19.7, y * 2.1 + 7.3)

// --- Zustand der Körnchen ------------------------------------------------------------------------

private class Slot {
    val g = FloatArray(MAX_GRAINS * FLOATS) // ankommende / ruhende Körnchen
    val v = FloatArray(MAX_GRAINS * FLOATS) // aufsteigender Dampf
    var gc = 0
    var vc = 0
    var active = false
}

private class VaporEngine(
    private val digitCount: Int,
    private val density: Float,
    private val animate: Boolean,
) {
    private val slots = Array(digitCount) { Slot() }
    private val colons = digitCount / 2 - 1
    private var digits: String? = null
    private var glyphs: Array<FloatArray>? = null
    private val cellX = FloatArray(digitCount)
    private var originY = 0f
    private val colonX = FloatArray(max(colons, 1))
    private var colonY1 = 0f
    private var colonY2 = 0f
    private var colonR = 0f
    private var laidOut = false
    private val dampC = 2f * ZETA * sqrt(SPRING_K)

    private val grainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val scratch = FloatArray(MAX_GRAINS * 2)
    private val buckets = Array(4) { FloatArray(MAX_GRAINS * 2) }
    private val bucketCount = IntArray(4)

    val anyActive: Boolean
        get() = slots.any { it.active } || (colonsVisible && colonFade < 1f) || (!colonsVisible && colonFade > 0f)

    // ----- Aufbau: Ziffern in Körnchen zerlegen -----

    fun layout(w: Int, h: Int) {
        if (w < 2 || h < 2) return
        val typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        val measure = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = 100f
        }
        var advance = 0f
        for (d in 0..9) advance = max(advance, measure.measureText(d.toString()))
        val unitW = advance / 100f
        val unitsTotal = digitCount * unitW + colons * 0.36f
        val fontPx = min(76f * density, w * 0.82f / unitsTotal)
        measure.textSize = fontPx

        val cellW = ceil(unitW * fontPx).toInt() + 2
        val fm = measure.fontMetrics
        val gh = ceil(fm.descent - fm.ascent).toInt() + 2
        val baseline = -fm.ascent + 1f
        val bounds = Rect()
        measure.getTextBounds("8", 0, 1, bounds)
        val digitCenterY = baseline + (bounds.top + bounds.bottom) / 2f
        originY = h / 2f - digitCenterY

        val colonW = 0.36f * fontPx
        val totalW = digitCount * cellW + colons * colonW
        var x = (w - totalW) / 2f
        var c = 0
        for (i in 0 until digitCount) {
            if (i > 0 && i % 2 == 0) {
                colonX[c++] = x + colonW / 2f
                x += colonW
            }
            cellX[i] = x
            x += cellW
        }
        val digitTop = originY + baseline + bounds.top
        val digitHeight = (bounds.bottom - bounds.top).toFloat()
        colonY1 = digitTop + digitHeight * 0.32f
        colonY2 = digitTop + digitHeight * 0.68f
        colonR = fontPx * 0.05f

        // Ziffern 0-9 einmal rastern und die Punkte einsammeln
        val bitmap = Bitmap.createBitmap(cellW, gh, Bitmap.Config.ARGB_8888)
        val bitmapCanvas = android.graphics.Canvas(bitmap)
        val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            textSize = fontPx
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
        }
        val pixels = IntArray(cellW * gh)
        val stride = max(3, (density * 2.1f).roundToInt())
        val capacity = ((cellW / stride) + 2) * ((gh / stride) + 2) * 2
        val tmp = FloatArray(capacity)
        glyphs = Array(10) { digit ->
            bitmap.eraseColor(0)
            bitmapCanvas.drawText(digit.toString(), cellW / 2f, baseline, glyphPaint)
            bitmap.getPixels(pixels, 0, cellW, 0, 0, cellW, gh)
            var n = 0
            var y = 1
            while (y < gh) {
                var px = 1
                while (px < cellW) {
                    if ((pixels[y * cellW + px] ushr 24) > 128 && (n + 1) * 2 <= capacity) {
                        tmp[n * 2] = px.toFloat()
                        tmp[n * 2 + 1] = y.toFloat()
                        n++
                    }
                    px += stride
                }
                y += stride
            }
            val keepEvery = max(1, ceil(n / MAX_GRAINS.toFloat()).toInt())
            val m = (n + keepEvery - 1) / keepEvery
            val out = FloatArray(m * 2)
            var k = 0
            var i = 0
            while (i < n && k < m) {
                out[k * 2] = tmp[i * 2]
                out[k * 2 + 1] = tmp[i * 2 + 1]
                k++
                i += keepEvery
            }
            out
        }
        bitmap.recycle()

        laidOut = true
        seatAll()
        if (introPending && digits != null) {
            introPending = false
            condense()
        }
    }

    /** Beim ersten Erscheinen: Die Ziffern kondensieren aus einer Wolke unterhalb. */
    var introPending = false
    private var colonsVisible = true
    private var colonFade = 1f

    private fun condense() {
        if (!animate) return
        val d = density
        for (s in 0 until digitCount) {
            val slot = slots[s]
            val g = slot.g
            for (i in 0 until slot.gc) {
                val o = i * FLOATS
                val angle = Random.nextFloat() * 6.2832f
                val r = (30f + Random.nextFloat() * 70f) * d
                g[o] = g[o + 4] + kotlin.math.cos(angle) * r
                g[o + 1] = g[o + 5] + kotlin.math.abs(sin(angle)) * r * 0.9f + 30f * d
                g[o + 2] = (Random.nextFloat() - 0.5f) * 60f * d
                g[o + 3] = -(40f + Random.nextFloat() * 80f) * d
            }
            slot.active = true
        }
        colonFade = 0f
    }

    /** Zum Schluss: Alle Ziffern lösen sich vollständig in Dampf auf. */
    fun evaporate() {
        if (!colonsVisible || !laidOut) return
        colonsVisible = false
        for (s in 0 until digitCount) {
            val slot = slots[s]
            val g = slot.g
            val v = slot.v
            var vc = 0
            var i = 0
            while (i < slot.gc && vc < MAX_GRAINS) {
                val o = i * FLOATS
                val vo = vc * FLOATS
                v[vo] = g[o]
                v[vo + 1] = g[o + 1]
                v[vo + 2] = (Random.nextFloat() - 0.5f) * 50f * density
                v[vo + 3] = -(10f + Random.nextFloat() * 50f) * density
                v[vo + 4] = Random.nextFloat() * 200f
                v[vo + 5] = 900f + Random.nextFloat() * 700f
                vc++
                i++
            }
            slot.vc = vc
            slot.gc = 0
            slot.active = true
        }
    }

    private fun digitAt(s: Int): Int = digits?.getOrNull(s)?.digitToIntOrNull() ?: 0

    private fun seat(s: Int) {
        val gl = glyphs ?: return
        val homes = gl[digitAt(s)]
        val slot = slots[s]
        val n = homes.size / 2
        slot.gc = n
        slot.vc = 0
        slot.active = false
        for (i in 0 until n) {
            val o = i * FLOATS
            val hx = cellX[s] + homes[i * 2]
            val hy = originY + homes[i * 2 + 1]
            slot.g[o] = hx
            slot.g[o + 1] = hy
            slot.g[o + 2] = 0f
            slot.g[o + 3] = 0f
            slot.g[o + 4] = hx
            slot.g[o + 5] = hy
        }
    }

    private fun seatAll() {
        for (s in 0 until digitCount) seat(s)
    }

    // ----- Neue Zeit: geänderte Ziffern wandeln sich -----

    fun update(text: String) {
        val previous = digits
        digits = text
        if (!laidOut) return
        if (previous == null || previous.length != text.length) {
            seatAll()
            if (introPending) {
                introPending = false
                condense()
            }
            return
        }
        for (s in 0 until digitCount) {
            if (text[s] != previous[s]) {
                if (animate) transition(s, digitAt(s)) else seat(s)
            }
        }
    }

    private fun transition(s: Int, digit: Int) {
        val gl = glyphs ?: return
        val d = density
        val slot = slots[s]
        val g = slot.g
        val v = slot.v

        // alte Ziffer: Körnchen wandern in den Dampf-Pool
        var vc = 0
        var i = 0
        while (i < slot.gc && vc < MAX_GRAINS) {
            val o = i * FLOATS
            val vo = vc * FLOATS
            v[vo] = g[o]
            v[vo + 1] = g[o + 1]
            v[vo + 2] = g[o + 2] * 0.4f
            v[vo + 3] = g[o + 3] * 0.4f - 12f * d
            v[vo + 4] = 0f
            v[vo + 5] = 600f + Random.nextFloat() * 300f
            vc++
            i++
        }
        slot.vc = vc

        // neue Ziffer: kondensiert aus der Wolke
        val homes = gl[digit]
        val n = homes.size / 2
        slot.gc = n
        for (k in 0 until n) {
            val o = k * FLOATS
            val hx = cellX[s] + homes[k * 2]
            val hy = originY + homes[k * 2 + 1]
            if (vc > 0) {
                val src = Random.nextInt(vc) * FLOATS
                g[o] = v[src] + (Random.nextFloat() - 0.5f) * 10f * d
                g[o + 1] = v[src + 1] + (Random.nextFloat() - 0.5f) * 10f * d
                g[o + 2] = (Random.nextFloat() - 0.5f) * 40f * d
                g[o + 3] = -(20f + Random.nextFloat() * 40f) * d
            } else {
                g[o] = hx
                g[o + 1] = hy
                g[o + 2] = 0f
                g[o + 3] = 0f
            }
            g[o + 4] = hx
            g[o + 5] = hy
        }
        slot.active = true
    }

    // ----- Ein Bild weiter -----

    fun step(dt: Float) {
        val d = density
        colonFade = if (colonsVisible) min(1f, colonFade + dt * 1.6f) else max(0f, colonFade - dt * 4f)
        for (s in 0 until digitCount) {
            val slot = slots[s]
            if (!slot.active) continue
            val g = slot.g
            val v = slot.v
            var settled = true

            // ankommende Körnchen: leicht überschwingende Feder zum Ziel
            for (i in 0 until slot.gc) {
                val o = i * FLOATS
                var x = g[o]
                var y = g[o + 1]
                var vx = g[o + 2]
                var vy = g[o + 3]
                val hx = g[o + 4]
                val hy = g[o + 5]

                vx = (vx + (SPRING_K * (hx - x) - dampC * vx) * dt) * DRAG
                vy = (vy + (SPRING_K * (hy - y) - dampC * vy) * dt) * DRAG
                x += vx * dt
                y += vy * dt

                g[o] = x
                g[o + 1] = y
                g[o + 2] = vx
                g[o + 3] = vy

                if (kotlin.math.abs(x - hx) > 0.5f * d ||
                    kotlin.math.abs(y - hy) > 0.5f * d ||
                    kotlin.math.abs(vx) > 2f * d ||
                    kotlin.math.abs(vy) > 2f * d
                ) {
                    settled = false
                }
            }

            // Dampf: Wirbel-Wind, nach oben geneigt, blendet über die Lebensdauer aus
            var j = 0
            while (j < slot.vc) {
                val o = j * FLOATS
                val age = v[o + 4] + dt * 1000f
                val life = v[o + 5]
                if (age >= life) {
                    val lo = (slot.vc - 1) * FLOATS
                    for (k in 0 until FLOATS) v[o + k] = v[lo + k]
                    slot.vc--
                    continue
                }
                v[o + 4] = age
                var x = v[o]
                var y = v[o + 1]
                var vx = v[o + 2]
                var vy = v[o + 3]

                val nx = (x / d * FIELD_SCALE).toDouble()
                val ny = (y / d * FIELD_SCALE).toDouble()
                val dndx = noise2(nx + CURL_EPS, ny) - noise2(nx - CURL_EPS, ny)
                val dndy = noise2(nx, ny + CURL_EPS) - noise2(nx, ny - CURL_EPS)
                var wx = dndy.toFloat()
                var wy = (-dndx).toFloat()
                var cl = hypot(wx, wy)
                if (cl == 0f) cl = 1f
                val sp = (40f + 50f * hash2(j * 1.31, s * 7.7).toFloat()) * d
                wx = wx / cl * sp * 0.6f
                wy = wy / cl * sp * 0.6f - sp * 0.8f
                val mix = min(1f, dt * 5f)
                vx += (wx - vx) * mix
                vy += (wy - vy) * mix
                x += vx * dt
                y += vy * dt

                v[o] = x
                v[o + 1] = y
                v[o + 2] = vx
                v[o + 3] = vy
                j++
            }
            if (slot.vc > 0) settled = false

            if (settled) {
                for (i in 0 until slot.gc) {
                    val o = i * FLOATS
                    g[o] = g[o + 4]
                    g[o + 1] = g[o + 5]
                    g[o + 2] = 0f
                    g[o + 3] = 0f
                }
                slot.active = false
            }
        }
    }

    // ----- Zeichnen -----

    fun draw(canvas: android.graphics.Canvas, argb: Int) {
        if (!laidOut) return
        val grain = 1.35f * density
        grainPaint.strokeWidth = grain

        for (s in 0 until digitCount) {
            val slot = slots[s]

            for (i in 0 until slot.gc) {
                scratch[i * 2] = slot.g[i * FLOATS]
                scratch[i * 2 + 1] = slot.g[i * FLOATS + 1]
            }
            grainPaint.color = argb
            grainPaint.alpha = if (slot.active) 245 else 225
            canvas.drawPoints(scratch, 0, slot.gc * 2, grainPaint)

            if (slot.vc > 0) {
                for (b in 0 until 4) bucketCount[b] = 0
                for (j in 0 until slot.vc) {
                    val o = j * FLOATS
                    val frac = (slot.v[o + 4] / slot.v[o + 5]).coerceIn(0f, 0.999f)
                    val b = (frac * 4f).toInt()
                    val k = bucketCount[b]
                    buckets[b][k * 2] = slot.v[o]
                    buckets[b][k * 2 + 1] = slot.v[o + 1]
                    bucketCount[b] = k + 1
                }
                for (b in 0 until 4) {
                    if (bucketCount[b] == 0) continue
                    val centerFrac = (b + 0.5f) / 4f
                    grainPaint.color = argb
                    grainPaint.alpha = ((1f - centerFrac) * 215f).toInt().coerceIn(0, 255)
                    canvas.drawPoints(buckets[b], 0, bucketCount[b] * 2, grainPaint)
                }
            }
        }

        // Doppelpunkte als feste Punkte
        dotPaint.color = argb
        dotPaint.alpha = (170 * colonFade).toInt()
        if (dotPaint.alpha > 0) for (c in 0 until colons) {
            canvas.drawCircle(colonX[c], colonY1, colonR, dotPaint)
            canvas.drawCircle(colonX[c], colonY2, colonR, dotPaint)
        }
    }
}

/** Ziffern für die Anzeige: 4 Ziffern = MM:SS, 6 Ziffern = HH:MM:SS. */
private fun formatDigits(seconds: Int, digitCount: Int): String {
    val total = seconds.coerceAtLeast(0)
    return if (digitCount >= 6) {
        val h = (total / 3600).coerceAtMost(99)
        "%02d%02d%02d".format(h, (total % 3600) / 60, total % 60)
    } else {
        val m = (total / 60).coerceAtMost(99)
        "%02d%02d".format(m, total % 60)
    }
}

/**
 * Countdown aus Körnchen. [seconds] ist die verbleibende Zeit.
 * 4 Ziffern zeigen MM:SS, 6 Ziffern HH:MM:SS. Ist in den Android-Einstellungen
 * "Animationen entfernen" aktiv, wechseln die Ziffern ohne Dampf-Effekt.
 */
@Composable
fun VaporDigits(
    seconds: Int,
    digitCount: Int,
    color: Color,
    description: String,
    modifier: Modifier = Modifier,
    intro: Boolean = false,
    dissolve: Boolean = false,
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    // Immer animieren: Der Dampf ist der Kern dieses Timers. (Die App läuft ohnehin
    // mit normaler Animationsgeschwindigkeit, siehe MainActivity.)
    val animate = true
    val engine = remember(digitCount, density, animate) {
        VaporEngine(digitCount, density, animate).also { it.introPending = intro }
    }
    var frame by remember { mutableLongStateOf(0L) }
    var layoutTick by remember { mutableLongStateOf(0L) }
    val text = formatDigits(seconds, digitCount)

    LaunchedEffect(engine, text, dissolve, layoutTick) {
        if (dissolve) engine.evaporate() else engine.update(text)
        frame++
        var last = androidx.compose.runtime.withFrameNanos { it }
        while (engine.anyActive) {
            val now = androidx.compose.runtime.withFrameNanos { it }
            val dt = ((now - last) / 1_000_000_000f).coerceIn(0f, DT_MAX)
            last = now
            engine.step(dt)
            frame++
        }
        frame++
    }

    Canvas(
        modifier
            .onSizeChanged {
                engine.layout(it.width, it.height)
                frame++
                layoutTick++
            }
            .semantics { contentDescription = description }
    ) {
        // Lesen von frame sorgt dafür, dass bei jedem Bild neu gezeichnet wird.
        val tick = frame
        if (tick < 0L) return@Canvas
        drawIntoCanvas { c -> engine.draw(c.nativeCanvas, color.toArgb()) }
    }
}
