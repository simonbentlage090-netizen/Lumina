package de.funke.app.usage

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import java.time.LocalDate
import java.time.ZoneId

/**
 * Bildschirmzeit aus den Nutzungsdaten von Android (UsageStatsManager).
 * Dafür braucht Lumina die Sonderberechtigung "Nutzungsdatenzugriff", die man in den Einstellungen erteilt.
 * Die Daten verlassen das Handy nicht.
 */
object ScreenTime {
    // Ereignis-Typen als Zahlen, damit es auch auf älteren Android-Versionen läuft
    private const val RESUMED = 1          // ACTIVITY_RESUMED / MOVE_TO_FOREGROUND
    private const val PAUSED = 2           // ACTIVITY_PAUSED / MOVE_TO_BACKGROUND
    private const val SCREEN_OFF = 16      // SCREEN_NON_INTERACTIVE
    private const val KEYGUARD_SHOWN = 17
    private const val KEYGUARD_HIDDEN = 18 // Handy entsperrt
    private const val STOPPED = 23         // ACTIVITY_STOPPED
    private const val SHUTDOWN = 26        // DEVICE_SHUTDOWN

    /** Darf Lumina die Nutzungsdaten lesen? */
    fun hasAccess(context: Context): Boolean {
        val ops = context.getSystemService(AppOpsManager::class.java) ?: return false
        val mode = if (Build.VERSION.SDK_INT >= 29) {
            ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            @Suppress("DEPRECATION")
            ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        return if (mode == AppOpsManager.MODE_DEFAULT) {
            context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            mode == AppOpsManager.MODE_ALLOWED
        }
    }

    /** Öffnet die Einstellung "Nutzungsdatenzugriff" (wenn möglich direkt für Lumina). */
    fun openSettings(context: Context) {
        val direct = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            .setData(Uri.parse("package:" + context.packageName))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val list = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(direct) }.onFailure { runCatching { context.startActivity(list) } }
    }

    /** Beginn eines Tages in Millisekunden (Ortszeit). */
    fun dayStart(date: LocalDate): Long = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    /** Nutzung eines Tages. */
    class Day(val start: Long, val end: Long) {
        val apps = HashMap<String, Long>()
        var unlocks = 0
        var longestPkg: String? = null
        var longestMs = 0L
        val total: Long get() = apps.values.sum()
    }

    /**
     * Nutzung der letzten [days] Tage (der letzte ist heute), Tag für Tag.
     *
     * Gezählt wird aus den einzelnen Ereignissen, wie es auch das "Digitale Wohlbefinden" macht.
     * Wichtig: pro Activity statt pro App. Viele Apps (z. B. Video-Apps mit eigenem Player-Fenster) öffnen
     * mehrere Activities nacheinander; die App ist vorne, solange irgendeine davon vorne ist. Wer nur pro App
     * zählt, verliert sonst die Zeit im Player, wenn das alte Fenster erst danach "pausiert" meldet.
     */
    fun days(context: Context, days: Int): List<Day> {
        val today = LocalDate.now()
        val list = (days - 1 downTo 0).map { i ->
            val d = today.minusDays(i.toLong())
            Day(dayStart(d), dayStart(d.plusDays(1)))
        }
        val usm = context.getSystemService(UsageStatsManager::class.java) ?: return list
        val now = System.currentTimeMillis()
        val from = list.first().start
        val events = runCatching { usm.queryEvents(from - 3 * 60 * 60 * 1000L, now) }.getOrNull() ?: return list

        fun dayIndex(t: Long): Int {
            var lo = 0
            var hi = list.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) / 2
                when {
                    t < list[mid].start -> hi = mid - 1
                    t >= list[mid].end -> lo = mid + 1
                    else -> return mid
                }
            }
            return -1
        }

        fun add(pkg: String, a0: Long, b0: Long) {
            var a = maxOf(a0, from)
            val b = minOf(b0, now)
            while (a < b) {
                val i = dayIndex(a)
                if (i < 0) return
                val day = list[i]
                val e = minOf(b, day.end)
                val ms = e - a
                day.apps[pkg] = (day.apps[pkg] ?: 0L) + ms
                if (ms > day.longestMs) { day.longestMs = ms; day.longestPkg = pkg }
                a = e
            }
        }

        val openActs = HashMap<String, MutableSet<String>>()   // App -> offene Activities
        val since = HashMap<String, Long>()                     // App -> vorne seit
        fun closePkg(pkg: String, t: Long) {
            openActs.remove(pkg)
            val s = since.remove(pkg) ?: return
            add(pkg, s, t)
        }
        fun closeAll(t: Long) { for (pkg in since.keys.toList()) closePkg(pkg, t) }

        val e = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(e)
            val pkg = e.packageName ?: continue
            val act = pkg + "/" + (e.className ?: "")
            val t = e.timeStamp
            when (e.eventType) {
                RESUMED -> {
                    // Es ist (außer im geteilten Bildschirm) nur eine App vorne: andere Apps enden hier.
                    for (other in since.keys.toList()) if (other != pkg) closePkg(other, t)
                    openActs.getOrPut(pkg) { HashSet() }.add(act)
                    if (pkg !in since) since[pkg] = t
                }
                PAUSED, STOPPED -> {
                    val set = openActs[pkg] ?: continue
                    set.remove(act)
                    if (set.isEmpty()) closePkg(pkg, t)
                }
                SCREEN_OFF, KEYGUARD_SHOWN, SHUTDOWN -> closeAll(t)
                KEYGUARD_HIDDEN -> {
                    val i = dayIndex(t)
                    if (i >= 0) list[i].unlocks++
                }
            }
        }
        closeAll(now)
        return list
    }

    /** Apps, die als Startbildschirm dienen (deren Zeit zählt nicht als Bildschirmzeit). */
    fun launcherPackages(context: Context): Set<String> {
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return runCatching {
            context.packageManager.queryIntentActivities(home, 0).map { it.activityInfo.packageName }.toSet()
        }.getOrDefault(emptySet())
    }

    /** Android-Kategorie einer App (ApplicationInfo.category), -1 wenn unbekannt. */
    fun category(context: Context, pkg: String): Int = runCatching {
        context.packageManager.getApplicationInfo(pkg, 0).category
    }.getOrDefault(-1)
}
