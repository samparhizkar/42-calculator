package com.sepidsa.fortytwocalculator.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstantDao {
    @Query("SELECT * FROM constant")
    fun getAllConstants(): Flow<List<ConstantEntity>>

    @Query("SELECT * FROM constant WHERE _id = :id")
    suspend fun getConstantById(id: Long): ConstantEntity?

    @Insert
    suspend fun insert(constant: ConstantEntity): Long

    @Update
    suspend fun update(constant: ConstantEntity): Int

    @Delete
    suspend fun delete(constant: ConstantEntity): Int

    @Query("DELETE FROM constant WHERE _id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM constant")
    suspend fun deleteAll(): Int

    @Query("UPDATE constant SET selected = :selected WHERE _id = :id")
    suspend fun updateSelected(id: Long, selected: Int): Int
}
