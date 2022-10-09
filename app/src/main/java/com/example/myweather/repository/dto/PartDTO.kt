package com.example.myweather.repository.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PartDTO(
    @SerializedName("feels_like")
    val feelsLike: Int,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("temp_avg")
    val tempAvg: Int,
    @SerializedName("temp_max")
    val tempMax: Int,
    @SerializedName("temp_min")
    val tempMin: Int
):Parcelable