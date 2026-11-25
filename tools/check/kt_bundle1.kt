//// FILE: androidx/lifecycle/Lifecycle.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.lifecycle

abstract class Lifecycle {
    abstract fun addObserver(observer: LifecycleObserver)
    abstract fun removeObserver(observer: LifecycleObserver)
    abstract val currentState: State
    enum class Event { ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY, ON_ANY; val targetState: State get() = TODO() }
    enum class State { DESTROYED, INITIALIZED, CREATED, STARTED, RESUMED; fun isAtLeast(state: State): Boolean = compareTo(state) >= 0 }
}
interface LifecycleObserver
fun interface LifecycleEventObserver : LifecycleObserver { fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) }
interface DefaultLifecycleObserver : LifecycleObserver {
    fun onCreate(owner: LifecycleOwner) {}
    fun onStart(owner: LifecycleOwner) {}
    fun onResume(owner: LifecycleOwner) {}
    fun onPause(owner: LifecycleOwner) {}
    fun onStop(owner: LifecycleOwner) {}
    fun onDestroy(owner: LifecycleOwner) {}
}
interface LifecycleOwner { val lifecycle: Lifecycle }
//// FILE: androidx/activity/Activity.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.activity

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

open class ComponentActivity : android.app.Activity(), LifecycleOwner {
    override val lifecycle: Lifecycle get() = TODO()
    val onBackPressedDispatcher: OnBackPressedDispatcher get() = TODO()
    fun <I, O> registerForActivityResult(contract: androidx.activity.result.contract.ActivityResultContract<I, O>, callback: androidx.activity.result.ActivityResultCallback<O>): androidx.activity.result.ActivityResultLauncher<I> = TODO()
}
class OnBackPressedDispatcher { fun onBackPressed() {}; fun addCallback(owner: LifecycleOwner, onBackPressedCallback: OnBackPressedCallback) {} }
abstract class OnBackPressedCallback(enabled: Boolean) { abstract fun handleOnBackPressed(); var isEnabled: Boolean = enabled; fun remove() {} }
class SystemBarStyle private constructor() {
    companion object {
        fun auto(lightScrim: Int, darkScrim: Int, detectDarkMode: (android.content.res.Resources) -> Boolean = { TODO() }): SystemBarStyle = TODO()
        fun dark(scrim: Int): SystemBarStyle = TODO()
        fun light(scrim: Int, darkScrim: Int): SystemBarStyle = TODO()
    }
}
fun ComponentActivity.enableEdgeToEdge(statusBarStyle: SystemBarStyle = SystemBarStyle.auto(0, 0), navigationBarStyle: SystemBarStyle = SystemBarStyle.auto(0, 0)): Unit = TODO()
//// FILE: androidx/activity/result/Result.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.activity.result

fun interface ActivityResultCallback<O> { fun onActivityResult(result: O) }
abstract class ActivityResultLauncher<I> {
    fun launch(input: I) {}
    abstract fun launch(input: I, options: Any?)
    abstract fun unregister()
}
class ActivityResult(val resultCode: Int, val data: android.content.Intent?)
//// FILE: androidx/activity/result/contract/Contracts.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.activity.result.contract

import android.content.Context
import android.content.Intent
import android.net.Uri

abstract class ActivityResultContract<I, O> {
    abstract fun createIntent(context: Context, input: I): Intent
    abstract fun parseResult(resultCode: Int, intent: Intent?): O
}
class ActivityResultContracts private constructor() {
    open class CreateDocument(private val mimeType: String) : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = TODO()
    }
    open class OpenDocument : ActivityResultContract<Array<String>, Uri?>() {
        override fun createIntent(context: Context, input: Array<String>): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = TODO()
    }
    open class OpenMultipleDocuments : ActivityResultContract<Array<String>, List<Uri>>() {
        override fun createIntent(context: Context, input: Array<String>): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> = TODO()
    }
    open class GetContent : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = TODO()
    }
    class RequestPermission : ActivityResultContract<String, Boolean>() {
        override fun createIntent(context: Context, input: String): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): Boolean = TODO()
    }
    class RequestMultiplePermissions : ActivityResultContract<Array<String>, Map<String, Boolean>>() {
        override fun createIntent(context: Context, input: Array<String>): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): Map<String, Boolean> = TODO()
    }
    class StartActivityForResult : ActivityResultContract<Intent, androidx.activity.result.ActivityResult>() {
        override fun createIntent(context: Context, input: Intent): Intent = TODO()
        override fun parseResult(resultCode: Int, intent: Intent?): androidx.activity.result.ActivityResult = TODO()
    }
}
//// FILE: androidx/activity/compose/ActivityCompose.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.activity.compose

import androidx.compose.runtime.Composable
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract

@Composable fun BackHandler(enabled: Boolean = true, onBack: () -> Unit): Unit = TODO()
class ManagedActivityResultLauncher<I, O> : ActivityResultLauncher<I>() {
    override fun launch(input: I, options: Any?) {}
    override fun unregister() {}
}
@Composable fun <I, O> rememberLauncherForActivityResult(contract: ActivityResultContract<I, O>, onResult: (O) -> Unit): ManagedActivityResultLauncher<I, O> = TODO()
fun androidx.activity.ComponentActivity.setContent(parent: androidx.compose.runtime.CompositionContext? = null, content: @Composable () -> Unit): Unit = TODO()
//// FILE: androidx/core/graphics/drawable/DrawableKt.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.core.graphics.drawable

fun android.graphics.drawable.Drawable.toBitmap(width: Int = intrinsicWidth, height: Int = intrinsicHeight, config: android.graphics.Bitmap.Config? = null): android.graphics.Bitmap = TODO()
//// FILE: androidx/compose/runtime/Runtime.kt
@file:Suppress("UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")
package androidx.compose.runtime

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.PROPERTY_GETTER)
annotation class Composable
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER) annotation class ReadOnlyComposable
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER) annotation class NonRestartableComposable
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER) annotation class NonSkippableComposable
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE) annotation class DisallowComposableCalls
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FUNCTION) annotation class ExplicitGroupsComposable
@MustBeDocumented @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY) @Retention(AnnotationRetention.BINARY) @StableMarker annotation class Stable
@MustBeDocumented @Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.BINARY) @StableMarker annotation class Immutable
@MustBeDocumented @Target(AnnotationTarget.ANNOTATION_CLASS) @Retention(AnnotationRetention.BINARY) annotation class StableMarker
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER) annotation class ComposableTarget(val applier: String)
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER) annotation class ComposableOpenTarget(val index: Int)
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER) annotation class ComposableInferredTarget(val scheme: String)
@Retention(AnnotationRetention.BINARY) @Target(AnnotationTarget.ANNOTATION_CLASS) annotation class ComposableTargetMarker(val description: String = "")
@RequiresOptIn(level = RequiresOptIn.Level.ERROR) annotation class InternalComposeApi
@RequiresOptIn(level = RequiresOptIn.Level.WARNING) annotation class ExperimentalComposeApi
@RequiresOptIn(level = RequiresOptIn.Level.ERROR) annotation class ExperimentalComposeRuntimeApi

@Stable interface State<out T> { val value: T }
@Stable interface MutableState<T> : State<T> {
    override var value: T
    operator fun component1(): T
    operator fun component2(): (T) -> Unit
}
@Stable interface IntState : State<Int> { override val value: Int get() = intValue; val intValue: Int }
@Stable interface MutableIntState : IntState, MutableState<Int> { override var value: Int get() = intValue; set(value) { intValue = value }; override var intValue: Int }
@Stable interface FloatState : State<Float> { override val value: Float get() = floatValue; val floatValue: Float }
@Stable interface MutableFloatState : FloatState, MutableState<Float> { override var value: Float get() = floatValue; set(value) { floatValue = value }; override var floatValue: Float }
@Stable interface LongState : State<Long> { override val value: Long get() = longValue; val longValue: Long }
@Stable interface MutableLongState : LongState, MutableState<Long> { override var value: Long get() = longValue; set(value) { longValue = value }; override var longValue: Long }
@Stable interface DoubleState : State<Double> { override val value: Double get() = doubleValue; val doubleValue: Double }
@Stable interface MutableDoubleState : DoubleState, MutableState<Double> { override var value: Double get() = doubleValue; set(value) { doubleValue = value }; override var doubleValue: Double }

inline operator fun <T> State<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value
inline operator fun <T> MutableState<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) { this.value = value }
inline operator fun IntState.getValue(thisObj: Any?, property: KProperty<*>): Int = intValue
inline operator fun MutableIntState.setValue(thisObj: Any?, property: KProperty<*>, value: Int) { intValue = value }
inline operator fun FloatState.getValue(thisObj: Any?, property: KProperty<*>): Float = floatValue
inline operator fun MutableFloatState.setValue(thisObj: Any?, property: KProperty<*>, value: Float) { floatValue = value }
inline operator fun LongState.getValue(thisObj: Any?, property: KProperty<*>): Long = longValue
inline operator fun MutableLongState.setValue(thisObj: Any?, property: KProperty<*>, value: Long) { longValue = value }
inline operator fun DoubleState.getValue(thisObj: Any?, property: KProperty<*>): Double = doubleValue
inline operator fun MutableDoubleState.setValue(thisObj: Any?, property: KProperty<*>, value: Double) { doubleValue = value }

interface SnapshotMutationPolicy<T> { fun equivalent(a: T, b: T): Boolean; fun merge(previous: T, current: T, applied: T): T? = null }
fun <T> structuralEqualityPolicy(): SnapshotMutationPolicy<T> = TODO()
fun <T> referentialEqualityPolicy(): SnapshotMutationPolicy<T> = TODO()
fun <T> neverEqualPolicy(): SnapshotMutationPolicy<T> = TODO()

fun <T> mutableStateOf(value: T, policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy()): MutableState<T> = TODO()
fun mutableIntStateOf(value: Int): MutableIntState = TODO()
fun mutableFloatStateOf(value: Float): MutableFloatState = TODO()
fun mutableLongStateOf(value: Long): MutableLongState = TODO()
fun mutableDoubleStateOf(value: Double): MutableDoubleState = TODO()

class SnapshotStateList<T> private constructor(private val impl: MutableList<T>) : MutableList<T> by impl, RandomAccess {
    constructor() : this(ArrayList())
    fun toList(): List<T> = impl.toList()
    fun removeRange(fromIndex: Int, toIndex: Int) {}
}
class SnapshotStateMap<K, V> private constructor(private val impl: MutableMap<K, V>) : MutableMap<K, V> by impl {
    constructor() : this(HashMap())
    fun toMap(): Map<K, V> = impl.toMap()
}
fun <T> mutableStateListOf(): SnapshotStateList<T> = TODO()
fun <T> mutableStateListOf(vararg elements: T): SnapshotStateList<T> = TODO()
fun <T> Collection<T>.toMutableStateList(): SnapshotStateList<T> = TODO()
fun <K, V> mutableStateMapOf(): SnapshotStateMap<K, V> = TODO()
fun <K, V> mutableStateMapOf(vararg pairs: Pair<K, V>): SnapshotStateMap<K, V> = TODO()
fun <K, V> Iterable<Pair<K, V>>.toMutableStateMap(): SnapshotStateMap<K, V> = TODO()

fun <T> derivedStateOf(calculation: () -> T): State<T> = TODO()
fun <T> derivedStateOf(policy: SnapshotMutationPolicy<T>, calculation: () -> T): State<T> = TODO()
fun <T> snapshotFlow(block: () -> T): Flow<T> = TODO()
suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R = TODO()
suspend fun <R> withFrameMillis(onFrame: (frameTimeMillis: Long) -> R): R = TODO()

@Composable inline fun <T> remember(crossinline calculation: @DisallowComposableCalls () -> T): T = TODO()
@Composable inline fun <T> remember(key1: Any?, crossinline calculation: @DisallowComposableCalls () -> T): T = TODO()
@Composable inline fun <T> remember(key1: Any?, key2: Any?, crossinline calculation: @DisallowComposableCalls () -> T): T = TODO()
@Composable inline fun <T> remember(key1: Any?, key2: Any?, key3: Any?, crossinline calculation: @DisallowComposableCalls () -> T): T = TODO()
@Composable inline fun <T> remember(vararg keys: Any?, crossinline calculation: @DisallowComposableCalls () -> T): T = TODO()
@Composable fun <T> rememberUpdatedState(newValue: T): State<T> = TODO()
@Composable fun rememberCoroutineScope(getContext: @DisallowComposableCalls () -> CoroutineContext = { EmptyCoroutineContext }): CoroutineScope = TODO()
@Composable inline fun <T> key(vararg keys: Any?, block: @Composable () -> T): T = TODO()

@Composable fun LaunchedEffect(key1: Any?, block: suspend CoroutineScope.() -> Unit): Unit = TODO()
@Composable fun LaunchedEffect(key1: Any?, key2: Any?, block: suspend CoroutineScope.() -> Unit): Unit = TODO()
@Composable fun LaunchedEffect(key1: Any?, key2: Any?, key3: Any?, block: suspend CoroutineScope.() -> Unit): Unit = TODO()
@Composable fun LaunchedEffect(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit): Unit = TODO()

class DisposableEffectScope { inline fun onDispose(crossinline onDisposeEffect: () -> Unit): DisposableEffectResult = TODO() }
interface DisposableEffectResult { fun dispose() }
@Composable fun DisposableEffect(key1: Any?, effect: DisposableEffectScope.() -> DisposableEffectResult): Unit = TODO()
@Composable fun DisposableEffect(key1: Any?, key2: Any?, effect: DisposableEffectScope.() -> DisposableEffectResult): Unit = TODO()
@Composable fun DisposableEffect(key1: Any?, key2: Any?, key3: Any?, effect: DisposableEffectScope.() -> DisposableEffectResult): Unit = TODO()
@Composable fun DisposableEffect(vararg keys: Any?, effect: DisposableEffectScope.() -> DisposableEffectResult): Unit = TODO()
@Composable fun SideEffect(effect: () -> Unit): Unit = TODO()

interface ProduceStateScope<T> : MutableState<T>, CoroutineScope { suspend fun awaitDispose(onDispose: () -> Unit): Nothing }
@Composable fun <T> produceState(initialValue: T, producer: suspend ProduceStateScope<T>.() -> Unit): State<T> = TODO()
@Composable fun <T> produceState(initialValue: T, key1: Any?, producer: suspend ProduceStateScope<T>.() -> Unit): State<T> = TODO()
@Composable fun <T> produceState(initialValue: T, key1: Any?, key2: Any?, producer: suspend ProduceStateScope<T>.() -> Unit): State<T> = TODO()
@Composable fun <T> produceState(initialValue: T, key1: Any?, key2: Any?, key3: Any?, producer: suspend ProduceStateScope<T>.() -> Unit): State<T> = TODO()
@Composable fun <T> produceState(initialValue: T, vararg keys: Any?, producer: suspend ProduceStateScope<T>.() -> Unit): State<T> = TODO()

@Composable fun <T> Flow<T>.collectAsState(initial: T, context: CoroutineContext = EmptyCoroutineContext): State<T> = TODO()
@Composable fun <T> kotlinx.coroutines.flow.StateFlow<T>.collectAsState(context: CoroutineContext = EmptyCoroutineContext): State<T> = TODO()

@Stable abstract class CompositionLocal<T> internal constructor() {
    val current: T @ReadOnlyComposable @Composable get() = TODO()
}
@Stable abstract class ProvidableCompositionLocal<T> internal constructor() : CompositionLocal<T>() {
    infix fun provides(value: T): ProvidedValue<T> = TODO()
    infix fun providesDefault(value: T): ProvidedValue<T> = TODO()
}
class ProvidedValue<T> internal constructor()
fun <T> compositionLocalOf(policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(), defaultFactory: () -> T): ProvidableCompositionLocal<T> = TODO()
fun <T> staticCompositionLocalOf(defaultFactory: () -> T): ProvidableCompositionLocal<T> = TODO()
@Composable fun CompositionLocalProvider(vararg values: ProvidedValue<*>, content: @Composable () -> Unit): Unit = TODO()
@Composable fun CompositionLocalProvider(value: ProvidedValue<*>, content: @Composable () -> Unit): Unit = TODO()

abstract class CompositionContext internal constructor()
class Recomposer(effectCoroutineContext: CoroutineContext) : CompositionContext() { fun cancel() {} }
interface RecomposeScope { fun invalidate() }
val currentRecomposeScope: RecomposeScope @Composable get() = TODO()
//// FILE: androidx/compose/runtime/saveable/Saveable.kt
@file:Suppress("UNUSED_PARAMETER", "unused")
package androidx.compose.runtime.saveable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

interface SaverScope { fun canBeSaved(value: Any): Boolean }
interface Saver<Original, Saveable : Any> { fun SaverScope.save(value: Original): Saveable?; fun restore(value: Saveable): Original? }
fun <Original, Saveable : Any> Saver(save: SaverScope.(value: Original) -> Saveable?, restore: (value: Saveable) -> Original?): Saver<Original, Saveable> = TODO()
fun <T> autoSaver(): Saver<T, Any> = TODO()
@Composable fun <T : Any> rememberSaveable(vararg inputs: Any?, saver: Saver<T, out Any> = autoSaver(), key: String? = null, init: () -> T): T = TODO()
@Composable fun <T> rememberSaveable(vararg inputs: Any?, stateSaver: Saver<T, out Any>, key: String? = null, init: () -> MutableState<T>): MutableState<T> = TODO()
//// FILE: androidx/compose/ui/unit/Unit.kt
@file:Suppress("UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")
package androidx.compose.ui.unit

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

@Immutable @JvmInline value class Dp(val value: Float) : Comparable<Dp> {
    inline operator fun plus(other: Dp): Dp = Dp(value + other.value)
    inline operator fun minus(other: Dp): Dp = Dp(value - other.value)
    inline operator fun unaryMinus(): Dp = Dp(-value)
    inline operator fun div(other: Float): Dp = Dp(value / other)
    inline operator fun div(other: Int): Dp = Dp(value / other)
    inline operator fun div(other: Dp): Float = value / other.value
    inline operator fun times(other: Float): Dp = Dp(value * other)
    inline operator fun times(other: Int): Dp = Dp(value * other)
    override operator fun compareTo(other: Dp): Int = value.compareTo(other.value)
    companion object { val Hairline = Dp(0f); val Infinity = Dp(Float.POSITIVE_INFINITY); val Unspecified = Dp(Float.NaN) }
}
inline val Int.dp: Dp get() = Dp(this.toFloat())
inline val Double.dp: Dp get() = Dp(this.toFloat())
inline val Float.dp: Dp get() = Dp(this)
inline operator fun Float.times(other: Dp): Dp = Dp(this * other.value)
inline operator fun Double.times(other: Dp): Dp = Dp(this.toFloat() * other.value)
inline operator fun Int.times(other: Dp): Dp = Dp(this * other.value)
inline fun min(a: Dp, b: Dp): Dp = Dp(kotlin.math.min(a.value, b.value))
inline fun max(a: Dp, b: Dp): Dp = Dp(kotlin.math.max(a.value, b.value))
inline fun Dp.coerceIn(minimumValue: Dp, maximumValue: Dp): Dp = TODO()
inline fun Dp.coerceAtLeast(minimumValue: Dp): Dp = TODO()
inline fun Dp.coerceAtMost(maximumValue: Dp): Dp = TODO()
inline val Dp.isSpecified: Boolean get() = !value.isNaN()
inline val Dp.isUnspecified: Boolean get() = value.isNaN()
inline val Dp.isFinite: Boolean get() = value != Float.POSITIVE_INFINITY
inline fun Dp.takeOrElse(block: () -> Dp): Dp = TODO()
fun lerp(start: Dp, stop: Dp, fraction: Float): Dp = TODO()
val Dp.Companion.VisibilityThreshold: Dp get() = TODO()

@Immutable class DpOffset(val x: Dp, val y: Dp) {
    fun copy(x: Dp = this.x, y: Dp = this.y): DpOffset = TODO()
    operator fun minus(other: DpOffset): DpOffset = TODO()
    operator fun plus(other: DpOffset): DpOffset = TODO()
    companion object { val Zero: DpOffset = TODO(); val Unspecified: DpOffset = TODO() }
}
@Immutable class DpSize(val width: Dp, val height: Dp) {
    fun copy(width: Dp = this.width, height: Dp = this.height): DpSize = TODO()
    operator fun minus(other: DpSize): DpSize = TODO()
    operator fun plus(other: DpSize): DpSize = TODO()
    operator fun times(other: Int): DpSize = TODO()
    operator fun times(other: Float): DpSize = TODO()
    operator fun div(other: Int): DpSize = TODO()
    operator fun div(other: Float): DpSize = TODO()
    operator fun component1(): Dp = width
    operator fun component2(): Dp = height
    companion object { val Zero: DpSize = TODO(); val Unspecified: DpSize = TODO() }
}
val DpSize.center: DpOffset get() = TODO()
@Immutable class DpRect(val left: Dp, val top: Dp, val right: Dp, val bottom: Dp)

enum class TextUnitType { Unspecified, Sp, Em }
@Immutable class TextUnit(val value: Float, val type: TextUnitType) : Comparable<TextUnit> {
    val isSp: Boolean get() = type == TextUnitType.Sp
    val isEm: Boolean get() = type == TextUnitType.Em
    inline operator fun times(other: Float): TextUnit = TODO()
    inline operator fun times(other: Double): TextUnit = TODO()
    inline operator fun times(other: Int): TextUnit = TODO()
    inline operator fun div(other: Float): TextUnit = TODO()
    inline operator fun div(other: Double): TextUnit = TODO()
    inline operator fun div(other: Int): TextUnit = TODO()
    inline operator fun unaryMinus(): TextUnit = TODO()
    override operator fun compareTo(other: TextUnit): Int = TODO()
    companion object { val Unspecified: TextUnit = TextUnit(Float.NaN, TextUnitType.Unspecified) }
}

val TextUnit.isSpecified: Boolean get() = type != TextUnitType.Unspecified
val TextUnit.isUnspecified: Boolean get() = type == TextUnitType.Unspecified
inline fun TextUnit.takeOrElse(block: () -> TextUnit): TextUnit = TODO()
val Int.sp: TextUnit get() = TODO()
val Float.sp: TextUnit get() = TODO()
val Double.sp: TextUnit get() = TODO()
val Int.em: TextUnit get() = TODO()
val Float.em: TextUnit get() = TODO()
val Double.em: TextUnit get() = TODO()
inline operator fun Float.times(other: TextUnit): TextUnit = TODO()
inline operator fun Double.times(other: TextUnit): TextUnit = TODO()
inline operator fun Int.times(other: TextUnit): TextUnit = TODO()
fun lerp(start: TextUnit, stop: TextUnit, fraction: Float): TextUnit = TODO()

interface FontScaling {
    val fontScale: Float
    fun Dp.toSp(): TextUnit = TODO()
    fun TextUnit.toDp(): Dp = TODO()
}
@Immutable interface Density : FontScaling {
    val density: Float
    fun Dp.toPx(): Float = value * density
    fun Dp.roundToPx(): Int = TODO()
    fun TextUnit.toPx(): Float = TODO()
    fun TextUnit.roundToPx(): Int = TODO()
    fun Int.toDp(): Dp = TODO()
    fun Int.toSp(): TextUnit = TODO()
    fun Float.toDp(): Dp = TODO()
    fun Float.toSp(): TextUnit = TODO()
    fun DpRect.toRect(): Rect = TODO()
    fun DpSize.toSize(): Size = TODO()
    fun Size.toDpSize(): DpSize = TODO()
}
fun Density(density: Float, fontScale: Float = 1f): Density = TODO()
fun Density(context: android.content.Context): Density = TODO()

@Immutable class IntOffset(val x: Int, val y: Int) {
    operator fun component1(): Int = x
    operator fun component2(): Int = y
    fun copy(x: Int = this.x, y: Int = this.y): IntOffset = TODO()
    operator fun minus(other: IntOffset): IntOffset = TODO()
    operator fun plus(other: IntOffset): IntOffset = TODO()
    operator fun unaryMinus(): IntOffset = TODO()
    operator fun times(operand: Float): IntOffset = TODO()
    operator fun div(operand: Float): IntOffset = TODO()
    operator fun rem(operand: Int): IntOffset = TODO()
    companion object { val Zero = IntOffset(0, 0); val Max: IntOffset = TODO() }
}
fun IntOffset.toOffset(): Offset = TODO()
fun Offset.round(): IntOffset = TODO()
operator fun Offset.plus(offset: IntOffset): Offset = TODO()
operator fun Offset.minus(offset: IntOffset): Offset = TODO()
operator fun IntOffset.plus(offset: Offset): Offset = TODO()
operator fun IntOffset.minus(offset: Offset): Offset = TODO()
fun lerp(start: IntOffset, stop: IntOffset, fraction: Float): IntOffset = TODO()

@Immutable class IntSize(val width: Int, val height: Int) {
    operator fun component1(): Int = width
    operator fun component2(): Int = height
    operator fun times(other: Int): IntSize = TODO()
    operator fun div(other: Int): IntSize = TODO()
    companion object { val Zero = IntSize(0, 0) }
}
val IntSize.center: IntOffset get() = TODO()
fun IntSize.toSize(): Size = TODO()
fun Size.toIntSize(): IntSize = TODO()
fun Size.roundToIntSize(): IntSize = TODO()
operator fun Int.times(size: IntSize): IntSize = TODO()

@Immutable class IntRect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top
    val size: IntSize get() = TODO()
    val topLeft: IntOffset get() = TODO()
    val center: IntOffset get() = TODO()
    companion object { val Zero = IntRect(0, 0, 0, 0) }
}

@Immutable class Constraints(val minWidth: Int = 0, val maxWidth: Int = Infinity, val minHeight: Int = 0, val maxHeight: Int = Infinity) {
    val hasBoundedWidth: Boolean get() = maxWidth != Infinity
    val hasBoundedHeight: Boolean get() = maxHeight != Infinity
    val hasFixedWidth: Boolean get() = maxWidth == minWidth
    val hasFixedHeight: Boolean get() = maxHeight == minHeight
    val isZero: Boolean get() = maxWidth == 0 || maxHeight == 0
    fun copy(minWidth: Int = this.minWidth, maxWidth: Int = this.maxWidth, minHeight: Int = this.minHeight, maxHeight: Int = this.maxHeight): Constraints = TODO()
    companion object {
        const val Infinity = Int.MAX_VALUE
        fun fixed(width: Int, height: Int): Constraints = TODO()
        fun fixedWidth(width: Int): Constraints = TODO()
        fun fixedHeight(height: Int): Constraints = TODO()
    }
}
fun Constraints.constrain(otherConstraints: Constraints): Constraints = TODO()
fun Constraints.constrain(size: IntSize): IntSize = TODO()
fun Constraints.constrainWidth(width: Int): Int = TODO()
fun Constraints.constrainHeight(height: Int): Int = TODO()
fun Constraints.offset(horizontal: Int = 0, vertical: Int = 0): Constraints = TODO()

@Immutable class Velocity(val x: Float, val y: Float) {
    operator fun component1(): Float = x
    operator fun component2(): Float = y
    fun copy(x: Float = this.x, y: Float = this.y): Velocity = TODO()
    operator fun unaryMinus(): Velocity = TODO()
    operator fun minus(other: Velocity): Velocity = TODO()
    operator fun plus(other: Velocity): Velocity = TODO()
    operator fun times(operand: Float): Velocity = TODO()
    operator fun div(operand: Float): Velocity = TODO()
    companion object { val Zero = Velocity(0f, 0f) }
}
enum class LayoutDirection { Ltr, Rtl }
//// FILE: androidx/compose/ui/geometry/Geometry.kt
@file:Suppress("UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")
package androidx.compose.ui.geometry

import androidx.compose.runtime.Immutable

@Immutable class Offset(val x: Float, val y: Float) {
    operator fun component1(): Float = x
    operator fun component2(): Float = y
    fun copy(x: Float = this.x, y: Float = this.y): Offset = Offset(x, y)
    fun isValid(): Boolean = TODO()
    fun getDistance(): Float = TODO()
    fun getDistanceSquared(): Float = TODO()
    operator fun unaryMinus(): Offset = TODO()
    operator fun minus(other: Offset): Offset = TODO()
    operator fun plus(other: Offset): Offset = TODO()
    operator fun times(operand: Float): Offset = TODO()
    operator fun div(operand: Float): Offset = TODO()
    operator fun rem(operand: Float): Offset = TODO()
    companion object { val Zero = Offset(0f, 0f); val Infinite = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY); val Unspecified = Offset(Float.NaN, Float.NaN) }
}
val Offset.isSpecified: Boolean get() = TODO()
val Offset.isUnspecified: Boolean get() = TODO()
val Offset.isFinite: Boolean get() = TODO()
inline fun Offset.takeOrElse(block: () -> Offset): Offset = TODO()
fun lerp(start: Offset, stop: Offset, fraction: Float): Offset = TODO()

@Immutable class Size(val width: Float, val height: Float) {
    operator fun component1(): Float = width
    operator fun component2(): Float = height
    fun copy(width: Float = this.width, height: Float = this.height): Size = Size(width, height)
    fun isEmpty(): Boolean = TODO()
    operator fun times(operand: Float): Size = TODO()
    operator fun div(operand: Float): Size = TODO()
    val minDimension: Float get() = TODO()
    val maxDimension: Float get() = TODO()
    companion object { val Zero = Size(0f, 0f); val Unspecified = Size(Float.NaN, Float.NaN) }
}
val Size.center: Offset get() = TODO()
val Size.isSpecified: Boolean get() = TODO()
val Size.isUnspecified: Boolean get() = TODO()
inline fun Size.takeOrElse(block: () -> Size): Size = TODO()
fun Size.toRect(): Rect = TODO()
operator fun Int.times(size: Size): Size = TODO()
operator fun Float.times(size: Size): Size = TODO()
fun lerp(start: Size, stop: Size, fraction: Float): Size = TODO()

@Immutable class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    val size: Size get() = TODO()
    val isInfinite: Boolean get() = TODO()
    val isFinite: Boolean get() = TODO()
    val isEmpty: Boolean get() = TODO()
    val minDimension: Float get() = TODO()
    val maxDimension: Float get() = TODO()
    val topLeft: Offset get() = TODO()
    val topCenter: Offset get() = TODO()
    val topRight: Offset get() = TODO()
    val centerLeft: Offset get() = TODO()
    val center: Offset get() = TODO()
    val centerRight: Offset get() = TODO()
    val bottomLeft: Offset get() = TODO()
    val bottomCenter: Offset get() = TODO()
    val bottomRight: Offset get() = TODO()
    fun copy(left: Float = this.left, top: Float = this.top, right: Float = this.right, bottom: Float = this.bottom): Rect = TODO()
    fun translate(offset: Offset): Rect = TODO()
    fun translate(translateX: Float, translateY: Float): Rect = TODO()
    fun inflate(delta: Float): Rect = TODO()
    fun deflate(delta: Float): Rect = TODO()
    fun intersect(other: Rect): Rect = TODO()
    fun intersect(otherLeft: Float, otherTop: Float, otherRight: Float, otherBottom: Float): Rect = TODO()
    fun overlaps(other: Rect): Boolean = TODO()
    operator fun contains(offset: Offset): Boolean = TODO()
    companion object { val Zero = Rect(0f, 0f, 0f, 0f) }
}
fun Rect(offset: Offset, size: Size): Rect = TODO()
fun Rect(topLeft: Offset, bottomRight: Offset): Rect = TODO()
fun Rect(center: Offset, radius: Float): Rect = TODO()
fun lerp(start: Rect, stop: Rect, fraction: Float): Rect = TODO()
class MutableRect(var left: Float, var top: Float, var right: Float, var bottom: Float)

@Immutable class CornerRadius(val x: Float, val y: Float = x) {
    operator fun component1(): Float = x
    operator fun component2(): Float = y
    fun copy(x: Float = this.x, y: Float = this.y): CornerRadius = TODO()
    operator fun unaryMinus(): CornerRadius = TODO()
    operator fun minus(other: CornerRadius): CornerRadius = TODO()
    operator fun plus(other: CornerRadius): CornerRadius = TODO()
    operator fun times(operand: Float): CornerRadius = TODO()
    operator fun div(operand: Float): CornerRadius = TODO()
    companion object { val Zero = CornerRadius(0f) }
}
fun lerp(start: CornerRadius, stop: CornerRadius, fraction: Float): CornerRadius = TODO()
@Immutable class RoundRect(
    val left: Float, val top: Float, val right: Float, val bottom: Float,
    val topLeftCornerRadius: CornerRadius = CornerRadius.Zero, val topRightCornerRadius: CornerRadius = CornerRadius.Zero,
    val bottomRightCornerRadius: CornerRadius = CornerRadius.Zero, val bottomLeftCornerRadius: CornerRadius = CornerRadius.Zero,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    operator fun contains(point: Offset): Boolean = TODO()
    companion object { val Zero: RoundRect = TODO() }
}
fun RoundRect(left: Float, top: Float, right: Float, bottom: Float, radiusX: Float, radiusY: Float): RoundRect = TODO()
fun RoundRect(left: Float, top: Float, right: Float, bottom: Float, cornerRadius: CornerRadius): RoundRect = TODO()
fun RoundRect(rect: Rect, radiusX: Float, radiusY: Float): RoundRect = TODO()
fun RoundRect(rect: Rect, cornerRadius: CornerRadius): RoundRect = TODO()
fun RoundRect(rect: Rect, topLeft: CornerRadius = CornerRadius.Zero, topRight: CornerRadius = CornerRadius.Zero, bottomRight: CornerRadius = CornerRadius.Zero, bottomLeft: CornerRadius = CornerRadius.Zero): RoundRect = TODO()
val RoundRect.boundingRect: Rect get() = TODO()
