package de.funke.app.ui.screens

import de.funke.app.ui.theme.Space
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import de.funke.app.ui.AppState
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.FunkeNavBar
import de.funke.app.ui.components.backdropSource
import de.funke.app.ui.components.NavIndicatorState
import de.funke.app.ui.components.NavTabCount
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

/** Platz, den die schwebende Leiste unten braucht. */
val NavBarSpace = 104.dp

/**
 * Der Hauptbildschirm: sechs Reiter zum Wischen (Fokus, Bücher, Denkpause, Aufgaben, Fortschritt mit Bildschirmzeit, Einstellungen), die Leiste darunter
 * und die Modus-Leiste, die von links hereinkommt.
 */
@Composable
fun MainScreen(state: AppState) {
    val pagerState = rememberPagerState(pageCount = { NavTabCount })
    val scope = rememberCoroutineScope()
    val haptics = LocalHaptics.current
    val indicator = remember { NavIndicatorState() }
    val drawer = remember { ModesDrawerState() }
    var navToken by remember { mutableIntStateOf(0) }
    var navJob by remember { mutableStateOf<Job?>(null) }

    // Beim Wischen folgt der Indikator dem Finger.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage + pagerState.currentPageOffsetFraction }
            .collect { pos -> indicator.follow(pos.coerceIn(0f, (NavTabCount - 1).toFloat())) }
    }
    // Leichter Tick, wenn man beim Wischen auf den nächsten Reiter kommt.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1)
            .collect { if (!indicator.locked) haptics?.tick() }
    }

    fun goTo(index: Int) {
        if (index == pagerState.targetPage && !indicator.locked) return
        haptics?.tick()
        val token = ++navToken
        indicator.locked = true
        navJob?.cancel()
        navJob = scope.launch {
            try {
                coroutineScope {
                    launch { indicator.glideTo(index) { haptics?.tick() } }
                    launch {
                        // Bei weiten Sprüngen nicht durch alle Seiten rauschen:
                        // erst direkt neben das Ziel springen, dann die letzte Seite weich gleiten.
                        val from = pagerState.currentPage
                        val distance = index - from
                        if (abs(distance) > 1) pagerState.scrollToPage(index - distance.sign)
                        pagerState.animateScrollToPage(
                            index,
                            animationSpec = spring(dampingRatio = 0.92f, stiffness = 420f),
                        )
                    }
                }
            } finally {
                if (navToken == token) indicator.locked = false
            }
        }
    }

    // Vom Widget geöffnet: direkt in den gewünschten Reiter.
    val pendingTab = de.funke.app.ui.DeepLink.tab
    LaunchedEffect(pendingTab) {
        if (pendingTab in 0 until NavTabCount) {
            de.funke.app.ui.DeepLink.tab = -1
            if (drawer.isOpen) drawer.close()
            goTo(pendingTab)
        }
    }

    BackHandler(enabled = drawer.isOpen || pagerState.currentPage != 0) {
        if (drawer.isOpen) {
            scope.launch { drawer.close() }
        } else {
            goTo(0)
        }
    }

    val reduced = de.funke.app.ui.theme.reducedMotion
    // Was hinter der Leiste liegt, für echtes Liquid Glass im Glass-Stil (ab Android 12)
    val backdrop = if (de.funke.app.ui.theme.Funke.glassy && de.funke.app.ui.components.Backdrop.supported) de.funke.app.ui.components.rememberBackdrop() else null
    AmbientBackground {
        val back = drawer.progress.value
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Der Inhalt tritt zurück, wenn die Modus-Leiste kommt.
                    translationX = back * 36.dp.toPx()
                    val s = 1f - 0.05f * back
                    scaleX = s
                    scaleY = s
                    if (canBlur) {
                        val r = 14.dp.toPx() * back
                        renderEffect = if (r > 0.5f) BlurEffect(r, r, TileMode.Decal) else null
                    }
                }
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !drawer.isOpen && !state.immersive && de.funke.app.ui.components.Overlays.count == 0,
                modifier = Modifier.fillMaxSize().then(Modifier.backdropSource(backdrop)),
            ) { page ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val distance = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                                .absoluteValue
                                .coerceIn(0f, 1f)
                            alpha = 1f - 0.7f * distance
                            if (!reduced) {
                                val s = 1f - 0.04f * distance
                                scaleX = s
                                scaleY = s
                            }
                        }
                ) {
                    when (page) {
                        0 -> FocusTab(
                            state = state,
                            drawer = drawer,
                            onOpenModes = {
                                haptics?.tick()
                                scope.launch { drawer.open() }
                            },
                            onOpenProgress = { goTo(Tabs.Progress) },
                        )
                        Tabs.Books -> LibraryTab(state)
                        Tabs.Gate -> GateTab(state)
                        Tabs.Tasks -> PracticeTab(state)
                        Tabs.Progress -> ProgressHub(state)
                        else -> SettingsScreen(state)
                    }
                }
            }

            // Während einer Aufgaben-Runde gleitet die Leiste nach unten weg (Platz fürs Zahlenfeld)
            androidx.compose.animation.AnimatedVisibility(
                // Weicht auch, solange ein Blatt offen ist (sonst lägen dessen Knöpfe darunter)
                visible = !state.immersive && de.funke.app.ui.components.Overlays.count == 0,
                enter = androidx.compose.animation.fadeIn(tween(de.funke.app.ui.theme.Motion.Base, easing = de.funke.app.ui.theme.Motion.EaseOut)) +
                    androidx.compose.animation.slideInVertically(tween(de.funke.app.ui.theme.Motion.Medium, easing = de.funke.app.ui.theme.Motion.EaseOut)) { it / 2 },
                exit = androidx.compose.animation.fadeOut(tween(de.funke.app.ui.theme.Motion.Fast)) +
                    androidx.compose.animation.slideOutVertically(tween(de.funke.app.ui.theme.Motion.Base, easing = de.funke.app.ui.theme.Motion.EaseOut)) { it / 2 },
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                androidx.compose.runtime.CompositionLocalProvider(de.funke.app.ui.components.LocalBackdrop provides backdrop) {
                    FunkeNavBar(
                        indicator = indicator,
                        selected = pagerState.targetPage,
                        onSelect = ::goTo,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = Space.s5),
                    )
                }
            }
        }

        ModesDrawer(state, drawer)

        // Beim ersten Öffnen erklärt Lumi kurz, wofür der Reiter da ist
        val introKind = when (pagerState.settledPage) {
            Tabs.Books -> TabIntroKind.Books
            Tabs.Gate -> TabIntroKind.Gate
            Tabs.Tasks -> TabIntroKind.Tasks
            else -> null
        }
        val introVisible = introKind != null && state.onboarded && introKind.id !in state.tabIntrosSeen && !pagerState.isScrollInProgress
        var shownIntro by remember { mutableStateOf(TabIntroKind.Gate) }
        if (introVisible && introKind != null) shownIntro = introKind
        androidx.compose.animation.AnimatedVisibility(
            visible = introVisible,
            enter = androidx.compose.animation.fadeIn(tween(de.funke.app.ui.theme.Motion.Base, easing = de.funke.app.ui.theme.Motion.EaseOut)) +
                androidx.compose.animation.scaleIn(tween(de.funke.app.ui.theme.Motion.Medium, easing = de.funke.app.ui.theme.Motion.EaseOut), initialScale = 0.98f),
            exit = androidx.compose.animation.fadeOut(tween(de.funke.app.ui.theme.Motion.Fast, easing = de.funke.app.ui.theme.Motion.EaseOut)),
        ) {
            TabIntro(shownIntro, onDone = { state.markTabIntro(shownIntro.id) })
        }
    }
}

/** Reihenfolge der Reiter unten (seit 13.4: Bücher auf Platz 2, Bildschirmzeit steckt im Fortschritt). */
object Tabs {
    const val Home = 0
    const val Books = 1
    const val Gate = 2
    const val Tasks = 3
    const val Progress = 4
    const val Settings = 5
}
