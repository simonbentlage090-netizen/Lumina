"""
Lumis Hände (seit 10.1): weiße Cartoon-Handschuhe mit Handfläche, drei dicken Fingern und Daumen
statt einer schwarzen Kugel. Dieselbe Form steht in der App (LumiArt.kt, gloveParts).
Erst werden alle Umrisse gezeichnet, dann alle Flächen – so entsteht ein gemeinsamer Rand.
"""
import math

GLOVE = (0xFF, 0xFF, 0xFF)

def capsule(x0, z0, x1, z1, r, n=10):
    a = math.atan2(z1 - z0, x1 - x0)
    pts = []
    for i in range(n + 1):
        t = a - math.pi / 2 + math.pi * i / n
        pts.append((x1 + r * math.cos(t), z1 + r * math.sin(t)))
    for i in range(n + 1):
        t = a + math.pi / 2 + math.pi * i / n
        pts.append((x0 + r * math.cos(t), z0 + r * math.sin(t)))
    return pts

def rot_ellipse(cx, cz, rx, rz, a, n=36):
    c, s = math.cos(a), math.sin(a)
    return [(cx + rx * math.cos(t) * c - rz * math.sin(t) * s, cz + rx * math.cos(t) * s + rz * math.sin(t) * c)
            for t in (2 * math.pi * i / n for i in range(n))]

def frame(x, z, ang, inward):
    ca, sa = math.cos(ang), math.sin(ang)
    return lambda u, v: (x + u * ca - v * sa * inward, z + u * sa + v * ca * inward)

def glove_parts(x, z, ang, s, open_=0.5, inward=1.0):
    """Teile eines Handschuhs am Handgelenk (x, z). ang = Richtung des Unterarms (Bogenmaß),
    s = Größe, open_ 0 = Faust … 1 = gespreizt, inward = ±1 (Daumen zur Körpermitte)."""
    P = frame(x, z, ang, inward)
    parts = []
    pc = P(0.50 * s, 0.0)
    parts.append(rot_ellipse(pc[0], pc[1], 0.56 * s, 0.54 * s, ang))
    for k in (-1, 0, 1):
        phi = k * math.radians(8 + 14 * open_)
        base = (0.74 * s, k * 0.34 * s)
        L_ = (0.24 + 0.24 * open_) * s * (0.88 if k != 0 else 1.0)
        tip = (base[0] + L_ * math.cos(phi), base[1] + L_ * math.sin(phi))
        b, t = P(*base), P(*tip)
        parts.append(capsule(b[0], b[1], t[0], t[1], 0.215 * s))
    th = math.radians(48 + 34 * open_)
    base = (0.40 * s, 0.38 * s)
    L_ = (0.30 + 0.14 * open_) * s
    tip = (base[0] + L_ * math.cos(th), base[1] + L_ * math.sin(th))
    b, t = P(*base), P(*tip)
    parts.append(capsule(b[0], b[1], t[0], t[1], 0.21 * s))
    return parts

def creases(x, z, ang, s, inward=1.0):
    P = frame(x, z, ang, inward)
    return [[P(0.62 * s, v * s), P(0.86 * s, v * s * 1.15)] for v in (-0.165, 0.165)]

def draw_parts(cv, parts, ow, ink, rim=None, rimw=0.02, lines=()):
    if rim is not None:
        for p in parts: cv.line(p, ow + rimw * 2, rim, closed=True)
    for p in parts: cv.line(p, ow, ink, closed=True)
    for p in parts: cv.poly(p, GLOVE)
    for l in lines: cv.line(l, ow * 0.55, ink)

def draw_glove(cv, x, z, ang, s, open_, inward, ink, rim=None, rimw=0.02):
    """Freie Hand (winken, jubeln, Hände in die Hüften …)."""
    x0, z0 = x - 0.25 * s * math.cos(ang), z - 0.25 * s * math.sin(ang)
    draw_parts(cv, glove_parts(x0, z0, ang, s, open_, inward), 0.26 * s, ink, rim, rimw, creases(x0, z0, ang, s, inward))

def draw_glove_hold(cv, hx, hz, hrx, hrz, fx, fz, frx, ink, rim=None, rimw=0.02):
    """Hand, die das Buch seitlich hält: Handfläche an der Buchkante, Daumen vorne auf dem Einband."""
    palm = rot_ellipse(hx, hz, hrx * 0.95, hrz * 0.92, 0.0)
    a = math.atan2(fz - hz, fx - hx)
    t0 = (hx + 0.30 * hrx * math.cos(a), hz + 0.30 * hrx * math.sin(a))
    thumb = capsule(t0[0], t0[1], fx, fz, frx * 1.05)
    draw_parts(cv, [palm, thumb], 0.26 * hrx * 1.25, ink, rim, rimw)
