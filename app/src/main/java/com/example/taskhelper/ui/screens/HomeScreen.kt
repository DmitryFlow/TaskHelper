// ui/screens/HomeScreen.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.taskhelper.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun HomeScreen() {
    // Scaffold es un layout de alto nivel de Material 3 con "slots" (topBar, bottomBar, FAB, content).
    // ¿Para qué? Te da estructura estándar (app bar + contenido) y maneja insets automáticamente.
    // ¿Dónde se usa? En prácticamente cualquier pantalla con barra superior/menús.
    Scaffold(
        // Slot "topBar" del Scaffold: define la barra superior.
        // Aquí usamos CenterAlignedTopAppBar (M3) con un título centrado.
        topBar = { CenterAlignedTopAppBar(title = { Text("TaskHelper") }) }
    ) { padding ->
        // Este lambda es el "content" del Scaffold. Recibe un PaddingValues (padding)
        // para que el contenido no quede TAPADO por la topBar/bottomBar.
        // Aplicamos ese padding al Text con Modifier.padding(padding).
        Text("¡Hola, TaskHelper!", modifier = Modifier.padding(padding))
        // ¿Dónde se usa? Esta vista se mostrará cuando el NavHost navegue a "Home".
        // Más adelante puedes reemplazar este Text por tu lista de tareas, etc.
    }
}
