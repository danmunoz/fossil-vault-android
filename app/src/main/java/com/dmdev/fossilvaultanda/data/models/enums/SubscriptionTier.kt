package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class SubscriptionTier(
    val displayName: String,
    val serializedName: String
) {
    FREE("Free", "free"),
    PRO("Pro", "pro"),
    MAX("Max", "max");

    companion object {
        fun fromSerializedName(name: String): SubscriptionTier {
            return entries.find { it.serializedName == name } ?: FREE
        }
    }
}