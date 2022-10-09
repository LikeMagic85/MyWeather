package com.example.myweather.utils

import com.example.myweather.domain.room.HistoryEntity
import com.example.myweather.repository.City
import com.example.myweather.repository.Weather
import com.example.myweather.repository.dto.FactDTO
import com.example.myweather.repository.dto.WeatherDTO
import com.example.myweather.repository.getDefaultCity

const val KEY_BUNDLE_WEATHER = "key"
const val YANDEX_DOMAIN = "https://api.weather.yandex.ru/"
const val YANDEX_ENDPOINT = "v2/informers?"
const val YANDEX_API_KEY = "X-Yandex-API-Key"
const val KEY_BUNDLE_LAT = "lat"
const val KEY_BUNDLE_LON = "lon"
const val KEY_WAVE = "myaction"
const val COUNTRY = "COUNTRY"
const val IS_RUSSIAN = "ISRUSSIAN"

class Utils {
}

fun convertDTOtoModel(weatherDTO: WeatherDTO): Weather{
    val fact: FactDTO = weatherDTO.factDTO
    val time: String = weatherDTO.nowDt
    return (Weather(getDefaultCity(), fact.temp, fact.feelsLike, fact.icon, time.removeRange(0..10).removeRange(8..15)))
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map {
        Weather(City(it.city, 0.0, 0.0), it.temperature, it.feelsLike, it.icon, it.time)
    }
}

fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity(0, weather.city.name, weather.temperature,weather.feelsLike, weather.icon, weather.time)
}


