package com.lancar.tugasakhir.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

// Kelas ini bertindak sebagai pembawa pesan global
@Singleton
class SessionExpiredNotifier @Inject constructor() {
    private val _sessionExpiredFlow = MutableSharedFlow<Boolean>()
    val sessionExpiredFlow = _sessionExpiredFlow.asSharedFlow()

    suspend fun notifySessionExpired() {
        _sessionExpiredFlow.emit(true)
    }
}