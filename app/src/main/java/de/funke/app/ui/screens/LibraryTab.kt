package de.funke.app.ui.screens

import androidx.compose.ui.semantics.selected
import androidx.compose.foundation.border
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.components.elevation
import de.funke.app.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.funke.app.books.Book
import de.funke.app.books.ImportResult
import de.funke.app.books.Library
import de.funke.app.books.LibrarySort
import de.funke.app.ui.AppState
import de.funke.app.ui.components.BottomSheet
import de.funke.app.ui.components.ConfirmDialog
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.PlusIcon
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.ReaderColors
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Reiter "Bücher" (seit 15.5 ein Regal aus Umschlägen statt "ein Buch groß, darunter eine Liste").
 *
 *  - Alle Bücher gleichberechtigt in Reihen zu drei (auf breiten Bildschirmen mehr): Umschlag, darunter Titel
 *    und Stand ("34 %", "Neu", "Fertig"). Zuletzt gelesen steht vorn – so ist das aktuelle Buch immer oben links.
 *  - Bücher ohne Titelbild bekommen einen Leineneinband mit ihrem Anfangsbuchstaben statt eines Textblocks.
 *  - Oben: Suche (ab sechs Büchern), Sortieren (ab drei) und Hinzufügen – schlichte Symbole wie auf der Startseite.
 *  - Antippen öffnet das Buch. "⋯" (oder lange drücken): Umbenennen und Entfernen.
 *  - Leer: Lumi, eine Frage und ein Knopf.
 *  Die Liste baut nur sichtbare Reihen, damit auch lange Regale auf langsamen Handys flüssig bleiben.
 */
@Composable
fun LibraryTab(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val context = LocalContext.current
    remember(context) { Library.attach(context); true }
    val scope = rememberCoroutineScope()
    var importing by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    var actions by remember { mutableStateOf<Book?>(null) }
    var sort by remember { mutableStateOf(Library.sort) }
    var sorting by remember { mutableStateOf(false) }
    // Suche (seit 15.3): ab einem vollen Regal über die Lupe oben, sonst gar nicht da
    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    BackHandler(enabled = searching) {
        searching = false
        query = ""
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        importing += uris.size
        scope.launch {
            var added: Book? = null
            val problems = ArrayList<String>()
            var existing = 0
            for (uri in uris) {
                when (val r = Library.import(context, uri)) {
                    is ImportResult.Added -> added = r.book
                    is ImportResult.Exists -> existing++
                    is ImportResult.Failed -> problems += importReason(r.reason)
                }
                importing = (importing - 1).coerceAtLeast(0)
            }
            message = when {
                problems.isNotEmpty() -> problems.distinct().joinToString("\n")
                existing > 0 && added == null -> tr("Das Buch ist schon in deinem Regal.", "That book is already on your shelf.")
                added != null && uris.size == 1 -> tr("„${added.title}“ steht jetzt im Regal.", "\"${added.title}\" is on your shelf now.")
                added != null -> tr("Deine Bücher stehen jetzt im Regal.", "Your books are on the shelf now.")
                else -> null
            }
        }
    }
    val pick: () -> Unit = { picker.launch(arrayOf("application/pdf", "application/epub+zip", "application/octet-stream")) }

    // Gerade aus einem Buch zurück: kurz sagen, was es gebracht hat
    val back = state.lastBookMinutes
    LaunchedEffect(back) {
        if (back != null) {
            val (_, minutes) = back
            val left = (state.dailyGoal - state.todayMinutes).coerceAtLeast(0)
            message = when {
                left == 0 -> tr("$minutes Min. Fokus im Buch. Tagesziel geschafft.", "$minutes min of focus in the book. Daily goal reached.")
                else -> tr("$minutes Min. Fokus im Buch. Noch $left bis zum Tagesziel.", "$minutes min of focus in the book. $left to go for today.")
            }
            state.lastBookMinutes = null
        }
    }
    LaunchedEffect(message) {
        if (message != null) {
            delay(4800)
            message = null
        }
    }

    val books = Library.books.toList()
    val shelf = remember(books, sort) { Library.sorted() }
    val canSearch = books.size >= SEARCH_FROM
    val q = query.trim()
    val hits = remember(books, sort, q) {
        if (q.isEmpty()) emptyList() else shelf.filter { b ->
            b.title.contains(q, ignoreCase = true) || b.author?.contains(q, ignoreCase = true) == true
        }
    }
    // Jede Reihe blendet nur beim ersten Erscheinen ein, nicht bei jedem Zurückscrollen
    val seen = remember { HashSet<String>() }

    androidx.compose.foundation.layout.BoxWithConstraints(modifier.fillMaxSize()) {
        // Reihen zu drei Büchern; erst ab breiten Bildschirmen mehr (mindestens 96 dp je Umschlag)
        val cols = ((maxWidth - Space.page * 2 + ShelfGap) / (MinCover + ShelfGap)).toInt().coerceAtLeast(3)
        val shown = if (searching && q.isNotEmpty()) hits else shelf
        val rows = remember(shown, cols) { shown.chunked(cols) }

        androidx.compose.foundation.lazy.LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = Space.page,
                end = Space.page,
                top = Space.s6,
                bottom = NavBarSpace + Space.s6,
            ),
        ) {
            item(key = "head") {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.heightIn(min = 44.dp).revealOnce(seen, "head", 0)) {
                    Text(
                        tr("Bücher", "Books"),
                        style = MaterialTheme.typography.headlineLarge,
                        color = p.text,
                        modifier = Modifier.weight(1f),
                    )
                    if (canSearch) {
                        de.funke.app.ui.components.HeaderIcon(
                            if (searching) tr("Suche schließen", "Close search") else tr("Bücher suchen", "Search books"),
                            onClick = {
                                searching = !searching
                                if (!searching) query = ""
                            },
                        ) {
                            androidx.compose.material3.Icon(
                                painterResource(R.drawable.ic_search),
                                contentDescription = null,
                                tint = if (searching) p.accent else p.textSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    if (books.size >= 3) {
                        de.funke.app.ui.components.HeaderIcon(tr("Sortieren", "Sort"), onClick = { sorting = true }) {
                            androidx.compose.material3.Icon(
                                painterResource(R.drawable.ic_tune),
                                contentDescription = null,
                                tint = p.textSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    if (books.isNotEmpty()) AddBookButton(onClick = pick)
                }
            }
            if (canSearch) {
                item(key = "search") {
                    de.funke.app.ui.components.Unfold(visible = searching) {
                        de.funke.app.ui.components.SearchField(
                            query,
                            onChange = { query = it },
                            placeholder = tr("Titel oder Autor", "Title or author"),
                            modifier = Modifier.padding(top = Space.s4),
                        )
                    }
                }
            }
            item(key = "message") {
                AnimatedVisibility(visible = importing > 0 || message != null, enter = fadeIn(tween(Motion.Base)), exit = fadeOut(tween(Motion.Fast))) {
                    Text(
                        if (importing > 0) tr("Wird ins Regal gestellt …", "Putting it on the shelf …") else message.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = p.textSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Space.s4)
                            .glass(Shapes.lg)
                            .padding(horizontal = Space.s4, vertical = Space.s3),
                    )
                }
            }

            if (books.isEmpty()) {
                item(key = "empty") { EmptyShelf(onAdd = pick) }
                return@LazyColumn
            }
            if (searching && q.isNotEmpty() && hits.isEmpty()) {
                item(key = "nohits") {
                    Text(
                        tr("Kein Buch gefunden.", "No book found."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = p.textTertiary,
                        modifier = Modifier.padding(start = Space.s1, top = Space.s5),
                    )
                }
                return@LazyColumn
            }

            itemsIndexed(rows, key = { _, row -> row.joinToString("|") { it.id } }) { i, row ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = if (i == 0) Space.s5 else Space.s6)
                        .then(if (de.funke.app.ui.theme.reducedMotion) Modifier else Modifier.animateItem())
                        .revealOnce(seen, row.first().id, Motion.Stagger * (1 + i.coerceAtMost(4))),
                    horizontalArrangement = Arrangement.spacedBy(ShelfGap),
                    verticalAlignment = Alignment.Top,
                ) {
                    row.forEach { b ->
                        BookTile(b, onOpen = { state.openBook(b.id) }, onActions = { actions = b }, modifier = Modifier.weight(1f))
                    }
                    // Angefangene letzte Reihe: die Lücken halten die Spalten bündig
                    repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }

    BookActionsSheet(actions, onDismiss = { actions = null }, onOpen = { b -> actions = null; state.openBook(b.id) })
    SortSheet(
        visible = sorting,
        sort = sort,
        onPick = { next ->
            sorting = false
            sort = next
            Library.updateSort(next)
        },
        onDismiss = { sorting = false },
    )
}

/** Mindestbreite eines Umschlags im Regal und Abstand dazwischen. */
private val MinCover = 96.dp
private val ShelfGap = 14.dp

/** Ab so vielen Büchern gibt es oben die Lupe. */
private const val SEARCH_FROM = 6

/** Blendet ein Element nur beim allerersten Erscheinen ein (nicht erneut, wenn es aus dem Bild und zurück scrollt). */
@Composable
private fun Modifier.revealOnce(seen: MutableSet<String>, key: String, delayMillis: Int): Modifier {
    val first = remember(key) { seen.add(key) }
    return if (first) this.reveal(delayMillis) else this
}

internal fun importReason(r: ImportResult.Reason): String = when (r) {
    ImportResult.Reason.Unsupported -> tr("Diese Datei ist kein PDF und kein EPUB.", "That file is neither a PDF nor an EPUB.")
    ImportResult.Reason.Drm -> tr("Dieses E-Book hat einen Kopierschutz (DRM) und lässt sich nur in der App des Shops lesen.", "This e-book is copy-protected (DRM) and only opens in the store's own app.")
    ImportResult.Reason.Password -> tr("Diese PDF ist mit einem Passwort geschützt.", "This PDF is password-protected.")
    ImportResult.Reason.Broken -> tr("Die Datei ließ sich nicht öffnen. Vielleicht ist sie beschädigt.", "The file couldn't be opened. It may be damaged.")
    ImportResult.Reason.TooBig -> tr("Die Datei ist größer als 300 MB.", "The file is larger than 300 MB.")
    ImportResult.Reason.NoSpace -> tr("Auf dem Handy ist nicht genug Platz frei.", "There isn't enough free space on the phone.")
}

@Composable
private fun AddBookButton(onClick: () -> Unit) {
    // Wie die Werkzeuge oben auf der Startseite: ein schlichtes Symbol ohne Glas und Rand
    de.funke.app.ui.components.HeaderIcon(tr("Buch hinzufügen", "Add book"), onClick = onClick) {
        PlusIcon(Funke.colors.text, Modifier.size(18.dp))
    }
}

/** Leeres Regal: Lumi, eine Frage, ein Knopf. */
@Composable
private fun EmptyShelf(onAdd: () -> Unit) {
    val p = Funke.colors
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = Space.s10)
            .reveal(Motion.Stagger),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Lumi2D(LumiPose.Reading, Modifier.size(136.dp), tappable = false)
        Spacer(Modifier.height(Space.s6))
        Text(
            tr("Was liest du gerade?", "What are you reading?"),
            style = MaterialTheme.typography.titleLarge,
            color = p.text,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Space.s8))
        PrimaryButton(tr("Buch hinzufügen", "Add a book"), onClick = onAdd)
        Spacer(Modifier.height(Space.s3))
        Text(tr("PDF oder EPUB", "PDF or EPUB"), style = MaterialTheme.typography.bodySmall, color = p.textTertiary, textAlign = TextAlign.Center)
    }
}

/**
 * Ein Buch im Regal: der Umschlag, darunter der Titel (immer zwei Zeilen Platz, damit die Reihen bündig bleiben)
 * und der Stand – "34 %" leise, "Neu" im Akzent, "Fertig" mit Haken –, rechts daneben "⋯".
 * Antippen öffnet das Buch; lange drücken oder "⋯" zeigt die Aktionen.
 */
@Composable
private fun BookTile(book: Book, onOpen: () -> Unit, onActions: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Column(
        modifier.bouncyClickable(
            shape = Shapes.md,
            pressedScale = 0.97f,
            onLongClick = onActions,
            onLongClickLabel = tr("Aktionen", "Actions"),
            onClick = onOpen,
        )
    ) {
        BookCover(book, Modifier.fillMaxWidth())
        Spacer(Modifier.height(Space.s2 + 2.dp))
        Text(
            book.title,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, lineHeight = 17.sp),
            color = p.text,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                progressLabel(book),
                style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                color = when {
                    book.finished -> p.success
                    !book.started -> p.accent
                    else -> p.textTertiary
                },
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            MoreButton(book, onActions)
        }
    }
}

/** "⋯": Umbenennen und Entfernen. Breit genug zum Antippen, das Symbol selbst bleibt leise und bündig rechts. */
@Composable
private fun MoreButton(book: Book, onActions: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(width = 40.dp, height = 36.dp)
            .offset(x = Space.s2)
            .bouncyClickable(shape = CircleShape, pressedScale = 0.9f, onClick = onActions)
            .semantics { contentDescription = tr("Mehr zu „${book.title}“", "More for \"${book.title}\"") },
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Icon(painterResource(R.drawable.ic_more), contentDescription = null, tint = Funke.colors.textTertiary, modifier = Modifier.size(16.dp))
    }
}

/** Kurz: "Neu", "34 %", "Fertig ✓". */
internal fun progressLabel(book: Book): String = when {
    book.finished -> tr("Fertig ✓", "Done ✓")
    !book.started -> tr("Neu", "New")
    else -> "${(book.progress * 100).toInt().coerceIn(0, 100)} %"
}

private fun durationLabel(min: Int): String = when {
    min < 60 -> "$min min"
    min % 60 == 0 -> "${min / 60} h"
    else -> "${min / 60} h ${min % 60} min"
}

/** Sortieren: drei Möglichkeiten in einem Blatt, die gewählte mit Haken. Antippen ordnet das Regal sofort neu. */
@Composable
private fun SortSheet(visible: Boolean, sort: LibrarySort, onPick: (LibrarySort) -> Unit, onDismiss: () -> Unit) {
    val p = Funke.colors
    BottomSheet(visible = visible, onDismiss = onDismiss) {
        Text(tr("Sortieren", "Sort"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.padding(start = Space.s1))
        Spacer(Modifier.height(Space.s3))
        LibrarySort.entries.forEach { option ->
            val label = when (option) {
                LibrarySort.Recent -> tr("Zuletzt gelesen", "Recently read")
                LibrarySort.Title -> tr("Nach Titel", "By title")
                LibrarySort.Added -> tr("Neu hinzugefügt", "Recently added")
            }
            val on = option == sort
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .bouncyClickable(shape = Shapes.md, pressedScale = 0.98f, role = Role.RadioButton) { onPick(option) }
                    .semantics { selected = on }
                    .padding(horizontal = Space.s1),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, style = MaterialTheme.typography.bodyLarge, color = if (on) p.text else p.textSecondary, modifier = Modifier.weight(1f))
                if (on) {
                    androidx.compose.material3.Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = p.accent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/** Umschlag-Form: am Rücken (links) fast eckig, vorne weich gerundet – wie ein echtes Buch. */
internal val BookShape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 9.dp, bottomEnd = 9.dp)

private object CoverCache {
    val bitmaps = LruCache<String, ImageBitmap>(40)
}

/**
 * Umschlag im Format 2:3. Ohne Titelbild: ruhige Farbe mit dem Titel darauf.
 * [compact] für kleine Umschläge (unter 64 dp): dann steht nur der erste Buchstabe darauf, statt eines zerquetschten Titels.
 */
@Composable
internal fun BookCover(book: Book, modifier: Modifier = Modifier, compact: Boolean = false) {
    val file = remember(book.id, book.cover) { Library.coverFile(book) }
    val image by produceState<ImageBitmap?>(CoverCache.bitmaps.get(book.id + book.cover), file) {
        if (value == null && file != null) {
            value = withContext(Dispatchers.IO) {
                runCatching { BitmapFactory.decodeFile(file.path)?.asImageBitmap() }.getOrNull()
            }?.also { CoverCache.bitmaps.put(book.id + book.cover, it) }
        }
    }
    Box(
        modifier
            .aspectRatio(2f / 3f)
            .elevation(BookShape, Elevation.Level1)
            .clip(BookShape)
            .background(Funke.colors.track),
    ) {
        val img = image
        if (img != null) {
            Image(img, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            GeneratedCover(book, compact)
        }
        // Buchrücken: leichter Schatten an der linken Kante
        Box(
            Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(Brush.horizontalGradient(listOf(ReaderColors.Spine, ReaderColors.Spine.copy(alpha = 0f)))),
        )
    }
}

/**
 * Umschlag ohne Titelbild (seit 15.5): ruhige Leinenfarbe mit dem Anfangsbuchstaben in einer Buchschrift, auf
 * größeren Umschlägen dazu eine feine eingeprägte Linie als Rahmen – wie ein Einband, nicht wie eine Karte.
 */
@Composable
private fun BoxScope.GeneratedCover(book: Book, compact: Boolean) {
    val (bg, fg) = ReaderColors.Covers[(book.id.hashCode() and 0x7fffffff) % ReaderColors.Covers.size]
    androidx.compose.foundation.layout.BoxWithConstraints(Modifier.matchParentSize().background(bg), contentAlignment = Alignment.Center) {
        if (!compact) {
            Box(
                Modifier
                    .matchParentSize()
                    .padding(start = 11.dp, top = 7.dp, end = 7.dp, bottom = 7.dp)
                    .border(1.dp, fg.copy(alpha = 0.26f), RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp, topEnd = 5.dp, bottomEnd = 5.dp)),
            )
        }
        val letter = book.title.trim().firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "·"
        Text(
            letter,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = with(androidx.compose.ui.platform.LocalDensity.current) { (maxWidth * 0.42f).toSp() },
            color = fg.copy(alpha = 0.92f),
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun BookActionsSheet(book: Book?, onDismiss: () -> Unit, onOpen: (Book) -> Unit) {
    val p = Funke.colors
    var shown by remember { mutableStateOf<Book?>(null) }
    if (book != null) shown = book
    var renaming by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }
    LaunchedEffect(book) {
        renaming = false
        name = book?.title.orEmpty()
    }
    BottomSheet(visible = book != null, onDismiss = onDismiss) {
        val b = shown ?: return@BottomSheet
        Row(verticalAlignment = Alignment.CenterVertically) {
            BookCover(b, Modifier.width(48.dp), compact = true)
            Spacer(Modifier.width(Space.s4))
            Column(Modifier.weight(1f)) {
                Text(b.title, style = MaterialTheme.typography.titleMedium, color = p.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                b.author?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = p.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(
                    when {
                        b.minutes > 0 -> progressLabel(b) + tr(", ${durationLabel(b.minutes)} Fokus", ", ${durationLabel(b.minutes)} of focus")
                        else -> progressLabel(b)
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum, ss03"),
                    color = p.textTertiary,
                    maxLines = 1,
                )
            }
        }
        Spacer(Modifier.height(Space.s6))
        if (renaming) {
            LabeledField(
                label = tr("Titel", "Title"),
                value = name,
                placeholder = b.title,
                maxLength = 160,
                singleLine = true,
                onChange = { name = it },
            )
            Spacer(Modifier.height(Space.s4))
            PrimaryButton(tr("Speichern", "Save"), enabled = name.isNotBlank(), onClick = {
                Library.rename(b.id, name)
                onDismiss()
            })
        } else {
            PrimaryButton(if (b.started) tr("Weiterlesen", "Continue reading") else tr("Lesen", "Read"), onClick = { onOpen(b) })
            Spacer(Modifier.height(Space.s3))
            // Seit 15.2 eine ruhige Liste statt zwei Pillen nebeneinander: Symbol und Wort, Entfernen in Rot.
            SheetAction(R.drawable.ic_pen, tr("Umbenennen", "Rename"), onClick = { renaming = true })
            SheetAction(R.drawable.ic_trash, tr("Aus dem Regal entfernen", "Remove from shelf"), danger = true, onClick = { confirmDelete = true })
        }
    }
    if (confirmDelete) {
        val b = shown
        ConfirmDialog(
            title = tr("Buch entfernen?", "Remove book?"),
            message = tr(
                "„${b?.title.orEmpty()}“ verschwindet aus deinem Regal, mit Lesezeichen und Kapiteln. Die Originaldatei auf deinem Handy bleibt.",
                "\"${b?.title.orEmpty()}\" leaves your shelf, with its bookmarks and chapters. The original file on your phone stays.",
            ),
            confirmText = tr("Entfernen", "Remove"),
            dismissText = tr("Behalten", "Keep"),
            destructive = true,
            onConfirm = {
                confirmDelete = false
                b?.let { Library.remove(it.id) }
                onDismiss()
            },
            onDismiss = { confirmDelete = false },
        )
    }
}

/** Eine Zeile im Aktionen-Blatt: Symbol und Wort, 52 dp hoch, ohne Rahmen. */
@Composable
private fun SheetAction(icon: Int, label: String, danger: Boolean = false, onClick: () -> Unit) {
    val p = Funke.colors
    val tint = if (danger) p.danger else p.text
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.98f, role = Role.Button, onClick = onClick)
            .padding(horizontal = Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Icon(
            painterResource(icon),
            contentDescription = null,
            tint = if (danger) p.danger else p.textSecondary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(Space.s4))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = tint, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
