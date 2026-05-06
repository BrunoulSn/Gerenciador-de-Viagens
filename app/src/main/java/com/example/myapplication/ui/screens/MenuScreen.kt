package com.example.myapplication.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    onNewTripClick: () -> Unit,
    onMyTripsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
            }
        }
    }
}

