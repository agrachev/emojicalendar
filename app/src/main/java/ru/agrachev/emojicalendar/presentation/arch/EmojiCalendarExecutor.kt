package ru.agrachev.emojicalendar.presentation.arch

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.agrachev.emojicalendar.domain.usecase.FetchMonthDataUseCase
import ru.agrachev.emojicalendar.domain.usecase.PushCalendarRuleUseCase
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent.DismissEventsBrowserModal
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent.LoadDayModelsForMonth
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent.NavigateToItem
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent.OpenEventsBrowserModal
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarLabel.*
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarMessage.DayModelsLoaded
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarMessage.EventsBrowserModalDismissed
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarMessage.EventsBrowserModalRequested
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarMessage.PendingRuleUpdated
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarMessage.PendingRuleUpdated2
import ru.agrachev.emojicalendar.presentation.core.value
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.model.toDomainModel
import ru.agrachev.emojicalendar.presentation.model.toPresentationModel
import kotlin.coroutines.CoroutineContext

internal class EmojiCalendarExecutor(
    private val fetchMonthDataUseCase: FetchMonthDataUseCase,
    private val pushCalendarRuleUseCase: PushCalendarRuleUseCase,
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<EmojiCalendarIntent, Nothing, EmojiCalendarUIModel, EmojiCalendarMessage, EmojiCalendarLabel>(
    mainContext,
) {

    override fun executeIntent(intent: EmojiCalendarIntent) {
        when (intent) {
            is LoadDayModelsForMonth -> {
                scope.launch {
                    val result = withContext(defaultDispatcher) {
                        fetchMonthDataUseCase(
                            offsetFromCurrentMonth = intent.monthOffsetFromNow.value,
                        ).map {
                            it.toPresentationModel()
                        }
                    }
                    dispatch(
                        DayModelsLoaded(
                            key = intent.monthOffsetFromNow,
                            models = result,
                        )
                    )
                }
            }

            is EmojiCalendarIntent.PushCalendarRule -> {
                scope.launch {
                    try {
                        withContext(defaultDispatcher) {
                            pushCalendarRuleUseCase(
                                calendarRule = intent.calendarRuleUIModel.toDomainModel(),
                            )
                        }
                        publish(
                            CalendarRuleUpdateSuccess
                        )
                    } catch (e: Exception) {
                        ensureActive()
                        publish(
                            CalendarRuleUpdateFailed(e)
                        )
                    }
                }
            }

            is OpenEventsBrowserModal -> {
                dispatch(
                    EventsBrowserModalRequested(
                        dateUiModel = intent.model,
                    )
                )
            }

            is DismissEventsBrowserModal -> dispatch(
                EventsBrowserModalDismissed
            )

            is NavigateToItem -> {
                dispatch(
                    PendingRuleUpdated(
                        itemIndex = intent.itemIndex,
                    )
                )
                publish(
                    NavigateToItem(
                        index = intent.itemIndex,
                    )
                )
            }

            is EmojiCalendarIntent.UpdatePendingRule -> dispatch(
                PendingRuleUpdated2(
                    pendingRuleUpdater = intent.pendingRuleUpdater,
                )
            )

            EmojiCalendarIntent.RequestCalendarUpdate -> {
                dispatch(
                    EmojiCalendarMessage.CalendarUpdateRequested
                )
            }
        }
    }
}
