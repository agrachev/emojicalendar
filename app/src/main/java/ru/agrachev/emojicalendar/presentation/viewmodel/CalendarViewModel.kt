package ru.agrachev.emojicalendar.presentation.viewmodel

import ru.agrachev.emojicalendar.domain.repository.CalendarDataRepository
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarLabel
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarStore
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.model.LocalizedCalendarResources
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
