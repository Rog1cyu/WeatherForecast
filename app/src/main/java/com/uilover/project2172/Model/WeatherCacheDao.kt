package com.uilover.project2172.Model

import androidx.room.*

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE cityName = :city LIMIT 1")
    suspend fun getWeatherByCity(city: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeather(entity: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache ORDER BY timestamp DESC")
    suspend fun getAllHistory(): List<WeatherCacheEntity>
}