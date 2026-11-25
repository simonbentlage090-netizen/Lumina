package de.funke.app.books

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Wie oft die Lumina-KI Kapitel suchen darf (seit 15.2): für höchstens drei verschiedene Bücher in sieben Tagen.
 *
 *  - Gezählt wird nur, was geklappt hat. Schlägt die KI fehl, kostet das nichts.
 *  - Dasselbe Buch noch einmal ("Neu suchen") zählt innerhalb der sieben Tage nicht doppelt.
 *  - Ist das Kontingent aufgebraucht, findet Lumina die Kapitel ohne KI aus dem Text ([LocalChapters]).
 *
 * Liegt nur auf dem Handy (in den App-Einstellungen), es geht nichts zusätzlich an den Server.
 */
object ChapterQuota {
    const val PER_WEEK = 3
    private const val WEEK = 7L * 24 * 60 * 60 * 1000
    private const val KEY = "chapter_ai_uses"

    private class Use(val book: String, val at: Long)

    private fun prefs(context: Context) = context.applicationContext.getSharedPreferences("funke", Context.MODE_PRIVATE)

    /** Die Nutzungen der letzten sieben Tage (ältere fallen weg). Kaputte Daten zählen als leer. */
    private fun recent(context: Context, now: Long): List<Use> {
        val raw = runCatching { prefs(context).getString(KEY, null) }.getOrNull() ?: return emptyList()
        val arr = runCatching { JSONArray(raw) }.getOrNull() ?: return emptyList()
        val out = ArrayList<Use>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val id = o.optString("id")
            val at = o.optLong("at")
            if (id.isNotEmpty() && at in (now - WEEK)..now) out += Use(id, at)
        }
        return out
    }

    /** Darf die KI für dieses Buch suchen? Ja, wenn es diese Woche schon gezählt ist oder noch Platz ist. */
    fun allows(context: Context, bookId: String, now: Long = System.currentTimeMillis()): Boolean {
        val uses = recent(context, now)
        return uses.any { it.book == bookId } || uses.map { it.book }.distinct().size < PER_WEEK
    }

    /** Noch frei in den letzten sieben Tagen (0 bis 3). */
    fun left(context: Context, now: Long = System.currentTimeMillis()): Int =
        (PER_WEEK - recent(context, now).map { it.book }.distinct().size).coerceIn(0, PER_WEEK)

    /** Eine erfolgreiche KI-Suche merken. */
    fun note(context: Context, bookId: String, now: Long = System.currentTimeMillis()) {
        val old = recent(context, now)
        // Schon gezählt: Der Zeitpunkt bleibt, sonst würde "Neu suchen" das Buch immer wieder verlängern
        val uses = if (old.any { it.book == bookId }) old else old + Use(bookId, now)
        val arr = JSONArray()
        uses.forEach { arr.put(JSONObject().put("id", it.book).put("at", it.at)) }
        runCatching { prefs(context).edit().putString(KEY, arr.toString()).apply() }
    }
}
