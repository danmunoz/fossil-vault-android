package com.fossilVault.geological

import androidx.compose.ui.graphics.Color

enum class GeologicalPeriod(
    val displayName: String,
    val startMya: Double,
    val endMya: Double,
    val color: Color
) {
    QUATERNARY("Quaternary", 0.0, 2.58, Color(0xFFF9F871)),
    NEOGENE("Neogene", 2.58, 23.03, Color(0xFFFFE619)),
    PALEOGENE("Paleogene", 23.03, 66.0, Color(0xFFFD9A52)),
    CRETACEOUS("Cretaceous", 66.0, 145.0, Color(0xFF7FC64E)),
    JURASSIC("Jurassic", 145.0, 201.3, Color(0xFF34B2C9)),
    TRIASSIC("Triassic", 201.3, 251.9, Color(0xFF812B92)),
    PERMIAN("Permian", 251.9, 298.9, Color(0xFFF04028)),
    CARBONIFEROUS("Carboniferous", 298.9, 358.9, Color(0xFF67A599)),
    PENNSYLVANIAN("Pennsylvanian", 298.9, 323.4, Color(0xFF99C3B5)),
    MISSISSIPPIAN("Mississippian", 323.4, 358.9, Color(0xFF678E7E)),
    DEVONIAN("Devonian", 358.9, 419.2, Color(0xFFCB8C37)),
    SILURIAN("Silurian", 419.2, 443.8, Color(0xFFB3E1B6)),
    ORDOVICIAN("Ordovician", 443.8, 485.4, Color(0xFF009270)),
    CAMBRIAN("Cambrian", 485.4, 541.0, Color(0xFF7FA056)),
    NEO_PROTEROZOIC("Neo-Proterozoic", 538.8, 1000.0, Color(0xFFFEB343)),
    MESO_PROTEROZOIC("Meso-Proterozoic", 1000.0, 1600.0, Color(0xFFFDB462)),
    PALEO_PROTEROZOIC("Paleo-Proterozoic", 1600.0, 2500.0, Color(0xFFF74370)),
    NEO_ARCHEAN("Neo-Archean", 2500.0, 2800.0, Color(0xFFFBA8C5)),
    MESO_ARCHEAN("Meso-Archean", 2800.0, 3200.0, Color(0xFFFCB4D0)),
    PALEO_ARCHEAN("Paleo-Archean", 3200.0, 3600.0, Color(0xFFF99BCB)),
    EO_ARCHEAN("Eo-Archean", 3600.0, 4031.0, Color(0xFFDA256E));
    
    val timeRange: String
        get() = "$startMya - $endMya Mya"

    /**
     * Serialized name in camelCase format for Firebase/iOS compatibility
     * Converts SCREAMING_SNAKE_CASE to camelCase
     * Example: NEO_PROTEROZOIC -> neoProterozoic
     */
    val serializedName: String
        get() {
            val parts = name.split("_")
            return parts.mapIndexed { index, part ->
                if (index == 0) {
                    part.lowercase()
                } else {
                    part.lowercase().replaceFirstChar { it.uppercase() }
                }
            }.joinToString("")
        }

    val era: GeologicalEra
        get() = when (this) {
            QUATERNARY, NEOGENE, PALEOGENE -> GeologicalEra.CENOZOIC
            CRETACEOUS, JURASSIC, TRIASSIC -> GeologicalEra.MESOZOIC
            PERMIAN, CARBONIFEROUS, PENNSYLVANIAN, MISSISSIPPIAN, DEVONIAN, SILURIAN, ORDOVICIAN, CAMBRIAN -> GeologicalEra.PALEOZOIC
            NEO_PROTEROZOIC, MESO_PROTEROZOIC, PALEO_PROTEROZOIC -> GeologicalEra.PROTEROZOIC
            NEO_ARCHEAN, MESO_ARCHEAN, PALEO_ARCHEAN, EO_ARCHEAN -> GeologicalEra.ARCHEAN
        }
    
    val epochs: List<GeologicalEpoch>
        get() = when (this) {
            QUATERNARY -> listOf(GeologicalEpoch.HOLOCENE, GeologicalEpoch.PLEISTOCENE)
            NEOGENE -> listOf(GeologicalEpoch.PLIOCENE, GeologicalEpoch.MIOCENE)
            PALEOGENE -> listOf(GeologicalEpoch.OLIGOCENE, GeologicalEpoch.EOCENE, GeologicalEpoch.PALEOCENE)
            CRETACEOUS -> listOf(GeologicalEpoch.LATE_CRETACEOUS, GeologicalEpoch.EARLY_CRETACEOUS)
            JURASSIC -> listOf(GeologicalEpoch.LATE_JURASSIC, GeologicalEpoch.MIDDLE_JURASSIC, GeologicalEpoch.EARLY_JURASSIC)
            TRIASSIC -> listOf(GeologicalEpoch.LATE_TRIASSIC, GeologicalEpoch.MIDDLE_TRIASSIC, GeologicalEpoch.EARLY_TRIASSIC)
            PERMIAN -> listOf(GeologicalEpoch.LOPINGIAN, GeologicalEpoch.GUADALUPIAN, GeologicalEpoch.CISURALIAN)
            CARBONIFEROUS -> listOf(GeologicalEpoch.LATE_CARBONIFEROUS, GeologicalEpoch.EARLY_CARBONIFEROUS)
            PENNSYLVANIAN -> listOf(GeologicalEpoch.LATE_PENNSYLVANIAN, GeologicalEpoch.MIDDLE_PENNSYLVANIAN, GeologicalEpoch.EARLY_PENNSYLVANIAN)
            MISSISSIPPIAN -> listOf(GeologicalEpoch.LATE_MISSISSIPPIAN, GeologicalEpoch.MIDDLE_MISSISSIPPIAN, GeologicalEpoch.EARLY_MISSISSIPPIAN)
            DEVONIAN -> listOf(GeologicalEpoch.LATE_DEVONIAN, GeologicalEpoch.MIDDLE_DEVONIAN, GeologicalEpoch.EARLY_DEVONIAN)
            SILURIAN -> listOf(GeologicalEpoch.PRIDOLI, GeologicalEpoch.LUDLOW, GeologicalEpoch.WENLOCK, GeologicalEpoch.LLANDOVERY)
            ORDOVICIAN -> listOf(GeologicalEpoch.LATE_ORDOVICIAN, GeologicalEpoch.MIDDLE_ORDOVICIAN, GeologicalEpoch.EARLY_ORDOVICIAN)
            CAMBRIAN -> listOf(GeologicalEpoch.FURONGIAN, GeologicalEpoch.MIAOLINGIAN, GeologicalEpoch.TERRENEUVIAN)
            NEO_PROTEROZOIC, MESO_PROTEROZOIC, PALEO_PROTEROZOIC -> emptyList()
            NEO_ARCHEAN, MESO_ARCHEAN, PALEO_ARCHEAN, EO_ARCHEAN -> emptyList()
        }
    
    companion object {
        fun getAllCases(divideCarboniferous: Boolean = false): List<GeologicalPeriod> {
            val all = mutableListOf(
                QUATERNARY, NEOGENE, PALEOGENE, CRETACEOUS, JURASSIC, TRIASSIC, PERMIAN,
                DEVONIAN, SILURIAN, ORDOVICIAN, CAMBRIAN, NEO_PROTEROZOIC, MESO_PROTEROZOIC,
                PALEO_PROTEROZOIC, NEO_ARCHEAN, MESO_ARCHEAN, PALEO_ARCHEAN, EO_ARCHEAN
            )

            if (divideCarboniferous) {
                all.add(7, MISSISSIPPIAN)
                all.add(7, PENNSYLVANIAN)
            } else {
                all.add(7, CARBONIFEROUS)
            }

            return all
        }

        /**
         * Deserializes from camelCase or snake_case format
         * Supports both new camelCase format (iOS compatible) and legacy snake_case format
         */
        fun fromSerializedName(name: String?): GeologicalPeriod? {
            if (name == null) return null
            return entries.find {
                it.serializedName.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true) ||
                it.name.replace("_", "").equals(name, ignoreCase = true)
            }
        }
    }
}
