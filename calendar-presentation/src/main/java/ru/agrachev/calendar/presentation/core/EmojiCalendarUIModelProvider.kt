package ru.agrachev.calendar.presentation.core

import ru.agrachev.calendar.domain.repository.DayModelStorage
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.model.MainCalendarUIModel

typealias MainCalendarUIModelStorage = DayModelStorage<CalendarDateStorageKey, CalendarDateStorageValue>

class EmojiCalendarUIModelProvider(
    private val storage: MainCalendarUIModelStorage,
) : UIModelProvider<EmojiCalendarUIModel> {

    override fun provideInstance(): EmojiCalendarUIModel =
        EmojiCalendarUIModel(
            mainCalendarUIModel = MainCalendarUIModel(
                mainCalendarDateModelStorage = storage,
            )
        )
}
