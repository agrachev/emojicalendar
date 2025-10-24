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
import ru.agrachev.emojicalendar.presentation.core.plus
import java.time.LocalDate
import java.time.Month
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

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
            stickyHeader(key = 0 + times * 13, contentType = "yearTile") {
                stickyHeaderContent(times, tileModifier)
            }
            items(
                Month.entries.size,
                key = { (it + 1) + times * 13 },
                contentType = { "monthTile" }
            ) {
                itemContent(it, tileModifier)
            }
        }
    }
}

@Composable
fun rememberYearOffsetFromNowRange(monthOffsetFromNow: Int): IntRange {
    val currentMonth = remember {
        LocalDate.now().monthValue
    }
    var range by remember {
        mutableStateOf(0..0)
    }
    return remember(monthOffsetFromNow) {
        val yearOffsetFromNow =
            floor((currentMonth + monthOffsetFromNow) / Month.entries.size.toFloat()).toInt()
        IntRange(
            start = min(range.start, yearOffsetFromNow),
            endInclusive = max(range.endInclusive, yearOffsetFromNow)
        ).apply {
            range = this
        }
    }
}

fun Boolean.toInt() = this.compareTo(false)
fun Boolean.toFloat() = this.toInt().toFloat()
