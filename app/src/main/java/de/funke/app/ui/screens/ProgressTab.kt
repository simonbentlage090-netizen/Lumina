package de.funke.app.ui.screens

import de.funke.app.ui.components.rememberLoop
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import de.funke.app.ui.components.bouncyClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.AppState
import de.funke.app.ui.GoalOptions
import de.funke.app.ui.components.CheckIcon
import de.funke.app.ui.components.FlameIcon
import de.funke.app.ui.components.StreakFlame
import de.funke.app.ui.components.ReadingHeatmap
import de.funke.app.ui.Lang
import de.funke.app.ui.daysText
import de.funke.app.ui.daysWord
import de.funke.app.ui.tr
import androidx.compose.ui.graphics.SolidColor
import de.funke.app.ui.components.ProgressRing
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.WheelPicker
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import kotlinx.coroutines.delay

private val WeekLetters: List<String>
    get() = if (Lang.german) listOf("M", "D", "M", "D", "F", "S", "S") else listOf("M", "T", "W", "T", "F", "S", "S")

/** Große Streak-Zahl: Inter Display, mittleres Gewicht, gleich breite Ziffern. */
private val BigNumber: TextStyle
    @Composable get() = de.funke.app.ui.theme.NumberStyle.copy(fontSize = 64.sp, lineHeight = 68.sp, letterSpacing = (-2).sp)

/** Reiter 3: Streak, Woche als Balken, heute als Ring und das Tagesziel. */
@Composable
fun ProgressTab(state: AppState, modifier: Modifier = Modifier, header: (@Composable () -> Unit)? = null) {
    val p = Funke.colors
    val flat = Funke.flat
    val streak = state.streak
    val goal = state.dailyGoal.coerceAtLeast(1)

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = NavBarSpace + Space.s6),
    ) {
        if (header != null) header() else {
            Text(
                tr("Fortschritt", "Progress"),
                style = MaterialTheme.typography.headlineLarge,
                color = p.text,
                modifier = Modifier.reveal(0),
            )
            Spacer(Modifier.height(Space.s5))
        }

        // ----- Streak -----
        val still = de.funke.app.ui.theme.reducedMotion
        val flame = if (still) 0.5f else {
            val f by rememberLoop(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(2400, easing = de.funke.app.ui.theme.Motion.EaseInOut), RepeatMode.Reverse),
                label = "flameT",
            )
            f
        }
        // Seit 15.2 ruhiger: Der Streak steht frei oben (wie die Kopfzeile der Startseite), Rekord und Streak-Eis
        // als eine leise Zeile darunter. Heute und die Woche teilen sich eine Fläche, das Tagesziel braucht keine.
        Row(
            Modifier
                .fillMaxWidth()
                .reveal(60)
                .drawBehind {
                    if (streak > 0 && !flat) {
                        val c = androidx.compose.ui.geometry.Offset(size.width - 32.dp.toPx(), size.height * 0.5f)
                        val r = size.height * 1.3f
                        drawCircle(
                            Brush.radialGradient(listOf(p.accent.copy(alpha = 0.10f + 0.04f * flame), Color.Transparent), center = c, radius = r),
                            radius = r,
                            center = c,
                        )
                    }
                }
                .padding(top = Space.s2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                SectionLabel("Streak")
                AnimatedContent(
                    targetState = streak,
                    transitionSpec = {
                        (slideInVertically(tween(de.funke.app.ui.theme.Motion.Medium, easing = de.funke.app.ui.theme.Motion.EaseOut)) { it / 3 } +
                            fadeIn(tween(de.funke.app.ui.theme.Motion.Base, easing = de.funke.app.ui.theme.Motion.EaseOut))) togetherWith
                            (slideOutVertically(tween(de.funke.app.ui.theme.Motion.Fast, easing = de.funke.app.ui.theme.Motion.EaseOut)) { -it / 3 } +
                                fadeOut(tween(de.funke.app.ui.theme.Motion.Fast, easing = de.funke.app.ui.theme.Motion.EaseOut)))
                    },
                    label = "streakCount",
                ) { count ->
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$count", style = BigNumber, color = p.text)
                        Spacer(Modifier.width(Space.s2))
                        Text(
                            daysWord(count),
                            style = MaterialTheme.typography.titleLarge,
                            color = p.textSecondary,
                            modifier = Modifier.padding(bottom = Space.s3),
                        )
                    }
                }
            }
            // Die Flamme brennt, solange der Streak lebt
            StreakFlame(streak, Modifier.size(width = 64.dp, height = 78.dp), showNumber = false)
        }
        Row(Modifier.fillMaxWidth().reveal(75).padding(top = Space.s2), verticalAlignment = Alignment.CenterVertically) {
            InlineStat(
                icon = { androidx.compose.material3.Icon(androidx.compose.ui.res.painterResource(de.funke.app.R.drawable.ic_trophy), contentDescription = null, tint = p.tame(SparkGold), modifier = Modifier.size(16.dp)) },
                text = tr("Rekord ${state.bestStreak}", "Best ${state.bestStreak}"),
            )
            Spacer(Modifier.width(Space.s5))
            InlineStat(
                icon = { de.funke.app.ui.components.IceIcon(Modifier.size(16.dp)) },
                text = tr("Streak-Eis ${state.streakFreezes}/${de.funke.app.progress.Streaks.MAX_FREEZES}", "Freezes ${state.streakFreezes}/${de.funke.app.progress.Streaks.MAX_FREEZES}"),
            )
        }

        Spacer(Modifier.height(Space.s6))

        // ----- Heute und diese Woche (eine Fläche) -----
        val fraction by animateFloatAsState(
            (state.todayMinutes / goal.toFloat()).coerceIn(0f, 1f),
            de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Slow),
            label = "todayFraction",
        )
        Column(
            Modifier
                .fillMaxWidth()
                .reveal(120)
                .glass(Shapes.large)
                .padding(Space.s5),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) {
                    ProgressRing(fraction, Modifier.fillMaxSize(), stroke = 9.dp)
                    if (state.todayMinutes >= goal) {
                        de.funke.app.ui.components.AnimatedCheckBadge(size = 36.dp)
                    } else {
                        Text(
                            "${(fraction * 100).toInt()} %",
                            style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"),
                            color = p.text,
                        )
                    }
                }
                Spacer(Modifier.width(Space.s5))
                Column {
                    SectionLabel(tr("Heute", "Today"))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${state.todayMinutes}", style = MaterialTheme.typography.headlineLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.text)
                        Text(
                            " / ${state.dailyGoal} min",
                            style = MaterialTheme.typography.titleMedium,
                            color = p.textSecondary,
                            modifier = Modifier.padding(bottom = Space.s1),
                        )
                    }
                    Text(
                        if (state.todayMinutes >= goal) tr("Tagesziel erreicht", "Daily goal reached")
                        else tr("Noch ${goal - state.todayMinutes} min", "${goal - state.todayMinutes} min to go"),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.todayMinutes >= goal) p.success else p.textTertiary,
                    )
                }
            }
            Box(Modifier.padding(vertical = Space.s5).fillMaxWidth().height(1.dp).background(p.divider))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionLabel(tr("Diese Woche", "This week"), Modifier.weight(1f))
                Text(minutesText(state.weekMinutes), style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.textSecondary)
            }
            Spacer(Modifier.height(Space.s4))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (i in 0..6) {
                    val status = state.weekDay(i)
                    DayBar(
                        letter = WeekLetters[i],
                        fill = (state.weekDayMinutes(i) / goal.toFloat()).coerceIn(0f, 1f),
                        done = status.done,
                        today = status.today,
                        future = status.future,
                        order = i,
                    )
                }
            }
        }

        Spacer(Modifier.height(Space.s4))

        // ----- Lese-Kalender (ein Jahr) -----
        Column(
            Modifier
                .fillMaxWidth()
                .reveal(180)
                .glass(Shapes.large)
                .padding(Space.s5)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionLabel(tr("Dein Jahr", "Your year"), Modifier.weight(1f))
                val days = state.daysReadLastYear
                Text(tr("$days Tage mit Fokus", "$days days of focus"), style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.textSecondary)
            }
            Spacer(Modifier.height(Space.s4))
            ReadingHeatmap(minutesOn = state::minutesOn, goal = goal)
        }

        Spacer(Modifier.height(Space.s4))

        // ----- Streak-Ziel -----
        StreakGoalCard(state, Modifier.reveal(210))

        // ----- Tagesziel: die Stufen selbst sind schon Flächen, deshalb ohne Karte drumherum -----
        SectionLabel(
            tr("Tagesziel", "Daily goal"),
            Modifier.padding(start = Space.s1, top = Space.s8, bottom = Space.s3).reveal(240),
        )
        GoalLevelPicker(selected = state.goalLevel, onSelect = { state.updateDailyGoal(it.minutes) }, modifier = Modifier.reveal(240))
    }
}

/** "2 h 14 min", "45 min" */
private fun minutesText(min: Int): String = when {
    min < 60 -> "$min min"
    min % 60 == 0 -> "${min / 60} h"
    else -> "${min / 60} h ${min % 60} min"
}

/** Leise Angabe in einer Zeile: kleines Symbol und Text. */
@Composable
private fun InlineStat(icon: @Composable () -> Unit, text: String) {
    val p = Funke.colors
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.semantics(mergeDescendants = true) {}) {
        icon()
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.textSecondary)
    }
}

/**
 * Ein Tag als Kapsel, die sich von unten füllt. Beim Öffnen wachsen die Balken
 * nacheinander mit Feder hoch. Heute bekommt einen leuchtenden Punkt.
 */
@Composable
private fun DayBar(
    letter: String,
    fill: Float,
    done: Boolean,
    today: Boolean,
    future: Boolean,
    order: Int,
) {
    val p = Funke.colors
    val flat = Funke.flat
    val still = de.funke.app.ui.theme.reducedMotion
    val grow = remember { Animatable(if (still) fill else 0f) }
    LaunchedEffect(fill, still) {
        if (still) { grow.snapTo(fill); return@LaunchedEffect }
        delay(80L + order * de.funke.app.ui.theme.Motion.Stagger)
        grow.animateTo(fill, tween(de.funke.app.ui.theme.Motion.Slow, easing = de.funke.app.ui.theme.Motion.EaseOut))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .width(30.dp)
                .height(86.dp)
                .clip(Shapes.pill)
                .background(if (future) p.track.copy(alpha = p.track.alpha * 0.5f) else p.track),
            contentAlignment = Alignment.BottomCenter,
        ) {
            val h = grow.value.coerceIn(0f, 1.05f)
            if (h > 0.01f) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(h.coerceAtMost(1f).coerceAtLeast(0.34f))
                        .graphicsLayer { alpha = (h / 0.15f).coerceIn(0f, 1f) }
                        .clip(Shapes.pill)
                        .background(
                            when {
                                flat -> SolidColor(if (done) p.accent else p.accent.copy(alpha = 0.40f))
                                done -> p.accentBrush
                                else -> SolidColor(p.accent.copy(alpha = 0.32f))
                            }
                        ),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    if (done) CheckIcon(p.onAccent, Modifier.padding(top = Space.s2).size(14.dp))
                }
            }
        }
        Spacer(Modifier.height(Space.s2))
        Text(
            letter,
            style = MaterialTheme.typography.labelMedium,
            color = if (today) p.text else p.textTertiary,
        )
        Spacer(Modifier.height(Space.s1))
        Box(
            Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (today) p.accent else Color.Transparent)
        )
    }
}

/**
 * Reiter "Fortschritt" (seit 13.4): oben ein Umschalter zwischen deinem Lesen und der Bildschirmzeit
 * (die früher einen eigenen Reiter hatte). Darunter jeweils die gewohnte Ansicht.
 */
@Composable
fun ProgressHub(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    var section by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    androidx.compose.runtime.LaunchedEffect(de.funke.app.ui.DeepLink.progressSection) {
        val s = de.funke.app.ui.DeepLink.progressSection
        if (s >= 0) { section = s; de.funke.app.ui.DeepLink.progressSection = -1 }
    }
    val header: @Composable () -> Unit = {
        Text(tr("Fortschritt", "Progress"), style = MaterialTheme.typography.headlineLarge, color = p.text, modifier = Modifier.reveal(0))
        Spacer(Modifier.height(Space.s5))
        de.funke.app.ui.components.SegmentedToggle(
            options = listOf(tr("Fokus", "Focus"), tr("Bildschirmzeit", "Screen time")),
            selected = section,
            onSelect = { section = it },
            modifier = Modifier.reveal(20),
        )
        Spacer(Modifier.height(Space.s5))
    }
    val still = de.funke.app.ui.theme.reducedMotion
    androidx.compose.animation.AnimatedContent(
        targetState = section,
        transitionSpec = {
            androidx.compose.animation.fadeIn(tween(if (still) 0 else de.funke.app.ui.theme.Motion.Medium, easing = de.funke.app.ui.theme.Motion.EaseOut)) togetherWith
                androidx.compose.animation.fadeOut(tween(if (still) 0 else de.funke.app.ui.theme.Motion.Fast))
        },
        modifier = modifier,
        label = "progressSection",
    ) { s ->
        if (s == 0) ProgressTab(state, header = header) else ScreenTimeTab(state, header = header)
    }
}
