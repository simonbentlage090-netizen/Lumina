"""Neue Körperform (8.1): echte A-Glühbirne. Kugel, langer Hals, hohes E27-Gewinde.
Gesicht, Arme, Hände und Buch bleiben in den alten Koordinaten und werden mit U() eingepasst."""
import math, sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L
F, Lz = L.F, L.Lz
INK = "#1E1A26"; BULB = "#FFC93C"; SHADOW = "#F2A324"; LIGHT = "#FFE27A"
METAL = "#CDD2DE"; METAL_D = "#9AA1B3"; METAL_L = "#EEF0F6"; ISO = "#2A2533"

C0 = 1.60            # alte Kugelmitte (Gesicht, Arme, Buch sind darauf ausgelegt)
CZ = 1.73            # neue Kugelmitte
R = 0.67             # neuer Kugelradius
K = R / 0.74         # Maßstab alt -> neu
JOIN = -30           # hier geht die Kugel in den Hals über (Grad)
NECK_W = 0.37        # halbe Breite am Halsende
NECK_Z = 0.78        # hier beginnt das Gewinde
TIP_Z = 0.12         # untere Spitze

def U(pts):
    return [(x * K, CZ + (z - C0) * K) for x, z in pts]
def Ushape(s):
    out = dict(s); out["pts"] = U(s["pts"])
    if s["kind"] == "line": out["width"] = s["width"] * K
    return out

def outline(join=JOIN, a=0.38):
    """Kugel oben, dann ein weich geschwungener, sich verjüngender Hals bis zum Gewinde."""
    t = math.radians(join)
    J = (R * math.cos(t), CZ + R * math.sin(t))
    tan = (-abs(math.sin(t)), -abs(math.cos(t)))   # entlang der Kugel nach unten innen
    neck = L.cubic(J, (J[0] + a * tan[0], J[1] + a * tan[1]), (NECK_W - 0.01, NECK_Z + 0.22), (NECK_W, NECK_Z), 28)
    arc = L.earc(0, CZ, R, R, join, 180 - join, 90)
    right_up = neck[::-1]
    left_down = [(-x, z) for x, z in neck]
    return right_up + arc[1:-1] + left_down

def crescent(seg, width, toward):
    return L.crescent(seg, width, toward)

def glass_shapes():
    out = outline()
    s = [F(out, BULB)]
    s.append(F(crescent(L.earc(0, CZ, R, R, -34, 72, 36), 0.21 * K, (-0.15 * K, CZ + 0.12 * K)), SHADOW))
    s.append(F(crescent(L.earc(0, CZ, R, R, 112, 200, 30), 0.13 * K, (0.15 * K, CZ - 0.08 * K)), LIGHT))
    s.append(Lz(L.earc(0, CZ, 0.60 * K, 0.60 * K, 148, 194, 16), 0.048 * K, "#FFFFFF"))
    s.append(F(L.ellipse(-0.29 * K, CZ + 0.50 * K, 0.035 * K, 0.035 * K, 20), "#FFFFFF"))
    s.append(Lz(out, 0.032, closed=True))
    return s

def base_shapes(n_th=4):
    s = []
    z0, z1 = NECK_Z + 0.02, 0.33          # Gewinde oben/unten
    def half_w(z):
        t = (z0 - z) / (z0 - z1)
        base = 0.355 - 0.03 * t
        return base + 0.024 * abs(math.sin(math.pi * t * n_th))
    right = [(half_w(z0 - (z0 - z1) * i / 80), z0 - (z0 - z1) * i / 80) for i in range(81)]
    bot = L.quad((right[-1][0], z1), (right[-1][0] * 0.9, z1 - 0.05), (0.0, z1 - 0.055), 8)[1:]
    rf = right + bot
    shell = rf + [(-x, z) for x, z in reversed(rf)][1:]
    s.append(F(shell, METAL))
    s.append(F(right + [(x - 0.11, z) for x, z in reversed(right)], METAL_D, 0.8))
    s.append(F([(-0.27, z0 - 0.02), (-0.18, z0 - 0.02), (-0.16, z1 + 0.01), (-0.24, z1 + 0.01)], METAL_L, 0.9))
    for i in range(1, n_th):
        zl = z0 - (z0 - z1) * i / n_th
        wl = half_w(zl) - 0.015
        s.append(Lz(L.quad((-wl, zl - 0.016), (0.0, zl - 0.05), (wl, zl + 0.016), 16), 0.02, "#6E7487"))
    s.append(Lz(shell, 0.028, closed=True))
    # Kragen am Glas
    col = L.rrect(0, NECK_Z + 0.015, 0.80, 0.075, 0.035)
    s += [F(col, METAL_L), F(L.rrect(0.20, NECK_Z + 0.015, 0.30, 0.075, 0.03), METAL),
          F(L.rrect(-0.14, NECK_Z + 0.028, 0.32, 0.018, 0.009), "#FFFFFF"), Lz(col, 0.028, closed=True)]
    # schwarzes Isolierglas und Kontakt
    zi = z1 - 0.045
    iso = L.quad((0.25, zi), (0.23, zi - 0.06), (0.14, zi - 0.075), 8) + L.quad((-0.14, zi - 0.075), (-0.23, zi - 0.06), (-0.25, zi), 8)
    tip = L.rrect(0, TIP_Z + 0.04, 0.15, 0.08, 0.035)
    s += [F(tip, METAL_D), Lz(tip, 0.026, closed=True), F(iso, ISO), Lz(iso, 0.026, closed=True)]
    return s

def glow():
    return [F(L.ellipse(0, CZ, r * K, r * K, 96), "#FFD25C", 0.055) for r in (1.16, 1.04, 0.94)]

def body():
    return glass_shapes() + base_shapes()
