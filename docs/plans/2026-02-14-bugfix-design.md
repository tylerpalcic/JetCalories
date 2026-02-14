# Bug Fix Design — Develop Branch

## Summary

Fix all known bugs on the `develop` branch: broken onboarding FAB wiring and AddFoodItem screen issues.

## Bug Category 1: Onboarding FAB in MainActivity

### Problem

The FAB in `MainActivity` navigates directly via `navController.navigate()`, bypassing each onboarding screen's ViewModel. This means gender/age are not saved to DataStore and age validation is skipped.

### Solution: Shared Callback Pattern

Each onboarding screen's `onNextClick` callback is hoisted so the FAB can invoke it. The flow becomes:

1. FAB tap calls the screen's `onNextClick` lambda
2. ViewModel validates input and saves to DataStore
3. ViewModel emits `UiEvent.Navigate` (or `UiEvent.ShowSnackBar` on error)
4. Screen's `LaunchedEffect` calls the navigation lambda

### Files Changed

- `MainActivity.kt` — FAB `onClick` calls hoisted `onNextClick` instead of navigating directly
- `GenderScreen.kt` — Ensure the screen's event collection and callback wiring is intact
- `AgeScreen.kt` — Same pattern; FAB triggers validation, ViewModel emits navigate or snackbar

## Bug Category 2: AddFoodItem Screen

### Bug 2.1: `mealName` Route Arg Ignored

**Problem:** The `mealName` argument from the route is never read. The dropdown always starts on Breakfast.

**Fix:** Read `mealName` from `SavedStateHandle` in `AddFoodItemViewModel`. Convert to `MealType` and use as initial selection. User can still change it via dropdown.

### Bug 2.2: Hardcoded Date

**Problem:** `LocalDate.now()` is hardcoded. If the user is viewing a past date in TrackerOverview and adds a food item, it goes to today instead.

**Fix:** Add `date` as a route argument (epoch day `Long`). Update `Route.AddFoodItem` to `add_food_item/{mealName}/{date}`. TrackerOverviewScreen passes the selected date. ViewModel reads it from `SavedStateHandle`.

### Bug 2.3: No Input Validation

**Problem:** `toInt()` on empty or non-numeric input crashes the app.

**Fix:** Validate before submission — name not blank, numeric fields parse successfully, values >= 0. On failure, emit `UiEvent.ShowSnackBar`.

### Bug 2.4: Hardcoded Spacing

**Problem:** Uses `16.dp` and `8.dp` instead of `LocalSpacing.current`.

**Fix:** Replace with `LocalSpacing.current.spaceMedium` and `spaceSmall`.

### Bug 2.5: Wrong Package

**Problem:** `AddFoodItemScreen.kt` and `AddFoodItemViewModel.kt` are in `overview.components`.

**Fix:** Move to `tracker_presentation/add_food_item` package.

### Files Changed

- `Route.kt` — Update `AddFoodItem` route to include `{date}` parameter
- `MainActivity.kt` — Pass date arg when navigating, read both args in composable destination
- `TrackerOverviewScreen.kt` — Pass selected date in navigation callback
- `AddFoodItemViewModel.kt` — Read `mealName` and `date` from SavedStateHandle, add validation
- `AddFoodItemScreen.kt` — Use `LocalSpacing`, connect to ViewModel state properly
- Move both files to `add_food_item` package
