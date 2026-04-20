package com.sepidsa.fortytwocalculator.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM log ORDER BY _id DESC")
    fun getAllLogs(): Flow<List<LogEntity>>

    @Query("SELECT * FROM log WHERE _id = :id")
    suspend fun getLogById(id: Long): LogEntity?

    @Insert
    suspend fun insert(log: LogEntity): Long

    @Update
    suspend fun update(log: LogEntity): Int

    @Delete
    suspend fun delete(log: LogEntity): Int

    @Query("DELETE FROM log WHERE _id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM log")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM log WHERE starred != 1")
    suspend fun deleteNonStarred(): Int

    @Query("UPDATE log SET starred = :starred WHERE _id = :id")
    suspend fun updateStarred(id: Long, starred: Int): Int

    @Query("UPDATE log SET tag = :tag WHERE _id = :id")
    suspend fun updateTag(id: Long, tag: String): Int
}
