package ru.agrachev.emojicalendar.presentation.widget.modal

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.agrachev.emojicalendar.R
import ru.agrachev.emojicalendar.domain.core.toFloat
import ru.agrachev.emojicalendar.domain.model.CalendarEvent
import ru.agrachev.emojicalendar.presentation.core.DragHandleAnchors
import ru.agrachev.emojicalendar.presentation.core.bounceHigh
import ru.agrachev.emojicalendar.presentation.scope.browser.EventBrowserScope
import ru.agrachev.emojicalendar.presentation.scope.browser.EventBrowserStatefulScope
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import ru.agrachev.emojicalendar.presentation.theme.Typography
import ru.agrachev.emojicalendar.presentation.toIntPx
import ru.agrachev.emojicalendar.presentation.widget.EMOJI_IMAGE_TOP_LEFT_Y_OFFSET_SCALE
import ru.agrachev.emojicalendar.presentation.widget.EmojiImage
import kotlin.math.abs

@Composable
fun EventListBrowserModalScreen(
    calendarEvents: List<CalendarEvent>,
    onEventClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier then Modifier.padding(top = 16.dp),
    ) {
        with(rememberEventListBrowserScope(calendarEvents)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
            ) {
                if (shouldDisplayExtraActions) {
                    ExtraActionsLayout(
                        modifier = Modifier
                            .wrapContentSize(),
                    )
                }
                CalendarEventDescriptionLayout(
                    calendarEvents = calendarEvents,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                )
            }
            EmojiList(
                calendarEvents = calendarEvents,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
            LaunchedEffect(emojiRowListState) {
                snapshotFlow {
                    emojiRowListState.layoutInfo
                }
                    .collect {
                        emojiIconVisibilityFraction =
                            it.getSelectedItemInvisibilityFraction(selectedIndex)
                    }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventListBrowserModalScreenPreview() {
    EmojiCalendarTheme {
        EventListBrowserModalScreen(
            calendarEvents = emptyList(),
            modifier = Modifier
                .fillMaxSize(),
            onEventClicked = {

            }
        )
    }
}

@Composable
private fun EventBrowserScope.ExtraActionsLayout(
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier then Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                if (constraints.maxHeight != Constraints.Infinity) {
                    undateFullExpandedAnchorOffset(placeable.width.toFloat())
                }
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(x = -placeable.width / 2, y = 0)
                }
            }
            .offset {
                IntOffset(
                    x = (totalDragOffset - partiallyExpandedAnchorOffset).toInt(),
                    0
                )
            }
    ) {
        ExtraActionButton(
            iconRes = R.drawable.delete_24px,
        )
        val paddingOffset = 12.dp.toIntPx()
        ExtraActionButton(
            iconRes = R.drawable.edit_calendar_24px,
            onGloballyPositionedCallback = { coordinates ->
                partiallyExpandedAnchorOffset =
                    coordinates.positionInParent().x - paddingOffset
            },
        )
    }
}

@Composable
private fun EventBrowserScope.ExtraActionButton(
    @DrawableRes iconRes: Int,
    onGloballyPositionedCallback: (LayoutCoordinates) -> Unit = {

    }
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .drawBehind {
                if (totalDragOffset > partiallyExpandedAnchorOffset) {
                    drawRoundRect(
                        color = containerColor.copy(
                            alpha = (totalDragOffset - partiallyExpandedAnchorOffset) /
                                    (fullExpandedAnchorOffset - partiallyExpandedAnchorOffset),
                        ),
                        cornerRadius = CornerRadius(
                            size.width / 4,
                            size.height / 4
                        )
                    )
                }
            }
            .onGloballyPositioned(
                onGloballyPositioned = onGloballyPositionedCallback,
            )
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(.7f)
                .graphicsLayer {
                    alpha =
                        (totalDragOffset / partiallyExpandedAnchorOffset).coerceIn(
                            0f,
                            1f
                        )
                }
        )
    }
}

@Composable
private fun EventBrowserScope.CalendarEventDescriptionLayout(
    calendarEvents: List<CalendarEvent>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier then Modifier
            .anchoredDraggable(
                state = anchoredState,
                reverseDirection = false,
                orientation = Orientation.Horizontal,
            )
            .offset {
                IntOffset(x = totalDragOffset.toInt(), y = 0)
            }
            .pointerInput(Unit) {
                with(currentCoroutineContext()) {
                    awaitPointerEventScope {
                        while (isActive) {
                            try {
                                val pointerEvent = awaitPointerEvent()
                                when (pointerEvent.type) {
                                    PointerEventType.Press -> {
                                        if (anchoredState.isCollapsed && pointerEvent.isNotConsumed) {
                                            isTitleLayoutPressed = true
                                        }
                                    }

                                    PointerEventType.Release ->
                                        isTitleLayoutPressed = false
                                }
                            } catch (_: Exception) {
                                isTitleLayoutPressed = false
                            }
                        }
                    }
                }
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 16.dp),
        ) {
            with(calendarEvents[selectedIndex]) {
                Text(
                    text = rule?.title.orEmpty(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = Typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                if (shouldDisplayExtraRow) {
                    Text(
                        text = this.title.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = Typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
        }
        val scope = rememberCoroutineScope()
        AnimatedVisibility(
            visible = isEmojiIconVisible,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut(),
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = true,
                ) {
                    scope.launch {
                        emojiRowListState.animateScrollToItem(selectedIndex)
                    }
                },
        ) {
            EmojiImage(
                emoji = calendarEvents[selectedIndex].emoji,
                scaleFactor = .85f,
                topLeftYOffsetScale = 0f,
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}

@Composable
private fun EventBrowserScope.EmojiList(
    calendarEvents: List<CalendarEvent>,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        val scope = rememberCoroutineScope()
        LazyRow(
            state = emojiRowListState,
            horizontalArrangement = Arrangement.spacedBy(space = (-25).dp),
            modifier = modifier,
        ) {
            items(
                count = calendarEvents.size,
                key = { calendarEvents[it].id.value },
            ) { index ->
                val brightnessAnimator = remember {
                    Animatable(0f)
                }
                val isSelected by remember(index) {
                    derivedStateOf {
                        selectedIndex == index
                    }
                }
                val yOffsetScale = EMOJI_IMAGE_TOP_LEFT_Y_OFFSET_SCALE
                val bounceOffset by animateIntAsState(
                    targetValue = if (isSelected) (-yOffsetScale * 100.dp.toIntPx()).toInt() else 0,
                    animationSpec = bounceHigh(),
                )
                EmojiImage(
                    emoji = calendarEvents[index].emoji,
                    topLeftYOffsetScale = yOffsetScale,
                    scaleFactor = .9f,
                    modifier = Modifier
                        .size(100.dp)
                        .zIndex { isSelected.toFloat() }
                        .graphicsLayer {
                            val brightness = lerp(
                                start = 0f,
                                stop = lerp(0f, 255f, .5f),
                                fraction = 2 * if (brightnessAnimator.value <= .5f) {
                                    brightnessAnimator.value
                                } else {
                                    1f - brightnessAnimator.value
                                }
                            )
                            val colorMatrix = floatArrayOf(
                                1f, 0f, 0f, 0f, brightness,
                                0f, 1f, 0f, 0f, brightness,
                                0f, 0f, 1f, 0f, brightness,
                                0f, 0f, 0f, 1f, 0f,
                            )
                            colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
                        }
                        .offset {
                            IntOffset(x = 0, y = bounceOffset)
                        }
                        .clickable(enabled = true) {
                            selectItem(index)
                            scope.launch {
                                brightnessAnimator.snapTo(0f)
                                brightnessAnimator.animateTo(
                                    targetValue = 1f,
                                )
                            }
                        },
                )
            }
        }
    }
}

@Composable
internal fun rememberEventListBrowserScope(calendarEvents: List<CalendarEvent>): EventBrowserScope {
    val emojiRowListState = rememberLazyListState()
    val selectedIndexState = rememberSaveable {
        mutableIntStateOf(0)
    }
    return remember {
        object : EventBrowserStatefulScope() {
            override val emojiRowListState = emojiRowListState
            override var selectedIndex by selectedIndexState
        }
    }.apply {
        pressOffsetState = animateFloatAsState(
            targetValue = if (isTitleLayoutPressed) partiallyExpandedAnchorOffset else 0f,
        )
        shouldDisplayExtraRowState = remember(calendarEvents) {
            mutableStateOf(calendarEvents.any { !it.title.isNullOrEmpty() })
        }
    }
}

@Composable
private fun Modifier.zIndex(zIndex: () -> Float) =
    this then Modifier
        .layout(remember {
            { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(IntOffset.Zero, zIndex())
                }
            }
        })

internal fun LazyListLayoutInfo.getSelectedItemInvisibilityFraction(selectedIndex: Int) =
    this.visibleItemsInfo.indexOfFirst { item -> item.index == selectedIndex }
        .let { index ->
            when {
                index < 0 -> 1f
                index > 0 && index < this.visibleItemsInfo.lastIndex -> 0f
                else -> {
                    val item = this.visibleItemsInfo[index]
                    if (index == 0) {
                        abs(item.offset) / item.size.toFloat()
                    } else {
                        1f - (this.viewportSize.width - item.offset) / item.size.toFloat()
                    }
                }
            }
        }

private inline val AnchoredDraggableState<DragHandleAnchors>.isCollapsed
    get() = this.currentValue == DragHandleAnchors.COLLAPSED

private inline val PointerEvent.isNotConsumed
    get() = this.changes.none { it.isConsumed }
