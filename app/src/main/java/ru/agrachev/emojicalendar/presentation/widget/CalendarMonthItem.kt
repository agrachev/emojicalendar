package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.shimmer
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import java.time.LocalDate

@Composable
fun CalendarMonthItem(
    mainCalendarDateModel: MainCalendarDateUIModel?,
    state: Int,
    modifier: Modifier = Modifier,
) {
    /*val a by remember {
        derivedStateOf {
            state.value == index
        }
    }*/
    val m = remember(Unit) {
        LocalDate.now().month
    }
    val aa = mainCalendarDateModel?.let {
        mainCalendarDateModel.date.month == m.plus(
            state - Int.MAX_VALUE / 2L
        )
    } ?: false
    val alpha by animateFloatAsState(
        targetValue = if (aa) 1f else 64f / 255f,
        animationSpec = tween(300),
    )
    Box(
        modifier = modifier
            .then(
                Modifier
                    .clipToBounds()
                    .graphicsLayer {
                        this.alpha = alpha
                    },
            ),
    ) {
        val textMeasurer = LocalTextMeasurer.current
        val density = LocalDensity.current
        val s = remember {
            with(density) {
                textMeasurer.measure(
                    "000",
                    TextStyle(fontSize = 20.sp)
                ).size.let { DpSize(it.width.toDp(), it.height.toDp()) }
            }
        }
        if (mainCalendarDateModel != null) {
            Text(
                text = "${mainCalendarDateModel.date.dayOfMonth}",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
            )
            AnimatedVisibility(
                visible = !mainCalendarDateModel.emoji.isNullOrEmpty(),
                modifier = Modifier
                    .fillMaxSize(),
                enter = fadeIn() + slideInVertically(
                    animationSpec = spring(
                        dampingRatio = .7f,
                        stiffness = Spring.StiffnessMedium
                    )
                ) { it }
            ) {
                EmojiImage(
                    emoji = mainCalendarDateModel.emoji.orEmpty(),
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(s)
                    .padding(top = 4.dp)
                    .align(Alignment.TopCenter)
                    .shimmer(16.dp)
            )
        }
    }
}


@Preview(showBackground = true, device = DEFAULT)
@Composable
fun CalendarItemPreview() {
    EmojiCalendarTheme {
        val state by remember {
            mutableIntStateOf(0)
        }
        CalendarMonthItem(
            mainCalendarDateModel = testModel,
            state = state,
            modifier = Modifier.size(
                with(LocalDensity.current) {
                    141.toDp()
                }, with(LocalDensity.current) {
                    320.toDp()
                }),
        )
    }
}

val testModel = MainCalendarDateUIModel(
    date = LocalDate.now(),
    calendarEvents = emptyList(),
)
val LocalTextMeasurer = staticCompositionLocalOf<TextMeasurer> {
    error("CompositionLocal for TextMeasurer not present")
}
