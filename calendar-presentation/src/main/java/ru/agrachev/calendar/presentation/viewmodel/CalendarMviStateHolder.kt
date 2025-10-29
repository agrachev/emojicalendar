package ru.agrachev.calendar.presentation.viewmodel

import ru.agrachev.calendar.presentation.arch.EmojiCalendarIntent
import ru.agrachev.calendar.presentation.arch.EmojiCalendarLabel
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.model.LocalizedCalendarResources
import java.util.Locale

interface CalendarMviStateHolder :
    MviStateHolder<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    fun getNumberOfWeeks(offsetFromCurrentMonth: Int): Int

    fun getLocalizedCalendarResources(locale: Locale): LocalizedCalendarResources
}
