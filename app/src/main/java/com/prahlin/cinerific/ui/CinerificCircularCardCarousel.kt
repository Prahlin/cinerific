package com.prahlin.cinerific.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlin.math.abs

private const val CIRCULAR_CARD_ROW_ITEM_COUNT = Int.MAX_VALUE
private const val CIRCULAR_CARD_ROW_SNAP_MS = 150
private const val CIRCULAR_CARD_ROW_VELOCITY_STRIDE_FRACTION = 0.62f
private const val SELECTION_STROKE_PULSE_MS = 420

@Composable
internal fun rememberCinerificSelectionStrokeAlpha(): Float {
    val transition = rememberInfiniteTransition(label = "selection-stroke-pulse")
    return transition.animateFloat(
        initialValue = 0.67f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SELECTION_STROKE_PULSE_MS,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selection-stroke-alpha"
    ).value
}

@Composable
internal fun CinerificCircularCardRow(
    itemCount: Int,
    selected: Boolean,
    itemSpacing: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    itemContent: @Composable (itemIndex: Int, selected: Boolean) -> Unit
) {
    if (itemCount <= 0) return

    val initialFirstVisibleItemIndex = remember(itemCount) {
        circularCardRowInitialIndex(itemCount)
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex
    )
    val flingBehavior = remember(listState, itemCount) {
        CircularCardRowSnapFlingBehavior(
            listState = listState,
            itemCount = itemCount
        )
    }
    val selectedVirtualItemIndex = if (selected) {
        listState.centerVisibleItemIndex()
            ?: (listState.firstVisibleItemIndex + 1).coerceAtMost(CIRCULAR_CARD_ROW_ITEM_COUNT - 1)
    } else {
        -1
    }

    LazyRow(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        flingBehavior = flingBehavior
    ) {
        items(count = CIRCULAR_CARD_ROW_ITEM_COUNT) { virtualIndex ->
            itemContent(
                virtualIndex.floorMod(itemCount),
                selected && virtualIndex == selectedVirtualItemIndex
            )
        }
    }
}

private class CircularCardRowSnapFlingBehavior(
    private val listState: LazyListState,
    private val itemCount: Int
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val centerItem = listState.centerVisibleItem() ?: return 0f
        val itemStridePx = listState.visibleItemStridePx() ?: centerItem.size.toFloat()
        val velocityThresholdPx = itemStridePx * CIRCULAR_CARD_ROW_VELOCITY_STRIDE_FRACTION
        val targetIndex = when {
            initialVelocity > velocityThresholdPx -> centerItem.index + 1
            initialVelocity < -velocityThresholdPx -> centerItem.index - 1
            else -> centerItem.index
        }.coerceIn(0, CIRCULAR_CARD_ROW_ITEM_COUNT - 1)
        val scrollDelta = listState.scrollDeltaToCenter(
            targetIndex = targetIndex,
            centerItem = centerItem,
            itemStridePx = itemStridePx
        )
        if (abs(scrollDelta) < 0.5f) return 0f

        var previousValue = 0f
        animate(
            initialValue = 0f,
            targetValue = scrollDelta,
            animationSpec = tween(
                durationMillis = CIRCULAR_CARD_ROW_SNAP_MS,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            val consumed = scrollBy(value - previousValue)
            previousValue += consumed
        }

        recenterIfNeeded(targetIndex)
        return 0f
    }

    private suspend fun recenterIfNeeded(targetIndex: Int) {
        val guardBand = itemCount * 100
        if (targetIndex > guardBand && targetIndex < CIRCULAR_CARD_ROW_ITEM_COUNT - guardBand) {
            return
        }

        val centeredIndex = circularCardRowInitialIndex(itemCount) + targetIndex.floorMod(itemCount)
        listState.scrollToItem(
            index = centeredIndex,
            scrollOffset = listState.firstVisibleItemScrollOffset
        )
    }
}

private fun LazyListState.centerVisibleItemIndex(): Int? {
    return centerVisibleItem()?.index
}

private fun LazyListState.centerVisibleItem(): LazyListItemInfo? {
    val layout = layoutInfo
    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f
    return layout.visibleItemsInfo.minByOrNull { item ->
        abs(item.centerPx - viewportCenter)
    }
}

private fun LazyListState.visibleItemStridePx(): Float? {
    val visibleItems = layoutInfo.visibleItemsInfo
        .sortedBy { it.index }

    return visibleItems
        .zipWithNext()
        .firstOrNull { (left, right) -> right.index == left.index + 1 }
        ?.let { (left, right) -> (right.offset - left.offset).toFloat() }
}

private fun LazyListState.scrollDeltaToCenter(
    targetIndex: Int,
    centerItem: LazyListItemInfo,
    itemStridePx: Float
): Float {
    val layout = layoutInfo
    val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f
    val targetCenter = layout.visibleItemsInfo
        .firstOrNull { it.index == targetIndex }
        ?.centerPx
        ?: (centerItem.centerPx + (targetIndex - centerItem.index) * itemStridePx)

    return targetCenter - viewportCenter
}

private val LazyListItemInfo.centerPx: Float
    get() = offset + size / 2f

private fun circularCardRowInitialIndex(itemCount: Int): Int {
    val midpoint = CIRCULAR_CARD_ROW_ITEM_COUNT / 2
    return midpoint - midpoint.floorMod(itemCount)
}

private fun Int.floorMod(divisor: Int): Int {
    return ((this % divisor) + divisor) % divisor
}
