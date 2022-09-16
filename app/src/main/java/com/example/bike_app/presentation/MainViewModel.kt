package com.example.bike_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bike_app.db.Ride
import com.example.bike_app.db.Walk
import com.example.bike_app.db.repository.RideRepository
import com.example.bike_app.db.repository.WalkRepository
import kotlinx.coroutines.launch

class MainViewModel (
    val rideRepository: RideRepository,
        ):ViewModel(){
    fun insertRide(ride: Ride) = viewModelScope.launch {
        rideRepository.insertRide(ride)
    }
    fun deleteRide(ride: Ride) = viewModelScope.launch {
        rideRepository.deleteRide(ride)
    }
    val rides = rideRepository.getAllRidesSortedByDate()

}