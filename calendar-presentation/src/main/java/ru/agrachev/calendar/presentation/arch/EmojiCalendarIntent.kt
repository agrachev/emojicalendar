package ru.agrachev.calendar.presentation.arch

import ru.agrachev.calendar.presentation.core.CalendarDateStorageKey
import ru.agrachev.calendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.calendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.calendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.calendar.presentation.widget.modal.ItemIndex

sealed interface EmojiCalendarIntent {

    data class LoadDayModelsForMonth(
        val monthOffsetFromNow: CalendarDateStorageKey,
    ) : EmojiCalendarIntent

    data class PushCalendarRule(
        val calendarRuleUIModel: CalendarRuleUIModel,
    ) : EmojiCalendarIntent

    object RequestCalendarUpdate : EmojiCalendarIntent

    data class OpenEventsBrowserModal(
        val model: MainCalendarDateUIModel,
    ) : EmojiCalendarIntent

    object DismissEventsBrowserModal : EmojiCalendarIntent

    data class NavigateToItem(
        val itemIndex: ItemIndex,
    ) : EmojiCalendarIntent

    data class UpdatePendingRule(
        val pendingRuleUpdater: CalendarRuleUILayout,
    ) : EmojiCalendarIntent
}
