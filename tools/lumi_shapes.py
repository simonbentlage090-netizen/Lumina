"""Lumi (Lese-Pose) als Liste flacher 2D-Formen. Reines Python, keine Abhängigkeiten.
Koordinaten: x nach rechts, z nach oben (Blender-Vorderansicht)."""
import os
import math

INK = "#1E1A26"
BULB = "#FFC93C"; SHADOW = "#F2A324"; LIGHT = "#FFE27A"
C = (0.0, 1.60); RX = 0.74; RZ = 0.74          # runde Glaskugel oben
EYES = [(-0.235, 1.62), (0.235, 1.62)]
JOIN = -38                                      # hier geht die Kugel in den Hals über (Grad)

def ellipse(cx, cz, rx, rz, n=40):
    return [(cx + rx * math.cos(2 * math.pi * i / n), cz + rz * math.sin(2 * math.pi * i / n)) for i in range(n)]
def earc(cx, cz, rx, rz, a0, a1, n=20):
    return [(cx + rx * math.cos(math.radians(a0 + (a1 - a0) * i / n)), cz + rz * math.sin(math.radians(a0 + (a1 - a0) * i / n))) for i in range(n + 1)]
def quad(p0, c, p1, n=12):
    return [((1-t)**2*p0[0] + 2*(1-t)*t*c[0] + t*t*p1[0], (1-t)**2*p0[1] + 2*(1-t)*t*c[1] + t*t*p1[1]) for t in [i / n for i in range(n + 1)]]
def cubic(p0, p1, p2, p3, n=28):
    out = []
    for i in range(n + 1):
        t = i / n; u = 1 - t
        out.append((u**3*p0[0] + 3*u*u*t*p1[0] + 3*u*t*t*p2[0] + t**3*p3[0], u**3*p0[1] + 3*u*u*t*p1[1] + 3*u*t*t*p2[1] + t**3*p3[1]))
    return out
def rrect(cx, cz, w, h, r, n=6):
    pts = []
    for (qx, qz, a) in ((cx + w/2 - r, cz + h/2 - r, 0), (cx - w/2 + r, cz + h/2 - r, 90), (cx - w/2 + r, cz - h/2 + r, 180), (cx + w/2 - r, cz - h/2 + r, 270)):
        for i in range(n + 1):
            ang = math.radians(a + 90 * i / n); pts.append((qx + r * math.cos(ang), qz + r * math.sin(ang)))
    return pts
def bulb_arc(a0, a1, n=40, s=1.0):
    return earc(C[0], C[1], RX * s, RZ * s, a0, a1, n)
def bulb_outline():
    """Klassische Glühbirne: Kugel oben, nach innen geschwungener Hals, unten das Gewinde."""
    a = math.radians(JOIN)
    jr = (C[0] + RX * math.cos(a), C[1] + RZ * math.sin(a))
    tan = (math.sin(a), -math.cos(a))                 # Richtung entlang der Kugel nach unten
    right_neck = cubic(jr, (jr[0] + 0.18 * tan[0], jr[1] + 0.18 * tan[1]), (0.40, 0.88), (0.40, 0.70), 18)  # oben -> unten
    right_up = right_neck[::-1]
    left_down = [(-x, z) for x, z in right_neck]
    arc = bulb_arc(JOIN, 180 - JOIN, 90)
    return [(0.39, 0.50)] + right_up + arc[1:-1] + left_down + [(-0.39, 0.50)]
def crescent(seg, width, toward):
    inner = []; n = len(seg)
    for i, (x, z) in enumerate(reversed(seg)):
        t = 1 - i / (n - 1); w = math.sin(math.pi * t) ** 0.9
        vx, vz = toward[0] - x, toward[1] - z; l = math.hypot(vx, vz) or 1
        inner.append((x + vx / l * width * w, z + vz / l * width * w))
    return seg + inner

def F(pts, color, alpha=1.0): return {"kind": "fill", "pts": pts, "color": color, "alpha": alpha}
def Lz(pts, width, color=INK, closed=False): return {"kind": "line", "pts": pts, "color": color, "width": width, "closed": closed}

def eyes_open():
    """Freundlich-entspannte Augen: weiches Oval, oben nur leicht abgeflacht, KEINE dunkle Lidlinie
    (die wirkte wie eine Augenbraue -> ernst/cool). Glanzpunkte unten = Blick nach unten aufs Buch."""
    out = []
    for cx, cz in EYES:
        rx, rz = 0.074, 0.088
        out.append(F(ellipse(cx, cz, rx, rz, 40), INK))
        out.append(F(ellipse(cx + 0.022, cz - 0.028, 0.019, 0.019, 16), "#FFFFFF"))
        out.append(F(ellipse(cx - 0.020, cz - 0.050, 0.009, 0.009, 12), "#FFFFFF"))
        a = 48
        top = earc(cx, cz, rx + 0.006, rz + 0.006, a, 180 - a, 24)
        left, right = top[-1], top[0]
        edge = quad(left, (cx, cz + 0.088), right, 14)       # sanft gewölbt, nur oberes Fünftel
        out.append(F(top + edge[1:-1], BULB))
    return out
def eyes_closed():
    return [Lz(quad((cx - 0.08, cz + 0.005), (cx, cz - 0.045), (cx + 0.08, cz + 0.005), 14), 0.032) for cx, cz in EYES]

def glow():
    return [F(ellipse(C[0], C[1], r, r, 96), "#FFD25C", 0.055) for r in (1.16, 1.04, 0.94)]

METAL = "#CDD2DE"; METAL_D = "#9AA1B3"; METAL_L = "#EEF0F6"; ISO = "#2A2533"

def socket_shapes():
    s = []
    # Gewinde: leicht verjüngter Zylinder mit gewellten Kanten (Gewindegänge)
    z0, z1 = 0.47, 0.245          # oben, unten
    n_th = 3
    def half_w(z):
        t = (z0 - z) / (z0 - z1)
        base = 0.365 - 0.045 * t
        ph = t * n_th
        bumpv = abs(math.sin(math.pi * ph))
        return base + 0.022 * bumpv
    right = [(half_w(z0 - (z0 - z1) * i / 60), z0 - (z0 - z1) * i / 60) for i in range(61)]
    # unten abgerundet
    bot = quad((right[-1][0], z1), (right[-1][0] * 0.85, z1 - 0.03), (0, z1 - 0.03), 8)[1:]
    right_full = right + bot
    left = [(-x, z) for x, z in reversed(right_full)]
    body = right_full + left[1:]
    s.append(F(body, METAL))
    # Schatten rechts, Licht links
    sh = [(x, z) for x, z in right] 
    inner = [(x - 0.10, z) for x, z in reversed(right)]
    s.append(F(sh + inner, METAL_D, 0.85))
    hl = [(-0.25, z0 - 0.02), (-0.17, z0 - 0.02), (-0.15, z1 + 0.02), (-0.22, z1 + 0.02)]
    s.append(F(hl, METAL_L, 0.9))
    # Gewindegänge: schräge Bögen (Helix), von links unten nach rechts oben
    for i in range(1, n_th + 1):
        zt = z0 - (z0 - z1) * (i - 0.5 + 0.5) / n_th
        zl = zt - 0.0
        if i == n_th: break
        wl = half_w(zl - 0.0) - 0.01
        s.append(Lz(quad((-wl, zl - 0.018), (0.0, zl - 0.055), (wl, zl + 0.018), 16), 0.022, "#6E7487"))
    s.append(Lz(body, 0.028, closed=True))
    # Kragen oben, überdeckt den Hals
    col = rrect(0, 0.50, 0.86, 0.10, 0.045)
    s += [F(col, METAL_L), F(rrect(0.20, 0.50, 0.30, 0.10, 0.04), METAL, 1.0),
          F(rrect(-0.14, 0.515, 0.34, 0.022, 0.011), "#FFFFFF"), Lz(col, 0.028, closed=True)]
    # Isolator und Kontakt
    iso = [(-0.27, 0.215), (0.27, 0.215)] 
    iso = quad((0.27, 0.222), (0.24, 0.165), (0.14, 0.165), 8) + quad((-0.14, 0.165), (-0.24, 0.165), (-0.27, 0.222), 8)
    tip = rrect(0, 0.158, 0.17, 0.06, 0.028)
    s += [F(tip, METAL_D), Lz(tip, 0.026, closed=True), F(iso, ISO), Lz(iso, 0.026, closed=True)]
    return s


def body(eyes="open"):
    s = []
    for sx in (-1, 1):   # Arme hinter dem Körper
        s.append(Lz(cubic((0.40 * sx, 1.02), (0.86 * sx, 1.02), (0.88 * sx, 0.60), (0.47 * sx, 0.70)), 0.08))
    out = bulb_outline()
    s.append(F(out, BULB))
    s.append(F(crescent(bulb_arc(-36, 72, 36), 0.21, (-0.15, 1.72)), SHADOW))
    s.append(F(crescent(bulb_arc(112, 200, 30), 0.13, (0.15, 1.52)), LIGHT))
    s.append(Lz(earc(C[0], C[1], 0.60, 0.60, 148, 194, 16), 0.048, "#FFFFFF"))
    s.append(F(ellipse(-0.29, 2.10, 0.035, 0.035, 20), "#FFFFFF"))
    s.append(Lz(out, 0.032, closed=True))
    s += socket_shapes()
    for sx in (-1, 1):
        s.append(F(ellipse(0.40 * sx, 1.485, 0.072, 0.044), "#FF8FA3", 0.92))
    s.append(Lz(quad((-0.105, 1.455), (0.0, 1.375), (0.105, 1.455), 16), 0.034))
    s += eyes_open() if eyes == "open" else eyes_closed()
    # Buch
    ptop = quad((-0.40, 0.90), (-0.22, 1.015), (0.0, 0.955), 14) + quad((0.0, 0.955), (0.22, 1.015), (0.40, 0.90), 14)[1:]
    pages = ptop + [(0.40, 0.86), (-0.40, 0.86)]
    s += [F(pages, "#FFF8EA"), Lz(pages, 0.026, closed=True)]
    for off in (0.03, 0.055):
        s.append(Lz(quad((-0.37, 0.90 - off * 0.6), (-0.21, 1.0 - off), (-0.02, 0.945 - off * 0.5), 12), 0.012, "#C9BDA3"))
        s.append(Lz(quad((0.02, 0.945 - off * 0.5), (0.21, 1.0 - off), (0.37, 0.90 - off * 0.6), 12), 0.012, "#C9BDA3"))
    cover = rrect(0, 0.725, 0.86, 0.33, 0.05)
    s += [F(cover, "#7E6CF2"), F(rrect(-0.05, 0.84, 0.62, 0.035, 0.017), "#A594FF"), F(rrect(0, 0.725, 0.07, 0.31, 0.02), "#5B4BD6"),
          Lz([(-0.035, 0.578), (-0.035, 0.872)], 0.012), Lz([(0.035, 0.578), (0.035, 0.872)], 0.012), Lz(cover, 0.03, closed=True)]
    band = [(0.07, 0.575), (0.115, 0.575), (0.115, 0.49), (0.0925, 0.51), (0.07, 0.49)]
    s += [F(band, "#FF6F7D"), Lz(band, 0.02, closed=True)]
    for sx in (-1, 1):
        s += [F(ellipse(0.43 * sx, 0.735, 0.085, 0.105, 32), INK), F(ellipse(0.375 * sx, 0.875, 0.04, 0.05, 20), INK)]
    return s


# ---------------------------------------------------------------------------------------------
# Ebenen für die Animationen in der App (alle im selben 2,8 x 2,8 Sichtfenster)
# ---------------------------------------------------------------------------------------------
TEAR = "#8FD3FF"

def drop_shape(x, z, r):
    """Tropfen (Schweiß / Träne), Spitze nach oben."""
    return [(x, z + r * 2.0)] + earc(x, z, r, r, 32, -212, 22)

def drop(x, z, r):
    pts = drop_shape(x, z, r)
    return [F(pts, TEAR), F(ellipse(x - r * 0.35, z + r * 0.15, r * 0.28, r * 0.34, 12), "#FFFFFF", 0.9), Lz(pts, 0.02, closed=True)]

def cheeks(alpha=0.92, rx=0.072, rz=0.044, z=1.485):
    return [F(ellipse(0.40 * sx, z, rx, rz), "#FF8FA3", alpha) for sx in (-1, 1)]

def parts():
    s = body("open")
    nb = 8 + len(socket_shapes())          # Arme (2) + Kolben (6) + Fassung
    return {
        "arms_hold": s[0:2],
        "body": s[2:nb],
        "face_read_open": s[nb:nb + 11],
        "face_read_closed": s[nb:nb + 3] + eyes_closed(),
        "book": s[nb + 11:nb + 25],
        "hands_hold": s[nb + 25:nb + 29],
    }

def face_scared():
    """Erschrocken, schaut den Betrachter an: große runde Augen (Glanz oben), besorgt hochgezogene
    Brauen (innen höher -> ängstlich, nicht böse), kleiner offener Mund, Schweißtropfen."""
    out = cheeks(0.45)
    for cx, cz in EYES:
        cz += 0.015
        out.append(F(ellipse(cx, cz, 0.088, 0.106, 40), INK))
        out.append(F(ellipse(cx + 0.026, cz + 0.034, 0.028, 0.028, 18), "#FFFFFF"))
        out.append(F(ellipse(cx - 0.030, cz - 0.040, 0.012, 0.012, 12), "#FFFFFF"))
        inner = 1 if cx < 0 else -1          # Richtung zur Gesichtsmitte
        outer = (cx - inner * 0.085, cz + 0.150)
        innr = (cx + inner * 0.070, cz + 0.205)
        out.append(Lz(quad(outer, (cx, cz + 0.200), innr, 12), 0.026))
    m = ellipse(0.0, 1.405, 0.052, 0.062, 28)
    out += [F(m, "#3A1F2B"), F(ellipse(0.0, 1.372, 0.030, 0.018, 16), "#FF8FA3"), Lz(m, 0.026, closed=True)]
    out += drop(0.60, 1.86, 0.045)
    return out

def face_sad():
    """Traurig: große, feuchte Kulleraugen (Tränenrand unten), besorgte Brauen (innen höher),
    Mundwinkel nach unten, eine Träne auf der Wange."""
    out = cheeks(0.55)
    for cx, cz in EYES:
        cz -= 0.015
        rx, rz = 0.080, 0.094
        out.append(F(ellipse(cx, cz, rx, rz, 40), INK))
        # Tränen sammeln sich unten im Auge
        rim = earc(cx, cz, rx * 0.80, rz * 0.80, 200, 340, 18)
        rim_in = earc(cx, cz + 0.022, rx * 0.70, rz * 0.62, 340, 200, 18)
        out.append(F(rim + rim_in, TEAR, 0.85))
        out.append(F(ellipse(cx + 0.024, cz + 0.030, 0.026, 0.026, 18), "#FFFFFF"))
        out.append(F(ellipse(cx - 0.028, cz - 0.006, 0.011, 0.011, 12), "#FFFFFF"))
        inner = 1 if cx < 0 else -1
        outer = (cx - inner * 0.088, cz + 0.140)
        innr = (cx + inner * 0.062, cz + 0.192)
        out.append(Lz(quad(outer, (cx - inner * 0.01, cz + 0.172), innr, 12), 0.024))
    out.append(Lz(quad((-0.085, 1.372), (0.0, 1.428), (0.085, 1.372), 16), 0.032))
    out += drop(0.262, 1.445, 0.032)
    return out

def face_happy():
    """Freude: Augen als fröhliche Bögen, großer offener Mund mit Zunge, kräftige Wangen."""
    out = cheeks(0.95, 0.080, 0.050, 1.50)
    for cx, cz in EYES:
        out.append(Lz(quad((cx - 0.082, cz - 0.025), (cx, cz + 0.095), (cx + 0.082, cz - 0.025), 16), 0.036))
    top = quad((-0.135, 1.475), (0.0, 1.495), (0.135, 1.475), 14)
    bot = quad((0.135, 1.475), (0.0, 1.215), (-0.135, 1.475), 22)
    mouth = top + bot[1:-1]
    low = [p for p in bot if abs(p[0]) <= 0.072]
    tongue = low + quad(low[-1], (0.0, 1.405), low[0], 10)[1:-1]
    out += [F(mouth, "#3A1F2B"), F(tongue, "#FF6F7D"), Lz(mouth, 0.030, closed=True)]
    return out

def arms_down():
    """Arme hängen schlaff herunter (traurig, Buch ist heruntergefallen)."""
    out = []
    for sx in (-1, 1):
        out.append(Lz(cubic((0.40 * sx, 1.02), (0.70 * sx, 1.00), (0.80 * sx, 0.76), (0.70 * sx, 0.56)), 0.08))
    for sx in (-1, 1):
        out.append(F(ellipse(0.70 * sx, 0.515, 0.078, 0.092, 32), INK))
    return out
