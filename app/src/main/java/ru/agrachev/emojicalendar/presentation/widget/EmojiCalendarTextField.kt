package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme

@Composable
fun EmojiCalendarTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    hintText: String = "",
    hintTextStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    validator: (CharSequence) -> Boolean = { true }
) {
    var fieldState by remember {
        mutableStateOf(EmojiCalendarTextFieldState.UNFOCUSED)
    }
    val indicatorColor by animateColorAsState(
        targetValue = when (fieldState) {
            EmojiCalendarTextFieldState.FOCUSED -> colors.focusedIndicatorColor
            EmojiCalendarTextFieldState.UNFOCUSED -> colors.unfocusedIndicatorColor
            EmojiCalendarTextFieldState.ERROR -> colors.errorIndicatorColor
        }
    )
    val isEmpty by remember {
        derivedStateOf {
            state.text.isEmpty()
        }
    }
    var isValid by remember {
        mutableStateOf(validator(state.text))
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    val textMeasurer = rememberTextMeasurer()
    val mr = remember {
        textMeasurer.measure(
            text = AnnotatedString(text = hintText),
            style = hintTextStyle,
        )
    }

    LaunchedEffect(state) {
        snapshotFlow {
            state.text
        }.collect {
            fieldState = choose(
                isValid = validator(it).apply {
                    isValid = this
                },
                isFocused = isFocused,
            )
        }
    }

    BasicTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = textStyle,
        modifier = modifier.then(
            Modifier
                .drawBehind {
                    drawLine(
                        color = indicatorColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = Stroke.DefaultMiter,
                    )
                    if (hintText.isNotEmpty() && isEmpty) {
                        drawText(
                            textLayoutResult = mr,
                            color = if (isValid) {
                                colors.disabledTextColor
                            } else {
                                colors.errorIndicatorColor
                            },
                        )
                    }
                }
                .onFocusChanged { focusState ->
                    fieldState = choose(
                        isValid = isValid,
                        isFocused = focusState.isFocused.apply {
                            isFocused = this
                        })
                }
        ),
    )
}

@Preview(showBackground = true, device = DEFAULT)
@Composable
fun EmojiCalendarTextFieldPreview() {
    EmojiCalendarTheme {
        EmojiCalendarTextField(
            state = rememberTextFieldState(
                initialText = "",
            ),
            hintText = "Hint",
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

enum class EmojiCalendarTextFieldState {
    FOCUSED,
    UNFOCUSED,
    ERROR,
}

private fun choose(isValid: Boolean, isFocused: Boolean) = when {
    !isValid -> EmojiCalendarTextFieldState.ERROR
    isFocused -> EmojiCalendarTextFieldState.FOCUSED
    else -> EmojiCalendarTextFieldState.UNFOCUSED
}
