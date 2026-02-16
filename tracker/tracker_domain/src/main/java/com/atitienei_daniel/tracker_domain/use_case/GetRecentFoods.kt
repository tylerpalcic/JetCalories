package com.atitienei_daniel.tracker_domain.use_case

import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackedFood
import com.atitienei_daniel.tracker_domain.repository.TrackerRepository

class GetRecentFoods(
    private val repository: TrackerRepository
) {

    suspend fun execute(mealType: MealType, limit: Int = 20): List<TrackedFood> =
        repository.getRecentFoodsForMealType(mealType, limit)
}
