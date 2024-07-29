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
import com.example.googlefit.utils.util.formatLastModifiedTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VitalsScreen(healthManager: HealthManager, navController: NavHostController) {

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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Day", "Week", "Month").forEach { range ->
                    Button(onClick = {
                        healthManager.setDateRange(range)
                        healthManager.setRange(range)
                    }) {
                        Text(text = range)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface {
                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                    ) {
                        if (heartRecords.isNotEmpty()) {
                            Text(
                                text = "Heart Rate Records",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            heartRecords.forEach { record ->
                                val data = record.samples.first()

                                val formattedDate = OffsetDateTime.parse(data.time.toString())
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                Text(
                                    text = "$formattedDate ==> ${formatLastModifiedTime(data.time.toString())} ==> ${data.beatsPerMinute} bpm",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Text(
                                text = "No heart raterecords available.",
                                color = Color.White,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface {
                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                    ) {
                        if (bloodPressureRecord.isNotEmpty()) {
                            Text(
                                text = "Blood Pressure Records",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            bloodPressureRecord.reversed().forEach { record ->
                                val formattedDate = OffsetDateTime.parse(record.time.toString())
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                Text(
                                    text = "$formattedDate ==> ${formatLastModifiedTime(record.time.toString())} ==> ${record.systolic.inMillimetersOfMercury.toInt()}/${record.diastolic.inMillimetersOfMercury.toInt()} mmHg",
                                    color = Color.White,
                                    fontSize = 14.sp
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

            Surface {
                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                    ) {
                        if (respiratoryRateRecord.isNotEmpty()) {
                            Text(
                                text = "Respiratory rate Records",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            respiratoryRateRecord.reversed().forEach { record ->
                                val formattedDate = OffsetDateTime.parse(record.time.toString())
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                Text(
                                    text = "$formattedDate ==> ${formatLastModifiedTime(record.time.toString())} ==> ${record.rate.toInt()} rpm",
                                    color = Color.White,
                                    fontSize = 14.sp
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

            Surface {
                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                    ) {
                        if (bodyTemperatureRecord.isNotEmpty()) {
                            Text(
                                text = "Body temperature Records",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            bodyTemperatureRecord.reversed().forEach { record ->
                                val formattedDate = OffsetDateTime.parse(record.time.toString())
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                Text(
                                    text = "$formattedDate ==> ${formatLastModifiedTime(record.time.toString())} ==> %.1f °F".format(record.temperature.inFahrenheit),
                                    color = Color.White,
                                    fontSize = 14.sp
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

            Surface {
                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                    ) {
                        if (oxygenRecords.isNotEmpty()) {
                            Text(
                                text = "Oxygen Records",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            oxygenRecords.reversed().forEach { record ->
                                val formattedDate = OffsetDateTime.parse(record.time.toString())
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                Text(
                                    text = "$formattedDate ==> ${formatLastModifiedTime(record.time.toString())} ==> ${record.percentage.value.toInt()}%",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
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
