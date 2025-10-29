package ru.agrachev.calendarpresentation.model

import ru.agrachev.calendardomain.model.RecurrenceRule
import ru.agrachev.calendarpresentation.core.Constants

internal sealed class CalendarRuleUiModelUpdater(
    calendarRuleUIModel: CalendarRuleUIModel
) : CalendarRuleUILayout by calendarRuleUIModel {

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
