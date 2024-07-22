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
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.navigation.NavHostController

@Composable
fun VitalsScreen(healthManager: HealthManager, navController: NavHostController) {

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val heartRateRecords by produceState<List<HeartRateRecord>>(initialValue = emptyList()) {
                value = healthManager.readHeartRateInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }

            val restingHeartRateRecords by produceState<List<RestingHeartRateRecord>>(
                initialValue = emptyList()
            ) {
                value = healthManager.readRestingHeartInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val bloodPressureRecords by produceState<List<BloodPressureRecord>>(initialValue = emptyList()) {
                value = healthManager.readBloodPressureInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val respiratoryRateRecords by produceState<List<RespiratoryRateRecord>>(initialValue = emptyList()) {
                value = healthManager.readRespiratoryInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val bloodGlucoseRecords by produceState<List<BloodGlucoseRecord>>(initialValue = emptyList()) {
                value = healthManager.readBloodGlucoseInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val oxygenSaturationRecords by produceState<List<OxygenSaturationRecord>>(initialValue = emptyList()) {
                value = healthManager.readOxygenSaturationInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }
            val bodyTemperatureRecords by produceState<List<BodyTemperatureRecord>>(initialValue = emptyList()) {
                value = healthManager.readBodyTemperatureInputs(
                    start = java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS),
                    end = java.time.Instant.now()
                )
            }



            Spacer(modifier = Modifier.height(24.dp))

            if (heartRateRecords.isNotEmpty()) {
                Text(text = "Heart Records:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                heartRateRecords.forEach { record ->
                    Text(text = "Heart Rate: ${record.samples.first().beatsPerMinute} bpm")
                }
            } else {
                Text(text = "No heart records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (restingHeartRateRecords.isNotEmpty()) {
                Text(
                    text = "Resting Heart Records:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                restingHeartRateRecords.forEach { record ->
                    Text(text = "Resting Heart Rate: ${record.beatsPerMinute}")
                }
            } else {
                Text(text = "No heart resting records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (bloodPressureRecords.isNotEmpty()) {
                Text(
                    text = "Blood Pressure Records:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                bloodPressureRecords.forEach { record ->
                    Text(text = "Blood Pressure: ${record.systolic.inMillimetersOfMercury.toInt()}/${record.diastolic.inMillimetersOfMercury.toInt()} mmHg")
                }
            } else {
                Text(text = "No blood pressure records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (respiratoryRateRecords.isNotEmpty()) {
                Text(
                    text = "Respiratory rate Records:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                respiratoryRateRecords.forEach { record ->
                    Text(text = "Respiratory rate: ${record.rate.toInt()} rpm")
                }
            } else {
                Text(text = "No respiratory rate records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (bloodGlucoseRecords.isNotEmpty()) {
                Text(
                    text = "Blood Glucose Records:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                bloodGlucoseRecords.forEach { record ->
                    Text(text = "Blood glucose : ${record.level.inMillimolesPerLiter.toInt()} mmol/L ")
                }
            } else {
                Text(text = "No blood glucose records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (oxygenSaturationRecords.isNotEmpty()) {
                Text(
                    text = "Blood Glucose Records:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                oxygenSaturationRecords.forEach { record ->
                    Text(text = "Blood glucose : ${record.percentage.value.toInt()}%")
                }
            } else {
                Text(text = "No blood glucose records available.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (bodyTemperatureRecords.isNotEmpty()) {
                Text(
                    text = "Body temperature Records:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                bodyTemperatureRecords.forEach { record ->
                    Text(text = "Body temperature : %.1f °F".format(record.temperature.inFahrenheit))
                }
            } else {
                Text(text = "No body temperature records available.")
            }

        }
    }
}