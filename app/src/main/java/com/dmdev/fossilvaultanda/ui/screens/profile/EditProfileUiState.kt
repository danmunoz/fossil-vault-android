package com.dmdev.fossilvaultanda.ui.screens.profile

data class EditProfileUiState(
    val email: String = "",
    val name: String = "",
    val location: String = "",
    val bio: String = "",
    val profileImageUrl: String? = null,
    val originalName: String = "",
    val originalLocation: String = "",
    val originalBio: String = "",
    val originalProfileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val hasChanges: Boolean
        get() = name != originalName || 
                location != originalLocation || 
                bio != originalBio ||
                profileImageUrl != originalProfileImageUrl
}