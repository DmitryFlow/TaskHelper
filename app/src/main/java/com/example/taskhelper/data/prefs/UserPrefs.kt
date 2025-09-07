// data/local/prefs/UserPrefs.kt
package com.example.taskhelper.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Crea **una** instancia de Preferences DataStore asociada al Context (scope de aplicación).
// - 'preferencesDataStore' es un property delegate que genera perezosamente el DataStore.
// - name = "user_prefs" será el nombre del fichero en /data/data/<pkg>/files/datastore/.
// - Al ser 'val Context.dataStore by ...' **de nivel de archivo**, evitas crear múltiples stores.
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

// Wrapper de acceso a preferencias de usuario.
// - Recibe un Context (usualmente inyectado con @ApplicationContext desde Hilt).
// - Expone Flows para leer y funciones 'suspend' para escribir.
class UserPrefs(private val context: Context) {
    // Define la clave tipada para una preferencia booleana.
    //  - Evita strings mágicos y te da seguridad de tipos.
    private val KEY_DARK = booleanPreferencesKey("dark_mode")

    // Lectura reactiva del modo oscuro.
    //  - DataStore.data devuelve un Flow<Preferences> que emite cada vez que cambian los datos.
    //  - catch: si hay un IOException (corrupción/lectura), emite preferencias vacías en lugar de fallar.
    //  - map: transforma Preferences -> Boolean, devolviendo 'false' por defecto si no existe la clave.
    val darkMode: Flow<Boolean> =
        context.dataStore.data
            .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { it[KEY_DARK] ?: false }

    // Escritura de la preferencia (transacción).
    //  - 'edit' abre y cierra una transacción atómica sobre el DataStore.
    //  - suspend: se ejecuta fuera del hilo principal.
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK] = enabled }
    }
}
