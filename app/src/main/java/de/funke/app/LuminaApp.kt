package de.funke.app

import android.app.Application

/**
 * Startet vor allem anderen im Prozess (seit 13.4): Das Absturzprotokoll gilt so auch für die Sperre
 * (Bedienungshilfe), die Erinnerungen und die Widgets, nicht nur für die sichtbare App.
 * Einstellungen → Über Lumina → Letzter Absturz zeigt es an.
 */
class LuminaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLog.install(this)
    }
}
