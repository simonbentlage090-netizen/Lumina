package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.ui.AppState
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import de.funke.app.ui.components.MiniLumi
import androidx.compose.foundation.layout.size

/** Reiter 2: Alle Modi ansehen, wählen, bearbeiten und neue anlegen. */
@Composable
fun ModesTab(state: AppState, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val installed = LocalInstalledApps.current
    val custom = state.customModes
    val builtIn = state.modes.first { it.builtIn }
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }

    fun tap(id: String) {
        state.selectMode(id)
        expandedId = if (expandedId == id) null else id
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = Space.s5, end = Space.s5, top = Space.s5, bottom = NavBarSpace + Space.s6),
    ) {
        item(key = "header") {
            Column(Modifier.padding(start = Space.s1, end = Space.s1, bottom = Space.s6).reveal(0)) {
                Text(tr("Modi", "Modes"), style = MaterialTheme.typography.headlineLarge, color = p.text)
                Spacer(Modifier.height(Space.s1))
                Text(
                    tr("Welche Apps in der Fokuszeit erlaubt sind.", "Which apps you can use while you focus."),
                    style = MaterialTheme.typography.bodySmall,
                    color = p.textTertiary,
                )
            }
        }

        item(key = "defaultLabel") {
            SectionLabel(tr("Standard", "Default"), Modifier.padding(start = Space.s2, bottom = Space.s3))
        }
        item(key = builtIn.id) {
            ModeTile(
                mode = builtIn,
                installed = installed,
                selected = state.selectedModeId == builtIn.id,
                expanded = expandedId == builtIn.id,
                minutes = state.minutes,
                onClick = { tap(builtIn.id) },
                onStart = { state.startWithMode(builtIn.id) },
                modifier = Modifier.reveal(60),
            )
        }

        item(key = "customLabel") {
            SectionLabel(tr("Eigene", "Your modes"), Modifier.padding(start = Space.s2, top = Space.s7, bottom = Space.s3))
        }

        if (custom.isEmpty()) {
            item(key = "empty") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .glass(Shapes.card)
                        .padding(Space.s6)
                        .reveal(120),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MiniLumi(Modifier.size(64.dp))
                    Spacer(Modifier.height(Space.s2))
                    Text(tr("Noch keine eigenen Modi", "No modes of your own yet"), style = MaterialTheme.typography.titleMedium, color = p.text)
                    Spacer(Modifier.height(Space.s2))
                    Text(
                        tr(
                            "Lege zum Beispiel „Lesen mit KI“ an und erlaube dir dort Gemini.",
                            "For example, create “Reading with AI” and allow Gemini there.",
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = p.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            items(custom, key = { it.id }) { mode ->
                ModeTile(
                    mode = mode,
                    installed = installed,
                    selected = mode.id == state.selectedModeId,
                    expanded = expandedId == mode.id,
                    minutes = state.minutes,
                    onClick = { tap(mode.id) },
                    onStart = { state.startWithMode(mode.id) },
                    onEdit = { state.openEditor(mode) },
                    modifier = Modifier
                        .padding(bottom = Space.s3)
                        .animateItem(),
                )
            }
        }

        item(key = "new") {
            NewModeCard(
                onClick = { state.openEditor(null) },
                modifier = Modifier.padding(top = Space.s1),
            )
        }
    }
}
