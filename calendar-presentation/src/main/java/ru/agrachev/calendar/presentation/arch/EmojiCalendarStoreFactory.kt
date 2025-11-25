package ru.agrachev.calendar.presentation.arch

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.Dispatchers
import ru.agrachev.calendar.domain.usecase.CancelCalendarEventOccurrenceUseCase
import ru.agrachev.calendar.domain.usecase.CancelCalendarRuleUseCase
import ru.agrachev.calendar.domain.usecase.CancelSingleCalendarEventUseCase
import ru.agrachev.calendar.domain.usecase.FetchMonthDataUseCase
import ru.agrachev.calendar.domain.usecase.PushCalendarRuleUseCase
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import kotlin.coroutines.CoroutineContext

internal class EmojiCalendarStoreFactory(
    private val storeFactory: StoreFactory,
    private val initialState: EmojiCalendarUIModel,
    private val fetchMonthDataUseCase: FetchMonthDataUseCase,
    private val pushCalendarRuleUseCase: PushCalendarRuleUseCase,
    private val cancelCalendarRuleUseCase: CancelCalendarRuleUseCase,
    private val cancelSingleCalendarEventUseCase: CancelSingleCalendarEventUseCase,
    private val cancelCalendarEventOccurrenceUseCase: CancelCalendarEventOccurrenceUseCase,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    private val mainContext: CoroutineContext = Dispatchers.Main,
) {

    fun create(): EmojiCalendarStore = object : EmojiCalendarStore,
        Store<EmojiCalendarIntent, EmojiCalendarUIModel, EmojiCalendarLabel> by storeFactory.create(
            name = EmojiCalendarStore.NAME,
            initialState = initialState,
            executorFactory = {
                EmojiCalendarExecutor(
                    fetchMonthDataUseCase = fetchMonthDataUseCase,
                    pushCalendarRuleUseCase = pushCalendarRuleUseCase,
                    cancelCalendarRuleUseCase = cancelCalendarRuleUseCase,
                    cancelSingleCalendarEventUseCase = cancelSingleCalendarEventUseCase,
                    cancelCalendarEventOccurrenceUseCase = cancelCalendarEventOccurrenceUseCase,
                    defaultDispatcher = defaultDispatcher,
                    mainContext = mainContext,
                )
            },
            reducer = EmojiCalendarReducer(),
        ) {

    }
}
