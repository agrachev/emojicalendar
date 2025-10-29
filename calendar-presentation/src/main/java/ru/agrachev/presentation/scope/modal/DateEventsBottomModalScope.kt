package ru.agrachev.calendarpresentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendarpresentation.arch.EmojiCalendarIntent
import ru.agrachev.calendarpresentation.model.CalendarRuleUILayout
import ru.agrachev.calendarpresentation.model.CalendarRuleUIModel
import ru.agrachev.calendarpresentation.model.EmojiCalendarUIModel
import ru.agrachev.calendarpresentation.viewmodel.CalendarMviStateHolder
import ru.agrachev.calendarpresentation.widget.modal.ItemIndex

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
