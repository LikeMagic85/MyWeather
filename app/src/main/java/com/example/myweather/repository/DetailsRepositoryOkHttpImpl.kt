package com.example.myweather.repository

import com.example.myweather.BuildConfig
import com.example.myweather.repository.dto.WeatherDTO
import com.example.myweather.utils.YANDEX_API_KEY
import com.example.myweather.utils.YANDEX_DOMAIN
import com.example.myweather.utils.YANDEX_ENDPOINT
import com.example.myweather.utils.convertDTOtoModel
import com.example.myweather.viewmodel.AppError
import com.example.myweather.viewmodel.DetailsState
import com.example.myweather.viewmodel.DetailsViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class DetailsRepositoryOkHttpImpl: DetailsRepository {
    override fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback) {
        val client = OkHttpClient()
        val builder = Request.Builder()
        with(builder){
            addHeader(YANDEX_API_KEY, BuildConfig.WEATHER_API_KEY)
            url("$YANDEX_DOMAIN${YANDEX_ENDPOINT}lat=${city.lat}&lon=${city.lon}")

        }
        val request = builder.build()

        val call = client.newCall(request)
        Thread {
            val response = call.execute()
            if (response.isSuccessful){
                val weatherDTO: WeatherDTO = Gson().fromJson(response.body.string(), WeatherDTO::class.java)
                val weather = convertDTOtoModel(weatherDTO)
                weather.city = city
                callback.onResponse(weather)
            }
            else if (response.code in 400..499){
                callback.onFailure(AppError.Error1)
            }
            else if(response.code >= 500){
                DetailsState.Error(AppError.Error2)
            }
        }.start()
    }

}