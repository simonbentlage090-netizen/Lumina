package de.funke.app.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import android.os.VibrationEffect.Composition as C

/** Wie die Haptik erzeugt wird. */
enum class HapticMode(val id: String) {
    /** Feine, kurze Effekte des Systems (für gute Vibrationsmotoren). Standard. */
    Precise("precise"),
    /** Längere, volle Impulse. Für einfache Motoren, auf denen feine Effekte kaum zu spüren sind. */
    Strong("strong");

    companion object {
        fun byId(id: String?) = entries.firstOrNull { it.id == id } ?: Precise
    }
}

/**
 * Haptik direkt über den Vibrationsmotor.
 *
 * - [tick]: ganz leicht (Rad drehen, Reiter wechseln, Halte-Knöpfe laden)
 * - [click]: klarer Klick (Auswahl, Knopf)
 * - [heavy]: kräftiger Stoß (Halte-Knopf voll)
 * - [success]: richtig gelöst, zwei aufsteigende Klicks
 * - [error]: nicht ganz, zwei weiche, dumpfe Stupser (nie hart)
 * - [celebrate]: Lektion oder Session geschafft, kleine aufsteigende Folge
 * - [detent]: Rastpunkt beim Schieben und Drehen, wird zum Ende hin kräftiger
 * - [charge]: Halte-Knopf lädt, jeder Tick etwas kräftiger als der davor
 * - [grab] / [release]: Regler gegriffen / losgelassen und federt zurück
 * - [snap]: Regler schnappt am Ende ein (Magnet)
 * - [ignite]: Session startet – ein kurzes Anschwellen, dann ein satter Klick mit leisem Echo
 * - [stop]: Session beendet – ein dumpfer Stoß, der ausklingt
 *
 * [HapticMode.Precise] nutzt die feinen "Primitive" des Systems, wenn der Motor sie kann, sonst die
 * vordefinierten Effekte. [HapticMode.Strong] spielt eigene, längere Impulse.
 * Als [HapticFeedback] eingesetzt, wird aus `LongPress` ein Stoß und aus allem anderen ein Tick.
 */
class Haptics(context: Context) : HapticFeedback {

    private val vibrator: Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= 31) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }.getOrNull()?.takeIf { it.hasVibrator() }

    private fun supports(vararg ids: Int): Boolean = Build.VERSION.SDK_INT >= 30 && runCatching {
        vibrator?.areAllPrimitivesSupported(*ids) == true
    }.getOrDefault(false)

    // Eine Komposition mit nur einem nicht unterstützten Primitiv schlägt ganz fehl, deshalb jedes Set prüfen.
    private val primBasic = supports(C.PRIMITIVE_TICK, C.PRIMITIVE_CLICK)
    private val primLow = Build.VERSION.SDK_INT >= 31 && supports(C.PRIMITIVE_LOW_TICK)
    private val primThud = Build.VERSION.SDK_INT >= 31 && supports(C.PRIMITIVE_THUD)
    private val primRise = supports(C.PRIMITIVE_QUICK_RISE)
    private val primFall = supports(C.PRIMITIVE_QUICK_FALL)
    private val amplitude: Boolean = vibrator?.hasAmplitudeControl() == true

    /** 0 = aus, 1 = sanft, 2 = mittel, 3 = kräftig. Wird aus den Einstellungen gesetzt. */
    @Volatile var level: Int = 2

    @Volatile var mode: HapticMode = HapticMode.Precise

    private val strength: Float
        get() = when (level) {
            0 -> 0f
            1 -> 0.55f
            2 -> 0.8f
            else -> 1f
        }

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        if (hapticFeedbackType == HapticFeedbackType.LongPress) heavy() else tick()
    }

    fun tick() = play(Kind.Tick)
    fun click() = play(Kind.Click)
    fun heavy() = play(Kind.Heavy)
    fun success() = play(Kind.Success)
    fun error() = play(Kind.Error)
    fun celebrate() = play(Kind.Celebrate)
    fun detent(f: Float) = play(Kind.Detent, f)
    fun charge(f: Float) = play(Kind.Charge, f)
    fun grab() = play(Kind.Grab)
    fun release() = play(Kind.Release)
    fun snap() = play(Kind.Snap)
    fun ignite() = play(Kind.Ignite)
    fun stop() = play(Kind.Stop)

    private enum class Kind { Tick, Click, Heavy, Success, Error, Celebrate, Detent, Charge, Grab, Release, Snap, Ignite, Stop }

    /** [f] 0..1: wie weit (nur für [Kind.Detent] und [Kind.Charge]). */
    private fun play(kind: Kind, f: Float = 1f) {
        val v = vibrator ?: return
        val k = strength
        if (k <= 0f) return
        val x = f.coerceIn(0f, 1f)
        runCatching {
            val effect = when {
                mode == HapticMode.Precise && primBasic -> composition(kind, k, x)
                mode == HapticMode.Precise && Build.VERSION.SDK_INT >= 29 && kind.isSingle() -> predefined(kind, k)
                mode == HapticMode.Precise -> pattern(kind, k, x, strong = false)
                else -> pattern(kind, k, x, strong = true)
            }
            v.vibrate(effect)
        }
    }

    private fun Kind.isSingle() = this == Kind.Tick || this == Kind.Click || this == Kind.Heavy ||
        this == Kind.Detent || this == Kind.Charge || this == Kind.Grab || this == Kind.Release || this == Kind.Snap

    /** Feine Effekte aus Primitiven (Pixel, Galaxy S und andere gute Motoren). */
    private fun composition(kind: Kind, k: Float, f: Float): VibrationEffect {
        fun s(x: Float) = (x * k).coerceIn(0.05f, 1f)
        val c = VibrationEffect.startComposition()
        val low = if (primLow) C.PRIMITIVE_LOW_TICK else C.PRIMITIVE_TICK
        when (kind) {
            Kind.Detent -> c.addPrimitive(C.PRIMITIVE_TICK, s(0.22f + 0.6f * f))
            Kind.Charge -> if (f > 0.86f) c.addPrimitive(C.PRIMITIVE_CLICK, s(0.45f + 0.3f * f)) else c.addPrimitive(C.PRIMITIVE_TICK, s(0.2f + 0.7f * f))
            Kind.Grab -> c.addPrimitive(low, s(0.85f))
            Kind.Release -> c.addPrimitive(low, s(0.4f))
            Kind.Snap -> {
                c.addPrimitive(C.PRIMITIVE_CLICK, s(0.9f))
                c.addPrimitive(C.PRIMITIVE_TICK, s(0.35f), 40)
            }
            Kind.Ignite -> {
                if (primRise) c.addPrimitive(C.PRIMITIVE_QUICK_RISE, s(0.5f)) else c.addPrimitive(C.PRIMITIVE_TICK, s(0.5f))
                c.addPrimitive(C.PRIMITIVE_CLICK, s(1f), if (primRise) 0 else 50)
                c.addPrimitive(C.PRIMITIVE_TICK, s(0.5f), 80)
                c.addPrimitive(C.PRIMITIVE_TICK, s(0.25f), 70)
            }
            Kind.Stop -> {
                if (primThud) c.addPrimitive(C.PRIMITIVE_THUD, s(1f)) else c.addPrimitive(C.PRIMITIVE_CLICK, s(1f))
                if (primFall) c.addPrimitive(C.PRIMITIVE_QUICK_FALL, s(0.45f), 20) else c.addPrimitive(low, s(0.4f), 70)
            }
            Kind.Tick -> c.addPrimitive(C.PRIMITIVE_TICK, s(0.75f))
            Kind.Click -> c.addPrimitive(C.PRIMITIVE_CLICK, s(0.8f))
            Kind.Heavy -> if (primThud) c.addPrimitive(C.PRIMITIVE_THUD, s(1f)) else c.addPrimitive(C.PRIMITIVE_CLICK, s(1f))
            Kind.Success -> {
                c.addPrimitive(C.PRIMITIVE_TICK, s(0.6f))
                c.addPrimitive(C.PRIMITIVE_CLICK, s(1f), 70)
            }
            Kind.Error -> {
                val p = if (primLow) C.PRIMITIVE_LOW_TICK else C.PRIMITIVE_TICK
                c.addPrimitive(p, s(0.9f))
                c.addPrimitive(p, s(0.7f), 90)
            }
            Kind.Celebrate -> {
                c.addPrimitive(C.PRIMITIVE_CLICK, s(0.45f))
                c.addPrimitive(C.PRIMITIVE_CLICK, s(0.65f), 70)
                c.addPrimitive(C.PRIMITIVE_CLICK, s(0.85f), 70)
                if (primThud) c.addPrimitive(C.PRIMITIVE_THUD, s(1f), 110) else c.addPrimitive(C.PRIMITIVE_CLICK, s(1f), 110)
            }
        }
        return c.compose()
    }

    /** Vordefinierte Effekte (ab Android 10). Stärke über die Wahl des Effekts. */
    private fun predefined(kind: Kind, k: Float): VibrationEffect {
        val lvl = level
        val id = when (kind) {
            Kind.Tick -> if (lvl >= 3) VibrationEffect.EFFECT_CLICK else VibrationEffect.EFFECT_TICK
            Kind.Click -> when (lvl) {
                1 -> VibrationEffect.EFFECT_TICK
                3 -> VibrationEffect.EFFECT_HEAVY_CLICK
                else -> VibrationEffect.EFFECT_CLICK
            }
            Kind.Detent, Kind.Charge, Kind.Release -> VibrationEffect.EFFECT_TICK
            Kind.Grab -> if (lvl >= 3) VibrationEffect.EFFECT_CLICK else VibrationEffect.EFFECT_TICK
            Kind.Snap -> if (lvl == 1) VibrationEffect.EFFECT_CLICK else VibrationEffect.EFFECT_HEAVY_CLICK
            else -> if (lvl == 1) VibrationEffect.EFFECT_CLICK else VibrationEffect.EFFECT_HEAVY_CLICK
        }
        return VibrationEffect.createPredefined(id)
    }

    /**
     * Eigene Impulse. [strong] = länger und voll (einfache Motoren brauchen etwas Anlauf,
     * sonst spürt man nichts); sonst kurz und knapp.
     */
    private fun pattern(kind: Kind, k: Float, x: Float, strong: Boolean): VibrationEffect {
        fun a(x: Float) = if (amplitude) (x * k * 255).toInt().coerceIn(1, 255) else VibrationEffect.DEFAULT_AMPLITUDE
        // ohne Amplitudensteuerung wird die Stärke über die Länge geregelt
        fun ms(base: Int) = if (amplitude) base.toLong() else (base * (0.6f + 0.4f * k)).toLong().coerceAtLeast(6)
        val f = if (strong) 1.7f else 1f
        fun d(x: Int) = ms((x * f).toInt())
        return when (kind) {
            Kind.Tick -> VibrationEffect.createOneShot(d(12), a(if (strong) 0.85f else 0.6f))
            Kind.Click -> VibrationEffect.createOneShot(d(18), a(if (strong) 1f else 0.8f))
            Kind.Heavy -> VibrationEffect.createOneShot(d(32), a(1f))
            Kind.Success -> wave(longArrayOf(0, d(16), 60, d(24)), intArrayOf(0, a(0.55f), 0, a(1f)))
            Kind.Error -> wave(longArrayOf(0, d(22), 90, d(22)), intArrayOf(0, a(0.5f), 0, a(0.4f)))
            Kind.Celebrate -> wave(
                longArrayOf(0, d(14), 60, d(16), 60, d(18), 100, d(34)),
                intArrayOf(0, a(0.4f), 0, a(0.6f), 0, a(0.8f), 0, a(1f)),
            )
            Kind.Detent -> VibrationEffect.createOneShot(d(8 + (6 * x).toInt()), a(0.35f + 0.55f * x))
            Kind.Charge -> VibrationEffect.createOneShot(d(9 + (8 * x).toInt()), a(0.3f + 0.65f * x))
            Kind.Grab -> VibrationEffect.createOneShot(d(14), a(0.65f))
            Kind.Release -> VibrationEffect.createOneShot(d(8), a(0.35f))
            Kind.Snap -> VibrationEffect.createOneShot(d(22), a(1f))
            // anschwellen, satter Stoß, leises Echo
            Kind.Ignite -> wave(
                longArrayOf(0, d(16), 0, d(16), 0, d(30), 70, d(12)),
                intArrayOf(0, a(0.3f), 0, a(0.6f), 0, a(1f), 0, a(0.45f)),
            )
            // dumpfer Stoß, der ausklingt
            Kind.Stop -> wave(
                longArrayOf(0, d(34), 0, d(22), 0, d(22)),
                intArrayOf(0, a(1f), 0, a(0.5f), 0, a(0.22f)),
            )
        }
    }

    private fun wave(timings: LongArray, amps: IntArray): VibrationEffect =
        if (amplitude) VibrationEffect.createWaveform(timings, amps, -1)
        else VibrationEffect.createWaveform(timings, -1)
}

/** Zugriff auf die erweiterte Haptik (z. B. für [Haptics.success]). */
val LocalHaptics = staticCompositionLocalOf<Haptics?> { null }

@Composable
fun rememberHaptics(): Haptics {
    val context = LocalContext.current
    return remember(context) { Haptics(context.applicationContext) }
}
