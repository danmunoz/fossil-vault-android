package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SizeUnit(val symbol: String, val displayName: String) {
    @SerialName("mm") MM("mm", "Millimeters"),
    @SerialName("cm") CM("cm", "Centimeters"),
    @SerialName("inch") INCH("in", "Inches");
    
    /**
     * Gets the serialized name for Firebase storage (lowercase)
     */
    val serializedName: String
        get() = when (this) {
            MM -> "mm"
            CM -> "cm"
            INCH -> "inch"
        }
    
    companion object {
        val default = MM
        
        /**
         * Parse from serialized name (case-insensitive for backwards compatibility)
         */
        fun fromSerializedName(name: String?): SizeUnit {
            if (name == null) return default
            return values().find { 
                it.serializedName.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true)
            } ?: default
        }
    }
}