package de.funke.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =============================================================================================
// Formelsatz: Text mit Formeln zwischen $…$ in einer kleinen LaTeX-Teilmenge.
// Unterstützt: \frac \dfrac \tfrac, \binom, \sqrt, ^ und _, \int \sum mit Grenzen, \text{…},
// griechische Buchstaben, Pfeile und Relationen, Funktionsnamen (sin, ln …), Abstände.
// Ohne WebView und ohne fremde Library, damit es schnell und offline bleibt.
// =============================================================================================

/** Baustein einer geparsten Formel. */
sealed interface MNode

data class MSym(val text: String, val italic: Boolean = false, val op: Boolean = false) : MNode
data class MRow(val items: List<MNode>) : MNode
data class MFrac(val num: MRow, val den: MRow, val small: Boolean, val binom: Boolean = false) : MNode
data class MSqrt(val body: MRow) : MNode
data class MScript(val base: MNode, val sup: MRow?, val sub: MRow?) : MNode
data class MBigOp(val symbol: String, val sup: MRow?, val sub: MRow?) : MNode
data class MSpace(val em: Float) : MNode

/** Zerlegt eine Formel (ohne die $-Zeichen) in Bausteine. Fehlerhafte Eingaben werden so gut wie möglich gezeigt. */
object MathParser {
    private val symbols = mapOf(
        "cdot" to "·", "times" to "×", "approx" to "≈", "Rightarrow" to "⇒", "Leftrightarrow" to "⇔",
        "rightarrow" to "→", "to" to "→", "leftarrow" to "←", "propto" to "∝", "ll" to "≪", "gg" to "≫",
        "le" to "≤", "leq" to "≤", "ge" to "≥", "geq" to "≥", "neq" to "≠", "equiv" to "≡", "sim" to "∼",
        "pm" to "±", "mp" to "∓", "infty" to "∞", "partial" to "∂", "nabla" to "∇", "circ" to "°", "degree" to "°",
        "uparrow" to "↑", "downarrow" to "↓", "langle" to "⟨", "rangle" to "⟩", "dots" to "…", "ldots" to "…",
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "Gamma" to "Γ", "delta" to "δ", "Delta" to "Δ",
        "epsilon" to "ε", "varepsilon" to "ε", "eta" to "η", "theta" to "θ", "kappa" to "κ", "lambda" to "λ",
        "Lambda" to "Λ", "mu" to "μ", "nu" to "ν", "xi" to "ξ", "pi" to "π", "rho" to "ρ", "sigma" to "σ",
        "Sigma" to "Σ", "tau" to "τ", "phi" to "φ", "varphi" to "φ", "Phi" to "Φ", "chi" to "χ", "psi" to "ψ",
        "Psi" to "Ψ", "omega" to "ω", "Omega" to "Ω", "hbar" to "ℏ", "ell" to "ℓ", "%" to "%", "&" to "&",
        "{" to "{", "}" to "}", "|" to "‖", "cap" to "∩", "cup" to "∪", "in" to "∈", "forall" to "∀", "exists" to "∃", "square" to "▢", "checkmark" to "✓", "odot" to "⊙", "mid" to "∣", "leftrightarrow" to "↔", "star" to "⋆", "cdots" to "⋯", "prime" to "′", "ast" to "∗",
    )
    private val bigOps = mapOf("int" to "∫", "oint" to "∮", "sum" to "∑", "prod" to "∏")
    private val functions = setOf("sin", "cos", "tan", "ln", "log", "exp", "max", "min", "lim", "det", "arcsin", "arccos", "arctan", "sinh", "cosh")
    private val spaces = mapOf("," to 0.17f, ";" to 0.28f, ":" to 0.22f, " " to 0.25f, "quad" to 1f, "qquad" to 2f, "!" to 0f)
    private val ignored = setOf("left", "right", "displaystyle", "textstyle", "big", "Big", "bigg", "Bigg", "limits", "nolimits", "mathbf", "boldsymbol", "vec", "mathcal", "mathbb", "mathit", "Bigl", "Bigr", "bigl", "bigr")
    private val relations = setOf("=", "≈", "⇒", "⇔", "→", "∝", "<", ">", "≤", "≥", "≠", "≡", "≪", "≫", "∼", "+", "−", "±", "·", "×", "∈")

    fun parse(src: String): MRow = Reader(src).row(null)

    private class Reader(val s: String) {
        var i = 0

        fun row(stop: Char?): MRow {
            val items = mutableListOf<MNode>()
            while (i < s.length) {
                val c = s[i]
                if (c == '}' && stop == '}') { i++; break }
                if (c == '}' ) { i++; continue }
                if (c == ' ' || c == '\n') { i++; continue }
                val atom = atom() ?: continue
                items += scripts(atom)
            }
            return MRow(items)
        }

        private fun group(): MRow {
            while (i < s.length && s[i] == ' ') i++
            if (i >= s.length) return MRow(emptyList())
            if (s[i] == '{') { i++; return row('}') }
            // Wie in LaTeX: ohne Klammern zählt nur ein Zeichen (\frac12 = 1/2, x^23 = x² · 3)
            if (s[i].isDigit()) { i++; return MRow(listOf(MSym(s[i - 1].toString()))) }
            val a = atom() ?: return MRow(emptyList())
            return MRow(listOf(a))
        }

        private fun scripts(base: MNode): MNode {
            var sup: MRow? = null
            var sub: MRow? = null
            while (i < s.length && (s[i] == '^' || s[i] == '_')) {
                val up = s[i] == '^'
                i++
                val g = group()
                if (up) sup = g else sub = g
            }
            if (sup == null && sub == null) return base
            return if (base is MBigOp) MBigOp(base.symbol, sup, sub) else MScript(base, sup, sub)
        }

        private fun atom(): MNode? {
            val c = s[i]
            return when {
                c == '{' -> { i++; row('}') }
                c == '\\' -> command()
                c == '^' || c == '_' -> { i++; null }
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < s.length && (s[i].isDigit() || s[i] == '.')) i++
                    MSym(s.substring(start, i))
                }
                c.isLetter() -> { i++; MSym(c.toString(), italic = true) }
                c == '-' -> { i++; MSym("−", op = true) }
                c == '\'' -> { i++; MSym("′") }
                c.toString() in relations -> { i++; MSym(c.toString(), op = true) }
                else -> { i++; MSym(c.toString()) }
            }
        }

        private fun command(): MNode? {
            i++ // Backslash
            if (i >= s.length) return null
            val start = i
            if (s[i].isLetter()) {
                while (i < s.length && s[i].isLetter()) i++
            } else {
                i++
            }
            val name = s.substring(start, i)
            return when {
                name == "frac" || name == "dfrac" || name == "tfrac" -> {
                    val a = group()
                    val b = group()
                    MFrac(a, b, small = name == "tfrac")
                }
                name == "binom" || name == "dbinom" || name == "tbinom" -> {
                    val a = group()
                    val b = group()
                    MFrac(a, b, small = name == "tbinom", binom = true)
                }
                name == "sqrt" -> {
                    if (i < s.length && s[i] == '[') { while (i < s.length && s[i] != ']') i++; i++ }
                    MSqrt(group())
                }
                name == "text" || name == "mathrm" || name == "textrm" || name == "operatorname" -> {
                    while (i < s.length && s[i] == ' ') i++
                    if (i < s.length && s[i] == '{') {
                        i++
                        val b = StringBuilder()
                        var depth = 1
                        while (i < s.length) {
                            val ch = s[i]
                            if (ch == '{') depth++
                            if (ch == '}') { depth--; if (depth == 0) { i++; break } }
                            b.append(ch)
                            i++
                        }
                        MSym(b.toString().replace("{,}", ","))
                    } else null
                }
                name == "bar" || name == "overline" -> {
                    val g = group()
                    val only = g.items.singleOrNull()
                    if (only is MSym) MSym(only.text + "\u0304", only.italic) else g
                }
                name in bigOps -> MBigOp(bigOps.getValue(name), null, null)
                name in functions -> MRow(listOf(MSym(name), MSpace(0.12f)))
                name in spaces -> MSpace(spaces.getValue(name))
                name in ignored -> null
                name in symbols -> {
                    val sym = symbols.getValue(name)
                    MSym(sym, op = sym in relations)
                }
                else -> MSym(name)
            }
        }
    }
}

/** Zerlegt Text in normale Wörter und Formeln ($…$). */
private sealed interface MPiece
private data class MWord(val text: String) : MPiece
private data class MFormula(val row: MRow) : MPiece

private fun pieces(text: String): List<MPiece> {
    val out = mutableListOf<MPiece>()
    var i = 0
    var inMath = false
    val buf = StringBuilder()
    fun flushText() {
        if (buf.isEmpty()) return
        // Wörter mit dem folgenden Leerzeichen, damit nach Formeln kein Abstand vor Satzzeichen entsteht
        val t = buf.toString()
        var start = 0
        for (k in t.indices) {
            if (t[k] == ' ') {
                out += MWord(t.substring(start, k + 1))
                start = k + 1
            }
        }
        if (start < t.length) out += MWord(t.substring(start))
        buf.clear()
    }
    while (i < text.length) {
        val c = text[i]
        if (c == '$') {
            if (inMath) {
                val row = MathParser.parse(buf.toString())
                buf.clear()
                // Auf oberster Ebene darf nach jedem Baustein umbrochen werden (wie bei KaTeX nach "=")
                row.items.forEach { out += MFormula(MRow(listOf(it))) }
            } else flushText()
            inMath = !inMath
            i++
            continue
        }
        buf.append(c)
        i++
    }
    if (inMath) buf.insert(0, '$')
    flushText()
    return out
}

/**
 * Text mit Formeln. Ohne $ ist es ein ganz normales [Text].
 * Formeln werden in der Schrift der Umgebung gesetzt, Variablen kursiv, Brüche übereinander.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MathText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    /** Zahlen mit Einheit hervorheben (fetter, in [emphasis]): Das Auge findet die Angaben sofort. */
    emphasis: Color? = null,
) {
    val c = if (color == Color.Unspecified) (style.color.takeIf { it != Color.Unspecified } ?: LocalContentColor.current) else color
    if ('$' !in text) {
        if (emphasis != null) Text(remember(text, emphasis) { emphasize(text, emphasis) }, modifier = modifier, style = style, color = c)
        else Text(text, modifier = modifier, style = style, color = c)
        return
    }
    val parts = remember(text) { pieces(text) }
    val size = if (style.fontSize == TextUnit.Unspecified) 16.sp else style.fontSize
    FlowRow(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        parts.forEach { p ->
            when (p) {
                is MWord -> if (emphasis != null) {
                    Text(remember(p.text, emphasis) { emphasize(p.text, emphasis) }, style = style, color = c, modifier = Modifier.align(Alignment.CenterVertically))
                } else {
                    Text(p.text, style = style, color = c, modifier = Modifier.align(Alignment.CenterVertically))
                }
                is MFormula -> Box(Modifier.align(Alignment.CenterVertically)) { MathRow(p.row, size, style, c) }
            }
        }
    }
}

@Composable
private fun MathRow(row: MRow, size: TextUnit, style: TextStyle, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        row.items.forEach { MathNode(it, size, style, color) }
    }
}

@Composable
private fun MathNode(n: MNode, size: TextUnit, style: TextStyle, color: Color) {
    val density = LocalDensity.current
    fun em(f: Float): Dp = with(density) { (size * f).toDp() }
    val sym = style.copy(fontSize = size, lineHeight = size * 1.2f, letterSpacing = 0.sp, fontFeatureSettings = "tnum, ss03")
    when (n) {
        is MSym -> Text(
            n.text,
            style = sym.copy(fontStyle = if (n.italic) FontStyle.Italic else FontStyle.Normal),
            color = color,
            modifier = if (n.op) Modifier.padding(horizontal = em(0.16f)) else Modifier,
        )
        is MRow -> MathRow(n, size, style, color)
        is MSpace -> Spacer(Modifier.width(em(n.em)))
        is MFrac -> if (n.binom) {
            // Binomialkoeffizient: übereinander ohne Strich, in hohen, gezeichneten Klammern
            val f = if (n.small) 0.72f else 0.86f
            Row(Modifier.height(IntrinsicSize.Min).padding(horizontal = em(0.06f)), verticalAlignment = Alignment.CenterVertically) {
                MathParen(left = true, color = color, width = em(0.32f))
                Column(Modifier.width(IntrinsicSize.Max).padding(vertical = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    MathRow(n.num, size * f, style, color)
                    Spacer(Modifier.height(em(0.08f)))
                    MathRow(n.den, size * f, style, color)
                }
                MathParen(left = false, color = color, width = em(0.32f))
            }
        } else {
            val f = if (n.small) 0.72f else 0.86f
            Column(
                Modifier.width(IntrinsicSize.Max).padding(horizontal = em(0.1f)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MathRow(n.num, size * f, style, color)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                        .height(1.dp)
                        .background(color)
                )
                MathRow(n.den, size * f, style, color)
            }
        }
        is MSqrt -> Row(verticalAlignment = Alignment.CenterVertically) {
            Text("√", style = sym.copy(fontSize = size * 1.15f), color = color)
            Box(
                Modifier
                    .drawBehind { drawLine(color, Offset(0f, 0f), Offset(this.size.width, 0f), 1.dp.toPx()) }
                    .padding(top = 2.dp, start = 1.dp, end = 2.dp)
            ) { MathRow(n.body, size, style, color) }
        }
        is MScript -> Row(verticalAlignment = Alignment.CenterVertically) {
            MathNode(n.base, size, style, color)
            Column {
                if (n.sup != null) MathRow(n.sup, size * 0.7f, style, color) else Spacer(Modifier.height(em(0.5f)))
                if (n.sub != null) MathRow(n.sub, size * 0.7f, style, color) else Spacer(Modifier.height(em(0.5f)))
            }
        }
        is MBigOp -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = em(0.08f))) {
            if (n.sup != null) MathRow(n.sup, size * 0.65f, style, color)
            Text(n.symbol, style = sym.copy(fontSize = size * 1.45f, lineHeight = size * 1.5f), color = color)
            if (n.sub != null) MathRow(n.sub, size * 0.65f, style, color)
        }
    }
}

/** Eine runde Klammer, so hoch wie ihr Inhalt. Gezeichnet, weil eine Schrift-Klammer nicht mitwächst. */
@Composable
private fun MathParen(left: Boolean, color: Color, width: Dp) {
    Box(
        Modifier
            .fillMaxHeight()
            .width(width)
            .drawBehind {
                val w = size.width
                val h = size.height
                val path = androidx.compose.ui.graphics.Path().apply {
                    if (left) {
                        moveTo(w * 0.86f, h * 0.03f)
                        quadraticBezierTo(-w * 0.30f, h / 2f, w * 0.86f, h * 0.97f)
                    } else {
                        moveTo(w * 0.14f, h * 0.03f)
                        quadraticBezierTo(w * 1.30f, h / 2f, w * 0.14f, h * 0.97f)
                    }
                }
                drawPath(
                    path,
                    color,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
                )
            }
    )
}

/** Zahl (mit Komma, Punkt oder Tausenderpunkt) und eine gleich folgende kurze Einheit. */
private val NumberWithUnit = Regex("""(?<![\p{L}])(\d+(?:[.,]\d+)*(?:\s?(?:%|‰|°C|°|km/h|m/s²|m/s|m²|m³|cm³|cm²|km²|kWh|MeV|keV|eV|GeV|nm|µm|mm|cm|km|kg|mg|ml|Hz|kHz|MHz|Pa|hPa|bar|min|Mio\.|Mrd\.|N|J|W|V|A|Ω|K|t|l|g|m|s|h|Jahre|Tage|Stunden|Minuten|Sekunden|years|days|hours|minutes|seconds))?)(?![\p{L}\d])""")

/** Text mit hervorgehobenen Zahlen (halbfett, Farbe [color]). */
internal fun emphasize(text: String, color: Color): androidx.compose.ui.text.AnnotatedString =
    androidx.compose.ui.text.buildAnnotatedString {
        var last = 0
        for (m in NumberWithUnit.findAll(text)) {
            append(text.substring(last, m.range.first))
            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = color))
            append(m.value)
            pop()
            last = m.range.last + 1
        }
        append(text.substring(last))
    }
