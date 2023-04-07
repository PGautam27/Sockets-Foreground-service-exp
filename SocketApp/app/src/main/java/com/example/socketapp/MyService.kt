package com.example.socketapp

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.*
import kotlin.concurrent.thread

const val INTENT_COMMAND = "Command"
const val INTENT_COMMAND_EXIT = "Exit"
const val INTENT_COMMAND_REPLY = "Reply"
const val INTENT_COMMAND_ACHIEVE = "Achieve"

private const val NOTIFICATION_CHANNEL_GENERAL = "Checking"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_REPLY_INTENT = 2
private const val CODE_ACHIEVE_INTENT = 3

class MyForegroundService : Service() {

    private var job: Job? = null
    private var socket = SocketHandler.mSocket
    private lateinit var handler: Handler
    private val counter  = MutableLiveData<String>()
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder


    override fun onStartCommand(intents: Intent?, flag: Int, startId: Int): Int {
        createNotificationChannel() // create a notification for the service
        startForegroundService()
        handler = Handler(Looper.getMainLooper())
        notificationManager = NotificationManagerCompat.from(this)
        notificationBuilder = createNotification("My Foreground Service", "Service is running")
        notificationManager.notify(1, notificationBuilder.build())// start the service in the foreground with the notification

        return START_STICKY // return START_STICKY to ensure the service is restarted if it's killed
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
//        val notification = createNotificationBuilder("My Foreground Service", "Service is running").build()
        startForeground(1, createNotification("My Foreground Service", "Service is running").build())

        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                socket.on("counter"){args->
                    if (args[0]!=null){
                        handler.post {
                            val message = args[0].toString()
                            val updatedNotification = createNotification("My Foreground Service", message).build()
                            notificationManager.notify(1, updatedNotification)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(title: String, text: String): NotificationCompat.Builder {
        val sharedViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(SharedViewModel::class.java)
        sharedViewModel.data.value = "Data to be passed to the composable screen"
        val resultIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = "my_action"
            putExtra("my_key", "my_value")
        }
        val pendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "my_service_channel")
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)

        return notification
    }

    private fun createNotificationBuilder(title: String, text: String): NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(this, "my_service_channel")
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(false)

        return notificationBuilder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_service_channel",
                "My Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}

