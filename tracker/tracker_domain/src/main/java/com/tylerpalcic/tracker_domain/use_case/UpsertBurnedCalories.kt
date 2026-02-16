package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.tracker_domain.model.BurnedCalories
import com.tylerpalcic.tracker_domain.repository.TrackerRepository

class UpsertBurnedCalories(
    private val repository: TrackerRepository
) {
    suspend fun execute(burnedCalories: BurnedCalories) {
        require(burnedCalories.calories >= 0) { "Burned calories must be non-negative" }
        repository.upsertBurnedCalories(burnedCalories)
    }
}
