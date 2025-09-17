package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.AcquisitionMethod
import com.dmdev.fossilvaultanda.data.models.enums.Condition
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.fossilVault.geological.GeologicalTime
import com.fossilVault.geological.GeologicalPeriod
import com.fossilVault.geological.GeologicalEra
import com.fossilVault.geological.GeologicalEpoch
import com.fossilVault.geological.GeologicalAge
import com.google.firebase.Timestamp
import kotlinx.datetime.Instant

data class FirestoreSpecimen(
    val id: String = "",
    val userId: String = "",
    val species: String = "", // Legacy field for backward compatibility
    val taxonomy: Map<String, String?>? = null, // New taxonomy structure
    val period: String = "", // Legacy field for backward compatibility
    val geologicalTime: Map<String, String?>? = null, // New geological time structure
    val element: String = "",
    
    // Location Information
    val location: String? = null,
    val formation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Physical Measurements
    val width: Double? = null,
    val height: Double? = null,
    val length: Double? = null,
    val unit: String = "mm",
    
    // Dates as Firestore Timestamps
    val collectionDate: Timestamp? = null,
    val acquisitionDate: Timestamp? = null,
    val creationDate: Timestamp? = null,
    
    // Additional Metadata
    val inventoryId: String? = null,
    val notes: String? = null,
    
    // Media as Maps
    val imageUrls: List<Map<String, Any>> = emptyList(),
    
    // Organization
    val isFavorite: Boolean = false,
    val tagNames: List<String> = emptyList(),
    val isPublic: Boolean = false,
    
    // Acquisition Information
    val acquisitionMethod: String? = null,
    val condition: String? = null,

    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: String? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: String? = null
) {
    fun toSpecimen(): Specimen {
        // Parse geological time with backward compatibility
        val geologicalTimeObject = parseGeologicalTime()

        // Parse taxonomy with backward compatibility
        val taxonomyObject = parseTaxonomy()

        return Specimen(
            id = id,
            userId = userId,
            taxonomy = taxonomyObject,
            geologicalTime = geologicalTimeObject,
            element = FossilElement.fromSerializedName(element),
            location = location,
            formation = formation,
            latitude = latitude,
            longitude = longitude,
            width = width,
            height = height,
            length = length,
            unit = SizeUnit.fromSerializedName(unit),
            collectionDate = collectionDate?.let { 
                Instant.fromEpochSeconds(it.seconds, it.nanoseconds) 
            },
            acquisitionDate = acquisitionDate?.let { 
                Instant.fromEpochSeconds(it.seconds, it.nanoseconds) 
            },
            creationDate = creationDate?.let {
                Instant.fromEpochSeconds(it.seconds, it.nanoseconds)
            } ?: kotlinx.datetime.Clock.System.now(),
            acquisitionMethod = acquisitionMethod?.let { AcquisitionMethod.fromSerializedName(it) },
            condition = condition?.let { Condition.fromSerializedName(it) },
            inventoryId = inventoryId,
            notes = notes,
            imageUrls = imageUrls.mapNotNull { imageMap ->
                val url = imageMap["url"] as? String
                val path = imageMap["path"] as? String
                val size = imageMap["size"] as? Number
                val formatString = imageMap["format"] as? String
                val format = formatString?.let { com.dmdev.fossilvaultanda.data.models.enums.ImageFormat.fromExtension(it) }

                if (url != null && path != null) {
                    StoredImage(
                        url = url,
                        path = path,
                        size = size?.toInt(),
                        format = format
                    )
                } else null
            },
            isFavorite = isFavorite,
            tagNames = tagNames,
            isPublic = isPublic,
            pricePaid = pricePaid,
            pricePaidCurrency = Currency.fromSerializedName(pricePaidCurrency),
            estimatedValue = estimatedValue,
            estimatedValueCurrency = Currency.fromSerializedName(estimatedValueCurrency)
        )
    }

    private fun parseGeologicalTime(): GeologicalTime {
        // If new geologicalTime structure exists, use it
        geologicalTime?.let { gtMap ->
            val era = gtMap["era"]?.let { eraName ->
                try {
                    GeologicalEra.valueOf(eraName.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            val geologicalPeriod = gtMap["period"]?.let { periodName ->
                try {
                    GeologicalPeriod.valueOf(periodName.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            val epoch = gtMap["epoch"]?.let { epochName ->
                try {
                    GeologicalEpoch.valueOf(epochName.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            val age = gtMap["age"]?.let { ageName ->
                try {
                    GeologicalAge.valueOf(ageName.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            return GeologicalTime(
                era = era,
                period = geologicalPeriod,
                epoch = epoch,
                age = age
            )
        }

        // Fallback to legacy period field
        if (period.isNotEmpty()) {
            val legacyPeriod = Period.fromSerializedName(period)
            return PeriodToGeologicalTimeMapper.mapPeriodToGeologicalTime(legacyPeriod)
        }

        // Return empty GeologicalTime if no data
        return GeologicalTime()
    }

    private fun parseTaxonomy(): Taxonomy {
        // If new taxonomy structure exists, use it
        taxonomy?.let { taxonomyMap ->
            return Taxonomy.fromFirestoreMap(taxonomyMap)
        }

        // Fallback to legacy species field for backward compatibility
        if (species.isNotEmpty()) {
            return Taxonomy.fromSpeciesString(species)
        }

        // Return empty Taxonomy if no data
        return Taxonomy()
    }
}