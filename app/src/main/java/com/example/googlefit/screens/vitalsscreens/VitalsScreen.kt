package com.example.googlefit.screens.vitalsscreens

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
import com.example.googlefit.data.VitalType.BLOOD_PRESSURE
import com.example.googlefit.data.VitalType.BLOOD_GLUCOSE
import com.example.googlefit.data.VitalType.BODY_TEMPERATURE
import com.example.googlefit.data.VitalType.HEART_RATE
import com.example.googlefit.data.VitalType.OXYGEN_SATURATION
import com.example.googlefit.data.VitalType.RESPIRATORY_RATE
import com.example.googlefit.navigation.Route.VITALS_RANGE_SCREEN
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.formateDate
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun VitalsScreen(healthManager: HealthManager, navController: NavHostController) {

    val heartRecords by healthManager.heartRateRecords.observeAsState(emptyList())
    val bloodPressureRecord by healthManager.bloodPressureRecords.observeAsState(emptyList())
    val bloodGlucoseRecord by healthManager.bloodGlucoseRecords.observeAsState(emptyList())
    val respiratoryRateRecord by healthManager.respiratoryRateRecords.observeAsState(emptyList())
    val oxygenRecords by healthManager.oxygenSaturationRecords.observeAsState(emptyList())
    val bodyTemperatureRecord by healthManager.bodyTemperatureRecords.observeAsState(emptyList())
    val range by healthManager.range.observeAsState()

    LaunchedEffect(range) {
        healthManager.fetchVitalsData()
    }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp)
        ) {

            TopBar(navController = navController, title = "Vitals")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.sdp)

            ) {
                // Heart Rate
                Surface {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 12.sdp, vertical = 10.dp)
                                .clickable {
                                    if (heartRecords.isNotEmpty()) {
                                        navController.navigate("$VITALS_RANGE_SCREEN/$HEART_RATE")
                                    }
                                }
                        ) {
                            if (heartRecords.isNotEmpty()) {
                                val lastRecord = heartRecords.last().samples.last()

                                Text(
                                    color = Color.White,
                                    text = "Heart Rate",
                                    fontSize = 12.ssp,
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
                                        text = lastRecord.beatsPerMinute.toString(),
                                        color = Color.White,
                                        fontSize = 17.ssp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        modifier = Modifier.padding(top = 2.sdp),
                                        text = "bpm",
                                        color = Color.White,
                                        fontSize = 8.ssp
                                    )
                                }
                            } else {
                                Text(
                                    text = "No heart rate records available.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }

                // Blood Pressure
                Surface {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 12.sdp, vertical = 10.dp)
                                .clickable {
                                    if (bloodPressureRecord.isNotEmpty()) {
                                        navController.navigate("$VITALS_RANGE_SCREEN/${BLOOD_PRESSURE}")
                                    }
                                }
                        ) {
                            if (bloodPressureRecord.isNotEmpty()) {
                                val lastRecord = bloodPressureRecord.last()
                                Text(
                                    text = "Blood Pressure Records",
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
                                        text = "${lastRecord.systolic.inMillimetersOfMercury.toInt()}/${lastRecord.diastolic.inMillimetersOfMercury.toInt()}",
                                        color = Color.White,
                                        fontSize = 17.ssp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        modifier = Modifier.padding(top = 2.sdp),
                                        text = "mmHg",
                                        color = Color.White,
                                        fontSize = 8.ssp
                                    )
                                }
                            } else {
                                Text(
                                    text = "No blood pressure records available.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }

                // Blood Glucose
                Surface {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 12.sdp, vertical = 10.dp)
                                .clickable {
                                    if (bloodGlucoseRecord.isNotEmpty()) {
                                        navController.navigate("$VITALS_RANGE_SCREEN/$BLOOD_GLUCOSE")
                                    }
                                }
                        ) {
                            if (bloodGlucoseRecord.isNotEmpty()) {
                                val lastRecord = bloodGlucoseRecord.last()
                                Text(
                                    text = "Blood Glucose Records",
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
                                        text = "%.1f ".format(lastRecord.level.inMillimolesPerLiter),
                                        color = Color.White,
                                        fontSize = 17.ssp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        modifier = Modifier.padding(top = 2.sdp),
                                        text = "mmol/L",
                                        color = Color.White,
                                        fontSize = 8.ssp
                                    )
                                }
                            } else {
                                Text(
                                    text = "No blood glucose records available.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }

                // Respiratory Rate
                Surface {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 12.sdp, vertical = 10.dp)
                                .clickable {
                                    if (respiratoryRateRecord.isNotEmpty()) {
                                        navController.navigate("$VITALS_RANGE_SCREEN/${RESPIRATORY_RATE}")
                                    }
                                }
                        ) {
                            if (respiratoryRateRecord.isNotEmpty()) {
                                val lastRecord = respiratoryRateRecord.last()
                                Text(
                                    text = "Respiratory Rate Records",
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
                                        text = lastRecord.rate.toInt().toString(),
                                        color = Color.White,
                                        fontSize = 17.ssp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        modifier = Modifier.padding(top = 2.sdp),
                                        text = "rpm",
                                        color = Color.White,
                                        fontSize = 8.ssp
                                    )
                                }

                            } else {
                                Text(
                                    text = "No respiratory rate records available.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }

                // Body Temperature
                Surface {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 12.sdp, vertical = 10.dp)
                                .clickable {
                                    if (bodyTemperatureRecord.isNotEmpty()) {
                                        navController.navigate("$VITALS_RANGE_SCREEN/${BODY_TEMPERATURE}")
                                    }
                                }
                        ) {
                            if (bodyTemperatureRecord.isNotEmpty()) {
                                val lastRecord = bodyTemperatureRecord.last()
                                Text(
                                    text = "Body Temperature Records",
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
                                        text = lastRecord.temperature.inFahrenheit.toInt().toString(),
                                        color = Color.White,
                                        fontSize = 17.ssp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        modifier = Modifier.padding(top = 2.sdp),
                                        text = "°F",
                                        color = Color.White,
                                        fontSize = 8.ssp
                                    )
                                }

                            } else {
                                Text(
                                    text = "No body temperature records available.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }

                // Oxygen Saturation
                Surface {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 12.sdp, vertical = 10.dp)
                                .clickable {
                                    if (oxygenRecords.isNotEmpty()) {
                                        navController.navigate("$VITALS_RANGE_SCREEN/${OXYGEN_SATURATION}")
                                    }
                                }
                        ) {
                            if (oxygenRecords.isNotEmpty()) {
                                val lastRecord = oxygenRecords.last()
                                Text(
                                    text = "Oxygen Records",
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

                                Text(
                                    text = "${lastRecord.percentage.value.toInt()}%",
                                    color = Color.White,
                                    fontSize = 17.ssp,
                                    fontWeight = FontWeight.Bold
                                )

                            } else {
                                Text(
                                    text = "No oxygen records available.",
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
}

