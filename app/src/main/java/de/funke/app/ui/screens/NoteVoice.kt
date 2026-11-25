package de.funke.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import de.funke.app.R
import de.funke.app.notes.AiCleanup
import de.funke.app.notes.Dictation
import de.funke.app.notes.NoteCleaner
import de.funke.app.notes.rememberDictation
import de.funke.app.ui.AppState
import de.funke.app.ui.Lang
import de.funke.app.ui.components.ModalWindow
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.VoiceState
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Diktieren und Aufräumen für eine Notiz. Seit 15.5 derselbe Ablauf im Vollbild-Editor und in der Notiz beim
 * Lesen (vorher konnte man im Buch nur tippen).
 *
 *  - Diktieren: [mic] fragt einmal nach der Mikrofon-Erlaubnis und öffnet die große Sprach-Ansicht
 *    ([NoteVoiceOverlays]). Was verstanden wird, kommt als neue Zeile ans Ende; "Verwerfen" stellt den Text von
 *    vorher wieder her.
 *  - Aufräumen: Blatt "Als Text / Als Stichpunkte", beim ersten Mal die Einwilligung, dann die Lumina-KI (oder
 *    auf dem Handy). Die Fassung davor bleibt bei der Notiz gespeichert ("Rückgängig", "Andere Fassung").
 *
 * Der Text gehört dem Aufrufer: [bind] sagt bei jeder Zusammensetzung, wie er gelesen, gesetzt und gespeichert wird.
 */
@Stable
class NoteVoice internal constructor(
    private val state: AppState,
    private val scope: CoroutineScope,
    private val context: Context,
    original: String?,
) {
    // ----- vom Aufrufer -----
    private var read: () -> String = { "" }
    private var write: (String) -> Unit = {}
    private var save: (String) -> Unit = {}
    private var saveOriginal: (String) -> Unit = {}
    private var markVoice: () -> Unit = {}

    /** Wie der Text dieser Notiz gelesen, gesetzt und gespeichert wird (bei jeder Zusammensetzung aufrufen). */
    fun bind(
        read: () -> String,
        write: (String) -> Unit,
        save: (String) -> Unit,
        saveOriginal: (String) -> Unit,
        markVoice: () -> Unit,
    ) {
        this.read = read
        this.write = write
        this.save = save
        this.saveOriginal = saveOriginal
        this.markVoice = markVoice
    }

    /** Die Spracherkennung (wechselt mit der Diktiersprache). */
    var dictation: Dictation? = null
        internal set

    /** Lumi räumt gerade auf (Verbinden, Nachdenken, Fertig) – sonst null. */
    var busy by mutableStateOf<VoiceState?>(null)
        private set
    var notice by mutableStateOf<Notice?>(null)
        internal set
    /** Die andere Fassung (vor bzw. nach dem Aufräumen), dauerhaft bei der Notiz. */
    var original by mutableStateOf(original)
        private set
    private var undo by mutableStateOf<String?>(null)
    var micDenied by mutableStateOf(false)
        private set
    /** Aufnahmezeit dieser Diktier-Runde in Sekunden (ohne Pausen). */
    var seconds by mutableIntStateOf(0)
        internal set
    /** Was in dieser Diktier-Runde gesagt wurde (für die große Ansicht). */
    var sessionText by mutableStateOf("")
        private set
    private var beforeSession: String? = null
    private var cleanupJob: Job? = null

    internal var askConsent by mutableStateOf(false)
    internal var choosing by mutableStateOf(false)
    internal var showPrivacy by mutableStateOf(false)
    internal var requestMic: () -> Unit = {}
    internal var micCenter by mutableStateOf(Offset.Zero)
    internal val stageMorph = Animatable(0f)

    val listening: Boolean get() = dictation?.let { it.listening || it.paused } == true
    /** Die große Ansicht ist offen: beim Diktieren oder während Lumi aufräumt. */
    val stageOpen: Boolean get() = listening || busy != null
    /** Ob dort nach der Notiz ein "Rückgängig" wartet (dann bleibt "Andere Fassung" noch weg). */
    val undoPending: Boolean get() = notice?.undo == true

    internal fun heard(t: String) {
        sessionText = if (sessionText.isBlank()) t else sessionText.trimEnd() + " " + t
        val cur = read()
        val next = when {
            cur.isBlank() -> t
            cur.endsWith("\n") -> cur + t
            else -> cur.trimEnd() + "\n" + t
        }
        write(next)
        markVoice()
    }

    private fun beginSession() {
        sessionText = ""
        seconds = 0
        beforeSession = read()
        dictation?.start()
    }

    internal fun permissionResult(ok: Boolean) {
        micDenied = !ok
        if (ok) beginSession()
    }

    /** Mikrofon: beim ersten Mal nach der Erlaubnis fragen, dann zuhören (oder aufhören, falls es schon zuhört). */
    fun mic() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (listening) dictation?.stop() else beginSession()
        } else requestMic()
    }

    fun stop() {
        dictation?.stop()
    }

    /** "Verwerfen": Was in dieser Runde gesagt wurde, kommt wieder weg. */
    fun discardSession() {
        dictation?.stop()
        beforeSession?.let { write(it) }
        sessionText = ""
    }

    fun cancelCleanup() {
        cleanupJob?.cancel()
        cleanupJob = null
        busy = null
        undo?.let { write(it) }
        undo = null
    }

    fun cleanUp(skipConsent: Boolean = false) {
        val before = read()
        if (before.isBlank() || busy != null) return
        if (!skipConsent && state.aiProvider == AiCleanup.Provider.Lumina && !state.aiConsent) {
            askConsent = true
            return
        }
        dictation?.stop()
        val format = state.noteFormat
        val german = state.dictationTag.startsWith("de")
        val ui = Lang.german
        cleanupJob = scope.launch {
            undo = before
            notice = null
            val local = {
                if (format == AiCleanup.Format.Bullets) NoteCleaner.tidy(before, german) else NoteCleaner.tidyText(before, german)
            }
            var done: Notice? = null
            val result = if (state.aiReady) {
                busy = VoiceState.Connecting
                val think = launch { delay(500); if (busy == VoiceState.Connecting) busy = VoiceState.Thinking }
                val r = AiCleanup.tidyLumina(before, german, state.aiServer, state.deviceId, format)
                think.cancel()
                r.fold(
                    onSuccess = { reply ->
                        state.aiLeft = reply.left
                        if (reply.text != null) {
                            done = Notice(R.drawable.ic_sparkles, tr("Aufgeräumt", "Cleaned up"), aiDetail(reply, ui), NoticeTone.Success, undo = true)
                            reply.text
                        } else {
                            done = Notice(
                                if (reply.fallbackReason == "device_limit" || reply.fallbackReason == "budget") R.drawable.ic_timer else R.drawable.ic_alert,
                                tr("Auf dem Handy aufgeräumt", "Tidied on the phone"),
                                when (reply.fallbackReason) {
                                    "device_limit" -> tr("Deine KI-Durchgänge für heute sind aufgebraucht – morgen geht's weiter.", "No AI runs left today – back tomorrow.")
                                    "budget" -> tr("Lumi macht für heute Pause.", "Lumi is resting for today.")
                                    else -> tr("Lumi nicht erreichbar: ", "Lumi unavailable: ") + AiCleanup.reasonLabel(reply.fallbackReason, ui)
                                },
                                NoticeTone.Warning,
                                undo = true,
                            )
                            local()
                        }
                    },
                    onFailure = {
                        done = Notice(
                            R.drawable.ic_wifi_off,
                            tr("Auf dem Handy aufgeräumt", "Tidied on the phone"),
                            tr("Kein Netz – Lumi war nicht erreichbar.", "No connection – Lumi couldn't be reached."),
                            NoticeTone.Warning,
                            undo = true,
                        )
                        local()
                    },
                )
            } else {
                busy = VoiceState.Thinking
                delay(900)
                done = Notice(R.drawable.ic_check, tr("Aufgeräumt", "Cleaned up"), tr("Auf dem Handy, ohne KI", "On the phone, without AI"), NoticeTone.Success, undo = true)
                local()
            }
            write(result)
            // Die Fassung davor bleibt bei der Notiz gespeichert (auch nach dem Schließen zurückzuholen)
            save(result)
            original = before
            saveOriginal(before)
            // Lumi freut sich kurz, dann geht die Ansicht zu
            busy = VoiceState.Speaking
            delay(900)
            busy = null
            cleanupJob = null
            notice = done
        }
    }

    /** Zwischen aufgeräumter und vorheriger Fassung tauschen. Nichts geht verloren: Die andere bleibt gespeichert. */
    fun swapVersions(restored: Boolean) {
        val other = original ?: undo ?: return
        val current = read()
        write(other)
        original = current
        save(other)
        saveOriginal(current)
        undo = null
        notice = if (restored) Notice(
            R.drawable.ic_undo, tr("Wiederhergestellt", "Restored"),
            tr("Die aufgeräumte Fassung bleibt bei der Notiz gespeichert.", "The tidied version stays saved with the note."),
        ) else Notice(
            R.drawable.ic_rotate, tr("Fassung getauscht", "Version swapped"),
            tr("Nochmal antippen holt die andere zurück.", "Tap again to bring the other one back."),
        )
    }

    fun undoCleanup() = swapVersions(restored = true)

    internal fun noticeExpired(n: Notice) {
        if (notice?.key == n.key) {
            notice = null
            if (n.undo) undo = null
        }
    }
}

/**
 * Diktieren und Aufräumen für eine Notiz bereitstellen. [key] wechselt mit der Notiz (neue Notiz = frischer Ablauf),
 * [original] ist die gespeicherte andere Fassung dieser Notiz.
 */
@Composable
fun rememberNoteVoice(state: AppState, original: String?, key: Any? = Unit): NoteVoice {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val voice = remember(key) { NoteVoice(state, scope, context, original) }
    val dictation = rememberDictation(state.dictationTag)
    voice.dictation = dictation
    dictation.onText = { t -> voice.heard(t) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> voice.permissionResult(ok) }
    voice.requestMic = { launcher.launch(Manifest.permission.RECORD_AUDIO) }
    LaunchedEffect(dictation.listening) {
        while (dictation.listening) {
            delay(1000)
            if (dictation.listening) voice.seconds++
        }
    }
    // Meldungen verschwinden von allein; mit "Rückgängig" erst, wenn die Zeit dafür abgelaufen ist
    val shown = voice.notice
    LaunchedEffect(shown?.key) {
        val n = shown ?: return@LaunchedEffect
        delay(if (n.undo) n.undoMillis.toLong() else NOTICE_MILLIS)
        voice.noticeExpired(n)
    }
    DisposableEffect(voice) { onDispose { voice.stop() } }
    return voice
}

/**
 * Unten an der Notiz: Meldung (falls da), darunter die Leiste mit Sprache, Mikrofon und "Aufräumen" ([NoteDock]).
 * Das Mikrofon merkt sich, wo es sitzt – daraus wächst die große Sprach-Ansicht.
 */
@Composable
fun NoteVoiceDock(voice: NoteVoice, state: AppState, hasText: Boolean, modifier: Modifier = Modifier) {
    val german = state.dictationTag.startsWith("de")
    val dictation = voice.dictation
    val error = dictation?.error
    val hint = remember(voice.micDenied, error) {
        when {
            voice.micDenied -> Notice(R.drawable.ic_mic_off, tr("Ohne Mikrofon-Erlaubnis geht nur Tippen.", "Without microphone access you can only type."), tone = NoticeTone.Warning)
            error != null -> Notice(R.drawable.ic_alert, error, tone = NoticeTone.Warning)
            else -> null
        }
    }
    val still = reducedMotion
    Column(modifier.fillMaxWidth()) {
        NoticeBar(voice.notice ?: hint, onUndo = { voice.undoCleanup() })
        NoteDock(
            language = if (german) "Deutsch" else "English",
            onLanguage = { state.updateDictationLang(if (german) "en" else "de") },
            hasText = hasText,
            working = voice.busy != null,
            onClean = { voice.choosing = true },
            onMic = { voice.mic() },
            micAvailable = dictation?.available == true,
            micModifier = Modifier
                // Während die Sprach-Ansicht aus dem Knopf wächst, ist der Knopf selbst unsichtbar
                .graphicsLayer { alpha = if (voice.stageMorph.value > 0.001f && !still && voice.busy == null) 0f else 1f }
                .onGloballyPositioned { c ->
                    val pos = c.positionInRoot()
                    voice.micCenter = Offset(pos.x + c.size.width / 2f, pos.y + c.size.height / 2f)
                },
        )
    }
}

/**
 * Was über allem liegt: das Aufräumen-Blatt, die Einwilligung beim ersten Mal, die Datenschutzerklärung und die
 * große Sprach-Ansicht. Muss im ganzen Bildschirm stehen (nicht in einem kleinen Feld). Zurück schließt zuerst die
 * Sprach-Ansicht (bzw. bricht das Aufräumen ab).
 */
@Composable
fun NoteVoiceOverlays(voice: NoteVoice, state: AppState) {
    val p = Funke.colors
    CleanupSheet(
        visible = voice.choosing,
        current = state.noteFormat,
        onPick = { f ->
            voice.choosing = false
            state.updateNoteFormat(f)
            voice.cleanUp()
        },
        onDismiss = { voice.choosing = false },
    )

    // Einmal fragen, bevor eine Notiz an Lumi geht
    if (voice.askConsent) {
        ModalWindow(onDismiss = { voice.askConsent = false }) {
            Text(tr("Mit Lumi aufräumen?", "Clean up with Lumi?"), style = MaterialTheme.typography.titleLarge, color = p.text)
            Spacer(Modifier.height(Space.s3))
            Text(
                tr(
                    "Dafür wird der Text dieser Notiz an Lumi geschickt (läuft bei Cloudflare), dort von einer KI aufgeräumt und sofort zurückgegeben. " +
                        "Lumina speichert dort nur einen Zähler pro Handy, keine Notizen. Gratis und ohne Anmeldung. " +
                        "Du kannst das jederzeit unter Einstellungen → Notizen ausschalten.",
                    "The text of this note is sent to Lumi (running on Cloudflare), tidied up by an AI and sent straight back. " +
                        "Lumina only keeps a counter per phone there, no notes. Free, no sign-up. " +
                        "You can switch this off any time in Settings → Notes.",
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = p.textSecondary,
            )
            TextAction(
                tr("Datenschutzerklärung lesen", "Read the privacy policy"),
                onClick = { voice.showPrivacy = true },
                modifier = Modifier.offset(x = -Space.s3),
            )
            Spacer(Modifier.height(Space.s4))
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s3), modifier = Modifier.fillMaxWidth()) {
                PillButton(
                    tr("Nur auf dem Handy", "Phone only"),
                    onClick = {
                        close {
                            voice.askConsent = false
                            state.updateAi(AiCleanup.Provider.Off)
                            voice.cleanUp(skipConsent = true)
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                PillButton(
                    tr("Aufräumen", "Clean up"),
                    filled = true,
                    onClick = {
                        close {
                            voice.askConsent = false
                            state.updateAiConsent(true)
                            voice.cleanUp(skipConsent = true)
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
    if (voice.showPrivacy) {
        LegalSheet(LegalDoc.Privacy, onClose = { voice.showPrivacy = false })
    }

    // Sprach-Ansicht: Das Mikrofon fliegt in einem Bogen nach oben und wird zur Sprach-Animation,
    // unten kommen die Knöpfe nacheinander dazu. Schließen läuft dieselbe Bewegung rückwärts, schneller.
    val stageOpen = voice.stageOpen
    val still = reducedMotion
    BackHandler(enabled = stageOpen) { if (voice.busy != null) voice.cancelCleanup() else voice.stop() }
    LaunchedEffect(stageOpen) {
        voice.stageMorph.animateTo(
            if (stageOpen) 1f else 0f,
            if (still) tween(Motion.Base, easing = Motion.EaseOut)
            else tween(if (stageOpen) STAGE_OPEN_MS else STAGE_CLOSE_MS, easing = LinearEasing),
        )
    }
    val dictation = voice.dictation
    if (voice.stageMorph.value > 0.001f && dictation != null) {
        val origin = if (voice.busy != null || voice.micCenter == Offset.Zero || still) null else voice.micCenter
        VoiceStage(
            state = state,
            t = voice.stageMorph.value,
            origin = origin,
            listening = dictation.listening,
            paused = dictation.paused,
            busy = voice.busy,
            level = { dictation.level },
            seconds = voice.seconds,
            said = voice.sessionText,
            partial = dictation.partial,
            onPause = { dictation.pause() },
            onResume = { dictation.start() },
            onDone = { dictation.stop() },
            onDiscard = { voice.discardSession() },
            onCancelCleanup = { voice.cancelCleanup() },
        )
    }
}

/**
 * Zeile unter "Aufgeräumt": welche KI-Stufe geantwortet hat und wie oft heute noch.
 * Beispiele: "Beste KI, noch 2× mit der besten KI heute", "Standard-KI, noch 11× heute".
 */
private fun aiDetail(reply: AiCleanup.LuminaReply, german: Boolean): String {
    fun t(de: String, en: String) = if (german) de else en
    val tier = AiCleanup.tierLabel(reply.tier, reply.model, german)
    val left = reply.left
    val best = reply.best
    val rest = when {
        left == 0 -> t("das war der letzte KI-Durchgang für heute", "that was today's last AI run")
        best != null && best > 0 -> t("noch ${best}× mit der besten KI heute", "${best}× left with the best AI today")
        best == 0 && reply.tier == "big" -> t("ab jetzt Standard-KI für heute", "standard AI from now on today")
        left != null -> t("noch ${left}× heute", "${left}× left today")
        else -> null
    }
    return listOfNotNull(tier, rest).joinToString(", ").replaceFirstChar { it.uppercase() }
}
