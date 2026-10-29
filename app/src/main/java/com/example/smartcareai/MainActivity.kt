package com.example.smartcareai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcareai.navigation.SmartCareAppNavGraph
import com.example.smartcareai.ui.theme.SMARTCAREAITheme
import com.example.smartcareai.viewmodel.SmartCareViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SMARTCAREAITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: SmartCareViewModel = viewModel()
                    SmartCareAppNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
