package com.dmdev.fossilvaultanda.data.repository.impl

import com.dmdev.fossilvaultanda.data.models.SubscriptionStatus
import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import com.dmdev.fossilvaultanda.data.repository.interfaces.SubscriptionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockSubscriptionManager @Inject constructor() : SubscriptionManager {

    private val _subscriptionStatus = MutableStateFlow(SubscriptionStatus.free())
    override val subscriptionStatus: Flow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()

    override suspend fun getCurrentSubscriptionStatus(): SubscriptionStatus {
        return _subscriptionStatus.value
    }

    override suspend fun hasAccessToTier(tier: SubscriptionTier): Boolean {
        val currentTier = _subscriptionStatus.value.tier
        return when {
            tier == SubscriptionTier.FREE -> true
            tier == SubscriptionTier.PRO -> currentTier == SubscriptionTier.PRO || currentTier == SubscriptionTier.MAX
            tier == SubscriptionTier.MAX -> currentTier == SubscriptionTier.MAX
            else -> false
        }
    }

    override suspend fun canAddSpecimen(currentCount: Int): Boolean {
        val status = getCurrentSubscriptionStatus()
        if (!status.isValid) return false

        val limits = status.usageLimits
        return if (limits.isUnlimitedSpecimens) {
            true
        } else {
            currentCount < limits.maxSpecimens
        }
    }

    override suspend fun canAddPhotos(currentPhotoCount: Int): Boolean {
        val status = getCurrentSubscriptionStatus()
        if (!status.isValid) return false

        return currentPhotoCount < status.usageLimits.maxPhotosPerSpecimen
    }

    override suspend fun hasStorageSpace(requiredBytes: Long, currentUsageBytes: Long): Boolean {
        val status = getCurrentSubscriptionStatus()
        if (!status.isValid) return false

        return (currentUsageBytes + requiredBytes) <= status.usageLimits.maxStorageBytes
    }

    override suspend fun initialize(apiKey: String) {
        // Mock implementation - no initialization needed for free tier
    }

    override suspend fun purchaseSubscription(tier: SubscriptionTier): Result<SubscriptionStatus> {
        // Mock implementation - always returns free tier
        return Result.success(_subscriptionStatus.value)
    }

    override suspend fun restorePurchases(): Result<SubscriptionStatus> {
        // Mock implementation - returns current status
        return Result.success(_subscriptionStatus.value)
    }

    override suspend fun refreshSubscriptionStatus(): SubscriptionStatus {
        // Mock implementation - returns current free status
        return _subscriptionStatus.value
    }

    // For testing purposes - allow changing subscription tier
    fun setSubscriptionTier(tier: SubscriptionTier) {
        _subscriptionStatus.value = SubscriptionStatus(
            tier = tier,
            isActive = true,
            expirationDate = null,
            willRenew = false
        )
    }
}