package com.tylerpalcic.fittrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tylerpalcic.core.domain.data_store.UserDataStore
import com.tylerpalcic.fittrack.navigation.Route
import com.tylerpalcic.fittrack.ui.theme.FitTrackTheme
import com.tylerpalcic.onboarding_presentation.activity_level.ActivityLevelScreen
import com.tylerpalcic.onboarding_presentation.activity_level.ActivityLevelViewModel
import com.tylerpalcic.onboarding_presentation.age.AgeScreen
import com.tylerpalcic.onboarding_presentation.age.AgeViewModel
import com.tylerpalcic.onboarding_presentation.gender.GenderScreen
import com.tylerpalcic.onboarding_presentation.gender.GenderViewModel
import com.tylerpalcic.onboarding_presentation.goal.GoalScreen
import com.tylerpalcic.onboarding_presentation.goal.GoalViewModel
import com.tylerpalcic.onboarding_presentation.height.HeightScreen
import com.tylerpalcic.onboarding_presentation.height.HeightViewModel
import com.tylerpalcic.onboarding_presentation.nutrient_goal.NutrientGoalEvent
import com.tylerpalcic.onboarding_presentation.nutrient_goal.NutrientGoalScreen
import com.tylerpalcic.onboarding_presentation.nutrient_goal.NutrientGoalViewModel
import com.tylerpalcic.onboarding_presentation.weight.WeightScreen
import com.tylerpalcic.onboarding_presentation.weight.WeightViewModel
import com.tylerpalcic.onboarding_presentation.welcome.WelcomeScreen
import com.tylerpalcic.tracker_presentation.add_food_item.AddFoodItemScreen
import com.tylerpalcic.tracker_presentation.overview.TrackerOverviewScreen
import com.tylerpalcic.tracker_presentation.search.SearchScreen
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDataStore: UserDataStore

    private lateinit var appUpdateManager: AppUpdateManager
    private var updateType = AppUpdateType.IMMEDIATE

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Log.d("Update result", "Update flow failed! Result code: $result.resultCode");
        }
    }

    val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            lifecycleScope.launch {
                delay(3.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
        checkForUpdates()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val shouldShowOnboarding by
            userDataStore.loadShouldShowOnBoarding().collectAsState(initial = true)

            FitTrackTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var fabOnClick by remember { mutableStateOf<(() -> Unit)?>(null) }

                val onShowSnackbar: (String) -> Unit = { message ->
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        if (currentRoute in listOf(
                                Route.Gender.route,
                                Route.Age.route,
                                Route.Height.route,
                                Route.Weight.route,
                                Route.Goal.route,
                                Route.NutrientGoal.route,
                                Route.ActivityLevel.route
                            )
                        ) {
                            ExtendedFloatingActionButton(
                                text = { Text(text = "Next") },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowForward,
                                        contentDescription = "Next"
                                    )
                                },
                                onClick = { fabOnClick?.invoke() }
                            )
                        }
                    }

                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues),
                        startDestination = if (shouldShowOnboarding) Route.Welcome.route else Route.TrackerOverview.route
                    ) {
                        composable(Route.Welcome.route) {
                            WelcomeScreen(
                                onNextClick = {
                                    navController.navigate(Route.Gender.route)
                                }
                            )
                        }
                        composable(Route.Gender.route) {
                            val genderViewModel: GenderViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = genderViewModel::onNextClick
                            }

                            GenderScreen(
                                onNextClick = {
                                    navController.navigate(Route.Age.route)
                                },
                                viewModel = genderViewModel
                            )
                        }
                        composable(Route.Age.route) {
                            val ageViewModel: AgeViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = ageViewModel::onNextClick
                            }

                            AgeScreen(
                                onNextClick = {
                                    navController.navigate(Route.Height.route)
                                },
                                onShowSnackbar = onShowSnackbar,
                                viewModel = ageViewModel
                            )
                        }
                        composable(Route.Height.route) {
                            val heightViewModel: HeightViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = heightViewModel::onNextClick
                            }

                            HeightScreen(
                                onNextClick = {
                                    navController.navigate(Route.Weight.route)
                                },
                                onShowSnackbar = onShowSnackbar,
                                viewModel = heightViewModel
                            )
                        }
                        composable(Route.Weight.route) {
                            val weightViewModel: WeightViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = weightViewModel::onNextClick
                            }

                            WeightScreen(
                                onNextClick = {
                                    navController.navigate(Route.Goal.route)
                                },
                                onShowSnackbar = onShowSnackbar,
                                viewModel = weightViewModel
                            )
                        }
                        composable(Route.Goal.route) {
                            val goalViewModel: GoalViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = goalViewModel::onNextClick
                            }

                            GoalScreen(
                                onNextClick = {
                                    navController.navigate(Route.NutrientGoal.route)
                                },
                                viewModel = goalViewModel
                            )
                        }
                        composable(Route.NutrientGoal.route) {
                            val nutrientGoalViewModel: NutrientGoalViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = { nutrientGoalViewModel.onEvent(NutrientGoalEvent.OnNextClick) }
                            }

                            NutrientGoalScreen(
                                onNextClick = {
                                    navController.navigate(Route.ActivityLevel.route)
                                },
                                onShowSnackbar = onShowSnackbar,
                                viewModel = nutrientGoalViewModel
                            )
                        }
                        composable(Route.ActivityLevel.route) {
                            val activityLevelViewModel: ActivityLevelViewModel = hiltViewModel()

                            LaunchedEffect(Unit) {
                                fabOnClick = activityLevelViewModel::onNextClick
                            }

                            ActivityLevelScreen(
                                onNextClick = {
                                    navController.navigate(Route.TrackerOverview.route)
                                },
                                viewModel = activityLevelViewModel
                            )
                        }
                        composable(
                            route = Route.Search.route,
                            arguments = listOf(
                                navArgument("mealName") {
                                    type = NavType.StringType
                                },
                                navArgument("dayOfMonth") {
                                    type = NavType.IntType
                                },
                                navArgument("month") {
                                    type = NavType.IntType
                                },
                                navArgument("year") {
                                    type = NavType.IntType
                                },
                            )
                        ) { backStackEntry ->
                            val mealName = backStackEntry.arguments?.getString("mealName")!!
                            val dayOfMonth = backStackEntry.arguments?.getInt("dayOfMonth")!!
                            val month = backStackEntry.arguments?.getInt("month")!!
                            val year = backStackEntry.arguments?.getInt("year")!!

                            Log.d(
                                "searchTerms",
                                "dayOfMonth: $dayOfMonth\nmonth: $month\nyear: $year\nmealName: $mealName"
                            )

                            SearchScreen(
                                mealName = mealName,
                                dayOfMonth = dayOfMonth,
                                month = month,
                                year = year,
                                onNavigateUp = {
                                    navController.popBackStack()
                                },
                                onShowSnackbar = onShowSnackbar
                            )
                        }
                        composable(Route.TrackerOverview.route) {
                            TrackerOverviewScreen(
                                onNavigateToSearch = { mealName, dayOfMonth, month, year ->
                                    navController.navigate(
                                        Route.Search.createRoute(
                                            mealName = mealName,
                                            dayOfMonth = dayOfMonth,
                                            month = month,
                                            year = year
                                        )
                                    )
                                },
                                onNavigateToAddItem = { mealName, dayOfMonth, month, year ->
                                    navController.navigate(
                                        Route.AddFoodItem.createRoute(
                                            mealName = mealName,
                                            dayOfMonth = dayOfMonth,
                                            month = month,
                                            year = year
                                        )
                                    )
                                }
                            )
                        }
                        composable(
                            route = Route.AddFoodItem.route,
                            arguments = listOf(
                                navArgument("mealName") { type = NavType.StringType },
                                navArgument("dayOfMonth") { type = NavType.IntType },
                                navArgument("month") { type = NavType.IntType },
                                navArgument("year") { type = NavType.IntType },
                            )
                        ) {
                            AddFoodItemScreen(
                                addItemOnClick = {
                                    navController.popBackStack()
                                },
                                onItemAdded = onShowSnackbar
                            )
                        }
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(updateType).build()
                    )
                }
            }
        }
    }

    fun checkForUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isAllowed = when (updateType) {
                AppUpdateType.IMMEDIATE -> appUpdateInfo.isImmediateUpdateAllowed
                AppUpdateType.FLEXIBLE -> appUpdateInfo.isFlexibleUpdateAllowed
                else -> false
            }
            if (isAvailable && isAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            }
        }
    }
}
