package de.funke.app.ui.theme

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween as coreTween

// =============================================================================================
// Design Tokens
//
// Alle Abstände, Radien, Schatten, Zustände und Bewegungen der App kommen von hier.
// Bildschirme benutzen nur diese Werte, keine eigenen Zahlen und keine festen Farben.
// =============================================================================================

/**
 * Abstände im 4-dp-Raster. `s4` heißt "4 Einheiten" = 16 dp. Größere Schritte sind Vielfache von 8.
 */
object Space {
    val none = 0.dp
    val s1 = 4.dp
    val s2 = 8.dp
    val s3 = 12.dp
    val s4 = 16.dp
    val s5 = 20.dp
    val s6 = 24.dp
    val s7 = 28.dp
    val s8 = 32.dp
    val s10 = 40.dp
    val s12 = 48.dp
    val s14 = 56.dp
    val s16 = 64.dp
    val s20 = 80.dp
    val s24 = 96.dp
    val s30 = 120.dp

    /** Seitenrand links und rechts auf allen Bildschirmen. */
    val page = s6
    /** Innenabstand von Karten. */
    val card = s5
    /** Abstand zwischen Karten in einer Liste. */
    val stack = s3
    /** Abstand zwischen zwei Abschnitten. */
    val section = s8
}

/** Ecken: rund bis sehr rund. */
object Radius {
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 28.dp
    val sheet = 32.dp
}

/** Fertige Formen zu den Radien. */
object Shapes {
    val xs = RoundedCornerShape(Radius.xs)
    val sm = RoundedCornerShape(Radius.sm)
    val md = RoundedCornerShape(Radius.md)
    val lg = RoundedCornerShape(Radius.lg)
    val card = RoundedCornerShape(Radius.xl)
    val large = RoundedCornerShape(Radius.xxl)
    val sheet = RoundedCornerShape(Radius.sheet)
    val pill = RoundedCornerShape(50)
}

/** Feste Größen für Bedienelemente (Mindestgröße zum Antippen: 48 dp). */
object Sizes {
    val touch = 48.dp
    val button = 56.dp
    val buttonSmall = 44.dp
    val iconSm = 16.dp
    val icon = 20.dp
    val iconLg = 24.dp
    val tile = 40.dp
    val avatar = 48.dp
}

/**
 * Höhe einer Fläche: dezente Schatten statt harter Ränder.
 * Hell: weicher, leicht versetzter Schatten. Dunkel sieht man Schatten kaum, deshalb wird die
 * Fläche dort zusätzlich um [lift] heller.
 */
enum class Elevation(
    val lightAlpha: Float,
    val darkAlpha: Float,
    val blur: Dp,
    val y: Dp,
    val lift: Float,
) {
    /** liegt auf dem Grund, kein Schatten */
    Level0(0f, 0f, 0.dp, 0.dp, 0.025f),
    /** Karten und Listen */
    Level1(0.05f, 0.28f, 12.dp, 4.dp, 0.04f),
    /** hervorgehobene Karten, schwebende Leisten */
    Level2(0.07f, 0.36f, 20.dp, 8.dp, 0.06f),
    /** Dialoge und Blätter */
    Level3(0.10f, 0.48f, 32.dp, 12.dp, 0.075f);

    /** kurzer, dunkler Kontaktschatten direkt unter der Kante */
    val contactAlpha: Float get() = if (this == Level0) 0f else 0.035f
}

/** Durchsichtigkeit für Zustände (über der Fläche, in der Textfarbe). */
object StateAlpha {
    const val hovered = 0.04f
    const val focused = 0.08f
    const val pressed = 0.08f
    /** ausgegraute Inhalte */
    const val disabled = 0.38f
    /** Rand/Spur, wenn ausgewählt (Active) */
    const val selectedTint = 0.12f
}

/**
 * Bewegung nach Emil Kowalski und den Motion-Tokens von transitions.dev:
 * organische Kurven statt "linear" oder "ease", kurze Dauer (150–300 ms).
 * - Was erscheint oder auf einen Finger reagiert: [EaseOut] (startet schnell, landet weich).
 * - Was sich auf dem Bildschirm von A nach B bewegt: [EaseInOut].
 * - Blätter und Leisten, die hereinfahren: [Drawer] (wie bei iOS).
 * - Kleines Nachfedern (Erfolgs-Haken): [Bob].
 * - Schließen geht schneller als Öffnen.
 */
object Motion {
    /** transitions.dev "--ease-out" (Modal, Dropdown, Seiten, Haken) */
    val EaseOut: Easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
    val EaseInOut: Easing = CubicBezierEasing(0.77f, 0f, 0.175f, 1f)
    val Drawer: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    /** transitions.dev "--check-ease-bob": schießt ein wenig übers Ziel und kommt zurück */
    val Bob: Easing = CubicBezierEasing(0.34f, 1.35f, 0.64f, 1f)

    /** Modal: öffnen 250 ms aus 96 %, schließen 150 ms auf 96 % (transitions.dev). */
    const val ModalScale = 0.96f
    /** Symbol-Wechsel: 200 ms, aus 25 % Größe und 2 dp Unschärfe (transitions.dev "icon swap"). */
    const val IconSwapScale = 0.25f

    /** Drücken: muss sich sofort anfühlen. */
    const val Press = 120
    const val Fast = 150
    const val Base = 200
    const val Medium = 250
    const val Slow = 300

    /** Abstand zwischen nacheinander erscheinenden Elementen. */
    const val Stagger = 40

    /** Tween mit Token-Kurve. Bei "Bewegung reduzieren" springt der Wert sofort ans Ziel. */
    @Composable
    @ReadOnlyComposable
    fun <T> spec(duration: Int = Medium, easing: Easing = EaseOut, delay: Int = 0): FiniteAnimationSpec<T> =
        if (LocalReducedMotion.current) snap(delay) else coreTween(duration, delay, easing)

    /** Wie [spec], aber ohne Rücksicht auf "Bewegung reduzieren" (für reine Farb-/Deckkraftwechsel). */
    fun <T> fade(duration: Int = Base, delay: Int = 0): FiniteAnimationSpec<T> = coreTween(duration, delay, EaseOut)

    /** Dialog öffnen (transitions.dev "modal"): aus 96 % Größe mit Einblenden, 250 ms. */
    fun modalEnter(reduced: Boolean): EnterTransition =
        if (reduced) fadeIn(coreTween(Fast, easing = EaseOut))
        else fadeIn(coreTween(Medium, easing = EaseOut)) +
            scaleIn(coreTween(Medium, easing = EaseOut), initialScale = ModalScale)

    /** Dialog schließen: schneller (150 ms), weich zurück auf 96 %. */
    fun modalExit(reduced: Boolean): ExitTransition =
        if (reduced) fadeOut(coreTween(Fast, easing = EaseOut))
        else fadeOut(coreTween(Fast, easing = EaseOut)) +
            scaleOut(coreTween(Fast, easing = EaseOut), targetScale = ModalScale)

    /** Vollbild-Blatt hereinfahren. */
    fun sheetEnter(reduced: Boolean): EnterTransition =
        if (reduced) fadeIn(coreTween(Fast, easing = EaseOut))
        else fadeIn(coreTween(Base, easing = EaseOut)) + slideInVertically(coreTween(Slow, easing = Drawer)) { it / 12 }

    fun sheetExit(reduced: Boolean): ExitTransition =
        if (reduced) fadeOut(coreTween(Fast, easing = EaseOut))
        else fadeOut(coreTween(Fast, easing = EaseOut)) + slideOutVertically(coreTween(Base, easing = EaseOut)) { it / 20 }

    /**
     * Ausklappen (z. B. Abschnitte der Bildschirmzeit). Seit 15.0 ohne eingebautes Abschneiden (clip = false):
     * AnimatedVisibility schneidet damit sonst den Inhalt *dauerhaft* ab – Schatten und die Cartoon-Kontur
     * wirkten "abgehackt". Mit [de.funke.app.ui.components.Unfold] wird nur während der Bewegung abgeschnitten.
     */
    fun expand(reduced: Boolean): EnterTransition =
        if (reduced) fadeIn(coreTween(Fast, easing = EaseOut))
        else expandVertically(coreTween(Medium, easing = EaseOut), expandFrom = Alignment.Top, clip = false) + fadeIn(coreTween(Base, delayMillis = 50, easing = EaseOut))

    fun collapse(reduced: Boolean): ExitTransition =
        if (reduced) fadeOut(coreTween(Fast, easing = EaseOut))
        else shrinkVertically(coreTween(Base, easing = EaseOut), shrinkTowards = Alignment.Top, clip = false) + fadeOut(coreTween(Fast, easing = EaseOut))
}

/**
 * "Bewegung reduzieren": an, wenn im Handy "Animationen entfernen" eingeschaltet ist
 * (oder in Lumina unter Aussehen). Dann gibt es keine Verschiebungen, Federn und Endlos-Animationen,
 * nur noch kurze Überblendungen. Funktionen wie Halte-Knöpfe und Timer laufen normal weiter.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** Liest die Systemeinstellung "Animationen entfernen" und folgt Änderungen sofort. */
@Composable
fun rememberSystemReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    fun read(): Boolean = runCatching {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }.getOrDefault(false)
    var reduced by remember { mutableStateOf(read()) }
    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                reduced = read()
            }
        }
        runCatching {
            resolver.registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer,
            )
        }
        onDispose { runCatching { resolver.unregisterContentObserver(observer) } }
    }
    return reduced
}

/**
 * Wie viel Bewegung die App zeigt. [Full] (Standard seit 14.0) zeigt immer alle Animationen,
 * [Auto] folgt der Systemeinstellung "Animationen entfernen" (prefers-reduced-motion), [Reduced] immer nur Überblendungen.
 */
enum class MotionMode(val id: String) {
    Auto("auto"), Full("full"), Reduced("reduced");

    companion object {
        fun byId(id: String?): MotionMode = entries.firstOrNull { it.id == id } ?: Full
    }
}

/** Kurzer Zugriff in Composables: `if (reducedMotion) …` */
val reducedMotion: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalReducedMotion.current
