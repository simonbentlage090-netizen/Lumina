"""Lumi-Bilder für das Lumi-Widget (9.0): je Situation ein Bild, hell + dunkel, transparent."""
import sys, os, math, inspect
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import emotions as E, lumi_rig as R, lumi_shapes as L
from PIL import Image

class TCanvas(R.Canvas):
    def __init__(self, W, bg=(0, 0, 0)):
        self.W = W; self.img = Image.new("RGBA", (W, W), (0, 0, 0, 0)); self.T = []

src = inspect.getsource(E.render).replace("R.Canvas(W, bg)", "TCanvas(W, bg)").replace('return cv.img.convert("RGB")', 'return cv, b')
src = src.replace("def render(", "def render_t(")
E.__dict__["TCanvas"] = TCanvas
exec(src, E.__dict__)
render_t = E.__dict__["render_t"]
face = E.face
INK = E.INK
FL_O = (0xFF, 0x8A, 0x3D); FL_M = (0xFF, 0xB8, 0x3A); FL_I = (0xFF, 0xE0, 0x6A)
ICE = (0x5F, 0xA8, 0xE6); ICE_L = (0xBF, 0xE3, 0xFF)
HEART = (0xFF, 0x6B, 0x8A)

def flame_pts(x, z, h, w, wob=0.0):
    pts = []
    n = 40
    for i in range(n + 1):
        t = i / n * 2 * math.pi
        # Tropfen mit Spitze oben
        s = math.sin(t); c = math.cos(t)
        r = 1.0
        px = x + w * s * (0.55 + 0.45 * (1 - c) / 2) * (1 - 0.15 * c)
        pz = z + h * (-0.42 * c + 0.58 * (1 - c) / 2 * 0 ) 
        pts.append((px, pz))
    return pts

def _drop(cx, cz, rw, rh, bend):
    pts = []
    for i in range(25):
        a = math.pi + math.pi * i / 24
        pts.append((cx + rw * math.cos(a), cz + rw * 0.95 * math.sin(a)))
    us = [j / 18 for j in range(1, 19)]
    right = [(cx + rw * (1 - u) ** 1.35 * (1 + 0.30 * math.sin(u * math.pi)) + bend * u * u, cz + rh * u) for u in us]
    left = [(cx - rw * (1 - u) ** 1.35 * (1 + 0.30 * math.sin(u * math.pi)) + bend * u * u, cz + rh * u) for u in us[:-1]]
    return pts + right + left[::-1]

def _rot(pts, ox, oz, deg):
    a = math.radians(deg); c, s_ = math.cos(a), math.sin(a)
    return [(ox + (x - ox) * c - (z - oz) * s_, oz + (x - ox) * s_ + (z - oz) * c) for x, z in pts]

def flame(cv, x, z, k=1.0, dim=False):
    main = _drop(x, z, 0.16 * k, 0.44 * k, -0.07 * k)
    side = _rot(_drop(x - 0.10 * k, z + 0.02 * k, 0.075 * k, 0.24 * k, -0.03 * k), x - 0.10 * k, z + 0.02 * k, 22)
    mid = _drop(x + 0.005 * k, z - 0.04 * k, 0.105 * k, 0.27 * k, -0.04 * k)
    core = _drop(x + 0.01 * k, z - 0.075 * k, 0.058 * k, 0.15 * k, -0.02 * k)
    o, m, c = ((0xF2, 0x9A, 0x6E), (0xF7, 0xC0, 0x80), (0xFB, 0xE2, 0xA8)) if dim else (FL_O, FL_M, FL_I)
    for sh in (main, side):
        cv.line(sh, 0.052, INK, closed=True)
    cv.poly(main, o); cv.poly(side, o); cv.poly(mid, m); cv.poly(core, c)

def zzz(cv, x, z, k=1.0, col=None):
    col = col or (0x9A, 0x96, 0xB8)
    for j, (dx, dz, s) in enumerate(((0, 0, 0.10), (0.13, 0.16, 0.13), (0.30, 0.36, 0.165))):
        s *= k; x0, z0 = x + dx * k, z + dz * k
        cv.line([(x0, z0 + s), (x0 + s, z0 + s), (x0, z0), (x0 + s, z0)], 0.032 * (0.8 + j * 0.15), col)

def snowflake(cv, x, z, r, col=ICE):
    for a in (90, 30, -30):
        ra = math.radians(a)
        dx, dz = r * math.cos(ra), r * math.sin(ra)
        cv.line([(x - dx, z - dz), (x + dx, z + dz)], 0.034, col)
        for sgn in (-1, 1):
            bx, bz = x + sgn * dx * 0.62, z + sgn * dz * 0.62
            for b in (-1, 1):
                rb = ra + b * math.radians(40) + (0 if sgn > 0 else math.pi)
                cv.line([(bx, bz), (bx + 0.32 * r * math.cos(rb), bz + 0.32 * r * math.sin(rb))], 0.030, col)
    cv.ell(x, z, 0.032, 0.032, col)

def heart(cv, x, z, s, broken=False):
    pts = []
    for i in range(60):
        t = 2 * math.pi * i / 60
        hx = 16 * math.sin(t) ** 3; hz = 13 * math.cos(t) - 5 * math.cos(2 * t) - 2 * math.cos(3 * t) - math.cos(4 * t)
        pts.append((x + hx / 17 * s, z + hz / 17 * s))
    cv.poly(pts, HEART); cv.line(pts, 0.024, INK, closed=True)
    cv.ell(x - 0.45 * s, z + 0.30 * s, 0.16 * s, 0.11 * s, (255, 255, 255), 0.8)
    if broken:
        crack = [(x + 0.02 * s, z + 0.55 * s), (x - 0.12 * s, z + 0.25 * s), (x + 0.10 * s, z + 0.0), (x - 0.06 * s, z - 0.30 * s), (x + 0.02 * s, z - 0.62 * s)]
        cv.line(crack, 0.026, INK)

def bang(cv, x, z, k=1.0):
    col = (0xE2, 0x4F, 0x50)
    cv.line([(x, z + 0.26 * k), (x - 0.015 * k, z + 0.07 * k)], 0.05 * k, col)
    cv.ell(x - 0.02 * k, z - 0.03 * k, 0.03 * k, 0.03 * k, col)


def bolt(cv, x, z, k=1.0, rot=0.0, col=(0xFF, 0xD2, 0x3C)):
    """Blitz (Zickzack, gefüllt) mit Kontur."""
    base = [(0.02, 0.30), (-0.10, 0.02), (-0.01, 0.02), (-0.06, -0.24), (0.11, 0.07), (0.02, 0.07), (0.09, 0.30)]
    a = math.radians(rot); c, s_ = math.cos(a), math.sin(a)
    pts = [(x + (u * c - v * s_) * k, z + (u * s_ + v * c) * k) for u, v in base]
    cv.line(pts, 0.030, INK, closed=True)
    cv.poly(pts, col)

def drop(cv, x, z, r, a=1.0):
    d = L.drop_shape(x, z, r)
    cv.poly(d, ICE_L, a); cv.ell(x - r * 0.35, z + r * 0.15, r * 0.26, r * 0.32, (255, 255, 255), 0.9 * a)
    cv.line(d, 0.016, ICE, a=a, closed=True)

def puddle(cv, x, z, w):
    cv.ell(x, z, w, w * 0.18, ICE_L, 0.9); cv.ell(x - w * 0.3, z + w * 0.03, w * 0.25, w * 0.05, (255, 255, 255), 0.7)

HAPPY = dict(eyes="happy", chA=0.92, chS=1.12)
OPEN_BIG = dict(es=1.12, hl=[(0.20, 0.38, 0.40), (-0.38, -0.30, 0.18), (0.40, -0.18, 0.10)])
DET = dict(brows=(-0.07, 0.02))

S = {
  "hello":  (dict(pose=("rest", "wave"), tilt=2, face=face()), None),
  "read":   (dict(pose="hold", face=face(look=(0, -0.03), hl=[(0.26, 0.06, 0.36), (-0.34, -0.62, 0.15)], lidT=(0.11, 0.11))), None),
  "wink":   (dict(pose=("rest", "wave"), tilt=6, face=face(eyes=("open", "happy"), sparkle=True, mouth="tongue", mw=0.085, md=0.040)), None),
  "cheer":  (dict(pose="cheer", lift=0.10, face=face(**HAPPY, mouth="open", mw=0.105, md=0.050, mzo=0.01, sparkles=True)), None),
  "proud":  (dict(pose="hips", tilt=-3, face=face(**HAPPY, mouth="laugh", mw=0.090, md=0.042, sparkles=True)), None),
  "fire":   (dict(pose=("hips", "cheer"), tilt=-3, face=face(es=1.05, sparkle=True, hl=[(0.24, 0.36, 0.38), (-0.36, -0.42, 0.16)], mouth="laugh", mw=0.095, md=0.046, **DET)), "fire"),
  "wow":    (dict(pose="open", face=face(es=1.12, hl=[(0.24, 0.36, 0.38), (-0.36, -0.42, 0.16)], sparkle=True, brows=(0.0, 0.06), mouth="o", mw=0.050, md=0.040, rays=True)), None),
  "go":     (dict(pose="cheer", tilt=0, face=face(es=1.0, mouth="laugh", mw=0.085, md=0.040, **DET)), None),
  "giggle": (dict(pose="rest", tilt=5, face=face(eyes="squint", mouth="cat", mw=0.085, md=0.038, chA=0.95, chS=1.15, lines=True, hihi=True)), None),
  "plead":  (dict(pose="clasp", tilt=6, face=face(**OPEN_BIG, wet=0.9, brows=(0.14, 0.0), mouth="wavy", mw=0.055, md=0.030, chA=0.9)), None),
  "scared": (dict(pose="cheeks", lift=0.04, face=face(es=1.12, hl=[(0.20, 0.30, 0.22), (-0.35, -0.40, 0.10)], brows=(0.08, 0.10), mouth="o", mw=0.068, md=0.062, chA=0.55, sweat=True)), "danger"),
  "sad":    (dict(pose="droop", tilt=-4, lift=-0.03, bookDown=True, face=face(look=(0.03, -0.05), **OPEN_BIG, wet=0.9, lidT=(0.30, 0.10), brows=(0.14, -0.02), mouth="frown", mw=0.060, md=0.022, chA=0.6, tears="drop")), None),
  "cry":    (dict(pose="droop", light=0.35, glowA=0.5, tilt=-3, lift=-0.04, bookDown=True, face=face(es=1.15, hl=[(0.20, 0.38, 0.40), (-0.38, -0.30, 0.18)], wet=1.0, lidT=(0.12, 0.0), brows=(0.16, -0.02), mouth="cry", mw=0.080, md=0.040, chA=0.6, tears="stream")), None),
  "miss":   (dict(pose="droop", light=0.0, tilt=-4, lift=-0.04, face=face(look=(0.02, -0.05), **OPEN_BIG, wet=1.0, lidT=(0.28, 0.06), brows=(0.14, -0.03), mouth="frown", mw=0.050, md=0.020, chA=0.5, tears="drop")), "heart"),
  "sleep":  (dict(pose="droop", light=0.0, tilt=-6, lift=-0.04, face=face(eyes="closed", slant=0.0, mouth="smile", mw=0.040, md=0.014, chA=0.7)), "zzz"),
  "zap":    (dict(pose="cheer", lift=0.08, face=face(**HAPPY, mouth="laugh", mw=0.095, md=0.046, zap=True)), "zap"),
  "thaw":   (dict(pose=("rest", "wave"), tilt=4, face=face(eyes="happy", chA=0.95, chS=1.15, mouth="smile", mw=0.080, md=0.034, sweat=True)), "thaw"),
  "icebreak": (dict(pose=("rest", "wave"), tilt=3, face=face(es=1.05, hl=[(0.24, 0.36, 0.38), (-0.36, -0.42, 0.16)], sparkle=True, mouth="tongue", mw=0.080, md=0.036, chA=1.0, chS=1.2, lines=True)), "iceblock"),
  "frozen": (dict(pose="clasp", light=0.06, glowA=0.25, tilt=4, lift=-0.02, face=face(es=1.16, hl=[(0.20, 0.38, 0.40), (-0.38, -0.30, 0.18), (0.40, -0.18, 0.10)], wet=1.0, lidT=(0.06, 0.0), brows=(0.18, 0.02), mouth="wavy", mw=0.060, md=0.034, chA=0.35, chS=0.9)), "frozen"),
  "ice":    (dict(pose=("rest", "head"), tilt=-7, face=face(look=(-0.05, -0.02), es=1.0, hl=[(0.10, 0.32, 0.34), (-0.40, -0.38, 0.14)], mouth="wavy", mw=0.065, md=0.035, chA=1.0, chS=1.25, lines=True)), "ice"),
}

def make(name, dark, W=1800, out_w=360):
    e, extra = S[name]
    bg = (0x0B, 0x0B, 0x0E) if dark else (0xF4, 0xF3, 0xF7)
    cv, b = render_t(e, W, bg)
    Rr, CZ = b.R, b.CZ
    if extra == "fire":
        flame(cv, -0.90, CZ - 0.20, 0.95)
    elif extra == "danger":
        flame(cv, -0.93, CZ - 0.32, 0.72); bang(cv, 1.0, CZ + 0.55, 1.1)
    elif extra == "zzz":
        zzz(cv, 0.66, CZ + 0.52, 0.85, (0xB0, 0xAC, 0xC8) if dark else (0x8A, 0x86, 0xA6))
    elif extra == "ice":
        snowflake(cv, -0.98, CZ + 0.40, 0.16); snowflake(cv, 1.02, CZ + 0.62, 0.10); snowflake(cv, -0.80, CZ - 0.25, 0.08)
    elif extra == "zap":
        bolt(cv, -0.96, CZ + 0.42, 1.25, -14); bolt(cv, 1.00, CZ + 0.50, 1.05, 16); bolt(cv, -0.80, CZ - 0.28, 0.75, -24); bolt(cv, 0.95, CZ - 0.20, 0.7, 20)
    elif extra == "thaw":
        drop(cv, -0.92, CZ + 0.30, 0.060); drop(cv, -0.78, CZ - 0.05, 0.045, 0.85); drop(cv, 0.98, CZ + 0.05, 0.050, 0.9)
        snowflake(cv, 0.96, CZ + 0.58, 0.09, (0xA6, 0xD3, 0xF5))
        drop(cv, 0.93, CZ + 0.40, 0.035, 0.8)
    elif extra == "iceblock":
        # Eisblock bis knapp unter die Augen, oben zackig aufgebrochen, Lumi guckt heraus
        top = CZ - 0.20
        jag = [(-0.82, top - 0.02), (-0.62, top + 0.07), (-0.46, top - 0.04), (-0.28, top + 0.05), (-0.08, top - 0.03),
               (0.10, top + 0.06), (0.30, top - 0.02), (0.48, top + 0.08), (0.66, top - 0.01), (0.82, top + 0.04)]
        block = [(-0.82, 0.10)] + jag + [(0.82, 0.10)]
        cv.poly(block, ICE_L, 0.62)
        cv.poly([(-0.82, 0.10), (-0.82, top), (-0.70, top + 0.03), (-0.70, 0.16)], (255, 255, 255), 0.35)
        cv.line(block, 0.034, ICE, closed=True)
        # Risse
        cv.line([(0.20, top + 0.02), (0.12, top - 0.18), (0.24, top - 0.34), (0.16, top - 0.50)], 0.022, ICE)
        cv.line([(-0.40, top), (-0.34, top - 0.22), (-0.46, top - 0.40)], 0.020, ICE)
        cv.line([(0.55, 0.40), (0.62, 0.28), (0.56, 0.18)], 0.018, ICE)
        # Glanzstreifen
        cv.line([(-0.60, CZ - 0.55), (-0.38, CZ - 0.33)], 0.030, (255, 255, 255), a=0.7)
        cv.line([(0.40, CZ - 0.70), (0.58, CZ - 0.52)], 0.026, (255, 255, 255), a=0.6)
        # abspringende Eissplitter
        for (x, z, r_, a) in ((-0.95, top + 0.30, 0.06, 20), (0.98, top + 0.42, 0.05, -30), (0.80, top + 0.62, 0.035, 10)):
            pts = [(x, z + r_), (x + r_ * 0.8, z), (x, z - r_ * 0.9), (x - r_ * 0.7, z + 0.1 * r_)]
            cv.poly(pts, ICE_L); cv.line(pts, 0.016, ICE, closed=True)
        snowflake(cv, -1.0, CZ + 0.70, 0.09, (0xA6, 0xD3, 0xF5))
    elif extra == "frozen":
        # Lumi steckt ganz im Eisblock, das Licht ist fast aus
        top = CZ + 0.84
        # letzter Funke im Glühdraht
        for (r_, a) in ((0.20, 0.10), (0.12, 0.22), (0.06, 0.55)):
            cv.ell(0.0, CZ - 0.44, r_, r_ * 0.7, (0xFF, 0xA8, 0x3A), a)
        blk = [(-0.86, 0.06), (-0.90, top - 0.10), (-0.80, top), (-0.30, top + 0.03), (0.20, top - 0.01), (0.74, top + 0.02), (0.88, top - 0.12), (0.86, 0.06)]
        cv.poly(blk, ICE_L, 0.58)
        # Frost: hellere Flecken innen
        cv.ell(-0.42, CZ + 0.62, 0.30, 0.16, (255, 255, 255), 0.30)
        cv.ell(0.50, 0.40, 0.26, 0.12, (255, 255, 255), 0.22)
        cv.poly([(-0.86, 0.06), (-0.90, top - 0.10), (-0.78, top - 0.04), (-0.74, 0.12)], (255, 255, 255), 0.30)
        cv.line(blk, 0.036, ICE, closed=True)
        # Risse
        cv.line([(0.30, top - 0.02), (0.22, top - 0.24), (0.34, top - 0.42), (0.26, top - 0.60)], 0.020, ICE)
        cv.line([(-0.62, 0.40), (-0.52, 0.58), (-0.60, 0.74)], 0.018, ICE)
        # Glanz
        cv.line([(-0.66, top - 0.18), (-0.44, top - 0.40)], 0.032, (255, 255, 255), a=0.75)
        cv.line([(0.50, top - 0.12), (0.66, top - 0.28)], 0.026, (255, 255, 255), a=0.65)
        # Eiszapfen an der Oberkante
        for (x, l) in ((-0.55, 0.16), (-0.18, 0.11), (0.12, 0.19), (0.46, 0.13)):
            ic = [(x - 0.05, top + 0.005), (x + 0.05, top + 0.005), (x + 0.004, top - l)]
            cv.poly(ic, ICE_L, 0.95); cv.line(ic, 0.016, ICE, closed=True)
        # kleine Schneeflocken
        snowflake(cv, -1.02, CZ + 0.55, 0.11, (0xA6, 0xD3, 0xF5))
        snowflake(cv, 1.03, CZ + 0.20, 0.08, (0xA6, 0xD3, 0xF5))
        snowflake(cv, 0.98, top + 0.10, 0.06, (0xA6, 0xD3, 0xF5))
    elif extra == "heart":
        heart(cv, 0.98, CZ + 0.45, 0.16, broken=True)
    s = W / 2.8
    box = (int((-1.2 + 1.4) * s), int((2.8 - 2.82) * s), int((1.2 + 1.4) * s), int((2.8 - 2.82) * s + 2.76 * s))
    img = cv.img.crop(box)
    h = int(out_w * (box[3] - box[1]) / (box[2] - box[0]))
    return fade_edges(img.resize((out_w, h), Image.LANCZOS))


def fade_edges(img, m=10):
    """Lässt den Leuchtschein zum Bildrand hin weich auslaufen – sonst sieht man auf farbigen
    Widget-Hintergründen eine feine Kante, wo das Bild aufhört."""
    img = img.convert("RGBA")
    w, h = img.size
    mask = Image.new("L", (w, h), 0)
    px = mask.load()
    for y in range(h):
        for x in range(w):
            d = min(x, y, w - 1 - x, h - 1 - y)
            t = min(1.0, d / m)
            px[x, y] = int(255 * t * t * (3 - 2 * t))
    r, g, b, a = img.split()
    a = _mul(a, mask)
    return Image.merge("RGBA", (r, g, b, a))


def _mul(a, mask):
    from PIL import ImageChops
    return ImageChops.multiply(a, mask)

if __name__ == "__main__":
    names = sys.argv[1:] or list(S)
    outd = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'widget_out'); os.makedirs(outd, exist_ok=True)
    for n in names:
        for dark in (False, True):
            im = make(n, dark)
            im.save(f"{outd}/{'d' if dark else 'l'}_{n}.png", optimize=True)
        print(n, flush=True)
