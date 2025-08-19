package com.yoyo.mushmoodapp.scenes

import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.yoyo.mushmoodapp.R

/**
 * Definitions for scenes, their preset ids, songs and gradients.
 */
data class GradientSpec(
    val colors: List<Color>,
    val animationDurationMillis: Int
)

data class SceneDef(
    val presetId: Int,
    @RawRes val songRes: Int,
    val gradient: GradientSpec
)

object SceneDefs {
    const val DEFAULT_PRESET_ID = 1
    const val DEFAULT_SONG_RES = R.raw.default_song

    val defaultGradient = GradientSpec(
        colors = listOf(
            Color(0xFF0f2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        ),
        animationDurationMillis = 4000
    )

    val sceneA = SceneDef(
        presetId = 11,
        songRes = R.raw.scene_a,
        gradient = GradientSpec(
            colors = listOf(Color(0xFFFF5F6D), Color(0xFFFFC371)),
            animationDurationMillis = 2000
        )
    )

    val sceneB = SceneDef(
        presetId = 12,
        songRes = R.raw.scene_b,
        gradient = GradientSpec(
            colors = listOf(Color(0xFF2193B0), Color(0xFF6DD5ED)),
            animationDurationMillis = 2000
        )
    )

    val sceneC = SceneDef(
        presetId = 13,
        songRes = R.raw.scene_c,
        gradient = GradientSpec(
            colors = listOf(Color(0xFFcc2b5e), Color(0xFF753a88)),
            animationDurationMillis = 2000
        )
    )

    val scenes = listOf(sceneA, sceneB, sceneC)
}
