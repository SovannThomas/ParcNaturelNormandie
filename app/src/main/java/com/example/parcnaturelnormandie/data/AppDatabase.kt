package com.example.parcnaturelnormandie.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CartItemEntity::class],
    version = 2, // Incrémentation de la version pour refléter le changement de schéma
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parc_normandie.db"
                )
                .fallbackToDestructiveMigration() // Ajout pour éviter le crash si migration manquante
                .build().also { INSTANCE = it }
            }
    }
}