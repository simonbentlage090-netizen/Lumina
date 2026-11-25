package de.funke.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import de.funke.app.ui.theme.BrandColors

/**
 * Das Lumina-Zeichen: eine Sonne geht hinter einem aufgeschlagenen Buch auf.
 * Genau die Form des App-Symbols (res/drawable/ic_launcher_*.xml), im 108er Raster.
 */
@Composable
fun LuminaLogo(modifier: Modifier = Modifier, alpha: Float = 1f) {
    Canvas(modifier) {
        drawLuminaIcon(center, size.minDimension, LogoShape.Squircle, alpha = alpha)
    }
}

enum class LogoShape { Circle, Squircle }

// Buchseiten und Strahlen wie im App-Symbol (Vordergrund, um 3 nach unten versetzt)
private val PageLeft = PathParser().parsePathString(
    "M52 57.5C45 52.5 37 51.2 30 52C29 52.1 28.5 52.8 28.5 53.8V75C28.5 76.1 29.4 76.9 30.5 76.8C37.5 76.1 45 77.5 52 82Z"
).toPath()
private val PageRight = PathParser().parsePathString(
    "M56 57.5C63 52.5 71 51.2 78 52C79 52.1 79.5 52.8 79.5 53.8V75C79.5 76.1 78.6 76.9 77.5 76.8C70.5 76.1 63 77.5 56 82Z"
).toPath()

/** Strahlen: innerer Punkt → äußerer Punkt */
private val Rays = listOf(
    Offset(40.14f, 41f) to Offset(34.51f, 37.75f),
    Offset(46f, 35.14f) to Offset(42.75f, 29.51f),
    Offset(54f, 33f) to Offset(54f, 26.5f),
    Offset(62f, 35.14f) to Offset(65.25f, 29.51f),
    Offset(67.86f, 41f) to Offset(73.49f, 37.75f),
)

/** Zeitplan wie in res/drawable/splash_icon_animated.xml (in ms). */
const val LOGO_SUN_MS = 520f
const val LOGO_RAY_START_MS = 240f
const val LOGO_RAY_STEP_MS = 45f
const val LOGO_RAY_MS = 260f
const val LOGO_ANIM_MS = 680f   // letzter Strahl fertig

/** Gleiche Kurve wie @interpolator/lumina_ease_out */
private fun easeOut(x: Float): Float =
    androidx.compose.animation.core.CubicBezierEasing(0.22f, 1f, 0.36f, 1f).transform(x.coerceIn(0f, 1f))

/**
 * Zeichnet das Zeichen mit Mitte [c] und Kantenlänge/Durchmesser [d].
 * [ms] = Stand der Aufgeh-Animation (wie beim System-Startbildschirm); ab [LOGO_ANIM_MS] fertig.
 */
fun DrawScope.drawLuminaIcon(
    c: Offset,
    d: Float,
    shape: LogoShape,
    ms: Float = LOGO_ANIM_MS,
    alpha: Float = 1f,
) {
    if (alpha <= 0.001f) return
    // Sichtbarer Bereich des App-Symbols: 72 von 108 Einheiten
    val unit = d / 72f
    val origin = Offset(c.x - 54f * unit, c.y - 54f * unit)
    val clip = Path().apply {
        when (shape) {
            LogoShape.Circle -> addOval(androidx.compose.ui.geometry.Rect(c, d / 2f))
            LogoShape.Squircle -> addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = c.x - d / 2f, top = c.y - d / 2f, right = c.x + d / 2f, bottom = c.y + d / 2f,
                    cornerRadius = CornerRadius(d * 0.30f),
                )
            )
        }
    }
    clipPath(clip) {
        drawRect(
            Brush.verticalGradient(
                listOf(BrandColors.IconTop, BrandColors.IconBottom),
                startY = c.y - d / 2f,
                endY = c.y + d / 2f,
            ),
            topLeft = Offset(c.x - d / 2f, c.y - d / 2f),
            size = Size(d, d),
            alpha = alpha,
        )
        withTransform({
            translate(origin.x, origin.y)
            scale(unit, unit, pivot = Offset.Zero)
        }) {
            // Strahlen wachsen nacheinander nach außen
            Rays.forEachIndexed { i, (a, b) ->
                val start = LOGO_RAY_START_MS + i * LOGO_RAY_STEP_MS
                val t = easeOut((ms - start) / LOGO_RAY_MS)
                if (t > 0.001f) {
                    drawLine(
                        BrandColors.IconSun,
                        a,
                        a + (b - a) * t,
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                        alpha = alpha * ((ms - start) / 120f).coerceIn(0f, 1f),
                    )
                }
            }
            // Sonne geht auf
            val s = easeOut(ms / LOGO_SUN_MS)
            val r = 11f * (0.7f + 0.3f * s)
            drawCircle(BrandColors.IconSun, r, Offset(54f, 49f + 6f * (1f - s)), alpha = alpha)
            // Seiten weiß, mit einem Rand in der Hintergrundfarbe (trennt sie sauber von der Sonne)
            val edge = Brush.verticalGradient(listOf(BrandColors.IconTop, BrandColors.IconBottom), startY = 18f, endY = 90f)
            val rim = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.2f, join = androidx.compose.ui.graphics.StrokeJoin.Round)
            drawPath(PageLeft, Color.White, alpha = alpha)
            drawPath(PageLeft, edge, alpha = alpha, style = rim)
            drawPath(PageRight, Color.White, alpha = alpha)
            drawPath(PageRight, edge, alpha = alpha, style = rim)
        }
    }
}
