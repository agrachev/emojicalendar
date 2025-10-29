package ru.agrachev.presentation.arch

import ru.agrachev.presentation.core.CalendarDateStorageKey
import ru.agrachev.presentation.core.CalendarDateStorageValue
import ru.agrachev.presentation.model.CalendarRuleUILayout
import ru.agrachev.presentation.model.MainCalendarDateUIModel
import ru.agrachev.presentation.widget.modal.ItemIndex

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
