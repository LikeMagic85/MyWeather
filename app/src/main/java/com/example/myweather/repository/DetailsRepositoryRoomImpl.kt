package com.example.myweather.repository

import com.example.myweather.MyApp
import com.example.myweather.utils.convertHistoryEntityToWeather
import com.example.myweather.utils.convertWeatherToEntity
import com.example.myweather.viewmodel.DetailsViewModel
import com.example.myweather.viewmodel.HistoryViewModel


class DetailsRepositoryRoomImpl:DetailsRepositoryAllWeather, DetailsRepository, DetailsRepositoryAdd {

    override fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback) {
       callback.onResponse(convertHistoryEntityToWeather(MyApp.getHistoryDao().getAll()).last())
    }

    override fun addWeather(weather: Weather) {
        MyApp.getHistoryDao().insert(convertWeatherToEntity(weather))
    }

    override fun getWeatherDetailsAll(callback: HistoryViewModel.CallbackAllWeather) {
        callback.onResponse(convertHistoryEntityToWeather(MyApp.getHistoryDao().getAll()))
    }


}