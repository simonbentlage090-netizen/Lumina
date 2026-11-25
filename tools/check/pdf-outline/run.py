import json, subprocess, sys, re
d = json.load(open("pdfs/cases.json"))
cases, exp = d["cases"], d["expected"]
args = []
for k, p in cases.items():
    e = exp[k]
    args += [p, str(e.get("pages", 40) or 40)]
out = subprocess.run(["java", "-jar", "outline.jar"] + args, capture_output=True, text=True).stdout
got = {}
cur = None
for line in out.splitlines():
    if line.startswith("== "):
        name, ms = line[3:].rsplit(" ", 1)
        cur = name; got[cur] = {"ms": ms, "entries": []}
    elif line == "NULL":
        got[cur]["entries"] = None
    else:
        lv, pg, t = line.split("\t", 2)
        got[cur]["entries"].append([int(lv), int(pg), t])

def tidy_expected(entries, pages):
    """Erwartung nach denselben Regeln wie PdfOutline.tidy (ohne Ziel → Seite des nächsten Eintrags)."""
    if entries is None: return None
    fixed = []
    for i, (lv, pg, t) in enumerate(entries):
        if pg is None or pg < 0:
            nxt = next((e[1] for e in entries[i+1:] if e[1] is not None and e[1] >= 0), None)
            if nxt is None: continue
            pg = nxt
        fixed.append([lv, max(0, min(pg, pages - 1)), t])
    if len(fixed) < 2: return None
    return fixed

ok = 0
for k, p in cases.items():
    name = p.split("/")[-1]
    g = got.get(name)
    e = exp[k]
    if "error" in e:
        print(f"?? {k}: pypdf error {e['error']}"); continue
    want = tidy_expected(e["entries"], e["pages"]) if e["entries"] is not None else None
    have = g["entries"] if g else "MISSING"
    if k == "incremental" and want:
        want[0][2] = "Neuer Titel nach Update"
    if want == have:
        ok += 1
        print(f"OK  {k:28s} {g['ms']:>6s}  {0 if have is None else len(have)} Einträge")
    else:
        print(f"ERR {k:28s} {g['ms'] if g else ''}")
        print("   want:", (want or [])[:6] if want else want)
        print("   have:", (have or [])[:6] if isinstance(have, list) else have)
print(f"{ok}/{len(cases)}")
