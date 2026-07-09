package com.example.atvidadedm.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion

@Composable
fun AboutScreen() {
    val version = LocalThemeVersion.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = if (version == ThemeVersion.VERSION_1) Alignment.Start else Alignment.CenterHorizontally
    ) {
        if (version == ThemeVersion.VERSION_2) Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (version == ThemeVersion.VERSION_1) "Sobre o Projeto" else "GO TRAVEL",
            style = if (version == ThemeVersion.VERSION_1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = if (version == ThemeVersion.VERSION_2) 4.sp else 0.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (version == ThemeVersion.VERSION_1) 2.dp else 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = if (version == ThemeVersion.VERSION_1) Alignment.Start else Alignment.CenterHorizontally
            ) {
                // Título/Proposta de valor clara
                Text(
                    text = "Seu assistente de viagem inteligente.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = if (version == ThemeVersion.VERSION_1) TextAlign.Start else TextAlign.Center
                )

                // Explicação do core do sistema (IA + Inputs do usuário)
                Text(
                    text = "O Travel Manager transforma seu planejamento. Diga-nos para onde quer ir, suas datas e seu orçamento disponível, e nossa Inteligência Artificial criará um roteiro personalizado, otimizado e sob medida para o seu bolso.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = if (version == ThemeVersion.VERSION_1) TextAlign.Start else TextAlign.Justify
                )

                if (version == ThemeVersion.VERSION_2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "v1.0.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}