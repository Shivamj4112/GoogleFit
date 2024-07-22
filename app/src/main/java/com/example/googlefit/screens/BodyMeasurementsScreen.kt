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
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.navigation.NavHostController

@Composable
fun BodyMeasurementsScreen(healthManager: HealthManager, navController: NavHostController){

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val weightRecords by produceState<List<WeightRecord>>(initialValue = emptyList()) {
                value = healthManager.readWeightInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val heightRecords by produceState<List<HeightRecord>>(initialValue = emptyList()) {
                value = healthManager.readHeightInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val bodyFatRecords by produceState<List<BodyFatRecord>>(initialValue = emptyList()) {
                value = healthManager.readBodyFatInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (weightRecords.isNotEmpty()) {
                Text(text = "Weight Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                weightRecords.forEach { record ->
                    Text(text = "Weight: %.1f lbs".format(record.weight.inPounds) + "  ||  ${record.weight.inKilograms} kg")
                }
            } else {
                Text(text = "No weight records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (heightRecords.isNotEmpty()) {
                Text(text = "Height Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                heightRecords.forEach { record ->
                    Text(text = "Height: %.1f ft".format(record.height.inFeet))
                }
            } else {
                Text(text = "No height records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))


            if (bodyFatRecords.isNotEmpty()) {
                Text(text = "Body Fat Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                bodyFatRecords.forEach { record ->
                    Text(text = "Body fat: ${record.percentage}")
                }
            } else {
                Text(text = "No body fat records available.")
            }
        }
    }
}