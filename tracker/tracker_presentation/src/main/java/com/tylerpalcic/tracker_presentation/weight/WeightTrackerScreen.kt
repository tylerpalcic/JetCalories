package com.tylerpalcic.tracker_presentation.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatryk.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.entryOf
import com.tylerpalcic.core_ui.LocalSpacing
import com.tylerpalcic.tracker_presentation.weight.components.WeightEntryDialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: WeightTrackerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val modelProducer = remember { ChartEntryModelProducer() }

    val filteredEntries = remember(state.entries, state.showAllTime) {
        if (state.showAllTime) {
            state.entries
        } else {
            val cutoff = LocalDate.now().minusDays(30)
            state.entries.filter { it.date >= cutoff }
        }
    }

    LaunchedEffect(filteredEntries) {
        if (filteredEntries.isNotEmpty()) {
            val entries = filteredEntries.mapIndexed { index, entry ->
                entryOf(index.toFloat(), entry.weight)
            }
            modelProducer.setEntries(entries)
        }
    }

    if (state.showWeightDialog) {
        WeightEntryDialog(
            currentWeight = state.latestWeight,
            onConfirm = { viewModel.onEvent(WeightTrackerEvent.OnWeightEnter(it)) },
            onDismiss = { viewModel.onEvent(WeightTrackerEvent.OnDismissWeightDialog) }
        )
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(WeightTrackerEvent.OnAddWeightClick) }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Log weight"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(spacing.spaceMedium)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(spacing.spaceMedium)
                ) {
                    Text(
                        text = "Current Weight",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.latestWeight?.let { "%.1f lbs".format(it) } ?: "No entries yet",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.spaceMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
            ) {
                FilterChip(
                    selected = !state.showAllTime,
                    onClick = {
                        if (state.showAllTime) viewModel.onEvent(WeightTrackerEvent.OnToggleTimeRange)
                    },
                    label = { Text("30 Days") }
                )
                FilterChip(
                    selected = state.showAllTime,
                    onClick = {
                        if (!state.showAllTime) viewModel.onEvent(WeightTrackerEvent.OnToggleTimeRange)
                    },
                    label = { Text("All Time") }
                )
            }

            Spacer(modifier = Modifier.height(spacing.spaceMedium))

            if (filteredEntries.isNotEmpty()) {
                Chart(
                    chart = lineChart(),
                    chartModelProducer = modelProducer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge * 4)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No weight entries yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap + to log your weight",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
