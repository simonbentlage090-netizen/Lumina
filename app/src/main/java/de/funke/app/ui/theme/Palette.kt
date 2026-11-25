package de.funke.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import de.funke.app.ui.tr

/**
 * Ein Farbschema. Alle Farben der App kommen von hier, deshalb lässt sich
 * das ganze Aussehen mit einem Fingertipp wechseln.
 */
@Immutable
data class Palette(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val bg: Color,            // Grundton des Hintergrunds
    val bgMid: Color,         // etwas hellerer Ton in der Mitte des Verlaufs
    val surface: Color,       // Flächen wie Menüs und Navigationsleiste
    val glowA: Color,         // weiche Lichtflecken im Hintergrund
    val glowB: Color,
    val accent: Color,        // Hauptakzent
    val accentAlt: Color,     // zweiter Ton für Verläufe
    val onAccent: Color,      // Schrift auf dem Akzent
    val text: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val glass: Color,         // Basis für halbtransparente "Glas"-Flächen (weiß bei dunkel, schwarz bei hell)
    val danger: Color,
    val ink: Color = Color(0xFF1C1A24), // Konturfarbe im Cartoon-Stil
    val font: FontFamily? = null,       // eigene Schrift für dieses Schema (sonst die Standardschrift)
) {
    /**
     * Verlauf für Akzent-Flächen. Bewusst ruhig: derselbe Farbton, nur ein Hauch heller oben links,
     * statt zweier verschiedener Farben (sonst wirkt es bunt statt hochwertig).
     */
    val accentBrush: Brush get() = Brush.linearGradient(listOf(lerpColor(accent, accentAlt, 0.22f), accent))
    fun glassAlpha(alpha: Float): Color = glass.copy(alpha = alpha)

    // ----- abgeleitete Tokens (für alle Schemata gleich berechnet) -----

    /** Erfolg/Erledigt: gedämpftes Grün mit genug Kontrast auf dem Grund. */
    val success: Color get() = if (isDark) SuccessDark else SuccessLight
    /** Warnung (z. B. Limit fast erreicht). */
    val warning: Color get() = if (isDark) WarningDark else WarningLight
    /** feine Trennlinie zwischen Zeilen */
    val divider: Color get() = glass.copy(alpha = if (isDark) 0.07f else 0.06f)
    /** Abdunklung hinter Dialogen */
    val scrim: Color get() = Color.Black.copy(alpha = if (isDark) 0.62f else 0.36f)
    /** zarte Akzent-Fläche für "ausgewählt"/"aktiv" */
    val accentSoft: Color get() = accent.copy(alpha = if (isDark) 0.14f else 0.10f)
    /** Spur von Reglern, Ringen und Balken */
    val track: Color get() = glass.copy(alpha = if (isDark) 0.08f else 0.06f)

    /**
     * Fremdfarbe (Fach, App-Gruppe …) gedämpft: ein Teil Grau aus dem Schema wird beigemischt,
     * damit alle diese Farben zueinander und zum Schema passen statt bunt nebeneinanderzustehen.
     */
    fun tame(c: Color): Color = lerpColor(c, textSecondary, if (isDark) 0.28f else 0.22f)
    fun tame(argb: Long): Color = tame(Color(argb))

    /** Fläche einer Karte auf Höhe [level] (dunkel heller, hell immer die Flächenfarbe). */
    fun raised(level: Elevation): Color =
        if (isDark) lerpColor(surface, Color.White, level.lift) else surface

    /** Name in der gewählten Sprache. */
    val displayName: String get() = tr(name, EnglishNames[id] ?: name)
}

/** Englische Namen der Farbschemata (die deutschen stehen direkt im Schema). */
private val EnglishNames = mapOf(
    "emerald" to "Emerald", "ruby" to "Ruby", "amber" to "Amber", "lagoon" to "Lagoon",
    "midnight" to "Midnight", "forest" to "Forest light",
    "rose" to "Sakura", "ember" to "Ember",
    "graphite" to "Graphite", "paper" to "Paper", "neon_green" to "Neon green", "neon_cyan" to "Neon cyan",
    "neon_pink" to "Neon pink", "neon_orange" to "Neon orange",
    "chalk" to "Chalk", "peach" to "Peach", "sky" to "Sky", "sand" to "Sand", "blush" to "Rosé", "toon_sunny" to "Sunny", "toon_sky" to "Sky",
    "toon_meadow" to "Meadow", "toon_candy" to "Candy", "toon_peach" to "Peach", "toon_lavender" to "Lavender",
    "toon_mint" to "Mint", "toon_dusk" to "Dusk", "toon_night" to "Night sky", "toon_cocoa" to "Cocoa",
    "toon_galaxy" to "Galaxy", "toon_neon" to "Neon comic", "toon_deep" to "Deep sea",
    "indigo_dark" to "Indigo", "indigo_light" to "Indigo",
    "terminal_dark" to "Terminal", "terminal_light" to "Terminal", "cosmos_dark" to "Cosmos", "cosmos_light" to "Cosmos",
    "sage_dark" to "Sage garden", "sage_light" to "Sage garden",
)

object Palettes {
    private fun dark(
        id: String,
        name: String,
        bg: Long,
        mid: Long,
        surface: Long,
        accent: Long,
        alt: Long,
        onAccent: Long = 0xFF0A0D1F,
    ) = Palette(
        id = id, name = name, isDark = true,
        bg = Color(bg), bgMid = Color(mid), surface = Color(surface),
        glowA = Color(accent), glowB = Color(alt),
        accent = Color(accent), accentAlt = Color(alt), onAccent = Color(onAccent),
        text = Color(0xFFF3F3F8), textSecondary = Color(0xFFB4B6CE), textTertiary = Color(0xFF7C7F9E),
        glass = Color.White, danger = Color(0xFFFF7A7A),
    )

    /** Sattes OLED-Schwarz mit Indigo-Licht (seit 11.0 die Markenfarbe, passend zum App-Symbol). Der Standard. */
    val Obsidian = Palette(
        id = "obsidian", name = "Obsidian", isDark = true,
        bg = Color(0xFF09090B), bgMid = Color(0xFF0E0E15), surface = Color(0xFF16161D),
        glowA = Color(0xFF6E6AF0), glowB = Color(0xFF4F8DFF),
        accent = Color(0xFF8E8AFF), accentAlt = Color(0xFF7AA6FF), onAccent = Color(0xFF0B0A24),
        text = Color(0xFFF5F5F7), textSecondary = Color(0xFFA1A1AA), textTertiary = Color(0xFF6B6B76),
        glass = Color.White, danger = Color(0xFFFF6B7A),
    )

    /**
     * OLED-Schwarz mit einem leuchtenden Akzent. [tint] färbt Flächen ganz leicht in Richtung Akzent,
     * damit sie nicht grau-tot wirken.
     */
    private fun oled(
        id: String,
        name: String,
        accent: Long,
        alt: Long,
        tint: Long,
        onAccent: Long = 0xFF050507,
    ) = Palette(
        id = id, name = name, isDark = true,
        bg = Color(0xFF09090B),
        bgMid = lerpColor(Color(0xFF0E0E11), Color(tint), 0.05f),
        surface = lerpColor(Color(0xFF16161A), Color(tint), 0.06f),
        glowA = Color(accent), glowB = Color(alt),
        accent = Color(accent), accentAlt = Color(alt), onAccent = Color(onAccent),
        text = Color(0xFFF5F5F7), textSecondary = Color(0xFFA1A1AA), textTertiary = Color(0xFF6B6B76),
        glass = Color.White, danger = Color(0xFFFF6B7A),
    )

    val Emerald = oled("emerald", "Smaragd", 0xFF34D399, 0xFFA3E635, 0xFF34D399)
    val Ruby = oled("ruby", "Rubin", 0xFFFF4D6D, 0xFFFF8A65, 0xFFFF4D6D)
    val Amber = oled("amber", "Bernstein", 0xFFFBBF24, 0xFFFB923C, 0xFFFBBF24)
    val Lagoon = oled("lagoon", "Lagune", 0xFF2DD4BF, 0xFF60A5FA, 0xFF2DD4BF)

    val Midnight = dark("midnight", "Mitternacht", 0xFF080B1A, 0xFF121842, 0xFF1A2058, 0xFF8AA4FF, 0xFFC58BFF)
    val Forest = dark("forest", "Waldlicht", 0xFF06110D, 0xFF0D2419, 0xFF143222, 0xFF6FE3A5, 0xFFD3F27A)
    val Rose = dark("rose", "Sakura", 0xFF140910, 0xFF2B1425, 0xFF3A1B31, 0xFFFF8FBA, 0xFFFFC4A8)
    val Ember = dark("ember", "Glut", 0xFF120909, 0xFF2A1311, 0xFF3A1B17, 0xFFFF8A5C, 0xFFFFC86B)
    val Graphite = dark(
        "graphite", "Graphit", 0xFF0B0B0D, 0xFF16161A, 0xFF202026, 0xFFF1F1F4, 0xFFA7A9B8,
        onAccent = 0xFF0B0B0D,
    )

    /** Warmes Papier, gut für den Tag. */
    val Paper = Palette(
        id = "paper", name = "Papier", isDark = false,
        bg = Color(0xFFFAF7F0), bgMid = Color(0xFFF1EBDD), surface = Color(0xFFFFFDF8),
        glowA = Color(0xFF2F6B57), glowB = Color(0xFFC9A66B),
        accent = Color(0xFF2F6B57), accentAlt = Color(0xFF6FA58E), onAccent = Color(0xFFFFFFFF),
        text = Color(0xFF1E1C17), textSecondary = Color(0xFF5E5A50), textTertiary = Color(0xFF8F897B),
        glass = Color.Black, danger = Color(0xFFC0392B),
    )

    // ----- Neon: reines Schwarz, sehr leuchtende Akzente -----

    private fun neon(id: String, name: String, accent: Long, alt: Long) = Palette(
        id = id, name = name, isDark = true,
        bg = Color(0xFF000000),
        bgMid = lerpColor(Color(0xFF050506), Color(accent), 0.05f),
        surface = lerpColor(Color(0xFF0F0F12), Color(accent), 0.08f),
        glowA = Color(accent), glowB = Color(alt),
        accent = Color(accent), accentAlt = Color(alt), onAccent = Color(0xFF000000),
        text = Color(0xFFF7F7FA), textSecondary = Color(0xFFA6A6B0), textTertiary = Color(0xFF6A6A74),
        glass = Color.White, danger = Color(0xFFFF4D6D),
    )

    val NeonGreen = neon("neon_green", "Neon-Grün", 0xFF39FF14, 0xFFB6FF3B)
    val NeonCyan = neon("neon_cyan", "Neon-Cyan", 0xFF00F0FF, 0xFF3D9BFF)
    val NeonPink = neon("neon_pink", "Neon-Pink", 0xFFFF2BD6, 0xFFFF6AA9)
    val NeonOrange = neon("neon_orange", "Neon-Orange", 0xFFFF7A00, 0xFFFFC400)

    // ----- Hell -----

    private fun light(id: String, name: String, bg: Long, mid: Long, surface: Long, accent: Long, alt: Long, onAccent: Long = 0xFFFFFFFF) = Palette(
        id = id, name = name, isDark = false,
        bg = Color(bg), bgMid = Color(mid), surface = Color(surface),
        glowA = Color(accent), glowB = Color(alt),
        accent = Color(accent), accentAlt = Color(alt), onAccent = Color(onAccent),
        text = Color(0xFF1C1B22), textSecondary = Color(0xFF5F5E6B), textTertiary = Color(0xFF9393A0),
        glass = Color.Black, danger = Color(0xFFD23B3B),
    )

    val Chalk = light("chalk", "Kreide", 0xFFFFFFFF, 0xFFF3F3F6, 0xFFFFFFFF, 0xFF16161A, 0xFF5B5B66)
    val Peach = light("peach", "Pfirsich", 0xFFFFF6F0, 0xFFFFE7D8, 0xFFFFFFFF, 0xFFE0643C, 0xFFF5A35C)
    val Sky = light("sky", "Himmel", 0xFFF1F7FD, 0xFFDCEAF8, 0xFFFFFFFF, 0xFF2F7FD6, 0xFF45C1D8)
    val Sand = light("sand", "Sand", 0xFFFAF6EE, 0xFFEFE6D2, 0xFFFFFDF8, 0xFFA8701F, 0xFFD8A955)
    val Blush = light("blush", "Rosé", 0xFFFFF5F7, 0xFFFBE2E8, 0xFFFFFFFF, 0xFFD9466F, 0xFFF28CA6)

    // ----- Themen: ruhige, abgestimmte Schemata mit eigenem Charakter -----

    private fun theme(
        id: String, name: String, dark: Boolean,
        bg: Long, mid: Long, surface: Long, accent: Long, alt: Long, onAccent: Long,
        text: Long, secondary: Long, tertiary: Long, danger: Long, font: FontFamily? = null,
    ) = Palette(
        id = id, name = name, isDark = dark,
        bg = Color(bg), bgMid = Color(mid), surface = Color(surface),
        glowA = Color(accent), glowB = Color(alt),
        accent = Color(accent), accentAlt = Color(alt), onAccent = Color(onAccent),
        text = Color(text), textSecondary = Color(secondary), textTertiary = Color(tertiary),
        glass = if (dark) Color.White else Color.Black, danger = Color(danger), font = font,
    )

    /**
     * Indigo (seit 14.0 der Standard): Nacht mit einem Hauch Indigo statt totem Schwarz, ein ruhiges Periwinkle als
     * Akzent. Grund, Flächen und Akzent liegen im selben Farbton, nur in der Helligkeit verschieden – so wirkt es
     * ruhig, und der Akzent hat trotzdem starken Kontrast (7:1 auf dem Grund).
     */
    val IndigoDark = theme("indigo_dark", "Indigo", true, 0xFF0B0C14, 0xFF10121D, 0xFF161926, 0xFF8B98EE, 0xFFB3BCF5, 0xFF0B0C14, 0xFFF3F4F8, 0xFFA9AEC2, 0xFF70758C, 0xFFE07A86)
    val TerminalDark = theme("terminal_dark", "Terminal", true, 0xFF0B1E22, 0xFF0F262B, 0xFF123036, 0xFF4DE6EA, 0xFF2FB8C4, 0xFF06181B, 0xFFC9F7F8, 0xFF7FC9CD, 0xFF4E8C90, 0xFFE5484D, FontFamily.Monospace)
    val CosmosDark = theme("cosmos_dark", "Kosmos", true, 0xFF0E0C1A, 0xFF151229, 0xFF1B1733, 0xFF8E7CF8, 0xFFB49CFF, 0xFF0E0C1A, 0xFFF4F2FF, 0xFFB3ADD1, 0xFF7A7499, 0xFFFF6B85)
    val SageDark = theme("sage_dark", "Salbeigarten", true, 0xFF101411, 0xFF151B16, 0xFF1B221C, 0xFF8FB39A, 0xFFC2D6B4, 0xFF0E1410, 0xFFEEF2EC, 0xFFAAB6A8, 0xFF768273, 0xFFE07A6C)

    val IndigoLight = theme("indigo_light", "Indigo", false, 0xFFF4F4FA, 0xFFE9EAF6, 0xFFFFFFFF, 0xFF3D3FB8, 0xFF6470D8, 0xFFFFFFFF, 0xFF141A2E, 0xFF525A73, 0xFF8A90A6, 0xFFC8415A)
    val TerminalLight = theme("terminal_light", "Terminal", false, 0xFFE8F1EF, 0xFFDBE9E6, 0xFFF8FCFB, 0xFF0E8B93, 0xFF3FB0B6, 0xFFFFFFFF, 0xFF0B3F44, 0xFF3F6B6E, 0xFF7C9C9E, 0xFFC0392B, FontFamily.Monospace)
    val CosmosLight = theme("cosmos_light", "Kosmos", false, 0xFFF4F3FE, 0xFFEAE7FC, 0xFFFFFFFF, 0xFF7552DB, 0xFF9F86F0, 0xFFFFFFFF, 0xFF1D1838, 0xFF5D5880, 0xFF948FB0, 0xFFD23B5A)
    val SageLight = theme("sage_light", "Salbeigarten", false, 0xFFF6F6F0, 0xFFECEDE3, 0xFFFFFFFC, 0xFF6F8D78, 0xFF9DB3A0, 0xFFFFFFFF, 0xFF1F2A22, 0xFF59665C, 0xFF8E998F, 0xFFC0533B)

    /** Gruppen für Modern, Glass und Flat. Bewusst wenige: je Farbe nur ein Schema, keine Doppelungen. */
    val ModernDark = listOf(
        PaletteGroup("Schwarz", "Black", listOf(IndigoDark, Obsidian, Graphite, Emerald, Ruby, Amber, Lagoon)),
        PaletteGroup("Stimmung", "Mood", listOf(CosmosDark, Midnight, Forest, SageDark, Ember, Rose)),
        PaletteGroup("Neon & Retro", "Neon & retro", listOf(NeonGreen, NeonCyan, NeonPink, NeonOrange, TerminalDark)),
    )
    val ModernLight = listOf(
        PaletteGroup("Ruhig", "Calm", listOf(IndigoLight, Paper, Chalk, SageLight, Sand)),
        PaletteGroup("Farbig", "Colorful", listOf(Sky, CosmosLight, Peach, Blush, TerminalLight)),
    )

    /** Schemata, die es seit 8.3 nicht mehr gibt, und womit sie ersetzt werden. */
    private val Replaced = mapOf(
        "cobalt" to "indigo_dark", "sapphire" to "midnight", "ocean" to "lagoon", "moss" to "forest",
        "autumn" to "ember", "neon" to "neon_pink", "neon_purple" to "cosmos_dark", "neon_yellow" to "neon_green",
        "neon_red" to "ruby", "neon_blue" to "neon_cyan", "parchment_dark" to "ember",
        "cloud" to "indigo_light", "sage" to "sage_light", "lavender" to "cosmos_light", "parchment_light" to "paper",
    )

    /** Altes, entferntes Schema auf das ähnlichste neue abbilden. */
    fun migrate(id: String): String = Replaced[id] ?: id

    // ----- Cartoon: warm, flach, mit dunklen Konturen -----

    private fun toon(
        id: String,
        name: String,
        bg: Long,
        mid: Long,
        surface: Long,
        accent: Long,
        alt: Long,
        ink: Long,
        textSecondary: Long,
        textTertiary: Long,
        onAccent: Long = ink,
        isDark: Boolean = false,
        text: Long = ink,
    ) = Palette(
        id = id, name = name, isDark = isDark,
        bg = Color(bg), bgMid = Color(mid), surface = Color(surface),
        glowA = Color(accent), glowB = Color(alt),
        accent = Color(accent), accentAlt = Color(alt), onAccent = Color(onAccent),
        text = Color(text), textSecondary = Color(textSecondary), textTertiary = Color(textTertiary),
        glass = if (isDark) Color.White else Color.Black, danger = Color(0xFFE5484D),
        ink = Color(ink),
    )

    val Sunny = toon("toon_sunny", "Sonnig", 0xFFFFF4DE, 0xFFFFE6BA, 0xFFFFFCF4, 0xFFFFB627, 0xFFFF8A5B, 0xFF2B1D0E, 0xFF6E5A43, 0xFFA08C72)
    val ToonSky = toon("toon_sky", "Himmel", 0xFFE8F3FF, 0xFFD3E7FF, 0xFFFFFFFF, 0xFF5B9BFF, 0xFF8ED8FF, 0xFF15233D, 0xFF4A5B78, 0xFF8494AE)
    val Meadow = toon("toon_meadow", "Wiese", 0xFFEEF8E6, 0xFFDBF0CB, 0xFFFFFFFF, 0xFF5CC76E, 0xFFC4DE4F, 0xFF16301A, 0xFF4B6650, 0xFF86A08A)
    val Candy = toon("toon_candy", "Bonbon", 0xFFFFEFF4, 0xFFFFDCE8, 0xFFFFFFFF, 0xFFFF7FA8, 0xFFFFBE6B, 0xFF2E0F1A, 0xFF7A4B5B, 0xFFAD8996)
    val ToonPeach = toon("toon_peach", "Pfirsich", 0xFFFFEDE3, 0xFFFFD9C6, 0xFFFFFAF6, 0xFFFF8A5B, 0xFFFFC145, 0xFF2E1508, 0xFF7A5341, 0xFFAE8B7B)
    val ToonLavender = toon("toon_lavender", "Lavendel", 0xFFF1EBFF, 0xFFE1D6FF, 0xFFFFFFFF, 0xFF8B6CFF, 0xFFFF9ED2, 0xFF1E1438, 0xFF5D5182, 0xFF968BB6)
    val ToonMint = toon("toon_mint", "Minze", 0xFFE6FAF4, 0xFFCCF2E6, 0xFFFFFFFF, 0xFF2EC4A0, 0xFF8FE36B, 0xFF0E2A23, 0xFF46675E, 0xFF83A098)

    val Dusk = toon(
        "toon_dusk", "Abend", 0xFF221E38, 0xFF2D2850, 0xFF35305C, 0xFFFFCB4D, 0xFFFF92A8, 0xFF0E0B1C,
        0xFFC9C0E0, 0xFF8E86AD, onAccent = 0xFF1A1630, isDark = true, text = 0xFFFFF7E8,
    )
    val ToonNight = toon(
        "toon_night", "Nachthimmel", 0xFF141A33, 0xFF1C2446, 0xFF232C55, 0xFFFFD54D, 0xFF7FD4FF, 0xFF070A17,
        0xFFB9C0E0, 0xFF7E87AD, onAccent = 0xFF141A33, isDark = true, text = 0xFFF4F6FF,
    )
    val ToonCocoa = toon(
        "toon_cocoa", "Kakao", 0xFF24170F, 0xFF302016, 0xFF3B2A1D, 0xFFFF9F43, 0xFFFFD27A, 0xFF0D0704,
        0xFFD8C3AA, 0xFF9C8670, onAccent = 0xFF24170F, isDark = true, text = 0xFFFFF4E6,
    )
    val ToonGalaxy = toon(
        "toon_galaxy", "Galaxie", 0xFF1A1030, 0xFF241645, 0xFF2E1C57, 0xFF5CE1E6, 0xFFFF7EDB, 0xFF08041A,
        0xFFC6B8E8, 0xFF8B7DB0, onAccent = 0xFF1A1030, isDark = true, text = 0xFFF7F2FF,
    )
    val ToonNeon = toon(
        "toon_neon", "Neon-Comic", 0xFF121212, 0xFF1A1A1A, 0xFF242424, 0xFF39FF14, 0xFFFFE600, 0xFF000000,
        0xFFBDBDBD, 0xFF808080, onAccent = 0xFF000000, isDark = true, text = 0xFFFFFFFF,
    )
    val ToonDeep = toon(
        "toon_deep", "Tiefsee", 0xFF0D2226, 0xFF123036, 0xFF173B42, 0xFFFF7A6B, 0xFF4FE3C1, 0xFF031013,
        0xFFA9CBC6, 0xFF6E928D, onAccent = 0xFF0D2226, isDark = true, text = 0xFFEFFFFC,
    )

    val CartoonLight = listOf(PaletteGroup("Hell", "Light", listOf(Sunny, ToonSky, Meadow, Candy, ToonPeach, ToonLavender, ToonMint)))
    val CartoonDark = listOf(PaletteGroup("Dunkel", "Dark", listOf(Dusk, ToonNight, ToonCocoa, ToonGalaxy, ToonNeon, ToonDeep)))

    private val everything: List<Palette> by lazy {
        (ModernDark + ModernLight + CartoonDark + CartoonLight).flatMap { it.items }
    }

    /** Farbschemata, die zu einem Stil und zu Hell/Dunkel passen, nach Gruppen. */
    fun groupsFor(style: UiStyle, dark: Boolean): List<PaletteGroup> = when {
        style == UiStyle.Cartoon && dark -> CartoonDark
        style == UiStyle.Cartoon -> CartoonLight
        dark -> ModernDark
        else -> ModernLight
    }

    fun defaultFor(style: UiStyle, dark: Boolean): Palette = when {
        style == UiStyle.Cartoon && dark -> Dusk
        style == UiStyle.Cartoon -> Sunny
        // Glas gehört zu Indigo (seit 14.0): das Licht der Glasflächen kommt aus dem Indigo-Grund
        style == UiStyle.Glass && dark -> IndigoDark
        dark -> Obsidian
        else -> IndigoLight
    }

    /** Passt das Schema zu Stil und Hell/Dunkel? */
    fun fits(id: String, style: UiStyle, dark: Boolean): Boolean =
        groupsFor(style, dark).any { g -> g.items.any { it.id == id } }

    fun byId(id: String): Palette = everything.firstOrNull { it.id == id } ?: IndigoDark
}

/** Eine benannte Gruppe von Farbschemata (z. B. "Neon"). */
@Immutable
data class PaletteGroup(private val titleDe: String, private val titleEn: String, val items: List<Palette>) {
    val title: String get() = tr(titleDe, titleEn)
}

private val SuccessDark = Color(0xFF5BC98A)
private val SuccessLight = Color(0xFF1E8A52)
private val WarningDark = Color(0xFFF2B455)
private val WarningLight = Color(0xFFB26A00)

private fun lerpColor(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f,
)

val LocalPalette = compositionLocalOf { Palettes.IndigoDark }

/**
 * Gesamter Look der App: Modern (Licht, weiche Flächen), Glass (durchscheinende Flächen mit Lichtkante),
 * Cartoon (Konturen, warm) oder Flat (klare, flache Flächen).
 */
enum class UiStyle(val id: String, val label: String) {
    Modern("modern", "Modern"),
    Glass("glass", "Glass"),
    Cartoon("cartoon", "Cartoon"),
    Flat("flat", "Flat");

    companion object {
        fun byId(id: String?): UiStyle = entries.firstOrNull { it.id == id } ?: Modern
    }
}

val LocalUiStyle = staticCompositionLocalOf { UiStyle.Modern }

/** Feines Muster im Hintergrund (wie Millimeterpapier oder Punktraster). */
enum class BackgroundPattern(val id: String, private val de: String, private val en: String) {
    None("none", "Ohne", "None"),
    Grid("grid", "Raster", "Grid"),
    Dots("dots", "Punkte", "Dots");

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?): BackgroundPattern = entries.firstOrNull { it.id == id } ?: None
    }
}

val LocalBackgroundPattern = staticCompositionLocalOf { BackgroundPattern.None }

/** Zugriff auf die aktuellen Farben: `Funke.colors.accent`, und auf den Stil: `Funke.cartoon` */
object Funke {
    val colors: Palette
        @Composable
        @ReadOnlyComposable
        get() = LocalPalette.current

    val style: UiStyle
        @Composable
        @ReadOnlyComposable
        get() = LocalUiStyle.current

    val cartoon: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalUiStyle.current == UiStyle.Cartoon

    val flat: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalUiStyle.current == UiStyle.Flat

    /** Schriften des aktuellen Looks (seit 15.2, siehe [FontSet]). */
    val fonts: FontSet
        @Composable
        @ReadOnlyComposable
        get() = LocalFontSet.current

    /** Glass-Stil: wie Modern, aber Flächen sind durchscheinend und haben eine feine Lichtkante. */
    val glassy: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalUiStyle.current == UiStyle.Glass
}

@Composable
private fun animated(color: Color): Color =
    animateColorAsState(color, tween(500), label = "palette").value

/** Blendet beim Wechsel des Farbschemas jede Farbe weich zur neuen hinüber. */
@Composable
fun animatePalette(target: Palette): Palette = Palette(
    id = target.id,
    name = target.name,
    isDark = target.isDark,
    bg = animated(target.bg),
    bgMid = animated(target.bgMid),
    surface = animated(target.surface),
    glowA = animated(target.glowA),
    glowB = animated(target.glowB),
    accent = animated(target.accent),
    accentAlt = animated(target.accentAlt),
    onAccent = animated(target.onAccent),
    text = animated(target.text),
    textSecondary = animated(target.textSecondary),
    textTertiary = animated(target.textTertiary),
    glass = animated(target.glass),
    danger = animated(target.danger),
    ink = animated(target.ink),
    font = target.font,
)
