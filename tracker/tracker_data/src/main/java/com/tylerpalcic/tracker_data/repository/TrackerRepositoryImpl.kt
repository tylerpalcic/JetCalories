package com.tylerpalcic.tracker_data.repository

import android.util.Log
import com.tylerpalcic.tracker_data.local.dao.TrackerDao
import com.tylerpalcic.tracker_data.mapper.toDomain
import com.tylerpalcic.tracker_data.mapper.toEntity
import com.tylerpalcic.tracker_domain.model.BurnedCalories
import com.tylerpalcic.tracker_data.mapper.toTrackableFood
import com.tylerpalcic.tracker_data.mapper.toTrackedFood
import com.tylerpalcic.tracker_data.remote.OpenFoodApi
import com.tylerpalcic.tracker_domain.model.MealType
import com.tylerpalcic.tracker_domain.model.TrackableFood
import com.tylerpalcic.tracker_domain.model.TrackedFood
import com.tylerpalcic.tracker_domain.model.WeightEntry
import com.tylerpalcic.tracker_domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TrackerRepositoryImpl @Inject constructor(
    private val dao: TrackerDao,
    private val api: OpenFoodApi
) : TrackerRepository {
    override suspend fun searchFood(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<TrackableFood>> =
        try {
            val searchDto = api.searchFood(
                query = query,
                page = page,
                pageSize = pageSize
            )
            Result.success(
                searchDto.products.mapNotNull { it.toTrackableFood() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    override suspend fun insertTrackedFood(food: TrackedFood) {
        dao.insertTrackedFood(food.toEntity())
    }

    override suspend fun deleteTrackedFood(food: TrackedFood) {
        dao.deleteTrackedFood(food.toEntity())
    }

    override fun getFoodsForDate(localDate: LocalDate): Flow<List<TrackedFood>> =
        dao.getFoodsForDate(
            day = localDate.dayOfMonth,
            month = localDate.monthValue,
            year = localDate.year
        ).map { entities ->
            Log.d("entities", entities.toString())
            entities.map { it.toTrackedFood() }
        }

    override suspend fun getRecentFoodsForMealType(mealType: MealType, limit: Int): List<TrackedFood> =
        dao.getRecentFoodsForMealType(mealType.name, limit).map { it.toTrackedFood() }

    override suspend fun getFoodsForDateAndMealType(date: LocalDate, mealType: MealType): List<TrackedFood> =
        dao.getFoodsForDateAndMealType(
            day = date.dayOfMonth,
            month = date.monthValue,
            year = date.year,
            mealType = mealType.name
        ).map { it.toTrackedFood() }

    override suspend fun upsertBurnedCalories(burnedCalories: BurnedCalories) {
        val existing = dao.getBurnedCaloriesForDate(
            day = burnedCalories.date.dayOfMonth,
            month = burnedCalories.date.monthValue,
            year = burnedCalories.date.year
        )
        dao.upsertBurnedCalories(
            burnedCalories.toEntity().copy(id = existing?.id)
        )
    }

    override suspend fun getBurnedCaloriesForDate(date: LocalDate): Int =
        dao.getBurnedCaloriesForDate(
            day = date.dayOfMonth,
            month = date.monthValue,
            year = date.year
        )?.calories ?: 0

    override suspend fun upsertWeightEntry(weightEntry: WeightEntry) {
        val existing = dao.getWeightEntryForDate(
            day = weightEntry.date.dayOfMonth,
            month = weightEntry.date.monthValue,
            year = weightEntry.date.year
        )
        dao.upsertWeightEntry(
            weightEntry.toEntity().copy(id = existing?.id)
        )
    }

    override suspend fun getWeightEntryForDate(date: LocalDate): WeightEntry? =
        dao.getWeightEntryForDate(
            day = date.dayOfMonth,
            month = date.monthValue,
            year = date.year
        )?.toDomain()

    override fun getAllWeightEntries(): Flow<List<WeightEntry>> =
        dao.getAllWeightEntries().map { entities ->
            entities.map { it.toDomain() }
        }
}