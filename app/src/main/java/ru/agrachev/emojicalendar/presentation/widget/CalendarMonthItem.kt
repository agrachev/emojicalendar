package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.agrachev.emojicalendar.domain.core.Constants.MAX_WEEKS_IN_MONTH
import ru.agrachev.emojicalendar.domain.core.Constants.WEEK_DAY_COUNT
import ru.agrachev.emojicalendar.domain.model.CalendarEvent
import ru.agrachev.emojicalendar.presentation.core.Constants.NOW_INDEX
import ru.agrachev.emojicalendar.presentation.core.LocalDateProvider
import ru.agrachev.emojicalendar.presentation.core.bounceLow
import ru.agrachev.emojicalendar.presentation.core.regularOffset
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.shimmer
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme

@Composable
fun CalendarMonthItem(
    mainCalendarDateModel: MainCalendarDateUIModel?,
    currentSelectedIndexProvider: () -> Int,
    modifier: Modifier = Modifier,
) {
    val isInCurrentMonth = mainCalendarDateModel?.let {
        it.date.month == LocalDateProvider.current.month.plus(
            currentSelectedIndexProvider().regularOffset.toLong()
        )
    } == true
    val alpha by animateFloatAsState(
        targetValue = if (isInCurrentMonth) 1f else 64f / 255f,
        animationSpec = tween(300),
    )
    Box(
        modifier = modifier then Modifier
            .clipToBounds()
            .graphicsLayer {
                this.alpha = alpha
            },
    ) {
        val textMeasurer = LocalTextMeasurer.current
        val density = LocalDensity.current
        val shimmerSize = remember {
            with(density) {
                textMeasurer.measure(
                    text = "000",
                    style = TextStyle(fontSize = 20.sp),
                ).size.let {
                    DpSize(
                        width = it.width.toDp(),
                        height = it.height.toDp(),
                    )
                }
            }
        }
        mainCalendarDateModel?.let { model ->
            Text(
                text = "${model.date.dayOfMonth}",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
            )
            AnimatedVisibility(
                visible = !model.emoji.isNullOrEmpty(),
                modifier = Modifier
                    .fillMaxSize(),
                enter = fadeIn() + slideInVertically(
                    animationSpec = bounceLow(),
                ) { it }
            ) {
                EmojiImage(
                    emoji = model.emoji.orEmpty(),
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
        } ?: Box(
            modifier = Modifier
                .size(shimmerSize)
                .padding(top = 4.dp)
                .align(Alignment.TopCenter)
                .shimmer(16.dp),
        )
    }
}


@Preview(showBackground = true, device = DEFAULT)
@Composable
fun CalendarItemPreview() {
    EmojiCalendarTheme {
        val now = LocalDateProvider.current
        val testModel = remember {
            MainCalendarDateUIModel(
                date = now,
                calendarEvents = listOf(
                    CalendarEvent(
                        emoji = "😭",
                        scheduledDate = now,
                    )
                ),
            )
        }
        val size = with(LocalConfiguration.current) {
            DpSize(
                width = (screenWidthDp / WEEK_DAY_COUNT).dp,
                height = (screenHeightDp / MAX_WEEKS_IN_MONTH).dp
            )
        }
        CompositionLocalProvider(LocalTextMeasurer provides rememberTextMeasurer()) {
            CalendarMonthItem(
                mainCalendarDateModel = testModel,
                currentSelectedIndexProvider = { NOW_INDEX },
                modifier = Modifier
                    .size(size),
            )
        }
    }
}

val LocalTextMeasurer = staticCompositionLocalOf<TextMeasurer> {
    error("CompositionLocal for TextMeasurer not present")
}
