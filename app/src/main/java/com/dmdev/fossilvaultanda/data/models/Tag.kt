package com.dmdev.fossilvaultanda.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val userId: String? = null,
    val name: String = ""
) {
    val id: String get() = name
    
    fun normalized(): Tag = copy(name = name.lowercase())
    
    fun isValid(): Boolean = name.isNotBlank()
}