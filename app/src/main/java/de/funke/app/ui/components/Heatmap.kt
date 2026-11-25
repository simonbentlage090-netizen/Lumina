package de.funke.app.ui.components

import de.funke.app.ui.theme.Space
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.Lang
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTextStyle

/**
 * Lese-Kalender wie bei GitHub: jede Spalte eine Woche (Montag oben), jedes Kästchen ein Tag.
 * Je mehr man an einem Tag gelesen hat (gemessen am Tagesziel), desto kräftiger leuchtet es.
 * Heute ist umrandet. Antippen zeigt Datum und Minuten.
 *
 * [weeks] Wochen bis einschließlich der aktuellen. Ist das breiter als der Platz, lässt es sich
 * seitlich wischen; am Anfang sieht man die neuesten Wochen.
 */
@Composable
fun ReadingHeatmap(
    minutesOn: (LocalDate) -> Int,
    goal: Int,
    modifier: Modifier = Modifier,
    weeks: Int = 53,
    cell: Dp = 13.dp,
    gap: Dp = 3.dp,
    showLegend: Boolean = true,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val today = LocalDate.now()
    // Montag der aktuellen Woche, davon aus rückwärts
    val thisMonday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val firstMonday = thisMonday.minusWeeks((weeks - 1).toLong())
    var selected by remember { mutableStateOf<LocalDate?>(null) }
    val measurer = rememberTextMeasurer()
    val monthStyle = TextStyle(fontSize = 10.sp, color = p.textTertiary)
    val locale = Lang.locale
    val safeGoal = goal.coerceAtLeast(1)

    fun level(minutes: Int): Int {
        if (minutes <= 0) return 0
        val f = minutes / safeGoal.toFloat()
        return when {
            f < 0.34f -> 1
            f < 0.67f -> 2
            f < 1f -> 3
            else -> 4
        }
    }
    fun colorFor(level: Int): Color = when (level) {
        0 -> p.glassAlpha(if (p.isDark) 0.07f else 0.06f)
        1 -> p.accent.copy(alpha = 0.28f)
        2 -> p.accent.copy(alpha = 0.50f)
        3 -> p.accent.copy(alpha = 0.75f)
        else -> p.accent
    }

    Column(modifier) {
        Row {
            // Wochentage links (nur jede zweite Zeile beschriftet, wie bei GitHub)
            Column(Modifier.padding(top = Space.s4, end = Space.s2)) {
                val labels = if (Lang.german) listOf("Mo", "", "Mi", "", "Fr", "", "So") else listOf("Mon", "", "Wed", "", "Fri", "", "Sun")
                labels.forEach { label ->
                    Box(Modifier.height(cell + gap), contentAlignment = Alignment.CenterStart) {
                        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.sp), color = p.textTertiary)
                    }
                }
            }
            val scroll = rememberScrollState()
            LaunchedEffect(scroll.maxValue) { scroll.scrollTo(scroll.maxValue) }
            Box(Modifier.weight(1f).horizontalScroll(scroll)) {
                Canvas(
                    Modifier
                        .width((cell + gap) * weeks)
                        .height(16.dp + (cell + gap) * 7)
                        .pointerInput(weeks, cell, gap) {
                            detectTapGestures { pos ->
                                val step = (cell + gap).toPx()
                                val top = 16.dp.toPx()
                                val col = (pos.x / step).toInt()
                                val row = ((pos.y - top) / step).toInt()
                                if (col in 0 until weeks && row in 0..6 && pos.y >= top) {
                                    val date = firstMonday.plusWeeks(col.toLong()).plusDays(row.toLong())
                                    selected = if (date.isAfter(today) || date == selected) null else date
                                }
                            }
                        }
                ) {
                    val step = (cell + gap).toPx()
                    val c = cell.toPx()
                    val top = 16.dp.toPx()
                    val radius = CornerRadius(c * 0.26f)
                    var lastMonth = -1
                    for (w in 0 until weeks) {
                        val monday = firstMonday.plusWeeks(w.toLong())
                        // Monatsname über der ersten Woche eines Monats
                        if (monday.monthValue != lastMonth) {
                            if (lastMonth != -1 || monday.dayOfMonth <= 7) {
                                val name = monday.month.getDisplayName(JTextStyle.SHORT, locale).trimEnd('.')
                                if (w < weeks - 1) drawText(measurer, name, Offset(w * step, 0f), monthStyle)
                            }
                            lastMonth = monday.monthValue
                        }
                        for (d in 0..6) {
                            val date = monday.plusDays(d.toLong())
                            if (date.isAfter(today)) continue
                            val tl = Offset(w * step, top + d * step)
                            val lv = level(minutesOn(date))
                            drawRoundRect(colorFor(lv), tl, Size(c, c), radius)
                            if (cartoon && lv > 0) drawRoundRect(p.ink.copy(alpha = 0.6f), tl, Size(c, c), radius, style = Stroke(1.dp.toPx()))
                            if (date == today) drawRoundRect(p.text.copy(alpha = 0.85f), tl, Size(c, c), radius, style = Stroke(1.5.dp.toPx()))
                            if (date == selected) drawRoundRect(p.accent, Offset(tl.x - 2f, tl.y - 2f), Size(c + 4f, c + 4f), CornerRadius(c * 0.3f), style = Stroke(2.dp.toPx()))
                        }
                    }
                }
            }
        }
        if (showLegend) {
            Spacer(Modifier.height(Space.s3))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val sel = selected
                Text(
                    if (sel != null) {
                        val fmt = DateTimeFormatter.ofPattern(if (Lang.german) "EEE, d. MMM" else "EEE, MMM d", locale)
                        "${sel.format(fmt)}, ${minutesOn(sel)} min"
                    } else {
                        tr("Tippe auf einen Tag", "Tap a day")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (sel != null) p.text else p.textTertiary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                Text(tr("Weniger", "Less"), style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
                Spacer(Modifier.width(Space.s2))
                Row(horizontalArrangement = Arrangement.spacedBy(Space.s1)) {
                    for (lv in 0..4) {
                        Canvas(Modifier.size(11.dp)) {
                            drawRoundRect(colorFor(lv), cornerRadius = CornerRadius(size.width * 0.26f))
                        }
                    }
                }
                Spacer(Modifier.width(Space.s2))
                Text(tr("Mehr", "More"), style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
            }
        }
    }
}
