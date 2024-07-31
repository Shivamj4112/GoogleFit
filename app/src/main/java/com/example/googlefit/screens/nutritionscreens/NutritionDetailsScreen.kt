package com.example.googlefit.screens.nutritionscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.NutritionRecord
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.utils.DebounceClick
import com.example.googlefit.utils.util.formatLastModifiedTime
import com.example.googlefit.utils.util.formateDate
import ir.ehsannarmani.compose_charts.PieChart
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun NutritionDetailsScreen(
    healthManager: HealthManager,
    navController: NavHostController,
    endTime: String
) {
    val nutritionRecords by healthManager.nutritionRecords.observeAsState(emptyList())

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TopBar(navController = navController)

            val record = nutritionRecords.find { it.endTime.toString() == endTime }
            record?.let { nutritionRecord ->
                NutritionDetails(nutritionRecord = nutritionRecord)
            }
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
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

        Text(
            text = "Nutrition Details",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.ssp
        )

        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more option")
    }
}

@Composable
fun NutritionDetails(nutritionRecord: NutritionRecord) {
    val mealsList = listOf("Unknown", "Breakfast", "Lunch", "Dinner", "Snack")
    val endTime = nutritionRecord.endTime.toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.sdp, vertical = 10.sdp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = mealsList[nutritionRecord.mealType],
            fontSize = 22.ssp,
            fontWeight = FontWeight.Normal
        )

        Row {
            Text(text = "${formateDate(endTime)} at ", fontSize = 10.ssp)
            Text(text = formatLastModifiedTime(endTime), fontSize = 10.ssp)
        }

        Spacer(modifier = Modifier.height(15.sdp))

        val nutritionItems = listOf("Calories Consumed" to "%.0f Cal".format(nutritionRecord.energy?.inKilocalories ?: 0f),
            "Food Item" to (nutritionRecord.name ?: "No data"),
            "Cholesterol" to "%.0f mg".format(nutritionRecord.cholesterol?.inMilligrams ?: 0f),
            "Fiber" to "%.0f g".format(nutritionRecord.dietaryFiber?.inGrams ?: 0f),
            "Potassium" to "%.0f mg".format(nutritionRecord.potassium?.inMilligrams ?: 0f),
            "Protein" to "%.0f g".format(nutritionRecord.protein?.inGrams ?: 0f),
            "Sodium" to "%.0f mg".format(nutritionRecord.sodium?.inMilligrams ?: 0f),
            "Sugar" to "%.0f g".format(nutritionRecord.sugar?.inGrams ?: 0f),
            "Total carbohydrates" to "%.0f g".format(nutritionRecord.totalCarbohydrate?.inGrams ?: 0f),
            "Total fat" to "%.0f g".format(nutritionRecord.totalFat?.inGrams ?: 0f),
            "Trans fat" to "%.0f g".format(nutritionRecord.transFat?.inGrams ?: 0f),
            "Saturated fat" to "%.0f g".format(nutritionRecord.saturatedFat?.inGrams ?: 0f),
            "Unsaturated fat" to "%.0f g".format(nutritionRecord.unsaturatedFat?.inGrams ?: 0f),
            "Monounsaturated fat" to "%.0f g".format(nutritionRecord.monounsaturatedFat?.inGrams ?: 0f),
            "Polyunsaturated fat" to "%.0f g".format(nutritionRecord.polyunsaturatedFat?.inGrams ?: 0f)

        )

        nutritionItems.forEach { (label, value) ->
            Spacer(modifier = Modifier.height(10.sdp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = label)
                Text(text = value)
            }
        }
    }
}
