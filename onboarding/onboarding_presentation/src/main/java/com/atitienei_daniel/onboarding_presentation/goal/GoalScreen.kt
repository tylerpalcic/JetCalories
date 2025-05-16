@file:OptIn(ExperimentalMaterial3Api::class)

package com.atitienei_daniel.onboarding_presentation.goal

import android.R.attr.text
import android.R.attr.value
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atitienei_daniel.core.domain.model.GoalType
import com.atitienei_daniel.core.util.UiEvent
import com.atitienei_daniel.core_ui.LocalSpacing
import com.atitienei_daniel.onboarding_presentation.components.SelectableButton


@Composable
fun GoalScreen(
    onNextClick: () -> Unit,
//    onSkipClick: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNextClick()
                else -> Unit
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Next") },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null
                    )
                },
                onClick = {
                    viewModel.onNextClick()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Do you want to lose weight, gain weight, or stay the same?")
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
            ) {
                SelectableButton(
                    text = "Lose weight",
                    isSelected = viewModel.selectedGoalType is GoalType.LoseWeight,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        viewModel.onActivityLevelClick(GoalType.LoseWeight)
                    }
                )

                SelectableButton(
                    text = "Keep weight",
                    isSelected = viewModel.selectedGoalType is GoalType.KeepWeight,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        viewModel.onActivityLevelClick(GoalType.KeepWeight)
                    }
                )

                SelectableButton(
                    text = "Gain weight",
                    isSelected = viewModel.selectedGoalType is GoalType.GainWeight,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        viewModel.onActivityLevelClick(GoalType.GainWeight)
                    }
                )
            }
//            Text(
//                text = "Click here if you want to enter your calorie allowance manually",
//                textDecoration = TextDecoration.Underline,
//                fontSize = 16.sp,
//                modifier = Modifier
//                    .padding(
//                        vertical = spacing.spaceMedium,
//                        horizontal = spacing.spaceSmall
//                    )
//                    .clickable {
//                        viewModel.onActivityLevelClick(GoalType.None)
//                        viewModel.showCalorieInput.value = true
//                    }
//            )
//
//            if (viewModel.showCalorieInput.value) {
//                OutlinedTextField(
//                    value = "${viewModel.calorieAllowance.value}",
//                    onValueChange = { new -> viewModel.calorieAllowance.value = new.toIntOrNull() ?: 0 },
//                    label = { Text(text = "Enter desired calorie daily allowance") },
//                    maxLines = 1,
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number,
//                        imeAction = ImeAction.Done
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            viewModel.onNextClick()
//                        }
//                    ),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        containerColor = MaterialTheme.colorScheme.surface,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
//                        focusedBorderColor = MaterialTheme.colorScheme.onSurface
//                    )
//                )
//
//                Button(
//                    onClick = onSkipClick,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    )
//                ) {
//                    Text(text = "Finish")
//                }
//            }
        }
    }
}