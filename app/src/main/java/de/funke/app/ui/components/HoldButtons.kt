package de.funke.app.ui.components

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Motion
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.animateFloatAsState
import de.funke.app.ui.theme.Space
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.CancelStyle
import de.funke.app.ui.StartButtonStyle
import de.funke.app.ui.tr
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.withFrameNanos
import de.funke.app.ui.LocalHaptics
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.text.style.TextOverflow

/**
 * Drückt man und hält, füllt sich [progress] von 0 auf 1. Lässt man vorher los, federt er zurück.
 * Ist er voll, wird [onConfirm] einmal ausgelöst. Haptik: ein Klick beim Aufsetzen,
 * immer dichtere Ticks beim Laden und ein kräftiger Stoß am Ende.
 */
fun Modifier.holdToConfirm(
    progress: Animatable<Float, AnimationVector1D>,
    durationMillis: Int,
    stopping: Boolean = false,
    onDown: ((Offset) -> Unit)? = null,
    onUp: (() -> Unit)? = null,
    onConfirm: () -> Unit,
): Modifier = composed {
    val haptics = LocalHaptics.current
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnDown by rememberUpdatedState(onDown)
    val currentOnUp by rememberUpdatedState(onUp)
    this.pointerInput(durationMillis) {
        detectTapGestures(
            onPress = { at ->
                currentOnDown?.invoke(at)
                coroutineScope {
                    var done = false
                    // Läuft nach echter Zeit (Bildtakt), nicht nach Animationsdauer.
                    // So ist der Knopf auch dann nicht sofort voll, wenn im System Animationen aus sind.
                    val charge = launch {
                        haptics?.grab()
                        val startValue = progress.value
                        val startNanos = withFrameNanos { it }
                        val total = durationMillis * 1_000_000f * (1f - startValue).coerceAtLeast(0.05f)
                        var nextTick = 0.16f
                        while (true) {
                            val now = withFrameNanos { it }
                            val t = ((now - startNanos) / total).coerceIn(0f, 1f)
                            val value = startValue + (1f - startValue) * t
                            progress.snapTo(value)
                            // Ticks werden zum Ende hin dichter und kräftiger: "gleich ist es so weit"
                            if (value >= nextTick && value < 1f) {
                                haptics?.charge(value)
                                nextTick += 0.18f * (1f - value * 0.6f)
                            }
                            if (t >= 1f) break
                        }
                        done = true
                        if (stopping) haptics?.stop() else haptics?.ignite()
                        currentOnConfirm()
                    }
                    tryAwaitRelease()
                    currentOnUp?.invoke()
                    if (!done) {
                        charge.cancel()
                        if (progress.value > 0.08f) haptics?.release()
                        progress.animateTo(0f, spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessMediumLow))
                    }
                }
            }
        )
    }
}

/** Kleiner runder Fortschrittsring, z. B. um ein Symbol in einem Halte-Knopf. */
@Composable
private fun MiniRing(
    progress: Float,
    track: Color,
    fill: Color,
    modifier: Modifier = Modifier,
    stroke: Dp = 2.5.dp,
) {
    Canvas(modifier) {
        val sw = stroke.toPx()
        val arc = Size(size.width - sw, size.height - sw)
        val tl = Offset(sw / 2f, sw / 2f)
        drawArc(track, 0f, 360f, false, topLeft = tl, size = arc, style = Stroke(sw))
        if (progress > 0.002f) {
            drawArc(fill, -90f, 360f * progress, false, topLeft = tl, size = arc, style = Stroke(sw, cap = StrokeCap.Round))
        }
    }
}

/**
 * Der Start-Knopf in der gewählten Form ([StartButtonStyle]).
 * [progress] (0..1) gehört dem Aufrufer, damit z. B. Lumi mitfiebern kann, während man hält.
 */
@Composable
fun StartButton(
    style: StartButtonStyle,
    onStart: () -> Unit,
    progress: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    title: String? = null,
) {
    when (style) {
        StartButtonStyle.Pill -> HoldToStartButton(
            onStart = onStart,
            progress = progress,
            title = title ?: tr("Halten zum Starten", "Hold to start"),
            subtitle = if (title != null) tr("Halten zum Starten", "Hold to start") else subtitle,
            modifier = modifier,
        )
        StartButtonStyle.Round -> RoundStartButton(onStart = onStart, progress = progress, subtitle = subtitle, modifier = modifier)
        StartButtonStyle.Slide -> SlideToConfirm(
            progress = progress,
            onConfirm = onStart,
            label = title ?: tr("Zum Starten schieben", "Slide to start"),
            hint = if (title != null) tr("Zum Starten schieben", "Slide to start") else null,
            tone = SlideTone.Accent,
            modifier = modifier,
        )
    }
}

/**
 * Der Start-Knopf (seit 11.0 neu): eine ruhige, satte Pille ohne Schimmer.
 *
 * Gedrückt halten fühlt sich an wie ein echter Knopf: Er sinkt sofort spürbar ein (Feder, kein Nachwippen),
 * der Schatten wird kürzer, und vom Finger aus breitet sich eine hellere Füllung aus wie Tinte im Wasser.
 * Die Füllung wächst erst langsam und dann zügig (Ease-in-out), die Vibration wird dichter.
 * Ist sie voll, federt der Knopf kurz nach oben, ein einzelner Lichtring läuft nach außen, und es geht los.
 * Lässt man vorher los, zieht sich die Füllung zum Finger zurück und der Knopf hebt sich wieder.
 */
@Composable
fun HoldToStartButton(
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = tr("Halten zum Starten", "Hold to start"),
    subtitle: String? = null,
    holdMillis: Int = 900,
    progress: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) },
) {
    val p = Funke.colors
    val shape = Shapes.pill
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val still = de.funke.app.ui.theme.reducedMotion
    val scope = rememberCoroutineScope()
    // Wo der Finger liegt (Ursprung der Füllung) und wie tief der Knopf gedrückt ist
    var origin by remember { mutableStateOf(Offset.Unspecified) }
    val press = remember { Animatable(0f) }
    val pop = remember { Animatable(0f) }
    val ring = remember { Animatable(0f) }
    val holding by remember { derivedStateOf { progress.value > 0.02f } }
    val nearlyDone by remember { derivedStateOf { progress.value > 0.82f } }
    val fill = { v: Float -> if (v < 0.5f) 2f * v * v else 1f - (-2f * v + 2f).let { it * it } / 2f }

    Box(
        modifier
            .fillMaxWidth()
            .height(Space.s16)
            .graphicsLayer {
                // einsinken beim Drücken, kurzes Hochfedern am Ende
                val s = 1f - 0.03f * press.value + 0.025f * pop.value
                scaleX = s
                scaleY = s
            }
            .drawBehind {
                // Lichtring am Ende: einmal nach außen, dann weg
                val r = ring.value
                if (r > 0.001f && r < 0.999f) {
                    val grow = 18.dp.toPx() * r
                    drawRoundRect(
                        p.accent.copy(alpha = 0.45f * (1f - r)),
                        topLeft = Offset(-grow, -grow),
                        size = Size(size.width + 2 * grow, size.height + 2 * grow),
                        cornerRadius = CornerRadius(size.height / 2f + grow),
                        style = Stroke(2.dp.toPx() * (1f - r) + 0.5f),
                    )
                }
            }
            .then(
                when {
                    cartoon -> Modifier
                        .hardShadow(shape, p.ink, 5.dp)
                        .graphicsLayer {
                            translationX = 1.5.dp.toPx() * press.value
                            translationY = 4.dp.toPx() * press.value
                        }
                        .clip(shape)
                        .background(p.accent)
                        .border(2.5.dp, p.ink, shape)
                    flat -> Modifier.clip(shape).background(p.accent)
                    else -> Modifier
                        .softShadow(shape, Color.Black, alpha = if (p.isDark) 0.30f else 0.08f, blur = 2.dp, offsetY = 1.dp)
                        .softShadow(shape, p.accent, alpha = (if (p.isDark) 0.22f else 0.18f) * (1f - 0.7f * press.value), blur = 18.dp, offsetY = (8f - 5f * press.value).dp)
                        .clip(shape)
                        .background(p.accentBrush)
                        // feine Lichtkante oben (wie bei Glas), beim Drücken weicher
                        .background(Brush.verticalGradient(0f to Color.White.copy(alpha = 0.16f * (1f - 0.6f * press.value)), 0.5f to Color.Transparent))
                }
            )
            .drawWithContent {
                // Füllung vom Finger aus
                val v = progress.value
                if (v > 0.001f) {
                    val o = if (origin.isSpecified) origin else Offset(size.height / 2f, size.height / 2f)
                    val far = maxOf(
                        (o - Offset(0f, 0f)).getDistance(), (o - Offset(size.width, 0f)).getDistance(),
                        (o - Offset(0f, size.height)).getDistance(), (o - Offset(size.width, size.height)).getDistance(),
                    )
                    val rad = far * fill(v).coerceAtLeast(0.04f) + 6.dp.toPx() * v
                    val tone = if (p.isDark) Color.White else Color.White
                    drawCircle(
                        Brush.radialGradient(
                            0f to tone.copy(alpha = 0.20f),
                            0.85f to tone.copy(alpha = 0.16f),
                            1f to tone.copy(alpha = 0.0f),
                            center = o,
                            radius = rad,
                        ),
                        radius = rad,
                        center = o,
                    )
                }
                // eingedrückt: oben ein Hauch Schatten nach innen
                if (press.value > 0.01f && !flat) {
                    drawRect(Brush.verticalGradient(0f to Color.Black.copy(alpha = 0.14f * press.value), 0.35f to Color.Transparent))
                }
                drawContent()
            }
            .holdToConfirm(
                progress,
                holdMillis,
                onDown = { at ->
                    origin = at
                    scope.launch { press.animateTo(1f, spring(dampingRatio = 1f, stiffness = 1400f)) }
                },
                onUp = { scope.launch { press.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 520f)) } },
                onConfirm = {
                    scope.launch {
                        launch { press.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 520f)) }
                        if (!still) {
                            launch { ring.snapTo(0f); ring.animateTo(1f, tween(520, easing = Motion.EaseOut)) }
                            launch { pop.snapTo(0f); pop.animateTo(1f, tween(110, easing = Motion.EaseOut)); pop.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 420f)) }
                        }
                    }
                    onStart()
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = Space.s2, end = Space.s6),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(p.onAccent.copy(alpha = 0.12f + 0.06f * press.value))
                )
                MiniRing(
                    progress = fill(progress.value),
                    track = p.onAccent.copy(alpha = 0.10f),
                    fill = p.onAccent,
                    modifier = Modifier.size(48.dp),
                    stroke = 3.dp,
                )
                PlayIcon(p.onAccent, Modifier.size(20.dp).offset(x = 1.dp).graphicsLayer { val k = 1f - 0.08f * press.value; scaleX = k; scaleY = k })
            }
            Spacer(Modifier.width(Space.s4))
            Column(Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = when {
                        nearlyDone -> 2
                        holding -> 1
                        else -> 0
                    },
                    transitionSpec = {
                        (slideInVertically(tween(Motion.Base, easing = Motion.EaseOut)) { it / 3 } + fadeIn(tween(Motion.Fast, easing = Motion.EaseOut))) togetherWith
                            (slideOutVertically(tween(Motion.Fast, easing = Motion.EaseOut)) { -it / 3 } + fadeOut(tween(Motion.Fast - 40)))
                    },
                    label = "startLabel",
                ) { stage ->
                    Text(
                        when (stage) {
                            2 -> tr("Los geht's", "Here we go")
                            1 -> tr("Weiter halten …", "Keep holding …")
                            else -> title
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = p.onAccent,
                        maxLines = 1,
                    )
                }
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = p.onAccent.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * Runder Start-Knopf: ein Kreis mit Play-Symbol. Beim Halten läuft ein Ring außen herum voll,
 * der Kreis wird ein wenig kleiner. Darunter steht leise, was passiert.
 */
@Composable
fun RoundStartButton(
    onStart: () -> Unit,
    progress: Animatable<Float, AnimationVector1D>,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    holdMillis: Int = 850,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val holding by remember { derivedStateOf { progress.value > 0.02f } }
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(96.dp)
                .holdToConfirm(progress, holdMillis, onConfirm = onStart),
            contentAlignment = Alignment.Center,
        ) {
            // Ring außen
            Canvas(Modifier.fillMaxSize()) {
                val sw = 4.dp.toPx()
                val arc = Size(size.width - sw, size.height - sw)
                val tl = Offset(sw / 2f, sw / 2f)
                drawArc(
                    if (cartoon) p.ink.copy(alpha = 0.15f) else p.track,
                    0f, 360f, false, topLeft = tl, size = arc, style = Stroke(sw),
                )
                if (progress.value > 0.002f) {
                    drawArc(p.accent, -90f, 360f * progress.value, false, topLeft = tl, size = arc, style = Stroke(sw, cap = StrokeCap.Round))
                }
            }
            val inner = 74.dp
            Box(
                Modifier
                    .size(inner)
                    .graphicsLayer {
                        val s = 1f - 0.07f * progress.value
                        scaleX = s
                        scaleY = s
                    }
                    .then(
                        when {
                            cartoon -> Modifier
                                .hardShadow(CircleShape, p.ink, 4.dp)
                                .clip(CircleShape)
                                .background(p.accent)
                                .border(2.5.dp, p.ink, CircleShape)
                            flat -> Modifier.clip(CircleShape).background(p.accent)
                            else -> Modifier
                                .softShadow(CircleShape, Color.Black, alpha = if (p.isDark) 0.30f else 0.08f, blur = 2.dp, offsetY = 1.dp)
                                .softShadow(CircleShape, Color.Black, alpha = (if (p.isDark) 0.28f else 0.10f) * (1f - 0.6f * progress.value), blur = 16.dp, offsetY = (6f - 3f * progress.value).dp)
                                .clip(CircleShape)
                                .background(p.accentBrush)
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                PlayIcon(p.onAccent, Modifier.size(26.dp).offset(x = 2.dp))
            }
        }
        Spacer(Modifier.height(Space.s3))
        Text(
            if (holding) tr("Weiter halten …", "Keep holding …") else tr("Halten zum Starten", "Hold to start"),
            style = MaterialTheme.typography.labelLarge,
            color = p.text,
        )
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 1)
        }
    }
}

/** Farbe eines Schiebereglers: rot zum Abbrechen, Akzent zum Starten. */
enum class SlideTone { Danger, Accent }

/**
 * Abbrechen in der gewählten Form ([CancelStyle]).
 * [progress] (0..1) gehört dem Aufrufer. So kann Lumi darauf reagieren, wie weit gezogen oder gehalten wird.
 */
@Composable
fun CancelControl(
    style: CancelStyle,
    progress: Animatable<Float, AnimationVector1D>,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    when (style) {
        CancelStyle.Slider -> SlideToConfirm(
            progress = progress,
            onConfirm = onConfirm,
            label = tr("Zum Abbrechen schieben", "Slide to stop"),
            tone = SlideTone.Danger,
            enabled = enabled,
            modifier = modifier,
        )
        CancelStyle.Line -> LineSlideToConfirm(
            progress = progress,
            onConfirm = onConfirm,
            label = tr("Zum Abbrechen schieben", "Slide to stop"),
            enabled = enabled,
            modifier = modifier,
        )
        CancelStyle.Hold -> HoldToCancelButton(
            progress = progress,
            onConfirm = onConfirm,
            enabled = enabled,
            modifier = modifier,
        )
    }
}

/** Alter Name, damit bestehender Code weiter funktioniert. */
@Composable
fun SlideToCancel(
    progress: Animatable<Float, AnimationVector1D>,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = tr("Zum Abbrechen schieben", "Slide to stop"),
) = SlideToConfirm(progress, onConfirm, label, SlideTone.Danger, modifier, enabled)

/** Was ein Schieberegler gerade tut – für Aussehen und Haptik. */
@androidx.compose.runtime.Stable
private class SlideFeel {
    var grabbed by mutableStateOf(false)
    var confirmed by mutableStateOf(false)
    /** Wie schnell gezogen wird (0..1, klingt von selbst ab). Der Knopf streckt sich dabei ein wenig. */
    var speed by mutableFloatStateOf(0f)
}

/**
 * Schieberegler: den Knopf in die Mulde rechts ziehen.
 *
 * Die Spur ist eine leicht vertiefte Rinne, der Knopf eine 3D-Fläche mit Kante, die beim Greifen
 * etwas einsinkt und sich beim schnellen Ziehen streckt. Hinter ihm füllt sich die Rinne in der
 * Tonfarbe, rechts wartet die Mulde mit dem Ziel-Symbol und leuchtet auf, je näher er kommt.
 * Kurz vor dem Ende zieht ein Magnet ihn hinein. Beim Start zeichnet sich ein Haken, ein Ring läuft
 * nach außen. Haptik: Greifen, Rasten (zum Ende hin kräftiger), ein satter Abschluss.
 * Wartet der Regler, bewegt sich nichts.
 */
@Composable
fun SlideToConfirm(
    progress: Animatable<Float, AnimationVector1D>,
    onConfirm: () -> Unit,
    label: String,
    tone: SlideTone,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hint: String? = null,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val still = de.funke.app.ui.theme.reducedMotion
    val color = if (tone == SlideTone.Danger) p.danger else p.accent
    val shape = Shapes.pill
    val knobSize = 56.dp
    val pad = 4.dp
    val lipH = 3.dp
    val feel = remember { SlideFeel() }
    val grab by animateFloatAsState(if (feel.grabbed) 1f else 0f, tween(if (feel.grabbed) 60 else Motion.Fast, easing = Motion.EaseOut), label = "slideGrab")
    val burst = remember { Animatable(0f) }
    val check = remember { Animatable(0f) }
    LaunchedEffect(feel.confirmed) {
        if (feel.confirmed) {
            launch { check.snapTo(0f); check.animateTo(1f, tween(Motion.Medium, delayMillis = 60, easing = Motion.EaseOut)) }
            if (!still) { burst.snapTo(0f); burst.animateTo(1f, tween(420, easing = Motion.EaseOut)) }
        } else {
            check.snapTo(0f)
            burst.snapTo(0f)
        }
    }
    // Seit 11.0 kein Stupsen mehr: Ein Knopf, der von selbst zuckt, zieht den Blick genau dorthin,
    // wo er beim Lesen nicht hin soll (Abbrechen). Der Regler wartet still.

    val face = { pv: Float ->
        if (tone == SlideTone.Accent) color
        else lerp(if (flat) p.glassAlpha(0.14f).compositeOverBg(p.surface) else p.raised(de.funke.app.ui.theme.Elevation.Level3), color, pv)
    }
    val edge = { pv: Float ->
        if (cartoon) p.ink else lipOf(face(pv), if (p.isDark) 0.40f else if (tone == SlideTone.Accent) 0.26f else 0.12f)
    }

    SlideTrack(
        progress = progress,
        onConfirm = onConfirm,
        enabled = enabled,
        knobSize = knobSize,
        pad = pad,
        resetAfterConfirm = true,
        tone = tone,
        feel = feel,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .then(
                when {
                    cartoon -> Modifier.surface(shape, de.funke.app.ui.theme.Elevation.Level1)
                    flat -> Modifier.clip(shape).background(p.track)
                    else -> Modifier
                        .clip(shape)
                        .background(p.glassAlpha(if (p.isDark) 0.06f else 0.05f))
                        // vertiefte Rinne: oben ein Hauch Schatten, unten eine feine Lichtkante
                        .drawBehind {
                            drawRect(
                                Brush.verticalGradient(
                                    0f to Color.Black.copy(alpha = if (p.isDark) 0.28f else 0.07f),
                                    0.45f to Color.Transparent,
                                ),
                            )
                        }
                        .border(1.dp, p.glassAlpha(if (p.isDark) 0.07f else 0.06f), shape)
                }
            )
            // Spur hinter dem Knopf und die Mulde rechts
            .drawBehind {
                val pv = progress.value.coerceIn(0f, 1f)
                val inset = pad.toPx()
                val knob = knobSize.toPx()
                val travelPx = size.width - knob - inset * 2f
                val h = size.height - inset * 2f
                val slotC = Offset(size.width - inset - knob / 2f, size.height / 2f)
                val near = ((pv - 0.45f) / 0.55f).coerceIn(0f, 1f)
                drawCircle(color.copy(alpha = 0.10f * near), radius = knob / 2f - 2.dp.toPx(), center = slotC)
                drawCircle(
                    lerp(p.glassAlpha(if (p.isDark) 0.16f else 0.14f), color.copy(alpha = 0.7f), near),
                    radius = knob / 2f - 3.dp.toPx(),
                    center = slotC,
                    style = Stroke(1.5.dp.toPx()),
                )
                if (pv > 0.002f) {
                    val w = knob + pv * travelPx
                    drawRoundRect(
                        Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.05f + 0.06f * pv), color.copy(alpha = 0.16f + 0.22f * pv)),
                            startX = inset,
                            endX = inset + w,
                        ),
                        Offset(inset, inset),
                        Size(w, h),
                        CornerRadius(h / 2f, h / 2f),
                    )
                }
            },
    ) { travel ->
        val pv = progress.value
        // Ziel-Symbol in der Mulde
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = pad).size(knobSize),
            contentAlignment = Alignment.Center,
        ) {
            val near = ((pv - 0.45f) / 0.55f).coerceIn(0f, 1f)
            val tint = lerp(p.textTertiary, color, near)
            if (tone == SlideTone.Danger) CloseIcon(tint, Modifier.size(12.dp)) else PlayIcon(tint, Modifier.size(14.dp).offset(x = 1.dp))
        }

        // Beschriftung zwischen Knopf und Mulde; wandert beim Ziehen langsam mit und blendet aus
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = knobSize + pad + Space.s2, end = knobSize + pad + Space.s2)
                .graphicsLayer {
                    alpha = (1f - pv * 2.2f).coerceIn(0f, 1f)
                    translationX = pv * 28.dp.toPx()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = if (tone == SlideTone.Accent) p.text else p.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                // Glanz nur beim Starten, nie beim Abbrechen
                modifier = Modifier.sheen(active = enabled && !flat && tone == SlideTone.Accent, color = if (p.isDark) Color.White.copy(alpha = 0.9f) else color.copy(alpha = 0.9f)),
            )
            if (hint != null) {
                Text(hint, style = MaterialTheme.typography.labelSmall, color = p.textTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        // Der Knopf
        Box(
            Modifier
                .offset { IntOffset((pv * travel).roundToInt(), 0) }
                .padding(start = pad)
                .size(knobSize)
                .graphicsLayer {
                    val st = if (still) 0f else 0.10f * feel.speed
                    scaleX = 1f + st
                    scaleY = 1f - st * 0.5f
                },
            contentAlignment = Alignment.Center,
        ) {
            val f = face(pv)
            val e = edge(pv)
            Canvas(Modifier.matchParentSize()) {
                val l = lipH.toPx()
                val r = (size.minDimension - l) / 2f
                val down = l * 0.7f * grab
                val top = Offset(size.width / 2f, r + down)
                // Ring, der beim Bestätigen nach außen läuft
                if (burst.value > 0.001f && burst.value < 0.999f) {
                    drawCircle(color.copy(alpha = 0.55f * (1f - burst.value)), radius = r * (1f + 0.9f * burst.value), center = top, style = Stroke(3.dp.toPx() * (1f - burst.value) + 0.5f))
                }
                if (!cartoon && !flat) {
                    // weicher Schatten unter dem Knopf (wird beim Greifen kürzer)
                    drawCircle(
                        Brush.radialGradient(
                            listOf(Color.Black.copy(alpha = if (p.isDark) 0.35f else 0.14f), Color.Transparent),
                            center = Offset(size.width / 2f, r + l + 3.dp.toPx() * (1f - grab)),
                            radius = r * 1.25f,
                        ),
                        radius = r * 1.25f,
                        center = Offset(size.width / 2f, r + l + 3.dp.toPx() * (1f - grab)),
                    )
                }
                // Kante, dann Fläche (sinkt beim Greifen ein)
                drawCircle(e, radius = r, center = Offset(size.width / 2f, r + l))
                drawCircle(f, radius = r, center = top)
                if (!flat) {
                    drawCircle(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = if (tone == SlideTone.Accent) 0.22f else 0.10f), Color.Transparent),
                            startY = top.y - r,
                            endY = top.y,
                        ),
                        radius = r,
                        center = top,
                    )
                }
                if (cartoon) drawCircle(p.ink, radius = r, center = top, style = Stroke(2.5.dp.toPx()))
            }
            Box(
                Modifier.matchParentSize().padding(bottom = lipH).graphicsLayer { translationY = lipH.toPx() * 0.7f * grab },
                contentAlignment = Alignment.Center,
            ) {
                if (tone == SlideTone.Accent) {
                    val k = check.value
                    if (k < 0.999f) PlayIcon(p.onAccent, Modifier.size(20.dp).offset(x = 1.dp).graphicsLayer { alpha = 1f - k; val sc = 1f - 0.4f * k; scaleX = sc; scaleY = sc })
                    if (k > 0.001f) DrawnCheck(p.onAccent, k, Modifier.size(22.dp), stroke = 0.14f)
                } else {
                    val k = ((pv - 0.65f) / 0.25f).coerceIn(0f, 1f)
                    ChevronRightIcon(lerp(p.text, Color.White, pv), Modifier.size(24.dp).graphicsLayer { alpha = 1f - k })
                    CloseIcon(Color.White, Modifier.size(18.dp).graphicsLayer { alpha = k; rotationZ = -90f * (1f - k) })
                }
            }
        }
    }
}

/**
 * Schlichte Variante: eine feine Linie mit kleinem Knopf. Hinter dem Knopf färbt sich die Linie rot.
 */
@Composable
fun LineSlideToConfirm(
    progress: Animatable<Float, AnimationVector1D>,
    onConfirm: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val knobSize = 40.dp
    val pad = 4.dp
    SlideTrack(
        progress = progress,
        onConfirm = onConfirm,
        enabled = enabled,
        knobSize = knobSize,
        pad = pad,
        resetAfterConfirm = true,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .drawBehind {
                val y = size.height * 0.62f
                val inset = pad.toPx() + knobSize.toPx() / 2f
                val end = size.width - inset
                val lw = (if (cartoon) 3.dp else 2.dp).toPx()
                drawLine(if (cartoon) p.ink.copy(alpha = 0.35f) else p.track.copy(alpha = p.track.alpha * 2f), Offset(inset, y), Offset(end, y), lw, StrokeCap.Round)
                val x = inset + (end - inset) * progress.value
                if (progress.value > 0.002f) drawLine(p.danger, Offset(inset, y), Offset(x, y), lw * 1.4f, StrokeCap.Round)
                // Zielpunkt
                drawCircle(p.danger.copy(alpha = 0.35f + 0.65f * progress.value), radius = 4.dp.toPx(), center = Offset(end, y))
            },
    ) { travel ->
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = p.textTertiary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = Space.s1)
                .graphicsLayer { alpha = (1f - progress.value * 2f).coerceIn(0f, 1f) },
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = Space.s16 * 0.38f - knobSize / 2)
                .offset { IntOffset((progress.value * travel).roundToInt(), 0) }
                .padding(start = pad)
                .size(knobSize)
                .then(if (cartoon) Modifier else Modifier.softShadow(CircleShape, Color.Black, alpha = if (p.isDark) 0.30f else 0.10f, blur = 10.dp, offsetY = 3.dp))
                .clip(CircleShape)
                .drawBehind { drawRect(lerp(p.raised(de.funke.app.ui.theme.Elevation.Level3), p.danger, progress.value)) }
                .then(if (cartoon) Modifier.border(2.5.dp, p.ink, CircleShape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            CloseIcon(lerp(p.danger, Color.White, progress.value), Modifier.size(12.dp))
        }
    }
}

/**
 * Gemeinsame Mechanik der Schieberegler: Ziehen bewegt [progress] (0..1). Kurz vor dem Ende zieht ein
 * Magnet den Knopf hinein, mit Schwung reicht auch weniger; sonst federt er zurück. Haptik: Greifen,
 * Rasten alle 10 % (zum Ende hin kräftiger), Loslassen, und zum Schluss je nach [tone] ein satter Start
 * oder ein dumpfer Stopp. [content] bekommt die Wegstrecke in Pixeln.
 */
@Composable
private fun SlideTrack(
    progress: Animatable<Float, AnimationVector1D>,
    onConfirm: () -> Unit,
    enabled: Boolean,
    knobSize: Dp,
    pad: Dp,
    resetAfterConfirm: Boolean,
    modifier: Modifier,
    tone: SlideTone = SlideTone.Danger,
    feel: SlideFeel = remember { SlideFeel() },
    content: @Composable androidx.compose.foundation.layout.BoxScope.(travel: Float) -> Unit,
) {
    val haptics = LocalHaptics.current
    val scope = rememberCoroutineScope()
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentEnabled by rememberUpdatedState(enabled)
    var pos by remember { mutableFloatStateOf(0f) }
    var lastStep by remember { mutableIntStateOf(0) }

    // Schwung klingt von selbst ab
    LaunchedEffect(feel.grabbed) {
        while (feel.grabbed) {
            withFrameNanos { }
            feel.speed *= 0.86f
        }
        feel.speed = 0f
    }

    BoxWithConstraints(modifier) {
        val travel = with(LocalDensity.current) { (maxWidth - knobSize - pad * 2).toPx() }.coerceAtLeast(1f)

        fun confirm() {
            if (feel.confirmed) return
            feel.confirmed = true
            feel.grabbed = false
            if (tone == SlideTone.Accent) haptics?.ignite() else haptics?.stop()
            scope.launch {
                progress.animateTo(1f, tween(140, easing = Motion.EaseOut))
                currentOnConfirm()
                if (resetAfterConfirm) {
                    try {
                        delay(450)
                        pos = 0f
                        lastStep = 0
                        progress.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow))
                    } finally {
                        // auch wenn jemand anderes die Animation unterbricht: wieder bereit
                        pos = 0f
                        lastStep = 0
                        feel.confirmed = false
                    }
                }
            }
        }

        val dragState = rememberDraggableState { delta ->
            if (!feel.confirmed && currentEnabled) {
                pos = (pos + delta / travel).coerceIn(0f, 1f)
                val v = pos
                feel.speed = (feel.speed * 0.6f + (abs(delta) / 26f).coerceAtMost(1f) * 0.4f).coerceIn(0f, 1f)
                scope.launch { progress.snapTo(v) }
                val step = (v * 10f).toInt()
                if (step > lastStep) haptics?.detent(v)
                lastStep = step
                // Magnet: kurz vor dem Ende schnappt der Knopf hinein
                if (v >= 0.92f) confirm()
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled,
                    onDragStarted = {
                        if (!feel.confirmed) {
                            progress.stop()
                            pos = progress.value
                            feel.grabbed = true
                            haptics?.grab()
                        }
                    },
                    onDragStopped = { velocity ->
                        if (!feel.confirmed) {
                            feel.grabbed = false
                            if (pos > 0.80f || (velocity > 2200f && pos > 0.4f)) {
                                confirm()
                            } else {
                                if (pos > 0.05f) haptics?.release()
                                pos = 0f
                                lastStep = 0
                                progress.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 420f))
                            }
                        }
                    },
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            content(travel)
        }
    }
}

/**
 * Abbrechen durch Gedrückthalten (ohne Schieben): eine ruhige Pille, die sich beim Halten von links
 * rot füllt. Nach 1,2 s ist es so weit.
 */
@Composable
fun HoldToCancelButton(
    progress: Animatable<Float, AnimationVector1D>,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    holdMillis: Int = 1200,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val shape = Shapes.pill
    val holding by remember { derivedStateOf { progress.value > 0.02f } }
    val scope = rememberCoroutineScope()
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    Box(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .surface(shape, de.funke.app.ui.theme.Elevation.Level1)
            .then(if (enabled) Modifier.holdToConfirm(progress, holdMillis, stopping = true) {
                currentOnConfirm()
                scope.launch {
                    delay(450)
                    progress.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow))
                }
            } else Modifier),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.value)
                .clip(shape)
                .background(p.danger.copy(alpha = (if (flat || cartoon) 0.14f else 0.06f) + 0.20f * progress.value))
        )
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Space.s2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                MiniRing(
                    progress = progress.value,
                    track = p.track,
                    fill = p.danger,
                    modifier = Modifier.size(42.dp),
                    stroke = 3.dp,
                )
                CloseIcon(lerp(p.textSecondary, p.danger, progress.value), Modifier.size(12.dp))
            }
            Spacer(Modifier.width(Space.s3))
            Text(
                if (holding) tr("Weiter halten …", "Keep holding …") else tr("Halten zum Abbrechen", "Hold to stop"),
                style = MaterialTheme.typography.labelLarge,
                color = p.textSecondary,
                maxLines = 1,
            )
        }
    }
}

/** Glasfarbe über dem Hintergrund, damit der Knopf deckend ist (sonst scheint die rote Spur durch). */
private fun Color.compositeOverBg(bg: Color): Color = Color(
    red = red * alpha + bg.red * (1f - alpha),
    green = green * alpha + bg.green * (1f - alpha),
    blue = blue * alpha + bg.blue * (1f - alpha),
    alpha = 1f,
)
