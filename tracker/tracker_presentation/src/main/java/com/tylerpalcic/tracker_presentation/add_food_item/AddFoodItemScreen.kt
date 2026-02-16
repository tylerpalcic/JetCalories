package com.tylerpalcic.tracker_presentation.add_food_item

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
import com.tylerpalcic.core.util.UiEvent
import com.tylerpalcic.core_ui.LocalSpacing
import com.tylerpalcic.tracker_domain.model.MealType

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

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = "Add Food Item")
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    MealType.values().forEach { meal ->
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
