package com.dmdev.fossilvaultanda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dmdev.fossilvaultanda.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserProfile(userId: String): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfileSync(userId: String): UserProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)
    
    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)
    
    @Delete
    suspend fun deleteUserProfile(profile: UserProfileEntity)
    
    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteUserProfileById(userId: String)
    
    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllUserProfiles()
}