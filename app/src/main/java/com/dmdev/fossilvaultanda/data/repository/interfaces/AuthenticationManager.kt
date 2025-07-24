package com.dmdev.fossilvaultanda.data.repository.interfaces

import kotlinx.coroutines.flow.Flow

interface AuthenticationManager {
    val isAuthenticated: Flow<Boolean>
    suspend fun getCurrentUserId(): String?
}