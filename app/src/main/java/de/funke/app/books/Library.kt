package de.funke.app.books

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors

/**
 * Die eigene Bibliothek (seit 13.4): PDFs und E-Books, die man aus seinen Dateien hinzufügt.
 *
 * Beim Hinzufügen wird eine Kopie in den App-Speicher gelegt. So bleibt das Buch lesbar, auch wenn
 * die Originaldatei verschoben oder gelöscht wird oder die Freigabe von "Dateien" abläuft.
 * Die Leseposition liegt je Buch unter einem eigenen kleinen Schlüssel, damit beim Umblättern nicht
 * jedes Mal die ganze Liste geschrieben wird.
 */
enum class BookFormat(val id: String) {
    Pdf("pdf"), Epub("epub");

    companion object {
        fun byId(id: String?): BookFormat = entries.firstOrNull { it.id == id } ?: Pdf
    }
}

/** Woher die Kapitel kommen: keine, aus dem Buch selbst oder von der Lumina-KI. */
enum class ChapterSource(val id: String) {
    /** Book: eigenes Inhaltsverzeichnis des Buchs. Ai: Lumina-KI. Text: ohne KI aus dem Text erkannt (seit 15.2). */
    None("none"), Book("book"), Ai("ai"), Text("text");

    companion object {
        fun byId(id: String?): ChapterSource = entries.firstOrNull { it.id == id } ?: None
    }
}

/**
 * Kapitel oder Unterkapitel. Bei PDFs ist [section] die Seite (ab 0), [block] immer 0.
 * Bei EPUBs ist [section] der Abschnitt und [block] der Absatz darin.
 */
data class Chapter(val title: String, val level: Int, val section: Int, val block: Int = 0) {
    fun toJson(): JSONObject = JSONObject().put("t", title).put("l", level).put("s", section).put("b", block)

    companion object {
        fun fromJson(o: JSONObject) = Chapter(o.optString("t"), o.optInt("l", 1).coerceIn(1, 3), o.optInt("s"), o.optInt("b"))
    }
}

/** Lesezeichen. [offset] = Zeichen im Abschnitt (EPUB) bzw. 0 (PDF). [page] nur zur Anzeige. */
data class Mark(val section: Int, val offset: Int, val page: Int, val at: Long) {
    fun toJson(): JSONObject = JSONObject().put("s", section).put("o", offset).put("p", page).put("at", at)

    companion object {
        fun fromJson(o: JSONObject) = Mark(o.optInt("s"), o.optInt("o"), o.optInt("p"), o.optLong("at"))
    }
}

data class Book(
    val id: String,
    val title: String,
    val author: String?,
    val format: BookFormat,
    val file: String,
    val cover: String?,
    val source: String,
    val size: Long,
    val addedAt: Long,
    /** PDF: Seitenzahl. EPUB: Zahl der Abschnitte. */
    val sections: Int,
    val chapters: List<Chapter> = emptyList(),
    val chaptersBy: ChapterSource = ChapterSource.None,
    val marks: List<Mark> = emptyList(),
    val minutes: Int = 0,
    /** Hat Lumina schon einmal von selbst versucht, Kapitel zu finden? (nur einmal je Buch) */
    val aiTried: Boolean = false,
    /** Wurden schon nummerierte Unterkapitel ("3.2 …") im Text gesucht? (seit 14.0, nur einmal je Buch) */
    val subTried: Boolean = false,
    /** Wurde das eingebaute Inhaltsverzeichnis des PDFs schon gelesen? (seit 15.0, nur einmal je Buch) */
    val outlineTried: Boolean = false,
    /** Wurden Kapitel schon ohne KI im Text gesucht? (seit 15.2) */
    val textTried: Boolean = false,
    // Leseposition (eigener Schlüssel, siehe Library.savePosition)
    val openedAt: Long = 0L,
    val section: Int = 0,
    val offset: Int = 0,
    val progress: Float = 0f,
) {
    val started: Boolean get() = openedAt > 0L
    val finished: Boolean get() = progress >= 0.995f

    fun toJson(): JSONObject = JSONObject()
        .put("id", id).put("title", title).put("author", author ?: JSONObject.NULL)
        .put("format", format.id).put("file", file).put("cover", cover ?: JSONObject.NULL)
        .put("source", source).put("size", size).put("added", addedAt).put("sections", sections)
        // Kapitel stehen seit 15.0 in einer eigenen Datei je Buch (siehe Library.writeToc)
        .put("chaptersBy", chaptersBy.id)
        .put("marks", JSONArray().also { a -> marks.forEach { a.put(it.toJson()) } })
        .put("minutes", minutes)
        .put("aiTried", aiTried)
        .put("subTried", subTried)
        .put("outlineTried", outlineTried)
        .put("textTried", textTried)

    companion object {
        /** Liest auch noch Kapitel aus der Liste (so war es bis 14.0; beim ersten Start werden sie umgezogen). */
        fun fromJson(o: JSONObject): Book? = runCatching {
            val chapters = o.optJSONArray("chapters")?.let { a -> List(a.length()) { Chapter.fromJson(a.getJSONObject(it)) } }.orEmpty()
            val marks = o.optJSONArray("marks")?.let { a -> List(a.length()) { Mark.fromJson(a.getJSONObject(it)) } }.orEmpty()
            Book(
                id = o.getString("id"),
                title = o.optString("title").ifBlank { "Buch" },
                author = if (o.isNull("author")) null else o.optString("author").ifBlank { null },
                format = BookFormat.byId(o.optString("format")),
                file = o.getString("file"),
                cover = if (o.isNull("cover")) null else o.optString("cover").ifBlank { null },
                source = o.optString("source"),
                size = o.optLong("size"),
                addedAt = o.optLong("added"),
                sections = o.optInt("sections", 1).coerceAtLeast(1),
                chapters = chapters,
                chaptersBy = ChapterSource.byId(o.optString("chaptersBy")),
                marks = marks,
                minutes = o.optInt("minutes"),
                aiTried = o.optBoolean("aiTried"),
                subTried = o.optBoolean("subTried"),
                outlineTried = o.optBoolean("outlineTried"),
                textTried = o.optBoolean("textTried"),
            )
        }.getOrNull()
    }
}

/** Wie die Bibliothek sortiert ist. */
enum class LibrarySort(val id: String) {
    Recent("recent"), Title("title"), Added("added");

    companion object {
        fun byId(id: String?): LibrarySort = entries.firstOrNull { it.id == id } ?: Recent
    }
}

sealed class ImportResult {
    class Added(val book: Book) : ImportResult()
    class Exists(val book: Book) : ImportResult()
    class Failed(val reason: Reason) : ImportResult()

    enum class Reason { Unsupported, Drm, Password, Broken, TooBig, NoSpace }
}

object Library {
    private const val K_BOOKS = "books"
    private const val K_POS = "bookpos_"
    private const val K_SORT = "books_sort"
    private const val MAX_BYTES = 300L * 1024 * 1024

    /** Alle Bücher (Compose-Zustand, die Oberfläche aktualisiert sich von selbst). */
    val books = mutableStateListOf<Book>()

    var sort: LibrarySort = LibrarySort.Recent
        private set

    private var prefs: SharedPreferences? = null
    private var dir: File? = null

    /**
     * Inhaltsverzeichnisse liegen seit 15.0 je Buch in einer eigenen kleinen Datei ("<id>.toc.json") statt in der
     * Bücherliste. Die Liste wird oft gespeichert (z. B. jede gelesene Minute) – mit Hunderten Kapiteln darin hätte
     * das beim Lesen kurz gehakt, und die App hätte beim Start mehr zu lesen. Geladen wird beim Öffnen des Buchs.
     */
    private val tocLoaded = HashSet<String>()
    private val writer = Executors.newSingleThreadExecutor()

    fun attach(context: Context) {
        if (prefs != null) return
        val app = context.applicationContext
        val sp = app.getSharedPreferences("funke", Context.MODE_PRIVATE)
        prefs = sp
        dir = File(app.filesDir, "books").also { it.mkdirs() }
        sort = LibrarySort.byId(sp.getString(K_SORT, null))
        val list = runCatching { JSONArray(sp.getString(K_BOOKS, "[]")) }.getOrNull() ?: JSONArray()
        val loaded = ArrayList<Book>()
        var moved = true
        var legacy = false
        for (i in 0 until list.length()) {
            val o = list.optJSONObject(i) ?: continue
            val b = Book.fromJson(o) ?: continue
            if (!File(dir, b.file).exists()) continue
            if (o.has("chapters")) {
                // Umzug aus der Liste in die eigene Datei (einmalig nach dem Update auf 15.0)
                legacy = true
                tocLoaded += b.id
                if (b.chapters.isNotEmpty() && !writeTocNow(b.id, b.chapters)) moved = false
            }
            loaded += withPosition(b, sp)
        }
        books.clear()
        books.addAll(loaded)
        if (legacy && moved) save()
    }

    private fun tocFile(id: String): File? = dir?.let { File(it, "$id.toc.json") }

    private fun tocJson(chapters: List<Chapter>): String = JSONArray().also { a -> chapters.forEach { a.put(it.toJson()) } }.toString()

    /** Sofort schreiben (beim Umzug und Hinzufügen). @return ob es geklappt hat */
    private fun writeTocNow(id: String, chapters: List<Chapter>): Boolean = runCatching {
        val f = tocFile(id) ?: return@runCatching false
        if (chapters.isEmpty()) {
            f.delete()
            return@runCatching true
        }
        val tmp = File(f.path + ".tmp")
        tmp.writeText(tocJson(chapters))
        tmp.renameTo(f) || (f.delete() && tmp.renameTo(f))
    }.getOrDefault(false)

    /** Im Hintergrund schreiben (der Reihe nach, damit nie eine ältere Fassung eine neuere überschreibt). */
    private fun writeToc(id: String, chapters: List<Chapter>) {
        writer.execute { writeTocNow(id, chapters) }
    }

    /** Kapitel eines Buchs aus seiner Datei holen (einmal, beim Öffnen). */
    suspend fun loadChapters(id: String) {
        if (id in tocLoaded) return
        val f = tocFile(id) ?: return
        val list = withContext(Dispatchers.IO) {
            runCatching {
                if (!f.exists()) null else JSONArray(f.readText()).let { a -> List(a.length()) { Chapter.fromJson(a.getJSONObject(it)) } }
            }.getOrNull()
        }
        tocLoaded += id
        if (list.isNullOrEmpty()) return
        val i = books.indexOfFirst { it.id == id }
        if (i >= 0 && books[i].chapters.isEmpty()) books[i] = books[i].copy(chapters = list)
    }

    private fun withPosition(b: Book, sp: SharedPreferences): Book {
        val raw = sp.getString(K_POS + b.id, null) ?: return b
        val parts = raw.split(':')
        return b.copy(
            section = parts.getOrNull(0)?.toIntOrNull() ?: 0,
            offset = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            progress = parts.getOrNull(2)?.toFloatOrNull() ?: 0f,
            openedAt = parts.getOrNull(3)?.toLongOrNull() ?: 0L,
        )
    }

    fun get(id: String?): Book? = if (id == null) null else books.firstOrNull { it.id == id }

    fun sorted(): List<Book> = when (sort) {
        LibrarySort.Recent -> books.sortedWith(compareByDescending<Book> { maxOf(it.openedAt, it.addedAt) })
        LibrarySort.Title -> books.sortedBy { it.title.lowercase() }
        LibrarySort.Added -> books.sortedByDescending { it.addedAt }
    }

    fun updateSort(s: LibrarySort) {
        sort = s
        prefs?.edit()?.putString(K_SORT, s.id)?.apply()
    }

    fun file(book: Book): File = File(dir, book.file)
    fun coverFile(book: Book): File? = book.cover?.let { File(dir, it) }?.takeIf { it.exists() }

    private fun save() {
        val a = JSONArray()
        books.forEach { a.put(it.toJson()) }
        prefs?.edit()?.putString(K_BOOKS, a.toString())?.apply()
    }

    fun update(id: String, change: (Book) -> Book) {
        val i = books.indexOfFirst { it.id == id }
        if (i < 0) return
        val old = books[i]
        books[i] = change(old)
        if (books[i].chapters !== old.chapters) writeToc(id, books[i].chapters)
        pending[id]?.let { pos -> pending[id] = books[i].copy(section = pos.section, offset = pos.offset, progress = pos.progress, openedAt = pos.openedAt) }
        save()
    }

    /** Positionen beim Lesen, noch nicht in [books] (siehe [savePosition]). */
    private val pending = HashMap<String, Book>()

    /**
     * Leseposition merken (bei jedem Umblättern). Seit 14.0 nur im Speicher und in den Einstellungen –
     * nicht mehr in der beobachteten Liste. Sonst zeichnete jedes Umblättern den ganzen Reader (und das
     * Regal dahinter) neu, und das Blättern ruckelte. In die Liste kommt die Position beim Schließen ([commitPosition]).
     */
    fun savePosition(id: String, section: Int, offset: Int, progress: Float) {
        val b = pending[id] ?: get(id) ?: return
        val now = System.currentTimeMillis()
        val p = progress.coerceIn(0f, 1f)
        pending[id] = b.copy(section = section, offset = offset, progress = p, openedAt = now)
        prefs?.edit()?.putString(K_POS + id, "$section:$offset:$p:$now")?.apply()
    }

    /** Die zuletzt gemerkte Position in die Liste übernehmen (beim Schließen des Buchs). */
    fun commitPosition(id: String) {
        val pos = pending.remove(id) ?: return
        val i = books.indexOfFirst { it.id == id }
        if (i < 0) return
        books[i] = books[i].copy(section = pos.section, offset = pos.offset, progress = pos.progress, openedAt = pos.openedAt)
    }

    fun addMinute(id: String) = update(id) { it.copy(minutes = it.minutes + 1) }

    fun rename(id: String, title: String) {
        val t = title.trim()
        if (t.isNotEmpty()) update(id) { it.copy(title = t.take(160)) }
    }

    fun addMark(id: String, mark: Mark) = update(id) { b ->
        b.copy(marks = (b.marks + mark).sortedWith(compareBy({ it.section }, { it.offset })))
    }

    fun removeMarks(id: String, marks: List<Mark>) = update(id) { b -> b.copy(marks = b.marks - marks.toSet()) }

    fun setChapters(id: String, chapters: List<Chapter>, by: ChapterSource) = update(id) {
        it.copy(chapters = chapters, chaptersBy = by, aiTried = true, textTried = it.textTried || by == ChapterSource.Text)
    }

    /** Die Suche ohne KI ist gelaufen (auch wenn sie nichts gefunden hat). */
    fun markTextTried(id: String) = update(id) { it.copy(aiTried = true, textTried = true) }

    fun markAiTried(id: String) = update(id) { it.copy(aiTried = true) }

    fun markSubTried(id: String) = update(id) { it.copy(subTried = true) }

    /**
     * Ergebnis des eingebauten PDF-Inhaltsverzeichnisses (seit 15.0). Gefunden → es ersetzt Kapitel von Lumi
     * (das Verzeichnis des Buchs ist genauer und hat oft Unterkapitel). Nichts gefunden → nur vermerken.
     */
    fun setOutline(id: String, chapters: List<Chapter>?) = update(id) { b ->
        if (chapters.isNullOrEmpty()) b.copy(outlineTried = true)
        else b.copy(chapters = chapters, chaptersBy = ChapterSource.Book, outlineTried = true, aiTried = true)
    }

    /** Gefundene Unterkapitel zu den Kapiteln stellen (die Kapitel der Ebene 1 bleiben). */
    fun mergeSubchapters(id: String, subs: List<Chapter>) = update(id) { b ->
        val merged = (b.chapters.filter { it.level == 1 } + subs)
            .sortedWith(compareBy({ it.section }, { it.block }, { it.level }))
        b.copy(chapters = merged, subTried = true)
    }

    fun remove(id: String) {
        val b = get(id) ?: return
        runCatching { file(b).delete() }
        b.cover?.let { runCatching { File(dir, it).delete() } }
        tocFile(id)?.let { f -> writer.execute { runCatching { f.delete() } } }
        tocLoaded -= id
        books.removeAll { it.id == id }
        pending.remove(id)
        prefs?.edit()?.remove(K_POS + id)?.apply()
        save()
    }

    // ----- Hinzufügen -----

    suspend fun import(context: Context, uri: Uri): ImportResult = withContext(Dispatchers.IO) {
        val folder = dir ?: return@withContext ImportResult.Failed(ImportResult.Reason.Broken)
        val resolver = context.contentResolver
        var name = "Buch"
        var size = -1L
        runCatching {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { c ->
                if (c.moveToFirst()) {
                    c.getString(0)?.let { name = it }
                    if (!c.isNull(1)) size = c.getLong(1)
                }
            }
        }
        if (size > MAX_BYTES) return@withContext ImportResult.Failed(ImportResult.Reason.TooBig)
        books.firstOrNull { it.source == name && size > 0 && it.size == size }?.let { return@withContext ImportResult.Exists(it) }

        val id = System.currentTimeMillis().toString(36) + (0..999).random().toString(36)
        val tmp = File(folder, "$id.part")
        val copied = runCatching {
            resolver.openInputStream(uri)?.use { input ->
                tmp.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024)
                    var total = 0L
                    while (true) {
                        val n = input.read(buf)
                        if (n < 0) break
                        total += n
                        if (total > MAX_BYTES) throw IllegalStateException("too big")
                        out.write(buf, 0, n)
                    }
                }
            } ?: throw IllegalStateException("no stream")
        }
        if (copied.isFailure) {
            tmp.delete()
            val msg = copied.exceptionOrNull()?.message.orEmpty()
            return@withContext ImportResult.Failed(
                when {
                    msg.contains("too big") -> ImportResult.Reason.TooBig
                    msg.contains("ENOSPC") || msg.contains("No space") -> ImportResult.Reason.NoSpace
                    else -> ImportResult.Reason.Broken
                }
            )
        }
        val realSize = tmp.length()
        val format = sniff(tmp, name, resolver.getType(uri))
        if (format == null) {
            tmp.delete()
            return@withContext ImportResult.Failed(ImportResult.Reason.Unsupported)
        }
        val file = File(folder, "$id.${format.id}")
        if (!tmp.renameTo(file)) {
            tmp.delete()
            return@withContext ImportResult.Failed(ImportResult.Reason.Broken)
        }
        val fallbackTitle = name.substringBeforeLast('.').replace('_', ' ').replace(Regex("\\s+"), " ").trim().ifBlank { "Buch" }
        val coverName = "$id.cover.jpg"
        val result: Book = try {
            when (format) {
                BookFormat.Pdf -> {
                    val pages = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                        PdfRenderer(fd).use { r ->
                            if (r.pageCount > 0) runCatching { r.openPage(0).use { page -> writeCover(renderCover(page), File(folder, coverName)) } }
                            r.pageCount
                        }
                    }
                    if (pages <= 0) throw IllegalStateException("empty")
                    // Eingebautes Inhaltsverzeichnis (Kapitel und Unterkapitel), wenn das PDF eins hat
                    val outline = PdfOutline.read(file, pages).orEmpty()
                    Book(
                        id, fallbackTitle, null, format, file.name, coverName.takeIf { File(folder, it).exists() }, name, realSize, System.currentTimeMillis(), pages,
                        chapters = outline, chaptersBy = if (outline.isEmpty()) ChapterSource.None else ChapterSource.Book, outlineTried = true,
                    )
                }
                BookFormat.Epub -> {
                    val epub = Epub.read(file)
                    epub.cover?.let { bytes -> runCatching { decodeCover(bytes)?.let { writeCover(it, File(folder, coverName)) } } }
                    val chapters = epub.toc.map { Chapter(it.title.take(140), it.level.coerceIn(1, 3), it.section, it.block) }
                    Book(
                        id, (epub.title ?: fallbackTitle).take(160), epub.author?.take(120), format, file.name,
                        coverName.takeIf { File(folder, it).exists() }, name, realSize, System.currentTimeMillis(), epub.sections.size,
                        chapters = chapters, chaptersBy = if (chapters.isEmpty()) ChapterSource.None else ChapterSource.Book,
                    )
                }
            }
        } catch (e: SecurityException) {
            file.delete()
            return@withContext ImportResult.Failed(ImportResult.Reason.Password)
        } catch (e: EpubException) {
            file.delete()
            return@withContext ImportResult.Failed(if (e.reason == EpubException.Reason.Drm) ImportResult.Reason.Drm else ImportResult.Reason.Broken)
        } catch (e: Throwable) {
            file.delete()
            File(folder, coverName).delete()
            return@withContext ImportResult.Failed(ImportResult.Reason.Broken)
        }
        if (result.chapters.isNotEmpty()) writeTocNow(result.id, result.chapters)
        withContext(Dispatchers.Main) {
            tocLoaded += result.id
            books.add(result)
            save()
        }
        ImportResult.Added(result)
    }

    private fun sniff(file: File, name: String, mime: String?): BookFormat? {
        val head = ByteArray(64)
        val n = runCatching { file.inputStream().use { it.read(head) } }.getOrDefault(0)
        if (n >= 4 && head[0] == '%'.code.toByte() && head[1] == 'P'.code.toByte() && head[2] == 'D'.code.toByte() && head[3] == 'F'.code.toByte()) return BookFormat.Pdf
        if (n >= 2 && head[0] == 'P'.code.toByte() && head[1] == 'K'.code.toByte()) {
            val isEpub = mime == "application/epub+zip" || name.endsWith(".epub", true) ||
                String(head, Charsets.ISO_8859_1).contains("application/epub+zip")
            if (isEpub) return BookFormat.Epub
        }
        return null
    }

    private fun renderCover(page: PdfRenderer.Page): Bitmap {
        val w = 360
        val h = (w * page.height.toFloat() / page.width.coerceAtLeast(1)).toInt().coerceIn(200, 720)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(Color.WHITE)
        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bmp
    }

    private fun decodeCover(bytes: ByteArray): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= 360) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    }

    private fun writeCover(bmp: Bitmap, target: File) {
        target.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 86, it) }
        bmp.recycle()
    }

    /**
     * Für die KI-Kapitel: der Anfang jeder PDF-Seite als Text. Das kann Android erst ab Version 15
     * (vorher gibt es keine Textabfrage für PDF-Seiten). Gescannte PDFs ohne Text liefern leere Zeilen.
     */
    /**
     * Nummerierte Unterkapitel ("3.2 Momentangeschwindigkeit") aus dem Text eines PDFs (seit 14.0, Android 15+).
     * Ohne KI und ohne Internet: Je Kapitel (Ebene 1) werden Zeilen gesucht, die wie eine Überschrift mit
     * Nummer aussehen. Nur steigende Nummern mit der Kapitelnummer zählen – so fallen Inhaltsverzeichnis,
     * Verweise ("siehe 3.9") und die Wiederholung in der Zusammenfassung am Kapitelende weg.
     * @return gefundene Unterkapitel (Ebene 2) oder null, wenn es nicht geht bzw. nichts Brauchbares da ist.
     */
    fun pdfSubchapters(book: Book): List<Chapter>? {
        if (Build.VERSION.SDK_INT < 35 || book.format != BookFormat.Pdf) return null
        val tops = book.chapters.filter { it.level == 1 }
        if (tops.size < 2) return null
        val ws = Regex("\\s+")
        val pages: List<List<String>> = runCatching {
            ParcelFileDescriptor.open(file(book), ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { r ->
                    List(r.pageCount) { i ->
                        r.openPage(i).use { page ->
                            page.textContents.flatMap { it.text.orEmpty().split('\n', '\r') }
                                .map { it.replace(ws, " ").trim() }
                                .filter { it.length in 4..110 }
                        }
                    }
                }
            }
        }.getOrNull() ?: return null
        val heading = Regex("^(\\d{1,2})\\.(\\d{1,2})\\s+(\\p{Lu}.{2,90})$")
        val out = ArrayList<Chapter>()
        tops.forEachIndexed { i, ch ->
            val from = ch.section.coerceIn(0, pages.size)
            val to = (if (i + 1 < tops.size) tops[i + 1].section else pages.size).coerceIn(from, pages.size)
            // (Hauptnummer, Unternummer, Seite, Titel)
            val found = ArrayList<SubHit>()
            for (pg in from until to) for (line in pages[pg]) {
                val m = heading.find(line) ?: continue
                val title = m.groupValues[3].trim()
                if (!looksLikeHeading(title)) continue
                found += SubHit(m.groupValues[1].toInt(), m.groupValues[2].toInt(), pg, "${m.groupValues[1]}.${m.groupValues[2]} $title")
            }
            val major = chapterNumber(ch.title) ?: found.groupingBy { it.major }.eachCount().maxByOrNull { it.value }?.key ?: return@forEachIndexed
            var last = 0
            for (h in found) {
                if (h.major != major || h.minor <= last || h.minor > last + 3) continue
                out += Chapter(h.title.take(140), 2, h.page)
                last = h.minor
            }
        }
        return out.takeIf { it.size >= 2 }
    }

    private class SubHit(val major: Int, val minor: Int, val page: Int, val title: String)

    /** "3 Bewegung", "Kapitel 3", "Chapter 3: …" → 3 */
    private fun chapterNumber(title: String): Int? =
        Regex("^(?:(?:kapitel|chapter|teil|part)\\s+)?(\\d{1,2})(?:\\b|[.:])", RegexOption.IGNORE_CASE).find(title.trim())?.groupValues?.get(1)?.toIntOrNull()

    private fun looksLikeHeading(t: String): Boolean {
        if (t.length < 3 || t.length > 90) return false
        if (t.last() in ".,;:") return false
        val words = t.split(' ').filter { it.isNotBlank() }
        if (words.size > 12 || words.none { w -> w.count { it.isLetter() } >= 3 }) return false
        val lower = t.lowercase()
        return listOf("figure", "fig.", "table", "abbildung", "abb.", "tabelle", "example", "beispiel", "equation", "gleichung").none { lower.startsWith(it) }
    }

    /**
     * Für die Kapitelsuche ohne KI (seit 15.2): die ersten [lines] Zeilen jeder PDF-Seite, in Lesereihenfolge.
     * Wie alle Textabfragen bei PDFs erst ab Android 15; vorher null.
     */
    fun pdfPageHeads(book: Book, lines: Int): List<List<String>>? {
        if (Build.VERSION.SDK_INT < 35 || book.format != BookFormat.Pdf) return null
        val ws = Regex("\\s+")
        return runCatching {
            ParcelFileDescriptor.open(file(book), ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { r ->
                    List(r.pageCount) { i ->
                        r.openPage(i).use { page ->
                            page.textContents.asSequence()
                                .flatMap { it.text.orEmpty().split('\n', '\r').asSequence() }
                                .map { it.replace(ws, " ").trim() }
                                .filter { it.isNotEmpty() }
                                .take(lines)
                                .toList()
                        }
                    }
                }
            }
        }.getOrNull()
    }

    fun pdfPageStarts(book: Book, perPage: Int): List<String>? {
        if (Build.VERSION.SDK_INT < 35) return null
        return runCatching {
            ParcelFileDescriptor.open(file(book), ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { r ->
                    List(r.pageCount) { i ->
                        r.openPage(i).use { page ->
                            page.textContents.joinToString(" ") { it.text.orEmpty() }
                                .replace(Regex("\\s+"), " ").trim().take(perPage)
                        }
                    }
                }
            }
        }.getOrNull()
    }
}
