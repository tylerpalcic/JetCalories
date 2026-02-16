@file:OptIn(ExperimentalMaterial3Api::class)

package com.tylerpalcic.onboarding_presentation.age

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tylerpalcic.core.util.UiEvent
import com.tylerpalcic.core_ui.LocalSpacing
import com.tylerpalcic.onboarding_presentation.components.UnitTextField

@Composable
fun AgeScreen(
    onNextClick: () -> Unit,
    onShowSnackbar: (String) -> Unit, // New callback for showing snackbar
    viewModel: AgeViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNextClick()
                is UiEvent.ShowSnackBar -> onShowSnackbar(event.message) // Call the hoisted function
                else -> Unit
            }
        }
    }

    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "What's your age?", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        UnitTextField(
            value = viewModel.age,
            onValueChange = viewModel::onAgeValueChange,
            unit = "years"
        )
    }
}