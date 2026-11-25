package de.funke.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.R
import de.funke.app.gate.Gate
import de.funke.app.gate.GateIntensity
import de.funke.app.lock.SessionLock
import de.funke.app.ui.AppState
import de.funke.app.ui.DeepLink
import de.funke.app.ui.InstalledApp
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.challenge.PracticeSubjects
import de.funke.app.ui.challenge.Topic
import de.funke.app.ui.challenge.art
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.AppIcon
import de.funke.app.ui.components.AppIconRow
import de.funke.app.ui.components.BottomSheet
import de.funke.app.ui.components.ChevronRight
import de.funke.app.ui.components.CloseButton
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.SegmentedToggle
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import de.funke.app.usage.AppGroups
import de.funke.app.usage.ScreenTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import de.funke.app.ui.components.Unfold

/*
 * Reiter "Denkpause" (seit 13.3 in zwei Seiten getrennt, damit nichts mehr durcheinandergeht):
 *
 *   [ Fragen | Erinnerungen ]
 *
 *  - Fragen: Lumi mit Schalter und einem Satz, was passiert. Ist die Denkpause aus, döst Lumi (seit 15.0; vorher
 *    war sein Licht aus und der Kolben weiß). Erst wenn sie an ist, klappen darunter drei Zeilen auf: Apps, Zeiten,
 *    Fragen (vorher halb durchsichtig, aber trotzdem antippbar – das war verwirrend).
 *    "Zeiten" öffnet ein Blatt mit einem 24-Stunden-Zifferblatt (statt der alten Linie), Vorlagen und Stunden.
 *  - Erinnerungen ([RemindersPage]): Lesen, Streak, Schlafenszeit, Nutzungslimit und eigene Erinnerungen.
 *    Jede Karte sagt, wann und wie sie kommt; antippen öffnet den Editor.
 * Fehlt ein Recht, steht genau dort eine Zeile zum Beheben.
 */
@Composable
fun GateTab(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val context = LocalContext.current
    // Gate ist seit 13.4 Compose-Zustand; der Zähler bleibt nur für Dinge außerhalb (Bedienungshilfe, Rückkehr)
    var tick by remember { mutableIntStateOf(0) }
    fun change(block: () -> Unit) { block(); tick++ }
    @Suppress("UNUSED_VARIABLE") val t = tick
    var section by rememberSaveable { mutableIntStateOf(0) }
    // Von außen (Einstellungen → "Erinnerungen einstellen") direkt auf die zweite Seite
    LaunchedEffect(DeepLink.gateSection) {
        if (DeepLink.gateSection >= 0) {
            section = DeepLink.gateSection
            DeepLink.gateSection = -1
        }
    }
    var serviceOn by remember { mutableStateOf(SessionLock.isServiceEnabled(context)) }
    var guide by remember { mutableStateOf(false) }
    var picking by remember { mutableStateOf(false) }
    var sheet by remember { mutableStateOf<GateSheet?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                serviceOn = SessionLock.isServiceEnabled(context)
                tick++
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
    // Editor-Blätter der Erinnerungsseite liegen über allem (auch über der Leiste)
    val reminderSheets = rememberReminderSheets()

    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = NavBarSpace + Space.s6),
        ) {
            Text(tr("Denkpause", "Think break"), style = MaterialTheme.typography.headlineLarge, color = p.text, modifier = Modifier.reveal(0))
            Spacer(Modifier.height(Space.s5))
            SegmentedToggle(
                options = listOf(tr("Fragen", "Questions"), tr("Erinnerungen", "Reminders")),
                selected = section,
                onSelect = { section = it },
                modifier = Modifier.reveal(20),
            )
            Spacer(Modifier.height(Space.s5))
            val still = reducedMotion
            AnimatedContent(
                targetState = section,
                transitionSpec = {
                    // clip = false: sonst schneidet der Wechsel die Kanten/Schatten der Karten unten ab (Cartoon-Stil)
                    (if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
                    else (fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) +
                        androidx.compose.animation.slideInHorizontally(tween(Motion.Medium, easing = Motion.EaseOut)) { w -> (if (targetState > initialState) w else -w) / 10 }) togetherWith
                        fadeOut(tween(Motion.Fast))) using androidx.compose.animation.SizeTransform(clip = false)
                },
                label = "gateSection",
            ) { s ->
                Column(Modifier.fillMaxWidth()) {
                    if (s == 0) QuestionsPage(
                        state = state,
                        serviceOn = serviceOn,
                        onToggle = {
                            change {
                                val next = !Gate.enabled
                                Gate.applyEnabled(next)
                                if (next && Gate.apps.isEmpty()) picking = true
                            }
                        },
                        onApps = { picking = true },
                        onTimes = { sheet = GateSheet.Times },
                        onQuestions = { sheet = GateSheet.Questions },
                        onFix = { guide = true },
                        onTry = {
                            de.funke.app.ui.DeepLink.gate = de.funke.app.ui.GateRequest(
                                pkg = Gate.apps.firstOrNull().orEmpty(),
                                checkIn = false,
                                preview = true,
                            )
                        },
                    ) else RemindersPage(state, reminderSheets)
                }
            }
        }

        // ----- Blätter der Seite "Fragen" -----
        BottomSheet(visible = sheet != null, onDismiss = { sheet = null }) {
            when (sheet) {
                GateSheet.Questions -> {
                    SheetTitle(tr("Fächer & Stufe", "Subjects & level"))
                    QuestionSettings(state)
                }
                GateSheet.Times -> TimesSheet(onChange = { block -> change(block) })
                null -> Unit
            }
        }

        ReminderSheetsHost(state, reminderSheets)
        if (guide) AccessibilityGuide(onDismiss = { guide = false })

        AnimatedVisibility(visible = picking, enter = Motion.sheetEnter(false), exit = Motion.sheetExit(false)) {
            GateAppPicker(Gate.apps, title = tr("Vor welchen Apps?", "Before which apps?")) { chosen ->
                change {
                    Gate.applyApps(chosen)
                    if (chosen.isNotEmpty() && !Gate.enabled) Gate.applyEnabled(true)
                }
                picking = false
            }
        }
    }
}

private enum class GateSheet { Questions, Times }

// ---------------------------------------------------------------------------------------------
// Seite 1: Fragen
// ---------------------------------------------------------------------------------------------

@Composable
private fun QuestionsPage(
    state: AppState,
    serviceOn: Boolean,
    onToggle: () -> Unit,
    onApps: () -> Unit,
    onTimes: () -> Unit,
    onQuestions: () -> Unit,
    onFix: () -> Unit,
    onTry: () -> Unit,
) {
    val context = LocalContext.current
    val installed = LocalInstalledApps.current
    HeroCard(
        on = Gate.enabled,
        ready = serviceOn,
        hasApps = Gate.apps.isNotEmpty(),
        onToggle = onToggle,
        onFix = onFix,
        onTry = onTry,
    )
    Spacer(Modifier.height(Space.s3))
    val cs = state.challengeSettings
    val subjects = PracticeSubjects.filter { s -> Topic.entries.any { it.subject == s && it in cs.topics } }
    val still = reducedMotion
    // Die Einstellungen erst zeigen, wenn die Denkpause an ist (ein Schalter, dann die Details)
    Unfold(visible = Gate.enabled, enter = Motion.expand(still), exit = Motion.collapse(still)) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(Space.s3))
            ListCard {
                ListRow(R.drawable.ic_nav_screen, tr("Apps", "Apps"), onClick = onApps) {
                    if (Gate.apps.isEmpty()) ValueText(tr("Auswählen", "Choose"), accent = true)
                    else AppIconRow(Gate.apps.toList(), installed, size = 26.dp, maxShown = 4)
                }
                CardDivider()
                ListRow(R.drawable.ic_timer, tr("Zeiten", "Times"), onClick = onTimes) {
                    ValueText(timesSummary())
                }
                CardDivider()
                ListRow(R.drawable.ic_nav_tasks, tr("Fächer & Stufe", "Subjects & level"), onClick = onQuestions) {
                    Box(Modifier.height(24.dp).width((24 + 14 * (subjects.take(3).size - 1).coerceAtLeast(0)).dp)) {
                        subjects.take(3).forEachIndexed { i, s ->
                            Image(painterResource(s.art), contentDescription = null, modifier = Modifier.offset(x = (i * 14).dp).size(24.dp))
                        }
                    }
                    Spacer(Modifier.width(Space.s2))
                    ValueText(cs.difficulty.label)
                }
            }
            // Heute: ein ruhiger Satz statt Zahlenkacheln
            if (Gate.shownToday > 0) {
                Spacer(Modifier.height(Space.s4))
                Text(
                    todayLine(),
                    style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum, ss03"),
                    color = Funke.colors.textTertiary,
                    modifier = Modifier.padding(horizontal = Space.s2),
                )
            }
            // Platz für Schatten/Kontur der Karte, damit das Aufklappen nichts abschneidet
            Spacer(Modifier.height(Space.s2))
        }
    }
}


/** "5× · nächste 14:00" */
private fun timesSummary(): String {
    val n = Gate.hours.size
    val next = Gate.nextSlot()
    val count = tr("$n×", "$n×")
    return if (Gate.enabled && next != null) count + tr(", nächste um ${next.hour}:00", ", next at ${clock(next.hour)}") else count + tr(" am Tag", " a day")
}

private fun todayLine(): String {
    val shown = Gate.shownToday
    val right = Gate.rightToday
    val resisted = Gate.resistedToday
    val base = tr("Heute: $right von $shown richtig", "Today: $right of $shown right")
    return if (resisted > 0) base + tr(", $resisted× die App doch nicht geöffnet", ", $resisted× decided not to open the app") else base
}

/** Oben: Lumi, "Erst eine Frage", ein Satz, was passiert, und der Schalter. Ist sie aus, döst Lumi. */
@Composable
private fun HeroCard(on: Boolean, ready: Boolean, hasApps: Boolean, onToggle: () -> Unit, onFix: () -> Unit, onTry: () -> Unit) {
    val p = Funke.colors
    // Seit 15.1 ohne Karte: Lumi, Satz und Schalter stehen frei oben – die Karte darunter (Apps, Zeiten, Fächer)
    // ist dann die einzige Fläche, und man sieht sofort, was zusammengehört.
    // Seit 15.4 erklärt der Satz selbst, wozu das gut ist, und darunter steht leise "Ausprobieren" – statt einer
    // eigenen Bildfolge, die das ruhige Bild zerrissen hat.
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .bouncyClickable(shape = Shapes.card, pressedScale = 0.99f, role = Role.Switch, onClick = onToggle)
                .padding(start = Space.s1, end = Space.s1, top = Space.s2, bottom = Space.s2)
                .semantics { stateDescription = if (on) tr("An", "On") else tr("Aus", "Off") },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Lumi2D(LumiPose.Idle, Modifier.size(56.dp).align(Alignment.Top), hat = false, tappable = false, asleep = !on)
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.weight(1f)) {
                Text(tr("Erst eine Frage", "A question first"), style = MaterialTheme.typography.titleMedium, color = p.text)
                Spacer(Modifier.height(Space.s1))
                val status = when {
                    !on -> tr(
                        "Bevor eine gewählte App aufgeht, stellt Lumi eine kurze Frage. Ein Moment Pause statt Scrollen.",
                        "Before an app you picked opens, Lumi asks a short question. A moment's pause instead of scrolling.",
                    )
                    !hasApps -> tr("Wähle die Apps, vor denen gefragt wird.", "Pick the apps to ask before.")
                    Gate.isDue() -> tr("Eine Frage wartet vor der nächsten App.", "A question is waiting before the next app.")
                    else -> Gate.nextSlot()?.let { tr("Bis ${it.hour}:00 sind die Apps frei.", "Apps are free until ${clock(it.hour)}.") }
                        ?: tr("Vor gewählten Apps kommt erst eine kurze Frage.", "A short question comes before the apps you pick.")
                }
                Text(status, style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum, ss03"), color = p.textSecondary)
                // Eine echte Denkpause als Probe (zählt nichts, öffnet keine App), bündig mit dem Text darüber
                Row(
                    Modifier
                        .padding(top = Space.s2)
                        .offset(x = -Space.s2)
                        .heightIn(min = 36.dp)
                        .bouncyClickable(shape = Shapes.pill, pressedScale = 0.95f, role = Role.Button, onClick = onTry)
                        .padding(horizontal = Space.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painterResource(R.drawable.ic_play), contentDescription = null, tint = p.accent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(tr("Ausprobieren", "Try it"), style = MaterialTheme.typography.labelLarge, color = p.accent)
                }
            }
            Spacer(Modifier.width(Space.s3))
            // Schalter oben auf Höhe der Überschrift (der Text darunter kann mehrzeilig sein)
            Box(Modifier.align(Alignment.Top).padding(top = Space.s1)) { LuminaSwitch(on) }
        }
        if (on && !ready) {
            Spacer(Modifier.height(Space.s2))
            // Das Einzige, was hier fehlt: zart rot hinterlegt, damit es nicht übersehen wird
            Box(Modifier.clip(Shapes.md).background(p.danger.copy(alpha = if (p.isDark) 0.12f else 0.07f))) {
                FixRow(tr("Bedienungshilfe einschalten", "Turn on accessibility service"), onFix = onFix)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Zeiten: Vorlagen, Zifferblatt, Stunden
// ---------------------------------------------------------------------------------------------

@Composable
private fun ColumnScope.TimesSheet(onChange: (() -> Unit) -> Unit) {
    val p = Funke.colors
    SheetTitle(tr("Wann kommt eine Frage?", "When does a question come?"))
    val options = GateIntensity.entries
    SegmentedToggle(
        options = options.map { if (it == GateIntensity.Custom) tr("Eigene", "Custom") else "${it.hours.size}×" },
        selected = options.indexOf(Gate.intensity),
        onSelect = { i -> onChange { Gate.applyIntensity(options[i]) } },
    )
    Spacer(Modifier.height(Space.s2))
    Text(
        when (Gate.intensity) {
            GateIntensity.Custom -> tr("Deine Zeiten. Tippe auf eine Stunde, um sie an- oder auszuschalten.", "Your times. Tap an hour to switch it on or off.")
            else -> tr("${Gate.intensity.label}: Ab jeder dieser Uhrzeiten kommt beim nächsten Öffnen eine Frage. Stunden antippen, um eigene Zeiten zu wählen.", "${Gate.intensity.label}: from each of these times, the next app opening brings a question. Tap hours to pick your own.")
        },
        style = MaterialTheme.typography.bodySmall,
        color = p.textSecondary,
        modifier = Modifier.padding(horizontal = Space.s1),
    )
    Spacer(Modifier.height(Space.s4))
    DayDial(
        hours = Gate.hours,
        next = Gate.nextSlot()?.hour.takeIf { Gate.enabled },
        editable = true,
        onToggle = { h -> onChange { Gate.editHour(h) } },
        modifier = Modifier.fillMaxWidth().widthIn(max = 320.dp).align(Alignment.CenterHorizontally),
    )
    // Stundenraster: immer da; eine Stunde antippen macht aus einer Vorlage eigene Zeiten
    HourPicker(Gate.hours) { h -> onChange { Gate.editHour(h) } }
}

/**
 * Der Tag als Zifferblatt (seit 13.3, statt der Linie): 24 Stunden im Kreis, oben Mitternacht.
 * Jede Fragezeit ist ein kleines Licht auf dem Ring, die nächste leuchtet am stärksten. Der schon vergangene
 * Teil des Tages ist leicht getönt, ein kurzer Strich zeigt "jetzt". In der Mitte steht die nächste Frage.
 * Bei "Eigene" schaltet Antippen eine Stunde (6–23 Uhr) an oder aus.
 */
@Composable
private fun DayDial(hours: List<Int>, next: Int?, editable: Boolean, onToggle: (Int) -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val still = reducedMotion
    val measurer = androidx.compose.ui.text.rememberTextMeasurer()
    val label = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03")
    val now = remember { java.time.LocalTime.now() }
    val nowHour = now.hour + now.minute / 60f
    // Jede Stunde hat ihr eigenes Licht, das weich an- und ausgeht
    val lights = remember { List(24) { Animatable(0f) } }
    LaunchedEffect(hours) {
        hours.sorted().forEachIndexed { i, h ->
            launch {
                if (still) lights[h].snapTo(1f)
                else { delay(i * Motion.Stagger.toLong()); lights[h].animateTo(1f, tween(Motion.Medium, easing = Motion.Bob)) }
            }
        }
        (0 until 24).filter { it !in hours }.forEach { h ->
            launch { if (still) lights[h].snapTo(0f) else lights[h].animateTo(0f, tween(Motion.Fast, easing = Motion.EaseOut)) }
        }
    }
    val nextLight by animateFloatAsState(if (next != null) 1f else 0f, Motion.spec(Motion.Medium), label = "dialNext")

    Box(modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(
            Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = tr("Fragezeiten: ", "Question times: ") + hours.sorted().joinToString(", ") { "$it:00" }
                }
                .pointerInput(editable) {
                    if (!editable) return@pointerInput
                    detectTapGestures { pos ->
                        val c = Offset(size.width / 2f, size.height / 2f)
                        val d = hypot(pos.x - c.x, pos.y - c.y)
                        val r = minOf(size.width, size.height) * 0.40f
                        // Nur Tipps in der Nähe des Rings zählen
                        if (d < r * 0.62f || d > r * 1.35f) return@detectTapGestures
                        var a = atan2(pos.y - c.y, pos.x - c.x) + (PI / 2).toFloat()
                        if (a < 0) a += (2 * PI).toFloat()
                        val h = ((a / (2 * PI).toFloat()) * 24f).roundToInt() % 24
                        if (h in Gate.CustomRange) {
                            haptics?.tick()
                            onToggle(h)
                        }
                    }
                },
        ) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension * 0.40f
            val ring = 10.dp.toPx()
            fun angle(h: Float) = (h / 24f) * 2f * PI.toFloat() - PI.toFloat() / 2f
            fun at(h: Float, rad: Float) = Offset(c.x + cos(angle(h)) * rad, c.y + sin(angle(h)) * rad)
            val topLeft = Offset(c.x - r, c.y - r)
            val arcSize = Size(2 * r, 2 * r)

            // Ring
            drawCircle(p.track.copy(alpha = (p.track.alpha * 2f).coerceAtMost(1f)), radius = r, center = c, style = Stroke(ring))
            // Nachts (0–6 Uhr) wird nie gefragt: dort ist der Ring etwas blasser
            drawArc(p.bg.copy(alpha = 0.35f), startAngle = -90f, sweepAngle = 6 * 15f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(ring))
            // Vergangener Teil von heute
            drawArc(p.accent.copy(alpha = 0.16f), startAngle = -90f, sweepAngle = nowHour * 15f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(ring, cap = StrokeCap.Round))

            // Stundenpunkte und Beschriftung (0, 6, 12, 18)
            for (h in 0 until 24) {
                val pos = at(h.toFloat(), r)
                val dim = h !in Gate.CustomRange
                drawCircle(p.textTertiary.copy(alpha = if (dim) 0.18f else 0.38f), radius = 1.6.dp.toPx(), center = pos)
                if (h % 6 == 0) {
                    val text = measurer.measure("$h", label.copy(color = p.textTertiary))
                    val lp = at(h.toFloat(), r + 20.dp.toPx())
                    drawText(text, topLeft = Offset(lp.x - text.size.width / 2f, lp.y - text.size.height / 2f))
                }
            }

            // Fragezeiten als Lichter
            for (h in 0 until 24) {
                val v = lights[h].value
                if (v <= 0.01f) continue
                val pos = at(h.toFloat(), r)
                val isNext = h == next
                val core = (if (isNext) 8.dp.toPx() else 6.dp.toPx()) * v
                val glow = (if (isNext) 22.dp.toPx() else 14.dp.toPx()) * v
                val glowAlpha = if (isNext) 0.42f * nextLight + 0.22f * (1f - nextLight) else 0.22f
                drawCircle(
                    Brush.radialGradient(listOf(p.accent.copy(alpha = glowAlpha), p.accent.copy(alpha = 0f)), center = pos, radius = glow),
                    radius = glow, center = pos,
                )
                val passed = h < nowHour && !isNext
                drawCircle(if (passed) p.accent.copy(alpha = 0.55f) else p.accent, radius = core, center = pos)
                // Die nächste hat einen hellen Kern, wie Lumis Glühdraht
                if (isNext) drawCircle(p.onAccent.copy(alpha = 0.9f * nextLight), radius = core * 0.36f, center = pos)
            }

            // Jetzt: kurzer Strich innen am Ring
            val inner = at(nowHour, r - ring / 2f - 4.dp.toPx())
            val inner2 = at(nowHour, r - ring / 2f - 16.dp.toPx())
            drawLine(p.text.copy(alpha = 0.55f), inner2, inner, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        }
        // Mitte: die nächste Frage
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (next != null) "%d:00".format(next) else "${hours.size}×",
                style = MaterialTheme.typography.headlineLarge.copy(fontFeatureSettings = "tnum, ss03"),
                color = p.text,
            )
            Text(
                if (next != null) tr("nächste Frage", "next question") else tr("am Tag", "a day"),
                style = MaterialTheme.typography.bodySmall,
                color = p.textTertiary,
            )
        }
    }
}

/** Eigene Fragezeiten: die Stunden als Kapseln zum Antippen (genauer als das Zifferblatt). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HourPicker(hours: List<Int>, onToggle: (Int) -> Unit) {
    val p = Funke.colors
    FlowRow(
        Modifier.fillMaxWidth().padding(top = Space.s5),
        horizontalArrangement = Arrangement.spacedBy(Space.s2, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        Gate.CustomRange.forEach { h ->
            val chosen = h in hours
            val bg by animateColorAsState(if (chosen) p.accent else p.track, Motion.fade(Motion.Fast), label = "hour$h")
            Box(
                Modifier
                    .size(width = 48.dp, height = 40.dp)
                    .bouncyClickable(shape = Shapes.pill, pressedScale = 0.94f, role = Role.Checkbox) { onToggle(h) }
                    .clip(Shapes.pill)
                    .background(bg)
                    .semantics { stateDescription = if (chosen) tr("An", "On") else tr("Aus", "Off") },
                contentAlignment = Alignment.Center,
            ) {
                Text("$h", style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = if (chosen) p.onAccent else p.textSecondary)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Gemeinsame Bausteine (auch für die Erinnerungsseite)
// ---------------------------------------------------------------------------------------------

@Composable
internal fun SheetTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, color = Funke.colors.text)
    Spacer(Modifier.height(Space.s5))
}

/** Karte mit Zeilen untereinander (wie eine Gruppe in den Systemeinstellungen). */
@Composable
internal fun ListCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().glass(Shapes.card).padding(vertical = Space.s1), content = content)
}

@Composable
internal fun CardDivider() {
    Box(Modifier.padding(start = 64.dp).fillMaxWidth().height(1.dp).background(Funke.colors.divider))
}

/** Symbol in einer kleinen Kachel, wie überall in Listen. */
@Composable
internal fun RowIcon(icon: Int, dim: Boolean = false) {
    val p = Funke.colors
    // Seit 15.1 schlicht wie in den Einstellungen: kein farbiges Kästchen je Zeile (der Akzent gehört den Aktionen)
    Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Icon(painterResource(icon), contentDescription = null, tint = if (dim) p.textTertiary else p.textSecondary, modifier = Modifier.size(Sizes.icon))
    }
}

/** Zeile: Symbol, Titel, rechts der aktuelle Wert und ein Pfeil. Die ganze Zeile öffnet die Auswahl. */
@Composable
internal fun ListRow(icon: Int, title: String, onClick: () -> Unit, value: @Composable RowScope.() -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .bouncyClickable(shape = Shapes.md, pressedScale = 1f, onClick = onClick)
            .padding(start = Space.s4, end = Space.s3, top = Space.s2, bottom = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RowIcon(icon)
        Spacer(Modifier.width(Space.s3))
        Text(title, style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f), maxLines = 1)
        value()
        Spacer(Modifier.width(Space.s1))
        ChevronRight()
    }
}

@Composable
internal fun ValueText(text: String, accent: Boolean = false) {
    val p = Funke.colors
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum, ss03"),
        color = if (accent) p.accent else p.textSecondary,
        maxLines = 1,
    )
}

/** Fehlendes Recht: farbiger Punkt, kurzer Satz (optional mit Grund), Pfeil. */
@Composable
internal fun FixRow(text: String, detail: String? = null, soft: Boolean = false, onFix: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .bouncyClickable(shape = Shapes.md, pressedScale = 1f, onClick = onFix)
            .padding(horizontal = Space.s4, vertical = Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(if (soft) p.warning else p.danger))
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            Text(text, style = MaterialTheme.typography.titleSmall, color = p.text)
            if (detail != null) Text(detail, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
        }
        Spacer(Modifier.width(Space.s2))
        ChevronRight()
    }
}

/**
 * Apps auswählen: oben Vorschläge (Social Media, Videos, Spiele, die auf dem Handy sind), darunter alle Apps.
 */
@Composable
internal fun GateAppPicker(selected: Set<String>, title: String, onDone: (Set<String>) -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val installed = LocalInstalledApps.current
    var chosen by remember { mutableStateOf(selected) }
    var suggestions by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    LaunchedEffect(installed) {
        suggestions = withContext(Dispatchers.Default) {
            installed.filter { a ->
                a.packageName != context.packageName &&
                    AppGroups.classify(a.packageName, a.label, ScreenTime.category(context, a.packageName)) in setOf("social", "video", "games")
            }
        }
    }
    BackHandler { onDone(chosen) }
    // Die Leiste der App weicht, solange die Auswahl offen ist
    de.funke.app.ui.components.HideNavBarWhile(true)
    AmbientBackground {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(start = Space.page, end = Space.page, top = Space.s3, bottom = Space.s4)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.headlineSmall, color = p.text, modifier = Modifier.weight(1f))
                CloseButton(onClick = { onDone(chosen) })
            }
            Spacer(Modifier.height(Space.s4))
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth().clip(Shapes.card).background(p.track),
                contentPadding = PaddingValues(vertical = Space.s2),
            ) {
                if (suggestions.isNotEmpty()) {
                    item { SectionLabel(tr("Vorschläge", "Suggestions"), Modifier.padding(start = Space.s4, top = Space.s2, bottom = Space.s1)) }
                    items(suggestions, key = { "s:" + it.packageName }) { app ->
                        PickRow(app, app.packageName in chosen) { chosen = if (app.packageName in chosen) chosen - app.packageName else chosen + app.packageName }
                    }
                    item { SectionLabel(tr("Alle Apps", "All apps"), Modifier.padding(start = Space.s4, top = Space.s4, bottom = Space.s1)) }
                }
                items(installed.filter { it.packageName != context.packageName }, key = { it.packageName }) { app ->
                    PickRow(app, app.packageName in chosen) { chosen = if (app.packageName in chosen) chosen - app.packageName else chosen + app.packageName }
                }
            }
            Spacer(Modifier.height(Space.s4))
            PrimaryButton(
                when (chosen.size) {
                    0 -> tr("Fertig", "Done")
                    1 -> tr("Fertig (1 App)", "Done (1 app)")
                    else -> tr("Fertig (${chosen.size} Apps)", "Done (${chosen.size} apps)")
                },
                onClick = { onDone(chosen) },
            )
        }
    }
}

@Composable
private fun PickRow(app: InstalledApp, on: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .bouncyClickable(shape = Shapes.md, role = Role.Checkbox, onClick = onClick)
            .padding(horizontal = Space.s4, vertical = Space.s2)
            .semantics { role = Role.Checkbox; stateDescription = if (on) tr("Ausgewählt", "Selected") else "" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(app, app.label, size = 40.dp)
        Spacer(Modifier.width(Space.s4))
        Text(app.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1)
        SelectionDot(on)
    }
}
