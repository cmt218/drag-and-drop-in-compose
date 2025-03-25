package com.example.draganddropincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.draganddropincompose.apis.DragAndDropApis
import com.example.draganddropincompose.pointerinput.DragAndDropPointerInput
import com.example.draganddropincompose.ui.theme.DragAndDropInComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var screen by mutableStateOf(Screen.HOME)
        setContent {
            DragAndDropInComposeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                val topAppBarText = when (screen) {
                                    Screen.HOME -> "Drag and Drop in Compose!"
                                    Screen.DRAG_AND_DROP_POINTER_INPUT -> "Drag and Drop Pointer Input"
                                    Screen.DRAG_AND_DROP_APIS -> "Drag and Drop APIs"
                                }
                                Text(topAppBarText)
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { screen = Screen.HOME }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    when (screen) {
                        Screen.HOME -> {
                            HomeScreen(modifier = Modifier.padding(innerPadding)) { screen = it }
                        }

                        Screen.DRAG_AND_DROP_POINTER_INPUT -> {
                            DragAndDropPointerInput(modifier = Modifier.padding(innerPadding))
                        }

                        Screen.DRAG_AND_DROP_APIS -> {
                            DragAndDropApis(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier, onClick: (Screen) -> Unit) {
    Column(modifier = modifier) {
        TextButton(
            modifier = Modifier.padding(16.dp),
            onClick = { onClick(Screen.DRAG_AND_DROP_POINTER_INPUT) }
        ) {
            Text("Drag and Drop Pointer Input")
        }

        TextButton(
            modifier = Modifier.padding(16.dp),
            onClick = { onClick(Screen.DRAG_AND_DROP_APIS) }
        ) {
            Text("Drag and Drop APIs")
        }
    }
}

enum class Screen {
    HOME,
    DRAG_AND_DROP_POINTER_INPUT,
    DRAG_AND_DROP_APIS
}