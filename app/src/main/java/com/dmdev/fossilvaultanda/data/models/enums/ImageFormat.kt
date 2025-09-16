package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ImageFormat(val value: String) {
    JPG("jpg"),
    PNG("png"),
    HEIC("heic");

    val mimeType: String
        get() = when (this) {
            JPG -> "image/jpeg"
            PNG -> "image/png"
            HEIC -> "image/heif"
        }

    val fileExtension: String
        get() = value

    val isLossy: Boolean
        get() = when (this) {
            JPG, HEIC -> true
            PNG -> false
        }

    companion object {
        fun fromExtension(extension: String): ImageFormat? {
            return when (extension.lowercase()) {
                "jpg", "jpeg" -> JPG
                "png" -> PNG
                "heic", "heif" -> HEIC
                else -> null
            }
        }

        fun fromMimeType(mimeType: String): ImageFormat? {
            return when (mimeType.lowercase()) {
                "image/jpeg" -> JPG
                "image/png" -> PNG
                "image/heif", "image/heic" -> HEIC
                else -> null
            }
        }

        fun fromFileHeader(data: ByteArray): ImageFormat? {
            if (data.size < 3) return null

            val header = data.take(3).joinToString("") {
                "%02x".format(it)
            }

            return when (header) {
                "ffd8ff" -> JPG
                "89504e" -> PNG
                else -> {
                    if (data.size >= 12 &&
                        data.sliceArray(4..11).contentEquals("ftypheic".toByteArray()) ||
                        data.sliceArray(4..11).contentEquals("ftypmif1".toByteArray())) {
                        HEIC
                    } else null
                }
            }
        }
    }
}