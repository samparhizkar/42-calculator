package com.sepidsa.fortytwocalculator.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log")
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    val id: Long = 0,

    @ColumnInfo(name = LogContract.LogEntry.COLUMN_RESULT)
    val result: String,

    @ColumnInfo(name = LogContract.LogEntry.COLUMN_RESULT_NO_COMMA)
    val resultNoComma: String,

    @ColumnInfo(name = LogContract.LogEntry.COLUMN_OPERATION)
    val operation: String,

    @ColumnInfo(name = LogContract.LogEntry.COLUMN_TAG)
    val tag: String = "",

    @ColumnInfo(name = LogContract.LogEntry.COLUMN_STARRED)
    val starred: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "words")
    val words: String = "",
)
