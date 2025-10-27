package ru.agrachev.emojicalendar.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.mutableFloatStateOf
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
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import ru.agrachev.emojicalendar.presentation.theme.Typography
import ru.agrachev.emojicalendar.presentation.toIntPx
import ru.agrachev.emojicalendar.presentation.widget.DragHandleAnchors
import ru.agrachev.emojicalendar.presentation.widget.EmojiImage
import kotlin.math.abs
import kotlin.math.max

@Composable
fun EventListBrowserModalScreen(
    calendarEvents: List<CalendarEvent>,
    onEventClicked: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .then(Modifier.padding(top = 16.dp)),
    ) {
        val anchoredState = remember {
            AnchoredDraggableState(
                initialValue = DragHandleAnchors.COLLAPSED,
                anchors = DraggableAnchors {
                    DragHandleAnchors.COLLAPSED at 0f
                    DragHandleAnchors.EXPANDED at 1f
                }
            )
        }
        var fullExpandedAnchorOffset by remember {
            mutableFloatStateOf(0f)
        }
        var partiallyExpandedAnchorOffset by remember {
            mutableFloatStateOf(1f)
        }
        val undateFullExpandedAnchorOffset: (Float) -> Unit = {
            fullExpandedAnchorOffset = it
            anchoredState.updateAnchors(
                DraggableAnchors {
                    DragHandleAnchors.COLLAPSED at 0f
                    DragHandleAnchors.EXPANDED at it
                }
            )
        }
        val state = rememberLazyListState()
        var selectedIndex by rememberSaveable {
            mutableIntStateOf(0)
        }
        var iconVisibilityFraction by remember {
            mutableFloatStateOf(0f)
        }
        val updater: (Int) -> Unit = remember {
            {
                selectedIndex = it
                iconVisibilityFraction = state.layoutInfo.kk(it)
            }
        }
        val vis by remember {
            derivedStateOf {
                iconVisibilityFraction >= .5f
            }
        }
        var isPressed by remember {
            mutableStateOf(false)
        }
        val pressOffset by animateFloatAsState(
            targetValue = if (isPressed) partiallyExpandedAnchorOffset else 0f
        )
        val totalOffset by remember {
            derivedStateOf {
                max(anchoredState.requireOffset(), pressOffset)
            }
        }
        val v by remember {
            derivedStateOf {
                totalOffset > 0
            }
        }
        val shouldDisplayExtraRow by remember(calendarEvents) {
            mutableStateOf(calendarEvents.any { !it.title.isNullOrEmpty() })
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            if (v) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .wrapContentWidth()
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
                            IntOffset(x = (totalOffset - partiallyExpandedAnchorOffset).toInt(), 0)
                        }
                ) {
                    val c = MaterialTheme.colorScheme.primaryContainer
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .drawBehind {
                                if (totalOffset > partiallyExpandedAnchorOffset) {
                                    drawRoundRect(
                                        color = c.copy(alpha = (totalOffset - partiallyExpandedAnchorOffset) / (fullExpandedAnchorOffset - partiallyExpandedAnchorOffset)),
                                        cornerRadius = CornerRadius(size.width / 4, size.height / 4)
                                    )
                                }
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete_24px),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize(.7f)
                                .graphicsLayer {
                                    alpha = (totalOffset / partiallyExpandedAnchorOffset).coerceIn(
                                        0f,
                                        1f
                                    )
                                }
                        )
                    }
                    val d = 12.dp.toIntPx()
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .drawBehind {
                                if (totalOffset > partiallyExpandedAnchorOffset) {
                                    drawRoundRect(
                                        color = c.copy(alpha = (totalOffset - partiallyExpandedAnchorOffset) / (fullExpandedAnchorOffset - partiallyExpandedAnchorOffset)),
                                        cornerRadius = CornerRadius(size.width / 4, size.height / 4)
                                    )
                                }
                            }
                            .onGloballyPositioned { coordinates ->
                                partiallyExpandedAnchorOffset =
                                    coordinates.positionInParent().x - d
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_calendar_24px),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize(.7f)
                                .graphicsLayer {
                                    alpha = (totalOffset / partiallyExpandedAnchorOffset).coerceIn(
                                        0f,
                                        1f
                                    )
                                }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .anchoredDraggable(
                        state = anchoredState,
                        reverseDirection = false,
                        orientation = Orientation.Horizontal,
                    )
                    .offset {
                        IntOffset(x = totalOffset.toInt(), y = 0)
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
                                                    isPressed = true
                                                }
                                            }

                                            PointerEventType.Release -> isPressed = false
                                        }
                                    } catch (_: Exception) {
                                        isPressed = false
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
                        .padding(top = 8.dp, bottom = 16.dp)
                ) {
                    with(calendarEvents[selectedIndex]) {
                        Text(
                            text = rule?.title.orEmpty(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = Typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        if (shouldDisplayExtraRow) {
                            Text(
                                text = this.title.orEmpty(),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = Typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
                val scope = rememberCoroutineScope()
                AnimatedVisibility(
                    visible = vis,
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
                                state.animateScrollToItem(selectedIndex)
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
        LaunchedEffect(state) {
            snapshotFlow {
                state.layoutInfo
            }
                .collect {
                    iconVisibilityFraction = it.kk(selectedIndex)
                }
        }
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            LazyRow(
                state = state,
                horizontalArrangement = Arrangement.spacedBy(space = (-25).dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                items(
                    count = calendarEvents.size,
                    key = { calendarEvents[it].id.value }
                ) { index ->
                    val scope = rememberCoroutineScope()
                    val anim = remember {
                        Animatable(0f)
                    }
                    val isSelected by remember {
                        derivedStateOf {
                            selectedIndex == index
                        }
                    }
                    val offset by animateIntAsState(
                        targetValue = if (isSelected) (-.35f * 100.dp.toIntPx()).toInt() else 0,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    )
                    EmojiImage(
                        emoji = calendarEvents[index].emoji,
                        scaleFactor = .9f,
                        modifier = Modifier
                            .size(100.dp)
                            .zIndex { isSelected.toFloat() }
                            .graphicsLayer {
                                val brightness = lerp(
                                    start = 0f,
                                    stop = lerp(0f, 255f, .5f),
                                    fraction = if (anim.value <= .5f) anim.value * 2 else ((1f - anim.value) * 2)
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
                                IntOffset(x = 0, y = offset)
                            }
                            .clickable(enabled = true) {
                                //onEventClicked(0)
                                updater(index)
                                scope.launch {
                                    anim.snapTo(0f)
                                    anim.animateTo(
                                        targetValue = 1f,
                                    )
                                }

                            }
                    )
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

fun Modifier.zIndex(zIndex: () -> Float) = this.then(
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset.Zero, zIndex())
        }
    }
)

private fun LazyListLayoutInfo.kk(selectedIndex: Int): Float {
    val index =
        this.visibleItemsInfo.indexOfFirst { item -> item.index == selectedIndex }
    return when {
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
