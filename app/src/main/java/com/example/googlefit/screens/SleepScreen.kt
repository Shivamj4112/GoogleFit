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
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavHostController
import com.example.googlefit.utils.util.formatLastModifiedTime
import java.time.temporal.ChronoUnit

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
                    start = java.time.Instant.now().minus(30, ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (sleepRecords.isNotEmpty()) {
                Text(text = "Sleep Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                sleepRecords.forEach { record ->
                    val formattedStartTime = formatLastModifiedTime(record.startTime.toString(),is24HourFormat = false)
                    val formattedEndTime = formatLastModifiedTime(record.endTime.toString(),is24HourFormat = false)
                    Text(text = "Sleep Records: $formattedStartTime -> $formattedEndTime")
                }
            } else {
                Text(text = "No sleep records available.")
            }
        }
    }

}