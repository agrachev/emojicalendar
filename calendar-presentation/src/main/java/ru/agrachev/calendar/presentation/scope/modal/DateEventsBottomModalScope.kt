package ru.agrachev.calendar.presentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendar.presentation.arch.EmojiCalendarIntent
import ru.agrachev.calendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.calendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.viewmodel.CalendarMviStateHolder
import ru.agrachev.calendar.presentation.widget.modal.ItemIndex

internal class DateEventsBottomModalScope(
    private val viewModel: CalendarMviStateHolder,
) : BottomModalScope {

    override val uiStates: Flow<EmojiCalendarUIModel>
        get() = viewModel.uiState

    override fun requestPendingRuleUpdate(pendingRuleUpdater: CalendarRuleUILayout) =
        viewModel.accept(
            EmojiCalendarIntent.UpdatePendingRule(
                pendingRuleUpdater
            )
        )

    override fun requestPushCalendarRule(calendarRuleUIModel: CalendarRuleUIModel) =
        viewModel.accept(
            EmojiCalendarIntent.PushCalendarRule(
                calendarRuleUIModel
            )
        )

    override fun reportCalendarRulePushSuccess() =
        viewModel.accept(
            EmojiCalendarIntent.RequestCalendarUpdate
        )

    override fun handleCalendarEventSelected(itemIndex: ItemIndex) =
        viewModel.accept(
            EmojiCalendarIntent.NavigateToItem(
                itemIndex
            )
        )

    override fun requestModalDismissal() = viewModel.accept(
        EmojiCalendarIntent.DismissEventsBrowserModal
    )
}
