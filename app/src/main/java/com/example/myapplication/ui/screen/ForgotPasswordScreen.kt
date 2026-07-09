package com.example.atvidadedm.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvidadedm.TravelApplication
import com.example.atvidadedm.ui.theme.AtvidadeDMTheme
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion
import com.example.atvidadedm.ui.viewmodel.ForgotPasswordViewModel
import com.example.atvidadedm.ui.viewmodel.ForgotPasswordViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    providedViewModel: ForgotPasswordViewModel? = null
) {
    val context = LocalContext.current
    val application = context.applicationContext as TravelApplication
    val snackbarHostState = remember { SnackbarHostState() }
    val version = LocalThemeVersion.current
    
    val defaultViewModel: ForgotPasswordViewModel = viewModel(
        factory = remember {
            ForgotPasswordViewModelFactory(application.userRepository)
        }
    )
    val viewModel = providedViewModel ?: defaultViewModel
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onFeedbackMessageShown()
        }
    }

    LaunchedEffect(uiState.recoverySent) {
        if (uiState.recoverySent) {
            viewModel.onRecoveryHandled()
            onBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Recuperar Senha",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (version == ThemeVersion.VERSION_2) 1.sp else 0.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (version == ThemeVersion.VERSION_1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    titleContentColor = if (version == ThemeVersion.VERSION_1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = if (version == ThemeVersion.VERSION_1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = if (version == ThemeVersion.VERSION_1) 32.dp else 48.dp),
            verticalArrangement = if (version == ThemeVersion.VERSION_1) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (version == ThemeVersion.VERSION_2) Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Esqueceu sua senha?",
                style = if (version == ThemeVersion.VERSION_1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Informe seu e-mail para receber as instruções de recuperação.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 12.dp else 28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (version == ThemeVersion.VERSION_1) 0.dp else 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (version == ThemeVersion.VERSION_1) 20.dp else 28.dp)
                ) {
                    if (version == ThemeVersion.VERSION_1) {
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("E-mail") },
                            placeholder = { Text("exemplo@email.com") },
                            isError = uiState.emailError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        TextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("E-mail") },
                            placeholder = { Text("exemplo@email.com") },
                            isError = uiState.emailError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = viewModel::submitRecovery,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 25.dp),
                        enabled = !uiState.isSending
                    ) {
                        Text(if (uiState.isSending) "Enviando..." else "Redefinir Senha")
                    }
                }
            }
        }
    }
}
