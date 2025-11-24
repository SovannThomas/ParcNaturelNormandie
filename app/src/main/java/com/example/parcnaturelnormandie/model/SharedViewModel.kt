package com.example.parcnaturelnormandie.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _selectedActivityId = MutableLiveData<String?>()
    val selectedActivityId: LiveData<String?> = _selectedActivityId

    private val _selectedActivityLabel = MutableLiveData<String?>()
    val selectedActivityLabel: LiveData<String?> = _selectedActivityLabel

    fun selectActivity(id: String, label: String) {
        _selectedActivityId.value = id
        _selectedActivityLabel.value = label
    }
}
