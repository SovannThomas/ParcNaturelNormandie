package com.example.parcnaturelnormandie.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    private val _activities = MutableLiveData<List<ActivityType>>()
    val activities: LiveData<List<ActivityType>> = _activities



    fun loadData() {
        viewModelScope.launch {
            try {
                val activityTypes = withContext(Dispatchers.IO) {
                    val url = URL("http://172.17.23.200:8001/api/activity-types/")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"

                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    connection.disconnect()

                    val gson = Gson()
                    val type = object : TypeToken<List<ActivityType>>() {}.type
                    gson.fromJson<List<ActivityType>>(response, type)
                }

                // Tri alphabétique sur le libelle
                val sortedActivities = activityTypes.sortedBy { it.libelle.lowercase() }

                // Mettre à jour le LiveData pour l'UI
                _activities.value = sortedActivities

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





}