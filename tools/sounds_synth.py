import numpy as np, wave
SR=44100
def env(n, attack=0.004, decay=0.25, release=0.04):
    t=np.arange(n)/SR
    a=np.clip(t/attack,0,1); a=np.sin(a*np.pi/2)**2
    e=a*np.exp(-t/decay)
    r=int(release*SR)
    if r>0 and r<n: e[-r:]*=np.cos(np.linspace(0,np.pi/2,r))**2
    return e
def tone(freq, dur, decay=0.25, partials=((1,1.0,1.0),(2,0.30,1.6),(3,0.10,2.4),(4.2,0.05,3.2)), attack=0.004, detune=0.0):
    n=int(dur*SR); t=np.arange(n)/SR; out=np.zeros(n)
    for mult,amp,dk in partials:
        f=freq*mult
        if f>SR/2.2: continue
        e=env(n,attack,decay/dk)
        out+=amp*e*np.sin(2*np.pi*f*t)
        if detune: out+=amp*0.5*e*np.sin(2*np.pi*f*(1+detune)*t)
    return out
def place(buf, sig, at):
    i=int(at*SR); m=min(len(sig),len(buf)-i); buf[i:i+m]+=sig[:m]
def lowpass(x, cutoff):
    a=np.exp(-2*np.pi*cutoff/SR)
    for _ in range(2):
        y=np.zeros_like(x); z=0.0
        for i in range(len(x)):
            z=(1-a)*x[i]+a*z; y[i]=z
        x=y
    return x
def finish(x, peak_db, tail=0.03):
    f=int(tail*SR); x[-f:]*=np.linspace(1,0,f)**2
    return x/np.max(np.abs(x))*10**(peak_db/20)
def save(name, x):
    d=(np.clip(x,-1,1)*32767).astype(np.int16)
    with wave.open(name+'.wav','wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(d.tobytes())

buf=np.zeros(int(0.66*SR))
place(buf, tone(1318.5, 0.56, decay=0.20), 0.0)
place(buf, tone(1760.0, 0.56, decay=0.26), 0.085)
save('correct', finish(lowpass(buf, 7000), -3))

buf=np.zeros(int(0.48*SR))
soft=((1,1.0,1.0),(2,0.18,1.8),(3,0.05,2.6))
place(buf, tone(220.0, 0.30, decay=0.09, partials=soft, attack=0.008), 0.0)
place(buf, tone(174.6, 0.36, decay=0.13, partials=soft, attack=0.008), 0.11)
save('wrong', finish(lowpass(buf, 1800), -7))

buf=np.zeros(int(0.16*SR))
place(buf, tone(783.99, 0.15, decay=0.045, partials=((1,1.0,1.0),(2,0.15,1.8)), attack=0.003), 0.0)
save('tap', finish(lowpass(buf, 5000), -12, tail=0.02))

buf=np.zeros(int(1.6*SR))
notes=[523.25,659.25,783.99,1046.5]
for i,f in enumerate(notes):
    place(buf, tone(f, 1.1 if i<3 else 1.3, decay=0.32 if i<3 else 0.55, detune=0.0015), i*0.095)
place(buf, tone(2093.0, 1.1, decay=0.45, partials=((1,0.35,1.0),(2,0.08,1.6)))*0.6, 0.38)
save('complete', finish(lowpass(buf, 8000), -3, tail=0.08))
