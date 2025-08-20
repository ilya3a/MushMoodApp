package com.yoyo.mushmoodapp.domain.usecase

import android.util.Log
import com.yoyo.mushmoodapp.data.wled.WledRepository
import com.yoyo.mushmoodapp.player.PlayerRepository
import com.yoyo.mushmoodapp.scenes.SceneDef
import javax.inject.Inject

/**
 * Activates a scene by triggering its preset on WLED and playing its song.
 */
class ActivateScene @Inject constructor(
    private val wledRepository: WledRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(scene: SceneDef): Boolean {
        Log.d(TAG, "activateScene presetId=${scene.presetId}")
        val success = wledRepository.setPreset(scene.presetId)
        if (success) {
            Log.d(TAG, "WLED preset activated, playing song=${scene.songRes}")
            playerRepository.playRaw(scene.songRes)
        } else {
            Log.e(TAG, "Failed to activate preset ${scene.presetId}")
        }
        return success
    }

    private companion object {
        const val TAG = "ActivateScene"
    }
}
