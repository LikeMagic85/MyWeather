package com.example.myweather.view.details

import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myweather.BuildConfig
import com.example.myweather.repository.dto.WeatherDTO
import com.example.myweather.utils.*
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DetailsService(name: String = ""): IntentService(name) {
    override fun onHandleIntent(p0: Intent?) {
        p0?.let{
            val lat = it.getDoubleExtra(KEY_BUNDLE_LAT, 0.0)
            val lon = it.getDoubleExtra(KEY_BUNDLE_LON, 0.0)

            val urlText = "${YANDEX_DOMAIN}${YANDEX_ENDPOINT}lat=$lat&lon=$lon"
            val uri = URL(urlText)


            val urlConnection: HttpsURLConnection =
                (uri.openConnection() as HttpsURLConnection).apply {
                    connectTimeout = 1000
                    readTimeout = 1000
                    addRequestProperty(YANDEX_API_KEY, BuildConfig.WEATHER_API_KEY)
                }


            val responseCode = urlConnection.responseCode


            val serverside = 500..599
            val clientside = 400..499
            val responseOk = 200..299
            var weatherDTO: WeatherDTO? = null
            val message = Intent(KEY_WAVE)
            when (responseCode) {
                in serverside -> {
                    /*message.putExtra(KEY_BUNDLE_SERVICE_BROADCAST_WEATHER, AppError.Error2)*/
                }
                in clientside -> {
                    /*message.putExtra(KEY_BUNDLE_SERVICE_BROADCAST_WEATHER, AppError.Error1)*/
                }
                in responseOk -> {
                    try {
                        val buffer = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        weatherDTO = Gson().fromJson(buffer, WeatherDTO::class.java)
                        /*message.putExtra(KEY_BUNDLE_SERVICE_BROADCAST_WEATHER, AppError.Success(weatherDTO))*/
                    } catch (e: Exception) {
                    }
                    finally {
                        urlConnection.disconnect()
                    }
                }
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(message)
        }
    }
}