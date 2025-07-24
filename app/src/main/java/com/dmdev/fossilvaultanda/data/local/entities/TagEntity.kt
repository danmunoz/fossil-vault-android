package com.dmdev.fossilvaultanda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val name: String,
    val userId: String,
    val lastModified: Long = System.currentTimeMillis()
)