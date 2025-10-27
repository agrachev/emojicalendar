package ru.agrachev.emojicalendar.presentation.core

import ru.agrachev.emojicalendar.domain.repository.DayModelStorage
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarUIModel

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
