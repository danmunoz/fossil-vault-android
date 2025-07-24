package com.dmdev.fossilvaultanda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "specimens")
data class SpecimenEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val species: String,
    val period: String,
    val element: String,
    
    // Location Information
    val location: String? = null,
    val formation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Physical Measurements
    val width: Double? = null,
    val height: Double? = null,
    val length: Double? = null,
    val unit: String = "MM",
    
    // Dates (stored as ISO strings)
    val collectionDate: String? = null,
    val acquisitionDate: String? = null,
    val creationDate: String,
    
    // Additional Metadata
    val inventoryId: String? = null,
    val notes: String? = null,
    
    // Media (stored as JSON string)
    val imageUrls: String = "[]",
    
    // Organization
    val isFavorite: Boolean = false,
    val tagNames: String = "[]", // JSON array
    val isPublic: Boolean = false,
    
    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: String? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: String? = null,
    
    // Sync metadata
    val lastModified: Long = System.currentTimeMillis(),
    val needsSync: Boolean = false
)