package com.dmdev.fossilvaultanda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dmdev.fossilvaultanda.data.local.entities.SpecimenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecimenDao {
    @Query("SELECT * FROM specimens WHERE userId = :userId ORDER BY creationDate DESC")
    fun getAllSpecimens(userId: String): Flow<List<SpecimenEntity>>
    
    @Query("SELECT * FROM specimens WHERE id = :id")
    suspend fun getSpecimenById(id: String): SpecimenEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecimen(specimen: SpecimenEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecimens(specimens: List<SpecimenEntity>)
    
    @Update
    suspend fun updateSpecimen(specimen: SpecimenEntity)
    
    @Delete
    suspend fun deleteSpecimen(specimen: SpecimenEntity)
    
    @Query("DELETE FROM specimens WHERE id = :id")
    suspend fun deleteSpecimenById(id: String)
    
    @Query("DELETE FROM specimens WHERE userId = :userId")
    suspend fun deleteAllSpecimensForUser(userId: String)
    
    @Query("SELECT COUNT(*) FROM specimens WHERE userId = :userId")
    suspend fun getSpecimenCount(userId: String): Int
}