package ru.agrachev.emojicalendar.presentation.scope.modal

import kotlinx.coroutines.flow.Flow
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUILayout
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.screen.ItemIndex

internal typealias PendingRuleProvider = () -> CalendarRuleUIModel?

internal interface BottomModalScope {
    val uiStates: Flow<EmojiCalendarUIModel>
    fun requestPendingRuleUpdate(pendingRuleUpdater: CalendarRuleUILayout)
    fun requestPushCalendarRule(calendarRuleUIModel: CalendarRuleUIModel)
    fun reportCalendarRulePushSuccess()
    fun handleCalendarEventSelected(itemIndex: ItemIndex)
    fun requestModalDismissal()
}
