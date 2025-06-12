package com.uilover.project2172.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val cityName: String,
    val temp: Float,
    val humidity: Int,
    val description: String,
    val icon: String,
    val timestamp: Long // 保存时间
)
