package ru.agrachev.emojicalendar.presentation.navigation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.agrachev.emojicalendar.presentation.model.AAA
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.screen.EventEditorModalScreen

@Composable
fun EventEditorModalScreenDestination(
    pendingRuleStateProvider: @Composable () -> State<CalendarRuleUIModel>,
    onCalendarRulePushRequest: (CalendarRuleUIModel) -> Unit,
    pendingRuleUpdater: (updater: AAA) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pendingRule by pendingRuleStateProvider()
    EventEditorModalScreen(
        pendingRule = pendingRule,
        pendingRuleUpdater = pendingRuleUpdater,
        onCalendarRulePushRequest = onCalendarRulePushRequest,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.85f),
    )
}
