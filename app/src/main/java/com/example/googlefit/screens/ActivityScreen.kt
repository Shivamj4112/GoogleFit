package com.example.googlefit.screens

import android.util.Log
import com.example.googlefit.HealthManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.navigation.NavHostController
import com.example.googlefit.utils.util.formatLastModifiedTime
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

@Composable
fun ActivityScreen(healthManager: HealthManager, navController: NavHostController) {
    val stepsRecords by healthManager.stepsRecords.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()
    val timeIntervals by healthManager.timeIntervals.observeAsState(emptyList())

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val exerciseRecords by produceState<List<ExerciseSessionRecord>>(initialValue = emptyList()) {
                value = healthManager.readExerciseSessions(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val distanceRecords by produceState<List<DistanceRecord>>(initialValue = emptyList()) {
                value = healthManager.readDistancesInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val speedRecords by produceState<List<SpeedRecord>>(initialValue = emptyList()) {
                value = healthManager.readSpeedInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            val caloriesRecords by produceState<List<TotalCaloriesBurnedRecord>>(initialValue = emptyList()) {
                value = healthManager.readTotalCaloriesBurnedInputs(
                    start = Instant.now().minus(30, ChronoUnit.DAYS),
                    end = Instant.now()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Day", "Week", "Month").forEach { range ->
                    Button(onClick = { healthManager.setDateRange(range) }) {
                        Text(text = range)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (timeIntervals.isEmpty()) {
                // Weekly data
                val groupedData = stepsRecords.groupBy {
                    OffsetDateTime.parse(it.metadata.lastModifiedTime.toString()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                }.mapValues { entry ->
                    entry.value.sumOf { it.count }
                }

                val dayMapping = mapOf(
                    "Mon" to DayOfWeek.MONDAY,
                    "Tue" to DayOfWeek.TUESDAY,
                    "Wed" to DayOfWeek.WEDNESDAY,
                    "Thu" to DayOfWeek.THURSDAY,
                    "Fri" to DayOfWeek.FRIDAY,
                    "Sat" to DayOfWeek.SATURDAY,
                    "Sun" to DayOfWeek.SUNDAY
                )

                val chartData = dayMapping.keys.map { day ->
                    val date = LocalDate.now().with(TemporalAdjusters.previousOrSame(dayMapping[day]!!))
                    val totalSteps = groupedData[date.toString()]?.toDouble() ?: 0.0
                    Bars(
                        label = day,
                        values = listOf(
                            Bars.Data(
                                label = "Steps",
                                value = totalSteps,
                                color = SolidColor(Color.Green),
                                properties = BarProperties(
                                    thickness = 10.dp,
                                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 4.dp, topLeft = 3.dp)
                                )
                            )
                        )
                    )
                }

                ColumnChart(
                    modifier = Modifier
                        .height(250.dp)
                        .padding(horizontal = 22.dp),
                    data = chartData,
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(
                            topRight = 6.dp,
                            topLeft = 6.dp
                        ),
                        spacing = 3.dp,
                        thickness = 20.dp,
                    ),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            } else {
                // Daily data
                val intervalLabels = listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")
                val intervalData = timeIntervals.mapIndexed { index, interval ->
                    val totalSteps = stepsRecords
                        .filter { it.endTime >= interval.first && it.endTime < interval.second }
                        .sumOf { it.count }
                        .toDouble()
                    Bars(
                        label = intervalLabels[index],
                        values = listOf(
                            Bars.Data(
                                label = "Steps",
                                value = totalSteps,
                                color = SolidColor(Color.Green),
                                properties = BarProperties(
                                    thickness = 10.dp,
                                    cornerRadius = Bars.Data.Radius.Rectangle(topRight = 4.dp, topLeft = 3.dp)
                                )
                            )
                        )
                    )
                }

                ColumnChart(
                    modifier = Modifier
                        .height(250.dp)
                        .padding(horizontal = 22.dp),
                    data = intervalData,
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(
                            topRight = 6.dp,
                            topLeft = 6.dp
                        ),
                        spacing = 3.dp,
                        thickness = 20.dp,
                    ),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            DisplayRecords(
                title = "Steps Records",
                records = stepsRecords.reversed(),
                noDataMessage = "No steps records available."
            ) { record ->
                val formattedDate = OffsetDateTime.parse(record.endTime.toString()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val formattedTime = formatLastModifiedTime(record.endTime.toString(),is24HourFormat = false)
                "${record.count} Steps   => $formattedDate ->  $formattedTime"
            }

            LaunchedEffect(dateRange) {
                healthManager.fetchStepsData()
            }

            Spacer(modifier = Modifier.height(24.dp))

            DisplayRecords(
                title = "Exercise Records",
                records = exerciseRecords,
                noDataMessage = "No exercise records available."
            ) { record ->
                "Exercise: ${record.exerciseType}"
            }

            Spacer(modifier = Modifier.height(24.dp))

            DisplayRecords(
                title = "Distance Records",
                records = distanceRecords,
                noDataMessage = "No distance records available."
            ) { record ->
                "Distance: %.3f km".format(record.distance.inKilometers)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DisplayRecords(
                title = "Speed Records",
                records = speedRecords,
                noDataMessage = "No speed records available."
            ) { record ->
                "Speed: %.1f km/h".format(record.samples.first().speed.inKilometersPerHour)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DisplayRecords(
                title = "Calories Records",
                records = caloriesRecords,
                noDataMessage = "No calories records available."
            ) { record ->
                "Calories: ${record.energy.inKilocalories.roundToInt()} kcal"
            }
        }
    }
}

@Composable
fun <T> DisplayRecords(
    title: String,
    records: List<T>,
    noDataMessage: String,
    recordContent: @Composable (T) -> String
) {
    if (records.isNotEmpty()) {
        Text(
            text = "$title:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        records.forEach { record ->
            Text(text = recordContent(record))
        }
    } else {
        Text(text = noDataMessage)
    }
}


