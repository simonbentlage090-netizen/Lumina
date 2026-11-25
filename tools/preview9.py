import os
import sys, math
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L, lumi_rig as R, body9 as B, book_proto as BP
from PIL import Image
P = L.parts()
UF = R.xform(sx=B.K, sy=B.K, ox=0.5, oy=(2.8 - B.C0) / 2.8, ty=-(B.CZ - B.C0) / 2.8)
RIM = (0x6B, 0x62, 0x80)
BOOK_U = [B.Ushape(s) for s in P['book']]
R.FACES['plead'] = R.face(rx=.112, rz=.132, dx=0, dz=.026, lidO=.3, lidI=.3, lidA=0,
               h1x=.032, h1z=.042, h1r=.040, h2x=-.036, h2z=-.048, h2r=.017,
               browA=1, browO=.178, browI=.250, browC=.236,
               mx=.040, mz=1.392, mTop=1.420, mBot=1.386, mFill=1, mTongue=0, mW=.028,
               chA=.9, chRx=.078, chRz=.048, chZ=1.47, rim=.65)
R.FACES['wow'] = R.face(rx=.092, rz=.11, dz=.016, lidO=.2, lidI=.2, lidA=0,
               h1x=.028, h1z=.036, h1r=.03, h2x=-.03, h2z=-.042, h2r=.013,
               mx=.034, mz=1.40, mTop=1.452, mBot=1.345, mFill=1, mTongue=.5, mW=.026, chA=1.0, chRx=.08, chRz=.05)
def rim_arms(cv, AA):
    for sx in (-1, 1):
        a = AA["L" if sx < 0 else "R"]
        cv.line(L.cubic((sx*0.40, 1.02), (sx*a["c1x"], a["c1z"]), (sx*a["c2x"], a["c2z"]), (sx*a["ex"], a["ez"]), 28), 0.124, RIM)
def rim_hands(cv, AA):
    for sx in (-1, 1):
        a = AA["L" if sx < 0 else "R"]
        cv.ell(sx*a["hx"], a["hz"], a["hrx"]+0.022, a["hrz"]+0.022, RIM)
        cv.ell(sx*a["fx"], a["fz"], a["frx"]+0.022, a["frz"]+0.022, RIM, a["fA"])
def frame(F, AA=None, book='hold', W=400, bg=(9, 9, 11), fig=None, glow_s=1.0, wink=0.0, rays=0.0):
    AA = AA or R.ARMS['hold']
    dark = bg[0] < 100
    cv = R.Canvas(W, bg)
    cv.T = [R.xform(sx=glow_s, sy=glow_s, oy=(2.8 - B.CZ) / 2.8)]; cv.shapes(B.glow()); cv.T = []
    figT = [fig] if fig else []
    cv.T = [UF] + figT
    if dark: rim_arms(cv, AA)
    R.draw_arm_lines(cv, AA)
    cv.T = figT; cv.shapes(B.body())
    if book == 'hold': cv.shapes(BOOK_U)
    cv.T = []
    if book == 'down': cv.shapes(BP.lying_book(0.74, 0.13))
    cv.T = [UF] + figT
    if dark: rim_hands(cv, AA)
    R.draw_hands(cv, AA)
    F = dict(F)
    R.draw_face(cv, F)
    if wink > 0:  # rechtes Auge zu: Lid in Kolbenfarbe drüber + Bogen
        ex, ez = 0.235 + F["dx"], 1.62 + F["dz"]
        cv.ell(ex, ez, F["rx"] + 0.012, F["rz"] + 0.012, R.BULB, wink)
        cv.line(L.quad((ex - 0.08, ez + 0.005), (ex, ez - 0.045), (ex + 0.08, ez + 0.005), 14), 0.032, a=wink)
    cv.T = figT
    if rays > 0:
        for i in range(8):
            a = math.radians(i * 45 + 22.5)
            r0, r1 = B.R + 0.10, B.R + 0.10 + 0.16 * rays
            cv.line([(r0 * math.cos(a), B.CZ + r0 * math.sin(a)), (r1 * math.cos(a), B.CZ + r1 * math.sin(a))], 0.035, (0xFF, 0xD2, 0x5C), a=rays)
    cv.T = []
    return cv.img.convert('RGB')
