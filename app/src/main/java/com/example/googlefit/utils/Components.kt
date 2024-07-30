package com.example.googlefit.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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