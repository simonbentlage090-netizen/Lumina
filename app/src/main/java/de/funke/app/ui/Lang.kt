package de.funke.app.ui

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Sprache der App. Jeder Text steht direkt im Code in beiden Sprachen: `tr("Deutsch", "English")`.
 * So sieht man beim Lesen sofort beide Fassungen, und es kann keine Übersetzung fehlen.
 *
 * Die Sprache ist ein beobachteter Zustand: Wechselt sie, baut sich jede Ansicht, die einen Text
 * mit [tr] holt, von selbst neu auf.
 */
enum class AppLanguage(val id: String, val label: String, val locale: Locale) {
    English("en", "English", Locale.ENGLISH),
    German("de", "Deutsch", Locale.GERMAN);

    companion object {
        fun byId(id: String?): AppLanguage = entries.firstOrNull { it.id == id } ?: English
    }
}

object Lang {
    private const val KEY = "language"

    /** Standard: Sprache des Handys (Deutsch oder Englisch). */
    var current by mutableStateOf(AppLanguage.English)
        private set

    val german: Boolean get() = current == AppLanguage.German
    val locale: Locale get() = current.locale

    fun load(prefs: SharedPreferences?) {
        // Noch nie gewählt: wie das Handy (Deutsch, sonst Englisch)
        val saved = prefs?.getString(KEY, null)
        current = AppLanguage.byId(saved ?: if (Locale.getDefault().language == "de") "de" else "en")
    }

    fun set(language: AppLanguage, prefs: SharedPreferences?) {
        current = language
        prefs?.edit()?.putString(KEY, language.id)?.apply()
    }
}

/** Text in der gewählten Sprache. Erst Deutsch, dann Englisch. */
fun tr(de: String, en: String): String = if (Lang.german) de else en

/** "1 Tag" / "3 Tage" bzw. "1 day" / "3 days". */
fun daysText(n: Int): String = if (Lang.german) {
    if (n == 1) "1 Tag" else "$n Tage"
} else {
    if (n == 1) "1 day" else "$n days"
}

/** Nach "von"/"seit": "1 Tag" / "3 Tagen" bzw. "1 day" / "3 days". */
fun daysTextDative(n: Int): String = if (Lang.german) {
    if (n == 1) "1 Tag" else "$n Tagen"
} else {
    if (n == 1) "1 day" else "$n days"
}

/** Nur das Wort, ohne Zahl: "Tag"/"Tage" bzw. "day"/"days". */
fun daysWord(n: Int): String = if (Lang.german) {
    if (n == 1) "Tag" else "Tage"
} else {
    if (n == 1) "day" else "days"
}
