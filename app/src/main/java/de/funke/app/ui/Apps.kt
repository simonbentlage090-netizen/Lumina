package de.funke.app.ui

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

/** Eine auf dem Handy installierte App (mit Start-Symbol im Launcher). */
@Immutable
data class InstalledApp(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?,
)

/** Die Liste der installierten Apps, überall in der App abrufbar. */
val LocalInstalledApps = compositionLocalOf<List<InstalledApp>> { emptyList() }

/**
 * Liest alle Apps aus, die im Launcher auftauchen, samt Namen und Symbol.
 * Das kann einen Moment dauern, deshalb im Hintergrund aufrufen.
 */
fun loadInstalledApps(context: Context): List<InstalledApp> {
    val pm = context.packageManager
    val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(launcher, 0)
        .mapNotNull { info ->
            val pkg = info.activityInfo.packageName
            if (pkg == context.packageName) return@mapNotNull null
            val icon = runCatching { info.loadIcon(pm).toBitmap(96, 96).asImageBitmap() }.getOrNull()
            InstalledApp(info.loadLabel(pm).toString(), pkg, icon)
        }
        .distinctBy { it.packageName }
        .sortedBy { it.label.lowercase() }
}

/** Der gewählte Timer-Stil, überall abrufbar. */
val LocalTimerStyle = compositionLocalOf { TimerStyleOption.Vapor }

/** Der gewählte Ring um den Timer. */
val LocalRingStyle = compositionLocalOf { RingStyle.Classic }

/** Ist Lumi (der Sidekick) eingeschaltet? */
val LocalSidekick = compositionLocalOf { true }
