package de.funke.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.notes.AiCleanup
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.components.accentFill
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.liquidRim
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr

/** Ton einer Meldung: bestimmt die Farbe des Symbols. */
enum class NoticeTone { Info, Success, Warning }

/**
 * Kurze Meldung über der Leiste unten: nach dem Aufräumen ("Aufgeräumt · Beste KI · noch 2× heute"),
 * bei Problemen mit dem genauen Grund. [undo] zeigt "Rückgängig" mit einem Zeitbalken, der in
 * [undoMillis] abläuft. [key] unterscheidet zwei gleich lautende Meldungen (für die Animation).
 */
data class Notice(
    val icon: Int,
    val title: String,
    val detail: String? = null,
    val tone: NoticeTone = NoticeTone.Info,
    val undo: Boolean = false,
    val undoMillis: Int = 8000,
    val key: Long = System.nanoTime(),
)

/**
 * Meldungs-Karte in voller Breite: Symbol in einem getönten Kreis, Titel und Zeile darunter,
 * rechts "Rückgängig". Kommt von unten herein (250 ms, Ease-out) und geht schneller (150 ms).
 */
@Composable
fun NoticeBar(notice: Notice?, onUndo: () -> Unit, modifier: Modifier = Modifier) {
    val still = de.funke.app.ui.theme.reducedMotion
    AnimatedContent(
        targetState = notice,
        contentKey = { it?.key },
        transitionSpec = {
            if (still) fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
            else (fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) +
                slideInVertically(tween(Motion.Medium, easing = Motion.EaseOut)) { it / 3 }) togetherWith
                (fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)) + slideOutVertically(tween(Motion.Fast)) { it / 4 }) using
                SizeTransform(clip = false) { _, _ -> tween(Motion.Medium, easing = Motion.EaseOut) }
        },
        modifier = modifier.fillMaxWidth(),
        label = "notice",
    ) { n ->
        if (n == null) Spacer(Modifier.fillMaxWidth().height(0.dp)) else NoticeCard(n, onUndo)
    }
}

@Composable
private fun NoticeCard(n: Notice, onUndo: () -> Unit) {
    val p = Funke.colors
    val tint = when (n.tone) {
        NoticeTone.Success -> p.accent
        NoticeTone.Warning -> p.danger
        NoticeTone.Info -> p.textSecondary
    }
    // Zeitbalken fürs Rückgängigmachen (Zeit läuft gleichmäßig, daher linear)
    val timer = remember(n.key) { Animatable(1f) }
    LaunchedEffect(n.key) { if (n.undo) timer.animateTo(0f, tween(n.undoMillis, easing = LinearEasing)) }
    Row(
        Modifier
            .padding(bottom = Space.s3)
            .fillMaxWidth()
            .heightIn(min = Sizes.button)
            .glass(Shapes.md, strong = true)
            .drawBehind {
                if (n.undo) {
                    val h = 2.dp.toPx()
                    drawLine(
                        tint.copy(alpha = 0.45f),
                        Offset(0f, size.height - h / 2f),
                        Offset(size.width * timer.value, size.height - h / 2f),
                        strokeWidth = h,
                        cap = StrokeCap.Round,
                    )
                }
            }
            .padding(start = Space.s3, end = Space.s2, top = Space.s2, bottom = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(Sizes.tile - Space.s2).clip(CircleShape).background(tint.copy(alpha = if (p.isDark) 0.16f else 0.12f)),
            contentAlignment = Alignment.Center,
        ) { Icon(painterResource(n.icon), contentDescription = null, tint = tint, modifier = Modifier.size(Sizes.iconSm + 2.dp)) }
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f).padding(vertical = Space.s1)) {
            Text(n.title, style = MaterialTheme.typography.labelLarge, color = p.text, maxLines = 2)
            if (n.detail != null) {
                Text(n.detail, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 2)
            }
        }
        if (n.undo) {
            Spacer(Modifier.width(Space.s2))
            // Sichtbar, wie lange es noch geht (danach bleibt die Fassung oben unter "Andere Fassung")
            val left = kotlin.math.ceil(timer.value * n.undoMillis / 1000f).toInt().coerceAtLeast(0)
            UndoChip(onUndo, left)
        }
    }
}

/** "Rückgängig": Akzent-Schrift auf einer leicht getönten Pille, 40 dp hoch, rechts die restlichen Sekunden. */
@Composable
private fun UndoChip(onClick: () -> Unit, secondsLeft: Int) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val layer by animateFloatAsState(if (pressed) 1f else 0f, tween(if (pressed) 60 else Motion.Fast), label = "undoPress")
    Row(
        Modifier
            .height(Sizes.touch - Space.s2)
            .graphicsLayer { val s = 1f - 0.04f * layer; scaleX = s; scaleY = s }
            .clip(Shapes.pill)
            .background(p.accent.copy(alpha = (if (p.isDark) 0.14f else 0.10f) + 0.08f * layer))
            .clickable(interactionSource = interaction, indication = null, role = Role.Button) {
                haptics?.click()
                onClick()
            }
            .semantics { contentDescription = tr("Rückgängig, noch $secondsLeft Sekunden", "Undo, $secondsLeft seconds left") }
            .padding(start = Space.s3, end = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(R.drawable.ic_undo), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconSm))
        Spacer(Modifier.width(Space.s2))
        Text(tr("Rückgängig", "Undo"), style = MaterialTheme.typography.labelLarge, color = p.accent, maxLines = 1, softWrap = false)
        Spacer(Modifier.width(Space.s2))
        // Sekunden in einem kleinen Kreis, gleich breite Ziffern, damit nichts springt
        Box(
            Modifier.size(24.dp).clip(CircleShape).background(p.accent.copy(alpha = if (p.isDark) 0.22f else 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("$secondsLeft", style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"), color = p.accent, maxLines = 1)
        }
    }
}

/**
 * Die Leiste unten im Notiz-Editor (seit 15.4 neu geordnet, deutlich ruhiger):
 *
 *     [文A Deutsch]          ( 🎤 )          [✦ Aufräumen]
 *
 * Drei Dinge, jedes mit Wort statt Rätsel-Symbol: links die Sprache, in der zugehört wird (Antippen wechselt),
 * in der Mitte groß das Mikrofon, rechts "Aufräumen". Ob als Text oder als Stichpunkte, fragt "Aufräumen" erst
 * beim Antippen ([CleanupSheet]) – die Wahl gehört zum Aufräumen und muss nicht dauernd in der Leiste stehen.
 * Ohne Glas-Pille drumherum: Wie die Kopfzeile der Startseite stehen die Werkzeuge frei; nur das Mikrofon hat
 * eine Fläche. Links und rechts schließen bündig mit dem Blatt darüber ab.
 */
@Composable
fun NoteDock(
    language: String,
    onLanguage: () -> Unit,
    hasText: Boolean,
    working: Boolean,
    onClean: () -> Unit,
    onMic: () -> Unit,
    micModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
    /** Ohne Spracherkennung auf dem Handy gibt es weder Mikrofon noch Sprachwahl (statt Knöpfen, die nur Fehler bringen). */
    micAvailable: Boolean = true,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val compact = maxWidth < 320.dp
        Row(
            Modifier.fillMaxWidth().height(DockHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (micAvailable) {
                    DockText(
                        icon = R.drawable.ic_languages,
                        label = if (compact) language.take(2).uppercase() else language,
                        tint = Funke.colors.textSecondary,
                        description = tr("Sprache beim Diktieren: $language. Antippen wechselt.", "Dictation language: $language. Tap to switch."),
                        // Symbol bündig mit dem Rand des Blatts (der Knopf selbst ist links 12 dp breiter)
                        modifier = Modifier.offset(x = -Space.s3),
                        onClick = onLanguage,
                    )
                }
            }
            if (micAvailable) MicButton(onClick = onMic, modifier = micModifier)
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                CleanButton(enabled = hasText && !working, working = working, compact = compact, onClick = onClean)
            }
        }
    }
}

private val DockHeight = 72.dp
private val MicSize = 64.dp

/** Ein Werkzeug als Symbol mit Wort, 48 dp hoch, ohne Fläche. Gedrückt wird es kurz kleiner (mit Haptik). */
@Composable
private fun DockText(
    icon: Int,
    label: String,
    tint: Color,
    description: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    spinning: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .height(Sizes.touch)
            .bouncyClickable(shape = Shapes.pill, pressedScale = 0.94f, enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(horizontal = Space.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(Sizes.iconSm + 2.dp).spinWhile(spinning),
        )
        Spacer(Modifier.width(Space.s2))
        Text(label, style = MaterialTheme.typography.labelLarge, color = tint, maxLines = 1, softWrap = false)
    }
}

/**
 * "Aufräumen": Funkel-Symbol und Wort in der Akzentfarbe. Ohne Text blass und nicht antippbar, beim Arbeiten
 * dreht sich das Symbol. Auf sehr schmalen Handys nur das Symbol.
 */
@Composable
private fun CleanButton(enabled: Boolean, working: Boolean, compact: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    val on by animateFloatAsState(if (enabled || working) 1f else 0f, Motion.fade(Motion.Base), label = "cleanOn")
    val tint = androidx.compose.ui.graphics.lerp(p.textTertiary, p.accent, on)
    if (compact) {
        Box(
            Modifier
                .size(Sizes.touch)
                .offset(x = Space.s2)
                .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, enabled = enabled, role = Role.Button, onClick = onClick)
                .semantics { contentDescription = tr("Aufräumen", "Clean up") },
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(R.drawable.ic_sparkles), contentDescription = null, tint = tint, modifier = Modifier.size(Sizes.icon).spinWhile(working))
        }
    } else {
        DockText(
            icon = R.drawable.ic_sparkles,
            label = tr("Aufräumen", "Clean up"),
            tint = tint,
            description = tr("Aufräumen", "Clean up"),
            // Wort bündig mit dem rechten Rand des Blatts
            modifier = Modifier.offset(x = Space.s3),
            enabled = enabled,
            spinning = working,
            onClick = onClick,
        )
    }
}

/**
 * Dreht das Symbol, solange [active] (Lumi räumt auf). Die Drehung läuft nur dann – sonst gäbe es auch im
 * Leerlauf jedes Bild einen Takt – und läuft danach weich bis zur vollen Umdrehung aus, statt zurückzuspringen.
 */
private fun Modifier.spinWhile(active: Boolean): Modifier = composed {
    if (reducedMotion) return@composed this
    val angle = remember { Animatable(0f) }
    LaunchedEffect(active) {
        val from = angle.value % 360f
        angle.snapTo(from)
        if (active) {
            // Von "from" bis eine Umdrehung weiter, dann von vorn: beides sieht gleich aus, also ohne Stocken
            angle.animateTo(from + 360f, infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart))
        } else if (from > 0.5f) {
            angle.animateTo(360f, tween(((360f - from) / 360f * 700f).toInt().coerceIn(160, 700), easing = Motion.EaseOut))
            angle.snapTo(0f)
        }
    }
    graphicsLayer { rotationZ = angle.value }
}

/**
 * Das Mikrofon: runder Akzent-Knopf (64 dp) in der Mitte, das Einzige mit Fläche. Bewusst ohne pulsierenden Ring:
 * Bewegung, die immer wieder neu einsetzt, zieht den Blick vom Text weg. Beim Antippen ploppt er kurz.
 */
@Composable
private fun MicButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Box(
        modifier
            .size(MicSize)
            .accentFill(CircleShape, glow = 10.dp)
            .liquidRim(CircleShape, strength = 0.7f)
            .bouncyClickable(shape = CircleShape, onClick = onClick)
            .semantics { contentDescription = tr("Diktieren", "Dictate") },
        contentAlignment = Alignment.Center,
    ) { Icon(painterResource(R.drawable.ic_mic), contentDescription = null, tint = p.onAccent, modifier = Modifier.size(28.dp)) }
}

/**
 * "Aufräumen" gewählt: Wie soll Lumi aufräumen? Zwei große Möglichkeiten mit einem kleinen Bild davon, wie das
 * Ergebnis aussieht (Absatz oder Liste). Die zuletzt gewählte ist zart hinterlegt und hat einen Haken.
 * Antippen räumt sofort auf.
 */
@Composable
fun CleanupSheet(visible: Boolean, current: AiCleanup.Format, onPick: (AiCleanup.Format) -> Unit, onDismiss: () -> Unit) {
    val p = Funke.colors
    de.funke.app.ui.components.BottomSheet(visible = visible, onDismiss = onDismiss) {
        Text(tr("Aufräumen", "Clean up"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.padding(start = Space.s1))
        Spacer(Modifier.height(Space.s4))
        CleanupOption(
            format = AiCleanup.Format.Text,
            title = tr("Als Text", "As text"),
            detail = tr("Ganze Sätze in Absätzen", "Full sentences in paragraphs"),
            selected = current == AiCleanup.Format.Text,
            onClick = { onPick(AiCleanup.Format.Text) },
        )
        Spacer(Modifier.height(Space.s2))
        CleanupOption(
            format = AiCleanup.Format.Bullets,
            title = tr("Als Stichpunkte", "As bullet points"),
            detail = tr("Eine Idee pro Zeile", "One idea per line"),
            selected = current == AiCleanup.Format.Bullets,
            onClick = { onPick(AiCleanup.Format.Bullets) },
        )
    }
}

@Composable
private fun CleanupOption(format: AiCleanup.Format, title: String, detail: String, selected: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    val bg by androidx.compose.animation.animateColorAsState(
        p.accentSoft.copy(alpha = if (selected) p.accentSoft.alpha else 0f), Motion.fade(Motion.Base), label = "cleanupOptionBg",
    )
    Row(
        Modifier
            .fillMaxWidth()
            .clip(Shapes.lg)
            .background(bg)
            .bouncyClickable(shape = Shapes.lg, pressedScale = 0.98f, role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(Space.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FormatPicture(format, Modifier.size(56.dp))
        Spacer(Modifier.width(Space.s4))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = p.text)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
        }
        if (selected) {
            Spacer(Modifier.width(Space.s2))
            Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconSm + 2.dp))
        }
    }
}

/** Kleines Bild vom Ergebnis: drei Zeilen Fließtext oder drei Stichpunkte. */
@Composable
private fun FormatPicture(format: AiCleanup.Format, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val ink = p.textSecondary.copy(alpha = 0.55f)
    val dot = p.accent
    Box(
        modifier
            .clip(Shapes.md)
            .background(p.track)
            .drawBehind {
                val w = size.width
                val h = size.height
                val stroke = 3.dp.toPx()
                val left = w * 0.2f
                val right = w * 0.8f
                val ys = listOf(h * 0.34f, h * 0.5f, h * 0.66f)
                if (format == AiCleanup.Format.Text) {
                    ys.forEachIndexed { i, y ->
                        val end = if (i == ys.lastIndex) left + (right - left) * 0.62f else right
                        drawLine(ink, Offset(left, y), Offset(end, y), strokeWidth = stroke, cap = StrokeCap.Round)
                    }
                } else {
                    ys.forEachIndexed { i, y ->
                        drawCircle(dot, radius = 2.4.dp.toPx(), center = Offset(left + 1.dp.toPx(), y))
                        val end = left + (right - left) * listOf(0.95f, 0.75f, 0.85f)[i]
                        drawLine(ink, Offset(left + 7.dp.toPx(), y), Offset(end, y), strokeWidth = stroke, cap = StrokeCap.Round)
                    }
                }
            },
    )
}

/** Ab wann eine Notiz-Meldung von allein verschwindet (ohne Rückgängig). */
const val NOTICE_MILLIS = 4200L
