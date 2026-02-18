package com.tylerpalcic.tracker_data.mapper

import com.tylerpalcic.tracker_data.local.entity.WeightEntryEntity
import com.tylerpalcic.tracker_domain.model.WeightEntry
import java.time.LocalDate

fun WeightEntry.toEntity(): WeightEntryEntity =
    WeightEntryEntity(
        weight = weight,
        dayOfMonth = date.dayOfMonth,
        month = date.monthValue,
        year = date.year
    )

fun WeightEntryEntity.toDomain(): WeightEntry =
    WeightEntry(
        weight = weight,
        date = LocalDate.of(year, month, dayOfMonth)
    )
