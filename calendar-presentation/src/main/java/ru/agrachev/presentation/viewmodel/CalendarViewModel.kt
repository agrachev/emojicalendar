package ru.agrachev.calendarpresentation.viewmodel

import ru.agrachev.calendardomain.repository.CalendarDataRepository
import ru.agrachev.calendarpresentation.arch.EmojiCalendarIntent
import ru.agrachev.calendarpresentation.arch.EmojiCalendarLabel
import ru.agrachev.calendarpresentation.arch.EmojiCalendarStore
import ru.agrachev.calendarpresentation.model.EmojiCalendarUIModel
import ru.agrachev.calendarpresentation.model.LocalizedCalendarResources
import java.time.format.TextStyle
import java.util.Locale

class CalendarViewModel(
    store: EmojiCalendarStore,
    private val calendarDataRepository: CalendarDataRepository,
) : BaseMviViewModel<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel>(store),
    CalendarMviStateHolder {

    override fun getNumberOfWeeks(offsetFromCurrentMonth: Int) =
        calendarDataRepository.getNumberOfWeeks(offsetFromCurrentMonth)

    override fun getLocalizedCalendarResources(locale: Locale) = LocalizedCalendarResources(
        weekdayNames = calendarDataRepository.getLocalizedWeekdayNames(locale, TextStyle.SHORT),
        monthNames = calendarDataRepository.getLocalizedMonthNames(locale, style = TextStyle.SHORT),
    )
}
