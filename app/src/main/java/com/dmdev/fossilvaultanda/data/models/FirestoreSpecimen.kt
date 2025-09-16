package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.google.firebase.Timestamp
import kotlinx.datetime.Instant

data class FirestoreSpecimen(
    val id: String = "",
    val userId: String = "",
    val species: String = "",
    val period: String = "",
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
    
    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: String? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: String? = null
) {
    fun toSpecimen(): Specimen {
        return Specimen(
            id = id,
            userId = userId,
            species = species,
            period = Period.fromSerializedName(period),
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
}