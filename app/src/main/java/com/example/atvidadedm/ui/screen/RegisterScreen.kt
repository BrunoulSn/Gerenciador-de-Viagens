package com.example.atvidadedm.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atvidadedm.TravelApplication
import com.example.atvidadedm.ui.theme.AtvidadeDMTheme
import com.example.atvidadedm.ui.theme.LocalThemeVersion
import com.example.atvidadedm.ui.theme.ThemeVersion
import com.example.atvidadedm.ui.viewmodel.RegisterViewModel
import com.example.atvidadedm.ui.viewmodel.RegisterViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    providedViewModel: RegisterViewModel? = null
) {
    val context = LocalContext.current
    val application = context.applicationContext as TravelApplication
    val snackbarHostState = remember { SnackbarHostState() }
    val version = LocalThemeVersion.current
    
    val defaultViewModel: RegisterViewModel = viewModel(
        factory = remember {
            RegisterViewModelFactory(application.userRepository)
        }
    )
    val activeViewModel = providedViewModel ?: defaultViewModel
    val uiState by activeViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            activeViewModel.onFeedbackMessageShown()
        }
    }

    LaunchedEffect(uiState.registrationSucceeded) {
        if (uiState.registrationSucceeded) {
            activeViewModel.onNavigationToLoginHandled()
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
                        "Criar Conta", 
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = if (version == ThemeVersion.VERSION_1) 16.dp else 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Preencha seus dados para começar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (version == ThemeVersion.VERSION_2) FontWeight.Medium else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    val inputFields = listOf(
                        Triple("Nome", uiState.name, activeViewModel::onNameChange),
                        Triple("E-mail", uiState.email, activeViewModel::onEmailChange),
                        Triple("Telefone", uiState.phone, activeViewModel::onPhoneChange)
                    )

                    inputFields.forEach { (label, value, onValueChange) ->
                        if (version == ThemeVersion.VERSION_1) {
                            OutlinedTextField(
                                value = value,
                                onValueChange = onValueChange,
                                label = { Text(label) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else {
                            TextField(
                                value = value,
                                onValueChange = onValueChange,
                                label = { Text(label) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Senha
                    if (version == ThemeVersion.VERSION_1) {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = activeViewModel::onPasswordChange,
                            label = { Text("Senha") },
                            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = activeViewModel::togglePasswordVisibility) {
                                    Icon(imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        TextField(
                            value = uiState.password,
                            onValueChange = activeViewModel::onPasswordChange,
                            label = { Text("Senha") },
                            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = activeViewModel::togglePasswordVisibility) {
                                    Icon(imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirmar Senha
                    if (version == ThemeVersion.VERSION_1) {
                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = activeViewModel::onConfirmPasswordChange,
                            label = { Text("Confirmar Senha") },
                            visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = activeViewModel::toggleConfirmPasswordVisibility) {
                                    Icon(imageVector = if (uiState.confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        TextField(
                            value = uiState.confirmPassword,
                            onValueChange = activeViewModel::onConfirmPasswordChange,
                            label = { Text("Confirmar Senha") },
                            visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = activeViewModel::toggleConfirmPasswordVisibility) {
                                    Icon(imageVector = if (uiState.confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = activeViewModel::registerUser,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(if (version == ThemeVersion.VERSION_1) 8.dp else 25.dp),
                        enabled = !uiState.isSaving
                    ) {
                        Text(if (uiState.isSaving) "Cadastrando..." else "Finalizar Cadastro")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
