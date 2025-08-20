package com.yoyo.mushmoodapp.domain.usecase

import android.util.Log
import com.yoyo.mushmoodapp.data.wled.WledRepository
import javax.inject.Inject

/**
 * Pings the configured WLED host to check connectivity.
 */
class PingWled @Inject constructor(
    private val wledRepository: WledRepository,
) {
    suspend operator fun invoke(): Boolean {
        Log.d(TAG, "pingWled")
        val success = wledRepository.ping()
        Log.d(TAG, "pingWled success=$success")
        return success
    }

    private companion object {
        const val TAG = "PingWled"
    }
}
