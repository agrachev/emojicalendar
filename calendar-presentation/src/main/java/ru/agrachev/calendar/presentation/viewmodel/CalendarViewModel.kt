package ru.agrachev.calendar.presentation.viewmodel

import ru.agrachev.calendar.domain.repository.CalendarDataRepository
import ru.agrachev.calendar.presentation.arch.EmojiCalendarIntent
import ru.agrachev.calendar.presentation.arch.EmojiCalendarLabel
import ru.agrachev.calendar.presentation.arch.EmojiCalendarStore
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.model.LocalizedCalendarResources
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
