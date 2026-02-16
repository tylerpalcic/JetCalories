package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.tracker_domain.model.MealType
import com.tylerpalcic.tracker_domain.model.TrackedFood
import com.tylerpalcic.tracker_domain.repository.TrackerRepository

class GetRecentFoods(
    private val repository: TrackerRepository
) {

    suspend fun execute(mealType: MealType, limit: Int = 20): List<TrackedFood> =
        repository.getRecentFoodsForMealType(mealType, limit)
}
