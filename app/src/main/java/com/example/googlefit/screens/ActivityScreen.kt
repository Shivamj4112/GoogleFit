package com.example.googlefit.screens

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.R
import com.example.googlefit.utils.DateRange
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.activitiesType
import com.example.googlefit.utils.util
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.timeDiffInSeconds
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ActivityScreen(healthManager: HealthManager, navController: NavHostController) {

    val stepsRecords by healthManager.stepsRecords.observeAsState(emptyList())
    val distanceRecords by healthManager.distanceRecords.observeAsState(emptyList())
    val powerRecords by healthManager.powerRecord.observeAsState(emptyList())
    val speedRecords by healthManager.speedRecords.observeAsState(emptyList())
    val caloriesRecords by healthManager.caloriesRecords.observeAsState(emptyList())
    val cyclingRecords by healthManager.cyclingRecords.observeAsState(emptyList())
    val exerciseRecords by healthManager.exerciseSessionRecords.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()
    val range by healthManager.range.observeAsState()
    val timeIntervals by healthManager.timeIntervals.observeAsState(emptyList())

    LaunchedEffect(dateRange) {
        healthManager.fetchActivityData()
    }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp)
        ) {
            TopBar(navController = navController, title = "Activity")
            DateRange(healthManager)

            ActivityContent(
                range,
                timeIntervals,
                stepsRecords,
                powerRecords,
                exerciseRecords,
                caloriesRecords,
                distanceRecords,
                cyclingRecords,
                speedRecords
            )


        }
    }
}

@Composable
private fun ActivityContent(
    range: String?,
    timeIntervals: List<Pair<Instant, Instant>>,
    stepsRecords: List<StepsRecord>,
    powerRecord: List<PowerRecord>,
    exerciseRecords: List<ExerciseSessionRecord>,
    caloriesRecords: List<TotalCaloriesBurnedRecord>,
    distanceRecords: List<DistanceRecord>,
    cyclingRecords: List<CyclingPedalingCadenceRecord>,
    speedRecords: List<SpeedRecord>
) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var totalSteps by remember { mutableStateOf("") }
    var totalCalories by remember { mutableStateOf(0.0) }
    var totalDistance by remember { mutableStateOf(0.0) }
    var totalPower by remember { mutableStateOf(0.0) }
    var totalCycling by remember { mutableStateOf(0.0) }
    var totalSpeed by remember { mutableStateOf(0.0) }

    var showChart1 by remember { mutableStateOf(false) }
    var showChart2 by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }

    LaunchedEffect(range) {
        if (!isTransitioning) {
            isTransitioning = true
            showChart1 = false
            showChart2 = false

            delay(350)

            when (range) {
                "Day" -> {
                    showChart1 = true
                }

                "Week" -> {
                    showChart2 = true
                }
            }
            isTransitioning = false
        }
    }

    if (isTransitioning) {
        CircularProgressIndicator(
            modifier = Modifier
        )
    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            when (range) {


                "Day" -> {
                    val intervalLabels =
                        listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")

                    val intervalData = timeIntervals.mapIndexed { index, interval ->

                        val totalSteps = stepsRecords
                            .filter { it.endTime >= interval.first && it.endTime < interval.second }
                            .sumOf { it.count }
                            .toDouble()


                        val totalCalories = caloriesRecords
                            .filter { it.endTime >= interval.first && it.endTime < interval.second }
                            .sumOf { it.energy.inKilocalories }

                        Bars(
                            label = intervalLabels[index],
                            values = listOf(

                                Bars.Data(
                                    label = "Steps",
                                    value = totalSteps,
                                    color = SolidColor(Color.Green),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 10.dp,
                                            topLeft = 10.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Calories",
                                    value = totalCalories,
                                    color = SolidColor(Color.Magenta),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        ),
                                    ),
                                )

                            )
                        )
                    }

                    val intervalData2 = timeIntervals.mapIndexed { index, interval ->
                        val totalDistance = distanceRecords
                            .filter { it.endTime >= interval.first && it.endTime < interval.second }
                            .sumOf { it.distance.inKilometers }

                        val totalPower = powerRecord
                            .filter { it.endTime >= interval.first && it.endTime < interval.second }
                            .sumOf { it.samples.first().power.inWatts }


                        val totalSpeed = speedRecords
                            .filter { it.endTime >= interval.first && it.endTime < interval.second }
                            .sumOf { it.samples.first().speed.inKilometersPerHour }

                        val totalCycling = cyclingRecords
                            .filter { it.endTime >= interval.first && it.endTime < interval.second }
                            .sumOf { it.samples.first().revolutionsPerMinute }

                        Bars(
                            label = intervalLabels[index],
                            values = listOf(
                                Bars.Data(
                                    label = "Distance",
                                    value = totalDistance,
                                    color = SolidColor(Color.Cyan),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Speed",
                                    value = totalSpeed,
                                    color = SolidColor(Color.Red),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Cycling",
                                    value = totalCycling,
                                    color = SolidColor(Color.Black),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Power",
                                    value = totalPower,
                                    color = SolidColor(Color.Gray),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                )
                            )
                        )
                    }

                    if (showChart1) {
                        ColumnChart(
                            modifier = Modifier
                                .height(250.sdp)
                                .padding(horizontal = 10.dp),
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
                        Spacer(modifier = Modifier.height(30.sdp))

                        ColumnChart(
                            modifier = Modifier
                                .height(250.sdp)
                                .padding(horizontal = 10.dp),
                            data = intervalData2,
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

                    Text(
                        text = "Exercise Records",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                    )
                    ExerciseRecordsList(exerciseRecords)

                    showDetailsDialog = false
                }

                "Week" -> {
                    val stepsGroupData = stepsRecords.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf { it.count }
                    }

                    val caloriesGroupedData = caloriesRecords.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf { it.energy.inKilocalories }
                    }

                    val distanceGroupedData = distanceRecords.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf { it.distance.inKilometers }
                    }

                    val powerGroupedData = powerRecord.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf { it.samples.first().power.inWatts }
                    }

                    val cyclingGroupedData = cyclingRecords.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf { it.samples.first().revolutionsPerMinute }
                    }

                    val speedGroupedData = speedRecords.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf { it.samples.first().speed.inKilometersPerHour }
                    }

                    val today = LocalDate.now()

                    val past7Days = (6 downTo 0).map {
                        val date = today.minusDays(it.toLong())
                        date to date.format(DateTimeFormatter.ofPattern(" dd\nMMM"))
                    }

                    val chartData = past7Days.map { (date, label) ->
                        val totalSteps = stepsGroupData[date.toString()]?.toDouble() ?: 0.0
                        val totalCalories = caloriesGroupedData[date.toString()] ?: 0.0

                        Bars(
                            label = label,
                            values = listOf(
                                Bars.Data(
                                    label = "Steps",
                                    value = totalSteps,
                                    color = SolidColor(Color.Green),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Calories",
                                    value = totalCalories,
                                    color = SolidColor(Color.Magenta),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),


                                )
                        )

                    }
                    val chartData2 = past7Days.map { (date, label) ->
                        val totalDistance = distanceGroupedData[date.toString()] ?: 0.0
                        val totalPower = powerGroupedData[date.toString()] ?: 0.0
                        val totalSpeed = speedGroupedData[date.toString()] ?: 0.0
                        val totalCycling = cyclingGroupedData[date.toString()] ?: 0.0

                        Bars(
                            label = label,
                            values = listOf(
                                Bars.Data(
                                    label = "Distance",
                                    value = totalDistance,
                                    color = SolidColor(Color.Cyan),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Speed",
                                    value = totalSpeed,
                                    color = SolidColor(Color.Red),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Cycling",
                                    value = totalCycling,
                                    color = SolidColor(Color.DarkGray),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                ),
                                Bars.Data(
                                    label = "Power",
                                    value = totalPower,
                                    color = SolidColor(Color.Gray),
                                    properties = BarProperties(
                                        thickness = 8.dp,
                                        spacing = 0.dp,
                                        cornerRadius = Bars.Data.Radius.Rectangle(
                                            topRight = 2.dp,
                                            topLeft = 2.dp
                                        )
                                    )
                                )
                            )
                        )
                    }

                    if (showChart2) {
                        ColumnChart(
                            modifier = Modifier
                                .height(250.sdp)
                                .padding(horizontal = 10.dp),
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
                        Spacer(modifier = Modifier.height(30.sdp))
                        ColumnChart(
                            modifier = Modifier
                                .height(250.sdp)
                                .padding(horizontal = 10.dp),
                            data = chartData2,
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

                    Text(
                        text = "Exercise Records",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                    )
                    ExerciseRecordsList(exerciseRecords)
                    showDetailsDialog = false
                }

                "Month" -> {
                    CustomCalendar(
                        modifier = Modifier,
                        stepsRecords = stepsRecords,
                        powerRecord = powerRecord,
                        exerciseRecords = exerciseRecords,
                        caloriesRecords = caloriesRecords,
                        distanceRecords = distanceRecords,
                        powerRecords = powerRecord,
                        speedRecords = speedRecords,
                        cyclingRecords = cyclingRecords,
                    ) { steps, calories, distance, powerRecords, cycling, speed ->
                        showDetailsDialog = true
                        totalSteps = steps.toString()
                        totalCalories = calories
                        totalDistance = distance
                        totalPower = powerRecords
                        totalCycling = cycling
                        totalSpeed = speed
                    }


                }
            }
        }
    }


    if (showDetailsDialog) {

        Dialog(
            properties = DialogProperties(),
            onDismissRequest = { showDetailsDialog = false },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.sdp))
                    .background(Color.White)
            ) {
                Text(
                    text = "Activity Records",
                    fontSize = 18.ssp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 15.sdp)
                )

                val list = listOf(
                    "Steps" to totalSteps,
                    "Calories" to totalCalories,
                    "Distance" to totalDistance,
                    "Power" to totalPower,
                    "Cycling" to totalCycling,
                    "Speed" to totalSpeed,
                )

                Spacer(modifier = Modifier.height(10.sdp))

                list.forEach { (label, value) ->
                    Spacer(modifier = Modifier.height(10.sdp))

                    var newValue = when (label) {
                        "Calories" -> "%.2f Cal".format(value)
                        "Distance" -> "%.2f km".format(value)
                        "Power" -> "%.2f W".format(value)
                        "Cycling" -> "%.2f km".format(value)
                        "Speed" -> "%.2f km/h".format(value)
                        else -> "$value steps"

                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(text = label)
                        Text(text = newValue)
                    }
                }


                Button(
                    modifier = Modifier.padding(vertical = 8.sdp),
                    onClick = {
                        showDetailsDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}


@Composable
fun CustomCalendar(
    modifier: Modifier = Modifier,
    stepsRecords: List<StepsRecord>,
    powerRecord: List<PowerRecord>,
    exerciseRecords: List<ExerciseSessionRecord>,
    caloriesRecords: List<TotalCaloriesBurnedRecord>,
    distanceRecords: List<DistanceRecord>,
    powerRecords: List<PowerRecord>,
    speedRecords: List<SpeedRecord>,
    cyclingRecords: List<CyclingPedalingCadenceRecord>,
    onDateClick: (Double, Double, Double,Double, Double, Double) -> Unit
) {
    val daysInMonth = LocalDate.now().lengthOfMonth()
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val stepsGroupedData = stepsRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.count }
    }

    val caloriesGroupedData = caloriesRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.energy.inKilocalories }
    }

    val distanceGroupedData = distanceRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.distance.inKilometers }
    }

    val powerGroupedData = powerRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.samples.first().power.inWatts }
    }

    val cyclingGroupedData = cyclingRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.samples.first().revolutionsPerMinute }
    }

    val speedGroupedData = speedRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.samples.first().speed.inKilometersPerHour }
    }

    val totalStepsForMonth =
        stepsGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()

    val totalCaloriesForMonth =
        caloriesGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()

    val totalDistanceForMonth =
        distanceGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()

    val totalPowerForMonth =
        powerGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()

    val totalSpeedForMonth =
        speedGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()

    Column(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                    text = "Total Steps: $totalStepsForMonth steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total Calories: %.0f Cal".format(totalCaloriesForMonth),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total Distance: %.2f km".format(totalDistanceForMonth),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total Power: %.2f km".format(totalPowerForMonth),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total Speed: %.0f km/h".format(totalSpeedForMonth),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val isCurrentMonth = currentMonth.isBefore(LocalDate.now().withDayOfMonth(1))
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
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayName ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
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

        (0 until ((daysInMonth + firstDayOfWeek - 1) / 7) + 1).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (0..6).forEach { day ->
                    val dayOfMonth = week * 7 + day - firstDayOfWeek + 1
                    val date = if (dayOfMonth in 1..daysInMonth) {
                        currentMonth.withDayOfMonth(dayOfMonth)
                    } else null

                    val totalSteps = stepsGroupedData[date?.toString()]?.toDouble() ?: 0.0
                    val totalCalories = caloriesGroupedData[date?.toString()] ?: 0.0
                    val totalDistance = distanceGroupedData[date?.toString()] ?: 0.0
                    val totalPower = powerGroupedData[date?.toString()] ?: 0.0
                    val totalCycling = cyclingGroupedData[date?.toString()] ?: 0.0
                    val totalSpeed = speedGroupedData[date?.toString()] ?: 0.0

                    val isFutureDate = date?.isAfter(LocalDate.now()) ?: true
                    val alphaValue = if (isFutureDate) 0.3f else 1f

                    if (date != null) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .let {
                                    if (!isFutureDate) {
                                        it.clickable {
                                            onDateClick(
                                                totalSteps,
                                                totalCalories,
                                                totalDistance,
                                                totalPower,
                                                totalCycling,
                                                totalSpeed
                                            )
                                        }
                                    } else {
                                        it
                                    }
                                }
                                .alpha(alphaValue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString(), fontSize = 12.sp)
                            if (totalSteps > 0.0 || totalCalories > 0.0 || totalSpeed > 0.0 || totalDistance > 0.0 || totalPower > 0.0 || totalCycling > 0.0) {
                                Canvas(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .align(Alignment.BottomCenter),
                                    onDraw = {
                                        drawCircle(color = Color.Green)
                                    })
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Exercise Records",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )
        ExerciseRecordsList(exerciseRecords)
    }
}


@Composable
fun ExerciseRecordsList(exerciseRecords: List<ExerciseSessionRecord>) {
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = exerciseRecords) {
        if (exerciseRecords.isNotEmpty()) {
            listState.scrollToItem(exerciseRecords.size - 1)
        }
    }

    if (exerciseRecords.isNotEmpty()) {
        LazyRow(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.sdp)
        ) {
            items(exerciseRecords) { exerciseRecord ->
                ExerciseRecordItem(exerciseRecord)
            }
        }
    } else {
        Text(
            text = "No Exercises records available",
            color = Color.White,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun ExerciseRecordItem(exerciseRecord: ExerciseSessionRecord) {

    var title = if (exerciseRecord.title.isNullOrEmpty()) {
        ""
    } else {
        "${exerciseRecord.title}, "
    }

    var type = when(exerciseRecord.exerciseType) {
        0 -> "Workout"
        2 -> "Badminton"
        4 -> "Baseball"
        5 -> "Basketball"
        8 -> "Biking"
        9 -> "Biking stationary"
        10 -> "Boot Camp"
        11 -> "Boxing"
        13 -> "Calisthenics"
        14 -> "Cricket"
        16 -> "Dancing"
        25 -> "Elliptical"
        26 -> "Exercise class"
        27 -> "Fencing"
        28 -> "American football"
        29 -> "Australian football"
        31 -> "Frisbee"
        32 -> "Golf"
        34 -> "Gymnastics"
        35 -> "Handball"
        36 -> "High intensity interval training"
        37 -> "Hiking"
        38 -> "Ice hockey"
        39 -> "Ice Skating"
        44 -> "Martial Arts"
        46 -> "Paddling"
        47 -> "Paragliding"
        48 -> "Pilates"
        50 -> "Racquetball"
        51 -> "Rock Climbing"
        53 -> "Rowing"
        54 -> "Rowing Machine"
        55 -> "Rugby"
        56 -> "Running"
        57 -> "Treadmill running"
        58 -> "Sailing"
        59 -> "Scuba diving"
        60 -> "Skating"
        61 -> "Skiing"
        62 -> "Snowboarding"
        63 -> "Snowshoeing"
        64 -> "Soccer"
        65 -> "Softball"
        66 -> "Squash"
        68 -> "Stair climbing"
        69 -> "Stair climbing machine"
        70 -> "Strength training"
        72 -> "Surfing"
        73 -> "Open water swimming"
        74 -> "Pool Swimming"
        75 -> "Table tennis"
        76 -> "Tennis"
        78 -> "Volleyball"
        79 -> "Walking"
        80 -> "Water Polo"
        81 -> "Weightlifting"
        82 -> "Wheelchair"
        83 -> "Yoga"
        else -> "Unknown"

    }

    Column(
        modifier = Modifier
            .width(250.sdp)
            .padding(end = 10.sdp)
            .background(Color.Black, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = type,
                color = Color.White,
                fontSize = 14.ssp,
                fontWeight = FontWeight.Bold
            )

            Log.d(
                "ExerciseRecordItem",
                "ExerciseRecordItem: ${exerciseRecord.exerciseType}  ==> $type"
            )
        }
        Text(
            color = Color.White,
            text = "${formateDate(exerciseRecord.startTime.toString())} at ${
                formatLastModifiedTime(exerciseRecord.startTime.toString())
            } - ${formatLastModifiedTime(exerciseRecord.endTime.toString())}",
            fontSize = 8.ssp,
        )
        Row {
            Text(
                text = util.formatDuration(
                    timeDiffInSeconds(
                        exerciseRecord.startTime.toString(),
                        exerciseRecord.endTime.toString()
                    )
                ),
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(5.dp))
        }

        HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(top = 5.sdp))
    }
}

