import os, re, sys
# Erzeugt R.kt (de.funke.app.R) aus app/src/main/res – Ersatz für den Android-Ressourcen-Compiler.
res = sys.argv[1]; out = sys.argv[2]
types = {}
def add(t, n):
    n = n.replace(".", "_")
    types.setdefault(t, set()).add(n)
for d in sorted(os.listdir(res)):
    full = os.path.join(res, d)
    if not os.path.isdir(full): continue
    base = d.split("-")[0]
    for f in os.listdir(full):
        name = f.split(".")[0]
        if base in ("drawable", "mipmap", "layout", "font", "raw", "xml", "interpolator", "anim", "animator", "menu", "color") and base != "values":
            add(base, name)
        p = os.path.join(full, f)
        if f.endswith(".xml"):
            txt = open(p, encoding="utf-8", errors="ignore").read()
            for m in re.finditer(r'@\+id/([A-Za-z0-9_]+)', txt): add("id", m.group(1))
            if base == "values":
                for m in re.finditer(r'<(string|color|dimen|style|bool|integer|array|string-array|integer-array|plurals|attr|declare-styleable)\s+name="([^"]+)"', txt):
                    t = m.group(1)
                    t = {"string-array": "array", "integer-array": "array", "declare-styleable": "styleable"}.get(t, t)
                    add(t, m.group(2))
lines = ["package de.funke.app", "", "object R {"]
i = 0x7f010000
for t in sorted(types):
    lines.append(f"    object {t} {{")
    for n in sorted(types[t]):
        lines.append(f"        const val {n}: Int = {i}"); i += 1
    lines.append("    }")
lines.append("}")
open(out, "w").write("\n".join(lines) + "\n")
print("R.kt:", sum(len(v) for v in types.values()), "Einträge")
