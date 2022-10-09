package com.example.myweather.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.myweather.R
import com.example.myweather.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService: FirebaseMessagingService(){

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("in","coming")
        if(message.data.isNotEmpty()){
            val title = message.data[KEY_TITLE]
            val icon = message.data[KEY_ICON]
            val mMessage = message.data[KEY_MESSAGE]

            if(!title.isNullOrEmpty()&&!mMessage.isNullOrEmpty()&&!icon.isNullOrEmpty()){
                push(title,mMessage, icon)
            }
        }
    }

    override fun onNewToken(token: String) {
        saveFile("token.txt", token)
        super.onNewToken(token)
    }

    private fun saveFile(fileName: String, token:String) {
        applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(token.toByteArray())
        }
    }

    companion object {
        private const val NOTIFICATION_ID_LOW = 5
        private const val CHANNEL_ID_LOW = "channel_id_2"

        private const val KEY_TITLE = "myTitle"
        private const val KEY_MESSAGE = "myBody"
        private const val KEY_ICON = "myIcon"
    }

    private fun push(title:String,message:String, icon:String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(applicationContext, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID_LOW).apply {
            setSmallIcon(R.drawable.ic_map_marker)
            setContentTitle(title)
            setContentText(message)
            setSmallIcon(R.drawable.ic_map_marker)
            setContentIntent(contentIntent)
            priority = NotificationManager.IMPORTANCE_HIGH
        }


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channelNameLow = "FCM"
            val channelDescriptionLow = "Description $CHANNEL_ID_LOW"
            val channelPriorityHich = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID_LOW,channelNameLow,channelPriorityHich).apply {
                description = channelDescriptionLow
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID_LOW,notificationBuilder.build())
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND and Notification.DEFAULT_VIBRATE)
    }
}