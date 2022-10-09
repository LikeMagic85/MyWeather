package com.example.myweather.repository.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherDTO(
    @SerializedName("fact")
    val factDTO: FactDTO,
    @SerializedName("forecast")
    val forecastDTO: ForecastDTO,
    @SerializedName("info")
    val infoDTO: InfoDTO,
    @SerializedName("now")
    val now: String,
    @SerializedName("now_dt")
    val nowDt: String
):Parcelable