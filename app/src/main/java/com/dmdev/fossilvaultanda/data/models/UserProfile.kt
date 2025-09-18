package com.dmdev.fossilvaultanda.data.models

import android.util.Patterns
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val fullName: String? = null,
    val username: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val isPublic: Boolean = false,
    val picture: StoredImage? = null,
    val settings: AppSettings = AppSettings(),
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus.free(),
    val specimenCount: Int = 0,
    val storageUsageBytes: Long = 0L
) {
    fun validate(): Result<Unit> {
        return when {
            userId.isBlank() -> Result.failure(DataException.ValidationException("User ID cannot be blank"))
            email.isBlank() -> Result.failure(DataException.ValidationException("Email cannot be blank"))
            !isValidEmail(email) -> Result.failure(DataException.ValidationException("Invalid email format"))
            else -> Result.success(Unit)
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Helper properties for subscription management
     */
    val canAddSpecimen: Boolean
        get() = if (subscriptionStatus.usageLimits.isUnlimitedSpecimens) {
            true
        } else {
            specimenCount < subscriptionStatus.usageLimits.maxSpecimens
        }

    val remainingSpecimenSlots: Int
        get() = if (subscriptionStatus.usageLimits.isUnlimitedSpecimens) {
            Int.MAX_VALUE
        } else {
            maxOf(0, subscriptionStatus.usageLimits.maxSpecimens - specimenCount)
        }

    val storageUsagePercentage: Float
        get() {
            val maxStorage = subscriptionStatus.usageLimits.maxStorageBytes
            return if (maxStorage > 0) {
                (storageUsageBytes.toFloat() / maxStorage.toFloat()).coerceIn(0f, 1f)
            } else 0f
        }

    /**
     * Converts the UserProfile to a Map for Firebase storage with proper enum serialization
     */
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "fullName" to fullName,
            "username" to username,
            "location" to location,
            "bio" to bio,
            "isPublic" to isPublic,
            "picture" to picture?.let { mapOf("url" to it.url, "path" to it.path) },
            "settings" to mapOf(
                "unit" to settings.unit.serializedName,
                "divideCarboniferous" to settings.divideCarboniferous,
                "defaultCurrency" to settings.defaultCurrency.serializedName
            ),
            "subscriptionStatus" to mapOf(
                "tier" to subscriptionStatus.tier.serializedName,
                "isActive" to subscriptionStatus.isActive,
                "expirationDate" to subscriptionStatus.expirationDate?.toString(),
                "willRenew" to subscriptionStatus.willRenew,
                "revenueCatCustomerId" to subscriptionStatus.revenueCatCustomerId,
                "productIdentifier" to subscriptionStatus.productIdentifier
            ),
            "specimenCount" to specimenCount,
            "storageUsageBytes" to storageUsageBytes
        )
    }
}