package com.dmdev.fossilvaultanda.authentication.ui

import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationMode

data class AuthenticationUiState(
    val mode: AuthenticationMode = AuthenticationMode.SIGN_UP,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTransitioning: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && 
                email.contains("@") && 
                password.length >= 6 &&
                emailError == null &&
                passwordError == null
}