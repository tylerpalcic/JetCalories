package com.tylerpalcic.tracker_presentation.weight

import com.tylerpalcic.tracker_domain.model.WeightEntry

data class WeightTrackerState(
    val entries: List<WeightEntry> = emptyList(),
    val latestWeight: Float? = null,
    val showAllTime: Boolean = false,
    val showWeightDialog: Boolean = false
)
