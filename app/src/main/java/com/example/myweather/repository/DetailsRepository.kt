package com.example.myweather.repository

import com.example.myweather.viewmodel.DetailsViewModel

fun interface DetailsRepository {
    fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback)
}