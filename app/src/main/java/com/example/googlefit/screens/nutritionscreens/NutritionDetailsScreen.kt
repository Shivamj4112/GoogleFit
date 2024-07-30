package com.example.googlefit.screens.nutritionscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import com.example.googlefit.HealthManager
import com.example.googlefit.utils.DebounceClick
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun NutritionDetailsScreen(healthManager: HealthManager, navController: NavHostController, endTime: String) {

    Surface {

        val nutritionRecords by healthManager.nutritionRecords.observeAsState(emptyList())


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

            nutritionRecords.forEach {record->

                if (endTime == record.endTime.toString()) {

                    record.apply {

                        Text("Calories Consumed : %.0f Cal".format(energy!!.inKilocalories))
                        Text("Food Item :${name!!}")
                        Text("Cholesterol : %.0f mg".format(cholesterol!!.inMicrograms))
                        Text("Fiber : %.0f g".format(dietaryFiber!!.inGrams))
                        Text("Potassium : %.0f mg".format(potassium!!.inMicrograms))
                        Text("Protein : %.0f g".format(protein!!.inGrams))
                        Text("Sodium : %.0f mg".format(sodium!!.inMicrograms))
                        Text("Sugar : %.0f g".format(sugar!!.inGrams))
                        Text("Total carbohydrates : %.0f g".format(totalCarbohydrate!!.inGrams))
                        Text("Total fat : %.0f g".format(totalFat!!.inGrams))
                        Text("Trans fat : %.0f g".format(transFat!!.inGrams))
                        Text("Saturated fat : %.0f g".format(saturatedFat!!.inGrams))
                        Text("Unsaturated fat : %.0f g".format(unsaturatedFat!!.inGrams))
                        Text("Monounsaturated fat : %.0f g".format(monounsaturatedFat!!.inGrams))
                        Text("Polyunsaturated : %.0f g".format(polyunsaturatedFat!!.inGrams))
                    }

                }
            }

        }



    }
}