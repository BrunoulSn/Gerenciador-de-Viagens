package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Menu Principal",
            fontSize = 28.sp
        )

        Text(
            text = "Bem-vindo ao Gerenciador de Viagens",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Funcionalidades serão adicionadas em breve...",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

