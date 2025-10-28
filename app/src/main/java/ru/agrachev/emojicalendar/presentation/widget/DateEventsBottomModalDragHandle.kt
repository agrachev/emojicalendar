package ru.agrachev.emojicalendar.presentation.widget

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.agrachev.emojicalendar.presentation.toIntPx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEventsBottomModalDragHandle(
    modifier: Modifier = Modifier,
    heightGrowthDelta: Dp = 0.dp,
) {
    val heightGrowthDeltaPx = heightGrowthDelta.toIntPx()
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = DragHandleAnchors.COLLAPSED,
            anchors = DraggableAnchors {
                DragHandleAnchors.COLLAPSED at 0f
                DragHandleAnchors.EXPANDED at heightGrowthDeltaPx * .75f
            }
        )
    }
    BottomSheetDefaults.DragHandle(
        modifier = modifier then Modifier.anchoredDraggable(
            state = anchoredDraggableState,
            reverseDirection = true,
            orientation = Orientation.Vertical,
            enabled = heightGrowthDeltaPx > 0f,
        ),
    )
    LaunchedEffect(anchoredDraggableState) {
        snapshotFlow {
            anchoredDraggableState.targetValue
        }.collect {

        }
    }
}

enum class DragHandleAnchors {
    COLLAPSED,
    EXPANDED,
}
