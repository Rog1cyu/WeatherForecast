package com.uilover.project2172.Model

data class WeatherResponse(
    val name: String, // 城市名称
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Float,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)
