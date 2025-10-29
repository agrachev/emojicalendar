package ru.agrachev.calendar.presentation.arch

import ru.agrachev.calendar.presentation.widget.modal.ItemIndex

sealed interface EmojiCalendarLabel {

    data class NavigateToItem(
        val index: ItemIndex,
    ) : EmojiCalendarLabel

    object CalendarRuleUpdateSuccess : EmojiCalendarLabel

    data class CalendarRuleUpdateFailed(
        val reason: Throwable,
    ) : EmojiCalendarLabel
}
