package com.dmdev.fossilvaultanda.data.di

import android.content.Context
import androidx.room.Room
import com.dmdev.fossilvaultanda.data.local.dao.SpecimenDao
import com.dmdev.fossilvaultanda.data.local.dao.TagDao
import com.dmdev.fossilvaultanda.data.local.dao.UserProfileDao
import com.dmdev.fossilvaultanda.data.local.database.FossilVaultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideFossilVaultDatabase(
        @ApplicationContext context: Context
    ): FossilVaultDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FossilVaultDatabase::class.java,
            FossilVaultDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    fun provideSpecimenDao(database: FossilVaultDatabase): SpecimenDao {
        return database.specimenDao()
    }
    
    @Provides
    fun provideTagDao(database: FossilVaultDatabase): TagDao {
        return database.tagDao()
    }
    
    @Provides
    fun provideUserProfileDao(database: FossilVaultDatabase): UserProfileDao {
        return database.userProfileDao()
    }
}