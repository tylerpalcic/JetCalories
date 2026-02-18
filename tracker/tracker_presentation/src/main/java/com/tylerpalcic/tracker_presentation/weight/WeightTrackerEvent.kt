package com.tylerpalcic.tracker_presentation.weight

sealed interface WeightTrackerEvent {
    object OnAddWeightClick : WeightTrackerEvent
    data class OnWeightEnter(val weight: String) : WeightTrackerEvent
    object OnDismissWeightDialog : WeightTrackerEvent
    object OnToggleTimeRange : WeightTrackerEvent
}
