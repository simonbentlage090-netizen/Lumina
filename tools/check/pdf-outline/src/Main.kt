import de.funke.app.books.PdfOutline
import java.io.File

/** Aufruf: Main <pdf> <seiten> … – gibt je Datei die Einträge als "Ebene<TAB>Seite<TAB>Titel" aus. */
fun main(args: Array<String>) {
    var i = 0
    while (i + 1 < args.size) {
        val f = File(args[i]); val pages = args[i + 1].toInt()
        val t0 = System.nanoTime()
        val r = PdfOutline.read(f, pages)
        val ms = (System.nanoTime() - t0) / 1_000_000
        println("== ${f.name} ${ms}ms")
        if (r == null) println("NULL") else r.forEach { println("${it.level}\t${it.section}\t${it.title}") }
        i += 2
    }
}
