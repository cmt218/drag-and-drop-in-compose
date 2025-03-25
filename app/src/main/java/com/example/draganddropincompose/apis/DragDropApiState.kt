package com.example.draganddropincompose.apis

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun rememberDragDropApiState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragDropApiState {
    val scope = rememberCoroutineScope()
    val state = remember(lazyListState) {
        DragDropApiState(state = lazyListState, onMove = onMove, scope = scope)
    }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

class DragDropApiState
internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)

    internal val scrollChannel = Channel<Float>()

    internal fun onDrag(y: Float) {
        val draggingIndex = draggingItemIndex ?: return
        val targetItem =
            state.layoutInfo.visibleItemsInfo.find { item ->
                val itemMiddleOffset = item.offset + (item.offsetEnd - item.offset) / 2f
                itemMiddleOffset <= y && draggingIndex < item.index || y <= itemMiddleOffset && draggingIndex > item.index
            }
        if (targetItem != null) {
            if (
                draggingIndex == state.firstVisibleItemIndex ||
                targetItem.index == state.firstVisibleItemIndex
            ) {
                scope.launch {
                    state.scrollToItem(
                        state.firstVisibleItemIndex,
                        state.firstVisibleItemScrollOffset
                    )
                }
            }
            onMove.invoke(draggingIndex, targetItem.index)
            draggingItemIndex = targetItem.index
        } else {
            val overscroll =
                when {
                    y < 100f -> {
                        y - 100
                    }

                    y > state.layoutInfo.viewportEndOffset.toFloat() - 100f -> {
                        y - (state.layoutInfo.viewportEndOffset.toFloat() - 100f)
                    }

                    else -> 0f
                }
            scrollChannel.trySend(overscroll)
        }
    }

    private val LazyListItemInfo.offsetEnd: Int
        get() = this.offset + this.size
}