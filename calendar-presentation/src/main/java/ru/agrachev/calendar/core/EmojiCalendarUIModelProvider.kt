package ru.agrachev.presentation.core

import ru.agrachev.calendardomain.repository.DayModelStorage
import ru.agrachev.presentation.model.EmojiCalendarUIModel
import ru.agrachev.presentation.model.MainCalendarUIModel

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
