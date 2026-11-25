package de.funke.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Palette
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.StateAlpha

/** Höhe der Kante unter 3D-Flächen. */
val LipHeight = 4.dp

/** Farben einer 3D-Fläche: Fläche, Kante darunter, feiner Rand (optional), Schrift. */
data class LipColors(val face: Color, val lip: Color, val border: Color?, val content: Color)

/** Farbton einer 3D-Fläche. */
enum class LipTone { Accent, Neutral, Success, Danger }

/** Kante: dieselbe Farbe, deutlich dunkler (wie die Seite eines Knopfs). */
fun lipOf(face: Color, amount: Float = 0.30f): Color = lerp(face, Color.Black, amount)

/** Token-Farben für [tone] im aktuellen Farbschema. [enabled] = false: neutrale, blasse Fläche. */
fun Palette.lipColors(tone: LipTone, enabled: Boolean = true): LipColors {
    if (!enabled) {
        val face = raised(Elevation.Level0)
        return LipColors(face, lipOf(face, if (isDark) 0.45f else 0.10f), null, textTertiary)
    }
    return when (tone) {
        LipTone.Accent -> LipColors(accent, lipOf(accent, if (isDark) 0.38f else 0.28f), null, onAccent)
        LipTone.Success -> LipColors(success, lipOf(success, 0.32f), null, if (isDark) bg else Color.White)
        LipTone.Danger -> LipColors(danger, lipOf(danger, 0.32f), null, if (isDark) bg else Color.White)
        LipTone.Neutral -> {
            val face = raised(Elevation.Level1)
            LipColors(face, lipOf(face, if (isDark) 0.45f else 0.10f), if (isDark) null else glassAlpha(0.08f), text)
        }
    }
}

/**
 * 3D-Fläche wie bei Brilliant und Duolingo: unter der Fläche liegt eine dunklere Kante.
 * Gedrückt sinkt die Fläche schnell (60 ms) um die Kante nach unten, beim Loslassen kommt sie
 * weich zurück (150 ms, Ease-out). Bei "Bewegung reduzieren" bleibt sie liegen und dunkelt nur ab.
 * Die Kante ist Teil der Größe: [modifier] mit height(56.dp) ergibt 52 dp Fläche plus 4 dp Kante.
 */
@Composable
fun LipBox(
    colors: LipColors,
    modifier: Modifier = Modifier,
    shape: Shape = Shapes.md,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    role: Role = Role.Button,
    lip: Dp = LipHeight,
    borderWidth: Dp = 1.5.dp,
    haptic: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    /** Leichter: im Cartoon-Stil Kontur und Kante nur angedeutet statt schwarz (z. B. viele Tasten auf einmal). */
    soft: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val still = de.funke.app.ui.theme.reducedMotion
    val haptics = de.funke.app.ui.LocalHaptics.current
    val down by animateFloatAsState(
        if (pressed && enabled && onClick != null && !still) 1f else 0f,
        tween(if (pressed) 60 else Motion.Fast, easing = Motion.EaseOut),
        label = "lipDown",
    )
    val layer by animateFloatAsState(if (pressed && enabled && onClick != null) StateAlpha.pressed else 0f, tween(if (pressed) 60 else Motion.Fast), label = "lipLayer")
    // Cartoon-Stil: Kontur und Kante in der Konturfarbe
    val cartoon = Funke.cartoon
    val dark = Funke.colors.isDark
    val ink = if (soft) lerp(colors.face, Funke.colors.ink, if (dark) 0.55f else 0.20f) else Funke.colors.ink
    val face by animateColorAsState(colors.face, Motion.fade(Motion.Base), label = "lipFace")
    val edge by animateColorAsState(if (cartoon) ink else colors.lip, Motion.fade(Motion.Base), label = "lipEdge")
    val border = if (cartoon) ink else colors.border
    val bw = if (cartoon) (if (soft) 1.5.dp else 2.dp) else borderWidth
    val tint = Funke.colors.text
    Box(
        modifier
            .then(
                if (onClick != null) Modifier.clickable(interactionSource = interaction, indication = null, enabled = enabled, role = role) {
                    if (haptic) haptics?.click()
                    onClick()
                } else Modifier
            )
            .drawBehind {
                // Kante: die Form, um [lip] nach unten versetzt
                val l = lip.toPx()
                val outline = shape.createOutline(Size(size.width, size.height - l), layoutDirection, this)
                translate(top = l) { drawOutline(outline, edge) }
            }
            .padding(bottom = lip)
            .graphicsLayer { translationY = down * lip.toPx() }
            .clip(shape)
            .background(face)
            .then(if (border != null) Modifier.border(bw, border, shape) else Modifier)
            .drawBehind { if (layer > 0.001f) drawRect(tint.copy(alpha = layer)) },
        contentAlignment = contentAlignment,
        content = content,
    )
}

/**
 * Großer 3D-Knopf (56 dp inkl. Kante), volle Breite. [tone] Accent = die wichtigste Aktion,
 * Neutral = zweitrangig, Success/Danger in Rückmeldungen. Ausgeschaltet: neutrale Fläche, blasse Schrift.
 */
@Composable
fun LipButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: LipTone = LipTone.Accent,
    enabled: Boolean = true,
    icon: Int? = null,
    height: Dp = Sizes.button,
) {
    val p = Funke.colors
    val c = p.lipColors(tone, enabled)
    LipBox(
        colors = c,
        modifier = modifier.height(height),
        shape = Shapes.md,
        onClick = onClick,
        enabled = enabled,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = Space.s4)) {
            if (icon != null) {
                Icon(painterResource(icon), contentDescription = null, tint = c.content, modifier = Modifier.size(Sizes.icon))
                Spacer(Modifier.width(Space.s2))
            }
            Text(text, style = MaterialTheme.typography.titleMedium, color = c.content, maxLines = 1)
        }
    }
}

/** Kleiner 3D-Knopf (44 dp inkl. Kante), so breit wie sein Text. */
@Composable
fun LipPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: LipTone = LipTone.Neutral,
    enabled: Boolean = true,
    icon: Int? = null,
) {
    val p = Funke.colors
    val c = p.lipColors(tone, enabled)
    LipBox(
        colors = c,
        modifier = modifier.height(Sizes.buttonSmall),
        shape = Shapes.sm,
        onClick = onClick,
        enabled = enabled,
        lip = 3.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = Space.s4)) {
            if (icon != null) {
                Icon(painterResource(icon), contentDescription = null, tint = c.content, modifier = Modifier.size(Sizes.iconSm))
                Spacer(Modifier.width(Space.s2))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, color = c.content, maxLines = 1)
        }
    }
}

/** Symbol-Knopf als 3D-Fläche (48 dp inkl. Kante). */
@Composable
fun LipIconButton(
    icon: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: LipTone = LipTone.Neutral,
    enabled: Boolean = true,
) {
    val p = Funke.colors
    val c = p.lipColors(tone, enabled)
    LipBox(
        colors = c,
        modifier = modifier
            .size(Sizes.touch)
            .semantics { contentDescription = description },
        shape = Shapes.sm,
        onClick = onClick,
        enabled = enabled,
        lip = 3.dp,
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = if (tone == LipTone.Neutral && enabled) p.textSecondary else c.content, modifier = Modifier.size(Sizes.icon))
    }
}

/**
 * Auswahl als 3D-Fläche (wie die Antworten in Lektionen): neutral, gewählt mit Akzent-Rand und
 * leicht getönter Fläche. Für Listen und Kacheln zum Auswählen.
 */
@Composable
fun LipOption(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.CenterStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val p = Funke.colors
    val n = p.lipColors(LipTone.Neutral)
    val tint = if (Funke.cartoon) (if (p.isDark) 0.30f else 0.22f) else (if (p.isDark) 0.14f else 0.08f)
    val colors = if (selected) LipColors(
        face = lerp(n.face, p.accent, tint),
        lip = lipOf(p.accent, if (p.isDark) 0.35f else 0.10f),
        border = p.accent,
        content = p.text,
    ) else n.copy(border = n.border ?: p.glassAlpha(0.06f))
    val haptics = de.funke.app.ui.LocalHaptics.current
    LipBox(
        colors = colors,
        modifier = modifier,
        shape = Shapes.md,
        onClick = {
            haptics?.tick()
            onClick()
        },
        role = Role.RadioButton,
        borderWidth = 2.dp,
        haptic = false,
        contentAlignment = contentAlignment,
        soft = !selected,
        content = content,
    )
}
