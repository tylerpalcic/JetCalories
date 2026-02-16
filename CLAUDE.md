# FitTrack — Project Rules

## App Summary
FitTrack is a native Android calorie and macronutrient tracker built with Kotlin and Jetpack Compose. It follows Clean Architecture with a multi-module structure.

### What It Does
- **Onboarding** (8 screens): Collects user profile — gender, age, height, weight, fitness goal (lose/keep/gain weight), macronutrient ratio targets (carbs/protein/fat summing to 100%), and activity level (low/medium/high). Persisted via DataStore Preferences.
- **Tracker Overview**: Daily dashboard showing a nutrients header (calories, carbs, protein, fat vs. goals), a day selector to navigate dates, and expandable meal sections (Breakfast, Lunch, Dinner, Snack). Each meal shows tracked foods with delete, search, and add-custom options.
- **Food Search**: Searches the Open Food Facts API via Retrofit, displays results with images (Coil), lets users set amounts and track items to the local Room database.
- **Add Food Item**: Manual entry of custom food with name, calories, carbs, protein, fat, and amount.
- **Calorie/Macro Calculation**: `CalculateMealNutrients` use case computes daily totals and per-meal breakdowns against user goals based on BMR, activity level, and goal type.
- **In-App Updates**: Google Play Core in-app update support (immediate mode).

## Module Structure

| Module                        | Purpose                                                                 |
|-------------------------------|-------------------------------------------------------------------------|
| `app`                         | MainActivity, Navigation (Route sealed class), Hilt DI modules (AppModule, UseCaseModule), Theme |
| `core`                        | Domain models (UserInfo, Gender, GoalType, ActivityLevel), UserDataStore interface + DataStore impl, FilterOutDigits use case, UiEvent |
| `core-ui`                     | Shared Compose utilities — Dimensions/LocalSpacing CompositionLocal, Colors |
| `onboarding:onboarding_domain`| ValidateNutrients use case, Hilt DI module                              |
| `onboarding:onboarding_presentation` | 8 onboarding screens + ViewModels, shared components (ActionButton, SelectableButton, UnitTextField) |
| `tracker:tracker_data`        | Room DB (TrackerDatabase, TrackerDao, TrackedFoodEntity), Retrofit API (OpenFoodApi, DTOs), TrackerRepositoryImpl, entity/model mappers |
| `tracker:tracker_domain`      | Domain models (TrackableFood, TrackedFood, MealType), TrackerRepository interface, UseCases (SearchFood, TrackFood, DeleteTrackedFood, GetFoodsForDate, CalculateMealNutrients) |
| `tracker:tracker_presentation`| TrackerOverviewScreen + ViewModel, SearchScreen + ViewModel, AddFoodItemScreen + ViewModel, UI components (NutrientsHeader, NutrientsBar, DaySelector, ExpandableMeal, TrackedFoodItem, TrackableFoodItem) |

## Tech Stack
- Kotlin, Jetpack Compose (Material 3), Dagger Hilt, Room, Retrofit + Moshi, Coil, DataStore Preferences, Coroutines/Flow, Navigation Compose, Google Play In-App Updates
- Build uses Kotlin DSL with `buildSrc` dependency objects
- JVM target 17, compileSdk 33, minSdk 29
- Package: `com.tylerpalcic.fittrack`

## Project-Specific Conventions

### buildSrc Dependencies
All dependency versions and coordinates are managed via `buildSrc` objects: `Compose`, `DaggerHilt`, `Room`, `Retrofit`, `Moshi`, `Coil`, `AndroidX`, `Google`, `Testing`, `Modules`, `ProjectConfig`, `Build`, `Kotlin`. **Never** inline version strings.

### Shared Gradle Scripts
Modules apply shared config via `base-module.gradle` and `compose-module.gradle` apply scripts.

### Navigation
Routes are defined as `sealed class Route(val route: String)` with `object` subclasses in `app/navigation/Route.kt`. Parameterized routes use `{param}` syntax with `createRoute()` helpers. All navigation is orchestrated in `MainActivity` — screens receive lambda callbacks only.

### Spacing
Use `LocalSpacing.current` from `core-ui` for all spacing values (spaceExtraSmall=4dp, spaceSmall=8dp, spaceMedium=16dp, spaceLarge=32dp, spaceExtraLarge=64dp). **Do not** hardcode dp values.

### ViewModels
- State: `MutableStateFlow` + `asStateFlow()`
- Events: `Channel<UiEvent>` + `receiveAsFlow()`
- Event handling: sealed event class + `onEvent(event)` function
- State class: data class with defaults (e.g., `TrackerOverviewState`)

### Use Cases
Each use case has an `execute()` function. Related use cases are grouped in a data class (e.g., `TrackerUseCases`) for injection via Hilt modules.

### Mappers
Entity/DTO to domain model conversions are extension functions (e.g., `Product.toTrackableFood()`, `TrackedFood.toEntity()`).

### Enums
User-facing enums (Gender, GoalType, ActivityLevel) include a `fromString()` companion function.

### External API
Open Food Facts API — base URL: `https://us.openfoodfacts.org/`. Search endpoint uses `cgi/search.pl` with query params for search_terms, page, and page_size.

## Token & Prompt Efficiency Conventions
Rules that reduce code verbosity, boilerplate, and context length when generating/reviewing code with AI.

### Code Conciseness & Idiomatic Kotlin
- Prefer Kotlin idioms: lambdas, extensions, scope functions (`let`, `apply`, `run`, `with`), destructuring, Elvis, etc.
- Use `data class` for all models and UI state classes (auto `equals`, `hashCode`, `toString`, `copy`).
- Favor default parameters in functions and composables to avoid overloads.
- Mark small utility functions `inline` when appropriate.
- Keep source files < 300 lines; split large classes/screens into helpers/extensions.

### Naming & Readability Trade-offs
- Use short but clear names for frequent items: `calcBMR`, `nutriHeader`, `addFood`, `mealTotals` (context should disambiguate).
- ViewModel classes end with `ViewModel`, state with `State`, events with `Event`.
- Routes: short kebab-case (`tracker-overview/{date}`), one `createRoute(vararg args: String)` helper per parameterized route.

### Reusability & Modularization
- Extract reusable composables/utilities to `core-ui` or new shared modules (target ≥80% reuse in presentation).
- Group use cases into data classes (`TrackerUseCases`, `OnboardingUseCases`) — inject once.
- All conversions are extension functions on source type (`toDomain()`, `toEntity()`, `toUi()`).
- Avoid separate mapper classes and custom annotations unless library-required.

### Build & Dependency Rules
- Prefer Gradle Version Catalogs (`libs.versions.toml`) over `buildSrc` objects when possible (future migration target).
- Use `implementation` (not `api`) between internal modules.
- Apply common plugins via shared scripts (`kotlin-parcelize`, etc.).

### AI/Prompt Optimization (Meta Rules)
- Use tables and bullet fragments instead of full sentences where possible.
- Reference patterns briefly (e.g., “Follow official Compose Navigation guidelines”) — avoid re-explaining standards.
- When prompting AI, prefix with “Follow FitTrack rules:” and include **only relevant sections** of this document.
- Omit obvious defaults (standard coroutine usage, Material 3 theming, etc.) unless overridden.