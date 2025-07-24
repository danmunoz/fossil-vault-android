package com.dmdev.fossilvaultanda.authentication.domain

import kotlinx.coroutines.flow.StateFlow

interface Authenticable {
    val profileSubject: StateFlow<UserProfile?>
    val authenticationState: StateFlow<AuthenticationState>
    
    suspend fun signInWith(email: String, password: String)
    suspend fun signUpWith(email: String, password: String)
    fun signOut()
    suspend fun deleteAccount()
    
    suspend fun signInAsLocalUser()
    fun isLocalUser(): Boolean
    
    fun updateProfile(id: String, email: String, username: String, location: String)
}