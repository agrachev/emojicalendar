package ru.agrachev.presentation.scope.editor

import ru.agrachev.calendardomain.model.Id
import ru.agrachev.calendardomain.model.RecurrenceRule
import ru.agrachev.presentation.model.CalendarRuleUiModelUpdater
import ru.agrachev.presentation.model.EmojiType
import ru.agrachev.presentation.model.TitleType

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
    ) {
        pendingRuleUpdater(
            CalendarRuleUiModelUpdater
                .DateRangeOffsetIndexesUpdater(
                    calendarRuleUIModel = pendingRuleProvider(),
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
