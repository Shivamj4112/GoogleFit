package com.example.googlefit.navigation

import com.example.googlefit.HealthManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.googlefit.navigation.Route.ACTIVITY_SCREEN
import com.example.googlefit.navigation.Route.BODY_MEASUREMENTS_SCREEN
import com.example.googlefit.navigation.Route.CYCLE_SCREEN
import com.example.googlefit.navigation.Route.MAIN_SCREEN
import com.example.googlefit.navigation.Route.NUTRITION_DETAILS_SCREEN
import com.example.googlefit.navigation.Route.NUTRITION_SCREEN
import com.example.googlefit.navigation.Route.SLEEP_DETAILS_SCREEN
import com.example.googlefit.navigation.Route.SLEEP_SCREEN
import com.example.googlefit.navigation.Route.VITALS_SCREEN
import com.example.googlefit.screens.ActivityScreen
import com.example.googlefit.screens.BodyMeasurementsScreen
import com.example.googlefit.screens.CycleTrackingScreen
import com.example.googlefit.screens.MainScreen
import com.example.googlefit.screens.nutritionscreens.NutritionScreen
import com.example.googlefit.screens.sleepscreens.SleepScreen
import com.example.googlefit.screens.VitalsScreen
import com.example.googlefit.screens.nutritionscreens.NutritionDetailsScreen
import com.example.googlefit.screens.sleepscreens.SleepDetailsScreen

object Route {

    const val MAIN_SCREEN = "main_screen"
    const val ACTIVITY_SCREEN = "activity_screen"
    const val BODY_MEASUREMENTS_SCREEN = "body_measurements_screen"
    const val VITALS_SCREEN = "vitals_screen"
    const val NUTRITION_SCREEN = "nutrition_screen"
    const val SLEEP_SCREEN = "sleep_screen"
    const val CYCLE_SCREEN = "cycle_screen"
    const val SLEEP_DETAILS_SCREEN = "sleep_details_screen"
    const val NUTRITION_DETAILS_SCREEN = "nutrition_details_screen"
}

@Composable
fun Navigation(){

    val navController = rememberNavController()
    val healthManager = HealthManager(LocalContext.current)

    NavHost(navController = navController, startDestination = MAIN_SCREEN) {

        composable(MAIN_SCREEN){
            MainScreen(healthManager,navController)
        }
        composable(ACTIVITY_SCREEN){
            ActivityScreen(healthManager,navController)
        }
        composable(BODY_MEASUREMENTS_SCREEN){
            BodyMeasurementsScreen(healthManager,navController)
        }
        composable(VITALS_SCREEN){
            VitalsScreen(healthManager,navController)
        }
        composable(NUTRITION_SCREEN){
            NutritionScreen(healthManager,navController)
        }
        composable(SLEEP_SCREEN){
            SleepScreen(healthManager,navController)
        }
        composable(CYCLE_SCREEN){
            CycleTrackingScreen(healthManager,navController)
        }

        composable("$SLEEP_DETAILS_SCREEN/{startTime}/{endTime}") {

            val startTime = it.arguments?.getString("startTime")
            val endTime = it.arguments?.getString("endTime")

            if (startTime != null && endTime != null) {
                SleepDetailsScreen(navController, startTime, endTime)
            }

        }


        composable("$NUTRITION_DETAILS_SCREEN/{endTime}") {
            val endTime = it.arguments?.getString("endTime")
            if (endTime != null) {
                NutritionDetailsScreen(healthManager,navController, endTime)
            }
        }
    }

}