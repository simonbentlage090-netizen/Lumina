import os
import math
from PIL import Image, ImageDraw, ImageFilter

def hexc(h, a=1.0):
    h = h.lstrip('#'); return tuple(int(h[i:i+2], 16) for i in (0, 2, 4)) + (int(255 * max(0, min(1, a))),)
def lerp(a, b, t): return a + (b - a) * t
def lerpc(c1, c2, t): return tuple(int(lerp(c1[i], c2[i], t)) for i in range(4))
def ss(x): x = max(0, min(1, x)); return x * x * (3 - 2 * x)

class R:
    def __init__(self, S, SS=3, bg="#09090B"):
        self.S = S; self.SS = SS; self.W = S * SS
        self.img = Image.new("RGBA", (self.W, self.W), hexc(bg))
    def layer(self): return Image.new("RGBA", (self.W, self.W), (0, 0, 0, 0))
    def comp(self, l): self.img.alpha_composite(l)
    def out(self): return self.img.resize((self.S, self.S), Image.LANCZOS)

def pt(c, r, deg):
    a = math.radians(deg); return (c + r * math.cos(a), c + r * math.sin(a))

def arc_poly(d, c, r, a0, a1, w, col, n=None):
    n = n or max(8, int(abs(a1 - a0) / 2))
    pts = [pt(c, r, a0 + (a1 - a0) * i / n) for i in range(n + 1)]
    d.line(pts, fill=col, width=max(1, int(w)), joint="curve")
    for q in (pts[0], pts[-1]): d.ellipse([q[0] - w / 2, q[1] - w / 2, q[0] + w / 2, q[1] + w / 2], fill=col)

def ring(style, p, t, S=300, accent="#A594FF", alt="#6CC8FF", text="#F5F5F7", bg="#09090B", glassc=(255, 255, 255)):
    R0 = R(S, bg=bg); W = R0.W; c = W / 2
    sw = 12 / 310 * S * R0.SS
    r = c - sw / 2
    A, B = hexc(accent), hexc(alt)
    track = glassc + (int(255 * 0.09),)
    L = R0.layer(); d = ImageDraw.Draw(L)
    if style == "classic":
        d.ellipse([c - r, c - r, c + r, c + r], outline=track, width=int(sw))
        n = 120
        for i in range(n):
            f0, f1 = i / n * p, (i + 1) / n * p
            col = lerpc(B, A, 0.5 - 0.5 * math.cos(2 * math.pi * f0))
            arc_poly(d, c, r, -90 + 360 * f0, -90 + 360 * f1 + 0.5, sw, col, 2)
        h = pt(c, r, -90 + 360 * p)
        d.ellipse([h[0] - sw * .9, h[1] - sw * .9, h[0] + sw * .9, h[1] + sw * .9], fill=hexc(text, .25))
        d.ellipse([h[0] - sw * .28, h[1] - sw * .28, h[0] + sw * .28, h[1] + sw * .28], fill=hexc(text))
    elif style == "ticks":
        N = 60; rout = c - sw * 0.15
        for i in range(N):
            f = max(0, min(1, p * N - i))
            major = i % 5 == 0
            Lm = sw * (1.5 if major else 0.95)
            Ln = Lm * (0.55 + 0.45 * f)
            col = lerpc(track[:3] + (int(255 * (0.16 if major else 0.10)),), lerpc(A, B, i / N), f)
            a = -90 + i * 6
            p0, p1 = pt(c, rout, a), pt(c, rout - Ln, a)
            wv = sw * (0.30 if major else 0.22)
            d.line([p0, p1], fill=col, width=int(wv))
            for q in (p0, p1): d.ellipse([q[0] - wv / 2, q[1] - wv / 2, q[0] + wv / 2, q[1] + wv / 2], fill=col)
        # Kopf: hellster Strich
        i = min(N - 1, int(p * N)); a = -90 + 360 * p
        g = Image.new("RGBA", (W, W), A[:3] + (0,)); gd = ImageDraw.Draw(g); hp = pt(c, rout - sw * 0.7, a)
        gd.ellipse([hp[0] - sw * 1.4, hp[1] - sw * 1.4, hp[0] + sw * 1.4, hp[1] + sw * 1.4], fill=A[:3] + (90,))
        R0.comp(g.filter(ImageFilter.GaussianBlur(sw * 0.8)))
    elif style == "comet":
        d.ellipse([c - r, c - r, c + r, c + r], outline=track, width=int(sw * 0.25))
        arc_poly(d, c, r, -90, -90 + 360 * p, sw * 0.25, A[:3] + (150,))
        head = -90 + (t % 60) / 60 * 360
        n = 60
        for i in range(n):
            k = i / n
            a0 = head - 110 * (k + 1 / n); a1 = head - 110 * k
            w = sw * (0.95 * (1 - k) ** 1.4 + 0.08)
            col = lerpc(A, B, k)[:3] + (int(255 * (1 - k) ** 1.6),)
            arc_poly(d, c, r, a0, a1 + 0.6, w, col, 2)
        hp = pt(c, r, head)
        g = Image.new("RGBA", (W, W), A[:3] + (0,)); gd = ImageDraw.Draw(g)
        gd.ellipse([hp[0] - sw * 1.8, hp[1] - sw * 1.8, hp[0] + sw * 1.8, hp[1] + sw * 1.8], fill=A[:3] + (140,))
        R0.comp(g.filter(ImageFilter.GaussianBlur(sw * 0.9)))
        d.ellipse([hp[0] - sw * .48, hp[1] - sw * .48, hp[0] + sw * .48, hp[1] + sw * .48], fill=hexc(text))
    elif style == "beads":
        N = 40
        for i in range(N):
            f = max(0, min(1, p * N - i))
            a = -90 + 360 * (i + 0.5) / N
            q = pt(c, r, a)
            rad = lerp(sw * 0.16, sw * 0.40, f)
            col = lerpc(track[:3] + (40,), lerpc(A, B, i / N), f)
            if 0 < p * N - i < 1.0 or (i == int(p * N) - 1 and False):
                pass
            d.ellipse([q[0] - rad, q[1] - rad, q[0] + rad, q[1] + rad], fill=col)
        i = int(max(0, p * N - 1e-4)); q = pt(c, r, -90 + 360 * (i + 0.5) / N)
        pulse = 0.5 + 0.5 * math.sin(t * 2 * math.pi / 1.6)
        g = Image.new("RGBA", (W, W), A[:3] + (0,)); gd = ImageDraw.Draw(g); rr = sw * (0.9 + 0.5 * pulse)
        gd.ellipse([q[0] - rr, q[1] - rr, q[0] + rr, q[1] + rr], fill=A[:3] + (120,))
        R0.comp(g.filter(ImageFilter.GaussianBlur(sw * 0.6)))
        rr = sw * (0.40 + 0.10 * pulse)
        d.ellipse([q[0] - rr, q[1] - rr, q[0] + rr, q[1] + rr], fill=hexc(text))
    elif style == "neon":
        d.ellipse([c - r, c - r, c + r, c + r], outline=track[:3] + (12,), width=int(sw * 0.4))
        pulse = 0.85 + 0.15 * math.sin(t * 2 * math.pi / 4)
        g = Image.new("RGBA", (W, W), A[:3] + (0,)); gd = ImageDraw.Draw(g)
        for w, a in ((sw * 3.4, .10), (sw * 2.2, .16), (sw * 1.3, .30)):
            arc_poly(gd, c, r, -90, -90 + 360 * p, w, A[:3] + (int(255 * a * pulse),))
        R0.comp(g.filter(ImageFilter.GaussianBlur(sw * 0.5)))
        arc_poly(d, c, r, -90, -90 + 360 * p, sw * 0.62, A)
        arc_poly(d, c, r, -90, -90 + 360 * p, sw * 0.26, lerpc(A, (255, 255, 255, 255), 0.65))
    elif style == "rays":
        N = 72; rin = c - sw * 1.8
        d.ellipse([rin - 0 + c - c - rin + c - sw * 0.0 - 0, 0, 0, 0], fill=None) if False else None
        for i in range(N):
            f = max(0, min(1, p * N - i))
            a = -90 + 360 * i / N
            th = math.radians(a)
            wave = 0.5 + 0.5 * math.sin(th * 4 + t * 1.3)
            Lr = sw * (0.35 + f * (0.55 + 1.05 * wave))
            col = lerpc(track[:3] + (34,), lerpc(A, B, i / N), f)
            p0 = pt(c, rin, a); p1 = pt(c, rin + Lr, a)
            wv = sw * 0.24
            d.line([p0, p1], fill=col, width=int(wv))
            for q in (p0, p1): d.ellipse([q[0] - wv / 2, q[1] - wv / 2, q[0] + wv / 2, q[1] + wv / 2], fill=col)
        rr = rin - sw * 0.55
        d.ellipse([c - rr, c - rr, c + rr, c + rr], outline=track[:3] + (26,), width=int(sw * 0.18))
    elif style == "wave":
        d.ellipse([c - r, c - r, c + r, c + r], outline=track[:3] + (18,), width=int(sw * 0.25))
        n = 260; a0 = -90; a1 = -90 + 360 * p
        g = Image.new("RGBA", (W, W), A[:3] + (0,)); gd = ImageDraw.Draw(g)
        for k, (ph, al, ww) in enumerate(((0.0, 1.0, 0.30), (2.1, 0.65, 0.22), (4.2, 0.40, 0.18))):
            pts = []
            for i in range(n + 1):
                a = a0 + (a1 - a0) * i / n
                th = math.radians(a)
                taper = ss(min(i, n - i) / 18)
                amp = sw * 0.55 * taper
                pts.append(pt(c, r + amp * math.sin(th * 5 - t * 1.4 + ph), a))
            col = lerpc(A, B, k / 2)[:3] + (int(255 * al),)
            gd.line(pts, fill=col, width=int(sw * ww), joint="curve")
            if k == 0:
                gg = Image.new("RGBA", (W, W), A[:3] + (0,)); ggd = ImageDraw.Draw(gg); ggd.line(pts, fill=A[:3] + (90,), width=int(sw * 1.2), joint="curve")
                R0.comp(gg.filter(ImageFilter.GaussianBlur(sw * 0.5)))
        R0.comp(g)
    R0.comp(L)
    return R0.out()

if __name__ == "__main__":
    styles = ["classic", "ticks", "comet", "beads", "neon", "rays", "wave"]
    S = 240
    pals = [("#A594FF", "#6CC8FF", "#F5F5F7", "#09090B"), ("#39FF14", "#B6FF3B", "#F7F7FA", "#000000")]
    out = Image.new("RGB", (S * len(styles), S * 2))
    for row, (a, b, tx, bg) in enumerate(pals):
        for i, st in enumerate(styles):
            out.paste(ring(st, 0.68, 12.0, S, a, b, tx, bg).convert("RGB"), (i * S, row * S))
    out.save("rings.png")
