package ru.agrachev.presentation.arch

import ru.agrachev.presentation.core.CalendarDateStorageKey
import ru.agrachev.presentation.model.CalendarRuleUILayout
import ru.agrachev.presentation.model.CalendarRuleUIModel
import ru.agrachev.presentation.model.MainCalendarDateUIModel
import ru.agrachev.presentation.widget.modal.ItemIndex

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
