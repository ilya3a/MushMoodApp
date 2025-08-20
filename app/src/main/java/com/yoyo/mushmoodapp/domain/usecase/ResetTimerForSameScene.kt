package com.yoyo.mushmoodapp.domain.usecase

import android.util.Log
import javax.inject.Inject

/**
 * Resets the timer when the same scene button is pressed again.
 * Currently only logs the action; timer implementation will hook in later.
 */
class ResetTimerForSameScene @Inject constructor() {
    operator fun invoke() {
        Log.d(TAG, "resetTimerForSameScene")
    }

    private companion object {
        const val TAG = "ResetTimerForSameScene"
    }
}
