package ru.agrachev.emojicalendar.presentation.arch

import ru.agrachev.emojicalendar.presentation.model.AAA
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.screen.ItemIndex

internal sealed interface EmojiCalendarMessage {

    data class DayModelsLoaded(
        val key: Int,
        val models: List<MainCalendarDateUIModel>,
    ) : EmojiCalendarMessage

    data class EventsBrowserModalRequested(
        val dateUiModel: MainCalendarDateUIModel,
    ) : EmojiCalendarMessage

    object EventsBrowserModalDismissed : EmojiCalendarMessage

    data class PendingRuleUpdated(
        val itemIndex: ItemIndex,
    ) : EmojiCalendarMessage

    data class PendingRuleUpdated2(
        val pendingRuleUpdater: AAA,
    ) : EmojiCalendarMessage

    object CalendarUpdateRequested : EmojiCalendarMessage
}
