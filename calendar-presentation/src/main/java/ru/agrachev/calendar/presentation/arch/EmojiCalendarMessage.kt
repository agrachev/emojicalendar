package ru.agrachev.calendar.presentation.arch

import ru.agrachev.calendar.presentation.core.CalendarDateStorageKey
import ru.agrachev.calendar.presentation.core.CalendarDateStorageValue
import ru.agrachev.calendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.calendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.calendar.presentation.widget.modal.ItemIndex

internal sealed interface EmojiCalendarMessage {

    data class DayModelsLoaded(
        val key: CalendarDateStorageKey,
        val models: CalendarDateStorageValue,
    ) : EmojiCalendarMessage

    data class EventsBrowserModalRequested(
        val dateUiModel: MainCalendarDateUIModel,
    ) : EmojiCalendarMessage

    object EventsBrowserModalDismissed : EmojiCalendarMessage

    data class PendingRuleSelected(
        val itemIndex: ItemIndex,
    ) : EmojiCalendarMessage

    data class PendingRuleUpdated(
        val pendingRuleUpdater: CalendarRuleUILayout,
    ) : EmojiCalendarMessage

    object CalendarUpdateRequested : EmojiCalendarMessage
}
