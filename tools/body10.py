"""Körperform seit 8.2: Glühbirne nach dem Foto.
Von unten nach oben: spitzer Kontakt, schwarzer Isolier-Kegel, Gewinde, das nach oben leicht breiter wird,
Kragen, das Glas weitet sich kurz, zieht sich zur Taille zusammen und formt dann die runde Kugel.
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
CZ = 1.73            # Kugelmitte
R = 0.67             # Kugelradius
K = R / 0.74         # Maßstab alt -> neu
JOIN = -33           # hier geht die Kugel in den Hals über (Grad)
WAIST_W, WAIST_Z = 0.300, 0.985   # schmalste Stelle des Glases
FLARE_W = 0.350      # unten weitet sich das Glas wieder bis zum Kragen
NECK_Z = 0.80        # Kragen / Beginn des Gewindes
SCREW_TOP_W, SCREW_BOT_W = 0.360, 0.292   # Gewinde wird nach unten schmaler
SCREW_BOT = 0.375
TIP_Z = 0.12         # untere Spitze

def U(pts):
    return [(x * K, CZ + (z - C0) * K) for x, z in pts]
def Ushape(s):
    out = dict(s); out["pts"] = U(s["pts"])
    if s["kind"] == "line": out["width"] = s["width"] * K
    return out

def outline():
    """Kugel oben, Hals mit Taille, unten ein kleines Ausweiten bis zum Kragen."""
    t = math.radians(JOIN)
    J = (R * math.cos(t), CZ + R * math.sin(t))
    tan = (-abs(math.sin(t)), -abs(math.cos(t)))          # entlang der Kugel nach unten innen
    a = 0.30
    upper = L.cubic(J, (J[0] + a * tan[0], J[1] + a * tan[1]), (WAIST_W, WAIST_Z + 0.16), (WAIST_W, WAIST_Z), 26)
    lower = L.cubic((WAIST_W, WAIST_Z), (WAIST_W, WAIST_Z - 0.09), (FLARE_W - 0.012, NECK_Z + 0.08), (FLARE_W, NECK_Z + 0.015), 14)
    neck = upper + lower[1:]                                # oben -> unten (rechte Seite)
    arc = L.earc(0, CZ, R, R, JOIN, 180 - JOIN, 90)
    return neck[::-1] + arc[1:-1] + [(-x, z) for x, z in neck]

def glass_shapes(col=ON):
    out = outline()
    s = [F(out, col["fill"])]
    s.append(F(L.crescent(L.earc(0, CZ, R, R, -34, 72, 36), 0.21 * K, (-0.15 * K, CZ + 0.12 * K)), col["shadow"]))
    s.append(F(L.crescent(L.earc(0, CZ, R, R, 112, 200, 30), 0.13 * K, (0.15 * K, CZ - 0.08 * K)), col["light"]))
    s.append(Lz(L.earc(0, CZ, 0.60 * K, 0.60 * K, 148, 194, 16), 0.048 * K, "#FFFFFF"))
    s.append(F(L.ellipse(-0.29 * K, CZ + 0.50 * K, 0.035 * K, 0.035 * K, 20), "#FFFFFF"))
    # Schatten an der Taille, damit die Form auch ohne Kontur lesbar bleibt
    s.append(F([(WAIST_W - 0.004, WAIST_Z + 0.14), (WAIST_W - 0.004, WAIST_Z - 0.10),
                (WAIST_W - 0.075, WAIST_Z - 0.08), (WAIST_W - 0.07, WAIST_Z + 0.12)], col["shadow"], 0.85))
    s.append(Lz(out, 0.032, closed=True))
    return s

def base_shapes(n_th=4):
    s = []
    z0, z1 = NECK_Z + 0.01, SCREW_BOT          # Gewinde oben/unten
    def half_w(z):
        t = (z0 - z) / (z0 - z1)
        base = SCREW_TOP_W + (SCREW_BOT_W - SCREW_TOP_W) * t
        return base + 0.022 * abs(math.sin(math.pi * t * n_th))
    right = [(half_w(z0 - (z0 - z1) * i / 80), z0 - (z0 - z1) * i / 80) for i in range(81)]
    bot = L.quad((right[-1][0], z1), (right[-1][0] * 0.92, z1 - 0.035), (0.0, z1 - 0.04), 8)[1:]
    rf = right + bot
    shell = rf + [(-x, z) for x, z in reversed(rf)][1:]
    # schwarzer Isolier-Kegel und spitzer Kontakt (unter dem Gewinde, wird von ihm überdeckt)
    zi = z1 + 0.01
    iso = (L.quad((0.255, zi), (0.235, zi - 0.07), (0.125, zi - 0.115), 10)
           + L.quad((-0.125, zi - 0.115), (-0.235, zi - 0.07), (-0.255, zi), 10))
    zt = zi - 0.105
    tip = L.quad((0.115, zt), (0.10, TIP_Z + 0.02), (0.0, TIP_Z), 12) + L.quad((0.0, TIP_Z), (-0.10, TIP_Z + 0.02), (-0.115, zt), 12)[1:]
    s += [F(tip, METAL_D), F([(0.02, zt - 0.005), (0.07, zt - 0.005), (0.035, TIP_Z + 0.03), (0.012, TIP_Z + 0.03)], METAL_L, 0.8),
          Lz(tip, 0.026, closed=True)]
    s += [F(iso, ISO), Lz(iso, 0.026, closed=True)]
    s.append(F(shell, METAL))
    s.append(F(right + [(x - 0.10, z) for x, z in reversed(right)], METAL_D, 0.8))
    s.append(F([(-0.26, z0 - 0.02), (-0.17, z0 - 0.02), (-0.15, z1 + 0.01), (-0.21, z1 + 0.01)], METAL_L, 0.9))
    for i in range(1, n_th):
        zl = z0 - (z0 - z1) * i / n_th
        wl = half_w(zl) - 0.015
        s.append(Lz(L.quad((-wl, zl - 0.016), (0.0, zl - 0.05), (wl, zl + 0.016), 16), 0.02, "#6E7487"))
    s.append(Lz(shell, 0.028, closed=True))
    # Kragen am Glas
    col = L.rrect(0, NECK_Z + 0.015, 0.78, 0.07, 0.033)
    s += [F(col, METAL_L), F(L.rrect(0.20, NECK_Z + 0.015, 0.30, 0.07, 0.03), METAL),
          F(L.rrect(-0.14, NECK_Z + 0.027, 0.32, 0.017, 0.008), "#FFFFFF"), Lz(col, 0.028, closed=True)]
    return s

# Glühdraht (zeichnet die App): zwei Haltedrähte aus dem Hals, dazwischen eine kleine Wendel.
FIL_STEM_Z = NECK_Z + 0.05        # unten im Hals
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
