package com.dmdev.fossilvaultanda.ui.screens.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.PlanComparisonData
import com.dmdev.fossilvaultanda.data.models.PlanFeature
import com.dmdev.fossilvaultanda.data.models.UsageLimits
import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import com.dmdev.fossilvaultanda.data.repository.interfaces.SubscriptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import javax.inject.Inject

/**
 * ViewModel for the Plan Comparison screen.
 * Manages subscription plan comparison data and user interactions.
 */
@HiltViewModel
class PlanComparisonViewModel @Inject constructor(
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {

    private val _plans = MutableStateFlow<List<PlanComparisonData>>(emptyList())
    val plans: StateFlow<List<PlanComparisonData>> = _plans.asStateFlow()

    private val _showPaywallPlaceholder = MutableStateFlow(false)
    val showPaywallPlaceholder: StateFlow<Boolean> = _showPaywallPlaceholder.asStateFlow()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            val currentStatus = subscriptionManager.getCurrentSubscriptionStatus()
            val currentTier = currentStatus.tier

            _plans.value = listOf(
                createPlanData(SubscriptionTier.FREE, currentTier),
                createPlanData(SubscriptionTier.PRO, currentTier),
                createPlanData(SubscriptionTier.MAX, currentTier)
            )
        }
    }

    private fun createPlanData(
        tier: SubscriptionTier,
        currentTier: SubscriptionTier
    ): PlanComparisonData {
        val limits = UsageLimits.forTier(tier)
        val isCurrentPlan = tier == currentTier
        val canPurchase = tier != currentTier

        return PlanComparisonData(
            tier = tier,
            title = tier.displayName.uppercase(),
            specimenLimit = if (limits.isUnlimitedSpecimens) null else limits.maxSpecimens,
            photoLimit = limits.maxPhotosPerSpecimen,
            storageLimit = limits.maxStorageBytes,
            features = getFeaturesForTier(tier),
            isCurrentPlan = isCurrentPlan,
            canPurchase = canPurchase
        )
    }

    private fun getFeaturesForTier(tier: SubscriptionTier): List<PlanFeature> {
        return when (tier) {
            SubscriptionTier.FREE -> listOf(
                PlanFeature("Basic collection tracking", included = true),
                PlanFeature("Limited specimens", included = true),
                PlanFeature("Advanced statistics", included = false),
                PlanFeature("Cloud sync", included = false),
                PlanFeature("Export functionality", included = false)
            )
            SubscriptionTier.PRO -> listOf(
                PlanFeature("Advanced collection tracking", included = true),
                PlanFeature("Extended limits", included = true),
                PlanFeature("Advanced statistics", included = true),
                PlanFeature("Cloud sync", included = true),
                PlanFeature("Export functionality", included = true)
            )
            SubscriptionTier.MAX -> listOf(
                PlanFeature("Advanced collection tracking", included = true),
                PlanFeature("Maximum limits", included = true),
                PlanFeature("Advanced statistics", included = true),
                PlanFeature("Cloud sync", included = true),
                PlanFeature("Export functionality", included = true),
                PlanFeature("Premium support", included = true)
            )
        }
    }

    /**
     * Formats storage size from bytes to human-readable format (MB or GB).
     */
    fun formatStorageSize(bytes: Long): String {
        val gb = bytes / 1_073_741_824.0 // 1024^3
        val mb = bytes / 1_048_576.0     // 1024^2

        val formatter = NumberFormat.getInstance().apply {
            maximumFractionDigits = 1
            minimumFractionDigits = 0
        }

        return if (gb >= 1) {
            "${formatter.format(gb)} GB"
        } else {
            "${formatter.format(mb)} MB"
        }
    }

    /**
     * Handles when user taps "See Pricing" button.
     * Currently shows placeholder until RevenueCat integration is complete.
     */
    fun onSeePricingClicked(tier: SubscriptionTier) {
        // TODO: Show RevenueCat paywall when integration is complete
        _showPaywallPlaceholder.value = true
    }

    fun onPaywallDismissed() {
        _showPaywallPlaceholder.value = false
    }
}
