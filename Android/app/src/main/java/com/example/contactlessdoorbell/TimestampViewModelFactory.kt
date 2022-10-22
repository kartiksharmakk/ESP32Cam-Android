package com.example.contactlessdoorbell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactlessdoorbell.db.TimestampRepository

class TimestampViewModelFactory(
    private val repository: TimestampRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TimestampViewModel::class.java)){
            return TimestampViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}