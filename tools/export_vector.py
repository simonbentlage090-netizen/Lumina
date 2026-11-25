"""Erzeugt Lumis feste Teile als Android-Vektorgrafiken (res/drawable).
Aufruf: python3 export_vector.py ../app/src/main/res/drawable"""
import os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L, body11 as B, book_proto as BP

X0, Z1, SPAN = -1.40, 2.80, 2.80   # Sichtfenster: x -1.4..1.4, z 0..2.8
S = 100.0
LIFT = 0.18                         # Arme, Hände und Buch sitzen so viel höher als das Gesicht (wie HOLD_LIFT in LumiArt.kt)
def tx(x): return (x - X0) * S
def ty(z): return (Z1 - z) * S
def path_d(pts, closed):
    d = "M%.2f,%.2f" % (tx(pts[0][0]), ty(pts[0][1]))
    d += "".join(" L%.2f,%.2f" % (tx(x), ty(z)) for x, z in pts[1:])
    return d + (" Z" if closed else "")
def vector(shapes):
    out = ['<?xml version="1.0" encoding="utf-8"?>',
           '<!-- Lumi, automatisch erzeugt mit tools/export_vector.py. Nicht von Hand bearbeiten. -->',
           '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
           '    android:width="280dp" android:height="280dp"',
           '    android:viewportWidth="%d" android:viewportHeight="%d">' % (SPAN * S, SPAN * S)]
    for s in shapes:
        if s["kind"] == "fill":
            out.append('    <path android:fillColor="%s" android:fillAlpha="%.3f" android:pathData="%s" />'
                       % (s["color"], s["alpha"], path_d(s["pts"], True)))
        else:
            out.append('    <path android:fillColor="#00000000" android:strokeColor="%s" android:strokeWidth="%.2f"'
                       ' android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="%s" />'
                       % (s["color"], s["width"] * S, path_d(s["pts"], s.get("closed", False))))
    out.append('</vector>')
    return "\n".join(out) + "\n"
def held(s):
    """Buch aus den alten Koordinaten in die neue Körperform einpassen (wie inHold in LumiArt.kt)."""
    o = dict(s); o["pts"] = [(x * B.K, B.CZ + (z - B.C0) * B.K + LIFT) for x, z in s["pts"]]
    if s["kind"] == "line": o["width"] = s["width"] * B.K
    return o

dst = sys.argv[1] if len(sys.argv) > 1 else "."
P = L.parts()
files = {
    "lumi_glow": B.glow(),
    "lumi_body": B.body(B.ON),
    "lumi_body_off": B.body(B.OFF),
    "lumi_book": [held(s) for s in P["book"]],
    "lumi_book_down": BP.lying_book(0.74, 0.13),
}
for name, shapes in files.items():
    open(os.path.join(dst, name + ".xml"), "w").write(vector(shapes))
    print(name, len(shapes))
