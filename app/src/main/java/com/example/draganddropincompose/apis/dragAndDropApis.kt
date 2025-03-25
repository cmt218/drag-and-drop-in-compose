package com.example.draganddropincompose.apis

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropApis(modifier: Modifier) {
    var list by remember { mutableStateOf(List(50) { it }) }

    var lazyColumnLayoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropApiState(listState) { fromIndex, toIndex ->
        list = list.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
    }
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onMoved(event: DragAndDropEvent) {
                val dragEvent = event.toAndroidDragEvent()
                lazyColumnLayoutCoordinates?.windowToLocal(Offset(dragEvent.x, dragEvent.y))?.let {
                    dragDropState.onDrag(it.y)
                }
                super.onMoved(event)
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                dragDropState.draggingItemIndex = null
                return true
            }

            override fun onEnded(event: DragAndDropEvent) {
                dragDropState.draggingItemIndex = null
                super.onEnded(event)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .dragAndDropTarget(
                shouldStartDragAndDrop = { _ -> true },
                target = dragAndDropTarget
            )
            .onGloballyPositioned { lazyColumnLayoutCoordinates = it },
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(list, key = { _, item -> item }) { index, item ->
            Card(
                modifier = Modifier
                    .drawWithContent {
                        if (dragDropState.draggingItemIndex != index) {
                            drawContent()
                        }
                    }
                    .dragAndDropSource {
                        detectDragGesturesAfterLongPress(
                            onDrag = { _, _ -> },
                            onDragStart = { _ ->
                                dragDropState.draggingItemIndex = index
                                startTransfer(
                                    transferData = DragAndDropTransferData(
                                        clipData = ClipData.newPlainText("demo", "$index")
                                    )
                                )
                            }
                        )
                    }
                    .animateItem()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    text = "Item $item"
                )
            }
        }
    }
}