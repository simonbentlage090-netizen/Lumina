"""Erzeugt Test-PDFs mit Inhaltsverzeichnis in vielen Spielarten und die Erwartung (über pypdf)."""
import io, json, os, re, sys, zlib, random
import pikepdf
from pikepdf import Pdf, Dictionary, Array, Name, String
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A5
import pypdf

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "pdfs")
os.makedirs(OUT, exist_ok=True)

TITLES = [
    "Vorwort",
    "1 Einführung in die Mechanik",
    "1.1 Größen und Einheiten",
    "1.1.1 Länge, Zeit, Masse",
    "1.2 Messfehler – „Genauigkeit“",
    "2 Bewegung in einer Dimension",
    "2.1 Ort und Verschiebung",
    "2.2 Geschwindigkeit",
    "2.2.1 Momentangeschwindigkeit",
    "2.2.2 Durchschnittsgeschwindigkeit",
    "3 Vektoren · Überblick",
    "Anhang A: Ähnliche Übungen (ß, €, ﬁ)",
]
LEVELS = [1, 1, 2, 3, 2, 1, 2, 2, 3, 3, 1, 1]


def reportlab_pdf(path, pages=40, deep=False):
    c = canvas.Canvas(path, pagesize=A5)
    # Jeder Eintrag auf eigener Seite: 0,2,4,…
    plan = {}
    for i, t in enumerate(TITLES):
        plan.setdefault(i * 3, []).append(i)
    for p in range(pages):
        c.drawString(72, 500, f"Seite {p + 1}")
        for i in plan.get(p, []):
            key = f"k{i}"
            c.bookmarkPage(key)
            c.addOutlineEntry(TITLES[i], key, level=LEVELS[i] - 1)
            if deep and LEVELS[i] == 3:
                c.bookmarkPage(key + "d")
                c.addOutlineEntry("Tiefe Ebene " + str(i), key + "d", level=3)
        c.showPage()
    c.save()


def expected(path):
    r = pypdf.PdfReader(path)
    out = []

    def walk(items, level):
        prev = None
        for it in items:
            if isinstance(it, list):
                if level < 3:
                    walk(it, level + 1)
                continue
            try:
                pg = r.get_destination_page_number(it)
            except Exception:
                pg = None
            title = re.sub(r"\s+", " ", str(it.title)).strip()
            out.append([level, pg, title])
    walk(r.outline, 1)
    return {"pages": len(r.pages), "entries": out}


def save_variants(src, stem):
    files = {}
    pdf = Pdf.open(src)
    variants = {
        "objstm": dict(object_stream_mode=pikepdf.ObjectStreamMode.generate),
        "linear": dict(linearize=True),
        "linear_objstm": dict(linearize=True, object_stream_mode=pikepdf.ObjectStreamMode.generate),
        "rc4_40": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=2, aes=False, metadata=False)),
        "rc4_128": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=3, aes=False, metadata=False)),
        "aes128": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=4, aes=True)),
        "aes128_objstm": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=4, aes=True), object_stream_mode=pikepdf.ObjectStreamMode.generate),
        "aes128_nometa": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=4, aes=True, metadata=False)),
        "aes256": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=6)),
        "aes256_objstm": dict(encryption=pikepdf.Encryption(owner="geheim", user="", R=6), object_stream_mode=pikepdf.ObjectStreamMode.generate),
        "rc4_128_userpw": dict(encryption=pikepdf.Encryption(owner="geheim", user="nutzer", R=3, aes=False, metadata=False)),
    }
    for name, kw in variants.items():
        p = os.path.join(OUT, f"{stem}_{name}.pdf")
        try:
            pdf.save(p, **kw)
            files[name] = p
        except Exception as e:
            print("skip", name, e, file=sys.stderr)
    return files


def named_dests_pdf(path, tree=True):
    pdf = Pdf.new()
    for i in range(30):
        pdf.add_blank_page(page_size=(300, 400))
    pages = pdf.pages
    names = []
    for i in range(12):
        names.append((f"sec.{i:02d}", i * 2 + 1))
    if tree:
        # Namensbaum mit Kindern (wie LaTeX/hyperref)
        kids = Array()
        for chunk in range(0, len(names), 4):
            part = names[chunk:chunk + 4]
            arr = Array()
            for key, pg in part:
                arr.append(String(key))
                if pg % 4 == 1:
                    arr.append(pdf.make_indirect(Dictionary(D=Array([pages[pg].obj, Name.XYZ, 0, 400, None]))))
                else:
                    arr.append(Array([pages[pg].obj, Name.Fit]))
            kids.append(pdf.make_indirect(Dictionary(Names=arr, Limits=Array([String(part[0][0]), String(part[-1][0])]))))
        pdf.Root.Names = Dictionary(Dests=pdf.make_indirect(Dictionary(Kids=kids)))
    else:
        d = Dictionary()
        for key, pg in names:
            d[Name("/" + key.replace(".", "_"))] = Array([pages[pg].obj, Name.Fit])
        pdf.Root.Dests = pdf.make_indirect(d)
    # Inhaltsverzeichnis von Hand bauen
    outline_root = pdf.make_indirect(Dictionary(Type=Name.Outlines))
    items = []
    for i, (key, pg) in enumerate(names):
        title = f"Abschnitt {i}: Übersicht" if i % 3 else f"{i} Kapitel Nummer {i}"
        if tree:
            dest = String(key) if i % 2 == 0 else None
            action = None if i % 2 == 0 else Dictionary(S=Name.GoTo, D=String(key))
        else:
            dest = Name("/" + key.replace(".", "_"))
            action = None
        it = Dictionary(Title=String(title), Parent=outline_root)
        if dest is not None:
            it.Dest = dest
        if action is not None:
            it.A = action
        items.append(pdf.make_indirect(it))
    for a, b in zip(items, items[1:]):
        a.Next = b
        b.Prev = a
    outline_root.First = items[0]
    outline_root.Last = items[-1]
    outline_root.Count = len(items)
    pdf.Root.Outlines = outline_root
    pdf.save(path, object_stream_mode=pikepdf.ObjectStreamMode.generate if tree else pikepdf.ObjectStreamMode.disable)


def nested_pages_pdf(path):
    """Seitenbaum mit Zwischenknoten (Count passt zu Kinderzahl, aber Kinder sind Knoten)."""
    pdf = Pdf.new()
    for i in range(12):
        pdf.add_blank_page(page_size=(300, 400))
    leaves = [p.obj for p in pdf.pages]
    root = pdf.Root.Pages
    # 4 Zwischenknoten je 3 Seiten
    mids = []
    for k in range(4):
        mid = pdf.make_indirect(Dictionary(Type=Name.Pages, Parent=root, Count=3, Kids=Array(leaves[k * 3:k * 3 + 3])))
        for leaf in leaves[k * 3:k * 3 + 3]:
            leaf.Parent = mid
        mids.append(mid)
    root.Kids = Array(mids)
    root.Count = 12
    with pdf.open_outline() as ol:
        from pikepdf import OutlineItem
        ol.root.extend([OutlineItem(f"Teil {i}", i * 2) for i in range(6)])
    pdf.save(path)


def wrapper_and_nodest_pdf(path):
    pdf = Pdf.new()
    for i in range(20):
        pdf.add_blank_page(page_size=(300, 400))
    from pikepdf import OutlineItem
    with pdf.open_outline() as ol:
        top = OutlineItem("Mein Buchtitel", 0)
        ch1 = OutlineItem("Kapitel ohne Ziel")  # kein Ziel
        ch1.children.extend([OutlineItem("1.1 Erstes", 3), OutlineItem("1.2 Zweites", 5)])
        ch2 = OutlineItem("2 Zweites Kapitel", 9)
        ch2.children.append(OutlineItem("2.1 Unten", 10))
        top.children.extend([ch1, ch2, OutlineItem("3 Drittes", 15)])
        ol.root.append(top)
    pdf.save(path)


def int_dest_pdf(path):
    pdf = Pdf.new()
    for i in range(10):
        pdf.add_blank_page(page_size=(300, 400))
    root = pdf.make_indirect(Dictionary(Type=Name.Outlines))
    items = [pdf.make_indirect(Dictionary(Title=String(f"Seite {p}"), Parent=root, Dest=Array([p, Name.Fit]))) for p in (1, 4, 7)]
    for a, b in zip(items, items[1:]):
        a.Next = b
    root.First = items[0]
    root.Last = items[-1]
    pdf.Root.Outlines = root
    pdf.save(path)


def incremental_update(src, path):
    """Hängt eine Änderung an (neuer Titel für den ersten Eintrag) – klassisch mit /Prev."""
    data = open(src, "rb").read()
    r = pypdf.PdfReader(src)
    first = r.trailer["/Root"]["/Outlines"].get_object().raw_get("/First")
    num = first.idnum
    obj = first.get_object()
    # Objekt neu schreiben (nur Titel geändert), Verweise übernehmen
    parts = []
    for k, v in obj.items():
        if k == "/Title":
            continue
        if hasattr(v, "idnum"):
            parts.append(f"{k} {v.idnum} {v.generation} R")
        elif isinstance(v, pypdf.generic.ArrayObject):
            inner = " ".join(f"{x.idnum} {x.generation} R" if hasattr(x, "idnum") else str(x) for x in v)
            parts.append(f"{k} [{inner}]")
        else:
            parts.append(f"{k} {v}")
    body = f"{num} 0 obj\n<< /Title (Neuer Titel nach Update) {' '.join(parts)} >>\nendobj\n".encode("latin-1")
    sx = data.rfind(b"startxref")
    prev = int(data[sx + 9:].split()[0])
    size = int(r.trailer["/Size"])
    root = r.trailer.raw_get("/Root")
    off = len(data) + 1
    xref = f"xref\n{num} 1\n{off:010d} 00000 n \ntrailer\n<< /Size {size} /Root {root.idnum} 0 R /Prev {prev} >>\nstartxref\n".encode()
    xref_off = off + len(body)
    out = data + b"\n" + body + xref + f"{xref_off}\n%%EOF\n".encode()
    open(path, "wb").write(out)


def broken_startxref(src, path):
    data = bytearray(open(src, "rb").read())
    sx = data.rfind(b"startxref")
    m = re.match(rb"startxref\s+(\d+)", bytes(data[sx:]))
    num = m.group(1)
    data[sx + 10:sx + 10 + len(num)] = b"9" * len(num)
    open(path, "wb").write(bytes(data))


def broken_offsets(src, path):
    """Alle Einträge der Tabelle um 7 verschoben (häufiger Fehler nach schlechtem Bearbeiten)."""
    data = open(src, "rb").read()
    sx = data.rfind(b"startxref")
    xo = int(data[sx + 9:].split()[0])
    head, tail = data[:xo], data[xo:]

    def shift(m):
        return b"%010d" % (int(m.group(1)) + 7) + m.group(2)
    tail = re.sub(rb"(\d{10})( \d{5} n)", shift, tail)
    open(path, "wb").write(head + tail)


def big_pdf(path):
    c = canvas.Canvas(path, pagesize=A5)
    n = 0
    for ch in range(1, 41):
        for sec in range(1, 9):
            for sub in range(0, 4):
                key = f"k{ch}_{sec}_{sub}"
                c.bookmarkPage(key)
                if sec == 1 and sub == 0:
                    c.addOutlineEntry(f"{ch} Kapitel {ch}", f"c{ch}", level=0) if False else None
                c.drawString(72, 500, key)
                c.showPage()
                n += 1
    c.save()
    # Inhaltsverzeichnis mit pikepdf (3 Ebenen, 40*(1+8*(1+3)) Einträge)
    pdf = Pdf.open(path, allow_overwriting_input=True)
    from pikepdf import OutlineItem
    with pdf.open_outline() as ol:
        pg = 0
        for ch in range(1, 41):
            top = OutlineItem(f"{ch} Kapitel {ch}", pg)
            for sec in range(1, 9):
                s = OutlineItem(f"{ch}.{sec} Abschnitt", pg)
                for sub in range(1, 4):
                    s.children.append(OutlineItem(f"{ch}.{sec}.{sub} Teil", pg + sub))
                top.children.append(s)
                pg += 4
            ol.root.append(top)
    pdf.save(path, object_stream_mode=pikepdf.ObjectStreamMode.generate)


def main():
    cases = {}
    base = os.path.join(OUT, "rl_classic.pdf")
    reportlab_pdf(base)
    cases["rl_classic"] = base
    deep = os.path.join(OUT, "rl_deep.pdf")
    reportlab_pdf(deep, deep=True)
    cases["rl_deep"] = deep
    for k, p in save_variants(base, "rl").items():
        cases["rl_" + k] = p
    p = os.path.join(OUT, "named_tree.pdf"); named_dests_pdf(p, tree=True); cases["named_tree"] = p
    p = os.path.join(OUT, "named_dict.pdf"); named_dests_pdf(p, tree=False); cases["named_dict"] = p
    for k, q in save_variants(os.path.join(OUT, "named_tree.pdf"), "named").items():
        if k in ("aes256_objstm", "rc4_128", "linear_objstm"):
            cases["named_" + k] = q
    p = os.path.join(OUT, "nested_pages.pdf"); nested_pages_pdf(p); cases["nested_pages"] = p
    p = os.path.join(OUT, "wrapper.pdf"); wrapper_and_nodest_pdf(p); cases["wrapper"] = p
    p = os.path.join(OUT, "int_dest.pdf"); int_dest_pdf(p); cases["int_dest"] = p
    p = os.path.join(OUT, "incremental.pdf"); incremental_update(base, p); cases["incremental"] = p
    p = os.path.join(OUT, "broken_startxref.pdf"); broken_startxref(base, p); cases["broken_startxref"] = p
    p = os.path.join(OUT, "broken_offsets.pdf"); broken_offsets(base, p); cases["broken_offsets"] = p
    p = os.path.join(OUT, "broken_startxref_objstm.pdf"); broken_startxref(os.path.join(OUT, "rl_objstm.pdf"), p); cases["broken_startxref_objstm"] = p
    p = os.path.join(OUT, "broken_startxref_aes.pdf"); broken_startxref(os.path.join(OUT, "rl_aes128_objstm.pdf"), p); cases["broken_startxref_aes"] = p
    p = os.path.join(OUT, "big.pdf"); big_pdf(p); cases["big"] = p
    # ohne Inhaltsverzeichnis
    p = os.path.join(OUT, "no_outline.pdf")
    c = canvas.Canvas(p); [c.showPage() for _ in range(3)]; c.save(); cases["no_outline"] = p

    exp = {}
    for k, path in cases.items():
        try:
            if k.endswith("userpw"):
                exp[k] = {"pages": 40, "entries": None}
            elif k.startswith("broken"):
                src = {"broken_startxref": base, "broken_offsets": base,
                       "broken_startxref_objstm": os.path.join(OUT, "rl_objstm.pdf"),
                       "broken_startxref_aes": os.path.join(OUT, "rl_aes128_objstm.pdf")}[k]
                exp[k] = expected(src)
            else:
                exp[k] = expected(path)
        except Exception as e:
            exp[k] = {"error": str(e)}
    json.dump({"cases": cases, "expected": exp}, open(os.path.join(OUT, "cases.json"), "w"), ensure_ascii=False, indent=1)
    print(len(cases), "Fälle")


main()
