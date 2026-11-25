import sys; import os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import widget_charts_preview as C, datetime as dt
FB='/usr/share/fonts/opentype/inter/Inter-Bold.otf'; FR='/usr/share/fonts/opentype/inter/Inter-Regular.otf'
S=3; RES=os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'app', 'src', 'main', 'res')
def font(b, sp): return ImageFont.truetype(FB if b else FR, int(sp*S))
def wrap(d, text, f, w, maxl):
    words=text.replace(' …','\u00a0…').split(' '); lines=[]; cur=''
    for wd in words:
        t=(cur+' '+wd).strip()
        if d.textlength(t,font=f)<=w: cur=t
        else: lines.append(cur); cur=wd
    lines.append(cur)
    if len(lines)>maxl: lines=lines[:maxl]; lines[-1]=lines[-1].rstrip('.')+' …'
    return lines
def flame(d,x,y,s,on=True):
    o=(0xFF,0x8A,0x3D) if on else (0xCF,0xCB,0xD9); i=(0xFF,0xD2,0x5C) if on else (0xE9,0xE7,0xEF); ol=(0x1E,0x1A,0x26) if on else (0x8A,0x85,0x97)
    import math
    pts=[]
    for k in range(40):
        t=2*math.pi*k/40
        px=x+s*0.42*math.sin(t)*(1-0.55*(1+math.cos(t))/2); py=y-s*0.5*math.cos(t)-s*0.05
        pts.append((px,py))
    d.polygon(pts,fill=o,outline=ol,width=max(1,int(S*0.9)))
    d.ellipse([x-s*0.18,y+s*0.02,x+s*0.18,y+s*0.40],fill=i)
def lumi_wide(pal, img, caption, title, text, prog, done, dark):
    W,H=int(300*S),int(140*S)
    c=Image.new('RGBA',(W,H),(0,0,0,0)); d=ImageDraw.Draw(c)
    d.rounded_rectangle([0,0,W-1,H-1],radius=26*S,fill=pal['bg'])
    lw=int((W-22*S)*0.38); lum=Image.open(f"{RES}/{'drawable-night-nodpi' if dark else 'drawable-nodpi'}/lumi_w_{img}.png")
    lh=H-16*S; sc=min(lw/lum.size[0], lh/lum.size[1]); lum=lum.resize((int(lum.size[0]*sc),int(lum.size[1]*sc)),Image.LANCZOS)
    c.alpha_composite(lum,(int(6*S+(lw-lum.size[0])/2),int(8*S+(lh-lum.size[1])/2)))
    x0=6*S+lw+8*S; cw=W-x0-16*S
    f1=font(True,17); f2=font(False,13); fc=font(True,12)
    tl=wrap(d,title,f1,cw,2); xl=wrap(d,text,f2,cw,3)
    hgt=15*S+5*S+len(tl)*21*S+2*S+len(xl)*17*S+10*S+6*S
    y=(H-hgt)/2
    flame(d,x0+7*S,y+8*S,15*S,done); d.text((x0+19*S,y+1.5*S),caption,font=fc,fill=pal['sub'])
    y+=15*S+5*S
    for l in tl: d.text((x0,y),l,font=f1,fill=pal['text']); y+=21*S
    y+=2*S
    for l in xl: d.text((x0,y),l,font=f2,fill=pal['sub']); y+=17*S
    y+=10*S
    d.rounded_rectangle([x0,y,x0+cw,y+6*S],radius=3*S,fill=pal['track'])
    if prog>0: d.rounded_rectangle([x0,y,x0+max(6*S,cw*prog),y+6*S],radius=3*S,fill=pal['accent'])
    return c
def lumi_small(pal,img,title,dark):
    W=H=int(140*S); c=Image.new('RGBA',(W,H),(0,0,0,0)); d=ImageDraw.Draw(c)
    d.rounded_rectangle([0,0,W-1,H-1],radius=26*S,fill=pal['bg'])
    f=font(True,13); tl=wrap(d,title,f,W-16*S,2)
    th=len(tl)*17*S+2*S; lh=H-16*S-th
    lum=Image.open(f"{RES}/{'drawable-night-nodpi' if dark else 'drawable-nodpi'}/lumi_w_{img}.png"); sc=lh/lum.size[1]
    lum=lum.resize((int(lum.size[0]*sc),int(lh)),Image.LANCZOS); c.alpha_composite(lum,(int((W-lum.size[0])/2),int(8*S)))
    y=8*S+lh+2*S
    for l in tl: d.text((W/2,y),l,font=f,fill=pal['text'],anchor='ma'); y+=17*S
    return c
def streak(pal,n,done,status,freezes):
    W=H=int(140*S); c=Image.new('RGBA',(W,H),(0,0,0,0)); d=ImageDraw.Draw(c)
    d.rounded_rectangle([0,0,W-1,H-1],radius=26*S,fill=pal['bg'])
    y=18*S; flame(d,W/2,y+15*S,30*S,done); y+=34*S
    d.text((W/2,y),str(n),font=font(True,30),fill=pal['text'],anchor='ma'); y+=36*S
    d.text((W/2,y),'Tage Streak',font=font(False,12),fill=pal['sub'],anchor='ma'); y+=20*S
    f=font(True,11); col=(0xC9,0x7A,0) if done and pal is C.LIGHT else pal['accent'] if done else pal['sub']
    tw=d.textlength(status,font=f)+(d.textlength(f'  * {freezes}',font=f) if freezes else 0)
    x=W/2-tw/2; d.text((x,y),status,font=f,fill=col)
    if freezes: d.text((x+d.textlength(status,font=f),y),f'  * {freezes}',font=f,fill=pal['ice'])
    return c
L,D=C.LIGHT,C.DARK
rows=[
 [lumi_wide(L,'scared','5 Tage · 0/20 Min','Streak in Gefahr!','Dein Streak von 5 Tagen steht auf dem Spiel. Eine Session reicht.',0,False,False),
  lumi_wide(D,'cry','5 Tage · 0/20 Min','Unser Streak …','5 Tage – und heute Nacht ist alles weg, wenn du nicht liest.',0,False,True),
  lumi_wide(L,'fire','9 Tage · 25/20 Min','Du hast einen Lauf!','9 Tage am Stück. Niemand hält dich auf!',1,True,False)],
 [lumi_wide(D,'miss','0 Tage · 0/20 Min','Ich vermisse dich …','Seit 5 Tagen kein Buch. Nur 10 Minuten?',0,False,True),
  lumi_wide(L,'wink','3 Tage · 12/20 Min','Fast geschafft!','Noch 8 Minuten bis zu deinem Tagesziel.',.6,True,False),
  lumi_wide(D,'ice','4 Tage · 0/20 Min','Puh, Glück gehabt!',"Dein Streak war gestern auf Eis. Heute geht's weiter!",0,False,True)],
]
small=[lumi_small(L,'sad','Schon 20 Uhr …',False),lumi_small(D,'go','Gib nicht auf!',True),lumi_small(L,'cheer','Geschafft!',False),
       streak(L,9,True,'Heute geschafft',1),streak(D,5,False,'Heute noch lesen!',0)]
mins=[25,0,18,32,12,20,18]; done=[1,0,0,1,0,1,0]; frozen=[0,1,0,0,0,0,0]
wk=C.card(C.week(272*S,80*S,S,L,mins,done,frozen,20,days=['Mi','Do','Fr','Sa','So','Mo','Di']),L,'Letzte 7 Tage','2 h 05 min','Ø 18 Min · Ziel an 3/7 Tagen',S,300*S)
import random; random.seed(3); mm=[0]*30; dd=[0]*30; ff=[0]*30
for i in range(10): mm[i]=random.choice([0,12,20,25,35,45,8]); dd[i]=1 if mm[i]>=20 else 0
ff[3]=1; mm[3]=0
mo=C.card(C.month(272*S,150*S,S,D,2026,11,dt.date(2026,11,10),mm,dd,ff,20),D,'November','9 Lesetage','3 h 12 min · Ziel an 6 Tagen',S,300*S)
gap=14*S; Wt=3*300*S+4*gap
Ht=gap+2*(140*S+gap)+140*S+gap+max(wk.size[1],mo.size[1])+gap+40*S
bg=Image.new('RGB',(int(Wt),int(Ht)))
dd_=ImageDraw.Draw(bg)
for yy in range(int(Ht)):
    t=yy/Ht; dd_.line([(0,yy),(Wt,yy)],fill=(int(0x3B+(0x7A-0x3B)*t),int(0x4A+(0x5C-0x4A)*t),int(0x7A+(0x8C-0x7A)*t)))
def paste(im,x,y):
    sh=Image.new('RGBA',im.size,(0,0,0,0)); a=im.getchannel('A').point(lambda v: int(v*0.35)); sh.putalpha(a)
    sh=sh.filter(ImageFilter.GaussianBlur(6*S)); bg.paste((0,0,0),(int(x),int(y+4*S)),sh); bg.paste(im,(int(x),int(y)),im)
y=gap
for r in rows:
    x=gap
    for im in r: paste(im,x,y); x+=im.size[0]+gap
    y+=140*S+gap
x=gap
for im in small: paste(im,x,y); x+=im.size[0]+gap
y+=140*S+gap
paste(wk,gap,y); paste(mo,gap+300*S+gap,y)
dd_=ImageDraw.Draw(bg)
dd_.text((gap+2*(300*S+gap), y+10*S),'Lumi-Widget: 16 Bilder,\nSätze wechseln alle 30 Min.\nKlein (2x2) zeigt nur Lumi\n+ Überschrift.\n\nAntippen öffnet Lumina.',font=font(True,15),fill=(255,255,255),spacing=8*S)
bg=bg.resize((bg.size[0]*2//3,bg.size[1]*2//3),Image.LANCZOS); bg.save(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'Widgets-Vorschau.png'),optimize=True); print(bg.size)
