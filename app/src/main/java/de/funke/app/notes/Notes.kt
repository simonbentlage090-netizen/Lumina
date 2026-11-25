package de.funke.app.notes

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Notizen, die man sich in einer Fokuszeit oder im Buch macht (getippt oder diktiert).
 * Liegen nur auf dem Handy, in einer kleinen JSON-Datei im App-Speicher.
 */
data class Note(
    val id: Long,
    /** Zeitpunkt (System.currentTimeMillis). */
    val createdAt: Long,
    val text: String,
    /** Per Sprache diktiert? */
    val voice: Boolean = false,
    /** Zu welcher Session sie gehört (Startzeit der Session), 0 = außerhalb einer Session. */
    val session: Long = 0L,
    /**
     * Die andere Fassung (seit 13.3): nach "Aufräumen" der Text davor. Bleibt gespeichert, damit man das
     * Aufräumen auch Tage später noch rückgängig machen kann. Tauschen macht daraus wieder die aufgeräumte Fassung.
     */
    val original: String? = null,
    /**
     * Im Buch geschrieben (seit 14.0): Kennung und Titel des Buchs, Seite (ab 1, zur Anzeige) und die
     * Stelle im Buch ([section]/[offset] wie bei Lesezeichen), damit man genau dorthin zurückspringt.
     */
    val book: String? = null,
    val bookTitle: String? = null,
    val page: Int = 0,
    val section: Int = 0,
    val offset: Int = 0,
) {
    val inBook: Boolean get() = book != null
}

object NotesStore {
    private const val FILE = "notes.json"

    fun load(context: Context?): List<Note> {
        val f = context?.let { File(it.filesDir, FILE) } ?: return emptyList()
        if (!f.exists()) return emptyList()
        return runCatching {
            val arr = JSONArray(f.readText())
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Note(
                    id = o.getLong("id"),
                    createdAt = o.getLong("at"),
                    text = o.getString("text"),
                    voice = o.optBoolean("voice", false),
                    session = o.optLong("session", 0L),
                    original = if (o.has("orig")) o.getString("orig") else null,
                    book = if (o.has("book")) o.getString("book") else null,
                    bookTitle = if (o.has("btitle")) o.getString("btitle") else null,
                    page = o.optInt("page", 0),
                    section = o.optInt("sec", 0),
                    offset = o.optInt("off", 0),
                )
            }
        }.getOrDefault(emptyList())
    }

    fun save(context: Context?, notes: List<Note>) {
        val dir = context?.filesDir ?: return
        runCatching {
            val arr = JSONArray()
            notes.forEach { n ->
                arr.put(
                    JSONObject().put("id", n.id).put("at", n.createdAt).put("text", n.text)
                        .put("voice", n.voice).put("session", n.session)
                        .apply { if (n.original != null) put("orig", n.original) }
                        .apply {
                            if (n.book != null) {
                                put("book", n.book).put("page", n.page).put("sec", n.section).put("off", n.offset)
                                if (n.bookTitle != null) put("btitle", n.bookTitle)
                            }
                        }
                )
            }
            // erst in eine Hilfsdatei, dann umbenennen: so geht beim Absturz nichts kaputt
            val tmp = File(dir, "$FILE.tmp")
            tmp.writeText(arr.toString())
            tmp.renameTo(File(dir, FILE))
        }
    }
}

/**
 * Räumt Diktiertes auf, ganz ohne KI: Füllwörter ("äh", "ähm", "quasi", "halt" …) raus,
 * doppelte Wörter ("das das") zusammen, Satzanfang groß, Punkt am Ende.
 * Was inhaltlich gesagt wurde, bleibt unverändert.
 */
object NoteCleaner {
    private val fillersDe = listOf(
        "ä+h+m*", "ä+m+", "ö+h+m*", "e+h+m+", "h+m+", "m+h+m+", "quasi", "sozusagen", "halt",
    )
    private val fillersEn = listOf("u+h+m*", "u+m+", "e+r+m*", "h+m+", "m+h+m+", "you know", "basically")
    private val startersDe = listOf("also", "ja", "okay", "ok", "genau", "naja", "na ja")
    private val startersEn = listOf("okay", "ok", "well", "yeah", "so yeah")

    /**
     * "Aufräumen" ohne KI: jeder Satz bzw. jede Zeile wird ein Stichpunkt, Füllwörter raus,
     * Formeln als Zeichen ([SpokenMath]), Satzanfang groß.
     */
    fun tidy(raw: String, german: Boolean): String {
        val parts = raw.split(Regex("\\n+"))
            .map { it.trim().removePrefix("•").removePrefix("-").removePrefix("*").trim() }
            .flatMap { it.split(Regex("(?<=[.!?])\\s+")) }
        return parts
            .map { clean(SpokenMath.convert(it, german), german).trimEnd('.') }
            .filter { it.isNotBlank() }
            .joinToString("\n") { "• $it" }
    }

    /**
     * "Aufräumen" ohne KI, Fließtext-Version: Füllwörter und doppelte Wörter raus, Formeln als Zeichen,
     * Satzanfänge groß. Die Zeilen eines Absatzes werden zusammengezogen, Leerzeilen bleiben Absätze.
     */
    fun tidyText(raw: String, german: Boolean): String =
        raw.split(Regex("\\n\\s*\\n+"))
            .map { para ->
                para.split(Regex("\\n+"))
                    .map { it.trim().removePrefix("•").removePrefix("-").removePrefix("*").trim() }
                    .filter { it.isNotBlank() }
                    .map { clean(SpokenMath.convert(it, german), german) }
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
            }
            .filter { it.isNotBlank() }
            .joinToString("\n\n")

    fun clean(raw: String, german: Boolean): String {
        var t = " " + raw.trim() + " "
        val fillers = if (german) fillersDe else fillersEn
        for (f in fillers) {
            // Wörter wie "halt" nur klein geschrieben (groß ist es eher ein Name: "Halt und Haltung")
            val flags = if (f.first().isLetter() && f.all { it.isLetter() || it == ' ' }) "" else "(?i)"
            t = t.replace(Regex("$flags(?<=[\\s,.!?])$f(?=[\\s,.!?])[,]?"), " ")
        }
        // Wörter am Satzanfang, die nur Anlauf sind ("Also, ...", "Okay ...")
        val starters = if (german) startersDe else startersEn
        repeat(2) {
            for (st in starters) {
                t = t.replace(Regex("(?i)(^|[.!?]\\s+)\\s*" + Regex.escape(st) + "\\s*,?\\s+"), "$1")
            }
        }
        // doppelte Wörter direkt hintereinander
        t = t.replace(Regex("(?i)\\b(\\p{L}+)(\\s+\\1\\b)+"), "$1")
        // Leerzeichen und Satzzeichen ordnen
        t = t.replace(Regex("\\s+"), " ").replace(Regex("\\s+([,.!?;:])"), "$1").replace(Regex(",+"), ",")
            .replace(Regex("^[\\s,]+"), "").trim().trimEnd(',')
        if (t.isEmpty()) return ""
        // Satzanfänge groß
        val sb = StringBuilder(t)
        var cap = true
        for (i in sb.indices) {
            val c = sb[i]
            if (cap && c.isLetter()) { sb.setCharAt(i, c.uppercaseChar()); cap = false }
            else if (c == '.' || c == '!' || c == '?') cap = true
        }
        t = sb.toString()
        if (t.last() !in ".!?") t += "."
        return t
    }
}
