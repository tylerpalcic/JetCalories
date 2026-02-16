package com.atitienei_daniel.tracker_domain.use_case

import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.repository.TrackerRepository
import java.time.LocalDate

class CopyMealFromDate(
    private val repository: TrackerRepository
) {

    suspend fun execute(sourceDate: LocalDate, targetDate: LocalDate, mealType: MealType) {
        val foods = repository.getFoodsForDateAndMealType(sourceDate, mealType)
        foods.forEach { food ->
            repository.insertTrackedFood(
                food.copy(id = null, date = targetDate)
            )
        }
    }

    suspend fun getMealCountsForDate(date: LocalDate): Map<MealType, Int> =
        MealType.values().associateWith { mealType ->
            repository.getFoodsForDateAndMealType(date, mealType).size
        }
}
