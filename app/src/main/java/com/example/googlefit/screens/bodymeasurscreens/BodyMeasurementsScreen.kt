package com.example.googlefit.screens.bodymeasurscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.data.BodyMeasurementType.BODY_FAT
import com.example.googlefit.data.BodyMeasurementType.HEIGHT
import com.example.googlefit.data.BodyMeasurementType.METABOLIC_RATE
import com.example.googlefit.data.BodyMeasurementType.WEIGHT
import com.example.googlefit.navigation.Route.BODY_MEASUREMENT_RANGE_SCREEN
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.formateDate
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay

@Composable
fun BodyMeasurementsScreen(healthManager: HealthManager, navController: NavHostController) {

    val weightRecords = healthManager.weightRecords.observeAsState(emptyList())
    val heightRecords = healthManager.heightRecords.observeAsState(emptyList())
    val bodyFatRecords = healthManager.bodyFatRecords.observeAsState(emptyList())
    val metabolicRateRecords = healthManager.basalMetabolicRate.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()

    LaunchedEffect(Unit) {
        delay(50)
        healthManager.setDateRange("Month")
        healthManager.setRange("Month")
    }

    LaunchedEffect(dateRange) {
        delay(50)
        healthManager.fetchBodyMeasurementsData()
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp)
        ) {

            TopBar(navController = navController, title = "Body Measurements")

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 12.sdp, vertical = 10.dp)
                            .clickable {
                                if (weightRecords.value.isNotEmpty()) {
                                    navController.navigate("$BODY_MEASUREMENT_RANGE_SCREEN/$WEIGHT")
                                }
                            }
                    ) {
                        if (weightRecords.value.isNotEmpty()) {
                            val lastRecord = weightRecords.value.last()
                            Text(
                                text = "Weight",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                color = Color.White,
                                text = "${formateDate(lastRecord.time.toString())} at ${
                                    formatLastModifiedTime(
                                        lastRecord.time.toString()
                                    )
                                }",
                                fontSize = 8.ssp,
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row {
                                Text(
                                    text = lastRecord.weight.inKilograms.toString(),
                                    color = Color.White,
                                    fontSize = 17.ssp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    modifier = Modifier.padding(top = 2.sdp),
                                    text = "kg",
                                    color = Color.White,
                                    fontSize = 8.ssp
                                )
                            }

                        } else {
                            Text(
                                text = "No weight records available",
                                color = Color.White,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 12.sdp, vertical = 10.dp)
                            .clickable {
                                if (heightRecords.value.isNotEmpty()) {
                                    navController.navigate("$BODY_MEASUREMENT_RANGE_SCREEN/$HEIGHT")
                                }
                            }
                    ) {
                        if (heightRecords.value.isNotEmpty()) {
                            val lastRecord = heightRecords.value.last()
                            Text(
                                text = "Height",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                color = Color.White,
                                text = "${formateDate(lastRecord.time.toString())} at ${
                                    formatLastModifiedTime(
                                        lastRecord.time.toString()
                                    )
                                }",
                                fontSize = 8.ssp,
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row {
                                Text(
                                    text = "%.1f ".format(lastRecord.height.inFeet),
                                    color = Color.White,
                                    fontSize = 17.ssp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = Modifier.padding(top = 2.sdp),
                                    text = "ft",
                                    color = Color.White,
                                    fontSize = 8.ssp
                                )
                            }

                        } else {
                            Text(
                                text = "No height records available",
                                color = Color.White,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 12.sdp, vertical = 10.dp)
                            .clickable {
                                if (bodyFatRecords.value.isNotEmpty()) {
                                    navController.navigate("$BODY_MEASUREMENT_RANGE_SCREEN/$BODY_FAT")
                                }
                            }
                    ) {
                        if (bodyFatRecords.value.isNotEmpty()) {
                            val lastRecord = bodyFatRecords.value.last()
                            Text(
                                text = "Body fat",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                color = Color.White,
                                text = "${formateDate(lastRecord.time.toString())} at ${
                                    formatLastModifiedTime(
                                        lastRecord.time.toString()
                                    )
                                }",
                                fontSize = 8.ssp,
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row {
                                Text(
                                    text = "%.1f ".format(lastRecord.percentage.value),
                                    color = Color.White,
                                    fontSize = 17.ssp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = Modifier.padding(top = 2.sdp),
                                    text = "%",
                                    color = Color.White,
                                    fontSize = 8.ssp
                                )
                            }

                        } else {
                            Text(
                                text = "No body fat records available",
                                color = Color.White,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 12.sdp, vertical = 10.dp)
                            .clickable {
                                if (metabolicRateRecords.value.isNotEmpty()) {
                                    navController.navigate("$BODY_MEASUREMENT_RANGE_SCREEN/$METABOLIC_RATE")
                                }
                            }
                    ) {
                        if (metabolicRateRecords.value.isNotEmpty()) {
                            val lastRecord = metabolicRateRecords.value.last()
                            Text(
                                text = "Metabolic Rate",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                color = Color.White,
                                text = "${formateDate(lastRecord.time.toString())} at ${
                                    formatLastModifiedTime(
                                        lastRecord.time.toString()
                                    )
                                }",
                                fontSize = 8.ssp,
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row {
                                Text(
                                    text = "${lastRecord.basalMetabolicRate.inKilocaloriesPerDay.toInt()} ",
                                    color = Color.White,
                                    fontSize = 17.ssp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    modifier = Modifier.padding(top = 2.sdp),
                                    text = "Cal",
                                    color = Color.White,
                                    fontSize = 8.ssp
                                )
                            }

                        } else {
                            Text(
                                text = "No body fat records available",
                                color = Color.White,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }


            }
        }
    }
}

@Composable
fun ContentCard(title: String, message: String) {

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
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}