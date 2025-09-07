// MainActivity.kt
package com.example.taskhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskhelper.ui.navigation.AppNav
import com.example.taskhelper.ui.theme.TaskHelperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent { TaskHelperTheme { AppNav() } }
        // 1) setContent { ... }  → punto de entrada a Jetpack Compose en la Activity.
        //    Sustituye a setContentView() y define el árbol de UI que se va a renderizar.
        // 2) TaskHelperTheme { ... } → aplica el tema Material 3 (colores, tipografías,
        //    shapes y modo claro/oscuro) a all lo que esté dentro del bloque.
        // 3) AppNav() → composable raíz de navegación. Crea/usa el NavController y el NavHost
        //    con las pantallas de la app. Cuando cambie el estado, solo se recomponen
        //    las partes afectadas del árbol.
        setContent { TaskHelperTheme { AppNav() } }
    }
}
