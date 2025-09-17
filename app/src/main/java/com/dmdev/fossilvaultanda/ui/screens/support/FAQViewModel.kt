package com.dmdev.fossilvaultanda.ui.screens.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.FaqItem
import com.dmdev.fossilvaultanda.data.repository.FaqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor(
    private val faqRepository: FaqRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FAQUiState())
    val uiState: StateFlow<FAQUiState> = _uiState.asStateFlow()

    fun loadFaqItems() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val faqItems = faqRepository.getFaqItems()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        faqItems = faqItems
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        faqItems = emptyList()
                    )
                }
            }
        }
    }

    fun toggleItemExpanded(question: String) {
        _uiState.update { currentState ->
            val expandedItems = currentState.expandedItems.toMutableSet()
            if (expandedItems.contains(question)) {
                expandedItems.remove(question)
            } else {
                expandedItems.add(question)
            }
            currentState.copy(expandedItems = expandedItems)
        }
    }
}

data class FAQUiState(
    val isLoading: Boolean = false,
    val faqItems: List<FaqItem> = emptyList(),
    val expandedItems: Set<String> = emptySet()
)