package com.example.googlefit.screens

import HealthManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.navigation.NavHostController
import com.example.googlefit.navigation.Route
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun MainScreen(healthManager: HealthManager, navController: NavHostController) {

    var allPermissionsGranted by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = healthManager.requestPermissionsActivityContract()
    ) { result: Set<String> ->

        val allPermissionsGranted = result.containsAll(healthManager.permissions)
        if (allPermissionsGranted) {

        } else {

        }
    }

    LaunchedEffect(Unit) {
        allPermissionsGranted = healthManager.hasAllPermissions(healthManager.permissions)
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            if (!allPermissionsGranted) {
                Text(text = "Request Health Permissions")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    permissionLauncher.launch(healthManager.permissions)
                }) {
                    Text(text = "Request Permissions")
                }
            } else {

                Button(onClick = {
                    navController.navigate(Route.ACTIVITY_SCREEN)
                }) {
                    Text(text = "Activity")
                }

                Button(onClick = {
                    navController.navigate(Route.BODY_MEASUREMENTS_SCREEN)
                }) {
                    Text(text = "Body Measurements")
                }

                Button(onClick = {
                    navController.navigate(Route.VITALS_SCREEN)
                }) {
                    Text(text = "Vitals")
                }

                Button(onClick = {
                    navController.navigate(Route.NUTRITION_SCREEN)
                }) {
                    Text(text = "Nutrition")
                }

                Button(onClick = {
                    navController.navigate(Route.SLEEP_SCREEN)
                }) {
                    Text(text = "Sleep")
                }

                Button(onClick = {
                    navController.navigate(Route.CYCLE_SCREEN)
                }) {
                    Text(text = "Cycle tracking")
                }

            }
        }
    }

}

@Preview
@Composable
fun MainPreview() {
    MainScreen(healthManager = HealthManager(LocalContext.current), navController = NavHostController(LocalContext.current)
    )
}