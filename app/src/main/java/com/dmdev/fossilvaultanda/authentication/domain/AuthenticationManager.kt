package com.dmdev.fossilvaultanda.authentication.domain

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationManager @Inject constructor(
    private val authenticationProvider: Authenticable
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    val isAuthenticated: StateFlow<Boolean> = authenticationProvider.authenticationState
        .map { state -> 
            state == AuthenticationState.LOCAL_USER || state == AuthenticationState.AUTHENTICATED 
        }
        .stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, false)
    
    val profile: StateFlow<UserProfile?> = authenticationProvider.profileSubject
    val authenticationState: StateFlow<AuthenticationState> = authenticationProvider.authenticationState
    
    suspend fun signIn(email: String, password: String) {
        authenticationProvider.signInWith(email, password)
    }
    
    suspend fun signUp(email: String, password: String) {
        authenticationProvider.signUpWith(email, password)
    }
    
    fun signOut() {
        authenticationProvider.signOut()
    }
    
    suspend fun deleteAccount() {
        authenticationProvider.deleteAccount()
    }
    
    suspend fun anonymousSignIn() {
        authenticationProvider.signInAsLocalUser()
    }
}