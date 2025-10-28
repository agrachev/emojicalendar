package ru.agrachev.emojicalendar.presentation.scope.browser

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.agrachev.emojicalendar.presentation.screen.getSelectedItemInvisibilityFraction
import ru.agrachev.emojicalendar.presentation.widget.DragHandleAnchors
import kotlin.math.max

internal abstract class EventBrowserStatefulScope internal constructor() : EventBrowserScope {

    abstract override val emojiRowListState: LazyListState
    abstract override var selectedIndex: Int

    internal lateinit var pressOffsetState: State<Float>
    internal lateinit var shouldDisplayExtraRowState: State<Boolean>

    override val anchoredState = AnchoredDraggableState(
        initialValue = DragHandleAnchors.COLLAPSED,
        anchors = DraggableAnchors {
            DragHandleAnchors.COLLAPSED at 0f
            DragHandleAnchors.EXPANDED at 1f
        },
    )
    override var fullExpandedAnchorOffset by mutableFloatStateOf(0f)
    override var partiallyExpandedAnchorOffset by mutableFloatStateOf(1f)
    override var iconVisibilityFraction by mutableFloatStateOf(0f)
    override val vis by derivedStateOf {
        iconVisibilityFraction >= .5f
    }
    override var isTitleLayoutPressed by mutableStateOf(false)
    override val pressOffset: Float
        get() = if (::pressOffsetState.isInitialized) pressOffsetState.value else 0f
    override val totalDragOffset by derivedStateOf {
        max(anchoredState.requireOffset(), pressOffset)
    }
    override val shouldDisplayExtraActions by derivedStateOf {
        totalDragOffset > 0f
    }
    override val shouldDisplayExtraRow: Boolean
        get() = ::shouldDisplayExtraRowState.isInitialized && shouldDisplayExtraRowState.value

    override fun undateFullExpandedAnchorOffset(offset: Float) {
        fullExpandedAnchorOffset = offset
        anchoredState.updateAnchors(
            DraggableAnchors {
                DragHandleAnchors.COLLAPSED at 0f
                DragHandleAnchors.EXPANDED at offset
            }
        )
    }

    override fun selectItem(index: Int) {
        selectedIndex = index
        iconVisibilityFraction =
            emojiRowListState.layoutInfo.getSelectedItemInvisibilityFraction(index)
    }
}
