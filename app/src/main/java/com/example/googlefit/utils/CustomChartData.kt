package com.example.googlefit.utils

import androidx.compose.ui.graphics.Color

data class CustomChartData<T>(
    val label: String,
    val value: T,
    val color: Color = Color.Green
)