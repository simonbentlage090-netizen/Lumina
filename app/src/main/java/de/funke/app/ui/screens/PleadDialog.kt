package de.funke.app.ui.screens

import androidx.compose.animation.core.snap
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.ui.LocalSidekick
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.SadFace
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.hardShadow
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * "Bist du dir ganz sicher?" – kommt, wenn man die Session ohne Aufgabe beenden will.
 *
 * Lumi taucht hinter der Karte auf, macht große Kulleraugen und schaut dich hoffnungsvoll an.
 * Weiterlesen ist der große Knopf; dann freut sich Lumi kurz, bevor die Karte verschwindet.
 * Beenden ist bewusst nur ein kleiner Text darunter.
 */
@Composable
fun PleadDialog(
    onStay: () -> Unit,
    onQuit: () -> Unit,
    /** Wo "Trotzdem beenden" steht: 0 = unter dem Knopf, 1 = darüber, 2 = mit Abstand weiter unten. Wechselt jedes Mal. */
    quitSlot: Int = 0,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val sidekick = LocalSidekick.current
    val scope = rememberCoroutineScope()
    val appear = remember { Animatable(0f) }
    val rise = remember { Animatable(0f) }
    val still = de.funke.app.ui.theme.reducedMotion
    var relieved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch { appear.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)) }
        delay(160)
        rise.animateTo(1f, if (still) snap() else spring(dampingRatio = 0.82f, stiffness = 320f))
    }

    fun stay() {
        if (relieved) return
        relieved = true
        scope.launch {
            // Lumi freut sich kurz, dann geht es weiter
            delay(if (sidekick) 650 else 0)
            onStay()
        }
    }

    val shape = Shapes.large
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f * appear.value.coerceIn(0f, 1f)))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { stay() },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .padding(horizontal = Space.s7)
                .graphicsLayer {
                    alpha = appear.value.coerceIn(0f, 1f)
                    val s = 0.9f + 0.1f * appear.value
                    scaleX = s
                    scaleY = s
                },
            contentAlignment = Alignment.TopCenter,
        ) {
            // Lumi schaut von oben über die Karte
            if (sidekick) {
                Lumi2D(
                    pose = if (relieved) LumiPose.Happy else LumiPose.Plead,
                    contentDescription = tr("Lumi schaut dich bittend an", "Lumi looks at you, pleading"),
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            translationY = (1f - rise.value) * 70.dp.toPx()
                            alpha = rise.value.coerceIn(0f, 1f)
                        },
                )
            }
            Column(
                Modifier
                    .padding(top = if (sidekick) 112.dp else 0.dp)
                    .fillMaxWidth()
                    .then(if (cartoon) Modifier.hardShadow(shape, p.ink, 4.dp) else Modifier)
                    .clip(shape)
                    .background(p.surface)
                    .then(if (cartoon) Modifier.border(2.5.dp, p.ink, shape) else Modifier)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                    .padding(start = Space.s6, end = Space.s6, top = if (sidekick) Space.s8 else Space.s6, bottom = Space.s4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!sidekick) {
                    SadFace(Modifier.size(56.dp))
                    Spacer(Modifier.height(Space.s4))
                }
                Text(
                    tr("Bist du dir ganz sicher?", "Are you really sure?"),
                    style = MaterialTheme.typography.titleLarge,
                    color = p.text,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Space.s2))
                Text(
                    if (relieved) tr("Juhu! Weiter geht's.", "Yay! Let's keep going.")
                    else tr("Nur noch ein bisschen? Lumi bleibt so gern bei dir.", "Just a little longer? Lumi loves staying with you."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = p.textSecondary,
                    textAlign = TextAlign.Center,
                )
                val quit = @Composable {
                    Text(
                        tr("Trotzdem beenden", "Stop anyway"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.danger.copy(alpha = 0.85f),
                        modifier = Modifier
                            .bouncyClickable(shape = Shapes.pill) { if (!relieved) onQuit() }
                            .padding(horizontal = Space.s4, vertical = Space.s3),
                    )
                }
                // "Trotzdem beenden" steht nicht immer an derselben Stelle, damit die Hand es sich nicht merkt.
                if (quitSlot == 1) {
                    Spacer(Modifier.height(Space.s2))
                    quit()
                    Spacer(Modifier.height(Space.s2))
                } else {
                    Spacer(Modifier.height(Space.s5))
                }
                PrimaryButton(tr("Weitermachen", "Keep going"), onClick = ::stay)
                if (quitSlot != 1) {
                    Spacer(Modifier.height(if (quitSlot == 2) 44.dp else Space.s2))
                    quit()
                }
            }
        }
    }
}

