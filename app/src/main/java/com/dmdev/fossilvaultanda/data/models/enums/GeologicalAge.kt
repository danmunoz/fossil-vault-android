package com.fossilVault.geological

enum class GeologicalAge(
    val displayName: String,
    val startMya: Double,
    val endMya: Double
) {
    // Quaternary - Holocene Epoch
    MEGHALAYAN("Meghalayan", 0.0042, 0.0082),
    NORTHGRIPPIAN("Northgrippian", 0.0082, 0.0117),
    GREENLANDIAN("Greenlandian", 0.0117, 0.012),
    
    // Quaternary - Pleistocene Epoch
    UPPER_PLEISTOCENE("Upper Pleistocene", 0.129, 0.774),
    CHIBANIAN("Chibanian", 0.774, 1.8),
    CALABRIAN("Calabrian", 0.012, 1.8),
    GELASIAN("Gelasian", 1.8, 2.58),
    
    // Neogene - Pliocene Epoch
    PIACENZIAN("Piacenzian", 2.58, 3.60),
    ZANCLEAN("Zanclean", 3.60, 5.333),
    
    // Neogene - Miocene Epoch
    MESSINIAN("Messinian", 5.333, 7.246),
    TORTONIAN("Tortonian", 7.246, 11.63),
    SERRAVALLIAN("Serravallian", 11.63, 13.82),
    LANGHIAN("Langhian", 13.82, 15.97),
    BURDIGALIAN("Burdigalian", 15.97, 20.44),
    AQUITANIAN("Aquitanian", 20.44, 23.03),
    
    // Paleogene - Oligocene Epoch
    CHATTIAN("Chattian", 23.03, 27.82),
    RUPELIAN("Rupelian", 27.82, 33.9),
    
    // Paleogene - Eocene Epoch
    PRIABONIAN("Priabonian", 33.9, 37.71),
    BARTONIAN("Bartonian", 37.71, 41.2),
    LUTETIAN("Lutetian", 41.2, 47.8),
    YPRESIAN("Ypresian", 47.8, 56.0),
    
    // Paleogene - Paleocene Epoch
    THANETIAN("Thanetian", 56.0, 59.2),
    SELANDIAN("Selandian", 59.2, 61.6),
    DANIAN("Danian", 61.6, 66.0),
    
    // Cretaceous - Upper Epoch
    MAASTRICHTIAN("Maastrichtian", 66.0, 72.1),
    CAMPANIAN("Campanian", 72.1, 83.6),
    SANTONIAN("Santonian", 83.6, 86.3),
    CONIACIAN("Coniacian", 86.3, 89.8),
    TURONIAN("Turonian", 89.8, 93.9),
    CENOMANIAN("Cenomanian", 93.9, 100.5),
    
    // Cretaceous - Lower Epoch
    ALBIAN("Albian", 100.5, 113.0),
    APTIAN("Aptian", 113.0, 125.0),
    BARREMIAN("Barremian", 125.0, 129.4),
    HAUTERIVIAN("Hauterivian", 129.4, 132.6),
    VALANGINIAN("Valanginian", 132.6, 139.8),
    BERRIASIAN("Berriasian", 139.8, 145.0),
    
    // Jurassic - Upper Epoch
    TITHONIAN("Tithonian", 145.0, 152.1),
    KIMMERIDGIAN("Kimmeridgian", 152.1, 157.3),
    OXFORDIAN("Oxfordian", 157.3, 163.5),
    
    // Jurassic - Middle Epoch
    CALLOVIAN("Callovian", 163.5, 166.1),
    BATHONIAN("Bathonian", 166.1, 168.3),
    BAJOCIAN("Bajocian", 168.3, 170.3),
    AALENIAN("Aalenian", 170.3, 174.1),
    
    // Jurassic - Lower Epoch
    TOARCIAN("Toarcian", 174.1, 182.7),
    PLIENSBACHIAN("Pliensbachian", 182.7, 190.8),
    SINEMURIAN("Sinemurian", 190.8, 199.3),
    HETTANGIAN("Hettangian", 199.3, 201.3),
    
    // Triassic - Upper Epoch
    RHAETIAN("Rhaetian", 201.3, 208.5),
    NORIAN("Norian", 208.5, 227.0),
    CARNIAN("Carnian", 227.0, 237.0),
    
    // Triassic - Middle Epoch
    LADINIAN("Ladinian", 237.0, 242.0),
    ANISIAN("Anisian", 242.0, 247.2),
    
    // Triassic - Lower Epoch
    OLENEKIAN("Olenekian", 247.2, 251.2),
    INDUAN("Induan", 251.2, 251.9),
    
    // Permian - Lopingian Epoch
    CHANGHSINGIAN("Changhsingian", 254.14, 251.902),
    WUCHIAPINGIAN("Wuchiapingian", 259.1, 254.14),
    
    // Permian - Guadalupian Epoch
    CAPITANIAN("Capitanian", 265.1, 259.1),
    WORDIAN("Wordian", 268.8, 265.1),
    ROADIAN("Roadian", 272.3, 268.8),
    
    // Permian - Cisuralian Epoch
    KUNGURIAN("Kungurian", 283.5, 272.3),
    ARTINSKIAN("Artinskian", 290.1, 283.5),
    SAKMARIAN("Sakmarian", 295.0, 290.1),
    ASSELIAN("Asselian", 298.9, 295.0),
    
    // Carboniferous
    GZHELIAN("Gzhelian", 303.7, 298.9),
    KASIMOVIAN("Kasimovian", 307.0, 303.7),
    MOSCOVIAN("Moscovian", 315.2, 307.0),
    BASHKIRIAN("Bashkirian", 323.2, 315.2),
    SERPUKHOVIAN("Serpukhovian", 330.9, 323.2),
    VISEAN("Visean", 346.7, 330.9),
    TOURNAISIAN("Tournaisian", 358.9, 346.7),
    
    // Devonian
    FAMENNIAN("Famennian", 372.2, 358.9),
    FRASNIAN("Frasnian", 382.7, 372.2),
    GIVETIAN("Givetian", 387.7, 382.7),
    EIFELIAN("Eifelian", 393.3, 387.7),
    EMSIAN("Emsian", 407.6, 393.3),
    PRAGIAN("Pragian", 410.8, 407.6),
    LOCHKOVIAN("Lochkovian", 419.2, 410.8),
    
    // Silurian
    LUDFORDIAN("Ludfordian", 425.6, 419.2),
    GORSTIAN("Gorstian", 427.4, 425.6),
    HOMERIAN("Homerian", 430.5, 427.4),
    SHEINWOODIAN("Sheinwoodian", 433.4, 430.5),
    TELYCHIAN("Telychian", 438.5, 433.4),
    AERONIAN("Aeronian", 440.8, 438.5),
    RHUDDANIAN("Rhuddanian", 443.8, 440.8),
    
    // Ordovician
    HIRNANTIAN("Hirnantian", 445.2, 443.8),
    KATIAN("Katian", 453.0, 445.2),
    SANDBIAN("Sandbian", 458.4, 453.0),
    DARRIWILIAN("Darriwilian", 467.3, 458.4),
    DAPINGIAN("Dapingian", 470.0, 467.3),
    FLOIAN("Floian", 477.7, 470.0),
    TREMADOCIAN("Tremadocian", 485.4, 477.7),
    
    // Cambrian
    STAGE10("Stage 10", 489.5, 485.4),
    JIANGSHANIAN("Jiangshanian", 494.0, 489.5),
    PAIBIAN("Paibian", 497.0, 494.0),
    GUZHANGIAN("Guzhangian", 500.5, 497.0),
    DRUMIAN("Drumian", 504.5, 500.5),
    WULIUAN("Wuliuan", 509.0, 504.5),
    STAGE4("Stage 4", 514.0, 509.0),
    STAGE3("Stage 3", 521.0, 514.0),
    STAGE2("Stage 2", 529.0, 521.0),
    FORTUNIAN("Fortunian", 538.8, 529.0);
    
    val timeRange: String
        get() = "$startMya - $endMya Mya"

    /**
     * Serialized name in camelCase format for Firebase/iOS compatibility
     * Converts SCREAMING_SNAKE_CASE to camelCase
     * Example: UPPER_PLEISTOCENE -> upperPleistocene
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

    fun getEpoch(divideCarboniferous: Boolean = false): GeologicalEpoch {
        return when (this) {
            MEGHALAYAN, NORTHGRIPPIAN, GREENLANDIAN -> GeologicalEpoch.HOLOCENE
            UPPER_PLEISTOCENE, CHIBANIAN, CALABRIAN, GELASIAN -> GeologicalEpoch.PLEISTOCENE
            PIACENZIAN, ZANCLEAN -> GeologicalEpoch.PLIOCENE
            MESSINIAN, TORTONIAN, SERRAVALLIAN, LANGHIAN, BURDIGALIAN, AQUITANIAN -> GeologicalEpoch.MIOCENE
            CHATTIAN, RUPELIAN -> GeologicalEpoch.OLIGOCENE
            PRIABONIAN, BARTONIAN, LUTETIAN, YPRESIAN -> GeologicalEpoch.EOCENE
            THANETIAN, SELANDIAN, DANIAN -> GeologicalEpoch.PALEOCENE
            MAASTRICHTIAN, CAMPANIAN, SANTONIAN, CONIACIAN, TURONIAN, CENOMANIAN -> GeologicalEpoch.LATE_CRETACEOUS
            ALBIAN, APTIAN, BARREMIAN, HAUTERIVIAN, VALANGINIAN, BERRIASIAN -> GeologicalEpoch.EARLY_CRETACEOUS
            TITHONIAN, KIMMERIDGIAN, OXFORDIAN -> GeologicalEpoch.LATE_JURASSIC
            CALLOVIAN, BATHONIAN, BAJOCIAN, AALENIAN -> GeologicalEpoch.MIDDLE_JURASSIC
            TOARCIAN, PLIENSBACHIAN, SINEMURIAN, HETTANGIAN -> GeologicalEpoch.EARLY_JURASSIC
            RHAETIAN, NORIAN, CARNIAN -> GeologicalEpoch.LATE_TRIASSIC
            LADINIAN, ANISIAN -> GeologicalEpoch.MIDDLE_TRIASSIC
            OLENEKIAN, INDUAN -> GeologicalEpoch.EARLY_TRIASSIC
            CHANGHSINGIAN, WUCHIAPINGIAN -> GeologicalEpoch.LOPINGIAN
            CAPITANIAN, WORDIAN, ROADIAN -> GeologicalEpoch.GUADALUPIAN
            KUNGURIAN, ARTINSKIAN, SAKMARIAN, ASSELIAN -> GeologicalEpoch.CISURALIAN
            GZHELIAN, KASIMOVIAN -> if (divideCarboniferous) GeologicalEpoch.LATE_PENNSYLVANIAN else GeologicalEpoch.LATE_CARBONIFEROUS
            MOSCOVIAN -> if (divideCarboniferous) GeologicalEpoch.MIDDLE_PENNSYLVANIAN else GeologicalEpoch.LATE_CARBONIFEROUS
            BASHKIRIAN -> if (divideCarboniferous) GeologicalEpoch.EARLY_PENNSYLVANIAN else GeologicalEpoch.LATE_CARBONIFEROUS
            SERPUKHOVIAN -> if (divideCarboniferous) GeologicalEpoch.LATE_MISSISSIPPIAN else GeologicalEpoch.EARLY_CARBONIFEROUS
            VISEAN -> if (divideCarboniferous) GeologicalEpoch.MIDDLE_MISSISSIPPIAN else GeologicalEpoch.EARLY_CARBONIFEROUS
            TOURNAISIAN -> if (divideCarboniferous) GeologicalEpoch.EARLY_MISSISSIPPIAN else GeologicalEpoch.EARLY_CARBONIFEROUS
            FAMENNIAN, FRASNIAN -> GeologicalEpoch.LATE_DEVONIAN
            GIVETIAN, EIFELIAN -> GeologicalEpoch.MIDDLE_DEVONIAN
            EMSIAN, PRAGIAN, LOCHKOVIAN -> GeologicalEpoch.EARLY_DEVONIAN
            LUDFORDIAN -> GeologicalEpoch.PRIDOLI
            GORSTIAN -> GeologicalEpoch.LUDLOW
            HOMERIAN, SHEINWOODIAN -> GeologicalEpoch.WENLOCK
            TELYCHIAN, AERONIAN, RHUDDANIAN -> GeologicalEpoch.LLANDOVERY
            HIRNANTIAN, KATIAN -> GeologicalEpoch.LATE_ORDOVICIAN
            SANDBIAN, DARRIWILIAN -> GeologicalEpoch.MIDDLE_ORDOVICIAN
            DAPINGIAN, FLOIAN, TREMADOCIAN -> GeologicalEpoch.EARLY_ORDOVICIAN
            STAGE10, JIANGSHANIAN, PAIBIAN -> GeologicalEpoch.FURONGIAN
            GUZHANGIAN, DRUMIAN, WULIUAN -> GeologicalEpoch.MIAOLINGIAN
            STAGE4, STAGE3, STAGE2, FORTUNIAN -> GeologicalEpoch.TERRENEUVIAN
        }
    }

    companion object {
        /**
         * Deserializes from camelCase or snake_case format
         * Supports both new camelCase format (iOS compatible) and legacy snake_case format
         */
        fun fromSerializedName(name: String?): GeologicalAge? {
            if (name == null) return null
            return entries.find {
                it.serializedName.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true) ||
                it.name.replace("_", "").equals(name, ignoreCase = true)
            }
        }
    }
}
