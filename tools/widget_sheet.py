import sys, glob, os
from PIL import Image, ImageDraw, ImageFont
names = sys.argv[1].split(',')
fnt = ImageFont.truetype('/usr/share/fonts/opentype/inter/Inter-Bold.otf', 18)
ims = []
for n in names:
    for v, bg in (('l', (255, 255, 255)), ('d', (0x1A, 0x19, 0x20))):
        im = Image.open(f'widget/{v}_{n}.png'); c = Image.new('RGBA', im.size, bg + (255,)); c.alpha_composite(im); ims.append((n, c))
w, h = ims[0][1].size; cols = 8
rows = (len(ims) + cols - 1) // cols
sh = Image.new('RGB', (cols * w, rows * (h + 28)), 'white'); d = ImageDraw.Draw(sh)
for i, (n, im) in enumerate(ims):
    x, y = (i % cols) * w, (i // cols) * (h + 28)
    d.text((x + 6, y + 4), n, font=fnt, fill=(30, 26, 38)); sh.paste(im.convert('RGB'), (x, y + 28))
sh.save(sys.argv[2])
