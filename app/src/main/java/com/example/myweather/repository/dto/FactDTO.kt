package com.example.myweather.repository.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FactDTO(
    @SerializedName("feels_like")
    val feelsLike: Int,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("temp")
    val temp: Int
):Parcelable