//// FILE: androidx/compose/ui/Ui.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.coroutines.CoroutineContext

@Stable interface Modifier {
    fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial
    fun <R> foldOut(initial: R, operation: (Element, R) -> R): R = initial
    fun any(predicate: (Element) -> Boolean): Boolean = false
    fun all(predicate: (Element) -> Boolean): Boolean = true
    infix fun then(other: Modifier): Modifier = other
    interface Element : Modifier
    companion object : Modifier
}
fun Modifier.composed(inspectorInfo: (Any) -> Unit = {}, factory: @Composable Modifier.() -> Modifier): Modifier = TODO()
fun Modifier.composed(key1: Any?, inspectorInfo: (Any) -> Unit = {}, factory: @Composable Modifier.() -> Modifier): Modifier = TODO()
fun Modifier.zIndex(zIndex: Float): Modifier = TODO()

@Stable fun interface Alignment {
    fun align(size: IntSize, space: IntSize, layoutDirection: LayoutDirection): IntOffset
    @Stable fun interface Horizontal { fun align(size: Int, space: Int, layoutDirection: LayoutDirection): Int; operator fun plus(other: Vertical): Alignment = TODO() }
    @Stable fun interface Vertical { fun align(size: Int, space: Int): Int; operator fun plus(other: Horizontal): Alignment = TODO() }
    companion object {
        val TopStart: Alignment get() = TODO()
        val TopCenter: Alignment get() = TODO()
        val TopEnd: Alignment get() = TODO()
        val CenterStart: Alignment get() = TODO()
        val Center: Alignment get() = TODO()
        val CenterEnd: Alignment get() = TODO()
        val BottomStart: Alignment get() = TODO()
        val BottomCenter: Alignment get() = TODO()
        val BottomEnd: Alignment get() = TODO()
        val Top: Vertical get() = TODO()
        val CenterVertically: Vertical get() = TODO()
        val Bottom: Vertical get() = TODO()
        val Start: Horizontal get() = TODO()
        val CenterHorizontally: Horizontal get() = TODO()
        val End: Horizontal get() = TODO()
    }
}
class BiasAlignment(val horizontalBias: Float, val verticalBias: Float) : Alignment {
    override fun align(size: IntSize, space: IntSize, layoutDirection: LayoutDirection): IntOffset = TODO()
    class Horizontal(val bias: Float) : Alignment.Horizontal { override fun align(size: Int, space: Int, layoutDirection: LayoutDirection): Int = TODO() }
    class Vertical(val bias: Float) : Alignment.Vertical { override fun align(size: Int, space: Int): Int = TODO() }
}
interface MotionDurationScale : CoroutineContext.Element {
    val scaleFactor: Float
    override val key: CoroutineContext.Key<*> get() = Key
    companion object Key : CoroutineContext.Key<MotionDurationScale>
}
@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalComposeUiApi
@RequiresOptIn(level = RequiresOptIn.Level.ERROR) annotation class InternalComposeUiApi
//// FILE: androidx/compose/ui/draw/Draw.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.draw

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun Modifier.clip(shape: Shape): Modifier = TODO()
fun Modifier.clipToBounds(): Modifier = TODO()
fun Modifier.alpha(alpha: Float): Modifier = TODO()
fun Modifier.rotate(degrees: Float): Modifier = TODO()
fun Modifier.scale(scale: Float): Modifier = TODO()
fun Modifier.scale(scaleX: Float, scaleY: Float): Modifier = TODO()
fun Modifier.drawBehind(onDraw: DrawScope.() -> Unit): Modifier = TODO()
fun Modifier.drawWithContent(onDraw: ContentDrawScope.() -> Unit): Modifier = TODO()
fun Modifier.drawWithCache(onBuildDrawCache: CacheDrawScope.() -> DrawResult): Modifier = TODO()
fun Modifier.shadow(elevation: Dp, shape: Shape = RectangleShape, clip: Boolean = elevation > 0.dp, ambientColor: Color = DefaultShadowColor, spotColor: Color = DefaultShadowColor): Modifier = TODO()
fun Modifier.blur(radius: Dp, edgeTreatment: BlurredEdgeTreatment = BlurredEdgeTreatment.Rectangle): Modifier = TODO()
fun Modifier.blur(radiusX: Dp, radiusY: Dp, edgeTreatment: BlurredEdgeTreatment = BlurredEdgeTreatment.Rectangle): Modifier = TODO()
class BlurredEdgeTreatment(val shape: Shape?) { companion object { val Rectangle = BlurredEdgeTreatment(RectangleShape); val Unbounded = BlurredEdgeTreatment(null) } }
class DrawResult internal constructor()
class CacheDrawScope internal constructor() : Density {
    override val density: Float get() = TODO()
    override val fontScale: Float get() = TODO()
    val size: Size get() = TODO()
    val layoutDirection: LayoutDirection get() = TODO()
    fun onDrawBehind(block: DrawScope.() -> Unit): DrawResult = TODO()
    fun onDrawWithContent(block: ContentDrawScope.() -> Unit): DrawResult = TODO()
}
//// FILE: androidx/compose/ui/focus/Focus.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.focus

import androidx.compose.ui.Modifier

class FocusRequester {
    fun requestFocus() {}
    fun freeFocus(): Boolean = TODO()
    fun captureFocus(): Boolean = TODO()
    companion object { val Default: FocusRequester = TODO() }
}
fun Modifier.focusRequester(focusRequester: FocusRequester): Modifier = TODO()
interface FocusState { val isFocused: Boolean; val hasFocus: Boolean; val isCaptured: Boolean }
fun Modifier.onFocusChanged(onFocusChanged: (FocusState) -> Unit): Modifier = TODO()
fun Modifier.focusTarget(): Modifier = TODO()
class FocusDirection private constructor() { companion object { val Next: FocusDirection = TODO(); val Previous: FocusDirection = TODO(); val Down: FocusDirection = TODO(); val Up: FocusDirection = TODO() } }
interface FocusManager { fun clearFocus(force: Boolean = false); fun moveFocus(focusDirection: FocusDirection): Boolean }
//// FILE: androidx/compose/ui/graphics/Graphics.kt
@file:Suppress("UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")
package androidx.compose.ui.graphics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Immutable @JvmInline value class Color(val value: ULong) {
    val colorSpace: ColorSpace get() = TODO()
    val red: Float get() = TODO()
    val green: Float get() = TODO()
    val blue: Float get() = TODO()
    val alpha: Float get() = TODO()
    fun convert(colorSpace: ColorSpace): Color = TODO()
    operator fun component1(): Float = red
    operator fun component2(): Float = green
    operator fun component3(): Float = blue
    operator fun component4(): Float = alpha
    operator fun component5(): ColorSpace = colorSpace
    fun copy(alpha: Float = this.alpha, red: Float = this.red, green: Float = this.green, blue: Float = this.blue): Color = TODO()
    companion object {
        val Black: Color get() = TODO()
        val DarkGray: Color get() = TODO()
        val Gray: Color get() = TODO()
        val LightGray: Color get() = TODO()
        val White: Color get() = TODO()
        val Red: Color get() = TODO()
        val Green: Color get() = TODO()
        val Blue: Color get() = TODO()
        val Yellow: Color get() = TODO()
        val Cyan: Color get() = TODO()
        val Magenta: Color get() = TODO()
        val Transparent: Color get() = TODO()
        val Unspecified: Color get() = TODO()
        fun hsv(hue: Float, saturation: Float, value: Float, alpha: Float = 1f, colorSpace: ColorSpace = ColorSpaces.Srgb): Color = TODO()
        fun hsl(hue: Float, saturation: Float, lightness: Float, alpha: Float = 1f, colorSpace: ColorSpace = ColorSpaces.Srgb): Color = TODO()
    }
}
fun Color(red: Float, green: Float, blue: Float, alpha: Float = 1f, colorSpace: ColorSpace = ColorSpaces.Srgb): Color = TODO()
fun Color(color: Int): Color = TODO()
fun Color(color: Long): Color = TODO()
fun Color(red: Int, green: Int, blue: Int, alpha: Int = 0xFF): Color = TODO()
fun lerp(start: Color, stop: Color, fraction: Float): Color = TODO()
fun Color.compositeOver(background: Color): Color = TODO()
fun Color.luminance(): Float = TODO()
fun Color.toArgb(): Int = TODO()
val Color.isSpecified: Boolean get() = TODO()
val Color.isUnspecified: Boolean get() = TODO()
inline fun Color.takeOrElse(block: () -> Color): Color = TODO()
val DefaultShadowColor: Color get() = TODO()
const val DefaultAlpha: Float = 1.0f
const val DefaultCameraDistance: Float = 8.0f

@Immutable abstract class Brush {
    open val intrinsicSize: Size get() = Size.Unspecified
    abstract fun applyTo(size: Size, p: Paint, alpha: Float)
    companion object {
        fun linearGradient(vararg colorStops: Pair<Float, Color>, start: Offset = Offset.Zero, end: Offset = Offset.Infinite, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun linearGradient(colors: List<Color>, start: Offset = Offset.Zero, end: Offset = Offset.Infinite, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun horizontalGradient(colors: List<Color>, startX: Float = 0.0f, endX: Float = Float.POSITIVE_INFINITY, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun horizontalGradient(vararg colorStops: Pair<Float, Color>, startX: Float = 0.0f, endX: Float = Float.POSITIVE_INFINITY, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun verticalGradient(colors: List<Color>, startY: Float = 0.0f, endY: Float = Float.POSITIVE_INFINITY, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun verticalGradient(vararg colorStops: Pair<Float, Color>, startY: Float = 0f, endY: Float = Float.POSITIVE_INFINITY, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun radialGradient(vararg colorStops: Pair<Float, Color>, center: Offset = Offset.Unspecified, radius: Float = Float.POSITIVE_INFINITY, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun radialGradient(colors: List<Color>, center: Offset = Offset.Unspecified, radius: Float = Float.POSITIVE_INFINITY, tileMode: TileMode = TileMode.Clamp): Brush = TODO()
        fun sweepGradient(vararg colorStops: Pair<Float, Color>, center: Offset = Offset.Unspecified): Brush = TODO()
        fun sweepGradient(colors: List<Color>, center: Offset = Offset.Unspecified): Brush = TODO()
    }
}
@Immutable class SolidColor(val value: Color) : Brush() { override fun applyTo(size: Size, p: Paint, alpha: Float) {} }
@Immutable abstract class ShaderBrush : Brush() { abstract fun createShader(size: Size): Shader; override fun applyTo(size: Size, p: Paint, alpha: Float) {} }
fun ShaderBrush(shader: Shader): ShaderBrush = TODO()
typealias Shader = android.graphics.Shader
fun LinearGradientShader(from: Offset, to: Offset, colors: List<Color>, colorStops: List<Float>? = null, tileMode: TileMode = TileMode.Clamp): Shader = TODO()
fun RadialGradientShader(center: Offset, radius: Float, colors: List<Color>, colorStops: List<Float>? = null, tileMode: TileMode = TileMode.Clamp): Shader = TODO()
fun SweepGradientShader(center: Offset, colors: List<Color>, colorStops: List<Float>? = null): Shader = TODO()
fun ImageShader(image: ImageBitmap, tileModeX: TileMode = TileMode.Clamp, tileModeY: TileMode = TileMode.Clamp): Shader = TODO()

@Immutable @JvmInline value class TileMode internal constructor(val value: Int) { companion object { val Clamp = TileMode(0); val Repeated = TileMode(1); val Mirror = TileMode(2); val Decal = TileMode(3) } }
@Immutable @JvmInline value class StrokeCap internal constructor(val value: Int) { companion object { val Butt = StrokeCap(0); val Round = StrokeCap(1); val Square = StrokeCap(2) } }
@Immutable @JvmInline value class StrokeJoin internal constructor(val value: Int) { companion object { val Miter = StrokeJoin(0); val Round = StrokeJoin(1); val Bevel = StrokeJoin(2) } }
@Immutable @JvmInline value class ClipOp internal constructor(val value: Int) { companion object { val Difference = ClipOp(0); val Intersect = ClipOp(1) } }
@Immutable @JvmInline value class FilterQuality internal constructor(val value: Int) { companion object { val None = FilterQuality(0); val Low = FilterQuality(1); val Medium = FilterQuality(2); val High = FilterQuality(3) } }
@Immutable @JvmInline value class PaintingStyle internal constructor(val value: Int) { companion object { val Fill = PaintingStyle(0); val Stroke = PaintingStyle(1) } }
@Immutable @JvmInline value class PointMode internal constructor(val value: Int) { companion object { val Points = PointMode(0); val Lines = PointMode(1); val Polygon = PointMode(2) } }
@Immutable @JvmInline value class PathFillType internal constructor(val value: Int) { companion object { val NonZero = PathFillType(0); val EvenOdd = PathFillType(1) } }
@Immutable @JvmInline value class PathOperation internal constructor(val value: Int) { companion object { val Difference = PathOperation(0); val Intersect = PathOperation(1); val Union = PathOperation(2); val Xor = PathOperation(3); val ReverseDifference = PathOperation(4) } }
@Immutable @JvmInline value class CompositingStrategy internal constructor(val value: Int) { companion object { val Auto = CompositingStrategy(0); val Offscreen = CompositingStrategy(1); val ModulateAlpha = CompositingStrategy(2) } }
@Immutable @JvmInline value class ImageBitmapConfig internal constructor(val value: Int) { companion object { val Argb8888 = ImageBitmapConfig(0); val Alpha8 = ImageBitmapConfig(1); val Rgb565 = ImageBitmapConfig(2); val F16 = ImageBitmapConfig(3); val Gpu = ImageBitmapConfig(4) } }
@Immutable @JvmInline value class BlendMode internal constructor(val value: Int) {
    companion object {
        val Clear = BlendMode(0); val Src = BlendMode(1); val Dst = BlendMode(2); val SrcOver = BlendMode(3); val DstOver = BlendMode(4); val SrcIn = BlendMode(5); val DstIn = BlendMode(6)
        val SrcOut = BlendMode(7); val DstOut = BlendMode(8); val SrcAtop = BlendMode(9); val DstAtop = BlendMode(10); val Xor = BlendMode(11); val Plus = BlendMode(12); val Modulate = BlendMode(13)
        val Screen = BlendMode(14); val Overlay = BlendMode(15); val Darken = BlendMode(16); val Lighten = BlendMode(17); val ColorDodge = BlendMode(18); val ColorBurn = BlendMode(19)
        val Hardlight = BlendMode(20); val Softlight = BlendMode(21); val Difference = BlendMode(22); val Exclusion = BlendMode(23); val Multiply = BlendMode(24); val Hue = BlendMode(25)
        val Saturation = BlendMode(26); val Color = BlendMode(27); val Luminosity = BlendMode(28)
    }
}
@Immutable class ColorFilter internal constructor() {
    companion object {
        fun tint(color: Color, blendMode: BlendMode = BlendMode.SrcIn): ColorFilter = TODO()
        fun colorMatrix(colorMatrix: ColorMatrix): ColorFilter = TODO()
        fun lighting(multiply: Color, add: Color): ColorFilter = TODO()
    }
}
class ColorMatrix(val values: FloatArray = floatArrayOf(1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f)) {
    operator fun get(row: Int, column: Int): Float = TODO()
    operator fun set(row: Int, column: Int, v: Float) {}
    fun reset() {}
    fun set(src: ColorMatrix) {}
    fun setToSaturation(sat: Float) {}
    fun setToScale(redScale: Float, greenScale: Float, blueScale: Float, alphaScale: Float) {}
    fun setToRotateRed(degrees: Float) {}
    fun setToRotateGreen(degrees: Float) {}
    fun setToRotateBlue(degrees: Float) {}
    operator fun timesAssign(colorMatrix: ColorMatrix) {}
}
class Matrix(val values: FloatArray = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)) {
    fun reset() {}
    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {}
    fun scale(x: Float = 1f, y: Float = 1f, z: Float = 1f) {}
    fun rotateZ(degrees: Float) {}
    fun rotateX(degrees: Float) {}
    fun rotateY(degrees: Float) {}
    fun map(point: Offset): Offset = TODO()
    fun map(rect: Rect): Rect = TODO()
    operator fun timesAssign(m: Matrix) {}
}

@Immutable interface Shape { fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline }
val RectangleShape: Shape get() = TODO()
sealed class Outline {
    abstract val bounds: Rect
    class Rectangle(val rect: Rect) : Outline() { override val bounds: Rect get() = rect }
    class Rounded(val roundRect: RoundRect) : Outline() { override val bounds: Rect get() = TODO() }
    class Generic(val path: Path) : Outline() { override val bounds: Rect get() = TODO() }
}
fun Path.addOutline(outline: Outline): Unit = TODO()
fun DrawScope.drawOutline(outline: Outline, color: Color, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DrawScope.DefaultBlendMode): Unit = TODO()
fun DrawScope.drawOutline(outline: Outline, brush: Brush, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DrawScope.DefaultBlendMode): Unit = TODO()
fun Canvas.drawOutline(outline: Outline, paint: Paint): Unit = TODO()

interface Path {
    var fillType: PathFillType
    val isConvex: Boolean
    val isEmpty: Boolean
    fun moveTo(x: Float, y: Float)
    fun relativeMoveTo(dx: Float, dy: Float)
    fun lineTo(x: Float, y: Float)
    fun relativeLineTo(dx: Float, dy: Float)
    @Deprecated("Use quadraticTo() instead", ReplaceWith("quadraticTo(x1, y1, x2, y2)")) fun quadraticBezierTo(x1: Float, y1: Float, x2: Float, y2: Float)
    fun quadraticTo(x1: Float, y1: Float, x2: Float, y2: Float) {}
    @Deprecated("Use relativeQuadraticTo() instead") fun relativeQuadraticBezierTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float)
    fun relativeQuadraticTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float) {}
    fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float)
    fun relativeCubicTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float, dx3: Float, dy3: Float)
    fun arcToRad(rect: Rect, startAngleRadians: Float, sweepAngleRadians: Float, forceMoveTo: Boolean) {}
    fun arcTo(rect: Rect, startAngleDegrees: Float, sweepAngleDegrees: Float, forceMoveTo: Boolean)
    fun addRect(rect: Rect)
    fun addOval(oval: Rect)
    fun addArcRad(oval: Rect, startAngleRadians: Float, sweepAngleRadians: Float)
    fun addArc(oval: Rect, startAngleDegrees: Float, sweepAngleDegrees: Float)
    fun addRoundRect(roundRect: RoundRect)
    fun addPath(path: Path, offset: Offset = Offset.Zero)
    fun close()
    fun reset()
    fun rewind() {}
    fun translate(offset: Offset)
    fun transform(matrix: Matrix) {}
    fun getBounds(): Rect
    fun op(path1: Path, path2: Path, operation: PathOperation): Boolean
    operator fun plus(path: Path): Path = TODO()
    operator fun minus(path: Path): Path = TODO()
    infix fun or(path: Path): Path = TODO()
    infix fun and(path: Path): Path = TODO()
    infix fun xor(path: Path): Path = TODO()
    companion object { fun combine(operation: PathOperation, path1: Path, path2: Path): Path = TODO() }
}
fun Path(): Path = TODO()
fun android.graphics.Path.asComposePath(): Path = TODO()
fun Path.asAndroidPath(): android.graphics.Path = TODO()
interface PathMeasure {
    val length: Float
    fun getSegment(startDistance: Float, stopDistance: Float, destination: Path, startWithMoveTo: Boolean = true): Boolean
    fun setPath(path: Path?, forceClosed: Boolean)
    fun getPosition(distance: Float): Offset
    fun getTangent(distance: Float): Offset
}
fun PathMeasure(): PathMeasure = TODO()
interface PathEffect {
    companion object {
        fun cornerPathEffect(radius: Float): PathEffect = TODO()
        fun dashPathEffect(intervals: FloatArray, phase: Float = 0f): PathEffect = TODO()
        fun chainPathEffect(outer: PathEffect, inner: PathEffect): PathEffect = TODO()
    }
}

interface ImageBitmap {
    val width: Int
    val height: Int
    val colorSpace: ColorSpace
    val hasAlpha: Boolean
    val config: ImageBitmapConfig
    fun prepareToDraw()
    fun readPixels(buffer: IntArray, startX: Int = 0, startY: Int = 0, width: Int = this.width, height: Int = this.height, bufferOffset: Int = 0, stride: Int = width) {}
    companion object
}
fun ImageBitmap(width: Int, height: Int, config: ImageBitmapConfig = ImageBitmapConfig.Argb8888, hasAlpha: Boolean = true, colorSpace: ColorSpace = ColorSpaces.Srgb): ImageBitmap = TODO()
fun android.graphics.Bitmap.asImageBitmap(): ImageBitmap = TODO()
fun ImageBitmap.asAndroidBitmap(): android.graphics.Bitmap = TODO()

interface Paint {
    var alpha: Float
    var isAntiAlias: Boolean
    var color: Color
    var blendMode: BlendMode
    var style: PaintingStyle
    var strokeWidth: Float
    var strokeCap: StrokeCap
    var strokeJoin: StrokeJoin
    var strokeMiterLimit: Float
    var filterQuality: FilterQuality
    var shader: Shader?
    var colorFilter: ColorFilter?
    var pathEffect: PathEffect?
    fun asFrameworkPaint(): android.graphics.Paint
}
fun Paint(): Paint = TODO()

interface Canvas {
    fun save()
    fun restore()
    fun saveLayer(bounds: Rect, paint: Paint)
    fun translate(dx: Float, dy: Float)
    fun scale(sx: Float, sy: Float = sx)
    fun rotate(degrees: Float)
    fun skew(sx: Float, sy: Float)
    fun concat(matrix: Matrix)
    fun clipRect(rect: Rect, clipOp: ClipOp = ClipOp.Intersect) {}
    fun clipRect(left: Float, top: Float, right: Float, bottom: Float, clipOp: ClipOp = ClipOp.Intersect)
    fun clipPath(path: Path, clipOp: ClipOp = ClipOp.Intersect)
    fun drawLine(p1: Offset, p2: Offset, paint: Paint)
    fun drawRect(rect: Rect, paint: Paint) {}
    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint)
    fun drawRoundRect(left: Float, top: Float, right: Float, bottom: Float, radiusX: Float, radiusY: Float, paint: Paint)
    fun drawOval(rect: Rect, paint: Paint) {}
    fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint)
    fun drawCircle(center: Offset, radius: Float, paint: Paint)
    fun drawArc(rect: Rect, startAngle: Float, sweepAngle: Float, useCenter: Boolean, paint: Paint) {}
    fun drawArc(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, useCenter: Boolean, paint: Paint)
    fun drawPath(path: Path, paint: Paint)
    fun drawImage(image: ImageBitmap, topLeftOffset: Offset, paint: Paint)
}
fun Canvas(image: ImageBitmap): Canvas = TODO()
val Canvas.nativeCanvas: android.graphics.Canvas get() = TODO()

@Immutable class Shadow(val color: Color = Color(0xFF000000), val offset: Offset = Offset.Zero, val blurRadius: Float = 0.0f) {
    fun copy(color: Color = this.color, offset: Offset = this.offset, blurRadius: Float = this.blurRadius): Shadow = TODO()
    companion object { val None: Shadow get() = TODO() }
}
fun lerp(start: Shadow, stop: Shadow, fraction: Float): Shadow = TODO()

@Immutable abstract class RenderEffect internal constructor() { fun asAndroidRenderEffect(): android.graphics.RenderEffect = TODO(); open fun isSupported(): Boolean = true }
@Immutable class BlurEffect(private val radiusX: Float, private val radiusY: Float = radiusX, private val edgeTreatment: TileMode = TileMode.Clamp) : RenderEffect()
@Immutable class OffsetEffect(private val offset: Offset) : RenderEffect()
fun android.graphics.RenderEffect.asComposeRenderEffect(): RenderEffect = TODO()

@Immutable class TransformOrigin(val pivotFractionX: Float, val pivotFractionY: Float) {
    fun copy(pivotFractionX: Float = this.pivotFractionX, pivotFractionY: Float = this.pivotFractionY): TransformOrigin = TODO()
    companion object { val Center: TransformOrigin get() = TODO() }
}
interface GraphicsLayerScope : Density {
    var scaleX: Float
    var scaleY: Float
    var alpha: Float
    var translationX: Float
    var translationY: Float
    var shadowElevation: Float
    var ambientShadowColor: Color
    var spotShadowColor: Color
    var rotationX: Float
    var rotationY: Float
    var rotationZ: Float
    var cameraDistance: Float
    var transformOrigin: TransformOrigin
    var shape: Shape
    var clip: Boolean
    var renderEffect: RenderEffect?
    var compositingStrategy: CompositingStrategy
    val size: Size
}
fun Modifier.graphicsLayer(block: GraphicsLayerScope.() -> Unit): Modifier = TODO()
fun Modifier.graphicsLayer(
    scaleX: Float = 1f, scaleY: Float = 1f, alpha: Float = 1f, translationX: Float = 0f, translationY: Float = 0f, shadowElevation: Float = 0f,
    rotationX: Float = 0f, rotationY: Float = 0f, rotationZ: Float = 0f, cameraDistance: Float = DefaultCameraDistance, transformOrigin: TransformOrigin = TransformOrigin.Center,
    shape: Shape = RectangleShape, clip: Boolean = false, renderEffect: RenderEffect? = null, ambientShadowColor: Color = DefaultShadowColor,
    spotShadowColor: Color = DefaultShadowColor, compositingStrategy: CompositingStrategy = CompositingStrategy.Auto,
): Modifier = TODO()
@Composable fun rememberGraphicsLayer(): androidx.compose.ui.graphics.layer.GraphicsLayer = TODO()
//// FILE: androidx/compose/ui/graphics/colorspace/ColorSpace.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.graphics.colorspace

abstract class ColorSpace internal constructor(val name: String)
object ColorSpaces { val Srgb: ColorSpace get() = TODO(); val LinearSrgb: ColorSpace get() = TODO(); val DisplayP3: ColorSpace get() = TODO(); val Oklab: ColorSpace get() = TODO() }
//// FILE: androidx/compose/ui/graphics/layer/Layer.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.graphics.layer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

class GraphicsLayer internal constructor() {
    var alpha: Float = 1f
    var scaleX: Float = 1f
    var scaleY: Float = 1f
    var translationX: Float = 0f
    var translationY: Float = 0f
    var topLeft: IntOffset = IntOffset.Zero
    val size: IntSize get() = TODO()
    var renderEffect: RenderEffect? = null
    var blendMode: BlendMode = BlendMode.SrcOver
    var colorFilter: ColorFilter? = null
    var pivotOffset: Offset = Offset.Unspecified
    var clip: Boolean = false
    val isReleased: Boolean get() = false
    fun record(density: Density, layoutDirection: LayoutDirection, size: IntSize, block: DrawScope.() -> Unit) {}
    fun setRectOutline(topLeft: Offset = Offset.Zero, size: androidx.compose.ui.geometry.Size = androidx.compose.ui.geometry.Size.Unspecified) {}
    fun setRoundRectOutline(topLeft: Offset = Offset.Zero, size: androidx.compose.ui.geometry.Size = androidx.compose.ui.geometry.Size.Unspecified, cornerRadius: Float = 0f) {}
}
fun DrawScope.drawLayer(graphicsLayer: GraphicsLayer): Unit = TODO()
//// FILE: androidx/compose/ui/graphics/drawscope/DrawScope.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.graphics.drawscope

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toIntSize

private fun Size.offsetSize(offset: Offset): Size = Size(width - offset.x, height - offset.y)

interface DrawContext { var size: Size; val canvas: Canvas; val transform: DrawTransform; var layoutDirection: LayoutDirection; var density: Density; var graphicsLayer: GraphicsLayer? }
interface DrawTransform {
    val size: Size
    val center: Offset get() = TODO()
    fun inset(left: Float, top: Float, right: Float, bottom: Float)
    fun clipRect(left: Float = 0.0f, top: Float = 0.0f, right: Float = size.width, bottom: Float = size.height, clipOp: ClipOp = ClipOp.Intersect)
    fun clipPath(path: Path, clipOp: ClipOp = ClipOp.Intersect)
    fun translate(left: Float = 0.0f, top: Float = 0.0f)
    fun rotate(degrees: Float, pivot: Offset = center)
    fun scale(scaleX: Float, scaleY: Float, pivot: Offset = center)
    fun transform(matrix: Matrix)
}
fun DrawTransform.rotateRad(radians: Float, pivot: Offset = center): Unit = TODO()
fun DrawTransform.scale(scale: Float, pivot: Offset = center): Unit = TODO()
fun DrawTransform.inset(inset: Float): Unit = TODO()
fun DrawTransform.inset(horizontal: Float = 0.0f, vertical: Float = 0.0f): Unit = TODO()

sealed class DrawStyle
object Fill : DrawStyle()
class Stroke(val width: Float = 0.0f, val miter: Float = DefaultMiter, val cap: StrokeCap = DefaultCap, val join: StrokeJoin = DefaultJoin, val pathEffect: PathEffect? = null) : DrawStyle() {
    companion object { const val HairlineWidth = 0.0f; const val DefaultMiter: Float = 4.0f; val DefaultCap: StrokeCap get() = StrokeCap.Butt; val DefaultJoin: StrokeJoin get() = StrokeJoin.Miter }
}

interface DrawScope : Density {
    val drawContext: DrawContext
    val center: Offset get() = TODO()
    val size: Size get() = TODO()
    val layoutDirection: LayoutDirection
    fun drawLine(brush: Brush, start: Offset, end: Offset, strokeWidth: Float = Stroke.HairlineWidth, cap: StrokeCap = Stroke.DefaultCap, pathEffect: PathEffect? = null, alpha: Float = 1.0f, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawLine(color: Color, start: Offset, end: Offset, strokeWidth: Float = Stroke.HairlineWidth, cap: StrokeCap = Stroke.DefaultCap, pathEffect: PathEffect? = null, alpha: Float = 1.0f, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawRect(brush: Brush, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawRect(color: Color, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawImage(image: ImageBitmap, topLeft: Offset = Offset.Zero, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawImage(image: ImageBitmap, srcOffset: IntOffset = IntOffset.Zero, srcSize: IntSize = IntSize(image.width, image.height), dstOffset: IntOffset = IntOffset.Zero, dstSize: IntSize = srcSize, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode, filterQuality: FilterQuality = DefaultFilterQuality) {}
    fun drawRoundRect(brush: Brush, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), cornerRadius: CornerRadius = CornerRadius.Zero, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawRoundRect(color: Color, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), cornerRadius: CornerRadius = CornerRadius.Zero, style: DrawStyle = Fill, alpha: Float = 1.0f, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawCircle(brush: Brush, radius: Float = size.minDimension / 2.0f, center: Offset = this.center, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawCircle(color: Color, radius: Float = size.minDimension / 2.0f, center: Offset = this.center, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawOval(brush: Brush, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawOval(color: Color, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawArc(brush: Brush, startAngle: Float, sweepAngle: Float, useCenter: Boolean, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawArc(color: Color, startAngle: Float, sweepAngle: Float, useCenter: Boolean, topLeft: Offset = Offset.Zero, size: Size = this.size.offsetSize(topLeft), alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawPath(path: Path, color: Color, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawPath(path: Path, brush: Brush, alpha: Float = 1.0f, style: DrawStyle = Fill, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawPoints(points: List<Offset>, pointMode: PointMode, color: Color, strokeWidth: Float = Stroke.HairlineWidth, cap: StrokeCap = StrokeCap.Butt, pathEffect: PathEffect? = null, alpha: Float = 1.0f, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun drawPoints(points: List<Offset>, pointMode: PointMode, brush: Brush, strokeWidth: Float = Stroke.HairlineWidth, cap: StrokeCap = StrokeCap.Butt, pathEffect: PathEffect? = null, alpha: Float = 1.0f, colorFilter: ColorFilter? = null, blendMode: BlendMode = DefaultBlendMode)
    fun GraphicsLayer.record(size: IntSize = this@DrawScope.size.toIntSize(), block: DrawScope.() -> Unit): Unit = TODO()
    companion object { val DefaultBlendMode: BlendMode get() = BlendMode.SrcOver; val DefaultFilterQuality: FilterQuality get() = FilterQuality.Low }
}
interface ContentDrawScope : DrawScope { fun drawContent() }
inline fun DrawScope.translate(left: Float = 0.0f, top: Float = 0.0f, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.rotate(degrees: Float, pivot: Offset = center, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.rotateRad(radians: Float, pivot: Offset = center, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.scale(scaleX: Float, scaleY: Float, pivot: Offset = center, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.scale(scale: Float, pivot: Offset = center, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.clipRect(left: Float = 0.0f, top: Float = 0.0f, right: Float = size.width, bottom: Float = size.height, clipOp: ClipOp = ClipOp.Intersect, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.clipPath(path: Path, clipOp: ClipOp = ClipOp.Intersect, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.inset(left: Float, top: Float, right: Float, bottom: Float, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.inset(inset: Float, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.inset(horizontal: Float = 0.0f, vertical: Float = 0.0f, block: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.withTransform(transformBlock: DrawTransform.() -> Unit, drawBlock: DrawScope.() -> Unit): Unit = TODO()
inline fun DrawScope.drawIntoCanvas(block: (Canvas) -> Unit): Unit = TODO()
//// FILE: androidx/compose/ui/graphics/painter/Painter.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.graphics.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope

abstract class Painter { abstract val intrinsicSize: Size; protected abstract fun DrawScope.onDraw() }
class BitmapPainter(private val image: ImageBitmap) : Painter() { override val intrinsicSize: Size get() = TODO(); override fun DrawScope.onDraw() {} }
class ColorPainter(val color: androidx.compose.ui.graphics.Color) : Painter() { override val intrinsicSize: Size get() = TODO(); override fun DrawScope.onDraw() {} }
//// FILE: androidx/compose/ui/graphics/vector/Vector.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.graphics.vector

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path

class ImageVector internal constructor() { val name: String get() = TODO(); companion object }
sealed class PathNode
class PathParser {
    fun parsePathString(pathData: String): PathParser = TODO()
    fun toNodes(): List<PathNode> = TODO()
    fun toPath(target: Path = Path()): Path = TODO()
    fun clear() {}
    fun addPathNodes(nodes: List<PathNode>): PathParser = TODO()
}
fun String.toPath(path: Path = Path()): Path = TODO()
@Composable fun rememberVectorPainter(image: ImageVector): androidx.compose.ui.graphics.painter.Painter = TODO()
@Composable fun ImageVector.Companion.vectorResource(id: Int): ImageVector = TODO()
//// FILE: androidx/compose/ui/hapticfeedback/Haptic.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.hapticfeedback

interface HapticFeedback { fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) }
@JvmInline value class HapticFeedbackType(internal val type: Int) {
    companion object { val LongPress: HapticFeedbackType get() = TODO(); val TextHandleMove: HapticFeedbackType get() = TODO(); fun values(): List<HapticFeedbackType> = TODO() }
}
//// FILE: androidx/compose/ui/input/nestedscroll/NestedScroll.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.input.nestedscroll

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity

@JvmInline value class NestedScrollSource internal constructor(internal val value: Int) {
    companion object {
        val UserInput: NestedScrollSource get() = TODO()
        val SideEffect: NestedScrollSource get() = TODO()
        @Deprecated("Use UserInput") val Drag: NestedScrollSource get() = TODO()
        @Deprecated("Use SideEffect") val Fling: NestedScrollSource get() = TODO()
        @Deprecated("Removed") val Wheel: NestedScrollSource get() = TODO()
    }
}
interface NestedScrollConnection {
    fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = Offset.Zero
    fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset = Offset.Zero
    suspend fun onPreFling(available: Velocity): Velocity = Velocity.Zero
    suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = Velocity.Zero
}
class NestedScrollDispatcher
fun Modifier.nestedScroll(connection: NestedScrollConnection, dispatcher: NestedScrollDispatcher? = null): Modifier = TODO()
//// FILE: androidx/compose/ui/input/pointer/Pointer.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.input.pointer

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize

enum class PointerEventPass { Initial, Main, Final }
@JvmInline value class PointerEventType private constructor(internal val value: Int) {
    companion object { val Unknown = PointerEventType(0); val Press = PointerEventType(1); val Release = PointerEventType(2); val Move = PointerEventType(3); val Enter = PointerEventType(4); val Exit = PointerEventType(5); val Scroll = PointerEventType(6) }
}
@JvmInline value class PointerType private constructor(internal val value: Int) {
    companion object { val Unknown = PointerType(0); val Touch = PointerType(1); val Mouse = PointerType(2); val Stylus = PointerType(3); val Eraser = PointerType(4) }
}
@JvmInline value class PointerId(val value: Long)
class PointerInputChange(
    val id: PointerId, val uptimeMillis: Long, val position: Offset, val pressed: Boolean, val previousUptimeMillis: Long,
    val previousPosition: Offset, val previousPressed: Boolean, val type: PointerType = PointerType.Touch,
) {
    val pressure: Float get() = TODO()
    val isConsumed: Boolean get() = TODO()
    fun consume() {}
}
class PointerEvent(val changes: List<PointerInputChange>) { val type: PointerEventType get() = TODO() }
fun PointerInputChange.changedToDown(): Boolean = TODO()
fun PointerInputChange.changedToDownIgnoreConsumed(): Boolean = TODO()
fun PointerInputChange.changedToUp(): Boolean = TODO()
fun PointerInputChange.changedToUpIgnoreConsumed(): Boolean = TODO()
fun PointerInputChange.positionChanged(): Boolean = TODO()
fun PointerInputChange.positionChangedIgnoreConsumed(): Boolean = TODO()
fun PointerInputChange.positionChange(): Offset = TODO()
fun PointerInputChange.positionChangeIgnoreConsumed(): Offset = TODO()
fun PointerInputChange.isOutOfBounds(size: IntSize, extendedTouchPadding: Size): Boolean = TODO()
interface AwaitPointerEventScope : Density {
    val size: IntSize
    val extendedTouchPadding: Size get() = Size.Zero
    val currentEvent: PointerEvent
    val viewConfiguration: ViewConfiguration
    suspend fun awaitPointerEvent(pass: PointerEventPass = PointerEventPass.Main): PointerEvent
    suspend fun <T> withTimeoutOrNull(timeMillis: Long, block: suspend AwaitPointerEventScope.() -> T): T? = TODO()
    suspend fun <T> withTimeout(timeMillis: Long, block: suspend AwaitPointerEventScope.() -> T): T = TODO()
}
interface PointerInputScope : Density {
    val size: IntSize
    val extendedTouchPadding: Size get() = Size.Zero
    val viewConfiguration: ViewConfiguration
    var interceptOutOfBoundsChildEvents: Boolean
    suspend fun <R> awaitPointerEventScope(block: suspend AwaitPointerEventScope.() -> R): R
}
fun Modifier.pointerInput(key1: Any?, block: suspend PointerInputScope.() -> Unit): Modifier = TODO()
fun Modifier.pointerInput(key1: Any?, key2: Any?, block: suspend PointerInputScope.() -> Unit): Modifier = TODO()
fun Modifier.pointerInput(vararg keys: Any?, block: suspend PointerInputScope.() -> Unit): Modifier = TODO()
//// FILE: androidx/compose/ui/layout/Layout.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

@Stable interface ContentScale {
    fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor
    companion object {
        val Crop: ContentScale get() = TODO(); val Fit: ContentScale get() = TODO(); val FillHeight: ContentScale get() = TODO(); val FillWidth: ContentScale get() = TODO()
        val Inside: ContentScale get() = TODO(); val None: ContentScale get() = TODO(); val FillBounds: ContentScale get() = TODO()
    }
}
class ScaleFactor(val scaleX: Float, val scaleY: Float)
interface LayoutCoordinates {
    val size: IntSize
    val parentLayoutCoordinates: LayoutCoordinates?
    val parentCoordinates: LayoutCoordinates?
    val isAttached: Boolean
    fun windowToLocal(relativeToWindow: Offset): Offset
    fun localToWindow(relativeToLocal: Offset): Offset
    fun localToRoot(relativeToLocal: Offset): Offset
    fun localPositionOf(sourceCoordinates: LayoutCoordinates, relativeToSource: Offset = Offset.Zero): Offset
    fun localBoundingBoxOf(sourceCoordinates: LayoutCoordinates, clipBounds: Boolean = true): Rect
}
fun LayoutCoordinates.positionInRoot(): Offset = TODO()
fun LayoutCoordinates.positionInWindow(): Offset = TODO()
fun LayoutCoordinates.positionOnScreen(): Offset = TODO()
fun LayoutCoordinates.boundsInRoot(): Rect = TODO()
fun LayoutCoordinates.boundsInWindow(): Rect = TODO()
fun LayoutCoordinates.boundsInParent(): Rect = TODO()
fun LayoutCoordinates.positionInParent(): Offset = TODO()
fun Modifier.onGloballyPositioned(onGloballyPositioned: (LayoutCoordinates) -> Unit): Modifier = TODO()
fun Modifier.onPlaced(onPlaced: (LayoutCoordinates) -> Unit): Modifier = TODO()
fun Modifier.onSizeChanged(onSizeChanged: (IntSize) -> Unit): Modifier = TODO()
fun Modifier.layoutId(layoutId: Any): Modifier = TODO()
interface Measured { val measuredWidth: Int; val measuredHeight: Int }
abstract class Placeable : Measured {
    var width: Int = 0; protected set
    var height: Int = 0; protected set
    override val measuredWidth: Int get() = TODO()
    override val measuredHeight: Int get() = TODO()
    abstract class PlacementScope {
        fun Placeable.place(x: Int, y: Int, zIndex: Float = 0f) {}
        fun Placeable.place(position: IntOffset, zIndex: Float = 0f) {}
        fun Placeable.placeRelative(x: Int, y: Int, zIndex: Float = 0f) {}
        fun Placeable.placeRelative(position: IntOffset, zIndex: Float = 0f) {}
        fun Placeable.placeWithLayer(x: Int, y: Int, zIndex: Float = 0f, layerBlock: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit = {}) {}
        fun Placeable.placeRelativeWithLayer(x: Int, y: Int, zIndex: Float = 0f, layerBlock: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit = {}) {}
    }
}
interface Measurable : IntrinsicMeasurable { fun measure(constraints: Constraints): Placeable }
interface IntrinsicMeasurable { val parentData: Any?; fun minIntrinsicWidth(height: Int): Int; fun maxIntrinsicWidth(height: Int): Int; fun minIntrinsicHeight(width: Int): Int; fun maxIntrinsicHeight(width: Int): Int }
interface MeasureResult { val width: Int; val height: Int; val alignmentLines: Map<AlignmentLine, Int>; fun placeChildren() }
abstract class AlignmentLine internal constructor()
interface IntrinsicMeasureScope : Density { val layoutDirection: LayoutDirection }
interface MeasureScope : IntrinsicMeasureScope {
    fun layout(width: Int, height: Int, alignmentLines: Map<AlignmentLine, Int> = emptyMap(), placementBlock: Placeable.PlacementScope.() -> Unit): MeasureResult = TODO()
}
fun interface MeasurePolicy { fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult }
@Composable inline fun Layout(content: @Composable () -> Unit, modifier: Modifier = Modifier, measurePolicy: MeasurePolicy): Unit = TODO()
@Composable fun Layout(modifier: Modifier = Modifier, measurePolicy: MeasurePolicy): Unit = TODO()
fun Modifier.layout(measure: MeasureScope.(measurable: Measurable, constraints: Constraints) -> MeasureResult): Modifier = TODO()
//// FILE: androidx/compose/ui/platform/Platform.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.platform

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class AbstractComposeView(context: Context) : android.view.ViewGroup(context) {
    fun setParentCompositionContext(parent: CompositionContext?) {}
    fun setViewCompositionStrategy(strategy: ViewCompositionStrategy) {}
    fun disposeComposition() {}
    fun createComposition() {}
    val hasComposition: Boolean get() = false
}
class ComposeView(context: Context, attrs: android.util.AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context) {
    fun setContent(content: @Composable () -> Unit) {}
}
interface ViewCompositionStrategy {
    object DisposeOnDetachedFromWindow : ViewCompositionStrategy
    object DisposeOnDetachedFromWindowOrReleasedFromPool : ViewCompositionStrategy
    class DisposeOnLifecycleDestroyed(private val lifecycle: Lifecycle) : ViewCompositionStrategy
    object DisposeOnViewTreeLifecycleDestroyed : ViewCompositionStrategy
}
@InternalComposeUiApi fun View.createLifecycleAwareWindowRecomposer(coroutineContext: CoroutineContext = EmptyCoroutineContext, lifecycle: Lifecycle? = null): Recomposer = TODO()
interface ViewConfiguration { val longPressTimeoutMillis: Long; val doubleTapTimeoutMillis: Long; val doubleTapMinTimeMillis: Long; val touchSlop: Float; val minimumTouchTargetSize: DpSize get() = TODO(); val maximumFlingVelocity: Float get() = Float.MAX_VALUE }
interface SoftwareKeyboardController { fun show(); fun hide() }
interface UriHandler { fun openUri(uri: String) }
interface ClipboardManager { fun setText(annotatedString: androidx.compose.ui.text.AnnotatedString); fun getText(): androidx.compose.ui.text.AnnotatedString? }
interface WindowInfo { val isWindowFocused: Boolean }
val LocalContext: ProvidableCompositionLocal<Context> = staticCompositionLocalOf { TODO() }
val LocalDensity: ProvidableCompositionLocal<Density> = staticCompositionLocalOf { TODO() }
val LocalConfiguration: ProvidableCompositionLocal<android.content.res.Configuration> = staticCompositionLocalOf { TODO() }
val LocalView: ProvidableCompositionLocal<View> = staticCompositionLocalOf { TODO() }
val LocalFocusManager: ProvidableCompositionLocal<FocusManager> = staticCompositionLocalOf { TODO() }
val LocalFontFamilyResolver: ProvidableCompositionLocal<FontFamily.Resolver> = staticCompositionLocalOf { TODO() }
val LocalHapticFeedback: ProvidableCompositionLocal<HapticFeedback> = staticCompositionLocalOf { TODO() }
val LocalLayoutDirection: ProvidableCompositionLocal<LayoutDirection> = staticCompositionLocalOf { TODO() }
@Deprecated("Moved to lifecycle-runtime-compose library in androidx.lifecycle.compose package.") val LocalLifecycleOwner: ProvidableCompositionLocal<LifecycleOwner> = staticCompositionLocalOf { TODO() }
val LocalSoftwareKeyboardController: ProvidableCompositionLocal<SoftwareKeyboardController?> = staticCompositionLocalOf { TODO() }
val LocalViewConfiguration: ProvidableCompositionLocal<ViewConfiguration> = staticCompositionLocalOf { TODO() }
val LocalUriHandler: ProvidableCompositionLocal<UriHandler> = staticCompositionLocalOf { TODO() }
val LocalClipboardManager: ProvidableCompositionLocal<ClipboardManager> = staticCompositionLocalOf { TODO() }
val LocalInspectionMode: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }
val LocalWindowInfo: ProvidableCompositionLocal<WindowInfo> = staticCompositionLocalOf { TODO() }
//// FILE: android/util/AttributeSet.kt
package android.util
interface AttributeSet
//// FILE: androidx/compose/ui/res/Res.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter

@Composable fun painterResource(id: Int): Painter = TODO()
@Composable fun stringResource(id: Int): String = TODO()
@Composable fun stringResource(id: Int, vararg formatArgs: Any): String = TODO()
@Composable fun colorResource(id: Int): Color = TODO()
@Composable fun ImageBitmap.Companion.imageResource(id: Int): ImageBitmap = TODO()
//// FILE: androidx/compose/ui/semantics/Semantics.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.semantics

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString

interface SemanticsPropertyReceiver
@JvmInline value class Role private constructor(private val value: Int) {
    companion object { val Button = Role(0); val Checkbox = Role(1); val Switch = Role(2); val RadioButton = Role(3); val Tab = Role(4); val Image = Role(5); val DropdownList = Role(6) }
}
@JvmInline value class LiveRegionMode private constructor(private val value: Int) { companion object { val Polite = LiveRegionMode(0); val Assertive = LiveRegionMode(1) } }
class ProgressBarRangeInfo(val current: Float, val range: ClosedFloatingPointRange<Float>, val steps: Int = 0) { companion object { val Indeterminate: ProgressBarRangeInfo get() = TODO() } }
fun Modifier.semantics(mergeDescendants: Boolean = false, properties: (SemanticsPropertyReceiver.() -> Unit)): Modifier = TODO()
fun Modifier.clearAndSetSemantics(properties: (SemanticsPropertyReceiver.() -> Unit)): Modifier = TODO()
var SemanticsPropertyReceiver.contentDescription: String
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.stateDescription: String
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.role: Role
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.selected: Boolean
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.focused: Boolean
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.testTag: String
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.liveRegion: LiveRegionMode
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.progressBarRangeInfo: ProgressBarRangeInfo
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.text: AnnotatedString
    get() = TODO()
    set(value) {}
var SemanticsPropertyReceiver.paneTitle: String
    get() = TODO()
    set(value) {}
fun SemanticsPropertyReceiver.heading() {}
fun SemanticsPropertyReceiver.disabled() {}
fun SemanticsPropertyReceiver.invisibleToUser() {}
fun SemanticsPropertyReceiver.onClick(label: String? = null, action: (() -> Boolean)?) {}
fun SemanticsPropertyReceiver.onLongClick(label: String? = null, action: (() -> Boolean)?) {}
fun SemanticsPropertyReceiver.dismiss(label: String? = null, action: (() -> Boolean)?) {}
fun SemanticsPropertyReceiver.setProgress(label: String? = null, action: ((targetValue: Float) -> Boolean)?) {}
//// FILE: androidx/compose/ui/window/Window.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset

enum class SecureFlagPolicy { Inherit, SecureOn, SecureOff }
class DialogProperties(val dismissOnBackPress: Boolean = true, val dismissOnClickOutside: Boolean = true, val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit, val usePlatformDefaultWidth: Boolean = true, val decorFitsSystemWindows: Boolean = true)
interface DialogWindowProvider { val window: android.view.Window }
@Composable fun Dialog(onDismissRequest: () -> Unit, properties: DialogProperties = DialogProperties(), content: @Composable () -> Unit): Unit = TODO()
class PopupProperties(val focusable: Boolean = false, val dismissOnBackPress: Boolean = true, val dismissOnClickOutside: Boolean = true, val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit, val excludeFromSystemGesture: Boolean = true, val clippingEnabled: Boolean = true)
@Composable fun Popup(alignment: Alignment = Alignment.TopStart, offset: IntOffset = IntOffset(0, 0), onDismissRequest: (() -> Unit)? = null, properties: PopupProperties = PopupProperties(), content: @Composable () -> Unit): Unit = TODO()
//// FILE: androidx/compose/ui/tooling/preview/Preview.kt
package androidx.compose.ui.tooling.preview
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION) @Repeatable
annotation class Preview(val name: String = "", val group: String = "", val apiLevel: Int = -1, val widthDp: Int = -1, val heightDp: Int = -1, val locale: String = "", val fontScale: Float = 1f, val showSystemUi: Boolean = false, val showBackground: Boolean = false, val backgroundColor: Long = 0, val uiMode: Int = 0, val device: String = "", val wallpaper: Int = -1)
