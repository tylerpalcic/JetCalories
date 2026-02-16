@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.tylerpalcic.tracker_presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tylerpalcic.core.util.UiEvent
import com.tylerpalcic.core_ui.LocalSpacing
import com.tylerpalcic.tracker_domain.model.MealType
import com.tylerpalcic.tracker_presentation.search.components.TrackableFoodItem
import java.time.LocalDate

@Composable
fun SearchScreen(
    mealName: String,
    dayOfMonth: Int,
    month: Int,
    year: Int,
    onNavigateUp: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = keyboardController) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> onNavigateUp()
                is UiEvent.ShowSnackBar -> {
                    keyboardController?.hide()
                    onShowSnackbar(event.message)
                }

                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium)
    ) {
        Text(
            text = "Add $mealName",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = {
                viewModel.onEvent(SearchEvent.OnQueryChange(it))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    viewModel.onEvent(SearchEvent.OnSearch)
                }
            ),
            singleLine = true,
            placeholder = {
                Text(text = "Search...")
            },
            trailingIcon = {
                IconButton(onClick = {
                    keyboardController?.hide()
                    viewModel.onEvent(SearchEvent.OnSearch)
                }) {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        if (uiState.isSearching) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.trackableFood.isEmpty() && uiState.query.isNotBlank()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No results",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.trackableFood) { food ->
                    TrackableFoodItem(
                        trackableFoodUiState = food,
                        onClick = {
                            viewModel.onEvent(SearchEvent.OnToggleTrackableFood(food.food))
                        },
                        onAmountChange = {
                            viewModel.onEvent(
                                SearchEvent.OnAmountForFoodChange(
                                    food.food, it
                                )
                            )
                        },
                        onTrack = {
                            keyboardController?.hide()
                            viewModel.onEvent(
                                SearchEvent.OnTrackFoodClick(
                                    food = food.food,
                                    mealType = MealType.fromString(mealName),
                                    date = LocalDate.of(year, month, dayOfMonth)
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
