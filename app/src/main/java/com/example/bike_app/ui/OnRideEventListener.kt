package com.example.bike_app.ui

import com.example.bike_app.db.Ride

interface OnRideEventListener {
    fun OnRideLongPressed(ride: Ride?): Boolean
}
