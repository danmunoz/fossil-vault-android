package com.dmdev.fossilvaultanda.authentication.domain

data class UserProfile(
    val userId: String,
    val email: String,
    val fullName: String? = null,
    val username: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val isPublic: Boolean = false,
    val pictureUrl: String? = null
)