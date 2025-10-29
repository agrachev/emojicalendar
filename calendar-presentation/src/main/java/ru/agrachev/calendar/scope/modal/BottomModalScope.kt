package ru.agrachev.presentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.presentation.model.CalendarRuleUILayout
import ru.agrachev.presentation.model.CalendarRuleUIModel
import ru.agrachev.presentation.model.EmojiCalendarUIModel
import ru.agrachev.presentation.widget.modal.ItemIndex

internal typealias PendingRuleProvider = () -> CalendarRuleUIModel?

internal interface BottomModalScope {
    val uiStates: Flow<EmojiCalendarUIModel>
    fun requestPendingRuleUpdate(pendingRuleUpdater: CalendarRuleUILayout)
    fun requestPushCalendarRule(calendarRuleUIModel: CalendarRuleUIModel)
    fun reportCalendarRulePushSuccess()
    fun handleCalendarEventSelected(itemIndex: ItemIndex)
    fun requestModalDismissal()
}
