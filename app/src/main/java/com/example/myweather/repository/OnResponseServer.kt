package com.example.myweather.repository

import com.example.myweather.repository.dto.WeatherDTO

fun interface OnResponseServer {
    fun onResponse(weatherDTO: WeatherDTO)
}