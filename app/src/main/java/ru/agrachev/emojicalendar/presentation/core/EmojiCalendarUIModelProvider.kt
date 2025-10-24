package ru.agrachev.emojicalendar.presentation.core

import ru.agrachev.emojicalendar.domain.repository.CalendarDataRepository
import ru.agrachev.emojicalendar.domain.repository.DayModelStorage
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarUIModel
import java.time.format.TextStyle
import java.util.Locale

typealias MainCalendarUIModelStorage = DayModelStorage<Int, List<MainCalendarDateUIModel>>

class EmojiCalendarUIModelProvider(
    private val storage: MainCalendarUIModelStorage,
    private val calendarDataRepository: CalendarDataRepository,
) : UIModelProvider<EmojiCalendarUIModel> {

    override fun provideInstance(): EmojiCalendarUIModel =
        with(Locale.getDefault()) {
            EmojiCalendarUIModel(
                mainCalendarUIModel = MainCalendarUIModel(
                    mainCalendarDateModelStorage = storage,
                    weekdayNames = calendarDataRepository.getLocalizedWeekdayNames(
                        locale = this,
                        style = TextStyle.SHORT
                    ),
                    monthNames = calendarDataRepository.getLocalizedMonthNames(
                        locale = this,
                        style = TextStyle.SHORT
                    ),
                )
            )
        }
}
