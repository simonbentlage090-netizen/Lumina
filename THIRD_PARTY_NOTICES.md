# Third-party notices / Fremdinhalte

Lumina enthält eine kleine, übersetzte und angepasste Auswahl von Textaufgaben aus **GSM8K**
(Grade School Math 8K) von OpenAI: https://github.com/openai/grade-school-math

Die Aufgaben stehen in `app/src/main/java/de/funke/app/ui/challenge/WordProblems.kt`.
Der Lizenztext wird zusätzlich in der App unter Einstellungen → Über Lumina angezeigt.

```
MIT License

Copyright (c) 2021 OpenAI

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

Alle anderen Inhalte (Aufgaben-Generatoren, Wissensfragen, Lumi, Grafiken) sind eigene Inhalte von Lumina.


## chatcn – Audio Visualizer und Voice Orb

Die Sprach-Animationen beim Diktieren (Balken, Kreis, Raster, Linie, Punkte, Kugel) sind aus den
React-Komponenten "Audio Visualizer" und "Voice Orb" von chatcn nach Kotlin/Compose übertragen
(`ui/components/VoiceVisuals.kt`, `ui/components/VoiceOrb.kt`).

Quelle: chatcn von Aaditya, https://chatcn.dev (auf 21st.dev als MIT lizenziert).
Vor einer Veröffentlichung die genaue Copyright-Zeile aus dem Original übernehmen.

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


## Lucide – Symbole

Die Linien-Symbole der App (Navigation, Einstellungen, Aufgaben; `res/drawable/ic_*.xml`) stammen aus
**Lucide** (https://lucide.dev), umgewandelt in Android-Vektorgrafiken. Einige Lucide-Symbole stammen
ursprünglich aus Feather (MIT, Cole Bemis). Der vollständige Lizenztext steht in
`licenses/Lucide-LICENSE.txt` und wird in der App unter Einstellungen → Über Lumina angezeigt.

ISC License – Copyright (c) 2026 Lucide Icons and Contributors

Seit 11.0 gehört dazu auch die Sanduhr der Denkpause (`ic_nav_gate.xml`, Lucide „hourglass“).

Die Fach-Bilder (`res/drawable/subj_*.xml`), das App-Symbol und Lumi sind eigene Inhalte von Lumina.


## Schriften (Inter, Nunito, Fredoka)

- **Inter** und **Inter Display** von Rasmus Andersson (https://rsms.me/inter), SIL Open Font License 1.1,
  `licenses/Inter-OFL.txt`.
- **Nunito** (seit 15.2), Copyright 2014 The Nunito Project Authors (https://github.com/googlefonts/nunito),
  SIL Open Font License 1.1, `licenses/Nunito-OFL.txt`.
- **Fredoka** (seit 15.2), Copyright 2016 The Fredoka Project Authors (https://github.com/hafontia/Fredoka-One),
  SIL Open Font License 1.1, `licenses/Fredoka-OFL.txt`.

Nunito und Fredoka stammen aus https://github.com/google/fonts (`ofl/nunito`, `ofl/fredoka`). Für die App wurden aus
den variablen Schriften feste Schnitte erzeugt und auf lateinische Schrift, Griechisch, Satzzeichen, Pfeile und
Mathe-Zeichen gekürzt (`tools/fonts-make.py`, mit fontTools). Keine der Schriften hat einen reservierten Namen.
Die Lizenztexte werden in der App unter Einstellungen → Über Lumina angezeigt.


## Philosophie und Literatur (seit 11.0)

Die Aufgaben in `ui/challenge/LabPhilosophy.kt` und `ui/challenge/LabLiterature.kt` sind selbst
geschrieben. Gedanken neuerer Autorinnen und Autoren sind in eigenen Worten wiedergegeben. Wörtliche Verse
und Sätze stammen nur aus gemeinfreien Werken (u. a. Goethe, Schiller, Lessing, Gryphius, Claudius, Kafka,
Shakespeare, Blake, Longfellow, C. C. Moore); englische Wiedergaben deutscher Texte sind eigene
Übersetzungen. Einige Beispielverse und alle Beispielstrophen für Reimschemata sind selbst gedichtet.


## Lehrbücher als Anregung für die Aufgaben (seit 10.1)

Die Aufgaben-Vorlagen im Aufgaben-Reiter (`ui/challenge/Lab*.kt`) sind selbst geschrieben. Themen,
Aufbau und Art der Erklärungen sind angeregt von:

- **OpenStax University Physics, Volumes 1–3** – Samuel J. Ling, Jeff Sanny, William Moebs u. a.,
  OpenStax, Rice University (2016). Lizenz: Creative Commons Attribution 4.0 International (CC BY 4.0),
  https://creativecommons.org/licenses/by/4.0/ – Buch: https://openstax.org/details/books/university-physics-volume-1
  Es wurden keine Texte oder Abbildungen übernommen.
- **Calculus Made Easy** – Silvanus P. Thompson (1910/1914), gemeinfrei (Project Gutenberg #33283).
- Halliday/Resnick/Walker „Fundamentals of Physics“, Susskind/Friedman „Quantum Mechanics – The
  Theoretical Minimum“ sowie Trodden & Carroll „TASI Lectures on Cosmology“ (arXiv:astro-ph/0401547)
  dienten nur als Anregung für Themen; daraus wurde nichts übernommen.

Beide Hinweise stehen in der App unter Einstellungen → Über Lumina → Quellen & Lizenzen.
