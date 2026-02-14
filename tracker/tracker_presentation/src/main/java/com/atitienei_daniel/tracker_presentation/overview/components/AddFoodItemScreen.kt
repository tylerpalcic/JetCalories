package com.atitienei_daniel.tracker_presentation.add_food_item

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.atitienei_daniel.tracker_domain.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodItemScreen(
    onAddItem: (
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        mealType: MealType
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("0") }
    var protein by remember { mutableStateOf("0") }
    var carbs by remember { mutableStateOf("0") }
    var fat by remember { mutableStateOf("0") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMeal by remember { mutableStateOf(MealType.BreakFast) }

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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
                    onAddItem(
                        name,
                        calories.toInt(),
                        protein.toInt(),
                        carbs.toInt(),
                        fat.toInt(),
                        selectedMeal
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Item")
            }
        }
    }
}