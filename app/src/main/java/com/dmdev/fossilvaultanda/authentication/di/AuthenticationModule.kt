package com.dmdev.fossilvaultanda.authentication.di

import com.dmdev.fossilvaultanda.authentication.data.AndroidFBAuthentication
import com.dmdev.fossilvaultanda.authentication.domain.Authenticable
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideAuthenticable(
        firebaseAuth: FirebaseAuth
    ): Authenticable = AndroidFBAuthentication(firebaseAuth)
}