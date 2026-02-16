package com.tylerpalcic.tracker_presentation.add_food_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerpalcic.core.util.UiEvent
import com.tylerpalcic.tracker_domain.model.MealType
import com.tylerpalcic.tracker_domain.model.TrackableFood
import com.tylerpalcic.tracker_domain.use_case.TrackerUseCases
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
            MealType.values().find {
                it.name.equals(name, ignoreCase = true)
            }
        } ?: MealType.Breakfast

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
