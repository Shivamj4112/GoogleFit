package com.example.googlefit.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

enum class NetworkStatus {
    WIFI,
    CELLULAR,
    ETHERNET,
    NO_CONNECTION
}

class ConnectivityObserver(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _networkStatus = MutableLiveData<NetworkStatus>()
    val networkStatus: LiveData<NetworkStatus> get() = _networkStatus

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternet = networkCapabilities!!.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            if (hasInternet) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> _networkStatus.postValue(NetworkStatus.WIFI)
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> _networkStatus.postValue(NetworkStatus.CELLULAR)
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> _networkStatus.postValue(NetworkStatus.ETHERNET)
                    else -> _networkStatus.postValue(NetworkStatus.NO_CONNECTION)
                }
            }
        }

        override fun onLost(network: Network) {
            _networkStatus.postValue(NetworkStatus.NO_CONNECTION)
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun cleanup() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
