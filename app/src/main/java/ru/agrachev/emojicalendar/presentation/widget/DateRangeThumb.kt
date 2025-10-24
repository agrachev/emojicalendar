package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.lerp
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateRangeThumb(
    modifier: Modifier = Modifier,
    thumbState: DateRangeThumbState = rememberDateRangeThumbState(),
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val scope = this
        val density = LocalDensity.current
        val textMeasurer = LocalTextMeasurer.current
        val layoutDirection = LocalLayoutDirection.current
        val contentWidth by animateDpAsState(
            targetValue = scope.minHeight * (if (thumbState.expanded) thumbState.expandRatio else 1f),
        )
        val contentWidthPx by remember {
            derivedStateOf {
                with(density) {
                    contentWidth.roundToPx().toFloat()
                }
            }
        }
        Spacer(
            modifier = modifier.then(
                Modifier
                    .offset {
                        IntOffset(
                            x = -(contentWidthPx.toInt() - with(density) { 16.dp.roundToPx() }) / 2,
                            y = 0,
                        )
                    }
                    .drawWithCache {
                        val measuredText = textMeasurer.measure(
                            text = thumbState.dateLabel,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = with(density) {
                                    (scope.minHeight * .7f).toSp()
                                },
                            )
                        )
                        onDrawBehind {
                            drawRoundRect(
                                color = Color.Red,
                                size = size.copy(
                                    width = contentWidthPx,
                                ),
                                cornerRadius = CornerRadius(x = size.height / 2f),
                            )
                            val ratio = contentWidth / scope.minWidth
                            if (ratio > 1f) {
                                drawText(
                                    textLayoutResult = measuredText,
                                    alpha = lerp(0f, 1f, ratio / thumbState.expandRatio),
                                    topLeft = Alignment.Center.align(
                                        size = measuredText.size,
                                        space = size.copy(
                                            width = contentWidthPx,
                                        ).toIntSize(),
                                        layoutDirection = layoutDirection,
                                    ).toOffset(),
                                )
                            }
                        }
                    }
            ),
        )
    }
}

@Composable
fun rememberDateRangeThumbState(
    initiallyExpanded: Boolean = false,
    expandRatio: Float = 4f,
) = remember {
    val state: DateRangeThumbState by inject(DateRangeThumbState::class.java) {
        parametersOf(
            initiallyExpanded,
            expandRatio,
        )
    }
    state
}

@Immutable
class DateRangeThumbState(
    initiallyExpanded: Boolean = false,
    val expandRatio: Float = 4f,
    private val formatter: DateTimeFormatter?
) {

    var expanded
        set(value) {
            expandedState = value && expandRatio > 1f
        }
        get() = expandedState

    val dateLabel by derivedStateOf {
        formatter?.let { dateState.format(it) } ?: ""
    }

    internal var dateState by mutableStateOf(LocalDate.now())
    private var expandedState by mutableStateOf(initiallyExpanded)
}
