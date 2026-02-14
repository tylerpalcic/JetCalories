# Develop Branch Bug Fixes — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Fix all known bugs on the develop branch — broken onboarding FAB wiring and AddFoodItem screen issues.

**Architecture:** Shared callback pattern for FAB-to-ViewModel communication; targeted fixes for AddFoodItem with proper state management, validation, and route arguments.

**Tech Stack:** Kotlin, Jetpack Compose, Hilt, Navigation Compose, Room, DataStore

---

### Task 1: Fix Onboarding FAB — Wire FAB to ViewModel via Shared Callback

**Files:**
- Modify: `app/src/main/java/com/atitienei_daniel/jetcalories/MainActivity.kt`

**Context:** The FAB in MainActivity navigates directly via `navController.navigate()`, bypassing each screen's ViewModel. The ViewModel saves data to DataStore and validates input. We need the FAB to trigger the ViewModel's `onNextClick()` instead.

The screens (`GenderScreen`, `AgeScreen`) already have `LaunchedEffect` blocks that collect `viewModel.uiEvent` and call their `onNextClick` lambda (which navigates) when `UiEvent.Navigate` is received. So the chain is: FAB → ViewModel.onNextClick() → saves to DataStore → emits UiEvent.Navigate → screen's LaunchedEffect calls navigation lambda.

**Step 1: Add a shared `fabOnClick` state and wire it**

In `MainActivity.kt`, add a `var fabOnClick` state before the `Scaffold`. Modify the FAB to call it. Modify each screen's composable destination to set it.

Replace the FAB block (lines 113-148) and the Gender/Age composable destinations with:

```kotlin
// Before the Scaffold, after navController/currentRoute declarations:
var fabOnClick by remember { mutableStateOf<(() -> Unit)?>(null) }

// FAB:
floatingActionButton = {
    if (currentRoute in listOf(Route.Gender.route, Route.Age.route)) {
        ExtendedFloatingActionButton(
            text = { Text(text = "Next") },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = "Next"
                )
            },
            onClick = { fabOnClick?.invoke() }
        )
    }
}

// Gender composable destination:
composable(Route.Gender.route) {
    val genderViewModel: GenderViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        fabOnClick = genderViewModel::onNextClick
    }

    GenderScreen(
        onNextClick = {
            navController.navigate(Route.Age.route)
        },
        viewModel = genderViewModel
    )
}

// Age composable destination:
composable(Route.Age.route) {
    val ageViewModel: AgeViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        fabOnClick = ageViewModel::onNextClick
    }

    AgeScreen(
        onNextClick = {
            navController.navigate(Route.Height.route)
        },
        onShowSnackbar = { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        },
        viewModel = ageViewModel
    )
}
```

Add required imports:
```kotlin
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
```

**Step 2: Add `mutableStateOf` and `setValue` imports if not already present**

Check existing imports — `mutableStateOf` and `setValue` may already be imported. `remember` is already imported. Add only what's missing.

**Step 3: Verify the GenderScreen and AgeScreen accept an explicit viewModel parameter**

Both already have `viewModel: GenderViewModel = hiltViewModel()` / `viewModel: AgeViewModel = hiltViewModel()` as default parameters, so passing explicitly will work. No changes needed to screen files.

**Step 4: Remove the commented-out `onSkipClick` in GoalScreen destination**

Delete lines 202-204 (the commented-out `onSkipClick` block) — dead code.

**Step 5: Commit**

```bash
git add app/src/main/java/com/atitienei_daniel/jetcalories/MainActivity.kt
git commit -m "Fix onboarding FAB to save data via ViewModel before navigating"
```

---

### Task 2: Move AddFoodItem Files to Correct Package

**Files:**
- Move: `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/components/AddFoodItemScreen.kt` → `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemScreen.kt`
- Move: `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/components/AddFoodItemViewModel.kt` → `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemViewModel.kt`
- Modify: `app/src/main/java/com/atitienei_daniel/jetcalories/MainActivity.kt` (update import)

**Step 1: Create the target directory**

```bash
mkdir -p tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item
```

**Step 2: Move files and update package declarations**

```bash
git mv tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/components/AddFoodItemScreen.kt tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemScreen.kt
git mv tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/components/AddFoodItemViewModel.kt tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemViewModel.kt
```

**Step 3: Update package declaration in both files**

In both files, change:
```kotlin
package com.atitienei_daniel.tracker_presentation.overview.components
```
to:
```kotlin
package com.atitienei_daniel.tracker_presentation.add_food_item
```

**Step 4: Update the import in MainActivity.kt**

Change:
```kotlin
import com.atitienei_daniel.tracker_presentation.overview.components.AddFoodItemScreen
```
to:
```kotlin
import com.atitienei_daniel.tracker_presentation.add_food_item.AddFoodItemScreen
```

**Step 5: Commit**

```bash
git add -A tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/ tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/components/ app/src/main/java/com/atitienei_daniel/jetcalories/MainActivity.kt
git commit -m "Move AddFoodItem files to dedicated add_food_item package"
```

---

### Task 3: Add Date Route Argument to AddFoodItem

**Files:**
- Modify: `app/src/main/java/com/atitienei_daniel/jetcalories/navigation/Route.kt`
- Modify: `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/TrackerOverviewScreen.kt`
- Modify: `app/src/main/java/com/atitienei_daniel/jetcalories/MainActivity.kt`

**Context:** The Search route already passes `dayOfMonth/month/year` as three ints. We'll follow the same pattern for AddFoodItem for consistency.

**Step 1: Update Route.AddFoodItem to include date parameters**

In `Route.kt`, replace:
```kotlin
object AddFoodItem : Route("add_food_item/{mealName}") {
    fun createRoute(mealName: String) = "add_food_item/$mealName"
}
```
with:
```kotlin
object AddFoodItem : Route("add_food_item/{mealName}/{dayOfMonth}/{month}/{year}") {
    fun createRoute(
        mealName: String,
        dayOfMonth: Int,
        month: Int,
        year: Int
    ) = "add_food_item/$mealName/$dayOfMonth/$month/$year"
}
```

**Step 2: Update TrackerOverviewScreen navigation callback**

Change the `onNavigateToAddItem` parameter type from `(String) -> Unit` to `(String, Int, Int, Int) -> Unit` and pass date components:

In `TrackerOverviewScreen.kt`, change:
```kotlin
onNavigateToAddItem: (String) -> Unit,
```
to:
```kotlin
onNavigateToAddItem: (String, Int, Int, Int) -> Unit,
```

And change the call:
```kotlin
onNavigateToAddItem(meal.name)
```
to:
```kotlin
onNavigateToAddItem(
    meal.name,
    uiState.date.dayOfMonth,
    uiState.date.monthValue,
    uiState.date.year
)
```

**Step 3: Update MainActivity wiring**

In the `TrackerOverviewScreen` composable destination, change:
```kotlin
onNavigateToAddItem = { mealName ->
    navController.navigate(Route.AddFoodItem.createRoute(mealName))
}
```
to:
```kotlin
onNavigateToAddItem = { mealName, dayOfMonth, month, year ->
    navController.navigate(
        Route.AddFoodItem.createRoute(
            mealName = mealName,
            dayOfMonth = dayOfMonth,
            month = month,
            year = year
        )
    )
}
```

In the `AddFoodItem` composable destination, add the nav arguments and pass them to the screen:
```kotlin
composable(
    route = Route.AddFoodItem.route,
    arguments = listOf(
        navArgument("mealName") { type = NavType.StringType },
        navArgument("dayOfMonth") { type = NavType.IntType },
        navArgument("month") { type = NavType.IntType },
        navArgument("year") { type = NavType.IntType },
    )
) {
    AddFoodItemScreen(
        addItemOnClick = {
            navController.popBackStack()
        },
        onItemAdded = { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    )
}
```

**Step 4: Commit**

```bash
git add app/src/main/java/com/atitienei_daniel/jetcalories/navigation/Route.kt app/src/main/java/com/atitienei_daniel/jetcalories/MainActivity.kt tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/overview/TrackerOverviewScreen.kt
git commit -m "Add date parameters to AddFoodItem route for correct date tracking"
```

---

### Task 4: Fix AddFoodItemViewModel — Read Route Args, Add Validation, Fix Amount Bug

**Files:**
- Modify: `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemViewModel.kt`

**Context:** Three bugs to fix:
1. `mealName` from route is ignored — always defaults to Breakfast
2. Date is hardcoded to `LocalDate.now()` instead of the selected date
3. `amount = 1` causes `TrackFood` to divide by 100 and multiply by 1, turning "500 calories" into 5. Fix: use `amount = 100` so the math is `(value / 100) * 100 = value`.

**Step 1: Rewrite the ViewModel with SavedStateHandle, state, and validation**

Replace the entire contents of `AddFoodItemViewModel.kt`:

```kotlin
package com.atitienei_daniel.tracker_presentation.add_food_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atitienei_daniel.core.util.UiEvent
import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackableFood
import com.atitienei_daniel.tracker_domain.use_case.TrackerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddFoodItemViewModel @Inject constructor(
    private val trackerUseCases: TrackerUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val initialMealType: MealType = savedStateHandle.get<String>("mealName")
        ?.let { name ->
            MealType.entries.find {
                it.name.equals(name, ignoreCase = true)
            }
        } ?: MealType.BreakFast

    private val date: LocalDate = run {
        val dayOfMonth = savedStateHandle.get<Int>("dayOfMonth") ?: LocalDate.now().dayOfMonth
        val month = savedStateHandle.get<Int>("month") ?: LocalDate.now().monthValue
        val year = savedStateHandle.get<Int>("year") ?: LocalDate.now().year
        LocalDate.of(year, month, dayOfMonth)
    }

    fun onAddItem(
        name: String,
        calories: String,
        protein: String,
        carbs: String,
        fat: String,
        mealType: MealType
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackBar("Name can't be empty."))
                return@launch
            }

            val caloriesInt = calories.toIntOrNull()
            val proteinInt = protein.toIntOrNull()
            val carbsInt = carbs.toIntOrNull()
            val fatInt = fat.toIntOrNull()

            if (caloriesInt == null || proteinInt == null || carbsInt == null || fatInt == null) {
                _uiEvent.send(UiEvent.ShowSnackBar("Please enter valid numbers."))
                return@launch
            }

            if (caloriesInt < 0 || proteinInt < 0 || carbsInt < 0 || fatInt < 0) {
                _uiEvent.send(UiEvent.ShowSnackBar("Values can't be negative."))
                return@launch
            }

            trackerUseCases.trackFood.execute(
                TrackableFood(
                    name = name,
                    imageUrl = null,
                    caloriesPer100g = caloriesInt,
                    proteinPer100g = proteinInt,
                    fatsPer100g = fatInt,
                    carbsPer100g = carbsInt,
                ),
                amount = 100,
                mealType = mealType,
                date = date
            )
            _uiEvent.send(UiEvent.ShowSnackBar("Food item added successfully."))
            _uiEvent.send(UiEvent.NavigateUp)
        }
    }
}
```

Key changes:
- `SavedStateHandle` injected to read `mealName`, `dayOfMonth`, `month`, `year`
- `initialMealType` exposed for the screen to use as dropdown default
- `date` computed from route args with fallback to today
- `onAddItem` now takes `String` params (not pre-parsed `Int`) and validates them
- `amount = 100` fixes the per-100g math bug
- Uses `Channel` + `receiveAsFlow()` matching the project's ViewModel convention
- Emits `UiEvent.NavigateUp` after successful add so the screen navigates back

**Step 2: Commit**

```bash
git add tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemViewModel.kt
git commit -m "Fix AddFoodItemViewModel: read route args, add validation, fix amount bug"
```

---

### Task 5: Fix AddFoodItemScreen — Use ViewModel State, LocalSpacing, Handle NavigateUp

**Files:**
- Modify: `tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemScreen.kt`

**Step 1: Rewrite the screen to use ViewModel's initialMealType, LocalSpacing, and handle NavigateUp**

Replace the entire contents of `AddFoodItemScreen.kt`:

```kotlin
package com.atitienei_daniel.tracker_presentation.add_food_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.atitienei_daniel.core.util.UiEvent
import com.atitienei_daniel.core_ui.LocalSpacing
import com.atitienei_daniel.tracker_domain.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodItemScreen(
    viewModel: AddFoodItemViewModel = hiltViewModel(),
    onItemAdded: (String) -> Unit,
    addItemOnClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMeal by remember { mutableStateOf(viewModel.initialMealType) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> onItemAdded(event.message)
                is UiEvent.NavigateUp -> addItemOnClick()
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Food Item")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(spacing.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text(text = "Calories") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text(text = "Protein") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text(text = "Carbs") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text(text = "Fat") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedMeal.name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Meal") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    MealType.entries.forEach { meal ->
                        DropdownMenuItem(
                            text = { Text(meal.name) },
                            onClick = {
                                selectedMeal = meal
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.onAddItem(
                        name = name,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        mealType = selectedMeal
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Item")
            }
        }
    }
}
```

Key changes:
- Uses `LocalSpacing.current` instead of hardcoded dp values
- `selectedMeal` initialized from `viewModel.initialMealType` (reads route arg)
- Text fields default to empty string (not "0") for better UX
- `onAddItem` passes strings — ViewModel handles parsing and validation
- Handles `UiEvent.NavigateUp` to pop back stack after successful add
- Removed `AlertDialog` import (unused)
- Uses `MealType.entries` instead of hardcoded list

**Step 2: Check that `UiEvent.NavigateUp` exists**

Verify `UiEvent` sealed interface includes `NavigateUp`. If not, it needs to be added.

Check: `core/src/main/java/com/atitienei_daniel/core/util/UiEvent.kt`

If `NavigateUp` is missing, add:
```kotlin
object NavigateUp : UiEvent
```

**Step 3: Commit**

```bash
git add tracker/tracker_presentation/src/main/java/com/atitienei_daniel/tracker_presentation/add_food_item/AddFoodItemScreen.kt core/src/main/java/com/atitienei_daniel/core/util/UiEvent.kt
git commit -m "Fix AddFoodItemScreen: use LocalSpacing, initial meal type, validation UX"
```

---

### Task 6: Verify Build Compiles

**Step 1: Run a Gradle build to verify everything compiles**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL

**Step 2: Fix any compilation errors**

If there are errors, fix them based on the error messages. Common issues might be:
- Missing imports
- `MealType.entries` requires Kotlin 1.9+ — if not available, use `MealType.values().toList()`

**Step 3: Final commit if any fixes were needed**

```bash
git add -A
git commit -m "Fix compilation errors from bug fix changes"
```
