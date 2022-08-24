package com.example.myweather.repository

class RepositoryImpl:Repository {
    override fun getWeatherFromServer():Weather {
        Thread.sleep(2000L)
        return Weather()
    }

    override fun getWorldWeatherFromLocalStorage() = getWorldCities()


    override fun getRussianWeatherFromLocalStorage() = getRussianCities()

}