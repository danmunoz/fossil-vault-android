package com.dmdev.fossilvaultanda.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager
import com.dmdev.fossilvaultanda.data.models.UserProfile
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val databaseManager: DatabaseManaging
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    
    private val userProfile: StateFlow<UserProfile?> = databaseManager.profile
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, null)
    
    init {
        loadProfile()
    }
    
    private fun loadProfile() {
        viewModelScope.launch {
            userProfile.collect { profile ->
                profile?.let {
                    _uiState.value = _uiState.value.copy(
                        email = it.email,
                        name = it.fullName ?: "",
                        location = it.location ?: "",
                        bio = it.bio ?: "",
                        profileImageUrl = it.picture?.url,
                        originalName = it.fullName ?: "",
                        originalLocation = it.location ?: "",
                        originalBio = it.bio ?: "",
                        originalProfileImageUrl = it.picture?.url
                    )
                }
            }
        }
    }
    
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }
    
    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }
    
    fun updateBio(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }
    
    fun selectProfileImage() {
        // TODO: Implement image selection
        // This would typically use ActivityResultLauncher for image picking
    }
    
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Get current profile from repository
                val currentProfile = userProfile.value
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No profile found to update"
                    )
                    return@launch
                }
                
                // Create updated profile with new data
                val currentState = _uiState.value
                val updatedProfile = currentProfile.copy(
                    fullName = if (currentState.name.isBlank()) null else currentState.name,
                    location = if (currentState.location.isBlank()) null else currentState.location,
                    bio = if (currentState.bio.isBlank()) null else currentState.bio
                )
                
                // Save to repository
                databaseManager.updateProfile(updatedProfile)
                
                // Update the original values to reflect the saved state
                _uiState.value = currentState.copy(
                    originalName = currentState.name,
                    originalLocation = currentState.location,
                    originalBio = currentState.bio,
                    originalProfileImageUrl = currentState.profileImageUrl,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save profile"
                )
            }
        }
    }
}