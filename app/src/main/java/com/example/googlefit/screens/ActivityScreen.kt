package com.example.googlefit.screens

import HealthManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.navigation.NavHostController
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun ActivityScreen(healthManager: HealthManager, navController: NavHostController) {

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val stepsRecords by produceState<List<StepsRecord>>(initialValue = emptyList()) {
                value = healthManager.readStepsInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val exerciseRecords by produceState<List<ExerciseSessionRecord>>(initialValue = emptyList()) {
                value = healthManager.readExerciseSessions(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val distanceRecords by produceState<List<DistanceRecord>>(initialValue = emptyList()) {
                value = healthManager.readDistancesInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val speedRecords by produceState<List<SpeedRecord>>(initialValue = emptyList()) {
                value = healthManager.readSpeedInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val caloriesRecords by produceState<List<TotalCaloriesBurnedRecord>>(initialValue = emptyList()) {
                value = healthManager.readTotalCaloriesBurnedInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (stepsRecords.isNotEmpty()) {
                Text(text = "Steps Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                stepsRecords.forEach { record ->
                    Text(text = "${record.count} Steps")
                }
            } else {
                Text(text = "No steps records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))


            if (exerciseRecords.isNotEmpty()) {
                Text(text = "Exercise Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                exerciseRecords.forEach { record ->
                    Text(text = "Exercise: ${record.exerciseType}")
                }
            } else {
                Text(text = "No exercise records available.")
            }


            Spacer(modifier = Modifier.height(24.dp))

            if (distanceRecords.isNotEmpty()) {
                Text(text = "Distance Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                distanceRecords.forEach { record ->
                    Text(text = "Distance: %.1f km".format(record.distance.inKilometers))
                }
            } else {
                Text(text = "No distance records available.")
            }


            Spacer(modifier = Modifier.height(24.dp))


            if (speedRecords.isNotEmpty()) {
                Text(text = "Speed Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                speedRecords.forEach { record ->
                    Text(text = "Speed: %.1f km/h".format(record.samples.first().speed.inKilometersPerHour))
                }
            } else {
                Text(text = "No speed records available.")
            }


            Spacer(modifier = Modifier.height(24.dp))


            if (caloriesRecords.isNotEmpty()) {
                Text(text = "Calories Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                caloriesRecords.forEach { record ->
                    Text(text = "Calories: ${record.energy.inKilocalories.roundToInt()} kcal")
                }
            } else {
                Text(text = "No calories records available.")
            }
        }
    }
}

