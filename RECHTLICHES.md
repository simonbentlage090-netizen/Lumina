# Datenschutz, Nutzungsbedingungen, Anbieter & Lizenz

Die Texte stehen direkt in der App: `app/src/main/java/de/funke/app/ui/screens/Legal.kt`
(Einstellungen → Rechtliches). Förmliche Fassung (Art. 13 DSGVO, Haftung nach § 521 BGB), Stand 15.2 (24.11.2026).
Seit 13.0 endgültig, ohne „Entwurf“. 13.3 ergänzte exakte Wecker, die Bitte um Ausnahme vom Energiesparen und das
Nutzungslimit. 13.4: Erinnerungen nur noch als Mitteilung oder Bildschirm (4 b), Bücher im App-Speicher (3),
„Kapitel finden“ mit der Lumina-KI (6 a, b, d) und eigene Bücher in den Nutzungsbedingungen (§ 5 Abs. 2 und 4).
14.0: Notizen im Buch speichern Buchtitel, Seite und Stelle (3), Erinnerungen heißen „Pop-up“ und „Vollbild“ und merken
sich, wann sie zuletzt kamen (4 b), Unterkapitel werden nur auf dem Gerät im PDF-Text gesucht (6 a), „Fokuszeit“
statt „Lese-Sitzung“ (4 a, § 1 der Nutzungsbedingungen). Keine neue Übermittlung, keine neue Berechtigung.
15.0: Ein im PDF eingebautes Inhaltsverzeichnis liest Lumina nur auf dem Gerät; dann wird für „Kapitel finden“ nichts
übermittelt (6 a). Keine neue Übermittlung, keine neue Berechtigung.
15.2: „Kapitel finden“ mit der KI höchstens für drei Bücher in sieben Tagen; sonst (oder ohne KI, oder offline) erkennt
Lumina Kapitel nur auf dem Gerät aus dem Text (6 a). Weniger Übermittlung, keine neue Berechtigung.

## Stand 13.0

- **Anbieter:** Simon Bentlage (Privatperson), E-Mail `s.bentlage.lumina@gmail.com` (`LegalTexts.CONTACT`).
  Eine Anschrift ist bewusst nicht eingetragen (Entscheidung des Entwicklers). Wer später doch eine eintragen will:
  `LegalTexts.ADDRESS`, eine Zeile pro Eintrag; sie erscheint dann automatisch unter „Impressum & Kontakt“.
- **Lizenz:** GNU General Public License, Version 3 (GPL-3.0). Der vollständige Text liegt als `LICENSE` im
  Hauptordner des Projekts. In der App steht die Lizenz unter „Impressum & Kontakt“ und in den Nutzungsbedingungen (§ 2).
- **Datenschutzerklärung** deckt alle Funktionen ab: Sperre, Denkpause, Erinnerung beim Entsperren, Bildschirmzeit,
  Benachrichtigungen, Widgets, Diktieren und das freiwillige „Aufräumen mit Lumi“.

## Noch offen (optional)

1. ~~Adresse des GitHub-Repositorys~~ erledigt in 15.7: `LegalTexts.SOURCE` = `https://github.com/simonbentlage090-netizen/Lumina`.
2. Im Cloudflare-Dashboard beim Worker unter Einstellungen → Observability „Logs speichern“ ausschalten,
   damit dort keine Anfragen mitprotokolliert werden.

## Bei jeder neuen Version

Prüfen, ob eine neue Datenverarbeitung, Berechtigung oder Netzwerkverbindung dazukommt. Wenn ja: Text in
`Legal.kt` anpassen und `LegalTexts.UPDATED` auf das neue Datum setzen.

## Falls die App später in den Google Play Store soll

Play verlangt für Bedienungshilfen, die nicht der Barrierefreiheit dienen, vor dem Einschalten einen gut sichtbaren
Hinweis mit Zustimmung und eine Erklärung im Play-Formular. F-Droid und GitHub verlangen das nicht.

Keine Rechtsberatung.
