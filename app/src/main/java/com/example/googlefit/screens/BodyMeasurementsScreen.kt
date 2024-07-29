package com.example.googlefit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager

@Composable
fun BodyMeasurementsScreen(healthManager: HealthManager, navController: NavHostController) {

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
                weightRecords.forEach { record ->
                    ContentCard("Weight Records", "${record.weight.inKilograms} kg")
                }
            } else {
                Text(text = "No weight records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (heightRecords.isNotEmpty()) {
                heightRecords.forEach { record ->
                    ContentCard("Height Records", "%.1f ft".format(record.height.inFeet))
                }
            } else {
                Text(text = "No height records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (bodyFatRecords.isNotEmpty()) {
                bodyFatRecords.forEach { record ->
                    ContentCard("Body Fat Records", "${record.percentage}")
                }
            } else {
                Text(text = "No body fat records available.")
            }
        }
    }
}

@Composable
fun ContentCard(title : String , message  :String) {

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(15.dp))
                    .padding(horizontal = 40.dp, vertical = 20.dp)
            ) {
                Text(text = title, color = Color.White, fontSize = 24.sp ,fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}