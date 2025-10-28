package ru.agrachev.emojicalendar.presentation.arch

import ru.agrachev.emojicalendar.presentation.core.CalendarDateStorageKey
import ru.agrachev.emojicalendar.presentation.widget.modal.ItemIndex
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel

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
