package ru.agrachev.emojicalendar.presentation.arch

import ru.agrachev.emojicalendar.presentation.widget.modal.ItemIndex

sealed interface EmojiCalendarLabel {

    data class NavigateToItem(
        val index: ItemIndex,
    ) : EmojiCalendarLabel

    object CalendarRuleUpdateSuccess : EmojiCalendarLabel

    data class CalendarRuleUpdateFailed(
        val reason: Throwable,
    ) : EmojiCalendarLabel
}
