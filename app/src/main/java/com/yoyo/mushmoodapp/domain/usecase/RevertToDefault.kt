package com.yoyo.mushmoodapp.domain.usecase

import android.util.Log
import com.yoyo.mushmoodapp.data.wled.WledRepository
import com.yoyo.mushmoodapp.player.PlayerRepository
import com.yoyo.mushmoodapp.scenes.SceneDefs
import javax.inject.Inject

/**
 * Reverts WLED and audio to the default preset and song.
 */
class RevertToDefault @Inject constructor(
    private val wledRepository: WledRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(): Boolean {
        Log.d(TAG, "revertToDefault presetId=${SceneDefs.DEFAULT_PRESET_ID}")
        val success = wledRepository.setPreset(SceneDefs.DEFAULT_PRESET_ID)
        playerRepository.fadeTo(SceneDefs.DEFAULT_SONG_RES)
        if (success) {
            Log.d(TAG, "Reverted to default preset")
        } else {
            Log.e(TAG, "Failed to revert to default preset")
        }
        return success
    }

    private companion object {
        const val TAG = "RevertToDefault"
    }
}
