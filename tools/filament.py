"""Glühdraht-Varianten für Lumi B (nur Vorschau). Koordinaten wie body11 (R 0.67, CZ 1.75, NECK_Z 0.745).
Alles liegt unter dem Mund (Mund bei z ≈ 1.58), damit der Draht nicht wie ein zweiter Mund wirkt."""
import math
import lumi_shapes as L, body11

WIRE = (0x8E, 0x8A, 0x9E)          # Drähte im ausgeschalteten Zustand
WIRE_D = (0x6E, 0x6A, 0x7E)
GLASS = (0xFF, 0xFF, 0xFF)          # Glasfuß
GLASS_EDGE = (0xA6, 0xA3, 0xB8)
HOT = (0xFF, 0xF4, 0xC2)
HALO = (0xFF, 0xD2, 0x5C)
NZ = 0.745

def mixc(a, b, t): return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))

def fil_col(light):
    """Farbe des eigentlichen Glühfadens: grau -> heiß (fast weiß)"""
    return mixc(WIRE_D, (0xFF, 0xFD, 0xEC), min(1.0, light * 2.2))

def halo(cv, pts, light, w=0.10):
    if light <= 0.03: return
    k_ = min(1.0, light * 2.2)
    for k, a, c in ((2.8, 0.16, (0xFF, 0xA8, 0x30)), (1.8, 0.26, HALO), (1.1, 0.45, (0xFF, 0xE9, 0x9A))):
        cv.line(pts, w * k * 0.5, c, a=a * k_)

def glass_stem(cv, top=1.16, button=None):
    """Glasfuß wie beim Foto: unten breiter Kelch, dann ein dünner Glasstab."""
    z0 = NZ + 0.035
    right = (L.quad((0.17, z0), (0.13, z0 + 0.06), (0.06, z0 + 0.13), 10)
             + L.quad((0.06, z0 + 0.13), (0.035, z0 + 0.17), (0.032, z0 + 0.24), 6)[1:]
             + [(0.026, top - 0.01)])
    shape = right + [(0.0, top + 0.005)] + [(-x, z) for x, z in reversed(right)]
    cv.poly(shape, GLASS, 0.40)
    cv.line(right, 0.013, GLASS_EDGE, a=0.9); cv.line([(-x, z) for x, z in right], 0.013, GLASS_EDGE, a=0.9)
    cv.line([(0.010, z0 + 0.20), (0.010, top - 0.04)], 0.010, (255, 255, 255), a=0.8)   # Glanz
    if button:
        bz = button
        cv.ell(0, bz, 0.048, 0.034, GLASS, 0.75)
        cv.line(L.ellipse(0, bz, 0.048, 0.034, 24), 0.012, GLASS_EDGE, a=0.9, closed=True)

# ------------------------------------------------------------------------------------------
def draw_jetzt(cv, light):
    """so wie 8.3 (zum Vergleich)"""
    hot = mixc(WIRE_D, HOT, light * 1.6 if light < 0.6 else 1)
    if 0.05 < light < 1:
        cv.ell(0, body11.FIL_Z, 0.23, 0.10, HOT, 0.45 * light)
    for pts in body11.filament_lines():
        cv.line(pts, 0.024, hot, a=1 - 0.4 * light)

def draw_klassisch(cv, light):
    """wie die klare Glühbirne auf dem Foto: Glasfuß, zwei Drähte als V, oben ein feiner Faden im Zickzack"""
    glass_stem(cv, top=1.30, button=1.12)
    zt = 1.43
    ends = (0.205, zt)
    for sx in (-1, 1):
        cv.line(L.quad((sx * 0.030, 1.03), (sx * 0.10, 1.20), (sx * ends[0], ends[1]), 14), 0.017, WIRE)
        cv.line([(0.0, 1.29), (sx * 0.075, 1.40)], 0.011, WIRE, a=0.9)   # kleine Halter
    fil = [(-0.205, zt), (-0.14, 1.392), (-0.075, 1.412), (0.0, 1.388), (0.075, 1.412), (0.14, 1.392), (0.205, zt)]
    fil = L.cubic(fil[0], fil[1], fil[2], fil[3], 10) + L.cubic(fil[3], fil[4], fil[5], fil[6], 10)[1:]
    halo(cv, fil, light, 0.12)
    cv.line(fil, 0.020, fil_col(light))

def draw_spirale(cv, light):
    """wie die Spiral-Birne auf dem Foto: eine kleine stehende Spirale um einen Haltestab"""
    glass_stem(cv, top=1.02)
    z0, z1, r, turns = 1.04, 1.42, 0.092, 3.5
    pts = []
    n = 220
    for i in range(n + 1):
        t = i / n * turns * 2 * math.pi
        zc = z0 + (z1 - z0) * i / n
        pts.append((r * math.sin(t), zc + 0.024 * math.cos(t), math.cos(t)))
    cv.line([(0, 1.0), (0, z1 + 0.01)], 0.013, WIRE, a=0.9)                         # Haltestab
    col = fil_col(light)
    halo(cv, [(x, z) for x, z, _ in pts], light, 0.13)
    # hinten (dünner, blasser), dann vorne
    seg = []
    for x, z, c in pts:
        if c < 0: seg.append((x, z))
        elif seg:
            if len(seg) > 1: cv.line(seg, 0.013, mixc(col, (0xB8, 0xB5, 0xC8), 0.35 if light < 0.3 else 0.15))
            seg = []
    seg = []
    for x, z, c in pts:
        if c >= -0.05: seg.append((x, z))
        elif seg:
            if len(seg) > 1: cv.line(seg, 0.021, col)
            seg = []
    if len(seg) > 1: cv.line(seg, 0.021, col)
    for sx in (-1, 1):   # Zuleitungen unten
        cv.line(L.quad((sx * 0.02, 0.98), (sx * 0.07, 1.00), (sx * 0.085 * (1 if sx > 0 else 1), 1.03), 6), 0.014, WIRE)

def draw_wendel(cv, light):
    """das jetzige Prinzip, aber richtig: Glasfuß, zwei Drähte, dazwischen eine echte Wendel mit Schlaufen"""
    glass_stem(cv, top=1.12, button=1.07)
    zc, w, n_loops, a = 1.37, 0.13, 4, 0.032
    for sx in (-1, 1):
        cv.line(L.quad((sx * 0.032, 1.06), (sx * 0.07, 1.25), (sx * w, zc), 14), 0.017, WIRE)
    c = 2 * w / (2 * math.pi * n_loops)
    pts = []
    for i in range(241):
        t = i / 240 * 2 * math.pi * n_loops
        pts.append((-w + c * t - a * math.sin(t), zc + a * 0.9 * (1 - math.cos(t)) * 0.5 + 0.004 * math.sin(math.pi * i / 240)))
    halo(cv, pts, light, 0.11)
    cv.line(pts, 0.015, fil_col(light))

def draw_herz(cv, light):
    """verspielt: der Glühfaden hat die Form eines kleinen Herzens"""
    glass_stem(cv, top=1.13, button=1.08)
    cz, s = 1.335, 0.0105
    heart = []
    for i in range(121):
        t = math.pi * (i / 120) * 2 - math.pi
        x = 16 * math.sin(t) ** 3
        z = 13 * math.cos(t) - 5 * math.cos(2 * t) - 2 * math.cos(3 * t) - math.cos(4 * t)
        heart.append((x * s, cz + z * s))
    # Drähte vom Glasfuß zu den beiden Herzbögen
    for sx in (-1, 1):
        cv.line(L.quad((sx * 0.030, 1.08), (sx * 0.12, 1.16), (sx * 0.150, 1.33), 12), 0.017, WIRE)
    halo(cv, heart, light, 0.12)
    cv.line(heart, 0.020, fil_col(light), closed=True)

STYLES = {"jetzt": draw_jetzt, "klassisch": draw_klassisch, "spirale": draw_spirale, "wendel": draw_wendel, "herz": draw_herz}

def draw(cv, style, light):
    STYLES[style](cv, light)

# ------------------------------------------------------------------------------------------
# Groß, wie auf dem Foto (Maße am Foto abgemessen, in R; unter der Mitte etwas gestaucht wie der Hals)
CZ_, R_ = 1.75, 0.67
def Zr(d): return CZ_ + (d * R_ if d >= 0 else d * R_ * 0.85)

def draw_foto(cv, light, top=0.18, tipx=0.40, subtle=False, wa=None, ww=None, sag=0.035):
    wa = wa if wa is not None else (0.75 if subtle else 1.0)
    ww = ww if ww is not None else (0.013 if subtle else 0.017)
    # Glasfuß: unten breiter Kelch bis zur Pressung (Knopf), darüber ein dünner Glasstab
    z0 = NZ + 0.03
    zb = Zr(-0.88)                     # Knopf
    zr = Zr(min(-0.03, top - 0.21))    # oberes Ende des Stabs (kleine Perle)
    right = (L.quad((0.18, z0), (0.10, z0 + 0.10), (0.085, zb - 0.10), 10) + [(0.085, zb - 0.03)])
    shape = right + [(-x, z) for x, z in reversed(right)]
    cv.poly(shape, GLASS, 0.38 * wa)
    cv.line(right, 0.012, GLASS_EDGE, a=0.85 * wa); cv.line([(-x, z) for x, z in right], 0.012, GLASS_EDGE, a=0.85 * wa)
    for sx in (-1, 1):   # Zuleitungen im Fuß
        cv.line([(sx * 0.035, z0 + 0.02), (sx * 0.045, zb)], 0.010, WIRE, a=0.7 * wa)
    cv.ell(0, zb, 0.075, 0.048, GLASS, 0.7 * wa)
    cv.line(L.ellipse(0, zb, 0.075, 0.048, 28), 0.012, GLASS_EDGE, a=0.9 * wa, closed=True)
    rod = [(0.028, zb + 0.04), (0.022, zr), (-0.022, zr), (-0.028, zb + 0.04)]
    cv.poly(rod, GLASS, 0.45 * wa); cv.line(rod, 0.011, GLASS_EDGE, a=0.85 * wa, closed=True)
    cv.ell(0, zr + 0.012, 0.022, 0.018, (0xE8, 0xC2, 0x7A), 0.9 * wa)        # kleine Perle oben
    # zwei Drähte als V
    tz = Zr(top); tx = tipx * R_
    for sx in (-1, 1):
        cv.line([(sx * 0.05, zb + 0.01), (sx * tx, tz)], ww, WIRE, a=wa)
        cv.line([(0.0, zr + 0.02), (sx * tx * 0.42, tz + 0.012)], ww * 0.65, WIRE, a=0.85 * wa)   # Halter
    # Glühfaden zwischen den Drahtspitzen, leicht durchhängend
    fil = L.cubic((-tx, tz), (-tx * 0.55, tz - sag), (tx * 0.55, tz - sag), (tx, tz), 30)
    halo(cv, fil, light, 0.13)
    cv.line(fil, ww + 0.004, fil_col(light), a=1.0 if light > 0.05 else wa)

STYLES["foto"] = lambda cv, l: draw_foto(cv, l)
STYLES["foto_dezent"] = lambda cv, l: draw_foto(cv, l, subtle=True)
STYLES["foto_hoch"] = lambda cv, l: draw_foto(cv, l, top=0.44, tipx=0.46)
STYLES["foto_breit"] = lambda cv, l: draw_foto(cv, l, top=0.18, tipx=0.52)

def draw_spirale_gross(cv, light, wa=0.8):
    """große Spirale vom Glasfuß bis knapp unter den Mund"""
    z0 = NZ + 0.03; zb = Zr(-1.33)
    right = (L.quad((0.17, z0), (0.10, z0 + 0.08), (0.075, zb - 0.06), 10) + [(0.075, zb)])
    shape = right + [(-x, z) for x, z in reversed(right)]
    cv.poly(shape, GLASS, 0.38 * wa)
    cv.line(right, 0.012, GLASS_EDGE, a=0.85 * wa); cv.line([(-x, z) for x, z in right], 0.012, GLASS_EDGE, a=0.85 * wa)
    za, zt, r, turns = zb + 0.03, Zr(-0.50), 0.115, 4.0
    cv.line([(0, zb), (0, zt + 0.02)], 0.012, WIRE, a=0.8 * wa)
    col = fil_col(light); pts = []
    for i in range(301):
        t = i / 300 * turns * 2 * math.pi
        pts.append((r * math.sin(t), za + (zt - za) * i / 300 + 0.03 * math.cos(t), math.cos(t)))
    halo(cv, [(x, z) for x, z, _ in pts], light, 0.14)
    for front, w, a in ((False, 0.012, 0.45), (True, 0.019, 1.0)):
        seg = []
        for x, z, c in pts + [(0, 0, -9 if front else 9)]:
            if (c >= -0.05) == front and abs(c) < 5: seg.append((x, z))
            else:
                if len(seg) > 1: cv.line(seg, w, col, a=(a * wa) if light < 0.05 else a)
                seg = []

STYLES["mittel"] = lambda cv, l: draw_foto(cv, l, top=-0.52, tipx=0.42, wa=0.85, ww=0.016, sag=-0.012)
STYLES["mittel_blass"] = lambda cv, l: draw_foto(cv, l, top=-0.52, tipx=0.42, wa=0.45, ww=0.013, sag=-0.012)
STYLES["gross_blass"] = lambda cv, l: draw_foto(cv, l, top=0.44, tipx=0.46, wa=0.32, ww=0.011)
STYLES["spirale_gross"] = lambda cv, l: draw_spirale_gross(cv, l)
