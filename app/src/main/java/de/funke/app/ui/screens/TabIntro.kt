package de.funke.app.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.funke.app.ui.components.AmbientBackground
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.reveal
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Space
import de.funke.app.ui.tr

/** Reiter, die sich beim ersten Öffnen kurz selbst erklären. */
enum class TabIntroKind(val id: String) { Gate("gate"), Books("books"), Tasks("tasks") }

private class IntroText(val title: String, val lines: List<String>)

private fun textFor(kind: TabIntroKind): IntroText = when (kind) {
    TabIntroKind.Gate -> IntroText(
        tr("Erst denken, dann scrollen", "Think first, then scroll"),
        listOf(
            tr("Wähle die Apps, in denen du oft zu lange hängen bleibst.", "Pick the apps you tend to get stuck in."),
            tr("Zu festen Uhrzeiten kommt vor diesen Apps erst eine kurze Frage.", "At set times, a short question comes before these apps open."),
            tr("Unter „Erinnerungen“ legst du fest, wann und wie Lumi dich erinnert.", "Under \"Reminders\" you choose when and how Lumi reminds you."),
        ),
    )
    TabIntroKind.Books -> IntroText(
        tr("Deine Bücher", "Your books"),
        listOf(
            tr("Füge PDFs und E-Books aus deinen Dateien hinzu.", "Add PDFs and e-books from your files."),
            tr("Rand antippen blättert, doppelt in die Mitte tippen öffnet das Menü.", "Tap the edge to turn the page, double-tap the middle for the menu."),
            tr("Jede Minute im Buch zählt als Fokuszeit, auch für deinen Streak.", "Every minute in a book counts as focus time, for your streak too."),
        ),
    )
    TabIntroKind.Tasks -> IntroText(
        tr("Kurz nachdenken statt scrollen", "A little thinking instead of scrolling"),
        listOf(
            tr("Wähle ein Fach und wie schwer es sein soll.", "Pick a subject and how hard it should be."),
            tr("Eine Lektion hat drei bis fünf Aufgaben und dauert nur ein paar Minuten.", "A lesson has three to five tasks and takes just a few minutes."),
            tr("Kommst du nicht weiter, hilft dir Lumi unten links mit einem Tipp.", "Stuck? Lumi in the bottom left gives you a hint."),
        ),
    )
}

/**
 * Erklärt einen Reiter beim ersten Öffnen: Lumi mit Doktorhut, eine Überschrift, drei kurze Sätze, ein Knopf.
 * Liegt über dem Reiter und der Leiste; danach sieht man ihn nie wieder (siehe AppState.tabIntrosSeen).
 */
@Composable
fun TabIntro(kind: TabIntroKind, onDone: () -> Unit) {
    val p = Funke.colors
    val text = remember(kind) { textFor(kind) }
    BackHandler(onBack = onDone)
    AmbientBackground {
        Column(
            Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Space.page),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            // Lumi lässt sich antippen und reagiert, wie überall in der App
            Lumi2D(
                pose = LumiPose.Idle,
                modifier = Modifier.size(184.dp).reveal(0),
                hat = true,
                hatTassel = p.accent,
            )
            Spacer(Modifier.height(Space.s5))
            Text(
                text.title,
                style = MaterialTheme.typography.headlineMedium,
                color = p.text,
                textAlign = TextAlign.Center,
                modifier = Modifier.reveal(60),
            )
            Spacer(Modifier.height(Space.s6))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Space.s4)) {
                text.lines.forEachIndexed { i, line ->
                    Row(Modifier.fillMaxWidth().reveal(120 + 60 * i), verticalAlignment = Alignment.Top) {
                        Box(
                            Modifier
                                .padding(top = 1.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(p.accentSoft),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("${i + 1}", style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.accent)
                        }
                        Spacer(Modifier.width(Space.s4))
                        Text(line, style = MaterialTheme.typography.bodyLarge, color = p.textSecondary, modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.weight(1.3f))
            LipButton(
                tr("Verstanden", "Got it"),
                onClick = onDone,
                tone = LipTone.Accent,
                modifier = Modifier.fillMaxWidth().padding(bottom = Space.s5).reveal(320),
            )
        }
    }
}
