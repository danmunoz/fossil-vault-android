package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the condition/state of a fossil specimen
 * Matches the iOS Condition enum for cross-platform compatibility
 * Uses sealed class to support custom "Other" values
 */
@Serializable
sealed class Condition {
    abstract val displayString: String
    abstract val rawValue: String

    @Serializable
    @SerialName("cast")
    object Cast : Condition() {
        override val displayString = "Cast"
        override val rawValue = "cast"
    }

    @Serializable
    @SerialName("composed")
    object Composed : Condition() {
        override val displayString = "Composed"
        override val rawValue = "composed"
    }

    @Serializable
    @SerialName("enhanced")
    object Enhanced : Condition() {
        override val displayString = "Enhanced"
        override val rawValue = "enhanced"
    }

    @Serializable
    @SerialName("natural")
    object Natural : Condition() {
        override val displayString = "Natural"
        override val rawValue = "natural"
    }

    @Serializable
    @SerialName("reconstructed")
    object Reconstructed : Condition() {
        override val displayString = "Reconstructed"
        override val rawValue = "reconstructed"
    }

    @Serializable
    @SerialName("restored")
    object Restored : Condition() {
        override val displayString = "Restored"
        override val rawValue = "restored"
    }

    @Serializable
    @SerialName("other")
    data class Other(val customValue: String) : Condition() {
        override val displayString = customValue.ifBlank { "Other" }
        override val rawValue = customValue.ifBlank { "other" }
    }

    /**
     * Gets the serialized name for Firebase storage
     */
    val serializedName: String
        get() = when (this) {
            is Cast -> "cast"
            is Composed -> "composed"
            is Enhanced -> "enhanced"
            is Natural -> "natural"
            is Reconstructed -> "reconstructed"
            is Restored -> "restored"
            is Other -> customValue.ifBlank { "other" }
        }

    companion object {
        /**
         * Creates a Condition from a serialized string value
         * Used when reading from Firebase or other data sources
         */
        fun fromSerializedName(name: String?): Condition {
            return when (name?.lowercase()) {
                "cast" -> Cast
                "composed" -> Composed
                "enhanced" -> Enhanced
                "natural" -> Natural
                "reconstructed" -> Reconstructed
                "restored" -> Restored
                null -> Natural // Default fallback
                else -> Other(name) // Custom condition
            }
        }

        /**
         * Gets all predefined conditions for UI pickers (excludes custom Other)
         */
        fun getAllPredefinedCases(): List<Condition> {
            return listOf(
                Cast,
                Composed,
                Enhanced,
                Natural,
                Reconstructed,
                Restored
            )
        }

        /**
         * Gets all conditions including a placeholder "Other" for UI pickers
         */
        fun getAllCasesForUI(): List<Condition> {
            return getAllPredefinedCases() + Other("Other")
        }

        /**
         * Checks if a condition is the "Other" placeholder
         */
        fun isOtherPlaceholder(condition: Condition): Boolean {
            return condition is Other && (condition.customValue == "Other" || condition.customValue.isBlank())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return when {
            this is Other && other is Other -> customValue == other.customValue
            else -> true // Object singletons are equal by type
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is Other -> customValue.hashCode()
            else -> javaClass.hashCode()
        }
    }
}