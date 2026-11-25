"""
Lumi-Rig: Gesicht und Arme als Parameter, damit Ausdrücke und Posen fließend ineinander übergehen.
Dieselben Zahlen stehen 1:1 in der App (LumiArt.kt). Hier dient das Ganze als Vorschau/Prototyp.
Koordinaten: Einheiten, x -1.4..1.4, z 0..2.8 (z nach oben).
"""
import os
import math, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L
from PIL import Image, ImageDraw

INK = (0x1E, 0x1A, 0x26); BULB = (0xFF, 0xC9, 0x3C); TEAR = (0x8F, 0xD3, 0xFF)
MOUTH = (0x3A, 0x1F, 0x2B); TONGUE = (0xFF, 0x6F, 0x7D); CHEEK = (0xFF, 0x8F, 0xA3); WHITE = (255, 255, 255)
DUST = (0xE9, 0xE4, 0xDA)
EYE_X, EYE_Z = 0.235, 1.62

# ------------------------------------------------------------------------------------------
# Gesichter
# ------------------------------------------------------------------------------------------
FACE_KEYS = ["rx", "rz", "dx", "dz", "lidO", "lidI", "lidA", "h1x", "h1z", "h1r", "h2x", "h2z", "h2r",
             "shut", "arcE", "arcC", "arcW", "browA", "browO", "browI", "browC",
             "mx", "mz", "mTop", "mBot", "mFill", "mTongue", "mW", "chA", "chRx", "chRz", "chZ",
             "sweat", "rim", "tears"]
def face(**kw):
    base = dict(rx=.074, rz=.088, dx=0, dz=0, lidO=.070, lidI=.070, lidA=.018,
                h1x=.022, h1z=-.028, h1r=.019, h2x=-.020, h2z=-.050, h2r=.009,
                shut=0, arcE=.005, arcC=-.045, arcW=.032, browA=0, browO=.15, browI=.15, browC=.17,
                mx=.105, mz=1.455, mTop=1.375, mBot=1.375, mFill=0, mTongue=0, mW=.034,
                chA=.92, chRx=.072, chRz=.044, chZ=1.485, sweat=0, rim=0, tears=0)
    base.update(kw)
    return base

FACES = {
    # liest: Blick aufs Buch, entspannt
    "read": face(),
    # erschrocken, schaut dich an
    "scared": face(rx=.088, rz=.106, dz=.015, lidO=.2, lidI=.2, lidA=0,
                   h1x=.026, h1z=.034, h1r=.028, h2x=-.030, h2z=-.040, h2r=.012,
                   browA=1, browO=.150, browI=.205, browC=.200,
                   mx=.050, mz=1.405, mTop=1.490, mBot=1.320, mFill=1, mTongue=.8, mW=.026,
                   chA=.45, sweat=1),
    # erschrocken, schaut dem fallenden Buch hinterher (nach unten rechts)
    "scaredDown": face(rx=.086, rz=.102, dx=.014, dz=-.004, lidO=.2, lidI=.2, lidA=0,
                       h1x=.030, h1z=-.012, h1r=.026, h2x=-.022, h2z=-.050, h2r=.011,
                       browA=1, browO=.150, browI=.210, browC=.205,
                       mx=.054, mz=1.405, mTop=1.505, mBot=1.300, mFill=1, mTongue=.8, mW=.026,
                       chA=.45, sweat=.6),
    # traurig, schaut auf das Buch am Boden, Augen werden feucht
    "sad": face(rx=.080, rz=.094, dx=.012, dz=-.020, lidO=.2, lidI=.2, lidA=0,
                h1x=.026, h1z=.024, h1r=.026, h2x=-.028, h2z=-.012, h2r=.011,
                browA=1, browO=.138, browI=.192, browC=.172,
                mx=.085, mz=1.375, mTop=1.430, mBot=1.430, mFill=0, mW=.032,
                chA=.55, rim=.85),
    # weint: Mund offen, Tränen laufen
    "cry": face(rx=.080, rz=.090, dx=.006, dz=-.018, lidO=.2, lidI=.2, lidA=0,
                h1x=.026, h1z=.022, h1r=.026, h2x=-.028, h2z=-.012, h2r=.011,
                browA=1, browO=.132, browI=.200, browC=.176,
                mx=.095, mz=1.365, mTop=1.500, mBot=1.400, mFill=1, mTongue=.55, mW=.030,
                chA=.7, rim=1, tears=1),
    # Freude
    "happy": face(shut=1, arcE=-.025, arcC=.095, arcW=.036,
                  mx=.135, mz=1.475, mTop=1.495, mBot=1.215, mFill=1, mTongue=1, mW=.030,
                  chA=.95, chRx=.080, chRz=.050, chZ=1.50),
}

def lerp(a, b, t): return a + (b - a) * t
def lerp_face(a, b, t): return {k: lerp(a[k], b[k], t) for k in FACE_KEYS}
def ease(t):  # FastOutSlowIn-ähnlich
    t = max(0.0, min(1.0, t)); return t * t * (3 - 2 * t)
def smooth(a, b, x):
    return ease((x - a) / (b - a))

# ------------------------------------------------------------------------------------------
# Arme (eine Seite, gespiegelt); Schulter fest am Hals
# ------------------------------------------------------------------------------------------
ARM_KEYS = ["c1x", "c1z", "c2x", "c2z", "ex", "ez", "hx", "hz", "hrx", "hrz", "fx", "fz", "frx", "frz", "fA"]
def arm(c1, c2, e, h, f, fA):
    return dict(c1x=c1[0], c1z=c1[1], c2x=c2[0], c2z=c2[1], ex=e[0], ez=e[1], hx=h[0], hz=h[1], hrx=h[2], hrz=h[3],
                fx=f[0], fz=f[1], frx=f[2], frz=f[3], fA=fA)
def both(x): return {"L": x, "R": x}
ARMS = {
    # seit 15.0 schwingen die Arme weniger weit aus (wirken kürzer), Hände bleiben am Buch – wie in LumiArt.kt
    "hold": both(arm((.80, 1.02), (.82, .62), (.47, .70), (.43, .735, .085, .105), (.375, .875, .040, .050), 1)),
    # Schreck beim Loslassen: Hände fliegen kurz auf und nach oben ("oh nein!"), weg vom fallenden Buch
    "open": {"L": arm((.70, 1.10), (.88, 1.02), (.76, .90), (.755, .875, .078, .095), (.73, .95, .034, .042), 0),
             "R": arm((.70, 1.08), (.90, .98), (.78, .86), (.775, .835, .078, .095), (.75, .91, .034, .042), 0)},
    # schlaff hängend, nah am Körper
    "hang": {"L": arm((.58, 1.00), (.61, .78), (.53, .67), (.515, .625, .072, .088), (.50, .66, .03, .04), 0),
             "R": arm((.58, 1.00), (.62, .75), (.555, .62), (.545, .58, .072, .088), (.53, .62, .03, .04), 0)},
}
def lerp_arm(a, b, t): return {side: {k: lerp(a[side][k], b[side][k], t) for k in ARM_KEYS} for side in ("L", "R")}
SHOULDER = (0.40, 1.02)

# ------------------------------------------------------------------------------------------
# Buch am Boden (aufgeklappt, mit dem Rücken nach oben gelandet)
# ------------------------------------------------------------------------------------------
BOOK_REST_X, FLOOR_Z = 0.74, 0.13
def book_down_shapes(cx=BOOK_REST_X, fz=FLOOR_Z, variant="lying"):
    if variant == "lying":
        import book_proto
        return book_proto.lying_book(cx, fz)
    def T(pts): return [(cx + x, fz + z) for x, z in pts]
    out = []
    if variant == "tent":
        # aufgeklappt, mit dem Rücken nach oben gelandet: dicke Deckel wie ein kleines Dach
        pages = L.quad((-0.36, 0.0), (-0.18, 0.15), (0.0, 0.20), 14) + L.quad((0.0, 0.20), (0.18, 0.15), (0.36, 0.0), 14)[1:]
        out += [L.F(T(pages), "#FFF8EA"), L.Lz(T(pages), 0.022, closed=True)]
        for k in (0.05, 0.10):
            out.append(L.Lz(T(L.quad((-0.36 + k * 1.3, 0.0), (-0.16, 0.15 - k), (0.0, 0.20 - k * 0.9), 12)), 0.011, "#C9BDA3"))
            out.append(L.Lz(T(L.quad((0.0, 0.20 - k * 0.9), (0.16, 0.15 - k), (0.36 - k * 1.3, 0.0), 12)), 0.011, "#C9BDA3"))
        for sx in (-1, 1):
            board = [(sx * 0.50, -0.01), (sx * 0.02, 0.33), (sx * 0.0, 0.235), (sx * 0.38, -0.01)]
            out += [L.F(T(board), "#7E6CF2"),
                    L.F(T([(sx * 0.46, -0.004), (sx * 0.06, 0.28), (sx * 0.06, 0.255), (sx * 0.42, -0.004)]), "#A594FF"),
                    L.Lz(T(board), 0.026, closed=True)]
        spine = L.rrect(0.0, 0.30, 0.09, 0.07, 0.03)
        out += [L.F(T(spine), "#5B4BD6"), L.Lz(T(spine), 0.024, closed=True)]
        band = [(0.10, 0.0), (0.145, 0.0), (0.16, -0.06), (0.13, -0.045), (0.105, -0.065)]
        out += [L.F(T(band), "#FF6F7D"), L.Lz(T(band), 0.018, closed=True)]
    else:
        # zugeklappt, flach liegend, leicht von oben gesehen
        top = [(-0.44, 0.10), (0.44, 0.10), (0.37, 0.22), (-0.37, 0.22)]
        front = L.rrect(0.0, 0.05, 0.84, 0.10, 0.015)
        lip = L.rrect(0.0, 0.0, 0.90, 0.035, 0.015)
        out += [L.F(T(lip), "#5B4BD6"), L.Lz(T(lip), 0.024, closed=True)]
        out += [L.F(T(front), "#FFF8EA"), L.Lz(T([(-0.38, 0.035), (0.38, 0.035)]), 0.011, "#C9BDA3"),
                L.Lz(T([(-0.38, 0.065), (0.38, 0.065)]), 0.011, "#C9BDA3"), L.Lz(T(front), 0.024, closed=True)]
        out += [L.F(T(top), "#7E6CF2"), L.F(T([(-0.30, 0.15), (0.24, 0.15), (0.22, 0.17), (-0.29, 0.17)]), "#A594FF"),
                L.Lz(T(top), 0.026, closed=True)]
        band = [(0.16, 0.10), (0.20, 0.10), (0.20, -0.03), (0.18, -0.015), (0.16, -0.03)]
        out += [L.F(T(band), "#FF6F7D"), L.Lz(T(band), 0.018, closed=True)]
    return out

# ------------------------------------------------------------------------------------------
# Zeichnen (PIL, für Vorschau)
# ------------------------------------------------------------------------------------------
class Canvas:
    def __init__(self, W, bg=(9, 9, 11)):
        self.W = W; self.img = Image.new("RGBA", (W, W), bg + (255,)); self.T = []
    def px(self, x, z):
        q = ((x + 1.4) / 2.8 * self.W, (2.8 - z) / 2.8 * self.W)
        for t in self.T: q = apply(t, q, self.W)
        return q
    def u(self, w): return w / 2.8 * self.W
    def layer(self): return Image.new("RGBA", self.img.size, (0, 0, 0, 0))
    def comp(self, lay, mask=None):
        if mask is not None:
            a = lay.getchannel("A"); from PIL import ImageChops
            lay.putalpha(ImageChops.multiply(a, mask))
        self.img.alpha_composite(lay)
    def poly(self, pts, col, a=1.0, mask=None):
        if a <= 0.003: return
        lay = self.layer(); ImageDraw.Draw(lay).polygon([self.px(*p) for p in pts], fill=col + (int(255 * min(1, a)),)); self.comp(lay, mask)
    def line(self, pts, w, col=INK, a=1.0, closed=False):
        if a <= 0.003: return
        lay = self.layer(); d = ImageDraw.Draw(lay); P = [self.px(*p) for p in pts]
        if closed: P = P + [P[0]]
        ws = 1.0
        for t in self.T: ws *= t["sx"]
        ww = max(1, int(self.u(w) * ws)); c = col + (int(255 * min(1, a)),)
        d.line(P, fill=c, width=ww, joint="curve")
        for q in (P[0], P[-1]): d.ellipse([q[0] - ww / 2, q[1] - ww / 2, q[0] + ww / 2, q[1] + ww / 2], fill=c)
        self.comp(lay)
    def ell(self, cx, cz, rx, rz, col, a=1.0, mask=None, n=40):
        self.poly(L.ellipse(cx, cz, rx, rz, n), col, a, mask)
    def mask_of(self, pts):
        m = Image.new("L", self.img.size, 0); ImageDraw.Draw(m).polygon([self.px(*p) for p in pts], fill=255); return m
    def shapes(self, shapes, a=1.0):
        for s in shapes:
            c = tuple(int(s["color"].lstrip('#')[i:i + 2], 16) for i in (0, 2, 4))
            if s["kind"] == "fill": self.poly(s["pts"], c, s["alpha"] * a)
            else: self.line(s["pts"], s["width"], c, a, s.get("closed", False))

def xform(rot=0, tx=0, ty=0, sx=1, sy=1, ox=0.5, oy=0.5):
    return dict(rot=rot, tx=tx, ty=ty, sx=sx, sy=sy, ox=ox, oy=oy)
def apply(T, q, W):
    px, py = T["ox"] * W, T["oy"] * W
    dx, dy = (q[0] - px) * T["sx"], (q[1] - py) * T["sy"]
    a = math.radians(T["rot"])
    return (px + dx * math.cos(a) - dy * math.sin(a) + T["tx"] * W, py + dx * math.sin(a) + dy * math.cos(a) + T["ty"] * W)

def draw_arm_lines(cv, AA):
    for sx in (-1, 1):
        A = AA["L" if sx < 0 else "R"]
        pts = L.cubic((sx * SHOULDER[0], SHOULDER[1]), (sx * A["c1x"], A["c1z"]), (sx * A["c2x"], A["c2z"]), (sx * A["ex"], A["ez"]), 28)
        cv.line(pts, 0.08)
def draw_hands(cv, AA):
    for sx in (-1, 1):
        A = AA["L" if sx < 0 else "R"]
        cv.ell(sx * A["hx"], A["hz"], A["hrx"], A["hrz"], INK)
        cv.ell(sx * A["fx"], A["fz"], A["frx"], A["frz"], INK, A["fA"])

def drop_pts(x, z, r): return L.drop_shape(x, z, r)

def draw_face(cv, F, T=0.0):
    # Wangen
    for sx in (-1, 1): cv.ell(sx * 0.40, F["chZ"], F["chRx"], F["chRz"], CHEEK, F["chA"])
    # Mund
    Lc, Rc = (-F["mx"], F["mz"]), (F["mx"], F["mz"])
    mouth = L.quad(Lc, (0, F["mTop"]), Rc, 16) + L.quad(Rc, (0, F["mBot"]), Lc, 16)[1:-1]
    if F["mFill"] > 0.01:
        cv.poly(mouth, MOUTH, F["mFill"])
        zb = F["mz"] + (F["mBot"] - F["mz"]) / 2
        cv.ell(0, zb + 0.022, F["mx"] * 0.55, 0.03, TONGUE, F["mTongue"] * F["mFill"], mask=cv.mask_of(mouth))
    cv.line(mouth, F["mW"], closed=True)
    # Augen
    shut = F["shut"]
    for sx in (-1, 1):
        ex, ez = sx * EYE_X + F["dx"], EYE_Z + F["dz"]
        rz = F["rz"] * (1 - shut)
        if rz > 0.004:
            eye = L.ellipse(ex, ez, F["rx"], rz, 40)
            em = cv.mask_of(eye)
            cv.poly(eye, INK)
            cv.ell(ex, ez - rz * 0.95, F["rx"] * 0.95, rz * 0.55, TEAR, F["rim"] * 0.85, mask=em)
            k = 1 - shut
            cv.ell(ex + F["h1x"], ez + F["h1z"] * k, F["h1r"] * k, F["h1r"] * k, WHITE)
            cv.ell(ex + F["h2x"], ez + F["h2z"] * k, F["h2r"] * k, F["h2r"] * k, WHITE)
            # Lid (Kugelfarbe über dem oberen Augenrand)
            ox, ix = ex + sx * (F["rx"] + 0.02), ex - sx * (F["rx"] + 0.02)
            edge = L.quad((ox, ez + F["lidO"]), (ex, ez + (F["lidO"] + F["lidI"]) / 2 + F["lidA"]), (ix, ez + F["lidI"]), 12)
            cv.poly(edge + [(ix, ez + 0.4), (ox, ez + 0.4)], BULB, mask=em)
        aa = smooth(0.55, 1.0, shut)
        cv.line(L.quad((ex - 0.08, ez + F["arcE"]), (ex, ez + F["arcC"]), (ex + 0.08, ez + F["arcE"]), 14), F["arcW"], a=aa)
        # Brauen
        if F["browA"] > 0.01:
            o = (ex + sx * 0.085, ez + F["browO"]); i = (ex - sx * 0.068, ez + F["browI"]); c = (ex + sx * 0.01, ez + F["browC"])
            cv.line(L.quad(o, c, i, 12), 0.025, a=F["browA"])
    # Schweißtropfen
    if F["sweat"] > 0.01:
        k = ease(F["sweat"]); r = 0.045 * k
        if r >= 0.006:
            d = drop_pts(0.60, 1.86 - 0.02 * (1 - k), r)
            cv.poly(d, TEAR); cv.ell(0.60 - r * 0.35, 1.86 + r * 0.15, r * 0.28, r * 0.34, WHITE, 0.9)
            cv.line(d, 0.02 * smooth(0.006, 0.03, r), closed=True)
    # Tränen laufen über die Wangen: bilden sich am Augenrand, laufen schneller werdend herunter, tropfen ab
    if F["tears"] > 0.01:
        period = 1.5
        for sx, off in ((1, 0.0), (-1, 0.75)):
            u = ((T + off) / period) % 1.0
            ex, ez = sx * EYE_X + F["dx"], EYE_Z + F["dz"]
            sx0, sz0 = ex + sx * 0.048, ez - 0.080
            if u < 0.28:
                g = u / 0.28; x, z, r = sx0, sz0, 0.032 * ease(g)
            else:
                g = (u - 0.28) / 0.72; x, z = sx0 + sx * 0.04 * g, sz0 - 0.30 * g * g
                r = 0.032 * (1 - smooth(0.80, 1.0, g))
            r *= ease(F["tears"])
            if r < 0.006: continue
            d = drop_pts(x, z, r)
            cv.poly(d, TEAR); cv.ell(x - r * 0.35, z + r * 0.15, r * 0.28, r * 0.34, WHITE, 0.9)
            cv.line(d, 0.018 * smooth(0.006, 0.022, r), closed=True)
