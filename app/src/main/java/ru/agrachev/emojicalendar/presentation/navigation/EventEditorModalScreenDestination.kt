package ru.agrachev.emojicalendar.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.mapNotNull
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.model.emptyCalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.scope.modal.BottomModalScope
import ru.agrachev.emojicalendar.presentation.scope.modal.PendingRuleProvider
import ru.agrachev.emojicalendar.presentation.screen.EventEditorModalScreen

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
