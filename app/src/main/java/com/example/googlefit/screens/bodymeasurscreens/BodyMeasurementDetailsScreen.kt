package com.example.googlefit.screens.bodymeasurscreens

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
import com.example.googlefit.data.BodyMeasurementType.BODY_FAT
import com.example.googlefit.data.BodyMeasurementType.HEIGHT
import com.example.googlefit.data.BodyMeasurementType.WEIGHT
import com.example.googlefit.data.BodyMeasurementType.METABOLIC_RATE
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.formateDate
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BodyMeasurementDetailsScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    measurement: String,
    date: String
) {

    val weightRecords = healthManager.weightRecords.observeAsState(emptyList())
    val heightRecords = healthManager.heightRecords.observeAsState(emptyList())
    val bodyFatRecords = healthManager.bodyFatRecords.observeAsState(emptyList())
    val metabolicRateRecords = healthManager.basalMetabolicRate.observeAsState(emptyList())
    val range by healthManager.range.observeAsState()

    LaunchedEffect(range) {
        healthManager.fetchBodyMeasurementsData()
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
        ) {

            val formattedBodyMeasurements = measurement.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() }
            TopBar(navController, formattedBodyMeasurements)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.sdp)
                    .padding(top = 10.sdp)
                    .verticalScroll(rememberScrollState()),
            ) {
                when (measurement) {
                    "$WEIGHT" -> {
                        if (weightRecords.value.isNotEmpty()) {


                            val dateWiseWeightRecords = weightRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }
                            val targetDateRecords = dateWiseWeightRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size

                                val minWeight =
                                    targetDateRecords.minByOrNull { it.weight.inKilograms }?.weight?.inKilograms
                                        ?: 0.0
                                val maxWeight =
                                    targetDateRecords.maxByOrNull { it.weight.inKilograms }?.weight?.inKilograms
                                        ?: 0.0

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val weightText = if (minWeight == maxWeight) {
                                        "%.1f kg".format(minWeight)
                                    } else {
                                        "%.1f - %.1f kg".format(minWeight,maxWeight)
                                    }
                                    Text(
                                        text = weightText,
                                        color = Color.Red,
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "kg",
                                        color = Color.Red,
                                        fontSize = 8.ssp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.sdp)
                                    )

                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Weight • ",
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
                                            text = "${record.weight.inKilograms} kg",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No weight records found for $date")
                            }
                        }
                    }
                    "$HEIGHT" -> {
                        if (heightRecords.value.isNotEmpty()) {

                            val dateWiseHeightRecords = heightRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }
                            val targetDateRecords = dateWiseHeightRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size

                                val minHeight =
                                    targetDateRecords.minByOrNull { it.height.inFeet }?.height?.inFeet
                                        ?: 0.0
                                val maxHeight =
                                    targetDateRecords.maxByOrNull { it.height.inFeet }?.height?.inFeet
                                        ?: 0.0

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val heightText = if (minHeight == maxHeight) {
                                        "%.1f ".format(minHeight)
                                    } else {
                                        "%.1f - %.1f ".format(minHeight, maxHeight)
                                    }
                                    Text(
                                        text = heightText,
                                        color = Color.Red,
                                        fontSize = 16.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "ft",
                                        color = Color.Red,
                                        fontSize = 10.ssp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.sdp)
                                    )

                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Height • ",
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
                                            text = "%.1f ft".format(record.height.inFeet),
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No weight records found for $date")
                            }
                        }
                    }
                    "$BODY_FAT" -> {
                        if (bodyFatRecords.value.isNotEmpty()) {

                            val dateWiseBodyFatRecords = bodyFatRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }
                            val targetDateRecords = dateWiseBodyFatRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size


                                val minBodyFat = targetDateRecords.minByOrNull { it.percentage.value }?.percentage?.value ?: 0.0
                                val maxBodyFat = targetDateRecords.maxByOrNull { it.percentage.value }?.percentage?.value ?: 0.0

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val bodyFatText = if (minBodyFat == maxBodyFat) {
                                        "$minBodyFat "
                                    } else {
                                        "$minBodyFat - $maxBodyFat "
                                    }

                                    Text(
                                        text = bodyFatText,
                                        color = Color.Red,
                                        fontSize = 16.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )


                                    Text(
                                        text = "%",
                                        color = Color.Red,
                                        fontSize = 10.ssp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.sdp)
                                    )

                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Body fat • ",
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
                                            text = "${record.percentage}",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No body fat records found for $date")
                            }
                        }
                    }
                    "$METABOLIC_RATE" -> {
                        if (metabolicRateRecords.value.isNotEmpty()) {

                            val dateWiseMetaBolicRateRecords = metabolicRateRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }
                            val targetDateRecords = dateWiseMetaBolicRateRecords[date]

                            if (targetDateRecords != null) {
                                val size = targetDateRecords.size

                                val minMetabolicRate = targetDateRecords.minByOrNull { it.basalMetabolicRate.inKilocaloriesPerDay.toInt() }?.basalMetabolicRate?.inKilocaloriesPerDay?.toInt() ?: 0
                                val maxMetabolicRate = targetDateRecords.maxByOrNull { it.basalMetabolicRate.inKilocaloriesPerDay.toInt() }?.basalMetabolicRate?.inKilocaloriesPerDay?.toInt() ?: 0

                                Text(
                                    text = date,
                                    fontSize = 20.ssp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(20.sdp))

                                Row {
                                    val metaBolicRateText = if (minMetabolicRate == maxMetabolicRate) {
                                        "$minMetabolicRate "
                                    } else {
                                        "$minMetabolicRate - $maxMetabolicRate "
                                    }
                                    Text(
                                        text = metaBolicRateText,
                                        color = Color.Red,
                                        fontSize = 12.ssp,
                                        fontWeight = FontWeight.ExtraBold
                                    )


                                    Text(
                                        text = "Cal",
                                        color = Color.Red,
                                        fontSize = 10.ssp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.sdp)
                                    )

                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Body fat • ",
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
                                            text = "${record.basalMetabolicRate.inKilocaloriesPerDay.toInt()} Cal",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )

                                    }

                                }
                            } else {
                                Text(text = "No body fat records found for $date")
                            }
                        }
                    }
                }
            }
        }
    }

}
