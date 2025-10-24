package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
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
    val itemWidth = (width / 7).toIntPx()
    val itemHeight = (height / 6).toIntPx()
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
        val layoutHeight = ceil(max(measurables.size, 1) / 7.0).toInt() * itemHeight
        layout(layoutWidth, layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = (index % 7) * itemWidth,
                    y = (index / 7) * itemHeight,
                )
            }
        }
    }
}
