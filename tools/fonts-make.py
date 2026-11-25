import sys
from fontTools.ttLib import TTFont
from fontTools.varLib import instancer
from fontTools import subset

SRC = "/home/claude/google/fonts/ofl"
OUT = "/home/claude/work/fonts/out"
# Latein (inkl. Erweiterungen), Griechisch (π, Δ), Satzzeichen, Währung, Brüche, Pfeile, Mathe, Formen, Ligaturen
UNICODES = (
    "U+0020-007E,U+00A0-024F,U+0259,U+02B0-02FF,U+0300-036F,U+0370-03FF,U+1E00-1EFF,"
    "U+2000-206F,U+2070-209F,U+20A0-20CF,U+2100-214F,U+2150-218F,U+2190-21FF,U+2200-22FF,"
    "U+2300-23FF,U+25A0-25FF,U+2600-26FF,U+2700-27BF,U+FB00-FB06,U+FEFF,U+FFFD"
)

def make(src, axes, out):
    vf = TTFont(src)
    inst = instancer.instantiateVariableFont(vf, axes, updateFontNames=True)
    opts = subset.Options()
    opts.layout_features = ["*"]
    opts.name_IDs = ["*"]
    opts.name_languages = ["*"]
    opts.notdef_outline = True
    opts.glyph_names = False
    opts.hinting = False
    sub = subset.Subsetter(options=opts)
    sub.populate(unicodes=subset.parse_unicodes(UNICODES))
    sub.subset(inst)
    inst.save(f"{OUT}/{out}")
    print(out, "ok")

for w, name in [(300, "light"), (400, "regular"), (500, "medium"), (600, "semibold"), (700, "bold")]:
    make(f"{SRC}/nunito/Nunito[wght].ttf", {"wght": w}, f"nunito_{name}.ttf")
for w, name in [(400, "regular"), (500, "medium"), (600, "semibold"), (700, "bold")]:
    make(f"{SRC}/fredoka/Fredoka[wdth,wght].ttf", {"wght": w, "wdth": 100}, f"fredoka_{name}.ttf")
