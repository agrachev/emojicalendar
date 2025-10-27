package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import ru.agrachev.emojicalendar.domain.core.Constants.MAX_WEEKS_IN_MONTH
import ru.agrachev.emojicalendar.domain.core.Constants.WEEK_DAY_COUNT
import ru.agrachev.emojicalendar.presentation.toIntPx
import kotlin.math.ceil
import kotlin.math.max

@Composable
fun MonthItemLayout(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val itemWidth = (width / WEEK_DAY_COUNT).toIntPx()
    val itemHeight = (height / MAX_WEEKS_IN_MONTH).toIntPx()
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val measurableConstraints = Constraints(
            minWidth = itemWidth,
            maxWidth = itemWidth,
            minHeight = itemHeight,
            maxHeight = itemHeight,
        )
        val placeables = measurables.map { measurable ->
            measurable.measure(measurableConstraints)
        }
        val layoutWidth = constraints.maxWidth
        val layoutHeight =
            ceil(max(measurables.size, 1) / WEEK_DAY_COUNT.toFloat()).toInt() * itemHeight
        layout(layoutWidth, layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = (index % WEEK_DAY_COUNT) * itemWidth,
                    y = (index / WEEK_DAY_COUNT) * itemHeight,
                )
            }
        }
    }
}
