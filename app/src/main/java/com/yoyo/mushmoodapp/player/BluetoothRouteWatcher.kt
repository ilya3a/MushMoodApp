package com.yoyo.mushmoodapp.player

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.util.Log

class BluetoothRouteWatcher(
    private val context: Context,
    private val onBluetoothActive: () -> Unit
) {
    companion object {
        private const val TAG = "BluetoothRouteWatcher"
    }

    private val am: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val callback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
            if (isBluetoothOutputActive()) {
                Log.d(TAG, "onAudioDevicesAdded: Bluetooth output active")
                onBluetoothActive()
            }
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
            // no-op
        }
    }

    fun register() {
        Log.d(TAG, "register()")
        am.registerAudioDeviceCallback(callback, null)
        // אם כבר פעיל כרגע – נודיע מייד
        if (isBluetoothOutputActive()) {
            Log.d(TAG, "register(): Bluetooth already active, invoking callback")
            onBluetoothActive()
        }
    }

    fun unregister() {
        Log.d(TAG, "unregister()")
        am.unregisterAudioDeviceCallback(callback)
    }

    fun isBluetoothOutputActive(): Boolean {
        val outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (d in outs) {
            when (d.type) {
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLE_HEADSET,
                AudioDeviceInfo.TYPE_BLE_SPEAKER -> {
                    Log.d(TAG, "BT output detected: type=${d.type}, name=${d.productName}")
                    return true
                }
            }
        }
        return false
    }
}
