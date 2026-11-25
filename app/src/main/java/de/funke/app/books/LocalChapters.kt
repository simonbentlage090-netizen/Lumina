package de.funke.app.books

/**
 * Kapitel ohne KI und ohne Internet (seit 15.2) – aus dem Text des Buchs selbst.
 *
 * Die Lumina-KI gliedert höchstens drei Bücher pro Woche ([ChapterQuota]). Danach, ohne KI, oder wenn die KI
 * gerade nicht erreichbar ist, sucht Lumina die Kapitel hier direkt auf dem Handy.
 *
 * PDF: Ein Kapitel beginnt oben auf einer Seite. Deshalb zählen nur die ersten Zeilen jeder Seite:
 *  - "Kapitel 3", "Chapter 3: …", "Lektion 4", "Teil II" (der Titel darf auch in der nächsten Zeile stehen),
 *  - "3 Bewegung in einer Dimension" (Nummer und Überschrift),
 *  - bekannte Abschnitte wie "Vorwort" und "Einleitung" (vor dem ersten Kapitel) oder "Anhang",
 *    "Lösungen" und "Register" (nach dem letzten).
 * Damit nichts Falsches hineinrutscht:
 *  - Seiten, die wie ein Inhaltsverzeichnis aussehen (viele Zeilen mit Seitenzahl am Ende), fallen weg.
 *  - Nummern müssen der Reihe nach steigen (höchstens eine ausgelassen), jede zählt nur einmal. So bleiben
 *    Kopfzeilen, die auf jeder Seite des Kapitels wiederkommen, beim ersten Mal stehen.
 *  - Zahlen im immer gleichen Abstand zur Seitenzahl sind Seitenzahlen in der Kopfzeile, keine Kapitel.
 * EPUB: die Überschriften im Text (H1 bis H3), sonst kurze Anfänge der Abschnitte.
 *
 * Lieber kein Inhaltsverzeichnis als ein falsches: Unter zwei Kapiteln kommt nichts heraus.
 * Reine Logik ohne Android, damit sie sich auf dem Rechner prüfen lässt.
 */
object LocalChapters {
    /** So viele Zeilen oben auf einer Seite können eine Kapitelüberschrift sein. */
    private const val HEAD_LINES = 4

    private val PartWords = setOf("teil", "part", "buch", "book")
    private val WordNumbered = Regex(
        "(kapitel|chapter|lektion|lesson|abschnitt|section|einheit|unit|teil|part|buch|book)\\s+(\\d{1,3}|[ivxlc]{1,7})(?:\\s*[.:–—-])?(?:\\s+(.*))?",
        RegexOption.IGNORE_CASE,
    )
    private val Numbered = Regex("(\\d{1,2})\\.?\\s+(\\p{Lu}.{1,88})")
    private val TocHead = Regex("(inhalt|inhaltsverzeichnis|contents|table of contents)", RegexOption.IGNORE_CASE)
    private val EndsWithPage = Regex(".*\\D\\s?\\d{1,4}")
    private val Space = Regex("\\s+")

    private val Front = setOf(
        "vorwort", "geleitwort", "einleitung", "einführung", "prolog", "zur einführung",
        "preface", "foreword", "introduction", "prologue",
    )
    private val Back = setOf(
        "nachwort", "epilog", "anhang", "glossar", "literatur", "literaturverzeichnis", "quellen", "quellenverzeichnis",
        "register", "stichwortverzeichnis", "sachregister", "index", "danksagung", "lösungen", "anmerkungen",
        "afterword", "epilogue", "appendix", "glossary", "bibliography", "references", "acknowledgments",
        "acknowledgements", "solutions", "answers", "notes",
    )

    private enum class Kind { Part, Chapter, Named }

    private class Hit(val page: Int, val kind: Kind, val number: Int, val title: String, val fromPageNumber: Boolean = false)

    /**
     * [pages]: je Seite die ersten Zeilen in Lesereihenfolge (gern 12, oben zählen davon nur [HEAD_LINES]).
     * Ergebnis: Kapitel mit [Chapter.section] = Seite (ab 0).
     */
    fun fromPdf(pages: List<List<String>>): List<Chapter> {
        val clean = pages.map { lines -> lines.map { it.replace(Space, " ").trim() }.filter { it.isNotEmpty() } }
        val candidates = ArrayList<Hit>()
        clean.forEachIndexed { page, lines ->
            val head = lines.filter { !isPageNumber(it) }.take(HEAD_LINES)
            if (looksLikeToc(lines)) {
                // Register, Literatur & Co. sehen selbst wie Listen mit Seitenzahlen aus – ihre Überschrift zählt trotzdem
                head.firstOrNull()?.let { first ->
                    val key = first.lowercase().trimEnd(':').trim()
                    if (key in Back) candidates += Hit(page, Kind.Named, 0, first.trimEnd(':'))
                }
                return@forEachIndexed
            }
            head.forEachIndexed { i, line -> hitIn(line, head.getOrNull(i + 1), page)?.let { candidates += it } }
        }
        // Seitenzahlen in der Kopfzeile: gleicher Abstand zwischen Zahl und Seite bei mehreren Treffern
        val offsets = candidates.filter { it.kind == Kind.Chapter && it.fromPageNumber }
            .groupingBy { it.number - (it.page + 1) }.eachCount()
        val pageLike = offsets.filterValues { it >= 3 }.keys
        val hits = candidates.filterNot { it.kind == Kind.Chapter && it.fromPageNumber && (it.number - (it.page + 1)) in pageLike }

        // Teile und Kapitel der Reihe nach; Kapitel dürfen nach einem neuen Teil wieder bei 1 anfangen
        val accepted = ArrayList<Hit>()
        var lastPart = 0
        var lastChapter = 0
        var afterPart = false
        val usedPages = HashSet<Int>()
        for (h in hits.filter { it.kind != Kind.Named }.sortedBy { it.page }) {
            if (h.page in usedPages) continue
            when (h.kind) {
                Kind.Part -> if (h.number in (lastPart + 1)..(lastPart + 2)) {
                    accepted += h
                    usedPages += h.page
                    lastPart = h.number
                    afterPart = true
                }
                Kind.Chapter -> if (h.number in (lastChapter + 1)..(lastChapter + 2) || (afterPart && h.number in 1..2 && h.number <= lastChapter)) {
                    accepted += h
                    usedPages += h.page
                    lastChapter = h.number
                    afterPart = false
                }
                Kind.Named -> Unit
            }
        }
        val numbered = accepted.count { it.kind == Kind.Chapter } + accepted.count { it.kind == Kind.Part }
        if (numbered < 2) return emptyList()

        // Vorwort & Co. nur vor dem ersten, Anhang & Co. nur nach dem letzten nummerierten Kapitel; jeder Name einmal
        val first = accepted.minOf { it.page }
        val last = accepted.maxOf { it.page }
        val names = HashSet<String>()
        for (h in hits.filter { it.kind == Kind.Named }.sortedBy { it.page }) {
            val key = h.title.lowercase().trimEnd(':')
            val ok = (key in Front && h.page < first) || (key in Back && h.page > last)
            if (ok && h.page !in usedPages && names.add(key)) {
                accepted += h
                usedPages += h.page
            }
        }

        val hasParts = accepted.any { it.kind == Kind.Part }
        return accepted.sortedBy { it.page }.map { h ->
            val level = if (hasParts && h.kind == Kind.Chapter) 2 else 1
            Chapter(h.title.take(140), level, h.page)
        }
    }

    /** Eine Zeile oben auf der Seite als Überschrift lesen (und bei "Kapitel 3" ohne Titel die nächste Zeile dazu). */
    private fun hitIn(line: String, next: String?, page: Int): Hit? {
        WordNumbered.matchEntire(line)?.let { m ->
            val word = m.groupValues[1]
            val raw = m.groupValues[2]
            val number = raw.toIntOrNull() ?: roman(raw) ?: return null
            if (number <= 0) return null
            val rest = m.groupValues[3].trim()
            if (rest.isNotEmpty() && !looksLikeHeading(rest)) return null
            val title = when {
                rest.isNotEmpty() -> line
                next != null && looksLikeHeading(next) && WordNumbered.matchEntire(next) == null && Numbered.matchEntire(next) == null -> "$line $next"
                else -> line
            }
            return Hit(page, if (word.lowercase() in PartWords) Kind.Part else Kind.Chapter, number, title)
        }
        Numbered.matchEntire(line)?.let { m ->
            val number = m.groupValues[1].toIntOrNull() ?: return null
            if (number <= 0) return null
            val title = m.groupValues[2].trim()
            if (!looksLikeHeading(title) || title.split(' ').size > 10) return null
            return Hit(page, Kind.Chapter, number, line, fromPageNumber = true)
        }
        val key = line.lowercase().trimEnd(':').trim()
        if (key in Front || key in Back) return Hit(page, Kind.Named, 0, line.trimEnd(':'))
        return null
    }

    private fun isPageNumber(line: String) = line.length <= 4 && line.all { it.isDigit() || it in "ivxlcIVXLC" }

    /** Inhaltsverzeichnis oder Register: Überschrift "Inhalt" oben oder viele Zeilen mit einer Seitenzahl am Ende. */
    private fun looksLikeToc(lines: List<String>): Boolean {
        if (lines.take(3).any { TocHead.matchEntire(it.trimEnd(':')) != null }) return true
        val sample = lines.take(12)
        val withPage = sample.count { EndsWithPage.matchEntire(it) != null || it.contains("....") || it.contains(". . .") }
        return withPage >= 4
    }

    /** Wie die Unterkapitel-Suche: kurze Zeile ohne Satzzeichen am Ende, mit echten Wörtern, keine Abbildung. */
    fun looksLikeHeading(t: String): Boolean {
        if (t.length < 3 || t.length > 90) return false
        if (t.last() in ".,;") return false
        val words = t.split(' ').filter { it.isNotBlank() }
        if (words.size > 12 || words.none { w -> w.count { it.isLetter() } >= 3 }) return false
        val lower = t.lowercase()
        return listOf("figure", "fig.", "table", "abbildung", "abb.", "tabelle", "example", "beispiel", "equation", "gleichung", "siehe", "see ")
            .none { lower.startsWith(it) }
    }

    /** Römische Zahl (I bis CCC) oder null. */
    fun roman(s: String): Int? {
        val v = mapOf('i' to 1, 'v' to 5, 'x' to 10, 'l' to 50, 'c' to 100)
        val t = s.lowercase()
        if (t.isEmpty() || t.any { it !in v }) return null
        var total = 0
        for (i in t.indices) {
            val cur = v.getValue(t[i])
            val nxt = if (i + 1 < t.length) v.getValue(t[i + 1]) else 0
            total += if (cur < nxt) -cur else cur
        }
        return total.takeIf { it in 1..300 }
    }

    /**
     * EPUB ohne eigenes Inhaltsverzeichnis: die Überschriften im Text. Gibt es kein H1, rückt H2 auf Ebene 1.
     * Ganz ohne Überschriften: kurze erste Zeilen der Abschnitte. Der Buchtitel selbst zählt nicht.
     */
    fun fromEpub(book: EpubBook): List<Chapter> {
        val skip = book.title?.trim()?.lowercase()
        val found = ArrayList<Chapter>()
        book.sections.forEachIndexed { s, sec ->
            sec.blocks.forEachIndexed { b, block ->
                val level = when (block.kind) {
                    BlockKind.H1 -> 1
                    BlockKind.H2 -> 2
                    BlockKind.H3 -> 3
                    else -> 0
                }
                if (level > 0) {
                    val t = block.text.replace(Space, " ").trim()
                    if (t.length in 2..140 && t.lowercase() != skip) found += Chapter(t, level, s, b)
                }
            }
        }
        var list: List<Chapter> = found.distinctBy { Triple(it.section, it.block, it.title) }
        if (list.isEmpty()) {
            list = book.sections.mapIndexedNotNull { s, sec ->
                val t = sec.blocks.firstOrNull()?.text?.replace(Space, " ")?.trim() ?: return@mapIndexedNotNull null
                if (t.length <= 70 && looksLikeHeading(t) && t.lowercase() != skip) Chapter(t, 1, s, 0) else null
            }
        }
        if (list.isEmpty()) return emptyList()
        val shift = list.minOf { it.level } - 1
        list = list.map { it.copy(level = (it.level - shift).coerceIn(1, 3)) }
        // Sehr lange Listen: erst die dritte, dann die zweite Ebene weglassen
        if (list.size > 400) list = list.filter { it.level <= 2 }
        if (list.size > 400) list = list.filter { it.level == 1 }
        // Gleiche Überschrift direkt hintereinander (z. B. Kopf jedes Abschnitts) nur einmal
        list = list.filterIndexed { i, c -> i == 0 || list[i - 1].title != c.title || list[i - 1].level != c.level }
        list = list.mapIndexed { i, c -> if (i == 0 && c.level > 1) c.copy(level = 1) else c }
        return if (list.count { it.level == 1 } >= 2) list else emptyList()
    }
}
