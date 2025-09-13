package com.example.taskhelper.ui.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * ¿Qué es, para qué sirve y dónde se usa?
 * - HomeScreen es la pantalla principal de la feature "task".
 * - Orquesta la UI en función del estado expuesto por el TaskViewModel (loading / datos / error).
 * - ¿Dónde se usa? Se registra en el NavHost (AppNav) como destino "home".
 * - ¿Por qué así? Separación de responsabilidades: VM gestiona flujos/estado; la pantalla solo dibuja.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Marca una función Compose: describe UI declarativa que puede recomponerse.
fun HomeScreen( // Punto de entrada de la pantalla en Compose.
    viewModel: TaskViewModel = hiltViewModel() // Inyección del ViewModel con Hilt; evita pasar VM manualmente.
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ^ Suscribimos la UI al StateFlow expuesto por el VM respetando el ciclo de vida.
    //   'by' delega para obtener el valor actual y recomponer la UI al cambiar.

    Scaffold( // Layout de alto nivel de Material 3: gestiona slots (topBar, content, etc.) y paddings de sistema.
        topBar = { TopAppBar(title = { Text("TaskHelper") }) } // Barra superior con título; común en apps.
    ) { padding -> // 'padding' son los insets que el Scaffold aplica para que el contenido no quede oculto por la app bar.

        when { // Render condicional según el estado actual: loading, error o lista de datos.

            state.isLoading -> // Caso 1: el estado indica carga inicial o refresco.
                Box(Modifier.padding(padding).fillMaxSize()) { // Contenedor que llena pantalla respetando el padding del Scaffold.
                    CircularProgressIndicator(Modifier.padding(16.dp)) // Indicador de progreso Material; feedback a usuario.
                }

            state.error != null -> // Caso 2: ha ocurrido un error en el flujo (Result.Failure).
                Box(Modifier.padding(padding).fillMaxSize()) { // Contenedor a pantalla completa.
                    Text("Error: ${state.error}", Modifier.padding(16.dp)) // Mensaje simple de error (puedes mapear a textos localizados).
                }

            else -> // Caso 3: datos disponibles; pintamos la lista de tareas.
                LazyColumn( // Lista perezosa (renderiza solo lo visible); ideal para datasets potencialmente largos.
                    Modifier.padding(padding).fillMaxSize() // Ocupar toda la pantalla y respetar padding del Scaffold.
                ) {
                    items(state.tasks) { task -> // Itera sobre la lista de tareas de dominio.
                        ListItem( // Componente M3 listo para mostrar filas con headline/supporting, etc.
                            headlineContent = { Text(task.title) }, // Título visible de la tarea.
                            supportingContent = { // Línea secundaria: estado lógico (Done/Pending).
                                Text(if (task.completed) "Done" else "Pending")
                            }
                        )
                        Divider() // Separador visual entre ítems; mejora legibilidad.
                    }
                }
        }
    }
}
