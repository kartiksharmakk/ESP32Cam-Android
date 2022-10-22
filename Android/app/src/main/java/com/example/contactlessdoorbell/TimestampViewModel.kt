package com.example.contactlessdoorbell

import androidx.lifecycle.*
import com.example.contactlessdoorbell.db.Timestamp
import com.example.contactlessdoorbell.db.TimestampRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimestampViewModel(private val repository: TimestampRepository) : ViewModel() {
    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage




    fun insertSubscriber(timestamp: Timestamp) = viewModelScope.launch {
        repository.insertTimestamp(timestamp)
    }
    fun getSavedSubscribers() = liveData {
        repository.Timestamp.collect {
            emit(it)
        }
    }

    fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAllTimestamp()
        if (noOfRowsDeleted > 0) {
            statusMessage.value = Event("$noOfRowsDeleted Subscribers Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }
}