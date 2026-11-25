import sys, os, re
# Zerlegt eine Bündel-Datei mit Zeilen "//// FILE: pfad" in einzelne Dateien.
src, outdir = sys.argv[1], sys.argv[2]
cur = None; buf = []
def flush():
    if cur:
        p = os.path.join(outdir, cur)
        os.makedirs(os.path.dirname(p), exist_ok=True)
        with open(p, "w") as f: f.write("".join(buf))
for line in open(src):
    m = re.match(r"^//// FILE: (\S+)", line)
    if m:
        flush(); cur = m.group(1); buf = []
    else:
        buf.append(line)
flush()
