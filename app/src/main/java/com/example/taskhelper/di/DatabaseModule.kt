package com.example.taskhelper.di

import android.content.Context
import androidx.room.Room
import com.example.taskhelper.data.task.local.AppDatabase
import com.example.taskhelper.data.task.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
Para qué sirve: centraliza la creación de la DB y DAOs, evitando duplicación y controlando su ciclo de vida.

Dónde se usa: repositorios de la capa data (por ejemplo, TaskRepositoryImpl) reciben TaskDao inyectado desde aquí.
 */

@Module // Declara este objeto como contenedor de "bindings" y proveedores para Hilt.
@InstallIn(SingletonComponent::class) // Indica que los proveedores viven con el ciclo de vida de la app (singleton).
object DatabaseModule { // Objeto estático: no se instancia manualmente; Hilt lo procesa.

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase = // Proveedor de la instancia de Room DB (una sola para toda la app).
        Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "taskhelper.db"
        )
            //    La versión con boolean permite indicar si quieres borrar TODAS las tablas (true)
            //    o solo las afectadas por el cambio (false). Para emular el antiguo comportamiento,
            //    usa 'true' (drop de todas las tablas).
            .fallbackToDestructiveMigration(true)
            // (Opcional) Si también quieres comportamiento destructivo en DOWGRADES:
            // .fallbackToDestructiveMigrationOnDowngrade(true)
            .build() // Construye la instancia final que se compartirá como singleton.

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao() // Expone el DAO principal; Room lo genera a partir de la DB.
    // ¿Dónde se usa? Inyectado en repositorios (p. ej., TaskRepositoryImpl) para operaciones CRUD sobre "tasks".
}
