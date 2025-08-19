package com.yoyo.mushmoodapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yoyo.mushmoodapp.ui.theme.MushMoodAppTheme
import com.yoyo.mushmoodapp.scenes.SceneDefs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(
            "SceneDefs",
            "Presets: default=${SceneDefs.DEFAULT_PRESET_ID}, " +
                "A=${SceneDefs.sceneA.presetId}, " +
                "B=${SceneDefs.sceneB.presetId}, " +
                "C=${SceneDefs.sceneC.presetId}"
        )
        enableEdgeToEdge()
        setContent {
            MushMoodAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MushMoodAppTheme {
        Greeting("Android")
    }
}