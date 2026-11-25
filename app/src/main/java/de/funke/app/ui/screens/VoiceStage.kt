package de.funke.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.R
import de.funke.app.ui.AppState
import de.funke.app.ui.components.LipBox
import de.funke.app.ui.components.LipHeight
import de.funke.app.ui.components.LipPill
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.VoiceState
import de.funke.app.ui.components.VoiceVisual
import de.funke.app.ui.components.VoiceVisualizer
import de.funke.app.ui.components.accentFill
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.lipColors
import de.funke.app.ui.components.rememberLoop
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Öffnen und Schließen der Sprach-Ansicht (ms). Die Zeit läuft gleichmäßig, jede Phase hat ihre Kurve. */
const val STAGE_OPEN_MS = 640
const val STAGE_CLOSE_MS = 420

/** Wo im Ablauf die fliegende Kugel an die Animation übergibt. */
private const val HANDOFF = 0.46f

/** Abschnitt [a]..[b] der Zeitleiste [t] als 0..1. */
private fun seg(t: Float, a: Float, b: Float) = ((t - a) / (b - a)).coerceIn(0f, 1f)

/** Flug: zügig los, weich ankommen (wie ein geworfener Ball, der gefangen wird). */
private val FlyEase = CubicBezierEasing(0.45f, 0f, 0.15f, 1f)
private val GrowEase = CubicBezierEasing(0.3f, 0f, 0.2f, 1f)
/** Knöpfe: kleines Nachfedern beim Ankommen. */
private val PopEase = CubicBezierEasing(0.34f, 1.35f, 0.64f, 1f)

/** Größe der Kugel, aus der die Animation entsteht (Radius). Runde Animationen starten größer. */
private fun seedFor(visual: VoiceVisual): Dp = when (visual) {
    VoiceVisual.Radial, VoiceVisual.Orb -> 56.dp
    VoiceVisual.Dots -> 40.dp
    else -> 44.dp
}

/**
 * Die große Sprach-Ansicht. [t] läuft beim Öffnen in [STAGE_OPEN_MS] von 0 bis 1, beim Schließen in
 * [STAGE_CLOSE_MS] zurück:
 *  0–46 %   das Mikrofon fliegt nach oben in die Mitte, streckt sich im Flug leicht, staucht sich beim
 *           Ankommen und wird zur glatten Kugel (Symbol, Glanz und Schein gehen weg),
 *  46–100 % aus der Kugel entsteht die gewählte Animation – jede auf ihre Art: Balken und Punkte teilen
 *           sich, der Kreis blüht auf, das Raster spritzt auseinander, die Kugel löst sich in Linien auf,
 *  ab 40 %  Status oben, ab 50 % der Text, ab 55 % die drei Knöpfe nacheinander.
 * Schließen läuft rückwärts: die Animation zieht sich zur Kugel zusammen, die ins Mikrofon zurückfliegt.
 * Ohne [origin] (Aufräumen, weniger Bewegung) blendet alles nur weich ein.
 */
@Composable
internal fun VoiceStage(
    state: AppState,
    t: Float,
    origin: Offset?,
    listening: Boolean,
    paused: Boolean,
    busy: VoiceState?,
    level: () -> Float,
    seconds: Int,
    said: String,
    partial: String,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDone: () -> Unit,
    onDiscard: () -> Unit,
    onCancelCleanup: () -> Unit,
) {
    val p = Funke.colors
    val density = LocalDensity.current
    val thinking = busy != null
    val visual = if (thinking) VoiceVisual.Orb else state.voiceVisual
    val round = visual == VoiceVisual.Orb || visual == VoiceVisual.Radial
    val seed = seedFor(visual)
    var stagePos by remember { mutableStateOf(Offset.Zero) }
    var visualCenter by remember { mutableStateOf<Offset?>(null) }

    val flying = origin != null
    val backdrop = Motion.EaseOut.transform(seg(t, 0.02f, 0.45f))
    val birth = if (flying) seg(t, HANDOFF, 1f) else 1f
    val fade = if (flying) (if (t >= HANDOFF) 1f else 0f) else Motion.EaseOut.transform(seg(t, 0.2f, 0.8f))
    val statusK = Motion.EaseOut.transform(seg(t, 0.40f, 0.75f))
    val textK = Motion.EaseOut.transform(seg(t, 0.50f, 0.85f))
    fun buttonK(i: Int) = PopEase.transform(seg(t, 0.55f + 0.07f * i, 0.82f + 0.07f * i))

    Box(Modifier.fillMaxSize().onGloballyPositioned { stagePos = it.positionInRoot() }) {
        // Hintergrund blendet ein, mit einem Hauch Akzentlicht in der Mitte
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = backdrop }
                .background(p.bg.copy(alpha = 0.97f))
                .background(Brush.radialGradient(listOf(p.accent.copy(alpha = if (p.isDark) 0.14f else 0.10f), Color.Transparent), radius = 900f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
        )
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Space.s6),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Space.s7))
            StatusPill(
                thinking = thinking,
                ai = state.aiReady,
                paused = paused,
                language = if (state.dictationTag.startsWith("de")) "Deutsch" else "English",
                seconds = seconds,
                modifier = Modifier.graphicsLayer { alpha = statusK; translationY = (1f - statusK) * -8.dp.toPx() },
            )

            Spacer(Modifier.weight(1f))
            // Mitte: die Animation (beim Aufräumen mit Lumi davor)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(264.dp)
                    .onGloballyPositioned {
                        val pos = it.positionInRoot()
                        visualCenter = Offset(pos.x + it.size.width / 2f, pos.y + it.size.height / 2f)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.graphicsLayer {
                        alpha = fade
                        if (!flying) {
                            val k = 0.9f + 0.1f * fade
                            scaleX = k
                            scaleY = k
                        }
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    VoiceVisualizer(
                        visual = visual,
                        state = when {
                            busy != null -> busy
                            paused -> VoiceState.Muted
                            else -> VoiceState.Listening
                        },
                        level = level,
                        color = p.accent,
                        modifier = if (round) Modifier.size(248.dp) else Modifier.fillMaxWidth(0.92f).height(152.dp),
                        birth = birth,
                        seed = seed,
                    )
                    if (thinking && state.sidekick) {
                        Lumi2D(
                            if (busy == VoiceState.Speaking) LumiPose.Happy else LumiPose.Reading,
                            Modifier.size(120.dp),
                            tappable = false,
                        )
                    }
                }
            }
            Spacer(Modifier.height(Space.s6))
            // Darunter: was verstanden wird – oder was Lumi gerade sagt
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .graphicsLayer { alpha = textK; translationY = (1f - textK) * 12.dp.toPx() },
                contentAlignment = Alignment.TopCenter,
            ) {
                if (thinking) LumiSpeech(busy) else Transcript(said, partial, paused)
            }
            Spacer(Modifier.weight(1f))

            // Unten: Knöpfe, einer nach dem anderen
            when {
                !thinking -> StageControls(
                    paused = paused,
                    listening = listening,
                    level = level,
                    rise = { i -> Modifier.graphicsLayer {
                        val k = buttonK(i)
                        alpha = k.coerceIn(0f, 1f)
                        translationY = (1f - k) * 40.dp.toPx()
                        val sc = 0.9f + 0.1f * k
                        scaleX = sc
                        scaleY = sc
                    } },
                    onDiscard = onDiscard,
                    onToggle = { if (paused) onResume() else onPause() },
                    onDone = onDone,
                )
                busy != VoiceState.Speaking -> LipPill(
                    tr("Abbrechen", "Cancel"),
                    onClick = onCancelCleanup,
                    modifier = Modifier.graphicsLayer { alpha = textK }.padding(bottom = Space.s8),
                )
                else -> Spacer(Modifier.height(Sizes.buttonSmall + Space.s8))
            }
        }

        // Die fliegende Kugel (bis zur Übergabe an die Animation)
        if (origin != null && t < HANDOFF) {
            FlyingMic(t, origin - stagePos, (visualCenter ?: origin) - stagePos, seed)
        }
    }
}

/**
 * Das Mikrofon auf dem Weg nach oben. Es wächst von 64 dp auf die Größe der Start-Kugel, streckt sich
 * im Flug ein wenig (schnell = länger) und staucht sich beim Ankommen. Das Symbol verblasst, Verlauf
 * und Schein gehen in eine glatte Akzent-Fläche über – genau so sieht die Kugel aus, aus der dann die
 * Animation entsteht. So gibt es beim Übergang keinen Sprung.
 */
@Composable
private fun FlyingMic(t: Float, start: Offset, end: Offset, seed: Dp) {
    val p = Funke.colors
    val density = LocalDensity.current
    val fly = FlyEase.transform(seg(t, 0f, HANDOFF))
    val grow = GrowEase.transform(seg(t, 0.04f, HANDOFF))
    val ctrl = Offset(start.x, end.y + (start.y - end.y) * 0.35f)
    val u = fly
    val pos = Offset(
        (1 - u) * (1 - u) * start.x + 2 * (1 - u) * u * ctrl.x + u * u * end.x,
        (1 - u) * (1 - u) * start.y + 2 * (1 - u) * u * ctrl.y + u * u * end.y,
    )
    // Startgröße = das Mikrofon unten in der Leiste (64 dp seit 15.4), damit es beim Abheben nicht springt
    val d0 = with(density) { 64.dp.toPx() }
    val d1 = with(density) { (seed * 2).toPx() }
    val d = d0 + (d1 - d0) * grow
    val stretch = 0.12f * sin(PI.toFloat() * fly)
    val squash = 0.07f * sin(PI.toFloat() * seg(t, 0.34f, HANDOFF))
    val plain = seg(t, 0.10f, 0.40f)
    val iconAlpha = 1f - seg(t, 0f, 0.2f)
    Box(
        Modifier
            .offset { IntOffset((pos.x - d / 2f).toInt(), (pos.y - d / 2f).toInt()) }
            .size(with(density) { d.toDp() })
            .graphicsLayer {
                scaleY = 1f + stretch - squash
                scaleX = 1f - stretch * 0.6f + squash
            }
            .accentFill(CircleShape, glow = 16.dp * (1f - plain))
            .drawBehind { drawCircle(p.accent.copy(alpha = plain)) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(R.drawable.ic_mic),
            contentDescription = null,
            tint = p.onAccent,
            modifier = Modifier.size(30.dp).graphicsLayer { alpha = iconAlpha },
        )
    }
}

/** Status oben: Aufnahme-Punkt, was gerade passiert, die Sprache und die Aufnahmezeit (Ziffern gleich breit). */
@Composable
private fun StatusPill(thinking: Boolean, ai: Boolean, paused: Boolean, language: String, seconds: Int, modifier: Modifier) {
    val p = Funke.colors
    Row(
        modifier
            .glass(Shapes.pill)
            .height(Sizes.buttonSmall - Space.s1)
            .padding(horizontal = Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            thinking -> Icon(painterResource(R.drawable.ic_sparkles), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconSm))
            paused -> Box(Modifier.size(8.dp).clip(CircleShape).background(p.textTertiary))
            else -> RecDot(p.danger)
        }
        Spacer(Modifier.width(Space.s2))
        Text(
            when {
                thinking && ai -> tr("Lumi räumt auf", "Lumi is tidying")
                thinking -> tr("Aufräumen", "Cleaning up")
                paused -> tr("Pausiert", "Paused")
                else -> tr("Ich höre zu", "Listening") + " (" + language + ")"
            },
            style = MaterialTheme.typography.labelLarge,
            color = p.textSecondary,
        )
        if (!thinking) {
            Spacer(Modifier.width(Space.s3))
            Text(
                "%d:%02d".format(seconds / 60, seconds % 60),
                style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"),
                color = p.textTertiary,
            )
        }
    }
}

/** Was verstanden wurde, das Vorläufige kursiv und blasser. */
@Composable
private fun Transcript(said: String, partial: String, paused: Boolean) {
    val p = Funke.colors
    val shown = said.takeLast(160).let { if (said.length > 160) "… " + it.substringAfter(' ') else it }
    val text = buildAnnotatedString {
        append(shown)
        if (partial.isNotBlank()) {
            if (shown.isNotBlank()) append(" ")
            withStyle(SpanStyle(color = p.textTertiary, fontStyle = FontStyle.Italic)) { append(partial) }
        }
    }
    Text(
        if (text.isEmpty()) buildAnnotatedString {
            withStyle(SpanStyle(color = p.textTertiary)) {
                append(
                    if (paused) tr("Pausiert", "Paused")
                    else tr("Sprich einfach los …", "Just start talking …"),
                )
            }
        } else text,
        style = MaterialTheme.typography.titleLarge.copy(lineHeight = 30.sp),
        color = p.text,
        textAlign = TextAlign.Center,
        maxLines = 4,
    )
}

/**
 * Die drei Knöpfe als 3D-Flächen: Verwerfen (Papierkorb, wackelt beim Antippen), in der Mitte groß
 * Pause/Weiter (Akzent, mit einem Ring, der mit der Stimme atmet) und Fertig (der Haken zeichnet sich
 * beim Antippen neu). Verwerfen und Fertig lösen nach einem kurzen Moment aus, damit man die Rückmeldung
 * noch sieht.
 */
@Composable
private fun StageControls(
    paused: Boolean,
    listening: Boolean,
    level: () -> Float,
    rise: (Int) -> Modifier,
    onDiscard: () -> Unit,
    onToggle: () -> Unit,
    onDone: () -> Unit,
) {
    val p = Funke.colors
    val scope = rememberCoroutineScope()
    val still = de.funke.app.ui.theme.reducedMotion
    val wiggle = remember { Animatable(0f) }
    val check = remember { Animatable(1f) }
    var fired by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(bottom = Space.s7),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Box(rise(0)) {
            StageButton(tr("Verwerfen", "Discard"), face = 60.dp, tone = LipTone.Neutral, onClick = discard@{
                if (fired) return@discard
                fired = true
                scope.launch {
                    if (!still) launch {
                        wiggle.animateTo(0f, keyframes { durationMillis = 280; -12f at 70; 9f at 150; -4f at 220 })
                    }
                    delay(if (still) 0 else 140)
                    onDiscard()
                }
            }) { _ ->
                Icon(
                    painterResource(R.drawable.ic_trash),
                    contentDescription = null,
                    tint = p.danger,
                    modifier = Modifier.size(Sizes.iconLg).graphicsLayer { rotationZ = wiggle.value; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.2f) },
                )
            }
        }
        Box(rise(1)) {
            Box(contentAlignment = Alignment.TopCenter) {
                // Ring, der mit der Stimme atmet (nur beim Zuhören)
                if (listening && !paused && !Funke.flat) VoiceRing(level, Modifier.size(76.dp + 24.dp).offset(y = (-12).dp))
                StageButton(if (paused) tr("Weiter", "Resume") else tr("Pause", "Pause"), face = 76.dp, tone = LipTone.Accent, onClick = onToggle) { c ->
                    AnimatedContent(
                        targetState = paused,
                        transitionSpec = {
                            (fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) + scaleIn(tween(Motion.Base, easing = Motion.EaseOut), initialScale = 0.6f)) togetherWith
                                (fadeOut(tween(Motion.Fast)) + scaleOut(tween(Motion.Fast), targetScale = 0.6f))
                        },
                        label = "pauseGlyph",
                    ) { isPaused ->
                        if (isPaused) Icon(painterResource(R.drawable.ic_mic), contentDescription = null, tint = c, modifier = Modifier.size(30.dp))
                        else PauseGlyph(c, Modifier.size(26.dp))
                    }
                }
            }
        }
        Box(rise(2)) {
            StageButton(tr("Fertig", "Done"), face = 60.dp, tone = LipTone.Neutral, onClick = done@{
                if (fired) return@done
                fired = true
                scope.launch {
                    if (!still) {
                        check.snapTo(0f)
                        launch { check.animateTo(1f, tween(Motion.Medium, easing = Motion.EaseOut)) }
                    }
                    delay(if (still) 0 else 160)
                    onDone()
                }
            }) { _ ->
                de.funke.app.ui.components.DrawnCheck(p.success, check.value, Modifier.size(Sizes.iconLg))
            }
        }
    }
}

/** Ein runder 3D-Knopf ([face] = Durchmesser der Fläche, die Kante kommt dazu) mit Beschriftung darunter. */
@Composable
private fun StageButton(
    label: String,
    face: Dp,
    tone: LipTone,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit,
) {
    val p = Funke.colors
    val c = p.lipColors(tone)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(96.dp)) {
        Box(Modifier.height(76.dp + LipHeight), contentAlignment = Alignment.Center) {
            LipBox(
                colors = c,
                modifier = Modifier.width(face).height(face + LipHeight).semantics { contentDescription = label },
                shape = CircleShape,
                onClick = onClick,
            ) { icon(c.content) }
        }
        Spacer(Modifier.height(Space.s2))
        Text(label, style = MaterialTheme.typography.labelLarge, color = p.textSecondary)
    }
}

/** Weicher Ring um den Pause-Knopf, der mit der Lautstärke wächst. */
@Composable
private fun VoiceRing(level: () -> Float, modifier: Modifier) {
    val p = Funke.colors
    var smooth by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            androidx.compose.runtime.withFrameNanos { }
            smooth += (level().coerceIn(0f, 1f) - smooth) * 0.25f
        }
    }
    Canvas(modifier) {
        val base = size.minDimension / 2f - 12.dp.toPx()
        val r = base + 10.dp.toPx() * smooth
        drawCircle(p.accent.copy(alpha = 0.10f + 0.16f * smooth), radius = r)
    }
}

/** Was Lumi beim Aufräumen sagt – in einer Sprechblase, alle paar Sekunden etwas anderes. */
@Composable
private fun LumiSpeech(busy: VoiceState?) {
    val p = Funke.colors
    val lines = if (busy == VoiceState.Speaking) listOf(tr("Fertig! Schau mal.", "Done! Take a look."))
    else listOf(
        tr("Okay, ich räume das kurz auf!", "Okay, let me tidy this up!"),
        tr("Ich korrigiere die Rechtschreibung …", "Fixing the spelling …"),
        tr("Versprecher und Doppeltes fliegen raus …", "Removing slips and repeats …"),
        tr("Formeln werden zu Zeichen …", "Turning formulas into symbols …"),
        tr("Fast fertig …", "Almost done …"),
    )
    var i by remember(busy == VoiceState.Speaking) { mutableStateOf(0) }
    LaunchedEffect(busy == VoiceState.Speaking) {
        while (lines.size > 1) {
            delay(2200)
            i = (i + 1).coerceAtMost(lines.size - 1)
        }
    }
    AnimatedContent(
        targetState = lines[i.coerceIn(0, lines.size - 1)],
        transitionSpec = { (fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) + slideInVertically(tween(Motion.Slow, easing = Motion.EaseOut)) { it / 3 }) togetherWith fadeOut(tween(Motion.Fast)) },
        label = "lumiSpeech",
    ) { line ->
        Text(
            line,
            style = MaterialTheme.typography.titleMedium,
            color = p.text,
            textAlign = TextAlign.Center,
            modifier = Modifier.glass(Shapes.card).padding(horizontal = Space.s5, vertical = Space.s3),
        )
    }
}

/** Pause-Zeichen (zwei Balken). */
@Composable
private fun PauseGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.minDimension
        val bw = w * 0.26f
        drawRoundRect(color, Offset(w * 0.18f, w * 0.12f), Size(bw, w * 0.76f), CornerRadius(bw * 0.35f))
        drawRoundRect(color, Offset(w * 0.56f, w * 0.12f), Size(bw, w * 0.76f), CornerRadius(bw * 0.35f))
    }
}

/** Pulsierender Aufnahme-Punkt. */
@Composable
private fun RecDot(color: Color) {
    val a by rememberLoop(1f, 0.35f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "recA")
    Box(Modifier.size(8.dp).graphicsLayer { alpha = a }.clip(CircleShape).background(color))
}
