package com.example.googlefit.screens

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.R
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun ActivityScreen(healthManager: HealthManager, navController: NavHostController) {

    val stepsRecords by healthManager.stepsRecords.observeAsState(emptyList())
    val distanceRecords by healthManager.distanceRecords.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()
    val range by healthManager.range.observeAsState()
    val timeIntervals by healthManager.timeIntervals.observeAsState(emptyList())

    LaunchedEffect(dateRange) {
        healthManager.fetchAllData()
    }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
//            val exerciseRecords by produceState<List<ExerciseSessionRecord>>(initialValue = emptyList()) {
//                value = healthManager.readExerciseSessions(
//                    start = Instant.now().minus(30, ChronoUnit.DAYS),
//                    end = Instant.now()
//                )
//            }

//            val distanceRecords by produceState<List<DistanceRecord>>(initialValue = emptyList()) {
//                value = healthManager.readDistancesInputs(
//                    start = Instant.now().minus(30, ChronoUnit.DAYS),
//                    end = Instant.now()
//                )
//            }

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
                    Button(onClick = {
                        healthManager.setDateRange(range)
                        healthManager.setRange(range)
                    }) {
                        Text(text = range)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            StepsDataContent(range, timeIntervals, stepsRecords)
//            DisplayRecords(
//                title = "Steps Records",
//                records = stepsRecords.reversed(),
//                noDataMessage = "No steps records available."
//            ) { record ->
//                val formattedDate = OffsetDateTime.parse(record.endTime.toString())
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//                val formattedTime =
//                    formatLastModifiedTime(
//                        record.metadata.lastModifiedTime.toString(),
//                        is24HourFormat = false
//                    )
//                "${record.count} Steps   => $formattedDate ->  $formattedTime"
//            }
//            Spacer(modifier = Modifier.height(24.dp))

//            DisplayRecords(
//                title = "Exercise Records",
//                records = exerciseRecords,
//                noDataMessage = "No exercise records available."
//            ) { record ->
//                "Exercise: ${record.exerciseType}"
//            }

            DistanceDataContent(range = range, timeIntervals = timeIntervals, distanceRecords = distanceRecords)

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
private fun StepsDataContent(
    range: String?,
    timeIntervals: List<Pair<Instant, Instant>>,
    stepsRecords: List<StepsRecord>
) {
    when (range) {

        "Day" -> {
            val intervalLabels =
                listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")
            val intervalData = timeIntervals.mapIndexed { index, interval ->
                val totalSteps = stepsRecords
                    .filter { it.metadata.lastModifiedTime >= interval.first && it.metadata.lastModifiedTime < interval.second }
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
                                thickness = 15.dp,
                                spacing = 0.dp,
                                cornerRadius = Bars.Data.Radius.Rectangle(
                                    topRight = 10.dp,
                                    topLeft = 10.dp
                                )
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
            Spacer(modifier = Modifier.height(24.dp))
        }

        "Week" -> {
            val groupedData = stepsRecords.groupBy {
                OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }.mapValues { entry ->
                entry.value.sumOf { it.count }
            }

            val today = LocalDate.now()
            val past7Days = (6 downTo 0).map {
                val date = today.minusDays(it.toLong())
                date to date.format(DateTimeFormatter.ofPattern(" dd\nMMM"))
            }

            val chartData = past7Days.map { (date, label) ->
                val totalSteps = groupedData[date.toString()]?.toDouble() ?: 0.0
                Bars(
                    label = label,
                    values = listOf(
                        Bars.Data(
                            label = "Steps",
                            value = totalSteps,
                            color = SolidColor(Color.Green),
                            properties = BarProperties(
                                thickness = 15.dp,
                                spacing = 0.dp,
                                cornerRadius = Bars.Data.Radius.Rectangle(
                                    topRight = 4.dp,
                                    topLeft = 3.dp
                                )
                            )
                        )
                    )
                )
            }

            ColumnChart(
                modifier = Modifier
                    .height(250.dp)
                    .padding(horizontal = 20.dp),
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
            Spacer(modifier = Modifier.height(24.dp))
        }

        "Month" -> {
            CustomStepsCalendar(
                modifier = Modifier.padding(horizontal = 18.dp),
                stepsRecords = stepsRecords
            ) {}
        }
    }
}

@Composable
private fun DistanceDataContent(
    range: String?,
    timeIntervals: List<Pair<Instant, Instant>>,
    distanceRecords: List<DistanceRecord>
) {
    when (range) {

        "Day" -> {
            val intervalLabels =
                listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")
            val intervalData = timeIntervals.mapIndexed { index, interval ->
                val totalDistance = distanceRecords
                    .filter { it.metadata.lastModifiedTime >= interval.first && it.metadata.lastModifiedTime < interval.second }
                    .sumOf { it.distance.inKilometers }

                val formattedTotalDistance = String.format("%.3f", totalDistance)
                Log.d("TAG", "DistanceDataContent: $formattedTotalDistance")

                Bars(
                    label = intervalLabels[index],
                    values = listOf(
                        Bars.Data(
                            label = "Distance",
                            value = formattedTotalDistance.toDouble(),
                            color = SolidColor(Color.Green),
                            properties = BarProperties(
                                thickness = 15.dp,
                                spacing = 0.dp,
                                cornerRadius = Bars.Data.Radius.Rectangle(
                                    topRight = 10.dp,
                                    topLeft = 10.dp
                                )
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
            Spacer(modifier = Modifier.height(24.dp))
        }

        "Week" -> {
            val groupedData = distanceRecords.groupBy {
                OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }.mapValues { entry ->
                entry.value.sumOf { it.distance.inKilometers }
            }

            val today = LocalDate.now()
            val past7Days = (6 downTo 0).map {
                val date = today.minusDays(it.toLong())
                date to date.format(DateTimeFormatter.ofPattern(" dd\nMMM"))
            }

            val chartData = past7Days.map { (date, label) ->
                val totalSteps = groupedData[date.toString()] ?: 0.0
                Bars(
                    label = label,
                    values = listOf(
                        Bars.Data(
                            label = "Distance",
                            value = totalSteps,
                            color = SolidColor(Color.Green),
                            properties = BarProperties(
                                thickness = 15.dp,
                                spacing = 0.dp,
                                cornerRadius = Bars.Data.Radius.Rectangle(
                                    topRight = 4.dp,
                                    topLeft = 3.dp
                                )
                            )
                        )
                    )
                )
            }

            ColumnChart(
                modifier = Modifier
                    .height(250.dp)
                    .padding(horizontal = 20.dp),
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
            Spacer(modifier = Modifier.height(24.dp))
        }

        "Month" -> {
            CustomDistanceCalendar(
                modifier = Modifier.padding(horizontal = 18.dp),
                distanceRecords = distanceRecords
            ) {}
        }
    }
}

@Composable
fun CustomStepsCalendar(
    modifier: Modifier = Modifier,
    stepsRecords: List<StepsRecord>,
    onDateClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val groupedData = stepsRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.count }
    }

    val totalStepsForMonth = groupedData.filterKeys {
        LocalDate.parse(it).month == currentMonth.month
    }.values.sum()

    Column(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Total Steps: $totalStepsForMonth",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            var isCurrentMonth = currentMonth.isBefore(LocalDate.now().withDayOfMonth(1))
            IconButton(
                enabled = isCurrentMonth,
                onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Next Month",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        val firstDayOfMonth = currentMonth.withDayOfMonth(1)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayName ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        (0 until 6).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (1..7).forEach { day ->
                    val date =
                        firstDayOfMonth.plusDays((week * 7 + day - firstDayOfMonth.dayOfWeek.value).toLong())
                    if (date.month == currentMonth.month) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString() , fontSize = 12.sp)
                            val totalSteps = groupedData[date.toString()]?.toDouble() ?: 0.0
                            if (totalSteps > 0.0) {
                                Text(
                                    text = totalSteps.toString(),
                                    modifier = Modifier
                                        .padding(top = 22.dp)
                                        .align(Alignment.BottomCenter),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDistanceCalendar(
    modifier: Modifier = Modifier,
    distanceRecords: List<DistanceRecord>,
    onDateClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val groupedData = distanceRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.distance.inKilometers }
    }

    val totalStepsForMonth = groupedData.filterKeys {
        LocalDate.parse(it).month == currentMonth.month
    }.values.sum()

    Column(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Previous Month",
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Total Distance: %.3f".format(totalStepsForMonth),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            var isCurrentMonth = currentMonth.isBefore(LocalDate.now().withDayOfMonth(1))
            IconButton(
                enabled = isCurrentMonth,
                onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "Next Month",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        val firstDayOfMonth = currentMonth.withDayOfMonth(1)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayName ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        (0 until 6).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (1..7).forEach { day ->
                    val date =
                        firstDayOfMonth.plusDays((week * 7 + day - firstDayOfMonth.dayOfWeek.value).toLong())
                    if (date.month == currentMonth.month) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString() , fontSize = 12.sp)
                            val totalDistance = groupedData[date.toString()] ?: 0.0
                            if (totalDistance > 0.0) {
                                Text(
                                    text = "%.3f".format(totalDistance),
                                    modifier = Modifier
                                        .padding(top = 22.dp)
                                        .align(Alignment.BottomCenter),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
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


