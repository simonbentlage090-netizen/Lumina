package de.funke.app.ui.calc

import kotlin.math.PI
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.acosh
import kotlin.math.asin
import kotlin.math.asinh
import kotlin.math.atan
import kotlin.math.atanh
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

/*
 * Rechenkern des Taschenrechners (seit 13.4). Ohne Oberfläche, damit er sich testen lässt.
 *
 * Der Ausdruck ist ein Text aus den Zeichen der Tasten, z. B. "2sin(30)+√(2)×10ᴇ3". Intern immer mit
 * Punkt als Dezimaltrennzeichen; die Anzeige macht daraus im Deutschen ein Komma.
 *
 * Grammatik (von schwach nach stark bindend):
 *   sum     = product (("+" | "−") product)*
 *   product = signed (("×" | "÷" | "mod" | "C" | "P") signed | implizit-mal power)*
 *   signed  = "−" signed | power
 *   power   = postfix ("^" signed | "ʸ√" signed)?          (rechts-assoziativ)
 *   postfix = atom ("!" | "²" | "³" | "⁻¹" | "%")*
 *   atom    = Zahl ["ᴇ" ["−"] Zahl] | "π" | "e" | "Ans" | "(" sum ")" | Funktion "(" sum ")" | "√" atom | "∛" atom
 * Offene Klammern werden am Ende geschlossen. "a ʸ√ b" heißt "a-te Wurzel aus b".
 */

enum class AngleMode { Deg, Rad }

/** Ergebnis: entweder eine Zahl oder ein Grund, warum nicht. */
sealed interface CalcResult {
    data class Ok(val value: Double) : CalcResult
    data class Error(val reason: CalcError) : CalcResult
}

enum class CalcError { Syntax, DivZero, Domain, Overflow }

/** Funktionen, die als "name(" eingegeben werden. Längere Namen zuerst, damit "asinh" nicht als "asin" gelesen wird. */
internal val Functions: List<String> = listOf(
    "asinh", "acosh", "atanh", "sinh", "cosh", "tanh", "asin", "acos", "atan", "sin", "cos", "tan",
    "log2", "log", "ln", "exp", "abs",
)

object CalcEngine {

    fun eval(expr: String, mode: AngleMode, ans: Double = 0.0): CalcResult {
        if (expr.isBlank()) return CalcResult.Error(CalcError.Syntax)
        val open = expr.count { it == '(' } - expr.count { it == ')' }
        val e = if (open > 0) expr + ")".repeat(open) else expr
        return try {
            val p = Parser(e, mode, ans)
            val v = p.sum()
            if (!p.atEnd) throw CalcFail(CalcError.Syntax)
            when {
                v.isNaN() -> CalcResult.Error(CalcError.Domain)
                v.isInfinite() -> CalcResult.Error(CalcError.Overflow)
                else -> CalcResult.Ok(clean(v))
            }
        } catch (f: CalcFail) {
            CalcResult.Error(f.reason)
        }
    }

    /** Kleine Rundungsreste entfernen (sin(180°) = 0 statt 1,2·10⁻¹⁶). */
    private fun clean(v: Double): Double {
        if (abs(v) < 1e-13) return 0.0
        val r = round(v)
        return if (abs(v - r) < 1e-12 * maxOf(1.0, abs(v))) r else v
    }

    private class CalcFail(val reason: CalcError) : Exception()

    private class Parser(val e: String, val mode: AngleMode, val ans: Double) {
        var i = 0
        val atEnd get() = i >= e.length

        fun at(s: String) = e.startsWith(s, i)
        fun peek(): Char = if (i < e.length) e[i] else '\u0000'

        fun sum(): Double {
            var v = product()
            while (true) {
                v = when {
                    peek() == '+' -> { i++; v + product() }
                    peek() == '−' || peek() == '-' -> { i++; v - product() }
                    else -> return v
                }
            }
        }

        fun product(): Double {
            var v = signed()
            while (true) {
                when {
                    peek() == '×' || peek() == '*' -> { i++; v *= signed() }
                    peek() == '÷' || peek() == '/' -> {
                        i++
                        val r = signed()
                        if (r == 0.0) throw CalcFail(CalcError.DivZero)
                        v /= r
                    }
                    at("mod") -> {
                        i += 3
                        val r = signed()
                        if (r == 0.0) throw CalcFail(CalcError.DivZero)
                        v -= r * floor(v / r)
                    }
                    peek() == 'C' -> { i++; v = combinations(v, signed(), ordered = false) }
                    peek() == 'P' -> { i++; v = combinations(v, signed(), ordered = true) }
                    // Malzeichen darf fehlen: 2π, 3(4+1), 2sin(30), (1+2)(3+4), 2√(9)
                    startsAtom() -> v *= power()
                    else -> return v
                }
            }
        }

        fun startsAtom(): Boolean {
            val c = peek()
            return c.isDigit() || c == '.' || c == '(' || c == 'π' || c == '√' || c == '∛' || at("Ans") ||
                (c == 'e' && !at("exp")) || Functions.any { at(it) }
        }

        fun signed(): Double = if (peek() == '−' || peek() == '-') { i++; -signed() } else power()

        fun power(): Double {
            val base = postfix()
            return when {
                peek() == '^' -> { i++; checked(base.pow(signed())) }
                at("ʸ√") -> {
                    i += 2
                    val x = signed()
                    root(x, base)
                }
                else -> base
            }
        }

        fun postfix(): Double {
            var v = atom()
            while (true) {
                v = when {
                    peek() == '!' -> { i++; factorial(v) }
                    peek() == '²' -> { i++; v * v }
                    peek() == '³' -> { i++; v * v * v }
                    at("⁻¹") -> { i += 2; if (v == 0.0) throw CalcFail(CalcError.DivZero); 1 / v }
                    peek() == '%' -> { i++; v / 100 }
                    else -> return v
                }
            }
        }

        fun atom(): Double {
            val c = peek()
            return when {
                c == '(' -> {
                    i++
                    val v = sum()
                    if (peek() != ')') throw CalcFail(CalcError.Syntax)
                    i++
                    v
                }
                c == 'π' -> { i++; PI }
                at("Ans") -> { i += 3; ans }
                c == '√' -> { i++; val x = atom(); if (x < 0) throw CalcFail(CalcError.Domain); sqrt(x) }
                c == '∛' -> { i++; cbrt(atom()) }
                c.isDigit() || c == '.' -> number()
                else -> {
                    val f = Functions.firstOrNull { at(it) }
                    when {
                        f != null -> {
                            i += f.length
                            if (peek() != '(') throw CalcFail(CalcError.Syntax)
                            i++
                            val x = sum()
                            if (peek() != ')') throw CalcFail(CalcError.Syntax)
                            i++
                            apply(f, x)
                        }
                        c == 'e' -> { i++; E }
                        else -> throw CalcFail(CalcError.Syntax)
                    }
                }
            }
        }

        fun number(): Double {
            val start = i
            var dots = 0
            while (peek().isDigit() || peek() == '.') { if (peek() == '.') dots++; i++ }
            if (dots > 1) throw CalcFail(CalcError.Syntax)
            val mant = e.substring(start, i).let { if (it == ".") throw CalcFail(CalcError.Syntax) else it }.toDouble()
            // Zehnerpotenz: 6,02ᴇ23 bzw. 1,6ᴇ−19
            if (peek() == 'ᴇ') {
                i++
                var neg = false
                if (peek() == '−' || peek() == '-') { neg = true; i++ } else if (peek() == '+') i++
                val s = i
                while (peek().isDigit()) i++
                if (s == i) throw CalcFail(CalcError.Syntax)
                val exp = e.substring(s, i).toInt()
                return checked(mant * 10.0.pow(if (neg) -exp else exp))
            }
            return mant
        }

        fun toRad(x: Double) = if (mode == AngleMode.Deg) x * PI / 180 else x
        fun fromRad(x: Double) = if (mode == AngleMode.Deg) x * 180 / PI else x

        fun apply(f: String, x: Double): Double = when (f) {
            "sin" -> sin(toRad(x))
            "cos" -> cos(toRad(x))
            "tan" -> {
                // tan(90°) gibt es nicht
                if (mode == AngleMode.Deg && abs(((x % 180) + 180) % 180 - 90) < 1e-9) throw CalcFail(CalcError.Domain)
                tan(toRad(x))
            }
            "asin" -> { if (x < -1 || x > 1) throw CalcFail(CalcError.Domain); fromRad(asin(x)) }
            "acos" -> { if (x < -1 || x > 1) throw CalcFail(CalcError.Domain); fromRad(acos(x)) }
            "atan" -> fromRad(atan(x))
            "sinh" -> sinh(x)
            "cosh" -> cosh(x)
            "tanh" -> tanh(x)
            "asinh" -> asinh(x)
            "acosh" -> { if (x < 1) throw CalcFail(CalcError.Domain); acosh(x) }
            "atanh" -> { if (x <= -1 || x >= 1) throw CalcFail(CalcError.Domain); atanh(x) }
            "ln" -> { if (x <= 0) throw CalcFail(CalcError.Domain); ln(x) }
            "log" -> { if (x <= 0) throw CalcFail(CalcError.Domain); log10(x) }
            "log2" -> { if (x <= 0) throw CalcFail(CalcError.Domain); log2(x) }
            "exp" -> checked(kotlin.math.exp(x))
            "abs" -> abs(x)
            else -> throw CalcFail(CalcError.Syntax)
        }

        fun root(x: Double, n: Double): Double {
            if (n == 0.0) throw CalcFail(CalcError.Domain)
            if (x < 0) {
                // Ungerade Wurzeln aus negativen Zahlen gibt es: ³√(−8) = −2
                val odd = n == round(n) && round(n).toLong() % 2L != 0L
                if (!odd) throw CalcFail(CalcError.Domain)
                return -((-x).pow(1 / n))
            }
            return x.pow(1 / n)
        }

        fun factorial(x: Double): Double {
            if (x < 0 || x != floor(x)) throw CalcFail(CalcError.Domain)
            if (x > 170) throw CalcFail(CalcError.Overflow)
            var r = 1.0
            for (k in 2..x.toInt()) r *= k
            return r
        }

        fun combinations(n: Double, k: Double, ordered: Boolean): Double {
            if (n < 0 || k < 0 || n != floor(n) || k != floor(k) || k > n) throw CalcFail(CalcError.Domain)
            var r = 1.0
            val kk = if (ordered) k.toInt() else minOf(k, n - k).toInt()
            for (j in 0 until kk) {
                r *= (n - j)
                if (!ordered) r /= (j + 1)
            }
            return checked(round(r))
        }

        fun checked(v: Double): Double {
            if (v.isNaN()) throw CalcFail(CalcError.Domain)
            if (v.isInfinite()) throw CalcFail(CalcError.Overflow)
            return v
        }
    }

    /**
     * Zahl für die Anzeige: bis zu 10 gültige Stellen, sehr große oder kleine als Zehnerpotenz
     * ("6,022·10²³"). [german] = Komma statt Punkt.
     */
    fun format(v: Double, german: Boolean): String {
        val s = formatPlain(v)
        return if (german) s.replace('.', ',') else s
    }

    /** Mit Punkt, Zehnerpotenz als "·10ⁿ" in Hochzahlen. */
    fun formatPlain(v: Double): String {
        if (v == 0.0) return "0"
        val a = abs(v)
        if (a >= 1e12 || a < 1e-7) {
            val exp = floor(log10(a)).toInt()
            var mant = v / 10.0.pow(exp)
            var ex = exp
            if (abs(mant) >= 10) { mant /= 10; ex++ }
            val m = trimZeros("%.6f".format(java.util.Locale.ROOT, mant))
            return m + "·10" + superscript(ex)
        }
        val digits = (10 - 1 - floor(log10(a)).toInt()).coerceIn(0, 12)
        return trimZeros("%.${digits}f".format(java.util.Locale.ROOT, v))
    }

    /** Ohne Hochzahl-Zeichen, zum Weiterrechnen in einem Ausdruck ("6.022ᴇ23"). */
    fun toExpr(v: Double): String {
        val s = formatPlain(v)
        val k = s.indexOf("·10")
        if (k < 0) return s.replace("-", "−")
        val exp = s.substring(k + 3).map { superToDigit(it) }.joinToString("")
        return (s.substring(0, k) + "ᴇ" + exp).replace("-", "−")
    }

    private fun trimZeros(s: String) = if ('.' in s) s.trimEnd('0').trimEnd('.') else s

    private const val SUP = "⁰¹²³⁴⁵⁶⁷⁸⁹"
    fun superscript(n: Int): String = n.toString().map { c -> if (c == '-') '⁻' else SUP[c - '0'] }.joinToString("")
    private fun superToDigit(c: Char): Char = when (c) {
        '⁻' -> '-'
        else -> SUP.indexOf(c).takeIf { it >= 0 }?.let { '0' + it } ?: c
    }
}
