package com.example.parcnaturelnormandie.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcnaturelnormandie.model.Activity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class DetailsViewModel : ViewModel() {

    private val _activity = MutableLiveData<Activity?>()
    val activity: LiveData<Activity?> = _activity

    fun loadData(activityId: Int) {
        viewModelScope.launch {
            val urlString = "http://172.17.219.200:8002/api/activities/$activityId"

            val result = try {
                withContext(Dispatchers.IO) {
                    val url = URL(urlString)
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("Accept", "application/json")
                    }

                    try {
                        val code = conn.responseCode
                        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                        stream.bufferedReader().use { it.readText() }
                    } finally {
                        conn.disconnect()
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailsViewModel", "Erreur lors de l'appel API", e)
                null
            }

            Log.d("DetailsViewModel", "Réponse API : $result")

            result?.let {
                try {
                    val parsedActivity = Gson().fromJson(it, Activity::class.java)
                    _activity.postValue(parsedActivity)
                } catch (e: Exception) {
                    Log.e("DetailsViewModel", "Erreur lors du parsing JSON", e)
                    _activity.postValue(null)
                }
            }
        }
    }
}
