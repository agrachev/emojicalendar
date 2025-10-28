package ru.agrachev.emojicalendar.presentation.scope.browser

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.lazy.LazyListState
import ru.agrachev.emojicalendar.presentation.widget.DragHandleAnchors

internal interface EventBrowserScope {
    val emojiRowListState: LazyListState
    var selectedIndex: Int
    val anchoredState: AnchoredDraggableState<DragHandleAnchors>
    var fullExpandedAnchorOffset: Float
    var partiallyExpandedAnchorOffset: Float
    var iconVisibilityFraction: Float
    val vis: Boolean
    var isTitleLayoutPressed: Boolean
    val pressOffset: Float
    val totalDragOffset: Float
    val shouldDisplayExtraActions: Boolean
    val shouldDisplayExtraRow: Boolean

    fun undateFullExpandedAnchorOffset(offset: Float)
    fun selectItem(index: Int)
}
