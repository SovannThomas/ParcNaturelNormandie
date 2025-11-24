package com.example.parcnaturelnormandie.ui.cart

import java.sql.Date
import java.time.LocalDateTime

data class CartItem(
    val imageResId: Int, // ressource drawable
    val title: String,
    val duration: String,
    val price: String,
    val date: LocalDateTime
)

