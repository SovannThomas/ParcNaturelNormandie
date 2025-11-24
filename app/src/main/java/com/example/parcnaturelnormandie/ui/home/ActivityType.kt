package com.example.parcnaturelnormandie.ui.home
import com.google.gson.annotations.SerializedName
data class ActivityType(
    val id : Int,
    val libelle : String,
    @SerializedName("image_url") val imageUrl : String)
