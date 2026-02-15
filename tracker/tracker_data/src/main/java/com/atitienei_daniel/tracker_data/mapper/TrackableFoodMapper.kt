package com.atitienei_daniel.tracker_data.mapper

import com.atitienei_daniel.tracker_data.remote.dto.Product
import com.atitienei_daniel.tracker_domain.model.TrackableFood
import kotlin.math.roundToInt

fun Product.toTrackableFood(): TrackableFood? {
    val carbsPer100g = nutriments.carbohydrates100g?.roundToInt() ?: return null
    val proteinPer100g = nutriments.proteins100g?.roundToInt() ?: return null
    val fatPer100g = nutriments.fat100g?.roundToInt() ?: return null
    val caloriesPer100g = nutriments.energyKcal100g?.roundToInt() ?: return null

    return TrackableFood(
        name = productName ?: return null,
        carbsPer100g = carbsPer100g,
        proteinPer100g = proteinPer100g,
        caloriesPer100g = caloriesPer100g,
        fatsPer100g = fatPer100g,
        imageUrl = imageFrontThumbUrl
    )
}
