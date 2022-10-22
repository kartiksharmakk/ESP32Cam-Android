package com.example.contactlessdoorbell.db

import androidx.room.*
import com.example.contactlessdoorbell.db.Timestamp
import kotlinx.coroutines.flow.Flow

@Dao
interface TimestampDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insert(timestamp: Timestamp):Long

    @Query("DELETE FROM pir_timestamp_table")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM pir_timestamp_table ORDER BY date DESC,time DESC")
    fun getAllTimestamp(): Flow<List<Timestamp>>

}
