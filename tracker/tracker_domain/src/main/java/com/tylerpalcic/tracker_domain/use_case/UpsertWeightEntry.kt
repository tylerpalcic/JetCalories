package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.core.domain.data_store.UserDataStore
import com.tylerpalcic.tracker_domain.model.WeightEntry
import com.tylerpalcic.tracker_domain.repository.TrackerRepository

class UpsertWeightEntry(
    private val repository: TrackerRepository,
    private val dataStore: UserDataStore
) {
    suspend fun execute(weightEntry: WeightEntry) {
        require(weightEntry.weight > 0f) { "Weight must be positive" }
        repository.upsertWeightEntry(weightEntry)
        dataStore.saveWeight(weightEntry.weight)
    }
}
