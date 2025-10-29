package ru.agrachev.calendarpresentation.viewmodel

import ru.agrachev.calendarpresentation.arch.EmojiCalendarIntent
import ru.agrachev.calendarpresentation.arch.EmojiCalendarLabel
import ru.agrachev.calendarpresentation.model.EmojiCalendarUIModel
import ru.agrachev.calendarpresentation.model.LocalizedCalendarResources
import java.util.Locale

interface CalendarMviStateHolder :
    MviStateHolder<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    fun getNumberOfWeeks(offsetFromCurrentMonth: Int): Int

    fun getLocalizedCalendarResources(locale: Locale): LocalizedCalendarResources
}
