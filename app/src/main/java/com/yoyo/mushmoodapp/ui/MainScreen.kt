package com.yoyo.mushmoodapp.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yoyo.mushmoodapp.scenes.SceneDefs
import com.yoyo.mushmoodapp.scenes.GradientSpec

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    val gradientSpec = state.runningSceneId?.let { SceneDefs.scenes[it].gradient } ?: SceneDefs.defaultGradient

    val statusText = state.runningSceneId?.let { id ->
        val minutes = state.timeLeftSec / 60
        val seconds = state.timeLeftSec % 60
        "Scene ${'A' + id} — %02d:%02d".format(minutes, seconds)
    } ?: "No active scene"

    AnimatedGradientBackground(gradientSpec) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MUSHMOODAPP")
                            Text(statusText, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.onSceneClick(0) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) { Text("Scene A") }
                Button(
                    onClick = { viewModel.onSceneClick(1) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) { Text("Scene B") }
                Button(
                    onClick = { viewModel.onSceneClick(2) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) { Text("Scene C") }
            }
        }
    }
}

@Composable
private fun AnimatedGradientBackground(
    spec: GradientSpec,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "gradient")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(spec.animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    val brush = Brush.linearGradient(
        colors = spec.colors,
        start = Offset.Zero,
        end = Offset(offset, offset)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        content()
    }
}

