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
    }
}