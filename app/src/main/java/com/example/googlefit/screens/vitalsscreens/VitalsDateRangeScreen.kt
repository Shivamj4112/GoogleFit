package com.example.googlefit.screens.vitalsscreens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.data.VitalType.BLOOD_PRESSURE
import com.example.googlefit.data.VitalType.HEART_RATE
import com.example.googlefit.data.VitalType.RESPIRATORY_RATE
import com.example.googlefit.data.VitalType.BODY_TEMPERATURE
import com.example.googlefit.data.VitalType.OXYGEN_SATURATION
import com.example.googlefit.navigation.Route.VITALS_DETAILS_SCREEN
import com.example.googlefit.utils.DateRange
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.getWeekday
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun VitalsDateRangeScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    vitals: String
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

            Spacer(modifier = Modifier.height(10.sdp))
            DateRange(healthManager)
            Spacer(modifier = Modifier.height(10.sdp))

            when (vitals) {
                "$HEART_RATE" -> {
                    if (heartRecords.isNotEmpty()) {
                        val displayedDates = mutableSetOf<String>()

                        heartRecords.reversed().forEach { record ->
                            val data = record.samples.first()
                            val formattedDate = formateDate(data.time.toString())


                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val bpmValues = heartRecords.flatMap { record -> record.samples.map { it.beatsPerMinute } }
                                val minBpm = bpmValues.minOrNull() ?: 0
                                val maxBpm = bpmValues.maxOrNull() ?: 0

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$VITALS_DETAILS_SCREEN/$HEART_RATE/$formattedDate")
                                            Log.d(
                                                "VitalsDateRangeScreen",
                                                "VitalsDateRangeScreen: $formattedDate"
                                            )
                                        }
                                        .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                ) {
                                    Row {
                                        Text(
                                            text = "${getWeekday(data.time.toString())},",
                                            fontSize = 10.ssp
                                        )
                                        Text(text = formattedDate, fontSize = 10.ssp)
                                    }
                                    Text(
                                        text = "$minBpm-$maxBpm bpm",
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }
                        }
                    } else {
                        Text(text = "No data found", color = Color.Gray)
                    }
                }

                "$BLOOD_PRESSURE" -> {
                    if (bloodPressureRecord.isNotEmpty()) {
                        val displayedDates = mutableSetOf<String>()

                        bloodPressureRecord.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val dateWiseBloodPressureRecords = bloodPressureRecord.groupBy { record ->
                                    formateDate(record.time.toString())
                                }
                                val targetDateRecords = dateWiseBloodPressureRecords[formattedDate] ?: emptyList()

                                val minSystolic = targetDateRecords.minOf { it.systolic.inMillimetersOfMercury.toInt() }
                                val maxSystolic = targetDateRecords.maxOf { it.systolic.inMillimetersOfMercury.toInt() }
                                val minDiastolic = targetDateRecords.minOf { it.diastolic.inMillimetersOfMercury.toInt() }
                                val maxDiastolic = targetDateRecords.maxOf { it.diastolic.inMillimetersOfMercury.toInt() }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$VITALS_DETAILS_SCREEN/$BLOOD_PRESSURE/$formattedDate")
                                            Log.d(
                                                "VitalsDateRangeScreen",
                                                "VitalsDateRangeScreen: $formattedDate"
                                            )
                                        }
                                        .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                ) {
                                    Row {
                                        Text(
                                            text = "${getWeekday(record.time.toString())},",
                                            fontSize = 10.ssp
                                        )
                                        Text(text = formattedDate, fontSize = 10.ssp)
                                    }
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
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                            }
                        }
                    } else {
                        Text(text = "No data found", color = Color.Gray)
                    }
                }

                "$RESPIRATORY_RATE" -> {
                    if (respiratoryRateRecord.isNotEmpty()) {
                        val displayedDates = mutableSetOf<String>()

                        respiratoryRateRecord.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val dateWiseRespiratoryRateRecords = respiratoryRateRecord.groupBy { record ->
                                    formateDate(record.time.toString())
                                }
                                val targetDateRecords = dateWiseRespiratoryRateRecords[formattedDate] ?: emptyList()

                                val minRpm = targetDateRecords.minOf { it.rate.toInt() }
                                val maxRpm = targetDateRecords.maxOf { it.rate.toInt() }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$VITALS_DETAILS_SCREEN/${RESPIRATORY_RATE}/$formattedDate")
                                            Log.d(
                                                "VitalsDateRangeScreen",
                                                "VitalsDateRangeScreen: $formattedDate"
                                            )
                                        }
                                        .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                ) {
                                    Row {
                                        Text(
                                            text = "${getWeekday(record.time.toString())},",
                                            fontSize = 10.ssp
                                        )
                                        Text(text = formattedDate, fontSize = 10.ssp)
                                    }

                                    val rpmText = if (minRpm == maxRpm) {
                                        "$minRpm"
                                    } else {
                                        "$minRpm-$maxRpm"
                                    }
                                    Text(
                                        text = "$rpmText rpm",
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                            }
                        }
                    } else {
                        Text(text = "No data found", color = Color.Gray)
                    }
                }

                "$BODY_TEMPERATURE" -> {
                    if (bodyTemperatureRecord.isNotEmpty()) {
                        val displayedDates = mutableSetOf<String>()

                        bodyTemperatureRecord.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val dateWisebodyTemperatureRecords = bodyTemperatureRecord.groupBy { record ->
                                    formateDate(record.time.toString())
                                }
                                val targetDateRecords = dateWisebodyTemperatureRecords[formattedDate] ?: emptyList()

                                val minTemperature = targetDateRecords.minOf { it.temperature.inFahrenheit.toInt() }
                                val maxTemperature = targetDateRecords.maxOf { it.temperature.inFahrenheit.toInt() }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$VITALS_DETAILS_SCREEN/${BODY_TEMPERATURE}/$formattedDate")
                                            Log.d(
                                                "VitalsDateRangeScreen",
                                                "VitalsDateRangeScreen: $formattedDate"
                                            )
                                        }
                                        .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                ) {
                                    Row {
                                        Text(
                                            text = "${getWeekday(record.time.toString())},",
                                            fontSize = 10.ssp
                                        )
                                        Text(text = formattedDate, fontSize = 10.ssp)
                                    }

                                    val temperatureText = if (minTemperature == maxTemperature) {
                                        "$minTemperature"
                                    } else {
                                        "$minTemperature-$maxTemperature"
                                    }
                                    Text(
                                        text = "$temperatureText °F",
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                            }
                        }
                    } else {
                        Text(text = "No data found", color = Color.Gray)
                    }
                }

                "$OXYGEN_SATURATION" -> {
                    if (oxygenRecords.isNotEmpty()) {
                        val displayedDates = mutableSetOf<String>()

                        oxygenRecords.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val dateWiseOxygenRecords = oxygenRecords.groupBy { record ->
                                    formateDate(record.time.toString())
                                }
                                val targetDateRecords = dateWiseOxygenRecords[formattedDate] ?: emptyList()

                                val minOxygen = targetDateRecords.minOf { it.percentage.value.toInt() }
                                val maxOxygen  = targetDateRecords.maxOf { it.percentage.value.toInt() }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$VITALS_DETAILS_SCREEN/${OXYGEN_SATURATION}/$formattedDate")
                                            Log.d(
                                                "VitalsDateRangeScreen",
                                                "VitalsDateRangeScreen: $formattedDate"
                                            )
                                        }
                                        .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                ) {
                                    Row {
                                        Text(
                                            text = "${getWeekday(record.time.toString())},",
                                            fontSize = 10.ssp
                                        )
                                        Text(text = formattedDate, fontSize = 10.ssp)
                                    }

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

                            }
                        }
                    } else {
                        Text(text = "No data found", color = Color.Gray)
                    }
                }
            }

        }
    }


}