package com.atitienei_daniel.tracker_domain.use_case

import com.atitienei_daniel.tracker_domain.repository.TrackerRepository
import java.time.LocalDate

class GetBurnedCaloriesForDate(
    private val repository: TrackerRepository
) {
    suspend fun execute(date: LocalDate): Int {
        return repository.getBurnedCaloriesForDate(date)
    }
}
