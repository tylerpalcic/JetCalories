# Fix BMR/TDEE Calculation Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Fix the calorie goal calculation by switching to Mifflin-St Jeor (imperial), correcting activity multipliers, and updating the goal offset.

**Architecture:** Single use case class edit (`CalculateMealNutrients`). Two private functions change (`bmr`, `dailyCalorieRequirement`). Public API and data classes are unchanged.

**Tech Stack:** Kotlin, JUnit 4, Google Truth

---

### Task 1: Write unit tests for BMR and daily calorie requirement

**Files:**
- Create: `tracker/tracker_domain/src/test/java/com/tylerpalcic/tracker_domain/use_case/CalculateMealNutrientsTest.kt`

**Step 1: Create test file with BMR and TDEE tests**

The tests call the public `execute()` method and assert on `caloriesGoal` in the result, which exercises both `bmr()` and `dailyCalorieRequirement()` internally.

```kotlin
package com.tylerpalcic.tracker_domain.use_case

import com.tylerpalcic.core.domain.model.ActivityLevel
import com.tylerpalcic.core.domain.model.Gender
import com.tylerpalcic.core.domain.model.GoalType
import com.tylerpalcic.core.domain.model.UserInfo
import com.tylerpalcic.tracker_domain.model.MealType
import com.tylerpalcic.tracker_domain.model.TrackedFood
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
        // BMR = 4.536*180 + 15.875*70 - 5*30 + 5 = 1782
        // TDEE = 1782 * 1.375 = 2450 (rounded)
        // Goal = 2450 - 750 = 1700
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
        assertThat(result.caloriesGoal).isEqualTo(1700)
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
        // 1700 * 0.4 / 4 = 170g carbs
        // 1700 * 0.3 / 4 = 128g protein (127.5 rounds to 128)
        // 1700 * 0.3 / 9 = 57g fat (56.67 rounds to 57)
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
```

**Step 2: Run tests to verify they fail**

Run: `./gradlew :tracker:tracker_domain:testDebugUnitTest --tests "*.CalculateMealNutrientsTest" --info`
Expected: 3 of 5 tests FAIL (the calorie goal tests fail because the old formula produces wrong numbers; the totals test and macro test may also fail due to wrong calorie goal).

**Step 3: Commit the failing tests**

```bash
git add tracker/tracker_domain/src/test/java/com/tylerpalcic/tracker_domain/use_case/CalculateMealNutrientsTest.kt
git commit -m "Add tests for CalculateMealNutrients calorie goal calculation"
```

---

### Task 2: Fix `bmr()` and `dailyCalorieRequirement()` in CalculateMealNutrients

**Files:**
- Modify: `tracker/tracker_domain/src/main/java/com/tylerpalcic/tracker_domain/use_case/CalculateMealNutrients.kt` — `bmr()` function (lines 53-63) and `dailyCalorieRequirement()` function (lines 65-75)

**Step 1: Replace `bmr()` with Mifflin-St Jeor (imperial)**

Replace the existing `bmr` function body with:

```kotlin
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
```

**Step 2: Replace `dailyCalorieRequirement()` with corrected multipliers and offset**

Replace the existing `dailyCalorieRequirement` function body with:

```kotlin
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
```

**Step 3: Run tests to verify they pass**

Run: `./gradlew :tracker:tracker_domain:testDebugUnitTest --tests "*.CalculateMealNutrientsTest" --info`
Expected: All 5 tests PASS.

**Step 4: Commit**

```bash
git add tracker/tracker_domain/src/main/java/com/tylerpalcic/tracker_domain/use_case/CalculateMealNutrients.kt
git commit -m "Fix BMR calculation: switch to Mifflin-St Jeor with imperial units"
```

---

### Task 3: Full build verification

**Step 1: Run full debug compilation**

Run: `./gradlew compileDebugKotlin`
Expected: BUILD SUCCESSFUL

**Step 2: Run all unit tests**

Run: `./gradlew testDebugUnitTest`
Expected: BUILD SUCCESSFUL, all tests pass.
