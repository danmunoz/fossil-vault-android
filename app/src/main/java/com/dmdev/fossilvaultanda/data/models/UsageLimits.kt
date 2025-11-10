package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import kotlinx.serialization.Serializable

@Serializable
data class UsageLimits(
    val maxSpecimens: Int,
    val maxPhotosPerSpecimen: Int,
    val maxStorageBytes: Long
) {
    companion object {
        fun forTier(tier: SubscriptionTier): UsageLimits {
            return when (tier) {
                SubscriptionTier.FREE -> UsageLimits(
                    maxSpecimens = 15,
                    maxPhotosPerSpecimen = 5,
                    maxStorageBytes = 250L * 1024 * 1024 // 500 MB
                )
                SubscriptionTier.PRO -> UsageLimits(
                    maxSpecimens = Int.MAX_VALUE, // Unlimited
                    maxPhotosPerSpecimen = 10,
                    maxStorageBytes = 5L * 1024 * 1024 * 1024 // 5 GB
                )
                SubscriptionTier.MAX -> UsageLimits(
                    maxSpecimens = Int.MAX_VALUE, // Unlimited
                    maxPhotosPerSpecimen = 20,
                    maxStorageBytes = 15L * 1024 * 1024 * 1024 // 15 GB
                )
            }
        }
    }

    val isUnlimitedSpecimens: Boolean
        get() = maxSpecimens == Int.MAX_VALUE

    fun formatStorageLimit(): String {
        return when {
            maxStorageBytes >= 1024 * 1024 * 1024 -> "${maxStorageBytes / (1024 * 1024 * 1024)} GB"
            maxStorageBytes >= 1024 * 1024 -> "${maxStorageBytes / (1024 * 1024)} MB"
            else -> "${maxStorageBytes / 1024} KB"
        }
    }
}