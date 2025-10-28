package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import ru.agrachev.emojicalendar.domain.core.Constants.MONTH_COUNT
import ru.agrachev.emojicalendar.domain.core.plus
import ru.agrachev.emojicalendar.domain.core.toInt

private const val MARGIN = 2

@Composable
fun YearMonthLazyRow(
    stickyHeaderContent: @Composable (yearOffsetFromNow: Int, modifier: Modifier) -> Unit,
    itemContent: @Composable (monthIndex: Int, modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
    lazyRowState: LazyListState = rememberLazyListState(),
) {
    val tileModifier = remember {
        Modifier
            .wrapContentSize()
    }
    var range by remember {
        mutableStateOf(0..1)
    }

    LaunchedEffect(lazyRowState) {
        snapshotFlow {
            lazyRowState.firstVisibleItemIndex
        }
            .collect {
                with(lazyRowState.layoutInfo) {
                    val addHead = visibleItemsInfo.getOrNull(1)?.let {
                        it.index <= MARGIN
                    } ?: false
                    val addTail = visibleItemsInfo.lastOrNull()?.let {
                        it.index >= totalItemsCount - MARGIN
                    } ?: false
                    if (addHead || addTail) {
                        range += IntRange(
                            start = -addHead.toInt(),
                            endInclusive = addTail.toInt(),
                        )
                    }
                }
            }
    }

    LazyRow(
        modifier = modifier,
        state = lazyRowState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = lazyRowState,
            snapPosition = SnapPosition.Start,
        ),
        overscrollEffect = null,
    ) {
        for (times in range) {
            val yearTileIndex = times * (MONTH_COUNT + 1)
            stickyHeader(
                key = yearTileIndex,
                contentType = YearMonthItemContentType.YEAR,
            ) {
                stickyHeaderContent(times, tileModifier)
            }
            items(
                count = MONTH_COUNT,
                key = { yearTileIndex + (it + 1) },
                contentType = { YearMonthItemContentType.MONTH },
            ) {
                itemContent(it, tileModifier)
            }
        }
    }
}

enum class YearMonthItemContentType {
    YEAR,
    MONTH,
}
