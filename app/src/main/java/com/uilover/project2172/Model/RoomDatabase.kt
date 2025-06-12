package com.uilover.project2172.Model

import android.content.Context
import androidx.room.*

@Database(entities = [WeatherCacheEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "weather_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
