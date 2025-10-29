package ru.agrachev.calendar.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.mapNotNull
import ru.agrachev.calendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.calendar.presentation.model.emptyCalendarRuleUIModel
import ru.agrachev.calendar.presentation.scope.modal.BottomModalScope
import ru.agrachev.calendar.presentation.scope.modal.PendingRuleProvider
import ru.agrachev.calendar.presentation.widget.modal.EventEditorModalScreen

@Composable
internal fun BottomModalScope.EventEditorModalScreenDestination(
    dateModel: MainCalendarDateUIModel,
    initialPendingRuleProvider: PendingRuleProvider,
    modifier: Modifier = Modifier,
) {
    val pendingRule by uiStates
        .mapNotNull {
            it.eventsBrowserUIModel?.pendingRule
        }.collectAsStateWithLifecycle(
            initialValue = initialPendingRuleProvider() ?: emptyCalendarRuleUIModel(dateModel.date),
        )
    EventEditorModalScreen(
        pendingRuleProvider = { pendingRule },
        pendingRuleUpdater = ::requestPendingRuleUpdate,
        onCalendarRulePushRequest = ::requestPushCalendarRule,
        modifier = modifier,
    )
}
