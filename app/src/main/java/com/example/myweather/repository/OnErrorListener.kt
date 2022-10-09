package com.example.myweather.repository

import com.example.myweather.viewmodel.AppError

fun interface OnErrorListener {
    fun onError(appError: AppError)
}