package com.atitienei_daniel.tracker_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atitienei_daniel.tracker_data.local.entity.TrackedFoodEntity
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
}