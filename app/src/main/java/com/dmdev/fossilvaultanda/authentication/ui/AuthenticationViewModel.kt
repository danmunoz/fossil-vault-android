package com.dmdev.fossilvaultanda.authentication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = validateEmail(email),
            errorMessage = null
        )
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = validatePassword(password),
            errorMessage = null
        )
    }
    
    fun switchMode() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isTransitioning = true,
            errorMessage = null,
            emailError = null,
            passwordError = null
        )
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(150) // Brief delay for animation
            _uiState.value = _uiState.value.copy(
                mode = if (currentState.mode == AuthenticationMode.LOGIN) {
                    AuthenticationMode.SIGN_UP
                } else {
                    AuthenticationMode.LOGIN
                },
                isTransitioning = false
            )
        }
    }
    
    fun signIn() {
        val currentState = _uiState.value
        if (!currentState.isFormValid || currentState.isLoading) return
        
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            try {
                authenticationManager.signIn(currentState.email, currentState.password)
                // Success - the authentication state will be updated via the manager
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = mapErrorToUserMessage(error)
                )
            }
        }
    }
    
    fun signUp() {
        val currentState = _uiState.value
        if (!currentState.isFormValid || currentState.isLoading) return
        
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            try {
                authenticationManager.signUp(currentState.email, currentState.password)
                // Success - the authentication state will be updated via the manager
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = mapErrorToUserMessage(error)
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !email.contains("@") -> "Please enter a valid email address"
            else -> null
        }
    }
    
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
    
    private fun mapErrorToUserMessage(error: Exception): String {
        return when {
            error.message?.contains("network", ignoreCase = true) == true -> 
                "Network error. Please try again."
            error.message?.contains("invalid", ignoreCase = true) == true -> 
                "Invalid email or password"
            error.message?.contains("exists", ignoreCase = true) == true -> 
                "An account with this email already exists"
            error.message?.contains("weak", ignoreCase = true) == true -> 
                "Password is too weak"
            else -> "Something went wrong. Please try again."
        }
    }
}