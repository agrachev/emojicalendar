package ru.agrachev.presentation.arch

import com.arkivanov.mvikotlin.core.store.Store
import ru.agrachev.presentation.model.EmojiCalendarUIModel

interface EmojiCalendarStore :
    Store<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> {

    companion object {
        internal const val NAME = "EmojiCalendarStore"
    }
}
