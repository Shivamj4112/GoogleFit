package com.example.googlefit.modelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.googlefit.HealthManager
import com.example.googlefit.viewModel.MainViewModel

class MainViewModelFactory(
    private val healthManager: HealthManager,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(healthManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
