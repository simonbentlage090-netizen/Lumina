package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import android.content.Context
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.clickable
import de.funke.app.ui.components.ChevronRightIcon
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.FocusMode
import de.funke.app.ui.InstalledApp
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.LocalTimerStyle
import de.funke.app.ui.ResultKind
import de.funke.app.ui.TimerStyleOption
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.AppIcon
import de.funke.app.ui.components.CloseIcon
import de.funke.app.ui.components.ConfirmDialog
import de.funke.app.ui.components.CancelControl
import de.funke.app.ui.CancelStyle
import de.funke.app.ui.tr
import de.funke.app.ui.components.ConfettiBurst
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.core.AnimationVector1D
import de.funke.app.ui.components.TimerRing
import de.funke.app.ui.LocalRingStyle
import de.funke.app.ui.LocalSidekick
import androidx.compose.foundation.layout.wrapContentWidth
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.VaporDigits
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.TimerStyle
import kotlinx.coroutines.delay
import de.funke.app.ui.LocalHaptics
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import de.funke.app.ui.components.CheckIcon
import de.funke.app.ui.components.reveal
import kotlinx.coroutines.launch

/**
 * Die laufende Fokus-Session.
 *
 * Dieser Teil hält den Zustand (Countdown, Abbruch-Dialoge). Die eigentliche Oberfläche
 * steckt in [FocusContent], die keinen eigenen Zustand hat und sich deshalb gut als
 * Preview ansehen lässt.
 *
 * Die eigentliche Sperre übernimmt der Dienst [de.funke.app.lock.LockService]:
 * Er holt Lumina zurück, sobald man etwas anderes als eine erlaubte App öffnet.
 */
@Composable
fun FocusScreen(
    mode: FocusMode,
    totalMinutes: Int,
    endAtMillis: Long,
    onEnd: (readMinutes: Int, kind: ResultKind) -> Unit,
    cancelStyle: CancelStyle = CancelStyle.Slider,
    onOpenNotes: (() -> Unit)? = null,
    quitConfirm: de.funke.app.ui.QuitConfirm = de.funke.app.ui.QuitConfirm.Strict,
    readingLumi: de.funke.app.ui.ReadingLumi = de.funke.app.ui.ReadingLumi.Off,
) {
    FocusScreenInner(mode, totalMinutes, endAtMillis, onEnd, cancelStyle, onNote = onOpenNotes, quitConfirm = quitConfirm, readingLumi = readingLumi)
}

@Composable
private fun FocusScreenInner(
    mode: FocusMode,
    totalMinutes: Int,
    endAtMillis: Long,
    onEnd: (readMinutes: Int, kind: ResultKind) -> Unit,
    cancelStyle: CancelStyle,
    onNote: (() -> Unit)?,
    quitConfirm: de.funke.app.ui.QuitConfirm,
    readingLumi: de.funke.app.ui.ReadingLumi,
) {
    val context = LocalContext.current
    val installed = LocalInstalledApps.current
    val timerStyle = LocalTimerStyle.current
    val haptics = LocalHaptics.current
    val totalMs = totalMinutes * 60_000L
    var remainingMs by remember {
        mutableLongStateOf((endAtMillis - System.currentTimeMillis()).coerceIn(0L, totalMs))
    }
    var showChallenge by remember { mutableStateOf(false) }
    var showQuit by remember { mutableStateOf(false) }
    var finishing by remember { mutableStateOf(false) }
    // Abbruch (Aufgabe gelöst oder über das X): sofort zum Ergebnis. Seit 11.0 ohne Abschieds-Animation,
    // damit die Sperre im selben Moment aufgeht (vorher holte sie einen während der Animation zurück).
    var ending by remember { mutableStateOf<ResultKind?>(null) }
    var endMinutes by remember { mutableStateOf(0) }
    val currentOnEnd by rememberUpdatedState(onEnd)

    fun readMinutesNow(): Int = ((totalMs - remainingMs) / 60_000L).toInt()

    // Der Zurück-Knopf soll die Session nicht einfach beenden.
    BackHandler(enabled = true) {
        when {
            showQuit -> showQuit = false
            showChallenge -> showChallenge = false
            else -> Unit
        }
    }
    KeepScreenOn()

    LaunchedEffect(endAtMillis) {
        while (true) {
            remainingMs = (endAtMillis - System.currentTimeMillis()).coerceIn(0L, totalMs)
            if (remainingMs == 0L) break
            delay(100)
        }
        if (ending != null) return@LaunchedEffect
        // Abschluss-Animation abspielen, dann zum Ergebnis.
        showChallenge = false
        showQuit = false
        finishing = true
        haptics?.success()
        delay(2800)
        currentOnEnd(totalMinutes, ResultKind.Completed)
    }

    LaunchedEffect(ending) {
        val kind = ending ?: return@LaunchedEffect
        haptics?.heavy()
        currentOnEnd(endMinutes, kind)
    }

    fun startEnding(kind: ResultKind) {
        if (ending != null || finishing) return
        endMinutes = readMinutesNow()
        showChallenge = false
        showQuit = false
        ending = kind
    }

    FocusContent(
        mode = mode,
        installed = installed,
        timerStyle = timerStyle,
        remainingMs = remainingMs,
        totalMs = totalMs,
        showChallenge = showChallenge,
        showQuit = showQuit,
        onOpenApp = { pkg -> openApp(context, pkg) },
        onSlideComplete = { if (ending == null && !finishing) showChallenge = true },
        onKeepReading = { showChallenge = false },
        onSolved = { startEnding(ResultKind.EndedEarly) },
        onQuitClick = {
            if (ending == null && !finishing) {
                if (quitConfirm == de.funke.app.ui.QuitConfirm.None) startEnding(ResultKind.Abandoned) else showQuit = true
            }
        },
        onQuitConfirm = { startEnding(ResultKind.Abandoned) },
        onQuitDismiss = { showQuit = false },
        finishing = finishing,
        ending = ending != null,
        cancelStyle = cancelStyle,
        onNote = onNote,
        strictQuit = quitConfirm == de.funke.app.ui.QuitConfirm.Strict,
        readingLumi = readingLumi,
    )
}

/**
 * Oberfläche der Session.
 *
 * Start: Ein Lichtimpuls breitet sich aus, der Ring federt in seine Größe und zeichnet sich
 * einmal im Uhrzeigersinn voll, die Ziffern kondensieren aus einer Körnchenwolke,
 * danach erscheinen Kopfzeile, Apps und Abbrechen-Knopf nacheinander.
 *
 * Ende ([finishing]): Die Ziffern verdampfen, drei Lichtwellen laufen vom Ring nach außen,
 * der Ring weitet sich und verglimmt, und in der Mitte erscheint "Geschafft".
 */
@Composable
fun FocusContent(
    mode: FocusMode,
    installed: List<InstalledApp>,
    timerStyle: TimerStyleOption,
    remainingMs: Long,
    totalMs: Long,
    showChallenge: Boolean,
    showQuit: Boolean,
    onOpenApp: (String) -> Unit,
    onSlideComplete: () -> Unit,
    onKeepReading: () -> Unit,
    onSolved: () -> Unit,
    onQuitClick: () -> Unit,
    onQuitConfirm: () -> Unit,
    onQuitDismiss: () -> Unit,
    finishing: Boolean = false,
    ending: Boolean = false,
    animateIn: Boolean = true,
    cancelStyle: CancelStyle = CancelStyle.Slider,
    onNote: (() -> Unit)? = null,
    strictQuit: Boolean = true,
    readingLumi: de.funke.app.ui.ReadingLumi = de.funke.app.ui.ReadingLumi.Off,
) {
    val p = Funke.colors
    val flat = Funke.flat
    val ringStyle = LocalRingStyle.current
    // Lumi beim Lesen nur, wenn gewünscht (und Lumi überhaupt an ist). Am Ende freut er sich trotzdem.
    val sidekick = LocalSidekick.current && (readingLumi != de.funke.app.ui.ReadingLumi.Off || finishing)
    val lively = readingLumi == de.funke.app.ui.ReadingLumi.Lively
    val remainingFraction by animateFloatAsState(
        targetValue = if (totalMs > 0) remainingMs / totalMs.toFloat() else 0f,
        animationSpec = tween(150, easing = LinearEasing),
        label = "remaining",
    )
    val seconds = ((remainingMs + 999L) / 1000L).toInt()
    val hours = seconds / 3600
    val timeText = if (hours > 0) {
        "%d:%02d:%02d".format(hours, (seconds % 3600) / 60, seconds % 60)
    } else {
        "%02d:%02d".format(seconds / 60, seconds % 60)
    }

    // --- Start-Animation ---
    val intro = remember { Animatable(if (animateIn) 0f else 1f) }
    val ringPop = remember { Animatable(if (animateIn) 0.78f else 1f) }
    LaunchedEffect(Unit) {
        if (animateIn) {
            launch { ringPop.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = 300f)) }
            intro.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
        }
    }

    // --- End-Animation ---
    val outro = remember { Animatable(0f) }
    LaunchedEffect(finishing) {
        if (finishing) outro.animateTo(1f, tween(2100, easing = LinearEasing))
    }
    val sadOut = remember { Animatable(0f) }
    LaunchedEffect(ending) {
        if (ending) sadOut.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    val chromeAlpha = (1f - outro.value * 4f).coerceIn(0f, 1f) * (1f - sadOut.value)
    // Solange der "Bist du sicher?"-Dialog oder die Aufgabe offen ist, tritt Lumi im Ring leise zurück,
    // damit nicht zwei Lumis übereinander stehen.
    val lumiAway by animateFloatAsState(
        targetValue = if (showQuit || showChallenge) 1f else 0f,
        animationSpec = tween(if (showQuit || showChallenge) de.funke.app.ui.theme.Motion.Fast else de.funke.app.ui.theme.Motion.Slow, easing = de.funke.app.ui.theme.Motion.EaseOut),
        label = "lumiAway",
    )
    // Wie weit der Abbrechen-Regler gezogen ist (Lumi erschrickt darüber)
    val slide = remember { Animatable(0f) }
    val done = remember { Animatable(0f) }
    LaunchedEffect(finishing) {
        if (finishing) {
            delay(750)
            done.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 380f))
        }
    }

    AmbientBackground {
        BoxWithConstraints(Modifier.fillMaxSize().systemBarsPadding()) {
            val ringSize = minOf(maxWidth * 0.80f, 310.dp)
            // Timer-Anzeige: unter dem Ring oder (ohne Lumi) in seiner Mitte
            val timerBlock: @Composable (Boolean) -> Unit = { inRing ->
                // Am Ende verdampft er und "Geschafft" erscheint an seiner Stelle
                Box(
                    Modifier
                        .then(if (inRing) Modifier.width(ringSize * 0.74f) else Modifier.fillMaxWidth())
                        .height(104.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer {
                            alpha = 1f - sadOut.value
                            if (timerStyle != TimerStyleOption.Vapor) {
                                val o = outro.value
                                alpha = intro.value * (1f - o * 2f).coerceIn(0f, 1f) * (1f - sadOut.value)
                                val s = 0.9f + 0.1f * intro.value + 0.15f * o
                                scaleX = s
                                scaleY = s
                            }
                        },
                    ) {
                        when (timerStyle) {
                            TimerStyleOption.Vapor -> VaporDigits(
                                seconds = seconds,
                                digitCount = if (totalMs >= 3_600_000L) 6 else 4,
                                color = p.text,
                                description = tr("Noch $timeText", "$timeText left"),
                                intro = animateIn,
                                dissolve = finishing,
                                modifier = Modifier.size(width = ringSize * (if (inRing) 0.70f else 0.86f), height = 92.dp),
                            )
                            // Ab einer Stunde (H:MM:SS) kleiner und immer einzeilig, damit nichts umbricht
                            TimerStyleOption.Classic -> Text(
                                timeText,
                                style = if (hours > 0) TimerStyle.copy(fontSize = 50.sp, lineHeight = 56.sp, letterSpacing = (-1.2).sp) else TimerStyle,
                                color = p.text,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false,
                            )
                            TimerStyleOption.Flip -> de.funke.app.ui.components.FlipDigits(seconds, cardHeight = if (inRing) 72.dp else 84.dp)
                            TimerStyleOption.Terminal -> de.funke.app.ui.components.TerminalDigits(seconds, big = if (inRing) 46.dp else 54.dp)
                        }
                    }

                    if (ending) {
                        Text(
                            tr("Schade …", "Oh no …"),
                            style = MaterialTheme.typography.headlineLarge,
                            color = p.textSecondary,
                            softWrap = false,
                            modifier = Modifier.wrapContentWidth(unbounded = true).graphicsLayer {
                                val d = ((sadOut.value - 0.4f) / 0.6f).coerceIn(0f, 1f)
                                alpha = d
                                translationY = 10.dp.toPx() * (1f - d)
                            },
                        )
                    }

                    if (finishing) {
                        Text(
                            tr("Geschafft!", "Done!"),
                            style = MaterialTheme.typography.displayLarge,
                            color = p.text,
                            softWrap = false,
                            modifier = Modifier.wrapContentWidth(unbounded = true).graphicsLayer {
                                val d = done.value
                                alpha = d.coerceIn(0f, 1f)
                                val sc = 0.6f + 0.4f * d
                                scaleX = sc
                                scaleY = sc
                            },
                        )
                    }
                }

            }


            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = Space.s6, vertical = Space.s3),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Kopfzeile (seit 15.2 wie auf der Startseite): der Modus als Kugel mit Namen, ohne Glas-Pille.
                // Kugel und Kreuz stehen auf denselben Achsen wie Schloss und Rechner unten – alles fluchtet.
                val axis = cancelStyle.axis()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .then(if (animateIn) Modifier.reveal(650) else Modifier)
                        .graphicsLayer { alpha = chromeAlpha }
                        .padding(start = (axis - 12.dp).coerceAtLeast(0.dp), end = (axis - 18.dp).coerceAtLeast(0.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(Modifier.weight(1f, fill = false), verticalAlignment = Alignment.CenterVertically) {
                        de.funke.app.ui.components.ModeOrb(mode.name, selected = true, size = 24.dp)
                        Spacer(Modifier.width(Space.s3))
                        Text(
                            mode.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = p.textSecondary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                    }
                    // Bewusst unauffällig: klein, ohne Fläche, fast durchsichtig.
                    Box(
                        Modifier
                            .size(36.dp)
                            .bouncyClickable(onClick = { if (!finishing && !ending) onQuitClick() })
                            .graphicsLayer { alpha = 0.38f }
                            .semantics { contentDescription = tr("Beenden", "End") },
                        contentAlignment = Alignment.Center,
                    ) {
                        CloseIcon(p.textTertiary, Modifier.size(11.dp))
                    }
                }

                Spacer(Modifier.weight(1f))

                Box(Modifier.size(ringSize), contentAlignment = Alignment.Center) {
                    // Lichtimpuls beim Start und Wellen am Ende
                    Canvas(
                        Modifier
                            .size(ringSize)
                            .graphicsLayer {
                                // darf über den Ring hinaus leuchten
                                clip = false
                            }
                    ) {
                        val r = size.minDimension / 2f
                        val burst = intro.value
                        if (burst < 1f) {
                            val br = r * (0.2f + 1.3f * burst)
                            if (flat) {
                                drawCircle(p.accent.copy(alpha = 0.16f * (1f - burst)), radius = br)
                            } else {
                                drawCircle(
                                    Brush.radialGradient(
                                        listOf(p.accent.copy(alpha = 0.55f * (1f - burst)), Color.Transparent),
                                        center = center,
                                        radius = br,
                                    ),
                                    radius = br,
                                )
                            }
                        }
                        val o = outro.value
                        if (o > 0f) {
                            // Aufleuchten in der Mitte
                            val flash = (1f - kotlin.math.abs(o * 2.2f - 0.5f) * 2f).coerceIn(0f, 1f)
                            if (!flat) {
                                drawCircle(
                                    Brush.radialGradient(
                                        listOf(p.accent.copy(alpha = 0.40f * flash), Color.Transparent),
                                        center = center,
                                        radius = r * 1.3f,
                                    ),
                                    radius = r * 1.3f,
                                )
                            }
                            // drei Wellen
                            for (k in 0 until 3) {
                                val t = (o * 1.5f - k * 0.18f).coerceIn(0f, 1f)
                                if (t <= 0f || t >= 1f) continue
                                drawCircle(
                                    color = p.accent.copy(alpha = 0.55f * (1f - t)),
                                    radius = r * (0.92f + 0.75f * t),
                                    style = Stroke(width = (6.dp.toPx()) * (1f - t) + 1f),
                                )
                            }
                        }
                    }

                    TimerRing(
                        progress = remainingFraction * intro.value,
                        style = ringStyle,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val o = outro.value
                                val s = ringPop.value * (1f + 0.10f * o)
                                scaleX = s
                                scaleY = s
                                alpha = (intro.value * 3f).coerceAtMost(1f) * (1f - o * 1.6f).coerceIn(0f, 1f) *
                                    (1f - 0.6f * sadOut.value)
                            },
                        stroke = 12.dp,
                    )

                    // Konfetti schießt hinter Lumi hoch, wenn die Zeit geschafft ist
                    ConfettiBurst(play = finishing, modifier = Modifier.size(ringSize))

                    // Lumi in der Mitte des Rings, mit Luft zum Rand. Leicht nach oben versetzt,
                    // damit die Figur (nicht das Bild mit dem Schein) optisch mittig sitzt.
                    if (!sidekick) timerBlock(true)

                    if (sidekick) RingLumi(
                        pose = when {
                            finishing -> LumiPose.Happy
                            ending -> LumiPose.Sad
                            else -> LumiPose.Reading
                        },
                        slide = slide,
                        alarmed = lively && (showChallenge || showQuit),
                        reacts = lively,
                        modifier = Modifier
                            .size(ringSize * 0.66f)
                            .offset(y = -(ringSize * 0.66f * 0.03f))
                            .graphicsLayer {
                                alpha = intro.value.coerceIn(0f, 1f) * (1f - lumiAway)
                                val s = (0.85f + 0.15f * intro.value) * (1f - 0.08f * lumiAway)
                                scaleX = s
                                scaleY = s
                            },
                    )
                }

                // Timer unter dem Ring (mit Lumi) oder in der Mitte des Rings (ohne Lumi)
                if (sidekick) timerBlock(false)

                Spacer(Modifier.weight(1f))

                Column(
                    Modifier
                        .then(if (animateIn) Modifier.reveal(850) else Modifier)
                        .graphicsLayer { alpha = chromeAlpha }
                ) {
                    // Unten: kompakte Leiste mit den erlaubten Apps (ausklappbar) und rechts der Notizblock
                    AllowedAndNotes(
                        mode,
                        installed,
                        onOpenApp,
                        onNote = if (onNote != null) ({ if (!finishing && !ending) onNote() }) else null,
                        axis = cancelStyle.axis(),
                    )
                }
                Spacer(Modifier.height(Space.s2))
                Box(
                    Modifier
                        .then(if (animateIn) Modifier.reveal(1000) else Modifier)
                        .graphicsLayer { alpha = chromeAlpha }
                        
                ) {
                    CancelControl(
                        style = cancelStyle,
                        progress = slide,
                        enabled = !finishing && !ending,
                        onConfirm = { if (!finishing && !ending) onSlideComplete() },
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showChallenge,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250)),
        ) {
            CancelChallenge(onKeepReading = onKeepReading, onSolved = onSolved)
        }

        // Beenden über das Kreuz: erst die Bitte, dann (streng) noch zwei große Seiten, bevor wirklich Schluss ist.
        // "Trotzdem beenden" steht jedes Mal woanders, damit sich die Hand den Weg nicht merkt.
        var guiltStage by remember { mutableStateOf(0) }
        androidx.compose.runtime.LaunchedEffect(showQuit) { if (!showQuit) guiltStage = 0 }
        val quitSlots = remember(showQuit) {
            val r = kotlin.random.Random
            val s1 = r.nextInt(3)
            intArrayOf(r.nextInt(3), s1, (s1 + 1 + r.nextInt(2)) % 3)
        }
        AnimatedVisibility(
            visible = showQuit && guiltStage == 0,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150)),
        ) {
            PleadDialog(
                onStay = onQuitDismiss,
                onQuit = { if (strictQuit) guiltStage = 1 else onQuitConfirm() },
                quitSlot = quitSlots[0],
            )
        }
        AnimatedVisibility(
            visible = showQuit && guiltStage > 0,
            enter = fadeIn(tween(260)),
            exit = fadeOut(tween(200)),
        ) {
            GuiltScreen(
                stage = guiltStage.coerceAtLeast(1),
                onStay = onQuitDismiss,
                onNext = { if (guiltStage >= 2) onQuitConfirm() else guiltStage = 2 },
                quitSlot = quitSlots[guiltStage.coerceIn(1, 2)],
            )
        }
    }
}

/** Lumi im Ring. Liest den Regler-Fortschritt hier, damit nur Lumi bei jedem Bild neu aufgebaut wird. */
@Composable
private fun RingLumi(
    pose: LumiPose,
    slide: Animatable<Float, AnimationVector1D>,
    alarmed: Boolean,
    reacts: Boolean,
    modifier: Modifier = Modifier,
) {
    Lumi2D(
        pose = pose,
        scare = if (!reacts) 0f else if (alarmed) 1f else slide.value,
        tappable = reacts,
        contentDescription = when (pose) {
            LumiPose.Happy -> tr("Lumi freut sich", "Lumi is happy")
            LumiPose.Sad -> tr("Lumi ist traurig", "Lumi is sad")
            LumiPose.Reading -> tr("Lumi liest", "Lumi is reading")
            LumiPose.Idle -> "Lumi"
            LumiPose.Plead, LumiPose.Puppy, LumiPose.Heartbroken -> tr("Lumi schaut dich bittend an", "Lumi looks at you, pleading")
        },
        modifier = modifier,
    )
}

/**
 * Achse der Leiste unten (seit 15.2): So weit vom Rand liegt die Mitte des Knopfs im Abbrechen-Regler
 * (Mulde 4 dp + halber Knopf). Das Schloss links steht genau darüber, der Rechner rechts genau über dem Ziel
 * des Reglers – dadurch fluchtet alles, statt links weiter draußen und rechts weiter drinnen zu stehen.
 */
private fun CancelStyle.axis() = when (this) {
    CancelStyle.Line -> 24.dp
    CancelStyle.Slider, CancelStyle.Hold -> 32.dp
}

/**
 * Unten im Fokus: links das Schloss – geschlossen mit "Alles gesperrt" oder offen mit den erlaubten Apps zum
 * Antippen –, rechts Notizen und Rechner als schlichte Symbole wie auf der Startseite. Status leise (tertiär),
 * Werkzeuge etwas kräftiger (sekundär), damit man sieht, was man antippen kann.
 */
@Composable
private fun AllowedAndNotes(
    mode: FocusMode,
    installed: List<InstalledApp>,
    onOpenApp: (String) -> Unit,
    onNote: (() -> Unit)?,
    axis: androidx.compose.ui.unit.Dp,
) {
    val p = Funke.colors
    var calc by remember { mutableStateOf(false) }
    val locked = mode.packages.isEmpty()
    val icon = 22.dp
    val touch = 44.dp
    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(axis - icon / 2))
        Icon(
            painterResource(if (locked) de.funke.app.R.drawable.ic_lock else de.funke.app.R.drawable.ic_unlock),
            contentDescription = if (locked) null else tr("Erlaubte Apps", "Allowed apps"),
            tint = p.textTertiary,
            modifier = Modifier.size(icon),
        )
        Spacer(Modifier.width(Space.s3))
        if (locked) {
            Text(
                tr("Alles gesperrt", "All apps locked"),
                style = MaterialTheme.typography.bodyMedium,
                color = p.textTertiary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        } else {
            Row(
                Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.s2),
            ) {
                mode.packages.forEach { pkg ->
                    val app = installed.firstOrNull { it.packageName == pkg }
                    val name = app?.label ?: pkg.substringAfterLast('.')
                    Box(
                        Modifier
                            .bouncyClickable(shape = Shapes.sm, pressedScale = 0.92f) { onOpenApp(pkg) }
                            .semantics { contentDescription = tr("$name öffnen", "Open $name") },
                    ) { AppIcon(app, name, size = 32.dp) }
                }
            }
        }
        if (onNote != null) {
            de.funke.app.ui.components.HeaderIcon(tr("Notizen", "Notes"), onClick = onNote) {
                NotepadIcon(p.textSecondary, Modifier.size(icon))
            }
        }
        de.funke.app.ui.components.HeaderIcon(tr("Rechner", "Calculator"), onClick = { calc = true }) {
            Icon(painterResource(de.funke.app.R.drawable.ic_calculator), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(icon))
        }
        Spacer(Modifier.width((axis - touch / 2).coerceAtLeast(0.dp)))
    }
    if (calc) de.funke.app.ui.calc.CalculatorWindow(onDismiss = { calc = false })
}

private fun openApp(context: Context, packageName: String) {
    // Merken, damit die Sperre eine sofortige Weiterleitung (z. B. Gemini -> Google-App) erkennt
    de.funke.app.lock.SessionLock.noteLaunch(packageName)
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    val started = intent != null && runCatching { context.startActivity(intent) }.isSuccess
    if (!started) {
        Toast.makeText(context, tr("Diese App lässt sich nicht öffnen.", "This app can't be opened."), Toast.LENGTH_SHORT).show()
    }
}

/** Hält den Bildschirm an, solange die Session läuft. */
@Composable
private fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}
