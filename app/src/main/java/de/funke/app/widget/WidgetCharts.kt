package de.funke.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import de.funke.app.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * Die Diagramme der Fortschritts-Widgets, als Bild gezeichnet (Widgets können keine eigenen Grafiken).
 * Jedes Bild gibt es hell und dunkel; das Layout zeigt das passende (layout / layout-night).
 * Farben wie in res/values(-night)/colors.xml (w_*).
 */
object WidgetCharts {

    class Pal(val text: Int, val sub: Int, val track: Int, val accent: Int, val ice: Int, val onAccent: Int)

    /**
     * Schrift der Diagramme (seit 15.0): Inter wie in der App. Die Bilder entstehen in der App selbst, da geht das –
     * anders als bei Text direkt im Widget, wo Android nur Systemschriften erlaubt.
     */
    class Type(val regular: Typeface, val medium: Typeface) {
        companion object {
            private val System = Type(Typeface.DEFAULT, Typeface.create("sans-serif-medium", Typeface.NORMAL))

            @Volatile
            private var cached: Type? = null

            fun of(context: Context): Type = cached ?: runCatching {
                Type(context.resources.getFont(R.font.inter_regular), context.resources.getFont(R.font.inter_medium))
            }.getOrDefault(System).also { cached = it }
        }
    }

    val LIGHT = Pal(
        text = 0xFF1E1C17.toInt(), sub = 0xFF77715F.toInt(), track = 0xFFEFE9DC.toInt(),
        accent = 0xFFF2A324.toInt(), ice = 0xFF5FA8E6.toInt(), onAccent = 0xFF1E1C17.toInt(),
    )
    val DARK = Pal(
        text = 0xFFF5F5F7.toInt(), sub = 0xFFA1A1AA.toInt(), track = 0xFF2A2932.toInt(),
        accent = 0xFFFFC93C.toInt(), ice = 0xFF7CC0F5.toInt(), onAccent = 0xFF1E1A26.toInt(),
    )

    class WeekData(
        val days: List<LocalDate>,
        val minutes: List<Int>,
        val done: List<Boolean>,
        val frozen: List<Boolean>,
        val goal: Int,
        val locale: Locale,
    )

    class MonthData(
        val month: YearMonth,
        val today: LocalDate,
        val minutes: List<Int>,
        val done: List<Boolean>,
        val frozen: List<Boolean>,
        val goal: Int,
        val locale: Locale,
    )

    private fun alpha(color: Int, a: Float): Int = (color and 0x00FFFFFF) or ((255 * a).toInt().coerceIn(0, 255) shl 24)

    private fun dayShort(d: LocalDate, locale: Locale, len: Int): String =
        d.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).replace(".", "").take(len)

    // ------------------------------------------------------------------------------------------
    // Letzte 7 Tage: Balken pro Tag, gestrichelte Linie fürs Tagesziel
    // ------------------------------------------------------------------------------------------

    fun week(w: Int, h: Int, dp: Float, pal: Pal, data: WeekData, type: Type): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER; fontFeatureSettings = "tnum" }

        val roomy = h > 74 * dp
        val labelH = 17 * dp
        val valueH = if (roomy) 14 * dp else 3 * dp
        val bottom = h - labelH - 2 * dp
        val top = valueH
        val chartH = (bottom - top).coerceAtLeast(10 * dp)
        val maxMin = data.minutes.maxOrNull() ?: 0
        val scaleMax = max(max(data.goal * 1.25f, maxMin.toFloat()), 10f)
        val colW = w / 7f
        val barW = min(colW * 0.56f, 28 * dp)

        // Ziel-Linie
        if (data.goal > 0) {
            val y = bottom - chartH * data.goal / scaleMax
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.3f * dp
            paint.color = alpha(pal.sub, 0.55f)
            paint.pathEffect = DashPathEffect(floatArrayOf(4 * dp, 4 * dp), 0f)
            c.drawLine(0f, y, w.toFloat(), y, paint)
            paint.pathEffect = null
        }

        paint.style = Paint.Style.FILL
        for (i in 0 until 7) {
            val cx = colW * (i + 0.5f)
            val v = data.minutes[i]
            val isToday = i == 6
            val bh = when {
                v > 0 -> max(chartH * v / scaleMax, 7 * dp)
                data.frozen[i] -> 10 * dp
                else -> 4 * dp
            }
            val r = RectF(cx - barW / 2, bottom - bh, cx + barW / 2, bottom)
            paint.color = when {
                v > 0 && data.done[i] -> pal.accent
                v > 0 -> alpha(pal.accent, 0.45f)
                data.frozen[i] -> pal.ice
                else -> pal.track
            }
            val rad = min(barW / 2, 8 * dp).coerceAtMost(bh / 2)
            c.drawRoundRect(r, rad, rad, paint)

            // Wert über dem Balken
            if (roomy && v > 0) {
                text.typeface = type.medium
                text.textSize = 10 * dp
                text.color = if (data.done[i]) pal.text else pal.sub
                c.drawText(v.toString(), cx, r.top - 3.5f * dp, text)
            }
            // Wochentag
            text.typeface = if (isToday) type.medium else type.regular
            text.textSize = 11 * dp
            text.color = if (isToday) pal.text else pal.sub
            c.drawText(dayShort(data.days[i], data.locale, 2), cx, h - 4 * dp, text)
            if (isToday) {
                paint.color = pal.accent
                c.drawCircle(cx, h - 1.2f * dp, 1.4f * dp, paint)
            }
        }
        return bmp
    }

    // ------------------------------------------------------------------------------------------
    // Monat: jeder Tag ein Kreis, je mehr gelesen, desto kräftiger
    // ------------------------------------------------------------------------------------------

    fun month(w: Int, h: Int, dp: Float, pal: Pal, data: MonthData, type: Type): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER; fontFeatureSettings = "tnum" }

        val first = data.month.atDay(1)
        val offset = first.dayOfWeek.value - 1          // Montag zuerst
        val len = data.month.lengthOfMonth()
        val rows = (offset + len + 6) / 7
        val headH = 15 * dp
        val colW = w / 7f
        val rowH = (h - headH) / rows
        val r = min(colW, rowH) * 0.40f

        // Wochentage
        text.textSize = 10 * dp
        text.typeface = type.medium
        text.color = pal.sub
        for (i in 0 until 7) {
            val d = first.plusDays((i - offset).toLong())
            c.drawText(dayShort(d, data.locale, if (data.locale.language == "de") 2 else 1), colW * (i + 0.5f), 11 * dp, text)
        }

        val numSize = min(r * 0.95f, 12 * dp)
        for (day in 1..len) {
            val idx = offset + day - 1
            val cx = colW * (idx % 7 + 0.5f)
            val cy = headH + rowH * (idx / 7 + 0.5f)
            val date = data.month.atDay(day)
            val v = data.minutes[day - 1]
            val future = date.isAfter(data.today)
            val done = data.done[day - 1]
            val frozen = data.frozen[day - 1]

            paint.style = Paint.Style.FILL
            val fill: Int? = when {
                future -> null
                done -> pal.accent
                v > 0 -> alpha(pal.accent, 0.28f + 0.42f * min(1f, v.toFloat() / max(1, data.goal)))
                frozen -> pal.ice
                else -> pal.track
            }
            if (fill != null) {
                paint.color = fill
                c.drawCircle(cx, cy, r, paint)
            } else {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1.2f * dp
                paint.color = alpha(pal.sub, 0.25f)
                c.drawCircle(cx, cy, r - 0.6f * dp, paint)
            }
            if (date == data.today) {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1.8f * dp
                paint.color = pal.text
                c.drawCircle(cx, cy, r + 2.6f * dp, paint)
            }

            if (r > 7 * dp) {
                text.textSize = numSize
                text.typeface = if (done || date == data.today) type.medium else type.regular
                text.color = when {
                    future -> alpha(pal.sub, 0.55f)
                    done -> pal.onAccent
                    frozen && v == 0 -> 0xFFFFFFFF.toInt()
                    v > 0 -> pal.text
                    else -> pal.sub
                }
                c.drawText(day.toString(), cx, cy + numSize * 0.36f, text)
            }
        }
        return bmp
    }
}
