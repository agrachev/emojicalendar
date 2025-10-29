package ru.agrachev.calendarpresentation.arch

import ru.agrachev.calendarpresentation.widget.modal.ItemIndex

sealed interface EmojiCalendarLabel {

    data class NavigateToItem(
        val index: ItemIndex,
    ) : EmojiCalendarLabel

    object CalendarRuleUpdateSuccess : EmojiCalendarLabel

    data class CalendarRuleUpdateFailed(
        val reason: Throwable,
    ) : EmojiCalendarLabel
}
