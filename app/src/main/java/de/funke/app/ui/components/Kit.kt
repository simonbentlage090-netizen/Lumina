package de.funke.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.NumberStyle
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion

// =============================================================================================
// Baukasten: kleine, wiederverwendbare Bausteine. Alle mit Zuständen (gedrückt, ausgeschaltet,
// aktiv) und Werten aus den Design Tokens.
// =============================================================================================

/**
 * Karte: erhobene Fläche mit Innenabstand. Mit [onClick] wird sie zur Schaltfläche
 * (gedrückt: etwas kleiner + Zustands-Schicht; ausgeschaltet: blass).
 * Ändert sich der Inhalt, wächst/schrumpft die Karte weich.
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    level: Elevation = Elevation.Level1,
    shape: Shape = Shapes.card,
    padding: PaddingValues = PaddingValues(Space.card),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.bouncyClickable(enabled = enabled, shape = shape, pressedScale = 0.98f, onClick = onClick)
                else Modifier
            )
            .surface(shape, level)
            .smoothResize()
            .padding(padding),
        content = content,
    )
}

/**
 * Aufklappbarer Abschnitt: Kopfzeile mit Titel, optionaler Zusammenfassung rechts und Pfeil.
 * Der Pfeil dreht sich (250 ms), der Inhalt klappt weich auf (Ease-out) und blendet ein.
 */
@Composable
fun ExpandableCard(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    leading: (@Composable () -> Unit)? = null,
    level: Elevation = Elevation.Level1,
    content: @Composable ColumnScope.() -> Unit,
) {
    val p = Funke.colors
    val reduced = reducedMotion
    val shape = Shapes.card
    Column(
        modifier
            .fillMaxWidth()
            .surface(shape, level)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = Sizes.button)
                .bouncyClickable(shape = shape, pressedScale = 0.99f, role = Role.Button, onClick = onToggle)
                .padding(horizontal = Space.card, vertical = Space.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(Space.s3))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = p.text,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (summary != null) {
                Text(summary, style = MaterialTheme.typography.labelLarge, color = p.textSecondary, maxLines = 1)
                Spacer(Modifier.width(Space.s2))
            }
            Chevron(expanded)
        }
        Unfold(visible = expanded, enter = Motion.expand(reduced), exit = Motion.collapse(reduced)) {
            Column(Modifier.padding(start = Space.card, end = Space.card, bottom = Space.card)) {
                content()
            }
        }
    }
}

/** Pfeil nach unten, der sich beim Aufklappen nach oben dreht. */
@Composable
fun Chevron(expanded: Boolean, modifier: Modifier = Modifier, color: Color = Funke.colors.textTertiary) {
    val turn by animateFloatAsState(if (expanded) 180f else 0f, Motion.spec(Motion.Medium, Motion.EaseOut), label = "chevron")
    Canvas(
        modifier
            .size(Sizes.icon)
            .graphicsLayer { rotationZ = turn }
    ) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.63f)
            lineTo(w * 0.75f, h * 0.38f)
        }
        drawPath(path, color, style = Stroke(w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Pfeil nach rechts (für Zeilen, die eine neue Seite öffnen). */
@Composable
fun ChevronRight(modifier: Modifier = Modifier, color: Color = Funke.colors.textTertiary) {
    Canvas(modifier.size(Sizes.icon)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.4f, h * 0.25f)
            lineTo(w * 0.65f, h * 0.5f)
            lineTo(w * 0.4f, h * 0.75f)
        }
        drawPath(path, color, style = Stroke(w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Text-Schaltfläche (dritte Ebene, z. B. "+ Gruppe"): nur Text in Akzentfarbe, beim Drücken
 * erscheint eine zarte Pille dahinter. Mindestens 48 dp hoch zum Antippen.
 */
@Composable
fun TextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Funke.colors.accent,
) {
    Box(
        modifier
            .heightIn(min = Sizes.touch)
            .bouncyClickable(enabled = enabled, shape = Shapes.pill, onClick = onClick)
            .padding(horizontal = Space.s3),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = color, maxLines = 1)
    }
}

/** Waagrechter Balken mit weich wachsender Füllung. */
@Composable
fun ProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = Funke.colors.text,
    height: Dp = Space.s1,
) {
    val p = Funke.colors
    val f by animateFloatAsState(fraction.coerceIn(0f, 1f), Motion.spec(Motion.Slow, Motion.EaseOut), label = "bar")
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(Shapes.pill)
            .background(p.track)
    ) {
        if (f > 0.001f) {
            Box(
                Modifier
                    .fillMaxWidth(f)
                    .height(height)
                    .clip(Shapes.pill)
                    .background(color)
            )
        }
    }
}

/**
 * Auswahl-Chip. Aktiv: zarte Akzentfläche mit Akzentschrift. Inaktiv: neutrale Spur.
 * Der Wechsel blendet in 150 ms.
 */
@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val p = Funke.colors
    val bg by animateColorAsState(if (selected) p.accentSoft else p.track, Motion.fade(Motion.Fast), label = "chipBg")
    val fg by animateColorAsState(if (selected) p.accent else p.textSecondary, Motion.fade(Motion.Fast), label = "chipFg")
    Box(
        modifier
            .heightIn(min = 36.dp)
            .bouncyClickable(enabled = enabled, shape = Shapes.pill, role = Role.Checkbox, onClick = onClick)
            .clip(Shapes.pill)
            .background(bg)
            .padding(horizontal = Space.s4, vertical = Space.s2),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = fg, maxLines = 1)
    }
}

/** Kennzahl: kleines Etikett, große Zahl (gleich breite Ziffern), optional eine Zeile darunter. */
@Composable
fun Stat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    valueColor: Color = Funke.colors.text,
    big: Boolean = false,
) {
    val p = Funke.colors
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = p.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(Space.s1))
        Text(
            value,
            style = if (big) NumberStyle else MaterialTheme.typography.headlineSmall,
            color = valueColor,
            maxLines = 1,
        )
        if (sub != null) {
            Text(sub, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Überschrift eines Abschnitts mit optionaler Aktion rechts. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.touch)
            .padding(start = Space.s1),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SectionLabel(title, Modifier.weight(1f))
        action?.invoke(this)
    }
}

/** Kleiner Punkt (Legende, Status). */
@Composable
fun Dot(color: Color, modifier: Modifier = Modifier, size: Dp = Space.s2) {
    Canvas(modifier.size(size)) { drawCircle(color, radius = this.size.minDimension / 2f, center = Offset(this.size.width / 2f, this.size.height / 2f)) }
}

/**
 * Schlichtes Symbol in einer Kopfzeile – das Muster der Startseite (seit 14.0), seit 15.0 für die ganze App:
 * kein Glas, kein Rand, 44 dp Tippfläche, beim Drücken eine leise Fläche. Ränder um Werkzeuge machen Kopfzeilen eng.
 */
@Composable
fun HeaderIcon(description: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: @Composable () -> Unit) {
    Box(
        modifier
            .size(Sizes.buttonSmall)
            .bouncyClickable(shape = androidx.compose.foundation.shape.CircleShape, pressedScale = 0.9f, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) { icon() }
}

/**
 * Runder Symbol-Knopf (40 dp sichtbar, 48 dp zum Antippen) auf ruhiger Spur.
 * Zustände: gedrückt (kleiner + Zustands-Schicht), [active] (Akzent-Fläche), ausgeschaltet.
 */
@Composable
fun IconButton(
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    content: @Composable () -> Unit,
) {
    val p = Funke.colors
    val bg by animateColorAsState(if (active) p.accentSoft else p.track, Motion.fade(Motion.Fast), label = "iconBtn")
    Box(
        modifier
            .size(Sizes.touch)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(Sizes.tile)
                .bouncyClickable(enabled = enabled, shape = androidx.compose.foundation.shape.CircleShape, pressedScale = 0.92f, onClick = onClick)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}

/** Zurück-Knopf für Unterseiten. */
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(de.funke.app.ui.tr("Zurück", "Back"), onClick = onClick, modifier = modifier) {
        ChevronRight(Modifier.graphicsLayer { rotationZ = 180f }, color = Funke.colors.text)
    }
}

/** Schließen-Knopf (Kreuz) für Vollbilder wie Lektion, Denkpause oder Hinweise. */
@Composable
fun CloseButton(onClick: () -> Unit, modifier: Modifier = Modifier, description: String = de.funke.app.ui.tr("Schließen", "Close")) {
    IconButton(description, onClick = onClick, modifier = modifier) {
        CloseIcon(Funke.colors.textSecondary, Modifier.size(12.dp))
    }
}
