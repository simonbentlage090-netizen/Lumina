# Grober Nachbau der Lumi-Widget-Layouts (9.3, autoSize nachgerechnet) – nur zur Kontrolle, nicht exakt.
import os, sys
from PIL import Image, ImageDraw, ImageFont
S = 3
FB = "/usr/share/fonts/opentype/inter/Inter-Bold.otf"
FR = "/usr/share/fonts/opentype/inter/Inter-Regular.otf"
ART = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(os.path.abspath(__file__)), "widget_out")
OUT = sys.argv[2] if len(sys.argv) > 2 else os.path.join(os.path.dirname(os.path.abspath(__file__)), "lumi-widget-vorschau.png")
MOODS = {"warm": ((0xFF,0xEB,0xC2),(0xFF,0xFC,0xF5),(0x3A,0x2C,0x12),(0x17,0x16,0x1C)),
         "cool": ((0xDD,0xEE,0xFD),(0xF7,0xFB,0xFF),(0x15,0x28,0x3E),(0x15,0x17,0x1D)),
         "alert":((0xFF,0xE1,0xD6),(0xFF,0xFA,0xF6),(0x3B,0x1F,0x1C),(0x17,0x16,0x1C)),
         "night":((0xE9,0xE5,0xFB),(0xFB,0xFA,0xFF),(0x22,0x1D,0x3D),(0x13,0x12,0x18))}
TXT = {False: (0x1E,0x1C,0x17), True: (0xF5,0xF5,0xF7)}
PILL = {"warm": {False: (0xF2,0xA3,0x24), True: (0xFF,0xC9,0x3C)}, "cool": {False: (0x5F,0xA8,0xE6), True: (0x7C,0xC0,0xF5)},
        "alert": {False: (0xD9,0x48,0x3B), True: (0xFF,0x7A,0x6B)}}
PILL["night"] = PILL["warm"]
ONPILL = {"warm": {False: (0x1E,0x1C,0x17), True: (0x1E,0x1C,0x17)}, "cool": {False: (255,255,255), True: (0x0E,0x1B,0x28)},
          "alert": {False: (255,255,255), True: (0x2A,0x0E,0x0B)}}
ONPILL["night"] = ONPILL["warm"]
BIG = {"warm": {False: (0xC9,0x7A,0x00), True: (0xFF,0xC9,0x3C)}, "cool": {False: (0x2F,0x7F,0xC4), True: (0x8F,0xCB,0xF7)},
       "alert": {False: (0xD9,0x48,0x3B), True: (0xFF,0x7A,0x6B)}}
BIG["night"] = BIG["warm"]

def grad(w, h, a, b):
    im = Image.new("RGB", (w, h)); px = im.load()
    for y in range(h):
        for x in range(w):
            t = (x / w + y / h) / 2
            px[x, y] = tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))
    return im

def wrap(text, font, width):
    words, lines, cur = text.split(" "), [], ""
    for w_ in words:
        t = (cur + " " + w_).strip()
        if font.getlength(t) <= width or not cur: cur = t
        else: lines.append(cur); cur = w_
    if cur: lines.append(cur)
    return lines

def autosize(text, path, width, height, mx, mn, maxlines, lh=1.17, unit=None):
    for sz in range(mx, mn - 1, -1):
        f = ImageFont.truetype(path, sz * S)
        if unit is not None:
            fu = ImageFont.truetype(path, max(1, int(sz * 0.42)) * S)
            if f.getlength(text) + fu.getlength(" " + unit) <= width and sz * S * lh <= height: return f, [text], sz
            continue
        ls = wrap(text, f, width)
        if len(ls) <= maxlines and len(ls) * sz * S * lh <= height: return f, ls, sz
    f = ImageFont.truetype(path, mn * S)
    return f, wrap(text, f, width)[:maxlines], mn

def card(kind, wdp, hdp, img, title, big, unit, live, cta, mood, dark):
    W, H = wdp * S, hdp * S
    a, b = (MOODS[mood][2], MOODS[mood][3]) if dark else (MOODS[mood][0], MOODS[mood][1])
    mask = Image.new("L", (W, H), 0); ImageDraw.Draw(mask).rounded_rectangle((0, 0, W - 1, H - 1), 26 * S, fill=255)
    out = Image.new("RGBA", (W, H), (0, 0, 0, 0)); out.paste(grad(W, H, a, b), (0, 0), mask)
    d = ImageDraw.Draw(out); tc = TXT[dark]
    lumi = Image.open(f"{ART}/{'d' if dark else 'l'}_{img}.png").convert("RGBA")
    if live: big, unit = "42:17", None
    def place(box):
        x0, y0, x1, y1 = [int(v) for v in box]
        sc_ = min((x1 - x0) / lumi.width, (y1 - y0) / lumi.height)
        im = lumi.resize((int(lumi.width * sc_), int(lumi.height * sc_)), Image.LANCZOS)
        out.alpha_composite(im, (x0 + (x1 - x0 - im.width) // 2, y0 + (y1 - y0 - im.height) // 2))
    def bigdraw(x, y, w, h, mx, mn, center):
        f, ls, sz = autosize(big, FB, w, h, mx, mn, 1, 1.0, unit=unit if unit else "")
        fu = ImageFont.truetype(FB, max(1, int(sz * 0.42)) * S)
        tw = f.getlength(big) + (fu.getlength(" " + unit) if unit else 0)
        xx = x + ((w - tw) / 2 if center else 0)
        yy = y + h - sz * S * 1.0 if not center else y + (h - sz * S) / 2
        col = BIG[mood][dark]
        d.text((xx, yy), big, font=f, fill=col)
        if unit: d.text((xx + f.getlength(big) + fu.getlength(" "), yy + sz * S * 0.52), unit, font=fu, fill=col)
    def lines(ls, f, x, y, sz, lh, cw=None):
        for i, l in enumerate(ls):
            xx = x if cw is None else x + (cw - f.getlength(l)) / 2
            d.text((xx, y + i * sz * S * lh), l, font=f, fill=tc)
    def pill(x, y, cw=None, fs=14, ph=14, pv=6):
        f = ImageFont.truetype(FB, fs * S); tw = f.getlength(cta); th = fs * 1.17 * S
        pw, phh = tw + 2 * ph * S, th + 2 * pv * S
        if cw is not None: x = x + (cw - pw) / 2
        d.rounded_rectangle((x, y, x + pw, y + phh), phh / 2, fill=PILL[mood][dark])
        d.text((x + ph * S, y + pv * S + S), cta, font=f, fill=ONPILL[mood][dark])
    if kind == "wide":
        pl, pt, pr, pb = 4, 10, 16, 12
        iw = (wdp - pl - pr) * 0.40
        place((pl * S, pt * S, (pl + iw) * S, (hdp - pb) * S))
        cx = (pl + iw + 6) * S; cw = (wdp - pr) * S - cx
        ctah = (14 * 1.17 + 12 + 8) * S if cta else 0
        avail = (hdp - pt - pb) * S - ctah
        if big:
            bh, th_ = avail * 46 / 100, avail * 54 / 100
            bigdraw(cx, pt * S, cw, bh, 72, 28, False)
            f, ls, sz = autosize(title, FB, cw, th_, 32, 16, 2, 1.17 * .95)
            lines(ls, f, cx, pt * S + bh + 2 * S, sz, 1.17 * .95)
            y = pt * S + avail
        else:
            f, ls, sz = autosize(title, FB, cw, avail, 32, 16, 2, 1.17 * .95)
            lines(ls, f, cx, pt * S + (avail - len(ls) * sz * S * 1.11) / 2, sz, 1.17 * .95)
            y = pt * S + avail
        if cta: pill(cx, y + 8 * S)
    elif kind == "tall":
        pl, pt, pr, pb = 16, 12, 16, 16
        cw = (wdp - pl - pr) * S
        ctah = (16 * 1.17 + 16 + 10) * S if cta else 0
        avail = (hdp - pt - pb) * S - ctah - 4 * S
        tot = 52 + (22 if big else 0) + 20
        ih, bh, th_ = avail * 52 / tot, (avail * 22 / tot if big else 0), avail * 20 / tot
        place((pl * S, pt * S, (wdp - pr) * S, pt * S + ih)); y = pt * S + ih + 2 * S
        if big: bigdraw(pl * S, y, cw, bh, 80, 28, True); y += bh + 2 * S
        f, ls, sz = autosize(title, FB, cw, th_, 36, 18, 2, 1.17 * .95)
        lines(ls, f, pl * S, y + (th_ - len(ls) * sz * S * 1.11) / 2, sz, 1.17 * .95, cw)
        y += th_
        if cta: pill(pl * S, y + 10 * S, cw, fs=16, ph=20, pv=8) if wdp >= 230 else pill(pl * S, y + 10 * S, cw, fs=13, ph=12, pv=6)
    else:
        pl, pt, pr, pb = 8, 8, 8, 10
        cw = (wdp - pl - pr) * S; avail = (hdp - pt - pb - 2) * S
        ih, th_ = avail * .70, avail * .30
        place((pl * S, pt * S, (wdp - pr) * S, pt * S + ih))
        if big: bigdraw(pl * S, pt * S + ih + 2 * S, cw, th_, 40, 14, True)
        else:
            f, ls, sz = autosize(title, FB, cw, th_, 20, 12, 2, 1.17)
            lines(ls, f, pl * S, pt * S + ih + 2 * S + (th_ - len(ls) * sz * S * 1.17) / 2, sz, 1.17, cw)
    return out

def sheet(rows, path):
    pad = 14 * S
    cells = [[card(*c) for c in r] for r in rows]
    W = max(sum(c.width for c in line) + pad * (len(line) + 1) for line in cells)
    H = sum(max(c.height for c in line) + pad for line in cells) + pad
    img = Image.new("RGB", (W, H), (0xE4, 0xE2, 0xDC)); y = pad
    for line in cells:
        x = pad
        for im in line: img.paste(im, (x, y), im); x += im.width + pad
        y += max(c.height for c in line) + pad
    img = img.resize((img.width * 2 // 3, img.height * 2 // 3), Image.LANCZOS)
    img.save(path, optimize=True); print(path, img.size)

sheet([
  [("wide", 350, 175, "scared", "bis dein Streak erlischt", "3", "Std.", False, "Streak retten →", "alert", False),
   ("wide", 350, 175, "frozen", "Hilf mir!", None, None, False, "Lumi retten →", "cool", False),
   ("small", 170, 170, "scared", "", "3", "Std.", False, None, "alert", False)],
  [("tall", 350, 280, "cry", "Nein, nicht!", None, None, True, "Streak retten →", "alert", False),
   ("tall", 350, 280, "zap", "Du hast einen Lauf!", "9", "Tage", False, "Noch ein Kapitel →", "warm", True),
   ("tall", 170, 280, "frozen", "Hol mich hier raus!", None, None, False, "Lumi auftauen →", "cool", False)],
  [("wide", 350, 175, "icebreak", "Aufgetaut!", None, None, False, "Noch ein Kapitel →", "cool", True),
   ("wide", 350, 175, "read", "Zeit für ein Kapitel?", None, None, False, "Jetzt lesen →", "warm", False),
   ("small", 170, 170, "frozen", "Hilf mir!", None, None, False, None, "cool", True)],
], OUT)
