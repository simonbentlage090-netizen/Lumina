//// FILE: androidx/compose/ui/text/Text.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit

class PlatformTextStyle(val includeFontPadding: Boolean = false) { val spanStyle: Any? get() = null; val paragraphStyle: Any? get() = null }

@Immutable class SpanStyle(
    val color: Color = Color.Unspecified, val fontSize: TextUnit = TextUnit.Unspecified, val fontWeight: FontWeight? = null, val fontStyle: FontStyle? = null,
    val fontSynthesis: FontSynthesis? = null, val fontFamily: FontFamily? = null, val fontFeatureSettings: String? = null, val letterSpacing: TextUnit = TextUnit.Unspecified,
    val baselineShift: BaselineShift? = null, val textGeometricTransform: TextGeometricTransform? = null, val localeList: LocaleList? = null,
    val background: Color = Color.Unspecified, val textDecoration: TextDecoration? = null, val shadow: Shadow? = null, val platformStyle: Any? = null, val drawStyle: DrawStyle? = null,
) {
    fun merge(other: SpanStyle? = null): SpanStyle = TODO()
    operator fun plus(other: SpanStyle): SpanStyle = TODO()
    fun copy(
        color: Color = this.color, fontSize: TextUnit = this.fontSize, fontWeight: FontWeight? = this.fontWeight, fontStyle: FontStyle? = this.fontStyle,
        fontSynthesis: FontSynthesis? = this.fontSynthesis, fontFamily: FontFamily? = this.fontFamily, fontFeatureSettings: String? = this.fontFeatureSettings,
        letterSpacing: TextUnit = this.letterSpacing, baselineShift: BaselineShift? = this.baselineShift, textGeometricTransform: TextGeometricTransform? = this.textGeometricTransform,
        localeList: LocaleList? = this.localeList, background: Color = this.background, textDecoration: TextDecoration? = this.textDecoration, shadow: Shadow? = this.shadow,
        platformStyle: Any? = this.platformStyle, drawStyle: DrawStyle? = this.drawStyle,
    ): SpanStyle = TODO()
}
@Immutable class ParagraphStyle(
    val textAlign: TextAlign = TextAlign.Unspecified, val textDirection: TextDirection = TextDirection.Unspecified, val lineHeight: TextUnit = TextUnit.Unspecified,
    val textIndent: TextIndent? = null, val platformStyle: Any? = null, val lineHeightStyle: LineHeightStyle? = null, val lineBreak: LineBreak = LineBreak.Unspecified,
    val hyphens: Hyphens = Hyphens.Unspecified, val textMotion: TextMotion? = null,
) {
    fun merge(other: ParagraphStyle? = null): ParagraphStyle = TODO()
    operator fun plus(other: ParagraphStyle): ParagraphStyle = TODO()
    fun copy(textAlign: TextAlign = this.textAlign, textDirection: TextDirection = this.textDirection, lineHeight: TextUnit = this.lineHeight, textIndent: TextIndent? = this.textIndent, platformStyle: Any? = this.platformStyle, lineHeightStyle: LineHeightStyle? = this.lineHeightStyle, lineBreak: LineBreak = this.lineBreak, hyphens: Hyphens = this.hyphens, textMotion: TextMotion? = this.textMotion): ParagraphStyle = TODO()
}
@Immutable class TextStyle(
    val color: Color = Color.Unspecified, val fontSize: TextUnit = TextUnit.Unspecified, val fontWeight: FontWeight? = null, val fontStyle: FontStyle? = null,
    val fontSynthesis: FontSynthesis? = null, val fontFamily: FontFamily? = null, val fontFeatureSettings: String? = null, val letterSpacing: TextUnit = TextUnit.Unspecified,
    val baselineShift: BaselineShift? = null, val textGeometricTransform: TextGeometricTransform? = null, val localeList: LocaleList? = null,
    val background: Color = Color.Unspecified, val textDecoration: TextDecoration? = null, val shadow: Shadow? = null, val drawStyle: DrawStyle? = null,
    val textAlign: TextAlign = TextAlign.Unspecified, val textDirection: TextDirection = TextDirection.Unspecified, val lineHeight: TextUnit = TextUnit.Unspecified,
    val textIndent: TextIndent? = null, val platformStyle: PlatformTextStyle? = null, val lineHeightStyle: LineHeightStyle? = null, val lineBreak: LineBreak = LineBreak.Unspecified,
    val hyphens: Hyphens = Hyphens.Unspecified, val textMotion: TextMotion? = null,
) {
    val brush: Brush? get() = null
    val alpha: Float get() = 1f
    fun toSpanStyle(): SpanStyle = TODO()
    fun toParagraphStyle(): ParagraphStyle = TODO()
    fun merge(other: TextStyle? = null): TextStyle = TODO()
    fun merge(other: SpanStyle): TextStyle = TODO()
    fun merge(other: ParagraphStyle): TextStyle = TODO()
    operator fun plus(other: TextStyle): TextStyle = TODO()
    operator fun plus(other: SpanStyle): TextStyle = TODO()
    operator fun plus(other: ParagraphStyle): TextStyle = TODO()
    fun copy(
        color: Color = this.color, fontSize: TextUnit = this.fontSize, fontWeight: FontWeight? = this.fontWeight, fontStyle: FontStyle? = this.fontStyle,
        fontSynthesis: FontSynthesis? = this.fontSynthesis, fontFamily: FontFamily? = this.fontFamily, fontFeatureSettings: String? = this.fontFeatureSettings,
        letterSpacing: TextUnit = this.letterSpacing, baselineShift: BaselineShift? = this.baselineShift, textGeometricTransform: TextGeometricTransform? = this.textGeometricTransform,
        localeList: LocaleList? = this.localeList, background: Color = this.background, textDecoration: TextDecoration? = this.textDecoration, shadow: Shadow? = this.shadow,
        drawStyle: DrawStyle? = this.drawStyle, textAlign: TextAlign = this.textAlign, textDirection: TextDirection = this.textDirection, lineHeight: TextUnit = this.lineHeight,
        textIndent: TextIndent? = this.textIndent, platformStyle: PlatformTextStyle? = this.platformStyle, lineHeightStyle: LineHeightStyle? = this.lineHeightStyle,
        lineBreak: LineBreak = this.lineBreak, hyphens: Hyphens = this.hyphens, textMotion: TextMotion? = this.textMotion,
    ): TextStyle = TODO()
    companion object { val Default: TextStyle get() = TODO() }
}
fun lerp(start: TextStyle, stop: TextStyle, fraction: Float): TextStyle = TODO()

@Immutable class AnnotatedString(val text: String, val spanStyles: List<Range<SpanStyle>> = listOf(), val paragraphStyles: List<Range<ParagraphStyle>> = listOf()) : CharSequence {
    override val length: Int get() = text.length
    override fun get(index: Int): Char = text[index]
    override fun subSequence(startIndex: Int, endIndex: Int): AnnotatedString = TODO()
    fun subSequence(range: TextRange): AnnotatedString = TODO()
    operator fun plus(other: AnnotatedString): AnnotatedString = TODO()
    fun getStringAnnotations(tag: String, start: Int, end: Int): List<Range<String>> = TODO()
    fun getStringAnnotations(start: Int, end: Int): List<Range<String>> = TODO()
    fun hasStringAnnotations(tag: String, start: Int, end: Int): Boolean = TODO()
    @Immutable data class Range<out T>(val item: T, val start: Int, val end: Int, val tag: String = "") { constructor(item: T, start: Int, end: Int) : this(item, start, end, "") }
    class Builder(capacity: Int = 16) : Appendable {
        constructor(text: String) : this()
        constructor(text: AnnotatedString) : this()
        val length: Int get() = TODO()
        fun append(text: String) {}
        override fun append(text: CharSequence?): Builder = this
        override fun append(text: CharSequence?, start: Int, end: Int): Builder = this
        override fun append(char: Char): Builder = this
        fun append(text: AnnotatedString) {}
        fun append(text: AnnotatedString, start: Int, end: Int) {}
        fun addStyle(style: SpanStyle, start: Int, end: Int) {}
        fun addStyle(style: ParagraphStyle, start: Int, end: Int) {}
        fun addStringAnnotation(tag: String, annotation: String, start: Int, end: Int) {}
        fun pushStyle(style: SpanStyle): Int = TODO()
        fun pushStyle(style: ParagraphStyle): Int = TODO()
        fun pushStringAnnotation(tag: String, annotation: String): Int = TODO()
        fun pop() {}
        fun pop(index: Int) {}
        fun toAnnotatedString(): AnnotatedString = TODO()
    }
}
fun AnnotatedString(text: String, spanStyle: SpanStyle, paragraphStyle: ParagraphStyle? = null): AnnotatedString = TODO()
fun AnnotatedString(text: String, paragraphStyle: ParagraphStyle): AnnotatedString = TODO()
inline fun buildAnnotatedString(builder: (AnnotatedString.Builder).() -> Unit): AnnotatedString = TODO()
inline fun <R : Any> AnnotatedString.Builder.withStyle(style: SpanStyle, block: AnnotatedString.Builder.() -> R): R = TODO()
inline fun <R : Any> AnnotatedString.Builder.withStyle(style: ParagraphStyle, crossinline block: AnnotatedString.Builder.() -> R): R = TODO()
inline fun <R : Any> AnnotatedString.Builder.withAnnotation(tag: String, annotation: String, crossinline block: AnnotatedString.Builder.() -> R): R = TODO()

@Immutable class TextRange(val start: Int, val end: Int) {
    val min: Int get() = minOf(start, end)
    val max: Int get() = maxOf(start, end)
    val collapsed: Boolean get() = start == end
    val reversed: Boolean get() = start > end
    val length: Int get() = max - min
    operator fun contains(offset: Int): Boolean = TODO()
    operator fun contains(other: TextRange): Boolean = TODO()
    companion object { val Zero = TextRange(0, 0) }
}
fun TextRange(index: Int): TextRange = TODO()
fun String.substring(range: TextRange): String = TODO()
fun CharSequence.substring(range: TextRange): String = TODO()

class TextLayoutInput(val text: AnnotatedString, val style: TextStyle, val maxLines: Int, val softWrap: Boolean, val overflow: TextOverflow, val density: Density, val layoutDirection: LayoutDirection, val constraints: Constraints)
class MultiParagraph internal constructor() { val height: Float get() = TODO(); val width: Float get() = TODO(); val lineCount: Int get() = TODO() }
class TextLayoutResult internal constructor() {
    val layoutInput: TextLayoutInput get() = TODO()
    val multiParagraph: MultiParagraph get() = TODO()
    val size: IntSize get() = TODO()
    val firstBaseline: Float get() = TODO()
    val lastBaseline: Float get() = TODO()
    val didOverflowHeight: Boolean get() = TODO()
    val didOverflowWidth: Boolean get() = TODO()
    val hasVisualOverflow: Boolean get() = TODO()
    val placeholderRects: List<Rect?> get() = TODO()
    val lineCount: Int get() = TODO()
    fun getLineStart(lineIndex: Int): Int = TODO()
    fun getLineEnd(lineIndex: Int, visibleEnd: Boolean = false): Int = TODO()
    fun isLineEllipsized(lineIndex: Int): Boolean = TODO()
    fun getLineTop(lineIndex: Int): Float = TODO()
    fun getLineBottom(lineIndex: Int): Float = TODO()
    fun getLineLeft(lineIndex: Int): Float = TODO()
    fun getLineRight(lineIndex: Int): Float = TODO()
    fun getLineBaseline(lineIndex: Int): Float = TODO()
    fun getLineForOffset(offset: Int): Int = TODO()
    fun getLineForVerticalPosition(vertical: Float): Int = TODO()
    fun getHorizontalPosition(offset: Int, usePrimaryDirection: Boolean): Float = TODO()
    fun getParagraphDirection(offset: Int): ResolvedTextDirection = TODO()
    fun getBidiRunDirection(offset: Int): ResolvedTextDirection = TODO()
    fun getOffsetForPosition(position: Offset): Int = TODO()
    fun getBoundingBox(offset: Int): Rect = TODO()
    fun getWordBoundary(offset: Int): TextRange = TODO()
    fun getCursorRect(offset: Int): Rect = TODO()
    fun getPathForRange(start: Int, end: Int): androidx.compose.ui.graphics.Path = TODO()
    fun copy(layoutInput: TextLayoutInput = this.layoutInput, size: IntSize = this.size): TextLayoutResult = TODO()
}
@Immutable class TextMeasurer(defaultFontFamilyResolver: FontFamily.Resolver, defaultDensity: Density, defaultLayoutDirection: LayoutDirection, cacheSize: Int = 8) {
    fun measure(
        text: AnnotatedString, style: TextStyle = TextStyle.Default, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true, maxLines: Int = Int.MAX_VALUE,
        placeholders: List<AnnotatedString.Range<Placeholder>> = emptyList(), constraints: Constraints = Constraints(), layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        density: Density = TODO(), fontFamilyResolver: FontFamily.Resolver = TODO(), skipCache: Boolean = false,
    ): TextLayoutResult = TODO()
    fun measure(
        text: String, style: TextStyle = TextStyle.Default, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true, maxLines: Int = Int.MAX_VALUE,
        constraints: Constraints = Constraints(), layoutDirection: LayoutDirection = LayoutDirection.Ltr, density: Density = TODO(),
        fontFamilyResolver: FontFamily.Resolver = TODO(), skipCache: Boolean = false,
    ): TextLayoutResult = TODO()
}
class Placeholder(val width: TextUnit, val height: TextUnit, val placeholderVerticalAlign: PlaceholderVerticalAlign)
@JvmInline value class PlaceholderVerticalAlign internal constructor(internal val value: Int) { companion object { val AboveBaseline = PlaceholderVerticalAlign(1); val Top = PlaceholderVerticalAlign(2); val Bottom = PlaceholderVerticalAlign(3); val Center = PlaceholderVerticalAlign(4); val TextTop = PlaceholderVerticalAlign(5); val TextBottom = PlaceholderVerticalAlign(6); val TextCenter = PlaceholderVerticalAlign(7) } }
@Composable fun rememberTextMeasurer(cacheSize: Int = 8): TextMeasurer = TODO()
fun DrawScope.drawText(textMeasurer: TextMeasurer, text: AnnotatedString, topLeft: Offset = Offset.Zero, style: TextStyle = TextStyle.Default, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true, maxLines: Int = Int.MAX_VALUE, placeholders: List<AnnotatedString.Range<Placeholder>> = emptyList(), size: Size = Size.Unspecified, blendMode: BlendMode = DrawScope.DefaultBlendMode): Unit = TODO()
fun DrawScope.drawText(textMeasurer: TextMeasurer, text: String, topLeft: Offset = Offset.Zero, style: TextStyle = TextStyle.Default, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true, maxLines: Int = Int.MAX_VALUE, size: Size = Size.Unspecified, blendMode: BlendMode = DrawScope.DefaultBlendMode): Unit = TODO()
fun DrawScope.drawText(textLayoutResult: TextLayoutResult, color: Color = Color.Unspecified, topLeft: Offset = Offset.Zero, alpha: Float = Float.NaN, shadow: Shadow? = null, textDecoration: TextDecoration? = null, drawStyle: DrawStyle? = null, blendMode: BlendMode = DrawScope.DefaultBlendMode): Unit = TODO()
fun DrawScope.drawText(textLayoutResult: TextLayoutResult, brush: Brush, topLeft: Offset = Offset.Zero, alpha: Float = Float.NaN, shadow: Shadow? = null, textDecoration: TextDecoration? = null, drawStyle: DrawStyle? = null, blendMode: BlendMode = DrawScope.DefaultBlendMode): Unit = TODO()
@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalTextApi
//// FILE: androidx/compose/ui/text/intl/Locale.kt
package androidx.compose.ui.text.intl
class Locale(languageTag: String) { val language: String get() = TODO(); companion object { val current: Locale get() = TODO() } }
class LocaleList(val localeList: List<Locale>) { constructor(vararg locales: Locale) : this(locales.toList()); companion object { val current: LocaleList get() = TODO() } }
//// FILE: androidx/compose/ui/text/font/Font.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.text.font

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State

@Immutable class FontWeight(val weight: Int) : Comparable<FontWeight> {
    override operator fun compareTo(other: FontWeight): Int = weight.compareTo(other.weight)
    companion object {
        val W100 = FontWeight(100); val W200 = FontWeight(200); val W300 = FontWeight(300); val W400 = FontWeight(400); val W500 = FontWeight(500)
        val W600 = FontWeight(600); val W700 = FontWeight(700); val W800 = FontWeight(800); val W900 = FontWeight(900)
        val Thin = W100; val ExtraLight = W200; val Light = W300; val Normal = W400; val Medium = W500; val SemiBold = W600; val Bold = W700; val ExtraBold = W800; val Black = W900
    }
}
fun lerp(start: FontWeight, stop: FontWeight, fraction: Float): FontWeight = TODO()
@JvmInline value class FontStyle(val value: Int) { companion object { val Normal = FontStyle(0); val Italic = FontStyle(1); fun values(): List<FontStyle> = listOf(Normal, Italic) } }
@JvmInline value class FontSynthesis internal constructor(internal val value: Int) { companion object { val None = FontSynthesis(0); val All = FontSynthesis(1); val Weight = FontSynthesis(2); val Style = FontSynthesis(3) } }
@JvmInline value class FontLoadingStrategy private constructor(val value: Int) { companion object { val Blocking = FontLoadingStrategy(0); val OptionalLocal = FontLoadingStrategy(1); val Async = FontLoadingStrategy(2) } }
@Immutable interface Font { val weight: FontWeight; val style: FontStyle; val loadingStrategy: FontLoadingStrategy get() = FontLoadingStrategy.Blocking }
fun Font(resId: Int, weight: FontWeight = FontWeight.Normal, style: FontStyle = FontStyle.Normal, loadingStrategy: FontLoadingStrategy = FontLoadingStrategy.Blocking): Font = TODO()
@Immutable abstract class FontFamily internal constructor(val canLoadSynchronously: Boolean) {
    companion object {
        val Default: SystemFontFamily get() = TODO()
        val SansSerif: GenericFontFamily get() = TODO()
        val Serif: GenericFontFamily get() = TODO()
        val Monospace: GenericFontFamily get() = TODO()
        val Cursive: GenericFontFamily get() = TODO()
    }
    interface Resolver {
        suspend fun preload(fontFamily: FontFamily)
        fun resolve(fontFamily: FontFamily? = null, fontWeight: FontWeight = FontWeight.Normal, fontStyle: FontStyle = FontStyle.Normal, fontSynthesis: FontSynthesis = FontSynthesis.All): State<Any>
    }
}
abstract class SystemFontFamily internal constructor() : FontFamily(true)
class GenericFontFamily internal constructor(val name: String) : SystemFontFamily()
abstract class FileBasedFontFamily internal constructor() : FontFamily(false)
class FontListFontFamily internal constructor(val fonts: List<Font>) : FileBasedFontFamily(), List<Font> by fonts
fun FontFamily(fonts: List<Font>): FontFamily = TODO()
fun FontFamily(vararg fonts: Font): FontFamily = TODO()
fun FontFamily(typeface: Typeface): FontFamily = TODO()
fun FontFamily(typeface: android.graphics.Typeface): FontFamily = TODO()
interface Typeface { val fontFamily: FontFamily? }
fun Typeface(typeface: android.graphics.Typeface): Typeface = TODO()
//// FILE: androidx/compose/ui/text/input/Input.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.text.input

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange

@JvmInline value class ImeAction private constructor(private val value: Int) {
    companion object { val Unspecified = ImeAction(-1); val Default = ImeAction(1); val None = ImeAction(0); val Go = ImeAction(2); val Search = ImeAction(3); val Send = ImeAction(4); val Previous = ImeAction(5); val Next = ImeAction(6); val Done = ImeAction(7) }
}
@JvmInline value class KeyboardCapitalization private constructor(private val value: Int) {
    companion object { val Unspecified = KeyboardCapitalization(-1); val None = KeyboardCapitalization(0); val Characters = KeyboardCapitalization(1); val Words = KeyboardCapitalization(2); val Sentences = KeyboardCapitalization(3) }
}
@JvmInline value class KeyboardType private constructor(private val value: Int) {
    companion object { val Unspecified = KeyboardType(0); val Text = KeyboardType(1); val Ascii = KeyboardType(2); val Number = KeyboardType(3); val Phone = KeyboardType(4); val Uri = KeyboardType(5); val Email = KeyboardType(6); val Password = KeyboardType(7); val NumberPassword = KeyboardType(8); val Decimal = KeyboardType(9) }
}
class PlatformImeOptions(val privateImeOptions: String? = null)
@Immutable class TextFieldValue(val text: String = "", val selection: TextRange = TextRange.Zero, val composition: TextRange? = null) {
    constructor(annotatedString: AnnotatedString, selection: TextRange = TextRange.Zero, composition: TextRange? = null) : this(annotatedString.text, selection, composition)
    val annotatedString: AnnotatedString get() = TODO()
    fun copy(text: String = this.text, selection: TextRange = this.selection, composition: TextRange? = this.composition): TextFieldValue = TODO()
    fun copy(annotatedString: AnnotatedString, selection: TextRange = this.selection, composition: TextRange? = this.composition): TextFieldValue = TODO()
}
fun interface VisualTransformation { fun filter(text: AnnotatedString): TransformedText; companion object { val None: VisualTransformation get() = TODO() } }
class TransformedText(val text: AnnotatedString, val offsetMapping: OffsetMapping)
interface OffsetMapping { fun originalToTransformed(offset: Int): Int; fun transformedToOriginal(offset: Int): Int; companion object { val Identity: OffsetMapping get() = TODO() } }
class PasswordVisualTransformation(val mask: Char = '•') : VisualTransformation { override fun filter(text: AnnotatedString): TransformedText = TODO() }
//// FILE: androidx/compose/ui/text/style/Style.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.ui.text.style

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@JvmInline value class TextAlign internal constructor(internal val value: Int) {
    companion object { val Left = TextAlign(1); val Right = TextAlign(2); val Center = TextAlign(3); val Justify = TextAlign(4); val Start = TextAlign(5); val End = TextAlign(6); val Unspecified = TextAlign(Int.MIN_VALUE); fun values(): List<TextAlign> = TODO() }
}
@JvmInline value class TextOverflow internal constructor(internal val value: Int) { companion object { val Clip = TextOverflow(1); val Ellipsis = TextOverflow(2); val Visible = TextOverflow(3) } }
@JvmInline value class TextDirection internal constructor(internal val value: Int) { companion object { val Ltr = TextDirection(1); val Rtl = TextDirection(2); val Content = TextDirection(3); val ContentOrLtr = TextDirection(4); val ContentOrRtl = TextDirection(5); val Unspecified = TextDirection(Int.MIN_VALUE) } }
enum class ResolvedTextDirection { Ltr, Rtl }
@JvmInline value class Hyphens private constructor(internal val value: Int) { companion object { val None = Hyphens(1); val Auto = Hyphens(2); val Unspecified = Hyphens(Int.MIN_VALUE) } }
@JvmInline value class LineBreak private constructor(internal val mask: Int) { companion object { val Simple = LineBreak(1); val Heading = LineBreak(2); val Paragraph = LineBreak(3); val Unspecified = LineBreak(Int.MIN_VALUE) } }
@Immutable class TextIndent(val firstLine: TextUnit = 0.sp, val restLine: TextUnit = 0.sp) { companion object { val None: TextIndent get() = TODO() } }
@Immutable class TextDecoration internal constructor(val mask: Int) {
    operator fun plus(decoration: TextDecoration): TextDecoration = TODO()
    operator fun contains(other: TextDecoration): Boolean = TODO()
    companion object { val None = TextDecoration(0); val Underline = TextDecoration(1); val LineThrough = TextDecoration(2); fun combine(decorations: List<TextDecoration>): TextDecoration = TODO() }
}
@Immutable @JvmInline value class BaselineShift(val multiplier: Float) { companion object { val Superscript = BaselineShift(0.5f); val Subscript = BaselineShift(-0.5f); val None = BaselineShift(0.0f) } }
@Immutable class TextGeometricTransform(val scaleX: Float = 1.0f, val skewX: Float = 0f)
@Immutable class TextMotion private constructor() { companion object { val Static: TextMotion get() = TODO(); val Animated: TextMotion get() = TODO() } }
class LineHeightStyle(val alignment: Alignment, val trim: Trim) {
    @JvmInline value class Trim private constructor(private val value: Int) { companion object { val FirstLineTop = Trim(1); val LastLineBottom = Trim(16); val Both = Trim(17); val None = Trim(0) } }
    @JvmInline value class Alignment constructor(internal val topRatio: Float) { companion object { val Top = Alignment(0f); val Center = Alignment(0.5f); val Proportional = Alignment(-1f); val Bottom = Alignment(1f) } }
    companion object { val Default: LineHeightStyle get() = TODO() }
}
