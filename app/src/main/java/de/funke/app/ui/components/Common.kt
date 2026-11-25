package de.funke.app.ui.components

import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.semantics.Role
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.StateAlpha
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.InstalledApp
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.BackgroundPattern
import de.funke.app.ui.theme.LocalBackgroundPattern
import de.funke.app.ui.theme.UiStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ---------------------------------------------------------------------------------------------
// Hintergrund
// ---------------------------------------------------------------------------------------------

/**
 * Ruhiger Hintergrund in den Farben des gewählten Schemas, mit weichen Lichtflecken,
 * die ganz langsam driften.
 *
 * Wichtig: [content] wird für jeden Stil an genau derselben Stelle aufgerufen. Sonst würde
 * Compose beim Stilwechsel den ganzen Inhalt neu aufbauen (und z. B. die Einstellungen schließen).
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val p = Funke.colors
    val style = Funke.style
    val still = de.funke.app.ui.theme.reducedMotion
    // Sehr langsames Driften der Lichtflecken. Bei "Bewegung reduzieren" stehen sie still.
    val drift = if (still) 0.5f else {
        val infinite = rememberInfiniteTransition(label = "ambient")
        val d by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(24000, easing = Motion.EaseInOut), RepeatMode.Reverse),
            label = "drift",
        )
        d
    }
    val base = when (style) {
        UiStyle.Modern, UiStyle.Glass -> Modifier.background(Brush.verticalGradient(listOf(p.bg, p.bgMid, p.bg)))
        else -> Modifier.background(p.bg)
    }
    Box(
        modifier
            .fillMaxSize()
            .then(base)
            .drawBehind {
                val w = size.width
                val h = size.height
                when (style) {
                    UiStyle.Cartoon -> {
                        // flacher Grund, zwei große weiche Farbflecken
                        drawCircle(p.bgMid, radius = w * 0.55f, center = Offset(w * (0.95f - 0.12f * drift), h * 0.08f))
                        drawCircle(
                            p.accent.copy(alpha = 0.08f),
                            radius = w * 0.48f,
                            center = Offset(w * (0.05f + 0.12f * drift), h * 0.92f),
                        )
                    }
                    UiStyle.Flat -> Unit // ein einziger, ruhiger Farbton
                    UiStyle.Modern -> {
                        // Gedämpft: ein einziger, weicher Schein oben. Der Inhalt soll leuchten, nicht der Grund.
                        val g = if (p.isDark) 1f else 0.6f
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(p.glowA.copy(alpha = 0.12f * g), Color.Transparent),
                                center = Offset(w * (0.25f + 0.25f * drift), -h * 0.02f),
                                radius = w * 1.05f,
                            )
                        )
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(p.glowB.copy(alpha = 0.05f * g), Color.Transparent),
                                center = Offset(w * (0.95f - 0.2f * drift), h * 0.96f),
                                radius = w * 0.9f,
                            )
                        )
                    }
                    UiStyle.Glass -> {
                        // Glas braucht etwas Farbe dahinter, sonst sieht man nicht, dass es durchscheint.
                        // Seit 14.0 ruhiger: wie eine Schreibtischlampe – ein Licht oben, ein leiser Widerschein unten.
                        val g = if (p.isDark) 1f else 0.8f
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(p.glowA.copy(alpha = 0.20f * g), Color.Transparent),
                                center = Offset(w * (0.14f + 0.24f * drift), h * 0.06f),
                                radius = w * 0.95f,
                            )
                        )
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(p.accent.copy(alpha = 0.08f * g), Color.Transparent),
                                center = Offset(w * (0.86f - 0.20f * drift), h * 0.98f),
                                radius = w * 0.85f,
                            )
                        )
                    }
                }
            },
    ) {
        when (style) {
            UiStyle.Cartoon -> PatternLayer(color = p.ink.copy(alpha = if (p.isDark) 0.08f else 0.03f), fade = false)
            UiStyle.Flat -> PatternLayer(color = p.glassAlpha(if (p.isDark) 0.045f else 0.04f), fade = true)
            UiStyle.Modern -> PatternLayer(color = p.glassAlpha(if (p.isDark) 0.05f else 0.05f), fade = true)
            UiStyle.Glass -> PatternLayer(color = p.glassAlpha(if (p.isDark) 0.05f else 0.05f), fade = true)
        }
        content()
    }
}

/**
 * Feines Raster oder Punktmuster im Hintergrund (Einstellung "Hintergrund", Standard: ohne).
 * Im 8-dp-Raster gesetzt, sehr zart, und mit [fade] nur oben in der Mitte sichtbar.
 * Liegt in einer eigenen Ebene und wird nur neu gezeichnet, wenn sich die Größe ändert.
 */
@Composable
private fun PatternLayer(color: Color, fade: Boolean) {
    val pattern = LocalBackgroundPattern.current
    if (pattern == BackgroundPattern.None) return
    val cartoon = Funke.cartoon
    Canvas(
        Modifier
            .fillMaxSize()
            .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
    ) {
        val w = size.width
        val h = size.height
        when (pattern) {
            BackgroundPattern.Grid -> {
                val step = 32.dp.toPx()
                val line = 1.dp.toPx()
                var x = (w / 2f) % step
                while (x < w) {
                    drawLine(color, Offset(x, 0f), Offset(x, h), line)
                    x += step
                }
                var y = (h * 0.38f) % step
                while (y < h) {
                    drawLine(color, Offset(0f, y), Offset(w, y), line)
                    y += step
                }
            }
            BackgroundPattern.Dots -> {
                // kleine, weit gesetzte Punkte; im Cartoon-Stil versetzt wie gepunktetes Papier
                val step = 24.dp.toPx()
                val r = if (cartoon) 1.25.dp.toPx() else 0.9.dp.toPx()
                val dot = color.copy(alpha = (color.alpha * if (cartoon) 2.2f else 1.6f).coerceAtMost(1f))
                var y = (h * 0.38f) % step
                var row = 0
                while (y < h) {
                    var x = (w / 2f) % step + if (cartoon && row % 2 == 1) step / 2f else 0f
                    while (x < w) {
                        drawCircle(dot, radius = r, center = Offset(x, y))
                        x += step
                    }
                    y += step
                    row++
                }
            }
            BackgroundPattern.None -> Unit
        }
        if (fade) {
            // weich ausblenden: oben in der Mitte sichtbar, nach unten und außen weg
            drawRect(
                Brush.radialGradient(
                    0f to Color.Black,
                    0.35f to Color.Black.copy(alpha = 0.6f),
                    1f to Color.Transparent,
                    center = Offset(w * 0.5f, h * 0.18f),
                    radius = maxOf(w, h) * 0.62f,
                ),
                blendMode = androidx.compose.ui.graphics.BlendMode.DstIn,
            )
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Modifier
// ---------------------------------------------------------------------------------------------

/**
 * Fläche für Karten, Chips und Leisten, je nach Stil:
 * Modern: deckende Karte mit dezentem Schatten (dunkel: etwas heller als der Grund), keine Ränder.
 * Glass: durchscheinend mit feiner Lichtkante oben. Cartoon: Kontur und harter Schatten.
 * Flat: deckende Fläche ohne alles.
 * [strong] hebt die Fläche eine Stufe an ([Elevation.Level2] statt [Elevation.Level1]).
 */
@Composable
fun Modifier.glass(
    shape: Shape = Shapes.card,
    strong: Boolean = false,
): Modifier = surface(shape, if (strong) Elevation.Level2 else Elevation.Level1)

/**
 * Wie [glass], aber mit frei wählbarer Höhe.
 * [opaque]: Die Fläche liegt über anderem Inhalt (Dialoge, Menüs) und muss deshalb immer gut lesbar sein.
 * Im Glass-Stil bleibt sie dann deckend und bekommt nur die Lichtkante; sonst schiene der Inhalt dahinter
 * durch und man könnte kaum etwas erkennen.
 */
@Composable
fun Modifier.surface(
    shape: Shape = Shapes.card,
    level: Elevation = Elevation.Level1,
    opaque: Boolean = false,
): Modifier {
    val p = Funke.colors
    val strong = level.ordinal >= Elevation.Level2.ordinal
    if (opaque && Funke.glassy) {
        return this
            .elevation(shape, level)
            .clip(shape)
            .background(p.raised(level))
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (p.isDark) 0.05f else 0.4f), Color.Transparent)))
            .border(
                1.dp,
                Brush.verticalGradient(
                    0f to Color.White.copy(alpha = if (p.isDark) 0.22f else 0.9f),
                    0.5f to Color.White.copy(alpha = if (p.isDark) 0.04f else 0.2f),
                    1f to Color.White.copy(alpha = if (p.isDark) 0.08f else 0.4f),
                ),
                shape,
            )
    }
    if (Funke.cartoon) {
        // Cartoon: flache Karte, dunkle Kontur, harter versetzter Schatten
        return this
            .hardShadow(shape, p.ink, if (strong) 4.dp else 3.dp)
            .clip(shape)
            .background(p.surface)
            .border(2.dp, p.ink, shape)
    }
    if (Funke.flat) {
        // Flat: deckende Fläche, keine Kante, kein Schatten
        return this
            .clip(shape)
            .background(p.surface)
            .background(p.glassAlpha(if (strong) (if (p.isDark) 0.045f else 0.02f) else 0f))
    }
    if (Funke.glassy) {
        // Glass: man sieht die Lichtflecken des Hintergrunds durch. Oben fängt eine feine Kante das Licht.
        return if (p.isDark) {
            val top = if (strong) 0.12f else 0.08f
            this
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = top), Color.White.copy(alpha = top * 0.4f))))
                .liquidSheen(if (strong) 0.10f else 0.07f, 0.05f)
                .border(
                    1.dp,
                    Brush.verticalGradient(0f to Color.White.copy(alpha = 0.34f), 0.45f to Color.White.copy(alpha = 0.05f), 1f to Color.White.copy(alpha = 0.14f)),
                    shape,
                )
        } else {
            val top = if (strong) 0.66f else 0.52f
            this
                .elevation(shape, level)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = top), Color.White.copy(alpha = top * 0.62f))))
                .liquidSheen(0.35f, 0.12f)
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0f))),
                    shape,
                )
        }
    }
    // Modern: Höhe über Schatten (hell) bzw. hellere Fläche (dunkel). Keine Ränder.
    return this
        .elevation(shape, level)
        .clip(shape)
        .background(p.raised(level))
}

/**
 * Leichtes "Liquid Glass" für schwebende Bedienelemente im Modern-Stil (Leiste unten, Notiz-Leiste, Mikrofon):
 * eine haarfeine Lichtkante, die oben hell ist und zur Mitte verblasst, ein weicher Glanz oben links und ein
 * schwacher Widerschein an der Unterkante, wie bei gewölbtem Glas. Bewusst unbewegt (lenkt nicht ab) und nur auf
 * wenigen Flächen, damit es besonders bleibt. In Cartoon und Flat nichts, im Glass-Stil schon eingebaut.
 */
@Composable
fun Modifier.liquidRim(shape: Shape, strength: Float = 1f): Modifier {
    if (Funke.cartoon || Funke.flat || Funke.glassy) return this
    val dark = Funke.colors.isDark
    val rim = (if (dark) 0.20f else 0.85f) * strength
    return this
        .liquidSheen((if (dark) 0.06f else 0.22f) * strength, (if (dark) 0.03f else 0.06f) * strength)
        .border(
            0.8.dp,
            Brush.verticalGradient(
                0f to Color.White.copy(alpha = rim),
                0.4f to Color.White.copy(alpha = rim * 0.15f),
                1f to Color.White.copy(alpha = rim * if (dark) 0.35f else 0.2f),
            ),
            shape,
        )
}

/**
 * "Liquid Glass" in leise (seit 11.0): ein weicher Lichtfleck oben links, als fiele Licht auf gewölbtes Glas,
 * und ein schwacher Widerschein an der Unterkante. Liegt über der Fläche, aber unter dem Inhalt. Unbewegt.
 */
private fun Modifier.liquidSheen(top: Float, bottom: Float): Modifier = drawBehind {
    val r = maxOf(size.width * 0.55f, size.height * 1.4f)
    drawRect(Brush.radialGradient(listOf(Color.White.copy(alpha = top), Color.Transparent), center = Offset(size.width * 0.18f, -size.height * 0.25f), radius = r))
    drawRect(Brush.verticalGradient(0.7f to Color.Transparent, 1f to Color.White.copy(alpha = bottom)))
}

/**
 * Dezenter Schatten nach [level]: ein kurzer Kontaktschatten direkt unter der Kante
 * und ein weicher, weiter Schatten darunter.
 */
@Composable
fun Modifier.elevation(shape: Shape, level: Elevation): Modifier {
    if (level == Elevation.Level0) return this
    val dark = Funke.colors.isDark
    return this
        .softShadow(shape, Color.Black, alpha = level.contactAlpha * if (dark) 3f else 1f, blur = 2.dp, offsetY = 1.dp)
        .softShadow(shape, Color.Black, alpha = if (dark) level.darkAlpha else level.lightAlpha, blur = level.blur, offsetY = level.y)
}

/**
 * Natürlicher, weicher Schatten: unscharf, leicht nach unten versetzt, ohne harte Kante und ohne farbigen Rand.
 * Wird nur außerhalb der Form gezeichnet, damit er nicht durch durchscheinende Flächen schimmert.
 * (Ab Android 9; auf älteren Geräten fehlt der Schatten einfach.)
 */
fun Modifier.softShadow(
    shape: Shape,
    color: Color = Color.Black,
    alpha: Float = 0.10f,
    blur: Dp = 16.dp,
    offsetY: Dp = 6.dp,
): Modifier = this.drawBehind {
    if (alpha <= 0.002f) return@drawBehind
    val path = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@drawBehind)) }
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val fp = paint.asFrameworkPaint()
        fp.isAntiAlias = true
        fp.color = android.graphics.Color.TRANSPARENT
        fp.setShadowLayer(blur.toPx(), 0f, offsetY.toPx(), color.copy(alpha = alpha.coerceIn(0f, 0.99f)).toArgb())
        canvas.save()
        canvas.clipPath(path, ClipOp.Difference)
        canvas.drawPath(path, paint)
        canvas.restore()
    }
}

/** Harter, leicht versetzter Schatten wie bei Stickern und Comics (Cartoon-Stil). */
fun Modifier.hardShadow(shape: Shape, color: Color, offset: Dp): Modifier = this.drawBehind {
    val o = offset.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(left = o * 0.3f, top = o) { drawOutline(outline, color) }
}

/**
 * Volle Akzent-Fläche für die wichtigste Aktion eines Bildschirms (und für Indikatoren).
 * Modern: ruhiger Verlauf im selben Farbton, feiner Glanz oben, weicher farbiger Schatten.
 * Cartoon: flache Farbe, Kontur, harter Schatten. Flat: nur die Farbe.
 */
@Composable
fun Modifier.accentFill(shape: Shape = Shapes.pill, glow: Dp = 12.dp, outline: Dp = 2.5.dp): Modifier {
    val p = Funke.colors
    return if (Funke.cartoon) {
        this
            .hardShadow(shape, p.ink, if (glow > 0.dp) 4.dp else 0.dp)
            .clip(shape)
            .background(p.accent)
            .background(Brush.verticalGradient(0f to Color.White.copy(alpha = 0.22f), 0.45f to Color.Transparent))
            .border(outline, p.ink, shape)
    } else if (Funke.flat) {
        this
            .clip(shape)
            .background(p.accent)
    } else {
        // Modern und Glass: kurzer Kontaktschatten plus weicher Schein in der Akzentfarbe, kein Rand.
        val k = (glow.value / 12f).coerceIn(0f, 1.6f)
        this
            .softShadow(shape, Color.Black, alpha = (if (p.isDark) 0.28f else 0.06f) * k, blur = 2.dp, offsetY = 1.dp)
            .softShadow(shape, p.accent, alpha = (if (p.isDark) 0.16f else 0.20f) * k, blur = glow * 1.4f, offsetY = glow * 0.5f)
            .clip(shape)
            .background(p.accentBrush)
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (Funke.glassy) 0.24f else 0.12f), Color.Transparent)))
            .then(if (Funke.glassy) Modifier.border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)), shape) else Modifier)
    }
}

/**
 * Drück-Gefühl: Beim Drücken geht das Element schnell auf [pressedScale] (Ease-out, 120 ms),
 * beim Loslassen etwas langsamer zurück. Bei "Bewegung reduzieren" bleibt die Größe gleich.
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.97f,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val still = de.funke.app.ui.theme.reducedMotion
    val scale by animateFloatAsState(
        targetValue = if (pressed && !still) pressedScale else 1f,
        animationSpec = tween(if (pressed) Motion.Press else Motion.Base, easing = Motion.EaseOut),
        label = "pressScale",
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Zustands-Schicht über einer Fläche: Hovered, Focused, Pressed hellen (dunkles Schema)
 * bzw. dunkeln (helles Schema) die Fläche in der Textfarbe leicht ab. Blendet in 150 ms.
 */
fun Modifier.stateLayer(interactionSource: MutableInteractionSource, shape: Shape): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val target = when {
        pressed -> StateAlpha.pressed
        focused -> StateAlpha.focused
        hovered -> StateAlpha.hovered
        else -> 0f
    }
    val layer by animateFloatAsState(target, tween(if (pressed) Motion.Press else Motion.Fast, easing = Motion.EaseOut), label = "state")
    val tint = Funke.colors.text
    this.drawWithContent {
        drawContent()
        if (layer > 0.001f) drawOutline(shape.createOutline(size, layoutDirection, this), tint.copy(alpha = layer))
    }
}

/**
 * Klickbar mit Drück-Gefühl und Haptik, ohne grauen Standard-Ripple.
 * Mit [shape] bekommt die Fläche zusätzlich die Zustands-Schicht (Pressed/Hovered/Focused).
 * [enabled] = false graut aus ([StateAlpha.disabled]) und reagiert nicht.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
fun Modifier.bouncyClickable(
    strongHaptic: Boolean = false,
    enabled: Boolean = true,
    shape: Shape? = null,
    pressedScale: Float = 0.97f,
    role: Role = Role.Button,
    /**
     * Optional: lange drücken (z. B. Aktionen zu einem Buch). Läuft über dieselbe Gestenerkennung wie das
     * Antippen – zwei getrennte Erkennungen auf einem Element schlucken sich gegenseitig das Tippen (Fehler in 14.0).
     */
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    val haptics = de.funke.app.ui.LocalHaptics.current
    val dim by animateFloatAsState(if (enabled) 1f else StateAlpha.disabled, Motion.fade(Motion.Fast), label = "enabled")
    val tap: () -> Unit = {
        when {
            haptics != null -> if (strongHaptic) haptics.heavy() else haptics.click()
            else -> haptic.performHapticFeedback(
                if (strongHaptic) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove
            )
        }
        onClick()
    }
    val base = this
        .pressScale(interaction, pressedScale)
        .graphicsLayer { alpha = dim }
        .then(if (shape != null) Modifier.stateLayer(interaction, shape) else Modifier)
    if (onLongClick == null) {
        base.clickable(interactionSource = interaction, indication = null, enabled = enabled, role = role, onClick = tap)
    } else {
        base.combinedClickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            role = role,
            onLongClickLabel = onLongClickLabel,
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            },
            onClick = tap,
        )
    }
}

/**
 * Blendet ein Element beim ersten Anzeigen ein: 12 dp von unten, 300 ms Ease-out.
 * Verzögerungen werden gestaucht (höchstens 240 ms), damit sich nichts träge anfühlt.
 * Bei "Bewegung reduzieren" nur eine kurze Überblendung ohne Verschieben.
 * Der Platz bleibt von Anfang an reserviert.
 */
fun Modifier.reveal(delayMillis: Int): Modifier = composed {
    val still = de.funke.app.ui.theme.reducedMotion
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((delayMillis / 2).coerceAtMost(240).toLong())
        progress.animateTo(1f, tween(if (still) Motion.Fast else Motion.Slow, easing = Motion.EaseOut))
    }
    this.graphicsLayer {
        alpha = progress.value
        translationY = if (still) 0f else (1f - progress.value) * 12.dp.toPx()
    }
}

/** Kartengröße ändert sich weich (Ease-out, 250 ms) statt zu springen. */
fun Modifier.smoothResize(): Modifier = composed {
    val still = de.funke.app.ui.theme.reducedMotion
    if (still) this else this.animateContentSize(tween(Motion.Medium, easing = Motion.EaseOut))
}

// ---------------------------------------------------------------------------------------------
// Buttons & Labels
// ---------------------------------------------------------------------------------------------

/**
 * Die wichtigste Aktion eines Bildschirms: 3D-Knopf in der Akzentfarbe (siehe [LipButton]).
 * Pro Bildschirm höchstens einmal. Gedrückt sinkt er auf seine Kante, ausgeschaltet ist er neutral.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    LipButton(text, onClick, modifier.fillMaxWidth(), tone = LipTone.Accent, enabled = enabled)
}

/**
 * Kleiner Button. Standard: neutrale, leicht erhobene Fläche (zweitrangige Aktion).
 * [filled]: Akzentfläche (nur für die wichtigste Aktion). [danger]: Text in Warnfarbe.
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    danger: Boolean = false,
    enabled: Boolean = true,
) {
    val p = Funke.colors
    val shape = Shapes.pill
    val base = modifier
        .height(Sizes.buttonSmall)
        .bouncyClickable(enabled = enabled, shape = shape, onClick = onClick)
    val styled = if (filled) base.accentFill(shape, glow = 8.dp) else base.surface(shape, Elevation.Level1)
    Box(styled.padding(horizontal = Space.s5), contentAlignment = Alignment.Center) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = when {
                filled -> p.onAccent
                danger -> p.danger
                else -> p.text
            },
            maxLines = 1,
        )
    }
}

/**
 * Überschrift über einer Gruppe: normal geschrieben (keine gesperrten Großbuchstaben), mittleres Gewicht,
 * leise Farbe. Sie ordnet, ohne zu schreien.
 */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = Funke.colors.textTertiary,
    )
}

/** Echtes App-Symbol; ohne Symbol der Anfangsbuchstabe auf einer Fläche. */
@Composable
fun AppIcon(
    app: InstalledApp?,
    fallbackName: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val p = Funke.colors
    val icon = app?.icon
    if (icon != null) {
        Image(
            bitmap = icon,
            contentDescription = app.label,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.26f)),
        )
    } else {
        Box(
            modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.26f))
                .background(p.glassAlpha(0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                fallbackName.take(1).uppercase(),
                color = p.textSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.42f).sp,
            )
        }
    }
}

/** Mehrere App-Symbole leicht überlappend nebeneinander, mit "+N" für den Rest. */
@Composable
fun AppIconRow(
    packages: List<String>,
    installed: List<InstalledApp>,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    maxShown: Int = 4,
) {
    val p = Funke.colors
    if (packages.isEmpty()) return
    val shown = packages.take(maxShown)
    val rest = packages.size - shown.size
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        shown.forEachIndexed { index, pkg ->
            val app = installed.firstOrNull { it.packageName == pkg }
            Box(
                Modifier
                    .offset(x = (-(size.value * 0.28f) * index).dp)
                    .border(2.dp, p.bg, RoundedCornerShape(size * 0.30f))
            ) {
                AppIcon(app, app?.label ?: pkg.substringAfterLast('.'), size = size)
            }
        }
        if (rest > 0) {
            Text(
                "+$rest",
                modifier = Modifier.offset(x = (-(size.value * 0.28f) * shown.size + 8).dp),
                style = MaterialTheme.typography.labelLarge,
                color = p.textSecondary,
            )
        }
    }
}

/**
 * Bestätigungs-Dialog im Stil der App. Öffnet und schließt mit Übergang (siehe [Modal]);
 * die Knöpfe lösen ihre Aktion erst aus, wenn der Dialog zu ist.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false,
) {
    val p = Funke.colors
    Modal(onDismiss = onDismiss) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = p.text, textAlign = TextAlign.Center)
            Spacer(Modifier.height(Space.s2))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = p.textSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(Space.s6))
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s3), modifier = Modifier.fillMaxWidth()) {
                PillButton(dismissText, { close(onDismiss) }, modifier = Modifier.weight(1f))
                PillButton(
                    confirmText, { close(onConfirm) },
                    modifier = Modifier.weight(1f), filled = !destructive, danger = destructive,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Zeichnungen
// ---------------------------------------------------------------------------------------------

/** Fortschrittsring mit Farbverlauf und leuchtendem Punkt an der Spitze. */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    stroke: Dp = 12.dp,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    Canvas(modifier) {
        val sw = stroke.toPx()
        val inset = sw / 2f
        val arcSize = Size(size.width - sw, size.height - sw)
        val topLeft = Offset(inset, inset)
        val prog = progress.coerceIn(0f, 1f)

        drawArc(
            color = if (cartoon) p.ink.copy(alpha = 0.10f) else p.glassAlpha(0.09f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = sw),
        )
        if (cartoon) {
            // Konturen innen und außen um die Spur
            val r = arcSize.width / 2f
            val ow = 2.dp.toPx()
            drawCircle(p.ink, radius = r + sw / 2f, center = center, style = Stroke(ow))
            drawCircle(p.ink, radius = r - sw / 2f, center = center, style = Stroke(ow))
        }

        if (prog > 0.002f) {
            rotate(degrees = -90f) {
                drawArc(
                    brush = if (cartoon || flat) SolidColor(p.accent) else Brush.sweepGradient(
                        colors = listOf(p.accentAlt, p.accent, p.accentAlt),
                        center = center,
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f * prog,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = sw, cap = StrokeCap.Round),
                )
            }

            val angle = Math.toRadians(-90.0 + 360.0 * prog)
            val radius = arcSize.width / 2f
            val head = Offset(
                center.x + (radius * cos(angle)).toFloat(),
                center.y + (radius * sin(angle)).toFloat(),
            )
            if (cartoon) {
                drawCircle(Color.White, radius = sw * 0.62f, center = head)
                drawCircle(p.ink, radius = sw * 0.62f, center = head, style = Stroke(2.dp.toPx()))
            } else if (flat) {
                drawCircle(p.onAccent, radius = sw * 0.24f, center = head)
            } else {
                drawCircle(p.text.copy(alpha = 0.25f), radius = sw * 0.9f, center = head)
                drawCircle(p.text, radius = sw * 0.28f, center = head)
            }
        }
    }
}

/** Kleine Flamme für den Streak. */
@Composable
fun FlameIcon(modifier: Modifier = Modifier, active: Boolean = true) {
    val p = Funke.colors
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val outer = Path().apply {
            moveTo(w * 0.50f, h * 0.03f)
            cubicTo(w * 0.62f, h * 0.28f, w * 0.88f, h * 0.40f, w * 0.88f, h * 0.64f)
            cubicTo(w * 0.88f, h * 0.86f, w * 0.70f, h * 0.98f, w * 0.50f, h * 0.98f)
            cubicTo(w * 0.30f, h * 0.98f, w * 0.12f, h * 0.86f, w * 0.12f, h * 0.64f)
            cubicTo(w * 0.12f, h * 0.48f, w * 0.26f, h * 0.40f, w * 0.32f, h * 0.24f)
            cubicTo(w * 0.40f, h * 0.30f, w * 0.44f, h * 0.18f, w * 0.50f, h * 0.03f)
            close()
        }
        val colors = if (active) listOf(p.accentAlt, p.accent) else listOf(p.glassAlpha(0.30f), p.glassAlpha(0.18f))
        drawPath(outer, Brush.verticalGradient(colors, startY = 0f, endY = h))

        val inner = Path().apply {
            moveTo(w * 0.50f, h * 0.50f)
            cubicTo(w * 0.58f, h * 0.60f, w * 0.68f, h * 0.66f, w * 0.68f, h * 0.76f)
            cubicTo(w * 0.68f, h * 0.86f, w * 0.60f, h * 0.92f, w * 0.50f, h * 0.92f)
            cubicTo(w * 0.40f, h * 0.92f, w * 0.32f, h * 0.86f, w * 0.32f, h * 0.76f)
            cubicTo(w * 0.32f, h * 0.66f, w * 0.42f, h * 0.60f, w * 0.50f, h * 0.50f)
            close()
        }
        drawPath(inner, p.bg.copy(alpha = if (active) 0.35f else 0.18f))
    }
}

/** Haken, gezeichnet als Linie mit runden Enden. */
@Composable
fun CheckIcon(color: Color, modifier: Modifier = Modifier) = DrawnCheck(color, 1f, modifier, stroke = 0.16f)

/**
 * Haken, der sich von links unten nach rechts oben zeichnet ([progress] 0..1) – für Erfolgs-Momente
 * wie "Fertig" oder einen gestarteten Regler. [stroke] = Strichstärke im Verhältnis zur Breite.
 */
@Composable
fun DrawnCheck(color: Color, progress: Float, modifier: Modifier = Modifier, stroke: Float = 0.12f) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.54f)
            lineTo(w * 0.42f, h * 0.76f)
            lineTo(w * 0.84f, h * 0.26f)
        }
        val k = progress.coerceIn(0f, 1f)
        val style = Stroke(width = w * stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        if (k >= 0.999f) {
            drawPath(path, color, style = style)
        } else if (k > 0.001f) {
            val measure = androidx.compose.ui.graphics.PathMeasure().apply { setPath(path, false) }
            val part = Path()
            measure.getSegment(0f, measure.length * k, part, true)
            drawPath(part, color, style = style)
        }
    }
}

/** Trauriges Gesicht für den "Möchtest du wirklich aufhören?"-Dialog. */
@Composable
fun SadFace(modifier: Modifier = Modifier) {
    val p = Funke.colors
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val line = w * 0.06f
        drawCircle(p.accent.copy(alpha = 0.16f), radius = w * 0.5f)
        drawCircle(p.accent, radius = w * 0.5f - line / 2f, style = Stroke(width = line))
        // Augen
        drawCircle(p.accent, radius = w * 0.055f, center = Offset(w * 0.36f, h * 0.42f))
        drawCircle(p.accent, radius = w * 0.055f, center = Offset(w * 0.64f, h * 0.42f))
        // Augenbrauen, leicht schräg nach oben innen
        val brows = Path().apply {
            moveTo(w * 0.28f, h * 0.32f)
            lineTo(w * 0.42f, h * 0.27f)
            moveTo(w * 0.72f, h * 0.32f)
            lineTo(w * 0.58f, h * 0.27f)
        }
        drawPath(brows, p.accent, style = Stroke(width = line * 0.8f, cap = StrokeCap.Round))
        // Mund als traurige Kurve
        val mouth = Path().apply {
            moveTo(w * 0.34f, h * 0.72f)
            cubicTo(w * 0.42f, h * 0.60f, w * 0.58f, h * 0.60f, w * 0.66f, h * 0.72f)
        }
        drawPath(mouth, p.accent, style = Stroke(width = line, cap = StrokeCap.Round))
    }
}

// ---------------------------------------------------------------------------------------------
// Umschalter
// ---------------------------------------------------------------------------------------------

/**
 * Segment-Umschalter: Ein erhobener "Daumen" gleitet zur gewählten Option.
 * Seit 12.0 ruhiger: Die vordere Kante eilt voraus, die hintere folgt etwas weicher, dadurch zieht sich der
 * Daumen unterwegs kurz in die Länge – aber ohne Nachwackeln (Bedienelemente sollen sich fest anfühlen).
 * Bewusst neutral statt Akzentfarbe: Auswählen ist keine Hauptaktion. Die gewählte Option steht halbfett.
 * Im Glass-Stil ist der Daumen ein heller Glastropfen, im Cartoon-Stil bunt.
 * Bei "Bewegung reduzieren" gleitet er kurz ohne Strecken.
 */
@Composable
fun SegmentedToggle(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val glassy = Funke.glassy
    val still = de.funke.app.ui.theme.reducedMotion
    val haptic = LocalHapticFeedback.current
    val lead = remember { Animatable(selected.toFloat()) }
    val trail = remember { Animatable(selected.toFloat()) }
    LaunchedEffect(selected, still) {
        val target = selected.toFloat()
        if (still) {
            // Weniger Bewegung: ohne Strecken, aber der Daumen gleitet kurz (180 ms) statt zu springen
            val spec = androidx.compose.animation.core.tween<Float>(180, easing = de.funke.app.ui.theme.Motion.EaseOut)
            kotlinx.coroutines.coroutineScope {
                launch { lead.animateTo(target, spec) }
                launch { trail.animateTo(target, spec) }
            }
            return@LaunchedEffect
        }
        kotlinx.coroutines.coroutineScope {
            launch { lead.animateTo(target, spring(dampingRatio = 0.9f, stiffness = 650f)) }
            launch { trail.animateTo(target, spring(dampingRatio = 1f, stiffness = 320f)) }
        }
    }
    val trackShape = Shapes.pill
    val labelStyle = if (options.size >= 4) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(Sizes.touch)
            .then(
                when {
                    cartoon -> Modifier.glass(trackShape)
                    glassy -> Modifier
                        .clip(trackShape)
                        .background(if (p.isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.38f))
                        .border(1.dp, if (p.isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.7f), trackShape)
                    else -> Modifier.clip(trackShape).background(p.glassAlpha(if (p.isDark) 0.08f else 0.055f))
                }
            )
            .padding(Space.s1)
    ) {
        val segment = maxWidth / options.size
        val left = minOf(lead.value, trail.value)
        val stretch = kotlin.math.abs(lead.value - trail.value)
        Box(
            Modifier
                .offset(x = segment * left)
                .width(segment * (1f + stretch))
                .fillMaxHeight()
                .then(
                    when {
                        // Cartoon: flache Akzentfläche mit Kontur, ohne harten Schatten – der lag sonst unten auf der
                        // Kontur der Schiene und wurde an deren Rundung abgeschnitten (15.0)
                        cartoon -> Modifier
                            .clip(Shapes.pill)
                            .background(p.accent)
                            .border(2.dp, p.ink, Shapes.pill)
                        glassy -> Modifier
                            .elevation(Shapes.pill, Elevation.Level1)
                            .clip(Shapes.pill)
                            .background(
                                if (p.isDark) Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.12f)))
                                else Brush.verticalGradient(listOf(Color.White, Color.White.copy(alpha = 0.82f)))
                            )
                            .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = if (p.isDark) 0.4f else 1f), Color.White.copy(alpha = 0f))), Shapes.pill)
                        else -> Modifier.surface(Shapes.pill, Elevation.Level1)
                    }
                )
        )
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                val closeness = (1f - kotlin.math.abs(lead.value - index)).coerceIn(0f, 1f)
                val interaction = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .stateLayer(interaction, Shapes.pill)
                        .semantics { this.selected = index == selected }
                        .clickable(interactionSource = interaction, indication = null, role = Role.Tab) {
                            if (index != selected) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelect(index)
                        }
                        .padding(horizontal = Space.s1),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        style = labelStyle.copy(fontWeight = if (index == selected) FontWeight.SemiBold else FontWeight.Medium),
                        color = androidx.compose.ui.graphics.lerp(p.textSecondary, if (cartoon) p.onAccent else p.text, closeness),
                        maxLines = 1,
                        softWrap = false,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * Rundes "Licht" für einen Modus mit seinem Anfangsbuchstaben.
 * Gewählt leuchtet es im Akzentverlauf und pulsiert einmal kurz auf.
 */
@Composable
fun ModeOrb(
    name: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
) {
    val p = Funke.colors
    val flat = Funke.flat
    val glow by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = Motion.spec(Motion.Medium, Motion.EaseOut),
        label = "orbGlow",
    )
    Box(
        modifier
            .size(size)
            .graphicsLayer {
                val s = 1f + 0.08f * glow * (1f - glow).coerceAtLeast(0f) * 4f
                scaleX = s
                scaleY = s
            }
            .drawBehind {
                if (glow > 0.01f && !flat) {
                    drawCircle(
                        Brush.radialGradient(
                            listOf(p.accent.copy(alpha = 0.45f * glow), Color.Transparent),
                            center = center,
                            radius = this.size.minDimension * 0.95f,
                        ),
                        radius = this.size.minDimension * 0.95f,
                    )
                }
            }
            .clip(CircleShape)
            .background(if (Funke.cartoon) p.bgMid else p.glassAlpha(if (p.isDark) 0.08f else 0.05f))
            .then(if (Funke.cartoon) Modifier.border(2.dp, p.ink, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = glow.coerceIn(0f, 1f) }
                .then(
                    if (flat) Modifier.background(p.accent)
                    else Modifier
                        .background(p.accentBrush)
                        .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)))
                )
        )
        Text(
            name.trim().take(1).uppercase().ifEmpty { "·" },
            style = MaterialTheme.typography.titleLarge,
            color = androidx.compose.ui.graphics.lerp(p.textSecondary, p.onAccent, glow.coerceIn(0f, 1f)),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Animierter Haken
// ---------------------------------------------------------------------------------------------

/**
 * Erfolgs-Haken nach transitions.dev "success check": Einblenden + Drehen (aus 80°) + Unschärfe
 * (aus 10 dp) + Hochhüpfen mit kleinem Nachfedern, dazu zeichnet sich der Haken als Linie
 * (80 ms später). Werte von transitions.dev, Dauer auf 300 ms gestrafft (Emil Kowalski).
 * Bei "Bewegung reduzieren" oder [animate] = false steht er sofort fertig da.
 */
@Composable
fun AnimatedCheckBadge(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    animate: Boolean = true,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val flat = Funke.flat
    val still = de.funke.app.ui.theme.reducedMotion || !animate
    val start = if (still) 1f else 0f
    val appear = remember { Animatable(start) }
    val bob = remember { Animatable(start) }
    val path = remember { Animatable(start) }
    LaunchedEffect(still) {
        if (still) return@LaunchedEffect
        kotlinx.coroutines.coroutineScope {
            launch { appear.animateTo(1f, tween(Motion.Slow, easing = Motion.EaseOut)) }
            launch { bob.animateTo(1f, tween(Motion.Slow, easing = Motion.Bob)) }
            launch { path.animateTo(1f, tween(Motion.Slow, delayMillis = 80, easing = Motion.EaseOut)) }
        }
    }
    val fillColor = p.success
    val onFill = if (p.isDark) p.bg else Color.White
    Box(
        modifier
            .size(size)
            .graphicsLayer {
                val a = appear.value
                alpha = a.coerceIn(0f, 1f)
                rotationZ = 80f * (1f - a)
                translationY = (1f - bob.value) * size.toPx() * 0.5f
                if (android.os.Build.VERSION.SDK_INT >= 31) {
                    val r = 10.dp.toPx() * (1f - a)
                    renderEffect = if (r > 0.5f) androidx.compose.ui.graphics.BlurEffect(r, r) else null
                }
            }
            .then(
                when {
                    cartoon -> Modifier
                        .hardShadow(CircleShape, p.ink, 3.dp)
                        .clip(CircleShape)
                        .background(fillColor)
                        .border(2.5.dp, p.ink, CircleShape)
                    flat -> Modifier.clip(CircleShape).background(fillColor)
                    else -> Modifier
                        .softShadow(CircleShape, fillColor, alpha = 0.24f, blur = 16.dp, offsetY = 6.dp)
                        .clip(CircleShape)
                        .background(fillColor)
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Haken zeichnet sich in einem Zug
        Canvas(Modifier.size(size * 0.46f)) {
            if (path.value <= 0f) return@Canvas
            val w = this.size.width
            val h = this.size.height
            val line = Path().apply {
                moveTo(w * 0.14f, h * 0.54f)
                lineTo(w * 0.40f, h * 0.78f)
                lineTo(w * 0.88f, h * 0.24f)
            }
            val measure = androidx.compose.ui.graphics.PathMeasure()
            measure.setPath(line, false)
            val part = Path()
            measure.getSegment(0f, measure.length * path.value.coerceIn(0f, 1f), part, true)
            drawPath(part, if (cartoon) p.ink else onFill, style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

/**
 * Symbol-Wechsel nach transitions.dev "icon swap": das alte Symbol schrumpft auf 25 % und
 * verschwimmt, das neue wächst aus 25 % und wird scharf. 200 ms, Ease-in-out.
 */
@Composable
fun <T> IconSwap(
    target: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val reduced = de.funke.app.ui.theme.reducedMotion
    androidx.compose.animation.AnimatedContent(
        targetState = target,
        modifier = modifier,
        transitionSpec = {
            val spec = tween<Float>(Motion.Base, easing = Motion.EaseInOut)
            if (reduced) {
                androidx.compose.animation.fadeIn(tween(Motion.Fast)) togetherWith androidx.compose.animation.fadeOut(tween(Motion.Fast))
            } else {
                (androidx.compose.animation.fadeIn(spec) + androidx.compose.animation.scaleIn(spec, initialScale = Motion.IconSwapScale)) togetherWith
                    (androidx.compose.animation.fadeOut(spec) + androidx.compose.animation.scaleOut(spec, targetScale = Motion.IconSwapScale))
            }
        },
        contentAlignment = Alignment.Center,
        label = "iconSwap",
    ) { value ->
        val blur by transition.animateFloat(
            transitionSpec = { tween(Motion.Base, easing = Motion.EaseInOut) },
            label = "swapBlur",
        ) { st -> if (st == androidx.compose.animation.EnterExitState.Visible || reduced) 0f else 2f }
        Box(
            Modifier.graphicsLayer {
                if (android.os.Build.VERSION.SDK_INT >= 31) {
                    val r = blur.dp.toPx()
                    renderEffect = if (r > 0.3f) androidx.compose.ui.graphics.BlurEffect(r, r) else null
                }
            },
            contentAlignment = Alignment.Center,
        ) { content(value) }
    }
}

// ---------------------------------------------------------------------------------------------
// Wandernder Lichtrand
// ---------------------------------------------------------------------------------------------

/**
 * Ein Lichtpunkt wandert langsam im Uhrzeigersinn über den Rand (nach der Web-Vorlage
 * "hover border gradient"). Nur im Modern-Stil; Flat und Cartoon bleiben ruhig.
 * [phase] (0..1) verschiebt den Startpunkt, damit mehrere Ränder nicht im Gleichtakt laufen.
 */
@Composable
fun Modifier.glowBorder(
    shape: Shape,
    width: Dp = 1.2.dp,
    durationMillis: Int = 5200,
    phase: Float = 0f,
    spotColor: Color? = null,
): Modifier {
    if (Funke.flat || Funke.cartoon) return this
    val p = Funke.colors
    if (de.funke.app.ui.theme.reducedMotion) {
        val base = if (spotColor != null) spotColor.copy(alpha = 0.18f) else p.glassAlpha(if (p.isDark) 0.09f else 0.08f)
        return this.drawWithContent {
            drawContent()
            drawOutline(shape.createOutline(size, layoutDirection, this), base, style = Stroke(width.toPx()))
        }
    }
    val t by rememberInfiniteTransition(label = "glowBorder").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis, easing = LinearEasing)),
        label = "glowBorderT",
    )
    val spot = spotColor ?: if (p.isDark) Color.White else p.accent
    val base = if (spotColor != null) spotColor.copy(alpha = 0.18f) else p.glassAlpha(if (p.isDark) 0.09f else 0.08f)
    return this.drawWithContent {
        drawContent()
        val outline = shape.createOutline(size, layoutDirection, this)
        val a = ((t + phase) % 1f) * 2f * Math.PI.toFloat()
        // Punkt auf einer Ellipse entlang des Randes, oben beginnend, im Uhrzeigersinn
        val c = Offset(size.width / 2f + size.width / 2f * sin(a), size.height / 2f - size.height / 2f * cos(a))
        val radius = maxOf(size.height * 1.1f, size.width * 0.28f)
        val sw = width.toPx()
        drawOutline(outline, base, style = Stroke(sw))
        drawOutline(
            outline,
            Brush.radialGradient(listOf(spot.copy(alpha = 0.9f), spot.copy(alpha = 0f)), center = c, radius = radius),
            style = Stroke(sw * 1.5f),
        )
    }
}
