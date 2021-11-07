package com.example.kotlincoroutinstestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.kotlincoroutinstestapp.ui.theme.KotlinCoroutinsTestAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {

        }
        setContent {
            KotlinCoroutinsTestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
) {

    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        item {
            ButtonView(text = "Get Data in default launch") {
                mainViewModel.getData()
            }
        }
        item {
            ButtonView(text = "Get Data in Dispatcher.IO launch") {
                mainViewModel.getDataInDispatcherLaunch()
            }
        }
        item {
            ButtonView(text = "Send n Receive Test") {
                mainViewModel.channelSendReceiveTest()
            }
        }
        item {
            ButtonView(text = "Flow Test") {
                mainViewModel.flowTest()
            }
        }
        item {
            ButtonView(text = "Flows are cold") {
                mainViewModel.flowsAreCold()
            }
        }

        item {
            ButtonView(text = "Flows Cancellation") {
                mainViewModel.flowCancellation()
            }
        }
        item {
            ButtonView(text = "Flows Map Operator") {
                mainViewModel.flowMapOperator()
            }
        }

        item {
            ButtonView(text = "Flows Transform Operator") {
                mainViewModel.flowTransformOperator()
            }
        }

        item {
            ButtonView(text = "Flows Size Limiting Operator") {
                mainViewModel.flowSizeLimitingOperator()
            }
        }

        item {
            ButtonView(text = "Flows Terminal Operator") {
                mainViewModel.flowTerminalOperator()
            }
        }

        item {
            ButtonView(text = "Flows are sequential") {
                mainViewModel.flowsAreSequential()
            }
        }
    }
}

@Composable
fun ChannelsScreen(
    mainViewModel: MainViewModel,
) {
    Column() {
//        ButtonView(text = "Get Data in Dispatcher.IO launch") {
//            mainViewModel.getDataInDispatcherLaunch()
//        }
    }
}

@Composable
fun ButtonView(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.padding(top = 10.dp, start = 10.dp),
        onClick = {
            onClick()
        },
    ) {
        Text(text = text)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinCoroutinsTestAppTheme {
        Greeting("Android")
    }
}