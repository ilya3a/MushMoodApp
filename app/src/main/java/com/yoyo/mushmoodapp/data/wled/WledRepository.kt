package com.yoyo.mushmoodapp.data.wled

import android.util.Log
import com.yoyo.mushmoodapp.data.prefs.Prefs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WledRepository @Inject constructor(
    private val api: WledApi,
    private val prefs: Prefs
) {
    suspend fun setPreset(presetId: Int): Boolean {
        val url = "http://${'$'}{prefs.getHost()}/json/state"
        return try {
            val response = api.setPreset(url, PresetRequest(presetId))
            val success = response.isSuccessful
            Log.d(TAG, "setPreset(${presetId}) success=${'$'}success")
            success
        } catch (t: Throwable) {
            Log.e(TAG, "setPreset error", t)
            false
        }
    }

    suspend fun ping(): Boolean {
        val url = "https://${prefs.getHost()}:8080/json/state"
        return try {
            val response = api.ping(url)
            val success = response.isSuccessful
            Log.d(TAG, "ping success= success")
            success
        } catch (t: Throwable) {
            Log.e(TAG, "ping error", t)
            false
        }
    }

    private companion object {
        private const val TAG = "WledRepository"
    }
}
