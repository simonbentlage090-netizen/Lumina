package de.funke.app.ui.screens

import androidx.compose.foundation.layout.heightIn
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import de.funke.app.ui.AppState
import de.funke.app.ui.SessionDurations
import de.funke.app.ui.StartButtonStyle
import de.funke.app.ui.components.ChevronDownIcon
import de.funke.app.ui.components.ChevronRightIcon
import de.funke.app.ui.components.MiniLumi
import de.funke.app.ui.components.ModeOrb
import de.funke.app.ui.components.ProgressRing
import de.funke.app.ui.components.ReadingHeatmap
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.StartButton
import de.funke.app.ui.components.TimeDial
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.liquidRim
import de.funke.app.ui.daysWord
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.components.glowBorder
import de.funke.app.ui.tr
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.daysText
import de.funke.app.ui.components.StreakFlame
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import de.funke.app.ui.components.HeaderIcon

/**
 * Reiter 1, die Startseite (seit 11.0 in drei Etagen):
 * oben die Notizen (kommen von oben herein), in der Mitte das Wichtigste (Zeit-Ring, Start-Knopf),
 * unten die Modi (kommen von unten). Nach unten wischen oder auf "Notizen" tippen holt die Notizen,
 * nach oben wischen die Modi. Die Kopfzeile (Modus links, Notizen und Streak rechts) bleibt stehen.
 *
 * Die Modi kommen außerdem über eine Leiste von links: den Modus-Chip antippen
 * oder vom linken Bildschirmrand nach rechts ziehen.
 */
@Composable
fun FocusTab(
    state: AppState,
    drawer: ModesDrawerState,
    onOpenModes: () -> Unit,
    onOpenProgress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = Funke.colors
    val pager = rememberPagerState(initialPage = HOME, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    BackHandler(enabled = pager.currentPage != HOME && !drawer.isOpen) {
        scope.launch { pager.animateScrollToPage(HOME) }
    }
    var calc by remember { mutableStateOf(false) }
    // Von außen (Widget, Einführung) direkt zu den Notizen
    val wantNotes = de.funke.app.ui.DeepLink.notes
    LaunchedEffect(wantNotes) {
        if (wantNotes) {
            de.funke.app.ui.DeepLink.notes = false
            pager.animateScrollToPage(NOTES)
        }
    }
    // -1 = Notizen ganz da, 0 = Startseite, 1 = Modi ganz da
    val position = { (pager.currentPage + pager.currentPageOffsetFraction - HOME).coerceIn(-1f, 1f) }
    // Unsichtbare Teile der Startseite dürfen keine Fingertipps schlucken (sonst ging "Neue Notiz" nicht)
    val headerShown by remember { androidx.compose.runtime.derivedStateOf { position() > -0.9f } }
    val onHome by remember { androidx.compose.runtime.derivedStateOf { kotlin.math.abs(position()) < 0.1f } }

    Box(modifier.fillMaxSize()) {
        VerticalPager(
            state = pager,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
            userScrollEnabled = !drawer.isOpen,
        ) { page ->
            when (page) {
                NOTES -> NotesTab(
                    state = state,
                    onClose = { scope.launch { pager.animateScrollToPage(HOME) } },
                    modifier = Modifier.graphicsLayer { alpha = 0.25f + 0.75f * (-position()).coerceIn(0f, 1f) },
                )
                HOME -> HomePage(
                    state = state,
                    modifier = Modifier.graphicsLayer {
                        // verblasst und bleibt dabei fast stehen (wandert nur langsam mit).
                        // Ganz ausgeblendet, bevor die nächste Etage da ist: nichts soll blass durchscheinen.
                        val f = position()
                        val a = kotlin.math.abs(f)
                        alpha = (1f - a / 0.6f).coerceIn(0f, 1f)
                        // Ganz ausgeblendet springt sie an ihren eigentlichen Platz (außerhalb des Bildschirms),
                        // damit sie nicht unsichtbar über den Notizen liegt und deren Knöpfe blockiert.
                        translationY = if (a >= 0.6f) 0f else size.height * 0.55f * f
                        val sc = 1f - 0.07f * a
                        scaleX = sc
                        scaleY = sc
                    },
                )
                else -> ModesPage(
                    state = state,
                    onBack = { scope.launch { pager.animateScrollToPage(HOME) } },
                    modifier = Modifier.graphicsLayer { alpha = 0.25f + 0.75f * position().coerceIn(0f, 1f) },
                )
            }
        }

        // Auf der unteren Etage: sanfter Übergang hinter der festen Kopfzeile, damit Karten darunter verschwinden
        Box(
            Modifier
                .fillMaxWidth()
                .height(HeaderSpace + 40.dp)
                .graphicsLayer { alpha = position().coerceIn(0f, 1f) }
                .background(Brush.verticalGradient(listOf(p.bg, p.bg.copy(alpha = 0.92f), Color.Transparent)))
        )

        // Kopfzeile: Modus links (öffnet die Leiste), Notizen und Streak rechts. Bei den Notizen tritt sie zurück.
        if (headerShown) Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = Space.page, end = Space.page, top = Space.s3)
                .graphicsLayer {
                    val n = (-position()).coerceIn(0f, 1f)
                    alpha = 1f - n
                    translationY = -16.dp.toPx() * n
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Seit 14.0 luftiger: der Modus als ruhiges Wort mit Punkt (ohne Glas), Notizen und Rechner als
            // schlichte Symbole, nur der Streak trägt eine Fläche (er ist Belohnung, kein Werkzeug).
            Box(Modifier.weight(1f, fill = false)) { ModeChip(name = state.selectedMode.name, onClick = onOpenModes) }
            Spacer(Modifier.weight(1f))
            HeaderIcon(tr("Notizen", "Notes"), onClick = { scope.launch { pager.animateScrollToPage(NOTES) } }) {
                NotepadIcon(Funke.colors.textSecondary, Modifier.size(20.dp))
            }
            HeaderIcon(tr("Rechner", "Calculator"), onClick = { calc = true }) {
                androidx.compose.material3.Icon(
                    androidx.compose.ui.res.painterResource(de.funke.app.R.drawable.ic_calculator),
                    contentDescription = null,
                    tint = Funke.colors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(Space.s2))
            // Streak antippen führt zum Fortschritt (seit 13.4)
            StreakChip(state.streak, onClick = onOpenProgress)
        }
        if (calc) de.funke.app.ui.calc.CalculatorWindow(onDismiss = { calc = false })

        // Drei kleine Punkte am rechten Rand zeigen, dass es darüber und darunter weitergeht
        PageDots(
            position = pager.currentPage + pager.currentPageOffsetFraction,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Space.s2),
        )

        // Vom linken Rand aus aufziehen (nur auf der Startseite)
        if (onHome) DrawerEdgeHandle(drawer, Modifier.align(Alignment.CenterStart).padding(vertical = Space.s30))
    }
}

private const val NOTES = 0
private const val HOME = 1

/** Platz, den die feste Kopfzeile oben braucht. */
private val HeaderSpace = 64.dp

@Composable
private fun HomePage(state: AppState, modifier: Modifier = Modifier) {
    val mode = state.selectedMode
    val startProgress = remember { Animatable(0f) }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val dialSize = min(maxWidth.value * 0.82f, 330f).dp
        val compact = maxHeight < 700.dp

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = Space.page, end = Space.page, top = HeaderSpace, bottom = NavBarSpace),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))

            TimeDial(
                values = SessionDurations,
                selected = state.minutes,
                onSelected = state::updateMinutes,
                diameter = dialSize,
            )

            Spacer(Modifier.weight(1f))

            // Start-Knopf in der gewählten Form
            StartButton(
                modifier = Modifier,
                style = state.startButtonStyle,
                onStart = state::startSession,
                progress = startProgress,
                // Der Modus steht schon oben; hier nur, was der Knopf tut
                subtitle = if (state.startButtonStyle == StartButtonStyle.Round) tr("${state.minutes} Min Fokus", "${state.minutes} min focus") else null,
                title = tr("${state.minutes} Min Fokus", "${state.minutes} min focus"),
            )
            Spacer(Modifier.height(if (compact) Space.s3 else Space.s6))
        }
    }
}

/**
 * Untere Etage: nur die Modi (ansehen, wählen, bearbeiten, neu anlegen). Der Überblick hat seinen eigenen Reiter.
 */
@Composable
private fun ModesPage(state: AppState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val installed = LocalInstalledApps.current
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }
    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = Space.page, end = Space.page, top = HeaderSpace, bottom = NavBarSpace + Space.s4),
    ) {
        // Zurück nach oben
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = de.funke.app.ui.theme.Sizes.touch)
                .bouncyClickable(shape = Shapes.pill, onClick = onBack),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChevronDownIcon(p.textTertiary, Modifier.size(18.dp).graphicsLayer { rotationZ = 180f })
        }
        Spacer(Modifier.height(Space.s1))
        Text(tr("Modi", "Modes"), style = MaterialTheme.typography.headlineLarge, color = p.text)
        Spacer(Modifier.height(Space.s6))
        state.modes.forEach { m ->
            ModeTile(
                mode = m,
                installed = installed,
                selected = m.id == state.selectedModeId,
                expanded = expandedId == m.id,
                minutes = state.minutes,
                onClick = {
                    state.selectMode(m.id)
                    expandedId = if (expandedId == m.id) null else m.id
                },
                onStart = { state.startWithMode(m.id) },
                onEdit = if (m.builtIn) null else ({ state.openEditor(m) }),
                modifier = Modifier.padding(bottom = Space.s3),
            )
        }
        NewModeCard(onClick = { state.openEditor(null) })
    }
}

/** Drei senkrechte Punkte: welcher Teil der Startseite gerade zu sehen ist. */
@Composable
private fun PageDots(position: Float, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Space.s2)) {
        for (i in 0..2) {
            val near = (1f - abs(position - i)).coerceIn(0f, 1f)
            Box(
                Modifier
                    .size(width = 4.dp, height = 4.dp + 8.dp * near)
                    .clip(Shapes.pill)
                    .background(if (near > 0.5f) p.textSecondary else p.glassAlpha(0.22f))
            )
        }
    }
}

/** Der gewählte Modus als Chip. Wechselt der Modus, rollt der Name seitlich herein. */
@Composable
private fun ModeChip(name: String, onClick: () -> Unit) {
    val p = Funke.colors
    Row(
        Modifier
            .heightIn(min = de.funke.app.ui.theme.Sizes.buttonSmall)
            .bouncyClickable(shape = Shapes.pill, pressedScale = 0.97f, onClick = onClick)
            // ruhig, ohne Fläche: der Blick soll beim Start-Knopf bleiben
            .padding(start = Space.s1, end = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = name,
            transitionSpec = {
                (slideInHorizontally(tween(Motion.Medium, easing = Motion.EaseOut)) { -it / 4 } +
                    fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) +
                    scaleIn(tween(Motion.Medium, easing = Motion.EaseOut), initialScale = 0.92f)) togetherWith
                    (slideOutHorizontally(tween(Motion.Fast, easing = Motion.EaseOut)) { it / 6 } + fadeOut(tween(Motion.Fast - 30, easing = Motion.EaseOut)))
            },
            label = "modeChip",
        ) { current ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                ModeOrb(current, selected = true, size = 26.dp)
                Spacer(Modifier.width(Space.s3))
                Text(current, style = MaterialTheme.typography.titleSmall, color = p.text, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.width(Space.s1))
        ChevronRightIcon(p.textTertiary, Modifier.size(16.dp))
    }
}

/**
 * Streak oben rechts: eine kleine Flamme und die Zahl daneben (wie bei Duolingo).
 * Die Flamme flackert ganz leicht. Steht der Streak bei 0, ist sie grau und ruhig.
 */
@Composable
private fun StreakChip(streak: Int, onClick: () -> Unit) {
    Row(
        Modifier
            .bouncyClickable(shape = Shapes.pill, onClick = onClick)
            .glass(Shapes.pill)
            .liquidRim(Shapes.pill, strength = 0.6f)
            .padding(start = Space.s3, end = Space.s4, top = Space.s2, bottom = Space.s2)
            .semantics(mergeDescendants = true) {
                contentDescription = "Streak: " + daysText(streak) + tr(". Antippen für deinen Fortschritt.", ". Tap for your progress.")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StreakFlame(streak, Modifier.size(width = 20.dp, height = 24.dp), showNumber = false, still = true)
        Spacer(Modifier.width(Space.s2))
        AnimatedContent(
            targetState = streak,
            transitionSpec = {
                (slideInVertically(tween(Motion.Medium, easing = Motion.EaseOut)) { it / 2 } + fadeIn(tween(Motion.Fast, easing = Motion.EaseOut))) togetherWith
                    (slideOutVertically(tween(Motion.Fast, easing = Motion.EaseOut)) { -it / 2 } + fadeOut(tween(Motion.Fast - 30, easing = Motion.EaseOut)))
            },
            label = "streakNumber",
        ) { s ->
            Text(
                "$s",
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"),
                color = if (s > 0) Funke.colors.text else Funke.colors.textTertiary,
            )
        }
    }
}
