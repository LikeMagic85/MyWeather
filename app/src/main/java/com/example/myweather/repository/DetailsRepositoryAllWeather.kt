package com.example.myweather.repository

import com.example.myweather.viewmodel.HistoryViewModel

fun interface DetailsRepositoryAllWeather {
    fun getWeatherDetailsAll(callback: HistoryViewModel.CallbackAllWeather)
}