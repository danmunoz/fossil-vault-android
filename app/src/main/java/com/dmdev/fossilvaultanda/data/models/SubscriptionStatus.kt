package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionStatus(
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val isActive: Boolean = true,
    val expirationDate: Instant? = null,
    val willRenew: Boolean = false,
    val revenueCatCustomerId: String? = null,
    val productIdentifier: String? = null
) {
    val usageLimits: UsageLimits
        get() = UsageLimits.forTier(tier)

    val isExpired: Boolean
        get() = expirationDate?.let { it < kotlinx.datetime.Clock.System.now() } ?: false

    val isValid: Boolean
        get() = isActive && !isExpired

    companion object {
        fun free(): SubscriptionStatus {
            return SubscriptionStatus(
                tier = SubscriptionTier.FREE,
                isActive = true,
                expirationDate = null,
                willRenew = false
            )
        }
    }
}