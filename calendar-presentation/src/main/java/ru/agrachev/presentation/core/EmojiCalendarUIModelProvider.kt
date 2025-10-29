package ru.agrachev.calendarpresentation.core

import ru.agrachev.calendardomain.repository.DayModelStorage
import ru.agrachev.calendarpresentation.model.EmojiCalendarUIModel
import ru.agrachev.calendarpresentation.model.MainCalendarUIModel

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
