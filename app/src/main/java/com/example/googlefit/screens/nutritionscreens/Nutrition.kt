package com.example.googlefit.screens.nutritionscreens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.R
import com.example.googlefit.navigation.Route.NUTRITION_DETAILS_SCREEN
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.getWeekday
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
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


            NutritionDataContent(
                range = range,
                timeIntervals = timeIntervals,
                nutritionRecords = nutritionRecords,
                hydrationRecords = hydrationRecords,
            )

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

                            nutritionRecords.reversed().forEach { record ->

                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(15.dp))
                                        .padding(horizontal = 15.dp, vertical = 10.dp)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null
                                        ) {
                                            navController.navigate("$NUTRITION_DETAILS_SCREEN/${record.endTime}")
                                        }
                                ) {
                                    Text(
                                        text = "${getWeekday(record.endTime.toString())}, ${
                                            formateDate(
                                                record.endTime.toString()
                                            )
                                        }",
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = "${record.energy?.inKilocalories?.toInt()} Cal",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
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
private fun NutritionDataContent(
    range: String?,
    timeIntervals: List<Pair<Instant, Instant>>,
    nutritionRecords: List<NutritionRecord>,
    hydrationRecords: List<HydrationRecord>,
) {

    var totalNutrition by remember { mutableIntStateOf(0) }
    var totalHydration by remember { mutableIntStateOf(0) }
    var showNutritionDialog by remember { mutableStateOf(false) }

    when (range) {

        "Day" -> {
            showNutritionDialog = false
            val intervalLabels = listOf("4 - 8", "8 - 12", "12 - 16", "16 - 20", "20 - 24", "0 - 4")
            val intervalData = timeIntervals.mapIndexed { index, interval ->
                val totalNutrition =
                    nutritionRecords.filter { it.endTime >= interval.first && it.endTime < interval.second }
                        .sumOf { it.energy!!.inKilocalories }
                val totalHydration =
                    hydrationRecords.filter { it.endTime >= interval.first && it.endTime < interval.second }
                        .sumOf { it.volume.inMilliliters }

                Bars(
                    label = intervalLabels[index],
                    values = listOf(
                        Bars.Data(
                            label = "Nutrition",
                            value = totalNutrition,
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
                            label = "Hydration",
                            value = totalHydration,
                            color = SolidColor(Color.Red),
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
            ColumnChart(
                modifier = Modifier
                    .height(250.sdp)
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
                animationSpec = tween(500)
            )
            Spacer(modifier = Modifier.height(24.dp))

        }

        "Week" -> {
            showNutritionDialog = false

            val nutritionGroupedData = nutritionRecords.groupBy {
                OffsetDateTime.parse(it.endTime.toString())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }.mapValues { entry ->
                entry.value.sumOf { it.energy!!.inKilocalories }
            }

            val hydrationGroupedData = hydrationRecords.groupBy {
                OffsetDateTime.parse(it.endTime.toString())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }.mapValues { entry ->
                entry.value.sumOf { it.volume.inMilliliters }
            }

            val today = LocalDate.now()
            val past7Days = (6 downTo 0).map {
                val date = today.minusDays(it.toLong())
                date to date.format(DateTimeFormatter.ofPattern("dd\nMMM"))
            }

            val chartData = past7Days.map { (date, label) ->
                val totalNutrition = nutritionGroupedData[date.toString()] ?: 0.0
                val totalHydration = hydrationGroupedData[date.toString()] ?: 0.0

                Bars(
                    label = label,
                    values = listOf(
                        Bars.Data(
                            label = "Nutrition",
                            value = totalNutrition,
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
                            label = "Hydration",
                            value = totalHydration,
                            color = SolidColor(Color.Red),
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

            ColumnChart(
                modifier = Modifier
                    .height(250.sdp)
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
                animationSpec = tween(500)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        "Month" -> {
            NutritionCalendar(
                modifier = Modifier.padding(horizontal = 18.dp),
                nutritionRecords = nutritionRecords,
                hydrationRecords = hydrationRecords,
            ) { nutrition, hydration ->
                showNutritionDialog = true
                totalNutrition = nutrition
                totalHydration = hydration
            }
        }
    }

    if (showNutritionDialog) {

        Dialog(
            properties = DialogProperties(),
            onDismissRequest = { showNutritionDialog = false },
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
                    "Nutrition" to "$totalNutrition Cal",
                    "Hydration" to "$totalHydration mL",
                )

                Spacer(modifier = Modifier.height(10.sdp))

                list.forEach { (label, value) ->
                    Spacer(modifier = Modifier.height(10.sdp))


                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(text = label)
                        Text(text = value)
                    }
                }


                Button(
                    modifier = Modifier.padding(vertical = 8.sdp),
                    onClick = {
                        showNutritionDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    }

}

@Composable
fun NutritionCalendar(
    modifier: Modifier = Modifier,
    nutritionRecords: List<NutritionRecord>,
    hydrationRecords: List<HydrationRecord>,
    onDateClick: (Int, Int) -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val nutritionGroupedData = nutritionRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.energy!!.inKilocalories }
    }

    val hydrationGroupedData = hydrationRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.volume.inMilliliters }
    }

    val totalNutritionForMonth = nutritionGroupedData.filterKeys {
        LocalDate.parse(it).month == currentMonth.month
    }.values.sum()
    val totalHydrationForMonth = hydrationGroupedData.filterKeys {
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

                        val totalNutrition = nutritionGroupedData[date.toString()] ?: 0.0
                        val totalHydration = hydrationGroupedData[date.toString()] ?: 0.0
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onDateClick(
                                        totalNutrition.toInt(),
                                        totalHydration.toInt()
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString(), fontSize = 12.sp)
                            if (totalNutrition > 0.0) {
                                Text(
                                    text = "${totalNutrition.toInt()} Cal",
                                    modifier = Modifier
                                        .padding(top = 22.dp)
                                        .align(Alignment.BottomCenter),
                                    fontSize = 8.sp,
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
