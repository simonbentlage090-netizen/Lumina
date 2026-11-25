package de.funke.app.books

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.zip.Inflater
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Das eingebaute Inhaltsverzeichnis einer PDF-Datei lesen ("Lesezeichen", in PDF "Outline") – seit 15.0.
 *
 * Android selbst kann das nicht: PdfRenderer zeigt nur Seiten. Viele Bücher, Skripte und Lehrbücher haben aber
 * ein Inhaltsverzeichnis mit Kapiteln und Unterkapiteln eingebaut. Dieser kleine Leser holt genau das heraus –
 * ohne Internet, ohne KI, auf jedem Android:
 *  - Querverweise klassisch oder als Stream, auch mehrere Fassungen hintereinander (nachträglich geänderte PDFs)
 *    und beides gemischt. Sind sie kaputt, wird die Datei nach Objekten durchsucht (wie es PDF-Betrachter tun).
 *  - Objekte in komprimierten Objekt-Streams (Flate, auch mit PNG-Prädiktoren).
 *  - Seitenbaum, Ziele direkt, benannt (alt als Wörterbuch, neu als Namensbaum) oder über eine GoTo-Aktion.
 *  - Verschlüsselte PDFs mit leerem Kennwort (häufig nur "Kopierschutz"): RC4 und AES (128 und 256 Bit).
 *    Braucht ein PDF ein echtes Kennwort zum Öffnen, kann es die App ohnehin nicht anzeigen.
 * Geht irgendetwas nicht, kommt null zurück – dann bleibt alles wie bisher (Kapitel von Lumi, falls gewünscht).
 */
object PdfOutline {

    /** Höchstens so viele Einträge – das Inhaltsverzeichnis wird mit dem Buch gespeichert. */
    private const val MAX_ENTRIES = 1200

    /**
     * @param pages Seitenzahl laut PdfRenderer (Seiten außerhalb werden eingegrenzt)
     * @return Einträge in Buchreihenfolge (Ebene 1–3, section = Seite ab 0) oder null
     */
    fun read(file: File, pages: Int): List<Chapter>? = runCatching {
        RandomAccessFile(file, "r").use { raf -> PdfFile(raf).outline(pages) }
    }.getOrNull()

    /** Bereinigen: Seiten eingrenzen, zu lange Verzeichnisse kürzen, Unbrauchbares verwerfen. */
    internal fun tidy(raw: List<Chapter>, pages: Int): List<Chapter>? {
        if (pages <= 0) return null
        // Einträge ohne eigenes Ziel bekommen die Seite des nächsten Eintrags (meist ihr erstes Unterkapitel)
        val fixed = ArrayList<Chapter>(raw.size)
        for (i in raw.indices) {
            val c = raw[i]
            if (c.section >= 0) { fixed += c; continue }
            val next = (i + 1 until raw.size).firstOrNull { raw[it].section >= 0 } ?: continue
            fixed += c.copy(section = raw[next].section)
        }
        var list: List<Chapter> = fixed.map { it.copy(section = it.section.coerceIn(0, pages - 1)) }
        if (list.size > MAX_ENTRIES) list = list.filter { it.level <= 2 }
        if (list.size > MAX_ENTRIES) list = list.filter { it.level <= 1 }
        if (list.size > MAX_ENTRIES) list = list.take(MAX_ENTRIES)
        if (list.size < 2) return null
        // Alles auf derselben Seite? Dann sind die Ziele kaputt – lieber nichts als ein falsches Verzeichnis.
        if (list.size >= 3 && list.all { it.section == list[0].section }) return null
        return list
    }
}

// =============================================================================================
// Bausteine
// =============================================================================================

private class BadPdf(msg: String) : Exception(msg)

private data class Ref(val num: Int, val gen: Int)
private data class Name(val v: String)
private data class Kw(val v: String)
private class PStr(val bytes: ByteArray)

/** Ein Stream: Wörterbuch und wo die Daten in der Datei beginnen (Streams stehen nie in Objekt-Streams). */
private class PStream(val dict: Map<String, Any?>, val dataStart: Long, val num: Int, val gen: Int)

private fun isWs(c: Int) = c == 0x20 || c == 0x0A || c == 0x0D || c == 0x09 || c == 0x0C || c == 0x00
private fun isDelim(c: Int) = c == 0x28 || c == 0x29 || c == 0x3C || c == 0x3E || c == 0x5B || c == 0x5D ||
    c == 0x7B || c == 0x7D || c == 0x2F || c == 0x25
private fun isDigit(c: Int) = c in 0x30..0x39

private fun Any?.asInt(): Int? = (this as? Long)?.let { if (it in Int.MIN_VALUE..Int.MAX_VALUE) it.toInt() else null }
    ?: (this as? Double)?.toInt()

@Suppress("UNCHECKED_CAST")
private fun Any?.asDict(): Map<String, Any?>? = this as? Map<String, Any?>

/**
 * Zerlegt PDF-Syntax aus einem Ausschnitt [b]. [complete] = der Ausschnitt reicht bis zum Ende der Daten; sonst
 * heißt "Ende erreicht" nur "mehr lesen" (dann fliegt [Eof] und der Aufrufer holt einen größeren Ausschnitt).
 */
private class Lexer(val b: ByteArray, var p: Int, val complete: Boolean) {
    class Eof : Exception()

    private val end = b.size

    fun peek(): Int = if (p < end) b[p].toInt() and 0xFF else if (complete) -1 else throw Eof()
    fun at(k: Int): Int = if (p + k < end) b[p + k].toInt() and 0xFF else if (complete) -1 else throw Eof()
    fun next(): Int {
        val c = peek()
        if (c < 0) throw BadPdf("end")
        p++
        return c
    }

    fun skipWs() {
        while (true) {
            val c = peek()
            if (c < 0) return
            if (isWs(c)) p++
            else if (c == 0x25) { // Kommentar bis Zeilenende
                while (true) {
                    val d = peek()
                    if (d < 0 || d == 0x0A || d == 0x0D) break
                    p++
                }
            } else return
        }
    }

    fun startsWith(word: String): Boolean {
        for (i in word.indices) if (at(i) != word[i].code) return false
        return true
    }

    fun obj(depth: Int = 0): Any? {
        if (depth > 48) throw BadPdf("deep")
        skipWs()
        val c = peek()
        return when {
            c < 0 -> throw BadPdf("end")
            c == 0x2F -> name()
            c == 0x28 -> literal()
            c == 0x3C -> if (at(1) == 0x3C) dict(depth) else hex()
            c == 0x5B -> array(depth)
            c == 0x2B || c == 0x2D || c == 0x2E || isDigit(c) -> numberOrRef()
            else -> keyword()
        }
    }

    fun number(): Any {
        val start = p
        if (peek() == 0x2B || peek() == 0x2D) p++
        var dot = false
        while (true) {
            val c = peek()
            if (isDigit(c)) p++
            else if (c == 0x2E && !dot) { dot = true; p++ }
            else break
        }
        val s = String(b, start, p - start, Charsets.ISO_8859_1)
        if (!dot) s.toLongOrNull()?.let { return it }
        return s.toDoubleOrNull() ?: 0.0
    }

    private fun numberOrRef(): Any {
        val n = number()
        if (n is Long && n in 0..Int.MAX_VALUE) {
            val save = p
            skipWs()
            if (isDigit(peek())) {
                val g = number()
                skipWs()
                if (g is Long && peek() == 0x52) {
                    val after = at(1)
                    if (after < 0 || isWs(after) || isDelim(after)) {
                        p++
                        return Ref(n.toInt(), g.toInt())
                    }
                }
            }
            p = save
        }
        return n
    }

    private fun keyword(): Any? {
        val start = p
        while (true) {
            val c = peek()
            if (c < 0 || isWs(c) || isDelim(c)) break
            p++
        }
        if (p == start) { p++; return Kw("?") } // einzelnes Störzeichen überspringen
        return when (val w = String(b, start, p - start, Charsets.ISO_8859_1)) {
            "true" -> true
            "false" -> false
            "null" -> null
            else -> Kw(w)
        }
    }

    fun name(): Name {
        p++ // '/'
        val out = ByteArrayOutputStream()
        while (true) {
            val c = peek()
            if (c < 0 || isWs(c) || isDelim(c)) break
            p++
            if (c == 0x23) { // #xx
                val h = hexVal(peek()) * 16 + hexVal(at(1))
                if (h >= 0) { out.write(h); p += 2 } else out.write(c)
            } else out.write(c)
        }
        return Name(String(out.toByteArray(), Charsets.ISO_8859_1))
    }

    private fun hexVal(c: Int): Int = when (c) {
        in 0x30..0x39 -> c - 0x30
        in 0x41..0x46 -> c - 0x37
        in 0x61..0x66 -> c - 0x57
        else -> -1000
    }

    private fun literal(): PStr {
        p++ // '('
        val out = ByteArrayOutputStream()
        var depth = 1
        while (true) {
            val c = next()
            when (c) {
                0x28 -> { depth++; out.write(c) }
                0x29 -> { depth--; if (depth == 0) break; out.write(c) }
                0x5C -> {
                    val e = next()
                    when (e) {
                        0x6E -> out.write(0x0A)
                        0x72 -> out.write(0x0D)
                        0x74 -> out.write(0x09)
                        0x62 -> out.write(0x08)
                        0x66 -> out.write(0x0C)
                        0x0D -> if (peek() == 0x0A) p++ // Zeilenfortsetzung
                        0x0A -> Unit
                        in 0x30..0x37 -> {
                            var v = e - 0x30
                            repeat(2) {
                                val d = peek()
                                if (d in 0x30..0x37) { v = v * 8 + (d - 0x30); p++ }
                            }
                            out.write(v and 0xFF)
                        }
                        else -> out.write(e)
                    }
                }
                else -> out.write(c)
            }
        }
        return PStr(out.toByteArray())
    }

    private fun hex(): PStr {
        p++ // '<'
        val out = ByteArrayOutputStream()
        var hi = -1
        while (true) {
            val c = next()
            if (c == 0x3E) break
            val v = hexVal(c)
            if (v < 0) continue
            if (hi < 0) hi = v else { out.write(hi * 16 + v); hi = -1 }
        }
        if (hi >= 0) out.write(hi * 16)
        return PStr(out.toByteArray())
    }

    private fun array(depth: Int): List<Any?> {
        p++ // '['
        val list = ArrayList<Any?>()
        while (true) {
            skipWs()
            val c = peek()
            if (c < 0) throw BadPdf("array")
            if (c == 0x5D) { p++; return list }
            list += obj(depth + 1)
        }
    }

    private fun dict(depth: Int): Map<String, Any?> {
        p += 2 // '<<'
        val map = HashMap<String, Any?>()
        while (true) {
            skipWs()
            val c = peek()
            if (c < 0) throw BadPdf("dict")
            if (c == 0x3E) {
                p++
                if (peek() == 0x3E) p++
                return map
            }
            if (c != 0x2F) { obj(depth + 1); continue } // kaputter Schlüssel: überspringen
            val key = name().v
            skipWs()
            // Fehlender Wert vor dem Ende ("/Key >>"): als null nehmen
            if (peek() == 0x3E) { map[key] = null; continue }
            map[key] = obj(depth + 1)
        }
    }
}

/** Wo ein Objekt liegt. */
private sealed class Loc {
    class InFile(val offset: Long, val gen: Int) : Loc()
    class InStream(val stream: Int, val index: Int) : Loc()
    object Free : Loc()
}

// =============================================================================================
// Die Datei
// =============================================================================================

private class PdfFile(private val raf: RandomAccessFile) {
    private val length = raf.length()
    private val xref = HashMap<Int, Loc>()
    private var trailer: Map<String, Any?> = emptyMap()
    private val cache = HashMap<Int, Any?>()
    private val objStreams = object : LinkedHashMap<Int, ObjStm?>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, ObjStm?>?) = size > 48
    }
    private var budget = 0
    private var crypt: Crypt? = null
    private var encryptNum = -1

    private class ObjStm(val data: ByteArray, val first: Int, val nums: IntArray, val offsets: IntArray)

    fun outline(pages: Int): List<Chapter>? {
        if (length < 32) return null
        // Kaputte Querverweise: die Datei durchsuchen – bei sehr großen Dateien lieber nicht (dauert zu lange)
        val canScan = length <= 160L * 1024 * 1024
        if (readXref()) setupCrypt() else if (canScan) scan() else return null
        var root = resolve(trailer["Root"]).asDict()
        if (root == null) {
            if (!canScan) return null
            scan()
            root = resolve(trailer["Root"]).asDict() ?: return null
        }
        val outlines = resolve(root["Outlines"]).asDict() ?: return null
        val first = outlines["First"] ?: return null
        val pageIndex = collectPages(root["Pages"])
        if (pageIndex.isEmpty()) return null
        val dests = NamedDests(root)
        val out = ArrayList<Chapter>()
        walk(first, 1, out, HashSet(), pageIndex, dests)
        return PdfOutline.tidy(out, pages)
    }

    // ----- Lesen -----

    private fun read(offset: Long, size: Int): ByteArray {
        if (offset < 0 || offset >= length) return ByteArray(0)
        val n = minOf(size.toLong(), length - offset).toInt()
        val buf = ByteArray(n)
        raf.seek(offset)
        raf.readFully(buf)
        return buf
    }

    /** Ein Objekt "n g obj … endobj" ab [offset]. Streams bekommen nur ihr Wörterbuch und die Lage der Daten. */
    private fun parseAt(offset: Long, expect: Int?): Any? {
        var size = 4096
        while (true) {
            val buf = read(offset, size)
            if (buf.isEmpty()) throw BadPdf("offset")
            val complete = offset + buf.size >= length
            try {
                val lx = Lexer(buf, 0, complete)
                val n = lx.obj()
                val g = lx.obj()
                val kw = lx.obj()
                if (n !is Long || g !is Long || kw != Kw("obj")) throw BadPdf("header")
                if (expect != null && n.toInt() != expect) throw BadPdf("number")
                val v = lx.obj()
                if (v is Map<*, *>) {
                    lx.skipWs()
                    if (lx.startsWith("stream")) {
                        lx.p += 6
                        if (lx.peek() == 0x0D) lx.p++
                        if (lx.peek() == 0x0A) lx.p++
                        return PStream(v.asDict()!!, offset + lx.p, n.toInt(), g.toInt())
                    }
                }
                return v
            } catch (e: Lexer.Eof) {
                if (complete || size >= 16 * 1024 * 1024) throw BadPdf("too big")
                size *= 4
            }
        }
    }

    private fun getObj(num: Int): Any? {
        if (cache.containsKey(num)) return cache[num]
        if (++budget > 200_000) throw BadPdf("budget")
        cache[num] = null // gegen Kreise
        val v = when (val loc = xref[num]) {
            is Loc.InFile -> runCatching { parseAt(loc.offset, num) }.getOrNull()?.let { o ->
                val c = crypt
                if (c != null && num != encryptNum && o !is PStream) c.decryptAll(o, num, loc.gen) else o
            }
            is Loc.InStream -> fromObjStm(loc.stream, loc.index, num)
            else -> null
        }
        cache[num] = v
        return v
    }

    fun resolve(v: Any?): Any? {
        var x = v
        var hops = 0
        while (x is Ref && hops++ < 32) x = getObj(x.num)
        return x
    }

    private fun fromObjStm(stream: Int, index: Int, num: Int): Any? {
        val os = if (objStreams.containsKey(stream)) objStreams[stream] else loadObjStm(stream).also { objStreams[stream] = it }
        os ?: return null
        val i = if (index in os.nums.indices && os.nums[index] == num) index else os.nums.indexOf(num)
        if (i < 0) return null
        val start = os.first + os.offsets[i]
        if (start !in os.data.indices) return null
        return runCatching { Lexer(os.data, start, true).obj() }.getOrNull()
    }

    private fun loadObjStm(stream: Int): ObjStm? {
        val s = getObj(stream) as? PStream ?: return null
        val data = streamData(s, decrypt = true) ?: return null
        val n = s.dict["N"].asInt() ?: return null
        val first = s.dict["First"].asInt() ?: return null
        if (n <= 0 || n > 100_000 || first !in 0..data.size) return null
        val lx = Lexer(data, 0, true)
        val nums = IntArray(n)
        val offs = IntArray(n)
        for (i in 0 until n) {
            lx.skipWs()
            nums[i] = (lx.number() as? Long)?.toInt() ?: return null
            lx.skipWs()
            offs[i] = (lx.number() as? Long)?.toInt() ?: return null
        }
        return ObjStm(data, first, nums, offs)
    }

    /** Daten eines Streams: Länge prüfen, entschlüsseln, entpacken (nur Flate – mehr braucht es hier nicht). */
    private fun streamData(s: PStream, decrypt: Boolean): ByteArray? {
        var len = resolve(s.dict["Length"]).asInt() ?: -1
        if (len < 0 || s.dataStart + len > length || !endsWithEndstream(s.dataStart + len)) len = findEndstream(s.dataStart) ?: return null
        if (len > 64 * 1024 * 1024) return null
        var data = read(s.dataStart, len)
        val c = crypt
        if (decrypt && c != null) data = c.decryptStream(data, s.num, s.gen)
        var filters = resolve(s.dict["Filter"])
        var parms = resolve(s.dict["DecodeParms"])
        if (filters is Name) { filters = listOf(filters); parms = listOf(parms) }
        val fl = (filters as? List<*>) ?: emptyList<Any?>()
        val pl = (parms as? List<*>) ?: emptyList<Any?>()
        for ((i, f) in fl.withIndex()) {
            val name = (resolve(f) as? Name)?.v ?: return null
            data = when (name) {
                "FlateDecode", "Fl" -> predict(inflate(data) ?: return null, resolve(pl.getOrNull(i)).asDict()) ?: return null
                else -> return null
            }
        }
        return data
    }

    private fun endsWithEndstream(at: Long): Boolean {
        val tail = read(at, 32)
        var i = 0
        while (i < tail.size && isWs(tail[i].toInt() and 0xFF)) i++
        val word = "endstream"
        if (i + word.length > tail.size) return false
        for (k in word.indices) if (tail[i + k].toInt() != word[k].code) return false
        return true
    }

    /** Ohne brauchbare Längenangabe: bis "endstream" suchen (Zeilenende davor gehört nicht zu den Daten). */
    private fun findEndstream(start: Long): Int? {
        val word = "endstream".toByteArray(Charsets.ISO_8859_1)
        var pos = start
        val chunk = 256 * 1024
        while (pos < length && pos - start < 64L * 1024 * 1024) {
            val buf = read(pos, chunk + word.size)
            val hit = indexOf(buf, word, 0)
            if (hit >= 0) {
                var end = pos + hit
                val before = read(maxOf(start, end - 2), (end - maxOf(start, end - 2)).toInt())
                if (before.isNotEmpty() && before.last().toInt() == 0x0A) end--
                if (before.size >= 2 && before[before.size - 2].toInt() == 0x0D && before.last().toInt() == 0x0A) end--
                else if (before.isNotEmpty() && before.last().toInt() == 0x0D) end--
                return (end - start).toInt().coerceAtLeast(0)
            }
            pos += chunk
        }
        return null
    }

    private fun inflate(data: ByteArray): ByteArray? {
        for (raw in arrayOf(false, true)) {
            val inf = Inflater(raw)
            try {
                inf.setInput(data)
                val out = ByteArrayOutputStream(maxOf(64, data.size * 3))
                val buf = ByteArray(64 * 1024)
                var idle = 0
                while (!inf.finished()) {
                    val n = try { inf.inflate(buf) } catch (e: Exception) { -1 }
                    if (n < 0) break
                    if (n == 0) {
                        if (inf.needsInput() || inf.needsDictionary() || ++idle > 4) break
                    } else out.write(buf, 0, n)
                    if (out.size() > 128 * 1024 * 1024) return null
                }
                // Auch ein abgebrochener Stream liefert oft genug (Prüfsumme kaputt o. Ä.)
                if (out.size() > 0) return out.toByteArray()
            } finally {
                inf.end()
            }
        }
        return null
    }

    /** PNG-Prädiktoren (10–15), wie sie Querverweis- und Objekt-Streams nutzen. TIFF (2) kommt hier praktisch nicht vor. */
    private fun predict(data: ByteArray, parms: Map<String, Any?>?): ByteArray? {
        val pred = parms?.get("Predictor").asInt() ?: 1
        if (pred <= 1) return data
        if (pred < 10) return null
        val colors = parms?.get("Colors").asInt() ?: 1
        val bpc = parms?.get("BitsPerComponent").asInt() ?: 8
        val columns = parms?.get("Columns").asInt() ?: 1
        val bpp = maxOf(1, colors * bpc / 8)
        val row = (colors * bpc * columns + 7) / 8
        if (row <= 0) return null
        val out = ByteArrayOutputStream(data.size)
        var prev = ByteArray(row)
        var i = 0
        while (i + row + 1 <= data.size) {
            val type = data[i].toInt() and 0xFF
            val cur = data.copyOfRange(i + 1, i + 1 + row)
            for (k in 0 until row) {
                val a = if (k >= bpp) cur[k - bpp].toInt() and 0xFF else 0
                val b = prev[k].toInt() and 0xFF
                val c = if (k >= bpp) prev[k - bpp].toInt() and 0xFF else 0
                val x = cur[k].toInt() and 0xFF
                cur[k] = when (type) {
                    1 -> x + a
                    2 -> x + b
                    3 -> x + (a + b) / 2
                    4 -> {
                        val p = a + b - c
                        val pa = kotlin.math.abs(p - a)
                        val pb = kotlin.math.abs(p - b)
                        val pc = kotlin.math.abs(p - c)
                        x + if (pa <= pb && pa <= pc) a else if (pb <= pc) b else c
                    }
                    else -> x
                }.toByte()
            }
            out.write(cur)
            prev = cur
            i += row + 1
        }
        return out.toByteArray()
    }

    // ----- Querverweise -----

    /** Alle Querverweis-Abschnitte vom neuesten zum ältesten. false = kaputt (dann wird gesucht). */
    private fun readXref(): Boolean = runCatching {
        val start = findStartXref() ?: return@runCatching false
        var off = start
        val seen = HashSet<Long>()
        var newest = true
        while (off in 1 until length && seen.add(off) && seen.size <= 64) {
            val t = if (isClassicAt(off)) readClassic(off) else readXrefStream(off)
            if (newest) { trailer = t; newest = false }
            else if (trailer["Root"] == null && t["Root"] != null) trailer = trailer + ("Root" to t["Root"])
            t["XRefStm"].asInt()?.let { x -> runCatching { readXrefStream(x.toLong()) } }
            off = t["Prev"].asInt()?.toLong() ?: 0L
        }
        trailer["Root"] != null && xref.isNotEmpty()
    }.getOrDefault(false)

    private fun findStartXref(): Long? {
        for (span in intArrayOf(4096, 65536, 1024 * 1024)) {
            val from = maxOf(0L, length - span)
            val buf = read(from, (length - from).toInt())
            val hit = lastIndexOf(buf, "startxref".toByteArray(Charsets.ISO_8859_1))
            if (hit >= 0) {
                val lx = Lexer(buf, hit + 9, true)
                lx.skipWs()
                return (lx.number() as? Long)?.takeIf { it > 0 }
            }
            if (from == 0L) break
        }
        return null
    }

    private fun isClassicAt(off: Long): Boolean {
        val b = read(off, 32)
        val lx = Lexer(b, 0, true)
        lx.skipWs()
        return lx.startsWith("xref")
    }

    private fun readClassic(off: Long): Map<String, Any?> {
        var size = 64 * 1024
        while (true) {
            val buf = read(off, size)
            val complete = off + buf.size >= length
            try {
                val lx = Lexer(buf, 0, complete)
                lx.skipWs()
                if (!lx.startsWith("xref")) throw BadPdf("xref")
                lx.p += 4
                val found = ArrayList<LongArray>() // [Nummer, Stelle, Generation, frei?]
                var firstSection = true
                while (true) {
                    lx.skipWs()
                    if (lx.startsWith("trailer")) { lx.p += 7; break }
                    var startNum = (lx.number() as? Long)?.toInt() ?: throw BadPdf("xref start")
                    lx.skipWs()
                    val count = (lx.number() as? Long)?.toInt() ?: throw BadPdf("xref count")
                    if (count < 0 || count > 10_000_000) throw BadPdf("xref count")
                    for (i in 0 until count) {
                        lx.skipWs()
                        val o = lx.number() as? Long ?: throw BadPdf("xref entry")
                        lx.skipWs()
                        val g = lx.number() as? Long ?: throw BadPdf("xref entry")
                        lx.skipWs()
                        val t = lx.next()
                        // Häufiger Fehler: erster Abschnitt beginnt bei 1 statt 0
                        if (firstSection && i == 0 && startNum == 1 && t == 0x66 && g == 65535L) startNum = 0
                        found += longArrayOf((startNum + i).toLong(), o, g, if (t == 0x66) 1L else 0L)
                    }
                    firstSection = false
                }
                val t = lx.obj().asDict() ?: throw BadPdf("trailer")
                for (e in found) {
                    val num = e[0].toInt()
                    if (xref.containsKey(num)) continue
                    xref[num] = if (e[3] == 1L || e[1] <= 0L) Loc.Free else Loc.InFile(e[1], e[2].toInt())
                }
                return t
            } catch (e: Lexer.Eof) {
                if (complete || size >= 64 * 1024 * 1024) throw BadPdf("xref size")
                size *= 4
            }
        }
    }

    private fun readXrefStream(off: Long): Map<String, Any?> {
        val s = parseAt(off, null) as? PStream ?: throw BadPdf("xref stream")
        val d = s.dict
        if ((d["Type"] as? Name)?.v != "XRef") throw BadPdf("xref type")
        val data = streamData(s, decrypt = false) ?: throw BadPdf("xref data")
        val w = (d["W"] as? List<*>)?.map { it.asInt() ?: 0 } ?: throw BadPdf("W")
        if (w.size < 3 || w.any { it < 0 || it > 8 }) throw BadPdf("W")
        val size = d["Size"].asInt() ?: throw BadPdf("Size")
        val index = (d["Index"] as? List<*>)?.map { it.asInt() ?: 0 } ?: listOf(0, size)
        val entry = w[0] + w[1] + w[2]
        var pos = 0
        var k = 0
        while (k + 1 < index.size) {
            val start = index[k]
            val count = index[k + 1]
            for (i in 0 until count) {
                if (pos + entry > data.size) break
                val type = if (w[0] == 0) 1L else field(data, pos, w[0])
                val f2 = field(data, pos + w[0], w[1])
                val f3 = field(data, pos + w[0] + w[1], w[2])
                pos += entry
                val num = start + i
                if (xref.containsKey(num)) continue
                xref[num] = when (type) {
                    1L -> if (f2 > 0) Loc.InFile(f2, f3.toInt()) else Loc.Free
                    2L -> Loc.InStream(f2.toInt(), f3.toInt())
                    else -> Loc.Free
                }
            }
            k += 2
        }
        return d
    }

    private fun field(data: ByteArray, pos: Int, width: Int): Long {
        var v = 0L
        for (i in 0 until width) v = (v shl 8) or (data[pos + i].toLong() and 0xFF)
        return v
    }

    /**
     * Notfall: die Datei nach "n g obj" durchsuchen (spätere Fassungen gewinnen). Den Trailer liefert das letzte
     * "trailer"-Wörterbuch oder der letzte Querverweis-Stream; sonst wird der Katalog gesucht.
     */
    private fun scan() {
        xref.clear()
        cache.clear()
        objStreams.clear()
        crypt = null
        encryptNum = -1
        var lastTrailer: Map<String, Any?>? = null
        val xrefStreams = ArrayList<Long>()
        val objStmNums = ArrayList<Int>()
        val catalogs = ArrayList<Int>()
        val chunk = 4 * 1024 * 1024
        val back = 32      // Rückblick: die Nummern vor "obj" können im vorigen Stück beginnen
        val overlap = 4096 // Vorausschau: Trailer und Kopf des Objekts
        var pos = 0L
        while (pos < length) {
            val from = maxOf(0L, pos - back)
            val buf = read(from, (pos - from).toInt() + chunk + overlap)
            val skip = (pos - from).toInt()
            val last = from + buf.size >= length
            val limit = if (last) buf.size else buf.size - overlap
            val base = from
            var i = skip
            while (i < limit) {
                val c = buf[i].toInt() and 0xFF
                if (c == 0x6F && i + 2 < buf.size && buf[i + 1].toInt() == 0x62 && buf[i + 2].toInt() == 0x6A) {
                    headerBefore(buf, i)?.let { (num, gen, at) ->
                        val offset = base + at
                        xref[num] = Loc.InFile(offset, gen)
                        val peek = String(buf, i + 3, minOf(400, buf.size - i - 3), Charsets.ISO_8859_1)
                        if (peek.contains("/XRef")) xrefStreams += offset
                        if (peek.contains("/ObjStm")) objStmNums += num
                        if (peek.contains("/Catalog")) catalogs += num
                    }
                    i += 3
                    continue
                }
                if (c == 0x74 && matches(buf, i, "trailer")) {
                    runCatching { Lexer(buf, i + 7, last).obj().asDict() }.getOrNull()?.let { if (it["Root"] != null) lastTrailer = it }
                }
                i++
            }
            pos = base + limit
        }
        var t = lastTrailer
        if (t == null) {
            for (off in xrefStreams.asReversed()) {
                val d = runCatching { (parseAt(off, null) as? PStream)?.dict }.getOrNull()
                if (d?.get("Root") != null) { t = d; break }
            }
        }
        // Erst den Schlüssel (Objekt-Streams sind bei verschlüsselten PDFs mitverschlüsselt) …
        trailer = t ?: emptyMap()
        if (trailer["Encrypt"] != null) setupCrypt()
        // … dann die Objekte in Objekt-Streams eintragen (nur, wo es keine eigene Fassung in der Datei gibt)
        for (s in objStmNums) {
            val os = runCatching { loadObjStm(s) }.getOrNull() ?: continue
            for ((k, n) in os.nums.withIndex()) if (!xref.containsKey(n)) xref[n] = Loc.InStream(s, k)
        }
        cache.clear()
        if (t == null) {
            val cat = catalogs.lastOrNull() ?: xref.keys.sortedDescending().firstOrNull { n ->
                (runCatching { getObj(n) }.getOrNull().asDict()?.get("Type") as? Name)?.v == "Catalog"
            }
            if (cat != null) t = mapOf("Root" to Ref(cat, 0))
        }
        trailer = t ?: emptyMap()
        cache.clear()
    }

    private fun matches(buf: ByteArray, at: Int, word: String): Boolean {
        if (at + word.length > buf.size) return false
        for (k in word.indices) if (buf[at + k].toInt() != word[k].code) return false
        return true
    }

    /** Vor "obj" an Stelle [o]: "<Zahl> <Zahl> " am Zeilenanfang? → (Nummer, Generation, Beginn). */
    private fun headerBefore(buf: ByteArray, o: Int): Triple<Int, Int, Int>? {
        var i = o - 1
        if (i < 0 || !isWs(buf[i].toInt() and 0xFF)) return null
        while (i >= 0 && isWs(buf[i].toInt() and 0xFF)) i--
        val genEnd = i + 1
        while (i >= 0 && isDigit(buf[i].toInt() and 0xFF)) i--
        val genStart = i + 1
        if (genStart == genEnd || genEnd - genStart > 5) return null
        if (i < 0 || !isWs(buf[i].toInt() and 0xFF)) return null
        while (i >= 0 && isWs(buf[i].toInt() and 0xFF)) i--
        val numEnd = i + 1
        while (i >= 0 && isDigit(buf[i].toInt() and 0xFF)) i--
        val numStart = i + 1
        if (numStart == numEnd || numEnd - numStart > 10) return null
        if (i >= 0) {
            val before = buf[i].toInt() and 0xFF
            if (!isWs(before) && !isDelim(before)) return null
        }
        val num = String(buf, numStart, numEnd - numStart, Charsets.ISO_8859_1).toLongOrNull() ?: return null
        val gen = String(buf, genStart, genEnd - genStart, Charsets.ISO_8859_1).toIntOrNull() ?: return null
        if (num > Int.MAX_VALUE) return null
        return Triple(num.toInt(), gen, numStart)
    }

    // ----- Verschlüsselung -----

    private fun setupCrypt() {
        val encRaw = trailer["Encrypt"] ?: return
        encryptNum = (encRaw as? Ref)?.num ?: -1
        val enc = resolve(encRaw).asDict() ?: throw BadPdf("encrypt")
        if ((enc["Filter"] as? Name)?.v != "Standard") throw BadPdf("filter")
        val idList = resolve(trailer["ID"]) as? List<*>
        val id0 = (resolve(idList?.firstOrNull()) as? PStr)?.bytes ?: ByteArray(0)
        crypt = Crypt.create(enc, id0) { resolve(it) } ?: throw BadPdf("password")
        // Was bisher gelesen wurde, war noch verschlüsselt
        cache.clear()
        objStreams.clear()
    }

    // ----- Seiten und Ziele -----

    /** Seitenobjekt (Nummer) → Seite ab 0, in der Reihenfolge des Seitenbaums (wie PdfRenderer sie zeigt). */
    private fun collectPages(rootRef: Any?): Map<Int, Int> {
        val out = HashMap<Int, Int>()
        val seen = HashSet<Int>()
        var index = 0
        fun visit(node: Any?, depth: Int) {
            if (depth > 64 || index > 200_000) return
            val ref = node as? Ref
            if (ref != null && !seen.add(ref.num)) return
            val d = resolve(node).asDict() ?: return
            val kids = resolve(d["Kids"]) as? List<*>
            val type = (d["Type"] as? Name)?.v
            if (kids == null || type == "Page") {
                if (ref != null) out[ref.num] = index
                index++
                return
            }
            // Zählt der Knoten genau so viele Seiten, wie er Kinder hat, sind alle Kinder Seiten: nicht einzeln öffnen
            val count = resolve(d["Count"]).asInt()
            fun isLeaf(k: Any?): Boolean = resolve(k).asDict()?.let { (it["Type"] as? Name)?.v == "Page" || it["Kids"] == null } ?: false
            if (count == kids.size && kids.size > 2 && kids.all { it is Ref } && isLeaf(kids.first()) && isLeaf(kids.last())) {
                for (k in kids) {
                    val r = k as Ref
                    if (seen.add(r.num)) out[r.num] = index
                    index++
                }
                return
            }
            for (k in kids) visit(k, depth + 1)
        }
        visit(rootRef, 0)
        return out
    }

    private inner class NamedDests(root: Map<String, Any?>) {
        private val oldStyle = resolve(root["Dests"]).asDict()
        private val tree = resolve(resolve(root["Names"]).asDict()?.get("Dests")).asDict()
        private var map: HashMap<String, Any?>? = null

        fun lookup(key: String): Any? {
            oldStyle?.get(key)?.let { return it }
            val m = map ?: HashMap<String, Any?>().also { built -> tree?.let { collect(it, built, 0, HashSet()) }; map = built }
            return m[key]
        }

        private fun collect(node: Map<String, Any?>, into: HashMap<String, Any?>, depth: Int, seen: HashSet<Int>) {
            if (depth > 32 || into.size > 100_000) return
            (resolve(node["Names"]) as? List<*>)?.let { names ->
                var i = 0
                while (i + 1 < names.size) {
                    val k = resolve(names[i])
                    val key = when (k) {
                        is PStr -> String(k.bytes, Charsets.ISO_8859_1)
                        is Name -> k.v
                        else -> null
                    }
                    if (key != null && !into.containsKey(key)) into[key] = names[i + 1]
                    i += 2
                }
            }
            (resolve(node["Kids"]) as? List<*>)?.forEach { kid ->
                val r = kid as? Ref
                if (r != null && !seen.add(r.num)) return@forEach
                resolve(kid).asDict()?.let { collect(it, into, depth + 1, seen) }
            }
        }
    }

    private fun destPage(item: Map<String, Any?>, pageIndex: Map<Int, Int>, dests: NamedDests): Int? {
        var d = item["Dest"]
        if (d == null) {
            val a = resolve(item["A"]).asDict()
            if (a != null && (resolve(a["S"]) as? Name)?.v == "GoTo") d = a["D"]
        }
        var x = resolve(d)
        repeat(4) {
            x = when (val v = x) {
                is PStr -> resolve(dests.lookup(String(v.bytes, Charsets.ISO_8859_1)))
                is Name -> resolve(dests.lookup(v.v))
                is Map<*, *> -> resolve(v.asDict()?.get("D"))
                else -> return@repeat
            }
        }
        val arr = x as? List<*> ?: return null
        return when (val target = arr.firstOrNull()) {
            is Ref -> pageIndex[target.num]
            is Long -> target.toInt() // manche Programme schreiben die Seitennummer statt eines Verweises
            else -> null
        }
    }

    private fun walk(
        first: Any?,
        level: Int,
        out: MutableList<Chapter>,
        seen: HashSet<Int>,
        pageIndex: Map<Int, Int>,
        dests: NamedDests,
    ) {
        var cur = first
        var guard = 0
        while (cur != null && guard++ < 20_000 && out.size < 20_000) {
            val ref = cur as? Ref
            if (ref != null && !seen.add(ref.num)) return // Kreis
            val item = resolve(cur).asDict() ?: return
            val title = text(resolve(item["Title"]))
            if (title != null) out += Chapter(title.take(140), level, destPage(item, pageIndex, dests) ?: -1)
            if (level < 3) walk(item["First"], level + 1, out, seen, pageIndex, dests)
            cur = item["Next"]
        }
    }

    /** Titel: UTF-16 (mit BOM), UTF-8 (mit BOM) oder PDFDocEncoding. Steuerzeichen raus, Leerraum glätten. */
    private fun text(v: Any?): String? {
        val b = (v as? PStr)?.bytes ?: return null
        val s = when {
            b.size >= 2 && (b[0].toInt() and 0xFF) == 0xFE && (b[1].toInt() and 0xFF) == 0xFF -> String(b, 2, b.size - 2, Charsets.UTF_16BE)
            b.size >= 2 && (b[0].toInt() and 0xFF) == 0xFF && (b[1].toInt() and 0xFF) == 0xFE -> String(b, 2, b.size - 2, Charsets.UTF_16LE)
            b.size >= 3 && (b[0].toInt() and 0xFF) == 0xEF && (b[1].toInt() and 0xFF) == 0xBB && (b[2].toInt() and 0xFF) == 0xBF -> String(b, 3, b.size - 3, Charsets.UTF_8)
            else -> pdfDoc(b)
        }
        val clean = s.replace(LANG_ESCAPE, "")
            .map { if (it < ' ' || it == '\u007F') ' ' else it }
            .joinToString("")
            .replace(SPACES, " ")
            .trim()
        return clean.ifEmpty { null }
    }

    private fun pdfDoc(b: ByteArray): String {
        val sb = StringBuilder(b.size)
        for (x in b) {
            val c = x.toInt() and 0xFF
            sb.append(
                when (c) {
                    in 0x18..0x1F -> PDF_DOC_LOW[c - 0x18]
                    in 0x80..0xA0 -> PDF_DOC_HIGH[c - 0x80]
                    else -> c.toChar()
                }
            )
        }
        return sb.toString()
    }

    private companion object {
        /** Sprachkennungen in UTF-16-Titeln (ESC … ESC) */
        val LANG_ESCAPE = Regex("\u001B[^\u001B]*\u001B")
        val SPACES = Regex("\\s+")
        val PDF_DOC_LOW = charArrayOf('˘', 'ˇ', 'ˆ', '˙', '˝', '˛', '˚', '˜')
        val PDF_DOC_HIGH = charArrayOf(
            '•', '†', '‡', '…', '—', '–', 'ƒ', '⁄',
            '‹', '›', '−', '‰', '„', '“', '”', '‘',
            '’', '‚', '™', 'ﬁ', 'ﬂ', 'Ł', 'Œ', 'Š',
            'Ÿ', 'Ž', 'ı', 'ł', 'œ', 'š', 'ž', ' ',
            '€',
        )

        fun indexOf(hay: ByteArray, needle: ByteArray, from: Int): Int {
            var i = from
            while (i <= hay.size - needle.size) {
                var k = 0
                while (k < needle.size && hay[i + k] == needle[k]) k++
                if (k == needle.size) return i
                i++
            }
            return -1
        }

        fun lastIndexOf(hay: ByteArray, needle: ByteArray): Int {
            var i = hay.size - needle.size
            while (i >= 0) {
                var k = 0
                while (k < needle.size && hay[i + k] == needle[k]) k++
                if (k == needle.size) return i
                i--
            }
            return -1
        }
    }
}

// =============================================================================================
// Entschlüsselung (Standard-Sicherheit, leeres Benutzerkennwort)
// =============================================================================================

private class Crypt(private val key: ByteArray, private val strMethod: Method, private val stmMethod: Method) {
    enum class Method { None, RC4, AESV2, AESV3 }

    fun decryptAll(o: Any?, num: Int, gen: Int): Any? = when (o) {
        is PStr -> PStr(decrypt(o.bytes, num, gen, strMethod))
        is List<*> -> o.map { decryptAll(it, num, gen) }
        is Map<*, *> -> o.entries.associate { (k, v) -> (k as String) to decryptAll(v, num, gen) }
        else -> o
    }

    fun decryptStream(data: ByteArray, num: Int, gen: Int): ByteArray = decrypt(data, num, gen, stmMethod)

    private fun decrypt(data: ByteArray, num: Int, gen: Int, m: Method): ByteArray = when (m) {
        Method.None -> data
        Method.RC4 -> rc4(objectKey(num, gen, false), data)
        Method.AESV2 -> aesCbc(objectKey(num, gen, true), data)
        Method.AESV3 -> aesCbc(key, data)
    }

    private fun objectKey(num: Int, gen: Int, aes: Boolean): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        md.update(key)
        md.update(byteArrayOf(num.toByte(), (num shr 8).toByte(), (num shr 16).toByte(), gen.toByte(), (gen shr 8).toByte()))
        if (aes) md.update(byteArrayOf(0x73, 0x41, 0x6C, 0x54)) // "sAlT"
        return md.digest().copyOf(minOf(key.size + 5, 16))
    }

    companion object {
        private val PAD = intArrayOf(
            0x28, 0xBF, 0x4E, 0x5E, 0x4E, 0x75, 0x8A, 0x41, 0x64, 0x00, 0x4E, 0x56, 0xFF, 0xFA, 0x01, 0x08,
            0x2E, 0x2E, 0x00, 0xB6, 0xD0, 0x68, 0x3E, 0x80, 0x2F, 0x0C, 0xA9, 0xFE, 0x64, 0x53, 0x69, 0x7A,
        ).map { it.toByte() }.toByteArray()

        /** Schlüssel mit leerem Benutzerkennwort berechnen und prüfen. null = es braucht ein echtes Kennwort. */
        fun create(enc: Map<String, Any?>, id0: ByteArray, resolve: (Any?) -> Any?): Crypt? {
            val v = resolve(enc["V"]).asInt() ?: 0
            val r = resolve(enc["R"]).asInt() ?: return null
            val o = (resolve(enc["O"]) as? PStr)?.bytes ?: return null
            val u = (resolve(enc["U"]) as? PStr)?.bytes ?: return null
            val cf = resolve(enc["CF"]).asDict()
            fun method(name: Any?): Method {
                val n = (resolve(name) as? Name)?.v ?: return Method.None
                if (n == "Identity") return Method.None
                val f = resolve(cf?.get(n)).asDict() ?: return Method.None
                return when ((resolve(f["CFM"]) as? Name)?.v) {
                    "V2" -> Method.RC4
                    "AESV2" -> Method.AESV2
                    "AESV3" -> Method.AESV3
                    else -> Method.None
                }
            }
            val strM = if (v >= 4) method(enc["StrF"]) else Method.RC4
            val stmM = if (v >= 4) method(enc["StmF"]) else Method.RC4
            if (r >= 5) {
                val ue = (resolve(enc["UE"]) as? PStr)?.bytes ?: return null
                if (u.size < 48 || ue.size < 32) return null
                val check = if (r == 5) sha("SHA-256", u.copyOfRange(32, 40)) else hash2B(u.copyOfRange(32, 40))
                if (!check.contentEquals(u.copyOfRange(0, 32))) return null
                val ik = if (r == 5) sha("SHA-256", u.copyOfRange(40, 48)) else hash2B(u.copyOfRange(40, 48))
                val c = Cipher.getInstance("AES/CBC/NoPadding")
                c.init(Cipher.DECRYPT_MODE, SecretKeySpec(ik, "AES"), IvParameterSpec(ByteArray(16)))
                val fileKey = c.doFinal(ue.copyOf(32))
                return Crypt(fileKey, strM, stmM)
            }
            val bits = resolve(enc["Length"]).asInt()?.takeIf { it in 40..256 } ?: if (v >= 4) 128 else 40
            val n = if (r == 2) 5 else bits / 8
            val p = resolve(enc["P"])
            val pInt = (p as? Long)?.toInt() ?: (p as? Double)?.toLong()?.toInt() ?: 0
            val md = MessageDigest.getInstance("MD5")
            md.update(PAD)
            md.update(o.copyOf(32))
            md.update(byteArrayOf(pInt.toByte(), (pInt shr 8).toByte(), (pInt shr 16).toByte(), (pInt shr 24).toByte()))
            md.update(id0)
            if (r >= 4 && resolve(enc["EncryptMetadata"]) == false) md.update(byteArrayOf(-1, -1, -1, -1))
            var h = md.digest()
            if (r >= 3) repeat(50) { h = MessageDigest.getInstance("MD5").digest(h.copyOf(n)) }
            val key = h.copyOf(n)
            // Prüfen, ob das leere Kennwort stimmt
            val ok = if (r == 2) {
                rc4(key, PAD).contentEquals(u.copyOf(32))
            } else {
                val m2 = MessageDigest.getInstance("MD5")
                m2.update(PAD)
                m2.update(id0)
                var x = rc4(key, m2.digest())
                for (i in 1..19) x = rc4(ByteArray(key.size) { (key[it].toInt() xor i).toByte() }, x)
                u.size >= 16 && x.contentEquals(u.copyOf(16))
            }
            if (!ok) return null
            return Crypt(key, strM, stmM)
        }

        private fun sha(alg: String, vararg parts: ByteArray): ByteArray {
            val md = MessageDigest.getInstance(alg)
            parts.forEach { md.update(it) }
            return md.digest()
        }

        /** Algorithmus 2.B (PDF 2.0) für ein leeres Kennwort ohne Zusatzdaten. */
        private fun hash2B(salt: ByteArray): ByteArray {
            var k = sha("SHA-256", salt)
            var round = 0
            while (true) {
                val k1 = ByteArray(k.size * 64)
                for (i in 0 until 64) System.arraycopy(k, 0, k1, i * k.size, k.size)
                val c = Cipher.getInstance("AES/CBC/NoPadding")
                c.init(Cipher.ENCRYPT_MODE, SecretKeySpec(k.copyOf(16), "AES"), IvParameterSpec(k.copyOfRange(16, 32)))
                val e = c.doFinal(k1)
                var sum = 0
                for (i in 0 until 16) sum += e[i].toInt() and 0xFF
                k = when (sum % 3) {
                    0 -> sha("SHA-256", e)
                    1 -> sha("SHA-384", e)
                    else -> sha("SHA-512", e)
                }
                round++
                if (round >= 64 && (e[e.size - 1].toInt() and 0xFF) <= round - 32) break
                if (round > 2000) break
            }
            return k.copyOf(32)
        }

        fun rc4(key: ByteArray, data: ByteArray): ByteArray {
            val s = IntArray(256) { it }
            var j = 0
            for (i in 0 until 256) {
                j = (j + s[i] + (key[i % key.size].toInt() and 0xFF)) and 0xFF
                val t = s[i]; s[i] = s[j]; s[j] = t
            }
            val out = ByteArray(data.size)
            var i = 0
            j = 0
            for (k in data.indices) {
                i = (i + 1) and 0xFF
                j = (j + s[i]) and 0xFF
                val t = s[i]; s[i] = s[j]; s[j] = t
                out[k] = (data[k].toInt() xor s[(s[i] + s[j]) and 0xFF]).toByte()
            }
            return out
        }

        fun aesCbc(key: ByteArray, data: ByteArray): ByteArray {
            if (data.size < 32 || data.size % 16 != 0) return if (data.size == 16) ByteArray(0) else data
            val iv = data.copyOf(16)
            val c = Cipher.getInstance("AES/CBC/NoPadding")
            c.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
            val plain = c.doFinal(data, 16, data.size - 16)
            val pad = plain.last().toInt() and 0xFF
            return if (pad in 1..16 && pad <= plain.size) plain.copyOf(plain.size - pad) else plain
        }
    }
}
