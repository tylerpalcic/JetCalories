package com.tylerpalcic.tracker_domain.model

import java.time.LocalDate

data class BurnedCalories(
    val calories: Int,
    val date: LocalDate
)
