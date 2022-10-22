package com.example.contactlessdoorbell.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pir_timestamp_table")

data class Timestamp (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")

    var id : Int?,
    @ColumnInfo(name = "time")

    var time : String?,
    @ColumnInfo(name = "date")

    var date : String?
)