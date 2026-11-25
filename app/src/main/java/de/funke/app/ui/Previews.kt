package de.funke.app.ui

import de.funke.app.ui.theme.Space
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.screens.CancelChallenge
import de.funke.app.ui.screens.FocusContent
import de.funke.app.ui.screens.MainScreen
import de.funke.app.ui.screens.ModeEditor
import de.funke.app.ui.screens.ResultScreen
import de.funke.app.ui.screens.SettingsScreen
import de.funke.app.ui.theme.FunkeTheme
import de.funke.app.ui.theme.Palettes

// Lumina: Diese Previews zeigt Android Studio direkt neben dem Code an (Split-Ansicht).
// Mit dem "Interactive"-Knopf oben rechts im Preview-Fenster kann man sie anklicken
// und sieht die Animationen laufen.

private const val PHONE = "spec:width=411dp,height=891dp,dpi=420"

private val DemoMode = FocusMode("demo", "Lesen mit KI", listOf("com.google.android.apps.bard", "com.android.chrome"))

@Preview(name = "Hauptbildschirm", device = PHONE)
@Composable
private fun MainPreview() {
    FunkeTheme { MainScreen(AppState()) }
}

@Preview(name = "Hauptbildschirm · hell", device = PHONE)
@Composable
private fun MainLightPreview() {
    FunkeTheme(Palettes.Paper) { MainScreen(AppState()) }
}

@Preview(name = "Fokus", device = PHONE)
@Composable
private fun FocusPreview() {
    FunkeTheme {
        FocusContent(
            mode = DemoMode,
            installed = emptyList(),
            timerStyle = TimerStyleOption.Classic,
            remainingMs = 28 * 60_000L + 12_000L,
            totalMs = 45 * 60_000L,
            showChallenge = false,
            showQuit = false,
            onOpenApp = {},
            onSlideComplete = {},
            onKeepReading = {},
            onSolved = {},
            onQuitClick = {},
            onQuitConfirm = {},
            onQuitDismiss = {},
            animateIn = false,
        )
    }
}

@Preview(name = "Fokus · Ende", device = PHONE)
@Composable
private fun FocusFinishPreview() {
    FunkeTheme {
        FocusContent(
            mode = DemoMode,
            installed = emptyList(),
            timerStyle = TimerStyleOption.Classic,
            remainingMs = 0L,
            totalMs = 45 * 60_000L,
            showChallenge = false,
            showQuit = false,
            onOpenApp = {},
            onSlideComplete = {},
            onKeepReading = {},
            onSolved = {},
            onQuitClick = {},
            onQuitConfirm = {},
            onQuitDismiss = {},
            finishing = true,
            animateIn = false,
        )
    }
}

@Preview(name = "Fokus · Abbruch-Aufgabe", device = PHONE)
@Composable
private fun ChallengePreview() {
    FunkeTheme {
        FocusContent(
            mode = DefaultMode,
            installed = emptyList(),
            timerStyle = TimerStyleOption.Classic,
            remainingMs = 28 * 60_000L,
            totalMs = 45 * 60_000L,
            showChallenge = true,
            showQuit = false,
            onOpenApp = {},
            onSlideComplete = {},
            onKeepReading = {},
            onSolved = {},
            onQuitClick = {},
            onQuitConfirm = {},
            onQuitDismiss = {},
            animateIn = false,
        )
    }
}

@Preview(name = "Fokus · Beenden bestätigen", device = PHONE)
@Composable
private fun QuitPreview() {
    FunkeTheme {
        FocusContent(
            mode = DefaultMode,
            installed = emptyList(),
            timerStyle = TimerStyleOption.Classic,
            remainingMs = 28 * 60_000L,
            totalMs = 45 * 60_000L,
            showChallenge = false,
            showQuit = true,
            onOpenApp = {},
            onSlideComplete = {},
            onKeepReading = {},
            onSolved = {},
            onQuitClick = {},
            onQuitConfirm = {},
            onQuitDismiss = {},
            animateIn = false,
        )
    }
}

@Preview(name = "Abbruch-Blatt allein", device = PHONE)
@Composable
private fun ChallengeSheetPreview() {
    FunkeTheme {
        AmbientBackground {
            CancelChallenge(onKeepReading = {}, onSolved = {})
        }
    }
}

@Preview(name = "Einstellungen", device = PHONE)
@Composable
private fun SettingsPreview() {
    FunkeTheme { SettingsScreen(AppState()) }
}

@Preview(name = "Modus-Editor", device = PHONE)
@Composable
private fun EditorPreview() {
    FunkeTheme { ModeEditor(mode = DemoMode, onSave = { _, _ -> }, onDelete = {}, onClose = {}) }
}

@Preview(name = "Ergebnis · geschafft", device = PHONE)
@Composable
private fun ResultCompletedPreview() {
    FunkeTheme {
        ResultScreen(
            result = SessionResult(45, 45, ResultKind.Completed, streakIncreased = true, goalReached = true),
            streak = 13,
            todayMinutes = 45,
            dailyGoal = 30,
            onDone = {},
        )
    }
}

@Preview(name = "Ergebnis · abgebrochen", device = PHONE)
@Composable
private fun ResultAbandonedPreview() {
    FunkeTheme {
        ResultScreen(
            result = SessionResult(45, 12, ResultKind.Abandoned, streakIncreased = false, goalReached = false),
            streak = 12,
            todayMinutes = 12,
            dailyGoal = 30,
            onDone = {},
        )
    }
}

// ----- Lumi in allen Posen und die kleine Glühbirne -----

@Composable
private fun LumiRow() {
    androidx.compose.foundation.layout.Row(
        androidx.compose.ui.Modifier.padding(Space.s3),
    ) {
        de.funke.app.ui.components.LumiPose.entries.forEach { pose ->
            de.funke.app.ui.components.Lumi2D(pose, androidx.compose.ui.Modifier.padding(Space.s1).then(androidx.compose.ui.Modifier.size(72.dp)), animateChange = false)
        }
        de.funke.app.ui.components.MiniLumi(androidx.compose.ui.Modifier.size(56.dp))
    }
}

@Preview(name = "Lumi · Modern", widthDp = 460, heightDp = 120)
@Composable
private fun LumiModernPreview() {
    FunkeTheme {
        AmbientBackground { LumiRow() }
    }
}

@Preview(name = "Lumi · Cartoon", widthDp = 460, heightDp = 120)
@Composable
private fun LumiCartoonPreview() {
    FunkeTheme(palette = Palettes.Sunny, style = de.funke.app.ui.theme.UiStyle.Cartoon) {
        AmbientBackground { LumiRow() }
    }
}

@Preview(name = "Hauptseite · Cartoon", device = PHONE)
@Composable
private fun MainCartoonPreview() {
    FunkeTheme(palette = Palettes.Sunny, style = de.funke.app.ui.theme.UiStyle.Cartoon) {
        MainScreen(AppState())
    }
}
