package com.project.sleepwell.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkHelper {
    fun isConnected(context: Context): Boolean {
        val networkManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeConnection = networkManager.activeNetwork ?: return false
        val capabilities = networkManager.getNetworkCapabilities(activeConnection) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }}