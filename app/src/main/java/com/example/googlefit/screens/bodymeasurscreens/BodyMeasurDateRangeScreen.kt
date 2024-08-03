package com.example.googlefit.screens.bodymeasurscreens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.R
import com.example.googlefit.data.BodyMeasurementType.BODY_FAT
import com.example.googlefit.data.BodyMeasurementType.HEIGHT
import com.example.googlefit.data.BodyMeasurementType.METABOLIC_RATE
import com.example.googlefit.data.BodyMeasurementType.WEIGHT
import com.example.googlefit.data.MeasurementItem
import com.example.googlefit.navigation.Route.BODY_MEASUREMENT_DETAILS_SCREEN
import com.example.googlefit.utils.DateRange
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.getWeekday
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BodyMeasurDateRangeScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    bodyMeasurments: String
) {
    val weightRecords = healthManager.weightRecords.observeAsState(emptyList())
    val heightRecords = healthManager.heightRecords.observeAsState(emptyList())
    val bodyFatRecords = healthManager.bodyFatRecords.observeAsState(emptyList())
    val metabolicRateRecords = healthManager.basalMetabolicRate.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()

    var totalWeight by remember { mutableStateOf(0.0) }
    var totalHeight by remember { mutableStateOf(0.0) }
    var totalBodyFat by remember { mutableStateOf(0.0) }
    var totalMetabolic by remember { mutableStateOf(0) }

    var showDataDialog by remember { mutableStateOf(false) }
    var dialogReady by remember { mutableStateOf(false) }

    LaunchedEffect(dateRange) {
        healthManager.fetchBodyMeasurementsData()
    }
    LaunchedEffect(Unit) {
        healthManager.setDateRange("Week")
        dialogReady = false
    }

    LaunchedEffect(healthManager.range.value) {
        dialogReady = healthManager.range.value == "Month"
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
        ) {
            // navigating back
            val formattedBodyMeasurements = bodyMeasurments.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercaseChar() }
            TopBar(navController, formattedBodyMeasurements)

            Spacer(modifier = Modifier.height(10.sdp))
            DateRange(healthManager)

            if (healthManager.range.value != "Month") {
                when (bodyMeasurments) {
                    "$WEIGHT" -> {
                        if (weightRecords.value.isNotEmpty()) {
                            val displayedDates = mutableSetOf<String>()
                            val dateWiseWeightRecords = weightRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }

                            weightRecords.value.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val recordsForDate =
                                        dateWiseWeightRecords[formattedDate] ?: emptyList()
                                    val maxWeight =
                                        recordsForDate.maxByOrNull { it.weight.inKilograms }?.weight?.inKilograms
                                            ?: 0.0
                                    val minWeight =
                                        recordsForDate.minByOrNull { it.weight.inKilograms }?.weight?.inKilograms
                                            ?: 0.0



                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$WEIGHT/$formattedDate")
                                            }
                                            .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                    ) {
                                        Row {
                                            Text(
                                                text = "${getWeekday(record.time.toString())},",
                                                fontSize = 10.ssp
                                            )
                                            Text(text = formattedDate, fontSize = 10.ssp)
                                        }
                                        val weightText = if (minWeight == maxWeight) {
                                            "$minWeight kg"
                                        } else {
                                            "$minWeight - $maxWeight kg"
                                        }
                                        Text(
                                            text = weightText,
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(text = "No data found", color = Color.Gray)
                        }
                    }

                    "$HEIGHT" -> {
                        if (heightRecords.value.isNotEmpty()) {

                            val displayedDates = mutableSetOf<String>()
                            val dateWiseHeightRecords = heightRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }

                            heightRecords.value.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val recordsForDate =
                                        dateWiseHeightRecords[formattedDate] ?: emptyList()
                                    val maxHeight =
                                        recordsForDate.maxByOrNull { it.height.inFeet }?.height?.inFeet
                                            ?: 0.0
                                    val minHeight =
                                        recordsForDate.minByOrNull { it.height.inFeet }?.height?.inFeet
                                            ?: 0.0



                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$HEIGHT/$formattedDate")
                                            }
                                            .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                    ) {
                                        Row {
                                            Text(
                                                text = "${getWeekday(record.time.toString())},",
                                                fontSize = 10.ssp
                                            )
                                            Text(text = formattedDate, fontSize = 10.ssp)
                                        }
                                        val heightText = if (minHeight == maxHeight) {
                                            "%.1f ft".format(minHeight)
                                        } else {
                                            "%.1f - %.1f ft".format(minHeight, maxHeight)
                                        }
                                        Text(
                                            text = heightText,
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(text = "No data found", color = Color.Gray)
                        }
                    }

                    "$BODY_FAT" -> {
                        if (bodyFatRecords.value.isNotEmpty()) {

                            val displayedDates = mutableSetOf<String>()
                            val dateWiseBodyFatRecords = bodyFatRecords.value.groupBy { record ->
                                formateDate(record.time.toString())
                            }

                            bodyFatRecords.value.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val recordsForDate =
                                        dateWiseBodyFatRecords[formattedDate] ?: emptyList()

                                    val minBodyFat =
                                        recordsForDate.minByOrNull { it.percentage }?.percentage
                                            ?: 0.0
                                    val maxBodyFat =
                                        recordsForDate.maxByOrNull { it.percentage }?.percentage
                                            ?: 0.0



                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$BODY_FAT/$formattedDate")
                                            }
                                            .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                    ) {
                                        Row {
                                            Text(
                                                text = "${getWeekday(record.time.toString())},",
                                                fontSize = 10.ssp
                                            )
                                            Text(text = formattedDate, fontSize = 10.ssp)
                                        }

                                        val bodyFatText = if (minBodyFat == maxBodyFat) {
                                            "$minBodyFat"
                                        } else {
                                            "$minBodyFat - $maxBodyFat"
                                        }

                                        Text(
                                            text = bodyFatText,
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(text = "No data found", color = Color.Gray)
                        }
                    }

                    "$METABOLIC_RATE" -> {
                        if (metabolicRateRecords.value.isNotEmpty()) {

                            val displayedDates = mutableSetOf<String>()
                            val dateWiseMetaBolicRateRecords =
                                metabolicRateRecords.value.groupBy { record ->
                                    formateDate(record.time.toString())
                                }

                            metabolicRateRecords.value.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val recordsForDate =
                                        dateWiseMetaBolicRateRecords[formattedDate] ?: emptyList()

                                    val minMetabolicRate =
                                        recordsForDate.minByOrNull { it.basalMetabolicRate.inKilocaloriesPerDay.toInt() }?.basalMetabolicRate?.inKilocaloriesPerDay?.toInt()
                                            ?: 0
                                    val maxMetabolicRate =
                                        recordsForDate.maxByOrNull { it.basalMetabolicRate.inKilocaloriesPerDay.toInt() }?.basalMetabolicRate?.inKilocaloriesPerDay?.toInt()
                                            ?: 0

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$BODY_MEASUREMENT_DETAILS_SCREEN/$METABOLIC_RATE/$formattedDate")
                                            }
                                            .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                    ) {
                                        Row {
                                            Text(
                                                text = "${getWeekday(record.time.toString())},",
                                                fontSize = 10.ssp
                                            )
                                            Text(text = formattedDate, fontSize = 10.ssp)
                                        }

                                        val metaBolicRateText =
                                            if (minMetabolicRate == maxMetabolicRate) {
                                                "$minMetabolicRate Cal"
                                            } else {
                                                "$minMetabolicRate - $maxMetabolicRate Cal"
                                            }

                                        Text(
                                            text = metaBolicRateText,
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(text = "No data found", color = Color.Gray)
                        }
                    }
                }
            } else {
                BodyMeasurementCalendar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.sdp, vertical = 10.sdp),
                    weightRecords = weightRecords.value,
                    heightRecords = heightRecords.value,
                    bodyFatRecords = bodyFatRecords.value,
                    metabolicRateRecords = metabolicRateRecords.value,
                    type = bodyMeasurments,
                    onDateClick = { weight, height, bodyFat, metabolicRate ->

                        totalWeight = weight
                        totalHeight = height
                        totalBodyFat = bodyFat
                        totalMetabolic = metabolicRate

                        showDataDialog = true
                    }
                )
            }

            if (!dialogReady) {
                showDataDialog = false
            }

            if (showDataDialog) {

                Dialog(
                    properties = DialogProperties(),
                    onDismissRequest = { showDataDialog = false },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.sdp))
                            .background(Color.White)
                    ) {
                        Text(
                            text = "Body Measurement Records",
                            fontSize = 18.ssp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 15.sdp)
                                .padding(horizontal = 15.sdp)
                        )

                        val list = when (bodyMeasurments) {
                            "$WEIGHT" -> MeasurementItem("Weight", "%.1f kg".format(totalWeight))
                            "$HEIGHT" -> MeasurementItem("Height", "%.1f ft".format(totalHeight))
                            "$BODY_FAT" -> MeasurementItem("Body Fat", "%.1f %%".format(totalBodyFat))
                            "$METABOLIC_RATE" -> MeasurementItem("Metabolic Rate", "$totalMetabolic Cal")
                            else -> null
                        }

                        Spacer(modifier = Modifier.height(10.sdp))

                        list?.let { item ->
                            Spacer(modifier = Modifier.height(10.sdp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Text(text = item.label)
                                Text(text = item.value)
                            }
                        }



                        Button(
                            modifier = Modifier.padding(vertical = 8.sdp),
                            onClick = {
                                showDataDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun BodyMeasurementCalendar(
    modifier: Modifier = Modifier,
    weightRecords: List<WeightRecord>,
    heightRecords: List<HeightRecord>,
    bodyFatRecords: List<BodyFatRecord>,
    metabolicRateRecords: List<BasalMetabolicRateRecord>,
    type : String,
    onDateClick: (Double, Double, Double, Int) -> Unit,
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val weightGroupedData = weightRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.weight.inKilograms }
    }

    val heightGroupedData = heightRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.height.inKilometers }
    }

    val bodyFatGroupedData = bodyFatRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.percentage.value }
    }

    val metabolicRateGroupedData = metabolicRateRecords.groupBy {
        OffsetDateTime.parse(it.time.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.basalMetabolicRate.inKilocaloriesPerDay }
    }

    val totalWeightForMonth =
        weightGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()
    val totalHeightForMonth =
        heightGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()
    val totalBodyFatForMonth =
        bodyFatGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()
    val totalMetabolicRateForMonth =
        metabolicRateGroupedData.filterKeys { LocalDate.parse(it).month == currentMonth.month }.values.sum()

    Column(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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
                when(type){
                    "$WEIGHT" -> {
                        Text(
                            text = "Total Weight: %.1f kg".format(totalWeightForMonth),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    "$HEIGHT" -> {
                        Text(
                            text = "Total Height: %.1f ft".format(totalHeightForMonth),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    "$BODY_FAT" -> {
                        Text(
                            text = "Total Body Fat: %.1f ".format(totalBodyFatForMonth),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    "$METABOLIC_RATE" -> {
                        Text(
                            text = "Total Metabolic Rate: ${totalMetabolicRateForMonth.toInt()} Cal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }


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
                        .padding(top = 10.sdp),
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

                        val totalWeight = weightGroupedData[date.toString()] ?: 0.0
                        val totalHeight = heightGroupedData[date.toString()] ?: 0.0
                        val totalBodyFat = bodyFatGroupedData[date.toString()] ?: 0.0
                        val totalMetabolicRate = metabolicRateGroupedData[date.toString()] ?: 0.0

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .let {
                                    if (!isFutureDate) {
                                        it.clickable {
                                            onDateClick(
                                                totalWeight,
                                                totalHeight,
                                                totalBodyFat,
                                                totalMetabolicRate.toInt()
                                            )
                                        }
                                    }
                                    else {
                                        it
                                    }
                                }
                                .alpha(alphaValue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString(), fontSize = 12.sp)

                            when (type){

                                "$WEIGHT" -> {
                                    if (totalWeight > 0.0) {
                                        Canvas(modifier = Modifier.size(5.sdp).align(Alignment.BottomCenter), onDraw = {
                                            drawCircle(color = Color.Green)
                                        })
                                    }
                                }
                                "$HEIGHT" -> {
                                    if (totalHeight > 0.0) {
                                        Canvas(modifier = Modifier.size(5.sdp).align(Alignment.BottomCenter), onDraw = {
                                            drawCircle(color = Color.Green)
                                        })
                                    }
                                }
                                "$BODY_FAT" -> {
                                    if (totalBodyFat > 0.0) {
                                        Canvas(modifier = Modifier.size(5.sdp).align(Alignment.BottomCenter), onDraw = {
                                            drawCircle(color = Color.Green)
                                        })
                                    }
                                }
                                "$METABOLIC_RATE" -> {
                                    if (totalMetabolicRate > 0.0) {
                                        Canvas(modifier = Modifier.size(5.sdp).align(Alignment.BottomCenter), onDraw = {
                                            drawCircle(color = Color.Green)
                                        })
                                    }
                                }


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