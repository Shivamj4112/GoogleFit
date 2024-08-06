package com.example.googlefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.googlefit.navigation.Navigation
import com.example.googlefit.ui.theme.GoogleFitTheme
import com.example.googlefit.utils.ConnectivityObserver
import com.example.googlefit.utils.NetworkStatus
import ir.kaaveh.sdpcompose.sdp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleFitTheme {

//                var connectivityObserver = ConnectivityObserver(this)
//                val networkStatus by connectivityObserver.networkStatus.observeAsState(NetworkStatus.NO_CONNECTION)

//                when (networkStatus) {
//                    NetworkStatus.NO_CONNECTION -> {
//                        Text(
//                            text = "No internet connection",
//                            color = Color.Red,
//                            modifier = Modifier.padding(16.sdp)
//                        )
//                    }
//                    else -> {
                        Navigation()
//                    }
//                }
            }
        }
    }

}