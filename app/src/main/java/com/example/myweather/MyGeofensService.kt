package com.example.myweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.AttributionSource
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import com.example.myweather.view.MainActivity


class MyGeofensService(name:String=""):IntentService(name) {

    override fun onHandleIntent(p0: Intent?) {

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val intent = Intent(applicationContext, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hello")
            .setContentText("World")
            .setSmallIcon(R.drawable.ic_map_marker)
            .setContentIntent(contentIntent)
            .build()

        startForeground(1, notification)
        getPosition()
        return START_NOT_STICKY
    }


    private val locationListenerPosition = LocationListener {
        Log.d("@@@","${it.longitude}")
        showAlertShop(it)
    }


    @SuppressLint("MissingPermission")
    private fun getPosition() {
        applicationContext?.let {
            val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                providerGPS?.let {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000L,
                        0f,
                        locationListenerPosition
                    )
                }
            }
        }
    }


    private var sendOnceNotify = false


    private fun showAlertShop(location: Location){
        if(location.latitude in  54.48962741541227..54.49019586052192
            && location.longitude in 30.443016459400926..30.44401407518842
            && !sendOnceNotify){
                push(KEY_SHOP)
        }else if (location.latitude in  54.49683181195893..54.497569252974884
            && location.longitude in 30.407888808800365..30.408824824568228
            && !sendOnceNotify){
            push(KEY_HOME)
        }else if(location.latitude in  54.527723982442474..54.52827979110087
            && location.longitude in 30.449070990814686..30.449954810124513
            && !sendOnceNotify){
            push(KEY_WORK)
        }
        else if(location.latitude in  54.49018018187172..54.49075817385898
            && location.longitude in 30.440549203725315.. 30.441378006213906
            && !sendOnceNotify){
            push(KEY_MARUSIA)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 3
        private const val CHANNEL_ID = "channel_id_1"
        private const val KEY_TITLE = "Вы в точке"
        private const val KEY_SHOP = "Магазин 'Копеечка'"
        private const val KEY_HOME = "Дом"
        private const val KEY_WORK = "Работа"
        private const val KEY_MARUSIA = "Маруся"
    }

    private fun push(key:String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationBuilderHigh= NotificationCompat.Builder(this,
            CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_map_marker)
            setContentTitle(KEY_TITLE)
            setContentText(key)
            setContentIntent(contentIntent)
            priority = NotificationManager.IMPORTANCE_HIGH
        }


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channelNameLow = "Name $NOTIFICATION_ID"
            val channelDescriptionLow = "Description $CHANNEL_ID"
            val channelPriorityLow = NotificationManager.IMPORTANCE_LOW
            val channelLow = NotificationChannel(CHANNEL_ID,channelNameLow,channelPriorityLow).apply {
                description = channelDescriptionLow
            }
            notificationManager.createNotificationChannel(channelLow)
        }

        notificationManager.notify(NOTIFICATION_ID,notificationBuilderHigh.build())
        notificationBuilderHigh.setDefaults(Notification.DEFAULT_SOUND and Notification.DEFAULT_VIBRATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}