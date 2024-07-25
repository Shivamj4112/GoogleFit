package com.example.googlefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.googlefit.navigation.Navigation
import com.example.googlefit.ui.theme.GoogleFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleFitTheme {

                Navigation()
            }
        }
    }
}

