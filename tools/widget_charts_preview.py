import os
"""Python-Nachbau von WidgetCharts.kt: Vorschaubilder für die Widget-Auswahl + Sichtprüfung."""
import sys, datetime as dt
from PIL import Image, ImageDraw, ImageFont
FB='/usr/share/fonts/opentype/inter/Inter-Bold.otf'; FR='/usr/share/fonts/opentype/inter/Inter-Regular.otf'
LIGHT=dict(text=(0x1E,0x1C,0x17),sub=(0x77,0x71,0x5F),track=(0xEF,0xE9,0xDC),accent=(0xF2,0xA3,0x24),ice=(0x5F,0xA8,0xE6),on=(0x1E,0x1C,0x17),bg=(0xFF,0xFD,0xF8))
DARK=dict(text=(0xF5,0xF5,0xF7),sub=(0xA1,0xA1,0xAA),track=(0x2A,0x29,0x32),accent=(0xFF,0xC9,0x3C),ice=(0x7C,0xC0,0xF5),on=(0x1E,0x1A,0x26),bg=(0x17,0x16,0x1C))
def A(c,a): return c+(int(255*a),)
DE=['Mo','Di','Mi','Do','Fr','Sa','So']
def week(w,h,dp,pal,mins,done,frozen,goal,today_idx=6,days=None):
    im=Image.new('RGBA',(w,h),(0,0,0,0)); d=ImageDraw.Draw(im)
    roomy=h>74*dp; labelH=17*dp; valueH=14*dp if roomy else 3*dp
    bottom=h-labelH-2*dp; top=valueH; chartH=max(bottom-top,10*dp)
    sm=max(goal*1.25,max(mins),10); colW=w/7; barW=min(colW*.56,28*dp)
    y=bottom-chartH*goal/sm; x=0
    while x<w: d.line([(x,y),(min(x+4*dp,w),y)],fill=A(pal['sub'],.55),width=int(1.3*dp)); x+=8*dp
    for i in range(7):
        cx=colW*(i+.5); v=mins[i]
        bh=max(chartH*v/sm,7*dp) if v>0 else (10*dp if frozen[i] else 4*dp)
        col=A(pal['accent'],1) if v>0 and done[i] else A(pal['accent'],.45) if v>0 else A(pal['ice'],1) if frozen[i] else A(pal['track'],1)
        rad=min(barW/2,8*dp,bh/2)
        d.rounded_rectangle([cx-barW/2,bottom-bh,cx+barW/2,bottom],radius=rad,fill=col)
        if roomy and v>0:
            f=ImageFont.truetype(FB,int(10*dp)); d.text((cx,bottom-bh-3.5*dp),str(v),font=f,fill=pal['text'] if done[i] else pal['sub'],anchor='ms')
        f=ImageFont.truetype(FB if i==today_idx else FR,int(11*dp))
        d.text((cx,h-4*dp),days[i],font=f,fill=pal['text'] if i==today_idx else pal['sub'],anchor='ms')
        if i==today_idx: d.ellipse([cx-1.4*dp,h-1.2*dp-1.4*dp,cx+1.4*dp,h-1.2*dp+1.4*dp],fill=pal['accent'])
    return im
def month(w,h,dp,pal,year,mon,today,mins,done,frozen,goal):
    im=Image.new('RGBA',(w,h),(0,0,0,0)); d=ImageDraw.Draw(im)
    first=dt.date(year,mon,1); off=first.weekday(); import calendar; ln=calendar.monthrange(year,mon)[1]
    rows=(off+ln+6)//7; headH=15*dp; colW=w/7; rowH=(h-headH)/rows; r=min(colW,rowH)*.40
    f=ImageFont.truetype(FB,int(10*dp))
    for i in range(7): d.text((colW*(i+.5),11*dp),DE[i],font=f,fill=pal['sub'],anchor='ms')
    ns=min(r*.95,12*dp)
    for day in range(1,ln+1):
        idx=off+day-1; cx=colW*(idx%7+.5); cy=headH+rowH*(idx//7+.5); date=dt.date(year,mon,day)
        v=mins[day-1]; fut=date>today; dn=done[day-1]; fr=frozen[day-1]
        if fut: d.ellipse([cx-r+.6*dp,cy-r+.6*dp,cx+r-.6*dp,cy+r-.6*dp],outline=A(pal['sub'],.25),width=int(1.2*dp))
        else:
            col=A(pal['accent'],1) if dn else A(pal['accent'],.28+.42*min(1,v/max(1,goal))) if v>0 else A(pal['ice'],1) if fr else A(pal['track'],1)
            d.ellipse([cx-r,cy-r,cx+r,cy+r],fill=col)
        if date==today: rr=r+2.6*dp; d.ellipse([cx-rr,cy-rr,cx+rr,cy+rr],outline=pal['text'],width=int(1.8*dp))
        if r>7*dp:
            tc=A(pal['sub'],.55) if fut else pal['on'] if dn else (255,255,255) if fr and v==0 else pal['text'] if v>0 else pal['sub']
            d.text((cx,cy+ns*.36),str(day),font=ImageFont.truetype(FB if dn or date==today else FR,int(ns)),fill=tc,anchor='ms')
    return im
def card(inner, pal, title, total, sub, dp, w):
    pad=14*dp; head=40*dp
    H=inner.size[1]+head+12*dp+10*dp+6*dp
    c=Image.new('RGBA',(w,int(H)),(0,0,0,0)); d=ImageDraw.Draw(c)
    d.rounded_rectangle([0,0,w-1,H-1],radius=26*dp,fill=pal['bg'])
    d.text((pad,12*dp),title,font=ImageFont.truetype(FB,int(15*dp)),fill=pal['text'])
    d.text((w-pad,12*dp+2*dp),total,font=ImageFont.truetype(FB,int(13*dp)),fill=(0xC9,0x7A,0) if pal is LIGHT else pal['accent'],anchor='ra')
    d.text((pad,12*dp+21*dp),sub,font=ImageFont.truetype(FR,int(12*dp)),fill=pal['sub'])
    c.alpha_composite(inner,(int(pad),int(12*dp+head)))
    return c
if __name__=='__main__':
    S=3
    mins=[25,0,18,32,12,20,18]; done=[1,0,0,1,0,1,0]; frozen=[0,1,0,0,0,0,0]
    days=['Mi','Do','Fr','Sa','So','Mo','Di']
    out=os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'app', 'src', 'main', 'res')
    import random; random.seed(3)
    mm=[0]*30; dd=[0]*30; ff=[0]*30
    for i in range(10):
        mm[i]=random.choice([0,12,20,25,35,45,8]); dd[i]=1 if mm[i]>=20 else 0
    ff[3]=1; mm[3]=0; dd[3]=0
    sheet=[]
    for pal,dirn in ((LIGHT,'drawable-nodpi'),(DARK,'drawable-night-nodpi')):
        wk=week(222*S,96*S,S,pal,mins,done,frozen,20,days=days)
        mo=month(222*S,170*S,S,pal,2026,11,dt.date(2026,11,10),mm,dd,ff,20)
        # Vorschau-Bilder für die Widget-Auswahl (nur das Diagramm, Text kommt aus dem Layout)
        wk.resize((222*2,96*2),Image.LANCZOS).save(f'{out}/{dirn}/widget_week_preview.png',optimize=True)
        mo.resize((222*2,170*2),Image.LANCZOS).save(f'{out}/{dirn}/widget_month_preview.png',optimize=True)
        sheet.append(card(wk,pal,'Letzte 7 Tage','2 h 05 min','Ø 18 Min · Ziel an 3/7 Tagen',S,250*S))
        sheet.append(card(mo,pal,'November','10 Lesetage','3 h 12 min · Ziel an 6 Tagen',S,250*S))
    W=sum(s.size[0] for s in sheet)+50*S; H=max(s.size[1] for s in sheet)+20*S
    big=Image.new('RGB',(W,H),(0x5A,0x6E,0x8C)); x=10*S
    for s in sheet: big.paste(s,(x,10*S),s); x+=s.size[0]+10*S
    big.resize((W//2,H//2),Image.LANCZOS).save('/tmp/wprev/charts.png')
