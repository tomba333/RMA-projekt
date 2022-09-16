package com.example.bike_app.db

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riding_table")
data class Ride
    (
    @ColumnInfo(name = "mapa")
    var img: Bitmap? = null,
    @ColumnInfo(name = "date")
    var timestamp: Long = 0L,
    @ColumnInfo(name = "speed")
    var avgSpeed: Float = 0f,
    @ColumnInfo(name = "distance")
    var distanceInMeter: Int = 0,
    @ColumnInfo(name = "time")
    var timeInMilis: Long = 0L,
    @ColumnInfo(name = "cals")
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var Id: Int? = null
}