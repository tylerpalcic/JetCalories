package com.atitienei_daniel.tracker_presentation.add_food_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atitienei_daniel.core.util.UiEvent
import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackableFood
import com.atitienei_daniel.tracker_domain.use_case.TrackerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddFoodItemViewModel @Inject constructor(
    private val trackerUseCases: TrackerUseCases
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAddItem(
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        mealType: MealType
    ) {
        viewModelScope.launch {
            trackerUseCases.trackFood.execute(
                TrackableFood(
                    name = name,
                    imageUrl = null,
                    caloriesPer100g = calories,
                    proteinPer100g = protein,
                    fatsPer100g = fat,
                    carbsPer100g = carbs,
                ),
                amount = 1,
                mealType = mealType,
                date = LocalDate.now()
            )
            _uiEvent.emit(UiEvent.ShowSnackBar("Food item added successfully."))
        }
    }
}