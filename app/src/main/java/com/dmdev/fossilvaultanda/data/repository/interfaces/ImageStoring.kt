package com.dmdev.fossilvaultanda.data.repository.interfaces

import com.dmdev.fossilvaultanda.data.models.StoredImage

interface ImageStoring {
    suspend fun uploadImage(imageData: ByteArray, folder: String): StoredImage
    suspend fun uploadImages(imagesData: List<ByteArray>, folder: String): List<StoredImage>
    suspend fun deleteImage(image: StoredImage)
    suspend fun deleteImages(images: List<StoredImage>)
}