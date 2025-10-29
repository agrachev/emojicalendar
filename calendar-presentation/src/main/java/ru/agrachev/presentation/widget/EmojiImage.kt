package ru.agrachev.calendarpresentation.widget

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText

const val EMOJI_IMAGE_SCALE_FACTOR_DEFAULT = 2f
const val EMOJI_IMAGE_TOP_LEFT_X_OFFSET_SCALE = .5f
const val EMOJI_IMAGE_TOP_LEFT_Y_OFFSET_SCALE = .35f


@Composable
fun EmojiImage(
    emoji: String,
    modifier: Modifier = Modifier,
    scaleFactor: Float = EMOJI_IMAGE_SCALE_FACTOR_DEFAULT,
    topLeftXOffsetScale: Float = EMOJI_IMAGE_TOP_LEFT_X_OFFSET_SCALE,
    topLeftYOffsetScale: Float = EMOJI_IMAGE_TOP_LEFT_Y_OFFSET_SCALE,
) {
    val textMeasurer = LocalTextMeasurer.current
    Canvas(
        modifier = modifier then Modifier
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
    ) {

    }
}
