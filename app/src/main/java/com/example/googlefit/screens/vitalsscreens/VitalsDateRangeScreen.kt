package com.example.googlefit.screens.vitalsscreens

import android.util.Log
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
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.R
import com.example.googlefit.data.BodyMeasurementType.BODY_FAT
import com.example.googlefit.data.BodyMeasurementType.HEIGHT
import com.example.googlefit.data.BodyMeasurementType.METABOLIC_RATE
import com.example.googlefit.data.BodyMeasurementType.WEIGHT
import com.example.googlefit.data.MeasurementItem
import com.example.googlefit.data.VitalType.BLOOD_PRESSURE
import com.example.googlefit.data.VitalType.BODY_TEMPERATURE
import com.example.googlefit.data.VitalType.HEART_RATE
import com.example.googlefit.data.VitalType.OXYGEN_SATURATION
import com.example.googlefit.data.VitalType.RESPIRATORY_RATE
import com.example.googlefit.navigation.Route.VITALS_DETAILS_SCREEN
import com.example.googlefit.utils.DateRange
import com.example.googlefit.utils.TopBar
import com.example.googlefit.utils.util.formateDate
import com.example.googlefit.utils.util.getWeekday
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VitalsDateRangeScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    vitals: String
) {
    val heartRecords by healthManager.heartRateRecords.observeAsState(emptyList())
    val bloodPressureRecord by healthManager.bloodPressureRecords.observeAsState(emptyList())
    val respiratoryRateRecord by healthManager.respiratoryRateRecords.observeAsState(emptyList())
    val oxygenRecords by healthManager.oxygenSaturationRecords.observeAsState(emptyList())
    val bodyTemperatureRecord by healthManager.bodyTemperatureRecords.observeAsState(emptyList())
    val dateRange by healthManager.dateRange.observeAsState()

    var totalHeartRate by remember { mutableStateOf(0) }
    var totalSystolic by remember { mutableStateOf(0) }
    var totalDiastolic by remember { mutableStateOf(0) }
    var totalRespiratory by remember { mutableStateOf(0) }
    var totalOxygen by remember { mutableStateOf(0) }
    var totalTemp by remember { mutableStateOf(0) }
    var showDataDialog by remember { mutableStateOf(false) }

    LaunchedEffect(dateRange) {
        healthManager.fetchVitalsData()
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
        ) {
            val formattedVitals =
                vitals.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() }
            TopBar(navController, formattedVitals)

            Spacer(modifier = Modifier.height(10.sdp))
            DateRange(healthManager)
            Spacer(modifier = Modifier.height(10.sdp))

            if (healthManager.range.value != "Month") {
                when (vitals) {
                    "$HEART_RATE" -> {
                        if (heartRecords.isNotEmpty()) {
                            val displayedDates = mutableSetOf<String>()

                            heartRecords.reversed().forEach { record ->
                                val data = record.samples.first()
                                val formattedDate = formateDate(data.time.toString())


                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val bpmValues =
                                        heartRecords.flatMap { record -> record.samples.map { it.beatsPerMinute } }
                                    val minBpm = bpmValues.minOrNull() ?: 0
                                    val maxBpm = bpmValues.maxOrNull() ?: 0

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$VITALS_DETAILS_SCREEN/$HEART_RATE/$formattedDate")
                                                Log.d(
                                                    "VitalsDateRangeScreen",
                                                    "VitalsDateRangeScreen: $formattedDate"
                                                )
                                            }
                                            .padding(horizontal = 10.sdp, vertical = 10.sdp)
                                    ) {
                                        Row {
                                            Text(
                                                text = "${getWeekday(data.time.toString())},",
                                                fontSize = 10.ssp
                                            )
                                            Text(text = formattedDate, fontSize = 10.ssp)
                                        }
                                        Text(
                                            text = "$minBpm-$maxBpm bpm",
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                }
                            }
                        } else {
                            Text(text = "No data found", color = Color.Gray)
                        }
                    }

                    "$BLOOD_PRESSURE" -> {
                        if (bloodPressureRecord.isNotEmpty()) {
                            val displayedDates = mutableSetOf<String>()

                            bloodPressureRecord.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val dateWiseBloodPressureRecords =
                                        bloodPressureRecord.groupBy { record ->
                                            formateDate(record.time.toString())
                                        }
                                    val targetDateRecords =
                                        dateWiseBloodPressureRecords[formattedDate] ?: emptyList()

                                    val minSystolic =
                                        targetDateRecords.minOf { it.systolic.inMillimetersOfMercury.toInt() }
                                    val maxSystolic =
                                        targetDateRecords.maxOf { it.systolic.inMillimetersOfMercury.toInt() }
                                    val minDiastolic =
                                        targetDateRecords.minOf { it.diastolic.inMillimetersOfMercury.toInt() }
                                    val maxDiastolic =
                                        targetDateRecords.maxOf { it.diastolic.inMillimetersOfMercury.toInt() }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$VITALS_DETAILS_SCREEN/$BLOOD_PRESSURE/$formattedDate")
                                                Log.d(
                                                    "VitalsDateRangeScreen",
                                                    "VitalsDateRangeScreen: $formattedDate"
                                                )
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
                                        val systolicText = if (minSystolic == maxSystolic) {
                                            "$minSystolic"
                                        } else {
                                            "$minSystolic-$maxSystolic"
                                        }
                                        val diastolicText = if (minDiastolic == maxDiastolic) {
                                            "$minDiastolic"
                                        } else {
                                            "$minDiastolic-$maxDiastolic"
                                        }
                                        Text(
                                            text = "$systolicText mmHg / $diastolicText mmHg",
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

                    "$RESPIRATORY_RATE" -> {
                        if (respiratoryRateRecord.isNotEmpty()) {
                            val displayedDates = mutableSetOf<String>()

                            respiratoryRateRecord.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val dateWiseRespiratoryRateRecords =
                                        respiratoryRateRecord.groupBy { record ->
                                            formateDate(record.time.toString())
                                        }
                                    val targetDateRecords =
                                        dateWiseRespiratoryRateRecords[formattedDate] ?: emptyList()

                                    val minRpm = targetDateRecords.minOf { it.rate.toInt() }
                                    val maxRpm = targetDateRecords.maxOf { it.rate.toInt() }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$VITALS_DETAILS_SCREEN/${RESPIRATORY_RATE}/$formattedDate")
                                                Log.d(
                                                    "VitalsDateRangeScreen",
                                                    "VitalsDateRangeScreen: $formattedDate"
                                                )
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

                                        val rpmText = if (minRpm == maxRpm) {
                                            "$minRpm"
                                        } else {
                                            "$minRpm-$maxRpm"
                                        }
                                        Text(
                                            text = "$rpmText rpm",
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

                    "$BODY_TEMPERATURE" -> {
                        if (bodyTemperatureRecord.isNotEmpty()) {
                            val displayedDates = mutableSetOf<String>()

                            bodyTemperatureRecord.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val dateWisebodyTemperatureRecords =
                                        bodyTemperatureRecord.groupBy { record ->
                                            formateDate(record.time.toString())
                                        }
                                    val targetDateRecords =
                                        dateWisebodyTemperatureRecords[formattedDate] ?: emptyList()

                                    val minTemperature =
                                        targetDateRecords.minOf { it.temperature.inFahrenheit.toInt() }
                                    val maxTemperature =
                                        targetDateRecords.maxOf { it.temperature.inFahrenheit.toInt() }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$VITALS_DETAILS_SCREEN/${BODY_TEMPERATURE}/$formattedDate")
                                                Log.d(
                                                    "VitalsDateRangeScreen",
                                                    "VitalsDateRangeScreen: $formattedDate"
                                                )
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

                                        val temperatureText =
                                            if (minTemperature == maxTemperature) {
                                                "$minTemperature"
                                            } else {
                                                "$minTemperature-$maxTemperature"
                                            }
                                        Text(
                                            text = "$temperatureText °F",
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

                    "$OXYGEN_SATURATION" -> {
                        if (oxygenRecords.isNotEmpty()) {
                            val displayedDates = mutableSetOf<String>()

                            oxygenRecords.reversed().forEach { record ->
                                val formattedDate = formateDate(record.time.toString())

                                if (formattedDate !in displayedDates) {
                                    displayedDates.add(formattedDate)

                                    val dateWiseOxygenRecords = oxygenRecords.groupBy { record ->
                                        formateDate(record.time.toString())
                                    }
                                    val targetDateRecords =
                                        dateWiseOxygenRecords[formattedDate] ?: emptyList()

                                    val minOxygen =
                                        targetDateRecords.minOf { it.percentage.value.toInt() }
                                    val maxOxygen =
                                        targetDateRecords.maxOf { it.percentage.value.toInt() }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate("$VITALS_DETAILS_SCREEN/${OXYGEN_SATURATION}/$formattedDate")
                                                Log.d(
                                                    "VitalsDateRangeScreen",
                                                    "VitalsDateRangeScreen: $formattedDate"
                                                )
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

                                        val oxygenText = if (minOxygen == maxOxygen) {
                                            "$minOxygen"
                                        } else {
                                            "$minOxygen-$maxOxygen"
                                        }
                                        Text(
                                            text = "$oxygenText%",
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
            }
            else{
                VitalsCalendar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.sdp, vertical = 10.sdp),
                    heartRateRecords = heartRecords,
                    bloodPressureRecords = bloodPressureRecord,
                    respiratoryRecords = respiratoryRateRecord,
                    oxygenSaturationRecords = oxygenRecords,
                    bodyTemperatureRecords = bodyTemperatureRecord,
                    type = vitals,
                    onDateClick = { heartRate,systolic,diastolic,respiratory, oxygen, temp ->

                        totalHeartRate = heartRate
                        totalSystolic = systolic
                        totalDiastolic = diastolic
                        totalRespiratory = respiratory
                        totalOxygen = oxygen
                        totalTemp = temp
                        showDataDialog = true
                    }

                )
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

                        val list = when (vitals) {
                            "$HEART_RATE" -> MeasurementItem("Heart Rate ", totalHeartRate.toString())
                            "$BLOOD_PRESSURE" -> {
                                MeasurementItem("Systolic ", totalSystolic.toString())
                                MeasurementItem("Diastolic ", totalDiastolic.toString())
                            }
                            "$RESPIRATORY_RATE" -> MeasurementItem("Respiratory Rate ", totalRespiratory.toString())
                            "$BODY_TEMPERATURE" -> MeasurementItem("Body Temperature ", totalTemp.toString())
                            "$OXYGEN_SATURATION" -> MeasurementItem("Oxygen", totalOxygen.toString())
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
fun VitalsCalendar(
    modifier: Modifier = Modifier,
    heartRateRecords: List<HeartRateRecord>,
    bloodPressureRecords: List<BloodPressureRecord>,
    respiratoryRecords: List<RespiratoryRateRecord>,
    oxygenSaturationRecords: List<OxygenSaturationRecord>,
    bodyTemperatureRecords: List<BodyTemperatureRecord>,
    type: String,
    onDateClick: (Int, Int, Int, Int , Int , Int) -> Unit,
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val heartRateGroupedData = heartRateRecords.groupBy {
        OffsetDateTime.parse(it.endTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.samples.first().beatsPerMinute }
    }

    val bloodPressureSystolicGroupedData = bloodPressureRecords.groupBy {
        OffsetDateTime.parse(it.time.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.systolic.inMillimetersOfMercury }
    }

    val bloodPressureDiastolicGroupedData = bloodPressureRecords.groupBy {
        OffsetDateTime.parse(it.time.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.diastolic.inMillimetersOfMercury }
    }

    val respiratoryGroupedData = respiratoryRecords.groupBy {
        OffsetDateTime.parse(it.time.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.rate }
    }

    val oxygenSaturationGroupedData = oxygenSaturationRecords.groupBy {
        OffsetDateTime.parse(it.time.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.percentage.value.toInt() }
    }

    val bodyTemperatureGroupedData = bodyTemperatureRecords.groupBy {
        OffsetDateTime.parse(it.metadata.lastModifiedTime.toString())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.mapValues { entry ->
        entry.value.sumOf { it.temperature.inFahrenheit }
    }


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

                    if (date.month == currentMonth.month) {

                        val totalHeartRate = heartRateGroupedData[date.toString()] ?: 0.0
                        val totalSystolic = bloodPressureSystolicGroupedData[date.toString()] ?: 0.0
                        val totalDiastolic = bloodPressureDiastolicGroupedData[date.toString()] ?: 0.0
                        val totalRespiratory = respiratoryGroupedData[date.toString()] ?: 0.0
                        val totalBodyTemperature = bodyTemperatureGroupedData[date.toString()] ?: 0.0
                        val totalOxygen = oxygenSaturationGroupedData[date.toString()] ?: 0.0

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onDateClick(
                                        totalHeartRate.toInt(),
                                        totalSystolic.toInt(),
                                        totalDiastolic.toInt(),
                                        totalRespiratory.toInt(),
                                        totalBodyTemperature.toInt(),
                                        totalOxygen.toInt()
                                    )
                                }
                                .alpha(alphaValue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = date.dayOfMonth.toString(), fontSize = 12.sp)

                            when (type) {

                                "$HEART_RATE" -> {
                                    if (totalHeartRate.toDouble() > 0.0) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(5.sdp)
                                                .align(Alignment.BottomCenter), onDraw = {
                                                drawCircle(color = Color.Green)
                                            })
                                    }
                                }

                                "$BLOOD_PRESSURE" -> {
                                    if (totalSystolic > 0.0) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(5.sdp)
                                                .align(Alignment.BottomCenter), onDraw = {
                                                drawCircle(color = Color.Green)
                                            })
                                    }
                                }

                                "$RESPIRATORY_RATE" -> {
                                    if (totalRespiratory > 0.0) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(5.sdp)
                                                .align(Alignment.BottomCenter), onDraw = {
                                                drawCircle(color = Color.Green)
                                            })
                                    }
                                }

                                "$BODY_TEMPERATURE" -> {
                                    if (totalBodyTemperature > 0.0) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(5.sdp)
                                                .align(Alignment.BottomCenter), onDraw = {
                                                drawCircle(color = Color.Green)
                                            })
                                    }
                                }
                                "$OXYGEN_SATURATION" -> {
                                    if (totalOxygen.toDouble() > 0.0) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(5.sdp)
                                                .align(Alignment.BottomCenter), onDraw = {
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