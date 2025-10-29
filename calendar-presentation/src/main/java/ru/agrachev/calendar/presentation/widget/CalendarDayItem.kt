package ru.agrachev.calendar.presentation.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.agrachev.calendar.domain.core.Constants.WEEK_DAY_COUNT
import ru.agrachev.calendar.presentation.core.bounceLow
import ru.agrachev.calendar.presentation.model.CalendarEventUIModel
import ru.agrachev.calendar.presentation.toIntPx

@Composable
inline fun CalendarDayItem(
    calendarEvent: CalendarEventUIModel?,
    contentWidth: Dp,
    dayOfWeekResolver: () -> String,
    isItemActiveCallback: () -> Boolean,
    isItemSelectedCallback: () -> Boolean,
    crossinline onItemClicked: () -> Unit,
) {
    val isActive = isItemActiveCallback()
    val isSelected = isItemSelectedCallback()
    val borderWidth by animateFloatAsState(
        targetValue = (if (isSelected) 3.dp else 2.dp).toIntPx().toFloat(),
        animationSpec = bounceLow(),
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else TextFieldDefaults.colors().disabledTextColor,
        animationSpec = bounceLow(),
    )
    val emoji by rememberUpdatedState(calendarEvent?.emoji.orEmpty())
    Box(
        modifier = Modifier
            .wrapContentSize(),
    ) {
        Box(
            modifier = Modifier
                .width(width = contentWidth / WEEK_DAY_COUNT)
                .aspectRatio(.5f)
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    alpha = if (isActive) 1f else .4f
                }
                .clip(RoundedCornerShape(16f))
                .drawWithCache {
                    val borderStroke = Stroke(borderWidth)
                    onDrawWithContent {
                        drawContent()
                        drawRoundRect(
                            color = borderColor,
                            style = borderStroke,
                            cornerRadius = CornerRadius(16f)
                        )
                    }
                }
                .clickable(enabled = isActive) {
                    onItemClicked()
                }
        ) {
            Text(
                text = dayOfWeekResolver(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
            )
            AnimatedContent(
                targetState = emoji,
                label = "emoji popup",
                transitionSpec = {
                    (slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = bounceLow(),
                    ) togetherWith
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = bounceLow(),
                            ))
                        .using(
                            SizeTransform(clip = false)
                        )
                }
            ) {
                EmojiImage(
                    emoji = it,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
        }
        Canvas(
            modifier = Modifier
                .size(4.dp)
                .offset(y = 24.dp)
                .align(Alignment.BottomStart)
        ) {
            drawCircle(
                Color.Black,
                size.width / 2,
                center = Offset.Zero,
            )
        }
    }
}
