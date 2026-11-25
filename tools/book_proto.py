import os
import math, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import lumi_shapes as L
F, Lz = L.F, L.Lz
PURPLE, PURPLE_D, PURPLE_L, PAGE, PAGE_LINE, RIB = "#7E6CF2", "#5B4BD6", "#A594FF", "#FFF8EA", "#C9BDA3", "#FF6F7D"

def lying_book(cx=0.66, fz=0.13):
    """Zugeklapptes Buch, flach auf dem Boden, leicht von oben gesehen und etwas gedreht."""
    def T(pts): return [(cx + x, fz + z) for x, z in pts]
    out = []
    # Kontaktschatten
    out.append(F(T(L.ellipse(0.02, 0.005, 0.50, 0.045, 40)), "#000000", 0.28))
    # untere Deckelkante (dunkel), Seitenblock (creme), obere Deckelkante
    lip = [(-0.45, 0.0), (0.43, -0.012), (0.44, 0.022), (-0.44, 0.034)]
    out += [F(T(lip), PURPLE_D), Lz(T(lip), 0.024, closed=True)]
    pages = [(-0.41, 0.028), (0.41, 0.016), (0.42, 0.098), (-0.40, 0.11)]
    out += [F(T(pages), PAGE)]
    for k in (0.045, 0.072):
        out.append(Lz(T([(-0.38, 0.028 + k), (0.39, 0.016 + k)]), 0.010, PAGE_LINE))
    out.append(Lz(T(pages), 0.022, closed=True))
    # Deckel oben (Draufsicht, perspektivisch schmaler nach hinten)
    top = [(-0.44, 0.105), (0.45, 0.092), (0.36, 0.235), (-0.35, 0.245)]
    out += [F(T(top), PURPLE),
            F(T([(-0.30, 0.205), (0.26, 0.198), (0.245, 0.22), (-0.29, 0.226)]), PURPLE_L),
            F(T([(-0.44, 0.105), (-0.37, 0.104), (-0.285, 0.244), (-0.35, 0.245)]), PURPLE_D),  # Buchrücken links
            Lz(T(top), 0.026, closed=True)]
    # Lesezeichen hängt vorne heraus
    band = [(0.14, 0.10), (0.18, 0.099), (0.185, -0.05), (0.163, -0.035), (0.142, -0.05)]
    out += [F(T(band), RIB), Lz(T(band), 0.016, closed=True)]
    return out

def tent_book(cx=0.66, fz=0.13):
    """Aufgeklappt mit dem Rücken nach oben, mit Kontaktschatten und gefächerten Seiten."""
    def T(pts): return [(cx + x, fz + z) for x, z in pts]
    out = [F(T(L.ellipse(0.0, 0.0, 0.56, 0.045, 40)), "#000000", 0.28)]
    pages = L.quad((-0.40, 0.0), (-0.20, 0.17), (0.0, 0.22), 14) + L.quad((0.0, 0.22), (0.20, 0.17), (0.40, 0.0), 14)[1:]
    pages += L.quad((0.40, 0.0), (0.0, 0.035), (-0.40, 0.0), 12)[1:-1]
    out += [F(T(pages), PAGE)]
    for k in (0.05, 0.10):
        out.append(Lz(T(L.quad((-0.38 + k, 0.012), (-0.18, 0.16 - k), (0.0, 0.205 - k * 0.9), 12)), 0.010, PAGE_LINE))
        out.append(Lz(T(L.quad((0.0, 0.205 - k * 0.9), (0.18, 0.16 - k), (0.38 - k, 0.012), 12)), 0.010, PAGE_LINE))
    out.append(Lz(T(pages), 0.022, closed=True))
    for sx in (-1, 1):
        board = [(sx * 0.53, 0.0), (sx * 0.03, 0.34), (sx * 0.0, 0.25), (sx * 0.41, 0.0)]
        out += [F(T(board), PURPLE), F(T([(sx * 0.48, 0.008), (sx * 0.07, 0.29), (sx * 0.07, 0.265), (sx * 0.44, 0.008)]), PURPLE_L),
                Lz(T(board), 0.026, closed=True)]
    spine = L.rrect(0.0, 0.305, 0.10, 0.075, 0.032)
    out += [F(T(spine), PURPLE_D), Lz(T(spine), 0.024, closed=True)]
    band = [(0.10, 0.02), (0.14, 0.02), (0.15, -0.045), (0.125, -0.032), (0.105, -0.05)]
    out += [F(T(band), RIB), Lz(T(band), 0.016, closed=True)]
    return out
