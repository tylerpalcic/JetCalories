package com.tylerpalcic.tracker_data.mapper

import com.tylerpalcic.tracker_data.local.entity.BurnedCaloriesEntity
import com.tylerpalcic.tracker_domain.model.BurnedCalories
import java.time.LocalDate

fun BurnedCalories.toEntity(): BurnedCaloriesEntity =
    BurnedCaloriesEntity(
        calories = calories,
        dayOfMonth = date.dayOfMonth,
        month = date.monthValue,
        year = date.year
    )

fun BurnedCaloriesEntity.toBurnedCalories(): BurnedCalories =
    BurnedCalories(
        calories = calories,
        date = LocalDate.of(year, month, dayOfMonth)
    )
