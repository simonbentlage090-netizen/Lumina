package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.ui.FocusMode
import de.funke.app.ui.LocalInstalledApps
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.AppIcon
import de.funke.app.ui.components.CloseIcon
import de.funke.app.ui.components.ConfirmDialog
import de.funke.app.ui.components.PillButton
import de.funke.app.ui.components.PrimaryButton
import de.funke.app.ui.components.SectionLabel
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr

private val NameSuggestions: List<String>
    get() = listOf(tr("Lesen mit KI", "Reading with AI"), tr("Lernen", "Studying"), tr("Recherche", "Research"), tr("Buch + Notizen", "Book + notes"))

/**
 * Vollbild-Editor zum Anlegen oder Ändern eines Modus: Name vergeben und die Apps ankreuzen,
 * die währenddessen erlaubt sein sollen.
 */
@Composable
fun ModeEditor(
    mode: FocusMode?,
    onSave: (name: String, packages: List<String>) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
) {
    val p = Funke.colors
    val installed = LocalInstalledApps.current
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf(mode?.name ?: "") }
    val selected = remember { mutableStateListOf<String>().apply { addAll(mode?.packages ?: emptyList()) } }
    var confirmDelete by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (confirmDelete) confirmDelete = false else onClose()
    }

    AmbientBackground {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = Space.s6, vertical = Space.s3),
        ) {
            // Kopfzeile
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (mode == null) tr("Neuer Modus", "New mode") else tr("Modus bearbeiten", "Edit mode"),
                    style = MaterialTheme.typography.headlineMedium,
                    color = p.text,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    Modifier
                        .size(40.dp)
                        .bouncyClickable(onClick = onClose, shape = CircleShape)
                        .clip(CircleShape)
                        .background(p.glassAlpha(0.08f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CloseIcon(p.textSecondary, Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(Space.s5))
            SectionLabel(tr("Name", "Name"))
            Spacer(Modifier.height(Space.s2))

            BasicTextField(
                value = name,
                onValueChange = { if (it.length <= 28) name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(color = p.text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                cursorBrush = SolidColor(p.accent),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                decorationBox = { inner ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .glass(Shapes.lg)
                            .padding(horizontal = Space.s5),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (name.isEmpty()) {
                            Text(
                                tr("Name", "Name"),
                                style = MaterialTheme.typography.titleMedium,
                                color = p.textTertiary,
                            )
                        }
                        inner()
                    }
                },
            )

            if (name.isEmpty()) {
                Spacer(Modifier.height(Space.s3))
                Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                    NameSuggestions.take(2).forEach { suggestion ->
                        PillButton(suggestion, onClick = { name = suggestion })
                    }
                }
            }

            Spacer(Modifier.height(Space.s6))
            SectionLabel(tr("Erlaubte Apps (${selected.size})", "Allowed apps (${selected.size})"))
            Spacer(Modifier.height(Space.s2))

            if (installed.isEmpty()) {
                Text(
                    tr(
                        "Es wurden keine Apps gefunden. Ohne Auswahl bleibt in diesem Modus alles gesperrt.",
                        "No apps found. Without a selection everything stays locked in this mode.",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = p.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(Shapes.card)
                        .background(p.track),
                    contentPadding = PaddingValues(vertical = Space.s2),
                ) {
                    items(installed, key = { it.packageName }) { app ->
                        val isOn = app.packageName in selected
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .bouncyClickable(shape = Shapes.md) {
                                    if (isOn) selected.remove(app.packageName) else selected.add(app.packageName)
                                }
                                .padding(horizontal = Space.s4, vertical = Space.s3),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AppIcon(app, app.label, size = 40.dp)
                            Spacer(Modifier.width(Space.s4))
                            Text(
                                app.label,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleSmall,
                                color = p.text,
                                maxLines = 1,
                            )
                            SelectionDot(isOn)
                        }
                    }
                }
            }

            Spacer(Modifier.height(Space.s4))
            PrimaryButton(
                text = tr("Speichern", "Save"),
                enabled = name.isNotBlank(),
                onClick = {
                    focusManager.clearFocus()
                    onSave(name, selected.toList())
                },
            )
            if (mode != null) {
                Spacer(Modifier.height(Space.s1))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .bouncyClickable { confirmDelete = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(tr("Modus löschen", "Delete mode"), style = MaterialTheme.typography.labelLarge, color = p.danger)
                }
            }
        }

        if (confirmDelete) {
            ConfirmDialog(
                title = tr("Modus löschen?", "Delete mode?"),
                message = tr("„${mode?.name.orEmpty()}“ wird endgültig entfernt.", "“${mode?.name.orEmpty()}” will be removed for good."),
                confirmText = tr("Löschen", "Delete"),
                dismissText = tr("Abbrechen", "Cancel"),
                onConfirm = onDelete,
                onDismiss = { confirmDelete = false },
                destructive = true,
            )
        }
    }
}
