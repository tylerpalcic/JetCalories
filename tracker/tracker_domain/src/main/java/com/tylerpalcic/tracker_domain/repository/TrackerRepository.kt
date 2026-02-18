package com.tylerpalcic.tracker_domain.repository

import com.tylerpalcic.tracker_domain.model.BurnedCalories
import com.tylerpalcic.tracker_domain.model.MealType
import com.tylerpalcic.tracker_domain.model.TrackableFood
import com.tylerpalcic.tracker_domain.model.TrackedFood
import com.tylerpalcic.tracker_domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TrackerRepository {
    suspend fun searchFood(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<TrackableFood>>

    suspend fun insertTrackedFood(food: TrackedFood)

    suspend fun deleteTrackedFood(food: TrackedFood)

    fun getFoodsForDate(localDate: LocalDate): Flow<List<TrackedFood>>

    suspend fun getRecentFoodsForMealType(mealType: MealType, limit: Int = 20): List<TrackedFood>

    suspend fun getFoodsForDateAndMealType(date: LocalDate, mealType: MealType): List<TrackedFood>

    suspend fun upsertBurnedCalories(burnedCalories: BurnedCalories)

    suspend fun getBurnedCaloriesForDate(date: LocalDate): Int

    suspend fun upsertWeightEntry(weightEntry: WeightEntry)

    suspend fun getWeightEntryForDate(date: LocalDate): WeightEntry?

    fun getAllWeightEntries(): Flow<List<WeightEntry>>
}