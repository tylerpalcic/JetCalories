package com.tylerpalcic.tracker_presentation.overview.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tylerpalcic.core_ui.CarbColor
import com.tylerpalcic.core_ui.FatColor
import com.tylerpalcic.core_ui.LocalSpacing
import com.tylerpalcic.core_ui.ProteinColor
import com.tylerpalcic.tracker_presentation.components.UnitDisplay
import com.tylerpalcic.tracker_presentation.overview.TrackerOverviewState

@Composable
fun NutrientsHeader(
    state: TrackerOverviewState,
    modifier: Modifier = Modifier,
    onBurnedCaloriesClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val animatedCalorieCount by animateIntAsState(targetValue = state.totalCalories)
    val animatedTotalCalorieGoal by animateIntAsState(targetValue = state.caloriesGoal)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 50.dp,
                    bottomEnd = 50.dp
                )
            )
            .background(MaterialTheme.colorScheme.primary)
            .padding(
                horizontal = spacing.spaceLarge,
                vertical = spacing.spaceExtraLarge
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UnitDisplay(
                amount = animatedCalorieCount,
                unit = "kcal",
                amountColor = MaterialTheme.colorScheme.onPrimary,
                unitColor = MaterialTheme.colorScheme.onPrimary,
                amountTextSize = 40.sp,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Column {
                Text(text = "Your goal:", color = MaterialTheme.colorScheme.onPrimary)
                UnitDisplay(
                    amount = animatedTotalCalorieGoal,
                    unit = "kcal",
                    amountColor = MaterialTheme.colorScheme.onPrimary,
                    unitColor = MaterialTheme.colorScheme.onPrimary,
                    amountTextSize = 40.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.spaceSmall))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onBurnedCaloriesClick() }
                .padding(vertical = spacing.spaceExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDD25",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
            Text(
                text = "${state.burnedCalories} burned",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
            if (state.burnedCalories == 0) {
                Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add burned calories",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.spaceSmall))

        NutrientsBar(
            carbs = state.totalCarbs,
            protein = state.totalProtein,
            fat = state.totalFat,
            calories = state.totalCalories,
            calorieGoal = state.caloriesGoal,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        )

        Spacer(modifier = Modifier.height(spacing.spaceLarge))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NutrientBarInfo(
                value = state.totalCarbs,
                goal = state.carbsGoal,
                name = "Carbs",
                color = CarbColor,
                modifier = Modifier.size(90.dp)
            )
            NutrientBarInfo(
                value = state.totalProtein,
                goal = state.proteinGoal,
                name = "Protein",
                color = ProteinColor,
                modifier = Modifier.size(90.dp)
            )
            NutrientBarInfo(
                value = state.totalFat,
                goal = state.fatGoal,
                name = "Fat",
                color = FatColor,
                modifier = Modifier.size(90.dp)
            )
        }
    }
}