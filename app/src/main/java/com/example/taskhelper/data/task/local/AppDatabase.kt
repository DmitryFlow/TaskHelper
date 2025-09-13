package com.example.taskhelper.data.task.local

import androidx.room.Database
import androidx.room.RoomDatabase

// @Database: anota esta clase como definición de la base de datos de Room.
// - entities = [TaskEntity::class]  -> tablas que componen el esquema (aquí solo "tasks").
// - version = 1                     -> versión del esquema. Si cambias el modelo, debes subirla
//
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Room genera en compile-time una implementación concreta de esta clase abstracta.
    // No se instancia directamente: se crea con Room.databaseBuilder(...) (ver AppModule).

    // Exponer el DAO: Room rellena este mét,odo con la implementación real.
    // ¿Dónde se usa?
    // - En Hilt (AppModule.provideTaskDao) para inyectar TaskDao en repositorios.
    // - En tests locales puedes obtener el DAO desde una DB in-memory.
    abstract fun taskDao(): TaskDao
}
