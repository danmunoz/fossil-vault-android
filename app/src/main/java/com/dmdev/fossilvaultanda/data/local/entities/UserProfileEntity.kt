package com.dmdev.fossilvaultanda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val fullName: String? = null,
    val username: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val isPublic: Boolean = false,
    
    // Profile picture (stored as JSON string)
    val picture: String? = null,
    
    // App settings (stored as JSON string)
    val settings: String,
    
    // Sync metadata
    val lastModified: Long = System.currentTimeMillis()
)