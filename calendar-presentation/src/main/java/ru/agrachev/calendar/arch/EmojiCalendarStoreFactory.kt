package ru.agrachev.presentation.arch

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.Dispatchers
import ru.agrachev.calendardomain.usecase.FetchMonthDataUseCase
import ru.agrachev.calendardomain.usecase.PushCalendarRuleUseCase
import ru.agrachev.presentation.model.EmojiCalendarUIModel
import kotlin.coroutines.CoroutineContext

internal class EmojiCalendarStoreFactory(
    private val storeFactory: StoreFactory,
    private val initialState: EmojiCalendarUIModel,
    private val fetchMonthDataUseCase: FetchMonthDataUseCase,
    private val pushCalendarRuleUseCase: PushCalendarRuleUseCase,
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
                    defaultDispatcher = defaultDispatcher,
                    mainContext = mainContext,
                )
            },
            reducer = EmojiCalendarReducer(),
        ) {

    }
}
