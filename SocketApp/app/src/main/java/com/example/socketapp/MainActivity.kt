package com.example.socketapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.socketapp.composables.Screen1
import com.example.socketapp.composables.Screen2
import com.example.socketapp.ui.theme.SocketAppTheme

class MainActivity : ComponentActivity() {
    private val sharedViewModel by viewModels<SharedViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect()

        if (intent?.action == "my_action") {
            setContent {
                Screen2(sharedViewModel = sharedViewModel)
            }
        }

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController , startDestination = Screens.Screen1.route){
                composable(Screens.Screen1.route){
                    Screen1(mSocket = mSocket)
                }
                composable(Screens.Screen2.route){
                    Screen2(sharedViewModel)
                }
            }
        }
        startMyForegroundService()
    }
    private fun startMyForegroundService() {
        val intent = Intent(this, MyForegroundService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}



