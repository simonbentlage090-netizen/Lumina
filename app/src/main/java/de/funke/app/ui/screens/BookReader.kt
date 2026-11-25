package de.funke.app.ui.screens

import kotlin.math.roundToInt
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.runtime.derivedStateOf
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.Job
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.LruCache
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.R
import de.funke.app.books.BlockKind
import de.funke.app.books.Book
import de.funke.app.books.BookFormat
import de.funke.app.books.Chapter
import de.funke.app.books.ChapterAi
import de.funke.app.books.ChapterQuota
import de.funke.app.books.LocalChapters
import de.funke.app.books.ChapterSource
import de.funke.app.books.Epub
import de.funke.app.books.EpubBook
import de.funke.app.books.Library
import de.funke.app.books.Mark
import de.funke.app.notes.Note
import de.funke.app.ui.AppState
import de.funke.app.ui.Lang
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.calc.CalculatorPanel
import de.funke.app.ui.components.CloseButton
import de.funke.app.ui.components.CloseIcon
import de.funke.app.ui.components.HideNavBarWhile
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.SegmentedToggle
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.elevation
import de.funke.app.ui.theme.BackgroundPattern
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Palette
import de.funke.app.ui.theme.PreviewTheme
import de.funke.app.ui.components.Unfold
import de.funke.app.ui.theme.UiStyle
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Radius
import de.funke.app.ui.theme.ReaderColors
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// =============================================================================================
// Einstellungen des Readers
// =============================================================================================

/** Farbe der Seite. Papier und Sepia hell, Nacht dunkel (PDFs werden dafür umgekehrt). */
enum class ReaderTheme(
    val id: String,
    private val de: String,
    private val en: String,
    val bg: Color,
    val text: Color,
    val soft: Color,
    /** Akzent auf dieser Seite (Lesezeichen, aktive Werkzeuge): tief auf hellem Papier, hell bei Nacht. */
    val accent: Color,
) {
    Paper("paper", "Papier", "Paper", ReaderColors.PaperBg, ReaderColors.PaperText, ReaderColors.PaperSoft, ReaderColors.InkAccent),
    Sepia("sepia", "Sepia", "Sepia", ReaderColors.SepiaBg, ReaderColors.SepiaText, ReaderColors.SepiaSoft, ReaderColors.InkAccent),
    Night("night", "Nacht", "Night", ReaderColors.NightBg, ReaderColors.NightText, ReaderColors.NightSoft, ReaderColors.NightAccent);

    val label: String get() = tr(de, en)
    val dark: Boolean get() = this == Night
    /** Der "Tisch" um eine verkleinerte Seite herum: etwas dunkler bzw. heller als das Papier. */
    val desk: Color get() = lerp(bg, if (dark) Color.Black else text, if (dark) 0.35f else 0.07f)
    /**
     * Fläche der schwebenden Leiste und des Hinweises: auf Papier heller als die Seite (wie ein Blatt, das darüber
     * liegt), bei Nacht leicht angehoben. Getragen nur von Fläche und weichem Schatten, ohne Rand.
     */
    val bar: Color get() = if (dark) lerp(bg, Color.White, 0.08f) else lerp(bg, Color.White, 0.72f)

    /**
     * Farben der App, abgeleitet von dieser Seite (seit 14.0): Rechner, Notiz, Darstellung und Inhalt tragen im
     * Buch die Farben des Papiers statt der App – so wirkt alles wie aus einem Guss und nichts sticht heraus.
     */
    val palette: Palette
        get() = Palette(
            id = "reader_$id",
            name = label,
            isDark = dark,
            bg = bg,
            bgMid = lerp(bg, text, 0.03f),
            surface = if (dark) lerp(bg, Color.White, 0.04f) else lerp(bg, text, 0.035f),
            glowA = accent,
            glowB = accent,
            accent = accent,
            accentAlt = lerp(accent, Color.White, 0.25f),
            onAccent = if (dark) bg else Color.White,
            text = text,
            textSecondary = lerp(text, soft, 0.55f),
            textTertiary = soft,
            glass = if (dark) Color.White else Color.Black,
            danger = if (dark) Color(0xFFE07A86) else Color(0xFFB3443F),
        )

    companion object {
        fun byId(id: String?): ReaderTheme? = entries.firstOrNull { it.id == id }
    }
}

private class ReaderSettings(context: Context, appDark: Boolean) {
    private val sp = context.getSharedPreferences("funke", Context.MODE_PRIVATE)
    var theme by mutableStateOf(ReaderTheme.byId(sp.getString("reader_theme", null)) ?: if (appDark) ReaderTheme.Night else ReaderTheme.Paper)
        private set
    /** Schriftgröße für E-Books in sp (14–26). */
    var fontSize by mutableIntStateOf(sp.getInt("reader_font", 18).coerceIn(MIN_FONT, MAX_FONT))
        private set
    var serif by mutableStateOf(sp.getBoolean("reader_serif", true))
        private set

    /** Den Hinweis zeigen? Bis man das Menü einmal per Doppeltippen geöffnet hat, höchstens dreimal. */
    val showHint: Boolean get() = !sp.getBoolean("reader_menu_learned", false) && sp.getInt("reader_hint_count", 0) < 3
    fun countHint() { sp.edit().putInt("reader_hint_count", sp.getInt("reader_hint_count", 0) + 1).apply() }
    fun learnedMenu() { if (!sp.getBoolean("reader_menu_learned", false)) sp.edit().putBoolean("reader_menu_learned", true).apply() }
    /** Hinweis über dem Rechner ("Seite verschieben, mit zwei Fingern zoomen"): die ersten zwei Male. */
    val showViewerHint: Boolean get() = sp.getInt("reader_viewer_hint", 0) < 2
    fun countViewerHint() { sp.edit().putInt("reader_viewer_hint", sp.getInt("reader_viewer_hint", 0) + 1).apply() }

    fun updateTheme(t: ReaderTheme) { theme = t; sp.edit().putString("reader_theme", t.id).apply() }
    fun updateFont(size: Int) { fontSize = size.coerceIn(MIN_FONT, MAX_FONT); sp.edit().putInt("reader_font", fontSize).apply() }
    fun updateSerif(on: Boolean) { serif = on; sp.edit().putBoolean("reader_serif", on).apply() }

    companion object {
        const val MIN_FONT = 14
        const val MAX_FONT = 26
    }
}

/** Ohne Wischen, Tippen oder Umblättern zählt die Zeit nach 5 Minuten nicht mehr (Handy liegt nur offen da). */
private const val IDLE_LIMIT_MS = 5 * 60 * 1000L

/**
 * Werkzeug, das unten anliegt. Bei Notiz und Darstellung rückt die Seite nach oben und wird kleiner, damit sie ganz
 * sichtbar bleibt; beim Rechner bleibt sie groß und wird zum verschiebbaren Ausschnitt ([PageViewer]).
 */
private enum class ReaderTool { None, Note, Calc, Look }

/** Wo gerade gelesen wird. Bewusst kein Compose-Zustand: Umblättern soll nichts neu zeichnen. */
private class ReaderAnchor(var section: Int, var offset: Int)

/** Die Seite, zu der eine Notiz gehört (Seite ab 1 zur Anzeige, Stelle zum Zurückspringen). */
private class NoteTarget(val existing: Note?, val page: Int, val section: Int, val offset: Int)

/**
 * Die offene Notiz beim Lesen: Text, Kennung und ob diktiert wurde. Gespeichert wird beim Tippen und beim Schließen –
 * aber nur, was sich hier geändert hat. So schreibt ein altes Feld nie über das, was man inzwischen im großen Editor
 * geändert hat.
 */
private class ReaderNote(val target: NoteTarget) {
    var value by mutableStateOf(target.existing?.text.orEmpty().let { TextFieldValue(it, TextRange(it.length)) })
    var id by mutableStateOf(target.existing?.id)
    var voice by mutableStateOf(target.existing?.voice ?: false)
    private var savedText = target.existing?.text.orEmpty()
    private var savedVoice = target.existing?.voice ?: false

    fun save(state: AppState, book: Book) {
        if (value.text == savedText && voice == savedVoice) return
        id = state.saveBookNote(id, book, target.page, target.section, target.offset, value.text, voice)
        savedText = value.text
        savedVoice = voice
    }
}

// =============================================================================================
// Reader
// =============================================================================================

/**
 * Buch lesen (seit 14.0 neu): Seite für Seite, ruhig, ohne Prozent und ohne Schieberegler.
 *
 *  - Wischen oder an den Rand tippen blättert sofort. Einmal in die Mitte tippen tut nichts (damit man beim
 *    Lesen nichts aus Versehen öffnet), doppelt tippen öffnet das Menü. Beim ersten Mal steht das kurz da.
 *  - Menü: oben links das Inhaltsverzeichnis (kommt von links), in der Mitte das Kapitel, rechts Schließen.
 *    Unten eine schwebende Leiste: Notiz, Rechner, Merken, Darstellung.
 *  - Notiz und Darstellung liegen unten an, ohne Abdunklung. Die Seite rückt dafür nach oben und wird kleiner
 *    (wie ein Blatt auf dem Tisch), sodass sie ganz sichtbar bleibt und man weiterblättern kann.
 *  - Rechner (seit 15.5): Die Seite bleibt oben in voller Breite und lässt sich dort verschieben und zoomen
 *    ([PageViewer]); unten liegt der Rechner. So sieht man die Aufgabe groß, statt einer winzigen ganzen Seite.
 *  - Notizen gehören zur Seite, auf der man sie schreibt; im Inhalt gibt es sie gesammelt. Getippt oder diktiert
 *    (seit 15.5 mit derselben Leiste wie im Notiz-Editor: Sprache, Mikrofon, Aufräumen).
 * Die Position wird beim Umblättern gemerkt. Jede aktive Minute zählt als Fokuszeit für das Tagesziel.
 */
@Composable
fun BookReader(state: AppState, bookId: String, onClose: () -> Unit) {
    val context = LocalContext.current
    remember(context) { Library.attach(context); true }
    val book = Library.get(bookId)
    if (book == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }
    val appDark = Funke.colors.isDark
    val settings = remember { ReaderSettings(context, appDark) }
    val theme = settings.theme
    val scope = rememberCoroutineScope()
    val still = reducedMotion
    val haptics = LocalHaptics.current
    // Die Leiste der App bleibt weg, solange das Buch offen ist
    HideNavBarWhile(true)

    var chrome by remember { mutableStateOf(false) }
    var contents by remember { mutableStateOf(false) }
    var tool by remember { mutableStateOf(ReaderTool.None) }
    var calcCollapsed by remember { mutableStateOf(false) }
    // Beim Rechner: Ausschnitt der Seite oben (verschieben, zoomen)
    val viewer = remember { PageViewer() }
    var hint by remember { mutableStateOf(settings.showHint) }
    var lastActive by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var resumed by remember { mutableStateOf(true) }
    val anchor = remember(bookId) { ReaderAnchor(book.section, book.offset) }
    var nav by remember { mutableStateOf<ReaderNav?>(null) }

    BackHandler {
        when {
            contents -> contents = false
            tool != ReaderTool.None -> tool = ReaderTool.None
            chrome -> chrome = false
            else -> onClose()
        }
    }

    ImmersiveMode(theme.dark)

    // Pausiert, sobald die App im Hintergrund ist
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) { resumed = true; lastActive = System.currentTimeMillis() }
            if (e == Lifecycle.Event.ON_PAUSE) resumed = false
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }
    // Fokuszeit im Buch: eine Minute aktiv gelesen → zählt (eine halbe angefangene Minute beim Schließen auch)
    val seconds = remember { intArrayOf(0) }
    val credited = remember { intArrayOf(0) }
    val prefs: android.content.SharedPreferences = remember { context.getSharedPreferences("funke", Context.MODE_PRIVATE) }
    LaunchedEffect(bookId) {
        while (true) {
            delay(1000)
            if (resumed && System.currentTimeMillis() - lastActive < IDLE_LIMIT_MS) {
                // Erinnerungen wissen so, dass gerade gelesen wird
                if (seconds[0] % 30 == 0) prefs.edit().putLong(de.funke.app.notify.Reminders.K_BOOK_UNTIL, System.currentTimeMillis() + 90_000L).apply()
                seconds[0]++
                if (seconds[0] >= 60) {
                    seconds[0] = 0
                    credited[0]++
                    state.creditBookMinute(bookId)
                }
            }
        }
    }
    DisposableEffect(bookId) {
        onDispose {
            if (seconds[0] >= 30) { credited[0]++; state.creditBookMinute(bookId) }
            prefs.edit().remove(de.funke.app.notify.Reminders.K_BOOK_UNTIL).apply()
            Library.commitPosition(bookId)
            if (credited[0] > 0) state.lastBookMinutes = bookId to credited[0]
        }
    }
    LaunchedEffect(Unit) {
        if (hint) {
            settings.countHint()
            delay(5200)
            hint = false
        }
    }

    // Kapitel: zuerst das eingebaute Inhaltsverzeichnis des Buchs (bei PDFs einmal je Buch, für PDFs aus der Zeit
    // vor 15.0). Hat das Buch keins, sucht Lumi einmal von selbst: mit der Lumina-KI, solange sie an ist und die drei
    // Bücher dieser Woche nicht aufgebraucht sind, sonst ohne KI aus dem Text (seit 15.2).
    val ai = remember { ChapterSearch() }
    val appContext = LocalContext.current.applicationContext
    LaunchedEffect(bookId) {
        Library.loadChapters(bookId)
        if (book.format == BookFormat.Pdf && !book.outlineTried) {
            val found = withContext(Dispatchers.IO) { de.funke.app.books.PdfOutline.read(Library.file(book), book.sections) }
            Library.setOutline(book.id, found)
        }
        val b = Library.get(bookId) ?: return@LaunchedEffect
        val possible = b.format == BookFormat.Epub || Build.VERSION.SDK_INT >= 35
        // Auch Bücher, bei denen früher nur die KI gesucht hat (ohne Erfolg), bekommen einmal die Suche im Text
        if (b.chapters.isEmpty() && (!b.aiTried || !b.textTried) && possible) {
            ai.run(b, state, epub = null, context = appContext)
        }
    }
    // Sprung von außen (z. B. "Zur Seite" bei einer Notiz)
    val jump = state.readerJump
    LaunchedEffect(jump, nav) {
        val j = jump ?: return@LaunchedEffect
        val n = nav ?: return@LaunchedEffect
        state.readerJump = null
        n.jump(n.pageOfPos(j.first, j.second))
    }

    val bookNotes = remember(state.notes, bookId) { state.bookNotes(bookId) }
    // Zu welcher Seite die offene Notiz gehört – fest beim Öffnen, damit Weiterblättern sie nicht verschiebt
    var noteTarget by remember { mutableStateOf<NoteTarget?>(null) }
    val readerNote = remember(noteTarget) { noteTarget?.let { ReaderNote(it) } }
    // Diktieren und Aufräumen wie im Notiz-Editor; die große Sprach-Ansicht liegt über dem ganzen Buch
    val speech = rememberNoteVoice(state, original = noteTarget?.existing?.original, key = noteTarget)
    if (readerNote != null) {
        speech.bind(
            read = { readerNote.value.text },
            write = { t -> readerNote.value = TextFieldValue(t, TextRange(t.length)) },
            save = { _ -> readerNote.save(state, book) },
            saveOriginal = { before -> state.saveNoteOriginal(readerNote.id, before) },
            markVoice = { readerNote.voice = true },
        )
        LaunchedEffect(readerNote, readerNote.value.text) {
            delay(500)
            readerNote.save(state, book)
        }
        DisposableEffect(readerNote) { onDispose { readerNote.save(state, book) } }
    }
    fun openNote(page: Int, note: Note? = null) {
        val n = nav ?: return
        val ex = note ?: bookNotes.firstOrNull { n.pageOfPos(it.section, it.offset) == page }
        noteTarget = if (ex != null) NoteTarget(ex, ex.page.takeIf { it > 0 } ?: (page + 1), ex.section, ex.offset)
        else NoteTarget(null, page + 1, anchor.section, anchor.offset)
        chrome = false
        tool = ReaderTool.Note
    }

    val density = LocalDensity.current
    // Höhe des Werkzeugs unten (mit Tastatur). Nur in der Animation gelesen, nicht beim Zeichnen des Readers:
    // sonst würde während die Tastatur hochfährt jedes Bild den ganzen Reader neu aufbauen.
    val toolHeight = remember { mutableIntStateOf(0) }
    val pageScale = remember { Animatable(1f) }
    val tapHandler = rememberUpdatedState<(ReaderTap) -> Unit> { t ->
        lastActive = System.currentTimeMillis()
        val n = nav
        when (t) {
            ReaderTap.Prev -> n?.let { scope.launch { it.go(it.page - 1) } }
            ReaderTap.Next -> n?.let { scope.launch { it.go(it.page + 1) } }
            ReaderTap.Menu -> {
                hint = false
                settings.learnedMenu()
                haptics?.tick()
                if (tool != ReaderTool.None) tool = ReaderTool.None else chrome = !chrome
            }
            ReaderTap.Middle -> Unit
        }
    }
    val onTap: (ReaderTap) -> Unit = remember { { t -> tapHandler.value(t) } }
    BoxWithConstraints(Modifier.fillMaxSize().background(theme.desk)) {
        val fullH = constraints.maxHeight.toFloat().coerceAtLeast(1f)
        val fullW = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        viewer.width = fullW
        val gap = with(density) { Space.s3.toPx() }
        // Notiz und Darstellung: die ganze Seite kleiner darüber (wie ein Blatt auf dem Tisch)
        val lifts = tool == ReaderTool.Note || tool == ReaderTool.Look
        LaunchedEffect(lifts, fullH, still) {
            snapshotFlow {
                val h = toolHeight.intValue
                if (!lifts || h == 0) 1f else ((fullH - h - gap) / fullH).coerceIn(0.4f, 1f)
            }.collectLatest { t ->
                if (still) pageScale.snapTo(t) else pageScale.animateTo(t, tween(Motion.Medium, easing = Motion.EaseOut))
            }
        }
        val cornerPx = with(density) { 28.dp.toPx() }
        // Unten rund wie die obere Kante des Rechners darunter
        val viewCorner = with(density) { Radius.sheet.toPx() }
        // Werkzeug zu: Höhe vergessen, damit das nächste Werkzeug nicht mit der alten Höhe anfängt
        LaunchedEffect(tool) { if (tool == ReaderTool.None) toolHeight.intValue = 0 }

        // Rechner: Die Seite bleibt groß; sichtbar ist der Teil über dem Rechner. Die Unterkante läuft mit dem
        // Rechner mit (gleiche Dauer und Kurve wie sein Hereinschieben), beim Schließen zurück zur ganzen Seite.
        val calcOpen = tool == ReaderTool.Calc
        val span: () -> ClosedFloatingPointRange<Float> = { nav?.let { it.contentSpan(it.page) } ?: (0f..fullH) }
        LaunchedEffect(calcOpen, fullH, still) {
            if (calcOpen) {
                if (!viewer.active) viewer.reset(fullH)
                var first = true
                snapshotFlow { toolHeight.intValue }.collectLatest { h ->
                    if (h <= 0) return@collectLatest
                    val target = (fullH - h - gap).coerceAtLeast(fullH * 0.2f)
                    val b0 = viewer.bottom
                    val y0 = viewer.ty
                    // Beim Öffnen oben am Inhalt der Seite anfangen (ohne leeren Rand darüber)
                    val y1 = (if (first) -span().start * viewer.zoom else y0).coerceIn(viewer.yRange(span(), view = target))
                    val spec = when {
                        still -> tween<Float>(Motion.Fast)
                        first -> tween(Motion.Slow, easing = Motion.Drawer)
                        else -> tween(Motion.Medium, easing = Motion.EaseOut)
                    }
                    first = false
                    viewer.job?.cancel()
                    Animatable(0f).animateTo(1f, spec) {
                        viewer.bottom = mix(b0, target, value)
                        viewer.ty = mix(y0, y1, value)
                    }
                }
            } else if (viewer.active) {
                viewer.job?.cancel()
                val z0 = viewer.zoom
                val x0 = viewer.tx
                val y0 = viewer.ty
                val b0 = viewer.bottom
                Animatable(0f).animateTo(1f, tween(if (still) Motion.Fast else Motion.Base, easing = Motion.EaseOut)) {
                    viewer.zoom = mix(z0, 1f, value)
                    viewer.tx = mix(x0, 0f, value)
                    viewer.ty = mix(y0, 0f, value)
                    viewer.bottom = mix(b0, fullH, value)
                }
                viewer.reset(-1f)
            }
        }
        // Umgeblättert: oben links auf der neuen Seite anfangen (der Zoom bleibt)
        LaunchedEffect(calcOpen, nav) {
            val nv = nav ?: return@LaunchedEffect
            if (!calcOpen) return@LaunchedEffect
            snapshotFlow { nv.pager.settledPage }.drop(1).collect {
                viewer.job?.cancel()
                val x0 = viewer.tx
                val y0 = viewer.ty
                val y1 = (-span().start * viewer.zoom).coerceIn(viewer.yRange(span()))
                viewer.job = launch {
                    Animatable(0f).animateTo(1f, tween(if (still) Motion.Fast else Motion.Base, easing = Motion.EaseOut)) {
                        viewer.tx = mix(x0, 0f, value)
                        viewer.ty = mix(y0, y1, value)
                    }
                }
            }
        }

        // ----- Seiten (bei einem Werkzeug verkleinert, wie ein Blatt auf dem Tisch) -----
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val k = pageScale.value
                    scaleX = k
                    scaleY = k
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    val lifted = k < 0.999f
                    // Beim Rechner: nur der Teil über ihm, unten weich gerundet
                    val b = viewer.bottom
                    val cut = !lifted && b >= 0f && b < size.height - 0.5f
                    clip = lifted || cut
                    shape = if (cut) ViewportShape(b, viewCorner) else RoundedCornerShape(cornerPx)
                    shadowElevation = if (lifted) (1f - k) * 40f else 0f
                    translationY = if (lifted) gap * (1f - k) * 2f else 0f
                }
                // Verschieben und Zoomen im Ausschnitt (nur Ebenen-Werte: nichts wird neu gesetzt, nichts ruckelt)
                .graphicsLayer {
                    val z = viewer.zoom
                    scaleX = z
                    scaleY = z
                    translationX = viewer.tx
                    translationY = viewer.ty
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .background(theme.bg),
        ) {
            when (book.format) {
                BookFormat.Pdf -> PdfPages(
                    book = book,
                    theme = theme,
                    viewZoom = { viewer.zoom },
                    startPage = anchor.section,
                    onNav = { nav = it },
                    onPage = { page, count ->
                        lastActive = System.currentTimeMillis()
                        anchor.section = page
                        anchor.offset = 0
                        Library.savePosition(book.id, page, 0, (page + 1f) / count)
                    },
                    onTap = onTap,
                )
                BookFormat.Epub -> EpubPages(
                    book = book,
                    settings = settings,
                    anchor = anchor,
                    onNav = { nav = it },
                    onPage = { section, offset, page, count ->
                        lastActive = System.currentTimeMillis()
                        anchor.section = section
                        anchor.offset = offset
                        Library.savePosition(book.id, section, offset, (page + 1f) / count)
                    },
                    onTap = onTap,
                    onEpub = { ai.epub = it },
                )
            }
        }

        val n = nav
        val quiet = tool == ReaderTool.None && !chrome
        if (calcOpen && n != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(n) {
                        detectViewerGestures(
                            viewer = viewer,
                            span = span,
                            pager = n.pager,
                            onActive = { lastActive = System.currentTimeMillis() },
                            onTurn = { d -> scope.launch { n.go(n.page + d) } },
                            onSwipeEnd = { vx ->
                                val pager = n.pager
                                val off = pager.currentPageOffsetFraction
                                val fast = with(density) { 400.dp.toPx() }
                                val target = when {
                                    vx < -fast && off > 0f -> pager.currentPage + 1
                                    vx > fast && off < 0f -> pager.currentPage - 1
                                    else -> pager.currentPage
                                }.coerceIn(0, n.count - 1)
                                scope.launch { pager.animateScrollToPage(target) }
                            },
                            onFling = { v ->
                                viewer.job = scope.launch {
                                    val ax = Animatable(viewer.tx).apply { updateBounds(viewer.minX(), 0f) }
                                    val yr = viewer.yRange(span())
                                    val ay = Animatable(viewer.ty).apply { updateBounds(yr.start, yr.endInclusive) }
                                    val decay = exponentialDecay<Float>(frictionMultiplier = 1.6f)
                                    launch { ax.animateDecay(v.x, decay) { viewer.tx = value } }
                                    launch { ay.animateDecay(v.y, decay) { viewer.ty = value } }
                                }
                            },
                            onZoomTo = { z, c ->
                                viewer.job = scope.launch {
                                    val z0 = viewer.zoom
                                    val x0 = viewer.tx
                                    val y0 = viewer.ty
                                    val k = z / z0
                                    val x1 = (c.x - (c.x - x0) * k).coerceIn(viewer.minX(z), 0f)
                                    val y1 = (c.y - (c.y - y0) * k).coerceIn(viewer.yRange(span(), z))
                                    Animatable(0f).animateTo(1f, tween(if (still) Motion.Fast else Motion.Medium, easing = Motion.EaseOut)) {
                                        viewer.zoom = mix(z0, z, value)
                                        viewer.tx = mix(x0, x1, value)
                                        viewer.ty = mix(y0, y1, value)
                                    }
                                }
                            },
                        )
                    }
            )
        }
        if (n != null) {
            // Lesezeichen-Band oben rechts und Seitenzahl unten: leise, nur wenn nichts anderes offen ist
            BookmarkRibbon(n, book.marks, theme, visible = tool == ReaderTool.None, modifier = Modifier.align(Alignment.TopEnd))
            PageFooter(
                nav = n,
                theme = theme,
                notes = bookNotes,
                visible = quiet,
                onNote = { openNote(it) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        // Hinweis, Menü, Werkzeuge und Inhalt in den Farben der Seite: ruhiger Modern-Stil mit Inter, ohne Glas und
        // Konturen – auch wenn die App im Cartoon-Stil ist. So wirkt das Buch wie aus einem Guss.
        val readerColors = remember(theme) { theme.palette }
        PreviewTheme(readerColors, UiStyle.Modern, BackgroundPattern.None, font = de.funke.app.ui.theme.AppFont.Inter) {
        AnimatedVisibility(
            visible = hint && quiet,
            enter = fadeIn(tween(if (still) Motion.Fast else Motion.Slow, delayMillis = 400, easing = Motion.EaseOut)),
            exit = fadeOut(tween(Motion.Base)),
            modifier = Modifier.align(Alignment.Center),
        ) { ReaderHint(theme) }
        // Beim Rechner, die ersten zwei Male: dass sich die Seite verschieben und zoomen lässt
        if (calcOpen && n != null) ViewerHint(settings, theme, Modifier.align(Alignment.TopCenter))

        AnimatedVisibility(
            visible = chrome && n != null && tool == ReaderTool.None,
            enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)),
            exit = fadeOut(tween(Motion.Fast)),
            modifier = Modifier.fillMaxSize(),
        ) {
            if (n != null) ReaderChrome(
                book = book,
                theme = theme,
                nav = n,
                onContents = { chrome = false; contents = true },
                onClose = onClose,
                onTool = { t ->
                    chrome = false
                    if (t == ReaderTool.Note) openNote(n.page) else tool = t
                },
                onMark = {
                    val here = book.marks.filter { n.pageOfPos(it.section, it.offset) == n.page }
                    haptics?.tick()
                    if (here.isNotEmpty()) Library.removeMarks(book.id, here)
                    else Library.addMark(book.id, Mark(anchor.section, anchor.offset, n.page + 1, System.currentTimeMillis()))
                },
                onDismiss = { chrome = false },
            )
        }

        // ----- Werkzeug unten (ohne Abdunklung) -----
        AnimatedVisibility(
            visible = tool != ReaderTool.None,
            enter = if (still) fadeIn(tween(Motion.Fast)) else slideInVertically(tween(Motion.Slow, easing = Motion.Drawer)) { it } + fadeIn(tween(Motion.Fast)),
            exit = if (still) fadeOut(tween(Motion.Fast)) else slideOutVertically(tween(Motion.Base, easing = Motion.EaseOut)) { it } + fadeOut(tween(Motion.Base)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            var shown by remember { mutableStateOf(tool) }
            if (tool != ReaderTool.None) shown = tool
            Box(
                Modifier
                    .fillMaxWidth()
                    .onSizeChanged { toolHeight.intValue = it.height }
                    .imePadding(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                ToolSurface {
                    when (shown) {
                        ReaderTool.Note -> readerNote?.let { rn ->
                            NotePanel(
                                state = state,
                                note = rn,
                                speech = speech,
                                onExpand = {
                                    // Ganz an den Editor übergeben: Das Feld hier geht zu, damit nichts Altes zurückgeschrieben wird
                                    rn.save(state, book)
                                    speech.stop()
                                    tool = ReaderTool.None
                                    rn.id?.let { state.openNoteEditor(it, inSession = false) }
                                },
                                onDone = {
                                    rn.save(state, book)
                                    speech.stop()
                                    tool = ReaderTool.None
                                },
                            )
                        }
                        ReaderTool.Calc -> CalculatorPanel(
                            onClose = { tool = ReaderTool.None },
                            collapsed = calcCollapsed,
                            onCollapse = { calcCollapsed = it },
                        )
                        ReaderTool.Look -> LookPanel(book, settings, onDone = { tool = ReaderTool.None })
                        ReaderTool.None -> Unit
                    }
                }
            }
        }

        // ----- Inhalt (von links) -----
        ContentsDrawer(
            visible = contents,
            book = book,
            state = state,
            nav = n,
            ai = ai,
            notes = bookNotes,
            onJump = { page ->
                contents = false
                n?.let { scope.launch { it.jump(page) } }
            },
            onOpenNote = { note ->
                contents = false
                val nv = n ?: return@ContentsDrawer
                scope.launch {
                    val page = nv.pageOfPos(note.section, note.offset)
                    nv.jump(page)
                    openNote(page, note)
                }
            },
            onDismiss = { contents = false },
        )

        // Aufräumen-Blatt, Einwilligung und die große Sprach-Ansicht der Notiz: über allem
        NoteVoiceOverlays(speech, state)
        }
    }
}

/**
 * Was die Seitenansicht nach außen zeigt: aktuelle Seite, Seitenzahl, Blättern und Springen,
 * und auf welcher Seite eine Stelle (Kapitel, Lesezeichen, Notiz) liegt.
 */
private class ReaderNav(
    val pager: PagerState,
    val count: Int,
    /** Seite zu einer Stelle: PDF = Seite, EPUB = Abschnitt + Zeichen. */
    val pageOfPos: (section: Int, offset: Int) -> Int,
    val pageOfChapter: (Chapter) -> Int,
    /** Wo auf einer Seite Inhalt steht (px von oben, ohne die leeren Ränder) – für den Ausschnitt beim Rechner. */
    val contentSpan: (page: Int) -> ClosedFloatingPointRange<Float>,
    /** "Bewegung reduzieren": Blättern springt statt zu gleiten */
    private val still: Boolean,
) {
    val page: Int get() = pager.currentPage
    suspend fun go(page: Int) {
        if (page !in 0 until count) return
        if (still) pager.scrollToPage(page) else pager.animateScrollToPage(page, animationSpec = tween(Motion.Medium, easing = Motion.EaseOut))
    }
    suspend fun jump(page: Int) {
        if (page in 0 until count) pager.scrollToPage(page)
    }
    /** Kapitel, in dem die Seite liegt (das letzte, das davor oder auf ihr beginnt). */
    fun chapterAt(page: Int, chapters: List<Chapter>): Chapter? = chapters.lastOrNull { pageOfChapter(it) <= page }
}

// =============================================================================================
// Tippen: Rand blättert sofort, Mitte doppelt = Menü
// =============================================================================================

private enum class ReaderTap { Prev, Next, Menu, Middle }

/**
 * Eigene Erkennung statt detectTapGestures: Dort würde jedes Tippen auf ein mögliches Doppeltippen warten,
 * und das Blättern am Rand fühlte sich träge an. Hier blättert der Rand sofort; nur in der Mitte wird kurz
 * auf ein zweites Tippen gewartet. Wischen (vom Pager übernommen) zählt nicht als Tippen.
 * [enabled] = false (z. B. gezoomt) schluckt Tippen am Rand.
 */
private suspend fun PointerInputScope.detectReaderTaps(enabled: () -> Boolean, onTap: (ReaderTap) -> Unit) {
    val slop = viewConfiguration.touchSlop
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val up = waitForUpOrCancellation() ?: return@awaitEachGesture
        if ((up.position - down.position).getDistance() > slop) return@awaitEachGesture
        val x = up.position.x
        val w = size.width.toFloat()
        when {
            x < w * 0.28f -> if (enabled()) onTap(ReaderTap.Prev)
            x > w * 0.72f -> if (enabled()) onTap(ReaderTap.Next)
            else -> {
                val second = withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) { awaitFirstDown(requireUnconsumed = false) }
                if (second == null) {
                    onTap(ReaderTap.Middle)
                } else {
                    val up2 = waitForUpOrCancellation()
                    if (up2 != null && (up2.position - second.position).getDistance() <= slop) onTap(ReaderTap.Menu)
                }
            }
        }
    }
}

// =============================================================================================
// Rechner beim Lesen: die Seite oben groß, verschieb- und zoombar (seit 15.5)
// =============================================================================================

/**
 * Ausschnitt der Seite über dem Rechner. Die Seite behält ihre volle Größe – nichts wird neu umbrochen –, gezeigt
 * wird nur der Teil bis [bottom], verschoben um [tx]/[ty] und vergrößert um [zoom] (Ursprung oben links).
 * Alles sind nur Ebenen-Werte: Verschieben und Zoomen zeichnen die Seite nicht neu.
 */
@Stable
private class PageViewer {
    var zoom by mutableFloatStateOf(1f)
    var tx by mutableFloatStateOf(0f)
    var ty by mutableFloatStateOf(0f)
    /** Unterkante des Ausschnitts in px; negativ = kein Ausschnitt (ganze Seite). */
    var bottom by mutableFloatStateOf(-1f)
    /** Breite der ganzen Seite in px. */
    var width = 1f
    /** Laufende Bewegung (Schwung, Zoom per Doppeltippen) – jede neue Berührung hält sie an. */
    var job: Job? = null

    val active: Boolean get() = bottom >= 0f

    fun reset(bottom: Float) {
        zoom = 1f
        tx = 0f
        ty = 0f
        this.bottom = bottom
    }

    fun minX(z: Float = zoom): Float = width - width * z

    /** Senkrecht: höchstens bis der Inhalt ([span]) oben bündig steht, mindestens bis er unten bündig steht. */
    fun yRange(span: ClosedFloatingPointRange<Float>, z: Float = zoom, view: Float = bottom): ClosedFloatingPointRange<Float> {
        val hi = -span.start * z
        val lo = view - span.endInclusive * z
        return if (lo >= hi) hi..hi else lo..hi
    }
}

private fun mix(a: Float, b: Float, f: Float): Float = a + (b - a) * f

/** Ausschnitt über dem Rechner: oben die Kanten des Bildschirms, unten weich gerundet. */
private class ViewportShape(private val bottom: Float, private val radius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val b = bottom.coerceIn(0f, size.height)
        val r = CornerRadius(radius.coerceAtMost(b / 2f))
        return Outline.Rounded(RoundRect(0f, 0f, size.width, b, CornerRadius.Zero, CornerRadius.Zero, r, r))
    }
}

/**
 * Gesten im Ausschnitt: Ein Finger verschiebt (mit Schwung), zwei Finger zoomen (1- bis 4-fach, um die Mitte der
 * Finger). Ungezoomt blättert seitliches Wischen wie sonst – die Seite folgt dem Finger –, und Tippen an den Rand
 * blättert sofort. Doppelt tippen vergrößert auf das Doppelte, gezoomt zurück auf die ganze Breite.
 * Unterhalb des Ausschnitts (dort liegt der Rechner) passiert nichts.
 */
private suspend fun PointerInputScope.detectViewerGestures(
    viewer: PageViewer,
    span: () -> ClosedFloatingPointRange<Float>,
    pager: PagerState,
    onActive: () -> Unit,
    onTurn: (Int) -> Unit,
    onSwipeEnd: (velocityX: Float) -> Unit,
    onFling: (Velocity) -> Unit,
    onZoomTo: (zoom: Float, center: Offset) -> Unit,
) {
    val slop = viewConfiguration.touchSlop
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (!viewer.active || down.position.y > viewer.bottom) return@awaitEachGesture
        viewer.job?.cancel()
        onActive()
        // Letzte Positionen des ersten Fingers (für den Schwung beim Loslassen)
        val samples = ArrayDeque<Pair<Long, Offset>>()
        samples.addLast(down.uptimeMillis to down.position)
        var mode = 0 // 0 = noch offen, 1 = verschieben/zoomen, 2 = blättern
        var multi = false
        var pinched = false
        var total = Offset.Zero
        while (true) {
            val event = awaitPointerEvent()
            val pressed = event.changes.filter { it.pressed }
            if (pressed.isEmpty()) break
            if (pressed.size >= 2) multi = true
            val pan = event.calculatePan()
            val dz = event.calculateZoom()
            if (mode == 0) {
                total += pan
                when {
                    multi && kotlin.math.abs(dz - 1f) > 0.005f -> mode = 1
                    total.getDistance() > slop ->
                        mode = if (!multi && viewer.zoom < 1.01f && kotlin.math.abs(total.x) > kotlin.math.abs(total.y)) 2 else 1
                }
            }
            if (mode == 1) {
                val c = pressed.fold(Offset.Zero) { acc, ch -> acc + ch.position } / pressed.size.toFloat()
                val z0 = viewer.zoom
                val z1 = (z0 * dz).coerceIn(1f, 4f)
                if (z1 != z0) pinched = true
                val k = z1 / z0
                // Der Punkt unter den Fingern bleibt unter den Fingern
                val x = c.x - (c.x - pan.x - viewer.tx) * k
                val y = c.y - (c.y - pan.y - viewer.ty) * k
                viewer.zoom = z1
                viewer.tx = x.coerceIn(viewer.minX(z1), 0f)
                viewer.ty = y.coerceIn(viewer.yRange(span(), z1))
            } else if (mode == 2) {
                pager.dispatchRawDelta(-pan.x)
            }
            if (mode != 0) event.changes.forEach { if (it.positionChanged()) it.consume() }
            event.changes.firstOrNull { it.id == down.id && it.pressed }?.let { ch ->
                samples.addLast(ch.uptimeMillis to ch.position)
                while (samples.size > 2 && ch.uptimeMillis - samples.first().first > 100L) samples.removeFirst()
            }
        }
        val velocity = run {
            val (t0, p0) = samples.first()
            val (t1, p1) = samples.last()
            val dt = (t1 - t0).coerceAtLeast(1L) / 1000f
            if (samples.size < 2) Velocity(0f, 0f) else Velocity((p1.x - p0.x) / dt, (p1.y - p0.y) / dt)
        }
        when (mode) {
            2 -> onSwipeEnd(velocity.x)
            1 -> when {
                // Kaum vergrößert: zurück auf die ganze Breite
                pinched && viewer.zoom < 1.08f -> onZoomTo(1f, Offset(0f, 0f))
                !multi -> onFling(velocity)
            }
            else -> {
                if (multi) return@awaitEachGesture
                val x = down.position.x
                val w = size.width
                val zoomed = viewer.zoom > 1.01f
                when {
                    !zoomed && x < w * 0.28f -> onTurn(-1)
                    !zoomed && x > w * 0.72f -> onTurn(1)
                    else -> {
                        val second = withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) { awaitFirstDown(requireUnconsumed = false) }
                        if (second != null) {
                            val up = waitForUpOrCancellation()
                            if (up != null && (up.position - second.position).getDistance() <= slop) {
                                if (zoomed) onZoomTo(1f, second.position) else onZoomTo(2f, second.position)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Beim Rechner, die ersten zwei Male: kurz oben, dass sich die Seite verschieben und zoomen lässt. */
@Composable
private fun ViewerHint(settings: ReaderSettings, theme: ReaderTheme, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!settings.showViewerHint) return@LaunchedEffect
        settings.countViewerHint()
        delay(Motion.Slow.toLong())
        show = true
        delay(3200)
        show = false
    }
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)),
        exit = fadeOut(tween(Motion.Base)),
        modifier = modifier,
    ) {
        Text(
            tr("Seite verschieben, mit zwei Fingern zoomen", "Drag the page, pinch to zoom"),
            style = MaterialTheme.typography.labelLarge,
            color = theme.text,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(top = Space.s4)
                .elevation(Shapes.pill, Elevation.Level2)
                .clip(Shapes.pill)
                .background(theme.bar)
                .padding(horizontal = Space.s4, vertical = Space.s2),
        )
    }
}

// =============================================================================================
// Vollbild ohne Statusleiste, Bildschirm bleibt an
// =============================================================================================

private fun Context.findActivity(): Activity? {
    var c: Context = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

@Composable
private fun ImmersiveMode(dark: Boolean) {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        view.keepScreenOn = true
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
            view.keepScreenOn = false
        }
    }
    DisposableEffect(view, dark) {
        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val before = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = !dark
        onDispose { if (before != null) controller?.isAppearanceLightStatusBars = before }
    }
}

// =============================================================================================
// PDF
// =============================================================================================

/** Ein offenes PDF. PdfRenderer kann immer nur eine Seite gleichzeitig öffnen, deshalb synchronisiert. */
private class PdfDoc(file: File) {
    private val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    private val renderer = try { PdfRenderer(fd) } catch (e: Throwable) { fd.close(); throw e }
    val count: Int = renderer.pageCount
    /** Höchstens ~40 MB gerenderte Seiten im Speicher (Größe in KB), damit auch große PDFs nicht den Speicher sprengen. */
    private val cache = object : LruCache<String, ImageBitmap>(40 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = (value.width.toLong() * value.height * 4 / 1024).toInt().coerceAtLeast(1)
    }
    @Volatile private var closed = false
    /** Seitenformat (Höhe durch Breite) der schon gezeichneten Seiten, auch vom Hauptthread aus lesbar. */
    private val aspects = java.util.concurrent.ConcurrentHashMap<Int, Float>()

    fun cached(page: Int, width: Int): ImageBitmap? = cache.get("$page@$width")

    fun aspect(page: Int): Float? = aspects[page]

    fun render(page: Int, width: Int): ImageBitmap? = synchronized(this) { renderLocked(page, width) }

    private fun renderLocked(page: Int, width: Int): ImageBitmap? {
        if (closed || page !in 0 until count || width <= 0) return null
        val hit = cache.get("$page@$width")
        if (hit != null) return hit
        return runCatching {
            renderer.openPage(page).use { p ->
                aspects[page] = p.height.toFloat() / p.width.coerceAtLeast(1)
                val w = width.coerceAtMost(2400)
                val h = (w * p.height.toFloat() / p.width.coerceAtLeast(1)).toInt().coerceIn(1, 3600)
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                bmp.eraseColor(android.graphics.Color.WHITE)
                p.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                // Einmal auf die Grafikkarte, dann zeichnet jede Seite ohne Kopie (spürbar flüssiger beim Wischen)
                bmp.prepareToDraw()
                bmp.asImageBitmap()
            }
        }.getOrNull()?.also { cache.put("$page@$width", it) }
    }

    fun close() = synchronized(this) {
        closed = true
        cache.evictAll()
        runCatching { renderer.close() }
        runCatching { fd.close() }
    }
}

/** Farbfilter für PDF-Seiten: Nacht kehrt die Seite um (weiße Schrift auf dunklem Grund), Sepia tönt warm. */
private fun pdfFilter(theme: ReaderTheme): ColorFilter? = when (theme) {
    ReaderTheme.Paper -> null
    ReaderTheme.Sepia -> ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                0.95f, 0f, 0f, 0f, 0f,
                0f, 0.90f, 0f, 0f, 0f,
                0f, 0f, 0.78f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
    )
    ReaderTheme.Night -> ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                -0.80f, 0f, 0f, 0f, 225f,
                0f, -0.80f, 0f, 0f, 222f,
                0f, 0f, -0.80f, 0f, 215f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
    )
}

@Composable
private fun PdfPages(
    book: Book,
    theme: ReaderTheme,
    viewZoom: () -> Float,
    startPage: Int,
    onNav: (ReaderNav) -> Unit,
    onPage: (page: Int, count: Int) -> Unit,
    onTap: (ReaderTap) -> Unit,
) {
    val doc by produceState<Result<PdfDoc>?>(null, book.id) {
        value = withContext(Dispatchers.IO) { runCatching { PdfDoc(Library.file(book)) } }
    }
    DisposableEffect(doc) {
        val d = doc?.getOrNull()
        onDispose { d?.close() }
    }
    val r = doc
    when {
        r == null -> ReaderLoading(theme)
        r.isFailure || r.getOrNull()?.count == 0 -> ReaderError(theme)
        else -> BoxWithConstraints(Modifier.fillMaxSize()) {
            val d = r.getOrThrow()
            val pager = rememberPagerState(initialPage = startPage.coerceIn(0, d.count - 1)) { d.count }
            val still = reducedMotion
            val filter = remember(theme) { pdfFilter(theme) }
            var zoomed by remember { mutableStateOf(false) }
            val pageChanged by rememberUpdatedState(onPage)
            val density = LocalDensity.current
            val boxW = constraints.maxWidth.toFloat()
            val boxH = constraints.maxHeight.toFloat()
            LaunchedEffect(pager, boxW, boxH) {
                val clamp: (Int) -> Int = { it.coerceIn(0, d.count - 1) }
                // Die Seite steht eingepasst und mittig (Ränder wie in PdfPage); der Inhalt ist das Seitenbild
                val padH = with(density) { Space.s2.toPx() }
                val padV = with(density) { Space.s6.toPx() }
                val air = with(density) { Space.s2.toPx() }
                val span: (Int) -> ClosedFloatingPointRange<Float> = { page ->
                    val aspect = d.aspect(clamp(page)) ?: 1.414f
                    val areaW = boxW - 2 * padH
                    val areaH = boxH - 2 * padV
                    val imgH = minOf(areaH, areaW * aspect)
                    val top = padV + (areaH - imgH) / 2f
                    (top - air).coerceAtLeast(0f)..(top + imgH + air).coerceAtMost(boxH)
                }
                onNav(ReaderNav(pager, d.count, pageOfPos = { s, _ -> clamp(s) }, pageOfChapter = { clamp(it.section) }, contentSpan = span, still = still))
                // Erst wenn die Seite liegt (nicht schon mitten im Wischen): sonst ruckelt es
                snapshotFlow { pager.settledPage }.distinctUntilChanged().collect {
                    zoomed = false
                    pageChanged(it, d.count)
                }
            }
            HorizontalPager(
                state = pager,
                beyondViewportPageCount = 1,
                userScrollEnabled = !zoomed,
                key = { it },
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                PdfPage(d, page, filter, isCurrent = { pager.settledPage == page }, viewZoom = viewZoom, onZoom = { zoomed = it }, onTap = onTap)
            }
        }
    }
}

@Composable
private fun PdfPage(
    doc: PdfDoc,
    page: Int,
    filter: ColorFilter?,
    isCurrent: () -> Boolean,
    viewZoom: () -> Float,
    onZoom: (Boolean) -> Unit,
    onTap: (ReaderTap) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.roundToPx() }
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        // Weggeblättert: Zoom zurücksetzen
        LaunchedEffect(Unit) {
            snapshotFlow { isCurrent() }.collect { current -> if (!current) { scale = 1f; offset = Offset.Zero } }
        }
        // Gezoomt (hier oder im Ausschnitt beim Rechner): die Seite in höherer Auflösung nachladen, damit kleine
        // Schrift scharf bleibt
        val outerZoom by remember { derivedStateOf { viewZoom() > 1.4f } }
        val sharp = scale > 1.4f || outerZoom
        val target = if (sharp) (widthPx * 1.8f).toInt() else widthPx
        val bitmap by produceState(doc.cached(page, widthPx), page, target) {
            val hi = withContext(Dispatchers.IO) { doc.render(page, target) }
            if (hi != null) value = hi
        }
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        Box(
            Modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(page) {
                    // Zwei Finger zoomen. Gezoomt verschiebt ein Finger die Seite, sonst blättert der Pager.
                    // Seit 15.1 gegen versehentliches Zoomen: Erst ab einer deutlichen Spreizbewegung (8 %) wird gezoomt,
                    // und wer weniger als 15 % vergrößert, landet beim Loslassen wieder bei der ganzen Seite.
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        var pinch = 1f
                        var started = scale > 1.01f
                        do {
                            val event = awaitPointerEvent()
                            val fingers = event.changes.count { it.pressed }
                            if (!started && fingers >= 2) {
                                pinch *= event.calculateZoom()
                                if (kotlin.math.abs(pinch - 1f) > 0.08f) started = true
                            }
                            if (started && (fingers >= 2 || scale > 1.01f)) {
                                val z = event.calculateZoom()
                                val pan = event.calculatePan()
                                val ns = (scale * z).coerceIn(1f, 4f)
                                val maxX = (w * ns - w) / 2f
                                val maxY = (h * ns - h) / 2f
                                offset = Offset((offset.x + pan.x).coerceIn(-maxX, maxX), (offset.y + pan.y).coerceIn(-maxY, maxY))
                                scale = ns
                                onZoom(ns > 1.01f)
                                event.changes.forEach { if (it.positionChanged()) it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                        if (scale < 1.15f) {
                            scale = 1f
                            offset = Offset.Zero
                            onZoom(false)
                        }
                    }
                }
                .pointerInput(page) { detectReaderTaps(enabled = { scale <= 1.01f }, onTap = onTap) },
            contentAlignment = Alignment.Center,
        ) {
            val img = bitmap
            if (img != null) {
                Image(
                    img,
                    contentDescription = tr("Seite ${page + 1}", "Page ${page + 1}"),
                    contentScale = ContentScale.Fit,
                    colorFilter = filter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Space.s2, vertical = Space.s6)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        },
                )
            }
        }
    }
}

// =============================================================================================
// EPUB: Text in Seiten umbrechen
// =============================================================================================

/**
 * Ein Buch in Seiten umbrochen. Jeder Abschnitt wird einmal gesetzt; eine Seite ist ein Ausschnitt davon
 * (von einer Zeile bis vor die erste Zeile, die nicht mehr passt). So sieht jede Seite genau so aus, wie sie
 * gemessen wurde, und keine Zeile wird zerschnitten.
 */
private class EpubPagination(
    val texts: List<AnnotatedString>,
    /** je Abschnitt: Zeichenposition, an der jeder Block beginnt */
    val blockStarts: List<IntArray>,
    /** je Abschnitt: Zeichenposition, Oberkante und sichtbare Höhe jeder Seite */
    val pageOffsets: List<IntArray>,
    val pageTops: List<FloatArray>,
    val pageHeights: List<FloatArray>,
    /** erste globale Seite je Abschnitt */
    val firstPage: IntArray,
    val count: Int,
    private val measurer: TextMeasurer,
    private val style: TextStyle,
    private val width: Int,
) {
    private val layouts = LruCache<Int, TextLayoutResult>(6)

    fun put(section: Int, layout: TextLayoutResult) { layouts.put(section, layout) }

    @Synchronized
    fun layout(section: Int): TextLayoutResult =
        layouts.get(section) ?: measurer.measure(texts[section], style, constraints = Constraints(maxWidth = width)).also { layouts.put(section, it) }

    /** Schon gesetzt? Dann muss die Seite nicht erst im Hintergrund warten. */
    fun cachedLayout(section: Int): TextLayoutResult? = layouts.get(section)

    fun sectionOf(page: Int): Int {
        var lo = 0
        var hi = firstPage.size - 1
        while (lo < hi) {
            val mid = (lo + hi + 1) / 2
            if (firstPage[mid] <= page) lo = mid else hi = mid - 1
        }
        return lo
    }

    fun pageOf(section: Int, offset: Int): Int {
        val s = section.coerceIn(0, texts.size - 1)
        val offs = pageOffsets[s]
        var i = 0
        while (i + 1 < offs.size && offs[i + 1] <= offset) i++
        return (firstPage[s] + i).coerceIn(0, count - 1)
    }

    fun offsetOfBlock(section: Int, block: Int): Int {
        val s = section.coerceIn(0, texts.size - 1)
        val starts = blockStarts[s]
        return if (starts.isEmpty()) 0 else starts[block.coerceIn(0, starts.size - 1)]
    }

    fun position(page: Int): Pair<Int, Int> {
        val s = sectionOf(page)
        val local = (page - firstPage[s]).coerceIn(0, pageOffsets[s].size - 1)
        return s to pageOffsets[s][local]
    }
}

private fun buildSection(blocks: List<de.funke.app.books.Block>, size: Float): Pair<AnnotatedString, IntArray> {
    val starts = IntArray(blocks.size)
    val text = buildAnnotatedString {
        var prev: BlockKind? = null
        blocks.forEachIndexed { i, b ->
            val heading = b.kind == BlockKind.H1 || b.kind == BlockKind.H2 || b.kind == BlockKind.H3
            if (heading && i > 0) append("\n")
            starts[i] = length
            when (b.kind) {
                BlockKind.H1, BlockKind.H2, BlockKind.H3 -> {
                    val f = when (b.kind) { BlockKind.H1 -> 1.45f; BlockKind.H2 -> 1.25f; else -> 1.1f }
                    withStyle(ParagraphStyle(lineHeight = (size * f * 1.3f).sp, textAlign = TextAlign.Start, hyphens = Hyphens.None)) {
                        withStyle(SpanStyle(fontSize = (size * f).sp, fontWeight = FontWeight.SemiBold)) { append(b.text) }
                    }
                    append("\n")
                }
                BlockKind.Quote -> withStyle(ParagraphStyle(textIndent = TextIndent((size * 1.2f).sp, (size * 1.2f).sp))) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(b.text) }
                }
                BlockKind.Item -> withStyle(ParagraphStyle(textIndent = TextIndent(0.sp, (size * 1.1f).sp), textAlign = TextAlign.Start)) {
                    append("•  ")
                    append(b.text)
                }
                BlockKind.P -> {
                    val indent = if (prev == BlockKind.P) (size * 1.4f).sp else 0.sp
                    withStyle(ParagraphStyle(textIndent = TextIndent(firstLine = indent))) { append(b.text) }
                }
            }
            if (i < blocks.size - 1) append("\n")
            prev = b.kind
        }
    }
    return text to starts
}

private suspend fun paginate(
    epub: EpubBook,
    measurer: TextMeasurer,
    style: TextStyle,
    width: Int,
    height: Int,
): EpubPagination = withContext(Dispatchers.Default) {
    val size = style.fontSize.value
    val texts = ArrayList<AnnotatedString>()
    val blockStarts = ArrayList<IntArray>()
    val offsets = ArrayList<IntArray>()
    val tops = ArrayList<FloatArray>()
    val heights = ArrayList<FloatArray>()
    val first = IntArray(epub.sections.size)
    val firstLayouts = HashMap<Int, TextLayoutResult>()
    var total = 0
    epub.sections.forEachIndexed { s, sec ->
        val (text, starts) = buildSection(sec.blocks, size)
        texts += text
        blockStarts += starts
        val headingStarts = sec.blocks.indices.filter { sec.blocks[it].kind.let { k -> k == BlockKind.H1 || k == BlockKind.H2 || k == BlockKind.H3 } }.map { starts[it] }.toSet()
        val layout = measurer.measure(text, style, constraints = Constraints(maxWidth = width))
        if (firstLayouts.size < 3) firstLayouts[s] = layout
        val pOff = ArrayList<Int>()
        val pTop = ArrayList<Float>()
        val pH = ArrayList<Float>()
        var line = 0
        val lines = layout.lineCount
        while (line < lines) {
            val top = layout.getLineTop(line)
            var end = line
            while (end < lines && layout.getLineBottom(end) - top <= height) end++
            if (end == line) end = line + 1
            // Keine Überschrift allein unten auf der Seite
            if (end < lines && end - line > 3) {
                var e = end
                while (e - 1 > line + 2 && layout.getLineStart(e - 1).let { st -> headingStarts.any { h -> st >= h && st <= h + 1 } || text.getOrNull(st) == '\n' }) e--
                end = e
            }
            pOff += layout.getLineStart(line)
            pTop += top
            pH += (if (end < lines) layout.getLineTop(end) else layout.getLineBottom(lines - 1)) - top
            line = end
        }
        if (pOff.isEmpty()) { pOff += 0; pTop += 0f; pH += 0f }
        first[s] = total
        total += pOff.size
        offsets += pOff.toIntArray()
        tops += pTop.toFloatArray()
        heights += pH.toFloatArray()
    }
    EpubPagination(texts, blockStarts, offsets, tops, heights, first, total.coerceAtLeast(1), measurer, style, width).also { p ->
        firstLayouts.forEach { (s, l) -> p.put(s, l) }
    }
}

/** Fertig umbrochene Bücher im Speicher, damit das Wiederöffnen sofort geht. */
private object PaginationCache {
    val books = LruCache<String, EpubBook>(2)
    val pages = LruCache<String, EpubPagination>(3)
}

@Composable
private fun EpubPages(
    book: Book,
    settings: ReaderSettings,
    anchor: ReaderAnchor,
    onNav: (ReaderNav) -> Unit,
    onPage: (section: Int, offset: Int, page: Int, count: Int) -> Unit,
    onTap: (ReaderTap) -> Unit,
    onEpub: (EpubBook) -> Unit,
) {
    val theme = settings.theme
    val epub by produceState<Result<EpubBook>?>(PaginationCache.books.get(book.id)?.let { Result.success(it) }, book.id) {
        if (value == null) value = withContext(Dispatchers.IO) { runCatching { Epub.read(Library.file(book), withCover = false) } }
            .also { r -> r.getOrNull()?.let { PaginationCache.books.put(book.id, it) } }
    }
    LaunchedEffect(epub) { epub?.getOrNull()?.let(onEpub) }
    val e = epub
    if (e == null) { ReaderLoading(theme); return }
    val loaded = e.getOrNull()
    if (loaded == null) { ReaderError(theme); return }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val padH = 28.dp
        val padTop = 44.dp
        val padBottom = 52.dp
        val widthPx = with(density) { (maxWidth - padH * 2).roundToPx() }.coerceAtLeast(100)
        val heightPx = with(density) { (maxHeight - padTop - padBottom).roundToPx() }.coerceAtLeast(100)
        val resolver = LocalFontFamilyResolver.current
        val direction = LocalLayoutDirection.current
        val style = remember(settings.fontSize, settings.serif) {
            TextStyle(
                fontSize = settings.fontSize.sp,
                lineHeight = (settings.fontSize * 1.55f).sp,
                fontFamily = if (settings.serif) FontFamily.Serif else null,
                textAlign = TextAlign.Justify,
                hyphens = Hyphens.Auto,
                lineBreak = LineBreak.Paragraph,
            )
        }
        val cacheKey = "${book.id}:${settings.fontSize}:${settings.serif}:$widthPx:$heightPx"
        val pagination = remember(cacheKey) { mutableStateOf(PaginationCache.pages.get(cacheKey)) }
        LaunchedEffect(cacheKey) {
            if (pagination.value == null) {
                // Eigener Messer ohne Zwischenspeicher: wird im Hintergrund benutzt
                val measurer = TextMeasurer(resolver, density, direction, 0)
                pagination.value = paginate(loaded, measurer, style, widthPx, heightPx).also { PaginationCache.pages.put(cacheKey, it) }
            }
        }
        val pg = pagination.value
        if (pg == null) {
            ReaderLoading(theme)
            return@BoxWithConstraints
        }
        key(pg) {
            val start = remember { pg.pageOf(anchor.section, anchor.offset) }
            val pager = rememberPagerState(initialPage = start) { pg.count }
            val still = reducedMotion
            val textColor = theme.text
            val pageChanged by rememberUpdatedState(onPage)
            LaunchedEffect(pager) {
                // Text steht zwischen den Rändern oben und unten (etwas Luft bleibt)
                val air = with(density) { Space.s3.toPx() }
                val top = (with(density) { padTop.toPx() } - air).coerceAtLeast(0f)
                val bottom = with(density) { (maxHeight - padBottom).toPx() } + air
                onNav(
                    ReaderNav(
                        pager,
                        pg.count,
                        pageOfPos = { s, o -> pg.pageOf(s, o) },
                        pageOfChapter = { pg.pageOf(it.section, pg.offsetOfBlock(it.section, it.block)) },
                        contentSpan = { top..bottom },
                        still = still,
                    )
                )
                snapshotFlow { pager.settledPage }.distinctUntilChanged().collect { page ->
                    val (s, o) = pg.position(page)
                    pageChanged(s, o, page, pg.count)
                }
            }
            HorizontalPager(state = pager, beyondViewportPageCount = 1, key = { it }, modifier = Modifier.fillMaxSize()) { page ->
                val s = pg.sectionOf(page)
                val local = (page - pg.firstPage[s]).coerceIn(0, pg.pageTops[s].size - 1)
                val layout by produceState(pg.cachedLayout(s), pg, s) {
                    if (value == null) value = withContext(Dispatchers.Default) { pg.layout(s) }
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { detectReaderTaps(enabled = { true }, onTap = onTap) }
                        .padding(start = padH, end = padH, top = padTop, bottom = padBottom),
                ) {
                    val l = layout
                    if (l != null) {
                        val top = pg.pageTops[s][local]
                        val visible = pg.pageHeights[s][local]
                        Canvas(
                            Modifier
                                .fillMaxSize()
                                // Eigene Ebene: beim Wischen wird die Seite nur verschoben, nicht neu gesetzt
                                .graphicsLayer()
                                .semantics { contentDescription = pageText(pg, s, local) },
                        ) {
                            clipRect(0f, 0f, size.width, visible.coerceAtMost(size.height + 1f)) {
                                translate(top = -top) { drawText(l, color = textColor) }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Text einer Seite für TalkBack. */
private fun pageText(pg: EpubPagination, s: Int, local: Int): String {
    val offs = pg.pageOffsets[s]
    val from = offs[local]
    val to = if (local + 1 < offs.size) offs[local + 1] else pg.texts[s].length
    return pg.texts[s].text.substring(from.coerceIn(0, pg.texts[s].length), to.coerceIn(from, pg.texts[s].length))
}

// =============================================================================================
// Laden, Fehler, Hinweis
// =============================================================================================

@Composable
private fun ReaderLoading(theme: ReaderTheme) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Lumi2D(LumiPose.Reading, Modifier.size(96.dp), tappable = false)
        Spacer(Modifier.height(Space.s4))
        Text(tr("Seiten werden vorbereitet …", "Getting the pages ready …"), style = MaterialTheme.typography.bodyMedium, color = theme.soft)
    }
}

@Composable
private fun ReaderError(theme: ReaderTheme) {
    Column(Modifier.fillMaxSize().padding(Space.s8), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(tr("Das Buch lässt sich nicht öffnen", "This book won't open"), style = MaterialTheme.typography.titleMedium, color = theme.text, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Space.s2))
        Text(
            tr("Die Datei ist vielleicht beschädigt. Entferne das Buch und füge es neu hinzu.", "The file may be damaged. Remove the book and add it again."),
            style = MaterialTheme.typography.bodyMedium,
            color = theme.soft,
            textAlign = TextAlign.Center,
        )
    }
}

/** Beim ersten Öffnen: zwei kurze Zeilen, wie man blättert und wo das Menü ist. */
@Composable
private fun ReaderHint(theme: ReaderTheme) {
    Column(
        Modifier
            .padding(horizontal = Space.s8)
            .elevation(Shapes.large, Elevation.Level2)
            .clip(Shapes.large)
            .background(theme.bar)
            .padding(horizontal = Space.s6, vertical = Space.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(tr("Rand antippen oder wischen: blättern", "Tap the edge or swipe: turn the page"), style = MaterialTheme.typography.bodyMedium, color = theme.text, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Space.s1))
        Text(tr("Doppelt in die Mitte tippen: Menü", "Double-tap the middle: menu"), style = MaterialTheme.typography.bodyMedium, color = theme.soft, textAlign = TextAlign.Center)
    }
}

// =============================================================================================
// Leise Anzeigen auf der Seite: Lesezeichen-Band, Seitenzahl, Notiz-Marke
// =============================================================================================

private val RibbonShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(size.width / 2f, size.height * 0.78f)
    lineTo(0f, size.height)
    close()
}

/** Ein schmales Band oben rechts, wenn die Seite gemerkt ist (blendet in 200 ms). */
@Composable
private fun BookmarkRibbon(nav: ReaderNav, marks: List<Mark>, theme: ReaderTheme, visible: Boolean, modifier: Modifier) {
    val page = nav.page
    val marked = marks.any { nav.pageOfPos(it.section, it.offset) == page }
    val fade by animateFloatAsState(if (marked && visible) 1f else 0f, Motion.fade(Motion.Base), label = "ribbon")
    if (fade > 0f) {
        Box(
            modifier
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(end = Space.s6)
                .size(width = 14.dp, height = 26.dp)
                .graphicsLayer { alpha = fade; translationY = (1f - fade) * -8.dp.toPx() }
                .clip(RibbonShape)
                .background(theme.accent)
        )
    }
}

/** Unten in der Mitte: nur die Seitenzahl, ganz leise. Hat die Seite eine Notiz, steht ein kleiner Stift daneben (antippbar). */
@Composable
private fun PageFooter(nav: ReaderNav, theme: ReaderTheme, notes: List<Note>, visible: Boolean, onNote: (Int) -> Unit, modifier: Modifier) {
    val page = nav.page
    val hasNote = notes.any { nav.pageOfPos(it.section, it.offset) == page }
    val fade by animateFloatAsState(if (visible) 1f else 0f, Motion.fade(Motion.Base), label = "footer")
    Row(
        modifier
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(bottom = Space.s1)
            .graphicsLayer { alpha = fade }
            .heightIn(min = Sizes.touch),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${page + 1}",
            style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"),
            color = theme.soft.copy(alpha = 0.75f),
        )
        if (hasNote) {
            Box(
                Modifier
                    .size(Sizes.touch)
                    .clip(CircleShape)
                    .clickable(enabled = visible) { onNote(page) }
                    .semantics { contentDescription = tr("Notiz zu dieser Seite", "Note on this page") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(R.drawable.ic_pen), contentDescription = null, tint = theme.accent.copy(alpha = 0.85f), modifier = Modifier.size(14.dp))
            }
        }
    }
}

// =============================================================================================
// Menü über der Seite
// =============================================================================================

@Composable
private fun ReaderChrome(
    book: Book,
    theme: ReaderTheme,
    nav: ReaderNav,
    onContents: () -> Unit,
    onClose: () -> Unit,
    onTool: (ReaderTool) -> Unit,
    onMark: () -> Unit,
    onDismiss: () -> Unit,
) {
    val page = nav.page
    val chapter = nav.chapterAt(page, book.chapters)
    val marked = book.marks.any { nav.pageOfPos(it.section, it.offset) == page }
    val panel = theme.bg
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onDismiss() }) },
    ) {
        // oben: Inhalt links, wo man ist in der Mitte, Schließen rechts
        Row(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(0f to panel, 0.65f to panel.copy(alpha = 0.94f), 1f to panel.copy(alpha = 0f)))
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(start = Space.s2, end = Space.s2, top = Space.s3, bottom = Space.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChromeButton(tr("Inhalt", "Contents"), onContents) { ChromeIcon(R.drawable.ic_list, theme.text) }
            Column(Modifier.weight(1f).padding(horizontal = Space.s2), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    chapter?.title ?: book.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = theme.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
                if (chapter != null) {
                    Text(book.title, style = MaterialTheme.typography.bodySmall, color = theme.soft, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                }
            }
            ChromeButton(tr("Schließen", "Close"), onClose) { CloseIcon(theme.text, Modifier.size(12.dp)) }
        }

        // unten: schwebende Leiste mit den Werkzeugen – eine Pille ohne Rand, getragen von Fläche und weichem Schatten
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(bottom = Space.s6)
                .elevation(Shapes.pill, Elevation.Level2)
                .clip(Shapes.pill)
                .background(theme.bar)
                .pointerInput(Unit) { detectTapGestures { } }
                .padding(horizontal = Space.s2, vertical = Space.s1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolButton(R.drawable.ic_pen, tr("Notiz", "Note"), theme) { onTool(ReaderTool.Note) }
            ToolButton(R.drawable.ic_calculator, tr("Rechner", "Calculator"), theme) { onTool(ReaderTool.Calc) }
            ToolButton(
                if (marked) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark,
                if (marked) tr("Gemerkt", "Saved") else tr("Merken", "Bookmark"),
                theme,
                active = marked,
                onClick = onMark,
            )
            ToolButton(R.drawable.ic_text, tr("Darstellung", "Display"), theme) { onTool(ReaderTool.Look) }
        }
    }
}

@Composable
private fun ChromeIcon(icon: Int, tint: Color) {
    Icon(painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(Sizes.icon))
}

@Composable
private fun ChromeButton(description: String, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .size(Sizes.touch)
            .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) { content() }
}

/** Werkzeug in der Leiste: Symbol und kurzes Wort darunter. [active] (Lesezeichen gesetzt) trägt den Akzent. */
@Composable
private fun ToolButton(icon: Int, label: String, theme: ReaderTheme, active: Boolean = false, onClick: () -> Unit) {
    val tint by animateColorAsState(if (active) theme.accent else theme.text, Motion.fade(Motion.Fast), label = "tool")
    Column(
        Modifier
            .widthIn(min = 68.dp)
            .heightIn(min = Sizes.button)
            .bouncyClickable(shape = Shapes.pill, pressedScale = 0.94f, onClick = onClick)
            .padding(horizontal = Space.s2, vertical = Space.s2)
            .semantics { if (active) stateDescription = tr("An", "On") },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(Sizes.icon))
        Spacer(Modifier.height(Space.s1))
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint, maxLines = 1)
    }
}

// =============================================================================================
// Werkzeuge unten: Notiz, Darstellung (Rechner: ui/calc)
// =============================================================================================

/** Fläche unten für ein Werkzeug: runde Ecken oben, leichter Schatten, Tippen geht nicht an die Seite durch. */
@Composable
private fun ToolSurface(content: @Composable ColumnScope.() -> Unit) {
    val p = Funke.colors
    val shape = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet)
    Column(
        Modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth()
            .elevation(shape, Elevation.Level3)
            .clip(shape)
            .background(p.raised(Elevation.Level2))
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(start = Space.s4, end = Space.s4, top = Space.s3, bottom = Space.s4),
        content = content,
    )
}

/**
 * Notiz zur Seite: oben "Seite 12", rechts groß öffnen und Fertig; darunter das Feld und dieselbe Leiste wie im
 * Notiz-Editor (Sprache, Mikrofon, Aufräumen). Die Tastatur kommt erst, wenn man ins Feld tippt – so ist sofort
 * klar, dass man auch sprechen kann. Gespeichert wird beim Tippen (leicht verzögert) und beim Schließen; leer = gelöscht.
 */
@Composable
private fun NotePanel(state: AppState, note: ReaderNote, speech: NoteVoice, onExpand: () -> Unit, onDone: () -> Unit) {
    val p = Funke.colors
    val target = note.target
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            tr("Seite ${target.page}", "Page ${target.page}"),
            style = MaterialTheme.typography.titleSmall,
            color = p.text,
            modifier = Modifier.weight(1f).padding(start = Space.s1),
        )
        if (note.value.text.isNotBlank()) {
            Box(
                Modifier
                    .size(Sizes.touch)
                    .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, onClick = onExpand)
                    .semantics { contentDescription = tr("Groß öffnen", "Open full size") },
                contentAlignment = Alignment.Center,
            ) { Icon(painterResource(R.drawable.ic_expand), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.iconSm + 2.dp)) }
        }
        Box(
            Modifier
                .heightIn(min = Sizes.touch)
                .bouncyClickable(shape = Shapes.pill, pressedScale = 0.95f, onClick = onDone)
                .padding(horizontal = Space.s3),
            contentAlignment = Alignment.Center,
        ) { Text(tr("Fertig", "Done"), style = MaterialTheme.typography.labelLarge, color = p.accent) }
    }
    Spacer(Modifier.height(Space.s2))
    BasicTextField(
        value = note.value,
        onValueChange = { if (it.text.length <= 4000) note.value = it },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = p.text),
        cursorBrush = SolidColor(p.accent),
        minLines = 3,
        maxLines = 6,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.lg)
            .background(p.track)
            .semantics { contentDescription = tr("Notiz zu Seite ${target.page}", "Note on page ${target.page}") },
        decorationBox = { inner ->
            Box(Modifier.padding(horizontal = Space.s4, vertical = Space.s3)) {
                if (note.value.text.isEmpty()) {
                    Text(tr("Schreib oder sprich …", "Type or talk …"), style = MaterialTheme.typography.bodyLarge, color = p.textTertiary)
                }
                inner()
            }
        },
    )
    NoteVoiceDock(speech, state, hasText = note.value.text.isNotBlank(), modifier = Modifier.padding(top = Space.s1))
}

/**
 * Darstellung (seit 15.5 neu gestaltet): Papier wählen wie auf dem Tisch – drei kleine Seiten mit ein paar Zeilen
 * darauf, die gewählte liegt obenauf (angehoben, mit Schatten). Bei E-Books darunter die Schriftgröße in sieben
 * Stufen (kleines A, Stufen, großes A) und die Schrift. Die Seite dahinter ändert sich sofort mit.
 */
@Composable
private fun LookPanel(book: Book, settings: ReaderSettings, onDone: () -> Unit) {
    val p = Funke.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(tr("Darstellung", "Display"), style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f).padding(start = Space.s1))
        CloseButton(onClick = onDone)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = Space.s2),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ReaderTheme.entries.forEach { t ->
            PaperChoice(t, selected = t == settings.theme) { settings.updateTheme(t) }
        }
    }
    if (book.format == BookFormat.Epub) {
        Spacer(Modifier.height(Space.s4))
        FontSizeSteps(settings.fontSize, onChange = { settings.updateFont(it) })
        Spacer(Modifier.height(Space.s2))
        SegmentedToggle(
            options = listOf(tr("Buchschrift", "Book serif"), "Inter"),
            selected = if (settings.serif) 0 else 1,
            onSelect = { settings.updateSerif(it == 0) },
        )
    } else {
        Spacer(Modifier.height(Space.s3))
        Text(
            tr("Mit zwei Fingern zoomen.", "Pinch with two fingers to zoom."),
            style = MaterialTheme.typography.bodySmall,
            color = p.textTertiary,
            modifier = Modifier.padding(horizontal = Space.s1),
        )
    }
}

/** Eine Papierfarbe als kleine Seite mit Zeilen darauf. Gewählt: angehoben mit Schatten, Name in der Textfarbe. */
@Composable
private fun PaperChoice(theme: ReaderTheme, selected: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    val lift by animateFloatAsState(if (selected) 1f else 0f, Motion.spec(Motion.Medium), label = "paperLift")
    val shape = RoundedCornerShape(6.dp)
    val edge = if (p.isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.09f)
    val ink = theme.text
    Column(
        Modifier
            .widthIn(min = 84.dp)
            .bouncyClickable(shape = Shapes.lg, pressedScale = 0.96f, role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(top = Space.s3, bottom = Space.s2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(width = 54.dp, height = 72.dp)
                .graphicsLayer {
                    translationY = -6.dp.toPx() * lift
                    shadowElevation = 10.dp.toPx() * lift
                    this.shape = shape
                    clip = true
                }
                .background(theme.bg)
                .drawBehind {
                    // Rand nur, solange die Seite flach liegt (gewählt trägt sie ihr Schatten)
                    if (lift < 0.99f) {
                        drawRoundRect(edge.copy(alpha = edge.alpha * (1f - lift)), style = Stroke(1.dp.toPx()), cornerRadius = CornerRadius(6.dp.toPx()))
                    }
                    // Ein paar Zeilen: Überschrift kräftiger, darunter Text
                    val padX = 9.dp.toPx()
                    val w = size.width - padX * 2
                    val h = 3.dp.toPx()
                    var y = 12.dp.toPx()
                    listOf(0.62f to 0.75f, 1f to 0.32f, 1f to 0.32f, 1f to 0.32f, 0.55f to 0.32f).forEachIndexed { i, (len, a) ->
                        drawRoundRect(ink.copy(alpha = a), Offset(padX, y), Size(w * len, h), CornerRadius(h / 2f))
                        y += if (i == 0) 9.dp.toPx() else 7.dp.toPx()
                    }
                },
        )
        Spacer(Modifier.height(Space.s3))
        Text(
            theme.label,
            style = MaterialTheme.typography.labelMedium,
            color = androidx.compose.ui.graphics.lerp(p.textTertiary, p.text, lift),
        )
    }
}

/**
 * Schriftgröße in sieben Stufen (14 bis 26): links ein kleines A (eine Stufe kleiner), rechts ein großes A (eine
 * Stufe größer), dazwischen die Stufen auf einer feinen Linie. Antippen oder Ziehen wählt eine Stufe; übernommen
 * wird beim Loslassen, damit das Buch nicht bei jeder Zwischenstufe neu gesetzt wird.
 */
@Composable
private fun FontSizeSteps(fontSize: Int, onChange: (Int) -> Unit) {
    val p = Funke.colors
    val steps = remember { (ReaderSettings.MIN_FONT..ReaderSettings.MAX_FONT step 2).toList() }
    val index = steps.indexOfFirst { it >= fontSize }.let { if (it < 0) steps.lastIndex else it }
    var dragging by remember { mutableStateOf<Int?>(null) }
    // Die Geste läuft länger als eine Zusammensetzung: immer mit der aktuellen Größe vergleichen
    val currentSize by rememberUpdatedState(fontSize)
    val change by rememberUpdatedState(onChange)
    val shown = dragging ?: index
    val pos by animateFloatAsState(shown / steps.lastIndex.toFloat(), Motion.spec(Motion.Fast), label = "fontStep")
    val track = p.track
    val tick = p.textTertiary.copy(alpha = 0.45f)
    val knob = p.accent
    val ring = p.raised(Elevation.Level2)
    val haptics = LocalHaptics.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepLetter(14.sp, tr("Schrift kleiner", "Smaller text"), enabled = index > 0) { onChange(steps[index - 1]) }
        Box(
            Modifier
                .weight(1f)
                .height(Sizes.touch)
                .pointerInput(steps) {
                    val pad = 8.dp.toPx()
                    fun stepAt(x: Float): Int = (((x - pad) / (size.width - pad * 2)).coerceIn(0f, 1f) * steps.lastIndex).roundToInt()
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        var at = stepAt(down.position.x)
                        dragging = at
                        while (true) {
                            val event = awaitPointerEvent()
                            val ch = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!ch.pressed) break
                            val next = stepAt(ch.position.x)
                            if (next != at) {
                                at = next
                                dragging = at
                                haptics?.tick()
                            }
                            ch.consume()
                        }
                        dragging = null
                        if (steps[at] != currentSize) change(steps[at])
                    }
                }
                .semantics { contentDescription = tr("Schriftgröße ${steps[index]}", "Text size ${steps[index]}") }
                .drawBehind {
                    val pad = 8.dp.toPx()
                    val y = size.height / 2f
                    val w = size.width - pad * 2
                    drawLine(track, Offset(pad, y), Offset(size.width - pad, y), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    for (i in steps.indices) {
                        val x = pad + w * i / steps.lastIndex
                        drawLine(tick, Offset(x, y - 4.dp.toPx()), Offset(x, y + 4.dp.toPx()), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    }
                    val kx = pad + w * pos
                    drawCircle(ring, radius = 13.dp.toPx(), center = Offset(kx, y))
                    drawCircle(knob, radius = 9.dp.toPx(), center = Offset(kx, y))
                },
        )
        StepLetter(22.sp, tr("Schrift größer", "Larger text"), enabled = index < steps.lastIndex) { onChange(steps[index + 1]) }
    }
}

/** "A" in der Buchschrift als Knopf für eine Stufe kleiner oder größer. */
@Composable
private fun StepLetter(size: androidx.compose.ui.unit.TextUnit, description: String, enabled: Boolean, onClick: () -> Unit) {
    val p = Funke.colors
    Box(
        Modifier
            .size(Sizes.touch)
            .bouncyClickable(shape = CircleShape, pressedScale = 0.9f, enabled = enabled, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text("A", fontSize = size, fontFamily = FontFamily.Serif, color = p.text)
    }
}

// =============================================================================================
// Inhalt: Kapitel (aufklappbar), Lesezeichen, Notizen – von links
// =============================================================================================

/** Kapitelsuche mit der Lumina-KI und Unterkapitel aus dem Text (laufen weiter, auch wenn der Inhalt zu ist). */
private class ChapterSearch {
    var running by mutableStateOf(false)
        private set
    var subsRunning by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var epub: EpubBook? = null

    /**
     * Kapitel suchen. Mit der Lumina-KI, wenn sie an und erlaubt ist und das Buch in die drei Bücher dieser Woche
     * passt ([ChapterQuota]); sonst – oder wenn die KI gerade nicht klappt – direkt auf dem Handy aus dem Text
     * ([LocalChapters]). Danach die nummerierten Unterkapitel.
     * [preferAi] = false: gleich ohne KI (z. B. wenn man nur neu ordnen will).
     */
    suspend fun run(book: Book, state: AppState, epub: EpubBook?, context: android.content.Context, preferAi: Boolean = true) {
        if (running) return
        running = true
        error = null
        try {
            val eb = if (book.format == BookFormat.Epub) {
                epub ?: this.epub ?: withContext(Dispatchers.IO) {
                    runCatching { de.funke.app.books.Epub.read(Library.file(book), withCover = false) }.getOrNull()
                }?.also { this.epub = it }
            } else null
            var done = false
            var aiFail: String? = null
            val useAi = preferAi && state.aiReady && state.aiConsent && ChapterQuota.allows(context, book.id)
            if (useAi) {
                val candidates = when (book.format) {
                    BookFormat.Pdf -> withContext(Dispatchers.IO) { Library.pdfPageStarts(book, 140) }?.let { ChapterAi.pdfCandidates(it) }
                    BookFormat.Epub -> eb?.let { ChapterAi.epubCandidates(it) }.orEmpty()
                }
                if (candidates != null) {
                    when (val outcome = ChapterAi.detect(ChapterAi.buildInput(candidates), candidates, book.title, Lang.german, state.aiServer, state.deviceId)) {
                        is ChapterAi.Outcome.Ok -> {
                            Library.setChapters(book.id, outcome.chapters, ChapterSource.Ai)
                            ChapterQuota.note(context, book.id)
                            done = true
                        }
                        is ChapterAi.Outcome.Failed -> aiFail = outcome.reason
                    }
                } else {
                    aiFail = "old_android"
                }
            }
            // Hatte das Buch schon Kapitel von der KI und das neue Suchen klappt nicht: die alten behalten
            if (!done && aiFail != null && book.chaptersBy == ChapterSource.Ai && book.chapters.isNotEmpty()) {
                error = aiFail
                done = true
            }
            if (!done) {
                val local: List<Chapter>? = withContext(Dispatchers.IO) {
                    when (book.format) {
                        BookFormat.Pdf -> Library.pdfPageHeads(book, 12)?.let { LocalChapters.fromPdf(it) }
                        BookFormat.Epub -> eb?.let { LocalChapters.fromEpub(it) }.orEmpty()
                    }
                }
                when {
                    local == null -> {
                        Library.markTextTried(book.id)
                        error = "old_android"
                    }
                    local.isNotEmpty() -> {
                        Library.setChapters(book.id, local, ChapterSource.Text)
                        // Hatte man ausdrücklich die KI gewählt, sagen, warum es doch die Kapitel aus dem Text sind
                        if (aiFail != null && book.chapters.isNotEmpty()) error = aiFail
                    }
                    else -> {
                        Library.markTextTried(book.id)
                        error = "empty"
                    }
                }
            }
        } finally {
            running = false
        }
        // Danach gleich die Unterkapitel ("3.2 …") aus dem Text holen, falls nur Kapitel gefunden wurden
        Library.get(book.id)?.let { findSubs(it) }
    }

    /** Einmal je PDF: nummerierte Unterkapitel im Text suchen (ohne KI, ohne Internet, ab Android 15). */
    suspend fun findSubs(book: Book) {
        if (subsRunning || book.subTried || book.format != BookFormat.Pdf || Build.VERSION.SDK_INT < 35) return
        if (book.chapters.count { it.level == 1 } < 2 || book.chapters.any { it.level > 1 }) return
        subsRunning = true
        try {
            val subs = withContext(Dispatchers.IO) { Library.pdfSubchapters(book) }
            if (!subs.isNullOrEmpty()) Library.mergeSubchapters(book.id, subs) else Library.markSubTried(book.id)
        } finally {
            subsRunning = false
        }
    }
}

private fun aiError(reason: String): String = when (reason) {
    "old_android" -> tr("Text aus PDFs lesen kann Android erst ab Version 15. Auf diesem Handy geht die Kapitelsuche für PDFs deshalb nicht.", "Android can only read text from PDFs from version 15 on, so chapter search for PDFs doesn't work on this phone.")
    "no_text" -> tr("In diesem Buch steckt kein lesbarer Text (zum Beispiel eingescannte Seiten).", "There's no readable text in this book (scanned pages, for example).")
    "device_limit" -> tr("Heute hat Lumi schon 3 Bücher gegliedert. Morgen geht es weiter.", "Lumi has already sorted 3 books today. More tomorrow.")
    "budget" -> tr("Die Lumina-KI hat für heute ihr Kontingent aufgebraucht. Morgen geht es wieder.", "Lumina AI has used up today's quota. Try again tomorrow.")
    "old_server" -> tr("Der Lumina-KI-Server kennt die Kapitelsuche noch nicht. Er muss auf Version 4 aktualisiert werden (siehe ANLEITUNG).", "The Lumina AI server doesn't know chapter search yet. It needs updating to version 4 (see ANLEITUNG).")
    "offline" -> tr("Keine Verbindung zum Internet.", "No internet connection.")
    "empty" -> tr("Lumi hat keine Kapitel gefunden.", "Lumi didn't find any chapters.")
    else -> tr("Hat gerade nicht geklappt. Versuch es später noch einmal.", "That didn't work just now. Try again later.")
}

// ----- Inhalt als Baum -----

/** Ein Eintrag im Inhalt. [index] = Stelle in book.chapters (stabiler Schlüssel), [number] = abgetrennte Nummer ("3.2"). */
private class TocNode(val chapter: Chapter, val index: Int, val number: String?, val title: String, val children: List<TocNode>)

/** Eine sichtbare Zeile der Liste: der Baum, soweit er aufgeklappt ist. */
private class TocLine(val node: TocNode, val depth: Int)

private val NumArabic = Regex(
    "^(?:(?:kapitel|chapter|teil|part|abschnitt|section|lektion|lesson|einheit|unit)\\s+)?(\\d{1,3}(?:\\.\\d{1,3}){0,3})(?:[.:)]|\\s*[-–—])?\\s+(\\S.*)$",
    RegexOption.IGNORE_CASE,
)
private val NumRomanWord = Regex("^(?:Teil|Part|Kapitel|Chapter|Buch|Book)\\s+([IVXLC]{1,7})(?:[.:]|\\s*[-–—])?\\s+(\\S.*)$")
private val NumRoman = Regex("^([IVXLC]{1,7})[.:]\\s+(\\S.*)$")

/** "3.2 Geschwindigkeit" → ("3.2", "Geschwindigkeit"), "Kapitel 4: Wellen" → ("4", "Wellen"), sonst (null, Titel). */
private fun splitNumber(title: String): Pair<String?, String> {
    val t = title.trim()
    for (re in arrayOf(NumArabic, NumRomanWord, NumRoman)) {
        val m = re.find(t) ?: continue
        val rest = m.groupValues[2].trim()
        if (rest.any { it.isLetter() }) return m.groupValues[1] to rest
    }
    return null to t
}

/**
 * Aus der flachen Liste (mit Ebenen) einen Baum bauen.
 *  - Steht alles unter einem einzigen Eintrag (oft der Buchtitel), rückt der Inhalt eine Ebene nach oben –
 *    sonst müsste man erst diesen einen Eintrag aufklappen, um überhaupt etwas zu sehen.
 *  - Nummern ("3.2", "Kapitel 4:") werden vom Titel getrennt, wenn mindestens die Hälfte der Einträge eine hat:
 *    Sie stehen dann in einer eigenen Spalte, und alle Titel beginnen an derselben Kante.
 */
private fun buildToc(chapters: List<Chapter>): List<TocNode> {
    class Draft(val ch: Chapter, val index: Int) {
        val kids = ArrayList<Draft>()
    }
    val roots = ArrayList<Draft>()
    val stack = ArrayList<Draft>()
    chapters.forEachIndexed { i, c ->
        val d = Draft(c, i)
        while (stack.isNotEmpty() && stack.last().ch.level >= c.level) stack.removeAt(stack.lastIndex)
        if (stack.isEmpty()) roots += d else stack.last().kids += d
        stack += d
    }
    if (roots.size == 1 && roots[0].kids.size >= 2) {
        val kids = roots[0].kids.toList()
        roots[0].kids.clear()
        roots.addAll(kids)
    }
    val split = chapters.count { splitNumber(it.title).first != null } * 2 >= chapters.size
    fun convert(d: Draft): TocNode {
        val parts = if (split) splitNumber(d.ch.title) else Pair<String?, String>(null, d.ch.title.trim())
        return TocNode(d.ch, d.index, parts.first, parts.second, d.kids.map { convert(it) })
    }
    return roots.map { convert(it) }
}

/** Die sichtbaren Zeilen: alle Einträge der obersten Ebene und darunter, was aufgeklappt ist. */
private fun visibleLines(nodes: List<TocNode>, depth: Int, isOpen: (TocNode) -> Boolean, out: ArrayList<TocLine>) {
    for (n in nodes) {
        out += TocLine(n, depth)
        if (n.children.isNotEmpty() && isOpen(n)) visibleLines(n.children, depth + 1, isOpen, out)
    }
}

/** Alle Einträge auf dem Weg zum Eintrag [target] (Indizes in book.chapters). Die klappen von selbst auf. */
private fun pathTo(nodes: List<TocNode>, target: Int): Set<Int> {
    if (target < 0) return emptySet()
    val path = ArrayList<Int>()
    fun walk(list: List<TocNode>): Boolean {
        for (n in list) {
            path += n.index
            if (n.index == target || walk(n.children)) return true
            path.removeAt(path.lastIndex)
        }
        return false
    }
    return if (walk(nodes)) path.toSet() else emptySet()
}

/** Gibt es irgendwo im Baum etwas aufzuklappen? */
private fun hasChildrenDeep(nodes: List<TocNode>): Boolean = nodes.any { it.children.isNotEmpty() || hasChildrenDeep(it.children) }

/** "Heute", "Gestern" oder "3. Nov." */
private fun shortDate(at: Long): String {
    val d = java.time.Instant.ofEpochMilli(at).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    val today = java.time.LocalDate.now()
    return when (d) {
        today -> tr("Heute", "Today")
        today.minusDays(1) -> tr("Gestern", "Yesterday")
        else -> d.format(java.time.format.DateTimeFormatter.ofPattern(if (Lang.german) "d. MMM" else "MMM d", Lang.locale))
    }
}

/**
 * Inhalt, von links (300 ms hinein, 200 ms hinaus). Oben das Buch (Umschlag, Titel, Seite), darunter drei Reiter
 * als Wörter mit einer gleitenden Linie: Kapitel, Lesezeichen, Notizen.
 *  - Kapitel wie ein gesetztes Inhaltsverzeichnis (seit 15.5): Nummern in einer eigenen Spalte, Titel an einer
 *    Kante, Seiten rechts. Unterkapitel sind aufklappbar (bis zu drei Ebenen). Der Weg zur aktuellen Stelle ist
 *    offen, die Stelle trägt den Akzent und eine feine Fortschrittslinie – und die Liste springt beim Öffnen
 *    gleich dorthin. Kurze Inhalte (bis 16 Einträge) sind ganz offen.
 *  - Alle Listen laden nur, was sichtbar ist: Auch Bücher mit Hunderten Einträgen öffnen ohne Ruckeln.
 */
@Composable
private fun ContentsDrawer(
    visible: Boolean,
    book: Book,
    state: AppState,
    nav: ReaderNav?,
    ai: ChapterSearch,
    notes: List<Note>,
    onJump: (Int) -> Unit,
    onOpenNote: (Note) -> Unit,
    onDismiss: () -> Unit,
) {
    val p = Funke.colors
    val still = reducedMotion
    val scope = rememberCoroutineScope()
    val drawerContext = LocalContext.current.applicationContext
    // Beim ersten Öffnen: Unterkapitel im Text suchen (einmal je Buch, nur wo das Buch selbst keine hat)
    LaunchedEffect(visible) { if (visible) scope.launch { ai.findSubs(book) } }
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(visible, enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)), exit = fadeOut(tween(Motion.Base))) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(p.scrim)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
            )
        }
        AnimatedVisibility(
            visible,
            enter = if (still) fadeIn(tween(Motion.Fast)) else slideInHorizontally(tween(Motion.Slow, easing = Motion.Drawer)) { -it } + fadeIn(tween(Motion.Fast)),
            exit = if (still) fadeOut(tween(Motion.Fast)) else slideOutHorizontally(tween(Motion.Base, easing = Motion.EaseOut)) { -it } + fadeOut(tween(Motion.Base)),
        ) {
            BoxWithConstraints(Modifier.fillMaxHeight()) {
                val shape = RoundedCornerShape(topEnd = Radius.sheet, bottomEnd = Radius.sheet)
                var tab by remember { mutableIntStateOf(0) }
                // Kapitelbaum und aktuelle Stelle einmal für Kopf (Lesefortschritt) und Liste (Leseweg)
                val chapters = book.chapters
                val tree = remember(chapters) { buildToc(chapters) }
                val page = nav?.page ?: 0
                val current = remember(chapters, nav, page) { if (nav == null) -1 else chapters.indexOfLast { nav.pageOfChapter(it) <= page } }
                // Auf Papier wie das Papier selbst (die Inhaltsseite des Buchs), bei Nacht leicht angehoben
                val sheetBg = if (p.isDark) p.raised(Elevation.Level2) else p.bg
                // Suche (seit 15.1): nur auf Abruf über die Lupe neben den Reitern, statt immer als Feld
                var searching by remember { mutableStateOf(false) }
                Column(
                    Modifier
                        .fillMaxHeight()
                        .width(minOf(maxWidth * 0.88f, 400.dp))
                        .elevation(shape, Elevation.Level3)
                        .clip(shape)
                        .background(sheetBg)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                        .windowInsetsPadding(WindowInsets.displayCutout),
                ) {
                    DrawerHeader(book, nav, onDismiss)
                    Spacer(Modifier.height(Space.s3))
                    DrawerTabs(
                        labels = listOf(tr("Kapitel", "Chapters"), tr("Lesezeichen", "Bookmarks"), tr("Notizen", "Notes")),
                        counts = listOf(0, book.marks.size, notes.size),
                        selected = tab,
                        onSelect = { tab = it },
                        modifier = Modifier.padding(start = Space.s5, end = Space.s2),
                        trailing = if (tab == 0 && book.chapters.size > 12) ({
                            de.funke.app.ui.components.HeaderIcon(
                                if (searching) tr("Suche schließen", "Close search") else tr("Kapitel suchen", "Search chapters"),
                                onClick = { searching = !searching },
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_search),
                                    contentDescription = null,
                                    tint = if (searching) p.accent else p.textTertiary,
                                    modifier = Modifier.size(Sizes.iconSm),
                                )
                            }
                        }) else null,
                    )
                    Crossfade(tab, modifier = Modifier.weight(1f), animationSpec = tween(Motion.Fast), label = "contentsTab") { t ->
                        when (t) {
                            0 -> ChapterList(book, state, nav, ai, searching, tree, current, onJump, onFind = { scope.launch { ai.run(book, state, epub = null, context = drawerContext) } })
                            1 -> MarkList(book, nav, onJump)
                            else -> NoteList(notes, book, nav, onOpenNote)
                        }
                    }
                }
            }
        }
    }
}

/** Kopf des Inhalts: kleiner Umschlag, Titel, Autor und wo man gerade ist ("Seite 53 von 412"). */
@Composable
private fun DrawerHeader(book: Book, nav: ReaderNav?, onClose: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = Space.s5, end = Space.s3, top = Space.s5),
        verticalAlignment = Alignment.Top,
    ) {
        BookCover(book, Modifier.width(48.dp), compact = true)
        Spacer(Modifier.width(Space.s4))
        Column(Modifier.weight(1f).padding(top = Space.s1)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium, color = p.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
            book.author?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = p.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (nav != null) {
                Spacer(Modifier.height(Space.s2))
                Text(
                    tr("Seite ${nav.page + 1} von ${nav.count}", "Page ${nav.page + 1} of ${nav.count}"),
                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                    color = p.textTertiary,
                )
            }
        }
        CloseButton(onClick = onClose)
    }
}

/** Reiter als Wörter (mit Anzahl), darunter eine Haarlinie. Die Linie unter dem gewählten Reiter gleitet mit (250 ms). */
@Composable
private fun DrawerTabs(
    labels: List<String>,
    counts: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    val p = Funke.colors
    val still = reducedMotion
    // Lage und Breite jedes Reiters (wird nur geschrieben, wenn sie sich ändert – sonst liefe das Layout im Kreis)
    val bounds = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }
    val x = remember { Animatable(0f) }
    val w = remember { Animatable(0f) }
    val target = bounds[selected]
    LaunchedEffect(target) {
        val t = target ?: return@LaunchedEffect
        if (still || w.value == 0f) {
            x.snapTo(t.first)
            w.snapTo(t.second)
        } else {
            launch { x.animateTo(t.first, tween(Motion.Medium, easing = Motion.EaseOut)) }
            w.animateTo(t.second, tween(Motion.Medium, easing = Motion.EaseOut))
        }
    }
    val line = p.accent
    val hair = p.divider
    Row(
        modifier
            .fillMaxWidth()
            .drawBehind {
                val bottom = size.height
                val hp = 1.dp.toPx()
                drawRect(hair, Offset(0f, bottom - hp), Size(size.width, hp))
                if (w.value > 0f) {
                    val h = 2.dp.toPx()
                    drawRoundRect(line, Offset(x.value, bottom - h), Size(w.value, h), CornerRadius(h / 2f))
                }
            },
        horizontalArrangement = Arrangement.spacedBy(Space.s6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        labels.forEachIndexed { i, label ->
            val on = i == selected
            val color by animateColorAsState(if (on) p.text else p.textTertiary, Motion.fade(Motion.Fast), label = "tab")
            Row(
                Modifier
                    .onPlaced { c ->
                        val v = c.positionInParent().x to c.size.width.toFloat()
                        if (bounds[i] != v) bounds[i] = v
                    }
                    .heightIn(min = Sizes.touch)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, role = Role.Tab) { onSelect(i) }
                    .semantics { this.selected = on },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = color, maxLines = 1)
                if (counts[i] > 0) {
                    Spacer(Modifier.width(Space.s1))
                    Text("${counts[i]}", style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.textTertiary)
                }
            }
        }
        if (trailing != null) {
            Spacer(Modifier.weight(1f))
            trailing()
        }
    }
}

@Composable
private fun ChapterList(
    book: Book,
    state: AppState,
    nav: ReaderNav?,
    ai: ChapterSearch,
    searching: Boolean,
    tree: List<TocNode>,
    current: Int,
    onJump: (Int) -> Unit,
    onFind: () -> Unit,
) {
    val p = Funke.colors
    val still = reducedMotion
    val chapters = book.chapters
    // Lumi kann Kapitel nur finden, wo es auf diesem Handy geht: EPUB immer, PDF erst ab Android 15 (Text aus dem PDF).
    // Sonst gibt es den Knopf gar nicht – statt eines Knopfs, der nur einen Fehler bringt.
    val possible = book.format == BookFormat.Epub || Build.VERSION.SDK_INT >= 35
    val ready = possible && state.aiReady && state.aiConsent
    if (chapters.isEmpty() || nav == null) {
        ChaptersEmpty(book, possible, ready, ai, onFind)
        return
    }
    val path = remember(tree, current) { pathTo(tree, current) }
    val open = remember(book.id) { mutableStateMapOf<Int, Boolean>() }
    val small = chapters.size <= 16
    var query by remember { mutableStateOf("") }
    LaunchedEffect(searching) { if (!searching) query = "" }
    val q = query.trim()

    val lines = ArrayList<TocLine>()
    if (q.isEmpty()) {
        visibleLines(tree, 0, { open[it.index] ?: (small || it.index in path) }, lines)
    } else {
        val all = ArrayList<TocLine>()
        visibleLines(tree, 0, { true }, all)
        all.filterTo(lines) { it.node.title.contains(q, ignoreCase = true) || it.node.number?.startsWith(q) == true }
    }
    // Rechts die Pfeile zum Aufklappen (Platz für alle Zeilen, damit die Seitenzahlen untereinander stehen)
    val chevrons = q.isEmpty() && hasChildrenDeep(tree)
    // Die genaueste sichtbare Stelle: das aktuelle Unterkapitel – oder, wenn es zugeklappt ist, das Kapitel darüber
    val highlight = lines.firstOrNull { it.node.index == current }?.node?.index
        ?: lines.lastOrNull { it.node.index in path }?.node?.index ?: -1
    // Wie weit man in diesem Eintrag ist: von seiner ersten Seite bis vor den nächsten Eintrag (nach seinen Unterkapiteln)
    val page = nav.page
    val progress = remember(tree, highlight, page, nav) {
        val node = findNode(tree, highlight) ?: return@remember null
        val from = nav.pageOfChapter(node.chapter)
        val after = lastIndexIn(node) + 1
        val until = chapters.drop(after).map { nav.pageOfChapter(it) }.firstOrNull { it > from } ?: nav.count
        ((page - from + 1f) / (until - from).coerceAtLeast(1)).coerceIn(0f, 1f)
    }

    // Nummern in einer eigenen Spalte, rechtsbündig: Kapitel groß und leicht, Unterkapitel klein darunter.
    // So stehen alle Titel an derselben Kante wie im Inhaltsverzeichnis eines gedruckten Buchs.
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val bigNum = TocBigNumber
    val smallNum = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03")
    val numCol: Dp = remember(tree, bigNum, smallNum, density) {
        var widest = 0
        fun scan(list: List<TocNode>, d: Int) {
            for (n in list) {
                n.number?.let { widest = maxOf(widest, measurer.measure(it, if (d == 0) bigNum else smallNum).size.width) }
                scan(n.children, d + 1)
            }
        }
        scan(tree, 0)
        if (widest == 0) 0.dp else with(density) { widest.toDp() } + TocNumberGap
    }.let { if (it > 84.dp) 0.dp else it } // sehr lange Nummern ("12.3.4"): dann leise vor dem Titel

    val listState = rememberLazyListState()
    // Beim Öffnen gleich dorthin, wo man gerade liest (mit etwas Luft darüber)
    LaunchedEffect(Unit) {
        val i = lines.indexOfFirst { it.node.index == highlight }
        if (i > 2) listState.scrollToItem(i - 2)
    }

    Column(Modifier.fillMaxSize()) {
        Unfold(visible = searching) {
            ChapterSearchField(query, onChange = { query = it }, modifier = Modifier.padding(start = Space.s5, end = Space.s5, top = Space.s4))
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = Space.s3, end = Space.s2, top = Space.s2, bottom = Space.s8),
        ) {
            if (q.isNotEmpty() && lines.isEmpty()) {
                item(key = "nohit") { EmptyLine(tr("Kein Kapitel mit „$q“", "No chapter with \"$q\"")) }
            }
            itemsIndexed(lines, key = { _, it -> it.node.index }) { i, line ->
                val n = line.node
                val expandable = q.isEmpty() && n.children.isNotEmpty()
                val expanded = expandable && (open[n.index] ?: (small || n.index in path))
                ChapterRow(
                    node = n,
                    depth = line.depth,
                    page = nav.pageOfChapter(n.chapter),
                    read = n.index < current && n.index !in path,
                    onPath = n.index in path,
                    highlight = n.index == highlight,
                    progress = if (n.index == highlight) progress else null,
                    numCol = numCol,
                    groupStart = q.isEmpty() && line.depth == 0 && i > 0,
                    chevrons = chevrons,
                    expandable = expandable,
                    expanded = expanded,
                    onToggle = { open[n.index] = !expanded },
                    onJump = onJump,
                    modifier = if (still) Modifier else Modifier.animateItem(
                        fadeInSpec = tween(Motion.Base, delayMillis = 60),
                        placementSpec = tween(Motion.Medium, easing = Motion.EaseOut),
                        fadeOutSpec = tween(Motion.Fast),
                    ),
                )
            }
            if (ai.subsRunning) {
                item(key = "subs") {
                    Text(
                        tr("Lumi sucht die Unterkapitel …", "Lumi is looking for sub-chapters …"),
                        style = MaterialTheme.typography.bodySmall,
                        color = p.textTertiary,
                        modifier = Modifier.padding(start = Space.s2 + numCol, end = Space.s3, top = Space.s3, bottom = Space.s3),
                    )
                }
            }
            // Woher die Kapitel kommen (KI oder aus dem Text): leise Zeile am Ende, mit "Neu suchen"
            if (q.isEmpty() && (book.chaptersBy == ChapterSource.Ai || book.chaptersBy == ChapterSource.Text)) {
                item(key = "source") { SourceFooter(book, ready, ai, onFind) }
            }
        }
    }
}

/** Den Eintrag mit diesem Index im Baum finden. */
private fun findNode(nodes: List<TocNode>, index: Int): TocNode? {
    for (n in nodes) {
        if (n.index == index) return n
        findNode(n.children, index)?.let { return it }
    }
    return null
}

/** Letzter Index im Teilbaum (der Eintrag selbst oder sein letztes Unterkapitel). */
private fun lastIndexIn(node: TocNode): Int = node.children.lastOrNull()?.let { lastIndexIn(it) } ?: node.index

/** Kapitelnummern groß und leicht, wie im Inhaltsverzeichnis eines gesetzten Buchs. */
private val TocBigNumber: TextStyle
    @Composable get() = MaterialTheme.typography.titleLarge.copy(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.3).sp,
        fontFeatureSettings = "tnum, ss03",
    )

/** Abstand zwischen Nummernspalte und Titel. */
private val TocNumberGap = 14.dp

/**
 * Eine Zeile im Inhalt (seit 15.5 wie ein gesetztes Inhaltsverzeichnis): links die Nummer in einer eigenen Spalte
 * (Kapitel groß und leicht, Unterkapitel klein), daneben der Titel, rechts die Seite und – wo es Unterkapitel
 * gibt – der Pfeil zum Aufklappen. Gelesene Kapitel stehen leiser. Wo man gerade ist, tragen Nummer und Seite den
 * Akzent, und unter dem Titel zeigt eine feine Linie, wie weit man in diesem Abschnitt ist. Antippen springt.
 */
@Composable
private fun ChapterRow(
    node: TocNode,
    depth: Int,
    page: Int,
    read: Boolean,
    onPath: Boolean,
    highlight: Boolean,
    progress: Float?,
    numCol: Dp,
    groupStart: Boolean,
    chevrons: Boolean,
    expandable: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit,
    onJump: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = Funke.colors
    val top = depth == 0
    val turn by animateFloatAsState(if (expanded) 90f else 0f, Motion.spec(Motion.Medium), label = "tocChevron")
    val fill by animateFloatAsState(progress ?: 0f, Motion.spec(Motion.Medium), label = "tocProgress")
    val numColor = if (onPath || highlight) p.accent else p.textTertiary
    // Ohne eigene Spalte (keine oder sehr lange Nummern): Nummer leise vor dem Titel
    val inlineNumber = if (numCol == 0.dp) node.number else null
    val title = if (inlineNumber != null) buildAnnotatedString {
        withStyle(SpanStyle(color = numColor, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum, ss03")) { append(inlineNumber) }
        append("  ")
        append(node.title)
    } else AnnotatedString(node.title)
    val titleStyle = when (depth) {
        0 -> MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp, lineHeight = 22.sp)
        1 -> MaterialTheme.typography.bodyMedium
        else -> MaterialTheme.typography.bodySmall
    }
    val titleColor = when {
        highlight || (top && onPath) -> p.text
        read -> p.textSecondary
        top -> p.text
        else -> p.textSecondary
    }
    val track = p.track
    val line = p.accent
    val chevronW = if (chevrons) 32.dp else Space.s2
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = if (groupStart) 6.dp else Space.none)
            .heightIn(min = if (top) 52.dp else if (depth == 1) 40.dp else 36.dp)
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.99f) { onJump(page) }
            .drawBehind {
                // Wie weit man in diesem Abschnitt ist: feine Linie unter dem Titel, von der Titelkante bis zur Seitenzahl
                if (progress != null) {
                    val x0 = (Space.s2 + numCol).toPx()
                    val x1 = size.width - chevronW.toPx()
                    val y = size.height - 5.dp.toPx()
                    val h = 2.dp.toPx()
                    val r = CornerRadius(h / 2f)
                    drawRoundRect(track, Offset(x0, y), Size(x1 - x0, h), r)
                    if (fill > 0f) drawRoundRect(line, Offset(x0, y), Size(((x1 - x0) * fill).coerceAtLeast(h), h), r)
                }
            }
            .padding(start = Space.s2)
            .semantics { if (highlight) stateDescription = tr("Hier liest du gerade", "You're reading here") },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (numCol > 0.dp) {
            Text(
                node.number.orEmpty(),
                style = if (top) TocBigNumber else MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                color = numColor,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.width(numCol).padding(end = TocNumberGap),
            )
        }
        Text(
            title,
            // Lange deutsche Wörter ("Momentangeschwindigkeit") sauber trennen statt sie abzuschneiden
            style = titleStyle.copy(hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
            fontWeight = if (top || highlight) FontWeight.Medium else FontWeight.Normal,
            color = titleColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = if (top) Space.s3 else 9.dp),
        )
        Text(
            "${page + 1}",
            style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
            color = if (highlight) p.accent else p.textTertiary,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = Space.s3).widthIn(min = 28.dp),
        )
        if (chevrons) {
            Box(
                Modifier
                    .size(width = 32.dp, height = 44.dp)
                    .then(
                        if (expandable) Modifier
                            .clip(Shapes.sm)
                            .clickable(role = Role.Button, onClick = onToggle)
                            .semantics { contentDescription = if (expanded) tr("Zuklappen", "Collapse") else tr("Unterkapitel zeigen", "Show sub-chapters") }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (expandable) {
                    Icon(
                        painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = if (onPath) p.accent else p.textTertiary,
                        modifier = Modifier.size(14.dp).graphicsLayer { rotationZ = turn },
                    )
                }
            }
        } else {
            Spacer(Modifier.width(Space.s2))
        }
    }
}

@Composable
private fun ChapterSearchField(query: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) =
    de.funke.app.ui.components.SearchField(query, onChange, tr("Kapitel suchen", "Search chapters"), modifier)

/** Ohne Inhaltsverzeichnis: ruhiger Hinweis; mit Lumina-KI kann Lumi die Kapitel finden. */
@Composable
private fun ChaptersEmpty(book: Book, possible: Boolean, ready: Boolean, ai: ChapterSearch, onFind: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    if (ai.running) {
        Column(Modifier.fillMaxSize().padding(top = Space.s12), horizontalAlignment = Alignment.CenterHorizontally) {
            Lumi2D(LumiPose.Reading, Modifier.size(88.dp), tappable = false)
            Spacer(Modifier.height(Space.s4))
            Text(tr("Lumi sucht die Kapitel …", "Lumi is looking for the chapters …"), style = MaterialTheme.typography.titleSmall, color = p.text)
        }
        return
    }
    // Noch nie gesucht (kommt kurz vor der automatischen Suche vor): einfach suchen lassen.
    // Schon gesucht und nichts gefunden: Ein zweiter Versuch lohnt nur, wenn die KI noch helfen kann.
    val searched = book.aiTried && book.textTried
    val aiCanHelp = ready && remember(book.id, ai.running) { ChapterQuota.allows(context, book.id) }
    val err = ai.error
    EmptyState(
        icon = R.drawable.ic_list,
        title = tr("Kein Inhaltsverzeichnis", "No table of contents"),
        text = when {
            !possible || err == "old_android" -> aiError("old_android")
            searched -> tr("Lumi hat in diesem Buch keine Kapitel gefunden.", "Lumi didn't find any chapters in this book.")
            else -> null
        },
    ) {
        if (possible && (!searched || aiCanHelp)) {
            Spacer(Modifier.height(Space.s5))
            PillButton(
                if (searched) tr("Mit der KI suchen", "Search with AI") else tr("Kapitel finden", "Find chapters"),
                onClick = onFind,
                filled = true,
            )
        }
    }
}

/**
 * Woher die Kapitel kommen, als leise Zeile am Ende: von der Lumina-KI oder ohne KI aus dem Text erkannt.
 * Rechts "Neu suchen" (KI) bzw. "Mit der KI suchen" (Text), aber nur, wenn die KI an ist und diese Woche noch
 * Platz für das Buch hat – sonst gibt es den Knopf nicht.
 */
@Composable
private fun SourceFooter(book: Book, ready: Boolean, ai: ChapterSearch, onFind: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val byAi = book.chaptersBy == ChapterSource.Ai
    val canSearch = ready && !ai.running && remember(book.id, book.chaptersBy, ai.running) { ChapterQuota.allows(context, book.id) }
    // An derselben linken Kante wie Kopf und Nummern
    Column(Modifier.fillMaxWidth().padding(top = Space.s5)) {
        Box(Modifier.fillMaxWidth().padding(start = Space.s2, end = Space.s3).height(1.dp).background(p.divider))
        Row(Modifier.fillMaxWidth().heightIn(min = Sizes.touch).padding(start = Space.s2, top = Space.s2), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(if (byAi) R.drawable.ic_sparkles else R.drawable.ic_text),
                contentDescription = null,
                tint = p.textTertiary,
                modifier = Modifier.size(Sizes.iconSm),
            )
            Spacer(Modifier.width(Space.s2))
            Text(
                when {
                    ai.running -> tr("Lumi sucht neu …", "Lumi is searching again …")
                    byAi -> tr("Von der Lumina-KI erkannt", "Found by Lumina AI")
                    else -> tr("Aus dem Text erkannt", "Found in the text")
                },
                style = MaterialTheme.typography.bodySmall,
                color = p.textTertiary,
                modifier = Modifier.weight(1f),
            )
            if (canSearch) {
                Box(
                    Modifier
                        .heightIn(min = Sizes.touch)
                        .bouncyClickable(shape = Shapes.pill, pressedScale = 0.95f, onClick = onFind)
                        .padding(horizontal = Space.s3),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (byAi) tr("Neu suchen", "Search again") else tr("Mit der KI suchen", "Search with AI"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.accent,
                    )
                }
            }
        }
        ai.error?.let {
            Text(aiError(it), style = MaterialTheme.typography.bodySmall, color = p.textSecondary, modifier = Modifier.padding(start = Space.s2, end = Space.s3))
        }
    }
}

@Composable
private fun MarkList(book: Book, nav: ReaderNav?, onJump: (Int) -> Unit) {
    val still = reducedMotion
    if (book.marks.isEmpty() || nav == null) {
        EmptyState(R.drawable.ic_bookmark, tr("Keine Lesezeichen", "No bookmarks"), tr("Im Menü auf „Merken“ tippen.", "Tap “Bookmark” in the menu."))
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(start = Space.s2, end = Space.s2, top = Space.s3, bottom = Space.s8)) {
        items(book.marks, key = { "${it.section}:${it.offset}:${it.at}" }) { m ->
            val page = nav.pageOfPos(m.section, m.offset)
            MarkLine(
                title = nav.chapterAt(page, book.chapters)?.title ?: tr("Seite ${page + 1}", "Page ${page + 1}"),
                date = shortDate(m.at),
                page = page,
                onJump = { onJump(page) },
                onRemove = { Library.removeMarks(book.id, listOf(m)) },
                modifier = if (still) Modifier else Modifier.animateItem(
                    fadeInSpec = tween(Motion.Base),
                    placementSpec = tween(Motion.Medium, easing = Motion.EaseOut),
                    fadeOutSpec = tween(Motion.Fast),
                ),
            )
        }
    }
}

/** Lesezeichen: Band, Kapitel und Tag, rechts Seite und leises Entfernen. */
@Composable
private fun MarkLine(title: String, date: String, page: Int, onJump: () -> Unit, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.button)
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.99f, onClick = onJump)
            .padding(start = Space.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(width = 10.dp, height = 16.dp).clip(RibbonShape).background(p.accent))
        Spacer(Modifier.width(Space.s4))
        Column(Modifier.weight(1f).padding(vertical = Space.s2)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = p.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(date, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 1)
        }
        Text(
            "${page + 1}",
            style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
            color = p.textTertiary,
            modifier = Modifier.padding(start = Space.s3),
        )
        Box(
            Modifier
                .size(width = 44.dp, height = Sizes.touch)
                .clip(CircleShape)
                .clickable(onClick = onRemove)
                .semantics { contentDescription = tr("Lesezeichen entfernen", "Remove bookmark") },
            contentAlignment = Alignment.Center,
        ) { CloseIcon(p.textTertiary, Modifier.size(10.dp)) }
    }
}

@Composable
private fun NoteList(notes: List<Note>, book: Book, nav: ReaderNav?, onOpen: (Note) -> Unit) {
    if (notes.isEmpty() || nav == null) {
        EmptyState(R.drawable.ic_pen, tr("Keine Notizen", "No notes"), tr("Im Menü auf „Notiz“ tippen.", "Tap “Note” in the menu."))
        return
    }
    val sorted = remember(notes, nav) { notes.sortedBy { nav.pageOfPos(it.section, it.offset) } }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = Space.s2, end = Space.s2, top = Space.s3, bottom = Space.s8),
        verticalArrangement = Arrangement.spacedBy(Space.s1),
    ) {
        items(sorted, key = { it.id }) { n ->
            val page = nav.pageOfPos(n.section, n.offset)
            NoteLine(n.text, page, nav.chapterAt(page, book.chapters)?.title, onClick = { onOpen(n) })
        }
    }
}

/**
 * Notiz im Inhalt: wie ein Lesezeichen aufgebaut – vorn das Zeichen (ein kleiner Stift), daneben der Text und
 * darunter Seite und Kapitel. Ohne farbigen Strich am Rand: Die Notiz selbst ist der Inhalt.
 */
@Composable
private fun NoteLine(text: String, page: Int, chapter: String?, onClick: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.99f, onClick = onClick)
            .padding(horizontal = Space.s3, vertical = Space.s3),
        verticalAlignment = Alignment.Top,
    ) {
        // So breit wie Band und Abstand bei den Lesezeichen: Der Text beginnt an derselben Kante
        Box(Modifier.width(14.dp).padding(top = 3.dp), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_pen), contentDescription = null, tint = p.accent, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            Text(text, style = MaterialTheme.typography.bodyMedium, color = p.text, maxLines = 4, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(Space.s1))
            Text(
                listOfNotNull(tr("Seite ${page + 1}", "Page ${page + 1}"), chapter).joinToString(", "),
                style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                color = p.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Leerer Reiter: Symbol in einem zarten Kreis, eine Überschrift, höchstens eine kurze Zeile. */
@Composable
private fun EmptyState(icon: Int, title: String, text: String?, content: @Composable ColumnScope.() -> Unit = {}) {
    val p = Funke.colors
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = Space.s8, end = Space.s8, top = Space.s12, bottom = Space.s8),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(p.track), contentAlignment = Alignment.Center) {
            Icon(painterResource(icon), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.iconLg))
        }
        Spacer(Modifier.height(Space.s4))
        Text(title, style = MaterialTheme.typography.titleSmall, color = p.text, textAlign = TextAlign.Center)
        if (text != null) {
            Spacer(Modifier.height(Space.s1))
            Text(text, style = MaterialTheme.typography.bodySmall, color = p.textSecondary, textAlign = TextAlign.Center)
        }
        content()
    }
}

@Composable
private fun EmptyLine(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = Funke.colors.textSecondary, modifier = Modifier.padding(start = Space.s2, end = Space.s3, top = Space.s3, bottom = Space.s3))
}
