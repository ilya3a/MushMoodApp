package com.yoyo.mushmoodapp.player

import androidx.annotation.RawRes

interface PlayerRepository {
    fun playRaw(@RawRes resId: Int)
    fun stop()
    fun fadeTo(@RawRes resId: Int, fadeOutMillis: Long = 1500, fadeInMillis: Long = 600)
}
