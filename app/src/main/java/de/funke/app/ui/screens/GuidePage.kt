package de.funke.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.funke.app.R
import de.funke.app.ui.AppState
import de.funke.app.ui.components.ChevronRight
import de.funke.app.ui.components.LipBox
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.Lumi2D
import de.funke.app.ui.components.LumiPose
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.glass
import de.funke.app.ui.components.lipColors
import de.funke.app.ui.components.reveal
import de.funke.app.ui.components.surface
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.NumberStyle
import de.funke.app.ui.theme.RewardColors
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Sizes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import de.funke.app.ui.components.Unfold

/*
 * "So funktioniert's" (seit 13.0 ausführlich): jede Funktion als aufklappbarer Bereich. Aufgeklappt zeigt er
 * oben ein kleines Bild der Stelle in der App (aus denselben Bausteinen gezeichnet, in deinen Farben) und darunter
 * kurze Punkte mit fettem Stichwort vorn. Zugeklappt sieht man nur Symbol und Name: schnell zu überfliegen.
 * Mehrere Bereiche dürfen gleichzeitig offen sein, damit beim Öffnen nichts springt.
 */

private class GuideSection(
    val icon: Int,
    val title: String,
    val points: List<String>,
    val art: (@Composable () -> Unit)?,
)

private fun sections(): List<GuideSection> = listOf(
    GuideSection(
        R.drawable.ic_nav_focus, tr("Fokuszeit", "Focus time"),
        listOf(
            tr("**Zeit wählen:** Dreh auf der Startseite am Rad.", "**Pick a time:** turn the dial on the home screen."),
            tr("**Starten:** Halte den Knopf gedrückt, bis er voll ist. Lumi ist bei dir, der Ring zeigt die Zeit.", "**Start:** hold the button until it fills up. Lumi stays with you and the ring shows the time."),
            tr("**Unten in der Fokuszeit:** links das Schloss mit deinen erlaubten Apps (antippen öffnet sie), rechts Notizen und Rechner.", "**At the bottom during a session:** on the left the lock with your allowed apps (tap to open one), on the right notes and the calculator."),
            tr("**Früher aufhören:** Schieb den Regler unten zur Seite (oder halte ihn, je nach Einstellung). Erst kommt eine kurze Aufgabe; löst du sie, darfst du gehen.", "**Stop early:** slide the control at the bottom (or hold it, depending on your settings). First comes a short task; solve it and you can go."),
            tr("**Kreuz oben rechts:** beendet sofort, ohne Aufgabe. Je nach Einstellung fragt Lumi vorher nach.", "**Cross in the top right:** ends at once, without a task. Depending on your settings, Lumi asks first."),
            tr("**Was zählt:** Ganze Fokuszeit: Minuten und ein Streak-Tag. Früher beendet mit gelöster Aufgabe: nur die Minuten. Übers Kreuz: nichts.", "**What counts:** a full session: minutes and a streak day. Ended early with a solved task: just the minutes. Via the cross: nothing."),
            tr("**Danach:** Lumi zeigt, was die Fokuszeit gebracht hat. Notizen aus dieser Fokuszeit öffnest du dort direkt.", "**Afterwards:** Lumi shows what the session added up to. Notes from this session open right from there."),
            tr("**Anpassen:** Einstellungen → Session: Ring, Timer, Lumi in der Fokuszeit, Start-Knopf (halten, rund oder schieben), Abbrechen (Regler, Linie oder halten) und ob Lumi beim Kreuz nachfragt.", "**Adjust:** Settings → Session: ring, timer, Lumi during focus, start button (hold, round or slide), stopping (slider, line or hold) and whether Lumi asks before the cross ends it."),
        ),
    ) { ReadingArt() },
    GuideSection(
        R.drawable.ic_nav_books, tr("Bücher", "Books"),
        listOf(
            tr("**Hinzufügen:** Im Reiter Bücher tippst du oben auf +. PDFs und E-Books (EPUB) aus deinen Dateien kommen als Kopie ins Regal.", "**Add:** in the Books tab, tap + at the top. PDFs and e-books (EPUB) from your files are copied onto your shelf."),
            tr("**Regal:** Alle Bücher stehen als Umschläge nebeneinander, zuletzt gelesen vorn. Ab drei Büchern sortierst du oben, ab sechs suchst du nach Titel oder Autor. „⋯“ unter einem Buch (oder lange drücken): umbenennen oder aus dem Regal entfernen.", "**Shelf:** all your books stand side by side as covers, most recently read first. From three books you can sort at the top, from six you can search by title or author. \"⋯\" under a book (or a long press): rename it or remove it from the shelf."),
            tr("**Lesen:** Wischen oder an den Rand tippen blättert um. Doppelt in die Mitte tippen öffnet das Menü: oben links der Inhalt, unten Notiz, Rechner, Merken und Darstellung.", "**Read:** swipe or tap the edge to turn the page. Double-tap the middle for the menu: contents at the top left, note, calculator, bookmark and display at the bottom."),
            tr("**Inhalt:** Kapitel, Lesezeichen und Notizen des Buchs. Wo du gerade bist, steht in Farbe, darunter zeigt eine feine Linie, wie weit du in diesem Abschnitt bist. Antippen springt hin; bei vielen Kapiteln hilft die Lupe.", "**Contents:** the book's chapters, bookmarks and notes. Where you are is shown in colour, with a fine line below showing how far you are in that section. Tap to jump there; with many chapters, use the magnifier."),
            tr("**Kapitel:** E-Books bringen ihr Inhaltsverzeichnis meist mit, viele PDFs auch. Fehlt es, sucht Lumi die Kapitel: mit der Lumina-KI, wenn du sie eingeschaltet hast (für bis zu drei Bücher in sieben Tagen, dafür geht nur der Anfang jeder Seite an den Server), sonst direkt auf dem Handy im Text. Nummerierte Unterkapitel (3.2 …) findet Lumi im Text, ohne Internet (bei PDFs ab Android 15). Mit dem Pfeil klappst du sie auf.", "**Chapters:** e-books usually bring their own table of contents, many PDFs too. If there is none, Lumi looks for the chapters: with Lumina AI if you've turned it on (for up to three books in seven days; only the start of each page goes to the server), otherwise right on the phone in the text. Numbered sub-chapters (3.2 …) Lumi finds in the text, offline (for PDFs from Android 15). Expand them with the arrow."),
            tr("**Notiz:** gehört zur Seite, auf der du sie schreibst. Tippen oder diktieren, unten dieselbe Leiste wie bei allen Notizen: Sprache, Mikrofon, Aufräumen. Ein kleiner Stift neben der Seitenzahl zeigt sie an; groß öffnest du sie mit dem Pfeil oben.", "**Note:** belongs to the page you write it on. Type or dictate, with the same bar as all notes at the bottom: language, microphone, clean up. A small pen next to the page number shows it; the arrow at the top opens it full size."),
            tr("**Rechner:** Die Seite bleibt oben groß. Verschieben, mit zwei Fingern zoomen, doppelt tippen vergrößert; am Rand tippen oder wischen blättert weiter. Unten liegt der Rechner, mit dem Pfeil klappst du ihn klein.", "**Calculator:** the page stays big at the top. Drag it, pinch to zoom, double-tap to enlarge; tap the edge or swipe to turn the page. The calculator sits below; the arrow folds it down."),
            tr("**Merken:** setzt ein Lesezeichen (ein Band oben rechts auf der Seite). Alle stehen im Inhalt unter „Lesezeichen“.", "**Bookmark:** sets a bookmark (a ribbon at the top right of the page). You'll find them all under \"Bookmarks\" in the contents."),
            tr("**Darstellung:** Papier, Sepia oder Nacht; bei E-Books dazu Schriftgröße und Schrift (Buchschrift oder Inter). PDFs vergrößerst du mit zwei Fingern.", "**Display:** paper, sepia or night; for e-books also text size and font (book serif or Inter). Pinch PDFs to zoom."),
            tr("**Position:** Lumina merkt sich jede Seite. Beim nächsten Öffnen geht es genau dort weiter.", "**Position:** Lumina remembers every page. Next time you open the book, it continues right there."),
            tr("**Was zählt:** Jede Minute im Buch zählt als Fokuszeit für dein Tagesziel, ab 5 Minuten am Tag auch für den Streak. Liegt das Handy 5 Minuten unberührt da, pausiert die Zeit.", "**What counts:** every minute in a book counts toward your daily goal, and from 5 minutes a day toward your streak. If the phone sits untouched for 5 minutes, the time pauses."),
            tr("**Kopierschutz:** Bücher mit DRM aus Shops lassen sich nicht öffnen. Das ist technisch nicht anders möglich.", "**Copy protection:** DRM books from stores can't be opened. There's no technical way around that."),
        ),
    ) { BooksArt() },
    GuideSection(
        R.drawable.ic_lock, tr("Sperre und Modi", "Lock and modes"),
        listOf(
            tr("**Sperre:** Während einer Fokuszeit sind die anderen Apps zu. Dafür braucht Lumina einmal die Bedienungshilfe. Sie sieht nur, welche App vorne ist, keine Inhalte.", "**Lock:** during a session the other apps are closed. For this Lumina needs the accessibility service once. It only sees which app is in front, never any content."),
            tr("**Modi:** Ein Modus legt fest, welche Apps trotzdem gehen, zum Beispiel ein Wörterbuch. Oben links auf der Startseite steht der aktuelle Modus; antippen zeigt alle.", "**Modes:** a mode decides which apps still work, a dictionary for example. The current mode is shown at the top left of the home screen; tap it to see them all."),
            tr("**Erlaubte Apps öffnen:** In der Fokuszeit tippst du unten auf die Leiste mit den Apps.", "**Open allowed apps:** during a session, tap the bar with the apps at the bottom."),
            tr("**Einschalten:** Einstellungen → Sperre. Ohne Sperre kannst du trotzdem starten; dann bleiben die anderen Apps offen.", "**Turn it on:** Settings → Lock. You can still start without it; the other apps then stay open."),
        ),
    ) { LockArt() },
    GuideSection(
        R.drawable.ic_nav_notes, tr("Notizen", "Notes"),
        listOf(
            tr("**Schreiben oder sprechen:** In der Fokuszeit tippst du unten rechts auf den Block. Mit dem Mikrofon schreibt Lumina mit, was du sagst.", "**Type or speak:** during a session, tap the notepad at the bottom right. With the microphone, Lumina writes down what you say."),
            tr("**Aufräumen:** unten rechts. Lumi macht aus deinen Gedanken einen sauberen Text oder Stichpunkte; was davon, wählst du beim Antippen. Dafür geht der Text kurz an einen Server, aber nur, wenn du zugestimmt hast.", "**Clean up:** at the bottom right. Lumi turns your thoughts into clean text or bullet points; you choose which when you tap it. For this the text goes briefly to a server, but only if you've agreed."),
            tr("**Rückgängig und andere Fassung:** Nach dem Aufräumen kannst du es gleich rückgängig machen. Die Fassung davor bleibt bei der Notiz; oben unter „Andere Fassung“ holst du sie auch später zurück.", "**Undo and other version:** right after cleaning up you can undo it. The version before stays with the note; \"Other version\" at the top brings it back later, too."),
            tr("**Alle Notizen:** oben rechts auf der Startseite. Filter (Fokuszeit, Bücher, Diktiert) und Suche helfen beim Finden. Notizen aus Büchern zeigen Buch und Seite; in der Notiz springt „Zur Seite“ genau dorthin. Teilen und Speichern geht einzeln oder alle auf einmal.", "**All notes:** at the top right of the home screen. Filters (focus, books, dictated) and search help you find them. Notes from books show the book and page; inside the note, the book chip jumps right there. Share or save one at a time or all at once."),
            tr("**Diktiersprache:** unten links neben dem Mikrofon, antippen wechselt. **Papier** und ob Lumi mit der KI aufräumt: Einstellungen → Notizen. Ohne KI räumt Lumi direkt auf dem Handy auf.", "**Dictation language:** at the bottom left next to the microphone, tap to switch. **Paper** and whether Lumi cleans up with AI: Settings → Notes. Without AI, Lumi tidies right on the phone."),
        ),
    ) { NotesArt() },
    GuideSection(
        R.drawable.ic_flame, tr("Streak und Streak-Eis", "Streak and streak freezes"),
        listOf(
            tr("**Streak:** Jeder Tag mit einer ganzen Fokuszeit oder mindestens 5 Minuten im Buch zählt. Die Flamme zeigt, wie viele Tage am Stück es sind.", "**Streak:** every day with a full focus session or at least 5 minutes in a book counts. The flame shows how many days in a row."),
            tr("**Streak-Eis:** Fehlt ein Tag, springt ein Eis ein und der Streak bleibt. Höchstens 2 kannst du auf Vorrat haben.", "**Streak freeze:** if you miss a day, a freeze steps in and your streak stays. You can keep up to 2."),
            tr("**Eis verdienen:** eins alle 4 Streak-Tage und eins alle 15 gelösten Aufgaben.", "**Earn freezes:** one every 4 streak days and one every 15 solved tasks."),
            tr("**Rettung:** War dein Streak mindestens 6 Tage lang und fehlt nur gestern, schenkt Lumi dir ein Eis. Höchstens einmal im Monat.", "**Rescue:** if your streak was at least 6 days long and only yesterday is missing, Lumi gives you a freeze. At most once a month."),
        ),
    ) { StreakArt() },
    GuideSection(
        R.drawable.ic_nav_gate, tr("Denkpause", "Think break"),
        listOf(
            tr("**Apps:** Im Reiter Denkpause wählst du die Apps, in denen du oft hängen bleibst.", "**Apps:** in the Think break tab, pick the apps you tend to get stuck in."),
            tr("**Wie oft:** 3-, 5- oder 8-mal am Tag oder zu eigenen Uhrzeiten. Das Zifferblatt unter „Zeiten“ zeigt den ganzen Tag, die nächste Frage leuchtet am hellsten.", "**How often:** 3, 5 or 8 times a day, or at your own times. The dial under \"Times\" shows the whole day; the next question glows brightest."),
            tr("**Die Frage:** Öffnest du dann eine dieser Apps, kommt erst eine kurze Frage. Antworte oder tippe auf „Weiß ich nicht“. Danach entscheidest du: lieber nicht, oder weiter zur App.", "**The question:** if you then open one of these apps, a short question comes first. Answer it or tap \"I don't know\". Then you decide: better not, or on to the app."),
            tr("**Fragen:** Schwierigkeit und Fächer stellst du unter „Fragen“ ein. Das gilt auch beim früheren Aufhören einer Fokuszeit.", "**Questions:** set difficulty and subjects under \"Questions\". This also applies when you stop a session early."),
            tr("**Ausprobieren:** oben unter dem Satz. Eine Denkpause zur Probe: Sie zählt nichts und öffnet keine App.", "**Try it:** at the top, under the sentence. A think break as a trial: it doesn't count and doesn't open an app."),
            tr("**Einmal einrichten:** Wie die Sperre braucht die Denkpause die Bedienungshilfe. Fehlt sie, steht oben „Bedienungshilfe einschalten“.", "**Set up once:** like the lock, the think break needs the accessibility service. If it's missing, \"Turn on accessibility service\" appears at the top."),
        ),
    ) { GateArt() },
    GuideSection(
        R.drawable.ic_bell, tr("Erinnerungen", "Reminders"),
        listOf(
            tr("**Wo:** im Reiter Denkpause oben auf „Erinnerungen“. Die Liste sieht aus wie bei einem Wecker: Uhrzeit, Name, Tage und wie sie kommt.", "**Where:** in the Think break tab, tap \"Reminders\" at the top. The list looks like an alarm clock: time, name, days and how it arrives."),
            tr("**Wie:** als Pop-up oben am Bildschirm oder im Vollbild. Vollbild kommt sofort, wenn du das Handy gerade benutzt, sonst beim nächsten Entsperren.", "**How:** as a pop-up at the top of the screen or full screen. Full screen comes right away if you're using your phone, otherwise the next time you unlock it."),
            tr("**Testen:** „Jetzt testen“ zeigt sie gleich, „In 1 Min.“ schickt sie auf dem echten Weg – leg das Handy ruhig weg. Unten im Editor steht, was zuletzt passiert ist.", "**Test:** \"Test now\" shows it right away, \"In 1 min\" sends it the real way – feel free to put the phone down. The bottom of the editor shows what happened last."),
            tr("**Tagesziel und Streak retten:** kommen nur, wenn heute noch etwas fehlt. Hast du es schon geschafft, bleibt Lumi still.", "**Daily goal and save your streak:** only come when something is still missing today. If you've already made it, Lumi stays quiet."),
            tr("**Eigene:** mit „Eigene Erinnerung“: Titel, Nachricht, Uhrzeit in 15-Minuten-Schritten und Wochentage.", "**Your own:** with \"Custom reminder\": title, message, time in 15-minute steps and weekdays."),
        ),
    ) { NudgeArt() },
    GuideSection(
        R.drawable.ic_nav_tasks, tr("Aufgaben", "Tasks"),
        listOf(
            tr("**Fach und Stufe:** Wähle ein Fach oder „Gemischt“. Die Stufen stehen als Bücher im Regal, vom dünnen Heft (Leicht) bis zum dicken Wälzer (Meister).", "**Subject and level:** pick a subject or \"Mixed\". The levels stand on a shelf as books, from the thin booklet (Easy) to the thick tome (Master)."),
            tr("**Gemischt:** Welche Fächer gemischt werden, wählst du in der Zeile darunter.", "**Mixed:** choose which subjects go into the mix in the row below it."),
            tr("**Lektion:** drei bis fünf Aufgaben. Oben siehst du, wie weit du bist; fertige Aufgaben kannst du dort noch einmal ansehen.", "**Lesson:** three to five tasks. The top shows how far you are; tap finished tasks there to look at them again."),
            tr("**Hilfe:** Tipp unten links auf Lumi: erst Tipps, zuletzt die Formel. Die Lösung gibt es nach einem Fehlversuch oder nach 20 Sekunden.", "**Help:** tap Lumi in the bottom left: hints first, the formula last. The solution is available after a wrong try or after 20 seconds."),
            tr("**Belohnung:** Gelöste Aufgaben bringen Funken, jeder Tipp kostet einen. Alle 15 gelösten Aufgaben gibt es ein Streak-Eis.", "**Rewards:** solved tasks earn sparks; each hint costs one. Every 15 solved tasks earn a streak freeze."),
            tr("**Rechner und Fächer:** Bei Zahlen liegt rechts im Feld ein Rechner. Fächer schaltest du unter Einstellungen → Aufgaben an und aus.", "**Calculator and subjects:** number answers have a calculator on the right of the field. Turn subjects on and off in Settings → Tasks."),
        ),
    ) { TasksArt() },
    GuideSection(
        R.drawable.ic_nav_screen, tr("Bildschirmzeit", "Screen time"),
        listOf(
            tr("**Überblick:** Im Reiter Fortschritt unter „Bildschirmzeit“ siehst du, wie lange du heute, gestern oder in 7 Tagen am Handy warst, im Vergleich zu davor.", "**Overview:** in the Progress tab under \"Screen time\" you see how long you were on your phone today, yesterday or over 7 days, compared with before."),
            tr("**Apps und Gruppen:** welche Apps dir die meiste Zeit nehmen. Mit „+ Gruppe“ fasst du Apps zusammen, zum Beispiel alle Video-Apps.", "**Apps and groups:** which apps take most of your time. Use \"+ Group\" to bundle apps, all video apps for example."),
            tr("**Kennzahlen:** wie oft du entsperrt hast und wie lange am Stück.", "**Numbers:** how often you unlocked and your longest stretch."),
            tr("**Privat:** Lumina sieht nur, welche App wie lange offen war, keine Inhalte. Alles bleibt auf deinem Handy.", "**Private:** Lumina only sees which app was open for how long, never any content. Everything stays on your phone."),
        ),
    ) { ScreenTimeArt() },
    GuideSection(
        R.drawable.ic_nav_progress, tr("Fortschritt und Ziele", "Progress and goals"),
        listOf(
            tr("**Heute:** deine Minuten und wie viel zum Tagesziel fehlt.", "**Today:** your minutes and how much is left to your daily goal."),
            tr("**Woche und Jahr:** jeder Tag als Balken bzw. als Feld im Kalender.", "**Week and year:** every day as a bar or as a square in the calendar."),
            tr("**Ziele:** Tagesziel und Streak-Ziel änderst du hier. Dazu dein Rekord und dein Streak-Eis.", "**Goals:** change your daily goal and streak goal here. Plus your record and your streak freezes."),
        ),
    ) { ProgressArt() },
    GuideSection(
        R.drawable.ic_screen_alt, tr("Widgets", "Widgets"),
        listOf(
            tr("**Hinzufügen:** Startbildschirm gedrückt halten, „Widgets“ antippen und „Lumina“ suchen.", "**Add one:** press and hold your home screen, tap \"Widgets\" and look for \"Lumina\"."),
            tr("**Lumi:** zeigt, wie dein Tag läuft, mit einem Knopf direkt zum Start. Breit, hoch oder klein, Lumina passt es der Größe an.", "**Lumi:** shows how your day is going, with a button straight to the start. Wide, tall or small, Lumina adapts it to the size."),
            tr("**Streak, Woche, Monat:** deine Flamme, die letzten 7 Tage und der Monat als Kalender.", "**Streak, week, month:** your flame, the last 7 days and the month as a calendar."),
        ),
    ) { WidgetArt() },
    GuideSection(
        R.drawable.ic_notif, tr("Benachrichtigungen", "Notifications"),
        listOf(
            tr("**Nutzungslimit:** Lumi zählt die Zeit in Gruppen (etwa Videos und Social Media) oder einzelnen Apps und meldet sich ab deinem Limit, auf Wunsch alle 30 Minuten wieder.", "**Usage limit:** Lumi counts the time in groups (such as videos and social media) or single apps and speaks up from your limit, every 30 minutes again if you like."),
            tr("**Kommt nichts an?** Oben unter „Erinnerungen“ steht dann, was fehlt, zum Beispiel pünktliche Wecker, ein stummgeschaltetes Pop-up oder das Energiesparen bei Samsung. Antippen behebt es.", "**Nothing arriving?** At the top of \"Reminders\" you'll see what's missing, such as exact alarms, a silenced pop-up or Samsung's battery saving. Tap to fix it."),
        ),
    ) { NotifyArt() },
    GuideSection(
        R.drawable.ic_palette, tr("Aussehen und Gefühl", "Look and feel"),
        listOf(
            tr("**Stil:** Glas, Cartoon, Modern oder Flat, dazu Farbschema und Hintergrund. Am Anfang: Indigo-Glas. Hell, Dunkel oder System schaltest du oben rechts in den Einstellungen.", "**Style:** Glass, Cartoon, Modern or Flat, plus colour scheme and background. To start with: indigo glass. Switch light, dark or system at the top right of Settings."),
            tr("**Schrift:** passend zum Stil (Inter, bei Flat Nunito, bei Cartoon Fredoka) oder fest eine davon. Bücher bleiben bei ihrer eigenen Schrift.", "**Font:** to match the style (Inter, Nunito for Flat, Fredoka for Cartoon) or one of them fixed. Books keep their own font."),
            tr("**Bewegung:** „Voll“ zeigt alle Animationen (so ist es eingestellt), „Wie Handy“ folgt deinem Handy, „Weniger“ beruhigt alles.", "**Motion:** \"Full\" shows every animation (the default), \"Like phone\" follows your phone, \"Less\" calms everything."),
            tr("**Kleinigkeiten:** Start-Animation beim Öffnen, Lumis Glühdraht und wie die Sprache beim Diktieren aussieht – alles unter Einstellungen → Design.", "**Little things:** the start animation, Lumi's filament and how your voice looks while dictating – all under Settings → Design."),
            tr("**Haptik und Töne:** Stärke und Art der Vibration, Töne an oder aus.", "**Haptics and sounds:** strength and type of vibration, sounds on or off."),
            tr("**Lumi:** Unter „Lumi in der Fokuszeit“ schaltest du ihn aus. Tippst du ihn an, reagiert er.", "**Lumi:** under \"Lumi during focus\" you can switch him off. Tap him and he reacts."),
            tr("**Sprache:** Deutsch oder Englisch unter Einstellungen → Sprache.", "**Language:** German or English under Settings → Language."),
        ),
    ) { LookArt() },
    GuideSection(
        R.drawable.ic_eye, tr("Datenschutz", "Privacy"),
        listOf(
            tr("**Kein Konto, keine Werbung:** Deine Fokuszeiten, Bücher, Notizen und Einstellungen bleiben auf deinem Handy.", "**No account, no ads:** your focus times, books, notes and settings stay on your phone."),
            tr("**Zwei Ausnahmen:** „Aufräumen“ schickt den Text einer Notiz und „Kapitel finden“ den Anfang jeder Buchseite kurz an die Lumina-KI, beides nur nach deiner Zustimmung.", "**Two exceptions:** \"Clean up\" sends a note's text and \"Find chapters\" the start of each book page briefly to Lumina AI, both only after you've agreed."),
            tr("**Alles nachlesen:** Einstellungen → Rechtliches. Quellen und Lizenzen stehen unter Über Lumina, dort gibt es auch das Intro noch einmal.", "**Read it all:** Settings → Legal. Sources and licences are under About Lumina, where you can also see the intro again."),
        ),
        null,
    ),
)

@Composable
internal fun GuidePage(state: AppState) {
    val p = Funke.colors
    val all = remember(de.funke.app.ui.Lang.german) { sections() }
    var open by rememberSaveable { mutableStateOf(listOf<Int>()) }
    Row(
        Modifier
            .fillMaxWidth()
            .reveal(0)
            .padding(horizontal = Space.s2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Lumi2D(LumiPose.Idle, Modifier.size(88.dp))
        Spacer(Modifier.width(Space.s3))
        Text(tr("Alles auf einen Blick", "Everything at a glance"), style = MaterialTheme.typography.titleLarge, color = p.text, modifier = Modifier.weight(1f))
    }
    Spacer(Modifier.height(Space.s4))
    Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
        all.forEachIndexed { i, s ->
            val isOpen = i in open
            SectionCard(
                section = s,
                open = isOpen,
                onToggle = { open = if (isOpen) open - i else open + i },
                modifier = Modifier.reveal(30 + i * 16),
            )
        }
    }
    Spacer(Modifier.height(Space.s5))
    de.funke.app.ui.components.PillButton(
        tr("Intro noch einmal ansehen", "Show the intro again"),
        onClick = state::restartOnboarding,
        modifier = Modifier.fillMaxWidth().padding(horizontal = Space.s1),
    )
}

/** Ein Bereich: Kopf (Symbol, Name, Pfeil) und aufgeklappt Bild und Punkte. Der Pfeil dreht sich mit (200 ms, Ease-out). */
@Composable
private fun SectionCard(section: GuideSection, open: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val p = Funke.colors
    val still = reducedMotion
    val turn by animateFloatAsState(if (open) 90f else 0f, Motion.spec(Motion.Base, Motion.EaseOut), label = "guideChevron")
    // Gedrückt: Zustandsschicht genau über dem Kopf (offen unten eckig, weil darunter der Inhalt weitergeht)
    val r = de.funke.app.ui.theme.Radius.xl
    val headShape = androidx.compose.foundation.shape.RoundedCornerShape(r, r, if (open) 0.dp else r, if (open) 0.dp else r)
    Column(modifier.fillMaxWidth().glass(Shapes.card)) {
        Row(
            Modifier
                .fillMaxWidth()
                .bouncyClickable(shape = headShape, pressedScale = 1f, role = Role.Button, onClick = onToggle)
                .semantics {
                    heading()
                    stateDescription = if (open) tr("aufgeklappt", "expanded") else tr("zugeklappt", "collapsed")
                }
                .padding(horizontal = Space.s4, vertical = Space.s3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(Sizes.tile).clip(Shapes.sm).background(p.accentSoft), contentAlignment = Alignment.Center) {
                Icon(painterResource(section.icon), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.icon))
            }
            Spacer(Modifier.width(Space.s4))
            Text(section.title, style = MaterialTheme.typography.titleMedium, color = p.text, modifier = Modifier.weight(1f))
            ChevronRight(Modifier.graphicsLayer { rotationZ = turn })
        }
        Unfold(visible = open, enter = Motion.expand(still), exit = Motion.collapse(still)) {
            Column(Modifier.fillMaxWidth().padding(start = Space.s4, end = Space.s4, bottom = Space.s4)) {
                section.art?.let {
                    it()
                    Spacer(Modifier.height(Space.s4))
                }
                Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
                    section.points.forEach { point -> Point(point) }
                }
            }
        }
    }
}

/** Ein Punkt: kleiner Akzentpunkt, Text mit fettem Stichwort vorn (**so**). */
@Composable
private fun Point(text: String) {
    val p = Funke.colors
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .padding(top = 8.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(p.accent),
        )
        Spacer(Modifier.width(Space.s3))
        Text(bolded(text, p.text), style = MaterialTheme.typography.bodyMedium, color = p.textSecondary)
    }
}

/** "**fett** normal" → fetter Anfang in der Textfarbe. */
private fun bolded(s: String, strong: Color): AnnotatedString = buildAnnotatedString {
    var rest = s
    while (true) {
        val a = rest.indexOf("**")
        val b = if (a >= 0) rest.indexOf("**", a + 2) else -1
        if (a < 0 || b < 0) {
            append(rest)
            break
        }
        append(rest.substring(0, a))
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = strong)) { append(rest.substring(a + 2, b)) }
        rest = rest.substring(b + 2)
    }
}

// ---------------------------------------------------------------------------------------------
// Kleine Bilder: jeweils die Stelle in der App, vereinfacht, in den aktuellen Farben
// ---------------------------------------------------------------------------------------------

/** Bühne für ein Bild: ein Stück "Bildschirm" in der Grundfarbe mit feinem Rand. */
@Composable
private fun Stage(height: Dp = 136.dp, content: @Composable BoxScope.() -> Unit) {
    val p = Funke.colors
    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .clip(Shapes.lg)
            .background(p.bg)
            .border(1.dp, p.divider, Shapes.lg)
            .padding(Space.s4),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Platzhalter für eine Textzeile. */
@Composable
private fun Bar(width: Dp, height: Dp = 8.dp, color: Color = Funke.colors.track) {
    Box(Modifier.width(width).height(height).clip(Shapes.pill).background(color))
}

/** App-Symbol als Platzhalter (abgerundetes Quadrat mit Zeichen). */
@Composable
private fun AppTile(color: Color, icon: Int, size: Dp = 44.dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size).clip(androidx.compose.foundation.shape.RoundedCornerShape(size * 0.28f)).background(color), contentAlignment = Alignment.Center) {
        Icon(painterResource(icon), contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.46f))
    }
}

/** Kleines rundes Abzeichen mit Symbol (Schloss, Haken …). */
@Composable
private fun Badge(color: Color, icon: Int, modifier: Modifier = Modifier) {
    val p = Funke.colors
    Box(
        modifier.size(20.dp).clip(CircleShape).background(p.bg).padding(2.dp).clip(CircleShape).background(color),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
    }
}

@Composable
private fun ReadingArt() {
    val p = Funke.colors
    Stage {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(92.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(92.dp)) {
                    val sw = 7.dp.toPx()
                    val tl = Offset(sw / 2, sw / 2)
                    val sz = Size(size.width - sw, size.height - sw)
                    drawArc(p.track, 0f, 360f, false, tl, sz, style = Stroke(sw))
                    drawArc(p.accent, -90f, 360f * 0.62f, false, tl, sz, style = Stroke(sw, cap = StrokeCap.Round))
                }
                Text("25:00", style = NumberStyle.copy(fontSize = 20.sp, lineHeight = 24.sp, fontFeatureSettings = "tnum, ss03"), color = p.text)
            }
            Spacer(Modifier.width(Space.s5))
            Column {
                // Halten zum Starten: der Knopf füllt sich
                Box(Modifier.width(148.dp).height(40.dp).clip(Shapes.pill).background(p.track), contentAlignment = Alignment.CenterStart) {
                    Box(Modifier.fillMaxWidth(0.6f).fillMaxHeight().clip(Shapes.pill).background(p.accent.copy(alpha = 0.45f)))
                    Text(
                        tr("Halten …", "Hold …"),
                        style = MaterialTheme.typography.labelLarge,
                        color = p.text,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                Spacer(Modifier.height(Space.s3))
                // Regler zum früheren Aufhören
                Box(Modifier.width(148.dp).height(40.dp).clip(Shapes.pill).background(p.track).padding(4.dp), contentAlignment = Alignment.CenterStart) {
                    Box(Modifier.size(32.dp).clip(CircleShape).background(p.raised(Elevation.Level2)), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_arrow_right), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(Sizes.iconSm))
                    }
                }
            }
        }
    }
}

@Composable
private fun LockArt() {
    val p = Funke.colors
    Stage {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Modus-Name wie oben links auf der Startseite
            Row(
                Modifier.clip(Shapes.pill).background(p.track).padding(horizontal = Space.s3, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painterResource(R.drawable.ic_book), contentDescription = null, tint = p.accent, modifier = Modifier.size(Sizes.iconSm))
                Spacer(Modifier.width(6.dp))
                Text(tr("Fokus", "Focus"), style = MaterialTheme.typography.labelLarge, color = p.text)
                Spacer(Modifier.width(4.dp))
                ChevronRight(Modifier.size(14.dp).graphicsLayer { rotationZ = 90f })
            }
            Spacer(Modifier.height(Space.s4))
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s4)) {
                listOf(
                    Triple(Color(0xFFE5484D), R.drawable.ic_play, false),
                    Triple(Color(0xFF7E6CF2), R.drawable.ic_activity, false),
                    Triple(Color(0xFF3E9BEF), R.drawable.ic_search, false),
                    Triple(Color(0xFF2FA36B), R.drawable.ic_book, true),
                ).forEach { (c, icon, allowed) ->
                    Box {
                        AppTile(c, icon, modifier = Modifier.graphicsLayer { alpha = if (allowed) 1f else 0.35f })
                        Badge(
                            if (allowed) p.success else p.textSecondary,
                            if (allowed) R.drawable.ic_check else R.drawable.ic_lock,
                            Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp),
                        )
                    }
                }
            }
        }
    }
}

/** Notizen: ein Blatt und darunter die Leiste wie im Editor – Sprache, Mikrofon, Aufräumen (seit 15.4). */
@Composable
private fun NotesArt() {
    val p = Funke.colors
    Stage {
        Column(Modifier.width(232.dp)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .surface(Shapes.md, Elevation.Level1)
                    .padding(horizontal = Space.s4, vertical = Space.s3),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Bar(120.dp, color = p.text.copy(alpha = 0.55f))
                Bar(176.dp)
                Bar(150.dp)
            }
            Spacer(Modifier.height(Space.s2))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_languages), contentDescription = null, tint = p.textSecondary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(Space.s1))
                    Text(tr("Deutsch", "English"), style = MaterialTheme.typography.labelMedium, color = p.textSecondary, maxLines = 1)
                }
                Box(
                    Modifier.size(32.dp).clip(CircleShape).background(p.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_mic), contentDescription = null, tint = p.onAccent, modifier = Modifier.size(16.dp))
                }
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_sparkles), contentDescription = null, tint = p.accent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(Space.s1))
                    Text(tr("Aufräumen", "Clean up"), style = MaterialTheme.typography.labelMedium, color = p.accent, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun StreakArt() {
    val p = Funke.colors
    val flame = Color(0xFFFF8A3D)
    val ice = RewardColors.Ice
    Stage {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_flame), contentDescription = null, tint = flame, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(Space.s1))
                Text("12", style = NumberStyle.copy(fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum, ss03"), color = p.text)
            }
            Spacer(Modifier.height(Space.s3))
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                // fünf gelesene Tage, ein Tag mit Eis überbrückt, heute noch offen
                listOf(0, 0, 0, 1, 0, 0, 2).forEach { kind ->
                    val bg = when (kind) {
                        0 -> flame.copy(alpha = if (p.isDark) 0.22f else 0.16f)
                        1 -> ice.copy(alpha = if (p.isDark) 0.24f else 0.18f)
                        else -> Color.Transparent
                    }
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(bg)
                            .then(if (kind == 2) Modifier.border(2.dp, p.accent, CircleShape) else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (kind) {
                            0 -> Icon(painterResource(R.drawable.ic_flame), contentDescription = null, tint = flame, modifier = Modifier.size(14.dp))
                            1 -> Icon(painterResource(R.drawable.ic_snowflake), contentDescription = null, tint = ice, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GateArt() {
    val p = Funke.colors
    Stage {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                AppTile(Color(0xFFE5484D), R.drawable.ic_play, size = 52.dp)
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(p.accent),
                    contentAlignment = Alignment.Center,
                ) { Text("?", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = p.onAccent) }
            }
            Spacer(Modifier.width(Space.s4))
            ChevronRight(color = p.textTertiary)
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.width(168.dp).surface(Shapes.md, Elevation.Level1).padding(Space.s3)) {
                Text("12 · 7 = ?", style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.text)
                Spacer(Modifier.height(Space.s2))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LipBox(colors = p.lipColors(LipTone.Neutral), modifier = Modifier.weight(1f).height(30.dp), shape = Shapes.xs, lip = 2.dp) {
                        Text(tr("Weiß nicht", "Not sure"), style = MaterialTheme.typography.labelSmall, color = p.text, maxLines = 1)
                    }
                    LipBox(colors = p.lipColors(LipTone.Accent), modifier = Modifier.weight(1f).height(30.dp), shape = Shapes.xs, lip = 2.dp) {
                        Text(tr("Prüfen", "Check"), style = MaterialTheme.typography.labelSmall, color = p.onAccent, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun NudgeArt() {
    val p = Funke.colors
    Stage {
        Row(
            Modifier.width(244.dp).surface(Shapes.md, Elevation.Level2).padding(Space.s3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Lumi2D(LumiPose.Plead, Modifier.size(52.dp), tappable = false)
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.weight(1f)) {
                Text(tr("Heute noch keine Fokuszeit", "No focus time yet today"), style = MaterialTheme.typography.labelLarge, color = p.text, maxLines = 1)
                Spacer(Modifier.height(Space.s2))
                Box(Modifier.clip(Shapes.pill).background(p.accent).padding(horizontal = Space.s3, vertical = 4.dp)) {
                    Text(tr("Loslegen", "Start"), style = MaterialTheme.typography.labelMedium, color = p.onAccent)
                }
            }
        }
    }
}

@Composable
private fun TasksArt() {
    val p = Funke.colors
    Stage {
        Row(verticalAlignment = Alignment.Bottom) {
            // drei Stufen als Bücher, die mittlere ist herausgezogen
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.height(78.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(52.dp to 0.25f, 64.dp to 0.55f, 74.dp to 0.85f).forEachIndexed { i, (h, f) ->
                        val c = androidx.compose.ui.graphics.lerp(androidx.compose.ui.graphics.lerp(p.accent, Color.White, 0.35f), androidx.compose.ui.graphics.lerp(p.accent, Color.Black, 0.35f), f)
                        Box(
                            Modifier
                                .offset(y = if (i == 1) (-8).dp else 0.dp)
                                .size(22.dp, h)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
                                .background(c),
                        )
                    }
                }
                Box(Modifier.width(90.dp).height(6.dp).surface(Shapes.pill, Elevation.Level1))
            }
            Spacer(Modifier.width(Space.s6))
            // Lumi mit Doktorhut und "?": Hilfe
            Box {
                Lumi2D(LumiPose.Idle, Modifier.size(84.dp), tappable = false)
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(p.accent),
                    contentAlignment = Alignment.Center,
                ) { Text("?", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = p.onAccent) }
            }
        }
    }
}

/** Bücher: drei Umschläge im Regal (wie im Reiter Bücher), darunter ihr Stand. */
@Composable
private fun BooksArt() {
    val p = Funke.colors
    Stage {
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s4), verticalAlignment = Alignment.Top) {
            listOf(
                Triple("P", tr("34 %", "34 %"), p.textTertiary),
                Triple("K", tr("Neu", "New"), p.accent),
                Triple("F", tr("Fertig ✓", "Done ✓"), p.success),
            ).forEachIndexed { i, (letter, status, tint) ->
                val (bg, fg) = de.funke.app.ui.theme.ReaderColors.Covers[(i * 2) % de.funke.app.ui.theme.ReaderColors.Covers.size]
                Column(Modifier.width(54.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(81.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp, topEnd = 6.dp, bottomEnd = 6.dp))
                            .background(bg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .matchParentSize()
                                .padding(start = 7.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
                                .border(1.dp, fg.copy(alpha = 0.26f), androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                        )
                        Text(letter, fontFamily = androidx.compose.ui.text.font.FontFamily.Serif, fontSize = 24.sp, color = fg.copy(alpha = 0.92f))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(status, style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum, ss03"), color = tint, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun ScreenTimeArt() {
    val p = Funke.colors
    Stage {
        Column(Modifier.width(250.dp), verticalArrangement = Arrangement.spacedBy(Space.s3)) {
            listOf(
                Triple(Color(0xFFE5484D), 0.86f, "2:10"),
                Triple(Color(0xFF7E6CF2), 0.56f, "1:25"),
                Triple(p.accent, 0.30f, "0:45"),
            ).forEach { (c, f, t) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(c))
                    Spacer(Modifier.width(Space.s2))
                    Box(Modifier.weight(1f).height(12.dp).clip(Shapes.pill).background(p.track)) {
                        Box(Modifier.fillMaxWidth(f).fillMaxHeight().clip(Shapes.pill).background(c))
                    }
                    Spacer(Modifier.width(Space.s2))
                    Text(t, style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum, ss03"), color = p.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun ProgressArt() {
    val p = Funke.colors
    val days = remember { listOf(0.55f, 0.9f, 0.4f, 1f, 0.7f, 0.25f, 0.8f) }
    Stage {
        Canvas(Modifier.width(240.dp).height(96.dp)) {
            val n = days.size
            val gap = 10.dp.toPx()
            val w = (size.width - gap * (n - 1)) / n
            val goal = size.height * 0.35f
            days.forEachIndexed { i, f ->
                val h = size.height * f
                val x = i * (w + gap)
                drawRoundRect(
                    if (i == n - 1) p.accent else p.accent.copy(alpha = 0.35f),
                    topLeft = Offset(x, size.height - h),
                    size = Size(w, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                )
            }
            // Tagesziel als gestrichelte Linie
            drawLine(
                p.textSecondary,
                Offset(0f, goal),
                Offset(size.width, goal),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx())),
            )
        }
    }
}

@Composable
private fun WidgetArt() {
    val p = Funke.colors
    Stage {
        Row(
            Modifier
                .width(252.dp)
                .height(104.dp)
                .clip(Shapes.card)
                .background(p.surface)
                .background(p.accent.copy(alpha = if (p.isDark) 0.10f else 0.12f))
                .border(1.dp, p.divider, Shapes.card)
                .padding(start = 4.dp, top = Space.s3, end = Space.s3, bottom = Space.s3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.lumi_w_hello), contentDescription = null, modifier = Modifier.width(80.dp).fillMaxHeight())
            Spacer(Modifier.width(Space.s2))
            Column(Modifier.weight(1f)) {
                Text(tr("Streak gesichert!", "Streak saved!"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = p.text, maxLines = 1)
                Spacer(Modifier.height(Space.s2))
                Box(Modifier.fillMaxWidth().height(30.dp).clip(Shapes.pill).background(p.accent), contentAlignment = Alignment.Center) {
                    Text(tr("Weitermachen", "Keep going"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = p.onAccent, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun NotifyArt() {
    val p = Funke.colors
    Stage {
        Row(
            Modifier.width(262.dp).surface(Shapes.lg, Elevation.Level2).padding(Space.s3),
            verticalAlignment = Alignment.Top,
        ) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(p.accent), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_bulb), contentDescription = null, tint = p.onAccent, modifier = Modifier.size(Sizes.iconSm))
            }
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.weight(1f)) {
                Text(tr("Lumina · jetzt", "Lumina · now"), style = MaterialTheme.typography.labelSmall, color = p.textTertiary)
                Text(tr("Dein Streak ist in Gefahr", "Your streak is at risk"), style = MaterialTheme.typography.labelLarge, color = p.text, maxLines = 1)
                Text(tr("Noch 3 Std. Ein paar Minuten reichen.", "3 hours left. A few minutes will do."), style = MaterialTheme.typography.bodySmall, color = p.textSecondary, maxLines = 1)
            }
        }
    }
}

@Composable
private fun LookArt() {
    Stage(height = 120.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s3), verticalAlignment = Alignment.CenterVertically) {
            // die vier Stile als kleine Karten, jede in ihrem eigenen Look
            listOf(
                de.funke.app.ui.theme.UiStyle.Cartoon to de.funke.app.ui.theme.Palettes.defaultFor(de.funke.app.ui.theme.UiStyle.Cartoon, Funke.colors.isDark),
                de.funke.app.ui.theme.UiStyle.Glass to de.funke.app.ui.theme.Palettes.defaultFor(de.funke.app.ui.theme.UiStyle.Glass, Funke.colors.isDark),
                de.funke.app.ui.theme.UiStyle.Modern to Funke.colors,
                de.funke.app.ui.theme.UiStyle.Flat to Funke.colors,
            ).forEach { (style, palette) ->
                de.funke.app.ui.theme.PreviewTheme(palette, style, de.funke.app.ui.theme.BackgroundPattern.None) {
                    val q = Funke.colors
                    Box(
                        Modifier
                            .size(56.dp, 72.dp)
                            .clip(Shapes.md)
                            .background(q.bg)
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(Modifier.fillMaxWidth().height(28.dp).surface(Shapes.sm, Elevation.Level1), contentAlignment = Alignment.Center) {
                            Box(Modifier.size(width = 22.dp, height = 6.dp).clip(Shapes.pill).background(q.accent))
                        }
                    }
                }
            }
        }
    }
}
