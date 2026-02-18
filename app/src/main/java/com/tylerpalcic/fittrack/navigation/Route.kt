package com.tylerpalcic.fittrack.navigation

sealed class Route(val route: String) {
    object Welcome : Route("welcome")
    object Age : Route("age")
    object Gender : Route("gender")
    object Height : Route("height")
    object Weight : Route("weight")
    object NutrientGoal : Route("nutrient_goal")
    object ActivityLevel : Route("activity")
    object Goal : Route("goal")
    object TrackerOverview : Route("tracker_overview")
    object WeightTracker : Route("weight_tracker")

    object Search : Route("search/{mealName}/{dayOfMonth}/{month}/{year}") {
        fun createRoute(
            mealName: String,
            dayOfMonth: Int,
            month: Int,
            year: Int
        ) = "search/$mealName/$dayOfMonth/$month/$year"
    }

    object AddFoodItem : Route("add_food_item/{mealName}/{dayOfMonth}/{month}/{year}") {
        fun createRoute(
            mealName: String,
            dayOfMonth: Int,
            month: Int,
            year: Int
        ) = "add_food_item/$mealName/$dayOfMonth/$month/$year"
    }
}