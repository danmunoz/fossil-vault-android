package com.dmdev.fossilvaultanda.data.di

import com.dmdev.fossilvaultanda.data.repository.impl.AuthenticationManagerAdapter
import com.dmdev.fossilvaultanda.data.repository.impl.FirebaseStorageRepository
import com.dmdev.fossilvaultanda.data.repository.impl.FirestoreDataRepository
import com.dmdev.fossilvaultanda.data.repository.impl.MockSubscriptionManager
import com.dmdev.fossilvaultanda.data.repository.interfaces.AuthenticationManager
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import com.dmdev.fossilvaultanda.data.repository.interfaces.ImageStoring
import com.dmdev.fossilvaultanda.data.repository.interfaces.SubscriptionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindDatabaseManaging(
        firestoreDataRepository: FirestoreDataRepository
    ): DatabaseManaging
    
    @Binds
    @Singleton
    abstract fun bindImageStoring(
        firebaseStorageRepository: FirebaseStorageRepository
    ): ImageStoring
    
    @Binds
    @Singleton
    abstract fun bindAuthenticationManager(
        authenticationManagerAdapter: AuthenticationManagerAdapter
    ): AuthenticationManager

    @Binds
    @Singleton
    abstract fun bindSubscriptionManager(
        mockSubscriptionManager: MockSubscriptionManager
    ): SubscriptionManager
}