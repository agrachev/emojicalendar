package ru.agrachev.presentation.scope.editor

import ru.agrachev.calendardomain.model.Id
import ru.agrachev.calendardomain.model.RecurrenceRule
import ru.agrachev.presentation.core.nullableDateIndex
import ru.agrachev.presentation.core.nullableEmoji
import ru.agrachev.presentation.core.nullableId
import ru.agrachev.presentation.core.nullableTitle
import ru.agrachev.presentation.model.CalendarEventUIModel
import ru.agrachev.presentation.model.CalendarRuleUILayout
import ru.agrachev.presentation.model.CalendarRuleUIModel
import ru.agrachev.presentation.model.EmojiType
import ru.agrachev.presentation.model.TitleType

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