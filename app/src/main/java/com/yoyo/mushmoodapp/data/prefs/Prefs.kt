package com.yoyo.mushmoodapp.data.prefs

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.yoyo.mushmoodapp.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("mushmood_prefs", Context.MODE_PRIVATE)

    fun getHost(): String {
        val host = prefs.getString(KEY_HOST, BuildConfig.DEFAULT_WLED_HOST) ?: BuildConfig.DEFAULT_WLED_HOST
        Log.d(TAG, "getHost: $host")
        return host
    }

    fun setHost(host: String) {
        Log.d(TAG, "setHost: $host")
        prefs.edit { putString(KEY_HOST, host) }
    }

    private companion object {
        private const val KEY_HOST = "host"
        private const val TAG = "Prefs"
    }
}
