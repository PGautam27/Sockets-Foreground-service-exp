package com.example.socketapp.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socketapp.ui.theme.SocketAppTheme
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Screen1(mSocket: Socket) {
    val coroutineScope = rememberCoroutineScope()

    val counter = remember {
        mutableStateOf(String())
    }
    SocketAppTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Counter value : " + counter.value,
                style = TextStyle(fontSize = 25.sp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Button(onClick = {
                mSocket.emit("counter")
            }) {
                Text(text = "Counter Click")
            }
        }
        mSocket.on("counter"){args->
            if (args[0]!=null){
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        counter.value = args[0].toString()                    }
                }
            }
        }
    }
}