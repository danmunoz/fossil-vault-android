package com.dmdev.fossilvaultanda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.StoredImage
import com.dmdev.fossilvaultanda.data.models.PeriodToGeologicalTimeMapper
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
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

@Entity(tableName = "specimens")
data class SpecimenEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val species: String, // Legacy field for backward compatibility
    // Taxonomy fields (13 fields matching iOS)
    val taxonomyKingdom: String? = null,
    val taxonomyPhylum: String? = null,
    val taxonomySubPhylum: String? = null,
    val taxonomyClass: String? = null,
    val taxonomySubClass: String? = null,
    val taxonomySuperOrder: String? = null,
    val taxonomyOrder: String? = null,
    val taxonomyInfraOrder: String? = null,
    val taxonomySubOrder: String? = null,
    val taxonomySuperFamily: String? = null,
    val taxonomyFamily: String? = null,
    val taxonomyGenus: String? = null,
    val taxonomySubGenus: String? = null,
    val taxonomySpecies: String? = null,
    val period: String, // Legacy field for backward compatibility
    val geologicalEra: String? = null,
    val geologicalPeriod: String? = null,
    val geologicalEpoch: String? = null,
    val geologicalAge: String? = null,
    val element: String,
    
    // Location Information
    val location: String? = null,
    val country: String? = null,
    val formation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,

    // Physical Measurements
    val width: Double? = null,
    val height: Double? = null,
    val length: Double? = null,
    val unit: String = "MM",
    val weight: Double? = null,
    val weightUnit: String = "GR",
    
    // Dates (stored as ISO strings)
    val collectionDate: String? = null,
    val acquisitionDate: String? = null,
    val creationDate: String,
    
    // Additional Metadata
    val inventoryId: String? = null,
    val notes: String? = null,
    val storageRoom: String? = null,
    val storageCabinet: String? = null,
    val storageDrawer: String? = null,

    // Media (stored as JSON string)
    val imageUrls: String = "[]",
    val shareUrl: String? = null,
    
    // Organization
    val isFavorite: Boolean = false,
    val tagNames: String = "[]", // JSON array
    val isPublic: Boolean = false,
    
    // Acquisition Information
    val acquisitionMethod: String? = null,
    val condition: String? = null,

    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: String? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: String? = null,

    // Disposition (stored as JSON string)
    val dispositionJson: String? = null,

    // Sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val needsSync: Boolean = false
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
            country = country,
            formation = formation,
            latitude = latitude,
            longitude = longitude,
            width = width,
            height = height,
            length = length,
            unit = SizeUnit.fromSerializedName(unit),
            weight = weight,
            weightUnit = com.dmdev.fossilvaultanda.data.models.enums.WeightUnit.fromSerializedName(weightUnit),
            collectionDate = collectionDate?.let { Instant.parse(it) },
            acquisitionDate = acquisitionDate?.let { Instant.parse(it) },
            creationDate = Instant.parse(creationDate),
            acquisitionMethod = acquisitionMethod?.let { AcquisitionMethod.fromSerializedName(it) },
            condition = condition?.let { Condition.fromSerializedName(it) },
            inventoryId = inventoryId,
            notes = notes,
            storage = if (storageRoom != null || storageCabinet != null || storageDrawer != null) {
                com.dmdev.fossilvaultanda.data.models.StorageMethod(
                    drawer = storageDrawer,
                    cabinet = storageCabinet,
                    room = storageRoom
                )
            } else null,
            imageUrls = try {
                Json.decodeFromString<List<StoredImage>>(imageUrls)
            } catch (e: Exception) {
                emptyList()
            },
            shareUrl = shareUrl,
            isFavorite = isFavorite,
            tagNames = try {
                Json.decodeFromString<List<String>>(tagNames)
            } catch (e: Exception) {
                emptyList()
            },
            isPublic = isPublic,
            pricePaid = pricePaid,
            pricePaidCurrency = Currency.fromSerializedName(pricePaidCurrency),
            estimatedValue = estimatedValue,
            estimatedValueCurrency = Currency.fromSerializedName(estimatedValueCurrency),
            disposition = dispositionJson?.let {
                try {
                    Json.decodeFromString<com.dmdev.fossilvaultanda.data.models.Disposition>(it)
                } catch (e: Exception) {
                    null
                }
            }
        )
    }

    private fun parseGeologicalTime(): GeologicalTime {
        // If new geological time fields exist, use them
        if (geologicalPeriod != null) {
            val era = geologicalEra?.let {
                try {
                    GeologicalEra.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            val gPeriod = try {
                GeologicalPeriod.valueOf(geologicalPeriod.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }

            val epoch = geologicalEpoch?.let {
                try {
                    GeologicalEpoch.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            val age = geologicalAge?.let {
                try {
                    GeologicalAge.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }

            return GeologicalTime(
                era = era,
                period = gPeriod,
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

    private fun parseTaxonomy(): com.dmdev.fossilvaultanda.data.models.Taxonomy {
        // If new taxonomy fields exist, use them
        if (taxonomySpecies != null) {
            return com.dmdev.fossilvaultanda.data.models.Taxonomy(
                kingdom = taxonomyKingdom,
                phylum = taxonomyPhylum,
                subPhylum = taxonomySubPhylum,
                taxonomicClass = taxonomyClass,
                subClass = taxonomySubClass,
                superOrder = taxonomySuperOrder,
                order = taxonomyOrder,
                infraOrder = taxonomyInfraOrder,
                subOrder = taxonomySubOrder,
                superFamily = taxonomySuperFamily,
                family = taxonomyFamily,
                genus = taxonomyGenus,
                subGenus = taxonomySubGenus,
                species = taxonomySpecies
            )
        }

        // Fallback to legacy species field for backward compatibility
        if (species.isNotEmpty()) {
            return com.dmdev.fossilvaultanda.data.models.Taxonomy.fromSpeciesString(species)
        }

        // Return empty Taxonomy if no data
        return com.dmdev.fossilvaultanda.data.models.Taxonomy()
    }
}

fun Specimen.toSpecimenEntity(): SpecimenEntity {
    return SpecimenEntity(
        id = id,
        userId = userId,
        species = "", // Legacy field left empty for new specimens
        taxonomyKingdom = taxonomy.kingdom,
        taxonomyPhylum = taxonomy.phylum,
        taxonomySubPhylum = taxonomy.subPhylum,
        taxonomyClass = taxonomy.taxonomicClass,
        taxonomySubClass = taxonomy.subClass,
        taxonomySuperOrder = taxonomy.superOrder,
        taxonomyOrder = taxonomy.order,
        taxonomyInfraOrder = taxonomy.infraOrder,
        taxonomySubOrder = taxonomy.subOrder,
        taxonomySuperFamily = taxonomy.superFamily,
        taxonomyFamily = taxonomy.family,
        taxonomyGenus = taxonomy.genus,
        taxonomySubGenus = taxonomy.subGenus,
        taxonomySpecies = taxonomy.species,
        period = "", // Legacy field left empty for new specimens
        geologicalEra = geologicalTime.era?.name?.lowercase(),
        geologicalPeriod = geologicalTime.period?.name?.lowercase(),
        geologicalEpoch = geologicalTime.epoch?.name?.lowercase(),
        geologicalAge = geologicalTime.age?.name?.lowercase(),
        element = element.serializedName,
        location = location,
        country = country,
        formation = formation,
        latitude = latitude,
        longitude = longitude,
        width = width,
        height = height,
        length = length,
        unit = unit.serializedName,
        weight = weight,
        weightUnit = weightUnit.serializedName,
        collectionDate = collectionDate?.toString(),
        acquisitionDate = acquisitionDate?.toString(),
        creationDate = creationDate.toString(),
        acquisitionMethod = acquisitionMethod?.serializedName,
        condition = condition?.serializedName,
        inventoryId = inventoryId,
        notes = notes,
        storageRoom = storage?.room,
        storageCabinet = storage?.cabinet,
        storageDrawer = storage?.drawer,
        imageUrls = Json.encodeToString(imageUrls),
        shareUrl = shareUrl,
        isFavorite = isFavorite,
        tagNames = Json.encodeToString(tagNames),
        isPublic = isPublic,
        pricePaid = pricePaid,
        pricePaidCurrency = pricePaidCurrency?.serializedName,
        estimatedValue = estimatedValue,
        estimatedValueCurrency = estimatedValueCurrency?.serializedName,
        dispositionJson = disposition?.let { Json.encodeToString(it) },
        lastModified = System.currentTimeMillis(),
        needsSync = true
    )
}