package com.fossilVault.geological

enum class GeologicalEra(
    val displayName: String,
    val startMya: Double,
    val endMya: Double
) {
    CENOZOIC("Cenozoic", 0.0, 66.0),
    MESOZOIC("Mesozoic", 66.0, 251.9),
    PALEOZOIC("Paleozoic", 251.9, 541.0),
    PROTEROZOIC("Proterozoic", 541.0, 2500.0),
    ARCHEAN("Archean", 2500.0, 4000.0);
    
    val timeRange: String
        get() = "$startMya - $endMya Mya"
    
    fun getPeriods(divideCarboniferous: Boolean = false): List<GeologicalPeriod> {
        return when (this) {
            CENOZOIC -> listOf(
                GeologicalPeriod.QUATERNARY,
                GeologicalPeriod.NEOGENE,
                GeologicalPeriod.PALEOGENE
            )
            MESOZOIC -> listOf(
                GeologicalPeriod.CRETACEOUS,
                GeologicalPeriod.JURASSIC,
                GeologicalPeriod.TRIASSIC
            )
            PALEOZOIC -> {
                if (divideCarboniferous) {
                    listOf(
                        GeologicalPeriod.PERMIAN,
                        GeologicalPeriod.PENNSYLVANIAN,
                        GeologicalPeriod.MISSISSIPPIAN,
                        GeologicalPeriod.DEVONIAN,
                        GeologicalPeriod.SILURIAN,
                        GeologicalPeriod.ORDOVICIAN,
                        GeologicalPeriod.CAMBRIAN
                    )
                } else {
                    listOf(
                        GeologicalPeriod.PERMIAN,
                        GeologicalPeriod.CARBONIFEROUS,
                        GeologicalPeriod.DEVONIAN,
                        GeologicalPeriod.SILURIAN,
                        GeologicalPeriod.ORDOVICIAN,
                        GeologicalPeriod.CAMBRIAN
                    )
                }
            }
            PROTEROZOIC -> listOf(
                GeologicalPeriod.NEO_PROTEROZOIC,
                GeologicalPeriod.MESO_PROTEROZOIC,
                GeologicalPeriod.PALEO_PROTEROZOIC
            )
            ARCHEAN -> listOf(
                GeologicalPeriod.NEO_ARCHEAN,
                GeologicalPeriod.MESO_ARCHEAN,
                GeologicalPeriod.PALEO_ARCHEAN,
                GeologicalPeriod.EO_ARCHEAN
            )
        }
    }
}
