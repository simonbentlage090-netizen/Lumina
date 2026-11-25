"""Zeitabläufe der Lumi-Animationen (identisch zur App). Rendert Einzelbilder für Vorschau und GIF."""
import os
import math, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L
import lumi_rig as R

P = L.parts()
PIVOT_Y = 0.95

def bump(t, a, b):
    if t <= a or t >= b: return 0.0
    return math.sin(math.pi * (t - a) / (b - a))

def morph(schedule, table, lerp_fn, T):
    """schedule: [(start, key, dauer)], erster Eintrag gilt von Anfang an. Gibt den Wert zur Zeit T."""
    val = table[schedule[0][1]]
    for i in range(1, len(schedule)):
        ts, key, dur = schedule[i]
        if T < ts: break
        nxt = schedule[i + 1][0] if i + 1 < len(schedule) else float("inf")
        t_eval = min(T, nxt)
        val = lerp_fn(val, table[key], R.ease((t_eval - ts) / dur))
    return val

# ---- Abbruch: Buch fällt, Lumi wird traurig und weint ----
SAD_FACES = [(-1, "scared", 0), (0.04, "scaredDown", 0.18), (0.95, "sad", 0.45), (1.55, "cry", 0.40)]
SAD_ARMS = [(-1, "hold", 0), (0.06, "open", 0.20), (0.95, "hang", 0.75)]
RELEASE, LAND = 0.10, 0.56
SAD_END = 2.4

def sob(T):
    ph = (T % 1.6) / 1.6
    return bump(ph, 0.04, 0.17) + 0.7 * bump(ph, 0.21, 0.34)

def sad_frame(T, W=320, loopT=None):
    loopT = T if loopT is None else loopT
    cv = R.Canvas(W)
    F = morph(SAD_FACES, R.FACES, R.lerp_face, T)
    F = dict(F); F["shut"] = max(F["shut"], bump(T, 0.52, 0.70))
    A = morph(SAD_ARMS, R.ARMS, R.lerp_arm, T)
    ty = -0.022 * bump(T, 0.0, 0.26) + 0.008 * R.smooth(0.9, 1.6, T) + 0.006 * R.smooth(1.5, 2.0, T) * sob(loopT)
    fig = R.xform(ty=ty, oy=PIVOT_Y)
    # Schein wird schwächer
    cv.shapes(L.glow(), a=1 - 0.5 * R.smooth(0.9, 2.0, T))
    cv.T = [fig]; R.draw_arm_lines(cv, A); cv.shapes(P["body"]); cv.T = []
    draw_book_sad(cv, T)
    cv.T = [fig]; R.draw_hands(cv, A); R.draw_face(cv, F, loopT); cv.T = []
    return cv.img.convert("RGB")

def draw_book_sad(cv, T):
    W = cv.W
    if T < LAND:
        tau = max(0.0, T - RELEASE); f = tau / (LAND - RELEASE)
        dx, dz, rot = 0.70 * f ** 1.4, -0.47 * f * f, 28 * f ** 1.2
        # um die Buchmitte drehen, dann verschieben
        ox, oz = 0.0, 0.77
        t = R.xform(rot=rot, tx=dx / 2.8, ty=-dz / 2.8, ox=(ox + 1.4) / 2.8, oy=(2.8 - oz) / 2.8)
        cv.T = [t]; cv.shapes(P["book"]); cv.T = []
    else:
        tau = T - LAND
        k = math.exp(-7 * tau)
        sy = 1 - 0.18 * k * math.cos(18 * tau)
        hop = -0.05 * k * abs(math.sin(14 * tau)) / 2.8
        rot = 5 * k * math.cos(14 * tau)
        t = R.xform(rot=rot, sy=sy, ty=hop, ox=(R.BOOK_REST_X + 1.4) / 2.8, oy=(2.8 - R.FLOOR_Z) / 2.8)
        cv.T = [t]; cv.shapes(R.book_down_shapes()); cv.T = []
        # Staubwölkchen links und rechts
        if tau < 0.6:
            e = R.ease(tau / 0.6)
            for side in (-1, 1):
                for j, (dx0, dz0, r0) in enumerate(((0.47, 0.03, 0.035), (0.40, 0.09, 0.025))):
                    x = R.BOOK_REST_X + side * (dx0 + 0.16 * e * (1 - 0.3 * j))
                    z = R.FLOOR_Z + dz0 + 0.06 * e
                    cv.ell(x, z, r0 + 0.05 * e, r0 + 0.04 * e, R.DUST, 0.32 * (1 - e) ** 1.5)

# ---- Erschrecken beim Schieben (kein Zittern) ----
def scare_frame(loopT, scareA, startle, face_val, W=320):
    cv = R.Canvas(W)
    ph = (loopT / 6.0) * 2 * math.pi
    breath = (1 - math.cos(ph * 2)) / 2
    rot = 1.4 * math.sin(ph) * (1 - scareA) - 2.0 * scareA
    ty = -0.0057 * breath - 0.018 * math.sin(math.pi * startle)
    fig = R.xform(rot=rot, ty=ty, oy=PIVOT_Y)
    cv.shapes(L.glow())
    A = R.ARMS["hold"]
    cv.T = [fig]; R.draw_arm_lines(cv, A); cv.shapes(P["body"]); cv.shapes(P["book"]); R.draw_hands(cv, A); R.draw_face(cv, face_val, loopT)
    return cv.img.convert("RGB")

# ---- Freude: zwei sanfte Hüpfer ohne Quetschen, danach ruhiges Wiegen ----
JOY_DUR = 1.3
def happy_frame(T, loopT=None, W=320):
    loopT = T if loopT is None else loopT
    cv = R.Canvas(W)
    ph = (loopT / 6.0) * 2 * math.pi
    ty = 0.0
    if T < JOY_DUR:
        u = T / JOY_DUR * 2; k = min(1, int(u)); f = u - k
        ty = -[0.042, 0.024][k] * 4 * f * (1 - f)
    idle = R.smooth(JOY_DUR - 0.2, JOY_DUR + 0.4, T)
    rot = idle * 1.6 * math.sin(ph * 2)
    ty += idle * -0.006 * (1 - math.cos(ph * 4)) / 2
    fig = R.xform(rot=rot, ty=ty, oy=PIVOT_Y)
    j = min(1.0, T / JOY_DUR)
    g = L.glow()
    s = 1 + 0.08 + 0.10 * math.sin(math.pi * j)
    cv.T = [R.xform(sx=s, sy=s, oy=0.4286)]; cv.shapes(g); cv.T = []
    F = morph([(-1, "read", 0), (0.0, "happy", 0.25)], R.FACES, R.lerp_face, T)
    A = R.ARMS["hold"]
    cv.T = [fig]; R.draw_arm_lines(cv, A); cv.shapes(P["body"]); cv.shapes(P["book"]); R.draw_hands(cv, A); R.draw_face(cv, F, loopT)
    return cv.img.convert("RGB")
