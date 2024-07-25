package com.example.googlefit.screens

import com.example.googlefit.HealthManager
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
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.navigation.NavHostController

@Composable
fun NutritionScreen(healthManager: HealthManager, navController: NavHostController) {

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val hydrationRecords by produceState<List<HydrationRecord>>(initialValue = emptyList()) {
                value = healthManager.readHydrationInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }

            val caloriesConsumedRecords by produceState<List<NutritionRecord>>(initialValue = emptyList()) {
                value = healthManager.readNutritionRecordInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (hydrationRecords.isNotEmpty()) {
                Text(text = "Hydration Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                hydrationRecords.forEach { record ->
                    Text(text = "Hydration Records: ${record.volume.inMilliliters.toInt()} mL")
                }
            } else {
                Text(text = "No hydration records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (caloriesConsumedRecords.isNotEmpty()) {
                Text(text = "Calories Consumed Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                caloriesConsumedRecords.forEach { record ->
                    Text(text = "Calories consumed Records: ${record.energy?.inKilocalories?.toInt()} Cal")
                }
            } else {
                Text(text = "No calories burned records available.")
            }
        }
    }
}