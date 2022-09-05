package com.example.bike_app.db.repository

import androidx.lifecycle.LiveData
import com.example.bike_app.db.Ride
import com.example.bike_app.db.RideDAO

class RideRepositoryImpl(val rideDAO: RideDAO) : RideRepository {
    override fun insertRide(ride: Ride) = rideDAO.insertRide(ride)
    override fun deleteRide(ride: Ride) = rideDAO.deleteRide(ride)
    override fun getAllRidesSortedByDate(): LiveData<List<Ride>> = rideDAO.getAllRidesSortedByDate()

}