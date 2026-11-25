# Lumina – development log

Detailed notes for every version, written during development (in German).
For an overview of the app, see [README.md](README.md).

## Neu in 15.6 – bereit für GitHub, F-Droid und Google Play

- **Neuer Paketname:** `de.simonbentlage.lumina` (vorher `de.funke.app`). Der Code bleibt im Paket `de.funke.app` (`namespace`). Auf bestehenden Geräten wird Lumina dadurch als neue App installiert.
- **Pünktliche Erinnerungen:** nur noch `SCHEDULE_EXACT_ALARM` (selbst erlaubt; `USE_EXACT_ALARM` erlaubt Google Play nur Wecker- und Kalender-Apps). Ohne Erlaubnis kommen Erinnerungen ungenauer, unter Erinnerungen steht dann „Pünktliche Erinnerungen erlauben“. Wird es erlaubt, plant Lumina sofort neu.
- **Energiesparen:** ohne die Berechtigung `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`; Lumina öffnet nur noch die Liste in den Einstellungen.
- **Bedienungshilfe:** Bevor die Einstellungen aufgehen, steht immer klar da, wozu Lumina sie nutzt und was nicht – mit „Zustimmen und Einstellungen öffnen“ oder „Nicht jetzt“ (verlangt Google Play). Alle Wege zum Einschalten führen über diesen Hinweis.
- **`.gitignore`:** Signierschlüssel (`*.jks`, `*.keystore`), App Bundles und `app/release/` werden nie hochgeladen.
- **Gradle Wrapper:** `gradlew`, `gradlew.bat` und `gradle/wrapper/gradle-wrapper.jar` liegen jetzt im Projekt. Für Google Play steht noch `compileSdk`/`targetSdk` 36 an (AGP Upgrade Assistant).

## Neu in 15.5 – Diktieren im Buch, Rechner geteilt, Regal und Inhalt ohne Schablonen-Look

- **Sprachnotizen beim Lesen:** Die Notiz im Buch hat jetzt dieselbe Leiste wie jede Notiz (Sprache, Mikrofon, Aufräumen) samt großer Sprach-Ansicht über dem Buch. Die Tastatur kommt erst beim Tippen ins Feld. Diktieren und Aufräumen stecken dafür in einem gemeinsamen Baustein (`NoteVoice.kt`), den Editor und Buch nutzen. Behoben: Wer eine Notiz aus dem Buch groß öffnete und dort änderte, konnte sie beim Schließen mit dem alten Text überschreiben – das Feld im Buch speichert jetzt nur noch, was sich dort geändert hat, und geht beim Übergeben zu. Diktierte Buchnotizen tragen das Mikrofon-Zeichen.
- **Rechner beim Lesen:** Statt die Seite auf 40 % zu verkleinern, bleibt sie oben in voller Breite stehen – ein Ausschnitt bis zum Rechner, unten weich gerundet. Ein Finger verschiebt (mit Schwung), zwei Finger zoomen bis 4-fach, doppelt tippen vergrößert; Wischen und Tippen an den Rand blättern weiter. PDFs werden beim Zoomen scharf nachgeladen. Unten der Rechner mit den großen Tasten. Beim ersten und zweiten Mal ein kurzer Hinweis. (`PageViewer`, `detectViewerGestures`, `ViewportShape` in `BookReader.kt`)
- **Inhaltsverzeichnis:** wie ein gesetztes Inhaltsverzeichnis statt Zeitstrahl: Nummern in eigener Spalte (Kapitel groß und leicht, Unterkapitel klein), alle Titel an einer Kante, Seiten rechts. Gelesenes leiser, die aktuelle Stelle mit Akzent und einer feinen Linie darunter, die zeigt, wie weit man im Abschnitt ist. Ohne Abschnitts-Balken, Kreise und Hof. Notizen im Inhalt ohne farbigen Randstrich.
- **Bücher:** ein Regal aus Umschlägen in Reihen zu drei statt „ein Buch groß, darunter eine Liste“. Unter jedem Umschlag Titel und Stand, „⋯“ für Umbenennen und Entfernen. Sortieren und Suchen oben als Symbole (Sortieren in einem kleinen Blatt). Bücher ohne Titelbild bekommen einen Leineneinband mit ihrem Anfangsbuchstaben.
- **Darstellung im Buch:** Papier wählen wie auf dem Tisch – drei kleine Seiten, die gewählte liegt angehoben obenauf. Die Schriftgröße in sieben Stufen zwischen kleinem und großem A (antippen oder ziehen, übernommen beim Loslassen).
- **Anleitung:** „So funktioniert's“ beschreibt jetzt alles in der App (u. a. Regal, Inhalt, Rechner und Darstellung im Buch, Notizen beim Lesen, Rückgängig und andere Fassung, Filter und Suche bei Notizen, Ausprobieren in der Denkpause, Anpassungen der Fokuszeit, Schrift, Sprache, Intro).

## Neu in 15.4 – Notiz-Leiste, Inhaltsverzeichnis und Denkpause neu gedacht

- **Notiz-Leiste:** Drei Dinge, jedes mit Wort statt Rätsel-Symbol: links die Diktiersprache („Deutsch“, Antippen wechselt), in der Mitte groß das Mikrofon, rechts „Aufräumen“. Ob als Text oder als Stichpunkte, fragt „Aufräumen“ erst beim Antippen – in einem kleinen Blatt mit je einem Bild vom Ergebnis; die zuletzt gewählte Form ist vorgewählt. Keine Glas-Pille mehr um die Leiste, die Kopfzeile des Editors hat schlichte Symbole wie die Startseite. Das Funkel-Symbol dreht sich nur noch, während Lumi aufräumt (vorher lief die Drehung unsichtbar die ganze Zeit mit), und läuft danach weich aus. Doppelte Haptik beim Antippen behoben. (`NoteDock.kt`, `NoteEditor.kt`)
- **Inhaltsverzeichnis als Leseweg:** Oben der Lesefortschritt – ein Abschnitt je Kapitel, darunter das aktuelle Kapitel und die Seite. Links läuft eine feine Linie durchs ganze Verzeichnis; jedes Kapitel ist ein Punkt mit seiner Nummer: gelesene zart getönt, das aktuelle gefüllt, kommende als Ring. Bis zur aktuellen Stelle ist die Linie in der Akzentfarbe, die Stelle selbst ist leicht hinterlegt. Unterkapitel sind kleine Punkte auf der Linie und klappen rechts über den Pfeil auf. Fußzeile, Suchhinweis und „Lumi sucht die Unterkapitel …“ stehen an derselben Kante wie die Titel. (`BookReader.kt`: `DrawerHeader`, `ReadingBar`, `ChapterRow`, `TocDot`)
- **Denkpause:** Die Bildfolge „So läuft eine Denkpause“ ist weg. Der Satz oben erklärt es selbst („Bevor eine gewählte App aufgeht, stellt Lumi eine kurze Frage. Ein Moment Pause statt Scrollen.“), darunter steht leise „Ausprobieren“. Der Schalter steht auf Höhe der Überschrift. (`GateTab.kt`)
- **Anleitung:** Bild und Text zu den Notizen zeigen die neue Leiste; auf Englisch heißt „Aufräumen“ überall „Clean up“.

## Neu in 15.3 – Nachschliff

- **Bücher:** Ab sechs Büchern gibt es oben eine Lupe; die Suche findet Titel und Autoren, Zurück schließt sie. Das Suchfeld ist dasselbe wie im Inhaltsverzeichnis (`ui/components/SearchField.kt`).
- **Einheitlich:** „Im Regal“, „So läuft eine Denkpause“ und „Tagesziel“ nutzen dieselbe leise Abschnitts-Überschrift wie der Rest der App.
- **Einstellungen:** Die Vorschau unter Aussehen nennt Stil und Schrift („Glass, Inter“) statt Stil und Ring mit Mittelpunkt.

## Neu in 15.2 – strenger Rundgang nach Simons Test von 15.1

**Bücher**
- Neu als ruhige Liste, die mit einem wie mit hundert Büchern gleich aussieht: oben etwas größer das Buch, an dem man gerade ist (sonst das zuletzt hinzugefügte), darunter jedes Buch mit derselben Anatomie – Umschlag, Titel, Autor, Balken und „34 %“, „Neu“ oder „Fertig“. Keine leere Mitte mehr; die Liste baut nur sichtbare Zeilen (flüssig auch bei vielen Büchern).
- „⋯“: großes „Weiterlesen“, darunter ruhig „Umbenennen“ und in Rot „Aus dem Regal entfernen“ statt zwei Pillen nebeneinander.

**Kapitel**
- Die Lumina-KI gliedert höchstens drei verschiedene Bücher in sieben Tagen (nur Erfolge zählen, „Neu suchen“ beim selben Buch nicht doppelt). Danach – oder ohne KI, oder offline – erkennt Lumina die Kapitel direkt auf dem Handy aus dem Text: „Kapitel 3“, „3 Bewegung …“, „Teil II“, Vorwort, Anhang, Lösungen, Register. Kopfzeilen mit Seitenzahl und Inhaltsverzeichnis-Seiten fallen heraus. EPUBs ohne Verzeichnis: die Überschriften im Text.
- Inhaltsverzeichnis neu gesetzt: Aufklapp-Pfeil links vor der Nummer, Seitenzahlen bündig rechts, Kapitel kräftiger mit etwas Luft davor, Unterkapitel leiser und genau unter dem Titel darüber. Unten leise, woher die Kapitel kommen („Von der Lumina-KI erkannt“ / „Aus dem Text erkannt“).

**Fokuszeit**
- Unten fluchtet alles: Das Schloss steht genau über dem Knopf des Reglers, der Rechner genau über dessen Ziel. „Alles gesperrt“ mit dem Schloss im Stil der übrigen Symbole; erlaubte Apps mit offenem Schloss davor.
- Oben der Modus wie auf der Startseite (Kugel und Name statt Glas-Pille), auf denselben Achsen.

**Denkpause**
- „So läuft eine Denkpause“: drei Bilder – deine App, Lumi stellt eine kurze Frage, dann weiter oder lieber nicht – und „Ausprobieren“: eine echte Denkpause als Probe, die nichts zählt und keine App öffnet.

**Aufgaben**
- „Gemischt“: Fächer frei wählbar (Raster mit Fach-Bildern und Haken, „Alle“ mit einem Tipp, mindestens eins bleibt).
- Behoben: Wählte man während des Funkenregens schnell eine andere Stufe, blieben ein paar Funken stehen.

**Fortschritt**
- Ruhiger: Streak frei oben mit Rekord und Streak-Eis in einer Zeile, Heute und Woche auf einer Fläche, Tagesziel ohne Karte drumherum (4 statt 7 Flächen).

**Schriften**
- Jeder Stil hat seine eigene Schrift: Glass und Modern Inter, Flat Nunito (weich und rund), Cartoon Fredoka für Überschriften mit Nunito für Text, Zahlen und Aufgaben (Fredoka hat ungleich breite Ziffern und keine Mathe-Zeichen). Einstellungen → Aussehen → Schrift: „Zum Stil“ oder fest Inter, Nunito, Fredoka. Beide Schriften unter SIL OFL (`licenses/`), gebaut mit `tools/fonts-make.py` aus google/fonts.

## Neu in 15.1 – aufgeräumt für die Veröffentlichung

**Weniger Rahmen, mehr Ruhe** (Vorbild: die Kopfzeile der Startseite)
- Fokuszeit unten: „Alles gesperrt“ bzw. die erlaubten Apps stehen frei, Notizen und Rechner sind schlichte Symbole statt Glas-Kreise und Pille.
- Modi: erlaubte Apps als Symbole mit Namen statt Pillen, kein Leuchtschein mehr hinter der gewählten Karte.
- Navigationsleiste: ruhigerer Rand, die Markierung ist nur noch ein heller Hauch (keine Pille in der Pille).
- Bücher: das aktuelle Buch liegt frei auf dem Grund statt in einer Glaskarte, mit größerem Umschlag.
- Denkpause: Lumi und Schalter stehen frei; die Zeilen darunter ohne farbige Kästchen. Fehlt die Bedienungshilfe, ist genau diese Zeile zart rot hinterlegt.
- Einstellungen: Symbole ohne eigene Kacheln, Hell/Dunkel oben als schlichtes Symbol, Vorschau ohne wandernden Leuchtrand. „Eigene Erinnerung“ ohne Umrandung.

**Einzelnes**
- Start-Reiter mit Haus-Symbol (der Sucher war zu rätselhaft).
- Physik: Fachfarbe Himmelblau, das Atom passend dazu.
- Stufe wählen in den Aufgaben: Aus dem Buch steigt ein warmer Lichtschein mit Funken auf, das gewählte Buch leuchtet oben leise weiter – als käme das Wissen heraus.
- PDFs zoomen erst bei deutlicher Zwei-Finger-Bewegung; wer kaum vergrößert, landet wieder bei der ganzen Seite.
- Inhalt im Buch: Die Kapitelsuche kommt nur auf Abruf über die Lupe neben den Reitern.
- Einstellungen → Über Lumina zeigt die echte Version (stand vorher fest auf 13.4).

**Veröffentlichung (GitHub, F-Droid)**
- `fastlane/metadata/android/` mit Titel, Kurz- und Langbeschreibung (Deutsch, Englisch) und Änderungen.
- Kein verschlüsselter Abhängigkeits-Block im APK (`dependenciesInfo`), wie F-Droid es verlangt.

## Neu in 15.0 – Feinschliff nach Simons Test von 14.0

**Intro**
- Der Sprachschalter DE/EN oben rechts wird nicht mehr abgeschnitten (eigene kleine Pille statt eines zu schmalen Felds).
- Lumis Schein ist weich und läuft sanft aus – er wird nirgends mehr gekappt (Intro, Widgets, Startseite). Die Arme sind wieder etwas kürzer, aber rund und freundlich.
- „Wie viele Tage am Stück?“: vier Stufen mit Namen und einem motivierenden Satz statt nackter Zahlen – **Erste Woche** (7), **In Schwung** (14), **Gewohnheit** (30, empfohlen), **Unaufhaltsam** (60). Lumi freut sich je nach Stufe anders. Der Kalender mit den Monatsnamen zwischen den Zeilen ist weg; stattdessen „Geschafft am 24. Dezember“.
- Beim Tagesziel zeigt Lumi zu jeder Stufe eine eigene Pose.

**Überall**
- **Abgehackte Konturen behoben:** Aufklappende Bereiche (Denkpause, Bildschirmzeit, Hilfe, Rechner, Notizen, Intro) schnitten ihren Inhalt dauerhaft ab – Schatten und die schwarze Cartoon-Kontur wirkten dadurch gekappt. Jetzt wird nur während der Bewegung abgeschnitten. Ebenso Lumis Sprechblase in den Aufgaben und der Schieber der Umschalter.
- Tab-Symbole: **Fokus** ist jetzt ein Sucher (Viewfinder), **Bücher** bleibt das offene Buch – vorher sahen beide gleich aus.
- Die schlichte Kopfzeile der Startseite ist das Vorbild: Werkzeuge oben ohne Glas und Rand (z. B. „+“ bei den Büchern).
- Fach-Bilder im hellen Modus: **Physik** ist jetzt ein ruhiges Atom in der Farbe des Fachs (vorher blau auf Orange), **Geschichte** und **Chemie** heben sich deutlich vom hellen Grund ab.
- Die Frage in den Aufgaben steht halbfett im Display-Schnitt von Inter statt fett – klar, aber ruhiger.
- Was auf einem Handy nicht gehen kann, taucht dort nicht mehr auf: „Kapitel finden“ für PDFs erst ab Android 15, das Mikrofon nur mit Spracherkennung auf dem Handy (vorher Knopf mit Fehlermeldung).

**Bücher**
- Ein Buch **einfach antippen** öffnet es (vorher schluckte die Lang-Drücken-Erkennung das Antippen). „⋯“ öffnet Umbenennen und Entfernen, lange drücken geht weiterhin.

**Im Buch**
- Die Leiste unten ist eine schwebende Pille ohne Rand – getragen nur von Fläche und weichem Schatten; auf Papier heller als die Seite.
- **Inhalt komplett neu:** oben das Buch (kleiner Umschlag, Titel, „Seite 31 von 412“), darunter drei Reiter als Wörter mit gleitender Linie und Anzahl. Kapitel als Baum: Nummern in einer eigenen Spalte, Unterkapitel eingerückt (bis zu drei Ebenen), mit dem Pfeil rechts aufklappbar; die aktuelle Stelle ist zart hinterlegt und die Liste springt beim Öffnen dorthin. Lesezeichen mit Kapitel und Tag, Notizen als ruhige Zitate. Lange Listen laden nur, was sichtbar ist.
- **Unterkapitel aus dem PDF selbst:** Viele PDFs haben ein Inhaltsverzeichnis eingebaut. Lumina liest es jetzt direkt aus – mit Kapiteln und Unterkapiteln, ohne KI und ohne Internet, auf jedem Android, auch bei „kopiergeschützten“ PDFs. Bestehende Bücher bekommen es beim nächsten Öffnen. Nur ohne eingebautes Verzeichnis kommt die Lumina-KI ins Spiel.
- Inhaltsverzeichnisse liegen je Buch in einer eigenen kleinen Datei – die Bücherliste bleibt klein, Speichern während des Lesens hakt nicht.

**Denkpause**
- Ist sie aus, döst Lumi (Augen zu, kleine „z“) statt einer blassen weißen Glühbirne.
- Apps, Zeiten und Fächer erscheinen erst, wenn die Denkpause an ist – vorher waren es halb ausgegraute Zeilen, die man trotzdem antippen konnte.

**Aufgaben**
- Die gewählte Stufe wird sofort gemerkt (vorher stand nach „Experte“ und Zurück wieder „Mittel“ unter Physik). Unter jedem Fach zeigen fünf Punkte die Stufe, damit klar ist, was die Zeile bedeutet.
- Wahl einer Stufe: Das Buch hüpft kurz aus dem Regal und versprüht einen kleinen Funkenbogen.

**Widgets**
- Text in mittlerer statt fetter Schrift, wie in der App. Die Diagramme (Woche, Monat) sind jetzt in Inter gezeichnet. (Eigene Schriften für Text direkt im Widget erlaubt Android nicht.)
- Lumi-Bilder neu gerendert (weicher Schein, kürzere Arme).

**Prüfen ohne Android Studio:** Die Prüfung liegt jetzt im Projekt unter `tools/check/` (siehe README dort), samt Test für das PDF-Inhaltsverzeichnis.

**Lumina-KI-Worker:** unverändert Version 4 – nichts zu tun.

## Neu in 14.0 – alles noch einmal durchgesehen

**Grundlook**
- Standard ist jetzt **Indigo-Glas** (Nacht mit einem Hauch Indigo, ruhiges Periwinkle als Akzent) – im Intro die erste von zwei Wahlen, daneben Cartoon. Wer Glas mit Obsidian hatte, bekommt einmalig Indigo.
- Animationen stehen standardmäßig auf **Voll** (neu: „Voll | Wie Handy | Weniger“).
- Dialoge im Glas-Stil sind jetzt deckend – vorher schien der Inhalt dahinter durch.
- Glas-Hintergrund ruhiger (ein Licht oben, ein leiser Schein unten), Beschriftungen nicht mehr in Großbuchstaben, Ziffern überall gleich breit.
- Startseite: Kopfzeile luftiger (Modus als ruhiges Wort, Notizen und Rechner als schlichte Symbole, nur der Streak mit Fläche). Der Start-Knopf sagt „25 Min Fokus“.
- Überall „Fokus“ statt „Lesen“: Fokuszeit, Fortschritt „Fokus | Bildschirmzeit“, Erinnerungs- und Widget-Texte, Intro, Hilfe.

**Bücher**
- Regal neu geordnet: oben groß das Buch, an dem du gerade bist (nur dort, nicht noch einmal darunter), darunter die anderen. Jedes Buch hat „⋯“ für Umbenennen und Entfernen; lange drücken geht auch.
- Entfernen ging vorher nicht, weil die Leiste unten über dem Blatt lag. Jetzt weicht die Leiste, solange ein Blatt offen ist.
- Leeres Regal: „Was liest du gerade?“ und ein Knopf, kein Erklärtext. Nach dem Lesen ein Satz, wie viele Minuten es waren und was bis zum Tagesziel fehlt.

**Im Buch**
- Rand antippen oder wischen blättert sofort, flüssig (die Position wird nicht mehr bei jedem Wischen in die beobachtete Liste geschrieben, Seiten liegen auf eigenen Ebenen).
- Einmal in die Mitte tippen tut nichts mehr, **doppelt tippen** öffnet das Menü (beim ersten Mal steht es kurz da). Kein Schieberegler, keine Prozente – nur leise die Seitenzahl.
- Menü: oben links **Inhalt** (kommt von links), oben rechts Schließen. Unten eine schwebende Leiste: Notiz, Rechner, Merken, Darstellung.
- **Notiz, Rechner und Darstellung verdecken die Seite nicht mehr:** Sie liegen unten an, die Seite rückt nach oben und wird kleiner wie ein Blatt auf dem Tisch. Blättern geht weiter. Der Rechner lässt sich zur schmalen Leiste einklappen.
- **Notizen gehören zur Seite**, auf der du sie schreibst. Ein kleiner Stift neben der Seitenzahl zeigt sie, im Inhalt unter „Notizen“ stehen alle aus dem Buch, in der Notizen-Übersicht mit Buch und Seite, und in der Notiz springt „Buch, Seite 12“ genau dorthin. Groß öffnen (Diktieren, Aufräumen) geht weiter.
- Inhalt mit drei Reitern (Kapitel, Lesezeichen, Notizen), Suche bei vielen Kapiteln, **Kapitel mit Unterkapiteln zum Aufklappen**. Für PDFs sucht Lumi nummerierte Unterkapitel („3.2 …“) direkt im Text, ohne Internet (ab Android 15).

**Erinnerungen**
- Neuer Kanal mit höchster Wichtigkeit: **Pop-up** heißt jetzt so und kommt oben als Banner. Steht Lumina im System auf leise, erscheint oben eine Zeile zum Einschalten.
- **Vollbild** (früher „Bildschirm“): sofort, wenn du das Handy benutzt, sonst beim nächsten Entsperren.
- Liste wie beim Wecker: große Uhrzeit, Name, „Täglich als Pop-up“. Die Namen sagen schon, wann sie kommen („Tagesziel“, „Streak retten“), die Erklärsätze sind weg.
- Editor: große Uhrzeit in 15-Minuten-Schritten (gedrückt halten zählt schnell), Wochentage, Pop-up oder Vollbild mit Bild, **„Jetzt testen“ und „In 1 Min.“** (echter Weg über den Wecker – Handy ruhig weglegen) und eine Zeile, was zuletzt passiert ist („Zuletzt heute, 19:00 als Pop-up“, „nicht nötig – schon geschafft“ …).
- Eigene Erinnerungen ohne Beispielsätze: Felder „Titel“ und „Nachricht (optional)“.
- Absturzschutz: Einstellungen werden robust gelesen (alte Werte mit anderem Typ ließen die Seite abstürzen).

**Rechner**
- Ruhiger und klarer: flache Tasten im 8-dp-Raster, nur „=“ in der Akzentfarbe, Rechenzeichen in Akzentschrift, Funktionen als leise Texttasten, kleiner Grad/Rad-Umschalter statt Titel.

**Aufgaben**
- Die Stufen-Bücher heben sich mit einer weichen Feder aus dem Regal, ohne Kippen und Lichtstreifen.

**Lumina-KI-Worker:** unverändert Version 4 – nichts zu tun.

## Neu in 13.4 – Bücher, Rechner und Erinnerungen ohne Absturz

**Bücher (neuer Reiter)**
- PDFs und E-Books (EPUB) aus „Dateien“ hinzufügen. Lumina legt eine Kopie ins Regal, damit das Buch auch dann noch aufgeht, wenn die Originaldatei verschoben wird.
- Regal mit Umschlägen (eigene Titelbilder oder ruhige Farbumschläge), „Weiterlesen“-Karte oben, sortierbar nach Zuletzt, Titel oder Neu. Lange drücken: umbenennen oder entfernen.
- Lesen Seite für Seite wie auf einem E-Reader: wischen oder am Rand tippen. Vollbild ohne Statusleiste, ohne Uhr, Bildschirm bleibt an.
- Position wird bei jedem Umblättern gespeichert und beim nächsten Öffnen genau dort fortgesetzt.
- Menü (Tipp in die Mitte): Inhalt, Lesezeichen, Darstellung (Papier, Sepia, Nacht; bei E-Books Schriftgröße und Buchschrift/Inter), Notiz, Rechner, Seiten-Schieberegler.
- PDFs: Zoom mit zwei Fingern, beim Zoomen wird die Seite schärfer nachgeladen. „Nacht“ kehrt die PDF-Farben um.
- Kapitel: E-Books bringen ihr Inhaltsverzeichnis mit (EPUB 2 und 3). Für PDFs findet die Lumina-KI Kapitel und Unterkapitel (ab Android 15, einmal automatisch beim ersten Öffnen, wenn die KI eingeschaltet ist). Dafür geht nur der Anfang jeder Seite an den Server, nicht das Buch.
- Lesezeit im Buch zählt fürs Tagesziel, ab 5 Minuten am Tag auch für den Streak. Liegt das Handy 5 Minuten unberührt, pausiert die Zeit. Während du liest, schweigen Lese- und Streak-Erinnerungen.

**Fortschritt mit Bildschirmzeit**
- Der Reiter „Apps“ ist im Fortschritt aufgegangen: oben „Lesen | Bildschirmzeit“. Alles von vorher ist noch da.
- Die Streak-Flamme oben rechts auf der Startseite öffnet den Fortschritt.

**Wissenschaftlicher Rechner**
- Wie der erweiterte Samsung-Rechner: 2nd, Grad/Bogenmaß, sin/cos/tan und Umkehrungen, hyperbolisch, ln/log, Potenzen, Wurzeln, Fakultät, π, e, EE, Ans, Verlauf.
- Auf der Startseite neben Notizen, in der Lesezeit neben dem Notizblock, in Aufgaben (mit „Übernehmen“) und im Buch.

**Erinnerungen**
- Nur noch zwei Wege: **Mitteilung** (Pop-up) oder **Bildschirm** (sofort, wenn du das Handy gerade benutzt, sonst beim nächsten Entsperren). Vollbild über dem Sperrbildschirm ist weg.
- Absturz behoben: „Ausprobieren“ bei „Beim Entsperren“ hat die App so zerschossen, dass sie nicht mehr aufging. Die Erinnerung läuft jetzt über denselben sicheren Weg wie die Denkpause, nicht mehr über ein eigenes Fenster.

**Denkpause**
- Umschalter heißt „Fragen | Erinnerungen“, die Fragezeiten lassen sich jetzt wirklich antippen (auch bei den Vorgaben).
- Bedienungshilfe: eine bebilderte Anleitung zeigt, wo der Schalter liegt (direkt dorthin springen erlaubt Android nicht).

**Sonst**
- Tagesziel startet bei 30 Minuten (empfohlen).
- Lumina-KI-Worker Version 4 (`server/lumina-ki-worker.js`, siehe `server/ANLEITUNG.md`).

## Neu in 13.3 – die fertige Fassung

**Erinnerungen, die wirklich ankommen**
- Pünktlich auf die Minute: exakte Wecker statt ungenauer (Samsung hat die alten oft verschluckt), neuer Kanal mit Pop-up und Ton.
- Drei Wege pro Erinnerung: **Mitteilung**, **Beim Entsperren** (Lumi-Bildschirm, sobald du das Handy benutzt) oder **Vollbild** (wie ein leiser Wecker, auch über dem Sperrbildschirm).
- „In 15 Minuten“ direkt in der Mitteilung und im Lumi-Bildschirm. „Ausprobieren“ zeigt jede Erinnerung sofort, Vollbild nach 10 Sekunden zum Handy-Sperren.
- Fehlt etwas (Benachrichtigungen, pünktliche Wecker, Vollbild, Energiesparen, Bedienungshilfe, Nutzungsdaten), steht genau das oben unter „Erinnerungen“ und lässt sich mit einem Tippen beheben.
- Lesen und Streak schweigen, wenn heute schon alles erledigt ist. Das steht jetzt auch auf den Karten.

**Denkpause-Reiter neu**
- Oben ein Umschalter: **Fragen vor Apps** | **Erinnerungen**. Nichts ist mehr durcheinander.
- Fragezeiten als 24-Stunden-Zifferblatt statt der Linie: Jede Fragezeit ist ein Licht, die nächste leuchtet am hellsten. Bei „Eigene“ tippt man Stunden direkt am Zifferblatt an.
- Jede Erinnerung als Karte („19:00 · täglich · Mitteilung“), antippen öffnet den Editor mit Uhrzeit, Wochentagen, Weg und „Ausprobieren“.
- **Eigene Erinnerungen** mit Titel, Text, Uhrzeit und Wochentagen.
- **Nutzungslimit** statt „Viel Videozeit“: beliebige Gruppen und/oder einzelne Apps, Limit in 15-Minuten-Schritten, auf Wunsch alle 30 Minuten wieder. Die Bedienungshilfe zählt etwa minütlich nach.
- Schlafenszeit bekommt einen eigenen Wecker.

**Erklärungen, die man ohne Vorwissen versteht**
- Der Lösungsweg zeigt jeden Schritt als Karte: Gefragt, Die Idee (in Worten), So rechnest du (die Formel mit einem Satz dazu), Eingesetzt, Ergebnis.
- „Zeichen kurz erklärt“: Bruch, Wurzel, Sinus, Ableitung, ⇒, ≈, Zehnerpotenzen und mehr in einfachen Sätzen, automatisch für alle 1255 Aufgaben.
- In der Tipp-Blase bekommt eine Formel einen Satz davor und ihre Zeichen erklärt.

**Kleinigkeiten mit Wirkung**
- Intro: Antippen der dritten Erlaubnis öffnet die Bedienungshilfen, darunter steht der genaue Weg („Installierte Apps → Lumina Sperre“) und eine Hilfe, falls der Schalter ausgegraut ist. Direkt zum Schalter springen erlaubt Android nur dem System.
- Bücherregal: Das gewählte Buch wird wie ein echtes herausgezogen (kippt nach vorn, leuchtet auf, Lichtstreifen über den Rücken). Bei „Weniger Bewegung“ gleitet es ruhig hoch und leuchtet.
- Physik hat ein neues Bild: ein Atom mit Kern aus Protonen und Neutronen (statt Eigelb).
- Notizen: Nach dem Aufräumen zählt „Rückgängig“ sichtbar die Sekunden herunter. Danach bleibt die vorherige Fassung bei der Notiz gespeichert („Andere Fassung“), auch nach Tagen.
- Lumi trägt den Doktorhut nur noch, wenn er erklärt: im Intro, in den Reiter-Einführungen und bei den Aufgaben-Tipps.
- Rechtliches: Datenschutzerklärung um Vollbild-Erinnerungen, exakte Wecker, Energiesparen und Nutzungslimit ergänzt (Stand 19.11.2026).

## Neu in 13.2 – viel mehr verschiedene Aufgaben

Gezählt wird streng: Aufgaben mit demselben Aufbau (etwa Wasserkocher, Tee und Blitz-Energie) zählen nur einmal.

| Fach | geschrieben | verschieden (streng) |
|---|---|---|
| Physik (19 Themen) | 408 | 321 |
| Mathe (8 Themen) | 260 | 207 |
| Logik | 37 | 35 |
| Erdkunde | 38 | 38 |
| Chemie | 41 | 40 |
| Biologie | 42 | 42 |
| Geschichte | 42 | 40 |
| Geologie | 39 | 39 |
| Extras (Allgemeinwissen, Spaß-Fakten) | 40 | 40 |
| Philosophie | 149 | 149 |
| Literatur | 159 | 159 |

- **Physik:** 259 neue Aufgaben in allen 19 Themen, von Bungee, Hohmann-Bahn und Wärmepumpe bis Rastertunnelmikroskop, Higgs und Kosmologie. Jede mit Hinweisen, Lösungsweg und Hintergrund. Viele Denkfragen ohne Rechnung (Tischdecken-Trick, Fosbury-Flop, Spiegelbild, Mondphasen).
- **Mathe:** 194 neue Aufgaben, darunter Statistik in den Nachrichten (Simpson-Paradoxon, relatives Risiko, abgeschnittene Achsen), Denkfallen (Kreisgebiete, Krankenhaus-Frage, Efron-Würfel) und Alltag (Ratenkauf, Farbe kaufen, Wechselkurs).
- **Alle anderen Fächer:** je mindestens 30 verschiedene Aufgaben, mit neuen Rechen- und Denkaufgaben statt nur Begriffsfragen.
- **Bestehende Aufgaben:** Grammatik bei Einheiten (1 Sekunde, 1 Lichtjahr), Farbnamen, Toleranzen, Schwierigkeitsstufen von gut 20 Aufgaben angepasst.
- **Große und kleine Zahlen:** Das Eingabefeld fasst 10 Zeichen. Aufgaben mit Ergebnissen wie 6 · 10²⁴ fragen jetzt nach dem Vorfaktor vor einer angegebenen Zehnerpotenz, damit jede Antwort eintippbar ist.
- Quellen für Anregungen (nicht wörtlich übernommen): OpenStax University Physics (CC BY 4.0), Thompson „Calculus Made Easy“ (gemeinfrei), TASI-Kosmologie-Vorlesungen; Halliday/Resnick nur als Ideengeber.
- Rechtliches unverändert.

## Neu in 13.1 – Feinschliff nach dem ersten Test

- **Denkpause-Tab neu aufgebaut:** oben der Schalter mit Status, darunter eine Liste (Apps, Zeiten, Fragen) und ein Block „Erinnerungen“ mit Uhrzeit und Schalter je Zeile. Zeiten und Fragen öffnen ein Blatt statt großer Kästen.
- **Erinnerungen an einem Ort:** Die Lese-Erinnerung ist jetzt eine einzige (Mitteilung und Hinweis beim Entsperren, gleiche Uhrzeit). Alles stellst du im Denkpause-Tab ein; in den Einstellungen geht es nur noch um die Berechtigung.
- **Beim Entsperren:** Ist dein Streak zur eingestellten Zeit noch offen, fragt Lumi einmal: jetzt lesen, in 15 Minuten nochmal oder nicht heute.
- **Fehler behoben:** Beim Öffnen einer gesperrten App kam alle paar Sekunden eine neue Frage. Der Fertig-Knopf in der App-Auswahl lag unter der Navigationsleiste.
- **Aufgaben:** Konstanten wie die Schallgeschwindigkeit stehen jetzt automatisch bei den gegebenen Größen, wenn die Lösung sie braucht. Die Echo-Aufgabe hat neue Hinweise und die richtige Formelkarte. Unpassende Formelkarten sind weg.
- **Mehr Abwechslung:** Aufgaben mit demselben Motiv (Wasserkocher, Blitz …) kommen nicht mehr kurz nacheinander, auch nicht nach einem Neustart. Der Einstieg in eine Lektion ist weniger auf leichte Aufgaben festgelegt.
- **Aufgaben-Tab:** Streak-Eis und Funken oben rechts sitzen in einer ruhigen Pille im Stil der Streak-Flamme.
- **Lumi:** entspanntere Armhaltung (Arme locker nach unten) und ein neuer Doktorhut mit Quaste links.
- **Widget:** wird ab Android 12 für die echte Größe gebaut. Knopf, Schrift und Balken richten sich nach dem verfügbaren Platz.
- Rechtliches unverändert.

## Neu in 13.0 – die fertige Fassung

**Standard-Look und Intro**
- Neu installiert startet Lumina im Cartoon-Stil in warmen Farben (hell „Sonnig“, dunkel „Abend“), mit Raster
  im Hintergrund und Hell/Dunkel wie das Handy.
- Gegen Ende des Intros wählt man den Look: **Cartoon in warmen Farben** oder **Glass in Indigo**, jeweils als
  kleine echte Vorschau. Antippen schaltet die ganze App sofort um.
- Lumi ist überall antippbar und reagiert, auch im Intro und in den Reiter-Einführungen.

**Lumi**
- Längere Arme mit größeren Handschuhen in der App und in allen 20 Widget-Bildern (neu gerechnet): Hände hängen
  locker bis zum Ende des Glases, beim Schreck fliegen die Arme hoch, traurig hängen sie herab.
- Neuer Doktorhut: breites Brett (breiter als der Kopf), Kappe, die sich um die Kugel legt, lange Quaste,
  auf dunklem Grund mit feinem Rand.

**Aufgaben**
- Kopf aufgeräumt: kein Untertitel, kein „Anpassen“, keine Überschrift „Fächer“. Streak-Eis und Funken als
  kräftige, farbige Pillen.
- Bücherregal ruhiger: Das gewählte Buch zieht sich mit Ease-out heraus (240 ms), schwebt sichtbar über dem Brett,
  ohne Kippen, Nachfedern oder Abdunkeln. Im Cartoon-Stil mit Kontur.
- Die Aufgabe steht als Karte da, die eigentliche Frage groß und fett direkt über der Antwort.
- Zahlenfeld mit Beschriftung im Feld („Masse in kg“) und blinkender Schreibmarke; leichtere Tasten.
- Lumi unten links steht frei (ohne Kasten) mit einer „?“-Blase; die Tipp-Blase zeigt mit einem Zipfel auf Lumi,
  hat einen deckenden Grund (auch bei Glass) und nur noch eine Hauptaktion; die Lösung steht leise darunter.

**Denkpause**
- Neu angeordnet wie bei JEE PYQ Lock: oben Lumi mit Schalter und Status, darunter Apps und Fragen nebeneinander,
  dann die Fragezeiten mit Tages-Zeitleiste, zuletzt „Beim Entsperren“. Ohne Erklärtexte.

**Widgets**
- Das Lumi-Widget wählt die Fassung jetzt nach Seitenverhältnis (16 ideale Größen, per Simulation geprüft):
  Breite Widgets bekommen immer die breite Fassung mit großem Lumi. Große Zahl und Tagesziel-Balken nur, wenn
  genug Platz ist. Das Streak-Widget zeigt kein Streak-Eis mehr.

**Notizen**
- „Neue Notiz“ auf der Startseite funktioniert wieder. Jede Notiz lässt sich einzeln teilen und als Datei speichern.

**So funktioniert's**
- Alle Funktionen als aufklappbare Bereiche, jeweils mit einem kleinen Bild der Stelle in der App.

**Rechtliches**
- Endgültig, ohne „Entwurf“. Anbieter mit Name und E-Mail, Lizenz GPL-3.0 (Datei `LICENSE`, Volltext auch in der App).

**Überall**
- Untertitel und kleine Erklärtexte entfernt, wo die Oberfläche selbst zeigt, was zu tun ist.

## Neu in 12.0 – Denkpause wie JEE PYQ Lock, neue Lektionen, Widgets, echtes Liquid Glass

**Denkpause (neu gebaut)**
- Einfach wie bei JEE PYQ Lock: Apps wählen, Häufigkeit wählen (Locker 3×, Ausgewogen 5×, Intensiv 8× am Tag
  oder eigene Uhrzeiten). Ab jeder Prüfzeit kommt beim nächsten Öffnen einer gewählten App eine Frage, auch
  mitten in der App. Antworten oder „Weiß ich nicht“ → Erklärung lesen → weiter zur App. Bis zur nächsten
  Prüfzeit ist Ruhe. Fragen und Schwierigkeit wie beim Abbrechen einer Lesezeit (Knopf direkt dorthin).
- Neu: **Erinnerung beim Entsperren** („Heute noch nicht gelesen“ ab einer Uhrzeit, „Bald Schlafenszeit“).
  Läuft über die vorhandene Bedienungshilfe (Systemmeldung „Gerät entsperrt“), höchstens einmal pro Stunde,
  standardmäßig aus, alles auf dem Gerät.

**Aufgaben**
- Lektionen mit 3–5 Aufgaben je nach Stufe, Stufenwahl pro Fach als animiertes Bücherregal.
  Ohne „Fertig“ und Themen-Etikett oben; die eigentliche Frage steht klar hervorgehoben über dem Eingabefeld.
- Reihenfolge: Physik, Mathe, Philosophie, Literatur, dann der Rest. Keine Aufgabenzahlen mehr unter den Fächern.
- 640 Aufgaben (vorher 514): Mathe 66 (vorher 22: Kopfrechnen, Prozent, Gleichungen, Geometrie,
  Wahrscheinlichkeit), Biologie 18, Chemie 18, Erdkunde 14, Geologie 19, Geschichte 26, Allgemeinwissen 16,
  Logik 6 Rätsel mit mehreren Schritten. Physik 149 (125 Vorlagen mit neuen Zahlen bei jedem Ziehen, 24 feste).
- Formeln können jetzt Binomialkoeffizienten (n über k) darstellen.

**Intro, Erklärungen, Widgets**
- Intro wieder schlank (Lumi, Tagesziel, Streak, Widget), kein Rundgang mehr. Lumi trägt beim Erklären einen
  Doktorhut. Denkpause, Apps und Aufgaben erklären sich beim ersten Öffnen in drei Sätzen.
- Neue Seite „So funktioniert's“ in den Einstellungen.
- Widgets neu gestaltet: großer Knopf über die ganze Breite, klare Reihenfolge (Zahl, Überschrift, Fortschritt,
  Knopf), Lichtschein hinter Lumi, Ecken wie die System-Widgets, Streak-Eis als Abzeichen.

**Design**
- Glass-Stil: echtes Liquid Glass für die Leiste unten (Inhalt dahinter weichgezeichnet ab Android 12,
  Lichtbrechung am Rand ab Android 13). Der gewählte Reiter ist in allen Stilen klar zu sehen.
- Segment-Umschalter ruhiger (ohne Nachwackeln), gewählte Option halbfett.
- Timer ab 1 Stunde passt in eine Zeile; Notiz-Knopf ohne Zahl. Holprige Texte umgeschrieben.

**Rechtliches**
- Kontakt-E-Mail eingetragen, neue Seite „Impressum & Kontakt“, Datenschutz und Nutzungsbedingungen um die
  Erinnerung beim Entsperren ergänzt. Offen: Anschrift fürs Impressum und GitHub-Adresse (siehe RECHTLICHES.md).

## Neu in 11.0 – Denkpause, endlose Aufgaben, Philosophie & Literatur, ruhiger Lesebildschirm

**Denkpause (neuer Reiter)**
- Vor dem Öffnen ausgewählter Apps (z. B. Instagram, YouTube) kommt eine kurze Aufgabe. Groß: „Doch nicht“.
  Danach ist die App für eine wählbare Zeit frei. Stärke: Sanft (überspringen nach 5 s), Ausgewogen, Streng
  (3 Notfälle am Tag). Zeitfenster und Wochentage, „Auch mittendrin“ (Check-ins zur vollen Stunde),
  Fächer und Schwierigkeit. Läuft über die vorhandene Bedienungshilfe, keine neue Berechtigung.
- Vorbild: die one-sec-Studie (PNAS 2023): Schon eine kurze Pause mit der Wahl „lieber nicht“ senkt das
  Öffnen deutlich.

**Aufgaben**
- Kein Kurs mehr: endlose Runden („Bunt gemischt“ oder ein Fach), eine Schwierigkeit für alles,
  Zurückblättern zu den letzten 20 Aufgaben. Eigene Optik statt Fortschrittsbalken.
- Philosophie: 140 Aufgaben (Denker & Ideen, Begriffe, Gedankenexperimente, Argumente prüfen mit
  Kant-Test, Rawls, Gettier, Bayes, Newcomb …). Literatur neu: 160 Aufgaben (Epochen, 70 Werke,
  Stilmittel, Gattungen, Versmaße, Reimschemata, genaue Lektüre). Wissensfächer holen sich frische
  Aufgaben von der Nachbarstufe, statt sich schnell zu wiederholen.
- Einstellungen: Fächer an/aus, Details unter „Genauer einstellen“. Neuer Erfolgs-Ton (Marimba).

**Lesen & Start**
- Lumi ist beim Lesen standardmäßig aus (einstellbar: Ruhig, Lebendig). Kein Wackeln am Abbrechen-Regler,
  ruhigere Strahlen (neuer Standard-Ring), Flamme still.
- Neue Timer: Klappuhr und Terminal. Start standardmäßig „Halten“ mit neuer Druck-Animation.
- Kreuz oben rechts: Sofort, Einmal fragen oder Streng. Beim Beenden kein Zurückspringen mehr.

**Rest**
- Neues App-Symbol (weißes Buch und Sonne auf Indigo), Indigo als Standardfarbe. Leichter
  Liquid-Glass-Rand an Leiste, Notiz-Leiste und Chips. Notizen öffnen sich auf der Startseite von oben.
- Einführung neu und robust (Notizen, Modi, 5 Minuten starten, Abbrechen, Kreuz) plus Widget-Frage.
- Hinweis „Animationen deaktiviert“ entfernt. Notiz-Leiste symmetrisch.
- Datenschutzerklärung und Nutzungsbedingungen in förmlicher Fassung; Cloud-Backup aus.

## Neu in 10.1 – Aufgaben-Algorithmus, 215 Aufgaben, neue Notizen, satte Haptik

**Aufgaben**
- Lektionen haben jetzt 3–4 zufällige Aufgaben (keine feste Reihenfolge mehr). Der Algorithmus
  (`Tasks.next`) gewichtet: Lieblingsthemen, Lieblingsarten, Neues zuerst (Verlauf bleibt über Neustarts
  erhalten, `TaskHistory`), Themenwechsel und eine sanfte Steigerung innerhalb der Lektion.
- 132 neue Aufgaben-Vorlagen (`ui/challenge/Lab*.kt`): jede bringt bei jedem Ziehen neue Zahlen.
  Wer auf einer Stufe viel richtig löst, bekommt größere, krummere Zahlen und mehr Größen (Stufe 0–3).
- Physik hat jetzt 19 Unterthemen (Bewegung, Kräfte, Energie, Impuls, Drehung, Gravitation, Schwingungen,
  Druck, Wärme, Schall, Optik, Stromkreise, Felder, Magnetismus, Relativität, Quanten, Kerne, Astro,
  Teilchen) mit Aufgaben auf allen Stufen bis Experte und Meister; dazu neue Analysis-Vorlagen.
- Im Fach oben rechts: Lieblings-Unterthemen (Aus · Normal · Mehr · Nur) und Aufgabenarten
  (Rechnen, Mehrere Schritte, Verstehen, Schätzen). Öffnet Einstellungen → Aufgaben.
- Tipp-Blase und Erklärung neu gestaltet (Ergebnis, Zeitleiste, "Worum es geht", Formeln, "Wusstest du?").

**Notizen**
- Neue Leiste unten: Form-Wahl links, großes Mikrofon in der Mitte, "Aufräumen" rechts; Meldungen als
  Karte mit Grund und "Rückgängig" (mit Zeitbalken).
- Jede Sprach-Animation entsteht anders aus dem hochfliegenden Mikrofon (Balken teilen sich, Kreis blüht
  auf, Raster spritzt auseinander, Kugel löst sich in Linien auf). "Linie" entfernt.
- Knöpfe der Sprach-Ansicht als 3D-Flächen (Papierkorb wackelt, Haken zeichnet sich, Ring atmet mit der Stimme).
- Notizen-Reiter: Suche, Filter (Alle · Beim Lesen · Diktiert), Karten mit Titel und Vorschau, Treffer markiert.

**Lumina-KI (Worker Version 3)**
- Behebt "Lumina-KI gerade nicht erreichbar": Cloudflare hat die gpt-oss-Schnittstelle geändert.
  Der neue Worker probiert mehrere Anfrageformen und Modelle, zählt erst nach Erfolg und hat `/health`.
- Stufen pro Tag: 3× beste KI (gpt-oss-120b), bis 8× Standard (gpt-oss-20b), bis 20× Spar-KI (Llama 3.2 3B).
- Einstellungen → Notizen → "Verbindung testen" zeigt pro Modell, ob es antwortet. Anleitung: `server/ANLEITUNG.md`.

**Start, Lumi, Widgets**
- Intro-Absturz behoben (Spotlight-Blocker mit riesigen Maßen). Abstürze werden jetzt protokolliert
  (Einstellungen → Über Lumina → Letzter Absturz).
- Start und Abbrechen sind jetzt Schieberegler mit Mulde, Magnet, Stups-Hinweis und satter Haptik
  (Rasten, Greifen, "Zünden" beim Start, dumpfer Stopp beim Abbrechen).
- Zeit-Ring: Rastpunkte an den wählbaren Werten, Zahl in Inter Display, darunter "bis 14:35".
- Lumi trägt weiße Cartoon-Handschuhe statt Kugeln (App und Widget-Bilder), keine Brille mehr,
  in Lektionen ohne Buch (neue Pose `Idle`), kein Lumi mehr auf dem Stufen-Pfad.
- Widgets: Balken zum Tagesziel statt "Noch 25 Min", Streak-Widget mit Abzeichen und Kapseln.
- Bildschirmzeit: Karte "Wofür" mit Anteilen der Gruppen.

## Neu in 10.0 – Aufgaben wie bei Brilliant, neues Intro, aufgeräumt

- **Aufgaben-Reiter komplett neu** (`ui/screens/PracticeTab.kt`, `ui/screens/TaskView.kt`, `ui/challenge/Practice.kt`):
  - Übersicht: oben „Weiter“ mit dem zuletzt geübten Fach, darunter alle Fächer als Bilder (`res/drawable/subj_*.xml`).
  - Fach: ein Pfad aus Stufen (Leicht bis Experte, bei Physik zusätzlich Meister). Lumi steht neben der gewählten Stufe, der Ring zeigt die geschafften Lektionen (3 = gemeistert).
  - **Lektion** mit 3 bis 5 Aufgaben: oben Kreuz, dicker Fortschritt und Ton an/aus, unten fest „Prüfen“.
  - Richtig: grüne Leiste mit Haken, Funken, „Erklärung“ und „Weiter“. Nicht ganz: weich rote Leiste mit „Nochmal“ und „Lösung“. Die Antwort bekommt einen grünen bzw. roten Rand und wackelt bei Fehlern.
  - Lumi gibt links neben „Prüfen“ die Tipps, die Lösung gibt es nach einem Fehlversuch oder 20 Sekunden.
  - Am Ende steht Lumi auf einem leuchtenden Sockel, bei einer perfekten Lektion dreht er sich.
  - Alle 83 Aufgaben sind geblieben. Die Hürde beim Abbrechen nutzt dieselbe Ansicht.
- **Töne** (`ui/Sounds.kt`, `res/raw/lumina_*.ogg`, selbst erzeugt mit `tools/sounds_synth.py`): richtig (zwei helle Töne), nicht ganz (weich und tief), Auswahl, Lektion geschafft. Abschaltbar in jeder Lektion und unter Einstellungen → Aufgaben.
- **Haptik** (`ui/Haptics.kt`):
  - Art „Präzise“ (Standard) nutzt die feinen Effekte guter Vibrationsmotoren.
  - Art „Kräftig“ spielt längere Impulse für einfache Motoren.
  - Neue Muster für richtig, falsch und geschafft. Die Stärke bleibt einstellbar.
- **3D-Knöpfe** (`ui/components/Lip.kt`): Hauptknöpfe, Antworten, Tasten und Auswahlen haben eine dunklere Kante und sinken beim Drücken darauf.
- **Neues Intro** (`ui/screens/Onboarding.kt`, `ui/components/Guide.kt`):
  - Ablauf: Lumi mit Brille (sein Licht flackert an), Erlaubnisse, Tagesziel, Streak-Ziel, dann „5 Minuten lesen“.
  - In dieser ersten Session zeigt Lumi mit Spotlight: Notizen (Mikrofon, Aufräumen), Abbrechen, Ausweg oben rechts und die Reiter.
  - Der alte Rundgang ist weg. Wiederholen geht unter Einstellungen → Über Lumina.
- **Aufgeräumt**:
  - Erklärtexte fast überall entfernt: Einstellungen, Aufgaben, Fortschritt, Ziele, Notizen.
  - Einstellungen neu sortiert (Design, Session, Sperre, Haptik, Benachrichtigungen, Notizen, Aufgaben …). Lumi schaltet man direkt in der Übersicht, die Diktier-Animation liegt jetzt unter Design.
  - Im Fortschritt stehen Rekord und Streak-Eis als eigene Kacheln. Der laufende Funke im Streak-Kalender ist weg.
- **Symbole** (Lucide, ISC-Lizenz, siehe `licenses/`): Leiste, Einstellungen und Aufgaben. Neues App-Symbol (Sonne hinter einem Buch auf Violett, mit Symbol für Android 13). Der Startbildschirm geht nahtlos in die App über.
- **Start-Knopf**: zeigt die Dauer („20 Min lesen“). Ab und zu gleitet ein feiner Glanz über die Schrift, bei „Weniger Bewegung“ nicht.

## Neu in 9.9 – Runden, Lumi als Helfer, Einführung

- **Aufgaben-Reiter neu** (`ui/screens/PracticeTab.kt`): Startseite mit Funken, Streak-Eis-Fortschritt, Stufe, einer gemischten Runde und den Fächern als Karten (mit „x von y gelöst“). Antippen startet eine **Runde**: Aufgaben, so viele man will, oben ein Kreuz zum Aufhören. Alle 5 Aufgaben feiert Lumi („Runde geschafft!“), am Ende kommt eine Zusammenfassung. Während der Runde gleitet die Leiste unten weg.
- **Lumi als Helfer**: Statt „Tipp“ sitzt Lumi neben der Aufgabe. Ein Tipp auf seine Sprechblase bringt die Kernidee, dann den Ansatz, zuletzt die Formel, und er freut sich kurz. Nach jeder gelösten Aufgabe springt Lumi kurz ins Bild (ohne Wartezeit). Ohne Lumi (Einstellung) bleibt die schlichte Tipp-Leiter.
- **Streak-Eis aus Aufgaben**: 15 gelöste Aufgaben (ohne angeschaute Lösung) geben ein Streak-Eis, solange der Vorrat nicht voll ist.
- **Zuteilung neu** (`Tasks.next`): streng auf der gewählten Stufe (keine Mittel- oder Experte-Aufgaben mehr in der falschen Stufe), ungelöste Aufgaben zuerst, dann die am längsten nicht gesehenen, nie zweimal dasselbe Thema hintereinander. Erzeugte Aufgaben nur noch bei Leicht spürbar, bei Experte gar nicht. Gelöste Aufgaben werden gespeichert.
- **33 neue Aufgaben** (`TaskBankMore.kt`), jetzt 83 insgesamt: Mathe (Zinseszins, Fernseher-Pythagoras, Ziegenproblem, Fibonacci, Bayes-Test, Geburtstagsparadox, Zaun-Optimierung, Euler-Formel, Gauß-Integral), Physik (Spiegel, Handy-Akku, Kamerablitz, Doppler, Myonen, Wasserstoff-Spektrum), Geschichte (Eratosthenes, Römerpost, Zeitstrahl Antike), Geologie (Tropfstein, Ötzi und C-14, Pangäa), Philosophie (Sandhaufen, Kant und die Lüge, Gehirn im Tank), Erdkunde (Horizont, Flug nach New York), Biologie, Chemie, Allgemeinwissen.
- **Keine grauen Felder mehr**: Antworten, Zahlenfeld und Schritte stehen als ruhige, leicht erhobene Flächen auf der Seite statt in grauen Kästen. Zahnrad wie in der Leiste.
- **Animationen**: „Weniger Bewegung“ heißt jetzt ruhiger statt gar nicht – Stufen-Umschalter und Leiste gleiten auch dann kurz. Ist im Handy „Animationen entfernen“ an, fragt Lumina einmal, ob trotzdem alle Animationen gezeigt werden sollen.
- **Diktieren**: Das Mikrofon fliegt beim Antippen in einem Bogen nach oben und verwandelt sich in die gewählte Sprach-Animation (Kugel bleibt rund, Balken/Linie werden breit), dann kommen Verwerfen, Pause und Fertig nacheinander von unten. Gut eine halbe Sekunde, Schließen läuft rückwärts.
- **Einführung mit Lumi** (`ui/components/Coach.kt`): Beim ersten Start ein Rundgang mit Spotlight und Sprechblase: Zeit-Rad (5 Minuten), Start-Knopf selbst halten, in der Session Abbrechen, Ausweg oben rechts, Notizen, danach die Reiter der Leiste. Wiederholbar unter Einstellungen → Über.

## Neu in 9.8 – Aufgaben 2.0, Notizen-Reiter

- **Neuer Aufgabenbereich** (`ui/challenge/Tasks.kt`, `ui/screens/StepTask.kt`): Aufgaben mit mehreren Schritten, jeder mit eigener Antwortart: Zahl mit Toleranz und Einheit (eigenes Zahlenfeld mit Komma, dazu ein Rechner mit √, Potenz, ln und π), Auswahl (gemischt) und Schätzen auf einem logarithmischen Regler. Erledigte Schritte schrumpfen zu einer Zeile mit Ergebnis, die kommenden stehen blass darunter.
- **Tipp-Leiter**: Kernidee → Ansatz → „Formel ansehen“ (für alle, die die Formel gerade nicht kennen). **Lösung erst nach einem Fehlversuch oder 20 Sekunden**, mit Countdown. Hilfreiche Rückmeldung bei falschen Zahlen (Vorzeichen, Einheit, km/h↔m/s, knapp daneben).
- **Nach dem Lösen**: Lösungsweg mit gesetzten Formeln, „Worum es geht“ (erklärt das Thema, nicht nur die Rechnung) und „Wusstest du?“. Dazu **Funken** als Belohnung (schwerer und mehr Schritte geben mehr, Tipps kosten, Lösung angeschaut = keine).
- **50 selbst geschriebene Aufgaben** (`TaskBank.kt` Physik/Mathe, `TaskBankWorld.kt` die anderen Fächer), Ergebnisse werden im Code berechnet. Von Leicht (Luft im Zimmer, Wippe, Gewitter) über Mittel (Flummi, Astronautin, Blitz, Looping) und Schwer (ISS, E-Scooter mit Integral, Hagel, Golf auf dem Mond, Kaffee) bis Experte (Stern-Gerlach-Spin, Quantenpunkt, GPS-Relativität, Neutronzerfall, Schwarzes Loch, Basel-Reihe).
- **Meisterklasse**: eigener Schalter unter „Experte“ (nicht im Stufen-Balken), Uni-Niveau: Bell/CHSH, Hawking-Strahlung, Feynman-Diagramme.
- **Neue Fächer**: Geschichte, Geologie, Philosophie, dazu Aufgaben für Erdkunde, Biologie, Chemie und Allgemeinwissen. In den Einstellungen pro Fach an- und abschaltbar (mit Anzahl der Aufgaben mit Erklärung).
- **Spannweite pro Stufe**: etwa jede fünfte Aufgabe kommt von der Stufe darunter oder darüber. Gibt es für die gewählten Fächer auf einer Stufe nichts, kommt die nächstgelegene Stufe.
- **Abbrechen-Hürde**: nur einschrittige Aufgaben (Rechnen, Schätzen, Auswahl, Lücke), im selben neuen Look.
- **Eigener Formelsatz** (`ui/components/MathText.kt`): Brüche übereinander, Wurzeln, Hoch-/Tiefstellung, Integrale und Summen mit Grenzen, griechische Buchstaben. Keine neue Bibliothek.
- **Notizen als eigener Reiter** (statt „Modi“): Suche, Teilen, Speichern, neue Notiz über den runden Knopf. Modi bleiben über die Startseite und die Leiste von links erreichbar.
- **Bewegung**: „Automatisch / Voll / Weniger“ unter Aussehen. „Automatisch“ folgt „Animationen entfernen“ im System, „Voll“ zeigt alle Übergänge trotzdem. Stufen-Umschalter mit mehr Schwung, Bildschirmzeit-Zahlen zählen beim Wechsel (Heute/Gestern/7 Tage) um.
- **Start- und Abbrechen-Knopf** ruhiger: neutrale weiche Schatten statt farbigem Leuchten und wandernder Lichtkante, keine harten Ränder, flache Spur beim Ziehen.
- **Streak-Kalender**: der helle Punkt unter dem heutigen Tag ist weg.

Aufgaben: alle selbst geschrieben. Lehrbücher wie Halliday/Resnick dienten nur als Vorbild für den Aufbau (mehrteilige Aufgaben mit Kernidee), es wurde nichts daraus übernommen.

## Neu in 9.7 – Design-Überarbeitung

Alle Einstellungen, Funktionen und Farbschemata bleiben erhalten. Neu ist das Fundament darunter:

- **Design Tokens** (`ui/theme/Tokens.kt`): Abstände im 4/8-dp-Raster (`Space.s1` = 4 dp … `Space.s30`), Radien und Formen (`Radius`, `Shapes`), Höhen mit dezenten Schatten statt Rändern (`Elevation.Level0–3`), Zustände (`StateAlpha`), Bewegung (`Motion`). Alle Abstände in den Bildschirmen laufen über diese Werte.
- **Farben nur noch aus Tokens**: neue Palette-Werte `success`, `warning`, `divider`, `scrim`, `accentSoft`, `track`, `raised(level)` und `tame()` (dämpft Fach- und Gruppenfarben, damit nichts bunt nebeneinandersteht). Lumi, Flamme und Marke haben eigene Tokens (`ui/theme/ArtColors.kt`). Akzent-Verläufe bleiben im selben Farbton.
- **Schrift Inter** (Regular, Medium, SemiBold; Display-Variante für Überschriften) mit gleich breiten Ziffern überall. Lizenz: SIL OFL, `licenses/Inter-OFL.txt` und in der App unter Über → Quellen & Lizenzen.
- **Zustände für jede Fläche**: Gedrückt (kleiner + Zustands-Schicht), Hovered, Fokussiert, Ausgeschaltet, Aktiv. Neue Bausteine in `ui/components/Kit.kt`: `Card`, `ExpandableCard`, `Chip`, `IconButton`, `BackButton`, `TextAction`, `ProgressBar`, `Stat`, `SectionHeader`.
- **Übergänge nach transitions.dev** (`Modal.kt`, `Transitions.kt`): Dialog öffnen/schließen mit Größe und Überblenden (die Aktion läuft erst, wenn der Dialog zu ist), weiches Mitwachsen von Karten, Akkordeon mit drehendem Pfeil, Erfolgs-Haken mit Unschärfe, Drehung und gezeichneter Linie, Ziffern, die einzeln überblenden, Text-Wechsel mit Unschärfe, Fehler-Wackeln.
- **Bewegung nach Emil Kowalski**: Ease-out-Kurven statt linear, 150–300 ms, Schließen schneller als Öffnen, keine wackeligen Federn mehr. **Weniger Bewegung** folgt der Systemeinstellung „Animationen entfernen“ und lässt sich unter Aussehen auch selbst einschalten: dann nur noch kurze Überblendungen, dekorative Endlos-Animationen stehen still (Timer, Halte-Knöpfe und Sprach-Pegel laufen normal).
- **Bildschirmzeit** neu: ruhige Übersicht, ausklappbare Abschnitte (Woche, Kennzahlen, Top-Apps), neue 7-Tage-Grafik mit einfarbigen Balken zum Antippen.
- **Aufgaben** entrümpelt: Stufe nur noch oben, Fach als ruhige Zeile, flache Zahlentasten, „Prüfen“ als einzige Hauptaktion.
- **Navigationsleiste**, Dialoge, Einstellungen, Fortschritt und Notizen im selben ruhigen Stil; Hintergrundmuster standardmäßig aus und zarter.

## Neu in 9.6

* **Diktieren:** Das Mikrofon hat einen Licht-Ring, der zum Sprechen einlädt; die Sprach-Ansicht wächst als Kreis
  aus dem Knopf heraus. Unten jetzt **Verwerfen · Pause/Weiter · Fertig** als runde Knöpfe.
* **KI-Aufräumen neu:** Wahl zwischen **Text** (sauberer Fließtext mit Absätzen) und **Stichpunkte** (echte, kurze
  Stichpunkte) direkt über dem Knopf und in den Einstellungen. Neue, genaue Anweisung an die KI im Worker
  (Rechtschreibung, Versprecher, Doppeltes, falsch erkannte Wörter, Formeln) mit Beispielen → `server/` neu deployen.
  Beim Aufräumen sitzt **Lumi** in der Mitte und sagt, was er gerade macht.
* **Kein eigener Schlüssel mehr:** nur noch Lumina-KI an/aus. Ein alter gespeicherter Schlüssel wird gelöscht.
* **Notiz-Papier wählbar:** Schlicht, Linien, Heft, Kästchen, Punkte (Einstellungen → Notizen & KI).
* **Einstellungen übersichtlich:** Liste in vier Gruppen (Aussehen, Lesen & Fokus, Lernen, Allgemein), jede Zeile mit
  kurzer Beschreibung statt kryptischer Werte. Die Live-Vorschau ist jetzt unter „Aussehen“.
* **Rechtliches:** Datenschutzerklärung, Nutzungsbedingungen und Anbieter/Kontakt in der App (Entwurf mit Platzhaltern
  in `ui/screens/Legal.kt`), Links in der Einrichtung und im KI-Zustimmungsdialog.
* **Streak-Kalender:** Kometen weg. Geschaffte Tage hängen als Spur zusammen, ein Funke läuft Tag für Tag
  entlang der Wochen bis zum Ziel.

## Neu in 9.5

* **Notiz-Editor neu:** liniertes Blatt mit Rand (passt sich Modern/Glass/Cartoon/Flat an), App-Hintergrund statt Schwarz,
  Diktiersprache oben rechts. Unten links ein **KI-Knopf im Glas-Look** (nach der Glass-Button-Idee von 21st.dev):
  wandernde Lichtpunkte am Rand, Lichtstreifen, farbiger Schein, kippt beim Drücken. Rechts das Mikrofon.
* **Sprach-Ansicht wie ein Sprachassistent:** Antippen des Mikrofons öffnet eine große Ansicht mit der Animation
  in der Mitte, darunter groß, was gerade verstanden wird; „Fertig“ und „Verwerfen“. Das KI-Aufräumen läuft in
  derselben Ansicht (Verbinden/Denken, abbrechbar).
* **Bessere Spracherkennung:** zuerst der normale Erkennungsdienst (mit Internet genauer), das Sprachpaket auf dem
  Handy nur ohne Netz. Ab Android 13 Satzzeichen vom Dienst und bevorzugte Mathe-Wörter (Integral, Quadrat, Wurzel …).
* **Notiz-Knopf in der Session:** Glas wie die Erlaubt-Leiste, Notizblock mit kleinem Akzent-Stift.

## Neu in 9.4

* **Lumina-KI:** Notizen werden gratis und ohne Anmeldung mit KI aufgeräumt – über einen eigenen kleinen
  Cloudflare Worker (`server/`). Pro Handy und Tag 3 Notizen mit gpt-oss-120b, bis 10 mit gpt-oss-20b,
  danach automatisch auf dem Handy. Kostenbremse im Worker, im Gratis-Plan nie eine Rechnung.
* Einmalige Zustimmung vor dem ersten Senden; jederzeit abschaltbar (Einstellungen → Notizen).
* Einstellungen → Notizen: Aus / Lumina-KI / Eigener Key (OpenAI). Groq entfernt. Eigene Server-Adresse möglich.
* `server/ANLEITUNG.md` erklärt den Worker, `RECHTLICHES.md` fasst Datenschutz, Anbieter und Lizenz zusammen.

## Neu in 9.3

* **Lumi-Widget ohne Fließtext:** nur noch eine kurze, große Überschrift und ein Knopf. Jeder Satz hat seinen
  eigenen, passenden Knopf (kein „Streak retten“ mehr, wenn der Streak schon gerettet ist). Antippen öffnet den
  passenden Reiter (z. B. Aufgaben bei „Lust zu knobeln?“).
* **Countdown:** Ist der Streak in Gefahr und kein Eis mehr da, zeigt das Widget ab 20 Uhr groß „4 Std.“, „3 Std.“ …
  bis dein Streak erlischt; in der letzten Stunde läuft ein echter Countdown (mm:ss) mit „Nein, nicht!“.
  Das Widget wird dafür zur vollen Stunde neu gezeichnet.
* **Große Streak-Zahl** bei Erfolgen („9 Tage – Du hast einen Lauf!“).
* **Lumi im Eis:** Hat gestern Eis den Streak gerettet, steckt Lumi heute im Eisblock, das Licht fast aus
  („Hilf mir!“ → „Lumi retten“). Nach dem Lesen: „Aufgetaut!“, Lumi guckt aus dem Eis.
* **Rettungs-Eis:** Lief der Streak länger als 5 Tage, ist kein Eis mehr da und wurde genau ein Tag verpasst,
  schenkt Lumi einmal ein Eis (höchstens einmal in 30 Tagen) und sagt es beim nächsten Öffnen.
* **Stimmungen im Widget:** warm, kühl (Eis), Alarm (Countdown, roter Knopf), Nacht. Schmale hohe Widgets (2x3)
  bekommen einen kleineren Knopf.
* **KI-Aufräumen:** Mistral entfernt (API seit August 2026 nur noch für Firmen), dafür **Groq** mit kostenlosem
  Schlüssel (Modell gpt-oss-120b). OpenAI bleibt.

## Neu in 9.2

* **Notizen im Vollbild:** großer Notizblock-Knopf rechts neben der kompakten „Erlaubt“-Leiste (antippen zum Aufklappen).
  Normales Tippen, Mikro unten rechts, Sprach-Animation beim Diktieren. Text kommt erst roh rein, **„Aufräumen“** macht
  daraus Stichpunkte. Gesprochene Formeln werden umgesetzt („2 Quadrat“ → 2², „Integral von 0 bis 1“ → ∫₀¹, „Wurzel aus 2“ → √2).
  Speichert automatisch. Zu finden unter Fortschritt → Notizen und Einstellungen → Notizen, dort auch **„Als Datei speichern“** (.md).
* **Diktiersprache** unabhängig von der App-Sprache (Einstellungen → Notizen und Chip im Editor).
* **6 Sprach-Animationen** (Balken, Kreis, Raster, Linie, Punkte, Kugel), nach chatcn (MIT) – Standard: Balken.
* **KI-Aufräumen (optional):** OpenAI oder Mistral mit eigenem Schlüssel (Einstellungen → Notizen). Ohne Schlüssel räumt Lumina lokal auf.
* **Beenden:** „Trotzdem beenden“ springt an eine zufällige Stelle, kleiner Hinweistext weg, „Streng beim Kreuz“ abschaltbar.
* **Zeitrad:** genau eine Runde, kleine Zeiten bekommen mehr Platz, bis 240 Minuten.
* **Streak-Ziel:** 7 / 14 / 30 / 60 Tage, Wochenkalender (Mo–So) bis zum Ziel mit kleinen Kometen, auch im Fortschritt.
  Start mit 1 Streak-Eis, das zweite verdient man sich.
* **Bildschirmzeit-Hinweis:** Lumi meldet sich, wenn heute viel Video/Social-Zeit zusammenkommt (Grenze und Uhrzeit einstellbar).
* **Lumi-Widget:** große Schrift, die mit dem Widget mitwächst, Knopf „Jetzt lesen →“, Hintergrund nach Stimmung,
  neue große Fassung (4x3 / 2x3), neues Bild „Lumi guckt aus dem Eisblock“. Zeile „3 Tage · 3/10 Min“ entfernt.
* **Sprache:** beim ersten Start wie das Handy, Sprachwahl direkt im Willkommens-Schritt.

## Neu in 9.1

* **Einrichtung neu** (kommt nach dem Update einmal): Tagesziel als Stufe (Einstieg 10 / Solide 20 / Stark 30 –
  empfohlen / Extrem 60 Min, mit „≈ Stunden im Jahr“, Lumi freut sich mehr, je höher), **Streak-Ziel**
  (1 Woche bis 100 Tage, Punkte-Kalender mit „noch X Tage“ und Zieldatum), Berechtigungen, **Widget direkt hinzufügen**.
* **Streak-Ziel** im Fortschritt (Punkte, Zieldatum, „Nächstes Ziel“), im Streak-Widget („von 14 Tagen“) und bei Lumi.
* **Benachrichtigungen** als eigene Einstellungsseite: Tagesziel-Erinnerung und Streak-Warnung einzeln an/aus,
  Uhrzeit in 15-Minuten-Schritten, Erlaubnis direkt anfragen, Probe-Benachrichtigung.
* **Notizen beim Lesen:** Stift in der Session → ruhiges Notizblatt, tippen oder diktieren (Spracherkennung des Handys,
  keine KI/kein API-Schlüssel). Leuchtkugel im Takt der Stimme, „äh/ähm/quasi“ fliegen automatisch raus.
  Alle Notizen im Fortschritt (bearbeiten, löschen, teilen), Hinweis auf dem Ergebnis-Bildschirm.
* **Aufgaben-Reiter aufgeräumt:** direkt lösen, oben nur ƒ (Formelsammlung) und Zahnrad (Aufgaben-Einstellungen),
  kein Rätsel des Tages, keine Fach-Karten. Kleiner **Rechner** (+ − × ÷, Klammern) neben dem Zahlenfeld.
  Option **Steigern**: sauber gelöst → nächste Aufgabe eine Stufe schwerer. Weniger Wiederholungen (merkt sich die letzten 40 Arten).
* **Widgets:** neue Lumi-Bilder „Aufgetaut“ (nach Streak-Eis) und Blitze für „Du hast einen Lauf“.
* **Englisch:** Zahlen auf deutschen Handys mit Punkt statt Komma, ÷ statt „:“ in Aufgaben, holprige Sätze geglättet.

## Neu in 9.0

* **Zählregeln:** Session ganz durchgezogen → Minuten + Streak. Mit gelöster Aufgabe früher beendet → nur die Minuten.
  Über das Kreuz beendet → zählt gar nicht.
* **Streak-Eis:** alle 4 Tage am Stück gibt es ein Eis (höchstens 2). Verpasst man einen Tag, rettet es den Streak automatisch.
* **Abbrechen mit schlechtem Gewissen:** nach „Trotzdem beenden“ kommen zwei große Screens („Bist du wirklich sicher?“,
  „Willst du dir nichts Gutes tun?“) mit Lumi in Glubschaugen. Der große Knopf heißt immer „Weiterlesen“.
  Weinen jetzt mit großen nassen Augen statt grau und zu.
* **Start:** Der „Halten zum Starten“-Knopf blendet beim Runterscrollen ganz aus.
* **Einrichtung beim ersten Start:** Begrüßung, Tagesziel als Stufe (Locker 10 / Normal 20 / Ehrgeizig 30 / Extrem 60 Min),
  Berechtigungen direkt in der App (Benachrichtigungen, Nutzungsdaten, Sperre). Später erneut: Einstellungen → Über.
* **Erinnerungen:** abends zur gewählten Uhrzeit, wenn das Tagesziel fehlt, und um 21:30 „Streak in Gefahr“
  (Einstellungen → Lumi). Kommt nicht, wenn schon geschafft oder gerade eine Session läuft.
* **Widgets:** Lumi (16 Bilder für verschiedene Lagen, Sätze wechseln alle 30 Minuten), Streak, Letzte 7 Tage, Monat.
  Antippen öffnet Lumina (Fortschritts-Widgets direkt im Reiter Fortschritt).
* **Bildschirmzeit:** zählt jetzt pro Fenster (Prime Video & Co. wurden vorher zu wenig gezählt), schlichte einfarbige
  Symbole, Hörbücher (Audible) bei Musik & Podcasts, 14-Tage-Verlauf als Diagramm, Vergleich mit der Vorwoche, Top-Apps.
* **Modern-Design:** Karten deckend statt Glas.
* **Aufgaben:** neue Aufgabensammlung mit festen, ausgefallenen Aufgaben (mehrere Schritte, Zahlen wechseln jedes Mal),
  nach Können sortiert, neue Stufe **Expert**. Reiter „Aufgaben“ neu: Rätsel des Tages, Fächer als Karten, ohne Statistik oben.

## Neu in 8.4

* **Lumi B (Kulleraugen):** größere, tiefere Augen mit lila Schimmer, kleiner Mund, größere Bäckchen.
  Neue Gesichter beim Antippen: kichern (> <, Katzenmund), verlegen (Rotschimmer), staunen mit Funkeln.
  Bitten, Erschrecken, Traurig und Weinen im neuen Stil.
* **Glühdraht** mit Glasfuß wie bei einer echten Birne, vier Stile zur Wahl (Einstellungen → Lumi):
  Spirale (Standard), Klassisch, Wendel, Herz.
* **Neuer Reiter „Apps“:** Bildschirmzeit nach Gruppen (Videos, Social Media, Chats, Spiele …), Vergleich mit der
  konzentrierten Lesezeit, Heute / Gestern / 7 Tage. Apps werden automatisch einsortiert, lassen sich verschieben,
  Gruppen umbenennen und eigene Gruppen anlegen. Braucht einmal „Nutzungsdatenzugriff“.
* **Neuer Reiter „Aufgaben“:** üben ohne Session, mit Statistik (heute, Serie, gesamt), Fach-Auswahl und Formelsammlung.
* **Aufgaben neu gestaltet:** eigenes Zahlenfeld, Formeln groß hervorgehoben, Tipp-Knopf (Formel bzw. 50:50),
  Haken bei richtiger Antwort, Lösungsweg als Zeitleiste: Schritte mit ƒ antippen zeigt die Formelkarte.
* **Neue Fächer:** Physik in fünf Bereichen (Bewegung, Energie, Strom, Dichte/Druck/Wärme, Wellen/Optik),
  Logik, Erdkunde, Chemie, Biologie, Spaß-Fakten. Standard: Mathe und Physik. Alles selbst erstellt.

## Neu in 8.3

* Lumis Glaskörper direkt am Referenzfoto abgemessen (tools/body11.py): breiter Hals, der über der Fassung
  etwas nach außen geht und sich dann in die Kugel weitet. Keine Taille mehr.
* Streak überall nur noch als Flamme (Startseite, Fortschritt, Ergebnis).
* Startseite: nach unten wischen zeigt nur die Modi.
* Start-Knopf mit wanderndem Lichtrand auch im Modern-Stil; Modus- und Streak-Chip ohne Animation.
* Hell/Dunkel/System als kleiner Symbol-Knopf (Sonne, Mond, Bildschirm), das Symbol dreht sich beim Wechsel herein.
* Farbschemata aufgeräumt (Pergament und doppelte Farben entfernt, alte Auswahl wird automatisch ersetzt).

## Neu in 8.2

* Lumi schwebt (sanftes Auf und Ab, leichtes Wiegen), neue Glühbirnen-Form mit Taille und spitzem Kontakt.
* Sechs zufällige Reaktionen beim Antippen. Beim Abbrechen flackert sein Licht und geht aus, der Glühdraht wird sichtbar.
* Abgebrochene Sessions zählen nicht (keine Minuten, kein Streak).
* Neuer Stil **Glass**, natürlichere Schatten im Modern-Stil, Hell/Dunkel/System-Schalter.
* Startseite: Kopfzeile bleibt stehen, beim Runterwischen kommen die Modi und der Überblick. Streak mit Flamme.
* Aufgaben: 8 Themen, 3 Schwierigkeiten, Lösungsweg + ähnliche neue Aufgabe. Textaufgaben aus GSM8K (MIT, siehe THIRD_PARTY_NOTICES.md).

