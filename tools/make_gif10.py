"""Vorschau-GIF für 8.2: neue Form, Schweben, neue Reaktionen, Flackern und Ausgehen beim Abbrechen."""
import sys, os, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import body10
sys.modules['body9'] = body10
from preview9 import *
import preview9 as V
from PIL import ImageDraw, ImageFont
font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 14)
FPS, W = 20, 300
B = body10
def bump(t, a, b): return 0.0 if (t <= a or t >= b) else math.sin(math.pi * (t - a) / (b - a))
def smooth(a, b, x):
    t = min(1, max(0, (x - a) / (b - a))); return t * t * (3 - 2 * t)
LIFT = 0.14
HOVER, BOB = 0.07, 0.045
FLICK = [0.2, 1, 0.05, 0.9, 0.75, 0, 0.5, 0.08, 0.3, 0]
def light_level(s):
    if s < 1.05: return 1.0
    if s >= 1.85: return 0.0
    u = (s - 1.05) / 0.8 * len(FLICK); i = min(len(FLICK) - 1, int(u)); f = u - i
    nxt = FLICK[i + 1] if i + 1 < len(FLICK) else 0
    return FLICK[i] + (nxt - FLICK[i]) * (f - 0.7) / 0.3 if f > 0.7 else FLICK[i]
ON_RGB, OFF_RGB = (0xFF, 0xC9, 0x3C), (0xE4, 0xE3, 0xEC)
def mix(a, b, t): return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))

def frameH(F, AA=None, book='hold', glow_s=1.0, glow_a=1.0, wink=0.0, rays=0.0, rot=0, lift=HOVER, tx=0.0, sx=1.0, light=1.0, fil=False, cheek=None):
    AA = AA or R.ARMS['hold']
    f = R.xform(rot=rot, tx=tx / 2.8, ty=-lift / 2.8, sx=sx, oy=0.55)
    UH = R.xform(sx=B.K, sy=B.K, ox=0.5, oy=(2.8 - B.C0) / 2.8, ty=-(B.CZ - B.C0 + LIFT) / 2.8)
    cv = R.Canvas(W)
    # Schatten am Boden
    h = min(1, max(0, lift / 0.25))
    for k, a in ((1.0, 0.10), (0.75, 0.12), (0.5, 0.14)):
        cv.ell(tx, 0.10, 0.34 * (1 - 0.3 * h) * k, 0.06 * k, (0, 0, 0), a * (1 - 0.5 * h) * 3)
    cv.T = [R.xform(sx=glow_s, sy=glow_s, oy=(2.8 - B.CZ) / 2.8, ty=-lift / 2.8, tx=tx / 2.8)]; cv.shapes(B.glow(), a=glow_a); cv.T = []
    cv.T = [UH, f]; V.rim_arms(cv, AA); R.draw_arm_lines(cv, AA)
    cv.T = [f]; cv.shapes(B.body(B.ON))
    if light < 1: cv.shapes(B.body(B.OFF), a=1 - light)
    if fil and light < 1:
        col = mix((0x6E, 0x6A, 0x7E), (0xFF, 0xF4, 0xC2), light)
        if light > 0.05: cv.ell(0, 1.31, 0.23, 0.10, (0xFF, 0xF4, 0xC2), 0.35 * light)
        for pts in B.filament_lines(): cv.line(pts, 0.024, col, a=1 - 0.55 * light)
    if book == 'hold': cv.T = [UH, f]; cv.shapes(P['book'])
    cv.T = []
    if book == 'down': cv.shapes(BP.lying_book(0.74, 0.13))
    cv.T = [UH, f]; V.rim_hands(cv, AA); R.draw_hands(cv, AA)
    cv.T = [UF, f]
    old = R.BULB; R.BULB = mix(OFF_RGB, ON_RGB, light)
    F = dict(F)
    if cheek: F["chA"], F["chRx"], F["chRz"] = cheek
    R.draw_face(cv, F); R.BULB = old
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

read, happy = R.FACES['read'], R.FACES['happy']
frames = []
segs = [("schwebt und wiegt sich", 3.0), ("Antippen: schwebt hoch", 0.9), ("Antippen: dreht sich", 1.0),
        ("Antippen: wird verlegen", 1.1), ("Antippen: kichert", 0.9), ("Abbruch: Licht flackert, geht aus", 3.4)]
t0 = 0
for name, dur in segs:
    n = int(dur * FPS)
    for i in range(n):
        u = i / FPS; T = t0 + u; ph = (T % 6) / 6 * 2 * math.pi
        bob = (1 - math.cos(ph * 2)) / 2
        rot = 2.4 * math.sin(ph); lift = HOVER + BOB * bob; tx = 0.012 * math.sin(ph + 0.8)
        if name.startswith("schwebt"):
            img = frameH(read, rot=rot, lift=lift, tx=tx, glow_s=1 + 0.06 * (1 - math.cos(ph)) / 2)
        elif "schwebt hoch" in name:
            pk = min(1, u / 0.9); b = bump(pk, 0, 1); k = min(1, 2 * b)
            img = frameH(R.lerp_face(read, happy, R.ease(k)), rot=rot + 4 * math.sin(2 * math.pi * pk) * (1 - pk),
                         lift=lift + 0.14 * smooth(0, 1, b), tx=tx, glow_s=1 + 0.08 * bump(pk, 0, 0.6))
        elif "dreht" in name:
            pk = min(1, u / 1.0); b = bump(pk, 0, 1); k = min(1, 2 * b)
            turn = 360 * smooth(0, 1, pk)
            img = frameH(R.lerp_face(read, happy, R.ease(k)), rot=rot, lift=lift + 0.06 * b, tx=tx, sx=math.cos(math.radians(turn)))
        elif "verlegen" in name:
            pk = min(1, u / 1.1); b = bump(pk, 0, 1); k = min(1, 2 * b)
            F = R.lerp_face(read, happy, R.ease(k))
            img = frameH(F, rot=rot + 3 * b, lift=lift, tx=tx,
                         cheek=(F["chA"] + (1 - F["chA"]) * b, F["chRx"] * (1 + 0.4 * b), F["chRz"] * (1 + 0.4 * b)))
        elif "kichert" in name:
            pk = min(1, u / 0.82); k = min(1, 2 * bump(pk, 0, 1))
            img = frameH(R.lerp_face(read, happy, R.ease(k)), rot=rot + 7 * math.sin(2 * math.pi * pk * 3) * (1 - pk), lift=lift + 0.03 * bump(pk, 0, 1), tx=tx)
        else:
            s = u
            calm = 1 - smooth(0, 0.6, s)
            lt = light_level(s)
            rr = rot * calm - 3.5 * smooth(1.85, 2.5, s)
            ll = (HOVER + BOB * bob * calm) * (1 - smooth(1.85, 2.5, s)) + 0.06 * bump(s, 0, 0.26)
            AA = R.ARMS['hold'] if s < 0.06 else (R.lerp_arm(R.ARMS['hold'], R.ARMS['open'], R.ease(min(1, (s - 0.06) / 0.2))) if s < 0.95
                                                    else R.lerp_arm(R.ARMS['open'], R.ARMS['hang'], R.ease(min(1, (s - 0.95) / 0.75))))
            if s < 0.95: F = R.lerp_face(read, R.FACES['scaredDown'], R.ease(min(1, s / 0.25)))
            elif s < 2.0: F = R.lerp_face(R.FACES['scaredDown'], R.FACES['sad'], R.ease(min(1, (s - 0.95) / 0.45)))
            else: F = R.lerp_face(R.FACES['sad'], R.FACES['cry'], R.ease(min(1, (s - 2.0) / 0.4)))
            book = 'hold' if s < 0.10 else ('down' if s > 0.56 else 'none')
            img = frameH(F, AA, book, rot=rr, lift=ll, tx=tx * calm, light=lt, glow_a=lt, fil=s >= 1.05)
        d = ImageDraw.Draw(img); w = d.textlength(name, font=font)
        d.text(((W - w) / 2, 8), name, fill=(200, 200, 210), font=font)
        frames.append(img.convert("P", palette=Image.ADAPTIVE, colors=160))
    t0 += dur
out = sys.argv[1] if len(sys.argv) > 1 else "/tmp/Lumi_8.2.gif"
frames[0].save(out, save_all=True, append_images=frames[1:], duration=int(1000 / FPS), loop=0, optimize=True)
# Standbilder zum Prüfen
for idx in (10, int(3.0 * FPS) + 9, len(frames) - 70, len(frames) - 40, len(frames) - 1):
    frames[idx].convert('RGB').save(f"/tmp/g10_{idx}.png")
print(len(frames))
