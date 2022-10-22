package com.example.contactlessdoorbell.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Timestamp::class], version = 1)
abstract class TimestampDatabase : RoomDatabase() {
    abstract val TimestampDAO: TimestampDAO

    companion object {
        @Volatile
        private var INSTANCE: TimestampDatabase? = null
        fun getInstance(context: Context): TimestampDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TimestampDatabase::class.java,
                        "pir_timestamp_table"
                    ).build()
                }
                return instance
            }
        }
    }
}