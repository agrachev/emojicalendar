package ru.agrachev.presentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.presentation.arch.EmojiCalendarIntent
import ru.agrachev.presentation.model.CalendarRuleUILayout
import ru.agrachev.presentation.model.CalendarRuleUIModel
import ru.agrachev.presentation.model.EmojiCalendarUIModel
import ru.agrachev.presentation.viewmodel.CalendarMviStateHolder
import ru.agrachev.presentation.widget.modal.ItemIndex

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
