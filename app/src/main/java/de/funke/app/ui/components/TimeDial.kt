package de.funke.app.ui.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.core.Animatable
import de.funke.app.ui.LocalHaptics
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Große Minutenzahl in der Mitte des Rings: dieselbe Schrift wie die Überschriften der App (Inter Display,
 * mittleres Gewicht, eng gesetzt), Ziffern gleich breit – ruhig statt fett.
 */
private val DialNumberStyle: TextStyle
    @Composable get() = MaterialTheme.typography.displayLarge.copy(
        // Zahlenschrift des Looks (gleich breite Ziffern, damit die Zahl beim Drehen nicht wackelt)
        fontFamily = de.funke.app.ui.theme.LocalFontSet.current.let { f -> if (f.numbers == de.funke.app.ui.theme.Inter) de.funke.app.ui.theme.InterDisplay else f.numbers },
        fontWeight = FontWeight.Medium,
        fontSize = 80.sp,
        lineHeight = 84.sp,
        letterSpacing = (-2.4).sp,
        fontFeatureSettings = "tnum, ss03",
    )

/** Werte mit langem Strich am Ring (volle halbe Stunde, Stunde, zwei Stunden, vier Stunden). */
private val MajorValues = setOf(30, 60, 120, 240)

/** Uhrzeit, wann eine Session von [minutes] Minuten zu Ende wäre, wenn sie jetzt startet. Aktualisiert sich jede Minute. */
@Composable
private fun rememberEndTime(minutes: Int): String {
    var now by remember { mutableStateOf(java.time.LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            val wait = 60_000L - (System.currentTimeMillis() % 60_000L)
            kotlinx.coroutines.delay(wait + 50)
            now = java.time.LocalTime.now()
        }
    }
    return now.plusMinutes(minutes.toLong()).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
}


/**
 * Der Zeit-Ring: Man dreht mit dem Finger am Ring wie an einem Drehregler.
 * Jeder Schritt tickt in der Hand, die Zahl in der Mitte rollt in Drehrichtung weiter,
 * und der leuchtende Bogen federt zur neuen Länge.
 *
 * Wer in der Mitte wischt, scrollt ganz normal die Seite. Nur am Ring wird gedreht.
 */
@Composable
fun TimeDial(
    values: List<Int>,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    diameter: Dp = 280.dp,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val haptics = LocalHaptics.current
    val scope = rememberCoroutineScope()
    val currentValues by rememberUpdatedState(values)
    val currentSelected by rememberUpdatedState(selected)
    val currentOnSelected by rememberUpdatedState(onSelected)
    var dragging by remember { mutableStateOf(false) }
    // Während des Drehens folgt der Bogen dem Finger stufenlos.
    var dragFraction by remember { mutableFloatStateOf(0f) }

    // Ein Schritt = so viel Drehung, dass der Griff genau unter dem Finger bleibt.
    val stepDegrees = 360f / (values.size - 1).coerceAtLeast(1)

    fun fractionOf(value: Int): Float {
        val i = currentValues.indexOf(value).takeIf { it >= 0 }
            ?: currentValues.indices.minByOrNull { kotlin.math.abs(currentValues[it] - value) } ?: 0
        return if (currentValues.size > 1) i / (currentValues.size - 1f) else 1f
    }

    val shown = remember { Animatable(fractionOf(selected)) }
    LaunchedEffect(selected) {
        if (!dragging) shown.animateTo(fractionOf(selected), spring(dampingRatio = 0.72f, stiffness = 600f))
    }
    val fraction = if (dragging) dragFraction else shown.value

    val press by animateFloatAsState(
        targetValue = if (dragging) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Base),
        label = "dialPress",
    )
    val breath by rememberLoop(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath",
    )

    Box(
        modifier
            .size(diameter)
            .semantics {
                contentDescription = tr("Dauer: $selected Minuten. Am Ring drehen zum Ändern.", "Duration: $selected minutes. Turn the ring to change.")
            }
            .pointerInput(stepDegrees) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val radius = (down.position - c).getDistance()
                    // Nur am Ring drehen; in der Mitte passiert nichts.
                    if (radius < size.width * 0.28f) return@awaitEachGesture
                    down.consume()
                    val list = currentValues
                    val last = (list.size - 1).coerceAtLeast(1)
                    var index = list.indexOf(currentSelected).takeIf { it >= 0 }
                        ?: list.indices.minByOrNull { kotlin.math.abs(list[it] - currentSelected) } ?: 0
                    var accumulated = 0f
                    var lastAngle = angleOf(down.position - c)
                    var atEdge = false
                    dragFraction = index / last.toFloat()
                    dragging = true
                    haptics?.tick()
                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val a = angleOf(change.position - c)
                            var delta = a - lastAngle
                            if (delta > 180f) delta -= 360f
                            if (delta < -180f) delta += 360f
                            lastAngle = a
                            accumulated += delta

                            while (accumulated >= stepDegrees / 2f && index < last) {
                                index++
                                accumulated -= stepDegrees
                                haptics?.detent(if (list[index] in MajorValues) 0.9f else 0.35f)
                                currentOnSelected(list[index])
                            }
                            while (accumulated <= -stepDegrees / 2f && index > 0) {
                                index--
                                accumulated += stepDegrees
                                haptics?.detent(if (list[index] in MajorValues) 0.9f else 0.35f)
                                currentOnSelected(list[index])
                            }
                            // Am Anfang und Ende: spürbarer Anschlag, der Bogen gibt nur ein wenig nach.
                            val pushingPast = (index == last && accumulated > 0f) || (index == 0 && accumulated < 0f)
                            if (pushingPast) {
                                accumulated = accumulated.coerceIn(-stepDegrees * 0.4f, stepDegrees * 0.4f)
                                if (!atEdge) {
                                    atEdge = true
                                    haptics?.click()
                                }
                            } else {
                                atEdge = false
                            }
                            // Nie über eine volle Runde hinaus (kein "12:01"), nie unter null.
                            dragFraction = ((index + accumulated / stepDegrees) / last).coerceIn(0f, 1f)
                            change.consume()
                        }
                    } finally {
                        val target = index / last.toFloat()
                        val from = dragFraction
                        dragging = false
                        scope.launch {
                            shown.snapTo(from)
                            shown.animateTo(target, spring(dampingRatio = 0.55f, stiffness = 700f))
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = 14.dp.toPx()
            val inset = 26.dp.toPx()
            val r = size.minDimension / 2f - inset
            val arcSize = Size(r * 2, r * 2)
            val tl = Offset(center.x - r, center.y - r)

            if (cartoon) {
                // Cartoon: flaches Zifferblatt mit Kontur und kleinem Sticker-Schatten
                val face = r - sw / 2f - 8.dp.toPx()
                drawCircle(p.ink, radius = face, center = center + Offset(1.dp.toPx(), 4.dp.toPx()))
                drawCircle(p.surface, radius = face, center = center)
                drawCircle(p.ink, radius = face, center = center, style = Stroke(2.5.dp.toPx()))
            } else if (!flat) {
                // inneres Leuchten
                drawCircle(
                    Brush.radialGradient(
                        listOf(p.glowA.copy(alpha = 0.10f + 0.06f * breath + 0.10f * press), Color.Transparent),
                        center = center,
                        radius = r * 1.1f,
                    ),
                    radius = r * 1.1f,
                )
            }

            // Rastpunkte außen herum: ein Strich pro wählbarem Wert, lange Striche an den großen Werten.
            // Der Strich am Griff wächst beim Drehen ein wenig mit.
            val steps = (values.size - 1).coerceAtLeast(1)
            val tickOuter = r + sw / 2f + 13.dp.toPx()
            for (i in 0 until steps) {
                val t = i / steps.toFloat()
                val ang = Math.toRadians((-90.0 + 360.0 * t))
                val major = i == 0 || values[i] in MajorValues
                val near = (1f - kotlin.math.abs(t - fraction) * steps).coerceIn(0f, 1f) * press
                val len = (if (major) 8.dp.toPx() else 4.dp.toPx()) + 3.dp.toPx() * near
                val lit = t <= fraction + 0.001f
                val start = Offset(
                    center.x + ((tickOuter - len) * cos(ang)).toFloat(),
                    center.y + ((tickOuter - len) * sin(ang)).toFloat(),
                )
                val end = Offset(
                    center.x + (tickOuter * cos(ang)).toFloat(),
                    center.y + (tickOuter * sin(ang)).toFloat(),
                )
                drawLine(
                    color = when {
                        cartoon && lit -> p.ink.copy(alpha = if (major) 0.9f else 0.6f)
                        cartoon -> p.ink.copy(alpha = if (major) 0.22f else 0.12f)
                        lit -> p.accent.copy(alpha = if (major) 0.95f else 0.6f)
                        else -> p.glassAlpha(if (major) 0.22f else 0.12f)
                    },
                    start = start,
                    end = end,
                    strokeWidth = (if (major) 2.5.dp else 2.dp).toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Spur
            drawArc(
                if (cartoon) p.ink.copy(alpha = 0.10f) else p.glassAlpha(0.07f),
                0f, 360f, false, topLeft = tl, size = arcSize, style = Stroke(sw),
            )
            if (cartoon) {
                drawCircle(p.ink, radius = r + sw / 2f, center = center, style = Stroke(2.dp.toPx()))
                drawCircle(p.ink, radius = r - sw / 2f, center = center, style = Stroke(2.dp.toPx()))
            }

            // leuchtender Bogen
            val sweep = 360f * fraction.coerceIn(0.004f, 1f)
            rotate(-90f) {
                drawArc(
                    brush = if (cartoon || flat) SolidColor(p.accent)
                    else Brush.sweepGradient(listOf(p.accentAlt, p.accent, p.accentAlt), center = center),
                    startAngle = 0f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = tl,
                    size = arcSize,
                    style = Stroke(sw, cap = StrokeCap.Round),
                )
            }

            // Griff am Ende des Bogens
            val ang = Math.toRadians(-90.0 + sweep)
            val knob = Offset(center.x + (r * cos(ang)).toFloat(), center.y + (r * sin(ang)).toFloat())
            val knobR = sw * (0.95f + 0.25f * press)
            if (!cartoon && !flat) {
                drawCircle(
                    Brush.radialGradient(listOf(p.accent.copy(alpha = 0.55f), Color.Transparent), center = knob, radius = knobR * 2.6f),
                    radius = knobR * 2.6f,
                    center = knob,
                )
            }
            if (!cartoon && !flat) {
                drawCircle(
                    Brush.radialGradient(listOf(Color.Black.copy(alpha = if (p.isDark) 0.35f else 0.18f), Color.Transparent), center = knob + Offset(0f, 2.dp.toPx()), radius = knobR * 1.5f),
                    radius = knobR * 1.5f,
                    center = knob + Offset(0f, 2.dp.toPx()),
                )
            }
            drawCircle(Color.White, radius = knobR, center = knob)
            if (cartoon) {
                drawCircle(p.ink, radius = knobR, center = knob, style = Stroke(2.5.dp.toPx()))
                drawCircle(p.ink, radius = knobR * 0.3f, center = knob)
            } else {
                drawCircle(p.accent, radius = knobR * 0.38f, center = knob)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    val up = targetState > initialState
                    (slideInVertically(spring(dampingRatio = 0.8f, stiffness = 1500f)) { if (up) it / 3 else -it / 3 } +
                        fadeIn(tween(90))) togetherWith
                        (slideOutVertically(tween(110)) { if (up) -it / 3 else it / 3 } + fadeOut(tween(70)))
                },
                label = "dialNumber",
            ) { value ->
                Text(value.toString(), style = DialNumberStyle, color = p.text)
            }
            Text(
                if (selected == 1) tr("Minute", "minute") else tr("Minuten", "minutes"),
                style = MaterialTheme.typography.titleSmall,
                color = p.textSecondary,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.size(de.funke.app.ui.theme.Space.s2))
            // Wann die Session vorbei wäre
            androidx.compose.foundation.layout.Row(
                Modifier
                    .clip(de.funke.app.ui.theme.Shapes.pill)
                    .background(p.track)
                    .padding(horizontal = de.funke.app.ui.theme.Space.s3, vertical = de.funke.app.ui.theme.Space.s1),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material3.Icon(
                    androidx.compose.ui.res.painterResource(de.funke.app.R.drawable.ic_timer),
                    contentDescription = null,
                    tint = p.textTertiary,
                    modifier = Modifier.size(12.dp),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(de.funke.app.ui.theme.Space.s1))
                Text(
                    tr("bis ", "until ") + rememberEndTime(selected),
                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03", letterSpacing = 0.sp),
                    color = p.textTertiary,
                )
            }
        }
    }
}

private fun angleOf(v: Offset): Float = Math.toDegrees(atan2(v.y.toDouble(), v.x.toDouble())).toFloat()
