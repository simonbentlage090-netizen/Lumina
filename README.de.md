# Lumina

**Eine ruhige Fokus-App für Android.** Lumina hilft dir, das Handy eine Weile wegzulegen: Starte eine Fokuszeit, und ablenkende Apps bleiben gesperrt, bis du fertig bist. Lies dabei deine eigenen Bücher, schreib Notizen oder denk über eine kleine Aufgabe nach. Lumi, eine kleine Glühbirne, leistet dir Gesellschaft.

Kein Konto, keine Werbung, kein Tracking. Alles bleibt auf deinem Handy.

[English version](README.md)

## Funktionen

**Fokuszeiten**
- Dauer wählen, Knopf gedrückt halten, los. Apps, die du nicht erlaubt hast, sind gesperrt, solange du dich konzentrierst.
- Modi wie „Tiefenfokus“, jeder mit eigener Liste erlaubter Apps.
- Ein Tagesziel und ein Streak, die motivieren statt zu drängen.

**Denkpause**
- Bevor eine App aufgeht, die du weniger nutzen willst, stellt Lumi eine kurze Frage. Ein Moment Pause statt Scrollen.

**Eigene Bücher**
- PDF und EPUB Seite für Seite lesen, ohne Ablenkung.
- Inhaltsverzeichnis mit Kapiteln und Unterkapiteln, direkt aus dem Buch.
- Lesezeichen, Notizen auf jeder Seite (getippt oder diktiert) und ein Rechner neben der Seite.

**Notizen**
- Notizen tippen oder diktieren. Auf Wunsch räumt Lumi sie zu sauberem Text oder Stichpunkten auf.

**Außerdem**
- Kleine Denkaufgaben in Mathe, Physik, Logik und mehr, in drei Stufen.
- Fortschritt mit Bildschirmzeit, Erinnerungen und Widgets für den Startbildschirm.
- Vier Stile (Glass, Modern, Flat, Cartoon), hell und dunkel, auf Deutsch und Englisch.

## Herunterladen

- **GitHub:** Die neueste APK liegt unter [Releases](../../releases).
- **F-Droid:** eingereicht, kommt bald.
- **Google Play:** geplant.

Benötigt Android 8.0 oder neuer.

## Datenschutz und Berechtigungen

Lumina funktioniert offline. Bücher, Notizen und Statistiken werden nur auf deinem Gerät gespeichert.

| Berechtigung | Wofür |
|---|---|
| Bedienungshilfe „Lumina Sperre“ | Nur um zu sehen, welche App gerade vorne ist: zum Sperren während einer Fokuszeit, für die Denkpause und für Vollbild-Erinnerungen. Lumina liest keine Bildschirminhalte und keine Eingaben. Vor dem Einschalten siehst du eine klare Erklärung und stimmst zu. |
| Nutzungsdatenzugriff | Um zu zeigen, wie lange du welche App genutzt hast (Bildschirmzeit). |
| Benachrichtigungen, exakte Wecker | Für Erinnerungen, die du selbst einstellst. |
| Mikrofon | Nur während du eine Notiz diktierst. Die Spracherkennung übernimmt der Sprachdienst deines Handys. |
| Internet | Nur für die freiwillige Lumina-KI (siehe unten). |

**Freiwillige Lumina-KI.** „Aufräumen“ bei Notizen und „Kapitel finden“ bei Büchern können einen kleinen Server nutzen (einen Cloudflare Worker, Quellcode in [`server/`](server)). Er ist aus, bis du in der App zustimmst. Gesendet wird nur der Text für diese eine Anfrage. Der Text wird nicht gespeichert; der Server zählt nur Anfragen pro Tag, um im kostenlosen Rahmen zu bleiben. Ohne KI räumt Lumina Notizen auf und findet Kapitel direkt auf dem Gerät.

Datenschutzerklärung und Nutzungsbedingungen stehen in der App unter Einstellungen → Rechtliches.

## Selbst bauen

1. [Android Studio](https://developer.android.com/studio) installieren.
2. Dieses Repository klonen und den Ordner in Android Studio öffnen.
3. Gradle-Sync abwarten, dann auf Run drücken oder in der Kommandozeile bauen:

```
./gradlew assembleRelease
```

Technik: Kotlin, Jetpack Compose, Material 3. Nur AndroidX-Bibliotheken, keine Google Play Services, keine Analyse.

## Feedback

Fehler gefunden oder eine Idee? Gern ein [Issue](../../issues) öffnen oder an s.bentlage.lumina@gmail.com schreiben.

Versionsverlauf: [CHANGELOG.md](CHANGELOG.md).

## Lizenz

Lumina ist freie Software unter der [GNU General Public License v3.0](LICENSE).

Fremdinhalte (Schriften Inter, Nunito und Fredoka, Lucide-Symbole, GSM8K-Textaufgaben u. a.) stehen in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) und in der App unter Einstellungen → Über Lumina → Quellen & Lizenzen.
