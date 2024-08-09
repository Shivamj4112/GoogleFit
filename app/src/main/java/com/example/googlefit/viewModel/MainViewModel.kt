package com.example.googlefit.viewModel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlefit.HealthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val healthManager: HealthManager, private val context: Context) : ViewModel() {

    private val _isSupportedVersion = MutableStateFlow<Boolean?>(null)
    val isSupportedVersion: StateFlow<Boolean?> = _isSupportedVersion

    private val _isHealthConnectInstalled = MutableStateFlow<Boolean?>(null)
    val isHealthConnectInstalled: StateFlow<Boolean?> = _isHealthConnectInstalled

    private val _allPermissionsGranted = MutableStateFlow<Boolean?>(null)
    val allPermissionsGranted: StateFlow<Boolean?> = _allPermissionsGranted

    private val _permissionDeniedCount = MutableStateFlow(0)
    val permissionDeniedCount: StateFlow<Int> = _permissionDeniedCount

    private val _showPermissionDialog = MutableStateFlow(false)
    val showPermissionDialog: StateFlow<Boolean> = _showPermissionDialog

    init {
        checkDeviceCompatibility()
        checkHealthConnectInstallation()
        setInitialHealthManagerState()
    }

    private fun checkDeviceCompatibility() {
        _isSupportedVersion.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun checkHealthConnectInstallation() {
        viewModelScope.launch {
            _isSupportedVersion.collect { isSupported ->
                if (isSupported == true) {
                    _isHealthConnectInstalled.value = isHealthConnectInstalled()
                }
            }
        }
    }

    private fun isHealthConnectInstalled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            true
        } else {
            try {
                context.packageManager.getPackageInfo("com.google.android.apps.healthdata", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun setInitialHealthManagerState() {
        viewModelScope.launch {
            _isHealthConnectInstalled.collect { isInstalled ->
                if (isInstalled == true) {
                    _allPermissionsGranted.value = healthManager.hasAllPermissions(healthManager.permissions)
                    if (_allPermissionsGranted.value == true){
                        healthManager.setRange("Week")
                        healthManager.setDateRange("Week")
                    }
                }
            }
        }
    }

    fun requestPermissions(permissionLauncher: ManagedActivityResultLauncher<Set<String>, Set<String>>) {
        permissionLauncher.launch(healthManager.permissions)
    }

    fun updatePermissions(result: Set<String>) {
        val allGranted = result.containsAll(healthManager.permissions)
        _allPermissionsGranted.value = allGranted
        if (!allGranted) {
            _permissionDeniedCount.value += 1
            if (_permissionDeniedCount.value >= 2) {
                _showPermissionDialog.value = true
            }
        }
    }

    fun installHealthConnect() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
        }
        context.startActivity(intent)
    }

    fun recheckHealthConnectInstallation() {
        _isHealthConnectInstalled.value = isHealthConnectInstalled()
    }

    fun resetPermissionDialog() {
        _showPermissionDialog.value = false
    }
}

