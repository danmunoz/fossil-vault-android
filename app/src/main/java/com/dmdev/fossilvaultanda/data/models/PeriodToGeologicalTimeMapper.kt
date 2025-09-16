package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.fossilVault.geological.GeologicalTime
import com.fossilVault.geological.GeologicalPeriod

object PeriodToGeologicalTimeMapper {

    /**
     * Maps legacy Period enum values to the new GeologicalTime structure
     * This is used for backward compatibility when reading old Firestore documents
     */
    fun mapPeriodToGeologicalTime(period: Period): GeologicalTime {
        val geologicalPeriod = when (period) {
            Period.QUATERNARY -> GeologicalPeriod.QUATERNARY
            Period.NEOGENE -> GeologicalPeriod.NEOGENE
            Period.PALEOCENE -> GeologicalPeriod.PALEOGENE
            Period.CRETACEOUS -> GeologicalPeriod.CRETACEOUS
            Period.JURASSIC -> GeologicalPeriod.JURASSIC
            Period.TRIASSIC -> GeologicalPeriod.TRIASSIC
            Period.PERMIAN -> GeologicalPeriod.PERMIAN
            Period.CARBONIFEROUS -> GeologicalPeriod.CARBONIFEROUS
            Period.MISSISSIPPIAN -> GeologicalPeriod.MISSISSIPPIAN
            Period.PENNSYLVANIAN -> GeologicalPeriod.PENNSYLVANIAN
            Period.DEVONIAN -> GeologicalPeriod.DEVONIAN
            Period.SILURIAN -> GeologicalPeriod.SILURIAN
            Period.ORDOVICIAN -> GeologicalPeriod.ORDOVICIAN
            Period.CAMBRIAN -> GeologicalPeriod.CAMBRIAN
            Period.PRECAMBRIAN -> {
                // Map to most recent Proterozoic for lack of more specific info
                GeologicalPeriod.NEO_PROTEROZOIC
            }
            Period.UNKNOWN -> null
        }

        return GeologicalTime(
            era = geologicalPeriod?.era,
            period = geologicalPeriod,
            epoch = null, // Legacy data doesn't have epoch information
            age = null    // Legacy data doesn't have age information
        )
    }

    /**
     * Maps GeologicalPeriod to legacy Period enum for compatibility
     * Used when UI still needs to work with Period enum values
     */
    fun mapGeologicalPeriodToPeriod(geologicalPeriod: GeologicalPeriod?): Period {
        return when (geologicalPeriod) {
            GeologicalPeriod.QUATERNARY -> Period.QUATERNARY
            GeologicalPeriod.NEOGENE -> Period.NEOGENE
            GeologicalPeriod.PALEOGENE -> Period.PALEOCENE
            GeologicalPeriod.CRETACEOUS -> Period.CRETACEOUS
            GeologicalPeriod.JURASSIC -> Period.JURASSIC
            GeologicalPeriod.TRIASSIC -> Period.TRIASSIC
            GeologicalPeriod.PERMIAN -> Period.PERMIAN
            GeologicalPeriod.CARBONIFEROUS -> Period.CARBONIFEROUS
            GeologicalPeriod.PENNSYLVANIAN -> Period.PENNSYLVANIAN
            GeologicalPeriod.MISSISSIPPIAN -> Period.MISSISSIPPIAN
            GeologicalPeriod.DEVONIAN -> Period.DEVONIAN
            GeologicalPeriod.SILURIAN -> Period.SILURIAN
            GeologicalPeriod.ORDOVICIAN -> Period.ORDOVICIAN
            GeologicalPeriod.CAMBRIAN -> Period.CAMBRIAN
            GeologicalPeriod.NEO_PROTEROZOIC,
            GeologicalPeriod.MESO_PROTEROZOIC,
            GeologicalPeriod.PALEO_PROTEROZOIC -> Period.PRECAMBRIAN
            GeologicalPeriod.NEO_ARCHEAN,
            GeologicalPeriod.MESO_ARCHEAN,
            GeologicalPeriod.PALEO_ARCHEAN,
            GeologicalPeriod.EO_ARCHEAN -> Period.PRECAMBRIAN
            null -> Period.UNKNOWN
        }
    }

    /**
     * Creates a GeologicalTime from a Period selection (for new specimen creation)
     */
    fun createGeologicalTimeFromPeriodSelection(period: Period): GeologicalTime {
        return mapPeriodToGeologicalTime(period)
    }
}