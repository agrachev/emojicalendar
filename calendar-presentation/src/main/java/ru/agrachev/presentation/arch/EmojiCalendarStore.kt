package ru.agrachev.calendarpresentation.arch

import com.arkivanov.mvikotlin.core.store.Store
import ru.agrachev.calendarpresentation.model.EmojiCalendarUIModel

interface EmojiCalendarStore :
    Store<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    companion object {
        internal const val NAME = "EmojiCalendarStore"
    }
}
