package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText

@Composable
fun EmojiImage(
    emoji: String,
    modifier: Modifier = Modifier,
    scaleFactor: Float = 2f,
    topLeftXOffsetScale: Float = .5f,
    topLeftYOffsetScale: Float = .35f,
) {
    val textMeasurer = LocalTextMeasurer.current
    Canvas(
        modifier = modifier.then(
            Modifier
                .drawWithCache {
                    val measuredText =
                        textMeasurer.measure(
                            text = AnnotatedString(text = emoji),
                            style = TextStyle(fontSize = size.width.toSp() * scaleFactor),
                        )
                    onDrawBehind {
                        drawText(
                            textLayoutResult = measuredText,
                            topLeft = Offset(
                                x = -(measuredText.size.width - size.width) * topLeftXOffsetScale,
                                y = measuredText.size.height * topLeftYOffsetScale,
                            ),
                        )
                    }
                }
        )
    ) {

    }
}
