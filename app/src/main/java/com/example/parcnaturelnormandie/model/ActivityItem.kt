package com.example.parcnaturelnormandie.model

data class ActivityItem(
    val id: String,
    val nom: String,
    val description: String,
    val duree: String = "",
    val tarif: Int = 0,
    val type_id: Int = 0,
    val image_url: String = "",
)