package com.example.myweather.repository

import com.example.myweather.BuildConfig
import com.example.myweather.repository.dto.WeatherDTO
import com.example.myweather.utils.YANDEX_DOMAIN
import com.example.myweather.utils.convertDTOtoModel
import com.example.myweather.viewmodel.AppError
import com.example.myweather.viewmodel.DetailsState
import com.example.myweather.viewmodel.DetailsViewModel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsRepositoryRetrofit2Impl:DetailsRepository {
    override fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback) {
        val weatherApi = Retrofit.Builder().apply {
            baseUrl(YANDEX_DOMAIN)
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        }.build().create(WeatherApi::class.java)

       weatherApi.getWeather(BuildConfig.WEATHER_API_KEY,city.lat,city.lon).enqueue(object : Callback<WeatherDTO>{
           override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
               if(response.isSuccessful){
                   response.body()?.let {
                       val weather = convertDTOtoModel(it)
                       weather.city = city
                       callback.onResponse(weather)
                   }
               }
               else if (response.code() in 400..499){
                   callback.onFailure(AppError.Error1)
               }
               else if(response.code()>= 500){
                   DetailsState.Error(AppError.Error2)
               }
           }

           override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
                callback.onFailure(AppError.Error2)
           }

       })
    }
}