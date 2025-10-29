package ru.agrachev.calendar.presentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.calendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.widget.modal.ItemIndex

internal typealias PendingRuleProvider = () -> CalendarRuleUIModel?

internal interface BottomModalScope {
    val uiStates: Flow<EmojiCalendarUIModel>
    fun requestPendingRuleUpdate(pendingRuleUpdater: CalendarRuleUILayout)
    fun requestPushCalendarRule(calendarRuleUIModel: CalendarRuleUIModel)
    fun reportCalendarRulePushSuccess()
    fun handleCalendarEventSelected(itemIndex: ItemIndex)
    fun requestModalDismissal()
}
