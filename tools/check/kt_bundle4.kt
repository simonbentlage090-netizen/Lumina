//// FILE: androidx/compose/animation/core/AnimCore.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.animation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Stable fun interface Easing { fun transform(fraction: Float): Float }
@Immutable class CubicBezierEasing(private val a: Float, private val b: Float, private val c: Float, private val d: Float) : Easing { override fun transform(fraction: Float): Float = TODO() }
val FastOutSlowInEasing: Easing get() = TODO()
val LinearOutSlowInEasing: Easing get() = TODO()
val FastOutLinearInEasing: Easing get() = TODO()
val LinearEasing: Easing get() = TODO()
val Ease: Easing get() = TODO()
val EaseIn: Easing get() = TODO()
val EaseOut: Easing get() = TODO()
val EaseInOut: Easing get() = TODO()
val EaseInCubic: Easing get() = TODO()
val EaseOutCubic: Easing get() = TODO()
val EaseInOutCubic: Easing get() = TODO()
val EaseOutQuart: Easing get() = TODO()
val EaseOutQuint: Easing get() = TODO()
val EaseOutExpo: Easing get() = TODO()
val EaseOutBack: Easing get() = TODO()
val EaseInBack: Easing get() = TODO()
val EaseInOutBack: Easing get() = TODO()
val EaseOutSine: Easing get() = TODO()
val EaseInOutSine: Easing get() = TODO()
val EaseOutCirc: Easing get() = TODO()
val EaseOutBounce: Easing get() = TODO()
val EaseOutElastic: Easing get() = TODO()
val EaseInOutQuad: Easing get() = TODO()
val EaseOutQuad: Easing get() = TODO()

object Spring {
    const val StiffnessHigh = 10_000f
    const val StiffnessMedium = 1500f
    const val StiffnessMediumLow = 400f
    const val StiffnessLow = 200f
    const val StiffnessVeryLow = 50f
    const val DampingRatioHighBouncy = 0.2f
    const val DampingRatioMediumBouncy = 0.5f
    const val DampingRatioLowBouncy = 0.75f
    const val DampingRatioNoBouncy = 1f
    const val DefaultDisplacementThreshold = 0.01f
}
object AnimationConstants { const val DefaultDurationMillis: Int = 300; const val UnspecifiedTime: Long = Long.MIN_VALUE }
enum class RepeatMode { Restart, Reverse }
@JvmInline value class StartOffsetType private constructor(internal val value: Int) { companion object { val Delay = StartOffsetType(-1); val FastForward = StartOffsetType(1) } }
@JvmInline value class StartOffset private constructor(internal val value: Long) {
    constructor(offsetMillis: Int, offsetType: StartOffsetType = StartOffsetType.Delay) : this(offsetMillis.toLong())
    val offsetMillis: Int get() = TODO()
}
abstract class AnimationVector internal constructor()
class AnimationVector1D(initVal: Float) : AnimationVector() { val value: Float get() = TODO() }
class AnimationVector2D(v1: Float, v2: Float) : AnimationVector() { val v1: Float get() = TODO(); val v2: Float get() = TODO() }
class AnimationVector3D(v1: Float, v2: Float, v3: Float) : AnimationVector()
class AnimationVector4D(v1: Float, v2: Float, v3: Float, v4: Float) : AnimationVector()
interface TwoWayConverter<T, V : AnimationVector> { val convertToVector: (T) -> V; val convertFromVector: (V) -> T }
fun <T, V : AnimationVector> TwoWayConverter(convertToVector: (T) -> V, convertFromVector: (V) -> T): TwoWayConverter<T, V> = TODO()
val Float.Companion.VectorConverter: TwoWayConverter<Float, AnimationVector1D> get() = TODO()
val Int.Companion.VectorConverter: TwoWayConverter<Int, AnimationVector1D> get() = TODO()
val Dp.Companion.VectorConverter: TwoWayConverter<Dp, AnimationVector1D> get() = TODO()
val Offset.Companion.VectorConverter: TwoWayConverter<Offset, AnimationVector2D> get() = TODO()
val IntOffset.Companion.VectorConverter: TwoWayConverter<IntOffset, AnimationVector2D> get() = TODO()
val Size.Companion.VectorConverter: TwoWayConverter<Size, AnimationVector2D> get() = TODO()
val IntSize.Companion.VectorConverter: TwoWayConverter<IntSize, AnimationVector2D> get() = TODO()
val IntOffset.Companion.VisibilityThreshold: IntOffset get() = TODO()
val IntSize.Companion.VisibilityThreshold: IntSize get() = TODO()
val Offset.Companion.VisibilityThreshold: Offset get() = TODO()

interface AnimationSpec<T>
interface FiniteAnimationSpec<T> : AnimationSpec<T>
interface DurationBasedAnimationSpec<T> : FiniteAnimationSpec<T>
@Immutable class TweenSpec<T>(val durationMillis: Int = AnimationConstants.DefaultDurationMillis, val delay: Int = 0, val easing: Easing = FastOutSlowInEasing) : DurationBasedAnimationSpec<T>
@Immutable class SpringSpec<T>(val dampingRatio: Float = Spring.DampingRatioNoBouncy, val stiffness: Float = Spring.StiffnessMedium, val visibilityThreshold: T? = null) : FiniteAnimationSpec<T>
@Immutable class SnapSpec<T>(val delay: Int = 0) : DurationBasedAnimationSpec<T>
@Immutable class RepeatableSpec<T>(val iterations: Int, val animation: DurationBasedAnimationSpec<T>, val repeatMode: RepeatMode = RepeatMode.Restart, val initialStartOffset: StartOffset = StartOffset(0)) : FiniteAnimationSpec<T>
@Immutable class InfiniteRepeatableSpec<T>(val animation: DurationBasedAnimationSpec<T>, val repeatMode: RepeatMode = RepeatMode.Restart, val initialStartOffset: StartOffset = StartOffset(0)) : AnimationSpec<T>
class KeyframeEntity<T> internal constructor()
@Immutable class KeyframesSpec<T>(val config: KeyframesSpecConfig<T>) : DurationBasedAnimationSpec<T> {
    class KeyframesSpecConfig<T> {
        var durationMillis: Int = AnimationConstants.DefaultDurationMillis
        var delayMillis: Int = 0
        infix fun T.at(timeStamp: Int): KeyframeEntity<T> = TODO()
        infix fun T.atFraction(fraction: Float): KeyframeEntity<T> = TODO()
        infix fun KeyframeEntity<T>.using(easing: Easing): KeyframeEntity<T> = TODO()
        @Deprecated("Use using") infix fun KeyframeEntity<T>.with(easing: Easing) {}
    }
}
fun <T> tween(durationMillis: Int = AnimationConstants.DefaultDurationMillis, delayMillis: Int = 0, easing: Easing = FastOutSlowInEasing): TweenSpec<T> = TODO()
fun <T> spring(dampingRatio: Float = Spring.DampingRatioNoBouncy, stiffness: Float = Spring.StiffnessMedium, visibilityThreshold: T? = null): SpringSpec<T> = TODO()
fun <T> keyframes(init: KeyframesSpec.KeyframesSpecConfig<T>.() -> Unit): KeyframesSpec<T> = TODO()
fun <T> repeatable(iterations: Int, animation: DurationBasedAnimationSpec<T>, repeatMode: RepeatMode = RepeatMode.Restart, initialStartOffset: StartOffset = StartOffset(0)): RepeatableSpec<T> = TODO()
fun <T> infiniteRepeatable(animation: DurationBasedAnimationSpec<T>, repeatMode: RepeatMode = RepeatMode.Restart, initialStartOffset: StartOffset = StartOffset(0)): InfiniteRepeatableSpec<T> = TODO()
fun <T> snap(delayMillis: Int = 0): SnapSpec<T> = TODO()

interface DecayAnimationSpec<T>
fun <T> exponentialDecay(frictionMultiplier: Float = 1f, absVelocityThreshold: Float = 0.1f): DecayAnimationSpec<T> = TODO()
enum class AnimationEndReason { BoundReached, Finished }
class AnimationState<T, V : AnimationVector>(val typeConverter: TwoWayConverter<T, V>, initialValue: T) { val value: T get() = TODO(); val velocity: T get() = TODO(); val isRunning: Boolean get() = TODO() }
class AnimationResult<T, V : AnimationVector>(val endState: AnimationState<T, V>, val endReason: AnimationEndReason)
class Animatable<T, V : AnimationVector>(initialValue: T, val typeConverter: TwoWayConverter<T, V>, private val visibilityThreshold: T? = null, val label: String = "Animatable") {
    val value: T get() = TODO()
    val velocityVector: V get() = TODO()
    val velocity: T get() = TODO()
    var targetValue: T
        get() = TODO()
        private set(v) {}
    val isRunning: Boolean get() = TODO()
    val lowerBound: T? get() = TODO()
    val upperBound: T? get() = TODO()
    fun updateBounds(lowerBound: T? = this.lowerBound, upperBound: T? = this.upperBound) {}
    suspend fun animateTo(targetValue: T, animationSpec: AnimationSpec<T> = TODO(), initialVelocity: T = velocity, block: (Animatable<T, V>.() -> Unit)? = null): AnimationResult<T, V> = TODO()
    suspend fun animateDecay(initialVelocity: T, animationSpec: DecayAnimationSpec<T>, block: (Animatable<T, V>.() -> Unit)? = null): AnimationResult<T, V> = TODO()
    suspend fun snapTo(targetValue: T) {}
    suspend fun stop() {}
    fun asState(): State<T> = TODO()
}
fun Animatable(initialValue: Float, visibilityThreshold: Float = Spring.DefaultDisplacementThreshold): Animatable<Float, AnimationVector1D> = TODO()

@Composable fun animateFloatAsState(targetValue: Float, animationSpec: AnimationSpec<Float> = TODO(), visibilityThreshold: Float = 0.01f, label: String = "FloatAnimation", finishedListener: ((Float) -> Unit)? = null): State<Float> = TODO()
@Composable fun animateDpAsState(targetValue: Dp, animationSpec: AnimationSpec<Dp> = TODO(), label: String = "DpAnimation", finishedListener: ((Dp) -> Unit)? = null): State<Dp> = TODO()
@Composable fun animateIntAsState(targetValue: Int, animationSpec: AnimationSpec<Int> = TODO(), label: String = "IntAnimation", finishedListener: ((Int) -> Unit)? = null): State<Int> = TODO()
@Composable fun animateOffsetAsState(targetValue: Offset, animationSpec: AnimationSpec<Offset> = TODO(), label: String = "OffsetAnimation", finishedListener: ((Offset) -> Unit)? = null): State<Offset> = TODO()
@Composable fun animateIntOffsetAsState(targetValue: IntOffset, animationSpec: AnimationSpec<IntOffset> = TODO(), label: String = "IntOffsetAnimation", finishedListener: ((IntOffset) -> Unit)? = null): State<IntOffset> = TODO()
@Composable fun animateSizeAsState(targetValue: Size, animationSpec: AnimationSpec<Size> = TODO(), label: String = "SizeAnimation", finishedListener: ((Size) -> Unit)? = null): State<Size> = TODO()
@Composable fun <T, V : AnimationVector> animateValueAsState(targetValue: T, typeConverter: TwoWayConverter<T, V>, animationSpec: AnimationSpec<T> = TODO(), visibilityThreshold: T? = null, label: String = "ValueAnimation", finishedListener: ((T) -> Unit)? = null): State<T> = TODO()

class InfiniteTransition internal constructor(val label: String)
@Composable fun rememberInfiniteTransition(label: String = "InfiniteTransition"): InfiniteTransition = TODO()
@Composable fun InfiniteTransition.animateFloat(initialValue: Float, targetValue: Float, animationSpec: InfiniteRepeatableSpec<Float>, label: String = "FloatAnimation"): State<Float> = TODO()
@Composable fun <T, V : AnimationVector> InfiniteTransition.animateValue(initialValue: T, targetValue: T, typeConverter: TwoWayConverter<T, V>, animationSpec: InfiniteRepeatableSpec<T>, label: String = "ValueAnimation"): State<T> = TODO()

abstract class TransitionState<S> internal constructor() { abstract val currentState: S; abstract val targetState: S }
class MutableTransitionState<S>(initialState: S) : TransitionState<S>() {
    override val currentState: S get() = TODO()
    override var targetState: S
        get() = TODO()
        set(v) {}
    val isIdle: Boolean get() = TODO()
}
class Transition<S> internal constructor() {
    val currentState: S get() = TODO()
    val targetState: S get() = TODO()
    val isRunning: Boolean get() = TODO()
    val segment: Segment<S> get() = TODO()
    val label: String? get() = null
    interface Segment<S> { val initialState: S; val targetState: S; infix fun S.isTransitioningTo(targetState: S): Boolean = TODO() }
}
@Composable fun <T> updateTransition(targetState: T, label: String? = null): Transition<T> = TODO()
@Composable fun <T> rememberTransition(transitionState: TransitionState<T>, label: String? = null): Transition<T> = TODO()
@Composable inline fun <S> Transition<S>.animateFloat(noinline transitionSpec: @Composable Transition.Segment<S>.() -> FiniteAnimationSpec<Float> = { spring() }, label: String = "FloatAnimation", targetValueByState: @Composable (state: S) -> Float): State<Float> = TODO()
@Composable inline fun <S> Transition<S>.animateDp(noinline transitionSpec: @Composable Transition.Segment<S>.() -> FiniteAnimationSpec<Dp> = { spring() }, label: String = "DpAnimation", targetValueByState: @Composable (state: S) -> Dp): State<Dp> = TODO()
@Composable inline fun <S> Transition<S>.animateOffset(noinline transitionSpec: @Composable Transition.Segment<S>.() -> FiniteAnimationSpec<Offset> = { spring() }, label: String = "OffsetAnimation", targetValueByState: @Composable (state: S) -> Offset): State<Offset> = TODO()
//// FILE: androidx/compose/animation/Anim.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Immutable abstract class EnterTransition internal constructor() { operator fun plus(enter: EnterTransition): EnterTransition = TODO(); companion object { val None: EnterTransition get() = TODO() } }
@Immutable abstract class ExitTransition internal constructor() { operator fun plus(exit: ExitTransition): ExitTransition = TODO(); companion object { val None: ExitTransition get() = TODO() } }
fun fadeIn(animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow), initialAlpha: Float = 0f): EnterTransition = TODO()
fun fadeOut(animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow), targetAlpha: Float = 0f): ExitTransition = TODO()
fun slideIn(animationSpec: FiniteAnimationSpec<IntOffset> = spring(), initialOffset: (fullSize: IntSize) -> IntOffset): EnterTransition = TODO()
fun slideOut(animationSpec: FiniteAnimationSpec<IntOffset> = spring(), targetOffset: (fullSize: IntSize) -> IntOffset): ExitTransition = TODO()
fun scaleIn(animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow), initialScale: Float = 0f, transformOrigin: TransformOrigin = TransformOrigin.Center): EnterTransition = TODO()
fun scaleOut(animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow), targetScale: Float = 0f, transformOrigin: TransformOrigin = TransformOrigin.Center): ExitTransition = TODO()
fun expandIn(animationSpec: FiniteAnimationSpec<IntSize> = spring(), expandFrom: Alignment = Alignment.BottomEnd, clip: Boolean = true, initialSize: (fullSize: IntSize) -> IntSize = { IntSize(0, 0) }): EnterTransition = TODO()
fun shrinkOut(animationSpec: FiniteAnimationSpec<IntSize> = spring(), shrinkTowards: Alignment = Alignment.BottomEnd, clip: Boolean = true, targetSize: (fullSize: IntSize) -> IntSize = { IntSize(0, 0) }): ExitTransition = TODO()
fun expandHorizontally(animationSpec: FiniteAnimationSpec<IntSize> = spring(), expandFrom: Alignment.Horizontal = Alignment.End, clip: Boolean = true, initialWidth: (fullWidth: Int) -> Int = { 0 }): EnterTransition = TODO()
fun expandVertically(animationSpec: FiniteAnimationSpec<IntSize> = spring(), expandFrom: Alignment.Vertical = Alignment.Bottom, clip: Boolean = true, initialHeight: (fullHeight: Int) -> Int = { 0 }): EnterTransition = TODO()
fun shrinkHorizontally(animationSpec: FiniteAnimationSpec<IntSize> = spring(), shrinkTowards: Alignment.Horizontal = Alignment.End, clip: Boolean = true, targetWidth: (fullWidth: Int) -> Int = { 0 }): ExitTransition = TODO()
fun shrinkVertically(animationSpec: FiniteAnimationSpec<IntSize> = spring(), shrinkTowards: Alignment.Vertical = Alignment.Bottom, clip: Boolean = true, targetHeight: (fullHeight: Int) -> Int = { 0 }): ExitTransition = TODO()
fun slideInHorizontally(animationSpec: FiniteAnimationSpec<IntOffset> = spring(), initialOffsetX: (fullWidth: Int) -> Int = { -it / 2 }): EnterTransition = TODO()
fun slideInVertically(animationSpec: FiniteAnimationSpec<IntOffset> = spring(), initialOffsetY: (fullHeight: Int) -> Int = { -it / 2 }): EnterTransition = TODO()
fun slideOutHorizontally(animationSpec: FiniteAnimationSpec<IntOffset> = spring(), targetOffsetX: (fullWidth: Int) -> Int = { -it / 2 }): ExitTransition = TODO()
fun slideOutVertically(animationSpec: FiniteAnimationSpec<IntOffset> = spring(), targetOffsetY: (fullHeight: Int) -> Int = { -it / 2 }): ExitTransition = TODO()

enum class EnterExitState { PreEnter, Visible, PostExit }
interface AnimatedVisibilityScope {
    val transition: Transition<EnterExitState>
    fun Modifier.animateEnterExit(enter: EnterTransition = fadeIn(), exit: ExitTransition = fadeOut(), label: String = "animateEnterExit"): Modifier = TODO()
}
@Composable fun AnimatedVisibility(visible: Boolean, modifier: Modifier = Modifier, enter: EnterTransition = fadeIn() + expandIn(), exit: ExitTransition = shrinkOut() + fadeOut(), label: String = "AnimatedVisibility", content: @Composable AnimatedVisibilityScope.() -> Unit): Unit = TODO()
@Composable fun RowScope.AnimatedVisibility(visible: Boolean, modifier: Modifier = Modifier, enter: EnterTransition = fadeIn() + expandHorizontally(), exit: ExitTransition = fadeOut() + shrinkHorizontally(), label: String = "AnimatedVisibility", content: @Composable AnimatedVisibilityScope.() -> Unit): Unit = TODO()
@Composable fun ColumnScope.AnimatedVisibility(visible: Boolean, modifier: Modifier = Modifier, enter: EnterTransition = fadeIn() + expandVertically(), exit: ExitTransition = fadeOut() + shrinkVertically(), label: String = "AnimatedVisibility", content: @Composable AnimatedVisibilityScope.() -> Unit): Unit = TODO()
@Composable fun AnimatedVisibility(visibleState: MutableTransitionState<Boolean>, modifier: Modifier = Modifier, enter: EnterTransition = fadeIn() + expandIn(), exit: ExitTransition = fadeOut() + shrinkOut(), label: String = "AnimatedVisibility", content: @Composable AnimatedVisibilityScope.() -> Unit): Unit = TODO()
@Composable fun RowScope.AnimatedVisibility(visibleState: MutableTransitionState<Boolean>, modifier: Modifier = Modifier, enter: EnterTransition = expandHorizontally() + fadeIn(), exit: ExitTransition = shrinkHorizontally() + fadeOut(), label: String = "AnimatedVisibility", content: @Composable AnimatedVisibilityScope.() -> Unit): Unit = TODO()
@Composable fun ColumnScope.AnimatedVisibility(visibleState: MutableTransitionState<Boolean>, modifier: Modifier = Modifier, enter: EnterTransition = expandVertically() + fadeIn(), exit: ExitTransition = shrinkVertically() + fadeOut(), label: String = "AnimatedVisibility", content: @Composable AnimatedVisibilityScope.() -> Unit): Unit = TODO()

interface SizeTransform { val clip: Boolean; fun createAnimationSpec(initialSize: IntSize, targetSize: IntSize): FiniteAnimationSpec<IntSize> }
fun SizeTransform(clip: Boolean = true, sizeAnimationSpec: (initialSize: IntSize, targetSize: IntSize) -> FiniteAnimationSpec<IntSize> = { _, _ -> spring() }): SizeTransform = TODO()
class ContentTransform(val targetContentEnter: EnterTransition, val initialContentExit: ExitTransition, targetContentZIndex: Float = 0f, sizeTransform: SizeTransform? = SizeTransform()) {
    var targetContentZIndex: Float = targetContentZIndex
    var sizeTransform: SizeTransform? = sizeTransform; internal set
}
infix fun EnterTransition.togetherWith(exit: ExitTransition): ContentTransform = TODO()
interface AnimatedContentScope : AnimatedVisibilityScope
interface AnimatedContentTransitionScope<S> : Transition.Segment<S> {
    infix fun ContentTransform.using(sizeTransform: SizeTransform?): ContentTransform
    @JvmInline value class SlideDirection internal constructor(private val value: Int) { companion object { val Left = SlideDirection(0); val Right = SlideDirection(1); val Up = SlideDirection(2); val Down = SlideDirection(3); val Start = SlideDirection(4); val End = SlideDirection(5) } }
    fun slideIntoContainer(towards: SlideDirection, animationSpec: FiniteAnimationSpec<IntOffset> = spring(), initialOffset: (offsetForFullSlide: Int) -> Int = { it }): EnterTransition
    fun slideOutOfContainer(towards: SlideDirection, animationSpec: FiniteAnimationSpec<IntOffset> = spring(), targetOffset: (offsetForFullSlide: Int) -> Int = { it }): ExitTransition
    val contentAlignment: Alignment
}
@Composable fun <S> AnimatedContent(
    targetState: S, modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<S>.() -> ContentTransform = { fadeIn(tween(220, delayMillis = 90)) togetherWith fadeOut(tween(90)) },
    contentAlignment: Alignment = Alignment.TopStart, label: String = "AnimatedContent", contentKey: (targetState: S) -> Any? = { it },
    content: @Composable AnimatedContentScope.(targetState: S) -> Unit,
): Unit = TODO()
@Composable fun <T> Crossfade(targetState: T, modifier: Modifier = Modifier, animationSpec: FiniteAnimationSpec<Float> = tween(), label: String = "Crossfade", content: @Composable (T) -> Unit): Unit = TODO()
@Composable fun animateColorAsState(targetValue: Color, animationSpec: AnimationSpec<Color> = spring(), label: String = "ColorAnimation", finishedListener: ((Color) -> Unit)? = null): State<Color> = TODO()
@Composable fun InfiniteTransition.animateColor(initialValue: Color, targetValue: Color, animationSpec: InfiniteRepeatableSpec<Color>, label: String = "ColorAnimation"): State<Color> = TODO()
@Composable inline fun <S> Transition<S>.animateColor(noinline transitionSpec: @Composable Transition.Segment<S>.() -> FiniteAnimationSpec<Color> = { spring() }, label: String = "ColorAnimation", targetValueByState: @Composable (state: S) -> Color): State<Color> = TODO()
fun Animatable(initialValue: Color): Animatable<Color, AnimationVector4D> = TODO()
val Color.Companion.VectorConverter: (colorSpace: ColorSpace) -> TwoWayConverter<Color, AnimationVector4D> get() = TODO()
fun Modifier.animateContentSize(animationSpec: FiniteAnimationSpec<IntSize> = spring(), finishedListener: ((initialValue: IntSize, targetValue: IntSize) -> Unit)? = null): Modifier = TODO()
@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalAnimationApi
//// FILE: androidx/compose/foundation/Foundation.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalFoundationApi
@RequiresOptIn(level = RequiresOptIn.Level.ERROR) annotation class InternalFoundationApi

@Composable fun Canvas(modifier: Modifier, onDraw: DrawScope.() -> Unit): Unit = TODO()
@Composable fun Canvas(modifier: Modifier, contentDescription: String, onDraw: DrawScope.() -> Unit): Unit = TODO()
@Composable fun Image(bitmap: ImageBitmap, contentDescription: String?, modifier: Modifier = Modifier, alignment: Alignment = Alignment.Center, contentScale: ContentScale = ContentScale.Fit, alpha: Float = DefaultAlpha, colorFilter: ColorFilter? = null, filterQuality: FilterQuality = FilterQuality.Low): Unit = TODO()
@Composable fun Image(painter: Painter, contentDescription: String?, modifier: Modifier = Modifier, alignment: Alignment = Alignment.Center, contentScale: ContentScale = ContentScale.Fit, alpha: Float = DefaultAlpha, colorFilter: ColorFilter? = null): Unit = TODO()
@Composable fun Image(imageVector: ImageVector, contentDescription: String?, modifier: Modifier = Modifier, alignment: Alignment = Alignment.Center, contentScale: ContentScale = ContentScale.Fit, alpha: Float = DefaultAlpha, colorFilter: ColorFilter? = null): Unit = TODO()
fun Modifier.background(color: Color, shape: Shape = RectangleShape): Modifier = TODO()
fun Modifier.background(brush: Brush, shape: Shape = RectangleShape, alpha: Float = 1.0f): Modifier = TODO()
class BorderStroke(val width: Dp, val brush: Brush) { fun copy(width: Dp = this.width, brush: Brush = this.brush): BorderStroke = TODO() }
fun BorderStroke(width: Dp, color: Color): BorderStroke = TODO()
fun Modifier.border(border: BorderStroke, shape: Shape = RectangleShape): Modifier = TODO()
fun Modifier.border(width: Dp, color: Color, shape: Shape = RectangleShape): Modifier = TODO()
fun Modifier.border(width: Dp, brush: Brush, shape: Shape): Modifier = TODO()
interface Indication
val LocalIndication: ProvidableCompositionLocal<Indication> = staticCompositionLocalOf { TODO() }
fun Modifier.indication(interactionSource: InteractionSource, indication: Indication?): Modifier = TODO()
fun Modifier.clickable(enabled: Boolean = true, onClickLabel: String? = null, role: Role? = null, onClick: () -> Unit): Modifier = TODO()
fun Modifier.clickable(interactionSource: MutableInteractionSource?, indication: Indication?, enabled: Boolean = true, onClickLabel: String? = null, role: Role? = null, onClick: () -> Unit): Modifier = TODO()
fun Modifier.combinedClickable(enabled: Boolean = true, onClickLabel: String? = null, role: Role? = null, onLongClickLabel: String? = null, onLongClick: (() -> Unit)? = null, onDoubleClick: (() -> Unit)? = null, onClick: () -> Unit): Modifier = TODO()
fun Modifier.combinedClickable(interactionSource: MutableInteractionSource?, indication: Indication?, enabled: Boolean = true, onClickLabel: String? = null, role: Role? = null, onLongClickLabel: String? = null, onLongClick: (() -> Unit)? = null, onDoubleClick: (() -> Unit)? = null, onClick: () -> Unit): Modifier = TODO()
fun Modifier.focusable(enabled: Boolean = true, interactionSource: MutableInteractionSource? = null): Modifier = TODO()
fun Modifier.hoverable(interactionSource: MutableInteractionSource, enabled: Boolean = true): Modifier = TODO()
@Composable @ReadOnlyComposable fun isSystemInDarkTheme(): Boolean = TODO()
@Composable fun rememberScrollState(initial: Int = 0): ScrollState = TODO()
class ScrollState(initial: Int) : ScrollableState {
    val value: Int get() = TODO()
    val maxValue: Int get() = TODO()
    val viewportSize: Int get() = TODO()
    val interactionSource: InteractionSource get() = TODO()
    suspend fun animateScrollTo(value: Int, animationSpec: AnimationSpec<Float> = SpringSpec()) {}
    suspend fun scrollTo(value: Int): Float = TODO()
    override val isScrollInProgress: Boolean get() = TODO()
    override fun dispatchRawDelta(delta: Float): Float = TODO()
    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {}
}
enum class MutatePriority { Default, UserInput, PreventUserInput }
fun Modifier.verticalScroll(state: ScrollState, enabled: Boolean = true, flingBehavior: FlingBehavior? = null, reverseScrolling: Boolean = false): Modifier = TODO()
fun Modifier.horizontalScroll(state: ScrollState, enabled: Boolean = true, flingBehavior: FlingBehavior? = null, reverseScrolling: Boolean = false): Modifier = TODO()
fun Modifier.basicMarquee(iterations: Int = 3): Modifier = TODO()
//// FILE: androidx/compose/foundation/interaction/Interaction.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.Flow

interface Interaction
interface InteractionSource { val interactions: Flow<Interaction> }
interface MutableInteractionSource : InteractionSource { suspend fun emit(interaction: Interaction); fun tryEmit(interaction: Interaction): Boolean }
fun MutableInteractionSource(): MutableInteractionSource = TODO()
interface PressInteraction : Interaction {
    class Press(val pressPosition: Offset) : PressInteraction
    class Release(val press: Press) : PressInteraction
    class Cancel(val press: Press) : PressInteraction
}
interface DragInteraction : Interaction { class Start : DragInteraction; class Stop(val start: Start) : DragInteraction; class Cancel(val start: Start) : DragInteraction }
@Composable fun InteractionSource.collectIsPressedAsState(): State<Boolean> = TODO()
@Composable fun InteractionSource.collectIsFocusedAsState(): State<Boolean> = TODO()
@Composable fun InteractionSource.collectIsHoveredAsState(): State<Boolean> = TODO()
@Composable fun InteractionSource.collectIsDraggedAsState(): State<Boolean> = TODO()
//// FILE: androidx/compose/foundation/gestures/Gestures.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.gestures

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope

enum class Orientation { Vertical, Horizontal }
suspend fun PointerInputScope.awaitEachGesture(block: suspend AwaitPointerEventScope.() -> Unit): Unit = TODO()
suspend fun AwaitPointerEventScope.awaitFirstDown(requireUnconsumed: Boolean = true, pass: PointerEventPass = PointerEventPass.Main): PointerInputChange = TODO()
suspend fun AwaitPointerEventScope.waitForUpOrCancellation(pass: PointerEventPass = PointerEventPass.Main): PointerInputChange? = TODO()
fun PointerEvent.calculatePan(): Offset = TODO()
fun PointerEvent.calculateZoom(): Float = TODO()
fun PointerEvent.calculateRotation(): Float = TODO()
fun PointerEvent.calculateCentroid(useCurrent: Boolean = true): Offset = TODO()
interface PressGestureScope : Density { suspend fun awaitRelease(); suspend fun tryAwaitRelease(): Boolean }
suspend fun PointerInputScope.detectTapGestures(onDoubleTap: ((Offset) -> Unit)? = null, onLongPress: ((Offset) -> Unit)? = null, onPress: suspend PressGestureScope.(Offset) -> Unit = {}, onTap: ((Offset) -> Unit)? = null): Unit = TODO()
suspend fun PointerInputScope.detectHorizontalDragGestures(onDragStart: (Offset) -> Unit = { }, onDragEnd: () -> Unit = { }, onDragCancel: () -> Unit = { }, onHorizontalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit): Unit = TODO()
suspend fun PointerInputScope.detectVerticalDragGestures(onDragStart: (Offset) -> Unit = { }, onDragEnd: () -> Unit = { }, onDragCancel: () -> Unit = { }, onVerticalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit): Unit = TODO()
suspend fun PointerInputScope.detectDragGestures(onDragStart: (Offset) -> Unit = { }, onDragEnd: () -> Unit = { }, onDragCancel: () -> Unit = { }, onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit): Unit = TODO()
suspend fun PointerInputScope.detectTransformGestures(panZoomLock: Boolean = false, onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit): Unit = TODO()
interface DragScope { fun dragBy(pixels: Float) }
interface DraggableState { suspend fun drag(dragPriority: MutatePriority = MutatePriority.Default, block: suspend DragScope.() -> Unit); fun dispatchRawDelta(delta: Float) }
fun DraggableState(onDelta: (Float) -> Unit): DraggableState = TODO()
@Composable fun rememberDraggableState(onDelta: (Float) -> Unit): DraggableState = TODO()
fun Modifier.draggable(state: DraggableState, orientation: Orientation, enabled: Boolean = true, interactionSource: MutableInteractionSource? = null, startDragImmediately: Boolean = false, onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = {}, onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {}, reverseDirection: Boolean = false): Modifier = TODO()
interface ScrollScope { fun scrollBy(pixels: Float): Float }
interface ScrollableState {
    suspend fun scroll(scrollPriority: MutatePriority = MutatePriority.Default, block: suspend ScrollScope.() -> Unit)
    fun dispatchRawDelta(delta: Float): Float
    val isScrollInProgress: Boolean
    val canScrollForward: Boolean get() = true
    val canScrollBackward: Boolean get() = true
    val lastScrolledForward: Boolean get() = false
    val lastScrolledBackward: Boolean get() = false
}
suspend fun ScrollableState.animateScrollBy(value: Float, animationSpec: AnimationSpec<Float> = spring()): Float = TODO()
suspend fun ScrollableState.scrollBy(value: Float): Float = TODO()
suspend fun ScrollableState.stopScroll(scrollPriority: MutatePriority = MutatePriority.Default): Unit = TODO()
interface FlingBehavior { suspend fun ScrollScope.performFling(initialVelocity: Float): Float }
interface TargetedFlingBehavior : FlingBehavior
fun Modifier.scrollable(state: ScrollableState, orientation: Orientation, enabled: Boolean = true, reverseDirection: Boolean = false, flingBehavior: FlingBehavior? = null, interactionSource: MutableInteractionSource? = null): Modifier = TODO()
@Composable fun rememberScrollableState(consumeScrollDelta: (Float) -> Float): ScrollableState = TODO()
//// FILE: androidx/compose/foundation/gestures/snapping/Snapping.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.gestures.snapping

import androidx.compose.runtime.Composable
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.lazy.LazyListState

interface SnapPosition { companion object { val Center: SnapPosition get() = TODO(); val Start: SnapPosition get() = TODO(); val End: SnapPosition get() = TODO() } }
interface SnapLayoutInfoProvider
@Composable fun rememberSnapFlingBehavior(lazyListState: LazyListState, snapPosition: SnapPosition = SnapPosition.Center): TargetedFlingBehavior = TODO()
@Composable fun rememberSnapFlingBehavior(snapLayoutInfoProvider: SnapLayoutInfoProvider): TargetedFlingBehavior = TODO()
//// FILE: androidx/compose/foundation/layout/Layout.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalLayoutApi

@Immutable object Arrangement {
    @Stable interface Horizontal { val spacing: Dp get() = 0.dp; fun Density.arrange(totalSize: Int, sizes: IntArray, layoutDirection: LayoutDirection, outPositions: IntArray) }
    @Stable interface Vertical { val spacing: Dp get() = 0.dp; fun Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) }
    @Stable interface HorizontalOrVertical : Horizontal, Vertical { override val spacing: Dp get() = 0.dp }
    val Start: Horizontal get() = TODO()
    val End: Horizontal get() = TODO()
    val Top: Vertical get() = TODO()
    val Bottom: Vertical get() = TODO()
    val Center: HorizontalOrVertical get() = TODO()
    val SpaceEvenly: HorizontalOrVertical get() = TODO()
    val SpaceBetween: HorizontalOrVertical get() = TODO()
    val SpaceAround: HorizontalOrVertical get() = TODO()
    fun spacedBy(space: Dp): HorizontalOrVertical = TODO()
    fun spacedBy(space: Dp, alignment: Alignment.Horizontal): Horizontal = TODO()
    fun spacedBy(space: Dp, alignment: Alignment.Vertical): Vertical = TODO()
    fun aligned(alignment: Alignment.Horizontal): Horizontal = TODO()
    fun aligned(alignment: Alignment.Vertical): Vertical = TODO()
    object Absolute { val Left: Horizontal get() = TODO(); val Right: Horizontal get() = TODO(); val Center: Horizontal get() = TODO(); val SpaceBetween: Horizontal get() = TODO(); fun spacedBy(space: Dp): HorizontalOrVertical = TODO() }
}
@Stable interface PaddingValues {
    fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp
    fun calculateTopPadding(): Dp
    fun calculateRightPadding(layoutDirection: LayoutDirection): Dp
    fun calculateBottomPadding(): Dp
}
fun PaddingValues.calculateStartPadding(layoutDirection: LayoutDirection): Dp = TODO()
fun PaddingValues.calculateEndPadding(layoutDirection: LayoutDirection): Dp = TODO()
fun PaddingValues(all: Dp): PaddingValues = TODO()
fun PaddingValues(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues = TODO()
fun PaddingValues(start: Dp = 0.dp, top: Dp = 0.dp, end: Dp = 0.dp, bottom: Dp = 0.dp): PaddingValues = TODO()

@Stable interface WindowInsets {
    fun getLeft(density: Density, layoutDirection: LayoutDirection): Int
    fun getTop(density: Density): Int
    fun getRight(density: Density, layoutDirection: LayoutDirection): Int
    fun getBottom(density: Density): Int
    companion object
}
@JvmInline value class WindowInsetsSides private constructor(private val value: Int) {
    operator fun plus(sides: WindowInsetsSides): WindowInsetsSides = TODO()
    companion object { val Start = WindowInsetsSides(9); val End = WindowInsetsSides(6); val Top = WindowInsetsSides(16); val Bottom = WindowInsetsSides(32); val Left = WindowInsetsSides(10); val Right = WindowInsetsSides(5); val Horizontal = WindowInsetsSides(15); val Vertical = WindowInsetsSides(48) }
}
fun WindowInsets(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): WindowInsets = TODO()
fun WindowInsets(left: Dp = 0.dp, top: Dp = 0.dp, right: Dp = 0.dp, bottom: Dp = 0.dp): WindowInsets = TODO()
fun WindowInsets.only(sides: WindowInsetsSides): WindowInsets = TODO()
fun WindowInsets.union(insets: WindowInsets): WindowInsets = TODO()
fun WindowInsets.exclude(insets: WindowInsets): WindowInsets = TODO()
fun WindowInsets.add(insets: WindowInsets): WindowInsets = TODO()
@Composable fun WindowInsets.asPaddingValues(): PaddingValues = TODO()
fun WindowInsets.asPaddingValues(density: Density): PaddingValues = TODO()
val WindowInsets.Companion.statusBars: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.navigationBars: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.systemBars: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.ime: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.displayCutout: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.safeDrawing: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.safeContent: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.safeGestures: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.systemGestures: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.mandatorySystemGestures: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.tappableElement: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.captionBar: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.waterfall: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.navigationBarsIgnoringVisibility: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.statusBarsIgnoringVisibility: WindowInsets @Composable get() = TODO()
val WindowInsets.Companion.isImeVisible: Boolean @Composable get() = TODO()
fun Modifier.windowInsetsPadding(insets: WindowInsets): Modifier = TODO()
fun Modifier.consumeWindowInsets(insets: WindowInsets): Modifier = TODO()
fun Modifier.consumeWindowInsets(paddingValues: PaddingValues): Modifier = TODO()
fun Modifier.windowInsetsTopHeight(insets: WindowInsets): Modifier = TODO()
fun Modifier.windowInsetsBottomHeight(insets: WindowInsets): Modifier = TODO()
fun Modifier.windowInsetsStartWidth(insets: WindowInsets): Modifier = TODO()
fun Modifier.windowInsetsEndWidth(insets: WindowInsets): Modifier = TODO()
fun Modifier.statusBarsPadding(): Modifier = TODO()
fun Modifier.navigationBarsPadding(): Modifier = TODO()
fun Modifier.systemBarsPadding(): Modifier = TODO()
fun Modifier.imePadding(): Modifier = TODO()
fun Modifier.displayCutoutPadding(): Modifier = TODO()
fun Modifier.safeDrawingPadding(): Modifier = TODO()
fun Modifier.safeContentPadding(): Modifier = TODO()
fun Modifier.safeGesturesPadding(): Modifier = TODO()

enum class IntrinsicSize { Min, Max }
fun Modifier.height(intrinsicSize: IntrinsicSize): Modifier = TODO()
fun Modifier.width(intrinsicSize: IntrinsicSize): Modifier = TODO()
fun Modifier.requiredHeight(intrinsicSize: IntrinsicSize): Modifier = TODO()
fun Modifier.requiredWidth(intrinsicSize: IntrinsicSize): Modifier = TODO()
fun Modifier.aspectRatio(ratio: Float, matchHeightConstraintsFirst: Boolean = false): Modifier = TODO()
fun Modifier.fillMaxWidth(fraction: Float = 1f): Modifier = TODO()
fun Modifier.fillMaxHeight(fraction: Float = 1f): Modifier = TODO()
fun Modifier.fillMaxSize(fraction: Float = 1f): Modifier = TODO()
fun Modifier.width(width: Dp): Modifier = TODO()
fun Modifier.height(height: Dp): Modifier = TODO()
fun Modifier.size(size: Dp): Modifier = TODO()
fun Modifier.size(width: Dp, height: Dp): Modifier = TODO()
fun Modifier.size(size: DpSize): Modifier = TODO()
fun Modifier.widthIn(min: Dp = Dp.Unspecified, max: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.heightIn(min: Dp = Dp.Unspecified, max: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.sizeIn(minWidth: Dp = Dp.Unspecified, minHeight: Dp = Dp.Unspecified, maxWidth: Dp = Dp.Unspecified, maxHeight: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.requiredWidth(width: Dp): Modifier = TODO()
fun Modifier.requiredHeight(height: Dp): Modifier = TODO()
fun Modifier.requiredSize(size: Dp): Modifier = TODO()
fun Modifier.requiredSize(width: Dp, height: Dp): Modifier = TODO()
fun Modifier.requiredSize(size: DpSize): Modifier = TODO()
fun Modifier.requiredWidthIn(min: Dp = Dp.Unspecified, max: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.requiredHeightIn(min: Dp = Dp.Unspecified, max: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.requiredSizeIn(minWidth: Dp = Dp.Unspecified, minHeight: Dp = Dp.Unspecified, maxWidth: Dp = Dp.Unspecified, maxHeight: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.defaultMinSize(minWidth: Dp = Dp.Unspecified, minHeight: Dp = Dp.Unspecified): Modifier = TODO()
fun Modifier.wrapContentWidth(align: Alignment.Horizontal = Alignment.CenterHorizontally, unbounded: Boolean = false): Modifier = TODO()
fun Modifier.wrapContentHeight(align: Alignment.Vertical = Alignment.CenterVertically, unbounded: Boolean = false): Modifier = TODO()
fun Modifier.wrapContentSize(align: Alignment = Alignment.Center, unbounded: Boolean = false): Modifier = TODO()
fun Modifier.padding(start: Dp = 0.dp, top: Dp = 0.dp, end: Dp = 0.dp, bottom: Dp = 0.dp): Modifier = TODO()
fun Modifier.padding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): Modifier = TODO()
fun Modifier.padding(all: Dp): Modifier = TODO()
fun Modifier.padding(paddingValues: PaddingValues): Modifier = TODO()
fun Modifier.absolutePadding(left: Dp = 0.dp, top: Dp = 0.dp, right: Dp = 0.dp, bottom: Dp = 0.dp): Modifier = TODO()
fun Modifier.offset(x: Dp = 0.dp, y: Dp = 0.dp): Modifier = TODO()
fun Modifier.offset(offset: Density.() -> IntOffset): Modifier = TODO()
fun Modifier.absoluteOffset(x: Dp = 0.dp, y: Dp = 0.dp): Modifier = TODO()
fun Modifier.absoluteOffset(offset: Density.() -> IntOffset): Modifier = TODO()

@Immutable interface BoxScope { @Stable fun Modifier.align(alignment: Alignment): Modifier; @Stable fun Modifier.matchParentSize(): Modifier }
@Stable interface BoxWithConstraintsScope : BoxScope { val constraints: Constraints; val minWidth: Dp; val maxWidth: Dp; val minHeight: Dp; val maxHeight: Dp }
@Composable inline fun Box(modifier: Modifier = Modifier, contentAlignment: Alignment = Alignment.TopStart, propagateMinConstraints: Boolean = false, content: @Composable BoxScope.() -> Unit): Unit = TODO()
@Composable fun Box(modifier: Modifier): Unit = TODO()
@Composable fun BoxWithConstraints(modifier: Modifier = Modifier, contentAlignment: Alignment = Alignment.TopStart, propagateMinConstraints: Boolean = false, content: @Composable BoxWithConstraintsScope.() -> Unit): Unit = TODO()
@Immutable interface ColumnScope {
    @Stable fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier
    @Stable fun Modifier.align(alignment: Alignment.Horizontal): Modifier
}
@Immutable interface RowScope {
    @Stable fun Modifier.weight(weight: Float, fill: Boolean = true): Modifier
    @Stable fun Modifier.align(alignment: Alignment.Vertical): Modifier
    @Stable fun Modifier.alignByBaseline(): Modifier
}
@Composable inline fun Column(modifier: Modifier = Modifier, verticalArrangement: Arrangement.Vertical = Arrangement.Top, horizontalAlignment: Alignment.Horizontal = Alignment.Start, content: @Composable ColumnScope.() -> Unit): Unit = TODO()
@Composable inline fun Row(modifier: Modifier = Modifier, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, verticalAlignment: Alignment.Vertical = Alignment.Top, content: @Composable RowScope.() -> Unit): Unit = TODO()
@Composable fun Spacer(modifier: Modifier): Unit = TODO()
@ExperimentalLayoutApi interface FlowRowScope : RowScope
@ExperimentalLayoutApi interface FlowColumnScope : ColumnScope
@ExperimentalLayoutApi @Composable inline fun FlowRow(modifier: Modifier = Modifier, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, verticalArrangement: Arrangement.Vertical = Arrangement.Top, maxItemsInEachRow: Int = Int.MAX_VALUE, content: @Composable FlowRowScope.() -> Unit): Unit = TODO()
@ExperimentalLayoutApi @Composable inline fun FlowColumn(modifier: Modifier = Modifier, verticalArrangement: Arrangement.Vertical = Arrangement.Top, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, maxItemsInEachColumn: Int = Int.MAX_VALUE, content: @Composable FlowColumnScope.() -> Unit): Unit = TODO()
//// FILE: androidx/compose/foundation/lazy/Lazy.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.lazy

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

interface LazyItemScope {
    fun Modifier.fillParentMaxSize(fraction: Float = 1f): Modifier
    fun Modifier.fillParentMaxWidth(fraction: Float = 1f): Modifier
    fun Modifier.fillParentMaxHeight(fraction: Float = 1f): Modifier
    fun Modifier.animateItem(fadeInSpec: FiniteAnimationSpec<Float>? = spring(), placementSpec: FiniteAnimationSpec<IntOffset>? = spring(), fadeOutSpec: FiniteAnimationSpec<Float>? = spring()): Modifier = this
}
interface LazyListScope {
    fun item(key: Any? = null, contentType: Any? = null, content: @Composable LazyItemScope.() -> Unit)
    fun items(count: Int, key: ((index: Int) -> Any)? = null, contentType: (index: Int) -> Any? = { null }, itemContent: @Composable LazyItemScope.(index: Int) -> Unit)
    fun stickyHeader(key: Any? = null, contentType: Any? = null, content: @Composable LazyItemScope.() -> Unit) {}
}
inline fun <T> LazyListScope.items(items: List<T>, noinline key: ((item: T) -> Any)? = null, noinline contentType: (item: T) -> Any? = { null }, crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit): Unit = TODO()
inline fun <T> LazyListScope.itemsIndexed(items: List<T>, noinline key: ((index: Int, item: T) -> Any)? = null, crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null }, crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit): Unit = TODO()
inline fun <T> LazyListScope.items(items: Array<T>, noinline key: ((item: T) -> Any)? = null, noinline contentType: (item: T) -> Any? = { null }, crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit): Unit = TODO()
inline fun <T> LazyListScope.itemsIndexed(items: Array<T>, noinline key: ((index: Int, item: T) -> Any)? = null, crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null }, crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit): Unit = TODO()
interface LazyListItemInfo { val index: Int; val key: Any; val offset: Int; val size: Int; val contentType: Any? get() = null }
interface LazyListLayoutInfo {
    val visibleItemsInfo: List<LazyListItemInfo>
    val viewportStartOffset: Int
    val viewportEndOffset: Int
    val totalItemsCount: Int
    val viewportSize: IntSize get() = IntSize.Zero
    val orientation: Orientation get() = Orientation.Vertical
    val reverseLayout: Boolean get() = false
    val beforeContentPadding: Int get() = 0
    val afterContentPadding: Int get() = 0
    val mainAxisItemSpacing: Int get() = 0
}
class LazyListState(firstVisibleItemIndex: Int = 0, firstVisibleItemScrollOffset: Int = 0) : ScrollableState {
    val firstVisibleItemIndex: Int get() = TODO()
    val firstVisibleItemScrollOffset: Int get() = TODO()
    val layoutInfo: LazyListLayoutInfo get() = TODO()
    val interactionSource: InteractionSource get() = TODO()
    suspend fun scrollToItem(index: Int, scrollOffset: Int = 0) {}
    suspend fun animateScrollToItem(index: Int, scrollOffset: Int = 0) {}
    override val isScrollInProgress: Boolean get() = TODO()
    override fun dispatchRawDelta(delta: Float): Float = TODO()
    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {}
    override val canScrollForward: Boolean get() = TODO()
    override val canScrollBackward: Boolean get() = TODO()
}
@Composable fun rememberLazyListState(initialFirstVisibleItemIndex: Int = 0, initialFirstVisibleItemScrollOffset: Int = 0): LazyListState = TODO()
@Composable fun LazyColumn(modifier: Modifier = Modifier, state: LazyListState = rememberLazyListState(), contentPadding: PaddingValues = PaddingValues(0.dp), reverseLayout: Boolean = false, verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom, horizontalAlignment: Alignment.Horizontal = Alignment.Start, flingBehavior: FlingBehavior = TODO(), userScrollEnabled: Boolean = true, content: LazyListScope.() -> Unit): Unit = TODO()
@Composable fun LazyRow(modifier: Modifier = Modifier, state: LazyListState = rememberLazyListState(), contentPadding: PaddingValues = PaddingValues(0.dp), reverseLayout: Boolean = false, horizontalArrangement: Arrangement.Horizontal = if (!reverseLayout) Arrangement.Start else Arrangement.End, verticalAlignment: Alignment.Vertical = Alignment.Top, flingBehavior: FlingBehavior = TODO(), userScrollEnabled: Boolean = true, content: LazyListScope.() -> Unit): Unit = TODO()
//// FILE: androidx/compose/foundation/pager/Pager.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.pager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface PagerScope
interface PageSize { object Fill : PageSize; class Fixed(val pageSize: Dp) : PageSize }
interface PagerLayoutInfo { val pageSize: Int; val pageSpacing: Int; val visiblePagesInfo: List<PageInfo> }
interface PageInfo { val index: Int; val offset: Int }
abstract class PagerState(currentPage: Int = 0, currentPageOffsetFraction: Float = 0f) : ScrollableState {
    abstract val pageCount: Int
    val currentPage: Int get() = TODO()
    val currentPageOffsetFraction: Float get() = TODO()
    val settledPage: Int get() = TODO()
    val targetPage: Int get() = TODO()
    val interactionSource: InteractionSource get() = TODO()
    val layoutInfo: PagerLayoutInfo get() = TODO()
    fun getOffsetDistanceInPages(page: Int): Float = TODO()
    suspend fun scrollToPage(page: Int, pageOffsetFraction: Float = 0f) {}
    suspend fun animateScrollToPage(page: Int, pageOffsetFraction: Float = 0f, animationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow)) {}
    override val isScrollInProgress: Boolean get() = TODO()
    override fun dispatchRawDelta(delta: Float): Float = TODO()
    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {}
    override val canScrollForward: Boolean get() = TODO()
    override val canScrollBackward: Boolean get() = TODO()
}
fun PagerState(currentPage: Int = 0, currentPageOffsetFraction: Float = 0f, pageCount: () -> Int): PagerState = TODO()
@Composable fun rememberPagerState(initialPage: Int = 0, initialPageOffsetFraction: Float = 0f, pageCount: () -> Int): PagerState = TODO()
object PagerDefaults {
    const val BeyondViewportPageCount: Int = 0
    @Composable fun flingBehavior(state: PagerState, snapPositionalThreshold: Float = 0.5f): TargetedFlingBehavior = TODO()
    fun pageNestedScrollConnection(state: PagerState, orientation: Orientation): NestedScrollConnection = TODO()
}
@Composable fun HorizontalPager(
    state: PagerState, modifier: Modifier = Modifier, contentPadding: PaddingValues = PaddingValues(0.dp), pageSize: PageSize = PageSize.Fill,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount, pageSpacing: Dp = 0.dp, verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = state), userScrollEnabled: Boolean = true, reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null, pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(state, Orientation.Horizontal),
    snapPosition: SnapPosition = SnapPosition.Start, pageContent: @Composable PagerScope.(page: Int) -> Unit,
): Unit = TODO()
@Composable fun VerticalPager(
    state: PagerState, modifier: Modifier = Modifier, contentPadding: PaddingValues = PaddingValues(0.dp), pageSize: PageSize = PageSize.Fill,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount, pageSpacing: Dp = 0.dp, horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = state), userScrollEnabled: Boolean = true, reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null, pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(state, Orientation.Vertical),
    snapPosition: SnapPosition = SnapPosition.Start, pageContent: @Composable PagerScope.(page: Int) -> Unit,
): Unit = TODO()
//// FILE: androidx/compose/foundation/shape/Shape.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.shape

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Immutable interface CornerSize { fun toPx(shapeSize: Size, density: Density): Float }
fun CornerSize(size: Dp): CornerSize = TODO()
fun CornerSize(size: Float): CornerSize = TODO()
fun CornerSize(percent: Int): CornerSize = TODO()
val ZeroCornerSize: CornerSize get() = TODO()
abstract class CornerBasedShape(val topStart: CornerSize, val topEnd: CornerSize, val bottomEnd: CornerSize, val bottomStart: CornerSize) : Shape {
    abstract fun copy(topStart: CornerSize = this.topStart, topEnd: CornerSize = this.topEnd, bottomEnd: CornerSize = this.bottomEnd, bottomStart: CornerSize = this.bottomStart): CornerBasedShape
    fun copy(all: CornerSize): CornerBasedShape = TODO()
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline = TODO()
}
class RoundedCornerShape(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize) : CornerBasedShape(topStart, topEnd, bottomEnd, bottomStart) {
    override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize): RoundedCornerShape = TODO()
}
val CircleShape: RoundedCornerShape get() = TODO()
fun RoundedCornerShape(corner: CornerSize): RoundedCornerShape = TODO()
fun RoundedCornerShape(size: Dp): RoundedCornerShape = TODO()
fun RoundedCornerShape(size: Float): RoundedCornerShape = TODO()
fun RoundedCornerShape(percent: Int): RoundedCornerShape = TODO()
fun RoundedCornerShape(topStart: Dp = 0.dp, topEnd: Dp = 0.dp, bottomEnd: Dp = 0.dp, bottomStart: Dp = 0.dp): RoundedCornerShape = TODO()
fun RoundedCornerShape(topStart: Float = 0.0f, topEnd: Float = 0.0f, bottomEnd: Float = 0.0f, bottomStart: Float = 0.0f): RoundedCornerShape = TODO()
fun RoundedCornerShape(topStartPercent: Int = 0, topEndPercent: Int = 0, bottomEndPercent: Int = 0, bottomStartPercent: Int = 0): RoundedCornerShape = TODO()
class AbsoluteRoundedCornerShape(topLeft: CornerSize, topRight: CornerSize, bottomRight: CornerSize, bottomLeft: CornerSize) : CornerBasedShape(topLeft, topRight, bottomRight, bottomLeft) {
    override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize): AbsoluteRoundedCornerShape = TODO()
}
fun AbsoluteRoundedCornerShape(size: Dp): AbsoluteRoundedCornerShape = TODO()
class CutCornerShape(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize) : CornerBasedShape(topStart, topEnd, bottomEnd, bottomStart) {
    override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize): CutCornerShape = TODO()
}
fun CutCornerShape(size: Dp): CutCornerShape = TODO()
class GenericShape(private val builder: Path.(size: Size, layoutDirection: LayoutDirection) -> Unit) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline = TODO()
}
//// FILE: androidx/compose/foundation/text/Text.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.foundation.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow

@Immutable class KeyboardOptions(
    val capitalization: KeyboardCapitalization = KeyboardCapitalization.Unspecified, val autoCorrectEnabled: Boolean? = null,
    val keyboardType: KeyboardType = KeyboardType.Unspecified, val imeAction: ImeAction = ImeAction.Unspecified,
    val platformImeOptions: PlatformImeOptions? = null, val showKeyboardOnFocus: Boolean? = null,
) {
    @Deprecated("Please use the new constructor that takes optional autoCorrectEnabled parameter.", level = DeprecationLevel.WARNING)
    constructor(capitalization: KeyboardCapitalization = KeyboardCapitalization.None, autoCorrect: Boolean, keyboardType: KeyboardType = KeyboardType.Text, imeAction: ImeAction = ImeAction.Default) : this(capitalization, autoCorrect, keyboardType, imeAction, null, null)
    @Deprecated("autoCorrect deprecated") val autoCorrect: Boolean get() = TODO()
    fun copy(capitalization: KeyboardCapitalization = this.capitalization, autoCorrectEnabled: Boolean? = this.autoCorrectEnabled, keyboardType: KeyboardType = this.keyboardType, imeAction: ImeAction = this.imeAction, platformImeOptions: PlatformImeOptions? = this.platformImeOptions, showKeyboardOnFocus: Boolean? = this.showKeyboardOnFocus): KeyboardOptions = TODO()
    companion object { val Default: KeyboardOptions get() = TODO() }
}
interface KeyboardActionScope { fun defaultKeyboardAction(imeAction: ImeAction) }
class KeyboardActions(
    val onDone: (KeyboardActionScope.() -> Unit)? = null, val onGo: (KeyboardActionScope.() -> Unit)? = null, val onNext: (KeyboardActionScope.() -> Unit)? = null,
    val onPrevious: (KeyboardActionScope.() -> Unit)? = null, val onSearch: (KeyboardActionScope.() -> Unit)? = null, val onSend: (KeyboardActionScope.() -> Unit)? = null,
) { companion object { val Default: KeyboardActions get() = TODO() } }
fun KeyboardActions(onAny: KeyboardActionScope.() -> Unit): KeyboardActions = TODO()
@Composable fun BasicTextField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false, maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE, minLines: Int = 1, visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {}, interactionSource: MutableInteractionSource? = null, cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() },
): Unit = TODO()
@Composable fun BasicTextField(
    value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false, maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE, minLines: Int = 1, visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {}, interactionSource: MutableInteractionSource? = null, cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() },
): Unit = TODO()
@Composable fun BasicText(text: String, modifier: Modifier = Modifier, style: TextStyle = TextStyle.Default, onTextLayout: ((TextLayoutResult) -> Unit)? = null, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true, maxLines: Int = Int.MAX_VALUE, minLines: Int = 1): Unit = TODO()
@Composable fun BasicText(text: AnnotatedString, modifier: Modifier = Modifier, style: TextStyle = TextStyle.Default, onTextLayout: ((TextLayoutResult) -> Unit)? = null, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true, maxLines: Int = Int.MAX_VALUE, minLines: Int = 1): Unit = TODO()
class TextSelectionColors(val handleColor: Color, val backgroundColor: Color)
val LocalTextSelectionColors: androidx.compose.runtime.ProvidableCompositionLocal<TextSelectionColors> = androidx.compose.runtime.compositionLocalOf { TODO() }
//// FILE: androidx/compose/material3/Material3.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalMaterial3Api
val LocalContentColor: ProvidableCompositionLocal<Color> = compositionLocalOf { Color.Black }
val LocalTextStyle: ProvidableCompositionLocal<TextStyle> = compositionLocalOf { TextStyle.Default }
@Composable fun Text(
    text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified, fontSize: TextUnit = TextUnit.Unspecified, fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null, fontFamily: FontFamily? = null, letterSpacing: TextUnit = TextUnit.Unspecified, textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null, lineHeight: TextUnit = TextUnit.Unspecified, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE, minLines: Int = 1, onTextLayout: ((TextLayoutResult) -> Unit)? = null, style: TextStyle = LocalTextStyle.current,
): Unit = TODO()
@Composable fun Text(
    text: AnnotatedString, modifier: Modifier = Modifier, color: Color = Color.Unspecified, fontSize: TextUnit = TextUnit.Unspecified, fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null, fontFamily: FontFamily? = null, letterSpacing: TextUnit = TextUnit.Unspecified, textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null, lineHeight: TextUnit = TextUnit.Unspecified, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE, minLines: Int = 1, inlineContent: Map<String, Any> = mapOf(), onTextLayout: (TextLayoutResult) -> Unit = {}, style: TextStyle = LocalTextStyle.current,
): Unit = TODO()
@Composable fun Icon(imageVector: ImageVector, contentDescription: String?, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current): Unit = TODO()
@Composable fun Icon(painter: Painter, contentDescription: String?, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current): Unit = TODO()
@Composable fun Icon(bitmap: ImageBitmap, contentDescription: String?, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current): Unit = TODO()
@Immutable class Typography(
    val displayLarge: TextStyle = TextStyle.Default, val displayMedium: TextStyle = TextStyle.Default, val displaySmall: TextStyle = TextStyle.Default,
    val headlineLarge: TextStyle = TextStyle.Default, val headlineMedium: TextStyle = TextStyle.Default, val headlineSmall: TextStyle = TextStyle.Default,
    val titleLarge: TextStyle = TextStyle.Default, val titleMedium: TextStyle = TextStyle.Default, val titleSmall: TextStyle = TextStyle.Default,
    val bodyLarge: TextStyle = TextStyle.Default, val bodyMedium: TextStyle = TextStyle.Default, val bodySmall: TextStyle = TextStyle.Default,
    val labelLarge: TextStyle = TextStyle.Default, val labelMedium: TextStyle = TextStyle.Default, val labelSmall: TextStyle = TextStyle.Default,
) {
    fun copy(
        displayLarge: TextStyle = this.displayLarge, displayMedium: TextStyle = this.displayMedium, displaySmall: TextStyle = this.displaySmall,
        headlineLarge: TextStyle = this.headlineLarge, headlineMedium: TextStyle = this.headlineMedium, headlineSmall: TextStyle = this.headlineSmall,
        titleLarge: TextStyle = this.titleLarge, titleMedium: TextStyle = this.titleMedium, titleSmall: TextStyle = this.titleSmall,
        bodyLarge: TextStyle = this.bodyLarge, bodyMedium: TextStyle = this.bodyMedium, bodySmall: TextStyle = this.bodySmall,
        labelLarge: TextStyle = this.labelLarge, labelMedium: TextStyle = this.labelMedium, labelSmall: TextStyle = this.labelSmall,
    ): Typography = TODO()
}
@Immutable class Shapes
class ColorScheme internal constructor() {
    val primary: Color get() = TODO(); val onPrimary: Color get() = TODO(); val primaryContainer: Color get() = TODO(); val onPrimaryContainer: Color get() = TODO()
    val secondary: Color get() = TODO(); val onSecondary: Color get() = TODO(); val tertiary: Color get() = TODO(); val onTertiary: Color get() = TODO()
    val background: Color get() = TODO(); val onBackground: Color get() = TODO(); val surface: Color get() = TODO(); val onSurface: Color get() = TODO()
    val surfaceVariant: Color get() = TODO(); val onSurfaceVariant: Color get() = TODO(); val error: Color get() = TODO(); val onError: Color get() = TODO(); val outline: Color get() = TODO()
}
fun darkColorScheme(
    primary: Color = Color.Unspecified, onPrimary: Color = Color.Unspecified, primaryContainer: Color = Color.Unspecified, onPrimaryContainer: Color = Color.Unspecified,
    inversePrimary: Color = Color.Unspecified, secondary: Color = Color.Unspecified, onSecondary: Color = Color.Unspecified, secondaryContainer: Color = Color.Unspecified,
    onSecondaryContainer: Color = Color.Unspecified, tertiary: Color = Color.Unspecified, onTertiary: Color = Color.Unspecified, tertiaryContainer: Color = Color.Unspecified,
    onTertiaryContainer: Color = Color.Unspecified, background: Color = Color.Unspecified, onBackground: Color = Color.Unspecified, surface: Color = Color.Unspecified,
    onSurface: Color = Color.Unspecified, surfaceVariant: Color = Color.Unspecified, onSurfaceVariant: Color = Color.Unspecified, surfaceTint: Color = primary,
    inverseSurface: Color = Color.Unspecified, inverseOnSurface: Color = Color.Unspecified, error: Color = Color.Unspecified, onError: Color = Color.Unspecified,
    errorContainer: Color = Color.Unspecified, onErrorContainer: Color = Color.Unspecified, outline: Color = Color.Unspecified, outlineVariant: Color = Color.Unspecified,
    scrim: Color = Color.Unspecified, surfaceBright: Color = Color.Unspecified, surfaceContainer: Color = Color.Unspecified, surfaceContainerHigh: Color = Color.Unspecified,
    surfaceContainerHighest: Color = Color.Unspecified, surfaceContainerLow: Color = Color.Unspecified, surfaceContainerLowest: Color = Color.Unspecified, surfaceDim: Color = Color.Unspecified,
): ColorScheme = TODO()
fun lightColorScheme(
    primary: Color = Color.Unspecified, onPrimary: Color = Color.Unspecified, primaryContainer: Color = Color.Unspecified, onPrimaryContainer: Color = Color.Unspecified,
    inversePrimary: Color = Color.Unspecified, secondary: Color = Color.Unspecified, onSecondary: Color = Color.Unspecified, secondaryContainer: Color = Color.Unspecified,
    onSecondaryContainer: Color = Color.Unspecified, tertiary: Color = Color.Unspecified, onTertiary: Color = Color.Unspecified, tertiaryContainer: Color = Color.Unspecified,
    onTertiaryContainer: Color = Color.Unspecified, background: Color = Color.Unspecified, onBackground: Color = Color.Unspecified, surface: Color = Color.Unspecified,
    onSurface: Color = Color.Unspecified, surfaceVariant: Color = Color.Unspecified, onSurfaceVariant: Color = Color.Unspecified, surfaceTint: Color = primary,
    inverseSurface: Color = Color.Unspecified, inverseOnSurface: Color = Color.Unspecified, error: Color = Color.Unspecified, onError: Color = Color.Unspecified,
    errorContainer: Color = Color.Unspecified, onErrorContainer: Color = Color.Unspecified, outline: Color = Color.Unspecified, outlineVariant: Color = Color.Unspecified,
    scrim: Color = Color.Unspecified, surfaceBright: Color = Color.Unspecified, surfaceContainer: Color = Color.Unspecified, surfaceContainerHigh: Color = Color.Unspecified,
    surfaceContainerHighest: Color = Color.Unspecified, surfaceContainerLow: Color = Color.Unspecified, surfaceContainerLowest: Color = Color.Unspecified, surfaceDim: Color = Color.Unspecified,
): ColorScheme = TODO()
object MaterialTheme {
    val colorScheme: ColorScheme @Composable @ReadOnlyComposable get() = TODO()
    val typography: Typography @Composable @ReadOnlyComposable get() = TODO()
    val shapes: Shapes @Composable @ReadOnlyComposable get() = TODO()
}
@Composable fun MaterialTheme(colorScheme: ColorScheme = MaterialTheme.colorScheme, shapes: Shapes = MaterialTheme.shapes, typography: Typography = MaterialTheme.typography, content: @Composable () -> Unit): Unit = TODO()
//// FILE: androidx/compose/material/icons/Icons.kt
@file:Suppress("unused")
package androidx.compose.material.icons
object Icons { object Filled; object Outlined; object Rounded; object TwoTone; object Sharp; val Default = Filled }
//// FILE: androidx/compose/material/icons/rounded/Rounded.kt
@file:Suppress("unused")
package androidx.compose.material.icons.rounded
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
val Icons.Rounded.Info: ImageVector get() = TODO()
val Icons.Rounded.Lock: ImageVector get() = TODO()
val Icons.Rounded.Close: ImageVector get() = TODO()
val Icons.Rounded.Check: ImageVector get() = TODO()
