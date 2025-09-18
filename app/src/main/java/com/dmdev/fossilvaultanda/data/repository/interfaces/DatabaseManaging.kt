package com.dmdev.fossilvaultanda.data.repository.interfaces

import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.StoredImage
import com.dmdev.fossilvaultanda.data.models.Tag
import com.dmdev.fossilvaultanda.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface DatabaseManaging {
    // Reactive data streams
    val specimens: Flow<List<Specimen>>
    val tags: Flow<List<Tag>>
    val profile: Flow<UserProfile?>
    
    // Specimen operations
    suspend fun save(specimen: Specimen)
    suspend fun update(specimen: Specimen)
    suspend fun getSpecimen(identifier: String): Specimen?
    suspend fun getAllSpecimens(): List<Specimen>
    suspend fun deleteSpecimen(identifier: String)
    suspend fun getSpecimenCount(): Int
    suspend fun getStorageUsage(): Long
    
    // Tag operations
    suspend fun save(tag: Tag)
    
    // Profile operations
    suspend fun updateProfile(profile: UserProfile, imageUrl: StoredImage? = null)
    
    // Utility
    suspend fun clearAllData()
}