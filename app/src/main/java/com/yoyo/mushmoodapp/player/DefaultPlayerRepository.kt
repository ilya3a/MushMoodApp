package com.yoyo.mushmoodapp.player

import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.annotation.RawRes
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.audio.DefaultAudioSink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
class DefaultPlayerRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : PlayerRepository {

    companion object {
        private const val TAG = "DefaultPlayerRepository"
    }

    // יציב למדיה
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    // נעדיף 48kHz (מתלבש טוב על A2DP)
    private val TARGET_SAMPLE_RATE_HZ = 48_000
    // ל-Bluetooth נשמור סטריאו
    private val FORCE_MONO = false

    // RenderersFactory: float off, ריסמפל ל-48kHz, סטריאו
    private fun buildStableRenderersFactory(ctx: Context): RenderersFactory {
//        val resampler = AudioProcessor().apply {
//            setOutputSampleRateHz(TARGET_SAMPLE_RATE_HZ)
//        }

//        val processors: Array<AudioProcessor> = arrayOf(resampler)

        return object : DefaultRenderersFactory(ctx) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): DefaultAudioSink {
                Log.d(TAG, ">>> DefaultAudioSink: floatOutput=FALSE, target=${TARGET_SAMPLE_RATE_HZ}Hz, stereo")
                return DefaultAudioSink.Builder()
                    .setAudioCapabilities(AudioCapabilities.getCapabilities(context))
                    .setEnableFloatOutput(false)
                    .setEnableAudioTrackPlaybackParams(true)
//                    .setAudioProcessors(processors)
                    .build()
            }
        }.apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        }
    }

    private fun normalizeAudioManagerState() {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            Log.d(TAG, "AudioManager BEFORE: mode=${am.mode}, sco=${am.isBluetoothScoOn}, speaker=${am.isSpeakerphoneOn}")
            // חשוב: לא להיות במצב תקשורת (שמקושר ל-SCO)
            if (am.mode != AudioManager.MODE_NORMAL) {
                Log.w(TAG, "Audio mode=${am.mode}; switching to MODE_NORMAL")
                am.mode = AudioManager.MODE_NORMAL
            }
            // חשוב: SCO OFF – ל-A2DP אסור שיהיה SCO פעיל
            if (am.isBluetoothScoOn) {
                Log.w(TAG, "Bluetooth SCO is ON; turning it OFF for A2DP")
                am.stopBluetoothSco()
                am.isBluetoothScoOn = false
            }
            Log.d(TAG, "AudioManager AFTER : mode=${am.mode}, sco=${am.isBluetoothScoOn}, speaker=${am.isSpeakerphoneOn}")
        } catch (t: Throwable) {
            Log.e(TAG, "normalizeAudioManagerState() failed", t)
        }
    }

    private val player: ExoPlayer by lazy {
        normalizeAudioManagerState()
        Log.d(TAG, ">>> Building ExoPlayer (target ${TARGET_SAMPLE_RATE_HZ}Hz, stereo)")

        ExoPlayer.Builder(context, buildStableRenderersFactory(context))
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
            .setHandleAudioBecomingNoisy(true) // יעצור אם ה-BT יתנתק
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(
                            TAG,
                            "onPlayerError: code=${error.errorCode} name=${error.errorCodeName} cause=${error.cause}",
                            error
                        )
                    }
                })
            }
    }

    /** נגן raw מייד (לא מחכה ל-BT). */
    override fun playRaw(@RawRes resId: Int) {
        try {
            val uri = RawResourceDataSource.buildRawResourceUri(resId)
            val item = MediaItem.fromUri(uri)
            Log.d(TAG, ">>> playRaw(): resId=$resId uri=$uri")
            player.setMediaItem(item)
            player.prepare()
            player.volume = 1f
            player.playWhenReady = true
        } catch (t: Throwable) {
            Log.e(TAG, "playRaw() failed for resId=$resId", t)
        }
    }

    /** נגן raw רק כש-Bluetooth A2DP/‏BLE אקטיבי. */
    fun playRawOnBluetooth(@RawRes resId: Int) {
        val watcher = BluetoothRouteWatcher(context) {
            // יופעל כאשר BT אקטיבי
            scope.launch {
                Log.d(TAG, "BT active → starting playback")
                playRaw(resId)
            }
        }
        watcher.register()

        // אם כבר פעיל – ננגן מייד; אחרת נחכה ל-callback
        if (watcher.isBluetoothOutputActive()) {
            Log.d(TAG, "Bluetooth already active → play now")
            playRaw(resId)
            // אפשר לבטל רישום מיד אם לא צריך המשך האזנה
            watcher.unregister()
        } else {
            Log.d(TAG, "Waiting for Bluetooth to become active…")
            // אופציונלי: ביטול אוטומטי אחרי X שניות אם לא התחבר
            scope.launch {
                delay(15_000)
                Log.w(TAG, "BT not active after timeout; unregister watcher")
                watcher.unregister()
            }
        }
    }

    override fun stop() {
        Log.d(TAG, ">>> stop()")
        try { player.stop() } catch (t: Throwable) {
            Log.e(TAG, "stop() failed", t)
        }
    }

    fun release() {
        Log.d(TAG, ">>> release()")
        try { player.release() } catch (t: Throwable) {
            Log.e(TAG, "release() failed", t)
        }
    }

    override fun fadeTo(@RawRes resId: Int, fadeOutMillis: Long, fadeInMillis: Long) {
        scope.launch {
            Log.d(TAG, ">>> fadeTo(): resId=$resId, fadeOutMs=$fadeOutMillis, fadeInMs=$fadeInMillis")
            fadeVolume(from = player.volume, to = 0f, duration = fadeOutMillis)

            val uri = RawResourceDataSource.buildRawResourceUri(resId)
            val item = MediaItem.fromUri(uri)
            Log.d(TAG, ">>> Switching media item to resId=$resId")
            player.setMediaItem(item)
            player.prepare()
            player.volume = 0f
            player.playWhenReady = true

            fadeVolume(from = 0f, to = 1f, duration = fadeInMillis)
        }
    }

    private suspend fun fadeVolume(from: Float, to: Float, duration: Long) {
        val steps = 20
        val stepTime = if (duration <= 0L) 0L else duration / steps
        val delta = (to - from) / steps
        var current = from

        Log.d(TAG, ">>> fadeVolume(): from=$from to=$to durationMs=$duration stepMs=$stepTime")

        repeat(steps) {
            current += delta
            player.volume = current.coerceIn(0f, 1f)
            if (stepTime > 0L) delay(stepTime)
        }
        player.volume = to.coerceIn(0f, 1f)
    }
}
