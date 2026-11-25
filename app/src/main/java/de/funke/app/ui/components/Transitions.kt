package de.funke.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.reducedMotion

// =============================================================================================
// Übergänge nach transitions.dev, umgesetzt mit den Motion-Tokens (Ease-out, 150–300 ms).
// Jede Komponente fällt bei "Bewegung reduzieren" auf reines Überblenden zurück.
// =============================================================================================

/** Unschärfe gibt es erst ab Android 12; davor wird nur überblendet. */
private val canBlur: Boolean get() = android.os.Build.VERSION.SDK_INT >= 31

/**
 * Weiche Unschärfe als Teil eines Übergangs: [amount] 1 = ganz unscharf, 0 = scharf.
 * Billig, weil nur die eigene Ebene gefiltert wird.
 */
/**
 * Auf- und Zuklappen ohne abgeschnittene Schatten (seit 15.0).
 *
 * AnimatedVisibility mit expandVertically schneidet seinen Inhalt sonst *dauerhaft* auf die eigene Fläche zu –
 * auch lange nach der Bewegung. Weiche Schatten und vor allem die harte Cartoon-Kontur unten und rechts sahen
 * dadurch "abgehackt" aus. Hier wird nur abgeschnitten, solange sich etwas bewegt (sonst würde der Inhalt beim
 * Aufklappen kurz über das Darunterliegende ragen). Die Übergänge brauchen dafür clip = false (so wie
 * Motion.expand/collapse).
 */
@Composable
fun Unfold(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = Motion.expand(reducedMotion),
    exit: ExitTransition = Motion.collapse(reducedMotion),
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val state = remember { MutableTransitionState(visible) }
    state.targetState = visible
    AnimatedVisibility(
        visibleState = state,
        modifier = modifier.graphicsLayer { clip = !state.isIdle },
        enter = enter,
        exit = exit,
        content = content,
    )
}

fun Modifier.transitionBlur(amount: () -> Float, radius: androidx.compose.ui.unit.Dp = 4.dp): Modifier =
    if (!canBlur) this else this.graphicsLayer {
        val r = radius.toPx() * amount().coerceIn(0f, 1f)
        renderEffect = if (r > 0.3f) BlurEffect(r, r, TileMode.Decal) else null
    }

/**
 * "Number subtle change": Ändert sich eine Zahl, blenden nur die Ziffern über, die sich wirklich
 * ändern (die neue kommt 40 % ihrer Höhe von unten, die alte geht nach oben). Gleich breite Ziffern
 * sorgen dafür, dass nichts zur Seite springt.
 */
@Composable
fun AnimatedNumber(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val reduced = reducedMotion
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        // von rechts gezählt, damit "9" → "10" die Einer-Stelle als dieselbe Stelle erkennt
        val width = text.length
        for (i in 0 until width) {
            val ch = text[i]
            val slot = width - i
            androidx.compose.runtime.key(slot) {
                AnimatedContent(
                    targetState = ch,
                    transitionSpec = { digitSwap(reduced, up = true) },
                    label = "digit",
                ) { c ->
                    Text(c.toString(), style = style, color = color)
                }
            }
        }
    }
}

private fun digitSwap(reduced: Boolean, up: Boolean): ContentTransform =
    if (reduced) {
        fadeIn(tween(Motion.Fast, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
    } else {
        (slideInVertically(tween(Motion.Medium, easing = Motion.EaseOut)) { if (up) it * 2 / 5 else -it * 2 / 5 } +
            fadeIn(tween(Motion.Base, easing = Motion.EaseOut))) togetherWith
            (slideOutVertically(tween(Motion.Fast, easing = Motion.EaseOut)) { if (up) -it * 2 / 5 else it * 2 / 5 } +
                fadeOut(tween(Motion.Fast - 30, easing = Motion.EaseOut)))
    }

/**
 * "Text states swap": Wechselt ein Text (z. B. ein Status), blendet der alte unscharf aus und
 * der neue aus leichter Unschärfe und 96 % Größe scharf ein.
 */
@Composable
fun SwapText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    maxLines: Int = 1,
) {
    val reduced = reducedMotion
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            if (reduced) {
                fadeIn(tween(Motion.Fast, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
            } else {
                (fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) + scaleIn(tween(Motion.Medium, easing = Motion.EaseOut), initialScale = 0.96f)) togetherWith
                    (fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)) + scaleOut(tween(Motion.Fast, easing = Motion.EaseOut), targetScale = 1.02f))
            }
        },
        label = "swapText",
        modifier = modifier,
    ) { t ->
        // Die Unschärfe hängt am Fortschritt des Ein-/Ausblendens dieses Inhalts.
        val blur = remember { Animatable(if (reduced) 0f else 1f) }
        LaunchedEffect(Unit) { blur.animateTo(0f, tween(Motion.Medium, easing = Motion.EaseOut)) }
        Text(t, style = style, color = color, maxLines = maxLines, modifier = Modifier.transitionBlur({ blur.value }))
    }
}

/**
 * "Error state shake": kurzes, gedämpftes Wackeln (300 ms), wenn [trigger] sich erhöht.
 * Die Kurve schwingt aus statt hart hin und her zu springen. Bei "Bewegung reduzieren" kein Wackeln
 * (die Fehlerfarbe zeigt den Fehler trotzdem).
 */
fun Modifier.shakeOnError(trigger: Int): Modifier = composed {
    val reduced = reducedMotion
    val x = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger <= 0 || reduced) return@LaunchedEffect
        x.animateTo(0f, keyframes {
            durationMillis = Motion.Slow
            -8f at 40 using ShakeEase
            7f at 100 using ShakeEase
            -5f at 160 using ShakeEase
            3f at 220 using ShakeEase
            0f at Motion.Slow using ShakeEase
        })
    }
    this.graphicsLayer { translationX = x.value.dp.toPx() }
}

private val ShakeEase = CubicBezierEasing(0.36f, 0.07f, 0.19f, 0.97f)

/**
 * Endlos-Schleife für dekorative Bewegung (Atmen, Schimmern, Flackern …).
 * Bei "Bewegung reduzieren" steht der Wert still auf [still]; funktionale Animationen
 * (Timer, Halte-Fortschritt, Sprach-Pegel) benutzen diese Funktion bewusst nicht.
 */
@Composable
fun rememberLoop(
    initialValue: Float,
    targetValue: Float,
    animationSpec: androidx.compose.animation.core.InfiniteRepeatableSpec<Float>,
    label: String = "loop",
    still: Float = initialValue,
): androidx.compose.runtime.State<Float> {
    if (reducedMotion) return remember { androidx.compose.runtime.mutableFloatStateOf(still) }
    return androidx.compose.animation.core.rememberInfiniteTransition(label = label)
        .animateFloat(initialValue, targetValue, animationSpec, label = label)
}


/**
 * Feiner Glanz, der alle paar Sekunden über den Inhalt gleitet (nur über gezeichnete Pixel, z. B. Schrift).
 * Gibt einem ruhenden Knopf ein wenig Leben. Bei "Bewegung reduzieren" oder [active] = false: nichts.
 */
fun Modifier.sheen(active: Boolean, color: Color, periodMillis: Int = 4200): Modifier = composed {
    val t by rememberLoop(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            tween(periodMillis, easing = androidx.compose.animation.core.LinearEasing),
            androidx.compose.animation.core.RepeatMode.Restart,
        ),
        label = "sheen",
        still = 0f,
    )
    if (!active || reducedMotion) return@composed this
    this
        .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            // nur im ersten Drittel der Periode unterwegs (Ease-in-out), den Rest Pause
            val run = (t / 0.32f).coerceIn(0f, 1f)
            if (run <= 0f || run >= 1f) return@drawWithContent
            val e = Motion.EaseInOut.transform(run)
            val w = size.width * 0.35f
            val x = -w + (size.width + 2 * w) * e
            drawRect(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(Color.Transparent, color, Color.Transparent),
                    start = androidx.compose.ui.geometry.Offset(x - w / 2, 0f),
                    end = androidx.compose.ui.geometry.Offset(x + w / 2, size.height),
                ),
                blendMode = androidx.compose.ui.graphics.BlendMode.SrcAtop,
            )
        }
}
