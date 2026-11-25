package de.funke.app.ui.screens

import de.funke.app.R
import de.funke.app.ui.challenge.art
import de.funke.app.ui.components.PrimaryButton
import androidx.compose.ui.res.painterResource

import de.funke.app.ui.components.smoothResize
import de.funke.app.ui.components.rememberLoop
import androidx.compose.foundation.layout.heightIn
import de.funke.app.ui.components.softShadow
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.notes.AiCleanup.Provider as AiProvider

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import de.funke.app.ui.ThemeMode
import de.funke.app.ui.challenge.Challenges
import de.funke.app.ui.challenge.Difficulty
import de.funke.app.ui.challenge.GSM8K_LICENSE
import de.funke.app.ui.challenge.OPENSTAX_NOTE
import de.funke.app.ui.challenge.THOMPSON_NOTE
import de.funke.app.ui.challenge.Knowledge
import de.funke.app.ui.challenge.Topic
import de.funke.app.ui.challenge.Tasks
import de.funke.app.ui.challenge.TaskUse
import de.funke.app.ui.challenge.WordProblems
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.AppLanguage
import de.funke.app.ui.AppState
import de.funke.app.ui.CancelStyle
import de.funke.app.ui.StartButtonStyle
import de.funke.app.ui.tr
import de.funke.app.ui.components.AnimatedCheckBadge
import de.funke.app.ui.components.CancelControl
import de.funke.app.ui.components.StartButton
import de.funke.app.ui.components.hardShadow
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.RingStyle
import de.funke.app.ui.TimerStyleOption
import de.funke.app.ui.components.CheckIcon
import de.funke.app.ui.components.ChevronRightIcon
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.LuminaLogo
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.SegmentedToggle
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.TimerRing
import de.funke.app.ui.components.VaporDigits
import de.funke.app.ui.components.accentFill
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.BackgroundPattern
import de.funke.app.ui.components.glowBorder
import de.funke.app.ui.theme.LocalPalette
import de.funke.app.ui.theme.LocalUiStyle
import de.funke.app.ui.theme.Palette
import de.funke.app.ui.theme.Palettes
import de.funke.app.ui.theme.TimerStyle
import de.funke.app.ui.theme.UiStyle
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import de.funke.app.ui.components.Unfold

/** Die Unterseiten der Einstellungen. */
private enum class SettingsPage(private val de: String, private val en: String, val icon: Int) {
    Design("Design", "Design", R.drawable.ic_palette),
    Session("Session", "Session", R.drawable.ic_timer),
    Lock("Sperre", "Lock", R.drawable.ic_lock),
    Haptics("Haptik", "Haptics", R.drawable.ic_vibrate),
    Notifications("Benachrichtigungen", "Notifications", R.drawable.ic_bell),
    Notes("Notizen", "Notes", R.drawable.ic_nav_notes),
    Tasks("Aufgaben", "Tasks", R.drawable.ic_nav_tasks),
    Guide("So funktioniert's", "How it works", R.drawable.ic_bulb),
    Language("Sprache", "Language", R.drawable.ic_languages),
    Legal("Rechtliches", "Legal", R.drawable.ic_scale),
    About("Über Lumina", "About Lumina", R.drawable.ic_info);

    val title: String get() = tr(de, en)
}

private val HapticLabels: List<String>
    get() = listOf(tr("Aus", "Off"), tr("Leicht", "Light"), tr("Mittel", "Medium"), tr("Stark", "Strong"))

/** Versionsname aus dem installierten Paket (vorher fest "13.4" im Code – stand bis 15.0 falsch da). */
@Composable
private fun appVersion(): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(context) {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull().orEmpty()
    }
}

/**
 * Reiter 4: Einstellungen. Oben eine Übersicht mit Kategorien, jede öffnet eine eigene, kurze Seite.
 * Rechts oben wechselt man jederzeit zwischen Hell und Dunkel. Alles wirkt sofort.
 */
@Composable
fun SettingsScreen(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    var page by rememberSaveable { mutableStateOf<SettingsPage?>(null) }
    BackHandler(enabled = page != null) { page = null }
    // Direkt auf eine Seite springen (z. B. aus dem Aufgaben-Reiter)
    androidx.compose.runtime.LaunchedEffect(state.settingsRequest) {
        if (state.settingsRequest?.startsWith("tasks") == true) page = SettingsPage.Tasks
    }
    val reduced = reducedMotion

    Column(modifier.fillMaxSize().statusBarsPadding()) {
        // Kopfzeile: Zurück + Titel links, Hell/Dunkel rechts oben
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = Space.s4, end = Space.page, top = Space.s4, bottom = Space.s2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = page != null,
                enter = fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) + expandHorizontally(tween(Motion.Medium, easing = Motion.EaseOut)),
                exit = fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)) + shrinkHorizontally(tween(Motion.Base, easing = Motion.EaseOut)),
            ) {
                de.funke.app.ui.components.BackButton({ page = null }, Modifier.padding(end = Space.s1))
            }
            AnimatedContent(
                targetState = page,
                transitionSpec = { fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast - 30, easing = Motion.EaseOut)) },
                label = "settingsTitle",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Space.s2),
            ) { current ->
                Text(
                    current?.title ?: tr("Einstellungen", "Settings"),
                    style = if (current == null) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium,
                    color = p.text,
                    maxLines = 1,
                )
            }
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            ThemeModeToggle(
                mode = state.themeMode,
                onCycle = {
                    haptics?.click()
                    state.cycleThemeMode(systemDark)
                },
            )
        }

        AnimatedContent(
            targetState = page,
            transitionSpec = {
                // Seitenwechsel (transitions.dev "page side-by-side"): vor = von rechts, zurück = von links.
                // Schließen schneller als Öffnen; bei "Bewegung reduzieren" nur Überblenden.
                val dir = if (targetState != null) 1 else -1
                if (reduced) {
                    fadeIn(tween(Motion.Fast)) togetherWith fadeOut(tween(Motion.Fast))
                } else {
                    (slideInHorizontally(tween(Motion.Slow, easing = Motion.EaseOut)) { dir * it / 8 } + fadeIn(tween(Motion.Base, easing = Motion.EaseOut))) togetherWith
                        (slideOutHorizontally(tween(Motion.Base, easing = Motion.EaseOut)) { -dir * it / 16 } + fadeOut(tween(Motion.Fast, easing = Motion.EaseOut)))
                }
            },
            label = "settingsPage",
            modifier = Modifier.weight(1f),
        ) { current ->
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = Space.page, end = Space.page, top = Space.s3, bottom = NavBarSpace + Space.s6),
            ) {
                when (current) {
                    null -> Hub(state, open = { page = it })
                    SettingsPage.Design -> DesignPage(state)
                    SettingsPage.Session -> {
                        TimerPage(state)
                        Spacer(Modifier.height(Space.s8))
                        ButtonsPage(state)
                    }
                    SettingsPage.Tasks -> TasksSettings(state)
                    SettingsPage.Notifications -> NotificationsPage(state)
                    SettingsPage.Notes -> NotesSettingsPage(state)
                    SettingsPage.Lock -> LockPage(state)
                    SettingsPage.Haptics -> HapticsPage(state)
                    SettingsPage.Language -> LanguagePage(state)
                    SettingsPage.Legal -> LegalPage()
                    SettingsPage.About -> AboutPage(state)
                    SettingsPage.Guide -> GuidePage(state)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Übersicht
// ---------------------------------------------------------------------------------------------

private class Category(val page: SettingsPage, val status: String? = null)

/**
 * Übersicht wie in den System-Einstellungen: wenige Gruppen ohne Überschrift, jede Zeile nur
 * Symbol und Name. Rechts steht ein Zustand nur, wenn er wichtig ist. Lumi schaltet man direkt hier.
 */
@Composable
private fun Hub(state: AppState, open: (SettingsPage) -> Unit) {
    val p = Funke.colors
    val context = androidx.compose.ui.platform.LocalContext.current
    val onOff = { on: Boolean -> if (on) tr("An", "On") else tr("Aus", "Off") }
    val groups = listOf(
        listOf(Category(SettingsPage.Design), Category(SettingsPage.Session)),
        listOf(
            Category(SettingsPage.Lock, onOff(state.lockEnabled)),
            Category(SettingsPage.Haptics),
            Category(
                SettingsPage.Notifications,
                if (!de.funke.app.notify.Reminders.allowed(context)) tr("Aus", "Off") else onOff(state.reminderOn || state.streakWarnOn),
            ),
        ),
        listOf(Category(SettingsPage.Notes), Category(SettingsPage.Tasks)),
        listOf(
            Category(SettingsPage.Guide),
            Category(SettingsPage.Language, state.language.label),
            Category(SettingsPage.Legal),
            Category(SettingsPage.About, appVersion()),
        ),
    )
    groups.forEachIndexed { gi, items ->
        if (gi > 0) Spacer(Modifier.height(Space.s5))
        Column(Modifier.fillMaxWidth().reveal(gi * 40).glass(Shapes.card)) {
            if (gi == 0) {
                LumiSwitchRow(state)
                RowDivider()
            }
            items.forEachIndexed { i, c ->
                if (i > 0) RowDivider()
                CategoryRow(c, onClick = { open(c.page) })
            }
        }
    }
}

@Composable
private fun RowDivider() {
    Box(Modifier.padding(start = Space.s4 + Sizes.tile + Space.s4).fillMaxWidth().height(1.dp).background(Funke.colors.divider))
}

/**
 * Ruhiges, neutrales Symbol (die Akzentfarbe bleibt den wichtigen Aktionen vorbehalten).
 * Seit 15.1 ohne eigene Kachel – sonst stünde ein Kästchen in der Karte.
 */
@Composable
private fun RowIcon(icon: Int) {
    val p = Funke.colors
    Box(Modifier.size(Sizes.tile), contentAlignment = Alignment.Center) {
        Icon(painterResource(icon), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.icon))
    }
}

@Composable
private fun CategoryRow(category: Category, onClick: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .bouncyClickable(shape = androidx.compose.ui.graphics.RectangleShape, pressedScale = 1f, onClick = onClick)
            .padding(horizontal = Space.s4, vertical = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RowIcon(category.page.icon)
        Spacer(Modifier.width(Space.s4))
        Text(category.page.title, style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1, modifier = Modifier.weight(1f))
        if (category.status != null) {
            Spacer(Modifier.width(Space.s2))
            Text(category.status, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 1)
        }
        Spacer(Modifier.width(Space.s2))
        Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = p.textTertiary, modifier = Modifier.size(Sizes.iconSm))
    }
}

/** Lumi an oder aus, direkt in der Übersicht. */
@Composable
private fun LumiSwitchRow(state: AppState) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .bouncyClickable(shape = androidx.compose.ui.graphics.RectangleShape, pressedScale = 1f, role = Role.Switch) { state.updateSidekick(!state.sidekick) }
            .padding(horizontal = Space.s4, vertical = Space.s2)
            .semantics {
                role = Role.Switch
                stateDescription = if (state.sidekick) tr("An", "On") else tr("Aus", "Off")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(Sizes.tile), contentAlignment = Alignment.Center) {
            Lumi2D(LumiPose.Reading, Modifier.size(38.dp), tappable = false)
        }
        Spacer(Modifier.width(Space.s4))
        Text("Lumi", style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
        LuminaSwitch(state.sidekick)
    }
}

/** Kleine Live-Vorschau: so sieht die Session mit den aktuellen Einstellungen aus. */
@Composable
private fun LivePreview(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val demo by rememberLoop(
        initialValue = 0.92f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "liveDemoP",
    )
    Row(
        modifier
            .fillMaxWidth()
            // seit 15.1 ohne wandernden Leuchtrand – die Vorschau selbst ist der Blickfang
            .glass(Shapes.large, strong = true)
            .padding(Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(112.dp), contentAlignment = Alignment.Center) {
            TimerRing(demo, state.ringStyle, Modifier.fillMaxSize(), stroke = 6.dp)
            if (state.sidekick) {
                Lumi2D(LumiPose.Reading, Modifier.size(74.dp).offset(y = (-4).dp))
            } else {
                Text("24:13", style = MaterialTheme.typography.titleLarge, color = p.text)
            }
        }
        Spacer(Modifier.width(Space.s4))
        Column(Modifier.weight(1f)) {
            Text(tr("Deine Fokuszeit", "Your focus time"), style = MaterialTheme.typography.labelMedium, color = p.textTertiary)
            Spacer(Modifier.height(Space.s1))
            Text(Palettes.byId(state.themeId).displayName, style = MaterialTheme.typography.titleLarge, color = p.text, maxLines = 1)
            Spacer(Modifier.height(Space.s1))
            Text(
                // Stil und Schrift, die gerade gelten (seit 15.2)
                state.uiStyle.label + ", " + state.appFont.resolve(state.uiStyle).label,
                style = MaterialTheme.typography.bodyMedium,
                color = p.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Space.s3))
            Box(
                Modifier
                    .size(width = 46.dp, height = 8.dp)
                    .accentFill(Shapes.pill, glow = 6.dp, outline = 1.5.dp)
            )
        }
    }
}

/**
 * Hell / Dunkel / System als kleiner Knopf, nur mit Symbol (nach der Web-Vorlage "theme", Variante "button").
 * Jedes Antippen geht einen Schritt weiter: Hell -> Dunkel -> System -> Hell.
 * Das neue Symbol dreht sich dabei von -180 Grad herein und blendet auf (0,3 s), beim Drücken gibt der Knopf
 * kurz nach (95 %).
 */
@Composable
private fun ThemeModeToggle(mode: ThemeMode, onCycle: () -> Unit) {
    val p = Funke.colors
    val shape = Shapes.sm
    var shown by remember { mutableStateOf(mode) }
    val turn = remember { Animatable(0f) }
    val fade = remember { Animatable(1f) }
    LaunchedEffect(mode) {
        if (mode == shown) return@LaunchedEffect
        shown = mode
        turn.snapTo(-180f)
        fade.snapTo(0f)
        launch { fade.animateTo(1f, tween(300, easing = FastOutSlowInEasing)) }
        turn.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 700f),
        label = "themePress",
    )
    Box(
        Modifier
            .size(40.dp)
            .graphicsLayer {
                scaleX = press
                scaleY = press
            }
            // schlichtes Symbol wie in der Kopfzeile der Startseite (seit 15.1 ohne Glas)
            .clickable(interactionSource = interaction, indication = null, onClick = onCycle)
            .semantics {
                contentDescription = tr("Hell, Dunkel oder System", "Light, dark or system")
                stateDescription = mode.label
                role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(18.dp)
                .graphicsLayer {
                    rotationZ = turn.value
                    alpha = fade.value
                },
        ) { ThemeModeIcon(shown, p.text) }
    }
}

/** Die drei Symbole im Strich-Stil der Vorlage (lucide: sun, moon, monitor), 24er-Raster, Linienstärke 2. */
@Composable
private fun ThemeModeIcon(mode: ThemeMode, tint: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val u = size.minDimension / 24f
        fun o(x: Float, y: Float) = Offset(x * u, y * u)
        val stroke = Stroke(width = 2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(tint, o(x1, y1), o(x2, y2), strokeWidth = 2f * u, cap = StrokeCap.Round)
        when (mode) {
            ThemeMode.Light -> {
                drawCircle(tint, 4f * u, o(12f, 12f), style = stroke)
                line(12f, 2f, 12f, 4f)
                line(12f, 20f, 12f, 22f)
                line(4.93f, 4.93f, 6.34f, 6.34f)
                line(17.66f, 17.66f, 19.07f, 19.07f)
                line(2f, 12f, 4f, 12f)
                line(20f, 12f, 22f, 12f)
                line(6.34f, 17.66f, 4.93f, 19.07f)
                line(19.07f, 4.93f, 17.66f, 6.34f)
            }
            ThemeMode.Dark -> {
                val full = Path().apply { addOval(Rect(o(3f, 3f), Size(18f * u, 18f * u))) }
                val bite = Path().apply { addOval(Rect(o(16.5f - 6.36f, 7.5f - 6.36f), Size(12.72f * u, 12.72f * u))) }
                val moon = Path().apply { op(full, bite, PathOperation.Difference) }
                drawPath(moon, tint, style = stroke)
            }
            ThemeMode.System -> {
                drawRoundRect(tint, o(2f, 3f), Size(20f * u, 14f * u), androidx.compose.ui.geometry.CornerRadius(2f * u), style = stroke)
                line(8f, 21f, 16f, 21f)
                line(12f, 17f, 12f, 21f)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Design
// ---------------------------------------------------------------------------------------------

@Composable
private fun DesignPage(state: AppState) {
    val p = Funke.colors
    LivePreview(state)
    Spacer(Modifier.height(Space.s5))
    SectionLabel(tr("Stil", "Style"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    // vier Stile als 2 x 2 Kacheln
    UiStyle.entries.chunked(2).forEachIndexed { row, pair ->
        if (row > 0) Spacer(Modifier.height(Space.s3))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            pair.forEach { style ->
                val preview = if (Palettes.fits(state.themeId, style, state.darkMode)) Palettes.byId(state.themeId)
                else Palettes.defaultFor(style, state.darkMode)
                StyleTile(
                    style = style,
                    preview = preview,
                    ringStyle = state.ringStyle,
                    selected = state.uiStyle == style,
                    onClick = { state.changeUiStyle(style) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    // Schrift (seit 15.2): jeder Stil bringt seine eigene mit, man kann aber eine feste wählen.
    // Jede Kachel zeigt "Aa" in genau dieser Schrift.
    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Schrift", "Font"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        de.funke.app.ui.theme.AppFont.entries.forEach { font ->
            val family = de.funke.app.ui.theme.fontSetFor(font.resolve(state.uiStyle)).display
            OptionTile(
                label = if (font == de.funke.app.ui.theme.AppFont.Auto) tr("Zum Stil", "Match") else font.label,
                selected = state.appFont == font,
                onClick = { state.updateAppFont(font) },
                modifier = Modifier.weight(1f),
            ) { tint ->
                Text(
                    "Aa",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = family,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                    ),
                    color = if (tint == p.accent) p.text else p.textSecondary,
                )
            }
        }
    }

    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Hintergrund", "Background"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        BackgroundPattern.entries.forEach { pattern ->
            OptionTile(
                label = pattern.label,
                selected = state.backgroundPattern == pattern,
                onClick = { state.updateBackgroundPattern(pattern) },
                modifier = Modifier.weight(1f),
            ) { tint -> PatternSketch(pattern, tint) }
        }
    }

    Spacer(Modifier.height(Space.s7))
    Row(Modifier.fillMaxWidth().padding(horizontal = Space.s2), verticalAlignment = Alignment.CenterVertically) {
        SectionLabel(tr("Farbschema", "Color scheme"))
        Spacer(Modifier.weight(1f))
        Text(
            if (state.themeMode == ThemeMode.System) "${state.themeMode.label} (" + (if (state.darkMode) tr("dunkel", "dark") else tr("hell", "light")) + ")"
            else state.themeMode.label,
            style = MaterialTheme.typography.labelLarge,
            color = p.accent,
        )
    }
    Palettes.groupsFor(state.uiStyle, state.darkMode).forEach { group ->
        Spacer(Modifier.height(Space.s4))
        Text(group.title, style = MaterialTheme.typography.titleSmall, color = p.textSecondary, modifier = Modifier.padding(start = Space.s2))
        Spacer(Modifier.height(Space.s3))
        group.items.chunked(3).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
                rowItems.forEach { pal ->
                    PaletteTile(
                        palette = pal,
                        selected = pal.id == state.themeId,
                        onClick = { state.setTheme(pal.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(Space.s3))
        }
    }

    Spacer(Modifier.height(Space.s5))
    SectionLabel(tr("Beim Öffnen", "On launch"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    ToggleRow(
        title = tr("Start-Animation", "Start animation"),
        checked = state.startAnimation,
        onChange = state::updateStartAnimation,
    )

    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Bewegung", "Motion"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    val modes = listOf(de.funke.app.ui.theme.MotionMode.Full, de.funke.app.ui.theme.MotionMode.Auto, de.funke.app.ui.theme.MotionMode.Reduced)
    SegmentedToggle(
        options = listOf(tr("Voll", "Full"), tr("Wie Handy", "Like phone"), tr("Weniger", "Less")),
        selected = modes.indexOf(state.motionMode),
        onSelect = { state.updateMotionMode(modes[it]) },
    )

    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Diktieren", "Dictation"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    VoiceVisualPicker(state)

    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Lumis Glühdraht", "Lumi's filament"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    Row(horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        de.funke.app.ui.components.FilamentStyle.entries.forEach { style ->
            val selected = state.filamentStyle == style
            val shape = Shapes.lg
            Column(
                Modifier
                    .weight(1f)
                    .bouncyClickable(shape = shape) { state.updateFilamentStyle(style) }
                    .glass(shape, strong = selected)
                    .then(if (selected) Modifier.border(2.dp, p.accent, shape) else Modifier)
                    .padding(vertical = Space.s3),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                de.funke.app.ui.components.FilamentPreview(
                    style = style,
                    glowing = selected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .padding(horizontal = Space.s2)
                        .clip(Shapes.md),
                )
                Spacer(Modifier.height(Space.s2))
                Text(style.label, style = MaterialTheme.typography.labelMedium, color = if (selected) p.text else p.textSecondary, maxLines = 1)
            }
        }
    }
}

/**
 * Kachel für einen Stil: eine kleine Szene (Hintergrund, Karte mit Ring, Akzent-Knopf),
 * gezeichnet in genau diesem Stil.
 */
@Composable
private fun StyleTile(
    style: UiStyle,
    preview: Palette,
    ringStyle: RingStyle,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = Funke.colors
    val sel by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Medium),
        label = "styleSel",
    )
    val shape = Shapes.card
    Column(
        modifier
            .bouncyClickable(onClick = onClick)
            .graphicsLayer {
                val s = 0.95f + 0.05f * sel
                scaleX = s
                scaleY = s
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(112.dp)
                .clip(shape)
                .border(2.dp, current.accent.copy(alpha = sel), shape)
        ) {
            CompositionLocalProvider(LocalPalette provides preview, LocalUiStyle provides style) {
                AmbientBackground {
                    Column(Modifier.fillMaxSize().padding(Space.s3)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .glass(Shapes.sm)
                                .padding(Space.s2),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TimerRing(0.68f, ringStyle, Modifier.size(34.dp), stroke = 3.5.dp)
                            Spacer(Modifier.width(Space.s2))
                            Column(Modifier.weight(1f)) {
                                Box(Modifier.fillMaxWidth(0.85f).height(5.dp).clip(Shapes.pill).background(preview.text.copy(alpha = 0.55f)))
                                Spacer(Modifier.height(Space.s1))
                                Box(Modifier.fillMaxWidth(0.5f).height(5.dp).clip(Shapes.pill).background(preview.textTertiary.copy(alpha = 0.6f)))
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .accentFill(Shapes.pill, glow = 4.dp, outline = 1.5.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(Space.s2))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(style.label, style = MaterialTheme.typography.titleSmall, color = current.text)
            Spacer(Modifier.size(5.dp))
            Box(
                Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = sel
                        scaleY = sel
                        alpha = sel.coerceIn(0f, 1f)
                    }
                    .clip(CircleShape)
                    .background(current.accent),
                contentAlignment = Alignment.Center,
            ) {
                CheckIcon(current.onAccent, Modifier.size(9.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Timer
// ---------------------------------------------------------------------------------------------

@Composable
private fun TimerPage(state: AppState) {
    val p = Funke.colors
    SectionLabel("Ring", Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    // große Vorschau des gewählten Rings
    val demo by rememberLoop(
        initialValue = 0.95f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "ringShowP",
    )
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(vertical = Space.s5, horizontal = Space.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(176.dp), contentAlignment = Alignment.Center) {
            Crossfade(targetState = state.ringStyle, animationSpec = tween(280), label = "ringShowStyle") { style ->
                TimerRing(demo, style, Modifier.fillMaxSize(), stroke = 9.dp)
            }
            if (state.sidekick && state.readingLumi != de.funke.app.ui.ReadingLumi.Off) {
                Lumi2D(LumiPose.Reading, Modifier.size(116.dp).offset(y = (-6).dp), tappable = false)
            } else {
                Text("24:13", style = MaterialTheme.typography.headlineLarge, color = p.text)
            }
        }
        Spacer(Modifier.height(Space.s4))
        AnimatedContent(
            targetState = state.ringStyle,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
            label = "ringDesc",
        ) { style ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(style.label, style = MaterialTheme.typography.titleMedium, color = p.text)
            }
        }
    }
    Spacer(Modifier.height(Space.s3))
    RingStyle.entries.chunked(3).forEach { rowItems ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            rowItems.forEach { style ->
                RingTile(style, selected = state.ringStyle == style, onClick = { state.updateRingStyle(style) }, modifier = Modifier.weight(1f))
            }
            repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(Space.s3))
    }

    Spacer(Modifier.height(Space.s5))
    SectionLabel(tr("Timer", "Timer"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s4)
    ) {
        TimerPreview(state.timerStyle)
        Spacer(Modifier.height(Space.s4))
        val options = TimerStyleOption.entries
        SegmentedToggle(
            options = options.map { it.label },
            selected = options.indexOf(state.timerStyle),
            onSelect = { state.setTimerStyleOption(options[it]) },
        )
        Spacer(Modifier.height(Space.s2))
        Text(state.timerStyle.description, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, modifier = Modifier.padding(horizontal = Space.s2))
    }

    // ----- Lumi beim Lesen -----
    Spacer(Modifier.height(Space.s5))
    SectionLabel(tr("Lumi in der Fokuszeit", "Lumi during focus"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s4)
    ) {
        val lumiOptions = de.funke.app.ui.ReadingLumi.entries
        SegmentedToggle(
            options = lumiOptions.map { it.label },
            selected = lumiOptions.indexOf(state.readingLumi),
            onSelect = { state.updateReadingLumi(lumiOptions[it]) },
        )
        Spacer(Modifier.height(Space.s2))
        Text(
            state.readingLumi.description + if (!state.sidekick && state.readingLumi != de.funke.app.ui.ReadingLumi.Off) tr(" (Lumi ist gerade ganz aus.)", " (Lumi is switched off entirely right now.)") else "",
            style = MaterialTheme.typography.bodySmall,
            color = p.textTertiary,
            modifier = Modifier.padding(horizontal = Space.s2),
        )
    }
}

/** Kachel für eine Ring-Variante mit laufender Mini-Vorschau. */
@Composable
private fun RingTile(style: RingStyle, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val sel by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Medium),
        label = "ringSel",
    )
    val shape = Shapes.card
    Column(
        modifier
            .bouncyClickable(shape = shape, role = Role.RadioButton, onClick = onClick)
            .glass(shape)
            .background(p.accentSoft.copy(alpha = p.accentSoft.alpha * sel))
            .border(1.5.dp, p.accent.copy(alpha = sel), shape)
            .padding(vertical = Space.s4, horizontal = Space.s2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimerRing(0.72f, style, Modifier.size(64.dp), stroke = 4.5.dp)
        Spacer(Modifier.height(Space.s3))
        Text(style.label, style = MaterialTheme.typography.labelLarge, color = if (selected) p.text else p.textSecondary, maxLines = 1)
    }
}

// ---------------------------------------------------------------------------------------------
// Lumi, Sperre, Vibration, Über
// ---------------------------------------------------------------------------------------------

@Composable
private fun LockPage(state: AppState) {
    val p = Funke.colors
    val context = androidx.compose.ui.platform.LocalContext.current
    LockCard(enabled = state.lockEnabled)
    Spacer(Modifier.height(Space.s4))
    // Pflicht-Hinweis für die Bedienungshilfe: kurz und ehrlich
    Row(Modifier.fillMaxWidth().padding(horizontal = Space.s2), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(R.drawable.ic_eye), contentDescription = null, tint = p.textTertiary, modifier = Modifier.size(Sizes.iconSm))
        Spacer(Modifier.width(Space.s2))
        Text(
            tr("Sieht nur, welche App vorne ist. Liest keine Inhalte.", "Only sees which app is in front. Reads no content."),
            style = MaterialTheme.typography.bodySmall,
            color = p.textTertiary,
        )
    }
    Spacer(Modifier.height(Space.s5))
    var guide by remember { mutableStateOf(false) }
    if (guide) AccessibilityGuide(onDismiss = { guide = false })
    PrimaryButton(
        text = if (state.lockEnabled) tr("Bedienungshilfen öffnen", "Open accessibility settings") else tr("Sperre einschalten", "Turn on the lock"),
        // Einschalten erst nach dem Hinweis mit Zustimmung; ist sie schon an, direkt in die Einstellungen
        onClick = { if (state.lockEnabled) de.funke.app.lock.SessionLock.openAccessibilitySettings(context) else guide = true },
    )
}

@Composable
private fun HapticsPage(state: AppState) {
    val haptics = LocalHaptics.current
    SectionLabel(tr("Stärke", "Strength"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    SegmentedToggle(
        options = HapticLabels,
        selected = state.hapticLevel,
        onSelect = { level ->
            state.updateHapticLevel(level)
            haptics?.level = level
            haptics?.click()
        },
    )
    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Art", "Type"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    val modes = de.funke.app.ui.HapticMode.entries
    SegmentedToggle(
        options = listOf(tr("Präzise", "Precise"), tr("Kräftig", "Strong")),
        selected = modes.indexOf(state.hapticMode),
        onSelect = { i ->
            state.updateHapticMode(modes[i])
            haptics?.mode = modes[i]
            haptics?.success()
        },
    )
}


// ---------------------------------------------------------------------------------------------
// Aufgaben
// ---------------------------------------------------------------------------------------------

@Composable
internal fun TasksSettings(state: AppState) {
    val p = Funke.colors
    val requested = state.settingsRequest?.substringAfter("tasks:", "")?.let { id -> de.funke.app.ui.challenge.Subject.entries.firstOrNull { it.id == id } }
    var prefSubject by rememberSaveable { mutableStateOf((requested ?: state.practiceSubject ?: de.funke.app.ui.challenge.Subject.Physics).id) }
    androidx.compose.runtime.LaunchedEffect(requested) {
        if (requested != null) prefSubject = requested.id
        state.settingsRequest = null
    }
    // Einfach: Fächer an oder aus. Wer mehr will, klappt "Genauer" auf (Themen und Aufgabenarten).
    @Suppress("UNUSED_VARIABLE") val pv = state.taskPrefsVersion
    SectionLabel(tr("Fächer", "Subjects"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    de.funke.app.ui.challenge.PracticeSubjects.chunked(2).forEach { pair ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            pair.forEach { subject ->
                val on = de.funke.app.ui.challenge.TaskPrefs.subjectOn(subject)
                SubjectChip(subject, on, Modifier.weight(1f)) { state.setSubjectOn(subject, !on) }
            }
            if (pair.size == 1) Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(Space.s3))
    }

    var detailed by rememberSaveable { mutableStateOf(requested != null && requested.id.isNotEmpty()) }
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.99f) { detailed = !detailed }
            .glass(Shapes.card)
            .padding(horizontal = Space.s5, vertical = Space.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(tr("Genauer einstellen", "Fine-tune"), style = MaterialTheme.typography.titleSmall, color = p.text)
            Text(tr("Lieblingsthemen und Aufgabenarten", "Favourite topics and task types"), style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
        }
        de.funke.app.ui.components.Chevron(expanded = detailed)
    }
    androidx.compose.animation.AnimatedVisibility(visible = detailed) {
        Column {
            Spacer(Modifier.height(Space.s5))
            TaskPrefsSection(
                state = state,
                subject = de.funke.app.ui.challenge.Subject.entries.firstOrNull { it.id == prefSubject } ?: de.funke.app.ui.challenge.Subject.Physics,
                onSubject = { prefSubject = it.id },
            )
        }
    }

    Spacer(Modifier.height(Space.s7))
    ToggleRow(title = tr("Töne", "Sounds"), checked = state.soundOn, onChange = state::updateSound)

    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Fragen beim Abbrechen und in der Denkpause", "Questions when stopping and in think breaks"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    QuestionSettings(state)
}

/**
 * Fragen für Abbrechen und Denkpause: Schwierigkeit und Fächer. Dieselbe Einstellung an beiden Stellen
 * (Einstellungen → Aufgaben und Reiter "Denkpause").
 */
@Composable
internal fun QuestionSettings(state: AppState) {
    val cs = state.challengeSettings
    SegmentedToggle(
        options = Difficulty.entries.map { it.label },
        selected = cs.difficulty.ordinal,
        onSelect = { state.updateChallengeDifficulty(Difficulty.entries[it]) },
    )
    Spacer(Modifier.height(Space.s4))
    de.funke.app.ui.challenge.PracticeSubjects.chunked(2).forEach { pair ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            pair.forEach { subject ->
                val mine = Topic.entries.filter { it.subject == subject }
                val on = mine.any { it in cs.topics }
                SubjectChip(subject, on, Modifier.weight(1f)) { state.toggleChallengeSubject(subject) }
            }
            if (pair.size == 1) Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(Space.s3))
    }
}

/** Fach zum An- und Ausschalten: kleines Bild, Name, Haken. */
@Composable
private fun SubjectChip(subject: de.funke.app.ui.challenge.Subject, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val p = Funke.colors
    val shape = Shapes.lg
    val sel by animateFloatAsState(if (on) 1f else 0f, Motion.spec(Motion.Base), label = "subjSel")
    Row(
        modifier
            .heightIn(min = 56.dp)
            .bouncyClickable(shape = shape, pressedScale = 0.97f, role = Role.Checkbox, onClick = onClick)
            .glass(shape)
            .background(p.accentSoft.copy(alpha = p.accentSoft.alpha * sel))
            .border(1.5.dp, p.accent.copy(alpha = sel), shape)
            .padding(horizontal = Space.s3, vertical = Space.s2)
            .semantics {
                role = Role.Checkbox
                stateDescription = if (on) tr("An", "On") else tr("Aus", "Off")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.foundation.Image(
            painterResource(subject.art),
            contentDescription = null,
            modifier = Modifier.size(28.dp).graphicsLayer { alpha = 0.45f + 0.55f * sel },
        )
        Spacer(Modifier.width(Space.s2))
        Text(subject.label, style = MaterialTheme.typography.labelLarge, color = if (on) p.text else p.textSecondary, maxLines = 1, modifier = Modifier.weight(1f))
    }
}


@Composable
private fun AboutPage(state: AppState) {
    val p = Funke.colors
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s6),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LuminaLogo(Modifier.size(84.dp))
        Spacer(Modifier.height(Space.s3))
        Text("Lumina", style = MaterialTheme.typography.headlineMedium, color = p.text)
        Text("Version ${appVersion()} (GPL-3.0)", style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.textTertiary)
    }
    Spacer(Modifier.height(Space.s3))
    PillButton(tr("Intro noch einmal", "Show the intro again"), onClick = state::restartOnboarding, modifier = Modifier.fillMaxWidth())
    CrashReport()

    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Quellen & Lizenzen", "Sources & licenses"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    val context = androidx.compose.ui.platform.LocalContext.current
    var open by rememberSaveable { mutableStateOf<String?>(null) }
    val credits = listOf(
        Triple("Lumina", "Simon Bentlage · GPL-3.0", { readRaw(context, de.funke.app.R.raw.gpl3) }),
        Triple("GSM8K", "OpenAI · MIT", { GSM8K_LICENSE }),
        Triple("OpenStax University Physics", "Rice University · CC BY 4.0", { OPENSTAX_NOTE }),
        Triple("Calculus Made Easy", "Silvanus P. Thompson · gemeinfrei / public domain", { THOMPSON_NOTE }),
        Triple("Inter", "Rasmus Andersson · SIL OFL 1.1", { readRaw(context, de.funke.app.R.raw.inter_ofl) }),
        Triple("Nunito", "The Nunito Project Authors · SIL OFL 1.1", { readRaw(context, de.funke.app.R.raw.nunito_ofl) }),
        Triple("Fredoka", "The Fredoka Project Authors · SIL OFL 1.1", { readRaw(context, de.funke.app.R.raw.fredoka_ofl) }),
        Triple("Lucide", "Lucide Contributors · ISC", { readRaw(context, de.funke.app.R.raw.lucide_license) }),
        Triple("chatcn", "Aaditya · MIT", { CHATCN_LICENSE }),
    )
    Column(Modifier.fillMaxWidth().glass(Shapes.card)) {
        credits.forEachIndexed { i, (name, line, text) ->
            if (i > 0) Box(Modifier.padding(start = Space.s5).fillMaxWidth().height(1.dp).background(p.divider))
            val expanded = open == name
            Column(
                Modifier
                    .fillMaxWidth()
                    .bouncyClickable(shape = androidx.compose.ui.graphics.RectangleShape, pressedScale = 1f) { open = if (expanded) null else name }
                    .smoothResize()
                    .padding(horizontal = Space.s5, vertical = Space.s4),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(name, style = MaterialTheme.typography.titleSmall, color = p.text)
                        Text(line, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
                    }
                    Icon(
                        painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = p.textTertiary,
                        modifier = Modifier.size(Sizes.iconSm).graphicsLayer { rotationZ = if (expanded) 90f else 0f },
                    )
                }
                if (expanded) {
                    Spacer(Modifier.height(Space.s3))
                    Text(
                        remember(name) { text() },
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 10.sp, lineHeight = 16.sp),
                        color = p.textTertiary,
                    )
                }
            }
        }
    }
}

/** Letzter Absturz (falls es einen gab): kurz anzeigen, teilen oder löschen. */
@Composable
private fun CrashReport() {
    val p = Funke.colors
    val context = androidx.compose.ui.platform.LocalContext.current
    var report by remember { mutableStateOf(de.funke.app.CrashLog.read(context)) }
    val text = report ?: return
    val headline = remember(text) {
        text.lineSequence().drop(2).firstOrNull { it.isNotBlank() }?.substringAfterLast("Exception: ")?.take(120).orEmpty()
    }
    Spacer(Modifier.height(Space.s5))
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.card)
            .padding(Space.s5),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(p.danger))
            Spacer(Modifier.width(Space.s2))
            Text(tr("Letzter Absturz", "Last crash"), style = MaterialTheme.typography.titleSmall, color = p.text)
        }
        if (headline.isNotBlank()) {
            Spacer(Modifier.height(Space.s1))
            Text(headline, style = MaterialTheme.typography.bodySmall, color = p.textTertiary, maxLines = 2)
        }
        Spacer(Modifier.height(Space.s4))
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            PillButton(
                tr("Teilen", "Share"),
                filled = true,
                onClick = {
                    runCatching {
                        context.startActivity(
                            android.content.Intent.createChooser(
                                android.content.Intent(android.content.Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(LegalTexts.CONTACT))
                                    .putExtra(android.content.Intent.EXTRA_SUBJECT, "Lumina – Absturz")
                                    .putExtra(android.content.Intent.EXTRA_TEXT, text),
                                null,
                            ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
            PillButton(
                tr("Löschen", "Delete"),
                onClick = {
                    de.funke.app.CrashLog.clear(context)
                    report = null
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun readRaw(context: android.content.Context, id: Int): String =
    runCatching { context.resources.openRawResource(id).bufferedReader().use { it.readText() } }.getOrDefault("")

private const val CHATCN_LICENSE = """MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."""


/** Zeile mit Titel, Erklärung und Schalter. */
// ---------------------------------------------------------------------------------------------
// Notizen
// ---------------------------------------------------------------------------------------------

@Composable
private fun NotesSettingsPage(state: AppState) {
    val p = Funke.colors
    SectionLabel(tr("Diktiersprache", "Dictation language"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    val langs = listOf("auto", "de", "en")
    SegmentedToggle(
        options = listOf(tr("Wie Handy", "Like phone"), "Deutsch", "English"),
        selected = langs.indexOf(state.dictationLang).coerceAtLeast(0),
        onSelect = { state.updateDictationLang(langs[it]) },
    )

    Spacer(Modifier.height(Space.s3))
    Spacer(Modifier.height(Space.s5))
    SectionLabel(tr("Papier", "Paper"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    de.funke.app.ui.NotePaper.entries.chunked(3).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            row.forEach { paper ->
                val on = state.notePaper == paper
                val shape = Shapes.lg
                Column(
                    Modifier
                        .weight(1f)
                        .bouncyClickable(shape = shape) { state.updateNotePaper(paper) }
                        .clip(shape)
                        .background(if (on) p.accent.copy(alpha = 0.14f) else p.glassAlpha(0.05f))
                        .then(if (on) Modifier.border(1.5.dp, p.accent, shape) else Modifier)
                        .padding(Space.s3),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PaperSketch(paper, if (on) p.accent else p.textTertiary, Modifier.fillMaxWidth().height(46.dp))
                    Spacer(Modifier.height(Space.s2))
                    Text(paper.label, style = MaterialTheme.typography.labelMedium, color = if (on) p.accent else p.textSecondary)
                }
            }
            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(Space.s3))
    }

    Spacer(Modifier.height(Space.s5))
    SectionLabel(tr("Aufräumen", "Clean up"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    ToggleRow(
        title = "Lumi",
        checked = state.aiProvider == AiProvider.Lumina,
        onChange = { on -> state.updateAi(if (on) AiProvider.Lumina else AiProvider.Off) },
    )
    Spacer(Modifier.height(Space.s4))
    Text(tr("Form beim Aufräumen", "Clean-up format"), style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.padding(horizontal = Space.s2))
    Spacer(Modifier.height(Space.s2))
    val formats = de.funke.app.notes.AiCleanup.Format.entries
    SegmentedToggle(
        options = listOf(tr("Text", "Text"), tr("Stichpunkte", "Bullets")),
        selected = formats.indexOf(state.noteFormat),
        onSelect = { state.updateNoteFormat(formats[it]) },
    )
    if (state.aiProvider == AiProvider.Lumina) {
        Spacer(Modifier.height(Space.s4))
        LuminaTestCard(state)
        Spacer(Modifier.height(Space.s1))
        var showServer by remember { mutableStateOf(state.aiServer != de.funke.app.notes.AiCleanup.DEFAULT_SERVER) }
        var showPrivacy by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                tr("Datenschutz", "Privacy"),
                style = MaterialTheme.typography.labelMedium,
                color = p.accent,
                modifier = Modifier.clip(Shapes.pill).clickable { showPrivacy = true }.padding(horizontal = Space.s2, vertical = Space.s2),
            )
            if (state.aiConsent) {
                Text(
                    tr("Zustimmung zurücknehmen", "Withdraw consent"),
                    style = MaterialTheme.typography.labelMedium,
                    color = p.textTertiary,
                    modifier = Modifier.clip(Shapes.pill).clickable { state.updateAiConsent(false) }.padding(horizontal = Space.s2, vertical = Space.s2),
                )
            }
            Text(
                if (showServer) tr("Eigener Server ▴", "Own server ▴") else tr("Eigener Server ▾", "Own server ▾"),
                style = MaterialTheme.typography.labelMedium,
                color = p.textTertiary,
                modifier = Modifier.clip(Shapes.pill).clickable { showServer = !showServer }.padding(horizontal = Space.s2, vertical = Space.s2),
            )
        }
        if (showPrivacy) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showPrivacy = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            ) { LegalSheet(LegalDoc.Privacy, onClose = { showPrivacy = false }) }
        }
        if (showServer) {
            var url by remember { mutableStateOf(state.aiServer) }
            Row(
                Modifier.fillMaxWidth().glass(Shapes.lg).padding(horizontal = Space.s4, vertical = Space.s4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = url,
                    onValueChange = { url = it.trim() },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = p.text),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(p.accent),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    if (url == state.aiServer) tr("Gespeichert", "Saved") else tr("Speichern", "Save"),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (url == state.aiServer) p.textTertiary else p.accent,
                    modifier = Modifier.clip(Shapes.pill).clickable { state.updateAiServer(url); url = state.aiServer }.padding(Space.s2),
                )
            }
            Spacer(Modifier.height(Space.s1))
            Text(
                tr(
                    "Nur für Leute mit eigenem Lumi-Worker (Anleitung im Projekt unter server/). Leer = Standard.",
                    "Only for people running their own Lumi worker (guide in the project under server/). Empty = default.",
                ),
                style = MaterialTheme.typography.labelSmall,
                color = p.textTertiary,
                modifier = Modifier.padding(horizontal = Space.s2),
            )
        }
    }
}

/**
 * Lumi: die drei Stufen pro Tag (beste KI, Standard, Spar-KI), wie oft heute noch, und
 * "Verbindung testen" – fragt jedes Modell einmal und zeigt pro Modell, ob es antwortet und wie schnell.
 * Bei einem Fehler steht der genaue Grund da (z. B. "Worker veraltet" oder "Binding AI fehlt").
 */
@Composable
private fun LuminaTestCard(state: AppState) {
    val p = Funke.colors
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var running by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<Result<de.funke.app.notes.AiCleanup.Health>?>(null) }
    val german = de.funke.app.ui.Lang.german
    de.funke.app.ui.components.Card(padding = androidx.compose.foundation.layout.PaddingValues(Space.s4)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(tr("So räumt Lumi auf", "How Lumi tidies"), style = MaterialTheme.typography.titleSmall, color = p.text)
                Spacer(Modifier.height(Space.s1))
                Text(
                    tr(
                        "Am Tag 3× mit der besten KI, danach bis 8× mit der Standard-KI und bis 20× mit der Spar-KI. Danach räumt das Handy selbst auf.",
                        "Each day 3× with the best AI, then up to 8× with the standard AI and up to 20× with the lite AI. After that the phone tidies by itself.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = p.textSecondary,
                )
                state.aiLeft?.let { left ->
                    Spacer(Modifier.height(Space.s2))
                    Text(
                        tr("Heute noch ${left}×", "${left}× left today"),
                        style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"),
                        color = p.textTertiary,
                    )
                }
            }
        }
        Spacer(Modifier.height(Space.s4))
        de.funke.app.ui.components.LipButton(
            if (running) tr("Teste …", "Testing …") else tr("Verbindung testen", "Test connection"),
            onClick = {
                if (running) return@LipButton
                running = true
                result = null
                scope.launch {
                    result = de.funke.app.notes.AiCleanup.health(state.aiServer)
                    running = false
                }
            },
            tone = de.funke.app.ui.components.LipTone.Neutral,
            enabled = !running,
            icon = R.drawable.ic_activity,
            height = Sizes.buttonSmall,
            modifier = Modifier.fillMaxWidth(),
        )
        val r = result
        Unfold(
            r != null,
            enter = androidx.compose.animation.fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) + androidx.compose.animation.expandVertically(tween(Motion.Medium, easing = Motion.EaseOut), clip = false),
            exit = androidx.compose.animation.fadeOut(tween(Motion.Fast)) + androidx.compose.animation.shrinkVertically(tween(Motion.Fast), clip = false),
        ) {
            Column(Modifier.padding(top = Space.s3)) {
                val h = r?.getOrNull()
                when {
                    r == null -> {}
                    h == null -> TestLine(false, tr("Nicht erreichbar", "Not reachable"), tr("Kein Netz oder falsche Adresse", "No connection or wrong address"))
                    h.models.isEmpty() -> TestLine(false, tr("Lumi", "Lumi"), de.funke.app.notes.AiCleanup.reasonLabel(h.reason, german))
                    else -> {
                        val order = listOf("big", "mid", "small")
                        h.models.sortedBy { order.indexOf(it.key).let { i -> if (i < 0) 9 else i } }.forEach { m ->
                            val name = de.funke.app.notes.AiCleanup.tierLabel(m.key, m.id, german) ?: m.key
                            TestLine(
                                m.ok,
                                name + ", " + m.id.substringAfterLast('/'),
                                if (m.ok) "%.1f s".format(m.ms / 1000f) else (m.error ?: tr("antwortet nicht", "no answer")),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Eine Zeile im Test-Ergebnis: grüner/roter Punkt, Name, rechts bzw. darunter das Ergebnis. */
@Composable
private fun TestLine(ok: Boolean, title: String, detail: String) {
    val p = Funke.colors
    Row(Modifier.fillMaxWidth().padding(vertical = Space.s1), verticalAlignment = Alignment.Top) {
        Box(Modifier.padding(top = 6.dp).size(8.dp).clip(CircleShape).background(if (ok) p.success else p.danger))
        Spacer(Modifier.width(Space.s3))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = p.text)
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum, ss03"),
                color = if (ok) p.textTertiary else p.danger,
                maxLines = 4,
            )
        }
    }
}

/** Wie die Sprache beim Diktieren aussieht (Balken, Kreis, Kugel …). */
@Composable
private fun VoiceVisualPicker(state: AppState) {
    val p = Funke.colors
    val sim = remember { de.funke.app.ui.components.SpeechSim(seed = 3f) }
    de.funke.app.ui.components.VoiceVisual.entries.chunked(3).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            row.forEach { v ->
                val on = state.voiceVisual == v
                val shape = Shapes.lg
                Column(
                    Modifier
                        .weight(1f)
                        .bouncyClickable(shape = shape) { state.updateVoiceVisual(v) }
                        .clip(shape)
                        .background(if (on) p.accent.copy(alpha = 0.14f) else p.glassAlpha(0.05f))
                        .then(if (on) Modifier.border(1.5.dp, p.accent, shape) else Modifier)
                        .padding(vertical = Space.s3),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.height(56.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        de.funke.app.ui.components.VoiceVisualizer(
                            visual = v,
                            state = de.funke.app.ui.components.VoiceState.Listening,
                            level = sim::level,
                            color = if (on) p.accent else p.textSecondary,
                            modifier = if (v == de.funke.app.ui.components.VoiceVisual.Orb || v == de.funke.app.ui.components.VoiceVisual.Radial) Modifier.size(56.dp)
                            else Modifier.fillMaxWidth(0.8f).height(40.dp),
                        )
                    }
                    Spacer(Modifier.height(Space.s2))
                    Text(v.label, style = MaterialTheme.typography.labelMedium, color = if (on) p.accent else p.textSecondary)
                }
            }
            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(Space.s3))
    }
}

/** Kleine Skizze eines Notiz-Blatts. */
@Composable
private fun PaperSketch(paper: de.funke.app.ui.NotePaper, tint: Color, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Canvas(modifier.clip(Shapes.xs).background(if (paper == de.funke.app.ui.NotePaper.Plain) Color.Transparent else p.glassAlpha(0.05f))) {
        val w = size.width
        val h = size.height
        val c = tint.copy(alpha = 0.55f)
        val step = h / 4f
        when (paper) {
            de.funke.app.ui.NotePaper.Plain -> {
                drawLine(tint, Offset(w * 0.12f, h * 0.35f), Offset(w * 0.75f, h * 0.35f), strokeWidth = 3f, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * 0.12f, h * 0.62f), Offset(w * 0.55f, h * 0.62f), strokeWidth = 3f, cap = StrokeCap.Round)
            }
            de.funke.app.ui.NotePaper.Lines, de.funke.app.ui.NotePaper.Notebook -> {
                for (i in 1..3) drawLine(c, Offset(0f, step * i), Offset(w, step * i), strokeWidth = 1.5f)
                if (paper == de.funke.app.ui.NotePaper.Notebook) drawLine(tint, Offset(w * 0.18f, 0f), Offset(w * 0.18f, h), strokeWidth = 2f)
            }
            de.funke.app.ui.NotePaper.Grid -> {
                val g = h / 5f
                var y = g
                while (y < h) { drawLine(c, Offset(0f, y), Offset(w, y), strokeWidth = 1f); y += g }
                var x = g
                while (x < w) { drawLine(c, Offset(x, 0f), Offset(x, h), strokeWidth = 1f); x += g }
            }
            de.funke.app.ui.NotePaper.Dots -> {
                val g = h / 5f
                var y = g
                while (y < h) {
                    var x = g
                    while (x < w) { drawCircle(tint, 1.8f, Offset(x, y)); x += g }
                    y += g
                }
            }
        }
    }
}


// ---------------------------------------------------------------------------------------------
// Benachrichtigungen
// ---------------------------------------------------------------------------------------------

internal fun timeText(h: Int, m: Int) = "%02d:%02d".format(h, m)

@Composable
private fun NotificationsPage(state: AppState) {
    val p = Funke.colors
    val context = androidx.compose.ui.platform.LocalContext.current
    var allowed by remember { mutableStateOf(de.funke.app.notify.Reminders.allowed(context)) }
    var asked by rememberSaveable { mutableStateOf(false) }
    var testInfo by remember { mutableStateOf<String?>(null) }
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        allowed = granted && de.funke.app.notify.Reminders.allowed(context)
        if (granted) de.funke.app.notify.Reminders.schedule(context)
    }
    // Zurück aus den System-Einstellungen: neu nachsehen
    val owner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(owner) {
        val obs = androidx.lifecycle.LifecycleEventObserver { _, e ->
            if (e == androidx.lifecycle.Lifecycle.Event.ON_RESUME) allowed = de.funke.app.notify.Reminders.allowed(context)
        }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }


    if (!allowed) {
        Column(
            Modifier
                .fillMaxWidth()
                .glass(Shapes.card, strong = true)
                .padding(Space.s5),
        ) {
            Text(tr("Benachrichtigungen sind aus", "Notifications are off"), style = MaterialTheme.typography.titleMedium, color = p.text)
            Spacer(Modifier.height(Space.s4))
            PillButton(
                tr("Erlauben", "Allow"),
                filled = true,
                onClick = {
                    // Beim ersten Mal fragt Android direkt. Wurde schon abgelehnt, geht es nur noch über die Einstellungen.
                    if (android.os.Build.VERSION.SDK_INT >= 33 && !asked &&
                        androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        asked = true
                        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        de.funke.app.notify.Reminders.openSystemSettings(context)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(Space.s5))
    }

    if (allowed) {
        // Erlaubt: grüner Punkt und fertig
        Row(
            Modifier.fillMaxWidth().glass(Shapes.card).padding(horizontal = Space.s5, vertical = Space.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(p.success))
            Spacer(Modifier.width(Space.s3))
            Text(tr("Benachrichtigungen erlaubt", "Notifications allowed"), style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
            de.funke.app.ui.components.TextAction(tr("System", "System"), onClick = { de.funke.app.notify.Reminders.openSystemSettings(context) }, color = p.accent)
        }
    }
    // Welche Erinnerungen wann kommen, steht an einem Ort: im Reiter Denkpause
    Spacer(Modifier.height(Space.s3))
    Row(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.card)
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.99f) { de.funke.app.ui.DeepLink.gateSection = 1; de.funke.app.ui.DeepLink.tab = Tabs.Gate }
            .padding(horizontal = Space.s5, vertical = Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(de.funke.app.R.drawable.ic_bell), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.icon))
        Spacer(Modifier.width(Space.s3))
        Text(tr("Erinnerungen einstellen", "Set up reminders"), style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
        de.funke.app.ui.components.ChevronRight()
    }
    Spacer(Modifier.height(Space.s6))
    PillButton(
        tr("Probe-Benachrichtigung senden", "Send a test notification"),
        onClick = {
            testInfo = when {
                !de.funke.app.notify.Reminders.test(context) -> tr("Geht nicht: Benachrichtigungen sind nicht erlaubt.", "Not possible: notifications aren't allowed.")
                !de.funke.app.notify.Reminders.popupAllowed(context) -> tr("Gesendet. Im System steht Lumina auf leise, deshalb nur in der Leiste.", "Sent. Lumina is set to silent in the system, so it only lands in the shade.")
                else -> null
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
    testInfo?.let {
        Spacer(Modifier.height(Space.s2))
        Text(it, style = MaterialTheme.typography.bodySmall, color = p.textSecondary, modifier = Modifier.padding(horizontal = Space.s2))
    }
}

/** Uhrzeit in 15-Minuten-Schritten: [−] 19:00 [+] */
@Composable
internal fun TimeStepper(hour: Int, minute: Int, label: String = tr("Uhrzeit", "Time"), onChange: (Int, Int) -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    fun shift(delta: Int) {
        val t = ((hour * 60 + minute + delta) % 1440 + 1440) % 1440
        haptics?.tick()
        onChange(t / 60, t % 60)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.card)
            .padding(horizontal = Space.s3, vertical = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = p.textSecondary, modifier = Modifier.padding(start = Space.s2).weight(1f))
        StepButton("−") { shift(-15) }
        Text(
            timeText(hour, minute),
            style = MaterialTheme.typography.titleLarge,
            color = p.text,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.width(92.dp),
        )
        StepButton("+") { shift(15) }
    }
}

@Composable
private fun StepButton(label: String, onClick: () -> Unit) {
    val p = Funke.colors
    Box(
        Modifier
            .size(Sizes.buttonSmall)
            .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, onClick = onClick)
            .clip(CircleShape)
            .background(p.track),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.titleLarge, color = p.text)
    }
}

/** Zeile mit Titel und Schalter; die ganze Zeile ist tippbar. */
@Composable
internal fun ToggleRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .bouncyClickable(shape = Shapes.card, pressedScale = 0.98f, role = Role.Switch) { onChange(!checked) }
            .glass(Shapes.card)
            .padding(horizontal = Space.s5, vertical = Space.s3)
            .semantics {
                role = Role.Switch
                stateDescription = if (checked) tr("An", "On") else tr("Aus", "Off")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(Space.s4))
        LuminaSwitch(checked)
    }
}

/** Schalter im Look der App (nur Anzeige; getippt wird auf die ganze Zeile). */
@Composable
internal fun LuminaSwitch(checked: Boolean) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val x by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        // Daumen gleitet mit ganz leichtem Nachfedern (transitions.dev "toggle"), sonst sofort
        animationSpec = if (reducedMotion) androidx.compose.animation.core.snap() else spring(dampingRatio = 0.75f, stiffness = 700f),
        label = "switch",
    )
    Box(
        Modifier
            .size(width = 52.dp, height = 32.dp)
            .clip(Shapes.pill)
            .drawBehind { drawRect(lerp(p.glassAlpha(if (p.isDark) 0.16f else 0.12f), p.accent, x.coerceIn(0f, 1f))) }
            .then(if (cartoon) Modifier.border(2.dp, p.ink, Shapes.pill) else Modifier)
            .padding(Space.s1)
    ) {
        Box(
            Modifier
                .offset { IntOffset((20.dp.toPx() * x).roundToInt(), 0) }
                .size(26.dp)
                .then(if (Funke.flat) Modifier else Modifier.softShadow(CircleShape, alpha = 0.18f, blur = 4.dp, offsetY = 1.dp))
                .clip(CircleShape)
                .background(if (checked) p.onAccent.takeIf { p.isDark } ?: Color.White else Color.White)
        )
    }
}

/** Kleiner Probelauf des Timers: zählt in einer Schleife herunter. */
@Composable
private fun TimerPreview(style: TimerStyleOption) {
    val p = Funke.colors
    var demo by remember { mutableIntStateOf(75) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            demo = if (demo <= 1) 75 else demo - 1
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(Shapes.lg)
            .background(p.bg.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Crossfade(targetState = style, animationSpec = tween(300), label = "timerPreview") { current ->
            when (current) {
                TimerStyleOption.Vapor -> VaporDigits(
                    seconds = demo,
                    digitCount = 4,
                    color = p.text,
                    description = tr("Vorschau des Dampf-Timers", "Preview of the vapor timer"),
                    intro = true,
                    modifier = Modifier.fillMaxSize(),
                )
                TimerStyleOption.Classic -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "%02d:%02d".format(demo / 60, demo % 60),
                        style = TimerStyle,
                        color = p.text,
                        textAlign = TextAlign.Center,
                    )
                }
                // Vorschau: eine Minute ist hier 6 Sekunden lang, damit man das Umklappen sieht
                TimerStyleOption.Flip -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    de.funke.app.ui.components.FlipDigits(60 + (demo / 6) * 60, cardHeight = 64.dp)
                }
                TimerStyleOption.Terminal -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    de.funke.app.ui.components.TerminalDigits(demo, big = 46.dp)
                }
            }
        }
    }
}

/**
 * Kachel für ein Farbschema: eine kleine Vorschau mit Lichtfleck, Akzent-Pille und Namen.
 * Gewählt hebt sie sich mit Schein und Feder ab, ein Haken springt ins Bild.
 */
@Composable
private fun PaletteTile(
    palette: Palette,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = Shapes.card
    val sel by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Medium),
        label = "paletteSel",
    )
    val flat = Funke.flat
    Box(
        modifier
            .height(88.dp)
            .bouncyClickable(onClick = onClick)
            .graphicsLayer {
                val s = 0.97f + 0.03f * sel
                scaleX = s
                scaleY = s
            }
            .then(if (flat) Modifier else Modifier.shadow((2f + 14f * sel).dp, shape, ambientColor = palette.accent, spotColor = palette.accent))
            .clip(shape)
            .background(if (flat) SolidColor(palette.bg) else Brush.linearGradient(listOf(palette.bg, palette.bgMid)))
            .then(if (flat) Modifier.border(1.dp, palette.glass.copy(alpha = 0.10f), shape) else Modifier)
            .drawBehind {
                if (!flat) drawCircle(
                    Brush.radialGradient(
                        listOf(palette.glowA.copy(alpha = 0.45f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.1f),
                        radius = size.width * 0.7f,
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.85f, size.height * 0.1f),
                )
            }
            .padding(Space.s3),
    ) {
        // Farbmuster wie bei einer Farbkarte: Grund, Fläche, Text, zweiter Ton, Akzent
        Row(Modifier.align(Alignment.TopStart)) {
            listOf(palette.surface, palette.textTertiary, palette.textSecondary, palette.accentAlt, palette.accent).forEachIndexed { i, c ->
                Box(
                    Modifier
                        .offset(x = (-5 * i).dp)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(palette.bg)
                        .padding(1.5.dp)
                        .clip(CircleShape)
                        .background(c)
                )
            }
        }
        Text(
            palette.displayName,
            style = MaterialTheme.typography.labelLarge.let { st -> palette.font?.let { st.copy(fontFamily = it) } ?: st },
            color = palette.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart),
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .graphicsLayer {
                    scaleX = sel
                    scaleY = sel
                    alpha = sel.coerceIn(0f, 1f)
                }
                .clip(CircleShape)
                .background(palette.accent),
            contentAlignment = Alignment.Center,
        ) {
            CheckIcon(palette.onAccent, Modifier.size(13.dp))
        }
    }
}

/** Status der Sperre. Antippen: aus → erst der Hinweis mit Zustimmung, an → direkt die Bedienungshilfen-Einstellungen. */
@Composable
private fun LockCard(enabled: Boolean, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val context = androidx.compose.ui.platform.LocalContext.current
    // War die Sperre beim ersten Anzeigen noch aus, bekommt der Haken beim Einschalten seine Animation.
    var wasOff by remember { mutableStateOf(!enabled) }
    if (!enabled) wasOff = true
    var guide by remember { mutableStateOf(false) }
    if (guide) AccessibilityGuide(onDismiss = { guide = false })
    Row(
        modifier
            .fillMaxWidth()
            .bouncyClickable(onClick = { if (enabled) de.funke.app.lock.SessionLock.openAccessibilitySettings(context) else guide = true }, shape = Shapes.large)
            .glass(Shapes.large, strong = true)
            .padding(Space.s5),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (enabled) {
            AnimatedCheckBadge(size = 46.dp, animate = wasOff)
        } else {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(p.glassAlpha(0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = p.accent,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(tr("Sperre", "Lock"), style = MaterialTheme.typography.titleMedium, color = p.text)
            Text(
                if (enabled) tr("Aktiv. In einer Fokuszeit sind nur erlaubte Apps offen.", "Active. During a focus session only allowed apps open.")
                else tr("Aus. Tippe hier, um sie einzuschalten.", "Off. Tap here to turn it on."),
                style = MaterialTheme.typography.bodySmall,
                color = p.textSecondary,
            )
        }
    }
}


// ---------------------------------------------------------------------------------------------
// Knöpfe
// ---------------------------------------------------------------------------------------------

@Composable
private fun ButtonsPage(state: AppState) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val scope = rememberCoroutineScope()

    // ----- Start -----
    SectionLabel(tr("Start-Knopf", "Start button"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    val startDemo = remember { Animatable(0f) }
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s5),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Crossfade(targetState = state.startButtonStyle, animationSpec = tween(250), label = "startDemo") { style ->
            StartButton(
                style = style,
                onStart = {
                    haptics?.success()
                    // Der Schieberegler springt von selbst zurück, die Halte-Knöpfe setzen wir hier zurück
                    if (style != StartButtonStyle.Slide) {
                        scope.launch {
                            kotlinx.coroutines.delay(500)
                            startDemo.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow))
                        }
                    }
                },
                progress = startDemo,
                subtitle = tr("Probelauf", "Test run"),
            )
        }
    }
    Spacer(Modifier.height(Space.s3))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        StartButtonStyle.entries.forEach { style ->
            OptionTile(
                label = style.label,
                selected = state.startButtonStyle == style,
                onClick = { state.updateStartButtonStyle(style) },
                modifier = Modifier.weight(1f),
            ) { tint -> StartStyleSketch(style, tint) }
        }
    }

    // ----- Abbrechen -----
    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Abbrechen", "Stopping"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    val cancelDemo = remember { Animatable(0f) }
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s5),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.sidekick) {
            // Lumi erschrickt mit, wie in der echten Session
            Lumi2D(LumiPose.Reading, Modifier.size(96.dp), scare = cancelDemo.value, tappable = false)
            Spacer(Modifier.height(Space.s2))
        }
        Crossfade(targetState = state.cancelStyle, animationSpec = tween(250), label = "cancelDemo") { style ->
            CancelControl(style = style, progress = cancelDemo, onConfirm = { haptics?.success() })
        }
    }
    Spacer(Modifier.height(Space.s3))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        CancelStyle.entries.forEach { style ->
            OptionTile(
                label = style.label,
                selected = state.cancelStyle == style,
                onClick = { state.updateCancelStyle(style) },
                modifier = Modifier.weight(1f),
            ) { tint -> CancelStyleSketch(style, tint) }
        }
    }
    // ----- Kreuz oben rechts -----
    Spacer(Modifier.height(Space.s7))
    SectionLabel(tr("Kreuz oben rechts", "Cross in the top right"), Modifier.padding(start = Space.s2))
    Spacer(Modifier.height(Space.s3))
    de.funke.app.ui.components.SegmentedToggle(
        options = de.funke.app.ui.QuitConfirm.entries.map { it.label },
        selected = state.quitConfirm.ordinal,
        onSelect = { state.updateQuitConfirm(de.funke.app.ui.QuitConfirm.entries[it]) },
    )
    Spacer(Modifier.height(Space.s2))
    Text(
        when (state.quitConfirm) {
            de.funke.app.ui.QuitConfirm.None -> tr("Das Kreuz beendet ohne Nachfrage. Die Zeit zählt dann nicht.", "The cross ends right away. The time won't count.")
            de.funke.app.ui.QuitConfirm.Once -> tr("Lumi fragt einmal nach, ob du wirklich aufhören willst.", "Lumi asks once whether you really want to stop.")
            de.funke.app.ui.QuitConfirm.Strict -> tr("Nach der Frage kommen noch zwei Seiten, bevor Schluss ist.", "After the question two more pages follow before it ends.")
        },
        style = MaterialTheme.typography.bodySmall,
        color = p.textTertiary,
        modifier = Modifier.padding(horizontal = Space.s2),
    )
}

/** Kachel mit kleiner Skizze und Namen, gewählt mit Rahmen in der Akzentfarbe. */
@Composable
private fun OptionTile(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sketch: @Composable (Color) -> Unit,
) {
    val p = Funke.colors
    val sel by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Medium),
        label = "optionSel",
    )
    val shape = Shapes.card
    Column(
        modifier
            .bouncyClickable(shape = shape, role = Role.RadioButton, onClick = onClick)
            .glass(shape)
            .background(p.accentSoft.copy(alpha = p.accentSoft.alpha * sel))
            .border(1.5.dp, p.accent.copy(alpha = sel), shape)
            .padding(vertical = Space.s4, horizontal = Space.s3),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) {
            sketch(if (selected) p.accent else p.textTertiary)
        }
        Spacer(Modifier.height(Space.s3))
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) p.text else p.textSecondary, maxLines = 1)
    }
}

@Composable
private fun StartStyleSketch(style: StartButtonStyle, tint: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        when (style) {
            StartButtonStyle.Pill -> {
                drawRoundRect(tint, Offset(w * 0.06f, h * 0.22f), Size(w * 0.88f, h * 0.56f), androidx.compose.ui.geometry.CornerRadius(h * 0.28f))
                drawCircle(Color.White.copy(alpha = 0.85f), h * 0.16f, Offset(w * 0.06f + h * 0.28f, h * 0.5f))
            }
            StartButtonStyle.Round -> {
                drawCircle(tint.copy(alpha = 0.35f), h * 0.46f, center, style = Stroke(h * 0.06f))
                drawArc(tint, -90f, 220f, false, Offset(center.x - h * 0.46f, center.y - h * 0.46f), Size(h * 0.92f, h * 0.92f), style = Stroke(h * 0.06f, cap = StrokeCap.Round))
                drawCircle(tint, h * 0.32f, center)
            }
            StartButtonStyle.Slide -> {
                drawRoundRect(tint.copy(alpha = 0.25f), Offset(w * 0.04f, h * 0.24f), Size(w * 0.92f, h * 0.52f), androidx.compose.ui.geometry.CornerRadius(h * 0.26f))
                drawCircle(tint, h * 0.21f, Offset(w * 0.04f + h * 0.27f, h * 0.5f))
                val ax = w * 0.62f
                drawLine(tint, Offset(ax, h * 0.40f), Offset(ax + h * 0.1f, h * 0.5f), h * 0.05f, StrokeCap.Round)
                drawLine(tint, Offset(ax + h * 0.1f, h * 0.5f), Offset(ax, h * 0.60f), h * 0.05f, StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun CancelStyleSketch(style: CancelStyle, tint: Color) {
    val danger = Funke.colors.danger
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        when (style) {
            CancelStyle.Slider -> {
                drawRoundRect(tint.copy(alpha = 0.25f), Offset(w * 0.04f, h * 0.24f), Size(w * 0.92f, h * 0.52f), androidx.compose.ui.geometry.CornerRadius(h * 0.26f))
                drawCircle(danger, h * 0.21f, Offset(w * 0.04f + h * 0.27f, h * 0.5f))
            }
            CancelStyle.Line -> {
                drawLine(tint.copy(alpha = 0.45f), Offset(w * 0.08f, h * 0.6f), Offset(w * 0.92f, h * 0.6f), h * 0.05f, StrokeCap.Round)
                drawCircle(danger, h * 0.15f, Offset(w * 0.22f, h * 0.6f), style = Stroke(h * 0.05f))
                drawCircle(danger.copy(alpha = 0.5f), h * 0.05f, Offset(w * 0.92f, h * 0.6f))
            }
            CancelStyle.Hold -> {
                drawRoundRect(tint.copy(alpha = 0.25f), Offset(w * 0.04f, h * 0.24f), Size(w * 0.92f, h * 0.52f), androidx.compose.ui.geometry.CornerRadius(h * 0.26f))
                drawRoundRect(danger.copy(alpha = 0.45f), Offset(w * 0.04f, h * 0.24f), Size(w * 0.5f, h * 0.52f), androidx.compose.ui.geometry.CornerRadius(h * 0.26f))
                drawArc(danger, -90f, 230f, false, Offset(w * 0.04f + h * 0.08f, h * 0.31f), Size(h * 0.38f, h * 0.38f), style = Stroke(h * 0.05f, cap = StrokeCap.Round))
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Sprache
// ---------------------------------------------------------------------------------------------

@Composable
private fun LanguagePage(state: AppState) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    Column(
        Modifier
            .fillMaxWidth()
            .glass(Shapes.large, strong = true)
            .padding(Space.s2)
    ) {
        AppLanguage.entries.forEach { lang ->
            val selected = state.language == lang
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(Shapes.lg)
                    .bouncyClickable(shape = Shapes.md) {
                        haptics?.click()
                        state.updateLanguage(lang)
                    }
                    .background(if (selected) p.accent.copy(alpha = if (p.isDark) 0.14f else 0.10f) else Color.Transparent)
                    .padding(horizontal = Space.s4, vertical = Space.s4)
                    .semantics { role = Role.RadioButton },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(lang.label, style = MaterialTheme.typography.titleMedium, color = p.text)
                    Text(
                        when (lang) {
                            AppLanguage.English -> tr("Englisch", "English")
                            AppLanguage.German -> tr("Deutsch", "German")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = p.textTertiary,
                    )
                }
                Box(
                    Modifier
                        .size(24.dp)
                        .graphicsLayer { alpha = if (selected) 1f else 0f }
                        .clip(CircleShape)
                        .background(p.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    CheckIcon(p.onAccent, Modifier.size(13.dp))
                }
            }
        }
    }
}

/** Kleine Vorschau eines Hintergrundmusters. */
@Composable
private fun PatternSketch(pattern: BackgroundPattern, tint: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val c = tint.copy(alpha = 0.55f)
        when (pattern) {
            BackgroundPattern.None -> drawRoundRect(
                c.copy(alpha = 0.25f), Offset(w * 0.18f, h * 0.1f), Size(w * 0.64f, h * 0.8f),
                androidx.compose.ui.geometry.CornerRadius(h * 0.14f),
            )
            BackgroundPattern.Grid -> {
                val step = h / 3.2f
                var x = w * 0.5f - step * 2f
                while (x <= w * 0.5f + step * 2.01f) {
                    drawLine(c, Offset(x, 0f), Offset(x, h), 1.dp.toPx())
                    x += step
                }
                var y = h * 0.5f - step * 1.5f
                while (y <= h) {
                    drawLine(c, Offset(w * 0.5f - step * 2f, y), Offset(w * 0.5f + step * 2f, y), 1.dp.toPx())
                    y += step
                }
            }
            BackgroundPattern.Dots -> {
                val step = h / 4f
                for (i in -3..3) for (j in -2..2) {
                    drawCircle(tint, 1.6.dp.toPx(), Offset(w * 0.5f + i * step, h * 0.5f + j * step))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Rechtliches
// ---------------------------------------------------------------------------------------------

@Composable
private fun LegalPage() {
    val p = Funke.colors
    var open by remember { mutableStateOf<LegalDoc?>(null) }
    Column(Modifier.fillMaxWidth().glass(Shapes.card)) {
        LegalDoc.entries.forEachIndexed { i, d ->
            if (i > 0) Box(Modifier.padding(start = Space.s5).fillMaxWidth().height(1.dp).background(p.glassAlpha(0.06f)))
            Row(
                Modifier.fillMaxWidth().clickable { open = d }.padding(horizontal = Space.s5, vertical = Space.s4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(LegalTexts.title(d), style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
                ChevronRightIcon(p.textTertiary, Modifier.size(13.dp))
            }
        }
    }
    open?.let { d ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { open = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        ) { LegalSheet(d, onClose = { open = null }) }
    }
}
