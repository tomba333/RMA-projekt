package com.example.bike_app.db.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.bike_app.db.Ride
import com.example.bike_app.db.Walk

interface RideRepository {
    fun insertRide(ride: Ride)
    fun deleteRide(ride: Ride)
    fun getAllRidesSortedByDate(): LiveData<List<Ride>>

}