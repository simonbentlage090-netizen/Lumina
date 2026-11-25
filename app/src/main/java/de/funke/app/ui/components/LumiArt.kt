package de.funke.app.ui.components

import de.funke.app.ui.theme.LumiColors
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import de.funke.app.R
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.tr
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.atan2
import kotlin.random.Random

/*
 * Lumi, die Glühbirne, im 2D-Look.
 *
 * Feste Teile (Kolben + Gewinde, Buch, Buch am Boden, Lichtschein) sind Vektorgrafiken aus tools/lumi_shapes.py.
 * Gesicht, Arme und Hände zeichnet die App selbst aus Zahlenwerten (tools/lumi_rig.py). Dadurch gehen
 * Ausdrücke und Posen fließend ineinander über statt hart umzuspringen.
 *
 * Koordinaten: Einheiten im Sichtfenster x -1,4..1,4 und z 0..2,8 (z nach oben), wie in Blender.
 */

/** Was Lumi gerade tut. */
enum class LumiPose {
    /** Liest ruhig. Mit `scare` > 0 schaut er erschrocken hoch. */
    Reading,

    /**
     * Das Buch rutscht ihm aus den Händen, er schaut hinterher und wird traurig. Dann flackert sein Licht
     * und geht aus, er sinkt zu Boden und weint. (Abbrechen wird nicht belohnt.)
     */
    Sad,

    /** Zwei kleine Freudenhüpfer, danach fröhliches Wiegen. */
    Happy,

    /** Große Kulleraugen, schaut dich hoffnungsvoll an ("Bitte bleib!"). Hält das Buch fest. */
    Plead,

    /** Riesige, nasse Glubschaugen, ganz traurig ("Bist du wirklich sicher?"). Hält das Buch fest. */
    Puppy,

    /** Wie [Puppy], aber jetzt kullern die Tränen und er schluchzt leise. */
    Heartbroken,

    /** Ohne Buch: schwebt entspannt, die Hände locker an der Seite, und schaut dich freundlich an. */
    Idle,
}

/** Kleine Reaktionen beim Antippen. Es kommt jedes Mal eine andere, zufällig gewählt. */
/** Kurze Reaktionen, die man von außen auslösen kann (siehe Lumi2D cue). */
enum class LumiCue { Hop, Giggle, Wink, Glow, Spin, Blush }

private enum class Reaction(val millis: Int) {
    /** Schwebt ein Stück höher, freut sich und gleitet sanft zurück (ohne Nachfedern). */
    Hop(900),

    /** Wackelt kichernd hin und her. */
    Giggle(820),

    /** Legt den Kopf schief und zwinkert. */
    Wink(900),

    /** Leuchtet hell auf, staunt mit großen Augen, kleine Lichtstrahlen. */
    Glow(950),

    /** Dreht sich einmal um sich selbst. */
    Spin(1000),

    /** Wird verlegen: Wangen glühen, Augen zu, macht sich ein bisschen klein. */
    Blush(1100),
}

private const val PIVOT_Y = 0.55f          // Drehpunkt fürs Wiegen: etwa die Körpermitte (Lumi schwebt, er steht nicht)
private const val BULB_CENTER_Y = 0.375f // Mitte der Glaskugel (z = 1,75)
private const val BOOK_CENTER_Y = 0.59357f // Mitte des gehaltenen Buchs (z = 1,138)

/*
 * Körperform seit 8.3: eine echte A-Glühbirne, am Referenzfoto abgemessen (breiter Hals, der direkt über
 * der Fassung etwas nach außen geht und sich dann in die Kugel weitet), siehe tools/body11.py.
 * Gesicht, Arme, Hände und Buch sind weiter in den alten Koordinaten beschrieben (Kugelmitte z = 1,60,
 * Radius 0,74) und werden beim Zeichnen in die neue, etwas kleinere Kugel eingepasst:
 * um [FACE_C0] mit [UPPER_K] verkleinert und nach oben zur neuen Kugelmitte [BODY_CZ] geschoben.
 * Arme, Hände und Buch sitzen zusätzlich [HOLD_LIFT] höher, damit unter dem Buch noch etwas Hals zu sehen ist.
 */
private const val FACE_C0 = 1.60f
private const val BODY_CZ = 1.75f
private const val UPPER_K = 0.67f / 0.74f
private const val HOLD_LIFT = 0.18f
private const val TWO_PI = (2.0 * PI).toFloat()

// Ablauf "Buch fällt" (Sekunden)
private const val RELEASE = 0.10f   // Hände lassen los
private const val LAND = 0.56f      // Buch schlägt auf
private const val FLICKER_START = 1.05f // Licht flackert ...
private const val FLICKER_END = 1.85f   // ... und geht aus
private const val SINK_END = 2.5f       // ohne Licht sinkt er langsam zu Boden
private const val SAD_END = 2.9f
// Schweben: so hoch (Sichtfenster-Einheiten) hält sich Lumi über dem Boden, dazu ein sanftes Auf und Ab
private const val HOVER = 0.07f
private const val BOB = 0.045f
// Freude
private const val JOY_DUR = 1.3f
private const val JOY_END = 1.8f

private val INK = LumiColors.Ink
private val BULB = LumiColors.Bulb
private val TEAR = LumiColors.Tear
private val MOUTH = LumiColors.Mouth
private val TONGUE = LumiColors.Tongue
private val CHEEK = LumiColors.Cheek
private val DUST = LumiColors.Dust
private val GLOW_RAY = LumiColors.GlowRay
/** Farbe des weichen Scheins (wie vorher in lumi_glow.xml). */
private val GLOW_SOFT = Color(0xFFFFD25C)
/** Glas bei ausgeschaltetem Licht (gleiche Farbe wie in lumi_body_off.xml). */
private val GLASS_OFF = LumiColors.GlassOff
private val FILAMENT_OFF = LumiColors.FilamentOff
private val FILAMENT_HOT = LumiColors.FilamentHot
private const val NECK_Z = 0.745f
private const val FIL_Z = 1.30f
private const val FIL_X = 0.13f
/** Feiner heller Rand um Arme und Hände auf dunklem Grund, damit sie nicht im Schwarz verschwinden. */
private val ARM_RIM = LumiColors.ArmRim
private const val ARM_RIM_W = 0.022f
// Gesicht seit 8.4 ("Kulleraugen", Entwurf B): größere Augen, tiefer und weiter auseinander, kleiner Mund, größere Bäckchen
private const val EYE_X = 0.248f
private const val EYE_Z = 1.574f
private const val CHEEK_X = 0.433f
/** lila Schimmer unten in den Augen */
private val EYE_LOW = LumiColors.EyeLow
/** kleine Rotschimmer-Striche auf den Bäckchen (verlegen, kichern) */
private val BLUSH_LINE = LumiColors.BlushLine

// --- automatisch erzeugt aus tools/lumi_rig.py (gleiche Zahlen wie die Vorschau) ---
private const val F_RX = 0
private const val F_RZ = 1
private const val F_DX = 2
private const val F_DZ = 3
private const val F_LIDO = 4
private const val F_LIDI = 5
private const val F_LIDA = 6
private const val F_H1X = 7
private const val F_H1Z = 8
private const val F_H1R = 9
private const val F_H2X = 10
private const val F_H2Z = 11
private const val F_H2R = 12
private const val F_SHUT = 13
private const val F_ARCE = 14
private const val F_ARCC = 15
private const val F_ARCW = 16
private const val F_BROWA = 17
private const val F_BROWO = 18
private const val F_BROWI = 19
private const val F_BROWC = 20
private const val F_MX = 21
private const val F_MZ = 22
private const val F_MTOP = 23
private const val F_MBOT = 24
private const val F_MFILL = 25
private const val F_MTONGUE = 26
private const val F_MW = 27
private const val F_CHA = 28
private const val F_CHRX = 29
private const val F_CHRZ = 30
private const val F_CHZ = 31
private const val F_SWEAT = 32
private const val F_RIM = 33
private const val F_TEARS = 34
private const val F_LOWA = 35    // lila Schimmer unten im Auge
private const val F_SQUINT = 36  // Augen zusammengekniffen: > <
private const val F_BLUSHL = 37  // Rotschimmer-Striche
private const val F_MCAT = 38    // Katzenmund ω statt des normalen Munds
private const val F_SPARK = 39   // kleiner Stern im Auge
private const val FACE_SIZE = 40

/**
 * Ein Gesicht im Stil B (alte Koordinaten: Kugelmitte 1,60, Radius 0,74).
 * Augen: [rx]/[rz] Größe, [dx]/[dz] Blickrichtung, Lider oben außen/innen ([lidO]/[lidI], Abstand über der Augenmitte).
 * Mund: Mundwinkel bei ±[mx] auf Höhe [mz], oberer/unterer Bogen über [mTop]/[mBot]. Gleich = Strich.
 */
private fun faceB(
    rx: Float = 0.102f, rz: Float = 0.117f, dx: Float = 0f, dz: Float = 0f,
    lidO: Float = 0.2f, lidI: Float = 0.2f, lidA: Float = 0f,
    h1x: Float = 0.0265f, h1z: Float = 0.042f, h1r: Float = 0.0367f,
    h2x: Float = -0.0347f, h2z: Float = -0.047f, h2r: Float = 0.0153f,
    shut: Float = 0f, arcE: Float = 0.004f, arcC: Float = -0.06f, arcW: Float = 0.040f,
    browA: Float = 0f, browO: Float = 0.2f, browI: Float = 0.2f, browC: Float = 0.22f,
    mx: Float = 0.0555f, mz: Float = 1.415f, mTop: Float = 1.356f, mBot: Float = 1.356f,
    mFill: Float = 0f, mTongue: Float = 0f, mW: Float = 0.031f,
    chA: Float = 0.82f, chRx: Float = 0.10f, chRz: Float = 0.058f, chZ: Float = 1.448f,
    sweat: Float = 0f, rim: Float = 0f, tears: Float = 0f,
    lowA: Float = 0.55f, squint: Float = 0f, blushL: Float = 0f, mCat: Float = 0f, spark: Float = 0f,
) = floatArrayOf(
    rx, rz, dx, dz, lidO, lidI, lidA, h1x, h1z, h1r, h2x, h2z, h2r, shut, arcE, arcC, arcW,
    browA, browO, browI, browC, mx, mz, mTop, mBot, mFill, mTongue, mW, chA, chRx, chRz, chZ, sweat, rim, tears,
    lowA, squint, blushL, mCat, spark,
)

/** liest: Blick aufs Buch, Lider ein klein wenig tiefer, kleines Lächeln */
private val FACE_READ = faceB(dz = -0.022f, lidO = 0.091f, lidI = 0.091f, lidA = 0.02f, h1z = 0.007f, h2z = -0.0725f)
/** erschrickt: große runde Augen, Brauen hoch, kleiner runder Mund, Schweißtropfen */
private val FACE_SCARED = faceB(
    rx = 0.114f, rz = 0.131f, dz = 0.01f, h1x = 0.023f, h1z = 0.039f, h1r = 0.025f, h2x = -0.04f, h2z = -0.052f, h2r = 0.0114f,
    browA = 1f, browO = 0.25f, browI = 0.295f, browC = 0.29f,
    mx = 0.052f, mz = 1.395f, mTop = 1.485f, mBot = 1.295f, mFill = 1f, mTongue = 0.6f, mW = 0.028f,
    chA = 0.55f, sweat = 1f,
)
/** schaut dem fallenden Buch hinterher */
private val FACE_SCAREDDOWN = faceB(
    rx = 0.107f, rz = 0.123f, dx = 0.044f, dz = -0.052f, h1x = 0.03f, h1z = 0.012f, h1r = 0.032f, h2x = -0.032f, h2z = -0.068f, h2r = 0.014f,
    browA = 1f, browO = 0.19f, browI = 0.264f, browC = 0.243f,
    mx = 0.042f, mz = 1.39f, mTop = 1.455f, mBot = 1.32f, mFill = 1f, mTongue = 0.5f, mW = 0.028f,
    chA = 0.55f, sweat = 0.6f,
)
/** traurig: Lider außen tiefer, Tränen stehen in den Augen, kleiner umgedrehter Mund */
private val FACE_SAD = faceB(
    dx = 0.022f, dz = -0.037f, lidO = 0.037f, lidI = 0.089f, lidA = 0.014f,
    h1x = 0.0245f, h1z = 0.0117f, h1r = 0.0347f, h2z = -0.049f,
    browA = 1f, browO = 0.132f, browI = 0.22f, browC = 0.19f,
    mx = 0.044f, mz = 1.40f, mTop = 1.432f, mBot = 1.432f, chA = 0.6f, rim = 0.7f,
)
/** weint: große nasse Glubschaugen, die dich anschauen, Tränen laufen, Mund zittrig offen */
private val FACE_CRY = faceB(
    rx = 0.118f, rz = 0.138f, dx = 0.004f, dz = -0.01f, h1x = 0.022f, h1z = 0.046f, h1r = 0.045f, h2x = -0.045f, h2z = -0.04f, h2r = 0.02f,
    browA = 1f, browO = 0.15f, browI = 0.265f, browC = 0.235f,
    mx = 0.06f, mz = 1.38f, mTop = 1.44f, mBot = 1.32f, mFill = 1f, mTongue = 0.4f, mW = 0.028f,
    chA = 0.75f, rim = 1f, tears = 1f,
)
/** bittet: große glänzende Hundeaugen, Brauen innen hoch, kleiner Mund */
private val FACE_PLEAD = faceB(
    rx = 0.114f, rz = 0.131f, dz = 0.012f, h1x = 0.023f, h1z = 0.05f, h1r = 0.046f, h2x = -0.043f, h2z = -0.039f, h2r = 0.0205f,
    browA = 1f, browO = 0.161f, browI = 0.249f, browC = 0.227f,
    mx = 0.05f, mz = 1.40f, mTop = 1.425f, mBot = 1.372f, mFill = 1f, mW = 0.028f,
    chA = 0.9f, rim = 0.75f, spark = 1f,
)
/** Glubschaugen: riesig, ganz nass, Brauen innen hoch, kleiner zittriger Mund */
private val FACE_PUPPY = faceB(
    rx = 0.124f, rz = 0.145f, dz = 0.008f, h1x = 0.022f, h1z = 0.052f, h1r = 0.05f, h2x = -0.047f, h2z = -0.04f, h2r = 0.022f,
    browA = 1f, browO = 0.17f, browI = 0.27f, browC = 0.245f,
    mx = 0.045f, mz = 1.392f, mTop = 1.425f, mBot = 1.40f, mFill = 1f, mW = 0.028f,
    chA = 0.85f, chRx = 0.11f, chRz = 0.064f, rim = 1f, spark = 1f,
)
/** Wie Glubschaugen, dazu laufen Tränen und der Mund ist leicht offen */
private val FACE_HEARTBROKEN = faceB(
    rx = 0.124f, rz = 0.145f, dz = 0.004f, h1x = 0.022f, h1z = 0.05f, h1r = 0.05f, h2x = -0.047f, h2z = -0.042f, h2r = 0.022f,
    browA = 1f, browO = 0.16f, browI = 0.275f, browC = 0.245f,
    mx = 0.05f, mz = 1.385f, mTop = 1.435f, mBot = 1.345f, mFill = 1f, mTongue = 0.35f, mW = 0.028f,
    chA = 0.85f, chRx = 0.11f, chRz = 0.064f, rim = 1f, tears = 1f,
)
/** staunt: große Augen mit Sternchen, Brauen hoch, kleines o */
private val FACE_WOW = faceB(
    rx = 0.114f, rz = 0.131f, dz = 0.01f, h1x = 0.0274f, h1z = 0.047f, h1r = 0.0433f, h2x = -0.041f, h2z = -0.055f, h2r = 0.0182f,
    browA = 1f, browO = 0.25f, browI = 0.25f, browC = 0.268f,
    mx = 0.048f, mz = 1.395f, mTop = 1.475f, mBot = 1.305f, mFill = 1f, mTongue = 0.5f, mW = 0.026f,
    chA = 1f, spark = 1f,
)
/** schläft: Augen zu (sanfte Bögen ∪), kleines zufriedenes Lächeln, Bäckchen etwas blasser */
private val FACE_SLEEP = faceB(
    dz = -0.008f, shut = 1f, arcE = 0.018f, arcC = -0.048f, arcW = 0.036f,
    mx = 0.042f, mz = 1.412f, mTop = 1.392f, mBot = 1.392f, chA = 0.62f,
)
/** freundlich: schaut dich an, kleines Lächeln (ohne Buch) */
private val FACE_FRIENDLY = faceB(mTop = 1.372f, mBot = 1.372f, mx = 0.06f)
/** freut sich: Bogenaugen ∩, offener Mund, kräftige Bäckchen */
private val FACE_HAPPY = faceB(
    shut = 1f, arcE = -0.035f, arcC = 0.111f, arcW = 0.044f,
    mx = 0.078f, mz = 1.425f, mTop = 1.435f, mBot = 1.30f, mFill = 1f, mTongue = 1f, mW = 0.028f,
    chA = 0.92f, chRx = 0.112f, chRz = 0.065f,
)
/** kichert: Augen zusammengekniffen > <, Katzenmund ω, Rotschimmer */
private val FACE_GIGGLE = faceB(
    shut = 1f, squint = 1f, mx = 0.063f, mz = 1.42f, mCat = 1f,
    chA = 0.95f, chRx = 0.115f, chRz = 0.067f, blushL = 1f,
)
/** verlegen: schaut zur Seite nach unten, Lider etwas tiefer, große rote Bäckchen mit Strichen */
private val FACE_BLUSH = faceB(
    rx = 0.097f, rz = 0.112f, dx = -0.052f, dz = -0.026f, lidO = 0.09f, lidI = 0.09f, lidA = 0.013f,
    h1x = 0f, h1z = 0.034f, h1r = 0.033f, h2x = -0.039f, h2z = -0.043f, h2r = 0.0136f,
    mx = 0.042f, mz = 1.412f, mTop = 1.388f, mBot = 1.388f,
    chA = 1f, chRx = 0.125f, chRz = 0.072f, blushL = 1f,
)
private const val A_C1X = 0
private const val A_C1Z = 1
private const val A_C2X = 2
private const val A_C2Z = 3
private const val A_EX = 4
private const val A_EZ = 5
private const val A_HX = 6
private const val A_HZ = 7
private const val A_HRX = 8
private const val A_HRZ = 9
private const val A_FX = 10
private const val A_FZ = 11
private const val A_FRX = 12
private const val A_FRZ = 13
private const val A_FA = 14
private const val ARM_SIZE = 15
/** Hält das Buch. Seit 15.0 schwingen die Arme weniger weit aus (wirken dadurch kürzer); Hände bleiben am Buch. */
private val ARMS_HOLD = floatArrayOf(0.80f, 1.02f, 0.82f, 0.62f, 0.47f, 0.7f, 0.43f, 0.735f, 0.085f, 0.105f, 0.375f, 0.875f, 0.04f, 0.05f, 1f, 0.80f, 1.02f, 0.82f, 0.62f, 0.47f, 0.7f, 0.43f, 0.735f, 0.085f, 0.105f, 0.375f, 0.875f, 0.04f, 0.05f, 1f)
private val ARMS_OPEN = floatArrayOf(0.7338f, 1.0334f, 0.9635f, 1.0687f, 0.9459f, 1.2808f, 0.755f, 0.9f, 0.078f, 0.095f, 0.73f, 0.95f, 0.034f, 0.042f, 0.0f, 0.7338f, 1.0334f, 0.9635f, 1.0687f, 0.9459f, 1.2808f, 0.775f, 0.9f, 0.078f, 0.095f, 0.75f, 0.91f, 0.034f, 0.042f, 0.0f)
/**
 * Hände locker an der Seite (ohne Buch). Seit 15.0 um 14 % kürzer (zur Schulter hin gestaucht, dieselbe
 * Krümmung): wirkt kindlicher und süßer, die Handschuhe bleiben gleich groß.
 */
private val ARMS_REST = floatArrayOf(0.6259f, 0.9584f, 0.7019f, 0.7875f, 0.6639f, 0.6354f, 0.71f, 0.68f, 0.075f, 0.09f, 0.69f, 0.7f, 0.03f, 0.04f, 0.0f, 0.6259f, 0.9584f, 0.7019f, 0.7875f, 0.6639f, 0.6354f, 0.71f, 0.68f, 0.075f, 0.09f, 0.69f, 0.7f, 0.03f, 0.04f, 0.0f)
/** Schlaff hängend (traurig), seit 15.0 ebenfalls etwas kürzer. */
private val ARMS_HANG = floatArrayOf(0.5923f, 0.957f, 0.6506f, 0.7626f, 0.5923f, 0.607f, 0.515f, 0.62f, 0.072f, 0.088f, 0.5f, 0.66f, 0.03f, 0.04f, 0.0f, 0.5923f, 0.957f, 0.6506f, 0.7626f, 0.5923f, 0.607f, 0.545f, 0.62f, 0.072f, 0.088f, 0.53f, 0.62f, 0.03f, 0.04f, 0.0f)
private const val BOOK_REST_X = 0.74f
private const val FLOOR_Z = 0.13f

private enum class FaceKey(val values: FloatArray, val ms: Int) {
    READ(FACE_READ, 320),
    SCARED(FACE_SCARED, 260),
    SCARED_DOWN(FACE_SCAREDDOWN, 180),
    SAD(FACE_SAD, 450),
    CRY(FACE_CRY, 400),
    HAPPY(FACE_HAPPY, 250),
    PLEAD(FACE_PLEAD, 520),
    WOW(FACE_WOW, 180),
    GIGGLE(FACE_GIGGLE, 160),
    BLUSH(FACE_BLUSH, 260),
    PUPPY(FACE_PUPPY, 520),
    HEARTBROKEN(FACE_HEARTBROKEN, 520),
    FRIENDLY(FACE_FRIENDLY, 320),
    SLEEP(FACE_SLEEP, 520),
}

private enum class ArmKey(val values: FloatArray, val ms: Int) {
    HOLD(ARMS_HOLD, 300),
    OPEN(ARMS_OPEN, 200),
    HANG(ARMS_HANG, 750),
    REST(ARMS_REST, 300),
}

/** Gesicht während einer Reaktion (null = bleibt beim normalen Gesicht, z. B. beim Zwinkern). */
private fun reactionFace(r: Reaction): FaceKey? = when (r) {
    Reaction.Hop, Reaction.Spin -> FaceKey.HAPPY
    Reaction.Giggle -> FaceKey.GIGGLE
    Reaction.Blush -> FaceKey.BLUSH
    Reaction.Wink -> null
    Reaction.Glow -> FaceKey.WOW
}

/**
 * Wie hell die Birne beim Abbrechen noch leuchtet (1 = an, 0 = aus).
 * Erst ein paar unruhige Flacker-Aussetzer, dann geht das Licht ganz aus.
 */
private fun lightLevel(s: Float): Float {
    if (s < FLICKER_START) return 1f
    if (s >= FLICKER_END) return 0f
    val steps = FLICKER
    val u = (s - FLICKER_START) / (FLICKER_END - FLICKER_START) * steps.size
    val i = u.toInt().coerceIn(0, steps.size - 1)
    val next = if (i + 1 < steps.size) steps[i + 1] else 0f
    val f = u - i
    // kurze weiche Übergänge am Ende jedes Schritts, damit es nicht wie ein Bildfehler wirkt
    return if (f > 0.7f) steps[i] + (next - steps[i]) * (f - 0.7f) / 0.3f else steps[i]
}

private val FLICKER = floatArrayOf(0.2f, 1f, 0.05f, 0.9f, 0.75f, 0f, 0.5f, 0.08f, 0.3f, 0f)

private val SmoothStep = Easing { t -> t * t * (3f - 2f * t) }
private fun smooth(a: Float, b: Float, x: Float): Float = SmoothStep.transform(((x - a) / (b - a)).coerceIn(0f, 1f))
private fun bump(t: Float, a: Float, b: Float): Float = if (t <= a || t >= b) 0f else sin(PI.toFloat() * (t - a) / (b - a))

/** Zwei kleine Schluchzer, alle 1,5 s. */
private fun sob(t: Float): Float {
    val ph = (t % 1.5f) / 1.5f
    return bump(ph, 0.04f, 0.17f) + 0.7f * bump(ph, 0.21f, 0.34f)
}

/** Bewegung der ganzen Figur (in Einheiten des Sichtfensters bzw. Grad). */
private class Motion {
    var rot = 0f
    var lift = 0f
    var tx = 0f
    var turn = 0f
    var scale = 1f
}

/** Blendet weich von einem Satz Zahlen (Gesicht/Arme) zum nächsten über. */
@Stable
private class Morph(start: FloatArray, var target: Any?) {
    private var from by mutableStateOf(start)
    private var to by mutableStateOf(start)
    val progress = Animatable(1f)
    private val out = FloatArray(start.size)

    fun current(): FloatArray {
        val t = SmoothStep.transform(progress.value.coerceIn(0f, 1f))
        val a = from
        val b = to
        for (i in out.indices) out[i] = a[i] + (b[i] - a[i]) * t
        return out
    }

    suspend fun go(key: Any, values: FloatArray, ms: Int) {
        if (key == target) return
        target = key
        from = current().copyOf()
        to = values
        progress.snapTo(0f)
        progress.animateTo(1f, tween(ms, easing = LinearEasing))
    }
}

/**
 * Lumi.
 *
 * @param scare 0..1, wie weit der Abbrechen-Regler gezogen ist. Schon ein kleines Stück reicht:
 *   sein Gesicht geht fließend in einen erschrockenen Blick über (er schaut dich an) und er lehnt sich
 *   leicht zurück. Kein Zittern.
 * @param animateChange [LumiPose.Sad]: `true` = das Buch fällt sichtbar herunter, `false` = es liegt schon
 *   am Boden und Lumi weint. [LumiPose.Happy]: `true` = er hüpft zuerst zweimal.
 */
@Composable
fun Lumi2D(
    pose: LumiPose,
    modifier: Modifier = Modifier,
    scare: Float = 0f,
    animateChange: Boolean = true,
    contentDescription: String? = null,
    tappable: Boolean = true,
    /** Licht: 0 = aus (blasses Glas, Glühdraht sichtbar), 1 = an */
    power: Float = 1f,
    /** Zählt [cue] hoch, spielt Lumi einmal [cueReaction] (z. B. Drehung bei einer perfekten Lektion). */
    cue: Int = 0,
    cueReaction: LumiCue = LumiCue.Hop,
    /** Doktorhut (beim Erklären), mit Quaste in [hatTassel] */
    hat: Boolean = false,
    hatTassel: Color = Color(0xFF8E8AFF),
    /**
     * Lumi döst (seit 15.0, z. B. wenn die Denkpause aus ist): Augen zu, ein ruhiges Lächeln, kleine "z" steigen
     * auf, der Schein wird leiser. Das Licht bleibt an – ein ausgeschalteter, weißer Kolben wirkte eher kaputt.
     * Wird er geweckt (false), öffnet er weich die Augen.
     */
    asleep: Boolean = false,
) {
    val dark = Funke.colors.isDark
    val rim = if (dark) ARM_RIM else null
    val filament = LocalFilamentStyle.current
    val scope = rememberCoroutineScope()
    // Antippen: ein kleiner Hüpfer mit fröhlichem Gesicht. Ohne Vibration.
    val poke = remember { Animatable(1f) }
    val motionCache = remember { Motion() }
    val heldLift = remember { floatArrayOf(HOVER) }
    var poking by remember { mutableStateOf(false) }
    var reaction by remember { mutableStateOf(Reaction.Hop) }
    // Grundschleife in Sekunden (0..6): Wiegen, Atmen, Tränen, Schluchzen
    val time = rememberLoop(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "lumiT",
    )

    val scareState = animateFloatAsState(
        targetValue = scare.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 1f, stiffness = 300f),
        label = "lumiScare",
    )
    val scared = scare > 0.06f

    // kleiner Schreck-Hüpfer (ohne Quetschen)
    val startle = remember { Animatable(1f) }
    LaunchedEffect(scared, pose) {
        if (scared && pose == LumiPose.Reading) {
            startle.snapTo(0f)
            startle.animateTo(1f, tween(420, easing = LinearEasing))
        }
    }

    // Ablauf "Buch fällt, Lumi weint" in Sekunden
    val sadT = remember { Animatable(if (pose == LumiPose.Sad && !animateChange) SAD_END else 0f) }
    LaunchedEffect(pose) {
        if (pose == LumiPose.Sad) {
            val left = SAD_END - sadT.value
            if (left > 0f) sadT.animateTo(SAD_END, tween((left * 1000f).roundToInt(), easing = LinearEasing))
        } else {
            sadT.snapTo(0f)
        }
    }

    // Freude in Sekunden
    val joyT = remember { Animatable(if (pose == LumiPose.Happy && !animateChange) JOY_END else 0f) }
    LaunchedEffect(pose) {
        if (pose == LumiPose.Happy) {
            val left = JOY_END - joyT.value
            if (left > 0f) joyT.animateTo(JOY_END, tween((left * 1000f).roundToInt(), easing = LinearEasing))
        } else {
            joyT.snapTo(0f)
        }
    }

    // Blinzeln beim Lesen: Lid geht kurz zu und wieder auf
    val blink = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(3400)
        while (true) {
            blink.animateTo(1f, tween(70, easing = LinearEasing))
            blink.animateTo(0f, tween(110, easing = LinearEasing))
            delay(5820)
        }
    }

    val faceKey by remember(pose, scared, poking, reaction, asleep) {
        derivedStateOf {
            if (asleep && !poking) return@derivedStateOf FaceKey.SLEEP
            when (pose) {
                LumiPose.Happy -> FaceKey.HAPPY
                LumiPose.Plead -> if (poking) reactionFace(reaction) ?: FaceKey.PLEAD else FaceKey.PLEAD
                LumiPose.Puppy -> FaceKey.PUPPY
                LumiPose.Heartbroken -> FaceKey.HEARTBROKEN
                LumiPose.Sad -> {
                    val t = sadT.value
                    when {
                        t < 0.04f -> FaceKey.SCARED
                        t < 0.95f -> FaceKey.SCARED_DOWN
                        t < 2.0f -> FaceKey.SAD // das Licht flackert und geht aus
                        else -> FaceKey.CRY
                    }
                }
                LumiPose.Reading -> when {
                    scared -> FaceKey.SCARED
                    poking -> reactionFace(reaction) ?: FaceKey.READ
                    else -> FaceKey.READ
                }
                LumiPose.Idle -> if (poking) reactionFace(reaction) ?: FaceKey.FRIENDLY else FaceKey.FRIENDLY
            }
        }
    }
    val armKey by remember(pose) {
        derivedStateOf {
            if (pose == LumiPose.Idle) {
                ArmKey.REST
            } else if (pose != LumiPose.Sad) {
                ArmKey.HOLD
            } else {
                val t = sadT.value
                when {
                    t < 0.06f -> ArmKey.HOLD
                    t < 0.95f -> ArmKey.OPEN
                    else -> ArmKey.HANG
                }
            }
        }
    }
    val bookDown by remember(pose) { derivedStateOf { pose == LumiPose.Sad && sadT.value >= LAND } }

    val face = remember { Morph(faceKey.values, faceKey) }
    val arms = remember { Morph(armKey.values, armKey) }
    LaunchedEffect(faceKey) { face.go(faceKey, faceKey.values, faceKey.ms) }
    LaunchedEffect(armKey) { arms.go(armKey, armKey.values, armKey.ms) }

    // Bewegung der ganzen Figur. Lumi hat keine Beine: er schwebt knapp über dem Boden, wiegt sich
    // ruhig hin und her (um die Körpermitte) und gleitet dabei sanft auf und ab. Kein Federn, kein Quetschen.
    // Die Werte werden in jeder Ebene (Schein, Schatten, Figur, Buch) neu berechnet, damit alle dieselbe Bewegung haben.
    fun motionNow(): Motion {
        val m = motionCache
        val t = time.value
        val ph = t / 6f * TWO_PI
        val sc = scareState.value
        // Grund-Schweben: ein ganzer Schwebe-Zyklus dauert 3 s, das Wiegen 6 s
        val bob = (1f - cos(ph * 2f)) / 2f
        val idleRot = 2.4f * sin(ph) * (1f - sc) - 2f * sc // erschrocken: lehnt sich ruhig zurück
        val idleLift = HOVER + BOB * bob * (1f - 0.6f * sc)
        val idleTx = 0.012f * sin(ph + 0.8f) * (1f - sc)
        var rot = idleRot
        var lift = idleLift
        var tx = idleTx
        var turn = 0f
        var scale = 1f
        when (pose) {
            LumiPose.Reading -> {
                lift += 0.05f * sin(PI.toFloat() * startle.value) // kleiner Schreck nach oben
            }
            LumiPose.Sad -> {
                val s = sadT.value
                val calm = 1f - smooth(0f, 0.6f, s) // das ruhige Schweben klingt aus
                rot = idleRot * calm - 3.5f * smooth(FLICKER_END, SINK_END, s) // sackt zur Seite
                tx = idleTx * calm
                lift = (HOVER + BOB * bob * calm) * (1f - smooth(FLICKER_END, SINK_END, s)) + // ohne Licht sinkt er zu Boden
                    0.06f * bump(s, 0f, 0.26f) - // zuckt zusammen, dabei rutscht das Buch weg
                    0.016f * smooth(SINK_END, SINK_END + 0.3f, s) * sob(t) // leises Schluchzen
            }
            LumiPose.Happy -> {
                val j = joyT.value
                if (j < JOY_DUR) {
                    // zwei Freudensprünge in die Luft, sanft wieder runter
                    val u = j / JOY_DUR * 2f
                    val k = min(1, u.toInt())
                    val f = u - k
                    lift += (if (k == 0) 0.13f else 0.08f) * sin(PI.toFloat() * f)
                }
                val idle = smooth(JOY_DUR - 0.2f, JOY_DUR + 0.4f, j)
                rot = idleRot * (1f - idle) + idle * 3f * sin(ph * 2f)
            }
            LumiPose.Plead -> {
                // legt den Kopf schief und schwebt ganz ruhig, als würde er auf deine Antwort warten
                rot = -3.2f + 1.2f * sin(ph * 2f)
            }
            LumiPose.Puppy -> {
                // ganz still, Kopf schief, schwebt tiefer (traurig)
                rot = -4.5f + 0.8f * sin(ph * 2f)
                lift -= 0.03f
            }
            LumiPose.Heartbroken -> {
                // sackt etwas ab und schluchzt leise
                rot = -5f + 0.6f * sin(ph * 2f)
                lift = lift - 0.05f - 0.018f * sob(t)
            }
            LumiPose.Idle -> {} // ruhiges Schweben und Wiegen
        }
        // Reaktion nach dem Antippen
        val pk = poke.value
        if (pk < 1f) {
            val b = bump(pk, 0f, 1f)
            when (reaction) {
                Reaction.Hop -> {
                    // schwebt hoch und gleitet weich zurück
                    lift += 0.14f * SmoothStep.transform(b)
                    rot += 4f * sin(TWO_PI * pk) * (1f - pk)
                }
                Reaction.Giggle -> {
                    // schnelles Hin-und-her-Wackeln, das ausklingt
                    rot += 7f * sin(TWO_PI * pk * 3f) * (1f - pk)
                    lift += 0.03f * b
                }
                Reaction.Wink -> {
                    rot += -6f * b
                    lift += 0.03f * b
                }
                Reaction.Glow -> {
                    lift += 0.07f * bump(pk, 0f, 0.5f)
                }
                Reaction.Spin -> {
                    turn = 360f * SmoothStep.transform(pk)
                    lift += 0.06f * b
                }
                Reaction.Blush -> {
                    scale = 1f - 0.05f * b
                    rot += 3f * b
                }
            }
        }
        m.rot = rot
        m.lift = lift
        m.tx = tx
        m.turn = turn
        m.scale = scale
        return m
    }

    // Von außen ausgelöste Reaktion
    LaunchedEffect(cue) {
        if (cue <= 0) return@LaunchedEffect
        val next = Reaction.entries[cueReaction.ordinal]
        reaction = next
        poking = true
        poke.snapTo(0f)
        poke.animateTo(1f, tween(next.millis, easing = LinearEasing))
        poking = false
    }
    val powerNow = power.coerceIn(0f, 1f)

    val figure: GraphicsLayerScope.() -> Unit = {
        val m = motionNow()
        rotationZ = m.rot
        rotationY = m.turn
        cameraDistance = 14f * density
        scaleX = m.scale
        scaleY = m.scale
        translationX = m.tx / 2.8f * size.width
        translationY = -m.lift / 2.8f * size.height
        transformOrigin = TransformOrigin(0.5f, PIVOT_Y)
    }

    Box(
        modifier
            .aspectRatio(1f)
            .then(
                if (tappable) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            scope.launch {
                                // jedes Mal eine andere Reaktion als zuletzt
                                val next = Reaction.entries.filter { it != reaction }.random()
                                reaction = next
                                poking = true
                                poke.snapTo(0f)
                                poke.animateTo(1f, tween(next.millis, easing = LinearEasing))
                                poking = false
                            }
                        })
                    }
                } else {
                    Modifier
                }
            )
    ) {
        // weicher Schatten auf dem Boden: je höher Lumi schwebt, desto kleiner und blasser
        Canvas(Modifier.fillMaxSize()) {
            val m = motionNow()
            val h = (m.lift / 0.25f).coerceIn(0f, 1f)
            val r = pw(0.34f * (1f - 0.3f * h))
            val c = Offset(px(m.tx), py(0.10f))
            val a = (if (dark) 0.42f else 0.16f) * (1f - 0.5f * h)
            withTransform({ scale(1f, 0.2f, c) }) {
                drawCircle(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        listOf(Color.Black.copy(alpha = a), Color.Black.copy(alpha = a * 0.5f), Color.Transparent),
                        center = c,
                        radius = r,
                    ),
                    radius = r,
                    center = c,
                )
            }
        }

        // Lichtschein: seit 15.0 ein weicher Verlauf, der zum Rand ganz ausläuft (vorher drei harte Ringe,
        // die an Kanten wie abgeschnitten aussahen). Wird nur einmal gezeichnet; bewegt wird er über die Ebene.
        val sleepGlow by animateFloatAsState(if (asleep) 0.45f else 1f, tween(600), label = "lumiSleepGlow")
        Canvas(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val ph = time.value / 6f * TWO_PI
                    var s = 1f
                    var a = 1f
                    when (pose) {
                        LumiPose.Reading -> s = 1f + 0.06f * (1f - cos(ph)) / 2f
                        LumiPose.Sad -> a = lightLevel(sadT.value) // Licht flackert und geht aus
                        LumiPose.Happy -> s = 1.08f + 0.10f * sin(PI.toFloat() * (joyT.value / JOY_DUR).coerceAtMost(1f))
                        LumiPose.Plead -> s = 0.96f + 0.04f * (1f - cos(ph * 2f)) / 2f
                        LumiPose.Puppy -> { s = 0.92f; a = 0.8f }
                        LumiPose.Heartbroken -> { s = 0.88f; a = 0.6f }
                        LumiPose.Idle -> s = 1f + 0.04f * (1f - cos(ph)) / 2f
                    }
                    if (poke.value < 1f) {
                        if (reaction == Reaction.Glow) {
                            s *= 1f + 0.20f * bump(poke.value, 0f, 1f)
                        } else {
                            s *= 1f + 0.08f * bump(poke.value, 0f, 0.6f)
                        }
                    }
                    scaleX = s
                    scaleY = s
                    alpha = a * powerNow * sleepGlow
                    // der Schein schwebt mit
                    val m = motionNow()
                    translationX = m.tx / 2.8f * size.width
                    translationY = -m.lift / 2.8f * size.height
                    transformOrigin = TransformOrigin(0.5f, BULB_CENTER_Y)
                },
        ) { drawSoftGlow() }

        // hinten: Arme (hinter dem Körper) und Körper
        Box(Modifier.fillMaxSize().graphicsLayer(figure)) {
            Canvas(Modifier.fillMaxSize()) { inHold { drawArmLines(arms.current(), rim) } }
            Image(
                painter = painterResource(R.drawable.lumi_body),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
            )
            if (hat) Canvas(Modifier.fillMaxSize()) { drawMortarboard(hatTassel, dark) }
            if (pose != LumiPose.Sad && powerNow < 1f) {
                Image(
                    painter = painterResource(R.drawable.lumi_body_off),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 1f - powerNow },
                )
                Canvas(Modifier.fillMaxSize()) { drawFilament(filament, powerNow) }
            }
            if (pose == LumiPose.Sad) {
                // Licht aus: blasses Glas blendet über den leuchtenden Kolben, darin wird der Glühdraht sichtbar
                Image(
                    painter = painterResource(R.drawable.lumi_body_off),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 1f - lightLevel(sadT.value) },
                )
                Canvas(Modifier.fillMaxSize()) {
                    val s = sadT.value
                    if (s >= FLICKER_START) drawFilament(filament, lightLevel(s))
                }
            }
        }

        // Buch: beim Lesen bewegt es sich mit Lumi, beim Fallen folgt es nur der Schwerkraft
        Box(
            Modifier.fillMaxSize().graphicsLayer {
                if (pose != LumiPose.Sad) {
                    figure()
                } else {
                    // fällt aus der Höhe, in der Lumi es gehalten hat
                    val s = sadT.value
                    if (s < RELEASE) heldLift[0] = motionNow().lift
                    val f = ((s - RELEASE) / (LAND - RELEASE)).coerceIn(0f, 1f)
                    translationY = -heldLift[0] * (1f - f) / 2.8f * size.height
                }
            }
        ) {
            if (pose == LumiPose.Idle) {
                // ohne Buch
            } else if (!bookDown) {
                Image(
                    painter = painterResource(R.drawable.lumi_book),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (pose != LumiPose.Sad) return@graphicsLayer
                            val tau = (sadT.value - RELEASE).coerceAtLeast(0f)
                            val f = (tau / (LAND - RELEASE)).coerceIn(0f, 1f)
                            translationX = 0.70f * f.pow(1.4f) / 2.8f * size.width
                            translationY = 0.82f * f * f / 2.8f * size.height // Schwerkraft: immer schneller
                            rotationZ = 28f * f.pow(1.2f) // kippt beim Fallen
                            transformOrigin = TransformOrigin(0.5f, BOOK_CENTER_Y)
                        },
                )
            } else {
                // zugeklappt flach auf dem Boden gelandet: hüpft noch zweimal kurz und kommt zur Ruhe
                Image(
                    painter = painterResource(R.drawable.lumi_book_down),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val tau = sadT.value - LAND
                            val k = exp(-7f * tau)
                            scaleY = 1f - 0.18f * k * cos(18f * tau)
                            translationY = -0.05f * k * abs(sin(14f * tau)) / 2.8f * size.height
                            rotationZ = 5f * k * cos(14f * tau)
                            transformOrigin = TransformOrigin((BOOK_REST_X + 1.4f) / 2.8f, (2.8f - FLOOR_Z) / 2.8f)
                        },
                )
                Canvas(Modifier.fillMaxSize()) { drawDust(sadT.value - LAND) }
            }
        }

        // vorne: Hände und Gesicht
        Box(Modifier.fillMaxSize().graphicsLayer(figure)) {
            Canvas(Modifier.fillMaxSize()) {
                inHold { drawHands(arms.current(), rim) }
                val wince = if (pose == LumiPose.Sad) bump(sadT.value, 0.52f, 0.70f) else 0f // kneift beim Aufprall die Augen zu
                val lids = if (face.target == FaceKey.READ || face.target == FaceKey.PLEAD) blink.value else 0f
                val f = face.current()
                if (face.target == FaceKey.PLEAD || face.target == FaceKey.PUPPY || face.target == FaceKey.HEARTBROKEN || face.target == FaceKey.CRY) {
                    // Unterlippe zittert ein wenig, die Glanzpunkte in den Augen funkeln
                    val t = time.value
                    f[F_MZ] += 0.004f * sin(TWO_PI * t * 3f)
                    f[F_H1R] *= 1f + 0.12f * sin(TWO_PI * t * 1.2f)
                }
                val pk = poke.value
                val wink = if (poking && reaction == Reaction.Wink) (2f * bump(pk, 0.08f, 0.92f)).coerceAtMost(1f) else 0f
                if (poking && reaction == Reaction.Blush) {
                    // Wangen glühen auf
                    val b = bump(pk, 0f, 1f)
                    f[F_CHA] = f[F_CHA] + (1f - f[F_CHA]) * b
                    f[F_CHRX] *= 1f + 0.4f * b
                    f[F_CHRZ] *= 1f + 0.4f * b
                }
                val light = if (pose == LumiPose.Sad) lightLevel(sadT.value) else powerNow
                val lid = androidx.compose.ui.graphics.lerp(GLASS_OFF, BULB, light)
                inFace {
                    drawFace(f, maxOf(wince, lids), time.value, wink, lid)
                }
                // kleine Lichtstrahlen beim Aufleuchten
                if (poking && reaction == Reaction.Glow) drawRays(bump(pk, 0f, 1f))
            }
        }
        if (asleep) {
            Canvas(Modifier.fillMaxSize()) { drawSleepZ(time.value) }
        }
    }
}

/**
 * Weicher Lichtschein um die Glaskugel (Mitte wie in der Grafik bei 140/105 von 280, Radius 105).
 * Innen kräftig, über der Kugelkante langsam weniger und am Rand ganz durchsichtig – kein sichtbarer Rand.
 */
private fun DrawScope.drawSoftGlow() {
    val c = Offset(size.width * 0.5f, size.height * BULB_CENTER_Y)
    val r = size.width * 0.375f
    drawCircle(
        androidx.compose.ui.graphics.Brush.radialGradient(
            0f to GLOW_SOFT.copy(alpha = 0.20f),
            0.60f to GLOW_SOFT.copy(alpha = 0.17f),
            0.76f to GLOW_SOFT.copy(alpha = 0.10f),
            0.90f to GLOW_SOFT.copy(alpha = 0.035f),
            1f to GLOW_SOFT.copy(alpha = 0f),
            center = c,
            radius = r,
        ),
        radius = r,
        center = c,
    )
}

/** Drei kleine "z", die nacheinander rechts oben aufsteigen und verblassen (Lumi döst). [t] in Sekunden (0..6). */
private fun DrawScope.drawSleepZ(t: Float) {
    for (i in 0..2) {
        // jedes z braucht 3 s für seinen Weg, versetzt um 1 s
        val u = (((t + i) % 3f) / 3f)
        val a = (bump(u, 0f, 1f) * 0.85f).coerceIn(0f, 1f)
        if (a <= 0.01f) continue
        val k = 0.07f + 0.035f * u // wächst ein wenig, während es steigt
        val x0 = 0.60f + 0.16f * u + 0.03f * sin(TWO_PI * u)
        val z0 = 2.16f + 0.42f * u
        val zPath = Path().apply {
            moveTo(px(x0), py(z0))
            lineTo(px(x0 + k), py(z0))
            lineTo(px(x0), py(z0 - k))
            lineTo(px(x0 + k), py(z0 - k))
        }
        drawPath(
            zPath,
            INK,
            alpha = a,
            style = Stroke(width = pw(0.026f + 0.006f * u), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Zeichnen in Einheiten des Sichtfensters
// ---------------------------------------------------------------------------------------------

/**
 * Doktorhut (seit 13.0 neu): eine Kappe, die sich um die Kugel legt, darauf ein breites Brett (breiter als der
 * Kopf, flach von vorn oben gesehen, mit sichtbarer Kante), in der Mitte ein Knopf, die Kordel läuft zur rechten
 * Ecke und die Quaste hängt neben dem Kopf herab. Leicht schräg aufgesetzt. Auf dunklem Grund mit feinem hellen
 * Rand, damit das dunkle Brett nicht im Hintergrund verschwindet.
 * Gezeichnet im 280er-Raster der Lumi-Grafik (Kugelmitte 140/105, Radius 68, Kolben oben bei y = 37).
 */
private fun DrawScope.drawMortarboard(tassel: Color, dark: Boolean) {
    val k = size.width / 280f
    fun o(x: Float, y: Float) = Offset(x * k, y * k)
    val ink = Color(0xFF23222F)
    val inkTop = Color(0xFF3A3854)
    val edge = Color(0xFF15141D)
    val rim = if (dark) ARM_RIM else null
    val rimStroke = Stroke(2.4f * k, join = StrokeJoin.Round)
    // Seit 13.1: Quaste links (gespiegelt), Brett flacher und breiter
    withTransform({
        scale(-1f, 1f, pivot = o(140f, 0f))
        rotate(7f, pivot = o(140f, 52f))
    }) {
        // Kappe: Kreis etwas größer als der Kopf, unten in einem flachen Bogen abgeschnitten
        val cx = 140f
        val cy = 105f
        val r = 71f
        val bot = 60f
        val xb = kotlin.math.sqrt(r * r - (bot - cy) * (bot - cy))
        val a0 = Math.toDegrees(atan2((bot - cy).toDouble(), (-xb).toDouble())).toFloat()
        val a1 = Math.toDegrees(atan2((bot - cy).toDouble(), xb.toDouble())).toFloat()
        val sweep = ((a1 - a0) % 360f + 360f) % 360f
        val cap = Path().apply {
            moveTo((cx - xb) * k, bot * k)
            arcTo(Rect((cx - r) * k, (cy - r) * k, (cx + r) * k, (cy + r) * k), a0, sweep, false)
            quadraticBezierTo(cx * k, (bot + 7f) * k, (cx - xb) * k, bot * k)
            close()
        }
        if (rim != null) drawPath(cap, rim, style = rimStroke)
        drawPath(cap, ink)
        drawPath(
            Path().apply {
                moveTo((cx - xb * 0.7f) * k, (bot - 3f) * k)
                quadraticBezierTo(cx * k, (bot + 3f) * k, (cx + xb * 0.7f) * k, (bot - 3f) * k)
            },
            Color.White.copy(alpha = 0.14f),
            style = Stroke(1.8f * k, cap = StrokeCap.Round),
        )
        // Brett: flache Raute mit Kante darunter
        val board = Path().apply {
            moveTo(54f * k, 36f * k)
            lineTo(140f * k, 24f * k)
            lineTo(226f * k, 36f * k)
            lineTo(140f * k, 48f * k)
            close()
        }
        val thick = 4f * k
        if (rim != null) {
            withTransform({ translate(0f, thick) }) { drawPath(board, rim, style = rimStroke) }
            drawPath(board, rim, style = rimStroke)
        }
        withTransform({ translate(0f, thick) }) { drawPath(board, edge) }
        drawPath(board, inkTop)
        drawPath(
            Path().apply {
                moveTo(59f * k, 35.5f * k)
                lineTo(140f * k, 25.2f * k)
                lineTo(221f * k, 35.5f * k)
            },
            Color.White.copy(alpha = 0.19f),
            style = Stroke(1.4f * k, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        // Knopf, Kordel zur rechten Ecke, dann hängt sie neben dem Kopf herab
        val cord = Path().apply {
            moveTo(140f * k, 36f * k)
            quadraticBezierTo(187f * k, 34f * k, 223f * k, 37f * k)
            quadraticBezierTo(227f * k, 55f * k, 225f * k, 74f * k)
        }
        drawPath(cord, tassel, style = Stroke(2.6f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(tassel, 4.2f * k, o(140f, 36f))
        drawCircle(Color.White.copy(alpha = 0.33f), 1.4f * k, o(138.8f, 34.8f))
        // Quaste: Knoten und gefächerte Fransen
        val tx = 225f
        val ty = 74f
        val len = 19f
        drawRoundRect(tassel, topLeft = o(tx - 4.5f, ty - 2f), size = Size(9f * k, 5f * k), cornerRadius = CornerRadius(2f * k))
        val tuft = Path().apply {
            moveTo((tx - 4f) * k, (ty + 2f) * k)
            lineTo((tx + 4f) * k, (ty + 2f) * k)
            lineTo((tx + 6.5f) * k, (ty + 2f + len) * k)
            quadraticBezierTo(tx * k, (ty + 5f + len) * k, (tx - 6.5f) * k, (ty + 2f + len) * k)
            close()
        }
        drawPath(tuft, tassel)
        for (dx in floatArrayOf(-2.6f, 0f, 2.6f)) {
            drawLine(Color.Black.copy(alpha = 0.2f), o(tx + dx * 0.7f, ty + 5f), o(tx + dx * 1.6f, ty + len), strokeWidth = 0.9f * k)
        }
    }
}

private fun DrawScope.px(x: Float) = (x + 1.4f) / 2.8f * size.width
private fun DrawScope.py(z: Float) = (2.8f - z) / 2.8f * size.height
private fun DrawScope.pw(v: Float) = v / 2.8f * size.width

private fun DrawScope.oval(cx: Float, cz: Float, rx: Float, rz: Float, color: Color, alpha: Float = 1f) {
    if (alpha <= 0.003f || rx <= 0f || rz <= 0f) return
    drawOval(color, Offset(px(cx - rx), py(cz + rz)), Size(pw(2f * rx), pw(2f * rz)), alpha = alpha.coerceAtMost(1f))
}

private fun DrawScope.ovalPath(cx: Float, cz: Float, rx: Float, rz: Float) =
    Path().apply { addOval(Rect(px(cx - rx), py(cz + rz), px(cx + rx), py(cz - rz))) }

private fun DrawScope.line(path: Path, width: Float, color: Color = INK, alpha: Float = 1f) {
    if (alpha <= 0.003f || width <= 0f) return
    drawPath(path, color, alpha = alpha.coerceAtMost(1f), style = Stroke(pw(width), cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.quadPath(x0: Float, z0: Float, cx: Float, cz: Float, x1: Float, z1: Float) = Path().apply {
    moveTo(px(x0), py(z0))
    quadraticBezierTo(px(cx), py(cz), px(x1), py(z1))
}

/** Tropfen mit der Spitze nach oben (Träne, Schweiß). */
private fun DrawScope.dropPath(x: Float, z: Float, r: Float) = Path().apply {
    val a = 32f * PI.toFloat() / 180f
    moveTo(px(x), py(z + 2f * r))
    lineTo(px(x + r * cos(a)), py(z + r * sin(a)))
    arcTo(Rect(px(x - r), py(z + r), px(x + r), py(z - r)), -32f, 244f, false)
    close()
}

private fun DrawScope.drop(x: Float, z: Float, r: Float, outline: Float) {
    val d = dropPath(x, z, r)
    drawPath(d, TEAR)
    oval(x - r * 0.35f, z + r * 0.15f, r * 0.28f, r * 0.34f, Color.White, 0.9f)
    line(d, outline)
}

private fun DrawScope.drawArmLines(a: FloatArray, rim: Color? = null) {
    for (side in 0..1) {
        val sx = if (side == 0) -1f else 1f
        val o = side * ARM_SIZE
        val p = Path().apply {
            moveTo(px(sx * 0.40f), py(1.02f))
            cubicTo(
                px(sx * a[o + A_C1X]), py(a[o + A_C1Z]),
                px(sx * a[o + A_C2X]), py(a[o + A_C2Z]),
                px(sx * a[o + A_EX]), py(a[o + A_EZ]),
            )
        }
        if (rim != null) line(p, ARM_W + 2f * ARM_RIM_W, rim)
        line(p, ARM_W)
    }
}

/**
 * Lumis Hände als weiße Cartoon-Handschuhe (wie tools/lumi_hands.py): Handfläche, drei dicke Finger und
 * ein Daumen, der zur Körpermitte zeigt. Hält Lumi das Buch, liegt die Handfläche an der Buchkante und
 * der Daumen vorne auf dem Einband. Zwischen beidem wird weich überblendet ([A_FA]).
 */
private fun DrawScope.drawHands(a: FloatArray, rim: Color? = null) {
    for (side in 0..1) {
        val sx = if (side == 0) -1f else 1f
        val o = side * ARM_SIZE
        val hold = a[o + A_FA].coerceIn(0f, 1f)
        if (hold < 0.999f) {
            // freie Hand: Richtung des Unterarms, Spreizung nach Höhe der Hand
            val ex = sx * a[o + A_EX]
            val ez = a[o + A_EZ]
            val ang = atan2(ez - a[o + A_C2Z], ex - sx * a[o + A_C2X])
            val open = smooth(0.60f, 0.88f, a[o + A_HZ])
            val inward = if (-sin(ang) * sx > 0f) -1f else 1f
            val s = GLOVE_SIZE
            val wx = ex - 0.25f * s * cos(ang)
            val wz = ez - 0.25f * s * sin(ang)
            drawGlove(gloveParts(wx, wz, ang, s, open, inward), 0.26f * s, rim, 1f - hold, gloveCreases(wx, wz, ang, s, inward))
        }
        if (hold > 0.001f) {
            val hx = sx * a[o + A_HX]
            val hz = a[o + A_HZ]
            val fx = sx * a[o + A_FX]
            val fz = a[o + A_FZ]
            val palm = rotEllipse(hx, hz, a[o + A_HRX] * 0.95f, a[o + A_HRZ] * 0.92f, 0f)
            val ta = atan2(fz - hz, fx - hx)
            val thumb = capsule(hx + 0.30f * a[o + A_HRX] * cos(ta), hz + 0.30f * a[o + A_HRX] * sin(ta), fx, fz, a[o + A_FRX] * 1.05f)
            drawGlove(listOf(palm, thumb), 0.26f * a[o + A_HRX] * 1.25f, rim, hold, emptyList())
        }
    }
}

/** Dicke der Arme (Lumi-Einheiten). Seit 13.0 etwas schlanker, dafür größere Handschuhe. */
private const val ARM_W = 0.075f
private const val GLOVE_SIZE = 0.117f
private val GLOVE = LumiColors.Glove

/** Erst alle Umrisse, dann alle Flächen: so bekommen die Teile einen gemeinsamen Rand. */
private fun DrawScope.drawGlove(parts: List<Path>, outline: Float, rim: Color?, alpha: Float, creases: List<Path>) {
    if (alpha <= 0.003f) return
    if (rim != null) parts.forEach { line(it, outline + 2f * ARM_RIM_W, rim, alpha) }
    parts.forEach { line(it, outline, INK, alpha) }
    parts.forEach { drawPath(it, GLOVE, alpha = alpha.coerceAtMost(1f)) }
    creases.forEach { line(it, outline * 0.55f, INK, alpha) }
}

/** Handfläche, drei Finger und Daumen am Handgelenk (x, z); [ang] = Richtung des Unterarms. */
private fun DrawScope.gloveParts(x: Float, z: Float, ang: Float, s: Float, open: Float, inward: Float): List<Path> {
    val ca = cos(ang)
    val sa = sin(ang)
    fun pu(u: Float, v: Float) = x + u * ca - v * sa * inward
    fun pv(u: Float, v: Float) = z + u * sa + v * ca * inward
    val parts = ArrayList<Path>(5)
    parts += rotEllipse(pu(0.50f * s, 0f), pv(0.50f * s, 0f), 0.56f * s, 0.54f * s, ang)
    for (k in -1..1) {
        val phi = k * (8f + 14f * open) * PI.toFloat() / 180f
        val bu = 0.74f * s
        val bv = k * 0.34f * s
        val len = (0.24f + 0.24f * open) * s * (if (k != 0) 0.88f else 1f)
        val tu = bu + len * cos(phi)
        val tv = bv + len * sin(phi)
        parts += capsule(pu(bu, bv), pv(bu, bv), pu(tu, tv), pv(tu, tv), 0.215f * s)
    }
    val th = (48f + 34f * open) * PI.toFloat() / 180f
    val bu = 0.40f * s
    val bv = 0.38f * s
    val len = (0.30f + 0.14f * open) * s
    parts += capsule(pu(bu, bv), pv(bu, bv), pu(bu + len * cos(th), bv + len * sin(th)), pv(bu + len * cos(th), bv + len * sin(th)), 0.21f * s)
    return parts
}

/** Zwei kurze Falten zwischen den Fingern. */
private fun DrawScope.gloveCreases(x: Float, z: Float, ang: Float, s: Float, inward: Float): List<Path> {
    val ca = cos(ang)
    val sa = sin(ang)
    fun p(u: Float, v: Float) = Offset(px(x + u * ca - v * sa * inward), py(z + u * sa + v * ca * inward))
    return listOf(-0.165f, 0.165f).map { v ->
        val a = p(0.62f * s, v * s)
        val b = p(0.86f * s, v * s * 1.15f)
        Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y) }
    }
}

/** Kapsel (Strich mit runden Enden) als Fläche, in Lumi-Einheiten. */
private fun DrawScope.capsule(x0: Float, z0: Float, x1: Float, z1: Float, r: Float, n: Int = 8): Path {
    val a = atan2(z1 - z0, x1 - x0)
    val half = PI.toFloat() / 2f
    return Path().apply {
        for (i in 0..n) {
            val t = a - half + PI.toFloat() * i / n
            val X = px(x1 + r * cos(t))
            val Z = py(z1 + r * sin(t))
            if (i == 0) moveTo(X, Z) else lineTo(X, Z)
        }
        for (i in 0..n) {
            val t = a + half + PI.toFloat() * i / n
            lineTo(px(x0 + r * cos(t)), py(z0 + r * sin(t)))
        }
        close()
    }
}

/** Gedrehte Ellipse als Fläche, in Lumi-Einheiten. */
private fun DrawScope.rotEllipse(cx: Float, cz: Float, rx: Float, rz: Float, a: Float, n: Int = 28): Path {
    val c = cos(a)
    val s = sin(a)
    return Path().apply {
        for (i in 0 until n) {
            val t = 2f * PI.toFloat() * i / n
            val X = px(cx + rx * cos(t) * c - rz * sin(t) * s)
            val Z = py(cz + rx * cos(t) * s + rz * sin(t) * c)
            if (i == 0) moveTo(X, Z) else lineTo(X, Z)
        }
        close()
    }
}

private fun DrawScope.drawFace(f: FloatArray, shutExtra: Float, t: Float, wink: Float = 0f, lidColor: Color = BULB) {
    // Wangen (bei Verlegenheit mit kleinen Rotschimmer-Strichen)
    for (sx in floatArrayOf(-1f, 1f)) {
        oval(sx * CHEEK_X, f[F_CHZ], f[F_CHRX], f[F_CHRZ], CHEEK, f[F_CHA])
        if (f[F_BLUSHL] > 0.01f) {
            for (k in -1..1) {
                val x0 = sx * CHEEK_X + k * 0.38f * f[F_CHRX]
                val p = Path().apply {
                    moveTo(px(x0 + 0.018f), py(f[F_CHZ] + 0.026f))
                    lineTo(px(x0 - 0.018f), py(f[F_CHZ] - 0.026f))
                }
                line(p, 0.013f, BLUSH_LINE, 0.9f * f[F_BLUSHL])
            }
        }
    }

    // Mund: zwei Bögen zwischen den Mundwinkeln. Liegen sie aufeinander, ist es ein Strich, sonst ein offener Mund.
    val mx = f[F_MX]
    val mz = f[F_MZ]
    val normal = 1f - f[F_MCAT].coerceIn(0f, 1f)
    val mouth = Path().apply {
        moveTo(px(-mx), py(mz))
        quadraticBezierTo(px(0f), py(f[F_MTOP]), px(mx), py(mz))
        quadraticBezierTo(px(0f), py(f[F_MBOT]), px(-mx), py(mz))
        close()
    }
    if (f[F_MFILL] > 0.01f) {
        drawPath(mouth, MOUTH, alpha = (f[F_MFILL] * normal).coerceIn(0f, 1f))
        val zb = mz + (f[F_MBOT] - mz) / 2f
        clipPath(mouth) { oval(0f, zb + 0.018f, mx * 0.55f, 0.026f, TONGUE, f[F_MTONGUE] * f[F_MFILL] * normal) }
    }
    line(mouth, f[F_MW], alpha = normal)
    if (f[F_MCAT] > 0.01f) {
        // Katzenmund ω
        val d = 0.026f
        val cat = Path().apply {
            moveTo(px(-mx), py(mz + 0.005f))
            quadraticBezierTo(px(-mx / 2f), py(mz - 1.9f * d), px(0f), py(mz + 0.003f))
            quadraticBezierTo(px(mx / 2f), py(mz - 1.9f * d), px(mx), py(mz + 0.005f))
        }
        line(cat, f[F_MW], alpha = f[F_MCAT])
    }

    // Augen (beim Zwinkern geht nur das rechte zu)
    val shutBoth = maxOf(f[F_SHUT], shutExtra).coerceIn(0f, 1f)
    val squint = f[F_SQUINT].coerceIn(0f, 1f)
    for (sx in floatArrayOf(-1f, 1f)) {
        val winkHere = if (sx > 0f) wink.coerceIn(0f, 1f) else 0f
        val shut = maxOf(shutBoth, winkHere)
        val ex = sx * EYE_X + f[F_DX]
        val ez = EYE_Z + f[F_DZ]
        val rx = f[F_RX]
        val rz = f[F_RZ] * (1f - shut)
        if (rz > 0.004f) {
            val eye = ovalPath(ex, ez, rx, rz)
            drawPath(eye, INK)
            val k = 1f - shut
            clipPath(eye) {
                oval(ex, ez - rz * 0.62f, rx * 0.78f, rz * 0.48f, EYE_LOW, f[F_LOWA]) // lila Schimmer unten
                oval(ex, ez - rz * 0.95f, rx * 0.95f, rz * 0.6f, TEAR, f[F_RIM] * 0.8f) // Tränen stehen in den Augen
            }
            oval(ex + f[F_H1X], ez + f[F_H1Z] * k, f[F_H1R] * k, f[F_H1R] * k, Color.White)
            oval(ex + f[F_H2X], ez + f[F_H2Z] * k, f[F_H2R] * k, f[F_H2R] * k, Color.White)
            if (f[F_SPARK] > 0.01f && k > 0.5f) {
                // kleiner Stern neben dem großen Glanzpunkt, funkelt leicht
                val tw = 0.85f + 0.15f * sin(TWO_PI * t * 0.9f + sx)
                star(ex + f[F_H1X] - 0.052f, ez + f[F_H1Z] + 0.04f, 0.022f * tw, Color.White, f[F_SPARK] * k)
            }
            // Lid in Kolbenfarbe über dem oberen Augenrand
            val ox = ex + sx * (rx + 0.02f)
            val ix = ex - sx * (rx + 0.02f)
            val lid = Path().apply {
                moveTo(px(ox), py(ez + f[F_LIDO]))
                quadraticBezierTo(px(ex), py(ez + (f[F_LIDO] + f[F_LIDI]) / 2f + f[F_LIDA]), px(ix), py(ez + f[F_LIDI]))
                lineTo(px(ix), py(ez + 0.4f))
                lineTo(px(ox), py(ez + 0.4f))
                close()
            }
            clipPath(eye) { drawPath(lid, lidColor) }
        }
        // geschlossene Augen: Bogen (je nach Gesicht ∪ oder ∩), beim Kichern > <
        val arc = smooth(0.55f, 1f, shut) * (1f - squint)
        val hw = 0.087f
        if (winkHere > 0.01f) {
            // Zwinkern: fröhlicher Bogen ∩
            line(quadPath(ex - hw, ez - 0.035f, ex, ez + 0.111f, ex + hw, ez - 0.035f), 0.042f, alpha = smooth(0.55f, 1f, winkHere))
            val rest = arc * (1f - winkHere)
            if (rest > 0.003f) line(quadPath(ex - hw, ez + f[F_ARCE], ex, ez + f[F_ARCC], ex + hw, ez + f[F_ARCE]), f[F_ARCW], alpha = rest)
        } else if (arc > 0.003f) {
            line(quadPath(ex - hw, ez + f[F_ARCE], ex, ez + f[F_ARCC], ex + hw, ez + f[F_ARCE]), f[F_ARCW], alpha = arc)
        }
        if (squint > 0.01f) {
            val o = if (sx < 0f) 1f else -1f // linkes Auge zeigt zur Nase (>), rechtes auch (<)
            val bx = 0.102f
            val bz = 0.117f
            val chevron = Path().apply {
                moveTo(px(ex - o * 0.70f * bx), py(ez + 0.55f * bz))
                lineTo(px(ex + o * 0.62f * bx), py(ez + 0.02f * bz))
                lineTo(px(ex - o * 0.70f * bx), py(ez - 0.55f * bz))
            }
            line(chevron, 0.044f, alpha = squint)
        }
        if (f[F_BROWA] > 0.01f) {
            line(
                quadPath(ex + sx * 0.10f, ez + f[F_BROWO], ex + sx * 0.012f, ez + f[F_BROWC], ex - sx * 0.08f, ez + f[F_BROWI]),
                0.030f,
                alpha = f[F_BROWA],
            )
        }
    }

    // Schweißtropfen (wächst statt einzublenden, sonst wirkt Blau über Gelb grünlich)
    val sw = SmoothStep.transform(f[F_SWEAT].coerceIn(0f, 1f))
    val sr = 0.05f * sw
    if (sr >= 0.006f) drop(0.62f, 1.86f - 0.02f * (1f - sw), sr, 0.02f * smooth(0.006f, 0.03f, sr))

    // Tränen: bilden sich am Augenrand, laufen immer schneller die Wange herunter und tropfen ab
    val tears = f[F_TEARS]
    if (tears > 0.01f) {
        for ((sx, off) in listOf(1f to 0f, -1f to 0.75f)) {
            val u = ((t + off) / 1.5f) % 1f
            val ex = sx * EYE_X + f[F_DX]
            val ez = EYE_Z + f[F_DZ]
            val x0 = ex + sx * 0.06f
            val z0 = ez - 0.06f
            var x = x0
            var z = z0
            var r: Float
            if (u < 0.28f) {
                r = 0.034f * SmoothStep.transform(u / 0.28f)
            } else {
                val g = (u - 0.28f) / 0.72f
                x = x0 + sx * 0.04f * g
                z = z0 - 0.30f * g * g
                r = 0.034f * (1f - smooth(0.80f, 1f, g))
            }
            r *= SmoothStep.transform(tears.coerceIn(0f, 1f))
            if (r >= 0.006f) drop(x, z, r, 0.018f * smooth(0.006f, 0.022f, r))
        }
    }
}

/** Kleiner Stern mit vier Zacken (Funkeln in den Augen). */
private fun DrawScope.star(cx: Float, cz: Float, r: Float, color: Color, alpha: Float) {
    if (alpha <= 0.003f) return
    val p = Path()
    for (i in 0 until 8) {
        val a = (90f + i * 45f) * PI.toFloat() / 180f
        val rr = if (i % 2 == 0) r else r * 0.3f
        val x = px(cx + rr * cos(a))
        val y = py(cz + rr * sin(a))
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color, alpha = alpha.coerceAtMost(1f))
}

/**
 * Runde Brille: zwei Gläser um die Augen, Steg in der Mitte, kurze Bügel nach außen.
 * Feiner Glanz oben links in jedem Glas. Sitzt fest am Kopf (folgt nicht dem Blick).
 */

/** Zeichnet in den Koordinaten des Gesichts (eingepasst in die neue Kugel). */
private inline fun DrawScope.inFace(block: DrawScope.() -> Unit) = upper(0f, block)

/** Zeichnet in den Koordinaten von Armen, Händen und Buch (etwas höher als das Gesicht). */
private inline fun DrawScope.inHold(block: DrawScope.() -> Unit) = upper(HOLD_LIFT, block)

private inline fun DrawScope.upper(lift: Float, block: DrawScope.() -> Unit) {
    val dy = -pw(BODY_CZ - FACE_C0 + lift)
    val pivot = Offset(px(0f), py(FACE_C0))
    withTransform({
        translate(top = dy)
        scale(UPPER_K, UPPER_K, pivot)
    }, block)
}

/** Wie der Glühdraht aussieht, wenn Lumis Licht ausgeht (Einstellung bei Lumi). */
enum class FilamentStyle(val id: String, private val de: String, private val en: String) {
    Spiral("spiral", "Spirale", "Spiral"),
    Classic("classic", "Klassisch", "Classic"),
    Coil("coil", "Wendel", "Coil"),
    Heart("heart", "Herz", "Heart");

    val label: String get() = tr(de, en)

    companion object {
        fun byId(id: String?): FilamentStyle = entries.firstOrNull { it.id == id } ?: Spiral
    }
}

/** Der gewählte Glühdraht, überall abrufbar. */
val LocalFilamentStyle = androidx.compose.runtime.compositionLocalOf { FilamentStyle.Spiral }

private val WIRE = LumiColors.Wire
private val WIRE_BACK = LumiColors.WireBack
private val GLASS_EDGE = LumiColors.GlassEdge
private val FIL_WHITE = LumiColors.FilamentWhite
private val HALO_OUT = LumiColors.HaloOut
private val HALO_IN = LumiColors.HaloIn

/** Farbe des eigentlichen Glühfadens: grau, glühend fast weiß. */
private fun filColor(light: Float) = androidx.compose.ui.graphics.lerp(FILAMENT_OFF, FIL_WHITE, (light * 2.2f).coerceIn(0f, 1f))

/** warmer Schein um einen glühenden Faden */
private fun DrawScope.halo(path: Path, light: Float, w: Float) {
    if (light <= 0.03f) return
    val k = (light * 2.2f).coerceAtMost(1f)
    line(path, w * 1.4f, HALO_OUT, 0.16f * k)
    line(path, w * 0.9f, GLOW_RAY, 0.26f * k)
    line(path, w * 0.55f, HALO_IN, 0.45f * k)
}

/** Glasfuß wie bei einer echten Birne: unten ein kleiner Kelch, dann ein dünner Glasstab, oben evtl. ein Knopf. */
private fun DrawScope.glassStem(top: Float, button: Float?, a: Float) {
    val z0 = NECK_Z + 0.035f
    fun side(sx: Float) = Path().apply {
        moveTo(px(sx * 0.17f), py(z0))
        quadraticBezierTo(px(sx * 0.13f), py(z0 + 0.06f), px(sx * 0.06f), py(z0 + 0.13f))
        quadraticBezierTo(px(sx * 0.035f), py(z0 + 0.17f), px(sx * 0.032f), py(z0 + 0.24f))
        lineTo(px(sx * 0.026f), py(top - 0.01f))
    }
    val shape = Path().apply {
        moveTo(px(0.17f), py(z0))
        quadraticBezierTo(px(0.13f), py(z0 + 0.06f), px(0.06f), py(z0 + 0.13f))
        quadraticBezierTo(px(0.035f), py(z0 + 0.17f), px(0.032f), py(z0 + 0.24f))
        lineTo(px(0.026f), py(top - 0.01f))
        lineTo(px(0f), py(top + 0.005f))
        lineTo(px(-0.026f), py(top - 0.01f))
        lineTo(px(-0.032f), py(z0 + 0.24f))
        quadraticBezierTo(px(-0.035f), py(z0 + 0.17f), px(-0.06f), py(z0 + 0.13f))
        quadraticBezierTo(px(-0.13f), py(z0 + 0.06f), px(-0.17f), py(z0))
        close()
    }
    drawPath(shape, Color.White, alpha = 0.40f * a)
    line(side(1f), 0.013f, GLASS_EDGE, 0.9f * a)
    line(side(-1f), 0.013f, GLASS_EDGE, 0.9f * a)
    line(Path().apply { moveTo(px(0.010f), py(z0 + 0.20f)); lineTo(px(0.010f), py(top - 0.04f)) }, 0.010f, Color.White, 0.8f * a)
    if (button != null) {
        oval(0f, button, 0.048f, 0.034f, Color.White, 0.75f * a)
        line(ovalPath(0f, button, 0.048f, 0.034f), 0.012f, GLASS_EDGE, 0.9f * a)
    }
}

/**
 * Glühdraht im Inneren (Varianten wie in tools/filament.py), sitzt im Hals unter dem Mund.
 * [light] 1 = glüht hell (beim Flackern), 0 = aus, dann als feiner grauer Draht.
 */
private fun DrawScope.drawFilament(style: FilamentStyle, light: Float) {
    val a = 1f - 0.5f * light          // Glasfuß und Drähte treten bei hellem Licht zurück
    val col = filColor(light)
    when (style) {
        FilamentStyle.Spiral -> {
            glassStem(top = 1.02f, button = null, a = a)
            val z0 = 1.04f
            val z1 = 1.42f
            val r = 0.092f
            val turns = 3.5f
            line(Path().apply { moveTo(px(0f), py(1.0f)); lineTo(px(0f), py(z1 + 0.01f)) }, 0.013f, WIRE, 0.9f * a)
            for (sx in floatArrayOf(-1f, 1f)) {
                line(quadPath(sx * 0.02f, 0.98f, sx * 0.07f, 1.0f, sx * 0.085f, 1.03f), 0.014f, WIRE, a)
            }
            val n = 220
            val all = Path()
            val back = Path()
            val front = Path()
            var lastFront: Boolean? = null
            for (i in 0..n) {
                val tt = i / n.toFloat() * turns * TWO_PI
                val x = px(r * sin(tt))
                val y = py(z0 + (z1 - z0) * i / n + 0.024f * cos(tt))
                if (i == 0) all.moveTo(x, y) else all.lineTo(x, y)
                val isFront = cos(tt) >= -0.05f
                val target = if (isFront) front else back
                if (lastFront != isFront) target.moveTo(x, y) else target.lineTo(x, y)
                lastFront = isFront
            }
            halo(all, light, 0.13f)
            line(back, 0.013f, androidx.compose.ui.graphics.lerp(col, WIRE_BACK, if (light < 0.3f) 0.35f else 0.15f), a.coerceAtLeast(0.6f))
            line(front, 0.021f, col)
        }
        FilamentStyle.Classic -> {
            glassStem(top = 1.30f, button = 1.12f, a = a)
            val zt = 1.43f
            for (sx in floatArrayOf(-1f, 1f)) {
                line(quadPath(sx * 0.030f, 1.03f, sx * 0.10f, 1.20f, sx * 0.205f, zt), 0.017f, WIRE, a)
                line(Path().apply { moveTo(px(0f), py(1.29f)); lineTo(px(sx * 0.075f), py(1.40f)) }, 0.011f, WIRE, 0.9f * a)
            }
            val fil = Path().apply {
                moveTo(px(-0.205f), py(zt))
                cubicTo(px(-0.14f), py(1.392f), px(-0.075f), py(1.412f), px(0f), py(1.388f))
                cubicTo(px(0.075f), py(1.412f), px(0.14f), py(1.392f), px(0.205f), py(zt))
            }
            halo(fil, light, 0.12f)
            line(fil, 0.020f, col)
        }
        FilamentStyle.Coil -> {
            glassStem(top = 1.12f, button = 1.07f, a = a)
            val zc = 1.37f
            val w = 0.13f
            val loops = 4
            val amp = 0.032f
            for (sx in floatArrayOf(-1f, 1f)) line(quadPath(sx * 0.032f, 1.06f, sx * 0.07f, 1.25f, sx * w, zc), 0.017f, WIRE, a)
            val c = 2f * w / (TWO_PI * loops)
            val coil = Path()
            val n = 240
            for (i in 0..n) {
                val tt = i / n.toFloat() * TWO_PI * loops
                val x = -w + c * tt - amp * sin(tt)
                val z = zc + amp * 0.45f * (1f - cos(tt)) + 0.004f * sin(PI.toFloat() * i / n)
                if (i == 0) coil.moveTo(px(x), py(z)) else coil.lineTo(px(x), py(z))
            }
            halo(coil, light, 0.11f)
            line(coil, 0.015f, col)
        }
        FilamentStyle.Heart -> {
            glassStem(top = 1.13f, button = 1.08f, a = a)
            val cz = 1.335f
            val s = 0.0105f
            for (sx in floatArrayOf(-1f, 1f)) line(quadPath(sx * 0.030f, 1.08f, sx * 0.12f, 1.16f, sx * 0.150f, 1.33f), 0.017f, WIRE, a)
            val heart = Path()
            val n = 120
            for (i in 0..n) {
                val tt = PI.toFloat() * (i / n.toFloat()) * 2f - PI.toFloat()
                val sn = sin(tt)
                val x = 16f * sn * sn * sn
                val z = 13f * cos(tt) - 5f * cos(2f * tt) - 2f * cos(3f * tt) - cos(4f * tt)
                if (i == 0) heart.moveTo(px(x * s), py(cz + z * s)) else heart.lineTo(px(x * s), py(cz + z * s))
            }
            heart.close()
            halo(heart, light, 0.12f)
            line(heart, 0.020f, col)
        }
    }
}

/**
 * Kleine Vorschau einer Glühdraht-Variante für die Einstellungen: ausgeschaltete Birne, nah herangeholt.
 * [glowing] zeigt den Draht wie beim Flackern.
 */
@Composable
fun FilamentPreview(style: FilamentStyle, glowing: Boolean, modifier: Modifier = Modifier) {
    val light by animateFloatAsState(if (glowing) 0.45f else 0f, tween(500), label = "filPreview")
    Box(modifier.clipToBounds()) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.55f
                    scaleY = 1.55f
                    transformOrigin = TransformOrigin(0.5f, (2.8f - 1.42f) / 2.8f)
                    translationY = 0.06f * size.height
                }
        ) {
            Image(
                painter = painterResource(R.drawable.lumi_body),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = light },
            )
            Image(
                painter = painterResource(R.drawable.lumi_body_off),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 1f - light },
            )
            Canvas(Modifier.fillMaxSize()) { drawFilament(style, light) }
        }
    }
}

/** Acht kurze Lichtstrahlen rund um die Kugel. */
private fun DrawScope.drawRays(k: Float) {
    if (k <= 0.01f) return
    val r0 = 0.67f + 0.10f
    val r1 = r0 + 0.16f * k
    for (i in 0 until 8) {
        val a = (i * 45f + 22.5f) * PI.toFloat() / 180f
        val p = Path().apply {
            moveTo(px(r0 * cos(a)), py(BODY_CZ + r0 * sin(a)))
            lineTo(px(r1 * cos(a)), py(BODY_CZ + r1 * sin(a)))
        }
        line(p, 0.035f, GLOW_RAY, k)
    }
}

/** Staubwölkchen links und rechts vom aufgeschlagenen Buch. */
private fun DrawScope.drawDust(tau: Float) {
    if (tau < 0f || tau >= 0.6f) return
    val e = SmoothStep.transform(tau / 0.6f)
    val fade = 0.32f * (1f - e).pow(1.5f)
    for (side in floatArrayOf(-1f, 1f)) {
        oval(BOOK_REST_X + side * (0.47f + 0.16f * e), FLOOR_Z + 0.03f + 0.06f * e, 0.035f + 0.05f * e, 0.035f + 0.04f * e, DUST, fade)
        oval(BOOK_REST_X + side * (0.40f + 0.16f * e * 0.7f), FLOOR_Z + 0.09f + 0.06f * e, 0.025f + 0.05f * e, 0.025f + 0.04f * e, DUST, fade)
    }
}

// ---------------------------------------------------------------------------------------------
// Konfetti-Fontäne
// ---------------------------------------------------------------------------------------------

private class Piece(
    val vx: Float,
    val vy: Float,
    val w: Float,
    val h: Float,
    val spin: Float,
    val flutter: Float,
    val phase: Float,
    val color: Color,
    val round: Boolean,
    val delay: Float,
)

/**
 * Konfetti, das hinter Lumi hochschießt und herunterflattert. Startet, sobald [play] true wird.
 * Darf über den eigenen Rahmen hinaus zeichnen (Größe = Bezugsgröße, z. B. der Ring).
 */
@Composable
fun ConfettiBurst(
    play: Boolean,
    modifier: Modifier = Modifier,
    originY: Float = 0.42f,
) {
    val p = Funke.colors
    val cartoon = Funke.cartoon
    val pieces = remember(p.id) {
        // Lumis Farben (Gelb, Buch-Lila, Wangen-Rosa) plus Akzent des Schemas
        val colors = listOf(
            LumiColors.Bulb, LumiColors.BookViolet, LumiColors.Cheek, LumiColors.Sky,
            LumiColors.Leaf, p.accent, LumiColors.Butter,
        )
        List(90) {
            val angle = (-90f + (Random.nextFloat() * 2f - 1f) * 62f) * (PI.toFloat() / 180f)
            val speed = 1.05f + Random.nextFloat() * 0.85f
            Piece(
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                w = 5f + Random.nextFloat() * 4f,
                h = 9f + Random.nextFloat() * 7f,
                spin = Random.nextFloat() * 2f - 1f,
                flutter = 4f + Random.nextFloat() * 6f,
                phase = Random.nextFloat() * 6.28f,
                color = colors[it % colors.size],
                round = Random.nextFloat() < 0.22f,
                delay = Random.nextFloat() * 0.12f,
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(play) {
        if (play) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(3000, easing = LinearEasing))
        }
    }
    Canvas(modifier) {
        val t = progress.value
        if (t <= 0f || t >= 1f) return@Canvas
        val unit = size.minDimension
        val ox = size.width / 2f
        val oy = size.height * originY
        val outline = if (cartoon) p.ink else null
        pieces.forEach { c ->
            val local = ((t - c.delay) / (1f - c.delay)).coerceIn(0f, 1f)
            if (local <= 0f) return@forEach
            val sec = local * 3f
            // Luftwiderstand: anfangs schnell, dann gleiten sie und schweben herunter
            val drag = (1f - kotlin.math.exp(-2.4f * sec)) / 2.4f
            val x = ox + c.vx * unit * drag + sin(sec * c.flutter * 0.6f + c.phase) * 8f * density * local
            val y = oy + c.vy * unit * drag + 0.16f * unit * sec * sec
            val alpha = if (local > 0.75f) (1f - local) / 0.25f else 1f
            val flip = abs(cos(sec * c.flutter + c.phase))
            val w = c.w * density * (0.25f + 0.75f * flip)
            val h = c.h * density
            rotate(degrees = c.spin * 720f * local, pivot = Offset(x, y)) {
                if (c.round) {
                    drawCircle(c.color.copy(alpha = alpha), radius = c.w * density * 0.55f, center = Offset(x, y))
                } else {
                    drawRoundRect(
                        color = c.color.copy(alpha = alpha),
                        topLeft = Offset(x - w / 2f, y - h / 2f),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(2f * density),
                    )
                    if (outline != null) {
                        drawRoundRect(
                            color = outline.copy(alpha = alpha * 0.8f),
                            topLeft = Offset(x - w / 2f, y - h / 2f),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(2f * density),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(1.2f * density),
                        )
                    }
                }
            }
        }
    }
}
