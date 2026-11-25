package de.funke.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.funke.app.ui.AppState
import de.funke.app.ui.InstalledApp
import de.funke.app.ui.Lang
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.components.AppIcon
import de.funke.app.ui.components.Chevron
import de.funke.app.ui.components.Dot
import de.funke.app.ui.components.ExpandableCard
import de.funke.app.ui.components.GroupIcon
import de.funke.app.ui.components.ModalScope
import de.funke.app.ui.components.ModalWindow
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.ProgressBar
import de.funke.app.ui.components.SectionHeader
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.SegmentedToggle
import de.funke.app.ui.components.Stat
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.components.surface
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.NumberStyle
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import de.funke.app.usage.AppGroup
import de.funke.app.usage.AppGroups
import de.funke.app.usage.GroupConfig
import de.funke.app.usage.ScreenTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.TextStyle as DayStyle
import de.funke.app.ui.components.Unfold

/** Eine App mit ihrer Nutzungszeit im gewählten Zeitraum. */
@Immutable
private data class AppUse(val pkg: String, val label: String, val app: InstalledApp?, val ms: Long, val group: String, val manual: Boolean)

/** Zeit hübsch: "3 h 12 min", "45 min", "< 1 min". */
private fun duration(ms: Long): String {
    val min = (ms / 60000L).toInt()
    if (min < 1) return "< 1 min"
    val h = min / 60
    val m = min % 60
    return when {
        h == 0 -> "$m min"
        m == 0 -> "$h h"
        else -> "$h h $m min"
    }
}

/** Wie viele Tage geladen werden: diese Woche und die Woche davor (für den Vergleich). */
private const val DAYS = 14

/**
 * Bildschirmzeit (seit 13.4 im Reiter Fortschritt, vorher eigener Reiter "Apps"): nach Gruppen statt App für App, damit man ehrlich sieht, wofür die Zeit
 * draufgeht (z. B. alle Videos zusammen).
 *
 * Aufbau (seit 9.7 ruhiger und aufklappbar): Zeitraum → Übersicht (Zeit, Vergleich, Lesen gegen Handy)
 * → Woche (antippbare Balken) → Kennzahlen → Gruppen → Top-Apps.
 */
@Composable
fun ScreenTimeTab(state: AppState, modifier: Modifier = Modifier, header: (@Composable () -> Unit)? = null) {
    val p = Funke.colors
    val context = LocalContext.current
    val installed = LocalInstalledApps.current
    var access by remember { mutableStateOf(ScreenTime.hasAccess(context)) }
    var refresh by remember { mutableIntStateOf(0) }
    var range by rememberSaveable { mutableIntStateOf(0) } // 0 heute, 1 gestern, 2 sieben Tage
    var report by remember { mutableStateOf<List<ScreenTime.Day>?>(null) }
    var categories by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var launchers by remember { mutableStateOf<Set<String>>(emptySet()) }
    val config = remember(state.appGroupsJson) { GroupConfig.fromJson(state.appGroupsJson) }
    var expanded by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<AppGroup?>(null) }
    var creating by remember { mutableStateOf(false) }
    var moving by remember { mutableStateOf<AppUse?>(null) }
    // aufgeklappte Abschnitte (bleiben beim Drehen erhalten)
    var weekOpen by rememberSaveable { mutableStateOf(true) }
    var statsOpen by rememberSaveable { mutableStateOf(false) }
    var topOpen by rememberSaveable { mutableStateOf(false) }

    // Zurück aus den Einstellungen: Berechtigung neu prüfen und Zahlen auffrischen
    val lifecycle = androidx.compose.ui.platform.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                access = ScreenTime.hasAccess(context)
                refresh++
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    LaunchedEffect(access, refresh, installed) {
        if (!access) return@LaunchedEffect
        val r = withContext(Dispatchers.IO) {
            val l = ScreenTime.launcherPackages(context)
            val known = installed.map { it.packageName } + context.packageName
            Triple(ScreenTime.days(context, DAYS), known.associateWith { ScreenTime.category(context, it) }, l)
        }
        report = r.first
        categories = r.second
        launchers = r.third
    }

    val known = remember(installed) { installed.associateBy { it.packageName } }
    fun counted(pkg: String) = pkg !in launchers && (pkg in known || pkg == context.packageName)
    fun groupOf(pkg: String): String {
        config.assign[pkg]?.let { return it }
        val label = known[pkg]?.label ?: "Lumina"
        return AppGroups.classify(pkg, label, categories[pkg] ?: -1)
    }
    fun appsOf(days: List<ScreenTime.Day>): Map<String, Long> {
        val m = HashMap<String, Long>()
        days.forEach { d -> d.apps.forEach { (k, v) -> if (counted(k)) m[k] = (m[k] ?: 0L) + v } }
        return m
    }

    val days = report.orEmpty()
    val sel = when {
        days.size < DAYS -> emptyList()
        range == 0 -> listOf(days[DAYS - 1])
        range == 1 -> listOf(days[DAYS - 2])
        else -> days.subList(DAYS - 7, DAYS)
    }
    val prev = when {
        days.size < DAYS -> emptyList()
        range == 0 -> listOf(days[DAYS - 2])
        range == 1 -> listOf(days[DAYS - 3])
        else -> days.subList(0, 7)
    }
    val selApps = appsOf(sel)
    val total = selApps.values.sum()
    val prevTotal = appsOf(prev).values.sum()
    val perDay = if (range == 2) total / 7 else total
    val prevPerDay = if (range == 2) prevTotal / 7 else prevTotal

    // Apps mit Gruppe (auch ohne Nutzung, für den Gruppen-Editor)
    val allApps: List<AppUse> = (installed.map { it.packageName to it } + (context.packageName to null)).map { (pkg, app) ->
        AppUse(pkg, app?.label ?: "Lumina", app, selApps[pkg] ?: 0L, groupOf(pkg), config.assign[pkg] != null)
    }
    val used = allApps.filter { it.ms >= 60_000L }.sortedByDescending { it.ms }
    val byGroup = used.groupBy { it.group }
    val groupRows = config.groups
        .map { g -> g to (byGroup[g.id].orEmpty()) }
        .filter { (g, list) -> list.isNotEmpty() || !g.builtIn }
        .sortedByDescending { (_, list) -> list.sumOf { it.ms } }

    val today = LocalDate.now()
    val selDates = when (range) {
        0 -> listOf(today)
        1 -> listOf(today.minusDays(1))
        else -> (6 downTo 0).map { today.minusDays(it.toLong()) }
    }
    val focusMin = selDates.sumOf { state.minutesOn(it) }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = Space.page, end = Space.page, top = Space.s6, bottom = NavBarSpace + Space.s6),
    ) {
        if (header != null) header() else {
            Text(
                tr("Bildschirmzeit", "Screen time"),
                style = MaterialTheme.typography.headlineLarge,
                color = p.text,
                modifier = Modifier.reveal(0),
            )
            Spacer(Modifier.height(Space.s5))
        }
        Box(Modifier.reveal(Motion.Stagger)) {
            SegmentedToggle(
                options = listOf(tr("Heute", "Today"), tr("Gestern", "Yesterday"), tr("7 Tage", "7 days")),
                selected = range,
                onSelect = { range = it },
            )
        }
        Spacer(Modifier.height(Space.s4))

        if (!access) {
            AccessCard(onAllow = { ScreenTime.openSettings(context) })
            return@Column
        }
        val loading = report == null
        OverviewCard(
            label = when (range) {
                0 -> tr("Heute am Handy", "Phone time today")
                1 -> tr("Gestern am Handy", "Phone time yesterday")
                else -> tr("Im Schnitt pro Tag", "Daily average")
            },
            perDayMs = perDay,
            totalMs = if (range == 2) total else null,
            prevPerDayMs = prevPerDay,
            compareText = when (range) {
                0 -> tr("als gestern", "than yesterday")
                1 -> tr("als vorgestern", "than the day before")
                else -> tr("als in der Woche davor", "than the week before")
            },
            focusMin = focusMin,
            loading = loading,
        )

        if (!loading && total > 0) {
            Spacer(Modifier.height(Space.stack))
            ShareCard(
                rows = groupRows.map { (g, list) -> g to list.sumOf { it.ms } },
                total = total,
                onPick = { id -> expanded = id },
                modifier = Modifier.reveal(2 * Motion.Stagger),
            )
        }

        if (!loading) {
            val last7 = days.takeLast(7)
            val weekAvg = last7.map { d -> d.apps.entries.sumOf { (k, v) -> if (counted(k)) v else 0L } }.let { if (it.isEmpty()) 0L else it.sum() / it.size }
            Spacer(Modifier.height(Space.stack))
            ExpandableCard(
                title = tr("Woche", "Week"),
                summary = "Ø " + duration(weekAvg),
                expanded = weekOpen,
                onToggle = { weekOpen = !weekOpen },
                modifier = Modifier.reveal(2 * Motion.Stagger),
            ) {
                WeekChart(
                    days = last7,
                    highlight = when (range) { 0 -> 6; 1 -> 5; else -> -1 },
                    counted = ::counted,
                    focusOn = { state.minutesOn(it) },
                )
            }

            // ----- Kennzahlen -----
            val unlocks = sel.sumOf { it.unlocks }
            val longest = sel.filter { it.longestPkg != null && counted(it.longestPkg!!) }.maxByOrNull { it.longestMs }
            val unlockText = if (range == 2) "${unlocks / 7}×" else "$unlocks×"
            Spacer(Modifier.height(Space.stack))
            ExpandableCard(
                title = tr("Kennzahlen", "Numbers"),
                summary = tr("$unlockText entsperrt", "$unlockText unlocked"),
                expanded = statsOpen,
                onToggle = { statsOpen = !statsOpen },
                modifier = Modifier.reveal(3 * Motion.Stagger),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Space.s4)) {
                    Stat(
                        tr("Entsperrt", "Unlocks"), unlockText,
                        sub = if (range == 2) tr("pro Tag", "per day") else tr("mal", "times"),
                        modifier = Modifier.weight(1f),
                    )
                    Stat(
                        tr("Am Stück", "Longest"), longest?.let { duration(it.longestMs) } ?: "–",
                        sub = longest?.longestPkg?.let { known[it]?.label ?: "Lumina" },
                        modifier = Modifier.weight(1.3f),
                    )
                    Stat(tr("Apps", "Apps"), used.size.toString(), sub = tr("benutzt", "used"), modifier = Modifier.weight(0.8f))
                }
            }
        }

        // ----- Gruppen -----
        Spacer(Modifier.height(Space.s6))
        SectionHeader(tr("Gruppen", "Groups")) {
            TextAction(tr("+ Gruppe", "+ Group"), onClick = { creating = true })
        }
        Spacer(Modifier.height(Space.s1))
        if (!loading && used.isEmpty()) {
            Text(
                tr("Noch keine Bildschirmzeit in diesem Zeitraum.", "No screen time in this period yet."),
                style = MaterialTheme.typography.bodyMedium,
                color = p.textTertiary,
                modifier = Modifier.padding(horizontal = Space.s1, vertical = Space.s2),
            )
        }
        val maxGroup = groupRows.maxOfOrNull { (_, l) -> l.sumOf { it.ms } }?.coerceAtLeast(1L) ?: 1L
        groupRows.forEach { (g, list) ->
            val ms = list.sumOf { it.ms }
            GroupRow(
                group = g,
                apps = list,
                ms = ms,
                share = if (total > 0) ms.toFloat() / total else 0f,
                fill = ms.toFloat() / maxGroup,
                expanded = expanded == g.id,
                onToggle = { expanded = if (expanded == g.id) null else g.id },
                onEdit = { editing = g },
                onApp = { moving = it },
            )
            Spacer(Modifier.height(Space.s2))
        }

        // ----- Top-Apps -----
        if (used.isNotEmpty()) {
            val top = used.take(5)
            Spacer(Modifier.height(Space.s4))
            ExpandableCard(
                title = tr("Top-Apps", "Top apps"),
                summary = top.first().label,
                expanded = topOpen,
                onToggle = { topOpen = !topOpen },
            ) {
                val maxMs = top.first().ms.coerceAtLeast(1L)
                top.forEach { a -> AppLine(a, fill = a.ms.toFloat() / maxMs, tint = p.tame(config.group(a.group).color), onClick = { moving = a }) }
            }
        }
        Spacer(Modifier.height(Space.s5))
        Text(
            tr(
                "Alles bleibt auf deinem Handy. App antippen, um sie umzusortieren.",
                "Everything stays on your phone. Tap an app to move it.",
            ),
            style = MaterialTheme.typography.bodySmall,
            color = p.textTertiary,
            modifier = Modifier.padding(horizontal = Space.s1),
        )
    }

    // ----- Dialoge -----
    moving?.let { app ->
        MoveAppDialog(
            app = app,
            config = config,
            onPick = { groupId ->
                val assign = config.assign.toMutableMap()
                if (groupId == null) assign.remove(app.pkg) else assign[app.pkg] = groupId
                state.saveAppGroups(config.copy(assign = assign).toJson())
                moving = null
            },
            onDismiss = { moving = null },
        )
    }
    if (creating || editing != null) {
        GroupEditor(
            group = editing,
            config = config,
            apps = allApps.sortedWith(compareByDescending<AppUse> { it.ms }.thenBy { it.label.lowercase() }),
            onSave = { c ->
                state.saveAppGroups(c.toJson())
                creating = false
                editing = null
            },
            onDismiss = {
                creating = false
                editing = null
            },
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Karten
// ---------------------------------------------------------------------------------------------

@Composable
private fun AccessCard(onAllow: () -> Unit) {
    val p = Funke.colors
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s6),
    ) {
        Box(
            Modifier.size(Sizes.avatar).clip(Shapes.md).background(p.accentSoft),
            contentAlignment = Alignment.Center,
        ) { GroupIcon("tools", p.accent, Modifier.size(Sizes.iconLg)) }
        Spacer(Modifier.height(Space.s4))
        Text(tr("Zugriff auf Nutzungsdaten", "Access to usage data"), style = MaterialTheme.typography.titleLarge, color = p.text)
        Spacer(Modifier.height(Space.s3))
        listOf(
            tr("Nur, welche App wie lange offen war. Keine Inhalte.", "Only which app was open for how long. No content."),
            tr("Bleibt auf deinem Handy.", "Stays on your phone."),
        ).forEach { line ->
            Row(Modifier.padding(vertical = Space.s1)) {
                Dot(p.textTertiary, Modifier.padding(top = Space.s2), size = 6.dp)
                Spacer(Modifier.width(Space.s3))
                Text(line, style = MaterialTheme.typography.bodyMedium, color = p.textSecondary)
            }
        }
        Spacer(Modifier.height(Space.s6))
        PrimaryButton(tr("Zugriff erlauben", "Allow access"), onClick = onAllow, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(Space.s3))
        Text(
            tr("In der Liste „Lumina“ antippen und erlauben, dann zurückkommen.", "Tap “Lumina” in the list, allow it, then come back."),
            style = MaterialTheme.typography.bodySmall,
            color = p.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Übersicht: die Zeit groß, darunter der Vergleich mit dem Zeitraum davor (weniger = grün),
 * dann Lesen gegen Handy als ein geteilter Balken.
 */
@Composable
private fun OverviewCard(
    label: String,
    perDayMs: Long,
    totalMs: Long?,
    prevPerDayMs: Long,
    compareText: String,
    focusMin: Int,
    loading: Boolean,
) {
    val p = Funke.colors
    Column(
        Modifier
            .fillMaxWidth()
            .reveal(Motion.Stagger * 2)
            .surface(Shapes.large, Elevation.Level2)
            .padding(Space.s6),
    ) {
        de.funke.app.ui.components.SwapText(label, style = MaterialTheme.typography.labelLarge, color = p.textSecondary)
        Spacer(Modifier.height(Space.s1))
        Row(verticalAlignment = Alignment.Bottom) {
            // Ziffern, die sich beim Wechsel Heute/Gestern/7 Tage ändern, rollen einzeln weiter
            de.funke.app.ui.components.AnimatedNumber(if (loading) "…" else duration(perDayMs), style = NumberStyle.copy(fontSize = NumberStyle.fontSize * 1.25f), color = p.text)
            if (totalMs != null && !loading) {
                Spacer(Modifier.width(Space.s3))
                Text(
                    tr("${duration(totalMs)} insgesamt", "${duration(totalMs)} in total"),
                    style = MaterialTheme.typography.bodySmall,
                    color = p.textTertiary,
                    modifier = Modifier.padding(bottom = Space.s2),
                )
            }
        }
        // Vergleich mit davor (nur, wenn es davor Daten gibt)
        if (!loading && prevPerDayMs > 0) {
            val diff = perDayMs - prevPerDayMs
            val same = kotlin.math.abs(diff) < 60_000L
            val less = diff < 0
            val tint = when {
                same -> p.textSecondary
                less -> p.success
                else -> p.danger
            }
            Spacer(Modifier.height(Space.s1))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!same) {
                    TrendArrow(up = !less, color = tint)
                    Spacer(Modifier.width(Space.s1))
                }
                de.funke.app.ui.components.SwapText(
                    if (same) tr("Genauso viel $compareText", "About the same $compareText")
                    else (if (less) tr("${duration(-diff)} weniger $compareText", "${duration(-diff)} less $compareText")
                    else tr("${duration(diff)} mehr $compareText", "${duration(diff)} more $compareText")),
                    style = MaterialTheme.typography.labelLarge,
                    color = tint,
                )
            }
        }

        // Lesen gegen Handy
        Spacer(Modifier.height(Space.s6))
        val screenMin = (perDayMs / 60000L).toInt()
        val focusPerDay = if (totalMs != null) focusMin / 7 else focusMin
        val share by animateFloatAsState(
            if (screenMin + focusPerDay == 0) 0f else focusPerDay / (screenMin + focusPerDay).toFloat(),
            Motion.spec(Motion.Slow, Motion.EaseOut),
            label = "share",
        )
        Row {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Dot(p.accent)
                    Spacer(Modifier.width(Space.s2))
                    Text(tr("Fokuszeit", "Focus time"), style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
                }
                de.funke.app.ui.components.AnimatedNumber(if (focusPerDay == 0) "0 min" else duration(focusPerDay * 60000L), style = MaterialTheme.typography.titleMedium, color = p.text)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("Am Handy", "On your phone"), style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
                    Spacer(Modifier.width(Space.s2))
                    Dot(p.textTertiary)
                }
                de.funke.app.ui.components.AnimatedNumber(if (loading) "…" else duration(perDayMs), style = MaterialTheme.typography.titleMedium, color = p.text)
            }
        }
        Spacer(Modifier.height(Space.s3))
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(Space.s2)
        ) {
            // zwei Kapseln mit kleiner Lücke: Lesen (Akzent) | Handy (ruhig)
            val gap = 4.dp.toPx()
            val r = CornerRadius(size.height / 2f)
            val split = (size.width - gap) * share
            if (split > 0.5f) drawRoundRect(p.accent, Offset.Zero, Size(split, size.height), r)
            val restX = if (split > 0.5f) split + gap else 0f
            drawRoundRect(p.textTertiary.copy(alpha = 0.45f), Offset(restX, 0f), Size(size.width - restX, size.height), r)
        }
        Spacer(Modifier.height(Space.s4))
        val text = when {
            loading -> ""
            screenMin == 0 && focusPerDay == 0 -> tr("Noch nichts los. Ein guter Moment für ein Buch.", "Nothing yet. A good moment for a book.")
            focusPerDay == 0 -> tr("Noch keine Fokuszeit heute.", "No focus time yet today.")
            focusPerDay >= screenMin -> tr("Mehr Fokus als Handy. Stark.", "More focus than phone. Great.")
            else -> {
                val r = screenMin.toFloat() / focusPerDay
                val ratio = if (r < 10) "%.1f".format(java.util.Locale.ROOT, r).replace('.', if (Lang.german) ',' else '.') else r.toInt().toString()
                tr("Auf 1 Minute Fokus kommen $ratio Minuten am Handy.", "For every minute of focus, $ratio minutes on your phone.")
            }
        }
        if (text.isNotEmpty()) Text(text, style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
    }
}

/** Ein Abschnitt der Aufteilung. */
private class ShareSeg(val id: String, val name: String, val color: Color, val ms: Long)

/**
 * "Wofür": Anteil der Gruppen an der Bildschirmzeit als ein durchgehender Balken (Abschnitte mit
 * kleinen Lücken, Breiten gleiten beim Wechsel Heute/Gestern/7 Tage weich um), darunter eine
 * Legende mit Prozent (gleich breite Ziffern). Die fünf größten Gruppen, der Rest als "Weitere".
 * Antippen einer Gruppe klappt sie unten in der Liste auf.
 */
@Composable
private fun ShareCard(rows: List<Pair<AppGroup, Long>>, total: Long, onPick: (String) -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val top = rows.filter { it.second > 0 }.sortedByDescending { it.second }.take(5)
    val restMs = (total - top.sumOf { it.second }).coerceAtLeast(0L)
    val segs = top.map { (g, ms) -> ShareSeg(g.id, g.name, p.tame(g.color), ms) } +
        if (restMs * 100 >= total) listOf(ShareSeg("_rest", tr("Weitere", "Other"), p.textTertiary.copy(alpha = 0.55f), restMs)) else emptyList()
    // Breiten weich animieren (auch Abschnitte, die gerade verschwinden, gleiten auf 0)
    val fractions = segs.map { seg ->
        androidx.compose.runtime.key(seg.id) {
            animateFloatAsState(seg.ms.toFloat() / total.coerceAtLeast(1L), Motion.spec(Motion.Slow, Motion.EaseOut), label = "share_${seg.id}").value
        }
    }
    de.funke.app.ui.components.Card(modifier = modifier, padding = androidx.compose.foundation.layout.PaddingValues(Space.card)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Wofür", "Spent on"), style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
            Text(
                tr("${rows.count { it.second > 0 }} Gruppen", "${rows.count { it.second > 0 }} groups"),
                style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                color = p.textTertiary,
            )
        }
        Spacer(Modifier.height(Space.s3))
        Canvas(Modifier.fillMaxWidth().height(Space.s3)) {
            val gap = 3.dp.toPx()
            val n = fractions.count { it > 0.001f }
            val usable = size.width - gap * (n - 1).coerceAtLeast(0)
            val sum = fractions.sum().coerceAtLeast(0.0001f)
            var x = 0f
            val r = CornerRadius(size.height / 2f)
            segs.forEachIndexed { i, seg ->
                val w = usable * fractions[i] / sum
                if (w > 0.5f) {
                    drawRoundRect(seg.color, Offset(x, 0f), Size(w, size.height), r)
                    x += w + gap
                }
            }
        }
        Spacer(Modifier.height(Space.s4))
        segs.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s4)) {
                pair.forEach { seg ->
                    val pct = (seg.ms * 100f / total.coerceAtLeast(1L)).let { if (it in 0.01f..0.99f) "<1" else kotlin.math.round(it).toInt().toString() }
                    Row(
                        Modifier
                            .weight(1f)
                            .clip(Shapes.sm)
                            .then(if (seg.id != "_rest") Modifier.bouncyClickable(shape = Shapes.sm, pressedScale = 0.97f) { onPick(seg.id) } else Modifier)
                            .padding(vertical = Space.s2),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(Space.s2).clip(CircleShape).background(seg.color))
                        Spacer(Modifier.width(Space.s2))
                        Text(
                            seg.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = p.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(Space.s2))
                        Text("$pct %", style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum, ss03"), color = p.text)
                    }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/** Kleiner Trend-Pfeil (nach oben oder unten). */
@Composable
private fun TrendArrow(up: Boolean, color: Color) {
    Canvas(Modifier.size(Sizes.iconSm)) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            if (up) {
                moveTo(w * 0.5f, h * 0.2f); lineTo(w * 0.82f, h * 0.62f); lineTo(w * 0.18f, h * 0.62f)
            } else {
                moveTo(w * 0.5f, h * 0.8f); lineTo(w * 0.82f, h * 0.38f); lineTo(w * 0.18f, h * 0.38f)
            }
            close()
        }
        drawPath(path, color)
    }
}

/**
 * Die letzten 7 Tage als schlichte Kapsel-Balken in einer Farbe. Der gewählte Tag steht in
 * voller Textfarbe, die anderen treten zurück. Antippen zeigt die Werte des Tages oben an.
 * Ein Akzent-Punkt unter dem Balken heißt: an dem Tag wurde gelesen.
 */
@Composable
private fun WeekChart(
    days: List<ScreenTime.Day>,
    highlight: Int,
    counted: (String) -> Boolean,
    focusOn: (LocalDate) -> Int,
) {
    val p = Funke.colors
    val reduced = reducedMotion
    val dayTotals = days.map { d -> d.apps.entries.sumOf { (k, v) -> if (counted(k)) v else 0L } }
    val maxMs = (dayTotals.maxOrNull() ?: 0L).coerceAtLeast(30 * 60_000L)
    val avg = if (dayTotals.isNotEmpty()) dayTotals.sum() / dayTotals.size else 0L
    val dates = (6 downTo 0).map { LocalDate.now().minusDays(it.toLong()) }
    var picked by remember(highlight) { mutableIntStateOf(if (highlight >= 0) highlight else 6) }
    val grow = remember { Animatable(if (reduced) 1f else 0f) }
    LaunchedEffect(Unit) { grow.animateTo(1f, tween(Motion.Slow, easing = Motion.EaseOut)) }
    // je Balken ein weicher Übergang zwischen "gewählt" und "zurückgenommen"
    val emphasis = List(7) { i ->
        animateFloatAsState(if (i == picked) 1f else 0f, Motion.fade(Motion.Base), label = "bar$i").value
    }
    val haptics = de.funke.app.ui.LocalHaptics.current

    val pickedDate = dates.getOrElse(picked) { LocalDate.now() }
    val pickedMs = dayTotals.getOrElse(picked) { 0L }
    val pickedRead = focusOn(pickedDate)
    Row(verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(
                pickedDate.dayOfWeek.getDisplayName(DayStyle.FULL, Lang.locale) +
                    if (picked == 6) tr(", heute", ", today") else "",
                style = MaterialTheme.typography.bodySmall,
                color = p.textSecondary,
            )
            de.funke.app.ui.components.AnimatedNumber(duration(pickedMs), style = MaterialTheme.typography.headlineSmall, color = p.text)
        }
        Text(
            if (pickedRead > 0) tr("$pickedRead min Fokus", "$pickedRead min focus") else tr("kein Fokus", "no focus"),
            style = MaterialTheme.typography.bodySmall,
            color = if (pickedRead > 0) p.accent else p.textTertiary,
            modifier = Modifier.padding(bottom = Space.s1),
        )
    }
    Spacer(Modifier.height(Space.s5))
    val barTrack = p.track
    val barRest = p.textTertiary.copy(alpha = 0.55f)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .pointerInput(days.size) {
                detectTapGestures { pos ->
                    val slot = size.width / 7f
                    val i = (pos.x / slot).toInt().coerceIn(0, 6)
                    if (i != picked) haptics?.tick()
                    picked = i
                }
            }
    ) {
        val slot = size.width / 7f
        val bw = minOf(slot * 0.42f, 20.dp.toPx())
        val h = size.height
        val r = CornerRadius(bw / 2f)
        // Schnitt als zarte gestrichelte Linie
        val ay = h - h * (avg / maxMs.toFloat()) * grow.value
        drawLine(
            p.textTertiary.copy(alpha = 0.5f), Offset(0f, ay), Offset(size.width, ay), 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
        )
        days.indices.forEach { i ->
            if (i > 6) return@forEach
            val x = slot * i + (slot - bw) / 2f
            // Spur über die ganze Höhe, darin der Balken
            drawRoundRect(barTrack, Offset(x, 0f), Size(bw, h), r)
            val bh = (h * (dayTotals[i] / maxMs.toFloat()) * grow.value).coerceAtLeast(if (dayTotals[i] > 0) bw else 0f)
            if (bh > 0f) drawRoundRect(lerp(barRest, p.text, emphasis[i]), Offset(x, h - bh), Size(bw, bh), r)
        }
    }
    Spacer(Modifier.height(Space.s2))
    Row(Modifier.fillMaxWidth()) {
        dates.forEachIndexed { i, d ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    d.dayOfWeek.getDisplayName(DayStyle.SHORT, Lang.locale).take(2),
                    style = MaterialTheme.typography.labelSmall,
                    color = lerp(p.textTertiary, p.text, emphasis.getOrElse(i) { 0f }),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Space.s1))
                Box(Modifier.size(6.dp), contentAlignment = Alignment.Center) {
                    if (focusOn(d) > 0) Dot(p.accent, size = 6.dp)
                }
            }
        }
    }
    Spacer(Modifier.height(Space.s3))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Dot(p.accent, size = 6.dp)
        Spacer(Modifier.width(Space.s2))
        Text(tr("Fokus", "focus"), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
        Spacer(Modifier.width(Space.s4))
        Canvas(Modifier.width(Space.s3).height(1.dp)) {
            drawLine(p.textTertiary, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f)))
        }
        Spacer(Modifier.width(Space.s2))
        Text("Ø " + duration(avg), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
    }
}

/** Eine App-Zeile mit Symbol, Name, Zeit und dünnem Balken. */
@Composable
private fun AppLine(a: AppUse, fill: Float, tint: Color, onClick: () -> Unit, showManual: Boolean = false) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = Sizes.touch)
            .bouncyClickable(shape = Shapes.md, pressedScale = 0.99f, onClick = onClick)
            .padding(vertical = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(a.app, a.label, size = 32.dp)
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(a.label, style = MaterialTheme.typography.bodyMedium, color = p.text, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                if (showManual && a.manual) {
                    Spacer(Modifier.width(Space.s2))
                    Text(tr("von dir", "moved by you"), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
                }
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(Space.s2))
                Text(duration(a.ms), style = MaterialTheme.typography.labelLarge, color = p.textSecondary)
            }
            if (fill > 0f) {
                Spacer(Modifier.height(Space.s1))
                ProgressBar(fill, color = tint, height = Space.s1)
            }
        }
    }
}

@Composable
private fun GroupRow(
    group: AppGroup,
    apps: List<AppUse>,
    ms: Long,
    share: Float,
    fill: Float,
    expanded: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onApp: (AppUse) -> Unit,
) {
    val p = Funke.colors
    val reduced = reducedMotion
    val tint = p.tame(group.color)
    val shape = Shapes.card
    Column(
        Modifier
            .fillMaxWidth()
            .surface(shape, Elevation.Level1)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .bouncyClickable(shape = shape, pressedScale = 0.99f, onClick = onToggle)
                .padding(horizontal = Space.s4, vertical = Space.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(Sizes.tile)
                    .clip(Shapes.sm)
                    .background(tint.copy(alpha = if (p.isDark) 0.16f else 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                GroupIcon(group.icon, tint, Modifier.size(Sizes.icon))
            }
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(group.name, style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(Space.s2))
                    Text(if (ms > 0) duration(ms) else "–", style = MaterialTheme.typography.titleSmall, color = p.text)
                }
                Spacer(Modifier.height(Space.s2))
                ProgressBar(fill, color = tint, height = Space.s1)
                Spacer(Modifier.height(Space.s1))
                Text(
                    if (ms > 0) "${(share * 100).toInt()} %, ${apps.size} " + if (apps.size == 1) "App" else "Apps" else tr("leer", "empty"),
                    style = MaterialTheme.typography.labelSmall,
                    color = p.textTertiary,
                )
            }
            Spacer(Modifier.width(Space.s2))
            Chevron(expanded)
        }
        Unfold(visible = expanded, enter = Motion.expand(reduced), exit = Motion.collapse(reduced)) {
            Column(Modifier.padding(start = Space.s4, end = Space.s4, bottom = Space.s3)) {
                apps.forEach { a -> AppLine(a, fill = 0f, tint = tint, onClick = { onApp(a) }, showManual = true) }
                de.funke.app.ui.components.TextAction(tr("Gruppe bearbeiten", "Edit group"), onClick = onEdit, modifier = Modifier.padding(top = Space.s1))
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Dialoge
// ---------------------------------------------------------------------------------------------

/** App in eine andere Gruppe schieben (oder zurück auf automatisch). */
@Composable
private fun MoveAppDialog(app: AppUse, config: GroupConfig, onPick: (String?) -> Unit, onDismiss: () -> Unit) {
    val p = Funke.colors
    ModalWindow(onDismiss) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppIcon(app.app, app.label, size = Sizes.tile)
            Spacer(Modifier.width(Space.s3))
            Column {
                Text(app.label, style = MaterialTheme.typography.titleMedium, color = p.text)
                Text(tr("In welche Gruppe gehört die App?", "Which group does this app belong to?"), style = MaterialTheme.typography.bodySmall, color = p.textSecondary)
            }
        }
        Spacer(Modifier.height(Space.s4))
        LazyColumn(Modifier.heightIn(max = 420.dp)) {
            items(config.groups, key = { it.id }) { g ->
                val selected = g.id == app.group
                Row(
                    Modifier
                        .fillMaxWidth()
                        .bouncyClickable(shape = Shapes.md) { close { onPick(g.id) } }
                        .clip(Shapes.md)
                        .background(if (selected) p.tame(g.color).copy(alpha = 0.14f) else Color.Transparent)
                        .padding(horizontal = Space.s3, vertical = Space.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    de.funke.app.ui.components.GroupIcon(g.icon, p.tame(g.color), Modifier.size(Sizes.icon))
                    Spacer(Modifier.width(Space.s3))
                    Text(g.name, style = MaterialTheme.typography.bodyLarge, color = p.text, modifier = Modifier.weight(1f))
                    if (selected) de.funke.app.ui.components.CheckIcon(p.tame(g.color), Modifier.size(Sizes.iconSm))
                }
            }
        }
        if (app.manual) {
            Spacer(Modifier.height(Space.s3))
            PillButton(tr("Wieder automatisch sortieren", "Sort automatically again"), onClick = { close { onPick(null) } }, modifier = Modifier.fillMaxWidth())
        }
    }
}

/** Gruppe anlegen oder bearbeiten: Name, Symbol, Farbe und welche Apps dazugehören. */
@Composable
private fun GroupEditor(
    group: AppGroup?,
    config: GroupConfig,
    apps: List<AppUse>,
    onSave: (GroupConfig) -> Unit,
    onDismiss: () -> Unit,
) {
    val p = Funke.colors
    val id = remember { group?.id ?: "custom_" + System.currentTimeMillis().toString(36) }
    var name by remember { mutableStateOf(group?.name ?: "") }
    var icon by remember { mutableStateOf(group?.icon ?: AppGroups.IconChoices.first()) }
    var color by remember { mutableStateOf(group?.color ?: AppGroups.ColorChoices.first()) }
    val members = remember { mutableStateOf(apps.filter { it.group == id }.map { it.pkg }.toSet()) }
    var confirmDelete by remember { mutableStateOf(false) }

    fun save() {
        val n = name.trim().ifEmpty { tr("Meine Gruppe", "My group") }
        val newGroup = AppGroup(id, if (group?.builtIn == true && n == AppGroups.defaultName(id)) null else n, icon, color, group?.builtIn ?: false)
        val groups = if (group == null) config.groups + newGroup else config.groups.map { if (it.id == id) newGroup else it }
        val assign = config.assign.toMutableMap()
        apps.forEach { a ->
            val inNow = a.pkg in members.value
            val wasIn = a.group == id
            if (inNow && !wasIn) assign[a.pkg] = id
            if (!inNow && wasIn) {
                if (assign[a.pkg] == id) assign.remove(a.pkg)
                // gehört sie automatisch auch hierher, landet sie bei "Sonstiges"
                if (a.group == id && !a.manual) assign[a.pkg] = AppGroups.OTHER
            }
        }
        onSave(GroupConfig(groups, assign))
    }

    ModalWindow(onDismiss) {
        Text(if (group == null) tr("Neue Gruppe", "New group") else tr("Gruppe bearbeiten", "Edit group"), style = MaterialTheme.typography.titleLarge, color = p.text)
        Spacer(Modifier.height(Space.s4))
        // Name
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(Shapes.md)
                    .background(p.tame(color).copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) { de.funke.app.ui.components.GroupIcon(icon, p.tame(color), Modifier.size(Sizes.iconLg)) }
            Spacer(Modifier.width(Space.s3))
            BasicTextField(
                value = name,
                onValueChange = { if (it.length <= 28) name = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium.copy(color = p.text),
                cursorBrush = SolidColor(p.accent),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .glass(Shapes.md)
                            .padding(horizontal = Space.s4, vertical = Space.s3)
                    ) {
                        if (name.isEmpty()) Text(tr("Name der Gruppe", "Group name"), color = p.textTertiary, style = MaterialTheme.typography.titleMedium)
                        inner()
                    }
                },
            )
        }
        Spacer(Modifier.height(Space.s3))
        // Symbol
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
            AppGroups.IconChoices.forEach { e ->
                Box(
                    Modifier
                        .size(38.dp)
                        .bouncyClickable(shape = Shapes.sm) { icon = e }
                        .clip(Shapes.sm)
                        .background(if (e == icon) p.tame(color).copy(alpha = 0.16f) else p.track),
                    contentAlignment = Alignment.Center,
                ) { de.funke.app.ui.components.GroupIcon(e, if (e == icon) p.tame(color) else p.textSecondary, Modifier.size(Sizes.icon)) }
            }
        }
        Spacer(Modifier.height(Space.s3))
        // Farbe
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
            AppGroups.ColorChoices.forEach { c ->
                val sel = c == color
                Box(
                    Modifier
                        .size(28.dp)
                        .bouncyClickable(shape = CircleShape) { color = c }
                        .clip(CircleShape)
                        .background(p.tame(c))
                        .then(if (sel) Modifier.border(3.dp, p.text, CircleShape) else Modifier)
                )
            }
        }
        Spacer(Modifier.height(Space.s4))
        SectionLabel(tr("Apps in dieser Gruppe", "Apps in this group") + " (${members.value.size})")
        Spacer(Modifier.height(Space.s2))
        LazyColumn(Modifier.heightIn(max = 300.dp)) {
            items(apps, key = { it.pkg }) { a ->
                val checked = a.pkg in members.value
                Row(
                    Modifier
                        .fillMaxWidth()
                        .bouncyClickable(shape = Shapes.md) { members.value = if (checked) members.value - a.pkg else members.value + a.pkg }
                        .padding(vertical = Space.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIcon(a.app, a.label, size = 30.dp)
                    Spacer(Modifier.width(Space.s3))
                    Column(Modifier.weight(1f)) {
                        Text(a.label, style = MaterialTheme.typography.bodyMedium, color = p.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            config.group(a.group).name + if (a.ms >= 60_000L) ", " + duration(a.ms) else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = p.textTertiary,
                            maxLines = 1,
                        )
                    }
                    val bg by animateColorAsState(if (checked) p.tame(color) else p.track, Motion.fade(Motion.Fast), label = "chk")
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(Shapes.xs)
                            .background(bg),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (checked) de.funke.app.ui.components.CheckIcon(if (p.isDark) p.bg else Color.White, Modifier.size(Sizes.iconSm))
                    }
                }
            }
        }
        Spacer(Modifier.height(Space.s4))
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            if (group != null && !group.builtIn) {
                PillButton(tr("Löschen", "Delete"), onClick = { confirmDelete = true }, danger = true, modifier = Modifier.weight(1f))
            } else {
                PillButton(tr("Abbrechen", "Cancel"), onClick = { close(onDismiss) }, modifier = Modifier.weight(1f))
            }
            PillButton(tr("Speichern", "Save"), onClick = { close(::save) }, filled = true, modifier = Modifier.weight(1f))
        }
        AnimatedVisibility(visible = confirmDelete) {
            Column(Modifier.padding(top = Space.s3)) {
                Text(
                    tr("Gruppe wirklich löschen? Die Apps werden wieder automatisch sortiert.", "Really delete this group? Its apps will be sorted automatically again."),
                    style = MaterialTheme.typography.bodySmall,
                    color = p.danger,
                )
                Spacer(Modifier.height(Space.s2))
                PillButton(
                    tr("Ja, löschen", "Yes, delete"),
                    onClick = {
                        close { onSave(GroupConfig(config.groups.filter { it.id != id }, config.assign.filterValues { it != id })) }
                    },
                    danger = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
