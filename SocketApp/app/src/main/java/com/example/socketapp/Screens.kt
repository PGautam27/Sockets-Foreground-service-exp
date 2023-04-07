package com.example.socketapp

sealed class Screens(val route:String){
    object Screen1 : Screens("screen_1")
    object Screen2 : Screens("screen_2")
}
