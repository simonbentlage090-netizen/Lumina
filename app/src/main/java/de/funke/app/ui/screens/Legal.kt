package de.funke.app.ui.screens

import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.funke.app.ui.components.CloseIcon
import de.funke.app.ui.components.glass
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr

/*
 * Rechtliches: Datenschutzerklärung (Art. 13 DSGVO), Nutzungsbedingungen, Impressum & Kontakt.
 * Maßgeblich ist die deutsche Fassung; die englische ist eine Übersetzung.
 *
 * Seit 13.0 endgültig: Anbieter mit Name und E-Mail (eine Anschrift trägt der Entwickler bewusst nicht ein),
 * Lizenz GPL-3.0 (Datei LICENSE im Projekt). Sobald es das Repository gibt, kann [SOURCE] dessen Adresse bekommen.
 * Bei jeder Version prüfen: Kommt eine neue Datenverarbeitung dazu? Dann Text und [UPDATED] anpassen.
 * Keine Rechtsberatung.
 */

enum class LegalDoc { Privacy, Terms, Provider }

object LegalTexts {
    /** Verantwortlicher im Sinne der DSGVO und Anbieter (Privatperson, nicht gewerblich). */
    const val OWNER = "Simon Bentlage"

    /** Kontakt für Datenschutz, Impressum und Fragen (eigene Adresse nur für Lumina). */
    const val CONTACT = "s.bentlage.lumina@gmail.com"

    /** Anschrift, eine Zeile pro Eintrag (optional; leer = ohne Anschrift). */
    val ADDRESS: List<String> = emptyList()

    /** Adresse des Quellcode-Repositorys, z. B. "https://github.com/name/lumina" (leer = ohne Link). */
    const val SOURCE = "https://github.com/simonbentlage090-netizen/Lumina"

    /** Lizenz der App (Volltext in der Datei LICENSE). */
    const val LICENSE_DE = "GNU General Public License, Version 3 (GPL-3.0)"
    const val LICENSE_EN = "GNU General Public License, version 3 (GPL-3.0)"

    const val UPDATED = "24.11.2026"

    fun title(doc: LegalDoc) = when (doc) {
        LegalDoc.Privacy -> tr("Datenschutzerklärung", "Privacy policy")
        LegalDoc.Terms -> tr("Nutzungsbedingungen", "Terms of use")
        LegalDoc.Provider -> tr("Impressum & Kontakt", "Legal notice & contact")
    }

    private val addressLines: String get() = if (ADDRESS.isEmpty()) "" else "\n" + ADDRESS.joinToString("\n")
    private val sourceDe: String get() = if (SOURCE.isBlank()) "im öffentlichen Repository von Lumina auf GitHub" else "unter $SOURCE"
    private val sourceEn: String get() = if (SOURCE.isBlank()) "in Lumina's public repository on GitHub" else "at $SOURCE"

    fun text(doc: LegalDoc): String = when (doc) {
        LegalDoc.Privacy -> tr(PRIVACY_DE, PRIVACY_EN)
        LegalDoc.Terms -> tr(TERMS_DE, TERMS_EN)
        LegalDoc.Provider -> tr(
            "## Anbieter\n$OWNER (Privatperson)$addressLines\n\n" +
                "## Kontakt\nE-Mail: $CONTACT\n\n" +
                "## Hinweis\nLumina ist ein privates, nicht gewerbliches Open-Source-Projekt. Die App ist kostenlos, enthält keine Werbung und erzielt keine Einnahmen.\n\n" +
                "## Quellcode und Lizenz\nLumina ist freie Software unter der $LICENSE_DE. Der vollständige Quellcode und der Lizenztext sind $sourceDe abrufbar.\n\n" +
                "## Fehler und Fragen\nFehlermeldungen, Vorschläge und Fragen zum Datenschutz bitte an die oben genannte E-Mail-Adresse oder über die Fehlerverfolgung des Repositorys.",
            "## Provider\n$OWNER (private individual)$addressLines\n\n" +
                "## Contact\nE-mail: $CONTACT\n\n" +
                "## Note\nLumina is a private, non-commercial open-source project. The app is free of charge, contains no advertising and generates no income.\n\n" +
                "## Source code and licence\nLumina is free software under the $LICENSE_EN. The complete source code and the licence text are available $sourceEn.\n\n" +
                "## Bugs and questions\nPlease send bug reports, suggestions and data protection questions to the e-mail address above or via the repository's issue tracker.",
        )
    }

    private val PRIVACY_DE = """
## Kurzfassung
Lumina verfügt über keine Nutzerkonten, keine Werbung, keine Analyse- oder Tracking-Werkzeuge und keine eigenen Server für Ihre Daten. Sämtliche Inhalte und Einstellungen verbleiben auf Ihrem Gerät. Eine Übermittlung an Dritte findet ausschließlich statt, wenn Sie die optionale Lumina-KI („Aufräumen mit Lumi“ und „Kapitel finden“) ausdrücklich einschalten und nutzen (Ziffer 6) oder die Spracheingabe Ihres Geräts verwenden (Ziffer 5).

## 1. Verantwortlicher
Verantwortlicher im Sinne von Art. 4 Nr. 7 der Datenschutz-Grundverordnung (DSGVO) ist:
$OWNER (Privatperson)
E-Mail: $CONTACT
Ein Datenschutzbeauftragter ist nicht bestellt; eine gesetzliche Pflicht hierzu besteht nicht.

## 2. Geltungsbereich
Diese Erklärung gilt für die App „Lumina“ in der Fassung, in der sie über das Quellcode-Repository oder einen App-Katalog (z. B. F-Droid) bereitgestellt wird. Für Vertriebsplattformen, über die Sie die App beziehen, gelten deren eigene Datenschutzbestimmungen; auf die dortige Verarbeitung hat der Verantwortliche keinen Einfluss.

## 3. Lokale Speicherung auf Ihrem Gerät
Fokuszeiten (auch die Zeit in Büchern), Sitzungen, Streak, Ziele, Modi, Notizen (bei Notizen in einem Buch mit Buchtitel, Seite und Stelle), Aufgaben-Fortschritt, die Einstellungen der Denkpause (ausgewählte Apps, Prüfzeiten) und ihre Zähler, die Einstellungen der Erinnerungen (einschließlich der Titel und Texte selbst angelegter Erinnerungen und der gewählten Apps und Gruppen für das Nutzungslimit) die von Ihnen hinzugefügten Bücher (als Kopie der gewählten Datei) mit Leseposition, Lesezeichen, Kapiteln und Lesezeit sowie sämtliche übrigen Einstellungen werden ausschließlich im geschützten App-Speicher Ihres Geräts abgelegt. Der Verantwortliche hat auf diese Daten keinen Zugriff; sie werden weder übermittelt noch ausgewertet. Mit der Deinstallation der App oder dem Löschen der App-Daten in den Android-Einstellungen werden sie vollständig entfernt. Die automatische Android-Datensicherung in die Cloud ist für Lumina deaktiviert.

## 4. Berechtigungen und Gerätefunktionen
a) Bedienungshilfe (Sperre, Denkpause, Erinnerungen): Ist die Bedienungshilfe aktiviert, ermittelt Lumina, welche App sich im Vordergrund befindet. Dies dient ausschließlich dazu, während einer Fokuszeit nicht freigegebene Apps zu sperren und beim Öffnen von Ihnen ausgewählter Apps ab den von Ihnen gewählten Prüfzeiten eine Denkpause mit einer Frage anzuzeigen. Bildschirminhalte, Eingaben oder Passwörter werden nicht ausgelesen. Es werden lediglich Paketname und Zeitpunkt auf dem Gerät verarbeitet und nur als Zähler (etwa „heute widerstanden“) gespeichert.
b) Erinnerungen im „Vollbild“: Solange die Bedienungshilfe läuft, empfängt Lumina die Systemmeldung, dass das Gerät entsperrt wurde, und kann eine fällige Erinnerung im Vollbild in der App zeigen; benutzen Sie das Gerät gerade, holt Lumina sich dafür kurz nach vorn. Ob eine Erinnerung fällig ist, prüft Lumina auf dem Gerät anhand der Uhrzeit, der gewählten Wochentage und Ihrer heutigen Fokuszeit. Gespeichert werden nur noch nicht gezeigte Erinnerungen bis zum nächsten Entsperren (höchstens sechs Stunden), Ihre Auswahl („In 15 Minuten“) und je Erinnerung, wann und wie sie zuletzt kam (zur Anzeige im Editor). Sie wählen für jede Erinnerung selbst, wie sie kommt, und können sie im Reiter „Denkpause“ jederzeit ausschalten oder löschen.
c) Nutzungszugriff (Bildschirmzeit, Nutzungslimit): Mit Ihrer Erlaubnis liest Lumina die von Android geführte Nutzungsdauer der Apps, um Ihnen Statistiken anzuzeigen und, falls Sie das Nutzungslimit eingeschaltet haben, etwa einmal pro Minute zu prüfen, ob die heutige Zeit in den gewählten Apps oder Gruppen das Limit erreicht. Die Auswertung erfolgt ausschließlich auf dem Gerät; gespeichert wird nur, wann zuletzt erinnert wurde.
d) Benachrichtigungen und Wecker: Erinnerungen und der Sitzungs-Hinweis werden mit dem Android-Wecker (pünktliche Wecker) lokal geplant und angezeigt. Ein Push-Dienst wird nicht verwendet. Auf Wunsch fragt Lumina Android, die App vom Energiesparen auszunehmen, damit Erinnerungen pünktlich ankommen; dabei werden keine Daten übermittelt.
e) Widgets: Die Startbildschirm-Widgets zeigen Daten aus dem lokalen App-Speicher an.
Sämtliche Berechtigungen sind freiwillig und können jederzeit in den Android-Einstellungen widerrufen werden; die übrigen Funktionen der App bleiben nutzbar.

## 5. Spracheingabe (Diktieren)
Für das Diktieren von Notizen nutzt Lumina die Spracherkennung Ihres Geräts über die Android-Schnittstelle. Abhängig von Gerät und Einstellungen erfolgt die Verarbeitung auf dem Gerät oder beim Anbieter des Spracherkennungsdienstes (in der Regel Google). Für diese Verarbeitung ist der jeweilige Anbieter selbst verantwortlich; es gelten dessen Datenschutzbestimmungen. Lumina erhält lediglich den erkannten Text und speichert keine Tonaufnahmen. Die Nutzung erfordert Ihre Erlaubnis für das Mikrofon und ist freiwillig.

## 6. Optionale Lumina-KI („Aufräumen mit Lumi“ und „Kapitel finden“)
a) Gegenstand: Nur wenn Sie diese Funktion in der App aktivieren, zuvor ausdrücklich einwilligen und anschließend „Aufräumen“ antippen, wird der Text der jeweiligen Notiz zusammen mit der gewählten Form (Text oder Stichpunkte) an einen vom Verantwortlichen betriebenen Dienst übermittelt, dort durch ein KI-Sprachmodell überarbeitet und unmittelbar an Ihr Gerät zurückgesandt. Für „Kapitel finden“ werden unter denselben Voraussetzungen der Titel des Buches und kurze, nummerierte Textausschnitte übermittelt (bei PDFs der Anfang jeder Seite, bei E-Books Überschriften und Abschnittsanfänge, zusammen höchstens 24.000 Zeichen), nicht das ganze Buch; das Sprachmodell schlägt daraus Kapitel vor. Hat ein PDF ein eigenes Inhaltsverzeichnis, liest Lumina dieses ausschließlich auf dem Gerät; dann wird nichts übermittelt. Bei Büchern ohne eigenes Inhaltsverzeichnis geschieht dies beim ersten Öffnen einmalig automatisch, sonst nur auf Ihren Tipp, und für höchstens drei verschiedene Bücher innerhalb von sieben Tagen. Ist die Lumina-KI nicht eingeschaltet, dieses Kontingent ausgeschöpft oder der Dienst nicht erreichbar, erkennt Lumina Kapitel ausschließlich auf dem Gerät anhand des Textes; dabei wird nichts übermittelt. Nummerierte Unterkapitel (z. B. „3.2 …“) sucht Lumina danach ausschließlich auf dem Gerät im Text des PDFs; dabei wird nichts übermittelt.
b) Verarbeitete Daten: Notiztext bzw. Buchtitel und Textausschnitte, Formatwahl, eine zufällig erzeugte Geräte-Kennung ohne Bezug zu Name, Telefonnummer oder Werbe-ID, die technisch notwendige IP-Adresse sowie Zeitpunkt der Anfrage.
c) Zwecke und Rechtsgrundlagen: Die Überarbeitung des Textes erfolgt auf Grundlage Ihrer Einwilligung (Art. 6 Abs. 1 lit. a DSGVO). Die Geräte-Kennung dient der Einhaltung von Tageslimits und dem Schutz des kostenlosen Dienstes vor Missbrauch; die Verarbeitung der IP-Adresse ist für den Verbindungsaufbau und die Sicherheit technisch erforderlich (Art. 6 Abs. 1 lit. f DSGVO; berechtigtes Interesse an einem sicheren und fair nutzbaren Dienst).
d) Speicherdauer: Notiztexte, Buchtitel und Textausschnitte werden vom Verantwortlichen nicht gespeichert. Die Geräte-Kennung wird mit einem Nutzungszähler gespeichert und nach spätestens 48 Stunden automatisch gelöscht. Vom Dienstleister erzeugte technische Protokolle werden nach dessen Fristen gelöscht.
e) Auftragsverarbeiter und Drittlandübermittlung: Der Dienst wird über Cloudflare Workers und Cloudflare Workers AI der Cloudflare, Inc., 101 Townsend St., San Francisco, CA 94107, USA, betrieben, die als Auftragsverarbeiter (Art. 28 DSGVO) auf Grundlage ihrer Auftragsverarbeitungsbedingungen tätig wird. Eine Verarbeitung in den USA ist möglich. Cloudflare, Inc. ist nach dem EU-U.S. Data Privacy Framework zertifiziert; die Übermittlung stützt sich auf den Angemessenheitsbeschluss der Europäischen Kommission vom 10. Juli 2023 (Art. 45 DSGVO) sowie ergänzend auf die EU-Standardvertragsklauseln (Art. 46 Abs. 2 lit. c DSGVO).
f) Freiwilligkeit und Widerruf: Die Nutzung ist freiwillig; ohne sie wird die Notiz auf dem Gerät aufgeräumt. Sie können Ihre Einwilligung jederzeit mit Wirkung für die Zukunft widerrufen, indem Sie die Funktion unter Einstellungen → Notizen ausschalten (Art. 7 Abs. 3 DSGVO). Die Rechtmäßigkeit der bis zum Widerruf erfolgten Verarbeitung bleibt unberührt.
g) Bitte übermitteln Sie über diese Funktionen keine besonderen Kategorien personenbezogener Daten (Art. 9 DSGVO) und keine Daten Dritter.

## 7. Fehlerberichte
Stürzt die App ab, zeigt sie in den Einstellungen einen Fehlerbericht an, den Sie über die Teilen-Funktion Ihres Geräts versenden können, etwa per E-Mail an den Verantwortlichen. Der Bericht wird nur versandt, wenn Sie dies selbst auslösen und einen Empfänger wählen; er enthält ausschließlich technische Angaben (App-Version, Android-Version, Gerätemodell, Fehlermeldung), die vor dem Versand einsehbar sind. Rechtsgrundlage ist Art. 6 Abs. 1 lit. f DSGVO (Fehlerbehebung). Die E-Mail wird gelöscht, sobald der Fehler bearbeitet ist, spätestens nach zwölf Monaten.

## 8. Keine automatisierte Entscheidungsfindung
Eine automatisierte Entscheidungsfindung einschließlich Profiling im Sinne von Art. 22 DSGVO findet nicht statt.

## 9. Ihre Rechte
Ihnen stehen gegenüber dem Verantwortlichen folgende Rechte zu: Auskunft (Art. 15 DSGVO), Berichtigung (Art. 16), Löschung (Art. 17), Einschränkung der Verarbeitung (Art. 18), Datenübertragbarkeit (Art. 20) sowie Widerruf erteilter Einwilligungen (Art. 7 Abs. 3).
Widerspruchsrecht (Art. 21 DSGVO): Soweit eine Verarbeitung auf Art. 6 Abs. 1 lit. f DSGVO beruht, können Sie ihr aus Gründen, die sich aus Ihrer besonderen Situation ergeben, jederzeit widersprechen.
Da der Verantwortliche außerhalb der Funktion nach Ziffer 6 keine Daten von Ihnen erhält, können sich diese Rechte nur auf die dort genannten Daten beziehen. Die zufällige Geräte-Kennung lässt sich keiner Person zuordnen; kann der Verantwortliche Sie deshalb nicht identifizieren, gilt Art. 11 DSGVO. Die Kennung wird ohnehin nach spätestens 48 Stunden gelöscht; durch Löschen der App-Daten wird auf Ihrem Gerät eine neue erzeugt.
Beschwerderecht (Art. 77 DSGVO): Sie können sich bei einer Datenschutz-Aufsichtsbehörde beschweren, insbesondere in dem Mitgliedstaat Ihres Aufenthaltsorts. Zuständig für den Verantwortlichen ist der Hessische Beauftragte für Datenschutz und Informationsfreiheit, Wiesbaden.

## 10. Minderjährige
Die Funktion nach Ziffer 6 sollen Personen unter 16 Jahren nur mit Zustimmung ihrer Erziehungsberechtigten einschalten (Art. 8 DSGVO).

## 11. Datensicherheit
Übertragungen im Rahmen von Ziffer 6 erfolgen ausschließlich verschlüsselt (TLS). Lokale Daten sind durch die Sandbox des Betriebssystems vor dem Zugriff anderer Apps geschützt.

## 12. Änderungen
Diese Erklärung wird angepasst, wenn sich Funktionen der App oder die Rechtslage ändern. Maßgeblich ist die jeweils in der App angezeigte Fassung.
Stand: $UPDATED
""".trim()

    private val PRIVACY_EN = """
## Summary
Lumina has no user accounts, no advertising, no analytics or tracking tools and no servers of its own for your data. All content and settings remain on your device. Data is transmitted to third parties only if you expressly switch on and use the optional Lumina AI ("Clean up with Lumi" and "Find chapters", section 6) or use your device's voice input (section 5).
This English text is a translation for convenience; the German version is legally binding.

## 1. Controller
The controller within the meaning of Art. 4(7) of the General Data Protection Regulation (GDPR) is:
$OWNER (private individual)
E-mail: $CONTACT
No data protection officer has been appointed; there is no legal obligation to do so.

## 2. Scope
This policy applies to the app "Lumina" as provided via its source code repository or an app catalogue (e.g. F-Droid). Distribution platforms from which you obtain the app are subject to their own privacy policies; the controller has no influence over the processing there.

## 3. Local storage on your device
Focus times (including time spent in books), sessions, streak, goals, modes, notes (for notes in a book with the book title, page and position), task progress, the think-break settings (selected apps, check times) and their counters, the reminder settings (including the titles and texts of reminders you create and the apps and groups chosen for the usage limit) the books you add (as a copy of the chosen file) with reading position, bookmarks, chapters and reading time, and all other settings are stored exclusively in your device's protected app storage. The controller has no access to this data; it is neither transmitted nor analysed. It is removed completely when you uninstall the app or clear its data in the Android settings. Automatic Android cloud backup is disabled for Lumina.

## 4. Permissions and device functions
a) Accessibility service (lock, think break, reminders): when the accessibility service is enabled, Lumina determines which app is in the foreground. This serves solely to block apps that are not allowed during a reading session and, from the check times you have chosen, to show a think break with a question when you open apps you have selected. Screen content, input and passwords are not read. Only the package name and time are processed on the device and stored merely as counters (such as "resisted today").
b) Reminders in "Full screen": while the accessibility service is running, Lumina receives the system notice that the device has been unlocked and can show a due reminder full screen inside the app; if you are using the device at that moment, Lumina briefly brings itself to the front for this. Lumina checks on the device, using the time of day, the chosen weekdays and your focus time today, whether a reminder is due. Only reminders not yet shown are kept until the next unlock (at most six hours), along with your choice ("In 15 minutes") and, for each reminder, when and how it last arrived (shown in the editor). You choose for each reminder how it arrives and can switch it off or delete it at any time in the "Think break" tab.
c) Usage access (screen time, usage limit): with your permission, Lumina reads the app usage time recorded by Android in order to show you statistics and, if you have switched on the usage limit, to check about once a minute whether today's time in the chosen apps or groups has reached the limit. The analysis takes place exclusively on the device; only the time of the last reminder is stored.
d) Notifications and alarms: reminders and the session notice are scheduled with Android's alarm service (exact alarms) and shown locally. No push service is used. On request, Lumina asks Android to exempt the app from battery optimisation so reminders arrive on time; no data is transmitted in the process.
e) Widgets: the home screen widgets display data from local app storage.
All permissions are voluntary and can be revoked at any time in the Android settings; the app's other functions remain usable.

## 5. Voice input (dictation)
To dictate notes, Lumina uses your device's speech recognition via the Android interface. Depending on device and settings, processing takes place on the device or at the provider of the speech recognition service (usually Google). That provider is itself responsible for this processing, and its privacy policy applies. Lumina only receives the recognised text and does not store any audio. Use requires your microphone permission and is voluntary.

## 6. Optional Lumina AI ("Clean up with Lumi" and "Find chapters")
a) Subject: only if you activate this feature in the app, expressly consent beforehand and then tap "Clean up" is the text of the note concerned, together with the chosen format (text or bullet points), transmitted to a service operated by the controller, revised there by an AI language model and immediately returned to your device. For "Find chapters", under the same conditions, the book's title and short numbered text excerpts are transmitted (for PDFs the start of each page, for e-books headings and section starts, at most 24,000 characters in total), never the whole book; the language model suggests chapters from them. If a PDF has its own table of contents, Lumina reads it only on the device; nothing is transmitted then. For books without their own table of contents this happens once automatically on first opening, otherwise only when you tap it, and for at most three different books within seven days. If Lumina AI is switched off, this allowance is used up or the service cannot be reached, Lumina detects chapters only on the device from the text; nothing is transmitted then. Lumina then looks for numbered sub-chapters (e.g. "3.2 …") only on the device, in the text of the PDF; nothing is transmitted for this.
b) Data processed: note text or book title and text excerpts, format choice, a randomly generated device identifier with no link to your name, phone number or advertising ID, the technically necessary IP address and the time of the request.
c) Purposes and legal bases: revising the text is based on your consent (Art. 6(1)(a) GDPR). The device identifier serves to enforce daily limits and to protect the free service against abuse; processing the IP address is technically necessary for establishing the connection and for security (Art. 6(1)(f) GDPR; legitimate interest in a secure and fairly usable service).
d) Storage period: the controller does not store note texts, book titles or text excerpts. The device identifier is stored with a usage counter and deleted automatically after 48 hours at the latest. Technical logs generated by the service provider are deleted according to its retention periods.
e) Processor and transfer to a third country: the service is operated via Cloudflare Workers and Cloudflare Workers AI of Cloudflare, Inc., 101 Townsend St., San Francisco, CA 94107, USA, which acts as a processor (Art. 28 GDPR) on the basis of its data processing terms. Processing in the USA is possible. Cloudflare, Inc. is certified under the EU-U.S. Data Privacy Framework; the transfer is based on the European Commission's adequacy decision of 10 July 2023 (Art. 45 GDPR) and additionally on the EU Standard Contractual Clauses (Art. 46(2)(c) GDPR).
f) Voluntariness and withdrawal: use is voluntary; without it, the note is cleaned up on the device. You may withdraw your consent at any time with effect for the future by switching the feature off under Settings → Notes (Art. 7(3) GDPR). This does not affect the lawfulness of processing carried out before withdrawal.
g) Please do not transmit special categories of personal data (Art. 9 GDPR) or data of third parties via these features.

## 7. Crash reports
If the app crashes, it shows a crash report in the settings, which you can send using your device's share function, for example by e-mail to the controller. The report is only sent if you trigger this yourself and choose a recipient; it contains only technical information (app version, Android version, device model, error message), which you can view before sending. The legal basis is Art. 6(1)(f) GDPR (fixing errors). The e-mail is deleted once the error has been dealt with, after twelve months at the latest.

## 8. No automated decision-making
No automated decision-making, including profiling, within the meaning of Art. 22 GDPR takes place.

## 9. Your rights
You have the following rights against the controller: access (Art. 15 GDPR), rectification (Art. 16), erasure (Art. 17), restriction of processing (Art. 18), data portability (Art. 20) and withdrawal of consent given (Art. 7(3)).
Right to object (Art. 21 GDPR): where processing is based on Art. 6(1)(f) GDPR, you may object to it at any time on grounds relating to your particular situation.
As the controller receives no data from you outside the feature described in section 6, these rights can only relate to the data listed there. The random device identifier cannot be attributed to a person; if the controller is therefore unable to identify you, Art. 11 GDPR applies. The identifier is deleted after 48 hours at the latest anyway; clearing the app data creates a new one on your device.
Right to lodge a complaint (Art. 77 GDPR): you may lodge a complaint with a data protection supervisory authority, in particular in the Member State of your residence. The authority competent for the controller is the Hessian Commissioner for Data Protection and Freedom of Information, Wiesbaden, Germany.

## 10. Minors
Persons under 16 should only switch on the feature described in section 6 with the consent of their parents or guardians (Art. 8 GDPR).

## 11. Data security
Transmissions under section 6 are always encrypted (TLS). Local data is protected against access by other apps by the operating system's sandbox.

## 12. Changes
This policy will be updated if the app's functions or the legal situation change. The version shown in the app applies.
Last updated: $UPDATED
""".trim()

    private val TERMS_DE = """
## § 1 Geltungsbereich und Anbieter
(1) Diese Nutzungsbedingungen regeln die Nutzung der App „Lumina“ (nachfolgend „App“) einschließlich der optionalen Funktion „Aufräumen mit Lumi“ (nachfolgend „Lumi-Dienst“).
(2) Die App wird von $OWNER, einer Privatperson, unentgeltlich und ohne Gewinnerzielungsabsicht bereitgestellt (nachfolgend „Entwickler“). Kontakt: $CONTACT; die vollständigen Angaben stehen unter „Impressum & Kontakt“.
(3) Mit der Nutzung der App erklären Sie sich mit diesen Bedingungen einverstanden. Abweichende Bedingungen des Nutzers finden keine Anwendung.

## § 2 Lizenz und Quellcode
(1) Die App ist freie Software. Für den Quellcode und dessen Weitergabe gilt ausschließlich die dem Quellcode beigefügte $LICENSE_DE (siehe „Impressum & Kontakt“). Im Verhältnis zu diesen Bedingungen gehen die Bestimmungen der Lizenz vor, soweit sie Rechte an der Software betreffen.
(2) In der App enthaltene Werke Dritter (z. B. Schriftarten, Symbole) unterliegen den unter „Über Lumina → Quellen & Lizenzen“ genannten Lizenzen.

## § 3 Leistungsbeschreibung
(1) Die App unterstützt konzentriertes Arbeiten, Lernen und Lesen, unter anderem durch Fokuszeiten mit App-Sperre, Denkpausen beim Öffnen ausgewählter Apps, Erinnerungen beim Entsperren, Notizen und Übungsaufgaben.
(2) Ein Anspruch auf eine bestimmte Funktion, Verfügbarkeit, Fehlerfreiheit oder Weiterentwicklung der App besteht nicht. Der Lumi-Dienst unterliegt Tageslimits und kann jederzeit ohne Ankündigung eingeschränkt, geändert oder eingestellt werden.
(3) Die App ist kein Medizinprodukt und ersetzt keine ärztliche, psychologische oder therapeutische Beratung oder Behandlung. Übungsaufgaben und Erklärungen werden mit Sorgfalt erstellt; eine Gewähr für Vollständigkeit und inhaltliche Richtigkeit wird nicht übernommen. Sie stellen keinen Unterricht und keine Prüfungsvorbereitung mit Erfolgsgarantie dar.

## § 4 Sperre, Denkpause und Erinnerungen
(1) Die Sperr-, Denkpausen- und Erinnerungsfunktionen dienen ausschließlich der freiwilligen Selbstbeschränkung. Sie können jederzeit über die in der App vorgesehenen Möglichkeiten beendet, in der App ausgeschaltet oder in den Android-Einstellungen deaktiviert werden.
(2) Der Nutzer ist selbst dafür verantwortlich, die Funktionen nicht in Situationen einzusetzen, in denen der ungehinderte Zugriff auf sein Gerät erforderlich ist, insbesondere im Straßenverkehr, bei beruflichen Bereitschaftsdiensten oder in Notfällen. Die Notruffunktionen des Betriebssystems werden von der App nicht beeinflusst; eine Gewähr hierfür kann wegen der Vielzahl von Geräten jedoch nicht übernommen werden.

## § 5 Lumi-Dienst und KI-Ergebnisse
(1) Die Ergebnisse des Lumi-Dienstes werden automatisiert durch ein KI-Sprachmodell erzeugt. Sie können unvollständig, fehlerhaft oder sinnentstellend sein. Der Nutzer hat Ergebnisse vor jeder Verwendung selbst zu prüfen und bleibt für den Inhalt seiner Notizen allein verantwortlich.
(2) Der Lumi-Dienst darf nur über die App und nur für eigene Notizen und Bücher genutzt werden. Untersagt sind insbesondere automatisierte oder massenhafte Anfragen, das Umgehen von Nutzungslimits oder technischen Schutzmaßnahmen, die Übermittlung rechtswidriger Inhalte sowie von Inhalten, die Rechte Dritter verletzen.
(3) Bei Verstößen gegen Absatz 2 ist der Entwickler berechtigt, die betroffene Geräte-Kennung ohne vorherige Ankündigung vom Lumi-Dienst auszuschließen.
(4) In die Bibliothek darf der Nutzer nur Dateien aufnehmen, zu deren Nutzung er berechtigt ist. Die Bücher bleiben auf seinem Gerät; Lumina umgeht keinen Kopierschutz.

## § 6 Haftung
(1) Die App wird unentgeltlich überlassen. Der Entwickler haftet daher entsprechend § 521 BGB nur für Vorsatz und grobe Fahrlässigkeit. Für Mängel der App haftet er entsprechend §§ 523, 524 BGB nur, wenn er einen Mangel arglistig verschwiegen hat.
(2) Unberührt bleiben die Haftung für Schäden aus der Verletzung des Lebens, des Körpers oder der Gesundheit, die Haftung nach dem Produkthaftungsgesetz sowie die Haftung aufgrund einer ausdrücklich übernommenen Garantie.
(3) Für den Verlust von Daten haftet der Entwickler im Rahmen der Absätze 1 und 2 nur in dem Umfang, der bei regelmäßiger und gefahrentsprechender Sicherung durch den Nutzer entstanden wäre.
(4) Soweit die Haftung des Entwicklers ausgeschlossen oder beschränkt ist, gilt dies auch für die persönliche Haftung von Personen, die an der Entwicklung mitgewirkt haben.

## § 7 Minderjährige
Personen unter 16 Jahren sollen den Lumi-Dienst nur mit Zustimmung ihrer Erziehungsberechtigten einschalten.

## § 8 Änderungen dieser Bedingungen
Der Entwickler kann diese Bedingungen mit Wirkung für die Zukunft ändern, wenn dies wegen neuer Funktionen, geänderter Rechtslage oder zur Behebung von Unklarheiten erforderlich ist. Die jeweils aktuelle Fassung wird in der App angezeigt. Wer die App nach einer Änderung weiter nutzt, nimmt die geänderte Fassung an; wer nicht einverstanden ist, kann die Nutzung jederzeit beenden.

## § 9 Schlussbestimmungen
(1) Es gilt das Recht der Bundesrepublik Deutschland. Gegenüber Verbrauchern gilt diese Rechtswahl nur insoweit, als dadurch nicht der Schutz durch zwingende Bestimmungen des Rechts des Staates entzogen wird, in dem der Verbraucher seinen gewöhnlichen Aufenthalt hat.
(2) Sollten einzelne Bestimmungen dieser Bedingungen unwirksam sein oder werden, bleibt die Wirksamkeit der übrigen Bestimmungen unberührt. An die Stelle der unwirksamen Bestimmung tritt die gesetzliche Regelung.
(3) Maßgeblich ist die deutsche Fassung dieser Bedingungen.

Stand: $UPDATED
""".trim()

    private val TERMS_EN = """
## § 1 Scope and provider
(1) These terms of use govern the use of the app "Lumina" (the "App"), including the optional "Clean up with Lumi" feature (the "Lumi Service").
(2) The App is provided free of charge and without any intention to make a profit by $OWNER, a private individual (the "Developer"). Contact: $CONTACT; full details are given under "Legal notice & contact".
(3) By using the App you agree to these terms. Any conflicting terms of the user do not apply.
This English text is a translation for convenience; the German version is legally binding.

## § 2 Licence and source code
(1) The App is free software. The source code and its distribution are governed exclusively by the $LICENSE_EN enclosed with the source code (see "Legal notice & contact"). Where rights in the software are concerned, the provisions of the licence take precedence over these terms.
(2) Third-party works contained in the App (e.g. fonts, icons) are subject to the licences listed under "About Lumina → Sources & licences".

## § 3 Description of the service
(1) The App supports focused work, learning and reading, among other things through focus sessions with an app lock, think breaks when opening selected apps, reminders on unlock, notes and practice tasks.
(2) There is no entitlement to any particular function, availability, freedom from errors or further development of the App. The Lumi Service is subject to daily limits and may be restricted, changed or discontinued at any time without notice.
(3) The App is not a medical device and does not replace medical, psychological or therapeutic advice or treatment. Practice tasks and explanations are prepared with care; no guarantee is given for their completeness or accuracy. They do not constitute teaching or exam preparation with any guarantee of success.

## § 4 Lock, think break and reminders
(1) The lock, think-break and reminder functions serve exclusively voluntary self-restriction. They can be ended at any time using the options provided in the App, switched off in the App or deactivated in the Android settings.
(2) Users are themselves responsible for not using these functions in situations where unhindered access to their device is required, in particular in road traffic, during professional on-call duties or in emergencies. The App does not affect the operating system's emergency call functions; however, given the large number of devices, no guarantee can be given for this.

## § 5 Lumi Service and AI results
(1) Results of the Lumi Service are generated automatically by an AI language model. They may be incomplete, incorrect or distort the meaning. Users must check results themselves before any use and remain solely responsible for the content of their notes.
(2) The Lumi Service may only be used via the App and only for one's own notes and books. In particular, automated or mass requests, circumventing usage limits or technical protection measures, and transmitting unlawful content or content that infringes third-party rights are prohibited.
(3) In the event of breaches of paragraph 2, the Developer is entitled to exclude the device identifier concerned from the Lumi Service without prior notice.
(4) Users may only add files to the library that they are entitled to use. The books stay on their device; Lumina does not circumvent any copy protection.

## § 6 Liability
(1) The App is provided free of charge. The Developer is therefore liable, in accordance with § 521 of the German Civil Code (BGB), only for intent and gross negligence. For defects of the App he is liable, in accordance with §§ 523, 524 BGB, only if he fraudulently concealed a defect.
(2) This does not affect liability for injury to life, body or health, liability under the German Product Liability Act, or liability under an expressly assumed guarantee.
(3) Within the limits of paragraphs 1 and 2, the Developer is liable for loss of data only to the extent that would have arisen had the user backed up data regularly and appropriately.
(4) Insofar as the Developer's liability is excluded or limited, this also applies to the personal liability of persons who contributed to the development.

## § 7 Minors
Persons under 16 should only switch on the Lumi Service with the consent of their parents or guardians.

## § 8 Changes to these terms
The Developer may amend these terms with effect for the future where this is necessary because of new functions, changes in the law or to remove ambiguities. The current version is shown in the App. Anyone who continues to use the App after a change accepts the amended version; anyone who does not agree may stop using the App at any time.

## § 9 Final provisions
(1) The law of the Federal Republic of Germany applies. In relation to consumers, this choice of law applies only insofar as it does not deprive them of the protection afforded by mandatory provisions of the law of the country in which they have their habitual residence.
(2) Should individual provisions of these terms be or become invalid, the validity of the remaining provisions shall not be affected. The invalid provision shall be replaced by the statutory provision.
(3) The German version of these terms is authoritative.

Last updated: $UPDATED
""".trim()
}

/** Text in Vollbild zum Lesen (Überschriften mit "## "). */
@Composable
fun LegalSheet(doc: LegalDoc, onClose: () -> Unit) {
    val p = Funke.colors
    BackHandler { onClose() }
    Box(
        Modifier
            .fillMaxSize()
            .background(p.bg)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { },
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(start = Space.s4, end = Space.s4, top = Space.s3, bottom = Space.s2), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).glass(CircleShape).clickable { onClose() }
                        .semantics { contentDescription = tr("Schließen", "Close") },
                    contentAlignment = Alignment.Center,
                ) { CloseIcon(p.text, Modifier.size(13.dp)) }
                Spacer(Modifier.width(Space.s3))
                Text(LegalTexts.title(doc), style = MaterialTheme.typography.titleLarge, color = p.text)
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Space.s6, vertical = Space.s3),
            ) {
                for (block in LegalTexts.text(doc).split("\n\n")) {
                    val lines = block.trim().lines()
                    val head = lines.firstOrNull()?.takeIf { it.startsWith("## ") }
                    if (head != null) {
                        Spacer(Modifier.height(Space.s3))
                        Text(head.removePrefix("## "), style = MaterialTheme.typography.titleMedium, color = p.text)
                        Spacer(Modifier.height(Space.s1))
                    }
                    val body = (if (head != null) lines.drop(1) else lines).joinToString("\n")
                    if (body.isNotBlank()) {
                        Text(body, style = MaterialTheme.typography.bodyMedium, color = p.textSecondary)
                        Spacer(Modifier.height(Space.s2))
                    }
                }
                Spacer(Modifier.height(Space.s10))
            }
        }
    }
}
