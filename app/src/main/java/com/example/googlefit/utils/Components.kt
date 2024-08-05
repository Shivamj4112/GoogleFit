package com.example.googlefit.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val activitiesType = listOf(
    "Aerobics", "Australian football", "Backcountry skiing", "Badminton", "Baseball", "Basketball",
    "Beach volleyball", "Biathlon", "Biking", "Boxing", "Calisthenics", "Circuit training",
    "Cricket", "Cross skating", "Cross-country skiing", "Cross fit", "Curling", "Dancing",
    "Diving", "Downhill skiing", "Elliptical", "Ergometer", "Fencing", "Fitness walking",
    "Flossing", "Football", "Frisbee", "Gardening", "Golf", "Gymnastics", "Handball",
    "Handcycling", "High intensity interval training", "Hiking", "Hockey", "Horseback riding",
    "Ice skating","Indoor skating", "Indoor volleyball", "Inline skating", "Interval training", "Jogging",
    "Jumping rope", "Kayaking", "Kettlebell", "Kick scooter", "Kickboxing", "Kite skiing",
    "Kitesurfing", "Martial arts", "Meditating", "Mixed martial arts", "Mountain biking",
    "Nordic walking", "Open water swimming", "Other", "P90x", "Paced walking", "Paragliding",
    "Pilates", "Polo", "Pool swimming", "Racquetball", "Road biking", "Rock climbing",
    "Roller skiing", "Rowing", "Rowing machine", "Rugby", "Running", "Sailing", "Sand running",
    "Scuba diving", "Skateboarding", "Skating", "Skiing", "Sledding","Snowboarding", "Snowshoeing", "Soccer",
    "Softball", "Spinning", "Squash", "Stair climbing", "Stair climbing machine",
    "Stand-up paddle boarding", "Stationary biking", "Strength training", "Stroller walking",
    "Surfing", "Swimming", "Table tennis", "Tennis", "Treadmill running", "Treadmill walking",
    "Utility biking", "Volleyball", "Wakeboarding", "Walking", "Water polo", "Weightlifting",
    "Wheelchair", "Windsurfing", "Yoga", "Zumba"
)

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun DebounceClick(
    debounceDuration: Long = 2000L,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {

    var isEnabled by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier
        .clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            enabled = isEnabled,
            onClick = {
                if(isEnabled){
                    onClick()
                    isEnabled = false
                    scope.launch {
                        delay(debounceDuration)
                        isEnabled = true
                    }
                }
            }
        )
    ){
        content()
    }


}
@Composable
fun TopBar(navController: NavHostController,title : String) {
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

        Text(text = "$title Details", fontWeight = FontWeight.SemiBold, fontSize = 14.ssp)

        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more option" , modifier = Modifier.alpha(0f))
    }
}

@Composable
fun DateRange(healthManager: HealthManager) {

    Spacer(modifier = Modifier.height(5.dp))

    val selectedRange = healthManager.range.value.toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Day", "Week", "Month").forEach { range ->
            Button(
                onClick = {
                    if (selectedRange != range) {
                        healthManager.setDateRange(range)
                        healthManager.setRange(range)
                    }
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
}


