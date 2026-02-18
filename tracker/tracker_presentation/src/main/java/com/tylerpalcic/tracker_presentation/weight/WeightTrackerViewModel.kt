package com.tylerpalcic.tracker_presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tylerpalcic.tracker_domain.model.WeightEntry
import com.tylerpalcic.tracker_domain.use_case.TrackerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeightTrackerViewModel @Inject constructor(
    private val trackerUseCases: TrackerUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightTrackerState())
    val uiState = _uiState.asStateFlow()

    init {
        trackerUseCases.getWeightEntries.execute()
            .onEach { entries ->
                val latest = entries.maxByOrNull { it.date }
                _uiState.update { state ->
                    state.copy(
                        entries = entries,
                        latestWeight = latest?.weight
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: WeightTrackerEvent) {
        when (event) {
            WeightTrackerEvent.OnAddWeightClick -> {
                _uiState.update { it.copy(showWeightDialog = true) }
            }

            is WeightTrackerEvent.OnWeightEnter -> {
                val weight = event.weight.toFloatOrNull() ?: return
                if (weight <= 0f) return
                viewModelScope.launch {
                    trackerUseCases.upsertWeightEntry.execute(
                        WeightEntry(weight = weight, date = LocalDate.now())
                    )
                    _uiState.update { it.copy(showWeightDialog = false) }
                }
            }

            WeightTrackerEvent.OnDismissWeightDialog -> {
                _uiState.update { it.copy(showWeightDialog = false) }
            }

            WeightTrackerEvent.OnToggleTimeRange -> {
                _uiState.update { it.copy(showAllTime = !it.showAllTime) }
            }
        }
    }
}
