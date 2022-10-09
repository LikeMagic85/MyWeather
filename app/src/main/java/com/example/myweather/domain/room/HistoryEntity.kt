package com.example.myweather.domain.room

import androidx.room.Entity
import androidx.room.PrimaryKey


const val ID = "id"
const val NAME = "city"
const val TEMPERATURE = "temperature"
const val FEELS_LIKE = "feelsLike"
const val ICON = "icon"
const val TIME = "time"

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    var city: String,
    val temperature: Int,
    val feelsLike: Int ,
    val icon:String ,
    val time:String
)
