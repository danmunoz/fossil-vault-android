package com.dmdev.fossilvaultanda.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.data.models.UserProfile
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val databaseManager: DatabaseManaging
) : ViewModel() {
    
    val userProfile: StateFlow<UserProfile?> = databaseManager.profile
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, null)
    val authenticationState: StateFlow<AuthenticationState> = authenticationManager.authenticationState
    
    init {
        // Debug logging to monitor profile changes
        viewModelScope.launch {
            userProfile.collect { profile ->
                android.util.Log.d("SettingsViewModel", "Profile updated: $profile")
            }
        }
        viewModelScope.launch {
            authenticationState.collect { state ->
                android.util.Log.d("SettingsViewModel", "Auth state updated: $state")
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                authenticationManager.signOut()
            } catch (e: Exception) {
                // TODO: Handle sign out error
            }
        }
    }
    
    fun deleteAccount() {
        viewModelScope.launch {
            try {
                authenticationManager.deleteAccount()
            } catch (e: Exception) {
                // TODO: Handle delete account error
            }
        }
    }
}