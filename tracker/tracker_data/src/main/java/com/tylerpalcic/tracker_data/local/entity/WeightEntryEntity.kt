package com.tylerpalcic.tracker_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val weight: Float,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int
)
