package com.example.myweather.viewmodel

import com.example.myweather.repository.Weather

sealed class DetailsState {
    object Loading:DetailsState()
    data class Success(val weather: Weather):DetailsState()
    data class Error(val error: AppError):DetailsState()
}