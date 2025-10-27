package ru.agrachev.emojicalendar.presentation.viewmodel

import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarLabel
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.model.LocalizedCalendarResources
import java.util.Locale

interface CalendarMviStateHolder :
    MviStateHolder<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    fun getNumberOfWeeks(offsetFromCurrentMonth: Int): Int

    fun getLocalizedCalendarResources(locale: Locale): LocalizedCalendarResources
}
