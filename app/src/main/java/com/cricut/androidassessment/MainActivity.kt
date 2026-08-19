package com.cricut.androidassessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.cricut.androidassessment.ui.screens.AssessmentScreen
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAssessmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AssessmentScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
