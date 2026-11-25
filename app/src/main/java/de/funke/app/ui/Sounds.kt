package de.funke.app.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import de.funke.app.R
import java.util.concurrent.ConcurrentHashMap

/** Die Töne der App (res/raw, erzeugt mit tools/sounds_synth.py und seit 11.0 tools/sounds_synth11.py). */
enum class Sfx(val res: Int, val volume: Float) {
    /** Richtig (seit 11.0): weicher Marimba-Anschlag, eine Quinte mit leisem Luft-Schimmer */
    Correct(R.raw.lumina_correct, 0.9f),
    /** Nicht ganz: weich und tief, nie schrill */
    Wrong(R.raw.lumina_wrong, 0.8f),
    /** Auswahl: ein leiser Tropfen */
    Tap(R.raw.lumina_tap, 0.7f),
    /** Geschafft: dieselbe Klangfarbe, drei Töne aufwärts */
    Complete(R.raw.lumina_complete, 0.9f),
}

/**
 * Kurze Töne für Aufgaben. Laufen über die Medienlautstärke und lassen sich in den
 * Einstellungen und in jeder Lektion abschalten ([enabled]).
 */
class Sounds(context: Context) {
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val ready = ConcurrentHashMap.newKeySet<Int>()
    private val ids: Map<Sfx, Int>

    @Volatile var enabled: Boolean = true

    init {
        pool.setOnLoadCompleteListener { _, id, status -> if (status == 0) ready += id }
        ids = Sfx.entries.associateWith { s -> runCatching { pool.load(context, s.res, 1) }.getOrDefault(0) }
    }

    fun play(s: Sfx) {
        if (!enabled) return
        val id = ids[s] ?: return
        if (id != 0 && id in ready) pool.play(id, s.volume, s.volume, 1, 0, 1f)
    }

    fun release() = runCatching { pool.release() }
}

val LocalSounds = staticCompositionLocalOf<Sounds?> { null }

@Composable
fun rememberSounds(): Sounds {
    val context = LocalContext.current
    val sounds = remember(context) { Sounds(context.applicationContext) }
    DisposableEffect(sounds) { onDispose { sounds.release() } }
    return sounds
}
