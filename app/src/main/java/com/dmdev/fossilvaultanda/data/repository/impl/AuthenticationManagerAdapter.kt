package com.dmdev.fossilvaultanda.data.repository.impl

import android.util.Log
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager as ExistingAuthManager
import com.dmdev.fossilvaultanda.data.repository.interfaces.AuthenticationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationManagerAdapter @Inject constructor(
    private val existingAuthManager: ExistingAuthManager
) : AuthenticationManager {
    
    override val isAuthenticated: Flow<Boolean> = existingAuthManager.isAuthenticated
    
    override suspend fun getCurrentUserId(): String? {
        val userId = existingAuthManager.profile.value?.userId
        Log.d("AuthAdapter", "getCurrentUserId: $userId")
        return userId
    }
}