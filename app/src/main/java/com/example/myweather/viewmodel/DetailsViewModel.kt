package com.example.myweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweather.repository.*
import javax.inject.Inject

class DetailsViewModel(
    private val repository: DetailsRepository = DetailsRepositoryRetrofit2Impl(),
    private val repositoryAdd: DetailsRepositoryAdd = DetailsRepositoryRoomImpl(),
    private var liveData: MutableLiveData<DetailsState> = MutableLiveData()) : ViewModel()  {

    fun getLiveData()= liveData


    fun getWeather(city: City){
        liveData.postValue(DetailsState.Loading)
        repository.getWeatherDetails(city, object: Callback{
            override fun onResponse(weather: Weather) {
                liveData.postValue(DetailsState.Success(weather))
                repositoryAdd.addWeather(weather)
            }

            override fun onFailure(appError: AppError) {
                liveData.postValue(DetailsState.Error(appError))
            }

        })
    }

    interface Callback {
        fun onResponse(weather: Weather)
        fun onFailure(appError: AppError)
    }


}