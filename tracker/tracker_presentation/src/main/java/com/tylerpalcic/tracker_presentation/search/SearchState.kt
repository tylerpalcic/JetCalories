package com.tylerpalcic.tracker_presentation.search

data class SearchState(
    val isSearching: Boolean = false,
    val query: String = "",
    val trackableFood: List<TrackableFoodUiState> = emptyList()
)