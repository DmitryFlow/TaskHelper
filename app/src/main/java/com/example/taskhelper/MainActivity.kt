package com.example.taskhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.taskhelper.ui.navigation.AppNav
import com.example.taskhelper.ui.theme.TaskHelperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Habilita inyección con Hilt en esta Activity (p. ej., para ViewModels con hiltViewModel()).
class MainActivity : ComponentActivity() { // Activity raíz de la app (single-activity pattern).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent { ... } es el punto de entrada de Jetpack Compose dentro de la Activity.
        // Aquí definimos el árbol de UI declarativo que se va a renderizar.
        setContent {
            // TaskHelperTheme aplica colores, tipografías y shapes de Material 3 a tod0 el contenido.
            TaskHelperTheme {
                // Creamos y "recordamos" el NavController en el nivel superior (hoisting).
                // Ventajas: una única fuente de verdad para navegación, mejor testabilidad y reusabilidad.
                val navController = rememberNavController()

                // AppNav construye el NavHost con los destinos.
                // Ahora requiere 'navController' como parámetro (tras el refactor), así que se lo pasamos.
                AppNav(navController = navController)
            }
        }
    }
}
