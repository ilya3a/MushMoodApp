package com.yoyo.mushmoodapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoyo.mushmoodapp.data.prefs.Prefs
import com.yoyo.mushmoodapp.domain.usecase.ActivateScene
import com.yoyo.mushmoodapp.domain.usecase.PingWled
import com.yoyo.mushmoodapp.domain.usecase.ResetTimerForSameScene
import com.yoyo.mushmoodapp.domain.usecase.RevertToDefault
import com.yoyo.mushmoodapp.domain.usecase.SwitchScene
import com.yoyo.mushmoodapp.scenes.SceneDefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: Prefs,
    private val activateScene: ActivateScene,
    private val switchScene: SwitchScene,
    private val resetTimerForSameScene: ResetTimerForSameScene,
    private val revertToDefault: RevertToDefault,
    private val pingWled: PingWled,
) : ViewModel() {

    data class UiState(
        val runningSceneId: Int? = null,
        val timeLeftSec: Int = 0,
        val host: String = "",
        val message: String? = null,
        val isPinned: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun load() {
        _uiState.update { it.copy(host = prefs.getHost()) }
    }

    fun setHost(host: String) {
        prefs.setHost(host)
        _uiState.update { it.copy(host = host) }
    }

    fun onPingClick() {
        if (_uiState.value.host.isBlank()) {
            _uiState.update { it.copy(message = "Host is empty") }
            return
        }
        viewModelScope.launch {
            val success = pingWled()
            if (success) {
                _uiState.update { it.copy(message = "Ping successful") }
            } else {
                _uiState.update { it.copy(message = "Ping failed") }
            }
        }
    }

    fun onSceneClick(sceneId: Int) {
        val currentId = _uiState.value.runningSceneId
        val scene = SceneDefs.scenes.getOrNull(sceneId) ?: return
        val needNetwork = currentId == null || currentId != sceneId
        if (needNetwork && _uiState.value.host.isBlank()) {
            _uiState.update { it.copy(message = "Host is empty") }
            return
        }
        viewModelScope.launch {
            when {
                currentId == null -> {
                    if (activateScene(scene)) {
                        _uiState.update { it.copy(runningSceneId = sceneId) }
                        startTimer()
                    } else {
                        _uiState.update { it.copy(message = "Failed to activate scene") }
                    }
                }
                currentId == sceneId -> {
                    resetTimerForSameScene()
                    startTimer()
                }
                else -> {
                    if (switchScene(scene)) {
                        _uiState.update { it.copy(runningSceneId = sceneId) }
                        startTimer()
                    } else {
                        _uiState.update { it.copy(message = "Failed to switch scene") }
                    }
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = TIMER_SECONDS
            while (remaining > 0) {
                _uiState.update { it.copy(timeLeftSec = remaining) }
                delay(1000)
                remaining--
            }
            _uiState.update { it.copy(runningSceneId = null, timeLeftSec = 0) }
            revertToDefaultWithRetry()
        }
    }

    private suspend fun revertToDefaultWithRetry() {
        var success = revertToDefault()
        if (!success) {
            success = revertToDefault()
            if (!success) {
                _uiState.update { it.copy(message = "Failed to revert to default preset") }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun onPinStatusChanged(pinned: Boolean) {
        _uiState.update { it.copy(isPinned = pinned) }
    }

    companion object {
        private const val TIMER_SECONDS = 120
    }
}
