// ui/navigation/AppNav.kt
package com.example.taskhelper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskhelper.ui.screens.HomeScreen

object Destinations {
    // Constantes de rutas de navegación para evitar "strings mágicos" repartidos por el código.
    // Se usan al declarar destinos y al navegar (nav.navigate(Destinations.HOME)).
    const val HOME = "home"
}

@Composable
fun AppNav() {
    // Crea y "recuerda" un NavController dentro de este árbol Compose.
    // Gestiona el back stack y las transiciones entre pantallas.

    // Contenedor del gráfico de navegación para Compose.
    // - navController: el controlador que llevará el stack.
    // - startDestination: ruta inicial cuando se compone este NavHost.
    val nav = rememberNavController()
    NavHost(
        navController = nav,
        startDestination = Destinations.HOME
    ) {
        // Declara el destino "home" y asocia su contenido UI.
        // Cuando la ruta actual sea "home", se renderiza HomeScreen().
        composable(Destinations.HOME) {
            HomeScreen()
        }
    }
}
