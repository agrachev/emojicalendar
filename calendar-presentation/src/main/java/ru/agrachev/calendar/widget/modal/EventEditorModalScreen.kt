package ru.agrachev.calendar.widget.modal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.agrachev.calendardomain.core.Constants.WEEK_DAY_COUNT
import ru.agrachev.calendardomain.core.length
import ru.agrachev.calendar.LocalLocalizedCalendarResources
import ru.agrachev.calendar.R
import ru.agrachev.calendar.core.Constants
import ru.agrachev.calendar.core.LocalDateProvider
import ru.agrachev.calendar.core.dateItemIndexes
import ru.agrachev.calendar.core.observeStateChanges
import ru.agrachev.calendar.core.regularOffset
import ru.agrachev.calendar.core.regularOffsets
import ru.agrachev.calendar.currentLocale
import ru.agrachev.calendar.model.CalendarEventUIModel
import ru.agrachev.calendar.model.CalendarRuleUILayout
import ru.agrachev.calendar.model.CalendarRuleUIModel
import ru.agrachev.calendar.model.emptyCalendarRuleUIModel
import ru.agrachev.calendar.scope.editor.EventEditorScope
import ru.agrachev.calendar.scope.editor.EventEditorStatefulScope
import ru.agrachev.calendar.theme.EmojiCalendarTheme
import ru.agrachev.calendar.theme.Typography
import ru.agrachev.calendar.toIntPx
import ru.agrachev.calendar.widget.CalendarDayItem
import ru.agrachev.calendar.widget.EmojiCalendarTextField
import ru.agrachev.calendar.widget.EmojiPicker
import ru.agrachev.calendar.widget.RepeatButton
import ru.agrachev.calendar.widget.rememberRecurrenceRuleButtonState
import ru.agrachev.calendar.widget.slider.DateRangeThumb
import ru.agrachev.calendar.widget.slider.OffsetRangeSlider
import ru.agrachev.calendar.widget.slider.rememberDateRangeThumbState
import ru.agrachev.calendar.widget.slider.rememberOffsetRangeSliderState
import java.time.temporal.WeekFields

@Composable
fun EventEditorModalScreen(
    pendingRuleProvider: () -> CalendarRuleUIModel,
    pendingRuleUpdater: (CalendarRuleUILayout) -> Unit,
    onCalendarRulePushRequest: (CalendarRuleUIModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val now = LocalDateProvider.current
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val layoutWidth = this.minWidth
        val contentWidth = layoutWidth * .9f
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            with(rememberEventEditorScope(pendingRuleProvider, pendingRuleUpdater)) {
                val tileIndexesRange = pendingRuleProvider().dateRangeOffsetIndexes
                val tileIndexOffset =
                    (WEEK_DAY_COUNT / 2 - tileIndexesRange.length / 2).coerceAtLeast(0)
                val calendarRowState = rememberLazyListState(
                    initialFirstVisibleItemIndex = tileIndexesRange.start - tileIndexOffset,
                    initialFirstVisibleItemScrollOffset =
                        -((layoutWidth - contentWidth) / 2f).toIntPx(),
                )
                val offsetRangeSliderState = with(tileIndexesRange.regularOffsets) {
                    rememberOffsetRangeSliderState(
                        startOffset = start,
                        endOffset = endInclusive,
                    )
                }
                val selectedDateRangeIndexes by remember {
                    derivedStateOf {
                        IntRange(
                            start = offsetRangeSliderState.startOffsetValue,
                            endInclusive = offsetRangeSliderState.endOffsetValue,
                        )
                    }
                }
                val calendarRuleTitleTextFieldState = rememberTextFieldState(
                    initialText = pendingRuleProvider().title,
                )
                val calendarEventTitleTextFieldState = rememberTextFieldState(
                    initialText = selectedCalendarEvent.title,
                )
                var isCalendarEventTitleTextFieldDirty by remember {
                    mutableStateOf(true)
                }
                val emojiPickedCallbackBuilder = rememberEmojiPickedCallbackBuilder()
                val emojiPickedHandler by remember {
                    derivedStateOf {
                        emojiPickedCallbackBuilder(selectedDateIndex)
                    }
                }
                val setSelectedDateIndex: (Int) -> Unit = remember {
                    { index ->
                        selectedDateIndex = index
                        with(selectedCalendarEvent.title) {
                            isCalendarEventTitleTextFieldDirty =
                                calendarEventTitleTextFieldState.text != this
                            calendarEventTitleTextFieldState.setTextAndPlaceCursorAtEnd(this)
                        }
                    }
                }
                val startThumbState = rememberDateRangeThumbState(
                    initiallyExpanded = true,
                )
                val endThumbState = rememberDateRangeThumbState(
                    initiallyExpanded = selectedDateRangeIndexes.length > 1,
                )
                LaunchedEffect(offsetRangeSliderState) {
                    snapshotFlow {
                        selectedDateRangeIndexes
                    }
                        .onEach {
                            startThumbState.dateState =
                                now.plusDays(it.start + 0L)
                            endThumbState.dateState =
                                now.plusDays(it.endInclusive - 1L)
                            endThumbState.expanded = it.length > 1
                        }
                        .map { it.dateItemIndexes }
                        .observeStateChanges { itemIndexes ->
                            requestDateRangeOffsetIndexesUpdate(itemIndexes)
                            val index = selectedDateIndex.coerceIn(
                                itemIndexes.start,
                                itemIndexes.endInclusive - 1
                            )
                            if (selectedDateIndex != index) {
                                setSelectedDateIndex(index)
                            }
                        }
                }
                ModalHeadingRow {
                    onCalendarRulePushRequest(pendingRuleProvider())
                }
                EventTitleRow(calendarRuleTitleTextFieldState)
                EmojiCalendarTextField(
                    textFieldState = calendarEventTitleTextFieldState,
                    hintText = stringResource(R.string.hint_event_notes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                )
                LaunchedEffect(Unit) {
                    snapshotFlow {
                        calendarEventTitleTextFieldState.text
                    }
                        .filter {
                            (isCalendarEventTitleTextFieldDirty xor true).apply {
                                isCalendarEventTitleTextFieldDirty =
                                    this && isCalendarEventTitleTextFieldDirty
                            }
                        }
                        .collect { text ->
                            requestCalendarEventsModelsUpdate(
                                dateIndex = selectedDateIndex,
                                title = text.toString(),
                            )
                        }
                }
                val weekdayNames = LocalLocalizedCalendarResources.current.weekdayNames
                LazyRow(
                    state = calendarRowState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearFocusOnTap(),
                ) {
                    items(count = Constants.INFINITE, key = { it }) { index ->
                        CalendarDayItem(
                            calendarEvent = pendingRuleProvider()
                                .getCalendarEventForIndex(index),
                            contentWidth = contentWidth,
                            dayOfWeekResolver = {
                                val dayOfWeek =
                                    WeekFields.of(LocalConfiguration.currentLocale).dayOfWeek()
                                val dayOfWeekIndex = now
                                    .plusDays(index.regularOffset.toLong())
                                    .get(dayOfWeek)
                                weekdayNames[dayOfWeekIndex - 1]
                            },
                            isItemActiveCallback = {
                                with(selectedDateRangeIndexes.dateItemIndexes) {
                                    index >= start && index < endInclusive
                                }
                            },
                            isItemSelectedCallback = {
                                index == selectedDateIndex
                            },
                            onItemClicked = {
                                setSelectedDateIndex(index)
                            },
                        )
                    }
                }
                val thumbModifier = Modifier
                    .size(18.dp)
                OffsetRangeSlider(
                    sliderState = offsetRangeSliderState,
                    startThumb = {
                        DateRangeThumb(
                            thumbState = startThumbState,
                            modifier = thumbModifier,
                        )
                    },
                    endThumb = {
                        DateRangeThumb(
                            thumbState = endThumbState,
                            modifier = thumbModifier,
                        )
                    },
                    modifier = Modifier
                        .width(contentWidth)
                        .offset(x = (layoutWidth - contentWidth) / 2 - 8.dp, y = 12.dp)
                        .clearFocusOnTap()
                        .moveWithList(
                            listState = calendarRowState,
                            firstVisibleItemOffset = offsetRangeSliderState.startOffsetValue - tileIndexOffset,
                        ),
                )
                EmojiPicker(
                    gridColumns = 8,
                    onEmojiPicked = { emoji: String ->
                        emojiPickedHandler(emoji)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clearFocusOnTap(),
                )
            }
        }
    }
}

@Composable
internal fun rememberEventEditorScope(
    pendingRuleProvider: () -> CalendarRuleUIModel,
    pendingRuleUpdater: (CalendarRuleUILayout) -> Unit,
): EventEditorScope {
    val selectedDateIndex = rememberSaveable {
        mutableIntStateOf(
            pendingRuleProvider().dateRangeOffsetIndexes.start
        )
    }
    val selectedCalendarEvent = remember {
        derivedStateOf {
            pendingRuleProvider().calendarEventsUiModels.find {
                it.dateIndex == selectedDateIndex.intValue
            }
        }
    }
    return remember {
        object : EventEditorStatefulScope() {
            override var selectedDateIndex by selectedDateIndex
            override val selectedCalendarEvent by selectedCalendarEvent
            override val pendingRuleProvider = pendingRuleProvider
            override val pendingRuleUpdater = pendingRuleUpdater
        }
    }
}

@Composable
private fun EventTitleRowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(
        modifier = modifier,
    ) { constraints ->
        val firstPassPlaceables = subcompose(Pass.FIRST, content).map {
            it.measure(constraints)
        }
        val layoutHeight = firstPassPlaceables.minOf { it.height }
        val updatedConstraints = constraints.copy(
            maxWidth = constraints.maxWidth - layoutHeight,
            maxHeight = layoutHeight,
            minWidth = 0,
        )
        val secondPassPlaceables = subcompose(Pass.SECOND, content).map {
            it.measure(updatedConstraints)
        }
        layout(constraints.minWidth, layoutHeight) {
            secondPassPlaceables.fold(0) { xOffset, placeable ->
                placeable.placeRelative(x = xOffset, y = 0)
                xOffset + placeable.width
            }
        }
    }
}

@Composable
private fun EventEditorScope.ModalHeadingRow(
    onSaveButtonClicked: (CalendarRuleUIModel) -> Unit,
) {
    val isNewEvent = rememberSaveable {
        pendingRuleProvider().calendarEventsUiModels.isEmpty()
    }
    val canSave by remember {
        derivedStateOf {
            pendingRuleProvider().calendarEventsUiModels.isNotEmpty() && pendingRuleProvider().title.isNotEmpty()
        }
    }
    EventTitleRowLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    ) {
        Text(
            text = stringResource(if (isNewEvent) R.string.title_new_event else R.string.title_edit_event),
            fontFamily = Typography.headlineSmall.fontFamily,
            fontSize = Typography.headlineSmall.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
        )
        AnimatedVisibility(
            visible = canSave,
            enter = slideInHorizontally(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                )
            ) { fullWidth ->
                (fullWidth * 1.5).toInt()
            },
            exit = slideOutHorizontally(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                )
            ) { fullWidth ->
                (fullWidth * 1.5).toInt()
            },
            label = "show hide save button",
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clearFocusOnTap(),
        ) {
            Image(
                painter = painterResource(android.R.drawable.ic_menu_save),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = canSave,
                        interactionSource = null,
                        indication = null,
                        onClick = {
                            onSaveButtonClicked(pendingRuleProvider())
                        }
                    ),
            )
        }
    }
}

@Composable
private fun EventEditorScope.EventTitleRow(
    state: TextFieldState,
) {
    EventTitleRowLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    ) {
        EmojiCalendarTextField(
            textFieldState = state,
            hintText = stringResource(R.string.hint_event_title),
            textStyle = Typography.headlineSmall,
            validator = {
                it.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
        )
        val recurrenceRuleButtonState = rememberRecurrenceRuleButtonState(
            initial = pendingRuleProvider().recurrenceRule,
        )
        RepeatButton(
            recurrenceRuleButtonState = recurrenceRuleButtonState,
            onRepeatModeChanged = {
                requestPendingRuleRecurrenceRuleUpdate(it)
            },
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clearFocusOnTap(),
        )
    }
    LaunchedEffect(state) {
        snapshotFlow {
            state.text
        }.observeStateChanges { text ->
            requestPendingRuleTitleUpdate(
                newTitle = text.toString(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventEditorModalScreenPreview() {
    EmojiCalendarTheme {
        EventEditorModalScreen(
            pendingRuleProvider = { emptyCalendarRuleUIModel() },
            pendingRuleUpdater = {

            },
            onCalendarRulePushRequest = {

            },
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}

@Composable
private fun Modifier.moveWithList(
    listState: LazyListState,
    firstVisibleItemOffset: Int = 0
): Modifier {
    val calculateOffset = fun(
        lazyListItemInfo: LazyListItemInfo,
        firstVisibleItemOffset: Int,
    ): Int {
        return (lazyListItemInfo.size + listState.layoutInfo.mainAxisItemSpacing) *
                (listState.firstVisibleItemIndex - firstVisibleItemOffset) +
                listState.firstVisibleItemScrollOffset
    }
    var initialOffset by rememberSaveable {
        mutableIntStateOf(0)
    }
    val layoutInfo by remember(listState) {
        derivedStateOf {
            listState.layoutInfo
        }
    }
    val viewOffset by remember(listState) {
        derivedStateOf {
            layoutInfo.visibleItemsInfo.firstOrNull()
                ?.let { item ->
                    initialOffset - calculateOffset(item, 0)
                } ?: 0
        }
    }
    LaunchedEffect(listState) {
        if (initialOffset == 0) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
            }
                .first { it.isNotEmpty() }
                .first()
                .also { item ->
                    initialOffset = calculateOffset(item, firstVisibleItemOffset)
                }
        }
    }
    return this then Modifier.offset {
        IntOffset(x = viewOffset, y = 0)
    }
}

@Composable
@NonRestartableComposable
private fun EventEditorScope.rememberEmojiPickedCallbackBuilder() = remember {
    fun(index: Int) = { emoji: String ->
        this.requestCalendarEventsModelsUpdate(
            dateIndex = index,
            emoji = emoji,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun Modifier.clearFocusOnTap() = LocalFocusManager.current.let { focusManager ->
    this then Modifier
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(false)
                focusManager.clearFocus()
            }
        }
}

private enum class Pass {
    FIRST,
    SECOND,
}

private inline val CalendarEventUIModel?.title
    get() = this?.title.orEmpty()
