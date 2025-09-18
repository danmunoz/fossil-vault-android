package com.dmdev.fossilvaultanda.data.repository.interfaces

import com.dmdev.fossilvaultanda.data.models.SubscriptionStatus
import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import kotlinx.coroutines.flow.Flow

interface SubscriptionManager {

    /**
     * Current subscription status for the user
     */
    val subscriptionStatus: Flow<SubscriptionStatus>

    /**
     * Get the current subscription status
     */
    suspend fun getCurrentSubscriptionStatus(): SubscriptionStatus

    /**
     * Check if user has access to a specific tier
     */
    suspend fun hasAccessToTier(tier: SubscriptionTier): Boolean

    /**
     * Check if user can perform an action based on usage limits
     */
    suspend fun canAddSpecimen(currentCount: Int): Boolean

    /**
     * Check if user can add photos to a specimen
     */
    suspend fun canAddPhotos(currentPhotoCount: Int): Boolean

    /**
     * Check if user has storage space available
     */
    suspend fun hasStorageSpace(requiredBytes: Long, currentUsageBytes: Long): Boolean

    /**
     * Initialize subscription manager (for RevenueCat setup)
     */
    suspend fun initialize(apiKey: String)

    /**
     * Purchase a subscription tier (for future RevenueCat integration)
     */
    suspend fun purchaseSubscription(tier: SubscriptionTier): Result<SubscriptionStatus>

    /**
     * Restore purchases (for RevenueCat)
     */
    suspend fun restorePurchases(): Result<SubscriptionStatus>

    /**
     * Refresh subscription status from server
     */
    suspend fun refreshSubscriptionStatus(): SubscriptionStatus
}