package com.dmdev.fossilvaultanda.data.repository.impl

import com.dmdev.fossilvaultanda.data.models.DataException
import com.dmdev.fossilvaultanda.data.models.StoredImage
import com.dmdev.fossilvaultanda.data.repository.interfaces.AuthenticationManager
import com.dmdev.fossilvaultanda.data.repository.interfaces.ImageStoring
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val authManager: AuthenticationManager
) : ImageStoring {
    
    private suspend fun getUserId(): String {
        return authManager.getCurrentUserId() 
            ?: throw DataException.AuthenticationException("No user logged in")
    }
    
    private fun getImageExtension(data: ByteArray): String {
        val header = data.take(3).joinToString("") { 
            "%02x".format(it) 
        }
        
        return when (header) {
            "ffd8ff" -> "jpg"
            "89504e" -> "png"
            "474946" -> "gif"
            else -> "jpg"
        }
    }
    
    override suspend fun uploadImage(imageData: ByteArray, folder: String): StoredImage = 
        withContext(Dispatchers.IO) {
            try {
                val userId = getUserId()
                val extension = getImageExtension(imageData)
                val imageName = "${UUID.randomUUID()}.$extension"
                val path = "$folder/$userId/$imageName"
                
                val storageRef = storage.reference.child(path)
                storageRef.putBytes(imageData).await()
                val downloadUrl = storageRef.downloadUrl.await()
                
                StoredImage(url = downloadUrl.toString(), path = path)
            } catch (e: Exception) {
                throw DataException.StorageException("Failed to upload image", e)
            }
        }
    
    override suspend fun uploadImages(
        imagesData: List<ByteArray>, 
        folder: String
    ): List<StoredImage> = withContext(Dispatchers.IO) {
        try {
            imagesData.map { imageData ->
                async { uploadImage(imageData, folder) }
            }.awaitAll()
        } catch (e: Exception) {
            throw DataException.StorageException("Failed to upload images", e)
        }
    }
    
    override suspend fun deleteImage(image: StoredImage): Unit = withContext(Dispatchers.IO) {
        try {
            storage.reference.child(image.path).delete().await()
        } catch (e: Exception) {
            throw DataException.StorageException("Failed to delete image", e)
        }
    }
    
    override suspend fun deleteImages(images: List<StoredImage>): Unit = withContext(Dispatchers.IO) {
        try {
            images.map { image ->
                async { deleteImage(image) }
            }.awaitAll()
        } catch (e: Exception) {
            throw DataException.StorageException("Failed to delete images", e)
        }
    }
}