package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents how a fossil specimen was acquired
 * Matches the iOS AcquisitionMethod enum for cross-platform compatibility
 */
@Serializable
enum class AcquisitionMethod(val displayString: String) {
    @SerialName("found") FOUND("Found"),
    @SerialName("gifted") GIFTED("Gifted"),
    @SerialName("purchased") PURCHASED("Purchased"),
    @SerialName("traded") TRADED("Traded");

    /**
     * Gets the serialized name for Firebase storage (lowercase)
     */
    val serializedName: String
        get() = when (this) {
            FOUND -> "found"
            GIFTED -> "gifted"
            PURCHASED -> "purchased"
            TRADED -> "traded"
        }

    companion object {
        /**
         * Creates an AcquisitionMethod from a serialized string value
         * Used when reading from Firebase or other data sources
         */
        fun fromSerializedName(name: String?): AcquisitionMethod {
            return when (name?.lowercase()) {
                "found" -> FOUND
                "gifted" -> GIFTED
                "purchased" -> PURCHASED
                "traded" -> TRADED
                else -> FOUND // Default fallback
            }
        }

        /**
         * Gets all acquisition methods for UI pickers
         */
        fun getAllCases(): List<AcquisitionMethod> {
            return values().toList()
        }
    }
}