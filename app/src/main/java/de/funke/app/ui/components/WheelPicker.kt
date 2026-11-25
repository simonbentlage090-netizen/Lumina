package de.funke.app.ui.components

import de.funke.app.ui.theme.Shapes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import de.funke.app.ui.theme.Funke
import de.funke.app.ui.theme.WheelStyle
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlin.math.abs
import kotlin.math.min

/**
 * Ein Zahlenrad zum Wischen. Rastet auf jeder Zahl ein, tickt dabei leicht in der Hand,
 * und die Zahlen werden zum Rand hin kleiner und blasser.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<Int>,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    unit: String = "min",
    visibleCount: Int = 5,
    itemHeight: Dp = 52.dp,
) {
    val p = Funke.colors
    val haptic = LocalHapticFeedback.current
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
    val initialIndex = items.indexOf(selected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val currentOnSelected by rememberUpdatedState(onSelected)
    val currentItems by rememberUpdatedState(items)

    val centerIndex by remember {
        derivedStateOf {
            val extra = if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2f) 1 else 0
            (listState.firstVisibleItemIndex + extra).coerceIn(0, (currentItems.size - 1).coerceAtLeast(0))
        }
    }

    // Leichtes Ticken bei jeder Zahl, die durch die Mitte rutscht.
    LaunchedEffect(listState) {
        androidx.compose.runtime.snapshotFlow { centerIndex }
            .drop(1)
            .collect { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    }

    // Wert melden, sobald das Rad zur Ruhe gekommen ist.
    LaunchedEffect(listState) {
        androidx.compose.runtime.snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collect {
                val value = currentItems.getOrNull(centerIndex)
                if (value != null) currentOnSelected(value)
            }
    }

    // Wird der Wert von außen geändert, das Rad mitnehmen.
    LaunchedEffect(selected) {
        val target = items.indexOf(selected)
        if (target >= 0 && target != centerIndex && !listState.isScrollInProgress) {
            listState.animateScrollToItem(target)
        }
    }

    val pad = itemHeight * (visibleCount / 2)

    // Das Rad behält alle Wisch-Bewegungen für sich. Am Anfang und Ende gibt es sonst
    // "Rest-Bewegung", die die Seite dahinter mitscrollen würde.
    val keepScroll = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset = available
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = available
        }
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(itemHeight * visibleCount)
            .nestedScroll(keepScroll),
        contentAlignment = Alignment.Center,
    ) {
        // Auswahl-Balken in der Mitte
        Box(
            Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(p.accent.copy(alpha = 0.04f), p.accent.copy(alpha = 0.16f), p.accent.copy(alpha = 0.04f))
                    ),
                    Shapes.lg,
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.width(140.dp),
            contentPadding = PaddingValues(vertical = pad),
            flingBehavior = rememberSnapFlingBehavior(listState),
        ) {
            itemsIndexed(items) { index, value ->
                val isCenter = centerIndex == index
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .graphicsLayer {
                            val info = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                            val dist = if (info == null) 3f else abs(info.offset.toFloat()) / itemHeightPx
                            val d = min(dist, 2.5f)
                            alpha = 1f - 0.72f * (d / 2.5f)
                            val s = 1f - 0.16f * min(d, 2f)
                            scaleX = s
                            scaleY = s
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = value.toString(),
                        style = WheelStyle,
                        color = if (isCenter) p.text else p.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Text(
            text = unit,
            modifier = Modifier.offset(x = 66.dp),
            style = MaterialTheme.typography.titleMedium,
            color = p.textTertiary,
        )
    }
}
