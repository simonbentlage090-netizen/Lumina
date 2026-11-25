package de.funke.app.ui.screens

import androidx.compose.ui.res.painterResource
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.R
import de.funke.app.ui.AppState
import de.funke.app.ui.NoteEditorRequest
import de.funke.app.ui.NotePaper
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.CloseIcon
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import de.funke.app.ui.components.Unfold

/**
 * Notiz im Vollbild: ein Blatt zum Schreiben (Aussehen in den Einstellungen wählbar). Unten die Leiste
 * ([NoteDock]): links die Diktiersprache, in der Mitte das Mikrofon, rechts "Aufräumen" (fragt beim Antippen,
 * ob als Text oder als Stichpunkte).
 *
 * Diktieren: Das Mikrofon öffnet eine große Sprach-Ansicht, die aus dem Knopf heraus wächst – Animation in
 * der Mitte, darunter was verstanden wird, unten Verwerfen / Pause / Fertig. Beim KI-Aufräumen erscheint
 * dort Lumi und sagt, was er gerade macht. Gespeichert wird automatisch, auch beim Schließen.
 */
@Composable
fun NoteEditor(state: AppState, request: NoteEditorRequest, onClose: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val existing = remember(request) { request.id?.let { i -> state.notes.firstOrNull { it.id == i } } }
    var noteId by remember(request) { mutableStateOf(existing?.id) }
    var value by remember(request) { mutableStateOf(TextFieldValue(existing?.text ?: "", TextRange((existing?.text ?: "").length))) }
    var voice by remember(request) { mutableStateOf(existing?.voice ?: false) }

    // Diktieren und Aufräumen: derselbe Ablauf wie in der Notiz beim Lesen (NoteVoice.kt)
    val speech = rememberNoteVoice(state, original = existing?.original, key = request)
    speech.bind(
        read = { value.text },
        write = { t -> value = TextFieldValue(t, TextRange(t.length)) },
        save = { t -> noteId = state.saveNoteText(noteId, t, voice) },
        saveOriginal = { before -> state.saveNoteOriginal(noteId, before) },
        markVoice = { voice = true },
    )

    // Automatisch speichern, kurz nachdem sich der Text geändert hat
    val latest by rememberUpdatedState(value.text)
    val latestVoice by rememberUpdatedState(voice)
    LaunchedEffect(value.text) {
        delay(600)
        noteId = state.saveNoteText(noteId, value.text, voice)
    }
    DisposableEffect(Unit) {
        onDispose {
            speech.stop()
            state.saveNoteText(noteId, latest, latestVoice)
        }
    }

    fun close() {
        speech.stop()
        noteId = state.saveNoteText(noteId, value.text, voice)
        onClose()
    }
    // Zurück: Die Sprach-Ansicht fängt es selbst ab, solange sie offen ist (NoteVoiceOverlays)
    BackHandler { close() }

    val created = existing?.createdAt ?: System.currentTimeMillis()
    val saveOne = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        if (uri != null) runCatching {
            context.contentResolver.openOutputStream(uri)?.use { it.write(value.text.trim().toByteArray(Charsets.UTF_8)) }
        }
    }
    val subtitle = Instant.ofEpochMilli(created).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("d.M., HH:mm")) +
        if (request.inSession || existing?.session?.let { it != 0L } == true) tr(", in einer Fokuszeit", ", in a focus session") else ""

    val scroll = rememberScrollState()
    // Neuer diktierter Text: nach unten mitlaufen
    LaunchedEffect(speech.sessionText) {
        if (speech.sessionText.isNotBlank()) scroll.animateScrollTo(scroll.maxValue)
    }
    val lineHeight = 28.sp

    AmbientBackground {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
            // Kopfzeile (seit 15.4 wie auf der Startseite: schlichte Symbole ohne Glas): Schließen, Datum, dann Teilen
            // und Speichern dieser einen Notiz. Die Diktiersprache steht jetzt unten neben dem Mikrofon.
            Row(Modifier.fillMaxWidth().padding(start = Space.s2, end = Space.s2, top = Space.s3, bottom = Space.s3), verticalAlignment = Alignment.CenterVertically) {
                de.funke.app.ui.components.HeaderIcon(tr("Fertig", "Done"), onClick = { close() }) {
                    CloseIcon(p.text, Modifier.size(13.dp))
                }
                Spacer(Modifier.width(Space.s1))
                Column(Modifier.weight(1f)) {
                    Text(tr("Notiz", "Note"), style = MaterialTheme.typography.titleMedium, color = p.text)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"), color = p.textTertiary)
                }
                if (value.text.isNotBlank()) {
                    EditorAction(R.drawable.ic_share, tr("Diese Notiz teilen", "Share this note")) {
                        runCatching {
                            context.startActivity(
                                android.content.Intent.createChooser(
                                    android.content.Intent(android.content.Intent.ACTION_SEND).setType("text/plain")
                                        .putExtra(android.content.Intent.EXTRA_TEXT, value.text.trim()),
                                    null,
                                ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                    EditorAction(R.drawable.ic_download, tr("Diese Notiz als Datei speichern", "Save this note as a file")) {
                        saveOne.launch(noteFileName(value.text, created))
                    }
                }
            }

            // Im Buch geschrieben (seit 14.0): Buch und Seite, antippen springt genau dorthin
            val inBook = existing?.takeIf { it.inBook }
            if (inBook != null) {
                Row(Modifier.fillMaxWidth().padding(start = Space.s4, end = Space.s4, bottom = Space.s3)) {
                    Row(
                        Modifier
                            .heightIn(min = 40.dp)
                            .bouncyClickable(shape = Shapes.pill, pressedScale = 0.96f) { state.openNoteInBook(inBook) }
                            .padding(horizontal = Space.s2)
                            .semantics { contentDescription = tr("Zur Seite im Buch", "Go to the page in the book") },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(painterResource(R.drawable.ic_book), contentDescription = null, tint = p.accent, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(Space.s2))
                        Text(
                            bookPlace(inBook),
                            style = MaterialTheme.typography.labelLarge,
                            color = p.accent,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 280.dp),
                        )
                    }
                }
            }

            // Andere Fassung (vor bzw. nach dem Aufräumen): auch Tage später noch zurückzuholen
            Unfold(
                visible = speech.original != null && speech.original != value.text && speech.busy == null && !speech.undoPending,
                enter = androidx.compose.animation.expandVertically(androidx.compose.animation.core.tween(Motion.Base, easing = Motion.EaseOut), clip = false) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(Motion.Base)),
                exit = androidx.compose.animation.shrinkVertically(androidx.compose.animation.core.tween(Motion.Fast, easing = Motion.EaseOut), clip = false) + androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(Motion.Fast)),
            ) {
                Row(Modifier.fillMaxWidth().padding(start = Space.s4, end = Space.s4, bottom = Space.s3)) {
                    Row(
                        Modifier
                            .heightIn(min = 40.dp)
                            .bouncyClickable(shape = Shapes.pill, pressedScale = 0.96f) { speech.swapVersions(restored = false) }
                            .padding(horizontal = Space.s2)
                            .semantics { contentDescription = tr("Andere Fassung dieser Notiz zeigen", "Show the other version of this note") },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(painterResource(R.drawable.ic_rotate), contentDescription = null, tint = p.accent, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(Space.s2))
                        Text(tr("Andere Fassung", "Other version"), style = MaterialTheme.typography.labelLarge, color = p.accent)
                    }
                }
            }

            // Das Blatt
            val sheetShape = Shapes.large
            val paper = state.notePaper
            BoxWithConstraints(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Space.s4)
                    .then(if (paper == NotePaper.Plain) Modifier else Modifier.glass(sheetShape, strong = true).clip(sheetShape)),
            ) {
                val minH = maxHeight
                val lineColor = if (Funke.cartoon) p.ink.copy(alpha = 0.10f) else p.glassAlpha(if (p.isDark) 0.08f else 0.09f)
                val marginColor = p.accent.copy(alpha = if (p.isDark) 0.40f else 0.45f)
                val startPad = if (paper == NotePaper.Notebook) 46.dp else 22.dp
                Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                    BasicTextField(
                        value = value,
                        onValueChange = { value = it },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = p.text, fontSize = 18.sp, lineHeight = lineHeight),
                        cursorBrush = SolidColor(p.accent),
                        modifier = Modifier.fillMaxWidth().heightIn(min = minH),
                        decorationBox = { inner ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = minH)
                                    .drawBehind {
                                        val lh = lineHeight.toPx()
                                        val top = 22.dp.toPx()
                                        val px = 1.dp.toPx()
                                        when (paper) {
                                            NotePaper.Plain -> {}
                                            NotePaper.Lines, NotePaper.Notebook -> {
                                                var y = top + lh - 3.dp.toPx()
                                                while (y < size.height) {
                                                    drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = px)
                                                    y += lh
                                                }
                                                if (paper == NotePaper.Notebook) {
                                                    val mx = 34.dp.toPx()
                                                    drawLine(marginColor, Offset(mx, 0f), Offset(mx, size.height), strokeWidth = 1.5f * px)
                                                }
                                            }
                                            NotePaper.Grid -> {
                                                val step = lh / 2f
                                                var y = top + lh - 3.dp.toPx() - lh * 4
                                                while (y < size.height) {
                                                    if (y > 0f) drawLine(lineColor.copy(alpha = lineColor.alpha * 0.8f), Offset(0f, y), Offset(size.width, y), strokeWidth = px)
                                                    y += step
                                                }
                                                var x = (size.width % step) / 2f
                                                while (x < size.width) {
                                                    drawLine(lineColor.copy(alpha = lineColor.alpha * 0.8f), Offset(x, 0f), Offset(x, size.height), strokeWidth = px)
                                                    x += step
                                                }
                                            }
                                            NotePaper.Dots -> {
                                                val step = lh / 2f
                                                var y = top + lh - 3.dp.toPx() - lh * 4
                                                while (y < size.height) {
                                                    var x = (size.width % step) / 2f
                                                    if (y > 0f) while (x < size.width) {
                                                        drawCircle(lineColor.copy(alpha = (lineColor.alpha * 2.2f).coerceAtMost(1f)), radius = 1.3f * px, center = Offset(x, y))
                                                        x += step
                                                    }
                                                    y += step
                                                }
                                            }
                                        }
                                    }
                                    .padding(start = startPad, end = Space.s5, top = Space.s6, bottom = 36.dp),
                            ) {
                                if (value.text.isEmpty()) {
                                    Text(
                                        tr("Schreib oder sprich …", "Type or talk …"),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = lineHeight),
                                        color = p.textTertiary,
                                    )
                                }
                                inner()
                            }
                        },
                    )
                }
            }

            // Unten: Meldung (falls da), darunter die Leiste mit Sprache, Mikrofon und "Aufräumen"
            NoteVoiceDock(
                speech,
                state,
                hasText = value.text.isNotBlank(),
                modifier = Modifier.padding(start = Space.s4, end = Space.s4, top = Space.s3, bottom = Space.s3),
            )
        }

        NoteVoiceOverlays(speech, state)
    }
}

/** Kleiner Funkel-Stern (für "Aufräumen"). */
@Composable
fun SparkleIcon(color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.minDimension
        val c = Offset(w * 0.42f, w * 0.55f)
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(c.x, c.y - w * 0.40f)
            quadraticBezierTo(c.x + w * 0.06f, c.y - w * 0.06f, c.x + w * 0.38f, c.y)
            quadraticBezierTo(c.x + w * 0.06f, c.y + w * 0.06f, c.x, c.y + w * 0.40f)
            quadraticBezierTo(c.x - w * 0.06f, c.y + w * 0.06f, c.x - w * 0.38f, c.y)
            quadraticBezierTo(c.x - w * 0.06f, c.y - w * 0.06f, c.x, c.y - w * 0.40f)
            close()
        }
        drawPath(path, color)
        drawLine(color, Offset(w * 0.82f, w * 0.06f), Offset(w * 0.82f, w * 0.30f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.70f, w * 0.18f), Offset(w * 0.94f, w * 0.18f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}


/** Schlichtes Symbol in der Kopfzeile des Editors (44 dp zum Antippen, ohne Fläche – wie auf der Startseite). */
@Composable
private fun EditorAction(icon: Int, description: String, onClick: () -> Unit) {
    val p = Funke.colors
    de.funke.app.ui.components.HeaderIcon(description, onClick = onClick) {
        androidx.compose.material3.Icon(painterResource(icon), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.icon))
    }
}

/** Dateiname für eine einzelne Notiz: die ersten Wörter der ersten Zeile, sonst Datum. */
private fun noteFileName(text: String, created: Long): String {
    val first = text.trim().lineSequence().firstOrNull()?.trim()?.removePrefix("•")?.removePrefix("-")?.trim().orEmpty()
    val clean = first.replace(Regex("[\\\\/:*?\"<>|]"), "").split(Regex("\\s+")).take(6).joinToString(" ").take(48).trim()
    val date = Instant.ofEpochMilli(created).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return (if (clean.isNotEmpty()) clean else "Notiz $date") + ".md"
}

/** "University Physics, Seite 12" – wo eine Buch-Notiz steht. */
internal fun bookPlace(n: de.funke.app.notes.Note): String {
    val title = n.bookTitle?.takeIf { it.isNotBlank() } ?: tr("Buch", "Book")
    return if (n.page > 0) tr("$title, Seite ${n.page}", "$title, page ${n.page}") else title
}
