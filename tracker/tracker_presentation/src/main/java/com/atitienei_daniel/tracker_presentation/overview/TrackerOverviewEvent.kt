package com.atitienei_daniel.tracker_presentation.overview

import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackedFood

sealed interface TrackerOverviewEvent {
    object OnNextDayClick: TrackerOverviewEvent
    object OnPreviousDayClick: TrackerOverviewEvent
    data class OnToggleMealClick(val meal: Meal): TrackerOverviewEvent
    data class OnDeleteTrackedFoodClick(val trackedFood: TrackedFood): TrackerOverviewEvent
    data class OnShowRecentFoods(val mealType: MealType): TrackerOverviewEvent
    object OnDismissRecentSheet: TrackerOverviewEvent
    data class OnAddRecentFood(val food: TrackedFood): TrackerOverviewEvent
    data class OnCopyYesterdayMeal(val mealType: MealType): TrackerOverviewEvent
    object OnBurnedCaloriesClick: TrackerOverviewEvent
    data class OnBurnedCaloriesEnter(val calories: String): TrackerOverviewEvent
    object OnDismissBurnedCaloriesDialog: TrackerOverviewEvent
}