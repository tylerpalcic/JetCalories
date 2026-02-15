package com.atitienei_daniel.tracker_domain.use_case

import com.atitienei_daniel.core.domain.model.ActivityLevel
import com.atitienei_daniel.core.domain.model.Gender
import com.atitienei_daniel.core.domain.model.GoalType
import com.atitienei_daniel.core.domain.model.UserInfo
import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackedFood
import kotlin.math.roundToInt

class CalculateMealNutrients(

) {
    fun execute(trackedFoods: List<TrackedFood>, userInfo: UserInfo): Result {
        val allNutrients = trackedFoods
            .groupBy { it.mealType }
            .mapValues { entry ->
                val type = entry.key
                val foods = entry.value
                MealNutrients(
                    carbs = foods.sumOf { it.carbs },
                    protein = foods.sumOf { it.protein },
                    fat = foods.sumOf { it.fat },
                    calories = foods.sumOf { it.calories },
                    mealType = type
                )
            }

        val totalCarbs = allNutrients.values.sumOf { it.carbs }
        val totalProtein = allNutrients.values.sumOf { it.protein }
        val totalFat = allNutrients.values.sumOf { it.fat }
        val totalCalories = allNutrients.values.sumOf { it.calories }

        val calorieGoal = dailyCalorieRequirement(userInfo)
        val carbsGoal = (calorieGoal * userInfo.carbRatio / 4f).roundToInt()
        val proteinGoal = (calorieGoal * userInfo.proteinRatio / 4f).roundToInt()
        val fatGoal = (calorieGoal * userInfo.fatRatio / 9f).roundToInt()

        return Result(
            carbsGoal = carbsGoal,
            proteinGoal = proteinGoal,
            fatGoal = fatGoal,
            caloriesGoal = calorieGoal,
            totalCarbs = totalCarbs,
            totalProtein = totalProtein,
            totalFat = totalFat,
            totalCalories = totalCalories,
            mealNutrients = allNutrients
        )
    }

    private fun bmr(userInfo: UserInfo): Int {
    return when (userInfo.gender) {
        is Gender.Male -> {
            (4.536f * userInfo.weight + 15.875f * userInfo.height -
                    5f * userInfo.age + 5f).roundToInt()
        }
        is Gender.Female -> {
            (4.536f * userInfo.weight + 15.875f * userInfo.height -
                    5f * userInfo.age - 161f).roundToInt()
        }
    }
}

    private fun dailyCalorieRequirement(userInfo: UserInfo): Int {
    val activityFactor = when (userInfo.activityLevel) {
        is ActivityLevel.Low -> 1.2f
        is ActivityLevel.Medium -> 1.375f
        is ActivityLevel.High -> 1.55f
    }
    val calorieExtra = when (userInfo.goalType) {
        is GoalType.LoseWeight -> -750
        is GoalType.KeepWeight -> 0
        is GoalType.GainWeight -> 750
    }
    return (bmr(userInfo) * activityFactor + calorieExtra).roundToInt()
}

    data class MealNutrients(
        val carbs: Int,
        val protein: Int,
        val fat: Int,
        val calories: Int,
        val mealType: MealType
    )

    data class Result(
        val carbsGoal: Int,
        val proteinGoal: Int,
        val fatGoal: Int,
        val caloriesGoal: Int,
        val totalCarbs: Int,
        val totalProtein: Int,
        val totalFat: Int,
        val totalCalories: Int,
        val mealNutrients: Map<MealType, MealNutrients>
    )
}