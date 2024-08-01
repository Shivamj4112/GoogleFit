package com.example.googlefit.screens.vitalsscreens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.data.VitalType.HEART_RATE
import com.example.googlefit.data.VitalType.BLOOD_PRESSURE
import com.example.googlefit.data.VitalType.RESPIRATORY_RATE
import com.example.googlefit.data.VitalType.BODY_TEMPERATURE
import com.example.googlefit.data.VitalType.OXYGEN_SATURATION
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.formateDate
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun VitalsDetailsScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    vitals: String,
    date: String
) {

    val heartRecords by healthManager.heartRateRecords.observeAsState(emptyList())
    val bloodPressureRecord by healthManager.bloodPressureRecords.observeAsState(emptyList())
    val respiratoryRateRecord by healthManager.respiratoryRateRecords.observeAsState(emptyList())
    val oxygenRecords by healthManager.oxygenSaturationRecords.observeAsState(emptyList())
    val bodyTemperatureRecord by healthManager.bodyTemperatureRecords.observeAsState(emptyList())
    val range by healthManager.range.observeAsState()

    LaunchedEffect(range) {
        healthManager.fetchVitalsData()
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
        ) {

            TopBar(navController, "Vitals")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.sdp)
                    .padding(top = 10.sdp)
                    .verticalScroll(rememberScrollState()),
            ) {
                when (vitals) {
                    "$HEART_RATE" -> {
                        if (heartRecords.isNotEmpty()) {
                            val dateWiseHeartRecords = heartRecords.groupBy { record ->
                                formateDate(record.samples.first().time.toString())
                            }

                            val targetDateRecords = dateWiseHeartRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size
                                val minBpm = targetDateRecords.minOf { it.samples.first().beatsPerMinute }
                                val maxBpm = targetDateRecords.maxOf { it.samples.first().beatsPerMinute }

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    Text(
                                        text = "$minBpm-$maxBpm ",
                                        color = Color.Red,
                                        fontSize = 16.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "bpm",
                                        color = Color.Red,
                                        fontSize = 8.ssp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.sdp)
                                    )

                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Heart rate • ",
                                        fontSize = 10.ssp,
                                    )
                                    Text(
                                        "$size entries",
                                        fontSize = 9.ssp,
                                    )
                                }

                                targetDateRecords.reversed().forEach { record ->
                                    val data = record.samples.first()

                                    val formattedDate = formateDate(data.time.toString())


                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.sdp, bottom = 10.sdp),
                                    ) {

                                        Text(
                                            text = formatLastModifiedTime(data.time.toString()),
                                            fontSize = 10.ssp
                                        )

                                        Text(
                                            text = "${data.beatsPerMinute} bpm",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No heart rate records found for $date")
                            }
                        }
                    }
                    "$BLOOD_PRESSURE" -> {
                        if (bloodPressureRecord.isNotEmpty()) {
                            val dateWiseBloodPressureRecords = bloodPressureRecord.groupBy { record ->
                                formateDate(record.time.toString())
                            }

                            val targetDateRecords = dateWiseBloodPressureRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size
                                val minSystolic = targetDateRecords.minOf { it.systolic.inMillimetersOfMercury.toInt() }
                                val maxSystolic = targetDateRecords.maxOf { it.systolic.inMillimetersOfMercury.toInt() }
                                val minDiastolic = targetDateRecords.minOf { it.diastolic.inMillimetersOfMercury.toInt() }
                                val maxDiastolic = targetDateRecords.maxOf { it.diastolic.inMillimetersOfMercury.toInt() }

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val systolicText = if (minSystolic == maxSystolic) {
                                        "$minSystolic"
                                    } else {
                                        "$minSystolic-$maxSystolic"
                                    }
                                    val diastolicText = if (minDiastolic == maxDiastolic) {
                                        "$minDiastolic"
                                    } else {
                                        "$minDiastolic-$maxDiastolic"
                                    }
                                    Text(
                                        text = "$systolicText mmHg / $diastolicText mmHg",
                                        color = Color.Red,
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Blood Pressure • ",
                                        fontSize = 10.ssp,
                                    )
                                    Text(
                                        "$size entries",
                                        fontSize = 9.ssp,
                                    )
                                }

                                targetDateRecords.reversed().forEach { record ->

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.sdp, bottom = 10.sdp),
                                    ) {

                                        Text(
                                            text = formatLastModifiedTime(record.time.toString()),
                                            fontSize = 10.ssp
                                        )

                                        Text(
                                            text = "${record.systolic.inMillimetersOfMercury.toInt()}/${record.diastolic.inMillimetersOfMercury.toInt()} mmHg",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No blood pressure records found for $date")
                            }
                        }
                    }
                    "$RESPIRATORY_RATE" -> {
                        if (respiratoryRateRecord.isNotEmpty()) {

                            val dateWiseRespiratoryRecords = respiratoryRateRecord.groupBy { record ->
                                formateDate(record.time.toString())
                            }

                            val targetDateRecords = dateWiseRespiratoryRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size

                                val minRpm = targetDateRecords.minOf { it.rate.toInt() }
                                val maxRpm = targetDateRecords.maxOf { it.rate.toInt() }

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val rpmText = if (minRpm == maxRpm) {
                                        "$minRpm"
                                    } else {
                                        "$minRpm-$maxRpm"
                                    }
                                    Text(
                                        text = "$rpmText rpm",
                                        color = Color.Red,
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Respiratory Rate • ",
                                        fontSize = 10.ssp,
                                    )
                                    Text(
                                        "$size entries",
                                        fontSize = 9.ssp,
                                    )
                                }

                                targetDateRecords.reversed().forEach { record ->

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.sdp, bottom = 10.sdp),
                                    ) {

                                        Text(
                                            text = formatLastModifiedTime(record.time.toString()),
                                            fontSize = 10.ssp
                                        )

                                        Text(
                                            text = "${record.rate.toInt()} rpm",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No blood pressure records found for $date")
                            }
                        }
                    }
                    "$BODY_TEMPERATURE" -> {
                        if (bodyTemperatureRecord.isNotEmpty()) {

                            val dateWisebodyTemperatureRecords = bodyTemperatureRecord.groupBy { record ->
                                formateDate(record.time.toString())
                            }
                            val targetDateRecords = dateWisebodyTemperatureRecords[date]


                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size
                                val minTemperature = targetDateRecords.minOf { it.temperature.inFahrenheit.toInt() }
                                val maxTemperature = targetDateRecords.maxOf { it.temperature.inFahrenheit.toInt() }

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val temperatureText = if (minTemperature == maxTemperature) {
                                        "$minTemperature"
                                    } else {
                                        "$minTemperature-$maxTemperature"
                                    }
                                    Text(
                                        text = "$temperatureText °F",
                                        color = Color.Red,
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Body Temperature Rate • ",
                                        fontSize = 10.ssp,
                                    )
                                    Text(
                                        "$size entries",
                                        fontSize = 9.ssp,
                                    )
                                }

                                targetDateRecords.reversed().forEach { record ->

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.sdp, bottom = 10.sdp),
                                    ) {

                                        Text(
                                            text = formatLastModifiedTime(record.time.toString()),
                                            fontSize = 10.ssp
                                        )

                                        Text(
                                            text = "${record.temperature.inFahrenheit.toInt()} °F",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No blood pressure records found for $date")
                            }
                        }
                    }
                    "$OXYGEN_SATURATION" -> {
                        if (oxygenRecords.isNotEmpty()) {

                            val dateWiseOxygenRecords = oxygenRecords.groupBy { record ->
                                formateDate(record.time.toString())
                            }
                            val targetDateRecords = dateWiseOxygenRecords[date]


                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size
                                val minOxygen = targetDateRecords.minOf { it.percentage.value.toInt() }
                                val maxOxygen  = targetDateRecords.maxOf { it.percentage.value.toInt() }

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val oxygenText = if (minOxygen == maxOxygen) {
                                        "$minOxygen"
                                    } else {
                                        "$minOxygen-$maxOxygen"
                                    }
                                    Text(
                                        text = "$oxygenText%",
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Oxygen • ",
                                        fontSize = 10.ssp,
                                    )
                                    Text(
                                        "$size entries",
                                        fontSize = 9.ssp,
                                    )
                                }

                                targetDateRecords.reversed().forEach { record ->

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.sdp, bottom = 10.sdp),
                                    ) {

                                        Text(
                                            text = formatLastModifiedTime(record.time.toString()),
                                            fontSize = 10.ssp
                                        )

                                        Text(
                                            text = "${record.percentage.value.toInt()}%",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No blood pressure records found for $date")
                            }
                        }
                    }

                    else -> {


                    }
                }
            }


        }
    }
}

