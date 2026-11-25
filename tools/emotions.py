"""Lumi B (Kulleraugen): Gefühle und Ausgehen als Vergleichsblatt. Nur Vorschau, App bleibt unverändert."""
import sys, os, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L
import lumi_hands as H, lumi_rig as R, body11
import designs as D
from PIL import Image, ImageDraw, ImageFont

hexc, star, Body = D.hexc, D.star, D.Body
INK = (0x1E, 0x1A, 0x26); TEAR = (0x8F, 0xD3, 0xFF); TEAR_D = (0x4F, 0x9C, 0xD9); WHITE = (255, 255, 255)
MOUTH = R.MOUTH; TONGUE = R.TONGUE; CHEEK = (0xFF, 0x8F, 0xA3); EYE_LOW = (0x6A, 0x5A, 0xA8)
SPARK = (0xFF, 0xD2, 0x5C); SMOKE = (0xB4, 0xB0, 0xC2); RIM = (0x6B, 0x62, 0x80)
ON = dict(fill="#FFC93C", shadow="#F2A324", light="#FFE27A")
OFF = dict(fill="#E4E3EC", shadow="#C3C1D3", light="#F7F6FB")

def mixc(a, b, t): return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))
def mixhex(a, b, t):
    c = mixc(hexc(a), hexc(b), t); return "#%02X%02X%02X" % c

# B-Gesicht (Grundwerte, in Vielfachen von R)
B = dict(ex=0.335, ez=-0.035, erx=0.138, erz=0.158, mz=-0.250, cx=0.585, cz=-0.205, crx=0.135, crz=0.078)

def face(**kw):
    f = dict(eyes="open", es=1.0, look=(0.0, 0.0), hl=[(0.26, 0.36, 0.36), (-0.34, -0.40, 0.15)], sparkle=False,
             wet=0.0, lidT=(0.0, 0.0), lidB=0.0, dull=False, slant=0.0,
             brows=None, mouth="smile", mw=0.075, md=0.040, mzo=0.0, mxo=0.0,
             chA=0.82, chS=1.0, lines=False, tears=None, sweat=False, sparkles=False, smoke=False, rays=False,
             zap=False, hihi=False)
    f.update(kw); return f

# ------------------------------------------------------------------------------------------
# Gesicht zeichnen
# ------------------------------------------------------------------------------------------
def draw_face(cv, b, f, light=1.0):
    Rr, CZ = b.R, b.CZ
    X = lambda u: u * Rr
    Z = lambda v: CZ + v * Rr
    bulb = mixc(hexc(OFF["fill"]), hexc(ON["fill"]), light)
    lx, lz = f["look"]
    # Wangen
    for sx in (-1, 1):
        a = f["chA"] * (0.55 + 0.45 * light)
        cv.ell(sx * X(B["cx"]) + X(lx) * 0.4, Z(B["cz"]), B["crx"] * Rr * f["chS"], B["crz"] * Rr * f["chS"], CHEEK, a)
        if f["lines"]:
            for k in (-1, 0, 1):
                x0 = sx * X(B["cx"]) + X(lx) * 0.4 + k * 0.38 * B["crx"] * Rr * f["chS"]
                z0 = Z(B["cz"])
                cv.line([(x0 + 0.024 * Rr, z0 + 0.034 * Rr), (x0 - 0.024 * Rr, z0 - 0.034 * Rr)], 0.017, (0xE2, 0x4F, 0x70), a=0.9)
    # Mund
    draw_mouth(cv, b, f)
    # Augen
    kinds = f["eyes"] if isinstance(f["eyes"], tuple) else (f["eyes"], f["eyes"])
    for sx, kind in zip((-1, 1), kinds):
        ex, ez = sx * X(B["ex"]) + X(lx), Z(B["ez"]) + X(lz)
        rx, rz = B["erx"] * Rr * f["es"], B["erz"] * Rr * f["es"]
        lw = 0.060 * Rr
        if kind == "open":
            eye_open(cv, ex, ez, rx, rz, f, sx, bulb)
        elif kind == "happy":     # ∩
            cv.line(L.quad((ex - 0.85 * rx, ez - 0.30 * rz), (ex, ez + 0.95 * rz), (ex + 0.85 * rx, ez - 0.30 * rz), 16), lw, INK)
        elif kind == "closed":    # ∪, außen etwas tiefer, wenn slant
            s = f["slant"] * rz
            cv.line(L.quad((ex - 0.85 * rx, ez + (s if sx > 0 else -s) * 0 + (-s if sx < 0 else 0)), (ex, ez - 0.65 * rz),
                           (ex + 0.85 * rx, ez + (-s if sx > 0 else 0)), 16), lw, INK)
        elif kind == "squint":    # > <
            o = 1 if sx < 0 else -1   # linkes Auge zeigt nach rechts (zur Nase)
            pts = [(ex - o * 0.70 * rx, ez + 0.55 * rz), (ex + o * 0.62 * rx, ez + 0.02 * rz), (ex - o * 0.70 * rx, ez - 0.55 * rz)]
            cv.line(pts, lw, INK)
        # Augenbrauen
        if f["brows"]:
            tilt, lift_ = f["brows"]     # tilt > 0: innen höher (besorgt)
            bz = ez + rz + (0.10 + lift_) * Rr
            inner = (ex - sx * 0.55 * rx, bz + tilt * Rr * 0.5)
            outer = (ex + sx * 0.55 * rx, bz - tilt * Rr * 0.5)
            mid = ((inner[0] + outer[0]) / 2, (inner[1] + outer[1]) / 2 + 0.022 * Rr)
            cv.line(L.quad(outer, mid, inner, 10), 0.038 * Rr, INK)
    # Tränen
    if f["tears"]:
        for sx in (-1, 1):
            ex, ez = sx * X(B["ex"]) + X(lx), Z(B["ez"]) + X(lz)
            rx, rz = B["erx"] * Rr * f["es"], B["erz"] * Rr * f["es"]
            if f["tears"] == "stream":
                x0, z0 = ex + sx * 0.30 * rx, ez - 0.70 * rz
                n = 14; left = []; right = []
                for i in range(n + 1):
                    u = i / n
                    xc = x0 + sx * 0.10 * Rr * u + 0.012 * Rr * math.sin(u * 7)
                    zc = z0 - 0.62 * Rr * u
                    w = (0.030 + 0.030 * u) * Rr
                    left.append((xc - w, zc)); right.append((xc + w, zc))
                tip = left[-1]; tipR = right[-1]
                bottom = L.earc((tip[0] + tipR[0]) / 2, tip[1], (tipR[0] - tip[0]) / 2, 0.05 * Rr, 180, 360, 12)
                shape = left + bottom + right[::-1]
                cv.poly(shape, TEAR, 0.92)
                cv.line([(p[0] + 0.018 * Rr, p[1]) for p in left[2:-2]], 0.012, WHITE, a=0.8)
                cv.line(shape, 0.016, TEAR_D, closed=True)
            elif f["tears"] == "drop" and sx > 0:
                x, z, r = ex + 0.95 * rx, ez - 0.75 * rz, 0.050 * Rr
                d = L.drop_shape(x, z, r)
                cv.poly(d, TEAR); cv.ell(x - r * 0.35, z + r * 0.15, r * 0.28, r * 0.34, WHITE, 0.9)
                cv.line(d, 0.016, TEAR_D, closed=True)
    if f["sweat"]:
        x, z, r = X(0.80), Z(0.42), 0.075 * Rr
        d = L.drop_shape(x, z, r)
        cv.poly(d, TEAR); cv.ell(x - r * 0.35, z + r * 0.15, r * 0.28, r * 0.34, WHITE, 0.9)
        cv.line(d, 0.020, INK, closed=True)

def eye_open(cv, ex, ez, rx, rz, f, sx, bulb):
    eye = L.ellipse(ex, ez, rx, rz, 48)
    em = cv.mask_of(eye)
    cv.poly(eye, INK)
    if not f["dull"]:
        cv.ell(ex, ez - rz * 0.62, rx * 0.78, rz * 0.48, EYE_LOW, 0.55, mask=em)
    if f["wet"] > 0:
        cv.ell(ex, ez - rz * 0.95, rx * 1.0, rz * 0.62, TEAR, 0.75 * f["wet"], mask=em)
    if not f["dull"]:
        for hx, hz, hr in f["hl"]:
            cv.ell(ex + hx * rx, ez + hz * rz, hr * rx, hr * rx, WHITE)
        if f["sparkle"]:
            hx, hz, hr = f["hl"][0]
            cv.poly(star(ex + (hx - 0.52) * rx, ez + (hz + 0.36) * rz, 0.22 * rx), WHITE)
    # Oberlid (außen, innen): Anteil des Auges, der von oben verdeckt ist
    to, ti = f["lidT"]
    if to > 0 or ti > 0:
        zo, zi = ez + rz * (1 - 2 * to), ez + rz * (1 - 2 * ti)
        xo, xi = ex + sx * rx * 1.3, ex - sx * rx * 1.3
        edge = L.quad((xo, zo), (ex, (zo + zi) / 2 + 0.12 * rz), (xi, zi), 14)
        cv.poly(edge + [(xi, ez + 3 * rz), (xo, ez + 3 * rz)], bulb, mask=em)
    if f["lidB"] > 0:
        zb = ez - rz * (1 - 2 * f["lidB"])
        edge = L.quad((ex - rx * 1.3, zb - 0.25 * rz), (ex, zb + 0.35 * rz), (ex + rx * 1.3, zb - 0.25 * rz), 14)
        cv.poly(edge + [(ex + rx * 1.3, ez - 3 * rz), (ex - rx * 1.3, ez - 3 * rz)], bulb, mask=em)

def draw_mouth(cv, b, f):
    Rr = b.R
    mx = f["mxo"] * Rr + f["look"][0] * Rr * 0.6
    mz = b.CZ + (B["mz"] + f["mzo"]) * Rr + f["look"][1] * Rr * 0.5
    mw, md = f["mw"] * Rr, f["md"] * Rr
    lw = 0.042 * Rr
    k = f["mouth"]
    if k == "smile":
        cv.line(L.quad((mx - mw, mz), (mx, mz - 2 * md), (mx + mw, mz), 18), lw, INK)
    elif k == "flat":
        cv.line([(mx - mw, mz - 0.3 * md), (mx + mw, mz - 0.3 * md)], lw, INK)
    elif k == "frown":
        cv.line(L.quad((mx - mw, mz - md), (mx, mz + md), (mx + mw, mz - md), 18), lw, INK)
    elif k == "wavy":
        pts = [(mx - mw + 2 * mw * i / 24, mz - 0.4 * md + 0.55 * md * math.sin(2 * math.pi * 1.5 * i / 24 + math.pi)) for i in range(25)]
        cv.line(pts, lw * 0.9, INK)
    elif k == "cat":
        cv.line(L.quad((mx - mw, mz + 0.2 * md), (mx - mw / 2, mz - 1.9 * md), (mx, mz + 0.1 * md), 12)
                + L.quad((mx, mz + 0.1 * md), (mx + mw / 2, mz - 1.9 * md), (mx + mw, mz + 0.2 * md), 12)[1:], lw, INK)
    elif k in ("open", "laugh"):
        top = L.quad((mx - mw, mz), (mx, mz + 0.30 * md), (mx + mw, mz), 12)
        bot = L.quad((mx + mw, mz), (mx, mz - 3.4 * md), (mx - mw, mz), 18)[1:-1]
        shape = top + bot
        cv.poly(shape, MOUTH)
        cv.ell(mx, mz - 1.30 * md, mw * 0.62, md * 0.80, TONGUE, mask=cv.mask_of(shape))
        cv.line(shape, lw * 0.85, INK, closed=True)
    elif k == "o":
        e = L.ellipse(mx, mz - md, mw, md * 1.35, 30)
        cv.poly(e, MOUTH); cv.ell(mx, mz - md * 1.9, mw * 0.6, md * 0.55, TONGUE, 0.9, mask=cv.mask_of(e))
        cv.line(e, lw * 0.85, INK, closed=True)
    elif k == "scared":    # offen, oben leicht wellig, unten breiter
        top = [(mx - mw + 2 * mw * i / 16, mz + 0.18 * md * math.sin(2 * math.pi * i / 16 * 1.5)) for i in range(17)]
        bot = L.cubic((mx + mw, mz), (mx + mw * 1.15, mz - 2.6 * md), (mx - mw * 1.15, mz - 2.6 * md), (mx - mw, mz), 20)[1:-1]
        shape = top + bot
        cv.poly(shape, MOUTH)
        cv.ell(mx, mz - 1.75 * md, mw * 0.6, md * 0.65, TONGUE, mask=cv.mask_of(shape))
        cv.line(shape, lw * 0.85, INK, closed=True)
    elif k == "cry":       # offen, Mundwinkel nach unten
        top = L.quad((mx - mw, mz - 1.1 * md), (mx, mz + 0.9 * md), (mx + mw, mz - 1.1 * md), 14)
        bot = L.quad((mx + mw, mz - 1.1 * md), (mx, mz - 3.0 * md), (mx - mw, mz - 1.1 * md), 14)[1:-1]
        shape = top + bot
        cv.poly(shape, MOUTH)
        cv.ell(mx, mz - 2.0 * md, mw * 0.55, md * 0.55, TONGUE, mask=cv.mask_of(shape))
        cv.line(shape, lw * 0.85, INK, closed=True)
    elif k == "tongue":    # Lächeln, Zunge schaut raus
        cv.line(L.quad((mx - mw, mz), (mx, mz - 2 * md), (mx + mw, mz), 18), lw, INK)
        t = L.earc(mx + 0.30 * mw, mz - md * 0.95, 0.42 * mw, 1.2 * md, 180, 360, 14)
        cv.poly(t, TONGUE); cv.line(t, lw * 0.75, INK)
        cv.line([(mx + 0.30 * mw, mz - md * 1.1), (mx + 0.30 * mw, mz - md * 1.9)], lw * 0.45, (0xC8, 0x4A, 0x5A))
    elif k == "pout":      # kleiner Schmollmund
        cv.line(L.quad((mx - mw, mz - 0.9 * md), (mx - 0.1 * mw, mz + 0.7 * md), (mx + mw * 0.55, mz - 0.6 * md), 12), lw, INK)
        cv.line(L.quad((mx + mw * 0.55, mz - 0.6 * md), (mx + mw * 0.85, mz - 0.2 * md), (mx + mw * 1.0, mz - 1.1 * md), 8), lw, INK)
    elif k == "hidden":
        pass

# ------------------------------------------------------------------------------------------
# Arme
# ------------------------------------------------------------------------------------------
POSES = {   # pro Seite (außen positiv): c1, c2, Hand  (in R, relativ zur Kugelmitte). Seit 13.0 länger,
            # mit derselben Schulter wie in der App (LumiArt.kt), damit die Arme überall gleich aussehen.
    "rest":    ((0.896, -0.612), (1.015, -0.881), (0.955, -1.119)),
    "wave":    ((1.02, -0.496), (1.4, -0.394), (1.248, 0.189)),
    "cheer":   ((1.02, -0.471), (1.451, -0.115), (1.274, 0.494)),
    "open":    ((0.992, -0.497), (1.302, -0.449), (1.278, -0.163)),
    "cheeks":  ((1.194, -0.552), (1.463, -0.194), (0.896, -0.194)),
    "clasp":   ((1.284, -0.582), (1.224, -1.09), (0.239, -0.821)),
    "shy":     ((1.045, -0.672), (0.836, -1.149), (0.149, -1.06)),
    "hips":    ((1.164, -0.433), (1.403, -0.881), (0.806, -1.03)),
    "droop":   ((0.836, -0.612), (0.925, -0.91), (0.836, -1.149)),
    "mouth":   ((0.98, -0.92), (0.7, -0.62), (0.1, -0.33)),
    "head":    ((1.254, -0.493), (1.612, 0.134), (0.955, 0.493)),   # kratzt sich verlegen am Hinterkopf
}
SHOULDER = (0.54, -0.515)   # Schulter (in R): wie in der App
BACK = {"head"}   # diese Hände liegen hinter dem Glas
# Wie weit die Finger gespreizt sind (0 = Faust, 1 = offen)
OPENNESS = {"rest": 0.2, "wave": 1.0, "cheer": 0.75, "open": 0.9, "cheeks": 0.5, "clasp": 0.1, "shy": 0.2,
            "hips": 0.0, "droop": 0.15, "mouth": 0.4, "head": 0.4}
# Seit 15.0 etwas kürzere Arme (Simon: "ein kleines Ticken zu lang"): freie Arme werden zur Schulter hin um 13 %
# gestaucht, dieselbe Krümmung. Arme, deren Hände etwas berühren (Wangen, Hüften, Mund …), bleiben, wie sie sind.
ARM_SHORTEN = 0.87
SHORT_POSES = {"rest", "wave", "cheer", "open", "droop"}
def _short(q, k):
    return (SHOULDER[0] + (q[0] - SHOULDER[0]) * k, SHOULDER[1] + (q[1] - SHOULDER[1]) * k)
def arms(cv, b, pose, dark):
    left, right = pose if isinstance(pose, tuple) else (pose, pose)
    Rr, CZ = b.R, b.CZ
    w = 0.075 * b.K
    hands = []
    for sx, p in ((-1, left), (1, right)):
        c1, c2, e = POSES[p]
        if p in SHORT_POSES:
            c1, c2, e = (_short(q, ARM_SHORTEN) for q in (c1, c2, e))
        P_ = lambda q: (sx * q[0] * Rr, CZ + q[1] * Rr)
        pts = L.cubic(P_(SHOULDER), P_(c1), P_(c2), P_(e), 30)
        if dark: cv.line(pts, w + 0.044 * b.K, RIM)
        cv.line(pts, w, INK)
        q2, qe = P_(c2), P_(e)
        hand = (qe[0], qe[1], math.atan2(qe[1] - q2[1], qe[0] - q2[0]), sx, p)
        if p in BACK: hands_draw(cv, b, [hand], dark)
        else: hands.append(hand)
    return hands
def hands_draw(cv, b, hands, dark):
    """Weiße Handschuhe (lumi_hands.py), Daumen zur Körpermitte."""
    for (x, z, ang, sx, p) in hands:
        s = 0.117 * b.K
        inward = -1.0 if -math.sin(ang) * sx > 0 else 1.0
        H.draw_glove(cv, x, z, ang, s, OPENNESS.get(p, 0.4), inward, INK, RIM if dark else None, 0.022 * b.K)

# ------------------------------------------------------------------------------------------
# Figur
# ------------------------------------------------------------------------------------------
FLOOR = 0.12
P = L.parts()

def book_shapes(b, rot=0.0, dx=0.0, dz=0.0, hl=0.18):
    """Buch in neuen Koordinaten; rot/dx/dz für das fallende Buch."""
    K = b.K; out = []
    cx, cz = 0.0, b.CZ + (0.733 - 1.60) * K + hl
    a = math.radians(rot)
    for s in P["book"]:
        pts = []
        for x, z in s["pts"]:
            X_, Z_ = x * K, b.CZ + (z - 1.60) * K + hl
            u, v = X_ - cx, Z_ - cz
            pts.append((cx + u * math.cos(a) - v * math.sin(a) + dx, cz + u * math.sin(a) + v * math.cos(a) + dz))
        t = dict(s); t["pts"] = pts
        if s["kind"] == "line": t["width"] = s["width"] * K
        out.append(t)
    return out

def soft_glow(cv, b, a):
    """Weicher Lichtschein um die Kugel (seit 15.0 statt dreier harter Ringe): bis knapp über die Glaskante
    kräftig, dann weich auslaufend bis ganz durchsichtig. Der Radius wird so begrenzt, dass der Schein vor dem
    Bildrand endet – vorher war er oben abgeschnitten (Simon 15.0). Die Mitte liegt etwas unter der Kugelmitte,
    so hat der Schein nach oben mehr Platz und umfängt auch den Hals."""
    import numpy as np
    from PIL import Image as _Img
    cx, cy = cv.px(0.0, b.CZ - 0.10)
    edge = cv.u(b.R * 0.98)                       # ungefähr die Glaskante
    want = cv.u(1.08)
    room = min(cy, cx, cv.W - cx) - cv.u(0.02)   # Platz bis zum Rand der Leinwand (z = 2.80 oben)
    r = max(edge * 1.18, min(want, room))
    W = cv.W
    y0, y1 = int(max(0, cy - r - 2)), int(min(W, cy + r + 2))
    x0, x1 = int(max(0, cx - r - 2)), int(min(W, cx + r + 2))
    yy, xx = np.mgrid[y0:y1, x0:x1]
    d = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2)
    t = np.clip((d - edge) / max(1.0, r - edge), 0.0, 1.0)
    alpha = 0.22 * a * (1.0 - t * t * (3.0 - 2.0 * t)) ** 1.6
    alpha[d >= r] = 0.0
    lay = np.zeros((W, W, 4), dtype=np.uint8)
    lay[y0:y1, x0:x1, 0] = 0xFF
    lay[y0:y1, x0:x1, 1] = 0xD2
    lay[y0:y1, x0:x1, 2] = 0x5C
    lay[y0:y1, x0:x1, 3] = np.clip(alpha * 255.0 + 0.5, 0, 255).astype(np.uint8)
    cv.img.alpha_composite(_Img.fromarray(lay, "RGBA"))

def render(e, W, bg):
    dark = bg[0] < 100
    light = e.get("light", 1.0)
    col = {k: mixhex(OFF[k], ON[k], light) for k in ("fill", "shadow", "light")}
    b = Body(**col)
    cv = R.Canvas(W, bg)
    lift = FLOOR + 0.09 - b.bottom() + e.get("lift", 0.0)
    fig = R.xform(rot=e.get("tilt", 0.0), ty=-lift / 2.8, oy=(2.8 - b.CZ) / 2.8)
    sh = 1.0 - 0.35 * min(1, e.get("lift", 0.0) / 0.15)
    for k, a in ((1.0, 0.10), (0.75, 0.12), (0.5, 0.14)):
        cv.ell(0, FLOOR, 0.30 * k * sh, 0.05 * k * sh, (0, 0, 0), a * (2.5 if dark else 1.6))
    if e.get("bookDown"):
        cv.shapes(R.book_down_shapes(0.60, FLOOR + 0.01))
    cv.T = [fig]
    if light > 0.02:
        soft_glow(cv, b, light * e.get("glowA", 1.0))
    f = e["face"]
    if e["pose"] == "hold":
        hl = 0.18
        UH = R.xform(sx=b.K, sy=b.K, ox=0.5, oy=(2.8 - 1.60) / 2.8, ty=-(b.CZ - 1.60 + hl) / 2.8)
        AA = R.ARMS["hold"]
        cv.T = [UH, fig]
        if dark:
            for sx in (-1, 1):
                a = AA["L" if sx < 0 else "R"]
                cv.line(L.cubic((sx * 0.40, 1.02), (sx * a["c1x"], a["c1z"]), (sx * a["c2x"], a["c2z"]), (sx * a["ex"], a["ez"]), 28), 0.124, RIM)
        R.draw_arm_lines(cv, AA)
        cv.T = [fig]; cv.shapes(b.glass() + b.base()); cv.shapes(book_shapes(b))
        cv.T = [UH, fig]
        for sx in (-1, 1):
            a = AA["L" if sx < 0 else "R"]
            H.draw_glove_hold(cv, sx * a["hx"], a["hz"], a["hrx"], a["hrz"], sx * a["fx"], a["fz"], a["frx"], INK,
                              RIM if dark else None, 0.022)
        cv.T = [fig]
        draw_face(cv, b, f, light)
    else:
        hands = arms(cv, b, e["pose"], dark)
        cv.shapes(b.glass() + b.base())
        if light < 1:   # Glühdraht
            import filament
            filament.draw(cv, e.get("fil", "jetzt"), light)
        draw_face(cv, b, f, light)
        hands_draw(cv, b, hands, dark)
        if e.get("bookFall"):
            cv.shapes(book_shapes(b, rot=-32, dx=0.50, dz=-0.56))
    # Extras (im Figurenraum)
    Rr, CZ = b.R, b.CZ
    if f["sparkles"]:
        for (u, v, s) in ((0.92, 0.78, 0.090), (1.10, 0.52, 0.050), (-0.98, 0.70, 0.065)):
            pts = star(u * Rr, CZ + v * Rr, s * b.K / 0.905 * 1.0)
            cv.poly(pts, SPARK); cv.line(pts, 0.016, INK, closed=True)
    if f["rays"]:
        for i in range(8):
            a = math.radians(i * 45 + 22.5)
            r0, r1 = Rr + 0.10, Rr + 0.26
            cv.line([(r0 * math.cos(a), CZ + r0 * math.sin(a)), (r1 * math.cos(a), CZ + r1 * math.sin(a))], 0.040, SPARK)
    if f["zap"]:
        for sx, v in ((1, 0.62), (-1, 0.40)):
            x0 = sx * (Rr * 0.98)
            z0 = CZ + v * Rr
            pts = [(x0, z0), (x0 + sx * 0.07, z0 + 0.05), (x0 + sx * 0.05, z0 - 0.01), (x0 + sx * 0.13, z0 + 0.04)]
            cv.line(pts, 0.026, SPARK if not dark else (0xFF, 0xE0, 0x80))
    if f["smoke"]:
        base = (0.02 * Rr, CZ + 1.02 * Rr)
        curl = [(base[0] + 0.045 * Rr * math.sin(i / 20 * 2.6 * math.pi), base[1] + 0.30 * Rr * i / 20) for i in range(21)]
        cv.line(curl, 0.028, SMOKE, a=0.85)
        for (u, v, r_, a) in ((0.09, 1.38, 0.085, 0.60), (-0.06, 1.48, 0.065, 0.45), (0.05, 1.57, 0.045, 0.30)):
            cv.ell(u * Rr, CZ + v * Rr, r_ * Rr * 1.4, r_ * Rr, SMOKE, a)
    if f["hihi"]:
        for sx in (-1, 1):
            for k, (u, v) in enumerate(((1.08, 0.35), (1.16, 0.15))):
                x0, z0 = sx * u * Rr, CZ + v * Rr
                cv.line([(x0, z0), (x0 + sx * 0.08, z0 + 0.03)], 0.028, INK)
    cv.T = []
    return cv.img.convert("RGB")

# ------------------------------------------------------------------------------------------
# Gefühle
# ------------------------------------------------------------------------------------------
HAPPY_EYES = dict(eyes="happy", chA=0.92, chS=1.12)
ROWS = [
    ("Gute Gefühle", (0xF4, 0xF3, 0xF7), [
        ("Liest", "beim Lesen", dict(pose="hold", face=face(look=(0, -0.03), hl=[(0.26, 0.06, 0.36), (-0.34, -0.62, 0.15)], lidT=(0.11, 0.11)))),
        ("Hallo!", "Start, Startseite", dict(pose=("rest", "wave"), tilt=2, face=face())),
        ("Freut sich", "Session geschafft, Hüpfer", dict(pose="cheer", lift=0.10, face=face(**HAPPY_EYES, mouth="open", mw=0.105, md=0.050, mzo=0.01, sparkles=True))),
        ("Kichert", "Antippen", dict(pose="rest", tilt=5, face=face(eyes="squint", mouth="cat", mw=0.085, md=0.038, chA=0.95, chS=1.15, lines=True, hihi=True))),
        ("Zwinkert", "Antippen", dict(pose=("rest", "wave"), tilt=6, face=face(eyes=("open", "happy"), sparkle=True, mouth="tongue", mw=0.085, md=0.040))),
        ("Verlegen", "Antippen", dict(pose=("rest", "head"), tilt=-7, face=face(look=(-0.07, -0.035), es=0.95, hl=[(0.0, 0.30, 0.34), (-0.40, -0.38, 0.14)],
                                                                    lidT=(0.10, 0.10), mouth="wavy", mw=0.065, md=0.035, chA=1.0, chS=1.25, lines=True))),
    ]),
    ("Staunen, Bitten, Schreck", (0xF4, 0xF3, 0xF7), [
        ("Staunt", "Antippen: leuchtet", dict(pose="open", face=face(es=1.12, hl=[(0.24, 0.36, 0.38), (-0.36, -0.42, 0.16)], sparkle=True,
                                                                         brows=(0.0, 0.06), mouth="o", mw=0.050, md=0.040, rays=True))),
        ("Stolz", "Ziel / Streak geschafft", dict(pose="hips", tilt=-3, face=face(**HAPPY_EYES, mouth="laugh", mw=0.090, md=0.042, sparkles=True))),
        ("Bittet", "Abbrechen-Dialog", dict(pose="clasp", tilt=6, face=face(es=1.12, hl=[(0.20, 0.38, 0.40), (-0.38, -0.30, 0.18), (0.40, -0.18, 0.10)],
                                                                          wet=0.8, brows=(0.12, 0.0), mouth="wavy", mw=0.055, md=0.030, chA=0.9))),
        ("Erschrickt", "Abbruch: Buch rutscht", dict(pose="cheeks", lift=0.04, face=face(es=1.12, hl=[(0.20, 0.30, 0.22), (-0.35, -0.40, 0.10)],
                                                                                   brows=(0.06, 0.10), mouth="o", mw=0.068, md=0.062, chA=0.55, sweat=True))),
        ("Buch fällt", "Abbruch", dict(pose="open", bookFall=True, face=face(look=(0.06, -0.07), es=1.05, hl=[(0.28, 0.10, 0.30), (-0.30, -0.55, 0.13)],
                                                                              brows=(0.10, 0.04), mouth="o", mw=0.042, md=0.034, chA=0.55, sweat=True))),
        ("Traurig", "Abbruch, Licht noch an", dict(pose="droop", tilt=-4, lift=-0.03, bookDown=True,
                                                   face=face(look=(0.03, -0.05), hl=[(0.24, 0.10, 0.34), (-0.34, -0.42, 0.15)], wet=0.7,
                                                             lidT=(0.34, 0.12), brows=(0.12, -0.02), mouth="frown", mw=0.060, md=0.022, chA=0.6))),
    ]),
    ("Ausgehen (Abbruch zählt nicht)", (0x0B, 0x0B, 0x0E), [
        ("Flackert", "Licht geht aus", dict(pose="droop", light=0.45, glowA=0.6, bookDown=True,
                                           face=face(look=(0.0, 0.05), hl=[(0.22, 0.42, 0.34), (-0.34, -0.30, 0.15)], wet=0.5,
                                                     brows=(0.14, 0.04), mouth="wavy", mw=0.065, md=0.035, chA=0.6, zap=True))),
        ("Aus: weint", "so ist es jetzt", dict(pose="droop", light=0.0, tilt=-3, lift=-0.04, bookDown=True,
                                              face=face(eyes="closed", slant=0.25, brows=(0.14, -0.02), mouth="cry", mw=0.085, md=0.040, chA=0.6, tears="stream"))),
        ("Aus: still traurig", "leiser, eine Träne", dict(pose="droop", light=0.0, tilt=-4, lift=-0.04, bookDown=True,
                                                         face=face(look=(0.02, -0.05), hl=[(0.24, 0.05, 0.30), (-0.34, -0.45, 0.13)], wet=0.8,
                                                                   lidT=(0.40, 0.15), brows=(0.12, -0.03), mouth="frown", mw=0.050, md=0.020, chA=0.5, tears="drop"))),
        ("Aus: Rauchwölkchen", "wie eine ausgepustete Kerze", dict(pose="droop", light=0.0, tilt=-5, lift=-0.04, bookDown=True,
                                                          face=face(eyes="closed", slant=0.2, mouth="wavy", mw=0.060, md=0.030, chA=0.5, smoke=True))),
        ("Aus: schmollt", "beleidigt, schaut weg", dict(pose="droop", light=0.0, tilt=4, lift=-0.04, bookDown=True,
                                                       face=face(look=(0.08, 0.0), hl=[(0.30, 0.20, 0.30), (-0.30, -0.45, 0.13)], lidT=(0.42, 0.42),
                                                                 brows=(-0.06, -0.02), mouth="pout", mxo=0.05, mw=0.060, md=0.028, chA=0.85, chS=1.30))),
        ("Aus: leer", "Licht weg, auch in den Augen", dict(pose="droop", light=0.0, tilt=-3, lift=-0.05, bookDown=True,
                                                         face=face(look=(0.0, -0.06), es=0.62, dull=True, mouth="flat", mw=0.040, md=0.020, chA=0.25))),
    ]),
]

def sheet(out, CW=340):
    SS = 3; pu = CW / 2.35
    W = int(2.8 * pu * SS)
    fb = "/usr/share/fonts/opentype/inter/Inter-Bold.otf"; fr = "/usr/share/fonts/opentype/inter/Inter-Regular.otf"
    k_ = CW / 300
    fH = ImageFont.truetype(fb, int(24 * k_)); fT = ImageFont.truetype(fb, int(18 * k_)); fD = ImageFont.truetype(fr, int(12.5 * k_))
    ncol = max(len(r[2]) for r in ROWS)
    cellH = int(2.74 * pu); lab = int(48 * k_); rowH = int(40 * k_)
    total = sum(rowH + lab + cellH + 10 for _ in ROWS) + 10
    img = Image.new("RGB", (CW * ncol, total), (255, 255, 255)); d = ImageDraw.Draw(img)
    y = 6
    for title, bg, cells in ROWS:
        d.text((16, y + 6), title, font=fH, fill=INK); y += rowH
        for i, (name, sub, e) in enumerate(cells):
            x = i * CW
            d.text((x + 14, y + 2), name, font=fT, fill=INK)
            d.text((x + 14, y + int(25 * k_)), sub, font=fD, fill=(0x7A, 0x75, 0x88))
            fr_ = render(e, W, bg)
            s = W / 2.8
            c = fr_.crop((int((-1.175 + 1.4) * s), int((2.8 - 2.80) * s), int((1.175 + 1.4) * s), int((2.8 - 2.80) * s + 2.74 * s)))
            img.paste(c.resize((CW, cellH), Image.LANCZOS), (x, y + lab))
        y += lab + cellH + 10
    img.save(out); return img

if __name__ == "__main__":
    sheet(sys.argv[1] if len(sys.argv) > 1 else "/tmp/Lumi_B_Gefuehle.png")
