package de.funke.app.notes

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Diktieren mit der Spracherkennung, die schon auf dem Handy ist (Android SpeechRecognizer).
 * Keine eigene KI und kein API-Schlüssel nötig.
 *
 * Seit 9.5 zuerst der normale Erkennungsdienst (meist Google, mit Internet deutlich genauer als das
 * kleine Sprachpaket auf dem Handy). Nur ohne Netz wird auf die Erkennung direkt auf dem Handy gewechselt.
 * Ab Android 13 zusätzlich: Satzzeichen/Großschreibung vom Dienst ("Formatting") und eine Liste typischer
 * Mathe-Wörter, die der Dienst bevorzugt erkennen soll ("Biasing"). Kann der Dienst das nicht, geht es ohne.
 *
 * Solange man nicht auf Stopp tippt, hört sie weiter zu. Jeder fertige Satz geht an [onText]
 * (gesprochene Formeln schon als Zeichen, siehe [SpokenMath]). Die Sprache ist unabhängig von der App-Sprache.
 */
class Dictation(private val context: Context, private val languageTag: String) {
    private val german = languageTag.startsWith("de")
    var listening by mutableStateOf(false)
        private set
    /** Lautstärke 0..1 (für die Animation). */
    var level by mutableFloatStateOf(0f)
        private set
    /** Was gerade erkannt wird (noch nicht fertig). */
    var partial by mutableStateOf("")
        private set
    var error by mutableStateOf<String?>(null)
        private set
    /** Pausiert: hört gerade nicht zu, die Diktier-Runde ist aber noch offen. */
    var paused by mutableStateOf(false)
        private set

    var onText: (String) -> Unit = {}

    private var recognizer: SpeechRecognizer? = null
    private var wanted = false
    /** Wie oft hintereinander nur Stille kam (nach ein paar Mal hört die Erkennung von selbst auf). */
    private var silent = 0
    private val onDeviceAvailable = Build.VERSION.SDK_INT >= 31 && runCatching { SpeechRecognizer.isOnDeviceRecognitionAvailable(context) }.getOrDefault(false)
    private val serviceAvailable = runCatching { SpeechRecognizer.isRecognitionAvailable(context) }.getOrDefault(false)
    /** Erkennung auf dem Handy nur, wenn es keinen normalen Dienst gibt (oder kein Netz da ist). */
    private var onDevice = !serviceAvailable && onDeviceAvailable
    /** Android-13-Extras (Formatierung, Mathe-Wörter) – fallen weg, wenn der Dienst damit nicht klarkommt. */
    private var extras = Build.VERSION.SDK_INT >= 33

    val available: Boolean get() = serviceAvailable || onDeviceAvailable

    private val bias: ArrayList<String> = ArrayList(
        if (german) listOf(
            "Integral", "Quadrat", "hoch", "Wurzel", "Wurzel aus", "Ableitung", "f Strich", "f von x", "Funktion", "Gleichung",
            "Sinus", "Kosinus", "Tangens", "Logarithmus", "Exponent", "Pi", "Delta", "Lambda", "Omega", "Klammer auf", "Klammer zu",
            "gleich", "plus", "minus", "mal", "geteilt durch", "Bruch", "Vektor", "Matrix", "Grenzwert", "unendlich", "Prozent",
            "Newton", "Joule", "Energie", "Masse", "Beschleunigung", "Geschwindigkeit",
        ) else listOf(
            "integral", "squared", "cubed", "to the power of", "square root", "derivative", "f prime", "f of x", "function", "equation",
            "sine", "cosine", "tangent", "logarithm", "exponent", "pi", "delta", "lambda", "omega", "open bracket", "close bracket",
            "equals", "plus", "minus", "times", "divided by", "fraction", "vector", "matrix", "limit", "infinity", "percent",
            "Newton", "joule", "energy", "mass", "acceleration", "velocity",
        ),
    )

    fun toggle() = if (listening || wanted) stop() else start()

    fun start() {
        error = null
        silent = 0
        wanted = true
        paused = false
        begin()
    }

    fun stop() {
        wanted = false
        paused = false
        runCatching { recognizer?.stopListening() }
        listening = false
        level = 0f
        partial = ""
    }

    /** Kurz anhalten (was schon gesagt wurde, kommt noch an). [start] macht weiter. */
    fun pause() {
        if (!listening && !wanted) return
        wanted = false
        runCatching { recognizer?.stopListening() }
        listening = false
        level = 0f
        paused = true
    }

    fun release() {
        wanted = false
        runCatching { recognizer?.destroy() }
        recognizer = null
        listening = false
    }

    private fun create(): SpeechRecognizer? = runCatching {
        if (onDevice && Build.VERSION.SDK_INT >= 31) SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
        else SpeechRecognizer.createSpeechRecognizer(context)
    }.getOrNull()?.also { it.setRecognitionListener(listener) }

    private fun begin() {
        val r = recognizer ?: create().also { recognizer = it }
        if (r == null) {
            error = de.funke.app.ui.tr("Spracherkennung ist auf diesem Handy nicht verfügbar.", "Speech recognition isn't available on this phone.")
            wanted = false
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
            .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            .putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
            .putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        if (extras && Build.VERSION.SDK_INT >= 33) {
            intent.putExtra(RecognizerIntent.EXTRA_ENABLE_FORMATTING, RecognizerIntent.FORMATTING_OPTIMIZE_QUALITY)
            intent.putStringArrayListExtra(RecognizerIntent.EXTRA_BIASING_STRINGS, bias)
        }
        runCatching { r.startListening(intent) }
            .onSuccess { listening = true }
            .onFailure { listening = false; wanted = false }
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { listening = true }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {
            // grob -2 dB (still) bis 10 dB (laut)
            level = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
        }
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { level = 0f }

        override fun onError(code: Int) {
            level = 0f
            partial = ""
            // Selbst gestoppt oder pausiert: Fehler danach (z. B. "Client" nach stopListening) sind keine
            // echten Fehler und werden nicht angezeigt.
            if (!wanted) {
                listening = false
                return
            }
            // Erkennung noch mit der letzten Runde beschäftigt: kurz warten, dann weiter
            if (code == SpeechRecognizer.ERROR_RECOGNIZER_BUSY && ++silent <= 4) {
                runCatching { recognizer?.cancel() }
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ if (wanted) begin() }, 250)
                return
            }
            // Der Dienst kommt mit den Android-13-Extras nicht klar: ohne sie nochmal
            if (extras && wanted && (code == SpeechRecognizer.ERROR_CLIENT || code == 12 || code == 13 || code == 14)) {
                extras = false
                runCatching { recognizer?.destroy() }
                recognizer = null
                begin()
                return
            }
            // Kein Netz: auf die Erkennung direkt auf dem Handy wechseln (falls das Sprachpaket da ist)
            if (!onDevice && onDeviceAvailable && wanted &&
                (code == SpeechRecognizer.ERROR_NETWORK || code == SpeechRecognizer.ERROR_NETWORK_TIMEOUT || code == SpeechRecognizer.ERROR_SERVER)
            ) {
                onDevice = true
                runCatching { recognizer?.destroy() }
                recognizer = null
                begin()
                return
            }
            // Sprachpaket fürs Erkennen auf dem Handy fehlt: zurück zum normalen Dienst
            if (onDevice && serviceAvailable && (code == 12 || code == 13 || code == SpeechRecognizer.ERROR_CLIENT)) {
                onDevice = false
                runCatching { recognizer?.destroy() }
                recognizer = null
                if (wanted) { begin(); return }
            }
            if (wanted && (code == SpeechRecognizer.ERROR_NO_MATCH || code == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) && ++silent <= 4) {
                // Nur still gewesen: weiter zuhören, bis man selbst stoppt (nach langer Stille ist Schluss)
                begin()
                return
            }
            listening = false
            wanted = false
            paused = false
            error = when (code) {
                SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> null   // einfach still gewesen
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> de.funke.app.ui.tr("Lumina darf das Mikrofon nicht benutzen.", "Lumina isn't allowed to use the microphone.")
                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                    de.funke.app.ui.tr("Kein Internet für die Spracherkennung.", "No internet for speech recognition.")
                else -> de.funke.app.ui.tr("Das hat nicht geklappt. Tipp nochmal aufs Mikrofon.", "That didn't work. Tap the microphone again.")
            }
        }

        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
            silent = 0
            partial = ""
            level = 0f
            // Erst einmal so, wie es gesagt wurde (nur Formeln werden zu Zeichen). Aufräumen kommt auf Knopfdruck.
            val converted = SpokenMath.convert(text, german)
            if (converted.isNotBlank()) onText(converted)
            // weiter zuhören, bis man selbst stoppt
            if (wanted) begin() else listening = false
        }

        override fun onPartialResults(partialResults: Bundle?) {
            partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}

@Composable
fun rememberDictation(languageTag: String): Dictation {
    val context = LocalContext.current
    val d = remember(languageTag) { Dictation(context.applicationContext, languageTag) }
    DisposableEffect(d) { onDispose { d.release() } }
    return d
}
