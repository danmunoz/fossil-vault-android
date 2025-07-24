package com.dmdev.fossilvaultanda.authentication.domain

enum class AuthenticationState {
    UNAUTHENTICATED,
    AUTHENTICATING,
    AUTHENTICATED,
    LOCAL_USER
}