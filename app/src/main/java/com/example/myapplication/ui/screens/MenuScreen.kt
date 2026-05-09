package com.example.myapplication.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.MenuViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MenuScreen(
    onNewTripClick: () -> Unit,
    onMyTripsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(LocalContext.current))
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.checkLocationAndFindTrip()
        } else {
            viewModel.setError("Permissões de localização negadas")
        }
    }

    BackHandler(enabled = drawerState.isClosed) {
        (context as? android.app.Activity)?.finish()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(16.dp)
            ) {
                Text("Menu", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
                
                NavigationDrawerItem(
                    label = { Text("Nova Viagem ✈️") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNewTripClick()
                    }
                )
                
                NavigationDrawerItem(
                    label = { Text("Minhas Viagens 🧳") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onMyTripsClick()
                    }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    label = { Text("Sobre ℹ️") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onAboutClick()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutClick()
                    }
                )
            }
        }
    ) {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                    text = "Use o menu lateral para navegar",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Button(
                    onClick = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("Abrir Menu")
                }

                // Location and Trip Section
                Button(
                    onClick = {
                        val hasPermissions = androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                        if (hasPermissions) {
                            viewModel.checkLocationAndFindTrip()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("Verificar Viagem Atual 📍")
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }

                if (uiState.errorMessage.isNotEmpty()) {
                    Text(
                        text = uiState.errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                uiState.currentTrip?.let { trip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Viagem Atual", fontSize = 20.sp)
                            Text("Destino: ${trip.destination}")
                            Text("Data Início: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(trip.startDate))}")
                            Text("Data Fim: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(trip.endDate))}")
                            Text("Tipo: ${trip.type}")
                            Text("Orçamento: R$ ${trip.budget}")
                            Text("Total de Gastos: R$ ${trip.totalExpenses}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuViewModelFactory(context: android.content.Context) = object : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MenuViewModel(context) as T
    }
}
