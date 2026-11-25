package de.funke.app.books

import de.funke.app.notes.AiCleanup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Kapitel mit der Lumina-KI erkennen (seit 13.4).
 *
 * Geschickt wird NICHT das ganze Buch, sondern nur der Anfang jeder Seite (PDF) bzw. jeder Überschrift
 * und jedes Abschnitts (EPUB), jeweils mit einer Nummer. Die KI antwortet mit Zeilen
 * "Ebene|Nummer|Titel". Daraus werden Kapitel und Unterkapitel.
 */
object ChapterAi {
    /** Höchstens so viele Zeichen gehen an den Server (passt zum Worker, MAX_CHAPTER_CHARS). */
    const val MAX_CHARS = 24_000

    class Candidate(val id: Int, val section: Int, val block: Int, val text: String)

    sealed class Outcome {
        class Ok(val chapters: List<Chapter>) : Outcome()
        /** [reason] wie beim Notiz-Aufräumen (device_limit, budget, ai_error, …) oder "no_text"/"offline"/"empty". */
        class Failed(val reason: String) : Outcome()
    }

    /** PDF: eine Zeile je Seite. */
    fun pdfCandidates(starts: List<String>): List<Candidate> {
        val per = (MAX_CHARS / starts.size.coerceAtLeast(1) - 8).coerceIn(24, 140)
        return starts.mapIndexedNotNull { i, t -> t.take(per).takeIf { it.isNotBlank() }?.let { Candidate(i + 1, i, 0, it) } }
    }

    /** EPUB: jede Überschrift und der erste Absatz jedes Abschnitts. */
    fun epubCandidates(book: EpubBook): List<Candidate> {
        val out = ArrayList<Candidate>()
        var id = 1
        book.sections.forEachIndexed { s, sec ->
            sec.blocks.forEachIndexed { b, block ->
                val heading = block.kind == BlockKind.H1 || block.kind == BlockKind.H2 || block.kind == BlockKind.H3
                if (b == 0 || heading || (block.text.length < 70 && b < 3)) {
                    val tag = when (block.kind) { BlockKind.H1 -> "[H1] "; BlockKind.H2 -> "[H2] "; BlockKind.H3 -> "[H3] "; else -> "" }
                    out += Candidate(id++, s, b, tag + block.text.replace('\n', ' ').take(100))
                }
            }
        }
        // Zu lang? Dann nur die Überschriften und die Abschnittsanfänge
        if (out.sumOf { it.text.length + 8 } > MAX_CHARS) {
            return out.filter { it.block == 0 || it.text.startsWith("[H") }.let { list ->
                val per = (MAX_CHARS / list.size.coerceAtLeast(1) - 8).coerceIn(20, 100)
                list.map { Candidate(it.id, it.section, it.block, it.text.take(per)) }
            }
        }
        return out
    }

    fun buildInput(candidates: List<Candidate>): String {
        val sb = StringBuilder()
        for (c in candidates) {
            if (sb.length + c.text.length + 8 > MAX_CHARS) break
            sb.append(c.id).append(": ").append(c.text).append('\n')
        }
        return sb.toString()
    }

    /** Antwortzeilen "Ebene|Nummer|Titel" in Kapitel umwandeln. Unbekannte Nummern fallen weg. */
    fun parse(reply: String, candidates: List<Candidate>): List<Chapter> {
        val byId = candidates.associateBy { it.id }
        val out = ArrayList<Chapter>()
        for (raw in reply.lines()) {
            val line = raw.trim().trimStart('-', '•', '*', ' ')
            val parts = line.split('|').map { it.trim() }
            if (parts.size < 3) continue
            val level = parts[0].filter { it.isDigit() }.toIntOrNull()?.coerceIn(1, 3) ?: continue
            val id = parts[1].filter { it.isDigit() }.toIntOrNull() ?: continue
            val title = parts.drop(2).joinToString(" | ").trim().trim('"').take(140)
            val c = byId[id] ?: continue
            if (title.isEmpty()) continue
            out += Chapter(title, level, c.section, c.block)
        }
        // In Lesereihenfolge, ohne Doppelte; das erste Kapitel ist immer Ebene 1
        val sorted = out.distinctBy { Triple(it.section, it.block, it.title) }.sortedWith(compareBy({ it.section }, { it.block }))
        return sorted.mapIndexed { i, ch -> if (i == 0 && ch.level > 1) ch.copy(level = 1) else ch }
    }

    suspend fun detect(input: String, candidates: List<Candidate>, title: String, german: Boolean, server: String, device: String): Outcome =
        withContext(Dispatchers.IO) {
            if (candidates.size < 2 || input.isBlank()) return@withContext Outcome.Failed("no_text")
            val url = server.trim().trimEnd('/').ifBlank { AiCleanup.DEFAULT_SERVER }
            if (!url.startsWith("https://")) return@withContext Outcome.Failed("offline")
            val body = JSONObject()
                .put("text", input)
                .put("title", title.take(160))
                .put("device", device)
                .put("lang", if (german) "de" else "en")
                .put("mode", "chapters")
            val result = runCatching {
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 12_000
                    readTimeout = 110_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("x-lumina", AiCleanup.APP_TOKEN)
                }
                try {
                    conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                    val code = conn.responseCode
                    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                    code to stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                } finally {
                    conn.disconnect()
                }
            }
            val (code, text) = result.getOrNull() ?: return@withContext Outcome.Failed("offline")
            val json = runCatching { JSONObject(text) }.getOrNull() ?: return@withContext Outcome.Failed("ai_error")
            if (code !in 200..299 || json.optBoolean("fallback")) {
                return@withContext Outcome.Failed(json.optString("reason").ifBlank { if (code == 404) "old_server" else "ai_error" })
            }
            // Ein Worker vor Version 4 kennt "chapters" nicht und hätte den Text wie eine Notiz aufgeräumt
            if (json.optString("kind") != "chapters") return@withContext Outcome.Failed("old_server")
            val chapters = parse(json.optString("text"), candidates)
            if (chapters.isEmpty()) Outcome.Failed("empty") else Outcome.Ok(chapters)
        }
}
