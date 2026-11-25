package de.funke.app.notes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Aufräumen mit KI (freiwillig).
 *
 *  - **Lumina-KI** (Standard seit 9.4): Lumina schickt die Notiz an den eigenen kleinen Cloudflare-Worker
 *    (Code in `server/lumina-ki-worker.js`). Der prüft Limits, lässt ein offenes Modell (gpt-oss) aufräumen
 *    und schickt Stichpunkte zurück. Gratis, ohne Anmeldung. Ist ein Limit erreicht, sagt der Worker
 *    "fallback" und Lumina räumt auf dem Handy auf.
 *  - **Aus**: nur auf dem Handy ([NoteCleaner.tidy] / [NoteCleaner.tidyText]).
 *
 * Seit 9.6 gibt es keinen "eigenen Schlüssel" mehr (Sicherheitsrisiko, braucht niemand), und man wählt
 * die Form: [Format.Text] (bereinigter Fließtext mit Absätzen) oder [Format.Bullets] (echte Stichpunkte).
 * Die genaue Anweisung an die KI steht im Worker (`server/lumina-ki-worker.js`), damit sie sich ohne
 * neue App-Version verbessern lässt.
 *
 * Die Server-Adresse lässt sich in den Einstellungen ändern (eigener Worker, z. B. für F-Droid-Builds).
 */
object AiCleanup {
    /** Standard-Adresse des Lumina-KI-Workers. */
    const val DEFAULT_SERVER = "https://lumina-ki.simon-bentlage.workers.dev"

    /**
     * App-Kennwort für den Worker (Header "x-lumina"). Kein echtes Geheimnis – es steht im Open-Source-Code.
     * Es hält nur Gelegenheitsnutzer fern; den echten Schutz machen die Limits und die Kostenbremse im Worker.
     */
    const val APP_TOKEN = "ZQNVyCXDDOf49YobQPh3rclsZ3516tC3"

    enum class Provider(val id: String, val label: String) {
        Off("off", "–"),
        Lumina("lumina", "Lumi");

        companion object {
            /** Unbekannt oder alt (OpenAI, Groq, Mistral) → Lumina-KI. Nur "off" bleibt aus. */
            fun byId(id: String?): Provider = if (id == "off") Off else Lumina
        }
    }

    /** Form des Ergebnisses. */
    enum class Format(val id: String) {
        Text("text"), Bullets("bullets");

        companion object {
            fun byId(id: String?): Format = if (id == "bullets") Bullets else Text
        }
    }

    /** Antwort von Lumina-KI: entweder aufgeräumter Text oder ein Grund, lokal aufzuräumen. */
    class LuminaReply(
        val text: String?,
        val fallbackReason: String?,
        val left: Int?,
        val model: String?,
        /** Was genau schiefging (vom Worker, für die Fehlersuche) */
        val detail: String? = null,
        /** Welche Stufe geantwortet hat: "big", "mid" oder "small" (ältere Worker: null) */
        val tier: String? = null,
        /** Wie oft heute noch das beste Modell drankommt */
        val best: Int? = null,
    )

    /** Name der Stufe für die Anzeige. */
    fun tierLabel(tier: String?, model: String?, german: Boolean): String? {
        fun t(de: String, en: String) = if (german) de else en
        val k = tier ?: when {
            model == null -> null
            model.contains("120b") -> "big"
            model.contains("20b") -> "mid"
            else -> "small"
        }
        return when (k) {
            "big" -> t("Beste KI", "Best AI")
            "mid" -> t("Standard-KI", "Standard AI")
            "small" -> t("Spar-KI", "Lite AI")
            else -> null
        }
    }

    /** Stand eines Modells laut /health. */
    class ModelStatus(val key: String, val id: String, val ok: Boolean, val ms: Int, val error: String?)

    /** Ergebnis von "Lumina-KI testen". */
    class Health(val ok: Boolean, val version: Int, val reason: String?, val models: List<ModelStatus>)

    /**
     * Kurzer, verständlicher Grund für die Anzeige (z. B. "Kennwort falsch"). Steht immer dabei, damit man
     * bei Problemen sofort sieht, woran es liegt.
     */
    fun reasonLabel(reason: String?, german: Boolean): String {
        fun t(de: String, en: String) = if (german) de else en
        return when {
            reason == null -> t("unbekannt", "unknown")
            reason == "device_limit" -> t("Tageslimit erreicht", "daily limit reached")
            reason == "budget" -> t("Tages-Kontingent leer", "daily quota used up")
            reason == "forbidden" -> t("App-Kennwort passt nicht (403)", "app password doesn't match (403)")
            reason == "no_token" -> t("Secret APP_TOKEN fehlt im Worker", "secret APP_TOKEN missing in the worker")
            reason == "no_ai_binding" -> t("Binding AI fehlt im Worker", "binding AI missing in the worker")
            reason == "no_kv_binding" -> t("Binding LIMITS fehlt im Worker", "binding LIMITS missing in the worker")
            reason == "ai_error" -> t("KI-Modell antwortet nicht", "AI model doesn't answer")
            reason == "empty" -> t("KI-Antwort war leer", "AI answer was empty")
            reason == "http_404" -> t("Worker nicht gefunden (404)", "worker not found (404)")
            reason.startsWith("http_5") -> t("Worker-Fehler (${reason.removePrefix("http_")})", "worker error (${reason.removePrefix("http_")})")
            reason.startsWith("http_") -> t("Fehler ${reason.removePrefix("http_")}", "error ${reason.removePrefix("http_")}")
            reason == "offline" -> t("kein Netz", "offline")
            reason == "old_worker" -> t("Worker veraltet – bitte den neuen Code einfügen", "worker outdated – please paste the new code")
            else -> reason
        }
    }

    /**
     * Lumina-KI fragen. Netzfehler kommen als Result.failure zurück, Limits und Serverfehler als
     * [LuminaReply.fallbackReason]. Bei einem kurzen Netz-Aussetzer wird einmal nachgefragt.
     */
    suspend fun tidyLumina(text: String, german: Boolean, server: String, device: String, format: Format): Result<LuminaReply> = withContext(Dispatchers.IO) {
        val first = runCatching { post(text, german, server, device, format) }
        if (first.isSuccess || first.exceptionOrNull() is IllegalArgumentException) return@withContext first
        kotlinx.coroutines.delay(800)
        runCatching { post(text, german, server, device, format) }
    }

    private fun post(text: String, german: Boolean, server: String, device: String, format: Format): LuminaReply {
        val url = server.trim().trimEnd('/').ifBlank { DEFAULT_SERVER }
        require(url.startsWith("https://")) { "https only" }
        val body = JSONObject()
            .put("text", text)
            .put("device", device)
            .put("lang", if (german) "de" else "en")
            .put("mode", format.id)
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 12_000
            readTimeout = 75_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-lumina", APP_TOKEN)
        }
        try {
            conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            return parseLumina(code, response)
        } finally {
            conn.disconnect()
        }
    }

    /** "Lumina-KI testen": fragt /health beim Worker ab (prüft Kennwort, Bindings und jedes Modell). */
    suspend fun health(server: String): Result<Health> = withContext(Dispatchers.IO) {
        runCatching {
            val url = server.trim().trimEnd('/').ifBlank { DEFAULT_SERVER } + "/health"
            require(url.startsWith("https://")) { "https only" }
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 12_000
                readTimeout = 90_000
                setRequestProperty("x-lumina", APP_TOKEN)
            }
            try {
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                parseHealth(code, response)
            } finally {
                conn.disconnect()
            }
        }
    }

    /** Antwort von /health auswerten. Alte Worker (Version 2) kennen /health nicht. */
    fun parseHealth(code: Int, response: String): Health {
        val o = runCatching { JSONObject(response) }.getOrNull()
        val version = o?.optInt("version", 0) ?: 0
        if (o == null || code !in 200..299) {
            val reason = o?.optString("reason")?.takeIf { it.isNotBlank() } ?: when {
                // Version 2 antwortet auf alles außer POST mit {"error":"Lumina-KI läuft.","version":2}
                o != null && version in 1..2 -> "old_worker"
                else -> "http_$code"
            }
            return Health(false, version, reason, emptyList())
        }
        if (version in 1..2 || !o.has("models")) return Health(false, version, "old_worker", emptyList())
        val models = o.optJSONObject("models")
        val list = buildList {
            models?.keys()?.forEach { k ->
                val m = models.optJSONObject(k) ?: return@forEach
                add(ModelStatus(k, m.optString("id"), m.optBoolean("ok"), m.optInt("ms"), m.optString("error").takeIf { it.isNotBlank() }))
            }
        }
        return Health(o.optBoolean("ok"), version, if (o.optBoolean("ok")) null else "ai_error", list)
    }

    /** Antwort des Workers auswerten (eigene Funktion, damit sie sich ohne Netz testen lässt). */
    fun parseLumina(code: Int, response: String): LuminaReply {
        val o = runCatching { JSONObject(response) }.getOrNull()
        val text = o?.optString("text")?.takeIf { it.isNotBlank() }
        val left = o?.takeIf { it.has("left") }?.optInt("left")
        val model = o?.optString("model")?.takeIf { it.isNotBlank() }
        val detail = o?.optString("detail")?.takeIf { it.isNotBlank() }
        val tier = o?.optString("tier")?.takeIf { it.isNotBlank() }
        val best = o?.takeIf { it.has("best") }?.optInt("best")
        return when {
            code in 200..299 && text != null -> LuminaReply(text.trim(), null, left, model, tier = tier, best = best)
            o?.optBoolean("fallback") == true -> LuminaReply(null, o.optString("reason").ifBlank { "unknown" }, left, null, detail)
            code == 403 -> LuminaReply(null, "forbidden", null, null)
            // Version 2 meldet "empty"/"ai_error" ohne Detail
            else -> LuminaReply(null, "http_$code", null, null, detail)
        }
    }
}
