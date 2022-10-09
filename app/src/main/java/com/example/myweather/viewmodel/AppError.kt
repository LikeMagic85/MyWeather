package com.example.myweather.viewmodel

sealed class AppError {
    object Error1: AppError()
    object Error2:AppError()
}