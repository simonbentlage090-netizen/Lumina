package de.funke.app.ui.components

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import de.funke.app.R
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// Im 8er-Raster: 6 × 56 + 2 × 8 = 352 dp breit, 48 + 2 × 8 = 64 dp hoch
private val ItemWidth = 56.dp
private val ItemHeight = 48.dp
private val BarPadding = Space.s2

private class NavEntry(private val de: String, private val en: String, val icon: @Composable (Color, Modifier) -> Unit) {
    val label: String get() = tr(de, en)
}

private val Entries = listOf(
    NavEntry("Start", "Home") { c, m -> NavIcon(R.drawable.ic_nav_focus, c, m) },
    NavEntry("Bücher", "Books") { c, m -> NavIcon(R.drawable.ic_nav_books, c, m) },
    NavEntry("Denkpause", "Think break") { c, m -> NavIcon(R.drawable.ic_nav_gate, c, m) },
    NavEntry("Aufgaben", "Tasks") { c, m -> NavIcon(R.drawable.ic_nav_tasks, c, m) },
    NavEntry("Fortschritt", "Progress") { c, m -> NavIcon(R.drawable.ic_nav_progress, c, m) },
    NavEntry("Einstellungen", "Settings") { c, m -> NavIcon(R.drawable.ic_nav_settings, c, m) },
)

/** Linien-Symbol (Lucide) in der Farbe des Reiters. */
@Composable
private fun NavIcon(res: Int, color: Color, modifier: Modifier) {
    Icon(painterResource(res), contentDescription = null, tint = color, modifier = modifier)
}

/** Anzahl der Reiter in der Leiste. */
val NavTabCount: Int get() = Entries.size

/**
 * Position des leuchtenden Indikators, gemessen in Reitern (0 = erster Reiter).
 *
 * Er hat zwei Kanten: [lead] eilt schnell zum Ziel, [trail] folgt weicher.
 * Unterwegs zieht sich der Indikator dadurch kurz in die Länge wie ein Tropfen
 * und schnurrt beim Ankommen wieder zusammen.
 */
@Stable
class NavIndicatorState(initial: Float = 0f) {
    val lead = Animatable(initial)
    val trail = Animatable(initial)

    /**
     * Läuft gerade ein Wechsel per Antippen? Dann folgt der Indikator nicht der Seitenposition,
     * sondern gleitet auf eigenem Weg. Wird vom Hauptbildschirm gesetzt und zurückgesetzt.
     */
    var locked by mutableStateOf(false)

    /** "Bewegung reduzieren": Indikator springt direkt ans Ziel. Wird von der Leiste gesetzt. */
    var reduced = false

    suspend fun follow(position: Float) {
        if (locked) return
        lead.snapTo(position)
        trail.snapTo(position)
    }

    /** Gleitet zum Reiter [target]. [onLand] kommt, sobald die Vorderkante angekommen ist. */
    suspend fun glideTo(target: Int, onLand: () -> Unit) {
        if (reduced) {
            // Weniger Bewegung: kein Strecken und Federn, aber ein kurzes Gleiten, damit man sieht, wohin es geht
            coroutineScope {
                launch { lead.animateTo(target.toFloat(), androidx.compose.animation.core.tween(de.funke.app.ui.theme.Motion.Base, easing = de.funke.app.ui.theme.Motion.EaseOut)) }
                launch { trail.animateTo(target.toFloat(), androidx.compose.animation.core.tween(de.funke.app.ui.theme.Motion.Base, easing = de.funke.app.ui.theme.Motion.EaseOut)) }
            }
            onLand()
            return
        }
        // Federn ohne Überschwingen (kritisch gedämpft): schnell, aber ruhig. Die hintere Kante folgt
        // etwas weicher, so zieht sich der Indikator unterwegs kurz in die Länge.
        coroutineScope {
            launch {
                lead.animateTo(target.toFloat(), spring(dampingRatio = 0.9f, stiffness = 700f))
                onLand()
            }
            launch {
                trail.animateTo(target.toFloat(), spring(dampingRatio = 1f, stiffness = 300f))
            }
        }
    }
}

/**
 * Schwebende Leiste mit sechs Reitern.
 * Antippen reagiert schon beim Aufsetzen des Fingers (nicht erst beim Loslassen),
 * das fühlt sich auf dem Touchscreen direkter an.
 */
@Composable
fun FunkeNavBar(
    indicator: NavIndicatorState,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = Funke.colors
    val shape = Shapes.pill
    val cartoon = Funke.cartoon
    val reduced = de.funke.app.ui.theme.reducedMotion
    androidx.compose.runtime.SideEffect { indicator.reduced = reduced }

    val bar = if (cartoon) {
        Modifier
            .hardShadow(shape, p.ink, 4.dp)
            .clip(shape)
            .background(p.surface)
            .border(2.5.dp, p.ink, shape)
    } else if (Funke.flat) {
        // Flat: deckende Fläche, kein Schatten, kein Verlauf
        Modifier
            .clip(shape)
            .background(p.surface)
    } else if (Funke.glassy && liquidGlassAvailable()) {
        // Glass (ab Android 12): echtes Liquid Glass – der Inhalt dahinter scheint weichgezeichnet durch und
        // biegt sich am Rand wie in gewölbtem Glas. Oben fängt eine helle Kante das Licht.
        Modifier
            .elevation(shape, de.funke.app.ui.theme.Elevation.Level2)
            .liquidGlass(
                shape = shape,
                base = p.bg,
                tint = if (p.isDark) Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.05f)))
                else Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.50f), Color.White.copy(alpha = 0.32f))),
            )
            .clip(shape)
            .border(
                1.dp,
                Brush.verticalGradient(
                    if (p.isDark) listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.03f), Color.White.copy(alpha = 0.03f))
                    else listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.15f))
                ),
                shape,
            )
    } else if (Funke.glassy) {
        // Glass auf älteren Geräten: durchscheinende Leiste mit feiner Lichtkante
        Modifier
            .elevation(shape, de.funke.app.ui.theme.Elevation.Level2)
            .clip(shape)
            .background(
                if (p.isDark) Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.13f), Color.White.copy(alpha = 0.06f)))
                else Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.72f), Color.White.copy(alpha = 0.50f)))
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    if (p.isDark) listOf(Color.White.copy(alpha = 0.26f), Color.White.copy(alpha = 0.04f))
                    else listOf(Color.White, Color.White.copy(alpha = 0f))
                ),
                shape,
            )
    } else {
        // Modern: schwebende Fläche, Höhe nur über Schatten und hellere Fläche, kein Rand
        Modifier
            .elevation(shape, de.funke.app.ui.theme.Elevation.Level2)
            .clip(shape)
            .background(p.raised(de.funke.app.ui.theme.Elevation.Level2).copy(alpha = if (p.isDark) 0.97f else 0.94f))
    }
    Box(
        modifier
            .then(bar)
            .padding(BarPadding),
    ) {
        // Der Indikator: reicht von der hinteren bis zur vorderen Kante (echte Breite, keine verzerrten Ecken).
        // Ruhig und neutral (eine Stufe heller bzw. erhoben); nur im Cartoon-Stil in der Akzentfarbe.
        val a = indicator.lead.value
        val b = indicator.trail.value
        val left = min(a, b)
        val stretch = abs(a - b)
        Box(
            Modifier
                .offset { IntOffset((left * ItemWidth.toPx()).roundToInt(), 0) }
                .size(width = ItemWidth * (1f + stretch), height = ItemHeight)
                .graphicsLayer { scaleY = 1f - 0.08f * min(stretch, 1f) }
                .padding(horizontal = Space.s1, vertical = if (cartoon) Space.s1 else 0.dp)
                .then(
                    when {
                        cartoon -> Modifier.accentFill(Shapes.pill, glow = 14.dp, outline = 2.dp)
                        // Glass (seit 15.1 ohne eigene Kante): nur ein heller Hauch mit Akzent – keine Pille in der Pille
                        Funke.glassy -> Modifier
                            .clip(Shapes.pill)
                            .background(Color.White.copy(alpha = if (p.isDark) 0.08f else 0.45f))
                            .background(p.accent.copy(alpha = if (p.isDark) 0.16f else 0.12f))
                        // Modern und Flat: zarte Akzentfläche – man sieht sofort, wo man ist
                        else -> Modifier.clip(Shapes.pill).background(p.accent.copy(alpha = if (p.isDark) 0.18f else 0.14f))
                    }
                )
        )

        Row {
            Entries.forEachIndexed { index, entry ->
                val closeness = (1f - abs(indicator.lead.value - index)).coerceIn(0f, 1f)
                // Gewählt: Symbol in Akzentfarbe (hell etwas dunkler, damit es sich gut abhebt)
                val active = when {
                    cartoon -> p.onAccent
                    p.isDark -> p.accent
                    else -> lerp(p.accent, p.text, 0.38f)
                }
                val tint = lerp(p.textTertiary, active, closeness)
                var pressed by androidx.compose.runtime.remember { mutableStateOf(false) }
                val pressScale by animateFloatAsState(
                    targetValue = if (pressed && !reduced) 0.88f else 1f,
                    animationSpec = androidx.compose.animation.core.tween(
                        if (pressed) de.funke.app.ui.theme.Motion.Press else de.funke.app.ui.theme.Motion.Base,
                        easing = de.funke.app.ui.theme.Motion.EaseOut,
                    ),
                    label = "navPress",
                )
                Box(
                    Modifier
                        .size(ItemWidth, ItemHeight)
                        .semantics {
                            contentDescription = entry.label
                            role = Role.Tab
                            this.selected = index == selected
                        }
                        .pointerInput(index) {
                            detectTapGestures(
                                onPress = {
                                    pressed = true
                                    onSelect(index)
                                    tryAwaitRelease()
                                    pressed = false
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    entry.icon(
                        tint,
                        Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                scaleX = pressScale
                                scaleY = pressScale
                            },
                    )
                }
            }
        }
    }
}

/** Hilfsfunktion: Abstand der Indikator-Kanten, z. B. für Effekte außerhalb. */
fun NavIndicatorState.spread(): Float = max(lead.value, trail.value) - min(lead.value, trail.value)
