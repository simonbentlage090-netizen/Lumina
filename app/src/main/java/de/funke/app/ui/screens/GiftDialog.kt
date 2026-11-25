package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.hardShadow
import de.funke.app.ui.daysTextDative
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr

/**
 * Rettungs-Eis: Lumi hat einen langen Streak eingefroren, obwohl kein Eis mehr da war.
 * Kommt einmal, wenn man die App danach öffnet.
 */
@Composable
fun GiftDialog(streak: Int, onClose: () -> Unit) {
    val p = Funke.colors
    de.funke.app.ui.components.Modal(onDismiss = onClose) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.lumi_w_frozen),
                contentDescription = tr("Lumi steckt im Eis", "Lumi is stuck in ice"),
                modifier = Modifier.size(128.dp),
            )
            Spacer(Modifier.height(Space.s3))
            Text(
                tr("Lumi hat dir Eis geschenkt!", "Lumi gave you a freeze!"),
                style = MaterialTheme.typography.titleLarge,
                color = p.text,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Space.s2))
            Text(
                tr(
                    "Gestern war kein Eis mehr da. Damit dein Streak von ${daysTextDative(streak)} nicht reißt, " +
                        "hat Lumi ihn eingefroren. Das gibt es nur einmal im Monat – heute bitte lesen!",
                    "You had no freeze left yesterday. So your $streak-day streak wouldn't break, " +
                        "Lumi froze it. This only happens once a month – please read today!",
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = p.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Space.s6))
            PrimaryButton(tr("Danke, Lumi!", "Thanks, Lumi!"), onClick = { close(onClose) })
        }
    }
}
