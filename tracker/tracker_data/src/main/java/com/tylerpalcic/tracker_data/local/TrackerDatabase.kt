package com.tylerpalcic.tracker_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tylerpalcic.tracker_data.local.dao.TrackerDao
import com.tylerpalcic.tracker_data.local.entity.BurnedCaloriesEntity
import com.tylerpalcic.tracker_data.local.entity.TrackedFoodEntity
import com.tylerpalcic.tracker_data.local.entity.WeightEntryEntity

@Database(
    entities = [TrackedFoodEntity::class, BurnedCaloriesEntity::class, WeightEntryEntity::class],
    version = 3,
    exportSchema = true
)
abstract class TrackerDatabase : RoomDatabase() {

    abstract val dao: TrackerDao
}