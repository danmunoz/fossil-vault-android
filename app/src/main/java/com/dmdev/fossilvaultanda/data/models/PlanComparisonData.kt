package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier

/**
 * Data model representing a subscription plan for comparison display.
 *
 * @property tier The subscription tier (FREE, PRO, MAX)
 * @property title Display title for the plan (e.g., "FREE", "PRO", "MAX")
 * @property specimenLimit Maximum number of specimens allowed (null = unlimited)
 * @property photoLimit Maximum number of photos per specimen
 * @property storageLimit Storage limit in bytes
 * @property features List of features with inclusion status
 * @property isCurrentPlan Whether this is the user's current active plan
 * @property canPurchase Whether the user can purchase this plan
 */
data class PlanComparisonData(
    val tier: SubscriptionTier,
    val title: String,
    val specimenLimit: Int?,
    val photoLimit: Int,
    val storageLimit: Long,
    val features: List<PlanFeature>,
    val isCurrentPlan: Boolean,
    val canPurchase: Boolean
)

/**
 * Represents a feature within a subscription plan.
 *
 * @property name The feature name/description
 * @property included Whether this feature is included in the plan
 */
data class PlanFeature(
    val name: String,
    val included: Boolean
)
