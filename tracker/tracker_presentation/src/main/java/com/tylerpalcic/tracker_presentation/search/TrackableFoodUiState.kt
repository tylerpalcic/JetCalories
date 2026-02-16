package com.tylerpalcic.tracker_presentation.search

import com.tylerpalcic.tracker_domain.model.TrackableFood

data class TrackableFoodUiState(
    val food: TrackableFood,
    val isExpanded: Boolean = false,
    val amount: String = ""
)