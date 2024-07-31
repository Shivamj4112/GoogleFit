package com.example.googlefit.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.models.Bars

data class BarProperties(
    val thickness: Dp = 20.dp,
    val spacing: Dp = 3.dp,
    val barColor: Color = Color.Green,
    val cornerRadius: Bars.Data.Radius.Rectangle = Bars.Data.Radius.Rectangle(6.dp, 6.dp)
)