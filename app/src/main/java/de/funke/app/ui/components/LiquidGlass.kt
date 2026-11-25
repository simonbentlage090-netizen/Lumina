package de.funke.app.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/*
 * Echtes "Liquid Glass" für den Glass-Stil (seit 12.0).
 *
 * Wie bei Apples Liquid Glass sieht man durch die schwebende Leiste den Inhalt dahinter: weichgezeichnet,
 * an den Rändern wie durch gewölbtes Glas gebogen (Lichtbrechung), mit einem Hauch Farbsaum und einer hellen
 * Lichtkante oben. Technisch: Der Inhalt hinter der Leiste wird in eine Grafik-Ebene aufgenommen ([backdropSource]),
 * die Leiste zeichnet genau den Ausschnitt hinter sich noch einmal – weichgezeichnet (ab Android 12) und mit einem
 * kleinen Shader für die Lichtbrechung (ab Android 13). Ältere Geräte bekommen das bisherige milchige Glas.
 *
 * Keine fremde Bibliothek: nur Bordmittel von Android und Compose.
 */

/** Gemeinsamer Hintergrund für Glasflächen: was dahinter liegt, als Grafik-Ebene. */
@Stable
class Backdrop {
    internal var layer: GraphicsLayer? = null
    internal var coords: LayoutCoordinates? = null

    companion object {
        /** Echtes Glas braucht Weichzeichnen auf der Grafikkarte (ab Android 12). */
        val supported: Boolean get() = Build.VERSION.SDK_INT >= 31
    }
}

/** Der Hintergrund, durch den Glasflächen schauen. null = kein echtes Glas möglich. */
val LocalBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/** Merkt sich einen [Backdrop] für die Dauer der Komposition. */
@Composable
fun rememberBackdrop(): Backdrop {
    val layer = rememberGraphicsLayer()
    return remember(layer) { Backdrop().also { it.layer = layer } }
}

/**
 * Nimmt den Inhalt dieses Elements als Hintergrund für Glasflächen auf. Gezeichnet wird er ganz normal;
 * die Aufnahme kostet kaum etwas, weil Android die Zeichenbefehle nur einmal speichert.
 */
fun Modifier.backdropSource(backdrop: Backdrop?): Modifier {
    if (backdrop == null || !Backdrop.supported) return this
    return this
        .onGloballyPositioned { backdrop.coords = it }
        .drawWithContent {
            val layer = backdrop.layer
            if (layer == null) {
                drawContent()
            } else {
                layer.record { this@drawWithContent.drawContent() }
                drawLayer(layer)
            }
        }
}

/** Gibt es hier echtes Glas? (Android 12+ und ein [LocalBackdrop] in der Nähe.) Sonst bleibt das milchige Glas. */
@Composable
fun liquidGlassAvailable(): Boolean = Backdrop.supported && LocalBackdrop.current != null

/**
 * Fläche aus echtem Liquid Glass in der Form [shape]. [base] ist die deckende Grundfarbe darunter (meist die
 * Hintergrundfarbe), damit der scharfe Inhalt hinter der Fläche nicht durchscheint. [tint] färbt das Glas
 * (hell: milchig, dunkel: rauchig). Ohne [LocalBackdrop] oder vor Android 12 ändert sich nichts.
 */
fun Modifier.liquidGlass(
    shape: Shape,
    base: Color,
    tint: Brush,
    blur: Dp = 18.dp,
    bend: Dp = 9.dp,
    rim: Dp = 14.dp,
): Modifier = composed {
    val backdrop = LocalBackdrop.current
    if (backdrop == null || !Backdrop.supported) return@composed this
    val glassLayer = rememberGraphicsLayer()
    val lens = remember { if (Build.VERSION.SDK_INT >= 33) LiquidLens.create() else null }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val cache = remember { EffectCache() }
    this
        .onGloballyPositioned { me ->
            val src = backdrop.coords
            if (src != null && src.isAttached && me.isAttached) {
                val o = runCatching { me.localPositionOf(src, Offset.Zero) }.getOrNull()
                if (o != null && o != offset) offset = o
            }
        }
        .drawBehind {
            val src = backdrop.layer ?: return@drawBehind
            val margin = 24.dp.toPx()
            val w = size.width + 2 * margin
            val h = size.height + 2 * margin
            if (w < 1f || h < 1f) return@drawBehind
            // Den Ausschnitt hinter der Fläche (plus Rand für Weichzeichner und Lichtbrechung) aufnehmen
            glassLayer.record(IntSize(w.roundToInt(), h.roundToInt())) {
                translate(offset.x + margin, offset.y + margin) { drawLayer(src) }
            }
            val outline = shape.createOutline(size, layoutDirection, this)
            val radius = when (outline) {
                is androidx.compose.ui.graphics.Outline.Rounded -> outline.roundRect.topLeftCornerRadius.x
                else -> 0f
            }
            glassLayer.renderEffect = cache.effect(
                lens, w, h, margin, size.width, size.height, radius,
                blur.toPx(), bend.toPx(), rim.toPx(),
            )
            val path = Path().apply { addOutline(outline) }
            clipPath(path) {
                drawRect(base)
                translate(-margin, -margin) { drawLayer(glassLayer) }
                drawRect(tint)
            }
        }
}

/** Baut den Effekt nur neu, wenn sich Größen ändern (RenderEffects sind unveränderlich). */
private class EffectCache {
    private var key: List<Float>? = null
    private var effect: RenderEffect? = null

    fun effect(
        lens: LiquidLens?, w: Float, h: Float, margin: Float, sw: Float, sh: Float, radius: Float,
        blur: Float, bend: Float, rim: Float,
    ): RenderEffect? {
        val k = listOf(w, h, margin, sw, sh, radius, blur, bend, rim)
        if (k == key) return effect
        key = k
        effect = (if (lens != null && Build.VERSION.SDK_INT >= 33) lens.effect(margin, sw, sh, radius, blur, bend, rim) else null)
            ?: BlurEffect(blur, blur, TileMode.Clamp)
        return effect
    }
}

/**
 * Lichtbrechung am Rand (Android 13+): Ein kleiner AGSL-Shader biegt den Inhalt zur Kante hin nach außen,
 * wie bei einem Tropfen Glas, mit einem Hauch Farbsaum. Innen bleibt alles unverzerrt.
 */
private class LiquidLens private constructor(private val shader: RuntimeShader) {

    @RequiresApi(33)
    fun effect(margin: Float, sw: Float, sh: Float, radius: Float, blur: Float, bend: Float, rim: Float): RenderEffect? =
        runCatching {
            shader.setFloatUniform("origin", margin, margin)
            shader.setFloatUniform("glassSize", sw, sh)
            shader.setFloatUniform("radius", radius.coerceAtMost(minOf(sw, sh) / 2f))
            shader.setFloatUniform("rim", rim.coerceAtLeast(1f))
            shader.setFloatUniform("bend", bend)
            val lens = android.graphics.RenderEffect.createRuntimeShaderEffect(shader, "content")
            val soft = android.graphics.RenderEffect.createBlurEffect(blur, blur, android.graphics.Shader.TileMode.CLAMP)
            // Erst weichzeichnen, dann brechen
            android.graphics.RenderEffect.createChainEffect(lens, soft).asComposeRenderEffect()
        }.getOrNull()

    companion object {
        fun create(): LiquidLens? =
            if (Build.VERSION.SDK_INT >= 33) runCatching { LiquidLens(RuntimeShader(SOURCE)) }.getOrNull() else null

        private const val SOURCE = """
uniform shader content;
uniform float2 origin;
uniform float2 glassSize;
uniform float radius;
uniform float rim;
uniform float bend;

float sdRound(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + float2(r, r);
    return length(max(q, float2(0.0, 0.0))) + min(max(q.x, q.y), 0.0) - r;
}

half4 main(float2 coord) {
    float2 hs = glassSize * 0.5;
    float2 p = coord - (origin + hs);
    float d = sdRound(p, hs, radius);
    if (d > 0.0) {
        return content.eval(coord);
    }
    float t = clamp(1.0 + d / rim, 0.0, 1.0);
    float2 ex = float2(1.0, 0.0);
    float2 ey = float2(0.0, 1.0);
    float2 g = float2(sdRound(p + ex, hs, radius) - sdRound(p - ex, hs, radius),
                      sdRound(p + ey, hs, radius) - sdRound(p - ey, hs, radius));
    float gl = length(g);
    float2 n = float2(0.0, 0.0);
    if (gl > 0.0001) {
        n = g / gl;
    }
    float k = t * t * t * bend;
    float2 c = coord + n * k;
    half4 col = content.eval(c);
    half red = content.eval(c + n * (k * 0.10)).r;
    half blue = content.eval(c - n * (k * 0.10)).b;
    return half4(red, col.g, blue, col.a);
}
"""
    }
}
