package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FossilElement(val displayString: String) {
    @SerialName("tooth") TOOTH("Tooth"),
    @SerialName("jaw") JAW("Jaw"),
    @SerialName("skull") SKULL("Skull"),
    @SerialName("skeleton") SKELETON("Skeleton"),
    @SerialName("bone") BONE("Bone"),
    @SerialName("claw") CLAW("Claw"),
    @SerialName("shell") SHELL("Shell"),
    @SerialName("trilobite") TRILOBITE("Trilobite"),
    @SerialName("matrix") MATRIX("Matrix"),
    @SerialName("imprint") IMPRINT("Imprint"),
    @SerialName("egg") EGG("Egg"),
    @SerialName("urchin") URCHIN("Urchin"),
    @SerialName("ichnofossil") ICHNOFOSSIL("Ichnofossil"),
    @SerialName("other") OTHER("Other");
    
    /**
     * Gets the serialized name for Firebase storage (lowercase)
     */
    val serializedName: String
        get() = when (this) {
            TOOTH -> "tooth"
            JAW -> "jaw"
            SKULL -> "skull"
            SKELETON -> "skeleton"
            BONE -> "bone"
            CLAW -> "claw"
            SHELL -> "shell"
            TRILOBITE -> "trilobite"
            MATRIX -> "matrix"
            IMPRINT -> "imprint"
            EGG -> "egg"
            URCHIN -> "urchin"
            ICHNOFOSSIL -> "ichnofossil"
            OTHER -> "other"
        }
    
    companion object {
        /**
         * Parse from serialized name (case-insensitive for backwards compatibility)
         */
        fun fromSerializedName(name: String?): FossilElement {
            if (name == null) return OTHER
            return values().find { 
                it.serializedName.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true) ||
                it.displayString.equals(name, ignoreCase = true)
            } ?: OTHER
        }
    }
}