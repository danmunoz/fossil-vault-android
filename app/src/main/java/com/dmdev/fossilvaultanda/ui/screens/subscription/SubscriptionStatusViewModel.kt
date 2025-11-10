package com.dmdev.fossilvaultanda.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.UsageLimits
import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import com.dmdev.fossilvaultanda.data.repository.interfaces.SubscriptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

/**
 * ViewModel for the Subscription Status screen.
 * Manages current subscription tier, renewal info, and usage statistics.
 */
@HiltViewModel
class SubscriptionStatusViewModel @Inject constructor(
    private val subscriptionManager: SubscriptionManager,
    private val databaseManager: DatabaseManaging
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionStatusUiState())
    val uiState: StateFlow<SubscriptionStatusUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptionData()
    }

    /**
     * Loads subscription data and usage statistics.
     */
    fun loadSubscriptionData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Get subscription status
                val status = subscriptionManager.getCurrentSubscriptionStatus()
                val tier = status.tier
                val renewalDate = status.expirationDate

                // Get usage statistics
                val specimenCount = databaseManager.getSpecimenCount()
                val storageUsage = databaseManager.getStorageUsage()

                // Get limits for current tier
                val limits = UsageLimits.forTier(tier)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tier = tier,
                    renewalDate = renewalDate,
                    currentSpecimenCount = specimenCount,
                    specimenLimit = if (limits.isUnlimitedSpecimens) null else limits.maxSpecimens,
                    currentStorageUsage = storageUsage,
                    storageLimit = limits.maxStorageBytes
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Refreshes usage data (specimens and storage).
     */
    fun refreshUsageData() {
        loadSubscriptionData()
    }

    /**
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Calculates the percentage of specimen limit used.
     */
    fun getSpecimenUsagePercentage(): Float {
        val state = _uiState.value
        val limit = state.specimenLimit ?: return 0f
        if (limit == 0) return 0f
        return (state.currentSpecimenCount.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
    }

    /**
     * Calculates the percentage of storage limit used.
     */
    fun getStorageUsagePercentage(): Float {
        val state = _uiState.value
        if (state.storageLimit == 0L) return 0f
        return (state.currentStorageUsage.toFloat() / state.storageLimit.toFloat()).coerceIn(0f, 1f)
    }
}

/**
 * UI state for the Subscription Status screen.
 */
data class SubscriptionStatusUiState(
    val isLoading: Boolean = false,
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val renewalDate: Instant? = null,
    val currentSpecimenCount: Int = 0,
    val specimenLimit: Int? = null,
    val currentStorageUsage: Long = 0L,
    val storageLimit: Long = 0L,
    val error: String? = null
)
