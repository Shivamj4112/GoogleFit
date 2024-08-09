package com.example.googlefit.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import ir.kaaveh.sdpcompose.ssp

@Composable
fun CycleTrackingScreen(healthManager: HealthManager, navController: NavHostController) {


    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "No data available",
                fontSize = 18.ssp,
                color = Color.Red,
            )

//            val cyclingCadence by produceState<List<CervicalMucusRecord>>(initialValue = emptyList()) {
//                value = healthManager.readCyclePedalingCadence(
//                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
//                    end = java.time.Instant.now()
//                )
//            }


//            if (cyclingCadence.isNotEmpty()) {
//                Text(
//                    text = "Cycling Cadence Records:",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp
//                )
//                cyclingCadence.forEach { record ->
//                    Text(text = "Cycling Cadence Records: ${record.metadata.lastModifiedTime} rpm")
//                }
//            } else {
//                Text(text = "No cycling cadence records available.")
//            }

        }
    }
}
