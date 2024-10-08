package com.example.googlefit.screens.sleepscreens

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.R
import com.example.googlefit.navigation.Route.SLEEP_DETAILS_SCREEN
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formatDuration
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.getWeekday
import com.example.googlefit.utils.util.timeDiffInSeconds
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepScreen(healthManager: HealthManager, navController: NavHostController) {


    val sleepRecords by healthManager.sleepSessionRecords.observeAsState(emptyList())
    val range by healthManager.range.observeAsState()
    val timeIntervals by healthManager.timeIntervals.observeAsState(emptyList())

    var selectedRange by remember { mutableStateOf(range) }
    var showChart by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        healthManager.fetchSleepData()
        Log.d("SleepScreen", "SleepScreen: sleepRecords.size: ${sleepRecords.size}")
    }

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp)
        ) {
            TopBar(navController, "Sleep")
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Week", "Month").forEach { range ->
                    Button(
                        onClick = {
                            selectedRange = range
                            healthManager.setDateRange(range)
                            healthManager.setRange(range)
                        },
                        enabled = selectedRange != range,
                        colors = ButtonDefaults.buttonColors(
                            disabledContentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            containerColor = Color.LightGray.copy(alpha = 0.3f),
                            contentColor = Color.Black

                        )
                    ) {
                        Text(text = range)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            SleepDataContent(
                range = range,
                sleepRecords = sleepRecords,
                isTransitioning,
                showChart,
                onTransitionChanged = { isTransitioning = it },
                onChartChanged = { showChart = it }
            )

            if (showChart) {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .background(Color.Black, RoundedCornerShape(15.dp))
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            if (sleepRecords.isNotEmpty()) {
                                Text(
                                    text = "Sleep Records",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                sleepRecords.reversed().forEach { record ->
                                    val formattedDuration = formatDuration(
                                        timeDiffInSeconds(
                                            record.startTime.toString(),
                                            record.endTime.toString()
                                        )
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White, RoundedCornerShape(15.dp))
                                            .padding(horizontal = 15.dp, vertical = 10.dp)
                                            .align(Alignment.Start)
                                            .clickable(
                                                interactionSource = MutableInteractionSource(),
                                                indication = null
                                            ) {
                                                navController.navigate("$SLEEP_DETAILS_SCREEN/${record.startTime}/${record.endTime}")
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
                                            text = formattedDuration,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            } else {
                                Text(
                                    text = "No sleep records available.",
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

@Composable
private fun SleepDataContent(
    range: String?,
    sleepRecords: List<SleepSessionRecord>,
    isTransitioning: Boolean,
    showChart: Boolean,
    onTransitionChanged: (Boolean) -> Unit,
    onChartChanged: (Boolean) -> Unit
) {

    LaunchedEffect(range) {
        if (!isTransitioning) {
            onTransitionChanged(true)
            onChartChanged(false)

            delay(300)

            when (range) {
                "Week" -> {
                    onChartChanged(true)
                }
            }
            onTransitionChanged(false)
        }
    }

    if (isTransitioning) {
        CircularProgressIndicator(
            modifier = Modifier
        )
    } else {
        when (range) {

            "Week" -> {
                if (showChart) {
                    val groupedData = sleepRecords.groupBy {
                        OffsetDateTime.parse(it.endTime.toString())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }.mapValues { entry ->
                        entry.value.sumOf {
                            timeDiffInSeconds(
                                it.startTime.toString(),
                                it.endTime.toString()
                            )
                        }
                    }

                    val today = LocalDate.now()
                    val past7Days = (6 downTo 0).map {
                        val date = today.minusDays(it.toLong())
                        date to date.format(DateTimeFormatter.ofPattern(" dd\nMMM"))
                    }

                    val chartData = past7Days.map { (date, label) ->
                        val totalSleep = groupedData[date.toString()]?.toDouble() ?: 0.0
                        Bars(
                            label = label,
                            values = listOf(
                                Bars.Data(
                                    label = "Sleep",
                                    value = "%.5f ".format(totalSleep / 3600).toDouble(),
                                    color = SolidColor(Color.Green),
                                    properties = BarProperties(
                                        thickness = 20.dp,
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
                    Spacer(modifier = Modifier.height(24.dp))

                }
            }

            "Month" -> {
                CustomSleepCalendar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    sleepRecords = sleepRecords
                )
            }
        }
    }
}


@Composable
fun CustomSleepCalendar(
    modifier: Modifier = Modifier,
    sleepRecords: List<SleepSessionRecord>,
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val groupedData = sleepRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { timeDiffInSeconds(it.startTime.toString(), it.endTime.toString()) }
    }

    val totalSleepForMonth = groupedData.filterKeys {
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
                    text = "Total Sleep: ${formatDuration(totalSleepForMonth)}",
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

        (0 until 6).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                (1..7).forEach { day ->

                    val date =
                        firstDayOfMonth.plusDays((week * 7 + day - firstDayOfMonth.dayOfWeek.value).toLong())
                    val alphaValue = if (date.isAfter(LocalDate.now())) 0.3f else 1f
                    val isFutureDate = date.isAfter(LocalDate.now())

                    if (date.month == currentMonth.month) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .alpha(alphaValue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString(), fontSize = 12.sp)
                            val totalSleep = groupedData[date.toString()]?.toDouble() ?: 0.0
                            if (totalSleep > 0.0) {
                                Canvas(modifier = Modifier
                                    .size(5.sdp)
                                    .align(Alignment.BottomCenter), onDraw = {
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
    }
}