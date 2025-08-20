package com.yoyo.mushmoodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.yoyo.mushmoodapp.ui.MainScreen
import com.yoyo.mushmoodapp.ui.theme.MushMoodAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MushMoodAppTheme {
                MainScreen()
            }
        }
    }
}
