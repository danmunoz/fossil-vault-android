package com.dmdev.fossilvaultanda.data.models

import kotlinx.serialization.Serializable

@Serializable
data class StoredImage(
    val url: String,
    val path: String
) {
    constructor(url: String) : this(url, url)
    
    val isLocal: Boolean get() = url.startsWith("file://") || url.startsWith("content://")
}