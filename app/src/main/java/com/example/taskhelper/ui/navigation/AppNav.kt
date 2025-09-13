package com.example.taskhelper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskhelper.ui.task.HomeScreen

/**
 * ¿Qué es, para qué sirve y dónde se usa?
 * - AppNav define el "gráfico" de navegación de la app usando Navigation-Compose.
 * - ¿Para qué sirve? Centraliza rutas y destinos (pantallas) y cómo se conectan.
 * - ¿Dónde se usa? En la raíz de UI (por ejemplo, en tu MainActivity o AppRoot), pasando un NavHostController.
 * - ¿Por qué así? Hoist del NavController: lo crea el nivel superior (single source of truth) y se inyecta aquí.
 */
object Routes {                    // Objeto contenedor de rutas para evitar strings mágicos repartidos.
    const val HOME = "home"        // Ruta de la pantalla Home. Se usa en startDestination y al declarar el destino.
}

@Composable
fun AppNav(                        // Función Composable que construye el NavHost con todos los destinos.
    navController: NavHostController // El controlador de navegación lo provee el nivel superior (no se crea aquí).
) {
    NavHost(                       // NavHost "aloja" el gráfico de navegación y gestiona el back stack.
        navController = navController, // Controlador compartido: permite navegar desde distintas pantallas.
        startDestination = Routes.HOME  // Destino inicial al lanzar la app (pantalla Home).
    ) {
        composable(route = Routes.HOME) { // Declaración del destino "home": cuando la ruta sea "home"...
            HomeScreen()                  // ...se compone la pantalla Home. El VM se resolverá vía Hilt.
            // Nota: si HomeScreen necesitara navegar, puede obtener el navController con:
            // val nav = LocalNavigationProvider.current (si creas un CompositionLocal) o pásalo como parámetro.
        }

        // 🔜 Aquí irán más destinos (por feature):
        // composable(route = "${Routes.TASK_DETAIL}/{taskId}") { backStackEntry ->
        //     val id = backStackEntry.arguments?.getString("taskId")?.toLong()
        //     TaskDetailScreen(taskId = id)
        // }
    }
}
