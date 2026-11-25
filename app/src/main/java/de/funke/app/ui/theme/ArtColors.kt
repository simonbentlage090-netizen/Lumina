package de.funke.app.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================================
// Farb-Tokens für Illustrationen und die Marke.
// Diese Farben hängen bewusst NICHT vom Farbschema ab: Lumi ist immer eine gelbe Glühbirne,
// Feuer ist immer orange. Alles andere in der App kommt aus der Palette (Funke.colors).
// =============================================================================================

/** Lumi, das Glühbirnen-Maskottchen. */
object LumiColors {
    val Ink = Color(0xFF1E1A26)
    val Bulb = Color(0xFFFFC93C)
    val Tear = Color(0xFF8FD3FF)
    val Mouth = Color(0xFF3A1F2B)
    val Tongue = Color(0xFFFF6F7D)
    val Cheek = Color(0xFFFF8FA3)
    val Dust = Color(0xFFE9E4DA)
    val GlowRay = Color(0xFFFFD25C)
    /** Glas bei ausgeschaltetem Licht (gleiche Farbe wie in lumi_body_off.xml). */
    val GlassOff = Color(0xFFE4E3EC)
    val FilamentOff = Color(0xFF6E6A7E)
    val FilamentHot = Color(0xFFFFF4C2)
    /** Lumis Handschuhe */
    val Glove = Color(0xFFFFFFFF)
    /** feiner heller Rand um Arme auf dunklem Grund */
    val ArmRim = Color(0xFF6B6280)
    /** lila Schimmer unten in den Augen */
    val EyeLow = Color(0xFF6A5AA8)
    val BlushLine = Color(0xFFE24F70)
    val Wire = Color(0xFF8E8A9E)
    val WireBack = Color(0xFFB8B5C8)
    val GlassEdge = Color(0xFFA6A3B8)
    val FilamentWhite = Color(0xFFFFFDEC)
    val HaloOut = Color(0xFFFFA830)
    val HaloIn = Color(0xFFFFE99A)
    val Metal = Color(0xFFCDD2DE)
    val MetalDark = Color(0xFF9AA1B3)
    val Insulator = Color(0xFF2A2533)
    val BookViolet = Color(0xFF7E6CF2)
    val Sky = Color(0xFF5B9BFF)
    val Leaf = Color(0xFF5CC76E)
    val Butter = Color(0xFFFFE27A)
    val Peach = Color(0xFFFF8A5B)
    val Pink = Color(0xFFFF6F9C)
    val Sun = Color(0xFFFFC83D)
}

/** Konfetti in Lumis Farben (Cartoon) */
val ConfettiColors = listOf(LumiColors.Sun, LumiColors.Pink, LumiColors.Sky, LumiColors.Leaf, LumiColors.Peach)

/** Streak-Flamme und Eis. */
object FlameColors {
    val Top = Color(0xFFFFB020)
    val Bottom = Color(0xFFFF5A00)
    val Core = Color(0xFFFFE07A)
    val Flat = Color(0xFFFF9500)
    val Shadow = Color(0x66B03A00)
    val Ice = Color(0xFF7CC8FF)
}

/** Bücherregal im Aufgaben-Reiter: Licht und Schatten auf den Buchrücken. */
object ShelfColors {
    /** Lichtstreifen, der beim Herausziehen über den Rücken gleitet (seit 13.3) */
    val Sheen = Color(0xFFFFFFFF)
}

/**
 * Bücher (seit 13.4): Lesefarben im Reader und Umschläge für Bücher ohne eigenes Titelbild.
 * Bewusst ruhige, gedämpfte Töne – Papier soll nach Papier aussehen, nicht nach Bildschirm.
 */
object ReaderColors {
    val PaperBg = Color(0xFFFBF8F2)
    val PaperText = Color(0xFF2B2824)
    val PaperSoft = Color(0xFF8C857A)
    val SepiaBg = Color(0xFFF1E6D2)
    val SepiaText = Color(0xFF3F3427)
    val SepiaSoft = Color(0xFF8E7C64)
    val NightBg = Color(0xFF15161A)
    val NightText = Color(0xFFD4D0C8)
    val NightSoft = Color(0xFF7D7A74)
    /** Akzent auf hellem Papier (Lesezeichen, aktive Werkzeuge): tiefes Indigo, ~8:1 auf Papier */
    val InkAccent = Color(0xFF3D3FB8)
    /** Akzent auf der Nachtseite: helles Indigo */
    val NightAccent = Color(0xFF9AA5F0)
    /** Schatten am Buchrücken eines Umschlags */
    val Spine = Color(0x2E000000)
    /** Weißes PDF-Blatt, damit Seiten auch im dunklen Schema wie Papier wirken */
    val Sheet = Color(0xFFFFFFFF)

    /** Umschläge: [Grund, Schrift] */
    val Covers = listOf(
        Color(0xFF3E4A61) to Color(0xFFEDE6D8),
        Color(0xFF6B4F4F) to Color(0xFFF3E9DC),
        Color(0xFF4F6355) to Color(0xFFEEF0E6),
        Color(0xFF5B5470) to Color(0xFFEFEAF6),
        Color(0xFF8A6A45) to Color(0xFFFBF3E4),
        Color(0xFF3F5D66) to Color(0xFFE7F0F1),
    )
}

/** Belohnungen im Aufgaben-Reiter (werden mit Palette.tame() ans Farbschema angepasst). */
object RewardColors {
    /** Funken */
    val Spark = Color(0xFFFFB648)
    /** Streak-Eis */
    val Ice = Color(0xFF6FB7E8)
}

/** Markenfarben (Logo, Startbildschirm, Benachrichtigungen). */
object BrandColors {
    val Violet = Color(0xFF8E8AFF)
    val Cyan = Color(0xFF6CC8FF)
    /** gleiche Fensterfarben wie lumina_window in res/values(-night) */
    val WindowLight = Color(0xFFFAF7F0)
    val WindowDark = Color(0xFF09090B)
    val WarmLight = Color(0xFFFFF6DE)
    /** App-Symbol (seit 11.0 zweifarbig): tiefes Indigo von oben nach unten, Sonne und Buch weiß */
    val IconTop = Color(0xFF2A2777)
    val IconBottom = Color(0xFF15134A)
    val IconSun = Color(0xFFFFFFFF)
    /** Akzent der Benachrichtigungen */
    const val NotificationArgb = 0xFFF2A324.toInt()
    /** Pille über anderen Apps (Sperre) */
    const val OverlayPillArgb = 0xEB16161B.toInt()
}
