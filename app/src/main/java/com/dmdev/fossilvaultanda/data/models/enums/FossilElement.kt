package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FossilElement(val displayString: String) {
    @SerialName("tooth") TOOTH("Tooth"),
    @SerialName("jaw") JAW("Jaw"),
    @SerialName("skull") SKULL("Skull"),
    @SerialName("bone") BONE("Bone"),
    @SerialName("claw") CLAW("Claw"),
    @SerialName("horn") HORN("Horn"),
    @SerialName("rib") RIB("Rib"),
    @SerialName("vertebra") VERTEBRA("Vertebra"),
    @SerialName("shell") SHELL("Shell"),
    @SerialName("ammonite") AMMONITE("Ammonite"),
    @SerialName("matrix") MATRIX("Matrix"),
    @SerialName("coprolite") COPROLITE("Coprolite"),
    @SerialName("imprint") IMPRINT("Imprint"),
    @SerialName("track") TRACK("Track"),
    @SerialName("egg") EGG("Egg"),
    @SerialName("other") OTHER("Other");
    
    /**
     * Gets the serialized name for Firebase storage (lowercase)
     */
    val serializedName: String
        get() = when (this) {
            TOOTH -> "tooth"
            JAW -> "jaw"
            SKULL -> "skull"
            BONE -> "bone"
            CLAW -> "claw"
            HORN -> "horn"
            RIB -> "rib"
            VERTEBRA -> "vertebra"
            SHELL -> "shell"
            AMMONITE -> "ammonite"
            MATRIX -> "matrix"
            COPROLITE -> "coprolite"
            IMPRINT -> "imprint"
            TRACK -> "track"
            EGG -> "egg"
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