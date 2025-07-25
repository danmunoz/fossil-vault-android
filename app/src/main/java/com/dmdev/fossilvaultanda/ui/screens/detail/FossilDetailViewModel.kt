package com.dmdev.fossilvaultanda.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FossilDetailViewModel @Inject constructor(
    private val databaseManager: DatabaseManaging
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FossilDetailUiState())
    val uiState: StateFlow<FossilDetailUiState> = _uiState.asStateFlow()
    
    fun loadSpecimen(specimenId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val specimen = databaseManager.getSpecimen(specimenId)
                if (specimen != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        specimen = specimen,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Specimen not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    fun setCurrentImageIndex(index: Int) {
        _uiState.value = _uiState.value.copy(currentImageIndex = index)
    }
    
    fun toggleImageFullScreen() {
        _uiState.value = _uiState.value.copy(
            isImageFullScreen = !_uiState.value.isImageFullScreen
        )
    }
    
    fun setImageFullScreen(fullScreen: Boolean) {
        _uiState.value = _uiState.value.copy(isImageFullScreen = fullScreen)
    }
    
    fun toggleOverflowMenu() {
        _uiState.value = _uiState.value.copy(
            showOverflowMenu = !_uiState.value.showOverflowMenu
        )
    }
    
    fun setOverflowMenuVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showOverflowMenu = visible)
    }
    
    fun toggleBottomSheet() {
        _uiState.value = _uiState.value.copy(
            showBottomSheet = !_uiState.value.showBottomSheet
        )
    }
    
    fun setBottomSheetVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showBottomSheet = visible)
    }
    
    fun saveScrollPosition(position: Int) {
        _uiState.value = _uiState.value.copy(savedScrollPosition = position)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}