package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.ui.components.AnimatedCheckBadge
import de.funke.app.ui.tr
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.theme.Funke

/**
 * Einrichtung der Sperre. Erscheint, wenn man eine Session starten will,
 * die Bedienungshilfe aber noch nicht eingeschaltet ist.
 * Kommt man aus den Einstellungen zurück, wechselt der Inhalt zu "Sperre ist bereit".
 */
@Composable
fun LockSetupSheet(
    ready: Boolean,
    onOpenSettings: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onStart: () -> Unit,
    onStartWithoutLock: () -> Unit,
    onDismiss: () -> Unit,
) {
    val p = Funke.colors
    BackHandler(onBack = onDismiss)

    val rise = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        rise.animateTo(1f, spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = (1f - rise.value) * 420.dp.toPx()
                }
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(p.surface)
                .then(if (Funke.flat) Modifier else Modifier.background(Brush.verticalGradient(listOf(p.glassAlpha(0.06f), Color.Transparent))))
                // Klicks auf die Karte sollen sie nicht schließen
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .navigationBarsPadding()
                .padding(start = Space.s6, end = Space.s6, top = Space.s3, bottom = Space.s5),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(Shapes.pill)
                    .background(p.glassAlpha(0.18f))
            )
            Spacer(Modifier.height(Space.s6))

            AnimatedContent(
                targetState = ready,
                transitionSpec = {
                    (fadeIn(tween(260, delayMillis = 80)) +
                        scaleIn(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow), initialScale = 0.92f)) togetherWith
                        fadeOut(tween(140))
                },
                label = "lockSetup",
            ) { isReady ->
                if (isReady) ReadyContent(onStart) else SetupContent(onOpenSettings, onOpenAppInfo, onStartWithoutLock)
            }
        }
    }
}

@Composable
private fun Badge(ready: Boolean) {
    val p = Funke.colors
    if (ready) {
        // Berechtigung ist da: Kreis springt auf, Haken zeichnet sich, ein Lichtring läuft nach außen
        AnimatedCheckBadge(size = 64.dp, animate = true)
    } else {
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(p.glassAlpha(0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Lock, contentDescription = null, tint = p.accent, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun SetupContent(
    onOpenSettings: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onStartWithoutLock: () -> Unit,
) {
    val p = Funke.colors
    val serviceName = androidx.compose.ui.res.stringResource(de.funke.app.R.string.lock_service_label)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Badge(ready = false)
        Spacer(Modifier.height(Space.s5))
        Text(tr("Sperre einschalten", "Turn on the lock"), style = MaterialTheme.typography.headlineMedium, color = p.text)
        Spacer(Modifier.height(Space.s2))
        Text(
            tr(
                "Damit Lumina andere Apps beim Lesen sperren kann, schaltest du einmal eine Bedienungshilfe ein.",
                "So Lumina can lock other apps while you read, you turn on an accessibility service once.",
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = p.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Space.s6))

        Column(
            Modifier
                .fillMaxWidth()
                .clip(Shapes.card)
                .background(p.glassAlpha(0.05f))
                .padding(Space.s5),
            verticalArrangement = Arrangement.spacedBy(Space.s4),
        ) {
            Step(1, tr("Unten auf „Bedienungshilfe öffnen“ tippen", "Tap “Open accessibility” below"))
            Step(2, tr("„Installierte Apps“ → „$serviceName“ einschalten", "“Installed apps” → turn on “$serviceName”"))
            Step(3, tr("Zurück zu Lumina wischen", "Swipe back to Lumina"))
        }

        Spacer(Modifier.height(Space.s6))
        PrimaryButton(tr("Bedienungshilfe öffnen", "Open accessibility"), onClick = onOpenSettings, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(Space.s4))
        Text(
            tr("Schalter ausgegraut? Hier „Eingeschränkte Einstellungen zulassen“", "Switch greyed out? Tap here to “Allow restricted settings”"),
            style = MaterialTheme.typography.labelMedium,
            color = p.accent,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .bouncyClickable(shape = Shapes.pill, onClick = onOpenAppInfo)
                .padding(Space.s2),
        )
        Text(
            tr("Diesmal ohne Sperre starten", "Start without the lock this time"),
            style = MaterialTheme.typography.labelLarge,
            color = p.textTertiary,
            modifier = Modifier
                .bouncyClickable(shape = Shapes.pill, onClick = onStartWithoutLock)
                .padding(Space.s3),
        )
    }
}

@Composable
private fun Step(number: Int, text: String) {
    val p = Funke.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(p.accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text("$number", style = MaterialTheme.typography.labelLarge, color = p.accent)
        }
        Spacer(Modifier.width(Space.s4))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = p.text)
    }
}

@Composable
private fun ReadyContent(onStart: () -> Unit) {
    val p = Funke.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Badge(ready = true)
        Spacer(Modifier.height(Space.s5))
        Text(tr("Sperre ist bereit", "The lock is ready"), style = MaterialTheme.typography.headlineMedium, color = p.text)
        Spacer(Modifier.height(Space.s2))
        Text(
            tr(
                "Während einer Session lassen sich nur noch Lumina und die erlaubten Apps deines Modus öffnen.",
                "During a session only Lumina and the apps allowed in your mode can be opened.",
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = p.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Space.s6))
        PrimaryButton(tr("Fokuszeit starten", "Start focus session"), onClick = onStart, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(Space.s2))
    }
}
