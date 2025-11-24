package com.example.parcnaturelnormandie.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedActivityName = MutableLiveData<String>()
    val selectedActivityName: LiveData<String> = _selectedActivityName

    fun selectActivity(name: String) {
        _selectedActivityName.value = name
    }
}