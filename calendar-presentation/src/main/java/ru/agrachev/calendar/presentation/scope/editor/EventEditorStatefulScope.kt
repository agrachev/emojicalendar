package ru.agrachev.calendar.presentation.scope.editor

import ru.agrachev.calendar.domain.model.Id
import ru.agrachev.calendar.domain.model.RecurrenceRule
import ru.agrachev.calendar.presentation.model.CalendarRuleUiModelUpdater
import ru.agrachev.calendar.presentation.model.EmojiType
import ru.agrachev.calendar.presentation.model.TitleType

internal abstract class EventEditorStatefulScope internal constructor() : EventEditorScope {

    override fun requestPendingRuleTitleUpdate(
        newTitle: String,
    ) {
        pendingRuleUpdater(
            CalendarRuleUiModelUpdater
                .TitleUpdater(
                    calendarRuleUIModel = pendingRuleProvider(),
                    newTitle = newTitle,
                )
        )
    }

    override fun requestDateRangeOffsetIndexesUpdate(
        range: IntRange,
        offset: Int,
    ) {
        pendingRuleUpdater(
            CalendarRuleUiModelUpdater
                .DateRangeOffsetIndexesUpdater(
                    calendarRuleUIModel = pendingRuleProvider().let {
                        if (offset == 0) it else it.copy(
                            calendarEventsUiModels = it.calendarEventsUiModels
                                .map { m ->
                                    m.copy(
                                        dateIndex = m.dateIndex + offset,
                                    )
                                }.toSet(),
                        )
                    },
                    newDateRangeOffsetIndexes = range,
                )
        )
    }

    override fun requestCalendarEventsModelsUpdate(
        id: Id,
        title: TitleType,
        emoji: EmojiType,
        dateIndex: Int,
    ) {
        pendingRuleUpdater(
            CalendarRuleUiModelUpdater
                .CalendarEventsUIModelsUpdater(
                    calendarRuleUIModel = pendingRuleProvider(),
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

    override fun requestPendingRuleRecurrenceRuleUpdate(
        newRecurrenceRule: RecurrenceRule,
    ) {
        pendingRuleUpdater(
            CalendarRuleUiModelUpdater
                .RecurrenceRuleUpdater(
                    calendarRuleUIModel = pendingRuleProvider(),
                    newRecurrenceRule = newRecurrenceRule,
                )
        )
    }
}
