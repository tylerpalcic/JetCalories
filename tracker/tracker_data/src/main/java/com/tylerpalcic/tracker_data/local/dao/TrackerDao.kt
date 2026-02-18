package com.tylerpalcic.tracker_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tylerpalcic.tracker_data.local.entity.BurnedCaloriesEntity
import com.tylerpalcic.tracker_data.local.entity.TrackedFoodEntity
import com.tylerpalcic.tracker_data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedFood(trackedFoodEntity: TrackedFoodEntity)

    @Delete
    suspend fun deleteTrackedFood(trackedFoodEntity: TrackedFoodEntity)

    @Query(
        """
            SELECT *
            FROM trackedfoodentity
            WHERE dayOfMonth = :day AND month = :month AND year = :year
        """
    )
    fun getFoodsForDate(day: Int, month: Int, year: Int): Flow<List<TrackedFoodEntity>>

    @Query(
        """
            SELECT * FROM trackedfoodentity
            WHERE type = :mealType
            GROUP BY name
            ORDER BY year DESC, month DESC, dayOfMonth DESC
            LIMIT :limit
        """
    )
    suspend fun getRecentFoodsForMealType(mealType: String, limit: Int = 20): List<TrackedFoodEntity>

    @Query(
        """
            SELECT * FROM trackedfoodentity
            WHERE dayOfMonth = :day AND month = :month AND year = :year AND type = :mealType
        """
    )
    suspend fun getFoodsForDateAndMealType(day: Int, month: Int, year: Int, mealType: String): List<TrackedFoodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBurnedCalories(entity: BurnedCaloriesEntity)

    @Query(
        """
            SELECT * FROM burnedcaloriesentity
            WHERE dayOfMonth = :day AND month = :month AND year = :year
        """
    )
    suspend fun getBurnedCaloriesForDate(day: Int, month: Int, year: Int): BurnedCaloriesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeightEntry(entity: WeightEntryEntity)

    @Query(
        """
            SELECT * FROM weightentryentity
            WHERE dayOfMonth = :day AND month = :month AND year = :year
        """
    )
    suspend fun getWeightEntryForDate(day: Int, month: Int, year: Int): WeightEntryEntity?

    @Query("SELECT * FROM weightentryentity ORDER BY year ASC, month ASC, dayOfMonth ASC")
    fun getAllWeightEntries(): Flow<List<WeightEntryEntity>>
}