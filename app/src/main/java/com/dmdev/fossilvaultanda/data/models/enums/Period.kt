package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Period(val displayName: String) {
    @SerialName("precambrian") PRECAMBRIAN("Precambrian"),
    @SerialName("cambrian") CAMBRIAN("Cambrian"),
    @SerialName("ordovician") ORDOVICIAN("Ordovician"),
    @SerialName("silurian") SILURIAN("Silurian"),
    @SerialName("devonian") DEVONIAN("Devonian"),
    @SerialName("carboniferous") CARBONIFEROUS("Carboniferous"),
    @SerialName("mississippian") MISSISSIPPIAN("Mississippian"),
    @SerialName("pennsylvanian") PENNSYLVANIAN("Pennsylvanian"),
    @SerialName("permian") PERMIAN("Permian"),
    @SerialName("triassic") TRIASSIC("Triassic"),
    @SerialName("jurassic") JURASSIC("Jurassic"),
    @SerialName("cretaceous") CRETACEOUS("Cretaceous"),
    @SerialName("paleocene") PALEOCENE("Paleogene"),
    @SerialName("neogene") NEOGENE("Neogene"),
    @SerialName("quaternary") QUATERNARY("Quaternary"),
    @SerialName("unknown") UNKNOWN("Unknown");
    
    /**
     * Gets the serialized name for Firebase storage (lowercase)
     */
    val serializedName: String
        get() = when (this) {
            PRECAMBRIAN -> "precambrian"
            CAMBRIAN -> "cambrian"
            ORDOVICIAN -> "ordovician"
            SILURIAN -> "silurian"
            DEVONIAN -> "devonian"
            CARBONIFEROUS -> "carboniferous"
            MISSISSIPPIAN -> "mississippian"
            PENNSYLVANIAN -> "pennsylvanian"
            PERMIAN -> "permian"
            TRIASSIC -> "triassic"
            JURASSIC -> "jurassic"
            CRETACEOUS -> "cretaceous"
            PALEOCENE -> "paleocene"
            NEOGENE -> "neogene"
            QUATERNARY -> "quaternary"
            UNKNOWN -> "unknown"
        }
    
    companion object {
        fun getAllCases(divideCarboniferous: Boolean): List<Period> {
            val base = listOf(
                PRECAMBRIAN, CAMBRIAN, ORDOVICIAN, SILURIAN, DEVONIAN
            )
            val carboniferous = if (divideCarboniferous) {
                listOf(MISSISSIPPIAN, PENNSYLVANIAN)
            } else {
                listOf(CARBONIFEROUS)
            }
            val remaining = listOf(
                PERMIAN, TRIASSIC, JURASSIC, CRETACEOUS, 
                PALEOCENE, NEOGENE, QUATERNARY, UNKNOWN
            )
            return base + carboniferous + remaining
        }
        
        /**
         * Parse from serialized name (case-insensitive for backwards compatibility)
         */
        fun fromSerializedName(name: String?): Period {
            if (name == null) return UNKNOWN
            return values().find { 
                it.serializedName.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true) ||
                it.displayName.equals(name, ignoreCase = true)
            } ?: UNKNOWN
        }
    }
}