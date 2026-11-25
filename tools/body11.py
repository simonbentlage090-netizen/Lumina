"""Körperform seit 8.3: Glühbirne, direkt am Referenzfoto abgemessen (siehe PHOTO_NECK).
Von unten nach oben: kleiner spitzer Kontakt, schwarzer Isolierring, Gewinde, Fassung. Über der Fassung ist das
Glas gleich etwas breiter als das Gewinde und läuft dann als breiter Hals, der sich sanft weitet, in die runde Kugel.
Gesicht, Arme, Hände und Buch bleiben in den alten Koordinaten und werden mit U() eingepasst (wie in 8.1).

Dazu gibt es die Kugel "aus" (blasses Glas), über die die App beim Abbrechen überblendet,
und den Glühdraht, den die App selbst zeichnet (Koordinaten in FILAMENT, gleiche Zahlen wie in LumiArt.kt)."""
import math, sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L
F, Lz = L.F, L.Lz
INK = "#1E1A26"
ON = dict(fill="#FFC93C", shadow="#F2A324", light="#FFE27A")
OFF = dict(fill="#E4E3EC", shadow="#C3C1D3", light="#F7F6FB")   # Licht aus: blasses, leicht kühles Glas
METAL = "#CDD2DE"; METAL_D = "#9AA1B3"; METAL_L = "#EEF0F6"; ISO = "#2A2533"

C0 = 1.60            # alte Kugelmitte (Gesicht, Arme, Buch sind darauf ausgelegt)
CZ = 1.75            # Kugelmitte
R = 0.67             # Kugelradius
K = R / 0.74         # Maßstab alt -> neu
NECK_Z = 0.745       # Fassung (Metallrand) oben
SCREW_W = 0.325      # halbe Breite des Gewindes (Foto: 0,485 R)
SCREW_BOT = 0.38
TIP_Z = 0.13         # untere Spitze

# Umriss des Glases, direkt vom Referenzfoto abgemessen (Klare Glühbirne, Kugel R = 171 px):
# (Tiefe unter der Kugelmitte, halbe Breite), beides in Vielfachen von R.
# Ab 0,5 R unter der Mitte verlässt das Glas den Kreis und läuft als breiter, sanft enger werdender Hals
# zur Fassung. Direkt über der Fassung ist das Glas etwas breiter als das Gewinde ("geht nach außen").
PHOTO_NECK = [
    (0.526, 0.865), (0.643, 0.801), (0.760, 0.749), (0.877, 0.705), (0.994, 0.670),
    (1.111, 0.643), (1.228, 0.617), (1.345, 0.605), (1.404, 0.597), (1.462, 0.582),
]
NECK_SQUEEZE = 0.92  # der Hals ist etwas kürzer als beim Foto, damit Lumi ins Bild passt

def U(pts):
    return [(x * K, CZ + (z - C0) * K) for x, z in pts]
def Ushape(s):
    out = dict(s); out["pts"] = U(s["pts"])
    if s["kind"] == "line": out["width"] = s["width"] * K
    return out

def _depth(d):
    return 0.5 + (d - 0.5) * NECK_SQUEEZE

def catmull(pts, n=8):
    """glatte Kurve durch alle Punkte"""
    out = []
    P = [pts[0]] + pts + [pts[-1]]
    for i in range(1, len(P) - 2):
        p0, p1, p2, p3 = P[i - 1], P[i], P[i + 1], P[i + 2]
        for k in range(n):
            t = k / n
            t2, t3 = t * t, t * t * t
            out.append(tuple(0.5 * ((2 * p1[j]) + (-p0[j] + p2[j]) * t + (2 * p0[j] - 5 * p1[j] + 4 * p2[j] - p3[j]) * t2
                                     + (-p0[j] + 3 * p1[j] - 3 * p2[j] + p3[j]) * t3) for j in range(2)))
    out.append(pts[-1])
    return out

def neck_right():
    """rechte Halsseite von oben (Übergang zur Kugel) nach unten (Fassung)."""
    t0 = math.degrees(math.asin(-0.5))           # -30 Grad: hier verlässt das Glas die Kugel
    j = (R * math.cos(math.radians(t0)), CZ + R * math.sin(math.radians(t0)))
    pts = [j] + [(w * R, CZ - _depth(d) * R) for d, w in PHOTO_NECK]
    # unten rundet sich das Glas in die Fassung hinein
    last = pts[-1]
    pts += [(last[0] - 0.012, last[1] - 0.035), (0.355, NECK_Z + 0.012)]
    return catmull(pts, 8)

def outline():
    neck = neck_right()
    arc = L.earc(0, CZ, R, R, -30, 210, 90)
    return neck[::-1] + arc[1:-1] + [(-x, z) for x, z in neck]

def glass_shapes(col=ON):
    out = outline()
    s = [F(out, col["fill"])]
    s.append(F(L.crescent(L.earc(0, CZ, R, R, -34, 72, 36), 0.21 * K, (-0.15 * K, CZ + 0.12 * K)), col["shadow"]))
    s.append(F(L.crescent(L.earc(0, CZ, R, R, 112, 200, 30), 0.13 * K, (0.15 * K, CZ - 0.08 * K)), col["light"]))
    s.append(Lz(L.earc(0, CZ, 0.60 * K, 0.60 * K, 148, 194, 16), 0.048 * K, "#FFFFFF"))
    s.append(F(L.ellipse(-0.29 * K, CZ + 0.50 * K, 0.035 * K, 0.035 * K, 20), "#FFFFFF"))
    # leichter Schatten an der rechten Halsseite, damit der Hals rund wirkt
    nr = neck_right()
    inner = [(x - 0.07 - 0.03 * (1 - (z - NECK_Z) / (CZ - NECK_Z)), z) for x, z in nr]
    s.append(F(nr + inner[::-1], col["shadow"], 0.55))
    s.append(Lz(out, 0.032, closed=True))
    return s

def base_shapes(n_th=4):
    s = []
    z0, z1 = NECK_Z - 0.01, SCREW_BOT          # Gewinde oben/unten
    def half_w(z):
        t = (z0 - z) / (z0 - z1)
        return SCREW_W - 0.008 * t + 0.02 * abs(math.sin(math.pi * t * n_th))
    right = [(half_w(z0 - (z0 - z1) * i / 80), z0 - (z0 - z1) * i / 80) for i in range(81)]
    # unten rundet sich das Gewinde ab (wie auf dem Foto)
    bot = L.quad((right[-1][0], z1), (right[-1][0] * 0.98, z1 - 0.045), (0.215, z1 - 0.055), 8)[1:]
    rf = right + bot
    shell = rf + [(-x, z) for x, z in reversed(rf)][1:]
    # schwarzer Isolierring und kleiner Kontakt, der spitz zuläuft
    zi = z1 - 0.045
    iso = (L.quad((0.215, zi), (0.20, zi - 0.075), (0.095, zi - 0.11), 10)
           + L.quad((-0.095, zi - 0.11), (-0.20, zi - 0.075), (-0.215, zi), 10))
    zt = zi - 0.10
    tip = L.quad((0.09, zt), (0.075, TIP_Z + 0.015), (0.0, TIP_Z), 12) + L.quad((0.0, TIP_Z), (-0.075, TIP_Z + 0.015), (-0.09, zt), 12)[1:]
    s += [F(tip, METAL_D), F([(0.015, zt - 0.005), (0.05, zt - 0.005), (0.026, TIP_Z + 0.03), (0.01, TIP_Z + 0.03)], METAL_L, 0.8),
          Lz(tip, 0.026, closed=True)]
    s += [F(iso, ISO), Lz(iso, 0.026, closed=True)]
    s.append(F(shell, METAL))
    s.append(F(right + [(x - 0.10, z) for x, z in reversed(right)], METAL_D, 0.8))
    s.append(F([(-0.24, z0 - 0.02), (-0.16, z0 - 0.02), (-0.14, z1 + 0.01), (-0.20, z1 + 0.01)], METAL_L, 0.9))
    for i in range(1, n_th):
        zl = z0 - (z0 - z1) * i / n_th
        wl = half_w(zl) - 0.015
        s.append(Lz(L.quad((-wl, zl - 0.016), (0.0, zl - 0.05), (wl, zl + 0.016), 16), 0.02, "#6E7487"))
    s.append(Lz(shell, 0.028, closed=True))
    # Fassung (Metallrand), auf dem das Glas sitzt
    col = L.rrect(0, NECK_Z + 0.005, 0.74, 0.065, 0.03)
    s += [F(col, METAL_L), F(L.rrect(0.19, NECK_Z + 0.005, 0.28, 0.065, 0.028), METAL),
          F(L.rrect(-0.13, NECK_Z + 0.016, 0.30, 0.016, 0.008), "#FFFFFF"), Lz(col, 0.028, closed=True)]
    return s

# Glühdraht (zeichnet die App): zwei Haltedrähte aus dem Hals, dazwischen eine kleine Wendel.
FIL_STEM_Z = NECK_Z + 0.06        # unten im Hals
FIL_Z = 1.30                       # Höhe der Wendel
FIL_X = 0.13                       # halbe Breite der Wendel
FIL_COILS = 5

def filament_lines():
    pts_l = [(-0.05, FIL_STEM_Z), (-0.06, FIL_Z - 0.18), (-FIL_X, FIL_Z)]
    pts_r = [(0.05, FIL_STEM_Z), (0.06, FIL_Z - 0.18), (FIL_X, FIL_Z)]
    coil = []
    n = FIL_COILS * 12
    for i in range(n + 1):
        u = i / n
        x = -FIL_X + 2 * FIL_X * u
        coil.append((x, FIL_Z + 0.028 * math.sin(2 * math.pi * FIL_COILS * u) + 0.02 * math.sin(math.pi * u)))
    return [pts_l, pts_r, coil]

def glow():
    return [F(L.ellipse(0, CZ, r * K, r * K, 96), "#FFD25C", 0.055) for r in (1.16, 1.04, 0.94)]

def body(col=ON):
    return glass_shapes(col) + base_shapes()

def glass_off():
    """Nur das Glas im ausgeschalteten Zustand (liegt in der App über dem leuchtenden Kolben)."""
    return glass_shapes(OFF)
