package com.example.contactlessdoorbell.db

import com.example.contactlessdoorbell.db.Timestamp
import com.example.contactlessdoorbell.db.TimestampDAO

class TimestampRepository(private val dao: TimestampDAO) {
    //as we are returning a flow we dont need to create a suspend function
    val Timestamp = dao.getAllTimestamp()

    suspend fun insertTimestamp(timestamp: Timestamp):Long {
        return dao.insert(timestamp)
    }


    suspend fun deleteAllTimestamp(): Int {
        return dao.deleteAll()
    }
}