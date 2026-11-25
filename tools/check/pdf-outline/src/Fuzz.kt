import de.funke.app.books.PdfOutline
import java.io.File
import kotlin.random.Random

/** Zufällig beschädigte PDFs: darf nie hängen oder abstürzen. */
fun main(args: Array<String>) {
    val rnd = Random(42)
    val srcs = args.map { File(it).readBytes() }
    val tmp = File.createTempFile("fuzz", ".pdf")
    var worst = 0L
    var nonNull = 0
    val n = 1500
    for (i in 0 until n) {
        val src = srcs[i % srcs.size]
        val b = src.copyOf()
        when (i % 5) {
            0 -> repeat(1 + rnd.nextInt(20)) { b[rnd.nextInt(b.size)] = rnd.nextInt(256).toByte() }
            1 -> { val at = rnd.nextInt(b.size); val len = rnd.nextInt(2000); for (k in at until minOf(b.size, at + len)) b[k] = rnd.nextInt(256).toByte() }
            2 -> { val cut = rnd.nextInt(b.size); tmp.writeBytes(b.copyOf(cut)) }
            3 -> repeat(1 + rnd.nextInt(5)) { val at = rnd.nextInt(b.size); b[at] = "()<>[]/%0123456789 RobjendxrefstartxreftrailerR"[rnd.nextInt(47)].code.toByte() }
            else -> { val at = rnd.nextInt(b.size); for (k in at until minOf(b.size, at + 64)) b[k] = '0'.code.toByte() }
        }
        if (i % 5 != 2) tmp.writeBytes(b)
        val t0 = System.nanoTime()
        val r = PdfOutline.read(tmp, 40)
        val ms = (System.nanoTime() - t0) / 1_000_000
        if (r != null) nonNull++
        if (ms > worst) { worst = ms; println("neuer Höchstwert: $ms ms (Fall $i, Art ${i % 5})") }
    }
    println("fertig: $n Fälle, $nonNull mit Ergebnis, längster $worst ms")
}
