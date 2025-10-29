package ru.agrachev.calendar.presentation.arch

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.agrachev.calendar.domain.usecase.FetchMonthDataUseCase
import ru.agrachev.calendar.domain.usecase.PushCalendarRuleUseCase
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.model.toDomainModel
import ru.agrachev.calendar.presentation.model.toPresentationModel
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
            is EmojiCalendarIntent.LoadDayModelsForMonth -> {
                scope.launch {
                    val result = withContext(defaultDispatcher) {
                        fetchMonthDataUseCase(
                            offsetFromCurrentMonth = intent.monthOffsetFromNow.value,
                        ).map {
                            it.toPresentationModel()
                        }
                    }
                    dispatch(
                        EmojiCalendarMessage.DayModelsLoaded(
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
                            EmojiCalendarLabel.CalendarRuleUpdateSuccess
                        )
                    } catch (e: Exception) {
                        ensureActive()
                        publish(
                            EmojiCalendarLabel.CalendarRuleUpdateFailed(e)
                        )
                    }
                }
            }

            is EmojiCalendarIntent.OpenEventsBrowserModal -> {
                dispatch(
                    EmojiCalendarMessage.EventsBrowserModalRequested(
                        dateUiModel = intent.model,
                    )
                )
            }

            is EmojiCalendarIntent.DismissEventsBrowserModal -> dispatch(
                EmojiCalendarMessage.EventsBrowserModalDismissed
            )

            is EmojiCalendarIntent.NavigateToItem -> {
                dispatch(
                    EmojiCalendarMessage.PendingRuleUpdated(
                        itemIndex = intent.itemIndex,
                    )
                )
                publish(
                    EmojiCalendarLabel.NavigateToItem(
                        index = intent.itemIndex,
                    )
                )
            }

            is EmojiCalendarIntent.UpdatePendingRule -> dispatch(
                EmojiCalendarMessage.PendingRuleUpdated2(
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
