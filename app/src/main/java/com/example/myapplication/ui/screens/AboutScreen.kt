package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sobre",
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Gerenciador de Viagens v1.0",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Um aplicativo para gerenciar suas viagens de forma fácil e prática.",
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Desenvolvido com Kotlin e Jetpack Compose",
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(onClick = onBackClick, modifier = Modifier.padding(top = 32.dp)) {
            Text("Voltar")
        }
    }
}

