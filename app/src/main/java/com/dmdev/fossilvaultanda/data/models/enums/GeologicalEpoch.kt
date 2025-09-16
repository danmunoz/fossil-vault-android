package com.fossilVault.geological

enum class GeologicalEpoch(
    val displayName: String,
    val startMya: Double,
    val endMya: Double
) {
    // Quaternary
    HOLOCENE("Holocene", 0.0, 0.012),
    PLEISTOCENE("Pleistocene", 0.012, 2.58),
    
    // Neogene
    PLIOCENE("Pliocene", 2.58, 5.333),
    MIOCENE("Miocene", 5.333, 23.03),
    
    // Paleogene
    OLIGOCENE("Oligocene", 23.03, 33.9),
    EOCENE("Eocene", 33.9, 56.0),
    PALEOCENE("Paleocene", 56.0, 66.0),
    
    // Cretaceous
    LATE_CRETACEOUS("Late Cretaceous", 66.0, 100.5),
    EARLY_CRETACEOUS("Early Cretaceous", 100.5, 145.0),
    
    // Jurassic
    LATE_JURASSIC("Late Jurassic", 145.0, 163.5),
    MIDDLE_JURASSIC("Middle Jurassic", 163.5, 174.1),
    EARLY_JURASSIC("Early Jurassic", 174.1, 201.3),
    
    // Triassic
    LATE_TRIASSIC("Late Triassic", 201.3, 237.0),
    MIDDLE_TRIASSIC("Middle Triassic", 237.0, 247.2),
    EARLY_TRIASSIC("Early Triassic", 247.2, 251.9),
    
    // Permian
    LOPINGIAN("Lopingian", 251.9, 259.1),
    GUADALUPIAN("Guadalupian", 259.1, 272.3),
    CISURALIAN("Cisuralian", 272.3, 298.9),
    
    // Carboniferous
    LATE_CARBONIFEROUS("Late Carboniferous", 298.9, 323.4),
    EARLY_CARBONIFEROUS("Early Carboniferous", 323.4, 358.9),
    
    // Pennsylvanian (when Carboniferous is divided)
    LATE_PENNSYLVANIAN("Late Pennsylvanian", 298.9, 307.01),
    MIDDLE_PENNSYLVANIAN("Middle Pennsylvanian", 307.01, 315.2),
    EARLY_PENNSYLVANIAN("Early Pennsylvanian", 315.2, 323.4),
    
    // Mississippian (when Carboniferous is divided)
    LATE_MISSISSIPPIAN("Late Mississippian", 323.4, 330.3),
    MIDDLE_MISSISSIPPIAN("Middle Mississippian", 330.3, 346.7),
    EARLY_MISSISSIPPIAN("Early Mississippian", 346.7, 358.9),
    
    // Devonian
    LATE_DEVONIAN("Late Devonian", 358.9, 372.2),
    MIDDLE_DEVONIAN("Middle Devonian", 382.7, 393.3),
    EARLY_DEVONIAN("Early Devonian", 393.3, 419.2),
    
    // Silurian
    PRIDOLI("Pridoli", 419.2, 423.0),
    LUDLOW("Ludlow", 425.6, 427.4),
    WENLOCK("Wenlock", 430.5, 433.4),
    LLANDOVERY("Llandovery", 433.4, 443.8),
    
    // Ordovician
    LATE_ORDOVICIAN("Late Ordovician", 443.8, 453.0),
    MIDDLE_ORDOVICIAN("Middle Ordovician", 453.0, 470.0),
    EARLY_ORDOVICIAN("Early Ordovician", 470.0, 485.4),
    
    // Cambrian
    FURONGIAN("Furongian", 485.4, 497.0),
    MIAOLINGIAN("Miaolingian", 497.0, 509.0),
    TERRENEUVIAN("Terreneuvian", 521.0, 538.8);
    
    val timeRange: String
        get() = "$startMya - $endMya Mya"
    
    val period: GeologicalPeriod
        get() = when (this) {
            HOLOCENE, PLEISTOCENE -> GeologicalPeriod.QUATERNARY
            PLIOCENE, MIOCENE -> GeologicalPeriod.NEOGENE
            OLIGOCENE, EOCENE, PALEOCENE -> GeologicalPeriod.PALEOGENE
            LATE_CRETACEOUS, EARLY_CRETACEOUS -> GeologicalPeriod.CRETACEOUS
            LATE_JURASSIC, MIDDLE_JURASSIC, EARLY_JURASSIC -> GeologicalPeriod.JURASSIC
            LATE_TRIASSIC, MIDDLE_TRIASSIC, EARLY_TRIASSIC -> GeologicalPeriod.TRIASSIC
            LOPINGIAN, GUADALUPIAN, CISURALIAN -> GeologicalPeriod.PERMIAN
            LATE_CARBONIFEROUS, EARLY_CARBONIFEROUS -> GeologicalPeriod.CARBONIFEROUS
            LATE_PENNSYLVANIAN, MIDDLE_PENNSYLVANIAN, EARLY_PENNSYLVANIAN -> GeologicalPeriod.PENNSYLVANIAN
            LATE_MISSISSIPPIAN, MIDDLE_MISSISSIPPIAN, EARLY_MISSISSIPPIAN -> GeologicalPeriod.MISSISSIPPIAN
            LATE_DEVONIAN, MIDDLE_DEVONIAN, EARLY_DEVONIAN -> GeologicalPeriod.DEVONIAN
            PRIDOLI, LUDLOW, WENLOCK, LLANDOVERY -> GeologicalPeriod.SILURIAN
            LATE_ORDOVICIAN, MIDDLE_ORDOVICIAN, EARLY_ORDOVICIAN -> GeologicalPeriod.ORDOVICIAN
            FURONGIAN, MIAOLINGIAN, TERRENEUVIAN -> GeologicalPeriod.CAMBRIAN
        }
    
    val ages: List<GeologicalAge>
        get() = when (this) {
            HOLOCENE -> listOf(GeologicalAge.MEGHALAYAN, GeologicalAge.NORTHGRIPPIAN, GeologicalAge.GREENLANDIAN)
            PLEISTOCENE -> listOf(GeologicalAge.UPPER_PLEISTOCENE, GeologicalAge.CHIBANIAN, GeologicalAge.CALABRIAN, GeologicalAge.GELASIAN)
            PLIOCENE -> listOf(GeologicalAge.PIACENZIAN, GeologicalAge.ZANCLEAN)
            MIOCENE -> listOf(GeologicalAge.MESSINIAN, GeologicalAge.TORTONIAN, GeologicalAge.SERRAVALLIAN, 
                             GeologicalAge.LANGHIAN, GeologicalAge.BURDIGALIAN, GeologicalAge.AQUITANIAN)
            OLIGOCENE -> listOf(GeologicalAge.CHATTIAN, GeologicalAge.RUPELIAN)
            EOCENE -> listOf(GeologicalAge.PRIABONIAN, GeologicalAge.BARTONIAN, GeologicalAge.LUTETIAN, GeologicalAge.YPRESIAN)
            PALEOCENE -> listOf(GeologicalAge.THANETIAN, GeologicalAge.SELANDIAN, GeologicalAge.DANIAN)
            LATE_CRETACEOUS -> listOf(GeologicalAge.MAASTRICHTIAN, GeologicalAge.CAMPANIAN, GeologicalAge.SANTONIAN, 
                                     GeologicalAge.CONIACIAN, GeologicalAge.TURONIAN, GeologicalAge.CENOMANIAN)
            EARLY_CRETACEOUS -> listOf(GeologicalAge.ALBIAN, GeologicalAge.APTIAN, GeologicalAge.BARREMIAN, 
                                      GeologicalAge.HAUTERIVIAN, GeologicalAge.VALANGINIAN, GeologicalAge.BERRIASIAN)
            LATE_JURASSIC -> listOf(GeologicalAge.TITHONIAN, GeologicalAge.KIMMERIDGIAN, GeologicalAge.OXFORDIAN)
            MIDDLE_JURASSIC -> listOf(GeologicalAge.CALLOVIAN, GeologicalAge.BATHONIAN, GeologicalAge.BAJOCIAN, GeologicalAge.AALENIAN)
            EARLY_JURASSIC -> listOf(GeologicalAge.TOARCIAN, GeologicalAge.PLIENSBACHIAN, GeologicalAge.SINEMURIAN, GeologicalAge.HETTANGIAN)
            LATE_TRIASSIC -> listOf(GeologicalAge.RHAETIAN, GeologicalAge.NORIAN, GeologicalAge.CARNIAN)
            MIDDLE_TRIASSIC -> listOf(GeologicalAge.LADINIAN, GeologicalAge.ANISIAN)
            EARLY_TRIASSIC -> listOf(GeologicalAge.OLENEKIAN, GeologicalAge.INDUAN)
            LOPINGIAN -> listOf(GeologicalAge.CHANGHSINGIAN, GeologicalAge.WUCHIAPINGIAN)
            GUADALUPIAN -> listOf(GeologicalAge.CAPITANIAN, GeologicalAge.WORDIAN, GeologicalAge.ROADIAN)
            CISURALIAN -> listOf(GeologicalAge.KUNGURIAN, GeologicalAge.ARTINSKIAN, GeologicalAge.SAKMARIAN, GeologicalAge.ASSELIAN)
            LATE_CARBONIFEROUS -> listOf(GeologicalAge.GZHELIAN, GeologicalAge.KASIMOVIAN, GeologicalAge.MOSCOVIAN, GeologicalAge.BASHKIRIAN)
            LATE_PENNSYLVANIAN -> listOf(GeologicalAge.GZHELIAN, GeologicalAge.KASIMOVIAN)
            MIDDLE_PENNSYLVANIAN -> listOf(GeologicalAge.MOSCOVIAN)
            EARLY_PENNSYLVANIAN -> listOf(GeologicalAge.BASHKIRIAN)
            EARLY_CARBONIFEROUS -> listOf(GeologicalAge.SERPUKHOVIAN, GeologicalAge.VISEAN, GeologicalAge.TOURNAISIAN)
            LATE_MISSISSIPPIAN -> listOf(GeologicalAge.SERPUKHOVIAN)
            MIDDLE_MISSISSIPPIAN -> listOf(GeologicalAge.VISEAN)
            EARLY_MISSISSIPPIAN -> listOf(GeologicalAge.TOURNAISIAN)
            LATE_DEVONIAN -> listOf(GeologicalAge.FAMENNIAN, GeologicalAge.FRASNIAN)
            MIDDLE_DEVONIAN -> listOf(GeologicalAge.GIVETIAN, GeologicalAge.EIFELIAN)
            EARLY_DEVONIAN -> listOf(GeologicalAge.EMSIAN, GeologicalAge.PRAGIAN, GeologicalAge.LOCHKOVIAN)
            PRIDOLI -> listOf(GeologicalAge.LUDFORDIAN)
            LUDLOW -> listOf(GeologicalAge.GORSTIAN)
            WENLOCK -> listOf(GeologicalAge.HOMERIAN, GeologicalAge.SHEINWOODIAN)
            LLANDOVERY -> listOf(GeologicalAge.TELYCHIAN, GeologicalAge.AERONIAN, GeologicalAge.RHUDDANIAN)
            LATE_ORDOVICIAN -> listOf(GeologicalAge.HIRNANTIAN, GeologicalAge.KATIAN)
            MIDDLE_ORDOVICIAN -> listOf(GeologicalAge.SANDBIAN, GeologicalAge.DARRIWILIAN)
            EARLY_ORDOVICIAN -> listOf(GeologicalAge.DAPINGIAN, GeologicalAge.FLOIAN, GeologicalAge.TREMADOCIAN)
            FURONGIAN -> listOf(GeologicalAge.STAGE10, GeologicalAge.JIANGSHANIAN, GeologicalAge.PAIBIAN)
            MIAOLINGIAN -> listOf(GeologicalAge.GUZHANGIAN, GeologicalAge.DRUMIAN, GeologicalAge.WULIUAN)
            TERRENEUVIAN -> listOf(GeologicalAge.STAGE4, GeologicalAge.STAGE3, GeologicalAge.STAGE2, GeologicalAge.FORTUNIAN)
        }
}
