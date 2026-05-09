package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.di.AppContainer
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize database
        try {
            AppContainer.getDatabase(this)
        } catch (e: Exception) {
            // Log or handle error
            e.printStackTrace()
            return // or finish()
        }
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}
