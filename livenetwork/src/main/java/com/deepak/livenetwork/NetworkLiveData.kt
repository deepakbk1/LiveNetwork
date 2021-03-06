package com.deepak.livenetwork

import android.app.Application
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object NetworkLiveData : LiveData<Boolean>() {
    private lateinit var application: Application
    private lateinit var networkRequest: NetworkRequest
    override fun onActive() {
        super.onActive()
        getDetails()
    }

    fun init(application: Application) {
        this.application = application
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    private fun getDetails() {
        val cm = application.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                postValue(false)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }
        })
    }

    //if you want network status only one time
    fun isNetworkAvailable(): Boolean {
        application.getSystemService(
            CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = getConnectionType()
        return activeNetwork != 0
    }

    //if you want network type//Returns connection type. 0: no internet; 1: mobile data; 2: wifi
    fun getConnectionType(): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm =
            application.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = 2
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = 1
                    }
                }
            }
        }
        return result
    }

    //check if internet is reachable or not
    suspend fun isInternetReachable(myURL: String?, timeOut: Int): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val inputStream: InputStream
                val url: URL = URL(myURL)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.connectTimeout = timeOut
                conn.readTimeout
                conn.connect()
                inputStream = conn.inputStream
                conn.disconnect()
                inputStream != null
            } catch (e: Exception) {
                false
            }

        }


    fun getInternetSpeed(): String {
        var downSpeed = -1
        downSpeed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cm =
                application.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val nc = cm.getNetworkCapabilities(cm.activeNetwork)
            nc!!.linkDownstreamBandwidthKbps
            //val upSpeed = nc.linkUpstreamBandwidthKbps

        } else {
            val cm =
                application.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val nc = cm.getNetworkCapabilities(cm.allNetworks[cm.activeNetworkInfo?.type!!])
            nc!!.linkDownstreamBandwidthKbps
            //val upSpeed = nc.linkUpstreamBandwidthKbps
        }
        return when (downSpeed) {
            150 -> "POOR"
            in 151..550 -> "MODERATE"
            in 550..2000 -> "GOOD"
            in 2000..Int.MAX_VALUE -> "EXCELLENT"
            else -> { // Note the block
                "UNKNOWN"
            }
        }
    }
}