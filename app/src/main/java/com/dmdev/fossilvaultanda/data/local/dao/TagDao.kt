package com.dmdev.fossilvaultanda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dmdev.fossilvaultanda.data.local.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags WHERE userId = :userId ORDER BY name ASC")
    fun getAllTags(userId: String): Flow<List<TagEntity>>
    
    @Query("SELECT * FROM tags WHERE name = :name AND userId = :userId")
    suspend fun getTagByName(name: String, userId: String): TagEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)
    
    @Delete
    suspend fun deleteTag(tag: TagEntity)
    
    @Query("DELETE FROM tags WHERE name = :name AND userId = :userId")
    suspend fun deleteTagByName(name: String, userId: String)
    
    @Query("DELETE FROM tags WHERE userId = :userId")
    suspend fun deleteAllTagsForUser(userId: String)
}