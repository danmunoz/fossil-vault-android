package com.dmdev.fossilvaultanda.data.models

import android.util.Patterns
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val fullName: String? = null,
    val username: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val isPublic: Boolean = false,
    val picture: StoredImage? = null,
    val settings: AppSettings = AppSettings()
) {
    fun validate(): Result<Unit> {
        return when {
            userId.isBlank() -> Result.failure(DataException.ValidationException("User ID cannot be blank"))
            email.isBlank() -> Result.failure(DataException.ValidationException("Email cannot be blank"))
            !isValidEmail(email) -> Result.failure(DataException.ValidationException("Invalid email format"))
            else -> Result.success(Unit)
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}