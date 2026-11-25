package de.funke.app.ui.screens

import androidx.compose.animation.core.snap
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.ConfettiColors
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.ui.ResultKind
import de.funke.app.ui.SessionResult
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.CheckIcon
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LuminaLogo
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.FlameIcon
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.SadFace
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import de.funke.app.ui.daysText
import de.funke.app.ui.components.StreakFlame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ResultScreen(
    result: SessionResult,
    streak: Int,
    todayMinutes: Int,
    dailyGoal: Int,
    onDone: () -> Unit,
    notesCount: Int = 0,
    onNotes: () -> Unit = {},
) {
    val p = Funke.colors
    val haptic = LocalHapticFeedback.current
    val completed = result.kind == ResultKind.Completed

    val title = when (result.kind) {
        ResultKind.Completed -> tr("Geschafft!", "You did it!")
        ResultKind.EndedEarly -> tr("Früher beendet", "Stopped early")
        ResultKind.Abandoned -> tr("Abgebrochen", "Stopped")
    }
    val subtitle = when (result.kind) {
        ResultKind.Completed -> tr("${result.readMinutes} Minuten Fokus.", "${result.readMinutes} minutes of focus.")
        ResultKind.EndedEarly -> tr(
            "Aufgabe gelöst. Deine ${result.readMinutes} Minuten zählen fürs Tagesziel. " +
                "Den Streak gibt es aber nur für ganze Sessions.",
            "Task solved. Your ${result.readMinutes} minutes count toward your daily goal. " +
                "The streak only grows with full sessions, though.",
        )
        ResultKind.Abandoned -> tr(
            "Nach ${result.readMinutes} von ${result.plannedMinutes} Minuten abgebrochen. Diese Session zählt nicht. Morgen wieder?",
            "Stopped after ${result.readMinutes} of ${result.plannedMinutes} minutes. This session doesn't count. Again tomorrow?",
        )
    }

    // Das Symbol poppt mit Feder-Effekt ins Bild.
    val pop = remember { Animatable(0.55f) }
    val still = de.funke.app.ui.theme.reducedMotion
    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        // kleines Nachfedern (wie transitions.dev "bob"), bei "Bewegung reduzieren" sofort da
        pop.animateTo(1f, if (still) snap() else spring(dampingRatio = 0.7f, stiffness = 380f))
    }

    AmbientBackground {
        if (completed) {
            if (!still) ConfettiRain(Modifier.fillMaxSize())
            SparkBurst(Modifier.fillMaxSize())
        }

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = Space.s6, vertical = Space.s4),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            // Geschafft: Lumi springt vor Freude. Abgebrochen: das Buch liegt am Boden, Lumi ist traurig.
            if (de.funke.app.ui.LocalSidekick.current) {
            Lumi2D(
                pose = if (completed) LumiPose.Happy else LumiPose.Sad,
                animateChange = completed,
                contentDescription = if (completed) tr("Lumi freut sich", "Lumi is happy") else tr("Lumi ist traurig", "Lumi is sad"),
                modifier = Modifier
                    .size(230.dp)
                    .graphicsLayer {
                        scaleX = pop.value
                        scaleY = pop.value
                    },
            )
            } else {
                // Ohne Lumi: das Lumina-Zeichen
                LuminaLogo(
                    Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            scaleX = pop.value
                            scaleY = pop.value
                            alpha = if (completed) 1f else 0.55f
                        }
                )
            }

            Spacer(Modifier.height(Space.s3))
            Text(
                title,
                modifier = Modifier.reveal(250),
                style = MaterialTheme.typography.displayLarge,
                color = p.text,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Space.s3))
            Text(
                subtitle,
                modifier = Modifier.reveal(380),
                style = MaterialTheme.typography.bodyLarge,
                color = p.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Space.s7))

            Row(
                Modifier
                    .fillMaxWidth()
                    .reveal(520),
                horizontalArrangement = Arrangement.spacedBy(Space.s3),
            ) {
                StatTile(
                    label = tr("Heute", "Today"),
                    value = "$todayMinutes / $dailyGoal min",
                    modifier = Modifier.weight(1f),
                    highlight = result.goalReached,
                )
                StatTile(
                    label = if (result.streakIncreased) "Streak +1" else "Streak",
                    value = daysText(streak),
                    modifier = Modifier.weight(1f),
                    highlight = result.streakIncreased,
                    showFlame = true,
                )
            }

            if (result.goalReached) {
                Spacer(Modifier.height(Space.s4))
                Text(
                    tr("Tagesziel erreicht!", "Daily goal reached!"),
                    modifier = Modifier.reveal(650),
                    style = MaterialTheme.typography.titleMedium,
                    color = p.accent,
                )
            }

            if (result.freezeEarned) {
                Spacer(Modifier.height(Space.s3))
                Row(Modifier.reveal(720), verticalAlignment = Alignment.CenterVertically) {
                    de.funke.app.ui.components.IceIcon(Modifier.size(18.dp))
                    Spacer(Modifier.width(Space.s2))
                    Text(
                        tr("Streak-Eis verdient!", "Streak freeze earned!"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = p.textSecondary,
                    )
                }
            }

            if (notesCount > 0) {
                Spacer(Modifier.height(Space.s4))
                Row(
                    Modifier
                        .reveal(740)
                        .bouncyClickable(shape = Shapes.md, onClick = onNotes)
                        .padding(vertical = Space.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NotepadIcon(p.accent, Modifier.size(16.dp))
                    Spacer(Modifier.width(Space.s2))
                    Text(
                        if (notesCount == 1) tr("1 Notiz aus dieser Fokuszeit ansehen", "View 1 note from this session")
                        else tr("$notesCount Notizen aus dieser Fokuszeit ansehen", "View $notesCount notes from this session"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.accent,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Box(
                Modifier
                    .navigationBarsPadding()
                    .reveal(700)
            ) {
                PrimaryButton(tr("Weiter", "Continue"), onClick = onDone)
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    showFlame: Boolean = false,
) {
    val p = Funke.colors
    val shape = Shapes.card
    Row(
        modifier
            .glass(shape, strong = highlight)
            .then(if (highlight) Modifier.border(1.dp, p.accent.copy(alpha = 0.6f), shape) else Modifier)
            .padding(Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showFlame) {
            StreakFlame(if (highlight) 1 else 0, Modifier.size(width = 26.dp, height = 32.dp), showNumber = false)
            Spacer(Modifier.width(Space.s3))
        }
        Column {
            SectionLabel(label)
            Spacer(Modifier.height(Space.s1))
            Text(value, style = MaterialTheme.typography.titleMedium, color = p.text)
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Funken-Explosion
// ---------------------------------------------------------------------------------------------

private class Spark(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val spin: Float,
)

/** Konfetti-Funken in den Farben des Schemas, die aus der Mitte nach außen fliegen und leicht herunterfallen. */
@Composable
private fun SparkBurst(modifier: Modifier = Modifier) {
    val p = Funke.colors
    val sparks = remember(p.id) {
        val palette = listOf(p.accent, p.accentAlt, p.text, p.accent, p.accentAlt)
        List(44) {
            Spark(
                angle = Random.nextFloat() * (2f * PI.toFloat()),
                speed = 0.35f + Random.nextFloat() * 0.65f,
                size = 3f + Random.nextFloat() * 5f,
                color = palette.random(),
                spin = Random.nextFloat() * 2f - 1f,
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(2300, easing = LinearOutSlowInEasing))
    }

    Canvas(modifier) {
        val t = progress.value
        val origin = Offset(size.width / 2f, size.height * 0.34f)
        val maxDistance = size.minDimension * 0.62f
        sparks.forEach { spark ->
            val distance = maxDistance * spark.speed * FastOutSlowInEasing.transform(t)
            val x = origin.x + cos(spark.angle) * distance
            val y = origin.y + sin(spark.angle) * distance + 220f * t * t * spark.speed
            val w = spark.size * density * 2.2f
            val h = spark.size * density
            rotate(degrees = spark.spin * 360f * t, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = spark.color.copy(alpha = (1f - t).coerceIn(0f, 1f)),
                    topLeft = Offset(x - w / 2f, y - h / 2f),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(h / 3f, h / 3f),
                )
            }
        }
    }
}


/**
 * Konfetti, das von oben herabschwebt: Papierstreifen, die beim Fallen pendeln und sich drehen,
 * dabei "flattern" (werden schmal und wieder breit, als würden sie sich wenden).
 */
@Composable
private fun ConfettiRain(modifier: Modifier = Modifier) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val pieces = remember(p.id, cartoon) {
        val colors = if (cartoon) {
            ConfettiColors
        } else {
            listOf(p.accent, p.accentAlt, Color.White, p.glowA, p.accent)
        }
        List(80) {
            Confetto(
                x = Random.nextFloat(),
                delay = Random.nextFloat() * 0.35f,
                speed = 0.75f + Random.nextFloat() * 0.5f,
                sway = 10f + Random.nextFloat() * 22f,
                phase = Random.nextFloat() * 6.28f,
                spin = Random.nextFloat() * 2f - 1f,
                flutter = 3f + Random.nextFloat() * 5f,
                w = 6f + Random.nextFloat() * 5f,
                h = 10f + Random.nextFloat() * 8f,
                color = colors[it % colors.size],
                round = Random.nextFloat() < 0.25f,
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(4200, easing = LinearEasing))
    }
    Canvas(modifier) {
        val t = progress.value
        val outline = if (cartoon) p.ink else null
        pieces.forEach { c ->
            val local = ((t - c.delay) / (1f - c.delay)).coerceIn(0f, 1f) * c.speed
            if (local <= 0f || local >= 1f) return@forEach
            val y = -40f * density + local * (size.height + 80f * density)
            val x = c.x * size.width + sin(local * 7f + c.phase) * c.sway * density
            val alpha = if (local > 0.82f) (1f - local) / 0.18f else 1f
            val flip = abs(cos(local * c.flutter * 3f + c.phase))
            val w = c.w * density * (0.25f + 0.75f * flip)
            val h = c.h * density
            rotate(degrees = c.spin * 540f * local, pivot = Offset(x, y)) {
                if (c.round) {
                    drawCircle(c.color.copy(alpha = alpha), radius = c.w * density * 0.6f, center = Offset(x, y))
                } else {
                    drawRoundRect(
                        color = c.color.copy(alpha = alpha),
                        topLeft = Offset(x - w / 2f, y - h / 2f),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(2f * density),
                    )
                    if (outline != null) {
                        drawRoundRect(
                            color = outline.copy(alpha = alpha * 0.8f),
                            topLeft = Offset(x - w / 2f, y - h / 2f),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(2f * density),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(1.2f * density),
                        )
                    }
                }
            }
        }
    }
}

private class Confetto(
    val x: Float,
    val delay: Float,
    val speed: Float,
    val sway: Float,
    val phase: Float,
    val spin: Float,
    val flutter: Float,
    val w: Float,
    val h: Float,
    val color: Color,
    val round: Boolean,
)
