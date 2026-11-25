package de.funke.app.ui.screens

import de.funke.app.ui.components.rememberLoop
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.runtime.key

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.text.drawText
import de.funke.app.ui.AppState
import de.funke.app.ui.GoalLevel
import de.funke.app.ui.Lang
import de.funke.app.ui.StreakGoal
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.daysText
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sqrt

/*
 * Ziele: Tagesziel (Minuten pro Tag, als Stufe) und Streak-Ziel (Tage am Stück).
 * Wird in der Einrichtung und im Reiter "Fortschritt" benutzt.
 */

/** Kleines Abzeichen, z. B. "Empfohlen". */
@Composable
private fun Badge(text: String) {
    val p = Funke.colors
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = p.onAccent,
        modifier = Modifier
            .clip(Shapes.pill)
            .background(p.accent)
            .padding(horizontal = Space.s2, vertical = Space.s1),
    )
}

/** Tagesziel als vier Stufen: Balken, Name, Minuten. */
@Composable
fun GoalLevelPicker(selected: GoalLevel, onSelect: (GoalLevel) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Space.s3)) {
        GoalLevel.entries.forEachIndexed { i, level ->
            TierRow(
                index = i,
                name = level.label,
                value = "${level.minutes} min",
                recommended = level.recommended,
                selected = level == selected,
                onClick = { onSelect(level) },
            )
        }
    }
}

/**
 * Streak-Ziel als vier Stufen, genau wie das Tagesziel: Balken, Name der Stufe ("Gewohnheit"), Tage.
 * Seit 15.0 für die Einrichtung (vorher vier Zahlenkacheln über einem großen Kalender).
 */
@Composable
fun StreakGoalList(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Space.s3)) {
        StreakGoal.entries.forEachIndexed { i, g ->
            TierRow(
                index = i,
                name = g.label,
                value = daysText(g.days),
                recommended = g.recommended,
                selected = g.days == selected,
                onClick = { onSelect(g.days) },
            )
        }
    }
}

/** Eine Stufe als Zeile: Stufen-Balken (1 bis 4), Name, ggf. "Empfohlen", rechts der Wert. */
@Composable
private fun TierRow(index: Int, name: String, value: String, recommended: Boolean, selected: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    de.funke.app.ui.components.LipOption(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = Space.s4), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.width(24.dp)) {
                for (b in 0..3) {
                    Box(
                        Modifier
                            .width(4.dp)
                            .height((6 + b * 4).dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (b <= index) (if (selected) p.accent else p.textSecondary) else p.glassAlpha(0.14f))
                    )
                }
            }
            Spacer(Modifier.width(Space.s3))
            Text(name, style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1)
            if (recommended) {
                Spacer(Modifier.width(Space.s2))
                Badge(tr("Empfohlen", "Recommended"))
            }
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(Space.s2))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"),
                color = if (selected) p.accent else p.textSecondary,
                maxLines = 1,
            )
        }
    }
}

/** Streak-Ziele als vier Kacheln (7 / 14 / 30 / 60 Tage). */
@Composable
fun StreakGoalPicker(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
        StreakGoal.entries.forEach { g ->
            val on = g.days == selected
            de.funke.app.ui.components.LipOption(
                selected = on,
                onClick = { onSelect(g.days) },
                modifier = Modifier.weight(1f).height(76.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${g.days}", style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum, ss03"), color = if (on) p.accent else p.text)
                    Text(tr("Tage", "days"), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
                }
            }
        }
    }
}

/** Datum, an dem das Ziel erreicht ist (heute zählt als Tag 1, wenn heute noch nichts geschafft ist). */
fun streakGoalDate(goal: Int, streak: Int, doneToday: Boolean): LocalDate {
    val left = (goal - streak).coerceAtLeast(0)
    return if (doneToday) LocalDate.now().plusDays(left.toLong()) else LocalDate.now().plusDays((left - 1).coerceAtLeast(0).toLong())
}

fun formatGoalDate(d: LocalDate): String =
    d.format(DateTimeFormatter.ofPattern(if (Lang.german) "EEE, d. MMM" else "EEE, MMM d", Lang.locale))

/**
 * Das Streak-Ziel als Wochen-Kalender (Mo–So) vom Beginn des Streaks bis zum Zieltag.
 * Geschaffte Tage leuchten, heute hat einen Ring, der Zieltag einen Stern.
 * Ein neuer Monat steht seit 15.0 direkt im Feld seines ersten Tags ("Jan" statt "1") – vorher stand der
 * Monatsname eingequetscht zwischen den Reihen.
 */
@Composable
fun StreakGoalCalendar(goal: Int, streak: Int, doneToday: Boolean, modifier: Modifier = Modifier, cellHeight: androidx.compose.ui.unit.Dp = 34.dp) {
    val p = Funke.colors
    val measurer = androidx.compose.ui.text.rememberTextMeasurer()
    val today = LocalDate.now()
    val done = streak.coerceAtMost(goal)
    val start = if (done == 0) today else if (doneToday) today.minusDays((done - 1).toLong()) else today.minusDays(done.toLong())
    val goalDay = start.plusDays((goal - 1).coerceAtLeast(0).toLong())
    val first = start.minusDays((start.dayOfWeek.value - 1).toLong())
    val last = goalDay.plusDays((7 - goalDay.dayOfWeek.value).toLong())
    val rows = (java.time.temporal.ChronoUnit.DAYS.between(first, last).toInt() + 1) / 7
    val dayStyle = MaterialTheme.typography.labelMedium
    val headStyle = MaterialTheme.typography.labelSmall
    val headH = 20.dp
    Canvas(modifier.fillMaxWidth().height(headH + cellHeight * rows)) {
        val cw = size.width / 7f
        val ch = cellHeight.toPx()
        val top = headH.toPx()
        val half = min(cw, ch) * 0.40f
        val corner = androidx.compose.ui.geometry.CornerRadius(half * 0.62f)
        // Wochentage
        for (i in 0 until 7) {
            val label = first.plusDays(i.toLong()).dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Lang.locale).replace(".", "").take(2)
            val m = measurer.measure(label, headStyle.copy(color = p.textTertiary))
            drawText(m, topLeft = Offset(cw * (i + 0.5f) - m.size.width / 2f, 0f))
        }
        fun index(d: LocalDate) = java.time.temporal.ChronoUnit.DAYS.between(first, d).toInt()
        fun center(d: LocalDate): Offset {
            val idx = index(d)
            return Offset(cw * (idx % 7 + 0.5f), top + ch * (idx / 7 + 0.5f))
        }
        fun isDone(d: LocalDate) = done > 0 && !d.isBefore(start) && !d.isAfter(goalDay) && (d.isBefore(today) || (d == today && doneToday))

        // Geschaffte Tage einer Woche hängen als durchgehende Spur zusammen
        var d = first
        while (!d.isAfter(last)) {
            if (isDone(d) && (index(d) % 7 == 0 || !isDone(d.minusDays(1)))) {
                var e = d
                while (index(e) % 7 != 6 && isDone(e.plusDays(1))) e = e.plusDays(1)
                val a = center(d)
                val b = center(e)
                drawRoundRect(p.accent, Offset(a.x - half, a.y - half), androidx.compose.ui.geometry.Size(b.x - a.x + 2 * half, 2 * half), corner)
            }
            d = d.plusDays(1)
        }

        d = first
        while (!d.isAfter(last)) {
            val c = center(d)
            val inRange = !d.isBefore(start) && !d.isAfter(goalDay)
            val done1 = isDone(d)
            val tl = Offset(c.x - half, c.y - half)
            val sz = androidx.compose.ui.geometry.Size(2 * half, 2 * half)
            if (!done1 && inRange) {
                drawRoundRect(p.glassAlpha(if (p.isDark) 0.07f else 0.06f), tl, sz, corner)
            }
            if (d == goalDay && !done1) {
                drawRoundRect(p.accent.copy(alpha = 0.14f), tl, sz, corner)
                drawRoundRect(p.accent, tl, sz, corner, style = Stroke(2.dp.toPx()))
            } else if (d == today && !done1) {
                drawRoundRect(p.accent, Offset(tl.x - 2.dp.toPx(), tl.y - 2.dp.toPx()), androidx.compose.ui.geometry.Size(sz.width + 4.dp.toPx(), sz.height + 4.dp.toPx()),
                    androidx.compose.ui.geometry.CornerRadius(corner.x + 2.dp.toPx()), style = Stroke(2.dp.toPx()))
            }
            // Zahl (am Zieltag ein Stern). Am Monatsersten steht der Monat selbst im Feld ("Jan").
            if (d == goalDay && !done1) {
                drawPath(star(c, half * 0.62f), p.accent)
            } else {
                val first1 = d.dayOfMonth == 1
                val color = when {
                    done1 -> p.onAccent
                    first1 && inRange -> p.accent
                    inRange -> p.text
                    else -> p.textTertiary.copy(alpha = 0.40f)
                }
                val label = if (first1) {
                    d.month.getDisplayName(java.time.format.TextStyle.SHORT, Lang.locale).replace(".", "").take(3)
                } else {
                    d.dayOfMonth.toString()
                }
                val m = measurer.measure(label, (if (first1) headStyle else dayStyle).copy(color = color))
                drawText(m, topLeft = Offset(c.x - m.size.width / 2f, c.y - m.size.height / 2f))
            }
            d = d.plusDays(1)
        }
    }
}

private fun LocalDate.coerceAtMost(other: LocalDate): LocalDate = if (isAfter(other)) other else this

/** Fünfzackiger Stern um [c]. */
private fun star(c: Offset, r: Float): androidx.compose.ui.graphics.Path = androidx.compose.ui.graphics.Path().apply {
    for (i in 0 until 10) {
        val a = Math.toRadians(-90.0 + i * 36.0)
        val rr = if (i % 2 == 0) r else r * 0.45f
        val x = c.x + (rr * kotlin.math.cos(a)).toFloat()
        val y = c.y + (rr * kotlin.math.sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Karte im Fortschritt: Wie weit bin ich beim Streak-Ziel? Geschafft → nächstes Ziel. */
@Composable
fun StreakGoalCard(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val goal = state.streakGoal
    val streak = state.streak
    Column(
        modifier
            .fillMaxWidth()
            .glass(Shapes.large)
            .padding(Space.s5),
    ) {
        if (goal <= 0) {
            SectionLabel(tr("Streak-Ziel", "Streak goal"))
            Spacer(Modifier.height(Space.s3))
            StreakGoalPicker(selected = 0, onSelect = state::updateStreakGoal)
            return@Column
        }
        val reached = streak >= goal
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(tr("Streak-Ziel", "Streak goal"), Modifier.weight(1f))
            Text("$streak / ${daysText(goal)}", style = MaterialTheme.typography.labelLarge, color = p.textSecondary)
        }
        Spacer(Modifier.height(Space.s3))
        StreakGoalCalendar(goal, streak, state.streakDoneToday, cellHeight = 30.dp)
        Spacer(Modifier.height(Space.s3))
        if (reached) {
            Text(tr("Ziel geschafft!", "Goal reached!"), style = MaterialTheme.typography.titleMedium, color = p.accent)
            Spacer(Modifier.height(Space.s3))
            val next = StreakGoal.nextAfter(goal)
            PillButton(
                tr("Nächstes Ziel: ${daysText(next)}", "Next goal: ${daysText(next)}"),
                filled = true,
                onClick = { state.updateStreakGoal(next) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            val left = goal - streak
            val date = streakGoalDate(goal, streak, state.streakDoneToday)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    tr("Noch ${daysText(left)}", "${daysText(left)} to go"),
                    style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"),
                    color = p.text,
                    modifier = Modifier.weight(1f),
                )
                Text(formatGoalDate(date), style = MaterialTheme.typography.labelLarge, color = p.accent)
            }
            var change by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
            Text(
                if (change) tr("Fertig", "Done") else tr("Ziel ändern", "Change goal"),
                style = MaterialTheme.typography.labelLarge,
                color = p.accent,
                modifier = Modifier.padding(top = Space.s2).bouncyClickable(shape = Shapes.pill) { change = !change }.padding(vertical = Space.s1),
            )
            if (change) {
                Spacer(Modifier.height(Space.s2))
                StreakGoalPicker(selected = goal, onSelect = state::updateStreakGoal)
            }
        }
    }
}
