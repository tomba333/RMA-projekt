package com.example.bike_app.db.repository

import androidx.lifecycle.LiveData
import com.example.bike_app.db.Walk
import com.example.bike_app.db.WalkDao

class WalkRepositoryImpl(val walkDao: WalkDao):WalkRepository {
    override fun insertWalk(walk: Walk) = walkDao.insertWalk(walk)
    override fun deleteWalk(walk: Walk)= walkDao.deleteWalk(walk)
    override fun getAllWalksSortedByDate(): LiveData<List<Walk>> = walkDao.getAllWalksSortedByDate()
}