package ru.agrachev.emojicalendar.presentation.widget

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import ru.agrachev.emojicalendar.R
import ru.agrachev.emojicalendar.domain.core.fraction
import ru.agrachev.emojicalendar.domain.model.RecurrenceRule
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import kotlin.math.abs
import kotlin.math.ceil

const val SCALE_THRESHOLD = .8f

@Composable
fun RepeatButton(
    modifier: Modifier = Modifier,
    recurrenceRuleButtonState: RecurrenceRuleButtonState =
        rememberRecurrenceRuleButtonState(),
    onRepeatModeChanged: (mode: RecurrenceRule) -> Unit = {

    },
) {
    var recurrenceRule by recurrenceRuleButtonState::recurrenceRule
    val anim = remember {
        Animatable(0f)
    }
    val scope = rememberCoroutineScope()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier then Modifier
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    scope.launch {
                        anim.animateTo(
                            targetValue = ceil(anim.value) + 1f,
                            animationSpec = tween(
                                durationMillis = 300,
                            ),
                        )
                    }
                    recurrenceRuleButtonState.toggleNext(
                        onNextCallback = onRepeatModeChanged,
                    )
                }
            ),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_repeat),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationX = 180f
                    rotationZ = -180f * anim.value.fraction
                    scaleStep(anim.value.fraction).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                },
        )
        AnimatedContent(
            targetState = recurrenceRule,
            transitionSpec = {
                scaleIn() togetherWith scaleOut()
            }
        ) { rule ->
            Image(
                painter = painterResource(rule.iconResource),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(.55f),
            )
        }
    }
}

@Preview
@Composable
fun RepeatButtonPreview() {
    EmojiCalendarTheme {
        RepeatButton(
            modifier = Modifier
                .size(100.dp),
        )
    }
}

@Composable
fun rememberRecurrenceRuleButtonState(
    initial: RecurrenceRule = RecurrenceRule.DEFAULT,
) = remember {
    RecurrenceRuleButtonState(initial)
}

@Immutable
class RecurrenceRuleButtonState(
    initial: RecurrenceRule,
) {
    var recurrenceRule by mutableStateOf(initial)
        internal set

    internal inline fun toggleNext(onNextCallback: (RecurrenceRule) -> Unit) =
        recurrenceRule.next().let {
            recurrenceRule = it
            onNextCallback(it)
        }
}

private fun scaleStep(
    value: Float,
    @FloatRange(from = 0.0, to = 1.0) threshold: Float = SCALE_THRESHOLD,
) = (1f - threshold).let { delta ->
    threshold + abs(lerp(-delta, delta, value))
}

private fun RecurrenceRule.next() = with(RecurrenceRule.entries) {
    this[(this@next.ordinal + 1) % size]
}

private inline val RecurrenceRule.iconResource
    @DrawableRes get() = when (this) {
        RecurrenceRule.NONE -> R.drawable.ic_recurrence_none
        RecurrenceRule.PERIOD -> R.drawable.ic_recurrence_period
        RecurrenceRule.WEEK -> R.drawable.ic_recurrence_week
        RecurrenceRule.MONTH -> R.drawable.ic_recurrence_month
        RecurrenceRule.YEAR -> R.drawable.ic_recurrence_year
    }
