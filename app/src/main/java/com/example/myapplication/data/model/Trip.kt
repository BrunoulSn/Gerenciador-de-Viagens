package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val destination: String,
    val type: String, // "Lazer" ou "Negócios"
    val startDate: Long, // Milliseconds
    val endDate: Long, // Milliseconds
    val budget: Double
)

