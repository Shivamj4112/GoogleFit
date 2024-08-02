package com.example.googlefit.screens.bodymeasurscreens

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
import com.example.googlefit.data.BodyMeasurementType.WEIGHT
import com.example.googlefit.data.BodyMeasurementType.HEIGHT
import com.example.googlefit.data.BodyMeasurementType.BODY_FAT
import com.example.googlefit.data.BodyMeasurementType.METABOLIC_RATE
import com.example.googlefit.navigation.Route.BODY_MEASUREMENT_DETAILS_SCREEN
import com.example.googlefit.utils.DateRange
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.getWeekday
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BodyMeasurDateRangeScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    bodyMeasurments: String
) {
    val weightRecords = healthManager.weightRecords.observeAsState(emptyList())
    val heightRecords = healthManager.heightRecords.observeAsState(emptyList())
    val bodyFatRecords = healthManager.bodyFatRecords.observeAsState(emptyList())
    val metabolicRateRecords = healthManager.basalMetabolicRate.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()

    LaunchedEffect(dateRange) {
        healthManager.fetchBodyMeasurementsData()
    }
    LaunchedEffect(Unit) {
        healthManager.setDateRange("Week")
    }


    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
        ) {
            // navigating back
            TopBar(navController, "Body Measurements")

            Spacer(modifier = Modifier.height(10.sdp))
            DateRange(healthManager)
            Spacer(modifier = Modifier.height(10.sdp))

            when (bodyMeasurments) {
                "$WEIGHT" -> {
                    if (weightRecords.value.isNotEmpty()) {
                        val displayedDates = mutableSetOf<String>()
                        val dateWiseWeightRecords = weightRecords.value.groupBy { record ->
                            formateDate(record.time.toString())
                        }

                        weightRecords.value.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val recordsForDate = dateWiseWeightRecords[formattedDate] ?: emptyList()
                                val maxWeight = recordsForDate.maxByOrNull { it.weight.inKilograms }?.weight?.inKilograms ?: 0.0
                                val minWeight = recordsForDate.minByOrNull { it.weight.inKilograms }?.weight?.inKilograms ?: 0.0



                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$WEIGHT/$formattedDate")
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
                                    val weightText = if (minWeight == maxWeight) {
                                        "$minWeight kg"
                                    } else {
                                        "$minWeight - $maxWeight kg"
                                    }
                                    Text(
                                        text = weightText,
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
                "$HEIGHT" -> {
                    if (heightRecords.value.isNotEmpty()) {

                        val displayedDates = mutableSetOf<String>()
                        val dateWiseHeightRecords = heightRecords.value.groupBy { record ->
                            formateDate(record.time.toString())
                        }

                        heightRecords.value.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val recordsForDate = dateWiseHeightRecords[formattedDate] ?: emptyList()
                                val maxHeight = recordsForDate.maxByOrNull { it.height.inFeet }?.height?.inFeet ?: 0.0
                                val minHeight = recordsForDate.minByOrNull { it.height.inFeet }?.height?.inFeet ?: 0.0



                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$HEIGHT/$formattedDate")
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
                                    val heightText = if (minHeight == maxHeight) {
                                        "%.1f ft".format(minHeight)
                                    } else {
                                        "%.1f - %.1f ft".format(minHeight, maxHeight)
                                    }
                                    Text(
                                        text = heightText,
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
                "$BODY_FAT" -> {
                    if (bodyFatRecords.value.isNotEmpty()) {

                        val displayedDates = mutableSetOf<String>()
                        val dateWiseBodyFatRecords = bodyFatRecords.value.groupBy { record ->
                            formateDate(record.time.toString())
                        }

                        bodyFatRecords.value.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val recordsForDate = dateWiseBodyFatRecords[formattedDate] ?: emptyList()

                                val minBodyFat = recordsForDate.minByOrNull { it.percentage }?.percentage ?: 0.0
                                val maxBodyFat = recordsForDate.maxByOrNull { it.percentage }?.percentage ?: 0.0



                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$BODY_FAT/$formattedDate")
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

                                    val bodyFatText = if (minBodyFat == maxBodyFat) {
                                        "$minBodyFat"
                                    } else {
                                        "$minBodyFat - $maxBodyFat"
                                    }

                                    Text(
                                        text = bodyFatText,
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
                "$METABOLIC_RATE" -> {
                    if (metabolicRateRecords.value.isNotEmpty()) {

                        val displayedDates = mutableSetOf<String>()
                        val dateWiseMetaBolicRateRecords = metabolicRateRecords.value.groupBy { record ->
                            formateDate(record.time.toString())
                        }

                        metabolicRateRecords.value.reversed().forEach { record ->
                            val formattedDate = formateDate(record.time.toString())

                            if (formattedDate !in displayedDates) {
                                displayedDates.add(formattedDate)

                                val recordsForDate = dateWiseMetaBolicRateRecords[formattedDate] ?: emptyList()

                                val minMetabolicRate = recordsForDate.minByOrNull { it.basalMetabolicRate.inKilocaloriesPerDay.toInt() }?.basalMetabolicRate?.inKilocaloriesPerDay?.toInt() ?: 0
                                val maxMetabolicRate = recordsForDate.maxByOrNull { it.basalMetabolicRate.inKilocaloriesPerDay.toInt() }?.basalMetabolicRate?.inKilocaloriesPerDay?.toInt() ?: 0

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$METABOLIC_RATE/$formattedDate")
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

                                    val metaBolicRateText = if (minMetabolicRate == maxMetabolicRate) {
                                        "$minMetabolicRate Cal"
                                    } else {
                                        "$minMetabolicRate - $maxMetabolicRate Cal"
                                    }

                                    Text(
                                        text = metaBolicRateText,
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