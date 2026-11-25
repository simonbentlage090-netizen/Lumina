package de.funke.app.ui

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.saveable.rememberSaveable
import de.funke.app.ui.components.StartIntro
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.lock.SessionLock
import de.funke.app.ui.screens.FocusScreen
import de.funke.app.ui.screens.LockSetupSheet
import de.funke.app.ui.screens.MainScreen
import de.funke.app.ui.screens.ModeEditor
import de.funke.app.ui.screens.ResultScreen
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.FunkeTheme
import de.funke.app.ui.theme.Palettes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FunkeApp() {
    val context = LocalContext.current
    val state = remember { AppState(context.getSharedPreferences("funke", Context.MODE_PRIVATE), context.applicationContext) }

    // Die Liste der installierten Apps wird im Hintergrund geladen.
    var apps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.Default) { loadInstalledApps(context) }
    }

    // Bei jeder Rückkehr in die App prüfen, ob die Sperre (Bedienungshilfe) eingeschaltet ist.
    @Suppress("DEPRECATION")
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                state.updateLockEnabled(SessionLock.isServiceEnabled(context))
                // neuer Tag? verpasste Tage mit Streak-Eis überbrücken, Widgets und Erinnerung aktuell halten
                state.applyFreezes()
                de.funke.app.widget.Widgets.refresh(context)
                de.funke.app.notify.Reminders.schedule(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val haptics = rememberHaptics()
    val sounds = de.funke.app.ui.rememberSounds()
    SideEffect {
        haptics.level = state.hapticLevel
        haptics.mode = state.hapticMode
        sounds.enabled = state.soundOn
    }

    // Hell/Dunkel auch dem System melden (ab Android 12). Dann hat schon der Startbildschirm
    // beim nächsten Öffnen die richtige Farbe und nichts blitzt hell oder dunkel auf.
    // Bei "System" bekommt die App den Modus des Geräts (MODE_NIGHT_AUTO), sonst fest Hell oder Dunkel.
    LaunchedEffect(state.themeMode, state.darkMode) {
        if (Build.VERSION.SDK_INT >= 31) {
            runCatching {
                context.getSystemService(UiModeManager::class.java)?.setApplicationNightMode(
                    when (state.themeMode) {
                        ThemeMode.System -> UiModeManager.MODE_NIGHT_AUTO
                        else -> if (state.darkMode) UiModeManager.MODE_NIGHT_YES else UiModeManager.MODE_NIGHT_NO
                    }
                )
            }
        }
    }
    // Folgt dem Gerät, wenn "System" gewählt ist (auch wenn es sich abends selbst umstellt).
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    LaunchedEffect(systemDark, state.themeMode) {
        if (state.themeMode == ThemeMode.System) state.syncDarkMode(systemDark)
    }

    // Start-Animation: nur beim echten Öffnen (nicht nach Drehen o. Ä.) und nicht mitten in einer Session
    // Für eine Denkpause geöffnet: keine Start-Animation, die Aufgabe kommt sofort.
    val playIntro = rememberSaveable { state.startAnimation && state.screen == Screen.Main && DeepLink.gate == null && DeepLink.reminder == null }
    var introDone by rememberSaveable { mutableStateOf(!playIntro) }

    FunkeTheme(
        palette = Palettes.byId(state.themeId),
        style = state.uiStyle,
        pattern = state.backgroundPattern,
        motion = state.motionMode,
        font = state.appFont,
    ) {
        CompositionLocalProvider(
            LocalInstalledApps provides apps,
            LocalTimerStyle provides state.timerStyle,
            LocalRingStyle provides state.ringStyle,
            LocalSidekick provides state.sidekick,
            de.funke.app.ui.components.LocalFilamentStyle provides state.filamentStyle,
            LocalHaptics provides haptics,
            de.funke.app.ui.LocalSounds provides sounds,
            LocalHapticFeedback provides haptics,
            de.funke.app.ui.challenge.LocalChallengeSettings provides state.challengeSettings,
        ) {
            Box(Modifier.fillMaxSize()) {
                FunkeAppContent(state)
                if (!introDone) StartIntro(dark = state.darkMode, onDone = { introDone = true })
            }
        }
    }
}

@Composable
private fun FunkeAppContent(state: AppState) {
    val context = LocalContext.current
    val reduced = de.funke.app.ui.theme.reducedMotion
    // Hinweis zur Bedienungshilfe (mit Zustimmung), bevor die Einstellungen aufgehen
    var a11yGuide by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Funke.colors.bg)
    ) {
        AnimatedContent(
            targetState = state.screen,
            transitionSpec = {
                when {
                    reduced -> fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
                    // Eintauchen: Die Startseite weicht nach vorn aus, die Session kommt aus der Tiefe.
                    targetState == Screen.Focus ->
                        (fadeIn(tween(Motion.Medium, delayMillis = 60, easing = Motion.EaseOut)) +
                            scaleIn(tween(Motion.Slow, delayMillis = 60, easing = Motion.EaseOut), initialScale = 0.94f)) togetherWith
                            (fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)) + scaleOut(tween(Motion.Base, easing = Motion.EaseOut), targetScale = 1.04f))
                    else ->
                        (fadeIn(tween(Motion.Medium, delayMillis = 40, easing = Motion.EaseOut)) +
                            scaleIn(tween(Motion.Slow, delayMillis = 40, easing = Motion.EaseOut), initialScale = 0.97f)) togetherWith
                            fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
                }
            },
            label = "screen",
        ) { screen ->
            when (screen) {
                Screen.Main -> MainScreen(state)
                Screen.Focus -> FocusScreen(
                    mode = state.selectedMode,
                    totalMinutes = state.sessionMinutes,
                    endAtMillis = state.sessionEndAt,
                    onEnd = state::endSession,
                    cancelStyle = state.cancelStyle,
                    quitConfirm = state.quitConfirm,
                    readingLumi = state.readingLumi,
                    onOpenNotes = { state.openNoteEditor(null, inSession = true) },
                )
                Screen.Result -> state.result?.let { result ->
                    ResultScreen(
                        result = result,
                        streak = state.streak,
                        todayMinutes = state.todayMinutes,
                        dailyGoal = state.dailyGoal,
                        onDone = state::backToMain,
                        notesCount = state.notesOfSession(state.lastSessionKey).size,
                        onNotes = { state.notesVisible = true },
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = state.lockSetupVisible,
            enter = Motion.sheetEnter(reduced),
            exit = Motion.sheetExit(reduced),
        ) {
            LockSetupSheet(
                ready = state.lockEnabled,
                // Erst der Hinweis mit Zustimmung, dann die Einstellungen
                onOpenSettings = { a11yGuide = true },
                onOpenAppInfo = { SessionLock.openAppInfo(context) },
                onStart = state::startSession,
                onStartWithoutLock = state::startWithoutLock,
                onDismiss = state::closeLockSetup,
            )
        }

        if (a11yGuide) de.funke.app.ui.screens.AccessibilityGuide(onDismiss = { a11yGuide = false })

        // Buch lesen (seit 13.4): liegt über den Reitern, Notizen und Rechner öffnen sich darüber
        var shownBook by remember { mutableStateOf<String?>(null) }
        state.openBookId?.let { shownBook = it }
        AnimatedVisibility(
            visible = state.openBookId != null && state.screen == Screen.Main,
            enter = if (reduced) fadeIn(tween(Motion.Base)) else fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) + scaleIn(tween(Motion.Slow, easing = Motion.EaseOut), initialScale = 0.97f),
            exit = fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)),
        ) {
            val id = shownBook
            if (id != null) key(id) {
                de.funke.app.ui.screens.BookReader(state, id, onClose = state::closeBook)
            }
        }

        // Notizen-Übersicht
        AnimatedVisibility(
            visible = state.notesVisible,
            enter = Motion.sheetEnter(reduced),
            exit = Motion.sheetExit(reduced),
        ) {
            de.funke.app.ui.screens.NotesScreen(state, onClose = { state.notesVisible = false })
        }

        // Notiz im Vollbild (aus der Session oder aus der Übersicht)
        AnimatedVisibility(
            visible = state.noteEditor != null,
            enter = Motion.sheetEnter(reduced),
            exit = Motion.sheetExit(reduced),
        ) {
            val req = state.noteEditor
            if (req != null) key(req.nonce) {
                de.funke.app.ui.screens.NoteEditor(state, req, onClose = state::closeNoteEditor)
            }
        }

        // Rettungs-Eis: Lumi hat einen langen Streak verschenkt eingefroren
        AnimatedVisibility(
            visible = state.giftNotice && state.onboarded && state.screen == Screen.Main,
            enter = fadeIn(tween(0)),
            exit = fadeOut(tween(Motion.Fast)),
        ) {
            de.funke.app.ui.screens.GiftDialog(streak = state.streak, onClose = state::dismissGiftNotice)
        }

        // Einrichtung beim ersten Start (liegt über allem)
        AnimatedVisibility(
            visible = !state.onboarded,
            enter = fadeIn(tween(Motion.Slow, easing = Motion.EaseOut)),
            exit = fadeOut(tween(Motion.Slow, easing = Motion.EaseOut)),
        ) {
            de.funke.app.ui.screens.Onboarding(state)
        }

        AnimatedVisibility(
            visible = state.editorVisible,
            enter = Motion.sheetEnter(reduced),
            exit = Motion.sheetExit(reduced),
        ) {
            key(state.editorNonce) {
                ModeEditor(
                    mode = state.editorMode,
                    onSave = { name, packages -> state.saveMode(state.editorMode?.id, name, packages) },
                    onDelete = { state.editorMode?.id?.let { state.deleteMode(it) } },
                    onClose = state::closeEditor,
                )
            }
        }

        // Denkpause vor einer geschützten App (liegt über allem). Während einer Lese-Session gilt die Sperre.
        val gate = DeepLink.gate
        val blocked = state.screen == Screen.Focus || !state.onboarded
        LaunchedEffect(gate, blocked) { if (gate != null && blocked) DeepLink.gate = null }
        var shownGate by remember { mutableStateOf<GateRequest?>(null) }
        if (gate != null && !blocked) shownGate = gate
        AnimatedVisibility(
            visible = gate != null && !blocked,
            enter = fadeIn(tween(Motion.Fast)),
            exit = fadeOut(tween(Motion.Fast)),
        ) {
            val req = shownGate
            if (req != null) key(req.at) {
                de.funke.app.ui.screens.GateScreen(req, onDone = { DeepLink.gate = null })
            }
        }

        // Erinnerung als Lumi-Bildschirm (seit 13.4). Nicht während einer Lesezeit, im Buch, in einer Denkpause oder vor dem
        // Intro: dann kommt sie stattdessen als Mitteilung, damit nichts verloren geht.
        val reminder = DeepLink.reminder
        val reminderBlocked = state.screen == Screen.Focus || !state.onboarded || gate != null || state.openBookId != null
        val appContext = LocalContext.current
        LaunchedEffect(reminder, reminderBlocked) {
            if (reminder != null && reminderBlocked) {
                DeepLink.reminder = null
                val kind = reminder.shown.kind
                if (state.openBookId != null && (kind == de.funke.app.notify.ReminderKind.Read || kind == de.funke.app.notify.ReminderKind.Streak)) {
                    // Liest gerade im Buch: "Zeit zum Lesen" wäre hier nur störend
                    de.funke.app.notify.Reminders.removePending(appContext, reminder.shown.id)
                } else {
                    de.funke.app.notify.Reminders.deliver(appContext, reminder.shown, de.funke.app.notify.Delivery.Notify)
                }
            }
        }
        var shownReminder by remember { mutableStateOf<ReminderRequest?>(null) }
        if (reminder != null && !reminderBlocked) shownReminder = reminder
        AnimatedVisibility(
            visible = reminder != null && !reminderBlocked,
            enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)),
            exit = fadeOut(tween(Motion.Fast)),
        ) {
            val req = shownReminder
            if (req != null) key(req.at) {
                de.funke.app.ui.screens.ReminderScreen(req, onDone = { DeepLink.reminder = null })
            }
        }
    }
}
