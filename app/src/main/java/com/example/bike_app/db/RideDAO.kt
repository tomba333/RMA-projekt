package com.example.bike_app.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RideDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRide(ride: Ride)

    @Delete
    fun deleteRide(ride: Ride)

    @Query("SELECT * FROM riding_table Order BY time DESC")
    fun getAllRidesSortedByDate(): LiveData<List<Ride>>


}