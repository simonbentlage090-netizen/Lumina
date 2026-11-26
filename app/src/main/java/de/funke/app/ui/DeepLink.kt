package de.funke.app.ui

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Eine Denkpause, die die Bedienungshilfe angefordert hat: vor welcher App und warum.
 * [preview] = nur zum Ausprobieren aus dem Reiter "Denkpause" (seit 15.2): zählt nichts, öffnet keine App.
 */
data class GateRequest(val pkg: String, val checkIn: Boolean, val at: Long = System.nanoTime(), val preview: Boolean = false)

/**
 * Eine Erinnerung als Lumi-Bildschirm (seit 13.4). [fromOutside] = Lumina wurde dafür von außen nach vorn geholt
 * (dann schieben "Später" und "Nicht jetzt" die App wieder nach hinten); false = Probe aus der App heraus.
 */
data class ReminderRequest(
    val shown: de.funke.app.notify.Reminders.Shown,
    val fromOutside: Boolean,
    val at: Long = System.nanoTime(),
)

/**
 * Sprungziel von außen (Widget, Benachrichtigung, Bedienungshilfe): welcher Reiter beim Öffnen gezeigt
 * werden soll, oder ob eine Denkpause ansteht. Der Hauptbildschirm springt hin und setzt den Wert zurück.
 */
object DeepLink {
    const val EXTRA_TAB = "de.funke.app.extra.TAB"
    const val EXTRA_GATE_PKG = "de.funke.app.extra.GATE_PKG"
    const val EXTRA_GATE_CHECKIN = "de.funke.app.extra.GATE_CHECKIN"
    /** Vom Widget: gleich die Notizen öffnen. */
    const val EXTRA_NOTES = "de.funke.app.extra.NOTES"

    /**
     * Ausweis für Sprünge, die Lumina selbst auslöst (Denkpause, Erinnerung als Bildschirm). MainActivity ist als
     * Startbildschirm für alle Apps erreichbar; ohne diesen Ausweis könnte eine fremde App Lumina eine Denkpause oder
     * eine erfundene "Erinnerung" anzeigen lassen. Zufällig pro Installation, verlässt das Gerät nie (seit 15.7).
     */
    private const val EXTRA_PASS = "de.funke.app.extra.PASS"
    private var pass: String? = null

    private fun pass(ctx: Context): String = pass ?: synchronized(this) {
        pass ?: run {
            val prefs = ctx.applicationContext.getSharedPreferences("guard", Context.MODE_PRIVATE)
            prefs.getString("pass", null) ?: java.util.UUID.randomUUID().toString().also {
                prefs.edit().putString("pass", it).apply()
            }
        }.also { pass = it }
    }

    /** Hängt den Ausweis an einen Intent, den Lumina selbst an MainActivity schickt. */
    fun sign(ctx: Context, intent: Intent): Intent = intent.putExtra(EXTRA_PASS, pass(ctx))

    private fun signed(ctx: Context, intent: Intent): Boolean = intent.getStringExtra(EXTRA_PASS) == pass(ctx)

    /** -1 = kein Sprung. */
    var tab by mutableIntStateOf(-1)

    /** Anstehende Denkpause (null = keine). */
    var gate by mutableStateOf<GateRequest?>(null)

    /** Von einer Erinnerung "Bildschirm": der Inhalt als JSON (siehe Reminders.Shown). */
    const val EXTRA_REMINDER = "de.funke.app.extra.REMINDER"

    /** Anstehende Erinnerung als Lumi-Bildschirm (null = keine). */
    var reminder by mutableStateOf<ReminderRequest?>(null)

    /** Fortschritt-Reiter: 0 = Lesen, 1 = Bildschirmzeit (-1 = keine Vorgabe). */
    var progressSection by mutableIntStateOf(-1)

    /** Denkpause-Reiter: 0 = Fragen, 1 = Erinnerungen (-1 = keine Vorgabe). */
    var gateSection by mutableIntStateOf(-1)

    /** Notizen von oben öffnen (vom Widget oder aus einer Benachrichtigung). */
    var notes by mutableStateOf(false)

    fun consume(ctx: Context, intent: Intent?) {
        if (intent == null) return
        // Reiter und Notizen öffnen ist harmlos; Denkpause und Erinnerung nur mit Luminas eigenem Ausweis.
        val trusted = signed(ctx, intent)
        intent.removeExtra(EXTRA_PASS)
        val t = intent.getIntExtra(EXTRA_TAB, -1)
        if (t >= 0) {
            tab = t
            intent.removeExtra(EXTRA_TAB)
        }
        val pkg = intent.getStringExtra(EXTRA_GATE_PKG)
        if (!pkg.isNullOrBlank() && trusted) {
            // Steht schon eine Frage da, bleibt sie stehen (sonst käme alle paar Sekunden eine neue)
            if (gate == null) gate = GateRequest(pkg, intent.getBooleanExtra(EXTRA_GATE_CHECKIN, false))
        }
        intent.removeExtra(EXTRA_GATE_PKG)
        intent.getStringExtra(EXTRA_REMINDER)?.takeIf { trusted }?.let { json ->
            runCatching { de.funke.app.notify.Reminders.Shown.fromJson(org.json.JSONObject(json)) }.getOrNull()
                ?.let { reminder = ReminderRequest(it, fromOutside = true) }
        }
        intent.removeExtra(EXTRA_REMINDER)
        if (intent.getBooleanExtra(EXTRA_NOTES, false)) {
            notes = true
            intent.removeExtra(EXTRA_NOTES)
        }
    }
}
