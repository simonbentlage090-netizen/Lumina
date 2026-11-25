import sys, zipfile, struct, re, collections
# Verlegt die "eingebetteten" Paketnamen im Compose-Compiler-Plugin (org.jetbrains.kotlin.com.intellij → com.intellij),
# damit es mit dem normalen kotlinc (unverschattet) läuft. Nur für die lokale Prüfung.
src, dst = sys.argv[1], sys.argv[2]
PREFIXES = [("org/jetbrains/kotlin/com/intellij/", "com/intellij/"), ("org.jetbrains.kotlin.com.intellij.", "com.intellij.")]
stats = collections.Counter()
def fix_str(s):
    for a, b in PREFIXES:
        if a in s:
            stats[a] += 1
            s = s.replace(a, b)
    return s
def rewrite_class(data):
    assert data[:4] == b'\xca\xfe\xba\xbe'
    out = bytearray(data[:10])
    count = struct.unpack('>H', data[8:10])[0]
    i = 10; idx = 1
    while idx < count:
        tag = data[i]
        if tag == 1:
            ln = struct.unpack('>H', data[i+1:i+3])[0]
            raw = data[i+3:i+3+ln]
            try:
                s = raw.decode('utf-8')  # modified utf8, aber für ASCII-Pfade ausreichend
                ns = fix_str(s)
                if ns != s:
                    enc = ns.encode('utf-8'); out += bytes([1]) + struct.pack('>H', len(enc)) + enc
                else:
                    out += data[i:i+3+ln]
            except UnicodeDecodeError:
                out += data[i:i+3+ln]
            i += 3 + ln
        elif tag in (7, 8, 16, 19, 20):
            out += data[i:i+3]; i += 3
        elif tag == 15:
            out += data[i:i+4]; i += 4
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
            out += data[i:i+5]; i += 5
        elif tag in (5, 6):
            out += data[i:i+9]; i += 9; idx += 1
        else:
            raise ValueError("tag %d" % tag)
        idx += 1
    out += data[i:]
    return bytes(out)
zin = zipfile.ZipFile(src); zout = zipfile.ZipFile(dst, 'w', zipfile.ZIP_DEFLATED)
other = collections.Counter()
for item in zin.infolist():
    data = zin.read(item.filename)
    if item.filename.endswith('.class'):
        data = rewrite_class(data)
        for m in re.finditer(rb'org/jetbrains/kotlin/(com|org|it|javax|gnu|one|kotlinx)/[a-z]+', data): other[m.group(0)] += 1
    elif item.filename.startswith('META-INF/services/'):
        data = fix_str(data.decode()).encode()
    zout.writestr(item, data)
zout.close()
print(dict(stats)); print("weitere verschattete Präfixe:", other.most_common(10))
