package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.tracker_domain.model.WeightEntry
import com.tylerpalcic.tracker_domain.repository.TrackerRepository
import java.time.LocalDate

class GetLatestWeight(
    private val repository: TrackerRepository
) {
    suspend fun execute(date: LocalDate): WeightEntry? =
        repository.getWeightEntryForDate(date)
}
