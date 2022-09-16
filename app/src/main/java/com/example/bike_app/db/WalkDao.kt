package com.example.bike_app.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface WalkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWalk(walk: Walk)
    @Delete
    fun deleteWalk(walk: Walk)
    @Query("SELECT * FROM walking_table ORDER BY date DESC")
    fun getAllWalksSortedByDate():LiveData<List<Walk>>
}