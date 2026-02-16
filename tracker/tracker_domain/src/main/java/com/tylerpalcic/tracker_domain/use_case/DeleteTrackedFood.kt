package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.tracker_domain.model.TrackedFood
import com.tylerpalcic.tracker_domain.repository.TrackerRepository

class DeleteTrackedFood(
    private val repository: TrackerRepository
) {

    suspend fun execute(food: TrackedFood) {
        repository.deleteTrackedFood(food)
    }
}