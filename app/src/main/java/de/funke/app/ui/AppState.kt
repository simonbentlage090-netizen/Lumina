package de.funke.app.ui

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.funke.app.lock.SessionLock
import de.funke.app.ui.challenge.ChallengeSettings
import de.funke.app.ui.challenge.Difficulty
import de.funke.app.ui.challenge.Topic
import de.funke.app.ui.theme.BackgroundPattern
import de.funke.app.ui.theme.Palettes
import de.funke.app.ui.theme.UiStyle
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import de.funke.app.progress.Streaks
import java.util.UUID

// ---------------------------------------------------------------------------------------------
// Datenmodelle
// ---------------------------------------------------------------------------------------------

/**
 * Ein Lese-Modus: ein Name und die Apps, die währenddessen erlaubt sind.
 * Der eingebaute Standard-Modus heißt je nach Sprache anders, eigene Modi behalten ihren Namen.
 */
data class FocusMode(
    val id: String,
    val customName: String,
    val packages: List<String>,
    val builtIn: Boolean = false,
) {
    val name: String
        get() = if (builtIn) tr("Tiefenfokus", "Deep focus") else customName

    /** Kurz und knapp, z. B. "Alles gesperrt" oder "3 Apps". */
    val subtitle: String
        get() = when (packages.size) {
            0 -> tr("Alles gesperrt", "Everything locked")
            1 -> tr("1 App erlaubt", "1 app allowed")
            else -> tr("${packages.size} Apps erlaubt", "${packages.size} apps allowed")
        }
}

/** Der Standard-Modus: Alles ist gesperrt, es bleibt nur der Fokus-Bildschirm. */
val DefaultMode = FocusMode(id = "default", customName = "", packages = emptyList(), builtIn = true)

enum class ResultKind { Completed, EndedEarly, Abandoned }

/** Hell, Dunkel oder wie das System. Antippen des Schalters geht reihum: Hell -> Dunkel -> System -> Hell. */
enum class ThemeMode(val id: String, private val de: String, private val en: String) {
    Light("light", "Hell", "Light"),
    Dark("dark", "Dunkel", "Dark"),
    System("system", "System", "System");

    val label: String get() = tr(de, en)
    val next: ThemeMode get() = when (this) {
        Light -> Dark
        Dark -> System
        System -> Light
    }

    companion object {
        fun byId(id: String?): ThemeMode? = entries.firstOrNull { it.id == id }
    }
}

data class SessionResult(
    val plannedMinutes: Int,
    val readMinutes: Int,
    val kind: ResultKind,
    val streakIncreased: Boolean,
    val goalReached: Boolean,
    /** Gerade ein Streak-Eis verdient? */
    val freezeEarned: Boolean = false,
)

/** Tagesziel als Stufe statt beliebiger Minutenzahl: motiviert mehr, sich an ein klares Ziel zu halten. */
enum class GoalLevel(val minutes: Int, private val de: String, private val en: String, private val descDe: String, private val descEn: String) {
    Easy(10, "Einstieg", "Starter", "Ein paar ruhige Minuten am Tag.", "A few calm minutes a day."),
    Medium(20, "Solide", "Solid", "Ein fester Platz in deinem Tag.", "A fixed spot in your day."),
    Hard(30, "Stark", "Strong", "Hier kommt richtig was zusammen.", "This really adds up."),
    Extreme(60, "Extrem", "Extreme", "Eine ganze Stunde. Für echte Profis.", "A full hour. For true pros.");

    val label: String get() = tr(de, en)
    val description: String get() = tr(descDe, descEn)

    /** Stunden im Jahr, wenn man das Ziel jeden Tag schafft. */
    val hoursPerYear: Int get() = minutes * 365 / 60

    /** Grobe Bücher pro Jahr (ein Buch ≈ 6 Stunden Lesezeit). */
    val booksPerYear: Int get() = (minutes * 365 / 360f).toInt().coerceAtLeast(1)

    /** Die Stufe, die wir empfehlen (fordert, ist aber gut machbar). */
    val recommended: Boolean get() = this == Hard

    companion object {
        /** Die Stufe, die am besten zu einer Minutenzahl passt (für alte, frei gewählte Ziele). */
        fun forMinutes(m: Int): GoalLevel = entries.minByOrNull { kotlin.math.abs(it.minutes - m) } ?: Medium
    }
}

/**
 * Streak-Ziele zur Auswahl (Tage am Stück). Seit 15.0 mit einem Namen je Stufe (wie beim Tagesziel) und einem
 * kurzen, ermutigenden Satz – statt nur einer Zahl, die bei 60 eher abschreckt.
 */
enum class StreakGoal(
    val days: Int,
    private val nameDe: String,
    private val nameEn: String,
    private val descDe: String,
    private val descEn: String,
) {
    Week(7, "Erste Woche", "First week", "Sieben Tage, und du spürst den Unterschied.", "Seven days, and you'll feel the difference."),
    TwoWeeks(14, "In Schwung", "Momentum", "Zwei Wochen, und Fokus fällt dir leichter.", "Two weeks, and focusing comes easier."),
    Month(30, "Gewohnheit", "Habit", "Ein Monat, und es wird zur Gewohnheit.", "One month, and it becomes a habit."),
    Habit(60, "Unaufhaltsam", "Unstoppable", "Zwei Monate, und es gehört einfach zu dir.", "Two months, and it's simply part of you.");

    /** Name der Stufe, z. B. "Gewohnheit". */
    val label: String get() = tr(nameDe, nameEn)
    val description: String get() = tr(descDe, descEn)

    /** Die Stufe, die wir empfehlen: lang genug für eine echte Gewohnheit, aber gut erreichbar. */
    val recommended: Boolean get() = this == Month

    companion object {
        fun forDays(d: Int): StreakGoal? = entries.firstOrNull { it.days == d }
        /** Das nächste Ziel nach dem erreichten (oder ein eigenes, wenn alle geschafft sind). */
        fun nextAfter(d: Int): Int = entries.firstOrNull { it.days > d }?.days ?: (d + 30)
    }
}

/** Anfrage, den Notiz-Editor zu öffnen. [nonce] sorgt dafür, dass er jedes Mal frisch startet. */
data class NoteEditorRequest(val id: Long?, val inSession: Boolean, val nonce: Long)

data class DayStatus(val done: Boolean, val today: Boolean, val future: Boolean)

sealed interface Screen {
    data object Main : Screen
    data object Focus : Screen
    data object Result : Screen
}

/** Wie der Countdown in der Session aussieht. */
enum class TimerStyleOption(
    val id: String,
    private val de: String,
    private val en: String,
    private val descDe: String,
    private val descEn: String,
) {
    Classic("classic", "Klassisch", "Classic", "Ruhige, dünne Ziffern. Standard.", "Calm, thin digits. Default."),
    Flip("flip", "Klappuhr", "Flip", "Wie eine alte Bahnhofsuhr: Die Minuten klappen um, sonst steht alles still.", "Like an old station clock: the minutes flip over, otherwise everything stays still."),
    Terminal("terminal", "Terminal", "Terminal", "Grüne Schrift wie auf einem alten Computer.", "Green type like on an old computer."),
    Vapor("vapor", "Dampf", "Vapor", "Die Ziffern lösen sich in Körnchen auf. Lebendig, aber unruhiger.", "Digits dissolve into grains. Lively, but busier.");

    val label: String get() = tr(de, en)
    val description: String get() = tr(descDe, descEn)

    companion object {
        fun byId(id: String?): TimerStyleOption = entries.firstOrNull { it.id == id } ?: Classic
    }
}

/** Wie der Ring um Lumi während der Session aussieht. */
enum class RingStyle(
    val id: String,
    private val de: String,
    private val en: String,
    private val descDe: String,
    private val descEn: String,
) {
    Classic("classic", "Klassisch", "Classic", "Ein ruhiger Ring mit sanftem Farbverlauf.", "A calm ring with a soft gradient."),
    Ticks("ticks", "Skala", "Ticks", "60 Striche wie bei einer Uhr. Die verbleibende Zeit leuchtet.", "60 marks like a clock. The time left lights up."),
    Comet("comet", "Komet", "Comet", "Ein Lichtschweif kreist einmal pro Minute um den Ring.", "A trail of light circles the ring once a minute."),
    Beads("beads", "Perlen", "Beads", "Leuchtende Perlen. Die vorderste atmet leise.", "Glowing beads. The leading one breathes softly."),
    Neon("neon", "Neon", "Neon", "Ein glühender Neonring, der langsam pulsiert.", "A glowing neon ring that slowly pulses."),
    Rays("rays", "Strahlen", "Rays", "Lichtstrahlen wie von einer Glühbirne, die sanft wogen.", "Rays of light like from a bulb, gently swaying."),
    Wave("wave", "Welle", "Wave", "Ein Ring, der sich ruhig wie Wasser bewegt.", "A ring that moves calmly like water.");

    val label: String get() = tr(de, en)
    val description: String get() = tr(descDe, descEn)

    companion object {
        fun byId(id: String?): RingStyle = entries.firstOrNull { it.id == id } ?: Rays
    }
}

/** Wie der Start-Knopf auf der Startseite aussieht. */
enum class StartButtonStyle(
    val id: String,
    private val de: String,
    private val en: String,
    private val descDe: String,
    private val descEn: String,
) {
    Pill("pill", "Halten", "Hold", "Breiter Knopf. Gedrückt halten, bis er sich gefüllt hat. Standard.", "Wide button. Hold until it has filled up. Default."),
    Round("round", "Rund", "Round", "Runder Knopf. Gedrückt halten, der Ring läuft voll.", "Round button. Hold while the ring fills."),
    Slide("slide", "Schieben", "Slide", "Den Knopf in die Mulde rechts schieben.", "Slide the knob into the slot on the right.");

    val label: String get() = tr(de, en)
    val description: String get() = tr(descDe, descEn)

    companion object {
        fun byId(id: String?): StartButtonStyle = entries.firstOrNull { it.id == id } ?: Pill
    }
}

/** Wie man eine laufende Session abbricht. */
enum class CancelStyle(
    val id: String,
    private val de: String,
    private val en: String,
    private val descDe: String,
    private val descEn: String,
) {
    Slider("slider", "Regler", "Slider", "Den Knopf nach rechts ziehen, er wird dabei rot. Standard.", "Drag the knob to the right; it turns red. Default."),
    Line("line", "Linie", "Line", "Schlichter Regler: eine feine Linie mit kleinem Knopf.", "A minimal slider: a thin line with a small knob."),
    Hold("hold", "Halten", "Hold", "Kein Schieben: gedrückt halten wie beim Start.", "No sliding: press and hold, like the start button.");

    val label: String get() = tr(de, en)
    val description: String get() = tr(descDe, descEn)

    companion object {
        fun byId(id: String?): CancelStyle = entries.firstOrNull { it.id == id } ?: Slider
    }
}

/**
 * Lumi auf dem Lese-Bildschirm (seit 11.0). Gesichter ziehen Blicke an, deshalb ist er dort
 * standardmäßig nicht zu sehen; wer mag, holt ihn ruhig oder lebendig zurück.
 */
enum class ReadingLumi(
    val id: String,
    private val de: String,
    private val en: String,
    private val descDe: String,
    private val descEn: String,
) {
    Off("off", "Aus", "Off", "Nur der Timer. Lumi kommt am Ende wieder. Standard.", "Just the timer. Lumi comes back at the end. Default."),
    Calm("calm", "Ruhig", "Calm", "Lumi liest still mit und schaut ins Buch.", "Lumi reads along quietly, eyes on the book."),
    Lively("lively", "Lebendig", "Lively", "Lumi reagiert, wenn du abbrechen willst.", "Lumi reacts when you try to stop.");

    val label: String get() = tr(de, en)
    val description: String get() = tr(descDe, descEn)

    companion object {
        fun byId(id: String?): ReadingLumi = entries.firstOrNull { it.id == id } ?: Off
    }
}

/** Was das Kreuz oben rechts in einer Session tut (seit 11.0 wählbar). */
enum class QuitConfirm(val id: String, private val de: String, private val en: String) {
    None("none", "Sofort", "Instantly"),
    Once("once", "Einmal fragen", "Ask once"),
    Strict("strict", "Streng", "Strict");

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?): QuitConfirm? = entries.firstOrNull { it.id == id }
    }
}

/** Auswahl im Zeit-Rad für eine Session. */
/**
 * Auswahl im Zeit-Ring. Jeder Wert bekommt gleich viel Drehung, deshalb liegen vorne mehr kleine Werte:
 * 20 Minuten sind fast ein Drittel der Runde, 40 die Hälfte, eine volle Runde endet genau bei 4 Stunden.
 */
val SessionDurations = listOf(1, 5, 10, 15, 20, 25, 30, 40, 45, 60, 75, 90, 120, 180, 240)

/** Nächster Wert aus [SessionDurations] (für alte gespeicherte Zeiten wie 35 oder 150). */
fun snapDuration(m: Int): Int = SessionDurations.minByOrNull { kotlin.math.abs(it - m) } ?: 30

/** Auswahl im Zeit-Rad für das Tagesziel. */
val GoalOptions = (5..240 step 5).toList()

private const val K_THEME = "theme"
private const val K_TIMER = "timer_style"
private const val K_MODES = "modes"
private const val K_MODE = "last_mode"
private const val K_MINUTES = "last_minutes"
private const val K_GOAL = "daily_goal"
private const val K_BEST = "best_streak"
private const val K_BOOK_DAY = "book_day"

/** So viele Minuten im Buch an einem Tag zählen auch für den Streak (vorher nur fürs Tagesziel). */
const val BOOK_STREAK_MINUTES = 5

private const val K_DAYS = "day_minutes"
private const val K_STYLE = "ui_style"
private const val K_THEME_V14 = "theme_default_v14"
private const val K_HAPTIC = "haptic_level"
private const val K_HAPTIC_MODE = "haptic_mode"
private const val K_SOUND = "sound_on"
private const val K_PRACTICE_SUBJECT = "practice_subject"
private const val K_PRACTICE_LEVELS = "practice_levels"
private const val K_DARK = "dark_mode"
private const val K_THEME_MODE = "theme_mode"
private const val K_RING = "ring_style"
private const val K_SIDEKICK = "sidekick"
private const val K_FILAMENT = "filament_style"
private const val K_PRACTICE_TOTAL = "practice_total"
private const val K_PRACTICE_DAY = "practice_day"
private const val K_PRACTICE_STREAK = "practice_streak"
private const val K_PRACTICE_BEST = "practice_best"
private const val K_APP_GROUPS = "app_groups"
private const val K_ONBOARDED = "onboarded"
private const val K_REMINDER = "reminder_on"
private const val K_REMINDER_HOUR = "reminder_hour"
private const val K_REMINDER_MINUTE = "reminder_minute"
private const val K_WARN = "streak_warn_on"
private const val K_WARN_HOUR = "streak_warn_hour"
private const val K_WARN_MINUTE = "streak_warn_minute"
private const val K_ONBOARD_VERSION = "onboard_version"
private const val K_STREAK_GOAL = "streak_goal"
private const val K_PRACTICE_RAMP = "practice_ramp"
private const val K_PRACTICE_MASTER = "practice_master"
private const val K_SPARKS = "sparks"
private const val TASKS_PER_FREEZE = 15
private const val K_TASKS_SOLVED = "tasks_solved"
private const val K_TASK_FREEZE = "task_freeze_progress"
private const val K_MOTION_HINT = "motion_hint_seen"
private const val K_STRICT_QUIT = "strict_quit"
private const val K_VOICE_VISUAL = "voice_visual"
private const val K_DICTATION_LANG = "dictation_lang"
private const val K_AI_PROVIDER = "ai_provider"
private const val K_AI_KEY = "ai_key"
private const val K_AI_SERVER = "ai_server"
private const val K_NOTE_FORMAT = "note_format"
private const val K_NOTE_PAPER = "note_paper"
private const val K_AI_CONSENT = "ai_consent"
private const val K_DEVICE_ID = "device_id"

/** Wird die Einrichtung um neue Schritte erweitert, Zahl erhöhen: dann sehen sie alle einmal. */
private const val ONBOARD_VERSION = 4
private const val K_INTRO = "start_intro"
private const val K_START_STYLE = "start_button_style"
private const val K_CANCEL_STYLE = "cancel_style"
/**
 * Standard-Formen von Start und Abbrechen. 10.1: beide Schieberegler. 11.0: Start wieder Halten
 * (neu gestaltet), Abbrechen bleibt Schieben. Einmal auf die neue Form umstellen, danach frei wählbar.
 */
private const val K_CONTROLS_V = "controls_version"
private const val CONTROLS_VERSION = 3
/** 11.0: Strahlen als Ring für alle, die noch den alten Standard hatten (einmalig). */
private const val K_LOOK_V = "look_version"
private const val K_READING_LUMI = "reading_lumi"
private const val K_QUIT_CONFIRM = "quit_confirm"
private const val K_CHALLENGE_DIFFICULTY = "challenge_difficulty"
private const val K_CHALLENGE_TOPICS = "challenge_topics"
private const val K_PATTERN = "bg_pattern"
private const val K_FONT = "app_font"
private const val K_REDUCE_MOTION = "reduce_motion"
private const val K_MOTION_MODE = "motion_mode"
private const val K_MOTION_V14 = "motion_default_v14"
private const val K_SESSION_END = "session_end"
private const val K_SESSION_MINUTES = "session_minutes"
private const val K_SESSION_MODE = "session_mode"

// ---------------------------------------------------------------------------------------------
// Zustand der ganzen App ("State Holder")
// ---------------------------------------------------------------------------------------------

/**
 * Hier liegt alles, was sich ändert. Die Screens lesen nur und melden Aktionen zurück.
 * Mit [prefs] wird alles dauerhaft auf dem Handy gespeichert. Ohne (z. B. in Previews) bleibt es im Speicher.
 */
class AppState(private val prefs: SharedPreferences? = null, private val appContext: android.content.Context? = null) {

    var screen by mutableStateOf<Screen>(Screen.Main)
        private set

    // --- Aussehen ---
    /**
     * Ganz neu installiert (noch nie gestartet)? Dann gilt seit 13.0 der warme Cartoon-Look mit Raster
     * und Hell/Dunkel wie das System. Wer die App schon hatte, behält alles, wie es war.
     */
    private val freshInstall: Boolean = prefs != null && !prefs.contains(K_ONBOARDED) && !prefs.contains(K_THEME)

    var themeId by mutableStateOf(prefs?.getString(K_THEME, null) ?: if (freshInstall) Palettes.IndigoDark.id else Palettes.Obsidian.id)
        private set
    var timerStyle by mutableStateOf(TimerStyleOption.byId(prefs?.getString(K_TIMER, null)))
        private set
    var settingsVisible by mutableStateOf(false)
        private set
    var uiStyle by mutableStateOf(UiStyle.byId(prefs?.getString(K_STYLE, null) ?: if (freshInstall) UiStyle.Glass.id else null))
        private set
    /** Haptik-Stärke: 0 = aus, 1 = sanft, 2 = mittel, 3 = kräftig. */
    var hapticLevel by mutableIntStateOf(prefs?.getInt(K_HAPTIC, 2) ?: 2)
        private set
    var hapticMode by mutableStateOf(HapticMode.byId(prefs?.getString(K_HAPTIC_MODE, null)))
        private set
    /** Töne bei Aufgaben (richtig, falsch, geschafft) */
    var soundOn by mutableStateOf(prefs?.getBoolean(K_SOUND, true) ?: true)
        private set
    /** Dunkel oder Hell. Die Farbschemata sind danach getrennt. */
    var darkMode by mutableStateOf(
        prefs?.let { if (it.contains(K_DARK)) it.getBoolean(K_DARK, true) else null } ?: Palettes.byId(themeId).isDark
    )
        private set
    var ringStyle by mutableStateOf(RingStyle.byId(prefs?.getString(K_RING, null)))
        private set
    /** Lumi (der Sidekick) an oder aus. */
    var sidekick by mutableStateOf(prefs?.getBoolean(K_SIDEKICK, true) ?: true)
        private set
    /** Wie Lumis Glühdraht aussieht, wenn das Licht ausgeht. */
    var filamentStyle by mutableStateOf(de.funke.app.ui.components.FilamentStyle.byId(prefs?.getString(K_FILAMENT, null)))
        private set
    /** Kurze Licht-Animation beim Öffnen der App. */
    var startAnimation by mutableStateOf(prefs?.getBoolean(K_INTRO, true) ?: true)
        private set
    private val controlsFresh = (prefs?.getInt(K_CONTROLS_V, 0) ?: CONTROLS_VERSION) < CONTROLS_VERSION
    var startButtonStyle by mutableStateOf(
        if (controlsFresh) StartButtonStyle.Pill else StartButtonStyle.byId(prefs?.getString(K_START_STYLE, null)),
    )
        private set
    var cancelStyle by mutableStateOf(
        if (controlsFresh) CancelStyle.Slider else CancelStyle.byId(prefs?.getString(K_CANCEL_STYLE, null)),
    )
        private set

    /** Welche Aufgaben beim Abbrechen kommen (Schwierigkeit und Themen). */
    var challengeSettings by mutableStateOf(
        ChallengeSettings(
            difficulty = Difficulty.byId(prefs?.getString(K_CHALLENGE_DIFFICULTY, null)),
            topics = prefs?.getString(K_CHALLENGE_TOPICS, null)
                ?.split(',')?.map { it.trim() }
                ?.let { ids ->
                    // Bis 8.3 gab es nur ein Thema "Physik": wer es an hatte, bekommt alle neuen Physik-Themen.
                    val set = ids.mapNotNull { Topic.byId(it) }.toMutableSet()
                    if ("physics" in ids && ids.none { it.startsWith("physics_") }) {
                        set += Topic.entries.filter { it.subject == de.funke.app.ui.challenge.Subject.Physics }
                    }
                    set.toSet()
                }
                ?.ifEmpty { null }
                ?: Topic.Default,
        )
    )
        private set

    fun updateChallengeDifficulty(d: Difficulty) {
        challengeSettings = challengeSettings.copy(difficulty = d)
        prefs?.edit()?.putString(K_CHALLENGE_DIFFICULTY, d.id)?.apply()
    }

    /** Thema an/aus. Mindestens ein Thema bleibt immer an. */
    /** Alle Themen eines Fachs für die Aufgabe beim Abbrechen an oder aus (eins bleibt immer an). */
    fun toggleChallengeSubject(subject: de.funke.app.ui.challenge.Subject) {
        val now = challengeSettings.topics
        val mine = Topic.entries.filter { it.subject == subject }.toSet()
        val next = if (now.containsAll(mine)) now - mine else now + mine
        if (next.isEmpty()) return
        challengeSettings = challengeSettings.copy(topics = next)
        prefs?.edit()?.putString(K_CHALLENGE_TOPICS, next.joinToString(",") { it.id })?.apply()
    }

    fun toggleChallengeTopic(t: Topic) {
        val now = challengeSettings.topics
        val next = if (t in now) now - t else now + t
        if (next.isEmpty()) return
        challengeSettings = challengeSettings.copy(topics = next)
        prefs?.edit()?.putString(K_CHALLENGE_TOPICS, next.joinToString(",") { it.id })?.apply()
    }
    // --- Üben (Aufgaben-Tab) ---
    /** Richtig gelöste Übungsaufgaben insgesamt. */
    var practiceTotal by mutableIntStateOf(prefs?.getInt(K_PRACTICE_TOTAL, 0) ?: 0)
        private set
    /** So viele Aufgaben in Folge auf Anhieb gelöst (ohne Fehlversuch, Tipp oder Lösungsweg). */
    var practiceStreak by mutableIntStateOf(prefs?.getInt(K_PRACTICE_STREAK, 0) ?: 0)
        private set
    var practiceBest by mutableIntStateOf(prefs?.getInt(K_PRACTICE_BEST, 0) ?: 0)
        private set
    /** "2026-11-10:5" = heute 5 gelöst */
    private var practiceDay by mutableStateOf(prefs?.getString(K_PRACTICE_DAY, null) ?: "")

    val practiceToday: Int
        get() = if (practiceDay.substringBefore(':') == LocalDate.now().toString()) practiceDay.substringAfter(':').toIntOrNull() ?: 0 else 0

    fun recordPractice(firstTry: Boolean) {
        practiceTotal++
        practiceDay = "${LocalDate.now()}:${practiceToday + 1}"
        practiceStreak = if (firstTry) practiceStreak + 1 else 0
        if (practiceStreak > practiceBest) practiceBest = practiceStreak
        prefs?.edit()
            ?.putInt(K_PRACTICE_TOTAL, practiceTotal)
            ?.putString(K_PRACTICE_DAY, practiceDay)
            ?.putInt(K_PRACTICE_STREAK, practiceStreak)
            ?.putInt(K_PRACTICE_BEST, practiceBest)
            ?.apply()
    }

    /**
     * Funken: Belohnung im Aufgaben-Reiter. Schwerere Aufgaben und mehr Schritte geben mehr,
     * jeder Tipp kostet einen, mit angeschauter Lösung gibt es keine.
     */
    var sparks by mutableIntStateOf(prefs?.getInt(K_SPARKS, 0) ?: 0)
        private set

    fun addSparks(n: Int) {
        if (n <= 0) return
        sparks += n
        prefs?.edit()?.putInt(K_SPARKS, sparks)?.apply()
    }

    /** So viele gelöste Aufgaben (ohne angeschaute Lösung) geben ein Streak-Eis. */
    val tasksPerFreeze: Int get() = TASKS_PER_FREEZE

    /** Fortschritt zum nächsten Streak-Eis aus Aufgaben (0 … [tasksPerFreeze]). */
    var taskFreezeProgress by mutableIntStateOf(prefs?.getInt(K_TASK_FREEZE, 0) ?: 0)
        private set

    /** Gelöste geschriebene Aufgaben: die kommen erst wieder, wenn alle anderen dran waren. */
    private var solvedTaskIds: Set<String> = prefs?.getStringSet(K_TASKS_SOLVED, emptySet())?.toSet() ?: emptySet()

    /**
     * Eine Aufgabe im Reiter "Aufgaben" ist gelöst. Zählt fürs Üben, gibt Funken und bringt dem
     * nächsten Streak-Eis näher. Rückgabe: true, wenn dabei gerade ein Streak-Eis verdient wurde.
     */
    fun recordTaskSolved(id: String, sparks: Int, clean: Boolean): Boolean {
        recordPractice(clean)
        addSparks(sparks)
        de.funke.app.ui.challenge.Tasks.markSolved(id)
        if (!id.startsWith("gen:") && id !in solvedTaskIds) {
            solvedTaskIds = solvedTaskIds + id
            prefs?.edit()?.putStringSet(K_TASKS_SOLVED, solvedTaskIds)?.apply()
        }
        var earned = false
        val next = taskFreezeProgress + 1
        if (next >= TASKS_PER_FREEZE && streakFreezes < Streaks.MAX_FREEZES) {
            streakFreezes++
            taskFreezeProgress = 0
            earned = true
            prefs?.edit()?.putInt(Streaks.K_FREEZES, streakFreezes)?.apply()
        } else {
            taskFreezeProgress = next.coerceAtMost(TASKS_PER_FREEZE)
        }
        prefs?.edit()?.putInt(K_TASK_FREEZE, taskFreezeProgress)?.apply()
        return earned
    }

    // --- Lektionen (Aufgaben-Reiter seit 10.0) ---

    private fun readMap(key: String): Map<String, String> =
        prefs?.getString(key, null).orEmpty().split(';').mapNotNull {
            val i = it.indexOf('=')
            if (i <= 0) null else it.substring(0, i) to it.substring(i + 1)
        }.toMap()

    private fun writeMap(key: String, map: Map<String, String>) {
        prefs?.edit()?.putString(key, map.entries.joinToString(";") { it.key + "=" + it.value })?.apply()
    }

    /** Zuletzt geübtes Fach (öffnet in den Aufgaben-Einstellungen das passende Fach). */
    var practiceSubject by mutableStateOf(
        prefs?.getString(K_PRACTICE_SUBJECT, null)?.let { id -> de.funke.app.ui.challenge.Subject.entries.firstOrNull { it.id == id } }
    )
        private set

    /** Gewählte Stufe je Fach ("physics" → "medium"; "mix" für Gemischt). */
    private var subjectLevels by mutableStateOf(readMap(K_PRACTICE_LEVELS))

    /** Die Stufe, die zuletzt in diesem Fach gewählt war (null = Gemischt). */
    fun practiceLevelFor(subject: de.funke.app.ui.challenge.Subject?): de.funke.app.ui.challenge.PracticeLevel {
        val wanted = de.funke.app.ui.challenge.PracticeLevel.byId(subjectLevels[subject?.id ?: "mix"]) ?: practiceLevelAll
        return if (subject == null) wanted else de.funke.app.ui.challenge.Practice.fit(subject, wanted)
    }

    /**
     * Gewählte Stufe eines Fachs (null = Gemischt) merken, sobald man sie im Regal antippt – nicht erst beim Start
     * einer Lektion. Sonst stand in der Übersicht unter dem Fach noch die alte Stufe (Fehler in 14.0).
     */
    fun rememberPracticeLevel(subject: de.funke.app.ui.challenge.Subject?, level: de.funke.app.ui.challenge.PracticeLevel) {
        val key = subject?.id ?: "mix"
        if (subjectLevels[key] == level.id) return
        subjectLevels = subjectLevels + (key to level.id)
        writeMap(K_PRACTICE_LEVELS, subjectLevels)
    }

    /** Fach (null = Gemischt) und Stufe einer gestarteten Lektion merken. */
    fun choosePractice(subject: de.funke.app.ui.challenge.Subject?, level: de.funke.app.ui.challenge.PracticeLevel) {
        subjectLevels = subjectLevels + ((subject?.id ?: "mix") to level.id)
        writeMap(K_PRACTICE_LEVELS, subjectLevels)
        lastLesson = (subject?.id ?: "mix")
        prefs?.edit()?.putString("practice_last_lesson", lastLesson)?.apply()
        if (subject != null) {
            practiceSubject = subject
            prefs?.edit()?.putString(K_PRACTICE_SUBJECT, subject.id)?.apply()
        }
    }

    /** Zuletzt gestartete Lektion ("mix" oder Fach-ID), für "Weiter" oben im Reiter. */
    var lastLesson by mutableStateOf(prefs?.getString("practice_last_lesson", null))
        private set

    /**
     * Schwierigkeit im Aufgaben-Reiter (seit 11.0 eine für alle Fächer). Gibt es sie in einem Fach nicht,
     * nimmt das Fach die nächstgelegene darunter ([de.funke.app.ui.challenge.Practice.fit]).
     */
    var practiceLevelAll by mutableStateOf(
        de.funke.app.ui.challenge.PracticeLevel.byId(prefs?.getString("practice_level_all", null))
            ?: de.funke.app.ui.challenge.PracticeLevel.byId(subjectLevels[practiceSubject?.id ?: ""])
            ?: de.funke.app.ui.challenge.PracticeLevel.Medium
    )
        private set

    fun updatePracticeLevelAll(level: de.funke.app.ui.challenge.PracticeLevel) {
        practiceLevelAll = level
        prefs?.edit()?.putString("practice_level_all", level.id)?.apply()
    }

    /** Reiter, die sich schon einmal erklärt haben (siehe TabIntro). */
    var tabIntrosSeen by mutableStateOf(
        prefs?.getString("tab_intros_seen", "")!!.split(',').filter { it.isNotBlank() }.toSet()
    )
        private set

    fun markTabIntro(id: String) {
        tabIntrosSeen = tabIntrosSeen + id
        prefs?.edit()?.putString("tab_intros_seen", tabIntrosSeen.joinToString(","))?.apply()
    }

    /** Während einer Aufgaben-Runde: Leiste unten ausblenden, Wischen zwischen Reitern aus. */
    var immersive by mutableStateOf(false)

    /** Hinweis "Animationen sind im System aus" schon gezeigt? */
    var motionHintSeen by mutableStateOf(prefs?.getBoolean(K_MOTION_HINT, false) ?: true)
        private set

    fun dismissMotionHint() {
        motionHintSeen = true
        prefs?.edit()?.putBoolean(K_MOTION_HINT, true)?.apply()
    }

    /** Bei "Experte": Meisterklasse (Uni-Niveau) dazuschalten. */
    var practiceMaster by mutableStateOf(prefs?.getBoolean(K_PRACTICE_MASTER, false) ?: false)
        private set

    fun updatePracticeMaster(on: Boolean) {
        practiceMaster = on
        prefs?.edit()?.putBoolean(K_PRACTICE_MASTER, on)?.apply()
    }

    /** Lösungsweg angeschaut: Die Serie ist vorbei. */
    fun breakPracticeStreak() {
        if (practiceStreak == 0) return
        practiceStreak = 0
        prefs?.edit()?.putInt(K_PRACTICE_STREAK, 0)?.apply()
    }

    // --- Bildschirmzeit: Gruppen ---
    /** Eigene Gruppen und Zuordnungen (JSON, siehe usage/AppGroups.kt). */
    var appGroupsJson by mutableStateOf(prefs?.getString(K_APP_GROUPS, null))
        private set

    fun saveAppGroups(json: String) {
        appGroupsJson = json
        prefs?.edit()?.putString(K_APP_GROUPS, json)?.apply()
    }

    var backgroundPattern by mutableStateOf(BackgroundPattern.byId(prefs?.getString(K_PATTERN, null) ?: if (freshInstall) BackgroundPattern.Grid.id else null))
        private set

    /** Schrift (seit 15.2): "Passend zum Stil" oder eine feste Wahl. */
    var appFont by mutableStateOf(de.funke.app.ui.theme.AppFont.byId(runCatching { prefs?.getString(K_FONT, null) }.getOrNull()))
        private set

    fun updateAppFont(font: de.funke.app.ui.theme.AppFont) {
        appFont = font
        prefs?.edit()?.putString(K_FONT, font.id)?.apply()
    }

    /**
     * Bewegung: Automatisch (folgt "Animationen entfernen" im Handy), Voll oder Weniger.
     * Alte Einstellung "Bewegung reduzieren = an" (9.7) wird zu "Weniger".
     */
    var motionMode by mutableStateOf(
        // Seit 14.0 standardmäßig volle Animationen (auch wenn im Handy "Animationen entfernen" an ist).
        // Einmalig: wer noch auf dem alten Standard "Automatisch" stand, kommt auf "Voll".
        if (prefs != null && !prefs.getBoolean(K_MOTION_V14, false)) {
            val old = prefs.getString(K_MOTION_MODE, null)
            val next = if (old == null || old == de.funke.app.ui.theme.MotionMode.Auto.id) {
                if (prefs.getBoolean(K_REDUCE_MOTION, false) && old == null) de.funke.app.ui.theme.MotionMode.Reduced
                else de.funke.app.ui.theme.MotionMode.Full
            } else de.funke.app.ui.theme.MotionMode.byId(old)
            prefs.edit().putBoolean(K_MOTION_V14, true).putString(K_MOTION_MODE, next.id).apply()
            next
        } else {
            prefs?.getString(K_MOTION_MODE, null)?.let { de.funke.app.ui.theme.MotionMode.byId(it) } ?: de.funke.app.ui.theme.MotionMode.Full
        }
    )
        private set

    fun updateMotionMode(mode: de.funke.app.ui.theme.MotionMode) {
        motionMode = mode
        prefs?.edit()?.putString(K_MOTION_MODE, mode.id)?.apply()
    }
    val language: AppLanguage get() = Lang.current

    // --- Modi ---
    val modes = mutableStateListOf<FocusMode>()
    var selectedModeId by mutableStateOf(DefaultMode.id)
        private set
    var editorVisible by mutableStateOf(false)
        private set
    var editorMode by mutableStateOf<FocusMode?>(null)
        private set
    var editorNonce by mutableIntStateOf(0)
        private set

    // --- Zeit, Ziel, Fortschritt ---
    var minutes by mutableIntStateOf(snapDuration(prefs?.getInt(K_MINUTES, 30) ?: 30))
        private set
    var dailyGoal by mutableIntStateOf(prefs?.getInt(K_GOAL, 30) ?: 30)  // seit 13.4: empfohlen (Stark) ist vorausgewählt
        private set
    var bestStreak by mutableIntStateOf(prefs?.getInt(K_BEST, 0) ?: 0)
        private set
    private val dayMinutes = mutableStateMapOf<String, Int>()
    /** Tage mit mindestens einer ganz durchgezogenen Session (nur die zählen für den Streak). */
    private var doneDays by mutableStateOf(setOf<String>())
    /** Verpasste Tage, die mit Streak-Eis überbrückt wurden. */
    private var frozenDays by mutableStateOf(Streaks.parseSet(prefs?.getString(Streaks.K_FROZEN, null)))
    /** Streak-Eis auf Vorrat (höchstens [Streaks.MAX_FREEZES]). */
    var streakFreezes by mutableIntStateOf(prefs?.getInt(Streaks.K_FREEZES, 0) ?: 0)
        private set

    /** Ist die Einrichtung (in der aktuellen Fassung) schon durch? */
    var onboarded by mutableStateOf(prefs == null || prefs.getInt(K_ONBOARD_VERSION, 0) >= ONBOARD_VERSION)
        private set
    /** Abends erinnern, wenn das Tagesziel noch fehlt. */
    var reminderOn by mutableStateOf(prefs?.getBoolean(K_REMINDER, true) ?: true)
        private set
    /** Uhrzeit der Erinnerung. */
    var reminderHour by mutableIntStateOf(prefs?.getInt(K_REMINDER_HOUR, 19) ?: 19)
        private set
    var reminderMinute by mutableIntStateOf(prefs?.getInt(K_REMINDER_MINUTE, 0) ?: 0)
        private set
    /** Warnen, wenn der Streak heute sonst reißt. */
    var streakWarnOn by mutableStateOf(prefs?.getBoolean(K_WARN, true) ?: true)
        private set
    var streakWarnHour by mutableIntStateOf(prefs?.getInt(K_WARN_HOUR, 21) ?: 21)
        private set
    var streakWarnMinute by mutableIntStateOf(prefs?.getInt(K_WARN_MINUTE, 30) ?: 30)
        private set
    // ----- Notizen -----

    /** Alle Notizen (neueste zuletzt). */
    var notes by mutableStateOf(de.funke.app.notes.NotesStore.load(appContext))
        private set
    /** Notizen-Übersicht offen? */
    var notesVisible by mutableStateOf(false)
    /** Kennung der laufenden bzw. zuletzt beendeten Session (deren Endzeit). */
    var lastSessionKey by mutableLongStateOf(0L)
        private set
    private val noteSessionKey: Long get() = if (sessionEndAt > 0L) sessionEndAt else 0L

    fun notesOfSession(key: Long): List<de.funke.app.notes.Note> = if (key == 0L) emptyList() else notes.filter { it.session == key }
    val currentSessionNotes: List<de.funke.app.notes.Note> get() = notesOfSession(noteSessionKey)

    /** Offener Notiz-Editor (Vollbild): welche Notiz (null = neu) und ob er aus der Session kommt. */
    var noteEditor by mutableStateOf<NoteEditorRequest?>(null)
        private set

    fun openNoteEditor(id: Long?, inSession: Boolean) {
        noteEditor = NoteEditorRequest(id, inSession, System.nanoTime())
    }

    fun closeNoteEditor() {
        noteEditor = null
    }

    /**
     * Speichert den Text einer Notiz (Autospeichern). Neue Notiz, sobald Text da ist; leerer Text löscht sie.
     * @return die Kennung der Notiz (oder null, wenn es keine gibt).
     */
    fun saveNoteText(id: Long?, text: String, voice: Boolean): Long? {
        val t = text.trimEnd()
        val existing = id?.let { i -> notes.firstOrNull { it.id == i } }
        if (existing == null) {
            if (t.isBlank()) return null
            val now = System.currentTimeMillis()
            notes = notes + de.funke.app.notes.Note(id = now, createdAt = now, text = t, voice = voice, session = noteSessionKey)
            de.funke.app.notes.NotesStore.save(appContext, notes)
            return now
        }
        if (t.isBlank()) {
            deleteNote(existing.id)
            return null
        }
        if (existing.text != t || (voice && !existing.voice)) {
            notes = notes.map { if (it.id == existing.id) it.copy(text = t, voice = it.voice || voice) else it }
            de.funke.app.notes.NotesStore.save(appContext, notes)
        }
        return existing.id
    }

    /** Merkt sich die andere Fassung einer Notiz (vor/nach dem Aufräumen), null = keine. */
    fun saveNoteOriginal(id: Long?, original: String?) {
        val i = id ?: return
        if (notes.none { it.id == i }) return
        notes = notes.map { if (it.id == i) it.copy(original = original?.trimEnd()?.takeIf { o -> o.isNotBlank() }) else it }
        de.funke.app.notes.NotesStore.save(appContext, notes)
    }

    /** Welche Animation beim Diktieren läuft. */
    var voiceVisual by mutableStateOf(de.funke.app.ui.components.VoiceVisual.byId(prefs?.getString(K_VOICE_VISUAL, null)))
        private set
    fun updateVoiceVisual(v: de.funke.app.ui.components.VoiceVisual) {
        voiceVisual = v
        prefs?.edit()?.putString(K_VOICE_VISUAL, v.id)?.apply()
    }

    /** Diktiersprache: "auto" (wie das Handy), "de" oder "en" – unabhängig von der App-Sprache. */
    var dictationLang by mutableStateOf(prefs?.getString(K_DICTATION_LANG, "auto") ?: "auto")
        private set
    fun updateDictationLang(v: String) {
        dictationLang = v
        prefs?.edit()?.putString(K_DICTATION_LANG, v)?.apply()
    }
    /** Sprach-Tag für die Erkennung, z. B. "de-DE". */
    val dictationTag: String
        get() = when (dictationLang) {
            "de" -> "de-DE"
            "en" -> "en-US"
            else -> java.util.Locale.getDefault().toLanguageTag().ifBlank { "de-DE" }
        }

    /** KI zum Aufräumen: an (Lumina-KI) oder aus. Ein alter "eigener Schlüssel" wird beim Start gelöscht. */
    var aiProvider by mutableStateOf(de.funke.app.notes.AiCleanup.Provider.byId(prefs?.getString(K_AI_PROVIDER, null)))
        private set
    fun updateAi(provider: de.funke.app.notes.AiCleanup.Provider) {
        aiProvider = provider
        prefs?.edit()?.putString(K_AI_PROVIDER, provider.id)?.apply()
    }
    val aiReady: Boolean get() = aiProvider == de.funke.app.notes.AiCleanup.Provider.Lumina && aiServer.isNotBlank()

    /** Form beim Aufräumen: Fließtext oder Stichpunkte. */
    var noteFormat by mutableStateOf(de.funke.app.notes.AiCleanup.Format.byId(prefs?.getString(K_NOTE_FORMAT, null)))
        private set
    fun updateNoteFormat(f: de.funke.app.notes.AiCleanup.Format) {
        noteFormat = f
        prefs?.edit()?.putString(K_NOTE_FORMAT, f.id)?.apply()
    }

    /** Aussehen des Notiz-Blatts. */
    var notePaper by mutableStateOf(NotePaper.byId(prefs?.getString(K_NOTE_PAPER, null)))
        private set
    fun updateNotePaper(v: NotePaper) {
        notePaper = v
        prefs?.edit()?.putString(K_NOTE_PAPER, v.id)?.apply()
    }

    /** Adresse des Lumina-KI-Workers (änderbar, z. B. für einen eigenen Worker). */
    var aiServer by mutableStateOf(prefs?.getString(K_AI_SERVER, null) ?: de.funke.app.notes.AiCleanup.DEFAULT_SERVER)
        private set
    fun updateAiServer(url: String) {
        aiServer = url.trim().ifBlank { de.funke.app.notes.AiCleanup.DEFAULT_SERVER }
        prefs?.edit()?.putString(K_AI_SERVER, aiServer)?.apply()
    }

    /** Hat der Nutzer zugestimmt, dass Notizen zum Aufräumen an Lumina-KI gehen? */
    var aiConsent by mutableStateOf(prefs?.getBoolean(K_AI_CONSENT, false) ?: false)
        private set
    fun updateAiConsent(ok: Boolean) {
        aiConsent = ok
        prefs?.edit()?.putBoolean(K_AI_CONSENT, ok)?.apply()
    }

    /** Wie viele KI-Durchgänge heute noch gehen (vom Worker gemeldet, nur zur Anzeige). */
    var aiLeft by mutableStateOf<Int?>(null)

    /** Anonyme Kennung dieses Handys (zufällig, nur für die Tageslimits von Lumina-KI). */
    val deviceId: String by lazy {
        prefs?.getString(K_DEVICE_ID, null) ?: java.util.UUID.randomUUID().toString().also { id ->
            prefs?.edit()?.putString(K_DEVICE_ID, id)?.apply()
        }
    }

    fun addNote(text: String, voice: Boolean) {
        val t = text.trim()
        if (t.isEmpty()) return
        val now = System.currentTimeMillis()
        notes = notes + de.funke.app.notes.Note(id = now, createdAt = now, text = t, voice = voice, session = noteSessionKey)
        de.funke.app.notes.NotesStore.save(appContext, notes)
    }

    fun updateNote(id: Long, text: String) {
        val t = text.trim()
        notes = if (t.isEmpty()) notes.filter { it.id != id } else notes.map { if (it.id == id) it.copy(text = t) else it }
        de.funke.app.notes.NotesStore.save(appContext, notes)
    }

    fun deleteNote(id: Long) {
        notes = notes.filter { it.id != id }
        de.funke.app.notes.NotesStore.save(appContext, notes)
    }

    // ----- Notizen im Buch (seit 14.0): eine Notiz je Seite, mit Buch und Stelle -----

    fun bookNotes(bookId: String): List<de.funke.app.notes.Note> = notes.filter { it.book == bookId }

    /**
     * Notiz zu einer Buchseite speichern. Ohne [id] entsteht eine neue (sobald Text da ist); leerer Text löscht sie.
     * @return die Kennung (oder null, wenn es keine gibt).
     */
    fun saveBookNote(id: Long?, book: de.funke.app.books.Book, page: Int, section: Int, offset: Int, text: String, voice: Boolean = false): Long? {
        val t = text.trimEnd()
        val existing = id?.let { i -> notes.firstOrNull { it.id == i } }
        if (existing == null) {
            if (t.isBlank()) return null
            val now = System.currentTimeMillis()
            notes = notes + de.funke.app.notes.Note(
                id = now, createdAt = now, text = t, voice = voice, session = noteSessionKey,
                book = book.id, bookTitle = book.title, page = page, section = section, offset = offset,
            )
            de.funke.app.notes.NotesStore.save(appContext, notes)
            return now
        }
        if (t.isBlank()) {
            deleteNote(existing.id)
            return null
        }
        if (existing.text != t || existing.bookTitle != book.title || (voice && !existing.voice)) {
            notes = notes.map { if (it.id == existing.id) it.copy(text = t, bookTitle = book.title, voice = it.voice || voice) else it }
            de.funke.app.notes.NotesStore.save(appContext, notes)
        }
        return existing.id
    }

    /** Buch an der Stelle einer Notiz öffnen ("Zur Seite"). */
    fun openNoteInBook(n: de.funke.app.notes.Note) {
        val id = n.book ?: return
        val b = de.funke.app.books.Library.get(id) ?: return
        closeNoteEditor()
        notesVisible = false
        if (openBookId == id) {
            readerJump = n.section to n.offset
            return
        }
        de.funke.app.books.Library.savePosition(id, n.section, n.offset, b.progress)
        de.funke.app.books.Library.commitPosition(id)
        openBook(id)
    }

    /** Im Reiter "Aufgaben": nach jeder sauber gelösten Aufgabe wird es eine Stufe schwerer. */
    var practiceRamp by mutableStateOf(prefs?.getBoolean(K_PRACTICE_RAMP, false) ?: false)
        private set

    fun updatePracticeRamp(on: Boolean) {
        practiceRamp = on
        prefs?.edit()?.putBoolean(K_PRACTICE_RAMP, on)?.apply()
    }

    /** Streak-Ziel in Tagen (0 = keins). */
    var streakGoal by mutableIntStateOf(prefs?.getInt(K_STREAK_GOAL, 0) ?: 0)
        private set

    val goalLevel: GoalLevel get() = GoalLevel.forMinutes(dailyGoal)

    var result by mutableStateOf<SessionResult?>(null)
        private set

    // --- Laufende Session ---
    /** Ende der laufenden Session als Uhrzeit (System.currentTimeMillis). */
    var sessionEndAt by mutableStateOf(0L)
        private set
    /** Geplante Dauer der laufenden Session. */
    var sessionMinutes by mutableIntStateOf(0)
        private set

    // --- Sperre ---
    /** Ist die Bedienungshilfe eingeschaltet? Wird bei jedem Zurückkehren in die App aktualisiert. */
    var lockEnabled by mutableStateOf(false)
        private set
    var lockSetupVisible by mutableStateOf(false)
        private set

    /** Lumi hat gerade ein Rettungs-Eis verschenkt – einmal anzeigen. */
    var giftNotice by mutableStateOf(false)
        private set

    init {
        de.funke.app.ui.challenge.Tasks.restoreSolved(prefs?.getStringSet(K_TASKS_SOLVED, emptySet()).orEmpty())
        // Vorlieben und Verlauf der Aufgaben (bleiben über Neustarts erhalten)
        prefs?.let { sp ->
            val store = object : de.funke.app.ui.challenge.Store {
                override fun get(key: String): String? = sp.getString(key, null)
                override fun put(key: String, value: String?) {
                    sp.edit().apply { if (value == null) remove(key) else putString(key, value) }.apply()
                }
            }
            de.funke.app.ui.challenge.TaskPrefs.attach(store)
            de.funke.app.ui.challenge.TaskHistory.attach(store)
        }
    }

    /** Wird hochgezählt, wenn sich Aufgaben-Vorlieben ändern (damit die Oberfläche neu zeichnet). */
    var taskPrefsVersion by mutableStateOf(0)
        private set

    fun setTopicPref(topic: de.funke.app.ui.challenge.Topic, pref: de.funke.app.ui.challenge.TopicPref) {
        de.funke.app.ui.challenge.TaskPrefs.setTopic(topic, pref)
        taskPrefsVersion++
    }

    fun setKindPref(kind: de.funke.app.ui.challenge.TaskKind, pref: de.funke.app.ui.challenge.KindPref) {
        de.funke.app.ui.challenge.TaskPrefs.setKind(kind, pref)
        taskPrefsVersion++
    }

    fun setSubjectOn(subject: de.funke.app.ui.challenge.Subject, on: Boolean) {
        // Mindestens ein Fach bleibt an
        if (!on && de.funke.app.ui.challenge.PracticeSubjects.count { de.funke.app.ui.challenge.TaskPrefs.subjectOn(it) } <= 1) return
        de.funke.app.ui.challenge.TaskPrefs.setSubjectOn(subject, on)
        taskPrefsVersion++
    }

    /** Fach in "Gemischt" an oder aus (seit 15.2). Mindestens ein Fach bleibt im Mix. */
    fun setInMix(subject: de.funke.app.ui.challenge.Subject, on: Boolean): Boolean {
        if (!on && de.funke.app.ui.challenge.TaskPrefs.mixSubjects().let { it.size <= 1 && subject in it }) return false
        de.funke.app.ui.challenge.TaskPrefs.setInMix(subject, on)
        taskPrefsVersion++
        return true
    }

    /** Alle eingeschalteten Fächer in den Mix. */
    fun mixAll() {
        de.funke.app.ui.challenge.PracticeSubjects.forEach { de.funke.app.ui.challenge.TaskPrefs.setInMix(it, true) }
        taskPrefsVersion++
    }

    fun resetTaskPrefs(subject: de.funke.app.ui.challenge.Subject?) {
        de.funke.app.ui.challenge.TaskPrefs.reset(subject)
        taskPrefsVersion++
    }

    /** Einstellungen sollen direkt auf dieser Seite aufgehen (z. B. "tasks:physics" aus dem Aufgaben-Reiter). */
    var settingsRequest by mutableStateOf<String?>(null)

    init {
        // 10.1: neue Schieberegler für Start und Abbrechen einmal als Form setzen (danach frei wählbar)
        if (controlsFresh) {
            prefs?.edit()
                ?.putString(K_START_STYLE, startButtonStyle.id)
                ?.putString(K_CANCEL_STYLE, cancelStyle.id)
                ?.putInt(K_CONTROLS_V, CONTROLS_VERSION)
                ?.apply()
        }
        // Denkpause: Einstellungen laden (die Bedienungshilfe lädt sie im selben Prozess auch selbst)
        prefs?.let { de.funke.app.gate.Gate.load(it) }
        // 11.0: Wer noch den alten Standard-Ring hatte, bekommt die Strahlen (einmalig)
        if ((prefs?.getInt(K_LOOK_V, 0) ?: 1) < 1) {
            if (prefs?.getString(K_RING, null) == RingStyle.Classic.id) ringStyle = RingStyle.Rays
            prefs?.edit()?.putInt(K_LOOK_V, 1)?.putString(K_RING, ringStyle.id)?.apply()
        }
        // Seit 9.6 gibt es keinen eigenen KI-Schlüssel mehr: einen alten gespeicherten Schlüssel löschen.
        if (prefs?.contains(K_AI_KEY) == true) prefs.edit().remove(K_AI_KEY).apply()
        // Entfernte Schemata auf das ähnlichste neue abbilden.
        themeId = Palettes.migrate(themeId)
        // Passt das gespeicherte Farbschema nicht (mehr) zu Stil und Hell/Dunkel, das passende nehmen.
        if (!Palettes.fits(themeId, uiStyle, darkMode)) {
            themeId = rememberedTheme(uiStyle, darkMode)
        }
        modes.add(DefaultMode)
        prefs?.getString(K_MODES, null)?.let { json ->
            runCatching {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val pk = obj.getJSONArray("packages")
                    modes.add(
                        FocusMode(
                            id = obj.getString("id"),
                            customName = obj.getString("name"),
                            packages = List(pk.length()) { pk.getString(it) },
                        )
                    )
                }
            }
        }
        prefs?.getString(K_MODE, null)?.let { saved ->
            if (modes.any { it.id == saved }) selectedModeId = saved
        }
        prefs?.getString(K_DAYS, null)?.let { json ->
            runCatching {
                val obj = JSONObject(json)
                for (key in obj.keys()) dayMinutes[key] = obj.getInt(key)
            }
        }
        // Streak-Tage: bis 8.4 gab es nur ganze Sessions mit Minuten, also zählt jeder Tag mit Minuten.
        doneDays = if (prefs?.contains(Streaks.K_DONE) == true) {
            Streaks.parseSet(prefs.getString(Streaks.K_DONE, null))
        } else {
            dayMinutes.filterValues { it > 0 }.keys.toSet()
        }
        de.funke.app.ui.challenge.History.attach(prefs)
        applyFreezes()
        restoreSession()
    }

    /**
     * Wurde die App während einer Session beendet (z. B. vom System), geht es hier weiter:
     * Läuft die Zeit noch, öffnet sich wieder der Session-Bildschirm. Ist sie inzwischen um,
     * wird die Session als geschafft gezählt.
     */
    private fun restoreSession() {
        val end = prefs?.getLong(K_SESSION_END, 0L) ?: 0L
        if (end <= 0L) return
        val planned = prefs?.getInt(K_SESSION_MINUTES, minutes) ?: minutes
        prefs?.getString(K_SESSION_MODE, null)?.let { id -> if (modes.any { it.id == id }) selectedModeId = id }
        sessionEndAt = end
        sessionMinutes = planned
        prefs?.let { SessionLock.load(it) }
        if (System.currentTimeMillis() < end) {
            screen = Screen.Focus
        } else {
            endSession(planned, ResultKind.Completed)
        }
    }

    // ----- abgeleitete Werte -----

    val selectedMode: FocusMode
        get() = modes.firstOrNull { it.id == selectedModeId } ?: DefaultMode

    val customModes: List<FocusMode>
        get() = modes.filter { !it.builtIn }

    fun minutesOn(date: LocalDate): Int = dayMinutes[date.toString()] ?: 0

    val todayMinutes: Int
        get() = minutesOn(LocalDate.now())

    private fun goalMet(date: LocalDate) = minutesOn(date) >= dailyGoal

    /** Ein Tag zählt für den Streak, sobald man an ihm mindestens eine Minute gelesen hat. */
    private fun readOn(date: LocalDate) = minutesOn(date) > 0

    /**
     * Tage in Folge, an denen gelesen wurde (egal wie lange).
     * Heute zählt mit, sobald heute gelesen wurde. Hat man heute noch nicht gelesen,
     * bleibt der Streak von gestern stehen, bis der Tag vorbei ist.
     */
    val streak: Int
        get() = Streaks.count(doneDays, frozenDays)

    /** Ist heute schon eine ganze Session geschafft (zählt für den Streak)? */
    val streakDoneToday: Boolean
        get() = LocalDate.now().toString() in doneDays

    /** Wurde dieser Tag mit Streak-Eis überbrückt? */
    fun frozenOn(date: LocalDate): Boolean = date.toString() in frozenDays

    /** Verpasste Tage mit Eis überbrücken (beim Start und wenn die App wieder nach vorne kommt). */
    fun applyFreezes() {
        val gifts = Streaks.parseSet(prefs?.getString(Streaks.K_GIFTS, null))
        val b = Streaks.bridgeAll(doneDays, frozenDays, streakFreezes, gifts)
        if (b.used > 0 || b.gift != null) {
            frozenDays = b.frozen
            streakFreezes -= b.used
            val e = prefs?.edit()
                ?.putString(Streaks.K_FROZEN, Streaks.joinSet(frozenDays))
                ?.putInt(Streaks.K_FREEZES, streakFreezes)
            if (b.gift != null) {
                e?.putString(Streaks.K_GIFTS, Streaks.joinSet(gifts + b.gift))
                giftNotice = true
            }
            e?.apply()
        }
    }

    fun dismissGiftNotice() { giftNotice = false }

    /** Wochentag 0 = Montag ... 6 = Sonntag. */
    val todayIndex: Int
        get() = LocalDate.now().dayOfWeek.value - 1

    private val weekStart: LocalDate
        get() = LocalDate.now().minusDays(todayIndex.toLong())

    fun weekDay(index: Int): DayStatus {
        val date = weekStart.plusDays(index.toLong())
        return DayStatus(
            done = goalMet(date),
            today = index == todayIndex,
            future = index > todayIndex,
        )
    }

    /** Gelesene Minuten an einem Tag dieser Woche (0 = Montag). */
    fun weekDayMinutes(index: Int): Int = minutesOn(weekStart.plusDays(index.toLong()))

    val weekMinutes: Int
        get() = (0..6).sumOf { minutesOn(weekStart.plusDays(it.toLong())) }

    /** An wie vielen der letzten 365 Tage gelesen wurde. */
    val daysReadLastYear: Int
        get() {
            val from = LocalDate.now().minusDays(364)
            return dayMinutes.count { (key, value) ->
                value > 0 && runCatching { !LocalDate.parse(key).isBefore(from) }.getOrDefault(false)
            }
        }

    // ----- Aktionen: Aussehen -----

    fun setTheme(id: String) {
        themeId = id
        prefs?.edit()?.putString(K_THEME, id)?.putString(themeKey(uiStyle, darkMode), id)?.apply()
    }

    /** Wechselt den Stil. Jeder Stil merkt sich sein eigenes zuletzt gewähltes Farbschema. */
    /** Modern und Flat teilen sich die Farbschemata, Cartoon hat eigene. */
    private fun themeKey(style: UiStyle, dark: Boolean): String =
        "theme_" + (if (style == UiStyle.Cartoon) "cartoon" else "modern") + (if (dark) "_dark" else "_light")

    /** Zuletzt gewähltes Schema für diesen Stil und Hell/Dunkel, sonst das Standard-Schema. */
    private fun rememberedTheme(style: UiStyle, dark: Boolean): String {
        val saved = prefs?.getString(themeKey(style, dark), null)?.let { Palettes.migrate(it) }
        return if (saved != null && Palettes.fits(saved, style, dark)) saved else Palettes.defaultFor(style, dark).id
    }

    fun changeUiStyle(style: UiStyle) {
        if (style == uiStyle) return
        prefs?.edit()?.putString(themeKey(uiStyle, darkMode), themeId)?.apply()
        uiStyle = style
        prefs?.edit()?.putString(K_STYLE, style.id)?.apply()
        if (!Palettes.fits(themeId, style, darkMode)) setTheme(rememberedTheme(style, darkMode))
    }

    /** Hell / Dunkel / System. Bei System folgt [darkMode] dem Gerät (siehe [syncDarkMode]). */
    var themeMode by mutableStateOf(
        ThemeMode.byId(prefs?.getString(K_THEME_MODE, null))
            ?: if (freshInstall) ThemeMode.System else if (darkMode) ThemeMode.Dark else ThemeMode.Light
    )
        private set

    init {
        // 14.0: Wer Glas mit dem alten Standard (Obsidian) im Dunkeln hatte, bekommt einmalig Indigo-Glas –
        // so sieht Glas jetzt standardmäßig aus. Eine eigene Wahl danach bleibt natürlich.
        if (prefs != null && !prefs.getBoolean(K_THEME_V14, false)) {
            if (!freshInstall && uiStyle == UiStyle.Glass && darkMode && themeId == Palettes.Obsidian.id) setTheme(Palettes.IndigoDark.id)
            prefs.edit().putBoolean(K_THEME_V14, true).apply()
        }
        // Neuinstallation: den neuen Standard-Look gleich speichern, damit er nach dem Intro bleibt
        if (freshInstall) {
            prefs?.edit()
                ?.putString(K_STYLE, uiStyle.id)
                ?.putString(K_THEME, themeId)
                ?.putString(K_THEME_MODE, themeMode.id)
                ?.putString(K_PATTERN, backgroundPattern.id)
                ?.apply()
        }
    }

    fun cycleThemeMode(systemDark: Boolean) {
        themeMode = themeMode.next
        prefs?.edit()?.putString(K_THEME_MODE, themeMode.id)?.apply()
        syncDarkMode(systemDark)
    }

    /** Stellt Hell/Dunkel passend zur Einstellung ein (bei "System" so wie das Gerät gerade ist). */
    fun syncDarkMode(systemDark: Boolean) {
        changeDarkMode(
            when (themeMode) {
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
                ThemeMode.System -> systemDark
            }
        )
    }

    fun changeDarkMode(dark: Boolean) {
        if (dark == darkMode) return
        prefs?.edit()?.putString(themeKey(uiStyle, darkMode), themeId)?.apply()
        darkMode = dark
        prefs?.edit()?.putBoolean(K_DARK, dark)?.apply()
        setTheme(rememberedTheme(uiStyle, dark))
    }

    fun updateRingStyle(style: RingStyle) {
        ringStyle = style
        prefs?.edit()?.putString(K_RING, style.id)?.apply()
    }

    fun updateSidekick(on: Boolean) {
        sidekick = on
        prefs?.edit()?.putBoolean(K_SIDEKICK, on)?.apply()
    }

    fun updateFilamentStyle(style: de.funke.app.ui.components.FilamentStyle) {
        filamentStyle = style
        prefs?.edit()?.putString(K_FILAMENT, style.id)?.apply()
    }

    fun updateStartAnimation(on: Boolean) {
        startAnimation = on
        prefs?.edit()?.putBoolean(K_INTRO, on)?.apply()
    }

    fun updateStartButtonStyle(style: StartButtonStyle) {
        startButtonStyle = style
        prefs?.edit()?.putString(K_START_STYLE, style.id)?.apply()
    }

    /** Über das Kreuz beenden: sofort, nach einer Bitte, oder streng (Bitte plus zwei große Seiten). */
    var quitConfirm by mutableStateOf(
        QuitConfirm.byId(prefs?.getString(K_QUIT_CONFIRM, null))
            ?: if (prefs?.getBoolean(K_STRICT_QUIT, true) != false) QuitConfirm.Strict else QuitConfirm.Once
    )
        private set

    fun updateQuitConfirm(value: QuitConfirm) {
        quitConfirm = value
        prefs?.edit()?.putString(K_QUIT_CONFIRM, value.id)?.apply()
    }

    /** Lumi auf dem Lese-Bildschirm. */
    var readingLumi by mutableStateOf(ReadingLumi.byId(prefs?.getString(K_READING_LUMI, null)))
        private set

    fun updateReadingLumi(value: ReadingLumi) {
        readingLumi = value
        prefs?.edit()?.putString(K_READING_LUMI, value.id)?.apply()
    }

    fun updateCancelStyle(style: CancelStyle) {
        cancelStyle = style
        prefs?.edit()?.putString(K_CANCEL_STYLE, style.id)?.apply()
    }

    fun updateBackgroundPattern(pattern: BackgroundPattern) {
        backgroundPattern = pattern
        prefs?.edit()?.putString(K_PATTERN, pattern.id)?.apply()
    }

    fun updateLanguage(language: AppLanguage) {
        Lang.set(language, prefs)
    }

    fun updateHapticLevel(level: Int) {
        hapticLevel = level.coerceIn(0, 3)
        prefs?.edit()?.putInt(K_HAPTIC, hapticLevel)?.apply()
    }

    fun updateHapticMode(mode: HapticMode) {
        hapticMode = mode
        prefs?.edit()?.putString(K_HAPTIC_MODE, mode.id)?.apply()
    }

    fun updateSound(on: Boolean) {
        soundOn = on
        prefs?.edit()?.putBoolean(K_SOUND, on)?.apply()
    }

    fun setTimerStyleOption(option: TimerStyleOption) {
        timerStyle = option
        prefs?.edit()?.putString(K_TIMER, option.id)?.apply()
    }

    fun openSettings() {
        settingsVisible = true
    }

    fun closeSettings() {
        settingsVisible = false
    }

    // ----- Aktionen: Modi -----

    fun selectMode(id: String) {
        if (modes.any { it.id == id }) selectedModeId = id
    }

    fun updateMinutes(value: Int) {
        minutes = value
    }

    /** [mode] = null öffnet den Editor für einen neuen Modus. */
    fun openEditor(mode: FocusMode?) {
        editorMode = mode
        editorNonce++
        editorVisible = true
    }

    fun closeEditor() {
        editorVisible = false
    }

    fun saveMode(existingId: String?, name: String, packages: List<String>) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        if (existingId == null) {
            val mode = FocusMode(UUID.randomUUID().toString(), cleanName, packages)
            modes.add(mode)
            selectedModeId = mode.id
        } else {
            val index = modes.indexOfFirst { it.id == existingId }
            if (index >= 0) modes[index] = modes[index].copy(customName = cleanName, packages = packages)
        }
        persistModes()
        editorVisible = false
    }

    fun deleteMode(id: String) {
        modes.removeAll { it.id == id && !it.builtIn }
        if (modes.none { it.id == selectedModeId }) selectedModeId = DefaultMode.id
        persistModes()
        editorVisible = false
    }

    // ----- Aktionen: Ziel -----

    fun updateDailyGoal(value: Int) {
        dailyGoal = value
        prefs?.edit()?.putInt(K_GOAL, value)?.apply()
        de.funke.app.widget.Widgets.refresh(appContext)
    }

    fun finishOnboarding() {
        // Zum Start gibt es ein Streak-Eis geschenkt (einmalig), das zweite verdient man sich.
        if (prefs?.getBoolean("starter_freeze", false) != true) {
            streakFreezes = maxOf(streakFreezes, 1)
            prefs?.edit()?.putInt(Streaks.K_FREEZES, streakFreezes)?.putBoolean("starter_freeze", true)?.apply()
        }
        onboarded = true
        prefs?.edit()?.putBoolean(K_ONBOARDED, true)?.putInt(K_ONBOARD_VERSION, ONBOARD_VERSION)?.apply()
    }

    fun updateStreakGoal(days: Int) {
        streakGoal = days.coerceAtLeast(0)
        prefs?.edit()?.putInt(K_STREAK_GOAL, streakGoal)?.apply()
        de.funke.app.widget.Widgets.refresh(appContext)
    }

    /** Einrichtung noch einmal zeigen (Einstellungen → Über). */
    fun restartOnboarding() {
        onboarded = false
        // Die Reiter erklären sich danach auch wieder
        tabIntrosSeen = emptySet()
        prefs?.edit()?.remove("tab_intros_seen")?.apply()
    }

    /** Nutzungslimit an (früher "Viel Videozeit"); Details in notify/ReminderStore. */
    var screenWarnOn by mutableStateOf(prefs?.getBoolean(de.funke.app.notify.ReminderStore.K_USAGE_ON, true) ?: true)
        private set
    var screenWarnLimit by mutableIntStateOf(prefs?.getInt(de.funke.app.notify.ReminderStore.K_USAGE_LIMIT, 120) ?: 120)
        private set

    // ----- Erinnerungen (seit 13.3: eingebaute + eigene + Nutzungslimit, siehe notify/ReminderStore) -----

    /** Wird hochgezählt, wenn sich eine Erinnerung ändert (damit die Oberfläche neu liest). */
    var remindersVersion by mutableIntStateOf(0)
        private set

    /** Alle Erinnerungen mit Uhrzeit: Lesen, Streak, Schlafenszeit, dann die eigenen. */
    fun reminders(): List<de.funke.app.notify.Reminder> =
        prefs?.let { de.funke.app.notify.ReminderStore.all(it) } ?: emptyList()

    fun usageLimit(): de.funke.app.notify.UsageLimit? = prefs?.let { de.funke.app.notify.ReminderStore.usage(it) }

    /** Speichert eine Erinnerung (eingebaut oder eigene) und plant neu. */
    fun saveReminder(r: de.funke.app.notify.Reminder) {
        val sp = prefs ?: return
        if (r.isCustom) de.funke.app.notify.ReminderStore.upsertCustom(sp, r)
        else de.funke.app.notify.ReminderStore.saveBuiltIn(sp, r)
        syncReminderFields()
        afterReminderChange()
    }

    fun deleteReminder(id: String) {
        val sp = prefs ?: return
        de.funke.app.notify.ReminderStore.deleteCustom(sp, id)
        afterReminderChange()
    }

    fun saveUsageLimit(u: de.funke.app.notify.UsageLimit) {
        val sp = prefs ?: return
        de.funke.app.notify.ReminderStore.saveUsage(sp, u)
        screenWarnOn = u.on
        screenWarnLimit = u.limitMin
        afterReminderChange()
    }

    private fun syncReminderFields() {
        val sp = prefs ?: return
        reminderOn = sp.getBoolean(K_REMINDER, true)
        reminderHour = sp.getInt(K_REMINDER_HOUR, 19)
        reminderMinute = sp.getInt(K_REMINDER_MINUTE, 0)
        streakWarnOn = sp.getBoolean(K_WARN, true)
        streakWarnHour = sp.getInt(K_WARN_HOUR, 21)
        streakWarnMinute = sp.getInt(K_WARN_MINUTE, 30)
    }

    private fun afterReminderChange() {
        remindersVersion++
        appContext?.let { de.funke.app.notify.Reminders.schedule(it) }
    }

    // ----- Aktionen: Session -----

    /** Startet eine Session. Ist die Sperre noch nicht eingerichtet, kommt zuerst die Einrichtung. */
    fun startSession() {
        if (!lockEnabled && prefs != null) {
            lockSetupVisible = true
            return
        }
        beginSession()
    }

    /** Startet ohne Sperre (nur für dieses Mal). */
    fun startWithoutLock() {
        lockSetupVisible = false
        beginSession()
    }

    fun closeLockSetup() {
        lockSetupVisible = false
    }

    fun updateLockEnabled(enabled: Boolean) {
        lockEnabled = enabled
    }

    private fun beginSession() {
        lockSetupVisible = false
        val end = System.currentTimeMillis() + minutes * 60_000L
        sessionEndAt = end
        sessionMinutes = minutes
        prefs?.edit()
            ?.putString(K_MODE, selectedModeId)
            ?.putInt(K_MINUTES, minutes)
            ?.putLong(K_SESSION_END, end)
            ?.putInt(K_SESSION_MINUTES, minutes)
            ?.putString(K_SESSION_MODE, selectedModeId)
            ?.apply()
        SessionLock.start(prefs, end, selectedMode.packages, selectedMode.name)
        result = null
        screen = Screen.Focus
    }

    fun endSession(readMinutes: Int, kind: ResultKind) {
        lastSessionKey = sessionEndAt
        SessionLock.stop(prefs)
        prefs?.edit()?.remove(K_SESSION_END)?.remove(K_SESSION_MINUTES)?.remove(K_SESSION_MODE)?.apply()
        sessionEndAt = 0L
        val streakBefore = streak
        val goalBefore = todayMinutes >= dailyGoal
        // Durchgezogen: Minuten + Streak. Mit gelöster Aufgabe früher beendet: nur die Minuten.
        // Über das Kreuz ohne Aufgabe beendet: zählt gar nicht.
        val credited = if (kind == ResultKind.Abandoned) 0 else readMinutes
        if (credited > 0) {
            dayMinutes[LocalDate.now().toString()] = todayMinutes + credited
            persistDays()
        }
        val freezeEarned = if (kind == ResultKind.Completed) markTodayDone(streakBefore) else false
        val streakAfter = streak
        result = SessionResult(
            plannedMinutes = if (sessionMinutes > 0) sessionMinutes else minutes,
            readMinutes = readMinutes,
            kind = kind,
            streakIncreased = streakAfter > streakBefore,
            goalReached = !goalBefore && todayMinutes >= dailyGoal,
            freezeEarned = freezeEarned,
        )
        de.funke.app.widget.Widgets.refresh(appContext)
        screen = Screen.Result
    }

    /**
     * Heute zählt für den Streak (ganze Session geschafft oder genug im Buch gelesen).
     * Gibt zurück, ob dabei ein Streak-Eis verdient wurde.
     */
    private fun markTodayDone(streakBefore: Int): Boolean {
        var freezeEarned = false
        val today = LocalDate.now().toString()
        if (today !in doneDays) {
            doneDays = doneDays + today
            prefs?.edit()?.putString(Streaks.K_DONE, Streaks.joinSet(doneDays))?.apply()
        }
        val now = streak
        val awardedAt = prefs?.getInt(Streaks.K_FREEZE_AT, 0) ?: 0
        if (now > streakBefore && now % Streaks.FREEZE_EVERY == 0 && now != awardedAt && streakFreezes < Streaks.MAX_FREEZES) {
            streakFreezes++
            freezeEarned = true
            prefs?.edit()?.putInt(Streaks.K_FREEZES, streakFreezes)?.putInt(Streaks.K_FREEZE_AT, now)?.apply()
        }
        if (now > bestStreak) {
            bestStreak = now
            prefs?.edit()?.putInt(K_BEST, now)?.apply()
        }
        return freezeEarned
    }

    // ----- Bücher (seit 13.4) -----

    /** Offenes Buch im Reader (null = keins). */
    var openBookId by mutableStateOf<String?>(null)
        private set

    fun openBook(id: String) {
        openBookId = id
    }

    fun closeBook() {
        openBookId = null
    }

    /** Sprung im offenen Buch (Abschnitt, Stelle), z. B. von einer Notiz aus. Der Reader holt ihn ab. */
    var readerJump by mutableStateOf<Pair<Int, Int>?>(null)

    /** Nach dem Schließen eines Buchs: wie viele Minuten es diesmal waren (für einen freundlichen Satz im Regal). */
    var lastBookMinutes by mutableStateOf<Pair<String, Int>?>(null)

    /** Heute im Buch gelesene Minuten (für den Streak). Gespeichert als "Datum:Minuten". */
    var bookMinutesToday by mutableIntStateOf(
        prefs?.getString(K_BOOK_DAY, null)?.split(':')?.takeIf { it.getOrNull(0) == LocalDate.now().toString() }?.getOrNull(1)?.toIntOrNull() ?: 0
    )
        private set

    /**
     * Eine Minute im Buch gelesen: zählt für das Tagesziel wie eine Lesezeit. Ab [BOOK_STREAK_MINUTES] Minuten
     * an einem Tag zählt der Tag auch für den Streak.
     */
    fun creditBookMinute(bookId: String) {
        val today = LocalDate.now().toString()
        val stored = prefs?.getString(K_BOOK_DAY, null)?.split(':')
        val before = if (stored?.getOrNull(0) == today) stored.getOrNull(1)?.toIntOrNull() ?: 0 else 0
        bookMinutesToday = before + 1
        prefs?.edit()?.putString(K_BOOK_DAY, "$today:$bookMinutesToday")?.apply()
        dayMinutes[today] = todayMinutes + 1
        persistDays()
        de.funke.app.books.Library.addMinute(bookId)
        if (bookMinutesToday >= BOOK_STREAK_MINUTES && today !in doneDays) markTodayDone(streak)
        de.funke.app.widget.Widgets.refresh(appContext)
    }

    /** Modus wählen und direkt mit der zuletzt eingestellten Zeit starten. */
    fun startWithMode(id: String) {
        selectMode(id)
        startSession()
    }

    fun backToMain() {
        screen = Screen.Main
    }

    // ----- Speichern -----

    private fun persistModes() {
        val array = JSONArray()
        modes.filter { !it.builtIn }.forEach { mode ->
            array.put(
                JSONObject()
                    .put("id", mode.id)
                    .put("name", mode.customName)
                    .put("packages", JSONArray(mode.packages))
            )
        }
        prefs?.edit()?.putString(K_MODES, array.toString())?.apply()
    }

    private fun persistDays() {
        val obj = JSONObject()
        dayMinutes.forEach { (key, value) -> obj.put(key, value) }
        prefs?.edit()?.putString(K_DAYS, obj.toString())?.apply()
    }
}

/** Aussehen des Notiz-Blatts (Einstellungen → Notizen & KI). */
enum class NotePaper(val id: String, private val de: String, private val en: String) {
    Plain("plain", "Schlicht", "Plain"),
    Lines("lines", "Linien", "Lines"),
    Notebook("notebook", "Heft", "Notebook"),
    Grid("grid", "Kästchen", "Grid"),
    Dots("dots", "Punkte", "Dots");

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?): NotePaper = entries.firstOrNull { it.id == id } ?: Notebook
    }
}
