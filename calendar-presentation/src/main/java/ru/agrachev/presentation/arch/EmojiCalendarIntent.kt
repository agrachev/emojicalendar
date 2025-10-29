package ru.agrachev.calendarpresentation.arch

import ru.agrachev.calendarpresentation.core.CalendarDateStorageKey
import ru.agrachev.calendarpresentation.model.CalendarRuleUILayout
import ru.agrachev.calendarpresentation.model.CalendarRuleUIModel
import ru.agrachev.calendarpresentation.model.MainCalendarDateUIModel
import ru.agrachev.calendarpresentation.widget.modal.ItemIndex

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
