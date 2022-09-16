package com.example.bike_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bike_app.db.Walk
import com.example.bike_app.db.repository.WalkRepository
import kotlinx.coroutines.launch

class WalkViewModel(val walkRepository: WalkRepository,
):ViewModel() {

    fun insertWalk(walk: Walk) = viewModelScope.launch {
        walkRepository.insertWalk(walk)
    }
    fun deleteWalk(walk: Walk) = viewModelScope.launch {
        walkRepository.deleteWalk(walk)
    }

    val walks = walkRepository.getAllWalksSortedByDate()
}