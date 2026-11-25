package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import androidx.compose.animation.core.snap
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Space
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.ui.LocalSidekick
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.SadFace
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Nach "Trotzdem beenden" noch zwei große Seiten, bevor die Session wirklich endet (über das Kreuz zählt
 * sie dann gar nicht). Lumi schaut mit riesigen, nassen Kulleraugen, auf der zweiten Seite kullern Tränen.
 * Der große, leuchtende Knopf ist immer "Weiterlesen", Beenden nur ein kleiner Text.
 *
 * @param stage 1 = "Bist du wirklich sicher?", 2 = "Willst du dir nichts Gutes tun?"
 */
@Composable
fun GuiltScreen(
    stage: Int,
    onStay: () -> Unit,
    onNext: () -> Unit,
    /** Wo der kleine "Aufhören"-Text steht: 0 = ganz unten, 1 = ein Stück über dem Knopf, 2 = oben. */
    quitSlot: Int = 0,
) {
    val p = Funke.colors
    val sidekick = LocalSidekick.current
    val scope = rememberCoroutineScope()
    var relieved by remember { mutableStateOf(false) }
    val rise = remember { Animatable(0f) }
    val still = de.funke.app.ui.theme.reducedMotion
    LaunchedEffect(stage) {
        rise.snapTo(0f)
        rise.animateTo(1f, if (still) snap() else spring(dampingRatio = 0.82f, stiffness = 320f))
    }
    // Ein Grund zum Weiterlesen, jedes Mal ein anderer
    val reason = remember {
        listOf(
            tr("Jede Minute, die du dranbleibst, trainiert deine Konzentration. Genau das macht es beim nächsten Mal leichter.",
                "Every minute you stay with it trains your focus. That's exactly what makes next time easier."),
            tr("Du hast dir dein Ziel selbst gesetzt. Lumi glaubt fest daran, dass du es schaffst.",
                "You set this goal yourself. Lumi truly believes you can make it."),
            tr("Das Handy kann warten. Die Geschichte in deinem Buch nicht.",
                "Your phone can wait. The story in your book can't."),
            tr("Gleich ist es geschafft, und dann fühlt es sich richtig gut an. Versprochen.",
                "You're almost there, and then it'll feel really good. Promise."),
        ).random()
    }

    fun stay() {
        if (relieved) return
        relieved = true
        scope.launch {
            delay(if (sidekick) 700 else 0)
            onStay()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(p.bg)
            .background(Brush.verticalGradient(listOf(p.accent.copy(alpha = 0.10f), p.bg)))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
    ) {
        AnimatedContent(
            targetState = stage,
            transitionSpec = {
                if (still) fadeIn(tween(Motion.Base, easing = Motion.EaseOut)) togetherWith fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
                else (fadeIn(tween(Motion.Medium, easing = Motion.EaseOut)) + slideInVertically(tween(Motion.Slow, easing = Motion.EaseOut)) { it / 16 }) togetherWith
                    fadeOut(tween(Motion.Fast, easing = Motion.EaseOut))
            },
            label = "guilt",
        ) { st ->
            Column(
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = Space.s7, vertical = Space.s6),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (quitSlot == 2) {
                    Text(
                        if (st == 1) tr("Trotzdem beenden", "Stop anyway") else tr("Ich will wirklich aufhören", "I really want to stop"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.textTertiary,
                        modifier = Modifier
                            .bouncyClickable(shape = Shapes.pill) { if (!relieved) onNext() }
                            .padding(horizontal = Space.s4, vertical = Space.s3),
                    )
                }
                Spacer(Modifier.weight(1f))
                if (sidekick) {
                    Lumi2D(
                        pose = when {
                            relieved -> LumiPose.Happy
                            st == 1 -> LumiPose.Puppy
                            else -> LumiPose.Heartbroken
                        },
                        contentDescription = tr("Lumi schaut dich traurig an", "Lumi looks at you sadly"),
                        tappable = false,
                        modifier = Modifier
                            .size(270.dp)
                            .graphicsLayer {
                                translationY = (1f - rise.value) * 60.dp.toPx()
                                alpha = rise.value.coerceIn(0f, 1f)
                            },
                    )
                } else {
                    SadFace(Modifier.size(96.dp))
                }
                Spacer(Modifier.height(Space.s5))
                Text(
                    when {
                        relieved -> tr("Juhu! Weiter geht's.", "Yay! Let's keep going.")
                        st == 1 -> tr("Bist du wirklich sicher?", "Are you really sure?")
                        else -> tr("Willst du dir nichts Gutes tun?", "Don't you want to do yourself some good?")
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = p.text,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Space.s3))
                Text(
                    if (st == 1) tr(
                        "Dann zählt diese Session gar nicht – keine Minuten, kein Streak.",
                        "Then this session won't count at all – no minutes, no streak.",
                    ) else reason,
                    style = MaterialTheme.typography.bodyLarge,
                    color = p.textSecondary,
                    textAlign = TextAlign.Center,
                )
                val quit = @Composable {
                    Text(
                        if (st == 1) tr("Trotzdem beenden", "Stop anyway") else tr("Ich will wirklich aufhören", "I really want to stop"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.textTertiary,
                        modifier = Modifier
                            .bouncyClickable(shape = Shapes.pill) { if (!relieved) onNext() }
                            .padding(horizontal = Space.s4, vertical = Space.s3),
                    )
                }
                when (quitSlot) {
                    1 -> {
                        Spacer(Modifier.weight(0.55f))
                        quit()
                        Spacer(Modifier.weight(0.45f))
                    }
                    else -> Spacer(Modifier.weight(1f))
                }
                PrimaryButton(
                    if (st == 1) tr("Okay, ich mache weiter", "Okay, I'll keep going") else tr("Weitermachen", "Keep going"),
                    onClick = ::stay,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (quitSlot == 0) {
                    Spacer(Modifier.height(Space.s2))
                    quit()
                } else {
                    Spacer(Modifier.height(Space.s5))
                }
            }
        }
    }
}
