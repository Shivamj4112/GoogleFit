package com.example.googlefit.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.googlefit.HealthManager
import com.example.googlefit.modelFactory.MainViewModelFactory
import com.example.googlefit.navigation.Route.ACTIVITY_SCREEN
import com.example.googlefit.navigation.Route.BODY_MEASUREMENTS_SCREEN
import com.example.googlefit.navigation.Route.CYCLE_SCREEN
import com.example.googlefit.navigation.Route.NUTRITION_SCREEN
import com.example.googlefit.navigation.Route.SLEEP_SCREEN
import com.example.googlefit.navigation.Route.VITALS_SCREEN
import com.example.googlefit.viewModel.MainViewModel

@Composable
fun MainScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(healthManager, LocalContext.current))
) {
    val isSupportedVersion by mainViewModel.isSupportedVersion.collectAsState()
    val isHealthConnectInstalled by mainViewModel.isHealthConnectInstalled.collectAsState()
    val allPermissionsGranted by mainViewModel.allPermissionsGranted.collectAsState()
    val showPermissionDialog by mainViewModel.showPermissionDialog.collectAsState()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = healthManager.requestPermissionsActivityContract()
    ) { result ->
        mainViewModel.updatePermissions(result)
    }

    LaunchedEffect(Unit) {
        val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_ADDED).apply {
            addDataScheme("package")
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                mainViewModel.recheckHealthConnectInstallation()
            }
        }
        context.registerReceiver(receiver, intentFilter)
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { mainViewModel.resetPermissionDialog() },
            title = { Text("Permissions Required") },
            text = { Text("Please grant the necessary permissions in the app settings.") },
            confirmButton = {
                Button(onClick = {
                    mainViewModel.resetPermissionDialog()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            }
        )
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when {
                isSupportedVersion == null -> {
                    CircularProgressIndicator()
                    Text(text = "Checking device compatibility...")
                }
                isSupportedVersion == false -> {
                    Text(
                        text = "Your Android device doesn't support the Health Connect app.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                isHealthConnectInstalled == null -> {
                    CircularProgressIndicator()
                    Text(text = "Checking Health Connect installation...")
                }
                isHealthConnectInstalled == false -> {
                    Text(
                        text = "Health Connect app is not installed.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { mainViewModel.installHealthConnect() }) {
                        Text(text = "Install Health Connect")
                    }
                }
                allPermissionsGranted == false -> {
                    Text(text = "Request Health Permissions")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { mainViewModel.requestPermissions(permissionLauncher) }) {
                        Text(text = "Request Permissions")
                    }
                }
                allPermissionsGranted == true -> {

                    Button(onClick = { navController.navigate(ACTIVITY_SCREEN) }) {
                        Text(text = "Activity")
                    }
                    Button(onClick = { navController.navigate(BODY_MEASUREMENTS_SCREEN) }) {
                        Text(text = "Body Measurements")
                    }
                    Button(onClick = { navController.navigate(VITALS_SCREEN) }) {
                        Text(text = "Vitals")
                    }
                    Button(onClick = { navController.navigate(NUTRITION_SCREEN) }) {
                        Text(text = "Nutrition")
                    }
                    Button(onClick = { navController.navigate(SLEEP_SCREEN) }) {
                        Text(text = "Sleep")
                    }
                    Button(onClick = { navController.navigate(CYCLE_SCREEN) }) {
                        Text(text = "Cycle tracking")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun MainPreview() {
    MainScreen(
        healthManager = HealthManager(LocalContext.current),
        navController = rememberNavController()
    )
}