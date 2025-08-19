package com.yoyo.mushmoodapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.yoyo.mushmoodapp.ui.theme.MushMoodAppTheme
import com.yoyo.mushmoodapp.data.prefs.Prefs
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate - host=${prefs.getHost()}")
        enableEdgeToEdge()
        setContent {
            MushMoodAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                }
            }
        }
    }
}
