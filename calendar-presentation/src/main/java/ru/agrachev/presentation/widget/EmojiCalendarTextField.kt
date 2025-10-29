package ru.agrachev.calendarpresentation.widget

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import ru.agrachev.calendarpresentation.theme.EmojiCalendarTheme

@Composable
fun EmojiCalendarTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    hintText: String = "",
    textStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    validator: (CharSequence) -> Boolean = { true }
) {
    var fieldState by remember {
        mutableStateOf(EmojiCalendarTextFieldState.UNFOCUSED)
    }
    val indicatorColor by animateColorAsState(
        targetValue = when (fieldState) {
            EmojiCalendarTextFieldState.FOCUSED -> colors.focusedTextColor
            EmojiCalendarTextFieldState.UNFOCUSED -> colors.disabledTextColor
            EmojiCalendarTextFieldState.ERROR -> colors.errorIndicatorColor
        }
    )
    val isEmpty by remember {
        derivedStateOf {
            textFieldState.text.isEmpty()
        }
    }
    var isValid by remember {
        mutableStateOf(validator(textFieldState.text))
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    val textMeasurer = rememberTextMeasurer()
    val measuredText = remember {
        textMeasurer.measure(
            text = AnnotatedString(text = hintText),
            style = textStyle,
        )
    }

    LaunchedEffect(textFieldState) {
        snapshotFlow {
            textFieldState.text
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
        state = textFieldState,
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = textStyle,
        modifier = modifier then Modifier
            .drawBehind {
                drawLine(
                    color = indicatorColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = Stroke.DefaultMiter,
                )
                if (hintText.isNotEmpty() && isEmpty) {
                    drawText(
                        textLayoutResult = measuredText,
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
            },
    )
}

@Preview(showBackground = true, device = DEFAULT)
@Composable
fun EmojiCalendarTextFieldPreview() {
    EmojiCalendarTheme {
        EmojiCalendarTextField(
            hintText = "Hint",
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

private enum class EmojiCalendarTextFieldState {
    FOCUSED,
    UNFOCUSED,
    ERROR,
}

private fun choose(isValid: Boolean, isFocused: Boolean) = when {
    !isValid -> EmojiCalendarTextFieldState.ERROR
    isFocused -> EmojiCalendarTextFieldState.FOCUSED
    else -> EmojiCalendarTextFieldState.UNFOCUSED
}
