package ru.agrachev.emojicalendar.presentation.widget

import android.view.ContextThemeWrapper
import android.view.View
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.emoji2.emojipicker.R as Re
import androidx.recyclerview.widget.RecyclerView
import ru.agrachev.emojicalendar.R

@Composable
fun EmojiPicker(
    gridColumns: Int,
    onEmojiPicked: (emoji: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var emojiPickerView: View? = null
    var lastDragAmount by remember {
        mutableFloatStateOf(0f)
    }
    val onEmojiPickedCallbackState = rememberUpdatedState(onEmojiPicked)

    AndroidView(
        modifier = modifier
            .then(
                Modifier
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            lastDragAmount = -dragAmount
                        }
                    }
            ),
        factory = {
            EmojiPickerView(ContextThemeWrapper(it, R.style.EmojiPickerViewStyle))
                .apply {
                    emojiPickerView = this
                    emojiGridColumns = gridColumns
                    setOnEmojiPickedListener { item ->
                        onEmojiPickedCallbackState.value(item.emoji)
                    }
                }
        })

    LaunchedEffect(emojiPickerView) {
        snapshotFlow {
            lastDragAmount
        }.collect { dragAmount ->
            emojiPickerView
                ?.findViewById<RecyclerView>(Re.id.emoji_picker_body)
                ?.scrollBy(0, dragAmount.toInt())
        }
    }
}
