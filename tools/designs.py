"""Lumi-Entwürfe "noch süßer": mehrere Varianten nebeneinander (nur Vorschau, App bleibt unverändert).
Jede Variante = Körper-Parameter + Gesichts-Parameter. Gesicht in Vielfachen des Kugelradius R,
damit es bei jeder Kopfgröße gleich sitzt."""
import sys, os, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L, lumi_rig as R
from PIL import Image, ImageDraw, ImageFont, ImageFilter

F, Lz = L.F, L.Lz
P = L.parts()
WHITE = (255, 255, 255)

def hexc(h): return tuple(int(h.lstrip('#')[i:i + 2], 16) for i in (0, 2, 4))

# ------------------------------------------------------------------------------------------
# Körper
# ------------------------------------------------------------------------------------------
PHOTO_NECK = [(0.526, 0.865), (0.643, 0.801), (0.760, 0.749), (0.877, 0.705), (0.994, 0.670),
              (1.111, 0.643), (1.228, 0.617), (1.345, 0.605), (1.404, 0.597), (1.462, 0.582)]

BODY0 = dict(R=0.67, CZ=1.75, sqx=1.0, sqz=1.0, nsq=0.92, wf=1.0, bsw=1.0, bsh=1.0, threads=4, tip="pointy",
             fill="#FFC93C", shadow="#F2A324", light="#FFE27A", ink="#1E1A26", ow=0.032,
             metal="#CDD2DE", metal_d="#9AA1B3", metal_l="#EEF0F6", iso="#2A2533", thread="#6E7487", shadeA=1.0)

def catmull(pts, n=8):
    out = []; Pp = [pts[0]] + pts + [pts[-1]]
    for i in range(1, len(Pp) - 2):
        p0, p1, p2, p3 = Pp[i - 1], Pp[i], Pp[i + 1], Pp[i + 2]
        for k in range(n):
            t = k / n; t2, t3 = t * t, t * t * t
            out.append(tuple(0.5 * ((2 * p1[j]) + (-p0[j] + p2[j]) * t + (2 * p0[j] - 5 * p1[j] + 4 * p2[j] - p3[j]) * t2
                                     + (-p0[j] + 3 * p1[j] - 3 * p2[j] + p3[j]) * t3) for j in range(2)))
    out.append(pts[-1]); return out

class Body:
    def __init__(self, **kw):
        p = dict(BODY0); p.update(kw); self.p = p
        self.R, self.CZ = p["R"], p["CZ"]
        self.K = self.R / 0.74
        self.rx, self.rz = self.R * p["sqx"], self.R * p["sqz"]
        # Hals
        j = (self.rx * math.cos(math.radians(30)), self.CZ - self.rz * 0.5)
        pts = [j]
        for d, w in PHOTO_NECK:
            w2 = 0.866 - (0.866 - w) * p["wf"]
            pts.append((w2 * self.rx, self.CZ - (0.5 * p["sqz"] + (d - 0.5) * p["nsq"]) * self.R))
        last = pts[-1]
        self.NECK_Z = last[1] - 0.077 * p["bsh"]
        self.collar = max(0.37 * p["bsw"], last[0] - 0.005)
        pts += [(last[0] - 0.012, last[1] - 0.035 * p["bsh"]), (self.collar - 0.015, self.NECK_Z + 0.012)]
        self.neck = catmull(pts, 8)

    def outline(self):
        arc = L.earc(0, self.CZ, self.rx, self.rz, -30, 210, 90)
        return self.neck[::-1] + arc[1:-1] + [(-x, z) for x, z in self.neck]

    def glow(self):
        return [F(L.ellipse(0, self.CZ, r * self.K * self.p["sqx"], r * self.K, 96), "#FFD25C", 0.055) for r in (1.16, 1.04, 0.94)]

    def glass(self):
        p, K, CZ, rx, rz = self.p, self.K, self.CZ, self.rx, self.rz
        out = self.outline()
        s = [F(out, p["fill"])]
        s.append(F(L.crescent(L.earc(0, CZ, rx, rz, -34, 72, 36), 0.21 * K, (-0.15 * K, CZ + 0.12 * K)), p["shadow"], p["shadeA"]))
        s.append(F(L.crescent(L.earc(0, CZ, rx, rz, 112, 200, 30), 0.13 * K, (0.15 * K, CZ - 0.08 * K)), p["light"]))
        s.append(Lz(L.earc(0, CZ, rx - 0.14 * K, rz - 0.14 * K, 148, 194, 16), 0.048 * K, "#FFFFFF"))
        s.append(F(L.ellipse(-(rx / self.R) * 0.29 * K, CZ + (rz / self.R) * 0.50 * K, 0.035 * K, 0.035 * K, 20), "#FFFFFF"))
        nr = self.neck
        top, bot = nr[0][1], nr[-1][1]
        inner = [(x - 0.07 - 0.03 * (1 - (z - bot) / (top - bot)), z) for x, z in nr]
        s.append(F(nr + inner[::-1], p["shadow"], 0.55 * p["shadeA"]))
        s.append(Lz(out, p["ow"], p["ink"], closed=True))
        return s

    def base(self):
        p = self.p; s = []; ink = p["ink"]; ow = p["ow"] * 0.875
        bw, bh, NZ = p["bsw"], p["bsh"], self.NECK_Z
        def T(x, z): return (x * bw, NZ + (z - 0.745) * bh)
        def TT(pts): return [T(x, z) for x, z in pts]
        n_th = p["threads"]
        z0, z1 = 0.735, 0.38
        def half_w(z):
            t = (z0 - z) / (z0 - z1)
            return 0.325 - 0.008 * t + 0.02 * abs(math.sin(math.pi * t * n_th))
        right = [(half_w(z0 - (z0 - z1) * i / 80), z0 - (z0 - z1) * i / 80) for i in range(81)]
        bot = L.quad((right[-1][0], z1), (right[-1][0] * 0.98, z1 - 0.045), (0.215, z1 - 0.055), 8)[1:]
        rf = right + bot
        shell = rf + [(-x, z) for x, z in reversed(rf)][1:]
        zi = z1 - 0.045
        iso = (L.quad((0.215, zi), (0.20, zi - 0.075), (0.095, zi - 0.11), 10)
               + L.quad((-0.095, zi - 0.11), (-0.20, zi - 0.075), (-0.215, zi), 10))
        zt = zi - 0.10
        if p["tip"] == "pointy":
            tip = L.quad((0.09, zt), (0.075, 0.145), (0.0, 0.13), 12) + L.quad((0.0, 0.13), (-0.075, 0.145), (-0.09, zt), 12)[1:]
        else:  # rund und kurz
            tip = L.earc(0, zt + 0.01, 0.10, 0.075, 0, -180, 20)
        s += [F(TT(tip), p["metal_d"]), Lz(TT(tip), ow * 0.93, ink, closed=True)]
        s += [F(TT(iso), p["iso"]), Lz(TT(iso), ow * 0.93, ink, closed=True)]
        s.append(F(TT(shell), p["metal"]))
        s.append(F(TT(right + [(x - 0.10, z) for x, z in reversed(right)]), p["metal_d"], 0.8))
        s.append(F(TT([(-0.24, z0 - 0.02), (-0.16, z0 - 0.02), (-0.14, z1 + 0.01), (-0.20, z1 + 0.01)]), p["metal_l"], 0.9))
        for i in range(1, n_th):
            zl = z0 - (z0 - z1) * i / n_th; wl = half_w(zl) - 0.015
            s.append(Lz(TT(L.quad((-wl, zl - 0.016), (0.0, zl - 0.05), (wl, zl + 0.016), 16)), 0.02, p["thread"]))
        s.append(Lz(TT(shell), ow, ink, closed=True))
        cw = self.collar * 2 / bw
        col = L.rrect(0, 0.75, cw, 0.065, 0.03)
        s += [F(TT(col), p["metal_l"]), F(TT(L.rrect(cw * 0.26, 0.75, cw * 0.38, 0.065, 0.028)), p["metal"]),
              F(TT(L.rrect(-cw * 0.18, 0.761, cw * 0.40, 0.016, 0.008)), "#FFFFFF"), Lz(TT(col), ow, ink, closed=True)]
        return s

    def bottom(self):
        return self.NECK_Z + (0.13 - 0.745) * self.p["bsh"]

# ------------------------------------------------------------------------------------------
# Gesicht (alles in Vielfachen von R, relativ zur Kugelmitte)
# ------------------------------------------------------------------------------------------
FACE0 = dict(ex=0.317, ez=0.027, erx=0.100, erz=0.119, eye="#1E1A26", low=None, lowA=0.0,
             hl=[(0.30, 0.36, 0.30), (-0.32, -0.42, 0.14)], sparkle=False,
             mouth="smile", mz=-0.196, mw=0.142, mdip=0.054, mlw=0.046,
             cx=0.54, cz=-0.155, crx=0.097, crz=0.059, cheek="#FF8FA3", chA=0.92, blushLines=False,
             readLid=0.12, readDz=-0.035)

def star(cx, cz, r, k=0.28):
    pts = []
    for i in range(8):
        a = math.radians(90 + i * 45); rr = r if i % 2 == 0 else r * k
        pts.append((cx + rr * math.cos(a), cz + rr * math.sin(a)))
    return pts

def draw_face(cv, b, f, mode="front", bulb_rgb=None):
    Rr, CZ, ink = b.R, b.CZ, hexc(b.p["ink"])
    sx_ = b.p["sqx"]
    def X(u): return u * Rr * sx_
    def Z(v): return CZ + v * Rr
    bulb = bulb_rgb or hexc(b.p["fill"])
    # Wangen
    for sx in (-1, 1):
        cv.ell(sx * X(f["cx"]), Z(f["cz"]), f["crx"] * Rr, f["crz"] * Rr, hexc(f["cheek"]), f["chA"])
        if f["blushLines"]:
            for k in (-1, 0, 1):
                x0 = sx * X(f["cx"]) + k * 0.36 * f["crx"] * Rr
                z0 = Z(f["cz"])
                cv.line([(x0 + 0.020 * Rr, z0 + 0.030 * Rr), (x0 - 0.020 * Rr, z0 - 0.030 * Rr)], 0.012 * Rr / 0.67 * 1.4,
                        (0xE8, 0x5D, 0x7A), a=0.85)
    # Mund
    mz, mw, md = Z(f["mz"]), f["mw"] * Rr, f["mdip"] * Rr
    lw = f["mlw"] * Rr
    m = f["mouth"]
    if m == "smile":
        cv.line(L.quad((-mw, mz), (0, mz - 2 * md), (mw, mz), 18), lw, ink)
    elif m == "cat":  # ω
        cv.line(L.quad((-mw, mz + 0.2 * md), (-mw / 2, mz - 1.9 * md), (0, mz + 0.1 * md), 12)
                + L.quad((0, mz + 0.1 * md), (mw / 2, mz - 1.9 * md), (mw, mz + 0.2 * md), 12)[1:], lw, ink)
    elif m == "open":  # kleines offenes D mit Zunge
        top = L.quad((-mw, mz), (0, mz + 0.25 * md), (mw, mz), 12)
        botm = L.quad((mw, mz), (0, mz - 3.4 * md), (-mw, mz), 16)[1:-1]
        shape = top + botm
        cv.poly(shape, R.MOUTH)
        cv.ell(0, mz - 1.25 * md, mw * 0.62, md * 0.75, R.TONGUE, mask=cv.mask_of(shape))
        cv.line(shape, lw * 0.85, ink, closed=True)
    # Augen
    look = f["readDz"] * Rr if mode == "read" else 0.0
    for sx in (-1, 1):
        ex, ez = sx * X(f["ex"]), Z(f["ez"]) + look
        rx, rz = f["erx"] * Rr, f["erz"] * Rr
        eye = L.ellipse(ex, ez, rx, rz, 48)
        em = cv.mask_of(eye)
        cv.poly(eye, hexc(f["eye"]))
        if f["low"]:
            cv.ell(ex, ez - rz * 0.62, rx * 0.78, rz * 0.48, hexc(f["low"]), f["lowA"], mask=em)
        hl = f["hl"]
        if mode == "read":  # Blick nach unten: Glanzpunkte rutschen mit
            hl = [(hx, hz - 0.30, hr) for hx, hz, hr in hl]
        for hx, hz, hr in hl:
            cv.ell(ex + hx * rx, ez + hz * rz, hr * rx, hr * rx, WHITE)
        if f["sparkle"] and mode != "read":
            hx, hz, hr = hl[0]
            cv.poly(star(ex + (hx - 0.50) * rx, ez + (hz + 0.38) * rz, 0.20 * rx), WHITE)
        if mode == "read":
            lid = f["readLid"]
            yo = ez + rz * (1 - 2 * lid)
            edge = L.quad((ex - rx * 1.2, yo + 0.10 * rz), (ex, yo + 0.30 * rz), (ex + rx * 1.2, yo + 0.10 * rz), 12)
            cv.poly(edge + [(ex + rx * 1.2, ez + rz * 2), (ex - rx * 1.2, ez + rz * 2)], bulb, mask=em)

# ------------------------------------------------------------------------------------------
# Figur
# ------------------------------------------------------------------------------------------
RIM = (0x6B, 0x62, 0x80)

def front_arms(cv, b, dark, wave=True):
    Rr, CZ = b.R, b.CZ
    sq = b.p["sqx"]
    ink = hexc(b.p["ink"]); w = 0.072 * b.K
    b.hand = []
    for sx in (-1, 1):
        sh = (sx * 0.60 * Rr * sq, CZ - 0.80 * Rr)
        if wave and sx > 0:   # winkt
            c1 = (sx * 1.02 * Rr * sq, CZ - 0.86 * Rr); c2 = (sx * 1.16 * Rr * sq, CZ - 0.62 * Rr); e = (sx * 1.10 * Rr * sq, CZ - 0.36 * Rr)
        else:                 # hängt locker
            c1 = (sx * 0.86 * Rr * sq, CZ - 0.84 * Rr); c2 = (sx * 0.98 * Rr * sq, CZ - 0.96 * Rr); e = (sx * 0.98 * Rr * sq, CZ - 1.13 * Rr)
        pts = L.cubic(sh, c1, c2, e, 28)
        if dark: cv.line(pts, w + 0.044 * b.K, RIM)
        cv.line(pts, w, ink)
        b.hand.append(e)

def front_hands(cv, b, dark):
    ink = hexc(b.p["ink"])
    for (x, z) in b.hand:
        r = 0.082 * b.K
        if dark: cv.ell(x, z, r + 0.022 * b.K, r * 1.08 + 0.022 * b.K, RIM)
        cv.ell(x, z, r, r * 1.08, ink)
    b.hand = []

FLOOR = 0.12
def render(v, mode="front", W=1050, bg=(0xF4, 0xF3, 0xF7), lift=None):
    """mode: front (schaut dich an, Arme seitlich), read (hält das Buch)."""
    b = Body(**v["body"]); f = dict(FACE0); f.update(v["face"])
    dark = bg[0] < 100
    cv = R.Canvas(W, bg)
    if lift is None: lift = FLOOR + 0.09 - b.bottom()   # alle schweben gleich hoch über dem Boden
    tilt = v.get("tilt", 0.0) if mode == "front" else 0.0
    fig = R.xform(rot=tilt, ty=-lift / 2.8, oy=(2.8 - b.CZ) / 2.8)
    # Bodenschatten
    gz = FLOOR
    for k, a in ((1.0, 0.10), (0.75, 0.12), (0.5, 0.14)):
        cv.ell(0, gz, 0.30 * k, 0.05 * k, (0, 0, 0), a * 1.6 if not dark else a * 2.5)
    cv.T = [fig]
    cv.shapes(b.glow())
    if mode == "front":
        front_arms(cv, b, dark, wave=v.get("wave", True))
        cv.shapes(b.glass() + b.base())
        front_hands(cv, b, dark)
        draw_face(cv, b, f, "front")
        if v.get("spark"):
            sxp, szp = 0.78 * b.R * b.p["sqx"], b.CZ + 0.80 * b.R
            cv.poly(star(sxp, szp, 0.085 * b.K), (0xFF, 0xD2, 0x5C)); cv.line(star(sxp, szp, 0.085 * b.K), 0.018, hexc(b.p["ink"]), closed=True)
            cv.poly(star(sxp + 0.13 * b.K, szp - 0.10 * b.K, 0.045 * b.K), (0xFF, 0xD2, 0x5C))
    else:
        hl = v.get("holdLift", 0.18 / 0.67) * b.R
        UH = R.xform(sx=b.K, sy=b.K, ox=0.5, oy=(2.8 - 1.60) / 2.8, ty=-(b.CZ - 1.60 + hl) / 2.8)
        AA = R.ARMS["hold"]
        cv.T = [UH, fig]
        if dark:
            for sx in (-1, 1):
                a = AA["L" if sx < 0 else "R"]
                cv.line(L.cubic((sx * 0.40, 1.02), (sx * a["c1x"], a["c1z"]), (sx * a["c2x"], a["c2z"]), (sx * a["ex"], a["ez"]), 28), 0.124, RIM)
        R.draw_arm_lines(cv, AA)
        cv.T = [fig]; cv.shapes(b.glass() + b.base())
        cv.T = [UH, fig]; cv.shapes(P["book"])
        if dark:
            for sx in (-1, 1):
                a = AA["L" if sx < 0 else "R"]
                cv.ell(sx * a["hx"], a["hz"], a["hrx"] + 0.022, a["hrz"] + 0.022, RIM)
                cv.ell(sx * a["fx"], a["fz"], a["frx"] + 0.022, a["frz"] + 0.022, RIM, a["fA"])
        R.draw_hands(cv, AA)
        cv.T = [fig]
        draw_face(cv, b, f, "read")
    cv.T = []
    return cv.img.convert("RGB"), b

# ------------------------------------------------------------------------------------------
# Varianten
# ------------------------------------------------------------------------------------------
KULLER = dict(ex=0.335, ez=-0.035, erx=0.138, erz=0.158, low="#6A5AA8", lowA=0.55,
              hl=[(0.26, 0.36, 0.36), (-0.34, -0.40, 0.15)],
              mouth="smile", mz=-0.250, mw=0.075, mdip=0.040, mlw=0.042,
              cx=0.585, cz=-0.205, crx=0.135, crz=0.078, chA=0.82, readLid=0.11, readDz=-0.03)

VARIANTS = [
    dict(key="A", name="Jetzt (8.3)", desc="zum Vergleich",
         body=dict(), face=dict()),
    dict(key="B", name="Kulleraugen", desc="nur das Gesicht: größere, tiefere Augen, kleiner Mund",
         body=dict(), face=KULLER),
    dict(key="C", name="Großer Kopf", desc="nur der Körper: größere Kugel, kürzerer Hals",
         body=dict(R=0.76, CZ=1.70, nsq=0.62, bsw=0.95, bsh=0.85), face=dict()),
    dict(key="D", name="Pummelig", desc="runder & breiter, kurzes Gewinde, Katzenmund",
         body=dict(R=0.72, CZ=1.66, sqx=1.07, sqz=0.96, nsq=0.55, wf=0.55, bsw=1.0, bsh=0.78, threads=3, tip="round"),
         face=dict(ex=0.30, ez=-0.07, erx=0.098, erz=0.108, low="#6A5AA8", lowA=0.4,
                   hl=[(0.24, 0.32, 0.40), (-0.36, -0.38, 0.17)], mouth="cat", mz=-0.235, mw=0.085, mdip=0.034, mlw=0.040,
                   cx=0.56, cz=-0.19, crx=0.15, crz=0.085, chA=0.60, readLid=0.12)),
    dict(key="E", name="Kopf + Kulleraugen", desc="B und C zusammen, leicht schief, kleiner Funke",
         body=dict(R=0.76, CZ=1.70, nsq=0.62, bsw=0.95, bsh=0.85), face=dict(KULLER, sparkle=True, mouth="open", mw=0.080, mdip=0.046, mz=-0.235),
         tilt=-6, spark=True),
    dict(key="F", name="Pastell & weich", desc="Gesicht wie B, weichere Farben, braune Linien, Rotschimmer",
         body=dict(fill="#FFD45E", shadow="#F6B75A", light="#FFEDA8", ink="#3E2B3A", ow=0.026,
                   metal="#D8DBE5", metal_d="#AEB2C2", iso="#3E2B3A", thread="#8A8DA0", shadeA=0.75),
         face=dict(KULLER, eye="#3A2430", low="#B86A8A", lowA=0.55, mlw=0.036, cheek="#FF9AAE", chA=0.65, blushLines=True)),
    dict(key="G", name="Baby-Lumi", desc="ganz extrem, nur damit du die Grenze siehst",
         body=dict(R=0.80, CZ=1.62, sqx=1.02, sqz=0.98, nsq=0.52, wf=0.75, bsw=0.85, bsh=0.66, threads=3, tip="round"),
         face=dict(ex=0.35, ez=-0.09, erx=0.165, erz=0.19, low="#6A5AA8", lowA=0.6,
                   hl=[(0.24, 0.36, 0.38), (-0.36, -0.42, 0.16), (0.40, -0.05, 0.10)], sparkle=True,
                   mouth="open", mz=-0.29, mw=0.075, mdip=0.042, mlw=0.040,
                   cx=0.62, cz=-0.25, crx=0.15, crz=0.085, chA=0.70, readLid=0.11), holdLift=0.12),
]

# ------------------------------------------------------------------------------------------
# Blatt
# ------------------------------------------------------------------------------------------
def crop_units(img, W, x0, x1, z0, z1):
    s = W / 2.8
    return img.crop((int((x0 + 1.4) * s), int((2.8 - z1) * s), int((x0 + 1.4) * s + (x1 - x0) * s), int((2.8 - z1) * s + (z1 - z0) * s)))

def sheet(variants, out, CW=300):
    SS = 3
    pu = CW / 2.3                  # Pixel pro Einheit in Zeile 1 und 3
    W = int(2.8 * pu * SS)
    fb = "/usr/share/fonts/opentype/inter/Inter-Bold.otf"; fr = "/usr/share/fonts/opentype/inter/Inter-Regular.otf"
    k_ = CW / 300; fT = ImageFont.truetype(fb, int(22 * k_)); fD = ImageFont.truetype(fr, int(13.5 * k_)); fR = ImageFont.truetype(fb, int(15 * k_))
    H1 = int(2.62 * pu); H2 = CW; H3 = int(2.40 * pu)
    head = int(84 * k_); lab = int(28 * k_); gap = 8
    total_h = head + lab + H1 + gap + lab + H2 + gap + lab + H3 + 12
    LW = 0
    sheet_img = Image.new("RGB", (LW + CW * len(variants), total_h), (255, 255, 255))
    d = ImageDraw.Draw(sheet_img)
    for i, v in enumerate(variants):
        x = LW + i * CW
        # Kopf
        d.text((x + 14, 12), f"{v['key']}  {v['name']}", font=fT, fill=(0x1E, 0x1A, 0x26))
        words = v["desc"].split(); lines = [""]
        for w_ in words:
            t = (lines[-1] + " " + w_).strip()
            if d.textlength(t, font=fD) > CW - 28: lines.append(w_)
            else: lines[-1] = t
        for k, ln in enumerate(lines[:2]):
            d.text((x + 14, int((44 + k * 17) * k_)), ln, font=fD, fill=(0x6B, 0x66, 0x78))
        y = head
        # Zeile 1: schaut dich an
        img, b = render(v, "front", W=W)
        top = 2.66; c = crop_units(img, W, -1.15, 1.15, top - 2.62, top).resize((CW, H1), Image.LANCZOS)
        sheet_img.paste(c, (x, y + lab)); y += lab + H1 + gap
        # Zeile 2: Gesicht groß
        img2, b2 = render(v, "front", W=W * 2)
        lift2 = FLOOR + 0.09 - b2.bottom()
        r = b2.R * 1.08 * max(1.0, b2.p["sqx"])
        zc = b2.CZ + lift2 + 0.02 * b2.R
        c2 = crop_units(img2, W * 2, -r, r, zc - r, zc + r).resize((CW, H2), Image.LANCZOS)
        sheet_img.paste(c2, (x, y + lab)); y += lab + H2 + gap
        # Zeile 3: liest (dunkel)
        img3, _ = render(v, "read", W=W, bg=(0x0B, 0x0B, 0x0E))
        c3 = crop_units(img3, W, -1.15, 1.15, 0.12, 0.12 + 2.40).resize((CW, H3), Image.LANCZOS)
        sheet_img.paste(c3, (x, y + lab))
        if i > 0: d.line([(x, 8), (x, total_h - 8)], fill=(0xE6, 0xE4, 0xEC), width=1)
    # Zeilen-Beschriftung
    y = head
    for t in ("schaut dich an", "Gesicht groß", "beim Lesen (dunkel)"):
        for i in range(len(variants)):
            d.text((LW + i * CW + 14, y + 6), t, font=fR, fill=(0x9A, 0x96, 0xA8))
        y += lab + (H1 if t.startswith("schaut") else H2 if t.startswith("Gesicht") else H3) + gap
    sheet_img.save(out)
    return sheet_img

if __name__ == "__main__":
    keys = sys.argv[2:] if len(sys.argv) > 2 else None
    vs = [v for v in VARIANTS if not keys or v["key"] in keys]
    sheet(vs, sys.argv[1] if len(sys.argv) > 1 else "/tmp/Lumi_Entwuerfe.png")
