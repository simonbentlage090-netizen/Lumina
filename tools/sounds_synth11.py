# Lumina 11.0: neuer "Richtig"-Ton. Eigener Klang: weicher Marimba-/Glas-Anschlag, eine Quinte
# (D5 + A5) fast gleichzeitig, darüber ganz leise die Terz (Fis6) und ein kurzer Luft-Schimmer.
# Bewusst kein heller Zwei-Ton-"Ding-Ding" und kein Glockenspiel-Arpeggio.
import numpy as np, wave, subprocess
SR=44100
def env(n, attack, decay):
    t=np.arange(n)/SR
    a=np.clip(t/attack,0,1); a=np.sin(a*np.pi/2)**2
    return a*np.exp(-t/decay)
def mallet(freq, dur, decay, bright=1.0, at=0.0):
    n=int(dur*SR); t=np.arange(n)/SR; out=np.zeros(n)
    # Marimba-artige Teiltöne (1, ~4, ~9,2), die hohen klingen schneller ab
    for mult,amp,dk in ((1.0,1.0,1.0),(3.99,0.32*bright,0.32),(9.2,0.08*bright,0.14),(2.0,0.10,0.7)):
        f=freq*mult
        if f>SR/2.3: continue
        out+=amp*env(n,0.0025,decay*dk)*np.sin(2*np.pi*f*t+0.3*mult)
    return out
def place(buf,sig,at):
    i=int(at*SR); m=min(len(sig),len(buf)-i); buf[i:i+m]+=sig[:m]
def lowpass(x,cut):
    a=np.exp(-2*np.pi*cut/SR); y=np.zeros_like(x); z=0.0
    for i in range(len(x)):
        z=(1-a)*x[i]+a*z; y[i]=z
    return y
def shimmer(dur, at, f0=5200):
    n=int(dur*SR); t=np.arange(n)/SR
    rng=np.random.default_rng(7)
    noise=rng.standard_normal(n)
    # Band um f0 durch zwei einfache Filter
    hp=noise-lowpass(noise,f0*0.6)
    bp=lowpass(hp,f0*1.4)
    return bp*env(n,0.012,0.09)*0.18
def finish(x,peak_db,tail=0.04):
    f=int(tail*SR); x[-f:]*=np.linspace(1,0,f)**2
    return x/np.max(np.abs(x))*10**(peak_db/20)
def save(name,x):
    d=(np.clip(x,-1,1)*32767).astype(np.int16)
    with wave.open(name+'.wav','wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(d.tobytes())
    subprocess.run(["ffmpeg","-y","-loglevel","error","-i",name+".wav","-c:a","libvorbis","-q:a","5",name+".ogg"],check=True)

buf=np.zeros(int(0.9*SR))
place(buf, mallet(587.33,0.88,0.30), 0.000)
place(buf, mallet(880.00,0.86,0.26)*0.85, 0.032)
place(buf, mallet(1479.98,0.5,0.20,bright=0.6)*0.30, 0.070)
place(buf, shimmer(0.25,0.0), 0.040)
save("lumina_correct", finish(lowpass(buf,6500),-3,tail=0.12))

# Runde/Abschluss: dieselbe Klangfarbe, drei Töne aufwärts (D–Fis–A) und eine weiche Oktave
buf=np.zeros(int(1.3*SR))
for i,(f,a) in enumerate(((587.33,1.0),(739.99,0.9),(880.0,0.9),(1174.66,0.7))):
    place(buf, mallet(f,1.1,0.36 if i<3 else 0.55)*a, i*0.085)
place(buf, shimmer(0.4,0.0,6000)*1.2, 0.26)
save('lumina_complete', finish(lowpass(buf,7000),-3,tail=0.08))
print("ok")
