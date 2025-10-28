package ru.agrachev.emojicalendar.presentation.arch

import ru.agrachev.emojicalendar.presentation.core.CalendarDateStorageKey
import ru.agrachev.emojicalendar.presentation.core.CalendarDateStorageValue
import ru.agrachev.emojicalendar.presentation.widget.modal.ItemIndex
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel

internal sealed interface EmojiCalendarMessage {

    data class DayModelsLoaded(
        val key: CalendarDateStorageKey,
        val models: CalendarDateStorageValue,
    ) : EmojiCalendarMessage

    data class EventsBrowserModalRequested(
        val dateUiModel: MainCalendarDateUIModel,
    ) : EmojiCalendarMessage

    object EventsBrowserModalDismissed : EmojiCalendarMessage

    data class PendingRuleUpdated(
        val itemIndex: ItemIndex,
    ) : EmojiCalendarMessage

    data class PendingRuleUpdated2(
        val pendingRuleUpdater: CalendarRuleUILayout,
    ) : EmojiCalendarMessage

    object CalendarUpdateRequested : EmojiCalendarMessage
}
