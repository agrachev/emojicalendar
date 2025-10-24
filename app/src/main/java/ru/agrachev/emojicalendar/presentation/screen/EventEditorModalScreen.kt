package ru.agrachev.emojicalendar.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.agrachev.emojicalendar.R
import ru.agrachev.emojicalendar.domain.model.Id
import ru.agrachev.emojicalendar.domain.model.RecurrenceRule
import ru.agrachev.emojicalendar.presentation.LocalLocalizedCalendarResources
import ru.agrachev.emojicalendar.presentation.core.Constants
import ru.agrachev.emojicalendar.presentation.core.dateItemIndexes
import ru.agrachev.emojicalendar.presentation.core.length
import ru.agrachev.emojicalendar.presentation.core.nullableDateIndex
import ru.agrachev.emojicalendar.presentation.core.nullableEmoji
import ru.agrachev.emojicalendar.presentation.core.nullableId
import ru.agrachev.emojicalendar.presentation.core.nullableTitle
import ru.agrachev.emojicalendar.presentation.core.observeStateChanges
import ru.agrachev.emojicalendar.presentation.core.regularOffsets
import ru.agrachev.emojicalendar.presentation.model.AAA
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.model.EmojiType
import ru.agrachev.emojicalendar.presentation.model.TitleType
import ru.agrachev.emojicalendar.presentation.model.emptyCalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import ru.agrachev.emojicalendar.presentation.theme.Typography
import ru.agrachev.emojicalendar.presentation.toIntPx
import ru.agrachev.emojicalendar.presentation.widget.CalendarDayItem
import ru.agrachev.emojicalendar.presentation.widget.DateRangeThumb
import ru.agrachev.emojicalendar.presentation.widget.EmojiCalendarTextField
import ru.agrachev.emojicalendar.presentation.widget.EmojiPicker
import ru.agrachev.emojicalendar.presentation.widget.OffsetRangeSlider
import ru.agrachev.emojicalendar.presentation.widget.RepeatButton
import ru.agrachev.emojicalendar.presentation.widget.rememberDateRangeThumbState
import ru.agrachev.emojicalendar.presentation.widget.rememberOffsetRangeSliderState
import ru.agrachev.emojicalendar.presentation.widget.rememberRecurrenceRuleButtonState
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
fun EventEditorModalScreen(
    pendingRule: CalendarRuleUIModel,
    pendingRuleUpdater: (updater: AAA) -> Unit,
    onCalendarRulePushRequest: (CalendarRuleUIModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    println(pendingRule)
    val pendingRuleState by rememberUpdatedState(pendingRule)
    val now = remember(System.currentTimeMillis() / (60L * 60L * 1000L)) {
        LocalDate.now()
    }
    BoxWithConstraints(
        modifier = modifier
    ) {
        val layoutWidth = this.minWidth
        val contentWidth = layoutWidth * .9f
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            var selectedDateIndex by rememberSaveable {
                mutableIntStateOf(
                    pendingRuleState.dateRangeOffsetIndexes.start
                )
            }
            val selectedCalendarEvent by remember {
                derivedStateOf {
                    pendingRuleState.calendarEventsUiModels.find {
                        it.dateIndex == selectedDateIndex
                    }
                }
            }
            val aa: (Int) -> CalendarEventUIModel? = { index ->
                pendingRuleState.getCalendarEventForIndex(index)
            }
            val scope = remember {
                object : EventEditorModalScreenScope {
                    override val pendingRuleState
                        get() = pendingRuleState
                    override val selectedCalendarEvent
                        get() = selectedCalendarEvent
                    override val pendingRuleUpdater
                        get() = pendingRuleUpdater
                }
            }
            val tileIndexesRange = pendingRule.dateRangeOffsetIndexes
            val tileIndexOffset = (3 - tileIndexesRange.length / 2).coerceAtLeast(0)
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
            val state = rememberTextFieldState(
                initialText = pendingRule.title,
            )
            val state2 = rememberTextFieldState(
                initialText = selectedCalendarEvent?.title ?: "",
            )
            var changed by remember {
                mutableStateOf(true)
            }
            val u = remember {
                {
                    with(selectedCalendarEvent?.title ?: "") {
                        println("GHGHGHU $selectedCalendarEvent")
                        changed = state2.text != this
                        state2.setTextAndPlaceCursorAtEnd(this)
                    }
                }
            }
            val emojiPickedCallbackBuilder = scope.rememberEmojiPickedCallbackBuilder()
            var emojiPickedHandler by remember {
                mutableStateOf(emojiPickedCallbackBuilder(selectedDateIndex))
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
                        scope.requestDateRangeOffsetIndexesUpdate(itemIndexes)
                        val index = selectedDateIndex.coerceIn(
                            itemIndexes.start,
                            itemIndexes.endInclusive - 1
                        )
                        if (selectedDateIndex != index) {
                            selectedDateIndex = index.also { index ->
                                emojiPickedHandler = emojiPickedCallbackBuilder(index)
                            }
                            u()
                        }
                    }
            }
            with(scope) {
                ModalHeadingRow {
                    onCalendarRulePushRequest(pendingRuleState)
                }
                EventTitleRow(state)
            }
            EmojiCalendarTextField(
                state = state2,
                hintText = "hello 2",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                colors = TextFieldDefaults.colors(
                    errorIndicatorColor = Color.Red,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = TextFieldDefaults.colors().disabledTextColor
                )
            )
            LaunchedEffect(Unit) {
                snapshotFlow {
                    state2.text
                }
                    .filter {
                        println("GHGHGH ${changed xor true} ${(changed xor false) && changed}")
                        (changed xor true).apply {
                            changed = this && changed
                        }
                    }
                    .collect { text ->
                        println("GHGHGH changed text: $text $selectedCalendarEvent")
                        scope.requestCalendarEventsModelsUpdate(
                            dateIndex = selectedDateIndex,
                            title = text.toString(),
                        )
                    }
            }
            val s = rememberCoroutineScope()
            val weekdayNames = LocalLocalizedCalendarResources.current.weekdayNames
            LazyRow(
                state = calendarRowState,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                items(count = Int.MAX_VALUE, key = { it }) { index ->
                    CalendarDayItem(
                        calendarEvent = aa(index),
                        contentWidth = contentWidth,
                        dayOfWeekResolver = {
                            val aa =
                                WeekFields.of(LocalConfiguration.current.locales[0]).dayOfWeek()
                            val dayOfWeekIndex = now
                                .plusDays((index - Constants.NOW_INDEX).toLong())
                                .get(aa)
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
                            selectedDateIndex = index.also {
                                emojiPickedHandler = emojiPickedCallbackBuilder(it)
                            }
                            u()
                        }
                    )
                }
            }
            val thumbModifier = Modifier
                .size(16.dp)
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
                    .offset(x = (layoutWidth - contentWidth) / 2 - 8.dp, y = 8.dp)
                    .then(
                        remember {
                            Modifier
                                .moveWithList(
                                    listState = calendarRowState,
                                    firstVisibleItemOffset = offsetRangeSliderState.startOffsetValue - tileIndexOffset,
                                )
                        }
                    )
            )
            EmojiPicker(
                gridColumns = 8,
                onEmojiPicked = { emoji: String ->
                    emojiPickedHandler(emoji)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

private enum class Pass {
    FIRST,
    SECOND,
}

@Composable
fun EventTitleRowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(
        modifier = modifier,
    ) { constraints ->
        val firstPassPlaceables = subcompose(Pass.FIRST, content).map {
            it.measure(constraints)
        }
        val layoutHeight = firstPassPlaceables.firstOrNull()?.height ?: 0
        val updatedConstraints = constraints.copy(
            maxWidth = constraints.maxWidth - layoutHeight,
            maxHeight = layoutHeight,
            minWidth = 0,
        )
        val secondPassPlaceables = subcompose(Pass.SECOND, content).map {
            it.measure(updatedConstraints)
        }
        layout(constraints.minWidth, layoutHeight) {
            secondPassPlaceables.fold(0, { xOffset, placeable ->
                placeable.placeRelative(x = xOffset, y = 0)
                xOffset + placeable.width
            })
        }
    }
}

@Composable
private inline fun EventEditorModalScreenScope.ModalHeadingRow(
    crossinline onSaveButtonClicked: (CalendarRuleUIModel) -> Unit,
) {
    val isNewEvent = rememberSaveable {
        pendingRuleState.calendarEventsUiModels.isEmpty()
    }
    val canSave by remember {
        derivedStateOf {
            pendingRuleState.calendarEventsUiModels.isNotEmpty() && pendingRuleState.title.isNotEmpty()
        }
    }
    EventTitleRowLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                            onSaveButtonClicked(pendingRuleState)
                        }
                    ),
            )
        }
    }
}

@Composable
private fun EventEditorModalScreenScope.EventTitleRow(
    state: TextFieldState,
) {
    EventTitleRowLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        val ts = Typography.headlineSmall
        EmojiCalendarTextField(
            state = state,
            hintText = "hello",
            textStyle = ts,
            hintTextStyle = ts,
            validator = { text ->
                text.isNotEmpty()
            },
            modifier = Modifier
                //.weight(1f)
                .fillMaxWidth()
                .padding(end = 16.dp),
            colors = TextFieldDefaults.colors(
                errorIndicatorColor = Color.Red,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = TextFieldDefaults.colors().disabledTextColor
            )
        )
        val recurrenceRuleButtonState = rememberRecurrenceRuleButtonState(
            initial = pendingRuleState.recurrenceRule,
        )
        RepeatButton(
            recurrenceRuleButtonState = recurrenceRuleButtonState,
            onRepeatModeChanged = {
                requestPendingRuleRecurrenceRuleUpdate(it)
            },
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
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
            pendingRule = emptyCalendarRuleUIModel(),
            pendingRuleUpdater = {

            },
            onCalendarRulePushRequest = {

            },
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}

fun Modifier.moveWithList(
    listState: LazyListState,
    firstVisibleItemOffset: Int = 0
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "moveWithList"
        properties["listState"] = listState
        properties["firstVisibleItemOffset"] = firstVisibleItemOffset
    }
) {
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
    this.then(
        Modifier.offset {
            IntOffset(x = viewOffset, y = 0)
        }
    )
}

sealed class CalendarRuleUiModelUpdater(
    calendarRuleUIModel: CalendarRuleUIModel
) : AAA by calendarRuleUIModel {

    class TitleUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        val newTitle: String,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val title: String
            get() = newTitle
    }

    class DateRangeOffsetIndexesUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        val newDateRangeOffsetIndexes: IntRange,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val dateRangeOffsetIndexes: IntRange
            get() = newDateRangeOffsetIndexes
        override val calendarEventsUiModels: Set<CalendarEventUIModel>
            get() = super.calendarEventsUiModels.toMutableSet().let { events ->
                events.removeAll {
                    with(newDateRangeOffsetIndexes) {
                        it.dateIndex < start || it.dateIndex >= endInclusive
                    }
                }
                events.toSet()
            }
    }

    class CalendarEventsUIModelsUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        val calendarEventUIModel: CalendarEventUIModel?,
        val calendarEventUIModelReducer: CalendarEventUIModel.() -> CalendarEventUIModel,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val calendarEventsUiModels: Set<CalendarEventUIModel>
            get() = with(
                (calendarEventUIModel ?: CalendarEventUIModel())
                    .calendarEventUIModelReducer()
            ) {
                super.calendarEventsUiModels.toMutableSet().let { events ->
                    events.removeIf { it.dateIndex == this.dateIndex }
                    events.add(this)
                    events.sortedWith(
                        comparator = Constants.CALENDAR_EVENTS_UI_MODEL_COMPARATOR,
                    ).toSet()
                }
            }
    }

    class RecurrenceRuleUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        val newRecurrenceRule: RecurrenceRule,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val recurrenceRule: RecurrenceRule
            get() = newRecurrenceRule
    }
}

interface EventEditorModalScreenScope {
    val pendingRuleState: CalendarRuleUIModel
    val selectedCalendarEvent: CalendarEventUIModel?
    val pendingRuleUpdater: (updater: AAA) -> Unit
}

@Composable
@NonRestartableComposable
private fun EventEditorModalScreenScope.rememberEmojiPickedCallbackBuilder() = remember {
    fun(index: Int) = { emoji: String ->
        this.requestCalendarEventsModelsUpdate(
            dateIndex = index,
            emoji = emoji,
        )
    }
}

private fun EventEditorModalScreenScope.requestPendingRuleTitleUpdate(
    newTitle: String,
) {
    pendingRuleUpdater(
        CalendarRuleUiModelUpdater
            .TitleUpdater(
                calendarRuleUIModel = pendingRuleState,
                newTitle = newTitle,
            )
    )
}

private fun EventEditorModalScreenScope.requestDateRangeOffsetIndexesUpdate(
    range: IntRange,
) {
    pendingRuleUpdater(
        CalendarRuleUiModelUpdater
            .DateRangeOffsetIndexesUpdater(
                calendarRuleUIModel = pendingRuleState,
                newDateRangeOffsetIndexes = range,
            )
    )
}

private fun EventEditorModalScreenScope.requestCalendarEventsModelsUpdate(
    id: Id = selectedCalendarEvent.nullableId,
    title: TitleType = selectedCalendarEvent.nullableTitle,
    emoji: EmojiType = selectedCalendarEvent.nullableEmoji,
    dateIndex: Int = selectedCalendarEvent.nullableDateIndex,
) {
    pendingRuleUpdater(
        CalendarRuleUiModelUpdater
            .CalendarEventsUIModelsUpdater(
                calendarRuleUIModel = pendingRuleState,
                calendarEventUIModel = selectedCalendarEvent,
                calendarEventUIModelReducer = {
                    copy(
                        id = id,
                        title = title,
                        emoji = emoji,
                        dateIndex = dateIndex,
                    )
                },
            )
    )
}

private fun EventEditorModalScreenScope.requestPendingRuleRecurrenceRuleUpdate(
    newRecurrenceRule: RecurrenceRule,
) {
    pendingRuleUpdater(
        CalendarRuleUiModelUpdater
            .RecurrenceRuleUpdater(
                calendarRuleUIModel = pendingRuleState,
                newRecurrenceRule = newRecurrenceRule,
            )
    )
}
