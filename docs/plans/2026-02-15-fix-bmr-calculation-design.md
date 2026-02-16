# Fix BMR / TDEE Calculation

## Problem

`CalculateMealNutrients` has three bugs:

1. **Weight unit bug** — weight is collected in lbs but the Harris-Benedict formula uses it as kg, overestimating the weight component by ~2.2x.
2. **Outdated formula** — uses original Harris-Benedict (1919); Mifflin-St Jeor is more accurate for modern populations.
3. **Activity multipliers too low** — Low=1.0 gives zero activity adjustment (BMR = daily requirement), Medium=1.3 and High=1.4 are below standard values.

## Solution

### File: `tracker/tracker_domain/src/main/java/com/tylerpalcic/tracker_domain/use_case/CalculateMealNutrients.kt`

### `bmr()` — Switch to Mifflin-St Jeor (imperial units)

No unit conversions needed. Coefficients pre-baked for lbs and inches:

- Male: `4.536 * lbs + 15.875 * inches - 5 * age + 5`
- Female: `4.536 * lbs + 15.875 * inches - 5 * age - 161`

Derived from metric Mifflin-St Jeor (`10 * kg + 6.25 * cm - 5 * age +/- constant`) by substituting `kg = lbs * 0.4536` and `cm = inches * 2.54`.

### `dailyCalorieRequirement()` — Updated multipliers and goal offset

Activity multipliers (standard TDEE):
- Low → 1.2 (sedentary)
- Medium → 1.375 (light exercise 1-3 days/week)
- High → 1.55 (moderate exercise 3-5 days/week)

Goal offset:
- LoseWeight → -750
- KeepWeight → 0
- GainWeight → +750

### Sanity Check

Male, 30 years, 180 lbs, 70 inches, Medium activity, Lose Weight:
- BMR = 4.536(180) + 15.875(70) - 5(30) + 5 = 816 + 1111 - 150 + 5 = 1782
- TDEE = 1782 * 1.375 = 2450
- Goal = 2450 - 750 = 1700 kcal/day

Macro goals are unaffected — they derive from `calorieGoal * ratio / cals-per-gram`.
