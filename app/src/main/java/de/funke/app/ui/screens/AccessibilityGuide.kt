package de.funke.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.lock.SessionLock
import de.funke.app.ui.components.ChevronRight
import de.funke.app.ui.components.ModalWindow
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr

/**
 * Bevor die Bedienungshilfen aufgehen (seit 13.4, seit 15.6 mit Einwilligung).
 *
 * Oben steht klar, wozu Lumina die Bedienungshilfe nutzt und was nicht – Google Play verlangt diesen Hinweis
 * mit ausdrücklicher Zustimmung, bevor man in die Einstellungen geschickt wird. Darunter genau die Zeilen, die man
 * dort antippt (direkt auf den Schalter springen darf nur das System). Alle Wege zum Einschalten führen hierher.
 */
@Composable
fun AccessibilityGuide(onDismiss: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val name = stringResource(R.string.lock_service_label)
    val samsung = android.os.Build.MANUFACTURER.equals("samsung", ignoreCase = true)
    ModalWindow(onDismiss = onDismiss) {
        Text(tr("Bedienungshilfe für Lumina", "Accessibility service for Lumina"), style = MaterialTheme.typography.titleLarge, color = p.text)
        Spacer(Modifier.height(Space.s2))
        Text(
            tr(
                "Lumina nutzt die Bedienungshilfe nur, um zu erkennen, welche App gerade vorne ist. Damit sperrt Lumina während einer Fokuszeit andere Apps, zeigt vor gewählten Apps die Denkpause und Erinnerungen im Vollbild. " +
                    "Lumina liest keine Bildschirminhalte, keine Eingaben und keine Texte. Nichts davon verlässt dein Handy.",
                "Lumina uses the accessibility service only to see which app is in front. With it, Lumina locks other apps during a focus session, shows the think break before apps you picked and full-screen reminders. " +
                    "Lumina doesn't read screen content, input or text. None of it leaves your phone.",
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = p.textSecondary,
        )
        Spacer(Modifier.height(Space.s4))
        Text(
            tr("Android lässt Apps nicht direkt zum Schalter springen. Tippe in den Einstellungen diese Zeilen an:",
                "Android doesn't let apps jump straight to the switch. In the settings, tap these rows:"),
            style = MaterialTheme.typography.bodyMedium,
            color = p.textSecondary,
        )
        Spacer(Modifier.height(Space.s5))
        Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
            if (samsung) GuideRow(1, tr("Installierte Apps", "Installed apps"), trailing = null)
            GuideRow(if (samsung) 2 else 1, name, trailing = tr("Aus", "Off"))
            GuideRow(if (samsung) 3 else 2, tr("Schalter einschalten, dann „Zulassen“", "Turn the switch on, then “Allow”"), trailing = null, switch = true)
        }
        Spacer(Modifier.height(Space.s5))
        Text(
            tr("Danach einfach zurückwischen – Lumina hakt es selbst ab.", "Then just swipe back – Lumina ticks it off by itself."),
            style = MaterialTheme.typography.bodySmall,
            color = p.textTertiary,
        )
        Spacer(Modifier.height(Space.s5))
        PrimaryButton(
            tr("Zustimmen und Einstellungen öffnen", "Agree and open settings"),
            onClick = { close { onDismiss(); SessionLock.openAccessibilitySettings(context) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Space.s1))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextAction(tr("Nicht jetzt", "Not now"), onClick = { close(onDismiss) })
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextAction(
                tr("Schalter ausgegraut? Erst „Eingeschränkte Einstellungen zulassen“", "Switch greyed out? First “Allow restricted settings”"),
                onClick = { close { onDismiss(); SessionLock.openAppInfo(context) } },
            )
        }
    }
}

/** Eine nachgebaute Einstellungs-Zeile: Nummer, Text wie im System, rechts Pfeil, Zustand oder Schalter. */
@Composable
private fun GuideRow(n: Int, text: String, trailing: String?, switch: Boolean = false) {
    val p = Funke.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(p.accent), contentAlignment = Alignment.Center) {
            Text("$n", style = MaterialTheme.typography.labelLarge, color = p.onAccent)
        }
        Spacer(Modifier.width(Space.s3))
        Row(
            Modifier
                .weight(1f)
                .heightIn(min = 52.dp)
                .clip(Shapes.md)
                .background(p.track)
                .border(1.5.dp, p.accent.copy(alpha = 0.55f), Shapes.md)
                .padding(horizontal = Space.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall, color = p.text, modifier = Modifier.weight(1f))
            when {
                switch -> Box(Modifier.padding(start = Space.s2)) { LuminaSwitch(true) }
                trailing != null -> Text(trailing, style = MaterialTheme.typography.bodySmall, color = p.textTertiary)
                else -> ChevronRight()
            }
        }
    }
}
