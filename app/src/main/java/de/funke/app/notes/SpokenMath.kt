package de.funke.app.notes

/**
 * Macht aus gesprochenen Formeln Zeichen: "x Quadrat" → x², "Wurzel aus 2" → √2,
 * "Integral von 0 bis 1" → ∫₀¹, "a mal b" → a · b, "Pi" → π, "30 Grad" → 30° …
 * Deutsch und Englisch. Bewusst vorsichtig: "mal", "plus", "gleich" werden nur zwischen
 * Zahlen oder einzelnen Buchstaben umgewandelt, damit normale Sätze ("einmal", "gleich danach")
 * unverändert bleiben.
 */
object SpokenMath {
    private const val SUP = "⁰¹²³⁴⁵⁶⁷⁸⁹"
    private const val SUB = "₀₁₂₃₄₅₆₇₈₉"

    private fun sup(s: String): String? {
        if (s == "n") return "ⁿ"
        val neg = s.startsWith("-") || s.startsWith("−")
        val digits = s.trimStart('-', '−')
        if (digits.isEmpty() || !digits.all { it.isDigit() }) return null
        return (if (neg) "⁻" else "") + digits.map { SUP[it - '0'] }.joinToString("")
    }

    private fun sub(s: String): String? =
        if (s.isNotEmpty() && s.all { it.isDigit() }) s.map { SUB[it - '0'] }.joinToString("") else null

    /** Zahl, einzelner Buchstabe, Klammer zu, Hochzahl oder π am Ende des linken Teils. */
    private const val L = "(?<=(?:\\d|(?<![\\p{L}])[\\p{L}]|[)²³ⁿπ°%]))"
    /** Zahl, einzelner Buchstabe, Klammer auf, Wurzel, π oder Minus am Anfang des rechten Teils. */
    private const val R = "(?=(?:\\d|[\\p{L}](?![\\p{L}])|[(√∛π−-]))"

    private val numberWordsDe = mapOf(
        "null" to "0", "eins" to "1", "zwei" to "2", "drei" to "3", "vier" to "4", "fünf" to "5",
        "sechs" to "6", "sieben" to "7", "acht" to "8", "neun" to "9", "zehn" to "10", "n" to "n",
    )
    private val numberWordsEn = mapOf(
        "zero" to "0", "one" to "1", "two" to "2", "three" to "3", "four" to "4", "five" to "5",
        "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9", "ten" to "10", "n" to "n",
    )

    private val greek = listOf(
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "Δ", "epsilon" to "ε", "lambda" to "λ",
        "omega" to "ω", "rho" to "ρ", "sigma" to "σ", "phi" to "φ", "theta" to "θ", "pi" to "π",
    )

    fun convert(input: String, german: Boolean): String {
        var t = input
        // Englisch: "I" und "a" sind Wörter, keine Variablen
        val L = if (german) L else L.replace("(?<![\\p{L}])[\\p{L}]|", "(?<![\\p{L}])[\\p{L}&&[^IaA]]|")
        val words = if (german) numberWordsDe else numberWordsEn
        fun num(w: String) = words[w.lowercase()] ?: w

        // Griechische Buchstaben (nur als ganzes Wort)
        for ((w, g) in greek) t = t.replace(Regex("(?i)\\b$w\\b"), g)

        if (german) {
            // Klammern
            t = t.replace(Regex("(?i)\\bklammer auf\\b\\s*"), "(").replace(Regex("(?i)\\s*\\bklammer zu\\b"), ")")
            // Funktionen: "f Strich von x" → f′(x), "f von x" → f(x)
            t = t.replace(Regex("(?i)\\b([fgh]) strich von ([a-z])\\b")) { m -> "${m.groupValues[1].lowercase()}′(${m.groupValues[2]})" }
            t = t.replace(Regex("\\b([fgh]) von ([a-z])\\b"), "$1($2)")
            // Integral von a bis b
            t = t.replace(Regex("(?i)\\b(?:das )?integral von (\\S+) bis (\\S+)")) { m ->
                val a = num(m.groupValues[1]); val b = num(m.groupValues[2])
                val sa = sub(a); val sb = sup(b)
                if (sa != null && sb != null) "∫$sa$sb" else "∫[$a; $b]"
            }
            t = t.replace(Regex("(?i)\\bintegral\\b"), "∫")
            // Wurzeln
            t = t.replace(Regex("(?i)\\bdritte wurzel (?:aus |von )?"), "∛")
            t = t.replace(Regex("(?i)\\b(?:quadrat)?wurzel (?:aus |von )?"), "√")
            // Potenzen
            t = t.replace(Regex("(?i)$L\\s+(?:zum )?quadrat\\b"), "²")
            t = t.replace(Regex("(?i)$L\\s+hoch (minus |−|-)?(\\d+|n|[a-z]|null|eins|zwei|drei|vier|fünf|sechs|sieben|acht|neun|zehn)\\b")) { m ->
                val e = (if (m.groupValues[1].isNotBlank()) "-" else "") + num(m.groupValues[2])
                sup(e) ?: "^$e"
            }
            // Vergleiche (vor "gleich")
            t = t.replace(Regex("(?i)\\s+kleiner gleich\\s+"), " ≤ ").replace(Regex("(?i)\\s+größer gleich\\s+"), " ≥ ")
            t = t.replace(Regex("(?i)\\s+ungefähr gleich\\s+"), " ≈ ").replace(Regex("(?i)\\s+ungleich\\s+"), " ≠ ")
            t = t.replace(Regex("(?i)$L\\s+kleiner als\\s+$R"), " < ").replace(Regex("(?i)$L\\s+größer als\\s+$R"), " > ")
            t = t.replace(Regex("(?i)$L\\s+(?:ist )?gleich\\s+$R"), " = ")
            t = t.replace(Regex("(?i)\\bungefähr (?=\\d)"), "≈ ")
            // Rechenzeichen
            t = t.replace(Regex("(?i)$L\\s+geteilt durch\\s+$R"), " / ").replace(Regex("(?i)$L\\s+durch\\s+$R"), " / ")
            t = t.replace(Regex("(?i)$L\\s+mal\\s+$R"), " · ")
            t = t.replace(Regex("(?i)$L\\s+plus\\s+$R"), " + ")
            t = t.replace(Regex("(?i)$L\\s+minus\\s+$R"), " − ")
            // Einheiten und Sonstiges
            t = t.replace(Regex("(\\d)\\s+grad celsius\\b", RegexOption.IGNORE_CASE), "$1 °C")
            t = t.replace(Regex("(\\d)\\s+grad\\b", RegexOption.IGNORE_CASE), "$1°")
            t = t.replace(Regex("(\\d)\\s+prozent\\b", RegexOption.IGNORE_CASE), "$1 %")
            t = t.replace(Regex("(?i)\\bunendlich\\b"), "∞")
        } else {
            t = t.replace(Regex("(?i)\\bopen (?:bracket|parenthesis)\\b\\s*"), "(").replace(Regex("(?i)\\s*\\bclose (?:bracket|parenthesis)\\b"), ")")
            t = t.replace(Regex("\\b([fgh]) prime of ([a-z])\\b"), "$1′($2)")
            t = t.replace(Regex("\\b([fgh]) of ([a-z])\\b"), "$1($2)")
            t = t.replace(Regex("(?i)\\b(?:the )?integral from (\\S+) to (\\S+)(?: of)?")) { m ->
                val a = num(m.groupValues[1]); val b = num(m.groupValues[2])
                val sa = sub(a); val sb = sup(b)
                if (sa != null && sb != null) "∫$sa$sb" else "∫[$a; $b]"
            }
            t = t.replace(Regex("(?i)\\bintegral of\\s+"), "∫ ").replace(Regex("(?i)\\bintegral\\b"), "∫")
            t = t.replace(Regex("(?i)\\bcube root of\\s+"), "∛")
            t = t.replace(Regex("(?i)\\b(?:the )?square root of\\s+"), "√").replace(Regex("(?i)\\broot of\\s+"), "√")
            t = t.replace(Regex("(?i)$L\\s+squared\\b"), "²").replace(Regex("(?i)$L\\s+cubed\\b"), "³")
            t = t.replace(Regex("(?i)$L\\s+to the power of (minus |−|-)?(\\d+|n|[a-z]|zero|one|two|three|four|five|six|seven|eight|nine|ten)\\b")) { m ->
                val e = (if (m.groupValues[1].isNotBlank()) "-" else "") + num(m.groupValues[2])
                sup(e) ?: "^$e"
            }
            t = t.replace(Regex("(?i)\\s+less than or equal to\\s+"), " ≤ ").replace(Regex("(?i)\\s+greater than or equal to\\s+"), " ≥ ")
            t = t.replace(Regex("(?i)\\s+(?:is )?approximately(?: equal to)?\\s+"), " ≈ ").replace(Regex("(?i)\\s+not equal to\\s+"), " ≠ ")
            t = t.replace(Regex("(?i)$L\\s+(?:is )?less than\\s+$R"), " < ").replace(Regex("(?i)$L\\s+(?:is )?greater than\\s+$R"), " > ")
            t = t.replace(Regex("(?i)$L\\s+(?:equals|is equal to)\\s+$R"), " = ")
            t = t.replace(Regex("(?i)$L\\s+divided by\\s+$R"), " / ").replace(Regex("(?i)$L\\s+over\\s+$R"), " / ")
            t = t.replace(Regex("(?i)$L\\s+times\\s+$R"), " · ")
            t = t.replace(Regex("(?i)$L\\s+plus\\s+$R"), " + ")
            t = t.replace(Regex("(?i)$L\\s+minus\\s+$R"), " − ")
            t = t.replace(Regex("(\\d)\\s+degrees celsius\\b", RegexOption.IGNORE_CASE), "$1 °C")
            t = t.replace(Regex("(\\d)\\s+degrees\\b", RegexOption.IGNORE_CASE), "$1°")
            t = t.replace(Regex("(\\d)\\s+percent\\b", RegexOption.IGNORE_CASE), "$1 %")
            t = t.replace(Regex("(?i)\\binfinity\\b"), "∞")
        }
        // "2 x" → 2x (typische Variablen)
        t = t.replace(Regex("(\\d) ([xyztn])(?![\\p{L}])"), "$1$2")
        // Δ x → Δx
        t = t.replace(Regex("Δ\\s+([a-zA-Z])\\b"), "Δ$1")
        // "minus 3" am Anfang oder nach einem Zeichen → −3
        t = t.replace(Regex("(?i)(^|[=(≈<>≤≥ ])\\s*(?:minus|negative) (?=\\d)"), "$1−")
        return t.replace(Regex(" {2,}"), " ").trim()
    }
}
