package com.example.googlefit.screens.sleepscreens

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.googlefit.R
import com.example.googlefit.utils.DebounceClick
import com.example.googlefit.utils.util.formatDuration
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.timeDiffInSeconds
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun SleepDetailsScreen(navController: NavHostController, startTime: String, endTime: String) {

    val duration = formatDuration(timeDiffInSeconds(startTime, endTime))

    Surface {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 10.sdp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                DebounceClick(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back"
                    )
                }


                Text(text = "Sleep Details", fontWeight = FontWeight.SemiBold, fontSize = 14.ssp)

                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more option")
            }

            val formattedStartTime = formatLastModifiedTime(startTime)
            val formattedEndTime = formatLastModifiedTime(endTime)

            ClockView(formattedStartTime, formattedEndTime, duration)

            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 3.sdp)
            ) {

                Text(text = "Duration", fontSize = 12.ssp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = null,
                        modifier = Modifier.size(24.sdp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 10.sdp)
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(text = "Asleep", fontSize = 10.ssp, fontWeight = FontWeight.Bold)
                        Text(text = "People usually sleep 7-9 hours a day", fontSize = 9.ssp)
                    }

                    Text(text = duration, fontSize = 10.ssp)


                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 3.sdp, top = 10.sdp)
            ) {

                Text(text = "Schedule", fontSize = 12.ssp)

                val list = listOf("Got in bed", "Woke up")
                var time = listOf(formatLastModifiedTime(startTime), formatLastModifiedTime(endTime))
                repeat(2) {
                    if (time.isNotEmpty()) {
                        ScheduleCardContent(list[it], time[it])
                    } else {
                        ScheduleCardContent("list[it]", "time[it])")
                    }
                }

            }

        }
    }

}

@Composable
fun ClockView(startTime: String, endTime: String, duration: String, modifier: Modifier = Modifier) {

    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val start = dateFormat.parse(startTime)!!
    val end = dateFormat.parse(endTime)!!
    val totalMinutes = 12 * 60
    val startMinutes = start.hours % 12 * 60 + start.minutes
    val endMinutes = end.hours % 12 * 60 + end.minutes

    val isStartAM = startTime.endsWith("AM")
    val isEndAM = endTime.endsWith("AM")

    val progress =
        if (startMinutes == endMinutes || (isStartAM && !isEndAM && endMinutes >= startMinutes)) {
            1f
        } else if (endMinutes >= startMinutes) {
            (endMinutes - startMinutes) / totalMinutes.toFloat()
        } else {
            (totalMinutes - startMinutes + endMinutes) / totalMinutes.toFloat()
        }

    val startAngle = (startMinutes / totalMinutes.toFloat()) * 360f - 90f

    Canvas(
        modifier = Modifier
            .padding(vertical = 20.sdp)
            .size(140.sdp)
    ) {
        val clockRadius = size.minDimension / 2
        val clockCenterX = size.width / 2
        val clockCenterY = size.height / 2

        drawArc(
            color = Color.Blue,
            startAngle = startAngle,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )

        for (i in 0 until 60) {
            val angle = i * 6f
            val lineLength = if (i % 5 == 0) 8.dp.toPx() else 5.dp.toPx()
            val lineColor = if (i % 5 == 0) Color.Black else Color.Gray
            val startRadius = clockRadius - 12.dp.toPx()
            val endRadius = startRadius - lineLength

            rotate(degrees = angle, pivot = Offset(clockCenterX, clockCenterY)) {
                drawLine(
                    color = lineColor,
                    start = Offset(clockCenterX, clockCenterY - startRadius),
                    end = Offset(clockCenterX, clockCenterY - endRadius),
                    strokeWidth = 1.dp.toPx()
                )

            }


            if (i % 5 == 0) {
                val hour = if (i / 5 == 0) 12 else i / 5
                val hourTextRadius = startRadius - 20.dp.toPx()
                val hourX =
                    clockCenterX + hourTextRadius * cos(Math.toRadians((angle - 90).toDouble())).toFloat()
                val hourY =
                    clockCenterY + hourTextRadius * sin(Math.toRadians((angle - 90).toDouble())).toFloat()

                drawContext.canvas.nativeCanvas.drawText(
                    hour.toString(),
                    hourX,
                    hourY + 8.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 14.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        drawContext.canvas.nativeCanvas.drawText(
            duration,
            clockCenterX,
            clockCenterY + 10.dp.toPx(),
            android.graphics.Paint().apply {
                color = android.graphics.Color.RED
                textSize = 24.sp.toPx()
                typeface = Typeface.DEFAULT_BOLD
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}

@Composable
fun ScheduleCardContent(title: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.size(24.sdp)
        )

        Text(
            text = title,
            fontSize = 10.ssp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 10.sdp)
                .fillMaxWidth()
                .weight(1f)
        )

        Text(text = time, fontSize = 10.ssp)

    }
}

