package com.example.googlefit.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.googlefit.HealthManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.navigation.NavHostController
import com.example.googlefit.R
import com.example.googlefit.utils.util.formatLastModifiedTime
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NutritionScreen(healthManager: HealthManager, navController: NavHostController) {

    val hydrationRecords by healthManager.hydrationRecords.observeAsState(emptyList())
    val nutritionRecords by healthManager.nutritionRecords.observeAsState(emptyList())
    val range by healthManager.range.observeAsState()
    val timeIntervals by healthManager.timeIntervals.observeAsState(emptyList())

    LaunchedEffect(range) {
        healthManager.fetchNutritionData()
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

            HydrationDataContent(range = range , timeIntervals = timeIntervals , hydrationRecords = hydrationRecords)
            NutritionDataContent(range = range , timeIntervals = timeIntervals , nutritionRecords = nutritionRecords)


            Surface {
                Column(modifier = Modifier.fillMaxSize()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(15.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        if (nutritionRecords.isNotEmpty()) {
                            Text(
                                text = "Calories Consumed Records",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            nutritionRecords.forEach { record ->

                                val formattedDate = OffsetDateTime.parse(record.endTime.toString())
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(15.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                ){
                                    Text(
                                        text = "$formattedDate ==> ${formatLastModifiedTime(record.endTime.toString())} ==> ${record.energy?.inKilocalories?.toInt()} Cal",
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        } else {
                            Text(
                                text = "No calories burned records available.",
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
private fun HydrationDataContent(
    range: String?,
    timeIntervals: List<Pair<Instant, Instant>>,
    hydrationRecords: List<HydrationRecord>
) {
    when (range) {
        "Day" -> {
            val intervalLabels =
                listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")
            val intervalData = timeIntervals.mapIndexed { index, interval ->
                val totalSteps = hydrationRecords
                    .filter { it.endTime >= interval.first && it.endTime < interval.second }
                    .sumOf { it.volume.inMilliliters }
                Bars(
                    label = intervalLabels[index],
                    values = listOf(
                        Bars.Data(
                            label = "Hydration",
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
            val groupedData = hydrationRecords.groupBy {
                OffsetDateTime.parse(it.endTime.toString())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }.mapValues { entry ->
                entry.value.sumOf { it.volume.inMilliliters }
            }

            val today = LocalDate.now()
            val past7Days = (6 downTo 0).map {
                val date = today.minusDays(it.toLong())
                date to date.format(DateTimeFormatter.ofPattern(" dd\nMMM"))
            }

            val chartData = past7Days.map { (date, label) ->
                val totalHydration = groupedData[date.toString()] ?: 0.0
                Bars(
                    label = label,
                    values = listOf(
                        Bars.Data(
                            label = "Hydration",
                            value = totalHydration,
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
            HydrationCalendar(
                modifier = Modifier.padding(horizontal = 18.dp),
                hydrationRecords = hydrationRecords
            ) {}
        }
    }
}

@Composable
private fun NutritionDataContent(
    range: String?,
    timeIntervals: List<Pair<Instant, Instant>>,
    nutritionRecords: List<NutritionRecord>
) {
    when (range) {
        "Day" -> {
            val intervalLabels =
                listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")
            val intervalData = timeIntervals.mapIndexed { index, interval ->
                val totalNutrition = nutritionRecords
                    .filter { it.endTime >= interval.first && it.endTime < interval.second }
                    .sumOf { it.energy!!.inKilocalories }
                Bars(
                    label = intervalLabels[index],
                    values = listOf(
                        Bars.Data(
                            label = "Nutrition",
                            value = totalNutrition,
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
            val groupedData = nutritionRecords.groupBy {
                OffsetDateTime.parse(it.endTime.toString())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }.mapValues { entry ->
                entry.value.sumOf { it.energy!!.inKilocalories }
            }

            val today = LocalDate.now()
            val past7Days = (6 downTo 0).map {
                val date = today.minusDays(it.toLong())
                date to date.format(DateTimeFormatter.ofPattern(" dd\nMMM"))
            }

            val chartData = past7Days.map { (date, label) ->
                val totalNutrition = groupedData[date.toString()] ?: 0.0
                Bars(
                    label = label,
                    values = listOf(
                        Bars.Data(
                            label = "Nutrition",
                            value = totalNutrition,
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
            NutritionCalendar(
                modifier = Modifier.padding(horizontal = 18.dp),
                nutritionRecords = nutritionRecords
            ) {}
        }
    }
}

@Composable
fun NutritionCalendar(
    modifier: Modifier = Modifier,
    nutritionRecords: List<NutritionRecord>,
    onDateClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val groupedData = nutritionRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.energy!!.inKilocalories}
    }

    val totalNutritionForMonth = groupedData.filterKeys {
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
                    text = "Total Nutrition: ${totalNutritionForMonth.toInt()} Cal",
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
                                .size(50.dp)
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString() , fontSize = 12.sp)
                            val totalNutrition = groupedData[date.toString()] ?: 0.0
                            if (totalNutrition > 0.0) {
                                Text(
                                    text = "${totalNutrition.toInt()} Cal",
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
fun HydrationCalendar(
    modifier: Modifier = Modifier,
    hydrationRecords: List<HydrationRecord>,
    onDateClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val groupedData = hydrationRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.volume.inMilliliters }
    }

    val totalHydrationForMonth = groupedData.filterKeys {
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
                    text = "Total Hydration: ${totalHydrationForMonth.toInt()} mL",
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
                                .size(50.dp)
                                .clickable { onDateClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString() , fontSize = 12.sp)
                            val totalHydration = groupedData[date.toString()]?.toDouble() ?: 0.0
                            if (totalHydration > 0.0) {
                                Text(
                                    text = "${totalHydration.toInt()} mL",
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