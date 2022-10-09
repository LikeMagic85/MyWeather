package com.example.myweather.repository

import com.example.myweather.repository.dto.WeatherDTO
import com.example.myweather.utils.KEY_BUNDLE_LAT
import com.example.myweather.utils.KEY_BUNDLE_LON
import com.example.myweather.utils.YANDEX_API_KEY
import com.example.myweather.utils.YANDEX_ENDPOINT
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherApi {
    @GET(YANDEX_ENDPOINT)
    fun getWeather(
        @Header(YANDEX_API_KEY) apiKey: String,
        @Query(KEY_BUNDLE_LAT) lat: Double,
        @Query(KEY_BUNDLE_LON) lon: Double
    ): Call<WeatherDTO>
}