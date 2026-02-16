package com.tylerpalcic.onboarding_presentation.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun WeightScreen(
    onNextClick: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: WeightViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNextClick()
                is UiEvent.ShowSnackBar -> onShowSnackbar(event.message)
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
        Text(text = "What's your weight?", style = MaterialTheme.typography.titleLarge)
        UnitTextField(
            value = viewModel.weight,
            onValueChange = viewModel::onWeightValueChange,
            unit = "lbs"
        )
    }
}
