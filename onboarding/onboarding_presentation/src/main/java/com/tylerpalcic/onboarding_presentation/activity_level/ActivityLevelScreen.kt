package com.tylerpalcic.onboarding_presentation.activity_level

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
import com.tylerpalcic.core.domain.model.ActivityLevel
import com.tylerpalcic.core.util.UiEvent
import com.tylerpalcic.core_ui.LocalSpacing
import com.tylerpalcic.onboarding_presentation.components.SelectableButton

@Composable
fun ActivityLevelScreen(
    onNextClick: () -> Unit,
    viewModel: ActivityLevelViewModel = hiltViewModel()
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
        Text(text = "What's your activity level?")
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
        ) {
            SelectableButton(
                text = "Low",
                isSelected = viewModel.selectedActivityLevel is ActivityLevel.Low,
                color = MaterialTheme.colorScheme.primaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    viewModel.onActivityLevelClick(ActivityLevel.Low)
                }
            )

            SelectableButton(
                text = "Medium",
                isSelected = viewModel.selectedActivityLevel is ActivityLevel.Medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    viewModel.onActivityLevelClick(ActivityLevel.Medium)
                }
            )

            SelectableButton(
                text = "High",
                isSelected = viewModel.selectedActivityLevel is ActivityLevel.High,
                color = MaterialTheme.colorScheme.primaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    viewModel.onActivityLevelClick(ActivityLevel.High)
                }
            )
        }
    }
}
