package com.atitienei_daniel.tracker_domain.use_case

data class TrackerUseCases(
    val searchFood: SearchFood,
    val deleteTrackedFood: DeleteTrackedFood,
    val trackFood: TrackFood,
    val getFoodsForDate: GetFoodsForDate,
    val calculateMealNutrients: CalculateMealNutrients,
    val getRecentFoods: GetRecentFoods,
    val copyMealFromDate: CopyMealFromDate,
    val upsertBurnedCalories: UpsertBurnedCalories,
    val getBurnedCaloriesForDate: GetBurnedCaloriesForDate
)