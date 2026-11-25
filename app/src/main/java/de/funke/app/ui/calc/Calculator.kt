package de.funke.app.ui.calc

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import de.funke.app.ui.components.pressScale
import de.funke.app.ui.components.stateLayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.funke.app.R
import de.funke.app.ui.Lang
import de.funke.app.ui.LocalHaptics
import de.funke.app.ui.components.CloseButton
import de.funke.app.ui.components.LipButton
import de.funke.app.ui.components.LipTone
import de.funke.app.ui.components.bouncyClickable
import de.funke.app.ui.components.elevation
import de.funke.app.ui.theme.Elevation
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.Motion
import de.funke.app.ui.theme.Radius
import de.funke.app.ui.theme.Shapes
import de.funke.app.ui.theme.Space
import de.funke.app.ui.theme.reducedMotion
import de.funke.app.ui.tr
import org.json.JSONArray
import org.json.JSONObject
import de.funke.app.ui.components.Unfold

/*
 * Taschenrechner (seit 13.4): wissenschaftlich wie ein echter Schul-Rechner, aber ruhig.
 *
 *  - Oben die Anzeige: was man tippt (groß) und darunter schon das Ergebnis (leise), live beim Tippen.
 *  - Darüber Grad/Bogenmaß und der Verlauf.
 *  - Vier Reihen Funktionen (einklappbar): 2nd (Umkehrfunktionen), hyp (hyperbolisch), Klammern, mod,
 *    sin cos tan ln log, x² xʸ √ 1/x x!, π e EE |x| %. Mit "2nd" zeigen die Tasten ihre zweite Belegung.
 *  - Darunter das gewohnte Ziffernfeld mit C, ⌫, Ans und den vier Grundrechenarten.
 * Ein Fenster über allem: gleich auf der Startseite, während einer Fokuszeit und bei Aufgaben
 * (dort mit "Übernehmen" ins Antwortfeld). Im Buch liegt er seit 14.0 als Ablage unten ([CalculatorPanel]).
 */

/** Gemerkte Einstellungen und Verlauf (über Neustarts hinweg). */
private object CalcMemory {
    private const val K_MODE = "calc_mode"
    private const val K_SCI = "calc_sci"
    private const val K_HIST = "calc_history"
    const val MAX_HISTORY = 20

    fun mode(c: Context) = if (prefs(c).getString(K_MODE, "deg") == "rad") AngleMode.Rad else AngleMode.Deg
    fun setMode(c: Context, m: AngleMode) = prefs(c).edit().putString(K_MODE, if (m == AngleMode.Rad) "rad" else "deg").apply()
    fun sciOpen(c: Context) = prefs(c).getBoolean(K_SCI, true)
    fun setSciOpen(c: Context, on: Boolean) = prefs(c).edit().putBoolean(K_SCI, on).apply()

    fun history(c: Context): List<Pair<String, Double>> = runCatching {
        val a = JSONArray(prefs(c).getString(K_HIST, "[]"))
        List(a.length()) { i -> a.getJSONObject(i).let { it.getString("e") to it.getDouble("v") } }
    }.getOrDefault(emptyList())

    fun saveHistory(c: Context, h: List<Pair<String, Double>>) {
        val a = JSONArray()
        h.takeLast(MAX_HISTORY).forEach { (e, v) -> a.put(JSONObject().put("e", e).put("v", v)) }
        prefs(c).edit().putString(K_HIST, a.toString()).apply()
    }

    private fun prefs(c: Context) = c.getSharedPreferences("funke", Context.MODE_PRIVATE)
}

/** Zustand der Eingabe. Die Regeln fürs Tippen stehen hier, damit die Tasten nur noch "drücken" müssen. */
private class CalcInput(var ans: Double) {
    var expr by mutableStateOf("")
    /** Gerade "=" gedrückt: Eine Ziffer beginnt dann neu, ein Rechenzeichen rechnet weiter. */
    var justEvaluated by mutableStateOf(false)

    private fun lastIsValue(): Boolean {
        val c = expr.lastOrNull() ?: return false
        return c.isDigit() || c == '.' || c == ')' || c == 'π' || c == 'e' || c in "!²³%" || expr.endsWith("Ans") || expr.endsWith("⁻¹")
    }

    fun type(token: String, startsValue: Boolean) {
        if (justEvaluated) {
            // Nach "=": Ziffern und Werte fangen neu an, Rechenzeichen machen mit dem Ergebnis weiter
            if (startsValue) expr = ""
            justEvaluated = false
        }
        if (expr.length > 120) return
        expr += token
    }

    fun digit(d: String) = type(d, startsValue = true)

    fun dot() {
        if (justEvaluated) { expr = ""; justEvaluated = false }
        // Nur ein Komma pro Zahl
        val tail = expr.takeLastWhile { it.isDigit() || it == '.' }
        if ('.' in tail) return
        expr += if (tail.isEmpty()) "0." else "."
    }

    fun op(o: String) {
        justEvaluated = false
        if (expr.isEmpty()) {
            if (o == "−") expr = "−" else expr = CalcEngine.toExpr(ans) + o
            return
        }
        // Zwei Rechenzeichen hintereinander: das neue ersetzt das alte (außer "×−" für negative Zahlen)
        val last = expr.last()
        if (last in "+−×÷^" && !(o == "−" && last in "×÷^")) expr = expr.dropLast(1)
        expr += o
    }

    /** ±: macht die letzte Zahl negativ bzw. wieder positiv. */
    fun negate() {
        justEvaluated = false
        if (expr.isEmpty() || !lastIsValue()) { expr += "−"; return }
        val tail = expr.takeLastWhile { it.isDigit() || it == '.' || it == 'ᴇ' }
        val start = expr.length - tail.length
        expr = if (start >= 2 && expr.substring(start - 2, start) == "(−") expr.removeRange(start - 2, start)
        else expr.substring(0, start) + "(−" + tail
    }

    fun backspace() {
        justEvaluated = false
        val tokens = listOf("asinh(", "acosh(", "atanh(", "sinh(", "cosh(", "tanh(", "asin(", "acos(", "atan(",
            "sin(", "cos(", "tan(", "log2(", "log(", "ln(", "exp(", "abs(", "Ans", "mod", "ʸ√", "⁻¹", "10^(", "e^(")
        val t = tokens.firstOrNull { expr.endsWith(it) }
        expr = if (t != null) expr.dropLast(t.length) else expr.dropLast(1)
    }

    fun clear() {
        expr = ""
        justEvaluated = false
    }
}

private fun errorText(e: CalcError): String = when (e) {
    CalcError.Syntax -> tr("Ausdruck noch nicht fertig", "Expression not finished")
    CalcError.DivZero -> tr("Durch 0 teilen geht nicht", "Can't divide by 0")
    CalcError.Domain -> tr("Für diesen Wert nicht definiert", "Not defined for this value")
    CalcError.Overflow -> tr("Zu groß für den Rechner", "Too large for the calculator")
}

/** Anzeige eines Ausdrucks: Komma im Deutschen, sonst wie getippt. */
private fun shown(expr: String) = if (Lang.german) expr.replace('.', ',') else expr

/**
 * Der Rechner als Fenster über allem, von unten hereinfahrend.
 * [onUse] (optional): Knopf "Übernehmen" – gibt das Ergebnis zurück (z. B. ins Antwortfeld einer Aufgabe).
 */
@Composable
fun CalculatorWindow(onDismiss: () -> Unit, onUse: ((Double) -> Unit)? = null) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        val p = Funke.colors
        val still = reducedMotion
        val visible = remember { MutableTransitionState(false) }
        var after by remember { mutableStateOf<(() -> Unit)?>(null) }
        LaunchedEffect(Unit) { visible.targetState = true }
        LaunchedEffect(visible.isIdle, visible.currentState, after) {
            val a = after
            if (a != null && visible.isIdle && !visible.currentState) a()
        }
        fun close(then: () -> Unit = onDismiss) {
            if (after != null) return
            after = then
            visible.targetState = false
        }
        BackHandler { close() }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            AnimatedVisibility(visibleState = visible, enter = fadeIn(tween(Motion.Base)), exit = fadeOut(tween(Motion.Fast))) {
                Box(
                    Modifier.fillMaxSize().background(p.scrim)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { close() }
                )
            }
            AnimatedVisibility(
                visibleState = visible,
                enter = if (still) fadeIn(tween(Motion.Fast)) else slideInVertically(tween(Motion.Slow, easing = Motion.Drawer)) { it } + fadeIn(tween(Motion.Fast)),
                exit = if (still) fadeOut(tween(Motion.Fast)) else slideOutVertically(tween(Motion.Base, easing = Motion.EaseOut)) { it } + fadeOut(tween(Motion.Base)),
            ) {
                val shape = RoundedCornerShape(topStart = Radius.sheet, topEnd = Radius.sheet)
                Column(
                    Modifier
                        .statusBarsPadding()
                        .padding(top = Space.s8)
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .elevation(shape, Elevation.Level3)
                        .clip(shape)
                        .background(p.raised(Elevation.Level2))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                        .navigationBarsPadding()
                        .padding(start = Space.s4, end = Space.s4, top = Space.s4, bottom = Space.s4),
                ) {
                    CalculatorBody(
                        onClose = { close() },
                        onUse = onUse?.let { use -> { v: Double -> close { onDismiss(); use(v) } } },
                    )
                }
            }
        }
    }
}

/**
 * Der Rechner als Ablage unten (seit 14.0, im Buch): ohne Abdunklung. Seit 15.5 steht die Seite darüber groß und
 * lässt sich verschieben und zoomen – deshalb hat der Rechner hier dieselben großen Tasten wie sonst.
 * Mit dem Pfeil klappt er zur schmalen Leiste zusammen (nur Anzeige), Antippen der Leiste klappt ihn wieder auf.
 * Hintergrund und Abstand außen bestimmt der Aufrufer.
 */
@Composable
fun CalculatorPanel(onClose: () -> Unit, collapsed: Boolean, onCollapse: (Boolean) -> Unit) {
    CalculatorBody(onClose = onClose, onUse = null, collapsed = collapsed, onCollapse = onCollapse)
}

/**
 * Inhalt des Rechners (seit 14.0 ruhiger): flache Tasten in einem 8-dp-Raster, nur "=" in der Akzentfarbe,
 * Rechenzeichen in Akzentschrift. Oben links Grad/Rad als kleiner Umschalter, rechts Verlauf, Funktionen, Schließen.
 * Die Anzeige steht frei, ohne Kasten: was man tippt groß, das Ergebnis leise darunter (live).
 */
@Composable
private fun CalculatorBody(
    onClose: () -> Unit,
    onUse: ((Double) -> Unit)?,
    collapsed: Boolean = false,
    onCollapse: ((Boolean) -> Unit)? = null,
) {
    val p = Funke.colors
    val context = LocalContext.current
    val haptics = LocalHaptics.current
    var mode by remember { mutableStateOf(CalcMemory.mode(context)) }
    var sci by remember { mutableStateOf(CalcMemory.sciOpen(context)) }
    var second by remember { mutableStateOf(false) }
    var hyp by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<Pair<String, Double>>().apply { addAll(CalcMemory.history(context)) } }
    val input = remember { CalcInput(history.lastOrNull()?.second ?: 0.0) }
    val result = remember(input.expr, mode) { if (input.expr.isBlank()) null else CalcEngine.eval(input.expr, mode, input.ans) }

    fun press(block: () -> Unit) {
        block()
        // 2nd gilt für genau eine Taste (wie beim echten Rechner)
        second = false
    }
    fun equals() {
        val r = result
        if (r is CalcResult.Ok) {
            history.add(input.expr to r.value)
            while (history.size > CalcMemory.MAX_HISTORY) history.removeAt(0)
            CalcMemory.saveHistory(context, history)
            input.ans = r.value
            input.expr = CalcEngine.toExpr(r.value)
            input.justEvaluated = true
        }
    }

    // ----- Kopf: Grad/Rad links, rechts Verlauf, Funktionen, (Einklappen), Schließen -----
    Row(verticalAlignment = Alignment.CenterVertically) {
        AngleToggle(mode) { m ->
            mode = m
            CalcMemory.setMode(context, m)
        }
        Spacer(Modifier.weight(1f))
        if (!collapsed) {
            HeaderIcon(R.drawable.ic_list, tr("Verlauf", "History"), active = showHistory) { haptics?.tick(); showHistory = !showHistory }
            Spacer(Modifier.width(Space.s1))
            HeaderIcon(R.drawable.ic_formula, if (sci) tr("Funktionen ausblenden", "Hide functions") else tr("Funktionen zeigen", "Show functions"), active = sci) {
                haptics?.tick()
                sci = !sci
                CalcMemory.setSciOpen(context, sci)
            }
            Spacer(Modifier.width(Space.s1))
        }
        if (onCollapse != null) {
            HeaderIcon(
                R.drawable.ic_chevron_left,
                if (collapsed) tr("Rechner aufklappen", "Expand calculator") else tr("Rechner einklappen", "Collapse calculator"),
                active = false,
                rotate = if (collapsed) 90f else -90f,
            ) { haptics?.tick(); onCollapse(!collapsed) }
            Spacer(Modifier.width(Space.s1))
        }
        CloseButton(onClick = onClose)
    }
    Spacer(Modifier.height(Space.s2))

    // ----- Anzeige: frei, rechtsbündig. Eingeklappt: Antippen klappt auf -----
    Column(
        Modifier
            .fillMaxWidth()
            .then(
                if (collapsed && onCollapse != null) Modifier.clip(Shapes.md).clickable { onCollapse(false) } else Modifier
            )
            .padding(horizontal = Space.s2, vertical = Space.s2),
        horizontalAlignment = Alignment.End,
    ) {
        val text = shown(input.expr).ifEmpty { "0" }
        // Lange Ausdrücke werden kleiner statt abgeschnitten
        val size = when {
            collapsed -> 22.sp
            text.length > 34 -> 18.sp
            text.length > 22 -> 22.sp
            else -> 32.sp
        }
        Text(
            text,
            style = MaterialTheme.typography.displaySmall.copy(fontSize = size, lineHeight = size * 1.2f, fontFeatureSettings = "tnum, ss03"),
            color = p.text,
            textAlign = TextAlign.End,
            maxLines = if (collapsed) 1 else 3,
            modifier = Modifier.semantics { contentDescription = tr("Eingabe: ", "Input: ") + text },
        )
        val preview = when (val r = result) {
            null -> " "
            is CalcResult.Ok -> if (input.justEvaluated) " " else "= " + CalcEngine.format(r.value, Lang.german).replace("-", "−")
            is CalcResult.Error -> if (r.reason == CalcError.Syntax) " " else errorText(r.reason)
        }
        Text(
            preview,
            style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum, ss03"),
            color = if (result is CalcResult.Error) p.danger else p.textSecondary,
            maxLines = 1,
        )
    }
    if (collapsed) return

    // ----- Verlauf -----
    Unfold(showHistory, enter = expandVertically(tween(Motion.Base, easing = Motion.EaseOut), clip = false) + fadeIn(tween(Motion.Base)), exit = shrinkVertically(tween(Motion.Fast), clip = false) + fadeOut(tween(Motion.Fast))) {
        Column(Modifier.padding(top = Space.s2).heightIn(max = 180.dp).verticalScroll(rememberScrollState())) {
            if (history.isEmpty()) {
                Text(tr("Noch nichts gerechnet.", "Nothing calculated yet."), style = MaterialTheme.typography.bodySmall, color = p.textTertiary, modifier = Modifier.padding(Space.s2))
            }
            history.asReversed().forEach { (e, v) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .bouncyClickable(shape = Shapes.md, pressedScale = 0.99f) {
                            input.type(CalcEngine.toExpr(v), startsValue = true)
                            showHistory = false
                        }
                        .padding(horizontal = Space.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(shown(e), style = MaterialTheme.typography.bodyMedium, color = p.textSecondary, maxLines = 1, modifier = Modifier.weight(1f))
                    Text("= " + CalcEngine.format(v, Lang.german).replace("-", "−"), style = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = "tnum, ss03"), color = p.text, maxLines = 1)
                }
            }
            if (history.isNotEmpty()) {
                Text(
                    tr("Verlauf löschen", "Clear history"),
                    style = MaterialTheme.typography.labelLarge,
                    color = p.textTertiary,
                    modifier = Modifier.align(Alignment.End).clip(Shapes.pill).clickable { history.clear(); CalcMemory.saveHistory(context, history) }.padding(Space.s2),
                )
            }
        }
    }
    Spacer(Modifier.height(Space.s3))

    // ----- Funktionen: leise Texttasten, eingerastete (2nd, hyp) leicht getönt -----
    Unfold(sci, enter = expandVertically(tween(Motion.Medium, easing = Motion.EaseOut), clip = false) + fadeIn(tween(Motion.Base)), exit = shrinkVertically(tween(Motion.Base, easing = Motion.EaseOut), clip = false) + fadeOut(tween(Motion.Fast))) {
        Column(verticalArrangement = Arrangement.spacedBy(Space.s1), modifier = Modifier.padding(bottom = Space.s2)) {
            fun fn(name: String) = (if (second) "a" else "") + name + (if (hyp) "h" else "") + "("
            SciRow {
                Fn("2nd", Modifier.weight(1f), active = second, description = tr("Zweite Belegung", "Second function")) { second = !second }
                Fn("hyp", Modifier.weight(1f), active = hyp, description = tr("Hyperbolisch", "Hyperbolic")) { hyp = !hyp }
                Fn("(", Modifier.weight(1f)) { press { input.type("(", startsValue = true) } }
                Fn(")", Modifier.weight(1f)) { press { input.type(")", startsValue = false) } }
                Fn("mod", Modifier.weight(1f), description = tr("Rest beim Teilen", "Remainder")) { press { input.op("mod") } }
            }
            SciRow {
                val inv = if (second) "⁻¹" else ""
                val h = if (hyp) "h" else ""
                Fn("sin$h$inv", Modifier.weight(1f)) { press { input.type(fn("sin"), startsValue = true) } }
                Fn("cos$h$inv", Modifier.weight(1f)) { press { input.type(fn("cos"), startsValue = true) } }
                Fn("tan$h$inv", Modifier.weight(1f)) { press { input.type(fn("tan"), startsValue = true) } }
                Fn(if (second) "eˣ" else "ln", Modifier.weight(1f)) { press { input.type(if (second) "e^(" else "ln(", startsValue = true) } }
                Fn(if (second) "10ˣ" else "log", Modifier.weight(1f)) { press { input.type(if (second) "10^(" else "log(", startsValue = true) } }
            }
            SciRow {
                Fn(if (second) "x³" else "x²", Modifier.weight(1f)) { press { input.type(if (second) "³" else "²", startsValue = false) } }
                Fn(if (second) "ʸ√x" else "xʸ", Modifier.weight(1f)) { press { if (second) input.op("ʸ√") else input.op("^") } }
                Fn(if (second) "∛" else "√", Modifier.weight(1f)) { press { input.type(if (second) "∛(" else "√(", startsValue = true) } }
                Fn(if (second) "log₂" else "1/x", Modifier.weight(1f)) { press { if (second) input.type("log2(", startsValue = true) else input.type("⁻¹", startsValue = false) } }
                Fn(if (second) "nCr" else "x!", Modifier.weight(1f), description = if (second) tr("Kombinationen", "Combinations") else tr("Fakultät", "Factorial")) {
                    press { if (second) input.op("C") else input.type("!", startsValue = false) }
                }
            }
            SciRow {
                Fn("π", Modifier.weight(1f)) { press { input.type("π", startsValue = true) } }
                Fn("e", Modifier.weight(1f)) { press { input.type("e", startsValue = true) } }
                Fn("EE", Modifier.weight(1f), description = tr("mal zehn hoch", "times ten to the power")) { press { if (input.expr.isEmpty() || input.justEvaluated) input.digit("1"); input.type("ᴇ", startsValue = false) } }
                Fn(if (second) "nPr" else "|x|", Modifier.weight(1f), description = if (second) tr("Anordnungen", "Permutations") else tr("Betrag", "Absolute value")) {
                    press { if (second) input.op("P") else input.type("abs(", startsValue = true) }
                }
                Fn("%", Modifier.weight(1f), description = tr("Prozent", "Percent")) { press { input.type("%", startsValue = false) } }
            }
        }
    }

    // ----- Ziffernfeld -----
    val sep = if (Lang.german) "," else "."
    Column(verticalArrangement = Arrangement.spacedBy(Space.s2)) {
        KeyRow {
            Key("C", Modifier.weight(1f), kind = KeyKind.Clear, description = tr("Alles löschen", "Clear all")) { press { input.clear() } }
            Key("⌫", Modifier.weight(1f), kind = KeyKind.Fn, description = tr("Löschen", "Delete")) { press { input.backspace() } }
            Key("Ans", Modifier.weight(1f), kind = KeyKind.Fn, description = tr("Letztes Ergebnis", "Last answer")) { press { input.type("Ans", startsValue = true) } }
            Key("÷", Modifier.weight(1f), kind = KeyKind.Op) { press { input.op("÷") } }
        }
        listOf(listOf("7", "8", "9") to "×", listOf("4", "5", "6") to "−", listOf("1", "2", "3") to "+").forEach { (digits, o) ->
            KeyRow {
                digits.forEach { d -> Key(d, Modifier.weight(1f)) { press { input.digit(d) } } }
                Key(o, Modifier.weight(1f), kind = KeyKind.Op) { press { input.op(o) } }
            }
        }
        KeyRow {
            Key("±", Modifier.weight(1f), kind = KeyKind.Fn, description = tr("Vorzeichen", "Sign")) { press { input.negate() } }
            Key("0", Modifier.weight(1f)) { press { input.digit("0") } }
            Key(sep, Modifier.weight(1f)) { press { input.dot() } }
            Key("=", Modifier.weight(1f), kind = KeyKind.Equals) { press { equals() } }
        }
    }
    if (onUse != null) {
        val r = result
        val value = when {
            r is CalcResult.Ok -> r.value
            input.justEvaluated -> input.ans
            else -> null
        }
        Spacer(Modifier.height(Space.s3))
        LipButton(
            if (value != null) tr("${CalcEngine.format(value, true).replace("-", "−")} übernehmen", "Use ${CalcEngine.format(value, false).replace("-", "−")}") else tr("Übernehmen", "Use"),
            onClick = { value?.let { v -> haptics?.click(); onUse(v) } },
            tone = LipTone.Accent,
            enabled = value != null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private enum class KeyKind { Digit, Op, Fn, Clear, Equals }

@Composable
private fun KeyRow(content: @Composable RowScope.() -> Unit) =
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s2), content = content)

@Composable
private fun SciRow(content: @Composable RowScope.() -> Unit) =
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Space.s1), content = content)

/**
 * Taste des Ziffernfelds: flach und ruhig. Ziffern auf leiser Fläche, Rechenzeichen in Akzentschrift auf
 * Akzent-Hauch, nur "=" voll in der Akzentfarbe. Drücken: 0,95 in 120 ms und eine leichte Aufhellung, ein Tick.
 */
@Composable
private fun Key(label: String, modifier: Modifier, kind: KeyKind = KeyKind.Digit, description: String? = null, onClick: () -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val interaction = remember { MutableInteractionSource() }
    val bg = when (kind) {
        KeyKind.Equals -> p.accent
        KeyKind.Op -> p.accentSoft
        KeyKind.Digit -> p.track
        KeyKind.Fn, KeyKind.Clear -> p.track.copy(alpha = p.track.alpha * 0.5f)
    }
    Box(
        modifier
            .height(52.dp)
            .pressScale(interaction, 0.95f)
            .clip(Shapes.md)
            .background(bg)
            .stateLayer(interaction, Shapes.md)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button) {
                haptics?.tick()
                onClick()
            }
            .semantics { contentDescription = description ?: label },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = (if (kind == KeyKind.Digit) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium) else MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium))
                .copy(fontFeatureSettings = "tnum, ss03"),
            color = when (kind) {
                KeyKind.Equals -> p.onAccent
                KeyKind.Op -> p.accent
                KeyKind.Clear -> p.danger
                KeyKind.Fn -> p.textSecondary
                KeyKind.Digit -> p.text
            },
        )
    }
}

/** Funktionstaste: nur Schrift, kleiner als die Ziffern, damit das Feld ruhig bleibt. [active] = eingerastet (2nd, hyp). */
@Composable
private fun Fn(label: String, modifier: Modifier, active: Boolean = false, description: String? = null, onClick: () -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val interaction = remember { MutableInteractionSource() }
    val bg by animateColorAsState(if (active) p.accentSoft else p.track.copy(alpha = 0f), Motion.fade(Motion.Fast), label = "fn")
    Box(
        modifier
            .height(40.dp)
            .pressScale(interaction, 0.94f)
            .clip(Shapes.sm)
            .background(bg)
            .stateLayer(interaction, Shapes.sm)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button) {
                haptics?.tick()
                onClick()
            }
            .semantics {
                contentDescription = description ?: label
                if (active) stateDescription = tr("An", "On")
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum, ss03"), color = if (active) p.accent else p.textSecondary, maxLines = 1)
    }
}

/** "Grad | Rad" als kleiner Umschalter: die gewählte Hälfte trägt eine leise Fläche, die gleitet. */
@Composable
private fun AngleToggle(mode: AngleMode, onChange: (AngleMode) -> Unit) {
    val p = Funke.colors
    val haptics = LocalHaptics.current
    val x by animateFloatAsState(if (mode == AngleMode.Deg) 0f else 1f, Motion.spec(Motion.Base), label = "angle")
    Box(
        Modifier
            .height(36.dp)
            .width(116.dp)
            .clip(Shapes.pill)
            .background(p.track)
            .semantics { contentDescription = tr("Winkel in Grad oder Bogenmaß", "Angles in degrees or radians") },
    ) {
        Box(
            Modifier
                .padding(Space.s1)
                .fillMaxHeight()
                .width(54.dp)
                .graphicsLayer { translationX = x * 54.dp.toPx() }
                .clip(Shapes.pill)
                .background(p.raised(Elevation.Level3))
        )
        Row(Modifier.fillMaxSize()) {
            listOf(AngleMode.Deg to tr("Grad", "Deg"), AngleMode.Rad to tr("Rad", "Rad")).forEach { (m, label) ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(Shapes.pill)
                        .clickable(role = Role.RadioButton) {
                            if (m != mode) { haptics?.tick(); onChange(m) }
                        }
                        .semantics { stateDescription = if (m == mode) tr("Ausgewählt", "Selected") else "" },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge, color = if (m == mode) p.text else p.textTertiary)
                }
            }
        }
    }
}

@Composable
private fun HeaderIcon(icon: Int, description: String, active: Boolean, rotate: Float = 0f, onClick: () -> Unit) {
    val p = Funke.colors
    Box(
        Modifier
            .size(40.dp)
            .bouncyClickable(shape = CircleShape, pressedScale = 0.92f, onClick = onClick)
            .clip(CircleShape)
            .background(if (active) p.accentSoft else Color.Transparent)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = if (active) p.accent else p.textSecondary, modifier = Modifier.size(18.dp).graphicsLayer { rotationZ = rotate })
    }
}
