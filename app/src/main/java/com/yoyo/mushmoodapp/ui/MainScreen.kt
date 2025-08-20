package com.yoyo.mushmoodapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    BackHandler { }
    val state by viewModel.uiState.collectAsState()

    val gradientSpec = state.runningSceneId?.let { SceneDefs.scenes[it].gradient } ?: SceneDefs.defaultGradient

    val statusText = state.runningSceneId?.let { id ->
        val minutes = state.timeLeftSec / 60
        val seconds = state.timeLeftSec % 60
        "Scene ${'A' + id} — %02d:%02d".format(minutes, seconds)
    } ?: "No active scene"

    val snackbarHostState = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }
    var hostText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    AnimatedGradientBackground(gradientSpec) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    hostText = state.host
                    showSheet = true
                }) {
                    Text("⚙")
                }
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

        if (showSheet) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hostText,
                        onValueChange = { hostText = it },
                        label = { Text("Host") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.setHost(hostText)
                                showSheet = false
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Save") }
                        Button(
                            onClick = { viewModel.onPingClick() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Ping") }
                    }
                }
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

