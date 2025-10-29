package ru.agrachev.presentation.arch

import com.arkivanov.mvikotlin.core.store.Reducer
import ru.agrachev.calendardomain.model.Id
import ru.agrachev.presentation.model.EmojiCalendarUIModel
import ru.agrachev.presentation.model.EventsBrowserUIModel
import ru.agrachev.presentation.model.emptyCalendarRuleUIModel
import ru.agrachev.presentation.model.toPresentationModel

internal class EmojiCalendarReducer : Reducer<EmojiCalendarUIModel, EmojiCalendarMessage> {

    override fun EmojiCalendarUIModel.reduce(msg: EmojiCalendarMessage): EmojiCalendarUIModel =
        when (msg) {
            is EmojiCalendarMessage.DayModelsLoaded -> copy(
                mainCalendarUIModel = mainCalendarUIModel.copy(
                    mainCalendarDateModelStorage = mainCalendarUIModel.mainCalendarDateModelStorage.apply {
                        put(msg.key, msg.models)
                    },
                    monthOffsetKey = msg.key.value,
                )
            )

            is EmojiCalendarMessage.EventsBrowserModalRequested -> copy(
                eventsBrowserUIModel = EventsBrowserUIModel(
                    dateModel = msg.dateUiModel,
                )
            )

            EmojiCalendarMessage.EventsBrowserModalDismissed -> copy(
                eventsBrowserUIModel = null,
            )

            is EmojiCalendarMessage.PendingRuleUpdated -> copy(
                eventsBrowserUIModel = eventsBrowserUIModel?.copy(
                    pendingRule = with(eventsBrowserUIModel.scheduledEvents) {
                        this.getOrNull(msg.itemIndex.index)?.rule?.toPresentationModel()
                            ?: emptyCalendarRuleUIModel(eventsBrowserUIModel.dateModel.date)
                    }
                )
            )

            is EmojiCalendarMessage.PendingRuleUpdated2 -> copy(
                eventsBrowserUIModel = eventsBrowserUIModel?.copy(
                    pendingRule = with(msg.pendingRuleUpdater) {
                        eventsBrowserUIModel.pendingRule?.copy(
                            title = this.title,
                            dateRangeOffsetIndexes = this.dateRangeOffsetIndexes,
                            calendarEventsUiModels = this.calendarEventsUiModels,
                            recurrenceRule = this.recurrenceRule,
                        )
                    }
                )
            )

            EmojiCalendarMessage.CalendarUpdateRequested -> copy(
                mainCalendarUIModel = mainCalendarUIModel.copy(
                    calendarRefreshRequestToken = Id.UNIQUE,
                )
            )
        }
}
