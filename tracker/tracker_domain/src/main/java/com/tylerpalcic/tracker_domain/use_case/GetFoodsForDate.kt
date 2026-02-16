package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.tracker_domain.model.TrackedFood
import com.tylerpalcic.tracker_domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetFoodsForDate(
    private val repository: TrackerRepository
) {

    suspend fun execute(date: LocalDate): Flow<List<TrackedFood>> =
        repository.getFoodsForDate(date)
}