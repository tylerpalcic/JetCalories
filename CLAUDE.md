# FitTrack — Claude Rules

## Core Identity
Native Android calorie/macro tracker (Kotlin + Jetpack Compose).  
Clean Architecture, multi-module, MVVM, single source of truth via Room + DataStore.

## Must-Follow Rules (Never violate)
- Use **LocalSpacing.current** for all dp values — never hardcode.
- Navigation: sealed class Route in app/navigation/Route.kt → use createRoute(…) helpers.
- ViewModels: MutableStateFlow + asStateFlow(), Channel<UiEvent>, onEvent(…) handler, data class State/Event.
- Use cases: execute() fun, grouped in data classes (TrackerUseCases, OnboardingUseCases) → inject once.
- Mappers: extension functions only (toTrackableFood(), toEntity(), toDomain() etc.) — no mapper classes.
- Dependencies: **Never** inline versions — use buildSrc objects (Compose, Hilt, Room, Retrofit, Moshi, Coil…).
- Compose: prefer default parameters, lambdas, scope functions; keep files <300 LOC.
- Naming: short+clear in context (calcBMR, nutriHeader, mealTotals…); ViewModel/State/Event suffixes mandatory.
- Enums (Gender, GoalType, ActivityLevel): include fromString() companion.
- API: Open Food Facts — https://us.openfoodfacts.org/cgi/search.pl (search_terms, page, page_size params).

## High-Level Structure
app                  → MainActivity, Nav, Hilt modules, Theme  
core                 → Domain models, UserDataStore, FilterOutDigits, UiEvent  
core-ui              → LocalSpacing, Colors, shared Compose utils  
onboarding:domain    → ValidateNutrients  
onboarding:presentation → 8 screens + VMs, ActionButton, SelectableButton, UnitTextField  
tracker:data         → Room (TrackerDatabase, TrackedFoodEntity, TrackerDao), Retrofit (OpenFoodApi), RepositoryImpl, mappers  
tracker:domain       → TrackableFood/TrackedFood/MealType, TrackerRepository, use cases (SearchFood, TrackFood, Delete…, GetFoodsForDate, CalculateMealNutrients)  
tracker:presentation → TrackerOverviewScreen, SearchScreen, AddFoodItemScreen + VMs, NutrientsHeader/Bar, DaySelector, ExpandableMeal, Tracked/TrackableFoodItem

## Tech Stack (reference only — do NOT re-explain)
Kotlin, Compose (Material 3), Hilt, Room, Retrofit+Moshi, Coil, DataStore, Coroutines/Flow, Navigation Compose, Play In-App Updates  
minSdk 29, compileSdk 33, JVM 17, Kotlin DSL + buildSrc

## Token & Prompt Efficiency
- Prefer data class, extensions, Elvis, let/apply/run/with, inline where appropriate.
- Favor defaults over overloads.
- Extract ≥80% reusable composables to core-ui.
- When generating: output **code only** unless asked; no explanations by default.
- Prompt prefix: “Follow FitTrack rules strictly:” + paste **only** the relevant bullet sections above.
- Reset chat or summarize history frequently when context grows.

Keep answers concise. Violating any "Never" rule requires explicit user override.