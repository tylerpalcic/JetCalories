package com.atitienei_daniel.onboarding_presentation.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.atitienei_daniel.core.domain.model.GoalType
import com.atitienei_daniel.core.util.UiEvent
import com.atitienei_daniel.core_ui.LocalSpacing
import com.atitienei_daniel.onboarding_presentation.components.SelectableButton

@Composable
fun GoalScreen(
    onNextClick: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceLarge),
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
    }
}
