package com.example.myweather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweather.repository.Repository
import com.example.myweather.repository.RepositoryImpl
import com.example.myweather.repository.Weather
import java.lang.Thread.sleep

class MainViewModel(
    private val repository: RepositoryImpl = RepositoryImpl(),
    private val liveData: MutableLiveData<AppState> = MutableLiveData()) :ViewModel() {

    fun getData(): LiveData<AppState> {
        return liveData
    }

    fun getWeatherRussian() = getWeather(true)
    fun getWeatherWorld() = getWeather(false)

    private fun getWeather(isRussian: Boolean) {
        Thread {
            liveData.postValue(AppState.Loading)

            val answer = if(isRussian) repository.getRussianWeatherFromLocalStorage()else repository.getWorldWeatherFromLocalStorage()
            liveData.postValue(AppState.Success(answer))

        }.start()
    }
}