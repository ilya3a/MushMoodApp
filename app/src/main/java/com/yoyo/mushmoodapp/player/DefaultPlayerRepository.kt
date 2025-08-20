package com.yoyo.mushmoodapp.player

import android.content.Context
import androidx.annotation.RawRes
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.util.UnstableApi
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

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .build()

    override fun playRaw(@RawRes resId: Int) {
        val uri = RawResourceDataSource.buildRawResourceUri(resId)
        val item = MediaItem.fromUri(uri)
        player.setMediaItem(item)
        player.prepare()
        player.volume = 1f
        player.playWhenReady = true
    }

    override fun stop() {
        player.stop()
    }

    override fun fadeTo(@RawRes resId: Int, fadeOutMillis: Long, fadeInMillis: Long) {
        scope.launch {
            fadeVolume(from = player.volume, to = 0f, duration = fadeOutMillis)
            val uri = RawResourceDataSource.buildRawResourceUri(resId)
            val item = MediaItem.fromUri(uri)
            player.setMediaItem(item)
            player.prepare()
            player.volume = 0f
            player.playWhenReady = true
            fadeVolume(from = 0f, to = 1f, duration = fadeInMillis)
        }
    }

    private suspend fun fadeVolume(from: Float, to: Float, duration: Long) {
        val steps = 20
        val stepTime = duration / steps
        val delta = (to - from) / steps
        var current = from
        repeat(steps) {
            current += delta
            player.volume = current.coerceIn(0f, 1f)
            delay(stepTime)
        }
    }
}
