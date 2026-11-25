package de.funke.app.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.notify.ReminderKind
import de.funke.app.notify.Reminders
import de.funke.app.ui.DeepLink
import de.funke.app.ui.Lang
import de.funke.app.ui.ReminderRequest
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.CloseButton
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.TextAction
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.findActivity
import de.funke.app.ui.tr
import java.time.Instant
import java.time.ZoneId

/**
 * Erinnerung als Lumi-Bildschirm (seit 13.4, ersetzt den Hinweis beim Entsperren aus 12.0 und die eigene
 * Activity aus 13.3). Liegt über allem in der App. "Jetzt lesen" führt zur Startseite. "Später" und
 * "Nicht jetzt" schließen; wurde Lumina dafür von außen geholt, geht die App wieder nach hinten und man ist
 * dort, wo man war.
 */
@Composable
fun ReminderScreen(request: ReminderRequest, onDone: () -> Unit) {
    val p = Funke.colors
    val context = LocalContext.current
    val done by rememberUpdatedState(onDone)
    val s = request.shown
    val custom = s.kind == ReminderKind.Custom
    // Gezeigt: aus der Warteschlange und aus der Leiste nehmen, sonst käme dieselbe Erinnerung doppelt
    LaunchedEffect(request.at) {
        Reminders.removePending(context, s.id)
        runCatching { androidx.core.app.NotificationManagerCompat.from(context).cancel(Reminders.notificationId(s.id)) }
    }
    val time = remember(s.at) {
        val t = Instant.ofEpochMilli(s.at).atZone(ZoneId.systemDefault()).toLocalTime()
        if (Lang.german) "%d:%02d".format(t.hour, t.minute)
        else "%d:%02d %s".format(if (t.hour % 12 == 0) 12 else t.hour % 12, t.minute, if (t.hour < 12) "am" else "pm")
    }

    fun leave() {
        done()
        if (request.fromOutside) context.findActivity()?.moveTaskToBack(true)
    }
    fun later() {
        Reminders.snooze(context, s.id)
        leave()
    }
    fun readNow() {
        DeepLink.tab = 0
        done()
    }
    BackHandler(onBack = ::leave)

    AmbientBackground {
        Box(
            Modifier
                .fillMaxSize()
                // Tipps gehen nicht an die App dahinter durch
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        ) {
            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = Space.page)) {
                Row(Modifier.fillMaxWidth().padding(top = Space.s3), horizontalArrangement = Arrangement.End) {
                    CloseButton(onClick = ::leave, description = tr("Schließen", "Close"))
                }
                Column(
                    Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Lumi2D(
                        pose = when (s.kind) {
                            ReminderKind.Read, ReminderKind.Bed -> LumiPose.Reading
                            ReminderKind.Streak, ReminderKind.Usage -> LumiPose.Plead
                            ReminderKind.Custom -> LumiPose.Idle
                        },
                        modifier = Modifier.size(176.dp).reveal(0),
                        power = if (s.kind == ReminderKind.Bed) 0.75f else 1f,
                    )
                    Spacer(Modifier.height(Space.s6))
                    Text(time, style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.accent, modifier = Modifier.reveal(40))
                    Spacer(Modifier.height(Space.s1))
                    Text(
                        s.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = p.text,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 420.dp).reveal(70),
                    )
                    if (s.text.isNotBlank()) {
                        Spacer(Modifier.height(Space.s3))
                        Text(
                            s.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = p.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 420.dp).padding(horizontal = Space.s4).reveal(100),
                        )
                    }
                }
                Column(
                    Modifier.fillMaxWidth().padding(bottom = Space.s4).reveal(140),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (custom) {
                        LipButton(tr("Erledigt", "Done"), onClick = ::leave, tone = LipTone.Accent, modifier = Modifier.fillMaxWidth())
                    } else {
                        LipButton(tr("Loslegen", "Start"), onClick = ::readNow, tone = LipTone.Accent, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(Modifier.height(Space.s3))
                    LipButton(
                        tr("In ${Reminders.SNOOZE_MIN} Minuten", "In ${Reminders.SNOOZE_MIN} minutes"),
                        onClick = ::later,
                        tone = LipTone.Neutral,
                        icon = R.drawable.ic_bell,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (!custom) {
                        Spacer(Modifier.height(Space.s2))
                        TextAction(tr("Nicht jetzt", "Not now"), onClick = ::leave, color = p.textTertiary)
                    }
                }
            }
        }
    }
}
