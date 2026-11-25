"""Vorschau-GIF der Lumi-Animationen ab 8.1 (neue Körperform, vier Reaktionen beim Antippen)."""
import sys, os, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from preview9 import *
from PIL import ImageDraw, ImageFont
font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 15)
FPS, W = 20, 300
def bump(t, a, b): return 0.0 if (t <= a or t >= b) else math.sin(math.pi * (t - a) / (b - a))
def fig(rot=0, ty=0): return R.xform(rot=rot, ty=ty, oy=0.95)
LIFT = 0.14
def frameH(F, AA=None, book='hold', glow_s=1.0, wink=0.0, rays=0.0, rot=0, ty=0):
    # wie frame(), aber Arme/Hände/Buch mit Hub (wie in der App)
    import preview9 as V
    AA = AA or R.ARMS['hold']
    UH = R.xform(sx=B.K, sy=B.K, ox=0.5, oy=(2.8 - B.C0) / 2.8, ty=-(B.CZ - B.C0 + LIFT) / 2.8)
    f = fig(rot, ty)
    cv = R.Canvas(W)
    cv.T = [R.xform(sx=glow_s, sy=glow_s, oy=(2.8 - B.CZ) / 2.8)]; cv.shapes(B.glow()); cv.T = []
    cv.T = [UH, f]; V.rim_arms(cv, AA); R.draw_arm_lines(cv, AA)
    cv.T = [f]; cv.shapes(B.body())
    if book == 'hold': cv.T = [UH, f]; cv.shapes(P['book'])
    cv.T = []
    if book == 'down': cv.shapes(BP.lying_book(0.74, 0.13))
    cv.T = [UH, f]; V.rim_hands(cv, AA); R.draw_hands(cv, AA)
    cv.T = [UF, f]; R.draw_face(cv, F)
    if wink > 0:
        ex, ez = 0.235 + F["dx"], 1.62 + F["dz"]
        cv.ell(ex, ez, F["rx"] + 0.012, F["rz"] + 0.012, R.BULB, wink)
        cv.line(L.quad((ex - 0.08, ez + 0.005), (ex, ez - 0.045), (ex + 0.08, ez + 0.005), 14), 0.032, a=wink)
    cv.T = [f]
    if rays > 0:
        for i in range(8):
            a = math.radians(i * 45 + 22.5); r0 = B.R + 0.10; r1 = r0 + 0.16 * rays
            cv.line([(r0 * math.cos(a), B.CZ + r0 * math.sin(a)), (r1 * math.cos(a), B.CZ + r1 * math.sin(a))], 0.035, (0xFF, 0xD2, 0x5C), a=rays)
    cv.T = []
    return cv.img.convert('RGB')
read = R.FACES['read']
frames = []
segs = [("liest", 1.2), ("Antippen 1: Hüpfer", 1.0), ("Antippen 2: Kichern", 1.0), ("Antippen 3: Zwinkern", 1.1), ("Antippen 4: Aufleuchten", 1.2), ("Bist du dir sicher?", 2.0), ("abgebrochen", 2.6)]
t0 = 0
for name, dur in segs:
    n = int(dur * FPS)
    for i in range(n):
        u = i / FPS; T = t0 + u; ph = (T % 6) / 6 * 2 * math.pi
        breath = -0.0057 * (1 - math.cos(ph * 2)) / 2
        if name == "liest":
            img = frameH(read, rot=1.4 * math.sin(ph), ty=breath)
        elif "Hüpfer" in name:
            pk = min(1, u / 0.76)
            k = min(1, 2 * bump(pk, 0, 1)) if pk < 1 else 0
            img = frameH(R.lerp_face(read, R.FACES['happy'], R.ease(k)), ty=-0.06 * bump(pk, 0, 0.55) - 0.016 * bump(pk, 0.55, 0.82),
                         rot=5 * math.sin(2 * math.pi * pk * 1.5) * (1 - pk), glow_s=1 + 0.08 * bump(pk, 0, 0.6))
        elif "Kichern" in name:
            pk = min(1, u / 0.82); k = min(1, 2 * bump(pk, 0, 1))
            img = frameH(R.lerp_face(read, R.FACES['happy'], R.ease(k)), rot=7 * math.sin(2 * math.pi * pk * 3) * (1 - pk), ty=-0.012 * bump(pk, 0, 1))
        elif "Zwinkern" in name:
            pk = min(1, u / 0.9)
            img = frameH(read, rot=-6 * bump(pk, 0, 1), ty=-0.01 * bump(pk, 0, 1), wink=min(1, 2 * bump(pk, 0.08, 0.92)))
        elif "Aufleuchten" in name:
            pk = min(1, u / 0.95); k = min(1, 2.5 * bump(pk, 0, 1))
            img = frameH(R.lerp_face(read, R.FACES['wow'], R.ease(k)), glow_s=1 + 0.28 * bump(pk, 0, 1), rays=bump(pk, 0, 1), ty=-0.025 * bump(pk, 0, 0.5))
        elif "sicher" in name:
            F = dict(R.lerp_face(read, R.FACES['plead'], R.ease(u / 0.52)))
            F["mz"] += 0.004 * math.sin(2 * math.pi * T * 3)
            img = frameH(F, rot=(-3.2 + 1.2 * math.sin(ph * 2)) * R.ease(u / 0.5))
        else:
            # Buch fällt (vereinfacht): Hände öffnen sich, Buch liegt unten, Lumi weint
            k = R.ease(u / 0.4)
            AA = R.lerp_arm(R.ARMS['hold'], R.ARMS['hang'], R.ease(max(0, u - 0.3) / 0.8))
            F = R.lerp_face(R.FACES['scared'], R.FACES['cry'] if u > 1.2 else R.FACES['sad'], R.ease(min(1, u / 1.0)))
            img = frameH(F, AA, 'down' if u > 0.5 else 'none')
        d = ImageDraw.Draw(img); w = d.textlength(name, font=font)
        d.text(((W - w) / 2, 8), name, fill=(200, 200, 210), font=font)
        frames.append(img.convert("P", palette=Image.ADAPTIVE, colors=160))
    t0 += dur
out = sys.argv[1] if len(sys.argv) > 1 else "Lumi_Vorschau.gif"
frames[0].save(out, save_all=True, append_images=frames[1:], duration=int(1000 / FPS), loop=0, optimize=True)
print(len(frames))
