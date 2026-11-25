# Lumi (KI-Aufräumen) – der kleine Server bei Cloudflare

Lumina räumt Notizen mit KI auf, ohne dass Nutzer sich irgendwo anmelden müssen. Dafür gibt es einen
kleinen Cloudflare Worker (`lumina-ki-worker.js`, Version 3). Er nimmt die Notiz von der App an, prüft
die Limits, lässt ein offenes KI-Modell aufräumen und schickt Text oder Stichpunkte zurück.

```
Lumina-App  ──Notiz──▶  Worker (Kennwort, Limits, Kostenbremse)  ──▶  Workers AI
            ◀─Ergebnis──                                          ◀──  (gpt-oss-120b → gpt-oss-20b → Llama 3.2 3B)
```

## Was es kostet
Nichts, solange das Gratis-Kontingent reicht (10.000 "Neurons" pro Tag für alle zusammen). Ist es leer,
antwortet der Worker mit `fallback` und Lumina räumt auf dem Handy auf. Im Gratis-Plan kann keine
Rechnung entstehen.

## Stufen pro Handy und Tag (`TIERS`)
| Aufräumen am Tag | Modell | In der App |
|---|---|---|
| 1.–3. | `@cf/openai/gpt-oss-120b` | "Beste KI" |
| 4.–8. | `@cf/openai/gpt-oss-20b` | "Standard-KI" |
| 9.–20. | `@cf/meta/llama-3.2-3b-instruct` | "Spar-KI" |
| ab 21. | – | auf dem Handy |

Antwortet ein Modell nicht, nimmt der Worker automatisch das nächste der Stufe. Gezählt wird erst, wenn
eine Antwort da ist – Fehlversuche kosten nichts. Die App zeigt nach dem Aufräumen, welche Stufe
geantwortet hat und wie oft heute noch.

## Schutz gegen Missbrauch
1. **Kostenbremse:** `DAY_BUDGET` im Code – mehr wird am Tag nie verbraucht. Teure Modelle fallen zuerst weg.
2. **Limit pro Handy:** siehe Stufen oben.
3. **App-Kennwort:** Header `x-lumina` muss zum Secret `APP_TOKEN` passen (hält nur Neugierige fern,
   weil es auch im Open-Source-Code steht).
4. Später (Play Store): Play Integrity, damit nur die echte App aus dem Store fragen darf.

## Einrichten (einmal, im Browser)
1. Konto auf dash.cloudflare.com (Mindestalter 18 – Konto auf ein Elternteil), E-Mail bestätigen.
2. **Storage & databases → Workers KV → Create**, Name `lumina-limits`.
3. **Compute → Workers & Pages → Create → Start with Hello World!**, Name `lumina-ki`, Deploy.
4. **Edit code**: Inhalt von `worker.js` durch `lumina-ki-worker.js` ersetzen, Deploy.
5. **Settings → Bindings**: `Workers AI` als `AI`, `KV namespace` `lumina-limits` als `LIMITS`.
6. **Settings → Variables and Secrets**: Secret `APP_TOKEN` = derselbe Wert wie `AiCleanup.APP_TOKEN` in der App.
7. Test: die Worker-Adresse im Browser öffnen → `{"ok":true,"error":"Lumina-KI läuft.","version":4}`.
8. Adresse in `AiCleanup.DEFAULT_SERVER` eintragen (oder in der App: Einstellungen → Notizen → Eigener Server).

## Worker aktualisieren auf Version 4 (ab Lumina 13.4 – für „Kapitel finden“)
Version 4 kann zusätzlich Kapitel für Bücher finden (`mode: "chapters"`). Das Notiz-Aufräumen bleibt genau gleich.
Ohne Update zeigt die App beim Kapitel-Finden: „Der Lumina-KI-Server kennt die Kapitelsuche noch nicht.“

1. dash.cloudflare.com → **Compute → Workers & Pages → lumina-ki** → **Edit code**.
2. `worker.js` komplett durch den Inhalt von `server/lumina-ki-worker.js` ersetzen → **Deploy**.
3. Test im Browser: die Worker-Adresse öffnen → `"version":4`.

Kosten: Eine Kapitelsuche ist größer als eine Notiz (bis 24.000 Zeichen rein, grob 180–420 Neurons). Pro Handy
gehen höchstens 3 Bücher am Tag (`CHAPTER_LIMIT`), und alles zählt mit ins gemeinsame `DAY_BUDGET`.
Der Worker speichert keine Buchtexte.

## Worker aktualisieren (auf Version 3 ab Lumina 10.1 – wichtig!)
Version 2 schickte gpt-oss eine Anfrageform, die Cloudflare nicht mehr annimmt. Deshalb kam in der App
"Lumina-KI gerade nicht erreichbar". So kommt die neue Version drauf:

1. dash.cloudflare.com → **Compute → Workers & Pages → lumina-ki**.
2. Oben rechts **Edit code**.
3. Links `worker.js` öffnen, alles markieren (Strg+A) und löschen.
4. Den ganzen Inhalt von `server/lumina-ki-worker.js` aus diesem Projekt einfügen.
5. **Deploy**. Bindings (`AI`, `LIMITS`) und das Secret `APP_TOKEN` bleiben erhalten.
6. Test im Browser: die Worker-Adresse öffnen → `"version":3`.
7. Test in der App: **Einstellungen → Notizen → Verbindung testen**. Für jedes Modell steht dort
   "ok" mit der Antwortzeit – oder der genaue Grund, warum es nicht geht.

## Wenn etwas nicht geht
- **Verbindung testen** in der App zeigt den Grund, z. B. "Worker veraltet", "Binding AI fehlt im
  Worker", "App-Kennwort passt nicht (403)" oder die Fehlermeldung des Modells.
- Live-Logs: **lumina-ki → Observability/Logs** (oder **Logs → Begin log stream**), dann in der App
  aufräumen. Dort steht jeder Fehler mit Modellnamen.
- `GET /health` (mit Header `x-lumina`) fragt alle drei Modelle mit einem kurzen Satz und meldet pro
  Modell `ok`, Zeit und Fehler. Das macht der Knopf in der App.

## Die KI verbessern
Die KI ist nicht extra trainiert. Was sie tun soll, steht in `RULES`, `FORMAT` und den Beispielen
(`EXAMPLE_IN` / `EXAMPLE_OUT`) oben im Worker. Gute Beispiele bringen am meisten: echte, schlecht erkannte
Diktate und daneben, wie das Ergebnis aussehen soll.

## Zahlen und Modelle ändern
`DAY_BUDGET`, `MODELS`, `TIERS` und `MAX_CHARS` oben im Worker. Änderungen gelten sofort nach "Deploy",
ohne neue App-Version. Ein Modell austauschen: in `MODELS` die `id` ändern (Liste unter
developers.cloudflare.com/workers-ai/models), `cost` grob schätzen, `reasoning: true` nur für gpt-oss.

## Grenzen
- Der Gratis-KV-Speicher erlaubt 1.000 Schreibvorgänge am Tag (2 pro Notiz). Für ein paar hundert
  Nutzer reicht das; wird Lumina größer, auf Durable Objects umstellen oder den bezahlten Plan nehmen.
- F-Droid markiert Apps, die einen Dienst des Entwicklers nutzen ("Tethered Network Services").
  Deshalb ist die Server-Adresse in der App änderbar und Lumina-KI lässt sich ausschalten.
