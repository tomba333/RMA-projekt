package com.example.bike_app.db.repository

import androidx.lifecycle.LiveData
import com.example.bike_app.db.Walk

interface WalkRepository {
    fun insertWalk(walk: Walk)
    fun deleteWalk(walk: Walk)
    fun getAllWalksSortedByDate(): LiveData<List<Walk>>
}