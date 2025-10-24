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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarLabel
import ru.agrachev.emojicalendar.presentation.model.AAA
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.navigation.EventEditorModalScreenDestination
import ru.agrachev.emojicalendar.presentation.screen.EventBrowserModalScreen
import ru.agrachev.emojicalendar.presentation.screen.ItemIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEventsBottomModal(
    dateUIModel: MainCalendarDateUIModel,
    pendingRuleStateProvider: @Composable () -> State<CalendarRuleUIModel>,
    labelProvider: () -> Flow<EmojiCalendarLabel>,
    pendingRuleUpdater: (updater: AAA) -> Unit,
    onCalendarRulePushRequest: (CalendarRuleUIModel) -> Unit,
    onCalendarRulePushSuccess: () -> Unit,
    onEventItemClicked: (item: ItemIndex) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val navController = rememberNavController()
    val kb by keyboardAsState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            DateEventsBottomModalDragHandle(0.dp)
        }
        //modifier = modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = DateEventsDestination.EventBrowser.route,
            modifier = Modifier
                .then(
                    if (!kb) Modifier.animateContentSize(
                        animationSpec = tween(250)
                    ) else Modifier
                )
            //)
            /*.animateContentSize(
                animationSpec = tween(250),
            ),*/
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
                    onEventItemClicked = onEventItemClicked,
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
                //requireNotNull(eventsBrowserUIModel.pendingRule)
                EventEditorModalScreenDestination(
                    pendingRuleStateProvider = pendingRuleStateProvider,
                    onCalendarRulePushRequest = onCalendarRulePushRequest,
                    pendingRuleUpdater = pendingRuleUpdater,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.8f),
                    /*.height(600.dp.apply {
                        println("DDDDDDD $this")
                    }),*/
                )
                /*EventEditorModalScreen(
                    pendingRule = eventsBrowserUIModel.pendingRule,
                    modifier = modifier,
                )*/
            }
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow {
            kb
        }.collect {
            println("FFFFFFFFFFFFF $it")
        }
    }
    LaunchedEffect(navController) {
        labelProvider()
            .collect {
                when (it) {
                    is EmojiCalendarLabel.NavigateToItem -> {
                        navController.navigate(DateEventsDestination.EventEditor.route)
                    }

                    is EmojiCalendarLabel.CalendarRuleUpdateFailed -> {

                    }

                    EmojiCalendarLabel.CalendarRuleUpdateSuccess -> {
                        sheetState.hide()
                        onCalendarRulePushSuccess()
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
fun keyboardAsState(): State<Boolean> {
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
