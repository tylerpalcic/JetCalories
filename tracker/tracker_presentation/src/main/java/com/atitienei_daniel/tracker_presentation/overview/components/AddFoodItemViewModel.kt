package com.atitienei_daniel.tracker_presentation.overview.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atitienei_daniel.tracker_domain.model.MealType
import com.atitienei_daniel.tracker_domain.model.TrackableFood
import com.atitienei_daniel.tracker_domain.use_case.TrackerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFoodItemViewModel @Inject constructor(
    private val trackerUseCases: TrackerUseCases
) : ViewModel() {

    fun onAddItem(
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        mealType: MealType
    ) {
        viewModelScope.launch {
            trackerUseCases.trackFood(
                TrackableFood(
                    name = name,
                    imageUrl = null,
                    caloriesPer100g = calories,
                    proteinPer100g = protein,
                    carbsPer100g = carbs,
                    fatPer100g = fat
                ),
                amount = 1,
                mealType = mealType,
                date = java.time.LocalDate.now()
            )
        }
    }
}