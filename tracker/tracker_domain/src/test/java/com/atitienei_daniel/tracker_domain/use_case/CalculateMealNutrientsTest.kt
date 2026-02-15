package com.atitienei_daniel.tracker_domain.use_case

import com.atitienei_daniel.core.domain.model.ActivityLevel
import com.atitienei_daniel.core.domain.model.Gender
import com.atitienei_daniel.core.domain.model.GoalType
import com.atitienei_daniel.core.domain.model.UserInfo
import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackedFood
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CalculateMealNutrientsTest {

    private lateinit var calculateMealNutrients: CalculateMealNutrients

    @Before
    fun setUp() {
        calculateMealNutrients = CalculateMealNutrients()
    }

    @Test
    fun `male medium activity lose weight gives correct calorie goal`() {
        // Male, 30y, 180 lbs, 70 in, Medium, LoseWeight
        // BMR = 4.536*180 + 15.875*70 - 5*30 + 5 = 1782.73 -> 1783
        // TDEE = 1783 * 1.375 = 2451.625 -> 2452
        // Goal = 2452 - 750 = 1702
        val userInfo = UserInfo(
            gender = Gender.Male,
            age = 30,
            weight = 180f,
            height = 70,
            activityLevel = ActivityLevel.Medium,
            goalType = GoalType.LoseWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f
        )
        val result = calculateMealNutrients.execute(emptyList(), userInfo)
        assertThat(result.caloriesGoal).isEqualTo(1702)
    }

    @Test
    fun `female low activity keep weight gives correct calorie goal`() {
        // Female, 25y, 140 lbs, 65 in, Low, KeepWeight
        // BMR = 4.536*140 + 15.875*65 - 5*25 - 161 = 635 + 1032 - 125 - 161 = 1381
        // TDEE = 1381 * 1.2 = 1657 (rounded)
        // Goal = 1657 + 0 = 1657
        val userInfo = UserInfo(
            gender = Gender.Female,
            age = 25,
            weight = 140f,
            height = 65,
            activityLevel = ActivityLevel.Low,
            goalType = GoalType.KeepWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f
        )
        val result = calculateMealNutrients.execute(emptyList(), userInfo)
        assertThat(result.caloriesGoal).isEqualTo(1657)
    }

    @Test
    fun `male high activity gain weight gives correct calorie goal`() {
        // Male, 22y, 160 lbs, 72 in, High, GainWeight
        // BMR = 4.536*160 + 15.875*72 - 5*22 + 5 = 725.76 + 1143 - 110 + 5 = 1763.76
        // TDEE = 1763.76 * 1.55 = 2733.8 -> 2734
        // Goal = 2734 + 750 = 3484
        val userInfo = UserInfo(
            gender = Gender.Male,
            age = 22,
            weight = 160f,
            height = 72,
            activityLevel = ActivityLevel.High,
            goalType = GoalType.GainWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f
        )
        val result = calculateMealNutrients.execute(emptyList(), userInfo)
        assertThat(result.caloriesGoal).isEqualTo(3484)
    }

    @Test
    fun `macro goals are calculated from calorie goal and ratios`() {
        val userInfo = UserInfo(
            gender = Gender.Male,
            age = 30,
            weight = 180f,
            height = 70,
            activityLevel = ActivityLevel.Medium,
            goalType = GoalType.LoseWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f
        )
        val result = calculateMealNutrients.execute(emptyList(), userInfo)
        // 1702 * 0.4 / 4 = 170.2 -> 170g carbs
        // 1702 * 0.3 / 4 = 127.65 -> 128g protein
        // 1702 * 0.3 / 9 = 56.73 -> 57g fat
        assertThat(result.carbsGoal).isEqualTo(170)
        assertThat(result.proteinGoal).isEqualTo(128)
        assertThat(result.fatGoal).isEqualTo(57)
    }

    @Test
    fun `meal nutrient totals are summed correctly`() {
        val userInfo = UserInfo(
            gender = Gender.Male,
            age = 30,
            weight = 180f,
            height = 70,
            activityLevel = ActivityLevel.Medium,
            goalType = GoalType.KeepWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f
        )
        val foods = listOf(
            TrackedFood(
                name = "Chicken", carbs = 0, protein = 30, fat = 5, calories = 165,
                mealType = MealType.Lunch, imageUrl = null, amount = 100,
                date = LocalDate.now(), id = 1
            ),
            TrackedFood(
                name = "Rice", carbs = 45, protein = 4, fat = 0, calories = 200,
                mealType = MealType.Lunch, imageUrl = null, amount = 150,
                date = LocalDate.now(), id = 2
            )
        )
        val result = calculateMealNutrients.execute(foods, userInfo)
        assertThat(result.totalCarbs).isEqualTo(45)
        assertThat(result.totalProtein).isEqualTo(34)
        assertThat(result.totalFat).isEqualTo(5)
        assertThat(result.totalCalories).isEqualTo(365)
        val lunchNutrients = result.mealNutrients[MealType.Lunch]!!
        assertThat(lunchNutrients.calories).isEqualTo(365)
    }
}
