@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.atitienei_daniel.tracker_presentation.overview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atitienei_daniel.core_ui.LocalSpacing
import com.atitienei_daniel.tracker_presentation.overview.components.DaySelector
import com.atitienei_daniel.tracker_presentation.overview.components.ExpandableMeal
import com.atitienei_daniel.tracker_presentation.overview.components.NutrientsHeader
import com.atitienei_daniel.tracker_presentation.overview.components.RecentFoodsSheet
import com.atitienei_daniel.tracker_presentation.overview.components.TrackedFoodItem

@Composable
fun TrackerOverviewScreen(
    onNavigateToSearch: (String, Int, Int, Int) -> Unit,
    onNavigateToAddItem: (String, Int, Int, Int) -> Unit,
    viewModel: TrackerOverviewViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
    ) {
        item {
            NutrientsHeader(state = uiState)
        }
        item {
            DaySelector(
                date = uiState.date,
                onPreviousDayClick = {
                    viewModel.onEvent(TrackerOverviewEvent.OnPreviousDayClick)
                },
                onNextDayClick = {
                    viewModel.onEvent(TrackerOverviewEvent.OnNextDayClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium)
            )
        }
        items(uiState.meals) { meal ->
            ExpandableMeal(
                meal = meal,
                onToggleClick = {
                    viewModel.onEvent(TrackerOverviewEvent.OnToggleMealClick(meal))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spaceSmall)
                ) {
                    val foods = uiState.trackedFoods.filter {
                        it.mealType == meal.mealType
                    }
                    foods.forEach { trackedFood ->
                        TrackedFoodItem(
                            food = trackedFood,
                            onDeleteClick = {
                                viewModel.onEvent(
                                    TrackerOverviewEvent.OnDeleteTrackedFoodClick(trackedFood)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onNavigateToAddItem(
                                    meal.name,
                                    uiState.date.dayOfMonth,
                                    uiState.date.monthValue,
                                    uiState.date.year
                                )
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        ) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(spacing.spaceSmall))
                            Text(text = "Add")
                        }
                        OutlinedButton(
                            onClick = {
                                onNavigateToSearch(
                                    meal.name,
                                    uiState.date.dayOfMonth,
                                    uiState.date.monthValue,
                                    uiState.date.year
                                )
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        ) {
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(spacing.spaceSmall))
                            Text(text = "Search")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.onEvent(
                                    TrackerOverviewEvent.OnShowRecentFoods(meal.mealType)
                                )
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        ) {
                            Icon(imageVector = Icons.Rounded.History, contentDescription = null)
                            Spacer(modifier = Modifier.width(spacing.spaceSmall))
                            Text(text = "Recent")
                        }
                        val yesterdayCount = uiState.yesterdayMealCounts[meal.mealType] ?: 0
                        OutlinedButton(
                            onClick = {
                                viewModel.onEvent(
                                    TrackerOverviewEvent.OnCopyYesterdayMeal(meal.mealType)
                                )
                            },
                            enabled = yesterdayCount > 0,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (yesterdayCount > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                            ),
                        ) {
                            Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null)
                            Spacer(modifier = Modifier.width(spacing.spaceSmall))
                            Text(text = "Yesterday")
                        }
                    }

                }
            }
        }
    }

    if (uiState.showRecentSheet) {
        RecentFoodsSheet(
            foods = uiState.recentFoods,
            onAddFood = { food ->
                viewModel.onEvent(TrackerOverviewEvent.OnAddRecentFood(food))
            },
            onDismiss = {
                viewModel.onEvent(TrackerOverviewEvent.OnDismissRecentSheet)
            }
        )
    }
}