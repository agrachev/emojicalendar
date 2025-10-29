package ru.agrachev.calendarpresentation.scope.editor

import ru.agrachev.calendardomain.model.Id
import ru.agrachev.calendardomain.model.RecurrenceRule
import ru.agrachev.calendarpresentation.core.nullableDateIndex
import ru.agrachev.calendarpresentation.core.nullableEmoji
import ru.agrachev.calendarpresentation.core.nullableId
import ru.agrachev.calendarpresentation.core.nullableTitle
import ru.agrachev.calendarpresentation.model.CalendarEventUIModel
import ru.agrachev.calendarpresentation.model.CalendarRuleUILayout
import ru.agrachev.calendarpresentation.model.CalendarRuleUIModel
import ru.agrachev.calendarpresentation.model.EmojiType
import ru.agrachev.calendarpresentation.model.TitleType

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