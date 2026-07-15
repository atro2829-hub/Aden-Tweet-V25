package com.adentweets.app.core.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isOnline: Flow<Boolean>
    fun getCurrentConnectionStatus(): Boolean
}