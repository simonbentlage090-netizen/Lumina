package de.funke.app

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Merkt sich den letzten Absturz (Fehlermeldung und Stelle im Code) in einer kleinen Datei auf dem Handy.
 * Beim nächsten Start steht er unter Einstellungen → Über Lumina und lässt sich teilen. Nichts geht
 * automatisch irgendwohin.
 */
object CrashLog {
    private const val FILE = "last_crash.txt"

    fun install(context: Context) {
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        if (previous is Recorder) return
        Thread.setDefaultUncaughtExceptionHandler(Recorder(app, previous))
    }

    fun read(context: Context): String? =
        runCatching { File(context.filesDir, FILE).takeIf { it.exists() }?.readText() }.getOrNull()?.takeIf { it.isNotBlank() }

    fun clear(context: Context) {
        runCatching { File(context.filesDir, FILE).delete() }
    }

    private class Recorder(private val app: Context, private val previous: Thread.UncaughtExceptionHandler?) :
        Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            runCatching {
                val version = runCatching { app.packageManager.getPackageInfo(app.packageName, 0).versionName }.getOrNull() ?: "?"
                val text = buildString {
                    append("Lumina ").append(version)
                    append(" · Android ").append(Build.VERSION.RELEASE).append(" (").append(Build.VERSION.SDK_INT).append(")")
                    append(" · ").append(Build.MANUFACTURER).append(' ').append(Build.MODEL).append('\n')
                    append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append(" · Thread ").append(t.name).append("\n\n")
                    append(Log.getStackTraceString(e).take(12_000))
                }
                File(app.filesDir, FILE).writeText(text)
            }
            previous?.uncaughtException(t, e)
        }
    }
}
