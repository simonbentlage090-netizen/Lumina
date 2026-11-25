package de.funke.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.R
import de.funke.app.lock.SessionLock
import de.funke.app.notify.Reminders
import de.funke.app.ui.AppState
import de.funke.app.ui.GoalLevel
import de.funke.app.ui.StreakGoal
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.LipBox
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiCue
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.ProgressBar
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.lipColors
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import de.funke.app.usage.ScreenTime
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.surface
import de.funke.app.ui.theme.BackgroundPattern
import de.funke.app.ui.theme.Palettes
import de.funke.app.ui.theme.UiStyle
import kotlinx.coroutines.launch
import de.funke.app.ui.components.Unfold

/*
 * Einrichtung beim ersten Start (seit 10.0, nach dem Vorbild von Brilliant):
 * Lumi sagt Hallo (sein Licht geht an) → Erlaubnisse → Tagesziel → Streak-Ziel → Widget → Look → "Alles bereit".
 * Oben ein schmaler Fortschritt, unten immer ein großer Knopf. Lumi lässt sich überall antippen.
 */

private const val STEPS = 7

@Composable
fun Onboarding(state: AppState) {
    var step by remember { mutableIntStateOf(0) }
    BackHandler(enabled = step > 0) { step-- }
    val still = reducedMotion
    AmbientBackground {
        Column(
            Modifier
                .fillMaxSize()
                // schluckt Berührungen, damit nichts dahinter angetippt wird
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            TopBar(step = step, onBack = { if (step > 0) step-- }, state = state)
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val dir = if (targetState > initialState) 1 else -1
                    if (still) fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
                    else (fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) + slideInHorizontally(tween(Motion.Slow, easing = Motion.EaseOut)) { dir * it / 8 }) togetherWith
                        (fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)) + slideOutHorizontally(tween(Motion.Base, easing = Motion.EaseOut)) { -dir * it / 12 })
                },
                label = "onboarding",
                modifier = Modifier.weight(1f),
            ) { s ->
                when (s) {
                    0 -> HelloStep(onNext = { step = 1 })
                    1 -> PermissionsStep(onNext = { step = 2 })
                    2 -> GoalStep(state, onNext = { step = 3 })
                    3 -> StreakGoalStep(state, onNext = { step = 4 })
                    4 -> WidgetStep(onNext = { step = 5 })
                    5 -> StyleStep(state, onNext = { step = 6 })
                    // Seit 12.0 ohne Rundgang: Die Reiter erklären sich beim ersten Öffnen selbst (siehe TabIntro).
                    else -> ReadyStep(onStart = state::finishOnboarding)
                }
            }
        }
    }
}

/** Oben: zurück, schmaler Fortschritt, rechts die Sprache (nur am Anfang). */
@Composable
private fun TopBar(step: Int, onBack: () -> Unit, state: AppState) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = Space.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(Sizes.touch), contentAlignment = Alignment.Center) {
            if (step > 0) {
                de.funke.app.ui.components.IconButton(tr("Zurück", "Back"), onClick = onBack) {
                    Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.iconLg))
                }
            }
        }
        Spacer(Modifier.width(Space.s2))
        ProgressBar((step + 1) / STEPS.toFloat(), modifier = Modifier.weight(1f), color = p.accent, height = 8.dp)
        Spacer(Modifier.width(Space.s2))
        // Rechts so breit wie nötig (vorher ein fester 72-dp-Kasten, in dem "EN" abgeschnitten wurde)
        Box(Modifier.widthIn(min = Sizes.touch).height(Sizes.touch), contentAlignment = Alignment.CenterEnd) {
            if (step == 0) LanguageSwitch(state)
        }
    }
}

/**
 * DE | EN als kleine Pille. Jede Hälfte hat eine feste Mindestbreite und die Pille so viel Platz,
 * wie sie braucht – auch bei großer Systemschrift wird nichts abgeschnitten.
 */
@Composable
private fun LanguageSwitch(state: AppState) {
    val p = Funke.colors
    Row(
        Modifier
            .clip(Shapes.pill)
            .background(p.track)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        de.funke.app.ui.AppLanguage.entries.forEach { lang ->
            val on = state.language == lang
            val bg by androidx.compose.animation.animateColorAsState(
                if (on) p.accent else Color.Transparent,
                Motion.fade(Motion.Fast),
                label = "lang",
            )
            Box(
                Modifier
                    .widthIn(min = 40.dp)
                    .height(30.dp)
                    .clip(Shapes.pill)
                    .background(bg)
                    .clickable(role = androidx.compose.ui.semantics.Role.RadioButton) { state.updateLanguage(lang) }
                    .semantics { selected = on }
                    .padding(horizontal = Space.s2),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (lang == de.funke.app.ui.AppLanguage.German) "DE" else "EN",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (on) p.onAccent else p.textSecondary,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

/** Gerüst eines Schritts: Inhalt (scrollbar, mittig), unten der Knopf. */
@Composable
private fun StepFrame(
    button: String,
    onButton: () -> Unit,
    buttonIcon: Int? = null,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.page, vertical = Space.s4),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
        Column(Modifier.fillMaxWidth().padding(start = Space.page, end = Space.page, top = Space.s2, bottom = Space.s4)) {
            LipButton(button, onClick = onButton, modifier = Modifier.fillMaxWidth(), icon = buttonIcon)
            footer?.invoke(this)
        }
    }
}

@Composable
private fun Title(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.headlineMedium, color = Funke.colors.text, textAlign = TextAlign.Center, modifier = modifier)
}

// ---------------------------------------------------------------------------------------------
// 1. Hallo
// ---------------------------------------------------------------------------------------------

/**
 * Lumi mit Brille. Sein Licht flackert an (an, kurz aus, ganz an), dann leuchtet er einmal hell auf
 * und begrüßt dich. Bei "Bewegung reduzieren" ist das Licht gleich an.
 */
@Composable
private fun HelloStep(onNext: () -> Unit) {
    val p = Funke.colors
    val still = reducedMotion
    val power = remember { Animatable(if (still) 1f else 0f) }
    val words = remember { Animatable(if (still) 1f else 0f) }
    var cue by remember { mutableIntStateOf(0) }
    val haptics = de.funke.app.ui.LocalHaptics.current
    LaunchedEffect(Unit) {
        if (still) return@LaunchedEffect
        // kurz warten (die Start-Animation der App läuft noch), dann flackert das Licht an
        delay(900)
        power.animateTo(0.9f, tween(70))
        power.animateTo(0.1f, tween(120))
        delay(80)
        haptics?.tick()
        power.animateTo(1f, tween(320, easing = Motion.EaseOut))
        cue++
        words.animateTo(1f, tween(Motion.Slow, easing = Motion.EaseOut))
    }
    val hour = remember { java.time.LocalTime.now().hour }
    val greeting = when (hour) {
        in 5..10 -> tr("Guten Morgen!", "Good morning!")
        in 18..23 -> tr("Guten Abend!", "Good evening!")
        else -> tr("Hi!", "Hi!")
    }
    // Text erscheint erst, wenn Lumi leuchtet: 12 dp von unten, Ease-out
    val textIn = Modifier.graphicsLayer {
        alpha = words.value
        translationY = (1f - words.value) * 12.dp.toPx()
    }
    StepFrame(button = tr("Hallo, Lumi!", "Hi, Lumi!"), onButton = onNext) {
        Spacer(Modifier.weight(1f))
        Lumi2D(
            LumiPose.Reading,
            Modifier.size(240.dp),
            power = power.value,
            cue = cue,
            cueReaction = LumiCue.Glow,
        )
        Spacer(Modifier.height(Space.s5))
        Column(textIn, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(greeting, style = MaterialTheme.typography.titleMedium, color = p.accent)
            Spacer(Modifier.height(Space.s1))
            Title(tr("Ich bin Lumi.", "I'm Lumi."))
            Spacer(Modifier.height(Space.s3))
            Text(
                tr("Ich bin bei dir, wenn du dich konzentrierst. Jeden Tag ein bisschen.", "I'm with you while you focus. A little every day."),
                style = MaterialTheme.typography.bodyLarge,
                color = p.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

// ---------------------------------------------------------------------------------------------
// 2. Erlaubnisse
// ---------------------------------------------------------------------------------------------

@Composable
private fun PermissionsStep(onNext: () -> Unit) {
    val context = LocalContext.current
    fun notifOk() = Build.VERSION.SDK_INT < 33 ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    var notif by remember { mutableStateOf(notifOk()) }
    var usage by remember { mutableStateOf(ScreenTime.hasAccess(context)) }
    var lock by remember { mutableStateOf(SessionLock.isServiceEnabled(context)) }
    var lockTried by remember { mutableStateOf(false) }
    var lockGuide by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notif = granted || notifOk()
        if (notif) Reminders.schedule(context)
    }
    // Zurück aus den Einstellungen: Stand neu prüfen
    val lifecycle = androidx.compose.ui.platform.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                notif = notifOk()
                usage = ScreenTime.hasAccess(context)
                lock = SessionLock.isServiceEnabled(context)
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
    StepFrame(
        button = tr("Weiter", "Next"),
        onButton = {
            Reminders.schedule(context)
            onNext()
        },
    ) {
        Spacer(Modifier.height(Space.s4))
        Lumi2D(LumiPose.Idle, Modifier.size(132.dp), hat = true, hatTassel = Funke.colors.accent)
        Spacer(Modifier.height(Space.s4))
        Title(tr("Ein paar Erlaubnisse", "A few permissions"))
        Spacer(Modifier.height(Space.s6))
        PermissionRow(
            icon = R.drawable.ic_bell,
            title = tr("Benachrichtigungen", "Notifications"),
            text = tr("Erinnerungen am Abend", "Evening reminders"),
            granted = notif,
            onClick = {
                if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                else { notif = true; Reminders.schedule(context) }
            },
        )
        Spacer(Modifier.height(Space.s3))
        PermissionRow(
            icon = R.drawable.ic_nav_screen,
            title = tr("Bildschirmzeit", "Screen time"),
            text = tr("Für deine Bildschirmzeit im Fortschritt", "For your screen time in Progress"),
            granted = usage,
            onClick = { ScreenTime.openSettings(context) },
        )
        Spacer(Modifier.height(Space.s3))
        PermissionRow(
            icon = R.drawable.ic_lock,
            title = tr("Sperre & Denkpause", "Lock & think break"),
            text = tr("Sperrt Ablenkungen in der Fokuszeit und zeigt die Denkpause", "Blocks distractions while you focus and shows think breaks"),
            granted = lock,
            onClick = {
                lockTried = true
                lockGuide = true
            },
        )
        // Direkt zum Schalter springen erlaubt Android nur dem System. Deshalb steht der Weg hier,
        // sobald man einmal in den Einstellungen war und die Sperre noch nicht an ist.
        Unfold(
            visible = lockTried && !lock,
            enter = androidx.compose.animation.expandVertically(tween(Motion.Base, easing = Motion.EaseOut), clip = false) + androidx.compose.animation.fadeIn(tween(Motion.Base)),
            exit = androidx.compose.animation.shrinkVertically(tween(Motion.Fast, easing = Motion.EaseOut), clip = false) + androidx.compose.animation.fadeOut(tween(Motion.Fast)),
        ) {
            LockPathHint(onRestricted = { SessionLock.openAppInfo(context) })
        }
    }
    if (lockGuide) AccessibilityGuide(onDismiss = { lockGuide = false })
}

/** Der Weg zu "Lumina Sperre" in den Einstellungen, plus Hilfe, falls der Schalter ausgegraut ist. */
@Composable
private fun LockPathHint(onRestricted: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(top = Space.s3, start = Space.s4, end = Space.s4)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_arrow_right), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconSm))
            Spacer(Modifier.width(Space.s2))
            Text(
                tr("Bedienungshilfe → ", "Accessibility → ") + SessionLock.accessibilityPath(context),
                style = MaterialTheme.typography.bodySmall,
                color = p.textSecondary,
            )
        }
        Spacer(Modifier.height(Space.s1))
        Text(
            tr("Schalter ausgegraut? „Eingeschränkte Einstellungen zulassen“", "Switch greyed out? “Allow restricted settings”"),
            style = MaterialTheme.typography.labelMedium,
            color = p.accent,
            modifier = Modifier
                .padding(start = Sizes.iconSm)
                .bouncyClickable(shape = Shapes.pill, onClick = onRestricted)
                .padding(Space.s2),
        )
    }
}

/** Erlaubnis als Zeile: Symbol, Name, kurzer Zweck, rechts "Erlauben" oder ein grüner Haken. */
@Composable
private fun PermissionRow(icon: Int, title: String, text: String, granted: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    LipBox(
        colors = p.lipColors(LipTone.Neutral),
        modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp),
        shape = Shapes.md,
        onClick = if (granted) null else onClick,
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.padding(horizontal = Space.s4, vertical = Space.s3), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(Sizes.tile).clip(Shapes.sm).background(if (granted) p.success.copy(alpha = 0.16f) else p.track),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = if (granted) p.success else p.textSecondary, modifier = Modifier.size(Sizes.icon))
            }
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = p.text)
                Text(text, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
            }
            Spacer(Modifier.width(Space.s3))
            Crossfade(targetState = granted, animationSpec = tween(Motion.Base), label = "perm") { ok ->
                if (ok) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(p.success.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.success, modifier = Modifier.size(Sizes.iconSm))
                    }
                } else {
                    Text(
                        tr("Erlauben", "Allow"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.onAccent,
                        modifier = Modifier
                            .clip(Shapes.pill)
                            .background(p.accent)
                            .padding(horizontal = Space.s4, vertical = Space.s2),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// 3. Tagesziel
// ---------------------------------------------------------------------------------------------

@Composable
private fun GoalStep(state: AppState, onNext: () -> Unit) {
    val level = state.goalLevel
    StepFrame(button = tr("Weiter", "Next"), onButton = onNext) {
        Spacer(Modifier.height(Space.s2))
        // Lumi freut sich mehr, je höher das Ziel
        TierLumi(
            when (level) {
                GoalLevel.Easy -> R.drawable.lumi_w_read
                GoalLevel.Medium -> R.drawable.lumi_w_wink
                GoalLevel.Hard -> R.drawable.lumi_w_cheer
                GoalLevel.Extreme -> R.drawable.lumi_w_zap
            }
        )
        Spacer(Modifier.height(Space.s3))
        Title(tr("Wie viel Fokuszeit am Tag?", "How much focus time a day?"))
        Spacer(Modifier.height(Space.s6))
        GoalLevelPicker(selected = level, onSelect = { state.updateDailyGoal(it.minutes) })
    }
}

/**
 * Lumi über einer Auswahl: wechselt weich das Bild, je nachdem, was gewählt ist (er freut sich mehr, je höher
 * das Ziel). Antippen: ein kleiner Hüpfer wie überall in der App. Feste Höhe, damit unten nichts springt.
 */
@Composable
private fun TierLumi(res: Int) {
    val hop = remember { Animatable(0f) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val still = reducedMotion
    Box(Modifier.height(128.dp), contentAlignment = Alignment.Center) {
        Crossfade(targetState = res, animationSpec = tween(Motion.Medium), label = "tierLumi") { r ->
            Image(
                painterResource(r),
                contentDescription = "Lumi",
                modifier = Modifier
                    .height(128.dp)
                    .graphicsLayer {
                        translationY = -16.dp.toPx() * hop.value
                        rotationZ = 4f * hop.value
                    }
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        if (still) return@clickable
                        scope.launch {
                            hop.animateTo(1f, tween(180, easing = Motion.EaseOut))
                            hop.animateTo(0f, tween(320, easing = Motion.EaseOut))
                        }
                    },
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// 4. Streak-Ziel
// ---------------------------------------------------------------------------------------------

/**
 * Seit 15.0 ohne den großen Kalender (bei 60 Tagen wirkte er erschlagend, bei 7 mager, und die Monatsnamen
 * standen eingequetscht zwischen den Reihen). Jetzt wie beim Tagesziel: Lumi freut sich mit, darunter ein
 * ermutigender Satz zur gewählten Stufe und das Datum, an dem das Ziel geschafft ist. Die Stufen als ruhige Liste.
 */
@Composable
private fun StreakGoalStep(state: AppState, onNext: () -> Unit) {
    val p = Funke.colors
    var goal by remember { mutableIntStateOf(if (state.streakGoal > 0) state.streakGoal else StreakGoal.Month.days) }
    val tier = StreakGoal.forDays(goal) ?: StreakGoal.Month
    StepFrame(button = tr("Ziel setzen", "Set goal"), onButton = { state.updateStreakGoal(goal); onNext() }) {
        Spacer(Modifier.height(Space.s2))
        TierLumi(
            when (tier) {
                StreakGoal.Week -> R.drawable.lumi_w_hello
                StreakGoal.TwoWeeks -> R.drawable.lumi_w_proud
                StreakGoal.Month -> R.drawable.lumi_w_cheer
                StreakGoal.Habit -> R.drawable.lumi_w_fire
            }
        )
        Spacer(Modifier.height(Space.s3))
        Title(tr("Wie viele Tage am Stück?", "How many days in a row?"))
        Spacer(Modifier.height(Space.s2))
        AnimatedContent(
            targetState = tier,
            transitionSpec = { fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast)) },
            label = "streakTier",
        ) { t ->
            val date = formatGoalDate(streakGoalDate(t.days, state.streak, state.streakDoneToday))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    t.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = p.textSecondary,
                    textAlign = TextAlign.Center,
                    minLines = 2,
                )
                Text(
                    tr("Geschafft am $date", "Done on $date"),
                    style = MaterialTheme.typography.labelMedium,
                    color = p.accent,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Spacer(Modifier.height(Space.s5))
        StreakGoalList(selected = goal, onSelect = { goal = it })
    }
}

// ---------------------------------------------------------------------------------------------
// 5. Widget
// ---------------------------------------------------------------------------------------------

/**
 * Lumi auf den Startbildschirm holen. Wo der Launcher es erlaubt, legt Android das Widget nach einer
 * Rückfrage direkt ab (requestPinAppWidget). Sonst steht kurz da, wie es von Hand geht.
 */
@Composable
private fun WidgetStep(onNext: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val manager = remember { android.appwidget.AppWidgetManager.getInstance(context) }
    val canPin = remember { Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported }
    var asked by remember { mutableStateOf(false) }
    StepFrame(
        button = if (asked || !canPin) tr("Weiter", "Next") else tr("Widget hinzufügen", "Add widget"),
        onButton = {
            if (!asked && canPin) {
                asked = true
                runCatching {
                    manager.requestPinAppWidget(android.content.ComponentName(context, de.funke.app.widget.LumiWidget::class.java), null, null)
                }
            } else {
                onNext()
            }
        },
        footer = {
            if (!asked && canPin) {
                Spacer(Modifier.height(Space.s1))
                TextAction(tr("Später", "Later"), onClick = onNext, color = p.textTertiary, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        },
    ) {
        Spacer(Modifier.weight(1f))
        WidgetMock()
        Spacer(Modifier.height(Space.s6))
        Title(tr("Lumi auf deinem Startbildschirm", "Lumi on your home screen"))
        if (!canPin) {
            // Nur wenn Android das Widget nicht selbst ablegen kann: wie es von Hand geht
            Spacer(Modifier.height(Space.s3))
            Text(
                tr("Halte deinen Startbildschirm gedrückt, tippe auf „Widgets“ und such „Lumina“.", "Press and hold your home screen, tap \"Widgets\" and look for \"Lumina\"."),
                style = MaterialTheme.typography.bodyLarge,
                color = p.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        if (asked) {
            Spacer(Modifier.height(Space.s3))
            Text(
                tr("Bestätige kurz die Rückfrage deines Handys.", "Just confirm your phone's prompt."),
                style = MaterialTheme.typography.bodySmall,
                color = p.textTertiary,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

/** Kleine Nachbildung des Lumi-Widgets, genau wie es seit 12.0 auf dem Startbildschirm aussieht. */
@Composable
private fun WidgetMock() {
    val p = Funke.colors
    val glow = p.accent.copy(alpha = if (p.isDark) 0.20f else 0.30f)
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
    Row(
        Modifier
            .fillMaxWidth(0.9f)
            .height(160.dp)
            .shadow(10.dp, shape, ambientColor = Color.Black.copy(alpha = 0.10f), spotColor = Color.Black.copy(alpha = 0.10f))
            .clip(shape)
            .drawBehind {
                drawRect(p.surface)
                drawRect(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        listOf(glow, Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.55f),
                        radius = size.minDimension * 0.95f,
                    )
                )
            }
            .border(1.dp, p.divider.copy(alpha = 0.6f), shape)
            .padding(start = 8.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Image(
            painterResource(R.drawable.lumi_w_wink),
            contentDescription = null,
            modifier = Modifier.weight(0.4f).fillMaxSize(),
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(0.6f).fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text(
                    tr("Streak gesichert!", "Streak saved!"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = p.text,
                    maxLines = 2,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { ProgressBar(0.4f, color = p.accent, height = 6.dp) }
                Spacer(Modifier.width(8.dp))
                Text(tr("12/30 Min", "12/30 min"), style = MaterialTheme.typography.labelMedium, color = p.textSecondary)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.fillMaxWidth().height(44.dp).clip(CircleShape).background(p.accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    tr("Weiter", "Continue"),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = p.onAccent,
                    maxLines = 1,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// 6. Look
// ---------------------------------------------------------------------------------------------

/**
 * Zwei Looks zur Wahl, jeder als kleine echte Vorschau (dieselben Bausteine wie in der App, in seinem Stil):
 * Indigo-Glas (Standard) oder Cartoon in warmen Farben. Antippen schaltet die ganze App sofort um, Lumi in der
 * Vorschau hüpft dazu. Hell oder dunkel folgt dem Handy. Später jederzeit in den Einstellungen änderbar.
 */
@Composable
private fun StyleStep(state: AppState, onNext: () -> Unit) {
    val haptics = de.funke.app.ui.LocalHaptics.current
    var cartoonCue by remember { mutableIntStateOf(0) }
    var glassCue by remember { mutableIntStateOf(0) }
    fun choose(style: UiStyle) {
        if (style == UiStyle.Cartoon) cartoonCue++ else glassCue++
        if (state.uiStyle == style) return
        haptics?.tick()
        state.changeUiStyle(style)
        state.setTheme(Palettes.defaultFor(style, state.darkMode).id)
    }
    StepFrame(button = tr("Weiter", "Next"), onButton = onNext) {
        Spacer(Modifier.weight(1f))
        Title(tr("Welcher Look gefällt dir?", "Which look do you like?"))
        Spacer(Modifier.height(Space.s6))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            LookCard(
                name = tr("Indigo-Glas", "Indigo glass"),
                style = UiStyle.Glass,
                palette = Palettes.defaultFor(UiStyle.Glass, state.darkMode),
                pattern = state.backgroundPattern,
                selected = state.uiStyle == UiStyle.Glass,
                cue = glassCue,
                onClick = { choose(UiStyle.Glass) },
                modifier = Modifier.weight(1f).reveal(40),
            )
            LookCard(
                name = "Cartoon",
                style = UiStyle.Cartoon,
                palette = Palettes.defaultFor(UiStyle.Cartoon, state.darkMode),
                pattern = state.backgroundPattern,
                selected = state.uiStyle == UiStyle.Cartoon,
                cue = cartoonCue,
                onClick = { choose(UiStyle.Cartoon) },
                modifier = Modifier.weight(1f).reveal(100),
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

/**
 * Eine Wahl: die Vorschau in einem Rahmen, darunter der Name. Gewählt: Ring in der Akzentfarbe mit etwas
 * Abstand (wie bei der Wahl eines Hintergrundbilds) und ein Haken, der kurz aufspringt.
 */
@Composable
private fun LookCard(
    name: String,
    style: UiStyle,
    palette: de.funke.app.ui.theme.Palette,
    pattern: BackgroundPattern,
    selected: Boolean,
    cue: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = Funke.colors
    val ring by androidx.compose.animation.animateColorAsState(
        if (selected) p.accent else p.divider,
        Motion.fade(Motion.Base),
        label = "lookRing",
    )
    val outer = RoundedCornerShape(30.dp)
    val inner = RoundedCornerShape(24.dp)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .bouncyClickable(pressedScale = 0.97f, role = Role.RadioButton, onClick = onClick)
                .semantics {
                    this.selected = selected
                    contentDescription = name
                },
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .border(if (selected) 3.dp else 1.5.dp, ring, outer)
                    .padding(6.dp)
                    .aspectRatio(0.66f)
                    .clip(inner)
                    .border(1.dp, palette.glassAlpha(if (palette.isDark) 0.10f else 0.08f), inner),
            ) {
                LookPreview(style, palette, pattern, cue)
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = scaleIn(tween(Motion.Base, easing = Motion.Bob), initialScale = 0.4f) + fadeIn(tween(Motion.Fast)),
                exit = androidx.compose.animation.scaleOut(tween(Motion.Fast), targetScale = 0.6f) + fadeOut(tween(Motion.Fast)),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = Space.s4, end = Space.s4),
            ) {
                Box(
                    Modifier.size(26.dp).clip(CircleShape).background(p.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.onAccent, modifier = Modifier.size(Sizes.iconSm))
                }
            }
        }
        Spacer(Modifier.height(Space.s3))
        Text(
            name,
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) p.text else p.textSecondary,
        )
    }
}

/** Ein Stück App im jeweiligen Look: Hintergrund, Lumi, eine Karte mit Zeit und der große Knopf. */
@Composable
private fun LookPreview(style: UiStyle, palette: de.funke.app.ui.theme.Palette, pattern: BackgroundPattern, cue: Int) {
    de.funke.app.ui.theme.PreviewTheme(palette, style, pattern) {
        AmbientBackground {
            androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize().padding(Space.s3)) {
                val lumi = (maxHeight - 112.dp).coerceIn(48.dp, 88.dp)
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.weight(1f))
                    Lumi2D(LumiPose.Idle, Modifier.size(lumi), tappable = false, cue = cue, cueReaction = LumiCue.Hop)
                    Spacer(Modifier.height(Space.s1))
                    Box(
                        Modifier
                            .surface(Shapes.md, de.funke.app.ui.theme.Elevation.Level1)
                            .padding(horizontal = Space.s4, vertical = 6.dp),
                    ) {
                        Text(
                            "25:00",
                            style = de.funke.app.ui.theme.NumberStyle.copy(fontSize = 22.sp, lineHeight = 28.sp, fontFeatureSettings = "tnum, ss03"),
                            color = Funke.colors.text,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    LipBox(
                        colors = Funke.colors.lipColors(LipTone.Accent),
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = Shapes.sm,
                        lip = 3.dp,
                    ) {
                        Text(tr("Fokus", "Focus"), style = MaterialTheme.typography.labelLarge, color = Funke.colors.onAccent, maxLines = 1)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// 7. Los
// ---------------------------------------------------------------------------------------------

/** Lumi freut sich, ein großer Knopf, und ein Satz, wie es losgeht. */
@Composable
private fun ReadyStep(onStart: () -> Unit) {
    val p = Funke.colors
    var cue by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { delay(400); cue++ }
    StepFrame(
        button = tr("Los geht's", "Let's go"),
        onButton = onStart,
    ) {
        Spacer(Modifier.weight(1f))
        Lumi2D(LumiPose.Happy, Modifier.size(220.dp), cue = cue, cueReaction = LumiCue.Hop)
        Spacer(Modifier.height(Space.s5))
        Title(tr("Alles bereit!", "All set!"))
        Spacer(Modifier.height(Space.s3))
        Text(
            tr("Auf der Startseite wählst du eine Zeit und hältst den Knopf gedrückt. Dann geht es los.",
                "On the home screen, pick a time and hold the button. Then off we go."),
            style = MaterialTheme.typography.bodyLarge,
            color = p.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
    }
}
