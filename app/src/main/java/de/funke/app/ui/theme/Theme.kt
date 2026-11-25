package de.funke.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import de.funke.app.R
import de.funke.app.ui.tr

/**
 * Inter: ruhige, gut lesbare Schrift mit klaren Ziffern. Für große Überschriften die Display-Variante,
 * die bei großen Größen enger und feiner gezeichnet ist. (Lizenz: SIL OFL, siehe licenses/Inter-OFL.txt)
 */
val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)
val InterDisplay = FontFamily(
    Font(R.font.interdisplay_medium, FontWeight.Medium),
    Font(R.font.interdisplay_semibold, FontWeight.SemiBold),
    Font(R.font.interdisplay_bold, FontWeight.Bold),
)

/**
 * Nunito (seit 15.2): runde Strichenden, weich und gut lesbar – die Schrift des Flat-Stils und die Textschrift
 * im Cartoon-Stil. Ziffern sind gleich breit (gut für Uhren). Lizenz: SIL OFL, siehe licenses/Nunito-OFL.txt.
 */
val Nunito = FontFamily(
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
)

/**
 * Fredoka (seit 15.2): rund und fröhlich wie ein Bilderbuch – die Überschriften im Cartoon-Stil.
 * Nur für Überschriften: ihre Ziffern sind unterschiedlich breit und Mathe-Zeichen fehlen, deshalb stehen Zahlen,
 * Aufgaben und Fließtext in Nunito. Lizenz: SIL OFL, siehe licenses/Fredoka-OFL.txt.
 */
val Fredoka = FontFamily(
    Font(R.font.fredoka_regular, FontWeight.Normal),
    Font(R.font.fredoka_medium, FontWeight.Medium),
    Font(R.font.fredoka_semibold, FontWeight.SemiBold),
    Font(R.font.fredoka_bold, FontWeight.Bold),
)

/**
 * Schriftart der App (seit 15.2). "Passend zum Stil": Glass und Modern in Inter, Flat in Nunito, Cartoon in
 * Fredoka mit Nunito. Wer mag, wählt unter Einstellungen → Aussehen eine andere.
 */
enum class AppFont(val id: String, private val de: String, private val en: String) {
    Auto("auto", "Passend zum Stil", "Match the style"),
    Inter("inter", "Inter", "Inter"),
    Nunito("nunito", "Nunito", "Nunito"),
    Fredoka("fredoka", "Fredoka", "Fredoka");

    val label: String get() = tr(de, en)

    /** Welche Schrift tatsächlich gilt. */
    fun resolve(style: UiStyle): AppFont = if (this != Auto) this else when (style) {
        UiStyle.Cartoon -> Fredoka
        UiStyle.Flat -> Nunito
        UiStyle.Modern, UiStyle.Glass -> Inter
    }

    companion object {
        fun byId(id: String?): AppFont = entries.firstOrNull { it.id == id } ?: Auto
    }
}

/**
 * Die Schriften eines Looks: [display] für große Überschriften, [title] für Titel, [text] für alles andere,
 * [numbers] für Zahlen und Uhren (immer mit gleich breiten Ziffern), [question] für Aufgaben (braucht Mathe-Zeichen).
 * [timer]: Gewicht der großen Countdown-Ziffern.
 */
class FontSet(
    val display: FontFamily,
    val title: FontFamily,
    val text: FontFamily,
    val numbers: FontFamily,
    val question: FontFamily,
    val timer: FontWeight,
    /** Fredoka wirkt schon in Halbfett kräftig: Fett wird dort zu Halbfett. */
    val heavy: FontWeight,
)

/** Familie für große Zahlen: bei Inter der Display-Schnitt, sonst die Zahlenschrift selbst. */
val FontSet.bigNumbers: FontFamily get() = if (numbers == Inter) InterDisplay else numbers

fun fontSetFor(font: AppFont): FontSet = when (font) {
    AppFont.Nunito -> FontSet(Nunito, Nunito, Nunito, Nunito, Nunito, FontWeight.Light, FontWeight.Bold)
    AppFont.Fredoka -> FontSet(Fredoka, Fredoka, Nunito, Nunito, Nunito, FontWeight.Normal, FontWeight.SemiBold)
    AppFont.Inter, AppFont.Auto -> FontSet(InterDisplay, Inter, Inter, Inter, InterDisplay, FontWeight.Light, FontWeight.Bold)
}

/**
 * Schrift-Merkmale für alle Stile: "tnum" = alle Ziffern gleich breit (Zahlen stehen sauber
 * untereinander und wackeln nicht, wenn sie sich ändern), "ss03" = rundere Anführungszeichen und Kommas.
 */
private const val Features = "tnum, ss03"

private fun style(
    family: FontFamily,
    weight: FontWeight,
    size: Int,
    line: Int,
    tracking: Double = 0.0,
) = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = tracking.sp,
    fontFeatureSettings = Features,
)

/**
 * Typografie: wenige Größen, mittlere und normale Gewichte, Zeilenhöhen im 4er-Raster.
 * Hierarchie entsteht über Größe und Farbe, nicht über dicke Schrift.
 */
val FunkeTypography = Typography(
    displayLarge = style(InterDisplay, FontWeight.Medium, 44, 52, -1.0),
    displayMedium = style(InterDisplay, FontWeight.Medium, 36, 44, -0.8),
    displaySmall = style(InterDisplay, FontWeight.Medium, 30, 36, -0.6),
    headlineLarge = style(InterDisplay, FontWeight.SemiBold, 30, 36, -0.6),
    headlineMedium = style(InterDisplay, FontWeight.SemiBold, 24, 32, -0.4),
    headlineSmall = style(InterDisplay, FontWeight.SemiBold, 20, 28, -0.2),
    titleLarge = style(Inter, FontWeight.SemiBold, 20, 28, -0.3),
    titleMedium = style(Inter, FontWeight.Medium, 16, 24, -0.1),
    titleSmall = style(Inter, FontWeight.Medium, 14, 20, -0.05),
    bodyLarge = style(Inter, FontWeight.Normal, 16, 24, -0.1),
    bodyMedium = style(Inter, FontWeight.Normal, 14, 20, -0.05),
    /** Kleine Begleittexte unter Titeln. */
    bodySmall = style(Inter, FontWeight.Normal, 13, 20, 0.0),
    labelLarge = style(Inter, FontWeight.Medium, 14, 20, 0.0),
    /** Abschnitts-Überschriften (normal geschrieben, leicht geöffnet). */
    labelMedium = style(Inter, FontWeight.Medium, 13, 16, 0.1),
    labelSmall = style(Inter, FontWeight.Medium, 11, 16, 0.3),
)

/** Große, leichte Ziffern für den Countdown (Schrift und Gewicht kommen aus dem Look, siehe [FontSet]). */
val TimerStyle: TextStyle
    @androidx.compose.runtime.Composable
    @androidx.compose.runtime.ReadOnlyComposable
    get() = LocalFontSet.current.let { f -> style(f.numbers, f.timer, 68, 72, -2.0) }

/** Ziffern im Zeit-Rad. */
val WheelStyle: TextStyle
    @androidx.compose.runtime.Composable
    @androidx.compose.runtime.ReadOnlyComposable
    get() = style(LocalFontSet.current.numbers, FontWeight.Medium, 30, 36, -0.4)

/** Große Kennzahlen (Minuten heute, Streak …). */
val NumberStyle: TextStyle
    @androidx.compose.runtime.Composable
    @androidx.compose.runtime.ReadOnlyComposable
    get() = style(LocalFontSet.current.bigNumbers, FontWeight.Medium, 32, 40, -0.8)

/** Schriften des aktuellen Looks (seit 15.2). */
val LocalFontSet = androidx.compose.runtime.staticCompositionLocalOf { fontSetFor(AppFont.Inter) }

/** Die Typografie eines Stils mit den Schriften eines Looks: Gewichte vom Stil, Familien vom [FontSet]. */
private fun Typography.withFonts(f: FontSet): Typography {
    fun TextStyle.on(family: FontFamily): TextStyle {
        val w = fontWeight ?: FontWeight.Normal
        // Fredoka: nie fetter als Halbfett; Inter Display hat kein Normal, dort bleibt es bei Inter
        val weight = if (w > f.heavy) f.heavy else w
        val fam = if (family == InterDisplay && weight < FontWeight.Medium) Inter else family
        return copy(fontFamily = fam, fontWeight = weight)
    }
    return Typography(
        displayLarge = displayLarge.on(f.display),
        displayMedium = displayMedium.on(f.display),
        displaySmall = displaySmall.on(f.display),
        headlineLarge = headlineLarge.on(f.display),
        headlineMedium = headlineMedium.on(f.display),
        headlineSmall = headlineSmall.on(f.display),
        titleLarge = titleLarge.on(f.title),
        titleMedium = titleMedium.on(f.text),
        titleSmall = titleSmall.on(f.text),
        bodyLarge = bodyLarge.on(f.text),
        bodyMedium = bodyMedium.on(f.text),
        bodySmall = bodySmall.on(f.text),
        labelLarge = labelLarge.on(f.text),
        labelMedium = labelMedium.on(f.text),
        labelSmall = labelSmall.on(f.text),
    )
}

/** Typografie für Stil und Schrift; eine Palette mit eigener Schrift (Terminal) hat Vorrang. */
private fun typographyFor(style: UiStyle, set: FontSet, palette: Palette): Typography {
    val base = when (style) {
        UiStyle.Cartoon -> CartoonTypography
        UiStyle.Flat -> FlatTypography
        UiStyle.Modern, UiStyle.Glass -> FunkeTypography
    }.withFonts(set)
    return palette.font?.let { base.withFont(it) } ?: base
}

/** Im Cartoon-Stil: kräftigere Gewichte, weniger eng gesetzt. */
private val CartoonTypography = FunkeTypography.copy(
    displayLarge = FunkeTypography.displayLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.6).sp),
    headlineLarge = FunkeTypography.headlineLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    headlineMedium = FunkeTypography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
    titleLarge = FunkeTypography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
    titleMedium = FunkeTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = FunkeTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = FunkeTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelMedium = FunkeTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
)

/** Flat: noch ruhiger, Überschriften eine Stufe leichter. */
private val FlatTypography = FunkeTypography.copy(
    headlineLarge = FunkeTypography.headlineLarge.copy(fontWeight = FontWeight.Medium),
    headlineMedium = FunkeTypography.headlineMedium.copy(fontWeight = FontWeight.Medium),
    titleLarge = FunkeTypography.titleLarge.copy(fontWeight = FontWeight.Medium),
)

@Composable
fun FunkeTheme(
    palette: Palette = Palettes.Obsidian,
    style: UiStyle = UiStyle.Modern,
    pattern: BackgroundPattern = BackgroundPattern.None,
    motion: MotionMode = MotionMode.Auto,
    font: AppFont = AppFont.Auto,
    content: @Composable () -> Unit,
) {
    val p = animatePalette(palette)
    val system = rememberSystemReducedMotion()
    val reduced = when (motion) {
        MotionMode.Auto -> system
        MotionMode.Full -> false
        MotionMode.Reduced -> true
    }
    val scheme = if (p.isDark) {
        darkColorScheme(
            primary = p.accent, onPrimary = p.onAccent,
            background = p.bg, onBackground = p.text,
            surface = p.surface, onSurface = p.text,
        )
    } else {
        lightColorScheme(
            primary = p.accent, onPrimary = p.onAccent,
            background = p.bg, onBackground = p.text,
            surface = p.surface, onSurface = p.text,
        )
    }

    SystemBarIcons(darkTheme = p.isDark)

    val fonts = fontSetFor(font.resolve(style)).let { set ->
        // Palette mit eigener Schrift (Terminal): auch Zahlen und Aufgaben darin
        p.font?.let { f -> FontSet(f, f, f, f, f, set.timer, set.heavy) } ?: set
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = typographyFor(style, fonts, p),
    ) {
        // Ohne das wäre Text standardmäßig schwarz.
        CompositionLocalProvider(
            LocalFontSet provides fonts,
            LocalPalette provides p,
            LocalUiStyle provides style,
            LocalBackgroundPattern provides pattern,
            LocalReducedMotion provides reduced,
            LocalContentColor provides p.text,
        ) {
            content()
        }
    }
}

/** Alle Schriftstile mit einer anderen Schriftart (z. B. Monospace beim Schema "Terminal"). */
private fun Typography.withFont(f: FontFamily) = Typography(
    displayLarge = displayLarge.copy(fontFamily = f),
    displayMedium = displayMedium.copy(fontFamily = f),
    displaySmall = displaySmall.copy(fontFamily = f),
    headlineLarge = headlineLarge.copy(fontFamily = f),
    headlineMedium = headlineMedium.copy(fontFamily = f),
    headlineSmall = headlineSmall.copy(fontFamily = f),
    titleLarge = titleLarge.copy(fontFamily = f),
    titleMedium = titleMedium.copy(fontFamily = f),
    titleSmall = titleSmall.copy(fontFamily = f),
    bodyLarge = bodyLarge.copy(fontFamily = f),
    bodyMedium = bodyMedium.copy(fontFamily = f),
    bodySmall = bodySmall.copy(fontFamily = f),
    labelLarge = labelLarge.copy(fontFamily = f),
    labelMedium = labelMedium.copy(fontFamily = f),
    labelSmall = labelSmall.copy(fontFamily = f),
)

/** Dunkle Symbole in der Statusleiste bei hellem Schema, helle bei dunklem. */
/**
 * Kleine Vorschau in einem anderen Look (die Design-Wahl im Intro): dieselben Bausteine, aber mit [palette],
 * [style] und [pattern]. Ändert weder die Systemleisten noch blendet es Farben über.
 */
@Composable
fun PreviewTheme(palette: Palette, style: UiStyle, pattern: BackgroundPattern, font: AppFont = AppFont.Auto, content: @Composable () -> Unit) {
    val fonts = fontSetFor(font.resolve(style)).let { set ->
        palette.font?.let { f -> FontSet(f, f, f, f, f, set.timer, set.heavy) } ?: set
    }
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = typographyFor(style, fonts, palette),
    ) {
        CompositionLocalProvider(
            LocalFontSet provides fonts,
            LocalPalette provides palette,
            LocalUiStyle provides style,
            LocalBackgroundPattern provides pattern,
            LocalContentColor provides palette.text,
        ) {
            content()
        }
    }
}

@Composable
private fun SystemBarIcons(darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
