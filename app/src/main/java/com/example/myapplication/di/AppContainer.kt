package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.database.AppDatabase

object AppContainer {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build().also { database = it }
    }
}

