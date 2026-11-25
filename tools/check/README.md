# Prüfen ohne Android Studio

In der Arbeitsumgebung von Claude gibt es kein Maven Central / Google Maven, also keinen echten Gradle-Build.
Damit Fehler trotzdem nicht erst bei Simon auffallen, kompiliert diese Prüfung die ganze App mit `kotlinc` gegen
selbst geschriebene Stubs (Android, Compose 1.7, Material 3, Lifecycle …). Laufzeitverhalten wird nicht geprüft –
jede Änderung muss auf dem Gerät getestet werden.

## Einmal einrichten

1. Kotlin-Compiler **2.0.21** (gleiche Version wie im Projekt) von der offiziellen JetBrains-Release-Seite auf GitHub
   holen und nach `/home/claude/tools/kotlinc` entpacken (`kotlin-compiler-2.0.21.zip`). Keine anderen Quellen,
   Mirrors oder Proxys verwenden.
2. `tools/check/build_stubs.sh` – baut nach `tools/check/build/`:
   - `android-stubs.jar` aus `android_bundle.java` (Java-Stubs für `android.*`, `org.json`, AndroidX-Kern …),
   - `compose-stubs.jar` aus `kt_bundle1.kt` … `kt_bundle4.kt` (Compose-Runtime/UI/Foundation/Animation,
     Material 3, Icons, Lifecycle, Activity),
   - `compose-compiler-cli.jar`: das Compose-Plugin aus `kotlinc/lib/compose-compiler.jar`, mit `relocate.py`
     auf den unverschatteten `kotlinc` umgebaut (`org.jetbrains.kotlin.com.intellij` → `com.intellij`).

Andere Pfade: `KC=/pfad/zu/kotlinc tools/check/build_stubs.sh`.

## Prüfen

```
tools/check/check.sh        # beide Durchgänge, Ziel: ERRORS: 0 (Dauer ca. 1,5 min)
tools/check/check.sh A      # nur Durchgang A
```

- **Durchgang A** mit Compose-Plugin: findet z. B. `@Composable`-Aufrufe außerhalb von Composables. Der Compiler
  bricht danach absichtlich mit `IncompatibleComposeRuntimeVersionException` ab – das ist erwartet.
- **Durchgang B** ohne Plugin bis zum Bytecode: findet JVM-Fehler wie „Platform declaration clash“.
- Fehlt ein Android-/Compose-Teil in den Stubs, meldet die Prüfung „Unresolved reference“. Dann die **echte**
  Signatur in die passende Bündel-Datei eintragen (`//// FILE: pfad`-Abschnitte) und `build_stubs.sh` neu laufen
  lassen. Nur APIs verwenden, die es in Compose 1.7 sicher gibt.
- `genR.py` erzeugt `R.kt` aus `app/src/main/res` (auch `@+id/` aus Layouts).

## PDF-Inhaltsverzeichnis (`pdf-outline/`)

Test für `books/PdfOutline.kt` auf der JVM (die Datei braucht nichts von Android):

```
pip install --break-system-packages pypdf reportlab pikepdf
python3 tools/check/pdf-outline/gen.py          # 27 Test-PDFs nach pdf-outline/pdfs/, Erwartung über pypdf
cp app/src/main/java/de/funke/app/books/PdfOutline.kt tools/check/pdf-outline/src/
kotlinc -jvm-target 17 tools/check/pdf-outline/src/{Chapter,PdfOutline,Main}.kt -include-runtime -d tools/check/pdf-outline/outline.jar
cd tools/check/pdf-outline && python3 run.py
```

Abgedeckt: klassische Querverweise, Objekt-Streams, linearisiert, nachträglich geändert (/Prev), kaputte
Querverweise (Suche), benannte Ziele (Wörterbuch und Namensbaum), GoTo-Aktionen, verschachtelter Seitenbaum,
Einträge ohne Ziel, RC4 40/128, AES 128 (auch ohne Metadaten-Verschlüsselung), AES 256, Benutzerkennwort (→ nichts).
Zwei erwartete Abweichungen: `int_dest` (pypdf liest Seitenzahlen statt Verweisen falsch, Lumina richtig) und `big`
(über 1.200 Einträge lässt Lumina absichtlich die dritte Ebene weg).

`src/Fuzz.kt` beschädigt PDFs zufällig (1.500 Fälle) – es darf nie hängen oder abstürzen.
