package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.tracker_domain.model.WeightEntry
import com.tylerpalcic.tracker_domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow

class GetWeightEntries(
    private val repository: TrackerRepository
) {
    fun execute(): Flow<List<WeightEntry>> = repository.getAllWeightEntries()
}
