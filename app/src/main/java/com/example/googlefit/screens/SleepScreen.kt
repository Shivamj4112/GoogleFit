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
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavHostController

@Composable
fun SleepScreen(healthManager: HealthManager, navController: NavHostController) {

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val sleepRecords by produceState<List<SleepSessionRecord>>(initialValue = emptyList()) {
                value = healthManager.readSleepInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (sleepRecords.isNotEmpty()) {
                Text(text = "Sleep Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                sleepRecords.forEach { record ->
                    Text(text = "Sleep Records: ${record.stages.firstOrNull()?.stage} h")
                }
            } else {
                Text(text = "No sleep records available.")
            }
        }
    }

}