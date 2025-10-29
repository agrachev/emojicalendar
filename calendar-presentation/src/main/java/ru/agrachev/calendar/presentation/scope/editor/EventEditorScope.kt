package ru.agrachev.calendar.presentation.scope.editor

import ru.agrachev.calendar.domain.model.Id
import ru.agrachev.calendar.domain.model.RecurrenceRule
import ru.agrachev.calendar.presentation.core.nullableDateIndex
import ru.agrachev.calendar.presentation.core.nullableEmoji
import ru.agrachev.calendar.presentation.core.nullableId
import ru.agrachev.calendar.presentation.core.nullableTitle
import ru.agrachev.calendar.presentation.model.CalendarEventUIModel
import ru.agrachev.calendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.calendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.calendar.presentation.model.EmojiType
import ru.agrachev.calendar.presentation.model.TitleType

internal interface EventEditorScope {
    var selectedDateIndex: Int
    val selectedCalendarEvent: CalendarEventUIModel?
    val pendingRuleProvider: () -> CalendarRuleUIModel
    val pendingRuleUpdater: (CalendarRuleUILayout) -> Unit

    fun requestPendingRuleTitleUpdate(newTitle: String)
    fun requestDateRangeOffsetIndexesUpdate(range: IntRange)
    fun requestCalendarEventsModelsUpdate(
        id: Id = selectedCalendarEvent.nullableId,
        title: TitleType = selectedCalendarEvent.nullableTitle,
        emoji: EmojiType = selectedCalendarEvent.nullableEmoji,
        dateIndex: Int = selectedCalendarEvent.nullableDateIndex,
    )

    fun requestPendingRuleRecurrenceRuleUpdate(newRecurrenceRule: RecurrenceRule)
}