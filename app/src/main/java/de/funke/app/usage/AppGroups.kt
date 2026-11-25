package de.funke.app.usage

import androidx.compose.runtime.Immutable
import de.funke.app.ui.tr
import org.json.JSONArray
import org.json.JSONObject

/**
 * Gruppen für die Bildschirmzeit ("Videos", "Social Media", "Chats", …).
 * Jede App landet automatisch in einer Gruppe: erst nach einer Liste bekannter Apps, dann nach der Kategorie,
 * die die App selbst angibt, dann nach Stichwörtern im Namen. Man kann jede App von Hand umsortieren,
 * Gruppen umbenennen und eigene Gruppen anlegen. (Ein KI-Modus zum Sortieren kommt später.)
 */
@Immutable
data class AppGroup(
    val id: String,
    /** Eigener Name; null = Standardname in der gewählten Sprache. */
    val customName: String?,
    /** Symbol (siehe GroupIcon), z. B. "video", "chat", "star". */
    val icon: String,
    val color: Long,
    /** Von der App vorgegeben (kann umbenannt, aber nicht gelöscht werden). */
    val builtIn: Boolean,
) {
    val name: String get() = customName ?: AppGroups.defaultName(id)
}

/** Gespeicherte Einstellungen: alle Gruppen und die Apps, die man von Hand zugeordnet hat. */
@Immutable
data class GroupConfig(
    val groups: List<AppGroup>,
    val assign: Map<String, String>,
) {
    /** Nie ein Absturz, auch wenn gespeicherte Daten unvollständig sind: notfalls "Sonstiges". */
    fun group(id: String): AppGroup = groups.firstOrNull { it.id == id }
        ?: groups.firstOrNull { it.id == AppGroups.OTHER }
        ?: AppGroup(AppGroups.OTHER, null, AppGroups.defaultIcon(AppGroups.OTHER), 0xFF9A96A8, builtIn = true)

    fun toJson(): String {
        val o = JSONObject()
        o.put("groups", JSONArray().apply {
            groups.forEach { g ->
                put(JSONObject().apply {
                    put("id", g.id)
                    if (g.customName != null) put("name", g.customName)
                    put("icon", g.icon)
                    put("color", g.color)
                    put("builtIn", g.builtIn)
                })
            }
        })
        o.put("assign", JSONObject().apply { assign.forEach { (k, v) -> put(k, v) } })
        return o.toString()
    }

    companion object {
        fun fromJson(json: String?): GroupConfig {
            val base = AppGroups.defaults()
            if (json.isNullOrBlank()) return GroupConfig(base, emptyMap())
            return runCatching {
                val o = JSONObject(json)
                val arr = o.getJSONArray("groups")
                val saved = List(arr.length()) { i ->
                    val g = arr.getJSONObject(i)
                    AppGroup(
                        id = g.getString("id"),
                        customName = if (g.has("name")) g.getString("name") else null,
                        // bis 8.4 waren es Emojis: dann das passende Symbol nehmen
                        icon = g.optString("icon", "").ifEmpty { AppGroups.defaultIcon(g.getString("id")) },
                        color = g.optLong("color", 0xFF9A96A8),
                        builtIn = g.optBoolean("builtIn", false),
                    )
                }
                // neue eingebaute Gruppen aus späteren Versionen ergänzen
                val merged = saved + base.filter { b -> saved.none { it.id == b.id } }
                val a = o.optJSONObject("assign")
                val assign = HashMap<String, String>()
                a?.keys()?.forEach { k -> assign[k] = a.getString(k) }
                GroupConfig(merged, assign.filterValues { v -> merged.any { it.id == v } })
            }.getOrElse { GroupConfig(base, emptyMap()) }
        }
    }
}

object AppGroups {
    const val OTHER = "other"

    private class Def(val id: String, val de: String, val en: String, val icon: String, val color: Long)

    private val Defs = listOf(
        Def("video", "Videos & Streaming", "Videos & streaming", "video", 0xFFFF5A5F),
        Def("social", "Social Media", "Social media", "social", 0xFFFF7AB6),
        Def("chat", "Chats & Anrufe", "Chats & calls", "chat", 0xFF4CC38A),
        Def("games", "Spiele", "Games", "games", 0xFFA66BFF),
        Def("music", "Musik, Podcasts & Hörbücher", "Music, podcasts & audiobooks", "music", 0xFF2EC4B6),
        Def("learn", "Lernen & Arbeit", "Learning & work", "learn", 0xFF5B8CFF),
        Def("browser", "Browser & Suche", "Browser & search", "browser", 0xFF6FA8FF),
        Def("news", "News & Lesen", "News & reading", "news", 0xFFB49BFF),
        Def("shopping", "Shopping & Essen", "Shopping & food", "shopping", 0xFFFFA94D),
        Def("finance", "Banking & Finanzen", "Banking & finance", "finance", 0xFF2ECC9A),
        Def("photo", "Fotos & Kamera", "Photos & camera", "photo", 0xFFFFC93C),
        Def("travel", "Karten & Reisen", "Maps & travel", "travel", 0xFF4DB6FF),
        Def("tools", "Werkzeuge & System", "Tools & system", "tools", 0xFF8E8A9E),
        Def(OTHER, "Sonstiges", "Other", "other", 0xFF9A96A8),
    )

    fun defaults(): List<AppGroup> = Defs.map { AppGroup(it.id, null, it.icon, it.color, builtIn = true) }

    fun defaultName(id: String): String = Defs.firstOrNull { it.id == id }?.let { tr(it.de, it.en) } ?: id

    fun defaultIcon(id: String): String = Defs.firstOrNull { it.id == id }?.icon ?: "star"

    /** Symbole und Farben zur Auswahl für eigene Gruppen. */
    val IconChoices = listOf("star", "heart", "bolt", "moon", "leaf", "ball", "brush", "food", "video", "social", "chat", "games", "music", "learn", "news", "shopping", "photo", "travel", "tools", "other")
    val ColorChoices = listOf(0xFFFF5A5F, 0xFFFF7AB6, 0xFFA66BFF, 0xFF5B8CFF, 0xFF2EC4B6, 0xFF4CC38A, 0xFFFFC93C, 0xFFFFA94D, 0xFF8E8A9E)

    /** Bekannte Apps (Paketname oder Anfang davon) → Gruppe. */
    private val Known: List<Pair<String, String>> = listOf(
        // Videos
        "com.google.android.youtube" to "video", "com.google.android.apps.youtube.kids" to "video",
        "com.netflix." to "video", "com.amazon.avod" to "video", "com.disney.disneyplus" to "video",
        "tv.twitch." to "video", "com.crunchyroll." to "video", "com.zdf." to "video", "de.ard." to "video",
        "com.wbd.stream" to "video", "com.vimeo." to "video", "com.lemon.lvoverseas" to "video",
        "com.amazon.amazonvideo" to "video", "de.prosiebensat1digital." to "video", "com.joyn." to "video", "de.rtl." to "video",
        "com.mubi" to "video", "com.plexapp" to "video", "org.videolan.vlc" to "video", "com.apple.atve" to "video",
        // Social Media
        "com.zhiliaoapp.musically" to "social", "com.ss.android.ugc." to "social", "com.instagram." to "social",
        "com.snapchat." to "social", "com.facebook.katana" to "social", "com.facebook.lite" to "social",
        "com.twitter.android" to "social", "com.reddit." to "social", "com.pinterest" to "social",
        "com.bereal." to "social", "com.linkedin." to "social", "com.tumblr" to "social",
        "org.joinmastodon." to "social", "xyz.blueskyweb." to "social",
        // Chats & Anrufe
        "com.whatsapp" to "chat", "org.telegram." to "chat", "org.thoughtcrime.securesms" to "chat",
        "com.discord" to "chat", "com.facebook.orca" to "chat", "com.google.android.apps.messaging" to "chat",
        "com.samsung.android.messaging" to "chat", "com.android.mms" to "chat", "com.google.android.dialer" to "chat",
        "com.samsung.android.dialer" to "chat", "com.android.dialer" to "chat", "com.android.contacts" to "chat",
        "com.samsung.android.app.contacts" to "chat", "com.google.android.contacts" to "chat", "ch.threema." to "chat",
        "com.viber." to "chat", "jp.naver.line." to "chat", "com.skype." to "chat", "com.google.android.apps.tachyon" to "chat",
        // Spiele
        "com.supercell." to "games", "com.mojang." to "games", "com.roblox." to "games", "com.king." to "games",
        "com.epicgames." to "games", "com.activision." to "games", "com.mihoyo." to "games", "com.miHoYo." to "games",
        "com.innersloth." to "games", "com.chess" to "games", "com.dts.freefire" to "games", "com.tencent.ig" to "games",
        "com.ea." to "games", "com.rovio." to "games", "com.nianticlabs." to "games", "com.kiloo." to "games",
        "com.outfit7." to "games", "com.playrix." to "games", "com.zynga." to "games", "com.ketchapp" to "games",
        // Musik & Podcasts
        "com.spotify." to "music", "com.google.android.apps.youtube.music" to "music", "com.amazon.mp3" to "music",
        "com.apple.android.music" to "music", "deezer." to "music", "com.soundcloud." to "music", "com.shazam." to "music",
        "com.google.android.apps.podcasts" to "music", "au.com.shiftyjelly.pocketcasts" to "music",
        "com.audible." to "music", "com.bookbeat." to "music", "com.storytel" to "music", "de.radio.android" to "music",
        "com.tidal" to "music", "com.qobuz" to "music",
        // Lernen & Arbeit
        "de.simonbentlage.lumina" to "learn", "de.funke.app" to "learn", "com.duolingo" to "learn", "com.ichi2.anki" to "learn", "com.google.android.apps.docs" to "learn",
        "com.microsoft.office." to "learn", "com.microsoft.teams" to "learn", "notion.id" to "learn",
        "com.google.android.apps.classroom" to "learn", "com.google.android.keep" to "learn", "com.anthropic.claude" to "learn",
        "com.openai.chatgpt" to "learn", "org.wikipedia" to "learn", "com.quizlet." to "learn", "com.microblink.photomath" to "learn",
        "com.google.android.calendar" to "learn", "com.samsung.android.calendar" to "learn", "us.zoom." to "learn",
        "com.Slack" to "learn", "com.slack" to "learn", "com.todoist" to "learn", "com.evernote" to "learn", "com.dropbox." to "learn",
        "com.adobe.reader" to "learn", "com.google.android.apps.translate" to "learn", "com.grupet.web.app" to "learn",
        "com.google.android.gm" to "learn", "com.microsoft.office.outlook" to "learn", "com.samsung.android.app.notes" to "learn",
        // Browser & Suche
        "com.android.chrome" to "browser", "org.mozilla." to "browser", "com.opera." to "browser", "com.microsoft.emmx" to "browser",
        "com.brave.browser" to "browser", "com.sec.android.app.sbrowser" to "browser", "com.duckduckgo." to "browser",
        "com.google.android.googlequicksearchbox" to "browser", "com.kiwibrowser." to "browser",
        // News & Lesen
        "com.google.android.apps.magazines" to "news", "de.spiegel." to "news", "de.tagesschau" to "news",
        "flipboard." to "news", "com.amazon.kindle" to "news", "de.tolino." to "news", "com.medium." to "news",
        // Shopping & Essen
        "com.amazon.mShop" to "shopping", "com.ebay." to "shopping", "de.zalando." to "shopping", "com.einnovation.temu" to "shopping",
        "com.zzkko" to "shopping", "com.alibaba.aliexpress" to "shopping", "com.vinted" to "shopping", "com.mcdonalds." to "shopping",
        // Banking & Finanzen
        "com.paypal." to "finance", "de.number26." to "finance", "de.traderepublic." to "finance", "com.starfinanz." to "finance",
        "de.dkb." to "finance", "de.comdirect." to "finance", "de.consorsbank" to "finance", "de.ingdiba." to "finance",
        "de.commerzbanking." to "finance", "com.revolut." to "finance", "com.klarna." to "finance", "de.postbank." to "finance",
        "com.google.android.apps.walletnfcrel" to "finance", "com.coinbase." to "finance", "com.bitpanda." to "finance",
        // Fotos & Kamera
        "com.google.android.apps.photos" to "photo", "com.sec.android.gallery3d" to "photo", "com.sec.android.app.camera" to "photo",
        "com.android.camera" to "photo", "com.google.android.GoogleCamera" to "photo", "com.vsco." to "photo",
        "com.picsart." to "photo", "com.adobe.lrmobile" to "photo", "com.canva." to "photo",
        // Karten & Reisen
        "com.google.android.apps.maps" to "travel", "de.hafas.android" to "travel", "com.ubercab" to "travel", "com.waze" to "travel",
        "com.booking" to "travel", "com.airbnb." to "travel", "de.meinfernbus" to "travel",
        // Werkzeuge & System
        "com.android.settings" to "tools", "com.sec.android.app.myfiles" to "tools", "com.google.android.apps.nbu.files" to "tools",
        "com.android.vending" to "tools", "com.google.android.deskclock" to "tools", "com.sec.android.app.clockpackage" to "tools",
        "com.android.calculator" to "tools", "com.sec.android.app.popupcalculator" to "tools", "com.google.android.calculator" to "tools",
        "com.google.android.apps.authenticator" to "tools", "com.samsung.android.lool" to "tools", "com.sec.android.app.samsungapps" to "tools",
        "com.google.android.apps.wellbeing" to "tools",
    )

    private val Keywords: List<Pair<List<String>, String>> = listOf(
        listOf("bank", "sparkasse", "paypal", "finanz", "broker", "invest", "wallet", "volksbank") to "finance",
        listOf("game", "spiel", "puzzle", "racing", "craft") to "games",
        listOf("chat", "messenger", "messag", "telefon", "phone", "dialer") to "chat",
        listOf("video", "stream", "mediathek", "movie", "film", "serien", "joyn", "prime") to "video",
        listOf("music", "musik", "radio", "podcast", "audio", "hörbuch", "audiobook", "hörspiel") to "music",
        listOf("camera", "kamera", "gallery", "galerie", "photo", "foto") to "photo",
        listOf("maps", "karte", "navi", "bahn", "transit", "ticket", "flug", "flight") to "travel",
        listOf("news", "nachricht", "zeitung", "magazin") to "news",
        listOf("shop", "kaufen", "lieferando", "delivery", "food") to "shopping",
        listOf("browser") to "browser",
        listOf("learn", "lernen", "school", "schule", "study", ".edu", "office", "docs", "notes", "notiz", "vokabel", "untis") to "learn",
    )

    /**
     * Automatische Gruppe einer App. [category] ist ApplicationInfo.category (-1 = unbekannt).
     * Reihenfolge: bekannte App → Kategorie der App → Stichwort im Paket- oder App-Namen → Sonstiges.
     */
    fun classify(pkg: String, label: String, category: Int): String {
        Known.firstOrNull { (prefix, _) -> pkg.startsWith(prefix) }?.let { return it.second }
        when (category) {
            0 -> return "games"     // CATEGORY_GAME
            1 -> return "music"     // CATEGORY_AUDIO
            2 -> return "video"     // CATEGORY_VIDEO
            3 -> return "photo"     // CATEGORY_IMAGE
            4 -> return "social"    // CATEGORY_SOCIAL
            5 -> return "news"      // CATEGORY_NEWS
            6 -> return "travel"    // CATEGORY_MAPS
            7 -> return "learn"     // CATEGORY_PRODUCTIVITY
        }
        val hay = (pkg + " " + label).lowercase()
        Keywords.firstOrNull { (words, _) -> words.any { it in hay } }?.let { return it.second }
        if (pkg.startsWith("com.android.") || pkg.startsWith("com.samsung.") || pkg.startsWith("com.sec.") || pkg.startsWith("com.google.android.")) return "tools"
        return OTHER
    }
}
