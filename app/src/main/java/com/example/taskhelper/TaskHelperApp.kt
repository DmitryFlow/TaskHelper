// TaskHelperApp.kt
package com.example.taskhelper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la app para Hilt (DI).
 *
 * Al anotar la clase Application con @HiltAndroidApp, Hilt genera e inicializa el contenedor
 * de dependencias al arrancar el proceso. Desde este momento puedes:
 *  - Usar @AndroidEntryPoint en Activities/Fragments/Services/Compose para inyectar dependencias.
 *  - Definir módulos (@Module/@InstallIn) y scopes (SingletonComponent, etc.).
 *  - Obtener @ApplicationContext inyectado cuando lo necesites.
 *
 * Notas:
 *  - Debe existir **una sola** clase con @HiltAndroidApp en toda la app.
 *  - Registra esta clase en el AndroidManifest:
 *      <application android:name=".TaskHelperApp" ... />
 *  - Mantén esta clase ligera: evita trabajo pesado en onCreate(); Hilt solo necesita inicializarse.
 */
@HiltAndroidApp
class TaskHelperApp : Application()