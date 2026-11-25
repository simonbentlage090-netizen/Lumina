package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import de.funke.app.ui.FocusMode
import de.funke.app.ui.InstalledApp
import de.funke.app.ui.components.AppIcon
import de.funke.app.ui.components.AppIconRow
import de.funke.app.ui.components.CheckIcon
import de.funke.app.ui.components.EditIcon
import de.funke.app.ui.components.ModeOrb
import de.funke.app.ui.components.PlayIcon
import de.funke.app.ui.components.PlusIcon
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.accentFill
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import de.funke.app.ui.components.Unfold

/**
 * Karte für einen Modus.
 *
 * Wird sie gewählt, passiert mehrstufig etwas, statt nur eine Farbe zu wechseln:
 * Das Licht links zündet und pulsiert, die Karte bekommt einen weichen Schein,
 * ein Haken springt mit Feder ins Bild, und die Karte klappt auf und zeigt
 * die erlaubten Apps und den Start-Knopf.
 */
@Composable
fun ModeTile(
    mode: FocusMode,
    installed: List<InstalledApp>,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minutes: Int? = null,
    onStart: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
) {
    val p = Funke.colors
    val flat = Funke.flat
    val shape = Shapes.card
    val sel by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Medium),
        label = "tileSel",
    )
    val check by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Base),
        label = "tileCheck",
    )

    Column(
        modifier
            .fillMaxWidth()
            // Seit 15.1 ohne Schein hinter der Karte: die Wahl zeigen Haken, Licht und Tönung – das reicht
            .bouncyClickable(onClick = onClick, shape = shape)
            .glass(shape, strong = selected)
            .drawBehind {
                if (sel > 0.01f) {
                    if (flat) {
                        drawRect(p.accent.copy(alpha = 0.08f * sel))
                    } else {
                        drawRect(
                            Brush.linearGradient(
                                listOf(p.accent.copy(alpha = 0.16f * sel), Color.Transparent),
                                start = Offset.Zero,
                                end = Offset(size.width, size.height),
                            )
                        )
                    }
                }
            }
            .padding(horizontal = Space.s5, vertical = Space.s4),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ModeOrb(mode.name, selected)
            Spacer(Modifier.width(Space.s4))
            Column(Modifier.weight(1f)) {
                Text(mode.name, style = MaterialTheme.typography.titleMedium, color = p.text, maxLines = 1)
                Spacer(Modifier.height(Space.s1))
                Text(mode.subtitle, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
            }
            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(120)),
            ) {
                AppIconRow(mode.packages, installed, size = 26.dp, maxShown = 3)
            }
            Spacer(Modifier.width(Space.s3))
            Box(
                Modifier
                    .size(26.dp)
                    .graphicsLayer {
                        scaleX = check
                        scaleY = check
                        alpha = check.coerceIn(0f, 1f)
                        rotationZ = (1f - check) * -40f
                    }
                    .accentFill(CircleShape, glow = 0.dp, outline = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                CheckIcon(p.onAccent, Modifier.size(14.dp))
            }
        }

        Unfold(
            visible = expanded,
            enter = fadeIn(tween(260, delayMillis = 60)) +
                expandVertically(spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow), clip = false),
            exit = fadeOut(tween(120)) + shrinkVertically(spring(stiffness = Spring.StiffnessMedium), clip = false),
        ) {
            Column {
                Spacer(Modifier.height(Space.s4))
                if (mode.packages.isEmpty()) {
                    Text(
                        tr("Alles gesperrt", "Everything locked"),
                        style = MaterialTheme.typography.bodySmall,
                        color = p.textTertiary,
                    )
                } else {
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Space.s2),
                    ) {
                        // Seit 15.1 ohne Pillen: das Symbol und darunter leise der Name
                        mode.packages.forEach { pkg ->
                            val app = installed.firstOrNull { it.packageName == pkg }
                            val name = app?.label ?: pkg.substringAfterLast('.')
                            Column(Modifier.width(56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                AppIcon(app, name, size = 36.dp)
                                Spacer(Modifier.height(Space.s1))
                                Text(name, style = MaterialTheme.typography.labelSmall, color = p.textTertiary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            }
                        }
                    }
                }

                if (onStart != null || onEdit != null) {
                    Spacer(Modifier.height(Space.s4))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onEdit != null) {
                            Box(
                                Modifier
                                    .size(48.dp)
                                    .bouncyClickable(onClick = onEdit, shape = CircleShape)
                                    .glass(CircleShape, strong = true),
                                contentAlignment = Alignment.Center,
                            ) {
                                EditIcon(p.textSecondary, Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(Space.s3))
                        }
                        if (onStart != null) {
                            StartPill(
                                text = if (minutes != null) tr("$minutes Min starten", "Start $minutes min") else tr("Starten", "Start"),
                                onClick = onStart,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Kleiner, voller Start-Knopf in einer Modus-Karte. */
@Composable
private fun StartPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val shape = Shapes.pill
    Row(
        modifier
            .height(48.dp)
            .bouncyClickable(strongHaptic = true, onClick = onClick, shape = shape)
            .accentFill(shape, glow = 12.dp)
            .padding(horizontal = Space.s5),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        PlayIcon(p.onAccent, Modifier.size(16.dp))
        Spacer(Modifier.width(Space.s3))
        Text(text, style = MaterialTheme.typography.titleSmall, color = p.onAccent)
    }
}

/** Auswahl-Punkt (z. B. im Editor bei den Apps). Füllt sich mit Feder und Haken. */
@Composable
fun SelectionDot(selected: Boolean, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val fillScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = de.funke.app.ui.theme.Motion.spec(de.funke.app.ui.theme.Motion.Base),
        label = "dotScale",
    )
    val ring by animateColorAsState(if (selected) Color.Transparent else p.glassAlpha(0.22f), tween(180), label = "dotRing")
    Box(
        modifier
            .size(24.dp)
            .border(1.5.dp, ring, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = fillScale
                    scaleY = fillScale
                }
                .clip(CircleShape)
                .background(p.accentBrush),
            contentAlignment = Alignment.Center,
        ) {
            CheckIcon(p.onAccent, Modifier.size(13.dp))
        }
    }
}

/** Karte zum Anlegen eines neuen Modus. */
@Composable
fun NewModeCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val shape = Shapes.card
    Row(
        modifier
            .fillMaxWidth()
            .bouncyClickable(onClick = onClick, shape = shape)
            .clip(shape)
            .background(p.glassAlpha(0.025f))
            .padding(horizontal = Space.s5, vertical = Space.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(p.glassAlpha(0.07f)),
            contentAlignment = Alignment.Center,
        ) {
            PlusIcon(p.accent, Modifier.size(22.dp))
        }
        Spacer(Modifier.width(Space.s4))
        Text(tr("Neuer Modus", "New mode"), style = MaterialTheme.typography.titleMedium, color = p.text)
    }
}
