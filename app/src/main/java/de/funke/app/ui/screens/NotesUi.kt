package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import de.funke.app.R
import androidx.compose.ui.text.withStyle
import de.funke.app.ui.components.lipColors
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Motion
import de.funke.app.notes.Note
import de.funke.app.notes.rememberDictation
import de.funke.app.ui.AppState
import de.funke.app.ui.Lang
import de.funke.app.ui.components.CloseIcon
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import de.funke.app.ui.components.reveal
import de.funke.app.ui.components.elevation
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Sizes
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/*
 * Notizen beim Lesen.
 *
 * In der Session öffnet ein kleiner Stift das Notizblatt: ruhig, ohne Glas, ohne Hüpfen. Man tippt oder
 * diktiert (Mikrofon antippen, sprechen, nochmal antippen). Beim Diktieren leuchtet eine kleine Kugel im
 * Takt der Stimme. Füllwörter wie "äh" verschwinden automatisch.
 * Alle Notizen stehen später im Fortschritt unter "Notizen".
 */

/** Stift (für den Notiz-Knopf). */
@Composable
fun PencilIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.minDimension
        val s = Stroke(w * 0.10f, cap = StrokeCap.Round)
        // schräger Stift von unten links nach oben rechts
        drawLine(color, Offset(w * 0.22f, w * 0.78f), Offset(w * 0.70f, w * 0.30f), strokeWidth = w * 0.20f, cap = StrokeCap.Round)
        drawLine(color.copy(alpha = 0.55f), Offset(w * 0.66f, w * 0.20f), Offset(w * 0.80f, w * 0.34f), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.16f, w * 0.88f), Offset(w * 0.40f, w * 0.88f), strokeWidth = s.width, cap = StrokeCap.Round)
    }
}

/** Notizblock-Symbol (Lucide "notebook-pen"), wie in der Leiste. */
@Composable
fun NotepadIcon(color: Color, modifier: Modifier = Modifier) {
    androidx.compose.material3.Icon(
        androidx.compose.ui.res.painterResource(de.funke.app.R.drawable.ic_nav_notes),
        contentDescription = null,
        tint = color,
        modifier = modifier,
    )
}


/** Mikrofon. */
@Composable
fun MicIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.minDimension
        val s = Stroke(w * 0.10f, cap = StrokeCap.Round)
        drawRoundRect(color, Offset(w * 0.36f, w * 0.10f), Size(w * 0.28f, w * 0.46f), CornerRadius(w * 0.14f))
        drawArc(color, 20f, 140f, false, Offset(w * 0.22f, w * 0.22f), Size(w * 0.56f, w * 0.48f), style = s)
        drawLine(color, Offset(w * 0.5f, w * 0.72f), Offset(w * 0.5f, w * 0.88f), strokeWidth = s.width, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.36f, w * 0.88f), Offset(w * 0.64f, w * 0.88f), strokeWidth = s.width, cap = StrokeCap.Round)
    }
}

// ---------------------------------------------------------------------------------------------
// Übersicht aller Notizen
// ---------------------------------------------------------------------------------------------

private fun dayLabel(d: LocalDate): String = when (d) {
    LocalDate.now() -> tr("Heute", "Today")
    LocalDate.now().minusDays(1) -> tr("Gestern", "Yesterday")
    else -> d.format(DateTimeFormatter.ofPattern(if (Lang.german) "EEEE, d. MMMM" else "EEEE, MMMM d", Lang.locale))
}

private fun Note.date(): LocalDate = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
private fun Note.time(): String = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).toLocalTime()
    .format(DateTimeFormatter.ofPattern("HH:mm"))

/** Alles als Text (für Teilen und Datei). */
private fun notesAsText(notes: List<Note>): String =
    notes.sortedBy { it.createdAt }.groupBy { it.date() }.entries.joinToString("\n\n") { (d, list) ->
        "## " + dayLabel(d) + "\n\n" + list.joinToString("\n\n") { (if (it.inBook) it.time() + ", " + bookPlace(it) else it.time()) + "\n" + it.text }
    }

/** Alle Notizen, nach Tagen sortiert (neueste oben). Antippen öffnet den Editor. */
@Composable
fun NotesScreen(state: AppState, onClose: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    BackHandler { onClose() }
    val saveFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        if (uri != null) runCatching {
            context.contentResolver.openOutputStream(uri)?.use { it.write(notesAsText(state.notes).toByteArray(Charsets.UTF_8)) }
        }
    }
    Box(Modifier.fillMaxSize().background(p.bg)) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = Space.s5, end = Space.s5, top = Space.s3, bottom = Space.s30),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(p.glassAlpha(0.08f)).clickable { onClose() },
                    contentAlignment = Alignment.Center,
                ) { CloseIcon(p.text, Modifier.size(12.dp)) }
                Spacer(Modifier.width(Space.s3))
                Text(tr("Notizen", "Notes"), style = MaterialTheme.typography.headlineMedium, color = p.text, modifier = Modifier.weight(1f))
            }
            if (state.notes.isNotEmpty()) {
                Spacer(Modifier.height(Space.s3))
                Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                    SmallAction(tr("Teilen", "Share")) {
                        runCatching {
                            context.startActivity(
                                Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, notesAsText(state.notes)), null)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                    SmallAction(tr("Als Datei speichern", "Save as file")) { saveFile.launch("Lumina-Notizen.md") }
                }
            }
            Spacer(Modifier.height(Space.s4))

            if (state.notes.isEmpty()) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(Space.s5))
                    Image(painterResource(R.drawable.lumi_w_read), contentDescription = null, modifier = Modifier.height(150.dp))
                    Spacer(Modifier.height(Space.s2))
                    Text(tr("Noch keine Notizen", "No notes yet"), style = MaterialTheme.typography.titleMedium, color = p.text)
                }
            }

            state.notes.sortedByDescending { it.createdAt }.groupBy { it.date() }.forEach { (day, list) ->
                SectionLabel(dayLabel(day), Modifier.padding(start = Space.s2, top = Space.s2, bottom = Space.s2))
                list.forEach { n ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(Shapes.lg)
                            .background(p.glassAlpha(0.05f))
                            .clickable { state.openNoteEditor(n.id, inSession = false) }
                            .padding(Space.s4),
                    ) {
                        Text(n.text, style = MaterialTheme.typography.bodyLarge, color = p.text, maxLines = 8)
                        Spacer(Modifier.height(Space.s2))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (n.voice) {
                                MicIcon(p.textTertiary, Modifier.size(13.dp))
                                Spacer(Modifier.width(Space.s1))
                            }
                            Text(
                                when {
                                    n.inBook -> n.time() + ", " + bookPlace(n)
                                    n.session != 0L -> n.time() + tr(", in einer Fokuszeit", ", in a focus session")
                                    else -> n.time()
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = p.textTertiary,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Spacer(Modifier.height(Space.s2))
                }
                Spacer(Modifier.height(Space.s2))
            }
        }
        // Neue Notiz
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(Space.s5)
                .size(60.dp)
                .clip(CircleShape)
                .background(p.accent)
                .clickable { state.openNoteEditor(null, inSession = false) }
                .semantics { contentDescription = tr("Neue Notiz", "New note") },
            contentAlignment = Alignment.Center,
        ) { NotepadIcon(p.onAccent, Modifier.size(26.dp)) }
    }
}

@Composable
private fun SmallAction(label: String, onClick: () -> Unit) {
    val p = Funke.colors
    Text(
        label,
        style = MaterialTheme.typography.labelLarge,
        color = p.accent,
        modifier = Modifier
            .clip(Shapes.pill)
            .background(p.accent.copy(alpha = 0.10f))
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s4, vertical = Space.s2),
    )
}

/** Karte im Fortschritt: die letzten Notizen und der Weg zu allen. */
@Composable
fun NotesCard(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Column(
        modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .background(p.glassAlpha(0.05f))
            .clickable { state.notesVisible = true }
            .padding(Space.s5),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(tr("Notizen", "Notes"), Modifier.weight(1f))
            Text(
                if (state.notes.isEmpty()) tr("Noch keine", "None yet") else "${state.notes.size}",
                style = MaterialTheme.typography.labelLarge,
                color = p.textSecondary,
            )
        }
        Spacer(Modifier.height(Space.s2))
        if (state.notes.isEmpty()) {
            Text(tr("Noch keine Notizen", "No notes yet"), style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
        } else {
            state.notes.sortedByDescending { it.createdAt }.take(2).forEach { n ->
                Text("• " + n.text, style = MaterialTheme.typography.bodyMedium, color = p.text, maxLines = 2, modifier = Modifier.padding(bottom = Space.s1))
            }
            Text(tr("Alle ansehen", "See all"), style = MaterialTheme.typography.labelLarge, color = p.accent, modifier = Modifier.padding(top = Space.s1))
        }
    }
}

/** Schnellfilter über der Liste. */
private enum class NoteFilter { All, Reading, Books, Voice }

/**
 * Reiter "Notizen": Titel mit Zahl und rechts Teilen / Speichern, darunter Suche und Schnellfilter
 * (Alle · Beim Lesen · Diktiert), dann die Notizen nach Tagen als Karten mit Titel (erste Zeile),
 * Vorschau und Fußzeile. Treffer der Suche werden markiert. "Neue Notiz" sitzt über der Leiste und wird
 * beim Scrollen zum runden Knopf.
 */
@Composable
fun NotesTab(state: AppState, modifier: Modifier = Modifier, onClose: (() -> Unit)? = null) {
    val p = Funke.colors
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var filterId by rememberSaveable { mutableStateOf(0) }
    val filter = NoteFilter.entries[filterId.coerceIn(0, NoteFilter.entries.size - 1)]
    val saveFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        if (uri != null) runCatching {
            context.contentResolver.openOutputStream(uri)?.use { it.write(notesAsText(state.notes).toByteArray(Charsets.UTF_8)) }
        }
    }
    val all = state.notes
    val counts = remember(all) {
        mapOf(
            NoteFilter.All to all.size,
            NoteFilter.Reading to all.count { it.session != 0L && !it.inBook },
            NoteFilter.Books to all.count { it.inBook },
            NoteFilter.Voice to all.count { it.voice },
        )
    }
    val shown = remember(all, query, filter) {
        val q = query.trim()
        all.filter { n ->
            (q.isEmpty() || n.text.contains(q, ignoreCase = true)) && when (filter) {
                NoteFilter.All -> true
                NoteFilter.Reading -> n.session != 0L && !n.inBook
                NoteFilter.Books -> n.inBook
                NoteFilter.Voice -> n.voice
            }
        }.sortedByDescending { it.createdAt }
    }
    val scroll = rememberScrollState()
    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scroll)
                .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = NavBarSpace + Space.s20),
        ) {
            // Kopf
            Row(Modifier.fillMaxWidth().reveal(0), verticalAlignment = Alignment.CenterVertically) {
                // Die Zahlen stehen schon an den Filtern, hier reicht der Titel
                Text(tr("Notizen", "Notes"), style = MaterialTheme.typography.headlineLarge, color = p.text, modifier = Modifier.weight(1f))
                if (all.isNotEmpty()) {
                    HeaderAction(R.drawable.ic_share, tr("Alle Notizen teilen", "Share all notes")) {
                        runCatching {
                            context.startActivity(
                                Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, notesAsText(state.notes)), null)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                    Spacer(Modifier.width(Space.s2))
                    HeaderAction(R.drawable.ic_download, tr("Als Datei speichern", "Save as file")) { saveFile.launch("Lumina-Notizen.md") }
                }
                if (onClose != null) {
                    Spacer(Modifier.width(Space.s2))
                    // Die Notizen kommen von oben, also schiebt dieser Knopf sie wieder hoch
                    HeaderAction(R.drawable.ic_chevron_left, tr("Zurück zur Startseite", "Back to home"), rotate = 90f, onClick = onClose)
                }
            }

            if (all.isEmpty()) {
                Spacer(Modifier.height(Space.s10))
                Column(Modifier.fillMaxWidth().reveal(60), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(R.drawable.lumi_w_read), contentDescription = null, modifier = Modifier.height(144.dp))
                    Spacer(Modifier.height(Space.s5))
                    Text(tr("Noch keine Notizen", "No notes yet"), style = MaterialTheme.typography.titleMedium, color = p.text)
                    Spacer(Modifier.height(Space.s6))
                    de.funke.app.ui.components.PrimaryButton(tr("Erste Notiz schreiben", "Write your first note"), onClick = { state.openNoteEditor(null, inSession = false) })
                }
                return@Column
            }

            Spacer(Modifier.height(Space.s5))
            SearchField(query, onChange = { query = it }, modifier = Modifier.reveal(40))
            Spacer(Modifier.height(Space.s3))
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s2), modifier = Modifier.reveal(60)) {
                for (f in NoteFilter.entries) {
                    val n = counts[f] ?: 0
                    if (f != NoteFilter.All && n == 0) continue
                    de.funke.app.ui.components.Chip(
                        when (f) {
                            NoteFilter.All -> tr("Alle", "All")
                            NoteFilter.Reading -> tr("Fokuszeit", "Focus")
                            NoteFilter.Books -> tr("Bücher", "Books")
                            NoteFilter.Voice -> tr("Diktiert", "Dictated")
                        } + " $n",
                        selected = f == filter,
                        onClick = { filterId = f.ordinal },
                    )
                }
            }
            Spacer(Modifier.height(Space.s5))

            if (shown.isEmpty()) {
                Text(
                    if (query.isNotBlank()) tr("Nichts gefunden für „${query.trim()}“.", "Nothing found for \"${query.trim()}\".")
                    else tr("Hier ist noch nichts.", "Nothing here yet."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = p.textSecondary,
                    modifier = Modifier.padding(horizontal = Space.s2),
                )
            }
            var row = 0
            shown.groupBy { it.date() }.forEach { (day, list) ->
                SectionLabel(dayLabel(day), Modifier.padding(start = Space.s2, top = Space.s2, bottom = Space.s3))
                list.forEach { n ->
                    val delay = (80 + 30 * row++).coerceAtMost(240)
                    NoteCard(n, query.trim(), Modifier.reveal(delay)) { state.openNoteEditor(n.id, inSession = false) }
                    Spacer(Modifier.height(Space.s3))
                }
                Spacer(Modifier.height(Space.s2))
            }
        }

        // Neue Notiz: über der Leiste rechts; beim Scrollen nur noch das Plus
        if (all.isNotEmpty()) {
            NewNoteButton(
                expanded = scroll.value < 48,
                onClick = { state.openNoteEditor(null, inSession = false) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = Space.page, bottom = NavBarSpace + Space.s3),
            )
        }
    }
}

/** Runder Glas-Knopf mit Symbol oben rechts im Kopf (44 dp). */
@Composable
private fun HeaderAction(icon: Int, description: String, rotate: Float = 0f, onClick: () -> Unit) {
    val p = Funke.colors
    Box(
        Modifier
            .size(Sizes.buttonSmall)
            .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, onClick = onClick)
            .glass(CircleShape)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Icon(
            painterResource(icon),
            contentDescription = null,
            tint = p.textSecondary,
            modifier = Modifier.size(Sizes.icon).graphicsLayer { rotationZ = rotate },
        )
    }
}

/** Suchfeld als Glas-Pille mit Lupe; rechts ein Kreuz zum Leeren. */
@Composable
private fun SearchField(query: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Row(
        modifier
            .fillMaxWidth()
            .height(Sizes.touch)
            .glass(Shapes.pill)
            .padding(start = Space.s4, end = Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Icon(painterResource(R.drawable.ic_search), contentDescription = null, tint = p.textTertiary, modifier = Modifier.size(Sizes.iconSm + 2.dp))
        Spacer(Modifier.width(Space.s3))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) Text(tr("Notizen durchsuchen", "Search notes"), style = MaterialTheme.typography.bodyMedium, color = p.textTertiary)
            BasicTextField(
                value = query,
                onValueChange = onChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = p.text),
                cursorBrush = SolidColor(p.accent),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        AnimatedVisibility(query.isNotEmpty(), enter = fadeIn(tween(Motion.Fast)), exit = fadeOut(tween(Motion.Fast))) {
            Box(
                Modifier.size(Sizes.touch - Space.s2).clip(CircleShape).clickable { onChange("") }.semantics { contentDescription = tr("Suche leeren", "Clear search") },
                contentAlignment = Alignment.Center,
            ) { CloseIcon(p.textSecondary, Modifier.size(10.dp)) }
        }
    }
}

/**
 * Eine Notiz als Karte: erste Zeile als Titel, danach bis zu drei Zeilen Vorschau, unten Uhrzeit
 * (gleich breite Ziffern), ein Mikrofon bei Diktiertem und rechts "beim Lesen". Suchtreffer sind markiert.
 */
@Composable
private fun NoteCard(n: Note, query: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val p = Funke.colors
    val lines = remember(n.text) { n.text.trim().lines().filter { it.isNotBlank() } }
    val title = lines.firstOrNull()?.trim()?.removePrefix("•")?.removePrefix("-")?.trim() ?: ""
    val rest = lines.drop(1).joinToString("\n")
    de.funke.app.ui.components.Card(onClick = onClick, padding = androidx.compose.foundation.layout.PaddingValues(Space.s4), modifier = modifier) {
        Text(
            highlight(title, query),
            style = MaterialTheme.typography.titleSmall,
            color = p.text,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        )
        if (rest.isNotBlank()) {
            Spacer(Modifier.height(Space.s1))
            Text(
                highlight(rest, query),
                style = MaterialTheme.typography.bodyMedium,
                color = p.textSecondary,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(Space.s3))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(n.time(), style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"), color = p.textTertiary)
            if (n.voice) {
                Spacer(Modifier.width(Space.s2))
                androidx.compose.material3.Icon(painterResource(R.drawable.ic_mic), contentDescription = tr("diktiert", "dictated"), tint = p.textTertiary, modifier = Modifier.size(12.dp))
            }
            Spacer(Modifier.weight(1f))
            if (n.inBook || n.session != 0L) {
                Row(
                    Modifier.widthIn(max = 220.dp).clip(Shapes.pill).background(p.accentSoft).padding(horizontal = Space.s2, vertical = Space.s1),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.Icon(
                        painterResource(if (n.inBook) R.drawable.ic_book else R.drawable.ic_timer),
                        contentDescription = null,
                        tint = p.accent,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(Space.s1))
                    Text(
                        if (n.inBook) bookPlace(n) else tr("Fokuszeit", "Focus session"),
                        style = MaterialTheme.typography.labelSmall,
                        color = p.accent,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** Markiert alle Stellen von [query] in [text] (Groß/klein egal). */
@Composable
private fun highlight(text: String, query: String): androidx.compose.ui.text.AnnotatedString {
    val p = Funke.colors
    if (query.isBlank()) return androidx.compose.ui.text.AnnotatedString(text)
    return androidx.compose.ui.text.buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val hit = text.indexOf(query, i, ignoreCase = true)
            if (hit < 0) { append(text.substring(i)); break }
            append(text.substring(i, hit))
            withStyle(androidx.compose.ui.text.SpanStyle(background = p.accent.copy(alpha = 0.22f), color = p.text)) {
                append(text.substring(hit, hit + query.length))
            }
            i = hit + query.length
        }
    }
}

/**
 * "Neue Notiz": Akzent-Knopf mit Kante. Oben in der Liste mit Beschriftung, beim Scrollen zieht er sich
 * zum runden Plus zusammen (250 ms, Ease-out) – so verdeckt er weniger.
 */
@Composable
private fun NewNoteButton(expanded: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val c = p.lipColors(de.funke.app.ui.components.LipTone.Accent)
    de.funke.app.ui.components.LipBox(
        colors = c,
        modifier = modifier.height(Sizes.button).semantics { contentDescription = tr("Neue Notiz", "New note") },
        shape = Shapes.pill,
        onClick = onClick,
    ) {
        Row(Modifier.padding(horizontal = Space.s4), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Icon(painterResource(R.drawable.ic_plus), contentDescription = null, tint = c.content, modifier = Modifier.size(Sizes.icon))
            AnimatedVisibility(
                expanded,
                enter = fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) + androidx.compose.animation.expandHorizontally(tween(Motion.Medium, easing = Motion.EaseOut)),
                exit = fadeOut(tween(Motion.Fast)) + androidx.compose.animation.shrinkHorizontally(tween(Motion.Medium, easing = Motion.EaseOut)),
            ) {
                Row {
                    Spacer(Modifier.width(Space.s2))
                    Text(tr("Neue Notiz", "New note"), style = MaterialTheme.typography.labelLarge, color = c.content, maxLines = 1, softWrap = false)
                    Spacer(Modifier.width(Space.s1))
                }
            }
        }
    }
}
