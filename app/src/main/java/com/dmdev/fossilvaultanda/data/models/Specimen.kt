package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.AcquisitionMethod
import com.dmdev.fossilvaultanda.data.models.enums.Condition
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.dmdev.fossilvaultanda.data.models.enums.WeightUnit
import com.fossilVault.geological.GeologicalTime
import kotlinx.serialization.Contextual
import com.google.firebase.Timestamp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Specimen(
    val id: String = "",
    val userId: String = "",
    val taxonomy: Taxonomy = Taxonomy(),
    @Contextual val geologicalTime: GeologicalTime = GeologicalTime(),
    val element: FossilElement = FossilElement.OTHER,
    
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
    val unit: SizeUnit = SizeUnit.MM,
    val weight: Double? = null,
    val weightUnit: WeightUnit = WeightUnit.GR,
    
    // Dates
    val collectionDate: Instant? = null,
    val acquisitionDate: Instant? = null,
    val creationDate: Instant = Clock.System.now(),
    
    // Acquisition Information
    val acquisitionMethod: AcquisitionMethod? = null,
    val condition: Condition? = null,

    // Additional Metadata
    val inventoryId: String? = null,
    val notes: String? = null,
    val storage: StorageMethod? = null,

    // Media
    val imageUrls: List<StoredImage> = emptyList(),
    val shareUrl: String? = null,
    
    // Organization
    val isFavorite: Boolean = false,
    val tagNames: List<String> = emptyList(),
    val isPublic: Boolean = false,
    
    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: Currency? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: Currency? = null,

    // Disposition (tracking for sold/traded/gifted/lost specimens)
    val disposition: Disposition? = null
) {
    /**
     * Returns a formatted string describing the dimensions (LxWxH unit)
     * Returns null if no dimensions are set
     * Matches iOS dimensionsDescription computed property
     */
    val dimensionsDescription: String?
        get() {
            if (!hasDimensions) return null

            val dimensions = mutableListOf<String>()
            length?.let { dimensions.add(it.toString()) }
            width?.let { dimensions.add(it.toString()) }
            height?.let { dimensions.add(it.toString()) }

            return if (dimensions.isNotEmpty()) {
                "${dimensions.joinToString("x")} ${unit.serializedName}"
            } else null
        }

    /**
     * Returns true if at least one dimension is set
     */
    private val hasDimensions: Boolean
        get() = width != null || height != null || length != null

    /**
     * Returns true if the specimen has been archived (disposed of)
     * Matches iOS isArchived computed property
     */
    val isArchived: Boolean
        get() = disposition != null
    fun validate(): Result<Unit> {
        return when {
            id.isBlank() -> Result.failure(DataException.ValidationException("ID cannot be blank"))
            userId.isBlank() -> Result.failure(DataException.ValidationException("User ID cannot be blank"))
            taxonomy.species.isBlank() -> Result.failure(DataException.ValidationException("Species cannot be blank"))
            latitude != null && (latitude < -90 || latitude > 90) ->
                Result.failure(DataException.ValidationException("Invalid latitude"))
            longitude != null && (longitude < -180 || longitude > 180) ->
                Result.failure(DataException.ValidationException("Invalid longitude"))
            width != null && width < 0 ->
                Result.failure(DataException.ValidationException("Width cannot be negative"))
            height != null && height < 0 ->
                Result.failure(DataException.ValidationException("Height cannot be negative"))
            length != null && length < 0 ->
                Result.failure(DataException.ValidationException("Length cannot be negative"))
            weight != null && weight < 0 ->
                Result.failure(DataException.ValidationException("Weight cannot be negative"))
            pricePaid != null && pricePaid < 0 ->
                Result.failure(DataException.ValidationException("Price paid cannot be negative"))
            estimatedValue != null && estimatedValue < 0 ->
                Result.failure(DataException.ValidationException("Estimated value cannot be negative"))
            else -> {
                taxonomy.validate().fold(
                    onSuccess = { Result.success(Unit) },
                    onFailure = { Result.failure(DataException.ValidationException(it.message ?: "Invalid taxonomy")) }
                )
            }
        }
    }
    
    fun toCsvRow(): String {
        return csvEscape(
            listOf(
                id,
                taxonomy.getDisplayName(),
                geologicalTime.period?.displayName ?: "Unknown",
                element.displayString,
                location ?: "",
                country ?: "",
                formation ?: "",
                latitude?.toString() ?: "",
                longitude?.toString() ?: "",
                width?.toString() ?: "",
                height?.toString() ?: "",
                length?.toString() ?: "",
                unit.serializedName,
                weight?.toString() ?: "",
                weightUnit.displayString,
                collectionDate?.toString() ?: "",
                acquisitionDate?.toString() ?: "",
                acquisitionMethod?.displayString ?: "",
                condition?.displayString ?: "",
                inventoryId ?: "",
                notes ?: "",
                storage?.room ?: "",
                storage?.cabinet ?: "",
                storage?.drawer ?: "",
                creationDate.toString(),
                pricePaid?.toString() ?: "",
                pricePaidCurrency?.serializedName ?: "",
                estimatedValue?.toString() ?: "",
                estimatedValueCurrency?.serializedName ?: ""
            )
        ).joinToString(",")
    }
    
    private fun csvEscape(values: List<String>): List<String> {
        return values.map { value ->
            when {
                value.contains(",") || value.contains("\"") || value.contains("\n") ->
                    "\"${value.replace("\"", "\"\"")}\""
                else -> value
            }
        }
    }
    
    /**
     * Converts the Specimen to a Map for Firebase storage with proper enum serialization
     */
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "taxonomy" to taxonomy.toFirestoreMap(),
            "geologicalTime" to mapOf(
                "era" to geologicalTime.era?.serializedName,
                "period" to geologicalTime.period?.serializedName,
                "epoch" to geologicalTime.epoch?.serializedName,
                "age" to geologicalTime.age?.serializedName
            ),
            "element" to element.serializedName,
            "location" to location,
            "country" to country,
            "formation" to formation,
            "latitude" to latitude,
            "longitude" to longitude,
            "width" to width,
            "height" to height,
            "length" to length,
            "unit" to unit.serializedName,
            "weight" to weight,
            "weightUnit" to weightUnit.serializedName,
            "collectionDate" to collectionDate?.let {
                com.google.firebase.Timestamp(it.epochSeconds, it.nanosecondsOfSecond)
            },
            "acquisitionDate" to acquisitionDate?.let {
                com.google.firebase.Timestamp(it.epochSeconds, it.nanosecondsOfSecond)
            },
            "creationDate" to com.google.firebase.Timestamp(creationDate.epochSeconds, creationDate.nanosecondsOfSecond),
            "acquisitionMethod" to acquisitionMethod?.serializedName,
            "condition" to condition?.serializedName,
            "inventoryId" to inventoryId,
            "notes" to notes,
            "storage" to storage?.toFirestoreMap(),
            "imageUrls" to imageUrls.map { image ->
                val imageMap = mutableMapOf<String, Any>(
                    "url" to image.url,
                    "path" to image.path
                )
                image.size?.let { imageMap["size"] = it }
                image.format?.let { imageMap["format"] = it.value }
                imageMap
            },
            "shareUrl" to shareUrl,
            "isFavorite" to isFavorite,
            "tagNames" to tagNames,
            "isPublic" to isPublic,
            "pricePaid" to pricePaid,
            "pricePaidCurrency" to pricePaidCurrency?.serializedName,
            "estimatedValue" to estimatedValue,
            "estimatedValueCurrency" to estimatedValueCurrency?.serializedName,
            "disposition" to disposition?.toFirestoreMap()
        )
    }
    
    companion object {
        const val CSV_HEADER = "Identifier,Species,Period,Element,Location,Country,Formation," +
                "Latitude,Longitude,Width,Height,Length,Unit,Weight,Weight Unit," +
                "Collection Date,Acquisition Date,Acquisition Method,Condition,Inventory ID,Notes," +
                "Storage Room,Storage Cabinet,Storage Drawer,Creation Date," +
                "Price Paid,Price Paid Currency,Estimated Value,Estimated Value Currency"
    }
}