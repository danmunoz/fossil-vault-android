package com.dmdev.fossilvaultanda.data.repository.impl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmdev.fossilvaultanda.data.models.DataException
import com.dmdev.fossilvaultanda.data.models.StoredImage
import com.dmdev.fossilvaultanda.data.models.enums.ImageFormat
import com.dmdev.fossilvaultanda.data.repository.interfaces.AuthenticationManager
import com.dmdev.fossilvaultanda.data.repository.interfaces.ImageStoring
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
    
    private fun convertToJpg(imageData: ByteArray): ByteArray {
        return try {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                ?: throw DataException.StorageException("Failed to decode image data", null)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)

            bitmap.recycle()
            outputStream.toByteArray()
        } catch (e: Exception) {
            throw DataException.StorageException("Failed to convert image to JPG", e)
        }
    }

    private fun getOriginalImageFormat(data: ByteArray): ImageFormat {
        return ImageFormat.fromFileHeader(data) ?: ImageFormat.JPG
    }
    
    override suspend fun uploadImage(imageData: ByteArray, folder: String): StoredImage =
        withContext(Dispatchers.IO) {
            try {
                val userId = getUserId()

                val originalFormat = getOriginalImageFormat(imageData)
                val jpgData = convertToJpg(imageData)
                val imageName = "${UUID.randomUUID()}.jpg"
                val path = "$folder/$userId/$imageName"

                val storageRef = storage.reference.child(path)
                storageRef.putBytes(jpgData).await()
                val downloadUrl = storageRef.downloadUrl.await()

                StoredImage(
                    url = downloadUrl.toString(),
                    path = path,
                    size = jpgData.size,
                    format = ImageFormat.JPG
                )
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