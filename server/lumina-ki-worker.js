// Lumina-KI – Cloudflare Worker (Version 4, ab Lumina 13.4)
// Räumt diktierte Notizen auf. Lumina schickt: { text, device, lang, mode } und den Header "x-lumina".
//   mode = "text"    → sauberer Fließtext (mit Absätzen, wo ein neues Thema beginnt)
//   mode = "bullets" → echte, kurze Stichpunkte
// Antwort: { text, model, tier, left, best } – oder { fallback: true, reason, detail } → Lumina räumt dann lokal auf.
// tier: "big" | "mid" | "small" (welches Modell geantwortet hat), left: KI-Durchgänge heute insgesamt übrig,
// best: davon noch mit dem besten Modell.
//
// Neu in Version 4 (Lumina 13.4):
//  - mode = "chapters": Kapitel und Unterkapitel für ein Buch finden. Lumina schickt NICHT das Buch, sondern nur
//    nummerierte Zeilen (Anfang jeder PDF-Seite bzw. Überschriften eines E-Books). Antwort: { kind: "chapters",
//    text: "Ebene|Nummer|Titel" je Zeile }. Eigenes Tageslimit (CHAPTER_LIMIT je Handy), zählt nicht bei den Notizen.
//
// Neu in Version 3:
//  - Alle Modelle bekommen das Chat-Format ("messages"), das jedes Textmodell bei Workers AI versteht.
//    (Version 2 schickte gpt-oss das Responses-Format mit "instructions" + Beispiel-Antwort. Cloudflare hat
//    die gpt-oss-Schnittstelle seitdem umgebaut; genau diese Kombination wird nicht mehr angenommen.)
//  - Klappt ein Modell nicht, versucht der Worker automatisch das nächste (großes → kleineres → kleinstes).
//    Erst wenn keins antwortet, räumt Lumina auf dem Handy auf.
//  - Genug Platz für die Antwort (max_tokens), sonst schneidet Workers AI nach 256 Tokens ab.
//  - Gezählt wird erst, wenn die KI wirklich geantwortet hat.
//  - GET /health (mit Kennwort-Header) prüft jedes Modell mit einem Mini-Text. Die App zeigt das unter
//    Einstellungen → Notizen → "Lumina-KI testen".
//
// Die KI ist NICHT extra trainiert: Es sind allgemeine, offene Modelle bei Cloudflare.
// Was sie tun sollen, steht unten in RULES und in den Beispielen (EXAMPLES). Nach "Deploy" gilt alles sofort,
// ohne neue App-Version.
//
// Bindings (Settings → Bindings):  AI = Workers AI,  LIMITS = KV-Namespace "lumina-limits"
// Secret (Settings → Variables and Secrets):  APP_TOKEN

const VERSION = 4;
const DAY_BUDGET = 9000; // Neurons pro Tag für alle zusammen (Gratis-Kontingent: 10.000)

// Stufen pro Handy und Tag: die ersten 3 mit dem besten Modell, dann kleinere, danach auf dem Handy.
// cost = grob geschätzte Neurons pro Notiz (mit Anweisung und Beispiel).
const MODELS = {
  big: { id: "@cf/openai/gpt-oss-120b", cost: 75, reasoning: true },
  mid: { id: "@cf/openai/gpt-oss-20b", cost: 32, reasoning: true },
  small: { id: "@cf/meta/llama-3.2-3b-instruct", cost: 12, reasoning: false },
};
const TIERS = [
  { upTo: 3, chain: ["big", "mid", "small"] },
  { upTo: 8, chain: ["mid", "small"] },
  { upTo: 20, chain: ["small"] },
];

const MAX_CHARS = 6000;
const MAX_CHAPTER_CHARS = 24000;
const CHAPTER_LIMIT = 3; // Bücher pro Handy und Tag
const CHAPTER_MODELS = [
  { key: "big", cost: 420 },
  { key: "mid", cost: 180 },
];

const CHAPTER_RULES = {
  de: `Du bekommst nummerierte Zeilen aus einem Buch. Jede Zeile ist der Anfang einer Seite (bei PDFs) oder eine Überschrift bzw. ein Abschnittsanfang (bei E-Books, Überschriften sind mit [H1], [H2], [H3] markiert). Finde daraus das Inhaltsverzeichnis: Kapitel (Ebene 1) und, wo sinnvoll, Unterkapitel (Ebene 2).

Regeln:
1. Antworte NUR mit Zeilen im Format: Ebene|Nummer|Titel – zum Beispiel: 1|12|Die Newtonschen Gesetze
2. Nummer ist die Nummer der Zeile, an der das Kapitel beginnt. Erfinde keine Nummern.
3. Titel kurz und so, wie er im Buch steht (ohne "Kapitel 3:" davor, wenn der eigentliche Titel danach kommt; Nummerierungen wie "3.2" darfst du behalten).
4. Ein echtes Inhaltsverzeichnis im Buch selbst, Kopf- und Fußzeilen, Seitenzahlen und Fließtext sind KEINE Kapitel.
5. Gibt es keine erkennbaren Überschriften, teile das Buch in sinnvolle Abschnitte von etwa 10–30 Seiten und gib ihnen kurze, beschreibende Titel.
6. Höchstens 60 Zeilen, in Lesereihenfolge. Keine Einleitung, kein Markdown.`,
  en: `You get numbered lines from a book. Each line is the start of a page (PDF) or a heading or section start (e-book, headings are marked [H1], [H2], [H3]). Work out the table of contents: chapters (level 1) and, where it makes sense, sub-chapters (level 2).

Rules:
1. Reply ONLY with lines in the format: level|number|title – for example: 1|12|Newton's laws
2. Number is the number of the line where the chapter starts. Never invent numbers.
3. Keep titles short and as written in the book (drop "Chapter 3:" when the real title follows; numbering like "3.2" may stay).
4. A printed table of contents, running headers, footers, page numbers and body text are NOT chapters.
5. If there are no recognisable headings, split the book into sensible sections of about 10–30 pages with short descriptive titles.
6. At most 60 lines, in reading order. No intro, no markdown.`,
};

// ---------------------------------------------------------------------------------------------
// Die Anweisung an die KI
// ---------------------------------------------------------------------------------------------

const RULES = {
  de: `Du bist Lumi, der Notiz-Helfer in der Lern-App Lumina. Du bekommst eine Notiz, die ein Schüler per Spracherkennung diktiert hat. Gib sie aufgeräumt zurück.

So räumst du auf:
1. Rechtschreibung, Grammatik, Groß- und Kleinschreibung und Satzzeichen korrigieren.
2. Versprecher, abgebrochene Wörter, Füllwörter (äh, ähm, also am Satzanfang, halt, quasi, sozusagen, irgendwie, genau) und doppelt Gesagtes entfernen. Wenn sich der Sprecher korrigiert ("drei, nein vier"), nur die korrigierte Version behalten.
3. Falsch erkannte Wörter: Wenn ein Wort nicht existiert oder überhaupt nicht in den Zusammenhang passt, ersetze es durch das Wort, das wahrscheinlich gemeint war (ähnlicher Klang, passt zum Thema, z. B. "Foto Synthese" → "Photosynthese", "Mitochondrium" statt "mit Kondrium"). Bist du dir nicht sicher, lass das Wort stehen.
4. Mathe, Physik, Chemie: Gesprochene Formeln als Formel schreiben (x², x³, √2, ∫₀¹ x² dx, f′(x), π, ·, ÷, ≈, ≠, ≤, ≥, Δt, →, H₂O, CO₂).
5. Den Inhalt nicht verändern: nichts erfinden, nichts weglassen, was der Sprecher inhaltlich gesagt hat, keine Erklärungen, keine Bewertung, keine eigene Meinung.
6. Antworte nur mit der aufgeräumten Notiz – ohne Einleitung, ohne Anführungszeichen, ohne Markdown-Sternchen.`,
  en: `You are Lumi, the note helper in the study app Lumina. You get a note a student dictated with speech recognition. Return it cleaned up.

How to clean up:
1. Fix spelling, grammar, capitalisation and punctuation.
2. Remove slips of the tongue, broken-off words, filler words (uh, um, like, basically, you know, so at the start) and anything said twice. If the speaker corrects themselves ("three, no four"), keep only the corrected version.
3. Misheard words: if a word doesn't exist or doesn't fit the context at all, replace it with the word that was most likely meant (similar sound, fits the topic, e.g. "photo since this" → "photosynthesis"). If you're not sure, keep the word.
4. Maths, physics, chemistry: write spoken formulas as formulas (x², x³, √2, ∫₀¹ x² dx, f′(x), π, ·, ÷, ≈, ≠, ≤, ≥, Δt, →, H₂O, CO₂).
5. Don't change the content: invent nothing, don't drop anything the speaker said, no explanations, no judgement, no opinions.
6. Reply only with the cleaned-up note – no intro, no quotation marks, no markdown asterisks.`,
};

const FORMAT = {
  text: {
    de: `Form: Fließtext in ganzen Sätzen. Der Wortlaut des Sprechers bleibt so weit wie möglich erhalten – nur sauber. Wenn ein neues Thema beginnt, fang einen neuen Absatz an (Leerzeile). Keine Stichpunkte, keine Überschriften.`,
    en: `Format: running text in full sentences. Keep the speaker's wording as far as possible – just clean. Start a new paragraph (blank line) when a new topic begins. No bullet points, no headings.`,
  },
  bullets: {
    de: `Form: echte Stichpunkte. Jeder Stichpunkt beginnt mit "• ", ist kurz (meist 2–8 Wörter), enthält genau einen Gedanken und ist KEIN ganzer Satz: Artikel, Hilfsverben und Füllwörter weglassen, Pfeile (→) und Doppelpunkte benutzen. Zusammengehörige Unterpunkte mit zwei Leerzeichen und "– " darunter einrücken. Bei mehreren Themen darf vor jeder Gruppe eine kurze Überschrift (ohne Punkt, ohne "•") stehen.`,
    en: `Format: real bullet points. Each bullet starts with "• ", is short (usually 2–8 words), holds exactly one idea and is NOT a full sentence: drop articles, auxiliary verbs and filler, use arrows (→) and colons. Indent related sub-points underneath with two spaces and "– ". With several topics, each group may start with a short heading (no full stop, no "•").`,
  },
};

const EXAMPLE_IN = {
  de: `ähm also die Mitochondrien sind sind quasi das Kraftwerk der der Zelle die machen aus äh Glukose und Sauerstoff nein ich meine bei der Zellatmung entsteht ATP die Formel ist C6 H12 O6 plus sechs O2 und dann kommt sechs CO2 plus sechs H2O raus
und bei der Foto Synthese ist es genau andersrum die passiert in den Kloroplasten`,
  en: `um so the mitochondria are are basically the power house of the the cell they make uh from glucose and oxygen no I mean in cellular respiration ATP is made the formula is C6 H12 O6 plus six O2 and out comes six CO2 plus six H2O
and in photo since this it's exactly the other way round that happens in the chlorine plasts`,
};

const EXAMPLE_OUT = {
  text: {
    de: `Die Mitochondrien sind das Kraftwerk der Zelle. Bei der Zellatmung entsteht ATP. Die Formel ist C₆H₁₂O₆ + 6 O₂ → 6 CO₂ + 6 H₂O.

Bei der Photosynthese ist es genau andersherum. Sie findet in den Chloroplasten statt.`,
    en: `The mitochondria are the powerhouse of the cell. In cellular respiration, ATP is made. The formula is C₆H₁₂O₆ + 6 O₂ → 6 CO₂ + 6 H₂O.

In photosynthesis it's exactly the other way round. It happens in the chloroplasts.`,
  },
  bullets: {
    de: `Zellatmung
• Mitochondrien = Kraftwerk der Zelle
• Zellatmung → ATP
• C₆H₁₂O₆ + 6 O₂ → 6 CO₂ + 6 H₂O
Photosynthese
• Umkehrung der Zellatmung
• Ort: Chloroplasten`,
    en: `Cellular respiration
• Mitochondria = powerhouse of the cell
• Cellular respiration → ATP
• C₆H₁₂O₆ + 6 O₂ → 6 CO₂ + 6 H₂O
Photosynthesis
• Reverse of cellular respiration
• Location: chloroplasts`,
  },
};

// ---------------------------------------------------------------------------------------------

export default {
  async fetch(req, env) {
    const url = new URL(req.url);
    const authed = !!env.APP_TOKEN && req.headers.get("x-lumina") === env.APP_TOKEN;

    if (req.method === "GET" && url.pathname === "/health") {
      if (!authed) return json({ ok: false, reason: env.APP_TOKEN ? "forbidden" : "no_token", version: VERSION }, 403);
      return json(await health(env), 200);
    }
    if (req.method !== "POST") return json({ ok: true, error: "Lumina-KI läuft.", version: VERSION }, 200);
    if (!env.APP_TOKEN) return json({ fallback: true, reason: "no_token", detail: "Secret APP_TOKEN fehlt" }, 500);
    if (!authed) return json({ fallback: true, reason: "forbidden" }, 403);
    if (!env.AI) return json({ fallback: true, reason: "no_ai_binding", detail: "Binding AI fehlt" }, 500);
    if (!env.LIMITS) return json({ fallback: true, reason: "no_kv_binding", detail: "Binding LIMITS fehlt" }, 500);

    let body;
    try {
      body = await req.json();
    } catch {
      return json({ error: "bad request" }, 400);
    }
    if (body.mode === "chapters") return chapters(env, body);

    const text = String(body.text || "").slice(0, MAX_CHARS);
    const device = String(body.device || "").replace(/[^a-zA-Z0-9-]/g, "").slice(0, 64);
    const lang = body.lang === "en" ? "en" : "de";
    const mode = body.mode === "bullets" ? "bullets" : "text";
    if (!text.trim() || device.length < 8) return json({ error: "bad request" }, 400);

    const day = new Date().toISOString().slice(0, 10);
    const budgetKey = `budget:${day}`;
    const deviceKey = `dev:${day}:${device}`;
    let used = 0;
    let count = 0;
    try {
      [used, count] = await Promise.all([num(env, budgetKey), num(env, deviceKey)]);
    } catch (e) {
      // KV kaputt oder Limit erreicht: lieber ohne Zähler weiter als gar nicht
      used = 0;
      count = 0;
    }

    const tier = TIERS.find((t) => count < t.upTo);
    if (!tier) return json({ fallback: true, reason: "device_limit", left: 0 }, 429);

    // Kostenbremse: nur Modelle, die heute noch ins Budget passen
    const chain = tier.chain.filter((k) => used + MODELS[k].cost <= DAY_BUDGET);
    if (!chain.length) return json({ fallback: true, reason: "budget" }, 429);

    const messages = [
      { role: "system", content: RULES[lang] + "\n\n" + FORMAT[mode][lang] },
      { role: "user", content: EXAMPLE_IN[lang] },
      { role: "assistant", content: EXAMPLE_OUT[mode][lang] },
      { role: "user", content: text },
    ];
    const errors = [];
    for (const key of chain) {
      const model = MODELS[key];
      const out = tidyOutput(await runChat(env, model, messages, text.length, errors), mode);
      if (!out) continue;
      // Erst jetzt zählen: Fehlversuche kosten den Nutzer nichts
      const ttl = { expirationTtl: 60 * 60 * 48 };
      try {
        await Promise.all([
          env.LIMITS.put(budgetKey, String(used + model.cost), ttl),
          env.LIMITS.put(deviceKey, String(count + 1), ttl),
        ]);
      } catch (e) {
        errors.push("kv: " + short(e));
      }
      const last = TIERS[TIERS.length - 1].upTo;
      return json({
        text: out,
        model: model.id.split("/").pop(),
        tier: key,
        left: Math.max(0, last - count - 1),
        best: Math.max(0, TIERS[0].upTo - count - 1),
      }, 200);
    }
    return json({ fallback: true, reason: "ai_error", detail: errors.join(" | ").slice(0, 400) }, 502);
  },
};

/** Kapitel für ein Buch finden (mode = "chapters"). */
async function chapters(env, body) {
  const text = String(body.text || "").slice(0, MAX_CHAPTER_CHARS);
  const title = String(body.title || "").slice(0, 160);
  const device = String(body.device || "").replace(/[^a-zA-Z0-9-]/g, "").slice(0, 64);
  const lang = body.lang === "en" ? "en" : "de";
  if (!text.trim() || device.length < 8) return json({ error: "bad request" }, 400);

  const day = new Date().toISOString().slice(0, 10);
  const budgetKey = `budget:${day}`;
  const deviceKey = `chap:${day}:${device}`;
  let used = 0;
  let count = 0;
  try {
    [used, count] = await Promise.all([num(env, budgetKey), num(env, deviceKey)]);
  } catch (e) {
    used = 0;
    count = 0;
  }
  if (count >= CHAPTER_LIMIT) return json({ fallback: true, reason: "device_limit", left: 0 }, 429);
  const chain = CHAPTER_MODELS.filter((m) => used + m.cost <= DAY_BUDGET);
  if (!chain.length) return json({ fallback: true, reason: "budget" }, 429);

  const messages = [
    { role: "system", content: CHAPTER_RULES[lang] },
    { role: "user", content: (title ? `Buch/Book: ${title}\n\n` : "") + text },
  ];
  const errors = [];
  for (const m of chain) {
    const model = MODELS[m.key];
    const raw = await runChat(env, model, messages, 2400, errors);
    const lines = String(raw || "")
      .split("\n")
      .map((l) => l.replace(/^[\s*•-]+/, "").trim())
      .filter((l) => /^\d\s*\|\s*\d+\s*\|/.test(l))
      .slice(0, 80);
    if (!lines.length) {
      errors.push(`${model.id.split("/").pop()}: kein Format`);
      continue;
    }
    const ttl = { expirationTtl: 60 * 60 * 48 };
    try {
      await Promise.all([
        env.LIMITS.put(budgetKey, String(used + m.cost), ttl),
        env.LIMITS.put(deviceKey, String(count + 1), ttl),
      ]);
    } catch (e) {
      errors.push("kv: " + short(e));
    }
    return json({ kind: "chapters", text: lines.join("\n"), model: model.id.split("/").pop(), left: Math.max(0, CHAPTER_LIMIT - count - 1) }, 200);
  }
  return json({ fallback: true, reason: "ai_error", detail: errors.join(" | ").slice(0, 400) }, 502);
}

/**
 * Ein Modell im Chat-Format fragen. Probiert nacheinander ein paar Varianten der Anfrage, weil sich
 * die Schnittstellen bei Workers AI manchmal ändern. Fehler landen in [errors].
 */
async function runChat(env, model, messages, inputChars, errors) {
  const maxTokens = Math.min(4096, 900 + Math.ceil(inputChars / 2));
  const variants = [];
  if (model.reasoning) variants.push({ messages, max_tokens: maxTokens, reasoning_effort: "low" });
  variants.push({ messages, max_tokens: maxTokens });
  if (model.reasoning) {
    // Notfalls das Responses-Format, aber ohne Rollen im Verlauf (alles in einem Text)
    const rest = messages.slice(1);
    const last = rest.length ? rest[rest.length - 1].content : "";
    const shots = rest.slice(0, -1);
    const example = shots.length >= 2 ? `Beispiel/Example:\n${shots[0].content}\n\n→\n${shots[1].content}\n\n---\n\n` : "";
    variants.push({
      instructions: messages[0].content,
      input: example + last,
      reasoning: { effort: "low" },
      max_output_tokens: maxTokens,
    });
  }
  for (const payload of variants) {
    try {
      const res = await env.AI.run(model.id, payload);
      const out = extract(res);
      if (out && out.trim()) return out;
      errors.push(`${model.id.split("/").pop()}: leer`);
    } catch (e) {
      errors.push(`${model.id.split("/").pop()}: ${short(e)}`);
    }
  }
  return "";
}

/** Prüft jedes Modell mit einem Mini-Text. */
async function health(env) {
  const report = { ok: true, version: VERSION, ai: !!env.AI, kv: !!env.LIMITS, models: {} };
  if (!env.AI) return { ...report, ok: false };
  const messages = [
    { role: "system", content: "Antworte nur mit dem korrigierten Satz." },
    { role: "user", content: "das ist ein ein test" },
  ];
  for (const [key, model] of Object.entries(MODELS)) {
    const errors = [];
    const t0 = Date.now();
    const out = await runChat(env, model, messages, 40, errors);
    report.models[key] = { id: model.id, ok: !!out, ms: Date.now() - t0, error: out ? undefined : errors.join(" | ").slice(0, 200) };
  }
  report.ok = Object.values(report.models).some((m) => m.ok);
  return report;
}

async function num(env, key) {
  const v = await env.LIMITS.get(key);
  return v ? parseInt(v, 10) || 0 : 0;
}

function short(e) {
  return String((e && e.message) || e).replace(/\s+/g, " ").slice(0, 160);
}

// Kleine Nacharbeit: Markdown-Sternchen weg, Aufzählungszeichen vereinheitlichen.
function tidyOutput(s, mode) {
  let t = String(s || "").trim().replace(/\*\*(.+?)\*\*/g, "$1").replace(/^#+\s*/gm, "");
  if (mode === "bullets") t = t.replace(/^[-*]\s+/gm, "• ");
  return t.trim();
}

// Die Modelle antworten je nach Format etwas anders – hier wird der Text herausgesucht.
function extract(res) {
  if (!res) return "";
  if (typeof res === "string") return res;
  if (typeof res.response === "string") return res.response;
  if (res.result && typeof res.result.response === "string") return res.result.response;
  if (typeof res.output_text === "string") return res.output_text;
  if (res.choices && res.choices[0]) {
    const c = res.choices[0];
    if (c.message && typeof c.message.content === "string") return c.message.content;
    if (typeof c.text === "string") return c.text;
  }
  if (Array.isArray(res.output)) {
    const parts = [];
    for (const item of res.output) {
      if (item && item.type === "message" && Array.isArray(item.content)) {
        for (const c of item.content) if (c && typeof c.text === "string") parts.push(c.text);
      }
    }
    if (parts.length) return parts.join("\n");
  }
  return "";
}

function json(obj, status) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { "content-type": "application/json; charset=utf-8" },
  });
}
