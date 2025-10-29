package ru.agrachev.presentation.viewmodel

import ru.agrachev.presentation.arch.EmojiCalendarIntent
import ru.agrachev.presentation.arch.EmojiCalendarLabel
import ru.agrachev.presentation.model.EmojiCalendarUIModel
import ru.agrachev.presentation.model.LocalizedCalendarResources
import java.util.Locale

interface CalendarMviStateHolder :
    MviStateHolder<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    fun getNumberOfWeeks(offsetFromCurrentMonth: Int): Int

    fun getLocalizedCalendarResources(locale: Locale): LocalizedCalendarResources
}
