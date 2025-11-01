package ru.agrachev.calendar.presentation.model

import ru.agrachev.calendar.domain.model.RecurrenceRule
import ru.agrachev.calendar.presentation.core.Constants

internal sealed class CalendarRuleUiModelUpdater(
    calendarRuleUIModel: CalendarRuleUIModel
) : CalendarRuleUILayout by calendarRuleUIModel {

    class TitleUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        newTitle: String,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val title = newTitle
    }

    class DateRangeOffsetIndexesUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        newDateRangeOffsetIndexes: IntRange,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val dateRangeOffsetIndexes = newDateRangeOffsetIndexes
        override val calendarEventsUiModels =
            super.calendarEventsUiModels.toMutableSet().let { events ->
                events.removeAll {
                    with(newDateRangeOffsetIndexes) {
                        it.dateIndex !in start..<endInclusive
                    }
                }
                events.toSet()
            }
    }

    class CalendarEventsUIModelsUpdater(
        calendarRuleUIModel: CalendarRuleUIModel,
        calendarEventUIModel: CalendarEventUIModel?,
        calendarEventUIModelReducer: CalendarEventUIModel.() -> CalendarEventUIModel,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val calendarEventsUiModels = with(
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
        newRecurrenceRule: RecurrenceRule,
    ) : CalendarRuleUiModelUpdater(calendarRuleUIModel) {
        override val recurrenceRule = newRecurrenceRule
    }
}
