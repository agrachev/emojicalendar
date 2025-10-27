package ru.agrachev.emojicalendar.presentation.widget

import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.math.round
import kotlin.math.sign

@Composable
fun OffsetRangeSlider(
    sliderState: OffsetRangeSliderState,
    startThumb: @Composable () -> Unit,
    endThumb: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        content = {
            OffsetRangeSliderThumbBehavior(
                offsetRangeSliderState = sliderState,
                thumbLocation = OffsetRangeSliderState.ThumbLocation.Start,
                content = startThumb,
            )
            OffsetRangeSliderThumbBehavior(
                offsetRangeSliderState = sliderState,
                thumbLocation = OffsetRangeSliderState.ThumbLocation.End,
                content = endThumb,
            )
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val adjustedConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            measurable.measure(adjustedConstraints)
        }

        val layoutWidth = constraints.minWidth
        val layoutHeight = placeables.maxOfOrNull { it.height } ?: constraints.maxHeight

        sliderState.sliderWidth = layoutWidth
        layout(layoutWidth, layoutHeight) {
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = -placeable.height / 2)
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
internal fun OffsetRangeSliderThumbBehavior(
    offsetRangeSliderState: OffsetRangeSliderState,
    thumbLocation: OffsetRangeSliderState.ThumbLocation,
    content: @Composable () -> Unit,
) {
    val thumbAnimator = remember {
        Animatable(0f)
    }

    LaunchedEffect(thumbAnimator) {
        snapshotFlow {
            offsetRangeSliderState.sliderWidth
        }
            .flatMapLatest {
                thumbAnimator.snapTo(
                    targetValue = offsetRangeSliderState.offsetPx(thumbLocation),
                )
                snapshotFlow {
                    offsetRangeSliderState.offsetPx(thumbLocation)
                }
            }.collect {
                thumbAnimator.animateTo(
                    targetValue = it,
                    animationSpec = tween(
                        durationMillis = 100,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .offset {
                IntOffset(x = thumbAnimator.value.toInt(), y = 0)
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        offsetRangeSliderState.onDragStarted(
                            offsetX = it.x,
                            thumbLocation = thumbLocation,
                        )
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetRangeSliderState.onDrag(
                        dragAmount = dragAmount,
                        thumbLocation = thumbLocation,
                    )
                }
            }
    ) {
        content()
    }
}

@Composable
fun rememberOffsetRangeSliderState(
    startOffset: Int,
    endOffset: Int,
) = remember {
    OffsetRangeSliderState(
        offsetRange = startOffset..endOffset,
    )
}

@Stable
class OffsetRangeSliderState(
    offsetRange: ClosedRange<Int> = 0..1,
    @param:IntRange(from = 1) val segments: Int = 7,
) {

    init {
        require(offsetRange.endInclusive > offsetRange.start)
    }

    private val startOffsetStateHolder by lazy {
        OffsetStateHolder(
            initialOffsetValue = offsetRange.start,
        ) { value ->
            value.coerceAtMost(
                maximumValue = endOffsetValue - 1
            )
        }
    }
    private val endOffsetStateHolder by lazy {
        OffsetStateHolder(
            initialOffsetValue = offsetRange.endInclusive,
        ) { value ->
            value.coerceAtLeast(
                minimumValue = startOffsetValue + 1
            )
        }
    }
    private val segmentWidth: Float
        get() = sliderWidth / segments.toFloat()

    internal val startOffsetPx by derivedStateOf {
        sliderWidth * (startOffsetValue / segments) + segmentWidth * (startOffsetValue % segments)
    }

    internal val endOffsetPx by derivedStateOf {
        endOffsetValue * segmentWidth
    }
    //private set

    internal var sliderWidth by mutableIntStateOf(0)

    var startOffsetValue: Int by startOffsetStateHolder::offsetValue
    var endOffsetValue: Int by endOffsetStateHolder::offsetValue

    internal fun onDragStarted(offsetX: Float, thumbLocation: ThumbLocation) {
        thumbLocation.stateHolder
            .handleHorizontalDragStarted(offsetX)
    }

    internal fun onDrag(dragAmount: Float, thumbLocation: ThumbLocation) {
        thumbLocation.stateHolder
            .handleHorizontalDragChanged(dragAmount, segmentWidth)
    }

    private class OffsetStateHolder(
        initialOffsetValue: Int,
        private val offsetValueStateValidator: (Int) -> Int,
    ) {
        var offsetValueState by mutableIntStateOf(initialOffsetValue)
        var lastOffsetValue = 0
        var dragOffsetX = 0f

        var offsetValue
            set(value) {
                offsetValueState = offsetValueStateValidator(value)
            }
            get() = offsetValueState

        fun handleHorizontalDragStarted(offsetX: Float) {
            lastOffsetValue = offsetValue
            dragOffsetX = offsetX
        }

        fun handleHorizontalDragChanged(dragAmount: Float, segmentWidth: Float) {
            dragOffsetX += dragAmount
            offsetValue = lastOffsetValue + round(
                dragOffsetX / segmentWidth - 0.5f * sign(dragAmount)
            )
                .toInt()
        }
    }

    enum class ThumbLocation {
        Start,
        End,
    }

    internal fun offsetPx(thumbLocation: ThumbLocation) = when (thumbLocation) {
        ThumbLocation.Start -> startOffsetPx
        ThumbLocation.End -> endOffsetPx
    }

    private val ThumbLocation.stateHolder
        get() = when (this) {
            ThumbLocation.Start -> startOffsetStateHolder
            ThumbLocation.End -> endOffsetStateHolder
        }

}
