package ru.agrachev.calendar.presentation.arch

import com.arkivanov.mvikotlin.core.store.Store
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel

interface EmojiCalendarStore :
    Store<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    companion object {
        internal const val NAME = "EmojiCalendarStore"
    }
}
