package ru.agrachev.calendarpresentation.arch

import ru.agrachev.calendarpresentation.core.CalendarDateStorageKey
import ru.agrachev.calendarpresentation.core.CalendarDateStorageValue
import ru.agrachev.calendarpresentation.model.CalendarRuleUILayout
import ru.agrachev.calendarpresentation.model.MainCalendarDateUIModel
import ru.agrachev.calendarpresentation.widget.modal.ItemIndex

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
