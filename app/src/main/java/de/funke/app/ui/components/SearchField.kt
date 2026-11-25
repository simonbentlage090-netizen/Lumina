package de.funke.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.funke.app.R
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr

/**
 * Suchfeld als ruhige Pille (seit 15.3 gemeinsam für Inhaltsverzeichnis und Bücher): Lupe, Text, rechts ein
 * Kreuz zum Leeren. Holt sich beim Erscheinen den Fokus, damit man gleich tippen kann.
 */
@Composable
fun SearchField(query: String, onChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Row(
        modifier
            .fillMaxWidth()
            .height(Sizes.buttonSmall)
            .clip(Shapes.pill)
            .background(p.track)
            .padding(start = Space.s4, end = Space.s1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(R.drawable.ic_search), contentDescription = null, tint = p.textTertiary, modifier = Modifier.size(Sizes.iconSm))
        Spacer(Modifier.width(Space.s3))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = p.textTertiary, maxLines = 1)
            BasicTextField(
                value = query,
                onValueChange = onChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = p.text),
                cursorBrush = SolidColor(p.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focus)
                    .semantics { contentDescription = placeholder },
            )
        }
        if (query.isNotEmpty()) {
            Box(
                Modifier
                    .size(Sizes.touch - Space.s1)
                    .clip(CircleShape)
                    .clickable { onChange("") }
                    .semantics { contentDescription = tr("Suche leeren", "Clear search") },
                contentAlignment = Alignment.Center,
            ) { CloseIcon(p.textSecondary, Modifier.size(10.dp)) }
        }
    }
}
