package ru.agrachev.calendarpresentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendarpresentation.model.CalendarRuleUILayout
import ru.agrachev.calendarpresentation.model.CalendarRuleUIModel
import ru.agrachev.calendarpresentation.model.EmojiCalendarUIModel
import ru.agrachev.calendarpresentation.widget.modal.ItemIndex

internal typealias PendingRuleProvider = () -> CalendarRuleUIModel?

internal interface BottomModalScope {
    val uiStates: Flow<EmojiCalendarUIModel>
    fun requestPendingRuleUpdate(pendingRuleUpdater: CalendarRuleUILayout)
    fun requestPushCalendarRule(calendarRuleUIModel: CalendarRuleUIModel)
    fun reportCalendarRulePushSuccess()
    fun handleCalendarEventSelected(itemIndex: ItemIndex)
    fun requestModalDismissal()
}
