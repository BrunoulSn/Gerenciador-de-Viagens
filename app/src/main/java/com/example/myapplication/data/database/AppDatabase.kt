package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.TripDao
import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.model.Trip
import com.example.myapplication.data.model.User

@Database(
    entities = [User::class, Trip::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
}

