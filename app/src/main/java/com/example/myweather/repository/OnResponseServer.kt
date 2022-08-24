package com.example.myweather.repository

fun interface OnResponseServer {
    fun onResponse(weatherDTO: WeatherDTO)
}