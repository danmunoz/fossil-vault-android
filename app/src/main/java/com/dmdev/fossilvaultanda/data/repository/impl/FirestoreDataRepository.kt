package com.dmdev.fossilvaultanda.data.repository.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dmdev.fossilvaultanda.data.models.DataException
import com.dmdev.fossilvaultanda.data.models.FirestoreSpecimen
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.StoredImage
import com.dmdev.fossilvaultanda.data.models.Tag
import com.dmdev.fossilvaultanda.data.models.UserProfile
import com.dmdev.fossilvaultanda.data.repository.interfaces.AuthenticationManager
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import com.dmdev.fossilvaultanda.data.repository.interfaces.ImageStoring
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val imageStoring: ImageStoring,
    private val authManager: AuthenticationManager,
    @ApplicationContext private val context: Context
) : DatabaseManaging {
    
    private val _specimens = MutableStateFlow<List<Specimen>>(emptyList())
    override val specimens: Flow<List<Specimen>> = _specimens.asStateFlow()
    
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    override val tags: Flow<List<Tag>> = _tags.asStateFlow()
    
    private val _profile = MutableStateFlow<UserProfile?>(null)
    override val profile: Flow<UserProfile?> = _profile.asStateFlow()
    
    private var specimenListener: ListenerRegistration? = null
    private var tagListener: ListenerRegistration? = null
    private var profileListener: ListenerRegistration? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    init {
        setupAuthStateListener()
        // Force initial setup if user is already authenticated
        coroutineScope.launch {
            try {
                val currentUserId = authManager.getCurrentUserId()
                Log.d("FirestoreRepo", "Init - Current user ID: $currentUserId")
                if (currentUserId != null) {
                    Log.d("FirestoreRepo", "User already authenticated, setting up listeners")
                    setupFirestoreListeners()
                }
            } catch (e: Exception) {
                Log.e("FirestoreRepo", "Error during initialization", e)
            }
        }
    }
    
    private fun setupAuthStateListener() {
        authManager.isAuthenticated
            .onEach { isAuthenticated ->
                Log.d("FirestoreRepo", "Auth state changed: isAuthenticated=$isAuthenticated")
                if (isAuthenticated) {
                    // Add a small delay to ensure auth is fully established
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(500) // Wait 500ms
                        try {
                            val userId = authManager.getCurrentUserId()
                            Log.d("FirestoreRepo", "Setting up listeners for authenticated user: $userId")
                            if (userId != null) {
                                setupFirestoreListeners()
                            } else {
                                Log.w("FirestoreRepo", "User authenticated but no userId available")
                            }
                        } catch (e: Exception) {
                            Log.e("FirestoreRepo", "Error setting up listeners after auth", e)
                        }
                    }
                } else {
                    Log.d("FirestoreRepo", "User not authenticated, removing listeners")
                    removeListeners()
                    clearLocalData()
                }
            }
            .launchIn(coroutineScope)
    }
    
    private suspend fun getUserId(): String {
        return authManager.getCurrentUserId()
            ?: throw DataException.AuthenticationException("No user logged in")
    }

    override suspend fun checkInventoryIdExists(inventoryId: String?, excludingSpecimenId: String?): Boolean = withContext(Dispatchers.IO) {
        // Return false if inventory ID is null or empty
        if (inventoryId.isNullOrBlank()) {
            return@withContext false
        }

        try {
            val userId = getUserId()
            val lowercaseInventoryId = inventoryId.lowercase().trim()

            // Query all specimens for this user
            val querySnapshot = firestore.collection("specimens")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Check for case-insensitive matches
            for (document in querySnapshot.documents) {
                // Skip the specimen we're updating (if any)
                if (excludingSpecimenId != null && document.id == excludingSpecimenId) {
                    continue
                }

                val existingInventoryId = document.getString("inventoryId")
                if (!existingInventoryId.isNullOrBlank() &&
                    existingInventoryId.lowercase().trim() == lowercaseInventoryId) {
                    return@withContext true
                }
            }

            false
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error checking inventory ID", e)
            false
        }
    }

    private fun setupFirestoreListeners() {
        coroutineScope.launch {
            try {
                val userId = getUserId()
                Log.d("FirestoreRepo", "Setting up listeners for userId: $userId")
                
                // Specimens listener
                specimenListener = firestore.collection("specimens")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreRepo", "Specimens listener error", error)
                            return@addSnapshotListener
                        }
                        
                        snapshot?.let { querySnapshot ->
                            Log.d("FirestoreRepo", "Received ${querySnapshot.documents.size} documents")
                            val specimens = querySnapshot.documents.mapNotNull { doc ->
                                try {
                                    Log.d("FirestoreRepo", "Document ID: ${doc.id}")
                                    val firestoreSpecimen = doc.toObject(FirestoreSpecimen::class.java)
                                    firestoreSpecimen?.toSpecimen()
                                } catch (e: Exception) {
                                    Log.e("FirestoreRepo", "Error parsing specimen from ${doc.id}", e)
                                    null
                                }
                            }
                            Log.d("FirestoreRepo", "Parsed ${specimens.size} specimens")
                            _specimens.value = specimens
                        }
                    }
                
                // Tags listener
                tagListener = firestore.collection("tags")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreRepo", "Tags listener error", error)
                            return@addSnapshotListener
                        }
                        
                        snapshot?.let { querySnapshot ->
                            val tags = querySnapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(Tag::class.java)
                                } catch (e: Exception) {
                                    Log.e("FirestoreRepo", "Error parsing tag", e)
                                    null
                                }
                            }
                            _tags.value = tags
                        }
                    }
                
                // Profile listener
                profileListener = firestore.collection("users")
                    .document(userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreRepo", "Profile listener error", error)
                            return@addSnapshotListener
                        }
                        
                        snapshot?.let { documentSnapshot ->
                            Log.d("FirestoreRepo", "Profile listener triggered - exists: ${documentSnapshot.exists()}")
                            if (documentSnapshot.exists()) {
                                Log.d("FirestoreRepo", "Profile document data: ${documentSnapshot.data}")
                            }
                            
                            val profile = try {
                                if (documentSnapshot.exists()) {
                                    parseUserProfileFromFirestore(documentSnapshot.data!!)
                                } else {
                                    Log.w("FirestoreRepo", "Profile document does not exist for user: $userId")
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e("FirestoreRepo", "Error parsing profile", e)
                                null
                            }
                            
                            Log.d("FirestoreRepo", "Setting profile value: $profile")
                            _profile.value = profile
                        }
                    }
            } catch (e: Exception) {
                Log.e("FirestoreRepo", "Error setting up listeners", e)
            }
        }
    }
    
    override suspend fun save(specimen: Specimen): Unit = withContext(Dispatchers.IO) {
        try {
            specimen.validate().getOrThrow()

            // Check for duplicate inventory ID
            if (checkInventoryIdExists(specimen.inventoryId)) {
                throw DataException.DuplicateInventoryIdException()
            }

            var updatedSpecimen = specimen

            // Handle local images
            val localImages = specimen.imageUrls.filter { it.isLocal }
            if (localImages.isNotEmpty()) {
                val imageDataList = localImages.mapNotNull { image ->
                    loadImageData(image.url)
                }

                val remoteImages = imageStoring.uploadImages(imageDataList, "specimen")
                val existingRemoteUrls = specimen.imageUrls.filter { !it.isLocal }
                updatedSpecimen = specimen.copy(imageUrls = existingRemoteUrls + remoteImages)
            }

            firestore.collection("specimens")
                .document(updatedSpecimen.id)
                .set(updatedSpecimen.toFirestoreMap())
                .await()
        } catch (e: Exception) {
            // Re-throw DuplicateInventoryIdException as is
            if (e is DataException.DuplicateInventoryIdException) {
                throw e
            }
            throw DataException.FirestoreException("Failed to save specimen", e)
        }
    }
    
    override suspend fun update(specimen: Specimen): Unit = withContext(Dispatchers.IO) {
        try {
            val existingSpecimen = getSpecimen(specimen.id)

            if (existingSpecimen == null) {
                save(specimen)
                return@withContext
            }

            // Check for duplicate inventory ID, excluding the current specimen
            if (checkInventoryIdExists(specimen.inventoryId, excludingSpecimenId = specimen.id)) {
                throw DataException.DuplicateInventoryIdException()
            }

            // Find removed images
            val imagesToRemove = existingSpecimen.imageUrls.filter { oldImage ->
                !specimen.imageUrls.contains(oldImage) && !oldImage.isLocal
            }

            // Delete removed images
            if (imagesToRemove.isNotEmpty()) {
                imageStoring.deleteImages(imagesToRemove)
            }

            // Handle local images and save
            var updatedSpecimen = specimen
            val localImages = specimen.imageUrls.filter { it.isLocal }
            if (localImages.isNotEmpty()) {
                val imageDataList = localImages.mapNotNull { image ->
                    loadImageData(image.url)
                }

                val remoteImages = imageStoring.uploadImages(imageDataList, "specimen")
                val existingRemoteUrls = specimen.imageUrls.filter { !it.isLocal }
                updatedSpecimen = specimen.copy(imageUrls = existingRemoteUrls + remoteImages)
            }

            firestore.collection("specimens")
                .document(updatedSpecimen.id)
                .set(updatedSpecimen.toFirestoreMap())
                .await()
        } catch (e: Exception) {
            // Re-throw DuplicateInventoryIdException as is
            if (e is DataException.DuplicateInventoryIdException) {
                throw e
            }
            throw DataException.FirestoreException("Failed to update specimen", e)
        }
    }
    
    override suspend fun getSpecimen(identifier: String): Specimen? = withContext(Dispatchers.IO) {
        try {
            val document = firestore.collection("specimens")
                .document(identifier)
                .get()
                .await()
            
            val firestoreSpecimen = document.toObject(FirestoreSpecimen::class.java)
            firestoreSpecimen?.toSpecimen()
        } catch (e: Exception) {
            throw DataException.FirestoreException("Failed to get specimen", e)
        }
    }
    
    override suspend fun getAllSpecimens(): List<Specimen> = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId()
            val querySnapshot = firestore.collection("specimens")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            querySnapshot.documents.mapNotNull { doc ->
                try {
                    val firestoreSpecimen = doc.toObject(FirestoreSpecimen::class.java)
                    firestoreSpecimen?.toSpecimen()
                } catch (e: Exception) {
                    Log.e("FirestoreRepo", "Error parsing specimen", e)
                    null
                }
            }
        } catch (e: Exception) {
            throw DataException.FirestoreException("Failed to get all specimens", e)
        }
    }
    
    override suspend fun deleteSpecimen(identifier: String): Unit = withContext(Dispatchers.IO) {
        try {
            val specimen = getSpecimen(identifier)

            // Delete associated images
            specimen?.let {
                if (it.imageUrls.isNotEmpty()) {
                    imageStoring.deleteImages(it.imageUrls)
                }
            }

            firestore.collection("specimens")
                .document(identifier)
                .delete()
                .await()
        } catch (e: Exception) {
            throw DataException.FirestoreException("Failed to delete specimen", e)
        }
    }

    override suspend fun getSpecimenCount(): Int = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId()
                ?: throw DataException.AuthenticationException("User not authenticated")

            val result = firestore.collection("specimens")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            result.size()
        } catch (e: Exception) {
            if (e is DataException) throw e
            throw DataException.FirestoreException("Failed to get specimen count", e)
        }
    }

    override suspend fun getStorageUsage(): Long = withContext(Dispatchers.IO) {
        try {
            val specimens = getAllSpecimens()
            var totalBytes = 0L

            specimens.forEach { specimen ->
                // Estimate storage based on image count and average size
                // This is a simple estimation - in a real app you'd track actual file sizes
                totalBytes += specimen.imageUrls.size * (2 * 1024 * 1024) // 2MB per image estimate
            }

            totalBytes
        } catch (e: Exception) {
            0L // Return 0 if we can't calculate usage
        }
    }
    
    override suspend fun save(tag: Tag): Unit = withContext(Dispatchers.IO) {
        try {
            val userId = getUserId()
            val normalizedTag = tag.normalized().copy(userId = userId)
            val documentId = "${userId}_${normalizedTag.name}"
            
            firestore.collection("tags")
                .document(documentId)
                .set(normalizedTag)
                .await()
        } catch (e: Exception) {
            throw DataException.FirestoreException("Failed to save tag", e)
        }
    }
    
    override suspend fun updateProfile(profile: UserProfile, imageUrl: StoredImage?): Unit = withContext(Dispatchers.IO) {
        try {
            var updatedProfile = profile
            
            // Handle profile image upload
            if (imageUrl != null && imageUrl.isLocal) {
                val imageData = loadImageData(imageUrl.url)
                imageData?.let { data ->
                    val remoteImage = imageStoring.uploadImage(data, "profile")
                    updatedProfile = profile.copy(picture = remoteImage)
                }
            }
            
            firestore.collection("users")
                .document(profile.userId)
                .set(updatedProfile.toFirestoreMap())
                .await()
        } catch (e: Exception) {
            throw DataException.FirestoreException("Failed to update profile", e)
        }
    }
    
    override suspend fun clearAllData(): Unit = withContext(Dispatchers.IO) {
        removeListeners()
        clearLocalData()
    }
    
    private fun loadImageData(fileUrl: String): ByteArray? {
        return try {
            val uri = Uri.parse(fileUrl)
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error loading image data", e)
            null
        }
    }
    
    private fun removeListeners() {
        specimenListener?.remove()
        tagListener?.remove()
        profileListener?.remove()
        
        specimenListener = null
        tagListener = null
        profileListener = null
    }
    
    private fun clearLocalData() {
        _specimens.value = emptyList()
        _tags.value = emptyList()
        _profile.value = null
    }
    
    private fun parseUserProfileFromFirestore(data: Map<String, Any>): UserProfile {
        return UserProfile(
            userId = data["userId"] as? String ?: "",
            email = data["email"] as? String ?: "",
            fullName = data["fullName"] as? String,
            username = data["username"] as? String,
            location = data["location"] as? String,
            bio = data["bio"] as? String,
            isPublic = data["isPublic"] as? Boolean ?: false,
            picture = (data["picture"] as? Map<String, Any>)?.let { pictureMap ->
                StoredImage(
                    path = pictureMap["path"] as? String ?: "",
                    url = pictureMap["url"] as? String ?: ""
                )
            },
            settings = (data["settings"] as? Map<String, Any>)?.let { settingsMap ->
                com.dmdev.fossilvaultanda.data.models.AppSettings(
                    unit = parseSizeUnit(settingsMap["unit"] as? String),
                    divideCarboniferous = settingsMap["divideCarboniferous"] as? Boolean ?: false,
                    defaultCurrency = parseCurrency(settingsMap["defaultCurrency"] as? String)
                )
            } ?: com.dmdev.fossilvaultanda.data.models.AppSettings()
        )
    }
    
    private fun parseSizeUnit(value: String?): com.dmdev.fossilvaultanda.data.models.enums.SizeUnit {
        return com.dmdev.fossilvaultanda.data.models.enums.SizeUnit.fromSerializedName(value)
    }
    
    private fun parseCurrency(value: String?): com.dmdev.fossilvaultanda.data.models.enums.Currency {
        return com.dmdev.fossilvaultanda.data.models.enums.Currency.fromSerializedName(value)
    }
}