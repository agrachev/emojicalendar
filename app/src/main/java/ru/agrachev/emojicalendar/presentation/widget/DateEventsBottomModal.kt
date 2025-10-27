package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarLabel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.navigation.EventEditorModalScreenDestination
import ru.agrachev.emojicalendar.presentation.scope.modal.BottomModalScope
import ru.agrachev.emojicalendar.presentation.scope.modal.DateEventsBottomModalScope
import ru.agrachev.emojicalendar.presentation.scope.modal.PendingRuleProvider
import ru.agrachev.emojicalendar.presentation.screen.EventBrowserModalScreen
import ru.agrachev.emojicalendar.presentation.viewmodel.CalendarMviStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEventsBottomModal(
    dateUIModel: MainCalendarDateUIModel,
    initialPendingRuleProvider: PendingRuleProvider,
    calendarStateHolder: CalendarMviStateHolder,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val modalScope: BottomModalScope = remember(calendarStateHolder) {
        DateEventsBottomModalScope(calendarStateHolder)
    }
    val navController = rememberNavController()
    val isKeyboardVisible by rememberKeyboardVisibilityState()
    ModalBottomSheet(
        onDismissRequest = modalScope::requestModalDismissal,
        sheetState = sheetState,
        dragHandle = {
            DateEventsBottomModalDragHandle()
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DateEventsDestination.EventBrowser.route,
            modifier = Modifier
                .then(
                    if (!isKeyboardVisible) Modifier.animateContentSize(
                        animationSpec = tween(250)
                    ) else Modifier
                ),
        ) {
            composable(
                route = DateEventsDestination.EventBrowser.route,
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(250),
                    )
                },
                popEnterTransition = { EnterTransition.None },
            ) {
                EventBrowserModalScreen(
                    dateUIModel = dateUIModel,
                    onEventItemClicked = modalScope::handleCalendarEventSelected,
                    modifier = modifier,
                )
            }
            composable(
                route = DateEventsDestination.EventEditor.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(250),
                    )
                },
                popExitTransition = { ExitTransition.None },
            ) {
                modalScope.EventEditorModalScreenDestination(
                    dateModel = dateUIModel,
                    initialPendingRuleProvider = initialPendingRuleProvider,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.85f),
                )
            }
        }
    }
    LaunchedEffect(navController) {
        calendarStateHolder.labels
            .collect {
                when (it) {
                    is EmojiCalendarLabel.NavigateToItem -> {
                        navController.navigate(DateEventsDestination.EventEditor.route)
                    }

                    is EmojiCalendarLabel.CalendarRuleUpdateFailed -> {

                    }

                    EmojiCalendarLabel.CalendarRuleUpdateSuccess -> {
                        sheetState.hide()
                        modalScope.reportCalendarRulePushSuccess()
                    }
                }
            }
    }
}

sealed class DateEventsDestination(
    val route: String,
) {
    object EventBrowser : DateEventsDestination(route = "EventBrowser")
    object EventEditor : DateEventsDestination(route = "EventEditor")
}

@Composable
fun rememberKeyboardVisibilityState(): State<Boolean> {
    val bottomInsetAnimator = remember {
        Animatable(0f)
    }
    val isKeyboardVisibleOnScreenState by remember {
        derivedStateOf {
            bottomInsetAnimator.value > 0f
        }
    }
    val bottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisibleOnScreen = bottom > 0
    LaunchedEffect(isKeyboardVisibleOnScreen) {
        bottomInsetAnimator.animateTo(bottom.toFloat())
    }
    return rememberUpdatedState(isKeyboardVisibleOnScreen || isKeyboardVisibleOnScreenState)
}
