package de.funke.app.books

import java.io.File
import java.net.URLDecoder
import java.util.zip.ZipFile

/**
 * Liest EPUB-Dateien (Version 2 und 3) ohne fremde Bibliothek.
 *
 * Ein EPUB ist ein ZIP mit XHTML-Kapiteln. Hier wird nur der Text gebraucht: Überschriften, Absätze,
 * Zitate und Aufzählungen. Bilder, Tabellen-Layout und eigene Schriften des Verlags fallen weg,
 * damit sich jedes Buch gleich ruhig liest. Der Parser ist absichtlich nachsichtig: Viele EPUBs
 * sind kein sauberes XML (z. B. "&nbsp;" ohne Deklaration), deshalb keine XML-Bibliothek.
 */
enum class BlockKind { H1, H2, H3, P, Quote, Item }

class Block(val kind: BlockKind, val text: String)

/** Ein Abschnitt in Lesereihenfolge (ein Eintrag im "spine"). [anchors]: id im XHTML → Block-Nummer. */
class EpubSection(val path: String, val blocks: List<Block>, val anchors: Map<String, Int>)

/** Ein Eintrag im Inhaltsverzeichnis. [level] beginnt bei 1. */
class TocEntry(val title: String, val level: Int, val section: Int, val block: Int)

class EpubBook(
    val title: String?,
    val author: String?,
    val sections: List<EpubSection>,
    val toc: List<TocEntry>,
    val cover: ByteArray?,
)

class EpubException(val reason: Reason) : Exception(reason.name) {
    enum class Reason { NotEpub, Drm, Empty }
}

object Epub {

    fun read(file: File, withCover: Boolean = true): EpubBook = ZipFile(file).use { zip ->
        fun text(path: String): String? {
            val e = zip.getEntry(path) ?: zip.entries().asSequence().firstOrNull { it.name.equals(path, ignoreCase = true) } ?: return null
            return zip.getInputStream(e).use { it.readBytes() }.toString(Charsets.UTF_8)
        }
        fun bytes(path: String, limit: Int): ByteArray? {
            val e = zip.getEntry(path) ?: return null
            if (e.size > limit) return null
            return zip.getInputStream(e).use { it.readBytes() }
        }

        val container = text("META-INF/container.xml") ?: throw EpubException(EpubException.Reason.NotEpub)
        val opfPath = Regex("full-path\\s*=\\s*[\"']([^\"']+)[\"']").find(container)?.groupValues?.get(1)
            ?: throw EpubException(EpubException.Reason.NotEpub)
        val opf = text(opfPath) ?: throw EpubException(EpubException.Reason.NotEpub)
        val base = opfPath.substringBeforeLast('/', "")

        // Kopierschutz: verschlüsselte Inhalte lassen sich nicht lesen (Schriften dürfen verschleiert sein)
        text("META-INF/encryption.xml")?.let { enc ->
            val encrypted = Regex("URI\\s*=\\s*[\"']([^\"']+)[\"']").findAll(enc).map { it.groupValues[1].lowercase() }
            if (encrypted.any { it.endsWith(".xhtml") || it.endsWith(".html") || it.endsWith(".htm") || it.endsWith(".xml") }) {
                throw EpubException(EpubException.Reason.Drm)
            }
        }

        val title = tagText(opf, "dc:title") ?: tagText(opf, "title")
        val author = tagText(opf, "dc:creator") ?: tagText(opf, "creator")

        class Item(val id: String, val href: String, val type: String, val props: String)
        val items = Regex("<(?:\\w+:)?item\\b[^>]*>", RegexOption.IGNORE_CASE).findAll(opf).map { m ->
            val a = attrs(m.value)
            Item(a["id"].orEmpty(), resolve(base, a["href"].orEmpty()), a["media-type"].orEmpty(), a["properties"].orEmpty())
        }.filter { it.href.isNotEmpty() }.toList()
        val byId = items.associateBy { it.id }

        val spineTag = Regex("<(?:\\w+:)?spine\\b[^>]*>", RegexOption.IGNORE_CASE).find(opf)?.value.orEmpty()
        val ncxId = attrs(spineTag)["toc"]
        val spine = Regex("<(?:\\w+:)?itemref\\b[^>]*>", RegexOption.IGNORE_CASE).findAll(opf)
            .mapNotNull { byId[attrs(it.value)["idref"]] }
            .filter { it.type.contains("html") || it.href.endsWith(".xhtml") || it.href.endsWith(".html") || it.href.endsWith(".htm") }
            .toList()

        val sections = ArrayList<EpubSection>()
        for (item in spine) {
            val html = text(item.href) ?: continue
            val parsed = Html.parse(html)
            if (parsed.blocks.isEmpty()) continue
            sections += EpubSection(item.href, parsed.blocks, parsed.anchors)
        }
        if (sections.isEmpty()) throw EpubException(EpubException.Reason.Empty)
        val sectionOf = HashMap<String, Int>()
        sections.forEachIndexed { i, s -> sectionOf[s.path] = i }

        fun target(dir: String, href: String): Pair<Int, Int>? {
            val path = resolve(dir, href)
            val fragment = href.substringAfter('#', "").let { runCatching { URLDecoder.decode(it, "UTF-8") }.getOrDefault(it) }
            val s = sectionOf[path] ?: return null
            return s to (sections[s].anchors[fragment] ?: 0)
        }

        // Inhaltsverzeichnis: EPUB 3 (nav.xhtml), sonst EPUB 2 (toc.ncx)
        var toc: List<TocEntry> = emptyList()
        items.firstOrNull { it.props.split(' ').contains("nav") }?.let { nav ->
            val html = text(nav.href) ?: return@let
            toc = Toc.fromNav(html).mapNotNull { (label, level, href) ->
                target(nav.href.substringBeforeLast('/', ""), href)?.let { (s, b) -> TocEntry(label, level, s, b) }
            }
        }
        if (toc.isEmpty()) {
            val ncx = (ncxId?.let { byId[it] } ?: items.firstOrNull { it.type.contains("ncx") || it.href.endsWith(".ncx") })
            ncx?.let { item ->
                val xml = text(item.href) ?: return@let
                toc = Toc.fromNcx(xml).mapNotNull { (label, level, href) ->
                    target(item.href.substringBeforeLast('/', ""), href)?.let { (s, b) -> TocEntry(label, level, s, b) }
                }
            }
        }
        toc = toc.filter { it.title.isNotBlank() }.distinctBy { Triple(it.title, it.section, it.block) }

        var cover: ByteArray? = null
        if (withCover) {
            val coverMetaId = Regex("<meta\\b[^>]*>", RegexOption.IGNORE_CASE).findAll(opf).map { attrs(it.value) }
                .firstOrNull { it["name"] == "cover" }?.get("content")
            val coverItem = items.firstOrNull { it.props.split(' ').contains("cover-image") }
                ?: coverMetaId?.let { byId[it] }?.takeIf { it.type.startsWith("image") }
                ?: items.firstOrNull { it.type.startsWith("image") && (it.id.contains("cover", true) || it.href.contains("cover", true)) }
            cover = coverItem?.let { bytes(it.href, 8 * 1024 * 1024) }
        }

        EpubBook(title?.takeIf { it.isNotBlank() }, author?.takeIf { it.isNotBlank() }, sections, toc, cover)
    }

    // ----- kleine Helfer -----

    internal fun tagText(xml: String, tag: String): String? {
        val m = Regex("<$tag\\b[^>]*>(.*?)</$tag>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(xml) ?: return null
        return Html.entities(m.groupValues[1].replace(Regex("<[^>]+>"), "")).replace('\u00A0', ' ').replace(Regex("\\s+"), " ").trim()
    }

    internal fun attrs(tag: String): Map<String, String> {
        val out = HashMap<String, String>()
        Regex("([A-Za-z_][\\w:.-]*)\\s*=\\s*(\"([^\"]*)\"|'([^']*)')").findAll(tag).forEach { m ->
            val key = m.groupValues[1].lowercase()
            val value = m.groupValues[3].ifEmpty { m.groupValues[4] }
            out[key] = Html.entities(value)
            // "epub:type" auch als "type" nicht überschreiben, nur zusätzlich ablegen
            if (key.contains(':')) out.putIfAbsent(key.substringAfter(':'), Html.entities(value))
        }
        return out
    }

    /** Relativen Pfad (mit "../", %20, #anker) auf einen Pfad im ZIP abbilden. */
    internal fun resolve(dir: String, href: String): String {
        val clean = href.substringBefore('#').substringBefore('?')
        if (clean.isEmpty()) return ""
        val decoded = runCatching { URLDecoder.decode(clean.replace("+", "%2B"), "UTF-8") }.getOrDefault(clean)
        val parts = ArrayList<String>()
        if (!decoded.startsWith("/") && dir.isNotEmpty()) parts.addAll(dir.split('/'))
        for (seg in decoded.trimStart('/').split('/')) {
            when (seg) {
                "", "." -> {}
                ".." -> if (parts.isNotEmpty()) parts.removeAt(parts.size - 1)
                else -> parts += seg
            }
        }
        return parts.joinToString("/")
    }
}

/** Inhaltsverzeichnis aus nav.xhtml oder toc.ncx: Liste aus (Titel, Ebene, href). */
internal object Toc {

    fun fromNav(html: String): List<Triple<String, Int, String>> {
        // Den <nav epub:type="toc"> suchen, sonst den ersten <nav>
        val navs = Regex("<nav\\b([^>]*)>(.*?)</nav>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).findAll(html).toList()
        val nav = navs.firstOrNull { it.groupValues[1].contains("toc") } ?: navs.firstOrNull() ?: return emptyList()
        val body = nav.groupValues[2]
        val out = ArrayList<Triple<String, Int, String>>()
        var depth = 0
        var href: String? = null
        val label = StringBuilder()
        var i = 0
        while (i < body.length) {
            val lt = body.indexOf('<', i)
            if (lt < 0) break
            if (href != null) label.append(body, i, lt)
            val gt = body.indexOf('>', lt)
            if (gt < 0) break
            val tag = body.substring(lt + 1, gt)
            val name = tag.trimStart('/').takeWhile { !it.isWhitespace() && it != '/' }.lowercase()
            val closing = tag.startsWith("/")
            when (name) {
                "ol", "ul" -> depth += if (closing) -1 else 1
                "a" -> if (!closing) {
                    href = Epub.attrs(tag)["href"]
                    label.setLength(0)
                } else {
                    val h = href
                    val t = Html.entities(label.toString()).replace('\u00A0', ' ').replace(Regex("\\s+"), " ").trim()
                    if (h != null && t.isNotEmpty()) out += Triple(t, depth.coerceAtLeast(1), h)
                    href = null
                }
            }
            i = gt + 1
        }
        return out
    }

    fun fromNcx(xml: String): List<Triple<String, Int, String>> {
        val out = ArrayList<Triple<String, Int, String>>()
        val map = Regex("<navMap\\b[^>]*>(.*?)</navMap>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(xml)?.groupValues?.get(1) ?: return out
        var depth = 0
        var label: String? = null
        val tagRe = Regex("<(/?)(navPoint|text|content)\\b([^>]*)>", RegexOption.IGNORE_CASE)
        var pos = 0
        var textStart = -1
        while (true) {
            val m = tagRe.find(map, pos) ?: break
            val closing = m.groupValues[1] == "/"
            when (m.groupValues[2].lowercase()) {
                "navpoint" -> if (closing) depth-- else { depth++; label = null }
                "text" -> if (!closing) textStart = m.range.last + 1 else if (textStart >= 0) {
                    label = Html.entities(map.substring(textStart, m.range.first).replace(Regex("<[^>]+>"), "")).replace('\u00A0', ' ').replace(Regex("\\s+"), " ").trim()
                    textStart = -1
                }
                "content" -> if (!closing) {
                    val src = Epub.attrs(m.value)["src"]
                    val l = label
                    if (src != null && !l.isNullOrEmpty()) out += Triple(l, depth.coerceAtLeast(1), src)
                    label = null
                }
            }
            pos = m.range.last + 1
        }
        return out
    }
}

/** Nachsichtiger HTML-zu-Text-Wandler für Buchkapitel. */
internal object Html {

    class Parsed(val blocks: List<Block>, val anchors: Map<String, Int>)

    private val skip = setOf("head", "style", "script", "svg", "math", "nav", "title")
    private val blockTags = setOf(
        "p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "li", "blockquote", "section", "article", "header",
        "footer", "figure", "figcaption", "pre", "dt", "dd", "tr", "table", "hr", "aside", "ul", "ol", "body",
    )

    fun parse(html: String): Parsed {
        val blocks = ArrayList<Block>()
        val anchors = HashMap<String, Int>()
        val buf = StringBuilder()
        val kinds = ArrayList<BlockKind>()
        var quoteDepth = 0

        fun currentKind(): BlockKind = kinds.lastOrNull() ?: if (quoteDepth > 0) BlockKind.Quote else BlockKind.P
        fun flush() {
            val t = buf.toString()
                .replace(Regex("[ \\t\\r\\f\\u000B]+"), " ")
                .replace(Regex(" *\\n *"), "\n")
                .replace(Regex("\\n{2,}"), "\n")
                .trim()
            buf.setLength(0)
            if (t.isNotEmpty()) blocks += Block(currentKind(), t)
        }

        var i = run {
            val b = Regex("<body\\b[^>]*>", RegexOption.IGNORE_CASE).find(html)
            b?.range?.last?.plus(1) ?: 0
        }
        while (i < html.length) {
            val lt = html.indexOf('<', i)
            if (lt < 0) {
                buf.append(entities(html.substring(i)).replace('\n', ' '))
                break
            }
            if (lt > i) buf.append(entities(html.substring(i, lt)).replace('\n', ' '))
            if (html.startsWith("<!--", lt)) {
                val end = html.indexOf("-->", lt + 4)
                i = if (end < 0) html.length else end + 3
                continue
            }
            if (html.startsWith("<![CDATA[", lt)) {
                val end = html.indexOf("]]>", lt)
                if (end < 0) break
                buf.append(html, lt + 9, end)
                i = end + 3
                continue
            }
            val gt = html.indexOf('>', lt)
            if (gt < 0) break
            val tag = html.substring(lt + 1, gt)
            i = gt + 1
            if (tag.startsWith("!") || tag.startsWith("?")) continue
            val closing = tag.startsWith("/")
            val name = tag.trimStart('/').takeWhile { !it.isWhitespace() && it != '/' }.lowercase().substringAfter(':')
            val selfClosing = tag.endsWith("/")

            if (!closing && name in skip && !selfClosing) {
                val end = Regex("</(?:\\w+:)?$name\\s*>", RegexOption.IGNORE_CASE).find(html, i)
                i = end?.range?.last?.plus(1) ?: html.length
                continue
            }
            if (!closing) {
                val id = Regex("\\bid\\s*=\\s*[\"']([^\"']+)[\"']").find(tag)?.groupValues?.get(1)
                if (id != null) anchors.putIfAbsent(id, blocks.size)
            }
            when {
                name == "br" -> buf.append('\n')
                name == "body" && closing -> break
                name in blockTags -> {
                    flush()
                    if (name == "blockquote") quoteDepth = (quoteDepth + if (closing) -1 else 1).coerceAtLeast(0)
                    val kind = when (name) {
                        "h1" -> BlockKind.H1
                        "h2" -> BlockKind.H2
                        "h3", "h4", "h5", "h6" -> BlockKind.H3
                        "li", "dd" -> BlockKind.Item
                        else -> null
                    }
                    if (kind != null) {
                        if (closing) { if (kinds.isNotEmpty()) kinds.removeAt(kinds.size - 1) } else if (!selfClosing) kinds += kind
                    }
                }
            }
        }
        flush()
        // Anker hinter dem letzten Block auf den letzten Block legen
        val last = (blocks.size - 1).coerceAtLeast(0)
        val fixed = anchors.mapValues { it.value.coerceAtMost(last) }
        return Parsed(blocks, fixed)
    }

    private val named = mapOf(
        "nbsp" to "\u00A0", "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'",
        "mdash" to "—", "ndash" to "–", "hellip" to "…", "lsquo" to "‘", "rsquo" to "’", "sbquo" to "‚",
        "ldquo" to "“", "rdquo" to "”", "bdquo" to "„", "laquo" to "«", "raquo" to "»", "lsaquo" to "‹", "rsaquo" to "›",
        "shy" to "", "copy" to "©", "reg" to "®", "trade" to "™", "deg" to "°", "middot" to "·", "bull" to "•",
        "times" to "×", "divide" to "÷", "euro" to "€", "pound" to "£", "sect" to "§", "para" to "¶",
        "auml" to "ä", "ouml" to "ö", "uuml" to "ü", "Auml" to "Ä", "Ouml" to "Ö", "Uuml" to "Ü", "szlig" to "ß",
        "eacute" to "é", "egrave" to "è", "ecirc" to "ê", "aacute" to "á", "agrave" to "à", "acirc" to "â",
        "oacute" to "ó", "ograve" to "ò", "ocirc" to "ô", "uacute" to "ú", "ugrave" to "ù", "iacute" to "í",
        "ccedil" to "ç", "ntilde" to "ñ", "Eacute" to "É", "thinsp" to "\u2009", "ensp" to "\u2002", "emsp" to "\u2003",
        "zwnj" to "", "zwj" to "", "minus" to "−", "plusmn" to "±", "frac12" to "½", "frac14" to "¼", "frac34" to "¾",
        "sup2" to "²", "sup3" to "³", "micro" to "µ", "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ",
        "pi" to "π", "lambda" to "λ", "mu" to "μ", "sigma" to "σ", "omega" to "ω", "Omega" to "Ω", "Delta" to "Δ",
        "rarr" to "→", "larr" to "←", "harr" to "↔", "le" to "≤", "ge" to "≥", "ne" to "≠", "asymp" to "≈", "infin" to "∞",
    )

    fun entities(s: String): String {
        if (s.indexOf('&') < 0) return s
        val out = StringBuilder(s.length)
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '&') {
                val semi = s.indexOf(';', i + 1)
                if (semi in (i + 2)..(i + 12)) {
                    val name = s.substring(i + 1, semi)
                    val rep = when {
                        name.startsWith("#x") || name.startsWith("#X") -> name.substring(2).toIntOrNull(16)?.let { codePoint(it) }
                        name.startsWith("#") -> name.substring(1).toIntOrNull()?.let { codePoint(it) }
                        else -> named[name]
                    }
                    if (rep != null) {
                        out.append(rep)
                        i = semi + 1
                        continue
                    }
                }
            }
            out.append(c)
            i++
        }
        return out.toString()
    }

    private fun codePoint(cp: Int): String? =
        if (cp in 1..0x10FFFF && cp !in 0xD800..0xDFFF) String(Character.toChars(cp)).let { if (it == "\u00AD") "" else it } else null
}
