package com.example.googlefit.screens

import android.util.Log
import com.example.googlefit.HealthManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.googlefit.navigation.Route
import com.example.googlefit.utils.util.fetchInternetTime
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs

@Composable
fun MainScreen(healthManager: HealthManager, navController: NavHostController) {

    var allPermissionsGranted by remember { mutableStateOf<Boolean?>(null) }
//    var showTimeWarning by remember { mutableStateOf<Boolean?>(null) }

//    LaunchedEffect(Unit) {
//        val internetTime = fetchInternetTime()
//
//        val deviceTime = Calendar.getInstance().timeInMillis
//
//        if (internetTime != null) {
//            val istCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
//            istCalendar.timeInMillis = internetTime
//
//            val deviceCalendar = Calendar.getInstance()
//            val dateMismatch = deviceCalendar.get(Calendar.YEAR) != istCalendar.get(Calendar.YEAR) ||
//                    deviceCalendar.get(Calendar.MONTH) != istCalendar.get(Calendar.MONTH) ||
//                    deviceCalendar.get(Calendar.DAY_OF_MONTH) != istCalendar.get(Calendar.DAY_OF_MONTH)
//
//            val timeMismatch = abs(deviceTime - internetTime) > ALLOWED_TIME_DIFFERENCE
//
//            if (dateMismatch || timeMismatch) {
//                showTimeWarning = false
//            }
//            else{
//                showTimeWarning = false
//            }
//            Log.d("TAg","Date Mismatch: $dateMismatch, Time Mismatch: $timeMismatch")
//        } else {
//            showTimeWarning = false
//        }
//
//    }

    LaunchedEffect(Unit) {
//        if (showTimeWarning == false) {
            delay(50)
            healthManager.setRange("Week")
            healthManager.setDateRange("Week")
//        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = healthManager.requestPermissionsActivityContract()
    ) { result: Set<String> ->
        allPermissionsGranted = result.containsAll(healthManager.permissions)
    }

    LaunchedEffect(Unit) {
        allPermissionsGranted = healthManager.hasAllPermissions(healthManager.permissions)
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
//            showTimeWarning.let {
//                if (it == null) {
//                    CircularProgressIndicator()
//                    Text(text = "Please wait...")
//                }
//                else if (showTimeWarning == true) {
//                    Text(
//                        text = "Please correct the time and date on your device",
//                        color = Color.Red,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//                else{
                    when (allPermissionsGranted) {
                        null -> {
//                    CircularProgressIndicator()
                        }
                        false -> {
                            // Show the "Request Permissions" UI
                            Text(text = "Request Health Permissions")

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                permissionLauncher.launch(healthManager.permissions)
                            }) {
                                Text(text = "Request Permissions")
                            }
                        }
                        true -> {
                            Button(onClick = {
                                navController.navigate(Route.ACTIVITY_SCREEN)
                            }) {
                                Text(text = "Activity")
                            }

                            Button(onClick = {
                                navController.navigate(Route.BODY_MEASUREMENTS_SCREEN)
                            }) {
                                Text(text = "Body Measurements")
                            }

                            Button(onClick = {
                                navController.navigate(Route.VITALS_SCREEN)
                            }) {
                                Text(text = "Vitals")
                            }

                            Button(onClick = {
                                navController.navigate(Route.NUTRITION_SCREEN)
                            }) {
                                Text(text = "Nutrition")
                            }

                            Button(onClick = {
                                navController.navigate(Route.SLEEP_SCREEN)
                            }) {
                                Text(text = "Sleep")
                            }

                            Button(onClick = {
                                navController.navigate(Route.CYCLE_SCREEN)
                            }) {
                                Text(text = "Cycle tracking")
                            }
                        }
                    }
//                }
//            }

        }
    }
}

@Preview
@Composable
fun MainPreview() {
    MainScreen(
        healthManager = HealthManager(LocalContext.current),
        navController = NavHostController(LocalContext.current)
    )
}

private const val ALLOWED_TIME_DIFFERENCE = 5 * 60 * 1000L // 5 minutes in milliseconds



