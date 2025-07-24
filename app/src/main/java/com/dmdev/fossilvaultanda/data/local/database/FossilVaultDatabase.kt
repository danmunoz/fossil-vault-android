package com.dmdev.fossilvaultanda.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.dmdev.fossilvaultanda.data.local.dao.SpecimenDao
import com.dmdev.fossilvaultanda.data.local.dao.TagDao
import com.dmdev.fossilvaultanda.data.local.dao.UserProfileDao
import com.dmdev.fossilvaultanda.data.local.entities.SpecimenEntity
import com.dmdev.fossilvaultanda.data.local.entities.TagEntity
import com.dmdev.fossilvaultanda.data.local.entities.UserProfileEntity

@Database(
    entities = [
        SpecimenEntity::class,
        TagEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FossilVaultDatabase : RoomDatabase() {
    abstract fun specimenDao(): SpecimenDao
    abstract fun tagDao(): TagDao
    abstract fun userProfileDao(): UserProfileDao
    
    companion object {
        const val DATABASE_NAME = "fossil_vault_database"
        
        @Volatile
        private var INSTANCE: FossilVaultDatabase? = null
        
        fun getDatabase(context: Context): FossilVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FossilVaultDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}