package com.sepidsa.fortytwocalculator.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "constant")
data class ConstantEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    val id: Long = 0,

    @ColumnInfo(name = ConstantContract.ConstantEntry.COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = ConstantContract.ConstantEntry.COLUMN_NUMBER)
    val number: Double,

    @ColumnInfo(name = ConstantContract.ConstantEntry.COLUMN_SELECTED)
    val selected: Int = 0,
)
