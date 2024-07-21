package com.example.googlefit

import HealthManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.googlefit.ui.theme.GoogleFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleFitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val healthManager = HealthManager(context)
    var allPermissionsGranted by remember { mutableStateOf(false) }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = healthManager.requestPermissionsActivityContract()
    ) { result: Set<String> ->
        // Handle the result
        val allPermissionsGranted = result.containsAll(healthManager.permissions)
        if (allPermissionsGranted) {
            // Do something when all permissions are granted
        } else {
            // Do something when permissions are not granted
        }
    }

    LaunchedEffect(Unit) {
        // Check if all permissions are already granted
        allPermissionsGranted = healthManager.hasAllPermissions(healthManager.permissions)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (!allPermissionsGranted) {
            Text(text = "Request Health Permissions")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Trigger the permission request
                permissionLauncher.launch(healthManager.permissions)
            }) {
                Text(text = "Request Permissions")
            }
        } else {
            // Do something when all permissions are granted
            Text(
                text = "All permissions granted",
                textAlign = TextAlign.Center,
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoogleFitTheme {
        Greeting("Android")
    }
}