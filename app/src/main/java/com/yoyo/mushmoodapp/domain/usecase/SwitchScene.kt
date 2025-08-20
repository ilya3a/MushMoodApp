package com.yoyo.mushmoodapp.domain.usecase

import android.util.Log
import com.yoyo.mushmoodapp.data.wled.WledRepository
import com.yoyo.mushmoodapp.player.PlayerRepository
import com.yoyo.mushmoodapp.scenes.SceneDef
import javax.inject.Inject

/**
 * Switches from the current scene to another one.
 * Stops the current song, triggers the new preset and plays the new song.
 */
class SwitchScene @Inject constructor(
    private val wledRepository: WledRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(scene: SceneDef): Boolean {
        Log.d(TAG, "switchScene to presetId=${scene.presetId}")
        playerRepository.stop()
        val success = wledRepository.setPreset(scene.presetId)
        if (success) {
            Log.d(TAG, "WLED preset switched, playing song=${scene.songRes}")
            playerRepository.playRaw(scene.songRes)
        } else {
            Log.e(TAG, "Failed to switch to preset ${scene.presetId}")
        }
        return success
    }

    private companion object {
        const val TAG = "SwitchScene"
    }
}
