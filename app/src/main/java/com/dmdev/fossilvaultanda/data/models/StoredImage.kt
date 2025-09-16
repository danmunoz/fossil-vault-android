package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.ImageFormat
import kotlinx.serialization.Serializable

@Serializable
data class StoredImage(
    val url: String,
    val path: String,
    val size: Int? = null,
    val format: ImageFormat? = null
) {
    constructor(url: String) : this(url, url, null, null)

    constructor(url: String, path: String) : this(url, path, null, null)

    val isLocal: Boolean get() = url.startsWith("file://") || url.startsWith("content://")
}