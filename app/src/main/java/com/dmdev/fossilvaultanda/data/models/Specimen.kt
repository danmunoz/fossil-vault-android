package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.google.firebase.Timestamp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Specimen(
    val id: String = "",
    val userId: String = "",
    val species: String = "",
    val period: Period = Period.UNKNOWN,
    val element: FossilElement = FossilElement.OTHER,
    
    // Location Information
    val location: String? = null,
    val formation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Physical Measurements
    val width: Double? = null,
    val height: Double? = null,
    val length: Double? = null,
    val unit: SizeUnit = SizeUnit.MM,
    
    // Dates
    val collectionDate: Instant? = null,
    val acquisitionDate: Instant? = null,
    val creationDate: Instant = Clock.System.now(),
    
    // Additional Metadata
    val inventoryId: String? = null,
    val notes: String? = null,
    
    // Media
    val imageUrls: List<StoredImage> = emptyList(),
    
    // Organization
    val isFavorite: Boolean = false,
    val tagNames: List<String> = emptyList(),
    val isPublic: Boolean = false,
    
    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: Currency? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: Currency? = null
) {
    fun validate(): Result<Unit> {
        return when {
            id.isBlank() -> Result.failure(DataException.ValidationException("ID cannot be blank"))
            userId.isBlank() -> Result.failure(DataException.ValidationException("User ID cannot be blank"))
            species.isBlank() -> Result.failure(DataException.ValidationException("Species cannot be blank"))
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
            pricePaid != null && pricePaid < 0 -> 
                Result.failure(DataException.ValidationException("Price paid cannot be negative"))
            estimatedValue != null && estimatedValue < 0 -> 
                Result.failure(DataException.ValidationException("Estimated value cannot be negative"))
            else -> Result.success(Unit)
        }
    }
    
    fun toCsvRow(): String {
        return csvEscape(
            listOf(
                id, species, period.displayName, element.displayString,
                location ?: "", formation ?: "",
                latitude?.toString() ?: "", longitude?.toString() ?: "",
                width?.toString() ?: "", height?.toString() ?: "", 
                length?.toString() ?: "", unit.name,
                collectionDate?.toString() ?: "",
                acquisitionDate?.toString() ?: "",
                inventoryId ?: "", notes ?: "",
                creationDate.toString(),
                pricePaid?.toString() ?: "",
                pricePaidCurrency?.name ?: "",
                estimatedValue?.toString() ?: "",
                estimatedValueCurrency?.name ?: ""
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
    
    companion object {
        const val CSV_HEADER = "Identifier,Species,Period,Element,Location,Formation," +
                "Latitude,Longitude,Width,Height,Length,Unit,Collection Date," +
                "Acquisition Date,Inventory ID,Notes,Creation Date,Price Paid," +
                "Price Paid Currency,Estimated Value,Estimated Value Currency"
    }
}